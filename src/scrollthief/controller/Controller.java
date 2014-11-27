package scrollthief.controller;

import java.util.ArrayList;
import java.util.TimerTask;

import javax.swing.JFrame;

import ch.aplu.xboxcontroller.XboxController;
import scrollthief.ScrollThief;
import scrollthief.model.Boss;
import scrollthief.model.GameModel;
import scrollthief.model.Guard;
import scrollthief.model.Character;
import scrollthief.model.Projectile;
import scrollthief.view.View;

public class Controller extends TimerTask{
	JFrame window;
	View view;
	GameModel gameModel;
	public XboxController xbc;
	String dllPath;
	boolean devmode= true;
	
	public Controller(JFrame window, View view, GameModel gameModel){
		say("Loading main controller...");
		
		this.window= window;
		this.view= view;
		this.gameModel= gameModel;
		
		dllPath= System.getProperty("user.dir") + 
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
		Boss boss= (Boss) gameModel.getBoss();
		
		moveCamera();
		
// ------------ Update Ninja -------------------------------------------------------------------------
		ninja.move();
		if (ninja.getHP() <= 0 && !devmode)
			gameOver();
		// say ("Location: " + ninja.getLoc().toString());
		
// ------------ Update Guards ------------------------------------------------------------------------		
		
		for (int i= 0; i < guards.length; i++){
			Guard guard= guards[i];
			
			guard.navigate();
			guard.move();
			
			// check to see if the guard can see the ninja
			if (guard.isNear()){ // first check if he is even in range
				if (guard.isFacingNinja()){ // next check if the ninja is within guard's field of view
					if (guard.canSeeNinja()){ // now check for line of sight
						say("You have been spotted! Game Over!");
						if (!devmode)
							gameOver();
					}
				}
			}
		}
		
// ------------ Update Boss --------------------------------------------------------------------------
		boss.update();

		ArrayList<Projectile> projs= gameModel.getProjectiles();
		ArrayList<Projectile> collided= new ArrayList<Projectile>();
		
		for (int i= 0; i < projs.size(); i++){
			Projectile proj= projs.get(i);
			
			proj.move();
			
			if (proj.ninjaCollision()){
				ninja.takeDamage(1);
				collided.add(proj);
				say("You've been hit! Current HP: "+ ninja.getHP());
				continue;
			}
			
			if (proj.obsCollision()){
				collided.add(proj);
			}
		}
		
		for (int j= 0; j < collided.size(); j++){ // destroy all that collided
			Projectile proj= collided.get(j);
			
			gameModel.getModels().remove(proj.getModel());
			gameModel.getProjectiles().remove(proj);
		}
// ---------------------------------------------------------------------------------------------------	
		
		view.display();
		
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
	
	public void reset(){
		say("Resetting game...");
		
		window.getContentPane().remove(view);
		gameModel= new GameModel(); 
		view= new View(gameModel);
		
		window.getContentPane().add(view);
		window.pack();
		//window.setLocation(100, 10);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
		xbc= new XboxController(dllPath, 1, 50, 50);
        xbc.setLeftThumbDeadZone(.2);
        xbc.setRightThumbDeadZone(.2);
        xbc.addXboxControllerListener(new XboxAdapter(this));
	}
	
	private void gameOver(){
		// TODO implement this
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
