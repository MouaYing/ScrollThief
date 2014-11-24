package scrollthief.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Jon "Neo" Pimentel
 */
/**
 * This class is meant to hold any additional game information not pertaining to the 3d model.
 * e.g. health, what frame of their animation they are in. It is also meant to calculate things like
 * the character's hitbox. 
 * It also provides access to the 3d model information.
 */
public class Character {
	Model model;
	GameModel gameModel;
	Point2D[][] hitBox;
	double speed, deltaY, angleDelta, goalAngle= 0;
	double turnRate= .3;
	public boolean isJumping= false;
	public boolean isMoving= false;

	public Character(GameModel gameModel, Model model, double boxLength, double boxWidth){
		this.gameModel= gameModel;
		this.model= model;
		Point2D[] boxPoints= GameModel.findPoints(boxLength, boxWidth);
		hitBox= GameModel.createHitBox(boxPoints);
	}
	
	public void move(){
		adjustAngle();
		//say("new angle: "+getAngle());
		
		float scale= .25f;
		float gravity= .01f; // tune this
		double direction= getAngle() + Math.PI;
		double speed= getSpeed();
		Point3D loc= getLoc();
		Obstacle[] obstacles= gameModel.getObstacles();
		Character[] guards= gameModel.getGuards();
		double threshold= 10; // needs tuning, or to be done away with
		double threshold2= 2;
		ArrayList<Point2D[]> edges= new ArrayList<Point2D[]>();
		// say("Location: "+ loc.toString());
		
		Obstacle inBox= null; // the obstacle the ninja is standing on
		
		double deltaX= Math.sin(direction) * speed * scale;
		double deltaZ= -Math.cos(direction) * speed * scale;
		
		setDeltaY((loc.y > 0 || getDeltaY() > 0) ? (getDeltaY() - gravity) : 0);
		double newY= (loc.y + getDeltaY());
		if (newY <= 0){
			newY = 0;
			isJumping= false;
		}
		
		Point3D newLoc= new Point3D(loc.x + deltaX, newY, loc.z + deltaZ);
		//character.setLoc(newLoc);
		
		Point2D[][] hitBox= GameModel.boxToWorld(getModel(), getHitBox());
		Point2D[] edgePrime= null;
		Point2D[] edge2= null;
		Point3D delta= new Point3D(0,0,0);
		
//		for (int i= 0; i < guards.length; i++){
//			double dist= loc.minus(guards[i].getLoc()).length();
//			
//			if (dist > threshold2 || guards[i].equals(this))
//				continue;
//			
//			Point2D[][] guardBox= GameModel.boxToWorld(guards[i].getModel(), guards[i].getHitBox());
//			edges= gameModel.collision(hitBox, guardBox, edges);
//		}
		
		for (int i= 0; i < obstacles.length; i++){
			double obsHeight= obstacles[i].getHeight(); // tune this
			double dist= loc.minus(obstacles[i].getLoc()).length();
//			double dist2= newLoc.minus(obstacles[i].getLoc()).length();
			
			if (dist > threshold)
				continue;
			
			if (obstacles[i].isInBox(loc))
				inBox= obstacles[i];
			
//			if (dist <= dist2 || loc.y >= obsHeight) // character too far or not moving toward obstacle
//				continue;
			
			if (loc.y >= obsHeight)
				continue;
			
			edges= gameModel.collision(hitBox, obstacles[i].hitBox, edges);
			
			if (!edges.isEmpty()){
				// say("Number of collisions: "+edges.size());
				for (int j= 0; j < edges.size(); j++){
					Point2D[] edge= edges.get(j);
					
					if (edgePrime == null) 
						edgePrime= edge;
					else if (edgePrime[0].equals(edge[0]) && edgePrime[1].equals(edge[1]))
						continue;
					else if (loc.distanceToLine(edge[0], edge[1]) < loc.distanceToLine(edgePrime[0], edgePrime[1])){
						edge2= edgePrime;
						edgePrime= edge;
					}else edge2= edge;
				
					if (loc.distanceToLine(edgePrime[0], edgePrime[1]) < 
							newLoc.distanceToLine(edgePrime[0], edgePrime[1])){
						setLoc(newLoc);
						continue;
					}
					
					//say("Distance to edgePrime: "+loc.distanceToLine(edgePrime[0], edgePrime[1]));
					
					Point3D input= new Point3D(deltaX, 0, deltaZ);
					Point3D normal= new Point3D(-(edgePrime[0].getX() - edgePrime[1].getX()),0,
							(edgePrime[0].getY() - edgePrime[1].getY()));
					normal.Normalize();
					//say("Normal: "+normal.toString());
					
					Point3D undesired= normal.mult(input.dot(normal));
					Point3D desired= input.minus(undesired); 
					
					if (edge2 != null && edges.size() > 2){
						normal= new Point3D(-(edge2[0].getX() - edge2[1].getX()),0,
								(edge2[0].getY() - edge2[1].getY()));
						normal.Normalize();
						undesired= normal.mult(input.dot(normal));
						desired= desired.minus(undesired);
					}
					
//					delta.x += desired.x;
//					delta.z += desired.z;
					delta = desired;
					
					setLoc(new Point3D(loc.x + delta.x, loc.y, loc.z + delta.z));
				}
				
			}
			
		}
		if (edgePrime == null){ // No collision detected
			if (inBox != null){ // ninja is inside hitbox (probably on top of obstacle)
				//say("On the roof!");
				double obsHeight= inBox.getHeight();

				if (newLoc.y < obsHeight){
					if (getDeltaY() < 0) setDeltaY(0);
					newLoc.y= obsHeight;
					isJumping= false;
				}
			}
			setLoc(newLoc);
		}
		else{
			Point3D charLoc= getLoc();
			
			if (charLoc.y <= 0 && getDeltaY() < 0){ // just landed
				setDeltaY(0);
				isJumping= false;
			}
			
			charLoc.y += getDeltaY();
			
			if (charLoc.y < 0)
				charLoc.y= 0;
		}
		//say("deltaY: "+character.getDeltaY());
	}
	
	private void adjustAngle(){
		double angleDif= GameModel.normalizeAngle(goalAngle - getAngle());
		if (angleDif > -turnRate && angleDif < turnRate){
			angleDelta= 0;
			setAngle(goalAngle);
		}
		else angleDelta= (angleDif > 0) ? turnRate : -turnRate;
		
		setAngle(getAngle() + angleDelta);
	}
	
	//--------------- getters -----------------------------------------------------
	public Model getModel(){
		return model;
	}
	
	public double getAngle(){
		return model.getRot()[1];
	}
	
	public double getSpeed(){
		return speed;
	}
	
	public Point3D getLoc(){
		return model.getLoc();
	}
	
	public Point2D[][] getHitBox(){
		return hitBox;
	}
	
	public double getDeltaY(){
		return deltaY;
	}
	
	public double getTurnRate(){
		return turnRate; 
	}
	//--------------- setters -----------------------------------------------------
	public void setAngle(double newAngle){
		double[] newRot= model.getRot();
		newRot[1]= newAngle;
		model.setRot(newRot);
	}
	
	public void setGoalAngle(double angle){
		goalAngle= angle;
	}
	
	public void setSpeed(double newSpeed){
		speed= newSpeed;
	}
	
	public void setLoc(Point3D newPoint){
		model.setLoc(newPoint);
	}
	
	public void setDeltaY(double newDelta){
		deltaY= newDelta;
	}
	
	private void say(String message){
		System.out.println(message);
	}
	
}
