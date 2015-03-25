package scrollthief.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseControl implements MouseMotionListener, MouseListener {
	
	private GameControl gameControl;
	private int prevX;
	//private int prevY;

	public MouseControl(Controller controller){
		gameControl = new GameControl(controller);
		gameControl.setNinjaRotationIncrement(0.1);
		prevX = 0;
		//prevY = 0;
		//gameControl.resetCamera();
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0){
		gameControl.clickButton(arg0.getX(), arg0.getY());
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
//		if(arg0.getX() > prevX) {
//			gameControl.rotateCameraRight();
//		}
//		else if(arg0.getX() < prevX) {
//			gameControl.rotateCameraLeft();
//		}
//		
//		if (arg0.getY() > prevY) {
//			gameControl.increaseCameraHeight();
//		}
//		else if (arg0.getY() < prevY) {
//			gameControl.decreaseCameraHeight();
//		}
//				
//		prevX = arg0.getX();
//		prevY = arg0.getY();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		gameControl.highlightButton(arg0.getX(), arg0.getY());
		if(arg0.getX() > prevX) {
			gameControl.rotateCameraRight();
			gameControl.rotateNinjaRight();
		}
		else if(arg0.getX() < prevX) {
			gameControl.rotateCameraLeft();
			gameControl.rotateNinjaLeft();
		}
		
//		if (arg0.getY() > prevY) {
//			gameControl.increaseCameraHeight();
//		}
//		else if (arg0.getY() < prevY) {
//			gameControl.decreaseCameraHeight();
//		}
		
		prevX = arg0.getX();
//		prevY = arg0.getY();
		
		gameControl.setUsingMouse(true);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		gameControl.clickButton(e.getX(), e.getY());
		
	}

}
