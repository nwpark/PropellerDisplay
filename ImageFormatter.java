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
  private int noOfPixels;
  private Color[][] formattedImageArray;
  private JPanel panel;

  public ImageFormatter(ImageJPanel givenPanel) throws IOException
  {
    panel = givenPanel;
    image = outputImage = givenPanel.getImage();

    imageRadius = Math.min(image.getWidth(), image.getHeight()) / 2;
    imageCenterX = image.getWidth() / 2;
    imageCenterY = image.getHeight() / 2;
    // 14 pixels on the display
    pixelHeight = pixelWidth = imageRadius / 14;

    formattedImageArray = new Color[14][];
  } // ImageFormatter

  public Color[][] formatImage() //throws IOException
  {
    for(int radius = 1; radius < 14; radius++)
    {
      int noOfSections = (int)(2*Math.PI*radius*pixelHeight / (double)pixelWidth);
      formattedImageArray[radius] = new Color[noOfSections];
      double initAngle = 0;
      double endAngle = initAngle + 2*Math.PI / noOfSections;
      noOfPixels = pixelsPerSegment(radius*pixelHeight, initAngle, endAngle);
      for(int i=0; i < noOfSections; i++)
      {
        endAngle = initAngle + 2*Math.PI / noOfSections;
        formattedImageArray[radius][i]
          = getSectionColor(radius*pixelHeight, initAngle, endAngle);
        setSectionColor(radius*pixelHeight, initAngle, endAngle,
                        formattedImageArray[radius][i]);
        initAngle = endAngle;
      } // for
    } // for

    try
    {
      File output = new File("test.jpg");
      ImageIO.write(outputImage, "jpg", output);
    } // try
    catch(IOException e) { System.out.println(e); }

    return formattedImageArray;
  } // formatImage

  private Color getSectionColor(int radius, double initAngle,
                                double endAngle)
  {
    int outerRad = radius + pixelHeight / 2;
    int innerRad = radius - pixelHeight / 2;

    Color pixelColor = null;

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
          if(pixelColor == null)
            pixelColor = new Color(image.getRGB(x, y));
          else
            pixelColor = rollingAverage(pixelColor,
                                        new Color(image.getRGB(x, y)),
                                        noOfPixels);
        } // if
      } // for

    return pixelColor;
  } // getSectionColor

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

  private int pixelsPerSegment(int radius, double initAngle,
                               double endAngle)
  {
    int pixelCount = 0;

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
          pixelCount++;
      } // for

    return pixelCount;
  } // pixelsPerSegment

  private Color rollingAverage(Color average, Color newSample, int noOfSamples)
  {
    int averageRed = average.getRed() - (average.getRed()/noOfSamples)
                                      + (newSample.getRed()/noOfSamples);
    int averageGreen = average.getGreen() - (average.getGreen()/noOfSamples)
                                      + (newSample.getGreen()/noOfSamples);
    int averageBlue = average.getBlue() - (average.getBlue()/noOfSamples)
                                      + (newSample.getBlue()/noOfSamples);
    return new Color(averageRed, averageGreen, averageBlue);
  } // rollingAverage

} // class ImageFormatter
