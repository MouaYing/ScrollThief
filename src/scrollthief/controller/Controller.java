package scrollthief.controller;

import java.util.ArrayList;
import java.util.TimerTask;

import javax.swing.JFrame;

import ch.aplu.xboxcontroller.XboxController;
import scrollthief.ScrollThief;
import scrollthief.model.Boss;
import scrollthief.model.GameModel;
import scrollthief.model.GameState;
import scrollthief.model.Guard;
import scrollthief.model.Character;
import scrollthief.model.Point3D;
import scrollthief.model.Projectile;
import scrollthief.model.StateChange;
import scrollthief.model.StateChangedListener;
import scrollthief.view.View;

public class Controller extends TimerTask{
	JFrame window;
	View view;
	GameModel gameModel;
	public XboxController xbc;
	public KeyboardControl keyboard;
	public MouseControl mouse;
	String dllPath;
	int tick= 0;
	int hitTimer= 0;
	boolean devmode= false;
	
	public Controller(JFrame window, View view, GameModel gameModel){
		say("Loading main controller...");
		
		this.window= window;
		this.view= view;
		this.gameModel= gameModel;
		
		dllPath= System.getProperty("user.dir") + 
				(ScrollThief.is64bit() ? "/xboxcontroller64.dll" : "\\xboxcontroller.dll");
//		xbc= new XboxController(dllPath, 1, 50, 50);
//        xbc.setLeftThumbDeadZone(.2);
//        xbc.setRightThumbDeadZone(.2);
//        xbc.addXboxControllerListener(new XboxAdapter(this));
        keyboard = new KeyboardControl(this);
        view.addKeyListener(keyboard);
        mouse = new MouseControl(this);
        view.addMouseMotionListener(mouse);
//        if(!xbc.isConnected())
//        {
//        	System.out.println("Xbox controller not connected...");
//        }
        

		this.gameModel.addStateChangedListener(new StateChangedListener() {
	      public void stateChanged(StateChange evt) {
	        redisplay();
	      }
	    });
        
		say("--Main controller loaded--\n");
	}
	
	public void redisplay() {
		view.display();
	}

	@Override
	public void run() {
		if (gameModel.getState() == GameState.Uninitialized || gameModel.getState() == GameState.Initialized)
			return;
		if (gameModel.getState() == GameState.Start){
			gameModel.changeState(GameState.Paused);
			view.display();
			return;
		}
		if(gameModel.getState() == GameState.Dialog){
			view.display();
			return;
		}
		if(gameModel.getState() != GameState.Playing){
			view.display();
			return;
		}
		
		Guard[] guards= gameModel.getGuards();
		Character ninja= gameModel.getNinja();
		Boss boss= (Boss) gameModel.getBoss();
		
		tick++;
		if (tick > 10000) tick= 1; // reset
		
		moveCamera();
		
		if (hitTimer > 0){
			if (hitTimer > 25)
				vibrate(65535, 65535);
			hitTimer--;
			if (hitTimer <= 0)
				vibrate(0,0);
		}
		
// ------------ Update Ninja -------------------------------------------------------------------------
		ninja.move();
		ninja.animate(tick);
		if (ninja.getHP() <= 0 && !devmode)
			gameOver(GameState.Killed);
		if (gameModel.getState() == GameState.Victory){
			gameModel.changeState(GameState.Paused);
			vibrate(0,0);
			hitTimer= 0;
		}
//		say ("Location: " + ninja.getLoc().toString());
		
// ------------ Update Guards ------------------------------------------------------------------------		
		
		for (int i= 0; i < guards.length; i++){
			Guard guard= guards[i];
			
			guard.navigate();
			guard.move();
			guard.animate(tick);
			
			// check to see if the guard can see the ninja
			if (guard.isNear()){ // first check if he is even in range
				if (guard.isFacingNinja()){ // next check if the ninja is within guard's field of view
					if (guard.canSeeNinja()){ // now check for line of sight
						say("You have been spotted! Game Over!");
						if (!devmode)
							gameOver(GameState.Spotted);
					}
				}
			}
		}
		
// ------------ Update Boss --------------------------------------------------------------------------
		boss.update();
		boss.animate(tick);

		ArrayList<Projectile> projs= gameModel.getProjectiles();
		ArrayList<Projectile> collided= new ArrayList<Projectile>();
		
		for (int i= 0; i < projs.size(); i++){
			Projectile proj= projs.get(i);
			
			proj.move();
			
			if (proj.getModel().getLoc().y <= 0){
				collided.add(proj);
				continue;
			}
			
			if (proj.ninjaCollision()){
				ninja.takeDamage(1);
				collided.add(proj);
				say("You've been hit! Current HP: "+ ninja.getHP());
				hitTimer= 30;
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

// ----------------Check level dialog hotspots--------------------------------------------------------
		
		for(int i = 0; i < gameModel.getCurrentLevel().getDialogHotspots().size(); i++) {
			if(gameModel.getCurrentLevel().getDialogHotspots().get(i).checkForActivate(ninja.getLoc())) {
				gameModel.getCurrentLevel().setCurrentDialogHotspot(gameModel.getCurrentLevel().getDialogHotspots().get(i));
				gameModel.changeState(GameState.Dialog);
				break;
			}
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
		
//		window.getContentPane().remove(view);
//		gameModel= new GameModel(); 
//		view= new View(gameModel);
//		
//		window.getContentPane().add(view);
//		window.pack();
//		//window.setLocation(100, 10);
//		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		window.setVisible(true);
//		
//		xbc.release();
//		xbc= new XboxController(dllPath, 1, 50, 50);
//        xbc.setLeftThumbDeadZone(.2);
//        xbc.setRightThumbDeadZone(.2);
//        xbc.addXboxControllerListener(new XboxAdapter(this));
		
		gameModel.getNinja().reset();
		gameModel.getBoss().reset();
		
		view.setCamAngle(0);
		view.setCamHeight(4);
		view.setCamDistance(6);
		gameModel.changeState(GameState.Start);
	}
	
	private void gameOver(GameState reason){
		gameModel.changeState(reason);
		vibrate(0,0);
		hitTimer= 0;
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
