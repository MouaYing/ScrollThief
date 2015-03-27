package scrollthief.model;

import java.util.ArrayList;
import java.util.Random;

import scrollthief.model.Point3D;

public class Boss extends Character{
	ArrayList<Character> projectiles= new ArrayList<Character>();
	double sightRange= 30;
	int tickCount= 0;
	OBJ[] standing;
	OBJ[] pouncing;
	boolean inBattle= false;
	boolean isPouncing = false;
	Random randomGenerator;
	Point3D lastPouncePoint;
	float pounceSpeed = 1.5f;
	float jumpSpeed = 0.2f;
	boolean readyForAttack;
	int charge = 0; //for charging up the big attack
	final int ATTACK_SIZE_SMALL = 1;
	final int ATTACK_SIZE_BIG = 2;
	int TICKS_BETWEEN_POUNCES = 1000;
	int TICKS_BETWEEN_ATTACKS = 75;
	final int TICKS_FOR_CHARGE = 200;
	final int PROBABILITY_OF_BIG_ATTACK = 30; //out of 100
	final int PROBABILITY_OF_SMALL_ATTACK = 60; //out of 100
	final int TOTAL_ATTACK_PROBABILITY = PROBABILITY_OF_BIG_ATTACK + PROBABILITY_OF_SMALL_ATTACK;
	
	public Boss(GameModel gameModel, Model model, double boxLength, double boxWidth) {
		super(gameModel, model, boxLength, boxWidth);
		lastPouncePoint = gameModel.getCurrentLevel().getBossPouncePoints().get(0);
		turnRate= .05;
		standing= new OBJ[] {model.getObj()};
		pouncing= gameModel.getResource().getBossPounce();
		motion= standing;
		maxHp = 100;
		hp=maxHp;
		randomGenerator = new Random();
	}
	
	public void update(){
		if (!isNear() || !alive)
			return;
		
		tickCount++;
		
		if(isPouncing) {
			pounceController();
		}
		else if(tickCount > TICKS_BETWEEN_POUNCES) {
			startPounce();
		}
		else {
			navigate();
			handleAttack();
			move();
		}
	}
	
	public void animate(int tick){		
		if (isNear() && !inBattle){
			inBattle= true;
			motion= standing;
			animFrame= 0;
		}
		
		if (tick % 2 == 0)
			advanceFrame();
		
		model.setOBJ(motion[animFrame]);
	}
	
	public void reset(){
		setAngle(0);
		setGoalAngle(0);
		setLoc(new Point3D(0,0,76));
	}
	
	private void navigate(){
		Point3D target;
		if(isPouncing) {
			target = lastPouncePoint;
		}
		else {
			target = gameModel.getNinjaLoc();
		}
		faceToward(target);
	}
	
	private void startPounce() {
		setNextPouncePoint();
		isPouncing = true;
		Data.say("pouncing!");
	}
	
	private void pounceController() {
		if(getSpeed() > 0 && getLoc().y == 0) { //collisionWithPounce()) {  //end pounce, he is there!
			tickCount = 0;
			setSpeed(0);
			setDeltaY(0);
			isPouncing = false;
			return;
		}
		else if(getSpeed() == 0 && isFacingPouncePoint()) {
			double dist = getLoc().minus(lastPouncePoint).length();
			float nextPounceSpeed = (float)((1 + dist / 20) * pounceSpeed);
			setSpeed(nextPounceSpeed);
			float nextJumpSpeed = (float)(dist / 10 * jumpSpeed);
			if(nextJumpSpeed > jumpSpeed)
				nextJumpSpeed = jumpSpeed;
			setDeltaY(nextJumpSpeed);
		}
		navigate();
		move();
	}
	
	private boolean isFacingPouncePoint(){
		float fov= 0.001f; // (radians) needs tuning
		Point3D loc= getLoc();
		double bossAngle= -getAngle();
		double angToPoint= Math.atan2(lastPouncePoint.x - loc.x, lastPouncePoint.z - loc.z);
		
		// find the smallest difference between the angles
		double angDif = (angToPoint - bossAngle);
		angDif = gameModel.floorMod((angDif + Math.PI),(2 * Math.PI)) - Math.PI; 
		//say("Angle difference is: "+angDif);
		
		// check if this angle difference is small enough that the point would be in boss's FOV
		if (angDif > -fov && angDif < fov)
			return true;
		
		return false;
	}	
	
