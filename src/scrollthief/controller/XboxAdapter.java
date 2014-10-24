/**
 *  Javadoc for the Aplu Xbox Controller Library can be found here:
 *  http://www.aplu.ch/classdoc/xbox/ch/aplu/xboxcontroller/package-summary.html
 *  
 *  Further information is available here: http://www.aplu.ch/home/apluhomex.jsp?site=36
 */
package scrollthief.controller;

import ch.aplu.xboxcontroller.XboxControllerAdapter;

public class XboxAdapter extends XboxControllerAdapter{
	Controller controller;
	
	public XboxAdapter(Controller controller){
		this.controller= controller;
	}
	
	public void isConnected(boolean connected){
		if (connected)
			System.out.println("Xbox controller connected!");
		else System.out.println("Xbox controller has been disconnected!");
	}
	
	public void leftThumbDirection(double direction){
		controller.gameModel.setNinjaAngle(Math.toRadians(direction) +  controller.view.getCameraAngle());
	}
	
	public void leftThumbMagnitude(double magnitude){
		double scale= .5;
		controller.gameModel.setNinjaSpeed(scale * magnitude);
	}
	
	public void rightThumbDirection(double direction){
		
	}
	
	public void leftShoulder(boolean pressed){
		if (pressed){
			controller.view.resetting= true;
			System.out.println("Camera reset");
		}
		else controller.view.resetting= false;
	}

}
