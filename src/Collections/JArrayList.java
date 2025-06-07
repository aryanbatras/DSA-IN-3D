package Collections;

import Utility.Encoder;

import java.util.ArrayList;
import Collections.Animations.*;
import Animator.JArrayListAnimator;

public class JArrayList {

    private final ArrayList<Integer> arr;
    private final JArrayListAnimator animator;

    private final JArrayListRandomAnimation randomizer;
    private final JArrayListInsertAnimation defaultInsertAnimation;
    private final JArrayListRemoveAnimation defaultRemoveAnimation;


    //
    // CONSTRUCTORS
    //

    public JArrayList() {
        this(
                JArrayListInsertAnimation.SLIDE_FROM_RIGHT,
                JArrayListRemoveAnimation.SLIDE_UP
        );
    }

    public JArrayList(JArrayListInsertAnimation insertAnimation) {
        this(
                insertAnimation,
                JArrayListRemoveAnimation.SLIDE_UP
        );
    }

    public JArrayList(JArrayListRemoveAnimation removeAnimation) {
        this(
                JArrayListInsertAnimation.SLIDE_FROM_RIGHT,
                removeAnimation
        );
    }

    public JArrayList(
            JArrayListInsertAnimation insertAnimation,
            JArrayListRemoveAnimation removeAnimation
    ) {
        Encoder.initializeEncoder();
        this.arr = new ArrayList<>();
        this.animator = new JArrayListAnimator();
        this.defaultInsertAnimation = insertAnimation;
        this.defaultRemoveAnimation = removeAnimation;
        this.randomizer = null;
    }



//    //
//    // CONSTRUCTORS WITH RANDOMIZER
//    //

    public JArrayList(JArrayListRandomAnimation randomizer) {
        this(
                JArrayListInsertAnimation.SLIDE_FROM_RIGHT,
                JArrayListRemoveAnimation.SLIDE_UP,
                randomizer
        );
    }

    public JArrayList(JArrayListInsertAnimation insertAnimation, JArrayListRandomAnimation randomizer) {
        this(
                insertAnimation,
                JArrayListRemoveAnimation.SLIDE_UP,
                randomizer
        );
    }

    public JArrayList(JArrayListRemoveAnimation removeAnimation, JArrayListRandomAnimation randomizer) {
        this(
                JArrayListInsertAnimation.SLIDE_FROM_RIGHT,
                removeAnimation,
                randomizer
        );
    }

    public JArrayList(
            JArrayListInsertAnimation insertAnimation,
            JArrayListRemoveAnimation removeAnimation,
            JArrayListRandomAnimation randomizer
    ) {
        Encoder.initializeEncoder();
        this.arr = new ArrayList<>();
        this.animator = new JArrayListAnimator();
        this.defaultInsertAnimation = insertAnimation;
        this.defaultRemoveAnimation = removeAnimation;
        this.randomizer = randomizer;
    }





    //
    // METHODS ADD REMOVE
    //

    public void add(int value) {
        Code.markCurrentLine();
        arr.add(value);
        animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultInsertAnimation);
    }

    public void add(int value, JArrayListInsertAnimation boxAnimation) {
        Code.markCurrentLine();
        arr.add(value);
        animator.runAddAnimation(value, boxAnimation);
    }

    public void remove(int index) {
        Code.markCurrentLine();
        arr.remove(index);
        animator.runRemoveAnimation(index, randomizer != null ? randomizer.randomRemoveAnimation() : defaultRemoveAnimation);
    }

    public void remove(int index, JArrayListRemoveAnimation boxAnimation) {
        Code.markCurrentLine();
        arr.remove(index);
        animator.runRemoveAnimation(index, boxAnimation);
    }

    public Integer get(int index) {
        Code.markCurrentLine();
        animator.runHighlightAnimation(index);
        return arr.get(index);
    }

    public void set(int index, int value) {
        Code.markCurrentLine();
        animator.runHybridAnimation(index, value);
        arr.set(index, value);
    }

    public int size() {
        Code.markCurrentLine();
        return arr.size();
    }

}
