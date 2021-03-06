package lando.systems.ld40.spline;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;

public class SinglePointSplineIterator extends SplineIterator {
    
    public SinglePointSplineIterator(CatmullRomSpline<Vector2> spline, SplinePosition startPos, SplinePosition endPos, Precision precision) {
        super(spline, startPos, endPos, precision);
    }
    public SinglePointSplineIterator(CatmullRomSpline<Vector2> spline, Precision precision) {
        super(spline, precision);
    }

    public SplinePoint getNext() {
        if(!end) {
            currentPoint = getPoint(currentPos.span, currentPos.t);
            step(currentPrecision);
            return currentPoint;
        } else
            return null;
    }
}
