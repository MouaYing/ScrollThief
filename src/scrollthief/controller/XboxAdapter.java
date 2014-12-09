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
//		double scale= .5;
		double scale= 1;
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
	
	public void rightShoulder(boolean pressed){ // toggle devmode
		if (pressed){
			if (controller.devmode)
				controller.devmode= false;
			else controller.devmode= true;
			say("Developer mode = " + controller.devmode);
		}
	}
	
	int leftVibrate= 0;
	int rightVibrate= 0;
	
	public void leftTrigger(double value) // this is  for testing.
    {
      leftVibrate = (int)(65535 * value * value);
      controller.vibrate(leftVibrate, rightVibrate);
    }
    public void rightTrigger(double value)
    {
      rightVibrate = (int)(65535 * value * value);
      controller.vibrate(leftVibrate, rightVibrate);
    }
	
	public void buttonA(boolean pressed){
		if (pressed){
			scrollthief.model.Character ninja= gameModel.getNinja();
			if (ninja != null && !ninja.isJumping){
				ninja.isJumping= true;
				gameModel.getNinja().setDeltaY(.2);
				// say("Jump!");
			}
		}
	}
	
	public void back(boolean pressed){
		if (pressed)
			controller.reset();
	}
	
	public void start(boolean pressed){
		if (pressed){
			if (!gameModel.state.equals("running") && !gameModel.state.equals("start")){ // game is over---reset
				controller.reset();
				controller.paused= false;
			}
			else if (controller.paused){
				controller.paused= false;
				gameModel.state= "running";
			}
			else controller.paused= true;
		}
	}

	private void updateCamera(){
		double scale= .2;
		
		double dHoriz= -Math.sin(rightStickDir) * rightStickMag * scale;
		double dVert= Math.cos(rightStickDir) * rightStickMag * scale;
		
		double[] delta= {dHoriz, dVert};
		
		view.setCamDelta(delta);
	}
	
//	@SuppressWarnings("unused")
	private void say(String message){
		System.out.println(message);
	}
}
