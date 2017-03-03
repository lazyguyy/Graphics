package graphgui;

import java.awt.*;

public class GraphicsHelper {
    public static final double TIP_LENGTH = 15;
    public static final double TIP_ANGLE = Math.PI / 4;

    public static void drawArrowTip(Graphics g, Vector2D point, Vector2D direction) {
        Vector2D tip1 = direction.rotate(TIP_ANGLE).scale(TIP_LENGTH).negate();
        Vector2D tip2 = direction.rotate(2 * Math.PI - TIP_ANGLE).scale(TIP_LENGTH).negate();
        drawLine(g, point, tip1);
        drawLine(g, point, tip2);
    }

    public static void drawLine(Graphics g, Vector2D point, Vector2D direction) {
        g.drawLine((int) point.x, (int) point.y, (int) (point.x + direction.x), (int) (point.y + direction.y));
    }
}
