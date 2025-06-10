package Animator.AnimatorCore;

import Shapes.Core.Shape;
import Utility.Camera;
import Utility.Renderer;
import Rendering.Render;
import Utility.Subtitle;
import Utility.Window;

import java.util.ArrayList;

public class CameraAnimator {

    private final Subtitle subtitle;
    private Rendering.Camera rotationMode;
    private ArrayList<Shape> world;
    private Renderer renderer;
    private Camera camera;
    private double speed;
    private Render mode;
    private int frames;

    public CameraAnimator(Renderer renderer, Camera camera, ArrayList<Shape> world, Subtitle subtitle, int framesPerSecond){
        this.rotationMode = Rendering.Camera.NONE;
        this.frames = framesPerSecond;
        this.subtitle = subtitle;
        this.renderer = renderer;
        this.camera = camera;
        this.world = world;
        this.speed = 0.0052;
    }

    public void slideAlongX(double cameraFinal){
        double cameraInitial = camera.getM_X();
        double delta = (cameraFinal - cameraInitial) / frames;
        for (int i = 0; i < frames; i++) {
            camera.setM_X(camera.getM_X() + delta);
            renderer.drawImage(camera, world, subtitle, mode);
            CameraAnimator.triggerOptionalCameraEffect(speed, rotationMode, this, camera);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);
    }


    public static void triggerOptionalCameraEffect(double intensity, Rendering.Camera rotationMode, CameraAnimator cameraAnimator, Utility.Camera camera) {
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

    public void setCameraRotation(Rendering.Camera rotationType) {
        this.rotationMode = rotationType;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
