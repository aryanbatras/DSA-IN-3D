package Randomizer;

import Animations.JArrayListInsertAnimation;
import Animations.JArrayListRemoveAnimation;
import Rendering.*;

import java.util.Random;
import java.util.function.Supplier;

public final class JArrayListRandomizer {
    private static final Random rand = new Random();
    private boolean randomizeInsertAnimation = false;
    private boolean randomizeRemoveAnimation = false;
    private boolean randomizeRenderMode = false;
    private boolean randomizeQuality = false;
    private boolean randomizeMaterial = false;
    private boolean randomizeBackground = false;
    private boolean randomizeParticle = false;
    private boolean randomizeSteps = false;
    private boolean randomizeCameraRotation = false;
    private boolean randomizeAntiAliasing = false;
    private boolean randomizeCameraSpeed = false;

    public static final JArrayListRandomizer INSTANCE = new JArrayListRandomizer();

    private JArrayListRandomizer() {}

    public static JArrayListInsertAnimation randomInsertAnimation() {
        JArrayListInsertAnimation[] values = JArrayListInsertAnimation.values();
        return values[rand.nextInt(values.length)];
    }

    public static JArrayListRemoveAnimation randomRemoveAnimation() {
        JArrayListRemoveAnimation[] values = JArrayListRemoveAnimation.values();
        return values[rand.nextInt(values.length)];
    }

    public static Render randomRenderMode() {
        Render[] values = Render.values();
        return values[rand.nextInt(values.length)];
    }

    public static Quality randomQuality() {
        Quality[] values = Quality.values();
        return values[rand.nextInt(values.length)];
    }

    public static Material randomMaterial() {
        Material[] values = Material.values();
        return values[rand.nextInt(values.length)];
    }

    public static Background randomBackground() {
        Background[] values = Background.values();
        return values[rand.nextInt(values.length)];
    }

    public static Particle randomParticle() {
        Particle[] values = Particle.values();
        return values[rand.nextInt(values.length)];
    }

    public static Steps randomSteps() {
        Steps[] values = Steps.values();
        return values[rand.nextInt(values.length)];
    }

    public static Camera randomCameraRotation() {
        Camera[] values = Camera.values();
        return values[rand.nextInt(values.length)];
    }

    public static AntiAliasing randomAntiAliasing() {
        AntiAliasing[] values = AntiAliasing.values();
        return values[rand.nextInt(values.length)];
    }

    public static Speed randomCameraSpeed() {
        Speed[] values = Speed.values();
        return values[rand.nextInt(values.length)];
    }

    public JArrayListRandomizer withInsertAnimation() {
        this.randomizeInsertAnimation = true;
        return this;
    }

    public JArrayListRandomizer withRemoveAnimation() {
        this.randomizeRemoveAnimation = true;
        return this;
    }

    public JArrayListRandomizer withRenderMode() {
        this.randomizeRenderMode = true;
        return this;
    }

    public JArrayListRandomizer withQuality() {
        this.randomizeQuality = true;
        return this;
    }

    public JArrayListRandomizer withMaterial() {
        this.randomizeMaterial = true;
        return this;
    }

    public JArrayListRandomizer withBackground() {
        this.randomizeBackground = true;
        return this;
    }

    public JArrayListRandomizer withParticle() {
        this.randomizeParticle = true;
        return this;
    }

    public JArrayListRandomizer withSteps() {
        this.randomizeSteps = true;
        return this;
    }

    public JArrayListRandomizer withCameraRotation() {
        this.randomizeCameraRotation = true;
        return this;
    }

    public JArrayListRandomizer withAntiAliasing() {
        this.randomizeAntiAliasing = true;
        return this;
    }

    public JArrayListRandomizer withCameraSpeed() {
        this.randomizeCameraSpeed = true;
        return this;
    }

    public JArrayListRandomizer withCrazyMode() {
        this.randomizeInsertAnimation = true;
        this.randomizeRemoveAnimation = true;
        this.randomizeRenderMode = true;
        this.randomizeQuality = true;
        this.randomizeMaterial = true;
        this.randomizeBackground = true;
        this.randomizeParticle = true;
        this.randomizeSteps = true;
        this.randomizeCameraRotation = true;
        this.randomizeAntiAliasing = true;
        this.randomizeCameraSpeed = true;
        return this;
    }

    public JArrayListRandomizer withoutInsertAnimation() {
        this.randomizeInsertAnimation = false;
        return this;
    }

    public JArrayListRandomizer withoutRemoveAnimation() {
        this.randomizeRemoveAnimation = false;
        return this;
    }

    public JArrayListRandomizer withoutRenderMode() {
        this.randomizeRenderMode = false;
        return this;
    }

    public JArrayListRandomizer withoutQuality() {
        this.randomizeQuality = false;
        return this;
    }

    public JArrayListRandomizer withoutMaterial() {
        this.randomizeMaterial = false;
        return this;
    }

    public JArrayListRandomizer withoutBackground() {
        this.randomizeBackground = false;
        return this;
    }

    public JArrayListRandomizer withoutParticle() {
        this.randomizeParticle = false;
        return this;
    }

    public JArrayListRandomizer withoutSteps() {
        this.randomizeSteps = false;
        return this;
    }

    public JArrayListRandomizer withoutCameraRotation() {
        this.randomizeCameraRotation = false;
        return this;
    }

    public JArrayListRandomizer withoutAntiAliasing() {
        this.randomizeAntiAliasing = false;
        return this;
    }

    public JArrayListRandomizer withoutCameraSpeed() {
        this.randomizeCameraSpeed = false;
        return this;
    }

    public <T> T getRandomValue(boolean shouldRandomize, Supplier<T> randomSupplier, T currentValue) {
        return shouldRandomize ? randomSupplier.get() : currentValue;
    }

    public boolean shouldRandomizeInsertAnimation() { return randomizeInsertAnimation; }
    public boolean shouldRandomizeRemoveAnimation() { return randomizeRemoveAnimation; }
    public boolean shouldRandomizeRenderMode() { return randomizeRenderMode; }
    public boolean shouldRandomizeQuality() { return randomizeQuality; }
    public boolean shouldRandomizeMaterial() { return randomizeMaterial; }
    public boolean shouldRandomizeBackground() { return randomizeBackground; }
    public boolean shouldRandomizeParticle() { return randomizeParticle; }
    public boolean shouldRandomizeSteps() { return randomizeSteps; }
    public boolean shouldRandomizeCameraRotation() { return randomizeCameraRotation; }
    public boolean shouldRandomizeAntiAliasing() { return randomizeAntiAliasing; }
    public boolean shouldRandomizeCameraSpeed() { return randomizeCameraSpeed; }
}
