package Utility;

public class Point {
    public double x;
    public double y;
    public double z;

    public Point(Point p) {
        x = p.x;
        y = p.y;
        z = p.z;
    }

    public Point() {

    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point add(Point p) {
        return new Point(
                x + p.x,
                y + p.y,
                z + p.z
        );
    }

    public Point sub(Point p) {
        return new Point(
                x - p.x,
                y - p.y,
                z - p.z
        );
    }

    Point div(double scalar) {
        return new Point(
                x / scalar,
                y / scalar,
                z / scalar
        );
    }

    public Point mul(double scalar) {
        return new Point(
                this.x * scalar,
                this.y * scalar,
                this.z * scalar
        );
    }

    public double dot(Point p) {
        return x * p.x + y * p.y + z * p.z;
    }

    public Point normalize() {
        double len = Math.sqrt(x * x + y * y + z * z);
        if (len == 0) return new Point(0, 0, 0); // avoid division by zero
        return new Point(x / len, y / len, z / len);
    }

    public Point cross(Point other) {
        double x = this.y * other.z - this.z * other.y;
        double y = this.z * other.x - this.x * other.z;
        double z = this.x * other.y - this.y * other.x;
        return new Point(x, y, z);
    }

    public double length(){
        return Math.sqrt(x * x + y * y + x * x);
    }

    public static Point projectToGround(Ray ray, double preserveY) {
        if (Math.abs(ray.direction.y) < 1e-6) { return ray.origin; }
        double t = -ray.origin.y / ray.direction.y;
        Point projected = ray.at(t);
        return new Point(projected.x, preserveY, projected.z);
    }

    public double distanceTo(Point other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}












