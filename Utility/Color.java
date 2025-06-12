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
