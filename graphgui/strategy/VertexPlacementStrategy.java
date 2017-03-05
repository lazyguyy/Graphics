package graphgui.strategy;

import graph.*;
import graphgui.*;
import graphgui.dataManagement.*;

public abstract class VertexPlacementStrategy {
    protected Graph<? extends VertexNameInfo> g;
    protected boolean changing;
    protected Settings properties;


    public VertexPlacementStrategy(Graph<? extends VertexNameInfo> g, Settings properties) {
        this.g = g;
        this.properties = properties;
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
            distance += Math.abs(scaledSigmoid(g.edge(x, y).weight, properties.getValue("MIN_DISTANCE").getInt(), properties.getValue("MAX_DISTANCE").getInt()) - current);
        }
        else {
            distance += Math.abs(properties.getValue("MAX_DISTANCE").getInt() * properties.getValue("DISTANCE_OFFSET_FACTOR").getInt() - current);
        }
        if (g.hasEdge(y, x)) {
            distance += Math.abs(scaledSigmoid(g.edge(y, x).weight, properties.getValue("MIN_DISTANCE").getInt(), properties.getValue("MAX_DISTANCE").getInt()) - current);
        }
        else {
            distance += Math.abs(properties.getValue("MAX_DISTANCE").getInt() * properties.getValue("DISTANCE_OFFSET_FACTOR").getInt() - current);
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
