package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jwildfire.image.SimpleImage;

public class WelcomeInternalFrame extends JInternalFrame {

  private String[] imageFilenames = { "bronze_bubbles.jpg", "smoky_dreams.jpg", "watchers2.jpg", "woven.jpg" };

  public WelcomeInternalFrame() {
    getContentPane().setBackground(UIManager.getColor("Button.background"));

    JPanel panel = new JPanel();
    panel.setPreferredSize(new Dimension(10, 50));
    getContentPane().add(panel, BorderLayout.NORTH);

    JPanel panel_1 = new JPanel();
    panel_1.setPreferredSize(new Dimension(10, 100));
    getContentPane().add(panel_1, BorderLayout.SOUTH);
    setTitle("Welcome to JWildfire! :-)");
    setBounds(100, 100, 660, 490);

    try {
      String imageFilename = imageFilenames[(int) (Math.random() * imageFilenames.length)];
      SimpleImage img = getImage(imageFilename);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      getContentPane().add(imgPanel, BorderLayout.CENTER);

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private SimpleImage getImage(String pName) throws Exception {
    byte[] imgData = getImagedata(pName);
    Image fileImg = Toolkit.getDefaultToolkit().createImage(imgData);
    MediaTracker tracker = new MediaTracker(this);
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
    InputStream is = this.getClass().getResourceAsStream("welcomescreen/" + pName);
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
}
