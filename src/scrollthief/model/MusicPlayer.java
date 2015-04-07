package scrollthief.model;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MusicPlayer {
	AudioInputStream audioStream;
	private Thread lastThread;
	private StreamSwitcher streamManager;
	private Clip clip;
	private long delay;
	private RepeatType repeatType;
	private String fileName;
	
	public MusicPlayer(String file, RepeatType repeatType) {
		fileName = file;
		delay = 0;
		this.repeatType = repeatType;
		streamManager = new StreamSwitcher(file);
		loadPlayer();
	}

	public RepeatType getRepeatType() {
		return repeatType;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public void playFromBeginning() {
		Thread thread = new Thread() {
			
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				clip.setFramePosition(0);
				if (repeatType == RepeatType.NOREPEAT)
					clip.start();
				else
					clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
			
			public void interrupt() {
				clip.stop();
			}
		};
		
		lastThread = thread;
		thread.start();
	}
	
	public void resumePlay() {
		Thread thread = new Thread() {
			
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					say("Music thread interrupted!");
					e.printStackTrace();
				}
				if (repeatType == RepeatType.NOREPEAT)
					clip.start();
				else
					clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
			
			public void interrupt() {
				if (clip.isActive())
					clip.stop();
			}
		};
		
		lastThread = thread;
		thread.start();
	}
	
	public void stop() {
		lastThread.interrupt();
	}
	
	public void loadPlayer() {
		try {
			audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(streamManager.getStream()));
			clip = AudioSystem.getClip();
			clip.open(audioStream);
			streamManager.loadNextStream();
		} catch (UnsupportedAudioFileException e) {
			say("\nUnsupported file! File: " + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			say("\nIO error! File: " + fileName);
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			say("\nLine unavailable! File: " + fileName);
			e.printStackTrace();
		}
	}
	
	private void say(String message){
		System.out.println(message);
	}
}
