package scrollthief.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import scrollthief.model.GameModel;
import scrollthief.view.View;

public class MouseControl implements MouseMotionListener, MouseListener {
	
	private GameControl gameControl;
	private GameModel gameModel;
	private View view;
	private int prevX;
	//private int prevY;

	public MouseControl(Controller controller){
		gameControl = controller.getGameControl();
		gameModel = controller.gameModel;
		view = controller.view;
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
		if(gameModel.getNinja() != null) {
			if(gameModel.getAPressed() && !gameModel.getWPressed() && !gameModel.getSPressed()) {
				gameModel.getNinja().setAngle(view.getCamAngle() - Math.toRadians(90));
			}
			else if(gameModel.getAPressed() && gameModel.getWPressed()) {
				gameModel.getNinja().setAngle(view.getCamAngle() - Math.toRadians(45));
			}
			else if(gameModel.getAPressed() && gameModel.getSPressed()) {
				gameModel.getNinja().setAngle(view.getCamAngle() - Math.toRadians(135));
			}
			else if(gameModel.getDPressed() && !gameModel.getWPressed() && !gameModel.getSPressed()) {
				gameModel.getNinja().setAngle(view.getCamAngle() + Math.toRadians(90));
			}
			else if(gameModel.getDPressed() && gameModel.getWPressed()) {
				gameModel.getNinja().setAngle(view.getCamAngle() + Math.toRadians(45));
			}
			else if(gameModel.getDPressed() && gameModel.getSPressed()) {
				gameModel.getNinja().setAngle(view.getCamAngle() + Math.toRadians(135));
			}
			else if(gameModel.getSPressed() && !gameModel.getAPressed() && !gameModel.getDPressed()) {
				gameModel.getNinja().setAngle(view.getCamAngle() - Math.toRadians(180));
			}
			else {
				gameModel.getNinja().setAngle(view.getCamAngle());
			}
		}
		
		if(arg0.getX() > prevX) {
			gameControl.rotateCameraRight();
			gameControl.rotateNinjaRight();
			if(gameModel.getNinja() != null) {
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
		}
		else if(arg0.getX() < prevX) {
			gameControl.rotateCameraLeft();
			gameControl.rotateNinjaLeft();
			if(gameModel.getNinja() != null) {
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
		}
		
//		if (arg0.getY() > prevY) {
//			gameControl.increaseCameraHeight();
//		}
//		else if (arg0.getY() < prevY) {
//			gameControl.decreaseCameraHeight();
//		}
		
		prevX = arg0.getX();
//		prevY = arg0.getY();
		
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
