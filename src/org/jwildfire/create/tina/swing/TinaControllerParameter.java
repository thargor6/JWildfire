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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.swing.ErrorHandler;

public class TinaControllerParameter {
  public TinaInternalFrame pTinaFrame;
  public ErrorHandler pErrorHandler;
  public Prefs pPrefs;
  public JPanel pCenterPanel;
  public JWFNumberField pCameraRollREd;
  public JSlider pCameraRollSlider;
  public JWFNumberField pCameraPitchREd;
  public JSlider pCameraPitchSlider;
  public JWFNumberField pCameraYawREd;
  public JSlider pCameraYawSlider;
  public JWFNumberField pCameraPerspectiveREd;
  public JSlider pCameraPerspectiveSlider;
  public JWFNumberField pCameraCentreXREd;
  public JSlider pCameraCentreXSlider;
  public JWFNumberField pCameraCentreYREd;
  public JSlider pCameraCentreYSlider;
  public JWFNumberField pCameraZoomREd;
  public JSlider pCameraZoomSlider;
  public JCheckBox pNewDOFCBx;
  public JWFNumberField pFocusXREd;
  public JSlider pFocusXSlider;
  public JWFNumberField pFocusYREd;
  public JSlider pFocusYSlider;
  public JWFNumberField pFocusZREd;
  public JSlider pFocusZSlider;
  public JWFNumberField pDimishZREd;
  public JSlider pDimishZSlider;
  public JWFNumberField pCameraDOFREd;
  public JSlider pCameraDOFSlider;
  public JWFNumberField pCameraDOFAreaREd;
  public JSlider pCameraDOFAreaSlider;
  public JWFNumberField pCameraDOFExponentREd;
  public JSlider pCameraDOFExponentSlider;
  public JWFNumberField pCamZREd;
  public JSlider pCamZSlider;
  public JWFNumberField pPixelsPerUnitREd;
  public JSlider pPixelsPerUnitSlider;
  public JWFNumberField pBrightnessREd;
  public JSlider pBrightnessSlider;
  public JWFNumberField pContrastREd;
  public JSlider pContrastSlider;
  public JWFNumberField whiteLevelREd;
  public JSlider whiteLevelSlider;
  public JWFNumberField pGammaREd;
  public JSlider pGammaSlider;
  public JWFNumberField pVibrancyREd;
  public JSlider pVibrancySlider;
  public JWFNumberField saturationREd;
  public JSlider saturationSlider;
  public JWFNumberField pFilterRadiusREd;
  public JSlider pFilterRadiusSlider;
  public JComboBox pFilterKernelCmb;
  public JWFNumberField pGammaThresholdREd;
  public JSlider pGammaThresholdSlider;
  public JCheckBox pBGTransparencyCBx;
  public JTextField pPaletteRandomPointsREd;
  public JComboBox paletteRandomGeneratorCmb;
  public JCheckBox paletteFadeColorsCBx;
  public JPanel pPaletteImgPanel;
  public JPanel pColorChooserPaletteImgPanel;
  public JWFNumberField pPaletteShiftREd;
  public JSlider pPaletteShiftSlider;
  public JWFNumberField pPaletteRedREd;
  public JSlider pPaletteRedSlider;
  public JWFNumberField pPaletteGreenREd;
  public JSlider pPaletteGreenSlider;
  public JWFNumberField pPaletteBlueREd;
  public JSlider pPaletteBlueSlider;
  public JWFNumberField pPaletteHueREd;
  public JSlider pPaletteHueSlider;
  public JWFNumberField pPaletteSaturationREd;
  public JSlider pPaletteSaturationSlider;
  public JWFNumberField pPaletteContrastREd;
  public JSlider pPaletteContrastSlider;
  public JWFNumberField pPaletteGammaREd;
  public JSlider pPaletteGammaSlider;
  public JWFNumberField pPaletteBrightnessREd;
  public JSlider pPaletteBrightnessSlider;
  public JWFNumberField pPaletteSwapRGBREd;
  public JSlider pPaletteSwapRGBSlider;
  public JWFNumberField pPaletteFrequencyREd;
  public JSlider pPaletteFrequencySlider;
  public JWFNumberField pPaletteBlurREd;
  public JSlider pPaletteBlurSlider;
  public JButton pPaletteInvertBtn;
  public JButton pPaletteReverseBtn;
  public JTable pTransformationsTable;
  public JWFNumberField pAffineC00REd;
  public JWFNumberField pAffineC01REd;
  public JWFNumberField pAffineC10REd;
  public JWFNumberField pAffineC11REd;
  public JWFNumberField pAffineC20REd;
  public JWFNumberField pAffineC21REd;
  public JWFNumberField pAffineRotateAmountREd;
  public JWFNumberField pAffineScaleAmountREd;
  public JWFNumberField affineMoveHorizAmountREd;
  public JWFNumberField affineMoveVertAmountREd;
  public JButton pAffineRotateLeftButton;
  public JButton pAffineRotateRightButton;
  public JButton pAffineEnlargeButton;
  public JButton pAffineShrinkButton;
  public JButton pAffineMoveUpButton;
  public JButton pAffineMoveLeftButton;
  public JButton pAffineMoveRightButton;
  public JButton pAffineMoveDownButton;
  public JButton pAddTransformationButton;
  public JButton pAddLinkedTransformationButton;
  public JButton pDuplicateTransformationButton;
  public JButton pDeleteTransformationButton;
  public JButton pAddFinalTransformationButton;
  public JPanel pRandomBatchPanel;
  public TinaNonlinearControlsRow[] pTinaNonlinearControlsRows;
  public VariationControlsDelegate[] variationControlsDelegates;
  public JWFNumberField pXFormColorREd;
  public JSlider pXFormColorSlider;
  public JWFNumberField pXFormSymmetryREd;
  public JSlider pXFormSymmetrySlider;
  public JWFNumberField pXFormModGammaREd;
  public JSlider pXFormModGammaSlider;
  public JWFNumberField pXFormModGammaSpeedREd;
  public JSlider pXFormModGammaSpeedSlider;
  public JWFNumberField pXFormModContrastREd;
  public JSlider pXFormModContrastSlider;
  public JWFNumberField pXFormModContrastSpeedREd;
  public JSlider pXFormModContrastSpeedSlider;
  public JWFNumberField pXFormModSaturationREd;
  public JSlider pXFormModSaturationSlider;
  public JWFNumberField pXFormModSaturationSpeedREd;
  public JSlider pXFormModSaturationSpeedSlider;
  public JWFNumberField pXFormOpacityREd;
  public JSlider pXFormOpacitySlider;
  public JComboBox pXFormDrawModeCmb;
  public JTable pRelWeightsTable;
  public JButton pRelWeightsZeroButton;
  public JButton pRelWeightsOneButton;
  public JWFNumberField pRelWeightREd;
  public JToggleButton mouseTransformMoveTrianglesButton;
  public JToggleButton mouseTransformRotateTrianglesButton;
  public JToggleButton mouseTransformScaleTrianglesButton;
  public JToggleButton pMouseTransformEditFocusPointButton;
  public JToggleButton mouseTransformEditGradientButton;
  public JToggleButton pMouseTransformShearButton;
  public JToggleButton pMouseTransformViewButton;
  public JToggleButton pAffineEditPostTransformButton;
  public JToggleButton pAffineEditPostTransformSmallButton;
  public JToggleButton mouseTransformEditTriangleViewButton;
  public JButton affineRotateEditMotionCurveBtn;
  public JButton affineScaleEditMotionCurveBtn;
  public JButton pAffineResetTransformButton;
  public JTable pCreatePaletteColorsTable;
  public JComboBox pShadingCmb;
  public JWFNumberField pShadingAmbientREd;
  public JSlider pShadingAmbientSlider;
  public JWFNumberField pShadingDiffuseREd;
  public JSlider pShadingDiffuseSlider;
  public JWFNumberField pShadingPhongREd;
  public JSlider pShadingPhongSlider;
  public JWFNumberField pShadingPhongSizeREd;
  public JSlider pShadingPhongSizeSlider;
  public JComboBox pShadingLightCmb;
  public JWFNumberField pShadingLightXREd;
  public JSlider pShadingLightXSlider;
  public JWFNumberField pShadingLightYREd;
  public JSlider pShadingLightYSlider;
  public JWFNumberField pShadingLightZREd;
  public JSlider pShadingLightZSlider;
  public JWFNumberField pShadingLightRedREd;
  public JSlider pShadingLightRedSlider;
  public JWFNumberField pShadingLightGreenREd;
  public JSlider pShadingLightGreenSlider;
  public JWFNumberField pShadingLightBlueREd;
  public JSlider pShadingLightBlueSlider;
  public JWFNumberField pShadingDistanceColorRadiusREd;
  public JSlider pShadingDistanceColorRadiusSlider;
  public JWFNumberField pShadingDistanceColorScaleREd;
  public JSlider pShadingDistanceColorScaleSlider;
  public JWFNumberField pShadingDistanceColorExponentREd;
  public JSlider pShadingDistanceColorExponentSlider;
  public JWFNumberField pShadingDistanceColorOffsetXREd;
  public JSlider pShadingDistanceColorOffsetXSlider;
  public JWFNumberField pShadingDistanceColorOffsetYREd;
  public JSlider pShadingDistanceColorOffsetYSlider;
  public JWFNumberField pShadingDistanceColorOffsetZREd;
  public JSlider pShadingDistanceColorOffsetZSlider;
  public JWFNumberField pShadingDistanceColorStyleREd;
  public JSlider pShadingDistanceColorStyleSlider;
  public JWFNumberField pShadingDistanceColorCoordinateREd;
  public JSlider pShadingDistanceColorCoordinateSlider;
  public JWFNumberField pShadingDistanceColorShiftREd;
  public JSlider pShadingDistanceColorShiftSlider;
  public JToggleButton pMouseTransformSlowButton;
  public JTable pRenderBatchJobsTable;
  public JPanel pBatchPreviewRootPanel;
  public JProgressBar pBatchRenderJobProgressBar;
  public JProgressBar pBatchRenderTotalProgressBar;
  public ProgressUpdater pJobProgressUpdater;
  public JButton pBatchRenderAddFilesButton;
  public JButton pBatchRenderFilesMoveDownButton;
  public JButton pBatchRenderFilesMoveUpButton;
  public JButton pBatchRenderFilesRemoveButton;
  public JButton pBatchRenderFilesRemoveAllButton;
  public JButton pBatchRenderStartButton;
  public JTabbedPane pRootTabbedPane;
  public JButton pAffineFlipHorizontalButton;
  public JButton pAffineFlipVerticalButton;
  public JWFNumberField pShadingBlurRadiusREd;
  public JSlider pShadingBlurRadiusSlider;
  public JWFNumberField pShadingBlurFadeREd;
  public JSlider pShadingBlurFadeSlider;
  public JWFNumberField pShadingBlurFallOffREd;
  public JSlider pShadingBlurFallOffSlider;
  public JToggleButton pAffineScaleXButton;
  public JToggleButton pAffineScaleYButton;
  public JPanel pGradientLibraryPanel;
  public JTextPane pHelpPane;
  public JTextPane apophysisHintsPane;
  public JToggleButton pToggleVariationsButton;
  public JToggleButton pToggleTransparencyButton;
  public JToggleButton pAffinePreserveZButton;
  public JComboBox pQualityProfileCmb;
  public JComboBox pResolutionProfileCmb;
  public JComboBox pBatchQualityProfileCmb;
  public JComboBox pBatchResolutionProfileCmb;
  public JComboBox pInteractiveResolutionProfileCmb;
  public JComboBox pSWFAnimatorResolutionProfileCmb;
  public JComboBox swfAnimatorQualityProfileCmb;
  public JButton pRenderFlameButton;
  public JButton pRenderMainButton;
  public JButton pAppendToMovieButton;
  public JWFNumberField pTransformationWeightREd;
  public JButton pUndoButton;
  public JButton pRedoButton;
  public JWFNumberField pXFormAntialiasAmountREd;
  public JSlider pXFormAntialiasAmountSlider;
  public JWFNumberField pXFormAntialiasRadiusREd;
  public JSlider pXFormAntialiasRadiusSlider;
  public JPanel pDancingFlamesFlamePnl;
  public JPanel pDancingFlamesGraph1Pnl;
  public JButton pDancingFlamesLoadSoundBtn;
  public JButton pDancingFlamesAddFromClipboardBtn;
  public JButton pDancingFlamesAddFromEditorBtn;
  public JButton pDancingFlamesAddFromDiscBtn;
  public JWFNumberField pDancingFlamesRandomCountIEd;
  public JButton pDancingFlamesGenRandFlamesBtn;
  public JComboBox pDancingFlamesRandomGenCmb;
  public JPanel pDancingFlamesPoolFlamePreviewPnl;
  public JSlider pDancingFlamesBorderSizeSlider;
  public JButton pDancingFlamesFlameToEditorBtn;
  public JButton pDancingFlamesDeleteFlameBtn;
  public JTextField pDancingFlamesFramesPerSecondIEd;
  public JTextField pDancingFlamesMorphFrameCountIEd;
  public JButton pDancingFlamesStartShowButton;
  public JButton pDancingFlamesStopShowButton;
  public JCheckBox pDancingFlamesDoRecordCBx;
  public JComboBox pDancingFlamesFlamesCmb;
  public JCheckBox pDancingFlamesDrawTrianglesCBx;
  public JCheckBox pDancingFlamesDrawFFTCBx;
  public JCheckBox pDancingFlamesDrawFPSCBx;
  public JTree pDancingFlamesFlamePropertiesTree;
  public JPanel pDancingFlamesMotionPropertyPnl;
  public JTable pDancingFlamesMotionTable;
  public JComboBox pDancingFlamesAddMotionCmb;
  public JButton pDancingFlamesAddMotionBtn;
  public JButton pDancingFlamesDeleteMotionBtn;
  public JButton pDancingFlamesLinkMotionBtn;
  public JButton pDancingFlamesUnlinkMotionBtn;
  public JComboBox pDancingFlamesCreateMotionsCmb;
  public JButton pDancingFlamesClearMotionsBtn;
  public JButton pDancingFlamesLoadProjectBtn;
  public JButton pDancingFlamesSaveProjectBtn;
  public JTable pDancingFlamesMotionLinksTable;
  public JPanel mutaGen01Pnl;
  public JPanel mutaGen02Pnl;
  public JPanel mutaGen03Pnl;
  public JPanel mutaGen04Pnl;
  public JPanel mutaGen05Pnl;
  public JPanel mutaGen06Pnl;
  public JPanel mutaGen07Pnl;
  public JPanel mutaGen08Pnl;
  public JPanel mutaGen09Pnl;
  public JPanel mutaGen10Pnl;
  public JPanel mutaGen11Pnl;
  public JPanel mutaGen12Pnl;
  public JPanel mutaGen13Pnl;
  public JPanel mutaGen14Pnl;
  public JPanel mutaGen15Pnl;
  public JPanel mutaGen16Pnl;
  public JPanel mutaGen17Pnl;
  public JPanel mutaGen18Pnl;
  public JPanel mutaGen19Pnl;
  public JPanel mutaGen20Pnl;
  public JPanel mutaGen21Pnl;
  public JPanel mutaGen22Pnl;
  public JPanel mutaGen23Pnl;
  public JPanel mutaGen24Pnl;
  public JPanel mutaGen25Pnl;
  public JButton mutaGenLoadFlameFromEditorBtn;
  public JButton mutaGenLoadFlameFromFileBtn;
  public JProgressBar mutaGenProgressBar;
  public JWFNumberField mutaGenAmountREd;
  public JComboBox mutaGenHorizontalTrend1Cmb;
  public JComboBox mutaGenHorizontalTrend2Cmb;
  public JComboBox mutaGenVerticalTrend1Cmb;
  public JComboBox mutaGenVerticalTrend2Cmb;
  public JButton mutaGenBackBtn;
  public JButton mutaGenForwardBtn;
  public JTextPane mutaGenHintPane;
  public JButton mutaGenSaveFlameToEditorBtn;
  public JButton mutaGenSaveFlameToFileBtn;
  public JButton editTransformCaptionButton;
  public JButton editFlameTileButton;
  public JButton snapShotButton;
  public JButton qSaveButton;
  public JButton quickMutationButton;
  public JButton bokehButton;
  public JButton dancingFlamesButton;
  public JButton movieButton;
  public JToggleButton transformSlowButton;
  public JToggleButton transparencyButton;
  public JTree scriptTree;
  public JTextArea scriptDescriptionTextArea;
  public JTextArea scriptTextArea;
  public JButton compileScriptButton;
  public JButton saveScriptBtn;
  public JButton revertScriptBtn;
  public JButton rescanScriptsBtn;
  public JButton newScriptBtn;
  public JButton newScriptFromFlameBtn;
  public JButton deleteScriptBtn;
  public JButton scriptRenameBtn;
  public JButton scriptDuplicateBtn;
  public JButton scriptRunBtn;
  public JTree gradientLibTree;
  public JButton gradientLibraryRescanBtn;
  public JButton gradientLibraryNewFolderBtn;
  public JButton gradientLibraryRenameFolderBtn;
  public JList gradientsList;
  public JButton backgroundColorIndicatorBtn;
  public JButton randomizeButton;
  public JTree flameBrowserTree;
  public JPanel flameBrowersImagesPnl;
  public JButton flameBrowserRefreshBtn;
  public JButton flameBrowserChangeFolderBtn;
  public JButton flameBrowserToEditorBtn;
  public JButton flameBrowserToBatchEditorBtn;
  public JButton flameBrowserToMeshGenBtn;
  public JButton flameBrowserDeleteBtn;
  public JButton flameBrowserRenameBtn;
  public JButton flameBrowserCopyToBtn;
  public JButton flameBrowserMoveToBtn;
  public JButton dancingFlamesReplaceFlameFromEditorBtn;
  public JButton dancingFlamesRenameFlameBtn;
  public JButton dancingFlamesRenameMotionBtn;
  public JCheckBox dancingFlamesMutedCBx;
  public JWFNumberField layerWeightEd;
  public JButton layerAddBtn;
  public JButton layerDuplicateBtn;
  public JButton layerDeleteBtn;
  public JTable layersTable;
  public JToggleButton layerVisibleBtn;
  public JToggleButton layerAppendBtn;
  public JToggleButton layerPreviewBtn;
  public JButton layerHideOthersBtn;
  public JButton layerShowAllBtn;
  public JWFNumberField keyframesFrameField;
  public JSlider keyframesFrameSlider;
  public JWFNumberField keyframesFrameCountField;
  public JWFNumberField motionBlurLengthField;
  public JSlider motionBlurLengthSlider;
  public JWFNumberField motionBlurTimeStepField;
  public JSlider motionBlurTimeStepSlider;
  public JWFNumberField motionBlurDecayField;
  public JSlider motionBlurDecaySlider;
  public JToggleButton motionCurveEditModeButton;
  public JPanel frameSliderPanel;
  public JLabel keyframesFrameLbl;
  public JLabel keyframesFrameCountLbl;
  public JPanel motionBlurPanel;
  public JComboBox postSymmetryTypeCmb;
  public JWFNumberField postSymmetryDistanceREd;
  public JSlider postSymmetryDistanceSlider;
  public JWFNumberField postSymmetryRotationREd;
  public JSlider postSymmetryRotationSlider;
  public JWFNumberField postSymmetryOrderREd;
  public JSlider postSymmetryOrderSlider;
  public JWFNumberField postSymmetryCentreXREd;
  public JSlider postSymmetryCentreXSlider;
  public JWFNumberField postSymmetryCentreYREd;
  public JSlider postSymmetryCentreYSlider;
  public JComboBox stereo3dModeCmb;
  public JWFNumberField stereo3dAngleREd;
  public JSlider stereo3dAngleSlider;
  public JWFNumberField stereo3dEyeDistREd;
  public JSlider stereo3dEyeDistSlider;
  public JComboBox stereo3dLeftEyeColorCmb;
  public JComboBox stereo3dRightEyeColorCmb;
  public JWFNumberField stereo3dInterpolatedImageCountREd;
  public JSlider stereo3dInterpolatedImageCountSlider;
  public JComboBox stereo3dPreviewCmb;
  public JWFNumberField stereo3dFocalOffsetREd;
  public JSlider stereo3dFocalOffsetSlider;
  public JCheckBox stereo3dSwapSidesCBx;
  public JWFNumberField camPosXREd;
  public JSlider camPosXSlider;
  public JWFNumberField camPosYREd;
  public JSlider camPosYSlider;
  public JWFNumberField camPosZREd;
  public JSlider camPosZSlider;
  public JToggleButton toggleDrawGridButton;
  public JToggleButton toggleDrawGuidesButton;
  public JSlider editorFractalBrightnessSlider;
  public JToggleButton toggleTriangleWithColorsButton;
  public JComboBox triangleStyleCmb;

