/**
 *  @author Jon "Neo" Pimentel
 */
package scrollthief;

import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

import scrollthief.controller.Controller;
import scrollthief.model.GameModel;
import scrollthief.view.View;

public class ScrollThief {
	static FPSAnimator anim;
	
	public static void main(String[] args) {
		int FPS= 60;
		
		JFrame window= new JFrame("Scroll Thief");
		
		GameModel gameModel= new GameModel();
		View view= new View(gameModel);
		new Controller(view, gameModel);
		
		anim= new FPSAnimator(view, FPS);
		
		window.getContentPane().add(view);
		window.pack();
		window.setLocation(100, 10);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
		anim.start();
		
		//controller.xbc.release();
	}
	
	public static boolean is64bit()
	{
	  return System.getProperty("sun.arch.data.model").equals("64");
	}

}
