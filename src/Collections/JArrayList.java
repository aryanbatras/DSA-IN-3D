package Collections;

import Utility.*;
import Rendering.Render;
import Rendering.Quality;

import Animations.*;
import java.util.ArrayList;
import Animator.JArrayListAnimator;

public class JArrayList {

    private final ArrayList<Integer> arr;
    private final JArrayListAnimator animator;

    private Render mode;
    private Encoder encoder;
    private JArrayListRandomAnimation randomizer;
    private JArrayListInsertAnimation defaultInsertAnimation;
    private JArrayListRemoveAnimation defaultRemoveAnimation;

    private boolean built = false;
    private boolean userProvidedOutput = false;

    public JArrayList() {
        this.encoder = null;
        this.mode = Render.DISABLED;
        this.arr = new ArrayList<>();
        this.animator = new JArrayListAnimator();
        this.defaultInsertAnimation = JArrayListInsertAnimation.SLIDE_FROM_RIGHT;
        this.defaultRemoveAnimation = JArrayListRemoveAnimation.SLIDE_UP;
        this.randomizer = null;
        this.built = true;
    }

    public JArrayList withInsertAnimation(JArrayListInsertAnimation insertAnimation) {
        this.defaultInsertAnimation = insertAnimation;
        this.built = false;
        return this;
    }

    public JArrayList withRemoveAnimation(JArrayListRemoveAnimation removeAnimation) {
        this.defaultRemoveAnimation = removeAnimation;
        this.built = false;
        return this;
    }

    public JArrayList withRandomizer(JArrayListRandomAnimation randomizer) {
        this.randomizer = randomizer;
        this.built = false;
        return this;
    }

    public JArrayList withRenderMode(Render mode) {
        this.mode = mode;
        animator.setMode(mode);
        this.built = false;
        return this;
    }
//
//    public JArrayList withQuality(Quality quality) {
//        Screen.setQuality(quality);
//        return this;
//    }

    public JArrayList withOutput(String userOutput) {
        if (mode == Render.VIDEO) {
            encoder = Encoder.initializeEncoder(userOutput);
            animator.setEncoder(encoder);
            userProvidedOutput = true;
        }
        return this;
    }

    public JArrayList build() {

        if (mode == Render.VIDEO && userProvidedOutput == false) {
            encoder = Encoder.initializeEncoder();
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
        VariableTracker.update("add", value);

        arr.add(value);
        if (mode != Render.DISABLED) { animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultInsertAnimation); }
    }

    public void add(int value, JArrayListInsertAnimation boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        VariableTracker.update("add", value);

        arr.add(value);
        if(mode != Render.DISABLED){ animator.runAddAnimation(value, boxAnimation);}
    }

    public void remove(int index) {
        Code.markCurrentLine(); checkBuilt();
        VariableTracker.update("remove", index, arr.get(index));

        arr.remove(index);
        if(mode != Render.DISABLED){ animator.runRemoveAnimation(index, randomizer != null ? randomizer.randomRemoveAnimation() : defaultRemoveAnimation);}
    }

    public void remove(int index, JArrayListRemoveAnimation boxAnimation) {
        Code.markCurrentLine(); checkBuilt();
        VariableTracker.update("remove", index, arr.get(index));

        arr.remove(index);
        if(mode != Render.DISABLED){animator.runRemoveAnimation(index, boxAnimation);}
    }

    public Integer get(int index) {
        Code.markCurrentLine(); checkBuilt();
        VariableTracker.update("get", index, arr.get(index));

        if(mode != Render.DISABLED){ animator.runHighlightAnimation(index); }
        return arr.get(index);
    }

    public void set(int index, int value) {
        Code.markCurrentLine(); checkBuilt();
        VariableTracker.update("set", index, value);

        if(mode != Render.DISABLED){ animator.runHybridAnimation(index, value); }
        arr.set(index, value);
    }

    public int size() {
        Code.markCurrentLine(); checkBuilt();
        return arr.size();
    }

}
