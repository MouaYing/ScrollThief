package scrollthief.controller;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.swing.JFrame;

import ch.aplu.xboxcontroller.XboxController;
import scrollthief.ScrollThief;
import scrollthief.model.GameModel;
import scrollthief.model.GameState;
import scrollthief.model.Projectile;
import scrollthief.model.Sound;
import scrollthief.model.SoundFile;
import scrollthief.model.StateChange;
import scrollthief.model.StateChangedListener;
import scrollthief.model.characters.Boss;
import scrollthief.model.characters.Character;
import scrollthief.model.characters.Guard;
import scrollthief.view.View;

public class Controller extends TimerTask{
	JFrame window;
	View view;
	GameModel gameModel;
	private GameControl gameControl;
	public XboxController xbc;
	public KeyboardControl keyboard;
	public MouseControl mouse;
	String dllPath;
	int tick= 0;
	int hitTimer= 0;
	boolean devmode= false;
	boolean isCursorInvisible = true;
	
	public Controller(JFrame window, View view, GameModel gameModel){
		say("Loading main controller...");
		
		this.window= window;
		this.view= view;
		this.gameModel= gameModel;

		gameControl = new GameControl(this);

		try{
			dllPath =(ScrollThief.is64bit() ? this.getClass().getResource("/resources/xboxcontroller64.dll").getFile() :
				this.getClass().getResource("/resources/xboxcontroller.dll").getFile());
			say("dllPath: " + dllPath);
			xbc= new XboxController(dllPath, 1, 50, 50);
	        xbc.setLeftThumbDeadZone(.2);
	        xbc.setRightThumbDeadZone(.2);
	        xbc.addXboxControllerListener(new XboxAdapter(this));
		}catch(Exception e){
			System.out.println("Unable to load xbox controller dlls");
		}
		if (xbc == null){
			try{
				dllPath = System.getProperty("user.dir") + 
						(ScrollThief.is64bit() ? "\\src\\resources\\xboxcontroller64.dll" : 
							"\\src\\resources\\xboxcontroller.dll");
				say("dllPath: " + dllPath);
				xbc= new XboxController(dllPath, 1, 50, 50);
		        xbc.setLeftThumbDeadZone(.2);
		        xbc.setRightThumbDeadZone(.2);
		        xbc.addXboxControllerListener(new XboxAdapter(this));
			}catch(Exception e){
				System.out.println("Still unable to load xbox controller dlls");
			}
		}
        keyboard = new KeyboardControl(this);
        view.addKeyListener(keyboard);
        mouse = new MouseControl(this);
        view.addMouseMotionListener(mouse);
        view.addMouseListener(mouse);
        
        if(xbc != null && !xbc.isConnected())
        {
        	System.out.println("Xbox controller not connected...");
        }
        
		this.gameModel.addStateChangedListener(new StateChangedListener() {
	      public void stateChanged(StateChange evt) {
	        redisplay();
	      }
	    });
        
		say("--Main controller loaded--\n");
	}
	
	public void redisplay() {
		view.display();
	}

