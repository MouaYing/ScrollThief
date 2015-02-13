package scrollthief.model;

public class Button {
	
	private int width;
	private int height;
	private int x;
	private int y;
	private String text;
	
	public Button(int x, int y, int width, int height, String text){
		this.width = width;
		this.x = x;
		this.y = y;
		this.height = height;
		this.text = text;
	}
	
	public String getText() {
		return text;
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
