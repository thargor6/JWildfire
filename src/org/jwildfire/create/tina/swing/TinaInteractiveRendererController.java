/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.IterationObserver;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderThreads;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.ResumedFlameRender;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;

public class TinaInteractiveRendererController implements IterationObserver {
  private enum State {
    IDLE, RENDER
  }

  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JButton loadFlameButton;
  private final JButton fromClipboardButton;
  private final JButton nextButton;
  private final JButton stopButton;
  private final JButton toClipboardButton;
  private final JButton saveImageButton;
  private final JButton saveZBufferButton;
  private final JCheckBox autoLoadImageCBx;
  private final JButton saveFlameButton;
  private final JComboBox randomStyleCmb;
  private final JToggleButton halveSizeButton;
  private final JToggleButton quarterSizeButton;
  private final JToggleButton fullSizeButton;
  private final JComboBox interactiveResolutionProfileCmb;
  private final JButton pauseButton;
  private final JButton resumeButton;
  private final JPanel imageRootPanel;
  private JScrollPane imageScrollPane;
  private final JTextArea statsTextArea;
  private final JToggleButton showStatsButton;
  private final JToggleButton showPreviewButton;
  private SimpleImage image;
  private Flame currFlame;
  private RenderThreads threads;
  private UpdateDisplayThread updateDisplayThread;
  private FlameRenderer renderer;
  private State state = State.IDLE;
  private final QuickSaveFilenameGen qsaveFilenameGen;
  private InteractiveRendererDisplayUpdater displayUpdater = new EmptyInteractiveRendererDisplayUpdater();
  private boolean refreshing = false;

