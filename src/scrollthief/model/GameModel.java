package scrollthief.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

/**
 * @author Jon "Neo" Pimentel
 *
 * This class stores all the state data for the game itself, and does the bulk of the computations for
 * the game in general.
 */
public class GameModel {
	public boolean initializing= true;
	public String state= "start";
	final int numGuards= 5;
	final int numWalls= 32;
	final int numPillars= 8;
	final int numTables= 4;
	final int numObs= numWalls + numPillars + numTables + 1;
	int numModels;
	Guard[] guards;
	Obstacle[] obstacles;
	ArrayList<Model> models;
	ArrayList<Projectile> projectiles;
	OBJ[] objs;
	OBJ[] ninjaRun;
	OBJ[] guardWalk;
	Texture[] textures;
	Ninja ninja;
	Boss boss;
	Obstacle scroll;
	
	public GameModel(){
		numModels= numGuards + numObs + 3;
		models= new ArrayList<Model>();
		objs= new OBJ[9];
		ninjaRun= new OBJ[21];
		guardWalk= new OBJ[30];
		textures= new Texture[9];
		guards= new Guard[numGuards];
		obstacles= new Obstacle[numObs];
		projectiles= new ArrayList<Projectile>();
	}
	
	// Loads the default OBJ files
	private void loadOBJs(){
		say("Loading OBJ files...");
		objs[0]= new OBJ("obj/floor.obj");
		objs[1]= new OBJ("obj/ninja_stand.obj");
		objs[2]= new OBJ("obj/guard_stand.obj");
		objs[3]= new OBJ("obj/Scroll");
		objs[4]= new OBJ("obj/table.obj");
		objs[5]= new OBJ("obj/wall2.obj");
		objs[6]= new OBJ("obj/pillar2.obj");
		objs[7]= new OBJ("obj/Boss");
		objs[8]= new OBJ("obj/AcidBlob");
	}
	
	// Loads the OBJ files for every frame of animation
	private void loadAnimations(){
		int lastFrame= 21;
		
		// Ninja run cycle
		say("\nLoading Ninja animation frames...");
		for (int i= 0; i < lastFrame; i++){
			say("Loading run cycle frame " + (i+1));
			String fileName= "obj/anim/ninja/run." + (i+1) + ".obj";
			ninjaRun[i]= new OBJ(fileName);
		}
		
		// Guard walk cycle
		say("\nLoading Guard animation frames...");
		lastFrame= 30;
		for (int i= 0; i < lastFrame; i++){
			say("Loading walk cycle frame " + (i+1));
			String fileName= "obj/anim/guard/walk." + (i+1) + ".obj";
			guardWalk[i]= new OBJ(fileName);
		}
		
		// TODO Boss stomp cycle
	}
	
	private void loadTextures(GL2 gl){
		say("\nLoading texture files...");
		GLProfile profile= gl.getGLProfile();
		String[] imgPaths= new String[9];
		
		imgPaths[0]= "textures/wood.jpg";
		imgPaths[1]= "textures/default.jpg";
		imgPaths[2]= "textures/ninja.bmp";
		imgPaths[3]= "textures/pillar.jpg";
		imgPaths[4]= "textures/table.jpg";
		imgPaths[5]= "textures/wall.jpg";
		imgPaths[6]= "textures/longwall.jpg";
		imgPaths[7]= "textures/shortwall.jpg";
		imgPaths[8]= "textures/guard.jpg";
		
		for (int i= 0; i < imgPaths.length; i++){
			try {
				BufferedImage image= ImageIO.read(new File(imgPaths[i]));
				ImageUtil.flipImageVertically(image);
				
				textures[i]= AWTTextureIO.newTexture(profile, image, false);
				 
			} catch (IOException e) {
				System.out.println("Problem loading texture file " + imgPaths[i]);
				e.printStackTrace();
			}
		}
	}
	
