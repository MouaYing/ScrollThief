package scrollthief.view;

import scrollthief.model.GameModel;

public class View {
	GameModel model;

	public View(GameModel model){
		this.model= model;
		say("--View loaded--");
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
