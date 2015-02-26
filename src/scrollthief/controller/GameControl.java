package scrollthief.controller;

import java.util.List;

import scrollthief.model.Button;
import scrollthief.model.GameModel;
import scrollthief.model.GameState;
import scrollthief.view.View;

public class GameControl {
	Controller controller;
	View view;
	GameModel gameModel;
	
	double cameraDir = 0;
	double cameraMag = 0;
	private double speedIncrement = 0.7;
	private double ninjaRotationIncrement = 0.2;
	private double cameraRotationIncrement = 0.1;
	private float cameraHeightIncrement = 0.1f;
	
	public GameControl(Controller controller){
		this.controller= controller;
		this.view= controller.view;
		this.gameModel= controller.gameModel;
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
		if(canPlay())
			if (gameModel.getNinjaSpeed() < 1)
				gameModel.setNinjaSpeed(gameModel.getNinjaSpeed() + speedIncrement);
	}
	
	public void stop(){
		if(canPlay())
			gameModel.setNinjaSpeed(0);
	}
	
	public void setNinjaAngle(double direction){
		if(canPlay())
			gameModel.setNinjaAngle(Math.toRadians(direction) +  view.getCamAngle());
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
		if (controller.devmode)
			controller.devmode= false;
		else controller.devmode= true;
		say("Developer mode = " + controller.devmode);
	}
	
	public void jump(){
		scrollthief.model.Character ninja= gameModel.getNinja();
		if(canPlay())
			if (ninja != null && !ninja.isJumping){
				ninja.isJumping= true;
				gameModel.getNinja().setDeltaY(.2);
				// say("Jump!");
			}
	}
	
	public void startAttack() {
		scrollthief.model.Character ninja= gameModel.getNinja();
		if(canPlay()) {
			ninja.isBeginAttacking = true;
			
			
		}
	}
	
	public void attack(float attackPower) {
		scrollthief.model.Character ninja= gameModel.getNinja();
		if(canPlay()) {
			ninja.isBeginAttacking = false;
			ninja.isAttacking = true;
			
		}
	}
	
	public void reset(){
		controller.reset();
	}
	
	public void pause(){
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
			}
			else {
				gameModel.changeState(GameState.Paused);
			}
		}
	}
	
	public void pauseButtonClick(){
		if(gameModel.getState() == GameState.Paused){
			gameModel.doPauseButton();
		}
	}
	
	public void switchSelectedButton(int direction){
		if(gameModel.getState() == GameState.Paused){
			if(direction == 0){
				List<Button> buttons = gameModel.getPauseButtons();
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
				List<Button> buttons = gameModel.getPauseButtons();
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
		if(gameModel.getState() == GameState.Paused){
			for(Button b : gameModel.getPauseButtons()){
				if(b.isHit(x, y)){
					b.doAction();
				}
			}
		}
	}
	

	public void highlightButton(int x, int y) {
		y = view.getHeight() - y;
		if(gameModel.getState() == GameState.Paused){
			for(Button b : gameModel.getPauseButtons()){
				if(b.isHit(x, y)){
					for(Button b2 : gameModel.getPauseButtons()){
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
