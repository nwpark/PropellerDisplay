#include "Wire.h"
#include "DisplayInterface.h"

#define EEPROM_I2C_ADDRESS 0x50

DisplayInterface *disp;
byte *image[14];
byte layerLengths[14];
boolean transmitting = false;
byte layerTransmitting;
byte currentPixel = 0;

int incrementDelay = 1000;
unsigned long previousTime = 0;

void setup()
{
  Wire.begin();
  //Wire.setClock(400000L);
  Serial.begin(9600);
  disp = new DisplayInterface();

  loadImage();
  pinMode(8, INPUT);
  //attachInterrupt(digitalPinToInterrupt(8), updateSpeed, RISING);
} // setup

void loop()
{
  int angle = 0;
  while(!transmitting && angle < layerLengths[13])
  {
    if(digitalRead(8))
      updateSpeed();
    
    for(int layer=0; layer < 14; layer++)
    {
      byte pixelIndex = (byte)((layerLengths[layer] * angle)
                               / layerLengths[13]);
      byte rgbValue = image[layer][pixelIndex];
      disp->light(layer, rgbValue);
    } // for
    
    angle++;
    disp->wait(incrementDelay);
  } // while
  
//  if(digitalRead(8))
//  {
//    disp->light(3, 7);
//    disp->wait(500);
//  }
} // loop

void updateSpeed()
{
  unsigned long lastRotationTime = millis() - previousTime;
  
  // debouncing - definitely not going faster than 60 fps,
  // 1000/60 = 17
  if(lastRotationTime > 17)
  {
    incrementDelay = (lastRotationTime*1000) / layerLengths[13];
    previousTime = millis();
    //Serial.println(incrementDelay);
  } // if
} // updateSpeed

void loadImage()
{
  for(byte layer=0; layer < 14; layer++)
  {
    // noOfPixels in each layer is stored in first 14 bytes in EEPROM
    byte noOfPixels = layerLength(layer);
    layerLengths[layer] = noOfPixels;

    // initialize array of pixels for this layer
    delete[] image[layer];
    image[layer] = new byte[noOfPixels];

    byte layerAddress = readLayerAddress(layer);
    for(byte pixelNo = 0; pixelNo < noOfPixels; pixelNo++)
      image[layer][pixelNo] = readEEPROM(layerAddress + pixelNo);
  } // for
} // loadImage

void serialEvent()
{
  if(!transmitting)
  {
    transmitting = true;
    layerTransmitting = Serial.read();

    //reset the layer that is about to be downloaded
    delete[] image[layerTransmitting];
    image[layerTransmitting] = NULL;
  } // if
  
  else if(image[layerTransmitting] == NULL)
  {
    // first byte recieved indicates number of pixels in this layer
    int pixelsInLayer = Serial.read();

    // initialize array for this layer
    image[layerTransmitting] = new byte[pixelsInLayer];

    setLayerLength(layerTransmitting, pixelsInLayer);

    // next 28 memory addresses store the starting address for
    // the pixels in each layer
    if(layerTransmitting > 0)
    {
      byte previousLayer = layerTransmitting - 1;
      // set this layers address to be 1 greater than the last
      // address used for the previous layer
      int thisLayerAddress = layerLength(previousLayer)
                             + readLayerAddress(previousLayer);
      writeLayerAddress(layerTransmitting, thisLayerAddress);
    } // if
    else
      // start address for first pixel layer data is 36
      writeLayerAddress(0, 42);
  } // else if
  else
  {
    // all proceding bytes are pixel values
    byte pixelValue = Serial.read();
    
    image[layerTransmitting][currentPixel] = pixelValue;

    // address of this pixel = address of this layer + current
    // pixel index
    int pixelAddress = readLayerAddress(layerTransmitting)
                       + currentPixel;
    writeEEPROM(pixelAddress, pixelValue);
    currentPixel++;

    // if all pixels have been uploaded, then finish the transmission
    if(currentPixel >= readEEPROM(layerTransmitting)) {
      currentPixel = 0;
      transmitting = false;
    } // if
  } // else

  acknowledge();
} // serialEvent


void acknowledge() {
  // clear serial buffer
  while(Serial.available())
    Serial.read();

  // 1 indicates acknowledge
  Serial.write(1);
} // acknowledge

void writeLayerAddress(byte layer, int layerAddress)
{
  // address is 16 bit so stored across 2 bytes
  writeEEPROM(layer*2 + 14, layerAddress >> 8);
  writeEEPROM(layer*2 + 15, layerAddress & 0xFF);
} // writeLayerAddress

int readLayerAddress(byte layer)
{
  // address is 16 bit so stored across 2 bytes
  int layerAddress = readEEPROM(layer*2 + 14);
  layerAddress = layerAddress << 8;
  layerAddress |= readEEPROM(layer*2 + 15);
  return layerAddress;
} // readLayerAddress

void setLayerLength(byte layerNo, byte noOfPixels)
{
  // first 14 memory addresses store number of pixels in each layer
  writeEEPROM(layerNo, noOfPixels);
  layerLengths[layerNo] = noOfPixels;
} // setLayerLength

byte layerLength(byte layerNo)
{
  // first 14 memory addresses store number of pixels in each layer
  return readEEPROM(layerNo);
} // layerLength

void writeEEPROM(int address, byte val)
{
  Wire.beginTransmission(EEPROM_I2C_ADDRESS);
  Wire.write((int)(address >> 8));   // MSB
  Wire.write((int)(address & 0xFF)); // LSB
  
   
  Wire.write(val);
  Wire.endTransmission();

  delay(5);
} // writeEEPROM

byte readEEPROM(int address)
{
  byte rData = 0xFF;
  
  Wire.beginTransmission(EEPROM_I2C_ADDRESS);
  
  Wire.write((int)(address >> 8));   // MSB
  Wire.write((int)(address & 0xFF)); // LSB
  Wire.endTransmission();  


  Wire.requestFrom(EEPROM_I2C_ADDRESS, 1);  

  rData =  Wire.read();

  return rData;
} // readEEPROM
