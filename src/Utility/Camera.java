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

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Point getLowerLeftCorner() {
        return lowerLeftCorner;
    }

    public void setLowerLeftCorner(Point lowerLeftCorner) {
        this.lowerLeftCorner = lowerLeftCorner;
    }

    public Point getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(Point horizontal) {
        this.horizontal = horizontal;
    }

    public Point getVertical() {
        return vertical;
    }

    public void setVertical(Point vertical) {
        this.vertical = vertical;
    }

    public Point getU() {
        return u;
    }

    public void setU(Point u) {
        this.u = u;
    }

    public Point getV() {
        return v;
    }

    public void setV(Point v) {
        this.v = v;
    }

    public Point getW() {
        return w;
    }

    public void setW(Point w) {
        this.w = w;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getM_X() {
        return M_X;
    }

    public void setM_X(double m_X) {
        M_X = m_X;
    }

    public void addM_X(double m_X) {
        M_X += m_X;
    }

    public void subM_X(double m_X) {
        M_X -= m_X;
    }

    public double getM_Y() {
        return M_Y;
    }

    public void setM_Y(double m_Y) {
        M_Y = m_Y;
    }

    public void addM_Y(double m_Y) {
        M_Y += m_Y;
    }

    public void subM_Y(double m_Y) {
        M_Y -= m_Y;
    }

    public double getM_Z() {
        return M_Z;
    }

    public void setM_Z(double m_Z) {
        M_Z = m_Z;
    }

    public void addM_Z(double m_Z) {
        M_Z += m_Z;
    }

    public void subM_Z(double m_Z) {
        M_Z -= m_Z;
    }

    public Camera(double radius, double yaw, double pitch, int M_X, int M_Y, int M_Z){
        this.radius = radius;
        this.yaw = yaw;
        this.pitch = pitch;
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

    public Camera setCameraPerspective(int WIDTH, int HEIGHT) {
        double  yawRad = Math.toRadians(yaw),
                pitchRad = Math.toRadians(pitch),
                dirX = Math.cos(pitchRad) * Math.sin(yawRad),
                dirY = Math.sin(pitchRad),
                dirZ = Math.cos(pitchRad) * Math.cos(yawRad);
        Point direction = new Point(dirX, dirY, dirZ).normalize(),
                lookFrom = new Point(M_X, M_Y, M_Z).add(direction.mul(-radius)),
                lookAt = new Point(M_X, M_Y, M_Z); Point vup = new Point(0, 1, 0);
        return new Camera(lookFrom, lookAt, vup, 90, (double) WIDTH / HEIGHT);
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

    public Point getLookFrom() {
        return origin;
    }

    public Point getLookAt() {
        return origin.add(w.mul(-1));
    }

    public Point getVUp() {
        return v;
    }

    public Ray getRay(double s, double t) {
        Point direction = lowerLeftCorner.add(horizontal.mul(s)).add(vertical.mul(t)).sub(origin);
        return new Ray(origin, direction);
    }

    public Ray getRayFromScreen(double screenX, double screenY, int screenWidth, int screenHeight) {
        double u = screenX / (double) screenWidth;
        double v = 1.0 - (screenY / (double) screenHeight);
        return getRay(u, v);
    }

    public void setPosition(Point newOrigin) {
        this.origin = newOrigin;
        lowerLeftCorner = origin.sub(horizontal.mul(0.5)).sub(vertical.mul(0.5)).sub(w);
    }

}
