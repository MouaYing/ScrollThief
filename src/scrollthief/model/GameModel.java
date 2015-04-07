package scrollthief.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import javax.swing.event.EventListenerList;

import com.jogamp.opengl.util.texture.Texture;

/**
 * @author Jon "Neo" Pimentel
 *
 * This class stores all the state data for the game itself, and does the bulk of the computations for
 * the game in general.
 */
public class GameModel {
	Resource resource;
	private HashMap<String, ArrayList<String>> loadingPhrases;
	private ArrayList<Button> mainMenuButtons;
	private ArrayList<Button> pauseButtons;
	private GameRestore restore;
	private GameData data;
	private GameState state = GameState.Uninitialized;
	private GameState lastState;
	private Level currentLevel;
	private LevelFactory levelFactory;
	
	private boolean aPressed;
	private boolean sPressed;
	private boolean dPressed;
	private boolean wPressed;
	
	protected EventListenerList listenerList = new EventListenerList();

	  public void addStateChangedListener(StateChangedListener listener) {
	    listenerList.add(StateChangedListener.class, listener);
	  }
	  public void removeStateChangedListener(StateChangedListener listener) {
	    listenerList.remove(StateChangedListener.class, listener);
	  }
	  void fireStateChanged(StateChange evt) {
	    Object[] listeners = listenerList.getListenerList();
	    for (int i = 0; i < listeners.length; i = i+2) {
	      if (listeners[i] == StateChangedListener.class) {
	        ((StateChangedListener) listeners[i+1]).stateChanged(evt);
	      }
	    }
	  }
	
	public GameModel(){
		restore = new GameRestore();
		data = restore.getData();
		loadingPhrases = new HashMap<String, ArrayList<String>>();
		ArrayList<String> phrases = new ArrayList<String>();
		phrases.add("Training with Sensai...");
		phrases.add("Sharpening Katana...");
		phrases.add("Meditating...");
		loadingPhrases.put("resource", phrases);
		phrases = new ArrayList<String>();
		phrases.add("Preparing Ninja Skills...");
		phrases.add("Meditating...");
		phrases.add("Memorizing Floor Plan...");
		loadingPhrases.put("level", phrases);
		pauseButtons = new ArrayList<Button>();
		pauseButtons.add(new Button(25,175,100,50, ButtonType.RESUME,true, this));
		pauseButtons.add(new Button(25,100,100,50, ButtonType.RESTART,false, this));
		pauseButtons.add(new Button(25,25,100,50, ButtonType.SAVE,false, this));
		pauseButtons.add(new Button(175,100,100,50, ButtonType.MAINMENU,false, this));
		pauseButtons.add(new Button(175,25,100,50, ButtonType.QUIT,false, this));
		

		mainMenuButtons = new ArrayList<Button>();
		mainMenuButtons.add(new Button(100,175,100,50, ButtonType.START,true, this));
		mainMenuButtons.add(new Button(100,100,100,50, ButtonType.CONTINUE,false, this));
		mainMenuButtons.add(new Button(100,25,100,50, ButtonType.QUIT,false, this));
		
		resource = new Resource(this, loadingPhrases);

		levelFactory = new LevelFactory();
		
		currentLevel = levelFactory.getNextLevel(resource, this, loadingPhrases);
		changeState(GameState.Initialized);
		
		this.aPressed = false;
		this.sPressed = false;
		this.dPressed = false;
		this.wPressed = false;
	}
	
	public boolean getAPressed() {
		return aPressed;
	}
	
	public void setAPressed(boolean aPressed) {
		this.aPressed = aPressed;
	}
	
	public boolean getSPressed() {
		return sPressed;
	}
	
	public void setSPressed(boolean sPressed) {
		this.sPressed = sPressed;
	}
	
	public boolean getDPressed() {
		return dPressed;
	}
	
	public void setDPressed(boolean dPressed) {
		this.dPressed = dPressed;
	}
	
	public boolean getWPressed() {
		return wPressed;
	}
	
	public void setWPressed(boolean wPressed) {
		this.wPressed = wPressed;
	}
	
	public void saveGame() {
		ArrayList<Point3D> locations = new ArrayList<Point3D>();
		locations.add(getNinja().getLoc());
		locations.add(getBoss().getLoc());
		data = new GameData(locations, state, levelFactory.getLevelState());
		restore = new GameRestore(data);
		if(restore.Save()){
			resetLevel();
			resetLevelLoading();
			changeState(GameState.MainMenu);
		}
		else {
			say("Error Saving Game Data");
		}
	}
	
	public void continueGame() {
		if(restore.Load()){
			data = restore.getData();
		}
		else{
			data = new GameData();
		}
		levelFactory = new LevelFactory(data.getLevel());
		currentLevel = levelFactory.getNextLevel(resource, this, loadingPhrases);
		changeState(GameState.LevelLoading);
	}
	
