package scrollthief.controller;

import java.util.TimerTask;

import ch.aplu.xboxcontroller.XboxController;
import scrollthief.ScrollThief;
import scrollthief.model.GameModel;
import scrollthief.model.Guard;
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
		ninja.move();
		
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
						// TODO trigger Game Over
					}
				}
			}
		}
		
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
	
	private void say(String message){
		System.out.println(message);
	}
}
