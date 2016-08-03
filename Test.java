import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.IOException;

public class Test extends JPanel
{
  private static BufferedImage img;
  //  @Override
  //  public void paint(Graphics g)
  //  {
  //     try
  //     {
  //       Image img = ImageIO.read(new File("8bit_mushroom_intro.jpg"));
  //       g.drawImage(img, 0, 0, this);
  //     }
  //     catch (IOException e){}
  //  }

   @Override
   public void paintComponent(Graphics g)
   {
     super.paintComponent(g);
     //g.fillRect(0, 0, 100, 100);
     //g.fillArc(0, 0, 200, 200, 270, 90);
     g.drawImage(img, 0, 0, this);
   }

   public static void main(String[] args) throws IOException
   {
      img = ImageIO.read(new File("8bit_mushroom_intro.jpg"));

      JFrame frame = new JFrame();
      JPanel panel = new Test();
      frame.getContentPane().add(panel);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(350, 350);
      frame.setVisible(true);

      for(int x=0; x < 50; x++)
        for(int y=0; y < 50; y++)
          img.setRGB(x, y, new Color(0, 0, 0).getRGB());

      panel.repaint();
   }
}
