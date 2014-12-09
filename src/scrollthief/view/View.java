package scrollthief.view;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

import scrollthief.model.Point3D;
import scrollthief.model.Model;
import scrollthief.model.GameModel;

public class View extends GLCanvas implements GLEventListener{
	private static final long serialVersionUID = 1L;
	GameModel gameModel;
	GL2 gl;
	public boolean init= true;
	int windowX= 710;
	int windowY= 710;
	GLUT glut= new GLUT();
	float FOV= 45f;
	float ASPECT = 1f;
	float NEAR= .1f;
	float FAR= 3000f;
	public boolean resetting= false;
	float[] lookFrom= {2,4,5};
	float[] lookAt= {0,2.5f,0};
	double cameraDistance= 6;
	double cameraAngle= 0;
	double cameraRotRate= 0;
	double[] cameraDelta= {0,0};
	TextRenderer tRend;
	TextRenderer tRend2;

	public View(GameModel model){
		say("Loading view...");
		this.gameModel= model;
		setPreferredSize(new java.awt.Dimension(windowX, windowY));
        addGLEventListener(this);
        init= false;
		say("--View loaded--\n");
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		gl= drawable.getGL().getGL2();
		tRend= new TextRenderer(new Font("Helvetica", Font.BOLD, 30));
		tRend2= new TextRenderer(new Font("Helvetica", Font.BOLD, 60));
		gameModel.init(gl);
		
		setupLighting(gl);
		
		gl.glShadeModel(GL2.GL_SMOOTH);							// Enable Smooth Shading
		gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);				// grey Background
		gl.glClearDepth(1.0f);									// Depth Buffer Setup
		gl.glEnable(GL2.GL_DEPTH_TEST);							// Enables Depth Testing
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glDepthFunc(GL2.GL_LEQUAL);								// The Type Of Depth Testing To Do
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU glu= new GLU();
		glu.gluPerspective(FOV, ASPECT, NEAR, FAR);
		glu.gluLookAt(lookFrom[0], lookFrom[1], lookFrom[2],
		        lookAt[0], lookAt[1], lookAt[2],
		        0.0f, 1.0f, 0.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		if (init){
			return;
		}
		gl = drawable.getGL().getGL2();
		ArrayList<Model> models= gameModel.getModels();
		Texture[] textures= gameModel.getTextures();
		
		// apply world-to-camera transform
		world2camera(gl);
				
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glColor3f(1f, 1f, 1f);
		
		// draw each model with its texture
		for (int i= 0; i < models.size(); i++){
			gl.glPushMatrix();
			Model model= models.get(i);
			
			// assign texture
			textures[model.getTxtr()].bind(gl);
			
			//apply object to window transform
			obj2world(gl, model);
			
			model.getObj().DrawModel(gl);
			
			gl.glPopMatrix();
			gl.glFlush();
		}
		if (gameModel.state.equals("spotted")){
			String text= "You've been spotted!";
			overlayText(text,  windowX/2 - (30 * text.length()/2), windowY/2 + 100, Color.blue, "big");
			
			text= "Mission Failed! Press start to try again...";
			overlayText(text,  windowX/2 - (15 * text.length()/2), windowY/2 + 50, Color.blue, "reg");
		}
		else if (gameModel.state.equals("killed")){
			String text= "You've been killed!";
			overlayText(text,  windowX/2 - (30 * text.length()/2), windowY/2 + 100, Color.red, "big");
			
			text= "Mission Failed! Press start to try again...";
			overlayText(text,  windowX/2 - (15 * text.length()/2), windowY/2 + 50, Color.blue, "reg");
		}
		else if (gameModel.state.equals("victory")){
			String text= "Mission Complete!";
			overlayText(text,  windowX/2 - (30 * text.length()/2), windowY/2 + 100, Color.blue, "big");
			
			text= "You obtained the enemy intelligence!";
			overlayText(text,  windowX/2 - (15 * text.length()/2), windowY/2 + 50, Color.blue, "reg");
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		windowX= width;
		windowY= height;
		GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(x, y, width, height);
		
		ASPECT = (float)width / (float)height;
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU glu= new GLU();
		glu.gluPerspective(FOV, ASPECT, NEAR, FAR);
		glu.gluLookAt(lookFrom[0], lookFrom[1], lookFrom[2],
		        lookAt[0], lookAt[1], lookAt[2],
		        0.0f, 1.0f, 0.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void overlayText(String text, int x, int y, Color color, String type){
		TextRenderer rend= tRend;
		if (type == "big")
			rend= tRend2;
			
		rend.setColor(color);
		rend.beginRendering(windowX, windowY, true);
		rend.draw(text, x, y);
		rend.endRendering();
		rend.flush();
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		
	}
	
	private void setupLighting(GL2 gl){
		float[] lightPos = { 2000,3000,2000,1 };        // light position
		float[] ambiance = { 0.1f, 0.1f, 0.1f, 1f };     // low ambient light
		float[] diffuse = { 1f, 1f, 1f, 1f };        // full diffuse color

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambiance, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,lightPos, 0);
	}
	
	// calculates and applies the transformation matrix for object to world
	private void obj2world(GL2 gl, Model model){
		double[] rot= model.getRot();
		double angle= Math.toDegrees(rot[1]);
		double angleX= Math.toDegrees(rot[0]);
		double scale= model.getScale();
		double scaleX= scale * model.scaleX;
		Point3D loc= model.getLoc();
	
		gl.glTranslated(loc.x, loc.y, loc.z);
		gl.glRotated(angle, 0, -1, 0);
		gl.glRotated(angleX, 1, 0, 0);
		gl.glScaled(scaleX, scale, scale);
	}
	
	private void world2camera(GL2 gl){
		float scale= .6f; 
		double dZ= 0;
		double dX= 0;
		double ninjaAngle = gameModel.getNinjaAngle();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		lookAt= new float[]{(float) ninjaLoc.x, (float) ninjaLoc.y * scale + 2, (float) ninjaLoc.z};
		
		if (resetting){ // center the camera behind the ninja
//			dZ= Math.cos(ninjaAngle + Math.PI + cameraAngle); 
//			dX= Math.sin(ninjaAngle + Math.PI + cameraAngle);
			cameraAngle= ninjaAngle;
			cameraDistance= 6;
			lookFrom[1]= 4;
		}
		
		dZ= -Math.cos(cameraAngle); 
		dX= Math.sin(cameraAngle);
		
		lookFrom[2]= lookAt[2] + (float) (cameraDistance * dZ);
		lookFrom[0]= lookAt[0] + (float) (cameraDistance * dX);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU glu= new GLU();
		glu.gluPerspective(FOV, ASPECT, NEAR, FAR);
		glu.gluLookAt(lookFrom[0], lookFrom[1], lookFrom[2],
		        lookAt[0], lookAt[1], lookAt[2],
		        0.0f, 1.0f, 0.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		//gl.glLoadIdentity();
	}
	
// -----------------Camera getters -----------------------------
	public double getCamDistance(){
		return cameraDistance;
	}
	
	public float getCamHeight(){
		return lookFrom[1];
	}
	
	public double getCamAngle(){
		return cameraAngle;
	}
	
	public double[] getCamDelta(){
		return cameraDelta;
	}
	
// -----------------Camera setters ------------------------------------
	public void setCamDistance(double distance){
		cameraDistance= distance;
	}
	
	public void setCamHeight(float height){
		lookFrom[1]= height;
	}
	
	public void setCamAngle(double angle){
		cameraAngle= angle;
	}
	
	public void setCamRotRate(double rate){
		cameraRotRate= rate;
	}
	
	public void setCamDelta(double[] delta){
		cameraDelta= delta;
	}
// --------------------------------------------------------------------
	
	private void say(String message){
		System.out.println(message);
	}
}
