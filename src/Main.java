import Shapes.Box;
import Shapes.Shape;
import Shapes.Sphere;
import Utility.Color;
import Utility.Material;
import Utility.Point;
import Utility.Ray;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static Random random;
    public static void main(String[] args) throws IOException {
        random = new Random();
        new Window();
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
                    Point center = box.getMin().add(box.getMax()).mul(0.5);
                    Point localHit = hitPoint.sub(center);
                    double dx = Math.abs(localHit.x) - Math.abs(box.getMax().x - box.getMin().x) / 2.0;
                    double dy = Math.abs(localHit.y) - Math.abs(box.getMax().y - box.getMin().y) / 2.0;
                    double dz = Math.abs(localHit.z) - Math.abs(box.getMax().z - box.getMin().z) / 2.0;
                    if (dx > dy && dx > dz)
                        normal = new Point(Math.signum(localHit.x), 0, 0);
                    else if (dy > dz)
                        normal = new Point(0, Math.signum(localHit.y), 0);
                    else
                        normal = new Point(0, 0, Math.signum(localHit.z));
                    hitColor = box.color;
                    hitMaterial = box.material;
                    fuzz = box.fuzz;
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

    public static Point reflect(Point v, Point n) {
        return v.sub(n.mul(2 * v.dot(n)));
    }

    public static Point randomInUnitSphere() {
        while (true) {
            Point p = new Point(
                    random.nextDouble() * 2 - 1,
                    random.nextDouble() * 2 - 1,
                    random.nextDouble() * 2 - 1
            );
            if (p.dot(p) < 1) return p;
        }
    }

}




