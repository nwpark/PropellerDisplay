import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

  public ImageFormatter(BufferedImage givenImage) throws IOException
  {
    image = outputImage = givenImage;

    imageRadius = Math.min(image.getWidth(), image.getHeight()) / 2;
    imageCenterX = image.getWidth() / 2;
    imageCenterY = image.getHeight() / 2;
    // 14 pixels on the display
    pixelHeight = pixelWidth = imageRadius / 14;

    formattedArray = new Color[14][];
  } // ImageFormatter

  public void formatImage() throws IOException
  {
    // for (int x = 0; x < pixelHeight; x++)
    //     for (int y = 0; y < pixelHeight; y++)
    //     {
    //         double dx = x - imageCenterX;
    //         double dy = y - imageCenterY;
    //         double distanceSquared = dx * dx + dy * dy;
    //
    //         if (distanceSquared <= (pixelWidth * pixelWidth))
    //         {
    //           outputImage.setRGB(imageCenterX + x, imageCenterY + y,
    //                              new Color(0, 0, 0).getRGB());
    //           System.out.println("yay");
    //         }
    //         System.out.println("yay2");
    //     }
    // File output = new File("test.jpg");
    // ImageIO.write(outputImage, "jpg", output);

    for(int radius = 1; radius < 14; radius++)
    {
      int noOfSections = (int)(2*Math.PI*radius*pixelHeight / (double)pixelWidth);
      formattedArray[radius] = new Color[noOfSections];
      double initAngle = 0;
      for(int i=0; i < noOfSections; i++)
      {
        double endAngle = initAngle + 2*Math.PI / noOfSections;
        formattedArray[radius][i]
          = averageSectionColor(radius*pixelHeight,
                                initAngle, endAngle);
        initAngle = endAngle;
      } // for
    } // for
  } // formatImage

  private Color averageSectionColor(int radius, double initAngle,
                                    double endAngle) throws IOException
  {
    int redAverage, greenAverage, blueAverage, pixelCount;
    redAverage = greenAverage = blueAverage = pixelCount = 0;

    for(int currentRadius = radius - pixelHeight / 2;
        currentRadius <= radius + pixelHeight / 2; currentRadius++)
    {
      // arc length formula s = r*theta
      double angleIncrement = 1 / (double)currentRadius;
      double currentAngle = initAngle;

      while(currentAngle <= endAngle)
      {
        Color pixelColor
          = new Color(image.getRGB(imageCenterX + (int)(currentRadius * Math.sin(currentAngle)),
                                   imageCenterY + (int)(currentRadius * Math.cos(currentAngle))));

        // outputImage.setRGB(imageCenterX + (int)(currentRadius * Math.sin(currentAngle)),
        //                    imageCenterY + (int)(currentRadius * Math.cos(currentAngle)),
        //                    new Color(0, 0, 0).getRGB());

        redAverage += pixelColor.getRed();
        greenAverage += pixelColor.getGreen();
        blueAverage += pixelColor.getBlue();
        pixelCount++;

        currentAngle += angleIncrement;
      } // while
    } // for

    // File output = new File("test.jpg");
    // ImageIO.write(image, "jpg", output);

    redAverage /= pixelCount;
    greenAverage /= pixelCount;
    blueAverage /= pixelCount;
    return new Color(redAverage, greenAverage, blueAverage);
  } // averageSectionColor

  public void createOutput()
  {

  } // createOutput

} // class ImageFormatter
