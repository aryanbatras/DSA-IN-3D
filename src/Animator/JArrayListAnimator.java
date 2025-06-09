package Animator;
import Animator.AnimatorCore.BoxAnimator;
import Animator.AnimatorCore.CameraAnimator;

import Animations.*;

import Shapes.Box;
import Shapes.Shape;
import Utility.*;
import Utility.Renderer;

import Rendering.*;

import java.util.ArrayList;

public class JArrayListAnimator {
    private final ArrayList<Shape> world;
    private final Renderer renderer;
    private final Camera camera;
    private Subtitle subtitle;

    private int positionAlongX;
    private int framesPerSecond;

    private final CameraAnimator cameraAnimator;
    private final BoxAnimator boxAnimator;

    private Render mode;
    private double scale;
    private Material material;
    private String background;

    public JArrayListAnimator() {
        this.scale = 0.5;
        this.positionAlongX = 0;
        this.framesPerSecond = 25;
        this.camera = new Camera();
        this.world = new ArrayList<>();
        this.material = Material.METAL;
        this.background = "/Resources/lake.jpg";
        this.renderer = new Renderer(background);
        this.subtitle = new Subtitle("ArrayList");
        this.cameraAnimator = new CameraAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.boxAnimator = new BoxAnimator(renderer, camera, world, subtitle, framesPerSecond);
    }

    public void setMode(Render mode) {
        this.mode = mode;
        cameraAnimator.setMode(mode);
        boxAnimator.setMode(mode);
    }

    public void setEncoder(Encoder encoder) {
        renderer.setEncoder(encoder);
    }

    public void setScale(double scale){
        this.scale = scale;
        renderer.setScale(background, scale);
    }

    public void setBackground(String background) {
        this.background = background;
        renderer.setScale(background, scale);
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void runAddAnimation(int value, JArrayListInsertAnimation animation) {

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        double finalX = positionAlongX;
        double finalY = 0;
        Box box = new Box(
                new Point(finalX, finalY, 0),
                1, 1, 0.1,
                new Color(0.4f, 0.7f, 1.0f),
                material, 0, value
        );
        world.add(box);
        subtitle.setMode("Inserting");
        subtitle.setValue(String.valueOf(value));
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

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        Box box = (Box) world.get(index);
        double originalX = box.center.x;
        subtitle.setMode("Removing");
        subtitle.setValue(String.valueOf(box.val));
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

    public void runHighlightAnimation(int index) {

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        Box box = (Box) world.get(index);
        subtitle.setMode("Getting");
        subtitle.setValue(String.valueOf(box.val));
        cameraAnimator.slideAlongX(box.center.x);
        boxAnimator.highlight(box);
    }

    public void runHybridAnimation(int index, int value) {


        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        Box box = (Box) world.get(index);
        subtitle.setMode("Updating");
        subtitle.setValue(String.format("%d → %d", box.val, value));
        cameraAnimator.slideAlongX(box.center.x);
        boxAnimator.updateValue(box, value);
        box.val = value; box.setDigitsFromNumber(value);
        boxAnimator.shakeSlow(box);
    }
}

