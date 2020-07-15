/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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
package org.jwildfire.create.iflames.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.dance.FlamePropertiesTreeService;
import org.jwildfire.create.tina.dance.model.FlamePropertyPath;
import org.jwildfire.create.tina.edit.UndoManager;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.mutagen.RandomGradientMutation;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.FlameFileChooser;
import org.jwildfire.create.tina.swing.FlameHolder;
import org.jwildfire.create.tina.swing.FlameMessageHelper;
import org.jwildfire.create.tina.swing.FlamePanelProvider;
import org.jwildfire.create.tina.swing.FlamePreviewHelper;
import org.jwildfire.create.tina.swing.FlameThumbnail;
import org.jwildfire.create.tina.swing.ImageThumbnail;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.RenderProgressBarHolder;
import org.jwildfire.create.tina.swing.RenderProgressUpdater;
import org.jwildfire.create.tina.swing.ThumbnailCacheKey;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.iflames.CreationStatistics;
import org.jwildfire.create.tina.variation.iflames.FlameParams;
import org.jwildfire.create.tina.variation.iflames.IFlamesFunc;
import org.jwildfire.create.tina.variation.iflames.ImageParams;
import org.jwildfire.create.tina.variation.iflames.ShapeDistribution;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.MainController;

public class IFlamesController implements FlameHolder, FlamePanelProvider, RenderProgressBarHolder {
  private final Prefs prefs;
  private final MainController mainController;
  private final TinaController tinaController;
  private final JFrame iflamesFrame;
  private final ErrorHandler errorHandler;
  private final JPanel centerPanel;
  private final FlamePreviewHelper flamePreviewHelper;
  private final JButton undoButton;
  private final JButton redoButton;
  private final JButton renderButton;
  private final JPanel imageLibraryPanel;
  private final JPanel flameLibraryPanel;
  private final JButton loadIFlameButton;
  private final JButton loadIFlameFromClipboardButton;
  private final JButton saveIFlameToClipboardButton;
  private final JButton saveIFlameButton;
  private final JButton refreshIFlameButton;
  private final JProgressBar mainProgressBar;
  private final JToggleButton autoRefreshButton;
  private final JComboBox baseFlameCmb;
  private final JPanel baseFlamePreviewRootPnl;
  private final JComboBox resolutionProfileCmb;
  private final JToggleButton edgesNorthButton;
  private final JToggleButton edgesWestButton;
  private final JToggleButton edgesEastButton;
  private final JToggleButton edgesSouthButton;
  private final JToggleButton erodeButton;
  private final JToggleButton displayPreprocessedImageButton;
  private final JWFNumberField erodeSizeField;
  private final JWFNumberField maxImageWidthField;
  private final JWFNumberField structureThresholdField;
  private final JWFNumberField structureDensityField;
  private final JWFNumberField globalScaleXField;
  private final JWFNumberField globalScaleYField;
  private final JWFNumberField globalScaleZField;
  private final JWFNumberField globalOffsetXField;
  private final JWFNumberField globalOffsetYField;
  private final JWFNumberField globalOffsetZField;
  @SuppressWarnings("rawtypes")
  private final JComboBox shapeDistributionCmb;
  private final JWFNumberField iflameBrightnessField;
  private final JWFNumberField imageBrightnessField;
  private final JWFNumberField iflameDensityField;
  private final JWFNumberField baseFlameSizeField;
  private final JWFNumberField baseFlameSizeVariationField;
  private final JWFNumberField baseFlameRotateAlphaField;
  private final JWFNumberField baseFlameRotateAlphaVariationField;
  private final JWFNumberField baseFlameRotateBetaField;
  private final JWFNumberField baseFlameRotateBetaVariationField;
  private final JWFNumberField baseFlameRotateGammaField;
  private final JWFNumberField baseFlameRotateGammaVariationField;
  private final JWFNumberField baseFlameCentreXField;
  private final JWFNumberField baseFlameCentreYField;
  private final JWFNumberField baseFlameCentreZField;
  private final JButton baseFlameFromClipboardButton;
  private final JButton baseFlameToClipboardButton;
  private final JButton baseFlameClearButton;
  private final JButton baseFlameClearAllButton;
  private final JToggleButton previewButton;
  private final JButton renderFlameButton;
  private final JTextPane introductionTextPane;
  private final JWFNumberField baseFlameMinValueField;
  private final JLabel baseFlameMinValueLabel;
  private final JWFNumberField baseFlameMaxValueField;
  private final JLabel baseFlameMaxValueLabel;
  private final JTextArea statisticsTextArea;
  private final JWFNumberField baseFlameWeightField;
  private final JWFNumberField baseFlameGridXOffsetField;
  private final JWFNumberField baseFlameGridYOffsetField;
  private final JWFNumberField baseFlameGridXSizeField;
  private final JWFNumberField baseFlameGridYSizeField;
  private final JComboBox selectedMutationCmb;
  private final JWFNumberField paramMinValueField;
  private final JWFNumberField paramMaxValueField;
  private final JTree paramPropertyPathTree;
  private final JButton iflameToEditorButton;
  private final JWFNumberField motionTimeField;
  private final JWFNumberField motionLifeTimeField;
  private final JWFNumberField motionLifeTimeVariationField;
  private final JWFNumberField motionForceXField;
  private final JWFNumberField motionForceYField;
  private final JWFNumberField motionForceZField;
  private final JWFNumberField speedXField;
  private final JWFNumberField speedYField;
  private final JWFNumberField speedZField;
  private final JWFNumberField speedXVarField;
  private final JWFNumberField speedYVarField;
  private final JWFNumberField speedZVarField;
  private final JWFNumberField speedAlphaField;
  private final JWFNumberField speedBetaField;
  private final JWFNumberField speedGammaField;
  private final JWFNumberField speedAlphaVarField;
  private final JWFNumberField speedBetaVarField;
  private final JWFNumberField speedGammaVarField;
  private final JWFNumberField radialAccelField;
  private final JWFNumberField radialAccelVarField;
  private final JWFNumberField tangentialAccelField;
  private final JWFNumberField tangentialAccelVarField;
  private final JWFNumberField forceCentreXField;
  private final JWFNumberField forceCentreYField;
  private final JWFNumberField forceCentreZField;
  private final JButton baseFlameClearOthersButton;
  private final JButton copyDynamicsParamsToOthersButton;
  private final JWFNumberField baseFlameBrightnessMinField;
  private final JWFNumberField baseFlameBrightnessMaxField;
  private final JWFNumberField baseFlameBrightnessChangeField;
  private final JWFNumberField imageRedChangeField;
  private final JWFNumberField imageGreenChangeField;
  private final JWFNumberField imageBlueChangeField;
  private final JWFNumberField imageHueChangeField;
  private final JWFNumberField imageSaturationChangeField;
  private final JWFNumberField imageLightnessChangeField;
  private final JCheckBox baseFlameInstancingCBx;

  private Flame _currFlame;
  private FlamePanel flamePanel;
  private FlamePanel baseFlamePreviewPanel;
  private FlamePanel prevFlamePanel;
  private final UndoManager<Flame> undoManager = new UndoManager<Flame>();
  private final ProgressUpdater mainProgressUpdater;
  private final FlameMessageHelper messageHelper;
  private boolean undoDebug = true;
  private boolean noRefresh;
  private final FlamePropertiesTreeService flamePropertiesTreeService;

