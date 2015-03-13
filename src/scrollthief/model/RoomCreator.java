package scrollthief.model;

import java.util.List;
import java.util.Map;

public class RoomCreator {
	
	private Resource resource;
	private GameModel game;
	private double wallDepth = 1;
	private double wallLength = 7.7;
	public RoomCreator(Resource resource, GameModel game){
		this.resource = resource;
		this.game = game;
	}
	
	// wallLengths are -2, -1, -.5, .5, 1, 2 positives are walls, negatives are openings.
	// widthWallNumber && heightWallNumber must mastch the number of items in wallLengths
	
	public void createRoom(List<Model> model, Room room){
		createRoom(model, room.getWidthWallNumber(), room.getHeightWallNumber(), room.getWallLengths(), room.getCenter());
	}
	
	private void createRoom(List<Model> model, double widthWallNumber, double heightWallNumber, Map<WallLocation,double[]> wallLengths, Point3D center){
		double width = widthWallNumber * wallLength;
		double height = heightWallNumber * wallLength;
		double offModifier = 1.5;
		Point3D topLeftCorner = new Point3D(center.x + width/2,0,center.z + (height+wallDepth*offModifier)/2);
		Point3D bottomLeftCorner = new Point3D(center.x + width/2,0,center.z - (height+wallDepth*offModifier)/2);
		Point3D bottomLeftCorner2 = new Point3D(center.x + (width+wallDepth*offModifier)/2,0,center.z - height/2);
		Point3D bottomRightCorner = new Point3D(center.x - (width+wallDepth*offModifier)/2,0,center.z - height/2);
		addWalls(model, wallLengths.get(WallLocation.North),WallLocation.North, topLeftCorner);
		addWalls(model, wallLengths.get(WallLocation.South),WallLocation.South, bottomLeftCorner);
		addWalls(model, wallLengths.get(WallLocation.West),WallLocation.West, bottomLeftCorner2);
		addWalls(model, wallLengths.get(WallLocation.East),WallLocation.East, bottomRightCorner);
	}
	
	public void addWalls(List<Model> model, double[] wallLengths, WallLocation location, Point3D start){
		for(int i = 0; i < wallLengths.length;i++){
			Point3D wallCenter = getCenter(start, location, i, wallLengths);
			if(wallLengths[i] > 0){
				model.add(new Model(resource.getOBJs()[5], wallTexture(wallLengths[i]),
						wallCenter, getOrientation(location), 1, wallLengths[i]));
			}
		}
	}
	public double[] getOrientation(WallLocation location){

		if(location == WallLocation.North || location == WallLocation.South){
			return game.zero();
		}
		return game.rtAngle();
	}
	
	
	public int wallTexture(double value){
		if(value == .5){
			return 7;
		}
		else if(value == 1){
			return 5;
		}
		return 6;
	}
	
	public Point3D getCenter(Point3D start, WallLocation location, double wallNumber, double[] wallLengths){
		double x = start.x;
		double z = start.z;
		int loc = 0;
		if(location == WallLocation.North || location == WallLocation.South){
			x -= (wallLength*Math.abs(wallLengths[loc]))/2;
			loc++;
			while(loc <= wallNumber){
				double add = getAdd(Math.abs(wallLengths[loc-1]), Math.abs(wallLengths[loc]));
				x -= (add);
				loc++;
			}
		}
		else {
			z += (wallLength*Math.abs(wallLengths[loc]))/2;
			loc++;
			while(loc <= wallNumber){
				double add = getAdd(Math.abs(wallLengths[loc-1]), Math.abs(wallLengths[loc]));
				z += (add);
				loc++;
			}
		}
		return new Point3D(x,0,z);
	}
	
	public double getAdd(double previousWallLength, double curWallLength){
		return (((wallLength*previousWallLength)/2) + ((wallLength*curWallLength)/2));
	}
	
	
}
