package Animations.Animator.AnimatorCore;

import Rendering.Render;
import Rendering.View;
import Shapes.JBox;
import Shapes.Core.Shape;
import Utility.*;

import java.util.ArrayList;

public class BoxAnimator {
    private static final double BOUNCE_HEIGHT = 0.5;
    private CameraAnimator rotationModeAnimator;
    private View rotationMode;
    private final ArrayList<Shape> world;
    private final Subtitle subtitle;
    private final Renderer renderer;
    private final Camera camera;
    private int frames;
    private Render mode;
    private double speed;

    public BoxAnimator(Renderer renderer, Camera camera, ArrayList<Shape> world, Subtitle subtitle, int framesPerSecond) {
        this.rotationModeAnimator = null;
        this.rotationMode = View.NONE;
        this.frames = framesPerSecond;
        this.subtitle = subtitle;
        this.renderer = renderer;
        this.camera = camera;
        this.speed = 0.0052;
        this.world = world;
    }

    public void setFPS(int fps) {
        this.frames = fps;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setMode(Render mode) {
        this.mode = mode;
    }

    public void setCameraRotation(CameraAnimator cameraAnimator, View rotationType) {
        this.rotationMode = rotationType;
        this.rotationModeAnimator = cameraAnimator;
    }

    public void highlightTwoBoxes(JBox a, JBox b) {
        Color originalA = a.color;
        Color originalB = b.color;

        for (int i = 0; i < frames; i++) {
            float pulse = Math.abs((float)Math.sin(i * 0.15));
            a.color = new Color(pulse, 0.2f, 1f - pulse);
            b.color = new Color(pulse, 0.2f, 1f - pulse);
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }

        a.color = originalA;
        b.color = originalB;
    }

    public void highlight(JBox JBox) {
        Color c = JBox.color;
        for (int i = 0; i < frames; i++) {
            JBox.color = new Color(
                    Math.abs((float)Math.sin(i * 0.1)),
                    Math.abs((float)Math.sin(i * 0.1)),
                    JBox.color.b
            );
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
        JBox.color = c;
    }

    public void updateValue(JBox JBox) {
        Color originalColor = new Color(JBox.color);
        double originalPos = JBox.center.x;

        for (int i = 0; i < frames / 2; i++) {
            JBox.color = new Color(
                0.4f + 0.3f * (float)Math.sin(i * 0.5),
                0.7f - 0.3f * (float)Math.sin(i * 0.5),
                1.0f
            );
            JBox.center.x = originalPos + (Math.random() - 0.5) * 0.1;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
        JBox.center.x = originalPos;
        JBox.color = originalColor;
    }

    public void slideFromLeft(JBox JBox, double finalX) {
        double initialX = finalX - 5.0;
        double delta = (finalX - initialX) / frames;
        JBox.center.x = initialX;

        for (int i = 0; i < frames; i++) {
            JBox.center.x += delta;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
        JBox.center.x = finalX;
    }

    public void slideFromRight(JBox JBox, double finalX) {
        double initialX = finalX + 5.0;
        double delta = (finalX - initialX) / frames;
        JBox.center.x = initialX;
        for (int i = 0; i < frames; i++) {
            JBox.center.x += delta;
            renderer.drawImage(camera, world, subtitle, mode, 0);
            CameraAnimator.triggerOptionalCameraEffect(speed, rotationMode, rotationModeAnimator, camera);
        }
        JBox.center.x = finalX;
    }

    public void slideFromTop(JBox JBox, double finalY) {
        double initialY = finalY + 5.0;
        double delta = (finalY - initialY) / frames;
        JBox.center.y = initialY;
        for (int i = 0; i < frames; i++) {
            JBox.center.y += delta;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
        JBox.center.y = finalY;
    }

    public void slideUp(JBox JBox, double finalY) {
        double initialY = JBox.center.y;
        double delta = (finalY - initialY) / frames;
        for (int i = 0; i < frames; i++) {
            JBox.center.y += delta;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
        JBox.center.y = finalY;
    }

    public void slideDown(JBox JBox, int finalY) {
        double initialY = JBox.center.y;
        double delta = (initialY - finalY) / frames;
        for (int i = 0; i < frames; i++) {
            JBox.center.y -= delta;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
        JBox.center.y = finalY;
    }

    public void bounceIn(JBox JBox, double baseY) {
        JBox.center.y = baseY + BOUNCE_HEIGHT;
        double gravity = (BOUNCE_HEIGHT * 2) / (frames * frames);
        double velocity = 0;
        for (int i = 0; i < frames; i++) {
            velocity -= gravity;
            JBox.center.y += velocity;
            if (JBox.center.y < baseY) {
                JBox.center.y = baseY;
                velocity = -velocity * 0.6; // dampen bounce
            }
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
        JBox.center.y = baseY;
    }

    public void shake(JBox JBox) {
        double originalX = JBox.center.x;
        for (int i = 0; i < frames; i++) {
            JBox.center.x = originalX + Math.sin(i * 0.5) * 0.2;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
        JBox.center.x = originalX;
    }

 public void shakeSlow(JBox JBox) {
     double originalX = JBox.center.x;
     for (int i = 0; i < frames / 2; i++) {
         JBox.center.x = originalX + Math.sin(i * 0.5) * 0.05;
         renderer.drawImage(camera, world, subtitle, mode, 0);
     }
     JBox.center.x = originalX;
 }

    public void scalePop(JBox JBox) {
        double originalWidth = JBox.width;
        double originalHeight = JBox.height;
        double maxScale = 1.4;

        for (int i = 0; i < frames / 2; i++) {
            double scale = 1 + (maxScale - 1) * i / (frames / 2);
            JBox.width = originalWidth * scale;
            JBox.height = originalHeight * scale;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }

        for (int i = 0; i < frames / 2; i++) {
            double scale = maxScale - (maxScale - 1) * i / (frames / 2);
            JBox.width = originalWidth * scale;
            JBox.height = originalHeight * scale;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }

        JBox.width = originalWidth;
        JBox.height = originalHeight;
    }

    public void fadeOutAndUp(JBox JBox, double finalY) {
        double initialY = JBox.center.y;
        double deltaY = (finalY - initialY) / frames;
        float alpha = 1.0f;
        float fadeRate = alpha / frames;

        for (int i = 0; i < frames; i++) {
            JBox.center.y += deltaY;
            alpha -= fadeRate;
            float clampedAlpha = Math.max(0, alpha);
            JBox.color = new Color(JBox.color.r * clampedAlpha, JBox.color.g * clampedAlpha, JBox.color.b * clampedAlpha);
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }

        JBox.color = new Color(JBox.color.r, JBox.color.g, JBox.color.b);
    }

    public void shiftLeft(JBox JBox) {
        double delta = 1.0 / frames;
        for (int i = 0; i < frames; i++) {
            JBox.center.x += delta;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
    }

    public void shiftElementsLeft(int startIndex) {
        for (int i = startIndex; i < world.size(); i++) {
            Shape shape = world.get(i);
            if (shape instanceof JBox JBox) {
                shiftLeft(JBox);
            }
        }
    }

    public void scaleDown(JBox JBox) {
        double originalWidth = JBox.width;
        double originalHeight = JBox.height;

        for (int i = 0; i < frames; i++) {
            double scale = 1 - (double) i / frames;
            JBox.width = originalWidth * scale;
            JBox.height = originalHeight * scale;
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
    }

    public void shakeAndFade(JBox JBox) {
        double originalX = JBox.center.x;
        float alpha = 1.0f;
        float fadeRate = alpha / frames;

        for (int i = 0; i < frames; i++) {
            JBox.center.x = originalX + Math.sin(i * 0.5) * 0.2;
            alpha -= fadeRate;
            JBox.color = new Color(
                    JBox.color.r * Math.max(0, alpha),
                    JBox.color.g * Math.max(0, alpha),
                    JBox.color.b * Math.max(0, alpha)
            );
            renderer.drawImage(camera, world, subtitle, mode, 0);
        }
    }

    public void shrinkAndDrop(JBox JBox) {
        double deltaY = 1.5 / frames;
        double originalWidth = JBox.width;
        double originalHeight = JBox.height;

        for (int i = 0; i < frames; i++) {
            double scale = 1 - (double) i / frames;
            JBox.width = originalWidth * scale;
            JBox.height = originalHeight * scale;
            JBox.center.y -= deltaY;
            renderer.drawImage(camera, world, subtitle, mode, 0);

        }
    }
}
