import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JProgressBar;
import java.awt.Color;

import gnu.io.CommPortIdentifier;
import gnu.io.CommPort;
import gnu.io.SerialPort;
import gnu.io.PortInUseException;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class ImageUploader implements SerialPortEventListener
{
  private final HashMap<String, CommPortIdentifier>
    comPorts = new HashMap<String, CommPortIdentifier>();

  private InputStream in = null;
  private OutputStream out = null;
  private boolean ack = false;

  // constructor
  public ImageUploader()
  {
    refresh();
  } // ImageUploader

  // upload the given image through the given serial port
  public synchronized boolean upload(Color[][] formattedImageArray,
                                     String comPortName,
                                     JProgressBar progressBar)
  {
    if(formattedImageArray == null || comPortName == null)
      return false;

    SerialPort serialPort;

    try {
      // open(java.lang.String theOwner, int timeout), using 2 sec timeout
      CommPort commPort
        = comPorts.get(comPortName).open("Image Uploader", 2000);
      serialPort = (SerialPort)commPort;

      // 9600 baud rate
      serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                                     SerialPort.STOPBITS_1,
                                     SerialPort.PARITY_NONE);

      in = serialPort.getInputStream();
      out = serialPort.getOutputStream();

      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);

      // give time for connection to establish
      progressBar.setIndeterminate(true);
      progressBar.setString("Connecting...");
      Thread.sleep(2000);
      progressBar.setIndeterminate(false);
      progressBar.setString(null);

      // calculate total pixels to upload for progress bar purposes
      int totalPixels = 0;
      int pixelsUploaded = 0;
      for(int i=1; i < formattedImageArray.length; i++)
        totalPixels += formattedImageArray[i].length;

      // write the array items to serial port
      for(int i=1; i < formattedImageArray.length; i++)
      {
        // write the current pixel array index to serial port
        out.write(i);
        if(!acknowledge())      // wait for acknowledgement
          return false;
        // write the length of the current pixel array to serial port
        out.write(formattedImageArray[i].length);
        if(!acknowledge())      // wait for acknowledgement
          return false;
        for(int j=0; j < formattedImageArray[i].length; j++)
        {
          // write the pixel value to serial port
          out.write(formattedImageArray[i][j].getRed());
          if(!acknowledge())      // wait for acknowledgement
            return false;

          // update progress bar
          pixelsUploaded++;
          progressBar.setValue((pixelsUploaded*100 / totalPixels));
        }
      } // for
    } catch(PortInUseException e) {
      System.out.println(comPortName + " is in use");
      return false;
    } catch(ClassCastException e) {
      System.out.println(comPortName + " is not a serial port");
      return false;
    } catch(IOException e) {
      System.err.println(e);
    } catch(InterruptedException e) {
      System.err.println(e);
    } catch(Exception e) {
      System.out.println(e);
    } // catch

    return true;
  } // upload

  // this method is invoked upon recieving data through the serial port
  public synchronized void serialEvent(SerialPortEvent event)
  {
    if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE)
      try {
        // check if revieved byte was 1 to indicate ack
        ack = (in.read() == 1);
        // clear serial buffer
        while(in.read() != -1) {}

        // notify the thread to stop waiting for ack
        this.notify();
      } catch(IOException e) {}
  } // serialEvent

  private boolean acknowledge() throws InterruptedException
  {
    // wait for acknowledgement, with 1s timeout
    this.wait(1000);
    if(ack) {
      ack = false;
      return true;
    }
    else
      return false;
  } // acknowledge

  // returns the names of all the comm ports currently connected
  public String[] getPortNames()
  {
    Set<String> portNames = comPorts.keySet();
    return portNames.toArray(new String[portNames.size()]);
  } // getPortNames

  // refresh to check for new comm ports
  public void refresh()
  {
    comPorts.clear();

    Enumeration<CommPortIdentifier> comPortsEnum
      = CommPortIdentifier.getPortIdentifiers();
    while(comPortsEnum.hasMoreElements())
    {
      CommPortIdentifier serialPortId = comPortsEnum.nextElement();
      comPorts.put(serialPortId.getName(), serialPortId);
    } // while
  } // refresh

} // class ImageUploader
