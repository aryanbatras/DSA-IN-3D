package Collections;

import Randomizer.JArrayListRandomizer;
import Randomizer.JArrayListRandomizer;
import Rendering.*;
import Rendering.Camera;
import Utility.*;

import Animations.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import Animator.JArrayListAnimator;

public class JArrayList {

    private final ArrayList<Integer> arr;
    private final JArrayListAnimator animator;

    private Render mode;
    private Encoder encoder;
    private JArrayListRandomizer randomizer;
    private JArrayListInsertAnimation defaultInsertAnimation;
    private JArrayListRemoveAnimation defaultRemoveAnimation;

    private double scale;
    private String userOutput;
    private boolean built = false;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JArrayList() {
        this.scale = 0.5;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.arr = new ArrayList<>();
        this.animator = new JArrayListAnimator();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultInsertAnimation = JArrayListInsertAnimation.SLIDE_FROM_RIGHT;
        this.defaultRemoveAnimation = JArrayListRemoveAnimation.SLIDE_UP;
        this.randomizer = null;
        this.built = true;
    }

    public JArrayList withInsertAnimation(JArrayListInsertAnimation insertAnimation) {
        this.defaultInsertAnimation = insertAnimation;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JArrayList withRemoveAnimation(JArrayListRemoveAnimation removeAnimation) {
        this.defaultRemoveAnimation = removeAnimation;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JArrayList withRandomizer(JArrayListRandomizer randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JArrayList withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JArrayList withQuality(Quality quality) {
        switch (quality) {
            case BEST -> scale = 1.0;
            case GOOD -> scale = 0.75;
            case BALANCE -> scale = 0.5;
            case FASTEST -> scale = 0.25;
        }
        animator.setScale(scale);
        explicitlySetProperties.add("quality");
        this.built = false;
        return this;
    }

    public JArrayList withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JArrayList withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JArrayList withMaterial(Material material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JArrayList withBackground(Background bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JArrayList withParticle(Particle particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JArrayList withStepsPerAnimation(Steps step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }


    public JArrayList withCameraRotations(Camera rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JArrayList withAntiAliasing(AntiAliasing antiAliasing) {
        double alias = 0;
        switch (antiAliasing) {
            case NONE -> alias = 1.0;
            case X2 -> alias = 2.0;
            case X4 -> alias = 4.0;
            case X8 -> alias = 8.0;
        }
        animator.setAntiAliasing(alias);
        this.built = false;
    return this;
    }

    public JArrayList withCameraSpeed(Speed cs){
        double speed = cs.getMultiplier( );
        animator.setCameraSpeed(speed);
        explicitlySetProperties.add("cameraSpeed");
        return this;
    }


    public JArrayList withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        return this;
    }


    public JArrayList build() {

        if (randomizer != null) {
            if (randomizer.shouldRandomizeInsertAnimation()  && !explicitlySetProperties.contains("insertAnimation")) {
                this.defaultInsertAnimation = JArrayListRandomizer.randomInsertAnimation();
            }
            if (randomizer.shouldRandomizeRemoveAnimation()  && !explicitlySetProperties.contains("removeAnimation")) {
                this.defaultRemoveAnimation = JArrayListRandomizer.randomRemoveAnimation();
            }
            if (randomizer.shouldRandomizeRenderMode() && !explicitlySetProperties.contains("renderMode")) {
                withRenderMode(JArrayListRandomizer.randomRenderMode());
            }
            if (randomizer.shouldRandomizeQuality() && !explicitlySetProperties.contains("quality")) {
                withQuality(JArrayListRandomizer.randomQuality());
            }
            if (randomizer.shouldRandomizeMaterial() && !explicitlySetProperties.contains("material")) {
                withMaterial(JArrayListRandomizer.randomMaterial());
            }
            if (randomizer.shouldRandomizeBackground() && !explicitlySetProperties.contains("background")) {
                withBackground(JArrayListRandomizer.randomBackground());
            }
            if (randomizer.shouldRandomizeParticle() && !explicitlySetProperties.contains("particle")) {
                withParticle(JArrayListRandomizer.randomParticle());
            }
            if (randomizer.shouldRandomizeSteps() && !explicitlySetProperties.contains("steps")) {
                withStepsPerAnimation(JArrayListRandomizer.randomSteps());
            }
            if (randomizer.shouldRandomizeCameraRotation() && !explicitlySetProperties.contains("cameraRotation")) {
                withCameraRotations(JArrayListRandomizer.randomCameraRotation());
            }
            if (randomizer.shouldRandomizeCameraSpeed() && !explicitlySetProperties.contains("cameraSpeed")) {
                withCameraSpeed(JArrayListRandomizer.randomCameraSpeed());
            }
        }

        if(mode == Render.VIDEO && userProvidedOutput == true){

            if(preferSharedEncoder){
                encoder = Encoder.getOrCreateNamedEncoder(userOutput, scale);
            } else {
                encoder = Encoder.initializeEncoder(userOutput, scale);
            }

            animator.setEncoder(encoder);
        }

        if (mode == Render.VIDEO && userProvidedOutput == false) {

            if(preferSharedEncoder){
                encoder = Encoder.getOrCreateSharedEncoder(scale);
            } else {
                encoder = Encoder.initializeEncoder(scale);
            }

            animator.setEncoder(encoder);
        }

        if (mode == Render.LIVE) {
            Window.initializeWindow();
        }

        if(mode == Render.STEP_WISE) {
            Window.initializeWindow();
        }

        if(mode == Render.STEP_WISE_INTERACTIVE){
            Window.initializeWindow();
            Window.setupInteractivity();
        }

        this.built = true;
        return this;
    }

    private void checkBuilt() {
        if (built == false) { throw new IllegalStateException("JArrayList not built! Call .build() before use."); }
    }

    public void add(int value) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("add", value);

        arr.add(value);
        if (mode != Render.DISABLED) { animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultInsertAnimation); }
    }

    public void add(int value, JArrayListInsertAnimation boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("add", value);

        arr.add(value);
        if(mode != Render.DISABLED){ animator.runAddAnimation(value, boxAnimation);}
    }

    public void remove(int index) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("remove", index, arr.get(index));

        arr.remove(index);
        if(mode != Render.DISABLED){ animator.runRemoveAnimation(index, randomizer != null ? randomizer.randomRemoveAnimation() : defaultRemoveAnimation);}
    }

    public void remove(int index, JArrayListRemoveAnimation boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("remove", index, arr.get(index));

        arr.remove(index);
        if(mode != Render.DISABLED){animator.runRemoveAnimation(index, boxAnimation);}
    }

    public Integer get(int index) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("get", index, arr.get(index));

        if(mode != Render.DISABLED){ animator.runHighlightAnimation(index); }
        return arr.get(index);
    }

    public void set(int index, int value) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("set", index, value);

        if(mode != Render.DISABLED){ animator.runHybridAnimation(index, value); }
        arr.set(index, value);
    }

    public int size() {
        Code.markCurrentLine(); checkBuilt();
        return arr.size();
    }
}




