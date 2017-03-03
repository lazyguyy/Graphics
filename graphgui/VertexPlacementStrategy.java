package graphgui;

import graph.*;

public abstract class VertexPlacementStrategy {
    protected Graph g;
    protected boolean changed;
    private final double MIN_DISTANCE = 50, MAX_DISTANCE = 200, DISTANCE_OFFSET_FACTOR = 1.2;
    private final int VERTEX_SIZE = 20, CANVAS_SIZE = 1000;


    public VertextPlacementStrategy(Graph g, double MIN_DISTANCE, MAX_DISTANCE, DISTANCE_OFFSET_FACTOR, int CANVAS_SIZE) {
        this.g = g;
        this.MIN_DISTANCE = MIN_DISTANCE;
        this.MAX_DISTANCE = MAX_DISTANCE;
        this.DISTANCE_OFFSET_FACTOR = DISTANCE_OFFSET_FACTOR;
        this.CANVAS_SIZE = CANVAS_SIZE;
    }
    public double value(Tuple[] coordinates) {
        double value;
        for (int i = 0; i < g.vertexCount(); i++) {
            for (int j = i + 1; j < g.vertexCount(); j++) {
                value += distance(i, j);
            }
        }
    }
    public abstract double adjustPlacements(Tuple[] coordinates)
    public boolean hasChanged() {
        return changed;
    }

    protected double distance(int x, int y) {
        double distance;
        double current = x.squaredEuclidian(y);
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
        return 1 / (1 + Math.pow(Math.e, -x));
    }

    protected double scaledSigmoid(double x, double min, double max) {
        return (max - min)*sigmoid(x) + min;
    }
}
