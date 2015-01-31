package scrollthief.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import scrollthief.model.GameModel;
import scrollthief.view.View;

public class KeyboardControl implements KeyListener {

	private GameControl gameControl;
	private GameModel gameModel;
	
	public KeyboardControl(Controller controller){
		gameControl = controller.getGameControl();
		gameModel = controller.gameModel;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
			case KeyEvent.VK_W:
				gameModel.setWPressed(true);
				break;
			case KeyEvent.VK_S:
				gameModel.setSPressed(true);
				break;
			case KeyEvent.VK_A:
				gameModel.setAPressed(true);
				break;
			case KeyEvent.VK_D:
				gameModel.setDPressed(true);
				break;
			case KeyEvent.VK_LEFT:
				gameControl.rotateCameraLeft();
				break;
			case KeyEvent.VK_RIGHT:
				gameControl.rotateCameraRight();
				break;
			default:
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				gameControl.pause();
				break;
			case KeyEvent.VK_P:
				gameControl.reset();
				break;
			case KeyEvent.VK_O:
				gameControl.toggleDevMode();
				break;
			case KeyEvent.VK_SPACE:
				gameControl.jump();
				break;
			case KeyEvent.VK_W:
				gameModel.setWPressed(false);
				break;
			case KeyEvent.VK_S:
				gameModel.setSPressed(false);
				break;
			case KeyEvent.VK_A:
				gameModel.setAPressed(false);
				break;
			case KeyEvent.VK_D:
				gameModel.setDPressed(false);
				break;
			default:
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
