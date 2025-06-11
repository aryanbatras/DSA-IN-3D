package Animations.Animator;
import Animations.Animator.AnimatorCore.BoxAnimator;
import Animations.Animator.AnimatorCore.CameraAnimator;

import Animations.*;

import Rendering.View;
import Shapes.JBox;
import Shapes.Core.Shape;
import Utility.*;
import Utility.Renderer;

import Rendering.*;

import java.util.ArrayList;
import java.util.Random;

public class JStackAnimator {
    private final ArrayList<Shape> world;
    private final Renderer renderer;
    private final Utility.Camera camera;
    private Subtitle subtitle;

    private int positionAlongY;
    private int framesPerSecond;

    private final CameraAnimator cameraAnimator;
    private final BoxAnimator boxAnimator;

    private Render mode;
    private double scale;
    private Texture material;
    private String background;
    private Effect particle;
    private boolean randomBackground;
    private Random rand;

    private JBox top;

    public JStackAnimator() {
        this.scale = 0.5;
        rand = new Random();
        this.positionAlongY = 0;
        this.framesPerSecond = 20;
        this.particle = Effect.NONE;
        this.world = new ArrayList<>();
        this.material = Texture.METAL;
        this.camera = new Utility.Camera();
        this.background = "/Resources/lake.jpg";
        this.renderer = new Renderer(background);
        this.subtitle = new Subtitle("Stack");
        this.cameraAnimator = new CameraAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.boxAnimator = new BoxAnimator(renderer, camera, world, subtitle, framesPerSecond);
        this.randomBackground = false;
        world.add(setTopPointer());
    }

    private JBox setTopPointer() {
        this.top = new JBox(
                new Point(-7.5, +5, 5),
                2.5, 1.2, 0.15,
                new Color(1.0f, 0.82f, 0.0f),
                material, 0, null,
                particle
        );
        return top;
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
        this.top.material = material;
    }

    public void setParticle(Effect particle) {
        this.particle = particle;
        this.top.particleEffect = particle;
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
        Scenery randomBg = Scenery.values()[rand.nextInt(Scenery.values().length)];
        setBackground(randomBg.toString());
    }

    public void setCameraFocus(double focus) {
        this.camera.setRadius(camera.getRadius() - focus);
    }

    public void runAddAnimation(int value, Entrance animation) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }

        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        double finalX = 0;
        double finalY = positionAlongY + 5;

        positionAlongY += 5;
        cameraAnimator.slideAlongY(positionAlongY);
        
        // Move the top pointer up
        boxAnimator.slideUp(top, positionAlongY);

        // Create and add the new box
        JBox JBox = new JBox(
                new Point(finalX, finalY, 10),
                8.2, 5, 0.25,
                new Color(0.4f, 0.7f, 1.0f),
                material, 0, value,
                particle
        );
        world.add(JBox);
        
        subtitle.setMode("Pushing");
        subtitle.setValue(String.valueOf(value));
        
        // Animate the new box
        switch (animation) {
            case BOUNCE -> boxAnimator.bounceIn(JBox, finalY);
            case SLIDE_FROM_TOP -> boxAnimator.slideFromTop(JBox, finalY);
            case SLIDE_FROM_LEFT -> boxAnimator.slideFromLeft(JBox, finalX);
            case SLIDE_FROM_RIGHT -> boxAnimator.slideFromRight(JBox, finalX);
            case SCALE_POP -> boxAnimator.scalePop(JBox);
            case SHAKE -> boxAnimator.shake(JBox);
        }
    }

    public void runRemoveAnimation(int oldIndex, Exit animation) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        int index = oldIndex + 1;
        JBox JBox = (JBox) world.get(index);
        
        subtitle.setMode("Popping");
        subtitle.setValue(String.valueOf(JBox.val));
        
        // Animate the box removal
        switch (animation) {
            case FADE_UP -> boxAnimator.fadeOutAndUp(JBox, JBox.center.y + 5);
            case SLIDE_UP -> boxAnimator.slideUp(JBox, JBox.center.y + 5);
            case SCALE_DOWN -> boxAnimator.scaleDown(JBox);
            case SHAKE_AND_FADE -> boxAnimator.shakeAndFade(JBox);
            case SHRINK_AND_DROP -> boxAnimator.shrinkAndDrop(JBox);
        }
        
        // Remove the box from the world
        world.remove(JBox);
        
        // Move the camera down and update the top pointer
        positionAlongY -= 5;
        cameraAnimator.slideAlongY(positionAlongY);
        boxAnimator.slideDown(top, positionAlongY);
    }

    public void runHighlightAnimation(int oldIndex) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        int index = oldIndex + 1;

        JBox JBox = (JBox) world.get(index);
        subtitle.setMode("Getting");
        subtitle.setValue(String.valueOf(JBox.val));
        boxAnimator.highlight(JBox);
    }

    public void runHybridAnimation(int oldIndex, int value) {
        if(randomBackground){ setRandomBackground(); }

        if (mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.waitUntilNextStep();
            Window.setScale(scale);
        }
        Window.invokeReferences(renderer, camera, world, subtitle, mode);

        int index = oldIndex + 1;

        JBox JBox = (JBox) world.get(index);
        subtitle.setMode("Updating");
        subtitle.setValue(String.format("%d → %d", JBox.val, value));
        boxAnimator.updateValue(JBox, value);
        JBox.val = value; JBox.setDigitsFromNumber(value);
        boxAnimator.shakeSlow(JBox);
    }

}

