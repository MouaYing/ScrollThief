package scrollthief.model;

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
	float maxAttackDamage = 50f;
	float attack1Power = .3f;  //what percentage of the maxAttackDamage to use
	float attack2Power = .4f;
	float attack3Power = 1f;
	
	public Ninja(GameModel gameModel, Model model, double boxLength, double boxWidth) {
		super(gameModel, model, boxLength, boxWidth);
		standing= new OBJ[] {defaultOBJ};
		running= gameModel.getResource().getNinjaRun();
		attacking1 = running.clone(); //gameModel.getResource().getNinjaAttack1();
		attacking2 = running.clone(); //gameModel.getResource().getNinjaAttack2();
		attacking3 = running.clone(); //gameModel.getResource().getNinjaAttack3();
		jumping= new OBJ[] {running[0]};
		motion= standing;
	}
	
	// Determine what OBJ to use this tick, and set it as model.obj
	public void animate(int tick){
		// If there is a change in behavior, switch to the appropriate motion
		if (isJumping && !wasJumping){ // Jumping animation takes priority (i.e. use regardless of speed)
			motion= jumping;
			animFrame= 0;
		}
		else if (attacking == 1 && prevAttack != 1) {
			Data.say("attack 1");
			motion= attacking1;
			animFrame= 0;
		}
		else if (attacking == 2 && prevAttack != 2) {
			Data.say("attack 2");
			motion= attacking2;
			animFrame= 0;
		}
		else if (attacking == 3 && prevAttack != 3) {
			Data.say("attack 3");
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
		//area of attacking collision area
//		Point3D atkLoc= new Point3D(getLoc().x + deltaX, newY, getLoc().z);
	}
	
	public void reset(){
		hp= 3;
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
