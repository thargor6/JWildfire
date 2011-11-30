/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RandomFlameGenerator;
import org.jwildfire.create.tina.base.RandomFlameGeneratorStyle;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.io.Flam3Writer;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RGBPaletteImporter;
import org.jwildfire.create.tina.palette.RGBPaletteRenderer;
import org.jwildfire.create.tina.palette.RandomRGBPaletteGenerator;
import org.jwildfire.create.tina.render.AffineZStyle;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.transform.AnimationService;
import org.jwildfire.create.tina.transform.AnimationService.GlobalScript;
import org.jwildfire.create.tina.transform.AnimationService.XFormScript;
import org.jwildfire.create.tina.transform.FlameMorphService;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.swing.DefaultFileChooser;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileFilter;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.MainController;

public class TinaController implements FlameHolder {
  private static final double SLIDER_SCALE_PERSPECTIVE = 100.0;
  private static final double SLIDER_SCALE_CENTRE = 50.0;
  private static final double SLIDER_SCALE_ZOOM = 10.0;
  private static final double SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY = 100.0;
  private static final double SLIDER_SCALE_GAMMA = 100.0;
  private static final double SLIDER_SCALE_FILTER_RADIUS = 100.0;
  private static final double SLIDER_SCALE_COLOR = 100.0;

  private final JPanel centerPanel;
  private FlamePanel flamePanel;

  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  boolean gridRefreshing = false;
  boolean cmbRefreshing = false;
  boolean refreshing = false;

  public static class NonlinearControlsRow {
    private final JComboBox nonlinearVarCmb;
    private final JComboBox nonlinearParamsCmb;
    private final JTextField nonlinearVarREd;
    private final JTextField nonlinearParamsREd;
    private final JButton nonlinearVarLeftButton;
    private final JButton nonlinearVarRightButton;
    private final JButton nonlinearParamsLeftButton;
    private final JButton nonlinearParamsRightButton;

    public NonlinearControlsRow(JComboBox pNonlinearVarCmb, JComboBox pNonlinearParamsCmb, JTextField pNonlinearVarREd, JTextField pNonlinearParamsREd,
        JButton pNonlinearVarLeftButton, JButton pNonlinearVarRightButton, JButton pNonlinearParamsLeftButton, JButton pNonlinearParamsRightButton) {
      nonlinearVarCmb = pNonlinearVarCmb;
      nonlinearParamsCmb = pNonlinearParamsCmb;
      nonlinearVarREd = pNonlinearVarREd;
      nonlinearParamsREd = pNonlinearParamsREd;
      nonlinearVarLeftButton = pNonlinearVarLeftButton;
      nonlinearVarRightButton = pNonlinearVarRightButton;
      nonlinearParamsLeftButton = pNonlinearParamsLeftButton;
      nonlinearParamsRightButton = pNonlinearParamsRightButton;
    }

    public void initControls() {
      nonlinearVarCmb.removeAllItems();
      List<String> nameList = new ArrayList<String>();
      nameList.addAll(VariationFuncList.getNameList());
      Collections.sort(nameList);
      for (String name : nameList) {
        nonlinearVarCmb.addItem(name);
      }
      nonlinearVarCmb.setSelectedIndex(-1);

      nonlinearParamsCmb.removeAllItems();
      nonlinearParamsCmb.setSelectedIndex(-1);
    }

    public JComboBox getNonlinearVarCmb() {
      return nonlinearVarCmb;
    }

    public JComboBox getNonlinearParamsCmb() {
      return nonlinearParamsCmb;
    }

    public JTextField getNonlinearVarREd() {
      return nonlinearVarREd;
    }

    public JTextField getNonlinearParamsREd() {
      return nonlinearParamsREd;
    }

    public JButton getNonlinearVarLeftButton() {
      return nonlinearVarLeftButton;
    }

    public JButton getNonlinearVarRightButton() {
      return nonlinearVarRightButton;
    }

    public JButton getNonlinearParamsLeftButton() {
      return nonlinearParamsLeftButton;
    }

    public JButton getNonlinearParamsRightButton() {
      return nonlinearParamsRightButton;
    }
  }

  private MainController mainController;
  // camera, coloring
  private final JTextField cameraRollREd;
  private final JSlider cameraRollSlider;
  private final JTextField cameraPitchREd;
  private final JSlider cameraPitchSlider;
  private final JTextField cameraYawREd;
  private final JSlider cameraYawSlider;
  private final JTextField cameraPerspectiveREd;
  private final JSlider cameraPerspectiveSlider;
  private final JTextField previewQualityREd;
  private final JTextField renderQualityREd;
  private final JTextField cameraCentreXREd;
  private final JSlider cameraCentreXSlider;
  private final JTextField cameraCentreYREd;
  private final JSlider cameraCentreYSlider;
  private final JTextField cameraZoomREd;
  private final JSlider cameraZoomSlider;
  private final JTextField pixelsPerUnitREd;
  private final JSlider pixelsPerUnitSlider;
  private final JTextField brightnessREd;
  private final JSlider brightnessSlider;
  private final JTextField contrastREd;
  private final JSlider contrastSlider;
  private final JTextField gammaREd;
  private final JSlider gammaSlider;
  private final JTextField vibrancyREd;
  private final JSlider vibrancySlider;
  private final JTextField filterRadiusREd;
  private final JSlider filterRadiusSlider;
  private final JTextField spatialOversampleREd;
  private final JSlider spatialOversampleSlider;
  private final JTextField colorOversampleREd;
  private final JSlider colorOversampleSlider;
  private final JTextField bgColorRedREd;
  private final JSlider bgColorRedSlider;
  private final JTextField bgColorGreenREd;
  private final JSlider bgColorGreenSlider;
  private final JTextField bgColorBlueREd;
  private final JSlider bgColorBlueSlider;
  private final JTextField renderWidthREd;
  private final JTextField renderHeightREd;
  // palette -> create
  private final JTextField paletteRandomPointsREd;
  private final JPanel paletteImgPanel;
  private ImagePanel palettePanel;
  // palette -> transform
  private final JTextField paletteShiftREd;
  private final JSlider paletteShiftSlider;
  private final JTextField paletteRedREd;
  private final JSlider paletteRedSlider;
  private final JTextField paletteGreenREd;
  private final JSlider paletteGreenSlider;
  private final JTextField paletteBlueREd;
  private final JSlider paletteBlueSlider;
  private final JTextField paletteHueREd;
  private final JSlider paletteHueSlider;
  private final JTextField paletteSaturationREd;
  private final JSlider paletteSaturationSlider;
  private final JTextField paletteContrastREd;
  private final JSlider paletteContrastSlider;
  private final JTextField paletteGammaREd;
  private final JSlider paletteGammaSlider;
  private final JTextField paletteBrightnessREd;
  private final JSlider paletteBrightnessSlider;
  // Transformations
  private final JTable transformationsTable;
  private final JButton affineResetTransformButton;
  private final JTextField affineC00REd;
  private final JTextField affineC01REd;
  private final JTextField affineC10REd;
  private final JTextField affineC11REd;
  private final JTextField affineC20REd;
  private final JTextField affineC21REd;
  private final JTextField affineRotateAmountREd;
  private final JTextField affineScaleAmountREd;
  private final JTextField affineMoveAmountREd;
  private final JButton affineRotateLeftButton;
  private final JButton affineRotateRightButton;
  private final JButton affineEnlargeButton;
  private final JButton affineShrinkButton;
  private final JButton affineMoveUpButton;
  private final JButton affineMoveLeftButton;
  private final JButton affineMoveRightButton;
  private final JButton affineMoveDownButton;
  private final JButton addTransformationButton;
  private final JButton duplicateTransformationButton;
  private final JButton deleteTransformationButton;
  private final JButton addFinalTransformationButton;
  private final JButton transformationWeightLeftButton;
  private final JButton transformationWeightRightButton;
  private final JToggleButton affineEditPostTransformButton;
  private final JToggleButton affineEditPostTransformSmallButton;
  private final JButton mouseEditZoomInButton;
  private final JButton mouseEditZoomOutButton;
  private final JToggleButton toggleTrianglesButton;
  // Random batch
  private final JPanel randomBatchPanel;
  private JScrollPane randomBatchScrollPane = null;
  private final JCheckBox randomPostTransformCheckBox;
  private final JCheckBox randomSymmetryCheckBox;
  // Nonlinear transformations
  private final NonlinearControlsRow[] nonlinearControlsRows;
  // Color
  private final JTextField xFormColorREd;
  private final JSlider xFormColorSlider;
  private final JTextField xFormSymmetryREd;
  private final JSlider xFormSymmetrySlider;
  private final JTextField xFormOpacityREd;
  private final JSlider xFormOpacitySlider;
  private final JComboBox xFormDrawModeCmb;
  // Relative weights
  private final JTable relWeightsTable;
  private final JButton relWeightsLeftButton;
  private final JButton relWeightsRightButton;
  // Morphing
  private final JButton setMorphFlame1Button;
  private final JButton setMorphFlame2Button;
  private final JTextField morphFrameREd;
  private final JTextField morphFramesREd;
  private final JCheckBox morphCheckBox;
  private final JSlider morphFrameSlider;
  private final JButton importMorphedFlameButton;
  // Animate
  private final JTextField animateOutputREd;
  private final JTextField animateFramesREd;
  private final JComboBox animateGlobalScriptCmb;
  private final JComboBox animateXFormScriptCmb;
  private final JButton animationGenerateButton;
  // misc
  private final JComboBox zStyleCmb;
  private Flame _currFlame;
  private Flame morphFlame1, morphFlame2;
  private boolean noRefresh;
  private final ProgressUpdater progressUpdater;
  private final JTable createPaletteColorsTable;
  private List<RGBColor> paletteKeyFrames;
  // mouse dragging
  private final JToggleButton mouseTransformMoveButton;
  private final JToggleButton mouseTransformRotateButton;
  private final JToggleButton mouseTransformScaleButton;

