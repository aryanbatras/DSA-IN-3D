package Animator.Core;

import Shapes.Box;
import Shapes.Shape;
import Utility.Camera;
import Utility.Color;
import Utility.Render;
import Utility.Point;

import java.util.ArrayList;

public class BoxAnimator {
    private final ArrayList<Shape> world;
    private final Render renderer;
    private final Camera camera;
    private final int frames;
    private static final double BOUNCE_HEIGHT = 0.5;

    public BoxAnimator(Render renderer, Camera camera, ArrayList<Shape> world, int framesPerSecond) {
        this.frames = framesPerSecond;
        this.renderer = renderer;
        this.camera = camera;
        this.world = world;
    }

    public void slideFromLeft(Box box, double finalX) {
        double initialX = finalX - 5.0;
        double delta = (finalX - initialX) / frames;
        box.center.x = initialX;
        for (int i = 0; i < frames; i++) {
            box.center.x += delta;
            renderer.drawImage(camera, world);
        }
        box.center.x = finalX;
    }

    public void slideFromRight(Box box, double finalX) {
        double initialX = finalX + 5.0;
        double delta = (finalX - initialX) / frames;
        box.center.x = initialX;
        for (int i = 0; i < frames; i++) {
            box.center.x += delta;
            renderer.drawImage(camera, world);
        }
        box.center.x = finalX;
    }

    public void slideFromTop(Box box, double finalY) {
        double initialY = finalY + 5.0;
        double delta = (finalY - initialY) / frames;
        box.center.y = initialY;
        for (int i = 0; i < frames; i++) {
            box.center.y += delta;
            renderer.drawImage(camera, world);
        }
        box.center.y = finalY;
    }

    public void slideUp(Box box, double finalY) {
        double initialY = box.center.y;
        double delta = (finalY - initialY) / frames;
        for (int i = 0; i < frames; i++) {
            box.center.y += delta;
            renderer.drawImage(camera, world);
        }
        box.center.y = finalY;
    }

    public void bounceIn(Box box, double baseY) {
        box.center.y = baseY + BOUNCE_HEIGHT;
        double gravity = (BOUNCE_HEIGHT * 2) / (frames * frames);
        double velocity = 0;
        for (int i = 0; i < frames; i++) {
            velocity -= gravity;
            box.center.y += velocity;
            if (box.center.y < baseY) {
                box.center.y = baseY;
                velocity = -velocity * 0.6; // dampen bounce
            }
            renderer.drawImage(camera, world);
        }
        box.center.y = baseY;
    }

    public void shake(Box box) {
        double originalX = box.center.x;
        for (int i = 0; i < frames; i++) {
            box.center.x = originalX + Math.sin(i * 0.5) * 0.2;
            renderer.drawImage(camera, world);
        }
        box.center.x = originalX;
    }

    public void scalePop(Box box) {
        double originalWidth = box.width;
        double originalHeight = box.height;
        double maxScale = 1.4;

        for (int i = 0; i < frames / 2; i++) {
            double scale = 1 + (maxScale - 1) * i / (frames / 2);
            box.width = originalWidth * scale;
            box.height = originalHeight * scale;
            renderer.drawImage(camera, world);
        }

        for (int i = 0; i < frames / 2; i++) {
            double scale = maxScale - (maxScale - 1) * i / (frames / 2);
            box.width = originalWidth * scale;
            box.height = originalHeight * scale;
            renderer.drawImage(camera, world);
        }

        box.width = originalWidth;
        box.height = originalHeight;
    }

    public void fadeOutAndUp(Box box, double finalY) {
        double initialY = box.center.y;
        double deltaY = (finalY - initialY) / frames;
        float alpha = 1.0f;
        float fadeRate = alpha / frames;

        for (int i = 0; i < frames; i++) {
            box.center.y += deltaY;
            alpha -= fadeRate;
            float clampedAlpha = Math.max(0, alpha);
            box.color = new Color(box.color.r * clampedAlpha, box.color.g * clampedAlpha, box.color.b * clampedAlpha);
            renderer.drawImage(camera, world);
        }

        // Restore original color (optional)
        box.color = new Color(box.color.r, box.color.g, box.color.b);
    }

    public void shiftLeft(Box box) {
        double delta = 1.0 / frames;
        for (int i = 0; i < frames; i++) {
            box.center.x += delta;
            renderer.drawImage(camera, world);
        }
    }

    public void shiftElementsLeft(int startIndex) {
        for (int i = startIndex; i < world.size(); i++) {
            Shape shape = world.get(i);
            if (shape instanceof Box box) {
                shiftLeft(box);
            }
        }
    }

    public void scaleDown(Box box) {
        double originalWidth = box.width;
        double originalHeight = box.height;

        for (int i = 0; i < frames; i++) {
            double scale = 1 - (double) i / frames;
            box.width = originalWidth * scale;
            box.height = originalHeight * scale;
            renderer.drawImage(camera, world);
        }
    }


    public void shakeAndFade(Box box) {
        double originalX = box.center.x;
        float alpha = 1.0f;
        float fadeRate = alpha / frames;

        for (int i = 0; i < frames; i++) {
            box.center.x = originalX + Math.sin(i * 0.5) * 0.2;
            alpha -= fadeRate;
            box.color = new Color(
                    box.color.r * Math.max(0, alpha),
                    box.color.g * Math.max(0, alpha),
                    box.color.b * Math.max(0, alpha)
            );
            renderer.drawImage(camera, world);
        }
    }

    public void shrinkAndDrop(Box box) {
        double deltaY = 1.5 / frames;
        double originalWidth = box.width;
        double originalHeight = box.height;

        for (int i = 0; i < frames; i++) {
            double scale = 1 - (double) i / frames;
            box.width = originalWidth * scale;
            box.height = originalHeight * scale;
            box.center.y -= deltaY;
            renderer.drawImage(camera, world);
        }
    }


}
