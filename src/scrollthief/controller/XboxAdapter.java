/**
 *  Javadoc for the Aplu Xbox Controller Library can be found here:
 *  http://www.aplu.ch/classdoc/xbox/ch/aplu/xboxcontroller/package-summary.html
 *  
 *  Further information is available here: http://www.aplu.ch/home/apluhomex.jsp?site=36
 */
package scrollthief.controller;

import scrollthief.model.GameModel;
import scrollthief.view.View;
import ch.aplu.xboxcontroller.XboxControllerAdapter;

public class XboxAdapter extends XboxControllerAdapter{
	Controller controller;
	View view;
	GameModel gameModel;
	
	double rightStickDir= 0;
	double rightStickMag= 0;
	
	public XboxAdapter(Controller controller){
		this.controller= controller;
		this.view= controller.view;
		this.gameModel= controller.gameModel;
	}
	
	public void isConnected(boolean connected){
		if (connected)
			System.out.println("Xbox controller connected!");
		else System.out.println("Xbox controller has been disconnected!");
	}
	
	public void leftThumbDirection(double direction){
		gameModel.setNinjaAngle(Math.toRadians(direction) +  view.getCamAngle());
	}
	
	public void leftThumbMagnitude(double magnitude){
		double scale= .5;
		gameModel.setNinjaSpeed(scale * magnitude);
	}
	
	public void rightThumbDirection(double direction){
		rightStickDir= Math.toRadians(direction);
		updateCamera();
	}
	
	public void rightThumbMagnitude(double magnitude){
		rightStickMag= magnitude;
		updateCamera();
	}
	
	public void leftShoulder(boolean pressed){
		if (pressed){
			controller.view.resetting= true;
			System.out.println("Camera reset");
		}
		else controller.view.resetting= false;
	}
	
	public void buttonA(boolean pressed){
		if (pressed){
			gameModel.getNinja().setDeltaY(.2);
			say("Jump!");
		}
	}

	private void updateCamera(){
		double scale= .2;
		
		double dHoriz= -Math.sin(rightStickDir) * rightStickMag * scale;
		double dVert= Math.cos(rightStickDir) * rightStickMag * scale;
		
		double[] delta= {dHoriz, dVert};
		
		view.setCamDelta(delta);
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
