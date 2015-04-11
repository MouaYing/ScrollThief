package scrollthief.model;

import java.util.ArrayList;

import scrollthief.model.characters.Character;
import scrollthief.model.characters.Edge;
import scrollthief.model.characters.HitBox;

public class Projectile {
	GameModel gameModel;
	Model model;
	Point3D targetVector;
	HitBox actualHitBox;
	Point3D origLoc;
	double origAngle;
	double turnRate= .03;
	int attackSize;
	boolean heatSeeking;
	double goalAngle, angleDelta = 0;
	
	
	public Projectile(GameModel gameModel, Model model, Point3D targetVector, int attackSize, boolean heatSeeking, Point3D origLoc, double origAngle) {
		this.gameModel= gameModel;
		this.model= model;
		this.targetVector= targetVector;
		actualHitBox = new HitBox(.5, .5, model.getAngle(), model.getLoc());
		this.attackSize = attackSize;
		this.heatSeeking = heatSeeking;
		this.origLoc = origLoc;
		this.origAngle = origAngle;
	}
	
	public void move(){
		double speed= .1;
		Point3D loc= model.getLoc(); 
		if(heatSeeking) {
			setTargetVector();
		}
		Point3D delta= targetVector.mult(speed);
		
		model.setLoc(new Point3D(loc.x + delta.x, loc.y + delta.y, loc.z + delta.z));
		
	}
	
	//for making the projectile ninja seeking
	private void setTargetVector() {
		Character ninja = gameModel.getNinja();
		//double dist= ninja.getLoc().minus(origLoc).length();
		double dist= ninja.getLoc().distanceToPoint(origLoc.to2D());
		faceToward(ninja.getLoc());
		adjustAngle();
		
		double targetX= Math.sin(getAngle() - Math.PI);
		//double targetY= -1/dist;
		double targetY= 0;
		double targetZ= -Math.cos(getAngle() - Math.PI);
		targetVector= new Point3D(targetX, targetY, targetZ);
		targetVector.Normalize();
	}
	
	public boolean ninjaCollision(){
		Character ninja= gameModel.getNinja();
		actualHitBox.createNewHitBox(getModel().getAngle(), getModel().getLoc());
		HitBox ninjaBox = ninja.getHitBox();
		Point3D loc= model.getLoc();
		double threshold= 5;
		ArrayList<Edge> edges= new ArrayList<Edge>();
		double dist= loc.minus(ninja.getLoc()).length();
		
		if (dist > threshold)
			return false;
		
		edges= actualHitBox.getCollidedEdges(ninjaBox);
		
		if (!edges.isEmpty())
			return true;
		
		return false;
	}
	
	public boolean obsCollision(){
		Obstacle[] obstacles= gameModel.getObstacles();
		actualHitBox.createNewHitBox(getModel().getAngle(), getModel().getLoc());
		Point3D loc= model.getLoc();
		double threshold= 10;
		ArrayList<Edge> edges= new ArrayList<Edge>();
		
		for(int i= 0; i < obstacles.length; i++){
			Obstacle obs= obstacles[i];
			double dist= loc.minus(obs.getLoc()).length();
			
			if (dist > threshold || obs.isLow)
				continue;

			edges= actualHitBox.getCollidedEdges(obstacles[i].getHitBox());
			
			if (!edges.isEmpty())
				return true;
		}
		
		return false;
	}
	
	public Model getModel(){
		return model;
	}
	
	public int getAttackDamage() {
		return attackSize;
	}
	
	private void adjustAngle(){
		double angleDif= GameModel.normalizeAngle(goalAngle - getAngle());
		if (angleDif > -turnRate && angleDif < turnRate){
			angleDelta= 0;
			setAngle(goalAngle);
		}
		else angleDelta= (angleDif > 0) ? turnRate : -turnRate;

		actualHitBox.createNewHitBox(getAngle() + angleDelta, getLoc());
		
		setAngle(getAngle() + angleDelta);
	}
	
	public void faceToward(Point3D lookTarget){
		goalAngle= -Math.atan2(lookTarget.x - getLoc().x, lookTarget.z - getLoc().z);
	}
	
	public Point3D getLoc(){
		return model.getLoc();
	}
	
	public double getAngle(){
		return model.getAngle();
	}
	
	public void setAngle(double newAngle){
		model.setAngle(newAngle);
	}

}
