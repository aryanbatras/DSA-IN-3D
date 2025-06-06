package Animator;
import Animator.Core.BoxAnimator;
import Animator.Core.CameraAnimator;

import Collections.Animations.*;

import Shapes.Box;
import Shapes.Shape;
import Utility.Camera;
import Utility.Color;
import Utility.Material;
import Utility.Point;
import Utility.Render;
import java.util.ArrayList;

public class JArrayListAnimator {
    private final ArrayList<Shape> world;
    private final Render renderer;
    private final Camera camera;

    private int positionAlongX;
    private final int framesPerSecond;

    private final CameraAnimator cameraAnimator;
    private final BoxAnimator boxAnimator;

    public JArrayListAnimator() {
        this.world = new ArrayList<>();
        this.camera = new Camera();
        this.framesPerSecond = 25;
        this.positionAlongX = 0;
        this.renderer = new Render("/Resources/lake.jpg");
        this.cameraAnimator = new CameraAnimator(renderer, camera, world, framesPerSecond);
        this.boxAnimator = new BoxAnimator(renderer, camera, world, framesPerSecond);
    }


    public void runAddAnimation(int value, JArrayListInsertAnimation animation) {
        double finalX = positionAlongX;
        double finalY = 0;

        Box box = new Box(
                new Point(finalX, finalY, 0),
                1, 1, 0.1,
                new Color(0.4f, 0.7f, 1.0f),
                Material.CHROME, 0, value
        );

        world.add(box);

        switch (animation) {
            case BOUNCE -> boxAnimator.bounceIn(box, finalY);
            case SLIDE_FROM_TOP -> boxAnimator.slideFromTop(box, finalY);
            case SLIDE_FROM_LEFT -> boxAnimator.slideFromLeft(box, finalX);
            case SLIDE_FROM_RIGHT -> boxAnimator.slideFromRight(box, finalX);
            case SCALE_POP -> boxAnimator.scalePop(box);
            case SHAKE -> boxAnimator.shake(box);
        }

        cameraAnimator.slideAlongX(positionAlongX);
        positionAlongX--;
    }


    public void runRemoveAnimation(int index, JArrayListRemoveAnimation animation) {
        Box box = (Box) world.get(index);
        double originalX = box.center.x;
        cameraAnimator.slideAlongX(originalX);

        switch (animation) {
            case FADE_UP -> boxAnimator.fadeOutAndUp(box, box.center.y + 5);
            case SLIDE_UP -> boxAnimator.slideUp(box, box.center.y + 5);
            case SCALE_DOWN -> boxAnimator.scaleDown(box);
            case SHAKE_AND_FADE -> boxAnimator.shakeAndFade(box);
            case SHRINK_AND_DROP -> boxAnimator.shrinkAndDrop(box);
        }

        world.remove(box);
        boxAnimator.shiftElementsLeft(index);
        positionAlongX++;
    }



}

