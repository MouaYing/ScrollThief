package scrollthief.model;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.*;

public class MusicListener extends PlaybackListener {
	
	private Sound sound;
	public MusicListener(Sound sound) {
		this.sound = sound;
	}
	
	@Override
	public void playbackFinished(PlaybackEvent arg0) {
		sound.setShouldRepeat(true);
	}

	@Override
	public void playbackStarted(PlaybackEvent arg0) {
		// TODO Auto-generated method stub
		super.playbackStarted(arg0);
	}

}
