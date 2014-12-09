/**
 * This class is meant to represent a guard, and calculate information like its field of vision and
 * blind spots
 * 
 */

package scrollthief.model;

import java.util.Random;

public class Guard extends Character{
	double sightRange= 10; // needs tuning
	int goalIndex;
	Point3D goal= null;
	Point3D[] waypoints;
	Point3D pointToCheck;
	double tickCount= 0;
	Random rand= new Random();
	boolean isLooking= true;
	boolean waiting= false;
	final double topSpeed= .25;
	int waitTime;
	OBJ[] standing;
	OBJ[] walking;

	public Guard(GameModel gameModel, Model model, double boxLength, double boxWidth, Point3D[] wayPoints){
		super(gameModel, model, boxLength, boxWidth);
		turnRate= .05;
		this.waypoints= wayPoints;
		pointToCheck= wayPoints[0];
		standing= new OBJ[] {model.getObj()};
//		walking= gameModel.getGuardWalk();
		walking= standing; // this is here until we have a walk cycle
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
		float fov= 1.3f; // (radians) needs tuning
		Point3D loc= getLoc();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		double guardAngle= -getAngle();
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
			if ((obs.getLoc().minus(guardLoc)).length() > sightRange || obs.isLow)
				continue;
			
//			if (!obs.boxHit(guardLoc, ninjaLoc).isEmpty()) // see if the line between guard and ninja crosses hitbox
			if (!gameModel.boxHit(guardLoc, ninjaLoc, obs.hitBox).isEmpty()) // see if the line between guard and ninja crosses hitbox	
				return false;
		}
		
		return true;
	}
	
	// Determine what OBJ to use this tick, and set it as model.obj
	public void animate(int tick){
		if (oldSpeed != 0 && speed == 0){ // guard has stopped
			motion= standing;
			animFrame= 0;
		}
		else if (oldSpeed == 0 && speed != 0){ // guard started moving
			motion= walking;
			animFrame= 0;
		}
		
		oldSpeed= speed;
		
		model.setOBJ(motion[animFrame]);
	}
	
	public void navigate(){ // calculate what the current goal should be
		double threshold= .1; // tune this
		double minWait= 120; // minimum ticks before a chance to stop and look
		double pause= 60; // number of ticks to pause
		int stopChance= 25;
		
		if (goal == null){
			goal= waypoints[0];
			goalIndex= 0;
			return;
		}
		
		if (waiting){
			// say("Waiting...");
			tickCount++;
			if (tickCount >= waitTime)
				stopAndLook();
			else
				return;
		}
		
		if (!isMoving){
			if (isLooking){
				if (angleDelta == 0){ // pause a moment
					if (tickCount >= pause){ // after pause, turn back toward goal
						tickCount= 0; // reset
						faceToward(goal);
						isLooking= false;
					}
				} else tickCount= 0;
			}else{
				if (angleDelta == 0){
					if (tickCount >= pause){
						tickCount= 0;
						isMoving= true;
						speed= topSpeed;
					}
				} else tickCount= 0;
			}
			
		}else { // Guard is moving
			double dist= getLoc().minus(goal).length();
			
			if (dist < threshold){ // reached goal
				stopAndLook(); // look around before heading to new goal
				pickGoal(); // choose a new goal
			} else{
				if (tickCount >= minWait){
					if (rand.nextInt(100) < stopChance){
						stopAndLook();
					}else tickCount= 0; // reset this so we don't check again next tick
				}
			}
			
		}	
		tickCount++;
	}
	
	private void stopAndLook(){
		if (!waiting){
			//waiting= true;
			speed= 0;
			pause(.75, 1.25);
			return;
		}
		
		waiting= false;
		tickCount= 0;
		isMoving= false;
		isLooking= true;
		
		// pick a random waypoint to check
		pickPointToCheck();
		faceToward(pointToCheck);
	}
	
	private void pause(double low, double high){ // pause anywhere from 'low' to 'high' seconds
		tickCount= 0;
		int lowInt= (int)(low * 60);
		int highInt= (int)(high * 60);
		waiting= true;
		waitTime= rand.nextInt(highInt - lowInt) + lowInt;
	}
	
	private void pickPointToCheck(){
		int lookIndex= rand.nextInt(waypoints.length);
		Point3D point= waypoints[lookIndex];
		
		if (lookIndex == goalIndex)
			pointToCheck= waypoints[nextWaypoint()];
		else pointToCheck= point;
	}
	
	private void pickGoal(){
		// choose which adjacent waypoint will be the new goal
		int forwardChance= 66; // 2/3 chance of going main direction
		
		goalIndex= (rand.nextInt(100) < forwardChance) ? nextWaypoint() : prevWaypoint();
		goal= waypoints[goalIndex];
	}
	
	private int nextWaypoint(){ // returns the index of the next waypoint
		if (goalIndex == waypoints.length - 1) // end of the array---wrap around
			return 0;
		return goalIndex + 1;
	}
	
	private int prevWaypoint(){
		if (goalIndex == 0) // end of the array---wrap around
			return waypoints.length - 1;
		return goalIndex - 1;
	}
	
}
