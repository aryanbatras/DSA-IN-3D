package Rendering;

public enum Pace {
    X1(0.0052),
    X2(0.0104),
    X4(0.0208),
    X8(0.0416),
    X16(0.0832);

    private final double multiplier;

    Pace(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
