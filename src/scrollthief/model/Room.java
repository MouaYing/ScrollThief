package scrollthief.model;

import java.util.HashMap;
import java.util.Map;

public class Room {
	private double widthWallNumber;
	private double heightWallNumber;
	Map<WallLocation,double[]> wallLengths;
	private Point3D center;
	public Room(Point3D center){
		this.widthWallNumber = 0;
		this.heightWallNumber = 0;
		wallLengths = new HashMap<WallLocation, double[]>();
		double[] north = new double[0];
		double[] south = new double[0];
		double[] east = new double[0];
		double[] west = new double[0];
		
		wallLengths.put(WallLocation.North, north);
		wallLengths.put(WallLocation.South, south);
		wallLengths.put(WallLocation.East, east);
		wallLengths.put(WallLocation.West, west);
		this.center = center;
	}
	
	public void setWallLengths(WallLocation loc, double[] lengths){
		if(loc == WallLocation.North || loc == WallLocation.South){
			//see if wall number is greater than what is here
			double wallNumber = countLengths(lengths);
			if(wallNumber > widthWallNumber){
				widthWallNumber = wallNumber;
			}
		}
		else {
			double wallNumber = countLengths(lengths);
			if(wallNumber > heightWallNumber){
				heightWallNumber = wallNumber;
			}
		}
		wallLengths.put(loc, lengths);
	}
	
	public double countLengths(double[] lengths){
		double sum = 0;
		for(int i = 0; i < lengths.length; i++){
			sum += Math.abs(lengths[i]);
		}
		return sum;
	}

	public double getWidthWallNumber() {
		return widthWallNumber;
	}

	public double getHeightWallNumber() {
		return heightWallNumber;
	}

	public Map<WallLocation, double[]> getWallLengths() {
		return wallLengths;
	}

	public Point3D getCenter() {
		return center;
	}
	
	
	
}
