import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

import java.io.File;
import java.io.IOException;

public class ImageUploader extends JFrame implements ActionListener
{
  private BufferedImage image;
  private ImageJPanel imageJPanel;

  private final JButton formatJButton = new JButton("Format");
  private final JButton uploadJButton = new JButton("Upload");
  private final JButton browseJButton = new JButton("Browse");
  private final JTextField fileJTextField = new JTextField();

  public ImageUploader() throws IOException
  {
    try{
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch(Exception e) { System.out.println(e); }

    setTitle("Image Uploader");
    imageJPanel = new ImageJPanel();
    //imageJPanel.setImage("8bit_mushroom_intro.jpg");

    Container contents = getContentPane();
    contents.setLayout(new GridLayout(0, 2));

    // Left side of GUI
    JPanel uiJPanel = new JPanel();
    uiJPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
    uiJPanel.setLayout(new BorderLayout());
    contents.add(uiJPanel);
    // Right side of GUI
    //imageJPanel = new ImageJPanel(image);
    contents.add(imageJPanel);

    // North side of UI area
    JPanel settingsJPanel = new JPanel();
    settingsJPanel.setLayout(new GridLayout(0, 1));
    uiJPanel.add(settingsJPanel, BorderLayout.NORTH);

    // File location selector
    JPanel fileJPanel = new JPanel();
    fileJPanel.setLayout(new GridLayout(0, 2));
    fileJPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
    fileJPanel.add(new JLabel("File Location:"));
    fileJPanel.add(browseJButton);
    browseJButton.addActionListener(this);
    settingsJPanel.add(fileJPanel);
    //fileJTextField.setEnabled(false);
    settingsJPanel.add(fileJTextField);

    settingsJPanel.add(new JSeparator());

    // COM Port selector
    settingsJPanel.add(new JLabel("COM Port:"));
    JTextField comPortJTextField = new JTextField();
    settingsJPanel.add(comPortJTextField);

    // Pixel settings
    JPanel optionsJPanel = new JPanel();
    optionsJPanel.setLayout(new GridLayout(0, 2));
    JPanel pixelNoJPanel = new JPanel();
    pixelNoJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    pixelNoJPanel.add(new JLabel("Pixels:"));
    pixelNoJPanel.add(new JTextField("14", 3));
    optionsJPanel.add(pixelNoJPanel);
    JPanel pixelWidthJPanel = new JPanel();
    pixelWidthJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    pixelWidthJPanel.add(new JLabel("Pixel width:"));
    pixelWidthJPanel.add(new JTextField("1", 3));
    optionsJPanel.add(pixelWidthJPanel);
    settingsJPanel.add(optionsJPanel);

    // Buttons at botton of UI
    JPanel buttonsJPanel = new JPanel();
    buttonsJPanel.setLayout(new GridLayout(0, 2));
    buttonsJPanel.add(formatJButton);
    formatJButton.addActionListener(this);
    buttonsJPanel.add(uploadJButton);
    uiJPanel.add(buttonsJPanel, BorderLayout.SOUTH);

    setDefaultLookAndFeelDecorated(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    pack();
  } // ImageUploader

  @Override
  public void actionPerformed(ActionEvent event)
  {
    if(event.getSource() == formatJButton)
    {
      Thread formatImageThread = new Thread(new Runnable(){
        public void run(){ formatImage(); } });

      formatImageThread.start();
    } // if
    else if(event.getSource() == browseJButton)
    {
      JFileChooser fileChooser = new JFileChooser(".");
      int returnValue = fileChooser.showOpenDialog(null);
      if(returnValue == JFileChooser.APPROVE_OPTION)
      {
        File selectedFile = fileChooser.getSelectedFile();
        fileJTextField.setText(selectedFile.getPath());

        imageJPanel.setImage(selectedFile.getName());
        // File input = new File(selectedFile.getName());
        // image = ImageIO.read(input);
      } // if
    } // else if
  } // actionPerformed

  public void formatImage()
  {
    try
    {
      ImageFormatter imageFormatter = new ImageFormatter(imageJPanel);
      imageFormatter.formatImage();
    } // try
    catch(IOException e) {}
  } // formatImage

  public static void main(String[] args) throws IOException
  {
    ImageUploader imageUploader = new ImageUploader();
    imageUploader.setSize(650, 350);
    imageUploader.setVisible(true);
  } // main

} // class ImageUploader
