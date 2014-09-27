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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.GlobalScriptType;
import org.jwildfire.create.tina.animate.XFormScriptType;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.PostSymmetryType;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.Stereo3dColor;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.Stereo3dPreview;
import org.jwildfire.create.tina.dance.DancingFractalsController;
import org.jwildfire.create.tina.meshgen.filter.PreFilterType;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randommovie.RandomMovieGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.render.ChannelMixerMode;
import org.jwildfire.create.tina.render.dof.DOFBlurShapeType;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelControlStyle;
import org.jwildfire.swing.StandardErrorHandler;

public class TinaInternalFrame extends JInternalFrame {
  private TinaController tinaController; //  @jve:decl-index=0:
  private TinaNonlinearControlsRow[] nonlinearControlsRows;//  @jve:decl-index=0:
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null; //  @jve:decl-index=0:visual-constraint="10,10"

  private JPanel rootPanel = null;

  private JPanel tinaNorthPanel = null;

  private JPanel randomBatchPanel = null;

  private JPanel tinaEastPanel = null;

  private JPanel tinaSouthPanel = null;

  private JPanel tinaCenterPanel = null;

  private JPanel tinaDOFPanel = null;

  private JTabbedPane tinaSouthTabbedPane = null;

  private JPanel tinaCameraPanel = null;

  private JPanel tinaColoringPanel = null;

  private JLabel tinaCameraRollLbl = null;

  private JWFNumberField tinaCameraRollREd = null;

  private JLabel tinaCameraPitchLbl = null;

  private JWFNumberField tinaCameraPitchREd = null;

  private JLabel tinaCameraYawLbl = null;

  private JWFNumberField tinaCameraYawREd = null;

  private JLabel tinaCameraPerspectiveLbl = null;

  private JWFNumberField tinaCameraPerspectiveREd = null;

  private JSlider tinaCameraRollSlider = null;

  private JSlider tinaCameraPitchSlider = null;

  private JSlider tinaCameraYawSlider = null;

  private JSlider tinaCameraPerspectiveSlider = null;

  private JButton tinaLoadFlameButton = null;

  private JButton tinaSaveFlameButton = null;

  private JButton tinaRenderFlameButton = null;

  private JButton renderMainButton = null;

  private JLabel tinaCameraCentreXLbl = null;

  private JWFNumberField tinaCameraCentreXREd = null;

  private JLabel tinaCameraCentreYLbl = null;

  private JWFNumberField tinaCameraCentreYREd = null;

  private JSlider tinaCameraCentreXSlider = null;

  private JSlider tinaCameraCentreYSlider = null;

  private JLabel tinaCameraZoomLbl = null;

  private JWFNumberField tinaCameraZoomREd = null;

  private JSlider tinaCameraZoomSlider = null;

  private JLabel tinaBrightnessLbl = null;

  private JWFNumberField tinaBrightnessREd = null;

  private JSlider tinaBrightnessSlider = null;

  private JLabel tinaContrastLbl = null;

  private JLabel tinaGammaLbl = null;

  private JLabel tinaVibrancyLbl = null;

  private JLabel tinaFilterRadiusLbl = null;

  private JLabel tinaGammaThresholdLbl;

  private JLabel tinaPixelsPerUnitLbl = null;

  private JWFNumberField tinaPixelsPerUnitREd = null;

  private JSlider tinaPixelsPerUnitSlider = null;

  private JLabel tinaBGColorLbl = null;

  private JWFNumberField tinaContrastREd = null;

  private JWFNumberField tinaGammaREd = null;

  private JWFNumberField tinaVibrancyREd = null;

  private JWFNumberField tinaGammaThresholdREd = null;

  private JSlider tinaContrastSlider = null;

  private JSlider tinaGammaSlider = null;

  private JSlider tinaVibrancySlider = null;

  private JSlider tinaGammaThresholdSlider = null;

  private JButton tinaAddTransformationButton = null;

  private JTabbedPane tinaEastTabbedPane = null;

  private JPanel tinaTransformationsPanel = null;

  private JPanel tinaPalettePanel = null;

  private JButton tinaDeleteTransformationButton = null;

  private JButton tinaDuplicateTransformationButton = null;

  private JScrollPane tinaTransformationsScrollPane = null;

  private JTable tinaTransformationsTable = null;

  private JTabbedPane tinaTransformationsTabbedPane = null;

  private JPanel tinaAffineTransformationPanel = null;

  private JPanel tinaVariationPanel = null;

  private JPanel tinaModifiedWeightsPanel = null;

  private JPanel tinaTransformationColorPanel = null;

  private JLabel tinaPaletteRandomPointsLbl = null;

  private JTextField tinaPaletteRandomPointsREd = null;

  private JButton tinaRandomPaletteButton = null;

  private JButton distributeColorsButton = null;

  private JPanel tinaPaletteImgPanel = null;

  private JPanel tinaPaletteSubNorthPanel = null;

  private JPanel tinaPaletteSubSouthPanel = null;

  private JPanel tinaPaletteSubCenterPanel = null;

  private JTabbedPane tinaPaletteSubTabbedPane = null;

  private JPanel tinaPaletteCreatePanel = null;

  private JPanel tinaPaletteBalancingPanel = null;

  private JLabel tinaPaletteShiftLbl = null;

  private JLabel tinaPaletteRedLbl = null;

  private JLabel tinaPaletteGreenLbl = null;

  private JLabel tinaPaletteBlueLbl = null;

  private JLabel tinaPaletteHueLbl = null;

  private JLabel tinaPaletteSaturationLbl = null;

  private JLabel tinaPaletteContrastLbl = null;

  private JLabel tinaPaletteGammaLbl = null;

  private JLabel tinaPaletteBrightnessLbl = null;

  private JWFNumberField tinaPaletteShiftREd = null;

  private JWFNumberField tinaPaletteRedREd = null;

  private JWFNumberField tinaPaletteGreenREd = null;

  private JWFNumberField tinaPaletteBlueREd = null;

  private JWFNumberField tinaPaletteHueREd = null;

  private JWFNumberField tinaPaletteSaturationREd = null;

  private JWFNumberField tinaPaletteContrastREd = null;

  private JWFNumberField tinaPaletteGammaREd = null;

  private JWFNumberField tinaPaletteBrightnessREd = null;

  private JSlider tinaPaletteShiftSlider = null;

  private JSlider tinaPaletteRedSlider = null;

  private JSlider tinaPaletteGreenSlider = null;

  private JSlider tinaPaletteBlueSlider = null;

  private JSlider tinaPaletteHueSlider = null;

  private JSlider tinaPaletteSaturationSlider = null;

  private JSlider tinaPaletteContrastSlider = null;

  private JSlider tinaPaletteGammaSlider = null;

  private JSlider tinaPaletteBrightnessSlider = null;

  private JButton tinaAddFinalTransformationButton = null;
  private JLabel affineC00Lbl = null;
  private JWFNumberField affineC00REd = null;
  private JLabel affineC01Lbl = null;
  private JWFNumberField affineC01REd = null;
  private JLabel affineC10Lbl = null;
  private JLabel affineC11Lbl = null;
  private JWFNumberField affineC10REd = null;
  private JWFNumberField affineC11REd = null;
  private JLabel affineC20Lbl = null;
  private JLabel affineC21Lbl = null;
  private JWFNumberField affineC20REd = null;
  private JWFNumberField affineC21REd = null;
  private JButton affineRotateLeftButton = null;
  private JButton affineRotateRightButton = null;
  private JButton affineEnlargeButton = null;
  private JButton affineShrinkButton = null;
  private JWFNumberField affineRotateAmountREd = null;
  private JPanel transformationsNorthPanel = null;
  private JPanel trnsformationsEastPanel = null;
  private JSplitPane transformationsSplitPane = null;
  private JWFNumberField affineScaleAmountREd = null;
  private JButton affineMoveUpButton = null;
  private JButton affineMoveDownButton = null;
  private JButton affineMoveLeftButton = null;
  private JButton affineMoveRightButton = null;
  private JWFNumberField affineMoveVertAmountREd = null;
  private JButton randomBatchButton = null;
  private JLabel nonlinearVar1Lbl = null;
  private JComboBox nonlinearVar1Cmb = null;
  private JWFNumberField nonlinearVar1REd = null;
  private JLabel nonlinearParams1Lbl = null;
  private JComboBox nonlinearParams1Cmb = null;
  private JWFNumberField nonlinearParams1REd = null;
  private JPanel nonlinearVar1Panel = null;
  private JButton nonlinearParams1LeftButton = null;
  private JPanel nonlinearVar2Panel = null;
  private JLabel nonlinearVar2Lbl = null;
  private JComboBox nonlinearVar2Cmb = null;
  private JWFNumberField nonlinearVar2REd = null;
  private JLabel nonlinearParams2Lbl = null;
  private JComboBox nonlinearParams2Cmb = null;
  private JWFNumberField nonlinearParams2REd = null;
  private JButton nonlinearParams2LeftButton = null;
  private JPanel nonlinearVar3Panel = null;
  private JLabel nonlinearVar3Lbl = null;
  private JComboBox nonlinearVar3Cmb = null;
  private JWFNumberField nonlinearVar3REd = null;
  private JLabel nonlinearParams3Lbl = null;
  private JComboBox nonlinearParams3Cmb = null;
  private JWFNumberField nonlinearParams3REd = null;
  private JButton nonlinearParams3LeftButton = null;
  private JLabel xFormColorLbl = null;
  private JWFNumberField xFormColorREd = null;
  private JSlider xFormColorSlider = null;
  private JLabel xFormSymmetryLbl = null;
  private JWFNumberField xFormSymmetryREd = null;
  private JSlider xFormSymmetrySlider = null;
  private JLabel xFormOpacityLbl = null;
  private JWFNumberField xFormOpacityREd = null;
  private JSlider xFormOpacitySlider = null;
  private JLabel xFormDrawModeLbl = null;
  private JComboBox xFormDrawModeCmb = null;
  private JPanel relWeightsEastPanel = null;
  private JButton relWeightsZeroButton = null;
  private JButton relWeightsOneButton = null;
  private JScrollPane relWeightsScrollPane = null;
  private JTable relWeightsTable = null;
  private JButton newFlameButton = null;
  private JPanel tinaSWFAnimatorPanel = null;
  private JButton swfAnimatorGenerateButton = null;
  private JWFNumberField swfAnimatorFramesREd = null;
  private JLabel animateFramesLbl = null;
  private JLabel animateGlobalScriptLbl = null;
  private JComboBox swfAnimatorGlobalScript1Cmb = null;
  private JLabel animateXFormScriptLbl = null;
  private JComboBox swfAnimatorXFormScript1Cmb = null;
  private JPanel triangleOperationsPanel = null;
  private JToggleButton mouseTransformMoveTrianglesButton = null;
  private JToggleButton mouseTransformEditFocusPointButton = null;
  private JPanel centerNorthPanel = null;
  private JPanel centerWestPanel = null;
  private JPanel centerCenterPanel = null;
  private JLabel centerDescLabel = null;
  private JComboBox randomStyleCmb = null;
  private JLabel randomStyleLbl = null;
  private JToggleButton affineEditPostTransformButton = null;
  private JToggleButton affineEditPostTransformSmallButton = null;
  private JLabel editSpaceLbl1 = null;
  private JLabel editSpaceLbl2 = null;
  private JLabel editSpaceLbl3 = null;
  private JToggleButton toggleVariationsButton = null;
  private JLabel editSpaceLbl4 = null;
  private JProgressBar renderProgressBar = null;
  private JButton affineResetTransformButton = null;
  private JPanel nonlinearVar4Panel = null;
  private JLabel nonlinearVar4Lbl = null;
  private JComboBox nonlinearVar4Cmb = null;
  private JWFNumberField nonlinearVar4REd = null;
  private JLabel nonlinearParams4Lbl = null;
  private JComboBox nonlinearParams4Cmb = null;
  private JWFNumberField nonlinearParams4REd = null;
  private JButton nonlinearParams4LeftButton = null;
  private JScrollPane createPaletteScrollPane = null;
  private JTable createPaletteColorsTable = null;
  private JScrollPane nonlinearScrollPane = null;
  private JPanel nonlinearControlsPanel = null;
  private JPanel nonlinearVar5Panel = null;
  private JLabel nonlinearVar5Lbl = null;
  private JComboBox nonlinearVar5Cmb = null;
  private JWFNumberField nonlinearVar5REd = null;
  private JLabel nonlinearParams5Lbl = null;
  private JComboBox nonlinearParams5Cmb = null;
  private JWFNumberField nonlinearParams5REd = null;
  private JButton nonlinearParams5LeftButton = null;
  private JPanel nonlinearVar6Panel = null;
  private JLabel nonlinearVar6Lbl = null;
  private JComboBox nonlinearVar6Cmb = null;
  private JWFNumberField nonlinearVar6REd = null;
  private JLabel nonlinearParams6Lbl = null;
  private JComboBox nonlinearParams6Cmb = null;
  private JWFNumberField nonlinearParams6REd = null;
  private JButton nonlinearParams6LeftButton = null;
  private JPanel nonlinearVar7Panel = null;
  private JLabel nonlinearVar7Lbl = null;
  private JComboBox nonlinearVar7Cmb = null;
  private JWFNumberField nonlinearVar7REd = null;
  private JLabel nonlinearParams7Lbl = null;
  private JComboBox nonlinearParams7Cmb = null;
  private JWFNumberField nonlinearParams7REd = null;
  private JButton nonlinearParams7LeftButton = null;
  private JPanel nonlinearVar8Panel = null;
  private JLabel nonlinearVar8Lbl = null;
  private JComboBox nonlinearVar8Cmb = null;
  private JWFNumberField nonlinearVar8REd = null;
  private JLabel nonlinearParams8Lbl = null;
  private JComboBox nonlinearParams8Cmb = null;
  private JWFNumberField nonlinearParams8REd = null;
  private JButton nonlinearParams8LeftButton = null;
  private JPanel nonlinearVar9Panel = null;
  private JLabel nonlinearVar9Lbl = null;
  private JComboBox nonlinearVar9Cmb = null;
  private JWFNumberField nonlinearVar9REd = null;
  private JLabel nonlinearParams9Lbl = null;
  private JComboBox nonlinearParams9Cmb = null;
  private JWFNumberField nonlinearParams9REd = null;
  private JButton nonlinearParams9LeftButton = null;
  private JPanel nonlinearVar10Panel = null;
  private JLabel nonlinearVar10Lbl = null;
  private JComboBox nonlinearVar10Cmb = null;
  private JWFNumberField nonlinearVar10REd = null;
  private JLabel nonlinearParams10Lbl = null;
  private JComboBox nonlinearParams10Cmb = null;
  private JWFNumberField nonlinearParams10REd = null;
  private JButton nonlinearParams10LeftButton = null;
  private JPanel nonlinearVar11Panel = null;
  private JLabel nonlinearVar11Lbl = null;
  private JComboBox nonlinearVar11Cmb = null;
  private JWFNumberField nonlinearVar11REd = null;
  private JLabel nonlinearParams11Lbl = null;
  private JComboBox nonlinearParams11Cmb = null;
  private JWFNumberField nonlinearParams11REd = null;
  private JButton nonlinearParams11LeftButton = null;
  private JPanel nonlinearVar12Panel = null;
  private JLabel nonlinearVar12Lbl = null;
  private JComboBox nonlinearVar12Cmb = null;
  private JWFNumberField nonlinearVar12REd = null;
  private JLabel nonlinearParams12Lbl = null;
  private JComboBox nonlinearParams12Cmb = null;
  private JWFNumberField nonlinearParams12REd = null;
  private JButton nonlinearParams12LeftButton = null;
  private JButton tinaGrabPaletteFromFlameButton = null;
  private JLabel tinaCameraZPosLbl = null;
  private JLabel tinaCameraDOFLbl = null;
  private JPanel pseudo3DShadingPanel = null;
  private JLabel shadingDiffuseLbl = null;
  private JWFNumberField shadingAmbientREd = null;
  private JLabel shadingAmbientLbl = null;
  private JWFNumberField shadingDiffuseREd = null;
  private JSlider shadingAmbientSlider = null;
  private JSlider shadingDiffuseSlider = null;
  private JLabel shadingPhongLbl = null;
  private JWFNumberField shadingPhongREd = null;
  private JSlider shadingPhongSlider = null;
  private JLabel shadingPhongSizeLbl = null;
  private JWFNumberField shadingPhongSizeREd = null;
  private JSlider shadingPhongSizeSlider = null;
  private JLabel shadingLightLbl = null;
  private JComboBox shadingLightCmb = null;
  private JLabel shadingLightXLbl = null;
  private JWFNumberField shadingLightXREd = null;
  private JSlider shadingLightXSlider = null;
  private JLabel shadingLightYLbl = null;
  private JWFNumberField shadingLightYREd = null;
  private JSlider shadingLightYSlider = null;
  private JLabel shadingLightZLbl = null;
  private JWFNumberField shadingLightZREd = null;
  private JSlider shadingLightZSlider = null;
  private JLabel shadingLightRedLbl = null;
  private JWFNumberField shadingLightRedREd = null;
  private JSlider shadingLightRedSlider = null;
  private JLabel shadingLightGreenLbl = null;
  private JWFNumberField shadingLightGreenREd = null;
  private JSlider shadingLightGreenSlider = null;
  private JLabel shadingLightBlueLbl = null;
  private JWFNumberField shadingLightBlueREd = null;
  private JSlider shadingLightBlueSlider = null;
  private JPanel interactiveRenderPanel = null;
  private JButton loadFromClipboardFlameButton = null;
  private JButton saveFlameToClipboardButton = null;
  private JToggleButton mouseTransformSlowButton = null;
  private JPanel batchRenderPanel = null;
  private JTabbedPane rootTabbedPane = null;
  private JScrollPane renderBatchJobsScrollPane = null;
  private JTable renderBatchJobsTable = null;
  private JButton batchRenderAddFilesButton = null;
  private JButton batchRenderFilesMoveUpButton = null;
  private JButton batchRenderFilesMoveDownButton = null;
  private JProgressBar batchRenderJobProgressBar = null;
  private JProgressBar batchRenderTotalProgressBar = null;
  private JButton batchRenderFilesRemoveButton = null;
  private JButton batchRenderFilesRemoveAllButton = null;
  private JLabel batchRenderJobProgressLbl = null;
  private JLabel batchRenderTotalProgressLbl = null;
  private JButton batchRenderStartButton = null;
  private JButton affineFlipHorizontalButton = null;
  private JButton affineFlipVerticalButton = null;
  private JPanel shadingPanel = null;
  private JLabel shadingLbl = null;
  private JComboBox shadingCmb = null;
  private JPanel blurShadingPanel = null;
  private JLabel shadingBlurRadiusLbl = null;
  private JWFNumberField shadingBlurRadiusREd = null;
  private JSlider shadingBlurRadiusSlider = null;
  private JLabel shadingBlurFadeLbl = null;
  private JWFNumberField shadingBlurFadeREd = null;
  private JSlider shadingBlurFadeSlider = null;
  private JLabel shadingBlurFallOffLbl = null;
  private JWFNumberField shadingBlurFallOffREd = null;
  private JSlider shadingBlurFallOffSlider = null;
  private JPanel scriptPanel = null;
  private JScrollPane scriptScrollPane = null;
  private JTextArea scriptTextArea = null;
  private JButton compileScriptButton = null;
  private JToggleButton affineScaleXButton = null;
  private JToggleButton affineScaleYButton = null;
  private JButton randomizeColorsButton = null;
  private JLabel tinaPaletteSwapRGBLbl = null;
  private JPanel gradientLibraryPanel = null;
  private JPanel gradientLibraryCenterPanel = null;

  public TinaInternalFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1188, 740);
    this.setFont(new Font("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(0, 0));
    this.setClosable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setIconifiable(true);
    this.setTitle("Fractal flames");
    this.setVisible(true);
    this.setResizable(true);
    this.setMaximizable(true);
    this.setContentPane(getJContentPane());
    getGradientEditorFncPnl().setVisible(false);
    //    this.getInputMap().put(KeyStroke.getKeyStroke('z', InputEvent.CTRL_DOWN_MASK), new AbstractAction() {
    //      private static final long serialVersionUID = 1L;
    //
    //      public void actionPerformed(ActionEvent e) {
    //        System.out.println("ZZZZZZZZZZZZZZZZZZ");
    //      }
    //    }
    //        );
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.setFont(new Font("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      jContentPane.add(getRootTabbedPane(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRootPanel() {
    if (rootPanel == null) {
      rootPanel = new JPanel();
      rootPanel.setLayout(new BorderLayout());
      rootPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      rootPanel.add(getTinaNorthPanel(), BorderLayout.NORTH);
      rootPanel.add(getRandomBatchPanel(), BorderLayout.WEST);
      rootPanel.add(getTinaEastPanel(), BorderLayout.EAST);
      rootPanel.add(getTinaSouthPanel(), BorderLayout.SOUTH);
      rootPanel.add(getTinaCenterPanel(), BorderLayout.CENTER);
    }
    return rootPanel;
  }

  /**
   * This method initializes tinaNorthPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaNorthPanel() {
    if (tinaNorthPanel == null) {
      tinaNorthPanel = new JPanel();
      tinaNorthPanel.setPreferredSize(new Dimension(0, 66));
      tinaNorthPanel.setLayout(new BoxLayout(tinaNorthPanel, BoxLayout.X_AXIS));
      tinaNorthPanel.add(getRandomBatchButton());

      JButton randomBatchHighQualityButton = new JButton();
      randomBatchHighQualityButton.setToolTipText("Create a random batch in higher quality (may be much slower)");
      randomBatchHighQualityButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController.createRandomBatch(-1, (String) randomStyleCmb.getSelectedItem(), (String) randomSymmetryCmb.getSelectedItem(), (String) randomGradientCmb.getSelectedItem(), RandomBatchQuality.HIGH)) {
            tinaController.importFromRandomBatch(0);
          }
        }
      });
      randomBatchHighQualityButton.setText("H");
      randomBatchHighQualityButton.setPreferredSize(new Dimension(22, 46));
      randomBatchHighQualityButton.setMnemonic(KeyEvent.VK_D);
      randomBatchHighQualityButton.setMinimumSize(new Dimension(22, 46));
      randomBatchHighQualityButton.setMaximumSize(new Dimension(32000, 46));
      randomBatchHighQualityButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaNorthPanel.add(randomBatchHighQualityButton);
      tinaNorthPanel.add(getPanel_7());
      tinaNorthPanel.add(getNewFlameButton());
      tinaNorthPanel.add(getPanel_6());
      tinaNorthPanel.add(getPanel_13());
      tinaNorthPanel.add(getRenderMainButton());
      tinaNorthPanel.add(getPanel_15());

    }
    return tinaNorthPanel;
  }

  /**
   * This method initializes tinaWestPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getRandomBatchPanel() {
    if (randomBatchPanel == null) {
      randomBatchPanel = new JPanel();
      randomBatchPanel.setLayout(new BorderLayout());
      randomBatchPanel.setPreferredSize(new Dimension(128, 0));
      randomBatchPanel.setVisible(true);
    }
    return randomBatchPanel;
  }

  /**
   * This method initializes tinaEastPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaEastPanel() {
    if (tinaEastPanel == null) {
      GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
      gridBagConstraints4.gridx = 1;
      gridBagConstraints4.gridheight = 2;
      gridBagConstraints4.gridy = 0;
      tinaEastPanel = new JPanel();
      tinaEastPanel.setLayout(new BorderLayout());
      tinaEastPanel.setPreferredSize(new Dimension(328, 0));
      tinaEastPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaEastPanel.add(getTinaEastTabbedPane(), BorderLayout.CENTER);
    }
    return tinaEastPanel;
  }

  /**
   * This method initializes tinaSouthPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaSouthPanel() {
    if (tinaSouthPanel == null) {
      tinaSouthPanel = new JPanel();
      tinaSouthPanel.setLayout(new BorderLayout());
      tinaSouthPanel.setPreferredSize(new Dimension(0, 202));
      tinaSouthPanel.add(getTinaSouthTabbedPane(), BorderLayout.CENTER);
      tinaSouthPanel.add(getFrameSliderPanel(), BorderLayout.NORTH);
    }
    return tinaSouthPanel;
  }

  /**
   * This method initializes tinaCenterPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaCenterPanel() {
    if (tinaCenterPanel == null) {
      tinaCenterPanel = new JPanel();
      tinaCenterPanel.setLayout(new BorderLayout());
      tinaCenterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      tinaCenterPanel.setBackground(SystemColor.controlDkShadow);
      tinaCenterPanel.add(getTriangleOperationsPanel(), BorderLayout.EAST);
      tinaCenterPanel.add(getCenterNorthPanel(), BorderLayout.NORTH);
      tinaCenterPanel.add(getCenterWestPanel(), BorderLayout.WEST);
      tinaCenterPanel.add(getCenterCenterPanel(), BorderLayout.CENTER);
    }
    return tinaCenterPanel;
  }

  /**
   * This method initializes tinaSouthTabbedPane  
   *  
   * @return javax.swing.JTabbedPane  
   */
  private JTabbedPane getTinaSouthTabbedPane() {
    if (tinaSouthTabbedPane == null) {
      tinaSouthTabbedPane = new JTabbedPane();
      tinaSouthTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaSouthTabbedPane.addTab("Camera ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/modify_view.png")), getTinaCameraPanel(), null);

      tinaDOFPanel = new JPanel();
      tinaDOFPanel.setLayout(null);

      dofDOFSlider = new JSlider();
      dofDOFSlider.setName("dofDOFSlider");
      dofDOFSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().cameraDOFSlider_stateChanged(e);
          }
        }
      });
      dofDOFSlider.setValue(0);
      dofDOFSlider.setSize(new Dimension(220, 19));
      dofDOFSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFSlider.setMinimum(0);
      dofDOFSlider.setMaximum(100);
      dofDOFSlider.setLocation(new Point(202, 98));
      dofDOFSlider.setBounds(204, 28, 220, 24);
      tinaDOFPanel.add(dofDOFSlider);

      dofDOFREd = new JWFNumberField();
      dofDOFREd.setLinkedLabelControlName("lblDepthOfField");
      dofDOFREd.setMotionPropertyName("camDOF");
      dofDOFREd.setLinkedMotionControlName("dofDOFSlider");
      dofDOFREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFREd.isMouseAdjusting() || dofDOFREd.getMouseChangeCount() == 0) {
              if (!dofDOFSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().cameraDOFREd_changed();
          }
        }
      });
      dofDOFREd.setValueStep(0.01);
      dofDOFREd.setText("");
      dofDOFREd.setSize(new Dimension(100, 24));
      dofDOFREd.setPreferredSize(new Dimension(100, 24));
      dofDOFREd.setLocation(new Point(100, 98));
      dofDOFREd.setHasMinValue(true);
      dofDOFREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFREd.setBounds(102, 28, 100, 24);
      tinaDOFPanel.add(dofDOFREd);

      JLabel lblDepthOfField = new JLabel();
      lblDepthOfField.setName("lblDepthOfField");
      lblDepthOfField.setText("Amount");
      lblDepthOfField.setSize(new Dimension(94, 22));
      lblDepthOfField.setPreferredSize(new Dimension(94, 22));
      lblDepthOfField.setLocation(new Point(4, 98));
      lblDepthOfField.setFont(new Font("Dialog", Font.BOLD, 10));
      lblDepthOfField.setBounds(6, 28, 94, 22);
      tinaDOFPanel.add(lblDepthOfField);

      dofNewDOFCBx = new JCheckBox("New DOF");
      dofNewDOFCBx.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.newDOFCBx_changed();
          }
        }
      });
      dofNewDOFCBx.setBounds(102, 6, 104, 18);
      tinaDOFPanel.add(dofNewDOFCBx);

      JLabel lblArea = new JLabel();
      lblArea.setName("lblArea");
      lblArea.setText("Area");
      lblArea.setSize(new Dimension(94, 22));
      lblArea.setPreferredSize(new Dimension(94, 22));
      lblArea.setLocation(new Point(4, 98));
      lblArea.setFont(new Font("Dialog", Font.BOLD, 10));
      lblArea.setBounds(6, 52, 94, 22);
      tinaDOFPanel.add(lblArea);

      dofDOFAreaSlider = new JSlider();
      dofDOFAreaSlider.setName("dofDOFAreaSlider");
      dofDOFAreaSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().cameraDOFAreaSlider_stateChanged(e);
          }
        }
      });
      dofDOFAreaSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFAreaSlider.setValue(0);
      dofDOFAreaSlider.setSize(new Dimension(220, 19));
      dofDOFAreaSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFAreaSlider.setMinimum(0);
      dofDOFAreaSlider.setMaximum(200);
      dofDOFAreaSlider.setLocation(new Point(202, 98));
      dofDOFAreaSlider.setBounds(204, 52, 220, 24);
      tinaDOFPanel.add(dofDOFAreaSlider);

      dofDOFAreaREd = new JWFNumberField();
      dofDOFAreaREd.setLinkedLabelControlName("lblArea");
      dofDOFAreaREd.setMotionPropertyName("camDOFArea");
      dofDOFAreaREd.setLinkedMotionControlName("dofDOFAreaSlider");
      dofDOFAreaREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFAreaREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFAreaREd.isMouseAdjusting() || dofDOFAreaREd.getMouseChangeCount() == 0) {
              if (!dofDOFAreaSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().cameraDOFAreaREd_changed();
          }
        }
      });
      dofDOFAreaREd.setValueStep(0.01);
      dofDOFAreaREd.setText("");
      dofDOFAreaREd.setSize(new Dimension(100, 24));
      dofDOFAreaREd.setPreferredSize(new Dimension(100, 24));
      dofDOFAreaREd.setLocation(new Point(100, 98));
      dofDOFAreaREd.setHasMinValue(true);
      dofDOFAreaREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFAreaREd.setBounds(102, 52, 100, 24);
      tinaDOFPanel.add(dofDOFAreaREd);

      JLabel lblExponent = new JLabel();
      lblExponent.setName("lblExponent");
      lblExponent.setText("Exponent");
      lblExponent.setSize(new Dimension(94, 22));
      lblExponent.setPreferredSize(new Dimension(94, 22));
      lblExponent.setLocation(new Point(4, 98));
      lblExponent.setFont(new Font("Dialog", Font.BOLD, 10));
      lblExponent.setBounds(6, 76, 94, 22);
      tinaDOFPanel.add(lblExponent);

      dofDOFExponentSlider = new JSlider();
      dofDOFExponentSlider.setName("dofDOFExponentSlider");
      dofDOFExponentSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().cameraDOFExponentSlider_stateChanged(e);
          }
        }
      });
      dofDOFExponentSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFExponentSlider.setValue(0);
      dofDOFExponentSlider.setSize(new Dimension(220, 19));
      dofDOFExponentSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFExponentSlider.setMinimum(10);
      dofDOFExponentSlider.setMaximum(400);
      dofDOFExponentSlider.setLocation(new Point(202, 98));
      dofDOFExponentSlider.setBounds(204, 76, 220, 24);
      tinaDOFPanel.add(dofDOFExponentSlider);

      dofDOFExponentREd = new JWFNumberField();
      dofDOFExponentREd.setLinkedLabelControlName("lblExponent");
      dofDOFExponentREd.setMotionPropertyName("camDOFExponent");
      dofDOFExponentREd.setLinkedMotionControlName("dofDOFExponentSlider");
      dofDOFExponentREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFExponentREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFExponentREd.isMouseAdjusting() || dofDOFExponentREd.getMouseChangeCount() == 0) {
              if (!dofDOFExponentSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().cameraDOFExponentREd_changed();
          }
        }
      });
      dofDOFExponentREd.setValueStep(0.01);
      dofDOFExponentREd.setText("");
      dofDOFExponentREd.setSize(new Dimension(100, 24));
      dofDOFExponentREd.setPreferredSize(new Dimension(100, 24));
      dofDOFExponentREd.setLocation(new Point(100, 98));
      dofDOFExponentREd.setHasMinValue(true);
      dofDOFExponentREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFExponentREd.setBounds(102, 76, 100, 24);
      tinaDOFPanel.add(dofDOFExponentREd);

      JLabel lblCameraDistance = new JLabel();
      lblCameraDistance.setName("lblCameraDistance");
      lblCameraDistance.setText("Camera distance");
      lblCameraDistance.setSize(new Dimension(94, 22));
      lblCameraDistance.setPreferredSize(new Dimension(94, 22));
      lblCameraDistance.setLocation(new Point(4, 98));
      lblCameraDistance.setFont(new Font("Dialog", Font.BOLD, 10));
      lblCameraDistance.setBounds(447, 4, 94, 22);
      tinaDOFPanel.add(lblCameraDistance);

      dofCamZSlider = new JSlider();
      dofCamZSlider.setName("dofCamZSlider");
      dofCamZSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().camZSlider_stateChanged(e);
          }
        }
      });
      dofCamZSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofCamZSlider.setValue(0);
      dofCamZSlider.setSize(new Dimension(220, 19));
      dofCamZSlider.setPreferredSize(new Dimension(220, 19));
      dofCamZSlider.setMinimum(-100);
      dofCamZSlider.setMaximum(100);
      dofCamZSlider.setLocation(new Point(202, 98));
      dofCamZSlider.setBounds(645, 4, 220, 24);
      tinaDOFPanel.add(dofCamZSlider);

      dofCamZREd = new JWFNumberField();
      dofCamZREd.setLinkedLabelControlName("lblCameraDistance");
      dofCamZREd.setMotionPropertyName("camZ");
      dofCamZREd.setLinkedMotionControlName("dofCamZSlider");
      dofCamZREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofCamZREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofCamZREd.isMouseAdjusting() || dofCamZREd.getMouseChangeCount() == 0) {
              if (!dofCamZSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().camZREd_changed();
          }
        }
      });
      dofCamZREd.setValueStep(0.01);
      dofCamZREd.setToolTipText("Affects both the old DOF-effect and diminish in z-direction");
      dofCamZREd.setSize(new Dimension(100, 24));
      dofCamZREd.setPreferredSize(new Dimension(100, 24));
      dofCamZREd.setLocation(new Point(100, 98));
      dofCamZREd.setHasMinValue(true);
      dofCamZREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofCamZREd.setBounds(543, 4, 100, 24);
      tinaDOFPanel.add(dofCamZREd);

      JLabel lblFocusx = new JLabel();
      lblFocusx.setName("lblFocusx");
      lblFocusx.setText("FocusX");
      lblFocusx.setSize(new Dimension(94, 22));
      lblFocusx.setPreferredSize(new Dimension(94, 22));
      lblFocusx.setLocation(new Point(4, 98));
      lblFocusx.setFont(new Font("Dialog", Font.BOLD, 10));
      lblFocusx.setBounds(447, 52, 94, 22);
      tinaDOFPanel.add(lblFocusx);

      dofFocusXREd = new JWFNumberField();
      dofFocusXREd.setLinkedLabelControlName("lblFocusx");
      dofFocusXREd.setMotionPropertyName("focusX");
      dofFocusXREd.setLinkedMotionControlName("dofFocusXSlider");
      dofFocusXREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofFocusXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofFocusXREd.isMouseAdjusting() || dofFocusXREd.getMouseChangeCount() == 0) {
              if (!dofFocusXSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().focusXREd_changed();
          }
        }
      });
      dofFocusXREd.setValueStep(0.01);
      dofFocusXREd.setText("");
      dofFocusXREd.setSize(new Dimension(100, 24));
      dofFocusXREd.setPreferredSize(new Dimension(100, 24));
      dofFocusXREd.setLocation(new Point(100, 98));
      dofFocusXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofFocusXREd.setBounds(543, 52, 100, 24);
      tinaDOFPanel.add(dofFocusXREd);

      dofFocusXSlider = new JSlider();
      dofFocusXSlider.setName("dofFocusXSlider");
      dofFocusXSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().focusXSlider_stateChanged(e);
          }
        }
      });
      dofFocusXSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofFocusXSlider.setValue(0);
      dofFocusXSlider.setSize(new Dimension(220, 19));
      dofFocusXSlider.setPreferredSize(new Dimension(220, 19));
      dofFocusXSlider.setMinimum(-100);
      dofFocusXSlider.setMaximum(100);
      dofFocusXSlider.setLocation(new Point(202, 98));
      dofFocusXSlider.setBounds(645, 52, 220, 24);
      tinaDOFPanel.add(dofFocusXSlider);

      JLabel lblFocusy = new JLabel();
      lblFocusy.setName("lblFocusy");
      lblFocusy.setText("FocusY");
      lblFocusy.setSize(new Dimension(94, 22));
      lblFocusy.setPreferredSize(new Dimension(94, 22));
      lblFocusy.setLocation(new Point(4, 98));
      lblFocusy.setFont(new Font("Dialog", Font.BOLD, 10));
      lblFocusy.setBounds(447, 76, 94, 22);
      tinaDOFPanel.add(lblFocusy);

      dofFocusYREd = new JWFNumberField();
      dofFocusYREd.setLinkedLabelControlName("lblFocusy");
      dofFocusYREd.setMotionPropertyName("focusY");
      dofFocusYREd.setLinkedMotionControlName("dofFocusYSlider");
      dofFocusYREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofFocusYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofFocusYREd.isMouseAdjusting() || dofFocusYREd.getMouseChangeCount() == 0) {
              if (!dofFocusYSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().focusYREd_changed();
          }
        }
      });
      dofFocusYREd.setValueStep(0.01);
      dofFocusYREd.setText("");
      dofFocusYREd.setSize(new Dimension(100, 24));
      dofFocusYREd.setPreferredSize(new Dimension(100, 24));
      dofFocusYREd.setLocation(new Point(100, 98));
      dofFocusYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofFocusYREd.setBounds(543, 76, 100, 24);
      tinaDOFPanel.add(dofFocusYREd);

      dofFocusYSlider = new JSlider();
      dofFocusYSlider.setName("dofFocusYSlider");
      dofFocusYSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().focusYSlider_stateChanged(e);
          }
        }
      });
      dofFocusYSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofFocusYSlider.setValue(0);
      dofFocusYSlider.setSize(new Dimension(220, 19));
      dofFocusYSlider.setPreferredSize(new Dimension(220, 19));
      dofFocusYSlider.setMinimum(-100);
      dofFocusYSlider.setMaximum(100);
      dofFocusYSlider.setLocation(new Point(202, 98));
      dofFocusYSlider.setBounds(645, 76, 220, 24);
      tinaDOFPanel.add(dofFocusYSlider);

      JLabel lblFocusz = new JLabel();
      lblFocusz.setName("lblFocusz");
      lblFocusz.setText("FocusZ");
      lblFocusz.setSize(new Dimension(94, 22));
      lblFocusz.setPreferredSize(new Dimension(94, 22));
      lblFocusz.setLocation(new Point(4, 98));
      lblFocusz.setFont(new Font("Dialog", Font.BOLD, 10));
      lblFocusz.setBounds(447, 100, 94, 22);
      tinaDOFPanel.add(lblFocusz);

      dofFocusZSlider = new JSlider();
      dofFocusZSlider.setName("dofFocusZSlider");
      dofFocusZSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().focusZSlider_stateChanged(e);
          }
        }
      });
      dofFocusZSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofFocusZSlider.setValue(0);
      dofFocusZSlider.setSize(new Dimension(220, 19));
      dofFocusZSlider.setPreferredSize(new Dimension(220, 19));
      dofFocusZSlider.setMinimum(-100);
      dofFocusZSlider.setMaximum(100);
      dofFocusZSlider.setLocation(new Point(202, 98));
      dofFocusZSlider.setBounds(645, 100, 220, 24);
      tinaDOFPanel.add(dofFocusZSlider);

      dofFocusZREd = new JWFNumberField();
      dofFocusZREd.setLinkedLabelControlName("lblFocusz");
      dofFocusZREd.setMotionPropertyName("focusZ");
      dofFocusZREd.setLinkedMotionControlName("dofFocusZSlider");
      dofFocusZREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofFocusZREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofFocusZREd.isMouseAdjusting() || dofFocusZREd.getMouseChangeCount() == 0) {
              if (!dofFocusZSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().focusZREd_changed();
          }
        }
      });
      dofFocusZREd.setValueStep(0.01);
      dofFocusZREd.setText("");
      dofFocusZREd.setSize(new Dimension(100, 24));
      dofFocusZREd.setPreferredSize(new Dimension(100, 24));
      dofFocusZREd.setLocation(new Point(100, 98));
      dofFocusZREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofFocusZREd.setBounds(543, 100, 100, 24);
      tinaDOFPanel.add(dofFocusZREd);

      JLabel lblDimishz = new JLabel();
      lblDimishz.setName("lblDimishz");
      lblDimishz.setText("DiminishZ");
      lblDimishz.setSize(new Dimension(94, 22));
      lblDimishz.setPreferredSize(new Dimension(94, 22));
      lblDimishz.setLocation(new Point(4, 98));
      lblDimishz.setFont(new Font("Dialog", Font.BOLD, 10));
      lblDimishz.setBounds(447, 28, 94, 22);
      tinaDOFPanel.add(lblDimishz);

      camDimishZREd = new JWFNumberField();
      camDimishZREd.setLinkedLabelControlName("lblDimishz");
      camDimishZREd.setMotionPropertyName("dimishZ");
      camDimishZREd.setLinkedMotionControlName("camDimishZSlider");
      camDimishZREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      camDimishZREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!camDimishZREd.isMouseAdjusting() || camDimishZREd.getMouseChangeCount() == 0) {
              if (!camDimishZSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().diminishZREd_changed();
          }
        }
      });
      camDimishZREd.setToolTipText("Diminish brightness in z-direction to improve \"depth\"-effect, also affected by camera distance");
      camDimishZREd.setValueStep(0.01);
      camDimishZREd.setText("");
      camDimishZREd.setSize(new Dimension(100, 24));
      camDimishZREd.setPreferredSize(new Dimension(100, 24));
      camDimishZREd.setLocation(new Point(100, 98));
      camDimishZREd.setHasMinValue(true);
      camDimishZREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      camDimishZREd.setBounds(543, 28, 100, 24);
      tinaDOFPanel.add(camDimishZREd);

      camDimishZSlider = new JSlider();
      camDimishZSlider.setName("camDimishZSlider");
      camDimishZSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().diminishZSlider_stateChanged(e);
          }
        }
      });
      camDimishZSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      camDimishZSlider.setValue(0);
      camDimishZSlider.setSize(new Dimension(220, 19));
      camDimishZSlider.setPreferredSize(new Dimension(220, 19));
      camDimishZSlider.setMaximum(75);
      camDimishZSlider.setLocation(new Point(202, 98));
      camDimishZSlider.setBounds(645, 28, 220, 24);
      tinaDOFPanel.add(camDimishZSlider);
      tinaSouthTabbedPane.addTab("DOF / Bokeh ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/games-config-background.png")), getPanel_92(), null);

      tinaSouthTabbedPane.addTab("Coloring ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/color-wheel.png")), getTinaColoringPanel(), null);
      tinaSouthTabbedPane.addTab("Anti-Aliasing / Filter", null, getAntialiasPanel(), null);

      tinaSouthTabbedPane.addTab("Special Shading", null, getShadingPanel(), null);
      tinaSouthTabbedPane.addTab("Gradient ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-graphics-2.png")), getTinaPalettePanel(), null);
      tinaSouthTabbedPane.addTab("Stereo3d rendering ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/layer-novisible.png")), getPanel_82(), null);
      tinaSouthTabbedPane.addTab("Post symmetry", null, getPanel_34(), null);
      tinaSouthTabbedPane.addTab("Motion blur", null, getMotionBlurPanel(), null);
      tinaSouthTabbedPane.addTab("Layerz ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/emblem-photos.png")), getPanel_74(), null);
      tinaSouthTabbedPane.addTab("Channel mixer ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/color-fill.png")), getChannelMixerPanel(), null);

      tinaSouthTabbedPane.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            switch (tinaSouthTabbedPane.getSelectedIndex()) {
              case 5:
                if (tinaController.getGradientController() != null) {
                  tinaController.getGradientController().onActivate();
                }
                break;
            }
          }
        }
      });
    }
    return tinaSouthTabbedPane;
  }

  /**
   * This method initializes tinaCameraPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaCameraPanel() {
    if (tinaCameraPanel == null) {
      tinaPixelsPerUnitLbl = new JLabel();
      tinaPixelsPerUnitLbl.setName("tinaPixelsPerUnitLbl");
      tinaPixelsPerUnitLbl.setText("Pixs per unit");
      tinaPixelsPerUnitLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPixelsPerUnitLbl.setLocation(new Point(390, 78));
      tinaPixelsPerUnitLbl.setSize(new Dimension(68, 22));
      tinaPixelsPerUnitLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraZoomLbl = new JLabel();
      tinaCameraZoomLbl.setName("tinaCameraZoomLbl");
      tinaCameraZoomLbl.setText("Zoom");
      tinaCameraZoomLbl.setLocation(new Point(390, 52));
      tinaCameraZoomLbl.setSize(new Dimension(68, 22));
      tinaCameraZoomLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraZoomLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCentreYLbl = new JLabel();
      tinaCameraCentreYLbl.setName("tinaCameraCentreYLbl");
      tinaCameraCentreYLbl.setText("CentreY");
      tinaCameraCentreYLbl.setLocation(new Point(390, 28));
      tinaCameraCentreYLbl.setSize(new Dimension(68, 22));
      tinaCameraCentreYLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraCentreYLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCentreXLbl = new JLabel();
      tinaCameraCentreXLbl.setName("tinaCameraCentreXLbl");
      tinaCameraCentreXLbl.setText("CentreX");
      tinaCameraCentreXLbl.setLocation(new Point(390, 6));
      tinaCameraCentreXLbl.setSize(new Dimension(68, 22));
      tinaCameraCentreXLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraCentreXLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraPerspectiveLbl = new JLabel();
      tinaCameraPerspectiveLbl.setName("tinaCameraPerspectiveLbl");
      tinaCameraPerspectiveLbl.setText("Perspective");
      tinaCameraPerspectiveLbl.setLocation(new Point(4, 76));
      tinaCameraPerspectiveLbl.setSize(new Dimension(68, 22));
      tinaCameraPerspectiveLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraPerspectiveLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraYawLbl = new JLabel();
      tinaCameraYawLbl.setName("tinaCameraYawLbl");
      tinaCameraYawLbl.setText("Yaw");
      tinaCameraYawLbl.setLocation(new Point(4, 52));
      tinaCameraYawLbl.setSize(new Dimension(68, 22));
      tinaCameraYawLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraYawLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraPitchLbl = new JLabel();
      tinaCameraPitchLbl.setName("tinaCameraPitchLbl");
      tinaCameraPitchLbl.setText("Pitch");
      tinaCameraPitchLbl.setLocation(new Point(4, 28));
      tinaCameraPitchLbl.setSize(new Dimension(68, 22));
      tinaCameraPitchLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraPitchLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraRollLbl = new JLabel();
      tinaCameraRollLbl.setName("tinaCameraRollLbl");
      tinaCameraRollLbl.setText("Roll");
      tinaCameraRollLbl.setLocation(new Point(4, 4));
      tinaCameraRollLbl.setSize(new Dimension(68, 22));
      tinaCameraRollLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraRollLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraPanel = new JPanel();
      tinaCameraPanel.setLayout(null);
      tinaCameraPanel.add(tinaCameraRollLbl, null);
      tinaCameraPanel.add(getTinaCameraRollREd(), null);
      tinaCameraPanel.add(tinaCameraPitchLbl, null);
      tinaCameraPanel.add(getTinaCameraPitchREd(), null);
      tinaCameraPanel.add(tinaCameraYawLbl, null);
      tinaCameraPanel.add(getTinaCameraYawREd(), null);
      tinaCameraPanel.add(tinaCameraPerspectiveLbl, null);
      tinaCameraPanel.add(getTinaCameraPerspectiveREd(), null);
      tinaCameraPanel.add(getTinaCameraRollSlider(), null);
      tinaCameraPanel.add(getTinaCameraPitchSlider(), null);
      tinaCameraPanel.add(getTinaCameraYawSlider(), null);
      tinaCameraPanel.add(getTinaCameraPerspectiveSlider(), null);
      tinaCameraPanel.add(tinaCameraCentreXLbl, null);
      tinaCameraPanel.add(getTinaCameraCentreXREd(), null);
      tinaCameraPanel.add(tinaCameraCentreYLbl, null);
      tinaCameraPanel.add(getTinaCameraCentreYREd(), null);
      tinaCameraPanel.add(getTinaCameraCentreXSlider(), null);
      tinaCameraPanel.add(getTinaCameraCentreYSlider(), null);
      tinaCameraPanel.add(tinaCameraZoomLbl, null);
      tinaCameraPanel.add(getTinaCameraZoomREd(), null);
      tinaCameraPanel.add(getTinaCameraZoomSlider(), null);
      tinaCameraPanel.add(tinaPixelsPerUnitLbl, null);
      tinaCameraPanel.add(getTinaPixelsPerUnitREd(), null);
      tinaCameraPanel.add(getTinaPixelsPerUnitSlider(), null);

      tinaCameraCamPosXREd = new JWFNumberField();
      tinaCameraCamPosXREd.setValueStep(0.05);
      tinaCameraCamPosXREd.setText("");
      tinaCameraCamPosXREd.setSize(new Dimension(100, 24));
      tinaCameraCamPosXREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraCamPosXREd.setMotionPropertyName("camPosX");
      tinaCameraCamPosXREd.setLocation(new Point(456, 4));
      tinaCameraCamPosXREd.setLinkedMotionControlName("tinaCameraCamPosXSlider");
      tinaCameraCamPosXREd.setLinkedLabelControlName("tinaCameraCamPosXLbl");
      tinaCameraCamPosXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraCamPosXREd.setBounds(840, 4, 100, 24);
      tinaCameraCamPosXREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraCamPosXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraCamPosXREd.isMouseAdjusting() || tinaCameraCamPosXREd.getMouseChangeCount() == 0) {
            if (!tinaCameraCamPosXSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().camPosXREd_changed();
        }
      });
      tinaCameraPanel.add(tinaCameraCamPosXREd);

      JLabel tinaCameraCamPosXLbl = new JLabel();
      tinaCameraCamPosXLbl.setToolTipText("");
      tinaCameraCamPosXLbl.setText("CamPosX");
      tinaCameraCamPosXLbl.setSize(new Dimension(68, 22));
      tinaCameraCamPosXLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCamPosXLbl.setName("tinaCameraCamPosXLbl");
      tinaCameraCamPosXLbl.setLocation(new Point(390, 6));
      tinaCameraCamPosXLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraCamPosXLbl.setBounds(774, 4, 68, 22);
      tinaCameraPanel.add(tinaCameraCamPosXLbl);

      tinaCameraCamPosXSlider = new JSlider();
      tinaCameraCamPosXSlider.setValue(0);
      tinaCameraCamPosXSlider.setSize(new Dimension(205, 19));
      tinaCameraCamPosXSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCamPosXSlider.setName("tinaCameraCamPosXSlider");
      tinaCameraCamPosXSlider.setMinimum(-25000);
      tinaCameraCamPosXSlider.setMaximum(25000);
      tinaCameraCamPosXSlider.setLocation(new Point(558, 4));
      tinaCameraCamPosXSlider.setBounds(942, 4, 205, 19);
      tinaCameraCamPosXSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraCamPosXSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().camPosXSlider_stateChanged(e);
        }
      });

      tinaCameraPanel.add(tinaCameraCamPosXSlider);

      JLabel tinaCameraCamPosYLbl = new JLabel();
      tinaCameraCamPosYLbl.setText("CamPosY");
      tinaCameraCamPosYLbl.setSize(new Dimension(68, 22));
      tinaCameraCamPosYLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCamPosYLbl.setName("tinaCameraCamPosYLbl");
      tinaCameraCamPosYLbl.setLocation(new Point(390, 6));
      tinaCameraCamPosYLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraCamPosYLbl.setBounds(774, 28, 68, 22);
      tinaCameraPanel.add(tinaCameraCamPosYLbl);

      tinaCameraCamPosYREd = new JWFNumberField();
      tinaCameraCamPosYREd.setValueStep(0.05);
      tinaCameraCamPosYREd.setText("");
      tinaCameraCamPosYREd.setSize(new Dimension(100, 24));
      tinaCameraCamPosYREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraCamPosYREd.setMotionPropertyName("camPosY");
      tinaCameraCamPosYREd.setLocation(new Point(456, 4));
      tinaCameraCamPosYREd.setLinkedMotionControlName("tinaCameraCamPosYSlider");
      tinaCameraCamPosYREd.setLinkedLabelControlName("tinaCameraCamPosYLbl");
      tinaCameraCamPosYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraCamPosYREd.setBounds(840, 28, 100, 24);
      tinaCameraCamPosYREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraCamPosYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraCamPosYREd.isMouseAdjusting() || tinaCameraCamPosYREd.getMouseChangeCount() == 0) {
            if (!tinaCameraCamPosYSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().camPosYREd_changed();
        }
      });
      tinaCameraPanel.add(tinaCameraCamPosYREd);

      tinaCameraCamPosYSlider = new JSlider();
      tinaCameraCamPosYSlider.setValue(0);
      tinaCameraCamPosYSlider.setSize(new Dimension(205, 19));
      tinaCameraCamPosYSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCamPosYSlider.setName("tinaCameraCamPosYSlider");
      tinaCameraCamPosYSlider.setMinimum(-25000);
      tinaCameraCamPosYSlider.setMaximum(25000);
      tinaCameraCamPosYSlider.setLocation(new Point(558, 4));
      tinaCameraCamPosYSlider.setBounds(942, 28, 205, 19);
      tinaCameraCamPosYSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraCamPosYSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().camPosYSlider_stateChanged(e);
        }
      });
      tinaCameraPanel.add(tinaCameraCamPosYSlider);

      JLabel tinaCameraCamPosZLbl = new JLabel();
      tinaCameraCamPosZLbl.setText("CamPosZ");
      tinaCameraCamPosZLbl.setSize(new Dimension(68, 22));
      tinaCameraCamPosZLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCamPosZLbl.setName("tinaCameraCamPosZLbl");
      tinaCameraCamPosZLbl.setLocation(new Point(390, 6));
      tinaCameraCamPosZLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraCamPosZLbl.setBounds(775, 52, 68, 22);
      tinaCameraPanel.add(tinaCameraCamPosZLbl);

      tinaCameraCamPosZREd = new JWFNumberField();
      tinaCameraCamPosZREd.setValueStep(0.05);
      tinaCameraCamPosZREd.setText("");
      tinaCameraCamPosZREd.setSize(new Dimension(100, 24));
      tinaCameraCamPosZREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraCamPosZREd.setMotionPropertyName("camPosZ");
      tinaCameraCamPosZREd.setLocation(new Point(456, 4));
      tinaCameraCamPosZREd.setLinkedMotionControlName("tinaCameraCamPosZSlider");
      tinaCameraCamPosZREd.setLinkedLabelControlName("tinaCameraCamPosZLbl");
      tinaCameraCamPosZREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraCamPosZREd.setBounds(841, 52, 100, 24);
      tinaCameraCamPosZREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraCamPosZREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraCamPosZREd.isMouseAdjusting() || tinaCameraCamPosZREd.getMouseChangeCount() == 0) {
            if (!tinaCameraCamPosZSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().camPosZREd_changed();
        }
      });
      tinaCameraPanel.add(tinaCameraCamPosZREd);

      tinaCameraCamPosZSlider = new JSlider();
      tinaCameraCamPosZSlider.setValue(0);
      tinaCameraCamPosZSlider.setSize(new Dimension(205, 19));
      tinaCameraCamPosZSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCamPosZSlider.setName("tinaCameraCamPosZSlider");
      tinaCameraCamPosZSlider.setMinimum(-25000);
      tinaCameraCamPosZSlider.setMaximum(25000);
      tinaCameraCamPosZSlider.setLocation(new Point(558, 4));
      tinaCameraCamPosZSlider.setBounds(943, 52, 205, 19);
      tinaCameraCamPosZSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraCamPosZSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().camPosZSlider_stateChanged(e);
        }
      });
      tinaCameraPanel.add(tinaCameraCamPosZSlider);
    }
    return tinaCameraPanel;
  }

  /**
   * This method initializes tinaColoringPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaColoringPanel() {
    if (tinaColoringPanel == null) {
      tinaGammaThresholdLbl = new JLabel();
      tinaGammaThresholdLbl.setName("tinaGammaThresholdLbl");
      tinaGammaThresholdLbl.setText("Gamma threshold");
      tinaGammaThresholdLbl.setLocation(new Point(4, 76));
      tinaGammaThresholdLbl.setSize(new Dimension(94, 22));
      tinaGammaThresholdLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGammaThresholdLbl.setPreferredSize(new Dimension(94, 22));
      tinaVibrancyLbl = new JLabel();
      tinaVibrancyLbl.setName("tinaVibrancyLbl");
      tinaVibrancyLbl.setText("Vibrancy");
      tinaVibrancyLbl.setLocation(new Point(4, 100));
      tinaVibrancyLbl.setSize(new Dimension(94, 22));
      tinaVibrancyLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaVibrancyLbl.setPreferredSize(new Dimension(94, 22));
      tinaGammaLbl = new JLabel();
      tinaGammaLbl.setName("tinaGammaLbl");
      tinaGammaLbl.setText("Gamma");
      tinaGammaLbl.setLocation(new Point(4, 52));
      tinaGammaLbl.setSize(new Dimension(94, 22));
      tinaGammaLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGammaLbl.setPreferredSize(new Dimension(94, 22));
      tinaContrastLbl = new JLabel();
      tinaContrastLbl.setName("tinaContrastLbl");
      tinaContrastLbl.setText("Contrast");
      tinaContrastLbl.setLocation(new Point(4, 28));
      tinaContrastLbl.setSize(new Dimension(94, 22));
      tinaContrastLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaContrastLbl.setPreferredSize(new Dimension(94, 22));
      tinaBrightnessLbl = new JLabel();
      tinaBrightnessLbl.setName("tinaBrightnessLbl");
      tinaBrightnessLbl.setText("Brightness");
      tinaBrightnessLbl.setLocation(new Point(4, 4));
      tinaBrightnessLbl.setSize(new Dimension(94, 22));
      tinaBrightnessLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaBrightnessLbl.setPreferredSize(new Dimension(94, 22));
      tinaColoringPanel = new JPanel();
      tinaColoringPanel.setLayout(null);
      tinaColoringPanel.add(tinaBrightnessLbl, null);
      tinaColoringPanel.add(getTinaBrightnessREd(), null);
      tinaColoringPanel.add(getTinaBrightnessSlider(), null);
      tinaColoringPanel.add(tinaContrastLbl, null);
      tinaColoringPanel.add(tinaGammaLbl, null);
      tinaColoringPanel.add(tinaVibrancyLbl, null);
      tinaColoringPanel.add(tinaGammaThresholdLbl, null);
      tinaColoringPanel.add(getTinaContrastREd(), null);
      tinaColoringPanel.add(getTinaGammaREd(), null);
      tinaColoringPanel.add(getTinaVibrancyREd(), null);
      tinaColoringPanel.add(getTinaGammaThresholdREd(), null);
      tinaColoringPanel.add(getTinaContrastSlider(), null);
      tinaColoringPanel.add(getTinaGammaSlider(), null);
      tinaColoringPanel.add(getTinaVibrancySlider(), null);
      tinaColoringPanel.add(getTinaGammaThresholdSlider(), null);

      bgTransparencyCBx = new JCheckBox("Background transparency");
      bgTransparencyCBx.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.flameTransparencyCbx_changed();
          }
        }
      });
      bgTransparencyCBx.setBounds(451, 53, 197, 18);
      tinaColoringPanel.add(bgTransparencyCBx);
      tinaColoringPanel.add(getBackgroundColorIndicatorBtn());

      JLabel lblBackgroundColor = new JLabel();
      lblBackgroundColor.setText("Background color");
      lblBackgroundColor.setSize(new Dimension(94, 22));
      lblBackgroundColor.setPreferredSize(new Dimension(94, 22));
      lblBackgroundColor.setLocation(new Point(4, 4));
      lblBackgroundColor.setFont(new Font("Dialog", Font.BOLD, 10));
      lblBackgroundColor.setBounds(451, 4, 94, 22);
      tinaColoringPanel.add(lblBackgroundColor);

      JLabel tinaSaturationLbl = new JLabel();
      tinaSaturationLbl.setText("Saturation");
      tinaSaturationLbl.setSize(new Dimension(94, 22));
      tinaSaturationLbl.setPreferredSize(new Dimension(94, 22));
      tinaSaturationLbl.setName("tinaSaturationLbl");
      tinaSaturationLbl.setLocation(new Point(4, 100));
      tinaSaturationLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaSaturationLbl.setBounds(451, 98, 94, 22);
      tinaColoringPanel.add(tinaSaturationLbl);

      tinaSaturationREd = new JWFNumberField();
      tinaSaturationREd.setHasMaxValue(true);
      tinaSaturationREd.setValueStep(0.01);
      tinaSaturationREd.setText("");
      tinaSaturationREd.setSize(new Dimension(100, 24));
      tinaSaturationREd.setPreferredSize(new Dimension(100, 24));
      tinaSaturationREd.setMotionPropertyName("saturation");
      tinaSaturationREd.setMaxValue(2.0);
      tinaSaturationREd.setLocation(new Point(100, 100));
      tinaSaturationREd.setLinkedMotionControlName("tinaSaturationSlider");
      tinaSaturationREd.setLinkedLabelControlName("tinaSaturationLbl");
      tinaSaturationREd.setHasMinValue(true);
      tinaSaturationREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaSaturationREd.setBounds(547, 98, 100, 24);
      tinaSaturationREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSaturationREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaSaturationREd.isMouseAdjusting() || tinaSaturationREd.getMouseChangeCount() == 0) {
              if (!tinaSaturationSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().saturationREd_changed();
          }
        }
      });
      tinaColoringPanel.add(tinaSaturationREd);

      tinaSaturationSlider = new JSlider();
      tinaSaturationSlider.setValue(0);
      tinaSaturationSlider.setSize(new Dimension(220, 19));
      tinaSaturationSlider.setPreferredSize(new Dimension(220, 19));
      tinaSaturationSlider.setName("tinaSaturationSlider");
      tinaSaturationSlider.setMinimum(0);
      tinaSaturationSlider.setMaximum(200);
      tinaSaturationSlider.setLocation(new Point(202, 100));
      tinaSaturationSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaSaturationSlider.setBounds(649, 98, 220, 19);
      tinaSaturationSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSaturationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().saturationSlider_stateChanged(e);
        }
      });

      tinaColoringPanel.add(tinaSaturationSlider);
    }
    return tinaColoringPanel;
  }

  /**
   * This method initializes tinaCameraRollREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaCameraRollREd() {
    if (tinaCameraRollREd == null) {
      tinaCameraRollREd = new JWFNumberField();
      tinaCameraRollREd.setLinkedLabelControlName("tinaCameraRollLbl");
      tinaCameraRollREd.setLinkedMotionControlName("tinaCameraRollSlider");
      tinaCameraRollREd.setMotionPropertyName("camRoll");
      tinaCameraRollREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraRollREd.setValueStep(1.0);
      tinaCameraRollREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraRollREd.isMouseAdjusting() || tinaCameraRollREd.getMouseChangeCount() == 0) {
            if (!tinaCameraRollSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().cameraRollREd_changed();
        }
      });
      tinaCameraRollREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraRollREd.setLocation(new Point(71, 4));
      tinaCameraRollREd.setSize(new Dimension(100, 24));
      tinaCameraRollREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraRollREd;
  }

  /**
   * This method initializes tinaCameraPitchREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaCameraPitchREd() {
    if (tinaCameraPitchREd == null) {
      tinaCameraPitchREd = new JWFNumberField();
      tinaCameraPitchREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraPitchREd.setMotionPropertyName("camPitch");
      tinaCameraPitchREd.setLinkedLabelControlName("tinaCameraPitchLbl");
      tinaCameraPitchREd.setLinkedMotionControlName("tinaCameraPitchSlider");
      tinaCameraPitchREd.setValueStep(1.0);
      tinaCameraPitchREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraPitchREd.isMouseAdjusting() || tinaCameraPitchREd.getMouseChangeCount() == 0) {
            if (!tinaCameraPitchSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().cameraPitchREd_changed();
        }
      });
      tinaCameraPitchREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraPitchREd.setText("");
      tinaCameraPitchREd.setLocation(new Point(71, 28));
      tinaCameraPitchREd.setSize(new Dimension(100, 24));
      tinaCameraPitchREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraPitchREd;
  }

  /**
   * This method initializes tinaCameraYawREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaCameraYawREd() {
    if (tinaCameraYawREd == null) {
      tinaCameraYawREd = new JWFNumberField();
      tinaCameraYawREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraYawREd.setMotionPropertyName("camYaw");
      tinaCameraYawREd.setLinkedLabelControlName("tinaCameraYawLbl");
      tinaCameraYawREd.setLinkedMotionControlName("tinaCameraYawSlider");
      tinaCameraYawREd.setValueStep(1.0);
      tinaCameraYawREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraYawREd.isMouseAdjusting() || tinaCameraYawREd.getMouseChangeCount() == 0) {
            if (!tinaCameraYawSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().cameraYawREd_changed();
        }
      });
      tinaCameraYawREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraYawREd.setText("");
      tinaCameraYawREd.setLocation(new Point(71, 52));
      tinaCameraYawREd.setSize(new Dimension(100, 24));
      tinaCameraYawREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraYawREd;
  }

  /**
   * This method initializes tinaCameraPerspectiveREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaCameraPerspectiveREd() {
    if (tinaCameraPerspectiveREd == null) {
      tinaCameraPerspectiveREd = new JWFNumberField();
      tinaCameraPerspectiveREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraPerspectiveREd.setMotionPropertyName("camPerspective");
      tinaCameraPerspectiveREd.setLinkedLabelControlName("tinaCameraPerspectiveLbl");
      tinaCameraPerspectiveREd.setLinkedMotionControlName("tinaCameraPerspectiveSlider");
      tinaCameraPerspectiveREd.setValueStep(0.01);
      tinaCameraPerspectiveREd.setMaxValue(1.0);
      tinaCameraPerspectiveREd.setHasMinValue(true);
      tinaCameraPerspectiveREd.setHasMaxValue(true);
      tinaCameraPerspectiveREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraPerspectiveREd.isMouseAdjusting() || tinaCameraPerspectiveREd.getMouseChangeCount() == 0) {
            if (!tinaCameraPerspectiveSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().cameraPerspectiveREd_changed();
        }
      });
      tinaCameraPerspectiveREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraPerspectiveREd.setText("");
      tinaCameraPerspectiveREd.setLocation(new Point(71, 76));
      tinaCameraPerspectiveREd.setSize(new Dimension(100, 24));
      tinaCameraPerspectiveREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraPerspectiveREd;
  }

  /**
   * This method initializes tinaCameraRollSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaCameraRollSlider() {
    if (tinaCameraRollSlider == null) {
      tinaCameraRollSlider = new JSlider();
      tinaCameraRollSlider.setName("tinaCameraRollSlider");
      tinaCameraRollSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraRollSlider.setMaximum(180);
      tinaCameraRollSlider.setLocation(new Point(173, 4));
      tinaCameraRollSlider.setSize(new Dimension(205, 19));
      tinaCameraRollSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraRollSlider.setValue(0);
      tinaCameraRollSlider.setMinimum(-180);
      tinaCameraRollSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().cameraRollSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraRollSlider;
  }

  /**
   * This method initializes tinaCameraPitchSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaCameraPitchSlider() {
    if (tinaCameraPitchSlider == null) {
      tinaCameraPitchSlider = new JSlider();
      tinaCameraPitchSlider.setName("tinaCameraPitchSlider");
      tinaCameraPitchSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraPitchSlider.setMaximum(180);
      tinaCameraPitchSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraPitchSlider.setLocation(new Point(173, 28));
      tinaCameraPitchSlider.setSize(new Dimension(205, 19));
      tinaCameraPitchSlider.setValue(0);
      tinaCameraPitchSlider.setMinimum(-180);
      tinaCameraPitchSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().cameraPitchSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraPitchSlider;
  }

  /**
   * This method initializes tinaCameraYawSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaCameraYawSlider() {
    if (tinaCameraYawSlider == null) {
      tinaCameraYawSlider = new JSlider();
      tinaCameraYawSlider.setName("tinaCameraYawSlider");
      tinaCameraYawSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraYawSlider.setMaximum(180);
      tinaCameraYawSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraYawSlider.setLocation(new Point(173, 52));
      tinaCameraYawSlider.setSize(new Dimension(205, 19));
      tinaCameraYawSlider.setValue(0);
      tinaCameraYawSlider.setMinimum(-180);
      tinaCameraYawSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().cameraYawSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraYawSlider;
  }

  /**
   * This method initializes tinaCameraPerspectiveSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaCameraPerspectiveSlider() {
    if (tinaCameraPerspectiveSlider == null) {
      tinaCameraPerspectiveSlider = new JSlider();
      tinaCameraPerspectiveSlider.setName("tinaCameraPerspectiveSlider");
      tinaCameraPerspectiveSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraPerspectiveSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraPerspectiveSlider.setSize(new Dimension(205, 19));
      tinaCameraPerspectiveSlider.setValue(0);
      tinaCameraPerspectiveSlider.setLocation(new Point(173, 76));
      tinaCameraPerspectiveSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().cameraPerspectiveSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraPerspectiveSlider;
  }

  /**
   * This method initializes tinaLoadFlameButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaLoadFlameButton() {
    if (tinaLoadFlameButton == null) {
      tinaLoadFlameButton = new JButton();
      tinaLoadFlameButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/document-open-5.png")));
      tinaLoadFlameButton.setMinimumSize(new Dimension(100, 24));
      tinaLoadFlameButton.setMaximumSize(new Dimension(32000, 24));
      tinaLoadFlameButton.setText("Load Flame...");
      tinaLoadFlameButton.setPreferredSize(new Dimension(125, 24));
      tinaLoadFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaLoadFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.loadFlameButton_actionPerformed(e);
        }
      });
    }
    return tinaLoadFlameButton;
  }

  /**
   * This method initializes tinaSaveFlameButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaSaveFlameButton() {
    if (tinaSaveFlameButton == null) {
      tinaSaveFlameButton = new JButton();
      tinaSaveFlameButton.setIconTextGap(2);
      tinaSaveFlameButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/document-export-3.png")));
      tinaSaveFlameButton.setMinimumSize(new Dimension(100, 24));
      tinaSaveFlameButton.setMaximumSize(new Dimension(32000, 24));
      tinaSaveFlameButton.setText("Save...");
      tinaSaveFlameButton.setPreferredSize(new Dimension(125, 24));
      tinaSaveFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaSaveFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.saveFlameButton_actionPerformed(e);
        }
      });
    }
    return tinaSaveFlameButton;
  }

  /**
   * This method initializes tinaRenderFlameButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaRenderFlameButton() {
    if (tinaRenderFlameButton == null) {
      tinaRenderFlameButton = new JButton();
      tinaRenderFlameButton.setIconTextGap(0);
      tinaRenderFlameButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive.png")));
      tinaRenderFlameButton.setMnemonic(KeyEvent.VK_R);
      tinaRenderFlameButton.setFont(new Font("Dialog", Font.BOLD, 9));
      tinaRenderFlameButton.setToolTipText("Render image");
      tinaRenderFlameButton.setPreferredSize(new Dimension(42, 24));
      tinaRenderFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.renderFlameButton_actionPerformed(e);
        }
      });
    }
    return tinaRenderFlameButton;
  }

  /**
   * This method initializes renderImageNormalButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getRenderMainButton() {
    if (renderMainButton == null) {
      renderMainButton = new JButton();
      renderMainButton.setMinimumSize(new Dimension(125, 52));
      renderMainButton.setMaximumSize(new Dimension(32000, 52));
      renderMainButton.setText("Render image");
      renderMainButton.setPreferredSize(new Dimension(125, 24));
      renderMainButton.setFont(new Font("Dialog", Font.BOLD, 10));
      renderMainButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.renderImageButton_actionPerformed();
        }
      });
    }
    return renderMainButton;
  }

  /**
   * This method initializes tinaCameraCentreXREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaCameraCentreXREd() {
    if (tinaCameraCentreXREd == null) {
      tinaCameraCentreXREd = new JWFNumberField();
      tinaCameraCentreXREd.setLinkedLabelControlName("tinaCameraCentreXLbl");
      tinaCameraCentreXREd.setLinkedMotionControlName("tinaCameraCentreXSlider");
      tinaCameraCentreXREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraCentreXREd.setMotionPropertyName("centreX");
      tinaCameraCentreXREd.setValueStep(0.05);
      tinaCameraCentreXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraCentreXREd.isMouseAdjusting() || tinaCameraCentreXREd.getMouseChangeCount() == 0) {
            if (!tinaCameraCentreXSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().cameraCentreXREd_changed();
        }
      });
      tinaCameraCentreXREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraCentreXREd.setText("");
      tinaCameraCentreXREd.setLocation(new Point(456, 4));
      tinaCameraCentreXREd.setSize(new Dimension(100, 24));
      tinaCameraCentreXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraCentreXREd;
  }

  /**
   * This method initializes tinaCameraCentreYREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaCameraCentreYREd() {
    if (tinaCameraCentreYREd == null) {
      tinaCameraCentreYREd = new JWFNumberField();
      tinaCameraCentreYREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraCentreYREd.setMotionPropertyName("centreY");
      tinaCameraCentreYREd.setLinkedLabelControlName("tinaCameraCentreYLbl");
      tinaCameraCentreYREd.setLinkedMotionControlName("tinaCameraCentreYSlider");
      tinaCameraCentreYREd.setValueStep(0.05);
      tinaCameraCentreYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraCentreYREd.isMouseAdjusting() || tinaCameraCentreYREd.getMouseChangeCount() == 0) {
            if (!tinaCameraCentreYSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().cameraCentreYREd_changed();
        }
      });
      tinaCameraCentreYREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraCentreYREd.setText("");
      tinaCameraCentreYREd.setLocation(new Point(456, 28));
      tinaCameraCentreYREd.setSize(new Dimension(100, 24));
      tinaCameraCentreYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraCentreYREd;
  }

  /**
   * This method initializes tinaCameraCentreXSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaCameraCentreXSlider() {
    if (tinaCameraCentreXSlider == null) {
      tinaCameraCentreXSlider = new JSlider();
      tinaCameraCentreXSlider.setName("tinaCameraCentreXSlider");
      tinaCameraCentreXSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraCentreXSlider.setMinimum(-25000);
      tinaCameraCentreXSlider.setLocation(new Point(558, 4));
      tinaCameraCentreXSlider.setSize(new Dimension(205, 19));
      tinaCameraCentreXSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCentreXSlider.setValue(0);
      tinaCameraCentreXSlider.setMaximum(25000);
      tinaCameraCentreXSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().cameraCentreXSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraCentreXSlider;
  }

  /**
   * This method initializes tinaCameraCentreYSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaCameraCentreYSlider() {
    if (tinaCameraCentreYSlider == null) {
      tinaCameraCentreYSlider = new JSlider();
      tinaCameraCentreYSlider.setName("tinaCameraCentreYSlider");
      tinaCameraCentreYSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraCentreYSlider.setMinimum(-25000);
      tinaCameraCentreYSlider.setLocation(new Point(558, 28));
      tinaCameraCentreYSlider.setSize(new Dimension(205, 19));
      tinaCameraCentreYSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCentreYSlider.setValue(0);
      tinaCameraCentreYSlider.setMaximum(25000);
      tinaCameraCentreYSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().cameraCentreYSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraCentreYSlider;
  }

  /**
   * This method initializes tinaCameraZoomREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaCameraZoomREd() {
    if (tinaCameraZoomREd == null) {
      tinaCameraZoomREd = new JWFNumberField();
      tinaCameraZoomREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaCameraZoomREd.setMotionPropertyName("camZoom");
      tinaCameraZoomREd.setLinkedLabelControlName("tinaCameraZoomLbl");
      tinaCameraZoomREd.setLinkedMotionControlName("tinaCameraZoomSlider");
      tinaCameraZoomREd.setValueStep(0.01);
      tinaCameraZoomREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraZoomREd.isMouseAdjusting() || tinaCameraZoomREd.getMouseChangeCount() == 0) {
            if (!tinaCameraZoomSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().cameraZoomREd_changed();
        }
      });
      tinaCameraZoomREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraZoomREd.setText("");
      tinaCameraZoomREd.setLocation(new Point(456, 52));
      tinaCameraZoomREd.setSize(new Dimension(100, 24));
      tinaCameraZoomREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraZoomREd;
  }

  /**
   * This method initializes tinaCameraZoomSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaCameraZoomSlider() {
    if (tinaCameraZoomSlider == null) {
      tinaCameraZoomSlider = new JSlider();
      tinaCameraZoomSlider.setName("tinaCameraZoomSlider");
      tinaCameraZoomSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraZoomSlider.setMinimum(100);
      tinaCameraZoomSlider.setLocation(new Point(558, 52));
      tinaCameraZoomSlider.setSize(new Dimension(205, 19));
      tinaCameraZoomSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraZoomSlider.setValue(0);
      tinaCameraZoomSlider.setMaximum(10000);
      tinaCameraZoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().cameraZoomSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraZoomSlider;
  }

  /**
   * This method initializes tinaBrightnessREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaBrightnessREd() {
    if (tinaBrightnessREd == null) {
      tinaBrightnessREd = new JWFNumberField();
      tinaBrightnessREd.setMotionPropertyName("brightness");
      tinaBrightnessREd.setLinkedMotionControlName("tinaBrightnessSlider");
      tinaBrightnessREd.setLinkedLabelControlName("tinaBrightnessLbl");
      tinaBrightnessREd.setValueStep(0.05);
      tinaBrightnessREd.setMaxValue(25.0);
      tinaBrightnessREd.setHasMinValue(true);
      tinaBrightnessREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaBrightnessREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaBrightnessREd.isMouseAdjusting() || tinaBrightnessREd.getMouseChangeCount() == 0) {
            if (!tinaBrightnessSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().brightnessREd_changed();
        }
      });
      tinaBrightnessREd.setPreferredSize(new Dimension(100, 24));
      tinaBrightnessREd.setText("");
      tinaBrightnessREd.setSize(new Dimension(100, 24));
      tinaBrightnessREd.setLocation(new Point(100, 4));
      tinaBrightnessREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaBrightnessREd;
  }

  /**
   * This method initializes tinaBrightnessSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaBrightnessSlider() {
    if (tinaBrightnessSlider == null) {
      tinaBrightnessSlider = new JSlider();
      tinaBrightnessSlider.setName("tinaBrightnessSlider");
      tinaBrightnessSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaBrightnessSlider.setMinimum(0);
      tinaBrightnessSlider.setLocation(new Point(202, 4));
      tinaBrightnessSlider.setSize(new Dimension(220, 19));
      tinaBrightnessSlider.setPreferredSize(new Dimension(220, 19));
      tinaBrightnessSlider.setValue(0);
      tinaBrightnessSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaBrightnessSlider.setMaximum(2500);
      tinaBrightnessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().brightnessSlider_stateChanged(e);
        }
      });
    }
    return tinaBrightnessSlider;
  }

  /**
   * This method initializes tinaPixelsPerUnitREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPixelsPerUnitREd() {
    if (tinaPixelsPerUnitREd == null) {
      tinaPixelsPerUnitREd = new JWFNumberField();
      tinaPixelsPerUnitREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaPixelsPerUnitREd.setMotionPropertyName("pixelsPerUnit");
      tinaPixelsPerUnitREd.setLinkedLabelControlName("tinaPixelsPerUnitLbl");
      tinaPixelsPerUnitREd.setLinkedMotionControlName("tinaPixelsPerUnitSlider");
      tinaPixelsPerUnitREd.setValueStep(1.0);
      tinaPixelsPerUnitREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPixelsPerUnitREd.isMouseAdjusting() || tinaPixelsPerUnitREd.getMouseChangeCount() == 0) {
            if (!tinaPixelsPerUnitSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().pixelsPerUnitREd_changed();
        }
      });
      tinaPixelsPerUnitREd.setPreferredSize(new Dimension(100, 24));
      tinaPixelsPerUnitREd.setText("");
      tinaPixelsPerUnitREd.setLocation(new Point(456, 76));
      tinaPixelsPerUnitREd.setSize(new Dimension(100, 24));
      tinaPixelsPerUnitREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPixelsPerUnitREd;
  }

  /**
   * This method initializes tinaPixelsPerUnitSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPixelsPerUnitSlider() {
    if (tinaPixelsPerUnitSlider == null) {
      tinaPixelsPerUnitSlider = new JSlider();
      tinaPixelsPerUnitSlider.setName("tinaPixelsPerUnitSlider");
      tinaPixelsPerUnitSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPixelsPerUnitSlider.setMaximum(1000);
      tinaPixelsPerUnitSlider.setMinimum(0);
      tinaPixelsPerUnitSlider.setValue(0);
      tinaPixelsPerUnitSlider.setSize(new Dimension(205, 19));
      tinaPixelsPerUnitSlider.setLocation(new Point(558, 76));
      tinaPixelsPerUnitSlider.setPreferredSize(new Dimension(220, 19));
      tinaPixelsPerUnitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().pixelsPerUnitSlider_stateChanged(e);
        }
      });
    }
    return tinaPixelsPerUnitSlider;
  }

  /**
   * This method initializes tinaContrastREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaContrastREd() {
    if (tinaContrastREd == null) {
      tinaContrastREd = new JWFNumberField();
      tinaContrastREd.setMotionPropertyName("contrast");
      tinaContrastREd.setLinkedMotionControlName("tinaContrastSlider");
      tinaContrastREd.setLinkedLabelControlName("tinaContrastLbl");
      tinaContrastREd.setValueStep(0.05);
      tinaContrastREd.setHasMinValue(true);
      tinaContrastREd.setMaxValue(5.0);
      tinaContrastREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaContrastREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaContrastREd.isMouseAdjusting() || tinaContrastREd.getMouseChangeCount() == 0) {
              if (!tinaContrastSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().contrastREd_changed();
          }
        }
      });
      tinaContrastREd.setPreferredSize(new Dimension(100, 24));
      tinaContrastREd.setText("");
      tinaContrastREd.setLocation(new Point(100, 28));
      tinaContrastREd.setSize(new Dimension(100, 24));
      tinaContrastREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaContrastREd;
  }

  /**
   * This method initializes tinaGammaREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaGammaREd() {
    if (tinaGammaREd == null) {
      tinaGammaREd = new JWFNumberField();
      tinaGammaREd.setMotionPropertyName("gamma");
      tinaGammaREd.setLinkedMotionControlName("tinaGammaSlider");
      tinaGammaREd.setLinkedLabelControlName("tinaGammaLbl");
      tinaGammaREd.setHasMinValue(true);
      tinaGammaREd.setMaxValue(10.0);
      tinaGammaREd.setValueStep(0.05);
      tinaGammaREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaGammaREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaGammaREd.isMouseAdjusting() || tinaGammaREd.getMouseChangeCount() == 0) {
              if (!tinaGammaSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().gammaREd_changed();
          }
        }
      });
      tinaGammaREd.setPreferredSize(new Dimension(100, 24));
      tinaGammaREd.setText("");
      tinaGammaREd.setLocation(new Point(100, 52));
      tinaGammaREd.setSize(new Dimension(100, 24));
      tinaGammaREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaGammaREd;
  }

  /**
   * This method initializes tinaVibrancyREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaVibrancyREd() {
    if (tinaVibrancyREd == null) {
      tinaVibrancyREd = new JWFNumberField();
      tinaVibrancyREd.setMotionPropertyName("vibrancy");
      tinaVibrancyREd.setLinkedMotionControlName("tinaVibrancySlider");
      tinaVibrancyREd.setLinkedLabelControlName("tinaVibrancyLbl");
      tinaVibrancyREd.setValueStep(0.05);
      tinaVibrancyREd.setHasMinValue(true);
      tinaVibrancyREd.setHasMaxValue(true);
      tinaVibrancyREd.setMaxValue(1.0);
      tinaVibrancyREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaVibrancyREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaVibrancyREd.isMouseAdjusting() || tinaVibrancyREd.getMouseChangeCount() == 0) {
              if (!tinaVibrancySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().vibrancyREd_changed();
          }
        }
      });
      tinaVibrancyREd.setPreferredSize(new Dimension(100, 24));
      tinaVibrancyREd.setText("");
      tinaVibrancyREd.setLocation(new Point(100, 100));
      tinaVibrancyREd.setSize(new Dimension(100, 24));
      tinaVibrancyREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaVibrancyREd;
  }

  /**
   * This method initializes tinaOversampleREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaGammaThresholdREd() {
    if (tinaGammaThresholdREd == null) {
      tinaGammaThresholdREd = new JWFNumberField();
      tinaGammaThresholdREd.setMotionPropertyName("gammaThreshold");
      tinaGammaThresholdREd.setLinkedMotionControlName("tinaGammaThresholdSlider");
      tinaGammaThresholdREd.setLinkedLabelControlName("tinaGammaThresholdLbl");
      tinaGammaThresholdREd.setValueStep(0.005);
      tinaGammaThresholdREd.setMaxValue(1.0);
      tinaGammaThresholdREd.setHasMinValue(true);
      tinaGammaThresholdREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaGammaThresholdREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaGammaThresholdREd.isMouseAdjusting() || tinaGammaThresholdREd.getMouseChangeCount() == 0) {
              if (!tinaGammaThresholdSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().gammaThresholdREd_changed();
          }
        }
      });
      tinaGammaThresholdREd.setPreferredSize(new Dimension(100, 24));
      tinaGammaThresholdREd.setText("");
      tinaGammaThresholdREd.setLocation(new Point(100, 76));
      tinaGammaThresholdREd.setSize(new Dimension(100, 24));
      tinaGammaThresholdREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaGammaThresholdREd;
  }

  /**
   * This method initializes tinaContrastSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaContrastSlider() {
    if (tinaContrastSlider == null) {
      tinaContrastSlider = new JSlider();
      tinaContrastSlider.setName("tinaContrastSlider");
      tinaContrastSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaContrastSlider.setMaximum(500);
      tinaContrastSlider.setMinimum(0);
      tinaContrastSlider.setValue(0);
      tinaContrastSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaContrastSlider.setSize(new Dimension(220, 19));
      tinaContrastSlider.setLocation(new Point(202, 28));
      tinaContrastSlider.setPreferredSize(new Dimension(220, 19));
      tinaContrastSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().contrastSlider_stateChanged(e);
        }
      });
    }
    return tinaContrastSlider;
  }

  /**
   * This method initializes tinaGammaSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaGammaSlider() {
    if (tinaGammaSlider == null) {
      tinaGammaSlider = new JSlider();
      tinaGammaSlider.setName("tinaGammaSlider");
      tinaGammaSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaGammaSlider.setMaximum(1000);
      tinaGammaSlider.setMinimum(0);
      tinaGammaSlider.setValue(0);
      tinaGammaSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGammaSlider.setSize(new Dimension(220, 19));
      tinaGammaSlider.setLocation(new Point(202, 52));
      tinaGammaSlider.setPreferredSize(new Dimension(220, 19));
      tinaGammaSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().gammaSlider_stateChanged(e);
        }
      });
    }
    return tinaGammaSlider;
  }

  /**
   * This method initializes tinaVibrancySlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaVibrancySlider() {
    if (tinaVibrancySlider == null) {
      tinaVibrancySlider = new JSlider();
      tinaVibrancySlider.setName("tinaVibrancySlider");
      tinaVibrancySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaVibrancySlider.setMaximum(100);
      tinaVibrancySlider.setMinimum(0);
      tinaVibrancySlider.setValue(0);
      tinaVibrancySlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaVibrancySlider.setSize(new Dimension(220, 19));
      tinaVibrancySlider.setLocation(new Point(202, 100));
      tinaVibrancySlider.setPreferredSize(new Dimension(220, 19));
      tinaVibrancySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().vibrancySlider_stateChanged(e);
        }
      });
    }
    return tinaVibrancySlider;
  }

  /**
   * This method initializes tinaOversampleSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaGammaThresholdSlider() {
    if (tinaGammaThresholdSlider == null) {
      tinaGammaThresholdSlider = new JSlider();
      tinaGammaThresholdSlider.setName("tinaGammaThresholdSlider");
      tinaGammaThresholdSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaGammaThresholdSlider.setMaximum(1000);
      tinaGammaThresholdSlider.setMinimum(1);
      tinaGammaThresholdSlider.setValue(0);
      tinaGammaThresholdSlider.setMajorTickSpacing(1);
      tinaGammaThresholdSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGammaThresholdSlider.setSize(new Dimension(220, 19));
      tinaGammaThresholdSlider.setLocation(new Point(202, 76));
      tinaGammaThresholdSlider.setPreferredSize(new Dimension(220, 19));
      tinaGammaThresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().gammaThresholdSlider_stateChanged(e);
        }
      });
    }
    return tinaGammaThresholdSlider;
  }

  /**
   * This method initializes tinaAddTransformationButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaAddTransformationButton() {
    if (tinaAddTransformationButton == null) {
      tinaAddTransformationButton = new JButton();
      tinaAddTransformationButton.setText("Add");
      tinaAddTransformationButton.setPreferredSize(new Dimension(56, 24));
      tinaAddTransformationButton.setToolTipText("Add new transform");
      tinaAddTransformationButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaAddTransformationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.addXForm();
        }
      });
    }
    return tinaAddTransformationButton;
  }

  /**
   * This method initializes tinaEastTabbedPane 
   *  
   * @return javax.swing.JTabbedPane  
   */
  private JTabbedPane getTinaEastTabbedPane() {
    if (tinaEastTabbedPane == null) {
      tinaEastTabbedPane = new JTabbedPane();
      tinaEastTabbedPane.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            switch (tinaEastTabbedPane.getSelectedIndex()) {
              case 1:
                if (tinaController.getJwfScriptController() != null) {
                  tinaController.getJwfScriptController().onActivate();
                }
                break;
            }
          }
        }
      });
      tinaEastTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaEastTabbedPane.addTab("Transformations", null, getTinaTransformationsPanel(), null);
      tinaEastTabbedPane.addTab("Scripts", null, getScriptPanel(), null);
    }
    return tinaEastTabbedPane;
  }

  /**
   * This method initializes tinaTransformationsPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaTransformationsPanel() {
    if (tinaTransformationsPanel == null) {
      tinaTransformationsPanel = new JPanel();
      tinaTransformationsPanel.setLayout(new BorderLayout());
      tinaTransformationsPanel.setToolTipText("");
      tinaTransformationsPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaTransformationsPanel.add(getTransformationsSplitPane(), BorderLayout.CENTER);
    }
    return tinaTransformationsPanel;
  }

  /**
   * This method initializes tinaPalettePanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaPalettePanel() {
    if (tinaPalettePanel == null) {
      tinaPalettePanel = new JPanel();
      tinaPalettePanel.setLayout(new BorderLayout());
      tinaPalettePanel.add(getTinaPaletteSubNorthPanel(), BorderLayout.WEST);
      tinaPalettePanel.add(getTinaPaletteSubCenterPanel(), BorderLayout.CENTER);
    }
    return tinaPalettePanel;
  }

  /**
   * This method initializes tinaDeleteTransformationButton 
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaDeleteTransformationButton() {
    if (tinaDeleteTransformationButton == null) {
      tinaDeleteTransformationButton = new JButton();
      tinaDeleteTransformationButton.setText("Delete");
      tinaDeleteTransformationButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaDeleteTransformationButton.setToolTipText("Delete triangle");
      tinaDeleteTransformationButton.setPreferredSize(new Dimension(90, 24));
      tinaDeleteTransformationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.deleteXForm();
        }
      });
    }
    return tinaDeleteTransformationButton;
  }

  /**
   * This method initializes tinaDuplicateTransformationButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaDuplicateTransformationButton() {
    if (tinaDuplicateTransformationButton == null) {
      tinaDuplicateTransformationButton = new JButton();
      tinaDuplicateTransformationButton.setText("Dupl");
      tinaDuplicateTransformationButton.setPreferredSize(new Dimension(56, 24));
      tinaDuplicateTransformationButton.setToolTipText("Duplicate transform");
      tinaDuplicateTransformationButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaDuplicateTransformationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.duplicateXForm();
        }
      });
    }
    return tinaDuplicateTransformationButton;
  }

  /**
   * This method initializes tinaTransformationsScrollPane  
   *  
   * @return javax.swing.JScrollPane  
   */
  private JScrollPane getTinaTransformationsScrollPane() {
    if (tinaTransformationsScrollPane == null) {
      tinaTransformationsScrollPane = new JScrollPane();
      tinaTransformationsScrollPane.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaTransformationsScrollPane.setViewportView(getTinaTransformationsTable());
    }
    return tinaTransformationsScrollPane;
  }

  /**
   * This method initializes tinaTransformationsTable 
   *  
   * @return javax.swing.JTable 
   */
  private JTable getTinaTransformationsTable() {
    if (tinaTransformationsTable == null) {
      tinaTransformationsTable = new JTable();
      tinaTransformationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      tinaTransformationsTable.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaTransformationsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            tinaController.transformationTableClicked();
          }
        }

      });

    }
    return tinaTransformationsTable;
  }

  /**
   * This method initializes tinaTransformationsTabbedPane  
   *  
   * @return javax.swing.JTabbedPane  
   */
  private JTabbedPane getTinaTransformationsTabbedPane() {
    if (tinaTransformationsTabbedPane == null) {
      tinaTransformationsTabbedPane = new JTabbedPane();
      tinaTransformationsTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaTransformationsTabbedPane.addTab("Affine transf", null, getTinaAffineTransformationPanel(), null);
      tinaTransformationsTabbedPane.addTab("Nonlinear", null, getTinaVariationPanel(), null);
      tinaTransformationsTabbedPane.addTab("Xaos", null, getTinaModifiedWeightsPanel(), null);
      tinaTransformationsTabbedPane.addTab("Color", null, getTinaTransformationColorPanel(), null);

      JPanel panel_1 = new JPanel();
      tinaTransformationsTabbedPane.addTab("Gamma", null, panel_1, null);
      panel_1.setLayout(null);

      xFormModGammaSlider = new JSlider();
      xFormModGammaSlider.setValue(0);
      xFormModGammaSlider.setSize(new Dimension(172, 22));
      xFormModGammaSlider.setPreferredSize(new Dimension(172, 22));
      xFormModGammaSlider.setMinimum(-100);
      xFormModGammaSlider.setMaximum(100);
      xFormModGammaSlider.setLocation(new Point(125, 21));
      xFormModGammaSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormModGammaSlider.setBounds(125, 6, 195, 22);
      xFormModGammaSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormModGammaSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormModGammaSlider_changed();
        }
      });
      panel_1.add(xFormModGammaSlider);

      xFormModGammaREd = new JWFNumberField();
      xFormModGammaREd.setValueStep(0.01);
      xFormModGammaREd.setText("");
      xFormModGammaREd.setSize(new Dimension(55, 22));
      xFormModGammaREd.setPreferredSize(new Dimension(55, 22));
      xFormModGammaREd.setMinValue(-3.0);
      xFormModGammaREd.setMaxValue(3.0);
      xFormModGammaREd.setLocation(new Point(70, 21));
      xFormModGammaREd.setHasMinValue(true);
      xFormModGammaREd.setHasMaxValue(true);
      xFormModGammaREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormModGammaREd.setBounds(70, 6, 55, 22);
      xFormModGammaREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormModGammaREd.isMouseAdjusting() || xFormModGammaREd.getMouseChangeCount() == 0) {
              if (!xFormModGammaSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormModGammaREd_changed();
          }
        }
      });

      panel_1.add(xFormModGammaREd);

      JLabel label = new JLabel();
      label.setToolTipText("Local change of gamma");
      label.setText("Gamma");
      label.setSize(new Dimension(64, 22));
      label.setPreferredSize(new Dimension(64, 22));
      label.setLocation(new Point(6, 21));
      label.setFont(new Font("Dialog", Font.BOLD, 10));
      label.setBounds(6, 6, 64, 22);
      panel_1.add(label);

      JLabel label_2 = new JLabel();
      label_2.setToolTipText("Blending of local gamma change");
      label_2.setText("Gamma Spd");
      label_2.setSize(new Dimension(64, 22));
      label_2.setPreferredSize(new Dimension(64, 22));
      label_2.setLocation(new Point(6, 47));
      label_2.setFont(new Font("Dialog", Font.BOLD, 10));
      label_2.setBounds(6, 32, 64, 22);
      panel_1.add(label_2);

      xFormModGammaSpeedSlider = new JSlider();
      xFormModGammaSpeedSlider.setValue(0);
      xFormModGammaSpeedSlider.setSize(new Dimension(172, 22));
      xFormModGammaSpeedSlider.setPreferredSize(new Dimension(172, 22));
      xFormModGammaSpeedSlider.setMinimum(-100);
      xFormModGammaSpeedSlider.setMaximum(100);
      xFormModGammaSpeedSlider.setLocation(new Point(125, 47));
      xFormModGammaSpeedSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormModGammaSpeedSlider.setBounds(125, 32, 195, 22);
      xFormModGammaSpeedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormModGammaSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormModGammaSpeedSlider_changed();
        }
      });
      panel_1.add(xFormModGammaSpeedSlider);

      xFormModGammaSpeedREd = new JWFNumberField();
      xFormModGammaSpeedREd.setValueStep(0.01);
      xFormModGammaSpeedREd.setText("");
      xFormModGammaSpeedREd.setSize(new Dimension(55, 22));
      xFormModGammaSpeedREd.setPreferredSize(new Dimension(55, 22));
      xFormModGammaSpeedREd.setMinValue(-1.0);
      xFormModGammaSpeedREd.setMaxValue(1.0);
      xFormModGammaSpeedREd.setLocation(new Point(70, 47));
      xFormModGammaSpeedREd.setHasMinValue(true);
      xFormModGammaSpeedREd.setHasMaxValue(true);
      xFormModGammaSpeedREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormModGammaSpeedREd.setBounds(70, 32, 55, 22);
      xFormModGammaSpeedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormModGammaSpeedREd.isMouseAdjusting() || xFormModGammaSpeedREd.getMouseChangeCount() == 0) {
              if (!xFormModGammaSpeedSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormModGammaSpeedREd_changed();
          }
        }
      });
      panel_1.add(xFormModGammaSpeedREd);

      JLabel lblContrast = new JLabel();
      lblContrast.setToolTipText("Local contrast change (increase/decrease)");
      lblContrast.setText("Contrast");
      lblContrast.setSize(new Dimension(64, 22));
      lblContrast.setPreferredSize(new Dimension(64, 22));
      lblContrast.setLocation(new Point(6, 21));
      lblContrast.setFont(new Font("Dialog", Font.BOLD, 10));
      lblContrast.setBounds(6, 61, 64, 22);
      panel_1.add(lblContrast);

      xFormModContrastREd = new JWFNumberField();
      xFormModContrastREd.setValueStep(0.01);
      xFormModContrastREd.setText("");
      xFormModContrastREd.setSize(new Dimension(55, 22));
      xFormModContrastREd.setPreferredSize(new Dimension(55, 22));
      xFormModContrastREd.setMinValue(-2.0);
      xFormModContrastREd.setMaxValue(2.0);
      xFormModContrastREd.setLocation(new Point(70, 21));
      xFormModContrastREd.setHasMinValue(true);
      xFormModContrastREd.setHasMaxValue(true);
      xFormModContrastREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormModContrastREd.setBounds(70, 61, 55, 22);
      xFormModContrastREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormModContrastREd.isMouseAdjusting() || xFormModContrastREd.getMouseChangeCount() == 0) {
              if (!xFormModContrastSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormModContrastREd_changed();
          }
        }
      });

      panel_1.add(xFormModContrastREd);

      xFormModContrastSlider = new JSlider();
      xFormModContrastSlider.setValue(0);
      xFormModContrastSlider.setSize(new Dimension(172, 22));
      xFormModContrastSlider.setPreferredSize(new Dimension(172, 22));
      xFormModContrastSlider.setMinimum(-100);
      xFormModContrastSlider.setMaximum(100);
      xFormModContrastSlider.setLocation(new Point(125, 21));
      xFormModContrastSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormModContrastSlider.setBounds(125, 61, 195, 22);
      xFormModContrastSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormModContrastSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormModContrastSlider_changed();
        }
      });

      panel_1.add(xFormModContrastSlider);

      JLabel lblContrstSpd = new JLabel();
      lblContrstSpd.setToolTipText("Blending of local contrast change");
      lblContrstSpd.setText("Contrst Spd");
      lblContrstSpd.setSize(new Dimension(64, 22));
      lblContrstSpd.setPreferredSize(new Dimension(64, 22));
      lblContrstSpd.setLocation(new Point(6, 47));
      lblContrstSpd.setFont(new Font("Dialog", Font.BOLD, 10));
      lblContrstSpd.setBounds(6, 87, 64, 22);
      panel_1.add(lblContrstSpd);

      xFormModContrastSpeedREd = new JWFNumberField();
      xFormModContrastSpeedREd.setValueStep(0.01);
      xFormModContrastSpeedREd.setText("");
      xFormModContrastSpeedREd.setSize(new Dimension(55, 22));
      xFormModContrastSpeedREd.setPreferredSize(new Dimension(55, 22));
      xFormModContrastSpeedREd.setMinValue(-1.0);
      xFormModContrastSpeedREd.setMaxValue(1.0);
      xFormModContrastSpeedREd.setLocation(new Point(70, 47));
      xFormModContrastSpeedREd.setHasMinValue(true);
      xFormModContrastSpeedREd.setHasMaxValue(true);
      xFormModContrastSpeedREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormModContrastSpeedREd.setBounds(70, 87, 55, 22);
      xFormModContrastSpeedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormModContrastSpeedREd.isMouseAdjusting() || xFormModContrastSpeedREd.getMouseChangeCount() == 0) {
              if (!xFormModContrastSpeedSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormModContrastSpeedREd_changed();
          }
        }
      });
      panel_1.add(xFormModContrastSpeedREd);

      xFormModContrastSpeedSlider = new JSlider();
      xFormModContrastSpeedSlider.setValue(0);
      xFormModContrastSpeedSlider.setSize(new Dimension(172, 22));
      xFormModContrastSpeedSlider.setPreferredSize(new Dimension(172, 22));
      xFormModContrastSpeedSlider.setMinimum(-100);
      xFormModContrastSpeedSlider.setMaximum(100);
      xFormModContrastSpeedSlider.setLocation(new Point(125, 47));
      xFormModContrastSpeedSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormModContrastSpeedSlider.setBounds(125, 87, 195, 22);
      xFormModContrastSpeedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormModContrastSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormModContrastSpeedSlider_changed();
        }
      });

      panel_1.add(xFormModContrastSpeedSlider);

      JLabel lblSaturation = new JLabel();
      lblSaturation.setToolTipText("Local modification of color saturation");
      lblSaturation.setText("Saturation");
      lblSaturation.setSize(new Dimension(64, 22));
      lblSaturation.setPreferredSize(new Dimension(64, 22));
      lblSaturation.setLocation(new Point(6, 21));
      lblSaturation.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSaturation.setBounds(6, 117, 64, 22);
      panel_1.add(lblSaturation);

      xFormModSaturationREd = new JWFNumberField();
      xFormModSaturationREd.setValueStep(0.01);
      xFormModSaturationREd.setText("");
      xFormModSaturationREd.setSize(new Dimension(55, 22));
      xFormModSaturationREd.setPreferredSize(new Dimension(55, 22));
      xFormModSaturationREd.setMinValue(-2.0);
      xFormModSaturationREd.setMaxValue(2.0);
      xFormModSaturationREd.setLocation(new Point(70, 21));
      xFormModSaturationREd.setHasMinValue(true);
      xFormModSaturationREd.setHasMaxValue(true);
      xFormModSaturationREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormModSaturationREd.setBounds(70, 117, 55, 22);
      xFormModSaturationREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormModSaturationREd.isMouseAdjusting() || xFormModSaturationREd.getMouseChangeCount() == 0) {
              if (!xFormModSaturationSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormModSaturationREd_changed();
          }
        }
      });
      panel_1.add(xFormModSaturationREd);

      xFormModSaturationSlider = new JSlider();
      xFormModSaturationSlider.setValue(0);
      xFormModSaturationSlider.setSize(new Dimension(172, 22));
      xFormModSaturationSlider.setPreferredSize(new Dimension(172, 22));
      xFormModSaturationSlider.setMinimum(-100);
      xFormModSaturationSlider.setMaximum(100);
      xFormModSaturationSlider.setLocation(new Point(125, 21));
      xFormModSaturationSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormModSaturationSlider.setBounds(125, 117, 195, 22);
      xFormModSaturationSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormModSaturationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormModSaturationSlider_changed();
        }
      });

      panel_1.add(xFormModSaturationSlider);

      JLabel lblSaturatSpd = new JLabel();
      lblSaturatSpd.setToolTipText("Blending of local saturation change");
      lblSaturatSpd.setText("Saturat Spd");
      lblSaturatSpd.setSize(new Dimension(64, 22));
      lblSaturatSpd.setPreferredSize(new Dimension(64, 22));
      lblSaturatSpd.setLocation(new Point(6, 47));
      lblSaturatSpd.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSaturatSpd.setBounds(6, 143, 64, 22);
      panel_1.add(lblSaturatSpd);

      xFormModSaturationSpeedREd = new JWFNumberField();
      xFormModSaturationSpeedREd.setValueStep(0.01);
      xFormModSaturationSpeedREd.setText("");
      xFormModSaturationSpeedREd.setSize(new Dimension(55, 22));
      xFormModSaturationSpeedREd.setPreferredSize(new Dimension(55, 22));
      xFormModSaturationSpeedREd.setMinValue(-1.0);
      xFormModSaturationSpeedREd.setMaxValue(1.0);
      xFormModSaturationSpeedREd.setLocation(new Point(70, 47));
      xFormModSaturationSpeedREd.setHasMinValue(true);
      xFormModSaturationSpeedREd.setHasMaxValue(true);
      xFormModSaturationSpeedREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormModSaturationSpeedREd.setBounds(70, 143, 55, 22);
      xFormModSaturationSpeedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormModSaturationSpeedREd.isMouseAdjusting() || xFormModSaturationSpeedREd.getMouseChangeCount() == 0) {
              if (!xFormModSaturationSpeedSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormModSaturationSpeedREd_changed();
          }
        }
      });
      panel_1.add(xFormModSaturationSpeedREd);

      xFormModSaturationSpeedSlider = new JSlider();
      xFormModSaturationSpeedSlider.setValue(0);
      xFormModSaturationSpeedSlider.setSize(new Dimension(172, 22));
      xFormModSaturationSpeedSlider.setPreferredSize(new Dimension(172, 22));
      xFormModSaturationSpeedSlider.setMinimum(-100);
      xFormModSaturationSpeedSlider.setMaximum(100);
      xFormModSaturationSpeedSlider.setLocation(new Point(125, 47));
      xFormModSaturationSpeedSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormModSaturationSpeedSlider.setBounds(125, 143, 195, 22);
      xFormModSaturationSpeedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormModSaturationSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormModSaturationSpeedSlider_changed();
        }
      });

      panel_1.add(xFormModSaturationSpeedSlider);

      xFormModGammaRandomizeBtn = new JButton();
      xFormModGammaRandomizeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.xFormModGammaRandomizeBtn_Clicked(getXFormModGammaWholeFractalCBx().isSelected());
        }
      });
      xFormModGammaRandomizeBtn.setToolTipText("Randomize local color-changing effects, either of the whole fractal or the selected transform");
      xFormModGammaRandomizeBtn.setText("Randomize");
      xFormModGammaRandomizeBtn.setPreferredSize(new Dimension(104, 24));
      xFormModGammaRandomizeBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormModGammaRandomizeBtn.setBounds(4, 172, 104, 24);
      panel_1.add(xFormModGammaRandomizeBtn);

      xFormModGammaResetBtn = new JButton();
      xFormModGammaResetBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.xFormModGammaResetBtn_Clicked(getXFormModGammaWholeFractalCBx().isSelected());
        }
      });
      xFormModGammaResetBtn.setToolTipText("Reset local color-changing effects, either of the whole fractal or the selected transform");
      xFormModGammaResetBtn.setText("Reset");
      xFormModGammaResetBtn.setPreferredSize(new Dimension(190, 24));
      xFormModGammaResetBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormModGammaResetBtn.setBounds(111, 172, 104, 24);
      panel_1.add(xFormModGammaResetBtn);

      xFormModGammaWholeFractalCBx = new JCheckBox("Whole fractal");
      xFormModGammaWholeFractalCBx.setSelected(true);
      xFormModGammaWholeFractalCBx.setToolTipText("Check if Randomize/Reset should apply to the whole fractal rather than only to the selected transform");
      xFormModGammaWholeFractalCBx.setBounds(218, 175, 104, 18);
      panel_1.add(xFormModGammaWholeFractalCBx);
    }
    return tinaTransformationsTabbedPane;
  }

  /**
   * This method initializes tinaAffineTransformationPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaAffineTransformationPanel() {
    if (tinaAffineTransformationPanel == null) {
      affineC21Lbl = new JLabel();
      affineC21Lbl.setName("affineC21Lbl");
      affineC21Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC21Lbl.setText("O2");
      affineC21Lbl.setLocation(new Point(216, 30));
      affineC21Lbl.setSize(new Dimension(20, 22));
      affineC21Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC21Lbl.setPreferredSize(new Dimension(24, 22));
      affineC20Lbl = new JLabel();
      affineC20Lbl.setName("affineC20Lbl");
      affineC20Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC20Lbl.setText("O1");
      affineC20Lbl.setLocation(new Point(216, 6));
      affineC20Lbl.setSize(new Dimension(20, 22));
      affineC20Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC20Lbl.setPreferredSize(new Dimension(24, 22));
      affineC11Lbl = new JLabel();
      affineC11Lbl.setName("affineC11Lbl");
      affineC11Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC11Lbl.setText("Y2");
      affineC11Lbl.setLocation(new Point(108, 30));
      affineC11Lbl.setSize(new Dimension(20, 22));
      affineC11Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC11Lbl.setPreferredSize(new Dimension(24, 22));
      affineC10Lbl = new JLabel();
      affineC10Lbl.setName("affineC10Lbl");
      affineC10Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC10Lbl.setText("Y1");
      affineC10Lbl.setLocation(new Point(108, 6));
      affineC10Lbl.setSize(new Dimension(20, 22));
      affineC10Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC10Lbl.setPreferredSize(new Dimension(24, 22));
      affineC01Lbl = new JLabel();
      affineC01Lbl.setName("affineC01Lbl");
      affineC01Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC01Lbl.setText("X2");
      affineC01Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC01Lbl.setLocation(new Point(0, 30));
      affineC01Lbl.setSize(new Dimension(20, 22));
      affineC01Lbl.setPreferredSize(new Dimension(24, 22));
      affineC00Lbl = new JLabel();
      affineC00Lbl.setName("affineC00Lbl");
      affineC00Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC00Lbl.setText("X1");
      affineC00Lbl.setLocation(new Point(0, 6));
      affineC00Lbl.setSize(new Dimension(20, 22));
      affineC00Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC00Lbl.setPreferredSize(new Dimension(24, 22));
      tinaAffineTransformationPanel = new JPanel();
      tinaAffineTransformationPanel.setLayout(null);
      tinaAffineTransformationPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaAffineTransformationPanel.add(affineC00Lbl, null);
      tinaAffineTransformationPanel.add(getAffineC00REd(), null);
      tinaAffineTransformationPanel.add(affineC01Lbl, null);
      tinaAffineTransformationPanel.add(getAffineC01REd(), null);
      tinaAffineTransformationPanel.add(affineC10Lbl, null);
      tinaAffineTransformationPanel.add(affineC11Lbl, null);
      tinaAffineTransformationPanel.add(getAffineC10REd(), null);
      tinaAffineTransformationPanel.add(getAffineC11REd(), null);
      tinaAffineTransformationPanel.add(affineC20Lbl, null);
      tinaAffineTransformationPanel.add(affineC21Lbl, null);
      tinaAffineTransformationPanel.add(getAffineC20REd(), null);
      tinaAffineTransformationPanel.add(getAffineC21REd(), null);
      tinaAffineTransformationPanel.add(getAffineRotateLeftButton(), null);
      tinaAffineTransformationPanel.add(getAffineRotateRightButton(), null);
      tinaAffineTransformationPanel.add(getAffineEnlargeButton(), null);
      tinaAffineTransformationPanel.add(getAffineShrinkButton(), null);
      tinaAffineTransformationPanel.add(getAffineRotateAmountREd(), null);
      tinaAffineTransformationPanel.add(getAffineScaleAmountREd(), null);
      tinaAffineTransformationPanel.add(getAffineMoveUpButton(), null);
      tinaAffineTransformationPanel.add(getAffineMoveDownButton(), null);
      tinaAffineTransformationPanel.add(getAffineMoveLeftButton(), null);
      tinaAffineTransformationPanel.add(getAffineMoveRightButton(), null);
      tinaAffineTransformationPanel.add(getAffineMoveVertAmountREd(), null);
      tinaAffineTransformationPanel.add(getAffineEditPostTransformButton(), null);
      tinaAffineTransformationPanel.add(getAffineResetTransformButton(), null);
      tinaAffineTransformationPanel.add(getAffineFlipHorizontalButton(), null);
      tinaAffineTransformationPanel.add(getAffineFlipVerticalButton(), null);
      tinaAffineTransformationPanel.add(getAffineScaleXButton(), null);
      tinaAffineTransformationPanel.add(getAffineScaleYButton(), null);

      affinePreserveZButton = new JToggleButton();
      affinePreserveZButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.affinePreserveZButton_clicked();
        }
      });
      affinePreserveZButton.setToolTipText("Preserve the Z-coordinate (applies only if 2D- and 3D-variations are mixed)");
      affinePreserveZButton.setText("Preserve Z");
      affinePreserveZButton.setSize(new Dimension(138, 24));
      affinePreserveZButton.setPreferredSize(new Dimension(136, 24));
      affinePreserveZButton.setLocation(new Point(4, 181));
      affinePreserveZButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affinePreserveZButton.setBounds(218, 155, 104, 24);
      tinaAffineTransformationPanel.add(affinePreserveZButton);

      affineRotateEditMotionCurveBtn = new JButton();
      affineRotateEditMotionCurveBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editRotateMotionCurve(e);
        }
      });
      affineRotateEditMotionCurveBtn.setToolTipText("Create/edit a motion curve");
      affineRotateEditMotionCurveBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/curve-money2.png")));
      affineRotateEditMotionCurveBtn.setText("");
      affineRotateEditMotionCurveBtn.setSize(new Dimension(70, 24));
      affineRotateEditMotionCurveBtn.setPreferredSize(new Dimension(55, 24));
      affineRotateEditMotionCurveBtn.setLocation(new Point(0, 57));
      affineRotateEditMotionCurveBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      affineRotateEditMotionCurveBtn.setBounds(0, 57, 22, 24);
      tinaAffineTransformationPanel.add(affineRotateEditMotionCurveBtn);
      tinaAffineTransformationPanel.add(getAffineScaleEditMotionCurveBtn());
    }
    return tinaAffineTransformationPanel;
  }

  /**
   * This method initializes tinaVariationPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaVariationPanel() {
    if (tinaVariationPanel == null) {
      nonlinearParams1Lbl = new JLabel();
      nonlinearParams1Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams1Lbl.setText("Params");
      nonlinearParams1Lbl.setSize(new Dimension(50, 22));
      nonlinearParams1Lbl.setLocation(new Point(14, 26));
      nonlinearParams1Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar1Lbl = new JLabel();
      nonlinearVar1Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar1Lbl.setText("Variation 1");
      nonlinearVar1Lbl.setSize(new Dimension(60, 22));
      nonlinearVar1Lbl.setLocation(new Point(4, 2));
      nonlinearVar1Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaVariationPanel = new JPanel();
      tinaVariationPanel.setLayout(new BorderLayout());
      tinaVariationPanel.add(getNonlinearScrollPane(), BorderLayout.CENTER);
    }
    return tinaVariationPanel;
  }

  /**
   * This method initializes tinaModifiedWeightsPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaModifiedWeightsPanel() {
    if (tinaModifiedWeightsPanel == null) {
      tinaModifiedWeightsPanel = new JPanel();
      tinaModifiedWeightsPanel.setLayout(new BorderLayout());
      tinaModifiedWeightsPanel.add(getRelWeightsEastPanel(), BorderLayout.EAST);
      tinaModifiedWeightsPanel.add(getRelWeightsScrollPane(), BorderLayout.CENTER);
    }
    return tinaModifiedWeightsPanel;
  }

  /**
   * This method initializes tinaTransformationColorPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaTransformationColorPanel() {
    if (tinaTransformationColorPanel == null) {
      xFormDrawModeLbl = new JLabel();
      xFormDrawModeLbl.setPreferredSize(new Dimension(64, 22));
      xFormDrawModeLbl.setText("Draw mode");
      xFormDrawModeLbl.setSize(new Dimension(64, 22));
      xFormDrawModeLbl.setLocation(new Point(6, 99));
      xFormDrawModeLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormOpacityLbl = new JLabel();
      xFormOpacityLbl.setPreferredSize(new Dimension(64, 22));
      xFormOpacityLbl.setText("Opacity");
      xFormOpacityLbl.setSize(new Dimension(64, 22));
      xFormOpacityLbl.setLocation(new Point(6, 73));
      xFormOpacityLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormSymmetryLbl = new JLabel();
      xFormSymmetryLbl.setPreferredSize(new Dimension(64, 22));
      xFormSymmetryLbl.setText("Color speed");
      xFormSymmetryLbl.setSize(new Dimension(64, 22));
      xFormSymmetryLbl.setLocation(new Point(6, 47));
      xFormSymmetryLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormColorLbl = new JLabel();
      xFormColorLbl.setPreferredSize(new Dimension(64, 22));
      xFormColorLbl.setText("Color");
      xFormColorLbl.setSize(new Dimension(64, 22));
      xFormColorLbl.setLocation(new Point(6, 21));
      xFormColorLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaTransformationColorPanel = new JPanel();
      tinaTransformationColorPanel.setLayout(null);
      tinaTransformationColorPanel.add(xFormColorLbl, null);
      tinaTransformationColorPanel.add(getXFormColorREd(), null);
      tinaTransformationColorPanel.add(getXFormColorSlider(), null);
      tinaTransformationColorPanel.add(xFormSymmetryLbl, null);
      tinaTransformationColorPanel.add(getXFormSymmetryREd(), null);
      tinaTransformationColorPanel.add(getXFormSymmetrySlider(), null);
      tinaTransformationColorPanel.add(xFormOpacityLbl, null);
      tinaTransformationColorPanel.add(getXFormOpacityREd(), null);
      tinaTransformationColorPanel.add(getXFormOpacitySlider(), null);
      tinaTransformationColorPanel.add(xFormDrawModeLbl, null);
      tinaTransformationColorPanel.add(getXFormDrawModeCmb(), null);

      tinaColorChooserPaletteImgPanel = new JPanel();
      tinaColorChooserPaletteImgPanel.setBounds(125, 10, 172, 10);
      tinaTransformationColorPanel.add(tinaColorChooserPaletteImgPanel);
      tinaColorChooserPaletteImgPanel.setLayout(new BorderLayout(0, 0));
    }
    return tinaTransformationColorPanel;
  }

  /**
   * This method initializes tinaPaletteRandomPointsREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteRandomPointsREd() {
    if (tinaPaletteRandomPointsREd == null) {
      tinaPaletteRandomPointsREd = new JTextField();
      tinaPaletteRandomPointsREd.setBounds(259, 29, 80, 22);
      tinaPaletteRandomPointsREd.setPreferredSize(new Dimension(55, 22));
      tinaPaletteRandomPointsREd.setText("11");
      tinaPaletteRandomPointsREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteRandomPointsREd;
  }

  /**
   * This method initializes tinaRandomPaletteButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaRandomPaletteButton() {
    if (tinaRandomPaletteButton == null) {
      tinaRandomPaletteButton = new JButton();
      tinaRandomPaletteButton.setBounds(5, 3, 148, 46);
      tinaRandomPaletteButton.setText("Random Gradient");
      tinaRandomPaletteButton.setPreferredSize(new Dimension(190, 24));
      tinaRandomPaletteButton.setActionCommand("Random Gradient");
      tinaRandomPaletteButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaRandomPaletteButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.randomPaletteButton_actionPerformed(e);
        }
      });
    }
    return tinaRandomPaletteButton;
  }

  /**
   * This method initializes distributeColorsButton 
   *  
   * @return javax.swing.JButton  
   */
  private JButton getDistributeColorsButton() {
    if (distributeColorsButton == null) {
      distributeColorsButton = new JButton();
      distributeColorsButton.setBounds(174, 58, 148, 24);
      distributeColorsButton.setText("Distribute colors");
      distributeColorsButton.setFont(new Font("Dialog", Font.BOLD, 10));
      distributeColorsButton.setPreferredSize(new Dimension(190, 24));
      distributeColorsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.distributeColorsBtn_clicked();
        }
      });
    }
    return distributeColorsButton;
  }

  /**
   * This method initializes tinaPaletteImgPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaPaletteImgPanel() {
    if (tinaPaletteImgPanel == null) {
      tinaPaletteImgPanel = new JPanel();
      tinaPaletteImgPanel.setLayout(new BorderLayout());
      tinaPaletteImgPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      tinaPaletteImgPanel.add(getPanel_73(), BorderLayout.NORTH);
    }
    return tinaPaletteImgPanel;
  }

  /**
   * This method initializes tinaPaletteSubNorthPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaPaletteSubNorthPanel() {
    if (tinaPaletteSubNorthPanel == null) {
      tinaPaletteSubNorthPanel = new JPanel();
      tinaPaletteSubNorthPanel.setLayout(new BorderLayout());
      tinaPaletteSubNorthPanel.setPreferredSize(new Dimension(330, 0));
      tinaPaletteSubNorthPanel.add(getTinaPaletteImgPanel(), BorderLayout.CENTER);
      tinaPaletteSubNorthPanel.add(getTinaPaletteSubSouthPanel(), BorderLayout.SOUTH);
    }
    return tinaPaletteSubNorthPanel;
  }

  /**
   * This method initializes tinaPaletteSubSouthPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaPaletteSubSouthPanel() {
    if (tinaPaletteSubSouthPanel == null) {
      tinaPaletteSubSouthPanel = new JPanel();
      tinaPaletteSubSouthPanel.setPreferredSize(new Dimension(0, 88));
      tinaPaletteSubSouthPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      tinaPaletteSubSouthPanel.setLayout(null);
      tinaPaletteSubSouthPanel.add(getRandomizeColorsButton());
      tinaPaletteSubSouthPanel.add(getDistributeColorsButton());
      tinaPaletteSubSouthPanel.add(getTinaPaletteShiftSlider());
      tinaPaletteSubSouthPanel.add(getTinaPaletteShiftREd());
      tinaPaletteShiftLbl = new JLabel();
      tinaPaletteShiftLbl.setBounds(116, 7, 29, 22);
      tinaPaletteSubSouthPanel.add(tinaPaletteShiftLbl);
      tinaPaletteShiftLbl.setText("Shift");
      tinaPaletteShiftLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteShiftLbl.setPreferredSize(new Dimension(64, 22));
    }
    return tinaPaletteSubSouthPanel;
  }

  /**
   * This method initializes tinaPaletteSubCenterPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaPaletteSubCenterPanel() {
    if (tinaPaletteSubCenterPanel == null) {
      tinaPaletteSubCenterPanel = new JPanel();
      tinaPaletteSubCenterPanel.setLayout(new BorderLayout());
      tinaPaletteSubCenterPanel.add(getTinaPaletteSubTabbedPane(), BorderLayout.CENTER);
    }
    return tinaPaletteSubCenterPanel;
  }

  /**
   * This method initializes tinaPaletteSubTabbedPane 
   *  
   * @return javax.swing.JTabbedPane  
   */
  private JTabbedPane getTinaPaletteSubTabbedPane() {
    if (tinaPaletteSubTabbedPane == null) {
      tinaPaletteSubTabbedPane = new JTabbedPane();
      tinaPaletteSubTabbedPane.setTabPlacement(JTabbedPane.LEFT);
      tinaPaletteSubTabbedPane.setAutoscrolls(true);
      tinaPaletteSubTabbedPane.setToolTipText("");
      tinaPaletteSubTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSubTabbedPane.addTab("Gradient Library", null, getGradientLibraryPanel(), null);
      tinaPaletteSubTabbedPane.addTab("Create new", null, getTinaPaletteCreatePanel(), "Create or import a gradient");
      tinaPaletteSubTabbedPane.addTab("Modify gradient", null, getTinaPaletteTransformPanel(), "Apply general modifications to the gradient");
      tinaPaletteSubTabbedPane.addTab("Balancing", null, getTinaPaletteBalancingPanel(), "Apply common color balancing options to the gradient");
    }
    return tinaPaletteSubTabbedPane;
  }

  /**
   * This method initializes tinaPaletteCreatePanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaPaletteCreatePanel() {
    if (tinaPaletteCreatePanel == null) {
      tinaPaletteCreatePanel = new JPanel();
      tinaPaletteCreatePanel.setLayout(null);
      tinaPaletteCreatePanel.add(getTinaRandomPaletteButton());
      tinaPaletteCreatePanel.add(getTinaGrabPaletteFromFlameButton());

      JButton tinaGrabPaletteFromImageButton = new JButton();
      tinaGrabPaletteFromImageButton.setBounds(5, 89, 148, 24);
      tinaGrabPaletteFromImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.grabPaletteFromImageButton_actionPerformed(e);
        }
      });
      tinaGrabPaletteFromImageButton.setToolTipText("Create a gradient from an image by choosing the most important 256 colors");
      tinaGrabPaletteFromImageButton.setText("Create from image");
      tinaGrabPaletteFromImageButton.setPreferredSize(new Dimension(190, 24));
      tinaGrabPaletteFromImageButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteCreatePanel.add(tinaGrabPaletteFromImageButton);
      tinaPaletteRandomPointsLbl = new JLabel();
      tinaPaletteRandomPointsLbl.setBounds(163, 34, 100, 14);
      tinaPaletteCreatePanel.add(tinaPaletteRandomPointsLbl);
      tinaPaletteRandomPointsLbl.setText("Random points");
      tinaPaletteRandomPointsLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteRandomPointsLbl.setPreferredSize(new Dimension(100, 22));
      tinaPaletteCreatePanel.add(getTinaPaletteRandomPointsREd());
      tinaPaletteCreatePanel.add(getCreatePaletteScrollPane());

      tinaPaletteFadeColorsCBx = new JCheckBox("Fade colors");
      tinaPaletteFadeColorsCBx.setSelected(true);
      tinaPaletteFadeColorsCBx.setToolTipText("Create a gradient were each key-frame-color is faded into the next one");
      tinaPaletteFadeColorsCBx.setBounds(199, 56, 140, 18);
      tinaPaletteCreatePanel.add(tinaPaletteFadeColorsCBx);

      JLabel lblGradientGenerator = new JLabel();
      lblGradientGenerator.setText("Gradient generator");
      lblGradientGenerator.setPreferredSize(new Dimension(100, 22));
      lblGradientGenerator.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGradientGenerator.setAlignmentX(1.0f);
      lblGradientGenerator.setBounds(163, 8, 100, 14);
      tinaPaletteCreatePanel.add(lblGradientGenerator);

      tinaPaletteRandomGeneratorCmb = new JComboBox();
      tinaPaletteRandomGeneratorCmb.setToolTipText("Random-Symmetry-Geneator");
      tinaPaletteRandomGeneratorCmb.setPreferredSize(new Dimension(50, 24));
      tinaPaletteRandomGeneratorCmb.setMinimumSize(new Dimension(100, 24));
      tinaPaletteRandomGeneratorCmb.setMaximumSize(new Dimension(32767, 24));
      tinaPaletteRandomGeneratorCmb.setMaximumRowCount(32);
      tinaPaletteRandomGeneratorCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteRandomGeneratorCmb.setBounds(259, 3, 80, 24);
      tinaPaletteCreatePanel.add(tinaPaletteRandomGeneratorCmb);
    }
    return tinaPaletteCreatePanel;
  }

  /**
   * This method initializes tinaPaletteBalancingPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaPaletteBalancingPanel() {
    if (tinaPaletteBalancingPanel == null) {
      tinaPaletteBrightnessLbl = new JLabel();
      tinaPaletteBrightnessLbl.setText("Brightness");
      tinaPaletteBrightnessLbl.setSize(new Dimension(56, 22));
      tinaPaletteBrightnessLbl.setLocation(new Point(334, 84));
      tinaPaletteBrightnessLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBrightnessLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteGammaLbl = new JLabel();
      tinaPaletteGammaLbl.setText("Gamma");
      tinaPaletteGammaLbl.setSize(new Dimension(56, 22));
      tinaPaletteGammaLbl.setLocation(new Point(334, 58));
      tinaPaletteGammaLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteGammaLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteContrastLbl = new JLabel();
      tinaPaletteContrastLbl.setText("Contrast");
      tinaPaletteContrastLbl.setSize(new Dimension(56, 22));
      tinaPaletteContrastLbl.setLocation(new Point(334, 32));
      tinaPaletteContrastLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteContrastLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteSaturationLbl = new JLabel();
      tinaPaletteSaturationLbl.setText("Saturation");
      tinaPaletteSaturationLbl.setSize(new Dimension(56, 22));
      tinaPaletteSaturationLbl.setLocation(new Point(334, 6));
      tinaPaletteSaturationLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSaturationLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteHueLbl = new JLabel();
      tinaPaletteHueLbl.setText("Hue");
      tinaPaletteHueLbl.setSize(new Dimension(56, 22));
      tinaPaletteHueLbl.setLocation(new Point(6, 84));
      tinaPaletteHueLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteHueLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteBlueLbl = new JLabel();
      tinaPaletteBlueLbl.setText("Blue");
      tinaPaletteBlueLbl.setSize(new Dimension(56, 22));
      tinaPaletteBlueLbl.setLocation(new Point(6, 58));
      tinaPaletteBlueLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBlueLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteGreenLbl = new JLabel();
      tinaPaletteGreenLbl.setText("Green");
      tinaPaletteGreenLbl.setSize(new Dimension(56, 22));
      tinaPaletteGreenLbl.setLocation(new Point(6, 32));
      tinaPaletteGreenLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteGreenLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteRedLbl = new JLabel();
      tinaPaletteRedLbl.setText("Red");
      tinaPaletteRedLbl.setSize(new Dimension(56, 22));
      tinaPaletteRedLbl.setLocation(new Point(6, 6));
      tinaPaletteRedLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteRedLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteBalancingPanel = new JPanel();
      tinaPaletteBalancingPanel.setLayout(null);
      tinaPaletteBalancingPanel.add(tinaPaletteRedLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteGreenLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteBlueLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteHueLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteSaturationLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteContrastLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteGammaLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteBrightnessLbl, null);
      tinaPaletteBalancingPanel.add(getTinaPaletteRedREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteGreenREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteBlueREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteHueREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteSaturationREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteContrastREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteGammaREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteBrightnessREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteRedSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteGreenSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteBlueSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteHueSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteSaturationSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteContrastSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteGammaSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteBrightnessSlider(), null);
      tinaPaletteBalancingPanel.add(getGradientApplyBalancingBtn());
    }
    return tinaPaletteBalancingPanel;
  }

  /**
   * This method initializes tinaPaletteShiftREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteShiftREd() {
    if (tinaPaletteShiftREd == null) {
      tinaPaletteShiftREd = new JWFNumberField();
      tinaPaletteShiftREd.setMinValue(-255.0);
      tinaPaletteShiftREd.setEditable(true);
      tinaPaletteShiftREd.setOnlyIntegers(true);
      tinaPaletteShiftREd.setMaxValue(255.0);
      tinaPaletteShiftREd.setHasMinValue(true);
      tinaPaletteShiftREd.setHasMaxValue(true);
      tinaPaletteShiftREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteShiftREd.isMouseAdjusting() || tinaPaletteShiftREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteShiftSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteShiftREd_changed();
        }
      });
      tinaPaletteShiftREd.setBounds(145, 6, 71, 24);
      tinaPaletteShiftREd.setPreferredSize(new Dimension(56, 24));
      tinaPaletteShiftREd.setText("0");
      tinaPaletteShiftREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteShiftREd;
  }

  /**
   * This method initializes tinaPaletteRedREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteRedREd() {
    if (tinaPaletteRedREd == null) {
      tinaPaletteRedREd = new JWFNumberField();
      tinaPaletteRedREd.setMinValue(-255.0);
      tinaPaletteRedREd.setOnlyIntegers(true);
      tinaPaletteRedREd.setMaxValue(255.0);
      tinaPaletteRedREd.setHasMinValue(true);
      tinaPaletteRedREd.setHasMaxValue(true);
      tinaPaletteRedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteRedREd.isMouseAdjusting() || tinaPaletteRedREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteRedSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteRedREd_changed();
        }
      });
      tinaPaletteRedREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteRedREd.setText("0");
      tinaPaletteRedREd.setSize(new Dimension(56, 24));
      tinaPaletteRedREd.setLocation(new Point(62, 6));
      tinaPaletteRedREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteRedREd;
  }

  /**
   * This method initializes tinaPaletteGreenREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteGreenREd() {
    if (tinaPaletteGreenREd == null) {
      tinaPaletteGreenREd = new JWFNumberField();
      tinaPaletteGreenREd.setMinValue(-255.0);
      tinaPaletteGreenREd.setOnlyIntegers(true);
      tinaPaletteGreenREd.setMaxValue(255.0);
      tinaPaletteGreenREd.setHasMinValue(true);
      tinaPaletteGreenREd.setHasMaxValue(true);
      tinaPaletteGreenREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteGreenREd.isMouseAdjusting() || tinaPaletteGreenREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteGreenSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteGreenREd_changed();
        }
      });
      tinaPaletteGreenREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteGreenREd.setText("0");
      tinaPaletteGreenREd.setSize(new Dimension(56, 24));
      tinaPaletteGreenREd.setLocation(new Point(62, 32));
      tinaPaletteGreenREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteGreenREd;
  }

  /**
   * This method initializes tinaPaletteBlueREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteBlueREd() {
    if (tinaPaletteBlueREd == null) {
      tinaPaletteBlueREd = new JWFNumberField();
      tinaPaletteBlueREd.setOnlyIntegers(true);
      tinaPaletteBlueREd.setMinValue(-255.0);
      tinaPaletteBlueREd.setMaxValue(255.0);
      tinaPaletteBlueREd.setHasMinValue(true);
      tinaPaletteBlueREd.setHasMaxValue(true);
      tinaPaletteBlueREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteBlueREd.isMouseAdjusting() || tinaPaletteBlueREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteBlueSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteBlueREd_changed();
        }
      });
      tinaPaletteBlueREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteBlueREd.setText("0");
      tinaPaletteBlueREd.setSize(new Dimension(56, 24));
      tinaPaletteBlueREd.setLocation(new Point(62, 58));
      tinaPaletteBlueREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteBlueREd;
  }

  /**
   * This method initializes tinaPaletteHueREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteHueREd() {
    if (tinaPaletteHueREd == null) {
      tinaPaletteHueREd = new JWFNumberField();
      tinaPaletteHueREd.setOnlyIntegers(true);
      tinaPaletteHueREd.setMinValue(-255.0);
      tinaPaletteHueREd.setMaxValue(255.0);
      tinaPaletteHueREd.setHasMinValue(true);
      tinaPaletteHueREd.setHasMaxValue(true);
      tinaPaletteHueREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteHueREd.isMouseAdjusting() || tinaPaletteHueREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteHueSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteHueREd_changed();
        }
      });
      tinaPaletteHueREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteHueREd.setText("0");
      tinaPaletteHueREd.setSize(new Dimension(56, 24));
      tinaPaletteHueREd.setLocation(new Point(62, 84));
      tinaPaletteHueREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteHueREd;
  }

  /**
   * This method initializes tinaPaletteSaturationREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteSaturationREd() {
    if (tinaPaletteSaturationREd == null) {
      tinaPaletteSaturationREd = new JWFNumberField();
      tinaPaletteSaturationREd.setOnlyIntegers(true);
      tinaPaletteSaturationREd.setMinValue(-255.0);
      tinaPaletteSaturationREd.setMaxValue(255.0);
      tinaPaletteSaturationREd.setHasMinValue(true);
      tinaPaletteSaturationREd.setHasMaxValue(true);
      tinaPaletteSaturationREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteSaturationREd.isMouseAdjusting() || tinaPaletteSaturationREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteSaturationSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteSaturationREd_changed();
        }
      });
      tinaPaletteSaturationREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteSaturationREd.setText("0");
      tinaPaletteSaturationREd.setSize(new Dimension(56, 24));
      tinaPaletteSaturationREd.setLocation(new Point(390, 6));
      tinaPaletteSaturationREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteSaturationREd;
  }

  /**
   * This method initializes tinaPaletteContrastREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteContrastREd() {
    if (tinaPaletteContrastREd == null) {
      tinaPaletteContrastREd = new JWFNumberField();
      tinaPaletteContrastREd.setOnlyIntegers(true);
      tinaPaletteContrastREd.setHasMinValue(true);
      tinaPaletteContrastREd.setHasMaxValue(true);
      tinaPaletteContrastREd.setMinValue(-255.0);
      tinaPaletteContrastREd.setMaxValue(255.0);
      tinaPaletteContrastREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteContrastREd.isMouseAdjusting() || tinaPaletteContrastREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteContrastSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteContrastREd_changed();
        }
      });
      tinaPaletteContrastREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteContrastREd.setText("0");
      tinaPaletteContrastREd.setSize(new Dimension(56, 24));
      tinaPaletteContrastREd.setLocation(new Point(390, 32));
      tinaPaletteContrastREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteContrastREd;
  }

  /**
   * This method initializes tinaPaletteGammaREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteGammaREd() {
    if (tinaPaletteGammaREd == null) {
      tinaPaletteGammaREd = new JWFNumberField();
      tinaPaletteGammaREd.setOnlyIntegers(true);
      tinaPaletteGammaREd.setHasMinValue(true);
      tinaPaletteGammaREd.setHasMaxValue(true);
      tinaPaletteGammaREd.setMinValue(-255.0);
      tinaPaletteGammaREd.setMaxValue(255.0);
      tinaPaletteGammaREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteGammaREd.isMouseAdjusting() || tinaPaletteGammaREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteGammaSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteGammaREd_changed();
        }
      });
      tinaPaletteGammaREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteGammaREd.setText("0");
      tinaPaletteGammaREd.setSize(new Dimension(56, 24));
      tinaPaletteGammaREd.setLocation(new Point(390, 58));
      tinaPaletteGammaREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteGammaREd;
  }

  /**
   * This method initializes tinaPaletteBrightnessREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaPaletteBrightnessREd() {
    if (tinaPaletteBrightnessREd == null) {
      tinaPaletteBrightnessREd = new JWFNumberField();
      tinaPaletteBrightnessREd.setOnlyIntegers(true);
      tinaPaletteBrightnessREd.setMinValue(-255.0);
      tinaPaletteBrightnessREd.setMaxValue(255.0);
      tinaPaletteBrightnessREd.setHasMinValue(true);
      tinaPaletteBrightnessREd.setHasMaxValue(true);
      tinaPaletteBrightnessREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteBrightnessREd.isMouseAdjusting() || tinaPaletteBrightnessREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteBrightnessSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteBrightnessREd_changed();
        }
      });
      tinaPaletteBrightnessREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteBrightnessREd.setText("0");
      tinaPaletteBrightnessREd.setSize(new Dimension(56, 24));
      tinaPaletteBrightnessREd.setLocation(new Point(390, 84));
      tinaPaletteBrightnessREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteBrightnessREd;
  }

  /**
   * This method initializes tinaPaletteShiftSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteShiftSlider() {
    if (tinaPaletteShiftSlider == null) {
      tinaPaletteShiftSlider = new JSlider();
      tinaPaletteShiftSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteShiftSlider.setBounds(6, 32, 316, 22);
      tinaPaletteShiftSlider.setMaximum(25500);
      tinaPaletteShiftSlider.setMinimum(-25500);
      tinaPaletteShiftSlider.setValue(0);
      tinaPaletteShiftSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteShiftSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteShiftSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteShiftSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteShiftSlider;
  }

  /**
   * This method initializes tinaPaletteRedSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteRedSlider() {
    if (tinaPaletteRedSlider == null) {
      tinaPaletteRedSlider = new JSlider();
      tinaPaletteRedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteRedSlider.setMaximum(255);
      tinaPaletteRedSlider.setMinimum(-255);
      tinaPaletteRedSlider.setValue(0);
      tinaPaletteRedSlider.setSize(new Dimension(204, 22));
      tinaPaletteRedSlider.setLocation(new Point(118, 6));
      tinaPaletteRedSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteRedSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteRedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteRedSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteRedSlider;
  }

  /**
   * This method initializes tinaPaletteGreenSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteGreenSlider() {
    if (tinaPaletteGreenSlider == null) {
      tinaPaletteGreenSlider = new JSlider();
      tinaPaletteGreenSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteGreenSlider.setMaximum(255);
      tinaPaletteGreenSlider.setMinimum(-255);
      tinaPaletteGreenSlider.setValue(0);
      tinaPaletteGreenSlider.setSize(new Dimension(204, 22));
      tinaPaletteGreenSlider.setLocation(new Point(118, 32));
      tinaPaletteGreenSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteGreenSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteGreenSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteGreenSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteGreenSlider;
  }

  /**
   * This method initializes tinaPaletteBlueSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteBlueSlider() {
    if (tinaPaletteBlueSlider == null) {
      tinaPaletteBlueSlider = new JSlider();
      tinaPaletteBlueSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteBlueSlider.setMaximum(255);
      tinaPaletteBlueSlider.setMinimum(-255);
      tinaPaletteBlueSlider.setValue(0);
      tinaPaletteBlueSlider.setSize(new Dimension(204, 22));
      tinaPaletteBlueSlider.setLocation(new Point(118, 58));
      tinaPaletteBlueSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBlueSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteBlueSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteBlueSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteBlueSlider;
  }

  /**
   * This method initializes tinaPaletteHueSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteHueSlider() {
    if (tinaPaletteHueSlider == null) {
      tinaPaletteHueSlider = new JSlider();
      tinaPaletteHueSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteHueSlider.setMaximum(255);
      tinaPaletteHueSlider.setMinimum(-255);
      tinaPaletteHueSlider.setValue(0);
      tinaPaletteHueSlider.setSize(new Dimension(204, 22));
      tinaPaletteHueSlider.setLocation(new Point(118, 84));
      tinaPaletteHueSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteHueSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteHueSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteHueSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteHueSlider;
  }

  /**
   * This method initializes tinaPaletteSaturationSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteSaturationSlider() {
    if (tinaPaletteSaturationSlider == null) {
      tinaPaletteSaturationSlider = new JSlider();
      tinaPaletteSaturationSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteSaturationSlider.setMaximum(255);
      tinaPaletteSaturationSlider.setMinimum(-255);
      tinaPaletteSaturationSlider.setValue(0);
      tinaPaletteSaturationSlider.setSize(new Dimension(204, 22));
      tinaPaletteSaturationSlider.setLocation(new Point(446, 6));
      tinaPaletteSaturationSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSaturationSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteSaturationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteSaturationSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteSaturationSlider;
  }

  /**
   * This method initializes tinaPaletteContrastSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteContrastSlider() {
    if (tinaPaletteContrastSlider == null) {
      tinaPaletteContrastSlider = new JSlider();
      tinaPaletteContrastSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteContrastSlider.setMaximum(255);
      tinaPaletteContrastSlider.setMinimum(-255);
      tinaPaletteContrastSlider.setValue(0);
      tinaPaletteContrastSlider.setSize(new Dimension(204, 22));
      tinaPaletteContrastSlider.setLocation(new Point(446, 32));
      tinaPaletteContrastSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteContrastSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteContrastSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteContrastSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteContrastSlider;
  }

  /**
   * This method initializes tinaPaletteGammaSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteGammaSlider() {
    if (tinaPaletteGammaSlider == null) {
      tinaPaletteGammaSlider = new JSlider();
      tinaPaletteGammaSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteGammaSlider.setMaximum(255);
      tinaPaletteGammaSlider.setMinimum(-255);
      tinaPaletteGammaSlider.setValue(0);
      tinaPaletteGammaSlider.setSize(new Dimension(204, 22));
      tinaPaletteGammaSlider.setLocation(new Point(446, 58));
      tinaPaletteGammaSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteGammaSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteGammaSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteGammaSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteGammaSlider;
  }

  /**
   * This method initializes tinaPaletteBrightnessSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaPaletteBrightnessSlider() {
    if (tinaPaletteBrightnessSlider == null) {
      tinaPaletteBrightnessSlider = new JSlider();
      tinaPaletteBrightnessSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteBrightnessSlider.setMaximum(255);
      tinaPaletteBrightnessSlider.setMinimum(-255);
      tinaPaletteBrightnessSlider.setValue(0);
      tinaPaletteBrightnessSlider.setSize(new Dimension(204, 22));
      tinaPaletteBrightnessSlider.setLocation(new Point(446, 84));
      tinaPaletteBrightnessSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBrightnessSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteBrightnessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteBrightnessSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteBrightnessSlider;
  }

  public TinaController createController(StandardErrorHandler pErrorHandler, Prefs pPrefs) {
    nonlinearControlsRows = new TinaNonlinearControlsRow[12];
    nonlinearControlsRows[0] = new TinaNonlinearControlsRow(getNonlinearVar1Cmb(), getNonlinearParams1Cmb(), getNonlinearVar1REd(),
        getNonlinearParams1REd(), getNonlinearParams1LeftButton());
    nonlinearControlsRows[1] = new TinaNonlinearControlsRow(getNonlinearVar2Cmb(), getNonlinearParams2Cmb(), getNonlinearVar2REd(),
        getNonlinearParams2REd(), getNonlinearParams2LeftButton());
    nonlinearControlsRows[2] = new TinaNonlinearControlsRow(getNonlinearVar3Cmb(), getNonlinearParams3Cmb(), getNonlinearVar3REd(),
        getNonlinearParams3REd(), getNonlinearParams3LeftButton());
    nonlinearControlsRows[3] = new TinaNonlinearControlsRow(getNonlinearVar4Cmb(), getNonlinearParams4Cmb(), getNonlinearVar4REd(),
        getNonlinearParams4REd(), getNonlinearParams4LeftButton());
    nonlinearControlsRows[4] = new TinaNonlinearControlsRow(getNonlinearVar5Cmb(), getNonlinearParams5Cmb(), getNonlinearVar5REd(),
        getNonlinearParams5REd(), getNonlinearParams5LeftButton());
    nonlinearControlsRows[5] = new TinaNonlinearControlsRow(getNonlinearVar6Cmb(), getNonlinearParams6Cmb(), getNonlinearVar6REd(),
        getNonlinearParams6REd(), getNonlinearParams6LeftButton());
    nonlinearControlsRows[6] = new TinaNonlinearControlsRow(getNonlinearVar7Cmb(), getNonlinearParams7Cmb(), getNonlinearVar7REd(),
        getNonlinearParams7REd(), getNonlinearParams7LeftButton());
    nonlinearControlsRows[7] = new TinaNonlinearControlsRow(getNonlinearVar8Cmb(), getNonlinearParams8Cmb(), getNonlinearVar8REd(),
        getNonlinearParams8REd(), getNonlinearParams8LeftButton());
    nonlinearControlsRows[8] = new TinaNonlinearControlsRow(getNonlinearVar9Cmb(), getNonlinearParams9Cmb(), getNonlinearVar9REd(),
        getNonlinearParams9REd(), getNonlinearParams9LeftButton());
    nonlinearControlsRows[9] = new TinaNonlinearControlsRow(getNonlinearVar10Cmb(), getNonlinearParams10Cmb(), getNonlinearVar10REd(),
        getNonlinearParams10REd(), getNonlinearParams10LeftButton());
    nonlinearControlsRows[10] = new TinaNonlinearControlsRow(getNonlinearVar11Cmb(), getNonlinearParams11Cmb(), getNonlinearVar11REd(),
        getNonlinearParams11REd(), getNonlinearParams11LeftButton());
    nonlinearControlsRows[11] = new TinaNonlinearControlsRow(getNonlinearVar12Cmb(), getNonlinearParams12Cmb(), getNonlinearVar12REd(),
        getNonlinearParams12REd(), getNonlinearParams12LeftButton());

    initFilterKernelCmb(getTinaFilterKernelCmb());
    initPostSymmetryTypeCmb(getPostSymmetryTypeCmb());
    initRandomGenCmb(getRandomStyleCmb());
    initRandomSymmetryCmb(getRandomSymmetryCmb());
    initRandomMovieGenCmb(getSwfAnimatorRandGenCmb());
    initStereo3dModeCmb(getStereo3dModeCmb());
    initStereo3dPreviewCmb(getStereo3dPreviewCmb());
    initStereo3dColorCmb(getStereo3dLeftEyeColorCmb(), Stereo3dColor.RED);
    initStereo3dColorCmb(getStereo3dRightEyeColorCmb(), Stereo3dColor.CYAN);
    initRandomGradientCmb(getRandomGradientCmb());
    initRandomGradientCmb(getTinaPaletteRandomGeneratorCmb());
    initPreFilterTypeCmb(getMeshGenPreFilter1Cmb());
    initPreFilterTypeCmb(getMeshGenPreFilter2Cmb());
    initDOFBlurShapeCmb(getDofDOFShapeCmb());

    TinaControllerParameter params = new TinaControllerParameter();

    params.setParams1(this, pErrorHandler, pPrefs, getCenterCenterPanel(), getTinaCameraRollREd(), getTinaCameraRollSlider(), getTinaCameraPitchREd(),
        getTinaCameraPitchSlider(), getTinaCameraYawREd(), getTinaCameraYawSlider(), getTinaCameraPerspectiveREd(), getTinaCameraPerspectiveSlider(),
        getTinaCameraCentreXREd(), getTinaCameraCentreXSlider(), getTinaCameraCentreYREd(),
        getTinaCameraCentreYSlider(), getTinaCameraZoomREd(), getTinaCameraZoomSlider(), getDofNewDOFCBx(),
        getDofFocusXREd(), getDofFocusXSlider(), getDofFocusYREd(), getDofFocusYSlider(), getDofFocusZREd(), getDofFocusZSlider(),
        getCamDimishZREd(), getCamDimishZSlider(),
        getDofDOFREd(), getDofDOFSlider(), getDofDOFAreaREd(), getDofDOFAreaSlider(), getDofDOFExponentREd(), getDofDOFExponentSlider(),
        getDofCamZREd(), getDofCamZSlider(), getTinaPixelsPerUnitREd(), getTinaPixelsPerUnitSlider(),
        getTinaBrightnessREd(), getTinaBrightnessSlider(), getTinaContrastREd(), getTinaContrastSlider(), getTinaGammaREd(), getTinaGammaSlider(),
        getTinaVibrancyREd(), getTinaVibrancySlider(), getTinaFilterRadiusREd(), getTinaFilterRadiusSlider(), getTinaFilterKernelCmb(),
        getTinaGammaThresholdREd(), getTinaGammaThresholdSlider(), getBgTransparencyCBx(), getTinaPaletteRandomPointsREd(), getTinaPaletteImgPanel(), getTinaCholorChooserPaletteImgPanel(), getTinaPaletteShiftREd(), getTinaPaletteShiftSlider(),
        getTinaPaletteRedREd(), getTinaPaletteRedSlider(), getTinaPaletteGreenREd(), getTinaPaletteGreenSlider(), getTinaPaletteBlueREd(),
        getTinaPaletteBlueSlider(), getTinaPaletteHueREd(), getTinaPaletteHueSlider(), getTinaPaletteSaturationREd(), getTinaPaletteSaturationSlider(),
        getTinaPaletteContrastREd(), getTinaPaletteContrastSlider(), getTinaPaletteGammaREd(), getTinaPaletteGammaSlider(), getTinaPaletteBrightnessREd(),
        getTinaPaletteBrightnessSlider(), getTinaPaletteSwapRGBREd(), getTinaPaletteSwapRGBSlider(),
        getTinaPaletteFrequencyREd(), getTinaPaletteFrequencySlider(), getTinaPaletteBlurREd(), getTinaPaletteBlurSlider(), getTinaPaletteInvertBtn(), getTinaPaletteReverseBtn(),
        getTinaTransformationsTable(), getAffineC00REd(),
        getAffineC01REd(), getAffineC10REd(), getAffineC11REd(), getAffineC20REd(), getAffineC21REd(), getAffineRotateAmountREd(), getAffineScaleAmountREd(),
        getAffineMoveHorizAmountREd(), getAffineRotateLeftButton(), getAffineRotateRightButton(), getAffineEnlargeButton(), getAffineShrinkButton(),
        getAffineMoveUpButton(), getAffineMoveLeftButton(), getAffineMoveRightButton(), getAffineMoveDownButton(), getTinaAddTransformationButton(),
        getTinaAddLinkedTransformationButton(),
        getTinaDuplicateTransformationButton(), getTinaDeleteTransformationButton(), getTinaAddFinalTransformationButton(), getRandomBatchPanel(),
        nonlinearControlsRows, getXFormColorREd(), getXFormColorSlider(), getXFormSymmetryREd(), getXFormSymmetrySlider(), getXFormOpacityREd(),
        getXFormOpacitySlider(), getXFormDrawModeCmb(), getRelWeightsTable(), getRelWeightsZeroButton(), getRelWeightsOneButton(), getRelWeightREd(),
        getMouseTransformMoveTrianglesButton(),
        getMouseTransformEditFocusPointButton(), getMouseTransformShearButton(), getMouseTransformViewButton(),
        getAffineEditPostTransformButton(), getAffineEditPostTransformSmallButton(),
        new MainProgressUpdater(this), getAffineResetTransformButton(), getCreatePaletteColorsTable(),
        getShadingCmb(), getShadingAmbientREd(), getShadingAmbientSlider(), getShadingDiffuseREd(), getShadingDiffuseSlider(),
        getShadingPhongREd(), getShadingPhongSlider(), getShadingPhongSizeREd(), getShadingPhongSizeSlider(), getShadingLightCmb(),
        getShadingLightXREd(), getShadingLightXSlider(), getShadingLightYREd(), getShadingLightYSlider(), getShadingLightZREd(),
        getShadingLightZSlider(), getShadingLightRedREd(), getShadingLightRedSlider(), getShadingLightGreenREd(), getShadingLightGreenSlider(),
        getShadingLightBlueREd(), getShadingLightBlueSlider(),
        getShadingDistanceColorRadiusREd(), getShadingDistanceColorRadiusSlider(), getShadingDistanceColorScaleREd(), getShadingDistanceColorScaleSlider(),
        getShadingDistanceColorExponentREd(), getShadingDistanceColorExponentSlider(), getShadingDistanceColorOffsetXREd(), getShadingDistanceColorOffsetXSlider(),
        getShadingDistanceColorOffsetYREd(), getShadingDistanceColorOffsetYSlider(), getShadingDistanceColorOffsetZREd(), getShadingDistanceColorOffsetZSlider(),
        getMouseTransformSlowButton(), getRenderBatchJobsTable(), getBatchPreviewRootPanel(),
        getBatchRenderJobProgressBar(), getBatchRenderTotalProgressBar(), new JobProgressUpdater(this),
        getBatchRenderAddFilesButton(), getBatchRenderFilesMoveDownButton(), getBatchRenderFilesMoveUpButton(),
        getBatchRenderFilesRemoveButton(), getBatchRenderFilesRemoveAllButton(), getBatchRenderStartButton(),
        getRootTabbedPane(), getAffineFlipHorizontalButton(), getAffineFlipVerticalButton(), getShadingBlurRadiusREd(), getShadingBlurRadiusSlider(), getShadingBlurFadeREd(),
        getShadingBlurFadeSlider(), getShadingBlurFallOffREd(), getShadingBlurFallOffSlider(),
        getAffineScaleXButton(), getAffineScaleYButton(), gradientLibraryThumbnailPnl, getHelpPane(),
        getFaqPane(), getToggleVariationsButton(), getToggleTransparencyButton(), getAffinePreserveZButton(), getQualityProfileCmb(), getResolutionProfileCmb(),
        getBatchQualityProfileCmb(), getBatchResolutionProfileCmb(), getInteractiveResolutionProfileCmb(),
        getSwfAnimatorResolutionProfileCmb(), getTinaRenderFlameButton(), getRenderMainButton(), getTinaAppendToMovieButton(),
        getTransformationWeightREd(), getUndoButton(), getRedoButton(),
        getXFormAntialiasAmountREd(), getXFormAntialiasAmountSlider(), getXFormAntialiasRadiusREd(), getXFormAntialiasRadiusSlider(),
        getRealtimeFlamePnl(), getRealtimeGraph1Pnl(), getDancingFlamesLoadSoundBtn(), getDancingFlamesAddFromClipboardBtn(),
        getDancingFlamesAddFromEditorBtn(), getDancingFlamesAddFromDiscBtn(), getDancingFlamesRandomCountIEd(), getDancingFlamesGenRandFlamesBtn(),
        getDancingFlamesRandomGenCmb(), getDancingFlamesPoolFlamePreviewPnl(), getDancingFlamesBorderSizeSlider(),
        getDancingFlamesFlameToEditorBtn(), getDancingFlamesDeleteFlameBtn(), getDancingFlamesFramesPerSecondIEd(), getDancingFlamesMorphFrameCountIEd(),
        getDancingFlamesStartShowBtn(), getDancingFlamesStopShowBtn(), getDancingFlamesDoRecordCBx(),
        getDancingFlamesFlameCmb(), getDancingFlamesDrawTrianglesCBx(),
        getDancingFlamesDrawFFTCBx(), getDancingFlamesDrawFPSCBx(), getDancingFlamesFlamePropertiesTree(),
        getDancingFlamesMotionPropertyPnl(), getDancingFlamesMotionTable(), getDancingFlamesAddMotionCmb(), getDancingFlamesAddMotionBtn(),
        getDancingFlamesDeleteMotionBtn());

    params.setParams2(getDancingFlamesLinkMotionBtn(), getDancingFlamesUnlinkMotionBtn(),
        getDancingFlamesCreateMotionsCmb(), getDancingFlamesClearMotionsBtn(), getDancingFlamesLoadProjectBtn(), getDancingFlamesSaveProjectBtn(),
        getDancingFlamesMotionLinksTable(), getShadingDistanceColorStyleREd(), getShadingDistanceColorStyleSlider(),
        getShadingDistanceColorCoordinateREd(), getShadingDistanceColorCoordinateSlider(), getShadingDistanceColorShiftREd(),
        getShadingDistanceColorShiftSlider(), getMutaGen01Pnl(), getMutaGen02Pnl(), getMutaGen03Pnl(), getMutaGen04Pnl(), getMutaGen05Pnl(),
        getMutaGen06Pnl(), getMutaGen07Pnl(), getMutaGen08Pnl(), getMutaGen09Pnl(), getMutaGen10Pnl(), getMutaGen11Pnl(), getMutaGen12Pnl(),
        getMutaGen13Pnl(), getMutaGen14Pnl(), getMutaGen15Pnl(), getMutaGen16Pnl(), getMutaGen17Pnl(), getMutaGen18Pnl(), getMutaGen19Pnl(),
        getMutaGen20Pnl(), getMutaGen21Pnl(), getMutaGen22Pnl(), getMutaGen23Pnl(), getMutaGen24Pnl(), getMutaGen25Pnl(),
        getMutaGenLoadFlameFromEditorBtn(), getMutaGenLoadFlameFromFileBtn(), getMutaGenProgressBar(), getMutaGenAmountREd(), getMutaGenHorizontalTrend1Cmb(),
        getMutaGenHorizontalTrend2Cmb(), getMutaGenVerticalTrend1Cmb(), getMutaGenVerticalTrend2Cmb(), getMutaGenBackBtn(), getMutaGenForwardBtn(),
        getMutaGenHintPane(), getMutaGenSaveFlameToEditorBtn(), getMutaGenSaveFlameToFileBtn(),
        getEditTransformCaptionBtn(), getEditFlameTitleBtn(), getSnapShotButton(), getBtnQsave(), getQuickMutationButton(), getTinaAppendToDancingFlamesButton(),
        getTinaAppendToMovieButton(), getMouseTransformSlowButton(), getToggleTransparencyButton(), getMouseTransformRotateTrianglesButton(),
        getMouseTransformScaleTrianglesButton(), getScriptTree(), getScriptDescriptionTextArea(), getScriptTextArea(), getCompileScriptButton(), getScriptSaveBtn(), getScriptRevertBtn(), getRescanScriptsBtn(),
        getNewScriptBtn(), getNewScriptFromFlameBtn(), getDeleteScriptBtn(), getScriptRenameBtn(), getDuplicateScriptBtn(), getScriptRunBtn(),
        getMouseTransformEditGradientButton(), getGradientLibTree(), getGradientLibraryRescanBtn(), getGradientLibraryNewFolderBtn(), getGradientLibraryRenameFolderBtn(),
        getGradientsList(), getBackgroundColorIndicatorBtn(), getRandomizeBtn(), getFlameBrowserTree(), getFlameBrowserImagesPanel(),
        getFlameBrowserRefreshBtn(), getFlameBrowserChangeFolderBtn(), getFlameBrowserToEditorBtn(), getFlameBrowserToBatchRendererBtn(), getFlameBrowserDeleteBtn(),
        getFlameBrowserRenameBtn(), getTinaPaletteFadeColorsCBx(), getDancingFlamesReplaceFlameFromEditorBtn(), getDancingFlamesRenameFlameBtn(),
        getDancingFlamesRenameMotionBtn(), getDancingFlamesMutedCBx(),
        getLayerWeightEd(), getLayerAddBtn(), getLayerDuplicateBtn(), getLayerDeleteBtn(),
        getLayersTable(), getLayerVisibleBtn(), getLayerAppendBtn(), getLayerHideOthersBtn(), getLayerShowAllBtn(), getLayerPreviewBtn(),
        getKeyframesFrameField(), getKeyframesFrameSlider(), getKeyframesFrameCountField(), getMotionBlurLengthField(), getMotionBlurLengthSlider(),
        getMotionBlurTimeStepField(), getMotionBlurTimeStepSlider(), getMotionBlurDecayField(), getMotionBlurDecaySlider(),
        getMotionCurveEditModeButton(), getFrameSliderPanel(), getKeyframesFrameLbl(), getKeyframesFrameCountLbl(),
        getMotionBlurPanel(), getAffineMoveVertAmountREd(),
        getPostSymmetryTypeCmb(), getPostSymmetryDistanceREd(), getPostSymmetryDistanceSlider(), getPostSymmetryRotationREd(), getPostSymmetryRotationSlider(),
        getPostSymmetryOrderREd(), getPostSymmetryOrderSlider(), getPostSymmetryCentreXREd(), getPostSymmetryCentreXSlider(), getPostSymmetryCentreYREd(),
        getPostSymmetryCentreYSlider(), getStereo3dModeCmb(), getStereo3dAngleREd(), getStereo3dAngleSlider(), getStereo3dEyeDistREd(),
        getStereo3dEyeDistSlider(), getStereo3dLeftEyeColorCmb(), getStereo3dRightEyeColorCmb(), getStereo3dInterpolatedImageCountREd(),
        getStereo3dInterpolatedImageCountSlider(), getStereo3dPreviewCmb(), getStereo3dFocalOffsetREd(), getStereo3dFocalOffsetSlider(),
        getStereo3dSwapSidesCBx(), getTinaCameraCamPosXREd(), getTinaCameraCamPosXSlider(), getTinaCameraCamPosYREd(), getTinaCameraCamPosYSlider(),
        getTinaCameraCamPosZREd(), getTinaCameraCamPosZSlider(), getTinaSaturationREd(), getTinaSaturationSlider(), getToggleDrawGridButton(),
        getEditorFractalBrightnessSlider(), getMouseTransformEditTriangleViewButton(), getTinaPaletteRandomGeneratorCmb(), getToggleTriangleWithColorsButton(),
        getFlameBrowserCopyToBtn(), getFlameBrowserMoveToBtn(), getAffineRotateEditMotionCurveBtn(), getAffineScaleEditMotionCurveBtn(),
        getTriangleStyleCmb(), getXFormModGammaREd(), getXFormModGammaSlider(), getXFormModGammaSpeedREd(), getXFormModGammaSpeedSlider(),
        getXFormModContrastREd(), getXFormModContrastSlider(), getXFormModContrastSpeedREd(), getXFormModContrastSpeedSlider(),
        getXFormModSaturationREd(), getXFormModSaturationSlider(), getXFormModSaturationSpeedREd(), getXFormModSaturationSpeedSlider());

    params.setParams3(getMeshGenFromEditorBtn(), getMeshGenFromClipboardBtn(), getMeshGenLoadFlameBtn(), getMeshGenSliceCountREd(),
        getMeshGenSlicesPerRenderREd(), getMeshGenRenderWidthREd(), getMeshGenRenderHeightREd(), getMeshGenRenderQualityREd(),
        getMeshGenProgressbar(), getMeshGenGenerateBtn(), getMeshGenTopViewRootPnl(), getMeshGenFrontViewRootPnl(), getMeshGenPerspectiveViewRootPnl(),
        getMeshGenHintPane(), getMeshGenCentreXREd(), getMeshGenCentreXSlider(), getMeshGenCentreYREd(), getMeshGenCentreYSlider(),
        getMeshGenZoomREd(), getMeshGenZoomSlider(), getMeshGenZMinREd(), getMeshGenZMinSlider(), getMeshGenZMaxREd(), getMeshGenZMaxSlider(),
        getMeshGenTopViewRenderBtn(), getMeshGenFrontViewRenderBtn(), getMeshGenPerspectiveViewRenderBtn(), getMeshGenTopViewToEditorBtn(),
        getFlameBrowserToMeshGenBtn(), getMeshGenLoadSequenceBtn(), getMeshGenSequenceWidthREd(), getMeshGenSequenceHeightREd(),
        getMeshGenSequenceSlicesREd(), getMeshGenSequenceDownSampleREd(), getMeshGenSequenceFilterRadiusREd(), getMeshGenGenerateMeshProgressbar(),
        getMeshGenGenerateMeshBtn(), getMeshGenSequenceFromRendererBtn(), getMeshGenSequenceThresholdREd(), getMeshGenSequenceLbl(),
        getMeshGenPreviewRootPanel(), getMeshGenAutoPreviewCBx(), getMeshGenPreviewImportLastGeneratedMeshBtn(), getMeshGenPreviewImportFromFileBtn(),
        getMeshGenClearPreviewBtn(), getMeshGenPreviewPositionXREd(), getMeshGenPreviewPositionYREd(), getMeshGenPreviewSizeREd(),
        getMeshGenPreviewScaleZREd(), getMeshGenPreviewRotateAlphaREd(), getMeshGenPreviewRotateBetaREd(),
        getMeshGenPreviewPointsREd(), getMeshGenPreviewPolygonsREd(), getMeshGenRefreshPreviewBtn(), getApophysisHintsPane(),
        getMeshGenPreviewSunflowExportBtn(), getMeshGenThicknessREd(), getChannelMixerResetBtn(), getChannelMixerModeCmb(),
        getChannelMixerRedRedRootPanel(), getChannelMixerRedGreenRootPanel(), getChannelMixerRedBlueRootPanel(), getChannelMixerGreenRedRootPanel(),
        getChannelMixerGreenGreenRootPanel(), getChannelMixerGreenBlueRootPanel(), getChannelMixerBlueRedRootPanel(), getChannelMixerBlueGreenRootPanel(),
        getChannelMixerBlueBlueRootPanel(), getMeshGenThicknessSamplesREd(), getMeshGenPreFilter1Cmb(), getMeshGenPreFilter2Cmb(), getMeshGenImageStepREd(),
        getMotionCurvePlayPreviewButton(), getDofDOFShapeCmb(), getDofDOFScaleREd(), getDofDOFScaleSlider(), getDofDOFAngleREd(), getDofDOFAngleSlider(),
        getDofDOFFadeREd(), getDofDOFFadeSlider(), getDofDOFParam1REd(), getDofDOFParam1Slider(), getDofDOFParam1Lbl(), getDofDOFParam2REd(),
        getDofDOFParam2Slider(), getDofDOFParam2Lbl(), getDofDOFParam3REd(), getDofDOFParam3Slider(), getDofDOFParam3Lbl(), getDofDOFParam4REd(),
        getDofDOFParam4Slider(), getDofDOFParam4Lbl(), getDofDOFParam5REd(), getDofDOFParam5Slider(), getDofDOFParam5Lbl(), getDofDOFParam6REd(),
        getDofDOFParam6Slider(), getDofDOFParam6Lbl(), getBatchRenderOverrideCBx(), getBatchRenderShowImageBtn(), getBokehBtn());

    tinaController = new TinaController(params);

    VariationControlsDelegate[] variationControlsDelegates = new VariationControlsDelegate[12];
    for (int i = 0; i < variationControlsDelegates.length; i++) {
      variationControlsDelegates[i] = new VariationControlsDelegate(tinaController, tinaController.getData(), getRootTabbedPane(), i);
    }
    tinaController.setVariationControlsDelegates(variationControlsDelegates);

    tinaController.refreshing = tinaController.cmbRefreshing = tinaController.gridRefreshing = true;
    try {
      for (TinaNonlinearControlsRow row : nonlinearControlsRows) {
        row.initControls();
      }
      getXFormDrawModeCmb().removeAllItems();
      getXFormDrawModeCmb().addItem(DrawMode.NORMAL);
      getXFormDrawModeCmb().addItem(DrawMode.OPAQUE);
      getXFormDrawModeCmb().addItem(DrawMode.HIDDEN);

      getShadingCmb().removeAllItems();
      getShadingCmb().addItem(Shading.FLAT);
      getShadingCmb().addItem(Shading.PSEUDO3D);
      getShadingCmb().addItem(Shading.BLUR);
      getShadingCmb().addItem(Shading.DISTANCE_COLOR);

      getShadingLightCmb().removeAllItems();
      getShadingLightCmb().addItem(String.valueOf("1"));
      getShadingLightCmb().addItem(String.valueOf("2"));
      getShadingLightCmb().addItem(String.valueOf("3"));
      getShadingLightCmb().addItem(String.valueOf("4"));

      getChannelMixerModeCmb().removeAllItems();
      getChannelMixerModeCmb().addItem(ChannelMixerMode.OFF);
      getChannelMixerModeCmb().addItem(ChannelMixerMode.GAMMA);
      getChannelMixerModeCmb().addItem(ChannelMixerMode.RGB);
      getChannelMixerModeCmb().addItem(ChannelMixerMode.FULL);

      initTriangleStyleCmb(getTriangleStyleCmb(), pPrefs);
      initGlobalScriptCmb(getSwfAnimatorGlobalScript1Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript2Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript3Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript4Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript5Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript6Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript7Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript8Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript9Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript10Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript11Cmb());
      initGlobalScriptCmb(getSwfAnimatorGlobalScript12Cmb());

      initXFormScriptCmb(getSwfAnimatorXFormScript1Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript2Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript3Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript4Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript5Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript6Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript7Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript8Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript9Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript10Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript11Cmb());
      initXFormScriptCmb(getSwfAnimatorXFormScript12Cmb());

      tinaController.setInteractiveRendererCtrl(new TinaInteractiveRendererController(tinaController, pErrorHandler, pPrefs,
          getInteractiveLoadFlameButton(), getInteractiveLoadFlameFromClipboardButton(), getInteractiveNextButton(), getInteractiveStopButton(),
          getInteractiveFlameToClipboardButton(), getInteractiveSaveImageButton(),
          getInteractiveSaveFlameButton(), getInteractiveRandomStyleCmb(), getInteractiveCenterTopPanel(), getInteractiveStatsTextArea(),
          getInteractiveHalveSizeButton(), getInteractiveResolutionProfileCmb(), getInteractivePauseButton(), getInteractiveResumeButton()));
      tinaController.getInteractiveRendererCtrl().enableControls();

      JComboBox[] globalScriptCmbArray = {
          getSwfAnimatorGlobalScript1Cmb(), getSwfAnimatorGlobalScript2Cmb(), getSwfAnimatorGlobalScript3Cmb(),
          getSwfAnimatorGlobalScript4Cmb(), getSwfAnimatorGlobalScript5Cmb(), getSwfAnimatorGlobalScript6Cmb(),
          getSwfAnimatorGlobalScript7Cmb(), getSwfAnimatorGlobalScript8Cmb(), getSwfAnimatorGlobalScript9Cmb(),
          getSwfAnimatorGlobalScript10Cmb(), getSwfAnimatorGlobalScript11Cmb(), getSwfAnimatorGlobalScript12Cmb() };
      JWFNumberField[] globalScriptREdArray = {
          getSwfAnimatorGlobalScript1REd(), getSwfAnimatorGlobalScript2REd(), getSwfAnimatorGlobalScript3REd(),
          getSwfAnimatorGlobalScript4REd(), getSwfAnimatorGlobalScript5REd(), getSwfAnimatorGlobalScript6REd(),
          getSwfAnimatorGlobalScript7REd(), getSwfAnimatorGlobalScript8REd(), getSwfAnimatorGlobalScript9REd(),
          getSwfAnimatorGlobalScript10REd(), getSwfAnimatorGlobalScript11REd(), getSwfAnimatorGlobalScript12REd() };
      JComboBox[] xFormScriptCmbArray = {
          getSwfAnimatorXFormScript1Cmb(), getSwfAnimatorXFormScript2Cmb(), getSwfAnimatorXFormScript3Cmb(),
          getSwfAnimatorXFormScript4Cmb(), getSwfAnimatorXFormScript5Cmb(), getSwfAnimatorXFormScript6Cmb(),
          getSwfAnimatorXFormScript7Cmb(), getSwfAnimatorXFormScript8Cmb(), getSwfAnimatorXFormScript9Cmb(),
          getSwfAnimatorXFormScript10Cmb(), getSwfAnimatorXFormScript11Cmb(), getSwfAnimatorXFormScript12Cmb() };
      JWFNumberField[] xFormScriptREdArray = {
          getSwfAnimatorXFormScript1REd(), getSwfAnimatorXFormScript2REd(), getSwfAnimatorXFormScript3REd(),
          getSwfAnimatorXFormScript4REd(), getSwfAnimatorXFormScript5REd(), getSwfAnimatorXFormScript6REd(),
          getSwfAnimatorXFormScript7REd(), getSwfAnimatorXFormScript8REd(), getSwfAnimatorXFormScript9REd(),
          getSwfAnimatorXFormScript10REd(), getSwfAnimatorXFormScript11REd(), getSwfAnimatorXFormScript12REd() };

      tinaController.setSwfAnimatorCtrl(new TinaSWFAnimatorController(tinaController, pErrorHandler, pPrefs,
          globalScriptCmbArray, globalScriptREdArray,
          xFormScriptCmbArray, xFormScriptREdArray,
          getSwfAnimatorFramesREd(), getSwfAnimatorFramesPerSecondREd(),
          getSwfAnimatorGenerateButton(), getSwfAnimatorResolutionProfileCmb(),
          getSwfAnimatorLoadFlameFromMainButton(),
          getSwfAnimatorLoadFlameFromClipboardButton(), getSwfAnimatorLoadFlameButton(),
          getSwfAnimatorProgressBar(), getSwfAnimatorCancelButton(),
          new SWFAnimatorProgressUpdater(this), getSwfAnimatorPreviewRootPanel(),
          getSwfAnimatorFrameSlider(), getSwfAnimatorFrameREd(), getSwfAnimatorFlamesPanel(), getSwfAnimatorFlamesButtonGroup(),
          getSwfAnimatorMoveUpButton(), getSwfAnimatorMoveDownButton(), getSwfAnimatorRemoveFlameButton(),
          getSwfAnimatorRemoveAllFlamesButton(), getSwfAnimatorMovieFromClipboardButton(), getSwfAnimatorMovieFromDiscButton(),
          getSwfAnimatorMovieToClipboardButton(), getSwfAnimatorMovieToDiscButton(), getSwfAnimatorFrameToEditorBtn(),
          getSwfAnimatorPlayButton(), getSwfAnimatorMotionBlurLengthREd(),
          getSwfAnimatorMotionBlurTimeStepREd(), getRandomMoviePanel()));
      tinaController.getSwfAnimatorCtrl().enableControls();
      tinaController.getSwfAnimatorCtrl().refreshControls();
      getToggleTriangleWithColorsButton().setSelected(pPrefs.isTinaEditorControlsWithColor());
    }
    finally {
      tinaController.refreshing = tinaController.cmbRefreshing = tinaController.gridRefreshing = false;
    }
    return tinaController;
  }

  private void initXFormScriptCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(XFormScriptType.NONE);
    pCmb.addItem(XFormScriptType.ROTATE_FULL);
    pCmb.addItem(XFormScriptType.ROTATE_FIRST_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_2ND_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_3RD_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_4TH_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_5TH_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_LAST_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_FINAL_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_POST_FULL);
    pCmb.addItem(XFormScriptType.ROTATE_POST_FIRST_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_POST_2ND_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_POST_3RD_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_POST_4TH_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_POST_5TH_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_POST_LAST_XFORM);
    pCmb.addItem(XFormScriptType.ROTATE_POST_FINAL_XFORM);
    pCmb.setSelectedItem(XFormScriptType.NONE);
  }

  private void initDOFBlurShapeCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(DOFBlurShapeType.BUBBLE);
    pCmb.addItem(DOFBlurShapeType.RECT);
    pCmb.addItem(DOFBlurShapeType.SINEBLUR);
    pCmb.addItem(DOFBlurShapeType.STARBLUR);
    pCmb.addItem(DOFBlurShapeType.NBLUR);
    pCmb.addItem(DOFBlurShapeType.HEART);
    pCmb.setSelectedItem(DOFBlurShapeType.BUBBLE);
  }

  private void initGlobalScriptCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(GlobalScriptType.NONE);
    pCmb.addItem(GlobalScriptType.ROTATE_PITCH);
    pCmb.addItem(GlobalScriptType.ROTATE_ROLL);
    pCmb.addItem(GlobalScriptType.ROTATE_YAW);
    pCmb.addItem(GlobalScriptType.MOVE_CAM_X);
    pCmb.addItem(GlobalScriptType.MOVE_CAM_Y);
    pCmb.addItem(GlobalScriptType.MOVE_CAM_Z);
    pCmb.setSelectedItem(GlobalScriptType.NONE);
  }

  private void initTriangleStyleCmb(JComboBox pCmb, Prefs pPrefs) {
    pCmb.addItem(FlamePanelControlStyle.AXIS);
    pCmb.addItem(FlamePanelControlStyle.CROSSHAIR);
    pCmb.addItem(FlamePanelControlStyle.RECTANGLE);
    pCmb.addItem(FlamePanelControlStyle.TRIANGLE);
    pCmb.addItem(FlamePanelControlStyle.HIDDEN);
    pCmb.setSelectedItem(pPrefs.getTinaEditorControlsStyle());
  }

  private void initPreFilterTypeCmb(JComboBox pCmb) {
    pCmb.addItem(PreFilterType.NONE);
    pCmb.addItem(PreFilterType.GAUSS3X3);
    pCmb.addItem(PreFilterType.GAUSS5X5);
    pCmb.addItem(PreFilterType.DILATE3);
    pCmb.addItem(PreFilterType.DILATE5);
    pCmb.setSelectedItem(PreFilterType.NONE);
  }

  private void initStereo3dModeCmb(JComboBox pCmb) {
    pCmb.addItem(Stereo3dMode.NONE);
    pCmb.addItem(Stereo3dMode.ANAGLYPH);
    pCmb.addItem(Stereo3dMode.SIDE_BY_SIDE);
    pCmb.addItem(Stereo3dMode.INTERPOLATED_IMAGES);
    pCmb.setSelectedItem(Stereo3dMode.NONE);
  }

  private void initStereo3dPreviewCmb(JComboBox pCmb) {
    pCmb.addItem(Stereo3dPreview.NONE);
    pCmb.addItem(Stereo3dPreview.ANAGLYPH);
    pCmb.addItem(Stereo3dPreview.SIDE_BY_SIDE);
    pCmb.addItem(Stereo3dPreview.SIDE_BY_SIDE_FULL);
    pCmb.setSelectedItem(Stereo3dPreview.SIDE_BY_SIDE);
  }

  private void initStereo3dColorCmb(JComboBox pCmb, Stereo3dColor pSelected) {
    pCmb.addItem(Stereo3dColor.RED);
    pCmb.addItem(Stereo3dColor.CYAN);
    pCmb.setSelectedItem(pSelected);
  }

  private void initRandomGenCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    for (String name : RandomFlameGeneratorList.getNameList()) {
      pCmb.addItem(name);
    }
    pCmb.setSelectedItem(RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);
  }

  private void initRandomMovieGenCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    for (String name : RandomMovieGeneratorList.getNameList()) {
      pCmb.addItem(name);
    }
    pCmb.setSelectedItem(RandomMovieGeneratorList.DEFAULT_GENERATOR_NAME);
  }

  private void initRandomSymmetryCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    for (String name : RandomSymmetryGeneratorList.getNameList()) {
      pCmb.addItem(name);
    }
    pCmb.setSelectedItem(RandomSymmetryGeneratorList.DEFAULT_GENERATOR_NAME);
  }

  private void initRandomGradientCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    for (String name : RandomGradientGeneratorList.getNameList()) {
      pCmb.addItem(name);
    }
    pCmb.setSelectedItem(RandomGradientGeneratorList.DEFAULT_GENERATOR_NAME);
  }

  private void initPostSymmetryTypeCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(PostSymmetryType.NONE);
    pCmb.addItem(PostSymmetryType.X_AXIS);
    pCmb.addItem(PostSymmetryType.Y_AXIS);
    pCmb.addItem(PostSymmetryType.POINT);
  }

  private void initFilterKernelCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(FilterKernelType.BELL);
    pCmb.addItem(FilterKernelType.BLACKMAN);
    pCmb.addItem(FilterKernelType.BOX);
    pCmb.addItem(FilterKernelType.BSPLINE);
    pCmb.addItem(FilterKernelType.CATROM);
    pCmb.addItem(FilterKernelType.GAUSSIAN);
    pCmb.addItem(FilterKernelType.HAMMING);
    pCmb.addItem(FilterKernelType.HANNING);
    pCmb.addItem(FilterKernelType.HERMITE);
    pCmb.addItem(FilterKernelType.LANCZOS2);
    pCmb.addItem(FilterKernelType.LANCZOS3);
    pCmb.addItem(FilterKernelType.MITCHELL);
    pCmb.addItem(FilterKernelType.QUADRATIC);
    pCmb.addItem(FilterKernelType.TRIANGLE);
    pCmb.setSelectedItem(FilterKernelType.GAUSSIAN);
  }

  /**
   * This method initializes tinaAddFinalTransformationButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getTinaAddFinalTransformationButton() {
    if (tinaAddFinalTransformationButton == null) {
      tinaAddFinalTransformationButton = new JButton();
      tinaAddFinalTransformationButton.setActionCommand("Add Final");
      tinaAddFinalTransformationButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaAddFinalTransformationButton.setPreferredSize(new Dimension(90, 24));
      tinaAddFinalTransformationButton.setToolTipText("Add final transformation");
      tinaAddFinalTransformationButton.setText("Add Final");
      tinaAddFinalTransformationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.addFinalXForm();
        }
      });
    }
    return tinaAddFinalTransformationButton;
  }

  /**
   * This method initializes affineC00REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineC00REd() {
    if (affineC00REd == null) {
      affineC00REd = new JWFNumberField();
      affineC00REd.setLinkedLabelControlName("affineC00Lbl");
      affineC00REd.setValueStep(0.01);
      affineC00REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      affineC00REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC00REd_changed();
        }
      });
      affineC00REd.setPreferredSize(new Dimension(76, 24));
      affineC00REd.setText("");
      affineC00REd.setLocation(new Point(24, 6));
      affineC00REd.setSize(new Dimension(84, 24));
      affineC00REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC00REd;
  }

  /**
   * This method initializes affineC01REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineC01REd() {
    if (affineC01REd == null) {
      affineC01REd = new JWFNumberField();
      affineC01REd.setLinkedLabelControlName("affineC01Lbl");
      affineC01REd.setValueStep(0.01);
      affineC01REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      affineC01REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC01REd_changed();
        }
      });
      affineC01REd.setPreferredSize(new Dimension(76, 24));
      affineC01REd.setText("");
      affineC01REd.setLocation(new Point(24, 30));
      affineC01REd.setSize(new Dimension(84, 24));
      affineC01REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC01REd;
  }

  /**
   * This method initializes affineC10REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineC10REd() {
    if (affineC10REd == null) {
      affineC10REd = new JWFNumberField();
      affineC10REd.setLinkedLabelControlName("affineC10Lbl");
      affineC10REd.setName("affineC10Lbl");
      affineC10REd.setValueStep(0.01);
      affineC10REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      affineC10REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC10REd_changed();
        }
      });
      affineC10REd.setPreferredSize(new Dimension(76, 24));
      affineC10REd.setText("");
      affineC10REd.setLocation(new Point(132, 6));
      affineC10REd.setSize(new Dimension(84, 24));
      affineC10REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC10REd;
  }

  /**
   * This method initializes affineC11REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineC11REd() {
    if (affineC11REd == null) {
      affineC11REd = new JWFNumberField();
      affineC11REd.setLinkedLabelControlName("affineC11Lbl");
      affineC11REd.setValueStep(0.01);
      affineC11REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      affineC11REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC11REd_changed();
        }
      });
      affineC11REd.setPreferredSize(new Dimension(76, 24));
      affineC11REd.setText("");
      affineC11REd.setLocation(new Point(132, 30));
      affineC11REd.setSize(new Dimension(84, 24));
      affineC11REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC11REd;
  }

  /**
   * This method initializes affineC20REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineC20REd() {
    if (affineC20REd == null) {
      affineC20REd = new JWFNumberField();
      affineC20REd.setLinkedLabelControlName("affineC20Lbl");
      affineC20REd.setValueStep(0.01);
      affineC20REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      affineC20REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC20REd_changed();
        }
      });
      affineC20REd.setPreferredSize(new Dimension(76, 24));
      affineC20REd.setText("");
      affineC20REd.setLocation(new Point(240, 6));
      affineC20REd.setSize(new Dimension(84, 24));
      affineC20REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC20REd;
  }

  /**
   * This method initializes affineC21REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineC21REd() {
    if (affineC21REd == null) {
      affineC21REd = new JWFNumberField();
      affineC21REd.setLinkedLabelControlName("affineC21Lbl");
      affineC21REd.setValueStep(0.01);
      affineC21REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      affineC21REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC21REd_changed();
        }
      });
      affineC21REd.setPreferredSize(new Dimension(76, 24));
      affineC21REd.setText("");
      affineC21REd.setLocation(new Point(240, 30));
      affineC21REd.setSize(new Dimension(84, 24));
      affineC21REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC21REd;
  }

  /**
   * This method initializes affineRotateLeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineRotateLeftButton() {
    if (affineRotateLeftButton == null) {
      affineRotateLeftButton = new JButton();
      affineRotateLeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineRotateLeftButton.setPreferredSize(new Dimension(55, 24));
      affineRotateLeftButton.setSize(new Dimension(70, 24));
      affineRotateLeftButton.setLocation(new Point(0, 57));
      //      affineRotateLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/turnLeft.gif")));
      affineRotateLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/object-rotate-left-3.png")));
      affineRotateLeftButton.setToolTipText("Rotate triangle left");
      affineRotateLeftButton.setText("");
      affineRotateLeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_rotateLeft();
        }
      });
    }
    return affineRotateLeftButton;
  }

  /**
   * This method initializes affineRotateRightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineRotateRightButton() {
    if (affineRotateRightButton == null) {
      affineRotateRightButton = new JButton();
      affineRotateRightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineRotateRightButton.setPreferredSize(new Dimension(55, 24));
      affineRotateRightButton.setLocation(new Point(0, 102));
      affineRotateRightButton.setSize(new Dimension(70, 24));
      //      affineRotateRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/turnRight.gif")));
      affineRotateRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/object-rotate-right-3.png")));
      affineRotateRightButton.setToolTipText("Rotate triangle right");
      affineRotateRightButton.setText("");
      affineRotateRightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_rotateRight();
        }
      });
    }
    return affineRotateRightButton;
  }

  /**
   * This method initializes affineEnlargeButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineEnlargeButton() {
    if (affineEnlargeButton == null) {
      affineEnlargeButton = new JButton();
      affineEnlargeButton.setFont(new Font("Dialog", Font.BOLD, 8));
      affineEnlargeButton.setPreferredSize(new Dimension(55, 24));
      affineEnlargeButton.setLocation(new Point(92, 57));
      affineEnlargeButton.setSize(new Dimension(70, 24));
      affineEnlargeButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/arrow-out.png")));
      affineEnlargeButton.setToolTipText("Enlarge triangle");
      affineEnlargeButton.setText("");
      affineEnlargeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_enlarge();
        }
      });
    }
    return affineEnlargeButton;
  }

  /**
   * This method initializes affineShrinkButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineShrinkButton() {
    if (affineShrinkButton == null) {
      affineShrinkButton = new JButton();
      affineShrinkButton.setFont(new Font("Dialog", Font.BOLD, 8));
      affineShrinkButton.setPreferredSize(new Dimension(55, 24));
      affineShrinkButton.setLocation(new Point(92, 102));
      affineShrinkButton.setSize(new Dimension(70, 24));
      //      affineShrinkButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/shrink.gif")));
      affineShrinkButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/arrow-in.png")));
      affineShrinkButton.setToolTipText("Shrink triangle");
      affineShrinkButton.setText("");
      affineShrinkButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_shrink();
        }
      });
    }
    return affineShrinkButton;
  }

  /**
   * This method initializes affineRotateAmountREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineRotateAmountREd() {
    if (affineRotateAmountREd == null) {
      affineRotateAmountREd = new JWFNumberField();
      affineRotateAmountREd.setPreferredSize(new Dimension(56, 24));
      affineRotateAmountREd.setText("90");
      affineRotateAmountREd.setSize(new Dimension(70, 24));
      affineRotateAmountREd.setLocation(new Point(0, 80));
      affineRotateAmountREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineRotateAmountREd;
  }

  /**
   * This method initializes transformationsNorthPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTransformationsNorthPanel() {
    if (transformationsNorthPanel == null) {
      transformationsNorthPanel = new JPanel();
      transformationsNorthPanel.setLayout(new BorderLayout());
      transformationsNorthPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      transformationsNorthPanel.add(getTrnsformationsEastPanel(), BorderLayout.EAST);
      transformationsNorthPanel.add(getTinaTransformationsScrollPane(), BorderLayout.CENTER);
    }
    return transformationsNorthPanel;
  }

  /**
   * This method initializes trnsformationsEastPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTrnsformationsEastPanel() {
    if (trnsformationsEastPanel == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setVgap(3);
      flowLayout.setHgap(3);
      flowLayout.setAlignment(FlowLayout.LEFT);
      trnsformationsEastPanel = new JPanel();
      trnsformationsEastPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      trnsformationsEastPanel.setLayout(flowLayout);
      trnsformationsEastPanel.setPreferredSize(new Dimension(100, 0));

      JPanel panel_2 = new JPanel();
      FlowLayout flowLayout_2 = (FlowLayout) panel_2.getLayout();
      flowLayout_2.setAlignment(FlowLayout.LEFT);
      flowLayout_2.setVgap(0);
      flowLayout_2.setHgap(0);
      panel_2.setPreferredSize(new Dimension(90, 24));
      trnsformationsEastPanel.add(panel_2);
      panel_2.add(getTransformationWeightREd());

      JPanel panel_1 = new JPanel();
      FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
      flowLayout_1.setAlignment(FlowLayout.LEFT);
      flowLayout_1.setVgap(0);
      flowLayout_1.setHgap(0);
      panel_1.setPreferredSize(new Dimension(90, 24));
      trnsformationsEastPanel.add(panel_1);
      panel_1.add(getTinaAddTransformationButton());
      panel_1.add(getTinaAddLinkedTransformationButton());
      trnsformationsEastPanel.add(getPanel_19());
      trnsformationsEastPanel.add(getTinaDeleteTransformationButton(), null);
      trnsformationsEastPanel.add(getTinaAddFinalTransformationButton(), null);
    }
    return trnsformationsEastPanel;
  }

  /**
   * This method initializes transformationsSplitPane	
   * 	
   * @return javax.swing.JSplitPane	
   */
  private JSplitPane getTransformationsSplitPane() {
    if (transformationsSplitPane == null) {
      transformationsSplitPane = new JSplitPane();
      transformationsSplitPane.setDividerLocation(142);
      transformationsSplitPane.setFont(new Font("Dialog", Font.PLAIN, 10));
      transformationsSplitPane.setTopComponent(getTransformationsNorthPanel());
      transformationsSplitPane.setBottomComponent(getTinaTransformationsTabbedPane());
      transformationsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    }
    return transformationsSplitPane;
  }

  /**
   * This method initializes affineScaleAmountREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineScaleAmountREd() {
    if (affineScaleAmountREd == null) {
      affineScaleAmountREd = new JWFNumberField();
      affineScaleAmountREd.setPreferredSize(new Dimension(56, 24));
      affineScaleAmountREd.setText("105");
      affineScaleAmountREd.setSize(new Dimension(70, 24));
      affineScaleAmountREd.setLocation(new Point(92, 80));
      affineScaleAmountREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineScaleAmountREd;
  }

  /**
   * This method initializes affineMoveUpButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineMoveUpButton() {
    if (affineMoveUpButton == null) {
      affineMoveUpButton = new JButton();
      affineMoveUpButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineMoveUpButton.setPreferredSize(new Dimension(55, 24));
      affineMoveUpButton.setLocation(new Point(215, 57));
      affineMoveUpButton.setSize(new Dimension(70, 24));
      //      affineMoveUpButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveUp.gif")));
      affineMoveUpButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/arrow-up.png")));
      affineMoveUpButton.setToolTipText("Move triangle up");
      affineMoveUpButton.setText("");
      affineMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_moveUp(1.0);
        }
      });
    }
    return affineMoveUpButton;
  }

  /**
   * This method initializes affineMoveDownButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineMoveDownButton() {
    if (affineMoveDownButton == null) {
      affineMoveDownButton = new JButton();
      affineMoveDownButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineMoveDownButton.setPreferredSize(new Dimension(55, 24));
      affineMoveDownButton.setLocation(new Point(215, 102));
      affineMoveDownButton.setSize(new Dimension(70, 24));
      //      affineMoveDownButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveDown.gif")));
      affineMoveDownButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/arrow-down.png")));
      affineMoveDownButton.setToolTipText("Move triangle down");
      affineMoveDownButton.setText("");
      affineMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_moveDown(1.0);
        }
      });
    }
    return affineMoveDownButton;
  }

  /**
   * This method initializes affineMoveLeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineMoveLeftButton() {
    if (affineMoveLeftButton == null) {
      affineMoveLeftButton = new JButton();
      affineMoveLeftButton.setText("");
      affineMoveLeftButton.setPreferredSize(new Dimension(55, 24));
      affineMoveLeftButton.setLocation(new Point(176, 80));
      affineMoveLeftButton.setSize(new Dimension(42, 24));
      //      affineMoveLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      affineMoveLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/arrow-left.png")));
      affineMoveLeftButton.setToolTipText("Move triangle left");
      affineMoveLeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineMoveLeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_moveLeft(1.0);
        }
      });
    }
    return affineMoveLeftButton;
  }

  /**
   * This method initializes affineMoveRightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineMoveRightButton() {
    if (affineMoveRightButton == null) {
      affineMoveRightButton = new JButton();
      affineMoveRightButton.setText("");
      affineMoveRightButton.setLocation(new Point(282, 80));
      affineMoveRightButton.setSize(new Dimension(42, 24));
      affineMoveRightButton.setPreferredSize(new Dimension(55, 24));
      //      affineMoveRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      affineMoveRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/arrow-right.png")));
      affineMoveRightButton.setToolTipText("Move triangle right");
      affineMoveRightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineMoveRightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_moveRight(1.0);
        }
      });
    }
    return affineMoveRightButton;
  }

  /**
   * This method initializes affineMoveAmountREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getAffineMoveVertAmountREd() {
    if (affineMoveVertAmountREd == null) {
      affineMoveVertAmountREd = new JWFNumberField();
      affineMoveVertAmountREd.setPreferredSize(new Dimension(56, 24));
      affineMoveVertAmountREd.setText("0.5");
      affineMoveVertAmountREd.setSize(new Dimension(70, 24));
      affineMoveVertAmountREd.setLocation(new Point(215, 80));
      affineMoveVertAmountREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineMoveVertAmountREd;
  }

  /**
   * This method initializes randomBatchButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRandomBatchButton() {
    if (randomBatchButton == null) {
      randomBatchButton = new JButton();
      randomBatchButton.setToolTipText("Create a batch of random flames");
      randomBatchButton.setMaximumSize(new Dimension(32000, 46));
      randomBatchButton.setMinimumSize(new Dimension(100, 46));
      randomBatchButton.setFont(new Font("Dialog", Font.BOLD, 10));
      randomBatchButton.setText("Random batch");
      randomBatchButton.setPreferredSize(new Dimension(105, 46));
      randomBatchButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      randomBatchButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (tinaController.createRandomBatch(-1, (String) randomStyleCmb.getSelectedItem(), (String) randomSymmetryCmb.getSelectedItem(), (String) randomGradientCmb.getSelectedItem(), RandomBatchQuality.NORMAL)) {
            tinaController.importFromRandomBatch(0);
          }
        }
      });
    }
    return randomBatchButton;
  }

  /**
   * This method initializes nonlinearVar1Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar1Cmb() {
    if (nonlinearVar1Cmb == null) {
      nonlinearVar1Cmb = new JComboBox();
      nonlinearVar1Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar1Cmb.setSize(new Dimension(120, 24));
      nonlinearVar1Cmb.setLocation(new Point(66, 2));
      nonlinearVar1Cmb.setMaximumRowCount(22);
      nonlinearVar1Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar1Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(0);
        }
      });
    }
    return nonlinearVar1Cmb;
  }

  /**
   * This method initializes nonlinearVar1REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar1REd() {
    if (nonlinearVar1REd == null) {
      nonlinearVar1REd = new JWFNumberField();
      nonlinearVar1REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(0);
        }
      });
      nonlinearVar1REd.setValueStep(0.01);
      nonlinearVar1REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar1REd.isMouseAdjusting() || nonlinearVar1REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(0);
          }
        }
      });
      nonlinearVar1REd.setPreferredSize(new Dimension(56, 24));
      nonlinearVar1REd.setText("");
      nonlinearVar1REd.setSize(new Dimension(81, 24));
      nonlinearVar1REd.setLocation(new Point(188, 2));
      nonlinearVar1REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return nonlinearVar1REd;
  }

  /**
   * This method initializes nonlinearParams1Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams1Cmb() {
    if (nonlinearParams1Cmb == null) {
      nonlinearParams1Cmb = new JComboBox();
      nonlinearParams1Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams1Cmb.setSize(new Dimension(120, 24));
      nonlinearParams1Cmb.setLocation(new Point(66, 26));
      nonlinearParams1Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams1Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(0);
        }
      });
    }
    return nonlinearParams1Cmb;
  }

  /**
   * This method initializes nonlinearParams1REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams1REd() {
    if (nonlinearParams1REd == null) {
      nonlinearParams1REd = new JWFNumberField();
      nonlinearParams1REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(0);
        }
      });
      nonlinearParams1REd.setValueStep(0.05);
      nonlinearParams1REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams1REd.isMouseAdjusting() || nonlinearParams1REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(0);
          }
        }
      });
      nonlinearParams1REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams1REd.setText("");
      nonlinearParams1REd.setSize(new Dimension(81, 24));
      nonlinearParams1REd.setLocation(new Point(188, 26));
      nonlinearParams1REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return nonlinearParams1REd;
  }

  /**
   * This method initializes nonlinearVar1Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar1Panel() {
    if (nonlinearVar1Panel == null) {
      nonlinearVar1Panel = new JPanel();
      nonlinearVar1Panel.setLayout(null);
      nonlinearVar1Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar1Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar1Panel.add(nonlinearVar1Lbl, null);
      nonlinearVar1Panel.add(getNonlinearVar1Cmb(), null);
      nonlinearVar1Panel.add(getNonlinearVar1REd(), null);
      nonlinearVar1Panel.add(nonlinearParams1Lbl, null);
      nonlinearVar1Panel.add(getNonlinearParams1Cmb(), null);
      nonlinearVar1Panel.add(getNonlinearParams1REd(), null);
      nonlinearVar1Panel.add(getNonlinearParams1LeftButton(), null);

      JButton button = new JButton();
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (java.awt.Desktop.isDesktopSupported()) {
            try {
              java.awt.Desktop.getDesktop().browse(new URI("http://www.fractalgee.com/JFW_Guide.html"));
            }
            catch (Throwable ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      button.setToolTipText("A Visual Guide to J-Wildfire Variations (online resource, may not work on all systems)");
      button.setText("");
      button.setSize(new Dimension(22, 22));
      button.setPreferredSize(new Dimension(22, 22));
      button.setLocation(new Point(269, 26));
      button.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-4-modified.png")));
      button.setText("");
      button.setFont(new Font("Dialog", Font.BOLD, 10));
      button.setBounds(269, 2, 22, 22);
      nonlinearVar1Panel.add(button);
    }
    return nonlinearVar1Panel;
  }

  /**
   * This method initializes nonlinearParams1LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams1LeftButton() {
    if (nonlinearParams1LeftButton == null) {
      nonlinearParams1LeftButton = new JButton();
      nonlinearParams1LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams1LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams1LeftButton.setText("");
      nonlinearParams1LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams1LeftButton.setLocation(new Point(269, 26));
      nonlinearParams1LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams1LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(0);
        }
      });
    }
    return nonlinearParams1LeftButton;
  }

  /**
   * This method initializes nonlinearVar2Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar2Panel() {
    if (nonlinearVar2Panel == null) {
      nonlinearParams2Lbl = new JLabel();
      nonlinearParams2Lbl.setLocation(new Point(14, 26));
      nonlinearParams2Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams2Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams2Lbl.setText("Params");
      nonlinearParams2Lbl.setSize(new Dimension(50, 22));
      nonlinearVar2Lbl = new JLabel();
      nonlinearVar2Lbl.setLocation(new Point(4, 2));
      nonlinearVar2Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar2Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar2Lbl.setText("Variation 2");
      nonlinearVar2Lbl.setSize(new Dimension(60, 22));
      nonlinearVar2Panel = new JPanel();
      nonlinearVar2Panel.setLayout(null);
      nonlinearVar2Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar2Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar2Panel.add(nonlinearVar2Lbl, null);
      nonlinearVar2Panel.add(getNonlinearVar2Cmb(), null);
      nonlinearVar2Panel.add(getNonlinearVar2REd(), null);
      nonlinearVar2Panel.add(nonlinearParams2Lbl, null);
      nonlinearVar2Panel.add(getNonlinearParams2Cmb(), null);
      nonlinearVar2Panel.add(getNonlinearParams2REd(), null);
      nonlinearVar2Panel.add(getNonlinearParams2LeftButton(), null);
    }
    return nonlinearVar2Panel;
  }

  /**
   * This method initializes nonlinearVar2Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar2Cmb() {
    if (nonlinearVar2Cmb == null) {
      nonlinearVar2Cmb = new JComboBox();
      nonlinearVar2Cmb.setLocation(new Point(66, 2));
      nonlinearVar2Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar2Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar2Cmb.setMaximumRowCount(22);
      nonlinearVar2Cmb.setSize(new Dimension(120, 24));
      nonlinearVar2Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(1);
        }
      });
    }
    return nonlinearVar2Cmb;
  }

  /**
   * This method initializes nonlinearVar2REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar2REd() {
    if (nonlinearVar2REd == null) {
      nonlinearVar2REd = new JWFNumberField();
      nonlinearVar2REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(1);
        }
      });
      nonlinearVar2REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar2REd.isMouseAdjusting() || nonlinearVar2REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(1);
          }
        }
      });
      nonlinearVar2REd.setValueStep(0.01);
      nonlinearVar2REd.setLocation(new Point(188, 2));
      nonlinearVar2REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar2REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar2REd.setText("");
      nonlinearVar2REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar2REd;
  }

  /**
   * This method initializes nonlinearParams2Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams2Cmb() {
    if (nonlinearParams2Cmb == null) {
      nonlinearParams2Cmb = new JComboBox();
      nonlinearParams2Cmb.setLocation(new Point(66, 26));
      nonlinearParams2Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams2Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams2Cmb.setSize(new Dimension(120, 24));
      nonlinearParams2Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(1);
        }
      });
    }
    return nonlinearParams2Cmb;
  }

  /**
   * This method initializes nonlinearParams2REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams2REd() {
    if (nonlinearParams2REd == null) {
      nonlinearParams2REd = new JWFNumberField();
      nonlinearParams2REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(1);
        }
      });
      nonlinearParams2REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams2REd.isMouseAdjusting() || nonlinearParams2REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(1);
          }
        }
      });
      nonlinearParams2REd.setValueStep(0.05);
      nonlinearParams2REd.setLocation(new Point(188, 26));
      nonlinearParams2REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams2REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams2REd.setText("");
      nonlinearParams2REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams2REd;
  }

  /**
   * This method initializes nonlinearParams2LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams2LeftButton() {
    if (nonlinearParams2LeftButton == null) {
      nonlinearParams2LeftButton = new JButton();
      nonlinearParams2LeftButton.setLocation(new Point(269, 26));
      nonlinearParams2LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams2LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams2LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams2LeftButton.setText("");
      nonlinearParams2LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams2LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(1);
        }
      });
    }
    return nonlinearParams2LeftButton;
  }

  /**
   * This method initializes nonlinearVar3Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar3Panel() {
    if (nonlinearVar3Panel == null) {
      nonlinearParams3Lbl = new JLabel();
      nonlinearParams3Lbl.setLocation(new Point(14, 26));
      nonlinearParams3Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams3Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams3Lbl.setText("Params");
      nonlinearParams3Lbl.setSize(new Dimension(50, 22));
      nonlinearVar3Lbl = new JLabel();
      nonlinearVar3Lbl.setLocation(new Point(4, 2));
      nonlinearVar3Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar3Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar3Lbl.setText("Variation 3");
      nonlinearVar3Lbl.setSize(new Dimension(60, 22));
      nonlinearVar3Panel = new JPanel();
      nonlinearVar3Panel.setLayout(null);
      nonlinearVar3Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar3Panel.setName("");
      nonlinearVar3Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar3Panel.add(nonlinearVar3Lbl, null);
      nonlinearVar3Panel.add(getNonlinearVar3Cmb(), null);
      nonlinearVar3Panel.add(getNonlinearVar3REd(), null);
      nonlinearVar3Panel.add(nonlinearParams3Lbl, null);
      nonlinearVar3Panel.add(getNonlinearParams3Cmb(), null);
      nonlinearVar3Panel.add(getNonlinearParams3REd(), null);
      nonlinearVar3Panel.add(getNonlinearParams3LeftButton(), null);
    }
    return nonlinearVar3Panel;
  }

  /**
   * This method initializes nonlinearVar3Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar3Cmb() {
    if (nonlinearVar3Cmb == null) {
      nonlinearVar3Cmb = new JComboBox();
      nonlinearVar3Cmb.setLocation(new Point(66, 2));
      nonlinearVar3Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar3Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar3Cmb.setMaximumRowCount(22);
      nonlinearVar3Cmb.setSize(new Dimension(120, 24));
      nonlinearVar3Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(2);
        }
      });
    }
    return nonlinearVar3Cmb;
  }

  /**
   * This method initializes nonlinearVar3REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar3REd() {
    if (nonlinearVar3REd == null) {
      nonlinearVar3REd = new JWFNumberField();
      nonlinearVar3REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(2);
        }
      });
      nonlinearVar3REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar3REd.isMouseAdjusting() || nonlinearVar3REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(2);
          }
        }
      });
      nonlinearVar3REd.setValueStep(0.01);
      nonlinearVar3REd.setLocation(new Point(188, 2));
      nonlinearVar3REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar3REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar3REd.setText("");
      nonlinearVar3REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar3REd;
  }

  /**
   * This method initializes nonlinearParams3Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams3Cmb() {
    if (nonlinearParams3Cmb == null) {
      nonlinearParams3Cmb = new JComboBox();
      nonlinearParams3Cmb.setLocation(new Point(66, 26));
      nonlinearParams3Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams3Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams3Cmb.setSize(new Dimension(120, 24));
      nonlinearParams3Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(2);
        }
      });
    }
    return nonlinearParams3Cmb;
  }

  /**
   * This method initializes nonlinearParams3REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams3REd() {
    if (nonlinearParams3REd == null) {
      nonlinearParams3REd = new JWFNumberField();
      nonlinearParams3REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(2);
        }
      });
      nonlinearParams3REd.setValueStep(0.05);
      nonlinearParams3REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams3REd.isMouseAdjusting() || nonlinearParams3REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(2);
          }
        }
      });
      nonlinearParams3REd.setLocation(new Point(188, 26));
      nonlinearParams3REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams3REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams3REd.setText("");
      nonlinearParams3REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams3REd;
  }

  /**
   * This method initializes nonlinearParams3LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams3LeftButton() {
    if (nonlinearParams3LeftButton == null) {
      nonlinearParams3LeftButton = new JButton();
      nonlinearParams3LeftButton.setLocation(new Point(269, 26));
      nonlinearParams3LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams3LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams3LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams3LeftButton.setText("");
      nonlinearParams3LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams3LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(2);
        }
      });
    }
    return nonlinearParams3LeftButton;
  }

  /**
   * This method initializes xFormColorREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getXFormColorREd() {
    if (xFormColorREd == null) {
      xFormColorREd = new JWFNumberField();
      xFormColorREd.setValueStep(0.01);
      xFormColorREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormColorREd.isMouseAdjusting() || xFormColorREd.getMouseChangeCount() == 0) {
              if (!xFormColorSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormColorREd_changed();
          }
        }
      });
      xFormColorREd.setMaxValue(1.0);
      xFormColorREd.setHasMinValue(true);
      xFormColorREd.setHasMaxValue(true);
      xFormColorREd.setPreferredSize(new Dimension(55, 22));
      xFormColorREd.setText("");
      xFormColorREd.setSize(new Dimension(55, 22));
      xFormColorREd.setLocation(new Point(70, 21));
      xFormColorREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return xFormColorREd;
  }

  /**
   * This method initializes xFormColorSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getXFormColorSlider() {
    if (xFormColorSlider == null) {
      xFormColorSlider = new JSlider();
      xFormColorSlider.setPreferredSize(new Dimension(172, 22));
      xFormColorSlider.setMaximum(100);
      xFormColorSlider.setMinimum(0);
      xFormColorSlider.setValue(0);
      xFormColorSlider.setSize(new Dimension(195, 22));
      xFormColorSlider.setLocation(new Point(125, 21));
      xFormColorSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormColorSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormColorSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormColorSlider_changed();
        }
      });
    }
    return xFormColorSlider;
  }

  /**
   * This method initializes xFormSymmetryREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getXFormSymmetryREd() {
    if (xFormSymmetryREd == null) {
      xFormSymmetryREd = new JWFNumberField();
      xFormSymmetryREd.setValueStep(0.01);
      xFormSymmetryREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormSymmetryREd.isMouseAdjusting() || xFormSymmetryREd.getMouseChangeCount() == 0) {
              if (!xFormSymmetrySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormSymmetryREd_changed();
          }
        }
      });
      xFormSymmetryREd.setHasMinValue(true);
      xFormSymmetryREd.setHasMaxValue(true);
      xFormSymmetryREd.setMaxValue(1.0);
      xFormSymmetryREd.setMinValue(-1.0);
      xFormSymmetryREd.setPreferredSize(new Dimension(55, 22));
      xFormSymmetryREd.setText("");
      xFormSymmetryREd.setSize(new Dimension(55, 22));
      xFormSymmetryREd.setLocation(new Point(70, 47));
      xFormSymmetryREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return xFormSymmetryREd;
  }

  /**
   * This method initializes xFormSymmetrySlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getXFormSymmetrySlider() {
    if (xFormSymmetrySlider == null) {
      xFormSymmetrySlider = new JSlider();
      xFormSymmetrySlider.setPreferredSize(new Dimension(172, 22));
      xFormSymmetrySlider.setMaximum(100);
      xFormSymmetrySlider.setMinimum(-100);
      xFormSymmetrySlider.setValue(0);
      xFormSymmetrySlider.setLocation(new Point(125, 47));
      xFormSymmetrySlider.setSize(new Dimension(195, 22));
      xFormSymmetrySlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormSymmetrySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormSymmetrySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormSymmetrySlider_changed();
        }
      });
    }
    return xFormSymmetrySlider;
  }

  /**
   * This method initializes xFormOpacityREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getXFormOpacityREd() {
    if (xFormOpacityREd == null) {
      xFormOpacityREd = new JWFNumberField();
      xFormOpacityREd.setValueStep(0.05);
      xFormOpacityREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormOpacityREd.isMouseAdjusting() || xFormOpacityREd.getMouseChangeCount() == 0) {
              if (!xFormOpacitySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormOpacityREd_changed();
          }
        }
      });
      xFormOpacityREd.setHasMaxValue(true);
      xFormOpacityREd.setHasMinValue(true);
      xFormOpacityREd.setMaxValue(1.0);
      xFormOpacityREd.setPreferredSize(new Dimension(55, 22));
      xFormOpacityREd.setText("");
      xFormOpacityREd.setSize(new Dimension(55, 22));
      xFormOpacityREd.setLocation(new Point(70, 73));
      xFormOpacityREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return xFormOpacityREd;
  }

  /**
   * This method initializes xFormOpacitySlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getXFormOpacitySlider() {
    if (xFormOpacitySlider == null) {
      xFormOpacitySlider = new JSlider();
      xFormOpacitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormOpacitySlider.setPreferredSize(new Dimension(172, 22));
      xFormOpacitySlider.setMaximum(100);
      xFormOpacitySlider.setMinimum(0);
      xFormOpacitySlider.setValue(0);
      xFormOpacitySlider.setSize(new Dimension(195, 22));
      xFormOpacitySlider.setLocation(new Point(125, 73));
      xFormOpacitySlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormOpacitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormOpacitySlider_changed();
        }
      });
    }
    return xFormOpacitySlider;
  }

  /**
   * This method initializes xFormDrawModeCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getXFormDrawModeCmb() {
    if (xFormDrawModeCmb == null) {
      xFormDrawModeCmb = new JComboBox();
      xFormDrawModeCmb.setPreferredSize(new Dimension(120, 22));
      xFormDrawModeCmb.setSize(new Dimension(120, 22));
      xFormDrawModeCmb.setLocation(new Point(70, 99));
      xFormDrawModeCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormDrawModeCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.saveUndoPoint();
          tinaController.xFormDrawModeCmb_changed();
        }
      });
    }
    return xFormDrawModeCmb;
  }

  /**
   * This method initializes relWeightsEastPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getRelWeightsEastPanel() {
    if (relWeightsEastPanel == null) {
      relWeightsEastPanel = new JPanel();
      relWeightsEastPanel.setLayout(null);
      relWeightsEastPanel.setPreferredSize(new Dimension(91, 0));
      relWeightsEastPanel.add(getRelWeightsZeroButton(), null);
      relWeightsEastPanel.add(getRelWeightsOneButton(), null);

      relWeightREd = new JWFNumberField();
      relWeightREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!relWeightREd.isMouseAdjusting() || relWeightREd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.relWeightsREd_changed();
          }
        }
      });
      relWeightREd.setValueStep(0.05);
      relWeightREd.setText("");
      relWeightREd.setSize(new Dimension(81, 24));
      relWeightREd.setPreferredSize(new Dimension(81, 24));
      relWeightREd.setLocation(new Point(238, 6));
      relWeightREd.setHasMinValue(true);
      relWeightREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      relWeightREd.setBounds(4, 6, 81, 24);
      relWeightsEastPanel.add(relWeightREd);
    }
    return relWeightsEastPanel;
  }

  /**
   * This method initializes relWeightsLeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRelWeightsZeroButton() {
    if (relWeightsZeroButton == null) {
      relWeightsZeroButton = new JButton();
      relWeightsZeroButton.setToolTipText("Set the relative weight to 0");
      relWeightsZeroButton.setPreferredSize(new Dimension(22, 22));
      relWeightsZeroButton.setIcon(null);
      relWeightsZeroButton.setText("0");
      relWeightsZeroButton.setSize(new Dimension(36, 22));
      relWeightsZeroButton.setLocation(new Point(4, 37));
      relWeightsZeroButton.setFont(new Font("Dialog", Font.BOLD, 10));
      relWeightsZeroButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.saveUndoPoint();
          tinaController.relWeightsZeroButton_clicked();
        }
      });
    }
    return relWeightsZeroButton;
  }

  /**
   * This method initializes relWeightsRightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRelWeightsOneButton() {
    if (relWeightsOneButton == null) {
      relWeightsOneButton = new JButton();
      relWeightsOneButton.setToolTipText("Set the relative weight to 1");
      relWeightsOneButton.setPreferredSize(new Dimension(22, 22));
      relWeightsOneButton.setIcon(null);
      relWeightsOneButton.setText("1");
      relWeightsOneButton.setSize(new Dimension(36, 22));
      relWeightsOneButton.setLocation(new Point(49, 37));
      relWeightsOneButton.setFont(new Font("Dialog", Font.BOLD, 10));
      relWeightsOneButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.saveUndoPoint();
          tinaController.relWeightsOneButton_clicked();
        }
      });
    }
    return relWeightsOneButton;
  }

  /**
   * This method initializes relWeightsScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getRelWeightsScrollPane() {
    if (relWeightsScrollPane == null) {
      relWeightsScrollPane = new JScrollPane();
      relWeightsScrollPane.setViewportView(getRelWeightsTable());
    }
    return relWeightsScrollPane;
  }

  /**
   * This method initializes relWeightsTable	
   * 	
   * @return javax.swing.JTable	
   */
  private JTable getRelWeightsTable() {
    if (relWeightsTable == null) {
      relWeightsTable = new JTable();
      relWeightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      relWeightsTable.setFont(new Font("Dialog", Font.PLAIN, 10));
      relWeightsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            tinaController.relWeightsTableClicked();
          }
        }

      });
    }
    return relWeightsTable;
  }

  /**
   * This method initializes newFlameButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNewFlameButton() {
    if (newFlameButton == null) {
      newFlameButton = new JButton();
      newFlameButton.setMinimumSize(new Dimension(100, 52));
      newFlameButton.setMaximumSize(new Dimension(32000, 52));
      newFlameButton.setPreferredSize(new Dimension(125, 52));
      newFlameButton.setMnemonic(KeyEvent.VK_N);
      newFlameButton.setText("New from scratch");
      newFlameButton.setActionCommand("New from scratch");
      newFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      newFlameButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/document-new-7.png")));
      newFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.newFlameButton_clicked();
        }
      });
    }
    return newFlameButton;
  }

  /**
   * This method initializes tinaAnimatePanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTinaSWFAnimatorPanel() {
    if (tinaSWFAnimatorPanel == null) {
      tinaSWFAnimatorPanel = new JPanel();
      tinaSWFAnimatorPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(200, 10));
      panel_1.setMinimumSize(new Dimension(200, 10));
      tinaSWFAnimatorPanel.add(panel_1, BorderLayout.CENTER);
      panel_1.setLayout(new BorderLayout(0, 0));
      panel_1.add(getPanel_5(), BorderLayout.NORTH);

      JPanel panel_2 = new JPanel();
      panel_2.setBorder(null);
      panel_1.add(panel_2, BorderLayout.CENTER);
      panel_2.setLayout(new BorderLayout(0, 0));

      JPanel panel_8 = new JPanel();
      panel_2.add(panel_8, BorderLayout.NORTH);
      panel_8.setLayout(new BorderLayout(0, 0));

      JPanel panel_9 = new JPanel();
      panel_9.setBorder(new TitledBorder(null, "Flames", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_9.setPreferredSize(new Dimension(10, 260));
      panel_2.add(panel_9, BorderLayout.SOUTH);
      panel_9.setLayout(new BorderLayout(0, 0));

      JScrollPane swfAnimatorFlamesScrollPane = new JScrollPane();
      panel_9.add(swfAnimatorFlamesScrollPane, BorderLayout.CENTER);
      swfAnimatorFlamesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

      swfAnimatorFlamesPanel = new JPanel();
      swfAnimatorFlamesScrollPane.setViewportView(swfAnimatorFlamesPanel);
      swfAnimatorFlamesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

      JPanel panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(160, 10));
      panel_9.add(panel_3, BorderLayout.EAST);
      panel_3.add(getSwfAnimatorLoadFlameFromMainButton());
      panel_3.add(getSwfAnimatorLoadFlameFromClipboardButton());
      panel_3.add(getSwfAnimatorLoadFlameButton());
      panel_3.add(getSwfAnimatorMoveUpButton());
      panel_3.add(getSwfAnimatorMoveDownButton());
      panel_3.add(getSwfAnimatorRemoveFlameButton());
      panel_3.add(getSwfAnimatorRemoveAllFlamesButton());

      swfAnimatorPanel_1 = new JPanel();
      swfAnimatorPanel_1.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_2.add(swfAnimatorPanel_1, BorderLayout.CENTER);
      swfAnimatorPanel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_83 = new JPanel();
      panel_83.setPreferredSize(new Dimension(10, 74));
      swfAnimatorPanel_1.add(panel_83, BorderLayout.SOUTH);
      panel_83.setLayout(new BorderLayout(0, 0));

      swfAnimatorFrameSlider = new JSlider();
      swfAnimatorFrameSlider.setBorder(new EmptyBorder(0, 4, 4, 0));
      panel_83.add(swfAnimatorFrameSlider, BorderLayout.CENTER);
      swfAnimatorFrameSlider.setMinorTickSpacing(5);
      swfAnimatorFrameSlider.setMinimum(1);
      swfAnimatorFrameSlider.setMajorTickSpacing(10);
      swfAnimatorFrameSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().previewFlameImage();
          }
        }
      });
      swfAnimatorFrameSlider.setValue(0);
      swfAnimatorFrameSlider.setPreferredSize(new Dimension(340, 19));
      swfAnimatorFrameSlider.setMaximum(60);
      panel_83.add(getPanel_84(), BorderLayout.SOUTH);
      panel_83.add(getPanel_12(), BorderLayout.NORTH);

      swfAnimatorPreviewRootPanel = new JPanel();
      swfAnimatorPanel_1.add(swfAnimatorPreviewRootPanel, BorderLayout.CENTER);
      swfAnimatorPreviewRootPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_10 = new JPanel();
      panel_10.setBorder(null);
      panel_10.setPreferredSize(new Dimension(400, 10));
      panel_2.add(panel_10, BorderLayout.EAST);
      panel_10.setLayout(new BoxLayout(panel_10, BoxLayout.Y_AXIS));

      JScrollPane scrollPane_2 = new JScrollPane();
      scrollPane_2.setBorder(new TitledBorder(null, "Global scripts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      panel_10.add(scrollPane_2);

      JPanel panel_85 = new JPanel();
      panel_85.setPreferredSize(new Dimension(315, 280));
      scrollPane_2.setViewportView(panel_85);
      panel_85.setLayout(null);
      panel_85.add(getSwfAnimatorGlobalScript1Cmb());
      animateGlobalScriptLbl = new JLabel();
      animateGlobalScriptLbl.setBounds(0, 0, 21, 22);
      panel_85.add(animateGlobalScriptLbl);
      animateGlobalScriptLbl.setName("animateGlobalScriptLbl");
      animateGlobalScriptLbl.setPreferredSize(new Dimension(94, 22));
      animateGlobalScriptLbl.setText("01");
      animateGlobalScriptLbl.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript1REd = new JWFNumberField();
      swfAnimatorGlobalScript1REd.setBounds(213, 0, 100, 24);
      panel_85.add(swfAnimatorGlobalScript1REd);
      swfAnimatorGlobalScript1REd.setValue(1.0);
      swfAnimatorGlobalScript1REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript1REd.setValueStep(0.1);
      swfAnimatorGlobalScript1REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript1REd.setMotionPropertyName("globalScript1");
      swfAnimatorGlobalScript1REd.setLinkedLabelControlName("animateGlobalScriptLbl");
      swfAnimatorGlobalScript1REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblGlobalScript = new JLabel();
      lblGlobalScript.setBounds(0, 23, 21, 22);
      panel_85.add(lblGlobalScript);
      lblGlobalScript.setName("lblGlobalScript");
      lblGlobalScript.setText("02");
      lblGlobalScript.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript2Cmb = new JComboBox();
      swfAnimatorGlobalScript2Cmb.setBounds(22, 23, 186, 24);
      panel_85.add(swfAnimatorGlobalScript2Cmb);
      swfAnimatorGlobalScript2Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript2Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript2Cmb.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript2REd = new JWFNumberField();
      swfAnimatorGlobalScript2REd.setBounds(213, 23, 100, 24);
      panel_85.add(swfAnimatorGlobalScript2REd);
      swfAnimatorGlobalScript2REd.setValue(1.0);
      swfAnimatorGlobalScript2REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript2REd.setValueStep(0.1);
      swfAnimatorGlobalScript2REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript2REd.setMotionPropertyName("globalScript2");
      swfAnimatorGlobalScript2REd.setLinkedLabelControlName("lblGlobalScript");
      swfAnimatorGlobalScript2REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblGlobalScript_1 = new JLabel();
      lblGlobalScript_1.setBounds(0, 46, 21, 22);
      panel_85.add(lblGlobalScript_1);
      lblGlobalScript_1.setName("lblGlobalScript_1");
      lblGlobalScript_1.setText("03");
      lblGlobalScript_1.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_1.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript3Cmb = new JComboBox();
      swfAnimatorGlobalScript3Cmb.setBounds(22, 46, 186, 24);
      panel_85.add(swfAnimatorGlobalScript3Cmb);
      swfAnimatorGlobalScript3Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript3Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript3Cmb.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript3REd = new JWFNumberField();
      swfAnimatorGlobalScript3REd.setBounds(213, 46, 100, 24);
      panel_85.add(swfAnimatorGlobalScript3REd);
      swfAnimatorGlobalScript3REd.setValue(1.0);
      swfAnimatorGlobalScript3REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript3REd.setValueStep(0.1);
      swfAnimatorGlobalScript3REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript3REd.setMotionPropertyName("globalScript3");
      swfAnimatorGlobalScript3REd.setLinkedLabelControlName("lblGlobalScript_1");
      swfAnimatorGlobalScript3REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblGlobalScript_2 = new JLabel();
      lblGlobalScript_2.setBounds(0, 69, 21, 22);
      panel_85.add(lblGlobalScript_2);
      lblGlobalScript_2.setName("lblGlobalScript_2");
      lblGlobalScript_2.setText("04");
      lblGlobalScript_2.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_2.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript4Cmb = new JComboBox();
      swfAnimatorGlobalScript4Cmb.setBounds(22, 69, 186, 24);
      panel_85.add(swfAnimatorGlobalScript4Cmb);
      swfAnimatorGlobalScript4Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript4Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript4Cmb.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript4REd = new JWFNumberField();
      swfAnimatorGlobalScript4REd.setBounds(213, 69, 100, 24);
      panel_85.add(swfAnimatorGlobalScript4REd);
      swfAnimatorGlobalScript4REd.setValue(1.0);
      swfAnimatorGlobalScript4REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript4REd.setValueStep(0.1);
      swfAnimatorGlobalScript4REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript4REd.setMotionPropertyName("globalScript4");
      swfAnimatorGlobalScript4REd.setLinkedLabelControlName("lblGlobalScript_2");
      swfAnimatorGlobalScript4REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblGlobalScript_3 = new JLabel();
      lblGlobalScript_3.setBounds(0, 92, 21, 22);
      panel_85.add(lblGlobalScript_3);
      lblGlobalScript_3.setName("lblGlobalScript_3");
      lblGlobalScript_3.setText("05");
      lblGlobalScript_3.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_3.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript5Cmb = new JComboBox();
      swfAnimatorGlobalScript5Cmb.setBounds(22, 92, 186, 24);
      panel_85.add(swfAnimatorGlobalScript5Cmb);
      swfAnimatorGlobalScript5Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript5Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript5Cmb.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript5REd = new JWFNumberField();
      swfAnimatorGlobalScript5REd.setBounds(213, 92, 100, 24);
      panel_85.add(swfAnimatorGlobalScript5REd);
      swfAnimatorGlobalScript5REd.setValue(1.0);
      swfAnimatorGlobalScript5REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript5REd.setValueStep(0.1);
      swfAnimatorGlobalScript5REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript5REd.setMotionPropertyName("globalScript5");
      swfAnimatorGlobalScript5REd.setLinkedLabelControlName("lblGlobalScript_3");
      swfAnimatorGlobalScript5REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      lblGlobalScript_4 = new JLabel();
      lblGlobalScript_4.setText("06");
      lblGlobalScript_4.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_4.setName("lblGlobalScript_4");
      lblGlobalScript_4.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGlobalScript_4.setBounds(0, 115, 21, 22);
      panel_85.add(lblGlobalScript_4);

      swfAnimatorGlobalScript6Cmb = new JComboBox();
      swfAnimatorGlobalScript6Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript6Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript6Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript6Cmb.setBounds(22, 115, 186, 24);
      swfAnimatorGlobalScript6Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript6Cmb);

      swfAnimatorGlobalScript6REd = new JWFNumberField();
      swfAnimatorGlobalScript6REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript6REd.setValueStep(0.1);
      swfAnimatorGlobalScript6REd.setValue(1.0);
      swfAnimatorGlobalScript6REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript6REd.setMotionPropertyName("globalScript6");
      swfAnimatorGlobalScript6REd.setLinkedLabelControlName("lblGlobalScript_4");
      swfAnimatorGlobalScript6REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript6REd.setBounds(213, 115, 100, 24);
      swfAnimatorGlobalScript6REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript6REd());
        }
      });
      swfAnimatorGlobalScript6REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript6REd);

      swfAnimatorGlobalScript7Cmb = new JComboBox();
      swfAnimatorGlobalScript7Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript7Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript7Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript7Cmb.setBounds(22, 138, 186, 24);
      swfAnimatorGlobalScript7Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript7Cmb);

      lblGlobalScript_5 = new JLabel();
      lblGlobalScript_5.setText("07");
      lblGlobalScript_5.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_5.setName("lblGlobalScript_5");
      lblGlobalScript_5.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGlobalScript_5.setBounds(0, 138, 21, 22);
      panel_85.add(lblGlobalScript_5);

      swfAnimatorGlobalScript7REd = new JWFNumberField();
      swfAnimatorGlobalScript7REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript7REd.setValueStep(0.1);
      swfAnimatorGlobalScript7REd.setValue(1.0);
      swfAnimatorGlobalScript7REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript7REd.setMotionPropertyName("globalScript7");
      swfAnimatorGlobalScript7REd.setLinkedLabelControlName("lblGlobalScript_5");
      swfAnimatorGlobalScript7REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript7REd.setBounds(213, 138, 100, 24);
      swfAnimatorGlobalScript7REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript7REd());
        }
      });
      swfAnimatorGlobalScript7REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript7REd);

      lblGlobalScript_6 = new JLabel();
      lblGlobalScript_6.setText("08");
      lblGlobalScript_6.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_6.setName("lblGlobalScript_6");
      lblGlobalScript_6.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGlobalScript_6.setBounds(0, 161, 21, 22);
      panel_85.add(lblGlobalScript_6);

      swfAnimatorGlobalScript8Cmb = new JComboBox();
      swfAnimatorGlobalScript8Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript8Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript8Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript8Cmb.setBounds(22, 161, 186, 24);
      swfAnimatorGlobalScript8Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript8Cmb);

      swfAnimatorGlobalScript8REd = new JWFNumberField();
      swfAnimatorGlobalScript8REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript8REd.setValueStep(0.1);
      swfAnimatorGlobalScript8REd.setValue(1.0);
      swfAnimatorGlobalScript8REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript8REd.setMotionPropertyName("globalScript8");
      swfAnimatorGlobalScript8REd.setLinkedLabelControlName("lblGlobalScript_6");
      swfAnimatorGlobalScript8REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript8REd.setBounds(213, 161, 100, 24);
      swfAnimatorGlobalScript8REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript8REd());
        }
      });
      swfAnimatorGlobalScript8REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript8REd);

      lblGlobalScript_7 = new JLabel();
      lblGlobalScript_7.setText("09");
      lblGlobalScript_7.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_7.setName("lblGlobalScript_7");
      lblGlobalScript_7.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGlobalScript_7.setBounds(0, 184, 21, 22);
      panel_85.add(lblGlobalScript_7);

      swfAnimatorGlobalScript9Cmb = new JComboBox();
      swfAnimatorGlobalScript9Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript9Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript9Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript9Cmb.setBounds(22, 184, 186, 24);
      swfAnimatorGlobalScript9Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript9Cmb);

      swfAnimatorGlobalScript9REd = new JWFNumberField();
      swfAnimatorGlobalScript9REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript9REd.setValueStep(1.0);
      swfAnimatorGlobalScript9REd.setValue(1.0);
      swfAnimatorGlobalScript9REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript9REd.setMotionPropertyName("globalScript9");
      swfAnimatorGlobalScript9REd.setLinkedLabelControlName("lblGlobalScript_7");
      swfAnimatorGlobalScript9REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript9REd.setBounds(213, 184, 100, 24);
      swfAnimatorGlobalScript9REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript9REd());
        }
      });
      swfAnimatorGlobalScript9REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript9REd);

      lblGlobalScript_8 = new JLabel();
      lblGlobalScript_8.setText("10");
      lblGlobalScript_8.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_8.setName("lblGlobalScript_8");
      lblGlobalScript_8.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGlobalScript_8.setBounds(0, 207, 21, 22);
      panel_85.add(lblGlobalScript_8);

      swfAnimatorGlobalScript10Cmb = new JComboBox();
      swfAnimatorGlobalScript10Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript10Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript10Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript10Cmb.setBounds(22, 207, 186, 24);
      swfAnimatorGlobalScript10Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript10Cmb);

      swfAnimatorGlobalScript10REd = new JWFNumberField();
      swfAnimatorGlobalScript10REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript10REd.setValueStep(0.1);
      swfAnimatorGlobalScript10REd.setValue(1.0);
      swfAnimatorGlobalScript10REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript10REd.setMotionPropertyName("globalScript10");
      swfAnimatorGlobalScript10REd.setLinkedLabelControlName("lblGlobalScript_8");
      swfAnimatorGlobalScript10REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript10REd.setBounds(213, 207, 100, 24);
      swfAnimatorGlobalScript10REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript10REd());
        }
      });
      swfAnimatorGlobalScript10REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript10REd);

      JLabel lblGlobalScript_9 = new JLabel();
      lblGlobalScript_9.setText("11");
      lblGlobalScript_9.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_9.setName("lblGlobalScript_9");
      lblGlobalScript_9.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGlobalScript_9.setBounds(0, 230, 21, 22);
      panel_85.add(lblGlobalScript_9);

      swfAnimatorGlobalScript11Cmb = new JComboBox();
      swfAnimatorGlobalScript11Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript11Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript11Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript11Cmb.setBounds(22, 230, 186, 24);
      swfAnimatorGlobalScript11Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript11Cmb);

      swfAnimatorGlobalScript11REd = new JWFNumberField();
      swfAnimatorGlobalScript11REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript11REd.setValueStep(0.1);
      swfAnimatorGlobalScript11REd.setValue(1.0);
      swfAnimatorGlobalScript11REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript11REd.setMotionPropertyName("globalScript11");
      swfAnimatorGlobalScript11REd.setLinkedLabelControlName("lblGlobalScript_9");
      swfAnimatorGlobalScript11REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript11REd.setBounds(213, 230, 100, 24);
      swfAnimatorGlobalScript11REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript11REd());
        }
      });
      swfAnimatorGlobalScript11REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript11REd);

      JLabel lblGlobalScript_10 = new JLabel();
      lblGlobalScript_10.setText("12");
      lblGlobalScript_10.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_10.setName("lblGlobalScript_10");
      lblGlobalScript_10.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGlobalScript_10.setBounds(0, 253, 21, 22);
      panel_85.add(lblGlobalScript_10);

      swfAnimatorGlobalScript12Cmb = new JComboBox();
      swfAnimatorGlobalScript12Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript12Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript12Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript12Cmb.setBounds(22, 253, 186, 24);
      swfAnimatorGlobalScript12Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript12Cmb);

      swfAnimatorGlobalScript12REd = new JWFNumberField();
      swfAnimatorGlobalScript12REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript12REd.setValueStep(0.1);
      swfAnimatorGlobalScript12REd.setValue(1.0);
      swfAnimatorGlobalScript12REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript12REd.setMotionPropertyName("globalScript12");
      swfAnimatorGlobalScript12REd.setLinkedLabelControlName("lblGlobalScript_10");
      swfAnimatorGlobalScript12REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript12REd.setBounds(213, 253, 100, 24);
      swfAnimatorGlobalScript12REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript12REd());
        }
      });
      swfAnimatorGlobalScript12REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript12REd);
      panel_10.add(getScrollPane_9());
      panel_10.add(getPanel_87());
      swfAnimatorGlobalScript5REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript5REd());
        }
      });
      swfAnimatorGlobalScript5REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript5Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript4REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript4REd());
        }
      });
      swfAnimatorGlobalScript4REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript4Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript3REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript3REd());
        }
      });
      swfAnimatorGlobalScript3REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript3Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript2REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript2REd());
        }
      });
      swfAnimatorGlobalScript2REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript2Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript1REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript1REd());
        }
      });
      swfAnimatorGlobalScript1REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });

      JPanel panel_11 = new JPanel();
      panel_11.setBorder(null);
      panel_11.setPreferredSize(new Dimension(400, 10));
      panel_2.add(panel_11, BorderLayout.WEST);
      panel_11.setLayout(new BorderLayout(0, 0));
      panel_11.add(getPanel_14(), BorderLayout.CENTER);
    }
    return tinaSWFAnimatorPanel;
  }

  /**
   * This method initializes animationGenerateButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getSwfAnimatorGenerateButton() {
    if (swfAnimatorGenerateButton == null) {
      swfAnimatorGenerateButton = new JButton();
      swfAnimatorGenerateButton.setBounds(921, 5, 125, 52);
      swfAnimatorGenerateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().generateButton_clicked();
        }
      });
      swfAnimatorGenerateButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorGenerateButton.setText("Generate flames");
      swfAnimatorGenerateButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorGenerateButton;
  }

  /**
   * This method initializes animateFramesREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getSwfAnimatorFramesREd() {
    if (swfAnimatorFramesREd == null) {
      swfAnimatorFramesREd = new JWFNumberField();
      swfAnimatorFramesREd.setOnlyIntegers(true);
      swfAnimatorFramesREd.setEditable(false);
      swfAnimatorFramesREd.setPreferredSize(new Dimension(64, 24));
      swfAnimatorFramesREd.setText("60");
      swfAnimatorFramesREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return swfAnimatorFramesREd;
  }

  /**
   * This method initializes animateScriptCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getSwfAnimatorGlobalScript1Cmb() {
    if (swfAnimatorGlobalScript1Cmb == null) {
      swfAnimatorGlobalScript1Cmb = new JComboBox();
      swfAnimatorGlobalScript1Cmb.setBounds(22, 0, 186, 24);
      swfAnimatorGlobalScript1Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript1Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript1Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript1Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
    }
    return swfAnimatorGlobalScript1Cmb;
  }

  /**
   * This method initializes animateXFormScriptCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getSwfAnimatorXFormScript1Cmb() {
    if (swfAnimatorXFormScript1Cmb == null) {
      swfAnimatorXFormScript1Cmb = new JComboBox();
      swfAnimatorXFormScript1Cmb.setBounds(22, 0, 186, 24);
      swfAnimatorXFormScript1Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript1Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript1Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript1Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
    }
    return swfAnimatorXFormScript1Cmb;
  }

  private JPanel getTriangleOperationsPanel() {
    if (triangleOperationsPanel == null) {
      editSpaceLbl1 = new JLabel();
      editSpaceLbl1.setFont(new Font("Dialog", Font.BOLD, 10));
      editSpaceLbl1.setText("");
      editSpaceLbl1.setPreferredSize(new Dimension(42, 2));
      triangleOperationsPanel = new JPanel();
      FlowLayout fl_triangleOperationsPanel = new FlowLayout();
      fl_triangleOperationsPanel.setVgap(1);
      triangleOperationsPanel.setLayout(fl_triangleOperationsPanel);
      triangleOperationsPanel.setPreferredSize(new Dimension(52, 0));

      mouseTransformEditViewButton = new JToggleButton();
      mouseTransformEditViewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformViewButton_clicked();
        }
      });
      mouseTransformEditViewButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/modify_view.png")));
      mouseTransformEditViewButton.setToolTipText("Enable view editing mode (Left mouse: move, right mouse: rotate, middle/wheel: zoom)");
      mouseTransformEditViewButton.setPreferredSize(new Dimension(42, 24));
      triangleOperationsPanel.add(mouseTransformEditViewButton);
      triangleOperationsPanel.add(getMouseTransformMoveTrianglesButton(), null);

      mouseTransformEditPointsButton = new JToggleButton();
      mouseTransformEditPointsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformEditPointsButton_clicked();
        }
      });

      mouseTransformRotateTrianglesButton = new JToggleButton();
      mouseTransformRotateTrianglesButton.setToolTipText("Rotate triangles using the left mouse button");
      mouseTransformRotateTrianglesButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformRotateTrianglesButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/object-rotate-right-3.png")));
      mouseTransformRotateTrianglesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformRotateTrianglesButton_clicked();
        }
      });
      triangleOperationsPanel.add(mouseTransformRotateTrianglesButton);

      mouseTransformScaleTrianglesButton = new JToggleButton();
      mouseTransformScaleTrianglesButton.setToolTipText("Scale triangles using the left mouse button");
      mouseTransformScaleTrianglesButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformScaleTrianglesButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-inout.png")));
      mouseTransformScaleTrianglesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformScaleTrianglesButton_clicked();
        }
      });
      triangleOperationsPanel.add(mouseTransformScaleTrianglesButton);
      mouseTransformEditPointsButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle_points.png")));
      mouseTransformEditPointsButton.setToolTipText("Enable free triangle editing mode");
      mouseTransformEditPointsButton.setPreferredSize(new Dimension(42, 24));
      triangleOperationsPanel.add(mouseTransformEditPointsButton);

      mouseTransformEditTriangleViewButton = new JToggleButton();
      mouseTransformEditTriangleViewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformTriangleViewButton_clicked();
        }
      });
      mouseTransformEditTriangleViewButton.setToolTipText("Edit the view of the controls (drag mouse to move, mouse-wheel or hold <Alt> to zoom)");
      mouseTransformEditTriangleViewButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformEditTriangleViewButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle_view.png")));

      triangleOperationsPanel.add(mouseTransformEditTriangleViewButton);
      triangleOperationsPanel.add(getMouseTransformEditFocusPointButton(), null);
      triangleOperationsPanel.add(editSpaceLbl1, null);

      editorFractalBrightnessSlider = new JSlider();
      editorFractalBrightnessSlider.setOrientation(SwingConstants.VERTICAL);
      triangleOperationsPanel.add(editorFractalBrightnessSlider);
      editorFractalBrightnessSlider.setValue(100);
      editorFractalBrightnessSlider.setPreferredSize(new Dimension(19, 100));
      editorFractalBrightnessSlider.setName("tinaCameraCentreXSlider");
      editorFractalBrightnessSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.editorFractalBrightnessSliderChanged();
          }
        }
      });
      editSpaceLbl2 = new JLabel();
      editSpaceLbl2.setFont(new Font("Dialog", Font.BOLD, 10));
      editSpaceLbl2.setText("");
      editSpaceLbl2.setPreferredSize(new Dimension(42, 2));
      triangleOperationsPanel.add(editSpaceLbl2, null);
      triangleOperationsPanel.add(getTinaRenderFlameButton(), null);
    }
    return triangleOperationsPanel;
  }

  protected void expandGradientEditorFncPnl(boolean pSelected) {
    boolean oldVis = getGradientEditorFncPnl().isVisible();
    getGradientEditorFncPnl().setVisible(pSelected);
    if (oldVis != pSelected && tinaController != null) {
      tinaController.resolutionProfileCmb_changed();
    }
  }

  /**
   * This method initializes mouseTransformMoveButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getMouseTransformMoveTrianglesButton() {
    if (mouseTransformMoveTrianglesButton == null) {
      mouseTransformMoveTrianglesButton = new JToggleButton();
      mouseTransformMoveTrianglesButton.setSelected(true);
      mouseTransformMoveTrianglesButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformMoveTrianglesButton.setToolTipText("Move triangles using the left mouse button (right mouse: rotate, mouse wheel: scale)");
      mouseTransformMoveTrianglesButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle.png")));
      mouseTransformMoveTrianglesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformMoveTrianglesButton_clicked();
        }
      });
    }
    return mouseTransformMoveTrianglesButton;
  }

  /**
   * This method initializes mouseTransformScaleButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getMouseTransformEditFocusPointButton() {
    if (mouseTransformEditFocusPointButton == null) {
      mouseTransformEditFocusPointButton = new JToggleButton();
      mouseTransformEditFocusPointButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformEditFocusPointButton.setToolTipText("Edit focus point");
      mouseTransformEditFocusPointButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/games-config-background.png")));
      mouseTransformEditFocusPointButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformEditFocusPointButton_clicked();
        }
      });
    }
    return mouseTransformEditFocusPointButton;
  }

  /**
   * This method initializes centerNorthPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCenterNorthPanel() {
    if (centerNorthPanel == null) {
      centerNorthPanel = new JPanel();
      centerNorthPanel.setLayout(null);
      centerNorthPanel.setPreferredSize(new Dimension(0, 32));
      centerNorthPanel.add(getRenderProgressBar(), null);
      centerNorthPanel.add(getMouseTransformSlowButton(), null);
      centerNorthPanel.add(getToggleVariationsButton());
      centerNorthPanel.add(getToggleTransparencyButton());

      randomizeBtn = new JButton();
      randomizeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.randomizeBtn_clicked();
        }
      });
      randomizeBtn.setToolTipText("Randomize random parameters of the currently selected flame");
      randomizeBtn.setText("Rnd");
      randomizeBtn.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      randomizeBtn.setSelected(false);
      randomizeBtn.setPreferredSize(new Dimension(42, 24));
      randomizeBtn.setBounds(new Rectangle(434, 4, 72, 24));
      randomizeBtn.setBounds(1, 4, 72, 24);
      centerNorthPanel.add(randomizeBtn);

      toggleDrawGridButton = new JToggleButton();
      toggleDrawGridButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.toggleDrawGridButton_clicked();
        }
      });
      toggleDrawGridButton.setToolTipText("Turn grid on/off");
      toggleDrawGridButton.setSize(new Dimension(95, 24));
      toggleDrawGridButton.setSelected(false);
      toggleDrawGridButton.setPreferredSize(new Dimension(42, 24));
      toggleDrawGridButton.setLocation(new Point(4, 4));
      toggleDrawGridButton.setBounds(486, 4, 42, 24);
      toggleDrawGridButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/grid.png")));
      centerNorthPanel.add(toggleDrawGridButton);
      centerNorthPanel.add(getAffineEditPostTransformSmallButton());

      mouseTransformEditGradientButton = new JToggleButton();
      mouseTransformEditGradientButton.setIconTextGap(0);
      mouseTransformEditGradientButton.setFont(new Font("Dialog", Font.BOLD, 9));
      mouseTransformEditGradientButton.setText("Grdnt");
      mouseTransformEditGradientButton.setBounds(160, 4, 72, 24);
      centerNorthPanel.add(mouseTransformEditGradientButton);
      mouseTransformEditGradientButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-graphics-2.png")));

      mouseTransformEditGradientButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(mouseTransformEditGradientButton.isSelected());
          tinaController.mouseTransformEditGradientButton_clicked();
        }
      });
      mouseTransformEditGradientButton.setToolTipText("Edit gradient (use cursor-left and -right to control marker 1 and cursor-up and -down to control marker 2, press the 1 or 2 key for color choosers)");
      mouseTransformEditGradientButton.setPreferredSize(new Dimension(72, 24));

      toggleTriangleWithColorsButton = new JToggleButton();
      toggleTriangleWithColorsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.toggleTriangleWithColorsButton_clicked();
        }
      });
      toggleTriangleWithColorsButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/atomic.png")));
      toggleTriangleWithColorsButton.setToolTipText("Toggle monochrome/colored controls");
      toggleTriangleWithColorsButton.setPreferredSize(new Dimension(42, 24));
      toggleTriangleWithColorsButton.setMnemonic(KeyEvent.VK_P);
      toggleTriangleWithColorsButton.setFont(new Font("Dialog", Font.BOLD, 10));
      toggleTriangleWithColorsButton.setBounds(596, 4, 42, 24);
      centerNorthPanel.add(toggleTriangleWithColorsButton);

      triangleStyleCmb = new JComboBox();
      triangleStyleCmb.setPreferredSize(new Dimension(125, 24));
      triangleStyleCmb.setMinimumSize(new Dimension(100, 24));
      triangleStyleCmb.setMaximumSize(new Dimension(32767, 24));
      triangleStyleCmb.setMaximumRowCount(32);
      triangleStyleCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      triangleStyleCmb.setBounds(528, 4, 68, 24);
      triangleStyleCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.triangleStyleCmb_changed();
          }
        }
      });

      centerNorthPanel.add(triangleStyleCmb);
    }
    return centerNorthPanel;
  }

  private JToggleButton getToggleVariationsButton() {
    if (toggleVariationsButton == null) {
      toggleVariationsButton = new JToggleButton();
      toggleVariationsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.toggleVariationsButton_clicked();
        }
      });
      toggleVariationsButton.setToolTipText("Display/hide variation effect");
      toggleVariationsButton.setSize(new Dimension(95, 24));
      toggleVariationsButton.setSelected(false);
      toggleVariationsButton.setPreferredSize(new Dimension(42, 24));
      toggleVariationsButton.setLocation(new Point(4, 4));
      toggleVariationsButton.setBounds(74, 4, 42, 24);
      toggleVariationsButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/oscilloscope.png")));
    }
    return toggleVariationsButton;
  }

  /**
   * This method initializes centerWestPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCenterWestPanel() {
    if (centerWestPanel == null) {
      centerWestPanel = new JPanel();
      centerWestPanel.setPreferredSize(new Dimension(74, 0));
      centerWestPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 3));
      centerWestPanel.add(getTinaAppendToMovieButton());
      centerWestPanel.add(getTinaAppendToDancingFlamesButton());
      centerWestPanel.add(getQuickMutationButton());
      centerWestPanel.add(getLabel_5());
      centerWestPanel.add(getUndoButton());
      centerWestPanel.add(getRedoButton());
      centerWestPanel.add(getLabel_6());
      centerWestPanel.add(getSnapShotButton());
      centerWestPanel.add(getLabel_8());
      centerWestPanel.add(getEditFlameTitleBtn());
      centerWestPanel.add(getBokehBtn());

      motionCurveEditModeButton = new JToggleButton();
      centerWestPanel.add(motionCurveEditModeButton);
      motionCurveEditModeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getAnimationController().toggleMotionCurveEditing();
        }
      });
      motionCurveEditModeButton.setToolTipText("Enable new controls to edit flame-properties as motion-curves rather than simple values");
      motionCurveEditModeButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/motionEditingMode.gif")));
      motionCurveEditModeButton.setPreferredSize(new Dimension(72, 42));
      motionCurveEditModeButton.setFont(new Font("Dialog", Font.BOLD, 10));
      motionCurveEditModeButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/video-x-generic-2.png")));
    }
    return centerWestPanel;
  }

  /**
   * This method initializes centerCenterPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCenterCenterPanel() {
    if (centerCenterPanel == null) {
      centerCenterPanel = new JPanel();
      centerCenterPanel.setLayout(new BorderLayout());
      centerCenterPanel.setBackground(Color.gray);
      centerCenterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      centerDescLabel = new JLabel();
      centerCenterPanel.add(centerDescLabel, BorderLayout.NORTH);
      centerDescLabel.setText("  (just double-click or right-click on thumbnail to load it into main area)");
      centerDescLabel.setHorizontalAlignment(SwingConstants.CENTER);
      centerDescLabel.setFont(new Font("Dialog", Font.BOLD, 10));
      centerCenterPanel.add(getGradientEditorFncPnl(), BorderLayout.SOUTH);
    }
    return centerCenterPanel;
  }

  /**
   * This method initializes randomStyleCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getRandomStyleCmb() {
    if (randomStyleCmb == null) {
      randomStyleCmb = new JComboBox();
      randomStyleCmb.setToolTipText("Random-flame-generator");
      randomStyleCmb.setMaximumSize(new Dimension(32767, 24));
      randomStyleCmb.setMinimumSize(new Dimension(110, 24));
      randomStyleCmb.setPreferredSize(new Dimension(110, 24));
      randomStyleCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      randomStyleCmb.setMaximumRowCount(32);
    }
    return randomStyleCmb;
  }

  /**
   * This method initializes affineEditPostTransformButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getAffineEditPostTransformButton() {
    if (affineEditPostTransformButton == null) {
      affineEditPostTransformButton = new JToggleButton();
      affineEditPostTransformButton.setPreferredSize(new Dimension(136, 24));
      affineEditPostTransformButton.setSize(new Dimension(104, 24));
      affineEditPostTransformButton.setText("Post TF");
      affineEditPostTransformButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineEditPostTransformButton.setLocation(new Point(0, 155));
      affineEditPostTransformButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle_post.png")));
      affineEditPostTransformButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.affineEditPostTransformButton_clicked();
        }
      });
    }
    return affineEditPostTransformButton;
  }

  /**
   * This method initializes affineEditPostTransformSmallButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getAffineEditPostTransformSmallButton() {
    if (affineEditPostTransformSmallButton == null) {
      affineEditPostTransformSmallButton = new JToggleButton();
      affineEditPostTransformSmallButton.setBounds(638, 4, 42, 24);
      affineEditPostTransformSmallButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle_post.png")));
      affineEditPostTransformSmallButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineEditPostTransformSmallButton.setToolTipText("Toggle post transform mode");
      affineEditPostTransformSmallButton.setMnemonic(KeyEvent.VK_P);
      affineEditPostTransformSmallButton.setPreferredSize(new Dimension(42, 24));
      affineEditPostTransformSmallButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.affineEditPostTransformSmallButton_clicked();
        }
      });
    }
    return affineEditPostTransformSmallButton;
  }

  JProgressBar getRenderProgressBar() {
    if (renderProgressBar == null) {
      renderProgressBar = new JProgressBar();
      renderProgressBar.setValue(0);
      renderProgressBar.setSize(new Dimension(184, 14));
      renderProgressBar.setLocation(new Point(232, 9));
      renderProgressBar.setPreferredSize(new Dimension(179, 14));
      renderProgressBar.setStringPainted(true);
    }
    return renderProgressBar;
  }

  /**
   * This method initializes affineResetTransformButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineResetTransformButton() {
    if (affineResetTransformButton == null) {
      affineResetTransformButton = new JButton();
      affineResetTransformButton.setPreferredSize(new Dimension(136, 24));
      affineResetTransformButton.setText("Reset TF");
      affineResetTransformButton.setLocation(new Point(109, 155));
      affineResetTransformButton.setSize(new Dimension(104, 24));
      affineResetTransformButton.setToolTipText("Reset triangle to defaults");
      affineResetTransformButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineResetTransformButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.affineResetTransformButton_clicked();
        }
      });
    }
    return affineResetTransformButton;
  }

  /**
   * This method initializes nonlinearVar4Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar4Panel() {
    if (nonlinearVar4Panel == null) {
      nonlinearParams4Lbl = new JLabel();
      nonlinearParams4Lbl.setLocation(new Point(14, 26));
      nonlinearParams4Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams4Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams4Lbl.setText("Params");
      nonlinearParams4Lbl.setSize(new Dimension(50, 22));
      nonlinearVar4Lbl = new JLabel();
      nonlinearVar4Lbl.setLocation(new Point(4, 2));
      nonlinearVar4Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar4Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar4Lbl.setText("Variation 4");
      nonlinearVar4Lbl.setSize(new Dimension(60, 22));
      nonlinearVar4Panel = new JPanel();
      nonlinearVar4Panel.setLayout(null);
      nonlinearVar4Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar4Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar4Panel.add(nonlinearVar4Lbl, null);
      nonlinearVar4Panel.add(getNonlinearVar4Cmb(), null);
      nonlinearVar4Panel.add(getNonlinearVar4REd(), null);
      nonlinearVar4Panel.add(nonlinearParams4Lbl, null);
      nonlinearVar4Panel.add(getNonlinearParams4Cmb(), null);
      nonlinearVar4Panel.add(getNonlinearParams4REd(), null);
      nonlinearVar4Panel.add(getNonlinearParams4LeftButton(), null);
    }
    return nonlinearVar4Panel;
  }

  /**
   * This method initializes nonlinearVar4Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar4Cmb() {
    if (nonlinearVar4Cmb == null) {
      nonlinearVar4Cmb = new JComboBox();
      nonlinearVar4Cmb.setLocation(new Point(66, 2));
      nonlinearVar4Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar4Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar4Cmb.setMaximumRowCount(22);
      nonlinearVar4Cmb.setSize(new Dimension(120, 24));
      nonlinearVar4Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(3);
        }
      });
    }
    return nonlinearVar4Cmb;
  }

  /**
   * This method initializes nonlinearVar4REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar4REd() {
    if (nonlinearVar4REd == null) {
      nonlinearVar4REd = new JWFNumberField();
      nonlinearVar4REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(3);
        }
      });
      nonlinearVar4REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar4REd.isMouseAdjusting() || nonlinearVar4REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(3);
          }
        }
      });
      nonlinearVar4REd.setValueStep(0.01);
      nonlinearVar4REd.setLocation(new Point(188, 2));
      nonlinearVar4REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar4REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar4REd.setText("");
      nonlinearVar4REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar4REd;
  }

  /**
   * This method initializes nonlinearParams4Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams4Cmb() {
    if (nonlinearParams4Cmb == null) {
      nonlinearParams4Cmb = new JComboBox();
      nonlinearParams4Cmb.setLocation(new Point(66, 26));
      nonlinearParams4Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams4Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams4Cmb.setSize(new Dimension(120, 24));
      nonlinearParams4Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(3);
        }
      });
    }
    return nonlinearParams4Cmb;
  }

  /**
   * This method initializes nonlinearParams4REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams4REd() {
    if (nonlinearParams4REd == null) {
      nonlinearParams4REd = new JWFNumberField();
      nonlinearParams4REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(3);
        }
      });
      nonlinearParams4REd.setValueStep(0.05);
      nonlinearParams4REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams4REd.isMouseAdjusting() || nonlinearParams4REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(3);
          }
        }
      });
      nonlinearParams4REd.setLocation(new Point(188, 26));
      nonlinearParams4REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams4REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams4REd.setText("");
      nonlinearParams4REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams4REd;
  }

  /**
   * This method initializes nonlinearParams4LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams4LeftButton() {
    if (nonlinearParams4LeftButton == null) {
      nonlinearParams4LeftButton = new JButton();
      nonlinearParams4LeftButton.setLocation(new Point(269, 26));
      nonlinearParams4LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams4LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams4LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams4LeftButton.setText("");
      nonlinearParams4LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams4LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(3);
        }
      });
    }
    return nonlinearParams4LeftButton;
  }

  /**
   * This method initializes createPaletteScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getCreatePaletteScrollPane() {
    if (createPaletteScrollPane == null) {
      createPaletteScrollPane = new JScrollPane();
      createPaletteScrollPane.setBounds(347, 4, 317, 154);
      createPaletteScrollPane.setPreferredSize(new Dimension(453, 399));
      createPaletteScrollPane.setViewportView(getCreatePaletteColorsTable());
    }
    return createPaletteScrollPane;
  }

  /**
   * This method initializes createPaletteColorsTable	
   * 	
   * @return javax.swing.JTable	
   */
  private JTable getCreatePaletteColorsTable() {
    if (createPaletteColorsTable == null) {
      createPaletteColorsTable = new JTable();
      createPaletteColorsTable.setFont(new Font("Dialog", Font.PLAIN, 10));
      createPaletteColorsTable.setSize(new Dimension(177, 80));
    }
    return createPaletteColorsTable;
  }

  /**
   * This method initializes nonlinearScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getNonlinearScrollPane() {
    if (nonlinearScrollPane == null) {
      nonlinearScrollPane = new JScrollPane();
      nonlinearScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      nonlinearScrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      nonlinearScrollPane.setPreferredSize(new Dimension(318, 200));
      nonlinearScrollPane.setViewportView(getNonlinearControlsPanel());
      nonlinearScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }
    return nonlinearScrollPane;
  }

  /**
   * This method initializes nonlinearControlsPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearControlsPanel() {
    if (nonlinearControlsPanel == null) {
      FlowLayout flowLayout3 = new FlowLayout();
      flowLayout3.setAlignment(FlowLayout.LEFT);
      flowLayout3.setVgap(1);
      flowLayout3.setHgap(1);
      nonlinearControlsPanel = new JPanel();
      nonlinearControlsPanel.setPreferredSize(new Dimension(296, 638));
      nonlinearControlsPanel.setLayout(flowLayout3);
      nonlinearControlsPanel.add(getNonlinearVar1Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar2Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar3Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar4Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar5Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar6Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar7Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar8Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar9Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar10Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar11Panel(), null);
      nonlinearControlsPanel.add(getNonlinearVar12Panel(), null);
    }
    return nonlinearControlsPanel;
  }

  /**
   * This method initializes nonlinearVar5Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar5Panel() {
    if (nonlinearVar5Panel == null) {
      nonlinearParams5Lbl = new JLabel();
      nonlinearParams5Lbl.setLocation(new Point(14, 26));
      nonlinearParams5Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams5Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams5Lbl.setText("Params");
      nonlinearParams5Lbl.setSize(new Dimension(50, 22));
      nonlinearVar5Lbl = new JLabel();
      nonlinearVar5Lbl.setLocation(new Point(4, 2));
      nonlinearVar5Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar5Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar5Lbl.setText("Variation 5");
      nonlinearVar5Lbl.setSize(new Dimension(60, 22));
      nonlinearVar5Panel = new JPanel();
      nonlinearVar5Panel.setLayout(null);
      nonlinearVar5Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar5Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar5Panel.add(nonlinearVar5Lbl, null);
      nonlinearVar5Panel.add(getNonlinearVar5Cmb(), null);
      nonlinearVar5Panel.add(getNonlinearVar5REd(), null);
      nonlinearVar5Panel.add(nonlinearParams5Lbl, null);
      nonlinearVar5Panel.add(getNonlinearParams5Cmb(), null);
      nonlinearVar5Panel.add(getNonlinearParams5REd(), null);
      nonlinearVar5Panel.add(getNonlinearParams5LeftButton(), null);
    }
    return nonlinearVar5Panel;
  }

  /**
   * This method initializes nonlinearVar5Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar5Cmb() {
    if (nonlinearVar5Cmb == null) {
      nonlinearVar5Cmb = new JComboBox();
      nonlinearVar5Cmb.setLocation(new Point(66, 2));
      nonlinearVar5Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar5Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar5Cmb.setMaximumRowCount(22);
      nonlinearVar5Cmb.setSize(new Dimension(120, 24));
      nonlinearVar5Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(4);
        }
      });
    }
    return nonlinearVar5Cmb;
  }

  /**
   * This method initializes nonlinearVar5REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar5REd() {
    if (nonlinearVar5REd == null) {
      nonlinearVar5REd = new JWFNumberField();
      nonlinearVar5REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(4);
        }
      });
      nonlinearVar5REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar5REd.isMouseAdjusting() || nonlinearVar5REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(4);
          }
        }
      });
      nonlinearVar5REd.setValueStep(0.01);
      nonlinearVar5REd.setLocation(new Point(188, 2));
      nonlinearVar5REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar5REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar5REd.setText("");
      nonlinearVar5REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar5REd;
  }

  /**
   * This method initializes nonlinearParams5Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams5Cmb() {
    if (nonlinearParams5Cmb == null) {
      nonlinearParams5Cmb = new JComboBox();
      nonlinearParams5Cmb.setLocation(new Point(66, 26));
      nonlinearParams5Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams5Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams5Cmb.setSize(new Dimension(120, 24));
      nonlinearParams5Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(4);
        }
      });
    }
    return nonlinearParams5Cmb;
  }

  /**
   * This method initializes nonlinearParams5REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams5REd() {
    if (nonlinearParams5REd == null) {
      nonlinearParams5REd = new JWFNumberField();
      nonlinearParams5REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(4);
        }
      });
      nonlinearParams5REd.setValueStep(0.05);
      nonlinearParams5REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams5REd.isMouseAdjusting() || nonlinearParams5REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(4);
          }
        }
      });
      nonlinearParams5REd.setLocation(new Point(188, 26));
      nonlinearParams5REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams5REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams5REd.setText("");
      nonlinearParams5REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams5REd;
  }

  /**
   * This method initializes nonlinearParams5LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams5LeftButton() {
    if (nonlinearParams5LeftButton == null) {
      nonlinearParams5LeftButton = new JButton();
      nonlinearParams5LeftButton.setLocation(new Point(269, 26));
      nonlinearParams5LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams5LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams5LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams5LeftButton.setText("");
      nonlinearParams5LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams5LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(4);
        }
      });
    }
    return nonlinearParams5LeftButton;
  }

  /**
   * This method initializes nonlinearVar6Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar6Panel() {
    if (nonlinearVar6Panel == null) {
      nonlinearParams6Lbl = new JLabel();
      nonlinearParams6Lbl.setLocation(new Point(14, 26));
      nonlinearParams6Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams6Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams6Lbl.setText("Params");
      nonlinearParams6Lbl.setSize(new Dimension(50, 22));
      nonlinearVar6Lbl = new JLabel();
      nonlinearVar6Lbl.setLocation(new Point(4, 2));
      nonlinearVar6Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar6Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar6Lbl.setText("Variation 6");
      nonlinearVar6Lbl.setSize(new Dimension(60, 22));
      nonlinearVar6Panel = new JPanel();
      nonlinearVar6Panel.setLayout(null);
      nonlinearVar6Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar6Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar6Panel.add(nonlinearVar6Lbl, null);
      nonlinearVar6Panel.add(getNonlinearVar6Cmb(), null);
      nonlinearVar6Panel.add(getNonlinearVar6REd(), null);
      nonlinearVar6Panel.add(nonlinearParams6Lbl, null);
      nonlinearVar6Panel.add(getNonlinearParams6Cmb(), null);
      nonlinearVar6Panel.add(getNonlinearParams6REd(), null);
      nonlinearVar6Panel.add(getNonlinearParams6LeftButton(), null);
    }
    return nonlinearVar6Panel;
  }

  /**
   * This method initializes nonlinearVar6Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar6Cmb() {
    if (nonlinearVar6Cmb == null) {
      nonlinearVar6Cmb = new JComboBox();
      nonlinearVar6Cmb.setLocation(new Point(66, 2));
      nonlinearVar6Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar6Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar6Cmb.setMaximumRowCount(22);
      nonlinearVar6Cmb.setSize(new Dimension(120, 24));
      nonlinearVar6Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(5);
        }
      });
    }
    return nonlinearVar6Cmb;
  }

  /**
   * This method initializes nonlinearVar6REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar6REd() {
    if (nonlinearVar6REd == null) {
      nonlinearVar6REd = new JWFNumberField();
      nonlinearVar6REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(5);
        }
      });
      nonlinearVar6REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar6REd.isMouseAdjusting() || nonlinearVar6REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(5);
          }
        }
      });
      nonlinearVar6REd.setValueStep(0.01);
      nonlinearVar6REd.setLocation(new Point(188, 2));
      nonlinearVar6REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar6REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar6REd.setText("");
      nonlinearVar6REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar6REd;
  }

  /**
   * This method initializes nonlinearParams6Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams6Cmb() {
    if (nonlinearParams6Cmb == null) {
      nonlinearParams6Cmb = new JComboBox();
      nonlinearParams6Cmb.setLocation(new Point(66, 26));
      nonlinearParams6Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams6Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams6Cmb.setSize(new Dimension(120, 24));
      nonlinearParams6Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(5);
        }
      });
    }
    return nonlinearParams6Cmb;
  }

  /**
   * This method initializes nonlinearParams6REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams6REd() {
    if (nonlinearParams6REd == null) {
      nonlinearParams6REd = new JWFNumberField();
      nonlinearParams6REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(5);
        }
      });
      nonlinearParams6REd.setValueStep(0.05);
      nonlinearParams6REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams6REd.isMouseAdjusting() || nonlinearParams6REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(5);
          }
        }
      });
      nonlinearParams6REd.setLocation(new Point(188, 26));
      nonlinearParams6REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams6REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams6REd.setText("");
      nonlinearParams6REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams6REd;
  }

  /**
   * This method initializes nonlinearParams6LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams6LeftButton() {
    if (nonlinearParams6LeftButton == null) {
      nonlinearParams6LeftButton = new JButton();
      nonlinearParams6LeftButton.setLocation(new Point(269, 26));
      nonlinearParams6LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams6LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams6LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams6LeftButton.setText("");
      nonlinearParams6LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams6LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(5);
        }
      });
    }
    return nonlinearParams6LeftButton;
  }

  /**
   * This method initializes nonlinearVar7Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar7Panel() {
    if (nonlinearVar7Panel == null) {
      nonlinearParams7Lbl = new JLabel();
      nonlinearParams7Lbl.setLocation(new Point(14, 26));
      nonlinearParams7Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams7Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams7Lbl.setText("Params");
      nonlinearParams7Lbl.setSize(new Dimension(50, 22));
      nonlinearVar7Lbl = new JLabel();
      nonlinearVar7Lbl.setLocation(new Point(4, 2));
      nonlinearVar7Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar7Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar7Lbl.setText("Variation 7");
      nonlinearVar7Lbl.setSize(new Dimension(60, 22));
      nonlinearVar7Panel = new JPanel();
      nonlinearVar7Panel.setLayout(null);
      nonlinearVar7Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar7Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar7Panel.add(nonlinearVar7Lbl, null);
      nonlinearVar7Panel.add(getNonlinearVar7Cmb(), null);
      nonlinearVar7Panel.add(getNonlinearVar7REd(), null);
      nonlinearVar7Panel.add(nonlinearParams7Lbl, null);
      nonlinearVar7Panel.add(getNonlinearParams7Cmb(), null);
      nonlinearVar7Panel.add(getNonlinearParams7REd(), null);
      nonlinearVar7Panel.add(getNonlinearParams7LeftButton(), null);
    }
    return nonlinearVar7Panel;
  }

  /**
   * This method initializes nonlinearVar7Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar7Cmb() {
    if (nonlinearVar7Cmb == null) {
      nonlinearVar7Cmb = new JComboBox();
      nonlinearVar7Cmb.setLocation(new Point(66, 2));
      nonlinearVar7Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar7Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar7Cmb.setMaximumRowCount(22);
      nonlinearVar7Cmb.setSize(new Dimension(120, 24));
      nonlinearVar7Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(6);
        }
      });
    }
    return nonlinearVar7Cmb;
  }

  /**
   * This method initializes nonlinearVar7REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar7REd() {
    if (nonlinearVar7REd == null) {
      nonlinearVar7REd = new JWFNumberField();
      nonlinearVar7REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(6);
        }
      });
      nonlinearVar7REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar7REd.isMouseAdjusting() || nonlinearVar7REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(6);
          }
        }
      });
      nonlinearVar7REd.setValueStep(0.01);
      nonlinearVar7REd.setLocation(new Point(188, 2));
      nonlinearVar7REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar7REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar7REd.setText("");
      nonlinearVar7REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar7REd;
  }

  /**
   * This method initializes nonlinearParams7Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams7Cmb() {
    if (nonlinearParams7Cmb == null) {
      nonlinearParams7Cmb = new JComboBox();
      nonlinearParams7Cmb.setLocation(new Point(66, 26));
      nonlinearParams7Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams7Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams7Cmb.setSize(new Dimension(120, 24));
      nonlinearParams7Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(6);
        }
      });
    }
    return nonlinearParams7Cmb;
  }

  /**
   * This method initializes nonlinearParams7REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams7REd() {
    if (nonlinearParams7REd == null) {
      nonlinearParams7REd = new JWFNumberField();
      nonlinearParams7REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(6);
        }
      });
      nonlinearParams7REd.setValueStep(0.05);
      nonlinearParams7REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams7REd.isMouseAdjusting() || nonlinearParams7REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(6);
          }
        }
      });
      nonlinearParams7REd.setLocation(new Point(188, 26));
      nonlinearParams7REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams7REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams7REd.setText("");
      nonlinearParams7REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams7REd;
  }

  /**
   * This method initializes nonlinearParams7LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams7LeftButton() {
    if (nonlinearParams7LeftButton == null) {
      nonlinearParams7LeftButton = new JButton();
      nonlinearParams7LeftButton.setLocation(new Point(269, 26));
      nonlinearParams7LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams7LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams7LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams7LeftButton.setText("");
      nonlinearParams7LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams7LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(6);
        }
      });
    }
    return nonlinearParams7LeftButton;
  }

  /**
   * This method initializes nonlinearVar8Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar8Panel() {
    if (nonlinearVar8Panel == null) {
      nonlinearParams8Lbl = new JLabel();
      nonlinearParams8Lbl.setLocation(new Point(14, 26));
      nonlinearParams8Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams8Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams8Lbl.setText("Params");
      nonlinearParams8Lbl.setSize(new Dimension(50, 22));
      nonlinearVar8Lbl = new JLabel();
      nonlinearVar8Lbl.setLocation(new Point(4, 2));
      nonlinearVar8Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar8Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar8Lbl.setText("Variation 8");
      nonlinearVar8Lbl.setSize(new Dimension(60, 22));
      nonlinearVar8Panel = new JPanel();
      nonlinearVar8Panel.setLayout(null);
      nonlinearVar8Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar8Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar8Panel.add(nonlinearVar8Lbl, null);
      nonlinearVar8Panel.add(getNonlinearVar8Cmb(), null);
      nonlinearVar8Panel.add(getNonlinearVar8REd(), null);
      nonlinearVar8Panel.add(nonlinearParams8Lbl, null);
      nonlinearVar8Panel.add(getNonlinearParams8Cmb(), null);
      nonlinearVar8Panel.add(getNonlinearParams8REd(), null);
      nonlinearVar8Panel.add(getNonlinearParams8LeftButton(), null);
    }
    return nonlinearVar8Panel;
  }

  /**
   * This method initializes nonlinearVar8Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar8Cmb() {
    if (nonlinearVar8Cmb == null) {
      nonlinearVar8Cmb = new JComboBox();
      nonlinearVar8Cmb.setLocation(new Point(66, 2));
      nonlinearVar8Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar8Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar8Cmb.setMaximumRowCount(22);
      nonlinearVar8Cmb.setSize(new Dimension(120, 24));
      nonlinearVar8Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(7);
        }
      });
    }
    return nonlinearVar8Cmb;
  }

  /**
   * This method initializes nonlinearVar8REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar8REd() {
    if (nonlinearVar8REd == null) {
      nonlinearVar8REd = new JWFNumberField();
      nonlinearVar8REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(7);
        }
      });
      nonlinearVar8REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar8REd.isMouseAdjusting() || nonlinearVar8REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(7);
          }
        }
      });
      nonlinearVar8REd.setValueStep(0.01);
      nonlinearVar8REd.setLocation(new Point(188, 2));
      nonlinearVar8REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar8REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar8REd.setText("");
      nonlinearVar8REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar8REd;
  }

  /**
   * This method initializes nonlinearParams8Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams8Cmb() {
    if (nonlinearParams8Cmb == null) {
      nonlinearParams8Cmb = new JComboBox();
      nonlinearParams8Cmb.setLocation(new Point(66, 26));
      nonlinearParams8Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams8Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams8Cmb.setSize(new Dimension(120, 24));
      nonlinearParams8Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(7);
        }
      });
    }
    return nonlinearParams8Cmb;
  }

  /**
   * This method initializes nonlinearParams8REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams8REd() {
    if (nonlinearParams8REd == null) {
      nonlinearParams8REd = new JWFNumberField();
      nonlinearParams8REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(7);
        }
      });
      nonlinearParams8REd.setValueStep(0.05);
      nonlinearParams8REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams8REd.isMouseAdjusting() || nonlinearParams8REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(7);
          }
        }
      });
      nonlinearParams8REd.setLocation(new Point(188, 26));
      nonlinearParams8REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams8REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams8REd.setText("");
      nonlinearParams8REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams8REd;
  }

  /**
   * This method initializes nonlinearParams8LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams8LeftButton() {
    if (nonlinearParams8LeftButton == null) {
      nonlinearParams8LeftButton = new JButton();
      nonlinearParams8LeftButton.setLocation(new Point(269, 26));
      nonlinearParams8LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams8LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams8LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams8LeftButton.setText("");
      nonlinearParams8LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams8LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(7);
        }
      });
    }
    return nonlinearParams8LeftButton;
  }

  /**
   * This method initializes nonlinearVar9Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar9Panel() {
    if (nonlinearVar9Panel == null) {
      nonlinearParams9Lbl = new JLabel();
      nonlinearParams9Lbl.setLocation(new Point(14, 26));
      nonlinearParams9Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams9Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams9Lbl.setText("Params");
      nonlinearParams9Lbl.setSize(new Dimension(50, 22));
      nonlinearVar9Lbl = new JLabel();
      nonlinearVar9Lbl.setLocation(new Point(4, 2));
      nonlinearVar9Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar9Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar9Lbl.setText("Variation 9");
      nonlinearVar9Lbl.setSize(new Dimension(60, 22));
      nonlinearVar9Panel = new JPanel();
      nonlinearVar9Panel.setLayout(null);
      nonlinearVar9Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar9Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar9Panel.add(nonlinearVar9Lbl, null);
      nonlinearVar9Panel.add(getNonlinearVar9Cmb(), null);
      nonlinearVar9Panel.add(getNonlinearVar9REd(), null);
      nonlinearVar9Panel.add(nonlinearParams9Lbl, null);
      nonlinearVar9Panel.add(getNonlinearParams9Cmb(), null);
      nonlinearVar9Panel.add(getNonlinearParams9REd(), null);
      nonlinearVar9Panel.add(getNonlinearParams9LeftButton(), null);
    }
    return nonlinearVar9Panel;
  }

  /**
   * This method initializes nonlinearVar9Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar9Cmb() {
    if (nonlinearVar9Cmb == null) {
      nonlinearVar9Cmb = new JComboBox();
      nonlinearVar9Cmb.setLocation(new Point(66, 2));
      nonlinearVar9Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar9Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar9Cmb.setMaximumRowCount(22);
      nonlinearVar9Cmb.setSize(new Dimension(120, 24));
      nonlinearVar9Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(8);
        }
      });
    }
    return nonlinearVar9Cmb;
  }

  /**
   * This method initializes nonlinearVar9REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar9REd() {
    if (nonlinearVar9REd == null) {
      nonlinearVar9REd = new JWFNumberField();
      nonlinearVar9REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(8);
        }
      });
      nonlinearVar9REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar9REd.isMouseAdjusting() || nonlinearVar9REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(8);
          }
        }
      });
      nonlinearVar9REd.setValueStep(0.01);
      nonlinearVar9REd.setLocation(new Point(188, 2));
      nonlinearVar9REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar9REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar9REd.setText("");
      nonlinearVar9REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar9REd;
  }

  /**
   * This method initializes nonlinearParams9Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams9Cmb() {
    if (nonlinearParams9Cmb == null) {
      nonlinearParams9Cmb = new JComboBox();
      nonlinearParams9Cmb.setLocation(new Point(66, 26));
      nonlinearParams9Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams9Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams9Cmb.setSize(new Dimension(120, 24));
      nonlinearParams9Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(8);
        }
      });
    }
    return nonlinearParams9Cmb;
  }

  /**
   * This method initializes nonlinearParams9REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams9REd() {
    if (nonlinearParams9REd == null) {
      nonlinearParams9REd = new JWFNumberField();
      nonlinearParams9REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(8);
        }
      });
      nonlinearParams9REd.setValueStep(0.05);
      nonlinearParams9REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams9REd.isMouseAdjusting() || nonlinearParams9REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(8);
          }
        }
      });
      nonlinearParams9REd.setLocation(new Point(188, 26));
      nonlinearParams9REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams9REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams9REd.setText("");
      nonlinearParams9REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams9REd;
  }

  /**
   * This method initializes nonlinearParams9LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams9LeftButton() {
    if (nonlinearParams9LeftButton == null) {
      nonlinearParams9LeftButton = new JButton();
      nonlinearParams9LeftButton.setLocation(new Point(269, 26));
      nonlinearParams9LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams9LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams9LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams9LeftButton.setText("");
      nonlinearParams9LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams9LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(8);
        }
      });
    }
    return nonlinearParams9LeftButton;
  }

  /**
   * This method initializes nonlinearVar10Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar10Panel() {
    if (nonlinearVar10Panel == null) {
      nonlinearParams10Lbl = new JLabel();
      nonlinearParams10Lbl.setLocation(new Point(14, 26));
      nonlinearParams10Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams10Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams10Lbl.setText("Params");
      nonlinearParams10Lbl.setSize(new Dimension(50, 22));
      nonlinearVar10Lbl = new JLabel();
      nonlinearVar10Lbl.setLocation(new Point(4, 2));
      nonlinearVar10Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar10Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar10Lbl.setText("Variation 10");
      nonlinearVar10Lbl.setSize(new Dimension(60, 22));
      nonlinearVar10Panel = new JPanel();
      nonlinearVar10Panel.setLayout(null);
      nonlinearVar10Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar10Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar10Panel.add(nonlinearVar10Lbl, null);
      nonlinearVar10Panel.add(getNonlinearVar10Cmb(), null);
      nonlinearVar10Panel.add(getNonlinearVar10REd(), null);
      nonlinearVar10Panel.add(nonlinearParams10Lbl, null);
      nonlinearVar10Panel.add(getNonlinearParams10Cmb(), null);
      nonlinearVar10Panel.add(getNonlinearParams10REd(), null);
      nonlinearVar10Panel.add(getNonlinearParams10LeftButton(), null);
    }
    return nonlinearVar10Panel;
  }

  /**
   * This method initializes nonlinearVar10Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar10Cmb() {
    if (nonlinearVar10Cmb == null) {
      nonlinearVar10Cmb = new JComboBox();
      nonlinearVar10Cmb.setLocation(new Point(66, 2));
      nonlinearVar10Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar10Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar10Cmb.setMaximumRowCount(22);
      nonlinearVar10Cmb.setSize(new Dimension(120, 24));
      nonlinearVar10Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(9);
        }
      });
    }
    return nonlinearVar10Cmb;
  }

  /**
   * This method initializes nonlinearVar10REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar10REd() {
    if (nonlinearVar10REd == null) {
      nonlinearVar10REd = new JWFNumberField();
      nonlinearVar10REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(9);
        }
      });
      nonlinearVar10REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar10REd.isMouseAdjusting() || nonlinearVar10REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(9);
          }
        }
      });
      nonlinearVar10REd.setValueStep(0.01);
      nonlinearVar10REd.setLocation(new Point(188, 2));
      nonlinearVar10REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar10REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar10REd.setText("");
      nonlinearVar10REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar10REd;
  }

  /**
   * This method initializes nonlinearParams10Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams10Cmb() {
    if (nonlinearParams10Cmb == null) {
      nonlinearParams10Cmb = new JComboBox();
      nonlinearParams10Cmb.setLocation(new Point(66, 26));
      nonlinearParams10Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams10Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams10Cmb.setSize(new Dimension(120, 24));
      nonlinearParams10Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(9);
        }
      });
    }
    return nonlinearParams10Cmb;
  }

  /**
   * This method initializes nonlinearParams10REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams10REd() {
    if (nonlinearParams10REd == null) {
      nonlinearParams10REd = new JWFNumberField();
      nonlinearParams10REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(9);
        }
      });
      nonlinearParams10REd.setValueStep(0.05);
      nonlinearParams10REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams10REd.isMouseAdjusting() || nonlinearParams10REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(9);
          }
        }
      });
      nonlinearParams10REd.setLocation(new Point(188, 26));
      nonlinearParams10REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams10REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams10REd.setText("");
      nonlinearParams10REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams10REd;
  }

  /**
   * This method initializes nonlinearParams10LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams10LeftButton() {
    if (nonlinearParams10LeftButton == null) {
      nonlinearParams10LeftButton = new JButton();
      nonlinearParams10LeftButton.setLocation(new Point(269, 26));
      nonlinearParams10LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams10LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams10LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams10LeftButton.setText("");
      nonlinearParams10LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams10LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(9);
        }
      });
    }
    return nonlinearParams10LeftButton;
  }

  /**
   * This method initializes nonlinearVar11Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar11Panel() {
    if (nonlinearVar11Panel == null) {
      nonlinearParams11Lbl = new JLabel();
      nonlinearParams11Lbl.setLocation(new Point(14, 26));
      nonlinearParams11Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams11Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams11Lbl.setText("Params");
      nonlinearParams11Lbl.setSize(new Dimension(50, 22));
      nonlinearVar11Lbl = new JLabel();
      nonlinearVar11Lbl.setLocation(new Point(4, 2));
      nonlinearVar11Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar11Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar11Lbl.setText("Variation 11");
      nonlinearVar11Lbl.setSize(new Dimension(60, 22));
      nonlinearVar11Panel = new JPanel();
      nonlinearVar11Panel.setLayout(null);
      nonlinearVar11Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar11Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar11Panel.add(nonlinearVar11Lbl, null);
      nonlinearVar11Panel.add(getNonlinearVar11Cmb(), null);
      nonlinearVar11Panel.add(getNonlinearVar11REd(), null);
      nonlinearVar11Panel.add(nonlinearParams11Lbl, null);
      nonlinearVar11Panel.add(getNonlinearParams11Cmb(), null);
      nonlinearVar11Panel.add(getNonlinearParams11REd(), null);
      nonlinearVar11Panel.add(getNonlinearParams11LeftButton(), null);
    }
    return nonlinearVar11Panel;
  }

  /**
   * This method initializes nonlinearVar11Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar11Cmb() {
    if (nonlinearVar11Cmb == null) {
      nonlinearVar11Cmb = new JComboBox();
      nonlinearVar11Cmb.setLocation(new Point(66, 2));
      nonlinearVar11Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar11Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar11Cmb.setMaximumRowCount(22);
      nonlinearVar11Cmb.setSize(new Dimension(120, 24));
      nonlinearVar11Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(10);
        }
      });
    }
    return nonlinearVar11Cmb;
  }

  /**
   * This method initializes nonlinearVar11REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar11REd() {
    if (nonlinearVar11REd == null) {
      nonlinearVar11REd = new JWFNumberField();
      nonlinearVar11REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(10);
        }
      });
      nonlinearVar11REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar11REd.isMouseAdjusting() || nonlinearVar11REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(10);
          }
        }
      });
      nonlinearVar11REd.setValueStep(0.01);
      nonlinearVar11REd.setLocation(new Point(188, 2));
      nonlinearVar11REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar11REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar11REd.setText("");
      nonlinearVar11REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar11REd;
  }

  /**
   * This method initializes nonlinearParams11Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams11Cmb() {
    if (nonlinearParams11Cmb == null) {
      nonlinearParams11Cmb = new JComboBox();
      nonlinearParams11Cmb.setLocation(new Point(66, 26));
      nonlinearParams11Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams11Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams11Cmb.setSize(new Dimension(120, 24));
      nonlinearParams11Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(10);
        }
      });
    }
    return nonlinearParams11Cmb;
  }

  /**
   * This method initializes nonlinearParams11REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams11REd() {
    if (nonlinearParams11REd == null) {
      nonlinearParams11REd = new JWFNumberField();
      nonlinearParams11REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(10);
        }
      });
      nonlinearParams11REd.setValueStep(0.05);
      nonlinearParams11REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams11REd.isMouseAdjusting() || nonlinearParams11REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(10);
          }
        }
      });
      nonlinearParams11REd.setLocation(new Point(188, 26));
      nonlinearParams11REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams11REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams11REd.setText("");
      nonlinearParams11REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams11REd;
  }

  /**
   * This method initializes nonlinearParams11LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams11LeftButton() {
    if (nonlinearParams11LeftButton == null) {
      nonlinearParams11LeftButton = new JButton();
      nonlinearParams11LeftButton.setLocation(new Point(269, 26));
      nonlinearParams11LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams11LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams11LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams11LeftButton.setText("");
      nonlinearParams11LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams11LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(10);
        }
      });
    }
    return nonlinearParams11LeftButton;
  }

  /**
   * This method initializes nonlinearVar12Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar12Panel() {
    if (nonlinearVar12Panel == null) {
      nonlinearParams12Lbl = new JLabel();
      nonlinearParams12Lbl.setLocation(new Point(14, 26));
      nonlinearParams12Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams12Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams12Lbl.setText("Params");
      nonlinearParams12Lbl.setSize(new Dimension(50, 22));
      nonlinearVar12Lbl = new JLabel();
      nonlinearVar12Lbl.setLocation(new Point(4, 2));
      nonlinearVar12Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar12Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar12Lbl.setText("Variation 12");
      nonlinearVar12Lbl.setSize(new Dimension(60, 22));
      nonlinearVar12Panel = new JPanel();
      nonlinearVar12Panel.setLayout(null);
      nonlinearVar12Panel.setPreferredSize(new Dimension(292, 52));
      nonlinearVar12Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar12Panel.add(nonlinearVar12Lbl, null);
      nonlinearVar12Panel.add(getNonlinearVar12Cmb(), null);
      nonlinearVar12Panel.add(getNonlinearVar12REd(), null);
      nonlinearVar12Panel.add(nonlinearParams12Lbl, null);
      nonlinearVar12Panel.add(getNonlinearParams12Cmb(), null);
      nonlinearVar12Panel.add(getNonlinearParams12REd(), null);
      nonlinearVar12Panel.add(getNonlinearParams12LeftButton(), null);
    }
    return nonlinearVar12Panel;
  }

  /**
   * This method initializes nonlinearVar12Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearVar12Cmb() {
    if (nonlinearVar12Cmb == null) {
      nonlinearVar12Cmb = new JComboBox();
      nonlinearVar12Cmb.setLocation(new Point(66, 2));
      nonlinearVar12Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar12Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar12Cmb.setMaximumRowCount(22);
      nonlinearVar12Cmb.setSize(new Dimension(120, 24));
      nonlinearVar12Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearVarCmbChanged(11);
        }
      });
    }
    return nonlinearVar12Cmb;
  }

  /**
   * This method initializes nonlinearVar12REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearVar12REd() {
    if (nonlinearVar12REd == null) {
      nonlinearVar12REd = new JWFNumberField();
      nonlinearVar12REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearVarEditMotionCurve(11);
        }
      });
      nonlinearVar12REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearVar12REd.isMouseAdjusting() || nonlinearVar12REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearVarREdChanged(11);
          }
        }
      });
      nonlinearVar12REd.setValueStep(0.01);
      nonlinearVar12REd.setLocation(new Point(188, 2));
      nonlinearVar12REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar12REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar12REd.setText("");
      nonlinearVar12REd.setSize(new Dimension(81, 24));
    }
    return nonlinearVar12REd;
  }

  /**
   * This method initializes nonlinearParams12Cmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getNonlinearParams12Cmb() {
    if (nonlinearParams12Cmb == null) {
      nonlinearParams12Cmb = new JComboBox();
      nonlinearParams12Cmb.setLocation(new Point(66, 26));
      nonlinearParams12Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams12Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams12Cmb.setSize(new Dimension(120, 24));
      nonlinearParams12Cmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.nonlinearParamsCmbChanged(11);
        }
      });
    }
    return nonlinearParams12Cmb;
  }

  /**
   * This method initializes nonlinearParams12REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getNonlinearParams12REd() {
    if (nonlinearParams12REd == null) {
      nonlinearParams12REd = new JWFNumberField();
      nonlinearParams12REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.nonlinearParamsEditMotionCurve(11);
        }
      });
      nonlinearParams12REd.setValueStep(0.05);
      nonlinearParams12REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!nonlinearParams12REd.isMouseAdjusting() || nonlinearParams12REd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.nonlinearParamsREdChanged(11);
          }
        }
      });
      nonlinearParams12REd.setLocation(new Point(188, 26));
      nonlinearParams12REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams12REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams12REd.setText("");
      nonlinearParams12REd.setSize(new Dimension(81, 24));
    }
    return nonlinearParams12REd;
  }

  /**
   * This method initializes nonlinearParams12LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams12LeftButton() {
    if (nonlinearParams12LeftButton == null) {
      nonlinearParams12LeftButton = new JButton();
      nonlinearParams12LeftButton.setLocation(new Point(269, 26));
      nonlinearParams12LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams12LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams12LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams12LeftButton.setText("");
      nonlinearParams12LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams12LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsLeftButtonClicked(11);
        }
      });
    }
    return nonlinearParams12LeftButton;
  }

  /**
   * This method initializes tinaGrabPaletteFromFlameButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getTinaGrabPaletteFromFlameButton() {
    if (tinaGrabPaletteFromFlameButton == null) {
      tinaGrabPaletteFromFlameButton = new JButton();
      tinaGrabPaletteFromFlameButton.setBounds(5, 54, 148, 24);
      tinaGrabPaletteFromFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGrabPaletteFromFlameButton.setText("Extract from flame");
      tinaGrabPaletteFromFlameButton.setPreferredSize(new Dimension(190, 24));
      tinaGrabPaletteFromFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.grabPaletteFromFlameButton_actionPerformed(e);
        }
      });
    }
    return tinaGrabPaletteFromFlameButton;
  }

  /**
   * This method initializes pseudo3DShadingPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPseudo3DShadingPanel() {
    if (pseudo3DShadingPanel == null) {
      shadingLightBlueLbl = new JLabel();
      shadingLightBlueLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightBlueLbl.setText("Blue");
      shadingLightBlueLbl.setSize(new Dimension(94, 22));
      shadingLightBlueLbl.setLocation(new Point(664, 74));
      shadingLightBlueLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightGreenLbl = new JLabel();
      shadingLightGreenLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightGreenLbl.setText("Green");
      shadingLightGreenLbl.setSize(new Dimension(94, 22));
      shadingLightGreenLbl.setLocation(new Point(664, 50));
      shadingLightGreenLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightRedLbl = new JLabel();
      shadingLightRedLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightRedLbl.setText("Red");
      shadingLightRedLbl.setSize(new Dimension(94, 22));
      shadingLightRedLbl.setLocation(new Point(664, 26));
      shadingLightRedLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightZLbl = new JLabel();
      shadingLightZLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightZLbl.setText("Z position");
      shadingLightZLbl.setSize(new Dimension(94, 22));
      shadingLightZLbl.setLocation(new Point(334, 74));
      shadingLightZLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightYLbl = new JLabel();
      shadingLightYLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightYLbl.setText("Y position");
      shadingLightYLbl.setSize(new Dimension(94, 22));
      shadingLightYLbl.setLocation(new Point(334, 50));
      shadingLightYLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightXLbl = new JLabel();
      shadingLightXLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightXLbl.setText("X position");
      shadingLightXLbl.setSize(new Dimension(94, 22));
      shadingLightXLbl.setLocation(new Point(334, 26));
      shadingLightXLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightLbl = new JLabel();
      shadingLightLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightLbl.setText("Light source");
      shadingLightLbl.setSize(new Dimension(94, 22));
      shadingLightLbl.setLocation(new Point(334, 2));
      shadingLightLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingPhongSizeLbl = new JLabel();
      shadingPhongSizeLbl.setPreferredSize(new Dimension(94, 22));
      shadingPhongSizeLbl.setText("Phong size");
      shadingPhongSizeLbl.setSize(new Dimension(94, 22));
      shadingPhongSizeLbl.setLocation(new Point(4, 76));
      shadingPhongSizeLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingPhongLbl = new JLabel();
      shadingPhongLbl.setPreferredSize(new Dimension(94, 22));
      shadingPhongLbl.setText("Phong");
      shadingPhongLbl.setSize(new Dimension(94, 22));
      shadingPhongLbl.setLocation(new Point(4, 52));
      shadingPhongLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingAmbientLbl = new JLabel();
      shadingAmbientLbl.setPreferredSize(new Dimension(94, 22));
      shadingAmbientLbl.setText("Ambient");
      shadingAmbientLbl.setSize(new Dimension(94, 22));
      shadingAmbientLbl.setLocation(new Point(4, 4));
      shadingAmbientLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingDiffuseLbl = new JLabel();
      shadingDiffuseLbl.setPreferredSize(new Dimension(94, 22));
      shadingDiffuseLbl.setText("Diffuse");
      shadingDiffuseLbl.setSize(new Dimension(94, 22));
      shadingDiffuseLbl.setLocation(new Point(4, 28));
      shadingDiffuseLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      pseudo3DShadingPanel = new JPanel();
      pseudo3DShadingPanel.setLayout(null);
      pseudo3DShadingPanel.add(shadingDiffuseLbl, null);
      pseudo3DShadingPanel.add(getShadingAmbientREd(), null);
      pseudo3DShadingPanel.add(shadingAmbientLbl, null);
      pseudo3DShadingPanel.add(getShadingDiffuseREd(), null);
      pseudo3DShadingPanel.add(getShadingAmbientSlider(), null);
      pseudo3DShadingPanel.add(getShadingDiffuseSlider(), null);
      pseudo3DShadingPanel.add(shadingPhongLbl, null);
      pseudo3DShadingPanel.add(getShadingPhongREd(), null);
      pseudo3DShadingPanel.add(getShadingPhongSlider(), null);
      pseudo3DShadingPanel.add(shadingPhongSizeLbl, null);
      pseudo3DShadingPanel.add(getShadingPhongSizeREd(), null);
      pseudo3DShadingPanel.add(getShadingPhongSizeSlider(), null);
      pseudo3DShadingPanel.add(shadingLightLbl, null);
      pseudo3DShadingPanel.add(getShadingLightCmb(), null);
      pseudo3DShadingPanel.add(shadingLightXLbl, null);
      pseudo3DShadingPanel.add(getShadingLightXREd(), null);
      pseudo3DShadingPanel.add(getShadingLightXSlider(), null);
      pseudo3DShadingPanel.add(shadingLightYLbl, null);
      pseudo3DShadingPanel.add(getShadingLightYREd(), null);
      pseudo3DShadingPanel.add(getShadingLightYSlider(), null);
      pseudo3DShadingPanel.add(shadingLightZLbl, null);
      pseudo3DShadingPanel.add(getShadingLightZREd(), null);
      pseudo3DShadingPanel.add(getShadingLightZSlider(), null);
      pseudo3DShadingPanel.add(shadingLightRedLbl, null);
      pseudo3DShadingPanel.add(getShadingLightRedREd(), null);
      pseudo3DShadingPanel.add(getShadingLightRedSlider(), null);
      pseudo3DShadingPanel.add(shadingLightGreenLbl, null);
      pseudo3DShadingPanel.add(getShadingLightGreenREd(), null);
      pseudo3DShadingPanel.add(getShadingLightGreenSlider(), null);
      pseudo3DShadingPanel.add(shadingLightBlueLbl, null);
      pseudo3DShadingPanel.add(getShadingLightBlueREd(), null);
      pseudo3DShadingPanel.add(getShadingLightBlueSlider(), null);
    }
    return pseudo3DShadingPanel;
  }

  /**
   * This method initializes shadingAmbientREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingAmbientREd() {
    if (shadingAmbientREd == null) {
      shadingAmbientREd = new JWFNumberField();
      shadingAmbientREd.setHasMaxValue(true);
      shadingAmbientREd.setHasMinValue(true);
      shadingAmbientREd.setMaxValue(1.0);
      shadingAmbientREd.setValueStep(0.01);
      shadingAmbientREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingAmbientREd.isMouseAdjusting() || shadingAmbientREd.getMouseChangeCount() == 0) {
            if (!shadingAmbientSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingAmbientREd_changed();
        }
      });
      shadingAmbientREd.setPreferredSize(new Dimension(100, 24));
      shadingAmbientREd.setText("");
      shadingAmbientREd.setSize(new Dimension(100, 24));
      shadingAmbientREd.setLocation(new Point(100, 4));
      shadingAmbientREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingAmbientREd;
  }

  /**
   * This method initializes shadingDiffuseREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingDiffuseREd() {
    if (shadingDiffuseREd == null) {
      shadingDiffuseREd = new JWFNumberField();
      shadingDiffuseREd.setValueStep(0.01);
      shadingDiffuseREd.setMaxValue(1.0);
      shadingDiffuseREd.setHasMinValue(true);
      shadingDiffuseREd.setHasMaxValue(true);
      shadingDiffuseREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingDiffuseREd.isMouseAdjusting() || shadingDiffuseREd.getMouseChangeCount() == 0) {
            if (!shadingDiffuseSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingDiffuseREd_changed();
        }
      });
      shadingDiffuseREd.setPreferredSize(new Dimension(100, 24));
      shadingDiffuseREd.setText("");
      shadingDiffuseREd.setSize(new Dimension(100, 24));
      shadingDiffuseREd.setLocation(new Point(100, 28));
      shadingDiffuseREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingDiffuseREd;
  }

  /**
   * This method initializes shadingAmbientSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingAmbientSlider() {
    if (shadingAmbientSlider == null) {
      shadingAmbientSlider = new JSlider();
      shadingAmbientSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingAmbientSlider.setMaximum(100);
      shadingAmbientSlider.setMinimum(0);
      shadingAmbientSlider.setValue(0);
      shadingAmbientSlider.setLocation(new Point(202, 4));
      shadingAmbientSlider.setSize(new Dimension(120, 19));
      shadingAmbientSlider.setPreferredSize(new Dimension(120, 19));
      shadingAmbientSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingAmbientSlider_changed();
        }
      });
    }
    return shadingAmbientSlider;
  }

  /**
   * This method initializes shadingDiffuseSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingDiffuseSlider() {
    if (shadingDiffuseSlider == null) {
      shadingDiffuseSlider = new JSlider();
      shadingDiffuseSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDiffuseSlider.setMaximum(100);
      shadingDiffuseSlider.setMinimum(0);
      shadingDiffuseSlider.setValue(0);
      shadingDiffuseSlider.setSize(new Dimension(120, 19));
      shadingDiffuseSlider.setLocation(new Point(202, 28));
      shadingDiffuseSlider.setPreferredSize(new Dimension(120, 19));
      shadingDiffuseSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingDiffuseSlider_changed();
        }
      });
    }
    return shadingDiffuseSlider;
  }

  /**
   * This method initializes shadingPhongREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingPhongREd() {
    if (shadingPhongREd == null) {
      shadingPhongREd = new JWFNumberField();
      shadingPhongREd.setValueStep(0.01);
      shadingPhongREd.setHasMaxValue(true);
      shadingPhongREd.setHasMinValue(true);
      shadingPhongREd.setMaxValue(1.0);
      shadingPhongREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingPhongREd.isMouseAdjusting() || shadingPhongREd.getMouseChangeCount() == 0) {
            if (!shadingPhongSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingPhongREd_changed();
        }
      });
      shadingPhongREd.setPreferredSize(new Dimension(100, 24));
      shadingPhongREd.setText("");
      shadingPhongREd.setSize(new Dimension(100, 24));
      shadingPhongREd.setLocation(new Point(100, 52));
      shadingPhongREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingPhongREd;
  }

  /**
   * This method initializes shadingPhongSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingPhongSlider() {
    if (shadingPhongSlider == null) {
      shadingPhongSlider = new JSlider();
      shadingPhongSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingPhongSlider.setMaximum(100);
      shadingPhongSlider.setMinimum(0);
      shadingPhongSlider.setValue(0);
      shadingPhongSlider.setSize(new Dimension(120, 19));
      shadingPhongSlider.setLocation(new Point(202, 52));
      shadingPhongSlider.setPreferredSize(new Dimension(120, 19));
      shadingPhongSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingPhongSlider_changed();
        }
      });
    }
    return shadingPhongSlider;
  }

  /**
   * This method initializes shadingPhongSizeREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingPhongSizeREd() {
    if (shadingPhongSizeREd == null) {
      shadingPhongSizeREd = new JWFNumberField();
      shadingPhongSizeREd.setHasMinValue(true);
      shadingPhongSizeREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingPhongSizeREd.isMouseAdjusting() || shadingPhongSizeREd.getMouseChangeCount() == 0) {
            if (!shadingPhongSizeSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingPhongSizeREd_changed();
        }
      });
      shadingPhongSizeREd.setPreferredSize(new Dimension(100, 24));
      shadingPhongSizeREd.setText("");
      shadingPhongSizeREd.setSize(new Dimension(100, 24));
      shadingPhongSizeREd.setLocation(new Point(100, 76));
      shadingPhongSizeREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingPhongSizeREd;
  }

  /**
   * This method initializes shadingPhongSizeSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingPhongSizeSlider() {
    if (shadingPhongSizeSlider == null) {
      shadingPhongSizeSlider = new JSlider();
      shadingPhongSizeSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingPhongSizeSlider.setMaximum(1000);
      shadingPhongSizeSlider.setMinimum(0);
      shadingPhongSizeSlider.setValue(0);
      shadingPhongSizeSlider.setSize(new Dimension(120, 19));
      shadingPhongSizeSlider.setLocation(new Point(202, 76));
      shadingPhongSizeSlider.setPreferredSize(new Dimension(120, 19));
      shadingPhongSizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingPhongSizeSlider_changed();
        }
      });
    }
    return shadingPhongSizeSlider;
  }

  /**
   * This method initializes shadingLightCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getShadingLightCmb() {
    if (shadingLightCmb == null) {
      shadingLightCmb = new JComboBox();
      shadingLightCmb.setPreferredSize(new Dimension(125, 22));
      shadingLightCmb.setSelectedItem("1");
      shadingLightCmb.setSize(new Dimension(125, 22));
      shadingLightCmb.setLocation(new Point(430, 2));
      shadingLightCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightCmb.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getFlameControls().shadingLightCmb_changed();
        }
      });
    }
    return shadingLightCmb;
  }

  /**
   * This method initializes shadingLightXREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingLightXREd() {
    if (shadingLightXREd == null) {
      shadingLightXREd = new JWFNumberField();
      shadingLightXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingLightXREd.isMouseAdjusting() || shadingLightXREd.getMouseChangeCount() == 0) {
            if (!shadingLightXSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingLightXREd_changed();
        }
      });
      shadingLightXREd.setPreferredSize(new Dimension(100, 24));
      shadingLightXREd.setText("");
      shadingLightXREd.setSize(new Dimension(100, 24));
      shadingLightXREd.setLocation(new Point(430, 26));
      shadingLightXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingLightXREd;
  }

  /**
   * This method initializes shadingLightXSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingLightXSlider() {
    if (shadingLightXSlider == null) {
      shadingLightXSlider = new JSlider();
      shadingLightXSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingLightXSlider.setMaximum(2000);
      shadingLightXSlider.setMinimum(-2000);
      shadingLightXSlider.setValue(0);
      shadingLightXSlider.setSize(new Dimension(120, 19));
      shadingLightXSlider.setLocation(new Point(532, 26));
      shadingLightXSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightXSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingLightXSlider_changed();
        }
      });
    }
    return shadingLightXSlider;
  }

  /**
   * This method initializes shadingLightYREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingLightYREd() {
    if (shadingLightYREd == null) {
      shadingLightYREd = new JWFNumberField();
      shadingLightYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingLightYREd.isMouseAdjusting() || shadingLightYREd.getMouseChangeCount() == 0) {
            if (!shadingLightYSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingLightYREd_changed();
        }
      });
      shadingLightYREd.setPreferredSize(new Dimension(100, 24));
      shadingLightYREd.setText("");
      shadingLightYREd.setSize(new Dimension(100, 24));
      shadingLightYREd.setLocation(new Point(430, 50));
      shadingLightYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingLightYREd;
  }

  /**
   * This method initializes shadingLightYSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingLightYSlider() {
    if (shadingLightYSlider == null) {
      shadingLightYSlider = new JSlider();
      shadingLightYSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingLightYSlider.setMaximum(2000);
      shadingLightYSlider.setMinimum(-2000);
      shadingLightYSlider.setValue(0);
      shadingLightYSlider.setSize(new Dimension(120, 19));
      shadingLightYSlider.setLocation(new Point(532, 50));
      shadingLightYSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightYSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingLightYSlider_changed();
        }
      });
    }
    return shadingLightYSlider;
  }

  /**
   * This method initializes shadingLightZREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingLightZREd() {
    if (shadingLightZREd == null) {
      shadingLightZREd = new JWFNumberField();
      shadingLightZREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingLightZREd.isMouseAdjusting() || shadingLightZREd.getMouseChangeCount() == 0) {
            if (!shadingLightZSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingLightZREd_changed();
        }
      });
      shadingLightZREd.setPreferredSize(new Dimension(100, 24));
      shadingLightZREd.setText("");
      shadingLightZREd.setSize(new Dimension(100, 24));
      shadingLightZREd.setLocation(new Point(430, 74));
      shadingLightZREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingLightZREd;
  }

  /**
   * This method initializes shadingLightZSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingLightZSlider() {
    if (shadingLightZSlider == null) {
      shadingLightZSlider = new JSlider();
      shadingLightZSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingLightZSlider.setMaximum(2000);
      shadingLightZSlider.setMinimum(-2000);
      shadingLightZSlider.setValue(0);
      shadingLightZSlider.setSize(new Dimension(120, 19));
      shadingLightZSlider.setLocation(new Point(532, 74));
      shadingLightZSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightZSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingLightZSlider_changed();
        }
      });
    }
    return shadingLightZSlider;
  }

  /**
   * This method initializes shadingLightRedREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingLightRedREd() {
    if (shadingLightRedREd == null) {
      shadingLightRedREd = new JWFNumberField();
      shadingLightRedREd.setValueStep(1.0);
      shadingLightRedREd.setOnlyIntegers(true);
      shadingLightRedREd.setMaxValue(255.0);
      shadingLightRedREd.setHasMaxValue(true);
      shadingLightRedREd.setHasMinValue(true);
      shadingLightRedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingLightRedREd.isMouseAdjusting() || shadingLightRedREd.getMouseChangeCount() == 0) {
            if (!shadingLightRedSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingLightRedREd_changed();
        }
      });
      shadingLightRedREd.setPreferredSize(new Dimension(100, 24));
      shadingLightRedREd.setText("");
      shadingLightRedREd.setSize(new Dimension(100, 24));
      shadingLightRedREd.setLocation(new Point(760, 26));
      shadingLightRedREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingLightRedREd;
  }

  /**
   * This method initializes shadingLightRedSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingLightRedSlider() {
    if (shadingLightRedSlider == null) {
      shadingLightRedSlider = new JSlider();
      shadingLightRedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingLightRedSlider.setMaximum(255);
      shadingLightRedSlider.setMinimum(0);
      shadingLightRedSlider.setValue(0);
      shadingLightRedSlider.setLocation(new Point(862, 26));
      shadingLightRedSlider.setSize(new Dimension(120, 19));
      shadingLightRedSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightRedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingLightRedSlider_changed();
        }
      });
    }
    return shadingLightRedSlider;
  }

  /**
   * This method initializes shadingLightGreenREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingLightGreenREd() {
    if (shadingLightGreenREd == null) {
      shadingLightGreenREd = new JWFNumberField();
      shadingLightGreenREd.setValueStep(1.0);
      shadingLightGreenREd.setOnlyIntegers(true);
      shadingLightGreenREd.setMaxValue(255.0);
      shadingLightGreenREd.setHasMinValue(true);
      shadingLightGreenREd.setHasMaxValue(true);
      shadingLightGreenREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingLightGreenREd.isMouseAdjusting() || shadingLightGreenREd.getMouseChangeCount() == 0) {
            if (!shadingLightGreenSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingLightGreenREd_changed();
        }
      });
      shadingLightGreenREd.setPreferredSize(new Dimension(100, 24));
      shadingLightGreenREd.setText("");
      shadingLightGreenREd.setSize(new Dimension(100, 24));
      shadingLightGreenREd.setLocation(new Point(760, 50));
      shadingLightGreenREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingLightGreenREd;
  }

  /**
   * This method initializes shadingLightGreenSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingLightGreenSlider() {
    if (shadingLightGreenSlider == null) {
      shadingLightGreenSlider = new JSlider();
      shadingLightGreenSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingLightGreenSlider.setMaximum(255);
      shadingLightGreenSlider.setMinimum(0);
      shadingLightGreenSlider.setValue(0);
      shadingLightGreenSlider.setSize(new Dimension(120, 19));
      shadingLightGreenSlider.setLocation(new Point(862, 50));
      shadingLightGreenSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightGreenSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingLightGreenSlider_changed();
        }
      });
    }
    return shadingLightGreenSlider;
  }

  /**
   * This method initializes shadingLightBlueREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingLightBlueREd() {
    if (shadingLightBlueREd == null) {
      shadingLightBlueREd = new JWFNumberField();
      shadingLightBlueREd.setValueStep(1.0);
      shadingLightBlueREd.setMaxValue(255.0);
      shadingLightBlueREd.setHasMinValue(true);
      shadingLightBlueREd.setHasMaxValue(true);
      shadingLightBlueREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingLightBlueREd.isMouseAdjusting() || shadingLightBlueREd.getMouseChangeCount() == 0) {
            if (!shadingLightBlueSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingLightBlueREd_changed();
        }
      });
      shadingLightBlueREd.setPreferredSize(new Dimension(100, 24));
      shadingLightBlueREd.setText("");
      shadingLightBlueREd.setSize(new Dimension(100, 24));
      shadingLightBlueREd.setLocation(new Point(760, 74));
      shadingLightBlueREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingLightBlueREd;
  }

  /**
   * This method initializes shadingLightBlueSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingLightBlueSlider() {
    if (shadingLightBlueSlider == null) {
      shadingLightBlueSlider = new JSlider();
      shadingLightBlueSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingLightBlueSlider.setMaximum(255);
      shadingLightBlueSlider.setMinimum(0);
      shadingLightBlueSlider.setValue(0);
      shadingLightBlueSlider.setSize(new Dimension(120, 19));
      shadingLightBlueSlider.setLocation(new Point(862, 74));
      shadingLightBlueSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightBlueSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingLightBlueSlider_changed();
        }
      });
    }
    return shadingLightBlueSlider;
  }

  /**
   * This method initializes settingsPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getInteractiveRenderPanel() {
    if (interactiveRenderPanel == null) {
      interactiveRenderPanel = new JPanel();
      interactiveRenderPanel.setLayout(new BorderLayout(0, 0));
      interactiveRenderPanel.add(getInteractiveNorthPanel(), BorderLayout.SOUTH);
      interactiveRenderPanel.add(getInteractiveWestPanel(), BorderLayout.WEST);
      interactiveRenderPanel.add(getInteractiveEastPanel(), BorderLayout.EAST);
      interactiveRenderPanel.add(getInteractiveCenterPanel(), BorderLayout.CENTER);
    }
    return interactiveRenderPanel;
  }

  /**
   * This method initializes loadFromClipboardFlameButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getLoadFromClipboardFlameButton() {
    if (loadFromClipboardFlameButton == null) {
      loadFromClipboardFlameButton = new JButton();
      loadFromClipboardFlameButton.setMaximumSize(new Dimension(32000, 24));
      loadFromClipboardFlameButton.setMinimumSize(new Dimension(100, 24));
      loadFromClipboardFlameButton.setPreferredSize(new Dimension(125, 24));
      loadFromClipboardFlameButton.setText("From Clipboard");
      loadFromClipboardFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      loadFromClipboardFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.loadFlameFromClipboard();
        }
      });
    }
    return loadFromClipboardFlameButton;
  }

  /**
   * This method initializes saveFlameToClipboardButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getSaveFlameToClipboardButton() {
    if (saveFlameToClipboardButton == null) {
      saveFlameToClipboardButton = new JButton();
      saveFlameToClipboardButton.setIconTextGap(2);
      saveFlameToClipboardButton.setMinimumSize(new Dimension(100, 24));
      saveFlameToClipboardButton.setMaximumSize(new Dimension(32000, 24));
      saveFlameToClipboardButton.setPreferredSize(new Dimension(125, 24));
      saveFlameToClipboardButton.setText("To Clipboard");
      saveFlameToClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      saveFlameToClipboardButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.saveFlameToClipboard();
        }
      });
    }
    return saveFlameToClipboardButton;
  }

  /**
   * This method initializes mouseTransformSlowButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getMouseTransformSlowButton() {
    if (mouseTransformSlowButton == null) {
      mouseTransformSlowButton = new JToggleButton();
      mouseTransformSlowButton.setFont(new Font("Dialog", Font.BOLD, 9));
      mouseTransformSlowButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/click.png")));
      mouseTransformSlowButton.setSelected(false);
      mouseTransformSlowButton.setText("Fine");
      mouseTransformSlowButton.setSize(new Dimension(68, 24));
      mouseTransformSlowButton.setLocation(new Point(417, 4));
      mouseTransformSlowButton.setToolTipText("Toggle fine triangle adjustment mode");
      mouseTransformSlowButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformSlowButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.mouseTransformSlowButton_clicked();
        }
      });
    }
    return mouseTransformSlowButton;
  }

  /**
   * This method initializes batchRenderPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getBatchRenderPanel() {
    if (batchRenderPanel == null) {
      batchRenderPanel = new JPanel();
      batchRenderPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new EmptyBorder(10, 10, 10, 10));
      batchRenderPanel.add(panel_1);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(160, 10));
      panel_1.add(panel_2, BorderLayout.EAST);
      panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
      panel_2.add(getBatchPreviewRootPanel());
      panel_2.add(getBatchRenderAddFilesButton());
      panel_2.add(getPanel_20());
      panel_2.add(getBatchRenderFilesMoveDownButton());
      panel_2.add(getBatchRenderFilesMoveUpButton());
      panel_2.add(getPanel_21());
      panel_2.add(getBatchRenderFilesRemoveButton());
      panel_2.add(getBatchRenderFilesRemoveAllButton());
      panel_2.add(getPanel_22());

      batchResolutionProfileCmb = new JComboBox();
      batchResolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getBatchRendererController().batchRendererResolutionProfileCmb_changed();
          }
        }
      });
      batchResolutionProfileCmb.setMinimumSize(new Dimension(159, 24));
      batchResolutionProfileCmb.setMaximumSize(new Dimension(159, 24));
      panel_2.add(batchResolutionProfileCmb);
      batchResolutionProfileCmb.setPreferredSize(new Dimension(159, 24));
      batchResolutionProfileCmb.setMaximumRowCount(32);
      batchResolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));

      batchQualityProfileCmb = new JComboBox();
      panel_2.add(batchQualityProfileCmb);
      batchQualityProfileCmb.setMinimumSize(new Dimension(159, 24));
      batchQualityProfileCmb.setMaximumSize(new Dimension(159, 24));
      batchQualityProfileCmb.setPreferredSize(new Dimension(159, 24));
      batchQualityProfileCmb.setMaximumRowCount(32);
      batchQualityProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));

      JPanel panel_8 = new JPanel();
      panel_8.setMaximumSize(new Dimension(159, 28));
      panel_2.add(panel_8);
      panel_8.setLayout(null);

      batchRenderOverrideCBx = new JCheckBox("Overwrite images");
      batchRenderOverrideCBx.setBounds(4, 4, 149, 18);
      panel_8.add(batchRenderOverrideCBx);
      batchRenderOverrideCBx.setToolTipText("Overwrite already rendered images");

      JPanel panel_9 = new JPanel();
      panel_9.setMaximumSize(new Dimension(159, 36));
      panel_2.add(panel_9);

      batchRenderShowImageBtn = new JButton();
      batchRenderShowImageBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getBatchRendererController().showImageBtn_clicked();
        }
      });
      batchRenderShowImageBtn.setToolTipText("Show rendered image (if there is one)");
      batchRenderShowImageBtn.setText("Show image");
      batchRenderShowImageBtn.setPreferredSize(new Dimension(159, 24));
      batchRenderShowImageBtn.setMinimumSize(new Dimension(159, 12));
      batchRenderShowImageBtn.setMaximumSize(new Dimension(159, 24));
      batchRenderShowImageBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderShowImageBtn.setAlignmentX(0.5f);
      panel_2.add(batchRenderShowImageBtn);

      JPanel panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(10, 100));
      panel_1.add(panel_3, BorderLayout.SOUTH);
      panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));
      panel_3.add(getPanel_25());
      panel_3.add(getPanel_26());
      panel_3.add(getBatchRenderStartButton());
      panel_1.add(getRenderBatchJobsScrollPane(), BorderLayout.CENTER);
    }
    return batchRenderPanel;
  }

  /**
   * This method initializes rootTabbedPane	
   * 	
   * @return javax.swing.JTabbedPane	
   */
  private JTabbedPane getRootTabbedPane() {
    if (rootTabbedPane == null) {
      rootTabbedPane = new JTabbedPane();
      rootTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      rootTabbedPane.setEnabled(true);
      rootTabbedPane.addTab("Flame Editor ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/brick2.png")), getRootPanel(), null);
      rootTabbedPane.addTab("Interactive Renderer ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive.png")), getInteractiveRenderPanel(), null);
      rootTabbedPane.addTab("MutaGen ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/kdissert.png")), getPanel_16(), null);
      rootTabbedPane.addTab("Flame browser ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/application-view-tile.png")), getPanel_72(), null);
      rootTabbedPane.addTab("Easy Movie Maker ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-multimedia.png")), getTinaSWFAnimatorPanel(), null);
      rootTabbedPane.addTab("Dancing Flames Movies ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/kipina.png")), getPanel_36(), null);
      rootTabbedPane.addTab("Batch Flame Renderer ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/images.png")), getBatchRenderPanel(), null);
      rootTabbedPane.addTab("3DMesh Generation ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/sports-soccer.png")), getPanel_88(), null);
      // "FFmpeg Video Encoder" "emblem-videos.png"
      JPanel helpPanel = new JPanel();
      rootTabbedPane.addTab("Hints, Help and About ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-3.png")), helpPanel, null);
      helpPanel.setLayout(new BorderLayout(0, 0));

      JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
      helpPanel.add(tabbedPane_1);

      JPanel panel_1 = new JPanel();
      tabbedPane_1.addTab("About JWildfire ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-3.png")), panel_1, null);
      panel_1.setLayout(new BorderLayout(0, 0));
      panel_1.add(getScrollPane(), BorderLayout.CENTER);
      tabbedPane_1.addTab("Tips for Apophysis users ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-2.png")), getPanel_103(), null);
      tabbedPane_1.addTab("3DMesh Generation tips ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/sports-soccer.png")), getPanel_105(), null);
      tabbedPane_1.addTab("FAQ ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-2.png")), getPanel_104(), null);
    }
    return rootTabbedPane;
  }

  private JPanel interactiveNorthPanel;
  private JPanel interactiveWestPanel;
  private JPanel interactiveEastPanel;
  private JPanel interactiveCenterPanel;
  private JButton interactiveNextButton;
  private JButton interactiveLoadFlameFromClipboardButton;
  private JButton interactiveLoadFlameButton;
  private JButton interactiveFlameToClipboardButton;
  private JButton interactiveStopButton;
  private JButton interactiveSaveFlameButton;
  private JButton interactiveSaveImageButton;
  private JSplitPane interactiveCenterSplitPane;
  private JPanel interactiveCenterTopPanel;
  private JPanel interactiveCenterSouthPanel;
  private JScrollPane interactiveStatsScrollPane;
  private JTextArea interactiveStatsTextArea;
  private JComboBox interactiveRandomStyleCmb;
  private JToggleButton interactiveHalfSizeButton;
  private JToggleButton affinePreserveZButton;
  private JButton qualityProfileBtn;
  private JButton resolutionProfileBtn;
  private JComboBox interactiveResolutionProfileCmb;
  private JComboBox qualityProfileCmb;
  private JComboBox resolutionProfileCmb;
  private JComboBox batchQualityProfileCmb;
  private JComboBox batchResolutionProfileCmb;
  private JButton interactiveFlameToEditorButton;
  private JButton interactiveLoadFlameFromMainButton;
  private JWFNumberField swfAnimatorFramesPerSecondREd;
  private JPanel panel_5;
  private JButton swfAnimatorLoadFlameFromMainButton;
  private JButton swfAnimatorLoadFlameFromClipboardButton;
  private JButton swfAnimatorLoadFlameButton;
  private JLabel label_1;
  private JComboBox swfAnimatorResolutionProfileCmb;
  private JLabel label_3;
  private JProgressBar swfAnimatorProgressBar;
  private JButton swfAnimatorCancelButton;
  private JPanel swfAnimatorPanel_1;
  private JSlider swfAnimatorFrameSlider;
  private JWFNumberField swfAnimatorFrameREd;
  private JPanel swfAnimatorFlamesPanel;
  private ButtonGroup swfAnimatorFlamesButtonGroup;
  private JPanel panel_14;
  private JPanel swfAnimatorPreviewRootPanel;
  private JPanel panel_4;
  private JButton swfAnimatorRemoveFlameButton;
  private JButton swfAnimatorRemoveAllFlamesButton;
  private JButton swfAnimatorMoveUpButton;
  private JButton swfAnimatorMoveDownButton;
  private JButton swfAnimatorMovieFromClipboardButton;
  private JButton swfAnimatorMovieFromDiscButton;
  private JButton swfAnimatorMovieToClipboardButton;
  private JButton swfAnimatorMovieToDiscButton;
  private JButton tinaAppendToMovieButton;
  private JButton swfAnimatorFrameToEditorBtn;
  private JButton swfAnimatorPlayButton;
  private JWFNumberField transformationWeightREd;
  private JWFNumberField relWeightREd;
  private JScrollPane scrollPane;
  private JTextPane helpPane;
  private JPanel panel;
  private JScrollPane scrollPane_1;
  private JTextPane faqPane;
  private JToggleButton mouseTransformEditPointsButton;
  private JButton undoButton;
  private JLabel label_5;
  private JButton redoButton;
  private JButton interactivePauseButton;
  private JButton interactiveResumeButton;
  private JPanel antialiasPanel;
  private JWFNumberField xFormAntialiasAmountREd;
  private JLabel xFormAntialiasAmountLbl;
  private JSlider xFormAntialiasAmountSlider;
  private JWFNumberField xFormAntialiasRadiusREd;
  private JLabel xFormAntialiasRadiusLbl;
  private JSlider xFormAntialiasRadiusSlider;
  private JPanel panel_6;
  private JPanel panel_7;
  private JPanel panel_13;
  private JPanel panel_15;
  private JPanel tinaPaletteTransformPanel;
  private JWFNumberField tinaPaletteSwapRGBREd;
  private JSlider tinaPaletteSwapRGBSlider;
  private JWFNumberField tinaPaletteFrequencyREd;
  private JSlider tinaPaletteFrequencySlider;
  private JWFNumberField tinaPaletteBlurREd;
  private JSlider tinaPaletteBlurSlider;
  private JButton tinaPaletteInvertBtn;
  private JButton tinaPaletteReverseBtn;
  private JButton snapShotButton;
  private JButton btnQsave;
  private JLabel label_6;
  private JPanel panel_20;
  private JPanel panel_21;
  private JPanel panel_22;
  private JPanel panel_23;
  private JPanel panel_24;
  private JPanel panel_25;
  private JPanel panel_26;
  private JPanel panel_27;
  private JPanel panel_28;
  private JPanel panel_29;
  private JPanel panel_30;
  private JPanel panel_31;
  private JPanel panel_32;
  private JPanel panel_33;
  private JPanel panel_35;
  private JToggleButton toggleTransparencyButton;
  private JCheckBox bgTransparencyCBx;
  private JPanel panel_36;
  private JPanel dancingFlamesFlamePnl;
  private JButton dancingFlamesStopShowBtn;
  private JPanel dancingFlamesGraph1Pnl;
  private JButton tinaAppendToDancingFlamesButton;
  private JButton dancingFlamesAddFromEditorBtn;
  private JButton dancingFlamesAddFromClipboardBtn;
  private JButton dancingFlamesAddFromDiscBtn;
  private JPanel panel_43;
  private JButton dancingFlamesLoadSoundBtn;
  private JLabel lblRandomGenerator;
  private JPanel panel_52;
  private JPanel panel_54;
  private JComboBox dancingFlamesRandomGenCmb;
  private JWFNumberField dancingFlamesRandomCountIEd;
  private JButton dancingFlamesGenRandFlamesBtn;
  private JPanel dancingFlamesPoolFlamePreviewPnl;
  private JLabel lblBorderSize;
  private JSlider dancingFlamesBorderSizeSlider;
  private JButton dancingFlamesFlameToEditorBtn;
  private JTextField dancingFlamesFramesPerSecondIEd;
  private JLabel label_7;
  private JButton dancingFlamesDeleteFlameBtn;
  private JTextField dancingFlamesMorphFrameCountIEd;
  private JLabel lblMorphFrames;
  private JPanel batchPreviewRootPanel;
  private JButton dancingFlamesStartShowBtn;
  private JCheckBox dancingFlamesDoRecordCBx;
  private JPanel panel_37;
  private JPanel panel_41;
  private JToggleButton mouseTransformEditViewButton;
  private JPanel panel_49;
  private JComboBox dancingFlamesFlameCmb;
  private JCheckBox dancingFlamesDrawTrianglesCBx;
  private JCheckBox dancingFlamesDrawFFTCBx;
  private JCheckBox dancingFlamesDrawFPSCBx;
  private JTree dancingFlamesFlamePropertiesTree;
  private JPanel panel_42;
  private JPanel panel_40;
  private JPanel panel_44;
  private JCheckBox dofNewDOFCBx;
  private JWFNumberField dofDOFREd;
  private JSlider dofDOFSlider;
  private JWFNumberField dofDOFAreaREd;
  private JSlider dofDOFAreaSlider;
  private JWFNumberField dofDOFExponentREd;
  private JSlider dofDOFExponentSlider;
  private JWFNumberField dofCamZREd;
  private JSlider dofCamZSlider;
  private JWFNumberField dofFocusXREd;
  private JSlider dofFocusXSlider;
  private JWFNumberField dofFocusYREd;
  private JSlider dofFocusYSlider;
  private JWFNumberField dofFocusZREd;
  private JSlider dofFocusZSlider;
  private JPanel panel_46;
  private JPanel panel_47;
  private JPanel panel_48;
  private JPanel panel_39;
  private JPanel panel_38;
  private JPanel panel_45;
  private JPanel dancingFlamesMotionPropertyPnl;
  private JScrollPane scrollPane_3;
  private JTable dancingFlamesMotionTable;
  private JComboBox dancingFlamesAddMotionCmb;
  private JButton dancingFlamesAddMotionBtn;
  private JButton dancingFlamesDeleteMotionBtn;
  private JButton dancingFlamesLinkMotionBtn;
  private JButton dancingFlamesUnlinkMotionBtn;
  private JButton dancingFlamesCreateMotionsBtn;
  private JComboBox dancingFlamesCreateMotionsCmb;
  private JButton dancingFlamesClearMotionsBtn;
  private JButton dancingFlamesLoadProjectBtn;
  private JButton dancingFlamesSaveProjectBtn;
  private JPanel panel_50;
  private JTable dancingFlamesMotionLinksTable;
  private JPanel tinaColorChooserPaletteImgPanel;
  private JWFNumberField tinaFilterRadiusREd;
  private JSlider tinaFilterRadiusSlider;
  private JComboBox tinaFilterKernelCmb;
  private JWFNumberField camDimishZREd;
  private JSlider camDimishZSlider;
  private JButton tinaAddLinkedTransformationButton;
  private JWFNumberField shadingDistanceColorRadiusREd;
  private JSlider shadingDistanceColorRadiusSlider;
  private JWFNumberField shadingDistanceColorScaleREd;
  private JSlider shadingDistanceColorScaleSlider;
  private JWFNumberField shadingDistanceColorExponentREd;
  private JSlider shadingDistanceColorExponentSlider;
  private JWFNumberField shadingDistanceColorOffsetXREd;
  private JWFNumberField shadingDistanceColorOffsetYREd;
  private JWFNumberField shadingDistanceColorOffsetZREd;
  private JSlider shadingDistanceColorOffsetXSlider;
  private JSlider shadingDistanceColorOffsetYSlider;
  private JSlider shadingDistanceColorOffsetZSlider;
  private JWFNumberField shadingDistanceColorStyleREd;
  private JSlider shadingDistanceColorStyleSlider;
  private JWFNumberField shadingDistanceColorCoordinateREd;
  private JSlider shadingDistanceColorCoordinateSlider;
  private JWFNumberField shadingDistanceColorShiftREd;
  private JSlider shadingDistanceColorShiftSlider;
  private JButton quickMutationButton;
  private JPanel panel_16;
  private JPanel panel_51;
  private JPanel panel_53;
  private JPanel mutaGen05Pnl;
  private JPanel mutaGen01Pnl;
  private JPanel mutaGen03Pnl;
  private JPanel mutaGen04Pnl;
  private JPanel mutaGen06Pnl;
  private JPanel mutaGen07Pnl;
  private JPanel mutaGen08Pnl;
  private JPanel mutaGen09Pnl;
  private JPanel mutaGen10Pnl;
  private JPanel mutaGen12Pnl;
  private JPanel mutaGen13Pnl;
  private JPanel mutaGen14Pnl;
  private JPanel mutaGen15Pnl;
  private JPanel mutaGen16Pnl;
  private JPanel panel_69;
  private JPanel mutaGen17Pnl;
  private JPanel panel_71;
  private JPanel mutaGen18Pnl;
  private JPanel mutaGen19Pnl;
  private JPanel mutaGen20Pnl;
  private JPanel mutaGen02Pnl;
  private JPanel mutaGen21Pnl;
  private JPanel mutaGen22Pnl;
  private JPanel mutaGen23Pnl;
  private JPanel mutaGen25Pnl;
  private JPanel mutaGen24Pnl;
  private JPanel mutaGen11Pnl;
  private JButton mutaGenBackBtn;
  private JButton mutaGenLoadFlameFromEditorBtn;
  private JProgressBar mutaGenProgressBar;
  private JComboBox mutaGenHorizontalTrend1Cmb;
  private JComboBox mutaGenVerticalTrend1Cmb;
  private JButton mutaGenLoadFlameFromFileBtn;
  private JPanel panel_55;
  private JLabel lblH;
  private JComboBox mutaGenHorizontalTrend2Cmb;
  private JPanel panel_56;
  private JLabel lblV;
  private JPanel panel_57;
  private JLabel lblVertical;
  private JComboBox mutaGenVerticalTrend2Cmb;
  private JPanel panel_58;
  private JButton mutaGenForwardBtn;
  private JTextPane mutaGenHintPane;
  private JScrollPane scrollPane_4;
  private JWFNumberField mutaGenAmountREd;
  private JButton mutaGenRefreshBtn;
  private JButton mutaGenSaveFlameToEditorBtn;
  private JButton mutaGenSaveFlameToFileBtn;
  private JButton editFlameTitleBtn;
  private JLabel label_8;
  private JButton editTransformCaptionBtn;
  private JToggleButton mouseTransformRotateTrianglesButton;
  private JToggleButton mouseTransformScaleTrianglesButton;
  private JTree scriptTree;
  private JButton newScriptBtn;
  private JPanel panel_59;
  private JTabbedPane tabbedPane;
  private JPanel panel_60;
  private JPanel panel_61;
  private JScrollPane scrollPane_5;
  private JTextArea scriptDescriptionTextArea;
  private JButton rescanScriptsBtn;
  private JButton deleteScriptBtn;
  private JButton scriptRenameBtn;
  private JButton scriptRunBtn;
  private JButton duplicateScriptBtn;
  private JButton scriptSaveBtn;
  private JButton scriptRevertBtn;
  private JButton newScriptFromFlameBtn;
  private JToggleButton mouseTransformEditGradientButton;
  private JPanel gradientEditorFncPnl;
  private JButton gradientInvertBtn;
  private JButton gradientReverseBtn;
  private JButton gradientSortBtn;
  private JButton gradientFadeBtn;
  private JButton gradientSelectAllBtn;
  private JButton gradientApplyBalancingBtn;
  private JButton gradientApplyTXBtn;
  private JPanel panel_62;
  private JPanel panel_63;
  private JPanel panel_64;
  private JButton gradientCopyRangeBtn;
  private JButton gradientPasteRangeBtn;
  private JButton gradientEraseRangeBtn;
  private JSplitPane splitPane;
  private JPanel gradientLibraryThumbnailPnl;
  private JScrollPane scrollPane_6;
  private JTree gradientLibTree;
  private JButton gradientMonochromeRangeBtn;
  private JPanel panel_67;
  private JButton gradientFadeAllBtn;
  private JPanel panel_65;
  private JPanel frameSliderPanel;
  private JPanel panel_68;
  private JButton gradientLibraryRescanBtn;
  private JButton gradientLibraryNewFolderBtn;
  private JButton gradientLibraryRenameFolderBtn;
  private JScrollPane scrollPane_7;
  private JList gradientsList;
  private JPanel panel_70;
  private JButton gradientSaveBtn;
  private JButton backgroundColorIndicatorBtn;
  private JButton randomizeBtn;
  private JPanel panel_72;
  private JPanel flameBrowserRootTopPanel;
  private JPanel flameBrowserRootBottomPanel;
  private JPanel flameBrowserTreePanel;
  private JPanel flameBrowserDetailPanel;
  private JPanel flameBrowserImagesPanel;
  private JScrollPane flameBrowserTreeScrollPane;
  private JTree flameBrowserTree;
  private JButton flameBrowserRefreshBtn;
  private JButton flameBrowserToEditorBtn;
  private JButton flameBrowserDeleteBtn;
  private JButton flameBrowserRenameBtn;
  private JButton flameBrowserChangeFolderBtn;
  private JPanel panel_73;
  private JCheckBox tinaPaletteFadeColorsCBx;
  private JButton dancingFlamesReplaceFlameFromEditorBtn;
  private JButton dancingFlamesRenameFlameBtn;
  private JButton dancingFlamesRenameMotionBtn;
  private JCheckBox dancingFlamesMutedCBx;
  private JPanel panel_74;
  private JPanel panel_75;
  private JPanel panel_76;
  private JPanel panel_77;
  private JScrollPane scrollPane_8;
  private JTable layersTable;
  private JWFNumberField layerWeightEd;
  private JButton layerDeleteBtn;
  private JToggleButton layerVisibleBtn;
  private JButton layerAddBtn;
  private JButton layerDuplicateBtn;
  private JToggleButton layerAppendBtn;
  private JButton layerShowAllBtn;
  private JButton layerHideOthersBtn;
  private JButton flameBrowserToBatchRendererBtn;
  private JToggleButton layerPreviewBtn;
  private JPanel motionBlurPanel;
  private JPanel panel_79;
  private JPanel panel_80;
  private JWFNumberField keyframesFrameField;
  private JWFNumberField keyframesFrameCountField;
  private JSlider keyframesFrameSlider;
  private JToggleButton motionCurveEditModeButton;
  private JPanel panel_66;
  private JWFNumberField motionBlurLengthField;
  private JSlider motionBlurLengthSlider;
  private JWFNumberField motionBlurTimeStepField;
  private JSlider motionBlurTimeStepSlider;
  private JWFNumberField motionBlurDecayField;
  private JSlider motionBlurDecaySlider;
  private JLabel keyframesFrameLbl;
  private JLabel keyframesFrameCountLbl;
  private JPanel panel_34;
  private JComboBox postSymmetryTypeCmb;
  private JWFNumberField postSymmetryDistanceREd;
  private JSlider postSymmetryDistanceSlider;
  private JWFNumberField postSymmetryRotationREd;
  private JSlider postSymmetryRotationSlider;
  private JWFNumberField postSymmetryOrderREd;
  private JSlider postSymmetryOrderSlider;
  private JWFNumberField postSymmetryCentreXREd;
  private JSlider postSymmetryCentreXSlider;
  private JWFNumberField postSymmetryCentreYREd;
  private JSlider postSymmetryCentreYSlider;
  private JPanel panel_78;
  private JPanel panel_81;
  private JLabel lblSymmetry;
  private JComboBox randomSymmetryCmb;
  private JPanel panel_82;
  private JComboBox stereo3dModeCmb;
  private JWFNumberField stereo3dAngleREd;
  private JSlider stereo3dAngleSlider;
  private JWFNumberField stereo3dEyeDistREd;
  private JSlider stereo3dEyeDistSlider;
  private JComboBox stereo3dLeftEyeColorCmb;
  private JComboBox stereo3dRightEyeColorCmb;
  private JWFNumberField stereo3dInterpolatedImageCountREd;
  private JSlider stereo3dInterpolatedImageCountSlider;
  private JComboBox swfAnimatorGlobalScript2Cmb;
  private JComboBox swfAnimatorGlobalScript3Cmb;
  private JComboBox swfAnimatorGlobalScript4Cmb;
  private JComboBox swfAnimatorGlobalScript5Cmb;
  private JComboBox swfAnimatorXFormScript3Cmb;
  private JComboBox swfAnimatorXFormScript4Cmb;
  private JComboBox swfAnimatorXFormScript5Cmb;
  private JComboBox swfAnimatorXFormScript2Cmb;
  private JComboBox stereo3dPreviewCmb;
  private JLabel lblPreviewMode;
  private JWFNumberField stereo3dFocalOffsetREd;
  private JSlider stereo3dFocalOffsetSlider;
  private JWFNumberField swfAnimatorGlobalScript1REd;
  private JWFNumberField swfAnimatorGlobalScript2REd;
  private JWFNumberField swfAnimatorGlobalScript3REd;
  private JWFNumberField swfAnimatorGlobalScript4REd;
  private JWFNumberField swfAnimatorXFormScript1REd;
  private JWFNumberField swfAnimatorXFormScript2REd;
  private JWFNumberField swfAnimatorXFormScript3REd;
  private JWFNumberField swfAnimatorXFormScript4REd;
  private JWFNumberField swfAnimatorXFormScript5REd;
  private JWFNumberField swfAnimatorGlobalScript5REd;
  private JWFNumberField swfAnimatorMotionBlurLengthREd;
  private JWFNumberField swfAnimatorMotionBlurTimeStepREd;
  private JCheckBox stereo3dSwapSidesCBx;
  private JPanel randomMoviePanel;
  private JButton swfAnimatorGenRandomBatchBtn;
  private JComboBox swfAnimatorRandGenCmb;
  private JPanel panel_84;
  private JScrollPane scrollPane_9;
  private JPanel panel_86;
  private JPanel panel_87;
  private JComboBox swfAnimatorXFormScript6Cmb;
  private JComboBox swfAnimatorXFormScript7Cmb;
  private JComboBox swfAnimatorXFormScript8Cmb;
  private JComboBox swfAnimatorXFormScript9Cmb;
  private JComboBox swfAnimatorXFormScript10Cmb;
  private JComboBox swfAnimatorXFormScript11Cmb;
  private JComboBox swfAnimatorXFormScript12Cmb;
  private JWFNumberField swfAnimatorXFormScript6REd;
  private JWFNumberField swfAnimatorXFormScript7REd;
  private JWFNumberField swfAnimatorXFormScript8REd;
  private JWFNumberField swfAnimatorXFormScript9REd;
  private JWFNumberField swfAnimatorXFormScript10REd;
  private JWFNumberField swfAnimatorXFormScript11REd;
  private JWFNumberField swfAnimatorXFormScript12REd;
  private JLabel lblGlobalScript_4;
  private JLabel lblGlobalScript_5;
  private JLabel lblGlobalScript_6;
  private JLabel lblGlobalScript_7;
  private JLabel lblGlobalScript_8;
  private JComboBox swfAnimatorGlobalScript6Cmb;
  private JComboBox swfAnimatorGlobalScript7Cmb;
  private JComboBox swfAnimatorGlobalScript8Cmb;
  private JComboBox swfAnimatorGlobalScript9Cmb;
  private JComboBox swfAnimatorGlobalScript10Cmb;
  private JComboBox swfAnimatorGlobalScript11Cmb;
  private JComboBox swfAnimatorGlobalScript12Cmb;
  private JWFNumberField swfAnimatorGlobalScript6REd;
  private JWFNumberField swfAnimatorGlobalScript7REd;
  private JWFNumberField swfAnimatorGlobalScript8REd;
  private JWFNumberField swfAnimatorGlobalScript9REd;
  private JWFNumberField swfAnimatorGlobalScript10REd;
  private JWFNumberField swfAnimatorGlobalScript11REd;
  private JWFNumberField swfAnimatorGlobalScript12REd;
  private JButton btnRender;
  private JPanel panel_12;
  private JWFNumberField tinaCameraCamPosXREd;
  private JWFNumberField tinaCameraCamPosYREd;
  private JWFNumberField tinaCameraCamPosZREd;
  private JSlider tinaCameraCamPosXSlider;
  private JSlider tinaCameraCamPosYSlider;
  private JSlider tinaCameraCamPosZSlider;
  private JSlider tinaSaturationSlider;
  private JWFNumberField tinaSaturationREd;
  private JSlider editorFractalBrightnessSlider;
  private JToggleButton toggleDrawGridButton;
  private JToggleButton mouseTransformEditTriangleViewButton;
  private JComboBox randomGradientCmb;
  private JComboBox tinaPaletteRandomGeneratorCmb;
  private JToggleButton toggleTriangleWithColorsButton;
  private JButton flameBrowserCopyToBtn;
  private JButton flameBrowserMoveToBtn;
  private JButton affineScaleEditMotionCurveBtn;
  private JButton affineRotateEditMotionCurveBtn;
  private JComboBox triangleStyleCmb;
  private JPanel panel_17;
  private JPanel panel_18;
  private JPanel panel_19;
  private JPanel panel_88;
  private JWFNumberField xFormModGammaREd;
  private JWFNumberField xFormModGammaSpeedREd;
  private JWFNumberField xFormModContrastREd;
  private JWFNumberField xFormModContrastSpeedREd;
  private JWFNumberField xFormModSaturationREd;
  private JWFNumberField xFormModSaturationSpeedREd;
  private JSlider xFormModGammaSlider;
  private JSlider xFormModGammaSpeedSlider;
  private JSlider xFormModContrastSlider;
  private JSlider xFormModContrastSpeedSlider;
  private JSlider xFormModSaturationSlider;
  private JSlider xFormModSaturationSpeedSlider;
  private JCheckBox xFormModGammaWholeFractalCBx;
  private JButton xFormModGammaResetBtn;
  private JButton xFormModGammaRandomizeBtn;
  private JPanel panel_89;
  private JPanel panel_90;
  private JPanel panel_91;
  private JPanel meshGenTopViewRootPnl;
  private JPanel meshGenFrontViewRootPnl;
  private JPanel meshGenPerspectiveViewRootPnl;
  private JButton meshGenFromEditorBtn;
  private JButton meshGenFromClipboardBtn;
  private JButton meshGenLoadFlameBtn;
  private JWFNumberField meshGenSliceCountREd;
  private JWFNumberField meshGenSlicesPerRenderREd;
  private JWFNumberField meshGenRenderWidthREd;
  private JWFNumberField meshGenRenderHeightREd;
  private JWFNumberField meshGenRenderQualityREd;
  private JProgressBar meshGenProgressbar;
  private JButton meshGenGenerateBtn;
  private JScrollPane scrollPane_10;
  private JTextPane meshGenHintPane;
  private JWFNumberField meshGenCentreXREd;
  private JWFNumberField meshGenCentreYREd;
  private JWFNumberField meshGenZoomREd;
  private JSlider meshGenCentreXSlider;
  private JSlider meshGenCentreYSlider;
  private JSlider meshGenZoomSlider;
  private JWFNumberField meshGenZMinREd;
  private JWFNumberField meshGenZMaxREd;
  private JSlider meshGenZMinSlider;
  private JSlider meshGenZMaxSlider;
  private JPanel panel_96;
  private JButton meshGenTopViewRenderBtn;
  private JButton meshGenFrontViewRenderBtn;
  private JButton meshGenPerspectiveViewRenderBtn;
  private JButton meshGenTopViewToEditorBtn;
  private JButton flameBrowserToMeshGenBtn;
  private JPanel panel_93;
  private JPanel panel_94;
  private JTabbedPane tabbedPane_2;
  private JPanel panel_95;
  private JPanel panel_97;
  private JPanel panel_98;
  private JPanel panel_99;
  private JButton meshGenLoadSequenceBtn;
  private JWFNumberField meshGenSequenceWidthREd;
  private JWFNumberField meshGenSequenceHeightREd;
  private JWFNumberField meshGenSequenceSlicesREd;
  private JWFNumberField meshGenSequenceDownSampleREd;
  private JWFNumberField meshGenSequenceFilterRadiusREd;
  private JProgressBar meshGenGenerateMeshProgressbar;
  private JButton meshGenGenerateMeshBtn;
  private JButton meshGenSequenceFromRendererBtn;
  private JLabel meshGenSequenceLbl;
  private JWFNumberField meshGenSequenceThresholdREd;
  private JPanel panel_100;
  private JPanel panel_101;
  private JPanel meshGenPreviewRootPanel;
  private JCheckBox meshGenAutoPreviewCBx;
  private JButton meshGenPreviewImportLastGeneratedMeshBtn;
  private JButton meshGenPreviewImportFromFileBtn;
  private JButton meshGenClearPreviewBtn;
  private JWFNumberField meshGenPreviewPositionXREd;
  private JWFNumberField meshGenPreviewPositionYREd;
  private JWFNumberField meshGenPreviewSizeREd;
  private JWFNumberField meshGenPreviewScaleZREd;
  private JWFNumberField meshGenPreviewRotateAlphaREd;
  private JWFNumberField meshGenPreviewRotateBetaREd;
  private JWFNumberField meshGenPreviewPointsREd;
  private JPanel panel_102;
  private JWFNumberField meshGenPreviewPolygonsREd;
  private JButton meshGenRefreshPreviewBtn;
  private JPanel panel_103;
  private JPanel panel_104;
  private JPanel panel_105;
  private JButton meshGenPreviewSunflowExportBtn;
  private JScrollPane scrollPane_11;
  private JTextPane apophysisHintsPane;
  private JPanel channelMixerPanel;
  private JWFNumberField meshGenSliceThicknessModREd;
  private JPanel channelMixerRedRedRootPanel;
  private JPanel channelMixerRedGreenRootPanel;
  private JPanel channelMixerRedBlueRootPanel;
  private JPanel channelMixerGreenRedRootPanel;
  private JPanel channelMixerGreenGreenRootPanel;
  private JPanel channelMixerGreenBlueRootPanel;
  private JPanel channelMixerBlueRedRootPanel;
  private JPanel channelMixerBlueGreenRootPanel;
  private JPanel channelMixerBlueBlueRootPanel;
  private JPanel panel_117;
  private JLabel lblRed;
  private JLabel lblGreen;
  private JLabel lblBlue;
  private JButton channelMixerResetBtn;
  private JComboBox channelMixerModeCmb;
  private JWFNumberField meshGenSliceThicknessSamplesREd;
  private JComboBox meshGenPreFilter1Cmb;
  private JComboBox meshGenPreFilter2Cmb;
  private JWFNumberField meshGenImageStepREd;
  private JButton motionCurvePlayPreviewButton;
  private JPanel panel_92;
  private JTabbedPane tabbedPane_3;
  private JPanel panel_118;
  private JComboBox dofDOFShapeCmb;
  private JLabel dofDOFScaleLbl;
  private JLabel dofDOFAngleLbl;
  private JLabel dofDOFFadeLbl;
  private JLabel dofDOFParam1Lbl;
  private JLabel dofDOFParam2Lbl;
  private JLabel dofDOFParam3Lbl;
  private JLabel dofDOFParam4Lbl;
  private JLabel dofDOFParam5Lbl;
  private JLabel dofDOFParam6Lbl;
  private JSlider dofDOFScaleSlider;
  private JSlider dofDOFAngleSlider;
  private JSlider dofDOFFadeSlider;
  private JWFNumberField dofDOFScaleREd;
  private JSlider dofDOFParam1Slider;
  private JSlider dofDOFParam2Slider;
  private JSlider dofDOFParam3Slider;
  private JSlider dofDOFParam4Slider;
  private JSlider dofDOFParam5Slider;
  private JSlider dofDOFParam6Slider;
  private JWFNumberField dofDOFParam1REd;
  private JWFNumberField dofDOFParam2REd;
  private JWFNumberField dofDOFParam3REd;
  private JWFNumberField dofDOFParam4REd;
  private JWFNumberField dofDOFParam5REd;
  private JWFNumberField dofDOFParam6REd;
  private JWFNumberField dofDOFAngleREd;
  private JWFNumberField dofDOFFadeREd;
  private JCheckBox batchRenderOverrideCBx;
  private JButton batchRenderShowImageBtn;
  private JButton bokehBtn;

  /**
   * This method initializes renderBatchJobsScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getRenderBatchJobsScrollPane() {
    if (renderBatchJobsScrollPane == null) {
      renderBatchJobsScrollPane = new JScrollPane();
      renderBatchJobsScrollPane.setViewportView(getRenderBatchJobsTable());
    }
    return renderBatchJobsScrollPane;
  }

  /**
   * This method initializes renderBatchJobsTable	
   * 	
   * @return javax.swing.JTable	
   */
  private JTable getRenderBatchJobsTable() {
    if (renderBatchJobsTable == null) {
      renderBatchJobsTable = new JTable();
      renderBatchJobsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      renderBatchJobsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            tinaController.getBatchRendererController().renderBatchJobsTableClicked();
          }
        }
      });

    }
    return renderBatchJobsTable;
  }

  /**
   * This method initializes batchRenderAddFilesButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBatchRenderAddFilesButton() {
    if (batchRenderAddFilesButton == null) {
      batchRenderAddFilesButton = new JButton();
      batchRenderAddFilesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      batchRenderAddFilesButton.setSize(new Dimension(159, 24));
      batchRenderAddFilesButton.setMinimumSize(new Dimension(159, 24));
      batchRenderAddFilesButton.setMaximumSize(new Dimension(159, 24));
      batchRenderAddFilesButton.setPreferredSize(new Dimension(125, 24));
      batchRenderAddFilesButton.setText("Add files");
      batchRenderAddFilesButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderAddFilesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getBatchRendererController().batchRenderAddFilesButton_clicked();
        }
      });
    }
    return batchRenderAddFilesButton;
  }

  /**
   * This method initializes batchRenderFilesMoveUpButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBatchRenderFilesMoveUpButton() {
    if (batchRenderFilesMoveUpButton == null) {
      batchRenderFilesMoveUpButton = new JButton();
      batchRenderFilesMoveUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      batchRenderFilesMoveUpButton.setMinimumSize(new Dimension(159, 24));
      batchRenderFilesMoveUpButton.setMaximumSize(new Dimension(159, 24));
      batchRenderFilesMoveUpButton.setPreferredSize(new Dimension(159, 24));
      batchRenderFilesMoveUpButton.setText("Move up");
      batchRenderFilesMoveUpButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderFilesMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getBatchRendererController().batchRenderFilesMoveUpButton_clicked();
        }
      });
    }
    return batchRenderFilesMoveUpButton;
  }

  /**
   * This method initializes batchRenderFilesMoveDownButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBatchRenderFilesMoveDownButton() {
    if (batchRenderFilesMoveDownButton == null) {
      batchRenderFilesMoveDownButton = new JButton();
      batchRenderFilesMoveDownButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      batchRenderFilesMoveDownButton.setSize(new Dimension(159, 24));
      batchRenderFilesMoveDownButton.setMinimumSize(new Dimension(159, 24));
      batchRenderFilesMoveDownButton.setMaximumSize(new Dimension(159, 24));
      batchRenderFilesMoveDownButton.setPreferredSize(new Dimension(159, 24));
      batchRenderFilesMoveDownButton.setText("Move down");
      batchRenderFilesMoveDownButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderFilesMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getBatchRendererController().batchRenderFilesMoveDownButton_clicked();
        }
      });
    }
    return batchRenderFilesMoveDownButton;
  }

  /**
   * This method initializes batchRenderJobProgressBar	
   * 	
   * @return javax.swing.JProgressBar	
   */
  JProgressBar getBatchRenderJobProgressBar() {
    if (batchRenderJobProgressBar == null) {
      batchRenderJobProgressBar = new JProgressBar();
      batchRenderJobProgressBar.setValue(0);
      batchRenderJobProgressBar.setPreferredSize(new Dimension(568, 21));
      batchRenderJobProgressBar.setStringPainted(true);
    }
    return batchRenderJobProgressBar;
  }

  /**
   * This method initializes batchRenderTotalProgressBar	
   * 	
   * @return javax.swing.JProgressBar	
   */
  private JProgressBar getBatchRenderTotalProgressBar() {
    if (batchRenderTotalProgressBar == null) {
      batchRenderTotalProgressBar = new JProgressBar();
      batchRenderTotalProgressBar.setValue(0);
      batchRenderTotalProgressBar.setPreferredSize(new Dimension(568, 21));
      batchRenderTotalProgressBar.setStringPainted(true);
    }
    return batchRenderTotalProgressBar;
  }

  /**
   * This method initializes batchRenderFilesRemoveButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBatchRenderFilesRemoveButton() {
    if (batchRenderFilesRemoveButton == null) {
      batchRenderFilesRemoveButton = new JButton();
      batchRenderFilesRemoveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      batchRenderFilesRemoveButton.setMinimumSize(new Dimension(159, 12));
      batchRenderFilesRemoveButton.setMaximumSize(new Dimension(159, 24));
      batchRenderFilesRemoveButton.setPreferredSize(new Dimension(159, 24));
      batchRenderFilesRemoveButton.setText("Remove");
      batchRenderFilesRemoveButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderFilesRemoveButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getBatchRendererController().batchRenderFilesRemoveButton_clicked();
        }
      });
    }
    return batchRenderFilesRemoveButton;
  }

  /**
   * This method initializes batchRenderFilesRemoveAllButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBatchRenderFilesRemoveAllButton() {
    if (batchRenderFilesRemoveAllButton == null) {
      batchRenderFilesRemoveAllButton = new JButton();
      batchRenderFilesRemoveAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      batchRenderFilesRemoveAllButton.setMinimumSize(new Dimension(159, 24));
      batchRenderFilesRemoveAllButton.setMaximumSize(new Dimension(159, 24));
      batchRenderFilesRemoveAllButton.setPreferredSize(new Dimension(159, 24));
      batchRenderFilesRemoveAllButton.setText("Remove All");
      batchRenderFilesRemoveAllButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderFilesRemoveAllButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getBatchRendererController().batchRenderFilesRemoveAllButton_clicked();
        }
      });
    }
    return batchRenderFilesRemoveAllButton;
  }

  /**
   * This method initializes batchRenderStartButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBatchRenderStartButton() {
    if (batchRenderStartButton == null) {
      batchRenderStartButton = new JButton();
      batchRenderStartButton.setMinimumSize(new Dimension(159, 52));
      batchRenderStartButton.setMaximumSize(new Dimension(159, 59));
      batchRenderStartButton.setPreferredSize(new Dimension(159, 52));
      batchRenderStartButton.setText("Render");
      batchRenderStartButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderStartButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getBatchRendererController().batchRenderStartButton_clicked();
        }
      });
    }
    return batchRenderStartButton;
  }

  /**
   * This method initializes affineFlipHorizontalButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineFlipHorizontalButton() {
    if (affineFlipHorizontalButton == null) {
      affineFlipHorizontalButton = new JButton();
      affineFlipHorizontalButton.setPreferredSize(new Dimension(55, 24));
      //      affineFlipHorizontalButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/flipX.gif")));
      affineFlipHorizontalButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/object-flip-horizontal-2.png")));
      affineFlipHorizontalButton.setText("");
      affineFlipHorizontalButton.setToolTipText("Horizontal flip");
      affineFlipHorizontalButton.setSize(new Dimension(70, 24));
      affineFlipHorizontalButton.setLocation(new Point(176, 127));
      affineFlipHorizontalButton.setFont(new Font("Dialog", Font.BOLD, 8));
      affineFlipHorizontalButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_flipHorizontal();
        }
      });
    }
    return affineFlipHorizontalButton;
  }

  /**
   * This method initializes affineFlipVerticalButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAffineFlipVerticalButton() {
    if (affineFlipVerticalButton == null) {
      affineFlipVerticalButton = new JButton();
      affineFlipVerticalButton.setPreferredSize(new Dimension(55, 24));
      affineFlipVerticalButton.setToolTipText("Vertical flip");
      //      affineFlipVerticalButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/flipY.gif")));
      affineFlipVerticalButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/object-flip-vertical-2.png")));
      affineFlipVerticalButton.setText("");
      affineFlipVerticalButton.setSize(new Dimension(70, 24));
      affineFlipVerticalButton.setLocation(new Point(254, 127));
      affineFlipVerticalButton.setFont(new Font("Dialog", Font.BOLD, 8));
      affineFlipVerticalButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_flipVertical();
        }
      });
    }
    return affineFlipVerticalButton;
  }

  /**
   * This method initializes shadingPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getShadingPanel() {
    if (shadingPanel == null) {
      shadingPanel = new JPanel();
      shadingPanel.setLayout(new BorderLayout(0, 0));

      JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.LEFT);
      shadingPanel.add(tabbedPane_1, BorderLayout.CENTER);

      JPanel panel_2 = new JPanel();
      tabbedPane_1.addTab("Main", null, panel_2, null);
      panel_2.setLayout(null);
      shadingLbl = new JLabel();
      shadingLbl.setBounds(6, 6, 94, 22);
      panel_2.add(shadingLbl);
      shadingLbl.setPreferredSize(new Dimension(94, 22));
      shadingLbl.setText("Shading");
      shadingLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_2.add(getShadingCmb());
      tabbedPane_1.addTab("Pseudo3D-Shading", null, getPseudo3DShadingPanel(), null);
      tabbedPane_1.addTab("Blur-Shading", null, getBlurShadingPanel(), null);

      JPanel panel_1 = new JPanel();
      tabbedPane_1.addTab("Distance-Color-Shading", null, panel_1, null);
      panel_1.setLayout(null);

      shadingDistanceColorRadiusSlider = new JSlider();
      shadingDistanceColorRadiusSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorRadiusSlider_changed();
          }
        }
      });
      shadingDistanceColorRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorRadiusSlider.setValue(0);
      shadingDistanceColorRadiusSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorRadiusSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorRadiusSlider.setMinimum(0);
      shadingDistanceColorRadiusSlider.setMaximum(200);
      shadingDistanceColorRadiusSlider.setLocation(new Point(202, 4));
      shadingDistanceColorRadiusSlider.setBounds(204, 6, 120, 24);
      panel_1.add(shadingDistanceColorRadiusSlider);

      shadingDistanceColorRadiusREd = new JWFNumberField();
      shadingDistanceColorRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorRadiusREd.isMouseAdjusting() || shadingDistanceColorRadiusREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorRadiusSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorRadiusREd_changed();
          }
        }
      });
      shadingDistanceColorRadiusREd.setValueStep(0.01);
      shadingDistanceColorRadiusREd.setText("");
      shadingDistanceColorRadiusREd.setSize(new Dimension(100, 24));
      shadingDistanceColorRadiusREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorRadiusREd.setMaxValue(1.0);
      shadingDistanceColorRadiusREd.setLocation(new Point(100, 4));
      shadingDistanceColorRadiusREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorRadiusREd.setBounds(102, 6, 100, 24);
      panel_1.add(shadingDistanceColorRadiusREd);

      JLabel lblRadius = new JLabel();
      lblRadius.setText("Radius");
      lblRadius.setSize(new Dimension(94, 22));
      lblRadius.setPreferredSize(new Dimension(94, 22));
      lblRadius.setLocation(new Point(4, 4));
      lblRadius.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRadius.setBounds(6, 6, 94, 24);
      panel_1.add(lblRadius);

      shadingDistanceColorScaleREd = new JWFNumberField();
      shadingDistanceColorScaleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorScaleREd.isMouseAdjusting() || shadingDistanceColorScaleREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorScaleSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorScaleREd_changed();
          }
        }
      });
      shadingDistanceColorScaleREd.setValueStep(0.01);
      shadingDistanceColorScaleREd.setText("");
      shadingDistanceColorScaleREd.setSize(new Dimension(100, 24));
      shadingDistanceColorScaleREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorScaleREd.setMaxValue(1.0);
      shadingDistanceColorScaleREd.setLocation(new Point(100, 4));
      shadingDistanceColorScaleREd.setHasMinValue(true);
      shadingDistanceColorScaleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorScaleREd.setBounds(102, 29, 100, 24);
      panel_1.add(shadingDistanceColorScaleREd);

      JLabel lblScale = new JLabel();
      lblScale.setText("Scale");
      lblScale.setSize(new Dimension(94, 22));
      lblScale.setPreferredSize(new Dimension(94, 22));
      lblScale.setLocation(new Point(4, 4));
      lblScale.setFont(new Font("Dialog", Font.BOLD, 10));
      lblScale.setBounds(6, 29, 94, 24);
      panel_1.add(lblScale);

      shadingDistanceColorScaleSlider = new JSlider();
      shadingDistanceColorScaleSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorScaleSlider_changed();
          }
        }
      });
      shadingDistanceColorScaleSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorScaleSlider.setValue(0);
      shadingDistanceColorScaleSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorScaleSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorScaleSlider.setMinimum(0);
      shadingDistanceColorScaleSlider.setMaximum(300);
      shadingDistanceColorScaleSlider.setLocation(new Point(202, 4));
      shadingDistanceColorScaleSlider.setBounds(204, 29, 120, 24);
      panel_1.add(shadingDistanceColorScaleSlider);

      shadingDistanceColorExponentREd = new JWFNumberField();
      shadingDistanceColorExponentREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorExponentREd.isMouseAdjusting() || shadingDistanceColorExponentREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorExponentSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorExponentREd_changed();
          }
        }
      });
      shadingDistanceColorExponentREd.setValueStep(0.01);
      shadingDistanceColorExponentREd.setText("");
      shadingDistanceColorExponentREd.setSize(new Dimension(100, 24));
      shadingDistanceColorExponentREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorExponentREd.setMaxValue(1.0);
      shadingDistanceColorExponentREd.setLocation(new Point(100, 4));
      shadingDistanceColorExponentREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorExponentREd.setBounds(102, 52, 100, 24);
      panel_1.add(shadingDistanceColorExponentREd);

      JLabel lblExponent_1 = new JLabel();
      lblExponent_1.setText("Exponent");
      lblExponent_1.setSize(new Dimension(94, 22));
      lblExponent_1.setPreferredSize(new Dimension(94, 22));
      lblExponent_1.setLocation(new Point(4, 4));
      lblExponent_1.setFont(new Font("Dialog", Font.BOLD, 10));
      lblExponent_1.setBounds(6, 52, 94, 24);
      panel_1.add(lblExponent_1);

      shadingDistanceColorExponentSlider = new JSlider();
      shadingDistanceColorExponentSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorExponentSlider_changed();
          }
        }
      });
      shadingDistanceColorExponentSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorExponentSlider.setValue(0);
      shadingDistanceColorExponentSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorExponentSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorExponentSlider.setMinimum(-300);
      shadingDistanceColorExponentSlider.setMaximum(300);
      shadingDistanceColorExponentSlider.setLocation(new Point(202, 4));
      shadingDistanceColorExponentSlider.setBounds(204, 52, 120, 24);
      panel_1.add(shadingDistanceColorExponentSlider);

      shadingDistanceColorOffsetXREd = new JWFNumberField();
      shadingDistanceColorOffsetXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorOffsetXREd.isMouseAdjusting() || shadingDistanceColorOffsetXREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorOffsetXSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorOffsetXREd_changed();
          }
        }
      });
      shadingDistanceColorOffsetXREd.setValueStep(0.01);
      shadingDistanceColorOffsetXREd.setText("");
      shadingDistanceColorOffsetXREd.setSize(new Dimension(100, 24));
      shadingDistanceColorOffsetXREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorOffsetXREd.setMaxValue(1.0);
      shadingDistanceColorOffsetXREd.setLocation(new Point(100, 4));
      shadingDistanceColorOffsetXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorOffsetXREd.setBounds(432, 6, 100, 24);
      panel_1.add(shadingDistanceColorOffsetXREd);

      JLabel lblOffsetx = new JLabel();
      lblOffsetx.setText("OffsetX");
      lblOffsetx.setSize(new Dimension(94, 22));
      lblOffsetx.setPreferredSize(new Dimension(94, 22));
      lblOffsetx.setLocation(new Point(4, 4));
      lblOffsetx.setFont(new Font("Dialog", Font.BOLD, 10));
      lblOffsetx.setBounds(336, 6, 94, 24);
      panel_1.add(lblOffsetx);

      shadingDistanceColorOffsetXSlider = new JSlider();
      shadingDistanceColorOffsetXSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorOffsetXSlider_changed();
          }
        }
      });
      shadingDistanceColorOffsetXSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorOffsetXSlider.setValue(0);
      shadingDistanceColorOffsetXSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorOffsetXSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorOffsetXSlider.setMinimum(-100);
      shadingDistanceColorOffsetXSlider.setMaximum(100);
      shadingDistanceColorOffsetXSlider.setLocation(new Point(202, 4));
      shadingDistanceColorOffsetXSlider.setBounds(534, 6, 120, 24);
      panel_1.add(shadingDistanceColorOffsetXSlider);

      shadingDistanceColorOffsetYREd = new JWFNumberField();
      shadingDistanceColorOffsetYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorOffsetYREd.isMouseAdjusting() || shadingDistanceColorOffsetYREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorOffsetYSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorOffsetYREd_changed();
          }
        }
      });
      shadingDistanceColorOffsetYREd.setValueStep(0.01);
      shadingDistanceColorOffsetYREd.setText("");
      shadingDistanceColorOffsetYREd.setSize(new Dimension(100, 24));
      shadingDistanceColorOffsetYREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorOffsetYREd.setMaxValue(1.0);
      shadingDistanceColorOffsetYREd.setLocation(new Point(100, 4));
      shadingDistanceColorOffsetYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorOffsetYREd.setBounds(432, 29, 100, 24);
      panel_1.add(shadingDistanceColorOffsetYREd);

      JLabel lblOffsety = new JLabel();
      lblOffsety.setText("OffsetY");
      lblOffsety.setSize(new Dimension(94, 22));
      lblOffsety.setPreferredSize(new Dimension(94, 22));
      lblOffsety.setLocation(new Point(4, 4));
      lblOffsety.setFont(new Font("Dialog", Font.BOLD, 10));
      lblOffsety.setBounds(336, 29, 94, 24);
      panel_1.add(lblOffsety);

      shadingDistanceColorOffsetYSlider = new JSlider();
      shadingDistanceColorOffsetYSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorOffsetYSlider_changed();
          }
        }
      });
      shadingDistanceColorOffsetYSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorOffsetYSlider.setValue(0);
      shadingDistanceColorOffsetYSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorOffsetYSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorOffsetYSlider.setMinimum(-100);
      shadingDistanceColorOffsetYSlider.setMaximum(100);
      shadingDistanceColorOffsetYSlider.setLocation(new Point(202, 4));
      shadingDistanceColorOffsetYSlider.setBounds(534, 29, 120, 24);
      panel_1.add(shadingDistanceColorOffsetYSlider);

      shadingDistanceColorOffsetZREd = new JWFNumberField();
      shadingDistanceColorOffsetZREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorOffsetZREd.isMouseAdjusting() || shadingDistanceColorOffsetZREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorOffsetZSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorOffsetZREd_changed();
          }
        }
      });
      shadingDistanceColorOffsetZREd.setValueStep(0.01);
      shadingDistanceColorOffsetZREd.setText("");
      shadingDistanceColorOffsetZREd.setSize(new Dimension(100, 24));
      shadingDistanceColorOffsetZREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorOffsetZREd.setMaxValue(1.0);
      shadingDistanceColorOffsetZREd.setLocation(new Point(100, 4));
      shadingDistanceColorOffsetZREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorOffsetZREd.setBounds(432, 52, 100, 24);
      panel_1.add(shadingDistanceColorOffsetZREd);

      JLabel lblOffsetz = new JLabel();
      lblOffsetz.setText("OffsetZ");
      lblOffsetz.setSize(new Dimension(94, 22));
      lblOffsetz.setPreferredSize(new Dimension(94, 22));
      lblOffsetz.setLocation(new Point(4, 4));
      lblOffsetz.setFont(new Font("Dialog", Font.BOLD, 10));
      lblOffsetz.setBounds(336, 52, 94, 24);
      panel_1.add(lblOffsetz);

      shadingDistanceColorOffsetZSlider = new JSlider();
      shadingDistanceColorOffsetZSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorOffsetZSlider_changed();
          }
        }
      });
      shadingDistanceColorOffsetZSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorOffsetZSlider.setValue(0);
      shadingDistanceColorOffsetZSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorOffsetZSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorOffsetZSlider.setMinimum(-100);
      shadingDistanceColorOffsetZSlider.setMaximum(100);
      shadingDistanceColorOffsetZSlider.setLocation(new Point(202, 4));
      shadingDistanceColorOffsetZSlider.setBounds(534, 52, 120, 24);
      panel_1.add(shadingDistanceColorOffsetZSlider);

      shadingDistanceColorStyleREd = new JWFNumberField();
      shadingDistanceColorStyleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorStyleREd.isMouseAdjusting() || shadingDistanceColorStyleREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorStyleSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorStyleREd_changed();
          }
        }
      });
      shadingDistanceColorStyleREd.setHasMaxValue(true);
      shadingDistanceColorStyleREd.setHasMinValue(true);
      shadingDistanceColorStyleREd.setOnlyIntegers(true);
      shadingDistanceColorStyleREd.setValueStep(0.01);
      shadingDistanceColorStyleREd.setText("");
      shadingDistanceColorStyleREd.setSize(new Dimension(100, 24));
      shadingDistanceColorStyleREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorStyleREd.setMaxValue(2.0);
      shadingDistanceColorStyleREd.setLocation(new Point(100, 4));
      shadingDistanceColorStyleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorStyleREd.setBounds(759, 6, 100, 24);
      panel_1.add(shadingDistanceColorStyleREd);

      shadingDistanceColorStyleSlider = new JSlider();
      shadingDistanceColorStyleSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorStyleSlider_changed();
          }
        }
      });
      shadingDistanceColorStyleSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorStyleSlider.setValue(0);
      shadingDistanceColorStyleSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorStyleSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorStyleSlider.setMaximum(2);
      shadingDistanceColorStyleSlider.setLocation(new Point(202, 4));
      shadingDistanceColorStyleSlider.setBounds(861, 6, 120, 24);
      panel_1.add(shadingDistanceColorStyleSlider);

      shadingDistanceColorCoordinateREd = new JWFNumberField();
      shadingDistanceColorCoordinateREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorCoordinateREd.isMouseAdjusting() || shadingDistanceColorCoordinateREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorCoordinateSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorCoordinateREd_changed();
          }
        }
      });
      shadingDistanceColorCoordinateREd.setHasMinValue(true);
      shadingDistanceColorCoordinateREd.setHasMaxValue(true);
      shadingDistanceColorCoordinateREd.setOnlyIntegers(true);
      shadingDistanceColorCoordinateREd.setValueStep(0.01);
      shadingDistanceColorCoordinateREd.setText("");
      shadingDistanceColorCoordinateREd.setSize(new Dimension(100, 24));
      shadingDistanceColorCoordinateREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorCoordinateREd.setMaxValue(5.0);
      shadingDistanceColorCoordinateREd.setLocation(new Point(100, 4));
      shadingDistanceColorCoordinateREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorCoordinateREd.setBounds(759, 29, 100, 24);
      panel_1.add(shadingDistanceColorCoordinateREd);

      shadingDistanceColorCoordinateSlider = new JSlider();
      shadingDistanceColorCoordinateSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorCoordinateSlider_changed();
          }
        }
      });
      shadingDistanceColorCoordinateSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorCoordinateSlider.setValue(0);
      shadingDistanceColorCoordinateSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorCoordinateSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorCoordinateSlider.setMaximum(5);
      shadingDistanceColorCoordinateSlider.setLocation(new Point(202, 4));
      shadingDistanceColorCoordinateSlider.setBounds(861, 29, 120, 24);
      panel_1.add(shadingDistanceColorCoordinateSlider);

      shadingDistanceColorShiftREd = new JWFNumberField();
      shadingDistanceColorShiftREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!shadingDistanceColorShiftREd.isMouseAdjusting() || shadingDistanceColorShiftREd.getMouseChangeCount() == 0) {
              if (!shadingDistanceColorShiftSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().shadingDistanceColorShiftREd_changed();
          }
        }
      });
      shadingDistanceColorShiftREd.setValueStep(0.01);
      shadingDistanceColorShiftREd.setText("");
      shadingDistanceColorShiftREd.setSize(new Dimension(100, 24));
      shadingDistanceColorShiftREd.setPreferredSize(new Dimension(100, 24));
      shadingDistanceColorShiftREd.setMaxValue(1.0);
      shadingDistanceColorShiftREd.setLocation(new Point(100, 4));
      shadingDistanceColorShiftREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      shadingDistanceColorShiftREd.setBounds(759, 52, 100, 24);
      panel_1.add(shadingDistanceColorShiftREd);

      shadingDistanceColorShiftSlider = new JSlider();
      shadingDistanceColorShiftSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().shadingDistanceColorShiftSlider_changed();
          }
        }
      });
      shadingDistanceColorShiftSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingDistanceColorShiftSlider.setValue(0);
      shadingDistanceColorShiftSlider.setSize(new Dimension(120, 19));
      shadingDistanceColorShiftSlider.setPreferredSize(new Dimension(120, 19));
      shadingDistanceColorShiftSlider.setMinimum(-100);
      shadingDistanceColorShiftSlider.setMaximum(100);
      shadingDistanceColorShiftSlider.setLocation(new Point(202, 4));
      shadingDistanceColorShiftSlider.setBounds(861, 52, 120, 24);
      panel_1.add(shadingDistanceColorShiftSlider);

      JLabel lblStyle = new JLabel();
      lblStyle.setText("Style");
      lblStyle.setSize(new Dimension(94, 22));
      lblStyle.setPreferredSize(new Dimension(94, 22));
      lblStyle.setLocation(new Point(4, 4));
      lblStyle.setFont(new Font("Dialog", Font.BOLD, 10));
      lblStyle.setBounds(663, 6, 94, 24);
      panel_1.add(lblStyle);

      JLabel lblCoordinate = new JLabel();
      lblCoordinate.setText("Coordinate");
      lblCoordinate.setSize(new Dimension(94, 22));
      lblCoordinate.setPreferredSize(new Dimension(94, 22));
      lblCoordinate.setLocation(new Point(4, 4));
      lblCoordinate.setFont(new Font("Dialog", Font.BOLD, 10));
      lblCoordinate.setBounds(663, 29, 94, 24);
      panel_1.add(lblCoordinate);

      JLabel lblShift = new JLabel();
      lblShift.setText("Shift");
      lblShift.setSize(new Dimension(94, 22));
      lblShift.setPreferredSize(new Dimension(94, 22));
      lblShift.setLocation(new Point(4, 4));
      lblShift.setFont(new Font("Dialog", Font.BOLD, 10));
      lblShift.setBounds(663, 52, 94, 24);
      panel_1.add(lblShift);
    }
    return shadingPanel;
  }

  /**
   * This method initializes shadingCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getShadingCmb() {
    if (shadingCmb == null) {
      shadingCmb = new JComboBox();
      shadingCmb.setBounds(102, 6, 220, 22);
      shadingCmb.setPreferredSize(new Dimension(125, 22));
      shadingCmb.setSelectedItem(Shading.FLAT);
      shadingCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.saveUndoPoint();
          tinaController.shadingCmb_changed();
        }
      });
    }
    return shadingCmb;
  }

  /**
   * This method initializes blurShadingPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getBlurShadingPanel() {
    if (blurShadingPanel == null) {
      shadingBlurFallOffLbl = new JLabel();
      shadingBlurFallOffLbl.setPreferredSize(new Dimension(94, 22));
      shadingBlurFallOffLbl.setText("Blur falloff");
      shadingBlurFallOffLbl.setSize(new Dimension(94, 22));
      shadingBlurFallOffLbl.setLocation(new Point(4, 52));
      shadingBlurFallOffLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingBlurFadeLbl = new JLabel();
      shadingBlurFadeLbl.setPreferredSize(new Dimension(94, 22));
      shadingBlurFadeLbl.setText("Blur fade");
      shadingBlurFadeLbl.setSize(new Dimension(94, 22));
      shadingBlurFadeLbl.setLocation(new Point(4, 28));
      shadingBlurFadeLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingBlurRadiusLbl = new JLabel();
      shadingBlurRadiusLbl.setPreferredSize(new Dimension(94, 22));
      shadingBlurRadiusLbl.setText("Blur radius");
      shadingBlurRadiusLbl.setSize(new Dimension(94, 22));
      shadingBlurRadiusLbl.setLocation(new Point(4, 4));
      shadingBlurRadiusLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      blurShadingPanel = new JPanel();
      blurShadingPanel.setLayout(null);
      blurShadingPanel.add(shadingBlurRadiusLbl, null);
      blurShadingPanel.add(getShadingBlurRadiusREd(), null);
      blurShadingPanel.add(getShadingBlurRadiusSlider(), null);
      blurShadingPanel.add(shadingBlurFadeLbl, null);
      blurShadingPanel.add(getShadingBlurFadeREd(), null);
      blurShadingPanel.add(getShadingBlurFadeSlider(), null);
      blurShadingPanel.add(shadingBlurFallOffLbl, null);
      blurShadingPanel.add(getShadingBlurFallOffREd(), null);
      blurShadingPanel.add(getShadingBlurFallOffSlider(), null);
    }
    return blurShadingPanel;
  }

  /**
   * This method initializes shadingBlurRadiusREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingBlurRadiusREd() {
    if (shadingBlurRadiusREd == null) {
      shadingBlurRadiusREd = new JWFNumberField();
      shadingBlurRadiusREd.setMaxValue(10.0);
      shadingBlurRadiusREd.setOnlyIntegers(true);
      shadingBlurRadiusREd.setValueStep(1.0);
      shadingBlurRadiusREd.setHasMinValue(true);
      shadingBlurRadiusREd.setHasMaxValue(true);
      shadingBlurRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingBlurRadiusREd.isMouseAdjusting() || shadingBlurRadiusREd.getMouseChangeCount() == 0) {
            if (!shadingBlurRadiusSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingBlurRadiusREd_changed();
        }
      });
      shadingBlurRadiusREd.setPreferredSize(new Dimension(100, 24));
      shadingBlurRadiusREd.setText("");
      shadingBlurRadiusREd.setSize(new Dimension(100, 24));
      shadingBlurRadiusREd.setLocation(new Point(100, 4));
      shadingBlurRadiusREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingBlurRadiusREd;
  }

  /**
   * This method initializes shadingBlurRadiusSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingBlurRadiusSlider() {
    if (shadingBlurRadiusSlider == null) {
      shadingBlurRadiusSlider = new JSlider();
      shadingBlurRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingBlurRadiusSlider.setMaximum(10);
      shadingBlurRadiusSlider.setMinimum(0);
      shadingBlurRadiusSlider.setValue(0);
      shadingBlurRadiusSlider.setSize(new Dimension(120, 19));
      shadingBlurRadiusSlider.setLocation(new Point(202, 4));
      shadingBlurRadiusSlider.setPreferredSize(new Dimension(120, 19));
      shadingBlurRadiusSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingBlurRadiusSlider_changed();
        }
      });
    }
    return shadingBlurRadiusSlider;
  }

  /**
   * This method initializes shadingBlurFadeREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingBlurFadeREd() {
    if (shadingBlurFadeREd == null) {
      shadingBlurFadeREd = new JWFNumberField();
      shadingBlurFadeREd.setValueStep(0.005);
      shadingBlurFadeREd.setMaxValue(1.0);
      shadingBlurFadeREd.setHasMinValue(true);
      shadingBlurFadeREd.setHasMaxValue(true);
      shadingBlurFadeREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingBlurFadeREd.isMouseAdjusting() || shadingBlurFadeREd.getMouseChangeCount() == 0) {
            if (!shadingBlurFadeSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingBlurFadeREd_changed();
        }
      });
      shadingBlurFadeREd.setPreferredSize(new Dimension(100, 24));
      shadingBlurFadeREd.setText("");
      shadingBlurFadeREd.setSize(new Dimension(100, 24));
      shadingBlurFadeREd.setLocation(new Point(100, 28));
      shadingBlurFadeREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingBlurFadeREd;
  }

  /**
   * This method initializes shadingBlurFadeSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingBlurFadeSlider() {
    if (shadingBlurFadeSlider == null) {
      shadingBlurFadeSlider = new JSlider();
      shadingBlurFadeSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingBlurFadeSlider.setMaximum(100);
      shadingBlurFadeSlider.setMinimum(0);
      shadingBlurFadeSlider.setValue(0);
      shadingBlurFadeSlider.setSize(new Dimension(120, 19));
      shadingBlurFadeSlider.setLocation(new Point(202, 28));
      shadingBlurFadeSlider.setPreferredSize(new Dimension(120, 19));
      shadingBlurFadeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingBlurFadeSlider_changed();
        }
      });
    }
    return shadingBlurFadeSlider;
  }

  /**
   * This method initializes shadingBlurFallOffREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getShadingBlurFallOffREd() {
    if (shadingBlurFallOffREd == null) {
      shadingBlurFallOffREd = new JWFNumberField();
      shadingBlurFallOffREd.setValueStep(0.1);
      shadingBlurFallOffREd.setHasMinValue(true);
      shadingBlurFallOffREd.setHasMaxValue(true);
      shadingBlurFallOffREd.setMaxValue(10.0);
      shadingBlurFallOffREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!shadingBlurFallOffREd.isMouseAdjusting() || shadingBlurFallOffREd.getMouseChangeCount() == 0) {
            if (!shadingBlurFallOffSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().shadingBlurFallOffREd_changed();
        }
      });
      shadingBlurFallOffREd.setPreferredSize(new Dimension(100, 24));
      shadingBlurFallOffREd.setText("");
      shadingBlurFallOffREd.setSize(new Dimension(100, 24));
      shadingBlurFallOffREd.setLocation(new Point(100, 52));
      shadingBlurFallOffREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return shadingBlurFallOffREd;
  }

  /**
   * This method initializes shadingBlurFallOffSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getShadingBlurFallOffSlider() {
    if (shadingBlurFallOffSlider == null) {
      shadingBlurFallOffSlider = new JSlider();
      shadingBlurFallOffSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      shadingBlurFallOffSlider.setMaximum(100);
      shadingBlurFallOffSlider.setMinimum(0);
      shadingBlurFallOffSlider.setValue(0);
      shadingBlurFallOffSlider.setSize(new Dimension(120, 19));
      shadingBlurFallOffSlider.setLocation(new Point(202, 52));
      shadingBlurFallOffSlider.setPreferredSize(new Dimension(120, 19));
      shadingBlurFallOffSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().shadingBlurFallOffSlider_changed();
        }
      });
    }
    return shadingBlurFallOffSlider;
  }

  /**
   * This method initializes scriptPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getScriptPanel() {
    if (scriptPanel == null) {
      scriptPanel = new JPanel();
      scriptPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setMaximumSize(new Dimension(32767, 120));
      panel_1.setPreferredSize(new Dimension(10, 170));
      scriptPanel.add(panel_1, BorderLayout.NORTH);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(104, 10));
      panel_1.add(panel_2, BorderLayout.EAST);
      panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));

      rescanScriptsBtn = new JButton();
      rescanScriptsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().rescanBtn_clicked();
        }
      });
      panel_2.add(rescanScriptsBtn);
      rescanScriptsBtn.setToolTipText("Rescan script-folder");
      rescanScriptsBtn.setText("Rescan");
      rescanScriptsBtn.setPreferredSize(new Dimension(96, 24));
      rescanScriptsBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      rescanScriptsBtn.setBounds(new Rectangle(9, 280, 125, 24));

      JPanel panel_8 = new JPanel();
      panel_8.setPreferredSize(new Dimension(16, 4));
      panel_8.setMinimumSize(new Dimension(10, 8));
      panel_8.setMaximumSize(new Dimension(32767, 8));
      panel_2.add(panel_8);
      panel_2.add(getNewScriptBtn());
      panel_2.add(getNewScriptFromFlameBtn());
      panel_2.add(getDuplicateScriptBtn());

      deleteScriptBtn = new JButton();
      deleteScriptBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().deleteScriptBtn_clicked();
        }
      });
      deleteScriptBtn.setToolTipText("Delete script");
      deleteScriptBtn.setText("Del");
      deleteScriptBtn.setPreferredSize(new Dimension(48, 24));
      deleteScriptBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      deleteScriptBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_2.add(deleteScriptBtn);

      scriptRenameBtn = new JButton();
      scriptRenameBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().scriptRename_clicked();
        }
      });
      scriptRenameBtn.setToolTipText("Rename script");
      scriptRenameBtn.setText("Ren");
      scriptRenameBtn.setPreferredSize(new Dimension(48, 24));
      scriptRenameBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      scriptRenameBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_2.add(scriptRenameBtn);

      JPanel panel_3 = new JPanel();
      panel_3.setMaximumSize(new Dimension(32767, 8));
      panel_3.setMinimumSize(new Dimension(10, 8));
      panel_3.setPreferredSize(new Dimension(16, 4));
      panel_2.add(panel_3);

      scriptRunBtn = new JButton();
      scriptRunBtn.setMnemonic('u');
      scriptRunBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().scriptRunBtn_clicked();
        }
      });
      scriptRunBtn.setToolTipText("Run script");
      scriptRunBtn.setText("Run");
      scriptRunBtn.setPreferredSize(new Dimension(96, 24));
      scriptRunBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      scriptRunBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_2.add(scriptRunBtn);

      JScrollPane scrollPane_2 = new JScrollPane();
      panel_1.add(scrollPane_2, BorderLayout.CENTER);

      scriptTree = new JTree();
      scriptTree.setFont(new Font("SansSerif", Font.PLAIN, 10));
      scriptTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          if (tinaController != null) {
            tinaController.getJwfScriptController().scriptPropertiesTree_changed(e);
          }
        }
      });
      scrollPane_2.setViewportView(scriptTree);
      scriptPanel.add(getPanel_59(), BorderLayout.SOUTH);
      scriptPanel.add(getTabbedPane(), BorderLayout.CENTER);
    }
    return scriptPanel;
  }

  /**
   * This method initializes scriptScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getScriptScrollPane() {
    if (scriptScrollPane == null) {
      scriptScrollPane = new JScrollPane();
      scriptScrollPane.setViewportView(getScriptTextArea());
    }
    return scriptScrollPane;
  }

  /**
   * This method initializes scriptTextArea	
   * 	
   * @return javax.swing.JTextArea	
   */
  private JTextArea getScriptTextArea() {
    if (scriptTextArea == null) {
      scriptTextArea = new JTextArea();
      scriptTextArea.setFont(new Font("SansSerif", Font.PLAIN, 10));
      scriptTextArea.setText("");
    }
    return scriptTextArea;
  }

  /**
   * This method initializes compileScriptButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getCompileScriptButton() {
    if (compileScriptButton == null) {
      compileScriptButton = new JButton();
      compileScriptButton.setPreferredSize(new Dimension(96, 24));
      compileScriptButton.setToolTipText("Compile the currently loaded script to check the syntax");
      compileScriptButton.setText("Syntax check");
      compileScriptButton.setFont(new Font("Dialog", Font.BOLD, 10));
      compileScriptButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getJwfScriptController().compileScriptButton_clicked();
        }
      });
    }
    return compileScriptButton;
  }

  /**
   * This method initializes affineScaleXButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getAffineScaleXButton() {
    if (affineScaleXButton == null) {
      affineScaleXButton = new JToggleButton();
      affineScaleXButton.setPreferredSize(new Dimension(26, 24));
      affineScaleXButton.setToolTipText("Allow scaling in x-direction");
      affineScaleXButton.setMnemonic(KeyEvent.VK_P);
      affineScaleXButton.setText("");
      affineScaleXButton.setLocation(new Point(92, 127));
      affineScaleXButton.setSize(new Dimension(32, 24));
      affineScaleXButton.setSelected(true);
      //      affineScaleXButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/allowScaleX.gif")));
      affineScaleXButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/object-flip-horizontal.png")));
      affineScaleXButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineScaleXButton.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.affineScaleXButton_stateChanged();
        }
      });
    }
    return affineScaleXButton;
  }

  /**
   * This method initializes affineScaleYButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getAffineScaleYButton() {
    if (affineScaleYButton == null) {
      affineScaleYButton = new JToggleButton();
      affineScaleYButton.setPreferredSize(new Dimension(26, 24));
      affineScaleYButton.setToolTipText("Allow scaling in y-direction");
      affineScaleYButton.setMnemonic(KeyEvent.VK_P);
      affineScaleYButton.setSelected(true);
      affineScaleYButton.setText("");
      affineScaleYButton.setSize(new Dimension(32, 24));
      affineScaleYButton.setLocation(new Point(130, 127));
      //      affineScaleYButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/allowScaleY.gif")));
      affineScaleYButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/object-flip-vertical.png")));
      affineScaleYButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineScaleYButton.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.affineScaleYButton_stateChanged();
        }
      });
    }
    return affineScaleYButton;
  }

  /**
   * This method initializes randomizeColorsButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRandomizeColorsButton() {
    if (randomizeColorsButton == null) {
      randomizeColorsButton = new JButton();
      randomizeColorsButton.setBounds(6, 58, 148, 24);
      randomizeColorsButton.setFont(new Font("Dialog", Font.BOLD, 10));
      randomizeColorsButton.setText("Randomize colors");
      randomizeColorsButton.setPreferredSize(new Dimension(190, 24));
      randomizeColorsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.randomizeColorsBtn_clicked();
        }
      });
    }
    return randomizeColorsButton;
  }

  /**
   * This method initializes gradientLibraryPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getGradientLibraryPanel() {
    if (gradientLibraryPanel == null) {
      gradientLibraryPanel = new JPanel();
      gradientLibraryPanel.setLayout(new BorderLayout());
      gradientLibraryPanel.add(getGradientLibraryCenterPanel(), BorderLayout.CENTER);
    }
    return gradientLibraryPanel;
  }

  /**
   * This method initializes gradientLibraryCenterPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getGradientLibraryCenterPanel() {
    if (gradientLibraryCenterPanel == null) {
      gradientLibraryCenterPanel = new JPanel();
      gradientLibraryCenterPanel.setLayout(new BorderLayout());
      gradientLibraryCenterPanel.add(getSplitPane(), BorderLayout.CENTER);
    }
    return gradientLibraryCenterPanel;
  }

  private JPanel getInteractiveNorthPanel() {
    if (interactiveNorthPanel == null) {
      interactiveNorthPanel = new JPanel();
      interactiveNorthPanel.setBorder(new EmptyBorder(5, 5, 0, 0));
      interactiveNorthPanel.setPreferredSize(new Dimension(0, 86));
      interactiveNorthPanel.setSize(new Dimension(0, 42));
      interactiveNorthPanel.setLayout(new BoxLayout(interactiveNorthPanel, BoxLayout.X_AXIS));
      interactiveNorthPanel.add(getPanel_27());
      interactiveNorthPanel.add(getPanel_17());
      interactiveNorthPanel.add(getPanel_28());
      interactiveNorthPanel.add(getPanel_32());
      interactiveNorthPanel.add(getPanel_33());
      interactiveNorthPanel.add(getPanel_35());
      for (String name : RandomFlameGeneratorList.getNameList()) {
        interactiveRandomStyleCmb.addItem(name);
      }
    }
    return interactiveNorthPanel;
  }

  private JPanel getInteractiveWestPanel() {
    if (interactiveWestPanel == null) {
      interactiveWestPanel = new JPanel();
      interactiveWestPanel.setSize(new Dimension(8, 0));
      interactiveWestPanel.setPreferredSize(new Dimension(8, 0));
      interactiveWestPanel.setLayout(new BorderLayout(0, 0));
    }
    return interactiveWestPanel;
  }

  private JPanel getInteractiveEastPanel() {
    if (interactiveEastPanel == null) {
      interactiveEastPanel = new JPanel();
      interactiveEastPanel.setSize(new Dimension(8, 0));
      interactiveEastPanel.setPreferredSize(new Dimension(8, 0));
      interactiveEastPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 8, 8));
    }
    return interactiveEastPanel;
  }

  private JPanel getInteractiveCenterPanel() {
    if (interactiveCenterPanel == null) {
      interactiveCenterPanel = new JPanel();
      interactiveCenterPanel.setLayout(new BorderLayout(0, 0));
      interactiveCenterPanel.add(getInteractiveCenterSplitPane(), BorderLayout.CENTER);
    }
    return interactiveCenterPanel;
  }

  private JButton getInteractiveNextButton() {
    if (interactiveNextButton == null) {
      interactiveNextButton = new JButton();
      interactiveNextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      interactiveNextButton.setMaximumSize(new Dimension(32000, 32000));
      interactiveNextButton.setToolTipText("Cancel render, generate new random fractal and start render");
      interactiveNextButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().nextButton_clicked();
        }
      });
      interactiveNextButton.setText("Next");
      interactiveNextButton.setPreferredSize(new Dimension(125, 48));
      interactiveNextButton.setMnemonic(KeyEvent.VK_D);
      interactiveNextButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return interactiveNextButton;
  }

  private JButton getInteractiveLoadFlameFromClipboardButton() {
    if (interactiveLoadFlameFromClipboardButton == null) {
      interactiveLoadFlameFromClipboardButton = new JButton();
      interactiveLoadFlameFromClipboardButton.setMinimumSize(new Dimension(100, 24));
      interactiveLoadFlameFromClipboardButton.setMaximumSize(new Dimension(32000, 24));
      interactiveLoadFlameFromClipboardButton.setToolTipText("Load flame from clipboard and render");
      interactiveLoadFlameFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().fromClipboardButton_clicked();
        }
      });
      interactiveLoadFlameFromClipboardButton.setText("From Clipboard");
      interactiveLoadFlameFromClipboardButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameFromClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return interactiveLoadFlameFromClipboardButton;
  }

  private JButton getInteractiveLoadFlameButton() {
    if (interactiveLoadFlameButton == null) {
      interactiveLoadFlameButton = new JButton();
      interactiveLoadFlameButton.setMinimumSize(new Dimension(100, 24));
      interactiveLoadFlameButton.setMaximumSize(new Dimension(32000, 24));
      interactiveLoadFlameButton.setToolTipText("Load flame from file and render");
      interactiveLoadFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().loadFlameButton_clicked();
        }
      });
      interactiveLoadFlameButton.setText("Load Flame");
      interactiveLoadFlameButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return interactiveLoadFlameButton;
  }

  private JButton getInteractiveFlameToClipboardButton() {
    if (interactiveFlameToClipboardButton == null) {
      interactiveFlameToClipboardButton = new JButton();
      interactiveFlameToClipboardButton.setMinimumSize(new Dimension(100, 24));
      interactiveFlameToClipboardButton.setMaximumSize(new Dimension(32000, 24));
      interactiveFlameToClipboardButton.setToolTipText("Copy the current fractal into the clipboard");
      interactiveFlameToClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().toClipboardButton_clicked();
        }
      });
      interactiveFlameToClipboardButton.setText("To Clipboard");
      interactiveFlameToClipboardButton.setPreferredSize(new Dimension(125, 24));
      interactiveFlameToClipboardButton.setMnemonic(KeyEvent.VK_D);
      interactiveFlameToClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return interactiveFlameToClipboardButton;
  }

  private JButton getInteractiveStopButton() {
    if (interactiveStopButton == null) {
      interactiveStopButton = new JButton();
      interactiveStopButton.setMinimumSize(new Dimension(80, 24));
      interactiveStopButton.setMaximumSize(new Dimension(150, 24));
      interactiveStopButton.setToolTipText("Stop the render and free associated ressources");
      interactiveStopButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().stopButton_clicked();
        }
      });
      interactiveStopButton.setText("Stop");
      interactiveStopButton.setPreferredSize(new Dimension(125, 24));
      interactiveStopButton.setMnemonic(KeyEvent.VK_D);
      interactiveStopButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return interactiveStopButton;
  }

  private JButton getInteractiveSaveFlameButton() {
    if (interactiveSaveFlameButton == null) {
      interactiveSaveFlameButton = new JButton();
      interactiveSaveFlameButton.setMinimumSize(new Dimension(100, 24));
      interactiveSaveFlameButton.setMaximumSize(new Dimension(32000, 24));
      interactiveSaveFlameButton.setToolTipText("Save the current fractal");
      interactiveSaveFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().saveFlameButton_clicked();
        }
      });
      interactiveSaveFlameButton.setText("Save Flame");
      interactiveSaveFlameButton.setPreferredSize(new Dimension(125, 24));
      interactiveSaveFlameButton.setMnemonic(KeyEvent.VK_D);
      interactiveSaveFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return interactiveSaveFlameButton;
  }

  private JButton getInteractiveSaveImageButton() {
    if (interactiveSaveImageButton == null) {
      interactiveSaveImageButton = new JButton();
      interactiveSaveImageButton.setMinimumSize(new Dimension(100, 24));
      interactiveSaveImageButton.setMaximumSize(new Dimension(32000, 24));
      interactiveSaveImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().saveImageButton_clicked();
        }
      });
      interactiveSaveImageButton.setText("Save image");
      interactiveSaveImageButton.setPreferredSize(new Dimension(125, 24));
      interactiveSaveImageButton.setMnemonic(KeyEvent.VK_I);
      interactiveSaveImageButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return interactiveSaveImageButton;
  }

  private JSplitPane getInteractiveCenterSplitPane() {
    if (interactiveCenterSplitPane == null) {
      interactiveCenterSplitPane = new JSplitPane();
      interactiveCenterSplitPane.setDividerSize(6);
      interactiveCenterSplitPane.setLeftComponent(getInteractiveCenterSouthPanel());
      interactiveCenterSplitPane.setRightComponent(getInteractiveCenterTopPanel());
      interactiveCenterSplitPane.setDividerLocation(200);
    }
    return interactiveCenterSplitPane;
  }

  private JPanel getInteractiveCenterTopPanel() {
    if (interactiveCenterTopPanel == null) {
      interactiveCenterTopPanel = new JPanel();
      interactiveCenterTopPanel.setBorder(new TitledBorder(null, "Progressive preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      interactiveCenterTopPanel.setLayout(new BorderLayout(0, 0));
    }
    return interactiveCenterTopPanel;
  }

  private JPanel getInteractiveCenterSouthPanel() {
    if (interactiveCenterSouthPanel == null) {
      interactiveCenterSouthPanel = new JPanel();
      interactiveCenterSouthPanel.setLayout(new BorderLayout(0, 0));
      interactiveCenterSouthPanel.add(getInteractiveStatsScrollPane(), BorderLayout.CENTER);
    }
    return interactiveCenterSouthPanel;
  }

  private JScrollPane getInteractiveStatsScrollPane() {
    if (interactiveStatsScrollPane == null) {
      interactiveStatsScrollPane = new JScrollPane();
      interactiveStatsScrollPane.setViewportView(getInteractiveStatsTextArea());
    }
    return interactiveStatsScrollPane;
  }

  private JTextArea getInteractiveStatsTextArea() {
    if (interactiveStatsTextArea == null) {
      interactiveStatsTextArea = new JTextArea();
      interactiveStatsTextArea.setEditable(false);
    }
    return interactiveStatsTextArea;
  }

  public JComboBox getInteractiveRandomStyleCmb() {
    return interactiveRandomStyleCmb;
  }

  public JToggleButton getInteractiveHalveSizeButton() {
    return interactiveHalfSizeButton;
  }

  public JToggleButton getAffinePreserveZButton() {
    return affinePreserveZButton;
  }

  private JButton getQualityProfileBtn() {
    if (qualityProfileBtn == null) {
      qualityProfileBtn = new JButton();
      qualityProfileBtn.setMinimumSize(new Dimension(52, 42));
      qualityProfileBtn.setMaximumSize(new Dimension(52, 24));
      qualityProfileBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.editQualityProfiles();
        }
      });
      qualityProfileBtn.setToolTipText("Edit quality profiles");
      qualityProfileBtn.setText("...");
      qualityProfileBtn.setPreferredSize(new Dimension(52, 24));
      qualityProfileBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return qualityProfileBtn;
  }

  private JButton getResolutionProfileBtn() {
    if (resolutionProfileBtn == null) {
      resolutionProfileBtn = new JButton();
      resolutionProfileBtn.setMinimumSize(new Dimension(42, 24));
      resolutionProfileBtn.setMaximumSize(new Dimension(52, 24));
      resolutionProfileBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.editResolutionProfiles();
        }
      });
      resolutionProfileBtn.setToolTipText("Edit resolution profiles");
      resolutionProfileBtn.setText("...");
      resolutionProfileBtn.setPreferredSize(new Dimension(52, 24));
      resolutionProfileBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return resolutionProfileBtn;
  }

  private JComboBox getInteractiveResolutionProfileCmb() {
    if (interactiveResolutionProfileCmb == null) {
      interactiveResolutionProfileCmb = new JComboBox();
      interactiveResolutionProfileCmb.setMaximumSize(new Dimension(32767, 24));
      interactiveResolutionProfileCmb.setMinimumSize(new Dimension(100, 24));
      interactiveResolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getInteractiveRendererCtrl() != null) {
            tinaController.getInteractiveRendererCtrl().resolutionProfile_changed();
          }
        }
      });
      interactiveResolutionProfileCmb.setPreferredSize(new Dimension(125, 24));
      interactiveResolutionProfileCmb.setMaximumRowCount(32);
      interactiveResolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return interactiveResolutionProfileCmb;
  }

  public JComboBox getQualityProfileCmb() {
    return qualityProfileCmb;
  }

  public JComboBox getResolutionProfileCmb() {
    return resolutionProfileCmb;
  }

  public JComboBox getBatchQualityProfileCmb() {
    return batchQualityProfileCmb;
  }

  public JComboBox getBatchResolutionProfileCmb() {
    return batchResolutionProfileCmb;
  }

  public JButton getInteractiveFlameToEditorButton() {
    return interactiveFlameToEditorButton;
  }

  public JButton getInteractiveLoadFlameFromMainButton() {
    return interactiveLoadFlameFromMainButton;
  }

  public JWFNumberField getSwfAnimatorFramesPerSecondREd() {
    return swfAnimatorFramesPerSecondREd;
  }

  private ButtonGroup getSwfAnimatorFlamesButtonGroup() {
    if (swfAnimatorFlamesButtonGroup == null) {
      swfAnimatorFlamesButtonGroup = new ButtonGroup();
    }
    return swfAnimatorFlamesButtonGroup;
  }

  private JPanel getPanel_5() {
    if (panel_5 == null) {
      panel_5 = new JPanel();
      panel_5.setBorder(null);
      panel_5.setPreferredSize(new Dimension(10, 62));
      panel_5.setLayout(null);

      swfAnimatorProgressBar = new JProgressBar();
      swfAnimatorProgressBar.setBounds(578, 36, 331, 21);
      panel_5.add(swfAnimatorProgressBar);
      swfAnimatorProgressBar.setValue(0);
      swfAnimatorProgressBar.setStringPainted(true);
      swfAnimatorProgressBar.setPreferredSize(new Dimension(568, 21));
      panel_5.add(getSwfAnimatorGenerateButton());

      swfAnimatorCancelButton = new JButton();
      swfAnimatorCancelButton.setBounds(921, 5, 125, 52);
      panel_5.add(swfAnimatorCancelButton);
      swfAnimatorCancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().cancelButton_clicked();
        }
      });
      swfAnimatorCancelButton.setText("Cancel");
      swfAnimatorCancelButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorCancelButton.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_5.add(getLabel_3());
      panel_5.add(getSwfAnimatorResolutionProfileCmb());
      panel_5.add(getSwfAnimatorMovieFromClipboardButton());
      panel_5.add(getSwfAnimatorMovieFromDiscButton());
      panel_5.add(getSwfAnimatorMovieToClipboardButton());
      panel_5.add(getSwfAnimatorMovieToDiscButton());

      swfAnimatorGenRandomBatchBtn = new JButton();
      swfAnimatorGenRandomBatchBtn.setBounds(6, 6, 119, 50);
      panel_5.add(swfAnimatorGenRandomBatchBtn);
      swfAnimatorGenRandomBatchBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController.getSwfAnimatorCtrl().createRandomBatch(-1, (String) swfAnimatorRandGenCmb.getSelectedItem())) {
            tinaController.getSwfAnimatorCtrl().importFromRandomBatch(0);
          }

        }
      });
      swfAnimatorGenRandomBatchBtn.setToolTipText("Create a batch of random movies");
      swfAnimatorGenRandomBatchBtn.setText("Random movies");
      swfAnimatorGenRandomBatchBtn.setPreferredSize(new Dimension(105, 46));
      swfAnimatorGenRandomBatchBtn.setMinimumSize(new Dimension(100, 46));
      swfAnimatorGenRandomBatchBtn.setMaximumSize(new Dimension(32000, 46));
      swfAnimatorGenRandomBatchBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel label = new JLabel();
      label.setBounds(132, 13, 80, 14);
      panel_5.add(label);
      label.setText("  Rnd Generator");
      label.setPreferredSize(new Dimension(80, 22));
      label.setFont(new Font("Dialog", Font.BOLD, 10));
      label.setAlignmentX(1.0f);

      swfAnimatorRandGenCmb = new JComboBox();
      swfAnimatorRandGenCmb.setBounds(132, 31, 160, 24);
      panel_5.add(swfAnimatorRandGenCmb);
      swfAnimatorRandGenCmb.setToolTipText("Random-flame-generator");
      swfAnimatorRandGenCmb.setPreferredSize(new Dimension(100, 24));
      swfAnimatorRandGenCmb.setMinimumSize(new Dimension(100, 24));
      swfAnimatorRandGenCmb.setMaximumSize(new Dimension(32767, 24));
      swfAnimatorRandGenCmb.setMaximumRowCount(32);
      swfAnimatorRandGenCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return panel_5;
  }

  private JButton getSwfAnimatorLoadFlameFromMainButton() {
    if (swfAnimatorLoadFlameFromMainButton == null) {
      swfAnimatorLoadFlameFromMainButton = new JButton();
      swfAnimatorLoadFlameFromMainButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().loadFlameFromMainButton_clicked();
        }
      });
      swfAnimatorLoadFlameFromMainButton.setToolTipText("Load flame from Editor and add it to the movie");
      swfAnimatorLoadFlameFromMainButton.setText("Add from Editor");
      swfAnimatorLoadFlameFromMainButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorLoadFlameFromMainButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorLoadFlameFromMainButton;
  }

  private JButton getSwfAnimatorLoadFlameFromClipboardButton() {
    if (swfAnimatorLoadFlameFromClipboardButton == null) {
      swfAnimatorLoadFlameFromClipboardButton = new JButton();
      swfAnimatorLoadFlameFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().loadFlameFromClipboardButton_clicked();
        }
      });
      swfAnimatorLoadFlameFromClipboardButton.setToolTipText("Load flame from clipboard and add it to the movie");
      swfAnimatorLoadFlameFromClipboardButton.setText("Add from Clipboard");
      swfAnimatorLoadFlameFromClipboardButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorLoadFlameFromClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorLoadFlameFromClipboardButton;
  }

  private JButton getSwfAnimatorLoadFlameButton() {
    if (swfAnimatorLoadFlameButton == null) {
      swfAnimatorLoadFlameButton = new JButton();
      swfAnimatorLoadFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().loadFlameButton_clicked();
        }
      });
      swfAnimatorLoadFlameButton.setToolTipText("Load flame from file and add it to the movie");
      swfAnimatorLoadFlameButton.setText("Add flame from disc");
      swfAnimatorLoadFlameButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorLoadFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorLoadFlameButton;
  }

  private JLabel getLabel_1() {
    if (label_1 == null) {
      label_1 = new JLabel();
      label_1.setMinimumSize(new Dimension(80, 22));
      label_1.setText("Resolution");
      label_1.setPreferredSize(new Dimension(80, 22));
      label_1.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_1;
  }

  private JComboBox getSwfAnimatorResolutionProfileCmb() {
    if (swfAnimatorResolutionProfileCmb == null) {
      swfAnimatorResolutionProfileCmb = new JComboBox();
      swfAnimatorResolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getSwfAnimatorCtrl().resolutionProfileCmb_changed();
          }
        }
      });
      swfAnimatorResolutionProfileCmb.setMinimumSize(new Dimension(33, 24));
      swfAnimatorResolutionProfileCmb.setBounds(646, 6, 125, 24);
      swfAnimatorResolutionProfileCmb.setPreferredSize(new Dimension(125, 24));
      swfAnimatorResolutionProfileCmb.setMaximumRowCount(32);
      swfAnimatorResolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorResolutionProfileCmb;
  }

  private JLabel getLabel_3() {
    if (label_3 == null) {
      label_3 = new JLabel();
      label_3.setBounds(578, 5, 71, 22);
      label_3.setText("Resolution");
      label_3.setPreferredSize(new Dimension(94, 22));
      label_3.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_3;
  }

  public JProgressBar getSwfAnimatorProgressBar() {
    return swfAnimatorProgressBar;
  }

  public JButton getSwfAnimatorCancelButton() {
    return swfAnimatorCancelButton;
  }

  public JSlider getSwfAnimatorFrameSlider() {
    return swfAnimatorFrameSlider;
  }

  public JWFNumberField getSwfAnimatorFrameREd() {
    return swfAnimatorFrameREd;
  }

  public JPanel getSwfAnimatorFlamesPanel() {
    return swfAnimatorFlamesPanel;
  }

  private JPanel getPanel_14() {
    if (panel_14 == null) {
      panel_14 = new JPanel();
      panel_14.setBorder(null);
      panel_14.setLayout(new BorderLayout(0, 0));
      panel_14.add(getPanel_4(), BorderLayout.CENTER);
    }
    return panel_14;
  }

  public JPanel getSwfAnimatorPreviewRootPanel() {
    return swfAnimatorPreviewRootPanel;
  }

  private JPanel getPanel_4() {
    if (panel_4 == null) {
      panel_4 = new JPanel();
      panel_4.setBorder(new TitledBorder(null, "Movies", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_4.setLayout(new BorderLayout(0, 0));

      randomMoviePanel = new JPanel();
      panel_4.add(randomMoviePanel, BorderLayout.CENTER);
      randomMoviePanel.setLayout(new BorderLayout(0, 0));
    }
    return panel_4;
  }

  private JButton getSwfAnimatorRemoveFlameButton() {
    if (swfAnimatorRemoveFlameButton == null) {
      swfAnimatorRemoveFlameButton = new JButton();
      swfAnimatorRemoveFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().removeFlameButton_clicked();
        }
      });
      swfAnimatorRemoveFlameButton.setToolTipText("Remove the currently selected flame from the movie");
      swfAnimatorRemoveFlameButton.setText("Remove flame");
      swfAnimatorRemoveFlameButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorRemoveFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorRemoveFlameButton;
  }

  private JButton getSwfAnimatorRemoveAllFlamesButton() {
    if (swfAnimatorRemoveAllFlamesButton == null) {
      swfAnimatorRemoveAllFlamesButton = new JButton();
      swfAnimatorRemoveAllFlamesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().clearAllFlamesButton_clicked();
        }
      });
      swfAnimatorRemoveAllFlamesButton.setToolTipText("Remove all flames from the movie");
      swfAnimatorRemoveAllFlamesButton.setText("Remove all");
      swfAnimatorRemoveAllFlamesButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorRemoveAllFlamesButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorRemoveAllFlamesButton;
  }

  private JButton getSwfAnimatorMoveUpButton() {
    if (swfAnimatorMoveUpButton == null) {
      swfAnimatorMoveUpButton = new JButton();
      swfAnimatorMoveUpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().flameMoveUpButton_clicked();
        }
      });
      swfAnimatorMoveUpButton.setToolTipText("Move the currently selected flame up in the movie");
      swfAnimatorMoveUpButton.setText("Up");
      swfAnimatorMoveUpButton.setPreferredSize(new Dimension(65, 24));
      swfAnimatorMoveUpButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorMoveUpButton;
  }

  private JButton getSwfAnimatorMoveDownButton() {
    if (swfAnimatorMoveDownButton == null) {
      swfAnimatorMoveDownButton = new JButton();
      swfAnimatorMoveDownButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().flameMoveDownButton_clicked();
        }
      });
      swfAnimatorMoveDownButton.setToolTipText("Move the currently selected flame down in the movie");
      swfAnimatorMoveDownButton.setText("Down");
      swfAnimatorMoveDownButton.setPreferredSize(new Dimension(65, 24));
      swfAnimatorMoveDownButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorMoveDownButton;
  }

  private JButton getSwfAnimatorMovieFromClipboardButton() {
    if (swfAnimatorMovieFromClipboardButton == null) {
      swfAnimatorMovieFromClipboardButton = new JButton();
      swfAnimatorMovieFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieFromClipboardButton_clicked();
        }
      });
      swfAnimatorMovieFromClipboardButton.setText("From Clipboard");
      swfAnimatorMovieFromClipboardButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieFromClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorMovieFromClipboardButton.setBounds(new Rectangle(504, 7, 125, 24));
      swfAnimatorMovieFromClipboardButton.setBounds(304, 6, 125, 24);
    }
    return swfAnimatorMovieFromClipboardButton;
  }

  private JButton getSwfAnimatorMovieFromDiscButton() {
    if (swfAnimatorMovieFromDiscButton == null) {
      swfAnimatorMovieFromDiscButton = new JButton();
      swfAnimatorMovieFromDiscButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieFromDiscButton_clicked();
        }
      });
      swfAnimatorMovieFromDiscButton.setText("Load Movie");
      swfAnimatorMovieFromDiscButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieFromDiscButton.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorMovieFromDiscButton.setBounds(new Rectangle(504, 35, 125, 24));
      swfAnimatorMovieFromDiscButton.setBounds(304, 31, 125, 24);
    }
    return swfAnimatorMovieFromDiscButton;
  }

  private JButton getSwfAnimatorMovieToClipboardButton() {
    if (swfAnimatorMovieToClipboardButton == null) {
      swfAnimatorMovieToClipboardButton = new JButton();
      swfAnimatorMovieToClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieToClipboardButton_clicked();
        }
      });
      swfAnimatorMovieToClipboardButton.setText("To Clipboard");
      swfAnimatorMovieToClipboardButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieToClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorMovieToClipboardButton.setBounds(new Rectangle(643, 7, 125, 24));
      swfAnimatorMovieToClipboardButton.setBounds(441, 6, 125, 24);
    }
    return swfAnimatorMovieToClipboardButton;
  }

  private JButton getSwfAnimatorMovieToDiscButton() {
    if (swfAnimatorMovieToDiscButton == null) {
      swfAnimatorMovieToDiscButton = new JButton();
      swfAnimatorMovieToDiscButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieToDiscButton_clicked();
        }
      });
      swfAnimatorMovieToDiscButton.setText("Save Movie");
      swfAnimatorMovieToDiscButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieToDiscButton.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorMovieToDiscButton.setBounds(new Rectangle(643, 35, 125, 24));
      swfAnimatorMovieToDiscButton.setBounds(441, 31, 125, 24);
    }
    return swfAnimatorMovieToDiscButton;
  }

  private JButton getTinaAppendToMovieButton() {
    if (tinaAppendToMovieButton == null) {
      tinaAppendToMovieButton = new JButton();
      tinaAppendToMovieButton.setIconTextGap(2);
      tinaAppendToMovieButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.appendToMovieButton_actionPerformed(e);
        }
      });
      tinaAppendToMovieButton.setToolTipText("Append to movie");
      tinaAppendToMovieButton.setText("Movie");
      tinaAppendToMovieButton.setPreferredSize(new Dimension(72, 24));
      tinaAppendToMovieButton.setMnemonic(KeyEvent.VK_M);
      tinaAppendToMovieButton.setFont(new Font("Dialog", Font.BOLD, 9));
      tinaAppendToMovieButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/applications-multimedia.png")));
    }
    return tinaAppendToMovieButton;
  }

  public JButton getSwfAnimatorFrameToEditorBtn() {
    return swfAnimatorFrameToEditorBtn;
  }

  public JButton getSwfAnimatorPlayButton() {
    return swfAnimatorPlayButton;
  }

  private JWFNumberField getTransformationWeightREd() {
    if (transformationWeightREd == null) {
      transformationWeightREd = new JWFNumberField();
      transformationWeightREd.setHasMinValue(true);
      transformationWeightREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!transformationWeightREd.isMouseAdjusting() || transformationWeightREd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.transformationWeightREd_changed();
          }
        }
      });
      transformationWeightREd.setValueStep(0.05);
      transformationWeightREd.setText("");
      transformationWeightREd.setSize(new Dimension(81, 24));
      transformationWeightREd.setPreferredSize(new Dimension(90, 24));
      transformationWeightREd.setLocation(new Point(238, 6));
      transformationWeightREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return transformationWeightREd;
  }

  public JWFNumberField getRelWeightREd() {
    return relWeightREd;
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setPreferredSize(new Dimension(6, 400));
      scrollPane.setViewportView(getHelpPane());
    }
    return scrollPane;
  }

  private JTextPane getHelpPane() {
    if (helpPane == null) {
      helpPane = new JTextPane();
      helpPane.setBackground(SystemColor.menu);
      helpPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
      helpPane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
              java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      helpPane.setEditable(false);
    }
    return helpPane;
  }

  private JScrollPane getScrollPane_1() {
    if (scrollPane_1 == null) {
      scrollPane_1 = new JScrollPane();
      scrollPane_1.setViewportView(getTextPane_1());
    }
    return scrollPane_1;
  }

  private JTextPane getTextPane_1() {
    if (faqPane == null) {
      faqPane = new JTextPane();
      faqPane.setBackground(SystemColor.menu);
      faqPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
      faqPane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
              java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      faqPane.setEditable(false);
    }
    return faqPane;
  }

  public JTextPane getFaqPane() {
    return getTextPane_1();
  }

  public JToggleButton getMouseTransformShearButton() {
    return mouseTransformEditPointsButton;
  }

  private JButton getUndoButton() {
    if (undoButton == null) {
      undoButton = new JButton();
      undoButton.setIconTextGap(0);
      undoButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.undoAction();
        }
      });
      undoButton.setToolTipText("Undo");
      undoButton.setText("Undo");
      undoButton.setPreferredSize(new Dimension(72, 24));
      undoButton.setMnemonic(KeyEvent.VK_Z);
      undoButton.setFont(new Font("Dialog", Font.BOLD, 9));
      undoButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return undoButton;
  }

  private JLabel getLabel_5() {
    if (label_5 == null) {
      label_5 = new JLabel();
      label_5.setText("");
      label_5.setPreferredSize(new Dimension(42, 4));
      label_5.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_5;
  }

  private JButton getRedoButton() {
    if (redoButton == null) {
      redoButton = new JButton();
      redoButton.setIconTextGap(0);
      redoButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.redoAction();
        }
      });
      redoButton.setToolTipText("Redo");
      redoButton.setText("Redo");
      redoButton.setPreferredSize(new Dimension(72, 24));
      redoButton.setMnemonic(KeyEvent.VK_Y);
      redoButton.setFont(new Font("Dialog", Font.BOLD, 9));
      redoButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-redo-6.png")));
    }
    return redoButton;
  }

  public JButton getInteractivePauseButton() {
    return interactivePauseButton;
  }

  public JButton getInteractiveResumeButton() {
    return interactiveResumeButton;
  }

  private JPanel getAntialiasPanel() {
    if (antialiasPanel == null) {
      antialiasPanel = new JPanel();
      antialiasPanel.setLayout(null);
      antialiasPanel.add(getXFormAntialiasAmountREd());
      antialiasPanel.add(getXFormAntialiasAmountLbl());
      antialiasPanel.add(getXFormAntialiasAmountSlider());
      antialiasPanel.add(getXFormAntialiasRadiusREd());
      antialiasPanel.add(getXFormAntialiasRadiusLbl());
      antialiasPanel.add(getXFormAntialiasRadiusSlider());

      JLabel lblSpatialFilterRadius = new JLabel();
      lblSpatialFilterRadius.setText("Spatial filter radius");
      lblSpatialFilterRadius.setSize(new Dimension(94, 22));
      lblSpatialFilterRadius.setPreferredSize(new Dimension(94, 22));
      lblSpatialFilterRadius.setLocation(new Point(488, 2));
      lblSpatialFilterRadius.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpatialFilterRadius.setBounds(450, 6, 94, 22);
      antialiasPanel.add(lblSpatialFilterRadius);

      tinaFilterRadiusREd = new JWFNumberField();
      tinaFilterRadiusREd.setLinkedMotionControlName("tinaFilterRadiusSlider");
      tinaFilterRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaFilterRadiusREd.isMouseAdjusting() || tinaFilterRadiusREd.getMouseChangeCount() == 0) {
              if (!tinaFilterRadiusSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().filterRadiusREd_changed();
          }
        }
      });
      tinaFilterRadiusREd.setValueStep(0.05);
      tinaFilterRadiusREd.setText("");
      tinaFilterRadiusREd.setSize(new Dimension(100, 24));
      tinaFilterRadiusREd.setPreferredSize(new Dimension(100, 24));
      tinaFilterRadiusREd.setMaxValue(5.0);
      tinaFilterRadiusREd.setLocation(new Point(584, 2));
      tinaFilterRadiusREd.setHasMinValue(true);
      tinaFilterRadiusREd.setHasMaxValue(true);
      tinaFilterRadiusREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaFilterRadiusREd.setEditable(true);
      tinaFilterRadiusREd.setBounds(546, 6, 100, 24);
      antialiasPanel.add(tinaFilterRadiusREd);

      tinaFilterRadiusSlider = new JSlider();
      tinaFilterRadiusSlider.setName("tinaFilterRadiusSlider");
      tinaFilterRadiusSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().filterRadiusSlider_stateChanged(e);
          }
        }
      });
      tinaFilterRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaFilterRadiusSlider.setValue(0);
      tinaFilterRadiusSlider.setSize(new Dimension(220, 19));
      tinaFilterRadiusSlider.setPreferredSize(new Dimension(220, 19));
      tinaFilterRadiusSlider.setMinimum(0);
      tinaFilterRadiusSlider.setMaximum(500);
      tinaFilterRadiusSlider.setLocation(new Point(686, 2));
      tinaFilterRadiusSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaFilterRadiusSlider.setBounds(648, 6, 220, 24);
      antialiasPanel.add(tinaFilterRadiusSlider);

      tinaFilterKernelCmb = new JComboBox();
      tinaFilterKernelCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.spatialFilterKernelCmb_changed();
          }
        }
      });
      tinaFilterKernelCmb.setSize(new Dimension(125, 22));
      tinaFilterKernelCmb.setPreferredSize(new Dimension(125, 22));
      tinaFilterKernelCmb.setLocation(new Point(100, 4));
      tinaFilterKernelCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaFilterKernelCmb.setBounds(546, 31, 100, 24);
      antialiasPanel.add(tinaFilterKernelCmb);

      JLabel lblSpatialFilterKernel = new JLabel();
      lblSpatialFilterKernel.setText("Spatial filter kernel");
      lblSpatialFilterKernel.setSize(new Dimension(94, 22));
      lblSpatialFilterKernel.setPreferredSize(new Dimension(94, 22));
      lblSpatialFilterKernel.setLocation(new Point(488, 2));
      lblSpatialFilterKernel.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpatialFilterKernel.setBounds(450, 31, 94, 22);
      antialiasPanel.add(lblSpatialFilterKernel);

    }
    return antialiasPanel;
  }

  private JWFNumberField getXFormAntialiasAmountREd() {
    if (xFormAntialiasAmountREd == null) {
      xFormAntialiasAmountREd = new JWFNumberField();
      xFormAntialiasAmountREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormAntialiasAmountREd.isMouseAdjusting() || xFormAntialiasAmountREd.getMouseChangeCount() == 0) {
              if (!xFormAntialiasAmountSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().xFormAntialiasAmountREd_changed();
          }
        }
      });
      xFormAntialiasAmountREd.setValueStep(0.05);
      xFormAntialiasAmountREd.setText("");
      xFormAntialiasAmountREd.setSize(new Dimension(55, 22));
      xFormAntialiasAmountREd.setPreferredSize(new Dimension(55, 22));
      xFormAntialiasAmountREd.setMaxValue(1.0);
      xFormAntialiasAmountREd.setLocation(new Point(68, 4));
      xFormAntialiasAmountREd.setHasMinValue(true);
      xFormAntialiasAmountREd.setHasMaxValue(true);
      xFormAntialiasAmountREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormAntialiasAmountREd.setBounds(116, 6, 100, 22);
    }
    return xFormAntialiasAmountREd;
  }

  private JLabel getXFormAntialiasAmountLbl() {
    if (xFormAntialiasAmountLbl == null) {
      xFormAntialiasAmountLbl = new JLabel();
      xFormAntialiasAmountLbl.setText("Antialiasing amount");
      xFormAntialiasAmountLbl.setSize(new Dimension(64, 22));
      xFormAntialiasAmountLbl.setPreferredSize(new Dimension(64, 22));
      xFormAntialiasAmountLbl.setLocation(new Point(4, 4));
      xFormAntialiasAmountLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormAntialiasAmountLbl.setBounds(6, 6, 113, 22);
    }
    return xFormAntialiasAmountLbl;
  }

  private JSlider getXFormAntialiasAmountSlider() {
    if (xFormAntialiasAmountSlider == null) {
      xFormAntialiasAmountSlider = new JSlider();
      xFormAntialiasAmountSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().xFormAntialiasAmountSlider_changed();
          }
        }
      });
      xFormAntialiasAmountSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormAntialiasAmountSlider.setValue(0);
      xFormAntialiasAmountSlider.setSize(new Dimension(220, 19));
      xFormAntialiasAmountSlider.setPreferredSize(new Dimension(220, 19));
      xFormAntialiasAmountSlider.setMinimum(0);
      xFormAntialiasAmountSlider.setMaximum(100);
      xFormAntialiasAmountSlider.setLocation(new Point(123, 4));
      xFormAntialiasAmountSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormAntialiasAmountSlider.setBounds(218, 6, 220, 22);
    }
    return xFormAntialiasAmountSlider;
  }

  private JWFNumberField getXFormAntialiasRadiusREd() {
    if (xFormAntialiasRadiusREd == null) {
      xFormAntialiasRadiusREd = new JWFNumberField();
      xFormAntialiasRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormAntialiasRadiusREd.isMouseAdjusting() || xFormAntialiasRadiusREd.getMouseChangeCount() == 0) {
              if (!xFormAntialiasRadiusSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().xFormAntialiasRadiusREd_changed();
          }
        }
      });
      xFormAntialiasRadiusREd.setValueStep(0.05);
      xFormAntialiasRadiusREd.setText("");
      xFormAntialiasRadiusREd.setSize(new Dimension(55, 22));
      xFormAntialiasRadiusREd.setPreferredSize(new Dimension(55, 22));
      xFormAntialiasRadiusREd.setMaxValue(2.0);
      xFormAntialiasRadiusREd.setLocation(new Point(68, 4));
      xFormAntialiasRadiusREd.setHasMinValue(true);
      xFormAntialiasRadiusREd.setHasMaxValue(true);
      xFormAntialiasRadiusREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormAntialiasRadiusREd.setBounds(116, 28, 100, 22);
    }
    return xFormAntialiasRadiusREd;
  }

  private JLabel getXFormAntialiasRadiusLbl() {
    if (xFormAntialiasRadiusLbl == null) {
      xFormAntialiasRadiusLbl = new JLabel();
      xFormAntialiasRadiusLbl.setText("Antialiasing radius");
      xFormAntialiasRadiusLbl.setSize(new Dimension(64, 22));
      xFormAntialiasRadiusLbl.setPreferredSize(new Dimension(64, 22));
      xFormAntialiasRadiusLbl.setLocation(new Point(4, 4));
      xFormAntialiasRadiusLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormAntialiasRadiusLbl.setBounds(6, 28, 113, 22);
    }
    return xFormAntialiasRadiusLbl;
  }

  private JSlider getXFormAntialiasRadiusSlider() {
    if (xFormAntialiasRadiusSlider == null) {
      xFormAntialiasRadiusSlider = new JSlider();
      xFormAntialiasRadiusSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().xFormAntialiasRadiusSlider_changed();
          }
        }
      });
      xFormAntialiasRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormAntialiasRadiusSlider.setValue(0);
      xFormAntialiasRadiusSlider.setSize(new Dimension(172, 22));
      xFormAntialiasRadiusSlider.setPreferredSize(new Dimension(172, 22));
      xFormAntialiasRadiusSlider.setMinimum(0);
      xFormAntialiasRadiusSlider.setMaximum(200);
      xFormAntialiasRadiusSlider.setLocation(new Point(123, 4));
      xFormAntialiasRadiusSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormAntialiasRadiusSlider.setBounds(218, 28, 220, 22);
    }
    return xFormAntialiasRadiusSlider;
  }

  private JPanel getPanel_6() {
    if (panel_6 == null) {
      panel_6 = new JPanel();
      panel_6.setMinimumSize(new Dimension(100, 10));
      panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.Y_AXIS));
      panel_6.add(getLoadFromClipboardFlameButton());
      panel_6.add(getTinaLoadFlameButton());
    }
    return panel_6;
  }

  private JPanel getPanel_7() {
    if (panel_7 == null) {
      panel_7 = new JPanel();
      panel_7.setBorder(new EmptyBorder(0, 0, 0, 8));
      panel_7.setPreferredSize(new Dimension(200, 10));
      panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.Y_AXIS));
      panel_7.add(getPanel_78());
      panel_7.add(getPanel_81());
    }
    return panel_7;
  }

  private JPanel getPanel_13() {
    if (panel_13 == null) {
      panel_13 = new JPanel();
      panel_13.setBorder(new EmptyBorder(0, 0, 0, 8));
      panel_13.setLayout(new BoxLayout(panel_13, BoxLayout.Y_AXIS));
      panel_13.add(getSaveFlameToClipboardButton());
      panel_13.add(getPanel_66());
    }
    return panel_13;
  }

  private JPanel getPanel_15() {
    if (panel_15 == null) {
      panel_15 = new JPanel();
      panel_15.setLayout(new BoxLayout(panel_15, BoxLayout.Y_AXIS));

      JPanel panel_1 = new JPanel();
      panel_15.add(panel_1);
      panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

      resolutionProfileCmb = new JComboBox();
      resolutionProfileCmb.setMinimumSize(new Dimension(100, 24));
      resolutionProfileCmb.setMaximumSize(new Dimension(32767, 24));
      panel_1.add(resolutionProfileCmb);
      resolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.resolutionProfileCmb_changed();
          }
        }
      });
      resolutionProfileCmb.setPreferredSize(new Dimension(125, 24));
      resolutionProfileCmb.setMaximumRowCount(32);
      resolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_1.add(getResolutionProfileBtn());

      JPanel panel_2 = new JPanel();
      panel_15.add(panel_2);
      panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));

      qualityProfileCmb = new JComboBox();
      qualityProfileCmb.setMinimumSize(new Dimension(100, 24));
      qualityProfileCmb.setMaximumSize(new Dimension(32767, 24));
      panel_2.add(qualityProfileCmb);
      qualityProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.qualityProfileCmb_changed();
          }
        }
      });
      qualityProfileCmb.setPreferredSize(new Dimension(125, 24));
      qualityProfileCmb.setMaximumRowCount(32);
      qualityProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_2.add(getQualityProfileBtn());
    }
    return panel_15;
  }

  private JPanel getTinaPaletteTransformPanel() {
    if (tinaPaletteTransformPanel == null) {
      tinaPaletteTransformPanel = new JPanel();
      tinaPaletteTransformPanel.setLayout(null);

      JLabel tinaPaletteSwapRGBLbl_1 = new JLabel();
      tinaPaletteSwapRGBLbl_1.setText("Swap RGB");
      tinaPaletteSwapRGBLbl_1.setSize(new Dimension(56, 22));
      tinaPaletteSwapRGBLbl_1.setPreferredSize(new Dimension(64, 22));
      tinaPaletteSwapRGBLbl_1.setLocation(new Point(6, 214));
      tinaPaletteSwapRGBLbl_1.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSwapRGBLbl_1.setBounds(6, 6, 56, 22);
      tinaPaletteTransformPanel.add(tinaPaletteSwapRGBLbl_1);

      tinaPaletteSwapRGBREd = new JWFNumberField();
      tinaPaletteSwapRGBREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaPaletteSwapRGBREd.isMouseAdjusting() || tinaPaletteSwapRGBREd.getMouseChangeCount() == 0) {
              if (!tinaPaletteSwapRGBSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.paletteSwapRGBREd_changed();
          }
        }
      });
      tinaPaletteSwapRGBREd.setText("0");
      tinaPaletteSwapRGBREd.setSize(new Dimension(56, 24));
      tinaPaletteSwapRGBREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteSwapRGBREd.setOnlyIntegers(true);
      tinaPaletteSwapRGBREd.setMinValue(-255.0);
      tinaPaletteSwapRGBREd.setMaxValue(255.0);
      tinaPaletteSwapRGBREd.setLocation(new Point(62, 214));
      tinaPaletteSwapRGBREd.setHasMinValue(true);
      tinaPaletteSwapRGBREd.setHasMaxValue(true);
      tinaPaletteSwapRGBREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteSwapRGBREd.setBounds(62, 6, 56, 24);
      tinaPaletteTransformPanel.add(tinaPaletteSwapRGBREd);

      tinaPaletteSwapRGBSlider = new JSlider();
      tinaPaletteSwapRGBSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.paletteSwapRGBSlider_stateChanged(e);
          }
        }
      });
      tinaPaletteSwapRGBSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteSwapRGBSlider.setValue(0);
      tinaPaletteSwapRGBSlider.setSize(new Dimension(74, 22));
      tinaPaletteSwapRGBSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteSwapRGBSlider.setMinimum(-255);
      tinaPaletteSwapRGBSlider.setMaximum(255);
      tinaPaletteSwapRGBSlider.setLocation(new Point(118, 214));
      tinaPaletteSwapRGBSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSwapRGBSlider.setBounds(118, 6, 204, 22);
      tinaPaletteTransformPanel.add(tinaPaletteSwapRGBSlider);

      JLabel tinaPaletteFrequencyLbl = new JLabel();
      tinaPaletteFrequencyLbl.setText("Frequency");
      tinaPaletteFrequencyLbl.setSize(new Dimension(56, 22));
      tinaPaletteFrequencyLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteFrequencyLbl.setLocation(new Point(6, 214));
      tinaPaletteFrequencyLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteFrequencyLbl.setBounds(6, 31, 56, 22);
      tinaPaletteTransformPanel.add(tinaPaletteFrequencyLbl);

      tinaPaletteFrequencyREd = new JWFNumberField();
      tinaPaletteFrequencyREd.setMinValue(1.0);
      tinaPaletteFrequencyREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaPaletteFrequencyREd.isMouseAdjusting() || tinaPaletteFrequencyREd.getMouseChangeCount() == 0) {
              if (!tinaPaletteFrequencySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.paletteFrequencyREd_changed();
          }
        }
      });
      tinaPaletteFrequencyREd.setValueStep(0.05);
      tinaPaletteFrequencyREd.setText("0");
      tinaPaletteFrequencyREd.setSize(new Dimension(56, 24));
      tinaPaletteFrequencyREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteFrequencyREd.setOnlyIntegers(true);
      tinaPaletteFrequencyREd.setMaxValue(16.0);
      tinaPaletteFrequencyREd.setLocation(new Point(62, 214));
      tinaPaletteFrequencyREd.setHasMinValue(true);
      tinaPaletteFrequencyREd.setHasMaxValue(true);
      tinaPaletteFrequencyREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteFrequencyREd.setBounds(62, 31, 56, 24);
      tinaPaletteTransformPanel.add(tinaPaletteFrequencyREd);

      tinaPaletteFrequencySlider = new JSlider();
      tinaPaletteFrequencySlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.paletteFrequencySlider_stateChanged(e);
          }
        }
      });
      tinaPaletteFrequencySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteFrequencySlider.setValue(0);
      tinaPaletteFrequencySlider.setSize(new Dimension(74, 22));
      tinaPaletteFrequencySlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteFrequencySlider.setMinimum(1);
      tinaPaletteFrequencySlider.setMaximum(16);
      tinaPaletteFrequencySlider.setLocation(new Point(118, 214));
      tinaPaletteFrequencySlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteFrequencySlider.setBounds(118, 31, 204, 22);
      tinaPaletteTransformPanel.add(tinaPaletteFrequencySlider);

      JLabel tinaPaletteBlurLbl = new JLabel();
      tinaPaletteBlurLbl.setText("Blur");
      tinaPaletteBlurLbl.setSize(new Dimension(56, 22));
      tinaPaletteBlurLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteBlurLbl.setLocation(new Point(6, 214));
      tinaPaletteBlurLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBlurLbl.setBounds(6, 55, 56, 22);
      tinaPaletteTransformPanel.add(tinaPaletteBlurLbl);

      tinaPaletteBlurREd = new JWFNumberField();
      tinaPaletteBlurREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaPaletteBlurREd.isMouseAdjusting() || tinaPaletteBlurREd.getMouseChangeCount() == 0) {
              if (!tinaPaletteBlurSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.paletteBlurREd_changed();
          }
        }
      });
      tinaPaletteBlurREd.setText("0");
      tinaPaletteBlurREd.setSize(new Dimension(56, 24));
      tinaPaletteBlurREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteBlurREd.setOnlyIntegers(true);
      tinaPaletteBlurREd.setMaxValue(127.0);
      tinaPaletteBlurREd.setLocation(new Point(62, 214));
      tinaPaletteBlurREd.setHasMinValue(true);
      tinaPaletteBlurREd.setHasMaxValue(true);
      tinaPaletteBlurREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteBlurREd.setBounds(62, 55, 56, 24);
      tinaPaletteTransformPanel.add(tinaPaletteBlurREd);

      tinaPaletteBlurSlider = new JSlider();
      tinaPaletteBlurSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.paletteBlurSlider_stateChanged(e);
          }
        }
      });
      tinaPaletteBlurSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteBlurSlider.setValue(0);
      tinaPaletteBlurSlider.setSize(new Dimension(74, 22));
      tinaPaletteBlurSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteBlurSlider.setMaximum(127);
      tinaPaletteBlurSlider.setLocation(new Point(118, 214));
      tinaPaletteBlurSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBlurSlider.setBounds(118, 55, 204, 22);
      tinaPaletteTransformPanel.add(tinaPaletteBlurSlider);

      tinaPaletteInvertBtn = new JButton();
      tinaPaletteInvertBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.paletteInvertBtn_clicked();
        }
      });
      tinaPaletteInvertBtn.setToolTipText("Turn into negative colors");
      tinaPaletteInvertBtn.setText("Invert");
      tinaPaletteInvertBtn.setSize(new Dimension(138, 24));
      tinaPaletteInvertBtn.setPreferredSize(new Dimension(136, 24));
      tinaPaletteInvertBtn.setLocation(new Point(4, 181));
      tinaPaletteInvertBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteInvertBtn.setBounds(341, 5, 88, 24);
      tinaPaletteTransformPanel.add(tinaPaletteInvertBtn);

      tinaPaletteReverseBtn = new JButton();
      tinaPaletteReverseBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.paletteReverseBtn_clicked();
        }
      });
      tinaPaletteReverseBtn.setToolTipText("Reverse color order");
      tinaPaletteReverseBtn.setText("Reverse");
      tinaPaletteReverseBtn.setSize(new Dimension(138, 24));
      tinaPaletteReverseBtn.setPreferredSize(new Dimension(136, 24));
      tinaPaletteReverseBtn.setLocation(new Point(4, 181));
      tinaPaletteReverseBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteReverseBtn.setBounds(341, 30, 88, 24);
      tinaPaletteTransformPanel.add(tinaPaletteReverseBtn);

      JButton tinaPaletteSortBtn = new JButton();
      tinaPaletteSortBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.paletteSortBtn_clicked();
        }
      });
      tinaPaletteSortBtn.setToolTipText("Sort the colors (by hue and brightness)");
      tinaPaletteSortBtn.setText("Sort");
      tinaPaletteSortBtn.setSize(new Dimension(138, 24));
      tinaPaletteSortBtn.setPreferredSize(new Dimension(136, 24));
      tinaPaletteSortBtn.setLocation(new Point(4, 181));
      tinaPaletteSortBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSortBtn.setBounds(341, 54, 88, 24);
      tinaPaletteTransformPanel.add(tinaPaletteSortBtn);

      gradientApplyTXBtn = new JButton();
      gradientApplyTXBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientApplyTXBtn_clicked();
        }
      });
      gradientApplyTXBtn.setToolTipText("Apply all changes from the Modifications-tab permanently to this gradient and reset all of those options");
      gradientApplyTXBtn.setText("Apply all current modifications");
      gradientApplyTXBtn.setSize(new Dimension(190, 24));
      gradientApplyTXBtn.setPreferredSize(new Dimension(190, 24));
      gradientApplyTXBtn.setLocation(new Point(6, 230));
      gradientApplyTXBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      gradientApplyTXBtn.setBounds(6, 83, 316, 24);
      tinaPaletteTransformPanel.add(gradientApplyTXBtn);
    }
    return tinaPaletteTransformPanel;
  }

  public JWFNumberField getTinaPaletteSwapRGBREd() {
    return tinaPaletteSwapRGBREd;
  }

  public JSlider getTinaPaletteSwapRGBSlider() {
    return tinaPaletteSwapRGBSlider;
  }

  public JWFNumberField getTinaPaletteFrequencyREd() {
    return tinaPaletteFrequencyREd;
  }

  public JSlider getTinaPaletteFrequencySlider() {
    return tinaPaletteFrequencySlider;
  }

  public JWFNumberField getTinaPaletteBlurREd() {
    return tinaPaletteBlurREd;
  }

  public JSlider getTinaPaletteBlurSlider() {
    return tinaPaletteBlurSlider;
  }

  public JButton getTinaPaletteInvertBtn() {
    return tinaPaletteInvertBtn;
  }

  public JButton getTinaPaletteReverseBtn() {
    return tinaPaletteReverseBtn;
  }

  private JButton getSnapShotButton() {
    if (snapShotButton == null) {
      snapShotButton = new JButton();
      snapShotButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.snapshotButton_clicked();
        }
      });
      snapShotButton.setToolTipText("Create a snapshot of the current flame and store it to the thumbnail ribbon");
      snapShotButton.setText("SShot");
      snapShotButton.setPreferredSize(new Dimension(72, 24));
      snapShotButton.setMnemonic(KeyEvent.VK_H);
      snapShotButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return snapShotButton;
  }

  private JButton getBtnQsave() {
    if (btnQsave == null) {
      btnQsave = new JButton();
      btnQsave.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.quicksaveButton_clicked();
        }
      });
      btnQsave.setToolTipText("Quicksave the current flame");
      btnQsave.setText("Q");
      btnQsave.setPreferredSize(new Dimension(60, 24));
      btnQsave.setMnemonic(KeyEvent.VK_Q);
      btnQsave.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return btnQsave;
  }

  private JLabel getLabel_6() {
    if (label_6 == null) {
      label_6 = new JLabel();
      label_6.setText("");
      label_6.setPreferredSize(new Dimension(42, 4));
      label_6.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_6;
  }

  private JPanel getPanel_20() {
    if (panel_20 == null) {
      panel_20 = new JPanel();
      panel_20.setMaximumSize(new Dimension(159, 36));
    }
    return panel_20;
  }

  private JPanel getPanel_21() {
    if (panel_21 == null) {
      panel_21 = new JPanel();
      panel_21.setMaximumSize(new Dimension(159, 36));
    }
    return panel_21;
  }

  private JPanel getPanel_22() {
    if (panel_22 == null) {
      panel_22 = new JPanel();
      panel_22.setMaximumSize(new Dimension(32767, 36));
      panel_22.setLayout(null);

      JLabel lblGlobalSettings = new JLabel();
      lblGlobalSettings.setText("Global settings:");
      lblGlobalSettings.setPreferredSize(new Dimension(100, 22));
      lblGlobalSettings.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGlobalSettings.setAlignmentX(1.0f);
      lblGlobalSettings.setBounds(6, 20, 100, 14);
      panel_22.add(lblGlobalSettings);
    }
    return panel_22;
  }

  private JPanel getPanel_23() {
    if (panel_23 == null) {
      panel_23 = new JPanel();
      panel_23.setPreferredSize(new Dimension(10, 28));
      panel_23.setMinimumSize(new Dimension(10, 28));
      panel_23.setMaximumSize(new Dimension(32767, 28));
      panel_23.setLayout(new BoxLayout(panel_23, BoxLayout.X_AXIS));
      batchRenderJobProgressLbl = new JLabel();
      batchRenderJobProgressLbl.setMinimumSize(new Dimension(100, 0));
      panel_23.add(batchRenderJobProgressLbl);
      batchRenderJobProgressLbl.setPreferredSize(new Dimension(100, 22));
      batchRenderJobProgressLbl.setText("Job progress");
      batchRenderJobProgressLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_23.add(getBatchRenderJobProgressBar());
    }
    return panel_23;
  }

  private JPanel getPanel_24() {
    if (panel_24 == null) {
      panel_24 = new JPanel();
      panel_24.setPreferredSize(new Dimension(10, 28));
      panel_24.setMinimumSize(new Dimension(10, 28));
      panel_24.setMaximumSize(new Dimension(32767, 28));
      panel_24.setLayout(new BoxLayout(panel_24, BoxLayout.X_AXIS));
      batchRenderTotalProgressLbl = new JLabel();
      batchRenderTotalProgressLbl.setMinimumSize(new Dimension(100, 0));
      panel_24.add(batchRenderTotalProgressLbl);
      batchRenderTotalProgressLbl.setPreferredSize(new Dimension(100, 22));
      batchRenderTotalProgressLbl.setText("Total progress");
      batchRenderTotalProgressLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_24.add(getBatchRenderTotalProgressBar());
    }
    return panel_24;
  }

  private JPanel getPanel_25() {
    if (panel_25 == null) {
      panel_25 = new JPanel();
      panel_25.setLayout(new BoxLayout(panel_25, BoxLayout.Y_AXIS));
      panel_25.add(getPanel_23());
      panel_25.add(getPanel_24());
    }
    return panel_25;
  }

  private JPanel getPanel_26() {
    if (panel_26 == null) {
      panel_26 = new JPanel();
      panel_26.setPreferredSize(new Dimension(6, 10));
      panel_26.setMinimumSize(new Dimension(6, 10));
      panel_26.setMaximumSize(new Dimension(6, 32767));
    }
    return panel_26;
  }

  private JPanel getPanel_27() {
    if (panel_27 == null) {
      panel_27 = new JPanel();
      panel_27.setBorder(new EmptyBorder(0, 6, 0, 6));
      panel_27.setMinimumSize(new Dimension(110, 10));
      panel_27.setMaximumSize(new Dimension(150, 32767));
      panel_27.setLayout(new BoxLayout(panel_27, BoxLayout.Y_AXIS));

      interactiveLoadFlameFromMainButton = new JButton();
      panel_27.add(interactiveLoadFlameFromMainButton);
      interactiveLoadFlameFromMainButton.setMaximumSize(new Dimension(32000, 24));
      interactiveLoadFlameFromMainButton.setMinimumSize(new Dimension(100, 24));
      interactiveLoadFlameFromMainButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().fromEditorButton_clicked();
        }
      });
      interactiveLoadFlameFromMainButton.setToolTipText("Load flame from Editor and render");
      interactiveLoadFlameFromMainButton.setText("From Editor");
      interactiveLoadFlameFromMainButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameFromMainButton.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_27.add(getInteractiveLoadFlameFromClipboardButton());
      panel_27.add(getInteractiveLoadFlameButton());
    }
    return panel_27;
  }

  private JPanel getPanel_28() {
    if (panel_28 == null) {
      panel_28 = new JPanel();
      panel_28.setBorder(new EmptyBorder(0, 6, 0, 6));
      panel_28.setMaximumSize(new Dimension(250, 32767));
      panel_28.setMinimumSize(new Dimension(200, 10));
      panel_28.setLayout(new BoxLayout(panel_28, BoxLayout.Y_AXIS));

      interactiveHalfSizeButton = new JToggleButton();
      panel_28.add(interactiveHalfSizeButton);
      interactiveHalfSizeButton.setMinimumSize(new Dimension(125, 24));
      interactiveHalfSizeButton.setMaximumSize(new Dimension(32000, 24));
      interactiveHalfSizeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().halveSizeButton_clicked();
        }
      });
      interactiveHalfSizeButton.setToolTipText("Switch to half render resolution (to get rid of scroll bars in exploration mode)");
      interactiveHalfSizeButton.setText("Half size");
      interactiveHalfSizeButton.setPreferredSize(new Dimension(100, 24));
      interactiveHalfSizeButton.setMnemonic(KeyEvent.VK_M);
      interactiveHalfSizeButton.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_28.add(getPanel_29());
      panel_28.add(getPanel_30());
    }
    return panel_28;
  }

  private JPanel getPanel_29() {
    if (panel_29 == null) {
      panel_29 = new JPanel();
      panel_29.setLayout(new BoxLayout(panel_29, BoxLayout.X_AXIS));
      panel_29.add(getLabel_1());
      panel_29.add(getInteractiveResolutionProfileCmb());
    }
    return panel_29;
  }

  private JPanel getPanel_30() {
    if (panel_30 == null) {
      panel_30 = new JPanel();
      panel_30.setLayout(new BoxLayout(panel_30, BoxLayout.X_AXIS));
    }
    return panel_30;
  }

  private JPanel getPanel_31() {
    if (panel_31 == null) {
      panel_31 = new JPanel();
      panel_31.setLayout(new BoxLayout(panel_31, BoxLayout.X_AXIS));

      JLabel label = new JLabel();
      panel_31.add(label);
      label.setText("Random generator");
      label.setPreferredSize(new Dimension(94, 22));
      label.setFont(new Font("Dialog", Font.BOLD, 10));

      interactiveRandomStyleCmb = new JComboBox();
      panel_31.add(interactiveRandomStyleCmb);
      interactiveRandomStyleCmb.setMinimumSize(new Dimension(100, 24));
      interactiveRandomStyleCmb.setMaximumSize(new Dimension(32767, 24));
      interactiveRandomStyleCmb.setPreferredSize(new Dimension(125, 24));
      interactiveRandomStyleCmb.setMaximumRowCount(32);
      interactiveRandomStyleCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveRandomStyleCmb.setMaximumRowCount(32);
      interactiveRandomStyleCmb.removeAllItems();
      interactiveRandomStyleCmb.setSelectedItem(RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);
    }
    return panel_31;
  }

  private JPanel getPanel_32() {
    if (panel_32 == null) {
      panel_32 = new JPanel();
      panel_32.setBorder(new EmptyBorder(0, 11, 9, 11));
      panel_32.setMinimumSize(new Dimension(200, 10));
      panel_32.setMaximumSize(new Dimension(250, 32767));
      panel_32.setLayout(new BoxLayout(panel_32, BoxLayout.Y_AXIS));
      panel_32.add(getPanel_31());
      panel_32.add(getInteractiveNextButton());
    }
    return panel_32;
  }

  private JPanel getPanel_33() {
    if (panel_33 == null) {
      panel_33 = new JPanel();
      panel_33.setBorder(new EmptyBorder(0, 3, 0, 3));
      panel_33.setMaximumSize(new Dimension(150, 32767));
      panel_33.setLayout(new BoxLayout(panel_33, BoxLayout.Y_AXIS));

      interactivePauseButton = new JButton();
      panel_33.add(interactivePauseButton);
      interactivePauseButton.setMinimumSize(new Dimension(100, 24));
      interactivePauseButton.setMaximumSize(new Dimension(3200, 24));
      interactivePauseButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().pauseBtn_clicked();
        }
      });
      interactivePauseButton.setToolTipText("Save the current state for later resuming");
      interactivePauseButton.setText("Save render state");
      interactivePauseButton.setPreferredSize(new Dimension(125, 24));
      interactivePauseButton.setMnemonic(KeyEvent.VK_T);
      interactivePauseButton.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_33.add(getInteractiveSaveImageButton());
    }
    return panel_33;
  }

  private JPanel getPanel_35() {
    if (panel_35 == null) {
      panel_35 = new JPanel();
      panel_35.setBorder(new EmptyBorder(0, 3, 0, 16));
      panel_35.setMaximumSize(new Dimension(150, 32767));
      panel_35.setLayout(new BoxLayout(panel_35, BoxLayout.Y_AXIS));

      interactiveFlameToEditorButton = new JButton();
      panel_35.add(interactiveFlameToEditorButton);
      interactiveFlameToEditorButton.setMinimumSize(new Dimension(100, 24));
      interactiveFlameToEditorButton.setMaximumSize(new Dimension(32000, 24));
      interactiveFlameToEditorButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().toEditorButton_clicked();
        }
      });
      interactiveFlameToEditorButton.setToolTipText("Copy the current fractal into the Editor");
      interactiveFlameToEditorButton.setText("To Editor");
      interactiveFlameToEditorButton.setPreferredSize(new Dimension(125, 24));
      interactiveFlameToEditorButton.setMnemonic(KeyEvent.VK_D);
      interactiveFlameToEditorButton.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_35.add(getInteractiveFlameToClipboardButton());
      panel_35.add(getInteractiveSaveFlameButton());
    }
    return panel_35;
  }

  public JToggleButton getToggleTransparencyButton() {
    if (toggleTransparencyButton == null) {
      toggleTransparencyButton = new JToggleButton();
      toggleTransparencyButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.toggleTransparencyButton_clicked();
        }
      });
      toggleTransparencyButton.setToolTipText("Display/hide transparency");
      toggleTransparencyButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/transparency.png")));
      toggleTransparencyButton.setSize(new Dimension(95, 24));
      toggleTransparencyButton.setSelected(false);
      toggleTransparencyButton.setPreferredSize(new Dimension(42, 24));
      toggleTransparencyButton.setLocation(new Point(4, 4));
      toggleTransparencyButton.setBounds(117, 4, 42, 24);
    }
    return toggleTransparencyButton;
  }

  public JCheckBox getBgTransparencyCBx() {
    return bgTransparencyCBx;
  }

  private JPanel getPanel_36() {
    if (panel_36 == null) {
      panel_36 = new JPanel();
      panel_36.setLayout(new BorderLayout(0, 0));
      panel_36.add(getPanel_46(), BorderLayout.EAST);
      panel_36.add(getPanel_43());
    }
    return panel_36;
  }

  private JButton getDancingFlamesStopShowBtn() {
    if (dancingFlamesStopShowBtn == null) {
      dancingFlamesStopShowBtn = new JButton();
      dancingFlamesStopShowBtn.setBounds(413, 90, 125, 24);
      dancingFlamesStopShowBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().stopShow();
        }
      });
      dancingFlamesStopShowBtn.setText("Stop show");
      dancingFlamesStopShowBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesStopShowBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesStopShowBtn.setMaximumSize(new Dimension(32000, 24));
      dancingFlamesStopShowBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return dancingFlamesStopShowBtn;
  }

  public JPanel getRealtimeFlamePnl() {
    return dancingFlamesFlamePnl;
  }

  public JPanel getRealtimeGraph1Pnl() {
    return dancingFlamesGraph1Pnl;
  }

  private JButton getTinaAppendToDancingFlamesButton() {
    if (tinaAppendToDancingFlamesButton == null) {
      tinaAppendToDancingFlamesButton = new JButton();
      tinaAppendToDancingFlamesButton.setIconTextGap(0);
      tinaAppendToDancingFlamesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController.getCurrFlame() != null) {
            tinaController.getDancingFractalsController().importFlame(tinaController.getCurrFlame());
            rootTabbedPane.setSelectedIndex(DancingFractalsController.PAGE_INDEX);
          }
        }
      });
      tinaAppendToDancingFlamesButton.setToolTipText("Append to dancing flames show");
      tinaAppendToDancingFlamesButton.setText("Dance");
      tinaAppendToDancingFlamesButton.setPreferredSize(new Dimension(72, 24));
      tinaAppendToDancingFlamesButton.setMnemonic(KeyEvent.VK_D);
      tinaAppendToDancingFlamesButton.setFont(new Font("Dialog", Font.BOLD, 9));
      tinaAppendToDancingFlamesButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/kipina.png")));
    }
    return tinaAppendToDancingFlamesButton;
  }

  private JButton getDancingFlamesAddFromEditorBtn() {
    if (dancingFlamesAddFromEditorBtn == null) {
      dancingFlamesAddFromEditorBtn = new JButton();
      dancingFlamesAddFromEditorBtn.setBounds(320, 6, 135, 24);
      dancingFlamesAddFromEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().importFlame(tinaController.getCurrFlame());
        }
      });
      dancingFlamesAddFromEditorBtn.setMinimumSize(new Dimension(125, 24));
      dancingFlamesAddFromEditorBtn.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesAddFromEditorBtn.setToolTipText("Load flame from Editor and add it to the movie");
      dancingFlamesAddFromEditorBtn.setText("Add from Editor");
      dancingFlamesAddFromEditorBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesAddFromEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return dancingFlamesAddFromEditorBtn;
  }

  private JButton getDancingFlamesAddFromClipboardBtn() {
    if (dancingFlamesAddFromClipboardBtn == null) {
      dancingFlamesAddFromClipboardBtn = new JButton();
      dancingFlamesAddFromClipboardBtn.setBounds(320, 31, 135, 24);
      dancingFlamesAddFromClipboardBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().loadFlameFromClipboardButton_clicked();
        }
      });
      dancingFlamesAddFromClipboardBtn.setMinimumSize(new Dimension(125, 24));
      dancingFlamesAddFromClipboardBtn.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesAddFromClipboardBtn.setToolTipText("Load flame from clipboard and add it to the movie");
      dancingFlamesAddFromClipboardBtn.setText("Add from Clipboard");
      dancingFlamesAddFromClipboardBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesAddFromClipboardBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return dancingFlamesAddFromClipboardBtn;
  }

  private JButton getDancingFlamesAddFromDiscBtn() {
    if (dancingFlamesAddFromDiscBtn == null) {
      dancingFlamesAddFromDiscBtn = new JButton();
      dancingFlamesAddFromDiscBtn.setBounds(457, 31, 135, 24);
      dancingFlamesAddFromDiscBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().loadFlameButton_clicked();
        }
      });
      dancingFlamesAddFromDiscBtn.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesAddFromDiscBtn.setMinimumSize(new Dimension(125, 24));
      dancingFlamesAddFromDiscBtn.setToolTipText("Load flames from file and add it to the movie");
      dancingFlamesAddFromDiscBtn.setText("Add flames from disc");
      dancingFlamesAddFromDiscBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesAddFromDiscBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return dancingFlamesAddFromDiscBtn;
  }

  private JPanel getPanel_43() {
    if (panel_43 == null) {
      panel_43 = new JPanel();
      panel_43.setLayout(new BorderLayout(0, 0));
      panel_43.add(getPanel_49(), BorderLayout.SOUTH);
      panel_43.add(getPanel_52(), BorderLayout.CENTER);
    }
    return panel_43;
  }

  public JButton getDancingFlamesLoadSoundBtn() {
    return dancingFlamesLoadSoundBtn;
  }

  private JLabel getLblRandomGenerator() {
    if (lblRandomGenerator == null) {
      lblRandomGenerator = new JLabel();
      lblRandomGenerator.setBounds(0, 11, 100, 14);
      lblRandomGenerator.setMinimumSize(new Dimension(100, 24));
      lblRandomGenerator.setText("Random generator");
      lblRandomGenerator.setPreferredSize(new Dimension(100, 24));
      lblRandomGenerator.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblRandomGenerator;
  }

  private JPanel getPanel_52() {
    if (panel_52 == null) {
      panel_52 = new JPanel();
      panel_52.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_52.setLayout(new BorderLayout(0, 0));
      panel_52.add(getPanel_54(), BorderLayout.SOUTH);

      dancingFlamesFlamePnl = new JPanel();
      panel_52.add(dancingFlamesFlamePnl, BorderLayout.CENTER);
      dancingFlamesFlamePnl.setBorder(new EmptyBorder(25, 25, 25, 25));
      dancingFlamesFlamePnl.setLayout(new BorderLayout(0, 0));
    }
    return panel_52;
  }

  private JPanel getPanel_54() {
    if (panel_54 == null) {
      panel_54 = new JPanel();
      panel_54.setBorder(new EmptyBorder(0, 0, 0, 0));
      panel_54.setPreferredSize(new Dimension(10, 100));

      dancingFlamesGraph1Pnl = new JPanel();
      dancingFlamesGraph1Pnl.setMinimumSize(new Dimension(180, 24));
      dancingFlamesGraph1Pnl.setPreferredSize(new Dimension(180, 24));
      panel_54.add(dancingFlamesGraph1Pnl);
      dancingFlamesGraph1Pnl.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
      dancingFlamesGraph1Pnl.setLayout(new BorderLayout(0, 0));
      panel_54.add(getPanel_37());
      panel_54.add(getPanel_41());
      panel_54.add(getDancingFlamesDrawTrianglesCBx());
      panel_54.add(getDancingFlamesDrawFFTCBx());
      panel_54.add(getDancingFlamesDrawFPSCBx());

      dancingFlamesMutedCBx = new JCheckBox("Muted");
      dancingFlamesMutedCBx.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getDancingFractalsController().mutedCBx_changed();
          }
        }
      });
      dancingFlamesMutedCBx.setPreferredSize(new Dimension(110, 18));
      dancingFlamesMutedCBx.setMinimumSize(new Dimension(140, 18));
      dancingFlamesMutedCBx.setMaximumSize(new Dimension(160, 18));
      panel_54.add(dancingFlamesMutedCBx);
    }
    return panel_54;
  }

  public JComboBox getDancingFlamesRandomGenCmb() {
    return dancingFlamesRandomGenCmb;
  }

  public JWFNumberField getDancingFlamesRandomCountIEd() {
    return dancingFlamesRandomCountIEd;
  }

  public JButton getDancingFlamesGenRandFlamesBtn() {
    return dancingFlamesGenRandFlamesBtn;
  }

  public JPanel getDancingFlamesPoolFlamePreviewPnl() {
    return dancingFlamesPoolFlamePreviewPnl;
  }

  private JLabel getLblBorderSize() {
    if (lblBorderSize == null) {
      lblBorderSize = new JLabel();
      lblBorderSize.setHorizontalAlignment(SwingConstants.RIGHT);
      lblBorderSize.setText("Border size");
      lblBorderSize.setPreferredSize(new Dimension(60, 24));
      lblBorderSize.setMinimumSize(new Dimension(100, 24));
      lblBorderSize.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblBorderSize;
  }

  public JSlider getDancingFlamesBorderSizeSlider() {
    return dancingFlamesBorderSizeSlider;
  }

  private JButton getDancingFlamesFlameToEditorBtn() {
    return dancingFlamesFlameToEditorBtn;
  }

  private JTextField getDancingFlamesFramesPerSecondIEd() {
    if (dancingFlamesFramesPerSecondIEd == null) {
      dancingFlamesFramesPerSecondIEd = new JTextField();
      dancingFlamesFramesPerSecondIEd.setText("12");
      dancingFlamesFramesPerSecondIEd.setPreferredSize(new Dimension(56, 22));
      dancingFlamesFramesPerSecondIEd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return dancingFlamesFramesPerSecondIEd;
  }

  private JLabel getLabel_7() {
    if (label_7 == null) {
      label_7 = new JLabel();
      label_7.setText("Frames per second");
      label_7.setPreferredSize(new Dimension(110, 22));
      label_7.setHorizontalAlignment(SwingConstants.RIGHT);
      label_7.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_7;
  }

  private JButton getDancingFlamesDeleteFlameBtn() {
    return dancingFlamesDeleteFlameBtn;
  }

  private JTextField getDancingFlamesMorphFrameCountIEd() {
    if (dancingFlamesMorphFrameCountIEd == null) {
      dancingFlamesMorphFrameCountIEd = new JTextField();
      dancingFlamesMorphFrameCountIEd.setBounds(482, 57, 56, 22);
      dancingFlamesMorphFrameCountIEd.setText("0");
      dancingFlamesMorphFrameCountIEd.setPreferredSize(new Dimension(56, 22));
      dancingFlamesMorphFrameCountIEd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return dancingFlamesMorphFrameCountIEd;
  }

  private JLabel getLblMorphFrames() {
    if (lblMorphFrames == null) {
      lblMorphFrames = new JLabel();
      lblMorphFrames.setBounds(396, 57, 83, 22);
      lblMorphFrames.setText("Morph frames");
      lblMorphFrames.setPreferredSize(new Dimension(120, 22));
      lblMorphFrames.setHorizontalAlignment(SwingConstants.RIGHT);
      lblMorphFrames.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblMorphFrames;
  }

  private JPanel getBatchPreviewRootPanel() {
    if (batchPreviewRootPanel == null) {
      batchPreviewRootPanel = new JPanel();
      batchPreviewRootPanel.setMaximumSize(new Dimension(32767, 120));
      batchPreviewRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return batchPreviewRootPanel;
  }

  public JButton getDancingFlamesStartShowBtn() {
    return dancingFlamesStartShowBtn;
  }

  private JCheckBox getDancingFlamesDoRecordCBx() {
    if (dancingFlamesDoRecordCBx == null) {
      dancingFlamesDoRecordCBx = new JCheckBox("Record show");
      dancingFlamesDoRecordCBx.setBounds(226, 93, 125, 18);
      dancingFlamesDoRecordCBx.setActionCommand("Record show");
      dancingFlamesDoRecordCBx.setFont(new Font("Dialog", Font.PLAIN, 10));
      dancingFlamesDoRecordCBx.setPreferredSize(new Dimension(125, 18));
    }
    return dancingFlamesDoRecordCBx;
  }

  private JPanel getPanel_37() {
    if (panel_37 == null) {
      panel_37 = new JPanel();
      panel_37.setPreferredSize(new Dimension(180, 32));
      panel_37.setMinimumSize(new Dimension(180, 32));
      panel_37.add(getLabel_7());
      panel_37.add(getDancingFlamesFramesPerSecondIEd());
    }
    return panel_37;
  }

  private JPanel getPanel_41() {
    if (panel_41 == null) {
      panel_41 = new JPanel();
      panel_41.setMinimumSize(new Dimension(180, 32));
      panel_41.setPreferredSize(new Dimension(180, 32));
      panel_41.add(getLblBorderSize());

      dancingFlamesBorderSizeSlider = new JSlider();
      dancingFlamesBorderSizeSlider.setPaintLabels(true);
      panel_41.add(dancingFlamesBorderSizeSlider);
      dancingFlamesBorderSizeSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null)
            tinaController.getDancingFractalsController().borderSizeSlider_changed();
        }
      });
      dancingFlamesBorderSizeSlider.setValue(200);
      dancingFlamesBorderSizeSlider.setPreferredSize(new Dimension(100, 22));
      dancingFlamesBorderSizeSlider.setMaximum(640);
      dancingFlamesBorderSizeSlider.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return panel_41;
  }

  public JToggleButton getMouseTransformViewButton() {
    return mouseTransformEditViewButton;
  }

  private JPanel getPanel_49() {
    if (panel_49 == null) {
      panel_49 = new JPanel();
      panel_49.setBorder(new TitledBorder(null, "Project", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_49.setPreferredSize(new Dimension(10, 130));
      panel_49.setMinimumSize(new Dimension(10, 100));
      panel_49.setLayout(null);

      dancingFlamesFlameCmb = new JComboBox();
      dancingFlamesFlameCmb.setBounds(204, 57, 180, 24);
      panel_49.add(dancingFlamesFlameCmb);
      dancingFlamesFlameCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getDancingFractalsController().flameCmb_changed();
          }
        }
      });
      dancingFlamesFlameCmb.setPreferredSize(new Dimension(125, 24));
      dancingFlamesFlameCmb.setMinimumSize(new Dimension(125, 24));
      dancingFlamesFlameCmb.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesFlameCmb.setMaximumRowCount(32);
      dancingFlamesFlameCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesFlameCmb.setAlignmentX(1.0f);

      JLabel lblFlame = new JLabel();
      lblFlame.setBounds(154, 58, 47, 22);
      panel_49.add(lblFlame);
      lblFlame.setText("Flame");
      lblFlame.setPreferredSize(new Dimension(120, 22));
      lblFlame.setHorizontalAlignment(SwingConstants.RIGHT);
      lblFlame.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_49.add(getDancingFlamesMorphFrameCountIEd());
      panel_49.add(getLblMorphFrames());

      dancingFlamesLoadSoundBtn = new JButton();
      dancingFlamesLoadSoundBtn.setBounds(19, 57, 125, 24);
      panel_49.add(dancingFlamesLoadSoundBtn);
      dancingFlamesLoadSoundBtn.setMaximumSize(new Dimension(125, 24));
      dancingFlamesLoadSoundBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesLoadSoundBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().loadSoundButton_clicked();
        }
      });
      dancingFlamesLoadSoundBtn.setToolTipText("Load a *.wav or *.mp3 file");
      dancingFlamesLoadSoundBtn.setText("Load sound");
      dancingFlamesLoadSoundBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesLoadSoundBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      dancingFlamesStartShowBtn = new JButton();
      dancingFlamesStartShowBtn.setBounds(19, 90, 125, 24);
      panel_49.add(dancingFlamesStartShowBtn);
      dancingFlamesStartShowBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().startShow();
        }
      });
      dancingFlamesStartShowBtn.setText("Start show");
      dancingFlamesStartShowBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesStartShowBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesStartShowBtn.setMaximumSize(new Dimension(32000, 24));
      dancingFlamesStartShowBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_49.add(getDancingFlamesStopShowBtn());
      panel_49.add(getDancingFlamesDoRecordCBx());

      dancingFlamesLoadProjectBtn = new JButton();
      dancingFlamesLoadProjectBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().dancingFlamesLoadProjectBtn_clicked();
        }
      });
      dancingFlamesLoadProjectBtn.setToolTipText("Load dancing flames project");
      dancingFlamesLoadProjectBtn.setText("Load project");
      dancingFlamesLoadProjectBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesLoadProjectBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesLoadProjectBtn.setMaximumSize(new Dimension(125, 24));
      dancingFlamesLoadProjectBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesLoadProjectBtn.setBounds(282, 20, 125, 24);
      panel_49.add(dancingFlamesLoadProjectBtn);

      dancingFlamesSaveProjectBtn = new JButton();
      dancingFlamesSaveProjectBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().dancingFlamesSaveProjectBtn_clicked();
        }
      });
      dancingFlamesSaveProjectBtn.setToolTipText("Save all flames, all motions and the sound as dancing flames project");
      dancingFlamesSaveProjectBtn.setText("Save project");
      dancingFlamesSaveProjectBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesSaveProjectBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesSaveProjectBtn.setMaximumSize(new Dimension(125, 24));
      dancingFlamesSaveProjectBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesSaveProjectBtn.setBounds(413, 20, 125, 24);
      panel_49.add(dancingFlamesSaveProjectBtn);
    }
    return panel_49;
  }

  public JComboBox getDancingFlamesFlameCmb() {
    return dancingFlamesFlameCmb;
  }

  private JCheckBox getDancingFlamesDrawTrianglesCBx() {
    if (dancingFlamesDrawTrianglesCBx == null) {
      dancingFlamesDrawTrianglesCBx = new JCheckBox("Draw triangles");
      dancingFlamesDrawTrianglesCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getDancingFractalsController().drawTrianglesCBx_changed();
          }
        }
      });
      dancingFlamesDrawTrianglesCBx.setSelected(true);
      dancingFlamesDrawTrianglesCBx.setMaximumSize(new Dimension(160, 18));
      dancingFlamesDrawTrianglesCBx.setPreferredSize(new Dimension(110, 18));
      dancingFlamesDrawTrianglesCBx.setMinimumSize(new Dimension(140, 18));
    }
    return dancingFlamesDrawTrianglesCBx;
  }

  private JCheckBox getDancingFlamesDrawFFTCBx() {
    if (dancingFlamesDrawFFTCBx == null) {
      dancingFlamesDrawFFTCBx = new JCheckBox("Show FFT data");
      dancingFlamesDrawFFTCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getDancingFractalsController().drawFFTCBx_changed();
          }
        }
      });
      dancingFlamesDrawFFTCBx.setSelected(true);
      dancingFlamesDrawFFTCBx.setPreferredSize(new Dimension(110, 18));
      dancingFlamesDrawFFTCBx.setMinimumSize(new Dimension(140, 18));
      dancingFlamesDrawFFTCBx.setMaximumSize(new Dimension(160, 18));
    }
    return dancingFlamesDrawFFTCBx;
  }

  private JCheckBox getDancingFlamesDrawFPSCBx() {
    if (dancingFlamesDrawFPSCBx == null) {
      dancingFlamesDrawFPSCBx = new JCheckBox("Show fps");
      dancingFlamesDrawFPSCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getDancingFractalsController().drawFPSCBx_changed();
          }
        }
      });
      dancingFlamesDrawFPSCBx.setSelected(true);
      dancingFlamesDrawFPSCBx.setPreferredSize(new Dimension(110, 18));
      dancingFlamesDrawFPSCBx.setMinimumSize(new Dimension(140, 18));
      dancingFlamesDrawFPSCBx.setMaximumSize(new Dimension(160, 18));
    }
    return dancingFlamesDrawFPSCBx;
  }

  public JTree getDancingFlamesFlamePropertiesTree() {
    return dancingFlamesFlamePropertiesTree;
  }

  private JPanel getPanel_42_1() {
    if (panel_42 == null) {
      panel_42 = new JPanel();
      panel_42.setLayout(new BorderLayout(0, 0));

      JScrollPane scrollPane_2 = new JScrollPane();
      panel_42.add(scrollPane_2, BorderLayout.CENTER);

      dancingFlamesFlamePropertiesTree = new JTree();
      dancingFlamesFlamePropertiesTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          if (tinaController != null) {
            tinaController.getDancingFractalsController().flamePropertiesTree_changed(e);
          }
        }
      });
      dancingFlamesFlamePropertiesTree.setRootVisible(false);
      scrollPane_2.setViewportView(dancingFlamesFlamePropertiesTree);
    }
    return panel_42;
  }

  private JPanel getPanel_40() {
    if (panel_40 == null) {
      panel_40 = new JPanel();
      panel_40.setPreferredSize(new Dimension(10, 62));
      panel_40.setLayout(null);

      dancingFlamesRandomGenCmb = new JComboBox();
      dancingFlamesRandomGenCmb.setBounds(100, 6, 207, 24);
      panel_40.add(dancingFlamesRandomGenCmb);
      dancingFlamesRandomGenCmb.setAlignmentX(Component.RIGHT_ALIGNMENT);
      dancingFlamesRandomGenCmb.setPreferredSize(new Dimension(125, 24));
      dancingFlamesRandomGenCmb.setMinimumSize(new Dimension(125, 24));
      dancingFlamesRandomGenCmb.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesRandomGenCmb.setMaximumRowCount(32);
      dancingFlamesRandomGenCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesRandomGenCmb.removeAllItems();
      for (String name : RandomFlameGeneratorList.getNameList()) {
        dancingFlamesRandomGenCmb.addItem(name);
      }
      dancingFlamesRandomGenCmb.setSelectedItem(RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);
      panel_40.add(getLblRandomGenerator());

      JLabel lblCount = new JLabel();
      lblCount.setBounds(0, 36, 100, 14);
      panel_40.add(lblCount);
      lblCount.setMinimumSize(new Dimension(100, 24));
      lblCount.setText("Count");
      lblCount.setPreferredSize(new Dimension(100, 24));
      lblCount.setFont(new Font("Dialog", Font.BOLD, 10));

      dancingFlamesRandomCountIEd = new JWFNumberField();
      dancingFlamesRandomCountIEd.setBounds(100, 31, 62, 24);
      panel_40.add(dancingFlamesRandomCountIEd);
      dancingFlamesRandomCountIEd.setMinimumSize(new Dimension(125, 24));
      dancingFlamesRandomCountIEd.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesRandomCountIEd.setValueStep(1.0);
      dancingFlamesRandomCountIEd.setText("12");
      dancingFlamesRandomCountIEd.setPreferredSize(new Dimension(125, 24));
      dancingFlamesRandomCountIEd.setOnlyIntegers(true);
      dancingFlamesRandomCountIEd.setMinValue(-255.0);
      dancingFlamesRandomCountIEd.setMaxValue(255.0);
      dancingFlamesRandomCountIEd.setHasMinValue(true);
      dancingFlamesRandomCountIEd.setHasMaxValue(true);
      dancingFlamesRandomCountIEd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dancingFlamesRandomCountIEd.setEditable(true);

      dancingFlamesGenRandFlamesBtn = new JButton();
      dancingFlamesGenRandFlamesBtn.setBounds(161, 31, 146, 24);
      panel_40.add(dancingFlamesGenRandFlamesBtn);
      dancingFlamesGenRandFlamesBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().genRandomFlames();
        }
      });
      dancingFlamesGenRandFlamesBtn.setText("Gen. random flames");
      dancingFlamesGenRandFlamesBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesGenRandFlamesBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesGenRandFlamesBtn.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesGenRandFlamesBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_40.add(getDancingFlamesAddFromEditorBtn());
      panel_40.add(getDancingFlamesAddFromClipboardBtn());
      panel_40.add(getDancingFlamesAddFromDiscBtn());
    }
    return panel_40;
  }

  private JPanel getPanel_44_1() {
    if (panel_44 == null) {
      panel_44 = new JPanel();
      panel_44.setBorder(new EmptyBorder(0, 0, 0, 0));
      panel_44.setPreferredSize(new Dimension(240, 10));
      panel_44.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new EmptyBorder(0, 0, 0, 0));
      panel_1.setPreferredSize(new Dimension(160, 160));
      panel_44.add(panel_1, BorderLayout.CENTER);
      panel_1.setLayout(new BorderLayout(0, 0));
      dancingFlamesDeleteFlameBtn = new JButton();
      dancingFlamesDeleteFlameBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesDeleteFlameBtn.setMaximumSize(new Dimension(160, 24));
      dancingFlamesDeleteFlameBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().deleteFlameBtn_clicked();
        }
      });
      dancingFlamesDeleteFlameBtn.setToolTipText("Delete the current flame");
      dancingFlamesDeleteFlameBtn.setText("Delete");
      dancingFlamesDeleteFlameBtn.setPreferredSize(new Dimension(115, 24));
      dancingFlamesDeleteFlameBtn.setMnemonic(KeyEvent.VK_D);
      dancingFlamesDeleteFlameBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      JPanel panel_2 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
      flowLayout.setHgap(2);
      flowLayout.setAlignment(FlowLayout.LEADING);
      flowLayout.setVgap(2);
      panel_1.add(panel_2, BorderLayout.SOUTH);
      panel_2.setPreferredSize(new Dimension(135, 58));
      dancingFlamesFlameToEditorBtn = new JButton();
      dancingFlamesFlameToEditorBtn.setMaximumSize(new Dimension(160, 24));
      dancingFlamesFlameToEditorBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesFlameToEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().flameToEditorBtn_clicked();
        }
      });
      dancingFlamesFlameToEditorBtn.setToolTipText("Copy current flame into Editor");
      dancingFlamesFlameToEditorBtn.setText("To Editor");
      dancingFlamesFlameToEditorBtn.setPreferredSize(new Dimension(115, 24));
      dancingFlamesFlameToEditorBtn.setMnemonic(KeyEvent.VK_E);
      dancingFlamesFlameToEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_2.add(getDancingFlamesFlameToEditorBtn());
      panel_2.add(getDancingFlamesReplaceFlameFromEditorBtn());
      panel_2.add(getDancingFlamesRenameFlameBtn());
      panel_2.add(getDancingFlamesDeleteFlameBtn());

      dancingFlamesPoolFlamePreviewPnl = new JPanel();
      panel_1.add(dancingFlamesPoolFlamePreviewPnl, BorderLayout.CENTER);
      dancingFlamesPoolFlamePreviewPnl.setMaximumSize(new Dimension(32767, 160));
      dancingFlamesPoolFlamePreviewPnl.setPreferredSize(new Dimension(240, 160));
      dancingFlamesPoolFlamePreviewPnl.setMinimumSize(new Dimension(160, 100));
      dancingFlamesPoolFlamePreviewPnl.setBorder(new EmptyBorder(0, 0, 0, 0));
      dancingFlamesPoolFlamePreviewPnl.setLayout(new BorderLayout(0, 0));
    }
    return panel_44;
  }

  public JCheckBox getDofNewDOFCBx() {
    return dofNewDOFCBx;
  }

  public JWFNumberField getDofDOFREd() {
    return dofDOFREd;
  }

  public JSlider getDofDOFSlider() {
    return dofDOFSlider;
  }

  public JWFNumberField getDofDOFAreaREd() {
    return dofDOFAreaREd;
  }

  public JSlider getDofDOFAreaSlider() {
    return dofDOFAreaSlider;
  }

  public JWFNumberField getDofDOFExponentREd() {
    return dofDOFExponentREd;
  }

  public JSlider getDofDOFExponentSlider() {
    return dofDOFExponentSlider;
  }

  public JWFNumberField getDofCamZREd() {
    return dofCamZREd;
  }

  public JSlider getDofCamZSlider() {
    return dofCamZSlider;
  }

  public JWFNumberField getDofFocusXREd() {
    return dofFocusXREd;
  }

  public JSlider getDofFocusXSlider() {
    return dofFocusXSlider;
  }

  public JWFNumberField getDofFocusYREd() {
    return dofFocusYREd;
  }

  public JSlider getDofFocusYSlider() {
    return dofFocusYSlider;
  }

  public JWFNumberField getDofFocusZREd() {
    return dofFocusZREd;
  }

  public JSlider getDofFocusZSlider() {
    return dofFocusZSlider;
  }

  private JPanel getPanel_46() {
    if (panel_46 == null) {
      panel_46 = new JPanel();
      panel_46.setPreferredSize(new Dimension(620, 10));
      panel_46.setLayout(new BorderLayout(0, 0));
      panel_46.add(getPanel_47(), BorderLayout.NORTH);
      panel_46.add(getPanel_48(), BorderLayout.CENTER);
    }
    return panel_46;
  }

  private JPanel getPanel_47() {
    if (panel_47 == null) {
      panel_47 = new JPanel();
      panel_47.setBorder(new TitledBorder(null, "Flame pool", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_47.setPreferredSize(new Dimension(10, 280));
      panel_47.setLayout(new BorderLayout(0, 0));
      panel_47.add(getPanel_40(), BorderLayout.NORTH);
      panel_47.add(getPanel_39_1(), BorderLayout.CENTER);
    }
    return panel_47;
  }

  private JPanel getPanel_48() {
    if (panel_48 == null) {
      panel_48 = new JPanel();
      panel_48.setBorder(new TitledBorder(null, "Motions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_48.setLayout(new BorderLayout(0, 0));
      panel_48.add(getPanel_38(), BorderLayout.SOUTH);
      panel_48.add(getPanel_45(), BorderLayout.CENTER);

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new TitledBorder(null, "Motion-Links", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_1.setPreferredSize(new Dimension(10, 162));
      panel_48.add(panel_1, BorderLayout.NORTH);
      panel_1.setLayout(new BorderLayout(0, 0));
      panel_1.add(getPanel_50(), BorderLayout.EAST);

      JPanel panel_2 = new JPanel();
      panel_1.add(panel_2, BorderLayout.CENTER);
      panel_2.setLayout(new BorderLayout(0, 0));

      JScrollPane scrollPane_2 = new JScrollPane();
      panel_2.add(scrollPane_2, BorderLayout.CENTER);

      dancingFlamesMotionLinksTable = new JTable();
      dancingFlamesMotionLinksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      scrollPane_2.setViewportView(dancingFlamesMotionLinksTable);
    }
    return panel_48;
  }

  private JPanel getPanel_39_1() {
    if (panel_39 == null) {
      panel_39 = new JPanel();
      panel_39.setLayout(new BorderLayout(0, 0));
      panel_39.add(getPanel_44_1(), BorderLayout.EAST);
      panel_39.add(getPanel_42_1(), BorderLayout.CENTER);
    }
    return panel_39;
  }

  private JPanel getPanel_38() {
    if (panel_38 == null) {
      panel_38 = new JPanel();
      panel_38.setPreferredSize(new Dimension(10, 62));
      panel_38.setLayout(null);

      dancingFlamesAddMotionCmb = new JComboBox();
      dancingFlamesAddMotionCmb.setPreferredSize(new Dimension(125, 24));
      dancingFlamesAddMotionCmb.setMinimumSize(new Dimension(125, 24));
      dancingFlamesAddMotionCmb.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesAddMotionCmb.setMaximumRowCount(32);
      dancingFlamesAddMotionCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesAddMotionCmb.setAlignmentX(1.0f);
      dancingFlamesAddMotionCmb.setBounds(132, 6, 180, 24);
      panel_38.add(dancingFlamesAddMotionCmb);

      dancingFlamesAddMotionBtn = new JButton();
      dancingFlamesAddMotionBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().addMotionBtn_clicked();
        }
      });
      dancingFlamesAddMotionBtn.setText("Add motion");
      dancingFlamesAddMotionBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesAddMotionBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesAddMotionBtn.setMaximumSize(new Dimension(32000, 24));
      dancingFlamesAddMotionBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesAddMotionBtn.setBounds(6, 6, 125, 24);
      panel_38.add(dancingFlamesAddMotionBtn);

      dancingFlamesDeleteMotionBtn = new JButton();
      dancingFlamesDeleteMotionBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().deleteMotionBtn_clicked();
        }
      });
      dancingFlamesDeleteMotionBtn.setText("Delete motion");
      dancingFlamesDeleteMotionBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesDeleteMotionBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesDeleteMotionBtn.setMaximumSize(new Dimension(32000, 24));
      dancingFlamesDeleteMotionBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesDeleteMotionBtn.setBounds(461, 6, 125, 24);
      panel_38.add(dancingFlamesDeleteMotionBtn);
      panel_38.add(getDancingFlamesCreateMotionsBtn());
      panel_38.add(getDancingFlamesCreateMotionsCmb());
      panel_38.add(getDancingFlamesClearMotionsBtn());

      dancingFlamesRenameMotionBtn = new JButton();
      dancingFlamesRenameMotionBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().renameMotionBtn_clicked();
        }
      });
      dancingFlamesRenameMotionBtn.setToolTipText("Rename the current motion");
      dancingFlamesRenameMotionBtn.setText("Rename");
      dancingFlamesRenameMotionBtn.setPreferredSize(new Dimension(115, 24));
      dancingFlamesRenameMotionBtn.setMnemonic(KeyEvent.VK_M);
      dancingFlamesRenameMotionBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesRenameMotionBtn.setMaximumSize(new Dimension(160, 24));
      dancingFlamesRenameMotionBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesRenameMotionBtn.setBounds(334, 6, 115, 24);
      panel_38.add(dancingFlamesRenameMotionBtn);
    }
    return panel_38;
  }

  private JPanel getPanel_45() {
    if (panel_45 == null) {
      panel_45 = new JPanel();
      panel_45.setLayout(new BorderLayout(0, 0));
      panel_45.add(getDancingFlamesMotionPropertyPnl(), BorderLayout.EAST);
      panel_45.add(getScrollPane_3(), BorderLayout.CENTER);
    }
    return panel_45;
  }

  private JPanel getDancingFlamesMotionPropertyPnl() {
    if (dancingFlamesMotionPropertyPnl == null) {
      dancingFlamesMotionPropertyPnl = new JPanel();
      dancingFlamesMotionPropertyPnl.setPreferredSize(new Dimension(280, 10));
      dancingFlamesMotionPropertyPnl.setLayout(new BorderLayout(0, 0));
    }
    return dancingFlamesMotionPropertyPnl;
  }

  private JScrollPane getScrollPane_3() {
    if (scrollPane_3 == null) {
      scrollPane_3 = new JScrollPane();
      scrollPane_3.setViewportView(getDancingFlamesMotionTable());
    }
    return scrollPane_3;
  }

  private JTable getDancingFlamesMotionTable() {
    if (dancingFlamesMotionTable == null) {
      dancingFlamesMotionTable = new JTable();
      dancingFlamesMotionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      dancingFlamesMotionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            tinaController.getDancingFractalsController().motionTableClicked();
          }
        }

      });

    }
    return dancingFlamesMotionTable;
  }

  public JComboBox getDancingFlamesAddMotionCmb() {
    return dancingFlamesAddMotionCmb;
  }

  public JButton getDancingFlamesAddMotionBtn() {
    return dancingFlamesAddMotionBtn;
  }

  public JButton getDancingFlamesDeleteMotionBtn() {
    return dancingFlamesDeleteMotionBtn;
  }

  public JButton getDancingFlamesLinkMotionBtn() {
    return dancingFlamesLinkMotionBtn;
  }

  public JButton getDancingFlamesUnlinkMotionBtn() {
    return dancingFlamesUnlinkMotionBtn;
  }

  private JButton getDancingFlamesCreateMotionsBtn() {
    if (dancingFlamesCreateMotionsBtn == null) {
      dancingFlamesCreateMotionsBtn = new JButton();
      dancingFlamesCreateMotionsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().createMotionsBtn_clicked();
        }
      });
      dancingFlamesCreateMotionsBtn.setToolTipText("Create predefined motions and linkings to all currently available flames");
      dancingFlamesCreateMotionsBtn.setText("Create motions");
      dancingFlamesCreateMotionsBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesCreateMotionsBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesCreateMotionsBtn.setMaximumSize(new Dimension(32000, 24));
      dancingFlamesCreateMotionsBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesCreateMotionsBtn.setBounds(5, 31, 125, 24);
    }
    return dancingFlamesCreateMotionsBtn;
  }

  private JComboBox getDancingFlamesCreateMotionsCmb() {
    if (dancingFlamesCreateMotionsCmb == null) {
      dancingFlamesCreateMotionsCmb = new JComboBox();
      dancingFlamesCreateMotionsCmb.setPreferredSize(new Dimension(125, 24));
      dancingFlamesCreateMotionsCmb.setMinimumSize(new Dimension(125, 24));
      dancingFlamesCreateMotionsCmb.setMaximumSize(new Dimension(30000, 24));
      dancingFlamesCreateMotionsCmb.setMaximumRowCount(32);
      dancingFlamesCreateMotionsCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesCreateMotionsCmb.setAlignmentX(1.0f);
      dancingFlamesCreateMotionsCmb.setBounds(131, 31, 180, 24);
    }
    return dancingFlamesCreateMotionsCmb;
  }

  private JButton getDancingFlamesClearMotionsBtn() {
    if (dancingFlamesClearMotionsBtn == null) {
      dancingFlamesClearMotionsBtn = new JButton();
      dancingFlamesClearMotionsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().clearMotionsBtn_clicked();
        }
      });
      dancingFlamesClearMotionsBtn.setText("Clear all motions");
      dancingFlamesClearMotionsBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesClearMotionsBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesClearMotionsBtn.setMaximumSize(new Dimension(32000, 24));
      dancingFlamesClearMotionsBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      dancingFlamesClearMotionsBtn.setBounds(461, 31, 125, 24);
    }
    return dancingFlamesClearMotionsBtn;
  }

  public JButton getDancingFlamesLoadProjectBtn() {
    return dancingFlamesLoadProjectBtn;
  }

  public JButton getDancingFlamesSaveProjectBtn() {
    return dancingFlamesSaveProjectBtn;
  }

  private JPanel getPanel_50() {
    if (panel_50 == null) {
      panel_50 = new JPanel();
      panel_50.setPreferredSize(new Dimension(125, 10));
      panel_50.setLayout(null);

      dancingFlamesLinkMotionBtn = new JButton();
      dancingFlamesLinkMotionBtn.setBounds(0, 0, 125, 24);
      panel_50.add(dancingFlamesLinkMotionBtn);
      dancingFlamesLinkMotionBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().linkMotionBtn_clicked();
        }
      });
      dancingFlamesLinkMotionBtn.setToolTipText("Link the currently selected motion to the currently selected flame property");
      dancingFlamesLinkMotionBtn.setText("Add link");
      dancingFlamesLinkMotionBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesLinkMotionBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesLinkMotionBtn.setMaximumSize(new Dimension(32000, 24));
      dancingFlamesLinkMotionBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      dancingFlamesUnlinkMotionBtn = new JButton();
      dancingFlamesUnlinkMotionBtn.setBounds(0, 23, 125, 24);
      panel_50.add(dancingFlamesUnlinkMotionBtn);
      dancingFlamesUnlinkMotionBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().unlinkMotionBtn_clicked();
        }
      });
      dancingFlamesUnlinkMotionBtn.setToolTipText("Unlink the currently selected motion from the currently selected flame property");
      dancingFlamesUnlinkMotionBtn.setText("Delete link");
      dancingFlamesUnlinkMotionBtn.setPreferredSize(new Dimension(125, 24));
      dancingFlamesUnlinkMotionBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesUnlinkMotionBtn.setMaximumSize(new Dimension(32000, 24));
      dancingFlamesUnlinkMotionBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return panel_50;
  }

  public JTable getDancingFlamesMotionLinksTable() {
    return dancingFlamesMotionLinksTable;
  }

  public JPanel getTinaCholorChooserPaletteImgPanel() {
    return tinaColorChooserPaletteImgPanel;
  }

  public JWFNumberField getTinaFilterRadiusREd() {
    return tinaFilterRadiusREd;
  }

  public JSlider getTinaFilterRadiusSlider() {
    return tinaFilterRadiusSlider;
  }

  public JComboBox getTinaFilterKernelCmb() {
    return tinaFilterKernelCmb;
  }

  public JWFNumberField getCamDimishZREd() {
    return camDimishZREd;
  }

  public JSlider getCamDimishZSlider() {
    return camDimishZSlider;
  }

  private JButton getTinaAddLinkedTransformationButton() {
    if (tinaAddLinkedTransformationButton == null) {
      tinaAddLinkedTransformationButton = new JButton();
      tinaAddLinkedTransformationButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.addLinkedXForm();
        }
      });
      tinaAddLinkedTransformationButton.setToolTipText("Add new linked transform");
      tinaAddLinkedTransformationButton.setText("L");
      tinaAddLinkedTransformationButton.setPreferredSize(new Dimension(34, 24));
      tinaAddLinkedTransformationButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return tinaAddLinkedTransformationButton;
  }

  public JWFNumberField getShadingDistanceColorRadiusREd() {
    return shadingDistanceColorRadiusREd;
  }

  public JSlider getShadingDistanceColorRadiusSlider() {
    return shadingDistanceColorRadiusSlider;
  }

  public JWFNumberField getShadingDistanceColorScaleREd() {
    return shadingDistanceColorScaleREd;
  }

  public JSlider getShadingDistanceColorScaleSlider() {
    return shadingDistanceColorScaleSlider;
  }

  public JWFNumberField getShadingDistanceColorExponentREd() {
    return shadingDistanceColorExponentREd;
  }

  public JSlider getShadingDistanceColorExponentSlider() {
    return shadingDistanceColorExponentSlider;
  }

  public JWFNumberField getShadingDistanceColorOffsetXREd() {
    return shadingDistanceColorOffsetXREd;
  }

  public JWFNumberField getShadingDistanceColorOffsetYREd() {
    return shadingDistanceColorOffsetYREd;
  }

  public JWFNumberField getShadingDistanceColorOffsetZREd() {
    return shadingDistanceColorOffsetZREd;
  }

  public JSlider getShadingDistanceColorOffsetXSlider() {
    return shadingDistanceColorOffsetXSlider;
  }

  public JSlider getShadingDistanceColorOffsetYSlider() {
    return shadingDistanceColorOffsetYSlider;
  }

  public JSlider getShadingDistanceColorOffsetZSlider() {
    return shadingDistanceColorOffsetZSlider;
  }

  public JWFNumberField getShadingDistanceColorStyleREd() {
    return shadingDistanceColorStyleREd;
  }

  public JSlider getShadingDistanceColorStyleSlider() {
    return shadingDistanceColorStyleSlider;
  }

  public JWFNumberField getShadingDistanceColorCoordinateREd() {
    return shadingDistanceColorCoordinateREd;
  }

  public JSlider getShadingDistanceColorCoordinateSlider() {
    return shadingDistanceColorCoordinateSlider;
  }

  public JWFNumberField getShadingDistanceColorShiftREd() {
    return shadingDistanceColorShiftREd;
  }

  public JSlider getShadingDistanceColorShiftSlider() {
    return shadingDistanceColorShiftSlider;
  }

  private JButton getQuickMutationButton() {
    if (quickMutationButton == null) {
      quickMutationButton = new JButton();
      quickMutationButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.quickMutateButton_clicked();
        }
      });
      quickMutationButton.setToolTipText("Create mutations of the current flame in the MutaGen");
      quickMutationButton.setText("Muta");
      quickMutationButton.setPreferredSize(new Dimension(72, 24));
      quickMutationButton.setMnemonic(KeyEvent.VK_H);
      quickMutationButton.setFont(new Font("Dialog", Font.BOLD, 9));
      quickMutationButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/kdissert.png")));
    }
    return quickMutationButton;
  }

  private JPanel getPanel_16() {
    if (panel_16 == null) {
      panel_16 = new JPanel();
      panel_16.setLayout(new BorderLayout(0, 0));
      panel_16.add(getPanel_51(), BorderLayout.EAST);
      panel_16.add(getPanel_53(), BorderLayout.CENTER);
      panel_16.add(getPanel_71(), BorderLayout.SOUTH);
      panel_16.add(getPanel_69(), BorderLayout.NORTH);
    }
    return panel_16;
  }

  private JPanel getPanel_51() {
    if (panel_51 == null) {
      panel_51 = new JPanel();
      panel_51.setPreferredSize(new Dimension(262, 10));
      panel_51.setLayout(new BoxLayout(panel_51, BoxLayout.Y_AXIS));

      JPanel panel_1 = new JPanel();
      panel_1.setMinimumSize(new Dimension(10, 64));
      panel_1.setPreferredSize(new Dimension(10, 64));
      panel_1.setMaximumSize(new Dimension(32767, 64));
      panel_1.setBorder(new TitledBorder(null, "Input flame", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_51.add(panel_1);

      mutaGenLoadFlameFromEditorBtn = new JButton();
      mutaGenLoadFlameFromEditorBtn.setMnemonic(KeyEvent.VK_D);
      mutaGenLoadFlameFromEditorBtn.setBounds(16, 23, 115, 24);
      mutaGenLoadFlameFromEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMutaGenController().loadFlameFromEditorBtn_clicked();
        }
      });
      panel_1.setLayout(null);
      panel_1.add(mutaGenLoadFlameFromEditorBtn);
      mutaGenLoadFlameFromEditorBtn.setToolTipText("Import the current flame from the Editor");
      mutaGenLoadFlameFromEditorBtn.setText("Add from Editor");
      mutaGenLoadFlameFromEditorBtn.setPreferredSize(new Dimension(115, 24));
      mutaGenLoadFlameFromEditorBtn.setMinimumSize(new Dimension(115, 24));
      mutaGenLoadFlameFromEditorBtn.setMaximumSize(new Dimension(115, 24));
      mutaGenLoadFlameFromEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      mutaGenLoadFlameFromFileBtn = new JButton();
      mutaGenLoadFlameFromFileBtn.setMnemonic(KeyEvent.VK_L);
      mutaGenLoadFlameFromFileBtn.setBounds(135, 23, 115, 24);
      mutaGenLoadFlameFromFileBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMutaGenController().loadFlameFromFileBtn_clicked();
        }
      });
      panel_1.add(mutaGenLoadFlameFromFileBtn);
      mutaGenLoadFlameFromFileBtn.setToolTipText("Load a flame from file");
      mutaGenLoadFlameFromFileBtn.setText("Load flame");
      mutaGenLoadFlameFromFileBtn.setPreferredSize(new Dimension(125, 24));
      mutaGenLoadFlameFromFileBtn.setMinimumSize(new Dimension(125, 24));
      mutaGenLoadFlameFromFileBtn.setMaximumSize(new Dimension(30000, 24));
      mutaGenLoadFlameFromFileBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      JPanel panel_2 = new JPanel();
      panel_2.setBorder(new TitledBorder(null, "Hints", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_51.add(panel_2);
      panel_2.setLayout(new BorderLayout(0, 0));
      panel_2.add(getScrollPane_4(), BorderLayout.CENTER);

      JPanel panel_10 = new JPanel();
      panel_10.setMinimumSize(new Dimension(10, 64));
      panel_10.setMaximumSize(new Dimension(32767, 64));
      panel_10.setBorder(new TitledBorder(null, "Export selected flame", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_10.setPreferredSize(new Dimension(10, 64));
      panel_51.add(panel_10);
      panel_10.setLayout(null);

      mutaGenSaveFlameToEditorBtn = new JButton();
      mutaGenSaveFlameToEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMutaGenController().exportFlameBtn_clicked();
        }
      });
      mutaGenSaveFlameToEditorBtn.setToolTipText("Copy the current fractal into the Editor");
      mutaGenSaveFlameToEditorBtn.setText("To Editor");
      mutaGenSaveFlameToEditorBtn.setPreferredSize(new Dimension(125, 24));
      mutaGenSaveFlameToEditorBtn.setMnemonic(KeyEvent.VK_E);
      mutaGenSaveFlameToEditorBtn.setMinimumSize(new Dimension(100, 24));
      mutaGenSaveFlameToEditorBtn.setMaximumSize(new Dimension(32000, 24));
      mutaGenSaveFlameToEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      mutaGenSaveFlameToEditorBtn.setBounds(16, 23, 115, 24);
      panel_10.add(mutaGenSaveFlameToEditorBtn);

      mutaGenSaveFlameToFileBtn = new JButton();
      mutaGenSaveFlameToFileBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMutaGenController().saveFlameBtn_clicked();
        }
      });
      mutaGenSaveFlameToFileBtn.setToolTipText("Save the current fractal");
      mutaGenSaveFlameToFileBtn.setText("Save Flame");
      mutaGenSaveFlameToFileBtn.setPreferredSize(new Dimension(125, 24));
      mutaGenSaveFlameToFileBtn.setMnemonic(KeyEvent.VK_V);
      mutaGenSaveFlameToFileBtn.setMinimumSize(new Dimension(100, 24));
      mutaGenSaveFlameToFileBtn.setMaximumSize(new Dimension(32000, 24));
      mutaGenSaveFlameToFileBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      mutaGenSaveFlameToFileBtn.setBounds(135, 23, 115, 24);
      panel_10.add(mutaGenSaveFlameToFileBtn);

      JPanel panel_3 = new JPanel();
      panel_3.setMinimumSize(new Dimension(10, 64));
      panel_3.setMaximumSize(new Dimension(32767, 64));
      panel_3.setPreferredSize(new Dimension(10, 64));
      panel_3.setBorder(new TitledBorder(null, "Navigate through generations", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_51.add(panel_3);

      mutaGenBackBtn = new JButton();
      mutaGenBackBtn.setBounds(16, 23, 115, 24);
      mutaGenBackBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMutaGenController().backBtn_clicked();
        }
      });
      panel_3.setLayout(null);
      panel_3.add(mutaGenBackBtn);
      mutaGenBackBtn.setToolTipText("Move one generation back");
      mutaGenBackBtn.setText("Back");
      mutaGenBackBtn.setPreferredSize(new Dimension(115, 24));
      mutaGenBackBtn.setMnemonic(KeyEvent.VK_B);
      mutaGenBackBtn.setMinimumSize(new Dimension(115, 24));
      mutaGenBackBtn.setMaximumSize(new Dimension(160, 24));
      mutaGenBackBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_3.add(getMutaGenForwardBtn());

      JPanel panel_8 = new JPanel();
      panel_8.setBorder(new TitledBorder(null, "Mutation options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_51.add(panel_8);
      panel_8.setLayout(new BoxLayout(panel_8, BoxLayout.Y_AXIS));

      JPanel panel_9 = new JPanel();
      panel_9.setPreferredSize(new Dimension(10, 28));
      panel_9.setMinimumSize(new Dimension(10, 28));
      panel_9.setMaximumSize(new Dimension(32767, 28));
      panel_8.add(panel_9);

      JLabel lblTrendVertical = new JLabel();
      panel_9.add(lblTrendVertical);
      lblTrendVertical.setText("Horiz 1");
      lblTrendVertical.setPreferredSize(new Dimension(40, 22));
      lblTrendVertical.setHorizontalAlignment(SwingConstants.RIGHT);
      lblTrendVertical.setFont(new Font("Dialog", Font.BOLD, 10));

      mutaGenHorizontalTrend1Cmb = new JComboBox();
      panel_9.add(mutaGenHorizontalTrend1Cmb);
      mutaGenHorizontalTrend1Cmb.setPreferredSize(new Dimension(125, 24));
      mutaGenHorizontalTrend1Cmb.setMinimumSize(new Dimension(125, 24));
      mutaGenHorizontalTrend1Cmb.setMaximumSize(new Dimension(30000, 24));
      mutaGenHorizontalTrend1Cmb.setMaximumRowCount(32);
      mutaGenHorizontalTrend1Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      mutaGenHorizontalTrend1Cmb.setAlignmentX(1.0f);
      panel_8.add(getPanel_55());
      panel_8.add(getPanel_56());
      panel_8.add(getPanel_57());
      panel_8.add(getPanel_58());

    }
    return panel_51;
  }

  private JPanel getPanel_53() {
    if (panel_53 == null) {
      panel_53 = new JPanel();
      panel_53.setLayout(new GridLayout(5, 5, 10, 10));
      panel_53.add(getMutaGen01Pnl());
      panel_53.add(getMutaGen02Pnl());
      panel_53.add(getMutaGen03Pnl());
      panel_53.add(getMutaGen04Pnl());
      panel_53.add(getMutaGen05Pnl());
      panel_53.add(getMutaGen06Pnl());
      panel_53.add(getMutaGen07Pnl());
      panel_53.add(getMutaGen08Pnl());
      panel_53.add(getMutaGen09Pnl());
      panel_53.add(getMutaGen10Pnl());
      panel_53.add(getMutaGen11Pnl());
      panel_53.add(getMutaGen12Pnl());
      panel_53.add(getMutaGen13Pnl());
      panel_53.add(getMutaGen14Pnl());
      panel_53.add(getMutaGen15Pnl());
      panel_53.add(getMutaGen16Pnl());
      panel_53.add(getMutaGen17Pnl());
      panel_53.add(getMutaGen18Pnl());
      panel_53.add(getMutaGen19Pnl());
      panel_53.add(getMutaGen20Pnl());
      panel_53.add(getMutaGen21Pnl());
      panel_53.add(getMutaGen22Pnl());
      panel_53.add(getMutaGen23Pnl());
      panel_53.add(getMutaGen24Pnl());
      panel_53.add(getMutaGen25Pnl());
    }
    return panel_53;
  }

  private JPanel getMutaGen05Pnl() {
    if (mutaGen05Pnl == null) {
      mutaGen05Pnl = new JPanel();
      mutaGen05Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen05Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen05Pnl;
  }

  private JPanel getMutaGen01Pnl() {
    if (mutaGen01Pnl == null) {
      mutaGen01Pnl = new JPanel();
      mutaGen01Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen01Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen01Pnl;
  }

  private JPanel getMutaGen03Pnl() {
    if (mutaGen03Pnl == null) {
      mutaGen03Pnl = new JPanel();
      mutaGen03Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen03Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen03Pnl;
  }

  private JPanel getMutaGen04Pnl() {
    if (mutaGen04Pnl == null) {
      mutaGen04Pnl = new JPanel();
      mutaGen04Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen04Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen04Pnl;
  }

  private JPanel getMutaGen06Pnl() {
    if (mutaGen06Pnl == null) {
      mutaGen06Pnl = new JPanel();
      mutaGen06Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen06Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen06Pnl;
  }

  private JPanel getMutaGen07Pnl() {
    if (mutaGen07Pnl == null) {
      mutaGen07Pnl = new JPanel();
      mutaGen07Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen07Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen07Pnl;
  }

  private JPanel getMutaGen08Pnl() {
    if (mutaGen08Pnl == null) {
      mutaGen08Pnl = new JPanel();
      mutaGen08Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen08Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen08Pnl;
  }

  private JPanel getMutaGen09Pnl() {
    if (mutaGen09Pnl == null) {
      mutaGen09Pnl = new JPanel();
      mutaGen09Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen09Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen09Pnl;
  }

  private JPanel getMutaGen10Pnl() {
    if (mutaGen10Pnl == null) {
      mutaGen10Pnl = new JPanel();
      mutaGen10Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen10Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen10Pnl;
  }

  private JPanel getMutaGen12Pnl() {
    if (mutaGen12Pnl == null) {
      mutaGen12Pnl = new JPanel();
      mutaGen12Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen12Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen12Pnl;
  }

  private JPanel getMutaGen13Pnl() {
    if (mutaGen13Pnl == null) {
      mutaGen13Pnl = new JPanel();
      mutaGen13Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen13Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen13Pnl;
  }

  private JPanel getMutaGen14Pnl() {
    if (mutaGen14Pnl == null) {
      mutaGen14Pnl = new JPanel();
      mutaGen14Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen14Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen14Pnl;
  }

  private JPanel getMutaGen15Pnl() {
    if (mutaGen15Pnl == null) {
      mutaGen15Pnl = new JPanel();
      mutaGen15Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen15Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen15Pnl;
  }

  private JPanel getMutaGen16Pnl() {
    if (mutaGen16Pnl == null) {
      mutaGen16Pnl = new JPanel();
      mutaGen16Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen16Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen16Pnl;
  }

  private JPanel getPanel_69() {
    if (panel_69 == null) {
      panel_69 = new JPanel();
      panel_69.setPreferredSize(new Dimension(10, 4));
    }
    return panel_69;
  }

  private JPanel getMutaGen17Pnl() {
    if (mutaGen17Pnl == null) {
      mutaGen17Pnl = new JPanel();
      mutaGen17Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen17Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen17Pnl;
  }

  private JPanel getPanel_71() {
    if (panel_71 == null) {
      panel_71 = new JPanel();
      panel_71.setPreferredSize(new Dimension(10, 24));
      panel_71.setMinimumSize(new Dimension(8, 100));
      panel_71.setLayout(new BorderLayout(0, 0));

      mutaGenProgressBar = new JProgressBar();
      panel_71.add(mutaGenProgressBar);
    }
    return panel_71;
  }

  private JPanel getMutaGen18Pnl() {
    if (mutaGen18Pnl == null) {
      mutaGen18Pnl = new JPanel();
      mutaGen18Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen18Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen18Pnl;
  }

  private JPanel getMutaGen19Pnl() {
    if (mutaGen19Pnl == null) {
      mutaGen19Pnl = new JPanel();
      mutaGen19Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen19Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen19Pnl;
  }

  private JPanel getMutaGen20Pnl() {
    if (mutaGen20Pnl == null) {
      mutaGen20Pnl = new JPanel();
      mutaGen20Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen20Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen20Pnl;
  }

  private JPanel getMutaGen02Pnl() {
    if (mutaGen02Pnl == null) {
      mutaGen02Pnl = new JPanel();
      mutaGen02Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen02Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen02Pnl;
  }

  private JPanel getMutaGen21Pnl() {
    if (mutaGen21Pnl == null) {
      mutaGen21Pnl = new JPanel();
      mutaGen21Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen21Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen21Pnl;
  }

  private JPanel getMutaGen22Pnl() {
    if (mutaGen22Pnl == null) {
      mutaGen22Pnl = new JPanel();
      mutaGen22Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen22Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen22Pnl;
  }

  private JPanel getMutaGen23Pnl() {
    if (mutaGen23Pnl == null) {
      mutaGen23Pnl = new JPanel();
      mutaGen23Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen23Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen23Pnl;
  }

  private JPanel getMutaGen25Pnl() {
    if (mutaGen25Pnl == null) {
      mutaGen25Pnl = new JPanel();
      mutaGen25Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen25Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen25Pnl;
  }

  private JPanel getMutaGen24Pnl() {
    if (mutaGen24Pnl == null) {
      mutaGen24Pnl = new JPanel();
      mutaGen24Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen24Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen24Pnl;
  }

  private JPanel getMutaGen11Pnl() {
    if (mutaGen11Pnl == null) {
      mutaGen11Pnl = new JPanel();
      mutaGen11Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen11Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen11Pnl;
  }

  public JButton getMutaGenBackBtn() {
    return mutaGenBackBtn;
  }

  public JButton getMutaGenLoadFlameFromEditorBtn() {
    return mutaGenLoadFlameFromEditorBtn;
  }

  public JProgressBar getMutaGenProgressBar() {
    return mutaGenProgressBar;
  }

  public JComboBox getMutaGenHorizontalTrend1Cmb() {
    return mutaGenHorizontalTrend1Cmb;
  }

  public JComboBox getMutaGenVerticalTrend1Cmb() {
    return mutaGenVerticalTrend1Cmb;
  }

  public JButton getMutaGenLoadFlameFromFileBtn() {
    return mutaGenLoadFlameFromFileBtn;
  }

  private JPanel getPanel_55() {
    if (panel_55 == null) {
      panel_55 = new JPanel();
      panel_55.setPreferredSize(new Dimension(10, 28));
      panel_55.setMinimumSize(new Dimension(10, 28));
      panel_55.setMaximumSize(new Dimension(32767, 28));
      panel_55.add(getLblH());

      mutaGenHorizontalTrend2Cmb = new JComboBox();
      panel_55.add(mutaGenHorizontalTrend2Cmb);
      mutaGenHorizontalTrend2Cmb.setPreferredSize(new Dimension(125, 24));
      mutaGenHorizontalTrend2Cmb.setMinimumSize(new Dimension(125, 24));
      mutaGenHorizontalTrend2Cmb.setMaximumSize(new Dimension(30000, 24));
      mutaGenHorizontalTrend2Cmb.setMaximumRowCount(32);
      mutaGenHorizontalTrend2Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      mutaGenHorizontalTrend2Cmb.setAlignmentX(1.0f);
    }
    return panel_55;
  }

  private JLabel getLblH() {
    if (lblH == null) {
      lblH = new JLabel();
      lblH.setText("Horiz 2");
      lblH.setPreferredSize(new Dimension(40, 22));
      lblH.setHorizontalAlignment(SwingConstants.RIGHT);
      lblH.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblH;
  }

  public JComboBox getMutaGenHorizontalTrend2Cmb() {
    return mutaGenHorizontalTrend2Cmb;
  }

  private JPanel getPanel_56() {
    if (panel_56 == null) {
      panel_56 = new JPanel();
      panel_56.setPreferredSize(new Dimension(10, 28));
      panel_56.setMinimumSize(new Dimension(10, 28));
      panel_56.setMaximumSize(new Dimension(32767, 28));
      panel_56.add(getLblV());

      mutaGenVerticalTrend1Cmb = new JComboBox();
      panel_56.add(mutaGenVerticalTrend1Cmb);
      mutaGenVerticalTrend1Cmb.setPreferredSize(new Dimension(125, 24));
      mutaGenVerticalTrend1Cmb.setMinimumSize(new Dimension(125, 24));
      mutaGenVerticalTrend1Cmb.setMaximumSize(new Dimension(30000, 24));
      mutaGenVerticalTrend1Cmb.setMaximumRowCount(32);
      mutaGenVerticalTrend1Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      mutaGenVerticalTrend1Cmb.setAlignmentX(1.0f);
    }
    return panel_56;
  }

  private JLabel getLblV() {
    if (lblV == null) {
      lblV = new JLabel();
      lblV.setText("Vert 1");
      lblV.setPreferredSize(new Dimension(40, 22));
      lblV.setHorizontalAlignment(SwingConstants.RIGHT);
      lblV.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblV;
  }

  private JPanel getPanel_57() {
    if (panel_57 == null) {
      panel_57 = new JPanel();
      panel_57.setPreferredSize(new Dimension(10, 28));
      panel_57.setMinimumSize(new Dimension(10, 28));
      panel_57.setMaximumSize(new Dimension(32767, 28));
      panel_57.add(getLblVertical());
      panel_57.add(getMutaGenVerticalTrend2Cmb());
    }
    return panel_57;
  }

  private JLabel getLblVertical() {
    if (lblVertical == null) {
      lblVertical = new JLabel();
      lblVertical.setText("Vert 2");
      lblVertical.setPreferredSize(new Dimension(40, 22));
      lblVertical.setHorizontalAlignment(SwingConstants.RIGHT);
      lblVertical.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblVertical;
  }

  private JComboBox getMutaGenVerticalTrend2Cmb() {
    if (mutaGenVerticalTrend2Cmb == null) {
      mutaGenVerticalTrend2Cmb = new JComboBox();
      mutaGenVerticalTrend2Cmb.setPreferredSize(new Dimension(125, 24));
      mutaGenVerticalTrend2Cmb.setMinimumSize(new Dimension(125, 24));
      mutaGenVerticalTrend2Cmb.setMaximumSize(new Dimension(30000, 24));
      mutaGenVerticalTrend2Cmb.setMaximumRowCount(32);
      mutaGenVerticalTrend2Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      mutaGenVerticalTrend2Cmb.setAlignmentX(1.0f);
    }
    return mutaGenVerticalTrend2Cmb;
  }

  private JPanel getPanel_58() {
    if (panel_58 == null) {
      panel_58 = new JPanel();
      panel_58.setPreferredSize(new Dimension(10, 28));
      panel_58.setMinimumSize(new Dimension(10, 28));
      panel_58.setMaximumSize(new Dimension(32767, 28));

      JLabel lblAmount = new JLabel();
      panel_58.add(lblAmount);
      lblAmount.setText("Strength");
      lblAmount.setPreferredSize(new Dimension(48, 22));
      lblAmount.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_58.add(getMutaGenAmountREd());
      panel_58.add(getMutaGenRefreshBtn());
    }
    return panel_58;
  }

  private JButton getMutaGenForwardBtn() {
    if (mutaGenForwardBtn == null) {
      mutaGenForwardBtn = new JButton();
      mutaGenForwardBtn.setBounds(135, 23, 115, 24);
      mutaGenForwardBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMutaGenController().forwardBtn_clicked();
        }
      });
      mutaGenForwardBtn.setToolTipText("Move one generation forward");
      mutaGenForwardBtn.setText("Forward");
      mutaGenForwardBtn.setPreferredSize(new Dimension(115, 24));
      mutaGenForwardBtn.setMnemonic(KeyEvent.VK_F);
      mutaGenForwardBtn.setMinimumSize(new Dimension(100, 24));
      mutaGenForwardBtn.setMaximumSize(new Dimension(160, 24));
      mutaGenForwardBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return mutaGenForwardBtn;
  }

  private JTextPane getMutaGenHintPane() {
    if (mutaGenHintPane == null) {
      mutaGenHintPane = new JTextPane();
      mutaGenHintPane.setBackground(SystemColor.menu);
      mutaGenHintPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
      mutaGenHintPane.setEditable(false);
    }
    return mutaGenHintPane;
  }

  private JScrollPane getScrollPane_4() {
    if (scrollPane_4 == null) {
      scrollPane_4 = new JScrollPane();
      scrollPane_4.setViewportView(getMutaGenHintPane());
    }
    return scrollPane_4;
  }

  private JWFNumberField getMutaGenAmountREd() {
    if (mutaGenAmountREd == null) {
      mutaGenAmountREd = new JWFNumberField();
      mutaGenAmountREd.setValueStep(0.001);
      mutaGenAmountREd.setText("");
      mutaGenAmountREd.setSize(new Dimension(100, 24));
      mutaGenAmountREd.setPreferredSize(new Dimension(64, 24));
      mutaGenAmountREd.setMaxValue(1.0);
      mutaGenAmountREd.setLocation(new Point(100, 100));
      mutaGenAmountREd.setHasMinValue(true);
      mutaGenAmountREd.setHasMaxValue(true);
      mutaGenAmountREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return mutaGenAmountREd;
  }

  private JButton getMutaGenRefreshBtn() {
    if (mutaGenRefreshBtn == null) {
      mutaGenRefreshBtn = new JButton();
      mutaGenRefreshBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMutaGenController().drawSelectedSet();
        }
      });
      mutaGenRefreshBtn.setToolTipText("Recalculate the current generation using the current chosen strength value");
      mutaGenRefreshBtn.setText("Refresh");
      mutaGenRefreshBtn.setPreferredSize(new Dimension(100, 24));
      mutaGenRefreshBtn.setMnemonic(KeyEvent.VK_R);
      mutaGenRefreshBtn.setMinimumSize(new Dimension(100, 24));
      mutaGenRefreshBtn.setMaximumSize(new Dimension(160, 24));
      mutaGenRefreshBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return mutaGenRefreshBtn;
  }

  public JButton getMutaGenSaveFlameToEditorBtn() {
    return mutaGenSaveFlameToEditorBtn;
  }

  public JButton getMutaGenSaveFlameToFileBtn() {
    return mutaGenSaveFlameToFileBtn;
  }

  private JButton getEditFlameTitleBtn() {
    if (editFlameTitleBtn == null) {
      editFlameTitleBtn = new JButton();
      editFlameTitleBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.editFlameTitleBtn_clicked();
        }
      });
      editFlameTitleBtn.setToolTipText("Change the flame title");
      editFlameTitleBtn.setText("Title");
      editFlameTitleBtn.setPreferredSize(new Dimension(72, 24));
      editFlameTitleBtn.setMnemonic(KeyEvent.VK_T);
      editFlameTitleBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return editFlameTitleBtn;
  }

  private JLabel getLabel_8() {
    if (label_8 == null) {
      label_8 = new JLabel();
      label_8.setText("");
      label_8.setPreferredSize(new Dimension(42, 4));
      label_8.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_8;
  }

  public JButton getEditTransformCaptionBtn() {
    return editTransformCaptionBtn;
  }

  public JToggleButton getMouseTransformRotateTrianglesButton() {
    return mouseTransformRotateTrianglesButton;
  }

  public JToggleButton getMouseTransformScaleTrianglesButton() {
    return mouseTransformScaleTrianglesButton;
  }

  public JTree getScriptTree() {
    return scriptTree;
  }

  private JButton getNewScriptBtn() {
    if (newScriptBtn == null) {
      newScriptBtn = new JButton();
      newScriptBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().newScriptBtn_clicked();
        }
      });
      newScriptBtn.setToolTipText("Create a new script");
      newScriptBtn.setText("New script");
      newScriptBtn.setPreferredSize(new Dimension(96, 24));
      newScriptBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      newScriptBtn.setBounds(new Rectangle(9, 280, 125, 24));
    }
    return newScriptBtn;
  }

  private JPanel getPanel_59() {
    if (panel_59 == null) {
      panel_59 = new JPanel();
      panel_59.setPreferredSize(new Dimension(10, 28));

      scriptSaveBtn = new JButton();
      scriptSaveBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().saveScriptButton_clicked();
        }
      });
      scriptSaveBtn.setToolTipText("Save the current modifications");
      scriptSaveBtn.setText("Save");
      scriptSaveBtn.setPreferredSize(new Dimension(96, 24));
      scriptSaveBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_59.add(scriptSaveBtn);
      panel_59.add(getCompileScriptButton());

      scriptRevertBtn = new JButton();
      scriptRevertBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().revertScriptButton_clicked();
        }
      });
      scriptRevertBtn.setToolTipText("Revert all changes since the last save");
      scriptRevertBtn.setText("Revert");
      scriptRevertBtn.setPreferredSize(new Dimension(96, 24));
      scriptRevertBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_59.add(scriptRevertBtn);
    }
    return panel_59;
  }

  private JTabbedPane getTabbedPane() {
    if (tabbedPane == null) {
      tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane.addTab("Description", null, getPanel_60(), null);
      tabbedPane.addTab("Code", null, getPanel_61(), null);
    }
    return tabbedPane;
  }

  private JPanel getPanel_60() {
    if (panel_60 == null) {
      panel_60 = new JPanel();
      panel_60.setLayout(new BorderLayout(0, 0));
      panel_60.add(getScrollPane_5(), BorderLayout.CENTER);
    }
    return panel_60;
  }

  private JPanel getPanel_61() {
    if (panel_61 == null) {
      panel_61 = new JPanel();
      panel_61.setLayout(new BorderLayout(0, 0));
      panel_61.add(getScriptScrollPane(), BorderLayout.CENTER);
    }
    return panel_61;
  }

  private JScrollPane getScrollPane_5() {
    if (scrollPane_5 == null) {
      scrollPane_5 = new JScrollPane();
      scrollPane_5.setViewportView(getScriptDescriptionTextArea());
    }
    return scrollPane_5;
  }

  private JTextArea getScriptDescriptionTextArea() {
    if (scriptDescriptionTextArea == null) {
      scriptDescriptionTextArea = new JTextArea();
      scriptDescriptionTextArea.setLineWrap(true);
      scriptDescriptionTextArea.setWrapStyleWord(true);
      scriptDescriptionTextArea.setFont(new Font("SansSerif", Font.PLAIN, 10));
      scriptDescriptionTextArea.setText("");
    }
    return scriptDescriptionTextArea;
  }

  public JButton getRescanScriptsBtn() {
    return rescanScriptsBtn;
  }

  public JButton getDeleteScriptBtn() {
    return deleteScriptBtn;
  }

  public JButton getScriptRenameBtn() {
    return scriptRenameBtn;
  }

  public JButton getScriptRunBtn() {
    return scriptRunBtn;
  }

  private JButton getDuplicateScriptBtn() {
    if (duplicateScriptBtn == null) {
      duplicateScriptBtn = new JButton();
      duplicateScriptBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().duplicateScriptBtn_clicked();
        }
      });
      duplicateScriptBtn.setToolTipText("Create a copy of the currently selected script");
      duplicateScriptBtn.setText("Duplicate");
      duplicateScriptBtn.setPreferredSize(new Dimension(96, 24));
      duplicateScriptBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      duplicateScriptBtn.setBounds(new Rectangle(9, 280, 125, 24));
    }
    return duplicateScriptBtn;
  }

  public JButton getScriptSaveBtn() {
    return scriptSaveBtn;
  }

  public JButton getScriptRevertBtn() {
    return scriptRevertBtn;
  }

  private JButton getNewScriptFromFlameBtn() {
    if (newScriptFromFlameBtn == null) {
      newScriptFromFlameBtn = new JButton();
      newScriptFromFlameBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().newScriptFromFlameBtn_clicked();
        }
      });
      newScriptFromFlameBtn.setToolTipText("Create a new script by convderting the currently selected flame");
      newScriptFromFlameBtn.setText("From flame");
      newScriptFromFlameBtn.setPreferredSize(new Dimension(96, 24));
      newScriptFromFlameBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      newScriptFromFlameBtn.setBounds(new Rectangle(9, 280, 125, 24));
    }
    return newScriptFromFlameBtn;
  }

  public JToggleButton getMouseTransformEditGradientButton() {
    return mouseTransformEditGradientButton;
  }

  private JPanel getGradientEditorFncPnl() {
    if (gradientEditorFncPnl == null) {
      gradientEditorFncPnl = new JPanel();
      FlowLayout flowLayout = (FlowLayout) gradientEditorFncPnl.getLayout();
      flowLayout.setHgap(2);
      flowLayout.setVgap(1);
      gradientEditorFncPnl.setPreferredSize(new Dimension(80, 50));
      gradientEditorFncPnl.add(getPanel_62());
      gradientEditorFncPnl.add(getPanel_63());
      gradientEditorFncPnl.add(getPanel_64());
      gradientEditorFncPnl.add(getPanel_65());
      gradientEditorFncPnl.add(getPanel_67());
      gradientEditorFncPnl.add(getPanel_70());
    }
    return gradientEditorFncPnl;
  }

  private JButton getGradientInvertBtn() {
    if (gradientInvertBtn == null) {
      gradientInvertBtn = new JButton();
      gradientInvertBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientInvertBtn_clicked();
        }
      });
      gradientInvertBtn.setToolTipText("Turn into negative colors");
      gradientInvertBtn.setText("Invert");
      gradientInvertBtn.setSize(new Dimension(138, 24));
      gradientInvertBtn.setPreferredSize(new Dimension(70, 24));
      gradientInvertBtn.setLocation(new Point(4, 181));
      gradientInvertBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientInvertBtn;
  }

  private JButton getGradientReverseBtn() {
    if (gradientReverseBtn == null) {
      gradientReverseBtn = new JButton();
      gradientReverseBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientReverseBtn_clicked();
        }
      });
      gradientReverseBtn.setToolTipText("Reverse color order at the selected range");
      gradientReverseBtn.setText("Reverse");
      gradientReverseBtn.setSize(new Dimension(60, 24));
      gradientReverseBtn.setPreferredSize(new Dimension(70, 24));
      gradientReverseBtn.setLocation(new Point(4, 181));
      gradientReverseBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientReverseBtn;
  }

  private JButton getGradientSortBtn() {
    if (gradientSortBtn == null) {
      gradientSortBtn = new JButton();
      gradientSortBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientSortBtn_clicked();
        }
      });
      gradientSortBtn.setToolTipText("Sort the colors (by hue and brightness)");
      gradientSortBtn.setText("Sort");
      gradientSortBtn.setSize(new Dimension(138, 24));
      gradientSortBtn.setPreferredSize(new Dimension(70, 24));
      gradientSortBtn.setLocation(new Point(4, 181));
      gradientSortBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientSortBtn;
  }

  private JButton getGradientFadeBtn() {
    if (gradientFadeBtn == null) {
      gradientFadeBtn = new JButton();
      gradientFadeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientFadeBtn_clicked();
        }
      });
      gradientFadeBtn.setToolTipText("Fade the color to the left into the color to the right");
      gradientFadeBtn.setText("Fade");
      gradientFadeBtn.setSize(new Dimension(138, 24));
      gradientFadeBtn.setPreferredSize(new Dimension(70, 24));
      gradientFadeBtn.setLocation(new Point(4, 181));
      gradientFadeBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientFadeBtn;
  }

  private JButton getGradientSelectAllBtn() {
    if (gradientSelectAllBtn == null) {
      gradientSelectAllBtn = new JButton();
      gradientSelectAllBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientSelectAllBtn_clicked();
        }
      });
      gradientSelectAllBtn.setToolTipText("Select the complete gradient");
      gradientSelectAllBtn.setText("Sel all");
      gradientSelectAllBtn.setSize(new Dimension(138, 24));
      gradientSelectAllBtn.setPreferredSize(new Dimension(70, 24));
      gradientSelectAllBtn.setLocation(new Point(4, 181));
      gradientSelectAllBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientSelectAllBtn;
  }

  private JButton getGradientApplyBalancingBtn() {
    if (gradientApplyBalancingBtn == null) {
      gradientApplyBalancingBtn = new JButton();
      gradientApplyBalancingBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientApplyBalancingBtn_clicked();
        }
      });
      gradientApplyBalancingBtn.setToolTipText("Apply all changes from the Balance-tab permanently to this gradient and reset all of those options");
      gradientApplyBalancingBtn.setText("Apply all current balancing-options");
      gradientApplyBalancingBtn.setSize(new Dimension(316, 24));
      gradientApplyBalancingBtn.setPreferredSize(new Dimension(190, 24));
      gradientApplyBalancingBtn.setLocation(new Point(6, 112));
      gradientApplyBalancingBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientApplyBalancingBtn;
  }

  public JButton getGradientApplyTXBtn() {
    return gradientApplyTXBtn;
  }

  private JPanel getPanel_62() {
    if (panel_62 == null) {
      panel_62 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_62.getLayout();
      flowLayout.setVgap(0);
      flowLayout.setHgap(0);
      panel_62.setPreferredSize(new Dimension(70, 50));
      panel_62.add(getGradientFadeBtn());
      panel_62.add(getGradientFadeAllBtn());
    }
    return panel_62;
  }

  private JPanel getPanel_63() {
    if (panel_63 == null) {
      panel_63 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_63.getLayout();
      flowLayout.setVgap(0);
      flowLayout.setHgap(0);
      panel_63.setPreferredSize(new Dimension(70, 50));
      panel_63.add(getGradientInvertBtn());
      panel_63.add(getGradientReverseBtn());
    }
    return panel_63;
  }

  private JPanel getPanel_64() {
    if (panel_64 == null) {
      panel_64 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_64.getLayout();
      flowLayout.setVgap(0);
      flowLayout.setHgap(0);
      panel_64.setPreferredSize(new Dimension(70, 50));
      panel_64.add(getGradientCopyRangeBtn());
      panel_64.add(getGradientPasteRangeBtn());
    }
    return panel_64;
  }

  private JButton getGradientCopyRangeBtn() {
    if (gradientCopyRangeBtn == null) {
      gradientCopyRangeBtn = new JButton();
      gradientCopyRangeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientCopyRangeBtn_clicked();
        }
      });
      gradientCopyRangeBtn.setToolTipText("Copy the content of the selected range");
      gradientCopyRangeBtn.setText("Copy");
      gradientCopyRangeBtn.setSize(new Dimension(138, 24));
      gradientCopyRangeBtn.setPreferredSize(new Dimension(70, 24));
      gradientCopyRangeBtn.setLocation(new Point(4, 181));
      gradientCopyRangeBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientCopyRangeBtn;
  }

  private JButton getGradientPasteRangeBtn() {
    if (gradientPasteRangeBtn == null) {
      gradientPasteRangeBtn = new JButton();
      gradientPasteRangeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientPasteRangeBtn_clicked();
        }
      });
      gradientPasteRangeBtn.setToolTipText("Paste the previously copied range at the left marker position");
      gradientPasteRangeBtn.setText("Paste");
      gradientPasteRangeBtn.setSize(new Dimension(138, 24));
      gradientPasteRangeBtn.setPreferredSize(new Dimension(70, 24));
      gradientPasteRangeBtn.setLocation(new Point(4, 181));
      gradientPasteRangeBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientPasteRangeBtn;
  }

  private JButton getGradientEraseRangeBtn() {
    if (gradientEraseRangeBtn == null) {
      gradientEraseRangeBtn = new JButton();
      gradientEraseRangeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientEraseRangeBtn_clicked();
        }
      });
      gradientEraseRangeBtn.setToolTipText("Erase the currently selected range (i.e. turn it into black)");
      gradientEraseRangeBtn.setText("Erase");
      gradientEraseRangeBtn.setSize(new Dimension(138, 24));
      gradientEraseRangeBtn.setPreferredSize(new Dimension(70, 24));
      gradientEraseRangeBtn.setLocation(new Point(4, 181));
      gradientEraseRangeBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientEraseRangeBtn;
  }

  private JSplitPane getSplitPane() {
    if (splitPane == null) {
      splitPane = new JSplitPane();
      splitPane.setRightComponent(getGradientLibraryThumbnailPnl());
      splitPane.setLeftComponent(getPanel_68());
      splitPane.setDividerLocation(300);
    }
    return splitPane;
  }

  private JPanel getGradientLibraryThumbnailPnl() {
    if (gradientLibraryThumbnailPnl == null) {
      gradientLibraryThumbnailPnl = new JPanel();
      gradientLibraryThumbnailPnl.setLayout(new BorderLayout(0, 0));
      gradientLibraryThumbnailPnl.add(getScrollPane_7(), BorderLayout.CENTER);
    }
    return gradientLibraryThumbnailPnl;
  }

  private JScrollPane getScrollPane_6_1() {
    if (scrollPane_6 == null) {
      scrollPane_6 = new JScrollPane();
      scrollPane_6.setViewportView(getGradientLibTree());
    }
    return scrollPane_6;
  }

  private JTree getGradientLibTree() {
    if (gradientLibTree == null) {
      gradientLibTree = new JTree();
      gradientLibTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          if (tinaController != null) {
            tinaController.getGradientController().gradientTree_changed(e);
          }
        }
      });
    }
    return gradientLibTree;
  }

  private JButton getGradientMonochromeRangeBtn() {
    if (gradientMonochromeRangeBtn == null) {
      gradientMonochromeRangeBtn = new JButton();
      gradientMonochromeRangeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientMononchromeBtn_clicked();
        }
      });
      gradientMonochromeRangeBtn.setToolTipText("Turn the currently selected range into monochrome (i.e. all colors having the same hue)");
      gradientMonochromeRangeBtn.setText("Mono");
      gradientMonochromeRangeBtn.setSize(new Dimension(138, 24));
      gradientMonochromeRangeBtn.setPreferredSize(new Dimension(70, 24));
      gradientMonochromeRangeBtn.setLocation(new Point(4, 181));
      gradientMonochromeRangeBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientMonochromeRangeBtn;
  }

  private JPanel getPanel_67() {
    if (panel_67 == null) {
      panel_67 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_67.getLayout();
      flowLayout.setVgap(0);
      flowLayout.setHgap(0);
      panel_67.setPreferredSize(new Dimension(70, 50));
      panel_67.add(getGradientMonochromeRangeBtn());
      panel_67.add(getGradientSortBtn());
    }
    return panel_67;
  }

  private JButton getGradientFadeAllBtn() {
    if (gradientFadeAllBtn == null) {
      gradientFadeAllBtn = new JButton();
      gradientFadeAllBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientFadeAllBtn_clicked();
        }
      });
      gradientFadeAllBtn.setToolTipText("Fade all colors which are not exactly black into each other, best option to create to create new gradients");
      gradientFadeAllBtn.setText("Fade All");
      gradientFadeAllBtn.setSize(new Dimension(138, 24));
      gradientFadeAllBtn.setPreferredSize(new Dimension(70, 24));
      gradientFadeAllBtn.setLocation(new Point(4, 181));
      gradientFadeAllBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientFadeAllBtn;
  }

  private JPanel getPanel_65() {
    if (panel_65 == null) {
      panel_65 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_65.getLayout();
      flowLayout.setVgap(0);
      flowLayout.setHgap(0);
      panel_65.setPreferredSize(new Dimension(70, 50));
      panel_65.add(getGradientSelectAllBtn());
      panel_65.add(getGradientEraseRangeBtn());
    }
    return panel_65;
  }

  private JPanel getFrameSliderPanel() {
    if (frameSliderPanel == null) {
      frameSliderPanel = new JPanel();
      frameSliderPanel.setPreferredSize(new Dimension(10, 28));
      frameSliderPanel.setLayout(new BorderLayout(0, 0));
      frameSliderPanel.add(getPanel_79(), BorderLayout.WEST);
      frameSliderPanel.add(getPanel_80(), BorderLayout.EAST);

      keyframesFrameSlider = new JSlider();
      frameSliderPanel.add(keyframesFrameSlider, BorderLayout.CENTER);
      keyframesFrameSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getAnimationController() != null) {
            tinaController.getAnimationController().keyFrameSliderChanged();
          }
        }
      });

      keyframesFrameSlider.setPaintTicks(true);
      keyframesFrameSlider.setValue(0);
      keyframesFrameSlider.setPreferredSize(new Dimension(220, 19));
      keyframesFrameSlider.setMinorTickSpacing(5);
      keyframesFrameSlider.setMinimum(1);
      keyframesFrameSlider.setMaximum(300);
      keyframesFrameSlider.setMajorTickSpacing(10);
    }
    return frameSliderPanel;
  }

  private JPanel getPanel_68() {
    if (panel_68 == null) {
      panel_68 = new JPanel();
      panel_68.setLayout(new BorderLayout(0, 0));
      panel_68.add(getScrollPane_6_1(), BorderLayout.CENTER);

      JPanel panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(104, 10));
      panel_68.add(panel_1, BorderLayout.EAST);
      panel_1.setLayout(null);

      gradientLibraryRescanBtn = new JButton();
      gradientLibraryRescanBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGradientController().rescanBtn_clicked();
        }
      });
      gradientLibraryRescanBtn.setToolTipText("Rescan gradient-folder");
      gradientLibraryRescanBtn.setText("Rescan");
      gradientLibraryRescanBtn.setPreferredSize(new Dimension(96, 24));
      gradientLibraryRescanBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      gradientLibraryRescanBtn.setBounds(new Rectangle(4, 4, 96, 24));
      panel_1.add(gradientLibraryRescanBtn);

      gradientLibraryNewFolderBtn = new JButton();
      gradientLibraryNewFolderBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGradientController().newFolderBtn_clicked();
        }
      });
      gradientLibraryNewFolderBtn.setToolTipText("Create a new gradient folder");
      gradientLibraryNewFolderBtn.setText("New Folder");
      gradientLibraryNewFolderBtn.setPreferredSize(new Dimension(96, 24));
      gradientLibraryNewFolderBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      gradientLibraryNewFolderBtn.setBounds(new Rectangle(4, 30, 96, 24));
      panel_1.add(gradientLibraryNewFolderBtn);
      panel_1.add(getGradientLibraryRenameFolderBtn());
    }
    return panel_68;
  }

  public JButton getGradientLibraryRescanBtn() {
    return gradientLibraryRescanBtn;
  }

  public JButton getGradientLibraryNewFolderBtn() {
    return gradientLibraryNewFolderBtn;
  }

  private JButton getGradientLibraryRenameFolderBtn() {
    if (gradientLibraryRenameFolderBtn == null) {
      gradientLibraryRenameFolderBtn = new JButton();
      gradientLibraryRenameFolderBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGradientController().renameFolderBtn_clicked();
        }
      });
      gradientLibraryRenameFolderBtn.setToolTipText("Rename the selected folder");
      gradientLibraryRenameFolderBtn.setText("Rename Fld");
      gradientLibraryRenameFolderBtn.setPreferredSize(new Dimension(96, 24));
      gradientLibraryRenameFolderBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      gradientLibraryRenameFolderBtn.setBounds(new Rectangle(4, 30, 96, 24));
      gradientLibraryRenameFolderBtn.setBounds(4, 56, 96, 24);
    }
    return gradientLibraryRenameFolderBtn;
  }

  private JScrollPane getScrollPane_7() {
    if (scrollPane_7 == null) {
      scrollPane_7 = new JScrollPane();
      scrollPane_7.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane_7.setViewportView(getGradientsList());
    }
    return scrollPane_7;
  }

  private JList getGradientsList() {
    if (gradientsList == null) {
      gradientsList = new JList();
      gradientsList.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          tinaController.getGradientController().gradientLibraryGradientChanged();
        }
      });
    }
    return gradientsList;
  }

  private JPanel getPanel_70() {
    if (panel_70 == null) {
      panel_70 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_70.getLayout();
      flowLayout.setVgap(0);
      flowLayout.setHgap(0);
      panel_70.setPreferredSize(new Dimension(60, 50));
      panel_70.add(getGradientSaveBtn());
    }
    return panel_70;
  }

  private JButton getGradientSaveBtn() {
    if (gradientSaveBtn == null) {
      gradientSaveBtn = new JButton();
      gradientSaveBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGradientController().gradientSaveBtn_clicked();
        }
      });
      gradientSaveBtn.setToolTipText("Save the gradient to the library");
      gradientSaveBtn.setText("Save");
      gradientSaveBtn.setSize(new Dimension(128, 50));
      gradientSaveBtn.setPreferredSize(new Dimension(60, 48));
      gradientSaveBtn.setLocation(new Point(4, 181));
      gradientSaveBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return gradientSaveBtn;
  }

  private JButton getBackgroundColorIndicatorBtn() {
    if (backgroundColorIndicatorBtn == null) {
      backgroundColorIndicatorBtn = new JButton();
      backgroundColorIndicatorBtn.setToolTipText("Set the background color of your fractal");
      backgroundColorIndicatorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.backgroundColorBtn_clicked();
        }
      });
      backgroundColorIndicatorBtn.setBackground(Color.BLACK);
      backgroundColorIndicatorBtn.setPreferredSize(new Dimension(190, 24));
      backgroundColorIndicatorBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      backgroundColorIndicatorBtn.setBounds(544, 4, 56, 46);
    }
    return backgroundColorIndicatorBtn;
  }

  public JButton getRandomizeBtn() {
    return randomizeBtn;
  }

  private JPanel getPanel_72() {
    if (panel_72 == null) {
      panel_72 = new JPanel();
      panel_72.setVisible(false);
      panel_72.setLayout(new BorderLayout(0, 0));
      panel_72.add(getFlameBrowserRootTopPanel(), BorderLayout.NORTH);
      panel_72.add(getFlameBrowserRootBottomPanel(), BorderLayout.CENTER);
    }
    return panel_72;
  }

  private JPanel getFlameBrowserRootTopPanel() {
    if (flameBrowserRootTopPanel == null) {
      flameBrowserRootTopPanel = new JPanel();
      flameBrowserRootTopPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
      flameBrowserRootTopPanel.setPreferredSize(new Dimension(10, 36));
      flameBrowserRootTopPanel.setLayout(new BorderLayout(0, 0));
      flameBrowserRootTopPanel.add(getFlameBrowserRefreshBtn(), BorderLayout.WEST);
      flameBrowserRootTopPanel.add(getFlameBrowserChangeFolderBtn());
    }
    return flameBrowserRootTopPanel;
  }

  private JPanel getFlameBrowserRootBottomPanel() {
    if (flameBrowserRootBottomPanel == null) {
      flameBrowserRootBottomPanel = new JPanel();
      flameBrowserRootBottomPanel.setLayout(new BorderLayout(0, 0));
      flameBrowserRootBottomPanel.add(getFlameBrowserTreePanel(), BorderLayout.WEST);
      flameBrowserRootBottomPanel.add(getFlameBrowserDetailPanel(), BorderLayout.EAST);
      flameBrowserRootBottomPanel.add(getFlameBrowserImagesPanel(), BorderLayout.CENTER);
    }
    return flameBrowserRootBottomPanel;
  }

  private JPanel getFlameBrowserTreePanel() {
    if (flameBrowserTreePanel == null) {
      flameBrowserTreePanel = new JPanel();
      flameBrowserTreePanel.setPreferredSize(new Dimension(200, 10));
      flameBrowserTreePanel.setLayout(new BorderLayout(0, 0));
      flameBrowserTreePanel.add(getFlameBrowserTreeScrollPane(), BorderLayout.CENTER);
    }
    return flameBrowserTreePanel;
  }

  private JPanel getFlameBrowserDetailPanel() {
    if (flameBrowserDetailPanel == null) {
      flameBrowserDetailPanel = new JPanel();
      flameBrowserDetailPanel.setPreferredSize(new Dimension(120, 10));
      flameBrowserDetailPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
      flameBrowserDetailPanel.add(getFlameBrowserToEditorBtn());
      flameBrowserDetailPanel.add(getFlameBrowserToBatchRendererBtn());
      flameBrowserDetailPanel.add(getFlameBrowserToMeshGenBtn());
      flameBrowserDetailPanel.add(getFlameBrowserDeleteBtn());
      flameBrowserDetailPanel.add(getFlameBrowserRenameBtn());
      flameBrowserDetailPanel.add(getFlameBrowserCopyToBtn());
      flameBrowserDetailPanel.add(getFlameBrowserMoveToBtn());
    }
    return flameBrowserDetailPanel;
  }

  private JPanel getFlameBrowserImagesPanel() {
    if (flameBrowserImagesPanel == null) {
      flameBrowserImagesPanel = new JPanel();
      flameBrowserImagesPanel.setLayout(new BorderLayout(0, 0));
    }
    return flameBrowserImagesPanel;
  }

  private JScrollPane getFlameBrowserTreeScrollPane() {
    if (flameBrowserTreeScrollPane == null) {
      flameBrowserTreeScrollPane = new JScrollPane();
      flameBrowserTreeScrollPane.setViewportView(getFlameBrowserTree());
    }
    return flameBrowserTreeScrollPane;
  }

  private JTree getFlameBrowserTree() {
    if (flameBrowserTree == null) {
      flameBrowserTree = new JTree();
      flameBrowserTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          if (tinaController != null) {
            tinaController.getFlameBrowserController().flamesTree_changed();
          }
        }
      });
    }
    return flameBrowserTree;
  }

  private JButton getFlameBrowserRefreshBtn() {
    if (flameBrowserRefreshBtn == null) {
      flameBrowserRefreshBtn = new JButton();
      flameBrowserRefreshBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().refreshBtn_clicked();
        }
      });
      flameBrowserRefreshBtn.setText("Refresh");
      flameBrowserRefreshBtn.setPreferredSize(new Dimension(192, 24));
      flameBrowserRefreshBtn.setMnemonic(KeyEvent.VK_R);
      flameBrowserRefreshBtn.setMinimumSize(new Dimension(100, 46));
      flameBrowserRefreshBtn.setMaximumSize(new Dimension(32000, 46));
      flameBrowserRefreshBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserRefreshBtn;
  }

  private JButton getFlameBrowserToEditorBtn() {
    if (flameBrowserToEditorBtn == null) {
      flameBrowserToEditorBtn = new JButton();
      flameBrowserToEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().toEditorBtn_clicked();
        }
      });
      flameBrowserToEditorBtn.setToolTipText("Copy the current fractal into the Editor");
      flameBrowserToEditorBtn.setText("To Editor");
      flameBrowserToEditorBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserToEditorBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserToEditorBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserToEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserToEditorBtn;
  }

  private JButton getFlameBrowserDeleteBtn() {
    if (flameBrowserDeleteBtn == null) {
      flameBrowserDeleteBtn = new JButton();
      flameBrowserDeleteBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().deleteBtn_clicked();
        }
      });
      flameBrowserDeleteBtn.setToolTipText("Delete the currently selected flame");
      flameBrowserDeleteBtn.setText("Delete");
      flameBrowserDeleteBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserDeleteBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserDeleteBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserDeleteBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserDeleteBtn;
  }

  private JButton getFlameBrowserRenameBtn() {
    if (flameBrowserRenameBtn == null) {
      flameBrowserRenameBtn = new JButton();
      flameBrowserRenameBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().renameBtn_clicked();
        }
      });
      flameBrowserRenameBtn.setToolTipText("Rename the currently selected flame");
      flameBrowserRenameBtn.setText("Rename...");
      flameBrowserRenameBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserRenameBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserRenameBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserRenameBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserRenameBtn;
  }

  private JButton getFlameBrowserChangeFolderBtn() {
    if (flameBrowserChangeFolderBtn == null) {
      flameBrowserChangeFolderBtn = new JButton();
      flameBrowserChangeFolderBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().changeFolderBtn_clicked();
        }
      });
      flameBrowserChangeFolderBtn.setText("Change folder...");
      flameBrowserChangeFolderBtn.setPreferredSize(new Dimension(125, 46));
      flameBrowserChangeFolderBtn.setMnemonic(KeyEvent.VK_F);
      flameBrowserChangeFolderBtn.setMinimumSize(new Dimension(100, 46));
      flameBrowserChangeFolderBtn.setMaximumSize(new Dimension(32000, 46));
      flameBrowserChangeFolderBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserChangeFolderBtn;
  }

  private JPanel getPanel_73() {
    if (panel_73 == null) {
      panel_73 = new JPanel();
      panel_73.setPreferredSize(new Dimension(10, 24));
      panel_73.setMinimumSize(new Dimension(10, 20));
    }
    return panel_73;
  }

  public JCheckBox getTinaPaletteFadeColorsCBx() {
    return tinaPaletteFadeColorsCBx;
  }

  private JButton getDancingFlamesReplaceFlameFromEditorBtn() {
    if (dancingFlamesReplaceFlameFromEditorBtn == null) {
      dancingFlamesReplaceFlameFromEditorBtn = new JButton();
      dancingFlamesReplaceFlameFromEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().replaceFlameFromEditorBtn_clicked(tinaController.getCurrFlame());
        }
      });
      dancingFlamesReplaceFlameFromEditorBtn.setToolTipText("Replace the current flame with flame from the editor");
      dancingFlamesReplaceFlameFromEditorBtn.setText("Repl from Editor");
      dancingFlamesReplaceFlameFromEditorBtn.setPreferredSize(new Dimension(115, 24));
      dancingFlamesReplaceFlameFromEditorBtn.setMnemonic(KeyEvent.VK_F);
      dancingFlamesReplaceFlameFromEditorBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesReplaceFlameFromEditorBtn.setMaximumSize(new Dimension(160, 24));
      dancingFlamesReplaceFlameFromEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return dancingFlamesReplaceFlameFromEditorBtn;
  }

  private JButton getDancingFlamesRenameFlameBtn() {
    if (dancingFlamesRenameFlameBtn == null) {
      dancingFlamesRenameFlameBtn = new JButton();
      dancingFlamesRenameFlameBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getDancingFractalsController().renameFlameBtn_clicked();
        }
      });
      dancingFlamesRenameFlameBtn.setToolTipText("Rename the current flame");
      dancingFlamesRenameFlameBtn.setText("Rename");
      dancingFlamesRenameFlameBtn.setPreferredSize(new Dimension(115, 24));
      dancingFlamesRenameFlameBtn.setMnemonic(KeyEvent.VK_R);
      dancingFlamesRenameFlameBtn.setMinimumSize(new Dimension(100, 24));
      dancingFlamesRenameFlameBtn.setMaximumSize(new Dimension(160, 24));
      dancingFlamesRenameFlameBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return dancingFlamesRenameFlameBtn;
  }

  public JButton getDancingFlamesRenameMotionBtn() {
    return dancingFlamesRenameMotionBtn;
  }

  public JCheckBox getDancingFlamesMutedCBx() {
    return dancingFlamesMutedCBx;
  }

  private JPanel getPanel_74() {
    if (panel_74 == null) {
      panel_74 = new JPanel();
      panel_74.setLayout(new BorderLayout(0, 0));
      panel_74.add(getPanel_75(), BorderLayout.EAST);
      panel_74.add(getPanel_76(), BorderLayout.WEST);
      panel_74.add(getPanel_77(), BorderLayout.CENTER);
    }
    return panel_74;
  }

  private JPanel getPanel_75() {
    if (panel_75 == null) {
      panel_75 = new JPanel();
      panel_75.setPreferredSize(new Dimension(340, 10));
      panel_75.setLayout(null);

      layerAddBtn = new JButton();
      layerAddBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.addLayerBtn_clicked();
        }
      });
      layerAddBtn.setToolTipText("Add new layer");
      layerAddBtn.setText("Add");
      layerAddBtn.setPreferredSize(new Dimension(56, 24));
      layerAddBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      layerAddBtn.setBounds(6, 81, 90, 24);
      panel_75.add(layerAddBtn);

      layerWeightEd = new JWFNumberField();
      layerWeightEd.setValueStep(0.05);
      layerWeightEd.setText("");
      layerWeightEd.setSize(new Dimension(81, 24));
      layerWeightEd.setPreferredSize(new Dimension(56, 24));
      layerWeightEd.setLocation(new Point(238, 6));
      layerWeightEd.setHasMinValue(true);
      layerWeightEd.setFont(new Font("Dialog", Font.PLAIN, 10));
      layerWeightEd.setBounds(172, 6, 90, 24);
      layerWeightEd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!layerWeightEd.isMouseAdjusting() || layerWeightEd.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.layerWeightREd_changed();
          }
        }
      });

      panel_75.add(layerWeightEd);

      layerDuplicateBtn = new JButton();
      layerDuplicateBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.duplicateLayerBtn_clicked();
        }
      });
      layerDuplicateBtn.setToolTipText("Duplicate layer");
      layerDuplicateBtn.setText("Duplicate");
      layerDuplicateBtn.setPreferredSize(new Dimension(90, 24));
      layerDuplicateBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      layerDuplicateBtn.setBounds(6, 107, 90, 24);
      panel_75.add(layerDuplicateBtn);

      layerDeleteBtn = new JButton();
      layerDeleteBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.deleteLayerBtn_clicked();
        }
      });
      layerDeleteBtn.setToolTipText("Delete layer");
      layerDeleteBtn.setText("Delete");
      layerDeleteBtn.setPreferredSize(new Dimension(90, 24));
      layerDeleteBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      layerDeleteBtn.setBounds(112, 81, 90, 24);
      panel_75.add(layerDeleteBtn);

      layerVisibleBtn = new JToggleButton();
      layerVisibleBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.layerVisibilityButton_clicked();
        }
      });
      layerVisibleBtn.setSelected(true);
      layerVisibleBtn.setToolTipText("Hide/show layer");
      layerVisibleBtn.setText("Visible");
      layerVisibleBtn.setSize(new Dimension(138, 24));
      layerVisibleBtn.setPreferredSize(new Dimension(136, 24));
      layerVisibleBtn.setLocation(new Point(4, 181));
      layerVisibleBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      layerVisibleBtn.setBounds(6, 7, 86, 24);
      panel_75.add(layerVisibleBtn);

      JLabel lblWeight = new JLabel();
      lblWeight.setText("Weight");
      lblWeight.setSize(new Dimension(20, 22));
      lblWeight.setPreferredSize(new Dimension(24, 22));
      lblWeight.setLocation(new Point(212, 6));
      lblWeight.setHorizontalAlignment(SwingConstants.RIGHT);
      lblWeight.setFont(new Font("Dialog", Font.BOLD, 10));
      lblWeight.setBounds(107, 10, 58, 22);
      panel_75.add(lblWeight);

      layerHideOthersBtn = new JButton();
      layerHideOthersBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.layerHideAllOthersButton_clicked();
        }
      });
      layerHideOthersBtn.setToolTipText("Hide all layers except the currently selected one");
      layerHideOthersBtn.setText("Hide all others");
      layerHideOthersBtn.setPreferredSize(new Dimension(56, 24));
      layerHideOthersBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      layerHideOthersBtn.setBounds(6, 34, 159, 24);
      panel_75.add(layerHideOthersBtn);

      layerShowAllBtn = new JButton();
      layerShowAllBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.layerShowAllButton_clicked();
        }
      });
      layerShowAllBtn.setToolTipText("Show all layers");
      layerShowAllBtn.setText("Show all");
      layerShowAllBtn.setPreferredSize(new Dimension(56, 24));
      layerShowAllBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      layerShowAllBtn.setBounds(172, 34, 159, 24);
      panel_75.add(layerShowAllBtn);
    }
    return panel_75;
  }

  private JPanel getPanel_76() {
    if (panel_76 == null) {
      panel_76 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_76.getLayout();
      flowLayout.setVgap(10);
      panel_76.setPreferredSize(new Dimension(140, 10));

      layerAppendBtn = new JToggleButton();
      panel_76.add(layerAppendBtn);
      layerAppendBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.layerAppendModeBtnClicked();
        }
      });
      layerAppendBtn.setToolTipText("Append new flames as new layers");
      layerAppendBtn.setText("Layer append mode");
      layerAppendBtn.setPreferredSize(new Dimension(136, 42));
      layerAppendBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_76.add(getLayerPreviewBtn());
    }
    return panel_76;
  }

  private JPanel getPanel_77() {
    if (panel_77 == null) {
      panel_77 = new JPanel();
      panel_77.setLayout(new BorderLayout(0, 0));
      panel_77.add(getScrollPane_8(), BorderLayout.CENTER);
    }
    return panel_77;
  }

  private JScrollPane getScrollPane_8() {
    if (scrollPane_8 == null) {
      scrollPane_8 = new JScrollPane();
      scrollPane_8.setViewportView(getLayersTable());
    }
    return scrollPane_8;
  }

  private JTable getLayersTable() {
    if (layersTable == null) {
      layersTable = new JTable();
      layersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            tinaController.layersTableClicked();
          }
        }

      });
    }
    return layersTable;
  }

  public JWFNumberField getLayerWeightEd() {
    return layerWeightEd;
  }

  public JButton getLayerDeleteBtn() {
    return layerDeleteBtn;
  }

  public JToggleButton getLayerVisibleBtn() {
    return layerVisibleBtn;
  }

  public JButton getLayerAddBtn() {
    return layerAddBtn;
  }

  public JButton getLayerDuplicateBtn() {
    return layerDuplicateBtn;
  }

  public JToggleButton getLayerAppendBtn() {
    return layerAppendBtn;
  }

  public JButton getLayerShowAllBtn() {
    return layerShowAllBtn;
  }

  public JButton getLayerHideOthersBtn() {
    return layerHideOthersBtn;
  }

  private JButton getFlameBrowserToBatchRendererBtn() {
    if (flameBrowserToBatchRendererBtn == null) {
      flameBrowserToBatchRendererBtn = new JButton();
      flameBrowserToBatchRendererBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().toBatchRendererBtn_clicked();
        }
      });
      flameBrowserToBatchRendererBtn.setToolTipText("Schedule the current fractal for rendering in the Batch Renderer");
      flameBrowserToBatchRendererBtn.setText("To Batch Renderer");
      flameBrowserToBatchRendererBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserToBatchRendererBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserToBatchRendererBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserToBatchRendererBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserToBatchRendererBtn;
  }

  private JToggleButton getLayerPreviewBtn() {
    if (layerPreviewBtn == null) {
      layerPreviewBtn = new JToggleButton();
      layerPreviewBtn.setSelected(true);
      layerPreviewBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.layerPreviewBtnClicked();
        }
      });
      layerPreviewBtn.setToolTipText("Display a small realtime-preview of the currently selected layer");
      layerPreviewBtn.setText("Layer preview");
      layerPreviewBtn.setSize(new Dimension(138, 24));
      layerPreviewBtn.setPreferredSize(new Dimension(136, 24));
      layerPreviewBtn.setLocation(new Point(4, 181));
      layerPreviewBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return layerPreviewBtn;
  }

  private JPanel getMotionBlurPanel() {
    if (motionBlurPanel == null) {
      motionBlurPanel = new JPanel();
      motionBlurPanel.setLayout(null);

      JLabel lblBlurLength = new JLabel();
      lblBlurLength.setText("Blur length");
      lblBlurLength.setSize(new Dimension(94, 22));
      lblBlurLength.setPreferredSize(new Dimension(94, 22));
      lblBlurLength.setName("");
      lblBlurLength.setLocation(new Point(4, 4));
      lblBlurLength.setFont(new Font("Dialog", Font.BOLD, 10));
      lblBlurLength.setBounds(6, 6, 94, 22);
      motionBlurPanel.add(lblBlurLength);

      motionBlurLengthField = new JWFNumberField();
      motionBlurLengthField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!motionBlurLengthField.isMouseAdjusting() || motionBlurLengthField.getMouseChangeCount() == 0) {
              if (!motionBlurLengthSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().motionBlurLengthREd_changed();
          }
        }
      });
      motionBlurLengthField.setLinkedMotionControlName("motionBlurLengthSlider");
      motionBlurLengthField.setHasMinValue(true);
      motionBlurLengthField.setOnlyIntegers(true);
      motionBlurLengthField.setValueStep(1.0);
      motionBlurLengthField.setSize(new Dimension(100, 24));
      motionBlurLengthField.setPreferredSize(new Dimension(100, 24));
      motionBlurLengthField.setLocation(new Point(100, 4));
      motionBlurLengthField.setFont(new Font("Dialog", Font.PLAIN, 10));
      motionBlurLengthField.setBounds(102, 6, 100, 24);
      motionBlurPanel.add(motionBlurLengthField);

      motionBlurLengthSlider = new JSlider();
      motionBlurLengthSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      motionBlurLengthSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().motionBlurLengthSlider_changed(e);
          }
        }
      });
      motionBlurLengthSlider.setValue(0);
      motionBlurLengthSlider.setSize(new Dimension(220, 19));
      motionBlurLengthSlider.setPreferredSize(new Dimension(220, 19));
      motionBlurLengthSlider.setName("motionBlurLengthSlider");
      motionBlurLengthSlider.setLocation(new Point(202, 4));
      motionBlurLengthSlider.setBounds(204, 6, 220, 19);
      motionBlurPanel.add(motionBlurLengthSlider);

      motionBlurTimeStepSlider = new JSlider();
      motionBlurTimeStepSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      motionBlurTimeStepSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().motionBlurTimeStepSlider_changed(e);
          }
        }
      });
      motionBlurTimeStepSlider.setValue(0);
      motionBlurTimeStepSlider.setSize(new Dimension(220, 19));
      motionBlurTimeStepSlider.setPreferredSize(new Dimension(220, 19));
      motionBlurTimeStepSlider.setName("motionBlurTimeStepSlider");
      motionBlurTimeStepSlider.setLocation(new Point(202, 28));
      motionBlurTimeStepSlider.setBounds(204, 30, 220, 19);
      motionBlurPanel.add(motionBlurTimeStepSlider);

      motionBlurTimeStepField = new JWFNumberField();
      motionBlurTimeStepField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!motionBlurTimeStepField.isMouseAdjusting() || motionBlurTimeStepField.getMouseChangeCount() == 0) {
              if (!motionBlurTimeStepSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().motionBlurTimeStepREd_changed();
          }
        }
      });
      motionBlurTimeStepField.setLinkedMotionControlName("motionBlurTimeStepSlider");
      motionBlurTimeStepField.setHasMinValue(true);
      motionBlurTimeStepField.setValueStep(0.01);
      motionBlurTimeStepField.setText("");
      motionBlurTimeStepField.setSize(new Dimension(100, 24));
      motionBlurTimeStepField.setPreferredSize(new Dimension(100, 24));
      motionBlurTimeStepField.setLocation(new Point(100, 28));
      motionBlurTimeStepField.setFont(new Font("Dialog", Font.PLAIN, 10));
      motionBlurTimeStepField.setBounds(102, 30, 100, 24);
      motionBlurPanel.add(motionBlurTimeStepField);

      JLabel lblTimeStep = new JLabel();
      lblTimeStep.setText("Time step");
      lblTimeStep.setSize(new Dimension(94, 22));
      lblTimeStep.setPreferredSize(new Dimension(94, 22));
      lblTimeStep.setName("");
      lblTimeStep.setLocation(new Point(4, 28));
      lblTimeStep.setFont(new Font("Dialog", Font.BOLD, 10));
      lblTimeStep.setBounds(6, 30, 94, 22);
      motionBlurPanel.add(lblTimeStep);

      JLabel lblDecay = new JLabel();
      lblDecay.setText("Decay");
      lblDecay.setSize(new Dimension(94, 22));
      lblDecay.setPreferredSize(new Dimension(94, 22));
      lblDecay.setName("");
      lblDecay.setLocation(new Point(4, 52));
      lblDecay.setFont(new Font("Dialog", Font.BOLD, 10));
      lblDecay.setBounds(6, 54, 94, 22);
      motionBlurPanel.add(lblDecay);

      motionBlurDecayField = new JWFNumberField();
      motionBlurDecayField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!motionBlurDecayField.isMouseAdjusting() || motionBlurDecayField.getMouseChangeCount() == 0) {
              if (!motionBlurDecaySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().motionBlurDecayREd_changed();
          }
        }
      });
      motionBlurDecayField.setLinkedMotionControlName("motionBlurDecaySlider");
      motionBlurDecayField.setValueStep(0.01);
      motionBlurDecayField.setText("");
      motionBlurDecayField.setSize(new Dimension(100, 24));
      motionBlurDecayField.setPreferredSize(new Dimension(100, 24));
      motionBlurDecayField.setLocation(new Point(100, 52));
      motionBlurDecayField.setFont(new Font("Dialog", Font.PLAIN, 10));
      motionBlurDecayField.setBounds(102, 54, 100, 24);
      motionBlurPanel.add(motionBlurDecayField);

      motionBlurDecaySlider = new JSlider();
      motionBlurDecaySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      motionBlurDecaySlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().motionBlurDecaySlider_changed(e);
          }
        }
      });
      motionBlurDecaySlider.setMaximum(500);
      motionBlurDecaySlider.setValue(0);
      motionBlurDecaySlider.setSize(new Dimension(220, 19));
      motionBlurDecaySlider.setPreferredSize(new Dimension(220, 19));
      motionBlurDecaySlider.setName("motionBlurDecaySlider");
      motionBlurDecaySlider.setLocation(new Point(202, 52));
      motionBlurDecaySlider.setBounds(204, 54, 220, 19);
      motionBlurPanel.add(motionBlurDecaySlider);
    }
    return motionBlurPanel;
  }

  private JPanel getPanel_79() {
    if (panel_79 == null) {
      panel_79 = new JPanel();
      panel_79.setPreferredSize(new Dimension(128, 10));
      panel_79.setLayout(null);

      keyframesFrameLbl = new JLabel();
      keyframesFrameLbl.setBounds(6, 2, 39, 22);
      panel_79.add(keyframesFrameLbl);
      keyframesFrameLbl.setText("Frame");
      keyframesFrameLbl.setPreferredSize(new Dimension(94, 22));
      keyframesFrameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
      keyframesFrameLbl.setFont(new Font("Dialog", Font.BOLD, 10));

      keyframesFrameField = new JWFNumberField();
      keyframesFrameField.setText("1");
      keyframesFrameField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getAnimationController() != null) {
            if (tinaController != null && tinaController.getAnimationController() != null) {
              tinaController.getAnimationController().keyFrameFieldChanged();
            }
          }
        }
      });

      keyframesFrameField.setBounds(51, 2, 70, 24);
      panel_79.add(keyframesFrameField);
      keyframesFrameField.setPreferredSize(new Dimension(56, 22));
      keyframesFrameField.setOnlyIntegers(true);
      keyframesFrameField.setMinValue(1.0);
      keyframesFrameField.setMaxValue(30000.0);
      keyframesFrameField.setHasMinValue(true);
      keyframesFrameField.setHasMaxValue(true);
      keyframesFrameField.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return panel_79;
  }

  private JPanel getPanel_80() {
    if (panel_80 == null) {
      panel_80 = new JPanel();
      panel_80.setPreferredSize(new Dimension(220, 10));
      panel_80.setLayout(null);

      keyframesFrameCountField = new JWFNumberField();
      keyframesFrameCountField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getAnimationController() != null) {
            try {
              swfAnimatorFrameSlider.setMaximum(keyframesFrameCountField.getIntValue());
              tinaController.getAnimationController().keyFrameCountFieldChanged();
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });

      keyframesFrameCountField.setText("300");
      keyframesFrameCountField.setPreferredSize(new Dimension(56, 22));
      keyframesFrameCountField.setOnlyIntegers(true);
      keyframesFrameCountField.setMinValue(1.0);
      keyframesFrameCountField.setMaxValue(30000.0);
      keyframesFrameCountField.setHasMinValue(true);
      keyframesFrameCountField.setHasMaxValue(true);
      keyframesFrameCountField.setFont(new Font("Dialog", Font.PLAIN, 10));
      keyframesFrameCountField.setBounds(144, 0, 70, 24);
      panel_80.add(keyframesFrameCountField);

      keyframesFrameCountLbl = new JLabel();
      keyframesFrameCountLbl.setText("Frame count");
      keyframesFrameCountLbl.setPreferredSize(new Dimension(94, 22));
      keyframesFrameCountLbl.setHorizontalAlignment(SwingConstants.RIGHT);
      keyframesFrameCountLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      keyframesFrameCountLbl.setBounds(58, 0, 82, 22);
      panel_80.add(keyframesFrameCountLbl);

      motionCurvePlayPreviewButton = new JButton();
      motionCurvePlayPreviewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getAnimationController() != null) {
            try {
              tinaController.getAnimationController().playPreviewButtonClicked();
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      motionCurvePlayPreviewButton.setToolTipText("");
      motionCurvePlayPreviewButton.setText("Play");
      motionCurvePlayPreviewButton.setSize(new Dimension(70, 24));
      motionCurvePlayPreviewButton.setPreferredSize(new Dimension(55, 24));
      motionCurvePlayPreviewButton.setLocation(new Point(0, 57));
      motionCurvePlayPreviewButton.setFont(new Font("Dialog", Font.BOLD, 10));
      motionCurvePlayPreviewButton.setBounds(0, 0, 70, 24);
      panel_80.add(motionCurvePlayPreviewButton);
    }
    return panel_80;
  }

  public JWFNumberField getKeyframesFrameField() {
    return keyframesFrameField;
  }

  public JWFNumberField getKeyframesFrameCountField() {
    return keyframesFrameCountField;
  }

  public JSlider getKeyframesFrameSlider() {
    return keyframesFrameSlider;
  }

  public JToggleButton getMotionCurveEditModeButton() {
    return motionCurveEditModeButton;
  }

  private JPanel getPanel_66() {
    if (panel_66 == null) {
      panel_66 = new JPanel();
      panel_66.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel_66.setMaximumSize(new Dimension(200, 24));
      panel_66.setPreferredSize(new Dimension(125, 24));
      panel_66.setLayout(new BoxLayout(panel_66, BoxLayout.X_AXIS));
      panel_66.add(getTinaSaveFlameButton());
      panel_66.add(getBtnQsave());
    }
    return panel_66;
  }

  public JWFNumberField getMotionBlurLengthField() {
    return motionBlurLengthField;
  }

  public JSlider getMotionBlurLengthSlider() {
    return motionBlurLengthSlider;
  }

  public JWFNumberField getMotionBlurTimeStepField() {
    return motionBlurTimeStepField;
  }

  public JSlider getMotionBlurTimeStepSlider() {
    return motionBlurTimeStepSlider;
  }

  public JWFNumberField getMotionBlurDecayField() {
    return motionBlurDecayField;
  }

  public JSlider getMotionBlurDecaySlider() {
    return motionBlurDecaySlider;
  }

  public JLabel getKeyframesFrameLbl() {
    return keyframesFrameLbl;
  }

  public JLabel getKeyframesFrameCountLbl() {
    return keyframesFrameCountLbl;
  }

  public JWFNumberField getAffineMoveHorizAmountREd() {
    return affineMoveVertAmountREd;
  }

  private JPanel getPanel_34() {
    if (panel_34 == null) {
      panel_34 = new JPanel();
      panel_34.setLayout(null);

      postSymmetryTypeCmb = new JComboBox();
      postSymmetryTypeCmb.setSize(new Dimension(125, 22));
      postSymmetryTypeCmb.setPreferredSize(new Dimension(125, 22));
      postSymmetryTypeCmb.setLocation(new Point(100, 4));
      postSymmetryTypeCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryTypeCmb.setBounds(102, 6, 100, 24);
      postSymmetryTypeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().postSymmetryCmb_changed();
          }
        }
      });
      panel_34.add(postSymmetryTypeCmb);

      JLabel postSymmetryTypeLbl = new JLabel();
      postSymmetryTypeLbl.setText("Symmetry type");
      postSymmetryTypeLbl.setSize(new Dimension(94, 22));
      postSymmetryTypeLbl.setPreferredSize(new Dimension(94, 22));
      postSymmetryTypeLbl.setLocation(new Point(488, 2));
      postSymmetryTypeLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryTypeLbl.setBounds(6, 6, 94, 22);
      panel_34.add(postSymmetryTypeLbl);

      postSymmetryDistanceREd = new JWFNumberField();
      postSymmetryDistanceREd.setLinkedLabelControlName("postSymmetryDistanceLbl");
      postSymmetryDistanceREd.setValueStep(0.05);
      postSymmetryDistanceREd.setText("");
      postSymmetryDistanceREd.setSize(new Dimension(100, 24));
      postSymmetryDistanceREd.setPreferredSize(new Dimension(100, 24));
      postSymmetryDistanceREd.setLocation(new Point(584, 2));
      postSymmetryDistanceREd.setLinkedMotionControlName("postSymmetryDistanceSlider");
      postSymmetryDistanceREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      postSymmetryDistanceREd.setEditable(true);
      postSymmetryDistanceREd.setBounds(102, 31, 100, 24);
      postSymmetryDistanceREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!postSymmetryDistanceREd.isMouseAdjusting() || postSymmetryDistanceREd.getMouseChangeCount() == 0) {
              if (!postSymmetryDistanceSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().postSymmetryDistanceREd_changed();
          }
        }
      });
      panel_34.add(postSymmetryDistanceREd);

      JLabel postSymmetryDistanceLbl = new JLabel();
      postSymmetryDistanceLbl.setName("postSymmetryDistanceLbl");
      postSymmetryDistanceLbl.setText("Distance");
      postSymmetryDistanceLbl.setSize(new Dimension(94, 22));
      postSymmetryDistanceLbl.setPreferredSize(new Dimension(94, 22));
      postSymmetryDistanceLbl.setLocation(new Point(488, 2));
      postSymmetryDistanceLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryDistanceLbl.setBounds(6, 31, 94, 22);
      panel_34.add(postSymmetryDistanceLbl);

      JLabel postSymmetryRotationLbl = new JLabel();
      postSymmetryRotationLbl.setName("postSymmetryRotationLbl");
      postSymmetryRotationLbl.setText("Rotation");
      postSymmetryRotationLbl.setSize(new Dimension(94, 22));
      postSymmetryRotationLbl.setPreferredSize(new Dimension(94, 22));
      postSymmetryRotationLbl.setLocation(new Point(488, 2));
      postSymmetryRotationLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryRotationLbl.setBounds(6, 56, 94, 22);
      panel_34.add(postSymmetryRotationLbl);

      postSymmetryRotationREd = new JWFNumberField();
      postSymmetryRotationREd.setLinkedLabelControlName("postSymmetryRotationLbl");
      postSymmetryRotationREd.setValueStep(0.05);
      postSymmetryRotationREd.setText("");
      postSymmetryRotationREd.setSize(new Dimension(100, 24));
      postSymmetryRotationREd.setPreferredSize(new Dimension(100, 24));
      postSymmetryRotationREd.setLocation(new Point(584, 2));
      postSymmetryRotationREd.setLinkedMotionControlName("postSymmetryRotationSlider");
      postSymmetryRotationREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      postSymmetryRotationREd.setEditable(true);
      postSymmetryRotationREd.setBounds(102, 56, 100, 24);
      postSymmetryRotationREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!postSymmetryRotationREd.isMouseAdjusting() || postSymmetryRotationREd.getMouseChangeCount() == 0) {
              if (!postSymmetryRotationSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().postSymmetryRotationREd_changed();
          }
        }
      });
      panel_34.add(postSymmetryRotationREd);

      postSymmetryDistanceSlider = new JSlider();
      postSymmetryDistanceSlider.setValue(0);
      postSymmetryDistanceSlider.setSize(new Dimension(220, 19));
      postSymmetryDistanceSlider.setPreferredSize(new Dimension(220, 19));
      postSymmetryDistanceSlider.setName("postSymmetryDistanceSlider");
      postSymmetryDistanceSlider.setMinimum(-25000);
      postSymmetryDistanceSlider.setMaximum(25000);
      postSymmetryDistanceSlider.setLocation(new Point(686, 2));
      postSymmetryDistanceSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryDistanceSlider.setBounds(204, 31, 220, 24);
      postSymmetryDistanceSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postSymmetryDistanceSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().postSymmetryDistanceSlider_changed(e);
          }
        }
      });
      panel_34.add(postSymmetryDistanceSlider);

      postSymmetryRotationSlider = new JSlider();
      postSymmetryRotationSlider.setMaximum(180);
      postSymmetryRotationSlider.setValue(0);
      postSymmetryRotationSlider.setSize(new Dimension(220, 19));
      postSymmetryRotationSlider.setPreferredSize(new Dimension(220, 19));
      postSymmetryRotationSlider.setName("postSymmetryRotationSlider");
      postSymmetryRotationSlider.setMinimum(-180);
      postSymmetryRotationSlider.setLocation(new Point(686, 2));
      postSymmetryRotationSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryRotationSlider.setBounds(204, 56, 220, 24);
      postSymmetryRotationSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postSymmetryRotationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().postSymmetryRotationSlider_changed(e);
          }
        }
      });
      panel_34.add(postSymmetryRotationSlider);

      postSymmetryOrderREd = new JWFNumberField();
      postSymmetryOrderREd.setLinkedLabelControlName("postSymmetryOrderLbl");
      postSymmetryOrderREd.setMinValue(2.0);
      postSymmetryOrderREd.setOnlyIntegers(true);
      postSymmetryOrderREd.setValueStep(0.05);
      postSymmetryOrderREd.setText("");
      postSymmetryOrderREd.setSize(new Dimension(100, 24));
      postSymmetryOrderREd.setPreferredSize(new Dimension(100, 24));
      postSymmetryOrderREd.setMaxValue(18.0);
      postSymmetryOrderREd.setLocation(new Point(584, 2));
      postSymmetryOrderREd.setLinkedMotionControlName("postSymmetryOrderSlider");
      postSymmetryOrderREd.setHasMinValue(true);
      postSymmetryOrderREd.setHasMaxValue(true);
      postSymmetryOrderREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      postSymmetryOrderREd.setEditable(true);
      postSymmetryOrderREd.setBounds(532, 6, 100, 24);
      postSymmetryOrderREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!postSymmetryOrderREd.isMouseAdjusting() || postSymmetryOrderREd.getMouseChangeCount() == 0) {
              if (!postSymmetryOrderSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().postSymmetryOrderREd_changed();
          }
        }
      });
      panel_34.add(postSymmetryOrderREd);

      JLabel postSymmetryOrderLbl = new JLabel();
      postSymmetryOrderLbl.setName("postSymmetryOrderLbl");
      postSymmetryOrderLbl.setText("Symmetry order");
      postSymmetryOrderLbl.setSize(new Dimension(94, 22));
      postSymmetryOrderLbl.setPreferredSize(new Dimension(94, 22));
      postSymmetryOrderLbl.setLocation(new Point(488, 2));
      postSymmetryOrderLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryOrderLbl.setBounds(436, 6, 94, 22);
      panel_34.add(postSymmetryOrderLbl);

      JLabel postSymmetryCentreXLbl = new JLabel();
      postSymmetryCentreXLbl.setName("postSymmetryCentreXLbl");
      postSymmetryCentreXLbl.setText("Centre X");
      postSymmetryCentreXLbl.setSize(new Dimension(94, 22));
      postSymmetryCentreXLbl.setPreferredSize(new Dimension(94, 22));
      postSymmetryCentreXLbl.setLocation(new Point(488, 2));
      postSymmetryCentreXLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryCentreXLbl.setBounds(436, 31, 94, 22);
      panel_34.add(postSymmetryCentreXLbl);

      postSymmetryCentreXREd = new JWFNumberField();
      postSymmetryCentreXREd.setLinkedLabelControlName("postSymmetryCentreXLbl");
      postSymmetryCentreXREd.setValueStep(0.05);
      postSymmetryCentreXREd.setText("");
      postSymmetryCentreXREd.setSize(new Dimension(100, 24));
      postSymmetryCentreXREd.setPreferredSize(new Dimension(100, 24));
      postSymmetryCentreXREd.setLocation(new Point(584, 2));
      postSymmetryCentreXREd.setLinkedMotionControlName("postSymmetryCentreXSlider");
      postSymmetryCentreXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      postSymmetryCentreXREd.setEditable(true);
      postSymmetryCentreXREd.setBounds(532, 31, 100, 24);
      postSymmetryCentreXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!postSymmetryCentreXREd.isMouseAdjusting() || postSymmetryCentreXREd.getMouseChangeCount() == 0) {
              if (!postSymmetryCentreXSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().postSymmetryCentreXREd_changed();
          }
        }
      });
      panel_34.add(postSymmetryCentreXREd);

      postSymmetryOrderSlider = new JSlider();
      postSymmetryOrderSlider.setValue(0);
      postSymmetryOrderSlider.setSize(new Dimension(220, 19));
      postSymmetryOrderSlider.setPreferredSize(new Dimension(220, 19));
      postSymmetryOrderSlider.setName("postSymmetryOrderSlider");
      postSymmetryOrderSlider.setMinimum(2);
      postSymmetryOrderSlider.setMaximum(18);
      postSymmetryOrderSlider.setLocation(new Point(686, 2));
      postSymmetryOrderSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryOrderSlider.setBounds(634, 6, 220, 24);
      postSymmetryOrderSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postSymmetryOrderSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().postSymmetryOrderSlider_changed(e);
          }
        }
      });
      panel_34.add(postSymmetryOrderSlider);

      postSymmetryCentreXSlider = new JSlider();
      postSymmetryCentreXSlider.setMaximum(25000);
      postSymmetryCentreXSlider.setValue(0);
      postSymmetryCentreXSlider.setSize(new Dimension(220, 19));
      postSymmetryCentreXSlider.setPreferredSize(new Dimension(220, 19));
      postSymmetryCentreXSlider.setName("postSymmetryCentreXSlider");
      postSymmetryCentreXSlider.setMinimum(-25000);
      postSymmetryCentreXSlider.setLocation(new Point(686, 2));
      postSymmetryCentreXSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryCentreXSlider.setBounds(634, 31, 220, 24);
      postSymmetryCentreXSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postSymmetryCentreXSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().postSymmetryCentreXSlider_changed(e);
          }
        }
      });
      panel_34.add(postSymmetryCentreXSlider);

      JLabel postSymmetryCentreYLbl = new JLabel();
      postSymmetryCentreYLbl.setName("postSymmetryCentreYLbl");
      postSymmetryCentreYLbl.setText("Centre Y");
      postSymmetryCentreYLbl.setSize(new Dimension(94, 22));
      postSymmetryCentreYLbl.setPreferredSize(new Dimension(94, 22));
      postSymmetryCentreYLbl.setLocation(new Point(488, 2));
      postSymmetryCentreYLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryCentreYLbl.setBounds(436, 56, 94, 22);
      panel_34.add(postSymmetryCentreYLbl);

      postSymmetryCentreYREd = new JWFNumberField();
      postSymmetryCentreYREd.setLinkedLabelControlName("postSymmetryCentreYLbl");
      postSymmetryCentreYREd.setValueStep(0.05);
      postSymmetryCentreYREd.setText("");
      postSymmetryCentreYREd.setSize(new Dimension(100, 24));
      postSymmetryCentreYREd.setPreferredSize(new Dimension(100, 24));
      postSymmetryCentreYREd.setLocation(new Point(584, 2));
      postSymmetryCentreYREd.setLinkedMotionControlName("postSymmetryCentreYSlider");
      postSymmetryCentreYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      postSymmetryCentreYREd.setEditable(true);
      postSymmetryCentreYREd.setBounds(532, 56, 100, 24);
      postSymmetryCentreYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!postSymmetryCentreYREd.isMouseAdjusting() || postSymmetryCentreYREd.getMouseChangeCount() == 0) {
              if (!postSymmetryCentreYSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().postSymmetryCentreYREd_changed();
          }
        }
      });
      panel_34.add(postSymmetryCentreYREd);

      postSymmetryCentreYSlider = new JSlider();
      postSymmetryCentreYSlider.setValue(0);
      postSymmetryCentreYSlider.setSize(new Dimension(220, 19));
      postSymmetryCentreYSlider.setPreferredSize(new Dimension(220, 19));
      postSymmetryCentreYSlider.setName("postSymmetryCentreYSlider");
      postSymmetryCentreYSlider.setMinimum(-25000);
      postSymmetryCentreYSlider.setMaximum(25000);
      postSymmetryCentreYSlider.setLocation(new Point(686, 2));
      postSymmetryCentreYSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      postSymmetryCentreYSlider.setBounds(634, 56, 220, 24);
      postSymmetryCentreYSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postSymmetryCentreYSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().postSymmetryCentreYSlider_changed(e);
          }
        }
      });
      panel_34.add(postSymmetryCentreYSlider);
    }
    return panel_34;
  }

  public JComboBox getPostSymmetryTypeCmb() {
    return postSymmetryTypeCmb;
  }

  public JWFNumberField getPostSymmetryDistanceREd() {
    return postSymmetryDistanceREd;
  }

  public JSlider getPostSymmetryDistanceSlider() {
    return postSymmetryDistanceSlider;
  }

  public JWFNumberField getPostSymmetryRotationREd() {
    return postSymmetryRotationREd;
  }

  public JSlider getPostSymmetryRotationSlider() {
    return postSymmetryRotationSlider;
  }

  public JWFNumberField getPostSymmetryOrderREd() {
    return postSymmetryOrderREd;
  }

  public JSlider getPostSymmetryOrderSlider() {
    return postSymmetryOrderSlider;
  }

  public JWFNumberField getPostSymmetryCentreXREd() {
    return postSymmetryCentreXREd;
  }

  public JSlider getPostSymmetryCentreXSlider() {
    return postSymmetryCentreXSlider;
  }

  public JWFNumberField getPostSymmetryCentreYREd() {
    return postSymmetryCentreYREd;
  }

  public JSlider getPostSymmetryCentreYSlider() {
    return postSymmetryCentreYSlider;
  }

  private JPanel getPanel_78() {
    if (panel_78 == null) {
      panel_78 = new JPanel();
      panel_78.setPreferredSize(new Dimension(190, 24));
      panel_78.setLayout(new BoxLayout(panel_78, BoxLayout.X_AXIS));

      randomStyleLbl = new JLabel();
      panel_78.add(randomStyleLbl);
      randomStyleLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
      randomStyleLbl.setPreferredSize(new Dimension(100, 22));
      randomStyleLbl.setText("  Random Generator");
      randomStyleLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_78.add(getRandomStyleCmb());
    }
    return panel_78;
  }

  private JPanel getPanel_81() {
    if (panel_81 == null) {
      panel_81 = new JPanel();
      panel_81.setPreferredSize(new Dimension(180, 24));
      panel_81.setLayout(new BoxLayout(panel_81, BoxLayout.X_AXIS));
      panel_81.add(getLblSymmetry());
      panel_81.add(getRandomSymmetryCmb());

      randomGradientCmb = new JComboBox();
      randomGradientCmb.setToolTipText("Random-Symmetry-Geneator");
      randomGradientCmb.setPreferredSize(new Dimension(50, 24));
      randomGradientCmb.setMinimumSize(new Dimension(100, 24));
      randomGradientCmb.setMaximumSize(new Dimension(32767, 24));
      randomGradientCmb.setMaximumRowCount(32);
      randomGradientCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_81.add(randomGradientCmb);
    }
    return panel_81;
  }

  private JLabel getLblSymmetry() {
    if (lblSymmetry == null) {
      lblSymmetry = new JLabel();
      lblSymmetry.setText("  Symmetry/Gradient");
      lblSymmetry.setPreferredSize(new Dimension(100, 22));
      lblSymmetry.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSymmetry.setAlignmentX(1.0f);
    }
    return lblSymmetry;
  }

  private JComboBox getRandomSymmetryCmb() {
    if (randomSymmetryCmb == null) {
      randomSymmetryCmb = new JComboBox();
      randomSymmetryCmb.setToolTipText("Random-Symmetry-Geneator");
      randomSymmetryCmb.setPreferredSize(new Dimension(50, 24));
      randomSymmetryCmb.setMinimumSize(new Dimension(100, 24));
      randomSymmetryCmb.setMaximumSize(new Dimension(32767, 24));
      randomSymmetryCmb.setMaximumRowCount(32);
      randomSymmetryCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return randomSymmetryCmb;
  }

  private JPanel getPanel_82() {
    if (panel_82 == null) {
      panel_82 = new JPanel();
      panel_82.setLayout(null);

      stereo3dModeCmb = new JComboBox();
      stereo3dModeCmb.setSize(new Dimension(125, 22));
      stereo3dModeCmb.setPreferredSize(new Dimension(125, 22));
      stereo3dModeCmb.setLocation(new Point(100, 4));
      stereo3dModeCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      stereo3dModeCmb.setBounds(102, 6, 322, 24);
      stereo3dModeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dModeCmb_changed();
          }
        }
      });
      panel_82.add(stereo3dModeCmb);

      JLabel lblStereodMode = new JLabel();
      lblStereodMode.setText("Stereo3d mode");
      lblStereodMode.setSize(new Dimension(94, 22));
      lblStereodMode.setPreferredSize(new Dimension(94, 22));
      lblStereodMode.setLocation(new Point(488, 2));
      lblStereodMode.setFont(new Font("Dialog", Font.BOLD, 10));
      lblStereodMode.setBounds(6, 6, 94, 22);
      panel_82.add(lblStereodMode);

      JLabel lblAngle = new JLabel();
      lblAngle.setText("View angle");
      lblAngle.setSize(new Dimension(94, 22));
      lblAngle.setPreferredSize(new Dimension(94, 22));
      lblAngle.setName("lblAngle");
      lblAngle.setLocation(new Point(488, 2));
      lblAngle.setFont(new Font("Dialog", Font.BOLD, 10));
      lblAngle.setBounds(6, 31, 94, 22);
      panel_82.add(lblAngle);

      stereo3dAngleREd = new JWFNumberField();
      stereo3dAngleREd.setMinValue(-10000.0);
      stereo3dAngleREd.setMaxValue(10000.0);
      stereo3dAngleREd.setHasMinValue(true);
      stereo3dAngleREd.setHasMaxValue(true);
      stereo3dAngleREd.setValueStep(0.05);
      stereo3dAngleREd.setText("");
      stereo3dAngleREd.setSize(new Dimension(100, 24));
      stereo3dAngleREd.setPreferredSize(new Dimension(100, 24));
      stereo3dAngleREd.setLocation(new Point(584, 2));
      stereo3dAngleREd.setLinkedMotionControlName("stereo3dAngleSlider");
      stereo3dAngleREd.setLinkedLabelControlName("lblAngle");
      stereo3dAngleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      stereo3dAngleREd.setEditable(true);
      stereo3dAngleREd.setBounds(102, 31, 100, 24);
      stereo3dAngleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!stereo3dAngleREd.isMouseAdjusting() || stereo3dAngleREd.getMouseChangeCount() == 0) {
              if (!stereo3dAngleSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().stereo3dAngleREd_changed();
          }
        }
      });

      panel_82.add(stereo3dAngleREd);

      stereo3dAngleSlider = new JSlider();
      stereo3dAngleSlider.setMinimum(-30000);
      stereo3dAngleSlider.setValue(0);
      stereo3dAngleSlider.setSize(new Dimension(220, 19));
      stereo3dAngleSlider.setPreferredSize(new Dimension(220, 19));
      stereo3dAngleSlider.setName("stereo3dAngleSlider");
      stereo3dAngleSlider.setMaximum(30000);
      stereo3dAngleSlider.setLocation(new Point(686, 2));
      stereo3dAngleSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      stereo3dAngleSlider.setBounds(204, 31, 220, 24);
      stereo3dAngleSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      stereo3dAngleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dAngleSlider_changed(e);
          }
        }
      });

      panel_82.add(stereo3dAngleSlider);

      JLabel lblImageCount = new JLabel();
      lblImageCount.setToolTipText("Number of generated (total) images per side per frame");
      lblImageCount.setText("Images per eye");
      lblImageCount.setSize(new Dimension(94, 22));
      lblImageCount.setPreferredSize(new Dimension(94, 22));
      lblImageCount.setName("lblImageCount");
      lblImageCount.setLocation(new Point(488, 2));
      lblImageCount.setFont(new Font("Dialog", Font.BOLD, 10));
      lblImageCount.setBounds(437, 81, 94, 22);
      panel_82.add(lblImageCount);

      stereo3dInterpolatedImageCountREd = new JWFNumberField();
      stereo3dInterpolatedImageCountREd.setHasMinValue(true);
      stereo3dInterpolatedImageCountREd.setHasMaxValue(true);
      stereo3dInterpolatedImageCountREd.setMaxValue(24.0);
      stereo3dInterpolatedImageCountREd.setMinValue(2.0);
      stereo3dInterpolatedImageCountREd.setOnlyIntegers(true);
      stereo3dInterpolatedImageCountREd.setValueStep(0.05);
      stereo3dInterpolatedImageCountREd.setText("");
      stereo3dInterpolatedImageCountREd.setSize(new Dimension(100, 24));
      stereo3dInterpolatedImageCountREd.setPreferredSize(new Dimension(100, 24));
      stereo3dInterpolatedImageCountREd.setLocation(new Point(584, 2));
      stereo3dInterpolatedImageCountREd.setLinkedMotionControlName("stereo3dInterpolatedImageCountSlider");
      stereo3dInterpolatedImageCountREd.setLinkedLabelControlName("lblImageCount");
      stereo3dInterpolatedImageCountREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      stereo3dInterpolatedImageCountREd.setEditable(true);
      stereo3dInterpolatedImageCountREd.setBounds(533, 81, 100, 24);
      stereo3dInterpolatedImageCountREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!stereo3dInterpolatedImageCountREd.isMouseAdjusting() || stereo3dInterpolatedImageCountREd.getMouseChangeCount() == 0) {
              if (!stereo3dInterpolatedImageCountSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().stereo3dInterpolatedImageCountREd_changed();
          }
        }
      });
      panel_82.add(stereo3dInterpolatedImageCountREd);

      stereo3dInterpolatedImageCountSlider = new JSlider();
      stereo3dInterpolatedImageCountSlider.setValue(0);
      stereo3dInterpolatedImageCountSlider.setSize(new Dimension(220, 19));
      stereo3dInterpolatedImageCountSlider.setPreferredSize(new Dimension(220, 19));
      stereo3dInterpolatedImageCountSlider.setName("stereo3dInterpolatedImageCountSlider");
      stereo3dInterpolatedImageCountSlider.setMinimum(2);
      stereo3dInterpolatedImageCountSlider.setMaximum(24);
      stereo3dInterpolatedImageCountSlider.setLocation(new Point(686, 2));
      stereo3dInterpolatedImageCountSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      stereo3dInterpolatedImageCountSlider.setBounds(635, 81, 220, 24);
      stereo3dInterpolatedImageCountSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      stereo3dInterpolatedImageCountSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dInterpolatedImageCountSlider_changed(e);
          }
        }
      });
      panel_82.add(stereo3dInterpolatedImageCountSlider);

      JLabel lblEyeDistance = new JLabel();
      lblEyeDistance.setText("Eye distance");
      lblEyeDistance.setSize(new Dimension(94, 22));
      lblEyeDistance.setPreferredSize(new Dimension(94, 22));
      lblEyeDistance.setName("lblEyeDistance");
      lblEyeDistance.setLocation(new Point(488, 2));
      lblEyeDistance.setFont(new Font("Dialog", Font.BOLD, 10));
      lblEyeDistance.setBounds(6, 56, 94, 22);
      panel_82.add(lblEyeDistance);

      stereo3dEyeDistREd = new JWFNumberField();
      stereo3dEyeDistREd.setMaxValue(0.25);
      stereo3dEyeDistREd.setHasMinValue(true);
      stereo3dEyeDistREd.setValueStep(0.05);
      stereo3dEyeDistREd.setText("");
      stereo3dEyeDistREd.setSize(new Dimension(100, 24));
      stereo3dEyeDistREd.setPreferredSize(new Dimension(100, 24));
      stereo3dEyeDistREd.setLocation(new Point(584, 2));
      stereo3dEyeDistREd.setLinkedMotionControlName("stereo3dEyeDistSlider");
      stereo3dEyeDistREd.setLinkedLabelControlName("lblEyeDistance");
      stereo3dEyeDistREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      stereo3dEyeDistREd.setEditable(true);
      stereo3dEyeDistREd.setBounds(102, 56, 100, 24);
      stereo3dEyeDistREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!stereo3dEyeDistREd.isMouseAdjusting() || stereo3dEyeDistREd.getMouseChangeCount() == 0) {
              if (!stereo3dEyeDistSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().stereo3dEyeDistREd_changed();
          }
        }
      });
      panel_82.add(stereo3dEyeDistREd);

      stereo3dEyeDistSlider = new JSlider();
      stereo3dEyeDistSlider.setValue(0);
      stereo3dEyeDistSlider.setSize(new Dimension(220, 19));
      stereo3dEyeDistSlider.setPreferredSize(new Dimension(220, 19));
      stereo3dEyeDistSlider.setName("stereo3dEyeDistSlider");
      stereo3dEyeDistSlider.setMaximum(1000);
      stereo3dEyeDistSlider.setLocation(new Point(686, 2));
      stereo3dEyeDistSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      stereo3dEyeDistSlider.setBounds(204, 56, 220, 24);
      stereo3dEyeDistSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      stereo3dEyeDistSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dEyeDistSlider_changed(e);
          }
        }
      });
      panel_82.add(stereo3dEyeDistSlider);

      stereo3dLeftEyeColorCmb = new JComboBox();
      stereo3dLeftEyeColorCmb.setSize(new Dimension(125, 22));
      stereo3dLeftEyeColorCmb.setPreferredSize(new Dimension(125, 22));
      stereo3dLeftEyeColorCmb.setLocation(new Point(100, 4));
      stereo3dLeftEyeColorCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      stereo3dLeftEyeColorCmb.setBounds(533, 31, 322, 24);
      stereo3dLeftEyeColorCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dLeftEyeColorCmb_changed();
          }
        }
      });
      panel_82.add(stereo3dLeftEyeColorCmb);

      JLabel lblLeftEyeColor = new JLabel();
      lblLeftEyeColor.setToolTipText("Render color for anaglyph rendering (left image)");
      lblLeftEyeColor.setText("Left eye color");
      lblLeftEyeColor.setSize(new Dimension(94, 22));
      lblLeftEyeColor.setPreferredSize(new Dimension(94, 22));
      lblLeftEyeColor.setLocation(new Point(488, 2));
      lblLeftEyeColor.setFont(new Font("Dialog", Font.BOLD, 10));
      lblLeftEyeColor.setBounds(437, 31, 94, 22);
      panel_82.add(lblLeftEyeColor);

      stereo3dRightEyeColorCmb = new JComboBox();
      stereo3dRightEyeColorCmb.setSize(new Dimension(125, 22));
      stereo3dRightEyeColorCmb.setPreferredSize(new Dimension(125, 22));
      stereo3dRightEyeColorCmb.setLocation(new Point(100, 4));
      stereo3dRightEyeColorCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      stereo3dRightEyeColorCmb.setBounds(532, 56, 323, 24);
      stereo3dRightEyeColorCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dRightEyeColorCmb_changed();
          }
        }
      });
      panel_82.add(stereo3dRightEyeColorCmb);

      JLabel lblRightEyeColor = new JLabel();
      lblRightEyeColor.setToolTipText("Render color for anaglyph rendering (right image)");
      lblRightEyeColor.setText("Right eye color");
      lblRightEyeColor.setSize(new Dimension(94, 22));
      lblRightEyeColor.setPreferredSize(new Dimension(94, 22));
      lblRightEyeColor.setLocation(new Point(488, 2));
      lblRightEyeColor.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRightEyeColor.setBounds(436, 56, 94, 22);
      panel_82.add(lblRightEyeColor);
      panel_82.add(getStereo3dPreviewCmb());
      panel_82.add(getLblPreviewMode());

      stereo3dFocalOffsetREd = new JWFNumberField();
      stereo3dFocalOffsetREd.setValueStep(0.05);
      stereo3dFocalOffsetREd.setText("");
      stereo3dFocalOffsetREd.setSize(new Dimension(100, 24));
      stereo3dFocalOffsetREd.setPreferredSize(new Dimension(100, 24));
      stereo3dFocalOffsetREd.setMinValue(-10000.0);
      stereo3dFocalOffsetREd.setMaxValue(10000.0);
      stereo3dFocalOffsetREd.setLocation(new Point(584, 2));
      stereo3dFocalOffsetREd.setLinkedMotionControlName("stereo3dFocalOffsetSlider");
      stereo3dFocalOffsetREd.setLinkedLabelControlName("lblFocalOffset");
      stereo3dFocalOffsetREd.setHasMinValue(true);
      stereo3dFocalOffsetREd.setHasMaxValue(true);
      stereo3dFocalOffsetREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      stereo3dFocalOffsetREd.setEditable(true);
      stereo3dFocalOffsetREd.setBounds(102, 81, 100, 24);
      stereo3dFocalOffsetREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!stereo3dFocalOffsetREd.isMouseAdjusting() || stereo3dFocalOffsetREd.getMouseChangeCount() == 0) {
              if (!stereo3dFocalOffsetSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().stereo3dFocalOffsetREd_changed();
          }
        }
      });
      panel_82.add(stereo3dFocalOffsetREd);

      JLabel lblFocalOffset = new JLabel();
      lblFocalOffset.setText("Focal offset");
      lblFocalOffset.setSize(new Dimension(94, 22));
      lblFocalOffset.setPreferredSize(new Dimension(94, 22));
      lblFocalOffset.setName("lblFocalOffset");
      lblFocalOffset.setLocation(new Point(488, 2));
      lblFocalOffset.setFont(new Font("Dialog", Font.BOLD, 10));
      lblFocalOffset.setBounds(6, 81, 94, 22);
      panel_82.add(lblFocalOffset);

      stereo3dFocalOffsetSlider = new JSlider();
      stereo3dFocalOffsetSlider.setValue(0);
      stereo3dFocalOffsetSlider.setSize(new Dimension(220, 19));
      stereo3dFocalOffsetSlider.setPreferredSize(new Dimension(220, 19));
      stereo3dFocalOffsetSlider.setName("stereo3dFocalOffsetSlider");
      stereo3dFocalOffsetSlider.setMinimum(-30000);
      stereo3dFocalOffsetSlider.setMaximum(30000);
      stereo3dFocalOffsetSlider.setLocation(new Point(686, 2));
      stereo3dFocalOffsetSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      stereo3dFocalOffsetSlider.setBounds(204, 81, 220, 24);
      stereo3dFocalOffsetSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      stereo3dFocalOffsetSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dFocalOffsetSlider_changed(e);
          }
        }
      });
      panel_82.add(stereo3dFocalOffsetSlider);

      stereo3dSwapSidesCBx = new JCheckBox("Swap sides");
      stereo3dSwapSidesCBx.setToolTipText("Swap left and right images");
      stereo3dSwapSidesCBx.setBounds(865, 8, 197, 18);
      stereo3dSwapSidesCBx.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dSwapSidesCBx_changed();
          }
        }
      });

      panel_82.add(stereo3dSwapSidesCBx);
    }
    return panel_82;
  }

  public JComboBox getStereo3dModeCmb() {
    return stereo3dModeCmb;
  }

  public JWFNumberField getStereo3dAngleREd() {
    return stereo3dAngleREd;
  }

  public JSlider getStereo3dAngleSlider() {
    return stereo3dAngleSlider;
  }

  public JWFNumberField getStereo3dEyeDistREd() {
    return stereo3dEyeDistREd;
  }

  public JSlider getStereo3dEyeDistSlider() {
    return stereo3dEyeDistSlider;
  }

  public JComboBox getStereo3dLeftEyeColorCmb() {
    return stereo3dLeftEyeColorCmb;
  }

  public JComboBox getStereo3dRightEyeColorCmb() {
    return stereo3dRightEyeColorCmb;
  }

  public JWFNumberField getStereo3dInterpolatedImageCountREd() {
    return stereo3dInterpolatedImageCountREd;
  }

  public JSlider getStereo3dInterpolatedImageCountSlider() {
    return stereo3dInterpolatedImageCountSlider;
  }

  public JComboBox getSwfAnimatorGlobalScript2Cmb() {
    return swfAnimatorGlobalScript2Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript3Cmb() {
    return swfAnimatorGlobalScript3Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript4Cmb() {
    return swfAnimatorGlobalScript4Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript5Cmb() {
    return swfAnimatorGlobalScript5Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript3Cmb() {
    return swfAnimatorXFormScript3Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript4Cmb() {
    return swfAnimatorXFormScript4Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript5Cmb() {
    return swfAnimatorXFormScript5Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript2Cmb() {
    return swfAnimatorXFormScript2Cmb;
  }

  private JComboBox getStereo3dPreviewCmb() {
    if (stereo3dPreviewCmb == null) {
      stereo3dPreviewCmb = new JComboBox();
      stereo3dPreviewCmb.setSize(new Dimension(125, 22));
      stereo3dPreviewCmb.setPreferredSize(new Dimension(125, 22));
      stereo3dPreviewCmb.setLocation(new Point(100, 4));
      stereo3dPreviewCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      stereo3dPreviewCmb.setBounds(533, 6, 322, 24);
      stereo3dPreviewCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dPreviewCmb_changed();
          }
        }
      });
    }
    return stereo3dPreviewCmb;
  }

  private JLabel getLblPreviewMode() {
    if (lblPreviewMode == null) {
      lblPreviewMode = new JLabel();
      lblPreviewMode.setText("Preview mode");
      lblPreviewMode.setSize(new Dimension(94, 22));
      lblPreviewMode.setPreferredSize(new Dimension(94, 22));
      lblPreviewMode.setLocation(new Point(488, 2));
      lblPreviewMode.setFont(new Font("Dialog", Font.BOLD, 10));
      lblPreviewMode.setBounds(437, 6, 94, 22);
    }
    return lblPreviewMode;
  }

  public JWFNumberField getStereo3dFocalOffsetREd() {
    return stereo3dFocalOffsetREd;
  }

  public JSlider getStereo3dFocalOffsetSlider() {
    return stereo3dFocalOffsetSlider;
  }

  public JWFNumberField getSwfAnimatorGlobalScript1REd() {
    return swfAnimatorGlobalScript1REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript2REd() {
    return swfAnimatorGlobalScript2REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript3REd() {
    return swfAnimatorGlobalScript3REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript4REd() {
    return swfAnimatorGlobalScript4REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript1REd() {
    return swfAnimatorXFormScript1REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript2REd() {
    return swfAnimatorXFormScript2REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript3REd() {
    return swfAnimatorXFormScript3REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript4REd() {
    return swfAnimatorXFormScript4REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript5REd() {
    return swfAnimatorXFormScript5REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript5REd() {
    return swfAnimatorGlobalScript5REd;
  }

  public JWFNumberField getSwfAnimatorMotionBlurLengthREd() {
    return swfAnimatorMotionBlurLengthREd;
  }

  public JWFNumberField getSwfAnimatorMotionBlurTimeStepREd() {
    return swfAnimatorMotionBlurTimeStepREd;
  }

  public JCheckBox getStereo3dSwapSidesCBx() {
    return stereo3dSwapSidesCBx;
  }

  public JPanel getRandomMoviePanel() {
    return randomMoviePanel;
  }

  public JButton getSwfAnimatorGenRandomBatchBtn() {
    return swfAnimatorGenRandomBatchBtn;
  }

  public JComboBox getSwfAnimatorRandGenCmb() {
    return swfAnimatorRandGenCmb;
  }

  private JPanel getPanel_84() {
    if (panel_84 == null) {
      panel_84 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_84.getLayout();
      flowLayout.setVgap(2);
      flowLayout.setHgap(4);
      panel_84.setBorder(null);

      swfAnimatorPlayButton = new JButton();
      swfAnimatorPlayButton.setIconTextGap(2);
      panel_84.add(swfAnimatorPlayButton);
      swfAnimatorPlayButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().playButton_clicked();
        }
      });
      swfAnimatorPlayButton.setText("Play");
      swfAnimatorPlayButton.setPreferredSize(new Dimension(82, 28));
      swfAnimatorPlayButton.setFont(new Font("Dialog", Font.BOLD, 9));
      swfAnimatorPlayButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-multimedia.png")));

      swfAnimatorFrameToEditorBtn = new JButton();
      panel_84.add(swfAnimatorFrameToEditorBtn);
      swfAnimatorFrameToEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().swfAnimatorFrameToEditorBtn_clicked();
        }
      });
      swfAnimatorFrameToEditorBtn.setToolTipText("Copy current flame into Editor");
      swfAnimatorFrameToEditorBtn.setText("E");
      swfAnimatorFrameToEditorBtn.setPreferredSize(new Dimension(42, 28));
      swfAnimatorFrameToEditorBtn.setMnemonic(KeyEvent.VK_E);
      swfAnimatorFrameToEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_84.add(getBtnRender());
    }
    return panel_84;
  }

  private JScrollPane getScrollPane_9() {
    if (scrollPane_9 == null) {
      scrollPane_9 = new JScrollPane();
      scrollPane_9.setBorder(new TitledBorder(null, "XForm scripts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      scrollPane_9.setViewportView(getPanel_86());
    }
    return scrollPane_9;
  }

  private JPanel getPanel_86() {
    if (panel_86 == null) {
      panel_86 = new JPanel();
      panel_86.setPreferredSize(new Dimension(315, 280));
      panel_86.setLayout(null);
      animateXFormScriptLbl = new JLabel();
      animateXFormScriptLbl.setBounds(0, 0, 21, 22);
      panel_86.add(animateXFormScriptLbl);
      animateXFormScriptLbl.setName("animateXFormScriptLbl");
      animateXFormScriptLbl.setPreferredSize(new Dimension(94, 22));
      animateXFormScriptLbl.setText("01");
      animateXFormScriptLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_86.add(getSwfAnimatorXFormScript1Cmb());

      swfAnimatorXFormScript1REd = new JWFNumberField();
      swfAnimatorXFormScript1REd.setBounds(213, 0, 100, 24);
      panel_86.add(swfAnimatorXFormScript1REd);
      swfAnimatorXFormScript1REd.setValue(1.0);
      swfAnimatorXFormScript1REd.setWithMotionCurve(true);
      swfAnimatorXFormScript1REd.setValueStep(0.1);
      swfAnimatorXFormScript1REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript1REd.setMotionPropertyName("xFormScript1");
      swfAnimatorXFormScript1REd.setLinkedLabelControlName("animateXFormScriptLbl");
      swfAnimatorXFormScript1REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblXformScript = new JLabel();
      lblXformScript.setBounds(0, 23, 21, 22);
      panel_86.add(lblXformScript);
      lblXformScript.setName("lblXformScript");
      lblXformScript.setText("02");
      lblXformScript.setPreferredSize(new Dimension(94, 22));
      lblXformScript.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript2Cmb = new JComboBox();
      swfAnimatorXFormScript2Cmb.setBounds(22, 23, 186, 24);
      panel_86.add(swfAnimatorXFormScript2Cmb);
      swfAnimatorXFormScript2Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript2Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript2Cmb.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript2REd = new JWFNumberField();
      swfAnimatorXFormScript2REd.setBounds(213, 23, 100, 24);
      panel_86.add(swfAnimatorXFormScript2REd);
      swfAnimatorXFormScript2REd.setValue(1.0);
      swfAnimatorXFormScript2REd.setWithMotionCurve(true);
      swfAnimatorXFormScript2REd.setValueStep(0.1);
      swfAnimatorXFormScript2REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript2REd.setMotionPropertyName("xFormScript2");
      swfAnimatorXFormScript2REd.setLinkedLabelControlName("lblXformScript");
      swfAnimatorXFormScript2REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblXformScript_1 = new JLabel();
      lblXformScript_1.setBounds(0, 46, 21, 22);
      panel_86.add(lblXformScript_1);
      lblXformScript_1.setName("lblXformScript_1");
      lblXformScript_1.setText("03");
      lblXformScript_1.setPreferredSize(new Dimension(94, 22));
      lblXformScript_1.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript3Cmb = new JComboBox();
      swfAnimatorXFormScript3Cmb.setBounds(22, 46, 186, 24);
      panel_86.add(swfAnimatorXFormScript3Cmb);
      swfAnimatorXFormScript3Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript3Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript3Cmb.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript3REd = new JWFNumberField();
      swfAnimatorXFormScript3REd.setBounds(213, 46, 100, 24);
      panel_86.add(swfAnimatorXFormScript3REd);
      swfAnimatorXFormScript3REd.setValue(1.0);
      swfAnimatorXFormScript3REd.setWithMotionCurve(true);
      swfAnimatorXFormScript3REd.setValueStep(0.1);
      swfAnimatorXFormScript3REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript3REd.setMotionPropertyName("xFormScript3");
      swfAnimatorXFormScript3REd.setLinkedLabelControlName("lblXformScript_1");
      swfAnimatorXFormScript3REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      swfAnimatorXFormScript4Cmb = new JComboBox();
      swfAnimatorXFormScript4Cmb.setBounds(22, 69, 186, 24);
      panel_86.add(swfAnimatorXFormScript4Cmb);
      swfAnimatorXFormScript4Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript4Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript4Cmb.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel lblXformScript_2 = new JLabel();
      lblXformScript_2.setBounds(0, 69, 21, 22);
      panel_86.add(lblXformScript_2);
      lblXformScript_2.setName("lblXformScript_2");
      lblXformScript_2.setText("04");
      lblXformScript_2.setPreferredSize(new Dimension(94, 22));
      lblXformScript_2.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel lblXformScript_3 = new JLabel();
      lblXformScript_3.setBounds(0, 92, 21, 22);
      panel_86.add(lblXformScript_3);
      lblXformScript_3.setName("lblXformScript_3");
      lblXformScript_3.setText("05");
      lblXformScript_3.setPreferredSize(new Dimension(94, 22));
      lblXformScript_3.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript5Cmb = new JComboBox();
      swfAnimatorXFormScript5Cmb.setBounds(22, 92, 186, 24);
      panel_86.add(swfAnimatorXFormScript5Cmb);
      swfAnimatorXFormScript5Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript5Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript5Cmb.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript5REd = new JWFNumberField();
      swfAnimatorXFormScript5REd.setBounds(213, 92, 100, 24);
      panel_86.add(swfAnimatorXFormScript5REd);
      swfAnimatorXFormScript5REd.setValue(1.0);
      swfAnimatorXFormScript5REd.setWithMotionCurve(true);
      swfAnimatorXFormScript5REd.setValueStep(0.1);
      swfAnimatorXFormScript5REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript5REd.setMotionPropertyName("xFormScript5");
      swfAnimatorXFormScript5REd.setLinkedLabelControlName("lblXformScript_3");
      swfAnimatorXFormScript5REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      swfAnimatorXFormScript4REd = new JWFNumberField();
      swfAnimatorXFormScript4REd.setBounds(213, 69, 100, 24);
      panel_86.add(swfAnimatorXFormScript4REd);
      swfAnimatorXFormScript4REd.setValue(1.0);
      swfAnimatorXFormScript4REd.setWithMotionCurve(true);
      swfAnimatorXFormScript4REd.setValueStep(0.1);
      swfAnimatorXFormScript4REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript4REd.setMotionPropertyName("xFormScript4");
      swfAnimatorXFormScript4REd.setLinkedLabelControlName("lblXformScript_2");
      swfAnimatorXFormScript4REd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblXformScript_4 = new JLabel();
      lblXformScript_4.setText("06");
      lblXformScript_4.setPreferredSize(new Dimension(94, 22));
      lblXformScript_4.setName("lblXformScript_4");
      lblXformScript_4.setFont(new Font("Dialog", Font.BOLD, 10));
      lblXformScript_4.setBounds(0, 115, 21, 22);
      panel_86.add(lblXformScript_4);

      swfAnimatorXFormScript6Cmb = new JComboBox();
      swfAnimatorXFormScript6Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript6Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript6Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript6Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript6Cmb.setBounds(22, 115, 186, 24);
      panel_86.add(swfAnimatorXFormScript6Cmb);

      swfAnimatorXFormScript6REd = new JWFNumberField();
      swfAnimatorXFormScript6REd.setWithMotionCurve(true);
      swfAnimatorXFormScript6REd.setValueStep(0.1);
      swfAnimatorXFormScript6REd.setValue(1.0);
      swfAnimatorXFormScript6REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript6REd.setMotionPropertyName("xFormScript6");
      swfAnimatorXFormScript6REd.setLinkedLabelControlName("lblXformScript_4");
      swfAnimatorXFormScript6REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript6REd.setBounds(213, 115, 100, 24);
      swfAnimatorXFormScript6REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript6REd());
        }
      });
      swfAnimatorXFormScript6REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });

      panel_86.add(swfAnimatorXFormScript6REd);

      JLabel lblXformScript_5 = new JLabel();
      lblXformScript_5.setText("07");
      lblXformScript_5.setPreferredSize(new Dimension(94, 22));
      lblXformScript_5.setName("lblXformScript_5");
      lblXformScript_5.setFont(new Font("Dialog", Font.BOLD, 10));
      lblXformScript_5.setBounds(0, 138, 21, 22);
      panel_86.add(lblXformScript_5);

      swfAnimatorXFormScript7Cmb = new JComboBox();
      swfAnimatorXFormScript7Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript7Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript7Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript7Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript7Cmb.setBounds(22, 138, 186, 24);
      panel_86.add(swfAnimatorXFormScript7Cmb);

      swfAnimatorXFormScript7REd = new JWFNumberField();
      swfAnimatorXFormScript7REd.setWithMotionCurve(true);
      swfAnimatorXFormScript7REd.setValueStep(0.1);
      swfAnimatorXFormScript7REd.setValue(1.0);
      swfAnimatorXFormScript7REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript7REd.setMotionPropertyName("xFormScript7");
      swfAnimatorXFormScript7REd.setLinkedLabelControlName("lblXformScript_5");
      swfAnimatorXFormScript7REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript7REd.setBounds(213, 138, 100, 24);
      swfAnimatorXFormScript7REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript7REd());
        }
      });
      swfAnimatorXFormScript7REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });

      panel_86.add(swfAnimatorXFormScript7REd);

      JLabel lblXformScript_6 = new JLabel();
      lblXformScript_6.setText("08");
      lblXformScript_6.setPreferredSize(new Dimension(94, 22));
      lblXformScript_6.setName("lblXformScript_6");
      lblXformScript_6.setFont(new Font("Dialog", Font.BOLD, 10));
      lblXformScript_6.setBounds(0, 161, 21, 22);
      panel_86.add(lblXformScript_6);

      swfAnimatorXFormScript8Cmb = new JComboBox();
      swfAnimatorXFormScript8Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript8Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript8Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript8Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript8Cmb.setBounds(22, 161, 186, 24);
      panel_86.add(swfAnimatorXFormScript8Cmb);

      swfAnimatorXFormScript8REd = new JWFNumberField();
      swfAnimatorXFormScript8REd.setWithMotionCurve(true);
      swfAnimatorXFormScript8REd.setValueStep(0.1);
      swfAnimatorXFormScript8REd.setValue(1.0);
      swfAnimatorXFormScript8REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript8REd.setMotionPropertyName("xFormScript8");
      swfAnimatorXFormScript8REd.setLinkedLabelControlName("lblXformScript_6");
      swfAnimatorXFormScript8REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript8REd.setBounds(213, 161, 100, 24);
      swfAnimatorXFormScript8REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript8REd());
        }
      });
      swfAnimatorXFormScript8REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript8REd);

      JLabel lblXformScript_7 = new JLabel();
      lblXformScript_7.setText("09");
      lblXformScript_7.setPreferredSize(new Dimension(94, 22));
      lblXformScript_7.setName("lblXformScript_7");
      lblXformScript_7.setFont(new Font("Dialog", Font.BOLD, 10));
      lblXformScript_7.setBounds(0, 184, 21, 22);
      panel_86.add(lblXformScript_7);

      swfAnimatorXFormScript9Cmb = new JComboBox();
      swfAnimatorXFormScript9Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript9Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript9Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript9Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript9Cmb.setBounds(22, 184, 186, 24);
      panel_86.add(swfAnimatorXFormScript9Cmb);

      swfAnimatorXFormScript9REd = new JWFNumberField();
      swfAnimatorXFormScript9REd.setWithMotionCurve(true);
      swfAnimatorXFormScript9REd.setValueStep(0.1);
      swfAnimatorXFormScript9REd.setValue(1.0);
      swfAnimatorXFormScript9REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript9REd.setMotionPropertyName("xFormScript9");
      swfAnimatorXFormScript9REd.setLinkedLabelControlName("lblXformScript_7");
      swfAnimatorXFormScript9REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript9REd.setBounds(213, 184, 100, 24);
      swfAnimatorXFormScript9REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript9REd());
        }
      });
      swfAnimatorXFormScript9REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript9REd);

      JLabel lblXformScript_8 = new JLabel();
      lblXformScript_8.setText("10");
      lblXformScript_8.setPreferredSize(new Dimension(94, 22));
      lblXformScript_8.setName("lblXformScript_8");
      lblXformScript_8.setFont(new Font("Dialog", Font.BOLD, 10));
      lblXformScript_8.setBounds(0, 207, 21, 22);
      panel_86.add(lblXformScript_8);

      swfAnimatorXFormScript10Cmb = new JComboBox();
      swfAnimatorXFormScript10Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript10Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript10Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript10Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript10Cmb.setBounds(22, 207, 186, 24);
      panel_86.add(swfAnimatorXFormScript10Cmb);

      swfAnimatorXFormScript10REd = new JWFNumberField();
      swfAnimatorXFormScript10REd.setWithMotionCurve(true);
      swfAnimatorXFormScript10REd.setValueStep(0.1);
      swfAnimatorXFormScript10REd.setValue(1.0);
      swfAnimatorXFormScript10REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript10REd.setMotionPropertyName("xFormScript10");
      swfAnimatorXFormScript10REd.setLinkedLabelControlName("lblXformScript_8");
      swfAnimatorXFormScript10REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript10REd.setBounds(213, 207, 100, 24);
      swfAnimatorXFormScript10REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript10REd());
        }
      });
      swfAnimatorXFormScript10REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript10REd);

      JLabel lblXformScript_9 = new JLabel();
      lblXformScript_9.setText("11");
      lblXformScript_9.setPreferredSize(new Dimension(94, 22));
      lblXformScript_9.setName("lblXformScript_9");
      lblXformScript_9.setFont(new Font("Dialog", Font.BOLD, 10));
      lblXformScript_9.setBounds(0, 230, 21, 22);
      panel_86.add(lblXformScript_9);

      swfAnimatorXFormScript11Cmb = new JComboBox();
      swfAnimatorXFormScript11Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript11Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript11Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript11Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript11Cmb.setBounds(22, 230, 186, 24);
      panel_86.add(swfAnimatorXFormScript11Cmb);

      swfAnimatorXFormScript11REd = new JWFNumberField();
      swfAnimatorXFormScript11REd.setWithMotionCurve(true);
      swfAnimatorXFormScript11REd.setValueStep(0.1);
      swfAnimatorXFormScript11REd.setValue(1.0);
      swfAnimatorXFormScript11REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript11REd.setMotionPropertyName("xFormScript11");
      swfAnimatorXFormScript11REd.setLinkedLabelControlName("lblXformScript_9");
      swfAnimatorXFormScript11REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript11REd.setBounds(213, 230, 100, 24);
      swfAnimatorXFormScript11REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript11REd());
        }
      });
      swfAnimatorXFormScript11REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript11REd);

      JLabel lblXformScript_10 = new JLabel();
      lblXformScript_10.setText("12");
      lblXformScript_10.setPreferredSize(new Dimension(94, 22));
      lblXformScript_10.setName("lblXformScript_10");
      lblXformScript_10.setFont(new Font("Dialog", Font.BOLD, 10));
      lblXformScript_10.setBounds(0, 253, 21, 22);
      panel_86.add(lblXformScript_10);

      swfAnimatorXFormScript12Cmb = new JComboBox();
      swfAnimatorXFormScript12Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript12Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript12Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript12Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript12Cmb.setBounds(22, 253, 186, 24);
      panel_86.add(swfAnimatorXFormScript12Cmb);

      swfAnimatorXFormScript12REd = new JWFNumberField();
      swfAnimatorXFormScript12REd.setWithMotionCurve(true);
      swfAnimatorXFormScript12REd.setValueStep(0.1);
      swfAnimatorXFormScript12REd.setValue(1.0);
      swfAnimatorXFormScript12REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript12REd.setMotionPropertyName("xFormScript12");
      swfAnimatorXFormScript12REd.setLinkedLabelControlName("lblXformScript_10");
      swfAnimatorXFormScript12REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript12REd.setBounds(213, 253, 100, 24);
      swfAnimatorXFormScript12REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript12REd());
        }
      });
      swfAnimatorXFormScript12REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript12REd);
      swfAnimatorXFormScript4REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript4REd());
        }
      });
      swfAnimatorXFormScript4REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript5REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript5REd());
        }
      });
      swfAnimatorXFormScript5REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript5Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript4Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript3REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript3REd());
        }
      });
      swfAnimatorXFormScript3REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript3Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript2REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript2REd());
        }
      });
      swfAnimatorXFormScript2REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript2Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript1REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript1REd());
        }
      });
      swfAnimatorXFormScript1REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
    }
    return panel_86;
  }

  private JPanel getPanel_87() {
    if (panel_87 == null) {
      panel_87 = new JPanel();
      panel_87.setMaximumSize(new Dimension(32767, 120));
      panel_87.setBorder(new TitledBorder(null, "Speed / Motion blur", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_panel_87 = new GridBagLayout();
      gbl_panel_87.columnWidths = new int[] { 0, 0, 0, 0 };
      gbl_panel_87.rowHeights = new int[] { 0, 0 };
      gbl_panel_87.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
      gbl_panel_87.rowWeights = new double[] { 0.0, 0.0 };
      panel_87.setLayout(gbl_panel_87);
      animateFramesLbl = new JLabel();
      GridBagConstraints gbc_animateFramesLbl = new GridBagConstraints();
      gbc_animateFramesLbl.fill = GridBagConstraints.HORIZONTAL;
      gbc_animateFramesLbl.insets = new Insets(0, 0, 5, 5);
      gbc_animateFramesLbl.gridx = 0;
      gbc_animateFramesLbl.gridy = 0;
      panel_87.add(animateFramesLbl, gbc_animateFramesLbl);
      animateFramesLbl.setHorizontalAlignment(SwingConstants.LEFT);
      animateFramesLbl.setPreferredSize(new Dimension(94, 22));
      animateFramesLbl.setText("Total frame count");
      animateFramesLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      GridBagConstraints gbc_swfAnimatorFramesREd = new GridBagConstraints();
      gbc_swfAnimatorFramesREd.insets = new Insets(0, 0, 5, 5);
      gbc_swfAnimatorFramesREd.gridx = 1;
      gbc_swfAnimatorFramesREd.gridy = 0;
      panel_87.add(getSwfAnimatorFramesREd(), gbc_swfAnimatorFramesREd);

      JLabel lblFramesPerSecond = new JLabel();
      GridBagConstraints gbc_lblFramesPerSecond = new GridBagConstraints();
      gbc_lblFramesPerSecond.fill = GridBagConstraints.HORIZONTAL;
      gbc_lblFramesPerSecond.insets = new Insets(0, 0, 5, 5);
      gbc_lblFramesPerSecond.gridx = 2;
      gbc_lblFramesPerSecond.gridy = 0;
      panel_87.add(lblFramesPerSecond, gbc_lblFramesPerSecond);
      lblFramesPerSecond.setHorizontalAlignment(SwingConstants.LEFT);
      lblFramesPerSecond.setText("Frames per second");
      lblFramesPerSecond.setPreferredSize(new Dimension(110, 22));
      lblFramesPerSecond.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorFramesPerSecondREd = new JWFNumberField();
      GridBagConstraints gbc_swfAnimatorFramesPerSecondREd = new GridBagConstraints();
      gbc_swfAnimatorFramesPerSecondREd.insets = new Insets(0, 0, 5, 0);
      gbc_swfAnimatorFramesPerSecondREd.gridx = 3;
      gbc_swfAnimatorFramesPerSecondREd.gridy = 0;
      panel_87.add(swfAnimatorFramesPerSecondREd, gbc_swfAnimatorFramesPerSecondREd);
      swfAnimatorFramesPerSecondREd.setMaxValue(120.0);
      swfAnimatorFramesPerSecondREd.setMinValue(1.0);
      swfAnimatorFramesPerSecondREd.setHasMaxValue(true);
      swfAnimatorFramesPerSecondREd.setHasMinValue(true);
      swfAnimatorFramesPerSecondREd.setEditable(true);
      swfAnimatorFramesPerSecondREd.setText("12");
      swfAnimatorFramesPerSecondREd.setPreferredSize(new Dimension(64, 24));
      swfAnimatorFramesPerSecondREd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblMotionBlurLength = new JLabel();
      GridBagConstraints gbc_lblMotionBlurLength = new GridBagConstraints();
      gbc_lblMotionBlurLength.fill = GridBagConstraints.HORIZONTAL;
      gbc_lblMotionBlurLength.insets = new Insets(0, 0, 0, 5);
      gbc_lblMotionBlurLength.gridx = 0;
      gbc_lblMotionBlurLength.gridy = 1;
      panel_87.add(lblMotionBlurLength, gbc_lblMotionBlurLength);
      lblMotionBlurLength.setText("Motion blur length");
      lblMotionBlurLength.setPreferredSize(new Dimension(94, 22));
      lblMotionBlurLength.setHorizontalAlignment(SwingConstants.LEFT);
      lblMotionBlurLength.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorMotionBlurLengthREd = new JWFNumberField();
      swfAnimatorMotionBlurLengthREd.setMinimumSize(new Dimension(40, 28));
      GridBagConstraints gbc_swfAnimatorMotionBlurLengthREd = new GridBagConstraints();
      gbc_swfAnimatorMotionBlurLengthREd.insets = new Insets(0, 0, 0, 5);
      gbc_swfAnimatorMotionBlurLengthREd.gridx = 1;
      gbc_swfAnimatorMotionBlurLengthREd.gridy = 1;
      panel_87.add(swfAnimatorMotionBlurLengthREd, gbc_swfAnimatorMotionBlurLengthREd);
      swfAnimatorMotionBlurLengthREd.setValueStep(1.0);
      swfAnimatorMotionBlurLengthREd.setOnlyIntegers(true);
      swfAnimatorMotionBlurLengthREd.setText("12");
      swfAnimatorMotionBlurLengthREd.setPreferredSize(new Dimension(64, 24));
      swfAnimatorMotionBlurLengthREd.setMaxValue(120.0);
      swfAnimatorMotionBlurLengthREd.setHasMinValue(true);
      swfAnimatorMotionBlurLengthREd.setHasMaxValue(true);
      swfAnimatorMotionBlurLengthREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorMotionBlurLengthREd.setEditable(true);

      JLabel lblMotionBlurTimestep = new JLabel();
      GridBagConstraints gbc_lblMotionBlurTimestep = new GridBagConstraints();
      gbc_lblMotionBlurTimestep.fill = GridBagConstraints.HORIZONTAL;
      gbc_lblMotionBlurTimestep.insets = new Insets(0, 0, 0, 5);
      gbc_lblMotionBlurTimestep.gridx = 2;
      gbc_lblMotionBlurTimestep.gridy = 1;
      panel_87.add(lblMotionBlurTimestep, gbc_lblMotionBlurTimestep);
      lblMotionBlurTimestep.setText("Motion blur timestep");
      lblMotionBlurTimestep.setPreferredSize(new Dimension(110, 22));
      lblMotionBlurTimestep.setHorizontalAlignment(SwingConstants.LEFT);
      lblMotionBlurTimestep.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorMotionBlurTimeStepREd = new JWFNumberField();
      GridBagConstraints gbc_swfAnimatorMotionBlurTimeStepREd = new GridBagConstraints();
      gbc_swfAnimatorMotionBlurTimeStepREd.gridx = 3;
      gbc_swfAnimatorMotionBlurTimeStepREd.gridy = 1;
      panel_87.add(swfAnimatorMotionBlurTimeStepREd, gbc_swfAnimatorMotionBlurTimeStepREd);
      swfAnimatorMotionBlurTimeStepREd.setValueStep(0.1);
      swfAnimatorMotionBlurTimeStepREd.setText("0.15");
      swfAnimatorMotionBlurTimeStepREd.setPreferredSize(new Dimension(64, 24));
      swfAnimatorMotionBlurTimeStepREd.setMaxValue(1.0);
      swfAnimatorMotionBlurTimeStepREd.setHasMinValue(true);
      swfAnimatorMotionBlurTimeStepREd.setHasMaxValue(true);
      swfAnimatorMotionBlurTimeStepREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorMotionBlurTimeStepREd.setEditable(true);
      swfAnimatorMotionBlurTimeStepREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorMotionBlurLengthREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
    }
    return panel_87;
  }

  public JComboBox getSwfAnimatorXFormScript6Cmb() {
    return swfAnimatorXFormScript6Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript7Cmb() {
    return swfAnimatorXFormScript7Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript8Cmb() {
    return swfAnimatorXFormScript8Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript9Cmb() {
    return swfAnimatorXFormScript9Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript10Cmb() {
    return swfAnimatorXFormScript10Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript11Cmb() {
    return swfAnimatorXFormScript11Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript12Cmb() {
    return swfAnimatorXFormScript12Cmb;
  }

  public JWFNumberField getSwfAnimatorXFormScript6REd() {
    return swfAnimatorXFormScript6REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript7REd() {
    return swfAnimatorXFormScript7REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript8REd() {
    return swfAnimatorXFormScript8REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript9REd() {
    return swfAnimatorXFormScript9REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript10REd() {
    return swfAnimatorXFormScript10REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript11REd() {
    return swfAnimatorXFormScript11REd;
  }

  public JWFNumberField getSwfAnimatorXFormScript12REd() {
    return swfAnimatorXFormScript12REd;
  }

  public JLabel getLblGlobalScript_4() {
    return lblGlobalScript_4;
  }

  public JLabel getLblGlobalScript_5() {
    return lblGlobalScript_5;
  }

  public JLabel getLblGlobalScript_6() {
    return lblGlobalScript_6;
  }

  public JLabel getLblGlobalScript_7() {
    return lblGlobalScript_7;
  }

  public JLabel getLblGlobalScript_8() {
    return lblGlobalScript_8;
  }

  public JComboBox getSwfAnimatorGlobalScript6Cmb() {
    return swfAnimatorGlobalScript6Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript7Cmb() {
    return swfAnimatorGlobalScript7Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript8Cmb() {
    return swfAnimatorGlobalScript8Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript9Cmb() {
    return swfAnimatorGlobalScript9Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript10Cmb() {
    return swfAnimatorGlobalScript10Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript11Cmb() {
    return swfAnimatorGlobalScript11Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript12Cmb() {
    return swfAnimatorGlobalScript12Cmb;
  }

  public JWFNumberField getSwfAnimatorGlobalScript6REd() {
    return swfAnimatorGlobalScript6REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript7REd() {
    return swfAnimatorGlobalScript7REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript8REd() {
    return swfAnimatorGlobalScript8REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript9REd() {
    return swfAnimatorGlobalScript9REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript10REd() {
    return swfAnimatorGlobalScript10REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript11REd() {
    return swfAnimatorGlobalScript11REd;
  }

  public JWFNumberField getSwfAnimatorGlobalScript12REd() {
    return swfAnimatorGlobalScript12REd;
  }

  private JButton getBtnRender() {
    if (btnRender == null) {
      btnRender = new JButton();
      btnRender.setIconTextGap(2);
      btnRender.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().renderFlameImage();
        }
      });
      btnRender.setToolTipText("Render the current frame in higher qualiry");
      btnRender.setText("Render");
      btnRender.setPreferredSize(new Dimension(82, 28));
      btnRender.setMnemonic(KeyEvent.VK_R);
      btnRender.setFont(new Font("Dialog", Font.BOLD, 9));
      btnRender.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive.png")));
    }
    return btnRender;
  }

  private JPanel getPanel_12() {
    if (panel_12 == null) {
      panel_12 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_12.getLayout();
      flowLayout.setVgap(1);
      panel_12.setPreferredSize(new Dimension(10, 24));

      JLabel lblFrame = new JLabel();
      panel_12.add(lblFrame);
      lblFrame.setHorizontalAlignment(SwingConstants.RIGHT);
      lblFrame.setText("Frame");
      lblFrame.setPreferredSize(new Dimension(38, 22));
      lblFrame.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorFrameREd = new JWFNumberField();
      panel_12.add(swfAnimatorFrameREd);
      swfAnimatorFrameREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            try {
              swfAnimatorFrameSlider.setValue(swfAnimatorFrameREd.getIntValue());
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      swfAnimatorFrameREd.setMaxValue(120.0);
      swfAnimatorFrameREd.setMinValue(1.0);
      swfAnimatorFrameREd.setHasMaxValue(true);
      swfAnimatorFrameREd.setHasMinValue(true);
      swfAnimatorFrameREd.setOnlyIntegers(true);
      swfAnimatorFrameREd.setText("60");
      swfAnimatorFrameREd.setPreferredSize(new Dimension(56, 22));
      swfAnimatorFrameREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return panel_12;
  }

  public JWFNumberField getTinaCameraCamPosXREd() {
    return tinaCameraCamPosXREd;
  }

  public JWFNumberField getTinaCameraCamPosYREd() {
    return tinaCameraCamPosYREd;
  }

  public JWFNumberField getTinaCameraCamPosZREd() {
    return tinaCameraCamPosZREd;
  }

  public JSlider getTinaCameraCamPosXSlider() {
    return tinaCameraCamPosXSlider;
  }

  public JSlider getTinaCameraCamPosYSlider() {
    return tinaCameraCamPosYSlider;
  }

  public JSlider getTinaCameraCamPosZSlider() {
    return tinaCameraCamPosZSlider;
  }

  public JSlider getTinaSaturationSlider() {
    return tinaSaturationSlider;
  }

  public JWFNumberField getTinaSaturationREd() {
    return tinaSaturationREd;
  }

  public JSlider getEditorFractalBrightnessSlider() {
    return editorFractalBrightnessSlider;
  }

  public JToggleButton getToggleDrawGridButton() {
    return toggleDrawGridButton;
  }

  public JToggleButton getMouseTransformEditTriangleViewButton() {
    return mouseTransformEditTriangleViewButton;
  }

  public JComboBox getRandomGradientCmb() {
    return randomGradientCmb;
  }

  public JComboBox getTinaPaletteRandomGeneratorCmb() {
    return tinaPaletteRandomGeneratorCmb;
  }

  public JToggleButton getToggleTriangleWithColorsButton() {
    return toggleTriangleWithColorsButton;
  }

  private JButton getFlameBrowserCopyToBtn() {
    if (flameBrowserCopyToBtn == null) {
      flameBrowserCopyToBtn = new JButton();
      flameBrowserCopyToBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().copyToBtnClicked();
        }
      });
      flameBrowserCopyToBtn.setToolTipText("Copy the currently selected flame into another directory");
      flameBrowserCopyToBtn.setText("Copy to...");
      flameBrowserCopyToBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserCopyToBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserCopyToBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserCopyToBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserCopyToBtn;
  }

  private JButton getFlameBrowserMoveToBtn() {
    if (flameBrowserMoveToBtn == null) {
      flameBrowserMoveToBtn = new JButton();
      flameBrowserMoveToBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().moveToBtnClicked();
        }
      });
      flameBrowserMoveToBtn.setToolTipText("Move the currently selected flame to another directory");
      flameBrowserMoveToBtn.setText("Move to...");
      flameBrowserMoveToBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserMoveToBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserMoveToBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserMoveToBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserMoveToBtn;
  }

  private JButton getAffineScaleEditMotionCurveBtn() {
    if (affineScaleEditMotionCurveBtn == null) {
      affineScaleEditMotionCurveBtn = new JButton();
      affineScaleEditMotionCurveBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editScaleMotionCurve(e);
        }
      });
      affineScaleEditMotionCurveBtn.setToolTipText("Create/edit a motion curve");
      affineScaleEditMotionCurveBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/curve-money2.png")));
      affineScaleEditMotionCurveBtn.setText("");
      affineScaleEditMotionCurveBtn.setSize(new Dimension(70, 24));
      affineScaleEditMotionCurveBtn.setPreferredSize(new Dimension(55, 24));
      affineScaleEditMotionCurveBtn.setLocation(new Point(0, 57));
      affineScaleEditMotionCurveBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      affineScaleEditMotionCurveBtn.setBounds(92, 57, 22, 24);
    }
    return affineScaleEditMotionCurveBtn;
  }

  public JButton getAffineRotateEditMotionCurveBtn() {
    return affineRotateEditMotionCurveBtn;
  }

  public JComboBox getTriangleStyleCmb() {
    return triangleStyleCmb;
  }

  private JPanel getPanel_17() {
    if (panel_17 == null) {
      panel_17 = new JPanel();
      panel_17.setMinimumSize(new Dimension(110, 10));
      panel_17.setMaximumSize(new Dimension(150, 32767));
      panel_17.setLayout(new BoxLayout(panel_17, BoxLayout.Y_AXIS));

      interactiveResumeButton = new JButton();
      panel_17.add(interactiveResumeButton);
      interactiveResumeButton.setMinimumSize(new Dimension(100, 24));
      interactiveResumeButton.setMaximumSize(new Dimension(150, 24));
      interactiveResumeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().resumeBtn_clicked();
        }
      });
      interactiveResumeButton.setToolTipText("Resume a previously saved render");
      interactiveResumeButton.setText("Resume render");
      interactiveResumeButton.setPreferredSize(new Dimension(125, 24));
      interactiveResumeButton.setMnemonic(KeyEvent.VK_T);
      interactiveResumeButton.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_17.add(getPanel_18());
      panel_17.add(getInteractiveStopButton());
    }
    return panel_17;
  }

  private JPanel getPanel_18() {
    if (panel_18 == null) {
      panel_18 = new JPanel();
      panel_18.setMaximumSize(new Dimension(32767, 24));
    }
    return panel_18;
  }

  private JPanel getPanel_19() {
    if (panel_19 == null) {
      panel_19 = new JPanel();
      panel_19.setBorder(new EmptyBorder(0, 0, 0, 0));
      panel_19.setPreferredSize(new Dimension(90, 24));
      FlowLayout flowLayout = (FlowLayout) panel_19.getLayout();
      flowLayout.setVgap(0);
      flowLayout.setHgap(0);
      flowLayout.setAlignment(FlowLayout.LEFT);
      panel_19.add(getTinaDuplicateTransformationButton());

      editTransformCaptionBtn = new JButton();
      panel_19.add(editTransformCaptionBtn);
      editTransformCaptionBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.editTransformCaptionBtn_clicked();
        }
      });
      editTransformCaptionBtn.setToolTipText("Edit the name of the current transform");
      editTransformCaptionBtn.setText("T");
      editTransformCaptionBtn.setPreferredSize(new Dimension(34, 24));
      editTransformCaptionBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return panel_19;
  }

  private JPanel getPanel_88() {
    if (panel_88 == null) {
      panel_88 = new JPanel();
      panel_88.setLayout(new BorderLayout(0, 0));
      panel_88.add(getTabbedPane_2(), BorderLayout.CENTER);

      panel_93 = new JPanel();
      panel_93.setPreferredSize(new Dimension(10, 260));
      panel_95.add(panel_93, BorderLayout.SOUTH);
      panel_93.setLayout(new BorderLayout(0, 0));
      panel_93.add(getPanel_91(), BorderLayout.NORTH);

      panel_94 = new JPanel();
      panel_95.add(panel_94, BorderLayout.CENTER);
      panel_94.setLayout(new GridLayout(0, 3, 0, 0));

      JPanel panel_3 = new JPanel();
      panel_3.setBorder(new TitledBorder(null, "Top view", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_94.add(panel_3);
      panel_3.setLayout(new BorderLayout(0, 0));
      panel_3.add(getMeshGenTopViewRootPnl(), BorderLayout.CENTER);
      panel_3.add(getPanel_96(), BorderLayout.EAST);

      JPanel panel_8 = new JPanel();
      panel_8.setBorder(new TitledBorder(null, "Front view", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_94.add(panel_8);
      panel_8.setLayout(new BorderLayout(0, 0));
      panel_8.add(getMeshGenFrontViewRootPnl(), BorderLayout.CENTER);

      JPanel panel_10 = new JPanel();
      panel_10.setLayout(null);
      panel_10.setPreferredSize(new Dimension(52, 10));
      panel_8.add(panel_10, BorderLayout.EAST);

      meshGenFrontViewRenderBtn = new JButton();
      meshGenFrontViewRenderBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().frontViewRenderButtonClicked();
        }
      });
      meshGenFrontViewRenderBtn.setToolTipText("Render image");
      meshGenFrontViewRenderBtn.setPreferredSize(new Dimension(42, 24));
      meshGenFrontViewRenderBtn.setMnemonic(KeyEvent.VK_R);
      meshGenFrontViewRenderBtn.setIconTextGap(0);
      meshGenFrontViewRenderBtn.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive.png")));
      meshGenFrontViewRenderBtn.setFont(new Font("Dialog", Font.BOLD, 9));
      meshGenFrontViewRenderBtn.setBounds(6, 99, 42, 24);
      panel_10.add(meshGenFrontViewRenderBtn);

      JPanel panel_9 = new JPanel();
      panel_9.setBorder(new TitledBorder(null, "Perspective view", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_94.add(panel_9);
      panel_9.setLayout(new BorderLayout(0, 0));
      panel_9.add(getMeshGenPerspectiveViewRootPnl(), BorderLayout.CENTER);

      JPanel panel_11 = new JPanel();
      panel_11.setLayout(null);
      panel_11.setPreferredSize(new Dimension(52, 10));
      panel_9.add(panel_11, BorderLayout.EAST);

      meshGenPerspectiveViewRenderBtn = new JButton();
      meshGenPerspectiveViewRenderBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().perspectiveViewRenderButtonClicked();
        }
      });
      meshGenPerspectiveViewRenderBtn.setToolTipText("Render image");
      meshGenPerspectiveViewRenderBtn.setPreferredSize(new Dimension(42, 24));
      meshGenPerspectiveViewRenderBtn.setMnemonic(KeyEvent.VK_R);
      meshGenPerspectiveViewRenderBtn.setIconTextGap(0);
      meshGenPerspectiveViewRenderBtn.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive.png")));
      meshGenPerspectiveViewRenderBtn.setFont(new Font("Dialog", Font.BOLD, 9));
      meshGenPerspectiveViewRenderBtn.setBounds(6, 99, 42, 24);
      panel_11.add(meshGenPerspectiveViewRenderBtn);
    }
    return panel_88;
  }

  public JWFNumberField getXFormModGammaREd() {
    return xFormModGammaREd;
  }

  public JSlider getXFormModGammaSlider() {
    return xFormModGammaSlider;
  }

  public JWFNumberField getXFormModGammaSpeedREd() {
    return xFormModGammaSpeedREd;
  }

  public JSlider getXFormModGammaSpeedSlider() {
    return xFormModGammaSpeedSlider;
  }

  public JWFNumberField getXFormModContrastREd() {
    return xFormModContrastREd;
  }

  public JWFNumberField getXFormModContrastSpeedREd() {
    return xFormModContrastSpeedREd;
  }

  public JWFNumberField getXFormModSaturationREd() {
    return xFormModSaturationREd;
  }

  public JWFNumberField getXFormModSaturationSpeedREd() {
    return xFormModSaturationSpeedREd;
  }

  public JSlider getXFormModContrastSlider() {
    return xFormModContrastSlider;
  }

  public JSlider getXFormModContrastSpeedSlider() {
    return xFormModContrastSpeedSlider;
  }

  public JSlider getXFormModSaturationSlider() {
    return xFormModSaturationSlider;
  }

  public JSlider getXFormModSaturationSpeedSlider() {
    return xFormModSaturationSpeedSlider;
  }

  public JCheckBox getXFormModGammaWholeFractalCBx() {
    return xFormModGammaWholeFractalCBx;
  }

  public JButton getXFormModGammaResetBtn() {
    return xFormModGammaResetBtn;
  }

  public JButton getXFormModGammaRandomizeBtn() {
    return xFormModGammaRandomizeBtn;
  }

  private JPanel getPanel_89() {
    if (panel_89 == null) {
      panel_89 = new JPanel();
      panel_89.setPreferredSize(new Dimension(10, 112));
      panel_89.setLayout(new BorderLayout(0, 0));
      panel_89.add(getPanel_90(), BorderLayout.WEST);

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new TitledBorder(null, "Slice generation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_89.add(panel_1, BorderLayout.CENTER);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(10, 20));
      panel_1.add(panel_2, BorderLayout.SOUTH);
      panel_2.setLayout(new BorderLayout(0, 0));

      meshGenProgressbar = new JProgressBar();
      meshGenProgressbar.setValue(0);
      meshGenProgressbar.setStringPainted(true);
      meshGenProgressbar.setPreferredSize(new Dimension(568, 21));
      panel_2.add(meshGenProgressbar, BorderLayout.CENTER);

      JPanel panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(810, 10));
      panel_1.add(panel_3, BorderLayout.WEST);
      panel_3.setLayout(null);

      JLabel lblRenderWidth = new JLabel();
      lblRenderWidth.setText("Render width");
      lblRenderWidth.setSize(new Dimension(68, 22));
      lblRenderWidth.setPreferredSize(new Dimension(94, 22));
      lblRenderWidth.setName("");
      lblRenderWidth.setLocation(new Point(4, 76));
      lblRenderWidth.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRenderWidth.setBounds(254, 2, 79, 22);
      panel_3.add(lblRenderWidth);

      meshGenRenderWidthREd = new JWFNumberField();
      meshGenRenderWidthREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().renderWidth_changed();
          }
        }
      });
      meshGenRenderWidthREd.setMinValue(16.0);
      meshGenRenderWidthREd.setOnlyIntegers(true);
      meshGenRenderWidthREd.setValueStep(1.0);
      meshGenRenderWidthREd.setText("");
      meshGenRenderWidthREd.setSize(new Dimension(100, 24));
      meshGenRenderWidthREd.setPreferredSize(new Dimension(100, 24));
      meshGenRenderWidthREd.setMaxValue(4096.0);
      meshGenRenderWidthREd.setLocation(new Point(71, 76));
      meshGenRenderWidthREd.setHasMinValue(true);
      meshGenRenderWidthREd.setHasMaxValue(true);
      meshGenRenderWidthREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenRenderWidthREd.setBounds(330, 0, 100, 24);
      panel_3.add(meshGenRenderWidthREd);

      JLabel lblRenderHeight = new JLabel();
      lblRenderHeight.setText("Render height");
      lblRenderHeight.setSize(new Dimension(68, 22));
      lblRenderHeight.setPreferredSize(new Dimension(94, 22));
      lblRenderHeight.setName("");
      lblRenderHeight.setLocation(new Point(4, 76));
      lblRenderHeight.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRenderHeight.setBounds(254, 26, 79, 22);
      panel_3.add(lblRenderHeight);

      meshGenRenderHeightREd = new JWFNumberField();
      meshGenRenderHeightREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().renderHeight_changed();
          }
        }
      });
      meshGenRenderHeightREd.setMinValue(16.0);
      meshGenRenderHeightREd.setOnlyIntegers(true);
      meshGenRenderHeightREd.setValueStep(1.0);
      meshGenRenderHeightREd.setText("");
      meshGenRenderHeightREd.setSize(new Dimension(100, 24));
      meshGenRenderHeightREd.setPreferredSize(new Dimension(100, 24));
      meshGenRenderHeightREd.setMaxValue(4096.0);
      meshGenRenderHeightREd.setLocation(new Point(71, 76));
      meshGenRenderHeightREd.setHasMinValue(true);
      meshGenRenderHeightREd.setHasMaxValue(true);
      meshGenRenderHeightREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenRenderHeightREd.setBounds(330, 24, 100, 24);
      panel_3.add(meshGenRenderHeightREd);

      meshGenSliceCountREd = new JWFNumberField();
      meshGenSliceCountREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().sliceCount_changed();
          }
        }
      });
      meshGenSliceCountREd.setHasMaxValue(true);
      meshGenSliceCountREd.setMinValue(2.0);
      meshGenSliceCountREd.setOnlyIntegers(true);
      meshGenSliceCountREd.setValueStep(1.0);
      meshGenSliceCountREd.setText("");
      meshGenSliceCountREd.setSize(new Dimension(100, 24));
      meshGenSliceCountREd.setPreferredSize(new Dimension(100, 24));
      meshGenSliceCountREd.setMaxValue(4096.0);
      meshGenSliceCountREd.setLocation(new Point(71, 76));
      meshGenSliceCountREd.setHasMinValue(true);
      meshGenSliceCountREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSliceCountREd.setBounds(150, 0, 100, 24);
      panel_3.add(meshGenSliceCountREd);

      JLabel lblNumberOfSlices = new JLabel();
      lblNumberOfSlices.setText("Total number of slices");
      lblNumberOfSlices.setSize(new Dimension(68, 22));
      lblNumberOfSlices.setPreferredSize(new Dimension(94, 22));
      lblNumberOfSlices.setName("");
      lblNumberOfSlices.setLocation(new Point(6, 76));
      lblNumberOfSlices.setFont(new Font("Dialog", Font.BOLD, 10));
      lblNumberOfSlices.setBounds(2, 2, 151, 22);
      panel_3.add(lblNumberOfSlices);

      JLabel lblSlicesPerPass = new JLabel();
      lblSlicesPerPass.setText("Slices per pass (for speedup)");
      lblSlicesPerPass.setSize(new Dimension(68, 22));
      lblSlicesPerPass.setPreferredSize(new Dimension(94, 22));
      lblSlicesPerPass.setName("");
      lblSlicesPerPass.setLocation(new Point(4, 76));
      lblSlicesPerPass.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSlicesPerPass.setBounds(2, 26, 151, 22);
      panel_3.add(lblSlicesPerPass);

      meshGenSlicesPerRenderREd = new JWFNumberField();
      meshGenSlicesPerRenderREd.setMinValue(1.0);
      meshGenSlicesPerRenderREd.setOnlyIntegers(true);
      meshGenSlicesPerRenderREd.setValueStep(1.0);
      meshGenSlicesPerRenderREd.setText("");
      meshGenSlicesPerRenderREd.setSize(new Dimension(100, 24));
      meshGenSlicesPerRenderREd.setPreferredSize(new Dimension(100, 24));
      meshGenSlicesPerRenderREd.setMaxValue(1.0);
      meshGenSlicesPerRenderREd.setLocation(new Point(71, 76));
      meshGenSlicesPerRenderREd.setHasMinValue(true);
      meshGenSlicesPerRenderREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSlicesPerRenderREd.setBounds(150, 24, 100, 24);
      panel_3.add(meshGenSlicesPerRenderREd);

      meshGenRenderQualityREd = new JWFNumberField();
      meshGenRenderQualityREd.setMinValue(10.0);
      meshGenRenderQualityREd.setOnlyIntegers(true);
      meshGenRenderQualityREd.setValueStep(1.0);
      meshGenRenderQualityREd.setText("");
      meshGenRenderQualityREd.setSize(new Dimension(100, 24));
      meshGenRenderQualityREd.setPreferredSize(new Dimension(100, 24));
      meshGenRenderQualityREd.setLocation(new Point(71, 76));
      meshGenRenderQualityREd.setHasMinValue(true);
      meshGenRenderQualityREd.setHasMaxValue(false);
      meshGenRenderQualityREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenRenderQualityREd.setBounds(510, 2, 100, 24);
      panel_3.add(meshGenRenderQualityREd);

      JLabel lblRenderQuality = new JLabel();
      lblRenderQuality.setText("Render quality");
      lblRenderQuality.setSize(new Dimension(68, 22));
      lblRenderQuality.setPreferredSize(new Dimension(94, 22));
      lblRenderQuality.setName("");
      lblRenderQuality.setLocation(new Point(4, 76));
      lblRenderQuality.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRenderQuality.setBounds(434, 4, 79, 22);
      panel_3.add(lblRenderQuality);

      meshGenSliceThicknessModREd = new JWFNumberField();
      meshGenSliceThicknessModREd.setValueStep(0.01);
      meshGenSliceThicknessModREd.setText("");
      meshGenSliceThicknessModREd.setSize(new Dimension(100, 24));
      meshGenSliceThicknessModREd.setPreferredSize(new Dimension(100, 24));
      meshGenSliceThicknessModREd.setMaxValue(1.0);
      meshGenSliceThicknessModREd.setLocation(new Point(71, 76));
      meshGenSliceThicknessModREd.setHasMinValue(true);
      meshGenSliceThicknessModREd.setHasMaxValue(true);
      meshGenSliceThicknessModREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSliceThicknessModREd.setBounds(710, 2, 100, 24);
      panel_3.add(meshGenSliceThicknessModREd);

      JLabel lblSliceThickness = new JLabel();
      lblSliceThickness.setToolTipText("Thickness modifier of the generated model");
      lblSliceThickness.setText("Thickness mod");
      lblSliceThickness.setSize(new Dimension(68, 22));
      lblSliceThickness.setPreferredSize(new Dimension(94, 22));
      lblSliceThickness.setName("");
      lblSliceThickness.setLocation(new Point(4, 76));
      lblSliceThickness.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSliceThickness.setBounds(613, 4, 100, 22);
      panel_3.add(lblSliceThickness);

      meshGenSliceThicknessSamplesREd = new JWFNumberField();
      meshGenSliceThicknessSamplesREd.setValueStep(1.0);
      meshGenSliceThicknessSamplesREd.setText("");
      meshGenSliceThicknessSamplesREd.setSize(new Dimension(100, 24));
      meshGenSliceThicknessSamplesREd.setPreferredSize(new Dimension(100, 24));
      meshGenSliceThicknessSamplesREd.setOnlyIntegers(true);
      meshGenSliceThicknessSamplesREd.setMaxValue(200.0);
      meshGenSliceThicknessSamplesREd.setLocation(new Point(71, 76));
      meshGenSliceThicknessSamplesREd.setHasMinValue(true);
      meshGenSliceThicknessSamplesREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSliceThicknessSamplesREd.setBounds(710, 24, 100, 24);
      panel_3.add(meshGenSliceThicknessSamplesREd);

      JLabel lblThicknessSamples = new JLabel();
      lblThicknessSamples.setToolTipText("Number of samples for calculating thickness mod");
      lblThicknessSamples.setText("Thickness samples");
      lblThicknessSamples.setSize(new Dimension(68, 22));
      lblThicknessSamples.setPreferredSize(new Dimension(94, 22));
      lblThicknessSamples.setName("");
      lblThicknessSamples.setLocation(new Point(4, 76));
      lblThicknessSamples.setFont(new Font("Dialog", Font.BOLD, 10));
      lblThicknessSamples.setBounds(613, 26, 100, 22);
      panel_3.add(lblThicknessSamples);

      JPanel panel_8 = new JPanel();
      panel_8.setPreferredSize(new Dimension(132, 10));
      panel_1.add(panel_8, BorderLayout.EAST);

      meshGenGenerateBtn = new JButton();
      meshGenGenerateBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().generateButton_clicked();
        }
      });
      panel_8.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
      meshGenGenerateBtn.setText("Render slices");
      meshGenGenerateBtn.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/sports-soccer.png")));
      meshGenGenerateBtn.setPreferredSize(new Dimension(132, 46));
      meshGenGenerateBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_8.add(meshGenGenerateBtn);
    }
    return panel_89;
  }

  private JPanel getPanel_90() {
    if (panel_90 == null) {
      panel_90 = new JPanel();
      panel_90.setBorder(new TitledBorder(null, "Input flame", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_90.setPreferredSize(new Dimension(176, 10));
      panel_90.setLayout(null);

      meshGenFromEditorBtn = new JButton();
      meshGenFromEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().fromEditorButton_clicked();
        }
      });
      meshGenFromEditorBtn.setBounds(18, 22, 138, 24);
      panel_90.add(meshGenFromEditorBtn);
      meshGenFromEditorBtn.setToolTipText("Load flame from Editor and render");
      meshGenFromEditorBtn.setText("From Editor");
      meshGenFromEditorBtn.setPreferredSize(new Dimension(125, 24));
      meshGenFromEditorBtn.setMinimumSize(new Dimension(100, 24));
      meshGenFromEditorBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenFromEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      meshGenFromClipboardBtn = new JButton();
      meshGenFromClipboardBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().fromClipboardButton_clicked();
        }
      });
      meshGenFromClipboardBtn.setBounds(18, 46, 138, 24);
      panel_90.add(meshGenFromClipboardBtn);
      meshGenFromClipboardBtn.setToolTipText("Load flame from clipboard and render");
      meshGenFromClipboardBtn.setText("From Clipboard");
      meshGenFromClipboardBtn.setPreferredSize(new Dimension(125, 24));
      meshGenFromClipboardBtn.setMinimumSize(new Dimension(100, 24));
      meshGenFromClipboardBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenFromClipboardBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      meshGenLoadFlameBtn = new JButton();
      meshGenLoadFlameBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().loadFlameButton_clicked();
        }
      });
      meshGenLoadFlameBtn.setBounds(18, 70, 138, 24);
      panel_90.add(meshGenLoadFlameBtn);
      meshGenLoadFlameBtn.setToolTipText("Load flame from file and render");
      meshGenLoadFlameBtn.setText("Load Flame");
      meshGenLoadFlameBtn.setPreferredSize(new Dimension(125, 24));
      meshGenLoadFlameBtn.setMinimumSize(new Dimension(100, 24));
      meshGenLoadFlameBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenLoadFlameBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return panel_90;
  }

  private JPanel getPanel_91() {
    if (panel_91 == null) {
      panel_91 = new JPanel();
      panel_91.setPreferredSize(new Dimension(10, 110));
      panel_91.setBorder(null);
      panel_91.setLayout(new GridLayout(0, 2, 0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new TitledBorder(null, "Fractal position", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_91.add(panel_1);
      panel_1.setLayout(null);

      JLabel label_2 = new JLabel();
      label_2.setText("CentreY");
      label_2.setSize(new Dimension(68, 22));
      label_2.setPreferredSize(new Dimension(94, 22));
      label_2.setLocation(new Point(390, 28));
      label_2.setFont(new Font("Dialog", Font.BOLD, 10));
      label_2.setBounds(16, 46, 68, 22);
      panel_1.add(label_2);

      meshGenCentreYREd = new JWFNumberField();
      meshGenCentreYREd.setValueStep(0.05);
      meshGenCentreYREd.setText("");
      meshGenCentreYREd.setSize(new Dimension(100, 24));
      meshGenCentreYREd.setPreferredSize(new Dimension(100, 24));
      meshGenCentreYREd.setLocation(new Point(456, 28));
      meshGenCentreYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenCentreYREd.setBounds(82, 46, 100, 24);
      meshGenCentreYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().centreYREd_changed();
          }
        }
      });
      panel_1.add(meshGenCentreYREd);

      meshGenCentreYSlider = new JSlider();
      meshGenCentreYSlider.setValue(0);
      meshGenCentreYSlider.setSize(new Dimension(205, 19));
      meshGenCentreYSlider.setPreferredSize(new Dimension(220, 19));
      meshGenCentreYSlider.setMinimum(-25000);
      meshGenCentreYSlider.setMaximum(25000);
      meshGenCentreYSlider.setLocation(new Point(558, 28));
      meshGenCentreYSlider.setBounds(184, 46, 308, 19);
      meshGenCentreYSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().centreYSlider_changed();
          }
        }
      });
      panel_1.add(meshGenCentreYSlider);

      JLabel label_4 = new JLabel();
      label_4.setText("Zoom");
      label_4.setSize(new Dimension(68, 22));
      label_4.setPreferredSize(new Dimension(94, 22));
      label_4.setLocation(new Point(390, 52));
      label_4.setFont(new Font("Dialog", Font.BOLD, 10));
      label_4.setBounds(16, 70, 68, 22);
      panel_1.add(label_4);

      meshGenZoomREd = new JWFNumberField();
      meshGenZoomREd.setValueStep(0.01);
      meshGenZoomREd.setText("");
      meshGenZoomREd.setSize(new Dimension(100, 24));
      meshGenZoomREd.setPreferredSize(new Dimension(100, 24));
      meshGenZoomREd.setLocation(new Point(456, 52));
      meshGenZoomREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenZoomREd.setBounds(82, 70, 100, 24);
      meshGenZoomREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().zoomREd_changed();
          }
        }
      });
      panel_1.add(meshGenZoomREd);

      meshGenZoomSlider = new JSlider();
      meshGenZoomSlider.setValue(0);
      meshGenZoomSlider.setSize(new Dimension(205, 19));
      meshGenZoomSlider.setPreferredSize(new Dimension(220, 19));
      meshGenZoomSlider.setMinimum(100);
      meshGenZoomSlider.setMaximum(10000);
      meshGenZoomSlider.setLocation(new Point(558, 52));
      meshGenZoomSlider.setBounds(184, 70, 308, 19);
      meshGenZoomSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().zoomSlider_changed();
          }
        }
      });
      panel_1.add(meshGenZoomSlider);

      JLabel lblSlice = new JLabel();
      lblSlice.setBounds(16, 22, 68, 22);
      panel_1.add(lblSlice);
      lblSlice.setText("CentreX");
      lblSlice.setPreferredSize(new Dimension(94, 22));
      lblSlice.setFont(new Font("Dialog", Font.BOLD, 10));

      meshGenCentreXREd = new JWFNumberField();
      meshGenCentreXREd.setBounds(82, 20, 100, 24);
      panel_1.add(meshGenCentreXREd);
      meshGenCentreXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().centreXREd_changed();
          }
        }
      });
      meshGenCentreXREd.setValueStep(0.05);
      meshGenCentreXREd.setText("");
      meshGenCentreXREd.setPreferredSize(new Dimension(100, 24));
      meshGenCentreXREd.setFont(new Font("Dialog", Font.PLAIN, 10));

      meshGenCentreXSlider = new JSlider();
      meshGenCentreXSlider.setBounds(184, 22, 308, 19);
      panel_1.add(meshGenCentreXSlider);
      meshGenCentreXSlider.setValue(0);
      meshGenCentreXSlider.setPreferredSize(new Dimension(220, 19));
      meshGenCentreXSlider.setMinimum(-25000);
      meshGenCentreXSlider.setMaximum(25000);
      meshGenCentreXSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().centreXSlider_changed();
          }
        }
      });

      JPanel panel_2 = new JPanel();
      panel_2.setBorder(new TitledBorder(null, "Slice cutting range", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_91.add(panel_2);
      panel_2.setLayout(null);

      JLabel lblStartz = new JLabel();
      lblStartz.setText("Position 1");
      lblStartz.setSize(new Dimension(68, 22));
      lblStartz.setPreferredSize(new Dimension(94, 22));
      lblStartz.setLocation(new Point(390, 6));
      lblStartz.setFont(new Font("Dialog", Font.BOLD, 10));
      lblStartz.setBounds(16, 32, 68, 22);
      panel_2.add(lblStartz);

      meshGenZMinREd = new JWFNumberField();
      meshGenZMinREd.setMouseSpeed(0.01);
      meshGenZMinREd.setValueStep(0.05);
      meshGenZMinREd.setText("");
      meshGenZMinREd.setSize(new Dimension(100, 24));
      meshGenZMinREd.setPreferredSize(new Dimension(100, 24));
      meshGenZMinREd.setLocation(new Point(456, 4));
      meshGenZMinREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenZMinREd.setBounds(82, 30, 100, 24);
      meshGenZMinREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().zminREd_changed();
          }
        }
      });
      panel_2.add(meshGenZMinREd);

      meshGenZMinSlider = new JSlider();
      meshGenZMinSlider.setValue(0);
      meshGenZMinSlider.setSize(new Dimension(205, 19));
      meshGenZMinSlider.setPreferredSize(new Dimension(220, 19));
      meshGenZMinSlider.setMinimum(-25000);
      meshGenZMinSlider.setMaximum(25000);
      meshGenZMinSlider.setLocation(new Point(558, 4));
      meshGenZMinSlider.setBounds(184, 30, 308, 19);
      meshGenZMinSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().zminSlider_changed();
          }
        }
      });
      panel_2.add(meshGenZMinSlider);

      JLabel lblEndz = new JLabel();
      lblEndz.setText("Position 2");
      lblEndz.setSize(new Dimension(68, 22));
      lblEndz.setPreferredSize(new Dimension(94, 22));
      lblEndz.setLocation(new Point(390, 28));
      lblEndz.setFont(new Font("Dialog", Font.BOLD, 10));
      lblEndz.setBounds(16, 54, 68, 22);
      panel_2.add(lblEndz);

      meshGenZMaxREd = new JWFNumberField();
      meshGenZMaxREd.setMouseSpeed(0.01);
      meshGenZMaxREd.setValueStep(0.05);
      meshGenZMaxREd.setText("");
      meshGenZMaxREd.setSize(new Dimension(100, 24));
      meshGenZMaxREd.setPreferredSize(new Dimension(100, 24));
      meshGenZMaxREd.setLocation(new Point(456, 28));
      meshGenZMaxREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenZMaxREd.setBounds(82, 54, 100, 24);
      meshGenZMaxREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().zmaxREd_changed();
          }
        }
      });
      panel_2.add(meshGenZMaxREd);

      meshGenZMaxSlider = new JSlider();
      meshGenZMaxSlider.setValue(0);
      meshGenZMaxSlider.setSize(new Dimension(205, 19));
      meshGenZMaxSlider.setPreferredSize(new Dimension(220, 19));
      meshGenZMaxSlider.setMinimum(-25000);
      meshGenZMaxSlider.setMaximum(25000);
      meshGenZMaxSlider.setLocation(new Point(558, 28));
      meshGenZMaxSlider.setBounds(184, 54, 308, 19);
      meshGenZMaxSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().zmaxSlider_changed();
          }
        }
      });
      panel_2.add(meshGenZMaxSlider);
    }
    return panel_91;
  }

  private JPanel getMeshGenTopViewRootPnl() {
    if (meshGenTopViewRootPnl == null) {
      meshGenTopViewRootPnl = new JPanel();
      meshGenTopViewRootPnl.setLayout(new BorderLayout(0, 0));
    }
    return meshGenTopViewRootPnl;
  }

  private JPanel getMeshGenFrontViewRootPnl() {
    if (meshGenFrontViewRootPnl == null) {
      meshGenFrontViewRootPnl = new JPanel();
      meshGenFrontViewRootPnl.setLayout(new BorderLayout(0, 0));
    }
    return meshGenFrontViewRootPnl;
  }

  private JPanel getMeshGenPerspectiveViewRootPnl() {
    if (meshGenPerspectiveViewRootPnl == null) {
      meshGenPerspectiveViewRootPnl = new JPanel();
      meshGenPerspectiveViewRootPnl.setLayout(new BorderLayout(0, 0));
    }
    return meshGenPerspectiveViewRootPnl;
  }

  public JButton getMeshGenFromEditorBtn() {
    return meshGenFromEditorBtn;
  }

  public JButton getMeshGenFromClipboardBtn() {
    return meshGenFromClipboardBtn;
  }

  public JButton getMeshGenLoadFlameBtn() {
    return meshGenLoadFlameBtn;
  }

  public JWFNumberField getMeshGenSliceCountREd() {
    return meshGenSliceCountREd;
  }

  public JWFNumberField getMeshGenSlicesPerRenderREd() {
    return meshGenSlicesPerRenderREd;
  }

  public JWFNumberField getMeshGenRenderWidthREd() {
    return meshGenRenderWidthREd;
  }

  public JWFNumberField getMeshGenRenderHeightREd() {
    return meshGenRenderHeightREd;
  }

  public JWFNumberField getMeshGenRenderQualityREd() {
    return meshGenRenderQualityREd;
  }

  public JProgressBar getMeshGenProgressbar() {
    return meshGenProgressbar;
  }

  public JButton getMeshGenGenerateBtn() {
    return meshGenGenerateBtn;
  }

  private JScrollPane getScrollPane_10() {
    if (scrollPane_10 == null) {
      scrollPane_10 = new JScrollPane();
      scrollPane_10.setViewportView(getMeshGenHintPane());
    }
    return scrollPane_10;
  }

  private JTextPane getMeshGenHintPane() {
    if (meshGenHintPane == null) {
      meshGenHintPane = new JTextPane();
      meshGenHintPane.setBackground(SystemColor.menu);
      meshGenHintPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
      meshGenHintPane.setEditable(false);
    }
    return meshGenHintPane;
  }

  public JWFNumberField getMeshGenCentreXREd() {
    return meshGenCentreXREd;
  }

  public JWFNumberField getMeshGenCentreYREd() {
    return meshGenCentreYREd;
  }

  public JWFNumberField getMeshGenZoomREd() {
    return meshGenZoomREd;
  }

  public JSlider getMeshGenCentreXSlider() {
    return meshGenCentreXSlider;
  }

  public JSlider getMeshGenCentreYSlider() {
    return meshGenCentreYSlider;
  }

  public JSlider getMeshGenZoomSlider() {
    return meshGenZoomSlider;
  }

  public JWFNumberField getMeshGenZMinREd() {
    return meshGenZMinREd;
  }

  public JWFNumberField getMeshGenZMaxREd() {
    return meshGenZMaxREd;
  }

  public JSlider getMeshGenZMinSlider() {
    return meshGenZMinSlider;
  }

  public JSlider getMeshGenZMaxSlider() {
    return meshGenZMaxSlider;
  }

  private JPanel getPanel_96() {
    if (panel_96 == null) {
      panel_96 = new JPanel();
      panel_96.setPreferredSize(new Dimension(52, 10));
      panel_96.setLayout(null);

      meshGenTopViewRenderBtn = new JButton();
      meshGenTopViewRenderBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().topViewRenderButtonClicked();
        }
      });
      meshGenTopViewRenderBtn.setToolTipText("Render image");
      meshGenTopViewRenderBtn.setPreferredSize(new Dimension(42, 24));
      meshGenTopViewRenderBtn.setMnemonic(KeyEvent.VK_R);
      meshGenTopViewRenderBtn.setIconTextGap(0);
      meshGenTopViewRenderBtn.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive.png")));
      meshGenTopViewRenderBtn.setFont(new Font("Dialog", Font.BOLD, 9));
      meshGenTopViewRenderBtn.setBounds(6, 99, 42, 24);
      panel_96.add(meshGenTopViewRenderBtn);

      meshGenTopViewToEditorBtn = new JButton();
      meshGenTopViewToEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().topViewFlameToEditorBtn_clicked();
        }
      });
      meshGenTopViewToEditorBtn.setToolTipText("Render image");
      meshGenTopViewToEditorBtn.setPreferredSize(new Dimension(42, 24));
      meshGenTopViewToEditorBtn.setMnemonic(KeyEvent.VK_R);
      meshGenTopViewToEditorBtn.setIconTextGap(0);
      meshGenTopViewToEditorBtn.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/brick2.png")));
      meshGenTopViewToEditorBtn.setFont(new Font("Dialog", Font.BOLD, 9));
      meshGenTopViewToEditorBtn.setBounds(6, 6, 42, 24);
      panel_96.add(meshGenTopViewToEditorBtn);
    }
    return panel_96;
  }

  public JButton getMeshGenTopViewRenderBtn() {
    return meshGenTopViewRenderBtn;
  }

  public JButton getMeshGenFrontViewRenderBtn() {
    return meshGenFrontViewRenderBtn;
  }

  public JButton getMeshGenPerspectiveViewRenderBtn() {
    return meshGenPerspectiveViewRenderBtn;
  }

  public JButton getMeshGenTopViewToEditorBtn() {
    return meshGenTopViewToEditorBtn;
  }

  private JButton getFlameBrowserToMeshGenBtn() {
    if (flameBrowserToMeshGenBtn == null) {
      flameBrowserToMeshGenBtn = new JButton();
      flameBrowserToMeshGenBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().toMeshGenBtn_clicked();
        }
      });
      flameBrowserToMeshGenBtn.setToolTipText("Import this flame into the 3DMesh Generator module");
      flameBrowserToMeshGenBtn.setText("To Mesh Gen");
      flameBrowserToMeshGenBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserToMeshGenBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserToMeshGenBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserToMeshGenBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return flameBrowserToMeshGenBtn;
  }

  public JPanel getPanel_1() {
    return panel_93;
  }

  public JPanel getPanel_2() {
    return panel_94;
  }

  private JTabbedPane getTabbedPane_2() {
    if (tabbedPane_2 == null) {
      tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane_2.addTab("Slice rendering", null, getPanel_95(), null);
      tabbedPane_2.addTab("Mesh generation", null, getPanel_97(), null);
    }
    return tabbedPane_2;
  }

  private JPanel getPanel_95() {
    if (panel_95 == null) {
      panel_95 = new JPanel();
      panel_95.setLayout(new BorderLayout(0, 0));
      panel_95.add(getPanel_89(), BorderLayout.NORTH);
    }
    return panel_95;
  }

  private JPanel getPanel_97() {
    if (panel_97 == null) {
      panel_97 = new JPanel();
      panel_97.setLayout(new BorderLayout(0, 0));
      panel_97.add(getPanel_98(), BorderLayout.NORTH);
      panel_97.add(getPanel_100(), BorderLayout.CENTER);
    }
    return panel_97;
  }

  private JPanel getPanel_98() {
    if (panel_98 == null) {
      panel_98 = new JPanel();
      panel_98.setPreferredSize(new Dimension(10, 116));
      panel_98.setLayout(new BorderLayout(0, 0));
      panel_98.add(getPanel_99(), BorderLayout.WEST);

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new TitledBorder(null, "Mesh generation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_98.add(panel_1, BorderLayout.CENTER);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(10, 56));
      panel_1.add(panel_2, BorderLayout.NORTH);
      panel_2.setLayout(new BorderLayout(0, 0));

      JPanel panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(610, 10));
      panel_2.add(panel_3, BorderLayout.WEST);
      panel_3.setLayout(null);

      JLabel lblImageDownsample = new JLabel();
      lblImageDownsample.setText("Image downsample");
      lblImageDownsample.setSize(new Dimension(68, 22));
      lblImageDownsample.setPreferredSize(new Dimension(94, 22));
      lblImageDownsample.setName("");
      lblImageDownsample.setLocation(new Point(6, 76));
      lblImageDownsample.setFont(new Font("Dialog", Font.BOLD, 10));
      lblImageDownsample.setBounds(6, 8, 108, 22);
      panel_3.add(lblImageDownsample);

      meshGenSequenceDownSampleREd = new JWFNumberField();
      meshGenSequenceDownSampleREd.setValueStep(1.0);
      meshGenSequenceDownSampleREd.setText("");
      meshGenSequenceDownSampleREd.setSize(new Dimension(100, 24));
      meshGenSequenceDownSampleREd.setPreferredSize(new Dimension(100, 24));
      meshGenSequenceDownSampleREd.setOnlyIntegers(true);
      meshGenSequenceDownSampleREd.setMinValue(1.0);
      meshGenSequenceDownSampleREd.setMaxValue(12.0);
      meshGenSequenceDownSampleREd.setLocation(new Point(71, 76));
      meshGenSequenceDownSampleREd.setHasMinValue(true);
      meshGenSequenceDownSampleREd.setHasMaxValue(true);
      meshGenSequenceDownSampleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSequenceDownSampleREd.setBounds(113, 6, 100, 24);
      panel_3.add(meshGenSequenceDownSampleREd);

      JLabel lblSpatialFilterRadius_1 = new JLabel();
      lblSpatialFilterRadius_1.setText("Spatial filter radius");
      lblSpatialFilterRadius_1.setSize(new Dimension(68, 22));
      lblSpatialFilterRadius_1.setPreferredSize(new Dimension(94, 22));
      lblSpatialFilterRadius_1.setName("");
      lblSpatialFilterRadius_1.setLocation(new Point(4, 76));
      lblSpatialFilterRadius_1.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpatialFilterRadius_1.setBounds(6, 32, 108, 22);
      panel_3.add(lblSpatialFilterRadius_1);

      meshGenSequenceFilterRadiusREd = new JWFNumberField();
      meshGenSequenceFilterRadiusREd.setHasMaxValue(true);
      meshGenSequenceFilterRadiusREd.setValueStep(0.05);
      meshGenSequenceFilterRadiusREd.setText("");
      meshGenSequenceFilterRadiusREd.setSize(new Dimension(100, 24));
      meshGenSequenceFilterRadiusREd.setPreferredSize(new Dimension(100, 24));
      meshGenSequenceFilterRadiusREd.setMaxValue(2.0);
      meshGenSequenceFilterRadiusREd.setLocation(new Point(71, 76));
      meshGenSequenceFilterRadiusREd.setHasMinValue(true);
      meshGenSequenceFilterRadiusREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSequenceFilterRadiusREd.setBounds(113, 30, 100, 24);
      panel_3.add(meshGenSequenceFilterRadiusREd);

      meshGenSequenceThresholdREd = new JWFNumberField();
      meshGenSequenceThresholdREd.setValueStep(1.0);
      meshGenSequenceThresholdREd.setText("");
      meshGenSequenceThresholdREd.setSize(new Dimension(100, 24));
      meshGenSequenceThresholdREd.setPreferredSize(new Dimension(100, 24));
      meshGenSequenceThresholdREd.setOnlyIntegers(true);
      meshGenSequenceThresholdREd.setMaxValue(255.0);
      meshGenSequenceThresholdREd.setLocation(new Point(71, 76));
      meshGenSequenceThresholdREd.setHasMinValue(true);
      meshGenSequenceThresholdREd.setHasMaxValue(true);
      meshGenSequenceThresholdREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSequenceThresholdREd.setBounds(325, 6, 100, 24);
      panel_3.add(meshGenSequenceThresholdREd);

      JLabel lblBrightnessThreshold = new JLabel();
      lblBrightnessThreshold.setText("Brightness threshold");
      lblBrightnessThreshold.setSize(new Dimension(68, 22));
      lblBrightnessThreshold.setPreferredSize(new Dimension(94, 22));
      lblBrightnessThreshold.setName("");
      lblBrightnessThreshold.setLocation(new Point(6, 76));
      lblBrightnessThreshold.setFont(new Font("Dialog", Font.BOLD, 10));
      lblBrightnessThreshold.setBounds(218, 8, 108, 22);
      panel_3.add(lblBrightnessThreshold);

      JLabel lblPrefilter = new JLabel();
      lblPrefilter.setText("PreFilter 1");
      lblPrefilter.setSize(new Dimension(68, 22));
      lblPrefilter.setPreferredSize(new Dimension(94, 22));
      lblPrefilter.setName("");
      lblPrefilter.setLocation(new Point(6, 76));
      lblPrefilter.setFont(new Font("Dialog", Font.BOLD, 10));
      lblPrefilter.setBounds(428, 8, 59, 22);
      panel_3.add(lblPrefilter);

      meshGenPreFilter1Cmb = new JComboBox();
      meshGenPreFilter1Cmb.setToolTipText("Random-Symmetry-Geneator");
      meshGenPreFilter1Cmb.setPreferredSize(new Dimension(50, 24));
      meshGenPreFilter1Cmb.setMinimumSize(new Dimension(100, 24));
      meshGenPreFilter1Cmb.setMaximumSize(new Dimension(32767, 24));
      meshGenPreFilter1Cmb.setMaximumRowCount(32);
      meshGenPreFilter1Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      meshGenPreFilter1Cmb.setBounds(487, 6, 117, 24);
      panel_3.add(meshGenPreFilter1Cmb);

      meshGenPreFilter2Cmb = new JComboBox();
      meshGenPreFilter2Cmb.setToolTipText("Random-Symmetry-Geneator");
      meshGenPreFilter2Cmb.setPreferredSize(new Dimension(50, 24));
      meshGenPreFilter2Cmb.setMinimumSize(new Dimension(100, 24));
      meshGenPreFilter2Cmb.setMaximumSize(new Dimension(32767, 24));
      meshGenPreFilter2Cmb.setMaximumRowCount(32);
      meshGenPreFilter2Cmb.setFont(new Font("Dialog", Font.BOLD, 10));
      meshGenPreFilter2Cmb.setBounds(487, 30, 117, 24);
      panel_3.add(meshGenPreFilter2Cmb);

      JLabel lblPrefilter_1 = new JLabel();
      lblPrefilter_1.setText("PreFilter 2");
      lblPrefilter_1.setSize(new Dimension(68, 22));
      lblPrefilter_1.setPreferredSize(new Dimension(94, 22));
      lblPrefilter_1.setName("");
      lblPrefilter_1.setLocation(new Point(6, 76));
      lblPrefilter_1.setFont(new Font("Dialog", Font.BOLD, 10));
      lblPrefilter_1.setBounds(428, 32, 59, 22);
      panel_3.add(lblPrefilter_1);

      meshGenImageStepREd = new JWFNumberField();
      meshGenImageStepREd.setValueStep(1.0);
      meshGenImageStepREd.setText("");
      meshGenImageStepREd.setSize(new Dimension(100, 24));
      meshGenImageStepREd.setPreferredSize(new Dimension(100, 24));
      meshGenImageStepREd.setOnlyIntegers(true);
      meshGenImageStepREd.setMinValue(1.0);
      meshGenImageStepREd.setLocation(new Point(71, 76));
      meshGenImageStepREd.setHasMinValue(true);
      meshGenImageStepREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenImageStepREd.setBounds(325, 30, 100, 24);
      panel_3.add(meshGenImageStepREd);

      JLabel lblImageStep = new JLabel();
      lblImageStep.setText("Image step");
      lblImageStep.setSize(new Dimension(68, 22));
      lblImageStep.setPreferredSize(new Dimension(94, 22));
      lblImageStep.setName("");
      lblImageStep.setLocation(new Point(6, 76));
      lblImageStep.setFont(new Font("Dialog", Font.BOLD, 10));
      lblImageStep.setBounds(218, 32, 108, 22);
      panel_3.add(lblImageStep);

      JPanel panel_8 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_8.getLayout();
      panel_8.setPreferredSize(new Dimension(132, 10));
      panel_2.add(panel_8, BorderLayout.EAST);

      meshGenGenerateMeshBtn = new JButton();
      meshGenGenerateMeshBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().generateMeshButton_clicked();
        }
      });
      meshGenGenerateMeshBtn.setText("Create Mesh");
      meshGenGenerateMeshBtn.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/sports-soccer.png")));
      meshGenGenerateMeshBtn.setPreferredSize(new Dimension(132, 48));
      meshGenGenerateMeshBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_8.add(meshGenGenerateMeshBtn);

      meshGenGenerateMeshProgressbar = new JProgressBar();
      meshGenGenerateMeshProgressbar.setValue(0);
      meshGenGenerateMeshProgressbar.setStringPainted(true);
      meshGenGenerateMeshProgressbar.setPreferredSize(new Dimension(568, 21));
      panel_1.add(meshGenGenerateMeshProgressbar, BorderLayout.SOUTH);
    }
    return panel_98;
  }

  private JPanel getPanel_99() {
    if (panel_99 == null) {
      panel_99 = new JPanel();
      panel_99.setBorder(new TitledBorder(null, "Input slices", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_99.setPreferredSize(new Dimension(400, 10));
      panel_99.setLayout(null);

      meshGenLoadSequenceBtn = new JButton();
      meshGenLoadSequenceBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().loadSequenceButton_clicked();
        }
      });
      meshGenLoadSequenceBtn.setToolTipText("Load a previously rendered image sequence from harddisk");
      meshGenLoadSequenceBtn.setText("Load sequence");
      meshGenLoadSequenceBtn.setPreferredSize(new Dimension(125, 24));
      meshGenLoadSequenceBtn.setMinimumSize(new Dimension(100, 24));
      meshGenLoadSequenceBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenLoadSequenceBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      meshGenLoadSequenceBtn.setBounds(18, 47, 176, 24);
      panel_99.add(meshGenLoadSequenceBtn);

      meshGenSequenceWidthREd = new JWFNumberField();
      meshGenSequenceWidthREd.setValueStep(1.0);
      meshGenSequenceWidthREd.setText("");
      meshGenSequenceWidthREd.setSize(new Dimension(100, 24));
      meshGenSequenceWidthREd.setPreferredSize(new Dimension(100, 24));
      meshGenSequenceWidthREd.setOnlyIntegers(true);
      meshGenSequenceWidthREd.setMinValue(16.0);
      meshGenSequenceWidthREd.setMaxValue(4096.0);
      meshGenSequenceWidthREd.setLocation(new Point(71, 76));
      meshGenSequenceWidthREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSequenceWidthREd.setBounds(282, 23, 100, 24);
      panel_99.add(meshGenSequenceWidthREd);

      JLabel lblWidth = new JLabel();
      lblWidth.setText("Width");
      lblWidth.setSize(new Dimension(68, 22));
      lblWidth.setPreferredSize(new Dimension(94, 22));
      lblWidth.setName("");
      lblWidth.setLocation(new Point(4, 76));
      lblWidth.setFont(new Font("Dialog", Font.BOLD, 10));
      lblWidth.setBounds(206, 25, 79, 22);
      panel_99.add(lblWidth);

      JLabel lblHeight = new JLabel();
      lblHeight.setText("Height");
      lblHeight.setSize(new Dimension(68, 22));
      lblHeight.setPreferredSize(new Dimension(94, 22));
      lblHeight.setName("");
      lblHeight.setLocation(new Point(4, 76));
      lblHeight.setFont(new Font("Dialog", Font.BOLD, 10));
      lblHeight.setBounds(206, 49, 79, 22);
      panel_99.add(lblHeight);

      meshGenSequenceHeightREd = new JWFNumberField();
      meshGenSequenceHeightREd.setValueStep(1.0);
      meshGenSequenceHeightREd.setText("");
      meshGenSequenceHeightREd.setSize(new Dimension(100, 24));
      meshGenSequenceHeightREd.setPreferredSize(new Dimension(100, 24));
      meshGenSequenceHeightREd.setOnlyIntegers(true);
      meshGenSequenceHeightREd.setMinValue(16.0);
      meshGenSequenceHeightREd.setMaxValue(4096.0);
      meshGenSequenceHeightREd.setLocation(new Point(71, 76));
      meshGenSequenceHeightREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSequenceHeightREd.setBounds(282, 47, 100, 24);
      panel_99.add(meshGenSequenceHeightREd);

      JLabel lblSlices = new JLabel();
      lblSlices.setText("Slices");
      lblSlices.setSize(new Dimension(68, 22));
      lblSlices.setPreferredSize(new Dimension(94, 22));
      lblSlices.setName("");
      lblSlices.setLocation(new Point(4, 76));
      lblSlices.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSlices.setBounds(206, 74, 79, 22);
      panel_99.add(lblSlices);

      meshGenSequenceSlicesREd = new JWFNumberField();
      meshGenSequenceSlicesREd.setValueStep(1.0);
      meshGenSequenceSlicesREd.setText("");
      meshGenSequenceSlicesREd.setSize(new Dimension(100, 24));
      meshGenSequenceSlicesREd.setPreferredSize(new Dimension(100, 24));
      meshGenSequenceSlicesREd.setOnlyIntegers(true);
      meshGenSequenceSlicesREd.setMinValue(16.0);
      meshGenSequenceSlicesREd.setMaxValue(4096.0);
      meshGenSequenceSlicesREd.setLocation(new Point(71, 76));
      meshGenSequenceSlicesREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenSequenceSlicesREd.setBounds(282, 72, 100, 24);
      panel_99.add(meshGenSequenceSlicesREd);

      meshGenSequenceFromRendererBtn = new JButton();
      meshGenSequenceFromRendererBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().importSequenceFromRendererButton_clicked();
        }
      });
      meshGenSequenceFromRendererBtn.setToolTipText("Load the previously rendered image sequence");
      meshGenSequenceFromRendererBtn.setText("From renderer");
      meshGenSequenceFromRendererBtn.setPreferredSize(new Dimension(125, 24));
      meshGenSequenceFromRendererBtn.setMinimumSize(new Dimension(100, 24));
      meshGenSequenceFromRendererBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenSequenceFromRendererBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      meshGenSequenceFromRendererBtn.setBounds(18, 23, 176, 24);
      panel_99.add(meshGenSequenceFromRendererBtn);

      meshGenSequenceLbl = new JLabel();
      meshGenSequenceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
      meshGenSequenceLbl.setText("(none)");
      meshGenSequenceLbl.setSize(new Dimension(68, 22));
      meshGenSequenceLbl.setPreferredSize(new Dimension(94, 22));
      meshGenSequenceLbl.setName("");
      meshGenSequenceLbl.setLocation(new Point(4, 76));
      meshGenSequenceLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      meshGenSequenceLbl.setBounds(18, 75, 176, 22);
      panel_99.add(meshGenSequenceLbl);
    }
    return panel_99;
  }

  public JButton getMeshGenLoadSequenceBtn() {
    return meshGenLoadSequenceBtn;
  }

  public JWFNumberField getMeshGenSequenceWidthREd() {
    return meshGenSequenceWidthREd;
  }

  public JWFNumberField getMeshGenSequenceHeightREd() {
    return meshGenSequenceHeightREd;
  }

  public JWFNumberField getMeshGenSequenceSlicesREd() {
    return meshGenSequenceSlicesREd;
  }

  public JWFNumberField getMeshGenSequenceDownSampleREd() {
    return meshGenSequenceDownSampleREd;
  }

  public JWFNumberField getMeshGenSequenceFilterRadiusREd() {
    return meshGenSequenceFilterRadiusREd;
  }

  public JProgressBar getMeshGenGenerateMeshProgressbar() {
    return meshGenGenerateMeshProgressbar;
  }

  public JButton getMeshGenGenerateMeshBtn() {
    return meshGenGenerateMeshBtn;
  }

  public JButton getMeshGenSequenceFromRendererBtn() {
    return meshGenSequenceFromRendererBtn;
  }

  public JLabel getMeshGenSequenceLbl() {
    return meshGenSequenceLbl;
  }

  public JWFNumberField getMeshGenSequenceThresholdREd() {
    return meshGenSequenceThresholdREd;
  }

  private JPanel getPanel_100() {
    if (panel_100 == null) {
      panel_100 = new JPanel();
      panel_100.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_100.setLayout(new BorderLayout(0, 0));
      panel_100.add(getPanel_102(), BorderLayout.WEST);
      panel_100.add(getPanel_101(), BorderLayout.EAST);
      panel_100.add(getMeshGenPreviewRootPanel(), BorderLayout.CENTER);
    }
    return panel_100;
  }

  private JPanel getPanel_101() {
    if (panel_101 == null) {
      panel_101 = new JPanel();
      panel_101.setPreferredSize(new Dimension(188, 10));
      panel_101.setLayout(null);

      JLabel lblPositionY = new JLabel();
      lblPositionY.setText("Position Y");
      lblPositionY.setSize(new Dimension(68, 22));
      lblPositionY.setPreferredSize(new Dimension(94, 22));
      lblPositionY.setName("");
      lblPositionY.setLocation(new Point(6, 76));
      lblPositionY.setFont(new Font("Dialog", Font.BOLD, 10));
      lblPositionY.setBounds(6, 30, 78, 22);
      panel_101.add(lblPositionY);

      meshGenPreviewPositionYREd = new JWFNumberField();
      meshGenPreviewPositionYREd.setMouseSpeed(0.05);
      meshGenPreviewPositionYREd.setValueStep(0.1);
      meshGenPreviewPositionYREd.setText("");
      meshGenPreviewPositionYREd.setSize(new Dimension(100, 24));
      meshGenPreviewPositionYREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewPositionYREd.setLocation(new Point(71, 76));
      meshGenPreviewPositionYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenPreviewPositionYREd.setBounds(84, 30, 100, 24);
      meshGenPreviewPositionYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().previewPositionY_changed(meshGenPreviewPositionYREd.isMouseAdjusting());
          }
        }
      });
      panel_101.add(meshGenPreviewPositionYREd);

      meshGenPreviewPositionXREd = new JWFNumberField();
      meshGenPreviewPositionXREd.setMouseSpeed(0.05);
      meshGenPreviewPositionXREd.setValueStep(0.1);
      meshGenPreviewPositionXREd.setText("");
      meshGenPreviewPositionXREd.setSize(new Dimension(100, 24));
      meshGenPreviewPositionXREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewPositionXREd.setLocation(new Point(71, 76));
      meshGenPreviewPositionXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenPreviewPositionXREd.setBounds(84, 6, 100, 24);
      meshGenPreviewPositionXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().previewPositionX_changed(meshGenPreviewPositionXREd.isMouseAdjusting());
          }
        }
      });
      panel_101.add(meshGenPreviewPositionXREd);

      JLabel label_2 = new JLabel();
      label_2.setText("Position X");
      label_2.setSize(new Dimension(68, 22));
      label_2.setPreferredSize(new Dimension(94, 22));
      label_2.setName("");
      label_2.setLocation(new Point(6, 76));
      label_2.setFont(new Font("Dialog", Font.BOLD, 10));
      label_2.setBounds(6, 6, 78, 22);
      panel_101.add(label_2);

      meshGenPreviewSizeREd = new JWFNumberField();
      meshGenPreviewSizeREd.setMouseSpeed(0.2);
      meshGenPreviewSizeREd.setValueStep(0.01);
      meshGenPreviewSizeREd.setText("");
      meshGenPreviewSizeREd.setSize(new Dimension(100, 24));
      meshGenPreviewSizeREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewSizeREd.setMinValue(0.01);
      meshGenPreviewSizeREd.setLocation(new Point(71, 76));
      meshGenPreviewSizeREd.setHasMinValue(true);
      meshGenPreviewSizeREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenPreviewSizeREd.setBounds(84, 75, 100, 24);
      meshGenPreviewSizeREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().previewSize_changed(meshGenPreviewSizeREd.isMouseAdjusting());
          }
        }
      });
      panel_101.add(meshGenPreviewSizeREd);

      JLabel lblSize = new JLabel();
      lblSize.setText("Size");
      lblSize.setSize(new Dimension(68, 22));
      lblSize.setPreferredSize(new Dimension(94, 22));
      lblSize.setName("");
      lblSize.setLocation(new Point(6, 76));
      lblSize.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSize.setBounds(6, 75, 78, 22);
      panel_101.add(lblSize);

      JLabel lblScaleZ = new JLabel();
      lblScaleZ.setText("Scale Z");
      lblScaleZ.setSize(new Dimension(68, 22));
      lblScaleZ.setPreferredSize(new Dimension(94, 22));
      lblScaleZ.setName("");
      lblScaleZ.setLocation(new Point(6, 76));
      lblScaleZ.setFont(new Font("Dialog", Font.BOLD, 10));
      lblScaleZ.setBounds(6, 99, 78, 22);
      panel_101.add(lblScaleZ);

      meshGenPreviewScaleZREd = new JWFNumberField();
      meshGenPreviewScaleZREd.setMouseSpeed(0.2);
      meshGenPreviewScaleZREd.setValueStep(0.01);
      meshGenPreviewScaleZREd.setText("");
      meshGenPreviewScaleZREd.setSize(new Dimension(100, 24));
      meshGenPreviewScaleZREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewScaleZREd.setMinValue(0.01);
      meshGenPreviewScaleZREd.setLocation(new Point(71, 76));
      meshGenPreviewScaleZREd.setHasMinValue(true);
      meshGenPreviewScaleZREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenPreviewScaleZREd.setBounds(84, 99, 100, 24);
      meshGenPreviewScaleZREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().previewScaleZ_changed(meshGenPreviewScaleZREd.isMouseAdjusting());
          }
        }
      });
      panel_101.add(meshGenPreviewScaleZREd);

      meshGenPreviewRotateAlphaREd = new JWFNumberField();
      meshGenPreviewRotateAlphaREd.setValueStep(1.0);
      meshGenPreviewRotateAlphaREd.setText("");
      meshGenPreviewRotateAlphaREd.setSize(new Dimension(100, 24));
      meshGenPreviewRotateAlphaREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewRotateAlphaREd.setLocation(new Point(71, 76));
      meshGenPreviewRotateAlphaREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenPreviewRotateAlphaREd.setBounds(84, 146, 100, 24);
      meshGenPreviewRotateAlphaREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().previewRotateAlpha_changed(meshGenPreviewRotateAlphaREd.isMouseAdjusting());
          }
        }
      });
      panel_101.add(meshGenPreviewRotateAlphaREd);

      JLabel lblRotateAlpha = new JLabel();
      lblRotateAlpha.setText("Rotate Alpha");
      lblRotateAlpha.setSize(new Dimension(68, 22));
      lblRotateAlpha.setPreferredSize(new Dimension(94, 22));
      lblRotateAlpha.setName("");
      lblRotateAlpha.setLocation(new Point(6, 76));
      lblRotateAlpha.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRotateAlpha.setBounds(6, 146, 78, 22);
      panel_101.add(lblRotateAlpha);

      JLabel lblRotateBeta = new JLabel();
      lblRotateBeta.setText("Rotate Beta");
      lblRotateBeta.setSize(new Dimension(68, 22));
      lblRotateBeta.setPreferredSize(new Dimension(94, 22));
      lblRotateBeta.setName("");
      lblRotateBeta.setLocation(new Point(6, 76));
      lblRotateBeta.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRotateBeta.setBounds(6, 170, 78, 22);
      panel_101.add(lblRotateBeta);

      meshGenPreviewRotateBetaREd = new JWFNumberField();
      meshGenPreviewRotateBetaREd.setValueStep(1.0);
      meshGenPreviewRotateBetaREd.setText("");
      meshGenPreviewRotateBetaREd.setSize(new Dimension(100, 24));
      meshGenPreviewRotateBetaREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewRotateBetaREd.setLocation(new Point(71, 76));
      meshGenPreviewRotateBetaREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenPreviewRotateBetaREd.setBounds(84, 170, 100, 24);
      meshGenPreviewRotateBetaREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().previewRotateBeta_changed(meshGenPreviewRotateBetaREd.isMouseAdjusting());
          }
        }
      });
      panel_101.add(meshGenPreviewRotateBetaREd);

      meshGenRefreshPreviewBtn = new JButton();
      meshGenRefreshPreviewBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().refreshPreviewButton_clicked();
        }
      });
      meshGenRefreshPreviewBtn.setToolTipText("");
      meshGenRefreshPreviewBtn.setText("Refresh Preview");
      meshGenRefreshPreviewBtn.setPreferredSize(new Dimension(125, 24));
      meshGenRefreshPreviewBtn.setMinimumSize(new Dimension(100, 24));
      meshGenRefreshPreviewBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenRefreshPreviewBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      meshGenRefreshPreviewBtn.setBounds(6, 220, 176, 24);
      panel_101.add(meshGenRefreshPreviewBtn);

      meshGenPreviewSunflowExportBtn = new JButton();
      meshGenPreviewSunflowExportBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().previewSunflowExportButton_clicked();
        }
      });
      meshGenPreviewSunflowExportBtn.setToolTipText("Create a file which can be processed by the integrated sunflow renderer");
      meshGenPreviewSunflowExportBtn.setText("Export to sunflow renderer");
      meshGenPreviewSunflowExportBtn.setPreferredSize(new Dimension(125, 24));
      meshGenPreviewSunflowExportBtn.setMinimumSize(new Dimension(100, 24));
      meshGenPreviewSunflowExportBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenPreviewSunflowExportBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      meshGenPreviewSunflowExportBtn.setBounds(6, 276, 176, 24);
      panel_101.add(meshGenPreviewSunflowExportBtn);
    }
    return panel_101;
  }

  private JPanel getMeshGenPreviewRootPanel() {
    if (meshGenPreviewRootPanel == null) {
      meshGenPreviewRootPanel = new JPanel();
      meshGenPreviewRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return meshGenPreviewRootPanel;
  }

  public JCheckBox getMeshGenAutoPreviewCBx() {
    return meshGenAutoPreviewCBx;
  }

  public JButton getMeshGenPreviewImportLastGeneratedMeshBtn() {
    return meshGenPreviewImportLastGeneratedMeshBtn;
  }

  public JButton getMeshGenPreviewImportFromFileBtn() {
    return meshGenPreviewImportFromFileBtn;
  }

  public JButton getMeshGenClearPreviewBtn() {
    return meshGenClearPreviewBtn;
  }

  public JWFNumberField getMeshGenPreviewPositionXREd() {
    return meshGenPreviewPositionXREd;
  }

  public JWFNumberField getMeshGenPreviewPositionYREd() {
    return meshGenPreviewPositionYREd;
  }

  public JWFNumberField getMeshGenPreviewSizeREd() {
    return meshGenPreviewSizeREd;
  }

  public JWFNumberField getMeshGenPreviewScaleZREd() {
    return meshGenPreviewScaleZREd;
  }

  public JWFNumberField getMeshGenPreviewRotateAlphaREd() {
    return meshGenPreviewRotateAlphaREd;
  }

  public JWFNumberField getMeshGenPreviewRotateBetaREd() {
    return meshGenPreviewRotateBetaREd;
  }

  public JWFNumberField getMeshGenPreviewPointsREd() {
    return meshGenPreviewPointsREd;
  }

  private JPanel getPanel_102() {
    if (panel_102 == null) {
      panel_102 = new JPanel();
      panel_102.setPreferredSize(new Dimension(188, 10));
      panel_102.setLayout(null);

      meshGenAutoPreviewCBx = new JCheckBox("Auto-Preview");
      meshGenAutoPreviewCBx.setBounds(6, 6, 176, 18);
      panel_102.add(meshGenAutoPreviewCBx);
      meshGenAutoPreviewCBx.setSelected(true);
      meshGenAutoPreviewCBx.setToolTipText("Display preview after creating mesh (may be slow on slow machines)");

      meshGenPreviewImportLastGeneratedMeshBtn = new JButton();
      meshGenPreviewImportLastGeneratedMeshBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().importLastGeneratedMeshIntoPreviewBtn_clicked();
        }
      });
      meshGenPreviewImportLastGeneratedMeshBtn.setBounds(6, 53, 176, 24);
      panel_102.add(meshGenPreviewImportLastGeneratedMeshBtn);
      meshGenPreviewImportLastGeneratedMeshBtn.setToolTipText("Display the previously created mesh");
      meshGenPreviewImportLastGeneratedMeshBtn.setText("Import last generated");
      meshGenPreviewImportLastGeneratedMeshBtn.setPreferredSize(new Dimension(125, 24));
      meshGenPreviewImportLastGeneratedMeshBtn.setMinimumSize(new Dimension(100, 24));
      meshGenPreviewImportLastGeneratedMeshBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenPreviewImportLastGeneratedMeshBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      meshGenPreviewImportFromFileBtn = new JButton();
      meshGenPreviewImportFromFileBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().loadPreviewMeshBtn_clicked();
        }
      });
      meshGenPreviewImportFromFileBtn.setBounds(6, 78, 176, 24);
      panel_102.add(meshGenPreviewImportFromFileBtn);
      meshGenPreviewImportFromFileBtn.setToolTipText("Load and display a mesh from disc");
      meshGenPreviewImportFromFileBtn.setText("Import from file");
      meshGenPreviewImportFromFileBtn.setPreferredSize(new Dimension(125, 24));
      meshGenPreviewImportFromFileBtn.setMinimumSize(new Dimension(100, 24));
      meshGenPreviewImportFromFileBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenPreviewImportFromFileBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      meshGenClearPreviewBtn = new JButton();
      meshGenClearPreviewBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getMeshGenController().clearPreviewButton_clicked();
        }
      });
      meshGenClearPreviewBtn.setBounds(6, 120, 176, 24);
      panel_102.add(meshGenClearPreviewBtn);
      meshGenClearPreviewBtn.setToolTipText("Remove the currently displayed mesh from memory");
      meshGenClearPreviewBtn.setText("Clear Preview");
      meshGenClearPreviewBtn.setPreferredSize(new Dimension(125, 24));
      meshGenClearPreviewBtn.setMinimumSize(new Dimension(100, 24));
      meshGenClearPreviewBtn.setMaximumSize(new Dimension(32000, 24));
      meshGenClearPreviewBtn.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel lblModelReduction = new JLabel();
      lblModelReduction.setBounds(4, 194, 78, 22);
      panel_102.add(lblModelReduction);
      lblModelReduction.setToolTipText("");
      lblModelReduction.setText("Points");
      lblModelReduction.setPreferredSize(new Dimension(94, 22));
      lblModelReduction.setName("");
      lblModelReduction.setFont(new Font("Dialog", Font.BOLD, 10));

      meshGenPreviewPointsREd = new JWFNumberField();
      meshGenPreviewPointsREd.setBounds(82, 194, 100, 24);
      panel_102.add(meshGenPreviewPointsREd);
      meshGenPreviewPointsREd.setValueStep(1.0);
      meshGenPreviewPointsREd.setText("");
      meshGenPreviewPointsREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewPointsREd.setOnlyIntegers(true);
      meshGenPreviewPointsREd.setMinValue(1.0);
      meshGenPreviewPointsREd.setMaxValue(12.0);
      meshGenPreviewPointsREd.setHasMinValue(true);
      meshGenPreviewPointsREd.setHasMaxValue(true);
      meshGenPreviewPointsREd.setFont(new Font("Dialog", Font.PLAIN, 10));

      meshGenPreviewPolygonsREd = new JWFNumberField();
      meshGenPreviewPolygonsREd.setValueStep(1.0);
      meshGenPreviewPolygonsREd.setText("");
      meshGenPreviewPolygonsREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewPolygonsREd.setOnlyIntegers(true);
      meshGenPreviewPolygonsREd.setMinValue(1.0);
      meshGenPreviewPolygonsREd.setMaxValue(12.0);
      meshGenPreviewPolygonsREd.setHasMinValue(true);
      meshGenPreviewPolygonsREd.setHasMaxValue(true);
      meshGenPreviewPolygonsREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      meshGenPreviewPolygonsREd.setBounds(82, 216, 100, 24);
      panel_102.add(meshGenPreviewPolygonsREd);

      JLabel lblPolygons = new JLabel();
      lblPolygons.setToolTipText("");
      lblPolygons.setText("Polygons");
      lblPolygons.setPreferredSize(new Dimension(94, 22));
      lblPolygons.setName("");
      lblPolygons.setFont(new Font("Dialog", Font.BOLD, 10));
      lblPolygons.setBounds(4, 216, 78, 22);
      panel_102.add(lblPolygons);
    }
    return panel_102;
  }

  public JWFNumberField getMeshGenPreviewPolygonsREd() {
    return meshGenPreviewPolygonsREd;
  }

  public JButton getMeshGenRefreshPreviewBtn() {
    return meshGenRefreshPreviewBtn;
  }

  private JPanel getPanel_103() {
    if (panel_103 == null) {
      panel_103 = new JPanel();
      panel_103.setLayout(new BorderLayout(0, 0));
      panel_103.add(getScrollPane_11(), BorderLayout.CENTER);
    }
    return panel_103;
  }

  private JPanel getPanel_104() {
    if (panel_104 == null) {
      panel_104 = new JPanel();
      panel_104.setLayout(new BorderLayout(0, 0));
      panel_104.add(getScrollPane_1(), BorderLayout.CENTER);
    }
    return panel_104;
  }

  private JPanel getPanel_105() {
    if (panel_105 == null) {
      panel_105 = new JPanel();
      panel_105.setLayout(new BorderLayout(0, 0));
      panel_105.add(getScrollPane_10(), BorderLayout.CENTER);
    }
    return panel_105;
  }

  public JButton getMeshGenPreviewSunflowExportBtn() {
    return meshGenPreviewSunflowExportBtn;
  }

  private JScrollPane getScrollPane_11() {
    if (scrollPane_11 == null) {
      scrollPane_11 = new JScrollPane();
      scrollPane_11.setViewportView(getApophysisHintsPane());
    }
    return scrollPane_11;
  }

  private JTextPane getApophysisHintsPane() {
    if (apophysisHintsPane == null) {
      apophysisHintsPane = new JTextPane();
      apophysisHintsPane.setBackground(SystemColor.menu);
      apophysisHintsPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
      apophysisHintsPane.setEditable(false);
    }
    return apophysisHintsPane;
  }

  private JPanel getChannelMixerPanel() {
    if (channelMixerPanel == null) {
      channelMixerPanel = new JPanel();
      channelMixerPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(160, 10));
      channelMixerPanel.add(panel_1, BorderLayout.WEST);
      panel_1.setLayout(null);
      panel_1.add(getPanel_117());

      channelMixerModeCmb = new JComboBox();
      channelMixerModeCmb.setPreferredSize(new Dimension(125, 24));
      channelMixerModeCmb.setMinimumSize(new Dimension(100, 24));
      channelMixerModeCmb.setMaximumSize(new Dimension(32767, 24));
      channelMixerModeCmb.setMaximumRowCount(32);
      channelMixerModeCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      channelMixerModeCmb.setBounds(6, 39, 100, 24);
      channelMixerModeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getChannelMixerControls().channelMixerModeCmb_changed();
          }
        }
      });
      panel_1.add(channelMixerModeCmb);

      channelMixerResetBtn = new JButton();
      channelMixerResetBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null) {
            tinaController.getChannelMixerControls().resetBtn_clicked();
          }
        }
      });
      channelMixerResetBtn.setText("Reset");
      channelMixerResetBtn.setPreferredSize(new Dimension(125, 24));
      channelMixerResetBtn.setMinimumSize(new Dimension(100, 24));
      channelMixerResetBtn.setMaximumSize(new Dimension(32000, 24));
      channelMixerResetBtn.setIconTextGap(2);
      channelMixerResetBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      channelMixerResetBtn.setBounds(6, 75, 100, 24);
      panel_1.add(channelMixerResetBtn);

      JLabel lblMode = new JLabel();
      lblMode.setText("Mixer mode");
      lblMode.setSize(new Dimension(20, 22));
      lblMode.setPreferredSize(new Dimension(24, 22));
      lblMode.setName("affineC00Lbl");
      lblMode.setLocation(new Point(0, 6));
      lblMode.setHorizontalAlignment(SwingConstants.LEFT);
      lblMode.setFont(new Font("Dialog", Font.BOLD, 10));
      lblMode.setBounds(8, 16, 87, 22);
      panel_1.add(lblMode);

      JPanel panel_2 = new JPanel();
      channelMixerPanel.add(panel_2, BorderLayout.CENTER);
      panel_2.setLayout(new GridLayout(0, 3, 0, 0));

      JPanel panel_3 = new JPanel();
      panel_2.add(panel_3);
      panel_3.setLayout(new BorderLayout(0, 0));
      panel_3.add(getChannelMixerRedRedRootPanel(), BorderLayout.CENTER);

      JPanel panel_8 = new JPanel();
      panel_2.add(panel_8);
      panel_8.setLayout(new BorderLayout(0, 0));
      panel_8.add(getChannelMixerRedGreenRootPanel(), BorderLayout.CENTER);

      JPanel panel_9 = new JPanel();
      panel_2.add(panel_9);
      panel_9.setLayout(new BorderLayout(0, 0));
      panel_9.add(getChannelMixerRedBlueRootPanel(), BorderLayout.CENTER);

      JPanel panel_10 = new JPanel();
      panel_2.add(panel_10);
      panel_10.setLayout(new BorderLayout(0, 0));
      panel_10.add(getChannelMixerGreenRedRootPanel(), BorderLayout.CENTER);

      JPanel panel_11 = new JPanel();
      panel_2.add(panel_11);
      panel_11.setLayout(new BorderLayout(0, 0));
      panel_11.add(getChannelMixerGreenGreenRootPanel(), BorderLayout.CENTER);

      JPanel panel_83 = new JPanel();
      panel_2.add(panel_83);
      panel_83.setLayout(new BorderLayout(0, 0));
      panel_83.add(getChannelMixerGreenBlueRootPanel(), BorderLayout.CENTER);

      JPanel panel_85 = new JPanel();
      panel_2.add(panel_85);
      panel_85.setLayout(new BorderLayout(0, 0));
      panel_85.add(getChannelMixerBlueRedRootPanel(), BorderLayout.CENTER);

      JPanel panel_106 = new JPanel();
      panel_2.add(panel_106);
      panel_106.setLayout(new BorderLayout(0, 0));
      panel_106.add(getChannelMixerBlueGreenRootPanel(), BorderLayout.CENTER);

      JPanel panel_107 = new JPanel();
      panel_2.add(panel_107);
      panel_107.setLayout(new BorderLayout(0, 0));
      panel_107.add(getChannelMixerBlueBlueRootPanel(), BorderLayout.CENTER);
    }
    return channelMixerPanel;
  }

  public JWFNumberField getMeshGenThicknessREd() {
    return meshGenSliceThicknessModREd;
  }

  private JPanel getChannelMixerRedRedRootPanel() {
    if (channelMixerRedRedRootPanel == null) {
      channelMixerRedRedRootPanel = new JPanel();
      channelMixerRedRedRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerRedRedRootPanel;
  }

  private JPanel getChannelMixerRedGreenRootPanel() {
    if (channelMixerRedGreenRootPanel == null) {
      channelMixerRedGreenRootPanel = new JPanel();
      channelMixerRedGreenRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerRedGreenRootPanel;
  }

  private JPanel getChannelMixerRedBlueRootPanel() {
    if (channelMixerRedBlueRootPanel == null) {
      channelMixerRedBlueRootPanel = new JPanel();
      channelMixerRedBlueRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerRedBlueRootPanel;
  }

  private JPanel getChannelMixerGreenRedRootPanel() {
    if (channelMixerGreenRedRootPanel == null) {
      channelMixerGreenRedRootPanel = new JPanel();
      channelMixerGreenRedRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerGreenRedRootPanel;
  }

  private JPanel getChannelMixerGreenGreenRootPanel() {
    if (channelMixerGreenGreenRootPanel == null) {
      channelMixerGreenGreenRootPanel = new JPanel();
      channelMixerGreenGreenRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerGreenGreenRootPanel;
  }

  private JPanel getChannelMixerGreenBlueRootPanel() {
    if (channelMixerGreenBlueRootPanel == null) {
      channelMixerGreenBlueRootPanel = new JPanel();
      channelMixerGreenBlueRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerGreenBlueRootPanel;
  }

  private JPanel getChannelMixerBlueRedRootPanel() {
    if (channelMixerBlueRedRootPanel == null) {
      channelMixerBlueRedRootPanel = new JPanel();
      channelMixerBlueRedRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerBlueRedRootPanel;
  }

  private JPanel getChannelMixerBlueGreenRootPanel() {
    if (channelMixerBlueGreenRootPanel == null) {
      channelMixerBlueGreenRootPanel = new JPanel();
      channelMixerBlueGreenRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerBlueGreenRootPanel;
  }

  private JPanel getChannelMixerBlueBlueRootPanel() {
    if (channelMixerBlueBlueRootPanel == null) {
      channelMixerBlueBlueRootPanel = new JPanel();
      channelMixerBlueBlueRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerBlueBlueRootPanel;
  }

  private JPanel getPanel_117() {
    if (panel_117 == null) {
      panel_117 = new JPanel();
      panel_117.setBounds(118, 0, 42, 144);
      panel_117.setPreferredSize(new Dimension(42, 10));
      panel_117.setLayout(new GridLayout(0, 1, 0, 0));
      panel_117.add(getLblRed());
      panel_117.add(getLblGreen());
      panel_117.add(getLblBlue());
    }
    return panel_117;
  }

  private JLabel getLblRed() {
    if (lblRed == null) {
      lblRed = new JLabel();
      lblRed.setText("Red ");
      lblRed.setSize(new Dimension(20, 22));
      lblRed.setPreferredSize(new Dimension(24, 22));
      lblRed.setName("affineC00Lbl");
      lblRed.setLocation(new Point(0, 6));
      lblRed.setHorizontalAlignment(SwingConstants.RIGHT);
      lblRed.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblRed;
  }

  private JLabel getLblGreen() {
    if (lblGreen == null) {
      lblGreen = new JLabel();
      lblGreen.setText("Green ");
      lblGreen.setSize(new Dimension(20, 22));
      lblGreen.setPreferredSize(new Dimension(24, 22));
      lblGreen.setName("affineC00Lbl");
      lblGreen.setLocation(new Point(0, 6));
      lblGreen.setHorizontalAlignment(SwingConstants.RIGHT);
      lblGreen.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblGreen;
  }

  private JLabel getLblBlue() {
    if (lblBlue == null) {
      lblBlue = new JLabel();
      lblBlue.setText("Blue ");
      lblBlue.setSize(new Dimension(20, 22));
      lblBlue.setPreferredSize(new Dimension(24, 22));
      lblBlue.setName("affineC00Lbl");
      lblBlue.setLocation(new Point(0, 6));
      lblBlue.setHorizontalAlignment(SwingConstants.RIGHT);
      lblBlue.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return lblBlue;
  }

  public JButton getChannelMixerResetBtn() {
    return channelMixerResetBtn;
  }

  public JComboBox getChannelMixerModeCmb() {
    return channelMixerModeCmb;
  }

  public JWFNumberField getMeshGenThicknessSamplesREd() {
    return meshGenSliceThicknessSamplesREd;
  }

  public JComboBox getMeshGenPreFilter1Cmb() {
    return meshGenPreFilter1Cmb;
  }

  public JComboBox getMeshGenPreFilter2Cmb() {
    return meshGenPreFilter2Cmb;
  }

  public JWFNumberField getMeshGenImageStepREd() {
    return meshGenImageStepREd;
  }

  public JButton getMotionCurvePlayPreviewButton() {
    return motionCurvePlayPreviewButton;
  }

  private JPanel getPanel_92() {
    if (panel_92 == null) {
      panel_92 = new JPanel();
      panel_92.setLayout(new BorderLayout(0, 0));
      panel_92.add(getTabbedPane_3(), BorderLayout.CENTER);
    }
    return panel_92;
  }

  private JTabbedPane getTabbedPane_3() {
    if (tabbedPane_3 == null) {
      tabbedPane_3 = new JTabbedPane(JTabbedPane.LEFT);
      tabbedPane_3.addTab("DOF", null, tinaDOFPanel, null);
      tabbedPane_3.addTab("Bokeh", null, getPanel_118(), null);
    }
    return tabbedPane_3;
  }

  private JPanel getPanel_118() {
    if (panel_118 == null) {
      panel_118 = new JPanel();
      panel_118.setLayout(null);

      JLabel lblShape = new JLabel();
      lblShape.setText("Shape");
      lblShape.setSize(new Dimension(94, 22));
      lblShape.setPreferredSize(new Dimension(94, 22));
      lblShape.setLocation(new Point(488, 2));
      lblShape.setFont(new Font("Dialog", Font.BOLD, 10));
      lblShape.setBounds(6, 6, 94, 22);
      panel_118.add(lblShape);

      dofDOFScaleLbl = new JLabel();
      dofDOFScaleLbl.setText("Scale");
      dofDOFScaleLbl.setSize(new Dimension(94, 22));
      dofDOFScaleLbl.setPreferredSize(new Dimension(94, 22));
      dofDOFScaleLbl.setName("dofDOFScaleLbl");
      dofDOFScaleLbl.setLocation(new Point(488, 2));
      dofDOFScaleLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFScaleLbl.setBounds(6, 29, 94, 22);
      panel_118.add(dofDOFScaleLbl);

      dofDOFScaleREd = new JWFNumberField();
      dofDOFScaleREd.setName("dofDOFScaleREd");
      dofDOFScaleREd.setValueStep(0.05);
      dofDOFScaleREd.setText("");
      dofDOFScaleREd.setSize(new Dimension(100, 24));
      dofDOFScaleREd.setPreferredSize(new Dimension(100, 24));
      dofDOFScaleREd.setMinValue(-10000.0);
      dofDOFScaleREd.setMaxValue(10000.0);
      dofDOFScaleREd.setLocation(new Point(584, 2));
      dofDOFScaleREd.setLinkedMotionControlName("dofDOFScaleSlider");
      dofDOFScaleREd.setLinkedLabelControlName("dofDOFScaleLbl");
      dofDOFScaleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFScaleREd.setEditable(true);
      dofDOFScaleREd.setBounds(102, 29, 100, 24);
      dofDOFScaleREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFScaleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFScaleREd.isMouseAdjusting() || dofDOFScaleREd.getMouseChangeCount() == 0) {
              if (!dofDOFScaleSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFScaleREd_changed();
          }
        }
      });

      panel_118.add(dofDOFScaleREd);

      dofDOFShapeCmb = new JComboBox();
      dofDOFShapeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().dofDOFShapeCmb_changed();
          }
        }
      });
      dofDOFShapeCmb.setSize(new Dimension(125, 22));
      dofDOFShapeCmb.setPreferredSize(new Dimension(125, 22));
      dofDOFShapeCmb.setLocation(new Point(100, 4));
      dofDOFShapeCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFShapeCmb.setBounds(102, 6, 322, 24);
      panel_118.add(dofDOFShapeCmb);

      dofDOFScaleSlider = new JSlider();
      dofDOFScaleSlider.setValue(0);
      dofDOFScaleSlider.setSize(new Dimension(220, 19));
      dofDOFScaleSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFScaleSlider.setName("stereo3dAngleSlider");
      dofDOFScaleSlider.setMinimum(-30000);
      dofDOFScaleSlider.setMaximum(30000);
      dofDOFScaleSlider.setLocation(new Point(686, 2));
      dofDOFScaleSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFScaleSlider.setBounds(204, 29, 220, 24);
      dofDOFScaleSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFScaleSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFScaleSlider_stateChanged(e);
          }
        }
      });

      panel_118.add(dofDOFScaleSlider);

      dofDOFAngleREd = new JWFNumberField();
      dofDOFAngleREd.setName("dofDOFAngleREd");
      dofDOFAngleREd.setValueStep(0.05);
      dofDOFAngleREd.setText("");
      dofDOFAngleREd.setSize(new Dimension(100, 24));
      dofDOFAngleREd.setPreferredSize(new Dimension(100, 24));
      dofDOFAngleREd.setMinValue(-10000.0);
      dofDOFAngleREd.setMaxValue(10000.0);
      dofDOFAngleREd.setLocation(new Point(584, 2));
      dofDOFAngleREd.setLinkedMotionControlName("dofDOFAngleSlider");
      dofDOFAngleREd.setLinkedLabelControlName("dofDOFAngleLbl");
      dofDOFAngleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFAngleREd.setEditable(true);
      dofDOFAngleREd.setBounds(102, 52, 100, 24);
      dofDOFAngleREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFAngleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFAngleREd.isMouseAdjusting() || dofDOFAngleREd.getMouseChangeCount() == 0) {
              if (!dofDOFAngleSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFAngleREd_changed();
          }
        }
      });

      panel_118.add(dofDOFAngleREd);

      dofDOFAngleLbl = new JLabel();
      dofDOFAngleLbl.setText("Rotate");
      dofDOFAngleLbl.setSize(new Dimension(94, 22));
      dofDOFAngleLbl.setPreferredSize(new Dimension(94, 22));
      dofDOFAngleLbl.setName("dofDOFAngleLbl");
      dofDOFAngleLbl.setLocation(new Point(488, 2));
      dofDOFAngleLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFAngleLbl.setBounds(6, 52, 94, 22);
      panel_118.add(dofDOFAngleLbl);

      dofDOFAngleSlider = new JSlider();
      dofDOFAngleSlider.setValue(0);
      dofDOFAngleSlider.setSize(new Dimension(220, 19));
      dofDOFAngleSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFAngleSlider.setName("stereo3dAngleSlider");
      dofDOFAngleSlider.setMinimum(-30000);
      dofDOFAngleSlider.setMaximum(30000);
      dofDOFAngleSlider.setLocation(new Point(686, 2));
      dofDOFAngleSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFAngleSlider.setBounds(204, 52, 220, 24);
      dofDOFAngleSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFAngleSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFAngleSlider_stateChanged(e);
          }
        }
      });
      panel_118.add(dofDOFAngleSlider);

      dofDOFFadeLbl = new JLabel();
      dofDOFFadeLbl.setText("Fade");
      dofDOFFadeLbl.setSize(new Dimension(94, 22));
      dofDOFFadeLbl.setPreferredSize(new Dimension(94, 22));
      dofDOFFadeLbl.setName("dofDOFFadeLbl");
      dofDOFFadeLbl.setLocation(new Point(488, 2));
      dofDOFFadeLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFFadeLbl.setBounds(6, 75, 94, 22);
      panel_118.add(dofDOFFadeLbl);

      dofDOFFadeREd = new JWFNumberField();
      dofDOFFadeREd.setName("dofDOFFadeREd");
      dofDOFFadeREd.setValueStep(0.05);
      dofDOFFadeREd.setText("");
      dofDOFFadeREd.setSize(new Dimension(100, 24));
      dofDOFFadeREd.setPreferredSize(new Dimension(100, 24));
      dofDOFFadeREd.setMinValue(-10000.0);
      dofDOFFadeREd.setMaxValue(10000.0);
      dofDOFFadeREd.setLocation(new Point(584, 2));
      dofDOFFadeREd.setLinkedMotionControlName("dofDOFFadeSlider");
      dofDOFFadeREd.setLinkedLabelControlName("dofDOFFadeLbl");
      dofDOFFadeREd.setHasMinValue(true);
      dofDOFFadeREd.setHasMaxValue(true);
      dofDOFFadeREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFFadeREd.setEditable(true);
      dofDOFFadeREd.setBounds(102, 75, 100, 24);
      dofDOFFadeREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFFadeREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFFadeREd.isMouseAdjusting() || dofDOFFadeREd.getMouseChangeCount() == 0) {
              if (!dofDOFFadeSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFFadeREd_changed();
          }
        }
      });
      panel_118.add(dofDOFFadeREd);

      dofDOFFadeSlider = new JSlider();
      dofDOFFadeSlider.setValue(0);
      dofDOFFadeSlider.setSize(new Dimension(220, 19));
      dofDOFFadeSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFFadeSlider.setName("stereo3dAngleSlider");
      dofDOFFadeSlider.setMinimum(-30000);
      dofDOFFadeSlider.setMaximum(30000);
      dofDOFFadeSlider.setLocation(new Point(686, 2));
      dofDOFFadeSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFFadeSlider.setBounds(204, 75, 220, 24);
      dofDOFFadeSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFFadeSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFFadeSlider_stateChanged(e);
          }
        }
      });
      panel_118.add(dofDOFFadeSlider);

      dofDOFParam1REd = new JWFNumberField();
      dofDOFParam1REd.setName("dofDOFParam1REd");
      dofDOFParam1REd.setValueStep(0.05);
      dofDOFParam1REd.setText("");
      dofDOFParam1REd.setSize(new Dimension(100, 24));
      dofDOFParam1REd.setPreferredSize(new Dimension(100, 24));
      dofDOFParam1REd.setMinValue(-10000.0);
      dofDOFParam1REd.setMaxValue(10000.0);
      dofDOFParam1REd.setLocation(new Point(584, 2));
      dofDOFParam1REd.setLinkedMotionControlName("dofDOFParam1Slider");
      dofDOFParam1REd.setLinkedLabelControlName("dofDOFParam1Lbl");
      dofDOFParam1REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFParam1REd.setEditable(true);
      dofDOFParam1REd.setBounds(102, 98, 100, 24);
      dofDOFParam1REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFParam1REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFParam1REd.isMouseAdjusting() || dofDOFParam1REd.getMouseChangeCount() == 0) {
              if (!dofDOFParam1Slider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFParam1REd_changed();
          }
        }
      });
      panel_118.add(dofDOFParam1REd);

      dofDOFParam1Lbl = new JLabel();
      dofDOFParam1Lbl.setText("Param 1");
      dofDOFParam1Lbl.setSize(new Dimension(94, 22));
      dofDOFParam1Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam1Lbl.setName("dofDOFParam1Lbl");
      dofDOFParam1Lbl.setLocation(new Point(488, 2));
      dofDOFParam1Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam1Lbl.setBounds(6, 98, 94, 22);
      panel_118.add(dofDOFParam1Lbl);

      dofDOFParam1Slider = new JSlider();
      dofDOFParam1Slider.setValue(0);
      dofDOFParam1Slider.setSize(new Dimension(220, 19));
      dofDOFParam1Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam1Slider.setName("dofDOFParam1Slider");
      dofDOFParam1Slider.setMinimum(-30000);
      dofDOFParam1Slider.setMaximum(30000);
      dofDOFParam1Slider.setLocation(new Point(686, 2));
      dofDOFParam1Slider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam1Slider.setBounds(204, 98, 220, 24);
      dofDOFParam1Slider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFParam1Slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFParam1Slider_stateChanged(e);
          }
        }
      });
      panel_118.add(dofDOFParam1Slider);

      dofDOFParam3REd = new JWFNumberField();
      dofDOFParam3REd.setName("dofDOFParam3REd");
      dofDOFParam3REd.setValueStep(0.05);
      dofDOFParam3REd.setText("");
      dofDOFParam3REd.setSize(new Dimension(100, 24));
      dofDOFParam3REd.setPreferredSize(new Dimension(100, 24));
      dofDOFParam3REd.setMinValue(-10000.0);
      dofDOFParam3REd.setMaxValue(10000.0);
      dofDOFParam3REd.setLocation(new Point(584, 2));
      dofDOFParam3REd.setLinkedMotionControlName("dofDOFParam3Slider");
      dofDOFParam3REd.setLinkedLabelControlName("dofDOFParam3Lbl");
      dofDOFParam3REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFParam3REd.setEditable(true);
      dofDOFParam3REd.setBounds(562, 29, 100, 24);
      dofDOFParam3REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFParam3REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFParam3REd.isMouseAdjusting() || dofDOFParam3REd.getMouseChangeCount() == 0) {
              if (!dofDOFParam3Slider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFParam3REd_changed();
          }
        }
      });
      panel_118.add(dofDOFParam3REd);

      dofDOFParam3Lbl = new JLabel();
      dofDOFParam3Lbl.setText("Param 3");
      dofDOFParam3Lbl.setSize(new Dimension(94, 22));
      dofDOFParam3Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam3Lbl.setName("dofDOFParam3Lbl");
      dofDOFParam3Lbl.setLocation(new Point(488, 2));
      dofDOFParam3Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam3Lbl.setBounds(466, 29, 94, 22);
      panel_118.add(dofDOFParam3Lbl);

      dofDOFParam4Lbl = new JLabel();
      dofDOFParam4Lbl.setText("Param 4");
      dofDOFParam4Lbl.setSize(new Dimension(94, 22));
      dofDOFParam4Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam4Lbl.setName("dofDOFParam4Lbl");
      dofDOFParam4Lbl.setLocation(new Point(488, 2));
      dofDOFParam4Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam4Lbl.setBounds(466, 52, 94, 22);
      panel_118.add(dofDOFParam4Lbl);

      dofDOFParam4REd = new JWFNumberField();
      dofDOFParam4REd.setName("dofDOFParam4REd");
      dofDOFParam4REd.setValueStep(0.05);
      dofDOFParam4REd.setText("");
      dofDOFParam4REd.setSize(new Dimension(100, 24));
      dofDOFParam4REd.setPreferredSize(new Dimension(100, 24));
      dofDOFParam4REd.setMinValue(-10000.0);
      dofDOFParam4REd.setMaxValue(10000.0);
      dofDOFParam4REd.setLocation(new Point(584, 2));
      dofDOFParam4REd.setLinkedMotionControlName("dofDOFParam4Slider");
      dofDOFParam4REd.setLinkedLabelControlName("dofDOFParam4Lbl");
      dofDOFParam4REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFParam4REd.setEditable(true);
      dofDOFParam4REd.setBounds(562, 52, 100, 24);
      dofDOFParam4REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFParam4REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFParam4REd.isMouseAdjusting() || dofDOFParam4REd.getMouseChangeCount() == 0) {
              if (!dofDOFParam4Slider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFParam4REd_changed();
          }
        }
      });
      panel_118.add(dofDOFParam4REd);

      dofDOFParam3Slider = new JSlider();
      dofDOFParam3Slider.setValue(0);
      dofDOFParam3Slider.setSize(new Dimension(220, 19));
      dofDOFParam3Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam3Slider.setName("dofDOFParam3Slider");
      dofDOFParam3Slider.setMinimum(-30000);
      dofDOFParam3Slider.setMaximum(30000);
      dofDOFParam3Slider.setLocation(new Point(686, 2));
      dofDOFParam3Slider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam3Slider.setBounds(664, 29, 220, 24);
      dofDOFParam3Slider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFParam3Slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFParam3Slider_stateChanged(e);
          }
        }
      });
      panel_118.add(dofDOFParam3Slider);

      dofDOFParam4Slider = new JSlider();
      dofDOFParam4Slider.setValue(0);
      dofDOFParam4Slider.setSize(new Dimension(220, 19));
      dofDOFParam4Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam4Slider.setName("dofDOFParam4Slider");
      dofDOFParam4Slider.setMinimum(-30000);
      dofDOFParam4Slider.setMaximum(30000);
      dofDOFParam4Slider.setLocation(new Point(686, 2));
      dofDOFParam4Slider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam4Slider.setBounds(664, 52, 220, 24);
      dofDOFParam4Slider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFParam4Slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFParam4Slider_stateChanged(e);
          }
        }
      });
      panel_118.add(dofDOFParam4Slider);

      dofDOFParam5REd = new JWFNumberField();
      dofDOFParam5REd.setName("dofDOFParam5REd");
      dofDOFParam5REd.setValueStep(0.05);
      dofDOFParam5REd.setText("");
      dofDOFParam5REd.setSize(new Dimension(100, 24));
      dofDOFParam5REd.setPreferredSize(new Dimension(100, 24));
      dofDOFParam5REd.setMinValue(-10000.0);
      dofDOFParam5REd.setMaxValue(10000.0);
      dofDOFParam5REd.setLocation(new Point(584, 2));
      dofDOFParam5REd.setLinkedMotionControlName("dofDOFParam5CurveSlider");
      dofDOFParam5REd.setLinkedLabelControlName("dofDOFParam5CurveLbl");
      dofDOFParam5REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFParam5REd.setEditable(true);
      dofDOFParam5REd.setBounds(562, 75, 100, 24);
      dofDOFParam5REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFParam5REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFParam5REd.isMouseAdjusting() || dofDOFParam5REd.getMouseChangeCount() == 0) {
              if (!dofDOFParam5Slider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFParam5REd_changed();
          }
        }
      });
      panel_118.add(dofDOFParam5REd);

      dofDOFParam5Lbl = new JLabel();
      dofDOFParam5Lbl.setText("Param 5");
      dofDOFParam5Lbl.setSize(new Dimension(94, 22));
      dofDOFParam5Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam5Lbl.setName("dofDOFParam5Lbl");
      dofDOFParam5Lbl.setLocation(new Point(488, 2));
      dofDOFParam5Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam5Lbl.setBounds(466, 75, 94, 22);
      panel_118.add(dofDOFParam5Lbl);

      dofDOFParam5Slider = new JSlider();
      dofDOFParam5Slider.setValue(0);
      dofDOFParam5Slider.setSize(new Dimension(220, 19));
      dofDOFParam5Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam5Slider.setName("dofDOFParam5Slider");
      dofDOFParam5Slider.setMinimum(-30000);
      dofDOFParam5Slider.setMaximum(30000);
      dofDOFParam5Slider.setLocation(new Point(686, 2));
      dofDOFParam5Slider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam5Slider.setBounds(664, 75, 220, 24);
      dofDOFParam5Slider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFParam5Slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFParam5Slider_stateChanged(e);
          }
        }
      });
      panel_118.add(dofDOFParam5Slider);

      dofDOFParam6Lbl = new JLabel();
      dofDOFParam6Lbl.setText("Param 6");
      dofDOFParam6Lbl.setSize(new Dimension(94, 22));
      dofDOFParam6Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam6Lbl.setName("dofDOFParam6Lbl");
      dofDOFParam6Lbl.setLocation(new Point(488, 2));
      dofDOFParam6Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam6Lbl.setBounds(466, 100, 98, 22);
      panel_118.add(dofDOFParam6Lbl);

      dofDOFParam6REd = new JWFNumberField();
      dofDOFParam6REd.setName("dofDOFParam6REd");
      dofDOFParam6REd.setValueStep(0.05);
      dofDOFParam6REd.setText("");
      dofDOFParam6REd.setSize(new Dimension(100, 24));
      dofDOFParam6REd.setPreferredSize(new Dimension(100, 24));
      dofDOFParam6REd.setMinValue(-10000.0);
      dofDOFParam6REd.setMaxValue(10000.0);
      dofDOFParam6REd.setLocation(new Point(584, 2));
      dofDOFParam6REd.setLinkedMotionControlName("dofDOFParam6Slider");
      dofDOFParam6REd.setLinkedLabelControlName("dofDOFParam6Lbl");
      dofDOFParam6REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFParam6REd.setEditable(true);
      dofDOFParam6REd.setBounds(562, 100, 98, 24);
      dofDOFParam6REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFParam6REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFParam6REd.isMouseAdjusting() || dofDOFParam6REd.getMouseChangeCount() == 0) {
              if (!dofDOFParam6Slider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFParam6REd_changed();
          }
        }
      });
      panel_118.add(dofDOFParam6REd);

      dofDOFParam6Slider = new JSlider();
      dofDOFParam6Slider.setValue(0);
      dofDOFParam6Slider.setSize(new Dimension(220, 19));
      dofDOFParam6Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam6Slider.setName("dofDOFParam6Slider");
      dofDOFParam6Slider.setMinimum(-30000);
      dofDOFParam6Slider.setMaximum(30000);
      dofDOFParam6Slider.setLocation(new Point(686, 2));
      dofDOFParam6Slider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam6Slider.setBounds(664, 98, 220, 24);
      dofDOFParam6Slider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFParam6Slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFParam6Slider_stateChanged(e);
          }
        }
      });
      panel_118.add(dofDOFParam6Slider);

      dofDOFParam2Lbl = new JLabel();
      dofDOFParam2Lbl.setText("Param 2");
      dofDOFParam2Lbl.setSize(new Dimension(94, 22));
      dofDOFParam2Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam2Lbl.setName("dofDOFParam2Lbl");
      dofDOFParam2Lbl.setLocation(new Point(488, 2));
      dofDOFParam2Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam2Lbl.setBounds(466, 6, 94, 22);
      panel_118.add(dofDOFParam2Lbl);

      dofDOFParam2REd = new JWFNumberField();
      dofDOFParam2REd.setName("dofDOFParam2REd");
      dofDOFParam2REd.setValueStep(0.05);
      dofDOFParam2REd.setText("");
      dofDOFParam2REd.setSize(new Dimension(100, 24));
      dofDOFParam2REd.setPreferredSize(new Dimension(100, 24));
      dofDOFParam2REd.setMinValue(-10000.0);
      dofDOFParam2REd.setMaxValue(10000.0);
      dofDOFParam2REd.setLocation(new Point(584, 2));
      dofDOFParam2REd.setLinkedMotionControlName("dofDOFParam2Slider");
      dofDOFParam2REd.setLinkedLabelControlName("dofDOFParam2Lbl");
      dofDOFParam2REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      dofDOFParam2REd.setEditable(true);
      dofDOFParam2REd.setBounds(562, 6, 100, 24);
      dofDOFParam2REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      dofDOFParam2REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!dofDOFParam2REd.isMouseAdjusting() || dofDOFParam2REd.getMouseChangeCount() == 0) {
              if (!dofDOFParam2Slider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().dofDOFParam2REd_changed();
          }
        }
      });
      panel_118.add(dofDOFParam2REd);

      dofDOFParam2Slider = new JSlider();
      dofDOFParam2Slider.setValue(0);
      dofDOFParam2Slider.setSize(new Dimension(220, 19));
      dofDOFParam2Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam2Slider.setName("dofDOFParam2Slider");
      dofDOFParam2Slider.setMinimum(-30000);
      dofDOFParam2Slider.setMaximum(30000);
      dofDOFParam2Slider.setLocation(new Point(686, 2));
      dofDOFParam2Slider.setFont(new Font("Dialog", Font.BOLD, 10));
      dofDOFParam2Slider.setBounds(664, 6, 220, 24);
      dofDOFParam2Slider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      dofDOFParam2Slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().dofDOFParam2Slider_stateChanged(e);
          }
        }
      });
      panel_118.add(dofDOFParam2Slider);
    }
    return panel_118;
  }

  public JComboBox getDofDOFShapeCmb() {
    return dofDOFShapeCmb;
  }

  public JLabel getDofDOFScaleLbl() {
    return dofDOFScaleLbl;
  }

  public JLabel getDofDOFAngleLbl() {
    return dofDOFAngleLbl;
  }

  public JLabel getDofDOFFadeLbl() {
    return dofDOFFadeLbl;
  }

  public JLabel getDofDOFParam1Lbl() {
    return dofDOFParam1Lbl;
  }

  public JLabel getDofDOFParam2Lbl() {
    return dofDOFParam2Lbl;
  }

  public JLabel getDofDOFParam3Lbl() {
    return dofDOFParam3Lbl;
  }

  public JLabel getDofDOFParam4Lbl() {
    return dofDOFParam4Lbl;
  }

  public JLabel getDofDOFParam5Lbl() {
    return dofDOFParam5Lbl;
  }

  public JLabel getDofDOFParam6Lbl() {
    return dofDOFParam6Lbl;
  }

  public JSlider getDofDOFScaleSlider() {
    return dofDOFScaleSlider;
  }

  public JSlider getDofDOFAngleSlider() {
    return dofDOFAngleSlider;
  }

  public JSlider getDofDOFFadeSlider() {
    return dofDOFFadeSlider;
  }

  public JWFNumberField getDofDOFScaleREd() {
    return dofDOFScaleREd;
  }

  public JSlider getDofDOFParam1Slider() {
    return dofDOFParam1Slider;
  }

  public JSlider getDofDOFParam2Slider() {
    return dofDOFParam2Slider;
  }

  public JSlider getDofDOFParam3Slider() {
    return dofDOFParam3Slider;
  }

  public JSlider getDofDOFParam4Slider() {
    return dofDOFParam4Slider;
  }

  public JSlider getDofDOFParam5Slider() {
    return dofDOFParam5Slider;
  }

  public JSlider getDofDOFParam6Slider() {
    return dofDOFParam6Slider;
  }

  public JWFNumberField getDofDOFParam1REd() {
    return dofDOFParam1REd;
  }

  public JWFNumberField getDofDOFParam2REd() {
    return dofDOFParam2REd;
  }

  public JWFNumberField getDofDOFParam3REd() {
    return dofDOFParam3REd;
  }

  public JWFNumberField getDofDOFParam4REd() {
    return dofDOFParam4REd;
  }

  public JWFNumberField getDofDOFParam5REd() {
    return dofDOFParam5REd;
  }

  public JWFNumberField getDofDOFParam6REd() {
    return dofDOFParam6REd;
  }

  public JWFNumberField getDofDOFAngleREd() {
    return dofDOFAngleREd;
  }

  public JWFNumberField getDofDOFFadeREd() {
    return dofDOFFadeREd;
  }

  public JCheckBox getBatchRenderOverrideCBx() {
    return batchRenderOverrideCBx;
  }

  public JButton getBatchRenderShowImageBtn() {
    return batchRenderShowImageBtn;
  }

  private JButton getBokehBtn() {
    if (bokehBtn == null) {
      bokehBtn = new JButton();
      bokehBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.bokehBtn_clicked();
        }
      });
      bokehBtn.setToolTipText("Add random bokeh-like-effects (DOF)");
      bokehBtn.setText("DOF");
      bokehBtn.setPreferredSize(new Dimension(72, 24));
      bokehBtn.setMnemonic(KeyEvent.VK_K);
      bokehBtn.setFont(new Font("Dialog", Font.BOLD, 9));
      bokehBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/games-config-background.png")));
    }
    return bokehBtn;
  }
} //  @jve:decl-index=0:visual-constraint="10,10"

