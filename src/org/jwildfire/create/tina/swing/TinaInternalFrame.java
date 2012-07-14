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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.swing.TinaController.NonlinearControlsRow;
import org.jwildfire.swing.StandardErrorHandler;

public class TinaInternalFrame extends JInternalFrame {
  private TinaController tinaController; //  @jve:decl-index=0:
  private NonlinearControlsRow[] nonlinearControlsRows;//  @jve:decl-index=0:
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null; //  @jve:decl-index=0:visual-constraint="10,10"

  private JPanel rootPanel = null;

  private JPanel tinaNorthPanel = null;

  private JPanel tinaWestPanel = null;

  private JPanel tinaEastPanel = null;

  private JPanel tinaSouthPanel = null;

  private JPanel tinaCenterPanel = null;

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

  private JButton renderImageButton = null;

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

  private JWFNumberField tinaBGColorRedREd = null;

  private JWFNumberField tinaBGColorGreenREd = null;

  private JWFNumberField tinaBGColorBlueREd = null;

  private JSlider tinaBGColorRedSlider = null;

  private JSlider tinaBGColorGreenSlider = null;

  private JSlider tinaBGColorBlueSlider = null;

  private JWFNumberField tinaContrastREd = null;

  private JWFNumberField tinaGammaREd = null;

  private JWFNumberField tinaVibrancyREd = null;

  private JWFNumberField tinaFilterRadiusREd = null;

  private JWFNumberField tinaGammaThresholdREd = null;

  private JSlider tinaContrastSlider = null;

  private JSlider tinaGammaSlider = null;

  private JSlider tinaVibrancySlider = null;

  private JSlider tinaFilterRadiusSlider = null;

  private JSlider tinaGammaThresholdSlider = null;

  private JButton tinaAddTransformationButton = null;

  private JTabbedPane tinaEastTabbedPane = null;

  private JPanel tinaTransformationsPanel = null;

  private JTabbedPane tinaWestTabbedPane = null;

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
  private JTextField affineRotateAmountREd = null;
  private JPanel transformationsNorthPanel = null;
  private JPanel trnsformationsEastPanel = null;
  private JSplitPane transformationsSplitPane = null;
  private JTextField affineScaleAmountREd = null;
  private JButton affineMoveUpButton = null;
  private JButton affineMoveDownButton = null;
  private JButton affineMoveLeftButton = null;
  private JButton affineMoveRightButton = null;
  private JTextField affineMoveAmountREd = null;
  private JPanel createRandomPointsPanel = null;
  private JPanel createPaletteTablePanel = null;
  private JPanel randomBatchPanel = null;
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
  private JTextField swfAnimatorFramesREd = null;
  private JLabel animateFramesLbl = null;
  private JLabel animateGlobalScriptLbl = null;
  private JComboBox swfAnimatorGlobalScriptCmb = null;
  private JLabel animateXFormScriptLbl = null;
  private JComboBox swfAnimatorXFormScriptCmb = null;
  private JPanel triangleOperationsPanel = null;
  private JToggleButton mouseTransformMoveButton = null;
  private JToggleButton mouseTransformRotateButton = null;
  private JToggleButton mouseTransformScaleButton = null;
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
  private JButton mouseTransformZoomInButton = null;
  private JButton mouseTransformZoomOutButton = null;
  private JLabel editSpaceLbl3 = null;
  private JToggleButton toggleTrianglesButton = null;
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
  private JWFNumberField tinaCameraZPosREd = null;
  private JSlider tinaCameraZPosSlider = null;
  private JSlider tinaCameraDOFSlider = null;
  private JWFNumberField tinaCameraDOFREd = null;
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
  private JToggleButton darkTrianglesToggleButton = null;
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
  private JButton runScriptButton = null;
  private JToggleButton affineScaleXButton = null;
  private JToggleButton affineScaleYButton = null;
  private JButton randomizeColorsButton = null;
  private JLabel tinaPaletteSwapRGBLbl = null;
  private JWFNumberField tinaPaletteSwapRGBREd = null;
  private JSlider tinaPaletteSwapRGBSlider = null;
  private JPanel gradientLibraryPanel = null;
  private JPanel gradientLibrarySouthPanel = null;
  private JPanel gradientLibraryCenterPanel = null;
  private JComboBox gradientLibraryGradientCmb = null;

  /**
   * This is the xxx default constructor
   */
  public TinaInternalFrame() {
    super();
    initialize();
    // Looks only good for the Welcome screen
    //    setColors(this);
  }

  /*
    private void setColors(Container pContainer) {
      pContainer.setBackground(new Color(0, 0, 0));
      pContainer.setForeground(new Color(192, 192, 192));
      for (Component comp : pContainer.getComponents()) {
        if (comp instanceof Container) {
          setColors((Container) comp);
        }
        else {
          comp.setBackground(new Color(0, 0, 0));
          comp.setForeground(new Color(192, 192, 192));
        }
      }
    }
  */
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
      rootPanel.add(getTinaWestPanel(), BorderLayout.WEST);
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
      tinaNorthPanel.setLayout(null);
      tinaNorthPanel.setPreferredSize(new Dimension(0, 66));

      randomStyleLbl = new JLabel();
      randomStyleLbl.setPreferredSize(new Dimension(94, 22));
      randomStyleLbl.setText("Random generator");
      randomStyleLbl.setBounds(new Rectangle(135, 7, 94, 22));
      randomStyleLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaNorthPanel.add(getRandomBatchButton(), null);
      tinaNorthPanel.add(randomStyleLbl, null);
      tinaNorthPanel.add(getRandomStyleCmb(), null);
      tinaNorthPanel.add(getNewFlameButton(), null);
      tinaNorthPanel.add(getLoadFromClipboardFlameButton(), null);
      tinaNorthPanel.add(getTinaLoadFlameButton(), null);
      tinaNorthPanel.add(getTinaSaveFlameButton(), null);
      tinaNorthPanel.add(getSaveFlameToClipboardButton(), null);
      tinaNorthPanel.add(getRenderImageButton(), null);

