package scrollthief.controller;

import java.util.List;

import scrollthief.model.Button;
//import scrollthief.model.Data;
import scrollthief.model.GameModel;
import scrollthief.model.GameState;
//import scrollthief.model.Point3D;
import scrollthief.model.SoundFile;
import scrollthief.view.View;

public class GameControl {
	Controller controller;
	View view;
	GameModel gameModel;
	
	double cameraDir = 0;
	double cameraMag = 0;
	private double speedIncrement = 0.7;
	private double ninjaRotationIncrement = 0.075;
	private double cameraRotationIncrement = 0.075;
	private float cameraHeightIncrement = 0.2f;

	private boolean usingMouse;
	
	public GameControl(Controller controller) {
		this.controller= controller;
		this.view= controller.view;
		this.gameModel= controller.gameModel;
		
		this.usingMouse = false;
	}
	
	public boolean getUsingMouse() {
		return usingMouse;
	}
	
	public boolean canPlay(){
		if(gameModel.getState() == GameState.Playing || 
				gameModel.getState() == GameState.Paused ||
				gameModel.getState() == GameState.Spotted || 
				gameModel.getState() == GameState.Killed || 
				gameModel.getState() == GameState.Victory ||
				gameModel.getState() == GameState.Dialog){
			return true;
		}
		return false;
	}
	
	public void rotateNinjaLeft(){
		if(canPlay())
			gameModel.setNinjaAngle(gameModel.getNinjaAngle() - ninjaRotationIncrement);
	}
	
	public void rotateNinjaRight(){
		if(canPlay())
			gameModel.setNinjaAngle(gameModel.getNinjaAngle() + ninjaRotationIncrement);
	}
	
	public void rotateCameraLeft(){
		if(canPlay())
			view.setCamAngle(view.getCamAngle() - cameraRotationIncrement);
	}
	
	public void rotateCameraRight(){
		if(canPlay())
			view.setCamAngle(view.getCamAngle() + cameraRotationIncrement);
	}
	
	public void increaseCameraHeight(){
		if(canPlay())
			view.setCamHeight(view.getCamHeight() + cameraHeightIncrement);
	}
	
	public void decreaseCameraHeight(){
		if(canPlay())
			view.setCamHeight(view.getCamHeight() - cameraHeightIncrement);
	}
	
	public void increaseSpeed(){
		if(canPlay() && !isAttacking())
			if (gameModel.getNinjaSpeed() < 1)
				gameModel.setNinjaSpeed(gameModel.getNinjaSpeed() + speedIncrement);
	}
	
	public void stop(){
		if(canPlay() && !isAttacking())
			gameModel.setNinjaSpeed(0);
	}
	
	public void forward() {
		
	}
	
	public void backward() {
		
	}
	
	public void setNinjaAngle(double direction){
		if(canPlay()){
			gameModel.setNinjaAngle(Math.toRadians(direction) +  view.getCamAngle());
			gameModel.getNinja().setNinjaDirection(Math.toRadians(direction + 180) +  view.getCamAngle());
		}
	}
	
	public double getNinjaAngle() {
		return gameModel.getNinjaAngle();
	}
	
	public void setNinjaSpeed(double magnitude){
		double scale= 1;
		if(canPlay())
			gameModel.setNinjaSpeed(scale * magnitude);
	}
	
	public void setCameraDirection(double direction){
		cameraDir = Math.toRadians(direction);
		if(canPlay())
			updateCamera();
	}
	
	public void setCameraMagnitude(double magnitude){
		cameraMag = magnitude;
		if(canPlay())
			updateCamera();
	}
	
	public void setNinjaRotationIncrement(double increment) {
		if(canPlay())
			ninjaRotationIncrement = increment;
	}
	
	public void setCameraRotationIncrement(double increment) {
		if(canPlay())
			cameraRotationIncrement = increment;
	}
	
	public void resetCamera(){
		controller.view.resetting= true;
		System.out.println("Camera reset");
	}
	
	public void toggleDevMode(){
		//for putting the ninja next to the boss for testing
		//scrollthief.model.Character ninja= gameModel.getNinja();
		
//		ninja.setLoc(new Point3D(10, 0, 70));  //for if you want quick teleport to boss
		
		if (controller.devmode)
			controller.devmode= false;
		else 
			controller.devmode= true;
		say("Developer mode = " + controller.devmode);
	}
	
	public void jump(){
		scrollthief.model.Character ninja= gameModel.getNinja();
		if(canPlay() && !isAttacking())
			if (ninja != null && !ninja.isJumping){
				ninja.isJumping= true;
				gameModel.getNinja().setDeltaY(.2);
				gameModel.getSound().playEffect(SoundFile.JUMP);
			}
	}
	
