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

public class GameModel {
	public boolean initializing= true;
	Guard[] guards;
	Obstacle[] obstacles;
	Model[] models;
	OBJ[] objs;
	Texture[] textures;
	Character ninja;
	
	public GameModel(){
		models= new Model[4];
		objs= new OBJ[4];
		textures= new Texture[2];
		guards= new Guard[1];
		obstacles= new Obstacle[1];
	}
	
	private void loadOBJs(){
		say("Loading OBJ files...");
		objs[0]= new OBJ("obj/ParkingLot.obj");
		//objs[1]= new OBJ("obj/ninja.obj");
		objs[1]= new OBJ("obj/ninja_tri.obj");
		objs[2]= new OBJ("obj/guard.obj");
		objs[3]= new OBJ("obj/car.obj"); // standing in for an obstacle for now
	}
	
	private void loadTextures(GL2 gl){
		say("Loading texture files...");
		GLProfile profile= gl.getGLProfile();
		String[] imgPaths= new String[2];
		
		imgPaths[0]= "textures/ParkingLot.bmp";
		imgPaths[1]= "textures/default.jpg";
		
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
		say("Creating 3D models...");
		double[] zero= new double[] {0,0,0};
		
// ---------------Environment model/s ------------------------------------------------------------------
		models[0]= new Model(objs[0], 0, new Point3D(0, 0, 0), zero, 1); // lot model
// ---------------Character models ---------------------------------------------------------------------
		models[1]= new Model(objs[1], 1, new Point3D(0, 0, -5), new double[]{0,Math.PI,0}, .075); // ninja model
		models[2]= new Model(objs[1], 1, new Point3D(0, 0, 2), new double[]{0,-Math.PI,0}, .075); // a guard model
// ---------------Obstacle models ----------------------------------------------------------------------
		models[3]= new Model(objs[3], 1, new Point3D(0, 0, -3), new double[]{0,Math.PI/2,0}, 3); // a car for now
	}
	
	private void createCharacters(){
		say("Creating characters...");
		ninja= new Character(this, models[1], .2, .4);
		guards[0]= new Guard(this, models[2], .2, .4);
	}
	
	private void createObstacles(){
		say("Creating obstacles...");
//		obstacles[0]= new Obstacle(models[3], true, .6, .4);
		obstacles[0]= new Obstacle(models[3], true, 2.2, 1.2, 1);
	}
	
	public void init(GL2 gl){
		loadOBJs();
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
	
	public Character getNinja(){
		return ninja;
	}
	
	public Model[] getModels(){
		return models;
	}

	public Texture[] getTextures(){
		return textures;
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
	
	private void say(String message){
		System.out.println(message);
	}
}
