package scrollthief.controller;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.TimerTask;

import ch.aplu.xboxcontroller.XboxController;
import scrollthief.ScrollThief;
import scrollthief.model.GameModel;
import scrollthief.model.Guard;
import scrollthief.model.Obstacle;
import scrollthief.model.Point3D;
import scrollthief.model.Character;
import scrollthief.view.View;

public class Controller extends TimerTask{
	View view;
	GameModel gameModel;
	public XboxController xbc;
	
	public Controller(View view, GameModel gameModel){
		say("Loading main controller...");
		
		this.view= view;
		this.gameModel= gameModel;
		
		String dllPath= System.getProperty("user.dir") + 
				(ScrollThief.is64bit() ? "\\xboxcontroller64.dll" : "\\xboxcontroller.dll");
		xbc= new XboxController(dllPath, 1, 50, 50);
        xbc.setLeftThumbDeadZone(.2);
        xbc.setRightThumbDeadZone(.2);
        xbc.addXboxControllerListener(new XboxAdapter(this));
        if(!xbc.isConnected())
        	System.out.println("Xbox controller not connected...");
        
		say("--Main controller loaded--\n");
	}

	@Override
	public void run() {
		if (gameModel.initializing)
			return;
		
		Guard[] guards= gameModel.getGuards();
		Character ninja= gameModel.getNinja();
		
		moveCamera();
		
// ------------ Update Ninja -------------------------------------------------------------------------
		//moveCharacter(ninja);
		ninja.move();
		
// ------------ Update Guards ------------------------------------------------------------------------		
		
		for (int i= 0; i < guards.length; i++){
			Guard guard= guards[i];
			
			//moveCharacter(guard);
			guard.move();
			
			// check to see if the guard can see the ninja
			if (guard.isNear()){ // first check if he is even in range
				if (guard.isFacingNinja()){ // next check if the ninja is within guard's field of view
					if (guard.canSeeNinja()){ // now check for line of sight
						say("You have been spotted! Game Over!");
						// TODO trigger Game Over
					}
				}
			}
		}
		
		view.display();
		
	}
	
	public void moveCharacter(Character character){
		float scale= .25f;
		float gravity= .01f; // tune this
		double direction= character.getAngle() + Math.PI;
		double speed= character.getSpeed();
		Point3D loc= character.getLoc();
		Obstacle[] obstacles= gameModel.getObstacles();
		double threshold= 10; // needs tuning, or to be done away with
		
		Obstacle inBox= null; // the obstacle the ninja is standing on
		
		double deltaX= Math.sin(direction) * speed * scale;
		double deltaZ= -Math.cos(direction) * speed * scale;
		
		character.setDeltaY((loc.y > 0 || character.getDeltaY() > 0) ? (character.getDeltaY() - gravity) : 0);
		double newY= (loc.y + character.getDeltaY());
		if (newY <= 0) newY = 0;
		
		Point3D newLoc= new Point3D(loc.x + deltaX, newY, loc.z + deltaZ);
		//character.setLoc(newLoc);
		
		Point2D[][] hitBox= GameModel.boxToWorld(character.getModel(), character.getHitBox());
		Point2D[] edgePrime= null;
		Point3D delta= new Point3D(0,0,0);
		
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
			
			if (loc.y >= obsHeight) // character too far or not moving toward obstacle
				continue;
			
			ArrayList<Point2D[]> edges= obstacles[i].collision(hitBox);
			
			if (!edges.isEmpty()){
				//say("Number of collisions: "+edges.size());
				for (int j=0; j < edges.size(); j++){
					Point2D[] edge= edges.get(j);
					
					if (edgePrime == null) 
						edgePrime= edge;
					else if (loc.distanceToLine(edge[0], edge[1]) < loc.distanceToLine(edgePrime[0], edgePrime[1])){
						edgePrime= edge;
					}else continue;
				
					if (loc.distanceToLine(edgePrime[0], edgePrime[1]) < 
							newLoc.distanceToLine(edgePrime[0], edgePrime[1])){
						character.setLoc(newLoc);
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
					
//					delta.x += desired.x;
//					delta.z += desired.z;
					delta = desired;
					
					character.setLoc(new Point3D(loc.x + delta.x, loc.y, loc.z + delta.z));
				}
				
			}
			
		}
		if (edgePrime == null){ // No collision detected
			if (inBox != null){ // ninja is inside hitbox (probably on top of obstacle)
				//say("On the roof!");
				double obsHeight= inBox.getHeight();

				if (newLoc.y < obsHeight){
					if (character.getDeltaY() < 0) character.setDeltaY(0);
					newLoc.y= obsHeight;
				}
			}
			character.setLoc(newLoc);
		}
		else{
			Point3D charLoc= character.getLoc();
			
			if (charLoc.y <= 0 && character.getDeltaY() < 0)
				character.setDeltaY(0);
			
			charLoc.y += character.getDeltaY();
			
			if (charLoc.y < 0)
				charLoc.y= 0;
		}
		//say("deltaY: "+character.getDeltaY());
	}
	
	public void moveCamera(){
		double scale= .25;
		double[] dCam= view.getCamDelta();
		double dX= dCam[0];
		double dY= dCam[1];
		
		view.setCamAngle(view.getCamAngle() + dX * scale);
		if (view.getCamHeight() + dY > 0 && view.getCamDistance() >= 5)
			view.setCamHeight((float) (view.getCamHeight() + dY));
		view.setCamDistance(view.getCamDistance() + dY * scale);
	}
	
	public void vibrate(int leftVibrate, int rightVibrate){
		xbc.vibrate(leftVibrate, rightVibrate);
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
