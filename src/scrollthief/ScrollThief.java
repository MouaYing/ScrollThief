/**
 *  @author Jon "Neo" Pimentel
 */
package scrollthief;

import java.util.Timer;

import javax.swing.JFrame;

import scrollthief.controller.Controller;
import scrollthief.model.GameModel;
import scrollthief.view.View;

public class ScrollThief {
	//static FPSAnimator anim;
	
	public static void main(String[] args) {
		int FPS= 60;
		Timer timer = new Timer();
		
		JFrame window= new JFrame("Scroll Thief");
		
		GameModel gameModel= new GameModel();
		View view= new View(gameModel);
		Controller controller = new Controller(view, gameModel);
		
		//anim= new FPSAnimator(view, FPS);
		
		window.getContentPane().add(view);
		window.pack();
		window.setLocation(100, 10);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
		//anim.start();
		timer.scheduleAtFixedRate(controller, 3000, 1000/FPS); 
		
		//controller.xbc.release();
	}
	
	public static boolean is64bit()
	{
	  return System.getProperty("sun.arch.data.model").equals("64");
	}

}
