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

        if(hitShape != null && hitMaterial == Material.LAMBERTIAN){
            ArrayList<Point> lightsource = new ArrayList<>();
            lightsource.add(new Point(1,1,-1).normalize());
            lightsource.add(new Point(-1,1,-1).normalize());
            lightsource.add(new Point(0,-1,-1).normalize());
            float re = 0, g = 0, b = 0;
            for (Point light : lightsource) {
                double intensity = Math.max(0, normal.dot(light));
                re += hitColor.r * (float) intensity;
                g += hitColor.g * (float) intensity;
                b += hitColor.b * (float) intensity;
            }
            re = Math.min(1, re);
            g = Math.min(1, g);
            b = Math.min(1, b);
            return new Color(re, g, b);

        } else if(hitShape != null && hitMaterial == Material.METAL){
            Point ref = reflect(r.getDirection().normalize(), normal);
            ref = ref.add(randomInUnitSphere().mul(fuzz));
            if(ref.dot(normal) > 0){
                Ray refray = new Ray(hitPoint, ref);
                Color refcolor = rayColor(WORLD, refray, environmentMap, depth - 1);
                return new Color(
                        hitColor.r * refcolor.r,
                        hitColor.g * refcolor.g,
                        hitColor.b * refcolor.b
                );
            } else {
                return new Color(0,0,0);
            }

         } else if (hitShape != null && hitMaterial == Material.GLOSSY) {
            Point reflected = reflect(r.getDirection().normalize(), normal);
            reflected = reflected.add(randomInUnitSphere().mul(fuzz * 0.5));
            Ray glossyRay = new Ray(hitPoint, reflected);
            Color glossyColor = rayColor(WORLD, glossyRay, environmentMap, depth - 1);
            return new Color(
                    (hitColor.r * glossyColor.r + hitColor.r * 0.3f) / 1.3f,
                    (hitColor.g * glossyColor.g + hitColor.g * 0.3f) / 1.3f,
                    (hitColor.b * glossyColor.b + hitColor.b * 0.3f) / 1.3f
            );
        } else if (hitShape != null && hitMaterial == Material.PLASTIC) {
            Point diffuseDir = normal.add(randomInUnitSphere()).normalize();
            Ray diffuseRay = new Ray(hitPoint, diffuseDir);
            Color diffuseColor = rayColor(WORLD, diffuseRay, environmentMap, depth - 1);
            Point specular = reflect(r.getDirection().normalize(), normal).add(randomInUnitSphere().mul(fuzz * 0.3));
            Ray specularRay = new Ray(hitPoint, specular);
            Color specularColor = rayColor(WORLD, specularRay, environmentMap, depth - 1);
            return new Color(
                    0.7f * hitColor.r * diffuseColor.r + 0.3f * specularColor.r,
                    0.7f * hitColor.g * diffuseColor.g + 0.3f * specularColor.g,
                    0.7f * hitColor.b * diffuseColor.b + 0.3f * specularColor.b
            );
        } else if (hitShape != null && hitMaterial == Material.MATTE) {
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
        } else if (hitShape != null && hitMaterial == Material.MIRROR) {
            Point reflected = reflect(r.getDirection().normalize( ), normal);
            Ray reflectedRay = new Ray(hitPoint, reflected);
            Color reflectedColor = rayColor(WORLD, reflectedRay, environmentMap, depth - 1);
            return new Color(
                    reflectedColor.r,
                    reflectedColor.g,
                    reflectedColor.b
            );
        } else if (hitShape != null && hitMaterial == Material.TRANSLUCENT) {
            Point scatterDir = normal.add(randomInUnitSphere().mul(0.5)).normalize();
            Ray scatterRay = new Ray(hitPoint, scatterDir);
            Color scatterColor = rayColor(WORLD, scatterRay, environmentMap, depth - 1);
            return new Color(
                    hitColor.r * scatterColor.r * 0.9f,
                    hitColor.g * scatterColor.g * 0.9f,
                    hitColor.b * scatterColor.b * 0.9f
            );
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
        else if (hitShape != null && hitMaterial == Material.ANODIZED_METAL) {
            Point refl = reflect(r.getDirection().normalize(), normal).add(randomInUnitSphere().mul(fuzz));
            Ray rayAnodized = new Ray(hitPoint, refl);
            Color reflColor = rayColor(WORLD, rayAnodized, environmentMap, depth - 1);
            float shift = (float)Math.abs(Math.sin(hitPoint.y * 3));
            Color anodize = new Color(0.5f + shift * 0.5f, 0.3f, 0.7f);
            return new Color(
                    hitColor.r * reflColor.r * anodize.r,
                    hitColor.g * reflColor.g * anodize.g,
                    hitColor.b * reflColor.b * anodize.b
            );
        } else if (hitShape != null && hitMaterial == Material.MIST) {
            float fade = (float)Math.exp(-0.1 * hitPoint.length());
            fade = Math.max(0.50f, fade);
            return new Color(
                    hitColor.r * fade,
                    hitColor.g * fade,
                    hitColor.b * fade
            );
        } else if (hitMaterial == Material.MAGIC_GOO) {
            Point dir = normal.add(randomInUnitSphere().mul(0.7)).normalize();
            Ray rayGoo = new Ray(hitPoint, dir);
            Color scatter = rayColor(WORLD, rayGoo, environmentMap, depth - 1);
            float edgeGlow = (float)Math.pow(1 - Math.abs(normal.dot(r.getDirection().normalize())), 2);
            return new Color(
                    hitColor.r * scatter.r + edgeGlow * 0.3f,
                    hitColor.g * scatter.g + edgeGlow * 0.3f,
                    hitColor.b * scatter.b + edgeGlow * 0.3f
            );
        }

        if (hitShape == null) {
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

    public static double intersectRaySphere(Ray ray, Sphere sphere) {
        Point oc = ray.origin.sub(sphere.center);
        double a = ray.getDirection().dot(ray.getDirection());
        double b = 2.0 * oc.dot(ray.getDirection());
        double c = oc.dot(oc) - sphere.getRadius( ) * sphere.getRadius( );
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return -1;
        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t0 = (-b - sqrtDiscriminant) / (2.0 * a);
        double t1 = (-b + sqrtDiscriminant) / (2.0 * a);
        if (t0 > 1e-6) return t0;
        if (t1 > 1e-6) return t1;
        return -1;
    }

    public static double intersectRayBox(Ray ray, Box box) {

        double txmin = (box.getMin().x - ray.origin.x) / ray.getDirection().x;
        double txmax = (box.getMax().x - ray.origin.x) / ray.getDirection().x;
        if (txmin > txmax) { double temp = txmin; txmin = txmax; txmax = temp; }

        double tymin = (box.getMin().y - ray.origin.y) / ray.getDirection().y;
        double tymax = (box.getMax().y - ray.origin.y) / ray.getDirection().y;
        if (tymin > tymax) { double temp = tymin; tymin = tymax; tymax = temp; }

        if ((txmin > tymax) || (tymin > txmax)) return -1;

        if (tymin > txmin) txmin = tymin;
        if (tymax < txmax) txmax = tymax;

        double tzmin = (box.getMin().z - ray.origin.z) / ray.getDirection().z;
        double tzmax = (box.getMax().z - ray.origin.z) / ray.getDirection().z;
        if (tzmin > tzmax) { double temp = tzmin; tzmin = tzmax; tzmax = temp; }

        if ((txmin > tzmax) || (tzmin > txmax)) return -1;

        if (tzmin > txmin) txmin = tzmin;
        if (tzmax < txmax) txmax = tzmax;

        if (txmin < 1e-6 && txmax < 1e-6) return -1;

        return txmin > 1e-6 ? txmin : txmax;
    }


}




