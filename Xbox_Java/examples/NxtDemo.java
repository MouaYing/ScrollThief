// NxtDemo.java
// Steer Lego NXT robot with Xbox Controller
// Uses NxtJLib and Bluecove package, see www.aplu.ch/nxtjlib

import ch.aplu.nxt.*;
import ch.aplu.xboxcontroller.*;
import javax.swing.JOptionPane;

public class NxtDemo
{
  private class MyXboxControllerAdapter extends XboxControllerAdapter
  {
    public void start(boolean pressed)
    {
      advance = true;
    }

    public void back(boolean pressed)
    {
      advance = false;
    }
    
    public void leftTrigger(double value)
    {
      motA.setSpeed((int)(100 * value));
      if (advance)
        motA.forward();
      else
        motA.backward();
    }

    public void rightTrigger(double value)
    {
      motB.setSpeed((int)(100 * value));
      if (advance)
        motB.forward();
      else
        motB.backward();
    }
  }

  private Motor motA = new Motor(MotorPort.A);
  private Motor motB = new Motor(MotorPort.B);
  private boolean advance = true;

  public NxtDemo()
  {
    XboxController xc = new XboxController();
    if (!xc.isConnected())
    {
      JOptionPane.showMessageDialog(null,
        "Xbox controller not connected.",
        "Fatal error", JOptionPane.ERROR_MESSAGE);
      xc.release();
      return;
    }
    xc.addXboxControllerListener(new MyXboxControllerAdapter());
    NxtRobot robot = new NxtRobot();
    robot.addPart(motA);
    robot.addPart(motB);
    JOptionPane.showMessageDialog(
      null, 
      "Ready to go." +
      "\nLeft or right trigger to speed motors." +
      "\nSTART to drive forward, BACK to drive backward." +
      "\nOk to quit.",
      "NxtDemo V1.0 (www.aplu.ch)", 
      JOptionPane.PLAIN_MESSAGE);
    xc.release();
    robot.exit();
  }

  public static void main(String[] args)
  {
    new NxtDemo();
  }
}
