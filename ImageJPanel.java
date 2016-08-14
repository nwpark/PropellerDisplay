import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.io.File;
import java.io.IOException;

public class ImageJPanel extends JPanel
{
  private BufferedImage image;

  public ImageJPanel()
  {
    super();
    setBorder(BorderFactory.createTitledBorder("Output"));
  } // ImageJPanel

  public BufferedImage getImage()
  {
    return image;
  } // getImage

  public void setImage(String fileName)
  {
    try
    {
      File input = new File(fileName);
      image = ImageIO.read(input);
      repaint();
    } catch(IOException e) {}
  } // setImage

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    g.drawImage(image, 10, 10, this);
  } // paintComponent
} // class ImageJPanel
