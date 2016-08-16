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
    // format the central pixel
    formattedImageArray[0] = new Color[1];
    formattedImageArray[0][0] = getSectionColor(pixelHeight / 2, 0, 2*Math.PI);
    setSectionColor(pixelHeight / 2, 0, 2*Math.PI, formattedImageArray[0][0]);

    // format the rest of the pixels
    for(int radius = 1; radius < 14; radius++)
    {
      int noOfSections = (int)(2*Math.PI*radius*pixelHeight / (double)pixelWidth);
      formattedImageArray[radius] = new Color[noOfSections];
      double initAngle = 0;
      double endAngle = initAngle + 2*Math.PI / noOfSections;
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

    try {
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

    AverageColor pixelColor = null;

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
            pixelColor = new AverageColor(image.getRGB(x, y));
          else
            pixelColor.rollingAverage(image.getRGB(x, y));
        } // if
      } // for

    return pixelColor.getThreeBitAverage();
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


  public static class AverageColor extends Color
  {
    private int averageRed, averageGreen, averageBlue;
    private int noOfPixels;

    public AverageColor(int rgb)
    {
      super(rgb);
      noOfPixels = 0;
    } // AverageColor

    public void rollingAverage(int newRGB)
    {
      averageRed += (newRGB >> 16) & 0xFF;
      averageGreen += (newRGB >> 8) & 0xFF;
      averageBlue += newRGB & 0xFF;
      noOfPixels++;
    } // averageWith

    public Color getAverage()
    {
      return new Color(averageRed / noOfPixels,
                       averageGreen / noOfPixels,
                       averageBlue / noOfPixels);
    } // setAverage

    public Color getThreeBitAverage()
    {
      Color color = getAverage();
      return new Color(color.getRed() < 128 ? 0 : 255,
                       color.getGreen() < 128 ? 0 : 255,
                       color.getBlue() < 128 ? 0 : 255);
    } // getThreeBit
  } // class AverageColor

} // class ImageFormatter
