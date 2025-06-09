package Utility;

public class Camera {
    private Point lowerLeftCorner;
    private Point horizontal;
    private Point vertical;

    public Point origin;
    public Point u, v, w;

    private double yaw, pitch, radius;
    private double M_X, M_Y, M_Z;

    // --- Constructors ---
    public Camera() {
        this.radius = new Point(1, 2, -3).length();
        this.yaw = 0.25;
        this.pitch = 0.00;
        this.M_X = this.M_Y = this.M_Z = 0.00;
    }

    public Camera(double radius, double yaw, double pitch, double M_X, double M_Y, double M_Z) {
        this.radius = radius;
        this.yaw = yaw;
        this.pitch = pitch;
        this.M_X = M_X;
        this.M_Y = M_Y;
        this.M_Z = M_Z;
    }

    public Camera(Point lookfrom, Point lookat, Point vup, double vfov, double aspectRatio) {
        setup(lookfrom, lookat, vup, vfov, aspectRatio);
    }

    // --- Core Setup Method ---
    private void setup(Point lookfrom, Point lookat, Point vup, double vfov, double aspectRatio) {
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
        lowerLeftCorner = origin
                .sub(horizontal.mul(0.5))
                .sub(vertical.mul(0.5))
                .sub(w);
    }

    // --- Dynamic Perspective Setup ---
    public Camera setCameraPerspective(double width, double height) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double dirX = Math.cos(pitchRad) * Math.sin(yawRad);
        double dirY = Math.sin(pitchRad);
        double dirZ = Math.cos(pitchRad) * Math.cos(yawRad);

        Point direction = new Point(dirX, dirY, dirZ).normalize();
        Point center = new Point(M_X, M_Y, M_Z);
        Point lookFrom = center.add(direction.mul(-radius));
        Point lookAt = center;
        Point vup = new Point(0, 1, 0);

        setup(lookFrom, lookAt, vup, 90, width / height);
        return this;
    }

    // --- Ray Generation ---
    public Ray getRay(double s, double t) {
        Point direction = lowerLeftCorner.add(horizontal.mul(s)).add(vertical.mul(t)).sub(origin);
        return new Ray(origin, direction);
    }

    public void getRayFast(double u, double v, Ray ray) {
        Point dir = lowerLeftCorner.add(horizontal.mul(u)).add(vertical.mul(v)).sub(origin);
        ray.set(origin, dir);
    }

    // --- Rotation Helper (for interactive motion) ---
    public Point inverseRotateDirection(Point dir) {
        double cosPitch = Math.cos(-pitch), sinPitch = Math.sin(-pitch);
        double cosYaw = Math.cos(-yaw), sinYaw = Math.sin(-yaw);

        double y1 = dir.y * cosPitch - dir.z * sinPitch;
        double z1 = dir.y * sinPitch + dir.z * cosPitch;
        double x2 = dir.x * cosYaw + z1 * sinYaw;
        double z2 = -dir.x * sinYaw + z1 * cosYaw;

        return new Point(x2, y1, z2);
    }

    // --- Getters & Setters ---
    public double getPitch() { return pitch; }
    public double getYaw() { return yaw; }
    public double getM_X() { return M_X; }
    public double getM_Y() { return M_Y; }
    public double getM_Z() { return M_Z; }
    public double getRadius() { return radius; }

    public void setPitch(double pitch) { this.pitch = pitch; }
    public void setYaw(double yaw) { this.yaw = yaw; }
    public void setM_X(double m_X) { M_X = m_X; }
    public void setM_Y(double m_Y) { M_Y = m_Y; }
    public void setM_Z(double m_Z) { M_Z = m_Z; }
    public void setRadius(double radius) { this.radius = radius; }
}
