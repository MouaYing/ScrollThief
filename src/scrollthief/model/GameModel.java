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
	Model[] models;
	OBJ[] objs;
	Texture[] textures;
	int ninjaIndex= 1;
	double ninjaSpeed= 0;
	
	public GameModel(){
		models= new Model[2];
		objs= new OBJ[2];
		textures= new Texture[2];
		
		loadOBJs();
//		loadTextures(gl);
//		createModels();
		
		say("--Game Model loaded--");
	}
	
	private void loadOBJs(){
		say("Loading OBJ files...");
		objs[0]= new OBJ("obj/ParkingLot.obj");
		//objs[1]= new OBJ("obj/ninja.obj");
		objs[1]= new OBJ("obj/ninja_tri.obj");
	}
	
	public void loadTextures(GL2 gl){
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
	
	public void createModels(){
		say("Creating 3D models...");
		double[] lotRot= new double[] {0,0,0};
		double[] ninjaRot= new double[] {0,Math.PI,0};
		
		models[0]= new Model(objs[0], 0, new Point3D(0, 0, 0), lotRot, 1); // lot model
		models[1]= new Model(objs[1], 1, new Point3D(0, 0, -4), ninjaRot, .1); // ninja model
	}
	
	public Model[] getModels(){
		return models;
	}

	public Texture[] getTextures(){
		return textures;
	}
	
	public double getNinjaAngle(){
		return models[ninjaIndex].getRot()[1];
	}
	
	public double getNinjaSpeed(){
		return ninjaSpeed;
	}
	
	public Point3D getNinjaLoc(){
		return models[ninjaIndex].getLoc();
	}
	
	public void setNinjaAngle(double newAngle){
		double[] ninjaRot= models[1].getRot();
		ninjaRot[1]= newAngle;
		models[1].setRot(ninjaRot);
	}
	
	public void setNinjaSpeed(double newSpeed){
		ninjaSpeed= newSpeed;
	}
	
	public void setNinjaLoc(Point3D newPoint){
		models[ninjaIndex].setLoc(newPoint);
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
