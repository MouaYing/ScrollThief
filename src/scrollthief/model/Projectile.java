package scrollthief.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Projectile {
	GameModel gameModel;
	Model model;
	Point3D targetVector;
	Point2D[][] hitbox;
	int attackSize;
	
	public Projectile(GameModel gameModel, Model model, Point3D targetVector, int attackSize) {
		this.gameModel= gameModel;
		this.model= model;
		this.targetVector= targetVector;
		Point2D[] boxPoints= GameModel.findPoints(.5, .5);
		hitbox= GameModel.createHitBox(boxPoints);
		this.attackSize = attackSize;
	}
	
	public void move(){
		double speed= .15;
		Point3D loc= model.getLoc(); 
		Point3D delta= targetVector.mult(speed);
		
		model.setLoc(new Point3D(loc.x + delta.x, loc.y + delta.y, loc.z + delta.z));
		
	}
	
	public boolean ninjaCollision(){
		Character ninja= gameModel.getNinja();
		Point2D[][] myBox= GameModel.boxToWorld(model, hitbox);
		Point2D[][] ninjaBox= GameModel.boxToWorld(ninja.model, ninja.hitBox);
		Point3D loc= model.getLoc();
		double threshold= 5;
		ArrayList<Point2D[]> edges= new ArrayList<Point2D[]>();
		double dist= loc.minus(ninja.getLoc()).length();
		
		if (dist > threshold)
			return false;
		
		edges= gameModel.collision(myBox, ninjaBox, edges);
		
		if (!edges.isEmpty())
			return true;
		
		return false;
	}
	
	public boolean obsCollision(){
		Obstacle[] obstacles= gameModel.getObstacles();
		Point2D[][] myBox= GameModel.boxToWorld(model, hitbox);
		Point3D loc= model.getLoc();
		double threshold= 10;
		ArrayList<Point2D[]> edges= new ArrayList<Point2D[]>();
		
		for(int i= 0; i < obstacles.length; i++){
			Obstacle obs= obstacles[i];
			double dist= loc.minus(obs.getLoc()).length();
			
			if (dist > threshold || obs.isLow)
				continue;
			
			edges= gameModel.collision(myBox, obstacles[i].hitBox, edges);
			
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

}
