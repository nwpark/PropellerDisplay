#include "Wire.h"
#include "DisplayInterface.h"

#define EEPROM_I2C_ADDRESS 0x50

DisplayInterface *disp;
byte *image[14];
boolean transmitting = false;
byte layerTransmitting;
byte currentPixel = 0;

void setup()
{
  Wire.begin();
  //Wire.setClock(400000L);
  Serial.begin(9600);
  disp = new DisplayInterface();

  loadImage();
} // setup

void loop()
{
//  disp->clearAll();
//  disp->light(5, 1);
//  disp->wait(1000);

//  for(byte j=0; j <= 7; j++)
//  {
//    for(byte i=0; i < 14; i++)
//      disp->light(i, j);
//    
//    disp->wait(100);
//  }
} // loop

void loadImage()
{
  for(int layer=0; layer < 14; layer++)
  {
    image[layer] = new byte[readAddress(layer)];
    
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
    byte pixelsInLayer = Serial.read();

    // initialize array for this layer
    image[layerTransmitting] = new byte[pixelsInLayer];

    // first 14 memory addresses store number of pixels in each layer
    writeAddress(layerTransmitting, pixelsInLayer);

    // second 14 memory addresses store the starting address for
    // the pixels in each layer
    if(layerTransmitting > 0)
    {
      byte previousLayer = layerTransmitting - 1;
      // set this layers address to be 1 greater than the last
      // address used for the previous layer
      byte thisLayerAddress = 1 + readAddress(previousLayer)
                              + readAddress(layerTransmitting + 14);
      writeAddress(layerTransmitting + 14, thisLayerAddress);
    } // if
    else
      // start address for first pixel data is 28
      writeAddress(14, 28);
  } // else if
  else
  {
    // all proceding bytes are pixel values
    byte pixelValue = Serial.read();
    
    image[layerTransmitting][currentPixel] = pixelValue;

    // address of this pixel = address of this layer + current
    // pixel index
    byte pixelAddress = readAddress(layerTransmitting + 14)
                        + currentPixel;
    writeAddress(pixelAddress, pixelValue);
    currentPixel++;

    // if all pixels have been uploaded, then finish the transmission
    if(currentPixel == sizeof(image[layerTransmitting])) {
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

void writeAddress(int address, byte val)
{
  Wire.beginTransmission(EEPROM_I2C_ADDRESS);
  Wire.write((int)(address >> 8));   // MSB
  Wire.write((int)(address & 0xFF)); // LSB
  
   
  Wire.write(val);
  Wire.endTransmission();

  delay(5);
} // writeAddress

byte readAddress(int address)
{
  byte rData = 0xFF;
  
  Wire.beginTransmission(EEPROM_I2C_ADDRESS);
  
  Wire.write((int)(address >> 8));   // MSB
  Wire.write((int)(address & 0xFF)); // LSB
  Wire.endTransmission();  


  Wire.requestFrom(EEPROM_I2C_ADDRESS, 1);  

  rData =  Wire.read();

  return rData;
} // readAddress
