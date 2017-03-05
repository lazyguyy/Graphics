package graphgui;

import java.awt.*;
import java.awt.event.*;
import java.util.function.*;
import java.util.*;

public class Shape implements MouseMotionListener {
    private Color mainColor;
    private String value;
    private ArrayList<Shape> hover;
    private boolean hovered;
    private Polygon model;
    private Consumer<Graphics> painter;

    public Shape(String value, Polygon model) {
        mainColor = Color.black;
        this.value = value;
        this.model = model;
    }
    
    public Shape(Shape s) {
        mainColor = s.mainColor;
        value = s.value;
        hover = s.hover;
        model = s.model;
        painter = s.painter;
    }
    
    public void addHover(Shape s) {
        if (hover == null)
            hover = new ArrayList<Shape>();
        hover.add(s);
    }
    
    public void setColor(Color mainColor) {
        this.mainColor = mainColor;
    }

    public void setPainter(Consumer<Graphics> painter) {
        this.painter = painter;
    }

    public void draw(Graphics2D g) {
        if (hovered && hover != null) {
            for (Shape h : hover) {
                h.draw(g);
            }
        }
        g.setColor(mainColor);
        painter.accept(g);
    }

    public Color getColor() {
        return mainColor;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (model != null)
            hovered = model.contains(e.getX(), e.getY());
        
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }
}
