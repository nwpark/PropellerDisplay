import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
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

public class PropellerDisplay extends JFrame implements ActionListener
{
  private BufferedImage image = null;
  private Color[][] formattedImageArray = null;
  private ImageJPanel imageJPanel = null;

  private final ImageUploader imageUploader = new ImageUploader();

  private final JButton formatJButton;
  private final JButton refreshJButton;
  private final JButton uploadJButton;
  private final JButton browseJButton;
  private final JTextField fileJTextField;
  private final JComboBox comPortJComboBox;
  private final JProgressBar progressBar;

  public PropellerDisplay() throws IOException
  {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch(Exception e) { System.out.println(e); }

    setTitle("Propeller Display Uploader");

    Container contents = getContentPane();
    contents.setLayout(new GridLayout(0, 2));

    // Left side of GUI
    JPanel uiJPanel = new JPanel();
    uiJPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
    uiJPanel.setLayout(new BorderLayout());
    contents.add(uiJPanel);
    // Right side of GUI
    imageJPanel = new ImageJPanel();
    contents.add(imageJPanel);

    // North side of UI area
    JPanel settingsJPanel = new JPanel();
    settingsJPanel.setLayout(new GridLayout(0, 1));
    uiJPanel.add(settingsJPanel, BorderLayout.NORTH);

    // File location selector
    JPanel fileJPanel = new JPanel();
    fileJPanel.setLayout(new GridLayout(0, 2));
    fileJPanel.setBorder(new EmptyBorder(3, 0, 3, 0));
    fileJPanel.add(new JLabel("File Location:"));
    browseJButton = new JButton("Browse");
    fileJPanel.add(browseJButton);
    browseJButton.addActionListener(this);
    settingsJPanel.add(fileJPanel);
    fileJTextField = new JTextField();
    fileJTextField.setEnabled(false);
    settingsJPanel.add(fileJTextField);

    // Seperator between file and com port selection
    settingsJPanel.add(new JSeparator());

    // COM Port selector
    JPanel comPortJPanel = new JPanel();
    comPortJPanel.setLayout(new GridLayout(0, 2));
    comPortJPanel.setBorder(new EmptyBorder(3, 0, 3, 0));
    comPortJPanel.add(new JLabel("COM Port:"));
    refreshJButton = new JButton("Refresh");
    comPortJPanel.add(refreshJButton);
    refreshJButton.addActionListener(this);
    settingsJPanel.add(comPortJPanel);
    // Combo box
    comPortJComboBox = new JComboBox(imageUploader.getPortNames());
    settingsJPanel.add(comPortJComboBox);
    comPortJComboBox.addActionListener(this);

    // Seperator between com port selection and pixel settings
    settingsJPanel.add(new JSeparator());

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

    // South side of UI area
    JPanel controlsJPanel = new JPanel();
    controlsJPanel.setLayout(new GridLayout(0, 1));
    uiJPanel.add(controlsJPanel, BorderLayout.SOUTH);

    // Progress bar
    progressBar = new JProgressBar(0, 100);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    progressBar.setString("Select an image file, then format and upload");
    controlsJPanel.add(progressBar);

    // Buttons at botton of UI
    JPanel buttonsJPanel = new JPanel();
    buttonsJPanel.setLayout(new GridLayout(0, 2));
    formatJButton = new JButton("Format");          // format button
    buttonsJPanel.add(formatJButton);
    formatJButton.addActionListener(this);
    uploadJButton = new JButton("Upload");          // upload button
    buttonsJPanel.add(uploadJButton);
    uploadJButton.addActionListener(this);
    controlsJPanel.add(buttonsJPanel);


    setDefaultCloseOperation(EXIT_ON_CLOSE);
    pack();
  } // PropellerDisplay

  @Override
  public void actionPerformed(ActionEvent event)
  {
    if(event.getSource() == browseJButton)
    {
      JFileChooser fileChooser = new JFileChooser(".");
      int returnValue = fileChooser.showOpenDialog(null);
      if(returnValue == JFileChooser.APPROVE_OPTION)
      {
        File selectedFile = fileChooser.getSelectedFile();
        fileJTextField.setText(selectedFile.getPath());
        imageJPanel.setImage(selectedFile.getName());
      } // if
    } // else if

    else if(event.getSource() == refreshJButton)
    {
      imageUploader.refresh();
      comPortJComboBox.removeAllItems();
      for(String comPortName : imageUploader.getPortNames())
        comPortJComboBox.addItem(comPortName);
    } // else if

    else if(event.getSource() == formatJButton)
    {
      Thread formatImageThread = new Thread() {
        public void run() {
          formatJButton.setEnabled(false);
          formatImage();
          formatJButton.setEnabled(true);
        } // run
      }; // formatImageThread
      formatImageThread.start();
    } // if

    else if(event.getSource() == uploadJButton)
    {
      Thread uploadImageThread = new Thread() {
        public void run() {
          System.out.println(comPortJComboBox.getSelectedItem());
          uploadJButton.setEnabled(false);

          if(imageUploader.upload(formattedImageArray,
                                  (String)comPortJComboBox.getSelectedItem(),
                                  progressBar)) {
            progressBar.setString("Upload Success");
            progressBar.setValue(0);
          } // if
          else
            progressBar.setString("Upload Failed");

          uploadJButton.setEnabled(true);
        } // run
      }; // uploadImageThread
      uploadImageThread.start();
    } // else if
  } // actionPerformed

  public void formatImage()
  {
    try
    {
      ImageFormatter imageFormatter = new ImageFormatter(imageJPanel);
      formattedImageArray = imageFormatter.formatImage();
    } // try
    catch(IOException e) {}
  } // formatImage

  public static void main(String[] args) throws IOException
  {
    PropellerDisplay propellerDisplay = new PropellerDisplay();
    propellerDisplay.setSize(650, 350);
    propellerDisplay.setVisible(true);
  } // main

} // class PropellerDisplay