  @SuppressWarnings("unchecked")
  public IFlamesController(MainController pMainController, TinaController pTinaController,
      ErrorHandler pErrorHandler, JFrame pIflamesFrame, JPanel pCenterPanel,
      JButton pUndoButton, JButton pRedoButton, JButton pRenderButton, JPanel pImageLibraryPanel,
      JPanel pFlameLibraryPanel, JButton pLoadIFlameButton, JButton pLoadIFlameFromClipboardButton,
      JButton pSaveIFlameToClipboardButton, JButton pSaveIFlameButton, JButton pRefreshIFlameButton,
      JProgressBar pMainProgressBar, JToggleButton pAutoRefreshButton, JComboBox pBaseFlameCmb,
      JPanel pBaseFlamePreviewRootPnl, JComboBox pResolutionProfileCmb, JToggleButton pEdgesNorthButton,
      JToggleButton pEdgesWestButton, JToggleButton pEdgesEastButton, JToggleButton pEdgesSouthButton,
      JToggleButton pErodeButton, JToggleButton pDisplayPreprocessedImageButton, JWFNumberField pErodeSizeField,
      JWFNumberField pMaxImageWidthField, JWFNumberField pStructureThresholdField, JWFNumberField pStructureDensityField,
      JWFNumberField pGlobalScaleXField, JWFNumberField pGlobalScaleYField, JWFNumberField pGlobalScaleZField,
      JWFNumberField pGlobalOffsetXField, JWFNumberField pGlobalOffsetYField, JWFNumberField pGlobalOffsetZField,
      JComboBox pShapeDistributionCmb, JWFNumberField pIflameBrightnessField, JWFNumberField pImageBrightnessField,
      JWFNumberField pIflameDensityField, JWFNumberField pBaseFlameSizeField, JWFNumberField pBaseFlameSizeVariationField,
      JWFNumberField pBaseFlameRotateAlphaField, JWFNumberField pBaseFlameRotateAlphaVariationField,
      JWFNumberField pBaseFlameRotateBetaField, JWFNumberField pBaseFlameRotateBetaVariationField,
      JWFNumberField pBaseFlameRotateGammaField, JWFNumberField pBaseFlameRotateGammaVariationField,
      JWFNumberField pBaseFlameCentreXField, JWFNumberField pBaseFlameCentreYField, JWFNumberField pBaseFlameCentreZField,
      JButton pBaseFlameFromClipboardButton, JButton pBaseFlameToClipboardButton, JButton pBaseFlameClearButton,
      JButton pBaseFlameClearAllButton, JToggleButton pPreviewButton, JButton pRenderFlameButton,
      JTextPane pIntroductionTextPane, JWFNumberField pBaseFlameMinValueField, JLabel pBaseFlameMinValueLabel,
      JWFNumberField pBaseFlameMaxValueField, JLabel pBaseFlameMaxValueLabel, JTextArea pStatisticsTextArea,
      JWFNumberField pBaseFlameWeightField, JWFNumberField pBaseFlameGridXOffsetField,
      JWFNumberField pBaseFlameGridYOffsetField, JWFNumberField pBaseFlameGridXSizeField,
      JWFNumberField pBaseFlameGridYSizeField, JComboBox pSelectedMutationCmb, JWFNumberField pParamMinValueField,
      JWFNumberField pParamMaxValueField, JTree pParamPropertyPathTree, JButton pIflameToEditorButton,
      JWFNumberField pMotionTimeField, JWFNumberField pMotionLifeTimeField, JWFNumberField pMotionLifeTimeVariationField,
      JWFNumberField pMotionForceXField, JWFNumberField pMotionForceYField, JWFNumberField pMotionForceZField,
      JWFNumberField pSpeedXField, JWFNumberField pSpeedYField, JWFNumberField pSpeedZField,
      JWFNumberField pSpeedXVarField, JWFNumberField pSpeedYVarField, JWFNumberField pSpeedZVarField,
      JWFNumberField pSpeedAlphaField, JWFNumberField pSpeedBetaField, JWFNumberField pSpeedGammaField,
      JWFNumberField pSpeedAlphaVarField, JWFNumberField pSpeedBetaVarField, JWFNumberField pSpeedGammaVarField,
      JWFNumberField pRadialAccelField, JWFNumberField pRadialAccelVarField, JWFNumberField pTangentialAccelField,
      JWFNumberField pTangentialAccelVarField, JWFNumberField pForceCentreXField, JWFNumberField pForceCentreYField,
      JWFNumberField pForceCentreZField, JButton pBaseFlameClearOthersButton, JButton pCopyDynamicsParamsToOthersButton,
      JWFNumberField pBaseFlameBrightnessMinField, JWFNumberField pBaseFlameBrightnessMaxField, JWFNumberField pBaseFlameBrightnessChangeField,
      JWFNumberField pImageRedChangeField, JWFNumberField pImageGreenChangeField, JWFNumberField pImageBlueChangeField,
      JWFNumberField pImageHueChangeField, JWFNumberField pImageSaturationChangeField, JWFNumberField pImageLightnessChangeField,
      JCheckBox pBaseFlameInstancingCBx) {
    noRefresh = true;
    prefs = Prefs.getPrefs();
    mainController = pMainController;
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    iflamesFrame = pIflamesFrame;
    centerPanel = pCenterPanel;
    undoButton = pUndoButton;
    redoButton = pRedoButton;
    renderButton = pRenderButton;
    imageLibraryPanel = pImageLibraryPanel;
    flameLibraryPanel = pFlameLibraryPanel;
    loadIFlameButton = pLoadIFlameButton;
    loadIFlameFromClipboardButton = pLoadIFlameFromClipboardButton;
    saveIFlameToClipboardButton = pSaveIFlameToClipboardButton;
    saveIFlameButton = pSaveIFlameButton;
    refreshIFlameButton = pRefreshIFlameButton;
    mainProgressBar = pMainProgressBar;
    autoRefreshButton = pAutoRefreshButton;
    baseFlameCmb = pBaseFlameCmb;
    baseFlamePreviewRootPnl = pBaseFlamePreviewRootPnl;
    resolutionProfileCmb = pResolutionProfileCmb;
    edgesNorthButton = pEdgesNorthButton;
    edgesWestButton = pEdgesWestButton;
    edgesEastButton = pEdgesEastButton;
    edgesSouthButton = pEdgesSouthButton;
    erodeButton = pErodeButton;
    displayPreprocessedImageButton = pDisplayPreprocessedImageButton;
    erodeSizeField = pErodeSizeField;
    maxImageWidthField = pMaxImageWidthField;
    iflameBrightnessField = pIflameBrightnessField;
    imageBrightnessField = pImageBrightnessField;
    iflameDensityField = pIflameDensityField;
    baseFlameSizeField = pBaseFlameSizeField;
    baseFlameSizeVariationField = pBaseFlameSizeVariationField;
    baseFlameRotateAlphaField = pBaseFlameRotateAlphaField;
    baseFlameRotateAlphaVariationField = pBaseFlameRotateAlphaVariationField;
    baseFlameRotateBetaField = pBaseFlameRotateBetaField;
    baseFlameRotateBetaVariationField = pBaseFlameRotateBetaVariationField;
    baseFlameRotateGammaField = pBaseFlameRotateGammaField;
    baseFlameRotateGammaVariationField = pBaseFlameRotateGammaVariationField;
    baseFlameCentreXField = pBaseFlameCentreXField;
    baseFlameCentreYField = pBaseFlameCentreYField;
    baseFlameCentreZField = pBaseFlameCentreZField;
    structureThresholdField = pStructureThresholdField;
    structureDensityField = pStructureDensityField;
    globalScaleXField = pGlobalScaleXField;
    globalScaleYField = pGlobalScaleYField;
    globalScaleZField = pGlobalScaleZField;
    globalOffsetXField = pGlobalOffsetXField;
    globalOffsetYField = pGlobalOffsetYField;
    globalOffsetZField = pGlobalOffsetZField;
    shapeDistributionCmb = pShapeDistributionCmb;
    baseFlameFromClipboardButton = pBaseFlameFromClipboardButton;
    baseFlameToClipboardButton = pBaseFlameToClipboardButton;
    baseFlameClearButton = pBaseFlameClearButton;
    baseFlameClearAllButton = pBaseFlameClearAllButton;
    previewButton = pPreviewButton;
    renderFlameButton = pRenderFlameButton;
    introductionTextPane = pIntroductionTextPane;
    baseFlameMinValueField = pBaseFlameMinValueField;
    baseFlameMinValueLabel = pBaseFlameMinValueLabel;
    baseFlameMaxValueField = pBaseFlameMaxValueField;
    baseFlameMaxValueLabel = pBaseFlameMaxValueLabel;
    statisticsTextArea = pStatisticsTextArea;
    baseFlameWeightField = pBaseFlameWeightField;
    baseFlameGridXOffsetField = pBaseFlameGridXOffsetField;
    baseFlameGridYOffsetField = pBaseFlameGridYOffsetField;
    baseFlameGridXSizeField = pBaseFlameGridXSizeField;
    baseFlameGridYSizeField = pBaseFlameGridYSizeField;
    selectedMutationCmb = pSelectedMutationCmb;
    paramMinValueField = pParamMinValueField;
    paramMaxValueField = pParamMaxValueField;
    paramPropertyPathTree = pParamPropertyPathTree;
    iflameToEditorButton = pIflameToEditorButton;
    motionTimeField = pMotionTimeField;
    motionLifeTimeField = pMotionLifeTimeField;
    motionLifeTimeVariationField = pMotionLifeTimeVariationField;
    motionForceXField = pMotionForceXField;
    motionForceYField = pMotionForceYField;
    motionForceZField = pMotionForceZField;
    speedXField = pSpeedXField;
    speedYField = pSpeedYField;
    speedZField = pSpeedZField;
    speedXVarField = pSpeedXVarField;
    speedYVarField = pSpeedYVarField;
    speedZVarField = pSpeedZVarField;
    speedAlphaField = pSpeedAlphaField;
    speedBetaField = pSpeedBetaField;
    speedGammaField = pSpeedGammaField;
    speedAlphaVarField = pSpeedAlphaVarField;
    speedBetaVarField = pSpeedBetaVarField;
    speedGammaVarField = pSpeedGammaVarField;
    radialAccelField = pRadialAccelField;
    radialAccelVarField = pRadialAccelVarField;
    tangentialAccelField = pTangentialAccelField;
    tangentialAccelVarField = pTangentialAccelVarField;
    forceCentreXField = pForceCentreXField;
    forceCentreYField = pForceCentreYField;
    forceCentreZField = pForceCentreZField;
    baseFlameClearOthersButton = pBaseFlameClearOthersButton;
    copyDynamicsParamsToOthersButton = pCopyDynamicsParamsToOthersButton;
    baseFlameBrightnessMinField = pBaseFlameBrightnessMinField;
    baseFlameBrightnessMaxField = pBaseFlameBrightnessMaxField;
    baseFlameBrightnessChangeField = pBaseFlameBrightnessChangeField;
    imageRedChangeField = pImageRedChangeField;
    imageGreenChangeField = pImageGreenChangeField;
    imageBlueChangeField = pImageBlueChangeField;
    imageHueChangeField = pImageHueChangeField;
    imageSaturationChangeField = pImageSaturationChangeField;
    imageLightnessChangeField = pImageLightnessChangeField;
    baseFlameInstancingCBx = pBaseFlameInstancingCBx;

    messageHelper = new JFrameFlameMessageHelper(iflamesFrame);
    mainProgressUpdater = new RenderProgressUpdater(this);
    flamePropertiesTreeService = new FlamePropertiesTreeService();

    flamePreviewHelper = new FlamePreviewHelper(errorHandler, centerPanel, null,
        null, null, mainProgressUpdater, this, null, null, this, messageHelper, null);
    enableControls();

    refreshResolutionProfileCmb(resolutionProfileCmb, null);
    {
      baseFlameCmb.removeAllItems();
      for (int i = 0; i < IFlamesFunc.MAX_FLAME_COUNT; i++) {
        baseFlameCmb.addItem("Flame " + (i + 1));
      }
      baseFlameCmb.setSelectedIndex(0);
    }
    {
      selectedMutationCmb.removeAllItems();
      for (int i = 0; i < FlameParams.MAX_MUTATION_COUNT; i++) {
        selectedMutationCmb.addItem("Mutation " + (i + 1));
      }
      selectedMutationCmb.setSelectedIndex(0);
    }

    {
      shapeDistributionCmb.removeAllItems();
      shapeDistributionCmb.addItem(ShapeDistribution.HUE);
      shapeDistributionCmb.addItem(ShapeDistribution.RANDOM);
      shapeDistributionCmb.addItem(ShapeDistribution.LUMINOSITY);
      shapeDistributionCmb.addItem(ShapeDistribution.BRIGHTNESS);
      shapeDistributionCmb.addItem(ShapeDistribution.GRID);
      shapeDistributionCmb.setSelectedItem(ShapeDistribution.HUE);
    }

    displayPreprocessedImageButton.setSelected(false);

    noRefresh = false;

    initIntroductionTextPane();
  }

