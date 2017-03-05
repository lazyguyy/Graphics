package graphgui.curves;

import graphgui.*;

public class DeCasteljau {
    public static Vector2D[] calculateBezierPoints(Vector2D[] controlPoints, int accuracy) {
        double ratio = 1./accuracy;
        Vector2D[] bezierPoints = new Vector2D[accuracy + 1];
        for (int i = 0; i <= accuracy; i++) {
            bezierPoints[i] = calculateBezierPoint(controlPoints, ratio*i);
        }
        return bezierPoints;
    }
    
    public static Vector2D calculateBezierPoint(Vector2D[] controlPoints, double ratio) {
        if (controlPoints.length == 1) {
            return controlPoints[0];
        }
        Vector2D[] newControlPoints = new Vector2D[controlPoints.length - 1];
        for (int i = 0; i < newControlPoints.length; i++) {
            newControlPoints[i] = controlPoints[i].scaleBy(1 - ratio).add(controlPoints[i + 1].scaleBy(ratio));
        }
        return calculateBezierPoint(newControlPoints, ratio);
            
    }
}
