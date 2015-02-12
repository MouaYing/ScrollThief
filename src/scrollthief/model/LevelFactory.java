package scrollthief.model;

public class LevelFactory {
	private LevelState levelState;
	
	public LevelFactory() {
		levelState = LevelState.Level0;
	}
	
	public Level getNextLevel(Resource resource, GameModel gameModel) {
		if(levelState == LevelState.Level0)
			return new Level1(resource, gameModel);
//		if(levelState == LevelState.Level1)
//			return new Level2(resource, gameModel);
//		...
		
		return null;
	}
	
	public LevelState getLevelState() {
		return levelState;
	}
}