  public TinaController(ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pCenterPanel, JTextField pCameraRollREd, JSlider pCameraRollSlider, JTextField pCameraPitchREd,
      JSlider pCameraPitchSlider, JTextField pCameraYawREd, JSlider pCameraYawSlider, JTextField pCameraPerspectiveREd, JSlider pCameraPerspectiveSlider,
      JTextField pPreviewQualityREd, JTextField pRenderQualityREd, JTextField pCameraCentreXREd, JSlider pCameraCentreXSlider, JTextField pCameraCentreYREd,
      JSlider pCameraCentreYSlider, JTextField pCameraZoomREd, JSlider pCameraZoomSlider, JTextField pPixelsPerUnitREd, JSlider pPixelsPerUnitSlider,
      JTextField pBrightnessREd, JSlider pBrightnessSlider, JTextField pContrastREd, JSlider pContrastSlider, JTextField pGammaREd, JSlider pGammaSlider,
      JTextField pVibrancyREd, JSlider pVibrancySlider, JTextField pFilterRadiusREd, JSlider pFilterRadiusSlider, JTextField pSpatialOversampleREd,
      JSlider pSpatialOversampleSlider, JTextField pBGColorRedREd, JSlider pBGColorRedSlider, JTextField pBGColorGreenREd, JSlider pBGColorGreenSlider, JTextField pBGColorBlueREd,
      JSlider pBGColorBlueSlider, JTextField pPaletteRandomPointsREd, JPanel pPaletteImgPanel, JTextField pPaletteShiftREd, JSlider pPaletteShiftSlider,
      JTextField pPaletteRedREd, JSlider pPaletteRedSlider, JTextField pPaletteGreenREd, JSlider pPaletteGreenSlider, JTextField pPaletteBlueREd,
      JSlider pPaletteBlueSlider, JTextField pPaletteHueREd, JSlider pPaletteHueSlider, JTextField pPaletteSaturationREd, JSlider pPaletteSaturationSlider,
      JTextField pPaletteContrastREd, JSlider pPaletteContrastSlider, JTextField pPaletteGammaREd, JSlider pPaletteGammaSlider, JTextField pPaletteBrightnessREd,
      JSlider pPaletteBrightnessSlider, JTextField pRenderWidthREd, JTextField pRenderHeightREd, JTable pTransformationsTable, JTextField pAffineC00REd,
      JTextField pAffineC01REd, JTextField pAffineC10REd, JTextField pAffineC11REd, JTextField pAffineC20REd, JTextField pAffineC21REd,
      JTextField pAffineRotateAmountREd, JTextField pAffineScaleAmountREd, JTextField pAffineMoveAmountREd, JButton pAffineRotateLeftButton,
      JButton pAffineRotateRightButton, JButton pAffineEnlargeButton, JButton pAffineShrinkButton, JButton pAffineMoveUpButton, JButton pAffineMoveLeftButton,
      JButton pAffineMoveRightButton, JButton pAffineMoveDownButton, JButton pAddTransformationButton, JButton pDuplicateTransformationButton,
      JButton pDeleteTransformationButton, JButton pAddFinalTransformationButton, JPanel pRandomBatchPanel, NonlinearControlsRow[] pNonlinearControlsRows,
      JTextField pXFormColorREd, JSlider pXFormColorSlider, JTextField pXFormSymmetryREd, JSlider pXFormSymmetrySlider, JTextField pXFormOpacityREd,
      JSlider pXFormOpacitySlider, JComboBox pXFormDrawModeCmb, JTable pRelWeightsTable, JButton pRelWeightsLeftButton, JButton pRelWeightsRightButton,
      JButton pTransformationWeightLeftButton, JButton pTransformationWeightRightButton, JComboBox pZStyleCmb, JButton pSetMorphFlame1Button,
      JButton pSetMorphFlame2Button, JTextField pMorphFrameREd, JTextField pMorphFramesREd, JCheckBox pMorphCheckBox, JSlider pMorphFrameSlider,
      JButton pImportMorphedFlameButton, JTextField pAnimateOutputREd, JTextField pAnimateFramesREd, JComboBox pAnimateGlobalScriptCmb, JButton pAnimationGenerateButton,
      JComboBox pAnimateXFormScriptCmb, JToggleButton pMouseTransformMoveButton, JToggleButton pMouseTransformRotateButton, JToggleButton pMouseTransformScaleButton,
      JToggleButton pAffineEditPostTransformButton, JToggleButton pAffineEditPostTransformSmallButton, JButton pMouseEditZoomInButton, JButton pMouseEditZoomOutButton,
      JToggleButton pToggleTrianglesButton, ProgressUpdater pProgressUpdater, JCheckBox pRandomPostTransformCheckBox, JCheckBox pRandomSymmetryCheckBox,
      JButton pAffineResetTransformButton, JTextField pColorOversampleREd, JSlider pColorOversampleSlider, JTable pCreatePaletteColorsTable) {
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    centerPanel = pCenterPanel;

    cameraRollREd = pCameraRollREd;
    cameraRollSlider = pCameraRollSlider;
    cameraPitchREd = pCameraPitchREd;
    cameraPitchSlider = pCameraPitchSlider;
    cameraYawREd = pCameraYawREd;
    cameraYawSlider = pCameraYawSlider;
    cameraPerspectiveREd = pCameraPerspectiveREd;
    cameraPerspectiveSlider = pCameraPerspectiveSlider;
    previewQualityREd = pPreviewQualityREd;
    renderQualityREd = pRenderQualityREd;
    cameraCentreXREd = pCameraCentreXREd;
    cameraCentreXSlider = pCameraCentreXSlider;
    cameraCentreYREd = pCameraCentreYREd;
    cameraCentreYSlider = pCameraCentreYSlider;
    cameraZoomREd = pCameraZoomREd;
    cameraZoomSlider = pCameraZoomSlider;
    pixelsPerUnitREd = pPixelsPerUnitREd;
    pixelsPerUnitSlider = pPixelsPerUnitSlider;
    brightnessREd = pBrightnessREd;
    brightnessSlider = pBrightnessSlider;
    contrastREd = pContrastREd;
    contrastSlider = pContrastSlider;
    gammaREd = pGammaREd;
    gammaSlider = pGammaSlider;
    vibrancyREd = pVibrancyREd;
    vibrancySlider = pVibrancySlider;
    filterRadiusREd = pFilterRadiusREd;
    filterRadiusSlider = pFilterRadiusSlider;
    spatialOversampleREd = pSpatialOversampleREd;
    spatialOversampleSlider = pSpatialOversampleSlider;
    colorOversampleREd = pColorOversampleREd;
    colorOversampleSlider = pColorOversampleSlider;
    bgColorRedREd = pBGColorRedREd;
    bgColorRedSlider = pBGColorRedSlider;
    bgColorGreenREd = pBGColorGreenREd;
    bgColorGreenSlider = pBGColorGreenSlider;
    bgColorBlueREd = pBGColorBlueREd;
    bgColorBlueSlider = pBGColorBlueSlider;
    paletteRandomPointsREd = pPaletteRandomPointsREd;
    paletteImgPanel = pPaletteImgPanel;
    renderWidthREd = pRenderWidthREd;
    renderHeightREd = pRenderHeightREd;

    paletteShiftREd = pPaletteShiftREd;
    paletteShiftSlider = pPaletteShiftSlider;
    paletteRedREd = pPaletteRedREd;
    paletteRedSlider = pPaletteRedSlider;
    paletteGreenREd = pPaletteGreenREd;
    paletteGreenSlider = pPaletteGreenSlider;
    paletteBlueREd = pPaletteBlueREd;
    paletteBlueSlider = pPaletteBlueSlider;
    paletteHueREd = pPaletteHueREd;
    paletteHueSlider = pPaletteHueSlider;
    paletteSaturationREd = pPaletteSaturationREd;
    paletteSaturationSlider = pPaletteSaturationSlider;
    paletteContrastREd = pPaletteContrastREd;
    paletteContrastSlider = pPaletteContrastSlider;
    paletteGammaREd = pPaletteGammaREd;
    paletteGammaSlider = pPaletteGammaSlider;
    paletteBrightnessREd = pPaletteBrightnessREd;
    paletteBrightnessSlider = pPaletteBrightnessSlider;

    transformationsTable = pTransformationsTable;
    affineC00REd = pAffineC00REd;
    affineC01REd = pAffineC01REd;
    affineC10REd = pAffineC10REd;
    affineC11REd = pAffineC11REd;
    affineC20REd = pAffineC20REd;
    affineC21REd = pAffineC21REd;
    affineRotateAmountREd = pAffineRotateAmountREd;
    affineScaleAmountREd = pAffineScaleAmountREd;
    affineMoveAmountREd = pAffineMoveAmountREd;
    affineRotateLeftButton = pAffineRotateLeftButton;
    affineRotateRightButton = pAffineRotateRightButton;
    affineEnlargeButton = pAffineEnlargeButton;
    affineShrinkButton = pAffineShrinkButton;
    affineMoveUpButton = pAffineMoveUpButton;
    affineMoveLeftButton = pAffineMoveLeftButton;
    affineMoveRightButton = pAffineMoveRightButton;
    affineMoveDownButton = pAffineMoveDownButton;
    addTransformationButton = pAddTransformationButton;
    duplicateTransformationButton = pDuplicateTransformationButton;
    deleteTransformationButton = pDeleteTransformationButton;
    addFinalTransformationButton = pAddFinalTransformationButton;
    affineEditPostTransformButton = pAffineEditPostTransformButton;
    affineEditPostTransformSmallButton = pAffineEditPostTransformSmallButton;
    mouseEditZoomInButton = pMouseEditZoomInButton;
    mouseEditZoomOutButton = pMouseEditZoomOutButton;

    randomBatchPanel = pRandomBatchPanel;
    nonlinearControlsRows = pNonlinearControlsRows;

    xFormColorREd = pXFormColorREd;
    xFormColorSlider = pXFormColorSlider;
    xFormSymmetryREd = pXFormSymmetryREd;
    xFormSymmetrySlider = pXFormSymmetrySlider;
    xFormOpacityREd = pXFormOpacityREd;
    xFormOpacitySlider = pXFormOpacitySlider;
    xFormDrawModeCmb = pXFormDrawModeCmb;

    relWeightsTable = pRelWeightsTable;
    relWeightsLeftButton = pRelWeightsLeftButton;
    relWeightsRightButton = pRelWeightsRightButton;

    transformationWeightLeftButton = pTransformationWeightLeftButton;
    transformationWeightRightButton = pTransformationWeightRightButton;
    zStyleCmb = pZStyleCmb;

    setMorphFlame1Button = pSetMorphFlame1Button;
    setMorphFlame2Button = pSetMorphFlame2Button;
    morphFrameREd = pMorphFrameREd;
    morphFramesREd = pMorphFramesREd;
    morphCheckBox = pMorphCheckBox;
    morphFrameSlider = pMorphFrameSlider;
    importMorphedFlameButton = pImportMorphedFlameButton;

    animateOutputREd = pAnimateOutputREd;
    animateFramesREd = pAnimateFramesREd;
    animateGlobalScriptCmb = pAnimateGlobalScriptCmb;
    animateXFormScriptCmb = pAnimateXFormScriptCmb;
    animationGenerateButton = pAnimationGenerateButton;

    mouseTransformMoveButton = pMouseTransformMoveButton;
    mouseTransformRotateButton = pMouseTransformRotateButton;
    mouseTransformScaleButton = pMouseTransformScaleButton;
    toggleTrianglesButton = pToggleTrianglesButton;
    progressUpdater = pProgressUpdater;
    randomPostTransformCheckBox = pRandomPostTransformCheckBox;
    randomSymmetryCheckBox = pRandomSymmetryCheckBox;
    affineResetTransformButton = pAffineResetTransformButton;

    createPaletteColorsTable = pCreatePaletteColorsTable;
    refreshPaletteColorsTable();

    enableControls();
    enableXFormControls(null);
  }

