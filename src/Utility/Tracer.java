package Utility;

import Shapes.Box;
import Shapes.Shape;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static Utility.Digit.isInDigit;

public class Tracer {

    private static final Random RAND = new Random();

    public static Color rayColor(ArrayList<Shape> world, Ray r, BufferedImage environmentMap, int depth) {
        if (depth <= 0) return new Color(0, 0, 0);
        double nearest = Double.MAX_VALUE;
        Point hitPoint = null, normal = null;
        Color hitColor = null;
        double fuzz = 0;
        Material material = null;
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

            if (material == Material.CHROME) {
                Point reflectedDir = reflect(r.getDirection().normalize(), normal);
                if (fuzz > 0) {
                    reflectedDir = reflectedDir.add(randomInUnitSphere().mul(fuzz));
                }
                Ray reflectedRay = new Ray(hitPoint, reflectedDir);
                Color reflectedColor = rayColor(world, reflectedRay, environmentMap, depth - 1);

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
