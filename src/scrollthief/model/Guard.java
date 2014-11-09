/**
 * This class is meant to represent a guard, and calculate information like its field of vision and
 * blind spots
 * 
 */

package scrollthief.model;

public class Guard extends Character{
	double sightRange= 10; // needs tuning

	public Guard(GameModel gameModel, Model model, double boxLength, double boxWidth){
		super(gameModel, model, boxLength, boxWidth);
	}
	
	public boolean isNear(){
		
		Point3D loc= model.getLoc();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		
		double distance= ninjaLoc.minus(loc).length();
		
		if (distance < sightRange)
			return true;
		
		return false;
	}
	
	public boolean isFacingNinja(){
		float fov= 1.4f; // (radians) needs tuning
		Point3D loc= getLoc();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		double guardAngle= getAngle();
		double angToNinja= Math.atan2(ninjaLoc.x - loc.x, ninjaLoc.z - loc.z);
		
		//say("Guard angle: "+guardAngle+" Angle to Ninja: "+angToNinja);
		
		// find the smallest difference between the angles
		double angDif = (angToNinja - guardAngle);
		angDif = gameModel.floorMod((angDif + Math.PI),(2 * Math.PI)) - Math.PI; 
		//say("Angle difference is: "+angDif);
		
		// check if this angle difference is small enough that the ninja would be in guard's FOV
		if (angDif > -fov && angDif < fov)
			return true;
		
		return false;
	}
	
	public boolean canSeeNinja(){
		Point3D guardLoc= getLoc();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		Obstacle[] obstacles= gameModel.getObstacles();
		// Point3D dirToNinja= ninjaLoc.minus(guardLoc); // probably need to normalize
		
		for (int i= 0; i < obstacles.length; i++){
			Obstacle obs= obstacles[i];
			if ((obs.getLoc().minus(guardLoc)).length() > sightRange)
				continue;
			
			if (obs.boxHit(guardLoc, ninjaLoc) != null) // see if the line between guard and ninja crosses hitbox
				return false;
		}
		
		return true;
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
