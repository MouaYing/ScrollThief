package scrollthief;

/**
*
* @author Jon Pimentel
*/
public class Model {

	private OBJModel obj;
	private int textureIndex;
	private Point3D location;
	double[] rotation;
	double scale;
	
	public Model(OBJModel obj, int textureIndex, Point3D location, double[] rotation, double scale){
		this.obj= obj;
		this.textureIndex= textureIndex;
		this.location= location;
		this.rotation= rotation;
		this.scale= scale;
	}
	
	// getters
	public OBJModel getObj(){
		return obj;
	}
	
	public int getTxtr(){
		return textureIndex;
	}
	
	public Point3D getLoc(){
		return location;
	}
	
	public double[] getRot(){
		return rotation;
	}
	
	public double getScale(){
		return scale;
	}
	
	// setters
	public void setLoc(Point3D newLoc){
		location= newLoc;
	}
	
	public void setRot(double[] newRot){
		rotation= newRot;
	}
	
	public void setScale(int newScale){
		scale= newScale;
	}
}
