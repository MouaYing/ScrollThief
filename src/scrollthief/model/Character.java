package scrollthief.model;

import java.awt.geom.Point2D;

/**
 * @author Jon "Neo" Pimentel
 * 
 * This class is meant to hold any additional game information not pertaining to the 3d model.
 * e.g. health, what frame of their animation they are in. It is also meant to calculate things like
 * the character's hitbox. 
 * It also provides access to the 3d model information.
 */
public class Character {
	Model model;
	GameModel gameModel;
	Point2D[][] hitBox;
	double speed= 0;

	public Character(GameModel gameModel, Model model, double boxWidth, double boxLength){
		this.gameModel= gameModel;
		this.model= model;
		Point2D[] boxPoints= GameModel.findPoints(boxWidth, boxLength);
		hitBox= GameModel.createHitBox(boxPoints);
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
	
	public Point2D[][] getHitBox(){
		return hitBox;
	}
	
	//--------------- setters -----------------------------------------------------
	public void setAngle(double newAngle){
		double[] newRot= model.getRot();
		newRot[1]= newAngle;
		model.setRot(newRot);
	}
	
	public void setSpeed(double newSpeed){
		speed= newSpeed;
	}
	
	public void setLoc(Point3D newPoint){
		model.setLoc(newPoint);
	}
	
}