	private void initializeLevel(){
		currentLevel.initialize();
	}
	
	public void init(final GL2 gl){
		resource.loadImages(gl);
		resource.loadHeart(gl);
		changeState(GameState.ResourceLoading);
		Thread resourceThread = new Thread() {
			public void run() {
				resource.init();
			}
		};
		resourceThread.start();
		resource.loadTextures(gl);
	}
	
	public void loadLevel(){
		initializeLevel();
	}

	public void reloadMusic() {
		resource.reloadMusic();
	}
	
	public void finishedLoading(String type){
		if(type.equals("resource")){
			say("MainMenu");
			changeState(GameState.MainMenu);
		}
		else if(type.equals("level")){
			say("Waiting to start");
			changeState(GameState.Start);
		}
	}
	
	//This makes 
	public void changeState(GameState newState) {
		say("Game State moving from " + state + " to " + newState);
		if(state == GameState.Start){
			newState = data.getState();
			getNinja().setLoc(data.getLocations().get(0));
			getBoss().setLoc(data.getLocations().get(1));
			resource.getSound().playMusic(SoundFile.SNEAK);
		}
		lastState = state;
		state = newState;
		fireStateChanged(new StateChange(newState));
		if(newState == GameState.LevelLoading){
			loadLevel();
		}
	}
	
	public void resetLevel() {
		getNinja().reset();
		getBoss().reset();
	}
	
	public void resetLevelLoading() {
		currentLevel.reset();
	}
	
	public Texture getSplashImage(){
		return resource.getSplashImage();
	}
	
	public Texture getLevelSplashImage(){
		return resource.getLevelSplash();
	}
	
	public Texture getHeart() {
		return resource.getHeart();
	}
	
	public GameState getState(){
		return state;
	}
	
	public GameState getLastState(){
		return lastState;
	}
	
	public Sound getSound() {
		return resource.getSound();
	}
	
	public Guard[] getGuards(){
		return currentLevel.getGuards();
	}
	
	public LoadingBar getResourceLoadingBar(){
		return resource.getLoadingBar();
	}
	
	public LoadingBar getLevelLoadingBar() {
		return currentLevel.getLoadingBar();
	}
	
	public Obstacle[] getObstacles(){
		return currentLevel.getObstacles();
	}
	
	public Ninja getNinja(){
		return currentLevel.getNinja();
	}
	
	public Boss getBoss(){
		return currentLevel.getBoss();
	}
	
	public ArrayList<Projectile> getProjectiles(){
		return currentLevel.getProjectiles();
	}
	
	public Obstacle getScroll(){
		return currentLevel.getScroll();
	}
	
	public ArrayList<Model> getModels(){
		return currentLevel.getModels();
	}

	public double getNinjaAngle(){
		if(getNinja() == null){
			return 0;
		}
		return getNinja().getAngle();
	}
	
	public double getNinjaSpeed(){
		return getNinja().getSpeed();
	}
	
	public Level getCurrentLevel() {
		return currentLevel;
	}
	
	public Point3D getNinjaLoc(){
		if(getNinja() == null){
			return new Point3D(0,0,0);
		}
		return getNinja().getLoc();
	}
	
	public void setNinjaAngle(double newAngle){
		if (getNinja() != null)
//			ninja.setAngle(newAngle);
			getNinja().setGoalAngle(newAngle);
	}
	
	public void setNinjaSpeed(double newSpeed){
		if (getNinja() != null)
			getNinja().setSpeed(newSpeed);
	}
	
