package graphgui;
import graph.*;
import java.util.*;
import java.awt.*;

public class PainterPanel{
    private final double MIN_DISTANCE = 50, MAX_DISTANCE = 200, DISTANCE_OFFSET_FACTOR = 1.2;
    private final int VERTEX_SIZE = 20, CANVAS_SIZE = 1000;
    private Graph g;
    private Tuple[] coordinates;
    private VertexPlacementStrategy strategy;
    private ArrayList<Shape> shapes;

    public PainterPanel(Graph g) {
        g = GraphFactory.createListGraph(g);
        strategy = new RandomPlacementStrategy(g, MIN_DISTANCE, MAX_DISTANCE, DISTANCE_OFFSET_FACTOR, VERTEX_SIZE, CANVAS_SIZE);
        new Thread(() -> {
            constructGraphShapes();
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }).start();
    }

    private void constructGraphShapes() {
        shapes = new ArrayList<Shape>();
        while (strategy.hasChanged()) {
            strategy.adjustPlacements(coordinates);
        }
        for (int i = 0; i < g.vertexCount(); i++) {
            Shape vertex = new Shape(g.vertexNames()[i], null);
            double x = coordinates[i].x;
            double y = coordinates[i].y;
            vertex.setPainter(gr -> {
                gr.setColor(vertex.getColor());
                gr.drawOval((int)x - VERTEX_SIZE, (int)y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
                gr.setColor(Color.black);
                gr.drawString(vertex.getValue(), (int)x - VERTEX_SIZE, (int)y - VERTEX_SIZE - 10);
            });
            for (WeightedEdge e : g.adjacentEdges(i)) {
                Shape edge = new Shape("" + e.weight, null);
                edge.setPainter(gr -> {
                    gr.setColor(edge.getColor());
                    gr.drawLine((int)coordinates[e.from].x, (int)coordinates[e.from].y, (int)coordinates[e.to].x, (int)coordinates[e.to].y);
                });
                shapes.add(edge);
            }
            shapes.add(vertex);
        }
    }
}