  public JButton meshGenFromEditorBtn;
  public JButton meshGenFromClipboardBtn;
  public JButton meshGenLoadFlameBtn;
  public JWFNumberField meshGenSliceCountREd;
  public JWFNumberField meshGenSlicesPerRenderREd;
  public JWFNumberField meshGenRenderWidthREd;
  public JWFNumberField meshGenRenderHeightREd;
  public JWFNumberField meshGenRenderQualityREd;
  public JProgressBar meshGenProgressbar;
  public JButton meshGenGenerateBtn;
  public JPanel meshGenTopViewRootPnl;
  public JPanel meshGenFrontViewRootPnl;
  public JPanel meshGenPerspectiveViewRootPnl;
  public JTextPane meshGenHintPane;
  public JWFNumberField meshGenCentreXREd;
  public JSlider meshGenCentreXSlider;
  public JWFNumberField meshGenCentreYREd;
  public JSlider meshGenCentreYSlider;
  public JWFNumberField meshGenZoomREd;
  public JSlider meshGenZoomSlider;
  public JWFNumberField meshGenZMinREd;
  public JSlider meshGenZMinSlider;
  public JWFNumberField meshGenZMaxREd;
  public JSlider meshGenZMaxSlider;
  public JButton meshGenTopViewRenderBtn;
  public JButton meshGenFrontViewRenderBtn;
  public JButton meshGenPerspectiveViewRenderBtn;
  public JButton meshGenTopViewToEditorBtn;
  public JButton meshGenLoadSequenceBtn;
  public JWFNumberField meshGenSequenceWidthREd;
  public JWFNumberField meshGenSequenceHeightREd;
  public JWFNumberField meshGenSequenceSlicesREd;
  public JWFNumberField meshGenSequenceDownSampleREd;
  public JWFNumberField meshGenSequenceFilterRadiusREd;
  public JProgressBar meshGenGenerateMeshProgressbar;
  public JButton meshGenGenerateMeshBtn;
  public JButton meshGenSequenceFromRendererBtn;
  public JWFNumberField meshGenSequenceThresholdREd;
  public JLabel meshGenSequenceLbl;
  public JPanel meshGenPreviewRootPanel;
  public JCheckBox meshGenAutoPreviewCBx;
  public JButton meshGenPreviewImportLastGeneratedMeshBtn;
  public JButton meshGenPreviewImportFromFileBtn;
  public JButton meshGenClearPreviewBtn;
  public JWFNumberField meshGenPreviewPositionXREd;
  public JWFNumberField meshGenPreviewPositionYREd;
  public JWFNumberField meshGenPreviewSizeREd;
  public JWFNumberField meshGenPreviewScaleZREd;
  public JWFNumberField meshGenPreviewRotateAlphaREd;
  public JWFNumberField meshGenPreviewRotateBetaREd;
  public JWFNumberField meshGenPreviewPointsREd;
  public JWFNumberField meshGenPreviewPolygonsREd;
  public JButton meshGenRefreshPreviewBtn;
  public JButton meshGenPreviewSunflowExportBtn;
  public JWFNumberField meshGenThicknessModREd;
  public JWFNumberField meshGenThicknessSamplesREd;
  public JButton channelMixerResetBtn;
  public JComboBox channelMixerModeCmb;
  public JPanel channelMixerRRRootPanel;
  public JPanel channelMixerRGRootPanel;
  public JPanel channelMixerRBRootPanel;
  public JPanel channelMixerGRRootPanel;
  public JPanel channelMixerGGRootPanel;
  public JPanel channelMixerGBRootPanel;
  public JPanel channelMixerBRRootPanel;
  public JPanel channelMixerBGRootPanel;
  public JPanel channelMixerBBRootPanel;
  public JComboBox meshGenPreFilter1Cmb;
  public JComboBox meshGenPreFilter2Cmb;
  public JWFNumberField meshGenImageStepREd;
  public JButton motionCurvePlayPreviewButton;
  public JComboBox dofDOFShapeCmb;
  public JWFNumberField dofDOFScaleREd;
  public JSlider dofDOFScaleSlider;
  public JWFNumberField dofDOFAngleREd;
  public JSlider dofDOFAngleSlider;
  public JWFNumberField dofDOFFadeREd;
  public JSlider dofDOFFadeSlider;
  public JWFNumberField dofDOFParam1REd;
  public JSlider dofDOFParam1Slider;
  public JLabel dofDOFParam1Lbl;
  public JWFNumberField dofDOFParam2REd;
  public JSlider dofDOFParam2Slider;
  public JLabel dofDOFParam2Lbl;
  public JWFNumberField dofDOFParam3REd;
  public JSlider dofDOFParam3Slider;
  public JLabel dofDOFParam3Lbl;
  public JWFNumberField dofDOFParam4REd;
  public JSlider dofDOFParam4Slider;
  public JLabel dofDOFParam4Lbl;
  public JWFNumberField dofDOFParam5REd;
  public JSlider dofDOFParam5Slider;
  public JLabel dofDOFParam5Lbl;
  public JWFNumberField dofDOFParam6REd;
  public JSlider dofDOFParam6Slider;
  public JLabel dofDOFParam6Lbl;
  public JCheckBox batchRenderOverrideCBx;
  public JButton batchRenderShowImageBtn;
  public JButton resetCameraSettingsBtn;
  public JButton resetDOFSettingsButton;
  public JButton resetBokehOptionsButton;
  public JButton resetColoringOptionsButton;
  public JButton resetAntialiasOptionsButton;
  public JButton resetShadingSettingsBtn;
  public JButton resetStereo3DSettingsBtn;
  public JButton resetPostSymmetrySettingsBtn;
  public JButton resetMotionBlurSettingsBtn;
  public JRadioButton xaosViewAsToBtn;
  public JRadioButton xaosViewAsFromBtn;
  public JPanel previewEastMainPanel;
  public JPanel macroButtonVertPanel;
  public JPanel macroButtonHorizPanel;
  public JButton scriptAddButtonBtn;
  public JTable macroButtonsTable;
  public JButton macroButtonMoveUpBtn;
  public JButton macroButtonMoveDownBtn;
  public JButton macroButtonDeleteBtn;
  public JToggleButton toggleDetachedPreviewButton;
  public JButton gradientResetBtn;
  public JPanel macroButtonHorizRootPanel;
  public JComboBox affineEditPlaneCmb;
  public JWFNumberField gradientColorMapHorizOffsetREd;
  public JSlider gradientColorMapHorizOffsetSlider;
  public JWFNumberField gradientColorMapHorizScaleREd;
  public JSlider gradientColorMapHorizScaleSlider;
  public JWFNumberField gradientColorMapVertOffsetREd;
  public JSlider gradientColorMapVertOffsetSlider;
  public JWFNumberField gradientColorMapVertScaleREd;
  public JSlider gradientColorMapVertScaleSlider;
  public JWFNumberField gradientColorMapLocalColorAddREd;
  public JSlider gradientColorMapLocalColorAddSlider;
  public JWFNumberField gradientColorMapLocalColorScaleREd;
  public JSlider gradientColorMapLocalColorScaleSlider;
  public JWFNumberField flameFPSField;
  public JToggleButton leapMotionToggleButton;
  public JTable leapMotionConfigTable;
  public JComboBox leapMotionHandCmb;
  public JComboBox leapMotionInputChannelCmb;
  public JComboBox leapMotionOutputChannelCmb;
  public JWFNumberField leapMotionIndex1Field;
  public JWFNumberField leapMotionIndex2Field;
  public JWFNumberField leapMotionIndex3Field;
  public JWFNumberField leapMotionInvScaleField;
  public JWFNumberField leapMotionOffsetField;
  public JButton leapMotionAddButton;
  public JButton leapMotionDuplicateButton;
  public JButton leapMotionDeleteButton;
  public JButton leapMotionClearButton;
  public JButton leapMotionResetConfigButton;

