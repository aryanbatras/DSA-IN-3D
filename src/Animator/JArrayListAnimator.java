package Animator;

import java.io.IOException;
import java.util.*;

import Animator.Core.BoxAnimator;
import Animator.Core.CameraAnimator;
import Shapes.Box;
import Shapes.Shape;
import Utility.*;

public class JArrayListAnimator {

    private Shape bvhWorld = null;
    private ArrayList<Shape> world;
    private Render renderer;
    private Camera camera;

    private int PositionAlongX;
    private int framesPerSecond;

    private CameraAnimator cameraAnimator;
    private BoxAnimator boxAnimator;

    public JArrayListAnimator() throws IOException {
        renderer = new Render("/Resources/lake.jpg");
        world = new ArrayList<Shape>( );
        camera = new Camera( );
        framesPerSecond = 25;
        PositionAlongX = 0;

        cameraAnimator = new CameraAnimator(renderer, camera, world, framesPerSecond);
        boxAnimator = new BoxAnimator(renderer, camera, world, framesPerSecond);
    }

    public void addAnimatorReverse(Box box, int value) {
        boxAnimator.slideFromLeft(box, PositionAlongX);
        cameraAnimator.slideAlongX(PositionAlongX);
        PositionAlongX++;
    }

    public void addAnimator(Box box, int value) {
        boxAnimator.slideFromLeft(box, PositionAlongX);
        cameraAnimator.slideAlongX(PositionAlongX);
        PositionAlongX--;
    }

    public void removeAnimator(Box box, int index) {
        cameraAnimator.slideAlongX((int) box.center.x);
        boxAnimator.slideUp(box, (int) (box.center.x+5));
        for (int i = index + 1; i < world.size(); i++) {
            Shape shape = world.get(i);
            if (shape instanceof Box) {
                boxAnimator.shiftLeft((Box) shape);
            }
        }

    }

    public Box addBox(int value) {
        Box box = new Box(new Point(999, 0, 0), 1, 1, 0.1, new Color(0.4f, 0.7f, 1.0f), Material.CHROME, 0, value);
        world.add(box);
        return box;
    }
    public void removeBox(int index){
        world.remove(index);
    }

    public Box getBox(int index){
        return (Box) world.get(index);
    }

}

