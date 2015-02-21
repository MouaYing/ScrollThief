package scrollthief.model;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.*;

import java.io.*;
import java.util.HashMap;

public class Sound {
	HashMap<SoundFile, AdvancedPlayer> players;
	SoundFile currentMusic;
	Thread currentMusicThread;
	boolean shouldRepeat;
	
	public boolean shouldRepeat() {
		return shouldRepeat;
	}

	public void setShouldRepeat(boolean shouldRepeat) {
		this.shouldRepeat = shouldRepeat;
	}

	public SoundFile getCurrentMusic() {
		return currentMusic;
	}

	public Thread getCurrentMusicThread() {
		return currentMusicThread;
	}

	public Sound() {
		players = new HashMap<SoundFile, AdvancedPlayer>();
		currentMusic = null;
	}
	
	public void playMusic(SoundFile soundFile) {
		if (currentMusic != null && currentMusicThread != null)
			currentMusicThread.interrupt();
		currentMusic = soundFile;
		Thread thread = new Thread() {
			public void run() {
				try {
					players.get(soundFile).play();
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			public void interrupt() {
				players.get(soundFile).stop();
			}
		};
		thread.start();
		currentMusicThread = thread;
	}
	
	public void repeatMusic() {
		Thread thread = new Thread() {
			public void run() {
				try {
					players.get(currentMusic).play();
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			public void interrupt() {
				players.get(currentMusic).stop();
			}
		};
		thread.start();
		currentMusicThread = thread;
	}
	
	public void stopMusic(SoundFile soundFile) {
		currentMusicThread.interrupt();
	}
	
	public void loadMusic(SoundFile sf, String path) {
		String urlAsString = "";
		try {
			urlAsString = 
	                "file:///" 
	                + new java.io.File(".").getCanonicalPath()         + "/" 
	                + path;
			players.put(sf, new AdvancedPlayer
			(
					new java.net.URL(urlAsString).openStream(),
					javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice()
			));
			players.get(sf).setPlayBackListener(new MusicListener(this));
		} catch (JavaLayerException | IOException e) {
			System.out.println(urlAsString);
			e.printStackTrace();
		}
	}
}