  public void setParams1(TinaInternalFrame pTinaFrame, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pCenterPanel, JWFNumberField pCameraRollREd, JSlider pCameraRollSlider, JWFNumberField pCameraPitchREd, JSlider pCameraPitchSlider, JWFNumberField pCameraYawREd, JSlider pCameraYawSlider, JWFNumberField pCameraPerspectiveREd, JSlider pCameraPerspectiveSlider, JWFNumberField pCameraCentreXREd, JSlider pCameraCentreXSlider, JWFNumberField pCameraCentreYREd, JSlider pCameraCentreYSlider, JWFNumberField pCameraZoomREd, JSlider pCameraZoomSlider, JCheckBox pNewDOFCBx, JWFNumberField pFocusXREd, JSlider pFocusXSlider, JWFNumberField pFocusYREd, JSlider pFocusYSlider, JWFNumberField pFocusZREd, JSlider pFocusZSlider, JWFNumberField pDimishZREd, JSlider pDimishZSlider,
      JWFNumberField pCameraDOFREd, JSlider pCameraDOFSlider, JWFNumberField pCameraDOFAreaREd, JSlider pCameraDOFAreaSlider, JWFNumberField pCameraDOFExponentREd, JSlider pCameraDOFExponentSlider, JWFNumberField pCamZREd, JSlider pCamZSlider, JWFNumberField pPixelsPerUnitREd, JSlider pPixelsPerUnitSlider, JWFNumberField pBrightnessREd, JSlider pBrightnessSlider, JWFNumberField pContrastREd, JSlider pContrastSlider, JWFNumberField pGammaREd, JSlider pGammaSlider, JWFNumberField pVibrancyREd, JSlider pVibrancySlider, JWFNumberField pFilterRadiusREd, JSlider pFilterRadiusSlider, JComboBox pFilterKernelCmb,
      JWFNumberField pGammaThresholdREd, JSlider pGammaThresholdSlider, JCheckBox pBGTransparencyCBx, JTextField pPaletteRandomPointsREd, JPanel pPaletteImgPanel, JPanel pColorChooserPaletteImgPanel, JWFNumberField pPaletteShiftREd, JSlider pPaletteShiftSlider, JWFNumberField pPaletteRedREd, JSlider pPaletteRedSlider, JWFNumberField pPaletteGreenREd, JSlider pPaletteGreenSlider, JWFNumberField pPaletteBlueREd, JSlider pPaletteBlueSlider, JWFNumberField pPaletteHueREd, JSlider pPaletteHueSlider,
      JWFNumberField pPaletteSaturationREd, JSlider pPaletteSaturationSlider, JWFNumberField pPaletteContrastREd, JSlider pPaletteContrastSlider, JWFNumberField pPaletteGammaREd, JSlider pPaletteGammaSlider, JWFNumberField pPaletteBrightnessREd, JSlider pPaletteBrightnessSlider, JWFNumberField pPaletteSwapRGBREd, JSlider pPaletteSwapRGBSlider, JWFNumberField pPaletteFrequencyREd, JSlider pPaletteFrequencySlider, JWFNumberField pPaletteBlurREd, JSlider pPaletteBlurSlider, JButton pPaletteInvertBtn, JButton pPaletteReverseBtn, JTable pTransformationsTable, JWFNumberField pAffineC00REd, JWFNumberField pAffineC01REd, JWFNumberField pAffineC10REd, JWFNumberField pAffineC11REd, JWFNumberField pAffineC20REd, JWFNumberField pAffineC21REd, JWFNumberField pAffineRotateAmountREd,
      JWFNumberField pAffineScaleAmountREd, JWFNumberField pAffineMoveHorizAmountREd, JButton pAffineRotateLeftButton, JButton pAffineRotateRightButton, JButton pAffineEnlargeButton, JButton pAffineShrinkButton, JButton pAffineMoveUpButton, JButton pAffineMoveLeftButton, JButton pAffineMoveRightButton, JButton pAffineMoveDownButton, JButton pAddTransformationButton, JButton pAddLinkedTransformationButton, JButton pDuplicateTransformationButton, JButton pDeleteTransformationButton, JButton pAddFinalTransformationButton, JPanel pRandomBatchPanel, TinaNonlinearControlsRow[] pTinaNonlinearControlsRows, JWFNumberField pXFormColorREd, JSlider pXFormColorSlider, JWFNumberField pXFormSymmetryREd, JSlider pXFormSymmetrySlider, JWFNumberField pXFormOpacityREd,
      JSlider pXFormOpacitySlider,
      JComboBox pXFormDrawModeCmb, JTable pRelWeightsTable, JButton pRelWeightsZeroButton, JButton pRelWeightsOneButton, JWFNumberField pRelWeightREd, JToggleButton pMouseTransformMoveButton, JToggleButton pMouseTransformScaleButton, JToggleButton pMouseTransformShearButton, JToggleButton pMouseTransformViewButton, JToggleButton pAffineEditPostTransformButton, JToggleButton pAffineEditPostTransformSmallButton, JButton pAffineResetTransformButton, JTable pCreatePaletteColorsTable, JComboBox pShadingCmb, JWFNumberField pShadingAmbientREd, JSlider pShadingAmbientSlider, JWFNumberField pShadingDiffuseREd, JSlider pShadingDiffuseSlider, JWFNumberField pShadingPhongREd,
      JSlider pShadingPhongSlider, JWFNumberField pShadingPhongSizeREd, JSlider pShadingPhongSizeSlider, JComboBox pShadingLightCmb, JWFNumberField pShadingLightXREd, JSlider pShadingLightXSlider, JWFNumberField pShadingLightYREd, JSlider pShadingLightYSlider, JWFNumberField pShadingLightZREd, JSlider pShadingLightZSlider, JWFNumberField pShadingLightRedREd, JSlider pShadingLightRedSlider, JWFNumberField pShadingLightGreenREd, JSlider pShadingLightGreenSlider, JWFNumberField pShadingLightBlueREd, JSlider pShadingLightBlueSlider, JWFNumberField pShadingDistanceColorRadiusREd, JSlider pShadingDistanceColorRadiusSlider, JWFNumberField pShadingDistanceColorScaleREd, JSlider pShadingDistanceColorScaleSlider, JWFNumberField pShadingDistanceColorExponentREd,
      JSlider pShadingDistanceColorExponentSlider, JWFNumberField pShadingDistanceColorOffsetXREd, JSlider pShadingDistanceColorOffsetXSlider, JWFNumberField pShadingDistanceColorOffsetYREd, JSlider pShadingDistanceColorOffsetYSlider, JWFNumberField pShadingDistanceColorOffsetZREd, JSlider pShadingDistanceColorOffsetZSlider, JToggleButton pMouseTransformSlowButton, JTable pRenderBatchJobsTable, JPanel pBatchPreviewRootPanel, JProgressBar pBatchRenderJobProgressBar, JProgressBar pBatchRenderTotalProgressBar, ProgressUpdater pJobProgressUpdater, JButton pBatchRenderAddFilesButton, JButton pBatchRenderFilesMoveDownButton, JButton pBatchRenderFilesMoveUpButton, JButton pBatchRenderFilesRemoveButton, JButton pBatchRenderFilesRemoveAllButton, JButton pBatchRenderStartButton,
      JTabbedPane pRootTabbedPane, JButton pAffineFlipHorizontalButton, JButton pAffineFlipVerticalButton, JWFNumberField pShadingBlurRadiusREd, JSlider pShadingBlurRadiusSlider, JWFNumberField pShadingBlurFadeREd, JSlider pShadingBlurFadeSlider, JWFNumberField pShadingBlurFallOffREd, JSlider pShadingBlurFallOffSlider, JToggleButton pAffineScaleXButton, JToggleButton pAffineScaleYButton, JPanel pGradientLibraryPanel, JTextPane pHelpPane, JToggleButton pToggleVariationsButton, JToggleButton pToggleTransparencyButton, JToggleButton pAffinePreserveZButton, JComboBox pQualityProfileCmb, JComboBox pResolutionProfileCmb, JComboBox pBatchQualityProfileCmb,
      JComboBox pBatchResolutionProfileCmb, JComboBox pInteractiveResolutionProfileCmb, JComboBox pSWFAnimatorResolutionProfileCmb, JButton pRenderFlameButton, JButton pRenderMainButton, JButton pAppendToMovieButton, JWFNumberField pTransformationWeightREd, JButton pUndoButton, JButton pRedoButton, JWFNumberField pXFormAntialiasAmountREd, JSlider pXFormAntialiasAmountSlider, JWFNumberField pXFormAntialiasRadiusREd, JSlider pXFormAntialiasRadiusSlider, JPanel pDancingFlamesFlamePnl, JPanel pDancingFlamesGraph1Pnl, JButton pDancingFlamesLoadSoundBtn, JButton pDancingFlamesAddFromClipboardBtn, JButton pDancingFlamesAddFromEditorBtn, JButton pDancingFlamesAddFromDiscBtn,
      JWFNumberField pDancingFlamesRandomCountIEd, JButton pDancingFlamesGenRandFlamesBtn, JComboBox pDancingFlamesRandomGenCmb, JPanel pDancingFlamesPoolFlamePreviewPnl, JSlider pDancingFlamesBorderSizeSlider, JButton pDancingFlamesFlameToEditorBtn, JButton pDancingFlamesDeleteFlameBtn, JTextField pDancingFlamesFramesPerSecondIEd, JTextField pDancingFlamesMorphFrameCountIEd, JButton pDancingFlamesStartShowButton, JButton pDancingFlamesStopShowButton, JCheckBox pDancingFlamesDoRecordCBx, JComboBox pDancingFlamesFlamesCmb, JCheckBox pDancingFlamesDrawTrianglesCBx, JCheckBox pDancingFlamesDrawFFTCBx, JCheckBox pDancingFlamesDrawFPSCBx, JTree pDancingFlamesFlamePropertiesTree, JPanel pDancingFlamesMotionPropertyPnl, JTable pDancingFlamesMotionTable, JComboBox pDancingFlamesAddMotionCmb,
      JButton pDancingFlamesAddMotionBtn, JButton pDancingFlamesDeleteMotionBtn) {
    this.pTinaFrame = pTinaFrame;
    this.pErrorHandler = pErrorHandler;
    this.pPrefs = pPrefs;
    this.pCenterPanel = pCenterPanel;
    this.pCameraRollREd = pCameraRollREd;
    this.pCameraRollSlider = pCameraRollSlider;
    this.pCameraPitchREd = pCameraPitchREd;
    this.pCameraPitchSlider = pCameraPitchSlider;
    this.pCameraYawREd = pCameraYawREd;
    this.pCameraYawSlider = pCameraYawSlider;
    this.pCameraPerspectiveREd = pCameraPerspectiveREd;
    this.pCameraPerspectiveSlider = pCameraPerspectiveSlider;
    this.pCameraCentreXREd = pCameraCentreXREd;
    this.pCameraCentreXSlider = pCameraCentreXSlider;
    this.pCameraCentreYREd = pCameraCentreYREd;
    this.pCameraCentreYSlider = pCameraCentreYSlider;
    this.pCameraZoomREd = pCameraZoomREd;
    this.pCameraZoomSlider = pCameraZoomSlider;
    this.pNewDOFCBx = pNewDOFCBx;
    this.pFocusXREd = pFocusXREd;
    this.pFocusXSlider = pFocusXSlider;
    this.pFocusYREd = pFocusYREd;
    this.pFocusYSlider = pFocusYSlider;
    this.pFocusZREd = pFocusZREd;
    this.pFocusZSlider = pFocusZSlider;
    this.pDimishZREd = pDimishZREd;
    this.pDimishZSlider = pDimishZSlider;
    this.pCameraDOFREd = pCameraDOFREd;
    this.pCameraDOFSlider = pCameraDOFSlider;
    this.pCameraDOFAreaREd = pCameraDOFAreaREd;
    this.pCameraDOFAreaSlider = pCameraDOFAreaSlider;
    this.pCameraDOFExponentREd = pCameraDOFExponentREd;
    this.pCameraDOFExponentSlider = pCameraDOFExponentSlider;
    this.pCamZREd = pCamZREd;
    this.pCamZSlider = pCamZSlider;
    this.pPixelsPerUnitREd = pPixelsPerUnitREd;
    this.pPixelsPerUnitSlider = pPixelsPerUnitSlider;
    this.pBrightnessREd = pBrightnessREd;
    this.pBrightnessSlider = pBrightnessSlider;
    this.pContrastREd = pContrastREd;
    this.pContrastSlider = pContrastSlider;
    this.pGammaREd = pGammaREd;
    this.pGammaSlider = pGammaSlider;
    this.pVibrancyREd = pVibrancyREd;
    this.pVibrancySlider = pVibrancySlider;
    this.pFilterRadiusREd = pFilterRadiusREd;
    this.pFilterRadiusSlider = pFilterRadiusSlider;
    this.pFilterKernelCmb = pFilterKernelCmb;
    this.pGammaThresholdREd = pGammaThresholdREd;
    this.pGammaThresholdSlider = pGammaThresholdSlider;
    this.pBGTransparencyCBx = pBGTransparencyCBx;
    this.pPaletteRandomPointsREd = pPaletteRandomPointsREd;
    this.pPaletteImgPanel = pPaletteImgPanel;
    this.pColorChooserPaletteImgPanel = pColorChooserPaletteImgPanel;
    this.pPaletteShiftREd = pPaletteShiftREd;
    this.pPaletteShiftSlider = pPaletteShiftSlider;
    this.pPaletteRedREd = pPaletteRedREd;
    this.pPaletteRedSlider = pPaletteRedSlider;
    this.pPaletteGreenREd = pPaletteGreenREd;
    this.pPaletteGreenSlider = pPaletteGreenSlider;
    this.pPaletteBlueREd = pPaletteBlueREd;
    this.pPaletteBlueSlider = pPaletteBlueSlider;
    this.pPaletteHueREd = pPaletteHueREd;
    this.pPaletteHueSlider = pPaletteHueSlider;
    this.pPaletteSaturationREd = pPaletteSaturationREd;
    this.pPaletteSaturationSlider = pPaletteSaturationSlider;
    this.pPaletteContrastREd = pPaletteContrastREd;
    this.pPaletteContrastSlider = pPaletteContrastSlider;
    this.pPaletteGammaREd = pPaletteGammaREd;
    this.pPaletteGammaSlider = pPaletteGammaSlider;
    this.pPaletteBrightnessREd = pPaletteBrightnessREd;
    this.pPaletteBrightnessSlider = pPaletteBrightnessSlider;
    this.pPaletteSwapRGBREd = pPaletteSwapRGBREd;
    this.pPaletteSwapRGBSlider = pPaletteSwapRGBSlider;
    this.pPaletteFrequencyREd = pPaletteFrequencyREd;
    this.pPaletteFrequencySlider = pPaletteFrequencySlider;
    this.pPaletteBlurREd = pPaletteBlurREd;
    this.pPaletteBlurSlider = pPaletteBlurSlider;
    this.pPaletteInvertBtn = pPaletteInvertBtn;
    this.pPaletteReverseBtn = pPaletteReverseBtn;
    this.pTransformationsTable = pTransformationsTable;
    this.pAffineC00REd = pAffineC00REd;
    this.pAffineC01REd = pAffineC01REd;
    this.pAffineC10REd = pAffineC10REd;
    this.pAffineC11REd = pAffineC11REd;
    this.pAffineC20REd = pAffineC20REd;
    this.pAffineC21REd = pAffineC21REd;
    this.pAffineRotateAmountREd = pAffineRotateAmountREd;
    this.pAffineScaleAmountREd = pAffineScaleAmountREd;
    this.affineMoveHorizAmountREd = pAffineMoveHorizAmountREd;
    this.pAffineRotateLeftButton = pAffineRotateLeftButton;
    this.pAffineRotateRightButton = pAffineRotateRightButton;
    this.pAffineEnlargeButton = pAffineEnlargeButton;
    this.pAffineShrinkButton = pAffineShrinkButton;
    this.pAffineMoveUpButton = pAffineMoveUpButton;
    this.pAffineMoveLeftButton = pAffineMoveLeftButton;
    this.pAffineMoveRightButton = pAffineMoveRightButton;
    this.pAffineMoveDownButton = pAffineMoveDownButton;
    this.pAddTransformationButton = pAddTransformationButton;
    this.pAddLinkedTransformationButton = pAddLinkedTransformationButton;
    this.pDuplicateTransformationButton = pDuplicateTransformationButton;
    this.pDeleteTransformationButton = pDeleteTransformationButton;
    this.pAddFinalTransformationButton = pAddFinalTransformationButton;
    this.pRandomBatchPanel = pRandomBatchPanel;
    this.pTinaNonlinearControlsRows = pTinaNonlinearControlsRows;
    this.pXFormColorREd = pXFormColorREd;
    this.pXFormColorSlider = pXFormColorSlider;
    this.pXFormSymmetryREd = pXFormSymmetryREd;
    this.pXFormSymmetrySlider = pXFormSymmetrySlider;
    this.pXFormOpacityREd = pXFormOpacityREd;
    this.pXFormOpacitySlider = pXFormOpacitySlider;
    this.pXFormDrawModeCmb = pXFormDrawModeCmb;
    this.pRelWeightsTable = pRelWeightsTable;
    this.pRelWeightsZeroButton = pRelWeightsZeroButton;
    this.pRelWeightsOneButton = pRelWeightsOneButton;
    this.pRelWeightREd = pRelWeightREd;
    this.mouseTransformMoveTrianglesButton = pMouseTransformMoveButton;
    this.pMouseTransformEditFocusPointButton = pMouseTransformScaleButton;
    this.pMouseTransformShearButton = pMouseTransformShearButton;
    this.pMouseTransformViewButton = pMouseTransformViewButton;
    this.pAffineEditPostTransformButton = pAffineEditPostTransformButton;
    this.pAffineEditPostTransformSmallButton = pAffineEditPostTransformSmallButton;
    this.pAffineResetTransformButton = pAffineResetTransformButton;
    this.pCreatePaletteColorsTable = pCreatePaletteColorsTable;
    this.pShadingCmb = pShadingCmb;
    this.pShadingAmbientREd = pShadingAmbientREd;
    this.pShadingAmbientSlider = pShadingAmbientSlider;
    this.pShadingDiffuseREd = pShadingDiffuseREd;
    this.pShadingDiffuseSlider = pShadingDiffuseSlider;
    this.pShadingPhongREd = pShadingPhongREd;
    this.pShadingPhongSlider = pShadingPhongSlider;
    this.pShadingPhongSizeREd = pShadingPhongSizeREd;
    this.pShadingPhongSizeSlider = pShadingPhongSizeSlider;
    this.pShadingLightCmb = pShadingLightCmb;
    this.pShadingLightXREd = pShadingLightXREd;
    this.pShadingLightXSlider = pShadingLightXSlider;
    this.pShadingLightYREd = pShadingLightYREd;
    this.pShadingLightYSlider = pShadingLightYSlider;
    this.pShadingLightZREd = pShadingLightZREd;
    this.pShadingLightZSlider = pShadingLightZSlider;
    this.pShadingLightRedREd = pShadingLightRedREd;
    this.pShadingLightRedSlider = pShadingLightRedSlider;
    this.pShadingLightGreenREd = pShadingLightGreenREd;
    this.pShadingLightGreenSlider = pShadingLightGreenSlider;
    this.pShadingLightBlueREd = pShadingLightBlueREd;
    this.pShadingLightBlueSlider = pShadingLightBlueSlider;
    this.pShadingDistanceColorRadiusREd = pShadingDistanceColorRadiusREd;
    this.pShadingDistanceColorRadiusSlider = pShadingDistanceColorRadiusSlider;
    this.pShadingDistanceColorScaleREd = pShadingDistanceColorScaleREd;
    this.pShadingDistanceColorScaleSlider = pShadingDistanceColorScaleSlider;
    this.pShadingDistanceColorExponentREd = pShadingDistanceColorExponentREd;
    this.pShadingDistanceColorExponentSlider = pShadingDistanceColorExponentSlider;
    this.pShadingDistanceColorOffsetXREd = pShadingDistanceColorOffsetXREd;
    this.pShadingDistanceColorOffsetXSlider = pShadingDistanceColorOffsetXSlider;
    this.pShadingDistanceColorOffsetYREd = pShadingDistanceColorOffsetYREd;
    this.pShadingDistanceColorOffsetYSlider = pShadingDistanceColorOffsetYSlider;
    this.pShadingDistanceColorOffsetZREd = pShadingDistanceColorOffsetZREd;
    this.pShadingDistanceColorOffsetZSlider = pShadingDistanceColorOffsetZSlider;
    this.pMouseTransformSlowButton = pMouseTransformSlowButton;
    this.pRenderBatchJobsTable = pRenderBatchJobsTable;
    this.pBatchPreviewRootPanel = pBatchPreviewRootPanel;
    this.pBatchRenderJobProgressBar = pBatchRenderJobProgressBar;
    this.pBatchRenderTotalProgressBar = pBatchRenderTotalProgressBar;
    this.pJobProgressUpdater = pJobProgressUpdater;
    this.pBatchRenderAddFilesButton = pBatchRenderAddFilesButton;
    this.pBatchRenderFilesMoveDownButton = pBatchRenderFilesMoveDownButton;
    this.pBatchRenderFilesMoveUpButton = pBatchRenderFilesMoveUpButton;
    this.pBatchRenderFilesRemoveButton = pBatchRenderFilesRemoveButton;
    this.pBatchRenderFilesRemoveAllButton = pBatchRenderFilesRemoveAllButton;
    this.pBatchRenderStartButton = pBatchRenderStartButton;
    this.pRootTabbedPane = pRootTabbedPane;
    this.pAffineFlipHorizontalButton = pAffineFlipHorizontalButton;
    this.pAffineFlipVerticalButton = pAffineFlipVerticalButton;
    this.pShadingBlurRadiusREd = pShadingBlurRadiusREd;
    this.pShadingBlurRadiusSlider = pShadingBlurRadiusSlider;
    this.pShadingBlurFadeREd = pShadingBlurFadeREd;
    this.pShadingBlurFadeSlider = pShadingBlurFadeSlider;
    this.pShadingBlurFallOffREd = pShadingBlurFallOffREd;
    this.pShadingBlurFallOffSlider = pShadingBlurFallOffSlider;
    this.pAffineScaleXButton = pAffineScaleXButton;
    this.pAffineScaleYButton = pAffineScaleYButton;
    this.pGradientLibraryPanel = pGradientLibraryPanel;
    this.pHelpPane = pHelpPane;
    this.pToggleVariationsButton = pToggleVariationsButton;
    this.pToggleTransparencyButton = pToggleTransparencyButton;
    this.pAffinePreserveZButton = pAffinePreserveZButton;
    this.pQualityProfileCmb = pQualityProfileCmb;
    this.pResolutionProfileCmb = pResolutionProfileCmb;
    this.pBatchQualityProfileCmb = pBatchQualityProfileCmb;
    this.pBatchResolutionProfileCmb = pBatchResolutionProfileCmb;
    this.pInteractiveResolutionProfileCmb = pInteractiveResolutionProfileCmb;
    this.pSWFAnimatorResolutionProfileCmb = pSWFAnimatorResolutionProfileCmb;
    this.pRenderFlameButton = pRenderFlameButton;
    this.pRenderMainButton = pRenderMainButton;
    this.pAppendToMovieButton = pAppendToMovieButton;
    this.pTransformationWeightREd = pTransformationWeightREd;
    this.pUndoButton = pUndoButton;
    this.pRedoButton = pRedoButton;
    this.pXFormAntialiasAmountREd = pXFormAntialiasAmountREd;
    this.pXFormAntialiasAmountSlider = pXFormAntialiasAmountSlider;
    this.pXFormAntialiasRadiusREd = pXFormAntialiasRadiusREd;
    this.pXFormAntialiasRadiusSlider = pXFormAntialiasRadiusSlider;
    this.pDancingFlamesFlamePnl = pDancingFlamesFlamePnl;
    this.pDancingFlamesGraph1Pnl = pDancingFlamesGraph1Pnl;
    this.pDancingFlamesLoadSoundBtn = pDancingFlamesLoadSoundBtn;
    this.pDancingFlamesAddFromClipboardBtn = pDancingFlamesAddFromClipboardBtn;
    this.pDancingFlamesAddFromEditorBtn = pDancingFlamesAddFromEditorBtn;
    this.pDancingFlamesAddFromDiscBtn = pDancingFlamesAddFromDiscBtn;
    this.pDancingFlamesRandomCountIEd = pDancingFlamesRandomCountIEd;
    this.pDancingFlamesGenRandFlamesBtn = pDancingFlamesGenRandFlamesBtn;
    this.pDancingFlamesRandomGenCmb = pDancingFlamesRandomGenCmb;
    this.pDancingFlamesPoolFlamePreviewPnl = pDancingFlamesPoolFlamePreviewPnl;
    this.pDancingFlamesBorderSizeSlider = pDancingFlamesBorderSizeSlider;
    this.pDancingFlamesFlameToEditorBtn = pDancingFlamesFlameToEditorBtn;
    this.pDancingFlamesDeleteFlameBtn = pDancingFlamesDeleteFlameBtn;
    this.pDancingFlamesFramesPerSecondIEd = pDancingFlamesFramesPerSecondIEd;
    this.pDancingFlamesMorphFrameCountIEd = pDancingFlamesMorphFrameCountIEd;
    this.pDancingFlamesStartShowButton = pDancingFlamesStartShowButton;
    this.pDancingFlamesStopShowButton = pDancingFlamesStopShowButton;
    this.pDancingFlamesDoRecordCBx = pDancingFlamesDoRecordCBx;
    this.pDancingFlamesFlamesCmb = pDancingFlamesFlamesCmb;
    this.pDancingFlamesDrawTrianglesCBx = pDancingFlamesDrawTrianglesCBx;
    this.pDancingFlamesDrawFFTCBx = pDancingFlamesDrawFFTCBx;
    this.pDancingFlamesDrawFPSCBx = pDancingFlamesDrawFPSCBx;
    this.pDancingFlamesFlamePropertiesTree = pDancingFlamesFlamePropertiesTree;
    this.pDancingFlamesMotionPropertyPnl = pDancingFlamesMotionPropertyPnl;
    this.pDancingFlamesMotionTable = pDancingFlamesMotionTable;
    this.pDancingFlamesAddMotionCmb = pDancingFlamesAddMotionCmb;
    this.pDancingFlamesAddMotionBtn = pDancingFlamesAddMotionBtn;
    this.pDancingFlamesDeleteMotionBtn = pDancingFlamesDeleteMotionBtn;
  }

