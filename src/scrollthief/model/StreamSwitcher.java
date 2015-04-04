package scrollthief.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StreamSwitcher {
	private String file;
	private List<InputStream> streams;
	private int currentIndex;
	
	public StreamSwitcher(String file) {
		this.file = file;
		currentIndex = 0;
		streams = new ArrayList<InputStream>();
		streams.add(this.getClass().getResourceAsStream(file));
		streams.add(this.getClass().getResourceAsStream(file));
	}
	
	public void loadNextStream() {
		if (currentIndex == 0) {
			streams.set(1, this.getClass().getResourceAsStream(file));
			currentIndex = 1;
		}
		else {
			streams.set(0, this.getClass().getResourceAsStream(file));
			currentIndex = 0;
		}
	}
	
	public InputStream getStream() {
		return streams.get(currentIndex);
	}
}
