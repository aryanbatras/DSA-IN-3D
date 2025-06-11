package Collections;

import Randomizer.JStackRandomizer;
import Randomizer.JStackRandomizer;
import Rendering.*;
import Rendering.Camera;
import Utility.*;

import Animations.*;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import Animator.JStackAnimator;

public class JStack {

//    private final Stack<Integer> arr;
    private int top;
    private final Stack<Integer> stack;
    private final JStackAnimator animator;

    private Render mode;
    private Encoder encoder;
    private JStackRandomizer randomizer;
    private JStackInsertAnimation defaultInsertAnimation;
    private JStackRemoveAnimation defaultRemoveAnimation;

    private double scale;
    private String userOutput;
    private boolean built = false;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JStack() {
        this.scale = 0.5;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.stack = new Stack<>();
        this.animator = new JStackAnimator();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultInsertAnimation = JStackInsertAnimation.SLIDE_FROM_RIGHT;
        this.defaultRemoveAnimation = JStackRemoveAnimation.SHRINK_AND_DROP;
        this.randomizer = null;
        this.built = true;
    }

    public JStack withInsertAnimation(JStackInsertAnimation insertAnimation) {
        this.defaultInsertAnimation = insertAnimation;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JStack withRemoveAnimation(JStackRemoveAnimation removeAnimation) {
        this.defaultRemoveAnimation = removeAnimation;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JStack withRandomizer(JStackRandomizer randomizer) {
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

    public JStack withQuality(Quality quality) {
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

    public JStack withMaterial(Material material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JStack withBackground(Background bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JStack withParticle(Particle particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JStack withStepsPerAnimation(Steps step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }


    public JStack withCameraRotations(Camera rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JStack withAntiAliasing(AntiAliasing antiAliasing) {
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

    public JStack withCameraSpeed(Speed cs){
        double speed = cs.getMultiplier( );
        animator.setCameraSpeed(speed);
        explicitlySetProperties.add("cameraSpeed");
        return this;
    }

    public JStack withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        return this;
    }

    public JStack withCameraFocus(Focus focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }


    public JStack build() {

        if (randomizer != null) {
            if (randomizer.shouldRandomizeInsertAnimation()  && !explicitlySetProperties.contains("insertAnimation")) {
                this.defaultInsertAnimation = JStackRandomizer.randomInsertAnimation();
            }
            if (randomizer.shouldRandomizeRemoveAnimation()  && !explicitlySetProperties.contains("removeAnimation")) {
                this.defaultRemoveAnimation = JStackRandomizer.randomRemoveAnimation();
            }
            if (randomizer.shouldRandomizeRenderMode() && !explicitlySetProperties.contains("renderMode")) {
                withRenderMode(JStackRandomizer.randomRenderMode());
            }
            if (randomizer.shouldRandomizeQuality() && !explicitlySetProperties.contains("quality")) {
                withQuality(JStackRandomizer.randomQuality());
            }
            if (randomizer.shouldRandomizeMaterial() && !explicitlySetProperties.contains("material")) {
                withMaterial(JStackRandomizer.randomMaterial());
            }
            if (randomizer.shouldRandomizeBackground() && !explicitlySetProperties.contains("background")) {
                withBackground(JStackRandomizer.randomBackground());
            }
            if (randomizer.shouldRandomizeParticle() && !explicitlySetProperties.contains("particle")) {
                withParticle(JStackRandomizer.randomParticle());
            }
            if (randomizer.shouldRandomizeSteps() && !explicitlySetProperties.contains("steps")) {
                withStepsPerAnimation(JStackRandomizer.randomSteps());
            }
            if (randomizer.shouldRandomizeCameraRotation() && !explicitlySetProperties.contains("cameraRotation")) {
                withCameraRotations(JStackRandomizer.randomCameraRotation());
            }
            if (randomizer.shouldRandomizeCameraSpeed() && !explicitlySetProperties.contains("cameraSpeed")) {
                withCameraSpeed(JStackRandomizer.randomCameraSpeed());
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

    public void push(int value) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("push", value);

        top++;
        stack.push(value);
        if (mode != Render.DISABLED) { animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultInsertAnimation); }
    }

    public void push(int value, JStackInsertAnimation boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("push", value);

        top++;
        stack.add(value);
        if(mode != Render.DISABLED){ animator.runAddAnimation(value, boxAnimation);}
    }

    public int pop() {
        Code.markCurrentLine(); checkBuilt();

        top--;
        int e = stack.pop();
        if(mode != Render.DISABLED){ animator.runRemoveAnimation(top, randomizer != null ? randomizer.randomRemoveAnimation() : defaultRemoveAnimation); }
        Variable.update("pop", e);
        return e;
    }

    public int pop(JStackRemoveAnimation boxAnimation) {
        Code.markCurrentLine(); checkBuilt();

        top--;
        int e = stack.pop();
        if(mode != Render.DISABLED){animator.runRemoveAnimation(top, boxAnimation);}
        Variable.update("pop", e);
        return e;
    }

    public Integer peek() {
        Code.markCurrentLine(); checkBuilt();
        int e = stack.peek();
        Variable.update("peek", e);

        if(mode != Render.DISABLED){ animator.runHighlightAnimation(top - 1); }
        return e;
    }

    public boolean isempty() {
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




