// ControllerState.java

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

class ControllerState
{
  public short buttons;
  public short leftTrigger;
  public short rightTrigger;
  public short thumbLX;
  public short thumbLY;
  public short thumbRX;
  public short thumbRY;
  public short success;
  public short source;
  
  public final static short SOURCE_BUTTONS = 0x01;
  public final static short SOURCE_LEFTTRIGGER = 0x02;
  public final static short SOURCE_RIGHTTRIGGER = 0x04;
  public final static short SOURCE_THUMBLX = 0x08;
  public final static short SOURCE_THUMBLY = 0x10;
  public final static short SOURCE_THUMBRX = 0x20;
  public final static short SOURCE_THUMBRY = 0x40;
  public final static short SOURCE_SUCCESS = 0x80;

  public final static int DPAD_NORTH = 0x0001;
  public final static int DPAD_SOUTH = 0x0002;
  public final static int DPAD_WEST = 0x0004;
  public final static int DPAD_EAST = 0x0008;
  public final static int DPAD_NORTHEAST = DPAD_NORTH | DPAD_EAST;
  public final static int DPAD_SOUTHEAST = DPAD_SOUTH | DPAD_EAST;
  public final static int DPAD_SOUTHWEST = DPAD_SOUTH | DPAD_WEST;
  public final static int DPAD_NORTHWEST = DPAD_NORTH | DPAD_WEST;
  public final static int START = 0x0010;
  public final static int BACK = 0x0020;
  public final static int LEFT_THUMB = 0x0040;
  public final static int RIGHT_THUMB = 0x0080;
  public final static int LEFT_SHOULDER = 0x0100;
  public final static int RIGHT_SHOULDER = 0x0200;
  public final static int BUTTON_A = 0x1000;
  public final static int BUTTON_B = 0x2000;
  public final static int BUTTON_X = 0x4000;
  public final static int BUTTON_Y = 0x8000;
  
  public static int[] digitalId = {DPAD_NORTH, DPAD_NORTHEAST, 
                                   DPAD_EAST, DPAD_SOUTHEAST,
                                   DPAD_SOUTH, DPAD_SOUTHWEST, 
                                   DPAD_WEST, DPAD_NORTHWEST,
                                   START, BACK, LEFT_THUMB, RIGHT_THUMB, 
                                   LEFT_SHOULDER, RIGHT_SHOULDER, 
                                   BUTTON_A, BUTTON_B, BUTTON_X, BUTTON_Y};

  public void write()
  {
    System.out.println("buttons: " + buttons);
    System.out.println("leftTrigger: " + leftTrigger);
    System.out.println("rightTrigger: " + rightTrigger);
    System.out.println("thumbLX: " + thumbLX);
    System.out.println("thumbLY: " + thumbLY);
    System.out.println("thumbRX: " + thumbRX);
    System.out.println("thumbRY: " + thumbRY);
    System.out.println("success: " + success);
    System.out.println("source: " + source);
  }
}
