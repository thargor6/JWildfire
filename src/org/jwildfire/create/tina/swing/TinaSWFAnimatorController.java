/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.animate.FlameMorphType;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.animate.FlameMoviePart;
import org.jwildfire.create.tina.animate.GlobalScript;
import org.jwildfire.create.tina.animate.GlobalScriptType;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThread;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThreadController;
import org.jwildfire.create.tina.animate.SequenceOutputType;
import org.jwildfire.create.tina.animate.XFormScript;
import org.jwildfire.create.tina.animate.XFormScriptType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.io.FlameMovieReader;
import org.jwildfire.create.tina.io.FlameMovieWriter;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.randommovie.RandomMovieGenerator;
import org.jwildfire.create.tina.randommovie.RandomMovieGeneratorList;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ANBFileFilter;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.transform.ComposeTransformer;

public class TinaSWFAnimatorController implements SWFAnimationRenderThreadController, FlameHolder {
  public static final int PAGE_INDEX = 4;
  private SWFAnimationRenderThread renderThread = null;
  private FlameMovie currMovie;
  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;

  private final List<ScriptContainer> globalScripts;
  private final List<ScriptContainer> xFormScripts;

  private final JWFNumberField swfAnimatorFramesREd;
  private final JWFNumberField swfAnimatorFrameREd;
  private final JWFNumberField swfAnimatorFramesPerSecondREd;
  private final JButton swfAnimatorGenerateButton;
  private final JComboBox swfAnimatorResolutionProfileCmb;
  private final JComboBox swfAnimatorQualityProfileCmb;
  private final JComboBox swfAnimatorOutputTypeCmb;
  private final JButton swfAnimatorLoadFlameFromMainButton;
  private final JButton swfAnimatorLoadFlameFromClipboardButton;
  private final JButton swfAnimatorLoadFlameButton;
  private final JProgressBar swfAnimatorProgressBar;
  private final JButton swfAnimatorCancelButton;
  private final ProgressUpdater renderProgressUpdater;
  private final JPanel swfAnimatorPreviewRootPanel;
  private final JSlider swfAnimatorFrameSlider;
  private final JPanel swfAnimatorFlamesPanel;
  private final ButtonGroup swfAnimatorFlamesButtonGroup;
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
  private final JWFNumberField swfAnimatorMotionBlurLengthREd;
  private final JWFNumberField swfAnimatorMotionBlurTimeStepREd;
  private final JCheckBox swfAnimatorCompatCBx;
  private FlamePanel flamePanel;
  private final MotionControlsDelegate motionControlsDelegate;
  private final List<JPanel> flamePartPanelList = new ArrayList<JPanel>();
  private final List<JRadioButton> flamePartRadioButtonList = new ArrayList<JRadioButton>();
  private final JPanel randomBatchPanel;
  private JScrollPane randomBatchScrollPane;

  private boolean noRefresh;

