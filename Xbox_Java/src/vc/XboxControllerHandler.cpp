// XboxControllerHandler.cpp

#include "XboxControllerHandler.h"
#include "iostream"

using namespace std;

#define maxInt 2147483647

// ----------------- Selector -------------- ---------------
WindowHandler *
  selectWindowHandler(JNIEnv * env, jobject obj, int select)
{
  return new XboxControllerHandler(env, obj);
}

// ----------------- class XboxControllerHandler ------------------
XboxControllerHandler::XboxControllerHandler(JNIEnv * env, jobject obj)
    : WindowHandler(env, obj), isReady(false), doVibrate(false), debug(false), clock(0)
{
#ifdef XBOXCONTROLLER64
  cout << "Loading xboxcontroller64.dll (64-bit version)" << endl;
#else
  cout << "Loading xboxcontroller.dll (32-bit version)" << endl;
#endif

  data = new short[9];
  newData = new short[9];
  data[7] = -1; // Indicates "not valid" 
  controllerNum = JNIVar<int>(_exposedObj, "controllerNum").get();
  period = JNIVar<int>(_exposedObj, "controllerPollPeriod").get();
}

XboxControllerHandler::~XboxControllerHandler()
{
  delete [] data;
  delete [] newData;
}

bool XboxControllerHandler::wantThreadMsg(UINT uMsg)
{
  return (uMsg == WM_THREADSTART || 
                  WM_TIMER ||
                  WM_THREADSTOP);
 }

void XboxControllerHandler::evThreadMsg(HWND /*hwnd*/, 
                           UINT message, 
                           WPARAM /* wParam */, 
                           LPARAM /* lParam */)
// Runs in JNIThread and not in the normal application thread
{
  #define ID_TIMER 1

  switch (message)
  {
    case WM_THREADSTART:
      SetTimer(0, ID_TIMER, period, (TIMERPROC)NULL);
      break;

    case WM_TIMER:
      if (debug)
      {
        clock++;
        if (clock == maxInt)
          clock = 0;
      }
      if (doVibrate)
      {
        doVibrate = false;
        vibrate();
        break;
      }
      getState();
      if (isDataChanged())
        writeBuf();

      if (!isReady)
        isReady = true;
      break;
    
    case WM_THREADSTOP:
      KillTimer(0, ID_TIMER);
      break;
  }
}

int XboxControllerHandler::invoke(int val)
{
  switch (val)
  {
    case 0:  // check if ready
      return isReady ? 1 : 0;

    case 1:  // check if connected
      return data[7];

    case 2: // vibrate
      leftVal = JNIVar<int>(_exposedObj, "leftVal").get();
      rightVal = JNIVar<int>(_exposedObj, "rightVal").get();
      doVibrate = true;
      return 0;

    case 3: // debug
      debug = JNIVar<bool>(_exposedObj, "debug").get();
      clock = 0;
      return 0;

    case 4:
      return clock;
  }
  return 0;
}

void XboxControllerHandler::getState()
{
  XINPUT_STATE controllerState;

  // Zeroise the state
  ZeroMemory(&controllerState, sizeof(XINPUT_STATE));

  // Get the state
  DWORD result = XInputGetState(controllerNum, &controllerState);

  // Copy result
  newData[0] = (short)controllerState.Gamepad.wButtons;
  newData[1] = (short)controllerState.Gamepad.bLeftTrigger;
  newData[2] = (short)controllerState.Gamepad.bRightTrigger;
  newData[3] = (short)controllerState.Gamepad.sThumbLX;
  newData[4] = (short)controllerState.Gamepad.sThumbLY;
  newData[5] = (short)controllerState.Gamepad.sThumbRX;
  newData[6] = (short)controllerState.Gamepad.sThumbRY;
  if(result == ERROR_SUCCESS)
    newData[7] = 1;
  else
    newData[7] = 0;
  newData[8] = 0;
}

bool XboxControllerHandler::isDataChanged()
{
  bool isChanged = false;
  short mask = 0;
  short currentBit = 1;
  for (int i = 0; i < 8; i++)
  {
    if (data[i] != newData[i])
    {
      isChanged = true;
      data[i] = newData[i];
      mask += currentBit;
    }
    currentBit *= 2;
  }
  data[8] = mask;
  return isChanged;
}

void XboxControllerHandler::writeBuf()
{
  if (debug)
    cout << "Sent at " << clock << ": "; 
  for (int i = 0; i < 9; i++)
  {
    writeShort(data[i]);
    if (debug)
    {
     cout << data[i];
     if (i < 8)
       cout << ", ";
    }
  }
  if (debug)
    cout << endl;
}

void XboxControllerHandler::vibrate()
{
  // Create a Vibraton State
  XINPUT_VIBRATION vibration;

	// Zeroise the Vibration
	ZeroMemory(&vibration, sizeof(XINPUT_VIBRATION));

	// Set the Vibration Values
	vibration.wLeftMotorSpeed = leftVal;
	vibration.wRightMotorSpeed = rightVal;

	// Vibrate the controller
	XInputSetState(controllerNum, &vibration);
}