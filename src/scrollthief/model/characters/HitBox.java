package scrollthief.model.characters;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import scrollthief.model.Model;
import scrollthief.model.Point3D;

public class HitBox {
	ArrayList<Edge> edges;
	private Point2D[] boxPoints;
	public HitBox(double boxLength, double boxWidth, double angle, Point3D loc){
		edges = new ArrayList<Edge>();
		boxPoints= findPoints(boxLength, boxWidth);
		createNewHitBox(angle, loc);
	}
	
	public HitBox(Point2D[] boxPoints){
		createNewHitBox(boxPoints);
	}
	
	public void calculateNormals() {
		for(Edge e : edges) {
			e.calculate_normal();
		}
	}
	
	public void calculateNewHitBox(Model model){
		createNewHitBox(model.getAngle(), model.getLoc());
	}

	public void createNewHitBox(double angle, Point3D loc) {
		Point2D center= loc.to2D();
		//Point2D.Double[] points= new Point2D.Double[4];
		
		Point2D.Double newP1= new Point2D.Double();
		Point2D.Double newP2= new Point2D.Double();
		Point2D.Double newP3= new Point2D.Double();
		Point2D.Double newP4= new Point2D.Double();
		
		// construct obj to world transformation matrix
		AffineTransform objToWorld= 
				new AffineTransform(Math.cos(angle), Math.sin(angle), 
						-Math.sin(angle), Math.cos(angle), center.getX(), center.getY());
		
		// transform each point
		objToWorld.transform(boxPoints[0], newP1);
		objToWorld.transform(boxPoints[1], newP2);
		objToWorld.transform(boxPoints[2], newP3);
		objToWorld.transform(boxPoints[3], newP4);
		
		createNewHitBox(newP1, newP2, newP3, newP4);
	}
	
	// creates the list of hitbox edges that correspond to the given 4 points
	private void createNewHitBox(Point2D[] points){
		createNewHitBox(points[0], points[1], points[2], points[3]);
	}
	
	public void createNewHitBox(Point2D p1, Point2D p2, Point2D p3, Point2D p4){
		edges.clear();

		Edge edge = new Edge(p1, p2);
		edges.add(edge);
		edge = new Edge(p2, p3);
		edges.add(edge);
		edge = new Edge(p3, p4);
		edges.add(edge);
		edge = new Edge(p4, p1);
		edges.add(edge);
		
		calculateNormals();
	}
	
	public Point2D[] findPoints(double width, double length){
		// these are really half the width and height of the box---dividing everything by 2 would be silly
		Point2D[] points= new Point2D[4];
		
		points[0]= new Point2D.Double(-width, length);
		points[1]= new Point2D.Double(width, length);
		points[2]= new Point2D.Double(width, -length);
		points[3]= new Point2D.Double(-width, -length);
		
		return points;
	}

	public Point2D[] getBoxPoints() {
		return boxPoints;
	}
	
	public ArrayList<Edge> getCollidedEdges(HitBox otherHitBox){
		ArrayList<Edge> collidedEdges = new ArrayList<Edge>();
		for(Edge edge : this.edges){
			for(Edge otherEdge : otherHitBox.edges){
				Line2D edgeLine = edge.line;
				Line2D otherEdgeLine = otherEdge.line;
				if(edgeLine.intersectsLine(otherEdgeLine) && !collidedEdges.contains(otherEdge)){
					collidedEdges.add(otherEdge);
				}
			}
		}
		return collidedEdges;
	}
	
	public static ArrayList<Edge> getCollidedEdges(Line2D line, HitBox hitbox) {
		ArrayList<Edge> collidedEdges = new ArrayList<Edge>();
		for(Edge otherEdge : hitbox.edges){
			Line2D edgeLine = line;
			Line2D otherEdgeLine = otherEdge.line;
			if(edgeLine.intersectsLine(otherEdgeLine) && !collidedEdges.contains(otherEdge)){
				collidedEdges.add(otherEdge);
			}
		}
		return collidedEdges;
	}
	
	@Override
	public String toString() {
		StringBuilder hitbox = new StringBuilder("HitBox:\n");
		int count = 0;
		for (Edge e : edges) {
			hitbox.append("Line " + count++ + ": P1 (" + e.line.getX1() + "," + e.line.getY1() + ") , "
					+ "P2 ("  + e.line.getX2() + "," + e.line.getY2() + ")\n");
		}
		return hitbox.toString();
	}
}