  private static class MotionCurveEditInfo {
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
      JComboBox[] pSWFAnimatorGlobalScriptCmbArray, JWFNumberField[] pSWFAnimatorGlobalScriptREdArray,
      JComboBox[] pSWFAnimatorXFormScriptCmbArray, JWFNumberField[] pSWFAnimatorXFormScriptREdArray,
      JWFNumberField pSWFAnimatorFramesREd, JWFNumberField pSWFAnimatorFramesPerSecondREd,
      JButton pSWFAnimatorGenerateButton, JComboBox pSWFAnimatorResolutionProfileCmb,
      JButton pSWFAnimatorLoadFlameFromMainButton,
      JButton pSWFAnimatorLoadFlameFromClipboardButton, JButton pSWFAnimatorLoadFlameButton,
      JProgressBar pSWFAnimatorProgressBar, JButton pSWFAnimatorCancelButton,
      ProgressUpdater pRenderProgressUpdater,
      JPanel pSWFAnimatorPreviewRootPanel, JSlider pSWFAnimatorFrameSlider,
      JWFNumberField pSWFAnimatorFrameREd, JPanel pSWFAnimatorFlamesPanel, ButtonGroup pSWFAnimatorFlamesButtonGroup,
      JButton pSWFAnimatorMoveUpButton, JButton pSWFAnimatorMoveDownButton,
      JButton pSWFAnimatorRemoveFlameButton, JButton pSWFAnimatorRemoveAllFlamesButton, JButton pSWFAnimatorMovieFromClipboardButton,
      JButton pSWFAnimatorMovieFromDiskButton, JButton pSWFAnimatorMovieToClipboardButton, JButton pSWFAnimatorMovieToDiskButton,
      JButton pSWFAnimatorFrameToEditorBtn, JButton pSWFAnimatorPlayButton, JWFNumberField pSwfAnimatorMotionBlurLengthREd,
      JWFNumberField pSwfAnimatorMotionBlurTimeStepREd, JPanel pRandomMoviePanel, JComboBox pSWFAnimatorQualityProfileCmb,
      JComboBox pSWFAnimatorOutputTypeCmb, JCheckBox pSWFAnimatorCompatCBx) {
    noRefresh = true;
    try {
      parentCtrl = pParentCtrl;
      prefs = pPrefs;
      currMovie = new FlameMovie(pPrefs);
      //      currMovie.getGlobalScripts()[0] = new GlobalScript(GlobalScriptType.ROTATE_PITCH, 1.0);
      errorHandler = pErrorHandler;
      randomBatchPanel = pRandomMoviePanel;

      globalScripts = new ArrayList<ScriptContainer>();
      for (int i = 0; i < pSWFAnimatorGlobalScriptCmbArray.length; i++) {
        ScriptContainer container = new ScriptContainer(pSWFAnimatorGlobalScriptCmbArray[i], pSWFAnimatorGlobalScriptREdArray[i]);
        globalScripts.add(container);
        curves.put("globalScript" + (i + 1), new MotionCurveEditInfo(container.getMotionCurve(), container.getScriptREd()));
      }

      xFormScripts = new ArrayList<ScriptContainer>();
      for (int i = 0; i < pSWFAnimatorXFormScriptCmbArray.length; i++) {
        ScriptContainer container = new ScriptContainer(pSWFAnimatorXFormScriptCmbArray[i], pSWFAnimatorXFormScriptREdArray[i]);
        xFormScripts.add(container);
        curves.put("xFormScript" + (i + 1), new MotionCurveEditInfo(container.getMotionCurve(), container.getScriptREd()));
      }

      swfAnimatorFramesREd = pSWFAnimatorFramesREd;
      swfAnimatorFrameREd = pSWFAnimatorFrameREd;
      swfAnimatorFramesPerSecondREd = pSWFAnimatorFramesPerSecondREd;
      swfAnimatorGenerateButton = pSWFAnimatorGenerateButton;
      swfAnimatorResolutionProfileCmb = pSWFAnimatorResolutionProfileCmb;
      swfAnimatorLoadFlameFromMainButton = pSWFAnimatorLoadFlameFromMainButton;
      swfAnimatorLoadFlameFromClipboardButton = pSWFAnimatorLoadFlameFromClipboardButton;
      swfAnimatorLoadFlameButton = pSWFAnimatorLoadFlameButton;
      swfAnimatorProgressBar = pSWFAnimatorProgressBar;
      swfAnimatorCancelButton = pSWFAnimatorCancelButton;
      renderProgressUpdater = pRenderProgressUpdater;
      swfAnimatorPreviewRootPanel = pSWFAnimatorPreviewRootPanel;
      swfAnimatorFrameSlider = pSWFAnimatorFrameSlider;
      swfAnimatorFlamesPanel = pSWFAnimatorFlamesPanel;
      swfAnimatorFlamesButtonGroup = pSWFAnimatorFlamesButtonGroup;
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
      swfAnimatorMotionBlurLengthREd = pSwfAnimatorMotionBlurLengthREd;
      swfAnimatorMotionBlurTimeStepREd = pSwfAnimatorMotionBlurTimeStepREd;
      swfAnimatorQualityProfileCmb = pSWFAnimatorQualityProfileCmb;
      swfAnimatorOutputTypeCmb = pSWFAnimatorOutputTypeCmb;
      swfAnimatorCompatCBx = pSWFAnimatorCompatCBx;

      int frameCount = prefs.getTinaRenderMovieFrames();
      swfAnimatorFrameSlider.setValue(1);
      swfAnimatorFrameSlider.setMinimum(1);
      swfAnimatorFrameSlider.setMaximum(frameCount);
      swfAnimatorFramesREd.setValue(frameCount);
      swfAnimatorFrameREd.setMaxValue(frameCount);
      swfAnimatorFrameREd.setValue(1);
      swfAnimatorMotionBlurLengthREd.setValue(32);
      swfAnimatorMotionBlurTimeStepREd.setValue(0.01);
      motionControlsDelegate = new MotionControlsDelegate(parentCtrl, null, parentCtrl.getRootPanel());

      randomBatch.add(0, new MovieThumbnail(currMovie, null));
      updateThumbnails();
    }
    finally {
      noRefresh = false;
    }
  }

