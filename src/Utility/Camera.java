package Utility;

public class Camera {
    private Point origin;
    private Point lowerLeftCorner;
    private Point horizontal;
    private Point vertical;
    private Point u, v, w;
    private double yaw;
    private double pitch;
    private double radius;
    private double M_X;
    private double M_Y;
    private double M_Z;

    public Camera() {
        radius = new Point(1, 2, -3).length();
        pitch = 0.00;
        yaw = 0.25;
        M_X = 0.00;
        M_Y = 0.00;
        M_Z = 0.00;
    }
    public Camera(double radius, double yaw, double pitch, int M_X, int M_Y, int M_Z){
        this.radius = radius;
        this.pitch = pitch;
        this.yaw = yaw;
        this.M_X = M_X;
        this.M_Y = M_Y;
        this.M_Z = M_Z;
    }
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

    public Camera setCameraPerspective(double WIDTH, double HEIGHT) {
        double  yawRad = Math.toRadians(yaw),
                pitchRad = Math.toRadians(pitch),
                dirX = Math.cos(pitchRad) * Math.sin(yawRad),
                dirY = Math.sin(pitchRad),
                dirZ = Math.cos(pitchRad) * Math.cos(yawRad);
        Point direction = new Point(dirX, dirY, dirZ).normalize(),
                lookFrom = new Point(M_X, M_Y, M_Z).add(direction.mul(-radius)),
                lookAt = new Point(M_X, M_Y, M_Z); Point vup = new Point(0, 1, 0);
        return new Camera(lookFrom, lookAt, vup, 90,  WIDTH / HEIGHT);
    }

    public Ray getRay(double s, double t) {
        Point direction = lowerLeftCorner.add(horizontal.mul(s)).add(vertical.mul(t)).sub(origin);
        return new Ray(origin, direction);
    }

    public void getRayFast(double u, double v, Ray ray) {
        Point dir = lowerLeftCorner.add(horizontal.mul(u)).add(vertical.mul(v)).sub(origin);
        ray.set(origin, dir);
    }


    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public void setM_X(double m_X) {
        M_X = m_X;
    }

    public void setM_Y(double m_Y) {
        M_Y = m_Y;
    }

    public void setM_Z(double m_Z) {
        M_Z = m_Z;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public double getM_Y() {
        return M_Y;
    }

    public double getM_Z() {
        return M_Z;
    }

    public double getM_X() {
        return M_X;
    }

}
