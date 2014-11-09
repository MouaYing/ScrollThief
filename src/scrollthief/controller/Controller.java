package scrollthief.controller;

import java.awt.geom.Point2D;
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
		moveCharacter(ninja);
		
// ------------ Update Guards ------------------------------------------------------------------------		
		
		for (int i= 0; i < guards.length; i++){
			Guard guard= guards[i];
			
			moveCharacter(guard);
			
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
		double direction= character.getAngle() + Math.PI;
		double speed= character.getSpeed();
		Point3D loc= character.getLoc();
		Obstacle[] obstacles= gameModel.getObstacles();
		double threshold= 10; // needs tuning, or to be done away with
		
		double deltaX= Math.sin(direction) * speed * scale;
		double deltaZ= -Math.cos(direction) * speed * scale;
		
		Point3D newLoc= new Point3D(loc.x + deltaX, loc.y, loc.z + deltaZ);
		character.setLoc(newLoc);
		
		Point2D[][] hitBox= GameModel.boxToWorld(character.getModel(), character.getHitBox());
		Point2D[] edgePrime= null;
		
		for (int i= 0; i < obstacles.length; i++){
			double dist= loc.minus(obstacles[i].getLoc()).length();
			
//			if (dist1 > threshold || dist1 <= dist2) // character not moving toward obstacle
//				continue;
			if (dist > threshold) // character too far away to matter
				continue;
			
			Point2D[] edge= obstacles[i].collision(hitBox);
			
			if (edge != null){
				if (edgePrime == null) // this approach to corner cases doesn't work. Try adding deltas?
					edgePrime= edge;
				else if (loc.distanceToLine(edge[0], edge[1]) < loc.distanceToLine(edgePrime[0], edgePrime[1])){
					edgePrime= edge;
				}else continue;
				
				Point3D input= new Point3D(deltaX, 0, deltaZ);
				Point3D normal= new Point3D(-(edgePrime[1].getX() - edgePrime[0].getX()),0,
						(edgePrime[1].getY() - edgePrime[0].getY()));
				normal.Normalize();
				say("Normal: "+normal.toString());
				
				Point3D undesired= normal.mult(input.dot(normal));
				Point3D delta= input.minus(undesired); 
				
				character.setLoc(new Point3D(loc.x + delta.x, loc.y, loc.z + delta.z));
			}
		}
		
		
	}
	
	public void moveCamera(){
		double scale= .25;
		double[] dCam= view.getCamDelta();
		double dX= dCam[0];
		double dY= dCam[1];
		
		view.setCamAngle(view.getCamAngle() + dX * scale);
		if (view.getCamHeight() + dY > 0 && view.getCamDistance() >= 7)
			view.setCamHeight((float) (view.getCamHeight() + dY));
		view.setCamDistance(view.getCamDistance() + dY * scale);
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
