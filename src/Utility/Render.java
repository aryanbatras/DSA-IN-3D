package Utility;

import Shapes.Box;
import Shapes.Shape;
import Shapes.Sphere;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Render {

    int FRAMES;
    BufferedImage RAYTRACER, BEINGRENDERED, ENVIRONMENT;

    public Render(){
        this.BEINGRENDERED = new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.ENVIRONMENT = new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.RAYTRACER = new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB);
        setENVIRONMENT("/Resources/lake.jpg");
    }

    public Render(String environmentImagePath){
        this.BEINGRENDERED = new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.ENVIRONMENT = new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.RAYTRACER = new BufferedImage((int) Screen.getWidth(), (int) Screen.getHeight(), BufferedImage.TYPE_INT_RGB);
        setENVIRONMENT(environmentImagePath);
    }



    public Render(BufferedImage RAYTRACER, BufferedImage BEINGRENDERED, BufferedImage ENVIRONMENT) {
        this.BEINGRENDERED = BEINGRENDERED;
        this.ENVIRONMENT = ENVIRONMENT;
        this.RAYTRACER = RAYTRACER;
        this.FRAMES = 0;
    }

    public BufferedImage getRAYTRACER() {
        return RAYTRACER;
    }

    public void setRAYTRACER(BufferedImage RAYTRACER) {
        this.RAYTRACER = RAYTRACER;
    }

    public BufferedImage getBEINGRENDERED() {
        return BEINGRENDERED;
    }

    public void setBEINGRENDERED(BufferedImage BEINGRENDERED) {
        this.BEINGRENDERED = BEINGRENDERED;
    }

    public BufferedImage getENVIRONMENT() {
        return ENVIRONMENT;
    }

    public void setENVIRONMENT(BufferedImage ENVIRONMENT) {
        this.ENVIRONMENT = ENVIRONMENT;
    }

    public void setENVIRONMENT(String filePath) {
        try {
            ENVIRONMENT = ImageIO.read(
                    getClass().getResourceAsStream(filePath)
            );
        } catch(IOException e){
            System.out.println("Environment failed to load");
        }
    }

    public void drawImage(Camera CAMERA, ArrayList<Shape> WORLD) {

        long start = System.nanoTime() / 1000000;

        CAMERA = CAMERA.setCameraPerspective((int) Screen.getWidth(), (int) Screen.getHeight());

        int cores = Runtime.getRuntime().availableProcessors();

        ExecutorService exec = Executors.newFixedThreadPool(cores);

        int[] pixels = ((DataBufferInt) BEINGRENDERED.getRaster().getDataBuffer()).getData();

        ArrayList<Future<?>> tasks = new ArrayList<>();

        for (int yStart = 0; yStart < Screen.getHeight(); yStart += Screen.getHeight() / cores) {

            final int y0 = yStart;

            final int y1 = (int) Math.min(yStart + (Screen.getHeight() / cores), Screen.getHeight());

            Camera finalCAMERA = CAMERA;
            tasks.add(exec.submit(() -> {
                for (int y = y0; y < y1; y++) {
                    for (int x = 0; x < Screen.getWidth(); x++) {
                        double u = (double) x / Screen.getWidth();
                        double v = (double) (Screen.getHeight() - y) / Screen.getHeight();
                        Ray ray = finalCAMERA.getRay(u, v);
                        Color pixelColor = new Color(rayColor(WORLD, ray, ENVIRONMENT, 1));
                        pixels[(int) (y * Screen.getWidth() + x)] = pixelColor.colorToInteger();
                    }
                }
            }));
        }
        for (Future<?> f : tasks) {
            try { f.get(); }
            catch (InterruptedException | ExecutionException e) { throw new RuntimeException(e); }
        }
        exec.shutdown();
        RAYTRACER = BEINGRENDERED;

        try {
            File dir = new File("src/Resources/frames");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = String.format("src/Resources/frames/frame_%06d.png", FRAMES++);
            File outputFile = new File(fileName);

            ImageIO.write(RAYTRACER, "png", outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Rendered " + ( ( System.nanoTime() / 1000000 ) - start ) + "ms"+ " " + FRAMES);
    }


    public static Color rayColor(ArrayList<Shape> WORLD, Ray r, BufferedImage environmentMap, int depth) {
        if(depth <= 0){ return new Color(0,0,0); }
        double nearest = Double.MAX_VALUE;
        Material hitMaterial = null;
        Point hitPoint = null;
        Point normal = null;
        Shape hitShape = null;
        Color hitColor = null;
        double fuzz = 0f;

        for (Shape currentShape : WORLD) {
            double t = currentShape.hit(r);
            if (t > 0.001 && t < nearest) {
                nearest = t;
                hitPoint = r.at(t);
                hitShape = currentShape;

                if(currentShape instanceof Sphere sphere){
                    normal = hitPoint.sub(sphere.center).normalize( );
                    hitColor = sphere.color;
                    hitMaterial = sphere.material;
                    fuzz = sphere.fuzz;
                }

                else if (currentShape instanceof Box box) {
                    Point center = box.getMin( ).add(box.getMax( )).mul(0.5);
                    Point localHit = hitPoint.sub(center);
                    double dx = Math.abs(localHit.x) - Math.abs(box.getMax( ).x - box.getMin( ).x) / 2.0;
                    double dy = Math.abs(localHit.y) - Math.abs(box.getMax( ).y - box.getMin( ).y) / 2.0;
                    double dz = Math.abs(localHit.z) - Math.abs(box.getMax( ).z - box.getMin( ).z) / 2.0;
                    if (dx > dy && dx > dz)
                        normal = new Point(Math.signum(localHit.x), 0, 0);
                    else if (dy > dz)
                        normal = new Point(0, Math.signum(localHit.y), 0);
                    else
                        normal = new Point(0, 0, Math.signum(localHit.z));
                    hitColor = box.color;
                    hitMaterial = box.material;
                    fuzz = box.fuzz;

                        Point boxMin = box.getMin( );
                        Point boxMax = box.getMax( );
                        Point boxSize = boxMax.sub(boxMin);

                        double u = 0, v = 0;
                        if (normal.x != 0) {
                            u = (hitPoint.z - boxMin.z) / boxSize.z;
                            v = (hitPoint.y - boxMin.y) / boxSize.y;
                        } else if (normal.y != 0) {
                            u = (hitPoint.x - boxMin.x) / boxSize.x;
                            v = (hitPoint.z - boxMin.z) / boxSize.z;
                        } else if (normal.z != 0) {
                            u = (hitPoint.x - boxMin.x) / boxSize.x;
                            v = (hitPoint.y - boxMin.y) / boxSize.y;
                        }

                        if (normal.x > 0 || normal.y > 0 || normal.z < 0) {
                            u = 1.0 - u;
                        }

                        Integer[] digits = box.getDigits();
                        if (digits != null && normal.z != 0) {
                            int n = digits.length;
                            double digitWidth = 1.0 / n;

                            for (int i = 0; i < n; i++) {
                                double startU = i * digitWidth;
                                double endU = (i + 1) * digitWidth;
                                if (u >= startU && u < endU) {
                                    double localU = (u - startU) / digitWidth;
                                    if (isInDigit(digits[i], localU, v, n)) {

                                        if (hitMaterial == Material.CHROME) {
                                            Point reflected = reflect(r.getDirection().normalize(), normal);
                                            reflected = reflected.add(randomInUnitSphere().mul(fuzz * 0.1));
                                            Ray rayChromeDigit = new Ray(hitPoint, reflected);
                                            Color chromeBase = rayColor(WORLD, rayChromeDigit, environmentMap, 5);


                                            return new Color(
                                                    Math.min(1f, hitColor.r * chromeBase.r * 0.7f + 0.2f),
                                                    Math.min(1f, hitColor.g * chromeBase.g * 0.8f + 0.3f),
                                                    Math.min(1f, hitColor.b * chromeBase.b * 1.2f + 0.4f)
                                            );
                                        }
                                    }
                                }
                            }
                        }



                }
            }
        }

        if (hitShape != null && hitMaterial == Material.MATTE) {
            ArrayList<Point> lightsource = new ArrayList<>();
            lightsource.add(new Point(1,1,-1).normalize());
            lightsource.add(new Point(-1,1,-1).normalize());
            float re = 0, g = 0, b = 0;
            for (Point light : lightsource) {
                double intensity = Math.max(0, normal.dot(light));
                re += hitColor.r * 0.9f * (float) intensity;
                g += hitColor.g * 0.9f * (float) intensity;
                b += hitColor.b * 0.9f * (float) intensity;
            }
            return new Color(Math.min(1, re), Math.min(1, g), Math.min(1, b));
        } else if (hitShape != null && hitMaterial == Material.CHROME) {
            Point reflected = reflect(r.getDirection().normalize( ), normal);
            reflected = reflected.add(randomInUnitSphere( ).mul(fuzz * 0.1));
            Ray rayChrome = new Ray(hitPoint, reflected);
            Color chromeColor = rayColor(WORLD, rayChrome, environmentMap, depth - 1);
            return new Color(
                    hitColor.r * chromeColor.r * 1.2f,
                    hitColor.g * chromeColor.g * 1.1f,
                    hitColor.b * chromeColor.b * 1.2f
            );
        }

        if (hitShape == null && environmentMap != null) {
            Point getDirection = r.getDirection().normalize();
            double u = 0.5 + Math.atan2(getDirection.z, getDirection.x) / (2 * Math.PI);
            double v = 0.5 - Math.asin(getDirection.y) / Math.PI;
            int x = (int)(u * environmentMap.getWidth());
            int y = (int)(v * environmentMap.getHeight());
            x = Math.min(Math.max(x, 0), environmentMap.getWidth() - 1);
            y = Math.min(Math.max(y, 0), environmentMap.getHeight() - 1);
            int rgb = environmentMap.getRGB(x, y);
            float rr = ((rgb >> 16) & 0xFF) / 255.0f;
            float g = ((rgb >> 8) & 0xFF) / 255.0f;
            float b = (rgb & 0xFF) / 255.0f;
            return new Color(rr, g, b);
        }

        Point unitDirection = r.getDirection().normalize();
        double t = 0.5 * (unitDirection.y + 1.0);
        return new Color(
                (float)((1.0 - t) + t * 0.1),
                (float)((1.0 - t) + t * 0.1),
                (float)((1.0 - t) + t * 0.99)
        );

    }

    static boolean isInDigit(int digit, double u, double v, int l) {
        u = (u - 0.3) / 0.2;
        if( l <= 2){ v = (v - 0.45) / 0.2; }
        else if(l <= 4) { v = (v - 0.52) / 0.1; }
        else { v = (v - 0.52) / 0.05;}

        if (u < -0.2 || u > 1.2 || v < -0.2 || v > 1.2) return false;

        if (u < 0 || u > 1 || v < 0 || v > 1) return false;

        double r = 0.152;
        float segW = 0.6f;
        float segH = 0.05f;



        // Horizontal bars
        boolean top    = roundRect(u, v, 0.5, 0.92, segW, segH, r);
        boolean mid    = roundRect(u, v, 0.5, 0.5, segW, segH, r);
        boolean bottom = roundRect(u, v, 0.5, 0.08, segW, segH, r);

        // Vertical bars (left/right top/mid/bottom)
        boolean lt = roundRect(u, v, 0.18, 0.75, segH, 0.3, r);
        boolean lb = roundRect(u, v, 0.18, 0.25, segH, 0.3, r);
        boolean rt = roundRect(u, v, 0.82, 0.75, segH, 0.3, r);
        boolean rb = roundRect(u, v, 0.82, 0.25, segH, 0.3, r);

        switch (digit) {
            case 0: return top || bottom || lt || lb || rt || rb;
            case 1: return rt || rb;
            case 2: return top || mid || bottom || rt || lb;
            case 3: return top || mid || bottom || rt || rb;
            case 4: return mid || rt || rb || lt;
            case 5: return top || mid || bottom || lt || rb;
            case 6: return mid || bottom || lt || lb || rb;
            case 7: return top || rt || rb;
            case 8: return top || mid || bottom || lt || lb || rt || rb;
            case 9: return top || mid || lt || rt || rb;
        }

        return false;
    }

    public static boolean roundRect(double u, double v, double cx, double cy, double w, double h, double r) {
        double dx = Math.max(Math.abs(u - cx) - w / 2.0, 0);
        double dy = Math.max(Math.abs(v - cy) - h / 2.0, 0);
        return (dx * dx + dy * dy) < r * r;
    }


    public static Point reflect(Point v, Point n) {
        return v.sub(n.mul(2 * v.dot(n)));
    }

    private static final Random RAND = new Random();

    public static Point randomInUnitSphere() {
        while (true) {
            Point p = new Point(
                    RAND.nextDouble() * 2 - 1,
                    RAND.nextDouble() * 2 - 1,
                    RAND.nextDouble() * 2 - 1
            );
            if (p.dot(p) < 1) return p;
        }
    }


}
