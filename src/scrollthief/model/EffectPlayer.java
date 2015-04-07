package scrollthief.model;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class EffectPlayer {
	AudioInputStream audioStream;
	private StreamSwitcher streamManager;
	private Clip clip;
	
	public EffectPlayer(String file) {
		streamManager = new StreamSwitcher(file);
		loadPlayer();
	}
	
	public void play() {
		Thread thread = new Thread()
		{
			public void run()
			{
				clip.setFramePosition(0);
			    clip.start();
			}
		};
		thread.start();
	}

	public void loadPlayer() {
		try {
			audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(streamManager.getStream()));
			clip = AudioSystem.getClip();
			clip.open(audioStream);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}