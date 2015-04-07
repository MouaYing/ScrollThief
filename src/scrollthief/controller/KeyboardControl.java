package scrollthief.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import scrollthief.model.GameModel;
import scrollthief.model.GameState;

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
			case KeyEvent.VK_C:
				gameControl.resetCamera();
			case KeyEvent.VK_R:
				gameControl.attack();
				break;
			case KeyEvent.VK_SPACE:
				if (gameModel.getState() == GameState.Playing)
					gameControl.jump();
				gameControl.buttonClick();
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
			case KeyEvent.VK_UP:
				gameControl.switchSelectedButton(0);
				break;
			case KeyEvent.VK_DOWN:
				gameControl.switchSelectedButton(1);
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
