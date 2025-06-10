package Animator;

import Animator.AnimatorCore.BoxAnimator;
import Animator.AnimatorCore.CameraAnimator;

import Animations.*;

import Shapes.JBox;
import Rendering.Camera;

import Utility.*;
import Utility.Renderer;
import Shapes.Core.Shape;

import Rendering.*;

import java.util.Random;
import java.util.ArrayList;

public class JLinkedListAnimator {
    private final ArrayList<Shape> world;
    private final Utility.Camera camera;
    private final Renderer renderer;
    private Subtitle subtitle;

    private int positionAlongX;
    private int framesPerSecond;

    private final CameraAnimator cameraAnimator;
    private final BoxAnimator boxAnimator;

    private Random rand;
    private Render mode;
    private double scale;
    private Material material;
    private String background;
    private Particle particle;
    private boolean randomBackground;
    private double hidePointerFirstGlance;

    public JLinkedListAnimator() {
        this.scale = 0.5;
        this.positionAlongX = 0;
        this.framesPerSecond = 20;
        this.world = new ArrayList<>();
        this.material = Material.METAL;
        this.particle = Particle.NONE;
        this.camera = new Utility.Camera();
        this.background = "/Resources/lake.jpg";
        this.renderer = new Renderer(background);
        this.subtitle = new Subtitle("LinkedList");
        this.cameraAnimator = new CameraAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.boxAnimator = new BoxAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.randomBackground = false;
        hidePointerFirstGlance = 0.0;
        world.add(setGoldPointer());
        this.rand = new Random();
    }

    public JBox setGoldPointer() {
        JBox pointer = new JBox(
                new Point(0, 0, 0.1),
                0, 0.42, 0.1,
                new Color(1.0f, 0.82f, 0.0f),
                Material.METAL, 0.0f, null,
                particle
        );
        return pointer;
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

    public void setScale(double scale) {
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

    public void setCameraSpeed(double speed) {
        cameraAnimator.setSpeed(speed);
        boxAnimator.setSpeed(speed);
    }

    private void setRandomBackground() {
        Background randomBg = Background.values()[rand.nextInt(Background.values().length)];
        setBackground(randomBg.toString());
    }

    public void setRandomizeBackgroundAsTrue(){
        this.randomBackground = true;
    }

    public void setCameraFocus(double focus) {
        this.camera.setRadius(camera.getRadius() - focus);
    }

    public void runAddAnimation(int value, JLinkedListInsertAnimation animation) {

        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        double finalX = positionAlongX;
        double finalY = 0;

        JBox box = new JBox(
                new Point(finalX, finalY, 0),
                0.75, 0.82, 0.1,
                new Color(0.4f, 0.7f, 1.0f),
                material, 0, value,
                particle
        );

        world.add(box);

        JBox pointer = ( (JBox) world.get(0) );

        if(hidePointerFirstGlance > 0){
            pointer.setWidth(hidePointerFirstGlance);
            hidePointerFirstGlance = 0.0;
        }

        pointer.setWidth(pointer.getWidth() + 1);
        double mean = (double) Math.abs(positionAlongX) / 2.0f;
        pointer.center.x = - mean - 0.12;

        if(positionAlongX == 0){
            hidePointerFirstGlance = pointer.getWidth();
            pointer.setWidth(0);
        }

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

    public void runRemoveAnimation(int oldIndex, JLinkedListRemoveAnimation animation) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        int index = oldIndex + 1;

        JBox pointer = ( (JBox) world.get(0) );
        pointer.setWidth(pointer.getWidth() - 1);
        double mean = (double) Math.abs(positionAlongX) / 2.0f;
        pointer.center.x = - mean - 0.12 + 1;

        JBox nodeBox = (JBox) world.get(index);
        double originalX = nodeBox.center.x;
        subtitle.setMode("Removing");
        subtitle.setValue(String.valueOf(nodeBox.val));
        cameraAnimator.slideAlongX(originalX);

        switch (animation) {
            case FADE_UP -> boxAnimator.fadeOutAndUp(nodeBox, nodeBox.center.y + 5);
            case SLIDE_UP -> boxAnimator.slideUp(nodeBox, nodeBox.center.y + 5);
            case SCALE_DOWN -> boxAnimator.scaleDown(nodeBox);
            case SHAKE_AND_FADE -> boxAnimator.shakeAndFade(nodeBox);
            case SHRINK_AND_DROP -> boxAnimator.shrinkAndDrop(nodeBox);
        }

        world.remove(nodeBox);
        boxAnimator.shiftElementsLeft(index);
        positionAlongX++;
    }

    public void runHighlightAnimation(int olIndex) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        int index = olIndex + 1;

        JBox nodeBox = (JBox) world.get(index);
        subtitle.setMode("Getting");
        subtitle.setValue(String.valueOf(nodeBox.val));
        cameraAnimator.slideAlongX(nodeBox.center.x);
        boxAnimator.highlight(nodeBox);
    }

    public void runHybridAnimation(int oldIndex, int value) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        int index = oldIndex + 1;

        JBox nodeBox = (JBox) world.get(index);
        subtitle.setMode("Updating");
        subtitle.setValue(String.format("%d → %d", nodeBox.val, value));
        cameraAnimator.slideAlongX(nodeBox.center.x);
        boxAnimator.updateValue(nodeBox, value);
        nodeBox.val = value;
        nodeBox.setDigitsFromNumber(value);
        boxAnimator.shakeSlow(nodeBox);
    }
}

