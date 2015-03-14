/**
 *  @author Jon "Neo" Pimentel
 */
package scrollthief;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Timer;

import javax.swing.JFrame;

import scrollthief.controller.Controller;
import scrollthief.controller.GameControl;
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
		Controller controller = new Controller(window, view, gameModel);
		GameControl gameControl = new GameControl(controller);
		controller.setGameControl(gameControl);
		
		//anim= new FPSAnimator(view, FPS);
		
		view.addMouseListener(new MouseAdapter(){
			public void mouseExited(MouseEvent arg0) {
			  if(window.isShowing()){
			    Point locOnScreen = window.getLocationOnScreen();
			    int middleX = locOnScreen.x + (window.getWidth() / 2);
			    int middleY = locOnScreen.y + (window.getHeight() / 2);
			    try{
			      Robot rob = new Robot();
			      rob.mouseMove(middleX, middleY);
			    }catch(Exception e){System.out.println(e);}
			    //setting mouse coords
			  }
			};
		});
		
		window.getContentPane().add(view);
		window.pack();
		window.setLocation(100, 10);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
		window.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if(arg0.getWheelRotation() < 0) {
					for(int i = 0; i < 3; i++) {
						gameControl.decreaseCameraHeight();	
					}
				}
				else if(arg0.getWheelRotation() > 0) {
					for(int i = 0; i < 3; i++) {
						gameControl.increaseCameraHeight();	
					}
				}
			}
		});
		
		// Make cursor invisible
		Toolkit t = Toolkit.getDefaultToolkit();
	    Image i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Cursor noCursor = t.createCustomCursor(i, new Point(0, 0), "none");
		window.setCursor(noCursor);
		
		//anim.start();
		timer.scheduleAtFixedRate(controller, 3000, 1000/FPS); 
		
		//controller.xbc.release();
	}
	
	public static boolean is64bit()
	{
	  return System.getProperty("sun.arch.data.model").equals("64");
	}

}