  private FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      int width = centerPanel.getWidth();
      int height = centerPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(img, 0, 0, centerPanel.getWidth(), this);
      flamePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
          flamePanel_mouseDragged(e);
        }
      });
      flamePanel.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          flamePanel_mousePressed(e);
        }

        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
          flamePanel_mouseClicked(e);
        }
      });
      flamePanel.setSelectedXForm(getCurrXForm());
      centerPanel.remove(0);
      centerPanel.add(flamePanel, BorderLayout.CENTER);
      centerPanel.getParent().validate();
      centerPanel.repaint();
    }
    return flamePanel;
  }

  private ImagePanel getPalettePanel() {
    if (palettePanel == null) {
      int width = paletteImgPanel.getWidth();
      int height = paletteImgPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      palettePanel = new ImagePanel(img, 0, 0, paletteImgPanel.getWidth());
      paletteImgPanel.add(palettePanel, BorderLayout.CENTER);
      paletteImgPanel.getParent().validate();
    }
    return palettePanel;
  }

  public void refreshFlameImage() {
    refreshFlameImage(Integer.parseInt(previewQualityREd.getText()), (AffineZStyle) zStyleCmb.getSelectedItem());
  }

  private Flame lastMorphedFlame = null;
  private int lastMorphedFrame = -1;

  private Flame getCurrFlame() {
    if (morphFlame1 != null && morphFlame2 != null && morphCheckBox.isSelected()) {
      int frame = Integer.parseInt(morphFrameREd.getText());
      if (frame != lastMorphedFrame || lastMorphedFlame == null) {
        lastMorphedFrame = frame;
        lastMorphedFlame = FlameMorphService.morphFlames(morphFlame1, morphFlame2, frame, Integer.parseInt(morphFramesREd.getText()));
      }
      return lastMorphedFlame;
    }
    else {
      return _currFlame;
    }
  }

  public void refreshFlameImage(int pQuality, AffineZStyle pAffineZStyle) {
    FlamePanel imgPanel = getFlamePanel();
    int width = imgPanel.getWidth();
    int height = imgPanel.getHeight();
    if (width >= 16 && height >= 16) {
      SimpleImage img = new SimpleImage(width, height);
      Flame flame = getCurrFlame();
      if (flame != null) {
        double oldFilterRadius = flame.getSpatialFilterRadius();
        try {
          double wScl = (double) img.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) img.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(img.getImageWidth());
          flame.setHeight(img.getImageHeight());
          flame.setSampleDensity(pQuality);
          FlameRenderer renderer = new FlameRenderer();
          if (pQuality >= 100) {
            renderer.setProgressUpdater(progressUpdater);
          }
          renderer.setAffineZStyle(pAffineZStyle);
          renderer.renderFlame(flame, img);
        }
        finally {
          flame.setSpatialFilterRadius(oldFilterRadius);
        }
      }
      imgPanel.setImage(img);
    }
    centerPanel.repaint();
  }

  private void refreshUI() {
    noRefresh = true;
    try {
      Flame currFlame = getCurrFlame();
      cameraRollREd.setText(Tools.doubleToString(currFlame.getCamRoll()));
      cameraRollSlider.setValue(Tools.FTOI(currFlame.getCamRoll()));

      cameraPitchREd.setText(Tools.doubleToString(currFlame.getCamPitch()));
      cameraPitchSlider.setValue(Tools.FTOI(currFlame.getCamPitch()));

      cameraYawREd.setText(Tools.doubleToString(currFlame.getCamYaw()));
      cameraYawSlider.setValue(Tools.FTOI(currFlame.getCamYaw()));

      cameraPerspectiveREd.setText(Tools.doubleToString(currFlame.getCamPerspective()));
      cameraPerspectiveSlider.setValue(Tools.FTOI(currFlame.getCamPerspective() * SLIDER_SCALE_PERSPECTIVE));

      cameraCentreXREd.setText(Tools.doubleToString(currFlame.getCentreX()));
      cameraCentreXSlider.setValue(Tools.FTOI(currFlame.getCentreX() * SLIDER_SCALE_CENTRE));

      cameraCentreYREd.setText(Tools.doubleToString(currFlame.getCentreY()));
      cameraCentreYSlider.setValue(Tools.FTOI(currFlame.getCentreY() * SLIDER_SCALE_CENTRE));

      cameraZoomREd.setText(Tools.doubleToString(currFlame.getCamZoom()));
      cameraZoomSlider.setValue(Tools.FTOI(currFlame.getCamZoom() * SLIDER_SCALE_ZOOM));

      cameraZoomREd.setText(Tools.doubleToString(currFlame.getCamZoom()));
      cameraZoomSlider.setValue(Tools.FTOI(currFlame.getCamZoom() * SLIDER_SCALE_ZOOM));

      pixelsPerUnitREd.setText(Tools.doubleToString(currFlame.getPixelsPerUnit()));
      pixelsPerUnitSlider.setValue(Tools.FTOI(currFlame.getPixelsPerUnit()));

      brightnessREd.setText(Tools.doubleToString(currFlame.getBrightness()));
      brightnessSlider.setValue(Tools.FTOI(currFlame.getBrightness() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      contrastREd.setText(Tools.doubleToString(currFlame.getContrast()));
      contrastSlider.setValue(Tools.FTOI(currFlame.getContrast() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      vibrancyREd.setText(Tools.doubleToString(currFlame.getVibrancy()));
      vibrancySlider.setValue(Tools.FTOI(currFlame.getVibrancy() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      gammaREd.setText(Tools.doubleToString(currFlame.getGamma()));
      gammaSlider.setValue(Tools.FTOI(currFlame.getGamma() * SLIDER_SCALE_GAMMA));

      filterRadiusREd.setText(Tools.doubleToString(currFlame.getSpatialFilterRadius()));
      filterRadiusSlider.setValue(Tools.FTOI(currFlame.getSpatialFilterRadius() * SLIDER_SCALE_FILTER_RADIUS));

      spatialOversampleREd.setText(String.valueOf(currFlame.getSpatialOversample()));
      spatialOversampleSlider.setValue(currFlame.getSpatialOversample());

      colorOversampleREd.setText(String.valueOf(currFlame.getColorOversample()));
      colorOversampleSlider.setValue(currFlame.getColorOversample());

      bgColorRedREd.setText(String.valueOf(currFlame.getBGColorRed()));
      bgColorRedSlider.setValue(currFlame.getBGColorRed());

      bgColorGreenREd.setText(String.valueOf(currFlame.getBGColorGreen()));
      bgColorGreenSlider.setValue(currFlame.getBGColorGreen());

      bgColorBlueREd.setText(String.valueOf(currFlame.getBGColorBlue()));
      bgColorBlueSlider.setValue(currFlame.getBGColorBlue());

      refreshTransformationsTable();
      transformationTableClicked();

      refreshFlameImage();

      refreshPaletteUI(currFlame.getPalette());
    }
    finally {
      noRefresh = false;
    }
  }

  private void refreshTransformationsTable() {
    final int COL_TRANSFORM = 0;
    final int COL_VARIATIONS = 1;
    final int COL_WEIGHT = 2;
    transformationsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        Flame currFlame = getCurrFlame();
        return currFlame != null ? currFlame.getXForms().size() + (currFlame.getFinalXForm() != null ? 1 : 0) : 0;
      }

      @Override
      public int getColumnCount() {
        return 3;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_TRANSFORM:
            return "Transform";
          case COL_VARIATIONS:
            return "Variations";
          case COL_WEIGHT:
            return "Weight";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        Flame currFlame = getCurrFlame();
        if (currFlame != null) {
          XForm xForm = rowIndex < currFlame.getXForms().size() ? currFlame.getXForms().get(rowIndex) : currFlame.getFinalXForm();
          switch (columnIndex) {
            case COL_TRANSFORM:
              return rowIndex < currFlame.getXForms().size() ? String.valueOf(rowIndex + 1) : "Final";
            case COL_VARIATIONS:
              {
              String hs = "";
              for (int i = 0; i < xForm.getVariations().size() - 1; i++) {
                hs += xForm.getVariations().get(i).getFunc().getName() + ", ";
              }
              hs += xForm.getVariations().get(xForm.getVariations().size() - 1).getFunc().getName();
              return hs;
            }
            case COL_WEIGHT:
              return rowIndex < currFlame.getXForms().size() ? Tools.doubleToString(xForm.getWeight()) : "";
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return column == COL_WEIGHT;
      }

      @Override
      public void setValueAt(Object aValue, int row, int column) {
        Flame currFlame = getCurrFlame();
        if (currFlame != null && column == COL_WEIGHT && row < currFlame.getXForms().size()) {
          XForm xForm = currFlame.getXForms().get(row);
          String valStr = (String) aValue;
          if (valStr == null || valStr.length() == 0) {
            valStr = "0";
          }
          xForm.setWeight(Tools.stringToDouble(valStr));
          refreshFlameImage();
        }
        super.setValueAt(aValue, row, column);
      }

    });
    transformationsTable.getTableHeader().setFont(transformationsTable.getFont());
    transformationsTable.getColumnModel().getColumn(COL_TRANSFORM).setWidth(20);
    transformationsTable.getColumnModel().getColumn(COL_VARIATIONS).setPreferredWidth(120);
    transformationsTable.getColumnModel().getColumn(COL_WEIGHT).setWidth(16);
  }

  class PaletteTableCustomRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (paletteKeyFrames != null && paletteKeyFrames.size() > row) {
        RGBColor color = paletteKeyFrames.get(row);
        c.setBackground(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue()));
      }
      return c;
    }
  }

  private void refreshPaletteColorsTable() {
    final int COL_COLOR = 0;
    final int COL_RED = 1;
    final int COL_GREEN = 2;
    final int COL_BLUE = 3;

    createPaletteColorsTable.setDefaultRenderer(Object.class, new PaletteTableCustomRenderer());

    createPaletteColorsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return Integer.parseInt(paletteRandomPointsREd.getText());
      }

      @Override
      public int getColumnCount() {
        return 4;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_COLOR:
            return "Color";
          case COL_RED:
            return "Red";
          case COL_GREEN:
            return "Green";
          case COL_BLUE:
            return "Blue";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        if (paletteKeyFrames != null && paletteKeyFrames.size() > rowIndex) {
          switch (columnIndex) {
            case COL_COLOR:
              return String.valueOf(rowIndex + 1);
            case COL_RED:
              return String.valueOf(paletteKeyFrames.get(rowIndex).getRed());
            case COL_GREEN:
              return String.valueOf(paletteKeyFrames.get(rowIndex).getGreen());
            case COL_BLUE:
              return String.valueOf(paletteKeyFrames.get(rowIndex).getBlue());
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return column == COL_RED || column == COL_GREEN || column == COL_BLUE;
      }

      @Override
      public void setValueAt(Object aValue, int row, int column) {
        Flame currFlame = getCurrFlame();
        if (currFlame != null && paletteKeyFrames != null && paletteKeyFrames.size() > row) {
          String valStr = (String) aValue;
          if (valStr == null || valStr.length() == 0) {
            valStr = "0";
          }
          switch (column) {
            case COL_RED:
              paletteKeyFrames.get(row).setRed(Tools.limitColor(Tools.stringToInt(valStr)));
              break;
            case COL_GREEN:
              paletteKeyFrames.get(row).setGreen(Tools.limitColor(Tools.stringToInt(valStr)));
              break;
            case COL_BLUE:
              paletteKeyFrames.get(row).setBlue(Tools.limitColor(Tools.stringToInt(valStr)));
              break;
          }
          refreshPaletteColorsTable();
          RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(paletteKeyFrames);
          currFlame.setPalette(palette);
          refreshPaletteUI(palette);
          refreshFlameImage();
        }
        super.setValueAt(aValue, row, column);
      }

    });
    createPaletteColorsTable.getTableHeader().setFont(transformationsTable.getFont());
    createPaletteColorsTable.getColumnModel().getColumn(COL_COLOR).setWidth(20);
    createPaletteColorsTable.getColumnModel().getColumn(COL_RED).setPreferredWidth(60);
    createPaletteColorsTable.getColumnModel().getColumn(COL_GREEN).setPreferredWidth(60);
    createPaletteColorsTable.getColumnModel().getColumn(COL_BLUE).setPreferredWidth(60);
  }

  private void refreshRelWeightsTable() {
    final int COL_TRANSFORM = 0;
    final int COL_WEIGHT = 1;
    relWeightsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        Flame currFlame = getCurrFlame();
        XForm xForm = getCurrXForm();
        return xForm != null && xForm != currFlame.getFinalXForm() ? currFlame.getXForms().size() : 0;
      }

      @Override
      public int getColumnCount() {
        return 2;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_TRANSFORM:
            return "Transform";
          case COL_WEIGHT:
            return "Weight";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        Flame currFlame = getCurrFlame();
        if (currFlame != null) {
          switch (columnIndex) {
            case COL_TRANSFORM:
              return String.valueOf(rowIndex + 1);
            case COL_WEIGHT: {
              XForm xForm = getCurrXForm();
              return xForm != null ? Tools.doubleToString(xForm.getModifiedWeights()[rowIndex]) : null;
            }
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return column == COL_WEIGHT;
      }

      @Override
      public void setValueAt(Object aValue, int row, int column) {
        XForm xForm = getCurrXForm();
        Flame currFlame = getCurrFlame();
        if (currFlame != null && column == COL_WEIGHT && xForm != null) {
          String valStr = (String) aValue;
          if (valStr == null || valStr.length() == 0) {
            valStr = "0";
          }
          xForm.getModifiedWeights()[row] = Tools.stringToDouble(valStr);
          refreshFlameImage();
        }
        super.setValueAt(aValue, row, column);
      }

    });
    relWeightsTable.getTableHeader().setFont(relWeightsTable.getFont());
    relWeightsTable.getColumnModel().getColumn(COL_TRANSFORM).setWidth(20);
    relWeightsTable.getColumnModel().getColumn(COL_WEIGHT).setWidth(16);
  }

  private void refreshPaletteUI(RGBPalette pPalette) {
    paletteRedREd.setText(String.valueOf(pPalette.getModRed()));
    paletteRedSlider.setValue(pPalette.getModRed());
    paletteGreenREd.setText(String.valueOf(pPalette.getModGreen()));
    paletteGreenSlider.setValue(pPalette.getModGreen());
    paletteBlueREd.setText(String.valueOf(pPalette.getModBlue()));
    paletteBlueSlider.setValue(pPalette.getModBlue());
    paletteContrastREd.setText(String.valueOf(pPalette.getModContrast()));
    paletteContrastSlider.setValue(pPalette.getModContrast());
    paletteHueREd.setText(String.valueOf(pPalette.getModHue()));
    paletteHueSlider.setValue(pPalette.getModHue());
    paletteBrightnessREd.setText(String.valueOf(pPalette.getModBrightness()));
    paletteBrightnessSlider.setValue(pPalette.getModBrightness());
    paletteGammaREd.setText(String.valueOf(pPalette.getModGamma()));
    paletteGammaSlider.setValue(pPalette.getModGamma());
    paletteShiftREd.setText(String.valueOf(pPalette.getModShift()));
    paletteShiftSlider.setValue(pPalette.getModShift());
    paletteSaturationREd.setText(String.valueOf(pPalette.getModSaturation()));
    paletteSaturationSlider.setValue(pPalette.getModSaturation());
    refreshPaletteImg();
  }

  private void refreshPaletteImg() {
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      ImagePanel imgPanel = getPalettePanel();
      int width = imgPanel.getWidth();
      int height = imgPanel.getHeight();
      if (width >= 16 && height >= 16) {
        SimpleImage img = new RGBPaletteRenderer().renderHorizPalette(currFlame.getPalette(), width, height);
        imgPanel.setImage(img);
      }
      palettePanel.getParent().validate();
    }
  }

  private void flameSliderChanged(JSlider pSlider, JTextField pTextField, String pProperty, double pSliderScale) {
    Flame currFlame = getCurrFlame();
    if (noRefresh || currFlame == null)
      return;
    noRefresh = true;
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = currFlame.getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(currFlame, propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(currFlame, Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshFlameImage();
    }
    finally {
      noRefresh = false;
    }
  }

  private void paletteSliderChanged(JSlider pSlider, JTextField pTextField, String pProperty, double pSliderScale) {
    Flame currFlame = getCurrFlame();
    if (noRefresh || currFlame == null)
      return;
    noRefresh = true;
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = currFlame.getPalette().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(currFlame.getPalette(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(currFlame.getPalette(), Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
        field = cls.getDeclaredField("modified");
        field.setAccessible(true);
        field.setBoolean(currFlame.getPalette(), true);
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshPaletteImg();
      refreshFlameImage();
    }
    finally {
      noRefresh = false;
    }
  }

  private void xFormSliderChanged(JSlider pSlider, JTextField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh) {
      return;
    }
    Flame currFlame = getCurrFlame();
    if (currFlame == null) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm == null) {
      return;
    }
    noRefresh = true;
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = xForm.getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(xForm, propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(xForm, Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshFlameImage();
    }
    finally {
      noRefresh = false;
    }
  }

  private void flameTextFieldChanged(JSlider pSlider, JTextField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh) {
      return;
    }
    Flame currFlame = getCurrFlame();
    if (currFlame == null) {
      return;
    }
    noRefresh = true;
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = currFlame.getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(currFlame, propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(currFlame, Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshFlameImage();
    }
    finally {
      noRefresh = false;
    }
  }

  private void paletteTextFieldChanged(JSlider pSlider, JTextField pTextField, String pProperty, double pSliderScale) {
    Flame currFlame = getCurrFlame();
    if (noRefresh || currFlame == null)
      return;
    noRefresh = true;
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = currFlame.getPalette().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(currFlame.getPalette(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(currFlame.getPalette(), Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
        field = cls.getDeclaredField("modified");
        field.setAccessible(true);
        field.setBoolean(currFlame.getPalette(), true);
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshPaletteImg();
      refreshFlameImage();
    }
    finally {
      noRefresh = false;
    }
  }

  private void xFormTextFieldChanged(JSlider pSlider, JTextField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm == null) {
      return;
    }
    noRefresh = true;
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = xForm.getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(xForm, propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(xForm, Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshFlameImage();
    }
    finally {
      noRefresh = false;
    }
  }

  public void cameraRollSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraRollSlider, cameraRollREd, "camRoll", 1.0);
  }

  public void cameraRollREd_changed() {
    flameTextFieldChanged(cameraRollSlider, cameraRollREd, "camRoll", 1.0);
  }

  public void cameraPitchREd_changed() {
    flameTextFieldChanged(cameraPitchSlider, cameraPitchREd, "camPitch", 1.0);
  }

  public void cameraPitchSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraPitchSlider, cameraPitchREd, "camPitch", 1.0);
  }

  public void cameraYawREd_changed() {
    flameTextFieldChanged(cameraYawSlider, cameraYawREd, "camYaw", 1.0);
  }

  public void cameraPerspectiveREd_changed() {
    flameTextFieldChanged(cameraPerspectiveSlider, cameraPerspectiveREd, "camPerspective", SLIDER_SCALE_PERSPECTIVE);
  }

  public void cameraYawSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraYawSlider, cameraYawREd, "camYaw", 1.0);
  }

  public void cameraPerspectiveSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraPerspectiveSlider, cameraPerspectiveREd, "camPerspective", SLIDER_SCALE_PERSPECTIVE);
  }

  public void renderFlameButton_actionPerformed(ActionEvent e) {
    refreshing = true;
    try {
      toggleTrianglesButton.setSelected(false);
      flamePanel.setDrawFlame(false);
    }
    finally {
      refreshing = false;
    }
    refreshFlameImage(Integer.parseInt(renderQualityREd.getText()), (AffineZStyle) zStyleCmb.getSelectedItem());
  }

  public class FlameFileFilter extends FileFilter {

    @Override
    public boolean accept(File pFile) {
      if (pFile.isDirectory())
        return true;
      String extension = getExtension(pFile);
      return (extension != null)
          && (extension.equals("flame") || extension.equals("xml"));
    }

    @Override
    public String getDescription() {
      return "Supported flame files";
    }

    private String getExtension(File pFile) {
      String name = pFile.getName();
      int idx = name.lastIndexOf('.');
      if (idx > 0 && idx < name.length() - 1) {
        return name.substring(idx + 1).toLowerCase();
      }
      return null;
    }

  }

  private JFileChooser getFlameJFileChooser() {
    JFileChooser fileChooser = new DefaultFileChooser() {

      private static final long serialVersionUID = 1L;

      @Override
      protected String getDefaultExtension() {
        return Tools.FILEEXT_FLAME;
      }

    };
    fileChooser.addChoosableFileFilter(new FlameFileFilter());
    fileChooser.setAcceptAllFileFilterUsed(false);
    return fileChooser;
  }

  public void loadFlameButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = getFlameJFileChooser();
    try {
      chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      List<Flame> flames = new Flam3Reader().readFlames(file.getAbsolutePath());
      Flame flame = flames.get(0);
      prefs.setLastInputFlameFile(file);
      _currFlame = flame;
      refreshUI();
    }
  }

  public void previewQualityREd_changed() {
    if (noRefresh)
      return;
    refreshFlameImage();
  }

  public void cameraCentreYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraCentreYSlider, cameraCentreYREd, "centreY", SLIDER_SCALE_CENTRE);
  }

  public void cameraCentreYREd_changed() {
    flameTextFieldChanged(cameraCentreYSlider, cameraCentreYREd, "centreY", SLIDER_SCALE_CENTRE);
  }

  public void cameraCentreXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraCentreXSlider, cameraCentreXREd, "centreX", SLIDER_SCALE_CENTRE);
  }

  public void cameraZoomSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraZoomSlider, cameraZoomREd, "camZoom", SLIDER_SCALE_ZOOM);
  }

  public void cameraCentreXREd_changed() {
    flameTextFieldChanged(cameraCentreXSlider, cameraCentreXREd, "centreX", SLIDER_SCALE_CENTRE);
  }

  public void cameraZoomREd_changed() {
    flameTextFieldChanged(cameraZoomSlider, cameraZoomREd, "camZoom", SLIDER_SCALE_ZOOM);
  }

  public void brightnessSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(brightnessSlider, brightnessREd, "brightness", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void filterRadiusREd_changed() {
    flameTextFieldChanged(filterRadiusSlider, filterRadiusREd, "spatialFilterRadius", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void bgColorGreenSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(bgColorGreenSlider, bgColorGreenREd, "bgColorGreen", 1.0);
  }

  public void gammaREd_changed() {
    flameTextFieldChanged(gammaSlider, gammaREd, "gamma", SLIDER_SCALE_GAMMA);
  }

  public void bgColorRedSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(bgColorRedSlider, bgColorRedREd, "bgColorRed", 1.0);
  }

  public void bgColorBlueSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(bgColorBlueSlider, bgColorBlueREd, "bgColorBlue", 1.0);
  }

  public void spatialOversampleREd_changed() {
    flameTextFieldChanged(spatialOversampleSlider, spatialOversampleREd, "spatialOversample", 1.0);
  }

  public void colorOversampleREd_changed() {
    flameTextFieldChanged(colorOversampleSlider, colorOversampleREd, "colorOversample", 1.0);
  }

  public void contrastREd_changed() {
    flameTextFieldChanged(contrastSlider, contrastREd, "contrast", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void vibrancySlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(vibrancySlider, vibrancyREd, "vibrancy", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void filterRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(filterRadiusSlider, filterRadiusREd, "spatialFilterRadius", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void spatialOversampleSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(spatialOversampleSlider, spatialOversampleREd, "spatialOversample", 1.0);
  }

  public void colorOversampleSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(colorOversampleSlider, colorOversampleREd, "colorOversample", 1.0);
  }

  public void vibrancyREd_changed() {
    flameTextFieldChanged(vibrancySlider, vibrancyREd, "vibrancy", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void pixelsPerUnitSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(pixelsPerUnitSlider, pixelsPerUnitREd, "pixelsPerUnit", 1.0);
  }

  public void bgColorGreenREd_changed() {
    flameTextFieldChanged(bgColorGreenSlider, bgColorGreenREd, "bgColorGreen", 1.0);
  }

  public void gammaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(gammaSlider, gammaREd, "gamma", SLIDER_SCALE_GAMMA);
  }

  public void bgColorRedREd_changed() {
    flameTextFieldChanged(bgColorRedSlider, bgColorRedREd, "bgColorRed", 1.0);
  }

  public void bgBGColorBlueREd_changed() {
    flameTextFieldChanged(bgColorBlueSlider, bgColorBlueREd, "bgColorBlue", 1.0);
  }

  public void pixelsPerUnitREd_changed() {
    flameTextFieldChanged(pixelsPerUnitSlider, pixelsPerUnitREd, "pixelsPerUnit", 1.0);
  }

  public void brightnessREd_changed() {
    flameTextFieldChanged(brightnessSlider, brightnessREd, "brightness", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void contrastSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(contrastSlider, contrastREd, "contrast", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void randomPaletteButton_actionPerformed(ActionEvent e) {
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      RandomRGBPaletteGenerator generator = new RandomRGBPaletteGenerator();
      paletteKeyFrames = generator.generateKeyFrames(Integer.parseInt(paletteRandomPointsREd.getText()));
      refreshPaletteColorsTable();
      RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(paletteKeyFrames);
      currFlame.setPalette(palette);
      refreshPaletteUI(palette);
      refreshFlameImage();
    }
  }

  private JFileChooser getImageJFileChooser() {
    JFileChooser fileChooser = new DefaultFileChooser() {
      private static final long serialVersionUID = 1L;

      @Override
      protected String getDefaultExtension() {
        return Tools.FILEEXT_PNG;
      }

    };
    fileChooser.addChoosableFileFilter(new ImageFileFilter());
    fileChooser.setAcceptAllFileFilterUsed(false);
    return fileChooser;
  }

  public void grabPaletteFromImageButton_actionPerformed(ActionEvent e) {
    try {
      JFileChooser chooser = getImageJFileChooser();
      chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastInputImageFile(file);
        SimpleImage img = new ImageReader(centerPanel).loadImage(file.getAbsolutePath());
        RGBPalette palette = new RGBPaletteImporter().importFromImage(img);
        Flame currFlame = getCurrFlame();
        paletteKeyFrames = null;
        refreshPaletteColorsTable();
        currFlame.setPalette(palette);
        refreshPaletteUI(palette);
        refreshFlameImage();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }

  }

  public void paletteRedREd_changed() {
    paletteTextFieldChanged(paletteRedSlider, paletteRedREd, "modRed", 1.0);
  }

  public void paletteSaturationREd_changed() {
    paletteTextFieldChanged(paletteSaturationSlider, paletteSaturationREd, "modSaturation", 1.0);
  }

  public void paletteBlueREd_changed() {
    paletteTextFieldChanged(paletteBlueSlider, paletteBlueREd, "modBlue", 1.0);
  }

  public void paletteBlueSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteBlueSlider, paletteBlueREd, "modBlue", 1.0);
  }

  public void paletteBrightnessSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteBrightnessSlider, paletteBrightnessREd, "modBrightness", 1.0);
  }

  public void paletteBrightnessREd_changed() {
    paletteTextFieldChanged(paletteBrightnessSlider, paletteBrightnessREd, "modBrightness", 1.0);
  }

  public void paletteContrastREd_changed() {
    paletteTextFieldChanged(paletteContrastSlider, paletteContrastREd, "modContrast", 1.0);
  }

  public void paletteGreenREd_changed() {
    paletteTextFieldChanged(paletteGreenSlider, paletteGreenREd, "modGreen", 1.0);
  }

  public void paletteGreenSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteGreenSlider, paletteGreenREd, "modGreen", 1.0);
  }

  public void paletteHueREd_changed() {
    paletteTextFieldChanged(paletteHueSlider, paletteHueREd, "modHue", 1.0);
  }

  public void paletteGammaSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteGammaSlider, paletteGammaREd, "modGamma", 1.0);
  }

  public void paletteGammaREd_changed() {
    paletteTextFieldChanged(paletteGammaSlider, paletteGammaREd, "modGamma", 1.0);
  }

  public void paletteRedSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteRedSlider, paletteRedREd, "modRed", 1.0);
  }

  public void paletteContrastSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteContrastSlider, paletteContrastREd, "modContrast", 1.0);
  }

  public void paletteSaturationSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteSaturationSlider, paletteSaturationREd, "modSaturation", 1.0);
  }

  public void paletteShiftREd_changed() {
    paletteTextFieldChanged(paletteShiftSlider, paletteShiftREd, "modShift", 1.0);
  }

  public void paletteHueSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteHueSlider, paletteHueREd, "modHue", 1.0);
  }

  public void paletteShiftSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteShiftSlider, paletteShiftREd, "modShift", 1.0);
  }

  public void exportImageButton_actionPerformed(ActionEvent e) {
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      try {
        JFileChooser chooser = getImageJFileChooser();
        try {
          chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          prefs.setLastOutputImageFile(file);
          int width = Integer.parseInt(renderWidthREd.getText());
          int height = Integer.parseInt(renderHeightREd.getText());
          int quality = Integer.parseInt(renderQualityREd.getText());
          SimpleImage img = new SimpleImage(width, height);
          Flame flame = getCurrFlame();
          double wScl = (double) img.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) img.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(img.getImageWidth());
          flame.setHeight(img.getImageHeight());
          flame.setSampleDensity(quality);
          FlameRenderer renderer = new FlameRenderer();
          renderer.setProgressUpdater(progressUpdater);
          renderer.setAffineZStyle((AffineZStyle) zStyleCmb.getSelectedItem());
          renderer.renderFlame(flame, img);
          new ImageWriter().saveImage(img, file.getAbsolutePath());
          mainController.loadImage(file.getAbsolutePath(), false);
          //          JOptionPane.showMessageDialog(centerPanel, "Image was successfully saved", "Operation successful", JOptionPane.OK_OPTION);
        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void saveFlameButton_actionPerformed(ActionEvent e) {
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      JFileChooser chooser = getFlameJFileChooser();
      try {
        chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        try {
          File file = chooser.getSelectedFile();
          new Flam3Writer().writeFlame(currFlame, file.getAbsolutePath());
          prefs.setLastOutputFlameFile(file);
        }
        catch (Throwable ex) {
          errorHandler.handleError(ex);
        }
      }
    }
  }

  public XForm getCurrXForm() {
    XForm xForm = null;
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      int row = transformationsTable.getSelectedRow();
      if (row >= 0 && row < currFlame.getXForms().size()) {
        xForm = currFlame.getXForms().get(row);
      }
      else if (row == currFlame.getXForms().size()) {
        xForm = currFlame.getFinalXForm();
      }
    }
    return xForm;
  }

  public void transformationTableClicked() {
    if (!gridRefreshing) {
      cmbRefreshing = true;
      try {
        XForm xForm = getCurrXForm();
        if (flamePanel != null) {
          flamePanel.setSelectedXForm(xForm);
        }
        refreshXFormUI(xForm);
        enableXFormControls(xForm);
        refreshFlameImage();
      }
      finally {
        cmbRefreshing = false;
      }
    }
  }

  private void enableControls() {
    setMorphFlame1Button.setEnabled(true);
    setMorphFlame2Button.setEnabled(true);
    importMorphedFlameButton.setEnabled(true);
    animationGenerateButton.setEnabled(true);
    morphCheckBox.setEnabled(morphFlame1 != null && morphFlame2 != null);
  }

  private void enableXFormControls(XForm xForm) {
    Flame currFlame = getCurrFlame();
    boolean enabled = xForm != null;
    affineRotateLeftButton.setEnabled(enabled);
    affineRotateRightButton.setEnabled(enabled);
    affineEnlargeButton.setEnabled(enabled);
    affineShrinkButton.setEnabled(enabled);
    affineMoveUpButton.setEnabled(enabled);
    affineMoveLeftButton.setEnabled(enabled);
    affineMoveRightButton.setEnabled(enabled);
    affineMoveDownButton.setEnabled(enabled);
    addTransformationButton.setEnabled(currFlame != null);
    duplicateTransformationButton.setEnabled(enabled);
    deleteTransformationButton.setEnabled(enabled);
    addFinalTransformationButton.setEnabled(currFlame != null && currFlame.getFinalXForm() == null);

    affineEditPostTransformButton.setEnabled(currFlame != null);
    affineEditPostTransformSmallButton.setEnabled(currFlame != null);
    mouseEditZoomInButton.setEnabled(currFlame != null);
    mouseEditZoomOutButton.setEnabled(currFlame != null);
    toggleTrianglesButton.setEnabled(currFlame != null);

    affineC20REd.setEditable(enabled);
    affineC21REd.setEditable(enabled);
    affineResetTransformButton.setEnabled(enabled);

    transformationWeightLeftButton.setEnabled(enabled);
    transformationWeightRightButton.setEnabled(enabled);
    for (NonlinearControlsRow rows : nonlinearControlsRows) {
      rows.getNonlinearVarCmb().setEnabled(enabled);
      rows.getNonlinearVarREd().setEnabled(enabled);
      rows.getNonlinearVarLeftButton().setEnabled(enabled);
      rows.getNonlinearVarRightButton().setEnabled(enabled);
      rows.getNonlinearParamsCmb().setEnabled(enabled);
      rows.getNonlinearParamsREd().setEnabled(enabled);
      // refreshed in refreshXFormUI():
      // rows.getNonlinearParamsLeftButton().setEnabled(enabled);
      // rows.getNonlinearParamsRightButton().setEnabled(enabled);
    }
    xFormColorREd.setEnabled(enabled);
    xFormColorSlider.setEnabled(enabled);
    xFormSymmetryREd.setEnabled(enabled);
    xFormSymmetrySlider.setEnabled(enabled);
    xFormOpacityREd.setEnabled(enabled && xForm.getDrawMode() == DrawMode.OPAQUE);
    xFormOpacitySlider.setEnabled(xFormOpacityREd.isEnabled());
    xFormDrawModeCmb.setEnabled(enabled);

    relWeightsTable.setEnabled(enabled);
    relWeightsLeftButton.setEnabled(enabled);
    relWeightsRightButton.setEnabled(enabled);
  }

  private void refreshXFormUI(XForm pXForm) {
    if (pXForm != null) {
      if (affineEditPostTransformButton.isSelected()) {
        affineC00REd.setText(Tools.doubleToString(pXForm.getPostCoeff00()));
        affineC01REd.setText(Tools.doubleToString(pXForm.getPostCoeff01()));
        affineC10REd.setText(Tools.doubleToString(pXForm.getPostCoeff10()));
        affineC11REd.setText(Tools.doubleToString(pXForm.getPostCoeff11()));
        affineC20REd.setText(Tools.doubleToString(pXForm.getPostCoeff20()));
        affineC21REd.setText(Tools.doubleToString(pXForm.getPostCoeff21()));
      }
      else {
        affineC00REd.setText(Tools.doubleToString(pXForm.getCoeff00()));
        affineC01REd.setText(Tools.doubleToString(pXForm.getCoeff01()));
        affineC10REd.setText(Tools.doubleToString(pXForm.getCoeff10()));
        affineC11REd.setText(Tools.doubleToString(pXForm.getCoeff11()));
        affineC20REd.setText(Tools.doubleToString(pXForm.getCoeff20()));
        affineC21REd.setText(Tools.doubleToString(pXForm.getCoeff21()));
      }
      xFormColorREd.setText(Tools.doubleToString(pXForm.getColor()));
      xFormColorSlider.setValue(Tools.FTOI(pXForm.getColor() * SLIDER_SCALE_COLOR));
      xFormSymmetryREd.setText(Tools.doubleToString(pXForm.getColorSymmetry()));
      xFormSymmetrySlider.setValue(Tools.FTOI(pXForm.getColorSymmetry() * SLIDER_SCALE_COLOR));
      xFormOpacityREd.setText(Tools.doubleToString(pXForm.getOpacity()));
      xFormOpacitySlider.setValue(Tools.FTOI(pXForm.getOpacity() * SLIDER_SCALE_COLOR));
      xFormDrawModeCmb.setSelectedItem(pXForm.getDrawMode());
    }
    else {
      affineC00REd.setText(null);
      affineC01REd.setText(null);
      affineC10REd.setText(null);
      affineC11REd.setText(null);
      affineC20REd.setText(null);
      affineC21REd.setText(null);
      xFormColorREd.setText(null);
      xFormColorSlider.setValue(0);
      xFormSymmetryREd.setText(null);
      xFormSymmetrySlider.setValue(0);
      xFormOpacityREd.setText(null);
      xFormOpacitySlider.setValue(0);
      xFormDrawModeCmb.setSelectedIndex(-1);
    }

    refreshing = true;
    try {
      int idx = 0;
      for (NonlinearControlsRow row : nonlinearControlsRows) {
        if (pXForm == null || idx >= pXForm.getVariations().size()) {
          refreshParamCmb(row, null, null);
          row.getNonlinearParamsLeftButton().setEnabled(false);
          row.getNonlinearParamsRightButton().setEnabled(false);
        }
        else {
          Variation var = pXForm.getVariations().get(idx);
          refreshParamCmb(row, pXForm, var);
          String selected = (String) row.getNonlinearParamsCmb().getSelectedItem();
          boolean enabled = selected != null && selected.length() > 0;
          row.getNonlinearParamsLeftButton().setEnabled(enabled);
          row.getNonlinearParamsRightButton().setEnabled(enabled);
        }
        idx++;
      }
    }
    finally {
      refreshing = false;
    }

    gridRefreshing = true;
    try {
      refreshRelWeightsTable();
    }
    finally {
      gridRefreshing = false;
    }
  }

  public void refreshParamCmb(NonlinearControlsRow pRow, XForm pXForm, Variation pVar) {
    if (pXForm == null || pVar == null) {
      pRow.getNonlinearVarCmb().setSelectedIndex(-1);
      pRow.getNonlinearVarREd().setText(null);
      pRow.getNonlinearParamsCmb().setSelectedIndex(-1);
      pRow.getNonlinearParamsREd().setText(null);
    }
    else {
      VariationFunc varFunc = pVar.getFunc();
      pRow.getNonlinearVarCmb().setSelectedItem(varFunc.getName());
      pRow.getNonlinearVarREd().setText(Tools.doubleToString(pVar.getAmount()));
      pRow.getNonlinearParamsCmb().removeAllItems();
      for (String name : varFunc.getParameterNames()) {
        pRow.getNonlinearParamsCmb().addItem(name);
      }
      if (varFunc.getParameterNames().length > 0) {
        pRow.getNonlinearParamsCmb().setSelectedIndex(0);
        Object val = varFunc.getParameterValues()[0];
        if (val instanceof Double) {
          pRow.getNonlinearParamsREd().setText(Tools.doubleToString((Double) val));
        }
        else {
          pRow.getNonlinearParamsREd().setText(val.toString());
        }
      }
      else {
        pRow.getNonlinearParamsCmb().setSelectedIndex(-1);
        pRow.getNonlinearParamsREd().setText(null);
      }
    }
  }

  public void addXForm() {
    XForm xForm = new XForm();
    xForm.addVariation(0.5, new Linear3DFunc());
    xForm.setWeight(0.5);
    Flame currFlame = getCurrFlame();
    currFlame.getXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = currFlame.getXForms().size() - 1;
    transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage();
  }

  public void duplicateXForm() {
    XForm xForm = new XForm();
    xForm.assign(getCurrXForm());
    Flame currFlame = getCurrFlame();
    currFlame.getXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = currFlame.getXForms().size() - 1;
    transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage();
  }

  public void deleteXForm() {
    int row = transformationsTable.getSelectedRow();
    Flame currFlame = getCurrFlame();
    if (currFlame.getFinalXForm() != null && row == currFlame.getXForms().size()) {
      currFlame.setFinalXForm(null);
    }
    else {
      currFlame.getXForms().remove(getCurrXForm());
    }
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    refreshFlameImage();
  }

  public void addFinalXForm() {
    XForm xForm = new XForm();
    xForm.addVariation(0.5, new Linear3DFunc());
    Flame currFlame = getCurrFlame();
    currFlame.setFinalXForm(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = currFlame.getXForms().size();
    transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage();
  }

  public void xForm_moveRight() {
    double amount = Tools.stringToDouble(affineMoveAmountREd.getText());
    XFormTransformService.globalTranslate(getCurrXForm(), amount, 0);
    transformationTableClicked();
    refreshFlameImage();
  }

  public void xForm_rotateRight() {
    double amount = Tools.stringToDouble(affineRotateAmountREd.getText());
    XFormTransformService.rotate(getCurrXForm(), -amount);
    transformationTableClicked();
    refreshFlameImage();
  }

  public void xForm_moveLeft() {
    double amount = Tools.stringToDouble(affineMoveAmountREd.getText());
    XFormTransformService.globalTranslate(getCurrXForm(), -amount, 0);
    transformationTableClicked();
    refreshFlameImage();
  }

  public void xForm_enlarge() {
    double amount = 1.0 + Tools.stringToDouble(affineScaleAmountREd.getText());
    XFormTransformService.scale(getCurrXForm(), amount);
    transformationTableClicked();
    refreshFlameImage();
  }

  public void xForm_shrink() {
    double amount = 1.0 - Tools.stringToDouble(affineScaleAmountREd.getText());
    XFormTransformService.scale(getCurrXForm(), amount);
    transformationTableClicked();
    refreshFlameImage();
  }

  public void xForm_rotateLeft() {
    double amount = Tools.stringToDouble(affineRotateAmountREd.getText());
    XFormTransformService.rotate(getCurrXForm(), amount);
    transformationTableClicked();
    refreshFlameImage();
  }

  public void xForm_moveUp() {
    double amount = Tools.stringToDouble(affineMoveAmountREd.getText());
    XFormTransformService.globalTranslate(getCurrXForm(), 0, -amount);
    transformationTableClicked();
    refreshFlameImage();
  }

  public void xForm_moveDown() {
    double amount = Tools.stringToDouble(affineMoveAmountREd.getText());
    XFormTransformService.globalTranslate(getCurrXForm(), 0, amount);
    transformationTableClicked();
    refreshFlameImage();
  }

  private List<Flame> randomBatch = new ArrayList<Flame>();

  private final int IMG_WIDTH = 80;
  private final int IMG_HEIGHT = 60;
  private final int BORDER_SIZE = 10;

  public void updateThumbnails(List<SimpleImage> pImages) {
    if (randomBatchScrollPane != null) {
      randomBatchPanel.remove(randomBatchScrollPane);
      randomBatchScrollPane = null;
    }
    int panelWidth = (IMG_WIDTH + BORDER_SIZE) * randomBatch.size();
    int panelHeight = IMG_HEIGHT + 2 * BORDER_SIZE;
    JPanel batchPanel = new JPanel();
    batchPanel.setLayout(null);
    batchPanel.setSize(panelWidth, panelHeight);
    batchPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    for (int i = 0; i < randomBatch.size(); i++) {
      SimpleImage img;
      if (pImages != null) {
        img = pImages.get(i);
      }
      else {
        img = new SimpleImage(IMG_WIDTH, IMG_HEIGHT);
        Flame flame = randomBatch.get(i);
        flame.setSampleDensity(50);
        flame.setWidth(IMG_WIDTH);
        flame.setHeight(IMG_HEIGHT);
        flame.setPixelsPerUnit(10);
        FlameRenderer renderer = new FlameRenderer();
        renderer.renderFlame(flame, img);
      }
      // add it to the main panel
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(i * IMG_WIDTH + (i + 1) * BORDER_SIZE, BORDER_SIZE);
      final int idx = i;
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1) {
            importFromRandomBatch(idx);
          }
        }
      });
      batchPanel.add(imgPanel);
    }
    randomBatchScrollPane = new JScrollPane(batchPanel);
    randomBatchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    randomBatchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

    randomBatchPanel.add(randomBatchScrollPane, BorderLayout.CENTER);
    randomBatchPanel.validate();
  }

  public void createRandomBatch(int pCount, RandomFlameGeneratorStyle pStyle) {
    randomBatch.clear();
    final int IMG_COUNT = 24;
    final int MAX_IMG_SAMPLES = 10;
    final double MIN_COVERAGE = 0.33;
    List<SimpleImage> imgList = new ArrayList<SimpleImage>();
    int maxCount = (pCount > 0 ? pCount : IMG_COUNT);
    progressUpdater.initProgress(maxCount);
    for (int i = 0; i < maxCount; i++) {
      SimpleImage img = new SimpleImage(IMG_WIDTH, IMG_HEIGHT);
      Flame bestFlame = null;
      double bestCoverage = 0.0;
      for (int j = 0; j < MAX_IMG_SAMPLES; j++) {
        // create flame
        Flame flame = new RandomFlameGenerator().createFlame(pStyle, randomSymmetryCheckBox.isSelected(), randomPostTransformCheckBox.isSelected());
        flame.setWidth(IMG_WIDTH);
        flame.setHeight(IMG_HEIGHT);
        flame.setPixelsPerUnit(10);
        RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(Integer.parseInt(paletteRandomPointsREd.getText()));
        flame.setPalette(palette);
        // render it   
        if (j > 0) {
          img.fillBackground(0, 0, 0);
        }
        flame.setSampleDensity(50);
        FlameRenderer renderer = new FlameRenderer();
        renderer.renderFlame(flame, img);
        if (j == MAX_IMG_SAMPLES - 1) {
          randomBatch.add(bestFlame);
          renderer.renderFlame(bestFlame, img);
          imgList.add(img);
        }
        else {
          long maxCoverage = img.getImageWidth() * img.getImageHeight();
          long coverage = 0;
          Pixel pixel = new Pixel();
          for (int k = 0; k < img.getImageHeight(); k++) {
            for (int l = 0; l < img.getImageWidth(); l++) {
              pixel.setARGBValue(img.getARGBValue(l, k));
              if (pixel.r > 20 || pixel.g > 20 || pixel.b > 20) {
                coverage++;
              }
            }
          }
          double fCoverage = (double) coverage / (double) maxCoverage;
          if (fCoverage >= MIN_COVERAGE) {
            randomBatch.add(flame);
            imgList.add(img);
            break;
          }
          else {
            if (bestFlame == null || fCoverage > bestCoverage) {
              bestFlame = flame;
              bestCoverage = fCoverage;
            }
          }
        }
      }
      // add it to the main panel
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(i * IMG_WIDTH + (i + 1) * BORDER_SIZE, BORDER_SIZE);
      final int idx = i;
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1) {
            importFromRandomBatch(idx);
          }
        }
      });
      progressUpdater.updateProgress(i + 1);
    }
    updateThumbnails(imgList);
  }

  public void importFromRandomBatch(int pIdx) {
    if (pIdx >= 0 && pIdx < randomBatch.size()) {
      _currFlame = randomBatch.get(pIdx);
      refreshUI();
    }
  }

  public void nonlinearVarCmbChanged(int pIdx) {
    if (cmbRefreshing) {
      return;
    }
    cmbRefreshing = true;
    try {
      XForm xForm = getCurrXForm();
      if (xForm != null) {
        Variation var;
        if (pIdx < xForm.getVariations().size()) {
          var = xForm.getVariations().get(pIdx);
        }
        else {
          var = new Variation();
          String varStr = nonlinearControlsRows[pIdx].getNonlinearVarREd().getText();
          if (varStr == null || varStr.length() == 0) {
            varStr = "0";
          }
          var.setAmount(Tools.stringToDouble(varStr));
          xForm.getVariations().add(var);
        }
        String fName = (String) nonlinearControlsRows[pIdx].getNonlinearVarCmb().getSelectedItem();
        if (fName == null || fName.length() == 0) {
          var.setFunc(VariationFuncList.getVariationFuncInstance(VariationFuncList.DEFAULT_VARIATION));
        }
        else {
          var.setFunc(VariationFuncList.getVariationFuncInstance(fName));
        }
        refreshParamCmb(nonlinearControlsRows[pIdx], xForm, var);
        refreshXFormUI(xForm);
        refreshFlameImage();
      }
    }
    finally {
      cmbRefreshing = false;
    }
  }

  public void nonlinearVarREdChanged(int pIdx) {
    nonlinearVarREdChanged(pIdx, 0.0);
  }

  public void nonlinearVarREdChanged(int pIdx, double pDelta) {
    if (cmbRefreshing) {
      return;
    }
    cmbRefreshing = true;
    try {
      XForm xForm = getCurrXForm();
      if (xForm != null) {
        if (pIdx < xForm.getVariations().size()) {
          Variation var = xForm.getVariations().get(pIdx);
          String varStr = nonlinearControlsRows[pIdx].getNonlinearVarREd().getText();
          if (varStr == null || varStr.length() == 0) {
            varStr = "0";
          }
          var.setAmount(Tools.stringToDouble(varStr) + pDelta);
          nonlinearControlsRows[pIdx].getNonlinearVarREd().setText(Tools.doubleToString(var.getAmount()));
          refreshFlameImage();
        }
      }
    }
    finally {
      cmbRefreshing = false;
    }
  }

  public void nonlinearParamsREdChanged(int pIdx) {
    nonlinearParamsREdChanged(pIdx, 0.0);
  }

  public void nonlinearParamsREdChanged(int pIdx, double pDelta) {
    if (cmbRefreshing) {
      return;
    }
    cmbRefreshing = true;
    try {
      String selected = (String) nonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
      XForm xForm = getCurrXForm();
      if (xForm != null && selected != null && selected.length() > 0) {
        if (pIdx < xForm.getVariations().size()) {
          Variation var = xForm.getVariations().get(pIdx);
          String valStr = nonlinearControlsRows[pIdx].getNonlinearParamsREd().getText();
          if (valStr == null || valStr.length() == 0) {
            valStr = "0";
          }
          double val = Tools.stringToDouble(valStr) + pDelta;
          var.getFunc().setParameter(selected, val);
          nonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(Tools.doubleToString(val));
          refreshFlameImage();
        }
      }
    }
    finally {
      cmbRefreshing = false;
    }
  }

  public void nonlinearParamsCmbChanged(int pIdx) {
    if (cmbRefreshing) {
      return;
    }
    cmbRefreshing = true;
    try {
      String selected = (String) nonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
      XForm xForm = getCurrXForm();
      if (xForm != null && selected != null && selected.length() > 0) {
        if (pIdx < xForm.getVariations().size()) {
          Variation var = xForm.getVariations().get(pIdx);
          int idx = -1;
          for (int i = 0; i < var.getFunc().getParameterNames().length; i++) {
            if (var.getFunc().getParameterNames()[i].equals(selected)) {
              idx = i;
              break;
            }
          }
          if (idx >= 0) {
            Object val = var.getFunc().getParameterValues()[idx];
            if (val instanceof Double) {
              nonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(Tools.doubleToString((Double) val));
            }
            else {
              nonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(val.toString());
            }
          }
          else {
            nonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(null);
          }
        }
      }
    }
    finally {
      cmbRefreshing = false;
    }
  }

  private final double DELTA_VAR = 0.05;
  private final double DELTA_PARAM = 0.1;

  public void nonlinearVarLeftButtonClicked(int pIdx) {
    nonlinearVarREdChanged(pIdx, -DELTA_VAR);
  }

  public void nonlinearVarRightButtonClicked(int pIdx) {
    nonlinearVarREdChanged(pIdx, DELTA_VAR);
  }

  public void nonlinearParamsLeftButtonClicked(int pIdx) {
    nonlinearParamsREdChanged(pIdx, -DELTA_PARAM);
  }

  public void nonlinearParamsRightButtonClicked(int pIdx) {
    nonlinearParamsREdChanged(pIdx, DELTA_PARAM);
  }

  public void xFormSymmetrySlider_changed() {
    xFormSliderChanged(xFormSymmetrySlider, xFormSymmetryREd, "colorSymmetry", SLIDER_SCALE_COLOR);
  }

  public void xFormOpacityREd_changed() {
    xFormTextFieldChanged(xFormOpacitySlider, xFormOpacityREd, "opacity", SLIDER_SCALE_COLOR);
  }

  public void xFormOpacitySlider_changed() {
    xFormSliderChanged(xFormOpacitySlider, xFormOpacityREd, "opacity", SLIDER_SCALE_COLOR);
  }

  public void xFormDrawModeCmb_changed() {
    if (!cmbRefreshing) {
      XForm xForm = getCurrXForm();
      if (xForm != null && xFormDrawModeCmb.getSelectedItem() != null) {
        xForm.setDrawMode((DrawMode) xFormDrawModeCmb.getSelectedItem());
        refreshFlameImage();
        enableXFormControls(xForm);
      }
    }
  }

  public void xFormColorSlider_changed() {
    xFormSliderChanged(xFormColorSlider, xFormColorREd, "color", SLIDER_SCALE_COLOR);
  }

  public void xFormSymmetryREd_changed() {
    xFormTextFieldChanged(xFormSymmetrySlider, xFormSymmetryREd, "colorSymmetry", SLIDER_SCALE_COLOR);
  }

  public void xFormColorREd_changed() {
    xFormTextFieldChanged(xFormColorSlider, xFormColorREd, "color", SLIDER_SCALE_COLOR);
  }

  private void relWeightsChanged(double pDelta) {
    XForm xForm = getCurrXForm();
    Flame currFlame = getCurrFlame();
    if (xForm != null && currFlame != null && xForm != currFlame.getFinalXForm()) {
      int row = relWeightsTable.getSelectedRow();
      if (row >= 0 && row < currFlame.getXForms().size()) {
        xForm.getModifiedWeights()[row] += pDelta;
        gridRefreshing = true;
        try {
          refreshRelWeightsTable();
          relWeightsTable.getSelectionModel().setSelectionInterval(row, row);
          refreshFlameImage();
        }
        finally {
          gridRefreshing = false;
        }
      }
    }
  }

  private void transformationWeightChanged(double pDelta) {
    XForm xForm = getCurrXForm();
    Flame currFlame = getCurrFlame();
    if (xForm != null && currFlame != null && xForm != currFlame.getFinalXForm()) {
      xForm.setWeight(xForm.getWeight() + pDelta);
      gridRefreshing = true;
      try {
        int row = transformationsTable.getSelectedRow();
        refreshTransformationsTable();
        transformationsTable.getSelectionModel().setSelectionInterval(row, row);
        refreshFlameImage();
      }
      finally {
        gridRefreshing = false;
      }
    }
  }

  public void relWeightsLeftButton_clicked() {
    relWeightsChanged(-DELTA_PARAM);
  }

  public void relWeightsRightButton_clicked() {
    relWeightsChanged(DELTA_PARAM);
  }

  public void transformationWeightRightButton_clicked() {
    transformationWeightChanged(DELTA_PARAM);
  }

  public void transformationWeightLeftButton_clicked() {
    transformationWeightChanged(-DELTA_PARAM);
  }

  public void newFlameButton_clicked() {
    Flame flame = new Flame();
    flame.setWidth(800);
    flame.setHeight(600);
    flame.setPixelsPerUnit(50);
    RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(Integer.parseInt(paletteRandomPointsREd.getText()));
    flame.setPalette(palette);
    _currFlame = flame;
    refreshUI();
  }

  public void renderModeCmb_changed() {
    if (!refreshing && !cmbRefreshing) {
      refreshFlameImage();
    }
  }

  public void zStyleCmb_changed() {
    if (!refreshing && !cmbRefreshing) {
      refreshFlameImage();
    }
  }

  public void setMorphFlame1() {
    if (_currFlame != null) {
      morphFlame1 = _currFlame.makeCopy();
      lastMorphedFrame = -1;
      enableControls();
      refreshFlameImage();
    }
  }

  public void setMorphFlame2() {
    if (_currFlame != null) {
      morphFlame2 = _currFlame.makeCopy();
      lastMorphedFrame = -1;
      enableControls();
      refreshFlameImage();
    }
  }

  public void morphFramesREd_changed() {
    int frame = Integer.parseInt(morphFramesREd.getText());
    if (!refreshing) {
      refreshing = true;
      try {
        morphFrameSlider.setMaximum(frame);
        lastMorphedFrame = -1;
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void morphFrameREd_changed() {
    int frame = Integer.parseInt(morphFramesREd.getText());
    if (!refreshing) {
      refreshing = true;
      try {
        morphFrameSlider.setValue(frame);
        lastMorphedFrame = -1;
        refreshFlameImage();
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void morphCheckBox_changed() {
    lastMorphedFrame = -1;
    refreshFlameImage();
  }

  public void morphFrameSlider_changed() {
    int frame = morphFrameSlider.getValue();
    if (!refreshing) {
      refreshing = true;
      try {
        morphFrameREd.setText(String.valueOf(frame));
        lastMorphedFrame = -1;
        refreshFlameImage();
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void importMorphedFlameButton_clicked() {
    if (morphCheckBox.isSelected()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        randomBatch.add(0, flame);
        updateThumbnails(null);
      }
    }
  }

  public void animationGenerateButton_clicked() {
    try {
      int frames = Integer.parseInt(animateFramesREd.getText());
      boolean doMorph = morphCheckBox.isSelected();
      GlobalScript globalScript = (GlobalScript) animateGlobalScriptCmb.getSelectedItem();
      XFormScript xFormScript = (XFormScript) animateXFormScriptCmb.getSelectedItem();
      String imagePath = animateOutputREd.getText();
      int width = Integer.parseInt(renderWidthREd.getText());
      int height = Integer.parseInt(renderHeightREd.getText());
      int quality = Integer.parseInt(renderQualityREd.getText());
      AffineZStyle affineZStyle = (AffineZStyle) zStyleCmb.getSelectedItem();
      for (int frame = 1; frame <= frames; frame++) {
        Flame flame1 = doMorph ? morphFlame1.makeCopy() : _currFlame.makeCopy();
        Flame flame2 = doMorph ? morphFlame2.makeCopy() : null;
        AnimationService.renderFrame(frame, frames, flame1, flame2, doMorph, globalScript, xFormScript, imagePath, width, height, quality, affineZStyle);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  @Override
  public Flame getFlame() {
    return getCurrFlame();
  }

  private void flamePanel_mouseDragged(MouseEvent e) {
    if (flamePanel != null) {
      if (flamePanel.mouseDragged(e.getX(), e.getY())) {
        transformationTableClicked();
        refreshFlameImage();
      }
    }
  }

  private void flamePanel_mousePressed(MouseEvent e) {
    if (flamePanel != null) {
      flamePanel.mousePressed(e.getX(), e.getY());
    }
  }

  private void flamePanel_mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      renderFlameButton_actionPerformed(null);
    }
    else if (e.getClickCount() == 1) {
      Flame flame = getCurrFlame();
      if (flame != null && flamePanel != null) {
        XForm xForm = flamePanel.mouseClicked(e.getX(), e.getY());
        if (xForm != null) {
          for (int i = 0; i < flame.getXForms().size(); i++) {
            if (xForm == flame.getXForms().get(i)) {
              transformationsTable.getSelectionModel().setSelectionInterval(i, i);
              return;
            }
          }
          if (xForm == flame.getFinalXForm()) {
            int row = flame.getXForms().size();
            transformationsTable.getSelectionModel().setSelectionInterval(row, row);
            return;
          }
        }
      }
    }
  }

  public void mouseTransformMoveButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        mouseTransformRotateButton.setSelected(false);
        mouseTransformScaleButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(mouseTransformMoveButton.isSelected() ? MouseDragOperation.MOVE : MouseDragOperation.NONE);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void mouseTransformRotateButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        mouseTransformMoveButton.setSelected(false);
        mouseTransformScaleButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(mouseTransformRotateButton.isSelected() ? MouseDragOperation.ROTATE : MouseDragOperation.NONE);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void mouseTransformScaleButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        mouseTransformMoveButton.setSelected(false);
        mouseTransformRotateButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(mouseTransformScaleButton.isSelected() ? MouseDragOperation.SCALE : MouseDragOperation.NONE);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void setMainController(MainController pMainController) {
    mainController = pMainController;
  }

  public void affineEditPostTransformButton_clicked() {
    if (refreshing) {
      return;
    }
    refreshing = true;
    try {
      XForm xForm = getCurrXForm();
      if (flamePanel != null) {
        flamePanel.setEditPostTransform(affineEditPostTransformButton.isSelected());
      }
      refreshXFormUI(xForm);
      refreshFlameImage();
      affineEditPostTransformSmallButton.setSelected(affineEditPostTransformButton.isSelected());
    }
    finally {
      refreshing = false;
    }
  }

  public void affineEditPostTransformSmallButton_clicked() {
    refreshing = true;
    try {
      affineEditPostTransformButton.setSelected(affineEditPostTransformSmallButton.isSelected());
    }
    finally {
      refreshing = false;
    }
    affineEditPostTransformButton_clicked();
  }

  public void mouseTransformZoomInButton_clicked() {
    if (flamePanel != null) {
      flamePanel.zoomIn();
      refreshFlameImage();
    }
  }

  public void mouseTransformZoomOutButton_clicked() {
    if (flamePanel != null) {
      flamePanel.zoomOut();
      refreshFlameImage();
    }
  }

  public void toggleTrianglesButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setDrawFlame(toggleTrianglesButton.isSelected());
      refreshFlameImage();
    }
  }

  public void affineC21REd_changed() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      double value = Tools.stringToDouble(affineC21REd.getText());
      if (affineEditPostTransformButton.isSelected()) {
        xForm.setPostCoeff21(value);
      }
      else {
        xForm.setCoeff21(value);
      }
      transformationTableClicked();
      refreshFlameImage();
    }
  }

  public void affineC20REd_changed() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      double value = Tools.stringToDouble(affineC20REd.getText());
      if (affineEditPostTransformButton.isSelected()) {
        xForm.setPostCoeff20(value);
      }
      else {
        xForm.setCoeff20(value);
      }
      transformationTableClicked();
      refreshFlameImage();
    }
  }

  public void affineResetTransformButton_clicked() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      XFormTransformService.reset(xForm, affineEditPostTransformButton.isSelected());
      transformationTableClicked();
      refreshFlameImage();
    }
  }

}