	public void setNinjaLoc(Point3D newPoint){
		if (getNinja() != null)
			getNinja().setLoc(newPoint);
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public LevelFactory getLevelFactory() {
		return levelFactory;
	}
	
//Button Code
	public void doPauseButton() {
		for(Button b : pauseButtons){
			if(b.IsSelected()){
				b.doAction();
			}
		}
	}
	
	public void doMainMenuButton() {
		for(Button b : mainMenuButtons){
			if(b.IsSelected()){
				b.doAction();
			}
		}
	}

	public ArrayList<Button> getPauseButtons() {
		return pauseButtons;
	}
	public ArrayList<Button> getMainMenuButtons() {
		// TODO Auto-generated method stub
		return mainMenuButtons;
	}
	
	public double floorMod(double a, double n){
		return a - Math.floor(a/n) * n;
	}
	
	public static Point2D[][] boxToWorld(Model model, Point2D[][] oldBox){
		return boxToWorld(model.getAngle(), model.getLoc(), oldBox);
	}
	
	public static Point2D[][] boxToWorld(double angle, Point3D loc, Point2D[][] oldBox){
		Point2D[] points= new Point2D[4];
		points[0]= oldBox[0][0];
		points[1]= oldBox[1][0];
		points[2]= oldBox[2][0];
		points[3]= oldBox[3][0];
		
		return boxToWorld(angle, loc, points);
	}
	
	public static Point2D[][] boxToWorld(Model model, Point2D[] points){
		return boxToWorld(model.getAngle(), model.getLoc(), points);
	}
	
	public static Point2D[][] boxToWorld(double angle, Point3D loc, Point2D[] points){
		Point2D center= loc.to2D();
		//Point2D.Double[] points= new Point2D.Double[4];
		
		Point2D.Double newP1= new Point2D.Double();
		Point2D.Double newP2= new Point2D.Double();
		Point2D.Double newP3= new Point2D.Double();
		Point2D.Double newP4= new Point2D.Double();
		
		// construct obj to world transformation matrix
		AffineTransform objToWorld= 
				new AffineTransform(Math.cos(angle), Math.sin(angle), 
						-Math.sin(angle), Math.cos(angle), center.getX(), center.getY());
		
		// transform each point
		objToWorld.transform(points[0], newP1);
		objToWorld.transform(points[1], newP2);
		objToWorld.transform(points[2], newP3);
		objToWorld.transform(points[3], newP4);
		
		return createHitBox(newP1, newP2, newP3, newP4);
	}
	
	// creates the list of hitbox edges that correspond to the given 4 points
	public static Point2D[][] createHitBox(Point2D[] points){
		return createHitBox(points[0], points[1], points[2], points[3]);
	}
	
	public static Point2D[][] createHitBox(Point2D p1, Point2D p2, Point2D p3, Point2D p4){
		Point2D[][] hitBox= new Point2D[4][2];
		
		hitBox[0][0]= p1;
		hitBox[0][1]= p2;
		
		hitBox[1][0]= p2;
		hitBox[1][1]= p3;
		
		hitBox[2][0]= p3;
		hitBox[2][1]= p4;
		
		hitBox[3][0]= p4;
		hitBox[3][1]= p1;
		
		return hitBox;
	}
	
	public static Point2D[] findPoints(double width, double length){
		// these are really half the width and height of the box---dividing everything by 2 would be silly
		Point2D[] points= new Point2D[4];
		
		points[0]= new Point2D.Double(-width, length);
		points[1]= new Point2D.Double(width, length);
		points[2]= new Point2D.Double(width, -length);
		points[3]= new Point2D.Double(-width, -length);
		
		return points;
	}
	
	public ArrayList<Point2D[]> collision(Point2D[][] box1, Point2D[][] box2, ArrayList<Point2D[]> edges){
		for (int i= 0; i < 4; i++){
			boxHit(box1[i][0], box1[i][1], box2, edges) ;
		}

		return edges;
	}
	
	// calculate if the given line hits this obstacle
	public ArrayList<Point2D[]> boxHit(Point3D p1, Point3D p2, Point2D[][] box){
		ArrayList<Point2D[]> edges= new ArrayList<Point2D[]>();
		return boxHit(p1.to2D(), p2.to2D(), box, edges);
	}
//	public boolean boxHit(Point3D p1, Point3D p2){
//		return boxHit(p1.to2D(), p2.to2D());
//	}
	
	public ArrayList<Point2D[]> boxHit(Point2D p1, Point2D p2, Point2D[][] hitBox, ArrayList<Point2D[]> edges){
		// I found a way to test for intersection using only -, *, &&, and comparisons. Much faster.
		for (int i= 0; i < 4; i++){ // test each edge for an intersection with the line
			Point2D p3= hitBox[i][0];
			Point2D p4= hitBox[i][1];
			
			if ( (CCW(p1, p3, p4) != CCW(p2, p3, p4)) && (CCW(p1, p2, p3) != CCW(p1, p2, p4)) )
				edges.add( new Point2D[] {p3, p4});
		}
		return edges;
	}
	
	private boolean CCW(Point2D p1, Point2D p2, Point2D p3) {
		double a = p1.getX(); double b = p1.getY(); 
		double c = p2.getX(); double d = p2.getY();
		double e = p3.getX(); double f = p3.getY();
		return (f - b) * (c - a) > (d - b) * (e - a);
	}
	
	// makes any angle be between +/= pi
	public static double normalizeAngle(double angle){
		angle -= 2 * Math.PI * (int) (angle / (2 * Math.PI));
		if (angle <= -Math.PI)
			angle += 2 * Math.PI;
		else if (angle > Math.PI)
			angle -= 2 * Math.PI;
		
		return angle;
	}
	
	public double[] zero(){
		return new double[]{0, 0, 0};
	}
	
	public double[] rtAngle(){
		return new double[]{0, Math.PI/2, 0};
	}
	
	private void say(String message){
		System.out.println(message);
	}
}



