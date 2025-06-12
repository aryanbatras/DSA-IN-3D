package Rendering;

public enum Frames {
    VERY_FAST(1),
    FAST(5),
    NORMAL(15),
    SLOW(30),
    VERY_SLOW(60),
    EXTRA_SLOW(90),
    ULTRA_SLOW(120);

    private final int framesPerAnimation;

    Frames(int framesPerAnimation) {
        this.framesPerAnimation = framesPerAnimation;
    }

    public int getFrames() {
        return framesPerAnimation;
    }
}
