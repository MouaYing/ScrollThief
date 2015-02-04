package scrollthief.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class DialogRenderer {
	GL2 gl;
	TextRenderer tRend;
	TextRenderer tRend2;
	Texture dialogBackground;
	float startTime;
	float timer;
	int timePerLetter = 200;  //in milliseconds
	
	public DialogRenderer(GL2 gl) {
		this.gl = gl;
		tRend = new TextRenderer(new Font("Helvetica", Font.BOLD, 30));
		tRend2 = new TextRenderer(new Font("Helvetica", Font.BOLD, 60));
		loadDialogBackgroundImage(gl);
		timer = 0;
		startTime = 0;
	}
	
	public void render(String text) {
		updateTimer();
		
//		//draw scroll
//		File imageFile = new File(Data.DIALOG_SCROLL_FILE);
//        Texture texture = null;
//        
//        gl.glEnable(GL2.GL_TEXTURE_2D);
//        gl.glMatrixMode(GL2.GL_TEXTURE);
//        gl.glLoadIdentity();        
//		try {
//			texture = TextureIO.newTexture(imageFile, true);
//	        texture.enable(gl);
//	        texture.bind(gl);
//	
////	        gl.glBegin(GL2.GL_POLYGON);
////	        gl.glNormal3f(0,0,1);
////	        gl.glTexCoord2d(-texture.getWidth(), -texture.getHeight());
////	        gl.glVertex2d(-25, -25);
////	        gl.glTexCoord2d(-texture.getWidth(), texture.getHeight());
////	        gl.glVertex2d(Data.windowX,0);
////	        gl.glTexCoord2d(texture.getWidth(), texture.getHeight());
////	        gl.glVertex2d(Data.windowX, Data.windowY);
////	        gl.glTexCoord2d(texture.getWidth(), -texture.getHeight());
////	        gl.glVertex2d(0, Data.windowY);
////	        gl.glFlush();
////	        gl.glEnd();
//	        
//	        GLUquadric earth = glu.gluNewQuadric();
//	        glu.gluQuadricTexture(earth, true);
//	        
//		} catch (GLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
		displayDialogBackgroundImage(dialogBackground);
		

		//add text to top of scroll
//		text = "1 2 3 4 5 6 7 8 9 0 \n 1 2 3 4 5 6 7 8 9 0 \n 1 2 3 4 5 6 7 8 9 0 \n 1 2 3 4 5 6 7 8 9 0 \n 1 2 3 4 5 6 7 8 9 0 ";
		text = "1 2 3 4 5 6 7 8 9 0";
		int endIndex = (int)(timer / timePerLetter);
		if(endIndex >= text.length())
			endIndex = text.length();
		String textToShow = text.substring(0, endIndex);
	    overlayText(textToShow + timer, Data.windowX/2 - (30 * text.length()/2), Data.windowY - 50, Color.blue, "small");
	}
	
	public void overlayText(String text, int x, int y, Color color, String type){
		TextRenderer rend = tRend;
		if (type.equals("big"))
			rend = tRend2;
			
		rend.setColor(color);
		rend.beginRendering(Data.windowX, Data.windowY, true);
		rend.draw(text, x, y);
		rend.endRendering();
		rend.flush();
	}
	
	public void loadDialogBackgroundImage(GL2 gl) {
		GLProfile profile= gl.getGLProfile();
		try{
			BufferedImage image = ImageIO.read(new File(Data.DIALOG_SCROLL_FILE));
			ImageUtil.flipImageVertically(image);
			dialogBackground = AWTTextureIO.newTexture(profile, image, false);
		}catch(IOException e){
			System.out.println("Error Loading Dialog Images");
		}
 	}
	
	private void displayDialogBackgroundImage(Texture t){
		gl.glPushMatrix();
		gl.glMatrixMode(GL2.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glOrtho(0, Data.windowX, Data.windowY, 0, -10, 10);
		gl.glEnable( GL2.GL_TEXTURE_2D );
		t.bind(gl);
		gl.glLoadIdentity();
		gl.glBegin( GL2.GL_QUADS );
			double originX = -0.8; //-1.0;//-(windowX/2);
			double originY = 0.7; //-1.0;//-(windowY/2);
			
			double x = 0.8; //1.0;//windowX;
			double y = 0.95; //1.0;//windowY;
			gl.glTexCoord2d(0.2,0.05); gl.glVertex2d(originX,originY);
			gl.glTexCoord2d(0.8,0.05); gl.glVertex2d(x,originY);
			gl.glTexCoord2d(0.8,0.2); gl.glVertex2d(x,y);
			gl.glTexCoord2d(0.2,0.2); gl.glVertex2d(originX,y);
		gl.glEnd();
//		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
	
	private void updateTimer() {
		if(startTime == 0)
			startTime = System.nanoTime();
		timer = (System.nanoTime() - startTime) / 1000000;
	}
}