	private void createModels(){
		say("\nCreating 3D models...");
		
// ---------------Environment model/s ------------------------------------------------------------------
		models.add( new Model(objs[0], 0, new Point3D(0, 0, 0), zero(), 1, 1) ); // floor model (0)
// ---------------Character models ---------------------------------------------------------------------
		models.add( new Model(objs[1], 2, new Point3D(0, 0, -5), zero(), .075, 1) ); // ninja model 1
		
		models.add( new Model(objs[2], 8, new Point3D(15, 0, 23), zero(), .11, 1) ); // guard model 2
		models.add( new Model(objs[2], 8, new Point3D(24, 0, 33), zero(), .11, 1) ); // guard model 3
		models.add( new Model(objs[2], 8, new Point3D(32, 0, 39), zero(), .11, 1) ); // guard model 4
		models.add( new Model(objs[2], 8, new Point3D(20, 0, 54), zero(), .11, 1) ); // guard model 5
		models.add( new Model(objs[2], 8, new Point3D(11, 0, 50), zero(), .11, 1) ); // guard model 6
// ---------------Obstacle models ----------------------------------------------------------------------
		models.add( new Model(objs[3], 1, new Point3D(0, .25, 83), new double[]{Math.PI/2,0,0}, .25, 1)); //scroll
		// foyer
		models.add( new Model(objs[5], 6, new Point3D(-4.2, 0, 4.2), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 5, new Point3D(7.8, 0, -10.7), zero(), 1, 1)); // wall
		models.add( new Model(objs[5], 6, new Point3D(-12.4, 0, -3.3), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(12.4, 0, -3.3), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(-4.2, 0, -10.7), zero(), 1, 2)); // wall
		// room 1
		models.add( new Model(objs[5], 6, new Point3D(20.8, 0, 4.2), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(20.8, 0, 29.2), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(29.2, 0, 12.6), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 5, new Point3D(29.2, 0, 24.6), rtAngle(), 1, 1)); // wall
		models.add( new Model(objs[5], 6, new Point3D(4, 0, 21), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 7, new Point3D(11, 0, 29.2), zero(), 1, .5)); // wall
		// room 2 (hard)
		models.add( new Model(objs[5], 6, new Point3D(4, 0, 37), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(12.2, 0, 45.4), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(28.2, 0, 45.4), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 7, new Point3D(38.2, 0, 45.4), zero(), 1, .5)); // wall
		models.add( new Model(objs[5], 6, new Point3D(36.8, 0, 29.2), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(45.2, 0, 37.6), rtAngle(), 1, 2)); // wall
		// room 3 (hard)
		models.add( new Model(objs[5], 6, new Point3D(45.2, 0, 53.6), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(36.8, 0, 62), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(20.8, 0, 62), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 7, new Point3D(11, 0, 62), zero(), 1, .5)); // wall
		models.add( new Model(objs[5], 6, new Point3D(4, 0, 53), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 5, new Point3D(26.6, 0, 61), rtAngle(), 1, 1)); // wall
		models.add( new Model(objs[5], 5, new Point3D(26.6, 0, 49), rtAngle(), 1, 1)); // wall
		// Boss Chamber
		models.add( new Model(objs[5], 6, new Point3D(20.2, 0, 70.4), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(-12.4, 0, 62), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(-20.8, 0, 70.4), rtAngle(), 1, 2)); // wall
		models.add( new Model(objs[5], 5, new Point3D(-20.8, 0, 82.4), rtAngle(), 1, 1)); // wall
		models.add( new Model(objs[5], 6, new Point3D(-12.4, 0, 87), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 6, new Point3D(3.6, 0, 87), zero(), 1, 2)); // wall
		models.add( new Model(objs[5], 5, new Point3D(15.6, 0, 87), zero(), 1, 1)); // wall
		models.add( new Model(objs[5], 5, new Point3D(20.2, 0, 82.4), rtAngle(), 1, 1)); // wall
		// --- Pillars --- room 1
		models.add( new Model(objs[6], 3, new Point3D(10, 0, 11), zero(), .9, 1)); // pillar
		// room 2 (hard)
		models.add( new Model(objs[6], 3, new Point3D(14, 0, 34), zero(), .9, 1)); // pillar
		// room 3 (hard)
		models.add( new Model(objs[6], 3, new Point3D(38, 0, 51), zero(), .9, 1)); // pillar
		models.add( new Model(objs[6], 3, new Point3D(20, 0, 51), zero(), .9, 1)); // pillar
		// Boss Chamber
		models.add( new Model(objs[6], 3, new Point3D(5, 0, 67), zero(), .9, 1)); // pillar
		models.add( new Model(objs[6], 3, new Point3D(11, 0, 76), zero(), .9, 1)); // pillar
		models.add( new Model(objs[6], 3, new Point3D(-7, 0, 69), zero(), .9, 1)); // pillar
		models.add( new Model(objs[6], 3, new Point3D(-13, 0, 77), zero(), .9, 1)); // pillar
		// --- Tables --- room 2 (hard)
		models.add( new Model(objs[4], 4, new Point3D(20, 0, 40), rtAngle(), .4, 1)); // table
		models.add( new Model(objs[4], 4, new Point3D(28.5, 0, 40), rtAngle(), .4, 1)); // table
		models.add( new Model(objs[4], 4, new Point3D(37, 0, 40), rtAngle(), .4, 1)); // table
		// room 3 (hard)
		models.add( new Model(objs[4], 4, new Point3D(15.5, 0, 58.4), rtAngle(), .4, 1)); // table
		// --- Ceiling ---
		models.add( new Model(objs[0], 0, new Point3D(0, 5, 0), new double[] {Math.PI,0,0}, 1, 1) ); // ceiling model
		// --- Boss model ---
		models.add( new Model(objs[7], 1, new Point3D(0, 0, 76), zero(), .2, 1)); // Boss
	}
	
