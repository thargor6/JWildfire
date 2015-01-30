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
package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jwildfire.base.MacroButton;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.iflames.swing.JInternalFrameFlameMessageHelper;
import org.jwildfire.create.tina.AnimationController;
import org.jwildfire.create.tina.GradientController;
import org.jwildfire.create.tina.JWFScriptController;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.batch.BatchRendererController;
import org.jwildfire.create.tina.browser.FlameBrowserController;
import org.jwildfire.create.tina.dance.DancingFractalsController;
import org.jwildfire.create.tina.edit.UndoManager;
import org.jwildfire.create.tina.io.Flam3GradientReader;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.meshgen.MeshGenController;
import org.jwildfire.create.tina.mutagen.BokehMutation;
import org.jwildfire.create.tina.mutagen.MutaGenController;
import org.jwildfire.create.tina.mutagen.MutationType;
import org.jwildfire.create.tina.palette.DefaultGradientSelectionProvider;
import org.jwildfire.create.tina.palette.GradientSelectionProvider;
import org.jwildfire.create.tina.palette.MedianCutQuantizer;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RGBPaletteRenderer;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSample;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomflame.WikimediaCommonsRandomFlameGenerator;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.script.ScriptRunner;
import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelControlStyle;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.MainController;

import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.swing.JFontChooser;
import com.l2fprod.common.util.ResourceManager;

