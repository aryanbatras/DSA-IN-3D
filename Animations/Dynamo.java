package Animations;

import Rendering.*;

import java.util.Random;
import java.util.function.Supplier;

public final class Dynamo {
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

    public static final Dynamo INSTANCE = new Dynamo();

    private Dynamo() {}

    public static Entrance randomInsertAnimation() {
        Entrance[] values = Entrance.values();
        return values[rand.nextInt(values.length)];
    }

    public static Exit randomRemoveAnimation() {
        Exit[] values = Exit.values();
        return values[rand.nextInt(values.length)];
    }

    public static Render randomRenderMode() {
        Render[] values = Render.values();
        return values[rand.nextInt(values.length)];
    }

    public static Resolution randomQuality() {
        Resolution[] values = Resolution.values();
        return values[rand.nextInt(values.length)];
    }

    public static Texture randomMaterial() {
        Texture[] values = Texture.values();
        return values[rand.nextInt(values.length)];
    }

    public static Scenery randomBackground() {
        Scenery[] values = Scenery.values();
        return values[rand.nextInt(values.length)];
    }

    public static Effect randomParticle() {
        Effect[] values = Effect.values();
        return values[rand.nextInt(values.length)];
    }

    public static Frames randomSteps() {
        Frames[] values = Frames.values();
        return values[rand.nextInt(values.length)];
    }

    public static View randomCameraRotation() {
        View[] values = View.values();
        return values[rand.nextInt(values.length)];
    }

    public static Smooth randomAntiAliasing() {
        Smooth[] values = Smooth.values();
        return values[rand.nextInt(values.length)];
    }

    public static Pace randomCameraSpeed() {
        Pace[] values = Pace.values();
        return values[rand.nextInt(values.length)];
    }

    public Dynamo withInsertAnimation() {
        this.randomizeInsertAnimation = true;
        return this;
    }

    public Dynamo withRemoveAnimation() {
        this.randomizeRemoveAnimation = true;
        return this;
    }

    public Dynamo withRenderMode() {
        this.randomizeRenderMode = true;
        return this;
    }

    public Dynamo withQuality() {
        this.randomizeQuality = true;
        return this;
    }

    public Dynamo withMaterial() {
        this.randomizeMaterial = true;
        return this;
    }

    public Dynamo withBackground() {
        this.randomizeBackground = true;
        return this;
    }

    public Dynamo withParticle() {
        this.randomizeParticle = true;
        return this;
    }

    public Dynamo withSteps() {
        this.randomizeSteps = true;
        return this;
    }

    public Dynamo withCameraRotation() {
        this.randomizeCameraRotation = true;
        return this;
    }

    public Dynamo withAntiAliasing() {
        this.randomizeAntiAliasing = true;
        return this;
    }

    public Dynamo withCameraSpeed() {
        this.randomizeCameraSpeed = true;
        return this;
    }

    public Dynamo withCrazyMode() {
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

    public Dynamo withoutInsertAnimation() {
        this.randomizeInsertAnimation = false;
        return this;
    }

    public Dynamo withoutRemoveAnimation() {
        this.randomizeRemoveAnimation = false;
        return this;
    }

    public Dynamo withoutRenderMode() {
        this.randomizeRenderMode = false;
        return this;
    }

    public Dynamo withoutQuality() {
        this.randomizeQuality = false;
        return this;
    }

    public Dynamo withoutMaterial() {
        this.randomizeMaterial = false;
        return this;
    }

    public Dynamo withoutBackground() {
        this.randomizeBackground = false;
        return this;
    }

    public Dynamo withoutParticle() {
        this.randomizeParticle = false;
        return this;
    }

    public Dynamo withoutSteps() {
        this.randomizeSteps = false;
        return this;
    }

    public Dynamo withoutCameraRotation() {
        this.randomizeCameraRotation = false;
        return this;
    }

    public Dynamo withoutAntiAliasing() {
        this.randomizeAntiAliasing = false;
        return this;
    }

    public Dynamo withoutCameraSpeed() {
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