	// create the waypoints the guards will use for their patrol orders
	private Point3D[][] createOrders(){
		Point3D[][] orders= new Point3D[][]{
/*guard 0*/	new Point3D[]{new Point3D(6,0,26),new Point3D(16,0,26)}, 
/*guard 1*/	new Point3D[]{new Point3D(24,0,31),new Point3D(24,0,41)},
/*guard 2*/	new Point3D[]{new Point3D(32,0,31),new Point3D(32,0,41)},
/*guard 3*/	new Point3D[]{new Point3D(37,0,54),new Point3D(14,0,54)},
/*guard 4*/	new Point3D[]{new Point3D(11,0,60),new Point3D(11,0,47)},
		};
		
		return orders;
	}
	
	private void createCharacters(){
		say("Creating characters...");
		ninja= new Ninja(this, models.get(1), .35, .5);
		
		Point3D[][] orders= createOrders();
		for (int i= 0; i < numGuards; i++){
			guards[i]= new Guard(this, models.get(i + 2), .35, .5, orders[i]); 
		}
		boss= new Boss(this, models.get(models.size()-1), 1.5, 1.5);
	}
	
	private void createObstacles(){
		say("Creating obstacles...");
		// scroll
		obstacles[0]= new Obstacle(models.get(7), true, .75, .2, .2); 
		scroll= obstacles[0];
		// walls
		for (int i= 1; i < numWalls+1; i++){
			obstacles[i]= new Obstacle(models.get(i + 7), false, .6, 4, 5); 
		}
		// pillars
		for (int i= numWalls+1; i < numPillars+numWalls+1; i++){
			obstacles[i]= new Obstacle(models.get(i + 7), false, 1.5, 1.5, 10); 
		}
		// tables
		for (int i= numWalls+numPillars+1; i < numObs; i++){
			obstacles[i]= new Obstacle(models.get(i + 7), true, 2.25, 3, .95); 
		}
	}
	
	public void init(GL2 gl){
		loadOBJs();
		loadAnimations();
		loadTextures(gl);
		createModels();
		createCharacters();
		createObstacles();
		say("--Game model loaded--\n\nStarting game. Good luck!\n");
		initializing= false;
	}
	
	public Guard[] getGuards(){
		return guards;
	}
	
	public Obstacle[] getObstacles(){
		return obstacles;
	}
	
	public Ninja getNinja(){
		return ninja;
	}
	
	public Boss getBoss(){
		return boss;
	}
	
	public ArrayList<Projectile> getProjectiles(){
		return projectiles;
	}
	
	public Obstacle getScroll(){
		return scroll;
	}
	
	public OBJ[] getOBJs(){
		return objs;
	}
	
	public ArrayList<Model> getModels(){
		return models;
	}

	public Texture[] getTextures(){
		return textures;
	}
	
	public OBJ[] getGuardWalk(){
		return guardWalk;
	}
	
	public OBJ[] getNinjaRun(){
		return ninjaRun;
	}
	
	public double getNinjaAngle(){
		return ninja.getAngle();
	}
	
	public double getNinjaSpeed(){
		return ninja.getSpeed();
	}
	
	public Point3D getNinjaLoc(){
		return ninja.getLoc();
	}
	
	public void setNinjaAngle(double newAngle){
		if (ninja != null)
//			ninja.setAngle(newAngle);
			ninja.setGoalAngle(newAngle);
	}
	
	public void setNinjaSpeed(double newSpeed){
		if (ninja != null)
			ninja.setSpeed(newSpeed);
	}
	
	public void setNinjaLoc(Point3D newPoint){
		if (ninja != null)
			ninja.setLoc(newPoint);
	}
	
	public double floorMod(double a, double n){
		return a - Math.floor(a/n) * n;
	}
	
	public static Point2D[][] boxToWorld(Model model, Point2D[][] oldBox){
		Point2D[] points= new Point2D[4];
		points[0]= oldBox[0][0];
		points[1]= oldBox[1][0];
		points[2]= oldBox[2][0];
		points[3]= oldBox[3][0];
		
		return boxToWorld(model, points);
	}
	
	public static Point2D[][] boxToWorld(Model model, Point2D[] points){
		double angle= model.getAngle();
		Point2D center= model.getLoc().to2D();
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
	
	private double[] zero(){
		return new double[]{0, 0, 0};
	}
	
	private double[] rtAngle(){
		return new double[]{0, Math.PI/2, 0};
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
