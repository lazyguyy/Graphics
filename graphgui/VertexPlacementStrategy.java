package graphgui;

import graph.*;

public abstract class VertexPlacementStrategy {
    protected Graph g;
    protected boolean changing;
    protected final double MIN_DISTANCE, MAX_DISTANCE, DISTANCE_OFFSET_FACTOR, VERTEX_SIZE, CANVAS_SIZE;
    protected Settings properties;


    public VertexPlacementStrategy(Graph g, Settings properties) {
        this.g = g;
        this.properties = properties;
        MAX_DISTANCE = properties.parseAsDoubleOrDefault("MAX_DISTANCE");
        MIN_DISTANCE = properties.parseAsDoubleOrDefault("MIN_DISTANCE");
        VERTEX_SIZE = properties.parseAsDoubleOrDefault("VERTEX_SIZE");
        CANVAS_SIZE = properties.parseAsDoubleOrDefault("CANVAS_SIZE");
        DISTANCE_OFFSET_FACTOR = properties.parseAsDoubleOrDefault("DISTANCE_OFFSET_FACTOR");
        changing = true;
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
    public boolean changing() {
        return changing;
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
