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
	OBJ defaultOBJ;
	double speed, oldSpeed, deltaY, angleDelta, goalAngle= 0;
	double turnRate= .3;
	public boolean isJumping= false;
	public boolean isMoving= false;
	int hp = 3; // player dies after 3 projectile hits, or one boss hit.
	int animFrame= 0; // The frame of animation currently being displayed
	OBJ[] motion= null; // The current animation loop

	public Character(GameModel gameModel, Model model, double boxLength, double boxWidth){
		this.gameModel= gameModel;
		this.model= model;
		defaultOBJ= model.getObj();
		Point2D[] boxPoints= GameModel.findPoints(boxLength, boxWidth);
		hitBox= GameModel.createHitBox(boxPoints);
	}
	
	public void move(){
		adjustAngle();
		//say("new angle: "+getAngle());
		
//		float scale= .25f;
		float scale= .125f;
		float gravity= .01f; // tune this
		double direction= getAngle() + Math.PI;
		double speed= getSpeed();
		double deltaX;
		double deltaZ;
		Point3D loc= getLoc();
		Obstacle[] obstacles= gameModel.getObstacles();
		Character ninja= gameModel.getNinja();
//		Character[] guards= gameModel.getGuards();
		double threshold= 10; // needs tuning, or to be done away with
		double threshold2= 2;
		ArrayList<Point2D[]> edges= new ArrayList<Point2D[]>();
		Obstacle inBox= null; // the obstacle the ninja is standing on
		// say("Location: "+ loc.toString());
		
		// this is the movement vector for this tick
		deltaX= Math.sin(direction) * speed * scale;
		deltaZ= -Math.cos(direction) * speed * scale;
		
		if (this instanceof Boss){
			Point3D bossDelta= calcDelta(deltaX, deltaZ);
			deltaX= bossDelta.x;
			deltaZ= bossDelta.z;
		}
		
		// apply gravity
		setDeltaY((loc.y > 0 || getDeltaY() > 0) ? (getDeltaY() - gravity) : 0);
		double newY= (loc.y + getDeltaY());
		if (newY <= 0){ // on the ground
			newY = 0;
			isJumping= false;
		}
		
		// location to move to if there are no collisions
		Point3D newLoc= new Point3D(loc.x + deltaX, newY, loc.z + deltaZ);
		
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
		
		if (model.getObj().equals(ninja.getModel().getObj())){
			Character boss= gameModel.getBoss();
			double dist= loc.minus(boss.getLoc()).length();
			
			if (dist < threshold2){
				Point2D[][] bossBox= GameModel.boxToWorld(boss.getModel(), boss.getHitBox());
				edges= gameModel.collision(hitBox, bossBox, edges);
				if (!edges.isEmpty()){
					ninja.takeDamage(1);
					return;
				}
			}
		}
		
		for (int i= 0; i < obstacles.length; i++){
			double obsHeight= obstacles[i].getHeight(); // tune this
			double dist= loc.minus(obstacles[i].getLoc()).length();
//			double dist2= newLoc.minus(obstacles[i].getLoc()).length();
			
			if (dist > threshold)
				continue;
			
			if (obstacles[i].isInBox(loc))
				inBox= obstacles[i];
			
//			if (dist <= dist2 || loc.y >= obsHeight) // character above or not moving toward obstacle
//				continue;
			
			if (loc.y >= obsHeight)
				continue;
			
			edges= gameModel.collision(hitBox, obstacles[i].hitBox, edges);
			
			if (!edges.isEmpty()){
				if (obstacles[i].equals(gameModel.getScroll()) && model.getObj().equals(ninja.getModel().getObj())){
					gameModel.state= "victory";
				}
//				say("Number of collisions: "+edges.size());
				for (int j= 0; j < edges.size(); j++){
					Point2D[] edge= edges.get(j);
					double distLoc2prime= 9999;
					
					if (edgePrime == null) 
						edgePrime= edge;
					else if (edgePrime[0].equals(edge[0]) && edgePrime[1].equals(edge[1]))
						continue;
					else{ 
						distLoc2prime= loc.distanceToLine(edgePrime[0], edgePrime[1]);
						if (loc.distanceToLine(edge[0], edge[1]) < distLoc2prime){
							edge2= edgePrime;
							edgePrime= edge;
						}else edge2= edge;
					}
					
					for (int k= 0; k < 4; k++){
						Point2D[] curEdge= obstacles[i].hitBox[k];
						if (loc.distanceToLine(curEdge[0], curEdge[1]) < distLoc2prime)
							edgePrime= curEdge;
					}
					
					distLoc2prime= loc.distanceToLine(edgePrime[0], edgePrime[1]);
					double distNewLoc2prime= newLoc.distanceToLine(edgePrime[0], edgePrime[1]);
					
					double distLoc2edge2= 9999;
					double distNewLoc2edge2= 9999;
					if (edge2 != null){
//						say("At least 2 distinct edges:"+edgePrime[0].toString()+","+edgePrime[1].toString()+
//								" " + edge2[0].toString() + "," + edge2[1].toString());
						distLoc2edge2= loc.distanceToLine(edge2[0], edge2[1]);
						distNewLoc2edge2 = newLoc.distanceToLine(edge2[0], edge2[1]);
					}
					
//					say("loc Dist: "+distLoc2prime+" newLoc dist: "+distNewLoc2prime);
					if (distLoc2prime < distNewLoc2prime){
//					if (distLoc2prime < distNewLoc2prime || distLoc2edge2 < distNewLoc2edge2){
//						say("Facing away from prime");
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
//					if (edge2 != null && distLoc2edge2 >= distNewLoc2edge2){
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
			if (inBox != null){ // character is inside hitbox (probably on top of obstacle)
				//say("On the roof!");
				double obsHeight= inBox.getHeight();

				if (newLoc.y < obsHeight){
					if (getDeltaY() < 0) setDeltaY(0);
					newLoc.y= obsHeight;
					isJumping= false;
				}
			}
			setLoc(newLoc); // actually move the character
		}
		else{ // Collision detected!
			Point3D charLoc= getLoc();
			
			if (charLoc.y <= 0 && getDeltaY() < 0){ // just landed---don't keep going through the floor
				setDeltaY(0);
				isJumping= false;
			}
			
			charLoc.y += getDeltaY();
			
			if (charLoc.y < 0)
				charLoc.y= 0;
		}
		//say("deltaY: "+character.getDeltaY());
	}
	
	// Determine what OBJ to use this tick, and set it as model.obj
	public void animate(int tick){
		// This class is meant to be overridden my subclass
		say("This was supposed to be overridden!");
	}
	
	public Point3D calcDelta(double deltaX, double deltaZ) {
		// override this
		return null;
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
	
	public void faceToward(Point3D lookTarget){
		goalAngle= -Math.atan2(lookTarget.x - getLoc().x, lookTarget.z - getLoc().z);
	}
	
	public void takeDamage(int damage){
		hp -= damage;
	}
	
	public void advanceFrame(){
		animFrame++;
		if (animFrame >= motion.length)
			animFrame= 0;
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
	
	public int getHP(){
		return hp;
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
	
	protected void say(String message){
		System.out.println(message);
	}
	
}
