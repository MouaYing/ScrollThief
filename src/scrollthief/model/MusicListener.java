package scrollthief.model;
//import java.io.IOException;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.advanced.*;

public class MusicListener implements LineListener {
	
	private MusicPlayer player;
	public MusicListener(MusicPlayer player) {
		this.player = player;
	}

	@Override
	public void update(LineEvent arg0) {
	}
}
