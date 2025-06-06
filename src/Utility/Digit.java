package Utility;

public class Digit {

    private static final double SEGMENT_RADIUS = 0.152;
    private static final float SEGMENT_WIDTH = 0.6f;
    private static final float SEGMENT_HEIGHT = 0.05f;
    private static final float SEGMENT_THICKNESS = 0.3f;


    public static boolean isInDigit(int digit, double u, double v, int l) {
        u = (u - 0.3) / 0.2;
        v = (v - 0.52) / getVerticalScale(l);

        if (u < -0.2 || u > 1.2 || v < -0.2 || v > 1.2 || u < 0 || u > 1 || v < 0 || v > 1)
            return false;

        boolean top = roundRect(u, v, 0.5, 0.92, SEGMENT_WIDTH, SEGMENT_HEIGHT);
        boolean mid = roundRect(u, v, 0.5, 0.5, SEGMENT_WIDTH, SEGMENT_HEIGHT);
        boolean bot = roundRect(u, v, 0.5, 0.08, SEGMENT_WIDTH, SEGMENT_HEIGHT);
        boolean lt = roundRect(u, v, 0.18, 0.75, SEGMENT_HEIGHT, SEGMENT_THICKNESS);
        boolean lb = roundRect(u, v, 0.18, 0.25, SEGMENT_HEIGHT, SEGMENT_THICKNESS);
        boolean rt = roundRect(u, v, 0.82, 0.75, SEGMENT_HEIGHT, SEGMENT_THICKNESS);
        boolean rb = roundRect(u, v, 0.82, 0.25, SEGMENT_HEIGHT, SEGMENT_THICKNESS);

        return switch (digit) {
            case 0 -> top || bot || lt || lb || rt || rb;
            case 1 -> rt || rb;
            case 2 -> top || mid || bot || rt || lb;
            case 3 -> top || mid || bot || rt || rb;
            case 4 -> mid || rt || rb || lt;
            case 5 -> top || mid || bot || lt || rb;
            case 6 -> mid || bot || lt || lb || rb;
            case 7 -> top || rt || rb;
            case 8 -> top || mid || bot || lt || lb || rt || rb;
            case 9 -> top || mid || lt || rt || rb;
            default -> false;
        };
    }

    private static double getVerticalScale(int digitCount) {
        if (digitCount <= 2) return 0.2;
        if (digitCount <= 4) return 0.1;
        return 0.05;
    }

    public static boolean roundRect(double u, double v, double cx, double cy, double w, double h) {
        double dx = Math.max(Math.abs(u - cx) - w / 2.0, 0);
        double dy = Math.max(Math.abs(v - cy) - h / 2.0, 0);
        return (dx * dx + dy * dy) < SEGMENT_RADIUS * SEGMENT_RADIUS;
    }

}
