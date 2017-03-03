package graphgui;

import graph.*;

public abstract class VertexPlacementStrategy {
    protected Graph g;
    protected boolean changed;
    protected final double MIN_DISTANCE, MAX_DISTANCE, DISTANCE_OFFSET_FACTOR;
    protected final int VERTEX_SIZE, CANVAS_SIZE;


    public VertexPlacementStrategy(Graph g, double MIN_DISTANCE, double MAX_DISTANCE, double DISTANCE_OFFSET_FACTOR, int VERTEX_SIZE, int CANVAS_SIZE) {
        this.g = g;
        this.MIN_DISTANCE = MIN_DISTANCE;
        this.MAX_DISTANCE = MAX_DISTANCE;
        this.DISTANCE_OFFSET_FACTOR = DISTANCE_OFFSET_FACTOR;
        this.VERTEX_SIZE = VERTEX_SIZE;
        this.CANVAS_SIZE = CANVAS_SIZE;
        changed = true;
    }
    public double value(Vector2D[] coordinates) {
        double value = 0;
        for (int i = 0; i < g.vertexCount(); i++) {
            for (int j = i + 1; j < g.vertexCount(); j++) {
                value += distance(i, j, coordinates);
            }
        }
        return value;
    }
    public abstract void adjustPlacements(Vector2D[] coordinates);
    public boolean hasChanged() {
        return changed;
    }

    protected double distance(int x, int y, Vector2D[] coordinates) {
        double distance = 0;
        double current = coordinates[x].squaredEuclidian(coordinates[y]);
        if (g.hasEdge(x, y)) {
            distance += Math.abs(scaledSigmoid(g.edge(x, y).weight, MIN_DISTANCE, MAX_DISTANCE) - current);
        }
        else {
            distance += Math.abs(MAX_DISTANCE * DISTANCE_OFFSET_FACTOR - current);
        }
        if (g.hasEdge(y, x)) {
            distance += Math.abs(scaledSigmoid(g.edge(y, x).weight, MIN_DISTANCE, MAX_DISTANCE) - current);
        }
        else {
            distance += Math.abs(MAX_DISTANCE * DISTANCE_OFFSET_FACTOR - current);
        }
        return distance / 2;
    }

    protected double sigmoid(double x) {
        return 1 / (1 + Math.pow(Math.E, -x));
    }

    protected double scaledSigmoid(double x, double min, double max) {
        return (max - min)*sigmoid(x) + min;
    }
}
