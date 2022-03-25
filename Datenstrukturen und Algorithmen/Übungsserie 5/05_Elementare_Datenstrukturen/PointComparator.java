/**
 * File: PointComparator.java
 * Compares two awt.Point objects along a given axis
 *
 */
import java.awt.Point;

public class PointComparator implements java.util.Comparator<Point>
{
    /**
     * Dimension to compare: 0:x, 1:y
     */
    int dimension;

    public PointComparator(int d){
        this.dimension = d;
    }

    /** Vergleicht Objekt a mit Objekt b und
     *  liefert -1 (wenn a<b), 0 (wenn a=b) oder +1 (wenn a>b) */
    public int compare(Point a, Point b) {
        return Long.signum(signedDistance(a,b));
    }

    public long signedDistance(Point a, Point b)
    {
        // cast nach long vermeidet int overflow
        if(dimension == 0){
            return (long)a.x - (long)b.x;
        }else{
            return (long)a.y - (long)b.y;
        }
    }
}
