package scrollthief.model.characters;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import scrollthief.model.characters.Boss;
import scrollthief.model.characters.Character;
import scrollthief.model.Data;
import scrollthief.model.GameModel;
import scrollthief.model.GameState;
import scrollthief.model.Model;
import scrollthief.model.characters.Ninja;
import scrollthief.model.OBJ;
import scrollthief.model.Obstacle;
import scrollthief.model.Point3D;

/**
 * @author Jon "Neo" Pimentel
 */
/**
 * This class is meant to hold any additional game information not pertaining to the 3d model.
 * e.g. health, what frame of their animation they are in. It is also meant to calculate things like
 * the character's hitbox. 
 * It also provides access to the 3d model information.
 */
public class Character {
	public Model model;
	GameModel gameModel;
	OBJ defaultOBJ;
	double speed, oldSpeed, deltaY, angleDelta, goalAngle= 0;
	double turnRate= .3;
	public boolean isJumping= false;
	public int attacking = -1; //for the 4 attacking states
	public int nextAttack = -1;
	public int prevAttack = -1;
	public int attackingStateMax = 3;
	public boolean isMoving= false;
	int hp = 3; // player dies after 3 projectile hits, or one boss hit.
	int animFrame= 0; // The frame of animation currently being displayed
	OBJ[] motion= null; // The current animation loop
	boolean alive = true;
	int maxHp;
	
	private Point2D[] edgePrime;
	String type;
	double ninjaDirection;
	boolean bossCollision;
	
	public HitBox actualHitBox;
	protected Obstacle inObstacleBox;
	private int characterCollisionCheckThreshold = 4;
	private int obstacleCollisionCheckThreshold = 10; // to accommodate large walls;

	public Character(GameModel gameModel, Model model, double boxLength, double boxWidth, String type){
		this.gameModel= gameModel;
		this.model= model;
		defaultOBJ= model.getObj();
		actualHitBox = new HitBox(boxLength, boxWidth,getModel().getAngle(), getLoc());
		this.type = type;
		ninjaDirection = 0;
		bossCollision = false;
	}
	
	//function can be overridden to provide specific values - currently only ninja to override this
	public double getDirection() {
		return getAngle() + Math.PI;
	}
	
	//boss overrides these next two for various reasons
	public double getDeltaX(double direction, double movement) {
		return Math.sin(direction) * movement;
	}
	
	public double getDeltaZ(double direction, double movement) {
		return -Math.cos(direction) * movement;
	}
	
	public double calcNewY(Point3D loc) {
		return 0;
	}
	
	protected ArrayList<Edge> characterCollisionCheck(Point3D newLocation, Character characterToCheck){
		ArrayList<Edge> edges = new ArrayList<Edge>();
		double dist = newLocation.minus(characterToCheck.getLoc()).length();
		if(dist < characterCollisionCheckThreshold){
			edges.addAll(actualHitBox.getCollidedEdges(characterToCheck.actualHitBox));
		}
		return edges;
	}
	
	protected ArrayList<Edge> obstacleCollisionCheck(Point3D location, Obstacle obs) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		double obsHeight= obs.getHeight(); // tune this
		double dist= location.minus(obs.getLoc()).length();
		
		if (dist <= obstacleCollisionCheckThreshold) {
			//only for table
			if(obs.getModel().getTxtr() == 4){
				if (obs.isInBox(location))
					inObstacleBox = obs;
				
				if (location.y >= obsHeight)
					return edges;
			}
			
			edges = actualHitBox.getCollidedEdges(obs.actualHitBox);
		}
		
