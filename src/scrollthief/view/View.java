package scrollthief.view;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;

import scrollthief.model.Point3D;
import scrollthief.model.Model;
import scrollthief.model.GameModel;

public class View extends GLCanvas implements GLEventListener{
	private static final long serialVersionUID = 1L;
	GameModel gameModel;
	GL2 gl;
	boolean init= true;
	int windowSize= 710;
	float FOV= 45f;
	float ASPECT = 1f;
	float NEAR= .1f;
	float FAR= 3000f;
	public boolean resetting= false;
	float[] lookFrom= {2,4,5};
	float[] lookAt= {0,2,0};
	double cameraDistance= 10;
	double cameraAngle= 0;
	double cameraRotRate= 0;
	double[] cameraDelta= {0,0};

	public View(GameModel model){
		this.gameModel= model;
		setPreferredSize(new java.awt.Dimension(windowSize, windowSize));
        addGLEventListener(this);
		say("--View loaded--");
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl= drawable.getGL().getGL2();
		gameModel.loadTextures(gl);
		gameModel.createModels();
		
		setupLighting(gl);
		
		gl.glShadeModel(GL2.GL_SMOOTH);							// Enable Smooth Shading
		gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);				// grey Background
		gl.glClearDepth(1.0f);									// Depth Buffer Setup
		gl.glEnable(GL2.GL_DEPTH_TEST);							// Enables Depth Testing
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glShadeModel(GL2.GL_SMOOTH);
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
			init= false;
			return;
		}
		GL2 gl = drawable.getGL().getGL2();
		Model[] models= gameModel.getModels();
		Texture[] textures= gameModel.getTextures();
		
		moveCamera();
		moveNinja();
		
		// apply world-to-camera transform
		world2camera(gl);
				
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glColor3f(1f, 1f, 1f);
		
		// draw each model with its texture
		for (int i= 0; i < models.length; i++){
			gl.glPushMatrix();
			Model model= models[i];
			
			// assign texture
			textures[model.getTxtr()].bind(gl);
			
			//apply object to window transform
			obj2world(gl, model);
			
			model.getObj().DrawModel(gl);
			
			gl.glPopMatrix();
			gl.glFlush();
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
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
		double scale= model.getScale();
		Point3D loc= model.getLoc();
		
//		double[] scaleMatrix= {scale,0,0,0,
//				0,scale,0,0,
//				0,0,scale,0,
//				0,0,0,1};
//		
//		double[] rotAndTrans= {Math.cos(angle),0,-Math.sin(angle),0,
//				0,1,0,0,
//				Math.sin(angle),0, Math.cos(angle),0,
//				loc.x,loc.y,loc.z,1};
//		
//		double[][] scale2d= convTo2D(scaleMatrix);
//		double[][] rotAndTrans2d= convTo2D(rotAndTrans);
//		
//		double[][] objToWindow2d= matrixMult(rotAndTrans2d, scale2d);
//		double[] objToWindow1d= convTo1D(objToWindow2d);
//		
//		gl.glMultMatrixd(objToWindow1d, 0);
	
		gl.glTranslated(loc.x, loc.y, loc.z);
		gl.glRotated(angle, 0, -1, 0);
		gl.glScaled(scale, scale, scale);
		
	}
	
	private void world2camera(GL2 gl){
		double dZ= 0;
		double dX= 0;
		double ninjaAngle = gameModel.getNinjaAngle();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		lookAt= new float[]{(float) ninjaLoc.x, (float) ninjaLoc.y + 2, (float) ninjaLoc.z};
		
		if (resetting){ // center the camera behind the ninja
//			dZ= Math.cos(ninjaAngle + Math.PI + cameraAngle); 
//			dX= Math.sin(ninjaAngle + Math.PI + cameraAngle);
			cameraAngle= ninjaAngle;
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
	
	// having this method in the View instead of Controller breaks MVC, but this computes faster. Might
	// move this later
	public void moveNinja(){
		float scale= .25f;
		double direction= gameModel.getNinjaAngle() + Math.PI;
		double speed= gameModel.getNinjaSpeed();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		
		double deltaX= Math.sin(direction) * speed * scale;
		double deltaZ= -Math.cos(direction) * speed * scale;
		
		Point3D newLoc= new Point3D(ninjaLoc.x + deltaX, ninjaLoc.y, ninjaLoc.z + deltaZ);
		
		gameModel.setNinjaLoc(newLoc);
	}
	
	public void moveCamera(){
		double scale= .25;
		double dX= cameraDelta[0];
		double dY= cameraDelta[1];
		
		cameraAngle+= dX;
		lookFrom[1]+= dY;
		cameraDistance+= dY * scale;
	}
	
//	private double[][] convTo2D(double[] array){
//		int N= 4;
//		double[][] matrix= new double [N][N];
//		
//		for (int i = 0; i < (N * N); i++){
//			matrix[i%N][i/N]= array[i];
//		}
//		return matrix;
//	}
//	
//	private double[] convTo1D(double[][] matrix){
//		int N= 4;
//		double[] array= new double [N*N];
//		
//		for (int i = 0; i < (N * N); i++){
//			array[i]= matrix[i%N][i/N];
//		}
//		return array;
//	}
//	
//	// multiply 2 4x4 matrices together. result is AB
//	public double[][] matrixMult(double[][] A, double[][] B){
//		int N=4;
//		double[][] C= new double[N][N];
//		for (int i = 0; i < N; i++)
//			for (int j = 0; j < N; j++)
//				for (int k = 0; k < N; k++)
//				     C[i][j] += A[i][k] * B[k][j];
//			
//		return C;
//	}
//	
//	// multiply two vectors together (as Point3Ds)
//	public Point3D multVectors(Point3D a, Point3D b){
//		Point3D product= new Point3D(0, 0, 0);
//		
//		product.x = a.x * b.x;
//		product.y = a.y * b.y;
//		product.z = a.z * b.z;
//		
//		return product;
//	}
//	
//	// scale a vector (as a Point3D) by a double
//	public Point3D multVectors(double a, Point3D b){
//		Point3D product= new Point3D(0, 0, 0);
//			
//		product.x = a * b.x;
//		product.y = a * b.y;
//		product.z = a * b.z;
//			
//		return product;
//	}
//	
//	//add two vectors together (as Point3Ds)
//	public Point3D addVectors(Point3D a, Point3D b){
//		Point3D sum= new Point3D(0, 0, 0);
//		
//		sum.x= a.x + b.x;
//		sum.y= a.y + b.y;
//		sum.z= a.z + b.z;
//		
//		return sum;
//	}
//	
//	//add two vectors together (as Point3Ds)
//	public Point3D subtractVectors(Point3D a, Point3D b){
//		Point3D diff= new Point3D(0, 0, 0);
//			
//		diff.x= a.x - b.x;
//		diff.y= a.y - b.y;
//		diff.z= a.z - b.z;
//			
//		return diff;
//	}
	
	public double getCamAngle(){
		return cameraAngle;
	}
	
	public void setCamHeight(float height){
		lookFrom[1]= height;
	}
	
	public void setCamRotRate(double rate){
		cameraRotRate= rate;
	}
	
	public void setCamDelta(double[] delta){
		cameraDelta= delta;
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
