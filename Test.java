import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.IOException;

public class Test extends JPanel {

   public void paint(Graphics g)
   {
      try
      {
        Image img = ImageIO.read(new File("8bit_mushroom_intro.jpg"));
        g.drawImage(img, 0, 0, this);
      }
      catch (IOException e){}
   }

   public static void main(String[] args) {
      JFrame frame = new JFrame();
      frame.getContentPane().add(new Test());

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(350, 350);
      frame.setVisible(true);
   }
}