  public TinaInteractiveRendererController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs,
      JButton pLoadFlameButton, JButton pFromClipboardButton, JButton pNextButton,
      JButton pStopButton, JButton pToClipboardButton, JButton pSaveImageButton, JButton pSaveZBufferButton,
      JCheckBox pAutoLoadImageCBx, JButton pSaveFlameButton,
      JComboBox pRandomStyleCmb, JPanel pImagePanel, JTextArea pStatsTextArea, JToggleButton pHalveSizeButton,
      JToggleButton pQuarterSizeButton, JToggleButton pFullSizeButton, JComboBox pInteractiveResolutionProfileCmb, JButton pPauseButton, JButton pResumeButton,
      JToggleButton pShowStatsButton, JToggleButton pShowPreviewButton) {
    parentCtrl = pParentCtrl;
    prefs = pPrefs;
    errorHandler = pErrorHandler;
    qsaveFilenameGen = new QuickSaveFilenameGen(prefs);

    loadFlameButton = pLoadFlameButton;
    fromClipboardButton = pFromClipboardButton;
    nextButton = pNextButton;
    stopButton = pStopButton;
    toClipboardButton = pToClipboardButton;
    saveImageButton = pSaveImageButton;
    saveZBufferButton = pSaveZBufferButton;
    autoLoadImageCBx = pAutoLoadImageCBx;
    saveFlameButton = pSaveFlameButton;
    randomStyleCmb = pRandomStyleCmb;
    halveSizeButton = pHalveSizeButton;
    quarterSizeButton = pQuarterSizeButton;
    fullSizeButton = pFullSizeButton;

    interactiveResolutionProfileCmb = pInteractiveResolutionProfileCmb;
    imageRootPanel = pImagePanel;
    pauseButton = pPauseButton;
    resumeButton = pResumeButton;
    showStatsButton = pShowStatsButton;
    showPreviewButton = pShowPreviewButton;
    // interactiveResolutionProfileCmb must be already filled here!
    refreshImagePanel();
    statsTextArea = pStatsTextArea;
    state = State.IDLE;
    //genRandomFlame();
    enableControls();
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) interactiveResolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private void refreshImagePanel() {
    if (imageScrollPane != null) {
      imageRootPanel.remove(imageScrollPane);
      imageScrollPane = null;
    }
    ResolutionProfile profile = getResolutionProfile();
    int width = profile.getWidth();
    int height = profile.getHeight();
    if (quarterSizeButton.isSelected()) {
      width /= 4;
      height /= 4;
    }
    else if (halveSizeButton.isSelected()) {
      width /= 2;
      height /= 2;
    }
    image = new SimpleImage(width, height);
    image.getBufferedImg().setAccelerationPriority(1.0f);
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    ImagePanel imagePanel = new ImagePanel(image, 0, 0, image.getImageWidth());
    imagePanel.setSize(image.getImageWidth(), image.getImageHeight());
    imagePanel.setPreferredSize(new Dimension(image.getImageWidth(), image.getImageHeight()));

    imageScrollPane = new JScrollPane(imagePanel);
    imageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    imageRootPanel.add(imageScrollPane, BorderLayout.CENTER);

    imageRootPanel.getParent().validate();
  }

  public void enableControls() {
    saveImageButton.setEnabled(image != null);
    saveZBufferButton.setEnabled(image != null && getCurrFlame() != null && getCurrFlame().getSolidRenderSettings().isSolidRenderingEnabled());
    stopButton.setEnabled(state == State.RENDER);
    pauseButton.setEnabled(state == State.RENDER);
    resumeButton.setEnabled(state != State.RENDER);
  }

  public void genRandomFlame() {
    final int IMG_WIDTH = 80;
    final int IMG_HEIGHT = 60;

    RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance((String) randomStyleCmb.getSelectedItem(), true);
    int palettePoints = 3 + Tools.randomInt(68);
    boolean fadePaletteColors = Math.random() > 0.33;
    boolean uniformWidth = Math.random() > 0.75;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, RandomSymmetryGeneratorList.SPARSE, RandomGradientGeneratorList.DEFAULT, RandomWeightingFieldGeneratorList.SPARSE, palettePoints, fadePaletteColors, uniformWidth, RandomBatchQuality.HIGH);
    currFlame = sampler.createSample().getFlame();
    storeCurrFlame();
  }

  private void storeCurrFlame() {
    if (currFlame != null) {
      try {
        String filename = qsaveFilenameGen.generateFilename("jwf_ir_current.flame");
        if (filename != null) {
          new FlameWriter().writeFlame(currFlame, filename);
        }
      }
      catch (Exception ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void fromClipboardButton_clicked() {
    Flame newFlame = null;
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new FlameReader(prefs).readFlamesfromXML(xml);
          if (flames.size() > 0) {
            newFlame = flames.get(0);
          }
        }
      }
      if (newFlame == null) {
        throw new Exception("There is currently no valid flame in the clipboard");
      }
      else {
        currFlame = newFlame;
        storeCurrFlame();
        cancelRender();
        setupProfiles(currFlame);
        renderButton_clicked();
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void setupProfiles(Flame pFlame) {
    if (prefs.isTinaAssociateProfilesWithFlames()) {
      if (pFlame.getResolutionProfile() != null) {
        ResolutionProfile profile = null;
        for (int i = 0; i < interactiveResolutionProfileCmb.getItemCount(); i++) {
          profile = (ResolutionProfile) interactiveResolutionProfileCmb.getItemAt(i);
          if (pFlame.getResolutionProfile().equals(profile.toString()))
            break;
          else
            profile = null;
        }
        if (profile != null) {
          interactiveResolutionProfileCmb.setSelectedItem(profile);
        }
      }
    }
  }

  public void loadFlameButton_clicked() {
    try {
      JFileChooser chooser = new FlameFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
        Flame newFlame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        currFlame = newFlame;
        importFlame(newFlame);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(Flame flame) {
    currFlame = flame.makeCopy();
    storeCurrFlame();
    cancelRender();
    setupProfiles(currFlame);
    renderButton_clicked();
    enableControls();
  }

  public void renderButton_clicked() {
    try {
      clearScreen();
      ResolutionProfile resProfile = getResolutionProfile();
      int width = resProfile.getWidth();
      int height = resProfile.getHeight();
      if (quarterSizeButton.isSelected()) {
        width /= 4;
        height /= 4;
      }
      else if (halveSizeButton.isSelected()) {
        width /= 2;
        height /= 2;
      }
      RenderInfo info = new RenderInfo(width, height, RenderMode.INTERACTIVE);
      Flame flame = getCurrFlame();
      if (!Stereo3dMode.NONE.equals(flame.getStereo3dMode())) {
        throw new Exception("Stereo3d-rendering isn't currently supported in the interactive-renderer. Please use the editor or the batch-renderer to create stereo3d-images");
      }

      double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
      double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
      flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
      flame.setWidth(info.getImageWidth());
      flame.setHeight(info.getImageHeight());
      flame.setSampleDensity(10);
      info.setRenderHDR(prefs.isTinaSaveHDRInIR());
      info.setRenderZBuffer(false);
      if (flame.getBgColorRed() > 0 || flame.getBgColorGreen() > 0 || flame.getBgColorBlue() > 0) {
        image.fillBackground(flame.getBgColorRed(), flame.getBgColorGreen(), flame.getBgColorBlue());
      }
      renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
      renderer.registerIterationObserver(this);
      displayUpdater = createDisplayUpdater();
      renderStartTime = System.currentTimeMillis();
      pausedRenderTime = 0;
      lastQuality = 0.0;
      lastQualitySpeed = 0.0;
      lastQualityTime = 0;
      displayUpdater.initRender(prefs.getTinaRenderThreads());
      threads = renderer.startRenderFlame(info);
      for (Thread t : threads.getExecutingThreads()) {
        t.setPriority(Thread.MIN_PRIORITY);
      }
      updateDisplayThread = new UpdateDisplayThread();
      startRenderThread(updateDisplayThread);

      state = State.RENDER;
      enableControls();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private Thread startRenderThread(Runnable runnable) {
    Thread thread = new Thread(runnable);
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();
    return thread;
  }

  private Thread startDisplayThread(Runnable runnable) {
    Thread thread = new Thread(runnable);
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.start();
    return thread;
  }

  private InteractiveRendererDisplayUpdater createDisplayUpdater() {
    return prefs.isTinaOptimizedRenderingIR() ? new BufferedInteractiveRendererDisplayUpdater(imageRootPanel, image, showPreviewButton.isSelected()) : new DefaultInteractiveRendererDisplayUpdater(imageRootPanel, image, showPreviewButton.isSelected());
  }

  public void stopButton_clicked() {
    cancelRender();
    enableControls();
  }

  public boolean isRendering() {
    return state == State.RENDER;
  }

  private void cancelRender() {
    if (state == State.RENDER) {
      if (updateDisplayThread != null) {
        updateDisplayThread.cancel();
      }
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
          }
        }
        if (done) {
          break;
        }
      }
      if (updateDisplayThread != null) {
        while (!updateDisplayThread.isFinished()) {
          try {
            updateDisplayThread.cancel();
            Thread.sleep(1);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        updateDisplayThread = null;
      }

      state = State.IDLE;
    }
  }

  public void saveZBufferButton_clicked() {
    try {
      pauseRenderThreads();
      try {
        JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
        if (prefs.getOutputImagePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          double zBufferScale = Double.parseDouble(
              JOptionPane.showInputDialog(imageRootPanel,
                  "Enter ZBuffer-Scale", currFlame.getZBufferScale()));
          currFlame.setZBufferScale(zBufferScale);
          RenderedFlame res = renderer.finishZBuffer(displayUpdater.getSampleCount());
          if (res.getZBuffer() != null) {
            new ImageWriter().saveImage(res.getZBuffer(), file.getAbsolutePath());
            if (autoLoadImageCBx.isSelected()) {
              parentCtrl.mainController.loadImage(file.getAbsolutePath(), false);
            }
          }
        }
      }
      finally {
        resumeRenderThreads();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }

  }

  public void saveImageButton_clicked() {
    try {
      pauseRenderThreads();
      try {
        JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
        if (prefs.getOutputImagePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          prefs.setLastOutputImageFile(file);
          RenderedFlame res = renderer.finishRenderFlame(displayUpdater.getSampleCount());
          new ImageWriter().saveImage(res.getImage(), file.getAbsolutePath());
          if (res.getHDRImage() != null) {
            new ImageWriter().saveImage(res.getHDRImage(), Tools.makeHDRFilename(file.getAbsolutePath()));
          }
          if (res.getZBuffer() != null) {
            new ImageWriter().saveImage(res.getZBuffer(), Tools.makeZBufferFilename(file.getAbsolutePath(), currFlame.getZBufferFilename()));
          }
          if (prefs.isTinaSaveFlamesWhenImageIsSaved()) {
            new FlameWriter().writeFlame(getCurrFlame(), file.getParentFile().getAbsolutePath() + File.separator + Tools.trimFileExt(file.getName()) + ".flame");
          }
          if (autoLoadImageCBx.isSelected()) {
            parentCtrl.mainController.loadImage(file.getAbsolutePath(), false);
          }
        }
      }
      finally {
        resumeRenderThreads();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void resumeRenderThreads() {
    if (threads != null && state == State.RENDER) {
      for (Thread t : threads.getExecutingThreads()) {
        t.resume();
      }
    }
  }

  private void pauseRenderThreads() {
    if (threads != null && state == State.RENDER) {
      for (Thread t : threads.getExecutingThreads()) {
        t.suspend();
      }
    }
  }

  public Flame getCurrFlame() {
    return currFlame;
  }

  private final static int STATS_UPDATE_INTERVAL = 1000;
  private final static int INITIAL_IMAGE_UPDATE_INTERVAL = 50;
  private final static int IMAGE_UPDATE_INC_INTERVAL = 25;
  private final static int MAX_IMAGE_UPDATE_INC_INTERVAL = 15000;
  private final static int SLEEP_INTERVAL = 25;

  private class UpdateDisplayThread implements Runnable {
    private long nextImageUpdate;
    private long nextStatsUpdate;
    private long lastImageUpdateInterval;
    private boolean cancelSignalled;
    private boolean finished;

    public UpdateDisplayThread() {
      long time = System.currentTimeMillis();
      nextImageUpdate = time + INITIAL_IMAGE_UPDATE_INTERVAL;
      nextStatsUpdate = time + STATS_UPDATE_INTERVAL;
      lastImageUpdateInterval = INITIAL_IMAGE_UPDATE_INTERVAL;
    }

    @Override
    public void run() {
      {
        AbstractRenderThread thread = threads.getRenderThreads().get(0);
        initImage(thread.getBgRed(), thread.getBgGreen(), thread.getBgBlue(), thread.getBgImagefile());
      }
      finished = cancelSignalled = false;
      try {
        while (!cancelSignalled) {
          try {
            long time = System.currentTimeMillis();
            if (time >= nextImageUpdate) {
              lastImageUpdateInterval += IMAGE_UPDATE_INC_INTERVAL;
              if (lastImageUpdateInterval > MAX_IMAGE_UPDATE_INC_INTERVAL) {
                lastImageUpdateInterval = MAX_IMAGE_UPDATE_INC_INTERVAL;
              }
              updateImage();
              nextImageUpdate = System.currentTimeMillis() + lastImageUpdateInterval;
            }
            if (time >= nextStatsUpdate) {
              double quality = threads.getRenderThreads().get(0).getTonemapper().calcDensity(displayUpdater.getSampleCount());
              updateStats(quality);
              for (AbstractRenderThread thread : threads.getRenderThreads()) {
                thread.getTonemapper().setDensity(quality);
              }
              nextStatsUpdate = System.currentTimeMillis() + STATS_UPDATE_INTERVAL;
            }

            Thread.sleep(SLEEP_INTERVAL);
          }
          catch (Throwable e) {
            e.printStackTrace();
          }
        }
      }
      finally {
        finished = true;
      }
    }

    public void cancel() {
      cancelSignalled = true;
    }

    public boolean isFinished() {
      return finished;
    }

  }

  private long renderStartTime = 0;
  private long pausedRenderTime = 0;
  private int advanceQuality = 500;
  private double lastQuality;
  private double lastQualitySpeed;
  private long lastQualityTime;
  private boolean showStats = true;
  private final DateFormat timeFormat = createTimeFormat();

  private DateFormat createTimeFormat() {
    DateFormat res = new SimpleDateFormat("HH:mm:ss");
    res.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
    return res;
  }

  private void updateStats(double pQuality) {
    if (showStats) {
      StringBuilder sb = new StringBuilder();
      long currTime = System.currentTimeMillis();
      sb.append("Current quality: " + Tools.doubleToString(pQuality) + "\n");
      sb.append("Samples so far: " + displayUpdater.getSampleCount() + "\n\n");
      long renderTime = currTime - renderStartTime + pausedRenderTime;
      if (lastQuality > 0) {
        double qualitySpeed = (pQuality - lastQuality) / (currTime - lastQualityTime);
        if (lastQualitySpeed > 0.0) {
          qualitySpeed = (qualitySpeed + lastQualitySpeed) / 2.0;
        }
        if (qualitySpeed > 0.0) {
          int nextQuality = (Tools.FTOI(pQuality) / advanceQuality) * advanceQuality;
          if (nextQuality <= pQuality) {
            nextQuality += advanceQuality;
          }
          long qualityReach1 = (long) ((nextQuality - pQuality) / qualitySpeed + 0.5);
          long qualityReach2 = (long) ((nextQuality + advanceQuality - pQuality) / qualitySpeed + 0.5);
          sb.append("Render speed: " + Tools.FTOI(qualitySpeed * 1000.0 * 3600.0) + "\n");
          sb.append("Reach quality " + nextQuality + " in: " + timeFormat.format(new Date(qualityReach1)) + "\n");
          sb.append("Reach quality " + (nextQuality + advanceQuality) + " in: " + timeFormat.format(new Date(qualityReach2)) + "\n\n");
        }
        lastQualitySpeed = qualitySpeed;
      }
      sb.append("Elapsed time: " + timeFormat.format(new Date(renderTime)));
      lastQuality = pQuality;
      lastQualityTime = currTime;
      statsTextArea.setText(sb.toString());
      statsTextArea.validate();
    }
  }

  public void nextButton_clicked() {
    cancelRender();
    genRandomFlame();
    renderButton_clicked();
    enableControls();
  }

  private void clearScreen() {
    try {
      int scrollX = (image.getImageWidth() - (int) imageRootPanel.getBounds().getWidth()) / 2;
      if (scrollX > 0)
        imageScrollPane.getHorizontalScrollBar().setValue(scrollX);
      int scrollY = (image.getImageHeight() - (int) imageRootPanel.getBounds().getHeight()) / 2;
      if (scrollY > 0)
        imageScrollPane.getVerticalScrollBar().setValue(scrollY);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    imageRootPanel.repaint();
  }

  public void saveFlameButton_clicked() {
    try {
      Flame currFlame = getCurrFlame();
      if (currFlame != null) {
        JFileChooser chooser = new FlameFileChooser(prefs);
        if (prefs.getOutputFlamePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          new FlameWriter().writeFlame(currFlame, file.getAbsolutePath());
          prefs.setLastOutputFlameFile(file);
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void toClipboardButton_clicked() {
    try {
      Flame currFlame = getCurrFlame();
      if (currFlame != null) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String xml = new FlameWriter().getFlameXML(currFlame);
        StringSelection data = new StringSelection(xml);
        clipboard.setContents(data, data);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void halveRenderSizeButton_clicked() {
    refreshing = true;
    try {
      halveSizeButton.setSelected(true);
      quarterSizeButton.setSelected(false);
      fullSizeButton.setSelected(false);
    }
    finally {
      refreshing = false;
    }
    changeRenderSizeButton_clicked();
  }

  public void quarterRenderSizeButton_clicked() {
    refreshing = true;
    try {
      halveSizeButton.setSelected(false);
      quarterSizeButton.setSelected(true);
      fullSizeButton.setSelected(false);
    }
    finally {
      refreshing = false;
    }
    changeRenderSizeButton_clicked();
  }

  public void fullRenderSizeButton_clicked() {
    refreshing = true;
    try {
      halveSizeButton.setSelected(false);
      quarterSizeButton.setSelected(false);
      fullSizeButton.setSelected(true);
    }
    finally {
      refreshing = false;
    }
    changeRenderSizeButton_clicked();
  }

  public void changeRenderSizeButton_clicked() {
    if (!refreshing) {
      boolean oldRefreshing = refreshing;
      refreshing = true;
      try {
        boolean rendering = state == State.RENDER;
        if (rendering) {
          stopButton_clicked();
        }
        refreshImagePanel();
        enableControls();
        if (rendering) {
          renderButton_clicked();
        }
      }
      finally {
        refreshing = oldRefreshing;
      }
    }
  }

  public void resolutionProfile_changed() {
    if (!parentCtrl.cmbRefreshing) {
      // Nothing special here
      changeRenderSizeButton_clicked();
    }
  }

  public void fromEditorButton_clicked() {
    try {
      Flame newFlame = parentCtrl.exportFlame();
      if (newFlame != null) {
        currFlame = newFlame;
        storeCurrFlame();
        cancelRender();
        setupProfiles(currFlame);
        renderButton_clicked();
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void toEditorButton_clicked() {
    try {
      Flame currFlame = getCurrFlame();
      if (currFlame != null) {
        parentCtrl.importFlame(currFlame, true);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void qualityProfile_changed() {
    if (!parentCtrl.cmbRefreshing) {
      // Nothing special here
      changeRenderSizeButton_clicked();
    }
  }

  public void resumeBtn_clicked() {
    try {
      JFileChooser chooser = new JWFRenderFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
        cancelRender();
        File file = chooser.getSelectedFile();
        Flame newFlame = new Flame();
        FlameRenderer newRenderer = new FlameRenderer(newFlame, prefs, newFlame.isBGTransparency(), false);

        ResumedFlameRender resumedRender = newRenderer.resumeRenderFlame(file.getAbsolutePath());
        threads = new RenderThreads(resumedRender.getThreads(), new ArrayList<Thread>());
        Flame flame = currFlame = newRenderer.getFlame();
        // setup size profile
        {
          int width = newRenderer.getRenderInfo().getImageWidth();
          int height = newRenderer.getRenderInfo().getImageHeight();
          ResolutionProfile selected = null;
          boolean full = false;
          boolean halve = false;
          boolean quarter = false;

          for (int i = 0; i < interactiveResolutionProfileCmb.getItemCount(); i++) {
            ResolutionProfile profile = (ResolutionProfile) interactiveResolutionProfileCmb.getItemAt(i);
            if (profile.getWidth() == width && profile.getHeight() == height) {
              selected = profile;
              full = true;
              break;
            }
          }
          if (selected == null) {
            for (int i = 0; i < interactiveResolutionProfileCmb.getItemCount(); i++) {
              ResolutionProfile profile = (ResolutionProfile) interactiveResolutionProfileCmb.getItemAt(i);
              if (profile.getWidth() / 2 == width && profile.getHeight() / 2 == height) {
                selected = profile;
                halve = true;
                break;
              }
            }
          }
          if (selected == null) {
            for (int i = 0; i < interactiveResolutionProfileCmb.getItemCount(); i++) {
              ResolutionProfile profile = (ResolutionProfile) interactiveResolutionProfileCmb.getItemAt(i);
              if (profile.getWidth() / 4 == width && profile.getHeight() / 4 == height) {
                selected = profile;
                quarter = true;
                break;
              }
            }
          }
          if (selected == null) {
            selected = new ResolutionProfile(false, width, height);
            full = true;
            interactiveResolutionProfileCmb.addItem(selected);
          }
          boolean wasQuarterSelected = quarterSizeButton.isSelected();
          boolean wasHalveSelected = halveSizeButton.isSelected();
          boolean wasFullSelected = fullSizeButton.isSelected();
          refreshing = true;
          try {
            quarterSizeButton.setSelected(quarter);
            halveSizeButton.setSelected(halve);
            fullSizeButton.setSelected(full);
          }
          finally {
            refreshing = false;
          }
          ResolutionProfile currSel = (ResolutionProfile) interactiveResolutionProfileCmb.getSelectedItem();
          if (currSel == null || !currSel.equals(selected) || wasQuarterSelected != quarter || wasHalveSelected != halve || wasFullSelected != full) {
            interactiveResolutionProfileCmb.setSelectedItem(selected);
            refreshImagePanel();
          }
          else {
            clearScreen();
          }
        }
        //
        renderer = newRenderer;
        setupProfiles(currFlame);
        if (flame.getBgColorRed() > 0 || flame.getBgColorGreen() > 0 || flame.getBgColorBlue() > 0) {
          image.fillBackground(flame.getBgColorRed(), flame.getBgColorGreen(), flame.getBgColorBlue());
        }
        renderer.registerIterationObserver(this);
        displayUpdater = createDisplayUpdater();
        displayUpdater.initRender(threads.getRenderThreads().size());

        pausedRenderTime = resumedRender.getHeader().getElapsedMilliseconds();
        renderStartTime = System.currentTimeMillis();
        lastQuality = 0.0;
        lastQualitySpeed = 0.0;
        lastQualityTime = 0;
        for (int i = 0; i < threads.getRenderThreads().size(); i++) {
          AbstractRenderThread rThread = threads.getRenderThreads().get(i);
          Thread eThread = startRenderThread(rThread);
          threads.getExecutingThreads().add(eThread);
        }
        updateDisplayThread = new UpdateDisplayThread();
        startDisplayThread(updateDisplayThread);

        state = State.RENDER;
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void pauseBtn_clicked() {
    if (state == State.RENDER) {
      try {
        JFileChooser chooser = new JWFRenderFileChooser(prefs);
        if (prefs.getOutputFlamePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          prefs.setLastOutputFlameFile(file);
          renderer.saveState(file.getAbsolutePath(), threads.getRenderThreads(), displayUpdater.getSampleCount(), System.currentTimeMillis() - renderStartTime + pausedRenderTime, null);
        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void showStatsBtn_changed() {
    showStats = showStatsButton.isSelected();
  }

  public void showPreviewBtn_changed() {
    boolean showPreview = showPreviewButton.isSelected();
    displayUpdater.setShowPreview(showPreview);
    //    if (showPreview) {
    //      renderer.registerIterationObserver(this);
    //    }
    //    else {
    //      renderer.deregisterIterationObserver(this);
    //    }
  }

  private void updateImage() {
    displayUpdater.updateImage(null);
  }

  private void initImage(int pBGRed, int pBGGreen, int pBGBlue, String pBGImagefile) {
    displayUpdater.initImage(pBGRed, pBGGreen, pBGBlue, pBGImagefile);
  }

  @Override
  public void notifyIterationFinished(AbstractRenderThread pEventSource, int pPlotX, int pPlotY, XYZProjectedPoint pProjectedPoint, double pX, double pY, double pZ, double pColorRed, double pColorGreen, double pColorBlue) {
    displayUpdater.iterationFinished(pEventSource, pPlotX, pPlotY);
  }

}
