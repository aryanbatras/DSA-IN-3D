package Rendering;

public enum Focus {
    X1(0.0),
    X2(0.5),
    X4(1.0),
    X8(1.25),
    X16(1.75);

    private final double multiplier;

    Focus(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
