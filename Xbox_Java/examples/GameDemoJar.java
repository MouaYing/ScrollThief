// GameDemo.java, JAR Version
// Example how to use the Xbox360 controller with animated graphics
// scene.gif and piranha.gif must reside in class directory

import ch.aplu.util.*;
import ch.aplu.xboxcontroller.*;
import java.awt.Color;
import java.net.URL;

public class GameDemoJar implements ExitListener
{
  private GPanel p = new GPanel("Connecting...", -100, 100, -100, 100);
  private double mag = 0;
  private double dir = 0;
  private JRunner jRunner = new JRunner(this);
  private String connectInfo = "GameDemo V1.0 (www.aplu.ch) " +
    "-- Move: LeftThumb; Breeze: Press B";
  private ClassLoader loader = getClass().getClassLoader();
  private URL fish;

  public GameDemoJar()
  {
    XboxController xc = new XboxController();
    if (!xc.isConnected())
      p.title("Xbox controller not connected");
    else
      p.title(connectInfo);
    xc.setLeftThumbDeadZone(0.2);
    p.addExitListener(this);
    p.resizable(false);
    URL scene = loader.getResource("scene.gif");
    p.image(scene, -100, -100);  // Draw background scene
    fish = loader.getResource("piranha.gif");
    p.storeGraphics();
    p.enableRepaint(false);
    p.color(Color.white);
    showFish();

    // Register callbacks
    xc.addXboxControllerListener(new XboxControllerAdapter()
    {
      public void leftThumbMagnitude(double magnitude)
      {
        mag = magnitude;
        showFish();
      }

      public void leftThumbDirection(double direction)
      {
        dir = direction / 180.0 * Math.PI;
        showFish();
      }

      public void buttonB(boolean pressed)
      {
        if (pressed)
          jRunner.run("breeze"); // Run breeze() in a new thread
      }

      public void isConnected(boolean connected)
      {
        if (connected)
          p.title(connectInfo);
        else
          p.title("Connection lost");
      }
    });

    // Main thread will sleep until termination
    Monitor.putSleep();
    xc.release();
    System.exit(0);
  }

  private void showFish()
  {
    p.recallGraphics();  // Restore scene
    double x = 100 * mag * Math.sin(dir);
    double y = 100 * mag * Math.cos(dir);
    p.image(fish, x - 45, y - 15);
    p.repaint();
  }

  private void breeze()
  {
    double xcenter = 100 * mag * Math.sin(dir);
    double ycenter = 100 * mag * Math.cos(dir);
    HiResAlarmTimer timer = new HiResAlarmTimer(50000);
    for (int r = 0; r < 200; r += 5)
    {
      timer.start();
      drawBubbles(xcenter, ycenter, r);
      while (timer.isRunning())
        Thread.yield();
      showFish();
    }
  }

  private void drawBubbles(double xcenter, double ycenter, double radius)
  {
    double phi;
    double x,  y;
    for (int angle = 70; angle <= 110; angle += 2)
    {
      phi = angle / 180.0 * Math.PI;
      x = xcenter + radius * Math.cos(phi);
      y = ycenter + radius * Math.sin(phi);
      if (x > -100 && x < 100 && y > -100 && y < 100)
      {
        p.move(x, y);
        p.fillCircle(1);
      }
    }
    p.repaint();
  }

  public void notifyExit()
  {
    Monitor.wakeUp();
  }

  public static void main(String[] args)
  {
    new GameDemo();
  }
}
