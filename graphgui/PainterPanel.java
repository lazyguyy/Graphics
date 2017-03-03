package graphgui;

import graph.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;

public class PainterPanel extends JPanel{
    private final double MIN_DISTANCE = 50, MAX_DISTANCE = 200, DISTANCE_OFFSET_FACTOR = 1.2;
    private final int VERTEX_SIZE = 20, CANVAS_SIZE = 1000;
    private Graph g;
    private Vector2D[] coordinates;
    private VertexPlacementStrategy strategy;
    private ArrayList<Shape> edges;
    private ArrayList<Shape> vertices;
    private Color backgroundColor;
    private boolean stop;
    
    public PainterPanel(Graph g, Color backgroundColor) {
        this.g = GraphFactory.createListGraph(g);
        this.backgroundColor = backgroundColor;
        setPreferredSize(new Dimension(CANVAS_SIZE, CANVAS_SIZE));
        strategy = new RandomPlacementStrategy(g, MIN_DISTANCE, MAX_DISTANCE, DISTANCE_OFFSET_FACTOR, VERTEX_SIZE, CANVAS_SIZE);
        new Thread(() -> {
            constructGraphShapes();
            while (!stop) {
            	repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }).start();
    }

    private void constructGraphShapes() {
        edges = new ArrayList<Shape>();
        vertices = new ArrayList<Shape>();
        coordinates = new Vector2D[g.vertexCount()];
        for (int i = 0; i < coordinates.length; i++) {
        	coordinates[i] = new Vector2D(Math.random()*(CANVAS_SIZE -  2*VERTEX_SIZE) + VERTEX_SIZE, Math.random()*(CANVAS_SIZE -  2*VERTEX_SIZE) + VERTEX_SIZE);
        }
        while (strategy.hasChanged()) {
            strategy.adjustPlacements(coordinates);
        }
        for (int i = 0; i < g.vertexCount(); i++) {
            Shape vertex = new Shape(g.vertexNames()[i], null);
            double x = coordinates[i].x;
            double y = coordinates[i].y;
            vertex.setPainter(gr -> {
            	gr.setColor(backgroundColor);
            	gr.fillOval((int)x - VERTEX_SIZE, (int)y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
                gr.setColor(vertex.getColor());
                gr.drawOval((int)x - VERTEX_SIZE, (int)y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
                gr.setColor(Color.black);
                gr.drawString(vertex.getValue(), (int)x, (int)y);
            });
            addMouseMotionListener(vertex);
            vertices.add(vertex);
            //Add all the edges
            for (WeightedEdge e : g.adjacentEdges(i)) {
                Shape edge = new Shape("" + e.weight, null);
                edge.setPainter(gr -> {
                    gr.setColor(edge.getColor());
                    gr.drawLine((int)coordinates[e.from].x, (int)coordinates[e.from].y, (int)coordinates[e.to].x, (int)coordinates[e.to].y);
                    Vector2D direction = coordinates[e.to].diff(coordinates[e.from]);
                    double length = direction.distance();
                    direction = direction.scale(VERTEX_SIZE);
                    gr.drawString(edge.getValue(), (int)(coordinates[e.from].x + direction.scale(VERTEX_SIZE + MIN_DISTANCE/2).x), (int)(coordinates[e.from].y + direction.scale(VERTEX_SIZE + MIN_DISTANCE/2).y));
                    GraphicsHelper.drawArrowCap(gr, coordinates[e.to].diff(direction), direction);
                });
                edges.add(edge);
                addMouseMotionListener(edge);
            }
        }
    }
    public void stop() {
    	stop = true;
    }
    public void paint(Graphics g) {
    	if (vertices.size() == 0) {
    		g.drawString("Calculating optimal alignment", 50, 50);
    	}
    	for (Shape e : edges) {
    		e.draw(g);
    	}
    	for (Shape v : vertices) {
    		v.draw(g);
    	}
    }
}
