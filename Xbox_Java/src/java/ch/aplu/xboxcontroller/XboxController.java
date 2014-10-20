// XboxController.java

/*
This software is part of the Aplu XboxController package.
It is Open Source Free Software, so you may
- run the code for any purpose
- study how the code works and adapt it to your needs
- integrate all or parts of the code in your own programs
- redistribute copies of the code
- improve the code and release your improvements to the public
However the use of the code is entirely your responsibility.
 */
package ch.aplu.xboxcontroller;

import ch.aplu.jaw.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Class to handle the Microsoft Xbox Game Controller (wired or wireless) in a fully event triggered manner.
 * A native high priority thread polls the state of the controller and feeds an FIFO event queue.
 * This queue is polled by a Java thread that generates callback notifications whenever the state changes.
 * Both polling threads are fully hidden to the user of the package who get callback notifications whenever
 * the state of the controller changes.<br><br>
 * The native code executes in a Windows DLL created with the JAW (Java API Wrapper) framework.
 */
public class XboxController
{
  private static final String version = "1.06";
  private boolean is64bit;
  private NativeHandler nh;
  private Timer pollTimer = null;
  private boolean isDisconnected = true;
  private short[] values = new short[9];
  private int queuePollPeriod; // Polling period of the Java thread
  private boolean debug = false;
  private int controllerPollPeriod;  // Polling period of native thread in ms
  private XboxControllerListener listener;
  private long buttonState = 0;
  private int dpadState = -1;
  private int dpadDirection;
  private int controllerNum;
  private int leftVal;
  private int rightVal;
  private double oldLeftTriggerValue = -1;
  private double oldRightTriggerValue = -1;
  private double oldLeftMagnitude = -1;
  private double oldRightMagnitude = -1;
  private double oldLeftDir = -1;
  private double oldRightDir = -1;
  private short leftTriggerDeadZone = 0;
  private short rightTriggerDeadZone = 0;
  private short leftThumbDeadZone = 0;
  private short rightThumbDeadZone = 0;

  /**
   * Same as XboxController(dll, 1, 50, 50).
   * The DLL xboxcontroller.dll or xboxcontroller64.dll is searched in 
   * the Windows path (depending on the OS version).
   */
  public XboxController()
  {
    if (is64bit())
      init("xboxcontroller64", 1, 50, 50);
    else
      init("xboxcontroller", 1, 50, 50);
  }

  /**
   * Same as XboxController(dll, 1, controllerPollPeriod, queuePollPeriod).
   * The DLL xboxcontroller.dll or xboxcontroller64.dll is searched in 
   * the Windows path (depending on the OS version).
   */
  public XboxController(int controllerPollPeriod, int queuePollPeriod)
  {
    if (is64bit())
      init("xboxcontroller64", 1, controllerPollPeriod, queuePollPeriod);
    else
      init("xboxcontroller", 1, controllerPollPeriod, queuePollPeriod);
  }

  /**
   * Same as XboxController(dll, int playerNb, 50, 50).
   * The DLL xboxcontroller.dll or xboxcontroller64.dll is searched in 
   * the Windows path (depending on the OS version).
   */
  public XboxController(int playerNb)
  {
    if (is64bit())
      init("xboxcontroller64", playerNb, 50, 50);
    else
      init("xboxcontroller", playerNb, 50, 50);
  }

  /**
   * Creates a XboxController instance using the given native Windows DLL
   * and tries to establish a connection to the controller with the given 
   * player number.
   * @param dll the fully qualified path to the native DLL.
   * If the extension .DLL is missing, the DLL is searched in the Windows path.
   * @param playerNb the number of the player 1..4
   * @param controllerPollPeriod the period (in ms) of the native timer that polls the state of the controller
   * @param queuePollPeriod the period (in ms) of the Java timer that polls the message queue
   */
  public XboxController(String dll, int playerNb, int controllerPollPeriod, int queuePollPeriod)
  {
    init(dll, playerNb, controllerPollPeriod, queuePollPeriod);
  }
  
