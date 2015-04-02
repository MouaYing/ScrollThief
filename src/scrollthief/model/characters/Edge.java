package scrollthief.model.characters;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import scrollthief.model.Point3D;

public class Edge {
	
	public Line2D line;
	public Point3D normal;

	public Edge(Point2D startPoint, Point2D endPoint) {
		// TODO Auto-generated constructor stub
		line = new Line2D.Double(startPoint, endPoint);
	}
	
	public void calculate_normal() {
		double dx = line.getX2() - line.getX1();
		double dy = line.getY2() - line.getY1();
		normal = new Point3D(-dy, 0, dx);
		normal.Normalize();
	}

}
