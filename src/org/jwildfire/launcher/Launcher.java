package org.jwildfire.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class Launcher {

  private JFrame frame;
  private JPanel centerPanel;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        }
        catch (Throwable ex) {
          try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
          }
          catch (Throwable ex2) {
          }
        }

        try {
          Launcher window = new Launcher();
          window.frame.setVisible(true);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Launcher() {
    initialize();
    loadImages();
  }

  private void loadImages() {
    try {
      final String[] imageFilenames = { "image1.jpg", "image2.jpg" };
      String imageFilename = imageFilenames[(int) (Math.random() * imageFilenames.length)];
      SimpleImage img = getImage(imageFilename);
      getCenterPanel().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setPreferredSize(new Dimension(img.getImageWidth(), img.getImageHeight()));
      getCenterPanel().add(imgPanel);
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 551, 374);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel panel = new JPanel();
    panel.setBackground(Color.BLACK);
    panel.setPreferredSize(new Dimension(0, 60));
    frame.getContentPane().add(panel, BorderLayout.NORTH);

    JPanel panel_1 = new JPanel();
    panel_1.setBackground(Color.BLACK);
    panel_1.setPreferredSize(new Dimension(0, 60));
    frame.getContentPane().add(panel_1, BorderLayout.SOUTH);

    JButton button = new JButton("Enter");
    button.setPreferredSize(new Dimension(128, 24));
    button.setForeground(SystemColor.menu);
    button.setBorderPainted(false);
    button.setBackground(Color.BLACK);
    panel_1.add(button);

    JPanel panel_2 = new JPanel();
    panel_2.setBackground(Color.GRAY);
    panel_2.setPreferredSize(new Dimension(100, 0));
    frame.getContentPane().add(panel_2, BorderLayout.WEST);

    JPanel panel_3 = new JPanel();
    panel_3.setBackground(Color.GRAY);
    panel_3.setPreferredSize(new Dimension(100, 0));
    frame.getContentPane().add(panel_3, BorderLayout.EAST);

    centerPanel = new JPanel();
    centerPanel.setBackground(Color.LIGHT_GRAY);
    frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
  }

  private SimpleImage getImage(String pName) throws Exception {
    byte[] imgData = getImagedata(pName);
    Image fileImg = Toolkit.getDefaultToolkit().createImage(imgData);
    MediaTracker tracker = new MediaTracker(frame);
    tracker.addImage(fileImg, 0);
    tracker.waitForID(0);
    int width = fileImg.getWidth(null);
    int height = fileImg.getHeight(null);
    BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = bImg.getGraphics();
    g.drawImage(fileImg, 0, 0, null);
    fileImg = null;
    return new SimpleImage(bImg, width, height);
  }

  private byte[] getImagedata(String pName) throws Exception {
    InputStream is = this.getClass().getResourceAsStream("images/" + pName);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    final int BUFFER_SIZE = 40960;
    byte[] buffer = new byte[BUFFER_SIZE];
    try {
      int n;
      while ((n = is.read(buffer, 0, BUFFER_SIZE)) >= 0) {
        os.write(buffer, 0, n);
      }
      os.flush();
      os.close();
      return os.toByteArray();
    }
    finally {
      is.close();
    }
  }

  public JPanel getCenterPanel() {
    return centerPanel;
  }
}
