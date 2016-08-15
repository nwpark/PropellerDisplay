import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.awt.Color;

import gnu.io.CommPortIdentifier;
import gnu.io.CommPort;
import gnu.io.SerialPort;
import gnu.io.PortInUseException;

public class ImageUploader
{
  private final HashMap<String, CommPortIdentifier>
    comPorts = new HashMap<String, CommPortIdentifier>();

  public ImageUploader()
  {
    refresh();
  } // ImageUploader

  public boolean upload(Color[][] formattedImageArray, String comPortName)
  {
    SerialPort serialPort;
    InputStream in = null;
    OutputStream out = null;

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
    } catch(PortInUseException e) {
      System.out.println(comPortName + " is in use");
      return false;
    } catch(ClassCastException e) {
      System.out.println(comPortName + " is not a serial port");
      return false;
    } catch(Exception e) { System.out.println(e); }

    while(true)
    {
      try {
        //out.write(1);
        out.write(System.in.read());

        //System.out.println(in.read());
        byte[] buffer = new byte[1024];
        int len = -1;
        len = in.read(buffer);
        //while ( ( len = in.read(buffer)) > -1 )
          System.out.print(new String(buffer,0,len));
      } catch(IOException e) {}
    }

    // for(int i=1; i < formattedImageArray.length; i++)
    // {
    //
    // } // for
    // return true;
  } // upload

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
