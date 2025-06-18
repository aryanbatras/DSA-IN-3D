package Animations.Animator;
import Animations.Animator.AnimatorCore.BoxAnimator;
import Animations.Animator.AnimatorCore.CameraAnimator;

import Animations.*;

import Rendering.View;
import Shapes.Core.Shape;
import Utility.Renderer;
import Utility.*;
import Shapes.JBox;

import Rendering.*;

import java.util.ArrayList;
import java.util.Random;

public class JQueueAnimator<T> {
    private final ArrayList<Shape> world;
    private final Utility.Camera camera;
    private final Renderer renderer;
    private Subtitle subtitle;

    private int framesPerSecond;
    private int positionAlongYRear;
    private int positionAlongYFront;
    private int queueWorldIndex;

    private final BoxAnimator boxAnimator;
    private final CameraAnimator cameraAnimator;

    private Random rand;
    private Render mode;
    private double scale;
    private Effect particle;
    private Texture material;
    private String background;
    private boolean randomBackground;

    private JBox rear;
    private JBox front;

    public JQueueAnimator() {
        this.scale = 0.5;
        rand = new Random();
        this.framesPerSecond = 20;
        this.particle = Effect.NONE;
        this.positionAlongYRear = 0;
        this.positionAlongYFront = +5;
        this.world = new ArrayList<>();
        this.material = Texture.METAL;
        this.camera = new Utility.Camera();
        this.background = "Resources/lake.jpg";
        this.renderer = new Renderer(background);
        this.subtitle = new Subtitle("Queue");
        this.cameraAnimator = new CameraAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.boxAnimator = new BoxAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.randomBackground = false;
        world.add(setRearPointer());
        world.add(setFrontPointer());
        this.queueWorldIndex = 0;
    }

    private JBox setFrontPointer() {
        this.front  = new JBox(
                new Point(-7.5, +5, 5),
                2.5, 1.2, 0.15,
                new Color(1.0f, 0.82f, 0.0f),
                material, 0, null,
                particle
        );
        return front;
    }

    private JBox setRearPointer() {
        this.rear  = new JBox(
                new Point(-7.5, +5, 5),
                2.5, 1.2, 0.15,
                new Color(1.0f, 0.82f, 0.0f),
                material, 0, null,
                particle
        );
        return rear;
    }

    public void setFPS(int fps) {
        this.framesPerSecond = fps;
        cameraAnimator.setFPS(fps);
        boxAnimator.setFPS(fps);
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
        renderer.setBackground(background);
    }

    public void setMaterial(Texture material) {
        this.material = material;
        this.rear .material = material;
    }

    public void setParticle(Effect particle) {
        this.particle = particle;
        this.rear .particleEffect = particle;
    }

    public void setAntiAliasing(double antiAliasing) {
        renderer.setAntialiasing(antiAliasing);
    }

    public void setCameraRotation(View rotationType) {
        cameraAnimator.setCameraRotation(rotationType);
        boxAnimator.setCameraRotation(cameraAnimator, rotationType);
    }

    public void setCameraSpeed(double i) {
        cameraAnimator.setSpeed(i);
        boxAnimator.setSpeed(i);
    }

    public void setRandomizeBackgroundAsTrue(){
        this.randomBackground = true;
    }

    private void setRandomBackground() {
        if(mode == Render.DISABLED) return;
        Scenery randomBg = Scenery.values()[rand.nextInt(Scenery.values().length)];
        setBackground(randomBg.toString());
    }

    public void setCameraFocus(double focus) {
        this.camera.setRadius(camera.getRadius() - focus);
    }

    public void runAddAnimation(T value, Entrance animation) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        double finalX = 0;
        double finalY = positionAlongYRear + 5;

        positionAlongYRear += 5;
        cameraAnimator.slideAlongY(positionAlongYRear);
        boxAnimator.slideUp(rear, positionAlongYRear);
        queueWorldIndex++;


        JBox JBox = new JBox(
                new Point(finalX, finalY, 10),
                8.2, 5, 0.25,
                new Color(0.4f, 0.7f, 1.0f),
                material, 0,  String.valueOf(value),
                particle
        );
        world.add(JBox);

        subtitle.setMode("Pushing");
        subtitle.setValue(String.valueOf(value));

        switch (animation) {
            case BOUNCE -> boxAnimator.bounceIn(JBox, finalY);
            case SLIDE_FROM_TOP -> boxAnimator.slideFromTop(JBox, finalY);
            case SLIDE_FROM_LEFT -> boxAnimator.slideFromLeft(JBox, finalX);
            case SLIDE_FROM_RIGHT -> boxAnimator.slideFromRight(JBox, finalX);
            case SCALE_POP -> boxAnimator.scalePop(JBox);
            case SHAKE -> boxAnimator.shake(JBox);
        }
        boxAnimator.slideUp(rear, positionAlongYRear + 5);
    }

    public void runRemoveAnimation(Exit animation) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        JBox JBox = (JBox) world.get(2);

        double finalX = 0;
        double finalY = positionAlongYFront + 5;
        double targetY = positionAlongYFront + 5;

        cameraAnimator.slideAlongY((int) targetY);
        boxAnimator.slideUp(front, positionAlongYFront);
        queueWorldIndex--;

        subtitle.setMode("Popping");
        subtitle.setValue(String.valueOf(JBox.val));

        switch (animation) {
            case FADE_UP -> boxAnimator.fadeOutAndUp(JBox, JBox.center.y + 5);
            case SLIDE_UP -> boxAnimator.slideUp(JBox, JBox.center.y + 5);
            case SCALE_DOWN -> boxAnimator.scaleDown(JBox);
            case SHAKE_AND_FADE -> boxAnimator.shakeAndFade(JBox);
            case SHRINK_AND_DROP -> boxAnimator.shrinkAndDrop(JBox);
        }

        boxAnimator.slideUp(front, positionAlongYFront + 5);
        positionAlongYFront = (int) targetY;
        world.remove(2);
    }

    public void runHighlightAnimation() {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        JBox JBox = (JBox) world.get(2);
        subtitle.setMode("Getting");
        subtitle.setValue(String.valueOf(JBox.val));
        boxAnimator.highlight(JBox);
    }

}

