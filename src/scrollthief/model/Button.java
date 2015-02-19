package scrollthief.model;

public class Button {
	
	private int width;
	private int height;
	private int x;
	private int y;
	private GameModel game;
	private String text;
	private boolean selected;
	private ButtonType type;
	
	public Button(int x, int y, int width, int height, ButtonType type, boolean selected, GameModel game){
		this.width = width;
		this.x = x;
		this.y = y;
		this.height = height;
		this.type = type;
		this.text = type.toString();
		this.selected = selected;
		this.game = game;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean IsSelected() {
		return selected;
	}

	public boolean isHit(int x, int y){
		if((x >= this.x && x <= this.x+width) && (y >= this.y && y <= this.y+height)){
			return true;
		}
		return false;
	}
	
	public void setSelected(boolean sel){
		selected = sel;
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

	public void doAction() {
		if(type == ButtonType.CLOSE){
			game.changeState(GameState.Playing);
		}
		else if(type == ButtonType.QUIT){
			System.exit(0);
		}
		else if(type == ButtonType.RESTART){
			game.getNinja().reset();
			game.getBoss().reset();
		}
	}

}
