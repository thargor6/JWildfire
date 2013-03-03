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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
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

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.ShadingInfo;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.batch.Job;
import org.jwildfire.create.tina.batch.JobRenderThread;
import org.jwildfire.create.tina.batch.JobRenderThreadController;
import org.jwildfire.create.tina.dance.DancingFractalsController;
import org.jwildfire.create.tina.edit.UndoManager;
import org.jwildfire.create.tina.io.Flam3PaletteReader;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.io.Flam3Writer;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.palette.MedianCutQuantizer;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RGBPaletteRenderer;
import org.jwildfire.create.tina.palette.RandomRGBPaletteGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSample;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomflame.SubFlameRandomFlameGenerator;
import org.jwildfire.create.tina.render.DrawFocusPointFlameRenderer;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.script.ScriptRunner;
import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.MainController;

import com.l2fprod.common.swing.JFontChooser;

public class TinaController implements FlameHolder, JobRenderThreadController, ScriptRunnerEnvironment, UndoManagerHolder<Flame> {
  private static final double SLIDER_SCALE_PERSPECTIVE = 100.0;
  private static final double SLIDER_SCALE_CENTRE = 5000.0;
  private static final double SLIDER_SCALE_ZOOM = 1000.0;
  private static final double SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY = 100.0;
  private static final double SLIDER_SCALE_GAMMA = 100.0;
  private static final double SLIDER_SCALE_FILTER_RADIUS = 100.0;
  private static final double SLIDER_SCALE_GAMMA_THRESHOLD = 5000.0;
  private static final double SLIDER_SCALE_COLOR = 100.0;
  private static final double SLIDER_SCALE_ZPOS = 50.0;
  private static final double SLIDER_SCALE_DOF = 100.0;
  private static final double SLIDER_SCALE_DOF_AREA = 100.0;
  private static final double SLIDER_SCALE_DOF_EXPONENT = 100.0;
  private static final double SLIDER_SCALE_AMBIENT = 100.0;
  private static final double SLIDER_SCALE_PHONGSIZE = 10.0;
  private static final double SLIDER_SCALE_LIGHTPOS = 100.0;
  private static final double SLIDER_SCALE_BLUR_FALLOFF = 10.0;

  private DancingFractalsController dancingFractalsController;
  private TinaInteractiveRendererController interactiveRendererCtrl;
  private TinaSWFAnimatorController swfAnimatorCtrl;

  private final JInternalFrame tinaFrame;
  private final String tinaFrameTitle;
  private final JPanel centerPanel;
  private boolean firstFlamePanel = true;
  private FlamePanel flamePanel;
  private FlamePanel batchPreviewFlamePanel;

  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  boolean gridRefreshing = false;
  boolean cmbRefreshing = false;
  boolean refreshing = false;

  private final List<Job> batchRenderList = new ArrayList<Job>();
  private final UndoManager<Flame> undoManager = new UndoManager<Flame>();
  private final QuickSaveFilenameGen qsaveFilenameGen;

  private MainController mainController;
  private final JTabbedPane rootTabbedPane;
  // misc
  private Flame currFlame;
  private boolean noRefresh;
  private final ProgressUpdater mainProgressUpdater;
  private final ProgressUpdater jobProgressUpdater;
  private TinaControllerData data = new TinaControllerData();

  public TinaController(TinaControllerParameter parameterObject) {
    tinaFrame = parameterObject.pTinaFrame;
    tinaFrameTitle = tinaFrame.getTitle();
    errorHandler = parameterObject.pErrorHandler;
    prefs = parameterObject.pPrefs;
    centerPanel = parameterObject.pCenterPanel;

    dancingFractalsController = new DancingFractalsController(this, parameterObject.pErrorHandler, parameterObject.pDancingFlamesFlamePnl, parameterObject.pDancingFlamesGraph1Pnl,
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
        parameterObject.pDancingFlamesMotionLinksTable);

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
    data.gammaREd = parameterObject.pGammaREd;
    data.gammaSlider = parameterObject.pGammaSlider;
    data.vibrancyREd = parameterObject.pVibrancyREd;
    data.vibrancySlider = parameterObject.pVibrancySlider;
    data.filterRadiusREd = parameterObject.pFilterRadiusREd;
    data.filterRadiusSlider = parameterObject.pFilterRadiusSlider;
    data.filterKernelCmb = parameterObject.pFilterKernelCmb;
    data.deFilterEnableCbx = parameterObject.pDEFilterEnableCbx;
    data.deFilterMaxRadiusREd = parameterObject.pDEFilterMaxRadiusREd;
    data.deFilterMaxRadiusSlider = parameterObject.pDEFilterMaxRadiusSlider;
    data.deFilterMinRadiusREd = parameterObject.pDEFilterMinRadiusREd;
    data.deFilterMinRadiusSlider = parameterObject.pDEFilterMinRadiusSlider;
    data.deFilterCurveREd = parameterObject.pDEFilterCurveREd;
    data.deFilterKernelCmb = parameterObject.pDEFilterKernelCmb;
    data.deFilterCurveSlider = parameterObject.pDEFilterCurveSlider;
    data.gammaThresholdREd = parameterObject.pGammaThresholdREd;
    data.gammaThresholdSlider = parameterObject.pGammaThresholdSlider;
    data.bgTransparencyCBx = parameterObject.pBGTransparencyCBx;
    data.bgColorRedREd = parameterObject.pBGColorRedREd;
    data.bgColorRedSlider = parameterObject.pBGColorRedSlider;
    data.bgColorGreenREd = parameterObject.pBGColorGreenREd;
    data.bgColorGreenSlider = parameterObject.pBGColorGreenSlider;
    data.bgColorBlueREd = parameterObject.pBGColorBlueREd;
    data.bgColorBlueSlider = parameterObject.pBGColorBlueSlider;
    data.paletteRandomPointsREd = parameterObject.pPaletteRandomPointsREd;
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
    data.interactiveQualityProfileCmb = parameterObject.pInteractiveQualityProfileCmb;
    data.interactiveResolutionProfileCmb = parameterObject.pInteractiveResolutionProfileCmb;
    data.swfAnimatorQualityProfileCmb = parameterObject.pSWFAnimatorQualityProfileCmb;
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
    data.affineMoveAmountREd = parameterObject.pAffineMoveAmountREd;
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
    data.affineEditPostTransformSmallButton = parameterObject.pAffineEditPostTransformSmallButton;
    data.affinePreserveZButton = parameterObject.pAffinePreserveZButton;
    data.affineScaleXButton = parameterObject.pAffineScaleXButton;
    data.affineScaleYButton = parameterObject.pAffineScaleYButton;
    data.mouseEditZoomInButton = parameterObject.pMouseEditZoomInButton;
    data.mouseEditZoomOutButton = parameterObject.pMouseEditZoomOutButton;

    data.randomBatchPanel = parameterObject.pRandomBatchPanel;
    data.TinaNonlinearControlsRows = parameterObject.pTinaNonlinearControlsRows;
    data.gradientLibraryPanel = parameterObject.pGradientLibraryPanel;
    data.gradientLibraryGradientCmb = parameterObject.pGradientLibraryGradientCmb;
    data.renderFlameButton = parameterObject.pRenderFlameButton;
    data.renderMainButton = parameterObject.pRenderMainButton;
    data.appendToMovieButton = parameterObject.pAppendToMovieButton;

    data.xFormColorREd = parameterObject.pXFormColorREd;
    data.xFormColorSlider = parameterObject.pXFormColorSlider;
    data.xFormSymmetryREd = parameterObject.pXFormSymmetryREd;
    data.xFormSymmetrySlider = parameterObject.pXFormSymmetrySlider;
    data.xFormOpacityREd = parameterObject.pXFormOpacityREd;
    data.xFormOpacitySlider = parameterObject.pXFormOpacitySlider;
    data.xFormDrawModeCmb = parameterObject.pXFormDrawModeCmb;

    data.xFormAntialiasAmountREd = parameterObject.pXFormAntialiasAmountREd;
    data.xFormAntialiasAmountSlider = parameterObject.pXFormAntialiasAmountSlider;
    data.xFormAntialiasRadiusREd = parameterObject.pXFormAntialiasRadiusREd;
    data.xFormAntialiasRadiusSlider = parameterObject.pXFormAntialiasRadiusSlider;
    data.xFormAntialiasCopyToAllBtn = parameterObject.pXFormAntialiasCopyToAllBtn;

    data.relWeightsTable = parameterObject.pRelWeightsTable;
    data.relWeightsZeroButton = parameterObject.pRelWeightsZeroButton;
    data.relWeightsOneButton = parameterObject.pRelWeightsOneButton;
    data.relWeightREd = parameterObject.pRelWeightREd;

    data.transformationWeightREd = parameterObject.pTransformationWeightREd;

    data.mouseTransformEditTrianglesButton = parameterObject.pMouseTransformMoveButton;
    data.mouseTransformEditFocusPointButton = parameterObject.pMouseTransformScaleButton;
    data.mouseTransformEditPointsButton = parameterObject.pMouseTransformShearButton;
    data.mouseTransformEditViewButton = parameterObject.pMouseTransformViewButton;
    data.toggleVariationsButton = parameterObject.pToggleVariationsButton;
    data.toggleTransparencyButton = parameterObject.pToggleTransparencyButton;
    data.toggleDarkTrianglesButton = parameterObject.pToggleDarkTrianglesButton;
    mainProgressUpdater = parameterObject.pMainProgressUpdater;
    jobProgressUpdater = parameterObject.pJobProgressUpdater;
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
    data.faqPane = parameterObject.pFAQPane;

    data.undoButton = parameterObject.pUndoButton;
    data.redoButton = parameterObject.pRedoButton;

    qsaveFilenameGen = new QuickSaveFilenameGen(prefs);

    enableUndoControls();

    initHelpPane();
    initFAQPane();

    data.scriptTextArea = parameterObject.pScriptTextArea;

    refreshPaletteColorsTable();
    refreshRenderBatchJobsTable();

    initDefaultScript();

    initGradientLibrary();

    enableControls();
    enableShadingUI();
    enableDOFUI();
    enableDEFilterUI();

    enableXFormControls(null);

    refreshResolutionProfileCmb(data.resolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.interactiveResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.batchResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.swfAnimatorResolutionProfileCmb, null);
    if (data.swfAnimatorResolutionProfileCmb.getItemCount() > 0) {
      data.swfAnimatorResolutionProfileCmb.setSelectedIndex(0);
    }
    refreshQualityProfileCmb(data.qualityProfileCmb, null);
    refreshQualityProfileCmb(data.interactiveQualityProfileCmb, null);
    refreshQualityProfileCmb(data.batchQualityProfileCmb, null);
    refreshQualityProfileCmb(data.swfAnimatorQualityProfileCmb, null);
    if (data.swfAnimatorQualityProfileCmb.getItemCount() > 0) {
      data.swfAnimatorQualityProfileCmb.setSelectedIndex(0);
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

  private void initDefaultScript() {
    data.scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
        "import org.jwildfire.create.tina.base.XForm;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFunc;\r\n" +
        "import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;\r\n" +
        "\r\n" +
        "import org.jwildfire.create.tina.variation.BubbleFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.HemisphereFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.Julia3DFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.LinearFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.PreBlurFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.SpirographFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.SplitsFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.ZTranslateFunc;\r\n" +
        "\r\n" +
        "// Bases on the Soft Julian Script by AsaLegault\r\n" +
        "//  http://asalegault.deviantart.com/art/Cloud-Julian-Script-84635709\r\n" +
        "public void run(ScriptRunnerEnvironment pEnv) throws Exception {\r\n" +
        "  Flame currFlame = pEnv.getCurrFlame();\r\n" +
        "  if(currFlame==null) {\r\n" +
        "    throw new Exception(\"Please select a flame at first\");\r\n" +
        "  }\r\n" +
        "  // First transform\r\n" +
        "  {\r\n" +
        "    VariationFunc varFunc = new Julia3DFunc();\r\n" +
        "    varFunc.setParameter(\"power\", -2);\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.addVariation(1.0, varFunc);\r\n" +
        "    xForm.setWeight(2.0);\r\n" +
        "    xForm.setColor(0.0);\r\n" +
        "    xForm.setColorSymmetry(0.01);\r\n" +
        "    xForm.setCoeff20(0.3); //o0    \r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // Second transform\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.addVariation(0.1, new BubbleFunc());\r\n" +
        "    xForm.addVariation(1.0, new PreBlurFunc());\r\n" +
        "    VariationFunc varFunc=new SpirographFunc();\r\n" +
        "    varFunc.setParameter(\"a\", 7.0);\r\n" +
        "    varFunc.setParameter(\"b\", 5.0);\r\n" +
        "    varFunc.setParameter(\"d\", 0.0);\r\n" +
        "    varFunc.setParameter(\"c1\", 5.0);\r\n" +
        "    varFunc.setParameter(\"c2\", -5.0);\r\n" +
        "    varFunc.setParameter(\"tmin\", 1.0);\r\n" +
        "    varFunc.setParameter(\"tmax\", 50.0);\r\n" +
        "    varFunc.setParameter(\"ymin\", -1.0);\r\n" +
        "    varFunc.setParameter(\"ymax\", 0.1);\r\n" +
        "    xForm.addVariation(0.03, varFunc);\r\n" +
        "    xForm.setWeight(1.0);\r\n" +
        "    xForm.setColor(0.844);\r\n" +
        "    currFlame.getXForms().add(xForm);    \r\n" +
        "  }\r\n" +
        "  // Third transform\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.addVariation(0.18, new HemisphereFunc());\r\n" +
        "    xForm.addVariation(1.0, new PreBlurFunc());\r\n" +
        "    xForm.addVariation(-0.025, new ZTranslateFunc());\r\n" +
        "    xForm.setWeight(0.5);\r\n" +
        "    xForm.setColor(0.0);\r\n" +
        "    currFlame.getXForms().add(xForm);    \r\n" +
        "  }\r\n" +
        "  //A fourth transform can be very useful when trying\r\n" +
        "  //to fill in the bubbles....But i'll let you figure\r\n" +
        "  //that out.//\r\n" +
        "  // ...\r\n" +
        "  // Final settings   \r\n" +
        "  currFlame.setCamRoll(2.0);\r\n" +
        "  currFlame.setCamPitch(46.0);\r\n" +
        "  currFlame.setCamYaw(0.0);\r\n" +
        "  currFlame.setCamPerspective(0.30);\r\n" +
        "  currFlame.setPixelsPerUnit(96);\r\n" +
        "  // Refresh the UI\r\n" +
        "  pEnv.refreshUI();\r\n" +
        "}\r\n");
  }

  private boolean dragging = false;
  private boolean keypressing = false;

  private FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      int width = centerPanel.getWidth();
      int height = centerPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(img, 0, 0, centerPanel.getWidth(), this);
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
                break;
              // right
              case 39:
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
                break;
              // up
              case 38:
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
                break;
              // down
              case 40:
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
    refreshFlameImage(true, pMouseDown);
  }

