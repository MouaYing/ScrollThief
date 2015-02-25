package scrollthief.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseEventControl implements MouseListener {

	private GameControl gameControl;
	private float attackPower;
	private float attackPowerLimit = 3;
	private float attackChargeStartTime;
	
	public MouseEventControl(Controller controller){
		gameControl = new GameControl(controller);
		attackPower = 1;
		attackChargeStartTime = 0;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		attackPower = (System.nanoTime() / 1000000 - attackChargeStartTime) / 1000;
		if(attackPower > attackPowerLimit)
			attackPower = attackPowerLimit;
		gameControl.attack(attackPower);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		attackChargeStartTime = System.nanoTime() / 1000000;  //convert to milliseconds
		gameControl.startAttack();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
