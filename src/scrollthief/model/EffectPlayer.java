package scrollthief.model;

import java.io.IOException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class EffectPlayer {
	AdvancedPlayer player;
	private String file;
	private Thread lastThread;
	
	public EffectPlayer(String file) {
		this.file = file;
	}
	
	public void play() {
		if (lastThread != null)
			lastThread.interrupt();
		
		EffectPlayer thisPlayer = this;
			
		Thread thread = new Thread()
		{
			public void run()
			{
				try {
					player.setPlayBackListener(new EffectListener(thisPlayer));
					player.play();
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		};
		
		lastThread = thread;
		thread.start();
	}

	public void loadPlayer() {
		try {
			this.player = new AdvancedPlayer
						 (
						 	this.getClass().getResourceAsStream(file),
							javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice()
						 );
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}