  @Override
  public Flame getCurrFlame() {
    return currFlame;
  }

  public void refreshFlameImage(boolean pQuickRender, boolean pMouseDown) {
    FlamePanel imgPanel = getFlamePanel();
    Rectangle bounds = imgPanel.getImageBounds();
    int renderScale = pQuickRender && pMouseDown ? 2 : 1;
    int width = bounds.width / renderScale;
    int height = bounds.height / renderScale;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height);
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

          try {
            FlameRenderer renderer;
            if (imgPanel.getMouseDragOperation() == MouseDragOperation.FOCUS) {
              renderer = new DrawFocusPointFlameRenderer(flame, prefs, data.toggleTransparencyButton.isSelected());
            }
            else {
              renderer = new FlameRenderer(flame, prefs, data.toggleTransparencyButton.isSelected());
            }
            if (pQuickRender) {
              renderer.setProgressUpdater(null);
              flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
              flame.setSpatialFilterRadius(0.0);
            }
            else {
              renderer.setProgressUpdater(mainProgressUpdater);
              flame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
            }
            renderer.setRenderScale(renderScale);
            long t0 = System.currentTimeMillis();
            RenderedFlame res = renderer.renderFlame(info);
            long t1 = System.currentTimeMillis();
            imgPanel.setImage(res.getImage());
            showStatusMessage(flame, "render time: " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
          }
          catch (Throwable ex) {
            errorHandler.handleError(ex);
          }
        }
        finally {
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSampleDensity(oldSampleDensity);
        }
      }
      if (!pMouseDown) {
        for (int i = 0; i < randomBatch.size(); i++) {
          Flame bFlame = randomBatch.get(i).getFlame();
          if (bFlame == flame) {
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
    }
    centerPanel.getParent().validate();
    centerPanel.repaint();
  }

  @Override
  public void refreshUI() {
    noRefresh = true;
    try {
      refreshVisualCamValues();

      data.cameraPerspectiveREd.setText(Tools.doubleToString(currFlame.getCamPerspective()));
      data.cameraPerspectiveSlider.setValue(Tools.FTOI(currFlame.getCamPerspective() * SLIDER_SCALE_PERSPECTIVE));

      data.cameraZoomREd.setText(Tools.doubleToString(currFlame.getCamZoom()));
      data.cameraZoomSlider.setValue(Tools.FTOI(currFlame.getCamZoom() * SLIDER_SCALE_ZOOM));

      data.cameraDOFREd.setText(Tools.doubleToString(currFlame.getCamDOF()));
      data.cameraDOFSlider.setValue(Tools.FTOI(currFlame.getCamDOF() * SLIDER_SCALE_DOF));

      data.cameraDOFAreaREd.setText(Tools.doubleToString(currFlame.getCamDOFArea()));
      data.cameraDOFAreaSlider.setValue(Tools.FTOI(currFlame.getCamDOFArea() * SLIDER_SCALE_DOF_AREA));

      data.cameraDOFExponentREd.setText(Tools.doubleToString(currFlame.getCamDOFExponent()));
      data.cameraDOFExponentSlider.setValue(Tools.FTOI(currFlame.getCamDOFExponent() * SLIDER_SCALE_DOF_EXPONENT));

      data.camZREd.setText(Tools.doubleToString(currFlame.getCamZ()));
      data.camZSlider.setValue(Tools.FTOI(currFlame.getCamZ() * SLIDER_SCALE_ZPOS));

      data.newDOFCBx.setSelected(currFlame.isNewCamDOF());

      data.brightnessREd.setText(Tools.doubleToString(currFlame.getBrightness()));
      data.brightnessSlider.setValue(Tools.FTOI(currFlame.getBrightness() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.contrastREd.setText(Tools.doubleToString(currFlame.getContrast()));
      data.contrastSlider.setValue(Tools.FTOI(currFlame.getContrast() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.vibrancyREd.setText(Tools.doubleToString(currFlame.getVibrancy()));
      data.vibrancySlider.setValue(Tools.FTOI(currFlame.getVibrancy() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.gammaREd.setText(Tools.doubleToString(currFlame.getGamma()));
      data.gammaSlider.setValue(Tools.FTOI(currFlame.getGamma() * SLIDER_SCALE_GAMMA));

      data.filterRadiusREd.setText(Tools.doubleToString(currFlame.getSpatialFilterRadius()));
      data.filterRadiusSlider.setValue(Tools.FTOI(currFlame.getSpatialFilterRadius() * SLIDER_SCALE_FILTER_RADIUS));
      data.filterKernelCmb.setSelectedItem(currFlame.getSpatialFilterKernel());

      data.deFilterEnableCbx.setSelected(currFlame.isDeFilterEnabled());
      data.deFilterMaxRadiusREd.setText(Tools.doubleToString(currFlame.getDeFilterMaxRadius()));
      data.deFilterMaxRadiusSlider.setValue(Tools.FTOI(currFlame.getDeFilterMaxRadius() * SLIDER_SCALE_FILTER_RADIUS));
      data.deFilterMinRadiusREd.setText(Tools.doubleToString(currFlame.getDeFilterMinRadius()));
      data.deFilterMinRadiusSlider.setValue(Tools.FTOI(currFlame.getDeFilterMinRadius() * SLIDER_SCALE_FILTER_RADIUS));
      data.deFilterCurveREd.setText(Tools.doubleToString(currFlame.getDeFilterCurve()));
      data.deFilterCurveSlider.setValue(Tools.FTOI(currFlame.getDeFilterCurve() * SLIDER_SCALE_FILTER_RADIUS));
      data.deFilterKernelCmb.setSelectedItem(currFlame.getDeFilterKernel());

      data.gammaThresholdREd.setText(String.valueOf(currFlame.getGammaThreshold()));
      data.gammaThresholdSlider.setValue(Tools.FTOI(currFlame.getGammaThreshold() * SLIDER_SCALE_GAMMA_THRESHOLD));

      data.bgTransparencyCBx.setSelected(currFlame.isBGTransparency());

      data.bgColorRedREd.setText(String.valueOf(currFlame.getBGColorRed()));
      data.bgColorRedSlider.setValue(currFlame.getBGColorRed());

      data.bgColorGreenREd.setText(String.valueOf(currFlame.getBGColorGreen()));
      data.bgColorGreenSlider.setValue(currFlame.getBGColorGreen());

      data.bgColorBlueREd.setText(String.valueOf(currFlame.getBGColorBlue()));
      data.bgColorBlueSlider.setValue(currFlame.getBGColorBlue());

      data.affinePreserveZButton.setSelected(currFlame.isPreserveZ());

      gridRefreshing = true;
      try {
        refreshTransformationsTable();
      }
      finally {
        gridRefreshing = false;
      }
      transformationTableClicked();

      data.shadingLightCmb.setSelectedIndex(0);
      refreshShadingUI();
      enableShadingUI();
      enableDOFUI();
      enableDEFilterUI();
      //      refreshFlameImage();
      refreshPaletteUI(currFlame.getPalette());
    }
    finally {
      noRefresh = false;
    }
  }

  private void refreshShadingUI() {
    ShadingInfo shadingInfo = currFlame != null ? currFlame.getShadingInfo() : null;
    boolean pseudo3DEnabled;
    boolean blurEnabled;
    boolean distanceColorEnabled;
    if (shadingInfo != null) {
      data.shadingCmb.setSelectedItem(shadingInfo.getShading());
      pseudo3DEnabled = shadingInfo.getShading().equals(Shading.PSEUDO3D);
      blurEnabled = shadingInfo.getShading().equals(Shading.BLUR);
      distanceColorEnabled = shadingInfo.getShading().equals(Shading.DISTANCE_COLOR);
    }
    else {
      data.shadingCmb.setSelectedIndex(0);
      pseudo3DEnabled = false;
      blurEnabled = false;
      distanceColorEnabled = false;
    }
    if (pseudo3DEnabled) {
      data.shadingAmbientREd.setText(Tools.doubleToString(shadingInfo.getAmbient()));
      data.shadingAmbientSlider.setValue(Tools.FTOI(shadingInfo.getAmbient() * SLIDER_SCALE_AMBIENT));
      data.shadingDiffuseREd.setText(Tools.doubleToString(shadingInfo.getDiffuse()));
      data.shadingDiffuseSlider.setValue(Tools.FTOI(shadingInfo.getDiffuse() * SLIDER_SCALE_AMBIENT));
      data.shadingPhongREd.setText(Tools.doubleToString(shadingInfo.getPhong()));
      data.shadingPhongSlider.setValue(Tools.FTOI(shadingInfo.getPhong() * SLIDER_SCALE_AMBIENT));
      data.shadingPhongSizeREd.setText(Tools.doubleToString(shadingInfo.getPhongSize()));
      data.shadingPhongSizeSlider.setValue(Tools.FTOI(shadingInfo.getPhongSize() * SLIDER_SCALE_PHONGSIZE));
      int cIdx = data.shadingLightCmb.getSelectedIndex();
      data.shadingLightXREd.setText(Tools.doubleToString(shadingInfo.getLightPosX()[cIdx]));
      data.shadingLightXSlider.setValue(Tools.FTOI(shadingInfo.getLightPosX()[cIdx] * SLIDER_SCALE_LIGHTPOS));
      data.shadingLightYREd.setText(Tools.doubleToString(shadingInfo.getLightPosY()[cIdx]));
      data.shadingLightYSlider.setValue(Tools.FTOI(shadingInfo.getLightPosY()[cIdx] * SLIDER_SCALE_LIGHTPOS));
      data.shadingLightZREd.setText(Tools.doubleToString(shadingInfo.getLightPosZ()[cIdx]));
      data.shadingLightZSlider.setValue(Tools.FTOI(shadingInfo.getLightPosZ()[cIdx] * SLIDER_SCALE_LIGHTPOS));
      data.shadingLightRedREd.setText(String.valueOf(shadingInfo.getLightRed()[cIdx]));
      data.shadingLightRedSlider.setValue(shadingInfo.getLightRed()[cIdx]);
      data.shadingLightGreenREd.setText(String.valueOf(shadingInfo.getLightGreen()[cIdx]));
      data.shadingLightGreenSlider.setValue(shadingInfo.getLightGreen()[cIdx]);
      data.shadingLightBlueREd.setText(String.valueOf(shadingInfo.getLightBlue()[cIdx]));
      data.shadingLightBlueSlider.setValue(shadingInfo.getLightBlue()[cIdx]);
    }
    else {
      data.shadingAmbientREd.setText("");
      data.shadingAmbientSlider.setValue(0);
      data.shadingDiffuseREd.setText("");
      data.shadingDiffuseSlider.setValue(0);
      data.shadingPhongREd.setText("");
      data.shadingPhongSlider.setValue(0);
      data.shadingPhongSizeREd.setText("");
      data.shadingPhongSizeSlider.setValue(0);
      data.shadingLightXREd.setText("");
      data.shadingLightXSlider.setValue(0);
      data.shadingLightYREd.setText("");
      data.shadingLightYSlider.setValue(0);
      data.shadingLightZREd.setText("");
      data.shadingLightZSlider.setValue(0);
      data.shadingLightRedREd.setText("");
      data.shadingLightRedSlider.setValue(0);
      data.shadingLightGreenREd.setText("");
      data.shadingLightGreenSlider.setValue(0);
      data.shadingLightBlueREd.setText("");
      data.shadingLightBlueSlider.setValue(0);
    }
    if (blurEnabled) {
      data.shadingBlurRadiusREd.setText(Tools.doubleToString(shadingInfo.getBlurRadius()));
      data.shadingBlurRadiusSlider.setValue(shadingInfo.getBlurRadius());
      data.shadingBlurFadeREd.setText(Tools.doubleToString(shadingInfo.getBlurFade()));
      data.shadingBlurFadeSlider.setValue(Tools.FTOI(shadingInfo.getBlurFade() * SLIDER_SCALE_AMBIENT));
      data.shadingBlurFallOffREd.setText(Tools.doubleToString(shadingInfo.getBlurFallOff()));
      data.shadingBlurFallOffSlider.setValue(Tools.FTOI(shadingInfo.getBlurFallOff() * SLIDER_SCALE_BLUR_FALLOFF));
    }
    else {
      data.shadingBlurRadiusREd.setText("");
      data.shadingBlurRadiusSlider.setValue(0);
      data.shadingBlurFadeREd.setText("");
      data.shadingBlurFadeSlider.setValue(0);
      data.shadingBlurFallOffREd.setText("");
      data.shadingBlurFallOffSlider.setValue(0);
    }
    if (distanceColorEnabled) {
      data.shadingDistanceColorRadiusREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorRadius()));
      data.shadingDistanceColorRadiusSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorRadius() * SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorExponentREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorExponent()));
      data.shadingDistanceColorExponentSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorExponent() * SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorScaleREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorScale()));
      data.shadingDistanceColorScaleSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorScale() * SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorOffsetXREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorOffsetX()));
      data.shadingDistanceColorOffsetXSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorOffsetX() * SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorOffsetYREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorOffsetY()));
      data.shadingDistanceColorOffsetYSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorOffsetY() * SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorOffsetZREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorOffsetZ()));
      data.shadingDistanceColorOffsetZSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorOffsetZ() * SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorStyleREd.setText(String.valueOf(shadingInfo.getDistanceColorStyle()));
      data.shadingDistanceColorStyleSlider.setValue(shadingInfo.getDistanceColorStyle());
      data.shadingDistanceColorCoordinateREd.setText(String.valueOf(shadingInfo.getDistanceColorCoordinate()));
      data.shadingDistanceColorCoordinateSlider.setValue(shadingInfo.getDistanceColorCoordinate());
      data.shadingDistanceColorShiftREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorShift()));
      data.shadingDistanceColorShiftSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorShift() * SLIDER_SCALE_AMBIENT));
    }
    else {
      data.shadingDistanceColorRadiusREd.setText("");
      data.shadingDistanceColorRadiusSlider.setValue(0);
      data.shadingDistanceColorExponentREd.setText("");
      data.shadingDistanceColorExponentSlider.setValue(0);
      data.shadingDistanceColorScaleREd.setText("");
      data.shadingDistanceColorScaleSlider.setValue(0);
      data.shadingDistanceColorOffsetXREd.setText("");
      data.shadingDistanceColorOffsetXSlider.setValue(0);
      data.shadingDistanceColorOffsetYREd.setText("");
      data.shadingDistanceColorOffsetYSlider.setValue(0);
      data.shadingDistanceColorOffsetZREd.setText("");
      data.shadingDistanceColorOffsetZSlider.setValue(0);
      data.shadingDistanceColorStyleREd.setText("");
      data.shadingDistanceColorStyleSlider.setValue(0);
      data.shadingDistanceColorCoordinateREd.setText("");
      data.shadingDistanceColorCoordinateSlider.setValue(0);
      data.shadingDistanceColorShiftREd.setText("");
      data.shadingDistanceColorShiftSlider.setValue(0);
    }
  }

  private void enableDOFUI() {
    boolean newDOF = currFlame != null ? currFlame.isNewCamDOF() : false;
    data.mouseTransformEditFocusPointButton.setEnabled(newDOF);
    data.focusXREd.setEnabled(newDOF);
    data.focusXSlider.setEnabled(newDOF);
    data.focusYREd.setEnabled(newDOF);
    data.focusYSlider.setEnabled(newDOF);
    data.focusZREd.setEnabled(newDOF);
    data.focusZSlider.setEnabled(newDOF);
    data.cameraDOFAreaREd.setEnabled(newDOF);
    data.cameraDOFAreaSlider.setEnabled(newDOF);
    data.cameraDOFExponentREd.setEnabled(newDOF);
    data.cameraDOFExponentSlider.setEnabled(newDOF);
    data.camZREd.setEnabled(currFlame != null);
    data.camZSlider.setEnabled(currFlame != null);
    data.dimishZREd.setEnabled(currFlame != null);
    data.dimishZSlider.setEnabled(currFlame != null);
  }

  private void enableDEFilterUI() {
    boolean deEnabled = currFlame != null ? currFlame.isDeFilterEnabled() : false;
    data.deFilterMaxRadiusREd.setEnabled(deEnabled);
    data.deFilterMaxRadiusSlider.setEnabled(deEnabled);
    data.deFilterMinRadiusREd.setEnabled(deEnabled);
    data.deFilterMinRadiusSlider.setEnabled(deEnabled);
    data.deFilterCurveREd.setEnabled(deEnabled);
    data.deFilterCurveSlider.setEnabled(deEnabled);
    data.deFilterKernelCmb.setEnabled(deEnabled);
  }

  private void enableShadingUI() {
    ShadingInfo shadingInfo = currFlame != null ? currFlame.getShadingInfo() : null;
    boolean pseudo3DEnabled;
    boolean blurEnabled;
    boolean distanceColorEnabled;
    if (shadingInfo != null) {
      data.shadingCmb.setEnabled(true);
      pseudo3DEnabled = shadingInfo.getShading().equals(Shading.PSEUDO3D);
      blurEnabled = shadingInfo.getShading().equals(Shading.BLUR);
      distanceColorEnabled = shadingInfo.getShading().equals(Shading.DISTANCE_COLOR);
    }
    else {
      data.shadingCmb.setEnabled(false);
      pseudo3DEnabled = false;
      blurEnabled = false;
      distanceColorEnabled = false;
    }
    // pseudo3d
    data.shadingAmbientREd.setEnabled(pseudo3DEnabled);
    data.shadingAmbientSlider.setEnabled(pseudo3DEnabled);
    data.shadingDiffuseREd.setEnabled(pseudo3DEnabled);
    data.shadingDiffuseSlider.setEnabled(pseudo3DEnabled);
    data.shadingPhongREd.setEnabled(pseudo3DEnabled);
    data.shadingPhongSlider.setEnabled(pseudo3DEnabled);
    data.shadingPhongSizeREd.setEnabled(pseudo3DEnabled);
    data.shadingPhongSizeSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightCmb.setEnabled(pseudo3DEnabled);
    data.shadingLightXREd.setEnabled(pseudo3DEnabled);
    data.shadingLightXSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightYREd.setEnabled(pseudo3DEnabled);
    data.shadingLightYSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightZREd.setEnabled(pseudo3DEnabled);
    data.shadingLightZSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightRedREd.setEnabled(pseudo3DEnabled);
    data.shadingLightRedSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightGreenREd.setEnabled(pseudo3DEnabled);
    data.shadingLightGreenSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightBlueREd.setEnabled(pseudo3DEnabled);
    data.shadingLightBlueSlider.setEnabled(pseudo3DEnabled);
    // blur
    data.shadingBlurRadiusREd.setEnabled(blurEnabled);
    data.shadingBlurRadiusSlider.setEnabled(blurEnabled);
    data.shadingBlurFadeREd.setEnabled(blurEnabled);
    data.shadingBlurFadeSlider.setEnabled(blurEnabled);
    data.shadingBlurFallOffREd.setEnabled(blurEnabled);
    data.shadingBlurFallOffSlider.setEnabled(blurEnabled);
    // distanceColor
    data.shadingDistanceColorRadiusREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorRadiusSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorScaleREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorScaleSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorExponentREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorExponentSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetXREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetXSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetYREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetYSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetZREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetZSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorStyleREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorStyleSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorCoordinateREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorCoordinateSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorShiftREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorShiftSlider.setEnabled(distanceColorEnabled);
  }

  private void refreshTransformationsTable() {
    final int COL_TRANSFORM = 0;
    final int COL_VARIATIONS = 1;
    final int COL_WEIGHT = 2;
    data.transformationsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return currFlame != null ? currFlame.getXForms().size() + currFlame.getFinalXForms().size() : 0;
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
        if (currFlame != null) {
          XForm xForm = rowIndex < currFlame.getXForms().size() ? currFlame.getXForms().get(rowIndex) : currFlame.getFinalXForms().get(rowIndex - currFlame.getXForms().size());
          switch (columnIndex) {
            case COL_TRANSFORM:
              return rowIndex < currFlame.getXForms().size() ? String.valueOf(rowIndex + 1) : "Final";
            case COL_VARIATIONS:
            {
              String hs = "";
              if (xForm.getVariationCount() > 0) {
                for (int i = 0; i < xForm.getVariationCount() - 1; i++) {
                  hs += xForm.getVariation(i).getFunc().getName() + ", ";
                }
                hs += xForm.getVariation(xForm.getVariationCount() - 1).getFunc().getName();
              }
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
        if (currFlame != null && column == COL_WEIGHT && row < currFlame.getXForms().size()) {
          XForm xForm = currFlame.getXForms().get(row);
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
        if (currFlame != null && data.paletteKeyFrames != null && data.paletteKeyFrames.size() > row) {
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
          RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(data.paletteKeyFrames);
          saveUndoPoint();
          currFlame.setPalette(palette);
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

  private void refreshRelWeightsTable() {
    final int COL_TRANSFORM = 0;
    final int COL_WEIGHT = 1;
    data.relWeightsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        XForm xForm = getCurrXForm();
        return xForm != null && currFlame.getFinalXForms().indexOf(xForm) < 0 ? currFlame.getXForms().size() : 0;
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
        if (currFlame != null && column == COL_WEIGHT && xForm != null) {
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

  private void refreshPaletteUI(RGBPalette pPalette) {
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
    if (currFlame != null) {
      ImagePanel panels[] = { getPalettePanel(), getColorChooserPalettePanel() };
      for (ImagePanel imgPanel : panels) {
        int width = imgPanel.getWidth();
        int height = imgPanel.getHeight();
        if (width >= 16 && height >= 4) {
          SimpleImage img = new RGBPaletteRenderer().renderHorizPalette(currFlame.getPalette(), width, height);
          imgPanel.setImage(img);
        }
        imgPanel.getParent().validate();
        imgPanel.repaint();
      }
    }
  }

  private void flameSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
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
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void paletteSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
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
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void shadingInfoTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, int pIdx) {
    if (noRefresh || currFlame == null)
      return;
    ShadingInfo shadingInfo = currFlame.getShadingInfo();
    noRefresh = true;
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = shadingInfo.getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(shadingInfo, propValue);
        }
        else if (fieldCls == double[].class) {
          double[] arr = (double[]) field.get(shadingInfo);
          Array.set(arr, pIdx, propValue);
        }
        else if (fieldCls == Double[].class) {
          Double[] arr = (Double[]) field.get(shadingInfo);
          Array.set(arr, pIdx, propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(shadingInfo, Tools.FTOI(propValue));
        }
        else if (fieldCls == int[].class) {
          int[] arr = (int[]) field.get(shadingInfo);
          Array.set(arr, pIdx, Tools.FTOI(propValue));
        }
        else if (fieldCls == Integer[].class) {
          Integer[] arr = (Integer[]) field.get(shadingInfo);
          Array.set(arr, pIdx, Tools.FTOI(propValue));
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

  private void shadingInfoSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, int pIdx) {
    if (noRefresh || currFlame == null)
      return;
    ShadingInfo shadingInfo = currFlame.getShadingInfo();
    noRefresh = true;
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = shadingInfo.getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(shadingInfo, propValue);
        }
        else if (fieldCls == double[].class) {
          double[] arr = (double[]) field.get(shadingInfo);
          Array.set(arr, pIdx, propValue);
        }
        else if (fieldCls == Double[].class) {
          Double[] arr = (Double[]) field.get(shadingInfo);
          Array.set(arr, pIdx, propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(shadingInfo, Tools.FTOI(propValue));
        }
        else if (fieldCls == int[].class) {
          int[] arr = (int[]) field.get(shadingInfo);
          Array.set(arr, pIdx, Tools.FTOI(propValue));
        }
        else if (fieldCls == Integer[].class) {
          Integer[] arr = (Integer[]) field.get(shadingInfo);
          Array.set(arr, pIdx, Tools.FTOI(propValue));
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
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void flameTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh) {
      return;
    }
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
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  private void paletteTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
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

  public void cameraRollSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraRollSlider, data.cameraRollREd, "camRoll", 1.0);
  }

  public void cameraRollREd_changed() {
    flameTextFieldChanged(data.cameraRollSlider, data.cameraRollREd, "camRoll", 1.0);
  }

  public void cameraPitchREd_changed() {
    flameTextFieldChanged(data.cameraPitchSlider, data.cameraPitchREd, "camPitch", 1.0);
  }

  public void cameraPitchSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraPitchSlider, data.cameraPitchREd, "camPitch", 1.0);
  }

  public void cameraYawREd_changed() {
    flameTextFieldChanged(data.cameraYawSlider, data.cameraYawREd, "camYaw", 1.0);
  }

  public void cameraPerspectiveREd_changed() {
    flameTextFieldChanged(data.cameraPerspectiveSlider, data.cameraPerspectiveREd, "camPerspective", SLIDER_SCALE_PERSPECTIVE);
  }

  public void cameraYawSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraYawSlider, data.cameraYawREd, "camYaw", 1.0);
  }

  public void cameraPerspectiveSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraPerspectiveSlider, data.cameraPerspectiveREd, "camPerspective", SLIDER_SCALE_PERSPECTIVE);
  }

  public void renderFlameButton_actionPerformed(ActionEvent e) {
    refreshFlameImage(false, false);
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
        List<Flame> flames = new Flam3Reader(prefs).readFlames(file.getAbsolutePath());
        Flame flame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        currFlame = flame;
        currFlame.setLastFilename(file.getName());
        undoManager.initUndoStack(currFlame);

        for (int i = flames.size() - 1; i >= 0; i--) {
          randomBatch.add(0, new FlameThumbnail(flames.get(i), null));
        }
        updateThumbnails();
        setupProfiles(currFlame);
        refreshUI();
        showStatusMessage(currFlame, "opened from disc");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void cameraCentreYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraCentreYSlider, data.cameraCentreYREd, "centreY", SLIDER_SCALE_CENTRE);
  }

  public void cameraCentreYREd_changed() {
    flameTextFieldChanged(data.cameraCentreYSlider, data.cameraCentreYREd, "centreY", SLIDER_SCALE_CENTRE);
  }

  public void cameraCentreXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraCentreXSlider, data.cameraCentreXREd, "centreX", SLIDER_SCALE_CENTRE);
  }

  public void cameraZoomSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraZoomSlider, data.cameraZoomREd, "camZoom", SLIDER_SCALE_ZOOM);
  }

  public void cameraCentreXREd_changed() {
    flameTextFieldChanged(data.cameraCentreXSlider, data.cameraCentreXREd, "centreX", SLIDER_SCALE_CENTRE);
  }

  public void cameraZoomREd_changed() {
    flameTextFieldChanged(data.cameraZoomSlider, data.cameraZoomREd, "camZoom", SLIDER_SCALE_ZOOM);
  }

  public void brightnessSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.brightnessSlider, data.brightnessREd, "brightness", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void filterRadiusREd_changed() {
    flameTextFieldChanged(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterMaxRadiusREd_changed() {
    flameTextFieldChanged(data.deFilterMaxRadiusSlider, data.deFilterMaxRadiusREd, "deFilterMaxRadius", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterMinRadiusREd_changed() {
    flameTextFieldChanged(data.deFilterMinRadiusSlider, data.deFilterMinRadiusREd, "deFilterMinRadius", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterCurveREd_changed() {
    flameTextFieldChanged(data.deFilterCurveSlider, data.deFilterCurveREd, "deFilterCurve", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void bgColorGreenSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.bgColorGreenSlider, data.bgColorGreenREd, "bgColorGreen", 1.0);
  }

  public void gammaREd_changed() {
    flameTextFieldChanged(data.gammaSlider, data.gammaREd, "gamma", SLIDER_SCALE_GAMMA);
  }

  public void bgColorRedSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.bgColorRedSlider, data.bgColorRedREd, "bgColorRed", 1.0);
  }

  public void bgColorBlueSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.bgColorBlueSlider, data.bgColorBlueREd, "bgColorBlue", 1.0);
  }

  public void gammaThresholdREd_changed() {
    flameTextFieldChanged(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void contrastREd_changed() {
    flameTextFieldChanged(data.contrastSlider, data.contrastREd, "contrast", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void vibrancySlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void filterRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterMaxRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.deFilterMaxRadiusSlider, data.deFilterMaxRadiusREd, "deFilterMaxRadius", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterMinRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.deFilterMinRadiusSlider, data.deFilterMinRadiusREd, "deFilterMinRadius", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterCurveSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.deFilterCurveSlider, data.deFilterCurveREd, "deFilterCurve", SLIDER_SCALE_FILTER_RADIUS);
  }

  public void gammaThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void vibrancyREd_changed() {
    flameTextFieldChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void pixelsPerUnitSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.pixelsPerUnitSlider, data.pixelsPerUnitREd, "pixelsPerUnit", 1.0);
  }

  public void bgColorGreenREd_changed() {
    flameTextFieldChanged(data.bgColorGreenSlider, data.bgColorGreenREd, "bgColorGreen", 1.0);
  }

  public void gammaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaSlider, data.gammaREd, "gamma", SLIDER_SCALE_GAMMA);
  }

  public void bgColorRedREd_changed() {
    flameTextFieldChanged(data.bgColorRedSlider, data.bgColorRedREd, "bgColorRed", 1.0);
  }

  public void bgBGColorBlueREd_changed() {
    flameTextFieldChanged(data.bgColorBlueSlider, data.bgColorBlueREd, "bgColorBlue", 1.0);
  }

  public void pixelsPerUnitREd_changed() {
    flameTextFieldChanged(data.pixelsPerUnitSlider, data.pixelsPerUnitREd, "pixelsPerUnit", 1.0);
  }

  public void brightnessREd_changed() {
    flameTextFieldChanged(data.brightnessSlider, data.brightnessREd, "brightness", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void contrastSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.contrastSlider, data.contrastREd, "contrast", SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void randomPaletteButton_actionPerformed(ActionEvent e) {
    if (currFlame != null) {
      RandomRGBPaletteGenerator generator = new RandomRGBPaletteGenerator();
      data.paletteKeyFrames = generator.generateKeyFrames(Integer.parseInt(data.paletteRandomPointsREd.getText()));
      refreshPaletteColorsTable();
      RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(data.paletteKeyFrames);
      saveUndoPoint();
      currFlame.setPalette(palette);
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
      List<Flame> flames = new Flam3Reader(prefs).readFlames(file.getAbsolutePath());
      Flame flame = flames.get(0);
      prefs.setLastInputFlameFile(file);
      RGBPalette palette = flame.getPalette();
      data.paletteKeyFrames = null;
      saveUndoPoint();
      currFlame.setPalette(palette);
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
    paletteTextFieldChanged(data.paletteShiftSlider, data.paletteShiftREd, "modShift", 1.0);
  }

  public void paletteHueSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteHueSlider, data.paletteHueREd, "modHue", 1.0);
  }

  public void paletteShiftSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(data.paletteShiftSlider, data.paletteShiftREd, "modShift", 1.0);
  }

  public void saveFlameButton_actionPerformed(ActionEvent e) {
    try {
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
        if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          new Flam3Writer().writeFlame(currFlame, file.getAbsolutePath());
          currFlame.setLastFilename(file.getName());
          showStatusMessage(currFlame, "flame saved to disc");
          prefs.setLastOutputFlameFile(file);
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public XForm getCurrXForm() {
    XForm xForm = null;
    if (currFlame != null) {
      int row = data.transformationsTable.getSelectedRow();
      if (row >= 0 && row < currFlame.getXForms().size()) {
        xForm = currFlame.getXForms().get(row);
      }
      else if (row >= currFlame.getXForms().size() && row < (currFlame.getXForms().size() + currFlame.getFinalXForms().size())) {
        xForm = currFlame.getFinalXForms().get(row - currFlame.getXForms().size());
      }
    }
    return xForm;
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
        enableXFormControls(xForm);
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
        int selRow = data.relWeightsTable.getSelectedRow();
        XForm xForm = getCurrXForm();
        if (xForm != null && selRow >= 0) {
          data.relWeightREd.setText(Tools.doubleToString(xForm.getModifiedWeights()[selRow]));

        }
        else {
          data.relWeightREd.setText("");
        }
        enableRelWeightsControls();

      }
      finally {
        cmbRefreshing = oldCmbRefreshing;
        gridRefreshing = oldGridRefreshing;
      }
    }
  }

  private void enableControls() {
    enableJobRenderControls();
  }

  private void enableJobRenderControls() {
    boolean idle = jobRenderThread == null;
    data.batchRenderAddFilesButton.setEnabled(idle);
    data.batchRenderFilesMoveDownButton.setEnabled(idle);
    data.batchRenderFilesMoveUpButton.setEnabled(idle);
    data.batchRenderFilesRemoveButton.setEnabled(idle);
    data.batchRenderFilesRemoveAllButton.setEnabled(idle);
    data.batchRenderStartButton.setText(idle ? "Render" : "Stop");
    data.batchRenderStartButton.invalidate();
    data.batchRenderStartButton.validate();
    rootTabbedPane.setEnabled(idle);
  }

  private void enableXFormControls(XForm xForm) {
    boolean enabled = xForm != null;

    data.affineRotateAmountREd.setEnabled(enabled);
    data.affineScaleAmountREd.setEnabled(enabled);
    data.affineMoveAmountREd.setEnabled(enabled);
    data.affineRotateLeftButton.setEnabled(enabled);
    data.affineRotateRightButton.setEnabled(enabled);
    data.affineEnlargeButton.setEnabled(enabled);
    data.affineShrinkButton.setEnabled(enabled);
    data.affineMoveUpButton.setEnabled(enabled);
    data.affineMoveLeftButton.setEnabled(enabled);
    data.affineMoveRightButton.setEnabled(enabled);
    data.affineMoveDownButton.setEnabled(enabled);
    data.affineFlipHorizontalButton.setEnabled(enabled);
    data.affineFlipVerticalButton.setEnabled(enabled);

    data.addTransformationButton.setEnabled(currFlame != null);
    data.addLinkedTransformationButton.setEnabled(enabled && currFlame != null && currFlame.getXForms().indexOf(xForm) >= 0);
    data.duplicateTransformationButton.setEnabled(enabled);
    data.deleteTransformationButton.setEnabled(enabled);
    data.addFinalTransformationButton.setEnabled(currFlame != null);

    data.affineEditPostTransformButton.setEnabled(currFlame != null);
    data.affineEditPostTransformSmallButton.setEnabled(currFlame != null);
    data.mouseEditZoomInButton.setEnabled(currFlame != null);
    data.mouseEditZoomOutButton.setEnabled(currFlame != null);
    data.toggleVariationsButton.setEnabled(currFlame != null);

    data.affineC00REd.setEditable(enabled);
    data.affineC01REd.setEditable(enabled);
    data.affineC10REd.setEditable(enabled);
    data.affineC11REd.setEditable(enabled);
    data.affineC20REd.setEditable(enabled);
    data.affineC21REd.setEditable(enabled);
    data.affineResetTransformButton.setEnabled(enabled);

    data.transformationWeightREd.setEnabled(enabled);

    for (TinaNonlinearControlsRow rows : data.TinaNonlinearControlsRows) {
      rows.getNonlinearVarCmb().setEnabled(enabled);
      rows.getNonlinearVarREd().setEnabled(enabled);
      rows.getNonlinearParamsCmb().setEnabled(enabled);
      rows.getNonlinearParamsREd().setEnabled(enabled);
      // refreshing occurs in refreshXFormUI():
      // rows.getNonlinearParamsLeftButton().setEnabled(enabled);
      // rows.getNonlinearParamsRightButton().setEnabled(enabled);
    }
    data.xFormColorREd.setEnabled(enabled);
    data.xFormColorSlider.setEnabled(enabled);
    data.xFormSymmetryREd.setEnabled(enabled);
    data.xFormSymmetrySlider.setEnabled(enabled);
    data.xFormOpacityREd.setEnabled(enabled && xForm.getDrawMode() == DrawMode.OPAQUE);
    data.xFormOpacitySlider.setEnabled(data.xFormOpacityREd.isEnabled());
    data.xFormDrawModeCmb.setEnabled(enabled);

    data.xFormAntialiasAmountREd.setEnabled(enabled);
    data.xFormAntialiasAmountSlider.setEnabled(enabled);
    data.xFormAntialiasRadiusREd.setEnabled(enabled);
    data.xFormAntialiasRadiusSlider.setEnabled(enabled);
    data.xFormAntialiasCopyToAllBtn.setEnabled(enabled);

    data.relWeightsTable.setEnabled(enabled);
    enableRelWeightsControls();
  }

  public void enableRelWeightsControls() {
    int selRow = data.relWeightsTable.getSelectedRow();
    boolean enabled = data.relWeightsTable.isEnabled() && selRow >= 0 && getCurrXForm() != null;
    data.relWeightsZeroButton.setEnabled(enabled);
    data.relWeightsOneButton.setEnabled(enabled);
    data.relWeightREd.setEnabled(enabled);
  }

  private void refreshXFormUI(XForm pXForm) {
    boolean oldRefreshing = refreshing;
    boolean oldGridRefreshing = gridRefreshing;
    boolean oldCmbRefreshing = cmbRefreshing;
    boolean oldNoRefresh = noRefresh;
    gridRefreshing = cmbRefreshing = refreshing = noRefresh = true;
    try {

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
        data.xFormOpacityREd.setText(Tools.doubleToString(pXForm.getOpacity()));
        data.xFormOpacitySlider.setValue(Tools.FTOI(pXForm.getOpacity() * SLIDER_SCALE_COLOR));
        data.xFormDrawModeCmb.setSelectedItem(pXForm.getDrawMode());

        data.xFormAntialiasAmountREd.setText(Tools.doubleToString(pXForm.getAntialiasAmount()));
        data.xFormAntialiasAmountSlider.setValue(Tools.FTOI(pXForm.getAntialiasAmount() * SLIDER_SCALE_COLOR));
        data.xFormAntialiasRadiusREd.setText(Tools.doubleToString(pXForm.getAntialiasRadius()));
        data.xFormAntialiasRadiusSlider.setValue(Tools.FTOI(pXForm.getAntialiasRadius() * SLIDER_SCALE_COLOR));

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
        data.xFormOpacityREd.setText(null);
        data.xFormOpacitySlider.setValue(0);
        data.xFormAntialiasAmountREd.setText(null);
        data.xFormAntialiasAmountSlider.setValue(0);
        data.xFormAntialiasRadiusREd.setText(null);
        data.xFormAntialiasRadiusSlider.setValue(0);
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
    if (row < 0 || row >= currFlame.getXForms().size()) {
      return;
    }
    saveUndoPoint();
    addXForm();
    int fromId = row;
    int toId = currFlame.getXForms().size() - 1;
    for (int i = 0; i < currFlame.getXForms().size(); i++) {
      XForm xForm = currFlame.getXForms().get(i);
      if (i == fromId) {
        for (int j = 0; j < currFlame.getXForms().size(); j++) {
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
    xForm.setAntialiasAmount(prefs.getTinaDefaultAntialiasingAmount());
    xForm.setAntialiasRadius(prefs.getTinaDefaultAntialiasingRadius());
    saveUndoPoint();
    currFlame.getXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = currFlame.getXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(false);
  }

  public void duplicateXForm() {
    XForm xForm = new XForm();
    xForm.assign(getCurrXForm());
    saveUndoPoint();
    currFlame.getXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = currFlame.getXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(false);
  }

  public void deleteXForm() {
    int row = data.transformationsTable.getSelectedRow();
    saveUndoPoint();
    if (row >= currFlame.getXForms().size()) {
      currFlame.getFinalXForms().remove(row - currFlame.getXForms().size());
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
    refreshFlameImage(false);
  }

  public void addFinalXForm() {
    XForm xForm = new XForm();
    xForm.addVariation(1.0, new Linear3DFunc());
    xForm.setAntialiasAmount(prefs.getTinaDefaultAntialiasingAmount());
    xForm.setAntialiasRadius(prefs.getTinaDefaultAntialiasingRadius());
    saveUndoPoint();
    currFlame.getFinalXForms().add(xForm);
    gridRefreshing = true;
    try {
      refreshTransformationsTable();
    }
    finally {
      gridRefreshing = false;
    }
    int row = currFlame.getXForms().size() + currFlame.getFinalXForms().size() - 1;
    data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(false);
  }

  private void forceTriangleMode() {
    if (flamePanel.getMouseDragOperation() != MouseDragOperation.TRIANGLE) {
      flamePanel.setMouseDragOperation(MouseDragOperation.TRIANGLE);
      flamePanel.setDrawTriangles(true);
      data.mouseTransformEditTrianglesButton.setSelected(true);
      data.mouseTransformEditPointsButton.setSelected(false);
      data.mouseTransformEditViewButton.setSelected(false);
      data.mouseTransformEditFocusPointButton.setSelected(false);
    }
  }

  public void xForm_moveRight(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveAmountREd.getText()) * pScale;
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
    double amount = Tools.stringToDouble(data.affineMoveAmountREd.getText()) * pScale;
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
    double amount = Tools.stringToDouble(data.affineMoveAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), 0, -amount, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_moveDown(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(data.affineMoveAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), 0, amount, data.affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  private class FlameThumbnail {
    private Flame flame;
    private SimpleImage preview;
    private ImagePanel imgPanel;

    public FlameThumbnail(Flame pFlame, SimpleImage pPreview) {
      flame = pFlame;
      preview = pPreview;
    }

    private void generatePreview(int pQuality) {
      RenderInfo info = new RenderInfo(IMG_WIDTH, IMG_HEIGHT);
      Flame renderFlame = flame.makeCopy();
      double wScl = (double) info.getImageWidth() / (double) renderFlame.getWidth();
      double hScl = (double) info.getImageHeight() / (double) renderFlame.getHeight();
      renderFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * renderFlame.getPixelsPerUnit());
      renderFlame.setWidth(IMG_WIDTH);
      renderFlame.setHeight(IMG_HEIGHT);
      renderFlame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
      renderFlame.setDeFilterEnabled(false);
      renderFlame.setSpatialFilterRadius(0.0);
      for (XForm xForm : renderFlame.getXForms()) {
        xForm.setAntialiasAmount(0.0);
      }
      for (XForm xForm : renderFlame.getFinalXForms()) {
        xForm.setAntialiasAmount(0.0);
      }
      FlameRenderer renderer = new FlameRenderer(renderFlame, prefs, false);
      renderFlame.setSampleDensity(pQuality);
      RenderedFlame res = renderer.renderFlame(info);
      preview = res.getImage();
    }

    public SimpleImage getPreview(int pQuality) {
      if (preview == null) {
        generatePreview(pQuality);
      }
      return preview;
    }

    public Flame getFlame() {
      return flame;
    }

    public ImagePanel getImgPanel() {
      return imgPanel;
    }

    public void setImgPanel(ImagePanel imgPanel) {
      this.imgPanel = imgPanel;
    }
  }

  private List<FlameThumbnail> randomBatch = new ArrayList<FlameThumbnail>();

  private final int IMG_WIDTH = 80;
  private final int IMG_HEIGHT = 60;
  private final int BORDER_SIZE = 10;

  public void updateThumbnails() {
    if (data.randomBatchScrollPane != null) {
      data.randomBatchPanel.remove(data.randomBatchScrollPane);
      data.randomBatchScrollPane = null;
    }
    int panelWidth = (IMG_WIDTH + BORDER_SIZE) * randomBatch.size();
    int panelHeight = IMG_HEIGHT + 2 * BORDER_SIZE;
    JPanel batchPanel = new JPanel();
    batchPanel.setLayout(null);
    batchPanel.setSize(panelWidth, panelHeight);
    batchPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    for (int i = 0; i < randomBatch.size(); i++) {
      SimpleImage img = randomBatch.get(i).getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
      // add it to the main panel
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(i * IMG_WIDTH + (i + 1) * BORDER_SIZE, BORDER_SIZE);
      randomBatch.get(i).setImgPanel(imgPanel);
      final int idx = i;
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
    data.randomBatchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    data.randomBatchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

    data.randomBatchPanel.add(data.randomBatchScrollPane, BorderLayout.CENTER);
    data.randomBatchPanel.validate();
  }

  public void createRandomBatch(int pCount, String pGeneratorname) {
    randomBatch.clear();
    int imgCount = prefs.getTinaRandomBatchSize();
    List<SimpleImage> imgList = new ArrayList<SimpleImage>();
    int maxCount = (pCount > 0 ? pCount : imgCount);
    mainProgressUpdater.initProgress(maxCount);
    RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance(pGeneratorname, true);
    //int palettePoints = Integer.parseInt(paletteRandomPointsREd.getText());
    int palettePoints = 11 + (int) (Math.random() * 47.0);
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, palettePoints);
    for (int i = 0; i < maxCount; i++) {
      RandomFlameGeneratorSample sample = sampler.createSample();
      FlameThumbnail thumbnail;
      thumbnail = new FlameThumbnail(sample.getFlame(), sample.getImage());
      randomBatch.add(thumbnail);
      imgList.add(sample.getImage());
      // add it to the main panel
      SimpleImage img = imgList.get(imgList.size() - 1);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(i * IMG_WIDTH + (i + 1) * BORDER_SIZE, BORDER_SIZE);
      thumbnail.setImgPanel(imgPanel);
      final int idx = i;
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
            importFromRandomBatch(idx);
          }
        }
      });
      mainProgressUpdater.updateProgress(i + 1);
    }
    updateThumbnails();
  }

  public void importFromRandomBatch(int pIdx) {
    if (pIdx >= 0 && pIdx < randomBatch.size()) {
      currFlame = randomBatch.get(pIdx).getFlame();
      undoManager.initUndoStack(currFlame);
      {
        FlamePanel imgPanel = getFlamePanel();
        Rectangle bounds = imgPanel.getImageBounds();
        double wScl = (double) bounds.width / (double) currFlame.getWidth();
        double hScl = (double) bounds.height / (double) currFlame.getHeight();
        currFlame.setWidth(bounds.width);
        currFlame.setHeight(bounds.height);
        currFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * currFlame.getPixelsPerUnit());
      }
      refreshUI();
      data.transformationsTable.getSelectionModel().setSelectionInterval(0, 0);
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
          Variation var = xForm.getVariation(pIdx);
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
            String rName = var.getFunc().getRessourceNames()[idx];
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
                JFileChooser chooser = new ImageFileChooser();
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
              default: {
                RessourceDialog dlg = new RessourceDialog(SwingUtilities.getWindowAncestor(centerPanel));
                dlg.setRessourceName(rName);
                byte val[] = var.getFunc().getRessourceValues()[idx];
                if (val != null) {
                  dlg.setRessourceValue(new String(val));
                }
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
        enableXFormControls(xForm);
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

  public void xFormAntialiasAmountSlider_changed() {
    xFormSliderChanged(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasRadiusSlider_changed() {
    xFormSliderChanged(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasAmountREd_changed() {
    xFormTextFieldChanged(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasRadiusREd_changed() {
    xFormTextFieldChanged(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", SLIDER_SCALE_COLOR);
  }

  private void setRelWeight(double pValue) {
    XForm xForm = getCurrXForm();
    if (xForm != null && currFlame != null && currFlame.getFinalXForms().indexOf(xForm) < 0) {
      int row = data.relWeightsTable.getSelectedRow();
      if (row >= 0 && row < currFlame.getXForms().size()) {
        xForm.getModifiedWeights()[row] = pValue;
        gridRefreshing = true;
        try {
          refreshRelWeightsTable();
          data.relWeightsTable.getSelectionModel().setSelectionInterval(row, row);
          refreshFlameImage(false);
        }
        finally {
          gridRefreshing = false;
        }
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
    if (xForm != null && currFlame != null && currFlame.getFinalXForms().indexOf(xForm) < 0) {
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
    if (prefs.getTinaDefaultDEMaxRadius() < EPSILON) {
      flame.setDeFilterEnabled(false);
    }
    else {
      flame.setDeFilterEnabled(true);
      flame.setDeFilterMaxRadius(prefs.getTinaDefaultDEMaxRadius());
    }
    RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(Integer.parseInt(data.paletteRandomPointsREd.getText()));
    flame.setPalette(palette);
    currFlame = flame;
    undoManager.initUndoStack(currFlame);
    randomBatch.add(0, new FlameThumbnail(currFlame, null));
    updateThumbnails();
    refreshUI();
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
        refreshXFormUI(getCurrXForm());
        refreshFlameImage(true);
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
      refreshVisualCamValues();

    }
  }

  private void flamePanel_mouseWheelEvent(MouseWheelEvent e) {
    if (flamePanel != null) {
      if (flamePanel.mouseWheelMoved(e.getWheelRotation())) {
        refreshXFormUI(getCurrXForm());
        refreshFlameImage(true);
        refreshVisualCamValues();
      }
    }
  }

  private void refreshVisualCamValues() {
    boolean oldNoRefrsh = noRefresh;
    noRefresh = true;
    try {
      data.cameraRollREd.setText(Tools.doubleToString(currFlame.getCamRoll()));
      data.cameraRollSlider.setValue(Tools.FTOI(currFlame.getCamRoll()));

      data.cameraPitchREd.setText(Tools.doubleToString(currFlame.getCamPitch()));
      data.cameraPitchSlider.setValue(Tools.FTOI(currFlame.getCamPitch()));

      data.cameraYawREd.setText(Tools.doubleToString(currFlame.getCamYaw()));
      data.cameraYawSlider.setValue(Tools.FTOI(currFlame.getCamYaw()));

      data.cameraCentreXREd.setText(Tools.doubleToString(currFlame.getCentreX()));
      data.cameraCentreXSlider.setValue(Tools.FTOI(currFlame.getCentreX() * SLIDER_SCALE_CENTRE));

      data.cameraCentreYREd.setText(Tools.doubleToString(currFlame.getCentreY()));
      data.cameraCentreYSlider.setValue(Tools.FTOI(currFlame.getCentreY() * SLIDER_SCALE_CENTRE));

      data.pixelsPerUnitREd.setText(Tools.doubleToString(currFlame.getPixelsPerUnit()));
      data.pixelsPerUnitSlider.setValue(Tools.FTOI(currFlame.getPixelsPerUnit()));

      data.focusXREd.setText(Tools.doubleToString(currFlame.getFocusX()));
      data.focusXSlider.setValue(Tools.FTOI(currFlame.getFocusX() * SLIDER_SCALE_ZPOS));

      data.focusYREd.setText(Tools.doubleToString(currFlame.getFocusY()));
      data.focusYSlider.setValue(Tools.FTOI(currFlame.getFocusY() * SLIDER_SCALE_ZPOS));

      data.focusZREd.setText(Tools.doubleToString(currFlame.getFocusZ()));
      data.focusZSlider.setValue(Tools.FTOI(currFlame.getFocusZ() * SLIDER_SCALE_ZPOS));

      data.dimishZREd.setText(Tools.doubleToString(currFlame.getDimishZ()));
      data.dimishZSlider.setValue(Tools.FTOI(currFlame.getDimishZ() * SLIDER_SCALE_ZPOS));
    }
    finally {
      noRefresh = oldNoRefrsh;
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
        if (flamePanel.isRedrawAfterMouseClick()) {
          refreshFlameImage(false);
        }
        if (xForm != null) {
          for (int i = 0; i < flame.getXForms().size(); i++) {
            if (xForm == flame.getXForms().get(i)) {
              data.transformationsTable.getSelectionModel().setSelectionInterval(i, i);
              return;
            }
          }
          for (int i = 0; i < flame.getFinalXForms().size(); i++) {
            if (xForm == flame.getFinalXForms().get(i)) {
              int row = flame.getXForms().size() + i;
              data.transformationsTable.getSelectionModel().setSelectionInterval(row, row);
              return;
            }
          }
        }
      }

    }
  }

  public void mouseTransformEditTrianglesButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformEditTrianglesButton.isSelected() ? MouseDragOperation.TRIANGLE : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getMouseDragOperation() == MouseDragOperation.TRIANGLE);
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
        data.mouseTransformEditTrianglesButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformEditFocusPointButton.isSelected() ? MouseDragOperation.FOCUS : MouseDragOperation.NONE);
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
      forceTriangleMode();
      XForm xForm = getCurrXForm();
      if (flamePanel != null) {
        flamePanel.setEditPostTransform(data.affineEditPostTransformButton.isSelected());
      }
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

  public void mouseTransformZoomInButton_clicked() {
    if (flamePanel != null) {
      flamePanel.zoomIn();
      refreshFlameImage(false);
    }
  }

  public void mouseTransformZoomOutButton_clicked() {
    if (flamePanel != null) {
      flamePanel.zoomOut();
      refreshFlameImage(false);
    }
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
      //      refreshFlameImage();
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
      enableDOFUI();
    }
  }

  public void focusZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusZSlider, data.focusZREd, "focusZ", SLIDER_SCALE_ZPOS);
  }

  public void focusXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusXSlider, data.focusXREd, "focusX", SLIDER_SCALE_ZPOS);
  }

  public void focusYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusYSlider, data.focusYREd, "focusY", SLIDER_SCALE_ZPOS);
  }

  public void dimishZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dimishZSlider, data.dimishZREd, "dimishZ", SLIDER_SCALE_ZPOS);
  }

  public void camZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camZSlider, data.camZREd, "camZ", SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFSlider, data.cameraDOFREd, "camDOF", SLIDER_SCALE_DOF);
  }

  public void cameraDOFAreaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFAreaSlider, data.cameraDOFAreaREd, "camDOFArea", SLIDER_SCALE_DOF_AREA);
  }

  public void cameraDOFExponentSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFExponentSlider, data.cameraDOFExponentREd, "camDOFExponent", SLIDER_SCALE_DOF_EXPONENT);
  }

  public void focusXREd_changed() {
    flameTextFieldChanged(data.focusXSlider, data.focusXREd, "focusX", SLIDER_SCALE_ZPOS);
  }

  public void focusYREd_changed() {
    flameTextFieldChanged(data.focusYSlider, data.focusYREd, "focusY", SLIDER_SCALE_ZPOS);
  }

  public void focusZREd_changed() {
    flameTextFieldChanged(data.focusZSlider, data.focusZREd, "focusZ", SLIDER_SCALE_ZPOS);
  }

  public void dimishZREd_changed() {
    flameTextFieldChanged(data.dimishZSlider, data.dimishZREd, "dimishZ", SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFREd_changed() {
    flameTextFieldChanged(data.cameraDOFSlider, data.cameraDOFREd, "camDOF", SLIDER_SCALE_DOF);
  }

  public void camZREd_changed() {
    flameTextFieldChanged(data.camZSlider, data.camZREd, "camZ", SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFAreaREd_changed() {
    flameTextFieldChanged(data.cameraDOFAreaSlider, data.cameraDOFAreaREd, "camDOFArea", SLIDER_SCALE_DOF_AREA);
  }

  public void cameraDOFExponentREd_changed() {
    flameTextFieldChanged(data.cameraDOFExponentSlider, data.cameraDOFExponentREd, "camDOFExponent", SLIDER_SCALE_DOF_EXPONENT);
  }

  public void shadingAmbientREd_changed() {
    shadingInfoTextFieldChanged(data.shadingAmbientSlider, data.shadingAmbientREd, "ambient", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDiffuseREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDiffuseSlider, data.shadingDiffuseREd, "diffuse", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongREd_changed() {
    shadingInfoTextFieldChanged(data.shadingPhongSlider, data.shadingPhongREd, "phong", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongSizeREd_changed() {
    shadingInfoTextFieldChanged(data.shadingPhongSizeSlider, data.shadingPhongSizeREd, "phongSize", SLIDER_SCALE_PHONGSIZE, 0);
  }

  public void shadingLightXREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightXSlider, data.shadingLightXREd, "lightPosX", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightYREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightYSlider, data.shadingLightYREd, "lightPosY", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightZREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightZSlider, data.shadingLightZREd, "lightPosZ", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightRedREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightRedSlider, data.shadingLightRedREd, "lightRed", 1.0, cIdx);
  }

  public void shadingLightGreenREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightGreenSlider, data.shadingLightGreenREd, "lightGreen", 1.0, cIdx);
  }

  public void shadingLightBlueREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightBlueSlider, data.shadingLightBlueREd, "lightBlue", 1.0, cIdx);
  }

  public void shadingLightBlueSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightBlueSlider, data.shadingLightBlueREd, "lightBlue", 1.0, cIdx);
  }

  public void shadingLightGreenSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightGreenSlider, data.shadingLightGreenREd, "lightGreen", 1.0, cIdx);
  }

  public void shadingAmbientSlider_changed() {
    shadingInfoSliderChanged(data.shadingAmbientSlider, data.shadingAmbientREd, "ambient", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDiffuseSlider_changed() {
    shadingInfoSliderChanged(data.shadingDiffuseSlider, data.shadingDiffuseREd, "diffuse", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongSlider_changed() {
    shadingInfoSliderChanged(data.shadingPhongSlider, data.shadingPhongREd, "phong", SLIDER_SCALE_AMBIENT, 0);
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) data.resolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private ResolutionProfile getBatchRenderResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) data.batchResolutionProfileCmb.getSelectedItem();
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
    if (currFlame == null) {
      return;
    }
    noRefresh = true;
    try {
      QualityProfile profile = getQualityProfile();
      currFlame.setQualityProfile(profile);
    }
    finally {
      noRefresh = false;
    }
  }

  public void resolutionProfileCmb_changed() {
    if (noRefresh) {
      return;
    }
    if (currFlame == null) {
      return;
    }
    noRefresh = true;
    try {
      ResolutionProfile profile = getResolutionProfile();
      currFlame.setResolutionProfile(profile);
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
    if (currFlame == null) {
      return;
    }
    noRefresh = true;
    try {
      currFlame.getShadingInfo().setShading((Shading) data.shadingCmb.getSelectedItem());
      refreshShadingUI();
      enableShadingUI();
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  public void shadingLightXSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightXSlider, data.shadingLightXREd, "lightPosX", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightYSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightYSlider, data.shadingLightYREd, "lightPosY", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightZSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightZSlider, data.shadingLightZREd, "lightPosZ", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightRedSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightRedSlider, data.shadingLightRedREd, "lightRed", 1.0, cIdx);
  }

  public void shadingLightCmb_changed() {
    if (noRefresh) {
      return;
    }
    noRefresh = true;
    try {
      refreshShadingUI();
    }
    finally {
      noRefresh = false;
    }
  }

  public void shadingPhongSizeSlider_changed() {
    shadingInfoSliderChanged(data.shadingPhongSizeSlider, data.shadingPhongSizeREd, "phongSize", SLIDER_SCALE_PHONGSIZE, 0);
  }

  public void loadFlameFromClipboard() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new Flam3Reader(prefs).readFlamesfromXML(xml);
          Flame flame = flames.get(0);
          currFlame = flame;
          undoManager.initUndoStack(currFlame);
          for (int i = flames.size() - 1; i >= 0; i--) {
            randomBatch.add(0, new FlameThumbnail(flames.get(i), null));
          }
          setupProfiles(currFlame);
          updateThumbnails();
          refreshUI();
          showStatusMessage(currFlame, "opened from clipboard");
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(Flame pFlame) {
    currFlame = pFlame.makeCopy();
    undoManager.initUndoStack(currFlame);
    setupProfiles(currFlame);
    updateThumbnails();
    refreshUI();
    showStatusMessage(currFlame, "imported into editor");
  }

  protected Flame exportFlame() {
    if (currFlame != null) {
      return currFlame.makeCopy();
    }
    else {
      return null;
    }
  }

  public void saveFlameToClipboard() {
    try {
      if (currFlame != null) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String xml = new Flam3Writer().getFlameXML(currFlame);
        StringSelection data = new StringSelection(xml);
        clipboard.setContents(data, data);
        //        try {
        //          System.out.println(new ScriptGenerator(currFlame).generateScript());
        //        }
        //        catch (Throwable ex) {
        //          ex.printStackTrace();
        //        }
        showStatusMessage(currFlame, "flame saved to clipboard");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void mouseTransformSlowButton_clicked() {
    if (flamePanel != null) {
      flamePanel.setFineMovement(data.mouseTransformSlowButton.isSelected());
    }
  }

  @Override
  public void refreshRenderBatchJobsTable() {
    final int COL_FLAME_FILE = 0;
    final int COL_FINISHED = 1;
    final int COL_ELAPSED = 2;
    final int COL_LAST_ERROR = 3;
    data.renderBatchJobsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return batchRenderList.size();
      }

      @Override
      public int getColumnCount() {
        return 4;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_FLAME_FILE:
            return "Flame file";
          case COL_FINISHED:
            return "Finished";
          case COL_ELAPSED:
            return "Elapsed time (ms)";
          case COL_LAST_ERROR:
            return "Last error";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        Job job = rowIndex < batchRenderList.size() ? batchRenderList.get(rowIndex) : null;
        if (job != null) {
          switch (columnIndex) {
            case COL_FLAME_FILE:
              return new File(job.getFlameFilename()).getName();
            case COL_FINISHED:
              return job.isFinished() ? String.valueOf(job.isFinished()) : "";
            case COL_ELAPSED:
              return job.isFinished() ? Tools.doubleToString(job.getElapsedSeconds()) : "";
            case COL_LAST_ERROR:
              return job.getLastErrorMsg();
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    });
    data.renderBatchJobsTable.getTableHeader().setFont(data.transformationsTable.getFont());
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_FLAME_FILE).setWidth(120);
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_FINISHED).setPreferredWidth(10);
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_ELAPSED).setWidth(10);
    data.renderBatchJobsTable.getColumnModel().getColumn(COL_LAST_ERROR).setWidth(120);
    if (batchRenderList.size() > 0)
      data.renderBatchJobsTable.setRowSelectionInterval(0, 0);
  }

  public void batchRenderAddFilesButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
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
      int jobCount = batchRenderList.size();
      chooser.setMultiSelectionEnabled(true);
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        for (File file : chooser.getSelectedFiles()) {
          String filename = file.getPath();
          boolean hasFile = false;
          for (Job job : batchRenderList) {
            if (job.getFlameFilename().equals(filename)) {
              hasFile = true;
              break;
            }
          }
          if (!hasFile) {
            Job job = new Job();
            job.setFlameFilename(filename);
            batchRenderList.add(job);
          }
        }
      }
      if (jobCount != batchRenderList.size()) {
        refreshRenderBatchJobsTable();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }

  }

  public void batchRenderFilesMoveUpButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    int row = data.renderBatchJobsTable.getSelectedRow();
    if (row < 0 && batchRenderList.size() > 0) {
      row = 0;
      data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
    }
    else if (row > 0 && row < batchRenderList.size()) {
      Job t = batchRenderList.get(row - 1);
      batchRenderList.set(row - 1, batchRenderList.get(row));
      batchRenderList.set(row, t);
      refreshRenderBatchJobsTable();
      data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row - 1, row - 1);
    }
  }

  public void batchRenderFilesMoveDownButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    int row = data.renderBatchJobsTable.getSelectedRow();
    if (row < 0 && batchRenderList.size() > 0) {
      row = 0;
      data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
    }
    else if (row >= 0 && row < batchRenderList.size() - 1) {
      Job t = batchRenderList.get(row + 1);
      batchRenderList.set(row + 1, batchRenderList.get(row));
      batchRenderList.set(row, t);
      refreshRenderBatchJobsTable();
      data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row + 1, row + 1);
    }
  }

  private JobRenderThread jobRenderThread = null;

  public void batchRenderStartButton_clicked() {
    if (jobRenderThread != null) {
      jobRenderThread.setCancelSignalled(true);
      return;
    }
    List<Job> activeJobList = new ArrayList<Job>();
    for (Job job : batchRenderList) {
      if (!job.isFinished()) {
        activeJobList.add(job);
      }
    }
    if (activeJobList.size() > 0) {
      jobRenderThread = new JobRenderThread(this, activeJobList, (ResolutionProfile) data.batchResolutionProfileCmb.getSelectedItem(), (QualityProfile) data.batchQualityProfileCmb.getSelectedItem());
      new Thread(jobRenderThread).start();
    }
    enableJobRenderControls();
  }

  public void batchRenderFilesRemoveAllButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    batchRenderList.clear();
    refreshRenderBatchJobsTable();
  }

  public void batchRenderFilesRemoveButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    int row = data.renderBatchJobsTable.getSelectedRow();
    if (row >= 0 && row < batchRenderList.size()) {
      batchRenderList.remove(row);
      refreshRenderBatchJobsTable();
      if (row >= batchRenderList.size()) {
        row--;
      }
      if (row >= 0 && row < batchRenderList.size()) {
        data.renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
      }
    }
  }

  @Override
  public Prefs getPrefs() {
    return prefs;
  }

  @Override
  public JProgressBar getTotalProgressBar() {
    return data.batchRenderTotalProgressBar;
  }

  @Override
  public JProgressBar getJobProgressBar() {
    return data.batchRenderJobProgressBar;
  }

  @Override
  public ProgressUpdater getJobProgressUpdater() {
    return jobProgressUpdater;
  }

  @Override
  public void onJobFinished() {
    jobRenderThread = null;
    System.err.println("JOB FINISHED");
    enableJobRenderControls();
  }

  @Override
  public JTable getRenderBatchJobsTable() {
    return data.renderBatchJobsTable;
  }

  public void toggleDarkTriangles() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setDarkTriangles(data.toggleDarkTrianglesButton.isSelected());
      refreshFlameImage(false);
    }
  }

  public void shadingBlurFadeREd_changed() {
    shadingInfoTextFieldChanged(data.shadingBlurFadeSlider, data.shadingBlurFadeREd, "blurFade", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingBlurFallOffREd_changed() {
    shadingInfoTextFieldChanged(data.shadingBlurFallOffSlider, data.shadingBlurFallOffREd, "blurFallOff", SLIDER_SCALE_BLUR_FALLOFF, 0);
  }

  public void shadingBlurRadiusREd_changed() {
    shadingInfoTextFieldChanged(data.shadingBlurRadiusSlider, data.shadingBlurRadiusREd, "blurRadius", 1.0, 0);
  }

  public void shadingBlurFallOffSlider_changed() {
    shadingInfoSliderChanged(data.shadingBlurFallOffSlider, data.shadingBlurFallOffREd, "blurFallOff", SLIDER_SCALE_BLUR_FALLOFF, 0);
  }

  public void shadingBlurFadeSlider_changed() {
    shadingInfoSliderChanged(data.shadingBlurFadeSlider, data.shadingBlurFadeREd, "blurFade", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingBlurRadiusSlider_changed() {
    shadingInfoSliderChanged(data.shadingBlurRadiusSlider, data.shadingBlurRadiusREd, "blurRadius", 1.0, 0);
  }

  public void shadingDistanceColorRadiusREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorRadiusSlider, data.shadingDistanceColorRadiusREd, "distanceColorRadius", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorRadiusSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorRadiusSlider, data.shadingDistanceColorRadiusREd, "distanceColorRadius", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorStyleREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorStyleSlider, data.shadingDistanceColorStyleREd, "distanceColorStyle", 1.0, 0);
  }

  public void shadingDistanceColorStyleSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorStyleSlider, data.shadingDistanceColorStyleREd, "distanceColorStyle", 1.0, 0);
  }

  public void shadingDistanceColorCoordinateREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorCoordinateSlider, data.shadingDistanceColorCoordinateREd, "distanceColorCoordinate", 1.0, 0);
  }

  public void shadingDistanceColorCoordinateSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorCoordinateSlider, data.shadingDistanceColorCoordinateREd, "distanceColorCoordinate", 1.0, 0);
  }

  public void shadingDistanceColorShiftREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorShiftSlider, data.shadingDistanceColorShiftREd, "distanceColorShift", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorShiftSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorShiftSlider, data.shadingDistanceColorShiftREd, "distanceColorShift", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorExponentREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorExponentSlider, data.shadingDistanceColorExponentREd, "distanceColorExponent", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorExponentSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorExponentSlider, data.shadingDistanceColorExponentREd, "distanceColorExponent", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorScaleREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorScaleSlider, data.shadingDistanceColorScaleREd, "distanceColorScale", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorScaleSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorScaleSlider, data.shadingDistanceColorScaleREd, "distanceColorScale", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetXREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorOffsetXSlider, data.shadingDistanceColorOffsetXREd, "distanceColorOffsetX", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetXSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorOffsetXSlider, data.shadingDistanceColorOffsetXREd, "distanceColorOffsetX", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetYREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorOffsetYSlider, data.shadingDistanceColorOffsetYREd, "distanceColorOffsetY", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetYSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorOffsetYSlider, data.shadingDistanceColorOffsetYREd, "distanceColorOffsetY", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetZREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorOffsetZSlider, data.shadingDistanceColorOffsetZREd, "distanceColorOffsetZ", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetZSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorOffsetZSlider, data.shadingDistanceColorOffsetZREd, "distanceColorOffsetZ", SLIDER_SCALE_AMBIENT, 0);
  }

  private ScriptRunner compileScript() throws Exception {
    return ScriptRunner.compile(data.scriptTextArea.getText());
  }

  public void runScriptButton_clicked() {
    try {
      ScriptRunner script = compileScript();
      saveUndoPoint();
      script.run(this);
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
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
      flame.distributeColors();
      transformationTableClicked();
    }
  }

  public void randomizeColorsBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.randomizeColors();
      transformationTableClicked();
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

  private List<RGBPalette> gradientLibraryList = new ArrayList<RGBPalette>();
  private final int GRADIENT_THUMB_HEIGHT = 20;
  private final int GRADIENT_THUMB_BORDER = 2;

  private void initGradientLibrary() {
    boolean oldCmbRefreshing = cmbRefreshing;
    cmbRefreshing = true;
    try {
      try {
        RGBPaletteReader reader = new Flam3PaletteReader();
        InputStream is = reader.getClass().getResourceAsStream("flam3-palettes.xml");
        gradientLibraryList.addAll(reader.readPalettes(is));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      if (gradientLibraryList.size() == 0) {
        for (int i = 0; i < 20; i++) {
          RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(7);
          gradientLibraryList.add(palette);
        }
      }

      data.gradientLibraryGradientCmb.removeAllItems();
      List<SimpleImage> thumbnailsList = new ArrayList<SimpleImage>();
      for (RGBPalette palette : gradientLibraryList) {
        thumbnailsList.add(new RGBPaletteRenderer().renderHorizPalette(palette, RGBPalette.PALETTE_SIZE, GRADIENT_THUMB_HEIGHT));
        data.gradientLibraryGradientCmb.addItem(palette);
      }
      data.gradientLibraryGradientCmb.setSelectedIndex(-1);
      updateGradientThumbnails(thumbnailsList);
    }
    finally {
      cmbRefreshing = oldCmbRefreshing;
    }
  }

  public void updateGradientThumbnails(List<SimpleImage> pImages) {
    if (data.gradientLibraryScrollPane != null) {
      data.gradientLibraryPanel.remove(data.gradientLibraryScrollPane);
      data.gradientLibraryScrollPane = null;
    }
    int panelWidth = data.gradientLibraryPanel.getBounds().width - 2 * GRADIENT_THUMB_BORDER;
    int panelHeight = (GRADIENT_THUMB_HEIGHT + GRADIENT_THUMB_BORDER) * pImages.size();
    JPanel gradientsPanel = new JPanel();
    gradientsPanel.setLayout(null);
    gradientsPanel.setSize(panelWidth, panelHeight);
    gradientsPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    for (int i = 0; i < pImages.size(); i++) {
      SimpleImage img = pImages.get(i);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(GRADIENT_THUMB_BORDER, i * GRADIENT_THUMB_HEIGHT + (i + 1) * GRADIENT_THUMB_BORDER);
      final int idx = i;
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1 || (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3)) {
            importFromGradientLibrary(idx);
          }
        }
      });
      gradientsPanel.add(imgPanel);
    }
    data.gradientLibraryScrollPane = new JScrollPane(gradientsPanel);
    data.gradientLibraryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    data.gradientLibraryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    data.gradientLibraryPanel.add(data.gradientLibraryScrollPane, BorderLayout.CENTER);
    data.gradientLibraryPanel.validate();
  }

  private void importFromGradientLibrary(int idx) {
    if (idx >= 0 && idx < gradientLibraryList.size()) {
      data.gradientLibraryGradientCmb.setSelectedItem(gradientLibraryList.get(idx));
    }
  }

  public void gradientLibraryGradientChanged() {
    if (!cmbRefreshing) {
      if (currFlame != null && data.gradientLibraryGradientCmb.getSelectedIndex() >= 0 && data.gradientLibraryGradientCmb.getSelectedIndex() < gradientLibraryList.size()) {
        saveUndoPoint();
        RGBPalette palette = gradientLibraryList.get(data.gradientLibraryGradientCmb.getSelectedIndex()).makeCopy();
        currFlame.setPalette(palette);
        refreshPaletteUI(palette);
        refreshFlameImage(false);
      }
    }
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
    if (currFlame != null) {
      saveUndoPoint();
      currFlame.setPreserveZ(data.affinePreserveZButton.isSelected());
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
        refreshQualityProfileCmb(data.interactiveQualityProfileCmb, profile);
        refreshQualityProfileCmb(data.swfAnimatorQualityProfileCmb, profile);
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
    if (currFlame != null) {
      getSwfAnimatorCtrl().importFlameFromEditor(currFlame.makeCopy());
      rootTabbedPane.setSelectedIndex(TinaSWFAnimatorController.PAGE_INDEX);
    }
  }

  public JTabbedPane getRootTabbedPane() {
    return rootTabbedPane;
  }

  public void tinaWrapIntoSubFlameButton_clicked() {
    if (currFlame != null) {
      saveUndoPoint();
      Flame newFlame = new SubFlameRandomFlameGenerator().embedFlame(currFlame);
      currFlame.assign(newFlame);
      updateThumbnails();
      refreshUI();
    }
  }

  public void scriptExampleSoftJulian() {
    data.scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
        "import org.jwildfire.create.tina.base.XForm;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFunc;\r\n" +
        "import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;\r\n" +
        "\r\n" +
        "import org.jwildfire.create.tina.variation.BubbleFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.HemisphereFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.Julia3DFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.LinearFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.PreBlurFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.SpirographFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.SplitsFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.ZTranslateFunc;\r\n" +
        "\r\n" +
        "// Bases on the Soft Julian Script by AsaLegault\r\n" +
        "//  http://asalegault.deviantart.com/art/Cloud-Julian-Script-84635709\r\n" +
        "public void run(ScriptRunnerEnvironment pEnv) throws Exception {\r\n" +
        "  Flame currFlame = pEnv.getCurrFlame();\r\n" +
        "  if(currFlame==null) {\r\n" +
        "    throw new Exception(\"Please select a flame at first\");\r\n" +
        "  }\r\n" +
        "  // First transform\r\n" +
        "  {\r\n" +
        "    VariationFunc varFunc = new Julia3DFunc();\r\n" +
        "    varFunc.setParameter(\"power\", -2);\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.addVariation(1.0, varFunc);\r\n" +
        "    xForm.setWeight(2.0);\r\n" +
        "    xForm.setColor(0.0);\r\n" +
        "    xForm.setColorSymmetry(0.01);\r\n" +
        "    xForm.setCoeff20(0.3); //o0    \r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // Second transform\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.addVariation(0.1, new BubbleFunc());\r\n" +
        "    xForm.addVariation(1.0, new PreBlurFunc());\r\n" +
        "    VariationFunc varFunc=new SpirographFunc();\r\n" +
        "    varFunc.setParameter(\"a\", 7.0);\r\n" +
        "    varFunc.setParameter(\"b\", 5.0);\r\n" +
        "    varFunc.setParameter(\"d\", 0.0);\r\n" +
        "    varFunc.setParameter(\"c1\", 5.0);\r\n" +
        "    varFunc.setParameter(\"c2\", -5.0);\r\n" +
        "    varFunc.setParameter(\"tmin\", 1.0);\r\n" +
        "    varFunc.setParameter(\"tmax\", 50.0);\r\n" +
        "    varFunc.setParameter(\"ymin\", -1.0);\r\n" +
        "    varFunc.setParameter(\"ymax\", 0.1);\r\n" +
        "    xForm.addVariation(0.03, varFunc);\r\n" +
        "    xForm.setWeight(1.0);\r\n" +
        "    xForm.setColor(0.844);\r\n" +
        "    currFlame.getXForms().add(xForm);    \r\n" +
        "  }\r\n" +
        "  // Third transform\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.addVariation(0.18, new HemisphereFunc());\r\n" +
        "    xForm.addVariation(1.0, new PreBlurFunc());\r\n" +
        "    xForm.addVariation(-0.025, new ZTranslateFunc());\r\n" +
        "    xForm.setWeight(0.5);\r\n" +
        "    xForm.setColor(0.0);\r\n" +
        "    currFlame.getXForms().add(xForm);    \r\n" +
        "  }\r\n" +
        "  //A fourth transform can be very useful when trying\r\n" +
        "  //to fill in the bubbles....But i'll let you figure\r\n" +
        "  //that out.//\r\n" +
        "  // ...\r\n" +
        "  // Final settings   \r\n" +
        "  currFlame.setCamRoll(2.0);\r\n" +
        "  currFlame.setCamPitch(46.0);\r\n" +
        "  currFlame.setCamYaw(0.0);\r\n" +
        "  currFlame.setCamPerspective(0.30);\r\n" +
        "  currFlame.setPixelsPerUnit(96);\r\n" +
        "  // Refresh the UI\r\n" +
        "  pEnv.refreshUI();\r\n" +
        "}\r\n" +
        "");
    runScriptButton_clicked();
  }

  public void scriptExampleWrapHeart() {
    data.scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
        "import org.jwildfire.create.tina.base.XForm;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFunc;\r\n" +
        "import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFuncList;\r\n" +
        "\r\n" +
        "public void run(ScriptRunnerEnvironment pEnv) throws Exception {\r\n" +
        "  Flame currFlame = pEnv.getCurrFlame();\r\n" +
        "  if(currFlame==null) {\r\n" +
        "    throw new Exception(\"Please select a flame at first\");\r\n" +
        "  }\r\n" +
        "  XForm xForm=new XForm(); \r\n" +
        "  xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(\"xheart\", true));  \r\n" +
        "  currFlame.getFinalXForms().clear();\r\n" +
        "  currFlame.getFinalXForms().add(xForm);\r\n" +
        "  // Final settings   \r\n" +
        "  currFlame.setCamRoll(0.0);\r\n" +
        "  currFlame.setCamPitch(0.0);\r\n" +
        "  currFlame.setCamYaw(0.0);\r\n" +
        "  currFlame.setCamZoom(1.0);\r\n" +
        "  currFlame.setCentreX(0.0);\r\n" +
        "  currFlame.setCentreY(0.0);\r\n" +
        "  currFlame.setPixelsPerUnit(96);\r\n" +
        "  // Refresh the UI\r\n" +
        "  pEnv.refreshUI();\r\n" +
        "}\r\n" +
        "");
    runScriptButton_clicked();
  }

  public void scriptExampleWrapBubble() {
    data.scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
        "import org.jwildfire.create.tina.base.XForm;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFunc;\r\n" +
        "import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFuncList;\r\n" +
        "\r\n" +
        "public void run(ScriptRunnerEnvironment pEnv) throws Exception {\r\n" +
        "  Flame currFlame = pEnv.getCurrFlame();\r\n" +
        "  if(currFlame==null) {\r\n" +
        "    throw new Exception(\"Please select a flame at first\");\r\n" +
        "  }\r\n" +
        "  XForm xForm=new XForm(); \r\n" +
        "  xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(\"bubble\", true));  \r\n" +
        "  currFlame.getFinalXForms().clear();\r\n" +
        "  currFlame.getFinalXForms().add(xForm);\r\n" +
        "  // Final settings   \r\n" +
        "  currFlame.setCamRoll(11.0);\r\n" +
        "  currFlame.setCamPitch(26.0);\r\n" +
        "  currFlame.setCamYaw(39.0);\r\n" +
        "  currFlame.setCamPerspective(0.16);\r\n" +
        "  currFlame.setCamZoom(1.0);\r\n" +
        "  currFlame.setCentreX(0.0);\r\n" +
        "  currFlame.setCentreY(0.0);\r\n" +
        "  currFlame.setPixelsPerUnit(96);\r\n" +
        "  // Refresh the UI\r\n" +
        "  pEnv.refreshUI();\r\n" +
        "}\r\n" +
        "");
    runScriptButton_clicked();
  }

  public void scriptExampleMobiusDragon() {
    data.scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
        "import org.jwildfire.create.tina.base.XForm;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFuncList;\r\n" +
        "import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;\r\n" +
        "import org.jwildfire.create.tina.transform.XFormTransformService;\r\n" +
        "\r\n" +
        "// Based on the Apophysis script \"Mobius Dragon\" by penny5775\r\n" +
        "//   http://penny5775.deviantart.com/art/Mobius-Dragon-Script-104021373\r\n" +
        "public void run(ScriptRunnerEnvironment pEnv) throws Exception {\r\n" +
        "  XForm xForm1;\r\n" +
        "  VariationFunc varFunc;\r\n" +
        "  Flame currFlame = pEnv.getCurrFlame();\r\n" +
        "  if(currFlame==null) {\r\n" +
        "    throw new Exception(\"Please select a flame at first\");\r\n" +
        "  }\r\n" +
        "  currFlame.getXForms().clear();\r\n" +
        "  double ranx = (Math.random() / 10.0)*1.2;   \r\n" +
        "  double rany = (Math.random() / 10.0)*1.2;\r\n" +
        "\r\n" +
        "  //T1 mobius\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setWeight(0.5+Math.random()*4.0);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "    xForm.setColorSymmetry(0.8);\r\n" +
        "\r\n" +
        "    varFunc = VariationFuncList.getVariationFuncInstance(\"mobius\", true);\r\n" +
        "    varFunc.setParameter(\"re_a\", 1.0);\r\n" +
        "    varFunc.setParameter(\"im_a\", 0.0);\r\n" +
        "    varFunc.setParameter(\"re_b\", 0.0);\r\n" +
        "    varFunc.setParameter(\"im_b\", 0.0);\r\n" +
        "    varFunc.setParameter(\"re_c\", 0.0);\r\n" +
        "    varFunc.setParameter(\"im_c\", -1.0);\r\n" +
        "    varFunc.setParameter(\"re_d\", 1.0);\r\n" +
        "    varFunc.setParameter(\"im_d\", 0.0);\r\n" +
        "    xForm.setCoeff20(ranx);\r\n" +
        "    xForm.setCoeff21(-rany);\r\n" +
        "    xForm.addVariation(1.0, varFunc);\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // T2 linear\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setWeight(0.5+Math.random()*0.8);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "    xForm.setColorSymmetry(0.8);\r\n" +
        "\r\n" +
        "    xForm.setPostCoeff00(-1.0);\r\n" +
        "    xForm.setPostCoeff01(0.0);\r\n" +
        "    xForm.setPostCoeff10(0.0);\r\n" +
        "    xForm.setPostCoeff11(-1.0);\r\n" +
        "    xForm.setCoeff20(ranx);\r\n" +
        "    xForm.setCoeff21(-rany);\r\n" +
        "    xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(\"linear3D\", true));\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // T3 linear\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setWeight(0.4+Math.random()*0.3);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "    xForm.setColorSymmetry(1.0);\r\n" +
        "\r\n" +
        "    xForm.setCoeff20(-1.0);\r\n" +
        "    xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(\"linear3D\", true));\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // T4 linear\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setWeight(0.15+Math.random()*0.25);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "    xForm.setColorSymmetry(1.0);\r\n" +
        "\r\n" +
        "    xForm.setCoeff20(1.0);\r\n" +
        "    xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(\"linear3D\", true));\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // T5 linear moved away on y axis\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setWeight(0.0625);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "\r\n" +
        "    xForm.setCoeff21(-3.0);\r\n" +
        "    xForm.setPostCoeff20(-ranx);\r\n" +
        "    xForm.setPostCoeff21(-rany);\r\n" +
        "    xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(\"linear3D\", true));\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // T6 linear line at edge of design\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setWeight(0.0625);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "\r\n" +
        "    xForm.setPostCoeff11(0.001);\r\n" +
        "    xForm.setPostCoeff20(-ranx);\r\n" +
        "    xForm.setPostCoeff21(1.0-rany);\r\n" +
        "    xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(\"linear3D\", true));\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // T7 a detail in center of void\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setWeight(0.0625);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "\r\n" +
        "    xForm.setPostCoeff20(0.5-(ranx/2.0));\r\n" +
        "    xForm.setPostCoeff21(-(rany/2.0));\r\n" +
        "    xForm.addVariation(Math.random()/10.0+0.1, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "\r\n" +
        "  // final\r\n" +
        "  currFlame.getFinalXForms().clear();\r\n" +
        "\r\n" +
        "  currFlame.setCentreX(0.0);\r\n" +
        "  currFlame.setCentreY(0.0);\r\n" +
        "  currFlame.setCamRoll(0.0);\r\n" +
        "  currFlame.setCamPitch(0.0);\r\n" +
        "  currFlame.setCamYaw(0.0);\r\n" +
        "  currFlame.setCamPerspective(0.0);\r\n" +
        "  currFlame.setPixelsPerUnit(120);\r\n" +
        "  // Refresh the UI\r\n" +
        "  pEnv.refreshUI();\r\n" +
        "}\r\n" +
        "");
    runScriptButton_clicked();
  }

  public void scriptExampleEscherFlux() {
    data.scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
        "import org.jwildfire.create.tina.base.XForm;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFunc;\r\n" +
        "import org.jwildfire.create.tina.variation.VariationFuncList;\r\n" +
        "import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;\r\n" +
        "import org.jwildfire.create.tina.transform.XFormTransformService;\r\n" +
        "\r\n" +
        "// Based on the Apophysis script \"BC n BDs Textured Escher Flux\"\r\n" +
        "//  http://fractal-resources.deviantart.com/art/BC-n-BDs-Textured-Escher-Flux-129501160\r\n" +
        "public void run(ScriptRunnerEnvironment pEnv) throws Exception {\r\n" +
        "  XForm xForm1;\r\n" +
        "  VariationFunc varFunc;\r\n" +
        "  Flame currFlame = pEnv.getCurrFlame();\r\n" +
        "  if(currFlame==null) {\r\n" +
        "    throw new Exception(\"Please select a flame at first\");\r\n" +
        "  }\r\n" +
        "  currFlame.getXForms().clear();\r\n" +
        "\r\n" +
        "  // First transform\r\n" +
        "  {\r\n" +
        "    XForm xForm = xForm1 = new XForm();\r\n" +
        "    xForm.setWeight(0.5);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "    xForm.setColorSymmetry(-(0.8+(Math.random()*0.1)));\r\n" +
        "\r\n" +
        "    xForm.setCoeff00(0.266948); // a\r\n" +
        "    xForm.setCoeff10(0.137096); // b\r\n" +
        "    xForm.setCoeff20(0.04212);  // e\r\n" +
        "    xForm.setCoeff01(0.071529); // c \r\n" +
        "    xForm.setCoeff11(-0.511651); // d \r\n" +
        "    xForm.setCoeff21(-0.334332); // f \r\n" +
        "\r\n" +
        "    xForm.setPostCoeff00(0.5);\r\n" +
        "    xForm.setPostCoeff10(0);\r\n" +
        "    xForm.setPostCoeff01(0);\r\n" +
        "    xForm.setPostCoeff11(0.25);\r\n" +
        "    xForm.setPostCoeff20(0);\r\n" +
        "    xForm.setPostCoeff21(0.05);\r\n" +
        "\r\n" +
        "    xForm.addVariation((0.1+(Math.random()*0.1)), VariationFuncList.getVariationFuncInstance(\"bubble\", true));\r\n" +
        "    xForm.addVariation(2.0, VariationFuncList.getVariationFuncInstance(\"pre_blur\", true));\r\n" +
        "\r\n" +
        "    varFunc = VariationFuncList.getVariationFuncInstance(\"rectangles\", true);\r\n" +
        "    varFunc.setParameter(\"x\", (0.1+(Math.random()*0.1)));\r\n" +
        "    varFunc.setParameter(\"y\", (1.5+(Math.random()*0.5)));\r\n" +
        "    xForm.addVariation(-(0.2+(Math.random()*0.15)), varFunc);\r\n" +
        "\r\n" +
        "    varFunc = VariationFuncList.getVariationFuncInstance(\"hexes\", true);\r\n" +
        "    varFunc.setParameter(\"cellsize\", ((int)(1+Math.random()*5.0)) *0.1+(Math.random()*0.1));\r\n" +
        "    varFunc.setParameter(\"power\", 1.0);\r\n" +
        "    varFunc.setParameter(\"rotate\", 0.0);\r\n" +
        "    varFunc.setParameter(\"scale\", (0.25+(Math.random()*0.3)));\r\n" +
        "    xForm.addVariation(-(0.05+(Math.random()*0.05)), varFunc);\r\n" +
        "\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  // 2nd transform\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setWeight(30.0);\r\n" +
        "    xForm.setColor(Math.random());\r\n" +
        "    xForm.setColorSymmetry((0.8+(Math.random()*0.1)));\r\n" +
        "\r\n" +
        "\r\n" +
        "    xForm.setCoeff00(1.0); // a\r\n" +
        "    xForm.setCoeff10((Math.random()*0.1)); // b\r\n" +
        "    xForm.setCoeff20(0.0);  // e\r\n" +
        "    xForm.setCoeff01((Math.random()*0.1)); // c \r\n" +
        "    xForm.setCoeff11(1.0); // d \r\n" +
        "    xForm.setCoeff21(0.0); // f \r\n" +
        "    XFormTransformService.rotate(xForm, Math.random() * 360.0, false);\r\n" +
        "\r\n" +
        "    xForm.setPostCoeff00((1+(Math.random()*0.1)));\r\n" +
        "    xForm.setPostCoeff10((Math.random()*0.1));\r\n" +
        "    xForm.setPostCoeff01(-(0.1+(Math.random()*0.1)));\r\n" +
        "    xForm.setPostCoeff11((0.9+(Math.random()*0.1)));\r\n" +
        "    xForm.setPostCoeff20(0);\r\n" +
        "    xForm.setPostCoeff21(0);\r\n" +
        "\r\n" +
        "    xForm.addVariation((0.02+(Math.random()*0.01)), VariationFuncList.getVariationFuncInstance(\"spherical\", true));\r\n" +
        "    xForm.addVariation((0.02+(Math.random()*0.01)), VariationFuncList.getVariationFuncInstance(\"bubble\", true));\r\n" +
        "\r\n" +
        "    varFunc = VariationFuncList.getVariationFuncInstance(\"escher\", true);\r\n" +
        "    varFunc.setParameter(\"beta\", (0.21+(Math.random()*0.03)));\r\n" +
        "    xForm.addVariation(1.0, varFunc);\r\n" +
        "\r\n" +
        "    varFunc = VariationFuncList.getVariationFuncInstance(\"flux\", true);\r\n" +
        "    varFunc.setParameter(\"spread\", -(1.5+(Math.random()*0.3)));\r\n" +
        "    xForm.addVariation(0.05, varFunc);\r\n" +
        "\r\n" +
        "    currFlame.getXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "\r\n" +
        "  // 3rd transform\r\n" +
        "  {\r\n" +
        "    XForm xForm = xForm1.makeCopy();\r\n" +
        "    xForm.setPostCoeff20(0.05);\r\n" +
        "    xForm.setPostCoeff21(-0.25);\r\n" +
        "    currFlame.getXForms().add(xForm);  \r\n" +
        "  }\r\n" +
        "  // final transform\r\n" +
        "  {\r\n" +
        "    XForm xForm = new XForm();\r\n" +
        "    xForm.setCoeff00(1.0); // a\r\n" +
        "    xForm.setCoeff10(0.0); // b\r\n" +
        "    xForm.setCoeff20(0.0); // e\r\n" +
        "    xForm.setCoeff01(0.0); // c \r\n" +
        "    xForm.setCoeff11(1.0); // d \r\n" +
        "    xForm.setCoeff21(-0.225); // f \r\n" +
        "    varFunc = VariationFuncList.getVariationFuncInstance(\"juliascope\", true);\r\n" +
        "    varFunc.setParameter(\"power\", 2.0);\r\n" +
        "    varFunc.setParameter(\"dist\", 1.0);\r\n" +
        "    xForm.addVariation(1.0, varFunc);\r\n" +
        "    currFlame.getFinalXForms().add(xForm);\r\n" +
        "  }\r\n" +
        "  currFlame.setCamRoll(0.0);\r\n" +
        "  currFlame.setCamPitch(0.0);\r\n" +
        "  currFlame.setCamYaw(0.0);\r\n" +
        "  currFlame.setCamPerspective(0.0);\r\n" +
        "  currFlame.setPixelsPerUnit(50);\r\n" +
        "  // Refresh the UI\r\n" +
        "  pEnv.refreshUI();\r\n" +
        "}\r\n" +
        "");
    runScriptButton_clicked();
  }

  public void loadScript() {
    try {
      JFileChooser chooser = new JWFScriptFileChooser(prefs);
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
        String script = Tools.readUTF8Textfile(file.getAbsolutePath());
        data.scriptTextArea.setText(script);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void saveScript() {
    try {
      String script = data.scriptTextArea.getText();
      if (script != null && script.trim().length() > 0) {
        JFileChooser chooser = new JWFScriptFileChooser(prefs);
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
          Tools.writeUTF8Textfile(file.getAbsolutePath(), script);
          prefs.setLastOutputFlameFile(file);
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void mouseTransformEditPointsButton_clicked() {
    if (!refreshing) {
      refreshing = true;
      try {
        data.mouseTransformEditTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformEditPointsButton.isSelected() ? MouseDragOperation.POINTS : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getMouseDragOperation() == MouseDragOperation.POINTS);
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
        data.mouseTransformEditTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformEditViewButton.isSelected() ? MouseDragOperation.VIEW : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getMouseDragOperation() == MouseDragOperation.TRIANGLE);
          refreshFlameImage(false);
        }
      }
      finally {
        refreshing = false;
      }
    }
  }

  public void undoAction() {
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      undoManager.setEnabled(false);
      try {
        int txRow = data.transformationsTable.getSelectedRow();
        undoManager.saveUndoPoint(currFlame);
        undoManager.undo(currFlame);
        enableUndoControls();
        refreshUI();
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
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      undoManager.setEnabled(false);
      try {
        int txRow = data.transformationsTable.getSelectedRow();
        undoManager.redo(currFlame);
        enableUndoControls();
        refreshUI();
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
    Flame currFlame = getCurrFlame();
    if (currFlame != null) {
      undoManager.saveUndoPoint(currFlame);
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
    Flame currFlame = getCurrFlame();
    int stackSize = undoManager.getUndoStackSize(currFlame);
    if (stackSize > 0) {
      int pos = undoManager.getUndoStackPosition(currFlame);
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

  public void grabPaletteFromImageButton_actionPerformed(ActionEvent e) {
    JFileChooser chooser = new ImageFileChooser();
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
        currFlame.setPalette(palette);
        refreshPaletteColorsTable();
        refreshPaletteUI(palette);
        refreshFlameImage(false);
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void xFormAntialiasCopyToAllBtn_clicked() {
    Flame flame = getCurrFlame();
    XForm xForm = getCurrXForm();
    if (xForm != null) {
      saveUndoPoint();
      for (XForm xf : flame.getXForms()) {
        if (xf != xForm) {
          xf.setAntialiasAmount(xForm.getAntialiasAmount());
          xf.setAntialiasRadius(xForm.getAntialiasRadius());
        }
      }
      for (XForm xf : flame.getFinalXForms()) {
        if (xf != xForm) {
          xf.setAntialiasAmount(xForm.getAntialiasAmount());
          xf.setAntialiasRadius(xForm.getAntialiasRadius());
        }
      }
      transformationTableClicked();
    }
  }

  public void paletteInvertBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.getPalette().negativeColors();
      refreshPaletteUI(flame.getPalette());
      transformationTableClicked();
    }
  }

  public void paletteReverseBtn_clicked() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.getPalette().reverseColors();
      refreshPaletteUI(flame.getPalette());
      transformationTableClicked();
    }
  }

  private void showStatusMessage(Flame pFlame, String pStatus) {
    if (pFlame == null)
      return;
    String prefix;
    if (pFlame.getName() != null && pFlame.getName().length() > 0) {
      prefix = "Flame \"" + pFlame.getName() + "\"";
    }
    else {
      prefix = "Unamed flame";
    }
    if (pFlame.getLastFilename() != null && pFlame.getLastFilename().length() > 0) {
      prefix += " (" + pFlame.getLastFilename() + ") ";
    }
    else {
      prefix += " ";
    }
    tinaFrame.setTitle(prefix + (pStatus != null && pStatus.length() > 0 ? ": " + pStatus : ""));
    //    tinaFrame.setTitle(tinaFrameTitle + (pStatus != null && pStatus.length() > 0 ? ": " + pStatus : ""));
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
      if (currFlame != null) {
        String filename = qsaveFilenameGen.generateNextFilename();
        new Flam3Writer().writeFlame(currFlame, filename);
        showStatusMessage(currFlame, "quicksave <" + new File(filename).getName() + "> saved");
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
    if (currFlame != null) {
      currFlame.setBGTransparency(data.bgTransparencyCBx.isSelected());
      enableDOFUI();
    }
  }

  public DancingFractalsController getDancingFractalsController() {
    return dancingFractalsController;
  }

  private class BatchRenderPreviewFlameHolder implements FlameHolder {

    @Override
    public Flame getFlame() {
      try {
        int row = data.renderBatchJobsTable.getSelectedRow();
        if (row >= 0 && row < batchRenderList.size()) {
          Job job = batchRenderList.get(row);
          List<Flame> flames = new Flam3Reader(prefs).readFlames(job.getFlameFilename());
          return flames.size() > 0 ? flames.get(0) : null;
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      return null;
    }

  }

  private BatchRenderPreviewFlameHolder batchRenderPreviewFlameHolder = null;

  private BatchRenderPreviewFlameHolder getBatchRenderPreviewFlameHolder() {
    if (batchRenderPreviewFlameHolder == null) {
      batchRenderPreviewFlameHolder = new BatchRenderPreviewFlameHolder();
    }
    return batchRenderPreviewFlameHolder;
  }

  private FlamePanel getBatchPreviewFlamePanel() {
    if (batchPreviewFlamePanel == null) {
      int width = data.batchPreviewRootPanel.getWidth();
      int height = data.batchPreviewRootPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      batchPreviewFlamePanel = new FlamePanel(img, 0, 0, data.batchPreviewRootPanel.getWidth(), getBatchRenderPreviewFlameHolder());
      ResolutionProfile resProfile = getBatchRenderResolutionProfile();
      batchPreviewFlamePanel.setRenderWidth(resProfile.getWidth());
      batchPreviewFlamePanel.setRenderHeight(resProfile.getHeight());
      batchPreviewFlamePanel.setDrawTriangles(false);
      data.batchPreviewRootPanel.add(batchPreviewFlamePanel, BorderLayout.CENTER);
      data.batchPreviewRootPanel.getParent().validate();
      data.batchPreviewRootPanel.repaint();
    }
    return batchPreviewFlamePanel;
  }

  public void renderBatchJobsTableClicked() {
    FlamePanel imgPanel = getBatchPreviewFlamePanel();
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    Flame flame = getBatchRenderPreviewFlameHolder().getFlame();
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height);
      if (flame != null) {
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
        flame.setWidth(info.getImageWidth());
        flame.setHeight(info.getImageHeight());

        FlameRenderer renderer = new FlameRenderer(flame, prefs, data.toggleTransparencyButton.isSelected());
        flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
        flame.setSpatialFilterRadius(0.0);
        RenderedFlame res = renderer.renderFlame(info);
        imgPanel.setImage(res.getImage());
      }
      else {
        imgPanel.setImage(new SimpleImage(width, height));
      }
    }
    data.batchPreviewRootPanel.invalidate();
    data.batchPreviewRootPanel.validate();
  }

  public void batchRendererResolutionProfileCmb_changed() {
    if (batchPreviewFlamePanel != null) {
      data.batchPreviewRootPanel.remove(batchPreviewFlamePanel);
      batchPreviewFlamePanel = null;
    }
    renderBatchJobsTableClicked();
  }

  public void renderImageButton_actionPerformed0() {
    if (currFlame != null) {
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
        if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          QualityProfile qualProfile = getQualityProfile();
          ResolutionProfile resProfile = getResolutionProfile();
          File file = chooser.getSelectedFile();
          prefs.setLastOutputImageFile(file);
          int width = resProfile.getWidth();
          int height = resProfile.getHeight();
          RenderInfo info = new RenderInfo(width, height);
          Flame flame = getCurrFlame();
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(info.getImageWidth());
          flame.setHeight(info.getImageHeight());
          boolean renderHDR = qualProfile.isWithHDR();
          info.setRenderHDR(renderHDR);
          boolean renderHDRIntensityMap = qualProfile.isWithHDRIntensityMap();
          info.setRenderHDRIntensityMap(renderHDRIntensityMap);
          double oldSampleDensity = flame.getSampleDensity();
          double oldFilterRadius = flame.getSpatialFilterRadius();
          try {
            flame.setSampleDensity(qualProfile.getQuality());
            FlameRenderer renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency());
            renderer.setProgressUpdater(mainProgressUpdater);
            long t0 = Calendar.getInstance().getTimeInMillis();
            RenderedFlame res = renderer.renderFlame(info);
            long t1 = Calendar.getInstance().getTimeInMillis();
            showStatusMessage(flame, "render time: " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
            new ImageWriter().saveImage(res.getImage(), file.getAbsolutePath());
            if (res.getHDRImage() != null) {
              new ImageWriter().saveImage(res.getHDRImage(), file.getAbsolutePath() + ".hdr");
            }
            if (res.getHDRIntensityMap() != null) {
              new ImageWriter().saveImage(res.getHDRIntensityMap(), file.getAbsolutePath() + ".intensity.hdr");
            }
          }
          finally {
            flame.setSampleDensity(oldSampleDensity);
            flame.setSpatialFilterRadius(oldFilterRadius);
          }
          mainController.loadImage(file.getAbsolutePath(), false);
        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
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
    else if (currFlame != null) {
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
                showStatusMessage(flame, "render time: " + Tools.doubleToString(pElapsedTime) + "s");
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

  public void deFilterEnableCBx_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.setDeFilterEnabled(data.deFilterEnableCbx.isSelected());
      enableDEFilterUI();
    }
  }

  public void spatialFilterKernelCmb_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.setSpatialFilterKernel((FilterKernelType) data.filterKernelCmb.getSelectedItem());
    }
  }

  public void deFilterKernelCmb_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.setDeFilterKernel((FilterKernelType) data.deFilterKernelCmb.getSelectedItem());
    }
  }
}
