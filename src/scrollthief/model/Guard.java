/**
 * This class is meant to represent a guard, and hold information like its field of vision and blind spots
 * This class may be unnecessary if we calculate all of the guard information every frame and don't really 
 * need to store it.
 */

package scrollthief.model;

public class Guard extends Character{

	public Guard(Model model){
		super(model);
	}
}
