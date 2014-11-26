package scrollthief.model;

public class Boss extends Character{

	public Boss(GameModel gameModel, Model model, double boxLength, double boxWidth) {
		super(gameModel, model, boxLength, boxWidth);
		turnRate= .02;
		setSpeed(.075);
	}
	
	public void navigate(){
		Point3D ninjaLoc= gameModel.getNinjaLoc();
		faceToward(ninjaLoc);
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
}
