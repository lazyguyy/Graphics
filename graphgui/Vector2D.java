package graphgui;

import java.util.*;

public class Vector2D {
    public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double squaredEuclidian(Vector2D other) {
        return Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2);
    }

    public double euclidianDistance(Vector2D other) {
        return Math.sqrt(squaredEuclidian(other));
    }

    public double squaredAbs() {
        return x * x + y * y;
    }

    public double absolute() {
        return Math.sqrt(squaredAbs());
    }

    public Vector2D diff(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    public Vector2D negate() {
        return new Vector2D(-x, -y);
    }

    public Vector2D scale(double scale) {
        double scaleFactor = scale / absolute();
        return new Vector2D(x * scaleFactor, y * scaleFactor);
    }

    public Vector2D rotate(double angle) {
        double newX = x * Math.cos(angle) + y * Math.sin(angle);
        double newY = -x * Math.sin(angle) + y * Math.cos(angle);
        return new Vector2D(newX, newY);
    }

    public String toString() {
        return String.format(Locale.US, "(%3.2f,%3.2f)", x, y);
    }
}
