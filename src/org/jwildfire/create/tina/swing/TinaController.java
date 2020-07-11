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
package org.jwildfire.create.tina.swing;

import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.util.ResourceManager;
import org.jwildfire.base.*;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.iflames.swing.JFrameFlameMessageHelper;
import org.jwildfire.create.tina.AnimationController;
import org.jwildfire.create.tina.GradientController;
import org.jwildfire.create.tina.base.*;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;
import org.jwildfire.create.tina.batch.BatchRendererController;
import org.jwildfire.create.tina.browser.FlameBrowserController;
import org.jwildfire.create.tina.dance.DancingFractalsController;
import org.jwildfire.create.tina.edit.UndoManager;
import org.jwildfire.create.tina.io.Flam3GradientReader;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.leapmotion.LeapMotionMainEditorController;
import org.jwildfire.create.tina.meshgen.MeshGenController;
import org.jwildfire.create.tina.mutagen.BokehMutation;
import org.jwildfire.create.tina.mutagen.MutaGenController;
import org.jwildfire.create.tina.mutagen.MutationType;
import org.jwildfire.create.tina.mutagen.WeightingFieldMutation;
import org.jwildfire.create.tina.palette.*;
import org.jwildfire.create.tina.quilt.QuiltRendererController;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGenerator;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.render.*;
import org.jwildfire.create.tina.render.filter.*;
import org.jwildfire.create.tina.render.optix.OptixCmdLineDenoiser;
import org.jwildfire.create.tina.script.ScriptParam;
import org.jwildfire.create.tina.script.ScriptRunner;
import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;
import org.jwildfire.create.tina.script.swing.JWFScriptController;
import org.jwildfire.create.tina.script.ui.FormBuilder;
import org.jwildfire.create.tina.script.ui.ScriptParamsForm;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelControlStyle;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.swing.*;
import org.jwildfire.transform.TextTransformer;
import org.jwildfire.transform.TextTransformer.FontStyle;
import org.jwildfire.transform.TextTransformer.HAlignment;
import org.jwildfire.transform.TextTransformer.Mode;
import org.jwildfire.transform.TextTransformer.VAlignment;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TinaController implements FlameHolder, LayerHolder, ScriptRunnerEnvironment, UndoManagerHolder<Flame>, JWFScriptExecuteController, GradientSelectionProvider,
        DetachedPreviewProvider, FlamePanelProvider, RandomBatchHolder, RenderProgressBarHolder {

  static final double SLIDER_SCALE_PERSPECTIVE = 100.0;
  static final double SLIDER_SCALE_CENTRE = 5000.0;
  static final double SLIDER_SCALE_ZOOM = 1000.0;
  static final double SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY = 100.0;
  static final double SLIDER_SCALE_GAMMA = 100.0;
  static final double SLIDER_SCALE_FILTER_RADIUS = 100.0;
  static final double SLIDER_SCALE_GAMMA_THRESHOLD = 5000.0;
  static final double SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD = 1000.0;
  static final double SLIDER_SCALE_POST_OPTIX_DENOISER_BLEND = 1000.0;
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
  private QuiltRendererController quiltRendererController;
  private TinaInteractiveRendererController interactiveRendererCtrl;
  private FlamesGPURenderController gpuRendererCtrl;
  private TinaSWFAnimatorController swfAnimatorCtrl;
  private JWFScriptController jwfScriptController;
  private FlameBrowserController flameBrowserController;
  private GradientController gradientController;
  private AnimationController animationController;
  private LeapMotionMainEditorController leapMotionMainEditorController;

  private FlameControlsDelegate flameControls;
  private GradientControlsDelegate gradientControls;
  private LayerControlsDelegate layerControls;
  protected XFormControlsDelegate xFormControls;
  private ChannelMixerControlsDelegate channelMixerControls;
  private GradientCurveEditorControlsDelegate gradientCurveEditorControls;

  private final NonlinearControlsDelegate nonlinearControls;

  private final MainEditorFrame mainEditorFrame;
  private final String tinaFrameTitle;
  final JPanel centerPanel;
  private final FlameMessageHelper messageHelper;

  private final FlamePreviewHelper flamePreviewHelper;
  private FlamePanel flamePanel;
  private FlamePanel prevFlamePanel;

  private final JWildfire desktop;
  private final Prefs prefs;
  final ErrorHandler errorHandler;
  boolean gridRefreshing = false;
  boolean cmbRefreshing = false;
  boolean refreshing = false;

  final UndoManager<Flame> undoManager = new UndoManager<Flame>();
  private final QuickSaveFilenameGen qsaveFilenameGen;

  MainController mainController;
  private final JPanel rootPanel;
  private Flame _currFlame, _currRandomizeFlame;
  private boolean noRefresh;
  private final ProgressUpdater mainProgressUpdater;
  private final ProgressUpdater randomBatchProgressUpdater;
  public TinaControllerData data = new TinaControllerData();
  public TinaWeightingFieldControllerData weightMapData = new TinaWeightingFieldControllerData();

  private RGBPalette _lastGradient;

  static final String SCRIPT_PROPS_FILE = "j-wildfire-scripts.properties";
  private Properties scriptProps = new Properties();
  private File scriptPropFile = new File(System.getProperty("user.home"), SCRIPT_PROPS_FILE);

  private final FrameControlsUtil frameControlsUtil;

  public TinaController(final TinaControllerParameter parameterObject) {
    frameControlsUtil = new FrameControlsUtil(this);
    nonlinearControls = new NonlinearControlsDelegate(this, data.variationControlsDelegates);
    desktop = parameterObject.desktop;
    mainEditorFrame = parameterObject.pMainEditorFrame;
    tinaFrameTitle = mainEditorFrame.getTitle();
    errorHandler = parameterObject.pErrorHandler;
    prefs = parameterObject.pPrefs;
    centerPanel = parameterObject.pCenterPanel;

    dancingFractalsController = new DancingFractalsController(this, parameterObject.pErrorHandler, parameterObject.pRootPanel, parameterObject.pDancingFlamesFlamePnl, parameterObject.pDancingFlamesGraph1Pnl,
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
      mutaGenController = new MutaGenController(this, parameterObject.pErrorHandler, prefs, parameterObject.pRootPanel, flamePanels,
              parameterObject.mutaGenLoadFlameFromEditorBtn, parameterObject.mutaGenLoadFlameFromFileBtn, parameterObject.mutaGenProgressBar,
              parameterObject.mutaGenAmountREd, parameterObject.mutaGenHorizontalTrend1Cmb, parameterObject.mutaGenHorizontalTrend2Cmb,
              parameterObject.mutaGenVerticalTrend1Cmb, parameterObject.mutaGenVerticalTrend2Cmb, parameterObject.mutaGenBackBtn, parameterObject.mutaGenForwardBtn,
              parameterObject.mutaGenHintPane, parameterObject.mutaGenSaveFlameToEditorBtn, parameterObject.mutaGenSaveFlameToFileBtn);
    }

    batchRendererController = new BatchRendererController(this, parameterObject.pErrorHandler, prefs, parameterObject.pRootPanel, data,
            parameterObject.pJobProgressUpdater, parameterObject.batchRenderOverrideCBx, parameterObject.batchRenderShowImageBtn, parameterObject.enableOpenClBtn);

    data.quiltRendererOpenFlameButton = parameterObject.quiltRendererOpenFlameButton;
    data.quiltRendererImportFlameFromEditorButton = parameterObject.quiltRendererImportFlameFromEditorButton;
    data.quiltRendererImportFlameFromClipboardButton = parameterObject.quiltRendererImportFlameFromClipboardButton;
    data.quiltRendererQualityEdit = parameterObject.quiltRendererQualityEdit;
    data.quiltRendererXSegmentationLevelEdit = parameterObject.quiltRendererXSegmentationLevelEdit;
    data.quiltRendererYSegmentationLevelEdit = parameterObject.quiltRendererYSegmentationLevelEdit;
    data.quiltRenderer4KButton = parameterObject.quiltRenderer4KButton;
    data.quiltRenderer8KButton = parameterObject.quiltRenderer8KButton;
    data.quiltRenderer16KButton = parameterObject.quiltRenderer16KButton;
    data.quiltRenderer32Button = parameterObject.quiltRenderer32Button;

    data.quiltRendererRenderWidthEdit = parameterObject.quiltRendererRenderWidthEdit;
    data.quiltRendererRenderHeightEdit = parameterObject.quiltRendererRenderHeightEdit;
    data.quiltRendererSegmentWidthEdit = parameterObject.quiltRendererSegmentWidthEdit;
    data.quiltRendererSegmentHeightEdit = parameterObject.quiltRendererSegmentHeightEdit;
    data.quiltRendererOutputFilenameEdit = parameterObject.quiltRendererOutputFilenameEdit;
    data.quiltRendererSegmentProgressBar = parameterObject.quiltRendererSegmentProgressBar;
    data.quiltRendererTotalProgressBar = parameterObject.quiltRendererTotalProgressBar;
    data.quiltRendererRenderButton = parameterObject.quiltRendererRenderButton;
    data.quiltRendererPreviewRootPanel = parameterObject.quiltRendererPreviewRootPanel;

    quiltRendererController = new QuiltRendererController(this, parameterObject.pErrorHandler, prefs, parameterObject.pRootPanel, data);

    meshGenController = new MeshGenController(this, parameterObject.pErrorHandler, prefs, parameterObject.pRootPanel,
            parameterObject.meshGenFromEditorBtn, parameterObject.meshGenFromClipboardBtn, parameterObject.meshGenLoadFlameBtn,
            parameterObject.meshGenSliceCountREd, parameterObject.meshGenSlicesPerRenderREd, parameterObject.meshGenRenderWidthREd,
            parameterObject.meshGenRenderHeightREd, parameterObject.meshGenRenderQualityREd, parameterObject.meshGenProgressbar,
            parameterObject.meshGenGenerateBtn, parameterObject.meshGenTopViewRootPnl, parameterObject.meshGenFrontViewRootPnl,
            parameterObject.meshGenPerspectiveViewRootPnl, parameterObject.meshGenHintPane, parameterObject.meshGenCentreXREd,
            parameterObject.meshGenCentreXSlider, parameterObject.meshGenCentreYREd, parameterObject.meshGenCentreYSlider,
            parameterObject.meshGenZoomREd, parameterObject.meshGenZoomSlider, parameterObject.meshGenZMinREd,
            parameterObject.meshGenZMinSlider, parameterObject.meshGenZMaxREd, parameterObject.meshGenZMaxSlider,
            parameterObject.meshGenCellSizeREd, parameterObject.meshGenTopViewRenderBtn, parameterObject.meshGenFrontViewRenderBtn,
            parameterObject.meshGenPerspectiveViewRenderBtn, parameterObject.meshGenTopViewToEditorBtn,
            parameterObject.meshGenLoadSequenceBtn, parameterObject.meshGenSequenceWidthREd, parameterObject.meshGenSequenceHeightREd,
            parameterObject.meshGenSequenceSlicesREd, parameterObject.meshGenSequenceDownSampleREd, parameterObject.meshGenSequenceFilterRadiusREd,
            parameterObject.meshGenGenerateMeshProgressbar, parameterObject.meshGenGenerateMeshBtn, parameterObject.meshGenSequenceFromRendererBtn,
            parameterObject.meshGenSequenceThresholdREd, parameterObject.meshGenSequenceLbl, parameterObject.meshGenPreviewRootPanel,
            parameterObject.meshGenAutoPreviewCBx, parameterObject.meshGenPreviewImportLastGeneratedMeshBtn, parameterObject.meshGenPreviewImportFromFileBtn,
            parameterObject.meshGenClearPreviewBtn, parameterObject.meshGenPreviewPositionXREd, parameterObject.meshGenPreviewPositionYREd,
            parameterObject.meshGenPreviewSizeREd, parameterObject.meshGenPreviewScaleZREd, parameterObject.meshGenPreviewRotateAlphaREd,
            parameterObject.meshGenPreviewRotateBetaREd, parameterObject.meshGenPreviewPointsREd, parameterObject.meshGenPreviewPolygonsREd,
            parameterObject.meshGenRefreshPreviewBtn, parameterObject.meshGenPreviewSunflowExportBtn, parameterObject.meshGenPreFilter1Cmb,
            parameterObject.meshGenPreFilter2Cmb, parameterObject.meshGenImageStepREd, parameterObject.meshGenOutputTypeCmb,
            parameterObject.meshGenTaubinSmoothCbx, parameterObject.meshGenSmoothPassesREd, parameterObject.meshGenSmoothLambdaREd,
            parameterObject.meshGenSmoothMuREd);

    data.macroButtonsTable = parameterObject.macroButtonsTable;
    data.macroButtonMoveUpBtn = parameterObject.macroButtonMoveUpBtn;
    data.macroButtonMoveDownBtn = parameterObject.macroButtonMoveDownBtn;
    data.macroButtonDeleteBtn = parameterObject.macroButtonDeleteBtn;
    jwfScriptController = new JWFScriptController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, data, parameterObject.scriptTree,
            parameterObject.scriptDescriptionTextArea, parameterObject.scriptTextArea,
            parameterObject.rescanScriptsBtn, parameterObject.newScriptBtn, parameterObject.newScriptFromFlameBtn, parameterObject.deleteScriptBtn,
            parameterObject.scriptRenameBtn, parameterObject.scriptDuplicateBtn, parameterObject.scriptRunBtn, parameterObject.scriptAddButtonBtn,
            parameterObject.scriptEditBtn);

    flameBrowserController = new FlameBrowserController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, parameterObject.flameBrowserTree, parameterObject.flameBrowersImagesPnl,
            parameterObject.flameBrowserRefreshBtn, parameterObject.flameBrowserChangeFolderBtn, parameterObject.flameBrowserToEditorBtn, parameterObject.flameBrowserToBatchEditorBtn, parameterObject.flameBrowserDeleteBtn,
            parameterObject.flameBrowserRenameBtn, parameterObject.flameBrowserCopyToBtn, parameterObject.flameBrowserMoveToBtn, parameterObject.flameBrowserToMeshGenBtn);

    gradientController = new GradientController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, parameterObject.gradientLibTree, parameterObject.pGradientLibraryPanel,
            parameterObject.gradientLibraryRescanBtn, parameterObject.gradientLibraryNewFolderBtn, parameterObject.gradientLibraryRenameFolderBtn,
            parameterObject.gradientsList);

    animationController = new AnimationController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel,
            parameterObject.keyframesFrameField, parameterObject.keyframesFrameSlider, parameterObject.keyframesFrameCountField,
            parameterObject.frameSliderPanel, parameterObject.keyframesFrameLbl, parameterObject.keyframesFrameCountLbl,
            parameterObject.motionCurveEditModeButton, parameterObject.motionBlurPanel, parameterObject.motionCurvePlayPreviewButton,
            parameterObject.flameFPSField);

    leapMotionMainEditorController = new LeapMotionMainEditorController(this, parameterObject.pErrorHandler, prefs,
            parameterObject.pCenterPanel, parameterObject.flameFPSField, parameterObject.leapMotionToggleButton,
            parameterObject.leapMotionConfigTable, parameterObject.leapMotionHandCmb, parameterObject.leapMotionInputChannelCmb,
            parameterObject.leapMotionOutputChannelCmb, parameterObject.leapMotionIndex1Field, parameterObject.leapMotionIndex2Field,
            parameterObject.leapMotionIndex3Field, parameterObject.leapMotionInvScaleField,
            parameterObject.leapMotionOffsetField, parameterObject.leapMotionAddButton, parameterObject.leapMotionDuplicateButton,
            parameterObject.leapMotionDeleteButton, parameterObject.leapMotionClearButton, parameterObject.leapMotionResetConfigButton);

    data.toggleDetachedPreviewButton = parameterObject.toggleDetachedPreviewButton;
    data.cameraRollREd = parameterObject.pCameraRollREd;
    data.cameraRollSlider = parameterObject.pCameraRollSlider;
    data.cameraPitchREd = parameterObject.pCameraPitchREd;
    data.cameraPitchSlider = parameterObject.pCameraPitchSlider;
    data.cameraYawREd = parameterObject.pCameraYawREd;
    data.cameraYawSlider = parameterObject.pCameraYawSlider;
    data.cameraBankREd = parameterObject.pCameraBankREd;
    data.cameraBankSlider = parameterObject.pCameraBankSlider;
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
    data.dimishZColorButton = parameterObject.pDimishZColorButton;
    data.dimZDistanceREd = parameterObject.pDimZDistanceREd;
    data.dimZDistanceSlider = parameterObject.pDimZDistanceSlider;
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
    data.lowDensityBrightnessREd = parameterObject.lowDensityBrightnessREd;
    data.lowDensityBrightnessSlider = parameterObject.lowDensityBrightnessSlider;
    data.balanceRedREd = parameterObject.balanceRedREd;
    data.balanceRedSlider = parameterObject.balanceRedSlider;
    data.balanceGreenREd = parameterObject.balanceGreenREd;
    data.balanceGreenSlider = parameterObject.balanceGreenSlider;
    data.balanceBlueREd = parameterObject.balanceBlueREd;
    data.balanceBlueSlider = parameterObject.balanceBlueSlider;
    data.saturationREd = parameterObject.saturationREd;
    data.saturationSlider = parameterObject.saturationSlider;
    data.filterRadiusREd = parameterObject.pFilterRadiusREd;
    data.tinaFilterSharpnessREd = parameterObject.tinaFilterSharpnessREd;
    data.tinaFilterSharpnessSlider = parameterObject.tinaFilterSharpnessSlider;
    data.tinaFilterLowDensityREd = parameterObject.tinaFilterLowDensityREd;
    data.tinaFilterLowDensitySlider = parameterObject.tinaFilterLowDensitySlider;

    data.filterRadiusSlider = parameterObject.pFilterRadiusSlider;
    data.filterKernelCmb = parameterObject.pFilterKernelCmb;
    data.tinaFilterTypeCmb = parameterObject.tinaFilterTypeCmb;
    data.tinaFilterKernelCmbLbl = parameterObject.tinaFilterKernelCmbLbl;
    data.tinaFilterRadiusLbl = parameterObject.tinaFilterRadiusLbl;
    data.tinaFilterIndicatorCBx = parameterObject.tinaFilterIndicatorCBx;
    data.gammaThresholdREd = parameterObject.pGammaThresholdREd;
    data.gammaThresholdSlider = parameterObject.pGammaThresholdSlider;
    data.bgTransparencyCBx = parameterObject.pBGTransparencyCBx;
    data.paletteRandomPointsREd = parameterObject.pPaletteRandomPointsREd;
    data.paletteRandomGeneratorCmb = parameterObject.paletteRandomGeneratorCmb;
    data.paletteFadeColorsCBx = parameterObject.paletteFadeColorsCBx;
    data.paletteUniformWidthCBx = parameterObject.paletteUniformWidthCBx;
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
    data.gpuQualityProfileCmb = parameterObject.gpuQualityProfileCmb;
    data.gpuResolutionProfileCmb = parameterObject.gpuResolutionProfileCmb;
    data.interactiveResolutionProfileCmb = parameterObject.pInteractiveResolutionProfileCmb;
    data.swfAnimatorResolutionProfileCmb = parameterObject.pSWFAnimatorResolutionProfileCmb;
    data.swfAnimatorQualityProfileCmb = parameterObject.swfAnimatorQualityProfileCmb;
    data.transformationsTable = parameterObject.pTransformationsTable;
    data.affineC00Lbl = parameterObject.pAffineC00Lbl;
    data.affineC01Lbl = parameterObject.pAffineC01Lbl;
    data.affineC10Lbl = parameterObject.pAffineC10Lbl;
    data.affineC11Lbl = parameterObject.pAffineC11Lbl;
    data.affineC20Lbl = parameterObject.pAffineC20Lbl;
    data.affineC21Lbl = parameterObject.pAffineC21Lbl;
    data.affineC00REd = parameterObject.pAffineC00REd;
    data.affineC01REd = parameterObject.pAffineC01REd;
    data.affineC10REd = parameterObject.pAffineC10REd;
    data.affineC11REd = parameterObject.pAffineC11REd;
    data.affineC20REd = parameterObject.pAffineC20REd;
    data.affineC21REd = parameterObject.pAffineC21REd;
    data.affineCoordsViewTypeCmb = parameterObject.affineCoordsViewTypeCmb;
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

    data.xFormMaterialREd = parameterObject.xFormMaterialREd;
    data.xFormMaterialSlider = parameterObject.xFormMaterialSlider;
    data.xFormMaterialSpeedREd = parameterObject.xFormMaterialSpeedREd;
    data.xFormMaterialSpeedSlider = parameterObject.xFormMaterialSpeedSlider;

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

    data.xFormModHueREd = parameterObject.pXFormModHueREd;
    data.xFormModHueSlider = parameterObject.pXFormModHueSlider;
    data.xFormModHueSpeedREd = parameterObject.pXFormModHueSpeedREd;
    data.xFormModHueSpeedSlider = parameterObject.pXFormModHueSpeedSlider;

    data.xFormOpacityREd = parameterObject.pXFormOpacityREd;
    data.xFormOpacitySlider = parameterObject.pXFormOpacitySlider;
    data.xFormDrawModeCmb = parameterObject.pXFormDrawModeCmb;
    data.xFormColorTypeCmb = parameterObject.pXFormColorTypeCmb;
    data.xFormTargetColorBtn = parameterObject.pXFormTargetColorBtn;

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

    data.layerDensityREd = parameterObject.layerDensityREd;
    data.layerWeightEd = parameterObject.layerWeightEd;
    data.layerAddBtn = parameterObject.layerAddBtn;
    data.layerDuplicateBtn = parameterObject.layerDuplicateBtn;
    data.layerDeleteBtn = parameterObject.layerDeleteBtn;
    data.layerExtractBtn = parameterObject.layerExtractBtn;
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
    data.flameFPSField = parameterObject.flameFPSField;
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
    randomBatchProgressUpdater = new RenderProgressUpdater(new RenderProgressBarHolder() {
      @Override
      public JProgressBar getRenderProgressBar() {
        return parameterObject.randomBatchProgressBar;
      }
    });
    data.affineResetTransformButton = parameterObject.pAffineResetTransformButton;
    data.createPaletteColorsTable = parameterObject.pCreatePaletteColorsTable;
    data.randomBatchButton = parameterObject.randomBatchButton;
    data.randomBatchProgressBar = parameterObject.randomBatchProgressBar;
    data.postBlurRadiusREd = parameterObject.postBlurRadiusREd;
    data.postBlurRadiusSlider = parameterObject.postBlurRadiusSlider;
    data.postBlurFadeREd = parameterObject.postBlurFadeREd;
    data.postBlurFadeSlider = parameterObject.postBlurFadeSlider;
    data.postBlurFallOffREd = parameterObject.postBlurFallOffREd;
    data.postBlurFallOffSlider = parameterObject.postBlurFallOffSlider;

    data.tinaZBufferScaleREd = parameterObject.tinaZBufferScaleREd;
    data.tinaZBufferScaleSlider = parameterObject.tinaZBufferScaleSlider;
    data.tinaZBufferBiasREd = parameterObject.tinaZBufferBiasREd;
    data.tinaZBufferBiasSlider = parameterObject.tinaZBufferBiasSlider;
    data.tinaZBufferFilename1 = parameterObject.tinaZBufferFilename1;
    data.tinaZBufferFilename2 = parameterObject.tinaZBufferFilename2;

    data.tinaSpatialOversamplingREd = parameterObject.tinaSpatialOversamplingREd;
    data.tinaSpatialOversamplingSlider = parameterObject.tinaSpatialOversamplingSlider;
    data.filterKernelPreviewRootPnl = parameterObject.filterKernelPreviewRootPnl;
    data.filterKernelFlatPreviewBtn = parameterObject.filterKernelFlatPreviewBtn;
    data.tinaPostNoiseFilterCheckBox = parameterObject.tinaPostNoiseFilterCheckBox;
    data.tinaPostNoiseThresholdField = parameterObject.tinaPostNoiseThresholdField;
    data.tinaPostNoiseThresholdSlider = parameterObject.tinaPostNoiseThresholdSlider;
    data.tinaOptiXDenoiserCheckBox = parameterObject.tinaOptiXDenoiserCheckBox;
    data.tinaOptiXDenoiserBlendField = parameterObject.tinaOptiXDenoiserBlendField;
    data.tinaOptiXDenoiserBlendSlider = parameterObject.tinaOptiXDenoiserBlendSlider;

    data.foregroundOpacityField = parameterObject.foregroundOpacityField;
    data.foregroundOpacitySlider = parameterObject.foregroundOpacitySlider;

    data.solidRenderingToggleBtn = parameterObject.solidRenderingToggleBtn;
    data.tinaSolidRenderingEnableAOCBx = parameterObject.tinaSolidRenderingEnableAOCBx;
    data.tinaSolidRenderingAOIntensityREd = parameterObject.tinaSolidRenderingAOIntensityREd;
    data.tinaSolidRenderingAOIntensitySlider = parameterObject.tinaSolidRenderingAOIntensitySlider;
    data.tinaSolidRenderingAOSearchRadiusREd = parameterObject.tinaSolidRenderingAOSearchRadiusREd;
    data.tinaSolidRenderingAOSearchRadiusSlider = parameterObject.tinaSolidRenderingAOSearchRadiusSlider;
    data.tinaSolidRenderingAOBlurRadiusREd = parameterObject.tinaSolidRenderingAOBlurRadiusREd;
    data.tinaSolidRenderingAOBlurRadiusSlider = parameterObject.tinaSolidRenderingAOBlurRadiusSlider;
    data.tinaSolidRenderingAOFalloffREd = parameterObject.tinaSolidRenderingAOFalloffREd;
    data.tinaSolidRenderingAOFalloffSlider = parameterObject.tinaSolidRenderingAOFalloffSlider;
    data.tinaSolidRenderingAORadiusSamplesREd = parameterObject.tinaSolidRenderingAORadiusSamplesREd;
    data.tinaSolidRenderingAORadiusSamplesSlider = parameterObject.tinaSolidRenderingAORadiusSamplesSlider;
    data.tinaSolidRenderingAOAzimuthSamplesREd = parameterObject.tinaSolidRenderingAOAzimuthSamplesREd;
    data.tinaSolidRenderingAOAzimuthSamplesSlider = parameterObject.tinaSolidRenderingAOAzimuthSamplesSlider;
    data.tinaSolidRenderingAOAffectDiffuseREd = parameterObject.tinaSolidRenderingAOAffectDiffuseREd;
    data.tinaSolidRenderingAOAffectDiffuseSlider = parameterObject.tinaSolidRenderingAOAffectDiffuseSlider;
    data.tinaSolidRenderingShadowTypeCmb = parameterObject.tinaSolidRenderingShadowTypeCmb;
    data.tinaSolidRenderingShadowmapSizeCmb = parameterObject.tinaSolidRenderingShadowmapSizeCmb;
    data.tinaSolidRenderingShadowSmoothRadiusREd = parameterObject.tinaSolidRenderingShadowSmoothRadiusREd;
    data.tinaSolidRenderingShadowSmoothRadiusSlider = parameterObject.tinaSolidRenderingShadowSmoothRadiusSlider;
    data.tinaSolidRenderingShadowmapBiasREd = parameterObject.tinaSolidRenderingShadowmapBiasREd;
    data.tinaSolidRenderingShadowmapBiasSlider = parameterObject.tinaSolidRenderingShadowmapBiasSlider;
    data.bokehSettingsPnl = parameterObject.bokehSettingsPnl;
    data.postBokehSettingsPnl = parameterObject.postBokehSettingsPnl;
    data.resetPostBokehSettingsBtn = parameterObject.resetPostBokehSettingsBtn;
    data.postBokehIntensityREd = parameterObject.postBokehIntensityREd;
    data.postBokehIntensitySlider = parameterObject.postBokehIntensitySlider;
    data.postBokehBrightnessREd = parameterObject.postBokehBrightnessREd;
    data.postBokehBrightnessSlider = parameterObject.postBokehBrightnessSlider;
    data.postBokehSizeREd = parameterObject.postBokehSizeREd;
    data.postBokehSizeSlider = parameterObject.postBokehSizeSlider;
    data.postBokehActivationREd = parameterObject.postBokehActivationREd;
    data.postBokehActivationSlider = parameterObject.postBokehActivationSlider;
    data.postBokehFilterKernelCmb = parameterObject.postBokehFilterKernelCmb;
    data.thumbnailSelectPopupMenu = parameterObject.thumbnailSelectPopupMenu;
    data.thumbnailRemovePopupMenu = parameterObject.thumbnailRemovePopupMenu;

    data.resetSolidRenderingMaterialsBtn = parameterObject.resetSolidRenderingMaterialsBtn;
    data.resetSolidRenderingLightsBtn = parameterObject.resetSolidRenderingLightsBtn;
    data.resetSolidRenderingHardShadowOptionsBtn = parameterObject.resetSolidRenderingHardShadowOptionsBtn;
    data.resetSolidRenderingAmbientShadowOptionsBtn = parameterObject.resetSolidRenderingAmbientShadowOptionsBtn;
    data.tinaSolidRenderingSelectedLightCmb = parameterObject.tinaSolidRenderingSelectedLightCmb;
    data.tinaSolidRenderingAddLightBtn = parameterObject.tinaSolidRenderingAddLightBtn;
    data.tinaSolidRenderingDeleteLightBtn = parameterObject.tinaSolidRenderingDeleteLightBtn;
    data.tinaSolidRenderingLightAltitudeREd = parameterObject.tinaSolidRenderingLightPosAltitudeREd;
    data.tinaSolidRenderingLightAzimuthREd = parameterObject.tinaSolidRenderingLightPosAzimuthREd;
    data.tinaSolidRenderingLightAltitudeSlider = parameterObject.tinaSolidRenderingLightAltitudeSlider;
    data.tinaSolidRenderingLightAzimuthSlider = parameterObject.tinaSolidRenderingLightAzimuthSlider;
    data.tinaSolidRenderingLightColorBtn = parameterObject.tinaSolidRenderingLightColorBtn;
    data.tinaSolidRenderingLightCastShadowsCBx = parameterObject.tinaSolidRenderingLightCastShadowsCBx;
    data.tinaSolidRenderingLightIntensityREd = parameterObject.tinaSolidRenderingLightIntensityREd;
    data.tinaSolidRenderingLightIntensitySlider = parameterObject.tinaSolidRenderingLightIntensitySlider;
    data.tinaSolidRenderingShadowIntensityREd = parameterObject.tinaSolidRenderingShadowIntensityREd;
    data.tinaSolidRenderingShadowIntensitySlider = parameterObject.tinaSolidRenderingShadowIntensitySlider;
    data.tinaSolidRenderingSelectedMaterialCmb = parameterObject.tinaSolidRenderingSelectedMaterialCmb;
    data.tinaSolidRenderingAddMaterialBtn = parameterObject.tinaSolidRenderingAddMaterialBtn;
    data.tinaSolidRenderingDeleteMaterialBtn = parameterObject.tinaSolidRenderingDeleteMaterialBtn;
    data.tinaSolidRenderingMaterialDiffuseREd = parameterObject.tinaSolidRenderingMaterialDiffuseREd;
    data.tinaSolidRenderingMaterialDiffuseSlider = parameterObject.tinaSolidRenderingMaterialDiffuseSlider;
    data.tinaSolidRenderingMaterialAmbientREd = parameterObject.tinaSolidRenderingMaterialAmbientREd;
    data.tinaSolidRenderingMaterialAmbientSlider = parameterObject.tinaSolidRenderingMaterialAmbientSlider;
    data.tinaSolidRenderingMaterialSpecularREd = parameterObject.tinaSolidRenderingMaterialSpecularREd;
    data.tinaSolidRenderingMaterialSpecularSlider = parameterObject.tinaSolidRenderingMaterialSpecularSlider;
    data.tinaSolidRenderingMaterialSpecularSharpnessREd = parameterObject.tinaSolidRenderingMaterialSpecularSharpnessREd;
    data.tinaSolidRenderingMaterialSpecularSharpnessSlider = parameterObject.tinaSolidRenderingMaterialSpecularSharpnessSlider;
    data.tinaSolidRenderingMaterialSpecularColorBtn = parameterObject.tinaSolidRenderingMaterialSpecularColorBtn;
    data.tinaSolidRenderingMaterialDiffuseResponseCmb = parameterObject.tinaSolidRenderingMaterialDiffuseResponseCmb;
    data.tinaSolidRenderingMaterialReflectionMapIntensityREd = parameterObject.tinaSolidRenderingMaterialReflectionMapIntensityREd;
    data.tinaSolidRenderingMaterialReflectionMapIntensitySlider = parameterObject.tinaSolidRenderingMaterialReflectionMapIntensitySlider;
    data.tinaSolidRenderingMaterialReflectionMappingCmb = parameterObject.tinaSolidRenderingMaterialReflectionMappingCmb;
    data.tinaSolidRenderingMaterialReflMapBtn = parameterObject.tinaSolidRenderingMaterialReflMapBtn;
    data.tinaSolidRenderingMaterialSelectReflMapBtn = parameterObject.tinaSolidRenderingMaterialSelectReflMapBtn;
    data.tinaSolidRenderingMaterialRemoveReflMapBtn = parameterObject.tinaSolidRenderingMaterialRemoveReflMapBtn;

    data.mouseTransformSlowButton = parameterObject.pMouseTransformSlowButton;
    data.toggleTriangleWithColorsButton = parameterObject.toggleTriangleWithColorsButton;
    data.realtimePreviewToggleButton = parameterObject.realtimePreviewToggleButton;

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
    rootPanel = parameterObject.pRootPanel;
    data.helpPane = parameterObject.pHelpPane;
    data.apophysisHintsPane = parameterObject.apophysisHintsPane;
    data.getColorTypesPane = parameterObject.getColorTypesPane;

    data.undoButton = parameterObject.pUndoButton;
    data.redoButton = parameterObject.pRedoButton;
    data.editTransformCaptionButton = parameterObject.editTransformCaptionButton;
    data.editFlameTileButton = parameterObject.editFlameTileButton;
    data.snapShotButton = parameterObject.snapShotButton;
    data.qSaveButton = parameterObject.qSaveButton;
    data.saveAllButton = parameterObject.saveAllButton;
    data.sendToIRButton = parameterObject.sendToIRButton;
    data.bokehButton = parameterObject.bokehButton;
    data.movieButton = parameterObject.movieButton;
    data.transformSlowButton = parameterObject.transformSlowButton;
    data.transparencyButton = parameterObject.transparencyButton;
    data.scriptTree = parameterObject.scriptTree;
    data.scriptDescriptionTextArea = parameterObject.scriptDescriptionTextArea;
    data.scriptTextArea = parameterObject.scriptTextArea;
    data.rescanScriptsBtn = parameterObject.rescanScriptsBtn;
    data.newScriptBtn = parameterObject.newScriptBtn;
    data.newScriptFromFlameBtn = parameterObject.newScriptFromFlameBtn;
    data.deleteScriptBtn = parameterObject.deleteScriptBtn;
    data.scriptRenameBtn = parameterObject.scriptRenameBtn;
    data.scriptDuplicateBtn = parameterObject.scriptDuplicateBtn;
    data.scriptRunBtn = parameterObject.scriptRunBtn;
    data.scriptEditBtn = parameterObject.scriptEditBtn;
    data.gradientLibTree = parameterObject.gradientLibTree;
    data.backgroundColorTypeCmb = parameterObject.backgroundColorTypeCmb;
    data.backgroundColorIndicatorBtn = parameterObject.backgroundColorIndicatorBtn;
    data.backgroundColorURIndicatorBtn = parameterObject.backgroundColorURIndicatorBtn;
    data.backgroundColorLLIndicatorBtn = parameterObject.backgroundColorLLIndicatorBtn;
    data.backgroundColorLRIndicatorBtn = parameterObject.backgroundColorLRIndicatorBtn;
    data.backgroundColorCCIndicatorBtn = parameterObject.backgroundColorCCIndicatorBtn;
    data.toggleDrawGridButton = parameterObject.toggleDrawGridButton;
    data.toggleDrawGuidesButton = parameterObject.toggleDrawGuidesButton;
    data.triangleStyleCmb = parameterObject.triangleStyleCmb;
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

    data.gradientCurveEditorSaveBtn = parameterObject.gradientCurveEditorSaveBtn;
    data.gradientCurveEditorHueRootPanel = parameterObject.gradientCurveEditorHueRootPanel;
    data.gradientCurveEditorSaturationRootPanel = parameterObject.gradientCurveEditorSaturationRootPanel;
    data.gradientCurveEditorLuminosityRootPanel = parameterObject.gradientCurveEditorLuminosityRootPanel;

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
    data.affineXYEditPlaneToggleBtn = parameterObject.affineXYEditPlaneToggleBtn;
    data.affineYZEditPlaneToggleBtn = parameterObject.affineYZEditPlaneToggleBtn;
    data.affineZXEditPlaneToggleBtn = parameterObject.affineZXEditPlaneToggleBtn;

    weightMapData.weightingFieldTypeCmb = parameterObject.weightingFieldTypeCmb;
    weightMapData.weightingFieldInputCmb = parameterObject.weightingFieldInputCmb;
    weightMapData.weightingFieldColorIntensityREd = parameterObject.weightingFieldColorIntensityREd;
    weightMapData.weightingFieldVariationIntensityREd = parameterObject.weightingFieldVariationIntensityREd;
    weightMapData.weightingFieldJitterIntensityREd = parameterObject.weightingFieldJitterIntensityREd;
    weightMapData.weightingFieldVarParam1AmountREd = parameterObject.weightingFieldVarParam1AmountREd;
    weightMapData.weightingFieldVarParam2AmountREd = parameterObject.weightingFieldVarParam2AmountREd;
    weightMapData.weightingFieldVarParam3AmountREd = parameterObject.weightingFieldVarParam3AmountREd;
    weightMapData.weightingFieldVarParam1NameCmb = parameterObject.weightingFieldVarParam1NameCmb;
    weightMapData.weightingFieldVarParam2NameCmb = parameterObject.weightingFieldVarParam2NameCmb;
    weightMapData.weightingFieldVarParam3NameCmb = parameterObject.weightingFieldVarParam3NameCmb;

    weightMapData.weightingFieldColorMapFilenameLbl = parameterObject.weightingFieldColorMapFilenameLbl;
    weightMapData.weightingFieldColorMapFilenameBtn = parameterObject.weightingFieldColorMapFilenameBtn;
    weightMapData.weightingFieldColorMapFilenameInfoLbl = parameterObject.weightingFieldColorMapFilenameInfoLbl;
    weightMapData.weightingFieldParam01REd = parameterObject.weightingFieldParam01REd;
    weightMapData.weightingFieldParam01Lbl = parameterObject.weightingFieldParam01Lbl;
    weightMapData.weightingFieldParam02REd = parameterObject.weightingFieldParam02REd;
    weightMapData.weightingFieldParam02Lbl = parameterObject.weightingFieldParam02Lbl;
    weightMapData.weightingFieldParam03REd = parameterObject.weightingFieldParam03REd;
    weightMapData.weightingFieldParam03Lbl = parameterObject.weightingFieldParam03Lbl;
    weightMapData.weightingFieldParam04Cmb = parameterObject.weightingFieldParam04Cmb;
    weightMapData.weightingFieldParam04Lbl = parameterObject.weightingFieldParam04Lbl;
    weightMapData.weightingFieldParam05REd = parameterObject.weightingFieldParam05REd;
    weightMapData.weightingFieldParam05Lbl = parameterObject.weightingFieldParam05Lbl;
    weightMapData.weightingFieldParam06REd = parameterObject.weightingFieldParam06REd;
    weightMapData.weightingFieldParam06Lbl = parameterObject.weightingFieldParam06Lbl;
    weightMapData.weightingFieldParam07REd = parameterObject.weightingFieldParam07REd;
    weightMapData.weightingFieldParam07Lbl = parameterObject.weightingFieldParam07Lbl;
    weightMapData.weightingFieldParam08Cmb = parameterObject.weightingFieldParam08Cmb;
    weightMapData.weightingFieldParam08Lbl = parameterObject.weightingFieldParam08Lbl;
    weightMapData.weightingFieldPreviewImgRootPanel = parameterObject.weightingFieldPreviewImgRootPanel;

    // end create
    flameControls = new FlameControlsDelegate(this, data, rootPanel);
    layerControls = new LayerControlsDelegate(this, data, rootPanel);
    xFormControls = new XFormControlsDelegate(this, data, weightMapData, rootPanel);
    gradientControls = new GradientControlsDelegate(this, data, rootPanel);
    channelMixerControls = new ChannelMixerControlsDelegate(this, errorHandler, data, rootPanel, true);
    gradientCurveEditorControls = new GradientCurveEditorControlsDelegate(this, errorHandler, data, rootPanel, true);

    messageHelper = new JFrameFlameMessageHelper(mainEditorFrame);

    flamePreviewHelper = new FlamePreviewHelper(errorHandler, centerPanel, data.toggleTransparencyButton,
            data.layerAppendBtn, data.layerPreviewBtn, mainProgressUpdater, this, this,
            this, this, messageHelper, this);

    registerMotionPropertyControls();

    qsaveFilenameGen = new QuickSaveFilenameGen(prefs);

    enableUndoControls();

    initHelpPane();
    initApophysisHintsPane();
    initGetColorTypesPane();

    nonlinearControls.initPanels();

    refreshPaletteColorsTable();
    getBatchRendererController().refreshRenderBatchJobsTable();

    enableControls();

    xFormControls.enableControls(null);
    layerControls.enableControls();

    refreshResolutionProfileCmb(data.resolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.interactiveResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.batchResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.gpuResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.swfAnimatorResolutionProfileCmb, null);
    refreshQualityProfileCmb(data.qualityProfileCmb, null);
    refreshQualityProfileCmb(data.batchQualityProfileCmb, null);
    refreshQualityProfileCmb(data.gpuQualityProfileCmb, null);
    refreshQualityProfileCmb(data.swfAnimatorQualityProfileCmb, null);

    getFlameBrowserController().init();
    loadScriptProps();
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
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initGetColorTypesPane() {
    data.getColorTypesPane.setContentType("text/html");
    try {
      InputStream is = this.getClass().getResourceAsStream("ColoringTypes.html");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();

      data.getColorTypesPane.setText(content.toString());
      data.getColorTypesPane.setSelectionStart(0);
      data.getColorTypesPane.setSelectionEnd(0);
    } catch (Exception ex) {
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
    } catch (Exception ex) {
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
    } finally {
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
    } finally {
      cmbRefreshing = oldCmbRefreshing;
    }
  }

  private boolean dragging = false;
  private boolean keypressing = false;

  @Override
  public FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      int width = centerPanel.getParent().getWidth();
      int height = centerPanel.getParent().getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(prefs, img, 0, 0, centerPanel.getParent().getWidth(), this, this, frameControlsUtil);
      flamePanel.getConfig().setWithColoredTransforms(prefs.isTinaEditorControlsWithColor());
      flamePanel.getConfig().setProgressivePreview(prefs.isTinaEditorProgressivePreview());
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
            refreshFlameImage(true, false, 1, true, false);
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
                  } else if (ctrlPressed) {
                    xForm_moveLeft(0.1);
                  } else if (shiftPressed) {
                    xForm_moveLeft(10.0);
                  } else {
                    xForm_moveLeft(1.0);
                  }
                } else if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(0, -1);
                }
                break;
              // right
              case 39:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
                  if (altPressed) {
                    xForm_rotateRight();
                  } else if (ctrlPressed) {
                    xForm_moveRight(0.1);
                  } else if (shiftPressed) {
                    xForm_moveRight(10.0);
                  } else {
                    xForm_moveRight(1.0);
                  }
                } else if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(0, 1);
                }
                break;
              // up
              case 38:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
                  if (altPressed) {
                    xForm_enlarge();
                  } else if (ctrlPressed) {
                    xForm_moveUp(0.1);
                  } else if (shiftPressed) {
                    xForm_moveUp(10.0);
                  } else {
                    xForm_moveUp(1.0);
                  }
                } else if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(1, 1);
                }
                break;
              // down
              case 40:
                if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
                  if (altPressed) {
                    xForm_shrink();
                  } else if (ctrlPressed) {
                    xForm_moveDown(0.1);
                  } else if (shiftPressed) {
                    xForm_moveDown(10.0);
                  } else {
                    xForm_moveDown(1.0);
                  }
                } else if (flamePanel.getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
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
          } finally {
            flamePanel.requestFocus();
            keypressing = false;
          }
        }

      });

      flamePanel.setSelectedXForm(getCurrXForm());
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
      int palettePoints = 3 + Tools.randomInt(21);
      boolean fadePaletteColors = Math.random() > 0.09;
      boolean uniformWidth = Math.random() > 0.75;
      RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, RandomSymmetryGeneratorList.SPARSE, RandomGradientGeneratorList.DEFAULT, RandomWeightingFieldGeneratorList.SPARSE, palettePoints, fadePaletteColors, uniformWidth, RandomBatchQuality.LOW);
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
      } else {
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

  public void refreshFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale, boolean pReRender, boolean pAllowUseCache) {
    flamePreviewHelper.refreshFlameImage(pQuickRender, pMouseDown, pDownScale, pReRender, pAllowUseCache);
  }

  public void fastRefreshFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale) {
    flamePreviewHelper.fastRefreshFlameImage(pQuickRender, pMouseDown, pDownScale);
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
    } finally {
      gridRefreshing = false;
    }
    refreshFilterKernelPreviewImg();
    refreshLayerUI();
    refreshLayerControls(getCurrLayer());
    refreshKeyFramesUI();
  }

  public void refreshKeyFramesUI() {
    noRefresh = true;
    try {
      animationController.refreshUI();
    } finally {
      noRefresh = false;
    }
  }

  public int getCurrFrame() {
    return animationController != null ? animationController.getCurrFrame() : 1;
  }

  public void refreshLayerUI() {
    noRefresh = true;
    try {
      flameControls.refreshFlameValues();

      refreshBGColorIndicators();

      data.affinePreserveZButton.setSelected(getCurrFlame().isPreserveZ());

      gridRefreshing = true;
      try {
        int selectedTransform = data.transformationsTable.getSelectedRow();
        refreshTransformationsTable();
        if (selectedTransform >= 0 && selectedTransform < data.transformationsTable.getRowCount()) {
          try {
            data.transformationsTable.setRowSelectionInterval(selectedTransform, selectedTransform);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      } finally {
        gridRefreshing = false;
      }
      transformationChanged(true);

      enableControls();
      layerControls.enableControls();
      gradientCurveEditorControls.enableControls();
      channelMixerControls.refreshValues(true);
      refreshPaletteUI(getCurrLayer().getPalette());
    } finally {
      noRefresh = false;
    }
  }

  private void refreshBGColorIndicators() {
    refreshBGColorULIndicator();
    refreshBGColorURIndicator();
    refreshBGColorLLIndicator();
    refreshBGColorLRIndicator();
    refreshBGColorCCIndicator();
  }

  public void notifyRandGenFinished() {
    createRandomBatchThread = null;
    refreshRandomBatchButton();
    enableControls();
    if (loadFirstRandomFlame) {
      importFromRandomBatch(0);
      scrollThumbnailsToTop();
    }
  }

  public void initRandomizerHintsPanel(JTextPane hintPane) {
    hintPane.setContentType("text/html");
    try {
      Font f = new Font(Font.SANS_SERIF, 3, 10);
      hintPane.setFont(f);

      InputStream is = this.getClass().getResourceAsStream("randomizers.html");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();

      hintPane.setText(content.toString());
      hintPane.setSelectionStart(0);
      hintPane.setSelectionEnd(0);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void cancelBackgroundRender() {
    flamePreviewHelper.cancelBackgroundRender();
  }

  public static class TransformationsTableCellRenderer extends DefaultTableCellRenderer {
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
      } else {
        c.setForeground(table.getForeground());
      }
      return c;
    }
  }

  private void refreshTransformationsTable() {
    final int COL_TRANSFORM = 0;
    final int COL_VARIATIONS = 1;
    final int COL_WEIGHT = 2;
    if (data.toggleTriangleWithColorsButton.isSelected()) {
      data.transformationsTable.setDefaultRenderer(Object.class, new TransformationsTableCellRenderer());
    } else {
      data.transformationsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
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
          if (xForm.getColorType() == ColorType.UNSET) {
            xForm.setColorType(rowIndex < getCurrLayer().getXForms().size() ? ColorType.DIFFUSION : ColorType.NONE);
          }
          switch (columnIndex) {
            case COL_TRANSFORM:
              return rowIndex < getCurrLayer().getXForms().size() ? "Transf" + String.valueOf(rowIndex + 1) : "Final" + String.valueOf(rowIndex - getCurrLayer().getXForms().size() + 1);
            case COL_VARIATIONS:
              return getXFormCaption(xForm);
            case COL_WEIGHT:
              return rowIndex < getCurrLayer().getXForms().size() ? frameControlsUtil.getEvaluatedPropertyValue(xForm, "weight") : "";
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
          refreshFlameImage(true, false, 1, true, false);
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
    final int COL_DENSITY = 3;
    final int COL_WEIGHT = 4;
    data.layersTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return getCurrFlame() != null ? getCurrFlame().getLayers().size() : 0;
      }

      @Override
      public int getColumnCount() {
        return 5;
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
          case COL_DENSITY:
            return "Density";
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
            case COL_DENSITY:
              if (layer != null) {
                return Tools.doubleToString(layer.getDensity());
              }
            case COL_WEIGHT:
              if (layer != null) {
                return Tools.doubleToString(layer.getWeight());
              }
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return column == COL_CAPTION || column == COL_VISIBLE || column == COL_DENSITY || column == COL_WEIGHT;
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
              refreshFlameImage(true, false, 1, true, false);
              break;
            case COL_DENSITY:
              saveUndoPoint();
              layer.setDensity(Tools.limitValue(Tools.stringToDouble((String) aValue), 0.0, 1.0));
              frameControlsUtil.updateControl(layer, null, data.layerDensityREd, "density", 1.0);
              //data.layerDensityREd.setValue(layer.getDensity());
              break;
            case COL_WEIGHT:
              saveUndoPoint();
              layer.setWeight(Tools.stringToDouble((String) aValue));
              //data.layerWeightEd.setValue(layer.getWeight());
              frameControlsUtil.updateControl(layer, null, data.layerWeightEd, "weight", 1.0);
              // refreshed automatically:
              // refreshFlameImage(false);
              break;
            default: // nothing to do
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
    } else {
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
            default: // nothing to do
              break;
          }
          refreshPaletteColorsTable();
          RGBPalette palette = RandomGradientGenerator.generatePalette(data.paletteKeyFrames, data.paletteFadeColorsCBx.isSelected(), data.paletteUniformWidthCBx.isSelected());
          saveUndoPoint();
          getCurrLayer().setPalette(palette);
          setLastGradient(palette);
          refreshPaletteUI(palette);
          refreshFlameImage(true, false, 1, true, false);
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
          } else {
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
          refreshFlameImage(true, false, 1, true, false);
        }
        super.setValueAt(aValue, row, column);
      }

    });
    data.relWeightsTable.getTableHeader().setFont(data.relWeightsTable.getFont());
    data.relWeightsTable.getColumnModel().getColumn(COL_TRANSFORM).setWidth(20);
    data.relWeightsTable.getColumnModel().getColumn(COL_WEIGHT).setWidth(16);
  }

  public void refreshPaletteUI(RGBPalette pPalette) {
    frameControlsUtil.updateControl(pPalette, data.paletteRedSlider, data.paletteRedREd, "modRed", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteGreenSlider, data.paletteGreenREd, "modGreen", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteBlueSlider, data.paletteBlueREd, "modBlue", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteContrastSlider, data.paletteContrastREd, "modContrast", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteHueSlider, data.paletteHueREd, "modHue", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteBrightnessSlider, data.paletteBrightnessREd, "modBrightness", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteSwapRGBSlider, data.paletteSwapRGBREd, "modSwapRGB", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteFrequencySlider, data.paletteFrequencyREd, "modFrequency", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteBlurSlider, data.paletteBlurREd, "modBlur", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteGammaSlider, data.paletteGammaREd, "modGamma", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteShiftSlider, data.paletteShiftREd, "modShift", 1.0);
    frameControlsUtil.updateControl(pPalette, data.paletteSaturationSlider, data.paletteSaturationREd, "modSaturation", 1.0);
    refreshPaletteImg();
    gradientCurveEditorControls.refreshValues(true);
  }

  void refreshPaletteImg() {
    try {
      if (getCurrLayer() != null) {
        ImagePanel panels[] = {getPalettePanel(), getColorChooserPalettePanel()};
        for (ImagePanel imgPanel : panels) {
          int width = imgPanel.getWidth();
          int height = imgPanel.getHeight();
          if (width >= 16 && height >= 4) {
            if (getCurrLayer().getGradientMapFilename() != null && getCurrLayer().getGradientMapFilename().length() > 0) {
              SimpleImage img = (SimpleImage) RessourceManager.getImage(getCurrLayer().getGradientMapFilename());
              imgPanel.setImage(img, 0, 0, width, height);
            } else {
              SimpleImage img = new RGBPaletteRenderer().renderHorizPalette(getCurrLayer().getPalette(), width, height);
              imgPanel.setImage(img);
            }
          }
          imgPanel.getParent().validate();
          imgPanel.repaint();
        }
      }
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void paletteSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null)
      return;
    noRefresh = true;
    try {
      frameControlsUtil.valueChangedBySlider(getCurrLayer().getPalette(), pSlider, pTextField, pProperty, pSliderScale);
      try {
        Class<?> cls = getCurrLayer().getPalette().getClass();
        Field field = cls.getDeclaredField("modified");
        field.setAccessible(true);
        field.setBoolean(getCurrLayer().getPalette(), true);
      } catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshPaletteImg();
      refreshFlameImage(true, false, 1, true, false);
    } finally {
      noRefresh = false;
    }
  }

  private void layerSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null || getCurrLayer() == null)
      return;
    noRefresh = true;
    try {
      frameControlsUtil.valueChangedBySlider(getCurrLayer(), pSlider, pTextField, pProperty, pSliderScale);
      refreshFlameImage(true, false, 1, true, false);
    } finally {
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
      frameControlsUtil.valueChangedBySlider(xForm, pSlider, pTextField, pProperty, pSliderScale);
      refreshFlameImage(true, false, 1, true, false);
    } finally {
      noRefresh = false;
    }
  }

  private void paletteTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null)
      return;
    noRefresh = true;
    try {
      frameControlsUtil.valueChangedByTextField(getCurrLayer().getPalette(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      try {
        Class<?> cls = getCurrLayer().getPalette().getClass();
        Field field = cls.getDeclaredField("modified");
        field.setAccessible(true);
        field.setBoolean(getCurrLayer().getPalette(), true);
      } catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshPaletteImg();
      refreshFlameImage(true, false, 1, true, false);
    } finally {
      noRefresh = false;
    }
  }

  private void paletteTextFieldReset(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null)
      return;
    noRefresh = true;
    try {
      pTextField.setValue(getFrameControlsUtil().getRawPropertyValue(new Flame().getFirstLayer().getPalette(), pProperty));
      frameControlsUtil.valueChangedByTextField(getCurrLayer().getPalette(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      try {
        Class<?> cls = getCurrLayer().getPalette().getClass();
        Field field = cls.getDeclaredField("modified");
        field.setAccessible(true);
        field.setBoolean(getCurrLayer().getPalette(), true);
      } catch (Throwable ex) {
        ex.printStackTrace();
      }
      refreshPaletteImg();
      refreshFlameImage(true, false, 1, true, false);
    } finally {
      noRefresh = false;
    }
  }

  private void layerTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null || getCurrLayer() == null)
      return;
    noRefresh = true;
    try {
      frameControlsUtil.valueChangedByTextField(getCurrLayer(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      refreshFlameImage(true, false, 1, true, false);
    } finally {
      noRefresh = false;
    }
  }

  private void layerTextFieldReset(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null || getCurrLayer() == null)
      return;
    noRefresh = true;
    try {
      pTextField.setValue(getFrameControlsUtil().getRawPropertyValue(new Flame().getFirstLayer(), pProperty));
      frameControlsUtil.valueChangedByTextField(getCurrLayer(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      refreshFlameImage(true, false, 1, true, false);
    } finally {
      noRefresh = false;
    }
  }

  protected void xFormTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm == null) {
      return;
    }
    noRefresh = true;
    try {
      frameControlsUtil.valueChangedByTextField(xForm, pSlider, pTextField, pProperty, pSliderScale, 0.0);
      refreshFlameImage(true, false, 1, true, false);
    } finally {
      noRefresh = false;
    }
  }

  protected void xFormTextFieldReset(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm == null) {
      return;
    }
    noRefresh = true;
    try {
      pTextField.setValue(getFrameControlsUtil().getRawPropertyValue(new XForm(), pProperty));
      frameControlsUtil.valueChangedByTextField(xForm, pSlider, pTextField, pProperty, pSliderScale, 0.0);
      refreshFlameImage(true, false, 1, true, false);
    } finally {
      noRefresh = false;
    }
  }

  public void loadFlameButton_actionPerformed(ActionEvent e) {
    try {
      JFileChooser chooser = new FlameFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
        } catch (Exception ex) {
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
        } else {
          setCurrFlame(flame);
          getCurrFlame().setLastFilename(file.getName());
          undoManager.initUndoStack(getCurrFlameStack());

          for (int i = flames.size() - 1; i >= 1; i--) {
            randomBatch.add(0, new FlameThumbnail(flames.get(i), null, null));
          }
          updateThumbnails();
          setupProfiles(getCurrFlame());
          refreshUI();
          if (flames.size() > 1) {
            messageHelper.showStatusMessage(flames.size() + " flame opened from disc");
          } else {
            messageHelper.showStatusMessage(getCurrFlame(), "opened from disc");
          }
        }
      }
    } catch (Throwable ex) {
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
      RGBPalette palette = RandomGradientGenerator.generatePalette(data.paletteKeyFrames, data.paletteFadeColorsCBx.isSelected(), data.paletteUniformWidthCBx.isSelected());
      saveUndoPoint();
      getCurrLayer().setPalette(palette);
      setLastGradient(palette);
      refreshPaletteUI(palette);
      refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void tinaCreateSimilarPaletteButton_actionPerformed(ActionEvent e) {
    if (getCurrFlame() != null && getCurrLayer() != null) {
      saveUndoPoint();
      data.paletteKeyFrames = new SimilarGradientCreator().createKeyFrames(getCurrLayer().getPalette().getTransformedColors());
      RGBPalette palette = RandomGradientGenerator.generatePalette(data.paletteKeyFrames, data.paletteFadeColorsCBx.isSelected(), data.paletteUniformWidthCBx.isSelected());
      getCurrLayer().setPalette(palette);
      setLastGradient(palette);
      refreshPaletteUI(palette);
      refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void grabPaletteFromFlameButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new FlameFileChooser(prefs);
    if (prefs.getInputFlamePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
      } catch (Exception ex) {
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
      refreshFlameImage(true, false, 1, true, false);
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
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          String filename = file.getAbsolutePath();
          if (!filename.endsWith("." + Tools.FILEEXT_FLAME)) {
            filename += "." + Tools.FILEEXT_FLAME;
          }
          new FlameWriter().writeFlame(generateExportFlame(getCurrFlame()), filename);
          getCurrFlame().setLastFilename(file.getName());
          messageHelper.showStatusMessage(getCurrFlame(), "flame saved to disc");
          prefs.setLastOutputFlameFile(file);
        }
      }
    } catch (Throwable ex) {
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
    transformationChanged(false);
  }

  public void transformationChanged(boolean pReRender) {
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
        nonlinearControls.resizeNonlinearParamsPanel();
        refreshFlameImage(true, false, 1, pReRender, false);
      } finally {
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
          } else {
            data.relWeightREd.setText(Tools.doubleToString(layer.getXForms().get(xaosRow).getModifiedWeights()[transformRow]));
          }
        } else {
          data.relWeightREd.setText("");
        }
        xFormControls.enableRelWeightsControls();
      } finally {
        cmbRefreshing = oldCmbRefreshing;
        gridRefreshing = oldGridRefreshing;
      }
    }
  }

  protected void enableControls() {
    boolean enabled = getCurrFlame() != null;
    data.editTransformCaptionButton.setEnabled(enabled);
    data.editFlameTileButton.setEnabled(enabled);
    data.snapShotButton.setEnabled(enabled);
    data.qSaveButton.setEnabled(enabled);
    {
      boolean hasSelFlames = false;
      if (randomBatch != null) {
        for (FlameThumbnail thumbnail : randomBatch) {
          if (thumbnail.getSelectCheckbox() != null && thumbnail.getSelectCheckbox().isSelected()) {
            hasSelFlames = true;
          }
        }
      }
      data.saveAllButton.setEnabled(hasSelFlames);
    }
    data.sendToIRButton.setEnabled(true);
    data.movieButton.setEnabled(true);
    data.bokehButton.setEnabled(enabled);
    data.solidRenderingToggleBtn.setEnabled(enabled);
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
    gradientCurveEditorControls.enableControls();
    enableUndoControls();
    getBatchRendererController().enableJobRenderControls();
    getQuiltRendererController().enableControls();
    getAnimationController().enableControls();
    getLeapMotionMainEditorController().enableControls();
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
        frameControlsUtil.updateControl(null, null, data.layerDensityREd, "", 1.0);
        frameControlsUtil.updateControl(null, null, data.layerWeightEd, "", 1.0);
        data.layerVisibleBtn.setSelected(true);
        frameControlsUtil.updateControl(null, data.gradientColorMapHorizOffsetSlider, data.gradientColorMapHorizOffsetREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.gradientColorMapHorizScaleSlider, data.gradientColorMapHorizScaleREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.gradientColorMapVertOffsetSlider, data.gradientColorMapVertOffsetREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.gradientColorMapVertScaleSlider, data.gradientColorMapVertScaleREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.gradientColorMapLocalColorAddSlider, data.gradientColorMapLocalColorAddREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.gradientColorMapLocalColorScaleSlider, data.gradientColorMapLocalColorScaleREd, "", 1.0);
      } else {
        frameControlsUtil.updateControl(pLayer, null, data.layerDensityREd, "density", 1.0);
        frameControlsUtil.updateControl(pLayer, null, data.layerWeightEd, "weight", 1.0);
        data.layerVisibleBtn.setSelected(pLayer.isVisible());
        frameControlsUtil.updateControl(pLayer, data.gradientColorMapHorizOffsetSlider, data.gradientColorMapHorizOffsetREd, "gradientMapHorizOffset", TinaController.SLIDER_SCALE_CENTRE);
        frameControlsUtil.updateControl(pLayer, data.gradientColorMapHorizScaleSlider, data.gradientColorMapHorizScaleREd, "gradientMapHorizScale", TinaController.SLIDER_SCALE_CENTRE);
        frameControlsUtil.updateControl(pLayer, data.gradientColorMapVertOffsetSlider, data.gradientColorMapVertOffsetREd, "gradientMapVertOffset", TinaController.SLIDER_SCALE_CENTRE);
        frameControlsUtil.updateControl(pLayer, data.gradientColorMapVertScaleSlider, data.gradientColorMapVertScaleREd, "gradientMapVertScale", TinaController.SLIDER_SCALE_CENTRE);
        frameControlsUtil.updateControl(pLayer, data.gradientColorMapLocalColorAddSlider, data.gradientColorMapLocalColorAddREd, "gradientMapLocalColorAdd", TinaController.SLIDER_SCALE_CENTRE);
        frameControlsUtil.updateControl(pLayer, data.gradientColorMapLocalColorScaleSlider, data.gradientColorMapLocalColorScaleREd, "gradientMapLocalColorScale", TinaController.SLIDER_SCALE_CENTRE);
      }
    } finally {
      gridRefreshing = oldGridRefreshing;
      refreshing = oldRefreshing;
      cmbRefreshing = oldCmbRefreshing;
      noRefresh = oldNoRefresh;
    }
  }

  protected void refreshXFormUI(XForm pXForm) {
    boolean oldRefreshing = refreshing;
    boolean oldGridRefreshing = gridRefreshing;
    boolean oldCmbRefreshing = cmbRefreshing;
    boolean oldNoRefresh = noRefresh;
    gridRefreshing = cmbRefreshing = refreshing = noRefresh = true;
    try {
      EditPlane curEditPlane = getCurrFlame().getEditPlane();
      refreshEditPlaneToggleButtons(curEditPlane);
      if (pXForm != null) {
        switch (curEditPlane) {
          case XY:
            data.affineC00Lbl.setText("X1");
            data.affineC01Lbl.setText("X2");
            data.affineC10Lbl.setText("Y1");
            data.affineC11Lbl.setText("Y2");
            break;
          case YZ:
            data.affineC00Lbl.setText("Y1");
            data.affineC01Lbl.setText("Y2");
            data.affineC10Lbl.setText("Z1");
            data.affineC11Lbl.setText("Z2");
            break;
          default:
            data.affineC00Lbl.setText("X1");
            data.affineC01Lbl.setText("X2");
            data.affineC10Lbl.setText("Z1");
            data.affineC11Lbl.setText("Z2");
            break;
        }
        if (data.affineEditPostTransformButton.isSelected()) {
          switch (pXForm.getEditPlane()) {
            case YZ:
              frameControlsUtil.updateControl(pXForm, null, data.affineC00REd, "yzPostCoeff00", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC01REd, "yzPostCoeff01", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC10REd, "yzPostCoeff10", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC11REd, "yzPostCoeff11", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC20REd, "yzPostCoeff20", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC21REd, "yzPostCoeff21", 1.0);
              break;
            case ZX:
              frameControlsUtil.updateControl(pXForm, null, data.affineC00REd, "zxPostCoeff00", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC01REd, "zxPostCoeff01", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC10REd, "zxPostCoeff10", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC11REd, "zxPostCoeff11", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC20REd, "zxPostCoeff20", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC21REd, "zxPostCoeff21", 1.0);
              break;
            case XY:
            default:
              frameControlsUtil.updateControl(pXForm, null, data.affineC00REd, "xyPostCoeff00", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC01REd, "xyPostCoeff01", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC10REd, "xyPostCoeff10", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC11REd, "xyPostCoeff11", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC20REd, "xyPostCoeff20", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC21REd, "xyPostCoeff21", 1.0);
              break;
          }
        } else {
          switch (pXForm.getEditPlane()) {
            case YZ:
              frameControlsUtil.updateControl(pXForm, null, data.affineC00REd, "yzCoeff00", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC01REd, "yzCoeff01", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC10REd, "yzCoeff10", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC11REd, "yzCoeff11", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC20REd, "yzCoeff20", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC21REd, "yzCoeff21", 1.0);
              break;
            case ZX:
              frameControlsUtil.updateControl(pXForm, null, data.affineC00REd, "zxCoeff00", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC01REd, "zxCoeff01", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC10REd, "zxCoeff10", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC11REd, "zxCoeff11", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC20REd, "zxCoeff20", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC21REd, "zxCoeff21", 1.0);
              break;
            case XY:
            default:
              frameControlsUtil.updateControl(pXForm, null, data.affineC00REd, "xyCoeff00", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC01REd, "xyCoeff01", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC10REd, "xyCoeff10", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC11REd, "xyCoeff11", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC20REd, "xyCoeff20", 1.0);
              frameControlsUtil.updateControl(pXForm, null, data.affineC21REd, "xyCoeff21", 1.0);
              break;
          }
        }

        frameControlsUtil.updateControl(pXForm, data.xFormColorSlider, data.xFormColorREd, "color", SLIDER_SCALE_COLOR);
        frameControlsUtil.updateControl(pXForm, data.xFormSymmetrySlider, data.xFormSymmetryREd, "colorSymmetry", SLIDER_SCALE_COLOR);

        frameControlsUtil.updateControl(pXForm, data.xFormMaterialSlider, data.xFormMaterialREd, "material", SLIDER_SCALE_COLOR);
        frameControlsUtil.updateControl(pXForm, data.xFormMaterialSpeedSlider, data.xFormMaterialSpeedREd, "materialSpeed", SLIDER_SCALE_COLOR);

        frameControlsUtil.updateControl(pXForm, data.xFormModGammaSlider, data.xFormModGammaREd, "modGamma", SLIDER_SCALE_COLOR);
        frameControlsUtil.updateControl(pXForm, data.xFormModGammaSpeedSlider, data.xFormModGammaSpeedREd, "modGammaSpeed", SLIDER_SCALE_COLOR);

        frameControlsUtil.updateControl(pXForm, data.xFormModContrastSlider, data.xFormModContrastREd, "modContrast", SLIDER_SCALE_COLOR);
        frameControlsUtil.updateControl(pXForm, data.xFormModContrastSpeedSlider, data.xFormModContrastSpeedREd, "modContrastSpeed", SLIDER_SCALE_COLOR);

        frameControlsUtil.updateControl(pXForm, data.xFormModSaturationSlider, data.xFormModSaturationREd, "modSaturation", SLIDER_SCALE_COLOR);
        frameControlsUtil.updateControl(pXForm, data.xFormModSaturationSpeedSlider, data.xFormModSaturationSpeedREd, "modSaturationSpeed", SLIDER_SCALE_COLOR);

        frameControlsUtil.updateControl(pXForm, data.xFormModHueSlider, data.xFormModHueREd, "modHue", SLIDER_SCALE_COLOR);
        frameControlsUtil.updateControl(pXForm, data.xFormModHueSpeedSlider, data.xFormModHueSpeedREd, "modHueSpeed", SLIDER_SCALE_COLOR);

        frameControlsUtil.updateControl(pXForm, data.xFormOpacitySlider, data.xFormOpacityREd, "opacity", SLIDER_SCALE_COLOR);

        data.xFormDrawModeCmb.setSelectedItem(pXForm.getDrawMode());
        data.xFormColorTypeCmb.setSelectedItem(pXForm.getColorType());
        data.xFormTargetColorBtn.setBackground(pXForm.getTargetColor().getColor());

        frameControlsUtil.updateControl(pXForm, null, data.transformationWeightREd, "weight", 1.0);
        getWeightMapControlsUpdater(pXForm).updateControls(pXForm);
      } else {
        frameControlsUtil.updateControl(null, null, data.affineC00REd, "", 1.0);
        frameControlsUtil.updateControl(null, null, data.affineC01REd, "", 1.0);
        frameControlsUtil.updateControl(null, null, data.affineC10REd, "", 1.0);
        frameControlsUtil.updateControl(null, null, data.affineC11REd, "", 1.0);
        frameControlsUtil.updateControl(null, null, data.affineC20REd, "", 1.0);
        frameControlsUtil.updateControl(null, null, data.affineC21REd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormColorSlider, data.xFormColorREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormSymmetrySlider, data.xFormSymmetryREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormMaterialSlider, data.xFormMaterialREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormMaterialSpeedSlider, data.xFormMaterialSpeedREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormModGammaSlider, data.xFormModGammaREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormModGammaSpeedSlider, data.xFormModGammaSpeedREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormModContrastSlider, data.xFormModContrastREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormModContrastSpeedSlider, data.xFormModContrastSpeedREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormModSaturationSlider, data.xFormModSaturationREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormModSaturationSpeedSlider, data.xFormModSaturationSpeedREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormModHueSlider, data.xFormModHueREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormModHueSpeedSlider, data.xFormModHueSpeedREd, "", 1.0);
        frameControlsUtil.updateControl(null, data.xFormOpacitySlider, data.xFormOpacityREd, "", 1.0);
        frameControlsUtil.updateControl(null, null, data.transformationWeightREd, "", 1.0);
        data.xFormDrawModeCmb.setSelectedIndex(-1);
        data.xFormColorTypeCmb.setSelectedIndex(-1);
        data.xFormTargetColorBtn.setBackground(Color.BLACK);
        getWeightMapControlsUpdater(null).clearComponents();
      }

      nonlinearControls.refreshParamControls(pXForm);


      refreshRelWeightsTable();
    } finally {
      gridRefreshing = oldGridRefreshing;
      refreshing = oldRefreshing;
      cmbRefreshing = oldCmbRefreshing;
      noRefresh = oldNoRefresh;
    }
  }


  public void addLinkedXForm() {
    int row = data.transformationsTable.getSelectedRow();
    if (row < 0 || row >= getCurrLayer().getXForms().size()) {
      return;
    }
    XForm xForm = new XForm();
    xForm.addVariation(1.0, new Linear3DFunc());
    xForm.setWeight(0.5);
    xForm.setColorType(ColorType.NONE);
    xForm.setColorSymmetry(1.0);
    saveUndoPoint();
    getCurrLayer().getXForms().add(xForm);
    int fromId = row;
    int toId = getCurrLayer().getXForms().size() - 1;
    for (int i = 0; i < getCurrLayer().getXForms().size(); i++) {
      xForm = getCurrLayer().getXForms().get(i);
      if (i == fromId) {
        XForm toXForm = getCurrLayer().getXForms().get(toId);
        for (int j = 0; j < getCurrLayer().getXForms().size(); j++) {
          toXForm.getModifiedWeights()[j] = xForm.getModifiedWeights()[j];
          xForm.getModifiedWeights()[j] = (j == toId) ? 1 : 0;
        }
        xForm.setDrawMode(DrawMode.HIDDEN);
      } else {
        xForm.getModifiedWeights()[toId] = 0;
      }
    }
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    } finally {
      gridRefreshing = false;
    }
    row = getCurrLayer().getXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(true, false, 1, true, false);
  }

  private XForm createNewXForm() {
    XForm xForm = new XForm();
    xForm.addVariation(1.0, new Linear3DFunc());
    xForm.setWeight(0.5);
    xForm.setColorType(ColorType.DIFFUSION);
    return xForm;
  }

  public void addXForm() {
    XForm xForm = createNewXForm();
    saveUndoPoint();
    getCurrLayer().getXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    } finally {
      gridRefreshing = false;
    }
    int row = getCurrLayer().getXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(true, false, 1, true, false);
  }

  public void duplicateXForm() {
    int fromId = data.transformationsTable.getSelectedRow();
    if (fromId < 0 || fromId >= getCurrLayer().getXForms().size() + getCurrLayer().getFinalXForms().size())
      return;
    boolean isFinal = (fromId >= getCurrLayer().getXForms().size());
    XForm xForm = new XForm();
    xForm.assign(getCurrXForm());
    saveUndoPoint();
    if (isFinal) {
      getCurrLayer().getFinalXForms().add(xForm);
    } else {
      getCurrLayer().getXForms().add(xForm);
      if (fromId >= 0 && fromId < getCurrLayer().getXForms().size()) {
        // copy xaos from values
        int toId = getCurrLayer().getXForms().size() - 1;
        for (int i = 0; i < toId; i++) {
          XForm xFormi = getCurrLayer().getXForms().get(i);
          xFormi.getModifiedWeights()[toId] = xFormi.getModifiedWeights()[fromId];
          if (i == fromId) {
            xForm.getModifiedWeights()[toId] = xFormi.getModifiedWeights()[fromId];
          }
        }
      }
    }
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    } finally {
      gridRefreshing = false;
    }
    int row = getCurrLayer().getXForms().size() + (isFinal ? getCurrLayer().getFinalXForms().size() : 0) - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(true, false, 1, true, false);
  }

  public void deleteXForm() {
    int row = data.transformationsTable.getSelectedRow();
    saveUndoPoint();
    if (row >= getCurrLayer().getXForms().size()) {
      getCurrLayer().getFinalXForms().remove(row - getCurrLayer().getXForms().size());
    } else {
      getCurrLayer().getXForms().remove(getCurrXForm());
      // adjust xaos
      for (int i = 0; i < getCurrLayer().getXForms().size(); i++) {
        XForm xFormi = getCurrLayer().getXForms().get(i);
        for (int j = row; j < getCurrLayer().getXForms().size(); j++) {
          xFormi.getModifiedWeights()[j] = xFormi.getModifiedWeights()[j + 1];
        }
        xFormi.getModifiedWeights()[getCurrLayer().getXForms().size()] = 1;
      }
    }
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    } finally {
      gridRefreshing = false;
    }
    refreshFlameImage(true, false, 1, true, false);
  }

  public void addFinalXForm() {
    XForm xForm = new XForm();
    xForm.addVariation(1.0, new Linear3DFunc());
    xForm.setColorSymmetry(1.0);
    xForm.setColorType(ColorType.NONE);
    saveUndoPoint();
    getCurrLayer().getFinalXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    } finally {
      gridRefreshing = false;
    }
    int row = getCurrLayer().getXForms().size() + getCurrLayer().getFinalXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(true, false, 1, true, false);
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
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.globalTranslate(workXForm, amount, 0, isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
    }
  }

  public void xForm_rotateRight() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineRotateAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.rotate(workXForm, -amount, isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
    }
  }

  public void xForm_moveLeft(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveHorizAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.globalTranslate(workXForm, -amount, 0, isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
    }
  }

  public void xForm_flipHorizontal() {
    forceTriangleMode();
    saveUndoPoint();
    boolean isPost = data.affineEditPostTransformButton.isSelected();
    applyCurrentXFormToWorkXForm(isPost);
    XFormTransformService.flipHorizontal(workXForm, isPost);
    applyWorkXFormToCurrentXForm(isPost);
    transformationChanged(true);
  }

  public void xForm_flipVertical() {
    forceTriangleMode();
    saveUndoPoint();
    boolean isPost = data.affineEditPostTransformButton.isSelected();
    applyCurrentXFormToWorkXForm(isPost);
    XFormTransformService.flipVertical(workXForm, isPost);
    applyWorkXFormToCurrentXForm(isPost);
    transformationChanged(true);
  }

  public void xForm_enlarge() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineScaleAmountREd.getText()) / 100.0;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.scale(workXForm, amount, data.affineScaleXButton.isSelected(), data.affineScaleYButton.isSelected(), isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
    }
  }

  public void xForm_shrink() {
    forceTriangleMode();
    double amount = 100.0 / Tools.stringToDouble(data.affineScaleAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.scale(workXForm, amount, data.affineScaleXButton.isSelected(), data.affineScaleYButton.isSelected(), isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
    }
  }

  private XForm workXForm = new XForm();

  private void applyCurrentXFormToWorkXForm(boolean usePost) {
    workXForm.setOwner(getCurrLayer());
    XForm selectedXForm = getCurrXForm();
    workXForm.setCoeff00(frameControlsUtil.getAffineProperty(selectedXForm, "00", usePost));
    workXForm.setCoeff01(frameControlsUtil.getAffineProperty(selectedXForm, "01", usePost));
    workXForm.setCoeff10(frameControlsUtil.getAffineProperty(selectedXForm, "10", usePost));
    workXForm.setCoeff11(frameControlsUtil.getAffineProperty(selectedXForm, "11", usePost));
    workXForm.setCoeff20(frameControlsUtil.getAffineProperty(selectedXForm, "20", usePost));
    workXForm.setCoeff21(frameControlsUtil.getAffineProperty(selectedXForm, "21", usePost));
  }

  private void applyWorkXFormToCurrentXForm(boolean usePost) {
    XForm selectedXForm = getCurrXForm();
    frameControlsUtil.setAffineProperty(selectedXForm, "00", usePost, workXForm.getCoeff00());
    frameControlsUtil.setAffineProperty(selectedXForm, "01", usePost, workXForm.getCoeff01());
    frameControlsUtil.setAffineProperty(selectedXForm, "10", usePost, workXForm.getCoeff10());
    frameControlsUtil.setAffineProperty(selectedXForm, "11", usePost, workXForm.getCoeff11());
    frameControlsUtil.setAffineProperty(selectedXForm, "20", usePost, workXForm.getCoeff20());
    frameControlsUtil.setAffineProperty(selectedXForm, "21", usePost, workXForm.getCoeff21());
  }

  public void xForm_rotateLeft() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineRotateAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.rotate(workXForm, amount, isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
    }
  }

  public void xForm_moveUp(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveVertAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.globalTranslate(workXForm, 0, -amount, isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
    }
  }

  public void gradientMarker_move(int pIdx, int pDeltaPos) {
    getFlamePanel().gradientMarker_move(pIdx, pDeltaPos);
    transformationChanged(true);
  }

  public void gradientMarker_selectColor(int pIdx) {
    if (getFlamePanel().gradientMarker_selectColor(pIdx)) {
      transformationChanged(true);
      refreshPaletteImg();
    }
  }

  public void xForm_moveDown(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveVertAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.globalTranslate(workXForm, 0, amount, isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
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
      JCheckBox checkbox = addSelectCheckbox(imgPanel, idx);
      randomBatch.get(i).setSelectCheckbox(checkbox);
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
    data.randomBatchScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    data.randomBatchPanel.add(data.randomBatchScrollPane, BorderLayout.CENTER);
    data.randomBatchPanel.invalidate();
    data.randomBatchPanel.validate();
  }

  public int getScrollThumbnailsPosition() {
    JScrollBar vertical = data.randomBatchScrollPane.getVerticalScrollBar();
    return vertical.getValue();
  }

  public void scrollThumbnailsToPosition(int scrollPos) {
    JScrollBar vertical = data.randomBatchScrollPane.getVerticalScrollBar();
    vertical.setValue(scrollPos);
  }

  public void scrollThumbnailsToBottom() {
    JScrollBar vertical = data.randomBatchScrollPane.getVerticalScrollBar();
    vertical.setValue(vertical.getMaximum());
  }

  public void scrollThumbnailsToTop() {
    JScrollBar vertical = data.randomBatchScrollPane.getVerticalScrollBar();
    vertical.setValue(vertical.getMinimum());
  }

  private CreateRandomBatchThread createRandomBatchThread;
  private boolean loadFirstRandomFlame;

  public void createRandomBatch(int pCount, RandomFlameGenerator randGen, RandomSymmetryGenerator randSymmGen, RandomGradientGenerator randGradientGen, RandomWeightingFieldGenerator randomWeightingFieldGen, RandomBatchQuality pQuality) {
    if (createRandomBatchThread != null) {
      stopRandomBatchThread();
      return;
    }
    stopPreviewRendering();
    if (prefs.getTinaRandomBatchRefreshType() == RandomBatchRefreshType.CLEAR) {
      randomBatch.clear();
      updateThumbnails();
    }
    int imgCount = prefs.getTinaRandomBatchSize();
    List<SimpleImage> imgList = new ArrayList<SimpleImage>();
    int maxCount = (pCount > 0 ? pCount : imgCount);
    loadFirstRandomFlame = true;
    createRandomBatchThread = new CreateRandomBatchThread(this, randomBatchProgressUpdater, maxCount, imgList, randomBatch, randGen, randSymmGen, randGradientGen, randomWeightingFieldGen, pQuality);
    refreshRandomBatchButton();
    new Thread(createRandomBatchThread).start();
  }

  private void refreshRandomBatchButton() {
    data.randomBatchButton.setText(createRandomBatchThread != null ? "Cancel" : "Random batch");
    data.randomBatchButton.invalidate();
    data.randomBatchButton.validate();
  }

  private void stopRandomBatchThread() {
    if (createRandomBatchThread != null) {
      try {
        createRandomBatchThread.signalCancel();
        while (createRandomBatchThread != null && !createRandomBatchThread.isDone()) {
          Thread.sleep(1);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      createRandomBatchThread = null;
    }
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
    btn.setComponentPopupMenu(data.thumbnailRemovePopupMenu);

    pImgPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, BORDER, BORDER));
    pImgPanel.add(btn);
    pImgPanel.invalidate();
  }

  private JCheckBox addSelectCheckbox(ImagePanel pImgPanel, final int pIdx) {
    final int BTN_WIDTH = 20;
    final int BTN_HEIGHT = 20;
    final int BORDER = 0;
    final JCheckBox checkbox = new JCheckBox();
    checkbox.setSelected(true);
    checkbox.setMinimumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    checkbox.setMaximumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    checkbox.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    checkbox.setComponentPopupMenu(data.thumbnailSelectPopupMenu);

    pImgPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, BORDER, BORDER));
    pImgPanel.add(checkbox);
    pImgPanel.invalidate();
    return checkbox;
  }

  protected void removeThumbnail(int pIdx) {
    if (StandardDialogs.confirm(flamePanel, "Do you really want to remove this flame\n from the thumbnail-ribbon?\n (Please note that this cannot be undone)")) {
      randomBatch.remove(pIdx);
      updateThumbnails();
    }
  }

  public void importFromRandomBatch(int pIdx) {
    if (pIdx >= 0 && pIdx < randomBatch.size()) {
      loadFirstRandomFlame = false;
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
        refreshFlameImage(true, false, 1, true, false);
        xFormControls.enableControls(xForm);
      }
    }
  }

  public void xFormColorTypeCmb_changed() {
    if (!cmbRefreshing) {
      XForm xForm = getCurrXForm();
      if (xForm != null && data.xFormColorTypeCmb.getSelectedItem() != null) {
        xForm.setColorType((ColorType) data.xFormColorTypeCmb.getSelectedItem());
        refreshFlameImage(true, false, 1, true, false);
        xFormControls.enableControls(xForm);
      }
    }
  }

  public void xFormTargetColorBtn_clicked() {
    if (!cmbRefreshing) {
      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");
      XForm xForm = getCurrXForm();
      Color selectedColor = JColorChooser.showDialog(rootPanel, title, xForm.getTargetColor().getColor());
      if (selectedColor != null) {
        xForm.setTargetColor(selectedColor);
        refreshTargetColorBtn();
        refreshFlameImage(true, false, 1, true, false);
        xFormControls.enableControls(xForm);
      }
    }
  }

  public void refreshTargetColorBtn() {
    XForm xForm = getCurrXForm();
    data.xFormTargetColorBtn.setBackground(xForm.getTargetColor().getColor());
  }

  public void xFormColorREd_changed() {
    xFormTextFieldChanged(data.xFormColorSlider, data.xFormColorREd, "color", SLIDER_SCALE_COLOR);
  }

  public void xFormColorSlider_changed() {
    xFormSliderChanged(data.xFormColorSlider, data.xFormColorREd, "color", SLIDER_SCALE_COLOR);
  }

  public void xFormSymmetryREd_changed() {
    xFormTextFieldChanged(data.xFormSymmetrySlider, data.xFormSymmetryREd, "colorSymmetry", SLIDER_SCALE_COLOR);
  }

  public void xFormSymmetrySlider_changed() {
    xFormSliderChanged(data.xFormSymmetrySlider, data.xFormSymmetryREd, "colorSymmetry", SLIDER_SCALE_COLOR);
  }

  public void xFormMaterialREd_changed() {
    xFormTextFieldChanged(data.xFormMaterialSlider, data.xFormMaterialREd, "material", SLIDER_SCALE_COLOR);
  }

  public void xFormMaterialSlider_changed() {
    xFormSliderChanged(data.xFormMaterialSlider, data.xFormMaterialREd, "material", SLIDER_SCALE_COLOR);
  }

  public void xFormMaterialSpeedREd_changed() {
    xFormTextFieldChanged(data.xFormMaterialSpeedSlider, data.xFormMaterialSpeedREd, "materialSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormMaterialSpeedSlider_changed() {
    xFormSliderChanged(data.xFormMaterialSpeedSlider, data.xFormMaterialSpeedREd, "materialSpeed", SLIDER_SCALE_COLOR);
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

  public void xFormModHueREd_changed() {
    xFormTextFieldChanged(data.xFormModHueSlider, data.xFormModHueREd, "modHue", SLIDER_SCALE_COLOR);
  }

  public void xFormModHueSlider_changed() {
    xFormSliderChanged(data.xFormModHueSlider, data.xFormModHueREd, "modHue", SLIDER_SCALE_COLOR);
  }

  public void xFormModHueSpeedREd_changed() {
    xFormTextFieldChanged(data.xFormModHueSpeedSlider, data.xFormModHueSpeedREd, "modHueSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModHueSpeedSlider_changed() {
    xFormSliderChanged(data.xFormModHueSpeedSlider, data.xFormModHueSpeedREd, "modHueSpeed", SLIDER_SCALE_COLOR);
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
      } else {
        layer.getXForms().get(xaosRow).getModifiedWeights()[transformRow] = pValue;
      }
      gridRefreshing = true;
      try {
        refreshRelWeightsTable();
        data.relWeightsTable.getSelectionModel().setSelectionInterval(xaosRow, xaosRow);
        refreshFlameImage(true, false, 1, true, false);
      } finally {
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
      xFormTextFieldChanged(null, data.transformationWeightREd, "weight", 1.0);
      gridRefreshing = true;
      try {
        int row = data.transformationsTable.getSelectedRow();
        refreshTransformationsTable();
        data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
      } finally {
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
    RandomGradientGenerator gradientGen = RandomGradientGeneratorList.getRandomGradientGeneratorInstance((String) data.paletteRandomGeneratorCmb.getSelectedItem());
    RGBPalette palette = gradientGen.generatePalette(Integer.parseInt(data.paletteRandomPointsREd.getText()), data.paletteFadeColorsCBx.isSelected(), data.paletteUniformWidthCBx.isSelected());
    flame.getFirstLayer().setPalette(palette);
    setLastGradient(palette);
    setCurrFlame(flame);
    undoManager.initUndoStack(getCurrFlame());
  }

  public void renderModeCmb_changed() {
    if (!refreshing && !cmbRefreshing) {
      refreshFlameImage(true, false, 1, true, false);
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
          refreshFlameImage(true, true, 1, true, false);
          refreshPaletteImg();
        } else {
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
        refreshFlameImage(true, true, 1, true, false);
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
      refreshFlameImage(true, false, 1, false, false);
    } finally {
      gridRefreshing = lastGridRefreshing;
    }
  }

  private void flamePanel_mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      flamePanel_doubleClicked();
    } else if (e.getClickCount() == 1) {
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
              refreshFlameImage(true, false, 1, true, false);
              refreshPaletteImg();
            } else {
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

  private void flamePanel_doubleClicked() {
    switch (prefs.getTinaEditorDoubleClickAction()) {
      case ACTIVATE_TRIANGLE_EDIT:
        if (!data.mouseTransformMoveTrianglesButton.isSelected()) {
          data.mouseTransformMoveTrianglesButton.setSelected(true);
          mouseTransformMoveTrianglesButton_clicked();
        }
        break;
      case SWITCH_TRIANGLE_CAM_EDIT:
        if (data.mouseTransformMoveTrianglesButton.isSelected()) {
          data.mouseTransformEditViewButton.setSelected(true);
          mouseTransformViewButton_clicked();
        } else {
          data.mouseTransformMoveTrianglesButton.setSelected(true);
          mouseTransformMoveTrianglesButton_clicked();
        }
        break;
      case RENDER_FLAME:
        renderFlameButton_actionPerformed(null);
        break;
      case NONE:
        break;
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
          refreshFlameImage(true, false, 1, false, false);
        }
      } finally {
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
          refreshFlameImage(true, false, 1, true, false);
        }
      } finally {
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
      refreshFlameImage(true, false, 1, false, false);
      data.affineEditPostTransformSmallButton.setSelected(data.affineEditPostTransformButton.isSelected());
    } finally {
      refreshing = false;
    }
  }

  public void affineEditPostTransformSmallButton_clicked() {
    refreshing = true;
    try {
      data.affineEditPostTransformButton.setSelected(data.affineEditPostTransformSmallButton.isSelected());
    } finally {
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
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC21REd, frameControlsUtil.getAffinePropertyName(xForm, "21", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC20REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC20REd, frameControlsUtil.getAffinePropertyName(xForm, "20", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineResetTransformButton_clicked() {
    if (getCurrXForm() != null) {
      saveUndoPoint();
      boolean isPost = data.affineEditPostTransformButton.isSelected();
      applyCurrentXFormToWorkXForm(isPost);
      XFormTransformService.reset(workXForm, isPost);
      applyWorkXFormToCurrentXForm(isPost);
      transformationChanged(true);
    }
  }

  public void newDOFCBx_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.setNewCamDOF(data.newDOFCBx.isSelected());
      flameControls.enableDOFUI();
      refreshFlameImage(true, false, 1, true, false);
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
    } finally {
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
      refreshFlameImage(true, false, 1, true, false);
      data.resolutionProfileCmb.requestFocus();
    } finally {
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
            randomBatch.add(0, new FlameThumbnail(flames.get(i), null, null));
          }
          setupProfiles(getCurrFlame());
          updateThumbnails();
          refreshUI();
          messageHelper.showStatusMessage(getCurrFlame(), "opened from clipboard");
        }
      }
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(Flame pFlame, boolean pAddToThumbnails) {
    if (data.layerAppendBtn.isSelected() && getCurrFlame() != null) {
      if (appendToFlame(pFlame.makeCopy())) {
        messageHelper.showStatusMessage(pFlame, "added as new layers");
        setLastGradient(getCurrLayer().getPalette());
      }
    } else {
      if (_currFlame == null || !_currFlame.equals(pFlame)) {
        _currRandomizeFlame = pFlame.makeCopy();
      }
      if (pAddToThumbnails) {
        _currFlame = pFlame.makeCopy();
        undoManager.initUndoStack(_currFlame);
        setupProfiles(getCurrFlame());
        randomBatch.add(0, new FlameThumbnail(getCurrFlame(), null, null));
        updateThumbnails();
        messageHelper.showStatusMessage(getCurrFlame(), "imported into editor");
      } else {
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
    } else {
      return null;
    }
  }

  public void saveFlameToClipboard() {
    try {
      if (getCurrFlame() != null) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String xml = new FlameWriter().getFlameXML(generateExportFlame(getCurrFlame()));
        StringSelection data = new StringSelection(xml);
        clipboard.setContents(data, data);
        messageHelper.showStatusMessage(getCurrFlame(), "flame saved to clipboard");
      }
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private Flame generateExportFlame(Flame pFlame) {
    return generateExportFlame(pFlame, null);
  }

  private Flame generateExportFlame(Flame pFlame, Layer pLayer) {
    Flame res = pFlame.makeCopy(pLayer);
    ResolutionProfile resProfile = getResolutionProfile();

    double wScl = (double) resProfile.getWidth() / (double) res.getWidth();
    double hScl = (double) resProfile.getHeight() / (double) res.getHeight();
    res.setPixelsPerUnit((wScl + hScl) * 0.5 * res.getPixelsPerUnit());
    res.setWidth(resProfile.getWidth());
    res.setHeight(resProfile.getHeight());
    return res;
  }

  public void mouseTransformSlowButton_clicked() {
    if (flamePanel != null) {
      flamePanel.setFineMovement(data.mouseTransformSlowButton.isSelected());
      EditingPrecision.setCurrent(data.mouseTransformSlowButton.isSelected() ? EditingPrecision.FINE : EditingPrecision.NORMAL);
    }
  }

  @Override
  public ScriptRunner compileScript() throws Exception {
    return compileScript(data.scriptTextArea.getText());
  }

  public ScriptRunner compileScript(String pScript) throws Exception {
    return ScriptRunner.compile(pScript);
  }

  @Override
  public void runScript() throws Exception {
    System.out.println("WARNING: called TinaController.runScript()");
    // jwfScriptController.scriptRunBtn_clicked();
    ScriptRunner script = compileScript();
    saveUndoPoint();
    runJWFScript(script);
  }

  public void compileScriptButton_clicked() {
    try {
      compileScript();
    } catch (Throwable ex) {
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
      transformationChanged(true);
    }
  }

  public void randomizeColorsBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      getCurrLayer().randomizeColors();
      transformationChanged(true);
    }
  }

  public void resetColorsBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      getCurrLayer().resetColors();
      transformationChanged(true);
    }
  }

  public void randomizeColorSpeedBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      getCurrLayer().randomizeColorSpeed();
      transformationChanged(true);
    }
  }

  public void randomizeColorShiftBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      data.paletteShiftREd.setText(String.valueOf(255 - Tools.randomInt(511)));
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
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC01REd, frameControlsUtil.getAffinePropertyName(xForm, "01", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC11REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC11REd, frameControlsUtil.getAffinePropertyName(xForm, "11", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC00REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC00REd, frameControlsUtil.getAffinePropertyName(xForm, "00", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC10REd_changed() {
    if (gridRefreshing || cmbRefreshing) {
      return;
    }
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC10REd, frameControlsUtil.getAffinePropertyName(xForm, "10", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void toggleVariationsButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setDrawVariations(data.toggleVariationsButton.isSelected());
      refreshFlameImage(true, false, 1, false, false);
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
      refreshFlameImage(true, false, 1, true, false);
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
        prefs.saveToFile();

        refreshQualityProfileCmb(data.qualityProfileCmb, profile);
        refreshQualityProfileCmb(data.batchQualityProfileCmb, profile);
        refreshQualityProfileCmb(data.gpuQualityProfileCmb, profile);
        refreshQualityProfileCmb(data.swfAnimatorQualityProfileCmb, profile);
        qualityProfileCmb_changed();
      } catch (Throwable ex) {
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
        prefs.saveToFile();
        refreshResolutionProfileCmb(data.resolutionProfileCmb, profile);
        refreshResolutionProfileCmb(data.interactiveResolutionProfileCmb, profile);
        refreshResolutionProfileCmb(data.swfAnimatorResolutionProfileCmb, profile);
        refreshResolutionProfileCmb(data.batchResolutionProfileCmb, profile);
        refreshResolutionProfileCmb(data.gpuResolutionProfileCmb, profile);
        resolutionProfileCmb_changed();
      } catch (Throwable ex) {
        errorHandler.handleError(ex);

      }
    }
  }

  public void appendToMovieButton_actionPerformed(ActionEvent e) {
    desktop.showJFrame(EasyMovieMakerFrame.class);
    if (getCurrFlame() != null) {
      if (getSwfAnimatorCtrl().getFlame() == null) {
        getSwfAnimatorCtrl().importFlameFromEditor(getCurrFlame().makeCopy());
      }
    }
  }

  public JPanel getRootPanel() {
    return rootPanel;
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
          refreshFlameImage(true, false, 1, false, false);
        }
      } finally {
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
          refreshFlameImage(true, false, 1, false, false);
        }
      } finally {
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
      } finally {
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
      } finally {
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
    final String UNDO_LABEL = "Undo (<Strg>+Z)";
    final String REDO_LABEL = "Redo (<Strg>+Y)";
    if (getCurrFlame() != null) {
      int stackSize = undoManager.getUndoStackSize(getCurrFlame());
      if (stackSize > 0) {
        int pos = undoManager.getUndoStackPosition(getCurrFlame());
        data.undoButton.setEnabled(pos > 0 && pos < stackSize);
        if (undoDebug) {
          data.undoButton.setText("U " + pos);
        } else {
          data.undoButton.setToolTipText(UNDO_LABEL + " " + pos + "/" + stackSize);
        }
        data.redoButton.setEnabled(pos >= 0 && pos < stackSize - 1);
        if (undoDebug) {
          data.redoButton.setText("R " + stackSize);
        } else {
          data.redoButton.setToolTipText(REDO_LABEL + " " + pos + "/" + stackSize);
        }
      } else {
        data.undoButton.setEnabled(false);
        if (undoDebug) {
          data.undoButton.setText(UNDO_LABEL);
        } else {
          data.undoButton.setToolTipText(UNDO_LABEL);
        }
        data.redoButton.setEnabled(false);
        if (undoDebug) {
          data.redoButton.setText(REDO_LABEL);
        } else {
          data.redoButton.setToolTipText(REDO_LABEL);
        }
      }
    } else {
      data.undoButton.setEnabled(false);
      data.redoButton.setEnabled(false);
    }
  }

  public void grabPaletteFromImageButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      } catch (Exception ex) {
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
        refreshFlameImage(true, false, 1, true, false);
      } catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void selectImageForGradientButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      } catch (Exception ex) {
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
        refreshFlameImage(true, false, 1, true, false);
      } catch (Throwable ex) {
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
    refreshFlameImage(true, false, 1, true, false);
  }

  public void paletteInvertBtn_clicked() {
    Layer layer = getCurrLayer();
    if (layer != null) {
      saveUndoPoint();
      layer.getPalette().negativeColors();
      refreshPaletteUI(layer.getPalette());
      transformationChanged(true);
    }
  }

  public void paletteReverseBtn_clicked() {
    Layer layer = getCurrLayer();
    if (layer != null) {
      saveUndoPoint();
      layer.getPalette().reverseColors();
      refreshPaletteUI(layer.getPalette());
      transformationChanged(true);
    }
  }

  public void snapshotButton_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      Flame storedFlame = generateExportFlame(flame);
      undoManager.initUndoStack(storedFlame);
      randomBatch.add(0, new FlameThumbnail(storedFlame, null, null));
      updateThumbnails();
    }
  }

  public void extractLayerBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      Flame storedFlame = generateExportFlame(flame, getCurrLayer());
      undoManager.initUndoStack(storedFlame);
      randomBatch.add(0, new FlameThumbnail(storedFlame, null, null));
      updateThumbnails();
    }
  }

  public void quicksaveButton_clicked() {
    try {
      if (getCurrFlame() != null) {
        String filename = qsaveFilenameGen.generateNextFilename();
        new FlameWriter().writeFlame(generateExportFlame(getCurrFlame()), filename);
        messageHelper.showStatusMessage(getCurrFlame(), "quicksave <" + new File(filename).getName() + "> saved");
      }
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void toggleTransparencyButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setShowTransparency(data.toggleTransparencyButton.isSelected());
      refreshFlameImage(true, false, 1, true, false);
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
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      mainRenderThread = null;
      enableMainRenderControls();
    } else if (getCurrFlame() != null) {
      try {
        String dfltFileExt = Stereo3dMode.SIDE_BY_SIDE.equals(getCurrFlame().getStereo3dMode()) ? Tools.FILEEXT_PNS : Tools.FILEEXT_PNG;
        JFileChooser chooser = new RenderOutputFileChooser(dfltFileExt);
        if (prefs.getOutputImagePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
          } catch (Exception ex) {
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
                if (Tools.isImageFile(file.getAbsolutePath())) {
                  mainController.loadImage(file.getAbsolutePath(), false);
                  File zBuffer = new File(Tools.makeZBufferFilename(file.getAbsolutePath(), flame.getZBufferFilename()));
                  if (zBuffer.exists()) {
                    mainController.loadImage(zBuffer.getAbsolutePath(), false);
                  }
                } else if (Tools.isMovieFile(file.getAbsolutePath())) {
                  mainController.loadMovie(file.getAbsolutePath());
                }
              } catch (Throwable ex) {
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
          Thread worker = new Thread(mainRenderThread);
          worker.setPriority(Thread.MIN_PRIORITY);
          worker.start();
        }
      } catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void spatialFilterTypeCmb_changed() {
    if (!noRefresh) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        boolean oldNoRefresh = noRefresh;
        try {
          noRefresh = true;
          FilteringType filteringType = (FilteringType) data.tinaFilterTypeCmb.getSelectedItem();
          if (filteringType == null) {
            filteringType = FilteringType.GLOBAL_SMOOTHING;
          }

          flame.setSpatialFilteringType(filteringType);

          getFlameControls().fillFilterKernelCmb(filteringType);

          setDefaultFilterKernel(filteringType);
          noRefresh = false;
          spatialFilterKernelCmb_changed();
          flameControls.enableFilterUI();
        } finally {
          noRefresh = oldNoRefresh;
        }
      }
    }
  }

  private void setDefaultFilterKernel(FilteringType filteringType) {
    switch (filteringType) {
      case GLOBAL_SHARPENING:
        data.filterKernelCmb.setSelectedItem(FilterKernelType.MITCHELL);
        break;
      case GLOBAL_SMOOTHING:
        data.filterKernelCmb.setSelectedItem(FilterKernelType.SINEPOW10);
        break;
      default:
        data.filterKernelCmb.setSelectedIndex(0);
        break;
    }
  }

  public void spatialFilterKernelCmb_changed() {
    if (!noRefresh) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        flame.setSpatialFilterKernel((FilterKernelType) data.filterKernelCmb.getSelectedItem());
        if (getFlameControls() != getFlameControls()) {
          getFlameControls().enableFilterUI();
        }
        refreshFlameImage(true, false, 1, true, false);
        refreshFilterKernelPreviewImg();
      }
    }
  }

  public void backgroundColorTypeCmb_changed() {
    if (!noRefresh) {
      Flame flame = getCurrFlame();
      if (flame != null && !flame.getBgColorType().equals(data.backgroundColorTypeCmb.getSelectedItem())) {
        saveUndoPoint();
        flame.setBgColorType(((BGColorType) data.backgroundColorTypeCmb.getSelectedItem()));
        getFlameControls().enableBGColorUI();
        refreshBGColorIndicators();
        refreshFlameImage(true, false, 1, true, true);
      }
    }
  }

  public void backgroundColorTypeCmb_reset() {
    if (getCurrFlame() != null) {
      Flame newFlame = new Flame();
      getCurrFlame().setBgColorType(newFlame.getBgColorType());
      getCurrFlame().setBgColorRed(newFlame.getBgColorRed());
      getCurrFlame().setBgColorGreen(newFlame.getBgColorGreen());
      getCurrFlame().setBgColorBlue(newFlame.getBgColorBlue());
      getCurrFlame().setBgColorCCRed(newFlame.getBgColorCCRed());
      getCurrFlame().setBgColorCCGreen(newFlame.getBgColorCCGreen());
      getCurrFlame().setBgColorCCBlue(newFlame.getBgColorCCBlue());
      getCurrFlame().setBgColorLLRed(newFlame.getBgColorLLRed());
      getCurrFlame().setBgColorLLGreen(newFlame.getBgColorLLGreen());
      getCurrFlame().setBgColorLLBlue(newFlame.getBgColorLLBlue());
      getCurrFlame().setBgColorLRRed(newFlame.getBgColorLRRed());
      getCurrFlame().setBgColorLRGreen(newFlame.getBgColorLRGreen());
      getCurrFlame().setBgColorLRBlue(newFlame.getBgColorLRBlue());
      getCurrFlame().setBgColorURRed(newFlame.getBgColorURRed());
      getCurrFlame().setBgColorURGreen(newFlame.getBgColorURGreen());
      getCurrFlame().setBgColorURBlue(newFlame.getBgColorURBlue());
      getCurrFlame().setBgColorULRed(newFlame.getBgColorULRed());
      getCurrFlame().setBgColorULGreen(newFlame.getBgColorULGreen());
      getCurrFlame().setBgColorULBlue(newFlame.getBgColorULBlue());
      data.backgroundColorTypeCmb.setSelectedItem(getCurrFlame().getBgColorType());
      getFlameControls().enableBGColorUI();
      refreshBGColorIndicators();
      refreshFlameImage(true, false, 1, true, true);
    }
  }

  public void quickMutateButton_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      desktop.showJFrame(MutaGenFrame.class);
      rootPanel.getParent().invalidate();
      try {
        Graphics g = rootPanel.getParent().getGraphics();
        if (g != null) {
          rootPanel.getParent().paint(g);
        }
      } catch (Throwable ex) {
        ex.printStackTrace();
      }
      mutaGenController.importFlame(flame);
    }
  }

  public void sendFlameToIRButton_clicked() {
    //    Flame flame = getCurrFlame();
    //    if (flame != null) {
    //      if (!interactiveRendererCtrl.isRendering() || StandardDialogs.confirm(flamePanel, "The Interactive Renderer is already rendering. Do you really want to abort the current render?")) {
    //        interactiveRendererCtrl.importFlame(flame);
    desktop.showJFrame(InteractiveRendererFrame.class);
    //      }
    //    }
  }

  public void openFlameBrowserButton_clicked() {
    desktop.showJFrame(FlameBrowserFrame.class);
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
      transformationChanged(true);
    }
  }

  public void editFlameTitleBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      String s = StandardDialogs.promptForText(rootPanel, "Please enter the new title:", flame.getName());
      if (s != null) {
        flame.setName(s);
        messageHelper.showStatusMessage(flame, "Title changed");
      }
    }
  }

  public void editTransformCaptionBtn_clicked() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      String s = StandardDialogs.promptForText(rootPanel, "Please enter a new name:", xForm.getName());
      if (s != null) {
        xForm.setName(s);
        int row = data.transformationsTable.getSelectedRow();
        gridRefreshing = true;
        try {
          refreshTransformationsTable();
          data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
        } finally {
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
          refreshFlameImage(true, false, 1, false, false);
        }
      } finally {
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
          refreshFlameImage(true, false, 1, false, false);
        }
      } finally {
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

  public LeapMotionMainEditorController getLeapMotionMainEditorController() {
    return leapMotionMainEditorController;
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
          refreshFlameImage(true, false, 1, true, false);
        }
      } finally {
        refreshing = false;
      }
    }
  }

  public void gradientFadeBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientFade();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void gradientInvertBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientInvert();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void gradientReverseBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientReverse();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void gradientSortBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientSort();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void gradientSelectAllBtn_clicked() {
    getFlamePanel().gradientSelectAll();
    refreshFlameImage(true, false, 1, true, false);
  }

  public void gradientApplyBalancingBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().applyBalancing();
    refreshUI();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void gradientApplyTXBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().applyTX();
    refreshUI();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  private final static GradientSelectionProvider dfltGradientSelection = new DefaultGradientSelectionProvider();

  @Override
  public int getFrom() {
    if (getFlamePanel().getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
      return getFlamePanel().getGradientFrom();
    } else {
      return dfltGradientSelection.getFrom();
    }
  }

  @Override
  public int getTo() {
    if (getFlamePanel().getConfig().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
      return getFlamePanel().getGradientTo();
    } else {
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
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void gradientEraseRangeBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientEraseRange();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void gradientMononchromeBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientMonochrome();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void gradientFadeAllBtn_clicked() {
    undoManager.saveUndoPoint(getCurrFlame());
    getFlamePanel().gradientFadeAll();
    refreshFlameImage(true, false, 1, true, false);
    refreshPaletteImg();
  }

  public void backgroundColorBtn_clicked() {
    if (getCurrFlame() != null) {
      undoManager.saveUndoPoint(getCurrFlame());
      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(getCurrFlame().getBgColorULRed(), getCurrFlame().getBgColorULGreen(), getCurrFlame().getBgColorULBlue()));
      if (selectedColor != null) {
        getCurrFlame().setBgColorULRed(selectedColor.getRed());
        getCurrFlame().setBgColorULGreen(selectedColor.getGreen());
        getCurrFlame().setBgColorULBlue(selectedColor.getBlue());
        refreshFlameImage(true, false, 1, true, true);
        refreshBGColorULIndicator();
      }
    }

  }

  public void backgroundColorURBtn_clicked() {
    if (getCurrFlame() != null) {
      undoManager.saveUndoPoint(getCurrFlame());

      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(getCurrFlame().getBgColorURRed(), getCurrFlame().getBgColorURGreen(), getCurrFlame().getBgColorURBlue()));
      if (selectedColor != null) {
        getCurrFlame().setBgColorURRed(selectedColor.getRed());
        getCurrFlame().setBgColorURGreen(selectedColor.getGreen());
        getCurrFlame().setBgColorURBlue(selectedColor.getBlue());
        refreshFlameImage(true, false, 1, true, true);
        refreshBGColorURIndicator();
      }
    }
  }

  public void backgroundColorLLBtn_clicked() {
    if (getCurrFlame() != null) {
      undoManager.saveUndoPoint(getCurrFlame());

      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(getCurrFlame().getBgColorLLRed(), getCurrFlame().getBgColorLLGreen(), getCurrFlame().getBgColorLLBlue()));
      if (selectedColor != null) {
        getCurrFlame().setBgColorLLRed(selectedColor.getRed());
        getCurrFlame().setBgColorLLGreen(selectedColor.getGreen());
        getCurrFlame().setBgColorLLBlue(selectedColor.getBlue());
        refreshFlameImage(true, false, 1, true, true);
        refreshBGColorLLIndicator();
      }
    }
  }

  public void backgroundColorLRBtn_clicked() {
    if (getCurrFlame() != null) {
      undoManager.saveUndoPoint(getCurrFlame());

      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(getCurrFlame().getBgColorLRRed(), getCurrFlame().getBgColorLRGreen(), getCurrFlame().getBgColorLRBlue()));
      if (selectedColor != null) {
        getCurrFlame().setBgColorLRRed(selectedColor.getRed());
        getCurrFlame().setBgColorLRGreen(selectedColor.getGreen());
        getCurrFlame().setBgColorLRBlue(selectedColor.getBlue());
        refreshFlameImage(true, false, 1, true, true);
        refreshBGColorLRIndicator();
      }
    }
  }

  public void backgroundColorCCBtn_clicked() {
    if (getCurrFlame() != null) {
      undoManager.saveUndoPoint(getCurrFlame());

      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      if (BGColorType.GRADIENT_2X2_C.equals(getCurrFlame().getBgColorType())) {
        Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(getCurrFlame().getBgColorCCRed(), getCurrFlame().getBgColorCCGreen(), getCurrFlame().getBgColorCCBlue()));
        if (selectedColor != null) {
          getCurrFlame().setBgColorCCRed(selectedColor.getRed());
          getCurrFlame().setBgColorCCGreen(selectedColor.getGreen());
          getCurrFlame().setBgColorCCBlue(selectedColor.getBlue());
          refreshFlameImage(true, false, 1, true, true);
          refreshBGColorCCIndicator();
        }
      } else {
        Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(getCurrFlame().getBgColorRed(), getCurrFlame().getBgColorGreen(), getCurrFlame().getBgColorBlue()));
        if (selectedColor != null) {
          getCurrFlame().setBgColorRed(selectedColor.getRed());
          getCurrFlame().setBgColorGreen(selectedColor.getGreen());
          getCurrFlame().setBgColorBlue(selectedColor.getBlue());
          refreshFlameImage(true, false, 1, true, true);
          refreshBGColorCCIndicator();
        }
      }
    }
  }

  private void refreshBGColorULIndicator() {
    Color color = getCurrFlame() != null ? (BGColorType.GRADIENT_2X2.equals(getCurrFlame().getBgColorType()) || BGColorType.GRADIENT_2X2_C.equals(getCurrFlame().getBgColorType())) ? new Color(getCurrFlame().getBgColorULRed(), getCurrFlame().getBgColorULGreen(), getCurrFlame().getBgColorULBlue())
            : Color.BLACK : Color.BLACK;
    data.backgroundColorIndicatorBtn.setBackground(color);
  }

  private void refreshBGColorURIndicator() {
    Color color = getCurrFlame() != null ? (BGColorType.GRADIENT_2X2.equals(getCurrFlame().getBgColorType()) || BGColorType.GRADIENT_2X2_C.equals(getCurrFlame().getBgColorType())) ? new Color(getCurrFlame().getBgColorURRed(), getCurrFlame().getBgColorURGreen(), getCurrFlame().getBgColorURBlue())
            : Color.BLACK : Color.BLACK;
    data.backgroundColorURIndicatorBtn.setBackground(color);
  }

  private void refreshBGColorLLIndicator() {
    Color color = getCurrFlame() != null ? (BGColorType.GRADIENT_2X2.equals(getCurrFlame().getBgColorType()) || BGColorType.GRADIENT_2X2_C.equals(getCurrFlame().getBgColorType())) ? new Color(getCurrFlame().getBgColorLLRed(), getCurrFlame().getBgColorLLGreen(), getCurrFlame().getBgColorLLBlue())
            : Color.BLACK : Color.BLACK;
    data.backgroundColorLLIndicatorBtn.setBackground(color);
  }

  private void refreshBGColorLRIndicator() {
    Color color = getCurrFlame() != null ? (BGColorType.GRADIENT_2X2.equals(getCurrFlame().getBgColorType()) || BGColorType.GRADIENT_2X2_C.equals(getCurrFlame().getBgColorType())) ? new Color(getCurrFlame().getBgColorLRRed(), getCurrFlame().getBgColorLRGreen(), getCurrFlame().getBgColorLRBlue())
            : Color.BLACK : Color.BLACK;
    data.backgroundColorLRIndicatorBtn.setBackground(color);
  }

  private void refreshBGColorCCIndicator() {
    Color color;
    if (getCurrFlame() != null) {
      if (BGColorType.GRADIENT_2X2_C.equals(getCurrFlame().getBgColorType())) {
        color = new Color(getCurrFlame().getBgColorCCRed(), getCurrFlame().getBgColorCCGreen(), getCurrFlame().getBgColorCCBlue());
      } else if (BGColorType.SINGLE_COLOR.equals(getCurrFlame().getBgColorType())) {
        color = new Color(getCurrFlame().getBgColorRed(), getCurrFlame().getBgColorGreen(), getCurrFlame().getBgColorBlue());
      } else {
        color = Color.BLACK;
      }
    } else {
      color = Color.BLACK;
    }
    data.backgroundColorCCIndicatorBtn.setBackground(color);
  }

  private List<MutationType> createRandomMutationTypes() {
    MutationType allMutationTypes[] = {MutationType.LOCAL_GAMMA, MutationType.ADD_TRANSFORM, MutationType.ADD_VARIATION, MutationType.CHANGE_WEIGHT, MutationType.WEIGHTING_FIELD, MutationType.GRADIENT_POSITION, MutationType.AFFINE, MutationType.RANDOM_GRADIENT, MutationType.RANDOM_PARAMETER, MutationType.SIMILAR_GRADIENT};
    int[] allCounts = {1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 6, 7, 8, 9};
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

      if (!getCurrRandomizeFlame().isRenderable()) {
        _currRandomizeFlame = getCurrFlame().makeCopy();
      }

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
        } else if (coverage > bestCoverage) {
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
        layerControls.enableControls();
        //refreshFlameImage(false);
      } finally {
        cmbRefreshing = oldCmbRefreshing;
        gridRefreshing = oldGridRefreshing;
      }
    }
  }

  public void addLayerBtn_clicked() {
    Flame flame = getCurrFlame();
    Layer layer = new Layer();
    RGBPalette palette = RandomGradientGeneratorList.getRandomGradientGeneratorInstance((String) data.paletteRandomGeneratorCmb.getSelectedItem()).generatePalette(Integer.parseInt(data.paletteRandomPointsREd.getText()), data.paletteFadeColorsCBx.isSelected(), data.paletteUniformWidthCBx.isSelected());
    layer.setPalette(palette);
    setLastGradient(palette);
    saveUndoPoint();
    flame.getLayers().add(layer);

    gridRefreshing = true;
    try {
      refreshLayersTable();
    } finally {
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
    } finally {
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
    } finally {
      gridRefreshing = false;
    }

    layersTableClicked();
  }

  public void layerDensityREd_changed() {
    if (!gridRefreshing && getCurrLayer() != null) {
      saveUndoPoint();
      frameControlsUtil.valueChangedByTextField(getCurrLayer(), null, data.layerDensityREd, "density", 1.0, 0.0);
      int row = data.layersTable.getSelectedRow();
      boolean oldGridRefreshing = gridRefreshing;
      gridRefreshing = true;
      try {
        refreshLayersTable();
        data.layersTable.getSelectionModel().setSelectionInterval(row, row);
      } finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void layerDensityREd_reset() {
    if (!gridRefreshing && getCurrLayer() != null) {
      data.layerDensityREd.setValue(getFrameControlsUtil().getRawPropertyValue(new Flame().getFirstLayer(), "density"));
      saveUndoPoint();
      frameControlsUtil.valueChangedByTextField(getCurrLayer(), null, data.layerDensityREd, "density", 1.0, 0.0);
      int row = data.layersTable.getSelectedRow();
      boolean oldGridRefreshing = gridRefreshing;
      gridRefreshing = true;
      try {
        refreshLayersTable();
        data.layersTable.getSelectionModel().setSelectionInterval(row, row);
      } finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void layerWeightREd_changed() {
    if (!gridRefreshing && getCurrLayer() != null) {
      saveUndoPoint();
      frameControlsUtil.valueChangedByTextField(getCurrLayer(), null, data.layerWeightEd, "weight", 1.0, 0.0);
      int row = data.layersTable.getSelectedRow();
      boolean oldGridRefreshing = gridRefreshing;
      gridRefreshing = true;
      try {
        refreshLayersTable();
        data.layersTable.getSelectionModel().setSelectionInterval(row, row);
      } finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void layerWeightREd_reset() {
    if (!gridRefreshing && getCurrLayer() != null) {
      saveUndoPoint();
      data.layerWeightEd.setValue(getFrameControlsUtil().getRawPropertyValue(new Flame().getFirstLayer(), "weight"));
      frameControlsUtil.valueChangedByTextField(getCurrLayer(), null, data.layerWeightEd, "weight", 1.0, 0.0);
      int row = data.layersTable.getSelectedRow();
      boolean oldGridRefreshing = gridRefreshing;
      gridRefreshing = true;
      try {
        refreshLayersTable();
        data.layersTable.getSelectionModel().setSelectionInterval(row, row);
      } finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(true, false, 1, true, false);
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
      } finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(true, false, 1, true, false);
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
      } finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(true, false, 1, true, false);
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
      } finally {
        gridRefreshing = oldGridRefreshing;
      }
      refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void layerAppendModeBtnClicked() {
    refreshFlameImage(true, false, 1, true, false);
  }

  public void layerPreviewBtnClicked() {
    refreshFlameImage(true, false, 1, true, false);
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
    animationController.registerMotionPropertyControls(data.xFormMaterialREd);
    animationController.registerMotionPropertyControls(data.xFormMaterialSpeedREd);
    animationController.registerMotionPropertyControls(data.xFormOpacityREd);
    animationController.registerMotionPropertyControls(data.layerDensityREd);
    animationController.registerMotionPropertyControls(data.layerWeightEd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldVariationIntensityREd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldJitterIntensityREd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldColorIntensityREd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldVarParam1AmountREd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldVarParam2AmountREd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldVarParam3AmountREd);

    animationController.registerMotionPropertyControls(weightMapData.weightingFieldParam01REd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldParam02REd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldParam03REd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldParam05REd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldParam06REd);
    animationController.registerMotionPropertyControls(weightMapData.weightingFieldParam07REd);

    for (TinaNonlinearControlsRow row : data.TinaNonlinearControlsRows) {
      animationController.registerMotionPropertyControls(row.getNonlinearVarREd());
      animationController.registerMotionPropertyControls(row.getNonlinearParamsREd());
    }

  }

  public FlameControlsDelegate getFlameControls() {
    return flameControls;
  }

  public LayerControlsDelegate getLayerControls() {
    return layerControls;
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
      refreshFlameImage(true, false, 1, false, false);
    }
  }

  public void toggleDrawGridButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setWithGrid(data.toggleDrawGridButton.isSelected());
      refreshFlameImage(true, false, 1, false, false);
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
          refreshFlameImage(true, false, 1, false, false);
        }
      } finally {
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
      refreshTransformationsTable();
      refreshFlameImage(true, false, 1, false, false);
    } finally {
      refreshing = false;
    }
  }

  protected TinaControllerData getData() {
    return data;
  }

  public void xFormModLocalGammaRandomizeAllBtn_Clicked(boolean pWholeFractal) {
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
        refreshFlameImage(true, false, 1, true, false);
      }
    } else {
      XForm xForm = getCurrXForm(XFormType.NORMAL);
      if (xForm != null) {
        saveUndoPoint();
        xForm.randomizeModColorEffects();
        refreshXFormUI(xForm);
        refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void xFormModGammaRandomizeBtn_Clicked(boolean pWholeFractal) {
    if (pWholeFractal) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        for (Layer layer : flame.getLayers()) {
          for (XForm xForm : layer.getXForms()) {
            xForm.randomizeModGamma();
          }
        }
        refreshXFormUI(getCurrXForm(XFormType.BOTH));
        refreshFlameImage(true, false, 1, true, false);
      }
    } else {
      XForm xForm = getCurrXForm(XFormType.NORMAL);
      if (xForm != null) {
        saveUndoPoint();
        xForm.randomizeModGamma();
        refreshXFormUI(xForm);
        refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void xFormModContrastRandomizeBtn_Clicked(boolean pWholeFractal) {
    if (pWholeFractal) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        for (Layer layer : flame.getLayers()) {
          for (XForm xForm : layer.getXForms()) {
            xForm.randomizeModContrast();
          }
        }
        refreshXFormUI(getCurrXForm(XFormType.BOTH));
        refreshFlameImage(true, false, 1, true, false);
      }
    } else {
      XForm xForm = getCurrXForm(XFormType.NORMAL);
      if (xForm != null) {
        saveUndoPoint();
        xForm.randomizeModContrast();
        refreshXFormUI(xForm);
        refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void xFormModSaturationRandomizeBtn_Clicked(boolean pWholeFractal) {
    if (pWholeFractal) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        for (Layer layer : flame.getLayers()) {
          for (XForm xForm : layer.getXForms()) {
            xForm.randomizeModSaturation();
          }
        }
        refreshXFormUI(getCurrXForm(XFormType.BOTH));
        refreshFlameImage(true, false, 1, true, false);
      }
    } else {
      XForm xForm = getCurrXForm(XFormType.NORMAL);
      if (xForm != null) {
        saveUndoPoint();
        xForm.randomizeModSaturation();
        refreshXFormUI(xForm);
        refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void xFormModHueRandomizeBtn_Clicked(boolean pWholeFractal) {
    if (pWholeFractal) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        saveUndoPoint();
        for (Layer layer : flame.getLayers()) {
          for (XForm xForm : layer.getXForms()) {
            xForm.randomizeModHue();
          }
        }
        refreshXFormUI(getCurrXForm(XFormType.BOTH));
        refreshFlameImage(true, false, 1, true, false);
      }
    } else {
      XForm xForm = getCurrXForm(XFormType.NORMAL);
      if (xForm != null) {
        saveUndoPoint();
        xForm.randomizeModHue();
        refreshXFormUI(xForm);
        refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void xFormModLocalGammaResetAllBtn_Clicked(boolean pWholeFractal) {
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
        refreshFlameImage(true, false, 1, true, false);
      }
    } else {
      XForm xForm = getCurrXForm(XFormType.NORMAL);
      if (xForm != null) {
        saveUndoPoint();
        xForm.resetModColorEffects();
        refreshXFormUI(xForm);
        refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public BatchRendererController getBatchRendererController() {
    return batchRendererController;
  }

  public QuiltRendererController getQuiltRendererController() {
    return quiltRendererController;
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

  public void resetPostBlurSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.resetPostBlurSettings();
      refreshUI();
    }
  }

  public void resetPostBokehSettings() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.getSolidRenderSettings().setupDefaultPostBokehOptions();
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
    } finally {
      gridRefreshing = oldGridRefreshing;
    }
  }

  public void toggleDrawGuidesButton_clicked() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setWithGuides(data.toggleDrawGuidesButton.isSelected());
      refreshFlameImage(true, false, 1, false, false);
    }
  }

  public GradientControlsDelegate getGradientControls() {
    return gradientControls;
  }

  private static final int DFLT_VERT_TOOLBAR_SIZE = 52;

  public void refreshMacroButtonsPanel() {
    if (prefs.isTinaMacroButtonsVertical()) {
      data.macroButtonHorizRootPanel.setPreferredSize(new Dimension(0, 0));
      data.macroButtonHorizRootPanel.setVisible(false);
      refreshMacroButtonsPanelVertical();
    } else {
      data.previewEastMainPanel.setPreferredSize(new Dimension(DFLT_VERT_TOOLBAR_SIZE, 0));
      refreshMacroButtonsPanelHorizontal();
    }
  }

  public void refreshMacroButtonsPanelVertical() {
    data.macroButtonVertPanel.removeAll();
    if (prefs.getTinaMacroButtons().size() == 0) {
      data.previewEastMainPanel.setPreferredSize(new Dimension(DFLT_VERT_TOOLBAR_SIZE, 0));
    } else {
      int toolbarWidth = prefs.getTinaMacroToolbarWidth();
      int gap = 1;
      int buttonWidth = prefs.getTinaMacroToolbarWidth() - 16;
      int buttonHeight = 24;
      int toolbarHeight = buttonHeight * prefs.getTinaMacroButtons().size() + gap * (prefs.getTinaMacroButtons().size() - 1);
      data.macroButtonVertPanel.setPreferredSize(new Dimension(toolbarWidth, toolbarHeight));

      data.previewEastMainPanel.setPreferredSize(new Dimension(DFLT_VERT_TOOLBAR_SIZE + toolbarWidth, 0));
      for (final MacroButton macroButton : prefs.getTinaMacroButtons()) {
        JButton button = new JButton();
        button.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
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
            button.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/" + macroButton.getImage())));
          } catch (Exception ex) {
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
    } else {
      int toolbarHeight = prefs.getTinaMacroToolbarHeight();
      int gap = 1;
      int buttonWidth = prefs.getTinaMacroToolbarWidth();
      int buttonHeight = 24;
      int toolbarWidth = buttonWidth * prefs.getTinaMacroButtons().size() + gap * (prefs.getTinaMacroButtons().size() - 1);
      data.macroButtonHorizPanel.setPreferredSize(new Dimension(toolbarWidth, toolbarHeight));

      data.macroButtonHorizRootPanel.setPreferredSize(new Dimension(0, toolbarHeight + 2));
      for (final MacroButton macroButton : prefs.getTinaMacroButtons()) {
        JButton button = new JButton();
        button.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
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
            button.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/" + macroButton.getImage())));
          } catch (Exception ex) {
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
      } else {
        script = Tools.readUTF8Textfile(filename);
      }
      runScript(filename, script);
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void runScript(String scriptPath, String scriptText) {
    try {
      ScriptRunner scriptRunner = ScriptRunner.compile(scriptText);
      scriptRunner.setScriptPath(scriptPath);
      saveUndoPoint();
      runJWFScript(scriptRunner);
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void resetGridToDefaults() {
    flamePanel.resetGridToDefaults();
    refreshFlameImage(true, false, 1, true, false);
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
      registerToEditor(getCurrFlame(), getCurrLayer());
      refreshUI();
    }
  }

  private RGBPalette getLastGradient() {
    return _lastGradient;
  }

  public void changeAffineEditPlane(EditPlane editPlane) {
    if (gridRefreshing || cmbRefreshing || getCurrFlame() == null) {
      return;
    }
    getCurrFlame().setEditPlane(editPlane);
    refreshEditPlaneToggleButtons(editPlane);
    transformationChanged(true);
  }

  private void refreshEditPlaneToggleButtons(EditPlane editPlane) {
    boolean oldCmbRefreshing = cmbRefreshing;
    try {
      cmbRefreshing = true;
      data.affineXYEditPlaneToggleBtn.setSelected(EditPlane.XY.equals(editPlane));
      data.affineYZEditPlaneToggleBtn.setSelected(EditPlane.YZ.equals(editPlane));
      data.affineZXEditPlaneToggleBtn.setSelected(EditPlane.ZX.equals(editPlane));
    } finally {
      cmbRefreshing = oldCmbRefreshing;
    }
  }

  public void selectImageForBackgroundButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        if (getCurrFlame().getBGImageFilename().length() > 0) {
          chooser.setSelectedFile(new File(getCurrFlame().getBGImageFilename()));
        } else {
          chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
        }
      } catch (Exception ex) {
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
        refreshFlameImage(true, false, 1, true, true);
      } catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void removeBackgroundImageButton_actionPerformed() {
    saveUndoPoint();
    getCurrFlame().setBGImageFilename(null);
    refreshFlameImage(true, false, 1, true, true);
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
    return mainEditorFrame.getRenderProgressBar();
  }

  public void renderFlameButton_actionPerformed(ActionEvent e) {
    refreshFlameImage(false, false, 1, true, false);
  }

  public void optixDenoiserButton_actionPerformed(ActionEvent e) {
    try {
      flamePreviewHelper.cancelBackgroundRender();
      SimpleImage img = flamePreviewHelper.getImage();
      if (img != null) {
        new OptixCmdLineDenoiser().addDenoisePreviewToImage(img, getCurrFlame().getPostOptiXDenoiserBlend());
        flamePreviewHelper.setImage(img);
        flamePreviewHelper.forceRepaint();
      }
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void runJWFScript(ScriptRunner pScript) {
    FormBuilder formBuilder = pScript.createScriptForm(this);
    if (formBuilder == null) {
      pScript.run(this);
    } else {
      ScriptParamsForm form = formBuilder.getProduct(mainEditorFrame, errorHandler);
      form.showModal(this, pScript);
    }
  }

  @Override
  public ScriptParam getParamByName(String pName) {
    return new ScriptParam("");
  }

  public void loadScriptProps() {
    try {
      if (scriptPropFile.exists()) {
        FileInputStream fis = new FileInputStream(scriptPropFile);
        scriptProps.load(fis);
        fis.close();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void saveScriptProps() {
    try {
      FileOutputStream fos = new FileOutputStream(scriptPropFile);
      scriptProps.store(fos, "JWildfire script properties");
      fos.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void setScriptProperty(ScriptRunner runner, String propName, String propVal) {
    String path = runner.getScriptPath();
    String name = path + "." + propName;
    String normalizedName = name.replaceAll("[\\s=:]", ".");
    scriptProps.setProperty(normalizedName, propVal);
  }

  @Override
  public String getScriptProperty(ScriptRunner runner, String propName) {
    String path = runner.getScriptPath();
    String name = path + "." + propName;
    String normalizedName = name.replaceAll("[\\s=:]", ".");
    String propVal = scriptProps.getProperty(normalizedName);
    return propVal;
  }

  @Override
  public String getScriptProperty(ScriptRunner runner, String propName, String defaultVal) {
    String propVal = getScriptProperty(runner, propName);
    if (propVal == null) {
      return defaultVal;
    } else {
      return propVal;
    }
  }

  public void countDown(int pTime) {
    for (int i = pTime; i >= 0; i--) {
      Rectangle bounds = flamePreviewHelper.getPanelBounds();
      SimpleImage img = new SimpleImage((int) bounds.getWidth(), (int) bounds.getHeight());
      TextTransformer txt = new TextTransformer();
      txt.setText1(i > 0 ? String.valueOf(i) : "go!");
      txt.setAntialiasing(true);
      txt.setColor(Color.RED);
      txt.setMode(Mode.NORMAL);
      txt.setFontStyle(FontStyle.BOLD);
      txt.setFontName("Arial");
      txt.setFontSize(64);
      txt.setHAlign(HAlignment.CENTRE);
      txt.setVAlign(VAlignment.CENTRE);
      txt.transformImage(img);
      flamePreviewHelper.setImage(img);
      flamePreviewHelper.forceRepaint();
      try {
        Thread.sleep(i > 0 ? 500 : 50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private ImagePanel getFilterKernelPreviewPanel() {
    if (data.filterKernelPreviewPanel == null) {
      int width = data.filterKernelPreviewRootPnl.getWidth();
      int height = data.filterKernelPreviewRootPnl.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      data.filterKernelPreviewPanel = new ImagePanel(img, 0, 0, data.filterKernelPreviewRootPnl.getWidth());
      data.filterKernelPreviewRootPnl.add(data.filterKernelPreviewPanel, BorderLayout.CENTER);
      data.filterKernelPreviewRootPnl.getParent().validate();
    }
    return data.filterKernelPreviewPanel;
  }

  private FilterKernelVisualisationRenderer getFilterKernelVisualisationRenderer(boolean pFlatMode) {
    if (pFlatMode) {
      return new FilterKernelVisualisationFlatRenderer(getCurrFlame());
    } else {
      return new FilterKernelVisualisation3dRenderer(getCurrFlame());
    }
  }

  protected void refreshFilterKernelPreviewImg() {
    try {
      if (getCurrFlame() != null) {
        ImagePanel imgPanel = getFilterKernelPreviewPanel();
        int width = imgPanel.getWidth();
        int height = imgPanel.getHeight();
        if (width >= 16 && height >= 4) {
          SimpleImage img = getFilterKernelVisualisationRenderer(data.filterKernelFlatPreviewBtn.isSelected()).createKernelVisualisation(width, height);
          imgPanel.setImage(img);
        }
        imgPanel.getParent().validate();
        imgPanel.repaint();
      }
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void filterKernelFlatPreviewBtn_clicked() {
    refreshFilterKernelPreviewImg();
  }


  public void sendCurrentFlameToBatchRenderer() {
    try {
      Flame flame = getCurrFlame();
      if (flame != null) {
        String filename = qsaveFilenameGen.generateNextFilename();
        new FlameWriter().writeFlame(generateExportFlame(flame), filename);
        batchRendererController.importFlame(filename, getResolutionProfile(), getQualityProfile());
        messageHelper.showStatusMessage(getCurrFlame(), "sent as file <" + new File(filename).getName() + "> to batch-renderer");
      }
    } catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  private void stopPreviewRendering() {
    flamePreviewHelper.stopPreviewRendering();
  }

  public void realtimePreviewToggleButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.getConfig().setProgressivePreview(data.realtimePreviewToggleButton.isSelected());
          refreshFlameImage(true, false, 1, true, false);
        }
      } finally {
        refreshing = false;
      }
    }
  }

  public boolean isRealtimePreviewRefresh() {
    return data.realtimePreviewToggleButton.isSelected();
  }

  public void relWeightsResetButton_clicked() {
    if (gridRefreshing)
      return;
    Layer layer = getCurrLayer();
    int transformRow = data.transformationsTable.getSelectedRow();
    int xaosRow = data.relWeightsTable.getSelectedRow();
    if (layer != null && transformRow >= 0 && transformRow < layer.getXForms().size()) {
      for (int xaosIdx = 0; xaosIdx < layer.getXForms().get(transformRow).getModifiedWeights().length; xaosIdx++) {
        layer.getXForms().get(transformRow).getModifiedWeights()[xaosIdx] = 1.0;
      }
      gridRefreshing = true;
      try {
        refreshRelWeightsTable();
        if (xaosRow >= 0) {
          data.relWeightsTable.getSelectionModel().setSelectionInterval(xaosRow, xaosRow);
        }
        refreshFlameImage(true, false, 1, true, false);
      } finally {
        gridRefreshing = false;
      }
    }
    relWeightsTableClicked();
  }

  public void relWeightsResetAllButton_clicked() {
    if (gridRefreshing)
      return;
    Layer layer = getCurrLayer();
    int xaosRow = data.relWeightsTable.getSelectedRow();
    if (layer != null) {
      for (int transform = 0; transform < layer.getXForms().size(); transform++) {
        for (int xaosIdx = 0; xaosIdx < layer.getXForms().get(transform).getModifiedWeights().length; xaosIdx++) {
          layer.getXForms().get(transform).getModifiedWeights()[xaosIdx] = 1.0;
        }
      }
      gridRefreshing = true;
      try {
        refreshRelWeightsTable();
        if (xaosRow >= 0) {
          data.relWeightsTable.getSelectionModel().setSelectionInterval(xaosRow, xaosRow);
        }
        refreshFlameImage(true, false, 1, true, false);
      } finally {
        gridRefreshing = false;
      }
    }

    relWeightsTableClicked();
  }

  public JWildfire getDesktop() {
    return desktop;
  }

  public FlamesGPURenderController getGpuRendererCtrl() {
    return gpuRendererCtrl;
  }

  public void setGpuRendererCtrl(FlamesGPURenderController gpuRendererCtrl) {
    this.gpuRendererCtrl = gpuRendererCtrl;
  }

  public void allsaveButton_clicked() {
    try {
      List<Flame> flames = new ArrayList<>();
      for (FlameThumbnail thumbnail : randomBatch) {
        if (thumbnail.getSelectCheckbox() != null && thumbnail.getSelectCheckbox().isSelected()) {
          flames.add(generateExportFlame(thumbnail.getFlame()));
        }
      }
      if (!flames.isEmpty()) {
        JFileChooser chooser = new FlameFileChooser(prefs);
        if (prefs.getOutputFlamePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          String filename = file.getAbsolutePath();
          if (!filename.endsWith("." + Tools.FILEEXT_FLAME)) {
            filename += "." + Tools.FILEEXT_FLAME;
          }
          new FlameWriter().writeFlames(flames, filename);
          messageHelper.showStatusMessage(getCurrFlame(), flames.size() + " " + (flames.size() > 1 ? "flames" : "flame") + " saved to disc");
          prefs.setLastOutputFlameFile(file);
        }
      }
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }

  }

  public void toggleThumbnailSelectionAll() {
    for (FlameThumbnail thumbnail : randomBatch) {
      if (thumbnail.getSelectCheckbox() != null) {
        thumbnail.getSelectCheckbox().setSelected(!thumbnail.getSelectCheckbox().isSelected());
      }
    }
  }

  public void removeAllThumbnails() {
    if (!randomBatch.isEmpty() && StandardDialogs.confirm(flamePanel, "Do you really want to remove all " + randomBatch.size() + " " + (randomBatch.size() > 1 ? "flames" : "flame") + "\n from the thumbnail-ribbon?\n (Please note that this cannot be undone)")) {
      randomBatch.clear();
      updateThumbnails();
    }
  }

  public void removeSelectedThumbnails() {
    List<FlameThumbnail> newRandomBatch = new ArrayList<>();
    for (FlameThumbnail thumbnail : randomBatch) {
      if (thumbnail.getSelectCheckbox() == null || !thumbnail.getSelectCheckbox().isSelected()) {
        newRandomBatch.add(thumbnail);
      }
    }
    int delta = randomBatch.size() - newRandomBatch.size();
    if (delta > 0 && StandardDialogs.confirm(flamePanel, "Do you really want to remove " + delta + " " + (delta > 1 ? "flames" : "flame") + "\n from the thumbnail-ribbon?\n (Please note that this cannot be undone)")) {
      randomBatch = newRandomBatch;
      updateThumbnails();
    }
  }

  public void deselectAllThumbnails() {
    for (FlameThumbnail thumbnail : randomBatch) {
      if (thumbnail.getSelectCheckbox() != null) {
        thumbnail.getSelectCheckbox().setSelected(false);
      }
    }
  }

  public void initNonlinearVariationCmb() {
    for (TinaNonlinearControlsRow row : data.TinaNonlinearControlsRows) {
      row.initVariationCmb();
    }
  }

  protected WeightingFieldControlsUpdater getWeightMapControlsUpdater(XForm xform) {
    WeightingFieldType weightMapType = xform != null && xform.getWeightingFieldType() != null ? xform.getWeightingFieldType() : WeightingFieldType.NONE;
    return WeightingFieldControlsUpdaterFactory.getInstance(weightMapType, this, weightMapData);
  }

  public void weightMapParam01REd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldParam01REd_changed();
  }

  public void weightingFieldVarParam1NameCmb_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam1NameCmb_changed();
  }

  public void weightingFieldVarParam2NameCmb_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam2NameCmb_changed();
  }

  public void weightingFieldVarParam3NameCmb_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam3NameCmb_changed();
  }

  public void weightMapParam02REd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldParam02REd_changed();
  }

  public void weightMapParam03REd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldParam03REd_changed();
  }

  public void weightMapParam04Cmb_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldParam04Cmb_changed();
  }

  public void weightMapParam05REd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldParam05REd_changed();
  }

  public void weightMapParam06REd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldParam06REd_changed();
  }

  public void weightMapParam07REd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldParam07REd_changed();
  }

  public void weightMapParam08Cmb_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldParam08Cmb_changed();
  }

  public void weightMapColorIntensityREd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldColorIntensityREd_changed();
  }

  public void weightMapVariationIntensityREd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarAmountIntensityREd_changed();
  }

  public void weightingFieldJitterIntensityREd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldJitterIntensityREd_changed();
  }

  public void weightingFieldVarParam1AmountREd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam1AmountREd_changed();
  }

  public void weightingFieldVarParam2AmountREd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam2AmountREd_changed();
  }

  public void weightingFieldVarParam3AmountREd_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam3AmountREd_changed();
  }

  public void weightMapTypeCmb_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldTypeCmb_changed();
  }

  public void weightMapInputCmb_changed() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldInputCmb_changed();
  }

  public void weightMapColorMapFilenameBtn_clicked() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldColorMapFilenameBtn_clicked();
  }

  WeightingFieldMutation weightingFieldMutation = new WeightingFieldMutation();

  public void weightMapRandomizeAllBtn_clicked(boolean wholeFractal) {
    if (wholeFractal) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        Layer layer = getCurrLayer();
        if (layer != null) {
          saveUndoPoint();
          weightingFieldMutation.execute(layer);
          XForm xForm = getCurrXForm();
          if (xForm != null) {
            refreshXFormUI(xForm);
          }
          refreshFlameImage(true, false, 1, true, false);
        }
      }
    } else {
      XForm xForm = getCurrXForm();
      if (xForm != null) {
        saveUndoPoint();
        weightingFieldMutation.applyRandomWeightField(xForm);
        refreshXFormUI(xForm);
        refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void weightMapResetAllBtn_clicked(boolean wholeFractal) {
    if (wholeFractal) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        Layer layer = getCurrLayer();
        if (layer != null) {
          saveUndoPoint();
          weightingFieldMutation.clearWeightingField(layer);
          XForm xForm = getCurrXForm();
          if (xForm != null) {
            refreshXFormUI(xForm);
          }
          refreshFlameImage(true, false, 1, true, false);
        }
      }
    } else {
      XForm xForm = getCurrXForm();
      if (xForm != null) {
        saveUndoPoint();
        weightingFieldMutation.clearWeightingField(xForm);
        refreshXFormUI(xForm);
        refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void weightMapVariationIntensityREd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapVariationIntensityREd_reset();
  }

  public void weightingFieldJitterIntensityREd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldJitterIntensityREd_reset();
  }

  public void weightMapInputCmb_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapInputCmb_reset();
  }

  public void weightMapColorIntensityREd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapColorIntensityREd_reset();
  }

  public void weightingFieldVarParam1AmountREd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam1AmountREd_reset();
  }

  public void weightingFieldVarParam2AmountREd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam2AmountREd_reset();
  }

  public void weightingFieldVarParam3AmountREd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldVarParam3AmountREd_reset();
  }

  public void weightMapParam01REd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapParam01REd_reset();
  }

  public void weightMapParam02REd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapParam02REd_reset();
  }

  public void weightMapParam03REd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapParam03REd_reset();
  }

  public void weightMapParam05REd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapParam05REd_reset();
  }

  public void weightMapParam06REd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapParam06REd_reset();
  }

  public void weightMapParam04Cmb_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapParam04Cmb_reset();
  }

  public void weightMapParam07REd_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapParam07REd_reset();
  }

  public void weightMapParam08Cmb_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapParam08Cmb_reset();
  }

  public void weightMapColorMapFilename_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightMapColorMapFilename_reset();
  }

  public void weightingFieldTypeCmb_reset() {
    getWeightMapControlsUpdater(getCurrXForm()).weightingFieldTypeCmb_reset();
  }

  public GradientCurveEditorControlsDelegate getGradientCurveEditorControls() {
    return gradientCurveEditorControls;
  }

  public FrameControlsUtil getFrameControlsUtil() {
    return frameControlsUtil;
  }

  public FlamePreviewHelper getFlamePreviewHelper() {
    return flamePreviewHelper;
  }

  public void spatialFilterTypeCmb_reset() {
    data.tinaFilterTypeCmb.setSelectedItem(new Flame().getSpatialFilteringType());
    spatialFilterTypeCmb_changed();
  }

  public void spatialFilterKernelCmb_reset() {
    FilteringType filteringType = (FilteringType) data.tinaFilterTypeCmb.getSelectedItem();
    if (filteringType == null) {
      filteringType = FilteringType.GLOBAL_SMOOTHING;
    }
    setDefaultFilterKernel(filteringType);
    spatialFilterKernelCmb_changed();
    flameControls.enableFilterUI();
  }

  public void paletteFrequencyREd_reset() {
    paletteTextFieldReset(data.paletteFrequencySlider, data.paletteFrequencyREd, "modFrequency", 1.0);
  }

  public void paletteShiftREd_reset() {
    paletteTextFieldReset(data.paletteShiftSlider, data.paletteShiftREd, "modShift", 100.0);
  }

  public void paletteSwapRGBREd_reset() {
    paletteTextFieldReset(data.paletteSwapRGBSlider, data.paletteSwapRGBREd, "modSwapRGB", 1.0);
  }

  public void paletteBlurREd_reset() {
    paletteTextFieldReset(data.paletteBlurSlider, data.paletteBlurREd, "modBlur", 1.0);
  }

  public void paletteRedREd_reset() {
    paletteTextFieldReset(data.paletteRedSlider, data.paletteRedREd, "modRed", 1.0);
  }

  public void paletteGreenREd_reset() {
    paletteTextFieldReset(data.paletteGreenSlider, data.paletteGreenREd, "modGreen", 1.0);
  }

  public void paletteBlueREd_reset() {
    paletteTextFieldReset(data.paletteBlueSlider, data.paletteBlueREd, "modBlue", 1.0);
  }

  public void paletteHueREd_reset() {
    paletteTextFieldReset(data.paletteHueSlider, data.paletteHueREd, "modHue", 1.0);
  }

  public void paletteSaturationREd_reset() {
    paletteTextFieldReset(data.paletteSaturationSlider, data.paletteSaturationREd, "modSaturation", 1.0);
  }

  public void paletteContrastREd_reset() {
    paletteTextFieldReset(data.paletteContrastSlider, data.paletteContrastREd, "modContrast", 1.0);
  }

  public void paletteGammaREd_reset() {
    paletteTextFieldReset(data.paletteGammaSlider, data.paletteGammaREd, "modGamma", 1.0);
  }

  public void paletteBrightnessREd_reset() {
    paletteTextFieldReset(data.paletteBrightnessSlider, data.paletteBrightnessREd, "modBrightness", 1.0);
  }

  public void gradientColorMapHorizOffsetREd_reset() {
    layerTextFieldReset(data.gradientColorMapHorizOffsetSlider, data.gradientColorMapHorizOffsetREd, "gradientMapHorizOffset", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapHorizScaleREd_reset() {
    layerTextFieldReset(data.gradientColorMapHorizScaleSlider, data.gradientColorMapHorizScaleREd, "gradientMapHorizScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapLocalColorAddREd_reset() {
    layerTextFieldReset(data.gradientColorMapLocalColorAddSlider, data.gradientColorMapLocalColorAddREd, "gradientMapLocalColorAdd", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapLocalColorScaleREd_reset() {
    layerTextFieldReset(data.gradientColorMapLocalColorScaleSlider, data.gradientColorMapLocalColorScaleREd, "gradientMapLocalColorScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapVertOffsetREd_reset() {
    layerTextFieldReset(data.gradientColorMapVertOffsetSlider, data.gradientColorMapVertOffsetREd, "gradientMapVertOffset", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void gradientColorMapVertScaleREd_reset() {
    layerTextFieldReset(data.gradientColorMapVertScaleSlider, data.gradientColorMapVertScaleREd, "gradientMapVertScale", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void affineC00REd_reset() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      data.affineC00REd.setValue(createNewXForm().getXYCoeff00());
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC00REd, frameControlsUtil.getAffinePropertyName(xForm, "00", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC01REd_reset() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      data.affineC01REd.setValue(createNewXForm().getXYCoeff01());
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC01REd, frameControlsUtil.getAffinePropertyName(xForm, "01", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC10REd_reset() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      data.affineC10REd.setValue(createNewXForm().getXYCoeff10());
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC10REd, frameControlsUtil.getAffinePropertyName(xForm, "10", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC20REd_reset() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      data.affineC20REd.setValue(createNewXForm().getXYCoeff20());
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC20REd, frameControlsUtil.getAffinePropertyName(xForm, "20", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC21REd_reset() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      data.affineC21REd.setValue(createNewXForm().getXYCoeff21());
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC21REd, frameControlsUtil.getAffinePropertyName(xForm, "21", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void affineC11REd_reset() {
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      data.affineC11REd.setValue(createNewXForm().getXYCoeff11());
      frameControlsUtil.valueChangedByTextField(xForm, null, data.affineC11REd, frameControlsUtil.getAffinePropertyName(xForm, "11", data.affineEditPostTransformButton.isSelected()), 1.0, 0.0);
      transformationChanged(true);
    }
  }

  public void xFormColorTypeCmb_reset() {
    data.xFormColorTypeCmb.setSelectedItem(createNewXForm().getColorType());
    xFormColorTypeCmb_changed();
  }

  public void xFormColorREd_reset() {
    switch ((ColorType) data.xFormColorTypeCmb.getSelectedItem()) {
      case TARGET: {
        XForm xForm = getCurrXForm();
        if (xForm != null) {
          XForm newXForm = createNewXForm();
          undoManager.saveUndoPoint(getCurrFlame());
          xForm.setTargetColor(newXForm.getTargetColor());
          data.xFormTargetColorBtn.setBackground(xForm.getTargetColor().getColor());
        }
      }
      break;
      default:
        xFormTextFieldReset(data.xFormColorSlider, data.xFormColorREd, "color", SLIDER_SCALE_COLOR);
        break;
    }
  }

  public void xFormSymmetryREd_reset() {
    xFormTextFieldReset(data.xFormSymmetrySlider, data.xFormSymmetryREd, "colorSymmetry", SLIDER_SCALE_COLOR);
  }

  public void xFormDrawModeCmb_reset() {
    data.xFormDrawModeCmb.setSelectedItem(createNewXForm().getDrawMode());
    xFormDrawModeCmb_changed();
  }

  public void xFormOpacityREd_reset() {
    xFormTextFieldReset(data.xFormOpacitySlider, data.xFormOpacityREd, "opacity", SLIDER_SCALE_COLOR);
  }

  public void xFormMaterialREd_reset() {
    xFormTextFieldReset(data.xFormMaterialSlider, data.xFormMaterialREd, "material", SLIDER_SCALE_COLOR);
  }

  public void xFormMaterialSpeedREd_reset() {
    xFormTextFieldReset(data.xFormMaterialSpeedSlider, data.xFormMaterialSpeedREd, "materialSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModGammaREd_reset() {
    xFormTextFieldReset(data.xFormModGammaSlider, data.xFormModGammaREd, "modGamma", SLIDER_SCALE_COLOR);
  }

  public void xFormModGammaSpeedREd_reset() {
    xFormTextFieldReset(data.xFormModGammaSpeedSlider, data.xFormModGammaSpeedREd, "modGammaSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModContrastREd_reset() {
    xFormTextFieldReset(data.xFormModContrastSlider, data.xFormModContrastREd, "modContrast", SLIDER_SCALE_COLOR);
  }

  public void xFormModContrastSpeedREd_reset() {
    xFormTextFieldReset(data.xFormModContrastSpeedSlider, data.xFormModContrastSpeedREd, "modContrastSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModSaturationREd_reset() {
    xFormTextFieldReset(data.xFormModSaturationSlider, data.xFormModSaturationREd, "modSaturation", SLIDER_SCALE_COLOR);
  }

  public void xFormModSaturationSpeedREd_reset() {
    xFormTextFieldReset(data.xFormModSaturationSpeedSlider, data.xFormModSaturationSpeedREd, "modSaturationSpeed", SLIDER_SCALE_COLOR);
  }

  public void xFormModHueREd_reset() {
    xFormTextFieldReset(data.xFormModHueSlider, data.xFormModHueREd, "modHue", SLIDER_SCALE_COLOR);
  }

  public void xFormModHueSpeedREd_reset() {
    xFormTextFieldReset(data.xFormModHueSpeedSlider, data.xFormModHueSpeedREd, "modHueSpeed", SLIDER_SCALE_COLOR);
  }

  public NonlinearControlsDelegate getNonlinearControls() {
    return nonlinearControls;
  }

  public void affineCoordsViewTypeCmd_changed() {
    // TODO Auto-generated method stub

  }

}
