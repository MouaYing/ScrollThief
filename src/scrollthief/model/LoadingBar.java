package scrollthief.model;

public class LoadingBar {
	
	private int total;
	private GameModel model;
	
	public int progress;
	
	public LoadingBar(int total, GameModel model){
		this.total = total;
		this.model = model;
		progress = 0;
	}
	
	public synchronized void increaseProgress(int up){
		progress += up;
		if(progress == total){
			model.finishedLoading();
		}
	}

}
