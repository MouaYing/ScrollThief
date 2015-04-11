package scrollthief.model.characters;

import java.util.ArrayList;
import java.util.Random;

import scrollthief.model.Data;
import scrollthief.model.GameModel;
import scrollthief.model.Model;
import scrollthief.model.OBJ;
import scrollthief.model.Point3D;
import scrollthief.model.Projectile;
import scrollthief.model.SoundFile;

public class Boss extends Character{
	ArrayList<Character> projectiles= new ArrayList<Character>();
	double sightRange= 30;
	int tickCount= 0;
	OBJ[] standing;
	OBJ[] pounce;
	OBJ[] shooting;
	OBJ[] windUp;
	OBJ[] windDown;
	boolean inBattle= false;
	boolean isPouncing = false;
	Random randomGenerator;
	public Point3D lastPouncePoint;
	float pounceSpeed = .3f;
	float jumpSpeed = 0.2f;
	boolean readyForAttack;
	int coolDown = 0;
	int charge = 0; //for charging up the big attack
	final int ATTACK_SIZE_SMALL = 1;
	final int ATTACK_SIZE_BIG = 2;
	final int ATTACK_FRENZY = 3;
	int TICKS_BETWEEN_POUNCES = 1000;
	int TICKS_BETWEEN_ATTACKS = 200;
	int TICKS_FOR_COOLDOWN = 150;
//	final int TICKS_FOR_CHARGE = 200;
	final double HEALTH_RATIO_BEFORE_HEAT_SEEKING = .5; 
	final double HEALTH_RATIO_BEFORE_FRENZY = .4;  
	final int PROBABILITY_OF_BIG_ATTACK = 30;
	final int PROBABILITY_OF_SMALL_ATTACK = 60;
	final int PROBABILITY_OF_FRENZY_ATTACK = 30;
	final int PROBABILITY_OF_SMALL_VOLLEY_ATTACK = 30;
	final int PROBABILITY_OF_LARGE_VOLLEY_ATTACK = 30;
	final int TOTAL_ATTACK_PROBABILITY = PROBABILITY_OF_BIG_ATTACK + PROBABILITY_OF_SMALL_ATTACK + PROBABILITY_OF_FRENZY_ATTACK;
	int fullHP = 100;
	private float gravity = .01f;
	private int deathCounter = 0;

	public Boss(GameModel gameModel, Model model, double boxLength, double boxWidth) {
		super(gameModel, model, boxLength, boxWidth, "Boss");
		turnRate= .05;
		standing= new OBJ[] {model.getObj()};
		pounce = gameModel.getCurrentLevel().getBossPounce();
		shooting = gameModel.getCurrentLevel().getBossShooting();
		windUp = gameModel.getCurrentLevel().getBossWindUp();
		windDown = gameModel.getCurrentLevel().getBossWindDown();
		motion= standing;
		randomGenerator = new Random();
		hp=100;
		fullHP = 100;
		maxHp = 100;
	}
	
	public void update(){
		if (!isNear())
			return;
		
		if (!alive) {
			if (deathCounter < 120) {
				deathCounter++;
				model.setScale(model.getScale() * 0.98);
			}
			else if (deathCounter == 120) {
				model.setShouldDraw(false);
				getHitBox().nullify();
				Point3D loc = getModel().getLoc();
				Model scrollModel = gameModel.getScroll().getModel();
				scrollModel.setLoc(new Point3D(loc.x, loc.y + 1, loc.z));
				scrollModel.setShouldDraw(true);
				gameModel.getSound().stopMusic();
				gameModel.getSound().setShouldPlayMusic(false);
				deathCounter++;
		    } else {
		    	Model scrollModel = gameModel.getScroll().getModel();
		    	double[] rotation = scrollModel.getRot();
				scrollModel.setAngle(rotation[1] + 0.03);
				gameModel.getScroll().getHitBox().calculateNewHitBox(gameModel.getScroll().getModel());
			}
			return;
		}
		
		tickCount++;
		
		if(coolDown > 0) {
			coolDown--;
		}
		
		if(isPouncing) {
			pounceController();
		}
		else if(tickCount > TICKS_BETWEEN_POUNCES) {
			//say("starting pounce");
			startPounce();
		}
		else {
			move();
			if(motion != windUp)
				handleAttack();
			if(coolDown <=0)
				navigate();
		}
	}
	public double getDeltaX(double direction, double movement) {
		double deltaX= Math.sin(direction) * speed;
		double deltaZ= -Math.cos(direction) * speed;
		Point3D bossDelta= calcDelta(deltaX, deltaZ);
		return bossDelta.x;
	}
	
