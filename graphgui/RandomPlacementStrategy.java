package graphgui;

import graph.*;

public class RandomPlacementStrategy extends VertexPlacementStrategy {
    public RandomPlacementStrategy(Graph g, double MIN_DISTANCE, double MAX_DISTANCE, double DISTANCE_OFFSET_FACTOR, int VERTEX_SIZE, int CANVAS_SIZE) {
        super(g, MIN_DISTANCE, MAX_DISTANCE, DISTANCE_OFFSET_FACTOR, VERTEX_SIZE, CANVAS_SIZE);
    }
    
    @Override
    public void adjustPlacements(Vector2D[] coordinates) {
        for (int i = 0; i < coordinates.length; i++) {
            int x = (int)(Math.random()*(CANVAS_SIZE -  2*VERTEX_SIZE) + VERTEX_SIZE);
            int y = (int)(Math.random()*(CANVAS_SIZE -  2*VERTEX_SIZE) + VERTEX_SIZE);
            coordinates[i].x = x;
            coordinates[i].y = y;
        }
        changing = false;
        for (int i = 0; i < coordinates.length; i++) {
            for (int j = i + 1; j < coordinates.length; j++) {
                if (coordinates[i].squaredEuclidian(coordinates[j]) < MIN_DISTANCE*MIN_DISTANCE) {
                    changing = true;                    
                }
            }
        }
    }
}
