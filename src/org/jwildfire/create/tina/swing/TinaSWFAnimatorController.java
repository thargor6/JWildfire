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
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.animate.AnimationService.GlobalScript;
import org.jwildfire.create.tina.animate.AnimationService.XFormScript;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.animate.FlameMoviePart;
import org.jwildfire.create.tina.animate.OutputFormat;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThread;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThreadController;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.io.JWFMovieReader;
import org.jwildfire.create.tina.io.JWFMovieWriter;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.SWFFileChooser;

public class TinaSWFAnimatorController implements SWFAnimationRenderThreadController, FlameHolder {
  public static final int PAGE_INDEX = 2;
  private SWFAnimationRenderThread renderThread = null;
  private FlameMovie currMovie = new FlameMovie();
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
  private FlamePanel flamePanel;
  private final List<JPanel> flamePartPanelList = new ArrayList<JPanel>();
  private final List<JRadioButton> flamePartRadioButtonList = new ArrayList<JRadioButton>();

  private boolean noRefresh;

  public TinaSWFAnimatorController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs, JComboBox pSWFAnimatorGlobalScriptCmb,
      JComboBox pSWFAnimatorXFormScriptCmb, JTextField pSWFAnimatorFramesREd, JTextField pSWFAnimatorFramesPerSecondREd,
      JButton pSWFAnimatorGenerateButton, JComboBox pSWFAnimatorResolutionProfileCmb,
      JComboBox pSWFAnimatorQualityProfileCmb, JButton pSWFAnimatorLoadFlameFromMainButton,
      JButton pSWFAnimatorLoadFlameFromClipboardButton, JButton pSWFAnimatorLoadFlameButton,
      JToggleButton pSWFAnimatorHalveSizeButton, JProgressBar pSWFAnimatorProgressBar, JButton pSWFAnimatorCancelButton,
      JButton pSWFAnimatorLoadSoundButton, JButton pSWFAnimatorClearSoundButton, ProgressUpdater pRenderProgressUpdater,
      JPanel pSWFAnimatorPreviewRootPanel, JLabel pSWFAnimatorSoundCaptionLbl, JSlider pSWFAnimatorFrameSlider,
      JTextField pSWFAnimatorFrameREd, JPanel pSWFAnimatorFlamesPanel, ButtonGroup pSWFAnimatorFlamesButtonGroup,
      JComboBox pSWFAnimatorOutputCmb, JButton pSWFAnimatorMoveUpButton, JButton pSWFAnimatorMoveDownButton,
      JButton pSWFAnimatorRemoveFlameButton, JButton pSWFAnimatorRemoveAllFlamesButton, JButton pSWFAnimatorMovieFromClipboardButton,
      JButton pSWFAnimatorMovieFromDiskButton, JButton pSWFAnimatorMovieToClipboardButton, JButton pSWFAnimatorMovieToDiskButton,
      JButton pSWFAnimatorFrameToEditorBtn, JButton pSWFAnimatorPlayButton) {
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

      swfAnimatorOutputCmb.addItem(OutputFormat.PNG);
      swfAnimatorOutputCmb.addItem(OutputFormat.SWF);
      swfAnimatorOutputCmb.addItem(OutputFormat.SWF_AND_PNG);
      swfAnimatorOutputCmb.setSelectedItem(OutputFormat.SWF_AND_PNG);

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
    swfAnimatorGenerateButton.setEnabled(!rendering && currMovie.getFrameCount() > 0);
    swfAnimatorPlayButton.setEnabled(swfAnimatorGenerateButton.isEnabled());
    swfAnimatorResolutionProfileCmb.setEnabled(!rendering);
    swfAnimatorQualityProfileCmb.setEnabled(!rendering);
    swfAnimatorLoadFlameFromMainButton.setEnabled(!rendering);
    swfAnimatorLoadFlameFromClipboardButton.setEnabled(!rendering);
    swfAnimatorLoadFlameButton.setEnabled(!rendering);
    swfAnimatorHalveSizeButton.setEnabled(!rendering);
    swfAnimatorCancelButton.setEnabled(rendering && !renderThread.isCancelSignalled());
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
      swfAnimatorFramesREd.setText(String.valueOf(frameCount));
      if (value > frameCount) {
        swfAnimatorFrameSlider.setValue(frameCount);
        swfAnimatorFrameREd.setText(String.valueOf(frameCount));
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
    final int FIELD_WIDTH = 50;
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
        RenderInfo info = new RenderInfo(imageWidth, imageHeight);
        Flame flame = pPart.getFlame().makeCopy();
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
      final JTextField framesField = new JTextField();
      framesField.setText(String.valueOf(pPart.getFrameCount()));
      framesField.setBounds(xOff + BORDER_SIZE + LABEL_WIDTH, yOff, FIELD_WIDTH, FIELD_HEIGHT);
      framesField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
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
      final JTextField framesMorphField = new JTextField();
      framesMorphField.setText(String.valueOf(pPart.getFrameMorphCount()));
      framesMorphField.setBounds(xOff + BORDER_SIZE + LABEL_WIDTH, yOff, FIELD_WIDTH, FIELD_HEIGHT);
      framesMorphField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
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
    parentCtrl.importFlame(pPart.getFlame());
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

  public void cancelButton_clicked() {
    if (renderThread != null) {
      renderThread.setCancelSignalled(true);
      enableControls();
    }
  }

  private void updateMovieFields() {
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

    OutputFormat outputFormat = (OutputFormat) swfAnimatorOutputCmb.getSelectedItem();
    if (outputFormat == null) {
      outputFormat = OutputFormat.SWF;
    }
    currMovie.setGlobalScript(globalScript);
    currMovie.setxFormScript(xFormScript);
    currMovie.setSpatialOversampling(qualityProfile.getSpatialOversample());
    currMovie.setColorOversampling(qualityProfile.getColorOversample());
    currMovie.setQuality(qualityProfile.getQuality());
    currMovie.setOutputFormat(outputFormat);
    currMovie.setFrameWidth(frameWidth);
    currMovie.setFrameHeight(frameHeight);
    currMovie.setFramesPerSecond(framesPerSecond);
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
      res = new QualityProfile(false, "default", 1, 1, 200, true, false);
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
      int width = bounds.width;
      int height = bounds.height;
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
      else {
        imgPanel.setImage(new SimpleImage(width, height));
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
    Flame flame = currMovie.getFlame(frame);
    if (flame != null) {
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
        return AnimationService.createFlame(frame, frameCount, flame, globalScript, xFormScript, prefs);
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

  private void framesFieldChanged(JTextField pFramesField, FlameMoviePart pPart) {
    try {
      int frameCount = Integer.parseInt(pFramesField.getText());
      if (frameCount < 0) {
        frameCount = 0;
        pFramesField.setText(String.valueOf(frameCount));
      }
      pPart.setFrameCount(frameCount);
      refreshFrameCount();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void framesMorphFieldChanged(JTextField pMorphFramesField, FlameMoviePart pPart) {
    try {
      int framesMorphCount = Integer.parseInt(pMorphFramesField.getText());
      if (framesMorphCount < 0) {
        framesMorphCount = 0;
        pMorphFramesField.setText(String.valueOf(framesMorphCount));
      }
      else if (framesMorphCount > pPart.getFrameCount()) {
        framesMorphCount = pPart.getFrameCount();
        pMorphFramesField.setText(String.valueOf(framesMorphCount));
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
          FlameMovie movie = new JWFMovieReader().readMovieFromXML(xml);
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
      swfAnimatorFramesPerSecondREd.setText(Tools.doubleToString(currMovie.getFramesPerSecond()));
      swfAnimatorGlobalScriptCmb.setSelectedItem(currMovie.getGlobalScript());
      swfAnimatorXFormScriptCmb.setSelectedItem(currMovie.getxFormScript());
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
        int qualityIndex = QualityProfile.calculateQualityIndex(currMovie.getSpatialOversampling(), currMovie.getColorOversampling(), currMovie.getQuality());
        for (int i = 0; i < swfAnimatorQualityProfileCmb.getItemCount(); i++) {
          QualityProfile profile = (QualityProfile) swfAnimatorQualityProfileCmb.getItemAt(i);
          if (profile.getColorOversample() == currMovie.getColorOversampling() && profile.getSpatialOversample() == currMovie.getSpatialOversampling() && profile.getQuality() == currMovie.getQuality()) {
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
      swfAnimatorFramesREd.setText(String.valueOf(currMovie.getFrameCount()));
      swfAnimatorFrameREd.setText(String.valueOf(1));

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

  public void movieFromDiskButton_clicked() {
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
        FlameMovie movie = new JWFMovieReader().readMovie(file.getAbsolutePath());
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

  public void movieToDiskButton_clicked() {
    updateMovieFields();
    try {
      JFileChooser chooser = new JWFMovieFileChooser(prefs);
      if (prefs.getOutputFlamePath() != null) {
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
      parentCtrl.importFlame(flame);
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

  public void playButton_clicked() {
    try {
      updateMovieFields();
      int oldFrame = swfAnimatorFrameSlider.getValue();
      try {
        for (int i = 1; i < currMovie.getFrameCount(); i++) {
          swfAnimatorFrameSlider.setValue(i);
          getFlamePanel().getParent().paint(getFlamePanel().getParent().getGraphics());
          swfAnimatorFrameSlider.paint(swfAnimatorFrameSlider.getGraphics());
        }
      }
      finally {
        swfAnimatorFrameSlider.setValue(oldFrame);
        refreshFlameImage();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

}