      resolutionProfileCmb = new JComboBox();
      resolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.resolutionProfileCmb_changed();
          }
        }
      });
      resolutionProfileCmb.setPreferredSize(new Dimension(125, 22));
      resolutionProfileCmb.setMaximumRowCount(32);
      resolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      resolutionProfileCmb.setBounds(new Rectangle(231, 7, 125, 22));
      resolutionProfileCmb.setBounds(944, 7, 125, 22);
      tinaNorthPanel.add(resolutionProfileCmb);

      qualityProfileCmb = new JComboBox();
      qualityProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.saveUndoPoint();
            tinaController.qualityProfileCmb_changed();
          }
        }
      });
      qualityProfileCmb.setPreferredSize(new Dimension(125, 22));
      qualityProfileCmb.setMaximumRowCount(32);
      qualityProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      qualityProfileCmb.setBounds(new Rectangle(231, 7, 125, 22));
      qualityProfileCmb.setBounds(944, 35, 125, 22);
      tinaNorthPanel.add(qualityProfileCmb);
      tinaNorthPanel.add(getQualityProfileBtn());
      tinaNorthPanel.add(getResolutionProfileBtn());

    }
    return tinaNorthPanel;
  }

  /**
   * This method initializes tinaWestPanel  
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaWestPanel() {
    if (tinaWestPanel == null) {
      tinaWestPanel = new JPanel();
      tinaWestPanel.setLayout(new BorderLayout());
      tinaWestPanel.setPreferredSize(new Dimension(200, 0));
      tinaWestPanel.setVisible(true);
      tinaWestPanel.add(getTinaWestTabbedPane(), BorderLayout.CENTER);
    }
    return tinaWestPanel;
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
      tinaSouthPanel.setPreferredSize(new Dimension(0, 152));
      tinaSouthPanel.add(getTinaSouthTabbedPane(), BorderLayout.CENTER);
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
      tinaCenterPanel.add(getRandomBatchPanel(), BorderLayout.SOUTH);
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
      tinaSouthTabbedPane.addTab("Camera", null, getTinaCameraPanel(), null);
      tinaSouthTabbedPane.addTab("Coloring", null, getTinaColoringPanel(), null);
      tinaSouthTabbedPane.addTab("Shading", null, getShadingPanel(), null);
      tinaSouthTabbedPane.addTab("Pseudo3D-Shading", null, getPseudo3DShadingPanel(), null);
      tinaSouthTabbedPane.addTab("Blur-Shading", null, getBlurShadingPanel(), null);
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
      tinaCameraDOFLbl = new JLabel();
      tinaCameraDOFLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraDOFLbl.setText("Depth of field");
      tinaCameraDOFLbl.setSize(new Dimension(94, 22));
      tinaCameraDOFLbl.setLocation(new Point(488, 100));
      tinaCameraDOFLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraZPosLbl = new JLabel();
      tinaCameraZPosLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraZPosLbl.setText("Camera distance");
      tinaCameraZPosLbl.setSize(new Dimension(94, 22));
      tinaCameraZPosLbl.setLocation(new Point(4, 100));
      tinaCameraZPosLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPixelsPerUnitLbl = new JLabel();
      tinaPixelsPerUnitLbl.setText("Pixels per unit");
      tinaPixelsPerUnitLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPixelsPerUnitLbl.setLocation(new Point(488, 76));
      tinaPixelsPerUnitLbl.setSize(new Dimension(94, 22));
      tinaPixelsPerUnitLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraZoomLbl = new JLabel();
      tinaCameraZoomLbl.setText("Zoom");
      tinaCameraZoomLbl.setLocation(new Point(488, 52));
      tinaCameraZoomLbl.setSize(new Dimension(94, 22));
      tinaCameraZoomLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraZoomLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCentreYLbl = new JLabel();
      tinaCameraCentreYLbl.setText("CentreY");
      tinaCameraCentreYLbl.setLocation(new Point(488, 28));
      tinaCameraCentreYLbl.setSize(new Dimension(94, 22));
      tinaCameraCentreYLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraCentreYLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCentreXLbl = new JLabel();
      tinaCameraCentreXLbl.setText("CentreX");
      tinaCameraCentreXLbl.setLocation(new Point(488, 4));
      tinaCameraCentreXLbl.setSize(new Dimension(94, 22));
      tinaCameraCentreXLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraCentreXLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraPerspectiveLbl = new JLabel();
      tinaCameraPerspectiveLbl.setText("Perspective");
      tinaCameraPerspectiveLbl.setLocation(new Point(4, 76));
      tinaCameraPerspectiveLbl.setSize(new Dimension(94, 22));
      tinaCameraPerspectiveLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraPerspectiveLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraYawLbl = new JLabel();
      tinaCameraYawLbl.setText("Yaw");
      tinaCameraYawLbl.setLocation(new Point(4, 52));
      tinaCameraYawLbl.setSize(new Dimension(94, 22));
      tinaCameraYawLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraYawLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraPitchLbl = new JLabel();
      tinaCameraPitchLbl.setText("Pitch");
      tinaCameraPitchLbl.setLocation(new Point(4, 28));
      tinaCameraPitchLbl.setSize(new Dimension(94, 22));
      tinaCameraPitchLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraPitchLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraRollLbl = new JLabel();
      tinaCameraRollLbl.setText("Roll");
      tinaCameraRollLbl.setLocation(new Point(4, 4));
      tinaCameraRollLbl.setSize(new Dimension(94, 22));
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
      tinaCameraPanel.add(tinaCameraZPosLbl, null);
      tinaCameraPanel.add(getTinaCameraZPosREd(), null);
      tinaCameraPanel.add(getTinaCameraZPosSlider(), null);
      tinaCameraPanel.add(getTinaCameraDOFSlider(), null);
      tinaCameraPanel.add(getTinaCameraDOFREd(), null);
      tinaCameraPanel.add(tinaCameraDOFLbl, null);
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
      tinaBGColorLbl = new JLabel();
      tinaBGColorLbl.setText("Background color");
      tinaBGColorLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaBGColorLbl.setLocation(new Point(488, 76));
      tinaBGColorLbl.setSize(new Dimension(94, 22));
      tinaBGColorLbl.setPreferredSize(new Dimension(94, 22));
      tinaGammaThresholdLbl = new JLabel();
      tinaGammaThresholdLbl.setText("Gamma threshold");
      tinaGammaThresholdLbl.setLocation(new Point(4, 76));
      tinaGammaThresholdLbl.setSize(new Dimension(94, 22));
      tinaGammaThresholdLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGammaThresholdLbl.setPreferredSize(new Dimension(94, 22));
      tinaFilterRadiusLbl = new JLabel();
      tinaFilterRadiusLbl.setText("Filter radius");
      tinaFilterRadiusLbl.setLocation(new Point(488, 4));
      tinaFilterRadiusLbl.setSize(new Dimension(94, 22));
      tinaFilterRadiusLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaFilterRadiusLbl.setPreferredSize(new Dimension(94, 22));
      tinaVibrancyLbl = new JLabel();
      tinaVibrancyLbl.setText("Vibrancy");
      tinaVibrancyLbl.setLocation(new Point(4, 100));
      tinaVibrancyLbl.setSize(new Dimension(94, 22));
      tinaVibrancyLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaVibrancyLbl.setPreferredSize(new Dimension(94, 22));
      tinaGammaLbl = new JLabel();
      tinaGammaLbl.setText("Gamma");
      tinaGammaLbl.setLocation(new Point(4, 52));
      tinaGammaLbl.setSize(new Dimension(94, 22));
      tinaGammaLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGammaLbl.setPreferredSize(new Dimension(94, 22));
      tinaContrastLbl = new JLabel();
      tinaContrastLbl.setText("Contrast");
      tinaContrastLbl.setLocation(new Point(4, 28));
      tinaContrastLbl.setSize(new Dimension(94, 22));
      tinaContrastLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaContrastLbl.setPreferredSize(new Dimension(94, 22));
      tinaBrightnessLbl = new JLabel();
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
      tinaColoringPanel.add(tinaFilterRadiusLbl, null);
      tinaColoringPanel.add(tinaGammaThresholdLbl, null);
      tinaColoringPanel.add(tinaBGColorLbl, null);
      tinaColoringPanel.add(getTinaBGColorRedREd(), null);
      tinaColoringPanel.add(getTinaBGColorGreenREd(), null);
      tinaColoringPanel.add(getTinaBGColorBlueREd(), null);
      tinaColoringPanel.add(getTinaBGColorRedSlider(), null);
      tinaColoringPanel.add(getTinaBGColorGreenSlider(), null);
      tinaColoringPanel.add(getTinaBGColorBlueSlider(), null);
      tinaColoringPanel.add(getTinaContrastREd(), null);
      tinaColoringPanel.add(getTinaGammaREd(), null);
      tinaColoringPanel.add(getTinaVibrancyREd(), null);
      tinaColoringPanel.add(getTinaFilterRadiusREd(), null);
      tinaColoringPanel.add(getTinaGammaThresholdREd(), null);
      tinaColoringPanel.add(getTinaContrastSlider(), null);
      tinaColoringPanel.add(getTinaGammaSlider(), null);
      tinaColoringPanel.add(getTinaVibrancySlider(), null);
      tinaColoringPanel.add(getTinaFilterRadiusSlider(), null);
      tinaColoringPanel.add(getTinaGammaThresholdSlider(), null);
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
      tinaCameraRollREd.setValueStep(1.0);
      tinaCameraRollREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraRollREd.isMouseAdjusting() || tinaCameraRollREd.getMouseChangeCount() == 0) {
            if (!tinaCameraRollSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.cameraRollREd_changed();
        }
      });
      tinaCameraRollREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraRollREd.setLocation(new Point(100, 4));
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
      tinaCameraPitchREd.setValueStep(1.0);
      tinaCameraPitchREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraPitchREd.isMouseAdjusting() || tinaCameraPitchREd.getMouseChangeCount() == 0) {
            if (!tinaCameraPitchSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.cameraPitchREd_changed();
        }
      });
      tinaCameraPitchREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraPitchREd.setText("");
      tinaCameraPitchREd.setLocation(new Point(100, 28));
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
      tinaCameraYawREd.setValueStep(1.0);
      tinaCameraYawREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraYawREd.isMouseAdjusting() || tinaCameraYawREd.getMouseChangeCount() == 0) {
            if (!tinaCameraYawSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.cameraYawREd_changed();
        }
      });
      tinaCameraYawREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraYawREd.setText("");
      tinaCameraYawREd.setLocation(new Point(100, 52));
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
          tinaController.cameraPerspectiveREd_changed();
        }
      });
      tinaCameraPerspectiveREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraPerspectiveREd.setText("");
      tinaCameraPerspectiveREd.setLocation(new Point(100, 76));
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
      tinaCameraRollSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraRollSlider.setMaximum(180);
      tinaCameraRollSlider.setLocation(new Point(202, 4));
      tinaCameraRollSlider.setSize(new Dimension(220, 19));
      tinaCameraRollSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraRollSlider.setValue(0);
      tinaCameraRollSlider.setMinimum(-180);
      tinaCameraRollSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraRollSlider_stateChanged(e);
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
      tinaCameraPitchSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraPitchSlider.setMaximum(180);
      tinaCameraPitchSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraPitchSlider.setLocation(new Point(202, 28));
      tinaCameraPitchSlider.setSize(new Dimension(220, 19));
      tinaCameraPitchSlider.setValue(0);
      tinaCameraPitchSlider.setMinimum(-180);
      tinaCameraPitchSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraPitchSlider_stateChanged(e);
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
      tinaCameraYawSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraYawSlider.setMaximum(180);
      tinaCameraYawSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraYawSlider.setLocation(new Point(202, 52));
      tinaCameraYawSlider.setSize(new Dimension(220, 19));
      tinaCameraYawSlider.setValue(0);
      tinaCameraYawSlider.setMinimum(-180);
      tinaCameraYawSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraYawSlider_stateChanged(e);
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
      tinaCameraPerspectiveSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraPerspectiveSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraPerspectiveSlider.setSize(new Dimension(220, 19));
      tinaCameraPerspectiveSlider.setValue(0);
      tinaCameraPerspectiveSlider.setLocation(new Point(202, 76));
      tinaCameraPerspectiveSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraPerspectiveSlider_stateChanged(e);
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
      tinaLoadFlameButton.setText("Load Flame");
      tinaLoadFlameButton.setPreferredSize(new Dimension(125, 24));
      tinaLoadFlameButton.setBounds(new Rectangle(504, 35, 125, 24));
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
      tinaSaveFlameButton.setText("Save Flame");
      tinaSaveFlameButton.setPreferredSize(new Dimension(125, 24));
      tinaSaveFlameButton.setBounds(new Rectangle(643, 35, 125, 24));
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
      tinaRenderFlameButton.setText("R");
      tinaRenderFlameButton.setMnemonic(KeyEvent.VK_R);
      tinaRenderFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaRenderFlameButton.setToolTipText("Render image");
      tinaRenderFlameButton.setPreferredSize(new Dimension(42, 36));
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
  private JButton getRenderImageButton() {
    if (renderImageButton == null) {
      renderImageButton = new JButton();
      renderImageButton.setText("Render image");
      renderImageButton.setPreferredSize(new Dimension(180, 24));
      renderImageButton.setBounds(new Rectangle(780, 7, 152, 52));
      renderImageButton.setFont(new Font("Dialog", Font.BOLD, 10));
      renderImageButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.renderImageButton_actionPerformed();
        }
      });
    }
    return renderImageButton;
  }

  /**
   * This method initializes tinaCameraCentreXREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaCameraCentreXREd() {
    if (tinaCameraCentreXREd == null) {
      tinaCameraCentreXREd = new JWFNumberField();
      tinaCameraCentreXREd.setMouseThreshold(1.0);
      tinaCameraCentreXREd.setValueStep(0.05);
      tinaCameraCentreXREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraCentreXREd.isMouseAdjusting() || tinaCameraCentreXREd.getMouseChangeCount() == 0) {
            if (!tinaCameraCentreXSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.cameraCentreXREd_changed();
        }
      });
      tinaCameraCentreXREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraCentreXREd.setText("");
      tinaCameraCentreXREd.setLocation(new Point(584, 4));
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
      tinaCameraCentreYREd.setMouseThreshold(1.0);
      tinaCameraCentreYREd.setValueStep(0.05);
      tinaCameraCentreYREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraCentreYREd.isMouseAdjusting() || tinaCameraCentreYREd.getMouseChangeCount() == 0) {
            if (!tinaCameraCentreYSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.cameraCentreYREd_changed();
        }
      });
      tinaCameraCentreYREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraCentreYREd.setText("");
      tinaCameraCentreYREd.setLocation(new Point(584, 28));
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
      tinaCameraCentreXSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraCentreXSlider.setMinimum(-25000);
      tinaCameraCentreXSlider.setLocation(new Point(686, 4));
      tinaCameraCentreXSlider.setSize(new Dimension(220, 19));
      tinaCameraCentreXSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCentreXSlider.setValue(0);
      tinaCameraCentreXSlider.setMaximum(25000);
      tinaCameraCentreXSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraCentreXSlider_stateChanged(e);
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
      tinaCameraCentreYSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraCentreYSlider.setMinimum(-25000);
      tinaCameraCentreYSlider.setLocation(new Point(686, 28));
      tinaCameraCentreYSlider.setSize(new Dimension(220, 19));
      tinaCameraCentreYSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCentreYSlider.setValue(0);
      tinaCameraCentreYSlider.setMaximum(25000);
      tinaCameraCentreYSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraCentreYSlider_stateChanged(e);
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
      tinaCameraZoomREd.setMouseThreshold(1.0);
      tinaCameraZoomREd.setValueStep(0.01);
      tinaCameraZoomREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraZoomREd.isMouseAdjusting() || tinaCameraZoomREd.getMouseChangeCount() == 0) {
            if (!tinaCameraZoomSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.cameraZoomREd_changed();
        }
      });
      tinaCameraZoomREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraZoomREd.setText("");
      tinaCameraZoomREd.setLocation(new Point(584, 52));
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
      tinaCameraZoomSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraZoomSlider.setMinimum(100);
      tinaCameraZoomSlider.setLocation(new Point(686, 52));
      tinaCameraZoomSlider.setSize(new Dimension(220, 19));
      tinaCameraZoomSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraZoomSlider.setValue(0);
      tinaCameraZoomSlider.setMaximum(10000);
      tinaCameraZoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraZoomSlider_stateChanged(e);
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
      tinaBrightnessREd.setValueStep(0.05);
      tinaBrightnessREd.setMaxValue(10.0);
      tinaBrightnessREd.setHasMinValue(true);
      tinaBrightnessREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaBrightnessREd.isMouseAdjusting() || tinaBrightnessREd.getMouseChangeCount() == 0) {
            if (!tinaBrightnessSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.brightnessREd_changed();
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
      tinaBrightnessSlider.setMaximum(1000);
      tinaBrightnessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.brightnessSlider_stateChanged(e);
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
      tinaPixelsPerUnitREd.setMouseThreshold(1.0);
      tinaPixelsPerUnitREd.setValueStep(1.0);
      tinaPixelsPerUnitREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPixelsPerUnitREd.isMouseAdjusting() || tinaPixelsPerUnitREd.getMouseChangeCount() == 0) {
            if (!tinaPixelsPerUnitSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.pixelsPerUnitREd_changed();
        }
      });
      tinaPixelsPerUnitREd.setPreferredSize(new Dimension(100, 24));
      tinaPixelsPerUnitREd.setText("");
      tinaPixelsPerUnitREd.setLocation(new Point(584, 76));
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
      tinaPixelsPerUnitSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPixelsPerUnitSlider.setMaximum(1000);
      tinaPixelsPerUnitSlider.setMinimum(0);
      tinaPixelsPerUnitSlider.setValue(0);
      tinaPixelsPerUnitSlider.setSize(new Dimension(220, 19));
      tinaPixelsPerUnitSlider.setLocation(new Point(686, 76));
      tinaPixelsPerUnitSlider.setPreferredSize(new Dimension(220, 19));
      tinaPixelsPerUnitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.pixelsPerUnitSlider_stateChanged(e);
        }
      });
    }
    return tinaPixelsPerUnitSlider;
  }

  /**
   * This method initializes tinaBGColorRedREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaBGColorRedREd() {
    if (tinaBGColorRedREd == null) {
      tinaBGColorRedREd = new JWFNumberField();
      tinaBGColorRedREd.setValueStep(1.0);
      tinaBGColorRedREd.setHasMinValue(true);
      tinaBGColorRedREd.setOnlyIntegers(true);
      tinaBGColorRedREd.setHasMaxValue(true);
      tinaBGColorRedREd.setMaxValue(255.0);
      tinaBGColorRedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaBGColorRedREd.isMouseAdjusting() || tinaBGColorRedREd.getMouseChangeCount() == 0) {
              if (!tinaBGColorRedSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.bgColorRedREd_changed();
          }
        }
      });
      tinaBGColorRedREd.setPreferredSize(new Dimension(56, 24));
      tinaBGColorRedREd.setText("");
      tinaBGColorRedREd.setLocation(new Point(584, 76));
      tinaBGColorRedREd.setSize(new Dimension(56, 24));
      tinaBGColorRedREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaBGColorRedREd;
  }

  /**
   * This method initializes tinaBGColorGreenREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaBGColorGreenREd() {
    if (tinaBGColorGreenREd == null) {
      tinaBGColorGreenREd = new JWFNumberField();
      tinaBGColorGreenREd.setHasMinValue(true);
      tinaBGColorGreenREd.setHasMaxValue(true);
      tinaBGColorGreenREd.setMaxValue(255.0);
      tinaBGColorGreenREd.setOnlyIntegers(true);
      tinaBGColorGreenREd.setValueStep(1.0);
      tinaBGColorGreenREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaBGColorGreenREd.isMouseAdjusting() || tinaBGColorGreenREd.getMouseChangeCount() == 0) {
              if (!tinaBGColorGreenSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.bgColorGreenREd_changed();
          }
        }
      });
      tinaBGColorGreenREd.setPreferredSize(new Dimension(56, 24));
      tinaBGColorGreenREd.setText("");
      tinaBGColorGreenREd.setLocation(new Point(712, 76));
      tinaBGColorGreenREd.setSize(new Dimension(56, 24));
      tinaBGColorGreenREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaBGColorGreenREd;
  }

  /**
   * This method initializes tinaBGColorBlueREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaBGColorBlueREd() {
    if (tinaBGColorBlueREd == null) {
      tinaBGColorBlueREd = new JWFNumberField();
      tinaBGColorBlueREd.setOnlyIntegers(true);
      tinaBGColorBlueREd.setValueStep(1.0);
      tinaBGColorBlueREd.setMaxValue(255.0);
      tinaBGColorBlueREd.setHasMinValue(true);
      tinaBGColorBlueREd.setHasMaxValue(true);
      tinaBGColorBlueREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaBGColorBlueREd.isMouseAdjusting() || tinaBGColorBlueREd.getMouseChangeCount() == 0) {
              if (!tinaBGColorBlueSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.bgBGColorBlueREd_changed();
          }
        }
      });
      tinaBGColorBlueREd.setPreferredSize(new Dimension(56, 24));
      tinaBGColorBlueREd.setText("");
      tinaBGColorBlueREd.setLocation(new Point(840, 76));
      tinaBGColorBlueREd.setSize(new Dimension(56, 24));
      tinaBGColorBlueREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaBGColorBlueREd;
  }

  /**
   * This method initializes tinaBGColorRedSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaBGColorRedSlider() {
    if (tinaBGColorRedSlider == null) {
      tinaBGColorRedSlider = new JSlider();
      tinaBGColorRedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaBGColorRedSlider.setMaximum(255);
      tinaBGColorRedSlider.setMinimum(0);
      tinaBGColorRedSlider.setValue(0);
      tinaBGColorRedSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaBGColorRedSlider.setLocation(new Point(641, 76));
      tinaBGColorRedSlider.setSize(new Dimension(56, 19));
      tinaBGColorRedSlider.setPreferredSize(new Dimension(56, 19));
      tinaBGColorRedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.bgColorRedSlider_stateChanged(e);
        }
      });
    }
    return tinaBGColorRedSlider;
  }

  /**
   * This method initializes tinaBGColorGreenSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaBGColorGreenSlider() {
    if (tinaBGColorGreenSlider == null) {
      tinaBGColorGreenSlider = new JSlider();
      tinaBGColorGreenSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaBGColorGreenSlider.setMaximum(255);
      tinaBGColorGreenSlider.setMinimum(0);
      tinaBGColorGreenSlider.setValue(0);
      tinaBGColorGreenSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaBGColorGreenSlider.setLocation(new Point(770, 76));
      tinaBGColorGreenSlider.setSize(new Dimension(56, 19));
      tinaBGColorGreenSlider.setPreferredSize(new Dimension(56, 19));
      tinaBGColorGreenSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.bgColorGreenSlider_stateChanged(e);
        }
      });
    }
    return tinaBGColorGreenSlider;
  }

  /**
   * This method initializes tinaBGColorBlueSlider  
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaBGColorBlueSlider() {
    if (tinaBGColorBlueSlider == null) {
      tinaBGColorBlueSlider = new JSlider();
      tinaBGColorBlueSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaBGColorBlueSlider.setMaximum(255);
      tinaBGColorBlueSlider.setMinimum(0);
      tinaBGColorBlueSlider.setValue(0);
      tinaBGColorBlueSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaBGColorBlueSlider.setLocation(new Point(898, 76));
      tinaBGColorBlueSlider.setSize(new Dimension(56, 19));
      tinaBGColorBlueSlider.setPreferredSize(new Dimension(56, 19));
      tinaBGColorBlueSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.bgColorBlueSlider_stateChanged(e);
        }
      });
    }
    return tinaBGColorBlueSlider;
  }

  /**
   * This method initializes tinaContrastREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaContrastREd() {
    if (tinaContrastREd == null) {
      tinaContrastREd = new JWFNumberField();
      tinaContrastREd.setValueStep(0.05);
      tinaContrastREd.setHasMinValue(true);
      tinaContrastREd.setMaxValue(5.0);
      tinaContrastREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaContrastREd.isMouseAdjusting() || tinaContrastREd.getMouseChangeCount() == 0) {
              if (!tinaContrastSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.contrastREd_changed();
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
      tinaGammaREd.setHasMinValue(true);
      tinaGammaREd.setMaxValue(10.0);
      tinaGammaREd.setValueStep(0.05);
      tinaGammaREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaGammaREd.isMouseAdjusting() || tinaGammaREd.getMouseChangeCount() == 0) {
              if (!tinaGammaSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.gammaREd_changed();
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
      tinaVibrancyREd.setValueStep(0.05);
      tinaVibrancyREd.setHasMinValue(true);
      tinaVibrancyREd.setHasMaxValue(true);
      tinaVibrancyREd.setMaxValue(1.0);
      tinaVibrancyREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaVibrancyREd.isMouseAdjusting() || tinaVibrancyREd.getMouseChangeCount() == 0) {
              if (!tinaVibrancySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.vibrancyREd_changed();
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
   * This method initializes tinaFilterRadiusREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaFilterRadiusREd() {
    if (tinaFilterRadiusREd == null) {
      tinaFilterRadiusREd = new JWFNumberField();
      tinaFilterRadiusREd.setValueStep(0.05);
      tinaFilterRadiusREd.setMaxValue(5.0);
      tinaFilterRadiusREd.setHasMinValue(true);
      tinaFilterRadiusREd.setHasMaxValue(true);
      tinaFilterRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaFilterRadiusREd.isMouseAdjusting() || tinaFilterRadiusREd.getMouseChangeCount() == 0) {
              if (!tinaFilterRadiusSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.filterRadiusREd_changed();
          }
        }
      });
      tinaFilterRadiusREd.setPreferredSize(new Dimension(100, 24));
      tinaFilterRadiusREd.setText("");
      tinaFilterRadiusREd.setLocation(new Point(584, 4));
      tinaFilterRadiusREd.setSize(new Dimension(100, 24));
      tinaFilterRadiusREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaFilterRadiusREd;
  }

  /**
   * This method initializes tinaOversampleREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JWFNumberField getTinaGammaThresholdREd() {
    if (tinaGammaThresholdREd == null) {
      tinaGammaThresholdREd = new JWFNumberField();
      tinaGammaThresholdREd.setValueStep(0.005);
      tinaGammaThresholdREd.setMaxValue(1.0);
      tinaGammaThresholdREd.setHasMinValue(true);
      tinaGammaThresholdREd.setHasMaxValue(true);
      tinaGammaThresholdREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaGammaThresholdREd.isMouseAdjusting() || tinaGammaThresholdREd.getMouseChangeCount() == 0) {
              if (!tinaGammaThresholdSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.gammaThresholdREd_changed();
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
          tinaController.contrastSlider_stateChanged(e);
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
          tinaController.gammaSlider_stateChanged(e);
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
          tinaController.vibrancySlider_stateChanged(e);
        }
      });
    }
    return tinaVibrancySlider;
  }

  /**
   * This method initializes tinaFilterRadiusSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaFilterRadiusSlider() {
    if (tinaFilterRadiusSlider == null) {
      tinaFilterRadiusSlider = new JSlider();
      tinaFilterRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaFilterRadiusSlider.setMaximum(500);
      tinaFilterRadiusSlider.setMinimum(0);
      tinaFilterRadiusSlider.setValue(0);
      tinaFilterRadiusSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaFilterRadiusSlider.setSize(new Dimension(220, 19));
      tinaFilterRadiusSlider.setLocation(new Point(686, 4));
      tinaFilterRadiusSlider.setPreferredSize(new Dimension(220, 19));
      tinaFilterRadiusSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.filterRadiusSlider_stateChanged(e);
        }
      });
    }
    return tinaFilterRadiusSlider;
  }

  /**
   * This method initializes tinaOversampleSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaGammaThresholdSlider() {
    if (tinaGammaThresholdSlider == null) {
      tinaGammaThresholdSlider = new JSlider();
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
          tinaController.gammaThresholdSlider_stateChanged(e);
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
      tinaAddTransformationButton.setPreferredSize(new Dimension(81, 24));
      tinaAddTransformationButton.setToolTipText("Add new triangle");
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
      tinaEastTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaEastTabbedPane.addTab("Transformations", null, getTinaTransformationsPanel(), null);
      tinaEastTabbedPane.addTab("Script", null, getScriptPanel(), null);
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
   * This method initializes tinaWestTabbedPane 
   *  
   * @return javax.swing.JTabbedPane  
   */
  private JTabbedPane getTinaWestTabbedPane() {
    if (tinaWestTabbedPane == null) {
      tinaWestTabbedPane = new JTabbedPane();
      tinaWestTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaWestTabbedPane.addTab("Gradient", null, getTinaPalettePanel(), null);
    }
    return tinaWestTabbedPane;
  }

  /**
   * This method initializes tinaPalettePanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaPalettePanel() {
    if (tinaPalettePanel == null) {
      tinaPaletteRandomPointsLbl = new JLabel();
      tinaPaletteRandomPointsLbl.setText("Random points");
      tinaPaletteRandomPointsLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteRandomPointsLbl.setPreferredSize(new Dimension(110, 22));
      tinaPalettePanel = new JPanel();
      tinaPalettePanel.setLayout(new BorderLayout());
      tinaPalettePanel.add(getTinaPaletteSubNorthPanel(), BorderLayout.NORTH);
      tinaPalettePanel.add(getTinaPaletteSubCenterPanel(), BorderLayout.CENTER);
      tinaPalettePanel.add(getTinaPaletteSubSouthPanel(), BorderLayout.SOUTH);
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
      tinaDeleteTransformationButton.setPreferredSize(new Dimension(81, 24));
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
      tinaDuplicateTransformationButton.setText("Duplicate");
      tinaDuplicateTransformationButton.setPreferredSize(new Dimension(81, 24));
      tinaDuplicateTransformationButton.setToolTipText("Duplicate triangle");
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
      tinaTransformationsTabbedPane.addTab("Affine", null, getTinaAffineTransformationPanel(), null);
      tinaTransformationsTabbedPane.addTab("Nonlinear", null, getTinaVariationPanel(), null);
      tinaTransformationsTabbedPane.addTab("Rel. weights", null, getTinaModifiedWeightsPanel(), null);
      tinaTransformationsTabbedPane.addTab("Color", null, getTinaTransformationColorPanel(), null);
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
      affineC21Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC21Lbl.setText("O2");
      affineC21Lbl.setLocation(new Point(212, 30));
      affineC21Lbl.setSize(new Dimension(20, 22));
      affineC21Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC21Lbl.setPreferredSize(new Dimension(24, 22));
      affineC20Lbl = new JLabel();
      affineC20Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC20Lbl.setText("O1");
      affineC20Lbl.setLocation(new Point(212, 6));
      affineC20Lbl.setSize(new Dimension(20, 22));
      affineC20Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC20Lbl.setPreferredSize(new Dimension(24, 22));
      affineC11Lbl = new JLabel();
      affineC11Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC11Lbl.setText("Y2");
      affineC11Lbl.setLocation(new Point(105, 30));
      affineC11Lbl.setSize(new Dimension(20, 22));
      affineC11Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC11Lbl.setPreferredSize(new Dimension(24, 22));
      affineC10Lbl = new JLabel();
      affineC10Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC10Lbl.setText("Y1");
      affineC10Lbl.setLocation(new Point(104, 6));
      affineC10Lbl.setSize(new Dimension(20, 22));
      affineC10Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC10Lbl.setPreferredSize(new Dimension(24, 22));
      affineC01Lbl = new JLabel();
      affineC01Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC01Lbl.setText("X2");
      affineC01Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC01Lbl.setLocation(new Point(6, 30));
      affineC01Lbl.setSize(new Dimension(20, 22));
      affineC01Lbl.setPreferredSize(new Dimension(24, 22));
      affineC00Lbl = new JLabel();
      affineC00Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC00Lbl.setText("X1");
      affineC00Lbl.setLocation(new Point(6, 6));
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
      tinaAffineTransformationPanel.add(getAffineMoveAmountREd(), null);
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
      affinePreserveZButton.setText("Preserve Z-coordinate");
      affinePreserveZButton.setSize(new Dimension(138, 24));
      affinePreserveZButton.setPreferredSize(new Dimension(136, 24));
      affinePreserveZButton.setLocation(new Point(4, 181));
      affinePreserveZButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affinePreserveZButton.setBounds(4, 214, 138, 24);
      tinaAffineTransformationPanel.add(affinePreserveZButton);
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
      xFormDrawModeLbl.setLocation(new Point(4, 82));
      xFormDrawModeLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormOpacityLbl = new JLabel();
      xFormOpacityLbl.setPreferredSize(new Dimension(64, 22));
      xFormOpacityLbl.setText("Opacity");
      xFormOpacityLbl.setSize(new Dimension(64, 22));
      xFormOpacityLbl.setLocation(new Point(4, 56));
      xFormOpacityLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormSymmetryLbl = new JLabel();
      xFormSymmetryLbl.setPreferredSize(new Dimension(64, 22));
      xFormSymmetryLbl.setText("Symmetry");
      xFormSymmetryLbl.setSize(new Dimension(64, 22));
      xFormSymmetryLbl.setLocation(new Point(4, 30));
      xFormSymmetryLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      xFormColorLbl = new JLabel();
      xFormColorLbl.setPreferredSize(new Dimension(64, 22));
      xFormColorLbl.setText("Color");
      xFormColorLbl.setSize(new Dimension(64, 22));
      xFormColorLbl.setLocation(new Point(4, 4));
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
      tinaPaletteRandomPointsREd.setPreferredSize(new Dimension(55, 22));
      tinaPaletteRandomPointsREd.setText("7");
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
      tinaRandomPaletteButton.setText("Random Gradient");
      tinaRandomPaletteButton.setPreferredSize(new Dimension(180, 24));
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
      distributeColorsButton.setBounds(6, 56, 180, 24);
      distributeColorsButton.setText("Distribute colors");
      distributeColorsButton.setFont(new Font("Dialog", Font.BOLD, 10));
      distributeColorsButton.setPreferredSize(new Dimension(180, 24));
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
      tinaPaletteSubNorthPanel.setPreferredSize(new Dimension(0, 42));
      tinaPaletteSubNorthPanel.add(getTinaPaletteImgPanel(), BorderLayout.CENTER);
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
      tinaPaletteSubSouthPanel.setPreferredSize(new Dimension(0, 86));
      tinaPaletteSubSouthPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      tinaPaletteSubSouthPanel.setLayout(null);
      tinaPaletteSubSouthPanel.add(getRandomizeColorsButton());
      tinaPaletteSubSouthPanel.add(getDistributeColorsButton());
      tinaPaletteSubSouthPanel.add(getTinaPaletteShiftSlider());
      tinaPaletteSubSouthPanel.add(getTinaPaletteShiftREd());
      tinaPaletteShiftLbl = new JLabel();
      tinaPaletteShiftLbl.setBounds(6, 6, 43, 22);
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
      tinaPaletteSubTabbedPane.setToolTipText("");
      tinaPaletteSubTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSubTabbedPane.addTab("Create", null, getTinaPaletteCreatePanel(), null);
      tinaPaletteSubTabbedPane.addTab("Library", null, getGradientLibraryPanel(), null);
      tinaPaletteSubTabbedPane.addTab("Transform", null, getTinaPaletteBalancingPanel(), null);
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
      FlowLayout flowLayout2 = new FlowLayout();
      flowLayout2.setAlignment(FlowLayout.LEFT);
      tinaPaletteCreatePanel = new JPanel();
      tinaPaletteCreatePanel.setLayout(flowLayout2);
      tinaPaletteCreatePanel.add(getTinaRandomPaletteButton(), null);
      tinaPaletteCreatePanel.add(getCreateRandomPointsPanel(), null);
      tinaPaletteCreatePanel.add(getCreatePaletteTablePanel(), null);
      tinaPaletteCreatePanel.add(getTinaGrabPaletteFromFlameButton(), null);
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
      tinaPaletteSwapRGBLbl = new JLabel();
      tinaPaletteSwapRGBLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteSwapRGBLbl.setText("Swap RGB");
      tinaPaletteSwapRGBLbl.setSize(new Dimension(56, 22));
      tinaPaletteSwapRGBLbl.setLocation(new Point(4, 238));
      tinaPaletteSwapRGBLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBrightnessLbl = new JLabel();
      tinaPaletteBrightnessLbl.setText("Brightness");
      tinaPaletteBrightnessLbl.setSize(new Dimension(56, 22));
      tinaPaletteBrightnessLbl.setLocation(new Point(4, 212));
      tinaPaletteBrightnessLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBrightnessLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteGammaLbl = new JLabel();
      tinaPaletteGammaLbl.setText("Gamma");
      tinaPaletteGammaLbl.setSize(new Dimension(56, 22));
      tinaPaletteGammaLbl.setLocation(new Point(4, 186));
      tinaPaletteGammaLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteGammaLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteContrastLbl = new JLabel();
      tinaPaletteContrastLbl.setText("Contrast");
      tinaPaletteContrastLbl.setSize(new Dimension(56, 22));
      tinaPaletteContrastLbl.setLocation(new Point(4, 160));
      tinaPaletteContrastLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteContrastLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteSaturationLbl = new JLabel();
      tinaPaletteSaturationLbl.setText("Saturation");
      tinaPaletteSaturationLbl.setSize(new Dimension(56, 22));
      tinaPaletteSaturationLbl.setLocation(new Point(4, 134));
      tinaPaletteSaturationLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSaturationLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteHueLbl = new JLabel();
      tinaPaletteHueLbl.setText("Hue");
      tinaPaletteHueLbl.setSize(new Dimension(56, 22));
      tinaPaletteHueLbl.setLocation(new Point(4, 108));
      tinaPaletteHueLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteHueLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteBlueLbl = new JLabel();
      tinaPaletteBlueLbl.setText("Blue");
      tinaPaletteBlueLbl.setSize(new Dimension(56, 22));
      tinaPaletteBlueLbl.setLocation(new Point(4, 82));
      tinaPaletteBlueLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBlueLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteGreenLbl = new JLabel();
      tinaPaletteGreenLbl.setText("Green");
      tinaPaletteGreenLbl.setSize(new Dimension(56, 22));
      tinaPaletteGreenLbl.setLocation(new Point(4, 56));
      tinaPaletteGreenLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteGreenLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteRedLbl = new JLabel();
      tinaPaletteRedLbl.setText("Red");
      tinaPaletteRedLbl.setSize(new Dimension(56, 22));
      tinaPaletteRedLbl.setLocation(new Point(4, 30));
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
      tinaPaletteBalancingPanel.add(tinaPaletteSwapRGBLbl, null);
      tinaPaletteBalancingPanel.add(getTinaPaletteSwapRGBREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteSwapRGBSlider(), null);
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
      tinaPaletteShiftREd.setValueStep(1.0);
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
      tinaPaletteShiftREd.setBounds(50, 5, 56, 24);
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
      tinaPaletteRedREd.setValueStep(1.0);
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
      tinaPaletteRedREd.setLocation(new Point(60, 30));
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
      tinaPaletteGreenREd.setValueStep(1.0);
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
      tinaPaletteGreenREd.setLocation(new Point(60, 56));
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
      tinaPaletteBlueREd.setValueStep(1.0);
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
      tinaPaletteBlueREd.setLocation(new Point(60, 82));
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
      tinaPaletteHueREd.setValueStep(1.0);
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
      tinaPaletteHueREd.setLocation(new Point(60, 108));
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
      tinaPaletteSaturationREd.setValueStep(1.0);
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
      tinaPaletteSaturationREd.setLocation(new Point(60, 134));
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
      tinaPaletteContrastREd.setValueStep(1.0);
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
      tinaPaletteContrastREd.setLocation(new Point(60, 160));
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
      tinaPaletteGammaREd.setValueStep(1.0);
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
      tinaPaletteGammaREd.setLocation(new Point(60, 186));
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
      tinaPaletteBrightnessREd.setValueStep(1.0);
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
      tinaPaletteBrightnessREd.setLocation(new Point(60, 212));
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
      tinaPaletteShiftSlider.setBounds(106, 6, 86, 22);
      tinaPaletteShiftSlider.setMaximum(255);
      tinaPaletteShiftSlider.setMinimum(-255);
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
      tinaPaletteRedSlider.setSize(new Dimension(74, 22));
      tinaPaletteRedSlider.setLocation(new Point(116, 30));
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
      tinaPaletteGreenSlider.setSize(new Dimension(74, 22));
      tinaPaletteGreenSlider.setLocation(new Point(116, 56));
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
      tinaPaletteBlueSlider.setSize(new Dimension(74, 22));
      tinaPaletteBlueSlider.setLocation(new Point(116, 82));
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
      tinaPaletteHueSlider.setSize(new Dimension(74, 22));
      tinaPaletteHueSlider.setLocation(new Point(116, 108));
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
      tinaPaletteSaturationSlider.setSize(new Dimension(74, 22));
      tinaPaletteSaturationSlider.setLocation(new Point(116, 134));
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
      tinaPaletteContrastSlider.setSize(new Dimension(74, 22));
      tinaPaletteContrastSlider.setLocation(new Point(116, 160));
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
      tinaPaletteGammaSlider.setSize(new Dimension(74, 22));
      tinaPaletteGammaSlider.setLocation(new Point(116, 186));
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
      tinaPaletteBrightnessSlider.setSize(new Dimension(74, 22));
      tinaPaletteBrightnessSlider.setLocation(new Point(116, 212));
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
    nonlinearControlsRows = new NonlinearControlsRow[12];
    nonlinearControlsRows[0] = new NonlinearControlsRow(getNonlinearVar1Cmb(), getNonlinearParams1Cmb(), getNonlinearVar1REd(),
        getNonlinearParams1REd(), getNonlinearParams1LeftButton());
    nonlinearControlsRows[1] = new NonlinearControlsRow(getNonlinearVar2Cmb(), getNonlinearParams2Cmb(), getNonlinearVar2REd(),
        getNonlinearParams2REd(), getNonlinearParams2LeftButton());
    nonlinearControlsRows[2] = new NonlinearControlsRow(getNonlinearVar3Cmb(), getNonlinearParams3Cmb(), getNonlinearVar3REd(),
        getNonlinearParams3REd(), getNonlinearParams3LeftButton());
    nonlinearControlsRows[3] = new NonlinearControlsRow(getNonlinearVar4Cmb(), getNonlinearParams4Cmb(), getNonlinearVar4REd(),
        getNonlinearParams4REd(), getNonlinearParams4LeftButton());
    nonlinearControlsRows[4] = new NonlinearControlsRow(getNonlinearVar5Cmb(), getNonlinearParams5Cmb(), getNonlinearVar5REd(),
        getNonlinearParams5REd(), getNonlinearParams5LeftButton());
    nonlinearControlsRows[5] = new NonlinearControlsRow(getNonlinearVar6Cmb(), getNonlinearParams6Cmb(), getNonlinearVar6REd(),
        getNonlinearParams6REd(), getNonlinearParams6LeftButton());
    nonlinearControlsRows[6] = new NonlinearControlsRow(getNonlinearVar7Cmb(), getNonlinearParams7Cmb(), getNonlinearVar7REd(),
        getNonlinearParams7REd(), getNonlinearParams7LeftButton());
    nonlinearControlsRows[7] = new NonlinearControlsRow(getNonlinearVar8Cmb(), getNonlinearParams8Cmb(), getNonlinearVar8REd(),
        getNonlinearParams8REd(), getNonlinearParams8LeftButton());
    nonlinearControlsRows[8] = new NonlinearControlsRow(getNonlinearVar9Cmb(), getNonlinearParams9Cmb(), getNonlinearVar9REd(),
        getNonlinearParams9REd(), getNonlinearParams9LeftButton());
    nonlinearControlsRows[9] = new NonlinearControlsRow(getNonlinearVar10Cmb(), getNonlinearParams10Cmb(), getNonlinearVar10REd(),
        getNonlinearParams10REd(), getNonlinearParams10LeftButton());
    nonlinearControlsRows[10] = new NonlinearControlsRow(getNonlinearVar11Cmb(), getNonlinearParams11Cmb(), getNonlinearVar11REd(),
        getNonlinearParams11REd(), getNonlinearParams11LeftButton());
    nonlinearControlsRows[11] = new NonlinearControlsRow(getNonlinearVar12Cmb(), getNonlinearParams12Cmb(), getNonlinearVar12REd(),
        getNonlinearParams12REd(), getNonlinearParams12LeftButton());

    tinaController = new TinaController(pErrorHandler, pPrefs, getCenterCenterPanel(), getTinaCameraRollREd(), getTinaCameraRollSlider(), getTinaCameraPitchREd(),
        getTinaCameraPitchSlider(), getTinaCameraYawREd(), getTinaCameraYawSlider(), getTinaCameraPerspectiveREd(), getTinaCameraPerspectiveSlider(),
        getTinaCameraCentreXREd(), getTinaCameraCentreXSlider(), getTinaCameraCentreYREd(),
        getTinaCameraCentreYSlider(), getTinaCameraZoomREd(), getTinaCameraZoomSlider(),
        getTinaCameraZPosREd(), getTinaCameraZPosSlider(), getTinaCameraDOFREd(), getTinaCameraDOFSlider(),
        getTinaPixelsPerUnitREd(), getTinaPixelsPerUnitSlider(),
        getTinaBrightnessREd(), getTinaBrightnessSlider(), getTinaContrastREd(), getTinaContrastSlider(), getTinaGammaREd(), getTinaGammaSlider(),
        getTinaVibrancyREd(), getTinaVibrancySlider(), getTinaFilterRadiusREd(), getTinaFilterRadiusSlider(), getTinaGammaThresholdREd(),
        getTinaGammaThresholdSlider(), getTinaBGColorRedREd(), getTinaBGColorRedSlider(), getTinaBGColorGreenREd(), getTinaBGColorGreenSlider(), getTinaBGColorBlueREd(),
        getTinaBGColorBlueSlider(), getTinaPaletteRandomPointsREd(), getTinaPaletteImgPanel(), getTinaPaletteShiftREd(), getTinaPaletteShiftSlider(),
        getTinaPaletteRedREd(), getTinaPaletteRedSlider(), getTinaPaletteGreenREd(), getTinaPaletteGreenSlider(), getTinaPaletteBlueREd(),
        getTinaPaletteBlueSlider(), getTinaPaletteHueREd(), getTinaPaletteHueSlider(), getTinaPaletteSaturationREd(), getTinaPaletteSaturationSlider(),
        getTinaPaletteContrastREd(), getTinaPaletteContrastSlider(), getTinaPaletteGammaREd(), getTinaPaletteGammaSlider(), getTinaPaletteBrightnessREd(),
        getTinaPaletteBrightnessSlider(), getTinaPaletteSwapRGBREd(), getTinaPaletteSwapRGBSlider(), getTinaTransformationsTable(), getAffineC00REd(),
        getAffineC01REd(), getAffineC10REd(), getAffineC11REd(), getAffineC20REd(), getAffineC21REd(), getAffineRotateAmountREd(), getAffineScaleAmountREd(),
        getAffineMoveAmountREd(), getAffineRotateLeftButton(), getAffineRotateRightButton(), getAffineEnlargeButton(), getAffineShrinkButton(),
        getAffineMoveUpButton(), getAffineMoveLeftButton(), getAffineMoveRightButton(), getAffineMoveDownButton(), getTinaAddTransformationButton(),
        getTinaDuplicateTransformationButton(), getTinaDeleteTransformationButton(), getTinaAddFinalTransformationButton(), getRandomBatchPanel(),
        nonlinearControlsRows, getXFormColorREd(), getXFormColorSlider(), getXFormSymmetryREd(), getXFormSymmetrySlider(), getXFormOpacityREd(),
        getXFormOpacitySlider(), getXFormDrawModeCmb(), getRelWeightsTable(), getRelWeightsZeroButton(), getRelWeightsOneButton(), getRelWeightREd(),
        getMouseTransformMoveButton(),
        getMouseTransformRotateButton(), getMouseTransformScaleButton(), getMouseTransformShearButton(), getAffineEditPostTransformButton(), getAffineEditPostTransformSmallButton(),
        getMouseTransformZoomInButton(), getMouseTransformZoomOutButton(), getToggleTrianglesButton(), new MainProgressUpdater(this),
        getAffineResetTransformButton(), getCreatePaletteColorsTable(),
        getShadingCmb(), getShadingAmbientREd(), getShadingAmbientSlider(), getShadingDiffuseREd(), getShadingDiffuseSlider(),
        getShadingPhongREd(), getShadingPhongSlider(), getShadingPhongSizeREd(), getShadingPhongSizeSlider(), getShadingLightCmb(),
        getShadingLightXREd(), getShadingLightXSlider(), getShadingLightYREd(), getShadingLightYSlider(), getShadingLightZREd(),
        getShadingLightZSlider(), getShadingLightRedREd(), getShadingLightRedSlider(), getShadingLightGreenREd(), getShadingLightGreenSlider(),
        getShadingLightBlueREd(), getShadingLightBlueSlider(), getMouseTransformSlowButton(), getRenderBatchJobsTable(),
        getBatchRenderJobProgressBar(), getBatchRenderTotalProgressBar(), new JobProgressUpdater(this),
        getBatchRenderAddFilesButton(), getBatchRenderFilesMoveDownButton(), getBatchRenderFilesMoveUpButton(),
        getBatchRenderFilesRemoveButton(), getBatchRenderFilesRemoveAllButton(), getBatchRenderStartButton(),
        getRootTabbedPane(), getAffineFlipHorizontalButton(), getAffineFlipVerticalButton(), getDarkTrianglesToggleButton(), getShadingBlurRadiusREd(), getShadingBlurRadiusSlider(), getShadingBlurFadeREd(),
        getShadingBlurFadeSlider(), getShadingBlurFallOffREd(), getShadingBlurFallOffSlider(), getScriptTextArea(),
        getAffineScaleXButton(), getAffineScaleYButton(), getGradientLibraryCenterPanel(), getGradientLibraryGradientCmb(), getHelpPane(),
        getFaqPane(), getToggleVariationsButton(), getAffinePreserveZButton(), getQualityProfileCmb(), getResolutionProfileCmb(),
        getBatchQualityProfileCmb(), getBatchResolutionProfileCmb(), getInteractiveQualityProfileCmb(), getInteractiveResolutionProfileCmb(),
        getSwfAnimatorQualityProfileCmb(), getSwfAnimatorResolutionProfileCmb(), getTinaRenderFlameButton(), getTinaAppendToMovieButton(),
        getTransformationWeightREd(), getUndoButton(), getRedoButton());

    tinaController.refreshing = tinaController.cmbRefreshing = tinaController.gridRefreshing = true;
    try {
      for (NonlinearControlsRow row : nonlinearControlsRows) {
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

      getShadingLightCmb().removeAllItems();
      getShadingLightCmb().addItem(String.valueOf("1"));
      getShadingLightCmb().addItem(String.valueOf("2"));
      getShadingLightCmb().addItem(String.valueOf("3"));
      getShadingLightCmb().addItem(String.valueOf("4"));

      getSwfAnimatorGlobalScriptCmb().removeAllItems();
      getSwfAnimatorGlobalScriptCmb().addItem(AnimationService.GlobalScript.NONE);
      getSwfAnimatorGlobalScriptCmb().addItem(AnimationService.GlobalScript.ROTATE_PITCH);
      getSwfAnimatorGlobalScriptCmb().addItem(AnimationService.GlobalScript.ROTATE_ROLL);
      getSwfAnimatorGlobalScriptCmb().addItem(AnimationService.GlobalScript.ROTATE_YAW);
      getSwfAnimatorGlobalScriptCmb().addItem(AnimationService.GlobalScript.ROTATE_PITCH_YAW);
      getSwfAnimatorGlobalScriptCmb().setSelectedItem(AnimationService.GlobalScript.NONE);

      getSwfAnimatorXFormScriptCmb().removeAllItems();
      getSwfAnimatorXFormScriptCmb().addItem(AnimationService.XFormScript.NONE);
      getSwfAnimatorXFormScriptCmb().addItem(AnimationService.XFormScript.ROTATE_FULL);
      getSwfAnimatorXFormScriptCmb().addItem(AnimationService.XFormScript.ROTATE_SLIGHTLY);
      getSwfAnimatorXFormScriptCmb().addItem(AnimationService.XFormScript.ROTATE_FIRST_XFORM);
      getSwfAnimatorXFormScriptCmb().addItem(AnimationService.XFormScript.ROTATE_LAST_XFORM);
      getSwfAnimatorXFormScriptCmb().setSelectedItem(AnimationService.XFormScript.ROTATE_FIRST_XFORM);

      tinaController.setInteractiveRendererCtrl(new TinaInteractiveRendererController(tinaController, pErrorHandler, pPrefs,
          getInteractiveLoadFlameButton(), getInteractiveLoadFlameFromClipboardButton(), getInteractiveNextButton(), getInteractiveStopButton(),
          getInteractiveFlameToClipboardButton(), getInteractiveSaveImageButton(),
          getInteractiveSaveFlameButton(), getInteractiveRandomStyleCmb(), getInteractiveCenterTopPanel(), getInteractiveStatsTextArea(),
          getInteractiveHalveSizeButton(), getInteractiveResolutionProfileCmb(), getInteractiveQualityProfileCmb(),
          getInteractivePauseButton(), getInteractiveResumeButton()));
      tinaController.getInteractiveRendererCtrl().enableControls();

      tinaController.setSwfAnimatorCtrl(new TinaSWFAnimatorController(tinaController, pErrorHandler, pPrefs,
          getSwfAnimatorGlobalScriptCmb(), getSwfAnimatorXFormScriptCmb(), getSwfAnimatorFramesREd(), getSwfAnimatorFramesPerSecondREd(),
          getSwfAnimatorGenerateButton(), getSwfAnimatorResolutionProfileCmb(),
          getSwfAnimatorQualityProfileCmb(), getSwfAnimatorLoadFlameFromMainButton(),
          getSwfAnimatorLoadFlameFromClipboardButton(), getSwfAnimatorLoadFlameButton(),
          getSwfAnimatorHalfSizeButton(), getSwfAnimatorProgressBar(), getSwfAnimatorCancelButton(),
          getSwfAnimatorLoadSoundButton(), getSwfAnimatorClearSoundButton(),
          new SWFAnimatorProgressUpdater(this), getSwfAnimatorPreviewRootPanel(), getSwfAnimatorSoundCaptionLbl(),
          getSwfAnimatorFrameSlider(), getSwfAnimatorFrameREd(), getSwfAnimatorFlamesPanel(), getSwfAnimatorFlamesButtonGroup(),
          getSwfAnimatorOutputCmb(), getSwfAnimatorMoveUpButton(), getSwfAnimatorMoveDownButton(), getSwfAnimatorRemoveFlameButton(),
          getSwfAnimatorRemoveAllFlamesButton(), getSwfAnimatorMovieFromClipboardButton(), getSwfAnimatorMovieFromDiskButton(),
          getSwfAnimatorMovieToClipboardButton(), getSwfAnimatorMovieToDiskButton(), getSwfAnimatorFrameToEditorBtn(),
          getSwfAnimatorPlayButton(), getSwfAnimatorFromFrameREd(), getSwfAnimatorToFrameREd()));
      tinaController.getSwfAnimatorCtrl().enableControls();

    }
    finally {
      tinaController.refreshing = tinaController.cmbRefreshing = tinaController.gridRefreshing = false;
    }

    return tinaController;
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
      tinaAddFinalTransformationButton.setPreferredSize(new Dimension(81, 24));
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
      affineC00REd.setValueStep(0.01);
      affineC00REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC00REd_changed();
        }
      });
      affineC00REd.setPreferredSize(new Dimension(76, 24));
      affineC00REd.setText("");
      affineC00REd.setLocation(new Point(32, 6));
      affineC00REd.setSize(new Dimension(76, 24));
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
      affineC01REd.setValueStep(0.01);
      affineC01REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC01REd_changed();
        }
      });
      affineC01REd.setPreferredSize(new Dimension(76, 24));
      affineC01REd.setText("");
      affineC01REd.setLocation(new Point(32, 30));
      affineC01REd.setSize(new Dimension(76, 24));
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
      affineC10REd.setValueStep(0.01);
      affineC10REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC10REd_changed();
        }
      });
      affineC10REd.setPreferredSize(new Dimension(76, 24));
      affineC10REd.setText("");
      affineC10REd.setLocation(new Point(131, 6));
      affineC10REd.setSize(new Dimension(76, 24));
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
      affineC11REd.setValueStep(0.01);
      affineC11REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC11REd_changed();
        }
      });
      affineC11REd.setPreferredSize(new Dimension(76, 24));
      affineC11REd.setText("");
      affineC11REd.setLocation(new Point(131, 30));
      affineC11REd.setSize(new Dimension(76, 24));
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
      affineC20REd.setValueStep(0.01);
      affineC20REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC20REd_changed();
        }
      });
      affineC20REd.setPreferredSize(new Dimension(76, 24));
      affineC20REd.setText("");
      affineC20REd.setLocation(new Point(238, 6));
      affineC20REd.setSize(new Dimension(76, 24));
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
      affineC21REd.setValueStep(0.01);
      affineC21REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          tinaController.affineC21REd_changed();
        }
      });
      affineC21REd.setPreferredSize(new Dimension(76, 24));
      affineC21REd.setText("");
      affineC21REd.setLocation(new Point(238, 30));
      affineC21REd.setSize(new Dimension(76, 24));
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
      affineRotateLeftButton.setSize(new Dimension(55, 24));
      affineRotateLeftButton.setLocation(new Point(3, 60));
      affineRotateLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/turnLeft.gif")));
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
      affineRotateRightButton.setLocation(new Point(3, 109));
      affineRotateRightButton.setSize(new Dimension(55, 24));
      affineRotateRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/turnRight.gif")));
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
      affineEnlargeButton.setLocation(new Point(65, 60));
      affineEnlargeButton.setSize(new Dimension(55, 24));
      affineEnlargeButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/enlarge.gif")));
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
      affineShrinkButton.setLocation(new Point(65, 109));
      affineShrinkButton.setSize(new Dimension(55, 24));
      affineShrinkButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/shrink.gif")));
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
  private JTextField getAffineRotateAmountREd() {
    if (affineRotateAmountREd == null) {
      affineRotateAmountREd = new JTextField();
      affineRotateAmountREd.setPreferredSize(new Dimension(56, 24));
      affineRotateAmountREd.setText("90");
      affineRotateAmountREd.setSize(new Dimension(56, 24));
      affineRotateAmountREd.setLocation(new Point(3, 87));
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
      flowLayout.setAlignment(FlowLayout.LEFT);
      trnsformationsEastPanel = new JPanel();
      trnsformationsEastPanel.setFont(new Font("Dialog", Font.PLAIN, 10));
      trnsformationsEastPanel.setLayout(flowLayout);
      trnsformationsEastPanel.setPreferredSize(new Dimension(91, 0));
      trnsformationsEastPanel.add(getTransformationWeightREd());
      trnsformationsEastPanel.add(getTinaAddTransformationButton(), null);
      trnsformationsEastPanel.add(getTinaDuplicateTransformationButton(), null);
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
      transformationsSplitPane.setDividerLocation(152);
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
  private JTextField getAffineScaleAmountREd() {
    if (affineScaleAmountREd == null) {
      affineScaleAmountREd = new JTextField();
      affineScaleAmountREd.setPreferredSize(new Dimension(56, 24));
      affineScaleAmountREd.setText("105");
      affineScaleAmountREd.setSize(new Dimension(56, 24));
      affineScaleAmountREd.setLocation(new Point(65, 87));
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
      affineMoveUpButton.setLocation(new Point(181, 60));
      affineMoveUpButton.setSize(new Dimension(55, 24));
      affineMoveUpButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveUp.gif")));
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
      affineMoveDownButton.setLocation(new Point(181, 109));
      affineMoveDownButton.setSize(new Dimension(55, 24));
      affineMoveDownButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveDown.gif")));
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
      affineMoveLeftButton.setLocation(new Point(125, 87));
      affineMoveLeftButton.setSize(new Dimension(55, 24));
      affineMoveLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
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
      affineMoveRightButton.setLocation(new Point(237, 87));
      affineMoveRightButton.setSize(new Dimension(55, 24));
      affineMoveRightButton.setPreferredSize(new Dimension(55, 24));
      affineMoveRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
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
  private JTextField getAffineMoveAmountREd() {
    if (affineMoveAmountREd == null) {
      affineMoveAmountREd = new JTextField();
      affineMoveAmountREd.setPreferredSize(new Dimension(56, 24));
      affineMoveAmountREd.setText("0.5");
      affineMoveAmountREd.setSize(new Dimension(56, 24));
      affineMoveAmountREd.setLocation(new Point(181, 87));
      affineMoveAmountREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineMoveAmountREd;
  }

  /**
   * This method initializes createRandomPointsPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCreateRandomPointsPanel() {
    if (createRandomPointsPanel == null) {
      FlowLayout flowLayout1 = new FlowLayout();
      flowLayout1.setAlignment(FlowLayout.LEFT);
      createRandomPointsPanel = new JPanel();
      createRandomPointsPanel.setPreferredSize(new Dimension(180, 32));
      createRandomPointsPanel.setLayout(flowLayout1);
      createRandomPointsPanel.add(tinaPaletteRandomPointsLbl, null);
      createRandomPointsPanel.add(getTinaPaletteRandomPointsREd(), null);
    }
    return createRandomPointsPanel;
  }

  /**
   * This method initializes createPaletteTablePanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCreatePaletteTablePanel() {
    if (createPaletteTablePanel == null) {
      createPaletteTablePanel = new JPanel();
      createPaletteTablePanel.setLayout(new BorderLayout());
      createPaletteTablePanel.setPreferredSize(new Dimension(180, 178));
      createPaletteTablePanel.add(getCreatePaletteScrollPane(), BorderLayout.CENTER);
    }
    return createPaletteTablePanel;
  }

  /**
   * This method initializes randomBatchPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getRandomBatchPanel() {
    if (randomBatchPanel == null) {
      randomBatchPanel = new JPanel();
      randomBatchPanel.setLayout(new BorderLayout());
      randomBatchPanel.setPreferredSize(new Dimension(0, 100));
    }
    return randomBatchPanel;
  }

  /**
   * This method initializes randomBatchButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRandomBatchButton() {
    if (randomBatchButton == null) {
      randomBatchButton = new JButton();
      randomBatchButton.setFont(new Font("Dialog", Font.BOLD, 10));
      randomBatchButton.setMnemonic(KeyEvent.VK_D);
      randomBatchButton.setText("Random flames");
      randomBatchButton.setBounds(new Rectangle(7, 7, 125, 52));
      randomBatchButton.setPreferredSize(new Dimension(125, 52));
      randomBatchButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.createRandomBatch(-1, (String) randomStyleCmb.getSelectedItem());
          tinaController.importFromRandomBatch(0);
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
      nonlinearVar1Cmb.setMaximumRowCount(32);
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
      nonlinearParams1LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar2Cmb.setMaximumRowCount(32);
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
      nonlinearParams2LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar3Cmb.setMaximumRowCount(32);
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
      nonlinearParams3LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      xFormColorREd.setValueStep(0.05);
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
      xFormColorREd.setLocation(new Point(68, 4));
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
      xFormColorSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormColorSlider.setPreferredSize(new Dimension(172, 22));
      xFormColorSlider.setMaximum(100);
      xFormColorSlider.setMinimum(0);
      xFormColorSlider.setValue(0);
      xFormColorSlider.setSize(new Dimension(172, 22));
      xFormColorSlider.setLocation(new Point(123, 4));
      xFormColorSlider.setFont(new Font("Dialog", Font.BOLD, 10));
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
      xFormSymmetryREd.setValueStep(0.05);
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
      xFormSymmetryREd.setLocation(new Point(68, 30));
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
      xFormSymmetrySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormSymmetrySlider.setPreferredSize(new Dimension(172, 22));
      xFormSymmetrySlider.setMaximum(100);
      xFormSymmetrySlider.setMinimum(-100);
      xFormSymmetrySlider.setValue(0);
      xFormSymmetrySlider.setLocation(new Point(123, 30));
      xFormSymmetrySlider.setSize(new Dimension(172, 22));
      xFormSymmetrySlider.setFont(new Font("Dialog", Font.BOLD, 10));
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
      xFormOpacityREd.setLocation(new Point(68, 56));
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
      xFormOpacitySlider.setSize(new Dimension(172, 22));
      xFormOpacitySlider.setLocation(new Point(123, 56));
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
      xFormDrawModeCmb.setLocation(new Point(68, 82));
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
      newFlameButton.setPreferredSize(new Dimension(125, 52));
      newFlameButton.setMnemonic(KeyEvent.VK_N);
      newFlameButton.setText("New from scratch");
      newFlameButton.setActionCommand("New from scratch");
      newFlameButton.setBounds(new Rectangle(367, 7, 125, 52));
      newFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
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

      JPanel panel_12 = new JPanel();
      panel_12.setPreferredSize(new Dimension(10, 26));
      swfAnimatorPanel_1.add(panel_12, BorderLayout.SOUTH);
      panel_12.setLayout(null);

      swfAnimatorFrameSlider = new JSlider();
      swfAnimatorFrameSlider.setBounds(100, 5, 199, 19);
      panel_12.add(swfAnimatorFrameSlider);
      swfAnimatorFrameSlider.setMinorTickSpacing(5);
      swfAnimatorFrameSlider.setMinimum(1);
      swfAnimatorFrameSlider.setMajorTickSpacing(10);
      swfAnimatorFrameSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().refreshFlameImage();
          }
        }
      });
      swfAnimatorFrameSlider.setValue(0);
      swfAnimatorFrameSlider.setPreferredSize(new Dimension(220, 19));
      swfAnimatorFrameSlider.setMaximum(60);

      swfAnimatorFrameREd = new JTextField();
      swfAnimatorFrameREd.setBounds(45, 2, 56, 22);
      panel_12.add(swfAnimatorFrameREd);
      swfAnimatorFrameREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().swfAnimatorFrameREd_changed();
          }
        }
      });
      swfAnimatorFrameREd.setText("60");
      swfAnimatorFrameREd.setPreferredSize(new Dimension(56, 22));
      swfAnimatorFrameREd.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblFrame = new JLabel();
      lblFrame.setBounds(0, 2, 39, 22);
      panel_12.add(lblFrame);
      lblFrame.setHorizontalAlignment(SwingConstants.RIGHT);
      lblFrame.setText("Frame");
      lblFrame.setPreferredSize(new Dimension(94, 22));
      lblFrame.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorFrameToEditorBtn = new JButton();
      swfAnimatorFrameToEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().swfAnimatorFrameToEditorBtn_clicked();
        }
      });
      swfAnimatorFrameToEditorBtn.setToolTipText("Copy current flame into Editor");
      swfAnimatorFrameToEditorBtn.setText("E");
      swfAnimatorFrameToEditorBtn.setPreferredSize(new Dimension(42, 24));
      swfAnimatorFrameToEditorBtn.setMnemonic(KeyEvent.VK_E);
      swfAnimatorFrameToEditorBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorFrameToEditorBtn.setBounds(306, 0, 42, 28);
      panel_12.add(swfAnimatorFrameToEditorBtn);

      swfAnimatorPreviewRootPanel = new JPanel();
      swfAnimatorPanel_1.add(swfAnimatorPreviewRootPanel, BorderLayout.CENTER);
      swfAnimatorPreviewRootPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_10 = new JPanel();
      panel_10.setBorder(new TitledBorder(null, "Script", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_10.setPreferredSize(new Dimension(400, 10));
      panel_2.add(panel_10, BorderLayout.EAST);
      panel_10.setLayout(null);
      panel_10.add(getSwfAnimatorGlobalScriptCmb());
      animateGlobalScriptLbl = new JLabel();
      animateGlobalScriptLbl.setBounds(19, 23, 94, 22);
      panel_10.add(animateGlobalScriptLbl);
      animateGlobalScriptLbl.setPreferredSize(new Dimension(94, 22));
      animateGlobalScriptLbl.setText("Global script");
      animateGlobalScriptLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      animateXFormScriptLbl = new JLabel();
      animateXFormScriptLbl.setBounds(19, 51, 94, 22);
      panel_10.add(animateXFormScriptLbl);
      animateXFormScriptLbl.setPreferredSize(new Dimension(94, 22));
      animateXFormScriptLbl.setText("XForm script");
      animateXFormScriptLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_10.add(getSwfAnimatorXFormScriptCmb());
      panel_10.add(getSwfAnimatorFramesREd());

      swfAnimatorFramesPerSecondREd = new JTextField();
      swfAnimatorFramesPerSecondREd.setBounds(117, 119, 56, 22);
      panel_10.add(swfAnimatorFramesPerSecondREd);
      swfAnimatorFramesPerSecondREd.setText("12");
      swfAnimatorFramesPerSecondREd.setPreferredSize(new Dimension(56, 22));
      swfAnimatorFramesPerSecondREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      animateFramesLbl = new JLabel();
      animateFramesLbl.setBounds(19, 86, 94, 22);
      panel_10.add(animateFramesLbl);
      animateFramesLbl.setHorizontalAlignment(SwingConstants.LEFT);
      animateFramesLbl.setPreferredSize(new Dimension(94, 22));
      animateFramesLbl.setText("Total frame count");
      animateFramesLbl.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel lblFramesPerSecond = new JLabel();
      lblFramesPerSecond.setBounds(12, 120, 101, 22);
      panel_10.add(lblFramesPerSecond);
      lblFramesPerSecond.setHorizontalAlignment(SwingConstants.LEFT);
      lblFramesPerSecond.setText("Frames per second");
      lblFramesPerSecond.setPreferredSize(new Dimension(94, 22));
      lblFramesPerSecond.setFont(new Font("Dialog", Font.BOLD, 10));
      panel_10.add(getSwfAnimatorLoadSoundButton());

      swfAnimatorSoundCaptionLbl = new JLabel();
      swfAnimatorSoundCaptionLbl.setBounds(145, 280, 118, 22);
      panel_10.add(swfAnimatorSoundCaptionLbl);
      swfAnimatorSoundCaptionLbl.setText("(no sound loaded)");
      swfAnimatorSoundCaptionLbl.setPreferredSize(new Dimension(94, 22));
      swfAnimatorSoundCaptionLbl.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorClearSoundButton = new JButton();
      swfAnimatorClearSoundButton.setBounds(266, 279, 125, 24);
      panel_10.add(swfAnimatorClearSoundButton);
      swfAnimatorClearSoundButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().clearSoundButton_clicked();
        }
      });
      swfAnimatorClearSoundButton.setToolTipText("");
      swfAnimatorClearSoundButton.setText("Clear sound");
      swfAnimatorClearSoundButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorClearSoundButton.setFont(new Font("Dialog", Font.BOLD, 10));

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
      swfAnimatorGenerateButton.setBounds(1039, 12, 125, 52);
      swfAnimatorGenerateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().generateButton_clicked();
        }
      });
      swfAnimatorGenerateButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorGenerateButton.setText("Generate");
      swfAnimatorGenerateButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorGenerateButton;
  }

  /**
   * This method initializes animateFramesREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getSwfAnimatorFramesREd() {
    if (swfAnimatorFramesREd == null) {
      swfAnimatorFramesREd = new JTextField();
      swfAnimatorFramesREd.setBounds(117, 85, 56, 22);
      swfAnimatorFramesREd.setEditable(false);
      swfAnimatorFramesREd.setPreferredSize(new Dimension(56, 22));
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
  private JComboBox getSwfAnimatorGlobalScriptCmb() {
    if (swfAnimatorGlobalScriptCmb == null) {
      swfAnimatorGlobalScriptCmb = new JComboBox();
      swfAnimatorGlobalScriptCmb.setBounds(116, 23, 275, 22);
      swfAnimatorGlobalScriptCmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScriptCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorGlobalScriptCmb;
  }

  /**
   * This method initializes animateXFormScriptCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getSwfAnimatorXFormScriptCmb() {
    if (swfAnimatorXFormScriptCmb == null) {
      swfAnimatorXFormScriptCmb = new JComboBox();
      swfAnimatorXFormScriptCmb.setBounds(116, 51, 275, 22);
      swfAnimatorXFormScriptCmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScriptCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorXFormScriptCmb;
  }

  /**
   * This method initializes triangleOperationsPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTriangleOperationsPanel() {
    if (triangleOperationsPanel == null) {
      editSpaceLbl4 = new JLabel();
      editSpaceLbl4.setFont(new Font("Dialog", Font.BOLD, 10));
      editSpaceLbl4.setText("");
      editSpaceLbl4.setPreferredSize(new Dimension(42, 2));
      editSpaceLbl3 = new JLabel();
      editSpaceLbl3.setFont(new Font("Dialog", Font.BOLD, 10));
      editSpaceLbl3.setText("");
      editSpaceLbl3.setPreferredSize(new Dimension(42, 10));
      editSpaceLbl2 = new JLabel();
      editSpaceLbl2.setFont(new Font("Dialog", Font.BOLD, 10));
      editSpaceLbl2.setText("");
      editSpaceLbl2.setPreferredSize(new Dimension(42, 2));
      editSpaceLbl1 = new JLabel();
      editSpaceLbl1.setFont(new Font("Dialog", Font.BOLD, 10));
      editSpaceLbl1.setText("");
      editSpaceLbl1.setPreferredSize(new Dimension(42, 2));
      triangleOperationsPanel = new JPanel();
      triangleOperationsPanel.setLayout(new FlowLayout());
      triangleOperationsPanel.setPreferredSize(new Dimension(52, 0));
      triangleOperationsPanel.add(getAffineEditPostTransformSmallButton(), null);
      triangleOperationsPanel.add(editSpaceLbl4, null);
      triangleOperationsPanel.add(getMouseTransformMoveButton(), null);
      triangleOperationsPanel.add(getMouseTransformRotateButton(), null);
      triangleOperationsPanel.add(getMouseTransformScaleButton(), null);

      mouseTransformShearButton = new JToggleButton();
      mouseTransformShearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.mouseTransformShearButton_clicked();
        }
      });
      mouseTransformShearButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/shear.gif")));
      mouseTransformShearButton.setToolTipText("Enable free triangle editing mode");
      mouseTransformShearButton.setPreferredSize(new Dimension(42, 36));
      triangleOperationsPanel.add(mouseTransformShearButton);
      triangleOperationsPanel.add(editSpaceLbl1, null);
      triangleOperationsPanel.add(editSpaceLbl3, null);
      triangleOperationsPanel.add(getToggleTrianglesButton(), null);
      triangleOperationsPanel.add(editSpaceLbl2, null);
      triangleOperationsPanel.add(getTinaRenderFlameButton(), null);
    }
    return triangleOperationsPanel;
  }

  /**
   * This method initializes mouseTransformMoveButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getMouseTransformMoveButton() {
    if (mouseTransformMoveButton == null) {
      mouseTransformMoveButton = new JToggleButton();
      mouseTransformMoveButton.setPreferredSize(new Dimension(42, 36));
      mouseTransformMoveButton.setSelected(true);
      mouseTransformMoveButton.setToolTipText("Enable triangle dragging mode");
      mouseTransformMoveButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/move.gif")));
      mouseTransformMoveButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.mouseTransformMoveButton_clicked();
        }
      });
    }
    return mouseTransformMoveButton;
  }

  /**
   * This method initializes mouseTransformRotateButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getMouseTransformRotateButton() {
    if (mouseTransformRotateButton == null) {
      mouseTransformRotateButton = new JToggleButton();
      mouseTransformRotateButton.setPreferredSize(new Dimension(42, 36));
      mouseTransformRotateButton.setToolTipText("Enable triangle rotating mode");
      mouseTransformRotateButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/rotate.gif")));
      mouseTransformRotateButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.mouseTransformRotateButton_clicked();
        }
      });
    }
    return mouseTransformRotateButton;
  }

  /**
   * This method initializes mouseTransformScaleButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getMouseTransformScaleButton() {
    if (mouseTransformScaleButton == null) {
      mouseTransformScaleButton = new JToggleButton();
      mouseTransformScaleButton.setPreferredSize(new Dimension(42, 36));
      mouseTransformScaleButton.setToolTipText("Enable triangle scale mode");
      mouseTransformScaleButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/scale.gif")));
      mouseTransformScaleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.mouseTransformScaleButton_clicked();
        }
      });
    }
    return mouseTransformScaleButton;
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
      centerNorthPanel.add(getDarkTrianglesToggleButton(), null);
      centerNorthPanel.add(getMouseTransformZoomInButton());
      centerNorthPanel.add(getMouseTransformZoomOutButton());
      centerNorthPanel.add(getToggleVariationsButton());
      centerNorthPanel.add(getCancelRenderButton());
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
      toggleVariationsButton.setBounds(88, 4, 42, 24);
      toggleVariationsButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/variation.gif")));
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
      centerWestPanel.setPreferredSize(new Dimension(70, 0));
      centerWestPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
      centerWestPanel.add(getTinaAppendToMovieButton());
      centerWestPanel.add(getTinaWrapIntoSubFlameButton());
      centerWestPanel.add(getLabel_5());
      centerWestPanel.add(getUndoButton());
      centerWestPanel.add(getRedoButton());
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
      centerCenterPanel.add(centerDescLabel, BorderLayout.SOUTH);
      centerDescLabel.setText("  (just double-click on thumbnail to load it into main area)");
      centerDescLabel.setHorizontalAlignment(SwingConstants.CENTER);
      centerDescLabel.setFont(new Font("Dialog", Font.BOLD, 10));
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
      randomStyleCmb.setPreferredSize(new Dimension(125, 22));
      randomStyleCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      randomStyleCmb.setBounds(new Rectangle(231, 7, 125, 22));
      randomStyleCmb.setMaximumRowCount(32);
      randomStyleCmb.removeAllItems();
      for (String name : RandomFlameGeneratorList.getNameList()) {
        randomStyleCmb.addItem(name);
      }
      randomStyleCmb.setSelectedItem(RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);
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
      affineEditPostTransformButton.setSize(new Dimension(138, 24));
      affineEditPostTransformButton.setText("Edit Post  Transform");
      affineEditPostTransformButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineEditPostTransformButton.setLocation(new Point(4, 181));
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
      affineEditPostTransformSmallButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineEditPostTransformSmallButton.setText("P");
      affineEditPostTransformSmallButton.setToolTipText("Toggle post transform mode");
      affineEditPostTransformSmallButton.setMnemonic(KeyEvent.VK_P);
      affineEditPostTransformSmallButton.setPreferredSize(new Dimension(42, 36));
      affineEditPostTransformSmallButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.affineEditPostTransformSmallButton_clicked();
        }
      });
    }
    return affineEditPostTransformSmallButton;
  }

  /**
   * This method initializes mouseTransformZoomInButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getMouseTransformZoomInButton() {
    if (mouseTransformZoomInButton == null) {
      mouseTransformZoomInButton = new JButton();
      mouseTransformZoomInButton.setBounds(462, 4, 42, 24);
      mouseTransformZoomInButton.setFont(new Font("Dialog", Font.BOLD, 8));
      mouseTransformZoomInButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/triangleEnlarge.gif")));
      mouseTransformZoomInButton.setText("");
      mouseTransformZoomInButton.setToolTipText("Zoom in (triangles only)");
      mouseTransformZoomInButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformZoomInButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.mouseTransformZoomInButton_clicked();
        }
      });
    }
    return mouseTransformZoomInButton;
  }

  /**
   * This method initializes mouseTransformZoomOutButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getMouseTransformZoomOutButton() {
    if (mouseTransformZoomOutButton == null) {
      mouseTransformZoomOutButton = new JButton();
      mouseTransformZoomOutButton.setBounds(504, 4, 42, 24);
      mouseTransformZoomOutButton.setFont(new Font("Dialog", Font.BOLD, 8));
      mouseTransformZoomOutButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/triangleShrink.gif")));
      mouseTransformZoomOutButton.setText("");
      mouseTransformZoomOutButton.setToolTipText("Zoom out (triangles only)");
      mouseTransformZoomOutButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformZoomOutButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.mouseTransformZoomOutButton_clicked();
        }
      });
    }
    return mouseTransformZoomOutButton;
  }

  /**
   * This method initializes toggleTrianglesButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getToggleTrianglesButton() {
    if (toggleTrianglesButton == null) {
      toggleTrianglesButton = new JToggleButton();
      toggleTrianglesButton.setPreferredSize(new Dimension(42, 36));
      toggleTrianglesButton.setSelected(true);
      toggleTrianglesButton.setToolTipText("Display/hide triangles");
      toggleTrianglesButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/triangle.gif")));
      toggleTrianglesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.toggleTrianglesButton_clicked();
        }
      });
    }
    return toggleTrianglesButton;
  }

  /**
   * This method initializes renderProgressBar	
   * 	
   * @return javax.swing.JProgressBar	
   */
  JProgressBar getRenderProgressBar() {
    if (renderProgressBar == null) {
      renderProgressBar = new JProgressBar();
      renderProgressBar.setValue(0);
      renderProgressBar.setSize(new Dimension(192, 14));
      renderProgressBar.setLocation(new Point(136, 9));
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
      affineResetTransformButton.setText("Reset");
      affineResetTransformButton.setLocation(new Point(146, 181));
      affineResetTransformButton.setSize(new Dimension(130, 24));
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
      nonlinearVar4Cmb.setMaximumRowCount(32);
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
      nonlinearParams4LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar5Cmb.setMaximumRowCount(32);
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
      nonlinearParams5LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar6Cmb.setMaximumRowCount(32);
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
      nonlinearParams6LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar7Cmb.setMaximumRowCount(32);
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
      nonlinearParams7LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar8Cmb.setMaximumRowCount(32);
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
      nonlinearParams8LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar9Cmb.setMaximumRowCount(32);
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
      nonlinearParams9LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar10Cmb.setMaximumRowCount(32);
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
      nonlinearParams10LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar11Cmb.setMaximumRowCount(32);
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
      nonlinearParams11LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      nonlinearVar12Cmb.setMaximumRowCount(32);
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
      nonlinearParams12LeftButton.setIcon(new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/details.gif")));
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
      tinaGrabPaletteFromFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGrabPaletteFromFlameButton.setText("Grab from flame");
      tinaGrabPaletteFromFlameButton.setPreferredSize(new Dimension(180, 24));
      tinaGrabPaletteFromFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.grabPaletteFromFlameButton_actionPerformed(e);
        }
      });
    }
    return tinaGrabPaletteFromFlameButton;
  }

  /**
   * This method initializes tinaCameraZPosREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getTinaCameraZPosREd() {
    if (tinaCameraZPosREd == null) {
      tinaCameraZPosREd = new JWFNumberField();
      tinaCameraZPosREd.setValueStep(0.01);
      tinaCameraZPosREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraZPosREd.isMouseAdjusting() || tinaCameraZPosREd.getMouseChangeCount() == 0) {
            if (!tinaCameraZPosSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.cameraZPosREd_changed();
        }
      });
      tinaCameraZPosREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraZPosREd.setText("");
      tinaCameraZPosREd.setSize(new Dimension(100, 24));
      tinaCameraZPosREd.setLocation(new Point(100, 100));
      tinaCameraZPosREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraZPosREd;
  }

  /**
   * This method initializes tinaCameraZPosSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getTinaCameraZPosSlider() {
    if (tinaCameraZPosSlider == null) {
      tinaCameraZPosSlider = new JSlider();
      tinaCameraZPosSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraZPosSlider.setMaximum(100);
      tinaCameraZPosSlider.setMinimum(-100);
      tinaCameraZPosSlider.setValue(0);
      tinaCameraZPosSlider.setSize(new Dimension(220, 19));
      tinaCameraZPosSlider.setLocation(new Point(202, 100));
      tinaCameraZPosSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraZPosSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraZPosSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraZPosSlider;
  }

  /**
   * This method initializes tinaCameraDOFSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getTinaCameraDOFSlider() {
    if (tinaCameraDOFSlider == null) {
      tinaCameraDOFSlider = new JSlider();
      tinaCameraDOFSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaCameraDOFSlider.setMaximum(100);
      tinaCameraDOFSlider.setMinimum(0);
      tinaCameraDOFSlider.setValue(0);
      tinaCameraDOFSlider.setSize(new Dimension(220, 19));
      tinaCameraDOFSlider.setLocation(new Point(686, 100));
      tinaCameraDOFSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraDOFSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.cameraDOFSlider_stateChanged(e);
        }
      });
    }
    return tinaCameraDOFSlider;
  }

  /**
   * This method initializes tinaCameraDOFREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getTinaCameraDOFREd() {
    if (tinaCameraDOFREd == null) {
      tinaCameraDOFREd = new JWFNumberField();
      tinaCameraDOFREd.setValueStep(0.01);
      tinaCameraDOFREd.setHasMinValue(true);
      tinaCameraDOFREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaCameraDOFREd.isMouseAdjusting() || tinaCameraDOFREd.getMouseChangeCount() == 0) {
            if (!tinaCameraDOFSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.cameraDOFREd_changed();
        }
      });
      tinaCameraDOFREd.setPreferredSize(new Dimension(100, 24));
      tinaCameraDOFREd.setText("");
      tinaCameraDOFREd.setSize(new Dimension(100, 24));
      tinaCameraDOFREd.setLocation(new Point(584, 100));
      tinaCameraDOFREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaCameraDOFREd;
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
      shadingLightBlueLbl.setLocation(new Point(739, 75));
      shadingLightBlueLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightGreenLbl = new JLabel();
      shadingLightGreenLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightGreenLbl.setText("Green");
      shadingLightGreenLbl.setSize(new Dimension(94, 22));
      shadingLightGreenLbl.setLocation(new Point(739, 51));
      shadingLightGreenLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightRedLbl = new JLabel();
      shadingLightRedLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightRedLbl.setText("Red");
      shadingLightRedLbl.setSize(new Dimension(94, 22));
      shadingLightRedLbl.setLocation(new Point(739, 27));
      shadingLightRedLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightZLbl = new JLabel();
      shadingLightZLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightZLbl.setText("Z position");
      shadingLightZLbl.setSize(new Dimension(94, 22));
      shadingLightZLbl.setLocation(new Point(369, 76));
      shadingLightZLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightYLbl = new JLabel();
      shadingLightYLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightYLbl.setText("Y position");
      shadingLightYLbl.setSize(new Dimension(94, 22));
      shadingLightYLbl.setLocation(new Point(369, 52));
      shadingLightYLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightXLbl = new JLabel();
      shadingLightXLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightXLbl.setText("X position");
      shadingLightXLbl.setSize(new Dimension(94, 22));
      shadingLightXLbl.setLocation(new Point(369, 28));
      shadingLightXLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightLbl = new JLabel();
      shadingLightLbl.setPreferredSize(new Dimension(94, 22));
      shadingLightLbl.setText("Light source");
      shadingLightLbl.setSize(new Dimension(94, 22));
      shadingLightLbl.setLocation(new Point(369, 4));
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
          tinaController.shadingAmbientREd_changed();
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
          tinaController.shadingDiffuseREd_changed();
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
          tinaController.shadingAmbientSlider_changed();
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
          tinaController.shadingDiffuseSlider_changed();
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
          tinaController.shadingPhongREd_changed();
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
          tinaController.shadingPhongSlider_changed();
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
          tinaController.shadingPhongSizeREd_changed();
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
          tinaController.shadingPhongSizeSlider_changed();
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
      shadingLightCmb.setLocation(new Point(465, 4));
      shadingLightCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingLightCmb.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.shadingLightCmb_changed();
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
          tinaController.shadingLightXREd_changed();
        }
      });
      shadingLightXREd.setPreferredSize(new Dimension(100, 24));
      shadingLightXREd.setText("");
      shadingLightXREd.setSize(new Dimension(100, 24));
      shadingLightXREd.setLocation(new Point(465, 28));
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
      shadingLightXSlider.setLocation(new Point(567, 28));
      shadingLightXSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightXSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.shadingLightXSlider_changed();
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
          tinaController.shadingLightYREd_changed();
        }
      });
      shadingLightYREd.setPreferredSize(new Dimension(100, 24));
      shadingLightYREd.setText("");
      shadingLightYREd.setSize(new Dimension(100, 24));
      shadingLightYREd.setLocation(new Point(465, 52));
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
      shadingLightYSlider.setLocation(new Point(567, 52));
      shadingLightYSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightYSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.shadingLightYSlider_changed();
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
          tinaController.shadingLightZREd_changed();
        }
      });
      shadingLightZREd.setPreferredSize(new Dimension(100, 24));
      shadingLightZREd.setText("");
      shadingLightZREd.setSize(new Dimension(100, 24));
      shadingLightZREd.setLocation(new Point(465, 76));
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
      shadingLightZSlider.setLocation(new Point(567, 76));
      shadingLightZSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightZSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.shadingLightZSlider_changed();
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
          tinaController.shadingLightRedREd_changed();
        }
      });
      shadingLightRedREd.setPreferredSize(new Dimension(100, 24));
      shadingLightRedREd.setText("");
      shadingLightRedREd.setSize(new Dimension(100, 24));
      shadingLightRedREd.setLocation(new Point(835, 27));
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
      shadingLightRedSlider.setLocation(new Point(937, 27));
      shadingLightRedSlider.setSize(new Dimension(120, 19));
      shadingLightRedSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightRedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.shadingLightRedSlider_changed();
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
          tinaController.shadingLightGreenREd_changed();
        }
      });
      shadingLightGreenREd.setPreferredSize(new Dimension(100, 24));
      shadingLightGreenREd.setText("");
      shadingLightGreenREd.setSize(new Dimension(100, 24));
      shadingLightGreenREd.setLocation(new Point(835, 51));
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
      shadingLightGreenSlider.setLocation(new Point(937, 51));
      shadingLightGreenSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightGreenSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.shadingLightGreenSlider_changed();
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
          tinaController.shadingLightBlueREd_changed();
        }
      });
      shadingLightBlueREd.setPreferredSize(new Dimension(100, 24));
      shadingLightBlueREd.setText("");
      shadingLightBlueREd.setSize(new Dimension(100, 24));
      shadingLightBlueREd.setLocation(new Point(835, 75));
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
      shadingLightBlueSlider.setLocation(new Point(937, 75));
      shadingLightBlueSlider.setPreferredSize(new Dimension(120, 19));
      shadingLightBlueSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.shadingLightBlueSlider_changed();
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
      loadFromClipboardFlameButton.setPreferredSize(new Dimension(125, 24));
      loadFromClipboardFlameButton.setText("From Clipboard");
      loadFromClipboardFlameButton.setBounds(new Rectangle(504, 7, 125, 24));
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
      saveFlameToClipboardButton.setPreferredSize(new Dimension(125, 24));
      saveFlameToClipboardButton.setText("To Clipboard");
      saveFlameToClipboardButton.setBounds(new Rectangle(643, 7, 125, 24));
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
      mouseTransformSlowButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/move.gif")));
      mouseTransformSlowButton.setSelected(false);
      mouseTransformSlowButton.setText("Fine");
      mouseTransformSlowButton.setSize(new Dimension(82, 24));
      mouseTransformSlowButton.setLocation(new Point(4, 4));
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
      batchRenderTotalProgressLbl = new JLabel();
      batchRenderTotalProgressLbl.setPreferredSize(new Dimension(94, 22));
      batchRenderTotalProgressLbl.setText("Total progress");
      batchRenderTotalProgressLbl.setSize(new Dimension(94, 22));
      batchRenderTotalProgressLbl.setLocation(new Point(8, 442));
      batchRenderTotalProgressLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderJobProgressLbl = new JLabel();
      batchRenderJobProgressLbl.setPreferredSize(new Dimension(94, 22));
      batchRenderJobProgressLbl.setText("Job progress");
      batchRenderJobProgressLbl.setSize(new Dimension(94, 22));
      batchRenderJobProgressLbl.setLocation(new Point(8, 415));
      batchRenderJobProgressLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderPanel = new JPanel();
      batchRenderPanel.setLayout(null);
      batchRenderPanel.add(getRenderBatchJobsScrollPane(), null);
      batchRenderPanel.add(getBatchRenderAddFilesButton(), null);
      batchRenderPanel.add(getBatchRenderFilesMoveUpButton(), null);
      batchRenderPanel.add(getBatchRenderFilesMoveDownButton(), null);
      batchRenderPanel.add(getBatchRenderJobProgressBar(), null);
      batchRenderPanel.add(getBatchRenderTotalProgressBar(), null);
      batchRenderPanel.add(getBatchRenderFilesRemoveButton(), null);
      batchRenderPanel.add(getBatchRenderFilesRemoveAllButton(), null);
      batchRenderPanel.add(batchRenderJobProgressLbl, null);
      batchRenderPanel.add(batchRenderTotalProgressLbl, null);
      batchRenderPanel.add(getBatchRenderStartButton(), null);

      batchQualityProfileCmb = new JComboBox();
      batchQualityProfileCmb.setPreferredSize(new Dimension(125, 22));
      batchQualityProfileCmb.setMaximumRowCount(32);
      batchQualityProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      batchQualityProfileCmb.setBounds(new Rectangle(231, 7, 125, 22));
      batchQualityProfileCmb.setBounds(680, 385, 125, 22);
      batchRenderPanel.add(batchQualityProfileCmb);

      batchResolutionProfileCmb = new JComboBox();
      batchResolutionProfileCmb.setPreferredSize(new Dimension(125, 22));
      batchResolutionProfileCmb.setMaximumRowCount(32);
      batchResolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      batchResolutionProfileCmb.setBounds(new Rectangle(231, 7, 125, 22));
      batchResolutionProfileCmb.setBounds(680, 357, 125, 22);
      batchRenderPanel.add(batchResolutionProfileCmb);
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
      rootTabbedPane.addTab("Flame Editor", null, getRootPanel(), null);
      rootTabbedPane.addTab("Interactive Renderer", null, getInteractiveRenderPanel(), null);
      rootTabbedPane.addTab("JWFMovie Maker", null, getTinaSWFAnimatorPanel(), null);
      rootTabbedPane.addTab("Batch Flame Renderer", null, getBatchRenderPanel(), null);

      JPanel helpPanel = new JPanel();
      rootTabbedPane.addTab("Help/About", null, helpPanel, null);
      helpPanel.setLayout(new BorderLayout(0, 0));
      helpPanel.add(getScrollPane(), BorderLayout.CENTER);
      rootTabbedPane.addTab("FAQ", null, getPanel(), null);
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
  private JComboBox interactiveQualityProfileCmb;
  private JButton cancelRenderButton;
  private JTextField swfAnimatorFramesPerSecondREd;
  private JPanel panel_5;
  private JButton swfAnimatorLoadFlameFromMainButton;
  private JButton swfAnimatorLoadFlameFromClipboardButton;
  private JButton swfAnimatorLoadFlameButton;
  private JLabel label_1;
  private JLabel label_2;
  private JToggleButton swfAnimatorHalfSizeButton;
  private JComboBox swfAnimatorResolutionProfileCmb;
  private JLabel label_3;
  private JLabel label_4;
  private JComboBox swfAnimatorQualityProfileCmb;
  private JProgressBar swfAnimatorProgressBar;
  private JButton swfAnimatorCancelButton;
  private JButton swfAnimatorLoadSoundButton;
  private JButton swfAnimatorClearSoundButton;
  private JPanel swfAnimatorPanel_1;
  private JSlider swfAnimatorFrameSlider;
  private JLabel swfAnimatorSoundCaptionLbl;
  private JTextField swfAnimatorFrameREd;
  private JPanel swfAnimatorFlamesPanel;
  private ButtonGroup swfAnimatorFlamesButtonGroup;
  private JPanel panel_14;
  private JPanel swfAnimatorPreviewRootPanel;
  private JPanel panel_4;
  private JComboBox swfAnimatorOutputCmb;
  private JButton swfAnimatorRemoveFlameButton;
  private JButton swfAnimatorRemoveAllFlamesButton;
  private JButton swfAnimatorMoveUpButton;
  private JButton swfAnimatorMoveDownButton;
  private JButton swfAnimatorMovieFromClipboardButton;
  private JButton swfAnimatorMovieFromDiskButton;
  private JButton swfAnimatorMovieToClipboardButton;
  private JButton swfAnimatorMovieToDiskButton;
  private JButton tinaAppendToMovieButton;
  private JButton swfAnimatorFrameToEditorBtn;
  private JButton swfAnimatorPlayButton;
  private JButton tinaWrapIntoSubFlameButton;
  private JTextField swfAnimatorFromFrameREd;
  private JTextField swfAnimatorToFrameREd;
  private JWFNumberField transformationWeightREd;
  private JWFNumberField relWeightREd;
  private JButton scriptLoadButton;
  private JButton scriptSaveButton;
  private JButton scriptWrapHeartButton;
  private JButton btnWrapIntoBubble;
  private JScrollPane scrollPane;
  private JTextPane helpPane;
  private JPanel panel;
  private JScrollPane scrollPane_1;
  private JTextPane faqPane;
  private JToggleButton mouseTransformShearButton;
  private JButton undoButton;
  private JLabel label_5;
  private JButton redoButton;
  private JButton interactivePauseButton;
  private JButton interactiveResumeButton;

  /**
   * This method initializes renderBatchJobsScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getRenderBatchJobsScrollPane() {
    if (renderBatchJobsScrollPane == null) {
      renderBatchJobsScrollPane = new JScrollPane();
      renderBatchJobsScrollPane.setSize(new Dimension(667, 399));
      renderBatchJobsScrollPane.setLocation(new Point(8, 8));
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
      batchRenderAddFilesButton.setPreferredSize(new Dimension(125, 24));
      batchRenderAddFilesButton.setText("Add files");
      batchRenderAddFilesButton.setSize(new Dimension(125, 24));
      batchRenderAddFilesButton.setLocation(new Point(680, 8));
      batchRenderAddFilesButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderAddFilesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.batchRenderAddFilesButton_clicked();
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
      batchRenderFilesMoveUpButton.setPreferredSize(new Dimension(125, 24));
      batchRenderFilesMoveUpButton.setText("Move up");
      batchRenderFilesMoveUpButton.setSize(new Dimension(125, 24));
      batchRenderFilesMoveUpButton.setLocation(new Point(680, 144));
      batchRenderFilesMoveUpButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderFilesMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.batchRenderFilesMoveUpButton_clicked();
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
      batchRenderFilesMoveDownButton.setBounds(new Rectangle(680, 115, 125, 24));
      batchRenderFilesMoveDownButton.setPreferredSize(new Dimension(125, 24));
      batchRenderFilesMoveDownButton.setText("Move down");
      batchRenderFilesMoveDownButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderFilesMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.batchRenderFilesMoveDownButton_clicked();
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
      batchRenderJobProgressBar.setBounds(new Rectangle(105, 416, 568, 21));
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
      batchRenderTotalProgressBar.setSize(new Dimension(568, 21));
      batchRenderTotalProgressBar.setLocation(new Point(105, 443));
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
      batchRenderFilesRemoveButton.setPreferredSize(new Dimension(125, 24));
      batchRenderFilesRemoveButton.setText("Remove");
      batchRenderFilesRemoveButton.setSize(new Dimension(125, 24));
      batchRenderFilesRemoveButton.setLocation(new Point(680, 239));
      batchRenderFilesRemoveButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderFilesRemoveButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.batchRenderFilesRemoveButton_clicked();
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
      batchRenderFilesRemoveAllButton.setPreferredSize(new Dimension(125, 24));
      batchRenderFilesRemoveAllButton.setText("Remove All");
      batchRenderFilesRemoveAllButton.setSize(new Dimension(125, 24));
      batchRenderFilesRemoveAllButton.setLocation(new Point(680, 270));
      batchRenderFilesRemoveAllButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderFilesRemoveAllButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.batchRenderFilesRemoveAllButton_clicked();
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
      batchRenderStartButton.setPreferredSize(new Dimension(125, 52));
      batchRenderStartButton.setText("Render");
      batchRenderStartButton.setLocation(new Point(680, 414));
      batchRenderStartButton.setSize(new Dimension(125, 52));
      batchRenderStartButton.setFont(new Font("Dialog", Font.BOLD, 10));
      batchRenderStartButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.batchRenderStartButton_clicked();
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
      affineFlipHorizontalButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/flipX.gif")));
      affineFlipHorizontalButton.setText("");
      affineFlipHorizontalButton.setToolTipText("Horizontal flip");
      affineFlipHorizontalButton.setSize(new Dimension(55, 24));
      affineFlipHorizontalButton.setLocation(new Point(145, 138));
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
      affineFlipVerticalButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/flipY.gif")));
      affineFlipVerticalButton.setText("");
      affineFlipVerticalButton.setSize(new Dimension(55, 24));
      affineFlipVerticalButton.setLocation(new Point(219, 138));
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
   * This method initializes darkTrianglesToggleButton	
   * 	
   * @return javax.swing.JToggleButton	
   */
  private JToggleButton getDarkTrianglesToggleButton() {
    if (darkTrianglesToggleButton == null) {
      darkTrianglesToggleButton = new JToggleButton();
      darkTrianglesToggleButton.setBounds(new Rectangle(556, 4, 82, 24));
      darkTrianglesToggleButton.setToolTipText("Toggle dark triangle colors");
      darkTrianglesToggleButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/triangle.gif")));
      darkTrianglesToggleButton.setSelected(false);
      darkTrianglesToggleButton.setText("Dark");
      darkTrianglesToggleButton.setPreferredSize(new Dimension(42, 24));
      darkTrianglesToggleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.toggleDarkTriangles();
        }
      });
    }
    return darkTrianglesToggleButton;
  }

  /**
   * This method initializes shadingPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getShadingPanel() {
    if (shadingPanel == null) {
      shadingLbl = new JLabel();
      shadingLbl.setPreferredSize(new Dimension(94, 22));
      shadingLbl.setText("Shading");
      shadingLbl.setSize(new Dimension(94, 22));
      shadingLbl.setLocation(new Point(4, 4));
      shadingLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      shadingPanel = new JPanel();
      shadingPanel.setLayout(null);
      shadingPanel.add(shadingLbl, null);
      shadingPanel.add(getShadingCmb(), null);
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
      shadingCmb.setPreferredSize(new Dimension(125, 22));
      shadingCmb.setSelectedItem(Shading.FLAT);
      shadingCmb.setSize(new Dimension(125, 22));
      shadingCmb.setLocation(new Point(100, 4));
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
          tinaController.shadingBlurRadiusREd_changed();
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
          tinaController.shadingBlurRadiusSlider_changed();
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
          tinaController.shadingBlurFadeREd_changed();
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
          tinaController.shadingBlurFadeSlider_changed();
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
          tinaController.shadingBlurFallOffREd_changed();
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
          tinaController.shadingBlurFallOffSlider_changed();
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
      scriptPanel.setLayout(null);
      scriptPanel.add(getScriptScrollPane(), null);
      scriptPanel.add(getCompileScriptButton(), null);
      scriptPanel.add(getRunScriptButton(), null);

      JButton scriptEscherFluxButton = new JButton();
      scriptEscherFluxButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.scriptExampleEscherFlux();
        }
      });
      scriptEscherFluxButton.setToolTipText("Example: Textured Escher and Flux");
      scriptEscherFluxButton.setText("Escher flux");
      scriptEscherFluxButton.setSize(new Dimension(81, 24));
      scriptEscherFluxButton.setPreferredSize(new Dimension(81, 24));
      scriptEscherFluxButton.setLocation(new Point(97, 280));
      scriptEscherFluxButton.setFont(new Font("Dialog", Font.BOLD, 10));
      scriptEscherFluxButton.setBounds(9, 345, 125, 24);
      scriptPanel.add(scriptEscherFluxButton);

      JButton scriptSoftJulianButton = new JButton();
      scriptSoftJulianButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.scriptExampleSoftJulian();
        }
      });
      scriptSoftJulianButton.setToolTipText("Example: Soft Julian");
      scriptSoftJulianButton.setText("Soft Julian");
      scriptSoftJulianButton.setSize(new Dimension(81, 24));
      scriptSoftJulianButton.setPreferredSize(new Dimension(81, 24));
      scriptSoftJulianButton.setLocation(new Point(97, 280));
      scriptSoftJulianButton.setFont(new Font("Dialog", Font.BOLD, 10));
      scriptSoftJulianButton.setBounds(9, 373, 125, 24);
      scriptPanel.add(scriptSoftJulianButton);
      scriptPanel.add(getScriptLoadButton());
      scriptPanel.add(getScriptSaveButton());
      scriptPanel.add(getScriptWrapHeartButton());
      scriptPanel.add(getBtnWrapIntoBubble());
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
      scriptScrollPane.setBounds(new Rectangle(9, 13, 308, 263));
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
      compileScriptButton.setBounds(new Rectangle(9, 280, 125, 24));
      compileScriptButton.setPreferredSize(new Dimension(125, 24));
      compileScriptButton.setToolTipText("Compile the script");
      compileScriptButton.setText("Compile");
      compileScriptButton.setFont(new Font("Dialog", Font.BOLD, 10));
      compileScriptButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.compileScriptButton_clicked();
        }
      });
    }
    return compileScriptButton;
  }

  /**
   * This method initializes runScriptButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRunScriptButton() {
    if (runScriptButton == null) {
      runScriptButton = new JButton();
      runScriptButton.setPreferredSize(new Dimension(125, 24));
      runScriptButton.setToolTipText("Compile and run the script");
      runScriptButton.setText("Run");
      runScriptButton.setSize(new Dimension(125, 24));
      runScriptButton.setLocation(new Point(192, 280));
      runScriptButton.setFont(new Font("Dialog", Font.BOLD, 10));
      runScriptButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.runScriptButton_clicked();
        }
      });
    }
    return runScriptButton;
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
      affineScaleXButton.setLocation(new Point(65, 138));
      affineScaleXButton.setSize(new Dimension(26, 36));
      affineScaleXButton.setSelected(true);
      affineScaleXButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/allowScaleX.gif")));
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
      affineScaleYButton.setSize(new Dimension(26, 36));
      affineScaleYButton.setLocation(new Point(93, 138));
      affineScaleYButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/allowScaleY.gif")));
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
      randomizeColorsButton.setBounds(6, 32, 180, 24);
      randomizeColorsButton.setFont(new Font("Dialog", Font.BOLD, 10));
      randomizeColorsButton.setText("Randomize colors");
      randomizeColorsButton.setPreferredSize(new Dimension(180, 24));
      randomizeColorsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.randomizeColorsBtn_clicked();
        }
      });
    }
    return randomizeColorsButton;
  }

  /**
   * This method initializes tinaPaletteSwapRGBREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getTinaPaletteSwapRGBREd() {
    if (tinaPaletteSwapRGBREd == null) {
      tinaPaletteSwapRGBREd = new JWFNumberField();
      tinaPaletteSwapRGBREd.setValueStep(5.0);
      tinaPaletteSwapRGBREd.setOnlyIntegers(true);
      tinaPaletteSwapRGBREd.setMinValue(-255.0);
      tinaPaletteSwapRGBREd.setMaxValue(255.0);
      tinaPaletteSwapRGBREd.setHasMinValue(true);
      tinaPaletteSwapRGBREd.setHasMaxValue(true);
      tinaPaletteSwapRGBREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaPaletteSwapRGBREd.isMouseAdjusting() || tinaPaletteSwapRGBREd.getMouseChangeCount() == 0) {
            if (!tinaPaletteSwapRGBSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.paletteSwapRGBREd_changed();
        }
      });
      tinaPaletteSwapRGBREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteSwapRGBREd.setText("0");
      tinaPaletteSwapRGBREd.setSize(new Dimension(56, 24));
      tinaPaletteSwapRGBREd.setLocation(new Point(60, 238));
      tinaPaletteSwapRGBREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaPaletteSwapRGBREd;
  }

  /**
   * This method initializes tinaPaletteSwapRGBSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getTinaPaletteSwapRGBSlider() {
    if (tinaPaletteSwapRGBSlider == null) {
      tinaPaletteSwapRGBSlider = new JSlider();
      tinaPaletteSwapRGBSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaPaletteSwapRGBSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteSwapRGBSlider.setMaximum(255);
      tinaPaletteSwapRGBSlider.setMinimum(-255);
      tinaPaletteSwapRGBSlider.setValue(0);
      tinaPaletteSwapRGBSlider.setSize(new Dimension(74, 22));
      tinaPaletteSwapRGBSlider.setLocation(new Point(116, 238));
      tinaPaletteSwapRGBSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSwapRGBSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteSwapRGBSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteSwapRGBSlider;
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
      gradientLibraryPanel.add(getGradientLibrarySouthPanel(), BorderLayout.SOUTH);
      gradientLibraryPanel.add(getGradientLibraryCenterPanel(), BorderLayout.CENTER);
    }
    return gradientLibraryPanel;
  }

  /**
   * This method initializes gradientLibrarySouthPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getGradientLibrarySouthPanel() {
    if (gradientLibrarySouthPanel == null) {
      gradientLibrarySouthPanel = new JPanel();
      gradientLibrarySouthPanel.setLayout(null);
      gradientLibrarySouthPanel.setPreferredSize(new Dimension(0, 30));
      gradientLibrarySouthPanel.add(getGradientLibraryGradientCmb(), null);
    }
    return gradientLibrarySouthPanel;
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
    }
    return gradientLibraryCenterPanel;
  }

  /**
   * This method initializes gradientLibraryGradientCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getGradientLibraryGradientCmb() {
    if (gradientLibraryGradientCmb == null) {
      gradientLibraryGradientCmb = new JComboBox();
      gradientLibraryGradientCmb.setPreferredSize(new Dimension(126, 22));
      gradientLibraryGradientCmb.setMaximumRowCount(32);
      gradientLibraryGradientCmb.setSize(new Dimension(182, 22));
      gradientLibraryGradientCmb.setLocation(new Point(4, 4));
      gradientLibraryGradientCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      gradientLibraryGradientCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (tinaController != null) {
            tinaController.gradientLibraryGradientChanged();
          }
        }
      });
    }
    return gradientLibraryGradientCmb;
  }

  private JPanel getInteractiveNorthPanel() {
    if (interactiveNorthPanel == null) {
      interactiveNorthPanel = new JPanel();
      interactiveNorthPanel.setPreferredSize(new Dimension(0, 86));
      interactiveNorthPanel.setSize(new Dimension(0, 42));
      interactiveNorthPanel.setLayout(null);
      interactiveNorthPanel.add(getInteractiveNextButton());
      interactiveNorthPanel.add(getInteractiveLoadFlameFromClipboardButton());
      interactiveNorthPanel.add(getInteractiveLoadFlameButton());
      interactiveNorthPanel.add(getInteractiveSaveImageButton());
      interactiveNorthPanel.add(getInteractiveSaveFlameButton());
      interactiveNorthPanel.add(getInteractiveStopButton());
      interactiveNorthPanel.add(getInteractiveFlameToClipboardButton());

      JLabel label = new JLabel();
      label.setText("Random generator");
      label.setPreferredSize(new Dimension(94, 22));
      label.setFont(new Font("Dialog", Font.BOLD, 10));
      label.setBounds(new Rectangle(135, 7, 94, 22));
      label.setBounds(529, 8, 94, 22);
      interactiveNorthPanel.add(label);

      interactiveRandomStyleCmb = new JComboBox();
      interactiveRandomStyleCmb.setPreferredSize(new Dimension(125, 22));
      interactiveRandomStyleCmb.setMaximumRowCount(32);
      interactiveRandomStyleCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveRandomStyleCmb.setBounds(new Rectangle(231, 7, 125, 22));
      interactiveRandomStyleCmb.setBounds(622, 8, 125, 22);
      interactiveRandomStyleCmb.setMaximumRowCount(32);
      interactiveRandomStyleCmb.removeAllItems();
      for (String name : RandomFlameGeneratorList.getNameList()) {
        interactiveRandomStyleCmb.addItem(name);
      }
      interactiveRandomStyleCmb.setSelectedItem(RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);

      interactiveNorthPanel.add(interactiveRandomStyleCmb);

      interactiveHalfSizeButton = new JToggleButton();
      interactiveHalfSizeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().halveSizeButton_clicked();
        }
      });
      interactiveHalfSizeButton.setToolTipText("Switch to half render resolution (to get rid of scroll bars in exploration mode)");
      interactiveHalfSizeButton.setText("Half size");
      interactiveHalfSizeButton.setPreferredSize(new Dimension(42, 24));
      interactiveHalfSizeButton.setMnemonic(KeyEvent.VK_M);
      interactiveHalfSizeButton.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveHalfSizeButton.setBounds(364, 8, 125, 24);
      interactiveNorthPanel.add(interactiveHalfSizeButton);
      interactiveNorthPanel.add(getInteractiveResolutionProfileCmb());

      interactiveQualityProfileCmb = new JComboBox();
      interactiveQualityProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getInteractiveRendererCtrl().qualityProfile_changed();
          }
        }
      });
      interactiveQualityProfileCmb.setPreferredSize(new Dimension(125, 22));
      interactiveQualityProfileCmb.setMaximumRowCount(32);
      interactiveQualityProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveQualityProfileCmb.setBounds(new Rectangle(231, 7, 125, 22));
      interactiveQualityProfileCmb.setBounds(364, 59, 125, 22);
      interactiveNorthPanel.add(interactiveQualityProfileCmb);

      interactiveLoadFlameFromMainButton = new JButton();
      interactiveLoadFlameFromMainButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().fromEditorButton_clicked();
        }
      });
      interactiveLoadFlameFromMainButton.setToolTipText("Load flame from Editor and render");
      interactiveLoadFlameFromMainButton.setText("From Editor");
      interactiveLoadFlameFromMainButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameFromMainButton.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveLoadFlameFromMainButton.setBounds(new Rectangle(8, 32, 125, 24));
      interactiveLoadFlameFromMainButton.setBounds(8, 8, 125, 24);
      interactiveNorthPanel.add(interactiveLoadFlameFromMainButton);

      interactiveFlameToEditorButton = new JButton();
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
      interactiveFlameToEditorButton.setBounds(new Rectangle(806, 32, 125, 24));
      interactiveFlameToEditorButton.setBounds(950, 7, 125, 24);
      interactiveNorthPanel.add(interactiveFlameToEditorButton);
      interactiveNorthPanel.add(getLabel_1());
      interactiveNorthPanel.add(getLabel_2());

      interactivePauseButton = new JButton();
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
      interactivePauseButton.setBounds(new Rectangle(754, 32, 125, 24));
      interactivePauseButton.setBounds(786, 6, 125, 24);
      interactiveNorthPanel.add(interactivePauseButton);

      interactiveResumeButton = new JButton();
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
      interactiveResumeButton.setBounds(new Rectangle(754, 32, 125, 24));
      interactiveResumeButton.setBounds(145, 32, 125, 24);
      interactiveNorthPanel.add(interactiveResumeButton);
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
      interactiveNextButton.setBounds(new Rectangle(529, 32, 218, 48));
    }
    return interactiveNextButton;
  }

  private JButton getInteractiveLoadFlameFromClipboardButton() {
    if (interactiveLoadFlameFromClipboardButton == null) {
      interactiveLoadFlameFromClipboardButton = new JButton();
      interactiveLoadFlameFromClipboardButton.setToolTipText("Load flame from clipboard and render");
      interactiveLoadFlameFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().fromClipboardButton_clicked();
        }
      });
      interactiveLoadFlameFromClipboardButton.setText("From Clipboard");
      interactiveLoadFlameFromClipboardButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameFromClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveLoadFlameFromClipboardButton.setBounds(new Rectangle(8, 32, 125, 24));
    }
    return interactiveLoadFlameFromClipboardButton;
  }

  private JButton getInteractiveLoadFlameButton() {
    if (interactiveLoadFlameButton == null) {
      interactiveLoadFlameButton = new JButton();
      interactiveLoadFlameButton.setToolTipText("Load flame from file and render");
      interactiveLoadFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().loadFlameButton_clicked();
        }
      });
      interactiveLoadFlameButton.setText("Load Flame");
      interactiveLoadFlameButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveLoadFlameButton.setBounds(new Rectangle(8, 56, 125, 24));
    }
    return interactiveLoadFlameButton;
  }

  private JButton getInteractiveFlameToClipboardButton() {
    if (interactiveFlameToClipboardButton == null) {
      interactiveFlameToClipboardButton = new JButton();
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
      interactiveFlameToClipboardButton.setBounds(new Rectangle(950, 32, 125, 24));
    }
    return interactiveFlameToClipboardButton;
  }

  private JButton getInteractiveStopButton() {
    if (interactiveStopButton == null) {
      interactiveStopButton = new JButton();
      interactiveStopButton.setToolTipText("Stop the render and free associated ressources");
      interactiveStopButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          int response = JOptionPane.showConfirmDialog(null, "Do you really want to cancel rendering?", "Confirm",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
          if (response == JOptionPane.YES_OPTION) {
            tinaController.getInteractiveRendererCtrl().stopButton_clicked();
          }
        }
      });
      interactiveStopButton.setText("Stop");
      interactiveStopButton.setPreferredSize(new Dimension(125, 24));
      interactiveStopButton.setMnemonic(KeyEvent.VK_D);
      interactiveStopButton.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveStopButton.setBounds(new Rectangle(1114, 32, 56, 24));
    }
    return interactiveStopButton;
  }

  private JButton getInteractiveSaveFlameButton() {
    if (interactiveSaveFlameButton == null) {
      interactiveSaveFlameButton = new JButton();
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
      interactiveSaveFlameButton.setBounds(new Rectangle(950, 56, 125, 24));
    }
    return interactiveSaveFlameButton;
  }

  private JButton getInteractiveSaveImageButton() {
    if (interactiveSaveImageButton == null) {
      interactiveSaveImageButton = new JButton();
      interactiveSaveImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().saveImageButton_clicked();
        }
      });
      interactiveSaveImageButton.setText("Save image");
      interactiveSaveImageButton.setPreferredSize(new Dimension(125, 24));
      interactiveSaveImageButton.setMnemonic(KeyEvent.VK_I);
      interactiveSaveImageButton.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveSaveImageButton.setBounds(new Rectangle(786, 31, 125, 24));
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
      qualityProfileBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.editQualityProfiles();
        }
      });
      qualityProfileBtn.setToolTipText("Edit quality profiles");
      qualityProfileBtn.setText("...");
      qualityProfileBtn.setPreferredSize(new Dimension(125, 24));
      qualityProfileBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      qualityProfileBtn.setBounds(new Rectangle(508, 6, 125, 24));
      qualityProfileBtn.setBounds(1077, 35, 40, 24);
    }
    return qualityProfileBtn;
  }

  private JButton getResolutionProfileBtn() {
    if (resolutionProfileBtn == null) {
      resolutionProfileBtn = new JButton();
      resolutionProfileBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.editResolutionProfiles();
        }
      });
      resolutionProfileBtn.setToolTipText("Edit resolution profiles");
      resolutionProfileBtn.setText("...");
      resolutionProfileBtn.setPreferredSize(new Dimension(125, 24));
      resolutionProfileBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      resolutionProfileBtn.setBounds(new Rectangle(508, 6, 125, 24));
      resolutionProfileBtn.setBounds(1077, 7, 40, 24);
    }
    return resolutionProfileBtn;
  }

  private JComboBox getInteractiveResolutionProfileCmb() {
    if (interactiveResolutionProfileCmb == null) {
      interactiveResolutionProfileCmb = new JComboBox();
      interactiveResolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getInteractiveRendererCtrl() != null) {
            tinaController.getInteractiveRendererCtrl().resolutionProfile_changed();
          }
        }
      });
      interactiveResolutionProfileCmb.setPreferredSize(new Dimension(125, 22));
      interactiveResolutionProfileCmb.setMaximumRowCount(32);
      interactiveResolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      interactiveResolutionProfileCmb.setBounds(new Rectangle(231, 7, 125, 22));
      interactiveResolutionProfileCmb.setBounds(364, 34, 125, 22);
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

  public JComboBox getInteractiveQualityProfileCmb() {
    return interactiveQualityProfileCmb;
  }

  private JButton getCancelRenderButton() {
    if (cancelRenderButton == null) {
      cancelRenderButton = new JButton();
      cancelRenderButton.setVisible(false);
      cancelRenderButton.setToolTipText("");
      cancelRenderButton.setText("Cancel");
      cancelRenderButton.setPreferredSize(new Dimension(81, 24));
      cancelRenderButton.setFont(new Font("Dialog", Font.BOLD, 10));
      cancelRenderButton.setBounds(332, 5, 81, 24);
    }
    return cancelRenderButton;
  }

  public JTextField getSwfAnimatorFramesPerSecondREd() {
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
      panel_5.setBorder(new TitledBorder(null, "Movie", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_5.setPreferredSize(new Dimension(10, 100));
      panel_5.setLayout(null);

      swfAnimatorProgressBar = new JProgressBar();
      swfAnimatorProgressBar.setBounds(830, 68, 331, 21);
      panel_5.add(swfAnimatorProgressBar);
      swfAnimatorProgressBar.setValue(0);
      swfAnimatorProgressBar.setStringPainted(true);
      swfAnimatorProgressBar.setPreferredSize(new Dimension(568, 21));
      panel_5.add(getSwfAnimatorGenerateButton());

      swfAnimatorCancelButton = new JButton();
      swfAnimatorCancelButton.setBounds(1039, 12, 125, 52);
      panel_5.add(swfAnimatorCancelButton);
      swfAnimatorCancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().cancelButton_clicked();
        }
      });
      swfAnimatorCancelButton.setText("Cancel");
      swfAnimatorCancelButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorCancelButton.setFont(new Font("Dialog", Font.BOLD, 10));

      swfAnimatorOutputCmb = new JComboBox();
      swfAnimatorOutputCmb.setPreferredSize(new Dimension(125, 22));
      swfAnimatorOutputCmb.setMaximumRowCount(32);
      swfAnimatorOutputCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorOutputCmb.setBounds(902, 12, 125, 22);
      panel_5.add(swfAnimatorOutputCmb);

      JLabel lblOutput = new JLabel();
      lblOutput.setText("Output");
      lblOutput.setPreferredSize(new Dimension(94, 22));
      lblOutput.setFont(new Font("Dialog", Font.BOLD, 10));
      lblOutput.setBounds(834, 11, 71, 22);
      panel_5.add(lblOutput);
      panel_5.add(getSwfAnimatorHalfSizeButton());
      panel_5.add(getLabel_3());
      panel_5.add(getSwfAnimatorResolutionProfileCmb());
      panel_5.add(getLabel_4());
      panel_5.add(getSwfAnimatorQualityProfileCmb());
      panel_5.add(getSwfAnimatorMovieFromClipboardButton());
      panel_5.add(getSwfAnimatorMovieFromDiskButton());
      panel_5.add(getSwfAnimatorMovieToClipboardButton());
      panel_5.add(getSwfAnimatorMovieToDiskButton());

      swfAnimatorPlayButton = new JButton();
      swfAnimatorPlayButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().playButton_clicked();
        }
      });
      swfAnimatorPlayButton.setText("Play");
      swfAnimatorPlayButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorPlayButton.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorPlayButton.setBounds(518, 36, 125, 52);
      panel_5.add(swfAnimatorPlayButton);

      swfAnimatorFromFrameREd = new JTextField();
      swfAnimatorFromFrameREd.setPreferredSize(new Dimension(56, 22));
      swfAnimatorFromFrameREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorFromFrameREd.setBounds(908, 39, 56, 22);
      panel_5.add(swfAnimatorFromFrameREd);

      JLabel lblRange = new JLabel();
      lblRange.setText("Range");
      lblRange.setPreferredSize(new Dimension(94, 22));
      lblRange.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRange.setBounds(834, 38, 71, 22);
      panel_5.add(lblRange);

      swfAnimatorToFrameREd = new JTextField();
      swfAnimatorToFrameREd.setPreferredSize(new Dimension(56, 22));
      swfAnimatorToFrameREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      swfAnimatorToFrameREd.setBounds(967, 38, 56, 22);
      panel_5.add(swfAnimatorToFrameREd);
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
      swfAnimatorLoadFlameButton.setText("Add flame from disk");
      swfAnimatorLoadFlameButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorLoadFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorLoadFlameButton;
  }

  private JLabel getLabel_1() {
    if (label_1 == null) {
      label_1 = new JLabel();
      label_1.setText("Resolution");
      label_1.setPreferredSize(new Dimension(94, 22));
      label_1.setFont(new Font("Dialog", Font.BOLD, 10));
      label_1.setBounds(292, 32, 71, 22);
    }
    return label_1;
  }

  private JLabel getLabel_2() {
    if (label_2 == null) {
      label_2 = new JLabel();
      label_2.setText("Quality");
      label_2.setPreferredSize(new Dimension(94, 22));
      label_2.setFont(new Font("Dialog", Font.BOLD, 10));
      label_2.setBounds(292, 58, 71, 22);
    }
    return label_2;
  }

  private JToggleButton getSwfAnimatorHalfSizeButton() {
    if (swfAnimatorHalfSizeButton == null) {
      swfAnimatorHalfSizeButton = new JToggleButton();
      swfAnimatorHalfSizeButton.setBounds(89, 11, 125, 24);
      swfAnimatorHalfSizeButton.setSelected(true);
      swfAnimatorHalfSizeButton.setToolTipText("Switch to half render resolution (to get rid of scroll bars in exploration mode)");
      swfAnimatorHalfSizeButton.setText("Half size");
      swfAnimatorHalfSizeButton.setPreferredSize(new Dimension(42, 24));
      swfAnimatorHalfSizeButton.setMnemonic(KeyEvent.VK_M);
      swfAnimatorHalfSizeButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorHalfSizeButton;
  }

  private JComboBox getSwfAnimatorResolutionProfileCmb() {
    if (swfAnimatorResolutionProfileCmb == null) {
      swfAnimatorResolutionProfileCmb = new JComboBox();
      swfAnimatorResolutionProfileCmb.setBounds(88, 37, 125, 22);
      swfAnimatorResolutionProfileCmb.setPreferredSize(new Dimension(125, 22));
      swfAnimatorResolutionProfileCmb.setMaximumRowCount(32);
      swfAnimatorResolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorResolutionProfileCmb;
  }

  private JLabel getLabel_3() {
    if (label_3 == null) {
      label_3 = new JLabel();
      label_3.setBounds(20, 36, 71, 22);
      label_3.setText("Resolution");
      label_3.setPreferredSize(new Dimension(94, 22));
      label_3.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_3;
  }

  private JLabel getLabel_4() {
    if (label_4 == null) {
      label_4 = new JLabel();
      label_4.setBounds(20, 62, 71, 22);
      label_4.setText("Quality");
      label_4.setPreferredSize(new Dimension(94, 22));
      label_4.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_4;
  }

  private JComboBox getSwfAnimatorQualityProfileCmb() {
    if (swfAnimatorQualityProfileCmb == null) {
      swfAnimatorQualityProfileCmb = new JComboBox();
      swfAnimatorQualityProfileCmb.setBounds(88, 62, 125, 22);
      swfAnimatorQualityProfileCmb.setPreferredSize(new Dimension(125, 22));
      swfAnimatorQualityProfileCmb.setMaximumRowCount(32);
      swfAnimatorQualityProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorQualityProfileCmb;
  }

  public JProgressBar getSwfAnimatorProgressBar() {
    return swfAnimatorProgressBar;
  }

  public JButton getSwfAnimatorCancelButton() {
    return swfAnimatorCancelButton;
  }

  private JButton getSwfAnimatorLoadSoundButton() {
    if (swfAnimatorLoadSoundButton == null) {
      swfAnimatorLoadSoundButton = new JButton();
      swfAnimatorLoadSoundButton.setBounds(19, 279, 125, 24);
      swfAnimatorLoadSoundButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().loadSoundButton_clicked();
        }
      });
      swfAnimatorLoadSoundButton.setToolTipText("Load a *.wav or *.mp3 file");
      swfAnimatorLoadSoundButton.setText("Load sound");
      swfAnimatorLoadSoundButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorLoadSoundButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorLoadSoundButton;
  }

  public JButton getSwfAnimatorClearSoundButton() {
    return swfAnimatorClearSoundButton;
  }

  public JSlider getSwfAnimatorFrameSlider() {
    return swfAnimatorFrameSlider;
  }

  public JLabel getSwfAnimatorSoundCaptionLbl() {
    return swfAnimatorSoundCaptionLbl;
  }

  public JTextField getSwfAnimatorFrameREd() {
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
      panel_4.setBorder(new TitledBorder(null, "Reserved area for later ;-)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_4.setLayout(null);
    }
    return panel_4;
  }

  public JComboBox getSwfAnimatorOutputCmb() {
    return swfAnimatorOutputCmb;
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
      swfAnimatorMovieFromClipboardButton.setBounds(237, 12, 125, 24);
    }
    return swfAnimatorMovieFromClipboardButton;
  }

  private JButton getSwfAnimatorMovieFromDiskButton() {
    if (swfAnimatorMovieFromDiskButton == null) {
      swfAnimatorMovieFromDiskButton = new JButton();
      swfAnimatorMovieFromDiskButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieFromDiskButton_clicked();
        }
      });
      swfAnimatorMovieFromDiskButton.setText("Load Movie");
      swfAnimatorMovieFromDiskButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieFromDiskButton.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorMovieFromDiskButton.setBounds(new Rectangle(504, 35, 125, 24));
      swfAnimatorMovieFromDiskButton.setBounds(237, 40, 125, 24);
    }
    return swfAnimatorMovieFromDiskButton;
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
      swfAnimatorMovieToClipboardButton.setBounds(381, 12, 125, 24);
    }
    return swfAnimatorMovieToClipboardButton;
  }

  private JButton getSwfAnimatorMovieToDiskButton() {
    if (swfAnimatorMovieToDiskButton == null) {
      swfAnimatorMovieToDiskButton = new JButton();
      swfAnimatorMovieToDiskButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieToDiskButton_clicked();
        }
      });
      swfAnimatorMovieToDiskButton.setText("Save Movie");
      swfAnimatorMovieToDiskButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieToDiskButton.setFont(new Font("Dialog", Font.BOLD, 10));
      swfAnimatorMovieToDiskButton.setBounds(new Rectangle(643, 35, 125, 24));
      swfAnimatorMovieToDiskButton.setBounds(381, 40, 125, 24);
    }
    return swfAnimatorMovieToDiskButton;
  }

  private JButton getTinaAppendToMovieButton() {
    if (tinaAppendToMovieButton == null) {
      tinaAppendToMovieButton = new JButton();
      tinaAppendToMovieButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.appendToMovieButton_actionPerformed(e);
        }
      });
      tinaAppendToMovieButton.setToolTipText("Append to movie");
      tinaAppendToMovieButton.setText("Movie");
      tinaAppendToMovieButton.setPreferredSize(new Dimension(60, 36));
      tinaAppendToMovieButton.setMnemonic(KeyEvent.VK_M);
      tinaAppendToMovieButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return tinaAppendToMovieButton;
  }

  public JButton getSwfAnimatorFrameToEditorBtn() {
    return swfAnimatorFrameToEditorBtn;
  }

  public JButton getSwfAnimatorPlayButton() {
    return swfAnimatorPlayButton;
  }

  private JButton getTinaWrapIntoSubFlameButton() {
    if (tinaWrapIntoSubFlameButton == null) {
      tinaWrapIntoSubFlameButton = new JButton();
      tinaWrapIntoSubFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.tinaWrapIntoSubFlameButton_clicked();
        }
      });
      tinaWrapIntoSubFlameButton.setToolTipText("Wrap into sub flame");
      tinaWrapIntoSubFlameButton.setText("SF");
      tinaWrapIntoSubFlameButton.setPreferredSize(new Dimension(60, 36));
      tinaWrapIntoSubFlameButton.setMnemonic(KeyEvent.VK_S);
      tinaWrapIntoSubFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return tinaWrapIntoSubFlameButton;
  }

  public JTextField getSwfAnimatorToFrameREd() {
    return swfAnimatorToFrameREd;
  }

  public JTextField getSwfAnimatorFromFrameREd() {
    return swfAnimatorFromFrameREd;
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
      transformationWeightREd.setPreferredSize(new Dimension(81, 24));
      transformationWeightREd.setLocation(new Point(238, 6));
      transformationWeightREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return transformationWeightREd;
  }

  public JWFNumberField getRelWeightREd() {
    return relWeightREd;
  }

  private JButton getScriptLoadButton() {
    if (scriptLoadButton == null) {
      scriptLoadButton = new JButton();
      scriptLoadButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.loadScript();
        }
      });
      scriptLoadButton.setToolTipText("Load the script from disk");
      scriptLoadButton.setText("Load script");
      scriptLoadButton.setPreferredSize(new Dimension(125, 24));
      scriptLoadButton.setFont(new Font("Dialog", Font.BOLD, 10));
      scriptLoadButton.setBounds(new Rectangle(9, 280, 125, 24));
      scriptLoadButton.setBounds(9, 309, 125, 24);
    }
    return scriptLoadButton;
  }

  private JButton getScriptSaveButton() {
    if (scriptSaveButton == null) {
      scriptSaveButton = new JButton();
      scriptSaveButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.saveScript();
        }
      });
      scriptSaveButton.setToolTipText("Save the script to disk");
      scriptSaveButton.setText("Save script");
      scriptSaveButton.setPreferredSize(new Dimension(125, 24));
      scriptSaveButton.setFont(new Font("Dialog", Font.BOLD, 10));
      scriptSaveButton.setBounds(new Rectangle(9, 280, 125, 24));
      scriptSaveButton.setBounds(192, 306, 125, 24);
    }
    return scriptSaveButton;
  }

  private JButton getScriptWrapHeartButton() {
    if (scriptWrapHeartButton == null) {
      scriptWrapHeartButton = new JButton();
      scriptWrapHeartButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.scriptExampleWrapHeart();
        }
      });
      scriptWrapHeartButton.setToolTipText("Example: Wrap into heart");
      scriptWrapHeartButton.setText("Wrap into heart");
      scriptWrapHeartButton.setSize(new Dimension(81, 24));
      scriptWrapHeartButton.setPreferredSize(new Dimension(81, 24));
      scriptWrapHeartButton.setLocation(new Point(97, 280));
      scriptWrapHeartButton.setFont(new Font("Dialog", Font.BOLD, 10));
      scriptWrapHeartButton.setBounds(192, 345, 125, 24);
    }
    return scriptWrapHeartButton;
  }

  private JButton getBtnWrapIntoBubble() {
    if (btnWrapIntoBubble == null) {
      btnWrapIntoBubble = new JButton();
      btnWrapIntoBubble.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.scriptExampleWrapBubble();
        }
      });
      btnWrapIntoBubble.setToolTipText("Example: Wrap into bubble");
      btnWrapIntoBubble.setText("Wrap into bubble");
      btnWrapIntoBubble.setSize(new Dimension(81, 24));
      btnWrapIntoBubble.setPreferredSize(new Dimension(81, 24));
      btnWrapIntoBubble.setLocation(new Point(97, 280));
      btnWrapIntoBubble.setFont(new Font("Dialog", Font.BOLD, 10));
      btnWrapIntoBubble.setBounds(192, 373, 125, 24);
    }
    return btnWrapIntoBubble;
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setPreferredSize(new Dimension(6, 400));
      scrollPane.setSize(new Dimension(0, 400));
      scrollPane.setViewportView(getHelpPane());
    }
    return scrollPane;
  }

  private JTextPane getHelpPane() {
    if (helpPane == null) {
      helpPane = new JTextPane();
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

  private JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel();
      panel.setLayout(new BorderLayout(0, 0));
      panel.add(getScrollPane_1(), BorderLayout.CENTER);
    }
    return panel;
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
    return mouseTransformShearButton;
  }

  private JButton getUndoButton() {
    if (undoButton == null) {
      undoButton = new JButton();
      undoButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.undoAction();
        }
      });
      undoButton.setToolTipText("Undo");
      undoButton.setText("Undo");
      undoButton.setPreferredSize(new Dimension(60, 36));
      undoButton.setMnemonic(KeyEvent.VK_Z);
      undoButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return undoButton;
  }

  private JLabel getLabel_5() {
    if (label_5 == null) {
      label_5 = new JLabel();
      label_5.setText("");
      label_5.setPreferredSize(new Dimension(42, 100));
      label_5.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return label_5;
  }

  private JButton getRedoButton() {
    if (redoButton == null) {
      redoButton = new JButton();
      redoButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.redoAction();
        }
      });
      redoButton.setToolTipText("Redo");
      redoButton.setText("Redo");
      redoButton.setPreferredSize(new Dimension(60, 36));
      redoButton.setMnemonic(KeyEvent.VK_Y);
      redoButton.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return redoButton;
  }

  public JButton getInteractivePauseButton() {
    return interactivePauseButton;
  }

  public JButton getInteractiveResumeButton() {
    return interactiveResumeButton;
  }
} //  @jve:decl-index=0:visual-constraint="10,10"
