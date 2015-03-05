package scrollthief.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

import javafx.scene.input.KeyCode;
import scrollthief.model.Data;
import scrollthief.model.GameModel;
import scrollthief.view.View;

public class KeyboardControl implements KeyListener {

	private GameControl gameControl;
	
	public KeyboardControl(Controller controller){
		gameControl = new GameControl(controller);
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		switch (arg0.getKeyCode()) {
			case KeyEvent.VK_W:
				gameControl.increaseSpeed();
				break;
			case KeyEvent.VK_S:
				gameControl.stop();
				break;
			case KeyEvent.VK_A:
				gameControl.rotateNinjaLeft();
				break;
			case KeyEvent.VK_D:
				gameControl.rotateNinjaRight();
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
			case KeyEvent.VK_R:
				gameControl.attack();
				break;
			case KeyEvent.VK_SPACE:
				gameControl.jump();
				gameControl.pauseButtonClick();
				break;
			case KeyEvent.VK_UP:
				gameControl.switchSelectedButton(0);
				break;
			case KeyEvent.VK_DOWN:
				gameControl.switchSelectedButton(1);
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
