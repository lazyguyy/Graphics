package graphgui;

import java.awt.*;

public class GraphicsHelper {
    
    public static void drawLineInDirection(Graphics g, Vector2D point, Vector2D direction) {
        g.drawLine((int) point.x, (int) point.y, (int) (point.x + direction.x), (int) (point.y + direction.y));
    }
    
    public static void drawLine(Graphics g, Vector2D point, Vector2D target) {
        g.drawLine((int)point.x, (int)point.y, (int)target.x, (int)target.y);
    }
    
    public static void drawPolyLine(Graphics g, Vector2D[] points) {
        for (int i = 0; i < points.length - 1; i++) {
            drawLine(g, points[i], points[i+1]);
        }
    }
}
