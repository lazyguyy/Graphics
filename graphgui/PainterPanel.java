package graphgui;

import graph.*;
import graphgui.curves.*;
import graphgui.dataManagement.Settings;
import graphgui.strategy.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.util.function.*;

public class PainterPanel extends JPanel {
    private Graph<VertexNameInfo> g;
    private Vector2D[] coordinates;
    private VertexPlacementStrategy strategy;
    private ArrayList<Shape> edges;
    private ArrayList<Shape> vertices;
    private Color backgroundColor;
    private boolean stop, update;
    private BufferedImage buffer;
    private Settings properties;
    
    public PainterPanel(Graph<VertexNameInfo> g, Color backgroundColor) {
        this.g = GraphFactory.createListGraph(g);
        this.backgroundColor = backgroundColor;
        stop = true;
        update = true;
        edges = new ArrayList<Shape>();
        vertices = new ArrayList<Shape>();
        coordinates = new Vector2D[g.vertexCount()];
        properties = new Settings("guiproperties.txt");
//        properties.printProperties();
        setPreferredSize(new Dimension(properties.getValue("CANVAS_SIZE").getInt(), properties.getValue("CANVAS_SIZE").getInt()));
        buffer = new BufferedImage(properties.getValue("CANVAS_SIZE").getInt(), properties.getValue("CANVAS_SIZE").getInt(), BufferedImage.TYPE_INT_RGB);
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
            Shape vertex = new Shape(/*g.info().vertexNames()[i]*/"" + i, null);
            vertex.setPainter(createVertexPainter(vertex, i));
            addMouseMotionListener(vertex);
            vertices.add(vertex);
            // Add all the edges
            for (WeightedEdge e : g.adjacency(i)) {
                Shape edge = new Shape("" + e.weight, null);
                edge.setPainter(createCurvedEdgePainter(edge, e));
                edges.add(edge);
                addMouseMotionListener(edge);
            }
        }
    }

    private void placeVertices() {
        int verticesPerLine = (int) Math.floor(Math.sqrt(coordinates.length)) + 1;
        double vertexDistance = properties.getValue("CANVAS_SIZE").getInt() / 2 / verticesPerLine;
        strategy = new DirectedForcePlacementStrategy(g, properties);
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = new Vector2D((i % verticesPerLine) * vertexDistance + properties.getValue("CANVAS_SIZE").getInt()/4,
                                           i / verticesPerLine  * vertexDistance + properties.getValue("CANVAS_SIZE").getInt()/4);
        }
        while (strategy.changing() && !stop) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            strategy.adjustPlacements(coordinates);
        }
    }

    private Consumer<Graphics> createVertexPainter(Shape vertex, int index) {
        double x = coordinates[index].x;
        double y = coordinates[index].y;
        return (gr -> {
            gr.setColor(backgroundColor);
            gr.fillOval((int) x - properties.getValue("VERTEX_SIZE").getInt(), 
                        (int) y - properties.getValue("VERTEX_SIZE").getInt(), 
                                  properties.getValue("VERTEX_SIZE").getInt() * 2, 
                                  properties.getValue("VERTEX_SIZE").getInt() * 2);
            gr.setColor(vertex.getColor());
            gr.drawOval((int) x - properties.getValue("VERTEX_SIZE").getInt(), 
                        (int) y - properties.getValue("VERTEX_SIZE").getInt(), 
                                  properties.getValue("VERTEX_SIZE").getInt() * 2, 
                                  properties.getValue("VERTEX_SIZE").getInt() * 2);
            gr.drawString(vertex.getValue(), (int) x, (int) y);
        });

    }

    private Consumer<Graphics> createEdgePainter(Shape edge, WeightedEdge e) {
        Vector2D direction = coordinates[e.to].diff(coordinates[e.from]).scale(properties.getValue("VERTEX_SIZE").getInt());
        // The arrow tip
        Vector2D tip1 = direction.rotate(toRadians(properties.getValue("TIP_ANGLE").getDouble())).scale(properties.getValue("VERTEX_SIZE").getInt()).negate();
        Vector2D tip2 = direction.rotate(2 * Math.PI - toRadians(properties.getValue("TIP_ANGLE").getDouble())).scale(properties.getValue("VERTEX_SIZE").getInt()).negate();
        Vector2D pointOfImpact = coordinates[e.to].diff(direction);
        Vector2D labelPosition = direction.scale(properties.getValue("VERTEX_SIZE").getInt() + properties.getValue("MIN_DISTANCE").getInt() / 2).add(coordinates[e.from]);
        return (gr -> {
            gr.setColor(edge.getColor());
            gr.drawLine((int) coordinates[e.from].x, (int) coordinates[e.from].y, (int) coordinates[e.to].x,
                    (int) coordinates[e.to].y);
            gr.drawString(edge.getValue(), (int) labelPosition.x - edge.getValue().length()*3, (int) labelPosition.y);
            GraphicsHelper.drawLineInDirection(gr, pointOfImpact, tip1);
            GraphicsHelper.drawLineInDirection(gr, pointOfImpact, tip2);
        });
    }
    
    private Consumer<Graphics> createCurvedEdgePainter(Shape edge, WeightedEdge e) {
        Vector2D direction = coordinates[e.to].diff(coordinates[e.from]);
        System.out.println(direction);
        Vector2D controlPoint = coordinates[e.from].add(direction.rotate(Math.PI / 2).scaleBy(properties.getValue("BEZIER_CURVE").getDouble()).add(direction.scaleBy(0.5)));
        Vector2D[] controlPoints = {coordinates[e.from], controlPoint, coordinates[e.to]};
        Vector2D[] bezierPoints = DeCasteljau.calculateBezierPoints(controlPoints, properties.getValue("BEZIER_ACCURACY").getInt());
        Vector2D labelPosition = bezierPoints[bezierPoints.length/2 + 1];
        Vector2D tipDirection = labelPosition.diff(bezierPoints[bezierPoints.length/2]);
        Vector2D tip1 = tipDirection.rotate(toRadians(properties.getValue("TIP_ANGLE").getDouble())).scale(properties.getValue("VERTEX_SIZE").getInt()/2).negate();
        Vector2D tip2 = tipDirection.rotate(2 * Math.PI - toRadians(properties.getValue("TIP_ANGLE").getDouble())).scale(properties.getValue("VERTEX_SIZE").getInt()/2).negate();
        return (gr -> {
            gr.setColor(edge.getColor());
            GraphicsHelper.drawPolyLine(gr, bezierPoints);
//            gr.setColor(Color.red);
//            GraphicsHelper.drawPolyLine(gr, controlPoints);
            gr.setColor(Color.gray);
            GraphicsHelper.drawLineInDirection(gr, labelPosition, tip1);
            GraphicsHelper.drawLineInDirection(gr, labelPosition, tip2);
            gr.setColor(Color.black);
            gr.drawString(edge.getValue(), (int)labelPosition.x, (int)labelPosition.y);
        });
    }

    public void stop() {
        stop = true;
    }
    
    private double toRadians(double degree) {
        return degree / 180 * Math.PI;
    }

    public void updateGraph(Graph g) {
        this.g = GraphFactory.createListGraph(g);
        update = true;
    }
    
    public void updateProperties(Settings properties) {
        this.properties = properties;
        update = true;
    }
    
    private void repaintScreen() {
        if (buffer == null) {
            return;
        }
        Graphics renderGraphics = buffer.getGraphics();
        renderGraphics.setColor(backgroundColor);
        renderGraphics.fillRect(0, 0, properties.getValue("CANVAS_SIZE").getInt(), properties.getValue("CANVAS_SIZE").getInt());
        draw(renderGraphics);
        renderGraphics.dispose();
        Graphics g = this.getGraphics();
        g.drawImage(buffer, 0, 0, null);
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
    
    public void draw(Graphics g) {
        if (vertices.size() == 0) {
            g.setColor(Color.black);
            for (Vector2D point : coordinates) {
                if (point != null)
                    g.drawOval((int)point.x - properties.getValue("VERTEX_SIZE").getInt(), 
                               (int)point.y - properties.getValue("VERTEX_SIZE").getInt(), 
                               2*properties.getValue("VERTEX_SIZE").getInt(), 
                               2*properties.getValue("VERTEX_SIZE").getInt());
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
