package scrollthief.model;

public class LevelFactory {
	public LevelFactory() {
		
	}
	
	public Level getLevel(LevelState levelState, Resource resource, GameModel gameModel) {
		if(levelState == LevelState.Level1)
			return new Level1(resource, gameModel);
		
		return null;
	}
}
