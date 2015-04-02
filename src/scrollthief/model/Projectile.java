package scrollthief.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import scrollthief.model.characters.Character;
import scrollthief.model.characters.Edge;
import scrollthief.model.characters.HitBox;

public class Projectile {
	GameModel gameModel;
	Model model;
	Point3D targetVector;
	HitBox actualHitBox;
	
	public Projectile(GameModel gameModel, Model model, Point3D targetVector) {
		this.gameModel= gameModel;
		this.model= model;
		this.targetVector= targetVector;
		actualHitBox = new HitBox(.5, .5, model.getAngle(), model.getLoc());
	}
	
	public void move(){
		double speed= .15;
		Point3D loc= model.getLoc(); 
		Point3D delta= targetVector.mult(speed);
		
		model.setLoc(new Point3D(loc.x + delta.x, loc.y + delta.y, loc.z + delta.z));
		
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

}
