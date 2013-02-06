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
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.jwildfire.base.MathLib;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
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
import org.jwildfire.create.tina.render.CRendererInterface;
import org.jwildfire.create.tina.render.DrawFocusPointFlameRenderer;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.RendererType;
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
  private static final double SLIDER_SCALE_DE_FILTER_RADIUS = 1.0;
  private static final double SLIDER_SCALE_DE_FILTER_AMOUNT = 100.0;
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

  public static class NonlinearControlsRow {
    private final JComboBox nonlinearVarCmb;
    private final JComboBox nonlinearParamsCmb;
    private final JWFNumberField nonlinearVarREd;
    private final JWFNumberField nonlinearParamsREd;
    private final JButton nonlinearParamsLeftButton;

    public NonlinearControlsRow(JComboBox pNonlinearVarCmb, JComboBox pNonlinearParamsCmb, JWFNumberField pNonlinearVarREd, JWFNumberField pNonlinearParamsREd,
        JButton pNonlinearParamsLeftButton) {
      nonlinearVarCmb = pNonlinearVarCmb;
      nonlinearParamsCmb = pNonlinearParamsCmb;
      nonlinearVarREd = pNonlinearVarREd;
      nonlinearParamsREd = pNonlinearParamsREd;
      nonlinearParamsLeftButton = pNonlinearParamsLeftButton;
    }

    public void initControls() {
      nonlinearVarCmb.removeAllItems();
      List<String> nameList = new ArrayList<String>();
      nameList.addAll(VariationFuncList.getNameList());
      Collections.sort(nameList);
      nonlinearVarCmb.addItem(null);
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

    public JWFNumberField getNonlinearVarREd() {
      return nonlinearVarREd;
    }

    public JWFNumberField getNonlinearParamsREd() {
      return nonlinearParamsREd;
    }

    public JButton getNonlinearParamsLeftButton() {
      return nonlinearParamsLeftButton;
    }

  }

  private MainController mainController;
  private final JTabbedPane rootTabbedPane;
  private final JComboBox qualityProfileCmb;
  private final JComboBox resolutionProfileCmb;
  private final JComboBox batchQualityProfileCmb;
  private final JComboBox batchResolutionProfileCmb;
  private final JComboBox batchRendererCmb;
  private final JComboBox interactiveQualityProfileCmb;
  private final JComboBox interactiveResolutionProfileCmb;
  private final JComboBox swfAnimatorQualityProfileCmb;
  private final JComboBox swfAnimatorResolutionProfileCmb;
  private final JComboBox rendererCmb;
  // script
  private final JTextArea scriptTextArea;
  // camera, coloring  
  private final JWFNumberField cameraRollREd;
  private final JSlider cameraRollSlider;
  private final JWFNumberField cameraPitchREd;
  private final JSlider cameraPitchSlider;
  private final JWFNumberField cameraYawREd;
  private final JSlider cameraYawSlider;
  private final JWFNumberField cameraPerspectiveREd;
  private final JSlider cameraPerspectiveSlider;
  private final JWFNumberField cameraCentreXREd;
  private final JSlider cameraCentreXSlider;
  private final JWFNumberField cameraCentreYREd;
  private final JSlider cameraCentreYSlider;
  private final JWFNumberField cameraZoomREd;
  private final JSlider cameraZoomSlider;
  private final JWFNumberField focusXREd;
  private final JSlider focusXSlider;
  private final JWFNumberField focusYREd;
  private final JSlider focusYSlider;
  private final JWFNumberField focusZREd;
  private final JSlider focusZSlider;
  private final JWFNumberField camZREd;
  private final JSlider camZSlider;
  private final JWFNumberField cameraDOFREd;
  private final JSlider cameraDOFSlider;
  private final JWFNumberField cameraDOFAreaREd;
  private final JSlider cameraDOFAreaSlider;
  private final JWFNumberField cameraDOFExponentREd;
  private final JSlider cameraDOFExponentSlider;
  private final JCheckBox newDOFCBx;
  private final JWFNumberField pixelsPerUnitREd;
  private final JSlider pixelsPerUnitSlider;
  private final JWFNumberField brightnessREd;
  private final JSlider brightnessSlider;
  private final JWFNumberField contrastREd;
  private final JSlider contrastSlider;
  private final JWFNumberField gammaREd;
  private final JSlider gammaSlider;
  private final JWFNumberField vibrancyREd;
  private final JSlider vibrancySlider;
  private final JWFNumberField filterRadiusREd;
  private final JSlider filterRadiusSlider;
  private final JWFNumberField gammaThresholdREd;
  private final JSlider gammaThresholdSlider;
  private final JCheckBox bgTransparencyCBx;
  private final JWFNumberField bgColorRedREd;
  private final JSlider bgColorRedSlider;
  private final JWFNumberField bgColorGreenREd;
  private final JSlider bgColorGreenSlider;
  private final JWFNumberField bgColorBlueREd;
  private final JSlider bgColorBlueSlider;
  private final JWFNumberField deFilterRadiusREd;
  private final JSlider deFilterRadiusSlider;
  private final JWFNumberField deFilterAmountREd;
  private final JSlider deFilterAmountSlider;
  // shading
  private final JComboBox shadingCmb;
  private final JWFNumberField shadingAmbientREd;
  private final JSlider shadingAmbientSlider;
  private final JWFNumberField shadingDiffuseREd;
  private final JSlider shadingDiffuseSlider;
  private final JWFNumberField shadingPhongREd;
  private final JSlider shadingPhongSlider;
  private final JWFNumberField shadingPhongSizeREd;
  private final JSlider shadingPhongSizeSlider;
  private final JComboBox shadingLightCmb;
  private final JWFNumberField shadingLightXREd;
  private final JSlider shadingLightXSlider;
  private final JWFNumberField shadingLightYREd;
  private final JSlider shadingLightYSlider;
  private final JWFNumberField shadingLightZREd;
  private final JSlider shadingLightZSlider;
  private final JWFNumberField shadingLightRedREd;
  private final JSlider shadingLightRedSlider;
  private final JWFNumberField shadingLightGreenREd;
  private final JSlider shadingLightGreenSlider;
  private final JWFNumberField shadingLightBlueREd;
  private final JSlider shadingLightBlueSlider;

  private final JWFNumberField shadingBlurRadiusREd;
  private final JSlider shadingBlurRadiusSlider;
  private final JWFNumberField shadingBlurFadeREd;
  private final JSlider shadingBlurFadeSlider;
  private final JWFNumberField shadingBlurFallOffREd;
  private final JSlider shadingBlurFallOffSlider;

  // palette -> create
  private final JTextField paletteRandomPointsREd;
  private final JPanel paletteImgPanel;
  private final JPanel colorChooserPaletteImgPanel;
  private ImagePanel palettePanel;
  private ImagePanel colorChooserPalettePanel;
  // palette -> transform
  private final JWFNumberField paletteShiftREd;
  private final JSlider paletteShiftSlider;
  private final JWFNumberField paletteRedREd;
  private final JSlider paletteRedSlider;
  private final JWFNumberField paletteGreenREd;
  private final JSlider paletteGreenSlider;
  private final JWFNumberField paletteBlueREd;
  private final JSlider paletteBlueSlider;
  private final JWFNumberField paletteHueREd;
  private final JSlider paletteHueSlider;
  private final JWFNumberField paletteSaturationREd;
  private final JSlider paletteSaturationSlider;
  private final JWFNumberField paletteContrastREd;
  private final JSlider paletteContrastSlider;
  private final JWFNumberField paletteGammaREd;
  private final JSlider paletteGammaSlider;
  private final JWFNumberField paletteBrightnessREd;
  private final JSlider paletteBrightnessSlider;
  private final JWFNumberField paletteSwapRGBREd;
  private final JSlider paletteSwapRGBSlider;
  private final JWFNumberField paletteFrequencyREd;
  private final JSlider paletteFrequencySlider;
  private final JWFNumberField paletteBlurREd;
  private final JSlider paletteBlurSlider;
  private final JButton paletteInvertBtn;
  private final JButton paletteReverseBtn;
  // Batch render
  private final JTable renderBatchJobsTable;
  private final JProgressBar batchRenderJobProgressBar;
  private final JProgressBar batchRenderTotalProgressBar;
  private final JPanel batchPreviewRootPanel;

  // Transformations
  private final JToggleButton affineScaleXButton;
  private final JToggleButton affineScaleYButton;
  private final JTable transformationsTable;
  private final JButton affineResetTransformButton;
  private final JWFNumberField affineC00REd;
  private final JWFNumberField affineC01REd;
  private final JWFNumberField affineC10REd;
  private final JWFNumberField affineC11REd;
  private final JWFNumberField affineC20REd;
  private final JWFNumberField affineC21REd;
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
  private final JButton affineFlipHorizontalButton;
  private final JButton affineFlipVerticalButton;
  private final JButton addTransformationButton;
  private final JButton duplicateTransformationButton;
  private final JButton deleteTransformationButton;
  private final JButton addFinalTransformationButton;
  private final JWFNumberField transformationWeightREd;
  private final JToggleButton affineEditPostTransformButton;
  private final JToggleButton affineEditPostTransformSmallButton;
  private final JToggleButton affinePreserveZButton;
  private final JButton mouseEditZoomInButton;
  private final JButton mouseEditZoomOutButton;
  private final JToggleButton toggleDarkTrianglesButton;
  private final JToggleButton toggleVariationsButton;
  private final JToggleButton toggleTransparencyButton;
  // Gradient
  private final JPanel gradientLibraryPanel;
  private JScrollPane gradientLibraryScrollPane = null;
  private final JComboBox gradientLibraryGradientCmb;
  // Random batch
  private final JPanel randomBatchPanel;
  private JScrollPane randomBatchScrollPane = null;
  // Nonlinear transformations
  private final NonlinearControlsRow[] nonlinearControlsRows;
  // Color
  private final JWFNumberField xFormColorREd;
  private final JSlider xFormColorSlider;
  private final JWFNumberField xFormSymmetryREd;
  private final JSlider xFormSymmetrySlider;
  private final JWFNumberField xFormOpacityREd;
  private final JSlider xFormOpacitySlider;
  private final JComboBox xFormDrawModeCmb;
  private final JButton xFormAntialiasCopyToAllBtn;
  // Antialias
  private final JWFNumberField xFormAntialiasAmountREd;
  private final JSlider xFormAntialiasAmountSlider;
  private final JWFNumberField xFormAntialiasRadiusREd;
  private final JSlider xFormAntialiasRadiusSlider;
  // Relative weights
  private final JTable relWeightsTable;
  private final JButton relWeightsZeroButton;
  private final JButton relWeightsOneButton;
  private final JWFNumberField relWeightREd;
  // misc
  private Flame currFlame;
  private boolean noRefresh;
  private final ProgressUpdater mainProgressUpdater;
  private final ProgressUpdater jobProgressUpdater;
  private final JTable createPaletteColorsTable;
  private List<RGBColor> paletteKeyFrames;
  private final JButton renderFlameButton;
  private final JButton renderMainButton;
  private final JButton appendToMovieButton;

  // mouse dragging
  private final JToggleButton mouseTransformEditTrianglesButton;
  private final JToggleButton mouseTransformEditFocusPointButton;
  private final JToggleButton mouseTransformEditPointsButton;
  private final JToggleButton mouseTransformEditViewButton;
  private final JToggleButton mouseTransformSlowButton;
  //
  private final JButton batchRenderAddFilesButton;
  private final JButton batchRenderFilesMoveDownButton;
  private final JButton batchRenderFilesMoveUpButton;
  private final JButton batchRenderFilesRemoveButton;
  private final JButton batchRenderFilesRemoveAllButton;
  private final JButton batchRenderStartButton;
  //
  private final JTextPane helpPane;
  private final JTextPane faqPane;
  // Undo/redo
  private final JButton undoButton;
  private final JButton redoButton;

  public TinaController(JInternalFrame pTinaFrame, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pCenterPanel, JWFNumberField pCameraRollREd, JSlider pCameraRollSlider, JWFNumberField pCameraPitchREd,
      JSlider pCameraPitchSlider, JWFNumberField pCameraYawREd, JSlider pCameraYawSlider, JWFNumberField pCameraPerspectiveREd, JSlider pCameraPerspectiveSlider,
      JWFNumberField pCameraCentreXREd, JSlider pCameraCentreXSlider, JWFNumberField pCameraCentreYREd,
      JSlider pCameraCentreYSlider, JWFNumberField pCameraZoomREd, JSlider pCameraZoomSlider,
      JCheckBox pNewDOFCBx, JWFNumberField pFocusXREd, JSlider pFocusXSlider, JWFNumberField pFocusYREd, JSlider pFocusYSlider, JWFNumberField pFocusZREd,
      JSlider pFocusZSlider, JWFNumberField pCameraDOFREd, JSlider pCameraDOFSlider, JWFNumberField pCameraDOFAreaREd, JSlider pCameraDOFAreaSlider,
      JWFNumberField pCameraDOFExponentREd, JSlider pCameraDOFExponentSlider, JWFNumberField pCamZREd, JSlider pCamZSlider,
      JWFNumberField pPixelsPerUnitREd, JSlider pPixelsPerUnitSlider,
      JWFNumberField pBrightnessREd, JSlider pBrightnessSlider, JWFNumberField pContrastREd, JSlider pContrastSlider, JWFNumberField pGammaREd, JSlider pGammaSlider,
      JWFNumberField pVibrancyREd, JSlider pVibrancySlider, JWFNumberField pFilterRadiusREd, JSlider pFilterRadiusSlider,
      JWFNumberField pDEFilterRadiusREd, JSlider pDEFilterRadiusSlider, JWFNumberField pDEFilterAmountREd, JSlider pDEFilterAmountSlider, JWFNumberField pGammaThresholdREd,
      JSlider pGammaThresholdSlider, JCheckBox pBGTransparencyCBx, JWFNumberField pBGColorRedREd, JSlider pBGColorRedSlider, JWFNumberField pBGColorGreenREd, JSlider pBGColorGreenSlider, JWFNumberField pBGColorBlueREd,
      JSlider pBGColorBlueSlider, JTextField pPaletteRandomPointsREd, JPanel pPaletteImgPanel, JPanel pColorChooserPaletteImgPanel, JWFNumberField pPaletteShiftREd, JSlider pPaletteShiftSlider,
      JWFNumberField pPaletteRedREd, JSlider pPaletteRedSlider, JWFNumberField pPaletteGreenREd, JSlider pPaletteGreenSlider, JWFNumberField pPaletteBlueREd,
      JSlider pPaletteBlueSlider, JWFNumberField pPaletteHueREd, JSlider pPaletteHueSlider, JWFNumberField pPaletteSaturationREd, JSlider pPaletteSaturationSlider,
      JWFNumberField pPaletteContrastREd, JSlider pPaletteContrastSlider, JWFNumberField pPaletteGammaREd, JSlider pPaletteGammaSlider, JWFNumberField pPaletteBrightnessREd,
      JSlider pPaletteBrightnessSlider, JWFNumberField pPaletteSwapRGBREd, JSlider pPaletteSwapRGBSlider, JWFNumberField pPaletteFrequencyREd, JSlider pPaletteFrequencySlider,
      JWFNumberField pPaletteBlurREd, JSlider pPaletteBlurSlider, JButton pPaletteInvertBtn, JButton pPaletteReverseBtn,
      JTable pTransformationsTable, JWFNumberField pAffineC00REd,
      JWFNumberField pAffineC01REd, JWFNumberField pAffineC10REd, JWFNumberField pAffineC11REd, JWFNumberField pAffineC20REd, JWFNumberField pAffineC21REd,
      JTextField pAffineRotateAmountREd, JTextField pAffineScaleAmountREd, JTextField pAffineMoveAmountREd, JButton pAffineRotateLeftButton,
      JButton pAffineRotateRightButton, JButton pAffineEnlargeButton, JButton pAffineShrinkButton, JButton pAffineMoveUpButton, JButton pAffineMoveLeftButton,
      JButton pAffineMoveRightButton, JButton pAffineMoveDownButton, JButton pAddTransformationButton, JButton pDuplicateTransformationButton,
      JButton pDeleteTransformationButton, JButton pAddFinalTransformationButton, JPanel pRandomBatchPanel, NonlinearControlsRow[] pNonlinearControlsRows,
      JWFNumberField pXFormColorREd, JSlider pXFormColorSlider, JWFNumberField pXFormSymmetryREd, JSlider pXFormSymmetrySlider, JWFNumberField pXFormOpacityREd,
      JSlider pXFormOpacitySlider, JComboBox pXFormDrawModeCmb, JTable pRelWeightsTable, JButton pRelWeightsZeroButton, JButton pRelWeightsOneButton,
      JWFNumberField pRelWeightREd, JToggleButton pMouseTransformMoveButton, JToggleButton pMouseTransformScaleButton,
      JToggleButton pMouseTransformShearButton, JToggleButton pMouseTransformViewButton,
      JToggleButton pAffineEditPostTransformButton, JToggleButton pAffineEditPostTransformSmallButton, JButton pMouseEditZoomInButton, JButton pMouseEditZoomOutButton,
      ProgressUpdater pMainProgressUpdater, JButton pAffineResetTransformButton, JTable pCreatePaletteColorsTable,
      JComboBox pShadingCmb, JWFNumberField pShadingAmbientREd, JSlider pShadingAmbientSlider, JWFNumberField pShadingDiffuseREd, JSlider pShadingDiffuseSlider,
      JWFNumberField pShadingPhongREd, JSlider pShadingPhongSlider, JWFNumberField pShadingPhongSizeREd, JSlider pShadingPhongSizeSlider,
      JComboBox pShadingLightCmb, JWFNumberField pShadingLightXREd, JSlider pShadingLightXSlider, JWFNumberField pShadingLightYREd, JSlider pShadingLightYSlider,
      JWFNumberField pShadingLightZREd, JSlider pShadingLightZSlider, JWFNumberField pShadingLightRedREd, JSlider pShadingLightRedSlider,
      JWFNumberField pShadingLightGreenREd, JSlider pShadingLightGreenSlider, JWFNumberField pShadingLightBlueREd, JSlider pShadingLightBlueSlider,
      JToggleButton pMouseTransformSlowButton, JTable pRenderBatchJobsTable, JPanel pBatchPreviewRootPanel, JProgressBar pBatchRenderJobProgressBar,
      JProgressBar pBatchRenderTotalProgressBar, ProgressUpdater pJobProgressUpdater, JButton pBatchRenderAddFilesButton,
      JButton pBatchRenderFilesMoveDownButton, JButton pBatchRenderFilesMoveUpButton, JButton pBatchRenderFilesRemoveButton,
      JButton pBatchRenderFilesRemoveAllButton, JButton pBatchRenderStartButton, JTabbedPane pRootTabbedPane, JButton pAffineFlipHorizontalButton,
      JButton pAffineFlipVerticalButton, JToggleButton pToggleDarkTrianglesButton,
      JWFNumberField pShadingBlurRadiusREd, JSlider pShadingBlurRadiusSlider, JWFNumberField pShadingBlurFadeREd, JSlider pShadingBlurFadeSlider,
      JWFNumberField pShadingBlurFallOffREd, JSlider pShadingBlurFallOffSlider, JTextArea pScriptTextArea, JToggleButton pAffineScaleXButton,
      JToggleButton pAffineScaleYButton, JPanel pGradientLibraryPanel, JComboBox pGradientLibraryGradientCmb, JTextPane pHelpPane, JTextPane pFAQPane,
      JToggleButton pToggleVariationsButton, JToggleButton pToggleTransparencyButton, JToggleButton pAffinePreserveZButton,
      JComboBox pQualityProfileCmb, JComboBox pResolutionProfileCmb, JComboBox pBatchQualityProfileCmb, JComboBox pBatchResolutionProfileCmb,
      JComboBox pBatchRendererCmb,
      JComboBox pInteractiveQualityProfileCmb, JComboBox pInteractiveResolutionProfileCmb, JComboBox pSWFAnimatorQualityProfileCmb,
      JComboBox pSWFAnimatorResolutionProfileCmb, JButton pRenderFlameButton, JButton pRenderMainButton, JButton pAppendToMovieButton,
      JWFNumberField pTransformationWeightREd, JButton pUndoButton, JButton pRedoButton, JComboBox pRendererCmb,
      JWFNumberField pXFormAntialiasAmountREd, JSlider pXFormAntialiasAmountSlider, JWFNumberField pXFormAntialiasRadiusREd, JSlider pXFormAntialiasRadiusSlider,
      JButton pXFormAntialiasCopyToAllBtn, JPanel pDancingFlamesFlamePnl, JPanel pDancingFlamesGraph1Pnl, JButton pDancingFlamesLoadSoundBtn, JButton pDancingFlamesAddFromClipboardBtn,
      JButton pDancingFlamesAddFromEditorBtn, JButton pDancingFlamesAddFromDiscBtn, JWFNumberField pDancingFlamesRandomCountIEd, JButton pDancingFlamesGenRandFlamesBtn,
      JComboBox pDancingFlamesRandomGenCmb, JPanel pDancingFlamesPoolFlamePreviewPnl, JSlider pDancingFlamesBorderSizeSlider,
      JButton pDancingFlamesFlameToEditorBtn, JButton pDancingFlamesDeleteFlameBtn, JTextField pDancingFlamesFramesPerSecondIEd,
      JTextField pDancingFlamesMorphFrameCountIEd, JButton pDancingFlamesStartShowButton, JButton pDancingFlamesStopShowButton,
      JCheckBox pDancingFlamesDoRecordCBx,
      JComboBox pDancingFlamesFlamesCmb, JCheckBox pDancingFlamesDrawTrianglesCBx, JCheckBox pDancingFlamesDrawFFTCBx, JCheckBox pDancingFlamesDrawFPSCBx,
      JTree pDancingFlamesFlamePropertiesTree,
      JPanel pDancingFlamesMotionPropertyPnl, JTable pDancingFlamesMotionTable, JComboBox pDancingFlamesAddMotionCmb, JButton pDancingFlamesAddMotionBtn,
      JButton pDancingFlamesDeleteMotionBtn, JButton pDancingFlamesLinkMotionBtn, JButton pDancingFlamesUnlinkMotionBtn,
      JComboBox pDancingFlamesCreateMotionsCmb, JButton pDancingFlamesClearMotionsBtn, JButton pDancingFlamesLoadProjectBtn, JButton pDancingFlamesSaveProjectBtn,
      JButton pDancingFlamesMotionLinkToAllBtn, JButton pDancingFlamesUnlinkFromAllMotionsBtn, JTable pDancingFlamesMotionLinksTable) {
    tinaFrame = pTinaFrame;
    tinaFrameTitle = tinaFrame.getTitle();
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    centerPanel = pCenterPanel;

    dancingFractalsController = new DancingFractalsController(this, pErrorHandler, pDancingFlamesFlamePnl, pDancingFlamesGraph1Pnl,
        pDancingFlamesLoadSoundBtn, pDancingFlamesAddFromClipboardBtn, pDancingFlamesAddFromEditorBtn,
        pDancingFlamesAddFromDiscBtn, pDancingFlamesRandomCountIEd, pDancingFlamesGenRandFlamesBtn,
        pDancingFlamesRandomGenCmb, pDancingFlamesPoolFlamePreviewPnl, pDancingFlamesBorderSizeSlider,
        pDancingFlamesFlameToEditorBtn, pDancingFlamesDeleteFlameBtn, pDancingFlamesFramesPerSecondIEd, pDancingFlamesMorphFrameCountIEd,
        pDancingFlamesStartShowButton, pDancingFlamesStopShowButton, pDancingFlamesDoRecordCBx,
        pDancingFlamesFlamesCmb,
        pDancingFlamesDrawTrianglesCBx, pDancingFlamesDrawFFTCBx, pDancingFlamesDrawFPSCBx, pDancingFlamesFlamePropertiesTree,
        pDancingFlamesMotionPropertyPnl, pDancingFlamesMotionTable, pDancingFlamesAddMotionCmb, pDancingFlamesAddMotionBtn,
        pDancingFlamesDeleteMotionBtn, pDancingFlamesLinkMotionBtn, pDancingFlamesUnlinkMotionBtn,
        pDancingFlamesCreateMotionsCmb, pDancingFlamesClearMotionsBtn, pDancingFlamesLoadProjectBtn, pDancingFlamesSaveProjectBtn,
        pDancingFlamesMotionLinkToAllBtn, pDancingFlamesUnlinkFromAllMotionsBtn, pDancingFlamesMotionLinksTable);

    cameraRollREd = pCameraRollREd;
    cameraRollSlider = pCameraRollSlider;
    cameraPitchREd = pCameraPitchREd;
    cameraPitchSlider = pCameraPitchSlider;
    cameraYawREd = pCameraYawREd;
    cameraYawSlider = pCameraYawSlider;
    cameraPerspectiveREd = pCameraPerspectiveREd;
    cameraPerspectiveSlider = pCameraPerspectiveSlider;
    cameraCentreXREd = pCameraCentreXREd;
    cameraCentreXSlider = pCameraCentreXSlider;
    cameraCentreYREd = pCameraCentreYREd;
    cameraCentreYSlider = pCameraCentreYSlider;
    cameraZoomREd = pCameraZoomREd;
    cameraZoomSlider = pCameraZoomSlider;
    newDOFCBx = pNewDOFCBx;
    focusXREd = pFocusXREd;
    focusXSlider = pFocusXSlider;
    focusYREd = pFocusYREd;
    focusYSlider = pFocusYSlider;
    focusZREd = pFocusZREd;
    focusZSlider = pFocusZSlider;
    cameraDOFREd = pCameraDOFREd;
    cameraDOFSlider = pCameraDOFSlider;
    cameraDOFAreaREd = pCameraDOFAreaREd;
    cameraDOFAreaSlider = pCameraDOFAreaSlider;
    cameraDOFExponentREd = pCameraDOFExponentREd;
    cameraDOFExponentSlider = pCameraDOFExponentSlider;
    camZREd = pCamZREd;
    camZSlider = pCamZSlider;
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
    deFilterRadiusREd = pDEFilterRadiusREd;
    deFilterRadiusSlider = pDEFilterRadiusSlider;
    deFilterAmountREd = pDEFilterAmountREd;
    deFilterAmountSlider = pDEFilterAmountSlider;
    gammaThresholdREd = pGammaThresholdREd;
    gammaThresholdSlider = pGammaThresholdSlider;
    bgTransparencyCBx = pBGTransparencyCBx;
    bgColorRedREd = pBGColorRedREd;
    bgColorRedSlider = pBGColorRedSlider;
    bgColorGreenREd = pBGColorGreenREd;
    bgColorGreenSlider = pBGColorGreenSlider;
    bgColorBlueREd = pBGColorBlueREd;
    bgColorBlueSlider = pBGColorBlueSlider;
    paletteRandomPointsREd = pPaletteRandomPointsREd;
    paletteImgPanel = pPaletteImgPanel;
    colorChooserPaletteImgPanel = pColorChooserPaletteImgPanel;
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
    paletteSwapRGBREd = pPaletteSwapRGBREd;
    paletteSwapRGBSlider = pPaletteSwapRGBSlider;
    paletteFrequencyREd = pPaletteFrequencyREd;
    paletteFrequencySlider = pPaletteFrequencySlider;
    paletteBlurREd = pPaletteBlurREd;
    paletteBlurSlider = pPaletteBlurSlider;
    paletteInvertBtn = pPaletteInvertBtn;
    paletteReverseBtn = pPaletteReverseBtn;

    qualityProfileCmb = pQualityProfileCmb;
    resolutionProfileCmb = pResolutionProfileCmb;
    batchQualityProfileCmb = pBatchQualityProfileCmb;
    batchResolutionProfileCmb = pBatchResolutionProfileCmb;
    batchRendererCmb = pBatchRendererCmb;
    interactiveQualityProfileCmb = pInteractiveQualityProfileCmb;
    interactiveResolutionProfileCmb = pInteractiveResolutionProfileCmb;
    swfAnimatorQualityProfileCmb = pSWFAnimatorQualityProfileCmb;
    swfAnimatorResolutionProfileCmb = pSWFAnimatorResolutionProfileCmb;
    rendererCmb = pRendererCmb;
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
    affineFlipHorizontalButton = pAffineFlipHorizontalButton;
    affineFlipVerticalButton = pAffineFlipVerticalButton;
    addTransformationButton = pAddTransformationButton;
    duplicateTransformationButton = pDuplicateTransformationButton;
    deleteTransformationButton = pDeleteTransformationButton;
    addFinalTransformationButton = pAddFinalTransformationButton;
    affineEditPostTransformButton = pAffineEditPostTransformButton;
    affineEditPostTransformSmallButton = pAffineEditPostTransformSmallButton;
    affinePreserveZButton = pAffinePreserveZButton;
    affineScaleXButton = pAffineScaleXButton;
    affineScaleYButton = pAffineScaleYButton;
    mouseEditZoomInButton = pMouseEditZoomInButton;
    mouseEditZoomOutButton = pMouseEditZoomOutButton;

    randomBatchPanel = pRandomBatchPanel;
    nonlinearControlsRows = pNonlinearControlsRows;
    gradientLibraryPanel = pGradientLibraryPanel;
    gradientLibraryGradientCmb = pGradientLibraryGradientCmb;
    renderFlameButton = pRenderFlameButton;
    renderMainButton = pRenderMainButton;
    appendToMovieButton = pAppendToMovieButton;

    xFormColorREd = pXFormColorREd;
    xFormColorSlider = pXFormColorSlider;
    xFormSymmetryREd = pXFormSymmetryREd;
    xFormSymmetrySlider = pXFormSymmetrySlider;
    xFormOpacityREd = pXFormOpacityREd;
    xFormOpacitySlider = pXFormOpacitySlider;
    xFormDrawModeCmb = pXFormDrawModeCmb;

    xFormAntialiasAmountREd = pXFormAntialiasAmountREd;
    xFormAntialiasAmountSlider = pXFormAntialiasAmountSlider;
    xFormAntialiasRadiusREd = pXFormAntialiasRadiusREd;
    xFormAntialiasRadiusSlider = pXFormAntialiasRadiusSlider;
    xFormAntialiasCopyToAllBtn = pXFormAntialiasCopyToAllBtn;

    relWeightsTable = pRelWeightsTable;
    relWeightsZeroButton = pRelWeightsZeroButton;
    relWeightsOneButton = pRelWeightsOneButton;
    relWeightREd = pRelWeightREd;

    transformationWeightREd = pTransformationWeightREd;

    mouseTransformEditTrianglesButton = pMouseTransformMoveButton;
    mouseTransformEditFocusPointButton = pMouseTransformScaleButton;
    mouseTransformEditPointsButton = pMouseTransformShearButton;
    mouseTransformEditViewButton = pMouseTransformViewButton;
    toggleVariationsButton = pToggleVariationsButton;
    toggleTransparencyButton = pToggleTransparencyButton;
    toggleDarkTrianglesButton = pToggleDarkTrianglesButton;
    mainProgressUpdater = pMainProgressUpdater;
    jobProgressUpdater = pJobProgressUpdater;
    affineResetTransformButton = pAffineResetTransformButton;
    createPaletteColorsTable = pCreatePaletteColorsTable;
    shadingCmb = pShadingCmb;
    shadingAmbientREd = pShadingAmbientREd;
    shadingAmbientSlider = pShadingAmbientSlider;
    shadingDiffuseREd = pShadingDiffuseREd;
    shadingDiffuseSlider = pShadingDiffuseSlider;
    shadingPhongREd = pShadingPhongREd;
    shadingPhongSlider = pShadingPhongSlider;
    shadingPhongSizeREd = pShadingPhongSizeREd;
    shadingPhongSizeSlider = pShadingPhongSizeSlider;
    shadingLightCmb = pShadingLightCmb;
    shadingLightXREd = pShadingLightXREd;
    shadingLightXSlider = pShadingLightXSlider;
    shadingLightYREd = pShadingLightYREd;
    shadingLightYSlider = pShadingLightYSlider;
    shadingLightZREd = pShadingLightZREd;
    shadingLightZSlider = pShadingLightZSlider;
    shadingLightRedREd = pShadingLightRedREd;
    shadingLightRedSlider = pShadingLightRedSlider;
    shadingLightGreenREd = pShadingLightGreenREd;
    shadingLightGreenSlider = pShadingLightGreenSlider;
    shadingLightBlueREd = pShadingLightBlueREd;
    shadingLightBlueSlider = pShadingLightBlueSlider;

    shadingBlurRadiusREd = pShadingBlurRadiusREd;
    shadingBlurRadiusSlider = pShadingBlurRadiusSlider;
    shadingBlurFadeREd = pShadingBlurFadeREd;
    shadingBlurFadeSlider = pShadingBlurFadeSlider;
    shadingBlurFallOffREd = pShadingBlurFallOffREd;
    shadingBlurFallOffSlider = pShadingBlurFallOffSlider;

    mouseTransformSlowButton = pMouseTransformSlowButton;

    renderBatchJobsTable = pRenderBatchJobsTable;
    batchPreviewRootPanel = pBatchPreviewRootPanel;
    batchRenderJobProgressBar = pBatchRenderJobProgressBar;
    batchRenderTotalProgressBar = pBatchRenderTotalProgressBar;
    batchRenderAddFilesButton = pBatchRenderAddFilesButton;
    batchRenderFilesMoveDownButton = pBatchRenderFilesMoveDownButton;
    batchRenderFilesMoveUpButton = pBatchRenderFilesMoveUpButton;
    batchRenderFilesRemoveButton = pBatchRenderFilesRemoveButton;
    batchRenderFilesRemoveAllButton = pBatchRenderFilesRemoveAllButton;
    batchRenderStartButton = pBatchRenderStartButton;
    rootTabbedPane = pRootTabbedPane;
    helpPane = pHelpPane;
    faqPane = pFAQPane;

    undoButton = pUndoButton;
    redoButton = pRedoButton;

    qsaveFilenameGen = new QuickSaveFilenameGen(prefs);

    enableUndoControls();

    initHelpPane();
    initFAQPane();

    scriptTextArea = pScriptTextArea;

    refreshPaletteColorsTable();
    refreshRenderBatchJobsTable();

    initDefaultScript();

    initGradientLibrary();

    enableControls();
    enableShadingUI();
    enableDOFUI();

    enableXFormControls(null);

    refreshResolutionProfileCmb(resolutionProfileCmb, null);
    refreshResolutionProfileCmb(interactiveResolutionProfileCmb, null);
    refreshResolutionProfileCmb(batchResolutionProfileCmb, null);
    refreshResolutionProfileCmb(swfAnimatorResolutionProfileCmb, null);
    if (swfAnimatorResolutionProfileCmb.getItemCount() > 0) {
      swfAnimatorResolutionProfileCmb.setSelectedIndex(0);
    }
    refreshQualityProfileCmb(qualityProfileCmb, null);
    refreshQualityProfileCmb(interactiveQualityProfileCmb, null);
    refreshQualityProfileCmb(batchQualityProfileCmb, null);
    refreshQualityProfileCmb(swfAnimatorQualityProfileCmb, null);
    if (swfAnimatorQualityProfileCmb.getItemCount() > 0) {
      swfAnimatorQualityProfileCmb.setSelectedIndex(0);
    }

  }

  private void initHelpPane() {
    helpPane.setContentType("text/html");
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

      helpPane.setText(content.toString());
      helpPane.setSelectionStart(0);
      helpPane.setSelectionEnd(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initFAQPane() {
    faqPane.setContentType("text/html");
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

      faqPane.setText(content.toString());
      faqPane.setSelectionStart(0);
      faqPane.setSelectionEnd(0);
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
    scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
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

  private ImagePanel getColorChooserPalettePanel() {
    if (colorChooserPalettePanel == null) {
      int width = colorChooserPaletteImgPanel.getWidth();
      int height = colorChooserPaletteImgPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      colorChooserPalettePanel = new ImagePanel(img, 0, 0, colorChooserPaletteImgPanel.getWidth());
      colorChooserPaletteImgPanel.add(colorChooserPalettePanel, BorderLayout.CENTER);
      colorChooserPaletteImgPanel.getParent().validate();
    }
    return colorChooserPalettePanel;
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

          RendererType rendererType = (RendererType) rendererCmb.getSelectedItem();
          if (pQuickRender || rendererType == null) {
            rendererType = RendererType.JAVA;
          }

          switch (rendererType) {
            case JAVA: {
              FlameRenderer renderer;
              if (imgPanel.getMouseDragOperation() == MouseDragOperation.FOCUS) {
                renderer = new DrawFocusPointFlameRenderer(flame, prefs, toggleTransparencyButton.isSelected());
              }
              else {
                renderer = new FlameRenderer(flame, prefs, toggleTransparencyButton.isSelected());
              }
              if (pQuickRender) {
                renderer.setProgressUpdater(null);
                flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
                flame.setSpatialFilterRadius(0.0);
              }
              else {
                renderer.setProgressUpdater(mainProgressUpdater);
                flame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
                flame.setSpatialFilterRadius(0.0);
              }
              renderer.setRenderScale(renderScale);
              long t0 = System.currentTimeMillis();
              RenderedFlame res = renderer.renderFlame(info);
              long t1 = System.currentTimeMillis();
              imgPanel.setImage(res.getImage());
              showStatusMessage(flame, "render time: " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
            }
              break;
            case C32:
            case C64: {
              try {
                flame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
                CRendererInterface cudaRenderer = new CRendererInterface(rendererType, toggleTransparencyButton.isSelected());
                CRendererInterface.checkFlameForCUDA(flame);
                cudaRenderer.setProgressUpdater(mainProgressUpdater);
                long t0 = System.currentTimeMillis();
                RenderedFlame res = cudaRenderer.renderFlame(info, flame, prefs);
                long t1 = System.currentTimeMillis();
                showStatusMessage(flame, "render time: " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
                imgPanel.setImage(res.getImage());
              }
              catch (Throwable ex) {
                errorHandler.handleError(ex);
              }
            }
              break;
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

      cameraPerspectiveREd.setText(Tools.doubleToString(currFlame.getCamPerspective()));
      cameraPerspectiveSlider.setValue(Tools.FTOI(currFlame.getCamPerspective() * SLIDER_SCALE_PERSPECTIVE));

      cameraZoomREd.setText(Tools.doubleToString(currFlame.getCamZoom()));
      cameraZoomSlider.setValue(Tools.FTOI(currFlame.getCamZoom() * SLIDER_SCALE_ZOOM));

      cameraDOFREd.setText(Tools.doubleToString(currFlame.getCamDOF()));
      cameraDOFSlider.setValue(Tools.FTOI(currFlame.getCamDOF() * SLIDER_SCALE_DOF));

      cameraDOFAreaREd.setText(Tools.doubleToString(currFlame.getCamDOFArea()));
      cameraDOFAreaSlider.setValue(Tools.FTOI(currFlame.getCamDOFArea() * SLIDER_SCALE_DOF_AREA));

      cameraDOFExponentREd.setText(Tools.doubleToString(currFlame.getCamDOFExponent()));
      cameraDOFExponentSlider.setValue(Tools.FTOI(currFlame.getCamDOFExponent() * SLIDER_SCALE_DOF_EXPONENT));

      camZREd.setText(Tools.doubleToString(currFlame.getCamZ()));
      camZSlider.setValue(Tools.FTOI(currFlame.getCamZ() * SLIDER_SCALE_ZPOS));

      newDOFCBx.setSelected(currFlame.isNewCamDOF());

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

      deFilterRadiusREd.setText(Tools.doubleToString(currFlame.getDEFilterRadius()));
      deFilterRadiusSlider.setValue(Tools.FTOI(currFlame.getDEFilterRadius() * SLIDER_SCALE_DE_FILTER_RADIUS));
      deFilterAmountREd.setText(Tools.doubleToString(currFlame.getDEFilterAmount()));
      deFilterAmountSlider.setValue(Tools.FTOI(currFlame.getDEFilterAmount() * SLIDER_SCALE_DE_FILTER_AMOUNT));

      gammaThresholdREd.setText(String.valueOf(currFlame.getGammaThreshold()));
      gammaThresholdSlider.setValue(Tools.FTOI(currFlame.getGammaThreshold() * SLIDER_SCALE_GAMMA_THRESHOLD));

      bgTransparencyCBx.setSelected(currFlame.isBGTransparency());

      bgColorRedREd.setText(String.valueOf(currFlame.getBGColorRed()));
      bgColorRedSlider.setValue(currFlame.getBGColorRed());

      bgColorGreenREd.setText(String.valueOf(currFlame.getBGColorGreen()));
      bgColorGreenSlider.setValue(currFlame.getBGColorGreen());

      bgColorBlueREd.setText(String.valueOf(currFlame.getBGColorBlue()));
      bgColorBlueSlider.setValue(currFlame.getBGColorBlue());

      affinePreserveZButton.setSelected(currFlame.isPreserveZ());

      gridRefreshing = true;
      try {
        refreshTransformationsTable();
      }
      finally {
        gridRefreshing = false;
      }
      transformationTableClicked();

      shadingLightCmb.setSelectedIndex(0);
      refreshShadingUI();
      enableShadingUI();
      enableDOFUI();
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
    if (shadingInfo != null) {
      shadingCmb.setSelectedItem(shadingInfo.getShading());
      pseudo3DEnabled = shadingInfo.getShading().equals(Shading.PSEUDO3D);
      blurEnabled = shadingInfo.getShading().equals(Shading.BLUR);
    }
    else {
      shadingCmb.setSelectedIndex(0);
      pseudo3DEnabled = false;
      blurEnabled = false;
    }
    if (pseudo3DEnabled) {
      shadingAmbientREd.setText(Tools.doubleToString(shadingInfo.getAmbient()));
      shadingAmbientSlider.setValue(Tools.FTOI(shadingInfo.getAmbient() * SLIDER_SCALE_AMBIENT));
      shadingDiffuseREd.setText(Tools.doubleToString(shadingInfo.getDiffuse()));
      shadingDiffuseSlider.setValue(Tools.FTOI(shadingInfo.getDiffuse() * SLIDER_SCALE_AMBIENT));
      shadingPhongREd.setText(Tools.doubleToString(shadingInfo.getPhong()));
      shadingPhongSlider.setValue(Tools.FTOI(shadingInfo.getPhong() * SLIDER_SCALE_AMBIENT));
      shadingPhongSizeREd.setText(Tools.doubleToString(shadingInfo.getPhongSize()));
      shadingPhongSizeSlider.setValue(Tools.FTOI(shadingInfo.getPhongSize() * SLIDER_SCALE_PHONGSIZE));
      int cIdx = shadingLightCmb.getSelectedIndex();
      shadingLightXREd.setText(Tools.doubleToString(shadingInfo.getLightPosX()[cIdx]));
      shadingLightXSlider.setValue(Tools.FTOI(shadingInfo.getLightPosX()[cIdx] * SLIDER_SCALE_LIGHTPOS));
      shadingLightYREd.setText(Tools.doubleToString(shadingInfo.getLightPosY()[cIdx]));
      shadingLightYSlider.setValue(Tools.FTOI(shadingInfo.getLightPosY()[cIdx] * SLIDER_SCALE_LIGHTPOS));
      shadingLightZREd.setText(Tools.doubleToString(shadingInfo.getLightPosZ()[cIdx]));
      shadingLightZSlider.setValue(Tools.FTOI(shadingInfo.getLightPosZ()[cIdx] * SLIDER_SCALE_LIGHTPOS));
      shadingLightRedREd.setText(String.valueOf(shadingInfo.getLightRed()[cIdx]));
      shadingLightRedSlider.setValue(shadingInfo.getLightRed()[cIdx]);
      shadingLightGreenREd.setText(String.valueOf(shadingInfo.getLightGreen()[cIdx]));
      shadingLightGreenSlider.setValue(shadingInfo.getLightGreen()[cIdx]);
      shadingLightBlueREd.setText(String.valueOf(shadingInfo.getLightBlue()[cIdx]));
      shadingLightBlueSlider.setValue(shadingInfo.getLightBlue()[cIdx]);
    }
    else {
      shadingAmbientREd.setText("");
      shadingAmbientSlider.setValue(0);
      shadingDiffuseREd.setText("");
      shadingDiffuseSlider.setValue(0);
      shadingPhongREd.setText("");
      shadingPhongSlider.setValue(0);
      shadingPhongSizeREd.setText("");
      shadingPhongSizeSlider.setValue(0);
      shadingLightXREd.setText("");
      shadingLightXSlider.setValue(0);
      shadingLightYREd.setText("");
      shadingLightYSlider.setValue(0);
      shadingLightZREd.setText("");
      shadingLightZSlider.setValue(0);
      shadingLightRedREd.setText("");
      shadingLightRedSlider.setValue(0);
      shadingLightGreenREd.setText("");
      shadingLightGreenSlider.setValue(0);
      shadingLightBlueREd.setText("");
      shadingLightBlueSlider.setValue(0);
    }
    if (blurEnabled) {
      shadingBlurRadiusREd.setText(Tools.doubleToString(shadingInfo.getBlurRadius()));
      shadingBlurRadiusSlider.setValue(shadingInfo.getBlurRadius());
      shadingBlurFadeREd.setText(Tools.doubleToString(shadingInfo.getBlurFade()));
      shadingBlurFadeSlider.setValue(Tools.FTOI(shadingInfo.getBlurFade() * SLIDER_SCALE_AMBIENT));
      shadingBlurFallOffREd.setText(Tools.doubleToString(shadingInfo.getBlurFallOff()));
      shadingBlurFallOffSlider.setValue(Tools.FTOI(shadingInfo.getBlurFallOff() * SLIDER_SCALE_BLUR_FALLOFF));
    }
    else {
      shadingBlurRadiusREd.setText("");
      shadingBlurRadiusSlider.setValue(0);
      shadingBlurFadeREd.setText("");
      shadingBlurFadeSlider.setValue(0);
      shadingBlurFallOffREd.setText("");
      shadingBlurFallOffSlider.setValue(0);
    }
  }

  private void enableDOFUI() {
    boolean newDOF = currFlame != null ? currFlame.isNewCamDOF() : false;
    mouseTransformEditFocusPointButton.setEnabled(newDOF);
    focusXREd.setEnabled(newDOF);
    focusXSlider.setEnabled(newDOF);
    focusYREd.setEnabled(newDOF);
    focusYSlider.setEnabled(newDOF);
    focusZREd.setEnabled(newDOF);
    focusZSlider.setEnabled(newDOF);
    cameraDOFAreaREd.setEnabled(newDOF);
    cameraDOFAreaSlider.setEnabled(newDOF);
    cameraDOFExponentREd.setEnabled(newDOF);
    cameraDOFExponentSlider.setEnabled(newDOF);
    camZREd.setEnabled(!newDOF);
    camZSlider.setEnabled(!newDOF);
  }

  private void enableShadingUI() {
    ShadingInfo shadingInfo = currFlame != null ? currFlame.getShadingInfo() : null;
    boolean pseudo3DEnabled;
    boolean blurEnabled;
    if (shadingInfo != null) {
      shadingCmb.setEnabled(true);
      pseudo3DEnabled = shadingInfo.getShading().equals(Shading.PSEUDO3D);
      blurEnabled = shadingInfo.getShading().equals(Shading.BLUR);
    }
    else {
      shadingCmb.setEnabled(false);
      pseudo3DEnabled = false;
      blurEnabled = false;
    }
    // pseudo3d
    shadingAmbientREd.setEnabled(pseudo3DEnabled);
    shadingAmbientSlider.setEnabled(pseudo3DEnabled);
    shadingDiffuseREd.setEnabled(pseudo3DEnabled);
    shadingDiffuseSlider.setEnabled(pseudo3DEnabled);
    shadingPhongREd.setEnabled(pseudo3DEnabled);
    shadingPhongSlider.setEnabled(pseudo3DEnabled);
    shadingPhongSizeREd.setEnabled(pseudo3DEnabled);
    shadingPhongSizeSlider.setEnabled(pseudo3DEnabled);
    shadingLightCmb.setEnabled(pseudo3DEnabled);
    shadingLightXREd.setEnabled(pseudo3DEnabled);
    shadingLightXSlider.setEnabled(pseudo3DEnabled);
    shadingLightYREd.setEnabled(pseudo3DEnabled);
    shadingLightYSlider.setEnabled(pseudo3DEnabled);
    shadingLightZREd.setEnabled(pseudo3DEnabled);
    shadingLightZSlider.setEnabled(pseudo3DEnabled);
    shadingLightRedREd.setEnabled(pseudo3DEnabled);
    shadingLightRedSlider.setEnabled(pseudo3DEnabled);
    shadingLightGreenREd.setEnabled(pseudo3DEnabled);
    shadingLightGreenSlider.setEnabled(pseudo3DEnabled);
    shadingLightBlueREd.setEnabled(pseudo3DEnabled);
    shadingLightBlueSlider.setEnabled(pseudo3DEnabled);
    // blur
    shadingBlurRadiusREd.setEnabled(blurEnabled);
    shadingBlurRadiusSlider.setEnabled(blurEnabled);
    shadingBlurFadeREd.setEnabled(blurEnabled);
    shadingBlurFadeSlider.setEnabled(blurEnabled);
    shadingBlurFallOffREd.setEnabled(blurEnabled);
    shadingBlurFallOffSlider.setEnabled(blurEnabled);
  }

  private void refreshTransformationsTable() {
    final int COL_TRANSFORM = 0;
    final int COL_VARIATIONS = 1;
    final int COL_WEIGHT = 2;
    transformationsTable.setModel(new DefaultTableModel() {
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
          saveUndoPoint();
          currFlame.setPalette(palette);
          refreshPaletteUI(palette);
          refreshFlameImage(false);
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
    paletteSwapRGBREd.setText(String.valueOf(pPalette.getModSwapRGB()));
    paletteSwapRGBSlider.setValue(pPalette.getModSwapRGB());
    paletteFrequencyREd.setText(String.valueOf(pPalette.getModFrequency()));
    paletteFrequencySlider.setValue(pPalette.getModFrequency());
    paletteBlurREd.setText(String.valueOf(pPalette.getModBlur()));
    paletteBlurSlider.setValue(pPalette.getModBlur());
    paletteGammaREd.setText(String.valueOf(pPalette.getModGamma()));
    paletteGammaSlider.setValue(pPalette.getModGamma());
    paletteShiftREd.setText(String.valueOf(pPalette.getModShift()));
    paletteShiftSlider.setValue(pPalette.getModShift());
    paletteSaturationREd.setText(String.valueOf(pPalette.getModSaturation()));
    paletteSaturationSlider.setValue(pPalette.getModSaturation());
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

  public void deFilterRadiusREd_changed() {
    flameTextFieldChanged(deFilterRadiusSlider, deFilterRadiusREd, "deFilterRadius", SLIDER_SCALE_DE_FILTER_RADIUS);
  }

  public void deFilterAmountREd_changed() {
    flameTextFieldChanged(deFilterAmountSlider, deFilterAmountREd, "deFilterAmount", SLIDER_SCALE_DE_FILTER_AMOUNT);
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

  public void gammaThresholdREd_changed() {
    flameTextFieldChanged(gammaThresholdSlider, gammaThresholdREd, "gammaThreshold", SLIDER_SCALE_GAMMA_THRESHOLD);
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

  public void deFilterRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(deFilterRadiusSlider, deFilterRadiusREd, "deFilterRadius", SLIDER_SCALE_DE_FILTER_RADIUS);
  }

  public void deFilterAmountSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(deFilterAmountSlider, deFilterAmountREd, "deFilterAmount", SLIDER_SCALE_DE_FILTER_AMOUNT);
  }

  public void gammaThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(gammaThresholdSlider, gammaThresholdREd, "gammaThreshold", SLIDER_SCALE_GAMMA_THRESHOLD);
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
    if (currFlame != null) {
      RandomRGBPaletteGenerator generator = new RandomRGBPaletteGenerator();
      paletteKeyFrames = generator.generateKeyFrames(Integer.parseInt(paletteRandomPointsREd.getText()));
      refreshPaletteColorsTable();
      RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(paletteKeyFrames);
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
      paletteKeyFrames = null;
      saveUndoPoint();
      currFlame.setPalette(palette);
      refreshPaletteColorsTable();
      refreshPaletteUI(palette);
      refreshFlameImage(false);
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
      int row = transformationsTable.getSelectedRow();
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
        int selRow = relWeightsTable.getSelectedRow();
        XForm xForm = getCurrXForm();
        if (xForm != null && selRow >= 0) {
          relWeightREd.setText(Tools.doubleToString(xForm.getModifiedWeights()[selRow]));

        }
        else {
          relWeightREd.setText("");
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
    batchRenderAddFilesButton.setEnabled(idle);
    batchRenderFilesMoveDownButton.setEnabled(idle);
    batchRenderFilesMoveUpButton.setEnabled(idle);
    batchRenderFilesRemoveButton.setEnabled(idle);
    batchRenderFilesRemoveAllButton.setEnabled(idle);
    batchRenderStartButton.setText(idle ? "Render" : "Stop");
    batchRenderStartButton.invalidate();
    batchRenderStartButton.validate();
    rootTabbedPane.setEnabled(idle);
  }

  private void enableXFormControls(XForm xForm) {
    boolean enabled = xForm != null;

    affineRotateAmountREd.setEnabled(enabled);
    affineScaleAmountREd.setEnabled(enabled);
    affineMoveAmountREd.setEnabled(enabled);
    affineRotateLeftButton.setEnabled(enabled);
    affineRotateRightButton.setEnabled(enabled);
    affineEnlargeButton.setEnabled(enabled);
    affineShrinkButton.setEnabled(enabled);
    affineMoveUpButton.setEnabled(enabled);
    affineMoveLeftButton.setEnabled(enabled);
    affineMoveRightButton.setEnabled(enabled);
    affineMoveDownButton.setEnabled(enabled);
    affineFlipHorizontalButton.setEnabled(enabled);
    affineFlipVerticalButton.setEnabled(enabled);

    addTransformationButton.setEnabled(currFlame != null);
    duplicateTransformationButton.setEnabled(enabled);
    deleteTransformationButton.setEnabled(enabled);
    addFinalTransformationButton.setEnabled(currFlame != null);

    affineEditPostTransformButton.setEnabled(currFlame != null);
    affineEditPostTransformSmallButton.setEnabled(currFlame != null);
    mouseEditZoomInButton.setEnabled(currFlame != null);
    mouseEditZoomOutButton.setEnabled(currFlame != null);
    toggleVariationsButton.setEnabled(currFlame != null);

    affineC00REd.setEditable(enabled);
    affineC01REd.setEditable(enabled);
    affineC10REd.setEditable(enabled);
    affineC11REd.setEditable(enabled);
    affineC20REd.setEditable(enabled);
    affineC21REd.setEditable(enabled);
    affineResetTransformButton.setEnabled(enabled);

    transformationWeightREd.setEnabled(enabled);

    for (NonlinearControlsRow rows : nonlinearControlsRows) {
      rows.getNonlinearVarCmb().setEnabled(enabled);
      rows.getNonlinearVarREd().setEnabled(enabled);
      rows.getNonlinearParamsCmb().setEnabled(enabled);
      rows.getNonlinearParamsREd().setEnabled(enabled);
      // refreshing occurs in refreshXFormUI():
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

    xFormAntialiasAmountREd.setEnabled(enabled);
    xFormAntialiasAmountSlider.setEnabled(enabled);
    xFormAntialiasRadiusREd.setEnabled(enabled);
    xFormAntialiasRadiusSlider.setEnabled(enabled);
    xFormAntialiasCopyToAllBtn.setEnabled(enabled);

    relWeightsTable.setEnabled(enabled);
    enableRelWeightsControls();
  }

  public void enableRelWeightsControls() {
    int selRow = relWeightsTable.getSelectedRow();
    boolean enabled = relWeightsTable.isEnabled() && selRow >= 0 && getCurrXForm() != null;
    relWeightsZeroButton.setEnabled(enabled);
    relWeightsOneButton.setEnabled(enabled);
    relWeightREd.setEnabled(enabled);
  }

  private void refreshXFormUI(XForm pXForm) {
    boolean oldRefreshing = refreshing;
    boolean oldGridRefreshing = gridRefreshing;
    boolean oldCmbRefreshing = cmbRefreshing;
    boolean oldNoRefresh = noRefresh;
    gridRefreshing = cmbRefreshing = refreshing = noRefresh = true;
    try {

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

        xFormAntialiasAmountREd.setText(Tools.doubleToString(pXForm.getAntialiasAmount()));
        xFormAntialiasAmountSlider.setValue(Tools.FTOI(pXForm.getAntialiasAmount() * SLIDER_SCALE_COLOR));
        xFormAntialiasRadiusREd.setText(Tools.doubleToString(pXForm.getAntialiasRadius()));
        xFormAntialiasRadiusSlider.setValue(Tools.FTOI(pXForm.getAntialiasRadius() * SLIDER_SCALE_COLOR));

        transformationWeightREd.setText(Tools.doubleToString(pXForm.getWeight()));
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
        xFormAntialiasAmountREd.setText(null);
        xFormAntialiasAmountSlider.setValue(0);
        xFormAntialiasRadiusREd.setText(null);
        xFormAntialiasRadiusSlider.setValue(0);
        transformationWeightREd.setText(null);
        xFormDrawModeCmb.setSelectedIndex(-1);
      }

      {
        int idx = 0;
        for (NonlinearControlsRow row : nonlinearControlsRows) {
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

  public void addXForm() {
    XForm xForm = new XForm();
    xForm.addVariation(1.0, new Linear3DFunc());
    xForm.setWeight(0.5);
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
    transformationsTable.getSelectionModel().setSelectionInterval(row, row);
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
    transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(false);
  }

  public void deleteXForm() {
    int row = transformationsTable.getSelectedRow();
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
    transformationsTable.getSelectionModel().setSelectionInterval(row, row);
    refreshFlameImage(false);
  }

  private void forceTriangleMode() {
    if (flamePanel.getMouseDragOperation() != MouseDragOperation.TRIANGLE) {
      flamePanel.setMouseDragOperation(MouseDragOperation.TRIANGLE);
      flamePanel.setDrawTriangles(true);
      mouseTransformEditTrianglesButton.setSelected(true);
      mouseTransformEditPointsButton.setSelected(false);
      mouseTransformEditViewButton.setSelected(false);
      mouseTransformEditFocusPointButton.setSelected(false);
    }
  }

  public void xForm_moveRight(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(affineMoveAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), amount, 0, affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_rotateRight() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(affineRotateAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.rotate(getCurrXForm(), -amount, affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_moveLeft(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(affineMoveAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), -amount, 0, affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_flipHorizontal() {
    forceTriangleMode();
    saveUndoPoint();
    XFormTransformService.flipHorizontal(getCurrXForm(), affineEditPostTransformButton.isSelected());
    transformationTableClicked();
  }

  public void xForm_flipVertical() {
    forceTriangleMode();
    saveUndoPoint();
    XFormTransformService.flipVertical(getCurrXForm(), affineEditPostTransformButton.isSelected());
    transformationTableClicked();
  }

  public void xForm_enlarge() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(affineScaleAmountREd.getText()) / 100.0;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.scale(getCurrXForm(), amount, affineScaleXButton.isSelected(), affineScaleYButton.isSelected(), affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_shrink() {
    forceTriangleMode();
    double amount = 100.0 / Tools.stringToDouble(affineScaleAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.scale(getCurrXForm(), amount, affineScaleXButton.isSelected(), affineScaleYButton.isSelected(), affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_rotateLeft() {
    forceTriangleMode();
    double amount = Tools.stringToDouble(affineRotateAmountREd.getText());
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.rotate(getCurrXForm(), amount, affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_moveUp(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(affineMoveAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), 0, -amount, affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void xForm_moveDown(double pScale) {
    forceTriangleMode();
    double amount = Tools.stringToDouble(affineMoveAmountREd.getText()) * pScale;
    if (MathLib.fabs(amount) > MathLib.EPSILON) {
      saveUndoPoint();
      XFormTransformService.globalTranslate(getCurrXForm(), 0, amount, affineEditPostTransformButton.isSelected());
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
      SimpleImage img = randomBatch.get(i).getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
      // add it to the main panel
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(i * IMG_WIDTH + (i + 1) * BORDER_SIZE, BORDER_SIZE);
      randomBatch.get(i).setImgPanel(imgPanel);
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
          if (e.getClickCount() > 1) {
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
      transformationsTable.getSelectionModel().setSelectionInterval(0, 0);
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
        String fName = (String) nonlinearControlsRows[pIdx].getNonlinearVarCmb().getSelectedItem();
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
          String varStr = nonlinearControlsRows[pIdx].getNonlinearVarREd().getText();
          if (varStr == null || varStr.length() == 0) {
            varStr = "0";
          }
          var.setFunc(VariationFuncList.getVariationFuncInstance(fName));
          var.setAmount(Tools.stringToDouble(varStr));
          xForm.addVariation(var);
        }
        refreshParamCmb(nonlinearControlsRows[pIdx], xForm, var);
        refreshXFormUI(xForm);
        //        String selected = (String) nonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
        //        boolean enabled = selected != null && selected.length() > 0;
        //        nonlinearControlsRows[pIdx].getNonlinearParamsLeftButton().setEnabled(enabled);
        //        nonlinearControlsRows[pIdx].getNonlinearParamsRightButton().setEnabled(enabled);
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
          String varStr = nonlinearControlsRows[pIdx].getNonlinearVarREd().getText();
          if (varStr == null || varStr.length() == 0) {
            varStr = "0";
          }
          var.setAmount(Tools.stringToDouble(varStr) + pDelta);
          nonlinearControlsRows[pIdx].getNonlinearVarREd().setText(Tools.doubleToString(var.getAmount()));
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
      String selected = (String) nonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
      XForm xForm = getCurrXForm();
      if (xForm != null && selected != null && selected.length() > 0) {
        saveUndoPoint();
        if (pIdx < xForm.getVariationCount()) {
          Variation var = xForm.getVariation(pIdx);
          int idx;
          if ((idx = var.getFunc().getParameterIndex(selected)) >= 0) {
            String valStr = nonlinearControlsRows[pIdx].getNonlinearParamsREd().getText();
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
            nonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(Tools.doubleToString(val));
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
      String selected = (String) nonlinearControlsRows[pIdx].getNonlinearParamsCmb().getSelectedItem();
      XForm xForm = getCurrXForm();
      if (xForm != null && selected != null && selected.length() > 0) {
        if (pIdx < xForm.getVariationCount()) {
          Variation var = xForm.getVariation(pIdx);
          // params
          int idx;
          if ((idx = var.getFunc().getParameterIndex(selected)) >= 0) {
            enableNonlinearControls(nonlinearControlsRows[pIdx], false);
            Object val = var.getFunc().getParameterValues()[idx];
            if (val instanceof Double) {
              nonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(Tools.doubleToString((Double) val));
            }
            else {
              nonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(val.toString());
            }
          }
          // ressources
          else if ((idx = var.getFunc().getRessourceIndex(selected)) >= 0) {
            enableNonlinearControls(nonlinearControlsRows[pIdx], true);
            nonlinearControlsRows[pIdx].getNonlinearParamsREd().setText(null);
          }
          // empty
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

  private void enableNonlinearControls(NonlinearControlsRow pRow, boolean pRessource) {
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
        refreshFlameImage(false);
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

  public void xFormAntialiasAmountSlider_changed() {
    xFormSliderChanged(xFormAntialiasAmountSlider, xFormAntialiasAmountREd, "antialiasAmount", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasRadiusSlider_changed() {
    xFormSliderChanged(xFormAntialiasRadiusSlider, xFormAntialiasRadiusREd, "antialiasRadius", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasAmountREd_changed() {
    xFormTextFieldChanged(xFormAntialiasAmountSlider, xFormAntialiasAmountREd, "antialiasAmount", SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasRadiusREd_changed() {
    xFormTextFieldChanged(xFormAntialiasRadiusSlider, xFormAntialiasRadiusREd, "antialiasRadius", SLIDER_SCALE_COLOR);
  }

  private void setRelWeight(double pValue) {
    XForm xForm = getCurrXForm();
    if (xForm != null && currFlame != null && currFlame.getFinalXForms().indexOf(xForm) < 0) {
      int row = relWeightsTable.getSelectedRow();
      if (row >= 0 && row < currFlame.getXForms().size()) {
        xForm.getModifiedWeights()[row] = pValue;
        gridRefreshing = true;
        try {
          refreshRelWeightsTable();
          relWeightsTable.getSelectionModel().setSelectionInterval(row, row);
          refreshFlameImage(false);
        }
        finally {
          gridRefreshing = false;
        }
      }
    }
  }

  public void relWeightsREd_changed() {
    setRelWeight(Tools.stringToDouble(relWeightREd.getText()));
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
      xForm.setWeight(Tools.stringToDouble(transformationWeightREd.getText()));
      gridRefreshing = true;
      try {
        int row = transformationsTable.getSelectedRow();
        refreshTransformationsTable();
        transformationsTable.getSelectionModel().setSelectionInterval(row, row);
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
    RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(Integer.parseInt(paletteRandomPointsREd.getText()));
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
      cameraRollREd.setText(Tools.doubleToString(currFlame.getCamRoll()));
      cameraRollSlider.setValue(Tools.FTOI(currFlame.getCamRoll()));

      cameraPitchREd.setText(Tools.doubleToString(currFlame.getCamPitch()));
      cameraPitchSlider.setValue(Tools.FTOI(currFlame.getCamPitch()));

      cameraYawREd.setText(Tools.doubleToString(currFlame.getCamYaw()));
      cameraYawSlider.setValue(Tools.FTOI(currFlame.getCamYaw()));

      cameraCentreXREd.setText(Tools.doubleToString(currFlame.getCentreX()));
      cameraCentreXSlider.setValue(Tools.FTOI(currFlame.getCentreX() * SLIDER_SCALE_CENTRE));

      cameraCentreYREd.setText(Tools.doubleToString(currFlame.getCentreY()));
      cameraCentreYSlider.setValue(Tools.FTOI(currFlame.getCentreY() * SLIDER_SCALE_CENTRE));

      pixelsPerUnitREd.setText(Tools.doubleToString(currFlame.getPixelsPerUnit()));
      pixelsPerUnitSlider.setValue(Tools.FTOI(currFlame.getPixelsPerUnit()));

      focusXREd.setText(Tools.doubleToString(currFlame.getFocusX()));
      focusXSlider.setValue(Tools.FTOI(currFlame.getFocusX() * SLIDER_SCALE_ZPOS));

      focusYREd.setText(Tools.doubleToString(currFlame.getFocusY()));
      focusYSlider.setValue(Tools.FTOI(currFlame.getFocusY() * SLIDER_SCALE_ZPOS));

      focusZREd.setText(Tools.doubleToString(currFlame.getFocusZ()));
      focusZSlider.setValue(Tools.FTOI(currFlame.getFocusZ() * SLIDER_SCALE_ZPOS));

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
              transformationsTable.getSelectionModel().setSelectionInterval(i, i);
              return;
            }
          }
          for (int i = 0; i < flame.getFinalXForms().size(); i++) {
            if (xForm == flame.getFinalXForms().get(i)) {
              int row = flame.getXForms().size() + i;
              transformationsTable.getSelectionModel().setSelectionInterval(row, row);
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
        mouseTransformEditFocusPointButton.setSelected(false);
        mouseTransformEditPointsButton.setSelected(false);
        mouseTransformEditViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(mouseTransformEditTrianglesButton.isSelected() ? MouseDragOperation.TRIANGLE : MouseDragOperation.NONE);
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
        mouseTransformEditTrianglesButton.setSelected(false);
        mouseTransformEditPointsButton.setSelected(false);
        mouseTransformEditViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(mouseTransformEditFocusPointButton.isSelected() ? MouseDragOperation.FOCUS : MouseDragOperation.NONE);
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
        flamePanel.setEditPostTransform(affineEditPostTransformButton.isSelected());
      }
      refreshXFormUI(xForm);
      refreshFlameImage(false);
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
      double value = Tools.stringToDouble(affineC21REd.getText());
      if (affineEditPostTransformButton.isSelected()) {
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
      double value = Tools.stringToDouble(affineC20REd.getText());
      if (affineEditPostTransformButton.isSelected()) {
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
      XFormTransformService.reset(xForm, affineEditPostTransformButton.isSelected());
      transformationTableClicked();
    }
  }

  public void newDOFCBx_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      saveUndoPoint();
      flame.setNewCamDOF(newDOFCBx.isSelected());
      enableDOFUI();
    }
  }

  public void focusZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(focusZSlider, focusZREd, "focusZ", SLIDER_SCALE_ZPOS);
  }

  public void focusXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(focusXSlider, focusXREd, "focusX", SLIDER_SCALE_ZPOS);
  }

  public void focusYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(focusYSlider, focusYREd, "focusY", SLIDER_SCALE_ZPOS);
  }

  public void camZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(camZSlider, camZREd, "camZ", SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraDOFSlider, cameraDOFREd, "camDOF", SLIDER_SCALE_DOF);
  }

  public void cameraDOFAreaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraDOFAreaSlider, cameraDOFAreaREd, "camDOFArea", SLIDER_SCALE_DOF_AREA);
  }

  public void cameraDOFExponentSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(cameraDOFExponentSlider, cameraDOFExponentREd, "camDOFExponent", SLIDER_SCALE_DOF_EXPONENT);
  }

  public void focusXREd_changed() {
    flameTextFieldChanged(focusXSlider, focusXREd, "focusX", SLIDER_SCALE_ZPOS);
  }

  public void focusYREd_changed() {
    flameTextFieldChanged(focusYSlider, focusYREd, "focusY", SLIDER_SCALE_ZPOS);
  }

  public void focusZREd_changed() {
    flameTextFieldChanged(focusZSlider, focusZREd, "focusZ", SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFREd_changed() {
    flameTextFieldChanged(cameraDOFSlider, cameraDOFREd, "camDOF", SLIDER_SCALE_DOF);
  }

  public void camZREd_changed() {
    flameTextFieldChanged(camZSlider, camZREd, "camZ", SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFAreaREd_changed() {
    flameTextFieldChanged(cameraDOFAreaSlider, cameraDOFAreaREd, "camDOFArea", SLIDER_SCALE_DOF_AREA);
  }

  public void cameraDOFExponentREd_changed() {
    flameTextFieldChanged(cameraDOFExponentSlider, cameraDOFExponentREd, "camDOFExponent", SLIDER_SCALE_DOF_EXPONENT);
  }

  public void shadingAmbientREd_changed() {
    shadingInfoTextFieldChanged(shadingAmbientSlider, shadingAmbientREd, "ambient", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDiffuseREd_changed() {
    shadingInfoTextFieldChanged(shadingDiffuseSlider, shadingDiffuseREd, "diffuse", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongREd_changed() {
    shadingInfoTextFieldChanged(shadingPhongSlider, shadingPhongREd, "phong", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongSizeREd_changed() {
    shadingInfoTextFieldChanged(shadingPhongSizeSlider, shadingPhongSizeREd, "phongSize", SLIDER_SCALE_PHONGSIZE, 0);
  }

  public void shadingLightXREd_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(shadingLightXSlider, shadingLightXREd, "lightPosX", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightYREd_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(shadingLightYSlider, shadingLightYREd, "lightPosY", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightZREd_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(shadingLightZSlider, shadingLightZREd, "lightPosZ", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightRedREd_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(shadingLightRedSlider, shadingLightRedREd, "lightRed", 1.0, cIdx);
  }

  public void shadingLightGreenREd_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(shadingLightGreenSlider, shadingLightGreenREd, "lightGreen", 1.0, cIdx);
  }

  public void shadingLightBlueREd_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(shadingLightBlueSlider, shadingLightBlueREd, "lightBlue", 1.0, cIdx);
  }

  public void shadingLightBlueSlider_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(shadingLightBlueSlider, shadingLightBlueREd, "lightBlue", 1.0, cIdx);
  }

  public void shadingLightGreenSlider_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(shadingLightGreenSlider, shadingLightGreenREd, "lightGreen", 1.0, cIdx);
  }

  public void shadingAmbientSlider_changed() {
    shadingInfoSliderChanged(shadingAmbientSlider, shadingAmbientREd, "ambient", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDiffuseSlider_changed() {
    shadingInfoSliderChanged(shadingDiffuseSlider, shadingDiffuseREd, "diffuse", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongSlider_changed() {
    shadingInfoSliderChanged(shadingPhongSlider, shadingPhongREd, "phong", SLIDER_SCALE_AMBIENT, 0);
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) resolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private ResolutionProfile getBatchRenderResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) batchResolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private QualityProfile getQualityProfile() {
    QualityProfile res = (QualityProfile) qualityProfileCmb.getSelectedItem();
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
      resolutionProfileCmb.requestFocus();
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
      currFlame.getShadingInfo().setShading((Shading) shadingCmb.getSelectedItem());
      refreshShadingUI();
      enableShadingUI();
      refreshFlameImage(false);
    }
    finally {
      noRefresh = false;
    }
  }

  public void shadingLightXSlider_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(shadingLightXSlider, shadingLightXREd, "lightPosX", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightYSlider_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(shadingLightYSlider, shadingLightYREd, "lightPosY", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightZSlider_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(shadingLightZSlider, shadingLightZREd, "lightPosZ", SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightRedSlider_changed() {
    int cIdx = shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(shadingLightRedSlider, shadingLightRedREd, "lightRed", 1.0, cIdx);
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
    shadingInfoSliderChanged(shadingPhongSizeSlider, shadingPhongSizeREd, "phongSize", SLIDER_SCALE_PHONGSIZE, 0);
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

  public void checkCUDACompatibility() {
    try {
      if (currFlame != null) {
        CRendererInterface.checkFlameForCUDA(currFlame);
        JOptionPane.showMessageDialog(centerPanel, "Flame should render fine under JWildfireC(UDA)", "JWildfireC(UDA) compatibility check", JOptionPane.INFORMATION_MESSAGE);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void mouseTransformSlowButton_clicked() {
    if (flamePanel != null) {
      flamePanel.setFineMovement(mouseTransformSlowButton.isSelected());
    }
  }

  @Override
  public void refreshRenderBatchJobsTable() {
    final int COL_FLAME_FILE = 0;
    final int COL_FINISHED = 1;
    final int COL_ELAPSED = 2;
    final int COL_LAST_ERROR = 3;
    renderBatchJobsTable.setModel(new DefaultTableModel() {
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
    renderBatchJobsTable.getTableHeader().setFont(transformationsTable.getFont());
    renderBatchJobsTable.getColumnModel().getColumn(COL_FLAME_FILE).setWidth(120);
    renderBatchJobsTable.getColumnModel().getColumn(COL_FINISHED).setPreferredWidth(10);
    renderBatchJobsTable.getColumnModel().getColumn(COL_ELAPSED).setWidth(10);
    renderBatchJobsTable.getColumnModel().getColumn(COL_LAST_ERROR).setWidth(120);
    if (batchRenderList.size() > 0)
      renderBatchJobsTable.setRowSelectionInterval(0, 0);
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
    int row = renderBatchJobsTable.getSelectedRow();
    if (row < 0 && batchRenderList.size() > 0) {
      row = 0;
      renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
    }
    else if (row > 0 && row < batchRenderList.size()) {
      Job t = batchRenderList.get(row - 1);
      batchRenderList.set(row - 1, batchRenderList.get(row));
      batchRenderList.set(row, t);
      refreshRenderBatchJobsTable();
      renderBatchJobsTable.getSelectionModel().setSelectionInterval(row - 1, row - 1);
    }
  }

  public void batchRenderFilesMoveDownButton_clicked() {
    if (jobRenderThread != null) {
      return;
    }
    int row = renderBatchJobsTable.getSelectedRow();
    if (row < 0 && batchRenderList.size() > 0) {
      row = 0;
      renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
    }
    else if (row >= 0 && row < batchRenderList.size() - 1) {
      Job t = batchRenderList.get(row + 1);
      batchRenderList.set(row + 1, batchRenderList.get(row));
      batchRenderList.set(row, t);
      refreshRenderBatchJobsTable();
      renderBatchJobsTable.getSelectionModel().setSelectionInterval(row + 1, row + 1);
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
      jobRenderThread = new JobRenderThread(this, activeJobList, (ResolutionProfile) batchResolutionProfileCmb.getSelectedItem(), (QualityProfile) batchQualityProfileCmb.getSelectedItem(), (RendererType) batchRendererCmb.getSelectedItem());
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
    int row = renderBatchJobsTable.getSelectedRow();
    if (row >= 0 && row < batchRenderList.size()) {
      batchRenderList.remove(row);
      refreshRenderBatchJobsTable();
      if (row >= batchRenderList.size()) {
        row--;
      }
      if (row >= 0 && row < batchRenderList.size()) {
        renderBatchJobsTable.getSelectionModel().setSelectionInterval(row, row);
      }
    }
  }

  @Override
  public Prefs getPrefs() {
    return prefs;
  }

  @Override
  public JProgressBar getTotalProgressBar() {
    return batchRenderTotalProgressBar;
  }

  @Override
  public JProgressBar getJobProgressBar() {
    return batchRenderJobProgressBar;
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
    return renderBatchJobsTable;
  }

  public void toggleDarkTriangles() {
    if (refreshing) {
      return;
    }
    if (flamePanel != null) {
      flamePanel.setDarkTriangles(toggleDarkTrianglesButton.isSelected());
      refreshFlameImage(false);
    }
  }

  public void shadingBlurFadeREd_changed() {
    shadingInfoTextFieldChanged(shadingBlurFadeSlider, shadingBlurFadeREd, "blurFade", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingBlurFallOffREd_changed() {
    shadingInfoTextFieldChanged(shadingBlurFallOffSlider, shadingBlurFallOffREd, "blurFallOff", SLIDER_SCALE_BLUR_FALLOFF, 0);
  }

  public void shadingBlurRadiusREd_changed() {
    shadingInfoTextFieldChanged(shadingBlurRadiusSlider, shadingBlurRadiusREd, "blurRadius", 1.0, 0);
  }

  public void shadingBlurFallOffSlider_changed() {
    shadingInfoSliderChanged(shadingBlurFallOffSlider, shadingBlurFallOffREd, "blurFallOff", SLIDER_SCALE_BLUR_FALLOFF, 0);
  }

  public void shadingBlurFadeSlider_changed() {
    shadingInfoSliderChanged(shadingBlurFadeSlider, shadingBlurFadeREd, "blurFade", SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingBlurRadiusSlider_changed() {
    shadingInfoSliderChanged(shadingBlurRadiusSlider, shadingBlurRadiusREd, "blurRadius", 1.0, 0);
  }

  private ScriptRunner compileScript() throws Exception {
    return ScriptRunner.compile(scriptTextArea.getText());
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
      flamePanel.setAllowScaleX(affineScaleXButton.isSelected());
    }
  }

  public void affineScaleYButton_stateChanged() {
    if (flamePanel != null) {
      flamePanel.setAllowScaleY(affineScaleYButton.isSelected());
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
    paletteTextFieldChanged(paletteSwapRGBSlider, paletteSwapRGBREd, "modSwapRGB", 1.0);
  }

  public void paletteSwapRGBSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteSwapRGBSlider, paletteSwapRGBREd, "modSwapRGB", 1.0);
  }

  public void paletteFrequencyREd_changed() {
    paletteTextFieldChanged(paletteFrequencySlider, paletteFrequencyREd, "modFrequency", 1.0);
  }

  public void paletteFrequencySlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteFrequencySlider, paletteFrequencyREd, "modFrequency", 1.0);
  }

  public void paletteBlurREd_changed() {
    paletteTextFieldChanged(paletteBlurSlider, paletteBlurREd, "modBlur", 1.0);
  }

  public void paletteBlurSlider_stateChanged(ChangeEvent e) {
    paletteSliderChanged(paletteBlurSlider, paletteBlurREd, "modBlur", 1.0);
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

      gradientLibraryGradientCmb.removeAllItems();
      List<SimpleImage> thumbnailsList = new ArrayList<SimpleImage>();
      for (RGBPalette palette : gradientLibraryList) {
        thumbnailsList.add(new RGBPaletteRenderer().renderHorizPalette(palette, RGBPalette.PALETTE_SIZE, GRADIENT_THUMB_HEIGHT));
        gradientLibraryGradientCmb.addItem(palette);
      }
      gradientLibraryGradientCmb.setSelectedIndex(-1);
      updateGradientThumbnails(thumbnailsList);
    }
    finally {
      cmbRefreshing = oldCmbRefreshing;
    }
  }

  public void updateGradientThumbnails(List<SimpleImage> pImages) {
    if (gradientLibraryScrollPane != null) {
      gradientLibraryPanel.remove(gradientLibraryScrollPane);
      gradientLibraryScrollPane = null;
    }
    int panelWidth = gradientLibraryPanel.getBounds().width - 2 * GRADIENT_THUMB_BORDER;
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
    gradientLibraryScrollPane = new JScrollPane(gradientsPanel);
    gradientLibraryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    gradientLibraryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    gradientLibraryPanel.add(gradientLibraryScrollPane, BorderLayout.CENTER);
    gradientLibraryPanel.validate();
  }

  private void importFromGradientLibrary(int idx) {
    if (idx >= 0 && idx < gradientLibraryList.size()) {
      gradientLibraryGradientCmb.setSelectedItem(gradientLibraryList.get(idx));
    }
  }

  public void gradientLibraryGradientChanged() {
    if (!cmbRefreshing) {
      if (currFlame != null && gradientLibraryGradientCmb.getSelectedIndex() >= 0 && gradientLibraryGradientCmb.getSelectedIndex() < gradientLibraryList.size()) {
        saveUndoPoint();
        RGBPalette palette = gradientLibraryList.get(gradientLibraryGradientCmb.getSelectedIndex()).makeCopy();
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
      double value = Tools.stringToDouble(affineC01REd.getText());
      if (affineEditPostTransformButton.isSelected()) {
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
      double value = Tools.stringToDouble(affineC11REd.getText());
      if (affineEditPostTransformButton.isSelected()) {
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
      double value = Tools.stringToDouble(affineC00REd.getText());
      if (affineEditPostTransformButton.isSelected()) {
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
      double value = Tools.stringToDouble(affineC10REd.getText());
      if (affineEditPostTransformButton.isSelected()) {
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
      flamePanel.setDrawVariations(toggleVariationsButton.isSelected());
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
      currFlame.setPreserveZ(affinePreserveZButton.isSelected());
      refreshFlameImage(false);
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

      if (pFlame.getQualityProfile() != null) {
        QualityProfile profile = null;
        for (int i = 0; i < qualityProfileCmb.getItemCount(); i++) {
          profile = (QualityProfile) qualityProfileCmb.getItemAt(i);
          if (pFlame.getQualityProfile().equals(profile.toString()))
            break;
          else
            profile = null;
        }
        if (profile != null) {
          qualityProfileCmb.setSelectedItem(profile);
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

        refreshQualityProfileCmb(qualityProfileCmb, profile);
        refreshQualityProfileCmb(interactiveQualityProfileCmb, profile);
        refreshQualityProfileCmb(swfAnimatorQualityProfileCmb, profile);
        refreshQualityProfileCmb(batchQualityProfileCmb, profile);
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
        refreshResolutionProfileCmb(resolutionProfileCmb, profile);
        refreshResolutionProfileCmb(interactiveResolutionProfileCmb, profile);
        refreshResolutionProfileCmb(swfAnimatorResolutionProfileCmb, profile);
        refreshResolutionProfileCmb(batchResolutionProfileCmb, profile);
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
    scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
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
    scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
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
    scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
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
    scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
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
    scriptTextArea.setText("import org.jwildfire.create.tina.base.Flame;\r\n" +
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
        scriptTextArea.setText(script);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void saveScript() {
    try {
      String script = scriptTextArea.getText();
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
        mouseTransformEditTrianglesButton.setSelected(false);
        mouseTransformEditFocusPointButton.setSelected(false);
        mouseTransformEditViewButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(mouseTransformEditPointsButton.isSelected() ? MouseDragOperation.POINTS : MouseDragOperation.NONE);
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
        mouseTransformEditTrianglesButton.setSelected(false);
        mouseTransformEditFocusPointButton.setSelected(false);
        mouseTransformEditPointsButton.setSelected(false);
        if (flamePanel != null) {
          flamePanel.setMouseDragOperation(mouseTransformEditViewButton.isSelected() ? MouseDragOperation.VIEW : MouseDragOperation.NONE);
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
        int txRow = transformationsTable.getSelectedRow();
        undoManager.saveUndoPoint(currFlame);
        undoManager.undo(currFlame);
        enableUndoControls();
        refreshUI();
        if (txRow >= 0) {
          transformationsTable.getSelectionModel().setSelectionInterval(txRow, txRow);
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
        int txRow = transformationsTable.getSelectedRow();
        undoManager.redo(currFlame);
        enableUndoControls();
        refreshUI();
        if (txRow >= 0) {
          transformationsTable.getSelectionModel().setSelectionInterval(txRow, txRow);
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
      undoButton.invalidate();
      undoButton.validate();
      redoButton.invalidate();
      redoButton.validate();
      undoButton.getParent().repaint();
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
        paletteKeyFrames = null;
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
      flamePanel.setShowTransparency(toggleTransparencyButton.isSelected());
      refreshFlameImage(false);
    }
  }

  public void flameTransparencyCbx_changed() {
    if (currFlame != null) {
      currFlame.setBGTransparency(bgTransparencyCBx.isSelected());
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
        int row = renderBatchJobsTable.getSelectedRow();
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
      int width = batchPreviewRootPanel.getWidth();
      int height = batchPreviewRootPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      batchPreviewFlamePanel = new FlamePanel(img, 0, 0, batchPreviewRootPanel.getWidth(), getBatchRenderPreviewFlameHolder());
      ResolutionProfile resProfile = getBatchRenderResolutionProfile();
      batchPreviewFlamePanel.setRenderWidth(resProfile.getWidth());
      batchPreviewFlamePanel.setRenderHeight(resProfile.getHeight());
      batchPreviewFlamePanel.setDrawTriangles(false);
      batchPreviewRootPanel.add(batchPreviewFlamePanel, BorderLayout.CENTER);
      batchPreviewRootPanel.getParent().validate();
      batchPreviewRootPanel.repaint();
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

        FlameRenderer renderer = new FlameRenderer(flame, prefs, toggleTransparencyButton.isSelected());
        flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
        flame.setSpatialFilterRadius(0.0);
        RenderedFlame res = renderer.renderFlame(info);
        imgPanel.setImage(res.getImage());
      }
      else {
        imgPanel.setImage(new SimpleImage(width, height));
      }
    }
    batchPreviewRootPanel.invalidate();
    batchPreviewRootPanel.validate();
  }

  public void batchRendererResolutionProfileCmb_changed() {
    if (batchPreviewFlamePanel != null) {
      batchPreviewRootPanel.remove(batchPreviewFlamePanel);
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
            RendererType rendererType = (RendererType) rendererCmb.getSelectedItem();
            if (rendererType == null) {
              rendererType = RendererType.JAVA;
            }
            flame.setSampleDensity(qualProfile.getQuality());
            switch (rendererType) {
              case JAVA: {
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
                break;
              case C32:
              case C64: {
                CRendererInterface cudaRenderer = new CRendererInterface(rendererType, flame.isBGTransparency());
                CRendererInterface.checkFlameForCUDA(flame);
                cudaRenderer.setProgressUpdater(mainProgressUpdater);
                if (info.isRenderHDR()) {
                  String hdrFilename = file.getAbsolutePath() + ".hdr";
                  cudaRenderer.setHDROutputfilename(hdrFilename);
                  // do not allocate unnessary memory as the HDR file is completely generated and saved by the external renderer 
                  info.setRenderHDR(false);
                  info.setRenderHDRIntensityMap(false);
                }
                long t0 = System.currentTimeMillis();
                RenderedFlame res = cudaRenderer.renderFlame(info, flame, prefs);
                long t1 = System.currentTimeMillis();
                showStatusMessage(flame, "render time: " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
                new ImageWriter().saveImage(res.getImage(), file.getAbsolutePath());
              }
                break;
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
    renderMainButton.setText(mainRenderThread == null ? "Render" : "Cancel render");
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
          RendererType rendererType = (RendererType) rendererCmb.getSelectedItem();

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
          mainRenderThread = new RenderMainFlameThread(prefs, flame, file, qualProfile, resProfile, rendererType, finishEvent, mainProgressUpdater);
          enableMainRenderControls();
          new Thread(mainRenderThread).start();

        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }
}
