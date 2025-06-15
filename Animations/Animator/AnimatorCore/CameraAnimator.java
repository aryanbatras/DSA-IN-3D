package Animations.Animator.AnimatorCore;

import Rendering.View;
import Shapes.Core.Shape;
import Utility.*;
import Rendering.Render;

import java.util.ArrayList;

public class CameraAnimator {
    private final Subtitle subtitle;

    private View rotationMode;
    private ArrayList<Shape> world;
    private Renderer renderer;
    private Camera camera;
    private double speed;
    private Render mode;
    private int frames;
    private double previousRadius;


    public CameraAnimator(Renderer renderer, Camera camera, ArrayList<Shape> world, Subtitle subtitle, int framesPerSecond){
        this.rotationMode = View.NONE;
        this.frames = framesPerSecond;
        this.previousRadius = -1;
        this.subtitle = subtitle;
        this.renderer = renderer;
        this.camera = camera;
        this.world = world;
        this.speed = 0.0052;
    }

    public void slideTo(Point p) {

        double targetX = p.x;
        double targetY = p.y;

        double startX = camera.getM_X();
        double startY = camera.getM_Y();

        double deltaX = (targetX - startX) / frames;
        double deltaY = (targetY - startY) / frames;

        for (int i = 0; i < frames; i++) {
            camera.setM_X(camera.getM_X() + deltaX);
            camera.setM_Y(camera.getM_Y() + deltaY);

            renderer.drawImage(camera, world, subtitle, mode, 0);
            CameraAnimator.triggerOptionalCameraEffect(speed, rotationMode, this, camera);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);
    }

    public void slideAlongY(double cameraFinal) {
        double cameraInitial = camera.getM_Y();
        double delta = (cameraFinal - cameraInitial) / frames;
        for (int i = 0; i < frames; i++) {
            camera.setM_Y(camera.getM_Y() + delta);
            renderer.drawImage(camera, world, subtitle, mode, 0);
            CameraAnimator.triggerOptionalCameraEffect(speed, rotationMode, this, camera);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);
    }

    public void slideAlongX(double cameraFinal){
        double cameraInitial = camera.getM_X();
        double delta = (cameraFinal - cameraInitial) / frames;
        for (int i = 0; i < frames; i++) {
            camera.setM_X(camera.getM_X() + delta);
            renderer.drawImage(camera, world, subtitle, mode, 0);
            CameraAnimator.triggerOptionalCameraEffect(speed, rotationMode, this, camera);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);
    }

    public void zoomOut(double x1, double x2) {
        double center = (x1 + x2) / 2.0;
        double distance = Math.abs(x2 - x1);
        double targetRadius = Math.max(1.5, distance * 1.3);

        previousRadius = camera.getRadius();

        double initialRadius = camera.getRadius();
        double delta = (targetRadius - initialRadius) / frames;

        for (int i = 0; i < frames; i++) {
            camera.setRadius(camera.getRadius() + delta);
            camera.setM_X(center);
            renderer.drawImage(camera, world, subtitle, mode, 0);
            CameraAnimator.triggerOptionalCameraEffect(speed, rotationMode, this, camera);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);
    }

    public void zoomIn(double x1, double x2) {
        if (previousRadius < 0) return;

        double center = (x1 + x2) / 2.0;
        double initialRadius = camera.getRadius();
        double delta = (previousRadius - initialRadius) / frames;

        for (int i = 0; i < frames; i++) {
            camera.setRadius(camera.getRadius() + delta);
            camera.setM_X(center);
            renderer.drawImage(camera, world, subtitle, mode, 0);
            CameraAnimator.triggerOptionalCameraEffect(speed, rotationMode, this, camera);
        }

        previousRadius = -1; // Reset after restoring
        Window.invokeReferences(renderer, camera, world, subtitle, mode);
    }

    private double previousCenterX = 0;
    private double previousCenterY = 0;

    public void zoomOut(double x1, double y1, double x2, double y2) {
        double finalCenterX = (x1 + x2) / 2.0;
        double finalCenterY = (y1 + y2) / 2.0;

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

        // Use both dx and dy to compute radius (more balanced)
        double targetRadius = Math.max(1.5, Math.max(dx * 1.3, dy * 1.3));

        // Store previous camera state for zoom in
        previousRadius = camera.getRadius();
        previousCenterX = camera.getM_X();
        previousCenterY = camera.getM_Y();

        double startRadius = camera.getRadius();
        double startX = camera.getM_X();
        double startY = camera.getM_Y();

        for (int i = 1; i <= frames; i++) {
            double t = i / (double) frames;

            // Linear interpolation
            double currentX = lerp(startX, finalCenterX, t);
            double currentY = lerp(startY, finalCenterY, t);
            double currentRadius = lerp(startRadius, targetRadius, t);

            camera.setM_X(currentX);
            camera.setM_Y(currentY);
            camera.setRadius(currentRadius);

            renderer.drawImage(camera, world, subtitle, mode, 0);
            triggerOptionalCameraEffect(speed, rotationMode, this, camera);
        }

        // Ensure it ends at the exact center and radius
        camera.setM_X(finalCenterX);
        camera.setM_Y(finalCenterY);
        camera.setRadius(targetRadius);

        Window.invokeReferences(renderer, camera, world, subtitle, mode);
    }

    public void zoomIn(double x1, double y1, double x2, double y2) {
        if (previousRadius < 0) return;

        double finalRadius = previousRadius;
        double finalX = previousCenterX;
        double finalY = previousCenterY;

        double startRadius = camera.getRadius();
        double startX = camera.getM_X();
        double startY = camera.getM_Y();

        for (int i = 1; i <= frames; i++) {
            double t = i / (double) frames;

            double currentX = lerp(startX, finalX, t);
            double currentY = lerp(startY, finalY, t);
            double currentRadius = lerp(startRadius, finalRadius, t);

            camera.setM_X(currentX);
            camera.setM_Y(currentY);
            camera.setRadius(currentRadius);

            renderer.drawImage(camera, world, subtitle, mode, 0);
            triggerOptionalCameraEffect(speed, rotationMode, this, camera);
        }

        camera.setM_X(finalX);
        camera.setM_Y(finalY);
        camera.setRadius(finalRadius);

        previousRadius = -1;
        Window.invokeReferences(renderer, camera, world, subtitle, mode);
    }

    private double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }


    public static void triggerOptionalCameraEffect(double intensity, View rotationMode, CameraAnimator cameraAnimator, Utility.Camera camera) {
        switch (rotationMode) {
            case ROTATE_YAW -> cameraAnimator.smoothYawRotation((intensity));
            case ROTATE_PITCH -> cameraAnimator.smoothPitchRotation((intensity));
            case ROTATE_BOTH -> {
                cameraAnimator.smoothYawRotation((intensity));
                cameraAnimator.smoothPitchRotation((intensity));
            }
            case NONE -> { }
        }
    }


    public void smoothYawRotation(double deltaYaw) {
            camera.setYaw(camera.getYaw() + deltaYaw);
    }

    public void smoothPitchRotation(double deltaPitch) {
            camera.setPitch(camera.getPitch() + deltaPitch);
    }

    public void setFPS(int fps) {
        this.frames = fps;
    }

    public void setMode(Render mode) {
        this.mode = mode;
    }

    public void setCameraRotation(View rotationType) {
        this.rotationMode = rotationType;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
