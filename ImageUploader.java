import gnu.io.*; // RXTX
import java.util.Enumeration;

public class ImageUploader
{

	public static void main(String[] args)
  {
		//System.out.println(java.library.path);
	    CommPortIdentifier serialPortId;
	    //static CommPortIdentifier sSerialPortId;
	    Enumeration enumComm;
	    //SerialPort serialPort;

	    enumComm = CommPortIdentifier.getPortIdentifiers();
	    while (enumComm.hasMoreElements()) {
	     	serialPortId = (CommPortIdentifier) enumComm.nextElement();
	     	if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL)
	    		System.out.println(serialPortId.getName());
	    }
	}

}
