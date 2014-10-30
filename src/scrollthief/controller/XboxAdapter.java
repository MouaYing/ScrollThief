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
	
	Double rightStickDir= 0.0;
	
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
	}
	
	public void rightThumbMagnitude(double magnitude){
		double scale= .25;
		
		double dHoriz= -Math.sin(rightStickDir) * magnitude * scale;
		double dVert= Math.cos(rightStickDir) * magnitude * scale;
		
		double[] delta= {dHoriz, dVert};
		
		view.setCamDelta(delta);
	}
	
	public void leftShoulder(boolean pressed){
		if (pressed){
			controller.view.resetting= true;
			System.out.println("Camera reset");
		}
		else controller.view.resetting= false;
	}

}
