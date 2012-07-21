/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class Launcher {

  private JFrame frame;
  private JPanel centerPanel;
  private JButton launchButton;
  private JPanel northPanel;
  private final LauncherPrefs prefs;

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
          LauncherPrefs prefs = new LauncherPrefs();
          new LauncherPrefsReader().readPrefs(prefs);
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
    prefs = new LauncherPrefs();
    try {
      prefs.loadFromFile();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    initialize();
    loadImages();
    scanForJDKs();
    updateControls();
  }

  private JTextArea logTextArea;
  private JTextField maxMemField;
  private JComboBox jdkCmb;
  private JPanel mainPanel;
  private JPanel imgDisplayPanel;
  private JTabbedPane mainTabbedPane;

  private void loadImages() {
    frame.setTitle("Welcome to " + Tools.APP_TITLE);
    // Load logo
    try {
      SimpleImage img = getImage("logo.png");
      northPanel.setLayout(null);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setPreferredSize(new Dimension(img.getImageWidth(), img.getImageHeight()));
      imgPanel.setLocation(110, 0);
      getNorthPanel().add(imgPanel);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    try {
      final String[] imageFilenames = { "image1.jpg", "image2.jpg" };
      String imageFilename = imageFilenames[(int) (Math.random() * imageFilenames.length)];
      SimpleImage img = getImage(imageFilename);
      imgDisplayPanel.setLayout(null);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      getImgDisplayPanel().add(imgPanel);
      imgPanel.setLayout(null);
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  private void scanForJDKs() {
    JDKScanner scanner = new JDKScanner();
    scanner.scan();
    getJdkCmb().removeAllItems();
    List<String> jdks = scanner.getJDKs();
    if (jdks != null && jdks.size() > 0) {
      for (String jdk : jdks) {
        getJdkCmb().addItem(jdk);
      }
      getJdkCmb().setSelectedItem(scanner.getDefaultJDK());
    }
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.setBackground(Color.BLACK);
    frame.getContentPane().setBackground(Color.BLACK);
    frame.setResizable(false);
    {
      frame.setBounds(100, 100, 551, 540);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    northPanel = new JPanel();
    northPanel.setBackground(Color.BLACK);
    northPanel.setPreferredSize(new Dimension(0, 60));
    frame.getContentPane().add(northPanel, BorderLayout.NORTH);

    centerPanel = new JPanel();
    centerPanel.setBackground(Color.BLACK);
    frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

    centerPanel.setLayout(new BorderLayout(0, 0));
    mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
    mainTabbedPane.setBackground(Color.BLACK);
    centerPanel.add(mainTabbedPane, BorderLayout.CENTER);

    JPanel startPanel_1 = new JPanel();
    startPanel_1.setForeground(SystemColor.menu);
    startPanel_1.setBackground(Color.BLACK);
    mainTabbedPane.addTab("Start", null, startPanel_1, null);
    startPanel_1.setLayout(new BorderLayout(0, 0));

    JPanel panel_2 = new JPanel();
    panel_2.setBackground(Color.BLACK);
    panel_2.setPreferredSize(new Dimension(10, 60));
    startPanel_1.add(panel_2, BorderLayout.SOUTH);

    launchButton = new JButton("Start");
    panel_2.add(launchButton);
    launchButton.setBounds(42, 76, 128, 48);
    launchButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        launchAction();
      }
    });
    launchButton.setPreferredSize(new Dimension(128, 48));
    launchButton.setForeground(SystemColor.menu);
    launchButton.setBorderPainted(false);
    launchButton.setBackground(Color.BLACK);

    mainPanel = new JPanel();
    mainPanel.setBackground(Color.BLACK);
    startPanel_1.add(mainPanel, BorderLayout.CENTER);
    mainPanel.setLayout(null);

    maxMemField = new JTextField();
    maxMemField.setBounds(141, 38, 92, 22);
    mainPanel.add(maxMemField);
    maxMemField.setText("1024");
    maxMemField.setPreferredSize(new Dimension(56, 22));
    maxMemField.setFont(new Font("Dialog", Font.PLAIN, 10));

    JLabel lblMb = new JLabel();
    lblMb.setBounds(234, 38, 30, 22);
    mainPanel.add(lblMb);
    lblMb.setForeground(Color.LIGHT_GRAY);
    lblMb.setText("MB");
    lblMb.setPreferredSize(new Dimension(94, 22));
    lblMb.setFont(new Font("Dialog", Font.BOLD, 10));

    JLabel lblMemoryToUse = new JLabel();
    lblMemoryToUse.setBounds(19, 38, 107, 22);
    mainPanel.add(lblMemoryToUse);
    lblMemoryToUse.setForeground(Color.LIGHT_GRAY);
    lblMemoryToUse.setText("Memory to use");
    lblMemoryToUse.setPreferredSize(new Dimension(94, 22));
    lblMemoryToUse.setFont(new Font("Dialog", Font.BOLD, 10));

    JButton btnAddJavaRuntime = new JButton("Add Java runtime...");
    btnAddJavaRuntime.setBounds(340, 32, 172, 28);
    mainPanel.add(btnAddJavaRuntime);
    btnAddJavaRuntime.setPreferredSize(new Dimension(128, 28));
    btnAddJavaRuntime.setForeground(SystemColor.menu);
    btnAddJavaRuntime.setBorderPainted(false);
    btnAddJavaRuntime.setBackground(Color.BLACK);

    jdkCmb = new JComboBox();
    jdkCmb.setBounds(142, 6, 371, 22);
    mainPanel.add(jdkCmb);
    jdkCmb.setForeground(SystemColor.menu);
    jdkCmb.setBackground(Color.BLACK);
    jdkCmb.setPreferredSize(new Dimension(125, 22));
    jdkCmb.setMaximumRowCount(32);
    jdkCmb.setFont(new Font("Dialog", Font.BOLD, 10));

    JLabel lblJavaRuntimeTo = new JLabel();
    lblJavaRuntimeTo.setBounds(19, 6, 107, 22);
    mainPanel.add(lblJavaRuntimeTo);
    lblJavaRuntimeTo.setForeground(Color.LIGHT_GRAY);
    lblJavaRuntimeTo.setText("Java runtime to use");
    lblJavaRuntimeTo.setPreferredSize(new Dimension(94, 22));
    lblJavaRuntimeTo.setFont(new Font("Dialog", Font.BOLD, 10));

    JPanel logPanel = new JPanel();
    logPanel.setBackground(Color.BLACK);
    logPanel.setForeground(SystemColor.menu);
    mainTabbedPane.addTab("Message log", null, logPanel, null);
    logPanel.setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    panel.setBackground(Color.BLACK);
    panel.setPreferredSize(new Dimension(10, 38));
    logPanel.add(panel, BorderLayout.SOUTH);

    JButton toClipboardBtn = new JButton("To Clipboard");
    toClipboardBtn.setPreferredSize(new Dimension(128, 28));
    toClipboardBtn.setForeground(SystemColor.menu);
    toClipboardBtn.setBorderPainted(false);
    toClipboardBtn.setBackground(Color.BLACK);
    panel.add(toClipboardBtn);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBackground(Color.DARK_GRAY);
    logPanel.add(scrollPane, BorderLayout.CENTER);

    logTextArea = new JTextArea();
    logTextArea.setForeground(Color.LIGHT_GRAY);
    logTextArea.setEditable(false);
    logTextArea.setBackground(Color.BLACK);
    scrollPane.setViewportView(logTextArea);

    imgDisplayPanel = new JPanel();
    imgDisplayPanel.setBackground(Color.BLACK);
    imgDisplayPanel.setBounds(29, 86, 486, 270);
    mainPanel.add(imgDisplayPanel);

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

  private void launchAction() {
    getLaunchButton().setEnabled(false);
    try {
      savePrefs();
      launchApp();
      System.exit(0);
    }
    catch (Throwable ex) {
      ex.printStackTrace();
      getMainTabbedPane().setSelectedIndex(1);
      getLaunchButton().setEnabled(true);
      try {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(os));
        os.flush();
        os.close();
        getLogTextArea().setText(new String(os.toByteArray()));
        getLogTextArea().select(0, 0);
      }
      catch (Exception ex2) {
        ex2.printStackTrace();
      }
    }
  }

  private void savePrefs() throws Exception {
    prefs.setJavaPath((String) getJdkCmb().getSelectedItem());
    prefs.setMaxMem(Integer.parseInt(getMaxMemField().getText()));
    prefs.saveToFromFile();
  }

  public JButton getLaunchButton() {
    return launchButton;
  }

  public JPanel getNorthPanel() {
    return northPanel;
  }

  public JTextArea getLogTextArea() {
    return logTextArea;
  }

  private void updateControls() {
    getMainTabbedPane().setSelectedIndex(0);
    if (prefs.getJavaPath() != null && prefs.getJavaPath().length() > 0) {
      getJdkCmb().setSelectedItem(prefs.getJavaPath());
    }
    if (prefs.getMaxMem() > 0) {
      getMaxMemField().setText(String.valueOf(prefs.getMaxMem()));
    }
  }

  public JComboBox getJdkCmb() {
    return jdkCmb;
  }

  public JPanel getImgDisplayPanel() {
    return imgDisplayPanel;
  }

  public JTextField getMaxMemField() {
    return maxMemField;
  }

  public JTabbedPane getMainTabbedPane() {
    return mainTabbedPane;
  }

  private void launchApp() throws Exception {
    // TODO Auto-generated method stub

  }

}
