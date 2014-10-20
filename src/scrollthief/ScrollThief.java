/**
 *  @author Jon "Neo" Pimentel
 */
package scrollthief;

import scrollthief.controller.Controller;
import scrollthief.model.GameModel;
import scrollthief.view.View;

public class ScrollThief {

	public static void main(String[] args) {
		GameModel gameModel= new GameModel();
		View view= new View(gameModel);
		Controller controller= new Controller(view, gameModel);
		

	}

}
