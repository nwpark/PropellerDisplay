#include "DisplayInterface.h"

DisplayInterface *disp;

void setup()
{
  disp = new DisplayInterface();
} // setup

void loop()
{
  disp->clearAll();
  disp->light(5, 4);
  disp->writeDisplay();
  disp->wait(1000);

//  for(byte j=0; j <= 7; j++)
//  {
//    for(byte i=0; i < 14; i++)
//    {
//      disp->light(i, j);
//    }
//    
//    disp->writeDisplay();
//    disp->wait(1000);
//  }

} // loop
