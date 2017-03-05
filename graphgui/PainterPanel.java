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
    private Graph<? extends VertexNameInfo> g;
    private Vector2D[] coordinates;
    private VertexPlacementStrategy strategy;
    private ArrayList<Shape> edges;
    private ArrayList<Shape> vertices;
    private Color backgroundColor;
    private boolean stop, update;
    private BufferedImage buffer;
    private Settings properties;
    private boolean finished;
    
    public PainterPanel(Graph<? extends VertexNameInfo> g, Color backgroundColor) {
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
                	finished = false;
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
        createShapes();
    }
    
    private void createShapes() {
        edges = new ArrayList<Shape>();
        vertices = new ArrayList<Shape>();
    	for (int i = 0; i < g.vertexCount(); i++) {
            Polygon model = new Polygon();
            model.addPoint((int)coordinates[i].x - properties.getValue("VERTEX_SIZE").getInt(), (int)coordinates[i].y - properties.getValue("VERTEX_SIZE").getInt());
            model.addPoint((int)coordinates[i].x + properties.getValue("VERTEX_SIZE").getInt(), (int)coordinates[i].y - properties.getValue("VERTEX_SIZE").getInt());
            model.addPoint((int)coordinates[i].x + properties.getValue("VERTEX_SIZE").getInt(), (int)coordinates[i].y + properties.getValue("VERTEX_SIZE").getInt());
            model.addPoint((int)coordinates[i].x - properties.getValue("VERTEX_SIZE").getInt(), (int)coordinates[i].y + properties.getValue("VERTEX_SIZE").getInt());
            Shape vertex = new Shape(g.info().vertexName(i), model);
            vertex.setPainter(createVertexPainter(vertex, i));
            addMouseMotionListener(vertex);
            vertices.add(vertex);
            // Add all the edges
            for (WeightedEdge e : g.adjacency(i)) {
                Shape edge = new Shape("" + e.weight, null);
                edge.setPainter(createCurvedEdgePainter(edge, e));
                edges.add(edge);
                addMouseMotionListener(edge);
                Shape hoverEdge = new Shape(edge);
                hoverEdge.setColor(Color.red);
                vertex.addHover(hoverEdge);
            }
        }
    	finished = true;
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
    
    private Consumer<Graphics> createCurvedEdgePainter(Shape edge, WeightedEdge e) {
        Vector2D direction = coordinates[e.to].diff(coordinates[e.from]);
        Vector2D controlPoint = coordinates[e.from].add(direction.rotate(Math.PI / 2).scaleBy(properties.getValue("BEZIER_CURVE").getDouble()).add(direction.scaleBy(0.5)));
        Vector2D[] controlPoints = {coordinates[e.from], controlPoint, coordinates[e.to]};
        Vector2D[] bezierPoints = DeCasteljau.calculateBezierPoints(controlPoints, properties.getValue("BEZIER_ACCURACY").getInt());
        Vector2D tipPosition = bezierPoints[bezierPoints.length/2 + 1];
        Vector2D tipDirection = tipPosition.diff(bezierPoints[bezierPoints.length/2]);
        Vector2D tip1 = tipDirection.rotate(toRadians(properties.getValue("TIP_ANGLE").getDouble())).scale(properties.getValue("VERTEX_SIZE").getInt()).negate();
        Vector2D tip2 = tipDirection.rotate(2 * Math.PI - toRadians(properties.getValue("TIP_ANGLE").getDouble())).scale(properties.getValue("VERTEX_SIZE").getInt()).negate();
        Vector2D labelPosition = tipPosition.add(tipDirection.rotate(Math.PI / 2).scale(properties.getValue("VERTEX_SIZE").getInt()));
        return (gr -> {
            Color c = gr.getColor();
            GraphicsHelper.drawPolyLine(gr, bezierPoints);
            gr.setColor(Color.gray);
            GraphicsHelper.drawLineInDirection(gr, tipPosition, tip1);
            GraphicsHelper.drawLineInDirection(gr, tipPosition, tip2);
            gr.setColor(new Color((int)((255 + c.getRed())*0.25), (int)((255 + c.getGreen())*0.25), (int)((255 + c.getBlue())*0.25)));
            gr.drawString(edge.getValue(), (int)(labelPosition.x + tip1.x + tip2.x), (int)(labelPosition.y + tip1.y));
        });
    }

    public void stop() {
        stop = true;
    }
    
    private double toRadians(double degree) {
        return degree / 180 * Math.PI;
    }

    public void updateGraph(Graph<? extends VertexNameInfo> g) {
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
        Graphics2D renderGraphics = (Graphics2D) buffer.getGraphics();
        renderGraphics.setColor(backgroundColor);
        renderGraphics.fillRect(0, 0, properties.getValue("CANVAS_SIZE").getInt(), properties.getValue("CANVAS_SIZE").getInt());
        draw(renderGraphics);
        renderGraphics.dispose();
        Graphics g = this.getGraphics();
        g.drawImage(buffer, 0, 0, null);
        g.dispose();
        Toolkit.getDefaultToolkit().sync();
    }
    
    public void draw(Graphics2D g) {
    	g.setStroke(new BasicStroke(2));
    	g.setFont(new Font("Arial", Font.BOLD, 12));
        if (!finished) {
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
        else {
            for (Shape e : edges) {
                e.draw(g);
            }
            for (Shape v : vertices) {
                v.draw(g);
            }	
        }
    }
}
