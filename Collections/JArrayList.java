package Collections;

import Algorithms.Array;
import Animations.Dynamo;
import Rendering.*;
import Rendering.View;
import Utility.*;

import Animations.*;

import java.util.*;

import Animations.Animator.JArrayListAnimator;

public class JArrayList<T extends Comparable<T>> {

    private final ArrayList<T> arr;
    private final JArrayListAnimator<T> animator;

    private Render mode;
    private Encoder encoder;
    private Dynamo randomizer;
    private Entrance defaultEntrance;
    private Exit defaultExit;

    private Array algo;
    private double scale;
    private String userOutput;
    private boolean built = false;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JArrayList() {
        this.scale = 0.25;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.arr = new ArrayList<>();
        this.animator = new JArrayListAnimator<>();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultExit = Exit.SLIDE_UP;
        animator.setScale(0.25);
        animator.setFPS(1);
        this.randomizer = null;
        this.built = true;
    }

    public JArrayList withAlgoVisualizer(Array algo){
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
        if (arr.isEmpty()) { throw new IllegalStateException(" Array is empty "); }
        algo.run(this);
    }


    public JArrayList withInsertAnimation(Entrance entrance) {
        this.defaultEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JArrayList withRemoveAnimation(Exit exit) {
        this.defaultExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JArrayList withRandomizer(Dynamo randomizer) {
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

    public JArrayList withQuality(Resolution quality) {
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

    public JArrayList withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JArrayList withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JArrayList withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JArrayList withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }


    public JArrayList withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JArrayList withAntiAliasing(Smooth antiAliasing) {
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

    public JArrayList withCameraSpeed(Pace cs){
        double speed = cs.getMultiplier( );
        animator.setCameraSpeed(speed);
        explicitlySetProperties.add("cameraSpeed");
        return this;
    }


    public JArrayList withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        return this;
    }

    public JArrayList withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }


    public JArrayList build() {

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
        if (built == false) { throw new IllegalStateException("JArrayList not built! Call .build() before use."); }
    }

    public boolean isGreaterOrEqualTo(int i, int j) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("isGreater", "[" + i + "]", "[" + j + "]");

        T a = arr.get(i);
        T b = arr.get(j);

         animator.runComparisonAnimation(i, j);
        boolean result = a.compareTo(b) >= 0;
        Code.setConditionResult("→ " + result);
        return result;
    }

    public boolean isSmallerOrEqualTo(int i, int j) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("isGreater", "[" + i + "]", "[" + j + "]");

        T a = arr.get(i);
        T b = arr.get(j);

        animator.runComparisonAnimation(i, j);
        boolean result = a.compareTo(b) <= 0;
        Code.setConditionResult("→ " + result);
        return result;
    }

    public boolean isGreater(int i, int j) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("isGreater", "[" + i + "]", "[" + j + "]");

        T a = arr.get(i);
        T b = arr.get(j);

         animator.runComparisonAnimation(i, j);
        boolean result = a.compareTo(b) > 0;
        Code.setConditionResult("→ " + result);
        return result;
    }

    public boolean isSmaller(int i, int j) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("isSmaller", "[" + i + "]", "[" + j + "]");

        T a = arr.get(i);
        T b = arr.get(j);

        animator.runComparisonAnimation(i, j);
        boolean result = a.compareTo(b) < 0;
        Code.setConditionResult("→ " + result);
        return result;
    }

    public boolean isEqual(int i, int j) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("isEqual", "[" + i + "]", "[" + j + "]");

        T a = arr.get(i);
        T b = arr.get(j);

         animator.runComparisonAnimation(i, j);
        boolean result = a.compareTo(b) == 0;
        Code.setConditionResult("→ " + result);
        return result;
    }

    public void add(T value) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("add", value);

        arr.add(value);
         animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultEntrance);
    }

    public void add(T value, Entrance boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("add", value);

        arr.add(value);
        animator.runAddAnimation(value, boxAnimation);
    }

    public T remove(int index) {
        Code.markCurrentLine(); checkBuilt();
        T value = arr.get(index);
        Variable.update("remove", index, value);

        arr.remove(index);
         animator.runRemoveAnimation(index, randomizer != null ? randomizer.randomRemoveAnimation() : defaultExit);
        return value;
    }

    public T remove(int index, Exit boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        T value = arr.get(index);
        Variable.update("remove", index, value);

        arr.remove(index);
        animator.runRemoveAnimation(index, boxAnimation);
        return value;
    }

    public T get(int index) {
        Code.markCurrentLine(); checkBuilt();
        T value = arr.get(index);
        Variable.update("get", index, value);

         animator.runHighlightAnimation(index);
        return value;
    }

    public void set(int index, T value) {
        Code.markCurrentLine(); checkBuilt();
        Variable.update("set", index, value);

         animator.runHybridAnimation(index, value);
        arr.set(index, value);
    }

    public int size() {
        Code.markCurrentLine(); checkBuilt();
        return arr.size();
    }

    public T getRandomElement() {
        Code.markCurrentLine(); checkBuilt();
        if(size() == 0) throw new NoSuchElementException("List is Empty");
        return arr.get(new Random().nextInt(arr.size() - 1));
    }

    public boolean checkSorted() {
        Code.markCurrentLine(); checkBuilt();
        if (size() <= 1) return true;
        boolean ascending = arr.get(0).compareTo(arr.get(1)) <= 0;
        for (int i = 1; i < size() - 1; i++) {
            int cmp = arr.get(i).compareTo(arr.get(i + 1));
            if ((ascending && cmp > 0) || (!ascending && cmp < 0)) {
                throw new IllegalStateException("List is not sorted");
            }
        }
        return ascending;
    }

    public void checkInteger() {
        Code.markCurrentLine(); checkBuilt();
        if (size() == 0) return;
        if (!(arr.get(0) instanceof Integer)) {
            throw new IllegalStateException("List must contain only Integer elements");
        }
    }
}




