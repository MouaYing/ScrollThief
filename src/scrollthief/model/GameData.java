package scrollthief.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import scrollthief.model.levels.LevelState;

public class GameData implements Serializable  {
	private List<GamePoint> locations;
	private GameState state;
	private LevelState level;

	public GameData(List<Point3D> locations, GameState state, LevelState level) {
		this.locations = new ArrayList<GamePoint>();
		convertPoints(locations);
		this.state = state;
		this.level = level;
	}

	public GameData() {
		locations = new ArrayList<GamePoint>();
		//ninja starting location for level 1
		locations.add(new GamePoint(new Point3D(0, 0, -5)));
		//boss starting location for level 1
		locations.add(new GamePoint(new Point3D(0,0,76)));
		state = GameState.Playing;
		//start at level 1
		level = LevelState.Level0;
	}
	
	private void convertPoints(List<Point3D> locations){
		for(Point3D point : locations){
			this.locations.add(new GamePoint(point));
		}
	}

	public List<Point3D> getLocations() {
		List<Point3D> locations3D = new ArrayList<Point3D>();
		for(GamePoint point : locations){
			locations3D.add(point.convertToPoint3D());
		}
		return locations3D;
	}

	public GameState getState() {
		return state;
	}

	public LevelState getLevel() {
		return level;
	}
	
	
	class GamePoint implements Serializable {
		double x;
		double y;
		double z;
		public GamePoint(Point3D point){
			this.x = point.x;
			this.y = point.y;
			this.z = point.z;
		}
		
		public Point3D convertToPoint3D(){
			return new Point3D(x,y,z);
		}
	}
	
}
