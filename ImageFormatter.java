import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;

public class ImageFormatter
{
  private BufferedImage image;
  private BufferedImage outputImage;
  private int imageRadius;
  private int imageCenterX;
  private int imageCenterY;
  private int pixelHeight;
  private int pixelWidth;
  private Color[][] formattedArray;
  private JPanel panel;

  public ImageFormatter(ImageJPanel givenPanel) throws IOException
  {
    image = outputImage = givenPanel.getImage();
    panel = givenPanel;

    imageRadius = Math.min(image.getWidth(), image.getHeight()) / 2;
    imageCenterX = image.getWidth() / 2;
    imageCenterY = image.getHeight() / 2;
    // 14 pixels on the display
    pixelHeight = pixelWidth = imageRadius / 14;

    formattedArray = new Color[14][];
  } // ImageFormatter

  public void formatImage() //throws IOException
  {
    //averageSectionColor(300, Math.PI*3/2, Math.PI*2);

    for(int radius = 1; radius < 14; radius++)
    {
      int noOfSections = (int)(2*Math.PI*radius*pixelHeight / (double)pixelWidth);
      formattedArray[radius] = new Color[noOfSections];
      double initAngle = 0;
      for(int i=0; i < noOfSections; i++)
      {
        double endAngle = initAngle + 2*Math.PI / noOfSections;
        formattedArray[radius][i]
          = averageSectionColor(radius*pixelHeight, initAngle, endAngle);
        setSectionColor(radius*pixelHeight, initAngle, endAngle,
                        formattedArray[radius][i]);
        initAngle = endAngle;
      } // for
    } // for

    try
    {
      File output = new File("test.jpg");
      ImageIO.write(outputImage, "jpg", output);
    } // try
    catch(IOException e) { System.out.println(e); }
  } // formatImage

  private Color averageSectionColor(int radius, double initAngle,
                                    double endAngle)
  {
    int redAverage, greenAverage, blueAverage, pixelCount;
    redAverage = greenAverage = blueAverage = pixelCount = 0;

    int outerRad = radius + pixelHeight / 2;
    int innerRad = radius - pixelHeight / 2;

    for(int x=0; x < image.getWidth(); x++)
      for(int y=0; y < image.getHeight(); y++)
      {
        double dx = x - imageCenterX;
        double dy = y - imageCenterY;
        double distanceSquared = dx*dx + dy*dy;

        //double currentAngle = Math.atan2(dx, dy);
        double currentAngle = Math.atan(dx / dy);
        // 1st and 4th quadrants
        if(dy < 0) currentAngle += Math.PI;
        // 3rd quadrant
        else if(dy > 0 && dx < 0) currentAngle += 2*Math.PI;

        if(distanceSquared < outerRad*outerRad
                && distanceSquared > innerRad*innerRad
                && currentAngle > initAngle
                && currentAngle < endAngle)
        {
          Color pixelColor = new Color(image.getRGB(x, y));
          redAverage += pixelColor.getRed();
          greenAverage += pixelColor.getGreen();
          blueAverage += pixelColor.getBlue();
          pixelCount++;
        } // if
      } // for

    redAverage /= pixelCount;
    greenAverage /= pixelCount;
    blueAverage /= pixelCount;
    return new Color(redAverage, greenAverage, blueAverage);
  } // averageSectionColor

  private void setSectionColor(int radius, double initAngle,
                               double endAngle, Color color)
  {
    int outerRad = radius + pixelHeight / 2;
    int innerRad = radius - pixelHeight / 2;

    for(int x=0; x < image.getWidth(); x++)
      for(int y=0; y < image.getHeight(); y++)
      {
        double dx = x - imageCenterX;
        double dy = y - imageCenterY;
        double distanceSquared = dx*dx + dy*dy;

        //double currentAngle = Math.atan2(dx, dy);
        double currentAngle = Math.atan(dx / dy);
        // 1st and 4th quadrants
        if(dy < 0) currentAngle += Math.PI;
        // 3rd quadrant
        else if(dy > 0 && dx < 0) currentAngle += 2*Math.PI;

        if(distanceSquared < outerRad*outerRad
                && distanceSquared > innerRad*innerRad
                && currentAngle > initAngle
                && currentAngle < endAngle)
        {
          outputImage.setRGB(x, y, color.getRGB());
          panel.repaint();
        } // if
      } // for
  } // setSectionColor

} // class ImageFormatter
