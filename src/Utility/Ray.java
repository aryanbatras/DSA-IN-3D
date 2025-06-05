package Utility;

public class Ray {
    public Point origin;
    Point direction;

    public Ray() {}

    public Ray(Point origin, Point direction){
        this.origin = new Point(origin);
        this.direction = new Point(direction);
    }

    public void set(Point origin, Point direction){
        this.origin = origin;
        this.direction = direction;
    }

    public Point getDirection(){
        return direction;
    }

    public    Point at(double t) {
        return origin.add(direction.mul(t));
    }

}
