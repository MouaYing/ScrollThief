package scrollthief.model;

public class Button {
	
	private int width;
	private int height;
	private int x;
	private int y;
	private String text;
	private boolean selected;
	
	public Button(int x, int y, int width, int height, String text, boolean selected){
		this.width = width;
		this.x = x;
		this.y = y;
		this.height = height;
		this.text = text;
		this.selected = selected;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean IsSelected() {
		return selected;
	}

	public boolean isHit(int x, int y){
		if((x >= x && x <= x+width) && (y >= y && y <= y+height)){
			return true;
		}
		return false;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	

}
