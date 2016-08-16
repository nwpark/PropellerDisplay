import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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

  public ImageUploader()
  {
    refresh();
  } // ImageUploader

  public synchronized boolean upload(Color[][] formattedImageArray, String comPortName)
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
      Thread.sleep(2000);

      // write the array items to serial port
      for(int i=1; i < formattedImageArray.length; i++)
      {
        out.write(i);
        if(!acknowledge())      // wait for acknowledgement
          return false;
        out.write(formattedImageArray[i].length);
        if(!acknowledge())      // wait for acknowledgement
          return false;
        for(int j=0; j < formattedImageArray[i].length; j++)
        {
          out.write(formattedImageArray[i][j].getRed());
          if(!acknowledge())      // wait for acknowledgement
            return false;
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

  public synchronized void serialEvent(SerialPortEvent event)
  {
    if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE)
      try {
        ack = (in.read() == 1);

        // clear serial buffer
        while(in.read() != -1) {}

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

  public String[] getPortNames()
  {
    Set<String> portNames = comPorts.keySet();
    return portNames.toArray(new String[portNames.size()]);
  } // getPortNames

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
