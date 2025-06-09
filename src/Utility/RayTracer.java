package Utility;

import Rendering.Material;
import Rendering.Particle;
import Rendering.Render;
import Shapes.Box;
import Shapes.Shape;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import static Utility.Digit.isInDigit;

public class RayTracer {

    private static final Random RAND = new Random();

    public static Color rayColor( Render mode, Camera camera, ArrayList<Shape> world, Ray r, BufferedImage environmentMap, int depth) {
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

                    if (box.particleEffect == Particle.GRADIENT) {

                        float flicker = 0.8f + RAND.nextFloat( ) * 0.4f;
                        float glow = (float) Math.pow(Math.max(0, 1 - (hitPoint.y - box.getMax().y)), 2);

                        float red = Math.min(1f, flicker);
                        float green = Math.min(1f, 0.4f * flicker + 0.2f * glow);
                        float blue = Math.min(1f, 0.05f * flicker + 0.1f * glow);

                        hitColor = new Color(
                                (float)(hitColor.r * 0.4 + red * 0.6),
                                (float)(hitColor.g * 0.4 + green * 0.6),
                                (float)(hitColor.b * 0.4 + blue * 0.6)
                        );
                    }

                    else if (box.particleEffect == Particle.WATER) {
                        float ripple = (float) Math.sin(hitPoint.y * 10 + RAND.nextFloat() * 5);
                        float blueShift = 0.6f + 0.4f * ripple;
                        hitColor = new Color(
                                (float)(hitColor.r * 0.3),
                                (float)(hitColor.g * 0.6),
                                (float)(Math.min(1f, hitColor.b * 0.5 + blueShift))
                        );
                    }

                    else if (box.particleEffect == Particle.MAGIC) {
                        float phase = (float)(Math.sin(System.nanoTime() * 1e-9 + hitPoint.x + hitPoint.z) * 0.5 + 0.5);
                        hitColor = new Color(
                                (float)(hitColor.r * 0.3 + 0.4f * phase),
                                (float)(hitColor.g * 0.4 + 0.3f * (1 - phase)),
                                (float)(hitColor.b * 0.5 + 0.5f * phase)
                        );
                    }

                    else if (box.particleEffect == Particle.FIREWORKS) {
                        float flicker = 0.9f + RAND.nextFloat() * 0.2f;
                        hitColor = new Color(
                                Math.min(1f, hitColor.r + 0.4f * flicker),
                                Math.min(1f, hitColor.g * 0.6f * flicker),
                                Math.min(1f, hitColor.b * 0.2f * flicker)
                        );
                    }


                    if (box.particleEffect == Particle.ELECTRIC_SHOCK) {
                        float pulse = RAND.nextFloat();
                        if (pulse > 0.97f) {
                            hitColor = new Color(1f, 1f, 1f); // bright flash
                        } else {
                            hitColor = new Color(
                                    (float)(hitColor.r * 0.6 + 0.4 * RAND.nextFloat()),
                                    (float)(hitColor.g * 0.6),
                                    (float)(hitColor.b * 0.8 + 0.2 * RAND.nextFloat())
                            );
                        }
                    }


                    if (box.particleEffect == Particle.BLOOD_DRIP) {
                        float drip = (float)(Math.abs(Math.sin(hitPoint.y * 10 + System.nanoTime() * 1e-9)));
                        hitColor = new Color(
                                0.6f + drip * 0.3f,
                                0.0f + drip * 0.1f,
                                0.0f
                        );
                    }


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
                                Color bounce = rayColor(mode, camera, world, new Ray(hitPoint, reflected), environmentMap, depth - 1);
                                float glow = (float) Math.pow(1.0f - Math.abs(normal.z), 3);
                                float edge = (float) Math.pow(Math.abs(Math.sin(hitPoint.x * 8) * Math.cos(hitPoint.y * 8)), 4);

                                return new Color(
                                        Math.min(1f, hitColor.r * 0.5f + bounce.r * 0.4f + glow * 0.1f + edge * 0.1f),
                                        Math.min(1f, hitColor.g * 0.5f + bounce.g * 0.4f + glow * 0.2f + edge * 0.1f),
                                        Math.min(1f, hitColor.b * 0.5f + bounce.b * 0.5f + glow * 0.3f + edge * 0.2f)
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
                Color reflectedColor = rayColor(mode, camera, world, reflectedRay, environmentMap, depth - 1);

                return new Color(
                        (float) (hitColor.r * 0.2 + reflectedColor.r * 0.8),
                        (float) (hitColor.g * 0.2 + reflectedColor.g * 0.8),
                        (float) (hitColor.b * 0.2 + reflectedColor.b * 0.8)
                );
            }

            else if(material == Material.LAMBERTIAN){
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

            }

            else if(material == Material.METAL){
                Point ref = reflect(r.getDirection().normalize(), normal);
                ref = ref.add(randomInUnitSphere().mul(fuzz));
                if(ref.dot(normal) > 0){
                    Ray refray = new Ray(hitPoint, ref);
                    Color refcolor = rayColor(mode, camera, world, refray, environmentMap, depth - 1);
                    return new Color(
                            hitColor.r * refcolor.r,
                            hitColor.g * refcolor.g,
                            hitColor.b * refcolor.b
                    );
                } else {
                    return new Color(0,0,0);
                }

            }

            else if (material == Material.GLOSSY) {
                Point reflected = reflect(r.getDirection().normalize(), normal);
                reflected = reflected.add(randomInUnitSphere().mul(fuzz * 0.5));
                Ray glossyRay = new Ray(hitPoint, reflected);
                Color glossyColor = rayColor(mode, camera, world, glossyRay, environmentMap, depth - 1);
                return new Color(
                        (hitColor.r * glossyColor.r + hitColor.r * 0.3f) / 1.3f,
                        (hitColor.g * glossyColor.g + hitColor.g * 0.3f) / 1.3f,
                        (hitColor.b * glossyColor.b + hitColor.b * 0.3f) / 1.3f
                );
            }

            else if (material == Material.MATTE) {
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
            }

            else if (material == Material.MIRROR) {
                Point reflected = reflect(r.getDirection().normalize( ), normal);
                Ray reflectedRay = new Ray(hitPoint, reflected);
                Color reflectedColor = rayColor(mode, camera, world, reflectedRay, environmentMap, depth - 1);
                return new Color(
                        reflectedColor.r,
                        reflectedColor.g,
                        reflectedColor.b
                );
            }

            else if (material == Material.ANODIZED_METAL) {
                Point refl = reflect(r.getDirection().normalize(), normal).add(randomInUnitSphere().mul(fuzz));
                Ray rayAnodized = new Ray(hitPoint, refl);
                Color reflColor = rayColor(mode, camera, world, rayAnodized, environmentMap, depth - 1);
                float shift = (float)Math.abs(Math.sin(hitPoint.y * 3));
                Color anodize = new Color(0.5f + shift * 0.5f, 0.3f, 0.7f);
                return new Color(
                        hitColor.r * reflColor.r * anodize.r,
                        hitColor.g * reflColor.g * anodize.g,
                        hitColor.b * reflColor.b * anodize.b
                );
            }

            else if (material == Material.MIST) {
                float fade = (float)Math.exp(-0.1 * hitPoint.length());
                fade = Math.max(0.50f, fade);
                return new Color(
                        hitColor.r * fade,
                        hitColor.g * fade,
                        hitColor.b * fade
                );
            }





            return hitColor;
        }

        if(mode == Render.STEP_WISE_INTERACTIVE){
           return sampleEnvironment(camera, r, environmentMap);
        } else return sampleEnvironment(mode, camera, r, environmentMap);
    }

    private static Color sampleEnvironment(Camera camera, Ray r, BufferedImage env) {
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

    private static Color sampleEnvironment(Render mode, Camera camera, Ray r, BufferedImage env) {
        Point dir = r.getDirection().normalize();
        Point dirInEnvSpace = camera.inverseRotateDirection(dir);

        double u = 1.0 + Math.atan2(dirInEnvSpace.z, dirInEnvSpace.x) / (2 * Math.PI);
        double v = 0.5 - Math.asin(dirInEnvSpace.y) / Math.PI;

        double parallaxScaleU = 0.025;
        double parallaxScaleV = 0.025;

        double uOffset = - (camera.getM_X() * parallaxScaleU);
        double vOffset = (camera.getM_Y() * parallaxScaleV);

        u += uOffset;
        if (u > 1.0) u -= 1.0;
        if (u < 0) u += 1.0;

        v += vOffset;
        if (v > 1.0) v -= 1.0;
        if (v < 0) v += 1.0;

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
