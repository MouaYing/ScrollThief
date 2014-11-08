/**
 * This class is meant to represent a guard, and hold information like its field of vision and blind spots
 * This class may be unnecessary if we calculate all of the guard information every frame and don't really 
 * need to store it.
 */

package scrollthief.model;

public class Guard extends Character{

	public Guard(GameModel gameModel, Model model){
		super(gameModel, model);
	}
	
	public boolean isNear(){
		float threshold= 10; // This needs to be tuned
		Point3D loc= model.getLoc();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		
		double distance= ninjaLoc.minus(loc).length();
		
		if (distance < threshold)
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
		angDif =   gameModel.floorMod((angDif + Math.PI),(2 * Math.PI)) - Math.PI; 
		//say("Angle difference is: "+angDif);
		
		// check if this angle difference is small enough that the ninja would be in guard's FOV
		if (angDif > -fov && angDif < fov)
			return true;
		
		return false;
	}
	
	public boolean canSeeNinja(){
		// TODO implement this. Until then, guards have x-ray vision
		Point3D guardLoc= getLoc();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		Point3D dirToNinja= ninjaLoc.minus(guardLoc); // probably need to normalize
		
		return true;
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
