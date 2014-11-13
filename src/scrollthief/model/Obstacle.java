package scrollthief.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Obstacle {
	Model model;
	public boolean isLow;
	Point2D[][] hitBox;
	double boxWidth, boxLength, height;
	
	public Obstacle(Model model, boolean isLow, double boxLength, double boxWidth, double height){
		this.model= model;
		this.isLow= isLow;
		Point2D[] boxPoints= GameModel.findPoints(boxLength, boxWidth);
		hitBox= GameModel.boxToWorld(model, boxPoints);
		this.boxWidth= boxWidth;
		this.boxLength= boxLength;
		this.height= height;
	}
	
//	public boolean collision(Point2D[][] box){
	public ArrayList<Point2D[]> collision(Point2D[][] box){
		ArrayList<Point2D[]> edges = new ArrayList<Point2D[]>();
		for (int i= 0; i < 4; i++){
			boxHit(box[i][0], box[i][1], edges) ;
			
		}

		return edges;
	}
	
	// calculate if the given line hits this obstacle
	public ArrayList<Point2D[]> boxHit(Point3D p1, Point3D p2){
		ArrayList<Point2D[]> edges= new ArrayList<Point2D[]>();
		return boxHit(p1.to2D(), p2.to2D(), edges);
	}
//	public boolean boxHit(Point3D p1, Point3D p2){
//		return boxHit(p1.to2D(), p2.to2D());
//	}
	
	public ArrayList<Point2D[]> boxHit(Point2D p1, Point2D p2, ArrayList<Point2D[]> edges){
		// I found a way to test for intersection using only -, *, &&, and comparisons. Much faster.
		for (int i= 0; i < 4; i++){ // test each edge for an intersection with the line
			Point2D p3= hitBox[i][0];
			Point2D p4= hitBox[i][1];
			
			if ( (CCW(p1, p3, p4) != CCW(p2, p3, p4)) && (CCW(p1, p2, p3) != CCW(p1, p2, p4)) )
				edges.add( new Point2D[] {p3, p4});
		}
		return edges;
	}
	
	private boolean CCW(Point2D p1, Point2D p2, Point2D p3) {
		double a = p1.getX(); double b = p1.getY(); 
		double c = p2.getX(); double d = p2.getY();
		double e = p3.getX(); double f = p3.getY();
		return (f - b) * (c - a) > (d - b) * (e - a);
	}
	
	public boolean isInBox(Point3D point){
		Point2D flatPoint= new Point2D.Double(point.z, point.x);
		Point2D objPoint= worldToObject(flatPoint);
		//say("flatPoint: "+flatPoint.toString()+", objPoint: "+objPoint.toString());
		
		if (objPoint.getX() > -boxLength && objPoint.getX() < boxLength
				&& objPoint.getY() > -boxWidth && objPoint.getY() < boxWidth)
			return true;
		
		return false;
	}
	
	public Point2D worldToObject(Point2D point){
		Point2D objPoint= new Point2D.Double();
		Point3D center= getLoc();
		double angle= model.getAngle();
		
		AffineTransform worldToObj= new AffineTransform();
		AffineTransform translate= new AffineTransform(1,0,0,1, -center.z, -center.x); 
		AffineTransform rotate= new AffineTransform(Math.cos(angle), -Math.sin(angle), 
				Math.sin(angle), Math.cos(angle), 0, 0);
		
		worldToObj.preConcatenate(translate);
		worldToObj.preConcatenate(rotate);
		worldToObj.transform(point, objPoint);
		
		//say("Transformed to point "+objPoint.toString());
		return objPoint;
	}
	
	public double getHeight(){
		return height;
	}
	
	public Point3D getLoc(){
		return model.getLoc();
	}
	
	public double[] getBoxDimensions(){
		double[] dim= {boxLength, boxWidth};
		return  dim;
	}
	
	@SuppressWarnings("unused")
	private void say(String message){
		System.out.println(message);
	}
}
