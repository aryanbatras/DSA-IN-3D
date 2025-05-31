package Utility;

public class Color {
    public float r;
    public float g;
    public float b;

    public Color(Color c) {
        r = c.r;
        g = c.g;
        b = c.b;
    }

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    Color addColors(Color c) {
        r += c.r;
        g += c.g;
        b += c.b;
        return this;
    }

    Color divideColors(int sample) {
        r /= sample;
        g /= sample;
        b /= sample;
        return this;
    }

    Color multiplyColors(double t) {
        return new Color(
                (float) (this.r * t),
                (float) (this.g * t),
                (float) (this.b * t)
        );
    }

    public int colorToInteger() {

        float rClamped = Math.min(1.0f, Math.max(0.0f, r));
        float gClamped = Math.min(1.0f, Math.max(0.0f, g));
        float bClamped = Math.min(1.0f, Math.max(0.0f, b));

        int ir = (int)(255.999 * rClamped);
        int ig = (int)(255.999 * gClamped);
        int ib = (int)(255.999 * bClamped);

        return (
                (int) (ir) << 16 |
                (int) (ig) << 8 |
                (int) (ib)
        );
    }

}
