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
			
		Thread thread = new Thread()
		{
			public void run()
			{
				try {
					player = new AdvancedPlayer
							(
									new java.net.URL(file).openStream(),
									javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice()
							);
					player.play();
				} catch (JavaLayerException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		};
		
		lastThread = thread;
		thread.start();
	}
}