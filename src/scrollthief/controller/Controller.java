package scrollthief.controller;

import ch.aplu.xboxcontroller.XboxController;
import scrollthief.ScrollThief;
import scrollthief.model.GameModel;
import scrollthief.view.View;

public class Controller {
	View view;
	GameModel gameModel;
	XboxController xbc;
	
	public Controller(View view, GameModel gameModel){
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
        
		say("--Main controller loaded--");
	}

	private void say(String message){
		System.out.println(message);
	}
}
