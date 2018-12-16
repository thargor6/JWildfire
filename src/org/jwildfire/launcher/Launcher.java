/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.swing.StandardDialogs;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class Launcher {

  private JFrame frame;
  private JPanel centerPanel;
  private JButton launchButton;
  private JPanel northPanel;
  private LauncherPrefs prefs;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
          }
          catch(Exception ex) {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
          }
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
    checkForIncludedJRE();
    updateControls();
  }

  private void checkForIncludedJRE() {
    String jdk = locateIncludedJRE();
    if (jdk != null && jdk.length() > 0) {
      getJdkCmb().removeAllItems();
      getJdkCmb().addItem(jdk);
      getJdkCmb().setSelectedIndex(0);
      //      getJdkCmb().setEnabled(false);
      //      getBtnAddJavaRuntime().setEnabled(false);
    }
  }

  private String locateIncludedJRE() {
    try {
      String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
      String decodedPath = URLDecoder.decode(path, "UTF-8");
      if (decodedPath.endsWith(".jar")) {
        File jrePath = new File(new File(decodedPath).getParentFile(), "jre");
        if (jrePath.exists() && jrePath.isDirectory()) {
          getLogTextArea().setText("Included JRE found in \"" + jrePath.getAbsolutePath() + "\"");
          return jrePath.getAbsolutePath();
        }
      }
      getLogTextArea().setText("No JRE found in \"" + decodedPath + "\"");
      return null;
    }
    catch (Exception ex) {
      return null;
    }
  }

  private JTextArea logTextArea;
  private JTextField maxMemField;
  private JComboBox jdkCmb;
  private JPanel mainPanel;
  private JPanel imgDisplayPanel;
  private JTabbedPane mainTabbedPane;
  private JCheckBox debugCmb;
  private JButton btnAddJavaRuntime;
  private JCheckBox lowPriorityCBx;
  private JTextField uiScaleEdit;

  private void loadImages() {
    frame.setTitle("Welcome to " + Tools.APP_TITLE + " " + Tools.APP_VERSION);
    // Load logo
    try {
      SimpleImage img = getImage(Tools.SPECIAL_VERSION ? "logo_special.png" : "logo.png");
      northPanel.setLayout(null);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setPreferredSize(new Dimension(img.getImageWidth(), img.getImageHeight()));
      imgPanel.setLocation(Tools.SPECIAL_VERSION ? 7 : 107, 4);
      getNorthPanel().add(imgPanel);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    String imageFilename = "";
    try {
      if (Tools.SPECIAL_VERSION) {
        final int IMG_COUNT = 3;
        int imageIdx = (int) (Math.random() * IMG_COUNT) + 1;
        String id = String.valueOf(imageIdx);
        while (id.length() < 3) {
          id = "0" + id;
        }
        imageFilename = "special" + id + ".jpg";
      }
      else {
        final int IMG_COUNT = 103;
        int imageIdx = Tools.randomInt(IMG_COUNT) + 1;
        String id = String.valueOf(imageIdx);
        while (id.length() < 3) {
          id = "0" + id;
        }
        imageFilename = "image" + id + ".jpg";
      }
      SimpleImage img = getImage(imageFilename);
      imgDisplayPanel.setLayout(null);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      getImgDisplayPanel().add(imgPanel);

      //      System.out.println(imgDisplayPanel.getSize().height + " " + imgDisplayPanel.getSize().width);
      imgPanel.setLayout(null);
    }
    catch (Throwable ex) {
      System.out.println(imageFilename);
      ex.printStackTrace();
    }
  }

  private void scanForJDKs() {
    JDKScanner scanner = new JDKScanner();
    scanner.scan();
    getJdkCmb().removeAllItems();
    List<String> jdks = scanner.getJDKs();
    scanner.addSafeJDK(prefs.getJavaPath());
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
      frame.setBounds(100, 100, 551, 558);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    northPanel = new JPanel();
    northPanel.setBackground(Color.BLACK);
    northPanel.setPreferredSize(new Dimension(0, 78));
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
    panel_2.setLayout(null);

    launchButton = new JButton("Start");
    panel_2.add(launchButton);
    launchButton.setBounds(220, 5, 128, 48);
    launchButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        launchAction();
      }
    });
    launchButton.setPreferredSize(new Dimension(128, 48));
    launchButton.setForeground(SystemColor.menu);
    launchButton.setBorderPainted(false);
    launchButton.setBackground(Color.BLACK);

    debugCmb = new JCheckBox("Debug");
    debugCmb.setBounds(59, 20, 144, 18);
    debugCmb.setToolTipText("Trace the launching process and record messages");
    debugCmb.setForeground(SystemColor.menu);
    debugCmb.setBackground(Color.BLACK);
    panel_2.add(debugCmb);

    lowPriorityCBx = new JCheckBox("Low priority");
    lowPriorityCBx.setToolTipText("Launch the application with low process-priority (recommended, but currently only works with Windows)");
    lowPriorityCBx.setForeground(SystemColor.menu);
    lowPriorityCBx.setBackground(Color.BLACK);
    lowPriorityCBx.setBounds(404, 35, 135, 18);
    panel_2.add(lowPriorityCBx);

    JLabel label = new JLabel();
    label.setText("UI scale");
    label.setPreferredSize(new Dimension(94, 22));
    label.setForeground(Color.LIGHT_GRAY);
    label.setFont(new Font("Dialog", Font.BOLD, 10));
    label.setBounds(389, 5, 49, 22);
    panel_2.add(label);

    uiScaleEdit = new JTextField();
    uiScaleEdit.setToolTipText("Scales up the UI, works best with Java9");
    uiScaleEdit.setText("1");
    uiScaleEdit.setPreferredSize(new Dimension(56, 22));
    uiScaleEdit.setFont(new Font("Dialog", Font.PLAIN, 10));
    uiScaleEdit.setBounds(438, 5, 56, 22);
    panel_2.add(uiScaleEdit);

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

    btnAddJavaRuntime = new JButton("Add Java runtime...");
    btnAddJavaRuntime.setToolTipText("Manually add the path of a Java runtime which is installed on this system and was not detected by the launcher");
    btnAddJavaRuntime.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        manualAddRuntime();
      }
    });
    btnAddJavaRuntime.setBounds(276, 34, 172, 28);
    mainPanel.add(btnAddJavaRuntime);
    btnAddJavaRuntime.setPreferredSize(new Dimension(128, 28));
    btnAddJavaRuntime.setForeground(SystemColor.menu);
    btnAddJavaRuntime.setBorderPainted(false);
    btnAddJavaRuntime.setBackground(Color.BLACK);

    jdkCmb = new JComboBox();
    jdkCmb.setBounds(142, 6, 384, 22);
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
    toClipboardBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(getLogTextArea().getText());
        clipboard.setContents(data, data);
      }
    });
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
    imgDisplayPanel.setBounds(20, 86, 500, 270);
    mainPanel.add(imgDisplayPanel);

    JButton btnReset = new JButton("Reset");
    btnReset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        resetToDefaultsBtn_clicked();
      }
    });
    btnReset.setToolTipText("Rest all laucher settings to defaults (may be useful after system upgrade)");
    btnReset.setPreferredSize(new Dimension(128, 28));
    btnReset.setForeground(SystemColor.menu);
    btnReset.setBorderPainted(false);
    btnReset.setBackground(Color.BLACK);
    btnReset.setBounds(460, 34, 66, 28);
    mainPanel.add(btnReset);

  }

  protected void resetToDefaultsBtn_clicked() {
    if (StandardDialogs.confirm(mainPanel, "Do you really want to reset the launcher-settings to default-values?")) {
      try {
        LauncherPrefsWriter.deletePrefs();
        prefs = new LauncherPrefs();
        prefs.loadFromFile();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      scanForJDKs();
      updateControls();
    }
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
      validatePrefs();
      launchApp();
      if (!getDebugCbx().isSelected()) {
        System.exit(0);
      }
    }
    catch (Throwable ex) {
      getLaunchButton().setEnabled(true);
      handleError(ex);
    }
  }

  private void validatePrefs() {
    if (prefs.getMaxMem() > 1024 && prefs.getJavaPath() != null && prefs.getJavaPath().contains("(x86)")) {
      throw new RuntimeException("You seem to use a 32 BIT Java runtime, which can not\nuse more than 1 GB memory. Either please use a 64 BIT java or decrease the\namount of memory.\n\n\n");
    }
  }

  private void handleError(Throwable ex) {
    ex.printStackTrace();
    getMainTabbedPane().setSelectedIndex(1);
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ex.printStackTrace(new PrintStream(os));
      os.flush();
      os.close();
      getLogTextArea().setText(new String(os.toByteArray()) + "\n" + getLogTextArea().getText());
      getLogTextArea().select(0, 0);
    }
    catch (Exception ex2) {
      ex2.printStackTrace();
    }
  }

  private void savePrefs() throws Exception {
    prefs.setJavaPath((String) getJdkCmb().getSelectedItem());
    prefs.setMaxMem(Integer.parseInt(getMaxMemField().getText()));
    prefs.setLowPriority(getLowPriorityCBx().isSelected());
    prefs.setUiScale(Double.parseDouble(getUiScaleEdit().getText()));
    prefs.saveToFile();
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
    if (AppLauncher.isWindows()) {
      getLowPriorityCBx().setEnabled(true);
      getLowPriorityCBx().setSelected(prefs.isLowPriority());
    }
    else {
      getLowPriorityCBx().setEnabled(false);
      getLowPriorityCBx().setSelected(false);
    }
    getUiScaleEdit().setText(Tools.doubleToString(prefs.getUiScale()));
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
    AppLauncher launcher = new AppLauncher(prefs);
    String launchCmd[] = launcher.getLaunchCmd();
    getLogTextArea().setText("Attempting to launch " + Tools.APP_TITLE + " using the command:\n" + expandCmd(launchCmd) + "\n");

    if (getDebugCbx().isSelected()) {

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      int retVal = launcher.launchAsync(launchCmd, os);
      if (retVal != 0) {
        getLogTextArea().setText(new String(os.toByteArray()) + "\n" + getLogTextArea().getText());
        throw new Exception("Return code was " + retVal + " - see below for details:");
      }
    }
    else {
      launcher.launchSync(launchCmd);
    }
  }

  private String expandCmd(String[] pCmd) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < pCmd.length - 1; i++) {
      sb.append(pCmd[i]);
      sb.append(" ");
    }
    sb.append(pCmd[pCmd.length - 1]);
    return sb.toString();
  }

  private void manualAddRuntime() {
    final String NO_JAVA_EXE = "Please select a java executable inside the \"bin\" directory inside your \"jdk\"- or \"jre\"-directory";
    JFileChooser chooser = new JFileChooser();
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      try {
        String path = file.getAbsolutePath();
        // check for "java..."
        int lastSlash = path.lastIndexOf(File.separator);
        if (lastSlash < 0) {
          throw new Exception(NO_JAVA_EXE);
        }
        String hs = path.substring(lastSlash + 1, path.length());
        if (hs.indexOf("java") != 0) {
          throw new Exception(NO_JAVA_EXE);
        }
        // check for "bin"
        path = path.substring(0, lastSlash);
        lastSlash = path.lastIndexOf(File.separator);
        if (lastSlash < 0) {
          throw new Exception(NO_JAVA_EXE);
        }
        hs = path.substring(lastSlash + 1, path.length());
        if (!hs.equals("bin")) {
          throw new Exception(NO_JAVA_EXE);
        }

        //
        String jdkPath = path.substring(0, lastSlash);
        JDKScanner scanner = new JDKScanner();
        scanner.scan();
        scanner.addSafeJDK(jdkPath);

        getJdkCmb().removeAllItems();
        List<String> jdks = scanner.getJDKs();
        for (String jdk : jdks) {
          getJdkCmb().addItem(jdk);
        }
        getJdkCmb().setSelectedIndex(scanner.getSafeJDKIndex(jdkPath));
        savePrefs();
      }
      catch (Throwable ex) {
        handleError(ex);
      }
    }

  }

  public JCheckBox getDebugCbx() {
    return debugCmb;
  }

  public JButton getBtnAddJavaRuntime() {
    return btnAddJavaRuntime;
  }

  public JCheckBox getLowPriorityCBx() {
    return lowPriorityCBx;
  }

  public JTextField getUiScaleEdit() {
    return uiScaleEdit;
  }
}
