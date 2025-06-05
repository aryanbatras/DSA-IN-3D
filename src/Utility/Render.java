package Utility;

import Shapes.Box;
import Shapes.Shape;
import Utility.Material;
import Utility.Point;
import Utility.Ray;
import Utility.Color;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class Render {

    public static FFmpegStream PREVIEW_FFMPEG = null;

    public static void initFFmpegPreview(int width, int height, int fps, String outputFile) {
        try {
            PREVIEW_FFMPEG = new FFmpegStream(width, height, fps, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            PREVIEW_FFMPEG = null;
        }
    }

    public static void closeFFmpegPreview() {
        if (PREVIEW_FFMPEG != null) {
            try {
                PREVIEW_FFMPEG.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            PREVIEW_FFMPEG = null;
        }
    }

    int FRAMES;
    BufferedImage BEINGRENDERED, ENVIRONMENT;

    public Render(String environmentImagePath) {
        int w = (int) (Screen.getWidth() );
        int h = (int) (Screen.getHeight() );
        this.BEINGRENDERED = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.ENVIRONMENT = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        setENVIRONMENT(environmentImagePath);
    }

    public void setENVIRONMENT(String filePath) {
        try {
            ENVIRONMENT = ImageIO.read(getClass( ).getResourceAsStream(filePath));
        } catch (IOException e) {
            System.out.println("Environment failed to load");
        }
    }

    public void drawImage(Camera camera, ArrayList<Shape> world) {
        long start = System.nanoTime() / 1_000_000;

        int width = BEINGRENDERED.getWidth();
        int height = BEINGRENDERED.getHeight();
        int[] pixels = ((DataBufferInt) BEINGRENDERED.getRaster().getDataBuffer()).getData();

        camera = camera.setCameraPerspective(width, height);
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ArrayList<Future<?>> tasks = new ArrayList<>();
        int slice = height / Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            final int y0 = i * slice;
            final int y1 = (i == Runtime.getRuntime().availableProcessors() - 1) ? height : y0 + slice;
            Camera cam = camera;
            tasks.add(exec.submit(() -> {
                for (int y = y0; y < y1; y++) {
                    for (int x = 0; x < width; x++) {
                        double u = (double) x / ( width - 1 );
                        double v = (double) ( height - y ) / ( height - 1 );
                        Ray ray = cam.getRay(u, v);
                        Color pixelColor = rayColor(world, ray, ENVIRONMENT);
                        pixels[y * width + x] = pixelColor.colorToInteger();
                    }
                }
            }));
        }

        for (Future<?> f : tasks) {
            try { f.get(); } catch (Exception e) { throw new RuntimeException(e); }
        }
        exec.shutdown();

        BufferedImage fullRes = new BufferedImage(
                (int) Screen.getWidth(),
                (int) Screen.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = fullRes.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(BEINGRENDERED,
                0, 0, fullRes.getWidth(), fullRes.getHeight(), // destination rectangle
                0, 0, BEINGRENDERED.getWidth(), BEINGRENDERED.getHeight(), // source rectangle
                null);

        g.dispose();

        if (PREVIEW_FFMPEG != null) {
            try {
                PREVIEW_FFMPEG.writeFrame(fullRes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Rendered in " + (System.nanoTime() / 1_000_000 - start) + " ms | Frame " + FRAMES++);
    }

    private int rayColorFast(ArrayList<Shape> world, Ray r, BufferedImage environmentMap) {
        double nearest = Double.MAX_VALUE;
        Point hitPoint = null, normal = null;
        int hitColor = 0;

        for (Shape shape : world) {
            double t = shape.hit(r);
            if (t > 0.001 && t < nearest && shape instanceof Box box && box.material == Material.CHROME) {
                nearest = t;
                hitPoint = r.at(t);
                Point center = box.getMin().add(box.getMax()).mul(0.5);
                Point localHit = hitPoint.sub(center);
                double dx = Math.abs(localHit.x) - Math.abs(box.getMax().x - box.getMin().x) / 2.0;
                double dy = Math.abs(localHit.y) - Math.abs(box.getMax().y - box.getMin().y) / 2.0;
                double dz = Math.abs(localHit.z) - Math.abs(box.getMax().z - box.getMin().z) / 2.0;

                normal = (dx > dy && dx > dz) ? new Point(Math.signum(localHit.x), 0, 0)
                        : (dy > dz) ? new Point(0, Math.signum(localHit.y), 0)
                        : new Point(0, 0, Math.signum(localHit.z));

                hitColor = box.color.colorToInteger();
            }
        }


        return hitColor != 0 ? hitColor : sampleEnvironment(r, environmentMap).colorToInteger();
    }

    public static Color rayColor(ArrayList<Shape> world, Ray r, BufferedImage environmentMap) {
        return rayColor(world, r, environmentMap, 3);
    }
    
    private static Color rayColor(ArrayList<Shape> world, Ray r, BufferedImage environmentMap, int depth) {
        if (depth <= 0) return new Color(0, 0, 0);

        double nearest = Double.MAX_VALUE;
        Point hitPoint = null, normal = null;
        Color hitColor = null;
        double fuzz = 0;
        Material material = null;
        Shape hitShape = null;

        // First pass: find the closest hit
        for (Shape shape : world) {
            double t = shape.hit(r);
            if (shape instanceof Box box) {
                if (t > 0.001 && t < nearest) {
                    nearest = t;
                    hitPoint = r.at(t);
                    normal = box.getNormal(hitPoint);
                    hitColor = box.color;
                    fuzz = box.fuzz;
                    material = box.material;
                    hitShape = shape;

                    if (normal.z != 0 && box.getDigits( ) != null) {
                        Point boxMin = box.getMin( ), boxMax = box.getMax( ), boxSize = boxMax.sub(boxMin);
                        double u = 1.0 - (hitPoint.x - boxMin.x) / boxSize.x;
                        // Flip v-coordinate to correct the digit orientation
                        double v = (hitPoint.y - boxMin.y) / boxSize.y;
                        Integer[] digits = box.getDigits( );
                        double digitWidth = 1.0 / digits.length;

                        for (int i = 0; i < digits.length; i++) {
                            double startU = i * digitWidth, endU = (i + 1) * digitWidth;
                            if (u >= startU && u < endU && isInDigit(digits[i], (u - startU) / digitWidth, v, digits.length)) {
                                Point reflected = reflect(r.getDirection( ).normalize( ), normal)
                                        .add(randomInUnitSphere( ).mul(fuzz * 0.1));
                                Color bounce = rayColor(world, new Ray(hitPoint, reflected), environmentMap, depth - 1);
                                return new Color(
                                        Math.min(1f, hitColor.r * bounce.r * 0.7f + 0.2f),
                                        Math.min(1f, hitColor.g * bounce.g * 0.8f + 0.3f),
                                        Math.min(1f, hitColor.b * bounce.b * 1.2f + 0.4f)
                                );
                            }
                        }
                    }
                }
            }
        }


        if (hitPoint != null && hitColor != null) {
            // Handle chrome material with reflections
            if (material == Material.CHROME) {
                Point reflectedDir = reflect(r.getDirection().normalize(), normal);
                if (fuzz > 0) {
                    reflectedDir = reflectedDir.add(randomInUnitSphere().mul(fuzz));
                }
                Ray reflectedRay = new Ray(hitPoint, reflectedDir);
                Color reflectedColor = rayColor(world, reflectedRay, environmentMap, depth - 1);

                // Blend base color with reflected color (80% reflection, 20% base color)
                return new Color(
                    (float) (hitColor.r * 0.2 + reflectedColor.r * 0.8),
                    (float) (hitColor.g * 0.2 + reflectedColor.g * 0.8),
                    (float) (hitColor.b * 0.2 + reflectedColor.b * 0.8)
                );
            }
            return hitColor;
        }
        return sampleEnvironment(r, environmentMap);
    }

    private static Color sampleEnvironment(Ray r, BufferedImage env) {
        Point dir = r.getDirection().normalize();
        double u = 0.5 + Math.atan2(dir.z, dir.x) / (2 * Math.PI);
        double v = 0.5 - Math.asin(dir.y) / Math.PI;
        int x = Math.min(Math.max((int)(u * env.getWidth()), 0), env.getWidth() - 1);
        int y = Math.min(Math.max((int)(v * env.getHeight()), 0), env.getHeight() - 1);
        int rgb = env.getRGB(x, y);
        return new Color(
                ((rgb >> 16) & 0xFF) / 255.0f,
                ((rgb >> 8) & 0xFF) / 255.0f,
                (rgb & 0xFF) / 255.0f
        );
    }

    public static boolean isInDigit(int digit, double u, double v, int l) {
        u = (u - 0.3) / 0.2;
        v = (v - 0.52) / (l <= 2 ? 0.2 : l <= 4 ? 0.1 : 0.05);
        if (u < -0.2 || u > 1.2 || v < -0.2 || v > 1.2 || u < 0 || u > 1 || v < 0 || v > 1) return false;

        double r = 0.152;
        float segW = 0.6f, segH = 0.05f;

        boolean top = roundRect(u, v, 0.5, 0.92, segW, segH, r);
        boolean mid = roundRect(u, v, 0.5, 0.5, segW, segH, r);
        boolean bot = roundRect(u, v, 0.5, 0.08, segW, segH, r);
        boolean lt = roundRect(u, v, 0.18, 0.75, segH, 0.3, r);
        boolean lb = roundRect(u, v, 0.18, 0.25, segH, 0.3, r);
        boolean rt = roundRect(u, v, 0.82, 0.75, segH, 0.3, r);
        boolean rb = roundRect(u, v, 0.82, 0.25, segH, 0.3, r);

        return switch (digit) {
            case 0 -> top || bot || lt || lb || rt || rb;
            case 1 -> rt || rb;
            case 2 -> top || mid || bot || rt || lb;
            case 3 -> top || mid || bot || rt || rb;
            case 4 -> mid || rt || rb || lt;
            case 5 -> top || mid || bot || lt || rb;
            case 6 -> mid || bot || lt || lb || rb;
            case 7 -> top || rt || rb;
            case 8 -> top || mid || bot || lt || lb || rt || rb;
            case 9 -> top || mid || lt || rt || rb;
            default -> false;
        };
    }

    public static boolean roundRect(double u, double v, double cx, double cy, double w, double h, double r) {
        double dx = Math.max(Math.abs(u - cx) - w / 2.0, 0);
        double dy = Math.max(Math.abs(v - cy) - h / 2.0, 0);
        return (dx * dx + dy * dy) < r * r;
    }

    private static final Random RAND = new Random();

    public static Point randomInUnitSphere() {
        while (true) {
            Point p = new Point(RAND.nextDouble() * 2 - 1, RAND.nextDouble() * 2 - 1, RAND.nextDouble() * 2 - 1);
            if (p.dot(p) < 1) return p;
        }
    }

    public static Point reflect(Point v, Point n) {
        return v.sub(n.mul(2 * v.dot(n)));
    }
}
