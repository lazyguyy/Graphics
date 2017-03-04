package graphgui;

import graph.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.util.function.*;

public class PainterPanel extends JPanel{
    private final double MIN_DISTANCE = 50, MAX_DISTANCE = 200, DISTANCE_OFFSET_FACTOR = 1.2;
    private final int VERTEX_SIZE = 20, CANVAS_SIZE = 1000;
    private Graph g;
    private Vector2D[] coordinates;
    private VertexPlacementStrategy strategy;
    private ArrayList<Shape> edges;
    private ArrayList<Shape> vertices;
    private Color backgroundColor;
    private boolean stop, update;

    public PainterPanel(Graph g, Color backgroundColor) {
        this.g = GraphFactory.createListGraph(g);
        this.backgroundColor = backgroundColor;
        setPreferredSize(new Dimension(CANVAS_SIZE, CANVAS_SIZE));
        strategy = new RandomPlacementStrategy(g, MIN_DISTANCE, MAX_DISTANCE, DISTANCE_OFFSET_FACTOR, VERTEX_SIZE, CANVAS_SIZE);
        stop = true;
        update = true;
        edges = new ArrayList<Shape>();
        vertices = new ArrayList<Shape>();
        coordinates = new Vector2D[g.vertexCount()];
    }
    
    public void init() {
        if (!stop)
            return;
        new Thread(() -> {
            stop = false;
            while (!stop) {
                repaint();
                if (update) {
                    constructGraphShapes();
                    update = false;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }).start();
    }
    
    private void constructGraphShapes() {
        placeVertices();
        for (int i = 0; i < g.vertexCount(); i++) {
            Shape vertex = new Shape(g.vertexNames()[i], null);
            vertex.setPainter(createVertexPainter(vertex, i));
            addMouseMotionListener(vertex);
            vertices.add(vertex);
            //Add all the edges
            for (WeightedEdge e : g.adjacency(i)) {
                Shape edge = new Shape("" + e.weight, null);
                edge.setPainter(createEdgePainter(edge, e));
                edges.add(edge);
                addMouseMotionListener(edge);
            }
        }
    }
    
    private void placeVertices() {
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = new Vector2D(Math.random()*(CANVAS_SIZE -  2*VERTEX_SIZE) + VERTEX_SIZE, Math.random()*(CANVAS_SIZE -  2*VERTEX_SIZE) + VERTEX_SIZE);
        }
        while (strategy.hasChanged()) {
            strategy.adjustPlacements(coordinates);
        }
    }
    
    private Consumer<Graphics> createVertexPainter(Shape vertex, int index) {
        double x = coordinates[index].x;
        double y = coordinates[index].y;
        return (gr -> {
            gr.setColor(backgroundColor);
            gr.fillOval((int)x - VERTEX_SIZE, (int)y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
            gr.setColor(vertex.getColor());
            gr.drawOval((int)x - VERTEX_SIZE, (int)y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
            gr.setColor(Color.black);
            gr.drawString(vertex.getValue(), (int)x, (int)y);
        });
        
    }
    
    private Consumer<Graphics> createEdgePainter(Shape edge, WeightedEdge e) {
        return (gr -> {
            gr.setColor(edge.getColor());
            gr.drawLine((int)coordinates[e.from].x, (int)coordinates[e.from].y, (int)coordinates[e.to].x, (int)coordinates[e.to].y);
            Vector2D direction = coordinates[e.to].diff(coordinates[e.from]).scale(VERTEX_SIZE);
            gr.drawString(edge.getValue(), (int)(coordinates[e.from].x + direction.scale(VERTEX_SIZE + MIN_DISTANCE/2).x), (int)(coordinates[e.from].y + direction.scale(VERTEX_SIZE + MIN_DISTANCE/2).y));
            GraphicsHelper.drawArrowTip(gr, coordinates[e.to].diff(direction), direction);
        });
    }

    public void stop() {
        stop = true;
    }
    
    public void update(Graph g) {
        this.g = GraphFactory.createListGraph(g);
    }
    
    @Override
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
