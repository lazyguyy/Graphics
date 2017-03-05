package graphgui.strategy;

import graph.*;
import graphgui.*;
import graphgui.dataManagement.*;
import java.util.*;


public class DirectedForcePlacementStrategy extends VertexPlacementStrategy{
    Vector2D[] forces;
    private double min, max;
    
    
    public DirectedForcePlacementStrategy(Graph<? extends VertexNameInfo> g, Settings properties) {
        super(g, properties);
        forces = new Vector2D[g.vertexCount()];
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < g.vertexCount(); i++) {
        	for (WeightedEdge e : g.adjacency(i)) {
            	if (e.weight < min)
            		min = e.weight;
            	else if (e.weight > max) 
            		max = e.weight;
        	}
        }
    }

    @Override
    public void adjustPlacements(Vector2D[] coordinates) {
        //Initially, each force is zero.
    	for (int i = 0; i < forces.length; i++) {
            forces[i] = new Vector2D(0, 0);
        }
    	
    	ArrayList<Vector2D> edges = new ArrayList<>();
    	for (WeightedEdge e : g) {
    		Vector2D from = coordinates[e.from];
    		Vector2D to = coordinates[e.to];
    		edges.add(from.add(to).scaleBy(0.5));
    	}
        //Calculate the forces effecting each Vertex.
        //Vertices are repulsive to each other, edges create attraction.
        for (int i = 0; i < forces.length; i++) {
            for (int j = 0; j < forces.length; j++) {
                if (i == j)
                    continue;
                Vector2D direction = coordinates[j].diff(coordinates[i]);
                if (g.hasEdge(i, j)) {
                    Vector2D springForce = calculateSpringForce(g.edge(i, j).weight, direction).scaleBy(properties.getValue("EDGE_ATTRACTION").getDouble());
                    forces[j] = forces[j].add(springForce);
                    forces[i] = forces[i].add(springForce.negate());
                }
                Vector2D coulombForce = calculateCoulombForce(direction);
                forces[j] = forces[j].add(coulombForce);
                forces[i] = forces[i].add(coulombForce.negate());
            }
            for (Vector2D edge : edges) {
            	forces[i] = forces[i].add(calculateCoulombForce(coordinates[i].diff(edge)));
            }
            forces[i] = forces[i].add(calculateWallForce(coordinates[i]));
        }
        //Apply each force to the respective vertex
        changing = false;
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = coordinates[i].add(forces[i]);
            if (forces[i].absolute() >= properties.getValue("NEGLIGIBLE_FORCE").getDouble())
                changing = true;
        }
        
    }
    
    private double scale(double distance) {
    	if (max == min)
    		return (properties.getValue("MAX_DISTANCE").getInt() + properties.getValue("MIN_DISTANCE").getInt())/2;
    	return (distance - min)/(max - min)*(properties.getValue("MAX_DISTANCE").getInt() - properties.getValue("MIN_DISTANCE").getInt()) + properties.getValue("MIN_DISTANCE").getInt();
    }
    
    private Vector2D calculateSpringForce(double weight, Vector2D direction) {
    	System.out.println(weight + ", " + direction);
        double optimalDistance = scale(weight);
        System.out.println(optimalDistance);
        Vector2D springForce = direction.scale(optimalDistance).diff(direction);
        return springForce;
    }
    
    private Vector2D calculateCoulombForce(Vector2D direction) {
        return direction.scale(properties.getValue("VERTEX_REPULSION").getDouble()*5000 / direction.squaredAbs());
    }
    
    private Vector2D calculateWallForce(Vector2D point) {
        Vector2D wallForceTopLeft = new Vector2D(properties.getValue("WALL_FORCE").getDouble()/point.x,
                                                 properties.getValue("WALL_FORCE").getDouble()/point.y);
        Vector2D wallForceBottomRight = new Vector2D(properties.getValue("WALL_FORCE").getDouble()/(point.x - properties.getValue("CANVAS_SIZE").getInt()),
                                                     properties.getValue("WALL_FORCE").getDouble()/(point.y - properties.getValue("CANVAS_SIZE").getInt()));
        return wallForceTopLeft.add(wallForceBottomRight);
    }

}
