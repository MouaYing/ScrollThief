package scrollthief.model;

public class Button {
	
	private int width;
	private int height;
	private int x;
	private int y;
	private int baseX;
	private int baseY;
	private GameModel game;
	private String text;
	private boolean selected;
	private ButtonType type;
	
	public Button(int x, int y, int width, int height, ButtonType type, boolean selected, GameModel game){
		this.width = width;
		this.baseX = x;
		this.baseY = y;
		this.x = x;
		this.y = y;
		this.height = height;
		this.type = type;
		this.text = type.toString();
		if(type == ButtonType.MAINMENU){
			this.text = "MAIN MENU";
		}
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
	
	public void setOffset(int offsetX, int offsetY){
		this.x = offsetX + baseX;
		this.y = offsetY + baseY;
	}
	

	public void doAction() {
		if(type == ButtonType.RESUME){
			game.changeState(GameState.Playing);
			game.getResource().getSound().resumeMusic();
		}
		else if(type == ButtonType.QUIT){
			System.exit(0);
		}
		else if(type == ButtonType.RESTART){
			game.resetLevel();
			game.changeState(GameState.Playing);
			game.getResource().getSound().setShouldPlayMusic(true);
			game.getResource().getSound().playMusic(SoundFile.SNEAK);
		}
		else if(type == ButtonType.START){
			game.changeState(GameState.LevelLoading);
		}
		else if(type == ButtonType.CONTINUE){
			game.continueGame();
		}
		else if(type == ButtonType.SAVE){
			game.saveGame();
		}
		else if(type == ButtonType.MAINMENU){
			game.resetLevel();
			game.resetLevelLoading();
			game.changeState(GameState.MainMenu);
			game.getResource().getSound().setShouldPlayMusic(true);
			game.getResource().getSound().playMusic(SoundFile.TITLE);
		}
	}

}
