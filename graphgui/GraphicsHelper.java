package graphgui;

import java.awt.*;

public class GraphicsHelper {
    
    public static void drawLine(Graphics g, Vector2D point, Vector2D direction) {
        g.drawLine((int) point.x, (int) point.y, (int) (point.x + direction.x), (int) (point.y + direction.y));
    }
}
