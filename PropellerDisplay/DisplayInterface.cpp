#include "DisplayInterface.h"

// array to map cathode pin columns of leds to output pins
// on shift registers
const byte CATHODE_PINS[16]
  = {0, 1, 2, 3, 4, 5, 6, 255, 7, 8, 9, 10, 11, 12, 13, 255};

// shift register control pins
const byte CLOCK_PIN = 2;
const byte LATCH_PIN = 3;
const byte DATA_PIN = 4;

// anode pins
const byte RED_PIN = 11;
const byte GREEN_PIN = 9;
const byte BLUE_PIN = 10;

// array to hold value for each led in the cube (on or off)
byte ledStatus[16];

// constructor
DisplayInterface::DisplayInterface()
{
  // initialize all leds to be black
  for(byte i = 0; i < 16; i++)
    ledStatus[i] = 0;

  pinMode(CLOCK_PIN, OUTPUT);
  pinMode(LATCH_PIN, OUTPUT);
  pinMode(DATA_PIN, OUTPUT);

  pinMode(RED_PIN, OUTPUT);
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(BLUE_PIN, OUTPUT);
} // DisplayInterface

// destructor
DisplayInterface::~DisplayInterface(){}

// turn a specific led on
void DisplayInterface::light(byte ledIndex, byte rgbValue)
{
  ledStatus[ledIndex] = rgbValue;
} // light

// turn all leds off
void DisplayInterface::clearAll()
{
  for(byte i = 0; i < 16; i++)
    ledStatus[i] = 0;
} // clearAll

// shift a high bit (1) into the shift registers
void DisplayInterface::highBit()
{
  digitalWrite(DATA_PIN, HIGH);
  digitalWrite(CLOCK_PIN, HIGH);
  digitalWrite(CLOCK_PIN, LOW);
  digitalWrite(DATA_PIN, LOW);
} // highBit

// shift a low bit (0) into the shift registers
void DisplayInterface::lowBit()
{
  digitalWrite(CLOCK_PIN, HIGH);
  digitalWrite(CLOCK_PIN, LOW);
} // lowBit

// latch data stored in the shift registers to the outputs
void DisplayInterface::latch()
{
  digitalWrite(LATCH_PIN, HIGH);
  digitalWrite(LATCH_PIN, LOW);
} // latch

void DisplayInterface::writeDisplay()
{
  for(byte i=0; i < 16; i++)
    if(CATHODE_PINS[i] < 14 && (ledStatus[CATHODE_PINS[i]] & B100) > 0)
      highBit();
    else
      lowBit();
  digitalWrite(BLUE_PIN, LOW);
  latch();
  digitalWrite(RED_PIN, HIGH);
  delayMicroseconds(5);
  
  for(byte i=0; i < 16; i++)
    if(CATHODE_PINS[i] < 14 && (ledStatus[CATHODE_PINS[i]] & B010) > 0)
      highBit();
    else
      lowBit();
  digitalWrite(RED_PIN, LOW);
  latch();
  digitalWrite(GREEN_PIN, HIGH);
  delayMicroseconds(5);
  
  for(byte i=0; i < 16; i++)
    if(CATHODE_PINS[i] < 14 && (ledStatus[CATHODE_PINS[i]] & B001) > 0)
      highBit();
    else
      lowBit();
  digitalWrite(GREEN_PIN, LOW);
  latch();
  digitalWrite(BLUE_PIN, HIGH);
  delayMicroseconds(5);
} // writeDisplay

// allow for delays in the code by updating the display every 10
// microseconds rather than completely pausing the program.
void DisplayInterface::wait(int t)
{
  unsigned long currentTime = micros();
  while(micros() - currentTime < t)
  {
    writeDisplay();
  } // while
} // wait
