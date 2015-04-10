package scrollthief.model;

//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.advanced.*;

//import java.io.*;
//import java.net.MalformedURLException;
import java.util.HashMap;

public class Sound {
	//AdvancedPlayer currentPlayer;
	HashMap<SoundFile, MusicPlayer> players;
	HashMap<SoundFile, EffectPlayer> effects;
	SoundFile currentMusic;
	MusicPlayer currentMusicPlayer;
	boolean shouldRepeat;
	boolean playMusic;
	
	public boolean shouldRepeat() {
		return shouldRepeat;
	}

	public void setShouldRepeat(boolean shouldRepeat) {
		this.shouldRepeat = shouldRepeat;
	}

	public SoundFile getCurrentMusic() {
		return currentMusic;
	}

	public MusicPlayer getCurrentMusicPlayer() {
		return currentMusicPlayer;
	}

	public Sound() {
		players = new HashMap<SoundFile, MusicPlayer>();
		effects = new HashMap<SoundFile, EffectPlayer>();
		currentMusic = null;
		playMusic = true;
	}
	
	public void delayedPlayMusic(SoundFile soundFile, long delay) {
		if (playMusic) {
			if (currentMusic != null && currentMusicPlayer != null)
				currentMusicPlayer.stop();
			currentMusic = soundFile;
			MusicPlayer player = players.get(soundFile); 
			player.setDelay(delay);
			player.playFromBeginning();
			currentMusicPlayer = player;
		}
	}
	
	public void playMusic(SoundFile soundFile) {
		if (playMusic)	{
			if (currentMusic != null && currentMusicPlayer != null)
				currentMusicPlayer.stop();
			currentMusic = soundFile;
			MusicPlayer player = players.get(soundFile); 
			player.playFromBeginning();
			currentMusicPlayer = player;
		}
	}
	
	public void stopMusic() {
		if (currentMusicPlayer != null) {
			currentMusicPlayer.stop();
		}
		currentMusicPlayer = players.get(SoundFile.SNEAK);
	}
	
	public void setShouldPlayMusic(boolean value) {
		playMusic = value;
	}
	
	public void pauseMusic() {
		if (currentMusicPlayer != null) {
			currentMusicPlayer.stop();
		}
	}
	
	public void resumeMusic() {
		if (playMusic && currentMusicPlayer != null) {
			currentMusicPlayer.resumePlay();
		}
	}
	
	public void playEffect(SoundFile soundFile) {
		EffectPlayer player = effects.get(soundFile);
		player.play();
	}
	
	public void loadEffect(SoundFile sf, String path) {
		String urlAsString = "";
		urlAsString = path;
		EffectPlayer player = new EffectPlayer(urlAsString);
		effects.put(sf, player);
	}
	
	public void loadMusic(SoundFile sf, String path, RepeatType repeatType) {
		String urlAsString = "";
		urlAsString = path;
		players.put(sf, new MusicPlayer(urlAsString, repeatType));
	}
}
