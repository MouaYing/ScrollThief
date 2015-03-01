package scrollthief.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

/**
 * @author Taft Sandbakken
 *
 * This class handles texture and animation importing using multithreads
 */
public class Resource {
	boolean debug = false;
	
	//Arrays of frames for each type of animation
	private OBJ[] objs;
	private OBJ[] ninjaRun;
	private OBJ[] guardWalk;
	private OBJ[] bossStomp;
	private Texture[] textures;
	private LoadingBar loadingBar;
	private Texture[] images;
	private Sound sound;
	private Thread musicThread;
	private Random rand;
	private int mainSplashIndex;
	private final int total = 5;
	private final int OBJS_NUM = 9;
	private final int NINJA_RUN_NUM = 21;
	private final int GUARD_WALK_NUM = 30;
	private final int BOSS_STOMP_NUM = 24;
	private final int TEXTURES_NUM = 12;
	private final int IMAGES_NUM = 2;
	private final int SOUNDS_NUM = 4;
	
	public Resource(GameModel gameModel) {
		images = new Texture[IMAGES_NUM];
		rand = new Random();
		sound = new Sound();
		mainSplashIndex = rand.nextInt(IMAGES_NUM);
		objs = new OBJ[OBJS_NUM];
		ninjaRun = new OBJ[NINJA_RUN_NUM];
		guardWalk = new OBJ[GUARD_WALK_NUM];
		bossStomp = new OBJ[BOSS_STOMP_NUM];
		textures = new Texture[TEXTURES_NUM];
		int total = SOUNDS_NUM + OBJS_NUM + NINJA_RUN_NUM + GUARD_WALK_NUM + BOSS_STOMP_NUM + TEXTURES_NUM;
		loadingBar = new LoadingBar(total,gameModel,"resource");
		
	}
	
	public void reloadMusic() {
		sound.loadMusic(SoundFile.TITLE, "music/ST_Title_1.mp3", RepeatType.REPEAT);
		sound.loadMusic(SoundFile.SNEAK, "music/ST_Sneak.mp3", RepeatType.REPEAT);
		sound.loadMusic(SoundFile.GAMEOVER, "music/Game_Over.mp3", RepeatType.NOREPEAT);
		sound.loadMusic(SoundFile.BOSS, "music/ST_Boss_1.mp3", RepeatType.REPEAT);
	}
	
	private void loadSounds() {
		say("Loading music...");
		sound.loadMusic(SoundFile.TITLE, "music/ST_Title_1.mp3", RepeatType.REPEAT);
		loadingBar.increaseProgress(1);
		sound.playMusic(SoundFile.TITLE);
		sound.loadMusic(SoundFile.SNEAK, "music/ST_Sneak.mp3", RepeatType.REPEAT);
		loadingBar.increaseProgress(1);
		sound.loadMusic(SoundFile.GAMEOVER, "music/Game_Over.mp3", RepeatType.NOREPEAT);
		loadingBar.increaseProgress(1);
		sound.loadMusic(SoundFile.BOSS, "music/ST_Boss_1.mp3", RepeatType.REPEAT);
		loadingBar.increaseProgress(1);
	}
	
	// Loads the default OBJ files
	private void loadOBJs(){
		say("Loading OBJ files...");
		objs[0]= new OBJ("obj/floor.obj");
		loadingBar.increaseProgress(1);
		objs[1]= new OBJ("obj/ninja_stand.obj");
		loadingBar.increaseProgress(1);
		objs[2]= new OBJ("obj/guard_stand.obj");
		loadingBar.increaseProgress(1);
		objs[3]= new OBJ("obj/Scroll.obj");
		loadingBar.increaseProgress(1);
		objs[4]= new OBJ("obj/table.obj");
		loadingBar.increaseProgress(1);
		objs[5]= new OBJ("obj/wall2.obj");
		loadingBar.increaseProgress(1);
		objs[6]= new OBJ("obj/pillar2.obj");
		loadingBar.increaseProgress(1);
		objs[7]= new OBJ("obj/boss_stand.obj");
		loadingBar.increaseProgress(1);
		objs[8]= new OBJ("obj/AcidBlob.obj");
		loadingBar.increaseProgress(1);
	}
	
