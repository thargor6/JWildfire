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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
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
import org.jwildfire.create.tina.GradientController;
import org.jwildfire.create.tina.JWFScriptController;
import org.jwildfire.create.tina.KeyFramesController;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.ShadingInfo;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.batch.Job;
import org.jwildfire.create.tina.batch.JobRenderThread;
import org.jwildfire.create.tina.batch.JobRenderThreadController;
import org.jwildfire.create.tina.browser.FlameBrowserController;
import org.jwildfire.create.tina.dance.DancingFractalsController;
import org.jwildfire.create.tina.edit.UndoManager;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.mutagen.MutaGenController;
import org.jwildfire.create.tina.mutagen.MutationType;
import org.jwildfire.create.tina.palette.DefaultGradientSelectionProvider;
import org.jwildfire.create.tina.palette.GradientSelectionProvider;
import org.jwildfire.create.tina.palette.MedianCutQuantizer;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RGBPaletteRenderer;
import org.jwildfire.create.tina.palette.RandomRGBPaletteGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSample;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomflame.WikimediaCommonsRandomFlameGenerator;
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
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.MainController;
import org.jwildfire.transform.ComposeTransformer;
import org.jwildfire.transform.RectangleTransformer;
import org.jwildfire.transform.TextTransformer;
import org.jwildfire.transform.TextTransformer.FontStyle;
import org.jwildfire.transform.TextTransformer.HAlignment;
import org.jwildfire.transform.TextTransformer.Mode;
import org.jwildfire.transform.TextTransformer.VAlignment;

import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.swing.JFontChooser;
import com.l2fprod.common.util.ResourceManager;

public class TinaController implements FlameHolder, LayerHolder, JobRenderThreadController, ScriptRunnerEnvironment, UndoManagerHolder<Flame>, JWFScriptExecuteController, GradientSelectionProvider {
  public static final int PAGE_INDEX = 0;

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
  private MutaGenController mutaGenController;
  private TinaInteractiveRendererController interactiveRendererCtrl;
  private TinaSWFAnimatorController swfAnimatorCtrl;
  private JWFScriptController jwfScriptController;
  private FlameBrowserController flameBrowserController;
  private GradientController gradientController;
  private KeyFramesController keyFramesController;

  private final JInternalFrame tinaFrame;
  private final String tinaFrameTitle;
  private final JPanel centerPanel;
  private boolean firstFlamePanel = true;
  private FlamePanel flamePanel;
  private FlamePanel prevFlamePanel;
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
  private Flame _currFlame, _currRandomizeFlame;
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

