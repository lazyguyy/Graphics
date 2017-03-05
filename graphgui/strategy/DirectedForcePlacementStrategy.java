package graphgui.strategy;

import graph.*;
import graphgui.*;
import graphgui.dataManagement.*;


public class DirectedForcePlacementStrategy extends VertexPlacementStrategy{
    Vector2D[] forces;
    
    public DirectedForcePlacementStrategy(Graph g, Settings properties) {
        super(g, properties);
        forces = new Vector2D[g.vertexCount()];
    }

    @Override
    public void adjustPlacements(Vector2D[] coordinates) {
        //Initially, each force is zero.
        for (int i = 0; i < forces.length; i++) {
            forces[i] = new Vector2D(0, 0);
        }
        //Calculate the forces effecting each Vertex.
        //Vertices are repulsive to each other, edges create attraction.
        for (int i = 0; i < forces.length; i++) {
            for (int j = 0; j < forces.length; j++) {
                if (i == j)
                    continue;
                Vector2D direction = coordinates[j].diff(coordinates[i]);
                if (g.hasEdge(i, j)) {
                    Vector2D springForce = calculateSpringForce(g.edge(i, j).weight, direction);
                    forces[j] = forces[j].add(springForce);
                    forces[i] = forces[i].add(springForce.negate());
                }
                Vector2D coulombForce = calculateCoulombForce(direction);
                forces[j] = forces[j].add(coulombForce);
                forces[i] = forces[i].add(coulombForce.negate());
            }
            forces[i] = forces[i].add(calculateWallForce(coordinates[i]));
        }
        //Apply each force to the respective vertex
        changing = false;
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = coordinates[i].add(forces[i]);
            if (forces[i].absolute() > properties.getValue("NEGLIGIBLE_FORCE").getDouble())
                changing = true;
        }
        
    }
    
    private Vector2D calculateSpringForce(double weight, Vector2D direction) {
        return direction.scale(scaledSigmoid(weight, properties.getValue("MIN_DISTANCE").getInt(), properties.getValue("MAX_DISTANCE").getInt()) - direction.absolute());
    }
    
    private Vector2D calculateCoulombForce(Vector2D direction) {
        return direction.scale(properties.getValue("VERTEX_REPULSION").getDouble() / direction.squaredAbs());
    }
    
    private Vector2D calculateWallForce(Vector2D point) {
        Vector2D wallForceTopLeft = new Vector2D(Math.exp(-point.x/properties.getValue("WALL_FORCE").getDouble()),
                                                 Math.exp(-point.y/properties.getValue("WALL_FORCE").getDouble()));
        Vector2D wallForceBottomRight = new Vector2D(Math.exp((properties.getValue("CANVAS_SIZE").getInt() - point.x )/properties.getValue("WALL_FORCE").getDouble()),
                                                     Math.exp((properties.getValue("CANVAS_SIZE").getInt() - point.x )/properties.getValue("WALL_FORCE").getDouble()));
        return wallForceTopLeft.add(wallForceBottomRight);
    }

}
