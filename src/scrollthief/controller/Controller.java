package scrollthief.controller;

import scrollthief.model.GameModel;
import scrollthief.view.View;

public class Controller {
	View view;
	GameModel gameModel;
	
	public Controller(View view, GameModel gameModel){
		this.view= view;
		this.gameModel= gameModel;
		say("Main controller loaded");
	}

	private void say(String message){
		System.out.println(message);
	}
}
