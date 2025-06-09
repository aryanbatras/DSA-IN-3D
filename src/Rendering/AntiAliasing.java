package Rendering;

public enum AntiAliasing {
    NONE(1.0),
    X2(2.0),
    X4(4.0),
    X8(8.0);

    private final double path;

    AntiAliasing(double path) {
        this.path = path;
    }
}
