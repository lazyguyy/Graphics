package graphgui.strategy;

import graph.*;
import graphgui.*;
import graphgui.dataManagement.*;

public class RandomPlacementStrategy extends VertexPlacementStrategy {
    public RandomPlacementStrategy(Graph g, Settings properties) {
        super(g, properties);
    }
    
    @Override
    public void adjustPlacements(Vector2D[] coordinates) {
        for (int i = 0; i < coordinates.length; i++) {
            int x = (int)(Math.random()*(properties.getValue("CANVAS_SIZE").getInt() -  2*properties.getValue("VERTEX_SIZE").getInt())
                          + properties.getValue("VERTEX_SIZE").getInt());
            int y = (int)(Math.random()*(properties.getValue("CANVAS_SIZE").getInt() -  2*properties.getValue("VERTEX_SIZE").getInt())
                          + properties.getValue("VERTEX_SIZE").getInt());
            coordinates[i].x = x;
            coordinates[i].y = y;
        }
        changing = false;
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = i + 1; j < coordinates.length; j++) {
                if (coordinates[i].euclidianDistance(coordinates[j]) < properties.getValue("MIN_DISTANCE").getInt()) {
                    changing = true;                    
                }
            }
        }
    }
}