    jwfScriptController = new JWFScriptController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, parameterObject.scriptTree,
        parameterObject.scriptDescriptionTextArea, parameterObject.scriptTextArea, parameterObject.compileScriptButton,
        parameterObject.saveScriptBtn, parameterObject.revertScriptBtn, parameterObject.rescanScriptsBtn, parameterObject.newScriptBtn, parameterObject.newScriptFromFlameBtn, parameterObject.deleteScriptBtn,
        parameterObject.scriptRenameBtn, parameterObject.scriptDuplicateBtn, parameterObject.scriptRunBtn);

    flameBrowserController = new FlameBrowserController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, parameterObject.flameBrowserTree, parameterObject.flameBrowersImagesPnl,
        parameterObject.flameBrowserRefreshBtn, parameterObject.flameBrowserChangeFolderBtn, parameterObject.flameBrowserToEditorBtn, parameterObject.flameBrowserToBatchEditorBtn, parameterObject.flameBrowserDeleteBtn, parameterObject.flameBrowserRenameBtn);

    gradientController = new GradientController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel, parameterObject.gradientLibTree, parameterObject.pGradientLibraryPanel,
        parameterObject.gradientLibraryRescanBtn, parameterObject.gradientLibraryNewFolderBtn, parameterObject.gradientLibraryRenameFolderBtn,
        parameterObject.gradientsList);

    keyFramesController = new KeyFramesController(this, parameterObject.pErrorHandler, prefs, parameterObject.pCenterPanel,
        parameterObject.keyframesFrameField, parameterObject.keyframesFrameSlider, parameterObject.keyframesFrameCountField,
        parameterObject.addKeyframeBtn, parameterObject.duplicateKeyframeBtn, parameterObject.deleteKeyframeBtn, parameterObject.keyFramesTable);

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
    data.paletteRandomPointsREd = parameterObject.pPaletteRandomPointsREd;
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

    data.mouseTransformEditViewButton = parameterObject.pMouseTransformViewButton;
    data.toggleVariationsButton = parameterObject.pToggleVariationsButton;
    data.toggleTransparencyButton = parameterObject.pToggleTransparencyButton;
    data.toggleDarkTrianglesButton = parameterObject.pToggleDarkTrianglesButton;
    data.randomizeButton = parameterObject.randomizeButton;
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
    data.editTransformCaptionButton = parameterObject.editTransformCaptionButton;
    data.editFlameTileButton = parameterObject.editFlameTileButton;
    data.snapShotButton = parameterObject.snapShotButton;
    data.qSaveButton = parameterObject.qSaveButton;
    data.quickMutationButton = parameterObject.quickMutationButton;
    data.dancingFlamesButton = parameterObject.dancingFlamesButton;
    data.movieButton = parameterObject.movieButton;
    data.transformSlowButton = parameterObject.transformSlowButton;
    data.transparencyButton = parameterObject.transparencyButton;
    data.darkTrianglesButton = parameterObject.darkTrianglesButton;
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

    qsaveFilenameGen = new QuickSaveFilenameGen(prefs);

    enableUndoControls();

    initHelpPane();
    initFAQPane();

    refreshPaletteColorsTable();
    refreshRenderBatchJobsTable();

    enableControls();
    enableShadingUI();
    enableDOFUI();
    enableDEFilterUI();

    enableXFormControls(null);
    enableLayerControls();

    refreshResolutionProfileCmb(data.resolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.interactiveResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.batchResolutionProfileCmb, null);
    refreshResolutionProfileCmb(data.swfAnimatorResolutionProfileCmb, null);
    if (data.swfAnimatorResolutionProfileCmb.getItemCount() > 0) {
      data.swfAnimatorResolutionProfileCmb.setSelectedIndex(0);
    }
    refreshQualityProfileCmb(data.qualityProfileCmb, null);
    refreshQualityProfileCmb(data.batchQualityProfileCmb, null);
    refreshQualityProfileCmb(data.swfAnimatorQualityProfileCmb, null);
    if (data.swfAnimatorQualityProfileCmb.getItemCount() > 0) {
      data.swfAnimatorQualityProfileCmb.setSelectedIndex(0);
    }

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

  private FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      int width = centerPanel.getWidth();
      int height = centerPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(img, 0, 0, centerPanel.getWidth(), this, this);
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
                if (flamePanel.getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
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
                else if (flamePanel.getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(0, -1);
                }
                break;
              // right
              case 39:
                if (flamePanel.getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
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
                else if (flamePanel.getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(0, 1);
                }
                break;
              // up
              case 38:
                if (flamePanel.getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
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
                else if (flamePanel.getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(1, 1);
                }
                break;
              // down
              case 40:
                if (flamePanel.getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE) {
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
                else if (flamePanel.getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_move(1, -1);
                }
                break;
              // 1
              case 49:
                if (flamePanel.getMouseDragOperation() == MouseDragOperation.GRADIENT) {
                  gradientMarker_selectColor(0);
                }
                break;
              // 1
              case 50:
                if (flamePanel.getMouseDragOperation() == MouseDragOperation.GRADIENT) {
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
    refreshFlameImage(true, pMouseDown);
  }

  @Override
  public Flame getCurrFlame() {
    return _currFlame;
  }

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
    if (_currFlame != null) {
      for (Layer layer : _currFlame.getLayers()) {
        deRegisterFromEditor(_currFlame, layer);
      }
    }
    importFlame(pFlame, pAddToThumbnails);
    // TODO
    registerToEditor(_currFlame, _currFlame.getFirstLayer());
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

  //  static int rCount = 1;

  public void refreshFlameImage(boolean pQuickRender, boolean pMouseDown) {
    //    System.out.println("R" + rCount++);
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
              renderer = new FlameRenderer(flame, prefs, data.toggleTransparencyButton.isSelected(), false);
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
            SimpleImage img = res.getImage();
            if (data.layerAppendBtn.isSelected() && !pMouseDown) {
              TextTransformer txt = new TextTransformer();
              txt.setText1("layer-append-mode");
              txt.setText2("active");
              txt.setAntialiasing(true);
              txt.setColor(Color.RED);
              txt.setMode(Mode.NORMAL);
              txt.setFontStyle(FontStyle.BOLD);
              txt.setFontName("Arial");
              txt.setFontSize(36);
              txt.setHAlign(HAlignment.CENTRE);
              txt.setVAlign(VAlignment.CENTRE);
              txt.transformImage(img);
            }

            if (data.layerPreviewBtn.isSelected()) {
              Layer onlyVisibleLayer = null;
              for (Layer layer : getCurrFlame().getLayers()) {
                if (layer.isVisible()) {
                  if (onlyVisibleLayer == null) {
                    onlyVisibleLayer = layer;
                  }
                  else {
                    onlyVisibleLayer = null;
                    break;
                  }
                }
              }
              boolean drawSubLayer = flame.getLayers().size() > 1 && getCurrLayer() != null && getCurrLayer() != onlyVisibleLayer;

              if (drawSubLayer) {
                Flame singleLayerFlame = new Flame();
                singleLayerFlame.assign(flame);
                singleLayerFlame.getLayers().clear();
                singleLayerFlame.getLayers().add(getCurrLayer().makeCopy());
                singleLayerFlame.getFirstLayer().setVisible(true);
                singleLayerFlame.getFirstLayer().setWeight(1.0);
                RenderInfo lInfo = new RenderInfo(width / 4 * renderScale, height / 4 * renderScale);
                double lWScl = (double) lInfo.getImageWidth() / (double) singleLayerFlame.getWidth();
                double lHScl = (double) lInfo.getImageHeight() / (double) singleLayerFlame.getHeight();
                singleLayerFlame.setPixelsPerUnit((lWScl + lHScl) * 0.5 * singleLayerFlame.getPixelsPerUnit() * 0.5);
                singleLayerFlame.setWidth(lInfo.getImageWidth());
                singleLayerFlame.setHeight(lInfo.getImageHeight());
                FlameRenderer lRenderer = new FlameRenderer(singleLayerFlame, prefs, false, false);
                RenderedFlame lRes = lRenderer.renderFlame(lInfo);
                SimpleImage layerImg = lRes.getImage();

                RectangleTransformer rT = new RectangleTransformer();
                rT.setColor(new java.awt.Color(200, 200, 200));
                rT.setLeft(0);
                rT.setTop(0);
                rT.setThickness(3);
                rT.setWidth(lInfo.getImageWidth());
                rT.setHeight(lInfo.getImageHeight());
                rT.transformImage(layerImg);

                ComposeTransformer cT = new ComposeTransformer();
                cT.setHAlign(ComposeTransformer.HAlignment.LEFT);
                cT.setVAlign(ComposeTransformer.VAlignment.BOTTOM);
                cT.setTop(10);
                cT.setLeft(10);
                cT.setForegroundImage(layerImg);
                cT.transformImage(img);
              }
            }
            imgPanel.setImage(img);
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
  }

  public void refreshLayerUI() {
    noRefresh = true;
    try {
      refreshVisualCamValues();

      data.cameraPerspectiveREd.setText(Tools.doubleToString(getCurrFlame().getCamPerspective()));
      data.cameraPerspectiveSlider.setValue(Tools.FTOI(getCurrFlame().getCamPerspective() * SLIDER_SCALE_PERSPECTIVE));

      data.cameraZoomREd.setText(Tools.doubleToString(getCurrFlame().getCamZoom()));
      data.cameraZoomSlider.setValue(Tools.FTOI(getCurrFlame().getCamZoom() * SLIDER_SCALE_ZOOM));

      data.cameraDOFREd.setText(Tools.doubleToString(getCurrFlame().getCamDOF()));
      data.cameraDOFSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOF() * SLIDER_SCALE_DOF));

      data.cameraDOFAreaREd.setText(Tools.doubleToString(getCurrFlame().getCamDOFArea()));
      data.cameraDOFAreaSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOFArea() * SLIDER_SCALE_DOF_AREA));

      data.cameraDOFExponentREd.setText(Tools.doubleToString(getCurrFlame().getCamDOFExponent()));
      data.cameraDOFExponentSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOFExponent() * SLIDER_SCALE_DOF_EXPONENT));

      data.camZREd.setText(Tools.doubleToString(getCurrFlame().getCamZ()));
      data.camZSlider.setValue(Tools.FTOI(getCurrFlame().getCamZ() * SLIDER_SCALE_ZPOS));

      data.newDOFCBx.setSelected(getCurrFlame().isNewCamDOF());

      data.brightnessREd.setText(Tools.doubleToString(getCurrFlame().getBrightness()));
      data.brightnessSlider.setValue(Tools.FTOI(getCurrFlame().getBrightness() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.contrastREd.setText(Tools.doubleToString(getCurrFlame().getContrast()));
      data.contrastSlider.setValue(Tools.FTOI(getCurrFlame().getContrast() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.vibrancyREd.setText(Tools.doubleToString(getCurrFlame().getVibrancy()));
      data.vibrancySlider.setValue(Tools.FTOI(getCurrFlame().getVibrancy() * SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.gammaREd.setText(Tools.doubleToString(getCurrFlame().getGamma()));
      data.gammaSlider.setValue(Tools.FTOI(getCurrFlame().getGamma() * SLIDER_SCALE_GAMMA));

      data.filterRadiusREd.setText(Tools.doubleToString(getCurrFlame().getSpatialFilterRadius()));
      data.filterRadiusSlider.setValue(Tools.FTOI(getCurrFlame().getSpatialFilterRadius() * SLIDER_SCALE_FILTER_RADIUS));
      data.filterKernelCmb.setSelectedItem(getCurrFlame().getSpatialFilterKernel());

      data.deFilterEnableCbx.setSelected(getCurrFlame().isDeFilterEnabled());
      data.deFilterMaxRadiusREd.setText(Tools.doubleToString(getCurrFlame().getDeFilterMaxRadius()));
      data.deFilterMaxRadiusSlider.setValue(Tools.FTOI(getCurrFlame().getDeFilterMaxRadius() * SLIDER_SCALE_FILTER_RADIUS));
      data.deFilterMinRadiusREd.setText(Tools.doubleToString(getCurrFlame().getDeFilterMinRadius()));
      data.deFilterMinRadiusSlider.setValue(Tools.FTOI(getCurrFlame().getDeFilterMinRadius() * SLIDER_SCALE_FILTER_RADIUS));
      data.deFilterCurveREd.setText(Tools.doubleToString(getCurrFlame().getDeFilterCurve()));
      data.deFilterCurveSlider.setValue(Tools.FTOI(getCurrFlame().getDeFilterCurve() * SLIDER_SCALE_FILTER_RADIUS));
      data.deFilterKernelCmb.setSelectedItem(getCurrFlame().getDeFilterKernel());

      data.gammaThresholdREd.setText(String.valueOf(getCurrFlame().getGammaThreshold()));
      data.gammaThresholdSlider.setValue(Tools.FTOI(getCurrFlame().getGammaThreshold() * SLIDER_SCALE_GAMMA_THRESHOLD));

      data.bgTransparencyCBx.setSelected(getCurrFlame().isBGTransparency());

      data.xFormAntialiasAmountREd.setText(Tools.doubleToString(getCurrFlame().getAntialiasAmount()));
      data.xFormAntialiasAmountSlider.setValue(Tools.FTOI(getCurrFlame().getAntialiasAmount() * SLIDER_SCALE_COLOR));
      data.xFormAntialiasRadiusREd.setText(Tools.doubleToString(getCurrFlame().getAntialiasRadius()));
      data.xFormAntialiasRadiusSlider.setValue(Tools.FTOI(getCurrFlame().getAntialiasRadius() * SLIDER_SCALE_COLOR));

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
      refreshShadingUI();
      enableShadingUI();
      enableDOFUI();
      enableDEFilterUI();
      enableLayerControls();
      refreshPaletteUI(getCurrLayer().getPalette());
    }
    finally {
      noRefresh = false;
    }
  }

  private void refreshShadingUI() {
    ShadingInfo shadingInfo = getCurrFlame() != null ? getCurrFlame().getShadingInfo() : null;
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
    boolean newDOF = getCurrFlame() != null ? getCurrFlame().isNewCamDOF() : false;
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
    data.camZREd.setEnabled(getCurrFlame() != null);
    data.camZSlider.setEnabled(getCurrFlame() != null);
    data.dimishZREd.setEnabled(getCurrFlame() != null);
    data.dimishZSlider.setEnabled(getCurrFlame() != null);
  }

  private void enableDEFilterUI() {
    boolean deEnabled = getCurrFlame() != null ? getCurrFlame().isDeFilterEnabled() : false;
    data.deFilterMaxRadiusREd.setEnabled(deEnabled);
    data.deFilterMaxRadiusSlider.setEnabled(deEnabled);
    data.deFilterMinRadiusREd.setEnabled(deEnabled);
    data.deFilterMinRadiusSlider.setEnabled(deEnabled);
    data.deFilterCurveREd.setEnabled(deEnabled);
    data.deFilterCurveSlider.setEnabled(deEnabled);
    data.deFilterKernelCmb.setEnabled(deEnabled);
  }

  private void enableShadingUI() {
    ShadingInfo shadingInfo = getCurrFlame() != null ? getCurrFlame().getShadingInfo() : null;
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
              return rowIndex < getCurrLayer().getXForms().size() ? String.valueOf(rowIndex + 1) : "Final";
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
          RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(data.paletteKeyFrames, data.paletteFadeColorsCBx.isSelected());
          saveUndoPoint();
          getCurrLayer().setPalette(palette);
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
    final int COL_VARIATIONS = 1;
    final int COL_WEIGHT = 2;
    data.relWeightsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        XForm xForm = getCurrXForm();
        return xForm != null && getCurrLayer().getFinalXForms().indexOf(xForm) < 0 ? getCurrLayer().getXForms().size() : 0;
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
          switch (columnIndex) {
            case COL_TRANSFORM:
              return String.valueOf(rowIndex + 1);
            case COL_VARIATIONS: {
              XForm xForm = getCurrLayer().getXForms().get(rowIndex);
              return getXFormCaption(xForm);
            }
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
    if (getCurrLayer() != null) {
      ImagePanel panels[] = { getPalettePanel(), getColorChooserPalettePanel() };
      for (ImagePanel imgPanel : panels) {
        int width = imgPanel.getWidth();
        int height = imgPanel.getHeight();
        if (width >= 16 && height >= 4) {
          SimpleImage img = new RGBPaletteRenderer().renderHorizPalette(getCurrLayer().getPalette(), width, height);
          imgPanel.setImage(img);
        }
        imgPanel.getParent().validate();
        imgPanel.repaint();
      }
    }
  }

  private void flameSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh || getCurrFlame() == null)
      return;
    noRefresh = true;
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = getCurrFlame().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(getCurrFlame(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(getCurrFlame(), Tools.FTOI(propValue));
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

  private void shadingInfoTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, int pIdx) {
    if (noRefresh || getCurrFlame() == null)
      return;
    ShadingInfo shadingInfo = getCurrFlame().getShadingInfo();
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
    if (noRefresh || getCurrFlame() == null)
      return;
    ShadingInfo shadingInfo = getCurrFlame().getShadingInfo();
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

  private void flameTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (noRefresh) {
      return;
    }
    if (getCurrFlame() == null) {
      return;
    }
    noRefresh = true;
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = getCurrFlame().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(getCurrFlame(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(getCurrFlame(), Tools.FTOI(propValue));
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
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
        Flame flame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        if (data.layerAppendBtn.isSelected() && getCurrFlame() != null) {
          if (appendToFlame(flame)) {
            refreshUI();
            showStatusMessage(getCurrFlame(), "opened from disc and added as new layers");
          }
        }
        else {
          setCurrFlame(flame);
          getCurrFlame().setLastFilename(file.getName());
          undoManager.initUndoStack(getCurrFlame());

          for (int i = flames.size() - 1; i >= 1; i--) {
            randomBatch.add(0, new FlameThumbnail(flames.get(i), null));
          }
          updateThumbnails();
          setupProfiles(getCurrFlame());
          refreshUI();
          showStatusMessage(getCurrFlame(), "opened from disc");
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

  public void gammaREd_changed() {
    flameTextFieldChanged(data.gammaSlider, data.gammaREd, "gamma", SLIDER_SCALE_GAMMA);
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

  public void gammaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaSlider, data.gammaREd, "gamma", SLIDER_SCALE_GAMMA);
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
    if (getCurrFlame() != null) {
      RandomRGBPaletteGenerator generator = new RandomRGBPaletteGenerator();
      data.paletteKeyFrames = generator.generateKeyFrames(Integer.parseInt(data.paletteRandomPointsREd.getText()));
      refreshPaletteColorsTable();
      RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(data.paletteKeyFrames, data.paletteFadeColorsCBx.isSelected());
      saveUndoPoint();
      getCurrLayer().setPalette(palette);
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
          showStatusMessage(getCurrFlame(), "flame saved to disc");
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
    if (getCurrLayer() != null) {
      int row = data.transformationsTable.getSelectedRow();
      if (row >= 0 && row < getCurrLayer().getXForms().size()) {
        xForm = getCurrLayer().getXForms().get(row);
      }
      else if (row >= getCurrLayer().getXForms().size() && row < (getCurrLayer().getXForms().size() + getCurrLayer().getFinalXForms().size())) {
        xForm = getCurrLayer().getFinalXForms().get(row - getCurrLayer().getXForms().size());
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
    boolean enabled = getCurrFlame() != null;
    data.editTransformCaptionButton.setEnabled(enabled);
    data.editFlameTileButton.setEnabled(enabled);
    data.snapShotButton.setEnabled(enabled);
    data.qSaveButton.setEnabled(enabled);
    data.quickMutationButton.setEnabled(enabled);
    data.dancingFlamesButton.setEnabled(enabled);
    data.movieButton.setEnabled(enabled);
    data.transformSlowButton.setEnabled(enabled);
    data.transparencyButton.setEnabled(enabled);
    data.darkTrianglesButton.setEnabled(enabled);
    data.randomizeButton.setEnabled(enabled);
    enableUndoControls();
    enableJobRenderControls();
    getKeyFramesController().enableControls();
    getJwfScriptController().enableControls();
    getGradientController().enableControls();
    getFlameBrowserController().enableControls();
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

    data.addTransformationButton.setEnabled(getCurrFlame() != null);
    data.addLinkedTransformationButton.setEnabled(enabled && getCurrLayer() != null && getCurrLayer().getXForms().indexOf(xForm) >= 0);
    data.duplicateTransformationButton.setEnabled(enabled);
    data.deleteTransformationButton.setEnabled(enabled);
    data.addFinalTransformationButton.setEnabled(getCurrFlame() != null);

    data.affineEditPostTransformButton.setEnabled(getCurrFlame() != null);
    data.affineEditPostTransformSmallButton.setEnabled(getCurrFlame() != null);
    data.mouseEditZoomInButton.setEnabled(getCurrFlame() != null);
    data.mouseEditZoomOutButton.setEnabled(getCurrFlame() != null);
    data.toggleVariationsButton.setEnabled(getCurrFlame() != null);

    data.affineC00REd.setEditable(enabled);
    data.affineC01REd.setEditable(enabled);
    data.affineC10REd.setEditable(enabled);
    data.affineC11REd.setEditable(enabled);
    data.affineC20REd.setEditable(enabled);
    data.affineC21REd.setEditable(enabled);
    data.affineResetTransformButton.setEnabled(enabled);
    data.editTransformCaptionButton.setEnabled(enabled);

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
      }
      else {
        data.layerWeightEd.setValue(pLayer.getWeight());
        data.layerVisibleBtn.setSelected(pLayer.isVisible());
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
    if (flamePanel.getMouseDragOperation() != MouseDragOperation.MOVE_TRIANGLE && flamePanel.getMouseDragOperation() != MouseDragOperation.ROTATE_TRIANGLE && flamePanel.getMouseDragOperation() != MouseDragOperation.SCALE_TRIANGLE && flamePanel.getMouseDragOperation() != MouseDragOperation.POINTS) {
      flamePanel.setMouseDragOperation(MouseDragOperation.MOVE_TRIANGLE);
      flamePanel.setDrawTriangles(true);
      data.mouseTransformMoveTrianglesButton.setSelected(true);
      data.mouseTransformRotateTrianglesButton.setSelected(false);
      data.mouseTransformScaleTrianglesButton.setSelected(false);
      data.mouseTransformEditPointsButton.setSelected(false);
      data.mouseTransformEditGradientButton.setSelected(false);
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
      FlameRenderer renderer = new FlameRenderer(renderFlame, prefs, false, false);
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

  private final int IMG_WIDTH = 90;
  private final int IMG_HEIGHT = 68;
  private final int BORDER_SIZE = 8;

  public void updateThumbnails() {
    if (data.randomBatchScrollPane != null) {
      data.randomBatchPanel.remove(data.randomBatchScrollPane);
      data.randomBatchScrollPane = null;
    }
    int panelWidth = IMG_WIDTH + 2 * BORDER_SIZE;
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
    data.randomBatchScrollPane = new JScrollPane(batchPanel);
    data.randomBatchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    data.randomBatchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    data.randomBatchPanel.add(data.randomBatchScrollPane, BorderLayout.CENTER);
    data.randomBatchPanel.validate();
  }

  public boolean createRandomBatch(int pCount, String pGeneratorname, RandomBatchQuality pQuality) {
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
    //int palettePoints = Integer.parseInt(paletteRandomPointsREd.getText());
    for (int i = 0; i < maxCount; i++) {
      int palettePoints = 7 + (int) (Math.random() * 34.0);
      boolean fadePaletteColors = Math.random() > 0.25;
      RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH / 2, IMG_HEIGHT / 2, prefs, randGen, palettePoints, fadePaletteColors, pQuality);
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

              case IMAGE_FILE: {
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
                RessourceDialog dlg = new RessourceDialog(SwingUtilities.getWindowAncestor(centerPanel), prefs, errorHandler);
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
    flameSliderChanged(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasRadiusSlider_changed() {
    flameSliderChanged(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasAmountREd_changed() {
    flameTextFieldChanged(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasRadiusREd_changed() {
    flameTextFieldChanged(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", SLIDER_SCALE_COLOR);
  }

  private void setRelWeight(double pValue) {
    XForm xForm = getCurrXForm();
    if (xForm != null && getCurrLayer() != null && getCurrLayer().getFinalXForms().indexOf(xForm) < 0) {
      int row = data.relWeightsTable.getSelectedRow();
      if (row >= 0 && row < getCurrLayer().getXForms().size()) {
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

    if (prefs.getTinaDefaultDEMaxRadius() < EPSILON) {
      flame.setDeFilterEnabled(false);
    }
    else {
      flame.setDeFilterEnabled(true);
      flame.setDeFilterMaxRadius(prefs.getTinaDefaultDEMaxRadius());
    }
    RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(Integer.parseInt(data.paletteRandomPointsREd.getText()), data.paletteFadeColorsCBx.isSelected());
    flame.getFirstLayer().setPalette(palette);
    setCurrFlame(flame);
    undoManager.initUndoStack(getCurrFlame());
    //    randomBatch.add(0, new FlameThumbnail(getCurrFlame(), null));
    //    updateThumbnails();
    //    refreshUI();
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
      data.cameraRollREd.setText(Tools.doubleToString(getCurrFlame().getCamRoll()));
      data.cameraRollSlider.setValue(Tools.FTOI(getCurrFlame().getCamRoll()));

      data.cameraPitchREd.setText(Tools.doubleToString(getCurrFlame().getCamPitch()));
      data.cameraPitchSlider.setValue(Tools.FTOI(getCurrFlame().getCamPitch()));

      data.cameraYawREd.setText(Tools.doubleToString(getCurrFlame().getCamYaw()));
      data.cameraYawSlider.setValue(Tools.FTOI(getCurrFlame().getCamYaw()));

      data.cameraCentreXREd.setText(Tools.doubleToString(getCurrFlame().getCentreX()));
      data.cameraCentreXSlider.setValue(Tools.FTOI(getCurrFlame().getCentreX() * SLIDER_SCALE_CENTRE));

      data.cameraCentreYREd.setText(Tools.doubleToString(getCurrFlame().getCentreY()));
      data.cameraCentreYSlider.setValue(Tools.FTOI(getCurrFlame().getCentreY() * SLIDER_SCALE_CENTRE));

      data.pixelsPerUnitREd.setText(Tools.doubleToString(getCurrFlame().getPixelsPerUnit()));
      data.pixelsPerUnitSlider.setValue(Tools.FTOI(getCurrFlame().getPixelsPerUnit()));

      data.focusXREd.setText(Tools.doubleToString(getCurrFlame().getFocusX()));
      data.focusXSlider.setValue(Tools.FTOI(getCurrFlame().getFocusX() * SLIDER_SCALE_ZPOS));

      data.focusYREd.setText(Tools.doubleToString(getCurrFlame().getFocusY()));
      data.focusYSlider.setValue(Tools.FTOI(getCurrFlame().getFocusY() * SLIDER_SCALE_ZPOS));

      data.focusZREd.setText(Tools.doubleToString(getCurrFlame().getFocusZ()));
      data.focusZSlider.setValue(Tools.FTOI(getCurrFlame().getFocusZ() * SLIDER_SCALE_ZPOS));

      data.dimishZREd.setText(Tools.doubleToString(getCurrFlame().getDimishZ()));
      data.dimishZSlider.setValue(Tools.FTOI(getCurrFlame().getDimishZ() * SLIDER_SCALE_ZPOS));
    }
    finally {
      noRefresh = oldNoRefrsh;
    }
  }

  private void afterTriangleSelected(XForm pXForm, int pRow) {
    boolean lastGridRefreshing = gridRefreshing;
    gridRefreshing = true;
    try {
      flamePanel.setSelectedXForm(pXForm);
      data.transformationsTable.getSelectionModel().setSelectionInterval(pRow, pRow);
      refreshXFormUI(pXForm);
      enableXFormControls(pXForm);
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
        if (xForm != null) {
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
              centerPanel.getParent().validate();
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
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformMoveTrianglesButton.isSelected() ? MouseDragOperation.MOVE_TRIANGLE : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE);
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
          showStatusMessage(getCurrFlame(), "opened from clipboard");
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
        showStatusMessage(pFlame, "added as new layers");
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
        showStatusMessage(getCurrFlame(), "imported into editor");
      }
      else {
        _currFlame = pFlame;
        undoManager.initUndoStack(_currFlame);
        setupProfiles(getCurrFlame());
        showStatusMessage(getCurrFlame(), "imported into editor");
      }
    }
    refreshUI();
  }

  protected Flame exportFlame() {
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
        showStatusMessage(getCurrFlame(), "flame saved to clipboard");
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
          addFlameToBatchRenderer(file.getPath(), false);
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

  public void addFlameToBatchRenderer(String filename, boolean refreshTable) {
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
      if (refreshTable) {
        refreshRenderBatchJobsTable();
      }
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
        data.mouseTransformRotateTrianglesButton.setSelected(false);
        data.mouseTransformScaleTrianglesButton.setSelected(false);
        data.mouseTransformMoveTrianglesButton.setSelected(false);
        data.mouseTransformEditFocusPointButton.setSelected(false);
        data.mouseTransformEditPointsButton.setSelected(false);
        data.mouseTransformEditGradientButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformEditViewButton.isSelected() ? MouseDragOperation.VIEW : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getMouseDragOperation() == MouseDragOperation.MOVE_TRIANGLE);
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
        undoManager.saveUndoPoint(getCurrFlame());
        undoManager.undo(getCurrFlame());
        registerToEditor(getCurrFlame(), getCurrLayer());
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
    if (getCurrFlame() != null) {
      undoManager.setEnabled(false);
      try {
        int txRow = data.transformationsTable.getSelectedRow();
        undoManager.redo(getCurrFlame());
        registerToEditor(getCurrFlame(), getCurrLayer());
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
        getCurrLayer().setPalette(palette);
        refreshPaletteColorsTable();
        refreshPaletteUI(palette);
        refreshFlameImage(false);
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
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

  public void showStatusMessage(Flame pFlame, String pStatus) {
    if (pFlame == null)
      return;
    String prefix;
    if (pFlame.getName() != null && pFlame.getName().length() > 0) {
      prefix = "Flame \"" + pFlame.getName() + "\"";
    }
    else {
      prefix = "Unnamed flame";
    }
    if (pFlame.getLastFilename() != null && pFlame.getLastFilename().length() > 0) {
      prefix += " (" + pFlame.getLastFilename() + ") ";
    }
    else {
      prefix += " ";
    }
    tinaFrame.setTitle(prefix + (pStatus != null && pStatus.length() > 0 ? ": " + pStatus : ""));
  }

  public void showStatusMessage(RGBPalette pGradient, String pStatus) {
    if (pGradient == null)
      return;
    String prefix;
    if (pGradient.getFlam3Name() != null && pGradient.getFlam3Name().length() > 0) {
      prefix = "Gradient \"" + pGradient.getFlam3Name() + "\"";
    }
    else {
      prefix = "Unnamed gradient";
    }
    tinaFrame.setTitle(prefix + (pStatus != null && pStatus.length() > 0 ? ": " + pStatus : ""));
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
        showStatusMessage(getCurrFlame(), "quicksave <" + new File(filename).getName() + "> saved");
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
          List<Flame> flames = new FlameReader(prefs).readFlames(job.getFlameFilename());
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
      batchPreviewFlamePanel = new FlamePanel(img, 0, 0, data.batchPreviewRootPanel.getWidth(), getBatchRenderPreviewFlameHolder(), null);
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

        FlameRenderer renderer = new FlameRenderer(flame, prefs, data.toggleTransparencyButton.isSelected(), false);
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
        showStatusMessage(flame, "Title changed");
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
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformRotateTrianglesButton.isSelected() ? MouseDragOperation.ROTATE_TRIANGLE : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getMouseDragOperation() == MouseDragOperation.ROTATE_TRIANGLE);
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
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformScaleTrianglesButton.isSelected() ? MouseDragOperation.SCALE_TRIANGLE : MouseDragOperation.NONE);
          flamePanel.setDrawTriangles(flamePanel.getMouseDragOperation() == MouseDragOperation.SCALE_TRIANGLE);
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

  public KeyFramesController getKeyFramesController() {
    return keyFramesController;
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
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(data.mouseTransformEditGradientButton.isSelected() ? MouseDragOperation.GRADIENT : MouseDragOperation.NONE);
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
    if (getFlamePanel().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
      return getFlamePanel().getGradientFrom();
    }
    else {
      return dfltGradientSelection.getFrom();
    }
  }

  @Override
  public int getTo() {
    if (getFlamePanel().getMouseDragOperation() == MouseDragOperation.GRADIENT) {
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
    MutationType allMutationTypes[] = { MutationType.ADD_TRANSFORM, MutationType.ADD_VARIATION, MutationType.CHANGE_WEIGHT, MutationType.GRADIENT_POSITION, MutationType.AFFINE, MutationType.RANDOM_GRADIENT, MutationType.RANDOM_PARAMETER };
    int[] allCounts = { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 5, 6, 7, 8, 9 };
    int count = allCounts[(int) (Math.random() * allCounts.length)];
    List<MutationType> res = new ArrayList<MutationType>();
    for (int i = 0; i < count; i++) {
      res.add(allMutationTypes[(int) (Math.random() * allMutationTypes.length)]);
    }
    return res;
  }

  private SimpleImage renderRandomizedFlame(Flame pFlame, Dimension pImgSize) {
    int imageWidth = pImgSize.width, imageHeight = pImgSize.height;
    RenderInfo info = new RenderInfo(imageWidth, imageHeight);
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
    RandomRGBPaletteGenerator generator = new RandomRGBPaletteGenerator();
    List<RGBColor> paletteKeyFrames = generator.generateKeyFrames(Integer.parseInt(data.paletteRandomPointsREd.getText()));
    RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(paletteKeyFrames, data.paletteFadeColorsCBx.isSelected());
    layer.setPalette(palette);
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
}
