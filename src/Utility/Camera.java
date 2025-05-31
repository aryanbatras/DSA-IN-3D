package Utility;

public class Camera {
    private Point origin;
    private Point lowerLeftCorner;
    private Point horizontal;
    private Point vertical;
    private Point u, v, w;
    private double lensRadius;

    public Camera(Point lookfrom, Point lookat, Point vup, double vfov, double aspectRatio) {
        double theta = Math.toRadians(vfov);
        double h = Math.tan(theta / 2);
        double viewportHeight = 2.0 * h;
        double viewportWidth = aspectRatio * viewportHeight;

        w = lookfrom.sub(lookat).normalize();
        u = vup.cross(w).normalize();
        v = w.cross(u);

        origin = lookfrom;
        horizontal = u.mul(viewportWidth);
        vertical = v.mul(viewportHeight);
        lowerLeftCorner = origin.sub(horizontal.mul(0.5)).sub(vertical.mul(0.5)).sub(w);
    }

    public Ray getRay(double s, double t) {
        Point direction = lowerLeftCorner.add(horizontal.mul(s)).add(vertical.mul(t)).sub(origin);
        return new Ray(origin, direction);
    }

    public Point getForward() {
        return w.mul(-1);
    }

    public Point getRight() {
        return u;
    }

    public Point getUp() {
        return v;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setPosition(Point newOrigin) {
        this.origin = newOrigin;
        lowerLeftCorner = origin.sub(horizontal.mul(0.5)).sub(vertical.mul(0.5)).sub(w);
    }

    public Ray getRayFromScreen(double screenX, double screenY, int screenWidth, int screenHeight) {
        double u = screenX / (double) screenWidth;
        double v = 1.0 - (screenY / (double) screenHeight);
        return getRay(u, v);
    }

    public Point getLookFrom() {
        return origin;
    }

    public Point getLookAt() {
        return origin.add(w.mul(-1));
    }

    public Point getVUp() {
        return v;
    }

}
