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
	private Controller controller;
	private GameControl gameControl;
	
	public XboxAdapter(Controller controller){
		this.controller = controller;
		gameControl = new GameControl(controller);
	}
	
	public void isConnected(boolean connected){
		if (connected)
			System.out.println("Xbox controller connected!");
		else System.out.println("Xbox controller has been disconnected!");
	}
	
	public void leftThumbDirection(double direction){
		gameControl.setNinjaAngle(direction);
	}
	
	public void leftThumbMagnitude(double magnitude){
		gameControl.setNinjaSpeed(magnitude);
	}
	
	public void rightThumbDirection(double direction){
		gameControl.setCameraDirection(direction);
	}
	
	public void rightThumbMagnitude(double magnitude){
		gameControl.setCameraMagnitude(magnitude);
	}
	
	public void leftShoulder(boolean pressed){
		if (pressed){
			gameControl.resetCamera();
		}
		else controller.view.resetting= false;
	}
	
	public void rightShoulder(boolean pressed){ // toggle devmode
		if (pressed){
			gameControl.toggleDevMode();
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
			gameControl.jump();
		}
	}
	
	public void back(boolean pressed){
		if (pressed)
			controller.reset();
	}
	
	public void start(boolean pressed){
		if (pressed){
			gameControl.pause();
		}
	}
}
