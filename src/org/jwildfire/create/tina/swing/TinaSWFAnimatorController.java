/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.animate.FlameMoviePart;
import org.jwildfire.create.tina.animate.GlobalScript;
import org.jwildfire.create.tina.animate.GlobalScriptType;
import org.jwildfire.create.tina.animate.OutputFormat;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThread;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThreadController;
import org.jwildfire.create.tina.animate.XFormScript;
import org.jwildfire.create.tina.animate.XFormScriptType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.JWFMovieReader;
import org.jwildfire.create.tina.io.JWFMovieWriter;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.SWFFileChooser;

public class TinaSWFAnimatorController implements SWFAnimationRenderThreadController, FlameHolder {
  public static final int PAGE_INDEX = 4;
  private SWFAnimationRenderThread renderThread = null;
  private FlameMovie currMovie;
  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;

  private final JComboBox swfAnimatorGlobalScript1Cmb;
  private final JWFNumberField swfAnimatorGlobalScript1REd;
  private final MotionCurve swfAnimatorGlobalScript1Curve = new MotionCurve();

  private final JComboBox swfAnimatorGlobalScript2Cmb;
  private final JWFNumberField swfAnimatorGlobalScript2REd;
  private final MotionCurve swfAnimatorGlobalScript2Curve = new MotionCurve();

  private final JComboBox swfAnimatorGlobalScript3Cmb;
  private final JWFNumberField swfAnimatorGlobalScript3REd;
  private final MotionCurve swfAnimatorGlobalScript3Curve = new MotionCurve();

  private final JComboBox swfAnimatorGlobalScript4Cmb;
  private final JWFNumberField swfAnimatorGlobalScript4REd;
  private final MotionCurve swfAnimatorGlobalScript4Curve = new MotionCurve();

  private final JComboBox swfAnimatorGlobalScript5Cmb;
  private final JWFNumberField swfAnimatorGlobalScript5REd;
  private final MotionCurve swfAnimatorGlobalScript5Curve = new MotionCurve();

  private final JComboBox swfAnimatorXFormScript1Cmb;
  private final JWFNumberField swfAnimatorXFormScript1REd;
  private final MotionCurve swfAnimatorXFormScript1Curve = new MotionCurve();

  private final JComboBox swfAnimatorXFormScript2Cmb;
  private final JWFNumberField swfAnimatorXFormScript2REd;
  private final MotionCurve swfAnimatorXFormScript2Curve = new MotionCurve();

  private final JComboBox swfAnimatorXFormScript3Cmb;
  private final JWFNumberField swfAnimatorXFormScript3REd;
  private final MotionCurve swfAnimatorXFormScript3Curve = new MotionCurve();

  private final JComboBox swfAnimatorXFormScript4Cmb;
  private final JWFNumberField swfAnimatorXFormScript4REd;
  private final MotionCurve swfAnimatorXFormScript4Curve = new MotionCurve();

  private final JComboBox swfAnimatorXFormScript5Cmb;
  private final JWFNumberField swfAnimatorXFormScript5REd;
  private final MotionCurve swfAnimatorXFormScript5Curve = new MotionCurve();

  private final JWFNumberField swfAnimatorFramesREd;
  private final JWFNumberField swfAnimatorFrameREd;
  private final JWFNumberField swfAnimatorFramesPerSecondREd;
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
  private final JComboBox swfAnimatorOutputCmb;
  private final JButton swfAnimatorMoveUpButton;
  private final JButton swfAnimatorMoveDownButton;
  private final JButton swfAnimatorRemoveFlameButton;
  private final JButton swfAnimatorRemoveAllFlamesButton;
  private final JButton swfAnimatorMovieFromClipboardButton;
  private final JButton swfAnimatorMovieFromDiskButton;
  private final JButton swfAnimatorMovieToClipboardButton;
  private final JButton swfAnimatorMovieToDiskButton;
  private final JButton swfAnimatorFrameToEditorBtn;
  private final JButton swfAnimatorPlayButton;
  private final JWFNumberField swfAnimatorFromFrameREd;
  private final JWFNumberField swfAnimatorToFrameREd;
  private FlamePanel flamePanel;
  private final List<JPanel> flamePartPanelList = new ArrayList<JPanel>();
  private final List<JRadioButton> flamePartRadioButtonList = new ArrayList<JRadioButton>();

  private boolean noRefresh;

  private class MotionCurveEditInfo {
    private final MotionCurve curve;
    private final JWFNumberField field;

    public MotionCurveEditInfo(MotionCurve pCurve, JWFNumberField pField) {
      curve = pCurve;
      field = pField;
    }

    public MotionCurve getCurve() {
      return curve;
    }

    public JWFNumberField getField() {
      return field;
    }

  }

  private Map<String, MotionCurveEditInfo> curves = new HashMap<String, MotionCurveEditInfo>();