public class TinaController implements FlameHolder, LayerHolder, ScriptRunnerEnvironment, UndoManagerHolder<Flame>, JWFScriptExecuteController, GradientSelectionProvider,
    DetachedPreviewProvider, FlamePanelProvider, RandomBatchHolder, RenderProgressBarHolder {
  public static final int PAGE_INDEX = 0;

  static final double SLIDER_SCALE_PERSPECTIVE = 100.0;
  static final double SLIDER_SCALE_CENTRE = 5000.0;
  static final double SLIDER_SCALE_ZOOM = 1000.0;
  static final double SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY = 100.0;
  static final double SLIDER_SCALE_GAMMA = 100.0;
  static final double SLIDER_SCALE_FILTER_RADIUS = 100.0;
  static final double SLIDER_SCALE_GAMMA_THRESHOLD = 5000.0;
  static final double SLIDER_SCALE_COLOR = 100.0;
  static final double SLIDER_SCALE_ZPOS = 50.0;
  static final double SLIDER_SCALE_DOF = 100.0;
  static final double SLIDER_SCALE_DOF_AREA = 100.0;
  static final double SLIDER_SCALE_DOF_EXPONENT = 100.0;
  static final double SLIDER_SCALE_AMBIENT = 100.0;
  static final double SLIDER_SCALE_PHONGSIZE = 10.0;
  static final double SLIDER_SCALE_LIGHTPOS = 100.0;
  static final double SLIDER_SCALE_BLUR_FALLOFF = 10.0;

  private DancingFractalsController dancingFractalsController;
  private MutaGenController mutaGenController;
  private MeshGenController meshGenController;
  private BatchRendererController batchRendererController;
  private TinaInteractiveRendererController interactiveRendererCtrl;
  private TinaSWFAnimatorController swfAnimatorCtrl;
  private JWFScriptController jwfScriptController;
  private FlameBrowserController flameBrowserController;
  private GradientController gradientController;
  private AnimationController animationController;

  private FlameControlsDelegate flameControls;
  private GradientControlsDelegate gradientControls;
  private XFormControlsDelegate xFormControls;
  private ChannelMixerControlsDelegate channelMixerControls;

  private final TinaInternalFrame tinaFrame;
  private final String tinaFrameTitle;
  private final JPanel centerPanel;
  private final FlameMessageHelper messageHelper;
  private final FlamePreviewHelper flamePreviewHelper;
  private boolean firstFlamePanel = true;
  private FlamePanel flamePanel;
  private FlamePanel prevFlamePanel;

  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  boolean gridRefreshing = false;
  boolean cmbRefreshing = false;
  boolean refreshing = false;

  final UndoManager<Flame> undoManager = new UndoManager<Flame>();
  private final QuickSaveFilenameGen qsaveFilenameGen;

  private MainController mainController;
  private final JTabbedPane rootTabbedPane;
  private Flame _currFlame, _currRandomizeFlame;
  private boolean noRefresh;
  private final ProgressUpdater mainProgressUpdater;
  private TinaControllerData data = new TinaControllerData();
  private VariationControlsDelegate[] variationControlsDelegates;
  private RGBPalette _lastGradient;

  public TinaController(TinaControllerParameter parameterObject) {
    tinaFrame = parameterObject.pTinaFrame;
    tinaFrameTitle = tinaFrame.getTitle();
    errorHandler = parameterObject.pErrorHandler;
    prefs = parameterObject.pPrefs;
    centerPanel = parameterObject.pCenterPanel;

    dancingFractalsController = new DancingFractalsController(this, parameterObject.pErrorHandler, parameterObject.pRootTabbedPane, parameterObject.pDancingFlamesFlamePnl, parameterObject.pDancingFlamesGraph1Pnl,
        parameterObject.pDancingFlamesLoadSoundBtn, parameterObject.pDancingFlamesAddFromClipboardBtn, parameterObject.pDancingFlamesAddFromEditorBtn,
        parameterObject.pDancingFlamesAddFromDiscBtn, parameterObject.pDancingFlamesRandomCountIEd, parameterObject.pDancingFlamesGenRandFlamesBtn,
        parameterObject.pDancingFlamesRandomGenCmb, parameterObject.pDancingFlamesPoolFlamePreviewPnl, parameterObject.pDancingFlamesBorderSizeSlider,
        parameterObject.pDancingFlamesFlameToEditorBtn, parameterObject.pDancingFlamesDeleteFlameBtn, parameterObject.pDancingFlamesFramesPerSecondIEd, parameterObject.pDancingFlamesMorphFrameCountIEd,
        parameterObject.pDancingFlamesStartShowButton, parameterObject.pDancingFlamesStopShowButton, parameterObject.pDancingFlamesDoRecordCBx,
        parameterObject.pDancingFlamesFlamesCmb,
        parameterObject.pDancingFlamesDrawTrianglesCBx, parameterObject.pDancingFlamesDrawFFTCBx, parameterObject.pDancingFlamesDrawFPSCBx, parameterObject.pDancingFlamesFlamePropertiesTree,
        parameterObject.pDancingFlamesMotionPropertyPnl, parameterObject.pDancingFlamesMotionTable, parameterObject.pDancingFlamesAddMotionCmb, parameterObject.pDancingFlamesAddMotionBtn,
        parameterObject.pDancingFlamesDeleteMotionBtn, parameterObject.pDancingFlamesLinkMotionBtn, parameterObject.pDancingFlamesUnlinkMotionBtn,
        parameterObject.pDancingFlamesCreateMotionsCmb, parameterObject.pDancingFlamesClearMotionsBtn, parameterObject.pDancingFlamesLoadProjectBtn, parameterObject.pDancingFlamesSaveProjectBtn,
        parameterObject.pDancingFlamesMotionLinksTable, parameterObject.dancingFlamesReplaceFlameFromEditorBtn, parameterObject.dancingFlamesRenameFlameBtn,
        parameterObject.dancingFlamesRenameMotionBtn, parameterObject.dancingFlamesMutedCBx);

    {
      JPanel flamePanels[] = new JPanel[25];
      flamePanels[0] = parameterObject.mutaGen01Pnl;
      flamePanels[1] = parameterObject.mutaGen02Pnl;
      flamePanels[2] = parameterObject.mutaGen03Pnl;
      flamePanels[3] = parameterObject.mutaGen04Pnl;
      flamePanels[4] = parameterObject.mutaGen05Pnl;
      flamePanels[5] = parameterObject.mutaGen06Pnl;
      flamePanels[6] = parameterObject.mutaGen07Pnl;
      flamePanels[7] = parameterObject.mutaGen08Pnl;
      flamePanels[8] = parameterObject.mutaGen09Pnl;
      flamePanels[9] = parameterObject.mutaGen10Pnl;
      flamePanels[10] = parameterObject.mutaGen11Pnl;
      flamePanels[11] = parameterObject.mutaGen12Pnl;
      flamePanels[12] = parameterObject.mutaGen13Pnl;
      flamePanels[13] = parameterObject.mutaGen14Pnl;
      flamePanels[14] = parameterObject.mutaGen15Pnl;
      flamePanels[15] = parameterObject.mutaGen16Pnl;
      flamePanels[16] = parameterObject.mutaGen17Pnl;
      flamePanels[17] = parameterObject.mutaGen18Pnl;
      flamePanels[18] = parameterObject.mutaGen19Pnl;
      flamePanels[19] = parameterObject.mutaGen20Pnl;
      flamePanels[20] = parameterObject.mutaGen21Pnl;
      flamePanels[21] = parameterObject.mutaGen22Pnl;
      flamePanels[22] = parameterObject.mutaGen23Pnl;
      flamePanels[23] = parameterObject.mutaGen24Pnl;
      flamePanels[24] = parameterObject.mutaGen25Pnl;
      mutaGenController = new MutaGenController(this, parameterObject.pErrorHandler, prefs, parameterObject.pRootTabbedPane, flamePanels,
          parameterObject.mutaGenLoadFlameFromEditorBtn, parameterObject.mutaGenLoadFlameFromFileBtn, parameterObject.mutaGenProgressBar,
          parameterObject.mutaGenAmountREd, parameterObject.mutaGenHorizontalTrend1Cmb, parameterObject.mutaGenHorizontalTrend2Cmb,
          parameterObject.mutaGenVerticalTrend1Cmb, parameterObject.mutaGenVerticalTrend2Cmb, parameterObject.mutaGenBackBtn, parameterObject.mutaGenForwardBtn,
          parameterObject.mutaGenHintPane, parameterObject.mutaGenSaveFlameToEditorBtn, parameterObject.mutaGenSaveFlameToFileBtn);
    }

    batchRendererController = new BatchRendererController(this, parameterObject.pErrorHandler, prefs, parameterObject.pRootTabbedPane, data,
        parameterObject.pJobProgressUpdater, parameterObject.batchRenderOverrideCBx, parameterObject.batchRenderShowImageBtn);

    meshGenController = new MeshGenController(this, parameterObject.pErrorHandler, prefs, parameterObject.pRootTabbedPane,
        parameterObject.meshGenFromEditorBtn, parameterObject.meshGenFromClipboardBtn, parameterObject.meshGenLoadFlameBtn,
        parameterObject.meshGenSliceCountREd, parameterObject.meshGenSlicesPerRenderREd, parameterObject.meshGenRenderWidthREd,
        parameterObject.meshGenRenderHeightREd, parameterObject.meshGenRenderQualityREd, parameterObject.meshGenProgressbar,
        parameterObject.meshGenGenerateBtn, parameterObject.meshGenTopViewRootPnl, parameterObject.meshGenFrontViewRootPnl,
        parameterObject.meshGenPerspectiveViewRootPnl, parameterObject.meshGenHintPane, parameterObject.meshGenCentreXREd,
        parameterObject.meshGenCentreXSlider, parameterObject.meshGenCentreYREd, parameterObject.meshGenCentreYSlider,
        parameterObject.meshGenZoomREd, parameterObject.meshGenZoomSlider, parameterObject.meshGenZMinREd,
        parameterObject.meshGenZMinSlider, parameterObject.meshGenZMaxREd, parameterObject.meshGenZMaxSlider,
        parameterObject.meshGenTopViewRenderBtn, parameterObject.meshGenFrontViewRenderBtn, parameterObject.meshGenPerspectiveViewRenderBtn,
        parameterObject.meshGenTopViewToEditorBtn, parameterObject.meshGenLoadSequenceBtn, parameterObject.meshGenSequenceWidthREd,
        parameterObject.meshGenSequenceHeightREd, parameterObject.meshGenSequenceSlicesREd, parameterObject.meshGenSequenceDownSampleREd,
        parameterObject.meshGenSequenceFilterRadiusREd, parameterObject.meshGenGenerateMeshProgressbar, parameterObject.meshGenGenerateMeshBtn,
        parameterObject.meshGenSequenceFromRendererBtn, parameterObject.meshGenSequenceThresholdREd, parameterObject.meshGenSequenceLbl,
        parameterObject.meshGenPreviewRootPanel, parameterObject.meshGenAutoPreviewCBx, parameterObject.meshGenPreviewImportLastGeneratedMeshBtn,
        parameterObject.meshGenPreviewImportFromFileBtn, parameterObject.meshGenClearPreviewBtn, parameterObject.meshGenPreviewPositionXREd,
        parameterObject.meshGenPreviewPositionYREd, parameterObject.meshGenPreviewSizeREd,
        parameterObject.meshGenPreviewScaleZREd, parameterObject.meshGenPreviewRotateAlphaREd, parameterObject.meshGenPreviewRotateBetaREd,
        parameterObject.meshGenPreviewPointsREd, parameterObject.meshGenPreviewPolygonsREd, parameterObject.meshGenRefreshPreviewBtn,
        parameterObject.meshGenPreviewSunflowExportBtn, parameterObject.meshGenThicknessModREd, parameterObject.meshGenThicknessSamplesREd,
        parameterObject.meshGenPreFilter1Cmb, parameterObject.meshGenPreFilter2Cmb, parameterObject.meshGenImageStepREd);

    data.macroButtonsTable = parameterObject.macroButtonsTable;
    data.macroButtonMoveUpBtn = parameterObject.macroButtonMoveUpBtn;
    data.macroButtonMoveDownBtn = parameterObject.macroButtonMoveDownBtn;
    data.macroButtonDeleteBtn = parameterObject.macroButtonDeleteBtn;
    jwfScriptController = new JWFScriptController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, data, parameterObject.scriptTree,
        parameterObject.scriptDescriptionTextArea, parameterObject.scriptTextArea, parameterObject.compileScriptButton,
        parameterObject.saveScriptBtn, parameterObject.revertScriptBtn, parameterObject.rescanScriptsBtn, parameterObject.newScriptBtn, parameterObject.newScriptFromFlameBtn, parameterObject.deleteScriptBtn,
        parameterObject.scriptRenameBtn, parameterObject.scriptDuplicateBtn, parameterObject.scriptRunBtn, parameterObject.scriptAddButtonBtn);

    flameBrowserController = new FlameBrowserController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, parameterObject.flameBrowserTree, parameterObject.flameBrowersImagesPnl,
        parameterObject.flameBrowserRefreshBtn, parameterObject.flameBrowserChangeFolderBtn, parameterObject.flameBrowserToEditorBtn, parameterObject.flameBrowserToBatchEditorBtn, parameterObject.flameBrowserDeleteBtn,
        parameterObject.flameBrowserRenameBtn, parameterObject.flameBrowserCopyToBtn, parameterObject.flameBrowserMoveToBtn, parameterObject.flameBrowserToMeshGenBtn);

    gradientController = new GradientController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, parameterObject.gradientLibTree, parameterObject.pGradientLibraryPanel,
        parameterObject.gradientLibraryRescanBtn, parameterObject.gradientLibraryNewFolderBtn, parameterObject.gradientLibraryRenameFolderBtn,
        parameterObject.gradientsList);

    animationController = new AnimationController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel,
        parameterObject.keyframesFrameField, parameterObject.keyframesFrameSlider, parameterObject.keyframesFrameCountField,
        parameterObject.frameSliderPanel, parameterObject.keyframesFrameLbl, parameterObject.keyframesFrameCountLbl,
        parameterObject.motionCurveEditModeButton, parameterObject.motionBlurPanel, parameterObject.motionCurvePlayPreviewButton);

    data.toggleDetachedPreviewButton = parameterObject.toggleDetachedPreviewButton;
    data.cameraRollREd = parameterObject.pCameraRollREd;
    data.cameraRollSlider = parameterObject.pCameraRollSlider;
    data.cameraPitchREd = parameterObject.pCameraPitchREd;
    data.cameraPitchSlider = parameterObject.pCameraPitchSlider;
    data.cameraYawREd = parameterObject.pCameraYawREd;
    data.cameraYawSlider = parameterObject.pCameraYawSlider;
    data.cameraPerspectiveREd = parameterObject.pCameraPerspectiveREd;
    data.cameraPerspectiveSlider = parameterObject.pCameraPerspectiveSlider;
    data.cameraCentreXREd = parameterObject.pCameraCentreXREd;
    data.cameraCentreXSlider = parameterObject.pCameraCentreXSlider;
    data.cameraCentreYREd = parameterObject.pCameraCentreYREd;
    data.cameraCentreYSlider = parameterObject.pCameraCentreYSlider;
    data.cameraZoomREd = parameterObject.pCameraZoomREd;
    data.cameraZoomSlider = parameterObject.pCameraZoomSlider;
    data.camPosXREd = parameterObject.camPosXREd;
    data.camPosXSlider = parameterObject.camPosXSlider;
    data.camPosYREd = parameterObject.camPosYREd;
    data.camPosYSlider = parameterObject.camPosYSlider;
    data.camPosZREd = parameterObject.camPosZREd;
    data.camPosZSlider = parameterObject.camPosZSlider;

    data.newDOFCBx = parameterObject.pNewDOFCBx;
    data.focusXREd = parameterObject.pFocusXREd;
    data.focusXSlider = parameterObject.pFocusXSlider;
    data.focusYREd = parameterObject.pFocusYREd;
    data.focusYSlider = parameterObject.pFocusYSlider;
    data.focusZREd = parameterObject.pFocusZREd;
    data.focusZSlider = parameterObject.pFocusZSlider;
    data.dimishZREd = parameterObject.pDimishZREd;
    data.dimishZSlider = parameterObject.pDimishZSlider;
    data.cameraDOFREd = parameterObject.pCameraDOFREd;
    data.cameraDOFSlider = parameterObject.pCameraDOFSlider;
    data.cameraDOFAreaREd = parameterObject.pCameraDOFAreaREd;
    data.cameraDOFAreaSlider = parameterObject.pCameraDOFAreaSlider;
    data.cameraDOFExponentREd = parameterObject.pCameraDOFExponentREd;
    data.cameraDOFExponentSlider = parameterObject.pCameraDOFExponentSlider;
    data.camZREd = parameterObject.pCamZREd;
    data.camZSlider = parameterObject.pCamZSlider;
    data.pixelsPerUnitREd = parameterObject.pPixelsPerUnitREd;
    data.pixelsPerUnitSlider = parameterObject.pPixelsPerUnitSlider;
    data.brightnessREd = parameterObject.pBrightnessREd;
    data.brightnessSlider = parameterObject.pBrightnessSlider;
    data.contrastREd = parameterObject.pContrastREd;
    data.contrastSlider = parameterObject.pContrastSlider;
    data.whiteLevelREd = parameterObject.whiteLevelREd;
    data.whiteLevelSlider = parameterObject.whiteLevelSlider;
    data.gammaREd = parameterObject.pGammaREd;
    data.gammaSlider = parameterObject.pGammaSlider;
    data.vibrancyREd = parameterObject.pVibrancyREd;
    data.vibrancySlider = parameterObject.pVibrancySlider;
    data.saturationREd = parameterObject.saturationREd;
    data.saturationSlider = parameterObject.saturationSlider;
    data.filterRadiusREd = parameterObject.pFilterRadiusREd;
    data.filterRadiusSlider = parameterObject.pFilterRadiusSlider;
    data.filterKernelCmb = parameterObject.pFilterKernelCmb;
    data.gammaThresholdREd = parameterObject.pGammaThresholdREd;
    data.gammaThresholdSlider = parameterObject.pGammaThresholdSlider;
    data.bgTransparencyCBx = parameterObject.pBGTransparencyCBx;
    data.paletteRandomPointsREd = parameterObject.pPaletteRandomPointsREd;
    data.paletteRandomGeneratorCmb = parameterObject.paletteRandomGeneratorCmb;
    data.paletteFadeColorsCBx = parameterObject.paletteFadeColorsCBx;
    data.paletteImgPanel = parameterObject.pPaletteImgPanel;
    data.colorChooserPaletteImgPanel = parameterObject.pColorChooserPaletteImgPanel;
    data.paletteShiftREd = parameterObject.pPaletteShiftREd;
    data.paletteShiftSlider = parameterObject.pPaletteShiftSlider;
    data.paletteRedREd = parameterObject.pPaletteRedREd;
    data.paletteRedSlider = parameterObject.pPaletteRedSlider;
    data.paletteGreenREd = parameterObject.pPaletteGreenREd;
    data.paletteGreenSlider = parameterObject.pPaletteGreenSlider;
    data.paletteBlueREd = parameterObject.pPaletteBlueREd;
    data.paletteBlueSlider = parameterObject.pPaletteBlueSlider;
    data.paletteHueREd = parameterObject.pPaletteHueREd;
    data.paletteHueSlider = parameterObject.pPaletteHueSlider;
    data.paletteSaturationREd = parameterObject.pPaletteSaturationREd;
    data.paletteSaturationSlider = parameterObject.pPaletteSaturationSlider;
    data.paletteContrastREd = parameterObject.pPaletteContrastREd;
    data.paletteContrastSlider = parameterObject.pPaletteContrastSlider;
    data.paletteGammaREd = parameterObject.pPaletteGammaREd;
    data.paletteGammaSlider = parameterObject.pPaletteGammaSlider;
    data.paletteBrightnessREd = parameterObject.pPaletteBrightnessREd;
    data.paletteBrightnessSlider = parameterObject.pPaletteBrightnessSlider;
    data.paletteSwapRGBREd = parameterObject.pPaletteSwapRGBREd;
    data.paletteSwapRGBSlider = parameterObject.pPaletteSwapRGBSlider;
    data.paletteFrequencyREd = parameterObject.pPaletteFrequencyREd;
    data.paletteFrequencySlider = parameterObject.pPaletteFrequencySlider;
    data.paletteBlurREd = parameterObject.pPaletteBlurREd;
    data.paletteBlurSlider = parameterObject.pPaletteBlurSlider;
    data.paletteInvertBtn = parameterObject.pPaletteInvertBtn;
    data.paletteReverseBtn = parameterObject.pPaletteReverseBtn;

    data.qualityProfileCmb = parameterObject.pQualityProfileCmb;
    data.resolutionProfileCmb = parameterObject.pResolutionProfileCmb;
    data.batchQualityProfileCmb = parameterObject.pBatchQualityProfileCmb;
    data.batchResolutionProfileCmb = parameterObject.pBatchResolutionProfileCmb;
    data.interactiveResolutionProfileCmb = parameterObject.pInteractiveResolutionProfileCmb;
    data.swfAnimatorResolutionProfileCmb = parameterObject.pSWFAnimatorResolutionProfileCmb;
    data.transformationsTable = parameterObject.pTransformationsTable;
    data.affineC00REd = parameterObject.pAffineC00REd;
    data.affineC01REd = parameterObject.pAffineC01REd;
    data.affineC10REd = parameterObject.pAffineC10REd;
    data.affineC11REd = parameterObject.pAffineC11REd;
    data.affineC20REd = parameterObject.pAffineC20REd;
    data.affineC21REd = parameterObject.pAffineC21REd;
    data.affineRotateAmountREd = parameterObject.pAffineRotateAmountREd;
    data.affineScaleAmountREd = parameterObject.pAffineScaleAmountREd;
    data.affineMoveHorizAmountREd = parameterObject.affineMoveHorizAmountREd;
    data.affineMoveVertAmountREd = parameterObject.affineMoveVertAmountREd;
    data.affineRotateLeftButton = parameterObject.pAffineRotateLeftButton;
    data.affineRotateRightButton = parameterObject.pAffineRotateRightButton;
    data.affineEnlargeButton = parameterObject.pAffineEnlargeButton;
    data.affineShrinkButton = parameterObject.pAffineShrinkButton;
    data.affineMoveUpButton = parameterObject.pAffineMoveUpButton;
    data.affineMoveLeftButton = parameterObject.pAffineMoveLeftButton;
    data.affineMoveRightButton = parameterObject.pAffineMoveRightButton;
    data.affineMoveDownButton = parameterObject.pAffineMoveDownButton;
    data.affineFlipHorizontalButton = parameterObject.pAffineFlipHorizontalButton;
    data.affineFlipVerticalButton = parameterObject.pAffineFlipVerticalButton;
    data.addTransformationButton = parameterObject.pAddTransformationButton;
    data.addLinkedTransformationButton = parameterObject.pAddLinkedTransformationButton;
    data.duplicateTransformationButton = parameterObject.pDuplicateTransformationButton;
    data.deleteTransformationButton = parameterObject.pDeleteTransformationButton;
    data.addFinalTransformationButton = parameterObject.pAddFinalTransformationButton;
    data.affineEditPostTransformButton = parameterObject.pAffineEditPostTransformButton;
    data.affineRotateEditMotionCurveBtn = parameterObject.affineRotateEditMotionCurveBtn;
    data.affineScaleEditMotionCurveBtn = parameterObject.affineScaleEditMotionCurveBtn;
    data.affineEditPostTransformSmallButton = parameterObject.pAffineEditPostTransformSmallButton;
    data.affinePreserveZButton = parameterObject.pAffinePreserveZButton;
    data.affineScaleXButton = parameterObject.pAffineScaleXButton;
    data.affineScaleYButton = parameterObject.pAffineScaleYButton;

    data.randomBatchPanel = parameterObject.pRandomBatchPanel;
    data.TinaNonlinearControlsRows = parameterObject.pTinaNonlinearControlsRows;
    data.variationControlsDelegates = parameterObject.variationControlsDelegates;
    data.gradientLibraryPanel = parameterObject.pGradientLibraryPanel;
    data.renderFlameButton = parameterObject.pRenderFlameButton;
    data.renderMainButton = parameterObject.pRenderMainButton;
    data.appendToMovieButton = parameterObject.pAppendToMovieButton;

    data.xFormColorREd = parameterObject.pXFormColorREd;
    data.xFormColorSlider = parameterObject.pXFormColorSlider;
    data.xFormSymmetryREd = parameterObject.pXFormSymmetryREd;
    data.xFormSymmetrySlider = parameterObject.pXFormSymmetrySlider;
    data.xFormModGammaREd = parameterObject.pXFormModGammaREd;
    data.xFormModGammaSlider = parameterObject.pXFormModGammaSlider;
    data.xFormModGammaSpeedREd = parameterObject.pXFormModGammaSpeedREd;
    data.xFormModGammaSpeedSlider = parameterObject.pXFormModGammaSpeedSlider;

    data.xFormModContrastREd = parameterObject.pXFormModContrastREd;
    data.xFormModContrastSlider = parameterObject.pXFormModContrastSlider;
    data.xFormModContrastSpeedREd = parameterObject.pXFormModContrastSpeedREd;
    data.xFormModContrastSpeedSlider = parameterObject.pXFormModContrastSpeedSlider;

    data.xFormModSaturationREd = parameterObject.pXFormModSaturationREd;
    data.xFormModSaturationSlider = parameterObject.pXFormModSaturationSlider;
    data.xFormModSaturationSpeedREd = parameterObject.pXFormModSaturationSpeedREd;
    data.xFormModSaturationSpeedSlider = parameterObject.pXFormModSaturationSpeedSlider;

    data.xFormOpacityREd = parameterObject.pXFormOpacityREd;
    data.xFormOpacitySlider = parameterObject.pXFormOpacitySlider;
    data.xFormDrawModeCmb = parameterObject.pXFormDrawModeCmb;

    data.xFormAntialiasAmountREd = parameterObject.pXFormAntialiasAmountREd;
    data.xFormAntialiasAmountSlider = parameterObject.pXFormAntialiasAmountSlider;
    data.xFormAntialiasRadiusREd = parameterObject.pXFormAntialiasRadiusREd;
    data.xFormAntialiasRadiusSlider = parameterObject.pXFormAntialiasRadiusSlider;

    data.relWeightsTable = parameterObject.pRelWeightsTable;
    data.relWeightsZeroButton = parameterObject.pRelWeightsZeroButton;
    data.relWeightsOneButton = parameterObject.pRelWeightsOneButton;
    data.relWeightREd = parameterObject.pRelWeightREd;

    data.transformationWeightREd = parameterObject.pTransformationWeightREd;

    data.mouseTransformMoveTrianglesButton = parameterObject.mouseTransformMoveTrianglesButton;
    data.mouseTransformRotateTrianglesButton = parameterObject.mouseTransformRotateTrianglesButton;
    data.mouseTransformScaleTrianglesButton = parameterObject.mouseTransformScaleTrianglesButton;
    data.mouseTransformEditFocusPointButton = parameterObject.pMouseTransformEditFocusPointButton;
    data.mouseTransformEditPointsButton = parameterObject.pMouseTransformShearButton;
    data.mouseTransformEditGradientButton = parameterObject.mouseTransformEditGradientButton;
    data.mouseTransformEditTriangleViewButton = parameterObject.mouseTransformEditTriangleViewButton;

    data.layerWeightEd = parameterObject.layerWeightEd;
    data.layerAddBtn = parameterObject.layerAddBtn;
    data.layerDuplicateBtn = parameterObject.layerDuplicateBtn;
    data.layerDeleteBtn = parameterObject.layerDeleteBtn;
    data.layersTable = parameterObject.layersTable;
    data.layerVisibleBtn = parameterObject.layerVisibleBtn;
    data.layerAppendBtn = parameterObject.layerAppendBtn;
    data.layerPreviewBtn = parameterObject.layerPreviewBtn;
    data.layerHideOthersBtn = parameterObject.layerHideOthersBtn;
    data.layerShowAllBtn = parameterObject.layerShowAllBtn;

    data.gradientColorMapHorizOffsetREd = parameterObject.gradientColorMapHorizOffsetREd;
    data.gradientColorMapHorizOffsetSlider = parameterObject.gradientColorMapHorizOffsetSlider;
    data.gradientColorMapHorizScaleREd = parameterObject.gradientColorMapHorizScaleREd;
    data.gradientColorMapHorizScaleSlider = parameterObject.gradientColorMapHorizScaleSlider;
    data.gradientColorMapVertOffsetREd = parameterObject.gradientColorMapVertOffsetREd;
    data.gradientColorMapVertOffsetSlider = parameterObject.gradientColorMapVertOffsetSlider;
    data.gradientColorMapVertScaleREd = parameterObject.gradientColorMapVertScaleREd;
    data.gradientColorMapVertScaleSlider = parameterObject.gradientColorMapVertScaleSlider;
    data.gradientColorMapLocalColorAddREd = parameterObject.gradientColorMapLocalColorAddREd;
    data.gradientColorMapLocalColorAddSlider = parameterObject.gradientColorMapLocalColorAddSlider;
    data.gradientColorMapLocalColorScaleREd = parameterObject.gradientColorMapLocalColorScaleREd;
    data.gradientColorMapLocalColorScaleSlider = parameterObject.gradientColorMapLocalColorScaleSlider;

    data.motionBlurLengthField = parameterObject.motionBlurLengthField;
    data.motionBlurLengthSlider = parameterObject.motionBlurLengthSlider;
    data.motionBlurTimeStepField = parameterObject.motionBlurTimeStepField;
    data.motionBlurTimeStepSlider = parameterObject.motionBlurTimeStepSlider;
    data.motionBlurDecayField = parameterObject.motionBlurDecayField;
    data.motionBlurDecaySlider = parameterObject.motionBlurDecaySlider;

    data.postSymmetryTypeCmb = parameterObject.postSymmetryTypeCmb;
    data.postSymmetryDistanceREd = parameterObject.postSymmetryDistanceREd;
    data.postSymmetryDistanceSlider = parameterObject.postSymmetryDistanceSlider;
    data.postSymmetryRotationREd = parameterObject.postSymmetryRotationREd;
    data.postSymmetryRotationSlider = parameterObject.postSymmetryRotationSlider;
    data.postSymmetryOrderREd = parameterObject.postSymmetryOrderREd;
    data.postSymmetryOrderSlider = parameterObject.postSymmetryOrderSlider;
    data.postSymmetryCentreXREd = parameterObject.postSymmetryCentreXREd;
    data.postSymmetryCentreXSlider = parameterObject.postSymmetryCentreXSlider;
    data.postSymmetryCentreYREd = parameterObject.postSymmetryCentreYREd;
    data.postSymmetryCentreYSlider = parameterObject.postSymmetryCentreYSlider;

    data.stereo3dModeCmb = parameterObject.stereo3dModeCmb;
    data.stereo3dAngleREd = parameterObject.stereo3dAngleREd;
    data.stereo3dAngleSlider = parameterObject.stereo3dAngleSlider;
    data.stereo3dEyeDistREd = parameterObject.stereo3dEyeDistREd;
    data.stereo3dEyeDistSlider = parameterObject.stereo3dEyeDistSlider;
    data.stereo3dLeftEyeColorCmb = parameterObject.stereo3dLeftEyeColorCmb;
    data.stereo3dRightEyeColorCmb = parameterObject.stereo3dRightEyeColorCmb;
    data.stereo3dInterpolatedImageCountREd = parameterObject.stereo3dInterpolatedImageCountREd;
    data.stereo3dInterpolatedImageCountSlider = parameterObject.stereo3dInterpolatedImageCountSlider;
    data.stereo3dPreviewCmb = parameterObject.stereo3dPreviewCmb;
    data.stereo3dFocalOffsetREd = parameterObject.stereo3dFocalOffsetREd;
    data.stereo3dFocalOffsetSlider = parameterObject.stereo3dFocalOffsetSlider;
    data.stereo3dSwapSidesCBx = parameterObject.stereo3dSwapSidesCBx;

    data.mouseTransformEditViewButton = parameterObject.pMouseTransformViewButton;
    data.toggleVariationsButton = parameterObject.pToggleVariationsButton;
    data.toggleTransparencyButton = parameterObject.pToggleTransparencyButton;
    data.randomizeButton = parameterObject.randomizeButton;
    mainProgressUpdater = new RenderProgressUpdater(this);
    data.affineResetTransformButton = parameterObject.pAffineResetTransformButton;
    data.createPaletteColorsTable = parameterObject.pCreatePaletteColorsTable;
    data.shadingCmb = parameterObject.pShadingCmb;
    data.shadingAmbientREd = parameterObject.pShadingAmbientREd;
    data.shadingAmbientSlider = parameterObject.pShadingAmbientSlider;
    data.shadingDiffuseREd = parameterObject.pShadingDiffuseREd;
    data.shadingDiffuseSlider = parameterObject.pShadingDiffuseSlider;
    data.shadingPhongREd = parameterObject.pShadingPhongREd;
    data.shadingPhongSlider = parameterObject.pShadingPhongSlider;
    data.shadingPhongSizeREd = parameterObject.pShadingPhongSizeREd;
    data.shadingPhongSizeSlider = parameterObject.pShadingPhongSizeSlider;
    data.shadingLightCmb = parameterObject.pShadingLightCmb;
    data.shadingLightXREd = parameterObject.pShadingLightXREd;
    data.shadingLightXSlider = parameterObject.pShadingLightXSlider;
    data.shadingLightYREd = parameterObject.pShadingLightYREd;
    data.shadingLightYSlider = parameterObject.pShadingLightYSlider;
    data.shadingLightZREd = parameterObject.pShadingLightZREd;
    data.shadingLightZSlider = parameterObject.pShadingLightZSlider;
    data.shadingLightRedREd = parameterObject.pShadingLightRedREd;
    data.shadingLightRedSlider = parameterObject.pShadingLightRedSlider;
    data.shadingLightGreenREd = parameterObject.pShadingLightGreenREd;
    data.shadingLightGreenSlider = parameterObject.pShadingLightGreenSlider;
    data.shadingLightBlueREd = parameterObject.pShadingLightBlueREd;
    data.shadingLightBlueSlider = parameterObject.pShadingLightBlueSlider;

    data.shadingBlurRadiusREd = parameterObject.pShadingBlurRadiusREd;
    data.shadingBlurRadiusSlider = parameterObject.pShadingBlurRadiusSlider;
    data.shadingBlurFadeREd = parameterObject.pShadingBlurFadeREd;
    data.shadingBlurFadeSlider = parameterObject.pShadingBlurFadeSlider;
    data.shadingBlurFallOffREd = parameterObject.pShadingBlurFallOffREd;
    data.shadingBlurFallOffSlider = parameterObject.pShadingBlurFallOffSlider;

    data.shadingDistanceColorRadiusREd = parameterObject.pShadingDistanceColorRadiusREd;
    data.shadingDistanceColorRadiusSlider = parameterObject.pShadingDistanceColorRadiusSlider;
    data.shadingDistanceColorScaleREd = parameterObject.pShadingDistanceColorScaleREd;
    data.shadingDistanceColorScaleSlider = parameterObject.pShadingDistanceColorScaleSlider;
    data.shadingDistanceColorExponentREd = parameterObject.pShadingDistanceColorExponentREd;
    data.shadingDistanceColorExponentSlider = parameterObject.pShadingDistanceColorExponentSlider;
    data.shadingDistanceColorOffsetXREd = parameterObject.pShadingDistanceColorOffsetXREd;
    data.shadingDistanceColorOffsetXSlider = parameterObject.pShadingDistanceColorOffsetXSlider;
    data.shadingDistanceColorOffsetYREd = parameterObject.pShadingDistanceColorOffsetYREd;
    data.shadingDistanceColorOffsetYSlider = parameterObject.pShadingDistanceColorOffsetYSlider;
    data.shadingDistanceColorOffsetZREd = parameterObject.pShadingDistanceColorOffsetZREd;
    data.shadingDistanceColorOffsetZSlider = parameterObject.pShadingDistanceColorOffsetZSlider;
    data.shadingDistanceColorStyleREd = parameterObject.pShadingDistanceColorStyleREd;
    data.shadingDistanceColorStyleSlider = parameterObject.pShadingDistanceColorStyleSlider;
    data.shadingDistanceColorCoordinateREd = parameterObject.pShadingDistanceColorCoordinateREd;
    data.shadingDistanceColorCoordinateSlider = parameterObject.pShadingDistanceColorCoordinateSlider;
    data.shadingDistanceColorShiftREd = parameterObject.pShadingDistanceColorShiftREd;
    data.shadingDistanceColorShiftSlider = parameterObject.pShadingDistanceColorShiftSlider;

    data.mouseTransformSlowButton = parameterObject.pMouseTransformSlowButton;
    data.toggleTriangleWithColorsButton = parameterObject.toggleTriangleWithColorsButton;

    data.renderBatchJobsTable = parameterObject.pRenderBatchJobsTable;
    data.batchPreviewRootPanel = parameterObject.pBatchPreviewRootPanel;
    data.batchRenderJobProgressBar = parameterObject.pBatchRenderJobProgressBar;
    data.batchRenderTotalProgressBar = parameterObject.pBatchRenderTotalProgressBar;
    data.batchRenderAddFilesButton = parameterObject.pBatchRenderAddFilesButton;
    data.batchRenderFilesMoveDownButton = parameterObject.pBatchRenderFilesMoveDownButton;
    data.batchRenderFilesMoveUpButton = parameterObject.pBatchRenderFilesMoveUpButton;
    data.batchRenderFilesRemoveButton = parameterObject.pBatchRenderFilesRemoveButton;
    data.batchRenderFilesRemoveAllButton = parameterObject.pBatchRenderFilesRemoveAllButton;
    data.batchRenderStartButton = parameterObject.pBatchRenderStartButton;
    rootTabbedPane = parameterObject.pRootTabbedPane;
    data.helpPane = parameterObject.pHelpPane;
    data.apophysisHintsPane = parameterObject.apophysisHintsPane;
    data.faqPane = parameterObject.pFAQPane;

    data.undoButton = parameterObject.pUndoButton;
    data.redoButton = parameterObject.pRedoButton;
    data.editTransformCaptionButton = parameterObject.editTransformCaptionButton;
    data.editFlameTileButton = parameterObject.editFlameTileButton;
    data.snapShotButton = parameterObject.snapShotButton;
    data.qSaveButton = parameterObject.qSaveButton;
    data.quickMutationButton = parameterObject.quickMutationButton;
    data.bokehButton = parameterObject.bokehButton;
    data.dancingFlamesButton = parameterObject.dancingFlamesButton;
    data.movieButton = parameterObject.movieButton;
    data.transformSlowButton = parameterObject.transformSlowButton;
    data.transparencyButton = parameterObject.transparencyButton;
    data.scriptTree = parameterObject.scriptTree;
    data.scriptDescriptionTextArea = parameterObject.scriptDescriptionTextArea;
    data.scriptTextArea = parameterObject.scriptTextArea;
    data.compileScriptButton = parameterObject.compileScriptButton;
    data.saveScriptBtn = parameterObject.saveScriptBtn;
    data.revertScriptBtn = parameterObject.revertScriptBtn;
    data.rescanScriptsBtn = parameterObject.rescanScriptsBtn;
    data.newScriptBtn = parameterObject.newScriptBtn;
    data.newScriptFromFlameBtn = parameterObject.newScriptFromFlameBtn;
    data.deleteScriptBtn = parameterObject.deleteScriptBtn;
    data.scriptRenameBtn = parameterObject.scriptRenameBtn;
    data.scriptDuplicateBtn = parameterObject.scriptDuplicateBtn;
    data.scriptRunBtn = parameterObject.scriptRunBtn;
    data.gradientLibTree = parameterObject.gradientLibTree;
    data.backgroundColorIndicatorBtn = parameterObject.backgroundColorIndicatorBtn;
    data.toggleDrawGridButton = parameterObject.toggleDrawGridButton;
    data.toggleDrawGuidesButton = parameterObject.toggleDrawGuidesButton;
    data.triangleStyleCmb = parameterObject.triangleStyleCmb;
    data.editorFractalBrightnessSlider = parameterObject.editorFractalBrightnessSlider;
    data.channelMixerResetBtn = parameterObject.channelMixerResetBtn;
    data.channelMixerModeCmb = parameterObject.channelMixerModeCmb;
    data.channelMixerRRRootPanel = parameterObject.channelMixerRRRootPanel;
    data.channelMixerRGRootPanel = parameterObject.channelMixerRGRootPanel;
    data.channelMixerRBRootPanel = parameterObject.channelMixerRBRootPanel;
    data.channelMixerGRRootPanel = parameterObject.channelMixerGRRootPanel;
    data.channelMixerGGRootPanel = parameterObject.channelMixerGGRootPanel;
    data.channelMixerGBRootPanel = parameterObject.channelMixerGBRootPanel;
    data.channelMixerBRRootPanel = parameterObject.channelMixerBRRootPanel;
    data.channelMixerBGRootPanel = parameterObject.channelMixerBGRootPanel;
    data.channelMixerBBRootPanel = parameterObject.channelMixerBBRootPanel;
    data.resetCameraSettingsBtn = parameterObject.resetCameraSettingsBtn;
    data.resetDOFSettingsButton = parameterObject.resetDOFSettingsButton;
    data.resetBokehOptionsButton = parameterObject.resetBokehOptionsButton;
    data.resetColoringOptionsButton = parameterObject.resetColoringOptionsButton;
    data.resetAntialiasOptionsButton = parameterObject.resetAntialiasOptionsButton;
    data.resetShadingSettingsBtn = parameterObject.resetShadingSettingsBtn;
    data.resetStereo3DSettingsBtn = parameterObject.resetStereo3DSettingsBtn;
    data.resetPostSymmetrySettingsBtn = parameterObject.resetPostSymmetrySettingsBtn;
    data.resetMotionBlurSettingsBtn = parameterObject.resetMotionBlurSettingsBtn;
    data.dofDOFShapeCmb = parameterObject.dofDOFShapeCmb;
    data.dofDOFScaleREd = parameterObject.dofDOFScaleREd;
    data.dofDOFScaleSlider = parameterObject.dofDOFScaleSlider;
    data.dofDOFAngleREd = parameterObject.dofDOFAngleREd;
    data.dofDOFAngleSlider = parameterObject.dofDOFAngleSlider;
    data.dofDOFFadeREd = parameterObject.dofDOFFadeREd;
    data.dofDOFFadeSlider = parameterObject.dofDOFFadeSlider;
    data.dofDOFParam1REd = parameterObject.dofDOFParam1REd;
    data.dofDOFParam1Slider = parameterObject.dofDOFParam1Slider;
    data.dofDOFParam1Lbl = parameterObject.dofDOFParam1Lbl;
    data.dofDOFParam2REd = parameterObject.dofDOFParam2REd;
    data.dofDOFParam2Slider = parameterObject.dofDOFParam2Slider;
    data.dofDOFParam2Lbl = parameterObject.dofDOFParam2Lbl;
    data.dofDOFParam3REd = parameterObject.dofDOFParam3REd;
    data.dofDOFParam3Slider = parameterObject.dofDOFParam3Slider;
    data.dofDOFParam3Lbl = parameterObject.dofDOFParam3Lbl;
    data.dofDOFParam4REd = parameterObject.dofDOFParam4REd;
    data.dofDOFParam4Slider = parameterObject.dofDOFParam4Slider;
    data.dofDOFParam4Lbl = parameterObject.dofDOFParam4Lbl;
    data.dofDOFParam5REd = parameterObject.dofDOFParam5REd;
    data.dofDOFParam5Slider = parameterObject.dofDOFParam5Slider;
    data.dofDOFParam5Lbl = parameterObject.dofDOFParam5Lbl;
    data.dofDOFParam6REd = parameterObject.dofDOFParam6REd;
    data.dofDOFParam6Slider = parameterObject.dofDOFParam6Slider;
    data.dofDOFParam6Lbl = parameterObject.dofDOFParam6Lbl;
    data.xaosViewAsToBtn = parameterObject.xaosViewAsToBtn;
    data.xaosViewAsFromBtn = parameterObject.xaosViewAsFromBtn;
    data.previewEastMainPanel = parameterObject.previewEastMainPanel;
    data.macroButtonVertPanel = parameterObject.macroButtonVertPanel;
    data.macroButtonHorizPanel = parameterObject.macroButtonHorizPanel;
    data.gradientResetBtn = parameterObject.gradientResetBtn;
    data.macroButtonHorizRootPanel = parameterObject.macroButtonHorizRootPanel;
    data.affineEditPlaneCmb = parameterObject.affineEditPlaneCmb;

    // end create
    flameControls = new FlameControlsDelegate(this, data, rootTabbedPane);
    xFormControls = new XFormControlsDelegate(this, data, rootTabbedPane);
    gradientControls = new GradientControlsDelegate(this, data, rootTabbedPane);
    channelMixerControls = new ChannelMixerControlsDelegate(this, errorHandler, data, rootTabbedPane, true);

    messageHelper = new JInternalFrameFlameMessageHelper(tinaFrame);

    flamePreviewHelper = new FlamePreviewHelper(errorHandler, centerPanel, data.toggleTransparencyButton,
        data.layerAppendBtn, data.layerPreviewBtn, mainProgressUpdater, this, this,
        this, this, messageHelper, this);

    registerMotionPropertyControls();

    qsaveFilenameGen = new QuickSaveFilenameGen(prefs);

    enableUndoControls();

    initHelpPane();
    initApophysisHintsPane();
    initFAQPane();

    refreshPaletteColorsTable();
    getBatchRendererController().refreshRenderBatchJobsTable();

    enableControls();

    xFormControls.enableControls(null);
    enableLayerControls();

    refreshResolutionProfileCmb(data.resolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.interactiveResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.batchResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.swfAnimatorResolutionProfileCmb, null);
    refreshQualityProfileCmb(data.qualityProfileCmb, null);
    refreshQualityProfileCmb(data.batchQualityProfileCmb, null);

    getFlameBrowserController().init();
  }

  private void enableLayerControls() {
    Flame flame = getCurrFlame();
    Layer layer = getCurrLayer();
    data.layerWeightEd.setEnabled(layer != null);
    data.layerAddBtn.setEnabled(flame != null);
    data.layerDuplicateBtn.setEnabled(layer != null);
    data.layerDeleteBtn.setEnabled(flame != null && layer != null && getCurrFlame().getLayers().size() > 1);
    data.layersTable.setEnabled(flame != null);
    data.layerVisibleBtn.setEnabled(layer != null);
    data.layerAppendBtn.setEnabled(flame != null);
    data.layerPreviewBtn.setEnabled(flame != null);
    data.layerHideOthersBtn.setEnabled(layer != null);
    data.layerShowAllBtn.setEnabled(flame != null);
  }

  private void initApophysisHintsPane() {
    data.apophysisHintsPane.setContentType("text/html");
    try {
      InputStream is = this.getClass().getResourceAsStream("ApophysisHints.html");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();

      data.apophysisHintsPane.setText(content.toString());
      data.apophysisHintsPane.setSelectionStart(0);
      data.apophysisHintsPane.setSelectionEnd(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initHelpPane() {
    data.helpPane.setContentType("text/html");
    try {
      InputStream is = this.getClass().getResourceAsStream("TINA.html");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();

      data.helpPane.setText(content.toString());
      data.helpPane.setSelectionStart(0);
      data.helpPane.setSelectionEnd(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initFAQPane() {
    data.faqPane.setContentType("text/html");
    try {
      InputStream is = this.getClass().getResourceAsStream("FAQ.html");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();

      data.faqPane.setText(content.toString());
      data.faqPane.setSelectionStart(0);
      data.faqPane.setSelectionEnd(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void refreshResolutionProfileCmb(JComboBox pCmb, ResolutionProfile pSelectedProfile) {
    boolean oldCmbRefreshing = cmbRefreshing;
    cmbRefreshing = true;
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
      cmbRefreshing = oldCmbRefreshing;
    }
  }

  private void refreshQualityProfileCmb(JComboBox pCmb, QualityProfile pSelectedProfile) {
    boolean oldCmbRefreshing = cmbRefreshing;
    cmbRefreshing = true;
    try {
      QualityProfile selected = pSelectedProfile;
      QualityProfile defaultProfile = null;
      pCmb.removeAllItems();
      for (QualityProfile profile : prefs.getQualityProfiles()) {
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
      if (pCmb.getSelectedIndex() < 0 && prefs.getQualityProfiles().size() > 0) {
        pCmb.setSelectedIndex(0);
      }
    }
    finally {
      cmbRefreshing = oldCmbRefreshing;
    }
  }

  private boolean dragging = false;
  private boolean keypressing = false;

  @Override
  public FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      int width = centerPanel.getWidth();
      int height = centerPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(prefs, img, 0, 0, centerPanel.getWidth(), this, this);
      flamePanel.getConfig().setWithColoredTransforms(prefs.isTinaEditorControlsWithColor());
      flamePanel.setFlamePanelTriangleMode(prefs.getTinaEditorControlsStyle());
      flamePanel.importOptions(prevFlamePanel);
      prevFlamePanel = null;
      flamePanel.setUndoManagerHolder(this);
      ResolutionProfile resProfile = getResolutionProfile();
      flamePanel.setRenderWidth(resProfile.getWidth());
      flamePanel.setRenderHeight(resProfile.getHeight());
      flamePanel.setFocusable(true);
      flamePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
          flamePanel_mouseDragged(e);
          dragging = true;
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
          flamePanel.requestFocusInWindow();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          if (dragging) {
            refreshFlameImage(false);
            flamePanel_mouseReleased(e);
          }
          dragging = false;
        }

      });

      flamePanel.addMouseWheelListener(new MouseWheelListener() {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
          flamePanel_mouseWheelEvent(e);
        }
      });

      flamePanel.addKeyListener(new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
          if (keypressing) {
            return;
          }
          keypressing = true;
          try {
            boolean altPressed = e.isAltDown(); // || e.isAltGraphDown());
            boolean shiftPressed = e.isShiftDown();
            boolean ctrlPressed = e.isControlDown();
            switch (e.getKeyCode()) {
            // left
              case 37:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
                  if (altPressed) {
                    xForm_rotateLeft();
                  }
                  else if (ctrlPressed) {
                    xForm_moveLeft(0.1);
                  }
                  else if (shiftPressed) {
                    xForm_moveLeft(10.0);
                  }
                  else {
                    xForm_moveLeft(1.0);
                  }
                }
                else if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(0, -1);
                }
                break;
              // right
              case 39:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
                  if (altPressed) {
                    xForm_rotateRight();
                  }
                  else if (ctrlPressed) {
                    xForm_moveRight(0.1);
                  }
                  else if (shiftPressed) {
                    xForm_moveRight(10.0);
                  }
                  else {
                    xForm_moveRight(1.0);
                  }
                }
                else if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(0, 1);
                }
                break;
              // up
              case 38:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
                  if (altPressed) {
                    xForm_enlarge();
                  }
                  else if (ctrlPressed) {
                    xForm_moveUp(0.1);
                  }
                  else if (shiftPressed) {
                    xForm_moveUp(10.0);
                  }
                  else {
                    xForm_moveUp(1.0);
                  }
                }
                else if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(1, 1);
                }
                break;
              // down
              case 40:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
                  if (altPressed) {
                    xForm_shrink();
                  }
                  else if (ctrlPressed) {
                    xForm_moveDown(0.1);
                  }
                  else if (shiftPressed) {
                    xForm_moveDown(10.0);
                  }
                  else {
                    xForm_moveDown(1.0);
                  }
                }
                else if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(1, -1);
                }
                break;
              // 1
              case 49:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_selectColor(0);
                }
                break;
              // 1
              case 50:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_selectColor(1);
                }
                break;
            }
          }
          finally {
            flamePanel.requestFocus();
            keypressing = false;
          }
        }

      });

      flamePanel.setSelectedXForm(getCurrXForm());
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

  private ImagePanel getPalettePanel() {
    if (data.palettePanel == null) {
      int width = data.paletteImgPanel.getWidth();
      int height = data.paletteImgPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      data.palettePanel = new ImagePanel(img, 0, 0, data.paletteImgPanel.getWidth());
      data.paletteImgPanel.add(data.palettePanel, BorderLayout.CENTER);
      data.paletteImgPanel.getParent().validate();
    }
    return data.palettePanel;
  }

  private ImagePanel getColorChooserPalettePanel() {
    if (data.colorChooserPalettePanel == null) {
      int width = data.colorChooserPaletteImgPanel.getWidth();
      int height = data.colorChooserPaletteImgPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      data.colorChooserPalettePanel = new ImagePanel(img, 0, 0, data.colorChooserPaletteImgPanel.getWidth());
      data.colorChooserPaletteImgPanel.add(data.colorChooserPalettePanel, BorderLayout.CENTER);
      data.colorChooserPaletteImgPanel.getParent().validate();
    }
    return data.colorChooserPalettePanel;
  }

  public void refreshFlameImage(boolean pMouseDown) {
    refreshFlameImage(true, pMouseDown, 1);
  }

  @Override
  public Flame getCurrFlame() {
    return _currFlame;
  }

  @Override
  public Flame getCurrFlame(boolean autoGenerateIfEmpty) {
    if (_currFlame == null) {
      final int IMG_WIDTH = 80;
      final int IMG_HEIGHT = 60;
      RandomFlameGenerator randGen = new AllRandomFlameGenerator();
      int palettePoints = 3 + (int) (Math.random() * 21.0);
      boolean fadePaletteColors = Math.random() > 0.09;
      RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, RandomSymmetryGeneratorList.SPARSE, RandomGradientGeneratorList.DEFAULT, palettePoints, fadePaletteColors, RandomBatchQuality.LOW);
      Flame flame = sampler.createSample().getFlame();
      setCurrFlame(flame);
    }
    return _currFlame;
  }

  public Flame getCurrFlameStack() {
    return _currFlame;
  }

  @Override
  public Layer getCurrLayer() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      int row = data.layersTable.getSelectedRow();
      if (row >= 0 && row < flame.getLayers().size()) {
        return flame.getLayers().get(row);
      }
      else {
        return flame.getFirstLayer();
      }
    }
    return null;
  }

  private Flame getCurrRandomizeFlame() {
    return _currRandomizeFlame;
  }

  @Override
  public void setCurrFlame(Flame pFlame) {
    setCurrFlame(pFlame, true);
  }

  private void setCurrFlame(Flame pFlame, boolean pAddToThumbnails) {
    if (_currFlame == null || !_currFlame.equals(pFlame)) {
      _currRandomizeFlame = pFlame.makeCopy();
    }
    setLastGradient(null);
    if (_currFlame != null) {
      for (Layer layer : _currFlame.getLayers()) {
        deRegisterFromEditor(_currFlame, layer);
      }
    }
    importFlame(pFlame, pAddToThumbnails);
    for (Layer layer : _currFlame.getLayers()) {
      registerToEditor(_currFlame, layer);
    }
  }

  public void setLastGradient(RGBPalette pGradient) {
    _lastGradient = pGradient != null ? pGradient.makeDeepCopy() : null;
  }

  protected void deRegisterFromEditor(Flame pFlame, Layer pLayer) {
    if (pFlame != null && pLayer != null && pLayer.getPalette() != null) {
      pLayer.getPalette().setSelectionProvider(new DefaultGradientSelectionProvider());
    }
  }

  public void registerToEditor(Flame pFlame, Layer pLayer) {
    if (pFlame != null && pLayer != null && pLayer.getPalette() != null) {
      pLayer.getPalette().setSelectionProvider(this);
    }
  }

  @Override
  public FlamePanelConfig getFlamePanelConfig() {
    return getFlamePanel().getConfig();
  }

  public void refreshFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale) {
    flamePreviewHelper.refreshFlameImage(pQuickRender, pMouseDown, pDownScale);
  }

  @Override
  public void refreshUI() {
    gridRefreshing = true;
    try {
      refreshLayersTable();
      int row = data.layersTable.getSelectedRow();
      if (row < 0 && getCurrFlame() != null && getCurrFlame().getLayers().size() > 0) {
        data.layersTable.getSelectionModel().setSelectionInterval(0, 0);
      }

    }
    finally {
      gridRefreshing = false;
    }
    refreshLayerUI();
    refreshLayerControls(getCurrLayer());
    refreshKeyFramesUI();
  }

  public void refreshKeyFramesUI() {
    noRefresh = true;
    try {
      animationController.refreshUI();
    }
    finally {
      noRefresh = false;
    }
  }

  public void refreshLayerUI() {
    noRefresh = true;
    try {
      flameControls.refreshFlameValues();

      refreshBGColorIndicator();

      data.affinePreserveZButton.setSelected(getCurrFlame().isPreserveZ());

      gridRefreshing = true;
      try {
        refreshTransformationsTable();
      }
      finally {
        gridRefreshing = false;
      }
      transformationTableClicked();

      data.shadingLightCmb.setSelectedIndex(0);

      enableControls();
      flameControls.refreshShadingUI();
      enableLayerControls();
      channelMixerControls.refreshValues(true);
      refreshPaletteUI(getCurrLayer().getPalette());
    }
    finally {
      noRefresh = false;
    }
  }

  public class TransformationsTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (row >= 0 && column == 0) {
        int colorIdx = row % FlamePanelConfig.XFORM_COLORS.length;
        c.setForeground(FlamePanelConfig.XFORM_COLORS[colorIdx]);
      }
      else {
        c.setForeground(table.getForeground());
      }
      return c;
    }
  }

  private void refreshTransformationsTable() {
    final int COL_TRANSFORM = 0;
    final int COL_VARIATIONS = 1;
    final int COL_WEIGHT = 2;
    if (prefs.isTinaEditorControlsWithColor()) {
      data.transformationsTable.setDefaultRenderer(Object.class, new TransformationsTableCellRenderer());
    }

    data.transformationsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return getCurrLayer() != null ? getCurrLayer().getXForms().size() + getCurrLayer().getFinalXForms().size() : 0;
      }

      @Override
      public int getColumnCount() {
        return 3;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_TRANSFORM:
            return "Transf";
          case COL_VARIATIONS:
            return "Variations/Name";
          case COL_WEIGHT:
            return "Weight";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        if (getCurrFlame() != null) {
          XForm xForm = rowIndex < getCurrLayer().getXForms().size() ? getCurrLayer().getXForms().get(rowIndex) : getCurrLayer().getFinalXForms().get(rowIndex - getCurrLayer().getXForms().size());
          switch (columnIndex) {
            case COL_TRANSFORM:
              return rowIndex < getCurrLayer().getXForms().size() ? "Transf" + String.valueOf(rowIndex + 1) : "Final" + String.valueOf(rowIndex - getCurrLayer().getXForms().size() + 1);
            case COL_VARIATIONS:
              return getXFormCaption(xForm);
            case COL_WEIGHT:
              return rowIndex < getCurrLayer().getXForms().size() ? Tools.doubleToString(xForm.getWeight()) : "";
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
        if (getCurrLayer() != null && column == COL_WEIGHT && row < getCurrLayer().getXForms().size()) {
          XForm xForm = getCurrLayer().getXForms().get(row);
          String valStr = (String) aValue;
          if (valStr == null || valStr.length() == 0) {
            valStr = "0";
          }
          saveUndoPoint();
          xForm.setWeight(Tools.stringToDouble(valStr));
          refreshXFormUI(xForm);
          refreshFlameImage(false);
        }
        super.setValueAt(aValue, row, column);
      }

    });
    data.transformationsTable.getTableHeader().setFont(data.transformationsTable.getFont());
    data.transformationsTable.getColumnModel().getColumn(COL_TRANSFORM).setWidth(20);
    data.transformationsTable.getColumnModel().getColumn(COL_VARIATIONS).setPreferredWidth(120);
    data.transformationsTable.getColumnModel().getColumn(COL_WEIGHT).setWidth(16);
  }

  public Font getTableFont() {
    return data.transformationsTable.getFont();
  }

  private void refreshLayersTable() {
    final int COL_LAYER = 0;
    final int COL_CAPTION = 1;
    final int COL_VISIBLE = 2;
    final int COL_WEIGHT = 3;
    data.layersTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return getCurrFlame() != null ? getCurrFlame().getLayers().size() : 0;
      }

      @Override
      public int getColumnCount() {
        return 4;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_LAYER:
            return "Layer";
          case COL_CAPTION:
            return "Caption";
          case COL_VISIBLE:
            return "Visible";
          case COL_WEIGHT:
            return "Weight";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        if (getCurrFlame() != null) {
          Layer layer = getCurrFlame().getLayers().get(rowIndex);
          switch (columnIndex) {
            case COL_LAYER:
              return "Layer" + String.valueOf(rowIndex + 1);
            case COL_CAPTION:
              if (layer != null) {
                return layer.getName();
              }
            case COL_VISIBLE:
              if (layer != null) {
                return layer.isVisible() ? "1" : "0";
              }
            case COL_WEIGHT:
              return Tools.doubleToString(layer.getWeight());
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return column == COL_CAPTION || column == COL_VISIBLE || column == COL_WEIGHT;
      }

      @Override
      public void setValueAt(Object aValue, int row, int column) {
        Layer layer = getCurrLayer();
        if (layer != null) {
          switch (column) {
            case COL_CAPTION:
              saveUndoPoint();
              layer.setName((String) aValue);
              break;
            case COL_VISIBLE:
              saveUndoPoint();
              layer.setVisible("1".equals(aValue));
              data.layerVisibleBtn.setSelected(layer.isVisible());
              refreshFlameImage(false);
              break;
            case COL_WEIGHT:
              saveUndoPoint();
              layer.setWeight(Tools.stringToDouble((String) aValue));
              data.layerWeightEd.setValue(layer.getWeight());
              // refreshed automatically:
              // refreshFlameImage(false);
              break;
          }
        }
        super.setValueAt(aValue, row, column);
      }

    });
    data.layersTable.getTableHeader().setFont(data.layersTable.getFont());
    data.layersTable.getColumnModel().getColumn(COL_LAYER).setPreferredWidth(120);
  }

  protected Object getXFormCaption(XForm pXForm) {
    if (pXForm.getName() != null && pXForm.getName().length() > 0) {
      return pXForm.getName();
    }
    else {
      String hs = "";
      if (pXForm.getVariationCount() > 0) {
        for (int i = 0; i < pXForm.getVariationCount() - 1; i++) {
          hs += pXForm.getVariation(i).getFunc().getName() + ", ";
        }
        hs += pXForm.getVariation(pXForm.getVariationCount() - 1).getFunc().getName();
      }
      return hs;
    }
  }

  class PaletteTableCustomRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (data.paletteKeyFrames != null && data.paletteKeyFrames.size() > row) {
        RGBColor color = data.paletteKeyFrames.get(row);
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

    data.createPaletteColorsTable.setDefaultRenderer(Object.class, new PaletteTableCustomRenderer());

    data.createPaletteColorsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return Integer.parseInt(data.paletteRandomPointsREd.getText());
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
        if (data.paletteKeyFrames != null && data.paletteKeyFrames.size() > rowIndex) {
          switch (columnIndex) {
            case COL_COLOR:
              return String.valueOf(rowIndex + 1);
            case COL_RED:
              return String.valueOf(data.paletteKeyFrames.get(rowIndex).getRed());
            case COL_GREEN:
              return String.valueOf(data.paletteKeyFrames.get(rowIndex).getGreen());
            case COL_BLUE:
              return String.valueOf(data.paletteKeyFrames.get(rowIndex).getBlue());
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
        if (getCurrFlame() != null && data.paletteKeyFrames != null && data.paletteKeyFrames.size() > row) {
          String valStr = (String) aValue;
          if (valStr == null || valStr.length() == 0) {
            valStr = "0";
          }
          switch (column) {
            case COL_RED:
              data.paletteKeyFrames.get(row).setRed(Tools.limitColor(Tools.stringToInt(valStr)));
              break;
            case COL_GREEN:
              data.paletteKeyFrames.get(row).setGreen(Tools.limitColor(Tools.stringToInt(valStr)));
              break;
            case COL_BLUE:
              data.paletteKeyFrames.get(row).setBlue(Tools.limitColor(Tools.stringToInt(valStr)));
              break;
          }
          refreshPaletteColorsTable();
          RGBPalette palette = RandomGradientGenerator.generatePalette(data.paletteKeyFrames, data.paletteFadeColorsCBx.isSelected());
          saveUndoPoint();
          getCurrLayer().setPalette(palette);
          setLastGradient(palette);
          refreshPaletteUI(palette);
          refreshFlameImage(false);
        }
        super.setValueAt(aValue, row, column);
      }

    });
    data.createPaletteColorsTable.getTableHeader().setFont(data.transformationsTable.getFont());
    data.createPaletteColorsTable.getColumnModel().getColumn(COL_COLOR).setWidth(20);
    data.createPaletteColorsTable.getColumnModel().getColumn(COL_RED).setPreferredWidth(60);
    data.createPaletteColorsTable.getColumnModel().getColumn(COL_GREEN).setPreferredWidth(60);
    data.createPaletteColorsTable.getColumnModel().getColumn(COL_BLUE).setPreferredWidth(60);
  }

  private boolean isXaosViewAsTo() {
    return data.xaosViewAsToBtn.isSelected();
  }

  private void refreshRelWeightsTable() {
    final int COL_TRANSFORM = 0;
    final int COL_WEIGHT = 1;
    final int COL_FROM = 2;
    final int COL_TO = 3;

    data.relWeightsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        XForm xForm = getCurrXForm();
        return xForm != null && getCurrLayer().getFinalXForms().indexOf(xForm) < 0 ? getCurrLayer().getXForms().size() : 0;
      }

      @Override
      public int getColumnCount() {
        return 4;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_TRANSFORM:
            return isXaosViewAsTo() ? "A to B" : "B from A";
          case COL_WEIGHT:
            return "Weight";
          case COL_FROM:
            return "From";
          case COL_TO:
            return "To";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        int transformIndex = data.transformationsTable.getSelectedRow();
        Layer layer = getCurrLayer();
        if (layer != null && transformIndex >= 0 && transformIndex < layer.getXForms().size()) {
          if (isXaosViewAsTo()) {
            switch (columnIndex) {
              case COL_TRANSFORM:
                return String.valueOf(transformIndex + 1) + " to " + String.valueOf(rowIndex + 1);
              case COL_WEIGHT:
                return Tools.doubleToString(layer.getXForms().get(transformIndex).getModifiedWeights()[rowIndex]);
              case COL_FROM:
                return getXFormCaption(layer.getXForms().get(transformIndex));
              case COL_TO:
                return getXFormCaption(layer.getXForms().get(rowIndex));
            }
          }
          else {
            switch (columnIndex) {
              case COL_TRANSFORM:
                return String.valueOf(transformIndex + 1) + " from " + String.valueOf(rowIndex + 1);
              case COL_WEIGHT:
                return Tools.doubleToString(layer.getXForms().get(rowIndex).getModifiedWeights()[transformIndex]);
              case COL_FROM:
                return getXFormCaption(layer.getXForms().get(rowIndex));
              case COL_TO:
                return getXFormCaption(layer.getXForms().get(transformIndex));
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
        if (getCurrFlame() != null && column == COL_WEIGHT && xForm != null) {
          String valStr = (String) aValue;
          if (valStr == null || valStr.length() == 0) {
            valStr = "0";
          }
          saveUndoPoint();
          xForm.getModifiedWeights()[row] = Tools.stringToDouble(valStr);
          relWeightsTableClicked();
          refreshFlameImage(false);
        }
        super.setValueAt(aValue, row, column);
      }

    });
    data.relWeightsTable.getTableHeader().setFont(data.relWeightsTable.getFont());
    data.relWeightsTable.getColumnModel().getColumn(COL_TRANSFORM).setWidth(20);
    data.relWeightsTable.getColumnModel().getColumn(COL_WEIGHT).setWidth(16);
  }

  public void refreshPaletteUI(RGBPalette pPalette) {
    data.paletteRedREd.setText(String.valueOf(pPalette.getModRed()));
    data.paletteRedSlider.setValue(pPalette.getModRed());
    data.paletteGreenREd.setText(String.valueOf(pPalette.getModGreen()));
    data.paletteGreenSlider.setValue(pPalette.getModGreen());
    data.paletteBlueREd.setText(String.valueOf(pPalette.getModBlue()));
    data.paletteBlueSlider.setValue(pPalette.getModBlue());
    data.paletteContrastREd.setText(String.valueOf(pPalette.getModContrast()));
    data.paletteContrastSlider.setValue(pPalette.getModContrast());
    data.paletteHueREd.setText(String.valueOf(pPalette.getModHue()));
    data.paletteHueSlider.setValue(pPalette.getModHue());
    data.paletteBrightnessREd.setText(String.valueOf(pPalette.getModBrightness()));
    data.paletteBrightnessSlider.setValue(pPalette.getModBrightness());
    data.paletteSwapRGBREd.setText(String.valueOf(pPalette.getModSwapRGB()));
    data.paletteSwapRGBSlider.setValue(pPalette.getModSwapRGB());
    data.paletteFrequencyREd.setText(String.valueOf(pPalette.getModFrequency()));
    data.paletteFrequencySlider.setValue(pPalette.getModFrequency());
    data.paletteBlurREd.setText(String.valueOf(pPalette.getModBlur()));
    data.paletteBlurSlider.setValue(pPalette.getModBlur());
    data.paletteGammaREd.setText(String.valueOf(pPalette.getModGamma()));
    data.paletteGammaSlider.setValue(pPalette.getModGamma());
    data.paletteShiftREd.setText(String.valueOf(pPalette.getModShift()));
    data.paletteShiftSlider.setValue(pPalette.getModShift());
    data.paletteSaturationREd.setText(String.valueOf(pPalette.getModSaturation()));
    data.paletteSaturationSlider.setValue(pPalette.getModSaturation());
    refreshPaletteImg();
  }

  private void refreshPaletteImg() {
    try {
      if (getCurrLayer() != null) {
        ImagePanel panels[] = { getPalettePanel(), getColorChooserPalettePanel() };
        for (ImagePanel imgPanel : panels) {
          int width = imgPanel.getWidth();
          int height = imgPanel.getHeight();
          if (width >= 16 && height >= 4) {
            if (getCurrLayer().getGradientMapFilename() != null && getCurrLayer().getGradientMapFilename().length() > 0) {
              SimpleImage img = (SimpleImage) RessourceManager.getImage(getCurrLayer().getGradientMapFilename());
              imgPanel.setImage(img, 0, 0, width, height);
            }
            else {
              SimpleImage img = new RGBPaletteRenderer().renderHorizPalette(getCurrLayer().getPalette(), width, height);
              imgPanel.setImage(img);
            }
          }
          imgPanel.getParent().validate();
          imgPanel.repaint();
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void paletteSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null)
      return;
    noRefresh = true;
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = getCurrLayer().getPalette().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(getCurrLayer().getPalette(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(getCurrLayer().getPalette(), Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
        field = cls.getDeclaredField("modified");
        field.setAccessible(true);
        field.setBoolean(getCurrLayer().getPalette(), true);
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshPaletteImg();
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void layerSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null || getCurrLayer() == null)
      return;
    noRefresh = true;
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = getCurrLayer().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(getCurrLayer(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(getCurrLayer(), Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void xFormSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh) {
      return;
    }
    if (getCurrFlame() == null) {
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
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void paletteTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null)
      return;
    noRefresh = true;
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = getCurrLayer().getPalette().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(getCurrLayer().getPalette(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(getCurrLayer().getPalette(), Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
        field = cls.getDeclaredField("modified");
        field.setAccessible(true);
        field.setBoolean(getCurrLayer().getPalette(), true);
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshPaletteImg();
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void layerTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null || getCurrLayer() == null)
      return;
    noRefresh = true;
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = getCurrLayer().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(getCurrLayer(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(getCurrLayer(), Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void xFormTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
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
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  public void renderFlameButton_actionPerformed(ActionEvent e) {
    refreshFlameImage(false, false, 1);
  }

  public void loadFlameButton_actionPerformed(ActionEvent e) {
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
        if (data.layerAppendBtn.isSelected() && getCurrFlame() != null) {
          if (appendToFlame(flame)) {
            refreshUI();
            messageHelper.showStatusMessage(getCurrFlame(), "opened from disc and added as new layers");
          }
        }
        else {
          setCurrFlame(flame);
          getCurrFlame().setLastFilename(file.getName());
          undoManager.initUndoStack(getCurrFlameStack());

          for (int i = flames.size() - 1; i >= 1; i--) {
            randomBatch.add(0, new FlameThumbnail(flames.get(i), null));
          }
          updateThumbnails();
          setupProfiles(getCurrFlame());
          refreshUI();
          messageHelper.showStatusMessage(getCurrFlame(), "opened from disc");
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private boolean appendToFlame(Flame pSrc) {
    Flame destFlame = getCurrFlame();
    for (Layer layer : pSrc.getLayers()) {
      destFlame.getLayers().add(layer);
    }
    return pSrc.getLayers().size() > 0;
  }

  public void randomPaletteButton_actionPerformed(ActionEvent e) {
    if (getCurrFlame() != null) {
      RandomGradientGenerator gradientGen = RandomGradientGeneratorList.getRandomGradientGeneratorInstance((String) data.paletteRandomGeneratorCmb.getSelectedItem());
      data.paletteKeyFrames = gradientGen.generateKeyFrames(Integer.parseInt(data.paletteRandomPointsREd.getText()));
      refreshPaletteColorsTable();
      RGBPalette palette = RandomGradientGenerator.generatePalette(data.paletteKeyFrames, data.paletteFadeColorsCBx.isSelected());
      saveUndoPoint();
      getCurrLayer().setPalette(palette);
      setLastGradient(palette);
      refreshPaletteUI(palette);
      refreshFlameImage(false);
    }
  }

  public void grabPaletteFromFlameButton_actionPerformed(ActionEvent e) {
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
      RGBPalette palette = flame.getFirstLayer().getPalette();
      data.paletteKeyFrames = null;
      saveUndoPoint();
      getCurrLayer().setPalette(palette);
      setLastGradient(palette);
      refreshPaletteColorsTable();
      refreshPaletteUI(palette);
      refreshFlameImage(false);
    }
  }

  public void paletteRedREd_changed() {
    paletteTextFieldChanged(data.paletteRedSlider, data.paletteRedREd, "modRed", 1.0);
  }

  public void paletteSaturationREd_changed() {
    paletteTextFieldChanged(data.paletteSaturationSlider, data.paletteSaturationREd, "modSaturation", 1.0);
  }

  public void paletteBlueREd_changed() {
    paletteTextFieldChanged(data.paletteBlueSlider, data.paletteBlueREd, "modBlue", 1.0);
  }

  public void paletteBlueSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteBlueSlider, data.paletteBlueREd, "modBlue", 1.0);
  }

  public void paletteBrightnessSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteBrightnessSlider, data.paletteBrightnessREd, "modBrightness", 1.0);
  }

  public void paletteBrightnessREd_changed() {
    paletteTextFieldChanged(data.paletteBrightnessSlider, data.paletteBrightnessREd, "modBrightness", 1.0);
  }

  public void paletteContrastREd_changed() {
    paletteTextFieldChanged(data.paletteContrastSlider, data.paletteContrastREd, "modContrast", 1.0);
  }

  public void paletteGreenREd_changed() {
    paletteTextFieldChanged(data.paletteGreenSlider, data.paletteGreenREd, "modGreen", 1.0);
  }

  public void paletteGreenSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteGreenSlider, data.paletteGreenREd, "modGreen", 1.0);
  }

  public void paletteHueREd_changed() {
    paletteTextFieldChanged(data.paletteHueSlider, data.paletteHueREd, "modHue", 1.0);
  }

  public void paletteGammaSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteGammaSlider, data.paletteGammaREd, "modGamma", 1.0);
  }

  public void paletteGammaREd_changed() {
    paletteTextFieldChanged(data.paletteGammaSlider, data.paletteGammaREd, "modGamma", 1.0);
  }

  public void paletteRedSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteRedSlider, data.paletteRedREd, "modRed", 1.0);
  }

  public void paletteContrastSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteContrastSlider, data.paletteContrastREd, "modContrast", 1.0);
  }

  public void paletteSaturationSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteSaturationSlider, data.paletteSaturationREd, "modSaturation", 1.0);
  }

  public void paletteShiftREd_changed() {
    paletteTextFieldChanged(data.paletteShiftSlider, data.paletteShiftREd, "modShift", 100.0);
  }

  public void paletteHueSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteHueSlider, data.paletteHueREd, "modHue", 1.0);
  }

  public void paletteShiftSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteShiftSlider, data.paletteShiftREd, "modShift", 100.0);
  }

  public void saveFlameButton_actionPerformed(ActionEvent e) {
    try {
      if (getCurrFlame() != null) {
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
          new FlameWriter().writeFlame(getCurrFlame(), file.getAbsolutePath());
          getCurrFlame().setLastFilename(file.getName());
          messageHelper.showStatusMessage(getCurrFlame(), "flame saved to disc");
          prefs.setLastOutputFlameFile(file);
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  enum XFormType {
    NORMAL, FINAL, BOTH
  }

  public XForm getCurrXForm() {
    return getCurrXForm(XFormType.BOTH);
  }

  public XForm getCurrXForm(XFormType pXFormType) {
    if (getCurrLayer() != null) {
      int row = data.transformationsTable.getSelectedRow();
      if (row >= 0 && row < getCurrLayer().getXForms().size() && (pXFormType == XFormType.BOTH || pXFormType == XFormType.NORMAL)) {
        return getCurrLayer().getXForms().get(row);
      }
      if (row >= getCurrLayer().getXForms().size() && row < (getCurrLayer().getXForms().size() + getCurrLayer().getFinalXForms().size()) && (pXFormType == XFormType.BOTH || pXFormType == XFormType.FINAL)) {
        return getCurrLayer().getFinalXForms().get(row - getCurrLayer().getXForms().size());
      }
    }
    return null;
  }

  public void transformationTableClicked() {
    if (!gridRefreshing) {
      boolean oldGridRefreshing = gridRefreshing;
      boolean oldCmbRefreshing = cmbRefreshing;
      gridRefreshing = cmbRefreshing = true;
      try {
        XForm xForm = getCurrXForm();
        if (flamePanel != null) {
          flamePanel.setSelectedXForm(xForm);
        }
        refreshXFormUI(xForm);
        xFormControls.enableControls(xForm);
        refreshFlameImage(false);
      }
      finally {
        cmbRefreshing = oldCmbRefreshing;
        gridRefreshing = oldGridRefreshing;
      }
    }
  }

  public void relWeightsTableClicked() {
    if (!gridRefreshing) {
      boolean oldGridRefreshing = gridRefreshing;
      boolean oldCmbRefreshing = cmbRefreshing;
      gridRefreshing = cmbRefreshing = true;
      try {
        Layer layer = getCurrLayer();
        int xaosRow = data.relWeightsTable.getSelectedRow();
        int transformRow = data.transformationsTable.getSelectedRow();
        if (layer != null && xaosRow >= 0 && xaosRow < layer.getXForms().size() && transformRow >= 0 && transformRow < layer.getXForms().size()) {
          if (isXaosViewAsTo()) {
            data.relWeightREd.setText(Tools.doubleToString(layer.getXForms().get(transformRow).getModifiedWeights()[xaosRow]));
          }
          else {
            data.relWeightREd.setText(Tools.doubleToString(layer.getXForms().get(xaosRow).getModifiedWeights()[transformRow]));
          }
        }
        else {
          data.relWeightREd.setText("");
        }
        xFormControls.enableRelWeightsControls();
      }
      finally {
        cmbRefreshing = oldCmbRefreshing;
        gridRefreshing = oldGridRefreshing;
      }
    }
  }

  private void enableControls() {
    boolean enabled = getCurrFlame() != null;
    data.editTransformCaptionButton.setEnabled(enabled);
    data.editFlameTileButton.setEnabled(enabled);
    data.snapShotButton.setEnabled(enabled);
    data.qSaveButton.setEnabled(enabled);
    data.quickMutationButton.setEnabled(enabled);
    data.bokehButton.setEnabled(enabled);
    data.dancingFlamesButton.setEnabled(enabled);
    data.movieButton.setEnabled(enabled);
    data.transformSlowButton.setEnabled(enabled);
    data.transparencyButton.setEnabled(enabled);
    data.randomizeButton.setEnabled(enabled);
    data.resetCameraSettingsBtn.setEnabled(enabled);
    data.resetDOFSettingsButton.setEnabled(enabled);
    data.resetBokehOptionsButton.setEnabled(enabled);
    data.resetColoringOptionsButton.setEnabled(enabled);
    data.resetAntialiasOptionsButton.setEnabled(enabled);
    data.resetShadingSettingsBtn.setEnabled(enabled);
    data.resetStereo3DSettingsBtn.setEnabled(enabled);
    data.resetPostSymmetrySettingsBtn.setEnabled(enabled);
    data.resetMotionBlurSettingsBtn.setEnabled(enabled);
    flameControls.enableControls();
    gradientControls.enableControls();
    channelMixerControls.enableControls();
    enableUndoControls();
    getBatchRendererController().enableJobRenderControls();
    getAnimationController().enableControls();
    getJwfScriptController().enableControls();
    getGradientController().enableControls();
    getFlameBrowserController().enableControls();
    getMeshGenController().enableControls();
  }

  private void refreshLayerControls(Layer pLayer) {
    boolean oldRefreshing = refreshing;
    boolean oldGridRefreshing = gridRefreshing;
    boolean oldCmbRefreshing = cmbRefreshing;
    boolean oldNoRefresh = noRefresh;
    gridRefreshing = cmbRefreshing = refreshing = noRefresh = true;
    try {
      if (pLayer == null) {
        data.layerWeightEd.setText(null);
        data.layerVisibleBtn.setSelected(true);
        data.gradientColorMapHorizOffsetREd.setText(null);
        data.gradientColorMapHorizOffsetSlider.setValue(0);
        data.gradientColorMapHorizScaleREd.setText(null);
        data.gradientColorMapHorizScaleSlider.setValue(0);
        data.gradientColorMapVertOffsetREd.setText(null);
        data.gradientColorMapVertOffsetSlider.setValue(0);
        data.gradientColorMapVertScaleREd.setText(null);
        data.gradientColorMapVertScaleSlider.setValue(0);
        data.gradientColorMapLocalColorAddREd.setText(null);
        data.gradientColorMapLocalColorAddSlider.setValue(0);
        data.gradientColorMapLocalColorScaleREd.setText(null);
        data.gradientColorMapLocalColorScaleSlider.setValue(0);
      }
      else {
        data.layerWeightEd.setValue(pLayer.getWeight());
        data.layerVisibleBtn.setSelected(pLayer.isVisible());
        data.gradientColorMapHorizOffsetREd.setText(Tools.doubleToString(pLayer.getGradientMapHorizOffset()));
        data.gradientColorMapHorizOffsetSlider.setValue(Tools.FTOI(pLayer.getGradientMapHorizOffset() * TinaController.SLIDER_SCALE_CENTRE));
        data.gradientColorMapHorizScaleREd.setText(Tools.doubleToString(pLayer.getGradientMapHorizScale()));
        data.gradientColorMapHorizScaleSlider.setValue(Tools.FTOI(pLayer.getGradientMapHorizScale() * TinaController.SLIDER_SCALE_CENTRE));
        data.gradientColorMapVertOffsetREd.setText(Tools.doubleToString(pLayer.getGradientMapVertOffset()));
        data.gradientColorMapVertOffsetSlider.setValue(Tools.FTOI(pLayer.getGradientMapVertOffset() * TinaController.SLIDER_SCALE_CENTRE));
        data.gradientColorMapVertScaleREd.setText(Tools.doubleToString(pLayer.getGradientMapVertScale()));
        data.gradientColorMapVertScaleSlider.setValue(Tools.FTOI(pLayer.getGradientMapVertScale() * TinaController.SLIDER_SCALE_CENTRE));
        data.gradientColorMapLocalColorAddREd.setText(Tools.doubleToString(pLayer.getGradientMapLocalColorAdd()));
        data.gradientColorMapLocalColorAddSlider.setValue(Tools.FTOI(pLayer.getGradientMapLocalColorAdd() * TinaController.SLIDER_SCALE_CENTRE));
        data.gradientColorMapLocalColorScaleREd.setText(Tools.doubleToString(pLayer.getGradientMapLocalColorScale()));
        data.gradientColorMapLocalColorScaleSlider.setValue(Tools.FTOI(pLayer.getGradientMapLocalColorScale() * TinaController.SLIDER_SCALE_CENTRE));
      }
    }
    finally {
      gridRefreshing = oldGridRefreshing;
      refreshing = oldRefreshing;
      cmbRefreshing = oldCmbRefreshing;
      noRefresh = oldNoRefresh;
    }
  }

  private void refreshXFormUI(XForm pXForm) {
    boolean oldRefreshing = refreshing;
    boolean oldGridRefreshing = gridRefreshing;
    boolean oldCmbRefreshing = cmbRefreshing;
    boolean oldNoRefresh = noRefresh;
    gridRefreshing = cmbRefreshing = refreshing = noRefresh = true;
    try {
      data.affineEditPlaneCmb.setSelectedItem(getCurrFlame().getEditPlane());
      if (pXForm != null) {
        if (data.affineEditPostTransformButton.isSelected()) {
          data.affineC00REd.setText(Tools.doubleToString(pXForm.getPostCoeff00()));
          data.affineC01REd.setText(Tools.doubleToString(pXForm.getPostCoeff01()));
          data.affineC10REd.setText(Tools.doubleToString(pXForm.getPostCoeff10()));
          data.affineC11REd.setText(Tools.doubleToString(pXForm.getPostCoeff11()));
          data.affineC20REd.setText(Tools.doubleToString(pXForm.getPostCoeff20()));
          data.affineC21REd.setText(Tools.doubleToString(pXForm.getPostCoeff21()));
        }
        else {
          data.affineC00REd.setText(Tools.doubleToString(pXForm.getCoeff00()));
          data.affineC01REd.setText(Tools.doubleToString(pXForm.getCoeff01()));
          data.affineC10REd.setText(Tools.doubleToString(pXForm.getCoeff10()));
          data.affineC11REd.setText(Tools.doubleToString(pXForm.getCoeff11()));
          data.affineC20REd.setText(Tools.doubleToString(pXForm.getCoeff20()));
          data.affineC21REd.setText(Tools.doubleToString(pXForm.getCoeff21()));
        }

        data.xFormColorREd.setText(Tools.doubleToString(pXForm.getColor()));
        data.xFormColorSlider.setValue(Tools.FTOI(pXForm.getColor() * SLIDER_SCALE_COLOR));
        data.xFormSymmetryREd.setText(Tools.doubleToString(pXForm.getColorSymmetry()));
        data.xFormSymmetrySlider.setValue(Tools.FTOI(pXForm.getColorSymmetry() * SLIDER_SCALE_COLOR));

        data.xFormModGammaREd.setText(Tools.doubleToString(pXForm.getModGamma()));
        data.xFormModGammaSlider.setValue(Tools.FTOI(pXForm.getModGamma() * SLIDER_SCALE_COLOR));
        data.xFormModGammaSpeedREd.setText(Tools.doubleToString(pXForm.getModGammaSpeed()));
        data.xFormModGammaSpeedSlider.setValue(Tools.FTOI(pXForm.getModGammaSpeed() * SLIDER_SCALE_COLOR));

        data.xFormModContrastREd.setText(Tools.doubleToString(pXForm.getModContrast()));
        data.xFormModContrastSlider.setValue(Tools.FTOI(pXForm.getModContrast() * SLIDER_SCALE_COLOR));
        data.xFormModContrastSpeedREd.setText(Tools.doubleToString(pXForm.getModContrastSpeed()));
        data.xFormModContrastSpeedSlider.setValue(Tools.FTOI(pXForm.getModContrastSpeed() * SLIDER_SCALE_COLOR));

        data.xFormModSaturationREd.setText(Tools.doubleToString(pXForm.getModSaturation()));
        data.xFormModSaturationSlider.setValue(Tools.FTOI(pXForm.getModSaturation() * SLIDER_SCALE_COLOR));
        data.xFormModSaturationSpeedREd.setText(Tools.doubleToString(pXForm.getModSaturationSpeed()));
        data.xFormModSaturationSpeedSlider.setValue(Tools.FTOI(pXForm.getModSaturationSpeed() * SLIDER_SCALE_COLOR));

        data.xFormOpacityREd.setText(Tools.doubleToString(pXForm.getOpacity()));
        data.xFormOpacitySlider.setValue(Tools.FTOI(pXForm.getOpacity() * SLIDER_SCALE_COLOR));
        data.xFormDrawModeCmb.setSelectedItem(pXForm.getDrawMode());

        data.transformationWeightREd.setText(Tools.doubleToString(pXForm.getWeight()));
      }
      else {
        data.affineC00REd.setText(null);
        data.affineC01REd.setText(null);
        data.affineC10REd.setText(null);
        data.affineC11REd.setText(null);
        data.affineC20REd.setText(null);
        data.affineC21REd.setText(null);
        data.xFormColorREd.setText(null);
        data.xFormColorSlider.setValue(0);
        data.xFormSymmetryREd.setText(null);
        data.xFormSymmetrySlider.setValue(0);
        data.xFormModGammaREd.setText(null);
        data.xFormModGammaSlider.setValue(0);
        data.xFormModGammaSpeedREd.setText(null);
        data.xFormModGammaSpeedSlider.setValue(0);
        data.xFormModContrastREd.setText(null);
        data.xFormModContrastSlider.setValue(0);
        data.xFormModContrastSpeedREd.setText(null);
        data.xFormModContrastSpeedSlider.setValue(0);
        data.xFormModSaturationREd.setText(null);
        data.xFormModSaturationSlider.setValue(0);
        data.xFormModSaturationSpeedREd.setText(null);
        data.xFormModSaturationSpeedSlider.setValue(0);
        data.xFormOpacityREd.setText(null);
        data.xFormOpacitySlider.setValue(0);
        data.transformationWeightREd.setText(null);
        data.xFormDrawModeCmb.setSelectedIndex(-1);
      }

      {
        int idx = 0;
        for (TinaNonlinearControlsRow row : data.TinaNonlinearControlsRows) {
          if (pXForm == null || idx >= pXForm.getVariationCount()) {
            refreshParamCmb(row, null, null);
            row.getNonlinearParamsLeftButton().setEnabled(false);
          }
          else {
            Variation var = pXForm.getVariation(idx);
            refreshParamCmb(row, pXForm, var);
          }
          idx++;
        }
      }

      refreshRelWeightsTable();
    }
    finally {
      gridRefreshing = oldGridRefreshing;
      refreshing = oldRefreshing;
      cmbRefreshing = oldCmbRefreshing;
      noRefresh = oldNoRefresh;
    }
  }

  public void refreshParamCmb(TinaNonlinearControlsRow pRow, XForm pXForm, Variation pVar) {
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
      // ressources
      int resCount = 0;
      String[] resNames = varFunc.getRessourceNames();
      if (resNames != null) {
        for (String name : resNames) {
          pRow.getNonlinearParamsCmb().addItem(name);
          resCount++;
        }
      }
      // params      
      String[] paramNames = varFunc.getParameterNames();
      if (paramNames != null) {
        for (String name : paramNames) {
          pRow.getNonlinearParamsCmb().addItem(name);
        }
      }
      // preselection
      if (resCount > 0) {
        pRow.getNonlinearParamsCmb().setSelectedIndex(0);
        enableNonlinearControls(pRow, true);
      }
      else if (varFunc.getParameterNames().length > 0) {
        pRow.getNonlinearParamsCmb().setSelectedIndex(0);
        Object val = varFunc.getParameterValues()[0];
        if (val instanceof Double) {
          pRow.getNonlinearParamsREd().setText(Tools.doubleToString((Double) val));
        }
        else {
          pRow.getNonlinearParamsREd().setText(val.toString());
        }
        enableNonlinearControls(pRow, false);
      }
      else {
        pRow.getNonlinearParamsCmb().setSelectedIndex(-1);
        pRow.getNonlinearParamsREd().setText(null);
      }
    }
  }

  public void addLinkedXForm() {
    int row = data.transformationsTable.getSelectedRow();
    if (row < 0 || row >= getCurrLayer().getXForms().size()) {
      return;
    }
    saveUndoPoint();
    addXForm();
    int fromId = row;
    int toId = getCurrLayer().getXForms().size() - 1;
    for (int i = 0; i < getCurrLayer().getXForms().size(); i++) {
      XForm xForm = getCurrLayer().getXForms().get(i);
      if (i == fromId) {
        for (int j = 0; j < getCurrLayer().getXForms().size(); j++) {
          xForm.getModifiedWeights()[j] = (j == toId) ? 1 : 0;
        }
      }
      else {
        xForm.getModifiedWeights()[toId] = 0;
      }
    }

  }

  public void addXForm() {
    XForm xForm = new XForm();
    xForm.addVariation(1.0, new Linear3DFunc());
    xForm.setWeight(0.5);
    saveUndoPoint();
    getCurrLayer().getXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = getCurrLayer().getXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(false);
  }

  public void duplicateXForm() {
    XForm xForm = new XForm();
    xForm.assign(getCurrXForm());
    saveUndoPoint();
    getCurrLayer().getXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = getCurrLayer().getXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(false);
  }

  public void deleteXForm() {
    int row = data.transformationsTable.getSelectedRow();
    saveUndoPoint();
    if (row >= getCurrLayer().getXForms().size()) {
      getCurrLayer().getFinalXForms().remove(row - getCurrLayer().getXForms().size());
    }
    else {
      getCurrLayer().getXForms().remove(getCurrXForm());
    }
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    refreshFlameImage(false);
  }

  public void addFinalXForm() {
    XForm xForm = new XForm();
    xForm.addVariation(1.0, new Linear3DFunc());
    saveUndoPoint();
    getCurrLayer().getFinalXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = getCurrLayer().getXForms().size() + getCurrLayer().getFinalXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(false);
  }

  private void forceTriangleMode() {
    if (flamePanel.getConfig().getMouseDragOperation() != MouseDragOperation.MOVE_TRIANGLE && flamePanel.getConfig().getMouseDragOperation() != MouseDragOperation.ROTATE_TRIANGLE && flamePanel.getConfig().getMouseDragOperation() != MouseDragOperation.SCALE_TRIANGLE && flamePanel.getConfig().getMouseDragOperation() != MouseDragOperation.POINTS) {
      flamePanel.getConfig().setMouseDragOperation(MouseDragOperation.MOVE_TRIANGLE);
      flamePanel.setDrawTriangles(true);
      data.mouseTransformMoveTrianglesButton.setSelected(true);
      data.mouseTransformRotateTrianglesButton.setSelected(false);
      data.mouseTransformScaleTrianglesButton.setSelected(false);
      data.mouseTransformEditPointsButton.setSelected(false);
      data.mouseTransformEditGradientButton.setSelected(false);
      data.mouseTransformEditViewButton.setSelected(false);
      data.mouseTransformEditFocusPointButton.setSelected(false);
      data.mouseTransformEditTriangleViewButton.setSelected(false);
    }
  }

  public void xForm_moveRight(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveHorizAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), amount, 0, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_rotateRight() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineRotateAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.rotate(getCurrXForm(), -amount, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_moveLeft(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveHorizAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), -amount, 0, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_flipHorizontal() {
    forceTriangleMode();
    saveUndoPoint();
    XFormTransformService.flipHorizontal(getCurrXForm(), data.affineEditPostTransformButton.isSelected());
    transformationTableClicked();
  }

  public void xForm_flipVertical() {
    forceTriangleMode();
    saveUndoPoint();
    XFormTransformService.flipVertical(getCurrXForm(), data.affineEditPostTransformButton.isSelected());
    transformationTableClicked();
  }

  public void xForm_enlarge() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineScaleAmountREd.getText()) / 100.0;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.scale(getCurrXForm(), amount, data.affineScaleXButton.isSelected(), data.affineScaleYButton.isSelected(), data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_shrink() {
    forceTriangleMode();
    double amount = 100.0 / Tools.stringToDouble(data.affineScaleAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.scale(getCurrXForm(), amount, data.affineScaleXButton.isSelected(), data.affineScaleYButton.isSelected(), data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_rotateLeft() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineRotateAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.rotate(getCurrXForm(), amount, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_moveUp(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveVertAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), 0, -amount, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void gradientMarker_move(int pIdx, int pDeltaPos) {
    getFlamePanel().gradientMarker_move(pIdx, pDeltaPos);
    transformationTableClicked();
  }

  public void gradientMarker_selectColor(int pIdx) {
    if (getFlamePanel().gradientMarker_selectColor(pIdx)) {
      transformationTableClicked();
      refreshPaletteImg();
    }
  }

  public void xForm_moveDown(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveVertAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), 0, amount, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  private List<FlameThumbnail> randomBatch = new ArrayList<FlameThumbnail>();

  public List<FlameThumbnail> getRandomBatch() {
    return randomBatch;
  }

  public void updateThumbnails() {
    if (data.randomBatchScrollPane != null) {
      data.randomBatchPanel.remove(data.randomBatchScrollPane);
      data.randomBatchScrollPane = null;
    }
    int panelWidth = FlameThumbnail.IMG_WIDTH + 2 * FlameThumbnail.BORDER_SIZE;
    int panelHeight = (FlameThumbnail.IMG_HEIGHT + FlameThumbnail.BORDER_SIZE) * randomBatch.size();
    JPanel batchPanel = new JPanel();
    batchPanel.setLayout(null);
    batchPanel.setSize(panelWidth, panelHeight);
    batchPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    for (int i = 0; i < randomBatch.size(); i++) {
      SimpleImage img = randomBatch.get(i).getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
      // add it to the main panel
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(FlameThumbnail.BORDER_SIZE, i * FlameThumbnail.IMG_HEIGHT + (i + 1) * FlameThumbnail.BORDER_SIZE);
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
    data.randomBatchScrollPane = new JScrollPane(batchPanel);
    data.randomBatchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    data.randomBatchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    data.randomBatchPanel.add(data.randomBatchScrollPane, BorderLayout.CENTER);
    data.randomBatchPanel.validate();
  }

  public boolean createRandomBatch(int pCount, String pGeneratorname, String pSymmetryGeneratorname, String pGradientGeneratorname, RandomBatchQuality pQuality) {
    if (!confirmNewRandomBatch(pGeneratorname))
      return false;
    if (prefs.getTinaRandomBatchRefreshType() == RandomBatchRefreshType.CLEAR) {
      randomBatch.clear();
    }
    int imgCount = prefs.getTinaRandomBatchSize();
    List<SimpleImage> imgList = new ArrayList<SimpleImage>();
    int maxCount = (pCount > 0 ? pCount : imgCount);
    mainProgressUpdater.initProgress(maxCount);
    RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance(pGeneratorname, true);
    RandomSymmetryGenerator randSymmGen = RandomSymmetryGeneratorList.getRandomSymmetryGeneratorInstance(pSymmetryGeneratorname, true);
    RandomGradientGenerator randGradientGen = RandomGradientGeneratorList.getRandomGradientGeneratorInstance(pGradientGeneratorname, true);
    for (int i = 0; i < maxCount; i++) {
      int palettePoints = 7 + (int) (Math.random() * 24.0);
      boolean fadePaletteColors = Math.random() > 0.06;
      RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(FlameThumbnail.IMG_WIDTH / 2, FlameThumbnail.IMG_HEIGHT / 2, prefs, randGen, randSymmGen, randGradientGen, palettePoints, fadePaletteColors, pQuality);
      RandomFlameGeneratorSample sample = sampler.createSample();
      FlameThumbnail thumbnail;
      thumbnail = new FlameThumbnail(sample.getFlame(), null);
      SimpleImage img = thumbnail.getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
      if (prefs.getTinaRandomBatchRefreshType() == RandomBatchRefreshType.INSERT) {
        randomBatch.add(0, thumbnail);
        imgList.add(0, img);
      }
      else {
        randomBatch.add(thumbnail);
        imgList.add(img);
      }
      mainProgressUpdater.updateProgress(i + 1);
    }
    updateThumbnails();
    return true;
  }

  private static boolean commonsWarningDisplayed = false;

  private boolean confirmNewRandomBatch(String pGeneratorname) {
    Class<?> cls = RandomFlameGeneratorList.getGeneratorClassByName(pGeneratorname);
    if (WikimediaCommonsRandomFlameGenerator.class.equals(cls)) {
      if (!commonsWarningDisplayed && !prefs.isTinaDisableWikimediaCommonsWarning()) {
        HTMLInfoDialog dlg = new HTMLInfoDialog(SwingUtilities.getWindowAncestor(centerPanel), "Warning", "WikimediaCommonsRandomFlameGenerator.html");
        dlg.setModal(true);
        dlg.setVisible(true);
        boolean confirmed = dlg.isConfirmed();
        if (confirmed) {
          commonsWarningDisplayed = true;
        }
        return confirmed;
      }
    }
    return true;
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
    //    if (StandardDialogs.confirm(flamePanel, "Do you really want to remove this flame\n from the thumbnail-ribbon?\n (Please note that this cannot be undone)")) {
    randomBatch.remove(pIdx);
    updateThumbnails();
    //    }
  }

  public void importFromRandomBatch(int pIdx) {
    if (pIdx >= 0 && pIdx < randomBatch.size()) {
      setCurrFlame(randomBatch.get(pIdx).getFlame(), false);
      undoManager.initUndoStack(getCurrFlame());
      {
        FlamePanel imgPanel = getFlamePanel();
        Rectangle bounds = imgPanel.getImageBounds();
        double wScl = (double) bounds.width / (double) getCurrFlame().getWidth();
        double hScl = (double) bounds.height / (double) getCurrFlame().getHeight();
        getCurrFlame().setWidth(bounds.width);
        getCurrFlame().setHeight(bounds.height);
        getCurrFlame().setPixelsPerUnit((wScl + hScl) * 0.5 * getCurrFlame().getPixelsPerUnit());
      }
    }
  }

  public void nonlinearVarCmbChanged(int pIdx) {
    if (cmbRefreshing) {
      return;
    }
    boolean oldCmbRefreshing = cmbRefreshing;
    cmbRefreshing = true;
    try {
      XForm xForm = getCurrXForm();
      if (xForm != null) {
        saveUndoPoint();
        String fName = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearVarCmb().getSelectedItem();
        Variation var;
        if (pIdx < xForm.getVariationCount()) {
          var = xForm.getVariation(pIdx);
          if (fName == null || fName.length() == 0) {
            xForm.removeVariation(var);
          }
          else {
            if (var.getFunc() == null || !var.getFunc().getName().equals(fName)) {
              var.setFunc(VariationFuncList.getVariationFuncInstance(fName));
            }
          }
        }
        else {
          var = new Variation();
          String varStr = data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd().getText();
          if (varStr == null || varStr.length() == 0) {
            varStr = "0";
          }
          var.setFunc(VariationFuncList.getVariationFuncInstance(fName));
          var.setAmount(Tools.stringToDouble(varStr));
          xForm.addVariation(var);
        }
        refreshParamCmb(data.TinaNonlinearControlsRows[pIdx], xForm, var);
        refreshXFormUI(xForm);
        //        String selected = (String) TinaNonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
        //        boolean enabled = selected != null && selected.length() > 0;
        //        TinaNonlinearControlsRows[pIdx].getNonlinearParamsLeftButton().setEnabled(enabled);
        //        TinaNonlinearControlsRows[pIdx].getNonlinearParamsRightButton().setEnabled(enabled);
        refreshFlameImage(false);
        data.transformationsTable.invalidate();
        data.transformationsTable.repaint();
      }
    }
    finally {
      cmbRefreshing = oldCmbRefreshing;
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
        if (pIdx < xForm.getVariationCount()) {
          Variation var = xForm.getVariation(pIdx);
          String varStr = data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd().getText();
          if (varStr == null || varStr.length() == 0) {
            varStr = "0";
          }
          var.setAmount(Tools.stringToDouble(varStr) + pDelta);
          data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd().setText(Tools.doubleToString(var.getAmount()));
          refreshFlameImage(false);
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
      String selected = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
      XForm xForm = getCurrXForm();
      if (xForm != null && selected != null && selected.length() > 0) {
        saveUndoPoint();
        if (pIdx < xForm.getVariationCount()) {
          final Variation var = xForm.getVariation(pIdx);
          int idx;
          if ((idx = var.getFunc().getParameterIndex(selected)) >= 0) {
            String valStr = data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().getText();
            if (valStr == null || valStr.length() == 0) {
              valStr = "0";
            }
            // round the delta to whole numbers if the parameter is of type integer
            if (Math.abs(pDelta) > MathLib.EPSILON) {
              Object val = var.getFunc().getParameterValues()[idx];
              if (val != null && val instanceof Integer) {
                if (Math.abs(pDelta) < 1.0) {
                  pDelta = pDelta < 0 ? -1 : 1;
                }
                else {
                  pDelta = Math.round(pDelta);
                }
              }
            }

            double val = Tools.stringToDouble(valStr) + pDelta;
            var.getFunc().setParameter(selected, val);
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(Tools.doubleToString(val));
          }
          else if ((idx = var.getFunc().getRessourceIndex(selected)) >= 0) {
            final String rName = var.getFunc().getRessourceNames()[idx];
            RessourceType resType = var.getFunc().getRessourceType(rName);
            switch (resType) {
              case FONT_NAME: {
                String oldFontname = null;
                {
                  byte val[] = var.getFunc().getRessourceValues()[idx];
                  if (val != null) {
                    oldFontname = new String(val);
                  }
                }
                Font oldFont = new Font(oldFontname != null ? oldFontname : "Arial", Font.PLAIN, 24);
                Font selectedFont = JFontChooser.showDialog(centerPanel, "Choose font", oldFont);
                if (selectedFont != null) {
                  String valStr = selectedFont.getFontName();
                  byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                  var.getFunc().setRessource(rName, valByteArray);
                }
              }
                break;
              case IMAGE_FILENAME: {
                String oldFilename = null;
                {
                  byte val[] = var.getFunc().getRessourceValues()[idx];
                  if (val != null) {
                    oldFilename = new String(val);
                  }
                }
                JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
                if (oldFilename != null && oldFilename.length() > 0) {
                  try {
                    chooser.setCurrentDirectory(new File(oldFilename).getAbsoluteFile().getParentFile());
                    chooser.setSelectedFile(new File(oldFilename));
                  }
                  catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
                else {
                  if (prefs.getInputImagePath() != null) {
                    try {
                      chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
                    }
                    catch (Exception ex) {
                      ex.printStackTrace();
                    }
                  }
                }
                if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
                  File file = chooser.getSelectedFile();
                  String valStr = file.getAbsolutePath();
                  byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                  var.getFunc().setRessource(rName, valByteArray);
                }
              }
                break;

              case IMAGE_FILE: {
                JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
                if (prefs.getInputImagePath() != null) {
                  try {
                    chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
                  }
                  catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
                if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
                  try {
                    File file = chooser.getSelectedFile();
                    byte[] imgData = Tools.readFile(file.getAbsolutePath());
                    var.getFunc().setRessource(rName, imgData);
                  }
                  catch (Exception ex) {
                    errorHandler.handleError(ex);
                  }
                }
              }
                break;
              case SVG_FILE: {
                JFileChooser chooser = new SvgFileChooser(prefs);
                if (prefs.getTinaSVGPath() != null) {
                  try {
                    chooser.setCurrentDirectory(new File(prefs.getTinaSVGPath()));
                  }
                  catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
                if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
                  try {
                    File file = chooser.getSelectedFile();
                    String svg = Tools.readUTF8Textfile(file.getAbsolutePath());
                    byte[] valByteArray = svg != null ? svg.getBytes() : null;
                    var.getFunc().setRessource(rName, valByteArray);
                  }
                  catch (Exception ex) {
                    errorHandler.handleError(ex);
                  }
                }
              }
                break;
              default: {
                final RessourceDialog dlg = new RessourceDialog(SwingUtilities.getWindowAncestor(centerPanel), prefs, errorHandler);
                dlg.setRessourceName(rName);
                byte val[] = var.getFunc().getRessourceValues()[idx];
                if (val != null) {
                  dlg.setRessourceValue(new String(val));
                }
                dlg.addValidation(new RessourceValidation() {
                  @Override
                  public void validate() {
                    String valStr = dlg.getRessourceValue();
                    byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                    byte[] oldValue = var.getFunc().getRessource(rName);
                    try {
                      var.getFunc().setRessource(rName, valByteArray);
                      var.getFunc().validate();
                    }
                    catch (Throwable ex) {
                      var.getFunc().setRessource(rName, oldValue);
                      throw new RuntimeException(ex);
                    }
                  }
                });

                dlg.setModal(true);
                dlg.setVisible(true);

                if (dlg.isConfirmed()) {
                  try {
                    String valStr = dlg.getRessourceValue();
                    byte[] valByteArray = valStr != null ? valStr.getBytes() : null;
                    var.getFunc().setRessource(rName, valByteArray);
                  }
                  catch (Throwable ex) {
                    errorHandler.handleError(ex);
                  }
                }
              }
            }
          }
          refreshFlameImage(false);
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
      String selected = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
      XForm xForm = getCurrXForm();
      if (xForm != null && selected != null && selected.length() > 0) {
        if (pIdx < xForm.getVariationCount()) {
          Variation var = xForm.getVariation(pIdx);
          // params
          int idx;
          if ((idx = var.getFunc().getParameterIndex(selected)) >= 0) {
            enableNonlinearControls(data.TinaNonlinearControlsRows[pIdx], false);
            Object val = var.getFunc().getParameterValues()[idx];
            if (val instanceof Double) {
              data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(Tools.doubleToString((Double) val));
            }
            else {
              data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(val.toString());
            }
          }
          // ressources
          else if ((idx = var.getFunc().getRessourceIndex(selected)) >= 0) {
            enableNonlinearControls(data.TinaNonlinearControlsRows[pIdx], true);
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(null);
          }
          // empty
          else {
            data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(null);
          }
        }
      }
    }
    finally {
      cmbRefreshing = false;
    }
  }

  private void enableNonlinearControls(TinaNonlinearControlsRow pRow, boolean pRessource) {
    String selected = (String) pRow.getNonlinearParamsCmb().getSelectedItem();
    boolean enabled = selected != null && selected.length() > 0;
    pRow.getNonlinearParamsLeftButton().setEnabled(enabled && pRessource);
    pRow.getNonlinearParamsREd().setEnabled(enabled && !pRessource);
  }

  private final double DELTA_PARAM = 0.1;

  public void nonlinearParamsLeftButtonClicked(int pIdx) {
    nonlinearParamsREdChanged(pIdx, -DELTA_PARAM);
  }

  public void nonlinearParamsRightButtonClicked(int pIdx) {
    nonlinearParamsREdChanged(pIdx, DELTA_PARAM);
  }

  public void xFormSymmetrySlider_changed() {
    xFormSliderChanged(data.xFormSymmetrySlider, data.xFormSymmetryREd, "colorSymmetry", SLIDER_SCALE_COLOR);
  }

  public void xFormOpacityREd_changed() {
    xFormTextFieldChanged(data.xFormOpacitySlider, data.xFormOpacityREd, "opacity", SLIDER_SCALE_COLOR);
  }

  public void xFormOpacitySlider_changed() {
    xFormSliderChanged(data.xFormOpacitySlider, data.xFormOpacityREd, "opacity", SLIDER_SCALE_COLOR);
  }

  public void xFormDrawModeCmb_changed() {
    if (!cmbRefreshing) {
      XForm xForm = getCurrXForm();
      if (xForm != null && data.xFormDrawModeCmb.getSelectedItem() != null) {
        xForm.setDrawMode((DrawMode) data.xFormDrawModeCmb.getSelectedItem());
        refreshFlameImage(false);
        xFormControls.enableControls(xForm);
      }
    }
  }

  public void xFormColorSlider_changed() {
    xFormSliderChanged(data.xFormColorSlider, data.xFormColorREd, "color", SLIDER_SCALE_COLOR);
  }

  public void xFormSymmetryREd_changed() {
    xFormTextFieldChanged(data.xFormSymmetrySlider, data.xFormSymmetryREd, "colorSymmetry", SLIDER_SCALE_COLOR);
  }

  public void xFormColorREd_changed() {
    xFormTextFieldChanged(data.xFormColorSlider, data.xFormColorREd, "color", SLIDER_SCALE_COLOR);
  }

  public void xFormModGammaREd_changed() {
    xFormTextFieldChanged(data.xFormModGammaSlider, data.xFormModGammaREd, "modGamma", SLIDER_SCALE_COLOR);
  }

  public void xFormModGammaSlider_changed() {
    xFormSliderChanged(data.xFormModGammaSlider, data.xFormModGammaREd, "modGamma", SLIDER_SCALE_COLOR);
  }

  public void xFormModGammaSpeedREd_changed() {
    xFormTextFieldChanged(data.xFormModGammaSpeedSlider, data.xFormModGammaSpeedREd, "modGammaSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModGammaSpeedSlider_changed() {
    xFormSliderChanged(data.xFormModGammaSpeedSlider, data.xFormModGammaSpeedREd, "modGammaSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModContrastREd_changed() {
    xFormTextFieldChanged(data.xFormModContrastSlider, data.xFormModContrastREd, "modContrast", SLIDER_SCALE_COLOR);
  }

  public void xFormModContrastSlider_changed() {
    xFormSliderChanged(data.xFormModContrastSlider, data.xFormModContrastREd, "modContrast", SLIDER_SCALE_COLOR);
  }

  public void xFormModContrastSpeedREd_changed() {
    xFormTextFieldChanged(data.xFormModContrastSpeedSlider, data.xFormModContrastSpeedREd, "modContrastSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModContrastSpeedSlider_changed() {
    xFormSliderChanged(data.xFormModContrastSpeedSlider, data.xFormModContrastSpeedREd, "modContrastSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModSaturationREd_changed() {
    xFormTextFieldChanged(data.xFormModSaturationSlider, data.xFormModSaturationREd, "modSaturation", SLIDER_SCALE_COLOR);
  }

  public void xFormModSaturationSlider_changed() {
    xFormSliderChanged(data.xFormModSaturationSlider, data.xFormModSaturationREd, "modSaturation", SLIDER_SCALE_COLOR);
  }

  public void xFormModSaturationSpeedREd_changed() {
    xFormTextFieldChanged(data.xFormModSaturationSpeedSlider, data.xFormModSaturationSpeedREd, "modSaturationSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModSaturationSpeedSlider_changed() {
    xFormSliderChanged(data.xFormModSaturationSpeedSlider, data.xFormModSaturationSpeedREd, "modSaturationSpeed", SLIDER_SCALE_COLOR);
  }

  private void setRelWeight(double pValue) {
    if (gridRefreshing)
      return;
    Layer layer = getCurrLayer();
    int transformRow = data.transformationsTable.getSelectedRow();
    int xaosRow = data.relWeightsTable.getSelectedRow();
    if (layer != null && transformRow >= 0 && transformRow < layer.getXForms().size() && xaosRow >= 0 && xaosRow < layer.getXForms().size()) {
      if (isXaosViewAsTo()) {
        layer.getXForms().get(transformRow).getModifiedWeights()[xaosRow] = pValue;
      }
      else {
        layer.getXForms().get(xaosRow).getModifiedWeights()[transformRow] = pValue;
      }
      gridRefreshing = true;
      try {
        refreshRelWeightsTable();
        data.relWeightsTable.getSelectionModel().setSelectionInterval(xaosRow, xaosRow);
        refreshFlameImage(false);
      }
      finally {
        gridRefreshing = false;
      }
    }
  }

  public void relWeightsREd_changed() {
    setRelWeight(Tools.stringToDouble(data.relWeightREd.getText()));
  }

  public void relWeightsZeroButton_clicked() {
    setRelWeight(0.0);
    relWeightsTableClicked();
  }

  public void relWeightsOneButton_clicked() {
    setRelWeight(1.0);
    relWeightsTableClicked();
  }

  public void transformationWeightREd_changed() {
    XForm xForm = getCurrXForm();
    if (!gridRefreshing && xForm != null && getCurrLayer() != null && getCurrLayer().getFinalXForms().indexOf(xForm) < 0) {
      xForm.setWeight(Tools.stringToDouble(data.transformationWeightREd.getText()));
      gridRefreshing = true;
      try {
        int row = data.transformationsTable.getSelectedRow();
        refreshTransformationsTable();
        data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
        refreshFlameImage(false);
      }
      finally {
        gridRefreshing = false;
      }
    }
  }

  public void newFlameButton_clicked() {
    Flame flame = new Flame();
    flame.setWidth(800);
    flame.setHeight(600);
    flame.setPixelsPerUnit(50);
    flame.setBGTransparency(prefs.isTinaDefaultBGTransparency());
    flame.setAntialiasAmount(prefs.getTinaDefaultAntialiasingAmount());
    flame.setAntialiasRadius(prefs.getTinaDefaultAntialiasingRadius());
    RandomGradientGenerator gradientGen = RandomGradientGeneratorList.getRandomGradientGeneratorInstance((String) data.paletteRandomGeneratorCmb.getSelectedItem());
    RGBPalette palette = gradientGen.generatePalette(Integer.parseInt(data.paletteRandomPointsREd.getText()), data.paletteFadeColorsCBx.isSelected());
    flame.getFirstLayer().setPalette(palette);
    setLastGradient(palette);
    setCurrFlame(flame);
    undoManager.initUndoStack(getCurrFlame());
  }

  public void renderModeCmb_changed() {
    if (!refreshing && !cmbRefreshing) {
      refreshFlameImage(false);
    }
  }

  @Override
  public Flame getFlame() {
    return getCurrFlame();
  }

  private void flamePanel_mouseDragged(MouseEvent e) {
    if (flamePanel != null) {
      int modifiers = e.getModifiers();
      boolean leftButton = (modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK;
      boolean middleButton = (modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK;
      boolean rightButton = (modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
      if (flamePanel.mouseDragged(e.getX(), e.getY(), leftButton, rightButton, middleButton)) {
        if (flamePanel.isReRender()) {
          refreshXFormUI(getCurrXForm());
          refreshFlameImage(true);
          refreshPaletteImg();
        }
        else {
          centerPanel.getParent().validate();
          centerPanel.repaint();
        }
      }
    }
  }

  private void flamePanel_mousePressed(MouseEvent e) {
    if (flamePanel != null) {
      flamePanel.mousePressed(e.getX(), e.getY());
    }
  }

  private void flamePanel_mouseReleased(MouseEvent e) {
    if (flamePanel != null) {
      flameControls.refreshVisualCamValues();
    }
  }

  private void flamePanel_mouseWheelEvent(MouseWheelEvent e) {
    if (flamePanel != null) {
      if (flamePanel.mouseWheelMoved(e.getWheelRotation())) {
        refreshXFormUI(getCurrXForm());
        refreshFlameImage(true);
        flameControls.refreshVisualCamValues();
      }
    }
  }

  private void afterTriangleSelected(XForm pXForm, int pRow) {
    boolean lastGridRefreshing = gridRefreshing;
    gridRefreshing = true;
    try {
      flamePanel.setSelectedXForm(pXForm);
      data.transformationsTable.getSelectionModel().setSelectionInterval(pRow, pRow);
      refreshXFormUI(pXForm);
      xFormControls.enableControls(pXForm);
      refreshFlameImage(false);
    }
    finally {
      gridRefreshing = lastGridRefreshing;
    }
  }

  private void flamePanel_mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      renderFlameButton_actionPerformed(null);
    }
    else if (e.getClickCount() == 1) {
      Flame flame = getCurrFlame();
      Layer layer = getCurrLayer();
      if (flame != null && flamePanel != null) {
        XForm xForm = flamePanel.mouseClicked(e.getX(), e.getY());
        if (xForm != null || flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
          for (int i = 0; i < layer.getXForms().size(); i++) {
            if (xForm == layer.getXForms().get(i)) {
              afterTriangleSelected(xForm, i);
              return;
            }
          }
          for (int i = 0; i < layer.getFinalXForms().size(); i++) {
            if (xForm == layer.getFinalXForms().get(i)) {
              int row = layer.getXForms().size() + i;
              afterTriangleSelected(xForm, row);
              return;
            }
          }
          if (flamePanel.isRedrawAfterMouseClick()) {
            if (flamePanel.isReRender()) {
              refreshFlameImage(false);
              refreshPaletteImg();
            }
            else {
              centerPanel.getParent().invalidate();
              centerPanel.getParent().validate();
              centerPanel.invalidate();
              centerPanel.validate();
              centerPanel.repaint();
            }
          }

        }
      }

    }
  }

  public void mouseTransformMoveTrianglesButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        data.mouseTransformScaleTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditGradientButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        data.mouseTransformEditTriangleViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setMouseDragOperation(data.mouseTransformMoveTrianglesButton.isSelected() ? MouseDragOperation.MOVE_TRIANGLE : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE);
          refreshFlameImage(false);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void mouseTransformEditFocusPointButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        data.mouseTransformScaleTrianglesButton.setSelected(false);
        data.mouseTransformMoveTrianglesButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditGradientButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        data.mouseTransformEditTriangleViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setMouseDragOperation(data.mouseTransformEditFocusPointButton.isSelected() ? MouseDragOperation.FOCUS : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(false);
          refreshFlameImage(false);
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
      xFormControls.setUpMotionControls();
      forceTriangleMode();
      XForm xForm = getCurrXForm();
      if (flamePanel != null) {
        flamePanel.setEditPostTransform(data.affineEditPostTransformButton.isSelected());
      }
      xFormControls.enableControls(xForm);
      refreshXFormUI(xForm);
      refreshFlameImage(false);
      data.affineEditPostTransformSmallButton.setSelected(data.affineEditPostTransformButton.isSelected());
    }
    finally {
      refreshing = false;
    }
  }

  public void affineEditPostTransformSmallButton_clicked() {
    refreshing = true;
    try {
      data.affineEditPostTransformButton.setSelected(data.affineEditPostTransformSmallButton.isSelected());
    }
    finally {
      refreshing = false;
    }
    affineEditPostTransformButton_clicked();
  }

  public void affineC21REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      double value = Tools.stringToDouble(data.affineC21REd.getText());
      if (data.affineEditPostTransformButton.isSelected()) {
        xForm.setPostCoeff21(value);
      }
      else {
        xForm.setCoeff21(value);
      }
      transformationTableClicked();
    }
  }

  public void affineC20REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      double value = Tools.stringToDouble(data.affineC20REd.getText());
      if (data.affineEditPostTransformButton.isSelected()) {
        xForm.setPostCoeff20(value);
      }
      else {
        xForm.setCoeff20(value);
      }
      transformationTableClicked();
    }
  }

  public void affineResetTransformButton_clicked() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      saveUndoPoint();
      XFormTransformService.reset(xForm, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void newDOFCBx_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.setNewCamDOF(data.newDOFCBx.isSelected());
      flameControls.enableDOFUI();
      refreshFlameImage(false);
    }
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) data.resolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private QualityProfile getQualityProfile() {
    QualityProfile res = (QualityProfile) data.qualityProfileCmb.getSelectedItem();
    if (res == null) {
      res = new QualityProfile(false, "Default", 500, false, false);
    }
    return res;
  }

  public void qualityProfileCmb_changed() {
    if (noRefresh) {
      return;
    }
    if (getCurrFlame() == null) {
      return;
    }
    noRefresh = true;
    try {
      QualityProfile profile = getQualityProfile();
      getCurrFlame().setQualityProfile(profile);
    }
    finally {
      noRefresh = false;
    }
  }

  public void resolutionProfileCmb_changed() {
    if (noRefresh) {
      return;
    }
    if (getCurrFlame() == null) {
      return;
    }
    noRefresh = true;
    try {
      ResolutionProfile profile = getResolutionProfile();
      getCurrFlame().setResolutionProfile(profile);
      removeFlamePanel();
      refreshFlameImage(false);
      data.resolutionProfileCmb.requestFocus();
    }
    finally {
      noRefresh = false;
    }
  }

  public void shadingCmb_changed() {
    if (noRefresh) {
      return;
    }
    if (getCurrFlame() == null) {
      return;
    }
    noRefresh = true;
    try {
      getCurrFlame().getShadingInfo().setShading((Shading) data.shadingCmb.getSelectedItem());
      flameControls.enableShadingUI();
      flameControls.refreshShadingUI();
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  public void loadFlameFromClipboard() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new FlameReader(prefs).readFlamesfromXML(xml);
          Flame flame = flames.get(0);
          setCurrFlame(flame);
          undoManager.initUndoStack(getCurrFlame());
          for (int i = flames.size() - 1; i >= 1; i--) {
            randomBatch.add(0, new FlameThumbnail(flames.get(i), null));
          }
          setupProfiles(getCurrFlame());
          updateThumbnails();
          refreshUI();
          messageHelper.showStatusMessage(getCurrFlame(), "opened from clipboard");
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(Flame pFlame, boolean pAddToThumbnails) {
    if (data.layerAppendBtn.isSelected() && getCurrFlame() != null) {
      if (appendToFlame(pFlame)) {
        messageHelper.showStatusMessage(pFlame, "added as new layers");
        setLastGradient(getCurrLayer().getPalette());
      }
    }
    else {
      if (_currFlame == null || !_currFlame.equals(pFlame)) {
        _currRandomizeFlame = pFlame.makeCopy();
      }
      if (pAddToThumbnails) {
        _currFlame = pFlame.makeCopy();
        undoManager.initUndoStack(_currFlame);
        setupProfiles(getCurrFlame());
        randomBatch.add(0, new FlameThumbnail(getCurrFlame(), null));
        updateThumbnails();
        messageHelper.showStatusMessage(getCurrFlame(), "imported into editor");
      }
      else {
        _currFlame = pFlame;
        undoManager.initUndoStack(_currFlame);
        setupProfiles(getCurrFlame());
        messageHelper.showStatusMessage(getCurrFlame(), "imported into editor");
      }
      setLastGradient(getCurrLayer().getPalette());
    }
    refreshUI();
  }

  public Flame exportFlame() {
    if (getCurrFlame() != null) {
      return getCurrFlame().makeCopy();
    }
    else {
      return null;
    }
  }

  public void saveFlameToClipboard() {
    try {
      if (getCurrFlame() != null) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String xml = new FlameWriter().getFlameXML(getCurrFlame());
        StringSelection data = new StringSelection(xml);
        clipboard.setContents(data, data);
        //        try {
        //          System.out.println(new ScriptGenerator(getCurrFlame()).generateScript());
        //        }
        //        catch (Throwable ex) {
        //          ex.printStackTrace();
        //        }
        messageHelper.showStatusMessage(getCurrFlame(), "flame saved to clipboard");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void mouseTransformSlowButton_clicked() {
    if (flamePanel != null) {
      flamePanel.setFineMovement(data.mouseTransformSlowButton.isSelected());
      EditingPrecision.setCurrent(data.mouseTransformSlowButton.isSelected() ? EditingPrecision.FINE : EditingPrecision.NORMAL);
    }
  }

  @Override
  public ScriptRunner compileScript() throws Exception {
    return ScriptRunner.compile(data.scriptTextArea.getText());
  }

  @Override
  public void runScript() throws Exception {
    ScriptRunner script = compileScript();
    saveUndoPoint();
    script.run(this);
  }

  public void compileScriptButton_clicked() {
    try {
      compileScript();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void affineScaleXButton_stateChanged() {
    if (flamePanel != null) {
      flamePanel.setAllowScaleX(data.affineScaleXButton.isSelected());
    }
  }

  public void affineScaleYButton_stateChanged() {
    if (flamePanel != null) {
      flamePanel.setAllowScaleY(data.affineScaleYButton.isSelected());
    }
  }

  public void distributeColorsBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      getCurrLayer().distributeColors();
      transformationTableClicked();
    }
  }

  public void randomizeColorsBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      getCurrLayer().randomizeColors();
      transformationTableClicked();
    }
  }

  public void randomizeColorSpeedBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      getCurrLayer().randomizeColorSpeed();
      transformationTableClicked();
    }
  }

  public void randomizeColorShiftBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      data.paletteShiftREd.setText(String.valueOf(255 - (int) (511 * Math.random())));
    }
  }

  public void paletteSwapRGBREd_changed() {
    paletteTextFieldChanged(data.paletteSwapRGBSlider, data.paletteSwapRGBREd, "modSwapRGB", 1.0);
  }

  public void paletteSwapRGBSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteSwapRGBSlider, data.paletteSwapRGBREd, "modSwapRGB", 1.0);
  }

  public void paletteFrequencyREd_changed() {
    paletteTextFieldChanged(data.paletteFrequencySlider, data.paletteFrequencyREd, "modFrequency", 1.0);
  }

  public void paletteFrequencySlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteFrequencySlider, data.paletteFrequencyREd, "modFrequency", 1.0);
  }

  public void paletteBlurREd_changed() {
    paletteTextFieldChanged(data.paletteBlurSlider, data.paletteBlurREd, "modBlur", 1.0);
  }

  public void paletteBlurSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteBlurSlider, data.paletteBlurREd, "modBlur", 1.0);
  }

  public void affineC01REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      double value = Tools.stringToDouble(data.affineC01REd.getText());
      if (data.affineEditPostTransformButton.isSelected()) {
        xForm.setPostCoeff01(value);
      }
      else {
        xForm.setCoeff01(value);
      }
      transformationTableClicked();
    }
  }

  public void affineC11REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      double value = Tools.stringToDouble(data.affineC11REd.getText());
      if (data.affineEditPostTransformButton.isSelected()) {
        xForm.setPostCoeff11(value);
      }
      else {
        xForm.setCoeff11(value);
      }
      transformationTableClicked();
    }
  }

  public void affineC00REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      double value = Tools.stringToDouble(data.affineC00REd.getText());
      if (data.affineEditPostTransformButton.isSelected()) {
        xForm.setPostCoeff00(value);
      }
      else {
        xForm.setCoeff00(value);
      }
      transformationTableClicked();
    }
  }

  public void affineC10REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      double value = Tools.stringToDouble(data.affineC10REd.getText());
      if (data.affineEditPostTransformButton.isSelected()) {
        xForm.setPostCoeff10(value);
      }
      else {
        xForm.setCoeff10(value);
      }
      transformationTableClicked();
    }
  }

  public void toggleVariationsButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setDrawVariations(data.toggleVariationsButton.isSelected());
      refreshFlameImage(false);
    }
  }

  public TinaInteractiveRendererController getInteractiveRendererCtrl() {
    return interactiveRendererCtrl;
  }

  public void setInteractiveRendererCtrl(TinaInteractiveRendererController interactiveRendererCtrl) {
    this.interactiveRendererCtrl = interactiveRendererCtrl;
  }

  public void affinePreserveZButton_clicked() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    if (getCurrFlame() != null) {
      saveUndoPoint();
      getCurrFlame().setPreserveZ(data.affinePreserveZButton.isSelected());
      refreshFlameImage(false);
    }
  }

  private void setupProfiles(Flame pFlame) {
    if (prefs.isTinaAssociateProfilesWithFlames()) {
      if (pFlame.getResolutionProfile() != null) {
        ResolutionProfile profile = null;
        for (int i = 0; i < data.resolutionProfileCmb.getItemCount(); i++) {
          profile = (ResolutionProfile) data.resolutionProfileCmb.getItemAt(i);
          if (pFlame.getResolutionProfile().equals(profile.toString()))
            break;
          else
            profile = null;
        }
        if (profile != null) {
          data.resolutionProfileCmb.setSelectedItem(profile);
        }
      }

      if (pFlame.getQualityProfile() != null) {
        QualityProfile profile = null;
        for (int i = 0; i < data.qualityProfileCmb.getItemCount(); i++) {
          profile = (QualityProfile) data.qualityProfileCmb.getItemAt(i);
          if (pFlame.getQualityProfile().equals(profile.toString()))
            break;
          else
            profile = null;
        }
        if (profile != null) {
          data.qualityProfileCmb.setSelectedItem(profile);
        }
      }
    }
  }

  public void editQualityProfiles() {
    QualityProfileDialog dlg = new QualityProfileDialog(SwingUtilities.getWindowAncestor(centerPanel));
    dlg.setProfiles(prefs.getQualityProfiles());
    dlg.setProfile(getQualityProfile());
    dlg.setModal(true);
    dlg.setVisible(true);
    if (dlg.isConfirmed() && dlg.isConfigChanged()) {
      try {
        QualityProfile profile = getQualityProfile();
        prefs.getQualityProfiles().clear();
        prefs.getQualityProfiles().addAll(dlg.getProfiles());
        prefs.saveToFromFile();

        refreshQualityProfileCmb(data.qualityProfileCmb, profile);
        refreshQualityProfileCmb(data.batchQualityProfileCmb, profile);
        qualityProfileCmb_changed();
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public TinaSWFAnimatorController getSwfAnimatorCtrl() {
    return swfAnimatorCtrl;
  }

  public void setSwfAnimatorCtrl(TinaSWFAnimatorController pSWFAnimatorCtrl) {
    swfAnimatorCtrl = pSWFAnimatorCtrl;
  }

  public void editResolutionProfiles() {
    ResolutionProfileDialog dlg = new ResolutionProfileDialog(SwingUtilities.getWindowAncestor(centerPanel));
    dlg.setProfiles(prefs.getResolutionProfiles());
    dlg.setProfile(getResolutionProfile());
    dlg.setModal(true);
    dlg.setVisible(true);
    if (dlg.isConfirmed() && dlg.isConfigChanged()) {
      try {
        ResolutionProfile profile = getResolutionProfile();
        prefs.getResolutionProfiles().clear();
        prefs.getResolutionProfiles().addAll(dlg.getProfiles());
        prefs.saveToFromFile();
        refreshResolutionProfileCmb(data.resolutionProfileCmb, profile);
        refreshResolutionProfileCmb(data.interactiveResolutionProfileCmb, profile);
        refreshResolutionProfileCmb(data.swfAnimatorResolutionProfileCmb, profile);
        refreshResolutionProfileCmb(data.batchResolutionProfileCmb, profile);
        resolutionProfileCmb_changed();
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void appendToMovieButton_actionPerformed(ActionEvent e) {
    if (getCurrFlame() != null) {
      getSwfAnimatorCtrl().importFlameFromEditor(getCurrFlame().makeCopy());
      rootTabbedPane.setSelectedIndex(TinaSWFAnimatorController.PAGE_INDEX);
    }
  }

  public JTabbedPane getRootTabbedPane() {
    return rootTabbedPane;
  }

  public void mouseTransformEditPointsButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        data.mouseTransformScaleTrianglesButton.setSelected(false);
        data.mouseTransformMoveTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditGradientButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        data.mouseTransformEditTriangleViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setMouseDragOperation(data.mouseTransformEditPointsButton.isSelected() ? MouseDragOperation.POINTS : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.POINTS);
          refreshFlameImage(false);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void mouseTransformViewButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        data.mouseTransformScaleTrianglesButton.setSelected(false);
        data.mouseTransformMoveTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditGradientButton.setSelected(false);
        data.mouseTransformEditTriangleViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setMouseDragOperation(data.mouseTransformEditViewButton.isSelected() ? MouseDragOperation.VIEW : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE);
          refreshFlameImage(false);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void undoAction() {
    if (getCurrFlame() != null) {
      undoManager.setEnabled(false);
      try {
        int txRow = data.transformationsTable.getSelectedRow();
        int layersRow = data.layersTable.getSelectedRow();
        undoManager.saveUndoPoint(getCurrFlame());
        undoManager.undo(getCurrFlame());
        registerToEditor(getCurrFlame(), getCurrLayer());
        enableUndoControls();
        refreshUI();
        if (layersRow > 0) {
          data.layersTable.getSelectionModel().setSelectionInterval(layersRow, layersRow);
        }
        if (txRow >= 0) {
          data.transformationsTable.getSelectionModel().setSelectionInterval(txRow, txRow);
        }
      }
      finally {
        undoManager.setEnabled(true);
      }
    }
  }

  public void redoAction() {
    if (getCurrFlame() != null) {
      undoManager.setEnabled(false);
      try {
        int txRow = data.transformationsTable.getSelectedRow();
        int layersRow = data.layersTable.getSelectedRow();
        undoManager.redo(getCurrFlame());
        registerToEditor(getCurrFlame(), getCurrLayer());
        enableUndoControls();
        refreshUI();
        if (layersRow > 0) {
          data.layersTable.getSelectionModel().setSelectionInterval(layersRow, layersRow);
        }
        if (txRow >= 0) {
          data.transformationsTable.getSelectionModel().setSelectionInterval(txRow, txRow);
        }
      }
      finally {
        undoManager.setEnabled(true);
      }
    }
  }

  @Override
  public void saveUndoPoint() {
    if (getCurrFlame() != null) {
      undoManager.saveUndoPoint(getCurrFlame());
      enableUndoControls();
      data.undoButton.invalidate();
      data.undoButton.validate();
      data.redoButton.invalidate();
      data.redoButton.validate();
      data.undoButton.getParent().repaint();
    }
  }

  private boolean undoDebug = false;

  private void enableUndoControls() {
    final String UNDO_LABEL = "Undo";
    final String REDO_LABEL = "Redo";
    if (getCurrFlame() != null) {
      int stackSize = undoManager.getUndoStackSize(getCurrFlame());
      if (stackSize > 0) {
        int pos = undoManager.getUndoStackPosition(getCurrFlame());
        data.undoButton.setEnabled(pos > 0 && pos < stackSize);
        if (undoDebug) {
          data.undoButton.setText("U " + pos);
        }
        else {
          data.undoButton.setToolTipText(UNDO_LABEL + " " + pos + "/" + stackSize);
        }
        data.redoButton.setEnabled(pos >= 0 && pos < stackSize - 1);
        if (undoDebug) {
          data.redoButton.setText("R " + stackSize);
        }
        else {
          data.redoButton.setToolTipText(REDO_LABEL + " " + pos + "/" + stackSize);
        }
      }
      else {
        data.undoButton.setEnabled(false);
        if (undoDebug) {
          data.undoButton.setText(UNDO_LABEL);
        }
        else {
          data.undoButton.setToolTipText(UNDO_LABEL);
        }
        data.redoButton.setEnabled(false);
        if (undoDebug) {
          data.redoButton.setText(REDO_LABEL);
        }
        else {
          data.redoButton.setToolTipText(REDO_LABEL);
        }
      }
    }
    else {
      data.undoButton.setEnabled(false);
      data.redoButton.setEnabled(false);
    }
  }

  public void grabPaletteFromImageButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      try {
        SimpleImage img = new ImageReader(centerPanel).loadImage(file.getAbsolutePath());
        prefs.setLastInputImageFile(file);
        RGBPalette palette = new MedianCutQuantizer().createPalette(img);
        data.paletteKeyFrames = null;
        saveUndoPoint();
        getCurrLayer().setPalette(palette);
        setLastGradient(palette);
        refreshPaletteColorsTable();
        refreshPaletteUI(palette);
        refreshFlameImage(false);
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void selectImageForGradientButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      try {
        String filename = file.getAbsolutePath();
        WFImage img = RessourceManager.getImage(filename);
        if (img.getImageWidth() < 16 || img.getImageHeight() < 16 || !(img instanceof SimpleImage)) {
          throw new Exception("Invalid gradient map");
        }
        prefs.setLastInputImageFile(file);

        saveUndoPoint();
        getCurrLayer().setGradientMapFilename(filename);
        setLastGradient(getCurrLayer().getPalette());
        refreshPaletteColorsTable();
        refreshPaletteUI(getCurrLayer().getPalette());
        refreshFlameImage(false);
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void clearImageForGradientButton_actionPerformed(ActionEvent e) {
    saveUndoPoint();
    getCurrLayer().setGradientMapFilename(null);
    setLastGradient(getCurrLayer().getPalette());
    refreshPaletteColorsTable();
    refreshPaletteUI(getCurrLayer().getPalette());
    refreshFlameImage(false);
  }

  public void paletteInvertBtn_clicked() {
    Layer layer = getCurrLayer();
    if (layer != null) {
      saveUndoPoint();
      layer.getPalette().negativeColors();
      refreshPaletteUI(layer.getPalette());
      transformationTableClicked();
    }
  }

  public void paletteReverseBtn_clicked() {
    Layer layer = getCurrLayer();
    if (layer != null) {
      saveUndoPoint();
      layer.getPalette().reverseColors();
      refreshPaletteUI(layer.getPalette());
      transformationTableClicked();
    }
  }

  public void snapshotButton_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      Flame storedFlame = flame.makeCopy();
      undoManager.initUndoStack(storedFlame);
      randomBatch.add(0, new FlameThumbnail(storedFlame, null));
      updateThumbnails();
    }
  }

  public void quicksaveButton_clicked() {
    try {
      if (getCurrFlame() != null) {
        String filename = qsaveFilenameGen.generateNextFilename();
        new FlameWriter().writeFlame(getCurrFlame(), filename);
        messageHelper.showStatusMessage(getCurrFlame(), "quicksave <" + new File(filename).getName() + "> saved");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void toggleTransparencyButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setShowTransparency(data.toggleTransparencyButton.isSelected());
      refreshFlameImage(false);
    }
  }

  public void flameTransparencyCbx_changed() {
    if (getCurrFlame() != null) {
      getCurrFlame().setBGTransparency(data.bgTransparencyCBx.isSelected());
      flameControls.enableDOFUI();
    }
  }

  public DancingFractalsController getDancingFractalsController() {
    return dancingFractalsController;
  }

  private RenderMainFlameThread mainRenderThread = null;

  private void enableMainRenderControls() {
    data.renderMainButton.setText(mainRenderThread == null ? "Render" : "Cancel render");
  }

  public void renderImageButton_actionPerformed() {
    if (mainRenderThread != null) {
      mainRenderThread.setForceAbort();
      while (mainRenderThread.isFinished()) {
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      mainRenderThread = null;
      enableMainRenderControls();
    }
    else if (getCurrFlame() != null) {
      try {
        String dfltFileExt = Stereo3dMode.SIDE_BY_SIDE.equals(getCurrFlame().getStereo3dMode()) ? Tools.FILEEXT_PNS : Tools.FILEEXT_PNG;
        JFileChooser chooser = new ImageFileChooser(dfltFileExt);
        if (prefs.getOutputImagePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          QualityProfile qualProfile = getQualityProfile();
          ResolutionProfile resProfile = getResolutionProfile();
          final Flame flame = getCurrFlame();
          final File file = chooser.getSelectedFile();
          prefs.setLastOutputImageFile(file);
          RenderMainFlameThreadFinishEvent finishEvent = new RenderMainFlameThreadFinishEvent() {

            @Override
            public void succeeded(double pElapsedTime) {
              try {
                messageHelper.showStatusMessage(flame, "render time: " + Tools.doubleToString(pElapsedTime) + "s");
                mainController.loadImage(file.getAbsolutePath(), false);
              }
              catch (Throwable ex) {
                errorHandler.handleError(ex);
              }
              mainRenderThread = null;
              enableMainRenderControls();
            }

            @Override
            public void failed(Throwable exception) {
              errorHandler.handleError(exception);
              mainRenderThread = null;
              enableMainRenderControls();
            }

          };
          mainRenderThread = new RenderMainFlameThread(prefs, flame, file, qualProfile, resProfile, finishEvent, mainProgressUpdater);
          enableMainRenderControls();
          new Thread(mainRenderThread).start();
        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void spatialFilterKernelCmb_changed() {
    if (!noRefresh) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        flame.setSpatialFilterKernel((FilterKernelType) data.filterKernelCmb.getSelectedItem());
      }
    }
  }

  public void quickMutateButton_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      rootTabbedPane.setSelectedIndex(MutaGenController.PAGE_INDEX);
      rootTabbedPane.getParent().invalidate();
      try {
        Graphics g = rootTabbedPane.getParent().getGraphics();
        if (g != null) {
          rootTabbedPane.getParent().paint(g);
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      mutaGenController.importFlame(flame);
    }
  }

  public MutaGenController getMutaGenController() {
    return mutaGenController;
  }

  public MeshGenController getMeshGenController() {
    return meshGenController;
  }

  public void paletteSortBtn_clicked() {
    Layer layer = getCurrLayer();
    if (layer != null) {
      saveUndoPoint();
      layer.getPalette().sort();
      refreshPaletteUI(layer.getPalette());
      transformationTableClicked();
    }
  }

  public void editFlameTitleBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      String s = StandardDialogs.promptForText(rootTabbedPane, "Please enter the new title:", flame.getName());
      if (s != null) {
        flame.setName(s);
        messageHelper.showStatusMessage(flame, "Title changed");
      }
    }
  }

  public void editTransformCaptionBtn_clicked() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      String s = StandardDialogs.promptForText(rootTabbedPane, "Please enter a new name:", xForm.getName());
      if (s != null) {
        xForm.setName(s);
        int row = data.transformationsTable.getSelectedRow();
        gridRefreshing = true;
        try {
          refreshTransformationsTable();
          data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
        }
        finally {
          gridRefreshing = false;
        }
      }
    }
  }

  public void mouseTransformRotateTrianglesButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformMoveTrianglesButton.setSelected(false);
        data.mouseTransformScaleTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditGradientButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        data.mouseTransformEditTriangleViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setMouseDragOperation(data.mouseTransformRotateTrianglesButton.isSelected() ? MouseDragOperation.ROTATE_TRIANGLE : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.ROTATE_TRIANGLE);
          refreshFlameImage(false);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void mouseTransformScaleTrianglesButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        data.mouseTransformMoveTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditGradientButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        data.mouseTransformEditTriangleViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setMouseDragOperation(data.mouseTransformScaleTrianglesButton.isSelected() ? MouseDragOperation.SCALE_TRIANGLE : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.SCALE_TRIANGLE);
          refreshFlameImage(false);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public JWFScriptController getJwfScriptController() {
    return jwfScriptController;
  }

  public FlameBrowserController getFlameBrowserController() {
    return flameBrowserController;
  }

  public GradientController getGradientController() {
    return gradientController;
  }

  public AnimationController getAnimationController() {
    return animationController;
  }

  public void mouseTransformEditGradientButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        data.mouseTransformScaleTrianglesButton.setSelected(false);
        data.mouseTransformMoveTrianglesButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        data.mouseTransformEditTriangleViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setMouseDragOperation(data.mouseTransformEditGradientButton.isSelected() ? MouseDragOperation.GRADIENT : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(false);
          refreshFlameImage(false);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void gradientFadeBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientFade();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void gradientInvertBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientInvert();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void gradientReverseBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientReverse();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void gradientSortBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientSort();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void gradientSelectAllBtn_clicked() {
    getFlamePanel().gradientSelectAll();
    refreshFlameImage(false);
  }

  public void gradientApplyBalancingBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().applyBalancing();
    refreshUI();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void gradientApplyTXBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().applyTX();
    refreshUI();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  private final static GradientSelectionProvider dfltGradientSelection = new DefaultGradientSelectionProvider();

  @Override
  public int getFrom() {
    if (getFlamePanel().getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
      return getFlamePanel().getGradientFrom();
    }
    else {
      return dfltGradientSelection.getFrom();
    }
  }

  @Override
  public int getTo() {
    if (getFlamePanel().getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
      return getFlamePanel().getGradientTo();
    }
    else {
      return dfltGradientSelection.getTo();
    }
  }

  public void gradientCopyRangeBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientCopyRange();
  }

  public void gradientPasteRangeBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientPasteRange();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void gradientEraseRangeBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientEraseRange();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void gradientMononchromeBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientMonochrome();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void gradientFadeAllBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientFadeAll();
    refreshFlameImage(false);
    refreshPaletteImg();
  }

  public void backgroundColorBtn_clicked() {
    if (getCurrFlame() != null) {
      undoManager.saveUndoPoint(getCurrFlame());

      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      Color selectedColor = JColorChooser.showDialog(rootTabbedPane, title, new Color(getCurrFlame().getBGColorRed(), getCurrFlame().getBGColorGreen(), getCurrFlame().getBGColorBlue()));
      if (selectedColor != null) {
        getCurrFlame().setBGColorRed(selectedColor.getRed());
        getCurrFlame().setBGColorGreen(selectedColor.getGreen());
        getCurrFlame().setBGColorBlue(selectedColor.getBlue());
        refreshFlameImage(false);
        refreshBGColorIndicator();
      }
    }
  }

  private void refreshBGColorIndicator() {
    Color color = getCurrFlame() != null ? new Color(getCurrFlame().getBGColorRed(), getCurrFlame().getBGColorGreen(), getCurrFlame().getBGColorBlue()) : Color.BLACK;
    data.backgroundColorIndicatorBtn.setBackground(color);
  }

  private List<MutationType> createRandomMutationTypes() {
    MutationType allMutationTypes[] = { MutationType.LOCAL_GAMMA, MutationType.ADD_TRANSFORM, MutationType.ADD_VARIATION, MutationType.CHANGE_WEIGHT, MutationType.GRADIENT_POSITION, MutationType.AFFINE, MutationType.RANDOM_GRADIENT, MutationType.RANDOM_PARAMETER };
    int[] allCounts = { 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 6, 7, 8, 9 };
    int count = allCounts[(int) (Math.random() * allCounts.length)];
    List<MutationType> res = new ArrayList<MutationType>();
    for (int i = 0; i < count; i++) {
      res.add(allMutationTypes[(int) (Math.random() * allMutationTypes.length)]);
    }
    return res;
  }

  private SimpleImage renderRandomizedFlame(Flame pFlame, Dimension pImgSize) {
    int imageWidth = pImgSize.width, imageHeight = pImgSize.height;
    RenderInfo info = new RenderInfo(imageWidth, imageHeight, RenderMode.PREVIEW);
    double wScl = (double) info.getImageWidth() / (double) pFlame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) pFlame.getHeight();
    pFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * pFlame.getPixelsPerUnit());
    pFlame.setWidth(imageWidth);
    pFlame.setHeight(imageHeight);
    pFlame.setSampleDensity(40.0);
    FlameRenderer renderer = new FlameRenderer(pFlame, prefs, false, false);
    RenderedFlame res = renderer.renderFlame(info);
    return res.getImage();
  }

  public void randomizeBtn_clicked() {
    if (getCurrRandomizeFlame() != null) {
      saveUndoPoint();

      final int MAX_ITER = 10;
      final double MIN_RENDER_COVERAGE = 0.42;
      final double MIN_DIFF_COVERAGE = 0.28;
      final double INVALID_COVERAGE = -1.0;
      Dimension probeSize = new Dimension(80, 60);

      SimpleImage baseFlameImg = renderRandomizedFlame(getCurrRandomizeFlame().makeCopy(), probeSize);
      SimpleImage simplifiedBaseFlameImg = RandomFlameGeneratorSampler.createSimplifiedRefImage(baseFlameImg);

      int iter = 0;
      double bestCoverage = INVALID_COVERAGE;
      Flame bestMutation = null;
      while (true) {
        Flame currMutation = getCurrRandomizeFlame().makeCopy();
        List<MutationType> mutationTypes = createRandomMutationTypes();
        for (MutationType mutationType : mutationTypes) {
          int layerIdx = data.layersTable.getSelectedRow();
          Layer layer = currMutation.getLayers().get(layerIdx);
          mutationType.createMutationInstance().execute(layer);
        }

        SimpleImage renderedImg = renderRandomizedFlame(currMutation.makeCopy(), probeSize);
        double coverage = renderedImg != null ? RandomFlameGeneratorSampler.calculateCoverage(renderedImg, 0, 0, 0, true) : INVALID_COVERAGE;
        if (coverage > MIN_RENDER_COVERAGE) {
          coverage = RandomFlameGeneratorSampler.calculateDiffCoverage(renderedImg, simplifiedBaseFlameImg);
        }
        if (coverage > MIN_DIFF_COVERAGE) {
          bestMutation = currMutation;
          break;
        }
        else if (coverage > bestCoverage) {
          bestCoverage = coverage;
          bestMutation = currMutation;
        }
        // Don't count invalid mutations
        if (renderedImg != null) {
          iter++;
        }
        if (iter >= MAX_ITER) {
          break;
        }
      }

      if (bestMutation != null) {
        getCurrFlame().assign(bestMutation);
      }
      refreshUI();
    }
  }

  public void layersTableClicked() {
    if (!gridRefreshing) {
      boolean oldGridRefreshing = gridRefreshing;
      boolean oldCmbRefreshing = cmbRefreshing;
      gridRefreshing = cmbRefreshing = true;
      try {
        setLastGradient(null);
        Layer layer = getCurrLayer();
        refreshLayerUI();
        refreshLayerControls(layer);
        enableLayerControls();
        //refreshFlameImage(false);
      }
      finally {
        cmbRefreshing = oldCmbRefreshing;
        gridRefreshing = oldGridRefreshing;
      }
    }
  }

  public void addLayerBtn_clicked() {
    Flame flame = getCurrFlame();
    Layer layer = new Layer();
    RGBPalette palette = RandomGradientGeneratorList.getRandomGradientGeneratorInstance((String) data.paletteRandomGeneratorCmb.getSelectedItem()).generatePalette(Integer.parseInt(data.paletteRandomPointsREd.getText()), data.paletteFadeColorsCBx.isSelected());
    layer.setPalette(palette);
    setLastGradient(palette);
    saveUndoPoint();
    flame.getLayers().add(layer);

    gridRefreshing = true;
    try {
      refreshLayersTable();
    }
    finally {
      gridRefreshing = false;
    }

    int row = getCurrFlame().getLayers().size() - 1;
    data.layersTable.getSelectionModel().setSelectionInterval(row, row);
  }

  public void duplicateLayerBtn_clicked() {
    Flame flame = getCurrFlame();
    Layer layer = getCurrLayer();
    Layer duplicate = layer.makeCopy();
    saveUndoPoint();
    flame.getLayers().add(duplicate);

    gridRefreshing = true;
    try {
      refreshLayersTable();
    }
    finally {
      gridRefreshing = false;
    }

    int row = getCurrFlame().getLayers().size() - 1;
    data.layersTable.getSelectionModel().setSelectionInterval(row, row);
  }

  public void deleteLayerBtn_clicked() {
    int row = data.layersTable.getSelectedRow();
    saveUndoPoint();
    getCurrFlame().getLayers().remove(row);

    gridRefreshing = true;
    try {
      refreshLayersTable();
      data.layersTable.getSelectionModel().setSelectionInterval(0, 0);
    }
    finally {
      gridRefreshing = false;
    }

    layersTableClicked();
  }

  public void layerWeightREd_changed() {
    if (!gridRefreshing && getCurrLayer() != null) {
      saveUndoPoint();
      getCurrLayer().setWeight(Tools.stringToDouble(data.layerWeightEd.getText()));
      int row = data.layersTable.getSelectedRow();
      boolean oldGridRefreshing = gridRefreshing;
      gridRefreshing = true;
      try {
        refreshLayersTable();
        data.layersTable.getSelectionModel().setSelectionInterval(row, row);
      }
      finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(false);
    }
  }

  public void layerVisibilityButton_clicked() {
    if (!gridRefreshing) {
      saveUndoPoint();
      getCurrLayer().setVisible(data.layerVisibleBtn.isSelected());
      int row = data.layersTable.getSelectedRow();
      boolean oldGridRefreshing = gridRefreshing;
      gridRefreshing = true;
      try {
        refreshLayersTable();
        data.layersTable.getSelectionModel().setSelectionInterval(row, row);
      }
      finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(false);
    }
  }

  @Override
  public Layer getLayer() {
    return getCurrLayer();
  }

  public void layerHideAllOthersButton_clicked() {
    if (!gridRefreshing && getCurrLayer() != null) {
      saveUndoPoint();
      int row = data.layersTable.getSelectedRow();
      for (int i = 0; i < getCurrFlame().getLayers().size(); i++) {
        Layer layer = getCurrFlame().getLayers().get(i);
        layer.setVisible(row == i);
      }
      boolean oldGridRefreshing = gridRefreshing;
      gridRefreshing = true;
      try {
        refreshLayersTable();
        data.layersTable.getSelectionModel().setSelectionInterval(row, row);
      }
      finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(false);
    }
  }

  public void layerShowAllButton_clicked() {
    if (!gridRefreshing) {
      saveUndoPoint();
      for (Layer layer : getCurrFlame().getLayers()) {
        layer.setVisible(true);
      }
      int row = data.layersTable.getSelectedRow();
      boolean oldGridRefreshing = gridRefreshing;
      gridRefreshing = true;
      try {
        refreshLayersTable();
        data.layersTable.getSelectionModel().setSelectionInterval(row, row);
      }
      finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(false);
    }
  }

  public void layerAppendModeBtnClicked() {
    refreshFlameImage(false);
  }

  public void layerPreviewBtnClicked() {
    refreshFlameImage(false);
  }

  public boolean isNoRefresh() {
    return noRefresh;
  }

  public void setNoRefresh(boolean noRefresh) {
    this.noRefresh = noRefresh;
  }

  private void registerMotionPropertyControls() {
    for (JWFNumberField field : flameControls.getMotionControls()) {
      animationController.registerMotionPropertyControls(field);
    }
    for (JWFNumberField field : gradientControls.getMotionControls()) {
      animationController.registerMotionPropertyControls(field);
    }

    animationController.registerMotionPropertyControls(data.affineC00REd);
    animationController.registerMotionPropertyControls(data.affineC01REd);
    animationController.registerMotionPropertyControls(data.affineC10REd);
    animationController.registerMotionPropertyControls(data.affineC11REd);
    animationController.registerMotionPropertyControls(data.affineC20REd);
    animationController.registerMotionPropertyControls(data.affineC21REd);
    animationController.registerMotionPropertyControls(new MotionCurveButtonPair(data.affineRotateLeftButton, data.affineRotateEditMotionCurveBtn));
    animationController.registerMotionPropertyControls(new MotionCurveButtonPair(data.affineEnlargeButton, data.affineScaleEditMotionCurveBtn));
    animationController.registerMotionPropertyControls(data.transformationWeightREd);
    animationController.registerMotionPropertyControls(data.xFormColorREd);
    animationController.registerMotionPropertyControls(data.xFormSymmetryREd);
    animationController.registerMotionPropertyControls(data.xFormOpacityREd);

    for (TinaNonlinearControlsRow row : data.TinaNonlinearControlsRows) {
      animationController.registerMotionPropertyControls(row.getNonlinearVarREd());
      animationController.registerMotionPropertyControls(row.getNonlinearParamsREd());
    }

  }

  public FlameControlsDelegate getFlameControls() {
    return flameControls;
  }

  public XFormControlsDelegate getXFormControls() {
    return xFormControls;
  }

  public ChannelMixerControlsDelegate getChannelMixerControls() {
    return channelMixerControls;
  }

  public void triangleStyleCmb_changed() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      FlamePanelControlStyle style = (FlamePanelControlStyle) data.triangleStyleCmb.getSelectedItem();
      flamePanel.setFlamePanelTriangleMode(style);
      refreshFlameImage(false);
    }
  }

  public void toggleDrawGridButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setWithGrid(data.toggleDrawGridButton.isSelected());
      refreshFlameImage(false);
    }
  }

  public void editorFractalBrightnessSliderChanged() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setImageBrightness(data.editorFractalBrightnessSlider.getValue());
      refreshFlameImage(false);
    }

  }

  public void mouseTransformTriangleViewButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformMoveTrianglesButton.setSelected(false);
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        data.mouseTransformScaleTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditGradientButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setMouseDragOperation(data.mouseTransformEditTriangleViewButton.isSelected() ? MouseDragOperation.TRIANGLE_VIEW : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.TRIANGLE_VIEW);
          refreshFlameImage(false);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void toggleTriangleWithColorsButton_clicked() {
    if (refreshing) {
      return;
    }
    refreshing = true;
    try {
      if (flamePanel != null) {
        flamePanel.getConfig().setWithColoredTransforms(data.toggleTriangleWithColorsButton.isSelected());
      }
      refreshFlameImage(false);
    }
    finally {
      refreshing = false;
    }
  }

  public void nonlinearVarEditMotionCurve(int pIdx) {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      if (pIdx < xForm.getVariationCount()) {
        String propertyName = "amount";
        saveUndoPoint();
        variationControlsDelegates[pIdx].editMotionCurve(propertyName, "variation amount");
        variationControlsDelegates[pIdx].enableControl(data.TinaNonlinearControlsRows[pIdx].getNonlinearVarREd(), propertyName, false);
        refreshFlameImage(false);
      }
    }
  }

  public void nonlinearParamsEditMotionCurve(int pIdx) {
    String propertyname = (String) data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
    XForm xForm = getCurrXForm();
    if (xForm != null && propertyname != null && propertyname.length() > 0) {
      if (pIdx < xForm.getVariationCount()) {
        Variation var = xForm.getVariation(pIdx);
        if (var.getFunc().getParameterIndex(propertyname) >= 0) {
          double initialValue;
          try {
            String valStr = data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd().getText();
            if (valStr == null || valStr.length() == 0) {
              valStr = "0";
            }
            initialValue = Double.parseDouble(valStr);
          }
          catch (Exception ex) {
            initialValue = 0.0;
          }
          MotionCurve curve = var.getMotionCurve(propertyname);
          if (curve == null) {
            curve = var.createMotionCurve(propertyname);
          }
          variationControlsDelegates[pIdx].editMotionCurve(curve, initialValue, propertyname, "variation property \"" + propertyname + "\"");
          variationControlsDelegates[pIdx].enableControl(data.TinaNonlinearControlsRows[pIdx].getNonlinearParamsREd(), curve, false);
          refreshFlameImage(false);
        }
      }
    }
  }

  protected TinaControllerData getData() {
    return data;
  }

  public void setVariationControlsDelegates(VariationControlsDelegate[] pVariationControlsDelegates) {
    variationControlsDelegates = pVariationControlsDelegates;
  }

  public void xFormModGammaRandomizeBtn_Clicked(boolean pWholeFractal) {
    if (pWholeFractal) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        for (Layer layer : flame.getLayers()) {
          for (XForm xForm : layer.getXForms()) {
            xForm.randomizeModColorEffects();
          }
        }
        refreshXFormUI(getCurrXForm(XFormType.BOTH));
        refreshFlameImage(false);
      }
    }
    else {
      XForm xForm = getCurrXForm(XFormType.NORMAL);
      if (xForm != null) {
        saveUndoPoint();
        xForm.randomizeModColorEffects();
        refreshXFormUI(xForm);
        refreshFlameImage(false);
      }
    }
  }

  public void xFormModGammaResetBtn_Clicked(boolean pWholeFractal) {
    if (pWholeFractal) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        for (Layer layer : flame.getLayers()) {
          for (XForm xForm : layer.getXForms()) {
            xForm.resetModColorEffects();
          }
        }
        refreshXFormUI(getCurrXForm(XFormType.BOTH));
        refreshFlameImage(false);
      }
    }
    else {
      XForm xForm = getCurrXForm(XFormType.NORMAL);
      if (xForm != null) {
        saveUndoPoint();
        xForm.resetModColorEffects();
        refreshXFormUI(xForm);
        refreshFlameImage(false);
      }
    }
  }

  public BatchRendererController getBatchRendererController() {
    return batchRendererController;
  }

  public MainController getMainController() {
    return mainController;
  }

  protected ErrorHandler getErrorHandler() {
    return errorHandler;
  }

  public void bokehBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      new BokehMutation().execute(flame.getFirstLayer());
      refreshUI();
    }
  }

  public void resetCameraSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetCameraSettings();
      refreshUI();
    }
  }

  public void resetDOFSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetDOFSettings();
      refreshUI();
    }
  }

  public void resetBokehSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetBokehSettings();
      refreshUI();
    }
  }

  public void resetColoringSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetColoringSettings();
      refreshUI();
    }
  }

  public void resetAntialiasingSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetAntialiasingSettings();
      refreshUI();
    }
  }

  public void resetShadingSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetShadingSettings();
      refreshUI();
    }
  }

  public void resetStereo3DSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetStereo3DSettings();
      refreshUI();
    }
  }

  public void resetPostSymmetrySettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetPostSymmetrySettings();
      refreshUI();
    }
  }

  public void resetMotionBlurSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetMotionBlurSettings();
      refreshUI();
    }
  }

  public void xaosViewAsChanged() {
    boolean oldGridRefreshing = gridRefreshing;
    gridRefreshing = true;
    try {
      refreshRelWeightsTable();
    }
    finally {
      gridRefreshing = oldGridRefreshing;
    }
  }

  public void toggleDrawGuidesButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setWithGuides(data.toggleDrawGuidesButton.isSelected());
      refreshFlameImage(false);
    }
  }

  public GradientControlsDelegate getGradientControls() {
    return gradientControls;
  }

  final int DFLT_VERT_TOOLBAR_SIZE = 52;

  public void refreshMacroButtonsPanel() {
    if (prefs.isTinaMacroButtonsVertical()) {
      data.macroButtonHorizRootPanel.setPreferredSize(new Dimension(0, 0));
      data.macroButtonHorizRootPanel.setVisible(false);
      refreshMacroButtonsPanelVertical();
    }
    else {
      data.previewEastMainPanel.setPreferredSize(new Dimension(DFLT_VERT_TOOLBAR_SIZE, 0));
      refreshMacroButtonsPanelHorizontal();
    }
  }

  public void refreshMacroButtonsPanelVertical() {
    data.macroButtonVertPanel.removeAll();
    if (prefs.getTinaMacroButtons().size() == 0) {
      data.previewEastMainPanel.setPreferredSize(new Dimension(DFLT_VERT_TOOLBAR_SIZE, 0));
    }
    else {
      int toolbarWidth = prefs.getTinaMacroToolbarWidth();
      int gap = 1;
      int buttonWidth = prefs.getTinaMacroToolbarWidth() - 16;
      int buttonHeight = 24;
      int toolbarHeight = buttonHeight * prefs.getTinaMacroButtons().size() + gap * (prefs.getTinaMacroButtons().size() - 1);
      data.macroButtonVertPanel.setPreferredSize(new Dimension(toolbarWidth, toolbarHeight));

      data.previewEastMainPanel.setPreferredSize(new Dimension(DFLT_VERT_TOOLBAR_SIZE + toolbarWidth, 0));
      for (final MacroButton macroButton : prefs.getTinaMacroButtons()) {
        JButton button = new JButton();
        button.setFont(new Font("Dialog", Font.BOLD, 9));
        button.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        if (macroButton.getCaption() != null && macroButton.getCaption().length() > 0) {
          button.setText(macroButton.getCaption());
        }
        if (macroButton.getHint() != null && macroButton.getHint().length() > 0) {
          button.setToolTipText(macroButton.getHint());
        }
        if (macroButton.getImage() != null && macroButton.getImage().length() > 0) {
          try {
            button.setIconTextGap(0);
            button.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/" + macroButton.getImage())));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        button.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent e) {
            runScript(macroButton.getMacro(), macroButton.isInternal());
          }
        });
        data.macroButtonVertPanel.add(button);
      }
    }
    data.previewEastMainPanel.invalidate();
    data.previewEastMainPanel.getParent().validate();
    data.previewEastMainPanel.getParent().repaint();
  }

  public void refreshMacroButtonsPanelHorizontal() {
    data.macroButtonHorizPanel.removeAll();
    if (prefs.getTinaMacroButtons().size() == 0) {
      data.macroButtonHorizRootPanel.setPreferredSize(new Dimension(0, 0));
    }
    else {
      int toolbarHeight = prefs.getTinaMacroToolbarHeight();
      int gap = 1;
      int buttonWidth = prefs.getTinaMacroToolbarWidth();
      int buttonHeight = 24;
      int toolbarWidth = buttonWidth * prefs.getTinaMacroButtons().size() + gap * (prefs.getTinaMacroButtons().size() - 1);
      data.macroButtonHorizPanel.setPreferredSize(new Dimension(toolbarWidth, toolbarHeight));

      data.macroButtonHorizRootPanel.setPreferredSize(new Dimension(0, toolbarHeight + 2));
      for (final MacroButton macroButton : prefs.getTinaMacroButtons()) {
        JButton button = new JButton();
        button.setFont(new Font("Dialog", Font.BOLD, 9));
        button.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        if (macroButton.getCaption() != null && macroButton.getCaption().length() > 0) {
          button.setText(macroButton.getCaption());
        }
        if (macroButton.getHint() != null && macroButton.getHint().length() > 0) {
          button.setToolTipText(macroButton.getHint());
        }
        if (macroButton.getImage() != null && macroButton.getImage().length() > 0) {
          try {
            button.setIconTextGap(0);
            button.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/" + macroButton.getImage())));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        button.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent e) {
            runScript(macroButton.getMacro(), macroButton.isInternal());
          }
        });
        data.macroButtonHorizPanel.add(button);
      }
    }
    data.macroButtonHorizRootPanel.invalidate();
    data.macroButtonHorizRootPanel.getParent().validate();
    data.macroButtonHorizRootPanel.getParent().repaint();
  }

  public void runScript(String filename, boolean internal) {
    try {
      String script;
      if (internal) {
        RGBPaletteReader reader = new Flam3GradientReader();
        InputStream is = reader.getClass().getResourceAsStream(filename);
        script = Tools.readUTF8Textfile(is);
      }
      else {
        script = Tools.readUTF8Textfile(filename);
      }

      ScriptRunner scriptRunner = ScriptRunner.compile(script);
      saveUndoPoint();
      scriptRunner.run(this);
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void resetGridToDefaults() {
    flamePanel.resetGridToDefaults();
    refreshFlameImage(false);
  }

  DetachedPreviewWindow detachedPreviewWindow;
  DetachedPreviewController detachedPreviewController;

  @Override
  public DetachedPreviewController getDetachedPreviewController() {
    return detachedPreviewController;
  }

  public void openDetachedPreview() {
    closeDetachedPreview();
    detachedPreviewWindow = new DetachedPreviewWindow();
    detachedPreviewController = new DetachedPreviewController(this, detachedPreviewWindow, data.toggleDetachedPreviewButton);
    detachedPreviewWindow.setController(detachedPreviewController);
    detachedPreviewWindow.getFrame().setVisible(true);
    detachedPreviewController.setFlame(getCurrFlame());
  }

  public void closeDetachedPreview() {
    if (detachedPreviewController != null) {
      detachedPreviewController.cancelRender();
      detachedPreviewController = null;
    }
    if (detachedPreviewWindow != null) {
      detachedPreviewWindow.onClose(false);
      detachedPreviewWindow.getFrame().setVisible(false);
      detachedPreviewWindow = null;
    }
  }

  public void gradientResetBtn_clicked() {
    if (getLastGradient() != null && getCurrLayer() != null) {
      saveUndoPoint();
      getCurrLayer().setPalette(getLastGradient().makeDeepCopy());
      refreshUI();
    }
  }

  private RGBPalette getLastGradient() {
    return _lastGradient;
  }

  public void affineEditPlaneCmb_changed() {
    if (gridRefreshing || cmbRefreshing || getCurrFlame() == null) {
      return;
    }
    getCurrFlame().setEditPlane((EditPlane) data.affineEditPlaneCmb.getSelectedItem());
    transformationTableClicked();
  }

  public void selectImageForBackgroundButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        if (getCurrFlame().getBGImageFilename().length() > 0) {
          chooser.setSelectedFile(new File(getCurrFlame().getBGImageFilename()));
        }
        else {
          chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      try {
        String filename = file.getAbsolutePath();
        WFImage img = RessourceManager.getImage(filename);
        if (img.getImageWidth() < 2 || img.getImageHeight() < 2 || !(img instanceof SimpleImage)) {
          throw new Exception("Invalid background image");
        }
        prefs.setLastInputImageFile(file);

        saveUndoPoint();
        getCurrFlame().setBGImageFilename(filename);
        refreshFlameImage(false);
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void removeBackgroundImageButton_actionPerformed(ActionEvent e) {
    saveUndoPoint();
    getCurrFlame().setBGImageFilename(null);
    refreshFlameImage(false);
  }

  public void gradientColorMapHorizOffsetREd_changed() {
    layerTextFieldChanged(data.gradientColorMapHorizOffsetSlider, data.gradientColorMapHorizOffsetREd, "gradientMapHorizOffset", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapHorizOffsetSlider_changed() {
    layerSliderChanged(data.gradientColorMapHorizOffsetSlider, data.gradientColorMapHorizOffsetREd, "gradientMapHorizOffset", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapHorizScaleREd_changed() {
    layerTextFieldChanged(data.gradientColorMapHorizScaleSlider, data.gradientColorMapHorizScaleREd, "gradientMapHorizScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapHorizScaleSlider_changed() {
    layerSliderChanged(data.gradientColorMapHorizScaleSlider, data.gradientColorMapHorizScaleREd, "gradientMapHorizScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapVertOffsetREd_changed() {
    layerTextFieldChanged(data.gradientColorMapVertOffsetSlider, data.gradientColorMapVertOffsetREd, "gradientMapVertOffset", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapVertOffsetSlider_changed() {
    layerSliderChanged(data.gradientColorMapVertOffsetSlider, data.gradientColorMapVertOffsetREd, "gradientMapVertOffset", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapVertScaleREd_changed() {
    layerTextFieldChanged(data.gradientColorMapVertScaleSlider, data.gradientColorMapVertScaleREd, "gradientMapVertScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapVertScaleSlider_changed() {
    layerSliderChanged(data.gradientColorMapVertScaleSlider, data.gradientColorMapVertScaleREd, "gradientMapVertScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapLocalColorAddREd_changed() {
    layerTextFieldChanged(data.gradientColorMapLocalColorAddSlider, data.gradientColorMapLocalColorAddREd, "gradientMapLocalColorAdd", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapLocalColorAddSlider_changed() {
    layerSliderChanged(data.gradientColorMapLocalColorAddSlider, data.gradientColorMapLocalColorAddREd, "gradientMapLocalColorAdd", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapLocalColorScaleREd_changed() {
    layerTextFieldChanged(data.gradientColorMapLocalColorScaleSlider, data.gradientColorMapLocalColorScaleREd, "gradientMapLocalColorScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapLocalColorScaleSlider_changed() {
    layerSliderChanged(data.gradientColorMapLocalColorScaleSlider, data.gradientColorMapLocalColorScaleREd, "gradientMapLocalColorScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public FlameMessageHelper getMessageHelper() {
    return messageHelper;
  }

  @Override
  public JProgressBar getRenderProgressBar() {
    return tinaFrame.getRenderProgressBar();
  }
}