  private void init(String dll, int playerNb, int controllerPollPeriod, int queuePollPeriod)
  {
    System.out.println("XboxController V" + version + " (www.aplu.ch)");
    System.out.println("Current JVM: "  + System.getProperty("sun.arch.data.model") + " bit");
    this.controllerPollPeriod = controllerPollPeriod;
    this.queuePollPeriod = queuePollPeriod;
    if (playerNb < 1 || playerNb > 4)
    {
      JOptionPane.showMessageDialog(null, "Xboxcontroller number must be 1..4",
        "Fatal error while creating Xboxcontroller", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    controllerNum = playerNb - 1;
    nh = new NativeHandler(dll, this);
    nh.createBuf(values);  // JNIBuffer with default bufSize

    nh.startThread();      // Start high priority thread

    while (nh.invoke(0) == 0)  // Wait until thread is up
      delay(50);

    if (nh.invoke(1) == 1)
      isDisconnected = false;
  }


  /**
   * Release native resources. 
   * Should be called before terminating the application.
   */
  public void release()
  {
    isDisconnected = true;
    if (pollTimer != null)
      pollTimer.stop();
    nh.destroy();
  }

  /**
   * Sets the dead zone for LeftTrigger. Report 0 if
   * the measured magnitude is lower than the dead zone value.
   * @param zone the zone with no notifications, no change if < 0 or > 1
   */
  public void setLeftTriggerDeadZone(double zone)
  {
    if (zone >= 0 && zone <= 1)
      leftTriggerDeadZone = (short)Math.round(255 * zone);
  }

  /**
   * Sets the dead zone for RightTrigger. Report 0 if
   * the measured value is lower than the dead zone value.
   * @param zone the zone with no notifications, no change if < 0 or > 1
   */
  public void setRightTriggerDeadZone(double zone)
  {
    if (zone >= 0 && zone <= 1)
      rightTriggerDeadZone = (short)Math.round(255 * zone);
  }

  /**
   * Sets the dead zone for the left thumb. Report magnitude 0 if
   * the measured magnitude is lower than the dead zone value.
   * @param zone the zone to report magnitude 0
   */
  public void setLeftThumbDeadZone(double zone)
  {
    if (zone >= 0 && zone <= 1)
      leftThumbDeadZone = (short)Math.round(32767 * zone);
  }

  /**
   * Sets the dead zone for the right thumb. Report magnitude 0 if
   * the measured magnitude is lower than the dead zone value.
   * @param zone the zone to report magnitude 0
   */
  public void setRightThumbDeadZone(double zone)
  {
    if (zone >= 0 && zone <= 1)
      rightThumbDeadZone = (short)Math.round(32767 * zone);
  }

  /**
   * Starts vibration with given left und right intensity.
   * Returns immediately. Set left and right intensity to 0 to
   * stop vibration.
   * @param leftVal left intensity 0..65535
   * @param rightVal right intensity 0..65535
   */
  public void vibrate(int leftVal, int rightVal)
  {
    vibrate(leftVal, rightVal, 0);
  }

  /**
   * Vibrates with given left und right intensity for the given
   * duration. Returns when vibration stops.
   * @param leftVal left intensity 0..65535
   * @param rightVal right intensity 0..65535
   * @param duration rightVal vibration time in ms
   */
  public synchronized void vibrate(int leftVal, int rightVal, int duration)
  {
    if (isDisconnected)
      return;
    if (leftVal < 0 || leftVal > 65535)
    {
      JOptionPane.showMessageDialog(null, "left value must be 0..65535",
        "Fatal error while calling XboxController.vibrate", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    if (rightVal < 0 || rightVal > 65535)
    {
      JOptionPane.showMessageDialog(null, "right value must be 0..65535",
        "Fatal error while calling XboxController.vibrate", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    this.leftVal = leftVal;
    this.rightVal = rightVal;
    nh.invoke(2);
    if (duration > 0)
    {
      delay(duration);
      this.leftVal = 0;
      this.rightVal = 0;
      nh.invoke(2);
    }
  }

  /**
   * Returns true if the last native polling call to the controller was successful.
   * @return true, if the controller responded, otherwise false
   */
  public synchronized boolean isConnected()
  {
    if (isDisconnected)
      return false;
    return nh.invoke(1) == 1 ? true : false;
  }

  private int countBuf()
  {
    return nh.countBuf() / 9;
  }

  private synchronized ControllerState readData()
  {
    if (countBuf() <= 0)
      return null;
    nh.readBuf(9);
    ControllerState cs = new ControllerState();
    cs.buttons = values[0];
    cs.leftTrigger = values[1];
    cs.rightTrigger = values[2];
    cs.thumbLX = values[3];
    cs.thumbLY = values[4];
    cs.thumbRX = values[5];
    cs.thumbRY = values[6];
    cs.success = values[7];
    cs.source = values[8];
    if (debug)
      System.out.println("Got at " + nh.invoke(4) + ": " +
        values[0] + ", " +
        values[1] + ", " +
        values[2] + ", " +
        values[3] + ", " +
        values[4] + ", " +
        values[5] + ", " +
        values[6] + ", " +
        values[7] + ", " +
        values[8]);
    return cs;
  }

  /**
   * Registers the given XboxControllerListener and starts polling the event queue.
   * If a listener is already registered, stops its notifications. If null is given,
   * stops the  notification of an already registerd listener.
   * @param xboxControllerListener the XboxControllerListener to register
   */
  public void addXboxControllerListener(XboxControllerListener xboxControllerListener)
  {
    if (pollTimer != null)
      pollTimer.stop();
    if (xboxControllerListener == null)
      return;

    listener = xboxControllerListener;
    final DigitalCallback[] dfunc =
    {
      new DigitalCallback.F0(listener),
      new DigitalCallback.F1(listener),
      new DigitalCallback.F2(listener),
      new DigitalCallback.F3(listener),
      new DigitalCallback.F4(listener),
      new DigitalCallback.F5(listener),
      new DigitalCallback.F6(listener),
      new DigitalCallback.F7(listener),
      new DigitalCallback.F8(listener),
      new DigitalCallback.F9(listener),
    };

    ActionListener pollPerformer = new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        ControllerState cs = readData();
        if (cs != null)
        {
//          cs.write();
          if ((cs.source & ControllerState.SOURCE_BUTTONS) != 0)
          {
            int buttons = cs.buttons & 0x0000FFFF;
            int id;

            if ((id = (buttons & 0x000F)) != 0)  // dpad
            {
              if (id != dpadState)
              {
                if (dpadState != -1)
                  listener.dpad(dpadDirection, false);  // Release

                for (int i = 0; i < 8; i++)
                  if (id == ControllerState.digitalId[i])
                  {
                    listener.dpad(i, true);
                    dpadDirection = i;
                    dpadState = id;
                  }
              }
            }
            else
              if (dpadState != -1)
              {
                listener.dpad(dpadDirection, false);
                dpadState = -1;
              }

            int bitValue = 1;
            for (int i = 8; i < 18; i++)
            {
              if ((buttons & ControllerState.digitalId[i]) != 0)
              {
                if ((buttonState & bitValue) == 0)
                {
                  dfunc[i - 8].invoke(true);
                  buttonState += bitValue;
                }
              }
              else
                if ((buttonState & bitValue) != 0)
                {
                  dfunc[i - 8].invoke(false);
                  buttonState -= bitValue;
                }
              bitValue *= 2;
            }
          }

          if ((cs.source & ControllerState.SOURCE_SUCCESS) != 0)
          {
            nh.flushBuf();
            listener.isConnected((cs.success == 1) ? true : false);
          }
          else
          {
            if ((cs.source & ControllerState.SOURCE_LEFTTRIGGER) != 0)
            {
              short value = cs.leftTrigger;
              if (value > leftTriggerDeadZone)
              {
                value -= leftTriggerDeadZone;
                if (value != oldLeftTriggerValue)
                {
                  listener.leftTrigger(value / (255.0 - leftTriggerDeadZone));
                  oldLeftTriggerValue = value;
                }
              }
              else
              {
                value = 0;
                if (value != oldLeftTriggerValue)
                {
                  listener.leftTrigger(value);
                  oldLeftTriggerValue = 0;
                }
              }
            }

            if ((cs.source & ControllerState.SOURCE_RIGHTTRIGGER) != 0)
            {
              short value = cs.rightTrigger;
              if (value > rightTriggerDeadZone)
              {
                value -= rightTriggerDeadZone;
                if (value != oldRightTriggerValue)
                {
                  listener.rightTrigger(value / (255.0 - rightTriggerDeadZone));
                  oldRightTriggerValue = value;
                }
              }
              else
              {
                value = 0;
                if (value != oldRightTriggerValue)
                {
                  listener.rightTrigger(value);
                  oldRightTriggerValue = 0;
                }
              }
            }

            if (((cs.source & ControllerState.SOURCE_THUMBLX) != 0) ||
              ((cs.source & ControllerState.SOURCE_THUMBLY) != 0))
            {
              double magnitude = Math.sqrt(cs.thumbLX * cs.thumbLX + cs.thumbLY * cs.thumbLY);
              double dir = Math.atan2(cs.thumbLX, cs.thumbLY) / Math.PI * 180;
              if (dir < 0)
                dir = 360 + dir;
              if (magnitude > 32767)
                magnitude = 32767;
              if (magnitude > leftThumbDeadZone)
              {
                magnitude -= leftThumbDeadZone;
                if (magnitude != oldLeftMagnitude)
                {
                  listener.leftThumbMagnitude(magnitude / (32767.0 - leftThumbDeadZone));
                  oldLeftMagnitude = magnitude;
                }
                if (dir != oldLeftDir)
                {
                  listener.leftThumbDirection(dir);
                  oldLeftDir = dir;
                }
              }
              else
              {
                magnitude = 0;
                if (magnitude != oldLeftMagnitude)
                {
                  listener.leftThumbMagnitude(magnitude);
                  oldLeftMagnitude = 0;
                }
              }
            }

            if (((cs.source & ControllerState.SOURCE_THUMBRX) != 0) ||
              ((cs.source & ControllerState.SOURCE_THUMBRY) != 0))
            {
              double magnitude = Math.sqrt(cs.thumbRX * cs.thumbRX + cs.thumbRY * cs.thumbRY);
              double dir = Math.atan2(cs.thumbRX, cs.thumbRY) / Math.PI * 180;
              if (dir < 0)
                dir = 360 + dir;
              if (magnitude > 32767)
                magnitude = 32767;
              if (magnitude > rightThumbDeadZone)
              {
                magnitude -= rightThumbDeadZone;
                if (magnitude != oldRightMagnitude)
                {
                  listener.rightThumbMagnitude(magnitude / (32767.0 - rightThumbDeadZone));
                  oldRightMagnitude = magnitude;
                }
                if (dir != oldRightDir)
                {
                  listener.rightThumbDirection(dir);
                  oldRightDir = dir;
                }
              }
              else
              {
                magnitude = 0;
                if (magnitude != oldRightMagnitude)
                {
                  listener.rightThumbMagnitude(magnitude);
                  oldRightMagnitude = 0;
                }
              }
            }
          }
        }
      }
    };

    nh.flushBuf();
    pollTimer = new Timer(queuePollPeriod, pollPerformer);
    pollTimer.start();
  }

  /**
   * Returns version information.
   * @return the version information
   */
    public static String getVersion()
  {
    return version;
  }

  /**
   * Displays debug information when the native thread sends data to the
   * FIFO queue and when the Java thread receives it.
   * @param on if true, debug information is displayed at System.out
   */
  public void setDebug(boolean on)
  {
    if (on)
      System.out.println("\nDebug on. Format:\n" +
        "npcc, buttons, leftTrigger, rightTrigger, " +
        "thumbLX, thumbLY, thumbRX, thumbRY, success, source\n" +
        "npcc: native poll clock count");
    else
      System.out.println("\nDebug off");
    debug = on;
    nh.invoke(3);
  }

  private void delay(int duration)
  {
    try
    {
      Thread.currentThread().sleep(duration);
    }
    catch (InterruptedException ex)
    {
    }
  }

  private boolean is64bit()
  {
    return System.getProperty("sun.arch.data.model").equals("64");
  }
}
 
