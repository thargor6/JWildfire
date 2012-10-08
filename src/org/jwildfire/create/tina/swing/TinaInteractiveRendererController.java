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
package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.io.Flam3Writer;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.render.FlameRenderThread;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.IterationObserver;
import org.jwildfire.create.tina.render.RenderInfo;
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
  private final JButton saveFlameButton;
  private final JComboBox randomStyleCmb;
  private final JToggleButton halveSizeButton;
  private final JComboBox interactiveResolutionProfileCmb;
  private final JComboBox interactiveQualityProfileCmb;
  private final JButton pauseButton;
  private final JButton resumeButton;

  private final JPanel imageRootPanel;
  private JScrollPane imageScrollPane;
  private final JTextArea statsTextArea;
  private SimpleImage image;
  private Flame currFlame;
  private List<FlameRenderThread> threads;
  private FlameRenderer renderer;
  private State state = State.IDLE;

  public TinaInteractiveRendererController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs,
      JButton pLoadFlameButton, JButton pFromClipboardButton, JButton pNextButton,
      JButton pStopButton, JButton pToClipboardButton, JButton pSaveImageButton, JButton pSaveFlameButton,
      JComboBox pRandomStyleCmb, JPanel pImagePanel, JTextArea pStatsTextArea, JToggleButton pHalveSizeButton,
      JComboBox pInteractiveResolutionProfileCmb, JComboBox pInteractiveQualityProfileCmb,
      JButton pPauseButton, JButton pResumeButton) {
    parentCtrl = pParentCtrl;
    prefs = pPrefs;
    errorHandler = pErrorHandler;

    loadFlameButton = pLoadFlameButton;
    fromClipboardButton = pFromClipboardButton;
    nextButton = pNextButton;
    stopButton = pStopButton;
    toClipboardButton = pToClipboardButton;
    saveImageButton = pSaveImageButton;
    saveFlameButton = pSaveFlameButton;
    randomStyleCmb = pRandomStyleCmb;
    halveSizeButton = pHalveSizeButton;
    interactiveResolutionProfileCmb = pInteractiveResolutionProfileCmb;
    interactiveQualityProfileCmb = pInteractiveQualityProfileCmb;
    imageRootPanel = pImagePanel;
    pauseButton = pPauseButton;
    resumeButton = pResumeButton;
    // interactiveResolutionProfileCmb must be already filled here!
    refreshImagePanel();
    statsTextArea = pStatsTextArea;
    state = State.IDLE;
    genRandomFlame();
    enableControls();
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) interactiveResolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private QualityProfile getQualityProfile() {
    QualityProfile res = (QualityProfile) interactiveQualityProfileCmb.getSelectedItem();
    if (res == null) {
      res = new QualityProfile(false, "default", 1, 1, 0, true, false);
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
    if (halveSizeButton.isSelected()) {
      width /= 2;
      height /= 2;
    }
    image = new SimpleImage(width, height);
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
    stopButton.setEnabled(state == State.RENDER);
    pauseButton.setEnabled(state == State.RENDER);
    resumeButton.setEnabled(state != State.RENDER);
  }

  public void genRandomFlame() {
    final int IMG_WIDTH = 80;
    final int IMG_HEIGHT = 60;

    RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance((String) randomStyleCmb.getSelectedItem(), true);
    int palettePoints = 3 + (int) (Math.random() * 68.0);
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, palettePoints);
    currFlame = sampler.createSample().getFlame();
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
          List<Flame> flames = new Flam3Reader().readFlamesfromXML(xml);
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

      if (pFlame.getQualityProfile() != null) {
        QualityProfile profile = null;
        for (int i = 0; i < interactiveQualityProfileCmb.getItemCount(); i++) {
          profile = (QualityProfile) interactiveQualityProfileCmb.getItemAt(i);
          if (pFlame.getQualityProfile().equals(profile.toString()))
            break;
          else
            profile = null;
        }
        if (profile != null) {
          interactiveQualityProfileCmb.setSelectedItem(profile);
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
        List<Flame> flames = new Flam3Reader().readFlames(file.getAbsolutePath());
        Flame newFlame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        currFlame = newFlame;
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

  public void renderButton_clicked() {
    clearScreen();
    ResolutionProfile resProfile = getResolutionProfile();
    QualityProfile qualProfile = getQualityProfile();
    int width = resProfile.getWidth();
    int height = resProfile.getHeight();
    if (halveSizeButton.isSelected()) {
      width /= 2;
      height /= 2;
    }
    RenderInfo info = new RenderInfo(width, height);
    Flame flame = getCurrFlame();
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    flame.setSampleDensity(qualProfile.getQuality());
    info.setRenderHDR(qualProfile.isWithHDR());
    info.setRenderHDRIntensityMap(qualProfile.isWithHDRIntensityMap());
    if (flame.getBGColorRed() > 0 || flame.getBGColorGreen() > 0 || flame.getBGColorBlue() > 0) {
      image.fillBackground(flame.getBGColorRed(), flame.getBGColorGreen(), flame.getBGColorBlue());
    }
    renderer = new FlameRenderer(flame, prefs);
    renderer.registerIterationObserver(this);
    sampleCount = 0;
    renderStartTime = System.currentTimeMillis();
    pausedRenderTime = 0;
    threads = renderer.startRenderFlame(info);
    state = State.RENDER;
    enableControls();
  }

  public void stopButton_clicked() {
    cancelRender();
    enableControls();
  }

  private void cancelRender() {
    if (state == State.RENDER) {
      while (true) {
        boolean done = true;
        for (FlameRenderThread thread : threads) {
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

  public void saveImageButton_clicked() {
    try {
      JFileChooser chooser = new ImageFileChooser();
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
        RenderedFlame res = renderer.finishRenderFlame(sampleCount);
        new ImageWriter().saveImage(res.getImage(), file.getAbsolutePath());
        if (res.getHDRImage() != null) {
          new ImageWriter().saveImage(res.getHDRImage(), file.getAbsolutePath() + ".hdr");
        }
        if (res.getHDRIntensityMap() != null) {
          new ImageWriter().saveImage(res.getHDRIntensityMap(), file.getAbsolutePath() + ".intensity.hdr");
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public Flame getCurrFlame() {
    return currFlame;
  }

  private long sampleCount = 0;
  private long renderStartTime = 0;
  private long pausedRenderTime = 0;

  private long responsibility = 10;

  private synchronized void incSampleCount() {
    sampleCount++;
  }

  private synchronized void updateImage() {
    imageRootPanel.repaint();
  }

  private synchronized void updateStats(FlameRenderThread pEventSource) {
    double quality = pEventSource.getTonemapper().calcDensity(sampleCount);
    statsTextArea.setText("Current quality: " + Tools.doubleToString(quality) + "\n" +
        "samples so far: " + sampleCount + "\n" +
        "render time: " + Tools.doubleToString((System.currentTimeMillis() - renderStartTime + pausedRenderTime) / 1000.0) + "s");
    statsTextArea.validate();
  }

  @Override
  public void notifyIterationFinished(FlameRenderThread pEventSource, int pX, int pY) {
    incSampleCount();
    if (pX >= 0 && pX < image.getImageWidth() && pY >= 0 && pY < image.getImageHeight()) {
      image.setARGB(pX, pY, pEventSource.getTonemapper().tonemapSample(pX, pY));
      if (sampleCount % (1000 * responsibility) == 0) {
        updateImage();
      }
      if (sampleCount % (100000 * responsibility) == 0) {
        updateStats(pEventSource);
      }
    }
  }

  public void nextButton_clicked() {
    cancelRender();
    genRandomFlame();
    renderButton_clicked();
    enableControls();
  }

  private void clearScreen() {
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
          new Flam3Writer().writeFlame(currFlame, file.getAbsolutePath());
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
        String xml = new Flam3Writer().getFlameXML(currFlame);
        StringSelection data = new StringSelection(xml);
        clipboard.setContents(data, data);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void halveSizeButton_clicked() {
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

  public void resolutionProfile_changed() {
    if (!parentCtrl.cmbRefreshing) {
      // Nothing special here
      halveSizeButton_clicked();
    }
  }

  public void fromEditorButton_clicked() {
    try {
      Flame newFlame = parentCtrl.exportFlame();
      if (newFlame != null) {
        currFlame = newFlame;
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
        parentCtrl.importFlame(currFlame);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void qualityProfile_changed() {
    if (!parentCtrl.cmbRefreshing) {
      // Nothing special here
      halveSizeButton_clicked();
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
        FlameRenderer newRenderer = new FlameRenderer(newFlame, prefs);

        ResumedFlameRender resumedRender = newRenderer.resumeRenderFlame(file.getAbsolutePath());
        threads = resumedRender.getThreads();
        Flame flame = currFlame = newRenderer.getFlame();
        // setup size profile
        {
          int width = newRenderer.getRenderInfo().getImageWidth();
          int height = newRenderer.getRenderInfo().getImageHeight();
          ResolutionProfile selected = null;
          boolean halve = false;
          for (int i = 0; i < interactiveResolutionProfileCmb.getItemCount(); i++) {
            ResolutionProfile profile = (ResolutionProfile) interactiveResolutionProfileCmb.getItemAt(i);
            if (profile.getWidth() == width && profile.getHeight() == height) {
              selected = profile;
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
            selected = new ResolutionProfile(false, width, height);
            halve = false;
            interactiveResolutionProfileCmb.addItem(selected);
          }
          boolean wasHalveSelected = halveSizeButton.isSelected();
          halveSizeButton.setSelected(halve);
          ResolutionProfile currSel = (ResolutionProfile) interactiveResolutionProfileCmb.getSelectedItem();
          if (currSel == null || !currSel.equals(selected) || wasHalveSelected != halve) {
            interactiveResolutionProfileCmb.setSelectedItem(selected);
            refreshImagePanel();
          }
          else {
            clearScreen();
          }
        }
        // setup quality profile
        {
          QualityProfile selected = null;
          for (int i = 0; i < interactiveQualityProfileCmb.getItemCount(); i++) {
            QualityProfile profile = (QualityProfile) interactiveQualityProfileCmb.getItemAt(i);
            // do not currenty take oversampling into account
            if (profile.getQuality() == resumedRender.getHeader().getQuality() && profile.isWithHDR() == resumedRender.getHeader().isWithHDR() &&
                profile.isWithHDRIntensityMap() == resumedRender.getHeader().isWithHDRIntensityMap()) {
              selected = profile;
              // break;
              // no break, select the profile with the highest overall quality
            }
          }
          if (selected == null) {
            selected = new QualityProfile(false, "Resumed render", 1, 1, resumedRender.getHeader().getQuality(), resumedRender.getHeader().isWithHDR(), resumedRender.getHeader().isWithHDRIntensityMap());
            interactiveQualityProfileCmb.addItem(selected);
          }
          interactiveQualityProfileCmb.setSelectedItem(selected);
        }
        //
        renderer = newRenderer;
        setupProfiles(currFlame);
        if (flame.getBGColorRed() > 0 || flame.getBGColorGreen() > 0 || flame.getBGColorBlue() > 0) {
          image.fillBackground(flame.getBGColorRed(), flame.getBGColorGreen(), flame.getBGColorBlue());
        }
        renderer.registerIterationObserver(this);
        sampleCount = renderer.calcSampleCount();
        pausedRenderTime = resumedRender.getHeader().getElapsedMilliseconds();
        renderStartTime = System.currentTimeMillis();
        for (FlameRenderThread thread : threads) {
          new Thread(thread).start();
        }
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
          renderer.saveState(file.getAbsolutePath(), threads, sampleCount, System.currentTimeMillis() - renderStartTime + pausedRenderTime, getQualityProfile());
        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

}
