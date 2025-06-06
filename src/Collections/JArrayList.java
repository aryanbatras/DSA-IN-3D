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


    public void add(int value) {
        arr.add(value);
        animator.runAddAnimation(value, randomizer != null ? randomizer.randomInsertAnimation() : defaultInsertAnimation);
    }

    public void add(int value, JArrayListInsertAnimation boxAnimation) {
        arr.add(value);
        animator.runAddAnimation(value, boxAnimation);
    }





    public void remove(int index) {
        arr.remove(index);
        JArrayListRemoveAnimation animation;
        animator.runRemoveAnimation(index, randomizer != null ? randomizer.randomRemoveAnimation() : defaultRemoveAnimation);
    }

    public void remove(int index, JArrayListRemoveAnimation boxAnimation) {
        arr.remove(index);
        animator.runRemoveAnimation(index, boxAnimation);
    }




    public Integer get(int index) {
        return arr.get(index);
    }

    public int size() {
        return arr.size();
    }

}
