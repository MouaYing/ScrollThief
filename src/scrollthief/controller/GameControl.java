package scrollthief.controller;

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
	
	public void rotateNinjaLeft(){
		gameModel.setNinjaAngle(gameModel.getNinjaAngle() - ninjaRotationIncrement);
	}
	
	public void rotateNinjaRight(){
		gameModel.setNinjaAngle(gameModel.getNinjaAngle() + ninjaRotationIncrement);
	}
	
	public void rotateCameraLeft(){
		view.setCamAngle(view.getCamAngle() - cameraRotationIncrement);
	}
	
	public void strafeNinjaRight() {
		
	}
	
	public void strafeNinjaLeft() {
		
	}
	
	public void rotateCameraRight(){
		view.setCamAngle(view.getCamAngle() + cameraRotationIncrement);
	}
	
	public void increaseCameraHeight(){
		view.setCamHeight(view.getCamHeight() + cameraHeightIncrement);
	}
	
	public void decreaseCameraHeight(){
		view.setCamHeight(view.getCamHeight() - cameraHeightIncrement);
	}
	
	public void increaseSpeed(){
		if (gameModel.getNinjaSpeed() < 1)
			gameModel.setNinjaSpeed(gameModel.getNinjaSpeed() + speedIncrement);
	}
	
	public void stop(){
		gameModel.setNinjaSpeed(0);
	}
	
	public void forward() {
		
	}
	
	public void backward() {
		
	}
	
	public void setNinjaAngle(double direction){
		gameModel.setNinjaAngle(Math.toRadians(direction) +  view.getCamAngle());
	}
	
	public void setNinjaSpeed(double magnitude){
		double scale= 1;
		gameModel.setNinjaSpeed(scale * magnitude);
	}
	
	public void setCameraDirection(double direction){
		cameraDir = Math.toRadians(direction);
		updateCamera();
	}
	
	public void setCameraMagnitude(double magnitude){
		cameraMag = magnitude;
		updateCamera();
	}
	
	public void setNinjaRotationIncrement(double increment) {
		ninjaRotationIncrement = increment;
	}
	
	public void setCameraRotationIncrement(double increment) {
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
		if (ninja != null && !ninja.isJumping){
			ninja.isJumping= true;
			gameModel.getNinja().setDeltaY(.2);
			// say("Jump!");
		}
	}
	
	public void reset(){
		controller.reset();
	}
	
	public void pause(){
		System.out.println(gameModel == null);
		
		if (gameModel.getState() == GameState.Spotted || gameModel.getState() == GameState.Killed && gameModel.getState() == GameState.Victory){ // game is over---reset
			controller.reset();
		}
		else if (gameModel.getState() == GameState.Paused){
			gameModel.changeState(GameState.Playing);
		}
		else {
			gameModel.changeState(GameState.Paused);
		}
	}
	
	private void updateCamera(){
		double scale= .2;
		
		double dHoriz= -Math.sin(cameraDir) * cameraMag * scale;
		double dVert= Math.cos(cameraDir) * cameraMag * scale;
		
		double[] delta= {dHoriz, dVert};
		
		view.setCamDelta(delta);
	}
	
//	@SuppressWarnings("unused")
	private void say(String message){
		System.out.println(message);
	}
}