		return edges;
	}
	
	//basic for boss and guards (obstacles are not included to trim computation time and because paths should be set not to
	//collide with obstacles)
	public ArrayList<Edge> specificCharacterCollisionCheck(Point3D newLocation) {
		ArrayList<Edge> collidedEdges = new ArrayList<Edge>();
		Character ninja = gameModel.getNinja();
		collidedEdges.addAll(characterCollisionCheck(newLocation, ninja));
		return collidedEdges;
	}
	
	private Point3D adjustMovementForCollisions(Point3D originalLoc, Point3D newLoc,
			ArrayList<Edge> collidedEdges) {
		double deltaX = newLoc.x - originalLoc.x;
		double deltaZ = newLoc.z - originalLoc.z;
		double originalX = deltaX;
		double originalZ = deltaZ;
		//say("One Collision Check Round ---------> Edges: " + collidedEdges.size());
		//say("Original DeltaX: " + deltaX + ", DeltaZ: " + deltaZ);
		for (Edge e : collidedEdges) {
			double newLocationDistance = newLoc.distanceToLine(e.line.getP1(), e.line.getP2());
			double oldLocationDistance = originalLoc.distanceToLine(e.line.getP1(), e.line.getP2());
			//he collided with this edge but is moving away so don't factor it in
			if(newLocationDistance > oldLocationDistance) {
				continue;
			}
			Point3D normal = e.normal;
			Point3D input= new Point3D(deltaX, 0, deltaZ);
			Point3D projected = normal.mult(input.dot(normal));
			//say("DeltaX: " + deltaX + ", DeltaZ: " + deltaZ + ", Obstacle Normal: " + normal + ", ProjectedX: " + projected.x + ", ProjectedZ: " + projected.z);
			//check if moving in accordance to direction of normal (for the case of moving off the top of a table) normals are inverted because of 
			//orientation of the walls. yes x is compared to normal z
			if((deltaX >= 0 && normal.x <= 0)||(deltaX <= 0 && normal.x >= 0)){
				deltaX -= projected.x;
			}
			if((deltaZ >= 0 && normal.z <= 0)||(deltaZ <= 0 && normal.z >= 0)){
				deltaZ -= projected.z;
			}
		}
		//say("Final DeltaX: " + deltaX + ", DeltaZ: " + deltaZ);
		//make sure I haven't forced the delta in the opposite direction, most I can do is stop progress, not revert it
		if((deltaX <= 0 && originalX >= 0)||(deltaX >= 0 && originalX <= 0)) {
			deltaX = 0;
		}
		if((deltaZ <= 0 && originalZ >= 0)||(deltaZ >= 0 && originalZ <= 0) ) {
			deltaZ = 0;
		}
		//collisions with the boss are going to send him back
		if(bossCollision) {
			deltaX = 0;
		}
		if(bossCollision) {
			deltaZ = 0;
		}
		return new Point3D(originalLoc.x + deltaX, originalLoc.y, originalLoc.z + deltaZ);
	}
	
	private Point3D adjustYValue(Point3D charLoc) {
		
		if (charLoc.y <= 0 && getDeltaY() < 0){ // just landed---don't keep going through the floor
			setDeltaY(0);
			isJumping= false;
		}
		
		charLoc.y += getDeltaY();
		
		if (charLoc.y < 0)
			charLoc.y= 0;
		return new Point3D(charLoc.x, charLoc.y, charLoc.z);
		
	}
	
	public void move() {
		//adjust angle only after no collisions from adjusting
		adjustAngle();
		double movement = getSpeed() * .125f;
		double direction = getDirection();
		double deltaX = getDeltaX(direction, movement);
		double deltaZ = getDeltaZ(direction, movement);
		Point3D currentLocation = getLoc();
		double newY = calcNewY(currentLocation);
		Point3D newLoc= new Point3D(currentLocation.x + deltaX, newY, currentLocation.z + deltaZ);
		actualHitBox.createNewHitBox(getModel().getAngle(), newLoc);
		ArrayList<Edge> collidedEdges = specificCharacterCollisionCheck(newLoc);
		// no collisions
		if(collidedEdges.isEmpty()) {
			if (inObstacleBox != null){ // character is inside hitbox (on top of table)
				double obsHeight= inObstacleBox.getHeight();

				if (newLoc.y < obsHeight){
					if (getDeltaY() < 0) setDeltaY(0);
					newLoc.y= obsHeight;
					isJumping= false;
				}
			}
		}
		else {
			newLoc = adjustMovementForCollisions(currentLocation, newLoc, collidedEdges);
			newLoc = adjustYValue(newLoc);
		}
		setLoc(newLoc); // actually move the character
	}

	// Determine what OBJ to use this tick, and set it as model.obj
	public void animate(int tick){
		// This class is meant to be overridden my subclass
		say("This was supposed to be overridden!");
	}
	
	public Point3D calcDelta(double deltaX, double deltaZ) {
		// override this
		return null;
	}

	private void adjustAngle(){
		double angleDif= GameModel.normalizeAngle(goalAngle - getAngle());
		if (angleDif > -turnRate && angleDif < turnRate){
			angleDelta= 0;
			setAngle(goalAngle);
		}
		else angleDelta= (angleDif > 0) ? turnRate : -turnRate;

		actualHitBox.createNewHitBox(getAngle() + angleDelta, getLoc());
		ArrayList<Edge> collidedEdges = specificCharacterCollisionCheck(getLoc());
		// revert angle movement
		if(collidedEdges.isEmpty()) {
			setAngle(getAngle() + angleDelta);
		}
	}
	
	public void faceToward(Point3D lookTarget){
		goalAngle= -Math.atan2(lookTarget.x - getLoc().x, lookTarget.z - getLoc().z);
	}
	
	public void takeDamage(int damage){
		hp -= damage;
		this.model.setFlash(true);
		if(this instanceof Boss) {
			Character boss= gameModel.getBoss();
			boss.takeDamage(damage);
		}
		else if(this instanceof Ninja) {
//			Data.say("You got hit! ");
		}
	}
	
	public void advanceFrame(){
		animFrame++;
		if (animFrame >= motion.length)
			animFrame= 0;
	}
	
	public void advanceAttackFrame(Ninja ninja){
		animFrame++;
		if (animFrame >= motion.length) {
			ninja.detectAttackCollision();
			animFrame= 0;
			attacking = nextAttack;
			nextAttack = -1;
		}
	}
	
	//--------------- getters -----------------------------------------------------
	public Model getModel(){
		return model;
	}
	
	public double getAngle(){
		return model.getRot()[1];
	}
	
	public double getSpeed(){
		return speed;
	}
	
	public Point3D getLoc(){
		return model.getLoc();
	}
	
	public HitBox getHitBox(){
		return actualHitBox;
	}
	
	public double getDeltaY(){
		return deltaY;
	}
	
	public double getTurnRate(){
		return turnRate; 
	}
	
	public int getHP(){
		return hp;
	}
	
	//--------------- setters -----------------------------------------------------
	public void setAngle(double newAngle){
		double[] newRot= model.getRot();
		newRot[1]= newAngle;
		model.setRot(newRot);
	}
	
	public void setGoalAngle(double angle){
		goalAngle= angle;
	}
	
	public void setSpeed(double newSpeed){
		speed= newSpeed;
	}
	
	public void setLoc(Point3D newPoint){
		model.setLoc(newPoint);
	}
	
	public void setDeltaY(double newDelta){
		deltaY= newDelta;
	}
	
	public void setNinjaDirection(double ninjaDirection) {
		this.ninjaDirection = ninjaDirection;
	}
	
	protected void say(String message){
		System.out.println(message);
	}
	
}
