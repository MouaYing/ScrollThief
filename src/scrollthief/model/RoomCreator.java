package scrollthief.model;

import java.util.List;

public class RoomCreator {
	
	private Resource resource;
	private GameModel game;
	private int wallCount;
	private double wallDepth = 1;
	private double wallLength = 7;
	public RoomCreator(Resource resource, GameModel game){
		this.resource = resource;
		this.game = game;
		wallCount = 0;
	}
	
	public void createRoom(List<Model> model, int width, int height, List<WallLocation> openings, Point3D center){
		double scaleNS = 3/(width/this.wallLength);
		double scaleWE = 3/(height/this.wallLength);
		double zNorth = center.z + height/2 + wallDepth/2;
		double zSouth = center.z - height/2 - wallDepth/2;
		double xWest = center.x + width/2 + wallDepth/2;
		double xEast = center.x + width/2 + wallDepth/2;
		Point3D northCenter = new Point3D(center.x, 0, zNorth);
		Point3D southCenter = new Point3D(center.x, 0, zSouth);
		Point3D eastCenter = new Point3D(xEast, 0, center.z);
		Point3D westCenter = new Point3D(xWest, 0, center.z);
		addWallSide("North", scaleNS, model, openings, center, northCenter,width/3,width/2);
		addWallSide("South", scaleNS, model, openings, center, southCenter,width/3,width/2);
		addWallSide("East", scaleWE, model, openings, center, eastCenter,height/3,height/2);
		addWallSide("West", scaleWE, model, openings, center, westCenter,height/3,height/2);
	}

	private void addWallSide(String side, double scaleNS, List<Model> model,
			List<WallLocation> openings, Point3D center, Point3D wallCenter, int offset, int half) {
		Point3D otherCenter;
		double[] orientation = game.rtAngle();
		WallLocation main = WallLocation.valueOf(side);
		WallLocation left = WallLocation.valueOf(side + "North");
		WallLocation right = WallLocation.valueOf(side + "South");
		if(main == WallLocation.North||main == WallLocation.South){
			left = WallLocation.valueOf(side + "East");
			right = WallLocation.valueOf(side + "West");
			orientation = game.zero();
			
		}
		if(openings.contains(main)){
			if(openings.contains(left) && openings.contains(right)){
				return;
			}
			else {
				if(openings.contains(left)){
					//model.add(new Model(resource.getOBJs()[5], 5, getCenter(wallCenter,main,right,offset,half), orientation, scaleNS, 2));				
				}
				else if(openings.contains(right)){
					//model.add(new Model(resource.getOBJs()[5], 5, getCenter(wallCenter,main,left,offset,half), orientation, scaleNS, 2));
				}
				else {
					model.add(new Model(resource.getOBJs()[5], 6, wallCenter, orientation, scaleNS, 3));
				}
			}
		}
		else{
			if(openings.contains(left) && openings.contains(right)){
				model.add(new Model(resource.getOBJs()[5], 5, wallCenter, orientation, scaleNS, 1));
			}
			else {
				if(openings.contains(left)){
					model.add(new Model(resource.getOBJs()[5], 5, getDoubleCenter(wallCenter,main,right,offset,half), orientation, scaleNS, 2));				
				}
				else if(openings.contains(right)){
					model.add(new Model(resource.getOBJs()[5], 5, getDoubleCenter(wallCenter,main,left,offset,half), orientation, scaleNS, 2));
				}
				else {
					model.add(new Model(resource.getOBJs()[5], 6, wallCenter, orientation, scaleNS, 3));
				}
			}
		}
		
		
	}
	
	private Point3D getSingleCenter(Point3D normalCenter, WallLocation main, WallLocation other, double offset, double half){
		Point3D newCenter;
		if(main == WallLocation.North || main == WallLocation.South){
			if(other == WallLocation.NorthEast || other == WallLocation.SouthEast){
				newCenter = new Point3D(normalCenter.x, 0, normalCenter.z - wallLength);
			}
			else {

				newCenter = new Point3D(normalCenter.x, 0, normalCenter.z + wallLength);
			}
		}
		else {
			if(other == WallLocation.EastNorth || other == WallLocation.WestNorth){
				newCenter = new Point3D(normalCenter.x + wallLength, 0, normalCenter.z);
			}
			else {
				newCenter = new Point3D(normalCenter.x - wallLength, 0, normalCenter.z);
			}
		}
		return newCenter;
	}
	
	private Point3D getDoubleCenter(Point3D normalCenter, WallLocation main, WallLocation other, double offset, double half){
		Point3D newCenter;
		if(main == WallLocation.North || main == WallLocation.South){
			if(other == WallLocation.NorthEast || other == WallLocation.SouthEast){
				newCenter = new Point3D(normalCenter.x, 0, normalCenter.z - half + offset);
			}
			else {

				newCenter = new Point3D(normalCenter.x, 0, normalCenter.z + half - offset);
			}
		}
		else {
			if(other == WallLocation.EastNorth || other == WallLocation.WestNorth){
				newCenter = new Point3D(normalCenter.x + half - offset, 0, normalCenter.z);
			}
			else {
				newCenter = new Point3D(normalCenter.x - half + offset, 0, normalCenter.z);
			}
		}
		return newCenter;
	}
	
	
}
