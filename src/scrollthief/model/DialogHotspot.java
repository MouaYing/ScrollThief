package scrollthief.model;

/*
 * This is for marking where a dialog should occur
 */
public class DialogHotspot {
	
	Point3D loc; //coordinates for where the dialog should appear
	int r; //radius for how far from the coordinates the dialog should appear
	String text; //text to be shown
	boolean repeatable; //whether the hotspot should show up multiple times

	//default constructor with minimum params
	public DialogHotspot(int x, int y, String text) {
		this(x, y, text, 10, true);
	}
	
	//custom constructor
	public DialogHotspot(int x, int y, String text, int r, boolean repeatable) {
		loc = new Point3D(x, y, 0);
		this.text = text;
		this.r = r;
		this.repeatable = repeatable;
	}
	
	public boolean checkForActivate(Point3D ninjaLoc) {
		return repeatable && loc.minus(ninjaLoc).length() < r;
	}
	
	public Point3D getLoc() {
		return loc;
	}

	public void setLoc(Point3D loc) {
		this.loc = loc;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRepeatable() {
		return repeatable;
	}

	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}

	public void say(String text) {
		System.out.println(text);
	}
}
