package Shapes;
import Rendering.Material;
import Utility.*;

public class Sphere extends Shape {
    public Material material;
    public Point center;
    double radius;
    public double fuzz;
    public Color color;

    public Sphere(Point center, double radius, Color c, Material m, double f){
        this.center = new Point(center);
        this.color = new Color(c);
        this.radius = radius;
        material = m;
        fuzz = f;
    }

    public double hit(Ray r) {
        double a = r.getDirection( ).dot(r.getDirection( ));
        double b = 2 * r.origin.sub(center).dot(r.getDirection( ));
        double c = r.origin.sub(center).dot(r.origin.sub(center)) - radius * radius;
        double discriminant = ( b * b ) - 4 * a * c;
        if(discriminant < 0.0 ) {
            return 0;
        } else {
            double t0 = ( -b - Math.sqrt(discriminant) ) / ( 2 * a );
            double t1 = ( -b + Math.sqrt(discriminant) ) / ( 2 * a );
            if( t0 > 10E-9){
                return t0;
            }
            if(t1 > 10E-9){
                return t1;
            }
            return 0.0;
        }
    }

    public double distanceTo(Point other) {
        double dx = center.x - other.x;
        double dy = center.y - other.y;
        double dz = center.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double getRadius() {
        return radius;
    }


}