/**
 *  Javadoc for the Aplu Xbox Controller Library can be found here:
 *  http://www.aplu.ch/classdoc/xbox/ch/aplu/xboxcontroller/package-summary.html
 *  
 *  Further information is available here: http://www.aplu.ch/home/apluhomex.jsp?site=36
 */
package scrollthief.controller;

import ch.aplu.xboxcontroller.XboxControllerAdapter;

public class XboxAdapter extends XboxControllerAdapter{
	Controller controller;
	
	public XboxAdapter(Controller controller){
		this.controller= controller;
	}
	
	
	public void isConnected(boolean connected){
		if (connected)
			System.out.println("Controller Connected!");
		else System.out.println("Controller not Connected!");
	}

}
