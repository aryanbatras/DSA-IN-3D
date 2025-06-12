package Collections;

import Algorithms.Array;
import Animations.Dynamo;
import Rendering.*;
import Rendering.View;
import Utility.*;

import Animations.*;

import java.util.*;

import Animations.Animator.JStackAnimator;

public class JStack<T extends Comparable<T>> {

    private int top;
    private final Stack<T> stack;
    private final JStackAnimator<T> animator;

    private Render mode;
    private Encoder encoder;
    private Dynamo randomizer;
    private Entrance defaultEntrance;
    private Exit defaultExit;

    private double scale;
    private String userOutput;
    private boolean built = false;
    private Algorithms.Stack algo;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JStack() {
        this.scale = 0.5;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.stack = new Stack<>();
        this.animator = new JStackAnimator<>();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultExit = Exit.SHRINK_AND_DROP;
        this.randomizer = null;
        this.built = true;
    }



    public JStack withAlgoVisualizer(Algorithms.Stack algo){
        this.algo = algo;
        /*
         you have to build functions to
         sort the internal array as per algo
         because what algo handles is external array
         */
        this.built = false;
        return this;
    }

    public void run() {
        if (algo == null) { throw new IllegalStateException(" No algo was given via .withAlgoVisualizer() "); }
        if (stack.isEmpty()) { throw new IllegalStateException(" Stack is empty "); }
        algo.run(this);
    }


    public JStack withInsertAnimation(Entrance entrance) {
        this.defaultEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JStack withRemoveAnimation(Exit exit) {
        this.defaultExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JStack withRandomizer(Dynamo randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JStack withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JStack withQuality(Resolution quality) {
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

    public JStack withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JStack withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JStack withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JStack withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JStack withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JStack withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }


    public JStack withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JStack withAntiAliasing(Smooth antiAliasing) {
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

    public JStack withCameraSpeed(Pace cs){
        double speed = cs.getMultiplier( );
        animator.setCameraSpeed(speed);
        explicitlySetProperties.add("cameraSpeed");
        return this;
    }

    public JStack withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        return this;
    }

    public JStack withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }


    public JStack build() {

        if (randomizer != null) {
            if (randomizer.shouldRandomizeInsertAnimation()  && !explicitlySetProperties.contains("insertAnimation")) {
                this.defaultEntrance = Dynamo.randomInsertAnimation();
            }
            if (randomizer.shouldRandomizeRemoveAnimation()  && !explicitlySetProperties.contains("removeAnimation")) {
                this.defaultExit = Dynamo.randomRemoveAnimation();
            }
            if (randomizer.shouldRandomizeRenderMode() && !explicitlySetProperties.contains("renderMode")) {
                withRenderMode(Dynamo.randomRenderMode());
            }
            if (randomizer.shouldRandomizeQuality() && !explicitlySetProperties.contains("quality")) {
                withQuality(Dynamo.randomQuality());
            }
            if (randomizer.shouldRandomizeMaterial() && !explicitlySetProperties.contains("material")) {
                withMaterial(Dynamo.randomMaterial());
            }
            if (randomizer.shouldRandomizeBackground() && !explicitlySetProperties.contains("background")) {
                withBackground(Dynamo.randomBackground());
            }
            if (randomizer.shouldRandomizeParticle() && !explicitlySetProperties.contains("particle")) {
                withParticle(Dynamo.randomParticle());
            }
            if (randomizer.shouldRandomizeSteps() && !explicitlySetProperties.contains("steps")) {
                withStepsPerAnimation(Dynamo.randomSteps());
            }
            if (randomizer.shouldRandomizeCameraRotation() && !explicitlySetProperties.contains("cameraRotation")) {
                withCameraRotations(Dynamo.randomCameraRotation());
            }
            if (randomizer.shouldRandomizeCameraSpeed() && !explicitlySetProperties.contains("cameraSpeed")) {
                withCameraSpeed(Dynamo.randomCameraSpeed());
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
        if (built == false) { throw new IllegalStateException("JStack not built! Call .build() before use."); }
    }

//  push pop peek isempty isfull  size

    public void push(T value) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("push", value);

        top++;
        stack.push(value);
        if (mode != Render.DISABLED) { animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultEntrance); }
    }

    public void push(T value, Entrance boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("push", value);

        top++;
        stack.add(value);
        if(mode != Render.DISABLED){ animator.runAddAnimation(value, boxAnimation);}
    }

    public T pop() {
        Code.markCurrentLine(); checkBuilt();

        top--;
        T e = stack.pop();
        if(mode != Render.DISABLED){ animator.runRemoveAnimation(top, randomizer != null ? randomizer.randomRemoveAnimation() : defaultExit); }
        Variable.update("pop", e);
        return e;
    }

    public T pop(Exit boxAnimation) {
        Code.markCurrentLine(); checkBuilt();

        top--;
        T e = stack.pop();
        if(mode != Render.DISABLED){animator.runRemoveAnimation(top, boxAnimation);}
        Variable.update("pop", e);
        return e;
    }

    public T peek() {
        Code.markCurrentLine(); checkBuilt();
        T e = stack.peek();
        Variable.update("peek", e);

        if(mode != Render.DISABLED){ animator.runHighlightAnimation(top - 1); }
        return e;
    }

    public boolean isEmpty() {
        Code.markCurrentLine(); checkBuilt();
        boolean e = stack.isEmpty();

        Variable.update("isempty", e);
        return e;
    }

    public int size() {
        Code.markCurrentLine(); checkBuilt();
        int e = stack.size();

        Variable.update("size", e);
        return e;
    }

}




