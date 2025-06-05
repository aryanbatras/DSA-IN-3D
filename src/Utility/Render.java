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

    // Configuration
    private static final int MAX_RECURSION_DEPTH = 3;
    private static final int TILE_SIZE = 32; // For better cache locality
    private static final int MAX_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    // Thread-safe singleton for FFmpeg
    private static volatile FFmpegStream PREVIEW_FFMPEG = null;

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
        long startTime = System.nanoTime();
        final int width = BEINGRENDERED.getWidth();
        final int height = BEINGRENDERED.getHeight();
        final int[] pixels = ((DataBufferInt) BEINGRENDERED.getRaster().getDataBuffer()).getData();
        // Convert to array once for better performance
        final Shape[] worldArray = world.toArray(new Shape[0]);

        // Update camera perspective once
        final Camera finalCamera = camera.setCameraPerspective(width, height);

        // Use a fixed thread pool with optimal thread count
        final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        final CountDownLatch latch = new CountDownLatch((width / TILE_SIZE + 1) * (height / TILE_SIZE + 1));

        // Process image in tiles for better cache locality
        for (int tileY = 0; tileY < height; tileY += TILE_SIZE) {
            for (int tileX = 0; tileX < width; tileX += TILE_SIZE) {
                final int tx = tileX;
                final int ty = tileY;
                final int tileW = Math.min(TILE_SIZE, width - tx);
                final int tileH = Math.min(TILE_SIZE, height - ty);

                executor.submit(() -> {
                    try {
                        // Reusable objects for this thread
                        Ray ray = new Ray();

                        for (int y = ty; y < ty + tileH; y++) {
                            final double v = (double)(height - y) / (height - 1);
                            final int rowOffset = y * width;

                            for (int x = tx; x < tx + tileW; x++) {
                                final double u = (double)x / (width - 1);

                                // Get ray and calculate color
                                ray = finalCamera.getRay(u, v);
                                Color pixelColor = rayColor(world, ray, ENVIRONMENT);
                                pixels[rowOffset + x] = pixelColor.colorToInteger();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        try {
            // Wait for all tiles to complete with timeout
            if (!latch.await(10, TimeUnit.SECONDS)) {
                System.err.println("Warning: Rendering took too long, some tiles may be incomplete");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Rendering interrupted", e);
        } finally {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long renderTime = System.nanoTime() - startTime;

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

        // Post-processing and display
//        postProcessAndDisplay();

        // Log performance metrics
        System.out.printf("Rendered frame %d in %.2f ms%n",
            FRAMES++,
            renderTime / 1_000_000.0);
    }

    /**
     * Optimized version of rayColor that avoids allocations and uses array instead of ArrayList
     */
    /**
     * Optimized version that uses array instead of ArrayList for shapes
     */
    private static Color rayColor(Shape[] world, Ray r, BufferedImage environmentMap) {
        return rayColor(world, r, environmentMap, MAX_RECURSION_DEPTH);
    }

    private static Color rayColor(Shape[] world, Ray r, BufferedImage environmentMap, int depth) {
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
            if (t > 0.001 && t < nearest) {
                if (shape instanceof Box box) {
                    nearest = t;
                    hitPoint = r.at(t);
                    normal = box.getNormal(hitPoint);
                    hitColor = box.color;
                    fuzz = box.fuzz;
                    material = box.material;
                    hitShape = shape;
                }
            }
        }


        if (hitPoint != null) {
            if (hitShape instanceof Box box && box.getDigits() != null) {
                // Handle digit rendering logic here
                Point boxMin = box.getMin();
                Point boxMax = box.getMax();
                Point boxSize = boxMax.sub(boxMin);
                double u = 1.0 - (hitPoint.x - boxMin.x) / boxSize.x;
                double v = (hitPoint.y - boxMin.y) / boxSize.y;
                Integer[] digits = box.getDigits();
                double digitWidth = 1.0 / digits.length;

                for (int i = 0; i < digits.length; i++) {
                    double startU = i * digitWidth, endU = (i + 1) * digitWidth;
                    if (u >= startU && u < endU && isInDigit(digits[i], (u - startU) / digitWidth, v, digits.length)) {
                        Point reflected = reflect(r.getDirection().normalize(), normal)
                                .add(randomInUnitSphere().mul(fuzz * 0.1));
                        Color bounce = rayColor(world, new Ray(hitPoint, reflected), environmentMap, depth - 1);
                        return new Color(
                                Math.min(1f, hitColor.r * bounce.r * 0.7f + 0.2f),
                                Math.min(1f, hitColor.g * bounce.g * 0.8f + 0.3f),
                                Math.min(1f, hitColor.b * bounce.b * 1.2f + 0.4f)
                        );
                    }
                }
            }

            // Default material handling
            if (material == Material.CHROME) {
                Point reflected = reflect(r.getDirection().normalize(), normal);
                Ray scattered = new Ray(hitPoint, reflected.add(randomInUnitSphere().mul(fuzz)));
                Color reflectedColor = rayColor(world, scattered, environmentMap, depth - 1);
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

    public static Color rayColor(ArrayList<Shape> world, Ray r, BufferedImage environmentMap) {
        return rayColor(world, r, environmentMap, MAX_RECURSION_DEPTH);
    }

    /**
     * Trace a ray through the scene and calculate its color
     * @param world Array of shapes in the scene
     * @param r Ray to trace
     * @param environmentMap Environment map for background
     * @param depth Current recursion depth
     * @return Color of the ray
     */
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
