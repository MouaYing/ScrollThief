package scrollthief.model;
import java.io.IOException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.*;

public class MusicListener extends PlaybackListener {
	
	private MusicPlayer player;
	public MusicListener(MusicPlayer player) {
		this.player = player;
	}
	
	@Override
	public void playbackFinished(PlaybackEvent arg0) {
		if (player.getRepeatType() == RepeatType.REPEAT && player.shouldRestart())
			player.play();
	}

	@Override
	public void playbackStarted(PlaybackEvent arg0) {
		// TODO Auto-generated method stub
		super.playbackStarted(arg0);
	}
}