  private void initIntroductionTextPane() {
    introductionTextPane.setContentType("text/html");
    try {
      Font f = new Font(Font.SANS_SERIF, 3, 10);
      introductionTextPane.setFont(f);

      InputStream is = this.getClass().getResourceAsStream("iflames.html");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();

      introductionTextPane.setText(content.toString());
      introductionTextPane.setSelectionStart(0);
      introductionTextPane.setSelectionEnd(0);

    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private boolean initFlag = false;

  public void init() {
    if (!initFlag) {
      if (prefs.isIflamesLoadLibraryAtStartup()) {
        reloadLibraryButton_clicked();
      }
      initFlag = true;
    }
  }

  public void setFlame(Flame pFlame) {
    _currFlame = pFlame;
  }

  @Override
  public Flame getFlame() {
    return _currFlame;
  }

  private boolean firstFlamePanel = true;

  @Override
  public FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      Prefs prefs = Prefs.getPrefs();
      int width = centerPanel.getWidth();
      int height = centerPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(prefs, img, 0, 0, centerPanel.getWidth(), this, null, null);
      flamePanel.getConfig().setWithColoredTransforms(prefs.isTinaEditorControlsWithColor());
      flamePanel.setFlamePanelTriangleMode(prefs.getTinaEditorControlsStyle());
      flamePanel.getConfig().setProgressivePreview(prefs.isTinaEditorProgressivePreview());
      flamePanel.importOptions(prevFlamePanel);
      prevFlamePanel = null;
      ResolutionProfile resProfile = getResolutionProfile();
      flamePanel.setRenderWidth(resProfile.getWidth());
      flamePanel.setRenderHeight(resProfile.getHeight());
      flamePanel.setFocusable(true);
      if (firstFlamePanel) {
        centerPanel.remove(0);
        firstFlamePanel = false;
      }
      centerPanel.add(flamePanel, BorderLayout.CENTER);
      centerPanel.getParent().validate();
      centerPanel.repaint();
      flamePanel.requestFocusInWindow();
    }
    return flamePanel;
  }

  private void removeFlamePanel() {
    if (flamePanel != null) {
      centerPanel.remove(flamePanel);
      prevFlamePanel = flamePanel;
      flamePanel = null;
    }
  }

  @Override
  public FlamePanelConfig getFlamePanelConfig() {
    return getFlamePanel().getConfig();
  }

  public void newButton_clicked() {
    setFlame(createNewFlame());
    undoManager.initUndoStack(getFlame());
    refreshUI();
    enableControls();
  }

