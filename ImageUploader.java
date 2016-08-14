import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import gnu.io.CommPortIdentifier;

public class ImageUploader
{
  private final HashMap<String, CommPortIdentifier>
    comPorts = new HashMap<String, CommPortIdentifier>();

  public ImageUploader()
  {
    Enumeration<CommPortIdentifier> comPortsEnum
      = CommPortIdentifier.getPortIdentifiers();
    while(comPortsEnum.hasMoreElements())
    {
      CommPortIdentifier serialPortId = comPortsEnum.nextElement();
      comPorts.put(serialPortId.getName(), serialPortId);
    } // while
  } // ImageUploader

  public String[] getPortNames()
  {
    Set<String> portNames = comPorts.keySet();
    return portNames.toArray(new String[portNames.size()]);
  } // getPortNames

} // class ImageUploader
