package scrollthief.model;

import java.io.IOException;
import java.net.MalformedURLException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.*;

public class MusicPlayer extends Thread {
	AdvancedPlayer player;
	private String file;
	private long delay;
	private boolean restart;
	private boolean interruptable;
	private RepeatType repeatType;
	
	public MusicPlayer(String file, RepeatType repeatType) {
		this.file = file;
		delay = 0;
		restart = true;
		this.repeatType = repeatType;
		this.interruptable = interruptable;
	}

	public RepeatType getRepeatType() {
		return repeatType;
	}

	public boolean shouldRestart() {
		return restart;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public void run() {
		try {
		    Thread.sleep(delay);
			play();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void play() {
		try {
			player = new AdvancedPlayer
					(
							this.getClass().getResourceAsStream(file),
							javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice()
					);
			player.setPlayBackListener(new MusicListener(this));
			player.play();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void interrupt() {
		restart = false;
		if (repeatType == RepeatType.REPEAT)
			player.stop();
	}
}
