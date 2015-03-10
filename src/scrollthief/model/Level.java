package scrollthief.model;

import java.util.ArrayList;
import java.util.List;

public interface Level {
	public void createModels();
	public void createCharacters();
	public void createObstacles();
	public Guard[] getGuards();
	public Obstacle[] getObstacles();
	public Ninja getNinja();
	public Boss getBoss();
	public ArrayList<Projectile> getProjectiles();
	public Obstacle getScroll();
	public ArrayList<Model> getModels();
	public void setDialogHotspots();
	public List<DialogHotspot> getDialogHotspots();
	public DialogHotspot getCurrentDialogHotspot();
	public void setCurrentDialogHotspot(DialogHotspot curr);
	public LoadingBar getLoadingBar();
	public void reset();
}
