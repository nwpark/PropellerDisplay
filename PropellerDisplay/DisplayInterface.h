#ifndef DisplayInterface_H
#define DisplayInterface_H

#include <Arduino.h>

class DisplayInterface
{
  public:
    DisplayInterface();
    ~DisplayInterface();
    void light(byte ledIndex, byte rgbValue);
    void clearAll();
    void writeDisplay();
    void wait(int t);
  private:
    void highBit();
    void lowBit();
    void latch();
};

#endif
