package Animations;

import java.util.Random;

public final class JArrayListRandomAnimation {

    private static final Random rand = new Random();

    public static final JArrayListRandomAnimation INSTANCE = new JArrayListRandomAnimation();

    public static JArrayListInsertAnimation randomInsertAnimation() {
        JArrayListInsertAnimation[] values = JArrayListInsertAnimation.values();
        return values[rand.nextInt(values.length)];
    }

    public static JArrayListRemoveAnimation randomRemoveAnimation() {
        JArrayListRemoveAnimation[] values = JArrayListRemoveAnimation.values();
        return values[rand.nextInt(values.length)];
    }

}
