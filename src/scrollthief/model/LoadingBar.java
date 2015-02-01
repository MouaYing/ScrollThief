package scrollthief.model;

public class LoadingBar {
	
	private int total;
	private GameModel model;
	private String type;
	private int progress;
	
	public LoadingBar(int total, GameModel model, String type){
		this.total = total;
		this.model = model;
		this.type = type;
		progress = 0;
	}
	
	public synchronized void increaseProgress(int up){
		progress += up;
		if(progress >= total){
			model.finishedLoading(type);
		}
	}
	
	public int getProgress() {
		return progress;
	}
	
	public int getTotal() {
		return total;
	}

}