  public void setParams2(JButton pDancingFlamesLinkMotionBtn, JButton pDancingFlamesUnlinkMotionBtn, JComboBox pDancingFlamesCreateMotionsCmb, JButton pDancingFlamesClearMotionsBtn, JButton pDancingFlamesLoadProjectBtn, JButton pDancingFlamesSaveProjectBtn, JTable pDancingFlamesMotionLinksTable,
      JWFNumberField pShadingDistanceColorStyleREd, JSlider pShadingDistanceColorStyleSlider, JWFNumberField pShadingDistanceColorCoordinateREd, JSlider pShadingDistanceColorCoordinateSlider,
      JWFNumberField pShadingDistanceColorShiftREd, JSlider pShadingDistanceColorShiftSlider, JPanel pMutaGen01Pnl, JPanel pMutaGen02Pnl, JPanel pMutaGen03Pnl, JPanel pMutaGen04Pnl, JPanel pMutaGen05Pnl,
      JPanel pMutaGen06Pnl, JPanel pMutaGen07Pnl, JPanel pMutaGen08Pnl, JPanel pMutaGen09Pnl, JPanel pMutaGen10Pnl, JPanel pMutaGen11Pnl, JPanel pMutaGen12Pnl, JPanel pMutaGen13Pnl, JPanel pMutaGen14Pnl,
      JPanel pMutaGen15Pnl, JPanel pMutaGen16Pnl, JPanel pMutaGen17Pnl, JPanel pMutaGen18Pnl, JPanel pMutaGen19Pnl, JPanel pMutaGen20Pnl, JPanel pMutaGen21Pnl, JPanel pMutaGen22Pnl, JPanel pMutaGen23Pnl,
      JPanel pMutaGen24Pnl, JPanel pMutaGen25Pnl, JButton pMutaGenLoadFlameFromEditorBtn,
      JButton pMutaGenLoadFlameFromFileBtn, JProgressBar pMutaGenProgressBar, JWFNumberField pMutaGenAmountREd, JComboBox pMutaGenHorizontalTrend1Cmb,
      JComboBox pMutaGenHorizontalTrend2Cmb, JComboBox pMutaGenVerticalTrend1Cmb, JComboBox pMutaGenVerticalTrend2Cmb, JButton pMutaGenBackButtonBtn, JButton pMutaGenForwardButtonBtn,
      JTextPane pMutaGenHintPane, JButton pMutaGenSaveFlameToEditorBtn, JButton pMutaGenSaveFlameToFileBtn,
      JButton pEditTransformCaptionButton, JButton pEditFlameTileButton, JButton pSnapShotButton, JButton pQSaveButton, JButton pQuickMutationButton,
      JButton pDancingFlamesButton, JButton pMovieButton, JToggleButton pTransformSlowButton, JToggleButton pTransparencyButton, JToggleButton pMouseTransformRotateTrianglesButton, JToggleButton pMouseTransformScaleTrianglesButton, JTree pScriptTree,
      JTextArea pScriptDescriptionTextArea, JTextArea pScriptTextArea, JButton pCompileScriptButton, JButton pSaveScriptBtn, JButton pRevertScriptBtn, JButton pRescanScriptsBtn,
      JButton pNewScriptBtn, JButton pNewScriptFromFlameBtn, JButton pDeleteScriptBtn, JButton pScriptRenameBtn, JButton pScriptDuplicateBtn, JButton pScriptRunBtn,
      JToggleButton pMouseTransformEditGradientButton, JTree pGradientLibTree, JButton pGradientLibraryRescanBtn,
      JButton pGradientLibraryNewFolderBtn, JButton pGradientLibraryRenameFolderBtn, JList pGradientsList,
      JButton pBackgroundColorIndicatorBtn, JButton pRandomizeButton, JTree pFlameBrowserTree, JPanel pFlameBrowersImagesPnl,
      JButton pFlameBrowserRefreshBtn, JButton pFlameBrowserChangeFolderBtn, JButton pFlameBrowserToEditorBtn, JButton pFlameBrowserToBatchRendererBtn,
      JButton pFlameBrowserDeleteBtn, JButton pFlameBrowserRenameBtn, JCheckBox pPaletteFadeColorsCBx, JButton pDancingFlamesReplaceFlameFromEditorBtn,
      JButton pDancingFlamesRenameFlameBtn, JButton pDancingFlamesRenameMotionBtn, JCheckBox pDancingFlamesMutedCBx,
      JWFNumberField pLayerWeightEd, JButton pLayerAddBtn, JButton pLayerDuplicateBtn, JButton pLayerDeleteBtn,
      JTable pLayersTable, JToggleButton pLayerVisibleBtn, JToggleButton pLayerAppendBtn, JButton pLayerHideOthersBtn,
      JButton pLayerShowAllBtn, JToggleButton pLayerPreviewBtn,
      JWFNumberField pKeyframesFrameField, JSlider pKeyframesFrameSlider, JWFNumberField pKeyframesFrameCountField,
      JWFNumberField pMotionBlurLengthField, JSlider pMotionBlurLengthSlider, JWFNumberField pMotionBlurTimeStepField,
      JSlider pMotionBlurTimeStepSlider, JWFNumberField pMotionBlurDecayField, JSlider pMotionBlurDecaySlider,
      JToggleButton pMotionCurveEditModeButton, JPanel pFrameSliderPanel, JLabel pKeyframesFrameLbl, JLabel pKeyframesFrameCountLbl,
      JPanel pMotionBlurPanel, JWFNumberField pAffineMoveVertAmountREd, JComboBox pPostSymmetryTypeCmb, JWFNumberField pPostSymmetryDistanceREd,
      JSlider pPostSymmetryDistanceSlider, JWFNumberField pPostSymmetryRotationREd, JSlider pPostSymmetryRotationSlider,
      JWFNumberField pPostSymmetryOrderREd, JSlider pPostSymmetryOrderSlider, JWFNumberField pPostSymmetryCentreXREd,
      JSlider pPostSymmetryCentreXSlider, JWFNumberField pPostSymmetryCentreYREd, JSlider pPostSymmetryCentreYSlider,
      JComboBox pStereo3dModeCmb, JWFNumberField pStereo3dAngleREd, JSlider pStereo3dAngleSlider, JWFNumberField pStereo3dEyeDistREd,
      JSlider pStereo3dEyeDistSlider, JComboBox pStereo3dLeftEyeColorCmb, JComboBox pStereo3dRightEyeColorCmb,
      JWFNumberField pStereo3dInterpolatedImageCountREd, JSlider pStereo3dInterpolatedImageCountSlider, JComboBox pStereo3dPreviewCmb,
      JWFNumberField pStereo3dFocalOffsetREd, JSlider pStereo3dFocalOffsetSlider, JCheckBox pStereo3dSwapSidesCBx,
      JWFNumberField pCamPosXREd, JSlider pCamPosXSlider, JWFNumberField pCamPosYREd, JSlider pCamPosYSlider,
      JWFNumberField pCamPosZREd, JSlider pCamPosZSlider, JWFNumberField pSaturationREd, JSlider pSaturationSlider,
      JToggleButton pToggleDrawGridButton, JSlider pEditorFractalBrightnessSlider, JToggleButton pMouseTransformEditTriangleViewButton,
      JComboBox pPaletteRandomGeneratorCmb, JToggleButton pToggleTriangleWithColorsButton, JButton pFlameBrowserCopyToBtn,
      JButton pFlameBrowserMoveToBtn, JButton pAffineRotateEditMotionCurveBtn, JButton pAffineScaleEditMotionCurveBtn,
      JComboBox pTriangleStyleCmb, JWFNumberField pXFormModGammaREd, JSlider pXFormModGammaSlider, JWFNumberField pXFormModGammaSpeedREd,
      JSlider pXFormModGammaSpeedSlider, JWFNumberField pXFormModContrastREd, JSlider pXFormModContrastSlider, JWFNumberField pXFormModContrastSpeedREd,
      JSlider pXFormModContrastSpeedSlider, JWFNumberField pXFormModSaturationREd, JSlider pXFormModSaturationSlider, JWFNumberField pXFormModSaturationSpeedREd,
      JSlider pXFormModSaturationSpeedSlider) {
    this.pDancingFlamesLinkMotionBtn = pDancingFlamesLinkMotionBtn;
    this.pDancingFlamesUnlinkMotionBtn = pDancingFlamesUnlinkMotionBtn;
    this.pDancingFlamesCreateMotionsCmb = pDancingFlamesCreateMotionsCmb;
    this.pDancingFlamesClearMotionsBtn = pDancingFlamesClearMotionsBtn;
    this.pDancingFlamesLoadProjectBtn = pDancingFlamesLoadProjectBtn;
    this.pDancingFlamesSaveProjectBtn = pDancingFlamesSaveProjectBtn;
    this.pDancingFlamesMotionLinksTable = pDancingFlamesMotionLinksTable;
    this.pShadingDistanceColorStyleREd = pShadingDistanceColorStyleREd;
    this.pShadingDistanceColorStyleSlider = pShadingDistanceColorStyleSlider;
    this.pShadingDistanceColorCoordinateREd = pShadingDistanceColorCoordinateREd;
    this.pShadingDistanceColorCoordinateSlider = pShadingDistanceColorCoordinateSlider;
    this.pShadingDistanceColorShiftREd = pShadingDistanceColorShiftREd;
    this.pShadingDistanceColorShiftSlider = pShadingDistanceColorShiftSlider;
    this.mutaGen01Pnl = pMutaGen01Pnl;
    this.mutaGen02Pnl = pMutaGen02Pnl;
    this.mutaGen03Pnl = pMutaGen03Pnl;
    this.mutaGen04Pnl = pMutaGen04Pnl;
    this.mutaGen05Pnl = pMutaGen05Pnl;
    this.mutaGen06Pnl = pMutaGen06Pnl;
    this.mutaGen07Pnl = pMutaGen07Pnl;
    this.mutaGen08Pnl = pMutaGen08Pnl;
    this.mutaGen09Pnl = pMutaGen09Pnl;
    this.mutaGen10Pnl = pMutaGen10Pnl;
    this.mutaGen11Pnl = pMutaGen11Pnl;
    this.mutaGen12Pnl = pMutaGen12Pnl;
    this.mutaGen13Pnl = pMutaGen13Pnl;
    this.mutaGen14Pnl = pMutaGen14Pnl;
    this.mutaGen15Pnl = pMutaGen15Pnl;
    this.mutaGen16Pnl = pMutaGen16Pnl;
    this.mutaGen17Pnl = pMutaGen17Pnl;
    this.mutaGen18Pnl = pMutaGen18Pnl;
    this.mutaGen19Pnl = pMutaGen19Pnl;
    this.mutaGen20Pnl = pMutaGen20Pnl;
    this.mutaGen21Pnl = pMutaGen21Pnl;
    this.mutaGen22Pnl = pMutaGen22Pnl;
    this.mutaGen23Pnl = pMutaGen23Pnl;
    this.mutaGen24Pnl = pMutaGen24Pnl;
    this.mutaGen25Pnl = pMutaGen25Pnl;
    this.mutaGenLoadFlameFromEditorBtn = pMutaGenLoadFlameFromEditorBtn;
    this.mutaGenLoadFlameFromFileBtn = pMutaGenLoadFlameFromFileBtn;
    this.mutaGenProgressBar = pMutaGenProgressBar;
    this.mutaGenAmountREd = pMutaGenAmountREd;
    this.mutaGenHorizontalTrend1Cmb = pMutaGenHorizontalTrend1Cmb;
    this.mutaGenHorizontalTrend2Cmb = pMutaGenHorizontalTrend2Cmb;
    this.mutaGenVerticalTrend1Cmb = pMutaGenVerticalTrend1Cmb;
    this.mutaGenVerticalTrend2Cmb = pMutaGenVerticalTrend2Cmb;
    this.mutaGenBackBtn = pMutaGenBackButtonBtn;
    this.mutaGenForwardBtn = pMutaGenForwardButtonBtn;
    this.mutaGenHintPane = pMutaGenHintPane;
    this.mutaGenSaveFlameToEditorBtn = pMutaGenSaveFlameToEditorBtn;
    this.mutaGenSaveFlameToFileBtn = pMutaGenSaveFlameToFileBtn;
    this.editTransformCaptionButton = pEditTransformCaptionButton;
    this.editFlameTileButton = pEditFlameTileButton;
    this.snapShotButton = pSnapShotButton;
    this.qSaveButton = pQSaveButton;
    this.quickMutationButton = pQuickMutationButton;
    this.dancingFlamesButton = pDancingFlamesButton;
    this.movieButton = pMovieButton;
    this.transformSlowButton = pTransformSlowButton;
    this.transparencyButton = pTransparencyButton;
    this.mouseTransformRotateTrianglesButton = pMouseTransformRotateTrianglesButton;
    this.mouseTransformScaleTrianglesButton = pMouseTransformScaleTrianglesButton;
    this.scriptTree = pScriptTree;
    this.scriptDescriptionTextArea = pScriptDescriptionTextArea;
    this.scriptTextArea = pScriptTextArea;
    this.compileScriptButton = pCompileScriptButton;
    this.saveScriptBtn = pSaveScriptBtn;
    this.revertScriptBtn = pRevertScriptBtn;
    this.rescanScriptsBtn = pRescanScriptsBtn;
    this.newScriptBtn = pNewScriptBtn;
    this.newScriptFromFlameBtn = pNewScriptFromFlameBtn;
    this.deleteScriptBtn = pDeleteScriptBtn;
    this.scriptRenameBtn = pScriptRenameBtn;
    this.scriptDuplicateBtn = pScriptDuplicateBtn;
    this.scriptRunBtn = pScriptRunBtn;
    this.mouseTransformEditGradientButton = pMouseTransformEditGradientButton;
    this.gradientLibTree = pGradientLibTree;
    this.gradientLibraryRescanBtn = pGradientLibraryRescanBtn;
    this.gradientLibraryNewFolderBtn = pGradientLibraryNewFolderBtn;
    this.gradientLibraryRenameFolderBtn = pGradientLibraryRenameFolderBtn;
    this.gradientsList = pGradientsList;
    this.backgroundColorIndicatorBtn = pBackgroundColorIndicatorBtn;
    this.randomizeButton = pRandomizeButton;
    this.flameBrowserTree = pFlameBrowserTree;
    this.flameBrowersImagesPnl = pFlameBrowersImagesPnl;
    this.flameBrowserRefreshBtn = pFlameBrowserRefreshBtn;
    this.flameBrowserChangeFolderBtn = pFlameBrowserChangeFolderBtn;
    this.flameBrowserToEditorBtn = pFlameBrowserToEditorBtn;
    this.flameBrowserToBatchEditorBtn = pFlameBrowserToBatchRendererBtn;
    this.flameBrowserDeleteBtn = pFlameBrowserDeleteBtn;
    this.flameBrowserRenameBtn = pFlameBrowserRenameBtn;
    this.paletteFadeColorsCBx = pPaletteFadeColorsCBx;
    this.dancingFlamesReplaceFlameFromEditorBtn = pDancingFlamesReplaceFlameFromEditorBtn;
    this.dancingFlamesRenameFlameBtn = pDancingFlamesRenameFlameBtn;
    this.dancingFlamesRenameMotionBtn = pDancingFlamesRenameMotionBtn;
    this.dancingFlamesMutedCBx = pDancingFlamesMutedCBx;
    this.layerWeightEd = pLayerWeightEd;
    this.layerAddBtn = pLayerAddBtn;
    this.layerDuplicateBtn = pLayerDuplicateBtn;
    this.layerDeleteBtn = pLayerDeleteBtn;
    this.layersTable = pLayersTable;
    this.layerVisibleBtn = pLayerVisibleBtn;
    this.layerAppendBtn = pLayerAppendBtn;
    this.layerHideOthersBtn = pLayerHideOthersBtn;
    this.layerShowAllBtn = pLayerShowAllBtn;
    this.layerPreviewBtn = pLayerPreviewBtn;
    this.keyframesFrameField = pKeyframesFrameField;
    this.keyframesFrameSlider = pKeyframesFrameSlider;
    this.keyframesFrameCountField = pKeyframesFrameCountField;
    this.motionBlurLengthField = pMotionBlurLengthField;
    this.motionBlurLengthSlider = pMotionBlurLengthSlider;
    this.motionBlurTimeStepField = pMotionBlurTimeStepField;
    this.motionBlurTimeStepSlider = pMotionBlurTimeStepSlider;
    this.motionBlurDecayField = pMotionBlurDecayField;
    this.motionBlurDecaySlider = pMotionBlurDecaySlider;
    this.motionCurveEditModeButton = pMotionCurveEditModeButton;
    this.frameSliderPanel = pFrameSliderPanel;
    this.keyframesFrameLbl = pKeyframesFrameLbl;
    this.keyframesFrameCountLbl = pKeyframesFrameCountLbl;
    this.motionBlurPanel = pMotionBlurPanel;
    this.affineMoveVertAmountREd = pAffineMoveVertAmountREd;
    this.postSymmetryTypeCmb = pPostSymmetryTypeCmb;
    this.postSymmetryDistanceREd = pPostSymmetryDistanceREd;
    this.postSymmetryDistanceSlider = pPostSymmetryDistanceSlider;
    this.postSymmetryRotationREd = pPostSymmetryRotationREd;
    this.postSymmetryRotationSlider = pPostSymmetryRotationSlider;
    this.postSymmetryOrderREd = pPostSymmetryOrderREd;
    this.postSymmetryOrderSlider = pPostSymmetryOrderSlider;
    this.postSymmetryCentreXREd = pPostSymmetryCentreXREd;
    this.postSymmetryCentreXSlider = pPostSymmetryCentreXSlider;
    this.postSymmetryCentreYREd = pPostSymmetryCentreYREd;
    this.postSymmetryCentreYSlider = pPostSymmetryCentreYSlider;
    this.stereo3dModeCmb = pStereo3dModeCmb;
    this.stereo3dAngleREd = pStereo3dAngleREd;
    this.stereo3dAngleSlider = pStereo3dAngleSlider;
    this.stereo3dEyeDistREd = pStereo3dEyeDistREd;
    this.stereo3dEyeDistSlider = pStereo3dEyeDistSlider;
    this.stereo3dLeftEyeColorCmb = pStereo3dLeftEyeColorCmb;
    this.stereo3dRightEyeColorCmb = pStereo3dRightEyeColorCmb;
    this.stereo3dInterpolatedImageCountREd = pStereo3dInterpolatedImageCountREd;
    this.stereo3dInterpolatedImageCountSlider = pStereo3dInterpolatedImageCountSlider;
    this.stereo3dPreviewCmb = pStereo3dPreviewCmb;
    this.stereo3dFocalOffsetREd = pStereo3dFocalOffsetREd;
    this.stereo3dFocalOffsetSlider = pStereo3dFocalOffsetSlider;
    this.stereo3dSwapSidesCBx = pStereo3dSwapSidesCBx;
    this.camPosXREd = pCamPosXREd;
    this.camPosXSlider = pCamPosXSlider;
    this.camPosYREd = pCamPosYREd;
    this.camPosYSlider = pCamPosYSlider;
    this.camPosZREd = pCamPosZREd;
    this.camPosZSlider = pCamPosZSlider;
    this.saturationREd = pSaturationREd;
    this.saturationSlider = pSaturationSlider;
    this.toggleDrawGridButton = pToggleDrawGridButton;
    this.editorFractalBrightnessSlider = pEditorFractalBrightnessSlider;
    this.mouseTransformEditTriangleViewButton = pMouseTransformEditTriangleViewButton;
    this.paletteRandomGeneratorCmb = pPaletteRandomGeneratorCmb;
    this.toggleTriangleWithColorsButton = pToggleTriangleWithColorsButton;
    this.flameBrowserCopyToBtn = pFlameBrowserCopyToBtn;
    this.flameBrowserMoveToBtn = pFlameBrowserMoveToBtn;
    this.affineRotateEditMotionCurveBtn = pAffineRotateEditMotionCurveBtn;
    this.affineScaleEditMotionCurveBtn = pAffineScaleEditMotionCurveBtn;
    this.triangleStyleCmb = pTriangleStyleCmb;
    this.pXFormModGammaREd = pXFormModGammaREd;
    this.pXFormModGammaSlider = pXFormModGammaSlider;
    this.pXFormModGammaSpeedREd = pXFormModGammaSpeedREd;
    this.pXFormModGammaSpeedSlider = pXFormModGammaSpeedSlider;
    this.pXFormModContrastREd = pXFormModContrastREd;
    this.pXFormModContrastSlider = pXFormModContrastSlider;
    this.pXFormModContrastSpeedREd = pXFormModContrastSpeedREd;
    this.pXFormModContrastSpeedSlider = pXFormModContrastSpeedSlider;
    this.pXFormModSaturationREd = pXFormModSaturationREd;
    this.pXFormModSaturationSlider = pXFormModSaturationSlider;
    this.pXFormModSaturationSpeedREd = pXFormModSaturationSpeedREd;
    this.pXFormModSaturationSpeedSlider = pXFormModSaturationSpeedSlider;
  }