	public void attack() {
		scrollthief.model.Character ninja= gameModel.getNinja();
		if(canPlay()) {
			if(ninja.attacking < 1)
				ninja.attacking = 1;
			else {
				ninja.nextAttack = ninja.attacking + 1; 
				if(ninja.nextAttack > ninja.attackingStateMax) {
					ninja.nextAttack = 1;
				}
			}
		}
	}
	
	public boolean isAttacking() {
		scrollthief.model.Character ninja= gameModel.getNinja();
		return ninja.attacking > -1;
	}
	
	public void reset(){
		controller.reset();
	}
	
	public void pause(){
		//System.out.println(gameModel == null);
		
//		if (gameModel.getState() == GameState.Spotted || gameModel.getState() == GameState.Killed && gameModel.getState() == GameState.Victory){ // game is over---reset
//			controller.reset();
//		}
//		else if (gameModel.getState() == GameState.Paused){
//			gameModel.changeState(GameState.Playing);
//		}
//		else {
//			gameModel.changeState(GameState.Paused);
//		}
		if(canPlay() || gameModel.getState() == GameState.Start){
			if(gameModel.getState() == GameState.Start){
				gameModel.changeState(GameState.Playing);
				return;
			}
			if (gameModel.getState() == GameState.Spotted || gameModel.getState() == GameState.Killed || gameModel.getState() == GameState.Victory){ // game is over---reset
				controller.reset();
			}
			else if (gameModel.getState() == GameState.Dialog) {
				if(view.getDialogRenderer().isFinished() || view.getDialogRenderer().isNextScroll()) {
					gameModel.getCurrentLevel().getCurrentDialogHotspot().setRepeatable(false);
					gameModel.changeState(GameState.Playing);
				}
				else {
					view.getDialogRenderer().setIsNextScroll(true);
				}
			}
			else if (gameModel.getState() == GameState.Paused){
				gameModel.changeState(GameState.Playing);
				gameModel.getResource().getSound().resumeMusic();
			}
			else {
				gameModel.changeState(GameState.Paused);
				gameModel.getResource().getSound().pauseMusic();
			}
		}
	}
	
	private List<Button> getButtons() {
		List<Button> buttons = null;
		if(gameModel.getState() == GameState.Paused){
			buttons = gameModel.getPauseButtons();
		}
		else if(gameModel.getState() == GameState.MainMenu){
			buttons = gameModel.getMainMenuButtons();
		}
		return buttons;
	}
	
	public void buttonClick(){
		if(gameModel.getState() == GameState.Paused){
			gameModel.doPauseButton();
		}
		else if(gameModel.getState() == GameState.MainMenu){
			gameModel.doMainMenuButton();
		}
	}
	
	public void switchSelectedButton(int direction){
		List<Button> buttons = getButtons();
		if(buttons != null){
			if(direction == 0){
				for(int i = 0; i < buttons.size();i++){
					if( i == 0 && buttons.get(i).IsSelected()){
						return;
					}
					else if(buttons.get(i).IsSelected()){
						buttons.get(i-1).setSelected(true);
						buttons.get(i).setSelected(false);
						break;
					}
				}
			}
			else if(direction == 1){
				for(int i = 0; i < buttons.size();i++){
					if( i == buttons.size()-1 && buttons.get(i).IsSelected()){
						return;
					}
					else if(buttons.get(i).IsSelected()){
						buttons.get(i+1).setSelected(true);
						buttons.get(i).setSelected(false);
						break;
					}
				}
			}			
		}
	}

	public void clickButton(int x, int y) {
		y = view.getHeight() - y;
		List<Button> buttons = getButtons();
		if(buttons != null){
			for(Button b : buttons){
				if(b.isHit(x, y)){
					b.doAction();
				}
			}
		}
	}

	public void highlightButton(int x, int y) {
		y = view.getHeight() - y;

		List<Button> buttons = getButtons();
		if(buttons!= null){
			for(Button b : buttons){
				if(b.isHit(x, y)){
					for(Button b2 : buttons){
						b2.setSelected(false);
					}
					b.setSelected(true);
				}
			}
		}
	}
	
	private void updateCamera(){
		double scale= .2;
		
		double dHoriz= -Math.sin(cameraDir) * cameraMag * scale;
		double dVert= Math.cos(cameraDir) * cameraMag * scale;
		
		double[] delta= {dHoriz, dVert};
		if(canPlay())
			view.setCamDelta(delta);
	}
	
//	@SuppressWarnings("unused")
	private void say(String message){
		System.out.println(message);
	}


}
