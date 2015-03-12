package scrollthief.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Level1 implements Level {
	int numGuards;
	int numWalls;
	int numPillars;
	int numTables;
	int numObs;
	Resource resource;
	int numModels;
	Guard[] guards;
	Obstacle[] obstacles;
	ArrayList<Model> models;
	ArrayList<Projectile> projectiles;
	private LoadingBar loadingBar;
	Ninja ninja;
	Boss boss;
	Obstacle scroll;
	GameModel gameModel;
	List<DialogHotspot> dialogHotspots;
	DialogHotspot currentDialogHotspot;
	List<Point3D> bossPouncePoints;

	public Level1(Resource resource, GameModel gameModel, Map<String,ArrayList<String>> phrases) {
		numGuards = 5;
		numWalls = 34;
		numPillars = 8;
		numTables = 5;
		numObs = numWalls + numPillars + numTables + 1;
		this.resource = resource;
		this.gameModel = gameModel;
		
		numModels= numGuards + numObs + 3; //56
		models= new ArrayList<Model>();
		guards= new Guard[numGuards];
		obstacles= new Obstacle[numObs];
		projectiles= new ArrayList<Projectile>();
		setDialogHotspots();
		setBossPouncePoints();
		loadingBar = new LoadingBar(numModels,gameModel, "level", phrases);
	}
	
	@Override
	public void createModels() {
		say("\nCreating 3D models...");
		
// ---------------Environment model/s ------------------------------------------------------------------
		models.add( new Model(resource.getOBJs()[0], 1, new Point3D(14, 0, 36), gameModel.zero(), .5, 1) ); // floor model (0)
// ---------------Character models ---------------------------------------------------------------------
		models.add( new Model(resource.getOBJs()[1], 2, new Point3D(0, 0, -5), gameModel.zero(), .075, 1) ); // ninja model 1
		
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(15, 0, 23), gameModel.zero(), .11, 1) ); // guard model 2
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(24, 0, 33), gameModel.zero(), .11, 1) ); // guard model 3
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(32, 0, 39), gameModel.zero(), .11, 1) ); // guard model 4
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(20, 0, 54), gameModel.zero(), .11, 1) ); // guard model 5
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(11, 0, 50), gameModel.zero(), .11, 1) ); // guard model 6
// ---------------Obstacle models ----------------------------------------------------------------------
		models.add( new Model(resource.getOBJs()[3], 9, new Point3D(0, 1.2, 83), new double[]{Math.PI/2,0,0}, .25, 1)); //scroll
		// foyer
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(-3.6, 0, 4.4), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(7.8, 0, -10.7), gameModel.zero(), 1, 1)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(-12, 0, -3.1), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(12.4, 0, -3.3), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(-4.2, 0, -10.7), gameModel.zero(), 1, 2)); // wall
		// room 1
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(4, 0, 9), gameModel.rtAngle(), 1, 1)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(20.8, 0, 4.2), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(20.8, 0, 29.2), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(29.2, 0, 12.6), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(29.2, 0, 24.6), gameModel.rtAngle(), 1, 1)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(4, 0, 21), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 7, new Point3D(11, 0, 29.2), gameModel.zero(), 1, .5)); // wall
		// room 2 (hard)
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(4, 0, 37), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(12.2, 0, 45.4), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(28.2, 0, 45.4), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 7, new Point3D(38.2, 0, 45.4), gameModel.zero(), 1, .5)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(36.8, 0, 29.2), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(45.2, 0, 37.6), gameModel.rtAngle(), 1, 2)); // wall
		// room 3 (hard)
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(45.2, 0, 53.6), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(36.8, 0, 62), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(20.8, 0, 62), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 7, new Point3D(11, 0, 62), gameModel.zero(), 1, .5)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(4, 0, 53), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(26.6, 0, 61), gameModel.rtAngle(), 1, 1)); // wall
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(26.6, 0, 49), gameModel.rtAngle(), 1, 1)); // wall
		// Boss Chamber
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(20.2, 0, 70.4), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(-11.6, 0, 61.6), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(.4, 0, 61.6), gameModel.zero(), 1, 1)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(-20, 0, 70), gameModel.rtAngle(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(-20, 0, 82), gameModel.rtAngle(), 1, 1)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(-12.4, 0, 86.6), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(3.6, 0, 86.6), gameModel.zero(), 1, 2)); // wall
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(15.6, 0, 86.6), gameModel.zero(), 1, 1)); // wall
		models.add( new Model(resource.getOBJs()[5], 5, new Point3D(20.2, 0, 82.4), gameModel.rtAngle(), 1, 1)); // wall
		// --- Pillars --- room 1
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(10, 0, 11), gameModel.zero(), 1, 1)); // pillar
		// room 2 (hard)
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(14, 0, 34), gameModel.zero(), 1, 1)); // pillar
		// room 3 (hard)
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(38, 0, 51), gameModel.zero(), 1, 1)); // pillar
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(20, 0, 51), gameModel.zero(), 1, 1)); // pillar
		// Boss Chamber
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(5, 0, 67), gameModel.zero(), 1, 1)); // pillar
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(11, 0, 76), gameModel.zero(), 1, 1)); // pillar
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(-7, 0, 69), gameModel.zero(), 1, 1)); // pillar
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(-13, 0, 77), gameModel.zero(), 1, 1)); // pillar
		// --- Tables --- room 2 (hard)
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(20, 0, 41.8), gameModel.rtAngle(), .4, 1)); // table
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(28.5, 0, 41.8), gameModel.rtAngle(), .4, 1)); // table
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(37, 0, 41.8), gameModel.rtAngle(), .4, 1)); // table
		// room 3 (hard)
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(15.5, 0, 58.4), gameModel.rtAngle(), .4, 1)); // table
		// Boss Chamber
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(0, 0, 83), gameModel.zero(), .4, 1)); // table
		// --- Ceiling ---
		models.add( new Model(resource.getOBJs()[0], 0, new Point3D(0, 5, 0), new double[] {Math.PI,0,0}, 1, 1) ); // ceiling model
		// --- Boss model ---
		models.add( new Model(resource.getOBJs()[7], 10, new Point3D(0, 0, 76), gameModel.zero(), .2, 1)); // Boss
		loadingBar.increaseProgress(1);
	}
	
	private Point3D[][] createGuardOrders() {
		Point3D[][] orders= new Point3D[][]{
/*guard 0*/	new Point3D[]{new Point3D(6,0,26),new Point3D(16,0,26)}, 
/*guard 1*/	new Point3D[]{new Point3D(24,0,31),new Point3D(24,0,41)},
/*guard 2*/	new Point3D[]{new Point3D(32,0,31),new Point3D(32,0,41)},
/*guard 3*/	new Point3D[]{new Point3D(37,0,54),new Point3D(14,0,54)},
/*guard 4*/	new Point3D[]{new Point3D(11,0,60),new Point3D(11,0,47)},
		};
		
		return orders;
	}
	
	@Override
	public void createCharacters() {
		say("Creating characters...");
		ninja= new Ninja(gameModel, models.get(1), .35, .5);
		loadingBar.increaseProgress(1);
		
		Point3D[][] orders= createGuardOrders();
		for (int i= 0; i < numGuards; i++){
			guards[i]= new Guard(gameModel, models.get(i + 2), .35, .5, orders[i]); 
			loadingBar.increaseProgress(1);
		}
		boss= new Boss(gameModel, models.get(models.size()-1), 1.5, 1.5);
		loadingBar.increaseProgress(1);
	}
	
	@Override
	public void createObstacles() {
		say("Creating obstacles...");
		// scroll
		obstacles[0]= new Obstacle(models.get(7), true, .2, .2, 1.3); 
		scroll= obstacles[0];
		loadingBar.increaseProgress(1);
		// walls
		for (int i= 1; i < numWalls+1; i++){
			obstacles[i]= new Obstacle(models.get(i + 7), false, .6, 4, 5); 
			loadingBar.increaseProgress(1);
		}
		// pillars
		for (int i= numWalls+1; i < numPillars+numWalls+1; i++){
			obstacles[i]= new Obstacle(models.get(i + 7), false, 1.5, 1.5, 5); 
			loadingBar.increaseProgress(1);
		}
		// tables
		for (int i= numWalls+numPillars+1; i < numObs; i++){
			obstacles[i]= new Obstacle(models.get(i + 7), true, 2.25, 3, .95); 
			loadingBar.increaseProgress(1);
		}
	}
	
	public Guard[] getGuards(){
		return guards;
	}
	
	public Obstacle[] getObstacles(){
		return obstacles;
	}
	
	public Ninja getNinja(){
		return ninja;
	}
	
	public Boss getBoss(){
		return boss;
	}
	
	public ArrayList<Projectile> getProjectiles(){
		return projectiles;
	}
	
	public Obstacle getScroll(){
		return scroll;
	}
	
	public ArrayList<Model> getModels(){
		return models;
	}
	
	public DialogHotspot getCurrentDialogHotspot() {
		return currentDialogHotspot;
	}
	
	public void setCurrentDialogHotspot(DialogHotspot curr) {
		currentDialogHotspot = curr;
	}
	
	public void setDialogHotspots() {
		dialogHotspots = new ArrayList<DialogHotspot>();
		String startDialog = "Sensei: Young one, be careful, there are many guards patrolling the area. Avoid them and obtain the scroll!";
		dialogHotspots.add(new DialogHotspot(0, 0, startDialog, 10, true));
	}
	
	public List<DialogHotspot> getDialogHotspots() {
		return dialogHotspots;
	}
	
	public void setBossPouncePoints() {
		bossPouncePoints = new ArrayList<Point3D>();
		
		bossPouncePoints.add(new Point3D(6.4, 0, 76)); //by table with scroll (left)
		bossPouncePoints.add(new Point3D(-4.4, 0, 77)); //by table with scroll (right)
		bossPouncePoints.add(new Point3D(-1, 0, 70)); //between entrance and table to the side
		bossPouncePoints.add(new Point3D(10.5, 0, 70)); //by door
	}
	
	public List<Point3D> getBossPouncePoints() {
		return bossPouncePoints;
	}
	
	public LoadingBar getLoadingBar() {
		return loadingBar;
	}
	
	private void say(String message){
		System.out.println(message);
	}
	
}
