package Animator.Core;

import Shapes.Shape;
import Utility.Camera;
import Utility.Render;
import Utility.Screen;
import Utility.Subtitle;

import java.util.ArrayList;

public class CameraAnimator {

    private final Subtitle subtitle;
    private ArrayList<Shape> world;
    private Render renderer;
    private Camera camera;
    private int frames;

    public CameraAnimator(Render renderer, Camera camera, ArrayList<Shape> world, Subtitle subtitle, int framesPerSecond){
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
            renderer.drawImage(camera, world, subtitle);
        }
    }

}
