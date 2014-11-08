package scrollthief.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
		models[1]= new Model(objs[1], 1, new Point3D(0, 0, -4), new double[]{0,Math.PI,0}, .075); // ninja model
		models[2]= new Model(objs[1], 1, new Point3D(0, 0, 2), new double[]{0,-Math.PI,0}, .075); // a guard model
// ---------------Obstacle models ----------------------------------------------------------------------
		models[3]= new Model(objs[3], 1, new Point3D(0, 0, -1), new double[]{0,Math.PI,0}, 1); 
	}
	
	private void createCharacters(){
		say("Creating characters...");
		ninja= new Character(this, models[1]);
		guards[0]= new Guard(this, models[2]);
	}
	
	private void createObstacles(){
		say("Creating obstacles...");
		obstacles[0]= new Obstacle(models[3], true, .6, .4);
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
		double[] ninjaRot= models[1].getRot();
		ninjaRot[1]= newAngle;
		models[1].setRot(ninjaRot);
	}
	
	public void setNinjaSpeed(double newSpeed){
		ninja.setSpeed(newSpeed);
	}
	
	public void setNinjaLoc(Point3D newPoint){
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
	
	private void say(String message){
		System.out.println(message);
	}
}
