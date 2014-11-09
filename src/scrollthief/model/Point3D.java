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
    
    public Point3D mult(double scalar){
    	return new Point3D(x*scalar, y*scalar, z*scalar);
    }

    public double length() 
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

    public void Normalize() 
    {
        Double denominator = Math.sqrt(x*x+y*y+z*z);
        x/=denominator;
        y/=denominator;
        z/=denominator;
    }
    
    public double distanceToLine(Point2D start, Point2D end){
    	double length_sqd = (Math.pow(end.getX() - start.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));
    		        
    	double t = ((z - start.getX()) * (end.getX() - start.getX()) + (x - start.getY()) * (end.getY() - start.getY())) / length_sqd;
    	if (t < 0) return distanceToPoint(start);
    	if (t > 1) return distanceToPoint(end);
    	// if (t < 0 || t > 1) return 9001;
    		        
    	return distanceToPoint(new Point2D.Double(start.getX() + t * (end.getX() - start.getX()),
    			start.getY() + t * (end.getY() - start.getY())) );
    }
    
    public double distanceToPoint(Point2D point){
    	return Math.sqrt(Math.pow(point.getX() - z, 2) + Math.pow(x - point.getY(), 2));
    }
    
}