	@Override
	public void run() {
		if((gameModel.getState() == GameState.Paused||gameModel.getState() == GameState.MainMenu) && isCursorInvisible) {
			 window.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			 isCursorInvisible = false;
		}
		else if((gameModel.getState() != GameState.Paused&&gameModel.getState() != GameState.MainMenu) && !isCursorInvisible) {
			Toolkit t = Toolkit.getDefaultToolkit();
		    Image i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			Cursor noCursor = t.createCustomCursor(i, new Point(0, 0), "none");
			window.setCursor(noCursor);
			isCursorInvisible = true;
		}

		if (gameModel.getState() == GameState.Uninitialized || gameModel.getState() == GameState.Initialized)
			return;
		if(gameModel.getState() == GameState.Dialog){
			view.display();
			return;
		}
		if(gameModel.getState() != GameState.Playing){
			view.display();
			return;
		}
		
		Guard[] guards= gameModel.getGuards();
		Character ninja= gameModel.getNinja();
		Boss boss= (Boss) gameModel.getBoss();
		
		tick++;
		if (tick > 10000) tick= 1; // reset
		
		moveCamera();
		
		if (hitTimer > 0){
			if (hitTimer > 25)
				vibrate(65535, 65535);
			hitTimer--;
			if (hitTimer <= 0)
				vibrate(0,0);
		}
		
// ------------ Update Ninja -------------------------------------------------------------------------
		ninja.move();
		ninja.animate(tick);
		if (ninja.getHP() <= 0 && !devmode)
			gameOver(GameState.Killed);
		if (gameModel.getState() == GameState.Victory){
			//gameModel.changeState(GameState.Paused);
			vibrate(0,0);
			hitTimer= 0;
		}
//		say ("Location: " + ninja.getLoc().toString());
		
// ------------ Update Guards ------------------------------------------------------------------------		
		
		for (int i= 0; i < guards.length; i++){
			Guard guard= guards[i];
			
			guard.navigate();
			guard.move();
			guard.animate(tick);
			
			// check to see if the guard can see the ninja
			if (guard.isNear()){ // first check if he is even in range
				if (guard.isFacingNinja()){ // next check if the ninja is within guard's field of view
					if (guard.canSeeNinja()){ // now check for line of sight
						say("You have been spotted! Game Over!");
						if (!devmode)
							gameOver(GameState.Spotted);
					}
				}
			}
		}
		
// ------------------------------------------------Music--------------------------------------------
		Sound sound = gameModel.getSound();
		if ((sound.getCurrentMusic() == SoundFile.SNEAK) && boss.isNear())
			sound.delayedPlayMusic(SoundFile.BOSS, 700);
		
// ------------ Update Boss --------------------------------------------------------------------------
		boss.update();
		boss.animate(tick);

		ArrayList<Projectile> projs= gameModel.getProjectiles();
		ArrayList<Projectile> collided= new ArrayList<Projectile>();
		
		for (int i= 0; i < projs.size(); i++){
			Projectile proj= projs.get(i);
			
			proj.move();
			
			if (proj.getModel().getLoc().y <= 0){
				collided.add(proj);
				continue;
			}
			
			if (proj.ninjaCollision()){
				ninja.takeDamage(proj.getAttackDamage());
				collided.add(proj);
//				say("You've been hit! Current HP: "+ ninja.getHP());
				hitTimer= 30;
				continue;
			}
			
			if (proj.obsCollision()){
				collided.add(proj);
			}
		}
		
		for (int j= 0; j < collided.size(); j++){ // destroy all that collided
			Projectile proj= collided.get(j);
			
			gameModel.getModels().remove(proj.getModel());
			gameModel.getProjectiles().remove(proj);
		}
// ---------------------------------------------------------------------------------------------------	
		if (xbc == null || !xbc.isConnected()){
			
			if(gameModel.getWPressed() && !gameModel.getAPressed() && !gameModel.getDPressed()) {
				gameControl.setNinjaSpeed(-1);
				gameModel.getNinja().setAngle(view.getCamAngle());
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
			else if(gameModel.getSPressed() && !gameModel.getAPressed() && !gameModel.getDPressed()) {
				gameControl.setNinjaSpeed(-1);
				gameModel.getNinja().setAngle(view.getCamAngle() - Math.toRadians(180));
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
			else if(gameModel.getAPressed() && !gameModel.getWPressed() && !gameModel.getSPressed()) {
				gameControl.setNinjaSpeed(-1);
				gameModel.getNinja().setAngle(view.getCamAngle() - Math.toRadians(90));
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
			else if(gameModel.getDPressed() && !gameModel.getWPressed() && !gameModel.getSPressed()) {
				gameControl.setNinjaSpeed(-1);
				gameModel.getNinja().setAngle(view.getCamAngle() + Math.toRadians(90));
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
			else if(gameModel.getWPressed() && gameModel.getAPressed()) {
				gameControl.setNinjaSpeed(-1);
				gameModel.getNinja().setAngle(view.getCamAngle() - Math.toRadians(45));
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
			else if(gameModel.getWPressed() && gameModel.getDPressed()) {
				gameControl.setNinjaSpeed(-1);
				gameModel.getNinja().setAngle(view.getCamAngle() + Math.toRadians(45));
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
			else if(gameModel.getSPressed() && gameModel.getAPressed()) {
				gameControl.setNinjaSpeed(-1);
				gameModel.getNinja().setAngle(view.getCamAngle() - Math.toRadians(135));
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
			else if(gameModel.getSPressed() && gameModel.getDPressed()) {
				gameControl.setNinjaSpeed(-1);
				gameModel.getNinja().setAngle(view.getCamAngle() + Math.toRadians(135));
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
			else if(!gameModel.getWPressed() && !gameModel.getAPressed() && !gameModel.getSPressed() && !gameModel.getDPressed()) {
				gameControl.setNinjaSpeed(0);
				gameModel.getNinja().setAngle(view.getCamAngle());
				gameModel.getNinja().setNinjaDirection(gameModel.getNinjaAngle());
			}
		
		}


// ----------------Check level dialog hotspots--------------------------------------------------------
		
		for(int i = 0; i < gameModel.getCurrentLevel().getDialogHotspots().size(); i++) {
			if(gameModel.getCurrentLevel().getDialogHotspots().get(i).checkForActivate(ninja.getLoc())) {
				gameModel.getCurrentLevel().setCurrentDialogHotspot(gameModel.getCurrentLevel().getDialogHotspots().get(i));
				gameModel.changeState(GameState.Dialog);
				break;
			}
		}
		
// ---------------------------------------------------------------------------------------------------	
		
		view.display();
		
	}

	public void setGameControl(GameControl gameControl) {
		this.gameControl = gameControl;
	}
	
	public GameControl getGameControl() {
		return gameControl;
	}
	
	public void moveCamera(){
		double scale= .25;
		double[] dCam= view.getCamDelta();
		double dX= dCam[0];
		double dY= dCam[1];
		
		view.setCamAngle(view.getCamAngle() + dX * scale);
		if (view.getCamHeight() + dY > 0 && view.getCamDistance() >= 5)
			view.setCamHeight((float) (view.getCamHeight() + dY));
		view.setCamDistance(view.getCamDistance() + dY * scale);
	}
	
	public void vibrate(int leftVibrate, int rightVibrate){
		if(xbc != null)
			xbc.vibrate(leftVibrate, rightVibrate);
	}
	
	public void reset(){
		say("Resetting game...");
		
//		window.getContentPane().remove(view);
//		gameModel= new GameModel(); 
//		view= new View(gameModel);
//		
//		window.getContentPane().add(view);
//		window.pack();
//		//window.setLocation(100, 10);
//		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		window.setVisible(true);
//		
//		xbc.release();
//		xbc= new XboxController(dllPath, 1, 50, 50);
//        xbc.setLeftThumbDeadZone(.2);
//        xbc.setRightThumbDeadZone(.2);
//        xbc.addXboxControllerListener(new XboxAdapter(this));
		
		gameModel.getNinja().reset();
		gameModel.getBoss().reset();
		
//		view.setCamAngle(0);
//		view.setCamHeight(4);
//		view.setCamDistance(6);
		view.resetting = true;
		gameModel.changeState(GameState.Paused);
		
		gameModel.getSound().playMusic(SoundFile.SNEAK);
	}
	
	private void gameOver(GameState reason){
		if (reason == GameState.Spotted)
			gameModel.getSound().playEffect(SoundFile.GUARD);
		gameModel.changeState(reason);
		vibrate(0,0);
		hitTimer= 0;
		gameModel.getSound().playMusic(SoundFile.GAMEOVER);
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
