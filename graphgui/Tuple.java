package graphgui;

public class Tuple {
    public double x, y;

    public class Tuple(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double squaredEuclidian(Tuple other) {
        return Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2);
    }
}