	// Loads the OBJ files for every frame of animation
	private void loadAnimations() {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		threads.add(new Thread() {
			public void run() {
				loadNinjaAnimations();
			}
		});
		threads.get(threads.size() - 1).start();
		
		threads.add(new Thread() {
			public void run() {
				loadGuardAnimations();
			}
		});
		threads.get(threads.size() - 1).start();
		
		threads.add(new Thread() {
			public void run() {
				loadBossAnimations();
			}
		});
		threads.get(threads.size() - 1).start();
	
		/*
		 * To add a new animation, copy the above lines to add a new thread and start it,  
		 * calling the method where you add the animation frames.
		 */
		
		try {
			say("thread count: " + threads.size());
			for(int i = 0; i < threads.size(); i++) {
				threads.get(i).join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void loadNinjaAnimations() {
		// Ninja run cycle
		say("\nLoading Ninja animation frames...");
		for (int i= 0; i < NINJA_RUN_NUM; i++){
			say("Loading run cycle frame " + (i+1));
			String fileName= "obj/anim/ninja/run." + (i+1) + ".obj";
			ninjaRun[i]= new OBJ(fileName);
			loadingBar.increaseProgress(1);
		}
		say("done with ninja ");
	}
	
	private void loadGuardAnimations() {
		// Guard walk cycle
		say("\nLoading Guard animation frames...");
		for (int i= 0; i < GUARD_WALK_NUM; i++){
			say("Loading walk cycle frame " + (i+1));
			String fileName= "obj/anim/guard/walk." + (i+1) + ".obj";
			guardWalk[i]= new OBJ(fileName);
			loadingBar.increaseProgress(1);
		}
		say("done with guard ");
	}
	
	private void loadBossAnimations() {
		// Boss stomp cycle
		say("\nLoading Boss animation frames...");
		for (int i= 0; i < BOSS_STOMP_NUM; i++){
			say("Loading stomp cycle frame " + (i+1));
			String fileName= "obj/anim/boss/stomp." + (i+1) + ".obj";
			bossStomp[i]= new OBJ(fileName);
			loadingBar.increaseProgress(1);
		}
		say("done with boss ");
	}
	
	public void loadTextures(GL2 gl){
		say("\nLoading texture files...");
		GLProfile profile= gl.getGLProfile();
		String[] imgPaths= new String[12];
		
		imgPaths[0]= "textures/wood.jpg";
		imgPaths[1]= "textures/floor.jpg";
		imgPaths[2]= "textures/ninja.bmp";
		imgPaths[3]= "textures/pillar.jpg";
		imgPaths[4]= "textures/table.jpg";
		imgPaths[5]= "textures/wall.jpg";
		imgPaths[6]= "textures/longwall.jpg";
		imgPaths[7]= "textures/shortwall.jpg";
		imgPaths[8]= "textures/guard.jpg";
		imgPaths[9]= "textures/scroll.jpg";
		imgPaths[10]= "textures/alienSkin.jpg";
		imgPaths[11]= "textures/goo.jpg";
		
		for (int i= 0; i < imgPaths.length; i++){
			try {
				BufferedImage image= ImageIO.read(new File(imgPaths[i]));
				ImageUtil.flipImageVertically(image);
				
				textures[i]= AWTTextureIO.newTexture(profile, image, false);
				loadingBar.increaseProgress(1);
				 
			} catch (IOException e) {
				say("Problem loading texture file " + imgPaths[i]);
				e.printStackTrace();
			}
		}
	}
	
	public void loadImages(GL2 gl) {
		GLProfile profile= gl.getGLProfile();
		String[] imgPaths= new String[2];
		imgPaths[0]= "images/ninja_moon.jpg";
		imgPaths[1]= "images/ninja_slice.jpg";
		for(int i = 0; i < imgPaths.length; i++){
			try{
			BufferedImage image= ImageIO.read(new File(imgPaths[i]));
			ImageUtil.flipImageVertically(image);
			images[i] = AWTTextureIO.newTexture(profile, image, false);
			}catch(IOException e){
				System.out.println("Error Loading Splash Images");
			}
		}
	}
	
	public Sound getSound() {
		return sound;
	}
	
	public OBJ[] getOBJs(){
		return objs;
	}
	
	public OBJ[] getBossStomp(){
		return bossStomp;
	}
	
	public OBJ[] getGuardWalk(){
		return guardWalk;
	}
	
	public OBJ[] getNinjaRun(){
		return ninjaRun;
	}
	
	public Texture[] getTextures(){
		return textures;
	}
	
	public LoadingBar getLoadingBar() {
		return loadingBar;
	}
	
	public Texture getSplashImage() {
		return images[mainSplashIndex];
	}
	
	public Thread getMusicThread() {
		return musicThread;
	}
	
	public void init(){
		loadSounds();
		loadOBJs();
		
		long startTime = System.nanoTime();
		loadAnimations();
		long total = (System.nanoTime() - startTime) / 1000000000;
		
		//loadTextures(gl);
		
		say("time to load Animations: " + total);
	}
	
	private void say(String message){
		if(debug) {
			System.out.println(message);
		}
	}
}
