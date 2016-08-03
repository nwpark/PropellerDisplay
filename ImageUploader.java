import javax.swing.JFrame;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import java.awt.Graphics;
import javax.swing.JPanel;

public class ImageUploader extends JPanel
{
  private static BufferedImage image;

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    g.drawImage(image, 0, 0, this);
  } // paintComponent

  public static void main(String[] args) throws IOException
  {
    JFrame frame = new JFrame();
    JPanel panel = new ImageUploader();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(350, 350);
    frame.setVisible(true);

    File input = new File("8bit_mushroom_intro.jpg");
    image = ImageIO.read(input);
    ImageFormatter imageFormatter = new ImageFormatter(image, panel);

    panel.repaint();

    imageFormatter.formatImage();
  } // main

} // class ImageUploader