  private Flame createNewFlame() {
    Flame flame = new Flame();
    flame.setBGTransparency(prefs.isTinaDefaultBGTransparency());
    flame.setCamRoll(0);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0);
    flame.setCamPerspective(0);
    flame.setWidth(800);
    flame.setHeight(600);
    flame.setCamZoom(1);
    {
      Layer layer = flame.getFirstLayer();
      new RandomGradientMutation().execute(layer);

      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      IFlamesFunc iflames = (IFlamesFunc) VariationFuncList.getVariationFuncInstance("iflames_wf", true);
      if (imageLibrary.size() > 0) {
        String imgFilename = imageLibrary.get((int) (Math.random() * imageLibrary.size())).getFilename();
        iflames.getImageParams().setImageFilename(imgFilename);
      }
      if (flameLibrary.size() > 0) {
        for (int i = 0; i < IFlamesFunc.MAX_FLAME_COUNT; i++) {
          if (i > 1 && Math.random() < 0.33) {
            iflames.getFlameParams(i).setFlameXML(null);
          }
          else if (Math.random() < 0.50) {
            Flame libFlame = flameLibrary.get((int) (Math.random() * flameLibrary.size())).getFlame();
            String libFlameXML;
            try {
              libFlameXML = new FlameWriter().getFlameXML(libFlame);
            }
            catch (Exception e) {
              libFlameXML = null;
              e.printStackTrace();
            }
            iflames.getFlameParams(i).setFlameXML(libFlameXML);
          }
        }
      }
      if (Math.random() < 0.5) {
        iflames.getImageParams().setShape_distribution(ShapeDistribution.HUE);
      }
      else {
        iflames.getImageParams().setShape_distribution(ShapeDistribution.RANDOM);
      }
      for (int i = 0; i < IFlamesFunc.MAX_FLAME_COUNT; i++) {
        iflames.getFlameParams(i).setSize(2.0 + Math.random() * 4.0);
      }
      iflames.getMotionParams().setPreview(previewButton.isSelected() ? 1 : 0);

      xForm.addVariation(1.0, iflames);
    }
    return flame.makeCopy();
  }

  private void refreshPreview() {
    int renderId = prepareIFlame();
    try {
      if (displayPreprocessedImageButton.isSelected()) {
        flamePreviewHelper.renderFlameImage(true, true, 1, false);
        ImageParams imageParams = getIFlamesFunc().getImageParams();
        imageParams.init(new FlameTransformationContext(null, null, 0,0));
        SimpleImage img = (SimpleImage) RessourceManager.getRessource(imageParams.getCachedPreprocessedImageKey());
        if (img != null) {
          flamePreviewHelper.setImage(img);
          return;
        }
      }
      flamePreviewHelper.refreshFlameImage(true, false, 1, true, false);
    }
    finally {
      unprepareIFlame(renderId);
    }
  }

  private void unprepareIFlame(int pRenderId) {
    getIFlamesFunc().getImageParams().setRenderId(0);
    CreationStatistics statistics = (CreationStatistics) RessourceManager.getRessource(ImageParams.CACHE_KEY_PREFIX_STATISTICS + "#" + pRenderId);
    RessourceManager.removeRessource(ImageParams.CACHE_KEY_PREFIX_STATISTICS + "#" + pRenderId);
    RessourceManager.removeRessource(ImageParams.CACHE_KEY_PREFIX_PROGRESS_UPDATER + "#" + pRenderId);
    if (statistics != null && statistics.getActions().size() > 0) {
      statisticsTextArea.setText(statistics.toString());
    }
  }

  private int prepareIFlame() {
    int renderId = Long.valueOf(System.currentTimeMillis()).intValue();
    getIFlamesFunc().getImageParams().setRenderId(renderId);
    RessourceManager.putRessource(ImageParams.CACHE_KEY_PREFIX_STATISTICS + "#" + renderId, new CreationStatistics());
    RessourceManager.putRessource(ImageParams.CACHE_KEY_PREFIX_PROGRESS_UPDATER + "#" + renderId, mainProgressUpdater);
    return renderId;
  }

  public void enableControls() {
    enableUndoControls();
    boolean hasFlame = getFlame() != null;
    IFlamesFunc iflames = getIFlamesFunc();
    boolean hasIFlame = iflames != null;
    String baseFlameXML = hasIFlame ? iflames.getFlameParams(getCurrFlameIndex()).getFlameXML() : null;
    boolean hasBaseFlame = baseFlameXML != null && baseFlameXML.length() > 0;
    loadIFlameButton.setEnabled(true);
    loadIFlameFromClipboardButton.setEnabled(true);
    saveIFlameToClipboardButton.setEnabled(hasFlame);
    saveIFlameButton.setEnabled(hasFlame);
    edgesNorthButton.setEnabled(hasIFlame);
    edgesWestButton.setEnabled(hasIFlame);
    edgesEastButton.setEnabled(hasIFlame);
    edgesSouthButton.setEnabled(hasIFlame);
    erodeButton.setEnabled(hasIFlame);
    displayPreprocessedImageButton.setEnabled(hasIFlame);
    autoRefreshButton.setEnabled(hasIFlame);
    refreshIFlameButton.setEnabled(hasIFlame);
    erodeSizeField.setEnabled(erodeButton.isEnabled() && erodeButton.isSelected());
    maxImageWidthField.setEnabled(hasIFlame);
    baseFlameCmb.setEnabled(hasIFlame);
    structureThresholdField.setEnabled(hasIFlame);
    structureDensityField.setEnabled(hasIFlame);
    globalScaleXField.setEnabled(hasIFlame);
    globalScaleYField.setEnabled(hasIFlame);
    globalScaleZField.setEnabled(hasIFlame);
    globalOffsetXField.setEnabled(hasIFlame);
    globalOffsetYField.setEnabled(hasIFlame);
    globalOffsetZField.setEnabled(hasIFlame);
    shapeDistributionCmb.setEnabled(hasIFlame);
    iflameBrightnessField.setEnabled(hasIFlame);
    imageBrightnessField.setEnabled(hasIFlame);
    iflameDensityField.setEnabled(hasIFlame);
    baseFlameSizeField.setEnabled(hasIFlame);
    baseFlameSizeVariationField.setEnabled(hasIFlame);
    baseFlameRotateAlphaField.setEnabled(hasIFlame);
    baseFlameRotateAlphaVariationField.setEnabled(hasIFlame);
    baseFlameRotateBetaField.setEnabled(hasIFlame);
    baseFlameRotateBetaVariationField.setEnabled(hasIFlame);
    baseFlameRotateGammaField.setEnabled(hasIFlame);
    baseFlameRotateGammaVariationField.setEnabled(hasIFlame);
    baseFlameCentreXField.setEnabled(hasIFlame);
    baseFlameCentreYField.setEnabled(hasIFlame);
    baseFlameCentreZField.setEnabled(hasIFlame);
    baseFlameFromClipboardButton.setEnabled(hasIFlame);
    baseFlameToClipboardButton.setEnabled(hasBaseFlame);
    baseFlameClearButton.setEnabled(hasBaseFlame);
    baseFlameClearAllButton.setEnabled(hasIFlame);
    previewButton.setEnabled(true);
    renderFlameButton.setEnabled(hasFlame);
    selectedMutationCmb.setEnabled(hasBaseFlame);
    paramMinValueField.setEnabled(hasBaseFlame);
    paramMaxValueField.setEnabled(hasBaseFlame);
    paramPropertyPathTree.setEnabled(hasBaseFlame);
    iflameToEditorButton.setEnabled(hasFlame);
    motionTimeField.setEnabled(hasIFlame);
    motionLifeTimeField.setEnabled(hasIFlame);
    motionLifeTimeVariationField.setEnabled(hasIFlame);
    motionForceXField.setEnabled(hasIFlame);
    motionForceYField.setEnabled(hasIFlame);
    motionForceZField.setEnabled(hasIFlame);
    speedXField.setEnabled(hasBaseFlame);
    speedYField.setEnabled(hasBaseFlame);
    speedZField.setEnabled(hasBaseFlame);
    speedXVarField.setEnabled(hasBaseFlame);
    speedYVarField.setEnabled(hasBaseFlame);
    speedZVarField.setEnabled(hasBaseFlame);
    speedAlphaField.setEnabled(hasBaseFlame);
    speedBetaField.setEnabled(hasBaseFlame);
    speedGammaField.setEnabled(hasBaseFlame);
    speedAlphaVarField.setEnabled(hasBaseFlame);
    speedBetaVarField.setEnabled(hasBaseFlame);
    speedGammaVarField.setEnabled(hasBaseFlame);
    radialAccelField.setEnabled(hasBaseFlame);
    radialAccelVarField.setEnabled(hasBaseFlame);
    tangentialAccelField.setEnabled(hasBaseFlame);
    tangentialAccelVarField.setEnabled(hasBaseFlame);
    forceCentreXField.setEnabled(hasIFlame);
    forceCentreYField.setEnabled(hasIFlame);
    forceCentreZField.setEnabled(hasIFlame);
    baseFlameClearOthersButton.setEnabled(hasIFlame);
    copyDynamicsParamsToOthersButton.setEnabled(hasBaseFlame);
    baseFlameBrightnessMinField.setEnabled(hasBaseFlame);
    baseFlameBrightnessMaxField.setEnabled(hasBaseFlame);
    baseFlameBrightnessChangeField.setEnabled(hasBaseFlame);
    imageRedChangeField.setEnabled(hasIFlame);
    imageGreenChangeField.setEnabled(hasIFlame);
    imageBlueChangeField.setEnabled(hasIFlame);
    imageHueChangeField.setEnabled(hasIFlame);
    imageSaturationChangeField.setEnabled(hasIFlame);
    imageLightnessChangeField.setEnabled(hasIFlame);
    baseFlameInstancingCBx.setEnabled(hasIFlame);

    boolean minMaxFields = hasIFlame && (ShapeDistribution.HUE.equals(iflames.getImageParams().getShape_distribution()) || ShapeDistribution.BRIGHTNESS.equals(iflames.getImageParams().getShape_distribution()) ||
        ShapeDistribution.LUMINOSITY.equals(iflames.getImageParams().getShape_distribution()));
    baseFlameMinValueField.setEnabled(minMaxFields);
    baseFlameMaxValueField.setEnabled(minMaxFields);

    boolean weightFields = hasIFlame && ShapeDistribution.RANDOM.equals(iflames.getImageParams().getShape_distribution());
    baseFlameWeightField.setEnabled(weightFields);

    boolean gridFields = hasIFlame && ShapeDistribution.GRID.equals(iflames.getImageParams().getShape_distribution());
    baseFlameGridXOffsetField.setEnabled(gridFields);
    baseFlameGridYOffsetField.setEnabled(gridFields);
    baseFlameGridXSizeField.setEnabled(gridFields);
    baseFlameGridYSizeField.setEnabled(gridFields);
  }

  public void saveUndoPoint() {
    if (getFlame() != null) {
      undoManager.saveUndoPoint(getFlame());
      enableUndoControls();
      undoButton.invalidate();
      undoButton.validate();
      redoButton.invalidate();
      redoButton.validate();
      undoButton.getParent().repaint();
    }
  }

  private void enableUndoControls() {
    final String UNDO_LABEL = "Undo";
    final String REDO_LABEL = "Redo";
    if (getFlame() != null) {
      int stackSize = undoManager.getUndoStackSize(getFlame());
      if (stackSize > 0) {
        int pos = undoManager.getUndoStackPosition(getFlame());
        undoButton.setEnabled(pos > 0 && pos < stackSize);
        if (undoDebug) {
          undoButton.setText("U " + pos);
        }
        else {
          undoButton.setToolTipText(UNDO_LABEL + " " + pos + "/" + stackSize);
        }
        redoButton.setEnabled(pos >= 0 && pos < stackSize - 1);
        if (undoDebug) {
          redoButton.setText("R " + stackSize);
        }
        else {
          redoButton.setToolTipText(REDO_LABEL + " " + pos + "/" + stackSize);
        }
      }
      else {
        undoButton.setEnabled(false);
        if (undoDebug) {
          undoButton.setText(UNDO_LABEL);
        }
        else {
          undoButton.setToolTipText(UNDO_LABEL);
        }
        redoButton.setEnabled(false);
        if (undoDebug) {
          redoButton.setText(REDO_LABEL);
        }
        else {
          redoButton.setToolTipText(REDO_LABEL);
        }
      }
    }
    else {
      undoButton.setEnabled(false);
      redoButton.setEnabled(false);
    }
  }

  public void undoAction() {
    if (getFlame() != null) {
      undoManager.setEnabled(false);
      try {
        undoManager.saveUndoPoint(getFlame());
        undoManager.undo(getFlame());
        enableUndoControls();
        refreshIFlame();
        enableControls();
        refreshUI();
      }
      finally {
        undoManager.setEnabled(true);
      }
    }
  }

  private void refreshUI() {
    refreshBaseFlamePreview();
    refreshImageFields();
    refreshMotionFields();
    refreshBaseFlameFields();
    refreshPreview();
  }

  public void redoAction() {
    if (getFlame() != null) {
      undoManager.setEnabled(false);
      try {
        undoManager.redo(getFlame());
        enableUndoControls();
        refreshIFlame();
        enableControls();
        refreshUI();
      }
      finally {
        undoManager.setEnabled(true);
      }
    }
  }

  public void renderFlameButton_clicked() {
    flamePreviewHelper.refreshFlameImage(false, false, 1, true, false);
  }

  public void loadImagesButton_clicked() {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    chooser.setMultiSelectionEnabled(true);
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      Throwable lastError = null;
      for (File file : chooser.getSelectedFiles()) {
        try {
          String filename = file.getAbsolutePath();
          WFImage img = RessourceManager.getImage(filename);
          if (img.getImageWidth() < 16 || img.getImageHeight() < 16 || !(img instanceof SimpleImage)) {
            throw new Exception("Invalid image");
          }
          prefs.setLastInputImageFile(file);
          addImageToImageLibrary(filename, new ThumbnailCacheKey(filename));
        }
        catch (Throwable ex) {
          lastError = ex;
        }
      }
      refreshImageLibrary();
      if (lastError != null) {
        errorHandler.handleError(lastError);
      }
    }
  }

  public void loadFlamesButton_clicked() {
    JFileChooser chooser = new FlameFileChooser(prefs);
    if (prefs.getInputFlamePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    chooser.setMultiSelectionEnabled(true);
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      Throwable lastError = null;
      for (File file : chooser.getSelectedFiles()) {
        try {
          List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
          prefs.setLastInputFlameFile(file);
          for (int i = flames.size() - 1; i >= 0; i--) {
            addFlameToFlameLibrary(flames.get(i), new ThumbnailCacheKey(file.getAbsolutePath(), String.valueOf(i)));
          }
        }
        catch (Throwable ex) {
          lastError = ex;
        }
      }
      refreshFlameLibrary();
      if (lastError != null) {
        errorHandler.handleError(lastError);
      }
    }
  }

  private final List<ImageThumbnail> imageLibrary = new ArrayList<ImageThumbnail>();
  private JScrollPane imageLibraryScrollPane;

  private void refreshImageLibrary() {
    if (imageLibraryScrollPane != null) {
      imageLibraryPanel.remove(imageLibraryScrollPane);
      imageLibraryScrollPane = null;
    }
    int panelWidth = ImageThumbnail.IMG_WIDTH + 2 * ImageThumbnail.BORDER_SIZE;
    int panelHeight = 0;
    for (int i = 0; i < imageLibrary.size(); i++) {
      panelHeight += ImageThumbnail.BORDER_SIZE + imageLibrary.get(i).getPreview().getImageHeight();
    }
    JPanel batchPanel = new JPanel();
    batchPanel.setLayout(null);
    batchPanel.setSize(panelWidth, panelHeight);
    batchPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    int yOff = ImageThumbnail.BORDER_SIZE;
    for (int i = 0; i < imageLibrary.size(); i++) {
      SimpleImage img = imageLibrary.get(i).getPreview();
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(ImageThumbnail.BORDER_SIZE, yOff);
      yOff += img.getImageHeight() + ImageThumbnail.BORDER_SIZE;
      imageLibrary.get(i).setImgPanel(imgPanel);
      final int idx = i;
      addRemoveImageFromLibraryButton(imgPanel, idx);
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
            importFromImageLibrary(idx);
          }
        }
      });
      batchPanel.add(imgPanel);
    }
    imageLibraryScrollPane = new JScrollPane(batchPanel);
    imageLibraryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imageLibraryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    imageLibraryPanel.add(imageLibraryScrollPane, BorderLayout.CENTER);
    imageLibraryScrollPane.validate();
    imageLibraryScrollPane.getParent().validate();
  }

  private void addRemoveImageFromLibraryButton(ImagePanel pImgPanel, final int pIdx) {
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
        removeImageFromLibrary(pIdx);
      }
    });
    pImgPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, BORDER, BORDER));
    pImgPanel.add(btn);
    pImgPanel.invalidate();
  }

  public void importFromImageLibrary(int pIdx) {
    if (pIdx >= 0 && pIdx < imageLibrary.size() && getIFlamesFunc() != null) {
      String filename = imageLibrary.get(pIdx).getFilename();
      saveUndoPoint();
      getIFlamesFunc().getImageParams().setImageFilename(filename);
      refreshIFlame();
    }
  }

  protected void removeImageFromLibrary(int pIdx) {
    imageLibrary.remove(pIdx);
    refreshImageLibrary();
  }

  private void addImageToImageLibrary(String pFilename, ThumbnailCacheKey pCacheKey) {
    imageLibrary.add(0, new ImageThumbnail(pFilename, null, pCacheKey));
  }

  private final List<FlameThumbnail> flameLibrary = new ArrayList<FlameThumbnail>();
  private JScrollPane flameLibraryScrollPane;

  private void refreshFlameLibrary() {
    if (flameLibraryScrollPane != null) {
      flameLibraryPanel.remove(flameLibraryScrollPane);
      flameLibraryScrollPane = null;
    }
    int panelWidth = FlameThumbnail.IMG_WIDTH + 2 * FlameThumbnail.BORDER_SIZE;
    int panelHeight = (FlameThumbnail.IMG_HEIGHT + FlameThumbnail.BORDER_SIZE) * flameLibrary.size();
    JPanel batchPanel = new JPanel();
    batchPanel.setLayout(null);
    batchPanel.setSize(panelWidth, panelHeight);
    batchPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    for (int i = 0; i < flameLibrary.size(); i++) {
      SimpleImage img = flameLibrary.get(i).getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
      // add it to the main panel
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(FlameThumbnail.BORDER_SIZE, i * FlameThumbnail.IMG_HEIGHT + (i + 1) * FlameThumbnail.BORDER_SIZE);
      flameLibrary.get(i).setImgPanel(imgPanel);
      final int idx = i;
      addRemoveFlameFromLibraryButton(imgPanel, idx);
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
            importFromFlameLibrary(idx);
          }
        }
      });
      batchPanel.add(imgPanel);
    }
    flameLibraryScrollPane = new JScrollPane(batchPanel);
    flameLibraryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    flameLibraryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    flameLibraryPanel.add(flameLibraryScrollPane, BorderLayout.CENTER);
    flameLibraryScrollPane.validate();
    flameLibraryScrollPane.getParent().validate();
  }

  private void addFlameToFlameLibrary(Flame pFlame, ThumbnailCacheKey pCacheKey) {
    flameLibrary.add(0, new FlameThumbnail(pFlame, null, pCacheKey));
  }

  private void addRemoveFlameFromLibraryButton(ImagePanel pImgPanel, final int pIdx) {
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
        removeFlameFromLibrary(pIdx);
      }
    });
    pImgPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, BORDER, BORDER));
    pImgPanel.add(btn);
    pImgPanel.invalidate();
  }

  protected void removeFlameFromLibrary(int pIdx) {
    flameLibrary.remove(pIdx);
    refreshFlameLibrary();
  }

  public void importFromFlameLibrary(int pIdx) {
    if (pIdx >= 0 && pIdx < flameLibrary.size() && getIFlamesFunc() != null) {
      Flame flame = flameLibrary.get(pIdx).getFlame();
      saveUndoPoint();
      try {
        String flameXML = new FlameWriter().getFlameXML(flame);
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameXML(flameXML);
      }
      catch (Exception e) {
        errorHandler.handleError(e);
      }
      enableControls();
      baseFlameCmb_changed();
      refreshIFlame();
    }
  }

  private void refreshIFlame() {
    if (autoRefreshButton.isSelected()) {
      refreshPreview();
    }
  }

  private IFlamesFunc getIFlamesFunc() {
    Flame flame = getFlame();
    if (flame != null) {
      for (Layer layer : flame.getLayers()) {
        for (XForm xform : layer.getXForms()) {
          for (int i = 0; i < xform.getVariationCount(); i++) {
            Variation var = xform.getVariation(i);
            if (var.getFunc() instanceof IFlamesFunc) {
              return (IFlamesFunc) var.getFunc();
            }
          }
        }
        for (XForm xform : layer.getFinalXForms()) {
          for (int i = 0; i < xform.getVariationCount(); i++) {
            Variation var = xform.getVariation(i);
            if (var.getFunc() instanceof IFlamesFunc) {
              return (IFlamesFunc) var.getFunc();
            }
          }
        }
      }
    }
    return null;
  }

  private int getCurrFlameIndex() {
    return baseFlameCmb.getSelectedIndex();
  }

  private int getCurrMutationIndex() {
    return selectedMutationCmb.getSelectedIndex();
  }

  @SuppressWarnings("unchecked")
  private void refreshResolutionProfileCmb(@SuppressWarnings("rawtypes") JComboBox pCmb, ResolutionProfile pSelectedProfile) {
    boolean oldNoRefresh = noRefresh;
    noRefresh = true;
    try {
      ResolutionProfile selected = pSelectedProfile;
      ResolutionProfile defaultProfile = null;
      pCmb.removeAllItems();
      for (ResolutionProfile profile : prefs.getResolutionProfiles()) {
        if (selected == null && profile.isDefaultProfile()) {
          selected = profile;
        }
        if (defaultProfile == null && profile.isDefaultProfile()) {
          defaultProfile = profile;
        }
        pCmb.addItem(profile);
      }
      if (selected != null) {
        pCmb.setSelectedItem(selected);
      }
      if (pCmb.getSelectedIndex() < 0 && defaultProfile != null) {
        pCmb.setSelectedItem(defaultProfile);
      }
      if (pCmb.getSelectedIndex() < 0 && prefs.getResolutionProfiles().size() > 0) {
        pCmb.setSelectedIndex(0);
      }
    }
    finally {
      noRefresh = oldNoRefresh;
    }
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) resolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  public void resolutionProfileCmb_changed() {
    if (noRefresh || getFlame() == null) {
      return;
    }
    noRefresh = true;
    try {
      ResolutionProfile profile = getResolutionProfile();
      getFlame().setResolutionProfile(profile);
      removeFlamePanel();
      refreshPreview();
      resolutionProfileCmb.requestFocus();
    }
    finally {
      noRefresh = false;
    }
  }

  public void baseFlameCmb_changed() {
    if (noRefresh || getFlame() == null) {
      return;
    }
    enableControls();
    refreshBaseFlamePreview();
    refreshBaseFlameFields();
  }

  private FlamePanel getBaseFlamePreviewPanel() {
    if (baseFlamePreviewPanel == null) {
      int width = baseFlamePreviewRootPnl.getWidth();
      int height = baseFlamePreviewRootPnl.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      baseFlamePreviewPanel = new FlamePanel(prefs, img, 0, 0, baseFlamePreviewRootPnl.getWidth(), this, null, null);
      ResolutionProfile resProfile = getResolutionProfile();
      baseFlamePreviewPanel.setRenderWidth(resProfile.getWidth());
      baseFlamePreviewPanel.setRenderHeight(resProfile.getHeight());
      baseFlamePreviewPanel.setFocusable(true);
      baseFlamePreviewRootPnl.add(baseFlamePreviewPanel, BorderLayout.CENTER);
      baseFlamePreviewRootPnl.getParent().validate();
      baseFlamePreviewRootPnl.repaint();
      baseFlamePreviewPanel.requestFocusInWindow();
    }
    return baseFlamePreviewPanel;
  }

  private Flame getBaseFlame() {
    IFlamesFunc iflame = getIFlamesFunc();
    String flameXML = iflame != null ? iflame.getFlameParams(getCurrFlameIndex()).getFlameXML() : null;
    return flameXML != null && flameXML.length() > 0 ? new FlameReader(prefs).readFlamesfromXML(flameXML).get(0) : null;
  }

  private void refreshBaseFlamePreview() {
    FlamePanel imgPanel = getBaseFlamePreviewPanel();
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      Flame flame = getBaseFlame();
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
          flame.setSampleDensity(1.0);
          flame.setSpatialFilterRadius(0.0);
          RenderedFlame res = renderer.renderFlame(info);
          imgPanel.setImage(res.getImage());
        }
        finally {
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSampleDensity(oldSampleDensity);
        }
      }
      else {
        imgPanel.setImage(new SimpleImage(width, height));
      }
    }
    else {
      imgPanel.setImage(new SimpleImage(width, height));
    }
    baseFlamePreviewRootPnl.repaint();
  }

  private void refreshImageFields() {
    IFlamesFunc iflame = getIFlamesFunc();
    if (iflame == null) {
      edgesNorthButton.setSelected(false);
      edgesWestButton.setSelected(false);
      edgesEastButton.setSelected(false);
      edgesSouthButton.setSelected(false);
      erodeButton.setSelected(false);
      erodeSizeField.setValue(0.0);
      maxImageWidthField.setValue(0.0);
      structureThresholdField.setValue(0.0);
      structureDensityField.setValue(0.0);
      globalScaleXField.setValue(0.0);
      globalScaleYField.setValue(0.0);
      globalScaleZField.setValue(0.0);
      globalOffsetXField.setValue(0.0);
      globalOffsetYField.setValue(0.0);
      globalOffsetZField.setValue(0.0);
      shapeDistributionCmb.setSelectedIndex(-1);
      iflameBrightnessField.setValue(0.0);
      imageBrightnessField.setValue(0.0);
      iflameDensityField.setValue(0.0);
      previewButton.setSelected(false);
      statisticsTextArea.setText("");
      imageRedChangeField.setValue(0.0);
      imageGreenChangeField.setValue(0.0);
      imageBlueChangeField.setValue(0.0);
      imageHueChangeField.setValue(0.0);
      imageSaturationChangeField.setValue(0.0);
      imageLightnessChangeField.setValue(0.0);
    }
    else {
      edgesNorthButton.setSelected(iflame.getImageParams().getConv_north() == 1);
      edgesWestButton.setSelected(iflame.getImageParams().getConv_west() == 1);
      edgesEastButton.setSelected(iflame.getImageParams().getConv_east() == 1);
      edgesSouthButton.setSelected(iflame.getImageParams().getConv_south() == 1);
      erodeButton.setSelected(iflame.getImageParams().getErode() == 1);
      erodeSizeField.setValue(iflame.getImageParams().getErodeSize());
      maxImageWidthField.setValue(iflame.getImageParams().getMaxImgWidth());
      structureThresholdField.setValue(iflame.getImageParams().getStructure_threshold());
      structureDensityField.setValue(iflame.getImageParams().getStructure_density());
      globalScaleXField.setValue(iflame.getImageParams().getScaleX());
      globalScaleYField.setValue(iflame.getImageParams().getScaleY());
      globalScaleZField.setValue(iflame.getImageParams().getScaleZ());
      globalOffsetXField.setValue(iflame.getImageParams().getOffsetX());
      globalOffsetYField.setValue(iflame.getImageParams().getOffsetY());
      globalOffsetZField.setValue(iflame.getImageParams().getOffsetZ());
      shapeDistributionCmb.setSelectedItem(iflame.getImageParams().getShape_distribution());
      iflameBrightnessField.setValue(iflame.getImageParams().getIFlame_brightness());
      imageBrightnessField.setValue(iflame.getImageParams().getImage_brightness());
      iflameDensityField.setValue(iflame.getImageParams().getIFlame_density());
      previewButton.setSelected(iflame.getMotionParams().getPreview() == 1);
      imageRedChangeField.setValue(iflame.getImageParams().getColorChangeRed());
      imageGreenChangeField.setValue(iflame.getImageParams().getColorChangeGreen());
      imageBlueChangeField.setValue(iflame.getImageParams().getColorChangeBlue());
      imageHueChangeField.setValue(iflame.getImageParams().getColorChangeHue());
      imageSaturationChangeField.setValue(iflame.getImageParams().getColorChangeSaturation());
      imageLightnessChangeField.setValue(iflame.getImageParams().getColorChangeLightness());
    }
  }

  private void refreshMotionFields() {
    IFlamesFunc iflame = getIFlamesFunc();
    if (iflame == null) {
      motionTimeField.setValue(0.0);
      motionLifeTimeField.setValue(0.0);
      motionLifeTimeVariationField.setValue(0.0);
      motionForceXField.setValue(0.0);
      motionForceYField.setValue(0.0);
      motionForceZField.setValue(0.0);
      forceCentreXField.setValue(0.0);
      forceCentreYField.setValue(0.0);
      forceCentreZField.setValue(0.0);
    }
    else {
      motionTimeField.setValue(iflame.getMotionParams().getTime());
      motionLifeTimeField.setValue(iflame.getMotionParams().getLife());
      motionLifeTimeVariationField.setValue(iflame.getMotionParams().getLifeVar());
      motionForceXField.setValue(iflame.getMotionParams().getForceX0());
      motionForceYField.setValue(iflame.getMotionParams().getForceY0());
      motionForceZField.setValue(iflame.getMotionParams().getForceZ0());
      forceCentreXField.setValue(iflame.getMotionParams().getForceCentreX());
      forceCentreYField.setValue(iflame.getMotionParams().getForceCentreY());
      forceCentreZField.setValue(iflame.getMotionParams().getForceCentreZ());
    }
  }

  private void refreshBaseFlameFields() {
    IFlamesFunc iflame = getIFlamesFunc();
    if (iflame == null) {
      baseFlameSizeField.setValue(0.0);
      baseFlameSizeVariationField.setValue(0.0);
      baseFlameRotateAlphaField.setValue(0.0);
      baseFlameRotateAlphaVariationField.setValue(0.0);
      baseFlameRotateBetaField.setValue(0.0);
      baseFlameRotateBetaVariationField.setValue(0.0);
      baseFlameRotateGammaField.setValue(0.0);
      baseFlameRotateGammaVariationField.setValue(0.0);
      baseFlameCentreXField.setValue(0.0);
      baseFlameCentreYField.setValue(0.0);
      baseFlameCentreZField.setValue(0.0);
      baseFlameMinValueField.setValue(0.0);
      baseFlameMaxValueField.setValue(0.0);
      baseFlameWeightField.setValue(0.0);
      baseFlameGridXOffsetField.setValue(0.0);
      baseFlameGridYOffsetField.setValue(0.0);
      baseFlameGridXSizeField.setValue(0.0);
      baseFlameGridYSizeField.setValue(0.0);
      selectedMutationCmb.setSelectedIndex(0);
      speedXField.setValue(0.0);
      speedYField.setValue(0.0);
      speedZField.setValue(0.0);
      speedXVarField.setValue(0.0);
      speedYVarField.setValue(0.0);
      speedZVarField.setValue(0.0);
      speedAlphaField.setValue(0.0);
      speedBetaField.setValue(0.0);
      speedGammaField.setValue(0.0);
      speedAlphaVarField.setValue(0.0);
      speedBetaVarField.setValue(0.0);
      speedGammaVarField.setValue(0.0);
      radialAccelField.setValue(0.0);
      radialAccelVarField.setValue(0.0);
      tangentialAccelField.setValue(0.0);
      tangentialAccelVarField.setValue(0.0);
      baseFlameBrightnessMinField.setValue(0.0);
      baseFlameBrightnessMaxField.setValue(0.0);
      baseFlameBrightnessChangeField.setValue(0.0);
      baseFlameInstancingCBx.setSelected(false);
    }
    else {
      baseFlameSizeField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getSize());
      baseFlameSizeVariationField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getSizeVar());
      baseFlameRotateAlphaField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateAlpha());
      baseFlameRotateAlphaVariationField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateAlphaVar());
      baseFlameRotateBetaField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateBeta());
      baseFlameRotateBetaVariationField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateBetaVar());
      baseFlameRotateGammaField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateGamma());
      baseFlameRotateGammaVariationField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateGammaVar());
      baseFlameCentreXField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getCentreX());
      baseFlameCentreYField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getCentreY());
      baseFlameCentreZField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getCentreZ());
      baseFlameMinValueField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getMinVal());
      baseFlameMaxValueField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getMaxVal());
      baseFlameWeightField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getWeight());
      baseFlameGridXOffsetField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getGridXOffset());
      baseFlameGridYOffsetField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getGridYOffset());
      baseFlameGridXSizeField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getGridXSize());
      baseFlameGridYSizeField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getGridYSize());
      speedXField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getSpeedX());
      speedYField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getSpeedY());
      speedZField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getSpeedZ());
      speedXVarField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getSpeedXVar());
      speedYVarField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getSpeedYVar());
      speedZVarField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getSpeedZVar());
      speedAlphaField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateAlphaSpeed());
      speedBetaField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateBetaSpeed());
      speedGammaField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateGammaSpeed());
      speedAlphaVarField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateAlphaSpeedVar());
      speedBetaVarField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateBetaSpeedVar());
      speedGammaVarField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRotateGammaSpeedVar());
      radialAccelField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRadialAcceleration());
      radialAccelVarField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getRadialAccelerationVar());
      tangentialAccelField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getTangentialAcceleration());
      tangentialAccelVarField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getTangentialAccelerationVar());
      baseFlameBrightnessMinField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getBrightnessMin());
      baseFlameBrightnessMaxField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getBrightnessMax());
      baseFlameBrightnessChangeField.setValue(iflame.getFlameParams(getCurrFlameIndex()).getBrightnessChange());
      baseFlameInstancingCBx.setSelected(iflame.getFlameParams(getCurrFlameIndex()).isInstancing());

      switch (iflame.getImageParams().getShape_distribution()) {
        case BRIGHTNESS:
          baseFlameMinValueLabel.setText("Min Brightness");
          baseFlameMaxValueLabel.setText("Max Brightness");
          break;
        case LUMINOSITY:
          baseFlameMinValueLabel.setText("Min Luminosity");
          baseFlameMaxValueLabel.setText("Max Luminosity");
          break;
        case HUE:
          baseFlameMinValueLabel.setText("Min Hue");
          baseFlameMaxValueLabel.setText("Max Hue");
          break;
        default:
          baseFlameMinValueLabel.setText("Min Value");
          baseFlameMaxValueLabel.setText("Max Value");
          break;
      }
    }
    boolean oldNoRefresh = noRefresh;
    noRefresh = true;
    try {
      flamePropertiesTreeService.refreshFlamePropertiesTree(paramPropertyPathTree, getBaseFlame());
    }
    finally {
      noRefresh = oldNoRefresh;
    }
    refreshMutationFields();
  }

  private void refreshMutationFields() {
    IFlamesFunc iflame = getIFlamesFunc();
    if (iflame == null) {
      paramMinValueField.setValue(0.0);
      paramMaxValueField.setValue(0.0);
      paramPropertyPathTree.clearSelection();
    }
    else {
      FlameParams flameParams = iflame.getFlameParams(getCurrFlameIndex());
      switch (getCurrMutationIndex()) {
        case 0:
          fillMutationFields(getBaseFlame(), flameParams.getFlameParam1Min(), flameParams.getFlameParam1Max(), flameParams.getFlameParam1());
          break;
        case 1:
          fillMutationFields(getBaseFlame(), flameParams.getFlameParam2Min(), flameParams.getFlameParam2Max(), flameParams.getFlameParam2());
          break;
        case 2:
          fillMutationFields(getBaseFlame(), flameParams.getFlameParam3Min(), flameParams.getFlameParam3Max(), flameParams.getFlameParam3());
          break;
        default: // nothing to do
          break;
      }
    }
  }

  private void fillMutationFields(Flame pFlame, double pParamMin, double pParamMax, String pParamPath) {
    paramMinValueField.setValue(pParamMin);
    paramMaxValueField.setValue(pParamMax);
    boolean oldNoRefresh = noRefresh;
    noRefresh = true;
    try {
      if (pParamPath == null || pParamPath.length() == 0 || pFlame == null) {
        paramPropertyPathTree.clearSelection();
      }
      else {
        FlamePropertyPath path = new FlamePropertyPath(pFlame, pParamPath);
        flamePropertiesTreeService.selectPropertyPath(paramPropertyPathTree, path);
      }
    }
    finally {
      noRefresh = oldNoRefresh;
    }
  }

  public void loadIFlameFromClipboardButton_clicked() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new FlameReader(prefs).readFlamesfromXML(xml);
          Flame flame = flames.get(0);
          importFlame(flame);
          messageHelper.showStatusMessage(getFlame(), "opened from clipboard");
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void loadIFlameButton_clicked() {
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
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
        Flame flame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        importFlame(flame);
        getFlame().setLastFilename(file.getName());
        messageHelper.showStatusMessage(getFlame(), "opened from disc");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void importFlame(Flame pFlame) {
    setFlame(pFlame);
    undoManager.initUndoStack(getFlame());
    setupProfiles(getFlame());
    refreshUI();
    enableControls();
  }

  public void saveIFlameToClipboardButton_clicked() {
    try {
      if (getFlame() != null) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String xml = new FlameWriter().getFlameXML(getFlame());
        StringSelection data = new StringSelection(xml);
        clipboard.setContents(data, data);
        messageHelper.showStatusMessage(getFlame(), "flame saved to clipboard");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void saveIFlameButton_clicked() {
    try {
      if (getFlame() != null) {
        JFileChooser chooser = new FlameFileChooser(prefs);
        if (prefs.getOutputFlamePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          new FlameWriter().writeFlame(getFlame(), file.getAbsolutePath());
          getFlame().setLastFilename(file.getName());
          messageHelper.showStatusMessage(getFlame(), "flame saved to disc");
          prefs.setLastOutputFlameFile(file);
        }
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
        for (int i = 0; i < resolutionProfileCmb.getItemCount(); i++) {
          profile = (ResolutionProfile) resolutionProfileCmb.getItemAt(i);
          if (pFlame.getResolutionProfile().equals(profile.toString()))
            break;
          else
            profile = null;
        }
        if (profile != null) {
          resolutionProfileCmb.setSelectedItem(profile);
        }
      }
    }
  }

  @Override
  public JProgressBar getRenderProgressBar() {
    return mainProgressBar;
  }

  public void edgesNorthButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setConv_north(edgesNorthButton.isSelected() ? 1 : 0);
    refreshIFlame();
  }

  public void edgesEastButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setConv_east(edgesNorthButton.isSelected() ? 1 : 0);
    refreshIFlame();
  }

  public void edgesSouthButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setConv_south(edgesSouthButton.isSelected() ? 1 : 0);
    refreshIFlame();
  }

  public void edgesWestButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setConv_west(edgesWestButton.isSelected() ? 1 : 0);
    refreshIFlame();
  }

  public void erodeButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setErode(erodeButton.isSelected() ? 1 : 0);
    refreshIFlame();
    enableControls();
  }

  public void displayPreprocessedImageButton_clicked() {
    refreshPreview();
  }

  public void refreshIFlameButton_clicked() {
    RessourceManager.clearRessources(IFlamesFunc.KEY_PREFIX);
    refreshPreview();
  }

  public void erodeSizeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setErodeSize(erodeSizeField.getIntValue());
    refreshIFlame();
    enableControls();
  }

  public void maxImageWidthField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setMaxImgWidth(maxImageWidthField.getIntValue());
    refreshIFlame();
    enableControls();
  }

  public void shapeDistributionCmb_changed() {
    if (noRefresh || getIFlamesFunc() == null) {
      return;
    }
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setShape_distribution((ShapeDistribution) shapeDistributionCmb.getSelectedItem());
    refreshIFlame();
    enableControls();
  }

  public void structureThresholdField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setStructure_threshold(structureThresholdField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void structureDensityField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setStructure_density(structureDensityField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalScaleXField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setScaleX(globalScaleXField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalScaleYField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setScaleY(globalScaleYField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalScaleZField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setScaleZ(globalScaleZField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalOffsetXField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setOffsetX(globalOffsetXField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalOffsetYField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setOffsetY(globalOffsetYField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalOffsetZField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setOffsetZ(globalOffsetZField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void reloadLibraryButton_clicked() {
    reloadFlameLibrary();
    reloadImageLibrary();
  }

  private void reloadFlameLibrary() {
    flameLibrary.clear();
    if (prefs.getIflamesFlameLibraryPath() != null && prefs.getIflamesFlameLibraryPath().length() > 0) {
      List<String> filenames = new ArrayList<String>();
      _scanFiles(prefs.getIflamesFlameLibraryPath(), filenames);
      for (String filename : filenames) {
        if (filename.endsWith("." + Tools.FILEEXT_FLAME)) {
          try {
            List<Flame> flames = new FlameReader(prefs).readFlames(filename);
            for (int i = flames.size() - 1; i >= 0; i--) {
              addFlameToFlameLibrary(flames.get(i), new ThumbnailCacheKey(filename, String.valueOf(i)));
            }
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    }
    refreshFlameLibrary();
    flameLibraryScrollPane.getParent().repaint();
  }

  private void reloadImageLibrary() {
    imageLibrary.clear();
    if (prefs.getIflamesImageLibraryPath() != null && prefs.getIflamesImageLibraryPath().length() > 0) {
      List<String> filenames = new ArrayList<String>();
      _scanFiles(prefs.getIflamesImageLibraryPath(), filenames);
      for (String filename : filenames) {
        if (filename.endsWith("." + Tools.FILEEXT_PNG) || filename.endsWith("." + Tools.FILEEXT_JPG)) {
          try {
            addImageToImageLibrary(filename, new ThumbnailCacheKey(filename));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    }
    refreshImageLibrary();
    imageLibraryScrollPane.getParent().repaint();
  }

  public void _scanFiles(String pPath, List<String> pFilenames) {
    File root = new File(pPath);
    File[] list = root.listFiles();
    if (list != null) {
      try {
        Arrays.sort(list, new Comparator<File>() {

          @Override
          public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
          }

        });
      }
      catch (Exception ex) {
        // ex.printStackTrace();
      }
      for (File f : list) {
        if (f.isDirectory()) {
          _scanFiles(f.getAbsolutePath(), pFilenames);
        }
        else {
          pFilenames.add(f.getAbsolutePath());
        }
      }
    }
  }

  public void iflameBrightnessField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setIflame_brightness(iflameBrightnessField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void imageBrightnessField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setImage_brightness(imageBrightnessField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void iflameDensityField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setIflame_density(iflameDensityField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameSizeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setSize(baseFlameSizeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameSizeVariationField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setSizeVar(baseFlameSizeVariationField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameRotateAlphaField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateAlpha(baseFlameRotateAlphaField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameRotateAlphaVariationField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateAlphaVar(baseFlameRotateAlphaVariationField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameRotateBetaField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateBeta(baseFlameRotateBetaField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameRotateBetaVariationField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateBetaVar(baseFlameRotateBetaVariationField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameRotateGammaField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateGamma(baseFlameRotateGammaField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameRotateGammaVariationField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateGammaVar(baseFlameRotateGammaVariationField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameCentreXField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setCentreX(baseFlameCentreXField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameCentreYField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setCentreY(baseFlameCentreYField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameCentreZField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setCentreZ(baseFlameCentreZField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameFromClipboardButton_clicked() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          saveUndoPoint();
          getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameXML(xml);
          refreshBaseFlamePreview();
          refreshIFlame();
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void baseFlameToClipboardButton_clicked() {
    try {
      String xml = getIFlamesFunc().getFlameParams(getCurrFlameIndex()).getFlameXML();
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection data = new StringSelection(xml);
      clipboard.setContents(data, data);
      messageHelper.showStatusMessage(getFlame(), "flame saved to clipboard");
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void baseFlameClearButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameXML(null);
    enableControls();
    refreshBaseFlamePreview();
    refreshIFlame();
  }

  public void baseFlameClearAllButton_clicked() {
    saveUndoPoint();
    for (int i = 0; i < IFlamesFunc.MAX_FLAME_COUNT; i++) {
      getIFlamesFunc().getFlameParams(i).setFlameXML(null);
    }
    enableControls();
    refreshBaseFlamePreview();
    refreshIFlame();
  }

  public void previewButton_clicked() {
    getIFlamesFunc().getMotionParams().setPreview(previewButton.isSelected() ? 1 : 0);
    refreshPreview();
  }

  public void baseFlameMinValueField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setMinVal(baseFlameMinValueField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameMaxValueField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setMaxVal(baseFlameMaxValueField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameWeightField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setWeight(baseFlameWeightField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameGridXOffsetField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setGridXOffset(baseFlameGridXOffsetField.getIntValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameGridYOffsetField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setGridYOffset(baseFlameGridYOffsetField.getIntValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameGridXSizeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setGridXSize(baseFlameGridXSizeField.getIntValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameGridYSizeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setGridYSize(baseFlameGridYSizeField.getIntValue());
    refreshIFlame();
    enableControls();
  }

  public void paramPropertyPathTree_changed() {
    if (noRefresh || getFlame() == null) {
      return;
    }
    boolean plainPropertySelected = flamePropertiesTreeService.isPlainPropertySelected(paramPropertyPathTree);
    String path;
    if (plainPropertySelected) {
      FlamePropertyPath selPath = flamePropertiesTreeService.getSelectedPropertyPath(paramPropertyPathTree);
      path = selPath.getPath();
    }
    else {
      path = null;
    }
    saveUndoPoint();
    switch (getCurrMutationIndex()) {
      case 0:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam1(path);
        break;
      case 1:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam2(path);
        break;
      case 2:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam3(path);
        break;
      default: // nothing to do
        break;
    }
    refreshIFlame();
    enableControls();
  }

  public void selectedMutationCmb_changed() {
    if (noRefresh || getFlame() == null) {
      return;
    }
    enableControls();
    refreshMutationFields();
  }

  public void paramMinValueField_changed() {
    saveUndoPoint();
    switch (getCurrMutationIndex()) {
      case 0:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam1Min(paramMinValueField.getDoubleValue());
        break;
      case 1:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam2Min(paramMinValueField.getDoubleValue());
        break;
      case 2:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam3Min(paramMinValueField.getDoubleValue());
        break;
      default: // nothing to do
        break;
    }
    refreshIFlame();
    enableControls();
  }

  public void paramMaxValueField_changed() {
    saveUndoPoint();
    switch (getCurrMutationIndex()) {
      case 0:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam1Max(paramMaxValueField.getDoubleValue());
        break;
      case 1:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam2Max(paramMaxValueField.getDoubleValue());
        break;
      case 2:
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameParam3Max(paramMaxValueField.getDoubleValue());
        break;
      default: // nothing to do
        break;
    }
    refreshIFlame();
    enableControls();
  }

  public void iflameToEditorButton_clicked() {
    tinaController.importFlame(getFlame(), true);
  }

  public void motionForceZField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setForceZ0(motionForceZField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void motionForceYField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setForceY0(motionForceYField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void motionForceXField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setForceX0(motionForceXField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void motionLifeTimeVariationField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setLifeVar(motionLifeTimeVariationField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void motionLifeTimeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setLife(motionLifeTimeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void motionTimeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setTime(motionTimeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedXField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setSpeedX(speedXField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedYField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setSpeedY(speedYField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedAlphaField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateAlphaSpeed(speedAlphaField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedBetaVarField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateBetaSpeedVar(speedBetaVarField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedGammaVarField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateGammaSpeedVar(speedGammaVarField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedXVarField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setSpeedXVar(speedXVarField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedZField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setSpeedZ(speedZField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedZVarField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setSpeedZVar(speedZVarField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedBetaField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateBetaSpeed(speedBetaField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedYVarField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setSpeedYVar(speedYVarField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedGammaField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateGammaSpeed(speedGammaField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void speedAlphaVarField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRotateAlphaSpeedVar(speedAlphaVarField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void tangentialAccelVarField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setTangentialAccelerationVar(tangentialAccelVarField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void tangentialAccelField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setTangentialAcceleration(tangentialAccelField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void radialAccelVarField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRadialAccelerationVar(radialAccelVarField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void radialAccelField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setRadialAcceleration(radialAccelField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void forceCentreXField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setForceCentreX(forceCentreXField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void forceCentreYField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setForceCentreY(forceCentreYField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void forceCentreZField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getMotionParams().setForceCentreZ(forceCentreZField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameClearOthersButton_clicked() {
    saveUndoPoint();
    int currIdx = getCurrFlameIndex();
    for (int i = 0; i < IFlamesFunc.MAX_FLAME_COUNT; i++) {
      if (i != currIdx) {
        getIFlamesFunc().getFlameParams(i).setFlameXML(null);
      }
    }
    enableControls();
    refreshBaseFlamePreview();
    refreshIFlame();
  }

  public void copyDynamicsParamsToOthersButton_clicked() {
    saveUndoPoint();
    int currIdx = getCurrFlameIndex();
    FlameParams src = getIFlamesFunc().getFlameParams(getCurrFlameIndex());

    for (int i = 0; i < IFlamesFunc.MAX_FLAME_COUNT; i++) {
      if (i != currIdx) {
        FlameParams dst = getIFlamesFunc().getFlameParams(i);
        dst.setRadialAcceleration(src.getRadialAcceleration());
        dst.setRadialAccelerationVar(src.getRadialAccelerationVar());
        dst.setTangentialAcceleration(src.getTangentialAcceleration());
        dst.setTangentialAccelerationVar(src.getTangentialAccelerationVar());
        dst.setSpeedX(src.getSpeedX());
        dst.setSpeedXVar(src.getSpeedXVar());
        dst.setSpeedY(src.getSpeedY());
        dst.setSpeedYVar(src.getSpeedYVar());
        dst.setSpeedZ(src.getSpeedZ());
        dst.setSpeedZVar(src.getSpeedZVar());
        dst.setRotateAlphaSpeed(src.getRotateAlphaSpeed());
        dst.setRotateAlphaSpeedVar(src.getRotateAlphaSpeedVar());
        dst.setRotateBetaSpeed(src.getRotateBetaSpeed());
        dst.setRotateBetaSpeedVar(src.getRotateBetaSpeedVar());
        dst.setRotateGammaSpeed(src.getRotateGammaSpeed());
        dst.setRotateGammaSpeedVar(src.getRotateGammaSpeedVar());
      }
    }
    enableControls();
    refreshBaseFlamePreview();
    refreshIFlame();
  }

  public void clearCacheButton_clicked() {
    RessourceManager.clearAll();
    System.gc();
  }

  public void baseFlameBrightnessMinField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setBrightnessMin(baseFlameBrightnessMinField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameBrightnessMaxField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setBrightnessMax(baseFlameBrightnessMaxField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameBrightnessChangeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setBrightnessChange(baseFlameBrightnessChangeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void imageRedChangeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setColorChangeRed(imageRedChangeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void imageGreenChangeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setColorChangeGreen(imageGreenChangeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void imageBlueChangeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setColorChangeBlue(imageBlueChangeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void imageHueChangeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setColorChangeHue(imageHueChangeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void imageSaturationChangeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setColorChangeSaturation(imageSaturationChangeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void imageLightnessChangeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setColorChangeLightness(imageLightnessChangeField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void baseFlameInstancingCbx_changed() {
    saveUndoPoint();
    getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setInstancing(baseFlameInstancingCBx.isSelected());
    refreshIFlame();
    enableControls();
  }

  public void copyBaseFlameParamsToOthersButton_clicked() {
    saveUndoPoint();
    int currIdx = getCurrFlameIndex();
    FlameParams src = getIFlamesFunc().getFlameParams(getCurrFlameIndex());

    for (int i = 0; i < IFlamesFunc.MAX_FLAME_COUNT; i++) {
      if (i != currIdx) {
        FlameParams dst = getIFlamesFunc().getFlameParams(i);
        dst.setSize(src.getSize());
        dst.setSizeVar(src.getSizeVar());
        dst.setCentreX(src.getCentreX());
        dst.setCentreY(src.getCentreY());
        dst.setCentreZ(src.getCentreZ());
        dst.setRotateAlpha(src.getRotateAlpha());
        dst.setRotateAlphaVar(src.getRotateAlphaVar());
        dst.setRotateBeta(src.getRotateBeta());
        dst.setRotateBetaVar(src.getRotateBetaVar());
        dst.setRotateGamma(src.getRotateGamma());
        dst.setRotateGammaVar(src.getRotateGammaVar());
        dst.setInstancing(src.isInstancing());
      }
    }
    enableControls();
    refreshBaseFlamePreview();
    refreshIFlame();
  }
}
