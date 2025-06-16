package Collections;

import Animations.Animator.JMinHeapAnimator;
import Animations.Dynamo;
import Algorithms.MinHeap;
import java.util.*;

import Rendering.View;
import Animations.*;
import Rendering.*;
import Utility.*;

public class JMinHeap<T extends Comparable<T>> {

    private final ArrayList<T> heapList;
    private final JMinHeapAnimator<T> animator;

    private Render mode;
    private Encoder encoder;
    private Exit defaultExit;
    private Dynamo randomizer;
    private Entrance defaultEntrance;

    private MinHeap algo;
    private double scale;
    private String userOutput;
    private boolean built = false;
    private boolean userProvidedOutput = false;
    private boolean preferSharedEncoder = false;
    private final Set<String> explicitlySetProperties;

    public JMinHeap() {
        this.scale = 0.5;
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.heapList = new ArrayList<>();
        this.animator = new JMinHeapAnimator<>();
        this.explicitlySetProperties = new HashSet<>();
        this.defaultEntrance = Entrance.SLIDE_FROM_RIGHT;
        this.defaultExit = Exit.SLIDE_UP;
        this.randomizer = null;
        this.built = true;
    }

    public JMinHeap withAlgoVisualizer(MinHeap algo){
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
        if (this.isEmpty()) { throw new IllegalStateException(" Tree is empty "); }
        algo.run(this);
    }

    public JMinHeap withInsertAnimation(Entrance entrance) {
        this.defaultEntrance = entrance;
        explicitlySetProperties.add("insertAnimation");
        this.built = false;
        return this;
    }

    public JMinHeap withRemoveAnimation(Exit exit) {
        this.defaultExit = exit;
        explicitlySetProperties.add("removeAnimation");
        this.built = false;
        return this;
    }

    public JMinHeap withRandomizer(Dynamo randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JMinHeap withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        explicitlySetProperties.add("renderMode");
        this.built = false;
        return this;
    }

    public JMinHeap withQuality(Resolution quality) {
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

    public JMinHeap withOutput(String userOutput) {
        this.userOutput = userOutput;
        userProvidedOutput = true;
        this.built = false;
        return this;
    }

    public JMinHeap withSharedEncoder(boolean shared) {
        this.preferSharedEncoder = shared;
        this.built = false;
        return this;
    }

    public JMinHeap withMaterial(Texture material) {
        animator.setMaterial(material);
        explicitlySetProperties.add("material");
        this.built = false;
        return this;
    }

    public JMinHeap withBackground(Scenery bg) {
        String background = bg.toString();
        animator.setBackground(background);
        explicitlySetProperties.add("background");
        this.built = false;
        return this;
    }

    public JMinHeap withParticle(Effect particle) {
        animator.setParticle(particle);
        explicitlySetProperties.add("particle");
        this.built = false;
        return this;
    }

    public JMinHeap withStepsPerAnimation(Frames step) {
        int steps = step.getFrames();
        animator.setFPS(steps);
        explicitlySetProperties.add("steps");
        this.built = false;
        return this;
    }

    public JMinHeap withCameraRotations(View rotationType) {
        animator.setCameraRotation(rotationType);
        explicitlySetProperties.add("cameraRotation");
        return this;
    }

    public JMinHeap withAntiAliasing(Smooth antiAliasing) {
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

    public JMinHeap withCameraSpeed(Pace cs) {
        explicitlySetProperties.add("cameraSpeed");
        double speed = cs.getMultiplier();
        animator.setCameraSpeed(speed);
        this.built = false;
        return this;
    }

    public JMinHeap withBackgroundChangeOnEveryOperation(boolean change) {
        animator.setRandomizeBackgroundAsTrue();
        this.built = false;
        return this;
    }

    public JMinHeap withCameraFocus(Zoom focus) {
        double value = focus.getMultiplier();
        animator.setCameraFocus(value);
        this.built = false;
        return this;
    }

    public JMinHeap build() {

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
            throw new IllegalStateException("JHeap not built! Call .build() before use.");
        }
    }

    private void insertIntoHeap(T value) {
        heapList.add(value);
        heapifyUp(heapList.size() - 1);
    }

    private void deleteFromHeap() {
        int index = 0;
        if (index == -1) return;

        int lastIndex = heapList.size() - 1;
        Collections.swap(heapList, index, lastIndex);
        heapList.remove(lastIndex);

        if (index < heapList.size()) {
            heapifyDown(index);
            heapifyUp(index);
        }

        return;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heapList.get(index).compareTo(heapList.get(parent)) < 0) {
                Collections.swap(heapList, index, parent);
                index = parent;
            } else break;
        }
    }


    private void heapifyDown(int index) {
        int size = heapList.size();
        while (index < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && heapList.get(left).compareTo(heapList.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && heapList.get(right).compareTo(heapList.get(smallest)) < 0) {
                smallest = right;
            }

            if (smallest != index) {
                Collections.swap(heapList, index, smallest);
                index = smallest;
            } else break;
        }
    }


    public void add(T value) {
        Code.markCurrentLine();
        checkBuilt();
        Variable.update("add", value);

        insertIntoHeap(value);

        if (mode != Render.DISABLED) {
            animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultEntrance);
        }
    }


    public void add(T value, Entrance animation) {
        Code.markCurrentLine();
        checkBuilt();
        Variable.update("add", value);

        insertIntoHeap(value);

        if (mode != Render.DISABLED) {
            animator.runAddAnimation(value, animation);
        }
    }

    public T getPriority(){
        return remove();
    }

    public T remove() {
        Code.markCurrentLine();
        checkBuilt();
        T value =  heapList.get(0);
        Variable.update("remove", value);

        if (mode != Render.DISABLED) {
            animator.runRemoveAnimation();
        }

        deleteFromHeap();
        return value;
    }

    public int size() {
        return heapList.size();
    }

    public boolean isEmpty() {
        return heapList.isEmpty();
    }

    public void clear() {
        heapList.clear();
    }


}
