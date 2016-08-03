import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

public class ImageJPanel extends JPanel
{
  private BufferedImage image;

  public ImageJPanel(BufferedImage givenImage)
  {
    image = givenImage;
    setBorder(BorderFactory.createTitledBorder("Output"));
  } // ImageJPanel

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    g.drawImage(image, 10, 10, this);
  } // paintComponent
} // class ImageJPanel
