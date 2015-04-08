package scrollthief.model.levels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import scrollthief.model.DialogHotspot;
import scrollthief.model.GameModel;
import scrollthief.model.LoadingBar;
import scrollthief.model.Model;
import scrollthief.model.Obstacle;
import scrollthief.model.Point3D;
import scrollthief.model.Projectile;
import scrollthief.model.Resource;
import scrollthief.model.characters.Boss;
import scrollthief.model.characters.Guard;
import scrollthief.model.characters.Ninja;

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
	Map<String,ArrayList<String>> phrases;
	List<DialogHotspot> dialogHotspots;
	DialogHotspot currentDialogHotspot;
	RoomCreator creator;
	public Level1(Resource resource, GameModel gameModel, Map<String,ArrayList<String>> phrases) {
		creator = new RoomCreator(resource,gameModel);
		numGuards = 12;
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
		this.phrases = phrases;
		setDialogHotspots();
		loadingBar = new LoadingBar(numModels,gameModel, "level", phrases);
	}
	
	@Override
	public void initialize() {
		createModels();
		createCharacters();
		createObstacles();
	}
	
	@Override
	public void reset() {
		models= new ArrayList<Model>();
		guards= new Guard[numGuards];
		obstacles= new Obstacle[numObs];
		projectiles= new ArrayList<Projectile>();
		loadingBar = new LoadingBar(numModels,gameModel, "level", phrases);
	}
	
	private void createModels() {
		say("\nCreating 3D models...");
		addEnvironmentModels();
		addCharacterModels();
// ---------------Obstacle models ----------------------------------------------------------------------
		models.add( new Model(resource.getOBJs()[3], 9, new Point3D(0, 1.2, 83), new double[]{Math.PI/2,0,0}, .25, 1)); //scroll
		addFoyer(models);
		addNewEasyRoom1(models);
		addNewEasyRoom2(models);
		addNewEasyRoom3(models);
		addNewEasyRoom4(models);
		addNewEasyRoom5(models);
		addNewEasyRoom6(models);
		addBossRoom(models);
		addMediumRoom1(models);
		addHardRoom1(models);
		addHardRoom2(models);
		addHardRoom3(models);
		addHardRoom4(models);
		addOldRooms(models);
		setArrayValues();
		loadingBar = new LoadingBar(numModels,gameModel, "level", phrases);
		loadingBar.increaseProgress(1);
		
	}
	
	private void setArrayValues() {
		numGuards = 0;
		numWalls = 0;
		numPillars = 0;
		numTables = 0;
		for(int i = 0; i < models.size(); i++){
			int type = models.get(i).getTxtr();
			if( type == 8){
				numGuards++;
			}
			else if(type == 3){
				numPillars++;
			}
			else if(type == 4){
				numTables++;
			}
			else if(type >= 5 && type <=7) {
				numWalls++;
			}
		}
		
		numObs = numWalls + numPillars + numTables + 1;
		
		numModels= numGuards + numObs + 3; //56
		guards= new Guard[numGuards];
		obstacles= new Obstacle[numObs];
	}
	
	private void addEnvironmentModels() {
		// ---------------Environment model/s ------------------------------------------------------------------
		models.add( new Model(resource.getOBJs()[0], 1, new Point3D(14, 0, 36), gameModel.zero(), .5, 1) ); // floor model (0)
		// --- Ceiling ---
		models.add( new Model(resource.getOBJs()[0], 0, new Point3D(0, 5, 0), new double[] {Math.PI,0,0}, 1, 1) ); // ceiling model
		
	}
	
	private void addCharacterModels(){

		// ---------------Character models ---------------------------------------------------------------------
		models.add( new Model(resource.getOBJs()[1], 2, new Point3D(0, 0, -5), gameModel.zero(), .075, 1) ); // ninja model 1
		// --- Boss model ---
		models.add( new Model(resource.getOBJs()[7], 10, new Point3D(0, 0, 76), gameModel.zero(), .2, 1)); // Boss

		
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(15, 0, 23), gameModel.zero(), .11, 1) ); // guard model 0
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(24, 0, 33), gameModel.zero(), .11, 1) ); // guard model 1
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(32, 0, 39), gameModel.zero(), .11, 1) ); // guard model 2
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(20, 0, 54), gameModel.zero(), .11, 1) ); // guard model 3
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(11, 0, 50), gameModel.zero(), .11, 1) ); // guard model 4
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(-14.3,0,11), gameModel.zero(), .11, 1) ); // guard model 5
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(-20.3,0,23.4), gameModel.zero(), .11, 1) ); // guard model 6
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(-4.2,0,31.9), gameModel.zero(), .11, 1) ); // guard model 7
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(-20.2,0,33.9), gameModel.zero(), .11, 1) ); // guard model 8
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(56,0, 49.5), gameModel.zero(), .11, 1) ); // guard model 9
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(56,0, 41.8), gameModel.zero(), .11, 1) ); // guard model 10
		models.add( new Model(resource.getOBJs()[2], 8, new Point3D(56,0, 57.2), gameModel.zero(), .11, 1) ); // guard model 11
	}
	
	private void addFoyer(List<Model> models){
		Room room = new Room(new Point3D(0,0,-3.5));
		double[] north = {-1,2};
		double[] south = {1,2};
		double[] east = {1, -1};
		double[] west = {2};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
	}
	
	private void addNewEasyRoom1(List<Model> models) {
		Room room = new Room(new Point3D(-20.3,0,4.5));
		double[] north = {1,-1};
		double[] south = {2};
		double[] east = {2};
		double[] west = {-1,1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createPillar(models, new Point3D(-17.3,0,1));
		creator.createPillar(models, new Point3D(-23.3,0,1));
		creator.createPillar(models, new Point3D(-17.3,0,8.7));
		creator.createPillar(models, new Point3D(-23.3,0,8.7));
	}
	
	private void addNewEasyRoom2(List<Model> models) {
		Room room = new Room(new Point3D(-20.3,0,20.4));
		double[] north = {2};
		double[] south = {-2};
		double[] east = {2};
		double[] west = {1, -1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createPillar(models, new Point3D(-20.3,0,18.5));
	}
	
	private void addNewEasyRoom3(List<Model> models) {
		Room room = new Room(new Point3D(-4.5,0,16.5));
		double[] north = {-1, 1};
		double[] south = {-2};
		double[] east = {-1,-2};
		double[] west = {-1, -2};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createPillar(models, new Point3D(-4.1,0,18.6));
		creator.createPillar(models, new Point3D(-2,0,29.6));
	}
	
	private void addNewEasyRoom4(List<Model> models) {
		Room room = new Room(new Point3D(-11.2,0,36.9));
		double[] north = {2,1,-1};
		double[] south = {-4};
		double[] east = {2};
		double[] west = {-2};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createPillar(models, new Point3D(-4.2,0,39.9));
		creator.createTable(models, new Point3D(-9.2,0,41),false);
		creator.createPillar(models, new Point3D(-14.2,0,39.9));
	}
	
	private void addNewEasyRoom5(List<Model> models) {
		Room room = new Room(new Point3D(-22.9,0,49));
		double[] north = {-1};
		double[] south = {-1};
		double[] east = {1};
		double[] west = {1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
	}
	
	private void addNewEasyRoom6(List<Model> models) {
		Room room = new Room(new Point3D(-19.1,0,57.2));
		double[] north = {-1, 1};
		double[] south = {1, -1};
		double[] east = {1};
		double[] west = {1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
	}
	
	private void addEasyRoom1(List<Model> models){
		Room room = new Room(new Point3D(-3.9,0,17.5));
		double[] north = {2};
		double[] south = {-2};
		double[] east = {2,-2};
		double[] west = {-2, -2};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createPillar(models, new Point3D(-5.3,0,12.6));
	}
	
	private void addEasyRoom2(List<Model> models){
		Room room = new Room(new Point3D(-20.3,0,33.9));
		double[] north = {2};
		double[] south = {2};
		double[] east = {2,2};
		double[] west = {-2, 1, -1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createPillar(models, new Point3D(-19.1,0,23.9));
		creator.createTable(models, new Point3D(-25.1,0,27.6),true);
		creator.createPillar(models, new Point3D(-22.1,0,33.6));
		creator.createTable(models, new Point3D(-25.1,0,38.6),true);
		creator.createPillar(models, new Point3D(-22.1,0,44.6));
	}
	
	private void addEasyRoom3(List<Model> models){
		Room room = new Room(new Point3D(-7.5,0, 51.3));
		double[] north = {-1};
		double[] south = {1};
		double[] east = {-1,1,.5};
		double[] west = {2,.5};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
	}
	
	private void addMediumRoom1(List<Model> models){
		Room room = new Room(new Point3D(16.25,0, 17.25));
		double[] north = {-2,-1};
		double[] south = {2,-1};
		double[] east = {2,1};
		double[] west = {2,-1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createPillar(models, new Point3D(10, 0, 11));
	}
	
	private void addHardRoom1(List<Model> models){
		Room room = new Room(new Point3D(36.8,0, 25.5));
		double[] north = {-2};
		double[] south = {-1,1};
		double[] east = {-1};
		double[] west = {1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
	}
	
	private void addHardRoom2(List<Model> models){
		Room room = new Room(new Point3D(45.8,0, 17));
		double[] north = {-2};
		double[] south = {2};
		double[] east = {1};
		double[] west = {1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
	}
	
	private void addHardRoom3(List<Model> models){
		Room room = new Room(new Point3D(56,0, 45.65));
		double[] north = {2,.5};
		double[] south = {1,.5,-1};
		double[] east = {-1,-2,-2,-1};
		double[] west = {2,2,2};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createTable(models, new Point3D(62.6,0, 34.1), true);
		creator.createTable(models, new Point3D(62.6,0, 49.5), true);
		creator.createTable(models, new Point3D(49.5,0, 41.8), true);
		creator.createTable(models, new Point3D(49.5,0, 57.2), true);
		creator.createPillar(models, new Point3D(56,0, 61.2));
	}
	
	private void addHardRoom4(List<Model> models){
		Room room = new Room(new Point3D(31,0, 64.8));
		double[] north = {2,1,1};
		double[] south = {-2,-1,-1};
		double[] east = {-1};
		double[] west = {-1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
	}
	
	private void addBossRoom(List<Model> models){
		Room room = new Room(new Point3D(-1.7,0,74));
		double[] north = {2,.5,2};
		double[] south = {-1,-.5,1,1,-1};
		double[] west = {-1,2};
		double[] east = {2,1};
		room.setWallLengths(WallLocation.North, north);
		room.setWallLengths(WallLocation.South, south);
		room.setWallLengths(WallLocation.East, east);
		room.setWallLengths(WallLocation.West, west);
		creator.createRoom(models, room);
		creator.createPillar(models, new Point3D(5, 0, 67));
		creator.createPillar(models, new Point3D(11, 0, 76));
		creator.createPillar(models, new Point3D(-7, 0, 69));
		creator.createPillar(models, new Point3D(-13, 0, 77));
		creator.createTable(models, new Point3D(0, 0, 83),true);
	}
	
	public void addOldRooms(List<Model> models){
		// room 2 (hard)
		models.add( new Model(resource.getOBJs()[5], 7, new Point3D(11, 0, 29.2), gameModel.zero(), 1, .5)); // wall
		models.add( new Model(resource.getOBJs()[5], 6, new Point3D(20.8, 0, 29.2), gameModel.zero(), 1, 2)); // wall
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
		// room 2 (hard)
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(14, 0, 34), gameModel.zero(), 1, 1)); // pillar
		// room 3 (hard)
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(38, 0, 51), gameModel.zero(), 1, 1)); // pillar
		models.add( new Model(resource.getOBJs()[6], 3, new Point3D(20, 0, 51), gameModel.zero(), 1, 1)); // pillar
		// --- Tables --- room 2 (hard)
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(20, 0, 41.8), gameModel.rtAngle(), .4, 1)); // table
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(28.5, 0, 41.8), gameModel.rtAngle(), .4, 1)); // table
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(37, 0, 41.8), gameModel.rtAngle(), .4, 1)); // table
		// room 3 (hard)
		models.add( new Model(resource.getOBJs()[4], 4, new Point3D(15.5, 0, 58.4), gameModel.rtAngle(), .4, 1)); // table
	}
	
	private Point3D[][] createGuardOrders() {
		Point3D[][] orders= new Point3D[][]{
/*guard 0*/	new Point3D[]{new Point3D(6,0,26),new Point3D(16,0,26)}, 
/*guard 1*/	new Point3D[]{new Point3D(24,0,31),new Point3D(24,0,41)},
/*guard 2*/	new Point3D[]{new Point3D(32,0,31),new Point3D(32,0,41)},
/*guard 3*/	new Point3D[]{new Point3D(37,0,54),new Point3D(14,0,54)},
/*guard 4*/	new Point3D[]{new Point3D(11,0,60),new Point3D(11,0,47)},
/*guard 5*/	new Point3D[]{new Point3D(-14.7,0,11),new Point3D(-14.7,0,-2),new Point3D(-26.3,0,-2),new Point3D(-26.3,0,11)}, 
/*guard 6*/	new Point3D[]{new Point3D(-20.3,0,23.4),new Point3D(-6.3,0,23.4)},
/*guard 7*/	new Point3D[]{new Point3D(-2.2,0,31.9),new Point3D(-20.2,0,31.9)},
/*guard 8*/	new Point3D[]{new Point3D(-20.2,0,33.9),new Point3D(-20.2,0,39.9)},
/*guard 9*/	new Point3D[]{new Point3D(57,0, 49.5),new Point3D(49.5,0, 49.5)},
/*guard 10*/new Point3D[]{new Point3D(55,0, 41.8),new Point3D(62.6,0, 41.8)}, 
/*guard 11*/new Point3D[]{new Point3D(55,0, 57.2),new Point3D(62.6,0, 57.2)}
		};
		
		return orders;
	}
	
	private void createCharacters() {
		say("Creating characters...");
		ninja= new Ninja(gameModel, models.get(2), .35, .5);
		loadingBar.increaseProgress(1);
		boss= new Boss(gameModel, models.get(3), 1.5, 1.5);
		loadingBar.increaseProgress(1);
		
		Point3D[][] orders= createGuardOrders();
		for (int i= 0; i < numGuards; i++){
			guards[i]= new Guard(gameModel, models.get(i + 4), .35, .5, orders[i]); 
			loadingBar.increaseProgress(1);
		}
	}
	
	private void createObstacles() {
		say("Creating obstacles...");
		// scroll
		int start = 4 + numGuards;
		obstacles[0]= new Obstacle(models.get(start++), true, .2, .2, 1.3); 
		scroll= obstacles[0];
		loadingBar.increaseProgress(1);
		int obsLoc = 1;
		for(int i = start; i < models.size();i++){
			int type = models.get(i).getTxtr();
			//pillar
			if(type == 3){
				obstacles[obsLoc++]= new Obstacle(models.get(i), false, 1.5, 1.5, 5); 
				loadingBar.increaseProgress(1);				
			}
			//tables
			else if(type == 4){
				obstacles[obsLoc++]= new Obstacle(models.get(i), true, 2.25, 3, .95); 
				loadingBar.increaseProgress(1);
			}
			//walls
			else if(type >= 5 && type <=7){
				obstacles[obsLoc++]= new Obstacle(models.get(i), false, .65, 4.15, 5); 
				loadingBar.increaseProgress(1);
			}
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
	
	public LoadingBar getLoadingBar() {
		return loadingBar;
	}
	
	private void say(String message){
		System.out.println(message);
	}
	
}
