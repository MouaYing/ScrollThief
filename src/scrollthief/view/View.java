package scrollthief.view;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

import scrollthief.model.GameState;
import scrollthief.model.LoadingBar;
import scrollthief.model.Data;
import scrollthief.model.Point3D;
import scrollthief.model.Model;
import scrollthief.model.GameModel;

public class View extends GLCanvas implements GLEventListener{
	private static final long serialVersionUID = 1L;
	GameModel gameModel;
	GL2 gl;
	public boolean init= true;
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
	DialogRenderer dialogRenderer;
	
	public View(GameModel model){
		say("Loading view...");
		this.gameModel= model;
		setPreferredSize(new java.awt.Dimension(Data.windowX, Data.windowY));
        addGLEventListener(this);
        init= false;
		say("--View loaded--\n");
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		gl= drawable.getGL().getGL2();
		dialogRenderer = new DialogRenderer(gl);
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
		
		// apply world-to-camera transform
		world2camera(gl);
				
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glColor3f(1f, 1f, 1f);
		

		if(gameModel.getState() != GameState.ResourceLoading && gameModel.getState() != GameState.LevelLoading){

			ArrayList<Model> models= gameModel.getModels();
			Texture[] textures= gameModel.getResource().getTextures();
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
		}
		else if(gameModel.getState() == GameState.ResourceLoading){
			
			//Display the 
			displaySplashImage(gameModel.getSplashImage());
			
			//Display Loading Bar
			LoadingBar loading = gameModel.getResourceLoadingBar();

			drawLoadingBar(gl,loading);
			
			//Display Game Title
			String text = "THE SCROLL THIEF";
			dialogRenderer.overlayText(text, (int)(Data.windowX/2 - Data.windowX*.15), Data.windowY-100, Color.white, "reg");
			
			gl.glEnable(GL2.GL_TEXTURE_2D);
		}
		else if(gameModel.getState() == GameState.LevelLoading){
			
		}
		if (gameModel.getState() == GameState.Paused){
			String text= "Steal the enemy battle plans (scroll)";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (15 * text.length()/2), Data.windowY/2 + 150, Color.blue, "reg");
			text= "without being detected";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (15 * text.length()/2), Data.windowY/2 + 100, Color.blue, "reg");
			text= "Press start to begin";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (30 * text.length()/2), Data.windowY/2, Color.blue, "big");
		}
		else if (gameModel.getState() == GameState.Spotted){
			String text= "You've been spotted!";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (30 * text.length()/2), Data.windowY/2 + 100, Color.blue, "big");
			
			text= "Mission Failed! Press start to try again...";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (15 * text.length()/2), Data.windowY/2 + 50, Color.blue, "reg");
		}
		else if (gameModel.getState() == GameState.Killed){
			String text= "You've been killed!";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (30 * text.length()/2), Data.windowY/2 + 100, Color.red, "big");
			
			text= "Mission Failed! Press start to try again...";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (15 * text.length()/2), Data.windowY/2 + 50, Color.blue, "reg");
		}
		else if (gameModel.getState() == GameState.Victory){
			String text= "Mission Complete!";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (30 * text.length()/2), Data.windowY/2 + 100, Color.blue, "big");
			
			text= "You obtained the enemy battle plans!";
			dialogRenderer.overlayText(text,  Data.windowX/2 - (15 * text.length()/2), Data.windowY/2 + 50, Color.blue, "reg");
		}
		else if(gameModel.getState() == GameState.Dialog) {
			if(getDialogRenderer().isFinished()) {
				gameModel.changeState(GameState.Playing);
				gameModel.getCurrentLevel().getCurrentDialogHotspot().setRepeatable(false);
			}
			else
				dialogRenderer.render(gameModel.getCurrentLevel().getCurrentDialogHotspot().getText());
		}
	}
	
	private void displaySplashImage(Texture t){
		gl.glPushMatrix();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, Data.windowX, Data.windowY, 0, -10, 10);
		gl.glEnable( GL2.GL_TEXTURE_2D );
		t.bind(gl);
		gl.glLoadIdentity();
		gl.glBegin( GL2.GL_QUADS );
			double originX = -1.0;//-(windowX/2);
			double originY = -1.0;//-(windowY/2);
			
			double x = 1.0;//windowX;
			double y = 1.0;//windowY;
			gl.glTexCoord2d(0.0,0.0); gl.glVertex2d(originX,originY);
			gl.glTexCoord2d(1.0,0.0); gl.glVertex2d(x,originY);
			gl.glTexCoord2d(1.0,1.0); gl.glVertex2d(x,y);
			gl.glTexCoord2d(0.0,1.0); gl.glVertex2d(originX,y);
		gl.glEnd();
		
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
	
	private void drawLoadingBar(GL2 gl, LoadingBar loading){
		double leftX = Data.windowX/2 - Data.windowX*.05;
		double leftY = Data.windowY - 100;
		float percentage = ((float)loading.getProgress()/(float)loading.getTotal());
		float width = 100 * percentage;
		int maxWidth = 100;
		int height = 25;
		gl.glPushMatrix();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, Data.windowX, Data.windowY, 0, -10, 10);
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f(1, 1, 0);
			gl.glVertex2d(leftX, leftY);
			gl.glVertex2d(leftX, leftY+height);
			gl.glVertex2d(leftX+maxWidth, leftY+height);
			gl.glVertex2d(leftX+maxWidth, leftY);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f(1, 1, 1);
			gl.glVertex2d(leftX, leftY);
			gl.glVertex2d(leftX, leftY+height);
			gl.glVertex2d(leftX+width, leftY+height);
			gl.glVertex2d(leftX+width, leftY);
		gl.glEnd();

		gl.glPopMatrix();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		Data.windowX= width;
		Data.windowY= height;
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
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		
	}
	
	private void setupLighting(GL2 gl){
		float[] lightPos = { 2000,3000,2000,1 };        // light position
		float[] ambiance = { 0.4f, 0.4f, 0.4f, 1f };     // low ambient light
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
	
	public DialogRenderer getDialogRenderer() {
		return dialogRenderer;
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