  public TinaSWFAnimatorController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs,
      JComboBox pSWFAnimatorGlobalScript1Cmb, JWFNumberField pSWFAnimatorGlobalScript1REd,
      JComboBox pSWFAnimatorGlobalScript2Cmb, JWFNumberField pSWFAnimatorGlobalScript2REd,
      JComboBox pSWFAnimatorGlobalScript3Cmb, JWFNumberField pSWFAnimatorGlobalScript3REd,
      JComboBox pSWFAnimatorGlobalScript4Cmb, JWFNumberField pSWFAnimatorGlobalScript4REd,
      JComboBox pSWFAnimatorGlobalScript5Cmb, JWFNumberField pSWFAnimatorGlobalScript5REd,
      JComboBox pSWFAnimatorXFormScript1Cmb, JWFNumberField pSWFAnimatorXFormScript1REd,
      JComboBox pSWFAnimatorXFormScript2Cmb, JWFNumberField pSWFAnimatorXFormScript2REd,
      JComboBox pSWFAnimatorXFormScript3Cmb, JWFNumberField pSWFAnimatorXFormScript3REd,
      JComboBox pSWFAnimatorXFormScript4Cmb, JWFNumberField pSWFAnimatorXFormScript4REd,
      JComboBox pSWFAnimatorXFormScript5Cmb, JWFNumberField pSWFAnimatorXFormScript5REd,
      JWFNumberField pSWFAnimatorFramesREd, JWFNumberField pSWFAnimatorFramesPerSecondREd,
      JButton pSWFAnimatorGenerateButton, JComboBox pSWFAnimatorResolutionProfileCmb,
      JComboBox pSWFAnimatorQualityProfileCmb, JButton pSWFAnimatorLoadFlameFromMainButton,
      JButton pSWFAnimatorLoadFlameFromClipboardButton, JButton pSWFAnimatorLoadFlameButton,
      JToggleButton pSWFAnimatorHalveSizeButton, JProgressBar pSWFAnimatorProgressBar, JButton pSWFAnimatorCancelButton,
      JButton pSWFAnimatorLoadSoundButton, JButton pSWFAnimatorClearSoundButton, ProgressUpdater pRenderProgressUpdater,
      JPanel pSWFAnimatorPreviewRootPanel, JLabel pSWFAnimatorSoundCaptionLbl, JSlider pSWFAnimatorFrameSlider,
      JWFNumberField pSWFAnimatorFrameREd, JPanel pSWFAnimatorFlamesPanel, ButtonGroup pSWFAnimatorFlamesButtonGroup,
      JComboBox pSWFAnimatorOutputCmb, JButton pSWFAnimatorMoveUpButton, JButton pSWFAnimatorMoveDownButton,
      JButton pSWFAnimatorRemoveFlameButton, JButton pSWFAnimatorRemoveAllFlamesButton, JButton pSWFAnimatorMovieFromClipboardButton,
      JButton pSWFAnimatorMovieFromDiskButton, JButton pSWFAnimatorMovieToClipboardButton, JButton pSWFAnimatorMovieToDiskButton,
      JButton pSWFAnimatorFrameToEditorBtn, JButton pSWFAnimatorPlayButton, JWFNumberField pSWFAnimatorFromFrameREd,
      JWFNumberField pSWFAnimatorToFrameREd) {
    noRefresh = true;
    try {
      parentCtrl = pParentCtrl;
      prefs = pPrefs;
      currMovie = new FlameMovie(pPrefs);
      errorHandler = pErrorHandler;
      swfAnimatorGlobalScript1Cmb = pSWFAnimatorGlobalScript1Cmb;
      swfAnimatorGlobalScript1REd = pSWFAnimatorGlobalScript1REd;
      swfAnimatorGlobalScript2Cmb = pSWFAnimatorGlobalScript2Cmb;
      swfAnimatorGlobalScript2REd = pSWFAnimatorGlobalScript2REd;
      swfAnimatorGlobalScript3Cmb = pSWFAnimatorGlobalScript3Cmb;
      swfAnimatorGlobalScript3REd = pSWFAnimatorGlobalScript3REd;
      swfAnimatorGlobalScript4Cmb = pSWFAnimatorGlobalScript4Cmb;
      swfAnimatorGlobalScript4REd = pSWFAnimatorGlobalScript4REd;
      swfAnimatorGlobalScript5Cmb = pSWFAnimatorGlobalScript5Cmb;
      swfAnimatorGlobalScript5REd = pSWFAnimatorGlobalScript5REd;
      swfAnimatorXFormScript1Cmb = pSWFAnimatorXFormScript1Cmb;
      swfAnimatorXFormScript1REd = pSWFAnimatorXFormScript1REd;
      swfAnimatorXFormScript2Cmb = pSWFAnimatorXFormScript2Cmb;
      swfAnimatorXFormScript2REd = pSWFAnimatorXFormScript2REd;
      swfAnimatorXFormScript3Cmb = pSWFAnimatorXFormScript3Cmb;
      swfAnimatorXFormScript3REd = pSWFAnimatorXFormScript3REd;
      swfAnimatorXFormScript4Cmb = pSWFAnimatorXFormScript4Cmb;
      swfAnimatorXFormScript4REd = pSWFAnimatorXFormScript4REd;
      swfAnimatorXFormScript5Cmb = pSWFAnimatorXFormScript5Cmb;
      swfAnimatorXFormScript5REd = pSWFAnimatorXFormScript5REd;
      curves.put("globalScript1", new MotionCurveEditInfo(swfAnimatorGlobalScript1Curve, swfAnimatorXFormScript1REd));
      curves.put("globalScript2", new MotionCurveEditInfo(swfAnimatorGlobalScript2Curve, swfAnimatorXFormScript2REd));
      curves.put("globalScript3", new MotionCurveEditInfo(swfAnimatorGlobalScript3Curve, swfAnimatorXFormScript3REd));
      curves.put("globalScript4", new MotionCurveEditInfo(swfAnimatorGlobalScript4Curve, swfAnimatorXFormScript4REd));
      curves.put("globalScript5", new MotionCurveEditInfo(swfAnimatorGlobalScript5Curve, swfAnimatorXFormScript5REd));

      curves.put("xFormScript1", new MotionCurveEditInfo(swfAnimatorXFormScript1Curve, swfAnimatorXFormScript1REd));
      curves.put("xFormScript2", new MotionCurveEditInfo(swfAnimatorXFormScript2Curve, swfAnimatorXFormScript2REd));
      curves.put("xFormScript3", new MotionCurveEditInfo(swfAnimatorXFormScript3Curve, swfAnimatorXFormScript3REd));
      curves.put("xFormScript4", new MotionCurveEditInfo(swfAnimatorXFormScript4Curve, swfAnimatorXFormScript4REd));
      curves.put("xFormScript5", new MotionCurveEditInfo(swfAnimatorXFormScript5Curve, swfAnimatorXFormScript5REd));

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
      swfAnimatorOutputCmb = pSWFAnimatorOutputCmb;
      swfAnimatorMoveUpButton = pSWFAnimatorMoveUpButton;
      swfAnimatorMoveDownButton = pSWFAnimatorMoveDownButton;
      swfAnimatorRemoveFlameButton = pSWFAnimatorRemoveFlameButton;
      swfAnimatorRemoveAllFlamesButton = pSWFAnimatorRemoveAllFlamesButton;
      swfAnimatorMovieFromClipboardButton = pSWFAnimatorMovieFromClipboardButton;
      swfAnimatorMovieFromDiskButton = pSWFAnimatorMovieFromDiskButton;
      swfAnimatorMovieToClipboardButton = pSWFAnimatorMovieToClipboardButton;
      swfAnimatorMovieToDiskButton = pSWFAnimatorMovieToDiskButton;
      swfAnimatorFrameToEditorBtn = pSWFAnimatorFrameToEditorBtn;
      swfAnimatorPlayButton = pSWFAnimatorPlayButton;
      swfAnimatorFromFrameREd = pSWFAnimatorFromFrameREd;
      swfAnimatorToFrameREd = pSWFAnimatorToFrameREd;

      swfAnimatorOutputCmb.addItem(OutputFormat.FLAMES);
      swfAnimatorOutputCmb.addItem(OutputFormat.PNG);
      swfAnimatorOutputCmb.addItem(OutputFormat.SWF);
      swfAnimatorOutputCmb.addItem(OutputFormat.SWF_AND_PNG);
      swfAnimatorOutputCmb.setSelectedItem(OutputFormat.PNG);

      int frameCount = prefs.getTinaRenderMovieFrames();
      swfAnimatorFrameSlider.setValue(1);
      swfAnimatorFrameSlider.setMinimum(1);
      swfAnimatorFrameSlider.setMaximum(frameCount);
      swfAnimatorFramesREd.setValue(frameCount);
      swfAnimatorFrameREd.setMaxValue(frameCount);
      swfAnimatorFrameREd.setValue(1);
      swfAnimatorFromFrameREd.setValue(1);
      swfAnimatorFromFrameREd.setMaxValue(frameCount);
      swfAnimatorToFrameREd.setValue(frameCount);
      swfAnimatorToFrameREd.setMaxValue(frameCount);

      enableControls();
    }
    finally {
      noRefresh = false;
    }
  }

  protected void enableControls() {
    boolean rendering = renderThread != null;
    swfAnimatorXFormScript1Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript2Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript3Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript4Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript5Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript1Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript2Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript3Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript4Cmb.setEnabled(!rendering);
    swfAnimatorXFormScript5Cmb.setEnabled(!rendering);
    swfAnimatorFrameREd.setEnabled(!rendering);
    swfAnimatorFramesREd.setEnabled(!rendering);
    swfAnimatorFramesREd.setEditable(false);
    swfAnimatorFromFrameREd.setEnabled(!rendering);
    swfAnimatorToFrameREd.setEnabled(!rendering);
    swfAnimatorFramesPerSecondREd.setEnabled(!rendering);
    swfAnimatorGenerateButton.setEnabled(!rendering && currMovie.getFrameCount() > 0);
    swfAnimatorGenerateButton.setVisible(!rendering);
    swfAnimatorPlayButton.setEnabled(swfAnimatorGenerateButton.isEnabled());
    swfAnimatorResolutionProfileCmb.setEnabled(!rendering);
    swfAnimatorQualityProfileCmb.setEnabled(!rendering);
    swfAnimatorLoadFlameFromMainButton.setEnabled(!rendering);
    swfAnimatorLoadFlameFromClipboardButton.setEnabled(!rendering);
    swfAnimatorLoadFlameButton.setEnabled(!rendering);
    swfAnimatorHalveSizeButton.setEnabled(!rendering);
    swfAnimatorCancelButton.setEnabled(rendering && !renderThread.isCancelSignalled());
    swfAnimatorCancelButton.setVisible(rendering);
    swfAnimatorLoadSoundButton.setEnabled(!rendering);
    swfAnimatorClearSoundButton.setEnabled(!rendering && currMovie.getSoundFilename() != null);
    if (currMovie.getSoundFilename() == null) {
      swfAnimatorSoundCaptionLbl.setText("(no sound file loaded)");
    }
    else {
      swfAnimatorSoundCaptionLbl.setText(new File(currMovie.getSoundFilename()).getName());
    }
    int selected = getSelectedFlameRadioButtonIndex();
    int flameCount = getFlameCount();
    swfAnimatorMoveUpButton.setEnabled(!rendering && selected > 0);
    swfAnimatorMoveDownButton.setEnabled(!rendering && selected >= 0 && selected < flameCount - 1);
    swfAnimatorRemoveFlameButton.setEnabled(!rendering && selected >= 0);
    swfAnimatorRemoveAllFlamesButton.setEnabled(!rendering && flameCount > 0);
    swfAnimatorMovieFromClipboardButton.setEnabled(!rendering);
    swfAnimatorMovieFromDiskButton.setEnabled(!rendering);
    swfAnimatorMovieToClipboardButton.setEnabled(flameCount > 0);
    swfAnimatorMovieToDiskButton.setEnabled(flameCount > 0);
    swfAnimatorFrameSlider.setEnabled(flameCount > 0);
    swfAnimatorFrameToEditorBtn.setEnabled(flameCount > 0);
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
    FlameMoviePart part = new FlameMoviePart();
    part.setFlame(pFlame);
    part.setFrameCount(120);
    part.setFrameMorphCount(60);
    addFlameToFlamePanel(part);
    currMovie.addPart(part);
    refreshFrameCount();
    refreshFlameImage();
  }

  private void refreshFrameCount() {
    noRefresh = true;
    try {
      int value = swfAnimatorFrameSlider.getValue();
      int frameCount = currMovie.getFrameCount();
      swfAnimatorFrameSlider.setMaximum(frameCount);
      int oldFrameCount = 0;
      try {
        oldFrameCount = swfAnimatorFramesREd.getIntValue();
      }
      catch (Exception ex) {
        oldFrameCount = frameCount;
      }
      if (frameCount != oldFrameCount) {
        swfAnimatorToFrameREd.setValue(frameCount);
      }
      try {
        int from = swfAnimatorFromFrameREd.getIntValue();
        if (from > frameCount) {
          swfAnimatorFromFrameREd.setValue(frameCount);
        }
      }
      catch (Exception ex) {

      }

      swfAnimatorFramesREd.setValue(frameCount);
      swfAnimatorFrameREd.setMaxValue(frameCount);
      swfAnimatorFromFrameREd.setMaxValue(frameCount);
      swfAnimatorToFrameREd.setMaxValue(frameCount);
      if (value > frameCount) {
        swfAnimatorFrameSlider.setValue(frameCount);
        swfAnimatorFrameREd.setValue(frameCount);
        refreshFlameImage();
      }
    }
    finally {
      noRefresh = false;
    }
  }

  private void addFlameToFlamePanel(final FlameMoviePart pPart) {
    final int PANEL_HEIGHT = 240;
    final int LABEL_WIDTH = 100;
    final int FIELD_WIDTH = 62;
    final int FIELD_HEIGHT = 24;
    final int LABEL_HEIGHT = 24;
    final int BUTTON_WIDTH = 48;
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

    if (imageWidth > 16 && imageHeight > 16 && pPart.getFlame() != null) {
      SimpleImage img;
      {
        RenderInfo info = new RenderInfo(imageWidth, imageHeight, RenderMode.PREVIEW);
        Flame flame = pPart.getFlame().makeCopy();
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
        flame.setWidth(imageWidth);
        flame.setHeight(imageHeight);
        FlameRenderer renderer = new FlameRenderer(flame, prefs, false, false);
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
      final JWFNumberField framesField = new JWFNumberField();
      framesField.setOnlyIntegers(true);
      framesField.setValue(pPart.getFrameCount());
      framesField.setHasMinValue(true);
      framesField.setMinValue(1.0);
      framesField.setBounds(xOff + BORDER_SIZE + LABEL_WIDTH, yOff, FIELD_WIDTH, FIELD_HEIGHT);
      framesField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          framesFieldChanged(framesField, pPart);
        }
      });
      panel.add(framesField);
    }
    yOff += FIELD_HEIGHT;
    {
      JLabel framesMorphLbl = new JLabel("Morph (frames)");
      framesMorphLbl.setBounds(xOff + BORDER_SIZE, yOff, LABEL_WIDTH, LABEL_HEIGHT);
      panel.add(framesMorphLbl);
      final JWFNumberField framesMorphField = new JWFNumberField();
      framesMorphField.setOnlyIntegers(true);
      framesMorphField.setHasMinValue(true);
      framesMorphField.setMinValue(0.0);
      framesMorphField.setValue(pPart.getFrameMorphCount());
      framesMorphField.setBounds(xOff + BORDER_SIZE + LABEL_WIDTH, yOff, FIELD_WIDTH, FIELD_HEIGHT);
      framesMorphField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          framesMorphFieldChanged(framesMorphField, pPart);
        }
      });
      panel.add(framesMorphField);
    }
    yOff += FIELD_HEIGHT;
    {
      JButton editButton = new JButton("E");
      editButton.setBounds(BORDER_SIZE, yOff, BUTTON_WIDTH, FIELD_HEIGHT);
      editButton.setFont(new Font("Dialog", Font.BOLD, 10));
      editButton.setToolTipText("Edit flame");
      editButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          editPartBtn_clicked(pPart);
        }
      });
      panel.add(editButton);
    }
    {
      JButton delButton = new JButton("D");
      delButton.setBounds(BORDER_SIZE + BUTTON_WIDTH + 1, yOff, BUTTON_WIDTH, FIELD_HEIGHT);
      delButton.setFont(new Font("Dialog", Font.BOLD, 10));
      delButton.setToolTipText("Remove flame from movie");
      delButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          deletePartBtn_clicked(pPart);
        }
      });
      panel.add(delButton);
    }

    JRadioButton selectButton;
    {
      selectButton = new JRadioButton("");
      selectButton.setBounds(BORDER_SIZE + 2 * BUTTON_WIDTH + 2, yOff, FIELD_WIDTH + 1, FIELD_HEIGHT);
      selectButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          enableControls();
        }
      });
      selectButton.setSelected(flamePartRadioButtonList.size() == 0);
      panel.add(selectButton);
      swfAnimatorFlamesButtonGroup.add(selectButton);
    }
    swfAnimatorFlamesPanel.add(panel);

    flamePartPanelList.add(panel);
    flamePartRadioButtonList.add(selectButton);

    swfAnimatorFlamesPanel.getParent().validate();
  }

  protected void editPartBtn_clicked(FlameMoviePart pPart) {
    parentCtrl.importFlame(pPart.getFlame(), true);
    parentCtrl.getRootTabbedPane().setSelectedIndex(0);
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
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
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

  public void cancelButton_clicked() {
    if (renderThread != null) {
      renderThread.setCancelSignalled(true);
      enableControls();
    }
  }

  private GlobalScript getGlobalScriptFromUI(JComboBox pCmb, JWFNumberField pField) {
    GlobalScriptType scriptType = (GlobalScriptType) pCmb.getSelectedItem();
    GlobalScript res = new GlobalScript(scriptType != null ? scriptType : GlobalScriptType.NONE, pField.getDoubleValue());
    res.getAmplitudeCurve().assign(curves.get(pField.getMotionPropertyName()).getCurve());
    return res;
  }

  private XFormScript getXFormScriptFromUI(JComboBox pCmb, JWFNumberField pField) {
    XFormScriptType scriptType = (XFormScriptType) pCmb.getSelectedItem();
    XFormScript res = new XFormScript(scriptType != null ? scriptType : XFormScriptType.NONE, pField.getDoubleValue());
    res.getAmplitudeCurve().assign(curves.get(pField.getMotionPropertyName()).getCurve());
    return res;
  }

  private void updateMovieFields() {
    double framesPerSecond = swfAnimatorFramesPerSecondREd.getDoubleValue();
    ResolutionProfile resProfile = getResolutionProfile();
    QualityProfile qualityProfile = getQualityProfile();
    int frameWidth = resProfile.getWidth();
    int frameHeight = resProfile.getHeight();
    if (swfAnimatorHalveSizeButton.isSelected()) {
      frameWidth /= 2;
      frameHeight /= 2;
    }

    OutputFormat outputFormat = (OutputFormat) swfAnimatorOutputCmb.getSelectedItem();
    if (outputFormat == null) {
      outputFormat = OutputFormat.PNG;
    }
    currMovie.setGlobalScript1(getGlobalScriptFromUI(swfAnimatorGlobalScript1Cmb, swfAnimatorGlobalScript1REd));
    currMovie.setGlobalScript2(getGlobalScriptFromUI(swfAnimatorGlobalScript2Cmb, swfAnimatorGlobalScript2REd));
    currMovie.setGlobalScript3(getGlobalScriptFromUI(swfAnimatorGlobalScript3Cmb, swfAnimatorGlobalScript3REd));
    currMovie.setGlobalScript4(getGlobalScriptFromUI(swfAnimatorGlobalScript4Cmb, swfAnimatorGlobalScript4REd));
    currMovie.setGlobalScript5(getGlobalScriptFromUI(swfAnimatorGlobalScript5Cmb, swfAnimatorGlobalScript5REd));
    currMovie.setxFormScript1(getXFormScriptFromUI(swfAnimatorXFormScript1Cmb, swfAnimatorXFormScript1REd));
    currMovie.setxFormScript2(getXFormScriptFromUI(swfAnimatorXFormScript2Cmb, swfAnimatorXFormScript2REd));
    currMovie.setxFormScript3(getXFormScriptFromUI(swfAnimatorXFormScript3Cmb, swfAnimatorXFormScript3REd));
    currMovie.setxFormScript4(getXFormScriptFromUI(swfAnimatorXFormScript4Cmb, swfAnimatorXFormScript4REd));
    currMovie.setxFormScript5(getXFormScriptFromUI(swfAnimatorXFormScript5Cmb, swfAnimatorXFormScript5REd));

    currMovie.setQuality(qualityProfile.getQuality());
    currMovie.setOutputFormat(outputFormat);
    currMovie.setFrameWidth(frameWidth);
    currMovie.setFrameHeight(frameHeight);
    currMovie.setFramesPerSecond(framesPerSecond);
  }

  public void generateButton_clicked() {
    try {
      OutputFormat outputFormat = (OutputFormat) swfAnimatorOutputCmb.getSelectedItem();
      JFileChooser chooser;
      switch (outputFormat) {
        case FLAMES:
          chooser = new FlameFileChooser(prefs);
          break;
        case PNG:
          chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
          break;
        default:
          chooser = new SWFFileChooser();
      }

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
        updateMovieFields();
        int from = swfAnimatorFromFrameREd.getIntValue();
        int to = swfAnimatorToFrameREd.getIntValue();
        renderThread = new SWFAnimationRenderThread(this, currMovie, file.getAbsolutePath(), from, to);
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
        currMovie.setSoundFilename(file.getAbsolutePath());
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void clearSoundButton_clicked() {
    currMovie.setSoundFilename(null);
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
      res = new QualityProfile(false, "default", 200, true, false);
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
      flamePanel = new FlamePanel(img, 0, 0, swfAnimatorPreviewRootPanel.getWidth(), this, null);
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
      int width = bounds.width;
      int height = bounds.height;
      if (width >= 16 && height >= 16) {
        RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
        Flame flame = getCurrFlame();
        if (flame != null) {
          double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
          double oldSampleDensity = flame.getSampleDensity();
          try {
            double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
            double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
            flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
            flame.setWidth(info.getImageWidth());
            flame.setHeight(info.getImageHeight());

            FlameRenderer renderer = new FlameRenderer(flame, prefs, false, false);
            renderer.setProgressUpdater(null);
            flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
            flame.setSpatialFilterRadius(0.0);
            RenderedFlame res = renderer.renderFlame(info);
            imgPanel.setImage(res.getImage());
          }
          finally {
            flame.setSpatialFilterRadius(oldSpatialFilterRadius);
            flame.setSampleDensity(oldSampleDensity);
          }
        }
      }
      else {
        imgPanel.setImage(new SimpleImage(width, height));
      }
      swfAnimatorPreviewRootPanel.repaint();
    }
  }

  private void prepareFlame(Flame pFlame) {
    pFlame.setSpatialFilterRadius(0.0);
    pFlame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
  }

  private Flame getCurrFlame() {
    int frame = swfAnimatorFrameSlider.getValue();
    Flame flame = currMovie.getFlame(frame);
    if (flame != null) {
      prepareFlame(flame);

      boolean oldNoRefresh = noRefresh;
      noRefresh = true;
      try {
        swfAnimatorFrameREd.setValue(frame);
      }
      finally {
        noRefresh = oldNoRefresh;
      }

      int frameCount = swfAnimatorFramesREd.getIntValue();
      double fps = swfAnimatorFramesPerSecondREd.getDoubleValue();
      GlobalScript globalScripts[] = {
          getGlobalScriptFromUI(swfAnimatorGlobalScript1Cmb, swfAnimatorGlobalScript1REd),
          getGlobalScriptFromUI(swfAnimatorGlobalScript2Cmb, swfAnimatorGlobalScript2REd),
          getGlobalScriptFromUI(swfAnimatorGlobalScript3Cmb, swfAnimatorGlobalScript3REd),
          getGlobalScriptFromUI(swfAnimatorGlobalScript4Cmb, swfAnimatorGlobalScript4REd),
          getGlobalScriptFromUI(swfAnimatorGlobalScript5Cmb, swfAnimatorGlobalScript5REd)
      };
      XFormScript xFormScripts[] = {
          getXFormScriptFromUI(swfAnimatorXFormScript1Cmb, swfAnimatorXFormScript1REd),
          getXFormScriptFromUI(swfAnimatorXFormScript2Cmb, swfAnimatorXFormScript2REd),
          getXFormScriptFromUI(swfAnimatorXFormScript3Cmb, swfAnimatorXFormScript3REd),
          getXFormScriptFromUI(swfAnimatorXFormScript4Cmb, swfAnimatorXFormScript4REd),
          getXFormScriptFromUI(swfAnimatorXFormScript5Cmb, swfAnimatorXFormScript5REd)
      };

      try {
        Flame res = flame.makeCopy();
        for (GlobalScript script : globalScripts) {
          AnimationService.addMotionCurve(res, script, frame, frameCount, fps);
        }
        for (XFormScript script : xFormScripts) {
          AnimationService.addMotionCurve(res, script, frame, frameCount, fps);
        }
        res.setFrame(frame);
        return res;
      }
      catch (Throwable ex) {
        ex.printStackTrace();
        return null;
      }
    }
    else {
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

  public void swfAnimatorFrameREd_changed() {
    if (noRefresh)
      return;
    boolean oldNoRefresh = noRefresh;
    try {
      noRefresh = true;
      int frame = swfAnimatorFrameREd.getIntValue();
      int frameCount = swfAnimatorFramesREd.getIntValue();
      if (frame < 1) {
        swfAnimatorFrameSlider.setValue(1);
        swfAnimatorFrameREd.setValue(1);
      }
      else if (frame > frameCount) {
        swfAnimatorFrameSlider.setValue(frameCount);
        swfAnimatorFrameREd.setValue(frameCount);
      }
      else {
        swfAnimatorFrameSlider.setValue(frame);
      }
    }
    finally {
      noRefresh = oldNoRefresh;
    }
  }

  private void framesFieldChanged(JWFNumberField pFramesField, FlameMoviePart pPart) {
    try {
      int frameCount = pFramesField.getIntValue();
      if (frameCount < 0) {
        frameCount = 0;
        pFramesField.setValue(frameCount);
      }
      pPart.setFrameCount(frameCount);
      refreshFrameCount();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void framesMorphFieldChanged(JWFNumberField pMorphFramesField, FlameMoviePart pPart) {
    try {
      int framesMorphCount = pMorphFramesField.getIntValue();
      if (framesMorphCount < 0) {
        framesMorphCount = 0;
        pMorphFramesField.setValue(framesMorphCount);
      }
      else if (framesMorphCount > pPart.getFrameCount()) {
        framesMorphCount = pPart.getFrameCount();
        pMorphFramesField.setValue(framesMorphCount);
      }
      pPart.setFrameMorphCount(framesMorphCount);
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void flameMoveUpButton_clicked() {
    int selected = getSelectedFlameRadioButtonIndex();
    if (selected > 0) {
      swapFlameMoviePart(selected, selected - 1);
      enableControls();
    }
  }

  private int getFlameCount() {
    return flamePartRadioButtonList.size();
  }

  private int getSelectedFlameRadioButtonIndex() {
    for (int i = 0; i < flamePartRadioButtonList.size(); i++) {
      if (flamePartRadioButtonList.get(i).isSelected())
        return i;
    }
    return -1;
  }

  private void swapFlameMoviePart(int pFrom, int pTo) {
    FlameMoviePart part1 = currMovie.getParts().get(pFrom);
    FlameMoviePart part2 = currMovie.getParts().get(pTo);
    JPanel panel1 = flamePartPanelList.get(pFrom);
    JPanel panel2 = flamePartPanelList.get(pTo);
    JRadioButton radio1Btn = flamePartRadioButtonList.get(pFrom);
    JRadioButton radio2Btn = flamePartRadioButtonList.get(pTo);
    for (JPanel panel : flamePartPanelList) {
      swfAnimatorFlamesPanel.remove(panel);
    }
    currMovie.getParts().set(pFrom, part2);
    currMovie.getParts().set(pTo, part1);
    flamePartPanelList.set(pFrom, panel2);
    flamePartPanelList.set(pTo, panel1);
    flamePartRadioButtonList.set(pFrom, radio2Btn);
    flamePartRadioButtonList.set(pTo, radio1Btn);
    for (JPanel panel : flamePartPanelList) {
      swfAnimatorFlamesPanel.add(panel);
    }
    swfAnimatorFlamesPanel.getParent().validate();
  }

  public void flameMoveDownButton_clicked() {
    int selected = getSelectedFlameRadioButtonIndex();
    if (selected >= 0 && selected < getFlameCount() - 1) {
      swapFlameMoviePart(selected, selected + 1);
      enableControls();
    }
  }

  public void clearAllFlamesButton_clicked() {
    for (JPanel panel : flamePartPanelList) {
      swfAnimatorFlamesPanel.remove(panel);
    }
    currMovie.getParts().clear();
    for (AbstractButton btn : flamePartRadioButtonList) {
      swfAnimatorFlamesButtonGroup.remove(btn);
    }
    flamePartPanelList.clear();
    flamePartRadioButtonList.clear();
    swfAnimatorFlamesPanel.getParent().validate();
    refreshFlameImage();
    enableControls();
  }

  public void movieFromClipboardButton_clicked() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          FlameMovie movie = new JWFMovieReader(prefs).readMovieFromXML(xml);
          if (movie != null) {
            currMovie = movie;
          }
          refreshUI();
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void refreshUI() {
    noRefresh = true;
    try {
      swfAnimatorFramesPerSecondREd.setValue(currMovie.getFramesPerSecond());
      setGlobalScriptToUI(currMovie.getGlobalScript1(), swfAnimatorGlobalScript1Cmb, swfAnimatorGlobalScript1REd);
      setGlobalScriptToUI(currMovie.getGlobalScript2(), swfAnimatorGlobalScript2Cmb, swfAnimatorGlobalScript2REd);
      setGlobalScriptToUI(currMovie.getGlobalScript3(), swfAnimatorGlobalScript3Cmb, swfAnimatorGlobalScript3REd);
      setGlobalScriptToUI(currMovie.getGlobalScript4(), swfAnimatorGlobalScript4Cmb, swfAnimatorGlobalScript4REd);
      setGlobalScriptToUI(currMovie.getGlobalScript5(), swfAnimatorGlobalScript5Cmb, swfAnimatorGlobalScript5REd);
      setXFormScriptToUI(currMovie.getxFormScript1(), swfAnimatorXFormScript1Cmb, swfAnimatorXFormScript1REd);
      setXFormScriptToUI(currMovie.getxFormScript2(), swfAnimatorXFormScript2Cmb, swfAnimatorXFormScript2REd);
      setXFormScriptToUI(currMovie.getxFormScript3(), swfAnimatorXFormScript3Cmb, swfAnimatorXFormScript3REd);
      setXFormScriptToUI(currMovie.getxFormScript4(), swfAnimatorXFormScript4Cmb, swfAnimatorXFormScript4REd);
      setXFormScriptToUI(currMovie.getxFormScript5(), swfAnimatorXFormScript5Cmb, swfAnimatorXFormScript5REd);
      swfAnimatorOutputCmb.setSelectedItem(currMovie.getOutputFormat());
      {
        ResolutionProfile fittingProfile = null;
        ResolutionProfile doubleProfile = null;
        for (int i = 0; i < swfAnimatorResolutionProfileCmb.getItemCount(); i++) {
          ResolutionProfile profile = (ResolutionProfile) swfAnimatorResolutionProfileCmb.getItemAt(i);
          if (profile.getWidth() == currMovie.getFrameWidth() && profile.getHeight() == currMovie.getFrameHeight()) {
            fittingProfile = profile;
            break;
          }
          else if ((profile.getWidth() / 2) == currMovie.getFrameWidth() && (profile.getHeight() / 2) == currMovie.getFrameHeight()) {
            doubleProfile = profile;
          }
        }
        if (fittingProfile != null) {
          swfAnimatorResolutionProfileCmb.setSelectedItem(fittingProfile);
          swfAnimatorHalveSizeButton.setSelected(false);
        }
        else if (doubleProfile != null) {
          swfAnimatorResolutionProfileCmb.setSelectedItem(doubleProfile);
          swfAnimatorHalveSizeButton.setSelected(true);
        }
      }
      {
        QualityProfile fittingProfile = null;
        QualityProfile nearestProfile = null;
        int qualityIndex = QualityProfile.calculateQualityIndex(currMovie.getQuality());
        for (int i = 0; i < swfAnimatorQualityProfileCmb.getItemCount(); i++) {
          QualityProfile profile = (QualityProfile) swfAnimatorQualityProfileCmb.getItemAt(i);
          if (profile.getQuality() == currMovie.getQuality()) {
            fittingProfile = profile;
            break;
          }
          else if (nearestProfile == null || (Math.abs(profile.getQualityIndex() - qualityIndex) < Math.abs(nearestProfile.getQualityIndex() - qualityIndex))) {
            nearestProfile = profile;
          }
        }
        if (fittingProfile != null) {
          swfAnimatorQualityProfileCmb.setSelectedItem(fittingProfile);
        }
        else if (nearestProfile != null) {
          swfAnimatorQualityProfileCmb.setSelectedItem(nearestProfile);
        }
      }

      for (JPanel panel : flamePartPanelList) {
        swfAnimatorFlamesPanel.remove(panel);
      }
      flamePartPanelList.clear();
      for (AbstractButton btn : flamePartRadioButtonList) {
        swfAnimatorFlamesButtonGroup.remove(btn);
      }
      flamePartRadioButtonList.clear();
      swfAnimatorFlamesPanel.getParent().validate();

      swfAnimatorFrameSlider.setValue(1);
      swfAnimatorFrameSlider.setMaximum(currMovie.getFrameCount());
      int frameCount = currMovie.getFrameCount();
      swfAnimatorFramesREd.setValue(frameCount);
      swfAnimatorFrameREd.setValue(1);
      swfAnimatorFrameREd.setMaxValue(frameCount);
      swfAnimatorFromFrameREd.setValue(1);
      swfAnimatorFromFrameREd.setMaxValue(frameCount);
      swfAnimatorToFrameREd.setValue(frameCount);
      swfAnimatorToFrameREd.setMaxValue(frameCount);

      for (FlameMoviePart part : currMovie.getParts()) {
        addFlameToFlamePanel(part);
      }

      refreshFlameImage();
      enableControls();
    }
    finally {
      noRefresh = false;
    }
  }

  private void setXFormScriptToUI(XFormScript pScript, JComboBox pCmb, JWFNumberField pAmountField) {
    pCmb.setSelectedItem(pScript != null && pScript.getScriptType() != null ? pScript.getScriptType() : XFormScriptType.NONE);
    pAmountField.setValue(pScript != null ? pScript.getAmplitude() : 1.0);
    curves.get(pAmountField.getMotionPropertyName()).getCurve().assign(pScript.getAmplitudeCurve());
    new MotionControlsDelegate(parentCtrl, null, parentCtrl.getRootTabbedPane()).enableControl(pAmountField, false);
  }

  private void setGlobalScriptToUI(GlobalScript pScript, JComboBox pCmb, JWFNumberField pAmountField) {
    pCmb.setSelectedItem(pScript != null && pScript.getScriptType() != null ? pScript.getScriptType() : GlobalScriptType.NONE);
    pAmountField.setValue(pScript != null ? pScript.getAmplitude() : 1.0);
    curves.get(pAmountField.getMotionPropertyName()).getCurve().assign(pScript.getAmplitudeCurve());
    new MotionControlsDelegate(parentCtrl, null, parentCtrl.getRootTabbedPane()).enableControl(pAmountField, false);
  }

  public void movieFromDiscButton_clicked() {
    try {
      JFileChooser chooser = new JWFMovieFileChooser(prefs);
      if (prefs.getInputJWFMoviePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputJWFMoviePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(swfAnimatorFlamesPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        FlameMovie movie = new JWFMovieReader(prefs).readMovie(file.getAbsolutePath());
        if (movie != null) {
          currMovie = movie;
        }
        refreshUI();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void movieToClipboardButton_clicked() {
    try {
      updateMovieFields();
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      String xml = new JWFMovieWriter().getMovieXML(currMovie);
      StringSelection data = new StringSelection(xml);
      clipboard.setContents(data, data);
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }

  }

  public void movieToDiscButton_clicked() {
    updateMovieFields();
    try {
      JFileChooser chooser = new JWFMovieFileChooser(prefs);
      if (prefs.getOutputJWFMoviePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getOutputJWFMoviePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(swfAnimatorFlamesPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        new JWFMovieWriter().writeFlame(currMovie, file.getAbsolutePath());
        prefs.setLastOutputJWFMovieFile(file);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlameFromEditor(Flame pFlame) {
    addFlame(pFlame);
    enableControls();
  }

  public void swfAnimatorFrameToEditorBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      parentCtrl.importFlame(flame, true);
      parentCtrl.getRootTabbedPane().setSelectedIndex(0);
    }
  }

  private void deletePartBtn_clicked(FlameMoviePart pPart) {
    for (int i = 0; i < currMovie.getParts().size(); i++) {
      if (currMovie.getParts().get(i).equals(pPart)) {
        removeFlame(i);
        break;
      }
    }
  }

  public void removeFlameButton_clicked() {
    int selected = getSelectedFlameRadioButtonIndex();
    removeFlame(selected);
  }

  public void removeFlame(int pSelected) {
    if (pSelected >= 0 && pSelected < getFlameCount()) {
      swfAnimatorFlamesPanel.remove(pSelected);
      currMovie.getParts().remove(pSelected);
      flamePartPanelList.remove(pSelected);
      swfAnimatorFlamesButtonGroup.remove(flamePartRadioButtonList.get(pSelected));
      flamePartRadioButtonList.remove(pSelected);
      if (flamePartRadioButtonList.size() > 0) {
        flamePartRadioButtonList.get(0).setSelected(true);
      }

      swfAnimatorFlamesPanel.invalidate();
      swfAnimatorFlamesPanel.repaint();
      swfAnimatorFlamesPanel.getParent().invalidate();
      swfAnimatorFlamesPanel.getParent().validate();

      refreshFrameCount();
      enableControls();
    }
  }

  public class PlayMovieThread implements Runnable {
    private boolean finished;
    private boolean forceAbort;
    private final RenderMainFlameThreadFinishEvent finishEvent;

    public PlayMovieThread(RenderMainFlameThreadFinishEvent pFinishEvent) {
      finishEvent = pFinishEvent;
    }

    @Override
    public void run() {
      finished = forceAbort = false;
      try {
        long t0 = Calendar.getInstance().getTimeInMillis();
        for (int i = 1; i < currMovie.getFrameCount(); i++) {
          if (forceAbort) {
            break;
          }
          swfAnimatorFrameSlider.setValue(i);
          getFlamePanel().getParent().invalidate();
          getFlamePanel().getParent().validate();
        }
        long t1 = Calendar.getInstance().getTimeInMillis();
        finished = true;
        finishEvent.succeeded((t1 - t0) * 0.001);
      }
      catch (Throwable ex) {
        finished = true;
        finishEvent.failed(ex);
      }
    }

    public boolean isFinished() {
      return finished;
    }

    public void setForceAbort() {
      forceAbort = true;
    }

  }

  private PlayMovieThread playMovieThread = null;

  private void enablePlayMovieControls() {
    swfAnimatorPlayButton.setText(playMovieThread == null ? "Play" : "Cancel");
  }

  public void playButton_clicked() {
    if (playMovieThread != null) {
      playMovieThread.setForceAbort();
      while (playMovieThread.isFinished()) {
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      playMovieThread = null;
      enablePlayMovieControls();
    }
    else {
      updateMovieFields();
      final int oldFrame = swfAnimatorFrameSlider.getValue();

      RenderMainFlameThreadFinishEvent finishEvent = new RenderMainFlameThreadFinishEvent() {

        @Override
        public void succeeded(double pElapsedTime) {
          try {
          }
          catch (Throwable ex) {
            errorHandler.handleError(ex);
          }
          playMovieThread = null;
          enablePlayMovieControls();
          swfAnimatorFrameSlider.setValue(oldFrame);
          refreshFlameImage();
        }

        @Override
        public void failed(Throwable exception) {
          errorHandler.handleError(exception);
          playMovieThread = null;
          enablePlayMovieControls();
          swfAnimatorFrameSlider.setValue(oldFrame);
          refreshFlameImage();
        }
      };

      playMovieThread = new PlayMovieThread(finishEvent);
      enablePlayMovieControls();
      new Thread(playMovieThread).start();
    }
  }

  private class MotionControlsDelegate extends AbstractControlsDelegate {

    public MotionControlsDelegate(TinaController pOwner, TinaControllerData pData, JTabbedPane pRootTabbedPane) {
      super(pOwner, pData, pRootTabbedPane, false);
    }

    @Override
    public String getEditingTitle(JWFNumberField sender) {
      return "motion script \"" + sender.getLinkedLabelControl().getText() + "\"";
    }

    @Override
    public MotionCurve getCurveToEdit(String pPropName) {
      return curves.get(pPropName).getCurve();
    }

    @Override
    public double getInitialValue(String pPropName) {
      return curves.get(pPropName).getField().getDoubleValue();
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

  }

  public void editGlobalMotionCurve(JWFNumberField pSender) {
    new MotionControlsDelegate(parentCtrl, null, parentCtrl.getRootTabbedPane()).editMotionCurve(pSender);
    refreshFlameImage();
  }

  public void editXFormMotionCurve(JWFNumberField pSender) {
    new MotionControlsDelegate(parentCtrl, null, parentCtrl.getRootTabbedPane()).editMotionCurve(pSender);
    refreshFlameImage();
  }

}
