package Rendering;

public enum Smooth {
    NONE(1.0),
    X2(2.0),
    X4(4.0),
    X8(8.0);

    private final double path;

    Smooth(double path) {
        this.path = path;
    }
}
