package Animator;

import java.util.*;

import Animator.Core.BoxAnimator;
import Animator.Core.CameraAnimator;
import Shapes.Box;
import Shapes.Shape;
import Utility.*;

public class JArrayListAnimator {

    private ArrayList<Shape> world;
    private Render renderer;
    private Camera camera;

    private int PositionAlongX;
    private int framesPerSecond;

    private CameraAnimator cameraAnimator;
    private BoxAnimator boxAnimator;

    public JArrayListAnimator() {
        world = new ArrayList<Shape>( );
        renderer = new Render( );
        camera = new Camera( );
        PositionAlongX = 0;
        framesPerSecond = 30;

        cameraAnimator = new CameraAnimator(renderer, camera, world, framesPerSecond);
        boxAnimator = new BoxAnimator(renderer, camera, world, framesPerSecond);
    }

    public void addAnimatorReverse(Box box, int value) {
        cameraAnimator.slideAlongX(PositionAlongX);
        boxAnimator.slideFromLeft(box, PositionAlongX);
        PositionAlongX++;
    }

    public void addAnimator(Box box, int value) {
        cameraAnimator.slideAlongX(PositionAlongX);
        boxAnimator.slideFromLeft(box, PositionAlongX);
        PositionAlongX--;
    }

    public void removeAnimator(Box box, int index) {
        // Step 1: Slide the box out to the right
        int relativeX = (int) box.center.x;
        cameraAnimator.slideAlongX(relativeX);

        boxAnimator.slideUp(box, (int) (box.center.x + 5));

        // Step 2: Shift boxes to the right of the removed index one step to the left
        for (int i = index + 1; i < world.size(); i++) {
            Shape shape = world.get(i);
            if (shape instanceof Box) {
                boxAnimator.shiftLeft((Box) shape);
            }
        }

        // Step 3: Remove the box from the world
        world.remove(index);

        // Update position tracker

    }

    public Box getBox(int index){
        return (Box) world.get(index);
    }

    public Box setBox(int value) {
        Box box = new Box(new Point(999, 0, 0), 1, 1, 0.1, new Color(0.4f, 0.7f, 1.0f), Material.CHROME, 0, value);
        world.add(box);
        return box;
    }

}

