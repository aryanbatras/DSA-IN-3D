package Animator;
import Animator.AnimatorCore.BoxAnimator;
import Animator.AnimatorCore.CameraAnimator;

import Animations.*;

import Rendering.Camera;
import Shapes.JBox;
import Shapes.Core.Shape;
import Utility.*;
import Utility.Renderer;

import Rendering.*;

import java.util.ArrayList;
import java.util.Random;

public class JArrayListAnimator {
    private final ArrayList<Shape> world;
    private final Renderer renderer;
    private final Utility.Camera camera;
    private Subtitle subtitle;

    private int positionAlongX;
    private int framesPerSecond;

    private final CameraAnimator cameraAnimator;
    private final BoxAnimator boxAnimator;

    private Render mode;
    private double scale;
    private Material material;
    private String background;
    private Particle particle;
    private boolean randomBackground;
    private Random rand;

    public JArrayListAnimator() {
        this.scale = 0.5;
        this.positionAlongX = 0;
        this.framesPerSecond = 20;
        this.camera = new Utility.Camera();
        this.world = new ArrayList<>();
        this.material = Material.METAL;
        this.particle = Particle.NONE;
        this.background = "/Resources/lake.jpg";
        this.renderer = new Renderer(background);
        this.subtitle = new Subtitle("ArrayList");
        this.cameraAnimator = new CameraAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.boxAnimator = new BoxAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.randomBackground = false;
        rand = new Random();
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

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public void setAntiAliasing(double antiAliasing) {
        renderer.setAntialiasing(antiAliasing);
    }

    public void setCameraRotation(Camera rotationType) {
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
        Background randomBg = Background.values()[rand.nextInt(Background.values().length)];
        setBackground(randomBg.toString());
    }

    public void setCameraFocus(double focus) {
        this.camera.setRadius(camera.getRadius() - focus);
    }

    public void runAddAnimation(int value, JArrayListInsertAnimation animation) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        double finalX = positionAlongX;
        double finalY = 0;
        JBox JBox = new JBox(
                new Point(finalX, finalY, 0),
                1, 1, 0.1,
                new Color(0.4f, 0.7f, 1.0f),
                material, 0, value,
                particle
        );
        world.add(JBox);
        subtitle.setMode("Inserting");
        subtitle.setValue(String.valueOf(value));
        switch (animation) {
            case BOUNCE -> boxAnimator.bounceIn(JBox, finalY);
            case SLIDE_FROM_TOP -> boxAnimator.slideFromTop(JBox, finalY);
            case SLIDE_FROM_LEFT -> boxAnimator.slideFromLeft(JBox, finalX);
            case SLIDE_FROM_RIGHT -> boxAnimator.slideFromRight(JBox, finalX);
            case SCALE_POP -> boxAnimator.scalePop(JBox);
            case SHAKE -> boxAnimator.shake(JBox);
        }
        cameraAnimator.slideAlongX(positionAlongX);
        positionAlongX--;
    }

    public void runRemoveAnimation(int index, JArrayListRemoveAnimation animation) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        JBox JBox = (JBox) world.get(index);
        double originalX = JBox.center.x;
        subtitle.setMode("Removing");
        subtitle.setValue(String.valueOf(JBox.val));
        cameraAnimator.slideAlongX(originalX);
        switch (animation) {
            case FADE_UP -> boxAnimator.fadeOutAndUp(JBox, JBox.center.y + 5);
            case SLIDE_UP -> boxAnimator.slideUp(JBox, JBox.center.y + 5);
            case SCALE_DOWN -> boxAnimator.scaleDown(JBox);
            case SHAKE_AND_FADE -> boxAnimator.shakeAndFade(JBox);
            case SHRINK_AND_DROP -> boxAnimator.shrinkAndDrop(JBox);
        }
        world.remove(JBox);
        boxAnimator.shiftElementsLeft(index);
        positionAlongX++;
    }

    public void runHighlightAnimation(int index) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        JBox JBox = (JBox) world.get(index);
        subtitle.setMode("Getting");
        subtitle.setValue(String.valueOf(JBox.val));
        cameraAnimator.slideAlongX(JBox.center.x);
        boxAnimator.highlight(JBox);
    }

    public void runHybridAnimation(int index, int value) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        JBox JBox = (JBox) world.get(index);
        subtitle.setMode("Updating");
        subtitle.setValue(String.format("%d → %d", JBox.val, value));
        cameraAnimator.slideAlongX(JBox.center.x);
        boxAnimator.updateValue(JBox, value);
        JBox.val = value; JBox.setDigitsFromNumber(value);
        boxAnimator.shakeSlow(JBox);
    }

}

