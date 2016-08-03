import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.IOException;

public class Test extends JPanel
{

   @Override
   public void paint(Graphics g)
   {
      try
      {
        Image img = ImageIO.read(new File("8bit_mushroom_intro.jpg"));
        g.drawImage(img, 0, 0, this);
      }
      catch (IOException e){}
   }

   @Override
   public void paintComponent(Graphics g)
   {
     super.paintComponent(g);
     g.fillRect(0, 0, 100, 100);
   }

   public static void main(String[] args)
   {
      JFrame frame = new JFrame();
      JPanel panel = new Test();
      frame.getContentPane().add(panel);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(350, 350);
      frame.setVisible(true);

      // Graphics g = panel.getGraphics();
      // g.fillRect(0, 0, 100, 100);
      // panel.paintComponent(g);
      frame.repaint();
   }
}
