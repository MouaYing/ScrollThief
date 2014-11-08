package scrollthief.model;

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
	Model[] models;
	OBJ[] objs;
	Texture[] textures;
	Character ninja;
	
	public GameModel(){
		models= new Model[3];
		objs= new OBJ[3];
		textures= new Texture[2];
		guards= new Guard[1];
	}
	
	private void loadOBJs(){
		say("Loading OBJ files...");
		objs[0]= new OBJ("obj/ParkingLot.obj");
		//objs[1]= new OBJ("obj/ninja.obj");
		objs[1]= new OBJ("obj/ninja_tri.obj");
		objs[2]= new OBJ("obj/guard.obj");
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
		models[2]= new Model(objs[2], 1, new Point3D(0, 0, 2), new double[]{0,-Math.PI/2,0}, .25); // a guard model
// ---------------Obstacle models ----------------------------------------------------------------------
		// TODO add obstacle models
	}
	
	private void createCharacters(){
		say("Creating characters...");
		ninja= new Character(this, models[1]);
		guards[0]= new Guard(this, models[2]);
	}
	
	public void init(GL2 gl){
		loadOBJs();
		loadTextures(gl);
		createModels();
		createCharacters();
		//TODO create obstacles
		say("--Game model loaded--\n");
		initializing= false;
	}
	
	public Guard[] getGuards(){
		return guards;
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
	
	private void say(String message){
		System.out.println(message);
	}
}
