package Collections;

import Animations.Dynamo;
import Rendering.*;
import Rendering.View;
import Utility.*;

import Animations.*;

import Animations.Animator.JQueueAnimator;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;


public class JQueue<T extends Comparable<T>> {

    private int front;
    private int rear;
    private final Queue<T> queue;
    private final JQueueAnimator<T> animator;

    private Render mode;
    private Encoder encoder;
    private Dynamo randomizer;
    private Entrance defaultEntrance;
    private Exit defaultExit;

    private double scale;
    private String userOutput;
    private Algorithms.Queue algo;
    private boolean built = false;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JQueue() {
        this.scale = 0.5;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.queue = new ArrayDeque<>();
        this.animator = new JQueueAnimator<>();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultExit = Exit.SHRINK_AND_DROP;
        this.randomizer = null;
        this.built = true;
        front = 0;
        rear = 0;
    }

    public JQueue withAlgoVisualizer(Algorithms.Queue algo){
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
        if (queue.isEmpty()) { throw new IllegalStateException(" Queue is empty "); }
        algo.run(this);
    }


    public JQueue withInsertAnimation(Entrance entrance) {
        this.defaultEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JQueue withRemoveAnimation(Exit exit) {
        this.defaultExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JQueue withRandomizer(Dynamo randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JQueue withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JQueue withQuality(Resolution quality) {
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

    public JQueue withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JQueue withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JQueue withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JQueue withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JQueue withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JQueue withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }


    public JQueue withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JQueue withAntiAliasing(Smooth antiAliasing) {
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

    public JQueue withCameraSpeed(Pace cs){
        double speed = cs.getMultiplier( );
        animator.setCameraSpeed(speed);
        explicitlySetProperties.add("cameraSpeed");
        return this;
    }

    public JQueue withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        return this;
    }

    public JQueue withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }


    public JQueue build() {

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
        if (built == false) { throw new IllegalStateException("JQueue not built! Call .build() before use."); }
    }

// offer pool peek

    public void add(T value){ offer(value); }
    public void add(T value, Entrance boxAnimation){ offer(value, boxAnimation); }

    public void offer(T value) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("offer", value);

        rear++;
        queue.offer(value);
        if (mode != Render.DISABLED) { animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultEntrance); }
    }

    public void offer(T value, Entrance boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("offer", value);

        rear++;
        queue.offer(value);
        if(mode != Render.DISABLED){ animator.runAddAnimation(value, boxAnimation);}
    }


    public T poll() {
        Code.markCurrentLine(); checkBuilt();

        T e = queue.poll();
        if(mode != Render.DISABLED){ animator.runRemoveAnimation(randomizer != null ? randomizer.randomRemoveAnimation() : defaultExit); }
        Variable.update("poll", e);
        front++;
        return e;
    }

    public T poll(Exit boxAnimation) {
        Code.markCurrentLine(); checkBuilt();

        T e = queue.poll();
        if(mode != Render.DISABLED){animator.runRemoveAnimation(boxAnimation);}
        Variable.update("pop", e);
        front++;
        return e;
    }

    public T peek() {
        Code.markCurrentLine(); checkBuilt();
        T e = queue.peek();
        Variable.update("peek", e);

        if(mode != Render.DISABLED){ animator.runHighlightAnimation(); }
        return e;
    }

    public boolean isEmpty() {
        Code.markCurrentLine(); checkBuilt();
        boolean e = queue.isEmpty();

        Variable.update("isempty", e);
        return e;
    }

    public int size() {
        Code.markCurrentLine(); checkBuilt();
        int e = queue.size();

        Variable.update("size", e);
        return e;
    }
}




