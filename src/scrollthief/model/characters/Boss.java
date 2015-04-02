package scrollthief.model.characters;

import java.util.ArrayList;

import scrollthief.model.GameModel;
import scrollthief.model.Model;
import scrollthief.model.OBJ;
import scrollthief.model.Point3D;
import scrollthief.model.Projectile;

public class Boss extends Character{
	ArrayList<Character> projectiles= new ArrayList<Character>();
	double sightRange= 17;
	int tickCount= 0;
	OBJ[] standing;
	OBJ[] stomping;
	boolean inBattle= false;

	public Boss(GameModel gameModel, Model model, double boxLength, double boxWidth) {
		super(gameModel, model, boxLength, boxWidth, "Boss");
		turnRate= .02;
		setSpeed(.2);
		standing= new OBJ[] {model.getObj()};
		stomping= gameModel.getResource().getBossStomp();
		motion= standing;
		hp=100;
	}
	
	public void update(){
		if (!isNear())
			return;
		
		tickCount++;
		navigate();
		move();
		if (tickCount >= 30 && isFacingNinja()) // determine whether to shoot or not
			shoot();
	}
	
	public double getDeltaX(double direction, double movement) {
		Point3D bossDelta= calcDelta(0, 0);
		return bossDelta.x;
	}
	
	public double getDeltaZ(double direction, double movement) {
		Point3D bossDelta= calcDelta(0, 0);
		return bossDelta.z;
	}
	
	public void animate(int tick){
		if(!alive)
			return;
		
		if (isNear() && !inBattle){
			inBattle= true;
			motion= stomping;
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
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		faceToward(ninjaLoc);
	}
	
	private void shoot(){
		tickCount= 0;
		double scale= 3;
		double direction= getAngle() - Math.PI;
		OBJ[] objs= gameModel.getResource().getOBJs();
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		// calculate target vector
		Point3D bossHead= new Point3D(getLoc().x, getLoc().y + 3.2, getLoc().z);
		double dist= ninjaLoc.minus(bossHead).length();
		
		double targetX= Math.sin(direction);
		double targetY= (-1/dist) * scale;
		double targetZ= -Math.cos(direction);
		
		Point3D targetVector= new Point3D(targetX, targetY, targetZ);
		
		// create projectile
		scale= 2;
		double[] rot = model.getRot().clone();
		rot[0]= -targetY * scale;
		Model projModel= new Model(objs[8], 11, bossHead, rot, .4, 1);
		gameModel.getProjectiles().add(new Projectile(gameModel, projModel, targetVector));
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
