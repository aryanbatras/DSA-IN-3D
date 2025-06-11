package Collections;

import Animations.Dynamo;
import Animations.Animator.JLinkedListAnimator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import Rendering.View;
import Animations.*;
import Rendering.*;
import Utility.*;

public class JLinkedList {
    private final LinkedList<Integer> list;
    private final JLinkedListAnimator animator;

    private Render mode;
    private Encoder encoder;
    private Dynamo randomizer;
    private Entrance defaultEntrance;
    private Exit defaultExit;

    private double scale;
    private boolean built = false;
    private String userOutput;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JLinkedList() {
        this.scale = 0.5;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.list = new LinkedList<>();
        this.animator = new JLinkedListAnimator();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultExit = Exit.SLIDE_UP;
        this.randomizer = null;
        this.built = true;
    }

    public JLinkedList withInsertAnimation(Entrance entrance) {
        this.defaultEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JLinkedList withRemoveAnimation(Exit exit) {
        this.defaultExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JLinkedList withRandomizer(Dynamo randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JLinkedList withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JLinkedList withQuality(Resolution quality) {
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

    public JLinkedList withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JLinkedList withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JLinkedList withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JLinkedList withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JLinkedList withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JLinkedList withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }

    public JLinkedList withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JLinkedList withAntiAliasing(Smooth antiAliasing) {
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


    public JLinkedList withCameraSpeed(Pace cs) {
        explicitlySetProperties.add("cameraSpeed");
        double speed = cs.getMultiplier();
        animator.setCameraSpeed(speed);
        this.built = false;
        return this;
    }

    public JLinkedList withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        this.built = false;
        return this;
    }

    public JLinkedList withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }

    public JLinkedList build() {

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

        if (mode == Render.VIDEO && userProvidedOutput) {
            if (preferSharedEncoder) {
                encoder = Encoder.getOrCreateNamedEncoder(userOutput, scale);
            } else {
                encoder = Encoder.initializeEncoder(userOutput, scale);
            }
            animator.setEncoder(encoder);
        }

        if (mode == Render.VIDEO && !userProvidedOutput) {
            if (preferSharedEncoder) {
                encoder = Encoder.getOrCreateSharedEncoder(scale);
            } else {
                encoder = Encoder.initializeEncoder(scale);
            }
            animator.setEncoder(encoder);
        }

        if (mode == Render.LIVE || mode == Render.STEP_WISE || mode == Render.STEP_WISE_INTERACTIVE) {
            Window.initializeWindow();
            if (mode == Render.STEP_WISE_INTERACTIVE) {
                Window.setupInteractivity();
            }
        }

        this.built = true;
        return this;
    }

    private void checkBuilt() {
        if (!built) {
            throw new IllegalStateException("JLinkedList not built! Call .build() before use.");
        }
    }

    public void add(int value) {
        Code.markCurrentLine();
        checkBuilt();
        Variable.update("add", value);

        list.add(value);

        if (mode != Render.DISABLED) {
            animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultEntrance);
        }
    }

    public void add(int value, Entrance animation) {
        Code.markCurrentLine();
        checkBuilt();
        Variable.update("add", value);

        list.add(value);

        if (mode != Render.DISABLED) {
            animator.runAddAnimation(value, animation);
        }
    }

    public void remove(int index) {
        Code.markCurrentLine();
        checkBuilt();

        int value = list.get(index);
        list.remove(index);

        if (mode != Render.DISABLED) {
            animator.runRemoveAnimation(index, randomizer != null ? randomizer.randomRemoveAnimation() : defaultExit);
        }
        Variable.update("remove", index, value);
    }

    public void remove(int index, Exit animation) {
        Code.markCurrentLine();
        checkBuilt();

        int value = list.get(index);
        list.remove(index);

        if (mode != Render.DISABLED) {
            animator.runRemoveAnimation(index, animation);
        }
        Variable.update("remove", index, value);
    }

    public int get(int index) {
        Code.markCurrentLine();
        checkBuilt();

        int value = list.get(index);

        if (mode != Render.DISABLED) {
            animator.runHighlightAnimation(index);
        }
        Variable.update("get", index, value);
        return value;
    }

    public void set(int index, int value) {
        Code.markCurrentLine();
        checkBuilt();

        list.set(index, value);

        if (mode != Render.DISABLED) {
            animator.runHybridAnimation(index, value);
        }
        Variable.update("set", index, value);
    }

    public int size() {
        Code.markCurrentLine();
        checkBuilt();
        return list.size();
    }

    public boolean isEmpty() {
        Code.markCurrentLine();
        checkBuilt();
        return list.isEmpty();
    }

    @Override
    public String toString() {
        return list.toString();
    }

    public void clear() {
        for(int i = list.size() - 1; i >= 0; i--) {
            remove(i);
        }
    }
}