	public double getDeltaZ(double direction, double movement) {
		double deltaX= Math.sin(direction) * speed;
		double deltaZ= -Math.cos(direction) * speed;
		Point3D bossDelta= calcDelta(deltaX, deltaZ);
		return bossDelta.z;
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
	
	@Override
	public void advanceFrame(){
		animFrame++;
		if (animFrame >= motion.length) {
			animFrame= 0;
			if(motion == windUp) {
				shoot(ATTACK_SIZE_BIG, false);
				motion = shooting;
				return;
			}
			if(motion == shooting) {
				motion = windDown;
				return;
			}
			if(motion == windDown) {
				motion = standing;
				return;
			}
			motion = standing;
		}
	}
	
	public void reset(){
		setAngle(0);
		setGoalAngle(0);
		setLoc(new Point3D(0,0,76));
		hp = maxHp;
		getModel().setShouldDraw(true);
		if (!alive) {
			getModel().setScale(0.2);
			getHitBox().calculateNewHitBox(getModel());
			alive = true;
			deathCounter = 0;
		}
		gameModel.getScroll().getModel().setShouldDraw(false);
		gameModel.getScroll().getModel().setLoc(new Point3D(10000, 10000, 10000));
		gameModel.getScroll().getHitBox().calculateNewHitBox(gameModel.getScroll().getModel());
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
	}
	
	private void pounceController() {
		if(getSpeed() > 0 && getLoc().y == 0) {  //end pounce, he is there!
			tickCount = 0;
			setSpeed(0);
			setDeltaY(0);
			isPouncing = false;
			return;
		}
		else if(getSpeed() == 0 && isFacingPouncePoint(0.001f) && animFrame == 16) {
			double dist = getLoc().minus(lastPouncePoint).length();
			//say("distance to boss's target: " + dist);
			float nextPounceSpeed = (float)((1 + dist / 20) * pounceSpeed);
			setSpeed(nextPounceSpeed);
			//say("Boss's Speed: " + speed);
			float nextJumpSpeed = (float)(dist / 10 * jumpSpeed);
			if(nextJumpSpeed > jumpSpeed)
				nextJumpSpeed = jumpSpeed;
			setDeltaY(nextJumpSpeed);
		}
		if(getSpeed() == 0 && isFacingPouncePoint(0.1f) && motion != pounce) {
			animFrame= 0;
			motion = pounce;
		}
		navigate();
		move();
	}
	
	private boolean isFacingPouncePoint(float fov){
		Point3D loc= getLoc();
		double bossAngle= -getAngle();
		double angToPoint= Math.atan2(lastPouncePoint.x - loc.x, lastPouncePoint.z - loc.z);
		
		// find the smallest difference between the angles
		double angDif = (angToPoint - bossAngle);
		angDif = gameModel.floorMod((angDif + Math.PI),(2 * Math.PI)) - Math.PI;
		
		// check if this angle difference is small enough that the point would be in boss's FOV
		if (angDif > -fov && angDif < fov)
			return true;
		
		return false;
	}	
	
	private void handleAttack() {
		if(tickCount % TICKS_BETWEEN_ATTACKS == 0)
			readyForAttack = true;
		if(!isFacingNinja() || !readyForAttack)
			return;
		
		int rand = getRand(TOTAL_ATTACK_PROBABILITY);
		
		if((rand -= PROBABILITY_OF_BIG_ATTACK) < 0) {
			animFrame= 0;
			motion = windUp;
		}
		else if((rand -= PROBABILITY_OF_SMALL_ATTACK) < 0) {
			shoot(ATTACK_SIZE_SMALL, false);
		}
		else if((double)hp / (double)maxHp < HEALTH_RATIO_BEFORE_FRENZY && (rand -= PROBABILITY_OF_FRENZY_ATTACK) < 0) {
			shoot(ATTACK_SIZE_BIG, true);
		}
		coolDown = TICKS_FOR_COOLDOWN;
	}
	
	private void setNextPouncePoint() {
		int rand = getRand(gameModel.getCurrentLevel().getBossPouncePoints().size());
		while(gameModel.getCurrentLevel().getBossPouncePoints().get(rand).x == lastPouncePoint.x &&
				gameModel.getCurrentLevel().getBossPouncePoints().get(rand).y == lastPouncePoint.y)
			rand = getRand(gameModel.getCurrentLevel().getBossPouncePoints().size());
		lastPouncePoint = gameModel.getCurrentLevel().getBossPouncePoints().get(rand);
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
	
	private int getRand(int max) {
		return randomGenerator.nextInt(max);
	}
	
	//attackSize: 1 = small, 2 = large
	private void shoot(int attackSize, boolean isFrenzy) {
		readyForAttack = false;	
		animFrame= 0;
		motion = shooting;
		double directionScale = 4;
		double direction= getAngle() - Math.PI;
		double deltaX= Math.sin(direction) * directionScale;
		double deltaZ= -Math.cos(direction) * directionScale;
		OBJ[] objs= gameModel.getResource().getOBJs();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		// calculate target vector
		Point3D bossHead= new Point3D(getLoc().x + deltaX, getLoc().y + 2.4, getLoc().z + deltaZ);
		double dist= ninjaLoc.minus(bossHead).length();
		
		Point3D targetVector= calculateTargetVector(direction, dist);
		createProjectile(attackSize, targetVector, objs[8], bossHead, direction);
		gameModel.getResource().getSound().playEffect(SoundFile.FIREBALL);
		
		if(isFrenzy) { //if it's in a frenzy, then shoot left and right as well
			direction= getAngle() - Math.PI + .2;
			targetVector= calculateTargetVector(direction, dist);
			createProjectile(attackSize, targetVector, objs[8], bossHead, direction);
			direction= getAngle() - Math.PI - .2;
			targetVector= calculateTargetVector(direction, dist);
			createProjectile(attackSize, targetVector, objs[8], bossHead, direction);
		}
	}
	
	public Point3D calculateTargetVector(double direction, double dist) {
		double scale= 1;
		double targetX= Math.sin(direction);
		double targetY= (-1/dist) * scale;
		double targetZ= -Math.cos(direction);
		
		return new Point3D(targetX, targetY, targetZ);
	}
	
	public void createProjectile(int attackSize, Point3D targetVector, OBJ obj, Point3D bossHead, double direction) {
		int scale= 4;
		double sizeScale = .4 * attackSize;
		double[] rot = model.getRot().clone();
		rot[0]= -targetVector.y * scale;
		Model projModel= new Model(obj, 11, bossHead, rot, sizeScale, 1);
		boolean heatSeeking = ((double)hp / (double)maxHp < HEALTH_RATIO_BEFORE_HEAT_SEEKING) 
				&& attackSize == ATTACK_SIZE_SMALL;
		gameModel.getProjectiles().add(new Projectile(gameModel, projModel, targetVector, attackSize, heatSeeking, bossHead, direction));
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
		hp -= damage;
		if(hp < 0) {
			alive = false;
			hp = 0;
			Data.say("Boss killed!!!");
			return;
		}
		this.model.setFlash(true);
		Data.say("hit boss for " + damage + " damage");
		//increase boss difficulty after each hit
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
	
	public int getFullHP() {
		return fullHP;
	}
}