  protected void enableControls() {
    boolean rendering = renderThread != null;
    for (ScriptContainer container : globalScripts) {
      container.getScriptCmb().setEnabled(!rendering);
      motionControlsDelegate.enableControl(container.getScriptREd(), rendering);
    }
    for (ScriptContainer container : xFormScripts) {
      container.getScriptCmb().setEnabled(!rendering);
      motionControlsDelegate.enableControl(container.getScriptREd(), rendering);
    }
    swfAnimatorFrameREd.setEnabled(!rendering);
    swfAnimatorFramesREd.setEnabled(!rendering);
    swfAnimatorFramesREd.setEditable(false);
    swfAnimatorMotionBlurLengthREd.setEnabled(!rendering);
    swfAnimatorMotionBlurTimeStepREd.setEnabled(!rendering);
    swfAnimatorFramesPerSecondREd.setEnabled(!rendering);
    swfAnimatorGenerateButton.setEnabled(!rendering && currMovie.getFrameCount() > 0);
    swfAnimatorGenerateButton.setVisible(!rendering);
    swfAnimatorPlayButton.setEnabled(swfAnimatorGenerateButton.isEnabled());
    swfAnimatorResolutionProfileCmb.setEnabled(!rendering);
    swfAnimatorQualityProfileCmb.setEnabled(!rendering);
    swfAnimatorOutputTypeCmb.setEnabled(!rendering);
    swfAnimatorLoadFlameFromMainButton.setEnabled(!rendering);
    swfAnimatorLoadFlameFromClipboardButton.setEnabled(!rendering);
    swfAnimatorLoadFlameButton.setEnabled(!rendering);
    swfAnimatorCancelButton.setEnabled(rendering && !renderThread.isCancelSignalled());
    swfAnimatorCancelButton.setVisible(rendering);
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
    swfAnimatorCompatCBx.setEnabled(!rendering);
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
    boolean firstPart = currMovie.getParts().size() == 0;
    int frameCount, frameMorphCount;
    if (firstPart) {
      if (pFlame.getMotionBlurLength() > 0) {
        swfAnimatorMotionBlurLengthREd.setValue(pFlame.getMotionBlurLength());
        swfAnimatorMotionBlurTimeStepREd.setValue(pFlame.getMotionBlurTimeStep());
      }
      frameCount = 120;
      frameMorphCount = 60;
    }
    else {
      FlameMoviePart prevPart = currMovie.getParts().get(currMovie.getParts().size() - 1);
      frameCount = prevPart.getFrameCount();
      frameMorphCount = prevPart.getFrameMorphCount();
    }
    FlameMoviePart part = new FlameMoviePart();
    part.setFlame(pFlame);
    part.setFrameCount(frameCount);
    part.setFrameMorphCount(frameMorphCount);
    addFlameToFlamePanel(part);
    currMovie.addPart(part);
    refreshFrameCount();
    previewFlameImage();
    clearCurrentPreview();
    updateThumbnails();
  }

