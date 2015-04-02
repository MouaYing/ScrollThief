package scrollthief.model;

import java.util.ArrayList;
import java.util.List;

import scrollthief.model.characters.Boss;
import scrollthief.model.characters.Guard;
import scrollthief.model.characters.Ninja;

public interface Level {
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
	public void initialize();
}
