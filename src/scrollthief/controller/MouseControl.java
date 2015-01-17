package scrollthief.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseControl implements MouseMotionListener {
	
	private GameControl gameControl;
	private int prevX;

	public MouseControl(Controller controller){
		gameControl = new GameControl(controller);
		gameControl.setNinjaRotationIncrement(0.1);
		prevX = 0;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getX() > prevX) {
			gameControl.rotateCameraRight();
			gameControl.rotateNinjaRight();
		}
		else if(arg0.getX() < prevX) {
			gameControl.rotateCameraLeft();
			gameControl.rotateNinjaLeft();
		}
		
		prevX = arg0.getX();
	}

}