  private void clearCurrentPreview() {
    for (int i = 0; i < randomBatch.size(); i++) {
      FlameMovie bMovie = randomBatch.get(i).getMovie();
      if (bMovie == currMovie) {
        randomBatch.get(i).preview = null;
        ImagePanel pnl = randomBatch.get(i).getImgPanel();
        if (pnl != null) {
          pnl.replaceImage(randomBatch.get(i).getPreview(prefs.getTinaRenderPreviewQuality() / 2));
          pnl.repaint();
        }
        break;
      }
    }
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

      swfAnimatorFramesREd.setValue(frameCount);
      swfAnimatorFrameREd.setMaxValue(frameCount);
      if (value > frameCount) {
        swfAnimatorFrameSlider.setValue(frameCount);
        swfAnimatorFrameREd.setValue(frameCount);
        previewFlameImage();
      }
    }
    finally {
      noRefresh = false;
    }
  }

  private void addFlameToFlamePanel(final FlameMoviePart pPart) {
    final int PANEL_HEIGHT = 212;
    final int LABEL_WIDTH = 96;
    final int FIELD_WIDTH = 66;
    final int FIELD_HEIGHT = 24;
    final int LABEL_HEIGHT = 24;
    final int BUTTON_WIDTH = 48;
    final int BORDER_SIZE = 4;
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
      SimpleImage img = createPartPreview(pPart, imageWidth, imageHeight);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(BORDER_SIZE, BORDER_SIZE);
      panel.add(imgPanel);
    }

    int yOff = 2 * BORDER_SIZE + imageHeight;
    {
      JLabel framesLbl = new JLabel("Duration (frames)");
      framesLbl.setToolTipText("Number of frames to show this flame");
      framesLbl.setBounds(xOff + BORDER_SIZE, yOff, LABEL_WIDTH, LABEL_HEIGHT);
      panel.add(framesLbl);
      final JWFNumberField framesField = new JWFNumberField();
      framesField.setOnlyIntegers(true);
      framesField.setValue(pPart.getFrameCount());
      framesField.setHasMinValue(true);
      framesField.setFont(new Font("Dialog", Font.PLAIN, 10));
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
      framesMorphLbl.setToolTipText("Number of frames to change this flame to the next; 0 for sudden change");
      framesMorphLbl.setBounds(xOff + BORDER_SIZE, yOff, LABEL_WIDTH, LABEL_HEIGHT);
      panel.add(framesMorphLbl);
      final JWFNumberField framesMorphField = new JWFNumberField();
      framesMorphField.setOnlyIntegers(true);
      framesMorphField.setHasMinValue(true);
      framesMorphField.setMinValue(0.0);
      framesMorphField.setFont(new Font("Dialog", Font.PLAIN, 10));
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
      JLabel morphTypeLbl = new JLabel("Morph type");
      morphTypeLbl.setToolTipText("How to morph this flame to the next one");
      morphTypeLbl.setBounds(xOff + BORDER_SIZE, yOff, LABEL_WIDTH, LABEL_HEIGHT);
      panel.add(morphTypeLbl);
      final JComboBox morphTypeCmb = new JComboBox();
      morphTypeCmb.setToolTipText("<html>FADE: fade in the next flame and fade out this one<br>"
          + "MORPH: try to change the settings from this flame to those of the next one</html>");
      morphTypeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          morpthTypeChanged(morphTypeCmb, pPart);
        }
      });
      morphTypeCmb.setSize(new Dimension(125, 22));
      morphTypeCmb.setFont(new Font("Dialog", Font.PLAIN, 10));
      boolean oldNoRefresh = noRefresh;
      try {
        noRefresh = true;
        morphTypeCmb.addItem(FlameMorphType.FADE);
        morphTypeCmb.addItem(FlameMorphType.MORPH);
      }
      finally {
        noRefresh = oldNoRefresh;
      }

      morphTypeCmb.setSelectedItem(pPart.getFlameMorphType());
      morphTypeCmb.setBounds(xOff + BORDER_SIZE + LABEL_WIDTH, yOff, FIELD_WIDTH, FIELD_HEIGHT);
      panel.add(morphTypeCmb);
    }

    yOff += FIELD_HEIGHT;
    int btnOff = BORDER_SIZE;
    {
      JButton editButton = new JButton("E");
      editButton.setBounds(btnOff, yOff, BUTTON_WIDTH, FIELD_HEIGHT);
      editButton.setFont(new Font("Dialog", Font.BOLD, 10));
      editButton.setToolTipText("Edit flame in editor");
      editButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          editPartBtn_clicked(pPart);
        }
      });
      panel.add(editButton);
      btnOff += BUTTON_WIDTH + 1;
    }
    {
      JButton replaceButton = new JButton("R");
      replaceButton.setBounds(btnOff, yOff, BUTTON_WIDTH, FIELD_HEIGHT);
      replaceButton.setFont(new Font("Dialog", Font.BOLD, 10));
      replaceButton.setToolTipText("Replace flame with flame from editor");
      replaceButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          replacePartBtn_clicked(pPart);
        }
      });
      panel.add(replaceButton);
      btnOff += BUTTON_WIDTH + 1;
    }
    {
      JButton delButton = new JButton("D");
      delButton.setBounds(btnOff, yOff, BUTTON_WIDTH, FIELD_HEIGHT);
      delButton.setFont(new Font("Dialog", Font.BOLD, 10));
      delButton.setToolTipText("Remove flame from movie");
      delButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          deletePartBtn_clicked(pPart);
        }
      });
      panel.add(delButton);
      btnOff += BUTTON_WIDTH + 1;
    }

    JRadioButton selectButton;
    {
      selectButton = new JRadioButton("");
      selectButton.setBounds(btnOff, yOff, FIELD_WIDTH + 1, FIELD_HEIGHT);
      selectButton.setFont(new Font("Dialog", Font.PLAIN, 10));
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

  private SimpleImage createPartPreview(final FlameMoviePart pPart, int imageWidth, int imageHeight) {
    RenderInfo info = new RenderInfo(imageWidth, imageHeight, RenderMode.PREVIEW);
    Flame flame = pPart.getFlame().makeCopy();
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(imageWidth);
    flame.setHeight(imageHeight);
    FlameRenderer renderer = new FlameRenderer(flame, prefs, false, false);
    RenderedFlame res = renderer.renderFlame(info);
    return res.getImage();
  }

  protected void editPartBtn_clicked(FlameMoviePart pPart) {
    parentCtrl.importFlame(pPart.getFlame(), true);
    parentCtrl.getDesktop().showJFrame(MainEditorFrame.class);
  }

  protected void replacePartBtn_clicked(FlameMoviePart pPart) {
    Flame flame = parentCtrl.getCurrFlame();
    if (flame != null) {
      pPart.setFlame(flame.makeCopy());
      int idx = currMovie.getParts().indexOf(pPart);
      if (idx >= 0) {
        JPanel pnl = flamePartPanelList.get(idx);
        for (Component cmp : pnl.getComponents()) {
          if (cmp instanceof ImagePanel) {
            ImagePanel imgPnl = (ImagePanel) cmp;
            int width = imgPnl.getBounds().width;
            int height = imgPnl.getBounds().height;
            SimpleImage img = createPartPreview(pPart, width, height);
            imgPnl.setImage(img, imgPnl.getBounds().x, imgPnl.getBounds().y, width);
            imgPnl.invalidate();
            imgPnl.validate();
            pnl.getParent().repaint();
            break;
          }
        }
      }

      previewFlameImage();
      clearCurrentPreview();
      updateThumbnails();
    }
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

  protected void updateMovieFields() {
    double framesPerSecond = swfAnimatorFramesPerSecondREd.getDoubleValue();
    ResolutionProfile resProfile = getResolutionProfile();
    int frameWidth = resProfile.getWidth();
    int frameHeight = resProfile.getHeight();

    for (int i = 0; i < globalScripts.size(); i++) {
      ScriptContainer container = globalScripts.get(i);
      currMovie.getGlobalScripts()[i] = getGlobalScriptFromUI(container.getScriptCmb(), container.getScriptREd());
    }
    for (int i = 0; i < xFormScripts.size(); i++) {
      ScriptContainer container = xFormScripts.get(i);
      currMovie.getxFormScripts()[i] = getXFormScriptFromUI(container.getScriptCmb(), container.getScriptREd());
    }

    currMovie.setFrameWidth(frameWidth);
    currMovie.setFrameHeight(frameHeight);
    currMovie.setQuality(getQualityProfile().getQuality());
    currMovie.setFramesPerSecond(framesPerSecond);
    currMovie.setMotionBlurLength(swfAnimatorMotionBlurLengthREd.getIntValue());
    currMovie.setMotionBlurTimeStep(swfAnimatorMotionBlurTimeStepREd.getDoubleValue());
    currMovie.setSequenceOutputType((SequenceOutputType) swfAnimatorOutputTypeCmb.getSelectedItem());
  }

  public void generateButton_clicked() {
    try {
      JFileChooser chooser = createOutputFileChooser();
      if (chooser.showSaveDialog(swfAnimatorPreviewRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastOutputMovieFlamesFile(file);
        updateMovieFields();
        renderThread = new SWFAnimationRenderThread(this, currMovie, file.getAbsolutePath());
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

  private JFileChooser createOutputFileChooser() {
    switch (currMovie.getSequenceOutputType()) {
      case ANB: {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new ANBFileFilter());
        if (prefs.getOutputImagePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        return chooser;
      }
      case PNG_IMAGES: {
        JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
        if (prefs.getOutputImagePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        return chooser;
      }
      default: {
        JFileChooser chooser = new FlameFileChooser(prefs);
        if (prefs.getMovieFlamesPath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getMovieFlamesPath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        return chooser;
      }
    }
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
      res = new QualityProfile(false, "200", 200, false, false);
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
      flamePanel = new FlamePanel(prefs, img, 0, 0, swfAnimatorPreviewRootPanel.getWidth(), this, null, null);
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

  public void refreshFlameImage(boolean pQuickRender) {
    if (!noRefresh && currMovie.getFrameCount() > 0) {
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
            if (pQuickRender) {
              renderer.setProgressUpdater(null);
              flame.setSampleDensity(1.0);
              flame.setSpatialFilterRadius(0.0);
            }
            else {
              renderer.setProgressUpdater(renderProgressUpdater);
              flame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
            }
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

      int motionBlurLength = swfAnimatorMotionBlurLengthREd.getIntValue();
      double motionBlurTimeStep = swfAnimatorMotionBlurTimeStepREd.getDoubleValue();

      List<GlobalScript> editedGlobalScripts = new ArrayList<GlobalScript>();
      for (ScriptContainer container : globalScripts) {
        editedGlobalScripts.add(getGlobalScriptFromUI(container.getScriptCmb(), container.getScriptREd()));
      }

      List<XFormScript> editedXFormScripts = new ArrayList<XFormScript>();
      for (ScriptContainer container : xFormScripts) {
        editedXFormScripts.add(getXFormScriptFromUI(container.getScriptCmb(), container.getScriptREd()));
      }

      try {
        Flame res = flame.makeCopy();
        for (GlobalScript script : editedGlobalScripts) {
          AnimationService.addMotionCurve(res, script, frame, frameCount, fps);
        }
        for (XFormScript script : editedXFormScripts) {
          AnimationService.addMotionCurve(res, script, frame, frameCount, fps);
        }
        res.setFrame(frame);
        res.setMotionBlurLength(motionBlurLength);
        res.setMotionBlurTimeStep(motionBlurTimeStep);
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
    previewFlameImage();
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

  private void morpthTypeChanged(JComboBox pMorphTypeCmb, FlameMoviePart pPart) {
    if (noRefresh) {
      return;
    }
    try {
      pPart.setFlameMorphType((FlameMorphType) pMorphTypeCmb.getSelectedItem());
      previewFlameImage();
      clearCurrentPreview();
      updateThumbnails();
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
    clearCurrentPreview();
    updateThumbnails();
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
    swfAnimatorFlamesPanel.invalidate();
    swfAnimatorFlamesPanel.repaint();
    swfAnimatorFlamesPanel.getParent().invalidate();
    swfAnimatorFlamesPanel.getParent().validate();
    
    refreshFrameCount();
    previewFlameImage();
    enableControls();
    clearCurrentPreview();
    updateThumbnails();
  }
  
  public void compatCBx_changed() {
    currMovie.setCompat(swfAnimatorCompatCBx.isSelected());
    previewFlameImage();
    enableControls();
    clearCurrentPreview();
    updateThumbnails();
  }

  public void movieFromClipboardButton_clicked() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          FlameMovie movie = new FlameMovieReader(prefs).readMovieFromXML(xml);
          if (movie != null) {
            randomBatch.add(0, new MovieThumbnail(movie, null));
            currMovie = movie;
            updateThumbnails();
          }
          refreshControls();
          previewFlameImage();
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  protected void refreshControls() {
    noRefresh = true;
    try {
      swfAnimatorFramesPerSecondREd.setValue(currMovie.getFramesPerSecond());
      for (int i = 0; i < globalScripts.size(); i++) {
        ScriptContainer container = globalScripts.get(i);
        setGlobalScriptToUI(currMovie.getGlobalScripts()[i], container.getScriptCmb(), container.getScriptREd());
      }
      for (int i = 0; i < xFormScripts.size(); i++) {
        ScriptContainer container = xFormScripts.get(i);
        setXFormScriptToUI(currMovie.getxFormScripts()[i], container.getScriptCmb(), container.getScriptREd());
      }
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
        }
        else if (doubleProfile != null) {
          swfAnimatorResolutionProfileCmb.setSelectedItem(doubleProfile);
        }
      }
      {
        QualityProfile qualityProfile = null;
        for (int i = 0; i < swfAnimatorQualityProfileCmb.getItemCount(); i++) {
          QualityProfile profile = (QualityProfile) swfAnimatorQualityProfileCmb.getItemAt(i);
          if (qualityProfile == null) {
            qualityProfile = profile;
          }
          else if (profile.getQuality() == currMovie.getQuality()) {
            qualityProfile = profile;
            break;
          }
        }
        if (qualityProfile != null) {
          swfAnimatorQualityProfileCmb.setSelectedItem(qualityProfile);
        }
      }
      
      swfAnimatorOutputTypeCmb.setSelectedItem(currMovie.getSequenceOutputType());

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
      swfAnimatorMotionBlurLengthREd.setValue(currMovie.getMotionBlurLength());
      swfAnimatorMotionBlurTimeStepREd.setValue(currMovie.getMotionBlurTimeStep());
      swfAnimatorCompatCBx.setSelected(currMovie.getCompat());
      for (FlameMoviePart part : currMovie.getParts()) {
        addFlameToFlamePanel(part);
      }

      enableControls();
    }
    finally {
      noRefresh = false;
    }
  }

  private void setXFormScriptToUI(XFormScript pScript, JComboBox pCmb, JWFNumberField pAmountField) {
    if (pScript != null) {
      pCmb.setSelectedItem(pScript.getScriptType() != null ? pScript.getScriptType() : XFormScriptType.NONE);
      pAmountField.setValue(pScript.getAmplitude());
      curves.get(pAmountField.getMotionPropertyName()).getCurve().assign(pScript.getAmplitudeCurve());
    }
    else {
      pCmb.setSelectedItem(XFormScriptType.NONE);
      pAmountField.setValue(1.0);
    }
    new MotionControlsDelegate(parentCtrl, null, parentCtrl.getRootPanel()).enableControl(pAmountField, false);
  }

  private void setGlobalScriptToUI(GlobalScript pScript, JComboBox pCmb, JWFNumberField pAmountField) {
    if (pScript != null) {
      pCmb.setSelectedItem(pScript.getScriptType() != null ? pScript.getScriptType() : GlobalScriptType.NONE);
      pAmountField.setValue(pScript.getAmplitude());
      curves.get(pAmountField.getMotionPropertyName()).getCurve().assign(pScript.getAmplitudeCurve());
    }
    else {
      pCmb.setSelectedItem(GlobalScriptType.NONE);
      pAmountField.setValue(1.0);
    }
    new MotionControlsDelegate(parentCtrl, null, parentCtrl.getRootPanel()).enableControl(pAmountField, false);
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
        FlameMovie movie = new FlameMovieReader(prefs).readMovie(file.getAbsolutePath());
        if (movie != null) {
          randomBatch.add(0, new MovieThumbnail(movie, null));
          currMovie = movie;
          updateThumbnails();
        }
        refreshControls();
        previewFlameImage();
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
      String xml = new FlameMovieWriter().getMovieXML(currMovie);
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
        new FlameMovieWriter().writeFlame(currMovie, file.getAbsolutePath());
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
      parentCtrl.getDesktop().showJFrame(MainEditorFrame.class);
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
      clearCurrentPreview();
      updateThumbnails();
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
          previewFlameImage();
        }

        @Override
        public void failed(Throwable exception) {
          errorHandler.handleError(exception);
          playMovieThread = null;
          enablePlayMovieControls();
          swfAnimatorFrameSlider.setValue(oldFrame);
          previewFlameImage();
        }
      };

      playMovieThread = new PlayMovieThread(finishEvent);
      enablePlayMovieControls();
      new Thread(playMovieThread).start();
    }
  }

  private class MotionControlsDelegate extends AbstractControlsDelegate {

    public MotionControlsDelegate(TinaController pOwner, TinaControllerData pData, JPanel pRootPanel) {
      super(pOwner, pData, pRootPanel, false);
    }

    @Override
    public String getEditingTitle(JWFNumberField sender) {
      return "\"" + sender.getLinkedLabelControl().getText() + "\"";
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
    motionControlsDelegate.editMotionCurve(pSender);
    previewFlameImage();
  }

  public void editXFormMotionCurve(JWFNumberField pSender) {
    motionControlsDelegate.editMotionCurve(pSender);
    previewFlameImage();
  }

  private class MovieThumbnail {
    private FlameMovie movie;
    private SimpleImage preview;
    private ImagePanel imgPanel;

    public MovieThumbnail(FlameMovie pMovie, SimpleImage pPreview) {
      movie = pMovie;
      preview = pPreview;
    }

    private void generatePreview(int pQuality) {
      preview = new SimpleImage(IMG_WIDTH * IMG_COUNT, IMG_HEIGHT - 1);
      for (int i = 0; i < IMG_COUNT; i++) {

        RenderInfo info = new RenderInfo(IMG_WIDTH, IMG_HEIGHT, RenderMode.PREVIEW);
        Flame renderFlame;
        if (movie.getParts().size() > 0) {
          int frame = (int) ((double) movie.getFrameCount() / ((double) IMG_COUNT + 1) * i + 0.5);
          Flame morphedFlame = movie.getFlame(frame);
          renderFlame = movie.createAnimatedFlame(morphedFlame, frame);
        }
        else {
          renderFlame = new Flame();
        }

        double wScl = (double) info.getImageWidth() / (double) renderFlame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) renderFlame.getHeight();
        renderFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * renderFlame.getPixelsPerUnit());
        renderFlame.setWidth(IMG_WIDTH);
        renderFlame.setHeight(IMG_HEIGHT);
        renderFlame.setSpatialFilterRadius(0.0);
        FlameRenderer renderer = new FlameRenderer(renderFlame, prefs, false, false);
        renderFlame.setSampleDensity(pQuality / IMG_COUNT);
        RenderedFlame res = renderer.renderFlame(info);
        SimpleImage foreground = res.getImage();
        ComposeTransformer composeT = new ComposeTransformer();
        composeT.setHAlign(ComposeTransformer.HAlignment.OFF);
        composeT.setVAlign(ComposeTransformer.VAlignment.OFF);
        composeT.setTop(0);
        composeT.setLeft(i * IMG_WIDTH);
        composeT.setForegroundImage(foreground);
        composeT.transformImage(preview);
      }
    }

    public SimpleImage getPreview(int pQuality) {
      if (preview == null) {
        generatePreview(pQuality);
      }
      return preview;
    }

    public FlameMovie getMovie() {
      return movie;
    }

    public ImagePanel getImgPanel() {
      return imgPanel;
    }

    public void setImgPanel(ImagePanel imgPanel) {
      this.imgPanel = imgPanel;
    }
  }

  private List<MovieThumbnail> randomBatch = new ArrayList<MovieThumbnail>();

  private static final int IMG_WIDTH = 66;
  private static final int IMG_COUNT = 5;
  private static final int IMG_HEIGHT = 50;
  private static final int BORDER_SIZE = 8;

  public void updateThumbnails() {
    if (randomBatchScrollPane != null) {
      randomBatchPanel.remove(randomBatchScrollPane);
      randomBatchScrollPane = null;
    }
    int panelWidth = IMG_WIDTH * IMG_COUNT + 2 * BORDER_SIZE;
    int panelHeight = (IMG_HEIGHT + BORDER_SIZE) * randomBatch.size();
    JPanel batchPanel = new JPanel();
    batchPanel.setLayout(null);
    batchPanel.setSize(panelWidth, panelHeight);
    batchPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    for (int i = 0; i < randomBatch.size(); i++) {
      SimpleImage img = randomBatch.get(i).getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
      // add it to the main panel
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(BORDER_SIZE, i * IMG_HEIGHT + (i + 1) * BORDER_SIZE);
      randomBatch.get(i).setImgPanel(imgPanel);
      final int idx = i;
      addRemoveButton(imgPanel, idx);
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
            importFromRandomBatch(idx);
          }
        }
      });
      batchPanel.add(imgPanel);
    }
    randomBatchScrollPane = new JScrollPane(batchPanel);
    randomBatchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    randomBatchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    randomBatchPanel.add(randomBatchScrollPane, BorderLayout.CENTER);
    randomBatchPanel.validate();
  }

  private void addRemoveButton(ImagePanel pImgPanel, final int pIdx) {
    final int BTN_WIDTH = 20;
    final int BTN_HEIGHT = 20;
    final int BORDER = 0;
    JButton btn = new JButton();
    btn.setMinimumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setMaximumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/removeThumbnail.gif")));
    btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        removeThumbnail(pIdx);
      }
    });

    pImgPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, BORDER, BORDER));
    pImgPanel.add(btn);
    pImgPanel.invalidate();
  }

  protected void removeThumbnail(int pIdx) {
    try {
      int currMovieIdx = -1;
      for (int i = 0; i < randomBatch.size(); i++) {
        FlameMovie bMovie = randomBatch.get(i).getMovie();
        if (bMovie == currMovie) {
          currMovieIdx = i;
          break;
        }
      }
      if (pIdx == currMovieIdx) {
        throw new Exception("Sorry, the currently selected movie can't be deleted from the movie-ribbon");
      }
      randomBatch.remove(pIdx);
      updateThumbnails();
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFromRandomBatch(int pIdx) {
    if (pIdx >= 0 && pIdx < randomBatch.size()) {
      currMovie = randomBatch.get(pIdx).getMovie();

      refreshControls();
      previewFlameImage();
    }
  }

  public boolean createRandomBatch(int pCount, String pGeneratorname) {
    if (prefs.getTinaRandomBatchRefreshType() == RandomBatchRefreshType.CLEAR) {
      randomBatch.clear();
    }
    int imgCount = prefs.getTinaRandomMovieBatchSize();
    List<SimpleImage> imgList = new ArrayList<SimpleImage>();
    int maxCount = (pCount > 0 ? pCount : imgCount);
    renderProgressUpdater.initProgress(maxCount);
    RandomMovieGenerator randGen = RandomMovieGeneratorList.getRandomMovieGeneratorInstance(pGeneratorname, true);
    for (int i = 0; i < maxCount; i++) {
      MovieThumbnail thumbnail;
      thumbnail = new MovieThumbnail(randGen.createMovie(prefs), null);
      SimpleImage img = thumbnail.getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
      if (prefs.getTinaRandomBatchRefreshType() == RandomBatchRefreshType.INSERT) {
        randomBatch.add(0, thumbnail);
        imgList.add(0, img);
      }
      else {
        randomBatch.add(thumbnail);
        imgList.add(img);
      }
      renderProgressUpdater.updateProgress(i + 1);

    }
    updateThumbnails();
    return true;
  }

  public static class ScriptContainer {
    private final JComboBox scriptCmb;
    private final JWFNumberField scriptREd;
    private final MotionCurve motionCurve;

    public ScriptContainer(JComboBox pScriptCmb, JWFNumberField pSscriptREd) {
      scriptCmb = pScriptCmb;
      scriptREd = pSscriptREd;
      motionCurve = new MotionCurve();
    }

    public JComboBox getScriptCmb() {
      return scriptCmb;
    }

    public JWFNumberField getScriptREd() {
      return scriptREd;
    }

    public MotionCurve getMotionCurve() {
      return motionCurve;
    }

  }

  public void renderFlameImage() {
    refreshFlameImage(false);
  }

  public void previewFlameImage() {
    refreshFlameImage(true);
  }

  public void moviePropertyChanged() {
    if (noRefresh) {
      return;
    }
    updateMovieFields();
    previewFlameImage();
    clearCurrentPreview();
    updateThumbnails();
  }

}
