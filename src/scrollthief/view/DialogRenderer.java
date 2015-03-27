package scrollthief.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import scrollthief.model.Data;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class DialogRenderer {
	GL2 gl;
//	Dialog dialog;
	TextRenderer tRend;
	TextRenderer tRend2;
	TextRenderer tRendLoadingBar;
	Texture dialogBackground;
	float startTime;
	float timer;
	boolean isNextScroll;  //for if the user wants to skip through dialog
	boolean finished;
	int timePerLetter = 30;  //in milliseconds
	int timePerScroll = 5000;  //in milliseconds
	int charactersPerLine = (int) (Data.windowX * 0.055);  //how many characters can fit onto one line of the dialog scroll
	int pixelsPerLine = 30;  //how far to lower text for each line of dialog
	boolean debug = false;
	static String DEFAULT_DIALOG = "There will be a financial oppotunity in your future. Favorite numbers: 2, 15, 29";
	
	public DialogRenderer(GL2 gl) {
		this.gl = gl;
		tRend = new TextRenderer(new Font("Helvetica", Font.BOLD, 20));
		tRend2 = new TextRenderer(new Font("Helvetica", Font.BOLD, 60));
		tRendLoadingBar= new TextRenderer(new Font("Helvetica", Font.PLAIN, 10));
		loadDialogBackgroundImage(gl);
		timer = 0;
		startTime = 0;
		isNextScroll = false;
		finished = false;
	}
	
	public void render(String text) {
		updateTimer();
		displayDialogBackgroundImage(dialogBackground);

		//add text to top of scroll
		ArrayList<String> lines = getLines(text);
		int x = (int) (Data.windowX * 0.21);
		int y = (int) (Data.windowY * 0.185);
		
		for(int i = 0; i < lines.size(); i++) {
		    overlayText(lines.get(i), x, y, Color.red, "small");
		    y -= pixelsPerLine; //lower the next lines on the scroll
		}
	}
	
	private ArrayList<String> getLines(String text) {
		ArrayList<String> result = new ArrayList<String>();
		List<String> words = Arrays.asList(text.split("\\s+"));
		ArrayList<String> lines = getFullLines(words);
		int endIndex = (int)(timer / timePerLetter);
		
		//end dialog by timer
		if(endIndex > (text.length() * 2)) {
			finished = true;
		}
		
		//show the whole text (and only the whole text)
		if(endIndex > text.length() || isNextScroll) {
			endIndex = text.length() + 1;
		}
		
		//using the timer, limit what is returned so that it looks like it is being written out
		int i = 0;		
		while(i < lines.size()) {
			if(lines.get(i).length() > endIndex) {
				result.add(lines.get(i).substring(0, endIndex));
				break;
			}
			else {
				String temp = lines.get(i);
				result.add(temp);
				endIndex -= temp.length();
			}
			i++;
		}		
		return result;
	}
	
	//break up the text into what each line will be when the message has written itself out
	public ArrayList<String> getFullLines(List<String> words) {
		ArrayList<String> lines = new ArrayList<String>();
		int i = 0;
		String temp = "";
		while(i < words.size()) {
			if((temp + words.get(i)).length() >= charactersPerLine || i == words.size() - 1) {
				if((temp + words.get(i)).length() < charactersPerLine && i == words.size() - 1) // to account for the last word of the text
					temp += words.get(i);
				lines.add(temp);
				if(debug)
					System.out.println(i + " " + temp);
				temp = words.get(i) + " ";
			}
			else {
				temp += words.get(i) + " ";
			}
			i++;
		}
		return lines;
	}
	
	public void overlayText(String text, int x, int y, Color color, String type){
		TextRenderer rend = tRend;
		if (type.equals("big"))
			rend = tRend2;
		else if(type == "loading")
			rend = tRendLoadingBar;
		else if(type == "pause")
			rend = new TextRenderer(new Font("Helvetica", Font.PLAIN, 15));
			
		rend.setColor(color);
		rend.beginRendering(Data.windowX, Data.windowY, true);
		rend.draw(text, x, y);
		rend.endRendering();
		rend.flush();
	}
	
	public void loadDialogBackgroundImage(GL2 gl) {
		GLProfile profile= gl.getGLProfile();
		try{
			BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream(Data.DIALOG_SCROLL_FILE));
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
			double originY = -0.95;
			double x = 0.8;
			double y = -0.5;
			
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
	
	public boolean isNextScroll() {
		return isNextScroll;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public void say(String text) {
		System.out.println(text);
	}
}