  public void setParams3(JButton pMeshGenFromEditorBtn, JButton pMeshGenFromClipboardBtn, JButton pMeshGenLoadFlameBtn,
      JWFNumberField pMeshGenSliceCountREd, JWFNumberField pMeshGenSlicesPerRenderREd, JWFNumberField pMeshGenRenderWidthREd,
      JWFNumberField pMeshGenRenderHeightREd, JWFNumberField pMeshGenRenderQualityREd, JProgressBar pMeshGenProgressbar,
      JButton pMeshGenGenerateBtn, JPanel pMeshGenTopViewRootPnl, JPanel pMeshGenFrontViewRootPnl, JPanel pMeshGenPerspectiveViewRootPnl,
      JTextPane pMeshGenHintPane, JWFNumberField pMeshGenCentreXREd, JSlider pMeshGenCentreXSlider, JWFNumberField pMeshGenCentreYREd,
      JSlider pMeshGenCentreYSlider, JWFNumberField pMeshGenZoomREd, JSlider pMeshGenZoomSlider, JWFNumberField pMeshGenZMinREd,
      JSlider pMeshGenZMinSlider, JWFNumberField pMeshGenZMaxREd, JSlider pMeshGenZMaxSlider, JButton pMeshGenTopViewRenderBtn,
      JButton pMeshGenFrontViewRenderBtn, JButton pMeshGenPerspectiveViewRenderBtn, JButton pMeshGenTopViewToEditorBtn,
      JButton pFlameBrowserToMeshGenBtn, JButton pMeshGenLoadSequenceBtn, JWFNumberField pMeshGenSequenceWidthREd, JWFNumberField pMeshGenSequenceHeightREd,
      JWFNumberField pMeshGenSequenceSlicesREd, JWFNumberField pMeshGenSequenceDownSampleREd, JWFNumberField pMeshGenSequenceFilterRadiusREd,
      JProgressBar pMeshGenGenerateMeshProgressbar, JButton pMeshGenGenerateMeshBtn, JButton pMeshGenSequenceFromRendererBtn,
      JWFNumberField pMeshGenSequenceThresholdREd, JLabel pMeshGenSequenceLbl, JPanel pMeshGenPreviewRootPanel, JCheckBox pMeshGenAutoPreviewCBx,
      JButton pMeshGenPreviewImportLastGeneratedMeshBtn, JButton pMeshGenPreviewImportFromFileBtn, JButton pMeshGenClearPreviewBtn,
      JWFNumberField pMeshGenPreviewPositionXREd, JWFNumberField pMeshGenPreviewPositionYREd,
      JWFNumberField pMeshGenPreviewSizeREd, JWFNumberField pMeshGenPreviewScaleZREd, JWFNumberField pMeshGenPreviewRotateAlphaREd,
      JWFNumberField pMeshGenPreviewRotateBetaREd, JWFNumberField pMeshGenPreviewPointsREd, JWFNumberField pMeshGenPreviewPolygonsREd,
      JButton pMeshGenRefreshPreviewBtn, JTextPane pApophysisHintsPane, JButton pMeshGenPreviewSunflowExportBtn, JWFNumberField pMeshGenThicknessModREd,
      JButton pChannelMixerResetBtn, JComboBox pChannelMixerModeCmb, JPanel pChannelMixerRRRootPanel, JPanel pChannelMixerRGRootPanel,
      JPanel pChannelMixerRBRootPanel, JPanel pChannelMixerGRRootPanel, JPanel pChannelMixerGGRootPanel, JPanel pChannelMixerGBRootPanel,
      JPanel pChannelMixerBRRootPanel, JPanel pChannelMixerBGRootPanel, JPanel pChannelMixerBBRootPanel,
      JWFNumberField pMeshGenThicknessSamplesREd, JComboBox pMeshGenPreFilter1Cmb, JComboBox pMeshGenPreFilter2Cmb,
      JWFNumberField pMeshGenImageStepREd, JButton pMotionCurvePlayPreviewButton,
      JComboBox pDofDOFShapeCmb, JWFNumberField pDofDOFScaleREd, JSlider pDofDOFScaleSlider, JWFNumberField pDofDOFAngleREd,
      JSlider pDofDOFAngleSlider, JWFNumberField pDofDOFFadeREd, JSlider pDofDOFFadeSlider, JWFNumberField pDofDOFParam1REd,
      JSlider pDofDOFParam1Slider, JLabel pDofDOFParam1Lbl, JWFNumberField pDofDOFParam2REd, JSlider pDofDOFParam2Slider,
      JLabel pDofDOFParam2Lbl, JWFNumberField pDofDOFParam3REd, JSlider pDofDOFParam3Slider, JLabel pDofDOFParam3Lbl,
      JWFNumberField pDofDOFParam4REd, JSlider pDofDOFParam4Slider, JLabel pDofDOFParam4Lbl, JWFNumberField pDofDOFParam5REd,
      JSlider pDofDOFParam5Slider, JLabel pDofDOFParam5Lbl, JWFNumberField pDofDOFParam6REd, JSlider pDofDOFParam6Slider,
      JLabel pDofDOFParam6Lbl, JCheckBox pBatchRenderOverrideCBx, JButton pBatchRenderShowImageBtn, JButton pBokehButton,
      JButton pResetCameraSettingsBtn, JButton pResetDOFSettingsButton, JButton pResetBokehOptionsButton,
      JButton pResetColoringOptionsButton, JButton pResetAntialiasOptionsButton, JButton pResetShadingSettingsBtn,
      JButton pResetStereo3DSettingsBtn, JButton pResetPostSymmetrySettingsBtn, JButton pResetMotionBlurSettingsBtn,
      JRadioButton pXaosViewAsToBtn, JRadioButton pXaosViewAsFromBtn, JToggleButton pToggleDrawGuidesButton,
      JPanel pPreviewEastMainPanel, JPanel pMacroButtonPanel, JButton pScriptAddButtonBtn, JTable pMacroButtonsTable,
      JButton pMacroButtonMoveUpBtn, JButton pMacroButtonMoveDownBtn, JButton pMacroButtonDeleteBtn,
      JToggleButton pToggleDetachedPreviewButton, JButton pGradientResetBtn, JWFNumberField pWhiteLevelREd,
      JSlider pWhiteLevelSlider, JPanel pMacroButtonHorizPanel, JPanel pMacroButtonHorizRootPanel, JComboBox pAffineEditPlaneCmb,
      JWFNumberField pGradientColorMapHorizOffsetREd, JSlider pGradientColorMapHorizOffsetSlider, JWFNumberField pGradientColorMapHorizScaleREd,
      JSlider pGradientColorMapHorizScaleSlider, JWFNumberField pGradientColorMapVertOffsetREd, JSlider pGradientColorMapVertOffsetSlider,
      JWFNumberField pGradientColorMapVertScaleREd, JSlider pGradientColorMapVertScaleSlider, JWFNumberField pGradientColorMapLocalColorAddREd,
      JSlider pGradientColorMapLocalColorAddSlider, JWFNumberField pGradientColorMapLocalColorScaleREd, JSlider pGradientColorMapLocalColorScaleSlider,
      JComboBox pSWFAnimatorQualityProfileCmb, JWFNumberField pFlameFPSField, JToggleButton pLeapMotionToggleButton,
      JTable pLeapMotionConfigTable, JComboBox pLeapMotionHandCmb, JComboBox pLeapMotionInputChannelCmb,
      JComboBox pLeapMotionOutputChannelCmb, JWFNumberField pLeapMotionIndex1Field, JWFNumberField pLeapMotionIndex2Field,
      JWFNumberField pLeapMotionIndex3Field, JWFNumberField pLeapMotionInvScaleField,
      JWFNumberField pLeapMotionOffsetField, JButton pLeapMotionAddButton, JButton pLeapMotionDuplicateButton,
      JButton pLeapMotionDeleteButton, JButton pLeapMotionClearButton, JButton pLeapMotionResetConfigButton) {
    meshGenFromEditorBtn = pMeshGenFromEditorBtn;
    meshGenFromClipboardBtn = pMeshGenFromClipboardBtn;
    meshGenLoadFlameBtn = pMeshGenLoadFlameBtn;
    meshGenSliceCountREd = pMeshGenSliceCountREd;
    meshGenSlicesPerRenderREd = pMeshGenSlicesPerRenderREd;
    meshGenRenderWidthREd = pMeshGenRenderWidthREd;
    meshGenRenderHeightREd = pMeshGenRenderHeightREd;
    meshGenRenderQualityREd = pMeshGenRenderQualityREd;
    meshGenProgressbar = pMeshGenProgressbar;
    meshGenGenerateBtn = pMeshGenGenerateBtn;
    meshGenTopViewRootPnl = pMeshGenTopViewRootPnl;
    meshGenFrontViewRootPnl = pMeshGenFrontViewRootPnl;
    meshGenPerspectiveViewRootPnl = pMeshGenPerspectiveViewRootPnl;
    meshGenHintPane = pMeshGenHintPane;
    meshGenCentreXREd = pMeshGenCentreXREd;
    meshGenCentreXSlider = pMeshGenCentreXSlider;
    meshGenCentreYREd = pMeshGenCentreYREd;
    meshGenCentreYSlider = pMeshGenCentreYSlider;
    meshGenZoomREd = pMeshGenZoomREd;
    meshGenZoomSlider = pMeshGenZoomSlider;
    meshGenZMinREd = pMeshGenZMinREd;
    meshGenZMinSlider = pMeshGenZMinSlider;
    meshGenZMaxREd = pMeshGenZMaxREd;
    meshGenZMaxSlider = pMeshGenZMaxSlider;
    meshGenTopViewRenderBtn = pMeshGenTopViewRenderBtn;
    meshGenFrontViewRenderBtn = pMeshGenFrontViewRenderBtn;
    meshGenPerspectiveViewRenderBtn = pMeshGenPerspectiveViewRenderBtn;
    meshGenTopViewToEditorBtn = pMeshGenTopViewToEditorBtn;
    flameBrowserToMeshGenBtn = pFlameBrowserToMeshGenBtn;
    meshGenLoadSequenceBtn = pMeshGenLoadSequenceBtn;
    meshGenSequenceWidthREd = pMeshGenSequenceWidthREd;
    meshGenSequenceHeightREd = pMeshGenSequenceHeightREd;
    meshGenSequenceSlicesREd = pMeshGenSequenceSlicesREd;
    meshGenSequenceDownSampleREd = pMeshGenSequenceDownSampleREd;
    meshGenSequenceFilterRadiusREd = pMeshGenSequenceFilterRadiusREd;
    meshGenGenerateMeshProgressbar = pMeshGenGenerateMeshProgressbar;
    meshGenGenerateMeshBtn = pMeshGenGenerateMeshBtn;
    meshGenSequenceFromRendererBtn = pMeshGenSequenceFromRendererBtn;
    meshGenSequenceThresholdREd = pMeshGenSequenceThresholdREd;
    meshGenSequenceLbl = pMeshGenSequenceLbl;
    meshGenPreviewRootPanel = pMeshGenPreviewRootPanel;
    meshGenAutoPreviewCBx = pMeshGenAutoPreviewCBx;
    meshGenPreviewImportLastGeneratedMeshBtn = pMeshGenPreviewImportLastGeneratedMeshBtn;
    meshGenPreviewImportFromFileBtn = pMeshGenPreviewImportFromFileBtn;
    meshGenClearPreviewBtn = pMeshGenClearPreviewBtn;
    meshGenPreviewPositionXREd = pMeshGenPreviewPositionXREd;
    meshGenPreviewPositionYREd = pMeshGenPreviewPositionYREd;
    meshGenPreviewSizeREd = pMeshGenPreviewSizeREd;
    meshGenPreviewScaleZREd = pMeshGenPreviewScaleZREd;
    meshGenPreviewRotateAlphaREd = pMeshGenPreviewRotateAlphaREd;
    meshGenPreviewRotateBetaREd = pMeshGenPreviewRotateBetaREd;
    meshGenPreviewPointsREd = pMeshGenPreviewPointsREd;
    meshGenPreviewPolygonsREd = pMeshGenPreviewPolygonsREd;
    meshGenRefreshPreviewBtn = pMeshGenRefreshPreviewBtn;
    apophysisHintsPane = pApophysisHintsPane;
    meshGenPreviewSunflowExportBtn = pMeshGenPreviewSunflowExportBtn;
    meshGenThicknessModREd = pMeshGenThicknessModREd;
    channelMixerResetBtn = pChannelMixerResetBtn;
    channelMixerModeCmb = pChannelMixerModeCmb;
    channelMixerRRRootPanel = pChannelMixerRRRootPanel;
    channelMixerRGRootPanel = pChannelMixerRGRootPanel;
    channelMixerRBRootPanel = pChannelMixerRBRootPanel;
    channelMixerGRRootPanel = pChannelMixerGRRootPanel;
    channelMixerGGRootPanel = pChannelMixerGGRootPanel;
    channelMixerGBRootPanel = pChannelMixerGBRootPanel;
    channelMixerBRRootPanel = pChannelMixerBRRootPanel;
    channelMixerBGRootPanel = pChannelMixerBGRootPanel;
    channelMixerBBRootPanel = pChannelMixerBBRootPanel;
    meshGenThicknessSamplesREd = pMeshGenThicknessSamplesREd;
    meshGenPreFilter1Cmb = pMeshGenPreFilter1Cmb;
    meshGenPreFilter2Cmb = pMeshGenPreFilter2Cmb;
    meshGenImageStepREd = pMeshGenImageStepREd;
    motionCurvePlayPreviewButton = pMotionCurvePlayPreviewButton;
    dofDOFShapeCmb = pDofDOFShapeCmb;
    dofDOFScaleREd = pDofDOFScaleREd;
    dofDOFScaleSlider = pDofDOFScaleSlider;
    dofDOFAngleREd = pDofDOFAngleREd;
    dofDOFAngleSlider = pDofDOFAngleSlider;
    dofDOFFadeREd = pDofDOFFadeREd;
    dofDOFFadeSlider = pDofDOFFadeSlider;
    dofDOFParam1REd = pDofDOFParam1REd;
    dofDOFParam1Slider = pDofDOFParam1Slider;
    dofDOFParam1Lbl = pDofDOFParam1Lbl;
    dofDOFParam2REd = pDofDOFParam2REd;
    dofDOFParam2Slider = pDofDOFParam2Slider;
    dofDOFParam2Lbl = pDofDOFParam2Lbl;
    dofDOFParam3REd = pDofDOFParam3REd;
    dofDOFParam3Slider = pDofDOFParam3Slider;
    dofDOFParam3Lbl = pDofDOFParam3Lbl;
    dofDOFParam4REd = pDofDOFParam4REd;
    dofDOFParam4Slider = pDofDOFParam4Slider;
    dofDOFParam4Lbl = pDofDOFParam4Lbl;
    dofDOFParam5REd = pDofDOFParam5REd;
    dofDOFParam5Slider = pDofDOFParam5Slider;
    dofDOFParam5Lbl = pDofDOFParam5Lbl;
    dofDOFParam6REd = pDofDOFParam6REd;
    dofDOFParam6Slider = pDofDOFParam6Slider;
    dofDOFParam6Lbl = pDofDOFParam6Lbl;
    batchRenderOverrideCBx = pBatchRenderOverrideCBx;
    batchRenderShowImageBtn = pBatchRenderShowImageBtn;
    bokehButton = pBokehButton;
    resetCameraSettingsBtn = pResetCameraSettingsBtn;
    resetDOFSettingsButton = pResetDOFSettingsButton;
    resetBokehOptionsButton = pResetBokehOptionsButton;
    resetColoringOptionsButton = pResetColoringOptionsButton;
    resetAntialiasOptionsButton = pResetAntialiasOptionsButton;
    resetShadingSettingsBtn = pResetShadingSettingsBtn;
    resetStereo3DSettingsBtn = pResetStereo3DSettingsBtn;
    resetPostSymmetrySettingsBtn = pResetPostSymmetrySettingsBtn;
    resetMotionBlurSettingsBtn = pResetMotionBlurSettingsBtn;
    xaosViewAsToBtn = pXaosViewAsToBtn;
    xaosViewAsFromBtn = pXaosViewAsFromBtn;
    toggleDrawGuidesButton = pToggleDrawGuidesButton;
    previewEastMainPanel = pPreviewEastMainPanel;
    macroButtonVertPanel = pMacroButtonPanel;
    scriptAddButtonBtn = pScriptAddButtonBtn;
    macroButtonsTable = pMacroButtonsTable;
    macroButtonMoveUpBtn = pMacroButtonMoveUpBtn;
    macroButtonMoveDownBtn = pMacroButtonMoveDownBtn;
    macroButtonDeleteBtn = pMacroButtonDeleteBtn;
    toggleDetachedPreviewButton = pToggleDetachedPreviewButton;
    gradientResetBtn = pGradientResetBtn;
    whiteLevelREd = pWhiteLevelREd;
    whiteLevelSlider = pWhiteLevelSlider;
    macroButtonHorizPanel = pMacroButtonHorizPanel;
    macroButtonHorizRootPanel = pMacroButtonHorizRootPanel;
    affineEditPlaneCmb = pAffineEditPlaneCmb;
    gradientColorMapHorizOffsetREd = pGradientColorMapHorizOffsetREd;
    gradientColorMapHorizOffsetSlider = pGradientColorMapHorizOffsetSlider;
    gradientColorMapHorizScaleREd = pGradientColorMapHorizScaleREd;
    gradientColorMapHorizScaleSlider = pGradientColorMapHorizScaleSlider;
    gradientColorMapVertOffsetREd = pGradientColorMapVertOffsetREd;
    gradientColorMapVertOffsetSlider = pGradientColorMapVertOffsetSlider;
    gradientColorMapVertScaleREd = pGradientColorMapVertScaleREd;
    gradientColorMapVertScaleSlider = pGradientColorMapVertScaleSlider;
    gradientColorMapLocalColorAddREd = pGradientColorMapLocalColorAddREd;
    gradientColorMapLocalColorAddSlider = pGradientColorMapLocalColorAddSlider;
    gradientColorMapLocalColorScaleREd = pGradientColorMapLocalColorScaleREd;
    gradientColorMapLocalColorScaleSlider = pGradientColorMapLocalColorScaleSlider;
    swfAnimatorQualityProfileCmb = pSWFAnimatorQualityProfileCmb;
    flameFPSField = pFlameFPSField;
    leapMotionToggleButton = pLeapMotionToggleButton;
    leapMotionConfigTable = pLeapMotionConfigTable;
    leapMotionHandCmb = pLeapMotionHandCmb;
    leapMotionInputChannelCmb = pLeapMotionInputChannelCmb;
    leapMotionOutputChannelCmb = pLeapMotionOutputChannelCmb;
    leapMotionIndex1Field = pLeapMotionIndex1Field;
    leapMotionIndex2Field = pLeapMotionIndex2Field;
    leapMotionIndex3Field = pLeapMotionIndex3Field;
    leapMotionInvScaleField = pLeapMotionInvScaleField;
    leapMotionOffsetField = pLeapMotionOffsetField;
    leapMotionAddButton = pLeapMotionAddButton;
    leapMotionDuplicateButton = pLeapMotionDuplicateButton;
    leapMotionDeleteButton = pLeapMotionDeleteButton;
    leapMotionClearButton = pLeapMotionClearButton;
    leapMotionResetConfigButton = pLeapMotionResetConfigButton;
  }
}