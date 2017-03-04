package graphgui;

import graph.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.util.function.*;

public class PainterPanel extends JPanel {
    private Graph g;
    private Vector2D[] coordinates;
    private VertexPlacementStrategy strategy;
    private ArrayList<Shape> edges;
    private ArrayList<Shape> vertices;
    private Color backgroundColor;
    private boolean stop, update;
    private BufferedImage buffer;
    private Settings properties;
    
    public PainterPanel(Graph g, Color backgroundColor) {
        this.g = GraphFactory.createListGraph(g);
        this.backgroundColor = backgroundColor;
        stop = true;
        update = true;
        edges = new ArrayList<Shape>();
        vertices = new ArrayList<Shape>();
        coordinates = new Vector2D[g.vertexCount()];
        properties = new Settings("guiproperties.txt");
        properties.printProperties();
        int CANVAS_SIZE = (int)properties.parseAsDoubleOrDefault("CANVAS_SIZE");
        setPreferredSize(new Dimension(CANVAS_SIZE, CANVAS_SIZE));
        buffer = new BufferedImage(CANVAS_SIZE, CANVAS_SIZE, BufferedImage.TYPE_INT_RGB);
        strategy = new RandomPlacementStrategy(g, properties);
    }

    public void init() {
        if (!stop)
            return;
        new Thread(() -> {
            stop = false;
            while (!stop) {
                repaintScreen();
                if (update) {
                    new Thread(() -> {
                        constructGraphShapes();
                    }).start();
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
            // Add all the edges
            for (WeightedEdge e : g.adjacency(i)) {
                Shape edge = new Shape("" + e.weight, null);
                edge.setPainter(createEdgePainter(edge, e));
                edges.add(edge);
                addMouseMotionListener(edge);
            }
        }
    }

    private void placeVertices() {
        int VERTEX_SIZE = (int)properties.parseAsDoubleOrDefault("VERTEX_SIZE");
        int MIN_DISTANCE = (int)properties.parseAsDoubleOrDefault("MIN_DISTANCE");
        int verticesPerLine = (int) Math.floor(Math.sqrt(coordinates.length)) + 1;
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = new Vector2D((i % verticesPerLine) * MIN_DISTANCE + VERTEX_SIZE,
                    i / verticesPerLine * MIN_DISTANCE + VERTEX_SIZE);
        }
        while (strategy.changing() && !stop) {
            strategy.adjustPlacements(coordinates);
        }
    }

    private Consumer<Graphics> createVertexPainter(Shape vertex, int index) {
        double x = coordinates[index].x;
        double y = coordinates[index].y;
        int VERTEX_SIZE = (int)properties.parseAsDoubleOrDefault("VERTEX_SIZE");
        return (gr -> {
            gr.setColor(backgroundColor);
            gr.fillOval((int) x - VERTEX_SIZE, (int) y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
            gr.setColor(vertex.getColor());
            gr.drawOval((int) x - VERTEX_SIZE, (int) y - VERTEX_SIZE, VERTEX_SIZE * 2, VERTEX_SIZE * 2);
            gr.drawString(vertex.getValue(), (int) x, (int) y);
        });

    }

    private Consumer<Graphics> createEdgePainter(Shape edge, WeightedEdge e) {
        int VERTEX_SIZE = (int)properties.parseAsDoubleOrDefault("VERTEX_SIZE");
        int MIN_DISTANCE = (int)properties.parseAsDoubleOrDefault("MIN_DISTANCE");
        double TIP_ANGLE = properties.parseAsDoubleOrDefault("TIP_ANGLE") / 180 * Math.PI;
        Vector2D direction = coordinates[e.to].diff(coordinates[e.from]).scale(VERTEX_SIZE);
        // The arrow tip
        Vector2D tip1 = direction.rotate(TIP_ANGLE).scale(VERTEX_SIZE).negate();
        Vector2D tip2 = direction.rotate(2 * Math.PI - TIP_ANGLE).scale(VERTEX_SIZE).negate();
        Vector2D pointOfImpact = coordinates[e.to].diff(direction);
        Vector2D labelPosition = direction.scale(VERTEX_SIZE + MIN_DISTANCE / 2).add(coordinates[e.from]);
        return (gr -> {
            gr.setColor(edge.getColor());
            gr.drawLine((int) coordinates[e.from].x, (int) coordinates[e.from].y, (int) coordinates[e.to].x,
                    (int) coordinates[e.to].y);
            gr.drawString(edge.getValue(), (int) labelPosition.x - edge.getValue().length()*3, (int) labelPosition.y);
            GraphicsHelper.drawLine(gr, pointOfImpact, tip1);
            GraphicsHelper.drawLine(gr, pointOfImpact, tip2);
        });
    }

    public void stop() {
        stop = true;
    }

    public void update(Graph g) {
        this.g = GraphFactory.createListGraph(g);
    }
    
    private void repaintScreen() {
        int CANVAS_SIZE = (int)properties.parseAsDoubleOrDefault("CANVAS_SIZE");
        if (buffer == null) {
            return;
        }
        Graphics renderGraphics = buffer.getGraphics();
        renderGraphics.setColor(backgroundColor);
        renderGraphics.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
        draw(renderGraphics);
        renderGraphics.dispose();
        Graphics g = this.getGraphics();
        g.drawImage(buffer, 0, 0, null);
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
    
    public void draw(Graphics g) {
        if (vertices.size() == 0) {
            int VERTEX_SIZE = (int)properties.parseAsDoubleOrDefault("VERTEX_SIZE");
            g.setColor(Color.black);
            for (Vector2D point : coordinates) {
                if (point != null)
                    g.drawOval((int)point.x - VERTEX_SIZE, (int)point.y - VERTEX_SIZE, 2*VERTEX_SIZE, 2*VERTEX_SIZE);
            }
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
