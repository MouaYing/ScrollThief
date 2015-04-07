package scrollthief.model.characters;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import scrollthief.model.GameModel;
import scrollthief.model.GameState;
import scrollthief.model.Model;
import scrollthief.model.OBJ;
import scrollthief.model.Obstacle;
import scrollthief.model.Point3D;

/**
 * @author Jon "Neo" Pimentel
 *
 * This class mostly just handles the animations of the Ninja, since they are not shared between characters 
 */
public class Ninja extends Character {
	
	OBJ[] standing;
	OBJ[] running;
	OBJ[] jumping;
	OBJ[] attacking1;
	OBJ[] attacking2;
	OBJ[] attacking3;
	
	boolean wasJumping= false;
	
	int attack1Speed = 10;  //how many ticks it takes to perform each attack
	int attack2Speed = 15;
	int attack3Speed = 30;
	float maxAttackDamage = 10f;
	float[] attackPowers;  //what percentage of the maxAttackDamage to use
	
	private float gravity = .01f;
	
	public Ninja(GameModel gameModel, Model model, double boxLength, double boxWidth) {
		super(gameModel, model, boxLength, boxWidth, "Ninja");
		standing= new OBJ[] {defaultOBJ};
		attackPowers = new float[] {0f, .3f, .4f, 1f};
		running= gameModel.getResource().getNinjaRun();
		attacking1 = running.clone(); //gameModel.getResource().getNinjaAttack1();
		attacking2 = running.clone(); //gameModel.getResource().getNinjaAttack2();
		attacking3 = running.clone(); //gameModel.getResource().getNinjaAttack3();
		jumping= new OBJ[] {running[0]};
		motion= standing;
		hp = 6;																																																																											;
	}
	
	public double getDirection() {
		return ninjaDirection;
	}
	
	public double calcNewY(Point3D loc) {
		setDeltaY((loc.y > 0 || getDeltaY() > 0) ? (getDeltaY() - gravity) : 0);
		double newY= (loc.y + getDeltaY());
		if (newY <= 0){ // on the ground
			newY = 0;
			isJumping= false;
		}
		return newY;
	}
	
	public ArrayList<Edge> specificCharacterCollisionCheck(double deltax, double deltaZ, Point3D newLocation) {
		ArrayList<Edge> collidedEdges = new ArrayList<Edge>();
		inObstacleBox = null;
		Character boss= gameModel.getBoss();
		collidedEdges.addAll(characterCollisionCheck(newLocation, boss));
		
		Character[] guards = gameModel.getGuards();
		
		for (int i= 0; i < guards.length; i++){
			Character guard = guards[i];
			collidedEdges.addAll(characterCollisionCheck(newLocation, guard));
		}
		
		Obstacle[] obstacles= gameModel.getObstacles();
		
		for (int i = 0; i < obstacles.length; i++){
			int preSize = collidedEdges.size();
			collidedEdges.addAll(obstacleCollisionCheck(newLocation, obstacles[i]));

			if (obstacles[i].equals(gameModel.getScroll()) && preSize != collidedEdges.size()){
				gameModel.changeState(GameState.Victory);
				return collidedEdges;
			}
		}
		return collidedEdges;
	}
	
	// Determine what OBJ to use this tick, and set it as model.obj
	public void animate(int tick){
		// If there is a change in behavior, switch to the appropriate motion
		if (isJumping && !wasJumping){ // Jumping animation takes priority (i.e. use regardless of speed)
			motion= jumping;
			animFrame= 0;
		}
		else if (attacking == 1 && prevAttack != 1) {
			speed = 0;
			motion= attacking1;
			animFrame= 0;
		}
		else if (attacking == 2 && prevAttack != 2) {
			speed = 0;
			motion= attacking2;
			animFrame= 0;
		}
		else if (attacking == 3 && prevAttack != 3) {
			speed = 0;
			motion= attacking3;
			animFrame= 0;
		}
		else if (speed == 0 && (oldSpeed != 0 || wasJumping || attacking < 0) && !isJumping){ // Ninja is no longer moving
			motion= standing;
			animFrame= 0;
		}
		else if (speed != 0 && (oldSpeed == 0 || wasJumping) && !isJumping){ // Ninja has started running
			motion= running;
			animFrame= 0;
		}
		
		wasJumping= isJumping;
		oldSpeed= speed;
		prevAttack = attacking;
		
		// determine which frame of the loop to use, and use it
		if (motion.equals(running)){
//			say("tick " + tick);
//			say("Running at speed " + speed);
//			say("2/speed = " + (int)(2/speed));
//			say("mod: " + tick % (int)(2/speed));
			if (tick % (int)(2/speed) == 0) 
				advanceFrame();
		}
		else if(attacking > -1) {
			advanceAttackFrame(this);
		}
		else{
			if (tick % 2 == 0)
				advanceFrame();
		}
		
		model.setOBJ(motion[animFrame]);
	}
	
	public void detectAttackCollision() {
		Point3D loc = getLoc();
		Character boss = gameModel.getBoss();
		
		//area of attacking collision area
		double direction= getAngle() + Math.PI;
		float scale= .125f;
		double swordLength = 1.5;  //should be 1 or 2 probably, but it's longer for testing
		double deltaX= Math.sin(direction) * (scale * swordLength);
		double deltaZ= -Math.cos(direction) * (scale * swordLength);
		Point3D atkLoc= new Point3D(loc.x + deltaX, loc.y, loc.z + deltaZ);
		
		actualHitBox.createNewHitBox(getModel().getAngle(), atkLoc);
		ArrayList<Edge> collidedEdges = characterCollisionCheck(atkLoc, boss);
		
		if(!collidedEdges.isEmpty()) {
			if(attacking >= 0 && attacking < attackPowers.length) {
				boss.takeDamage((int) (attackPowers[attacking] * maxAttackDamage));
			}
		}
	}
	
	public void reset(){
		hp= 6;
		setLoc(new Point3D(0, 0, -5));
		setAngle(0);
		motion= standing;
		isJumping= false;
		setDeltaY(0);
		setGoalAngle(0);
		model.setOBJ(standing[0]);
		animFrame= 0;
		oldSpeed= 0;
	}

}
