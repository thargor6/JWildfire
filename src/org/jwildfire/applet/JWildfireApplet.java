/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.applet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.create.tina.render.backdrop.FlameBackdropHandler;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.IterationObserver;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderThreads;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class JWildfireApplet extends JApplet implements IterationObserver {
  private static final long serialVersionUID = 1L;

  private enum State {
    IDLE, RENDER
  }

  private static class ImgSize {
    private final int width;
    private final int height;

    public ImgSize(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

  }

  private State state = State.IDLE;
  private RenderThreads threads;
  private FlameRenderer renderer;
  private final Prefs prefs = Prefs.getPrefs();
  private Flame currFlame;
  private JScrollPane imageScrollPane;
  private SimpleImage image;
  private long renderStartTime = 0;
  private long pausedRenderTime = 0;
  private JPanel imgMainPnl;
  private long[] iterationCount;

  public JWildfireApplet() {

    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.NORTH);

    JPanel panel_1 = new JPanel();
    panel_1.setPreferredSize(new Dimension(10, 42));
    getContentPane().add(panel_1, BorderLayout.SOUTH);

    JButton btnNextnormal = new JButton();
    btnNextnormal.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nextButton_clicked(RandomBatchQuality.NORMAL);
      }
    });
    btnNextnormal.setToolTipText("Cancel render, generate new random fractal and start render");
    btnNextnormal.setText("Create next");
    btnNextnormal.setPreferredSize(new Dimension(125, 32));
    btnNextnormal.setMnemonic(KeyEvent.VK_D);
    btnNextnormal.setMaximumSize(new Dimension(32000, 32000));
    btnNextnormal.setFont(new Font("Dialog", Font.BOLD, 10));
    btnNextnormal.setAlignmentX(0.5f);
    panel_1.add(btnNextnormal);

    JButton button_1 = new JButton();
    button_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          copyToClipboardBtn_clicked();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    button_1.setToolTipText("Copy the current fractal into the clipboard");
    button_1.setText("To Clipboard");
    button_1.setPreferredSize(new Dimension(125, 32));
    button_1.setMnemonic(KeyEvent.VK_D);
    button_1.setMinimumSize(new Dimension(100, 24));
    button_1.setMaximumSize(new Dimension(32000, 24));
    button_1.setFont(new Font("Dialog", Font.BOLD, 10));
    panel_1.add(button_1);

    imgMainPnl = new JPanel();
    getContentPane().add(imgMainPnl, BorderLayout.CENTER);
  }

  protected void copyToClipboardBtn_clicked() throws Exception {
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      String xml = new FlameWriter().getFlameXML(currFlame);
      StringSelection data = new StringSelection(xml);
      clipboard.setContents(data, data);
    }
  }

  protected void nextButton_clicked(RandomBatchQuality pQuality) {
    cancelRender();
    genRandomFlame(pQuality);
    renderButton_clicked();
    enableControls();
  }

  private void cancelRender() {
    if (state == State.RENDER) {
      while (true) {
        boolean done = true;
        for (AbstractRenderThread thread : threads.getRenderThreads()) {
          if (!thread.isFinished()) {
            done = false;
            thread.cancel();
            try {
              Thread.sleep(1);
            }
            catch (InterruptedException e) {
              e.printStackTrace();
            }
            break;
          }
        }
        if (done) {
          break;
        }
      }
      state = State.IDLE;
    }
  }

  public void genRandomFlame(RandomBatchQuality pQuality) {
    final int IMG_WIDTH = 80;
    final int IMG_HEIGHT = 60;
    RandomFlameGenerator randGen = new AllRandomFlameGenerator();
    int palettePoints = 3 + Tools.randomInt(21);
    boolean fadePaletteColors = Math.random() > 0.09;
    boolean uniformSize = Math.random() > 0.75;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, RandomSymmetryGeneratorList.SPARSE, RandomGradientGeneratorList.DEFAULT, RandomWeightingFieldGeneratorList.SPARSE, palettePoints, fadePaletteColors, uniformSize, pQuality);
    currFlame = sampler.createSample().getFlame();
  }

  public void renderButton_clicked() {
    clearScreen();
    ImgSize size = getImgSize();
    RenderInfo info = new RenderInfo(size.getWidth(), size.getHeight(), RenderMode.PREVIEW);
    Flame flame = getCurrFlame();
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    flame.setSampleDensity(10);
    info.setRenderHDR(false);
    info.setRenderZBuffer(false);

    new FlameBackdropHandler(flame).fillBackground(image);

    initRender(prefs.getTinaRenderThreads());
    renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
    renderer.registerIterationObserver(this);
    renderStartTime = System.currentTimeMillis();
    pausedRenderTime = 0;
    threads = renderer.startRenderFlame(info);
    state = State.RENDER;
    enableControls();
  }

  private ImgSize getImgSize() {
    return new ImgSize(getImgMainPnl().getSize().width, getImgMainPnl().getSize().height);
  }

  private void clearScreen() {
    try {
      int scrollX = (image.getImageWidth() - (int) getImgMainPnl().getBounds().getWidth()) / 2;
      if (scrollX > 0)
        imageScrollPane.getHorizontalScrollBar().setValue(scrollX);
      int scrollY = (image.getImageHeight() - (int) getImgMainPnl().getBounds().getHeight()) / 2;
      if (scrollY > 0)
        imageScrollPane.getVerticalScrollBar().setValue(scrollY);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    getImgMainPnl().repaint();
  }

  private void refreshImagePanel() {
    if (imageScrollPane != null) {
      getImgMainPnl().remove(imageScrollPane);
      imageScrollPane = null;
    }
    ImgSize size = getImgSize();
    image = new SimpleImage(size.getWidth(), size.getHeight());
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    ImagePanel imagePanel = new ImagePanel(image, 0, 0, image.getImageWidth());
    imagePanel.setSize(image.getImageWidth(), image.getImageHeight());
    imagePanel.setPreferredSize(new Dimension(image.getImageWidth(), image.getImageHeight()));

    imageScrollPane = new JScrollPane(imagePanel);
    imageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    getImgMainPnl().add(imageScrollPane, BorderLayout.CENTER);

    getImgMainPnl().getParent().validate();
  }

  public Flame getCurrFlame() {
    return currFlame;
  }

  public void enableControls() {
    //    saveImageButton.setEnabled(image != null);
    //    stopButton.setEnabled(state == State.RENDER);
    //    pauseButton.setEnabled(state == State.RENDER);
    //    resumeButton.setEnabled(state != State.RENDER);
  }

  @Override
  public void notifyIterationFinished(AbstractRenderThread pEventSource, int pPlotX, int pPlotY, XYZProjectedPoint pProjectedPoint, double pX, double pY, double pZ, double pColorRed, double pColorGreen, double pColorBlue) {
    int x = pPlotX / pEventSource.getOversample();
    int y = pPlotY / pEventSource.getOversample();
    iterationCount[pEventSource.getThreadId()] = pEventSource.getCurrSample();
    long iteration = calculateSampleCount();
    if (x >= 0 && x < image.getImageWidth() && y >= 0 && y < image.getImageHeight()) {
      image.setARGB(x, y, pEventSource.getTonemapper().tonemapSample(x, y));
      if (iteration % 2000 == 0) {
        updateImage();
      }
      if (iteration % 10000 == 0) {
        double quality = pEventSource.getTonemapper().calcDensity(iteration);
        updateStats(pEventSource, quality);
        pEventSource.getTonemapper().setDensity(quality);
      }
    }
  }

  private synchronized void updateImage() {
    getImgMainPnl().repaint();
  }

  private synchronized void updateStats(AbstractRenderThread pEventSource, double pQuality) {
    //    statsTextArea.setText("Current quality: " + Tools.doubleToString(pQuality) + "\n" +
    //        "samples so far: " + sampleCount + "\n" +
    //        "render time: " + Tools.doubleToString((System.currentTimeMillis() - renderStartTime + pausedRenderTime) / 1000.0) + "s");
    //    statsTextArea.validate();
  }

  public JPanel getImgMainPnl() {
    return imgMainPnl;
  }

  @Override
  public void start() {
    refreshImagePanel();
    super.start();
    nextButton_clicked(RandomBatchQuality.NORMAL);
  }

  @Override
  public void stop() {
    cancelRender();
    super.stop();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {

      public void run() {
        JFrame frame = new JFrame(Tools.APP_TITLE + "Applet" + " " + Tools.getAppVersion());
        JWildfireApplet applet = new JWildfireApplet();
        frame.getContentPane().add(applet);
        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        applet.start();
      }

    });

  }

  private long calculateSampleCount() {
    long res = 0;
    for (int i = 0; i < iterationCount.length; i++) {
      res += iterationCount[i];
    }
    return res;
  }

  public void initRender(int pThreadGroupSize) {
    iterationCount = new long[pThreadGroupSize];
  }
}
