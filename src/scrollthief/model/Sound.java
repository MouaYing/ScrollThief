package scrollthief.model;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.*;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;

public class Sound {
	AdvancedPlayer currentPlayer;
	HashMap<SoundFile, MusicPlayer> players;
	HashMap<SoundFile, EffectPlayer> effects;
	SoundFile currentMusic;
	MusicPlayer currentMusicThread;
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
		players = new HashMap<SoundFile, MusicPlayer>();
		effects = new HashMap<SoundFile, EffectPlayer>();
		currentMusic = null;
	}
	
	public void delayedPlayMusic(SoundFile soundFile, long delay) {
		if (currentMusic != null && currentMusicThread != null)
			currentMusicThread.interrupt();
		currentMusic = soundFile;
		MusicPlayer player = players.get(soundFile); 
		player.setDelay(delay);
		player.start();
		currentMusicThread = player;
	}
	
	public void playMusic(SoundFile soundFile) {
		if (currentMusic != null && currentMusicThread != null)
			currentMusicThread.interrupt();
		currentMusic = soundFile;
		MusicPlayer player = players.get(soundFile); 
		player.start();
		currentMusicThread = player;
	}
	
	public void playEffect(SoundFile soundFile) {
		EffectPlayer player = effects.get(soundFile);
		player.play();
	}
	
	public void stopMusic(SoundFile soundFile) {
		currentMusicThread.interrupt();
	}
	
	public void loadEffect(SoundFile sf, String path) {
		String urlAsString = "";
		try {
			urlAsString = 
			        "file:///" 
			        + new java.io.File(".").getCanonicalPath()         + "/" 
			        + path;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		effects.put(sf, new EffectPlayer(urlAsString));
	}
	
	public void loadMusic(SoundFile sf, String path, RepeatType repeatType) {
		String urlAsString = "";
		try {
			urlAsString = 
			        "file:///" 
			        + new java.io.File(".").getCanonicalPath()         + "/" 
			        + path;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		players.put(sf, new MusicPlayer(urlAsString, repeatType));
	}
}
