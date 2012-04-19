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
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.animate.AnimationService.GlobalScript;
import org.jwildfire.create.tina.animate.AnimationService.XFormScript;
import org.jwildfire.create.tina.animate.FlameAnimation;
import org.jwildfire.create.tina.animate.FlameAnimationPart;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThread;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThreadController;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.SWFFileChooser;

public class TinaSWFAnimatorController implements SWFAnimationRenderThreadController, FlameHolder {
  private SWFAnimationRenderThread renderThread = null;
  private final FlameAnimation currAnimation = new FlameAnimation();
  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JComboBox swfAnimatorGlobalScriptCmb;
  private final JComboBox swfAnimatorXFormScriptCmb;
  private final JTextField swfAnimatorFramesREd;
  private final JTextField swfAnimatorFrameREd;
  private final JTextField swfAnimatorFramesPerSecondREd;
  private final JButton swfAnimatorGenerateButton;
  private final JComboBox swfAnimatorResolutionProfileCmb;
  private final JComboBox swfAnimatorQualityProfileCmb;
  private final JButton swfAnimatorLoadFlameFromMainButton;
  private final JButton swfAnimatorLoadFlameFromClipboardButton;
  private final JButton swfAnimatorLoadFlameButton;
  private final JToggleButton swfAnimatorHalveSizeButton;
  private final JProgressBar swfAnimatorProgressBar;
  private final JButton swfAnimatorCancelButton;
  private final JButton swfAnimatorLoadSoundButton;
  private final JButton swfAnimatorClearSoundButton;
  private final ProgressUpdater renderProgressUpdater;
  private final JPanel swfAnimatorPreviewRootPanel;
  private final JLabel swfAnimatorSoundCaptionLbl;
  private final JSlider swfAnimatorFrameSlider;
  private final JPanel swfAnimatorFlamesPanel;
  private final ButtonGroup swfAnimatorFlamesButtonGroup;
  private FlamePanel flamePanel;

  private boolean noRefresh;

