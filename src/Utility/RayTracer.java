package Utility;

import Rendering.Texture;
import Rendering.Effect;
import Rendering.Render;
import Shapes.JBox;
import Shapes.Core.Shape;

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
        Texture material = null;
        for (Shape shape : world) {
            double t = shape.hit(r);
            if (t > 0.001 && t < nearest) {
                if (shape instanceof JBox JBox) {
                    nearest = t;
                    hitPoint = r.at(t);
                    normal = JBox.getNormal(hitPoint);
                    hitColor = JBox.color;
                    fuzz = JBox.fuzz;
                    material = JBox.material;

                    if (JBox.particleEffect == Effect.GRADIENT) {

                        float flicker = 0.8f + RAND.nextFloat( ) * 0.4f;
                        float glow = (float) Math.pow(Math.max(0, 1 - (hitPoint.y - JBox.getMax().y)), 2);

                        float red = Math.min(1f, flicker);
                        float green = Math.min(1f, 0.4f * flicker + 0.2f * glow);
                        float blue = Math.min(1f, 0.05f * flicker + 0.1f * glow);

                        hitColor = new Color(
                                (float)(hitColor.r * 0.4 + red * 0.6),
                                (float)(hitColor.g * 0.4 + green * 0.6),
                                (float)(hitColor.b * 0.4 + blue * 0.6)
                        );
                    }

                    else if (JBox.particleEffect == Effect.WATER) {
                        float ripple = (float) Math.sin(hitPoint.y * 10 + RAND.nextFloat() * 5);
                        float blueShift = 0.6f + 0.4f * ripple;
                        hitColor = new Color(
                                (float)(hitColor.r * 0.3),
                                (float)(hitColor.g * 0.6),
                                (float)(Math.min(1f, hitColor.b * 0.5 + blueShift))
                        );
                    }

                    else if (JBox.particleEffect == Effect.MAGIC) {
                        float phase = (float)(Math.sin(System.nanoTime() * 1e-9 + hitPoint.x + hitPoint.z) * 0.5 + 0.5);
                        hitColor = new Color(
                                (float)(hitColor.r * 0.3 + 0.4f * phase),
                                (float)(hitColor.g * 0.4 + 0.3f * (1 - phase)),
                                (float)(hitColor.b * 0.5 + 0.5f * phase)
                        );
                    }

                    else if (JBox.particleEffect == Effect.FIREWORKS) {
                        float flicker = 0.9f + RAND.nextFloat() * 0.2f;
                        hitColor = new Color(
                                Math.min(1f, hitColor.r + 0.4f * flicker),
                                Math.min(1f, hitColor.g * 0.6f * flicker),
                                Math.min(1f, hitColor.b * 0.2f * flicker)
                        );
                    }


                    if (JBox.particleEffect == Effect.ELECTRIC_SHOCK) {
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


                    if (JBox.particleEffect == Effect.BLOOD_DRIP) {
                        float drip = (float)(Math.abs(Math.sin(hitPoint.y * 10 + System.nanoTime() * 1e-9)));
                        hitColor = new Color(
                                0.6f + drip * 0.3f,
                                0.0f + drip * 0.1f,
                                0.0f
                        );
                    }

                    else if (JBox.particleEffect == Effect.GALAXY) {
                        float time = (float)(System.nanoTime() * 1e-9);
                        float noise = (float)Math.sin(hitPoint.x * 10 + hitPoint.z * 5 + time) * 0.5f + 0.5f;
                        float rr = 0.2f + 0.3f * (float)Math.sin(time * 0.5f);
                        float g = 0.1f + 0.2f * (float)Math.cos(time * 0.3f);
                        float b = 0.7f + 0.3f * (float)Math.sin(time * 0.2f);
                        hitColor = new Color(
                                hitColor.r * 0.6f + rr * noise * 0.4f,
                                hitColor.g * 0.6f + g * noise * 0.4f,
                                hitColor.b * 0.8f + b * noise * 0.2f
                        );
                    }

                    else if (JBox.particleEffect == Effect.AURORA) {
                        float time = (float)(System.nanoTime() * 1e-9);
                        float wave = (float)Math.sin(hitPoint.y * 3 + time * 0.5f) * 0.5f + 0.5f;
                        float intensity = (float)Math.sin(hitPoint.x * 2 + hitPoint.z * 2) * 0.5f + 0.5f;
                        hitColor = new Color(
                                hitColor.r * 0.6f + 0.3f * wave * intensity,
                                hitColor.g * 0.8f + 0.7f * wave * intensity,
                                hitColor.b * 0.9f + 0.5f * wave * intensity
                        );
                    }

                    else if (JBox.particleEffect == Effect.NEON_GRID) {
                        float time = (float)(System.nanoTime() * 1e-9);
                        float gridX = (float)(Math.sin(hitPoint.x * 20 + time) + 1) * 0.5f;
                        float gridZ = (float)(Math.cos(hitPoint.z * 20 + time * 0.7f) + 1) * 0.5f;
                        float intensity = Math.min(1.0f, gridX + gridZ);
                        hitColor = new Color(
                                hitColor.r * 0.7f + 0.3f * (1 - intensity),
                                hitColor.g * 0.7f + 0.8f * intensity,
                                hitColor.b * 0.7f + 0.9f * intensity
                        );
                    }

                    else if (JBox.particleEffect == Effect.COSMIC_DUST) {
                        float time = (float)(System.nanoTime() * 1e-9);
                        float noise1 = (float)Math.sin(hitPoint.x * 15 + time * 0.3f) * 0.5f + 0.5f;
                        float noise2 = (float)Math.sin(hitPoint.y * 20 + time * 0.4f) * 0.5f + 0.5f;
                        float noise3 = (float)Math.sin(hitPoint.z * 15 + time * 0.5f) * 0.5f + 0.5f;
                        float intensity = (noise1 * noise2 * noise3) * 0.8f + 0.2f;
                        hitColor = new Color(
                                hitColor.r * 0.5f + 0.5f * intensity * (0.7f + 0.3f * noise1),
                                hitColor.g * 0.5f + 0.5f * intensity * (0.5f + 0.5f * noise2),
                                hitColor.b * 0.5f + 0.5f * intensity * (0.3f + 0.7f * noise3)
                        );
                    }

                    else if (JBox.particleEffect == Effect.CLOUDS) {
                        float time = (float)(System.nanoTime() * 1e-9);
                        float wave = (float)Math.sin(hitPoint.y * 3 + time * 0.5f) * 0.5f + 0.5f;
                        float intensity = (float)Math.sin(hitPoint.x * 2 + hitPoint.z * 2) * 0.5f + 0.5f;
                        hitColor = new Color(
                                hitColor.r * 0.6f + 0.3f * wave * intensity,
                                hitColor.g * 0.8f + 0.7f * wave * intensity,
                                hitColor.b * 0.9f + 0.5f * wave * intensity
                        );
                    }

                    if (normal.z != 0 && JBox.getDigits( ) != null) {
                        Point boxMin = JBox.getMin( ), boxMax = JBox.getMax( ), boxSize = boxMax.sub(boxMin);
                        double u = 1.0 - (hitPoint.x - boxMin.x) / boxSize.x;
                        double v = (hitPoint.y - boxMin.y) / boxSize.y;
                        Integer[] digits = JBox.getDigits( );
                        double digitWidth = 1.0 / digits.length;
                        for (int i = 0; i < digits.length; i++) {
                            double startU = i * digitWidth, endU = (i + 1) * digitWidth;
                            if (u >= startU && u < endU && isInDigit(digits[i], (u - startU) / digitWidth, v, digits.length)) {
                                Point reflected = reflect(r.getDirection( ).normalize( ), normal)
                                        .add(randomInUnitSphere( ).mul(fuzz * 0.1));
                                Color bounce = rayColor(mode, camera, world, new Ray(hitPoint, reflected), environmentMap, depth - 1);
                                float glow = (float) Math.pow(1.0f - Math.abs(normal.z), 3);
                                float edge = (float) Math.pow(Math.abs(Math.sin(hitPoint.x * 8) * Math.cos(hitPoint.y * 8)), 4);

                                float base = 0.8f;
                                float emission = 0.5f * glow + 0.3f * edge;
                                float shineBoost = 0.3f;

                                return new Color(
                                        Math.min(1f, hitColor.r * base + bounce.r * 0.2f + emission + shineBoost),
                                        Math.min(1f, hitColor.g * base + bounce.g * 0.2f + emission + shineBoost),
                                        Math.min(1f, hitColor.b * base + bounce.b * 0.3f + emission + shineBoost)
                                );

                            }
                        }
                    }
                }
            }
        }
        if (hitPoint != null && hitColor != null) {

            if (material == Texture.CHROME) {
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


            else if(material == Texture.METAL){
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

            else if (material == Texture.GLOSSY) {
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


            else if (material == Texture.MIRROR) {
                Point reflected = reflect(r.getDirection().normalize( ), normal);
                Ray reflectedRay = new Ray(hitPoint, reflected);
                Color reflectedColor = rayColor(mode, camera, world, reflectedRay, environmentMap, depth - 1);
                return new Color(
                        reflectedColor.r,
                        reflectedColor.g,
                        reflectedColor.b
                );
            }

            else if (material == Texture.ANODIZED_METAL) {
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

        double parallaxScaleU = 0.0052;
        double parallaxScaleV = 0.0052;

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
