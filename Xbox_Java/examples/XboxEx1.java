// XboxEx1.java

import ch.aplu.jgamegrid.*;
import ch.aplu.xboxcontroller.*;

public class XboxEx1 extends GameGrid
{
  private double mag = 0;
  private double dir = 0;
  private String connectInfo = "XboxEx1 " +
    "-- Move: LeftThumb, LeftTrigger -- Press: X";
  private Actor nozzle = new Actor(true, "sprites/nozzle.gif", 2);

  public XboxEx1()
  {
    super(500, 500, 1, null, "sprites/snowwindow.gif", false);
    addActor(nozzle, new Location(250, 250));
    show();
    XboxController xc = new XboxController();
    if (!xc.isConnected())
      setTitle("Xbox controller not connected");
    else
      setTitle(connectInfo);
    xc.setLeftThumbDeadZone(0.1);

    // Register callbacks
    xc.addXboxControllerListener(new XboxControllerAdapter()
    {
      public void leftThumbMagnitude(double magnitude)
      {
        mag = magnitude;
        int x = (int)(250 + 200 * toX(mag, dir));
        int y = (int)(250 - 200 * toY(mag, dir));
        nozzle.setLocation(new Location(x, y));
        refresh();
      }

      public void leftThumbDirection(double direction)
      {
        dir = direction;
        int x = (int)(250 + 200 * toX(mag, dir));
        int y = (int)(250 - 200 * toY(mag, dir));
        nozzle.setLocation(new Location(x, y));
        refresh();
      }

      public void leftTrigger(double value)
      {
        nozzle.setDirection(360 * value);
        refresh();
      }

      public void buttonX(boolean pressed)
      {
        if (pressed)
        {
          nozzle.show(1);
          addActor(new Spider(), nozzle.getLocation(), nozzle.getDirection());
        }
        else
          nozzle.show(0);
      }


      public void isConnected(boolean connected)
      {
        if (connected)
          setTitle(connectInfo);
        else
          setTitle("Connection lost");
      }
    });

    doRun();
  }

  private double toX(double r, double phi)
  {
    return r * Math.sin(phi / 180 * Math.PI);
  }

  private double toY(double r, double phi)
  {
    return r * Math.cos(phi / 180 * Math.PI);
  }

  public static void main(String[] args)
  {
    new XboxEx1();
  }
}
