package graphgui;

import java.awt.*;
import java.awt.event.*;
import java.util.function.*;

public class Shape implements MouseMotionListener {
    private Color mainColor;
    private String value;
    private Shape hover;
    private boolean hovered;
    private Polygon model;
    private Consumer<Graphics> painter;

    public Shape(String value, Polygon model) {
        mainColor = Color.black;
        this.value = value;
        this.model = model;
    }

    public void setPainter(Consumer<Graphics> painter) {
        this.painter = painter;
    }

    public void draw(Graphics g) {
        painter.accept(g);
        if (hovered && hover != null)
            hover.draw(g);
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