  public TinaSWFAnimatorController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs, JComboBox pSWFAnimatorGlobalScriptCmb,
      JComboBox pSWFAnimatorXFormScriptCmb, JTextField pSWFAnimatorFramesREd, JTextField pSWFAnimatorFramesPerSecondREd,
      JButton pSWFAnimatorGenerateButton, JComboBox pSWFAnimatorResolutionProfileCmb,
      JComboBox pSWFAnimatorQualityProfileCmb, JButton pSWFAnimatorLoadFlameFromMainButton,
      JButton pSWFAnimatorLoadFlameFromClipboardButton, JButton pSWFAnimatorLoadFlameButton,
      JToggleButton pSWFAnimatorHalveSizeButton, JProgressBar pSWFAnimatorProgressBar, JButton pSWFAnimatorCancelButton,
      JButton pSWFAnimatorLoadSoundButton, JButton pSWFAnimatorClearSoundButton, ProgressUpdater pRenderProgressUpdater,
      JPanel pSWFAnimatorPreviewRootPanel, JLabel pSWFAnimatorSoundCaptionLbl, JSlider pSWFAnimatorFrameSlider,
      JTextField pSWFAnimatorFrameREd, JPanel pSWFAnimatorFlamesPanel, ButtonGroup pSWFAnimatorFlamesButtonGroup) {
    noRefresh = true;
    try {
      parentCtrl = pParentCtrl;
      prefs = pPrefs;
      errorHandler = pErrorHandler;
      swfAnimatorGlobalScriptCmb = pSWFAnimatorGlobalScriptCmb;
      swfAnimatorXFormScriptCmb = pSWFAnimatorXFormScriptCmb;
      swfAnimatorFramesREd = pSWFAnimatorFramesREd;
      swfAnimatorFrameREd = pSWFAnimatorFrameREd;
      swfAnimatorFramesPerSecondREd = pSWFAnimatorFramesPerSecondREd;
      swfAnimatorGenerateButton = pSWFAnimatorGenerateButton;
      swfAnimatorResolutionProfileCmb = pSWFAnimatorResolutionProfileCmb;
      swfAnimatorQualityProfileCmb = pSWFAnimatorQualityProfileCmb;
      swfAnimatorLoadFlameFromMainButton = pSWFAnimatorLoadFlameFromMainButton;
      swfAnimatorLoadFlameFromClipboardButton = pSWFAnimatorLoadFlameFromClipboardButton;
      swfAnimatorLoadFlameButton = pSWFAnimatorLoadFlameButton;
      swfAnimatorHalveSizeButton = pSWFAnimatorHalveSizeButton;
      swfAnimatorProgressBar = pSWFAnimatorProgressBar;
      swfAnimatorCancelButton = pSWFAnimatorCancelButton;
      swfAnimatorLoadSoundButton = pSWFAnimatorLoadSoundButton;
      swfAnimatorClearSoundButton = pSWFAnimatorClearSoundButton;
      renderProgressUpdater = pRenderProgressUpdater;
      swfAnimatorPreviewRootPanel = pSWFAnimatorPreviewRootPanel;
      swfAnimatorSoundCaptionLbl = pSWFAnimatorSoundCaptionLbl;
      swfAnimatorFrameSlider = pSWFAnimatorFrameSlider;
      swfAnimatorFlamesPanel = pSWFAnimatorFlamesPanel;
      swfAnimatorFlamesButtonGroup = pSWFAnimatorFlamesButtonGroup;
      int frameCount = prefs.getTinaRenderMovieFrames();
      swfAnimatorFrameSlider.setValue(1);
      swfAnimatorFrameSlider.setMinimum(1);
      swfAnimatorFrameSlider.setMaximum(frameCount);
      swfAnimatorFramesREd.setText(String.valueOf(frameCount));
      swfAnimatorFrameREd.setText(String.valueOf(1));
      enableControls();
    }
    finally {
      noRefresh = false;
    }
  }

  protected void enableControls() {
    boolean rendering = renderThread != null;
    swfAnimatorXFormScriptCmb.setEnabled(!rendering);
    swfAnimatorXFormScriptCmb.setEnabled(!rendering);
    swfAnimatorFrameREd.setEnabled(!rendering);
    swfAnimatorFramesREd.setEnabled(!rendering);
    swfAnimatorFramesPerSecondREd.setEnabled(!rendering);
    swfAnimatorGenerateButton.setEnabled(!rendering && currAnimation.getFrameCount() > 0);
    swfAnimatorResolutionProfileCmb.setEnabled(!rendering);
    swfAnimatorQualityProfileCmb.setEnabled(!rendering);
    swfAnimatorLoadFlameFromMainButton.setEnabled(!rendering);
    swfAnimatorLoadFlameFromClipboardButton.setEnabled(!rendering);
    swfAnimatorLoadFlameButton.setEnabled(!rendering);
    swfAnimatorHalveSizeButton.setEnabled(!rendering);
    swfAnimatorCancelButton.setEnabled(rendering && !renderThread.isCancelSignalled());
    swfAnimatorLoadSoundButton.setEnabled(!rendering);
    swfAnimatorClearSoundButton.setEnabled(!rendering && currAnimation.getSoundFilename() != null);
    if (currAnimation.getSoundFilename() == null) {
      swfAnimatorSoundCaptionLbl.setText("(no sound file loaded)");
    }
    else {
      swfAnimatorSoundCaptionLbl.setText(new File(currAnimation.getSoundFilename()).getName());
    }
  }

  public void loadFlameFromMainButton_clicked() {
    try {
      Flame newFlame = parentCtrl.exportFlame();
      if (newFlame != null) {
        addFlame(newFlame);
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void addFlame(Flame pFlame) {
    FlameAnimationPart part = new FlameAnimationPart();
    part.setFlame(pFlame);
    part.setFrameCount(120);
    part.setFrameMorphCount(0);
    addFlameToFlamePanel(pFlame);
    currAnimation.addPart(part);
    noRefresh = true;
    try {
      int frameCount = currAnimation.getFrameCount();
      swfAnimatorFrameSlider.setMaximum(frameCount);
      swfAnimatorFramesREd.setText(String.valueOf(frameCount));
    }
    finally {
      noRefresh = false;
    }
    refreshFlameImage();
  }

  private void addFlameToFlamePanel(Flame pFlame) {
    final int PANEL_HEIGHT = 240;
    final int LABEL_WIDTH = 100;
    final int FIELD_WIDTH = 50;
    final int FIELD_HEIGHT = 24;
    final int LABEL_HEIGHT = 24;
    final int BORDER_SIZE = 8;
    int imageHeight = PANEL_HEIGHT - (4 * FIELD_HEIGHT + 5 * BORDER_SIZE);
    ResolutionProfile resProfile = getResolutionProfile();
    int imageWidth = (int) (imageHeight * resProfile.getAspect() + 0.5);
    int panelWidth = LABEL_WIDTH + FIELD_WIDTH + 2 * BORDER_SIZE;
    int xOff = 0;
    if (imageWidth > 16 && imageHeight > 16) {
      int imageWidthPlusBorder = imageWidth + 2 * BORDER_SIZE;
      if (imageWidthPlusBorder > panelWidth) {
        xOff = (imageWidthPlusBorder - panelWidth) / 2;
        panelWidth = imageWidthPlusBorder;
      }
    }

    JPanel panel = new JPanel();
    panel.setPreferredSize(new Dimension(panelWidth, PANEL_HEIGHT));
    panel.setSize(new Dimension(panelWidth, PANEL_HEIGHT));
    panel.setLayout(null);
    panel.setLocation(0, 0);

    if (imageWidth > 16 && imageHeight > 16) {
      SimpleImage img;
      {
        RenderInfo info = new RenderInfo(imageWidth, imageHeight);
        Flame flame = pFlame.makeCopy();
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
        flame.setWidth(imageWidth);
        flame.setHeight(imageHeight);
        FlameRenderer renderer = new FlameRenderer(flame, prefs);
        RenderedFlame res = renderer.renderFlame(info);
        img = res.getImage();
      }
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(BORDER_SIZE, BORDER_SIZE);
      panel.add(imgPanel);
    }

    int yOff = 2 * BORDER_SIZE + imageHeight;
    {
      JLabel framesLbl = new JLabel("Duration (frames)");
      framesLbl.setBounds(xOff + BORDER_SIZE, yOff, LABEL_WIDTH, LABEL_HEIGHT);
      panel.add(framesLbl);
      JTextField framesField = new JTextField();
      framesField.setBounds(xOff + BORDER_SIZE + LABEL_WIDTH, yOff, FIELD_WIDTH, FIELD_HEIGHT);
      panel.add(framesField);
    }
    yOff += FIELD_HEIGHT;
    {
      JLabel framesMorphLbl = new JLabel("Morph (frames)");
      framesMorphLbl.setBounds(xOff + BORDER_SIZE, yOff, LABEL_WIDTH, LABEL_HEIGHT);
      panel.add(framesMorphLbl);
      JTextField framesMorphField = new JTextField();
      framesMorphField.setBounds(xOff + BORDER_SIZE + LABEL_WIDTH, yOff, FIELD_WIDTH, FIELD_HEIGHT);
      panel.add(framesMorphField);
    }
    yOff += FIELD_HEIGHT;
    {
      JRadioButton selectButton = new JRadioButton("");
      selectButton.setBounds(BORDER_SIZE + LABEL_WIDTH, yOff, FIELD_WIDTH + 1, FIELD_HEIGHT);
      panel.add(selectButton);
      swfAnimatorFlamesButtonGroup.add(selectButton);
    }
    swfAnimatorFlamesPanel.add(panel);
    swfAnimatorFlamesPanel.getParent().validate();
  }

  public void loadFlameFromClipboardButton_clicked() {
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
        addFlame(newFlame);
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
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
      if (chooser.showOpenDialog(swfAnimatorPreviewRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new Flam3Reader().readFlames(file.getAbsolutePath());
        Flame newFlame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        addFlame(newFlame);
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void customScriptButton_clicked() {
    enableControls();
  }

  public void cancelButton_clicked() {
    if (renderThread != null) {
      renderThread.setCancelSignalled(true);
      enableControls();
    }
  }

  public void generateButton_clicked() {
    try {
      JFileChooser chooser = new SWFFileChooser();
      if (prefs.getSwfPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getSwfPath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(swfAnimatorPreviewRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastOutputSwfFile(file);
        int frameCount = Integer.parseInt(swfAnimatorFramesREd.getText());
        double framesPerSecond = Double.parseDouble(swfAnimatorFramesPerSecondREd.getText());
        ResolutionProfile resProfile = getResolutionProfile();
        QualityProfile qualityProfile = getQualityProfile();
        int frameWidth = resProfile.getWidth();
        int frameHeight = resProfile.getHeight();
        if (swfAnimatorHalveSizeButton.isSelected()) {
          frameWidth /= 2;
          frameHeight /= 2;
        }
        GlobalScript globalScript = (GlobalScript) swfAnimatorGlobalScriptCmb.getSelectedItem();
        XFormScript xFormScript = (XFormScript) swfAnimatorXFormScriptCmb.getSelectedItem();
        currAnimation.setGlobalScript(globalScript);
        currAnimation.setxFormScript(xFormScript);
        currAnimation.setQualityProfile(qualityProfile);

        renderThread = new SWFAnimationRenderThread(this, frameCount, frameWidth, frameHeight, framesPerSecond, currAnimation, file.getAbsolutePath());
        try {
          enableControls();
          new Thread(renderThread).start();
        }
        catch (Throwable ex) {
          renderThread = null;
          throw ex;
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
      enableControls();
    }
  }

  public void loadSoundButton_clicked() {
    try {
      JFileChooser chooser = new SoundFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputSoundFilePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(swfAnimatorPreviewRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastInputSoundFile(file);
        currAnimation.setSoundFilename(file.getAbsolutePath());
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void clearSoundButton_clicked() {
    currAnimation.setSoundFilename(null);
    enableControls();
  }

  @Override
  public void onRenderFinished() {
    if (renderThread != null && renderThread.getLastError() != null) {
      errorHandler.handleError(renderThread.getLastError());
    }
    renderThread = null;
    enableControls();
  }

  @Override
  public JProgressBar getProgressBar() {
    return swfAnimatorProgressBar;
  }

  @Override
  public ProgressUpdater getProgressUpdater() {
    return renderProgressUpdater;
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) swfAnimatorResolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private QualityProfile getQualityProfile() {
    QualityProfile res = (QualityProfile) swfAnimatorQualityProfileCmb.getSelectedItem();
    if (res == null) {
      res = new QualityProfile(false, "default", 1, 1, 0, true, false);
    }
    return res;
  }

  @Override
  public Prefs getPrefs() {
    return prefs;
  }

  private FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      int width = swfAnimatorPreviewRootPanel.getWidth();
      int height = swfAnimatorPreviewRootPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(img, 0, 0, swfAnimatorPreviewRootPanel.getWidth(), this, null, null);
      ResolutionProfile resProfile = getResolutionProfile();
      flamePanel.setRenderWidth(resProfile.getWidth());
      flamePanel.setRenderHeight(resProfile.getHeight());
      flamePanel.setFocusable(true);
      swfAnimatorPreviewRootPanel.add(flamePanel, BorderLayout.CENTER);
      swfAnimatorPreviewRootPanel.getParent().validate();
      swfAnimatorPreviewRootPanel.repaint();
      flamePanel.requestFocusInWindow();
    }
    return flamePanel;
  }

  private void removeFlamePanel() {
    if (flamePanel != null) {
      swfAnimatorPreviewRootPanel.remove(flamePanel);
      flamePanel = null;
    }
  }

  @Override
  public Flame getFlame() {
    return getCurrFlame();
  }

  public void refreshFlameImage() {
    if (!noRefresh) {
      FlamePanel imgPanel = getFlamePanel();
      Rectangle bounds = imgPanel.getImageBounds();
      int renderScale = 1;
      int width = bounds.width / renderScale;
      int height = bounds.height / renderScale;
      if (width >= 16 && height >= 16) {
        RenderInfo info = new RenderInfo(width, height);
        Flame flame = getCurrFlame();
        if (flame != null) {
          double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
          int oldSpatialOversample = flame.getSpatialOversample();
          int oldColorOversample = flame.getColorOversample();
          double oldSampleDensity = flame.getSampleDensity();
          try {
            double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
            double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
            flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
            flame.setWidth(info.getImageWidth());
            flame.setHeight(info.getImageHeight());

            FlameRenderer renderer = new FlameRenderer(flame, prefs);
            renderer.setProgressUpdater(null);
            flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
            flame.setSpatialFilterRadius(0.0);
            flame.setSpatialOversample(1);
            flame.setColorOversample(1);
            RenderedFlame res = renderer.renderFlame(info);
            imgPanel.setImage(res.getImage());
          }
          finally {
            flame.setSpatialFilterRadius(oldSpatialFilterRadius);
            flame.setSpatialOversample(oldSpatialOversample);
            flame.setColorOversample(oldColorOversample);
            flame.setSampleDensity(oldSampleDensity);
          }
        }
      }
      swfAnimatorPreviewRootPanel.repaint();
    }
  }

  private void prepareFlame(Flame pFlame) {
    pFlame.setSpatialFilterRadius(0.0);
    pFlame.setSpatialOversample(1);
    pFlame.setColorOversample(1);
    pFlame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
  }

  private Flame getCurrFlame() {
    int frame = swfAnimatorFrameSlider.getValue();
    Flame flame = currAnimation.getFlame(frame);
    prepareFlame(flame);
    boolean oldNoRefresh = noRefresh;
    noRefresh = true;
    try {
      swfAnimatorFrameREd.setText(String.valueOf(frame));
    }
    finally {
      noRefresh = oldNoRefresh;
    }

    int frameCount = Integer.parseInt(swfAnimatorFramesREd.getText());
    GlobalScript globalScript = (GlobalScript) swfAnimatorGlobalScriptCmb.getSelectedItem();
    XFormScript xFormScript = (XFormScript) swfAnimatorXFormScriptCmb.getSelectedItem();
    try {
      return AnimationService.createFlame(frame, frameCount, flame, null, false, globalScript, xFormScript, prefs);
    }
    catch (Throwable ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void resolutionProfileCmb_changed() {
    Flame currFlame = getCurrFlame();
    if (currFlame == null) {
      return;
    }
    ResolutionProfile profile = getResolutionProfile();
    currFlame.setResolutionProfile(profile);
    removeFlamePanel();
    refreshFlameImage();
  }

  public void swfAnimatorFramesREd_changed() {
    if (noRefresh)
      return;
    int frame = swfAnimatorFrameSlider.getValue();
    int frameCount = Integer.parseInt(swfAnimatorFramesREd.getText());
    swfAnimatorFrameSlider.setMaximum(frameCount);
    if (frame > frameCount) {
      frame = frameCount;
      swfAnimatorFrameSlider.setValue(frameCount);
    }
  }

  public void swfAnimatorFrameREd_changed() {
    if (noRefresh)
      return;
    int frame = Integer.parseInt(swfAnimatorFrameREd.getText());
    int frameCount = Integer.parseInt(swfAnimatorFramesREd.getText());
    if (frame < 1) {
      swfAnimatorFrameSlider.setValue(1);
      swfAnimatorFrameREd.setText(String.valueOf(1));
    }
    else if (frame > frameCount) {
      swfAnimatorFrameSlider.setValue(frameCount);
      swfAnimatorFrameREd.setText(String.valueOf(frameCount));
    }
    else {
      swfAnimatorFrameSlider.setValue(frame);
    }
  }

}
