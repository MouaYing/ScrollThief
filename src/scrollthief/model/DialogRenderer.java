package scrollthief.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	Dialog dialog;
	TextRenderer tRend;
	TextRenderer tRend2;
	Texture dialogBackground;
	float startTime;
	float timer;
	boolean isNextScroll;
	int timePerLetter = 50;  //in milliseconds
	int timePerScroll = 5000;  //in milliseconds
	int charactersPerLine = 25;  //how many characters can fit onto one line of the dialog scroll
	int pixelsPerLine = 30;  //how far to lower text for each line of dialog
	
	public DialogRenderer(GL2 gl) {
		this.gl = gl;
		tRend = new TextRenderer(new Font("Helvetica", Font.BOLD, 30));
		tRend2 = new TextRenderer(new Font("Helvetica", Font.BOLD, 60));
		loadDialogBackgroundImage(gl);
		timer = 0;
		startTime = 0;
		isNextScroll = false;
	}
	
	public void render(String text) {
		updateTimer();
		displayDialogBackgroundImage(dialogBackground);

		//change scroll if timer hits
//		boolean showNextLines = false;
//		if(timer > timePerScroll) {
//			timer = 0;
//			showNextLines = true;
//		}

		//add text to top of scroll
		ArrayList<String> lines = getLines(text);
		int x = (int) (Data.windowX * 0.21);
		int y = Data.windowY - 70;
		
		for(int i = 0; i < lines.size(); i++) {
		    overlayText(lines.get(i), x, y, Color.red, "small");
		    y -= pixelsPerLine; //lower the next lines on the scroll
		}
	}
	
	private ArrayList<String> getLines(String text) {
		ArrayList<String> result = new ArrayList<String>();
		List<String> words = Arrays.asList(text.split("\\s+"));
		int endIndex = text.length(); //(int)(timer / timePerLetter);
		int i = 0;
		String temp = "";
		
		while(i < words.size()) {
			if((temp + words.get(i)).length() >= charactersPerLine) {
				if(endIndex < temp.length())
					temp = temp.substring(0, endIndex);
				endIndex -= temp.length();
				result.add(temp);
				System.out.println(endIndex + " " + temp);
				temp = words.get(i) + " ";
				if(endIndex < -1)
					break;
			}
			else {
				temp += words.get(i) + " ";
			}
			i++;
		}
		
		return result;
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
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, Data.windowX, Data.windowY, 0, -10, 10);
		gl.glEnable( GL2.GL_TEXTURE_2D );
		t.bind(gl);
		gl.glLoadIdentity();
		gl.glBegin( GL2.GL_QUADS );
			//these values place where the image renders on the screen in Cartesian coordinates
			double originX = -0.8;
			double originY = 0.5;
			double x = 0.8;
			double y = 0.95;
			
			//these values define what part of the image renders on the screen from 0.0 to 1.0
			double coordOriginX = 0.0;
			double coordOriginY = 0.0;
			double coordX = 1.0;
			double coordY = 1.0;
			
			gl.glTexCoord2d(coordOriginX, coordOriginY); gl.glVertex2d(originX,originY);
			gl.glTexCoord2d(coordX, coordOriginY); gl.glVertex2d(x,originY);
			gl.glTexCoord2d(coordX, coordY); gl.glVertex2d(x,y);
			gl.glTexCoord2d(coordOriginX, coordY); gl.glVertex2d(originX,y);
		gl.glEnd();
//		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
	
	private void updateTimer() {
		if(startTime == 0)
			startTime = System.nanoTime();
		timer = (System.nanoTime() - startTime) / 1000000;
	}
	
	public void setIsNextScroll(boolean next) {
		isNextScroll = next;
	}
}