	private void handleAttack() {
		if(charge > 0) {
			charge--;
			if(charge <= 0)
				shoot(ATTACK_SIZE_BIG);
			return;
		}
		if(tickCount % TICKS_BETWEEN_ATTACKS == 0)
			readyForAttack = true;
		if(!isFacingNinja() || !readyForAttack)
			return;
		
		int rand = getRand(TOTAL_ATTACK_PROBABILITY);
		
		if((rand -= PROBABILITY_OF_BIG_ATTACK) < 0) {
			charge = TICKS_FOR_CHARGE;
		}
		else if((rand -= PROBABILITY_OF_SMALL_ATTACK) < 0) {
			shoot(ATTACK_SIZE_SMALL);
		}
	}
	
	private void setNextPouncePoint() {
		int rand = getRand(gameModel.getCurrentLevel().getBossPouncePoints().size());
		while(gameModel.getCurrentLevel().getBossPouncePoints().get(rand).x == lastPouncePoint.x &&
				gameModel.getCurrentLevel().getBossPouncePoints().get(rand).y == lastPouncePoint.y)
			rand = getRand(gameModel.getCurrentLevel().getBossPouncePoints().size());
		lastPouncePoint = gameModel.getCurrentLevel().getBossPouncePoints().get(rand);
	}
	
	private int getRand(int max) {
		return randomGenerator.nextInt(max);
	}
	
	//attackSize: 1 = small, 2 = large
	private void shoot(int attackSize) {
		readyForAttack = false;
		Data.say("Boss Attack " + attackSize);
		double scale= 1;
		double directionScale = 4;
		double direction= getAngle() - Math.PI;
		double deltaX= Math.sin(direction) * directionScale;
		double deltaZ= -Math.cos(direction) * directionScale;
		OBJ[] objs= gameModel.getResource().getOBJs();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		// calculate target vector
		Point3D bossHead= new Point3D(getLoc().x + deltaX, getLoc().y + 2.4, getLoc().z + deltaZ);
		double dist= ninjaLoc.minus(bossHead).length();
		
		double targetX= Math.sin(direction);
		double targetY= (-1/dist) * scale;
		double targetZ= -Math.cos(direction);
		
		Point3D targetVector= new Point3D(targetX, targetY, targetZ);
		
		// create projectile
		scale= 4;
		double sizeScale = .3 * attackSize;
		double[] rot = model.getRot().clone();
		rot[0]= -targetY * scale;
		Model projModel= new Model(objs[8], 11, bossHead, rot, sizeScale, 1);
		gameModel.getProjectiles().add(new Projectile(gameModel, projModel, targetVector, attackSize, bossHead, direction));
		gameModel.getModels().add(projModel);
	}

	public Point3D calcDelta(double deltaX, double deltaZ){
		Point3D scrollLoc= gameModel.getScroll().getLoc();
		
		Point3D input= new Point3D(deltaX, 0, deltaZ);
		Point3D scrollNormal= getLoc().minus(scrollLoc);
		scrollNormal.Normalize();
		
		Point3D undesired= scrollNormal.mult(input.dot(scrollNormal));
		Point3D desired= input.minus(undesired); 
		
		return desired;
	}
	
	public boolean isNear(){
		Point3D loc= model.getLoc();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		
		double distance= ninjaLoc.minus(loc).length();
		
		if (distance < sightRange)
			return true;
		
		return false;
	}
	
	public void takeDamage(int damage) {
		turnRate += damage / maxHp / 10;
		TICKS_BETWEEN_POUNCES -= damage / maxHp * 100;
		TICKS_BETWEEN_ATTACKS -= damage / maxHp * 100 / 3;
		if(hp <= 0) {
			Data.say("Boss killed!!!");
			alive = false;
		}
	}
	
	private boolean isFacingNinja(){
		float fov= .75f; // (radians) needs tuning
		Point3D loc= getLoc();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		double bossAngle= -getAngle();
		double angToNinja= Math.atan2(ninjaLoc.x - loc.x, ninjaLoc.z - loc.z);
		
		// find the smallest difference between the angles
		double angDif = (angToNinja - bossAngle);
		angDif = gameModel.floorMod((angDif + Math.PI),(2 * Math.PI)) - Math.PI; 
		//say("Angle difference is: "+angDif);
		
		// check if this angle difference is small enough that the ninja would be in boss's FOV
		if (angDif > -fov && angDif < fov)
			return true;
		
		return false;
	}
}
