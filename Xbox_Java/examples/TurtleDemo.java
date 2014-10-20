// TurtleDemo.java

import ch.aplu.turtle.*;
import ch.aplu.xboxcontroller.*;
import java.awt.event.*;

public class TurtleDemo
{
  private class MyXboxControllerAdapter extends XboxControllerAdapter
  {
    public void leftThumbMagnitude(double magnitude)
    {
      speed = (int)(100 * magnitude);
      t.speed(speed);
    }

    public void leftThumbDirection(double direction)
    {
      t.heading(direction);
    }

    public void start(boolean pressed)
    {
      if (pressed)
      {
        t.clear();
        t.home();
      }
    }
    
    public void isConnected(boolean connected)
    {
      if (connected)
       t.setTitle(connectInfo);
      else  
        t.setTitle("Connection lost");
    }
  }
  
  private final String 
    connectInfo = "Connected. 'Left thumb' to move, 'Start' to clear";
  private Turtle t = new Turtle();
  private volatile boolean isExiting = false;
  private int speed = 0;

  public TurtleDemo()
  {
    t.setTitle("Connecting...");
    XboxController xc = new XboxController();
    t.wrap();
    if (!xc.isConnected())
      t.setTitle("Xbox controller not connected");
    else
      t.setTitle(connectInfo);
    xc.addXboxControllerListener(new MyXboxControllerAdapter());
    xc.setLeftThumbDeadZone(0.2);
    t.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        isExiting = true;
      }
    });
   
    while (!isExiting)
    {
      if (speed > 10)
        t.forward(10);
      else
        Turtle.sleep(10);
    }
    xc.release();
    System.exit(0);
  }

  public static void main(String[] args)
  {
    new TurtleDemo();
  }
}
