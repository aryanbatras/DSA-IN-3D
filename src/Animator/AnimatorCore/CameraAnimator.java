package Animator.AnimatorCore;

import Shapes.Shape;
import Utility.Camera;
import Utility.Renderer;
import Rendering.Render;
import Utility.Subtitle;

import java.util.ArrayList;

public class CameraAnimator {

    private final Subtitle subtitle;
    private ArrayList<Shape> world;
    private Renderer renderer;
    private Camera camera;
    private Render mode;
    private int frames;

    public CameraAnimator(Renderer renderer, Camera camera, ArrayList<Shape> world, Subtitle subtitle, int framesPerSecond){
        this.frames = framesPerSecond;
        this.subtitle = subtitle;
        this.renderer = renderer;
        this.camera = camera;
        this.world = world;
    }

    public void slideAlongX(double cameraFinal){
        double cameraInitial = camera.getM_X();
        double delta = (cameraFinal - cameraInitial) / frames;
        for (int i = 0; i < frames; i++) {
            camera.setM_X(camera.getM_X() + delta);
            renderer.drawImage(camera, world, subtitle, mode);
        }
    }

    public void setMode(Render mode) {
        this.mode = mode;
    }
}
