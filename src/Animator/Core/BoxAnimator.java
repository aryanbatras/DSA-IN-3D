package Animator.Core;

import Shapes.Box;
import Shapes.Shape;
import Utility.Camera;
import Utility.Render;

import java.util.ArrayList;

public class BoxAnimator {

    private ArrayList<Shape> world;
    private Render renderer;
    private Camera camera;
    private int frames;

    public BoxAnimator(Render renderer, Camera camera, ArrayList<Shape> world, int framesPerSecond) {
        this.frames = framesPerSecond;
        this.renderer = renderer;
        this.camera = camera;
        this.world = world;
    }
    public void slideFromLeft(Box box, int finalX){
        int initialX = finalX + 5;
        double delta = (double) (finalX - initialX) / frames;
        box.center.x = initialX;
        for (int i = 0; i < frames; i++) {
            box.center.x += delta;
            renderer.drawImage(camera, world);
        }
    }

    public void slideUp(Box box, int finalY) {
        int initialX = (int) box.center.x;
        double delta = (double)(finalY - initialX) / frames;

        for (int i = 0; i < frames; i++) {
            box.center.y += delta;
            renderer.drawImage(camera, world);
        }
    }

    public void shiftLeft(Box box) {
        double delta = 1.0 / frames; // Move 1 unit left over all frames
        for (int i = 0; i < frames; i++) {
            box.center.x += delta;
            renderer.drawImage(camera, world);
        }
    }

}
