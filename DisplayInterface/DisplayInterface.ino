//byte anodePins[16] = {13, 12, 11, 10, 9, 8, 7, 255, 6, 5, 4, 3, 2, 1, 0, 255};
byte anodePins[16] = {0, 1, 2, 3, 4, 5, 6, 255, 7, 8, 9, 10, 11, 12, 13, 255};
boolean ledStatus[14];

// shift register control pins
const byte CLOCK_PIN = 2;
const byte LATCH_PIN = 3;
const byte DATA_PIN = 4;

void setup()
{
  pinMode(CLOCK_PIN, OUTPUT);
  pinMode(LATCH_PIN, OUTPUT);
  pinMode(DATA_PIN, OUTPUT);
  pinMode(8, INPUT);

  pinMode(9, OUTPUT);
  analogWrite(9, 20);

  pinMode(12, OUTPUT);
  digitalWrite(12, HIGH);
} // setup

void loop()
{
  clearAll();

  if(!digitalRead(8))
    light(13);
  else
    off(13);

  writeDisplay();

//  for(int i=0; i < 14; i++)
//  {
//    light(i);
//    writeDisplay();
//    delay(100);
//    off(i);
//    writeDisplay();
//  }
} // loop

void light(byte ledIndex)
{
  ledStatus[ledIndex] = HIGH;
} // light

void off(byte ledIndex)
{
  ledStatus[ledIndex] = LOW;
} // light

void clearAll()
{
  for(byte i=0; i < 14; i++)
    ledStatus[i] = LOW;
} // light

void writeDisplay()
{
  for(byte i=0; i < 16; i++)
    if(anodePins[i] < 14 && ledStatus[anodePins[i]])
      highBit();
    else
      lowBit();

  latch();
} // writeDisplay

// shift a high bit (1) into the shift registers
void highBit()
{
  digitalWrite(DATA_PIN, HIGH);
  digitalWrite(CLOCK_PIN, HIGH);
  digitalWrite(CLOCK_PIN, LOW);
  digitalWrite(DATA_PIN, LOW);
} // highBit

// shift a low bit (0) into the shift registers
void lowBit()
{
  digitalWrite(CLOCK_PIN, HIGH);
  digitalWrite(CLOCK_PIN, LOW);
} // lowBit

// latch data stored in the shift registers to the outputs
void latch()
{
  digitalWrite(LATCH_PIN, HIGH);
  digitalWrite(LATCH_PIN, LOW);
} // latch
