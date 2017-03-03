package graphgui;
import graph.*;
import java.util.*;
import java.awt.*;

public class PainterPanel implements Runnable{
    private final double MIN_DISTANCE = 50, MAX_DISTANCE = 200, DISTANCE_OFFSET_FACTOR = 1.2;
    private final int VERTEX_SIZE = 20, CANVAS_SIZE = 1000;
    private Graph g;
    private Tuple[] coordinates;
    private VertextPlacementStrategy strategy;
    private ArrayList<Shape> shapes;

    public PainterPanel(Graph g) {
        g = GraphFactory.createListGraph(g);
        strategy = new RandomPlacementStrategy(g, MIN_DISTANCE, CANVAS_SIZE);
        new Thread(new Runnable() { () -> {
            constructGraphShapes();
            while (true) {
                paintPanel();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }}).start();
    }

    private void constructGraphShapes() {
        shapes = new ArrayList<Shape>();
        while (strategy.hasChanged()) {
            strategy.adjustPlacements(g, vertexCoordinates);
        }
        for (int i = 0; i < g.vertexCount; i++) {
            Shape vertex = new Shape(g.vertices()[i], null);
            vertex.setPainter(g -> {
                g.setColor(vertex.mainColor);
                g.drawOval(coordinates[i].x - VERTEX_SIZE, coordinates[i].y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
                g.setColor(Color.black);
                g.drawString(vertex.getValue(), coordinates[i].x - VERTEX_SIZE, coordinates[i].y - VERTEX_SIZE - 10);
            });
            for (WeightedEdge e : g.adjacentEdges(i)) {
                Shape edge = new Shape(e.weight, null);
                edge.setPainter(g -> {
                    g.setColor(edge.mainColor);
                    g.drawLine(coordinates[i].x, coordinates[i].y, coordinates[e.to].x, coordinates[e.to].y);
                })
                shapes.add(edge);
            }
            shapes.add(vertex);
        }
    }
}
