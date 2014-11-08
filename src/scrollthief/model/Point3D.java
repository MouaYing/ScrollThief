package scrollthief.model;

import java.awt.geom.Point2D;

public class Point3D 
{
    public double x;
    public double y;
    public double z;
    
    public Point3D(double newX, double newY, double newZ)
    {
        x = newX;
        y = newY;
        z = newZ;
    }
    
    public final double dot(Point3D B) {
        return (x*B.x + y*B.y + z*B.z);
    }
    
    public final Point3D minus(Point3D B){
    	double newX = x - B.x;
    	double newY = y - B.y;
    	double newZ = z - B.z;
    	
    	return new Point3D(newX, newY, newZ);
    }

    double length() 
    {
        return Math.sqrt(x*x+y*y+z*z);
    }
    
    @Override
    public String toString()
    {
        return "X: "+x+", Y: "+y+", Z:"+z;
    }
    
    public Point2D to2D(){
    	return new Point2D.Double(z, x);
    }

    void Normalize() 
    {
        Double denominator = Math.sqrt(x*x+y*y+z*z);
        x/=denominator;
        y/=denominator;
        z/=denominator;
    }
    
    
}
