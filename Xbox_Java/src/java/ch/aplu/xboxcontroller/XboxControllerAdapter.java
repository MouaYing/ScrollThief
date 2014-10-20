// XboxControllerAdapter.java

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

/**
 * Class implementing XboxControllerListener with empty callback methods.
 */
public class XboxControllerAdapter implements XboxControllerListener
{
  // Digital buttons

  /**
   * Notification when button A is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void buttonA(boolean pressed)
  {
  }

  /**
   * Notification when button B is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void buttonB(boolean pressed)
  {
  }

  /**
   * Notification when button X is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void buttonX(boolean pressed)
  {
  }

  /**
   * Notification when button Y is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void buttonY(boolean pressed)
  {
  }

  /**
   * Notification when button BACK is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void back(boolean pressed)
  {
  }

  /**
   * Notification when button START is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void start(boolean pressed)
  {
  }

  /**
   * Notification when button LEFTSHOULDER is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void leftShoulder(boolean pressed)
  {
  }

  /**
   * Notification when button RIGHTSHOULDER is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void rightShoulder(boolean pressed)
  {
  }

  /**
   * Notification when button LEFTTHUMB is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void leftThumb(boolean pressed)
  {
  }

  /**
   * Notification when button RIGHTTHUMB is pressed or released.
   * @param pressed true, when button is pressed; false, when released
   */
  public void rightThumb(boolean pressed)
  {
  }

  /**
   * Notification when the direction pad is actuated. This pad has 8 
   * possible positions returned by the direction parameter: 0->NORTH, 
   * 1->NORTHEAST, 2->EAST, 3->SOUTHEAST, 4->SOUTH, 5->SOUTHWEST,
   * 6->WEST, 7->NORTHWEST 
   * @param direction one of the values 0..7
   * @param pressed true, when button is pressed; false, when released
   */
  public void dpad(int direction, boolean pressed)
  {
  }

  // Analog buttons
  /**
   * Notification when the LEFTTRIGGER value changes.
   * @param value the new value -1..0..1
   */
  public void leftTrigger(double value)
  {
  }

  /**
   * Notification when the RIGHTTRIGGER value changes.
   * @param value the new value -1..0..1
   */
  public void rightTrigger(double value)
  {
  }

  /**
   * Notification when the magnitude of the left thumb changes.
   * @param magnitude the new magnitude 0..1
   */
  public void leftThumbMagnitude(double magnitude)
  {
  }

  /**
   * Notification when the direction of the left thumb changes.
   * @param direction the new direction 0..360 degrees (clockwise, 0 to north)
   */
  public void leftThumbDirection(double direction)
  {
  }

  /**
   * Notification when the magnitude of the rigth thumb changes.
   * @param magnitude the new magnitude 0..1
   */
  public void rightThumbMagnitude(double magnitude)
  {
  }

  /**
   * Notification when the direction of the right thumb changes.
   * @param direction the new direction 0..360 degrees (clockwise, 0 to north
   *
   */
  public void rightThumbDirection(double direction)
  {
  }

  /**
   * Notification when the connection to the controller is lost or reestablished.
   * @param connected true, if the connection is reestablished; false, if lost
   */
  public void isConnected(boolean connected)
  {
  }
}
