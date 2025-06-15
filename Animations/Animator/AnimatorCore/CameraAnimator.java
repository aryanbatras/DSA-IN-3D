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
