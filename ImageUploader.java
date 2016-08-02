import javax.swing.JFrame;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class ImageUploader extends JFrame
{
  public static void main(String[] args) throws IOException
  {
    File input = new File("20160302_191646.jpg");
    BufferedImage image = ImageIO.read(input);
    ImageFormatter imageFormatter = new ImageFormatter(image);

    imageFormatter.formatImage();
  } // main

} // class ImageUploader
