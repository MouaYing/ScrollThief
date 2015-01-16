// XboxControllerHandler.h
// Must link with xinput.lib from the DirectX SDK

#ifndef __XboxControllerHandler_h__
#define __XboxControllerHandler_h__
#include "jaw.h"
#include <XInput.h>

// ----------------- class XboxControllerHandler -----------------
class XboxControllerHandler : public WindowHandler
{
public:
  XboxControllerHandler(JNIEnv * env, jobject obj);
  ~XboxControllerHandler();

protected:
   virtual bool wantThreadMsg(UINT uMsg);
   virtual void evThreadMsg(HWND hWnd, UINT uMsg, WPARAM wParam,
                            LPARAM lParam);

private:
  int controllerNum;
  int period;
  bool isReady;
  short * data;
  short * newData;
  int leftVal;
  int rightVal;
  bool doVibrate;
  bool debug;
  int clock;

  int invoke(int val);
  void writeBuf();
  void getState();
  void vibrate();
  bool isDataChanged();
};


#endif