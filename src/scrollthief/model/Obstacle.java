package scrollthief.model;

import java.awt.geom.Point2D;

public class Obstacle {
	Model model;
	public boolean blocksVision;
	Point2D[][] hitBox;
	
	public Obstacle(Model model, boolean blocksVision, double boxWidth, double boxLength){
		this.model= model;
		this.blocksVision= blocksVision;
		Point2D[] boxPoints= buildBox(boxWidth, boxLength);
		hitBox= GameModel.boxToWorld(model, boxPoints);
	}
	
	// creates the list of hitbox edges that correspond to the given 4 points
//	private void createHitBox(Point2D p1, Point2D p2, Point2D p3, Point2D p4){
//		hitBox[0][0].setLocation(p1.getX(), p1.getY());
//		hitBox[0][1].setLocation(p2.getX(), p2.getY());
//		
//		hitBox[1][0].setLocation(p2.getX(), p2.getY());
//		hitBox[1][1].setLocation(p3.getX(), p3.getY());
//		
//		hitBox[2][0].setLocation(p3.getX(), p3.getY());
//		hitBox[2][1].setLocation(p4.getX(), p4.getY());
//		
//		hitBox[3][0].setLocation(p4.getX(), p4.getY());
//		hitBox[3][1].setLocation(p1.getX(), p1.getY());
//	}
	
	private Point2D[] buildBox(double width, double length){
		// these are really half the width and height of the box---dividing everything by 2 would be silly
		Point2D[] points= new Point2D[4];
		
		points[0]= new Point2D.Double(-width, length);
		points[1]= new Point2D.Double(width, length);
		points[2]= new Point2D.Double(width, -length);
		points[3]= new Point2D.Double(-width, -length);
		
		return points;
	}
	
	// calculate if the given line hits this obstacle
	public boolean boxHit(Point3D p1, Point3D p2){
		// return boxHit(new Point2D.Double(p1.z, p1.x), new Point2D.Double(p2.z, p2.x));
		return boxHit(p1.to2D(), p2.to2D());
	}
	
	public boolean boxHit(Point2D p1, Point2D p2){
		// I found a way to test for intersection using only -, *, &&, and comparisons. Much faster.
		for (int i= 0; i < 4; i++){ // test each edge for an intersection with the line
			Point2D p3= hitBox[i][0];
			Point2D p4= hitBox[i][1];
			
			if ( (CCW(p1, p3, p4) != CCW(p2, p3, p4)) && (CCW(p1, p2, p3) != CCW(p1, p2, p4)) )
				return true;
		}
		
		return false;
	}
	
	private boolean CCW(Point2D p1, Point2D p2, Point2D p3) {
		double a = p1.getX(); double b = p1.getY(); 
		double c = p2.getX(); double d = p2.getY();
		double e = p3.getX(); double f = p3.getY();
		return (f - b) * (c - a) > (d - b) * (e - a);
	}
	
	public Point3D getLoc(){
		return model.getLoc();
	}
}
