package scrollthief.model;

import java.awt.geom.Point2D;

public class Obstacle {
	Model model;
	public boolean blocksVision;
	Point2D[][] hitBox;
	
	public Obstacle(Model model, boolean blocksVision, double boxWidth, double boxLength){
		this.model= model;
		this.blocksVision= blocksVision;
		Point2D[] boxPoints= GameModel.findPoints(boxWidth, boxLength);
		hitBox= GameModel.boxToWorld(model, boxPoints);
	}
	
//	public boolean collision(Point2D[][] box){
	public Point2D[] collision(Point2D[][] box){
		Point2D[] edge = null;
		for (int i= 0; i < 4; i++){
			edge= boxHit(box[i][0], box[i][1]);
			if (edge != null)
				return edge;
//			if (boxHit(box[i][0], box[i][1]))
//				return true;		
		}
//		return false;
		return edge;
	}
	
	// calculate if the given line hits this obstacle
	public Point2D[] boxHit(Point3D p1, Point3D p2){
		return boxHit(p1.to2D(), p2.to2D());
	}
//	public boolean boxHit(Point3D p1, Point3D p2){
//		return boxHit(p1.to2D(), p2.to2D());
//	}
	
	public Point2D[] boxHit(Point2D p1, Point2D p2){
		// I found a way to test for intersection using only -, *, &&, and comparisons. Much faster.
		Point2D[] edge = null;
		for (int i= 0; i < 4; i++){ // test each edge for an intersection with the line
			Point2D p3= hitBox[i][0];
			Point2D p4= hitBox[i][1];
			
			if ( (CCW(p1, p3, p4) != CCW(p2, p3, p4)) && (CCW(p1, p2, p3) != CCW(p1, p2, p4)) )
				edge= new Point2D[] {p3, p4};
		}
		return edge;
	}
	
//	public boolean boxHit(Point2D p1, Point2D p2){
//		// I found a way to test for intersection using only -, *, &&, and comparisons. Much faster.
//		for (int i= 0; i < 4; i++){ // test each edge for an intersection with the line
//			Point2D p3= hitBox[i][0];
//			Point2D p4= hitBox[i][1];
//			
//			if ( (CCW(p1, p3, p4) != CCW(p2, p3, p4)) && (CCW(p1, p2, p3) != CCW(p1, p2, p4)) )
//				return true;
//		}
//		return false;
//	}
	
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
