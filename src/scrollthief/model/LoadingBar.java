package scrollthief.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadingBar {
	
	private int total;
	private GameModel model;
	private String type;
	private int progress;
	private Map<String,ArrayList<String>> loadingPhrases;
	
	public LoadingBar(int total, GameModel model, String type, Map<String,ArrayList<String>> loadingPhrases){
		this.total = total;
		this.model = model;
		this.type = type;
		progress = 0;
		this.loadingPhrases = loadingPhrases;
	}
	
	public synchronized void increaseProgress(int up){
		progress += up;
		if(progress >= total){
			model.finishedLoading(type);
		}
	}
	
	public String getLoadingText() {
		int location = 0;
		double progressPercentage = (double)progress/(double)total;
		progressPercentage *= 100;
		if(progressPercentage > 40 && progressPercentage < 80){
			location = 1;
		}
		else if(progressPercentage > 80){
			location = 2;
		}
		return loadingPhrases.get(this.type).get(location);
	}
	
	public int getProgress() {
		return progress;
	}
	
	public int getTotal() {
		return total;
	}

}
