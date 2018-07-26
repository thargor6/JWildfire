/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
import java.awt.GridLayout;
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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.GlobalScriptType;
import org.jwildfire.create.tina.animate.SequenceOutputType;
import org.jwildfire.create.tina.animate.XFormScriptType;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.PostSymmetryType;
import org.jwildfire.create.tina.base.Stereo3dColor;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.Stereo3dPreview;
import org.jwildfire.create.tina.base.solidrender.LightDiffFuncPreset;
import org.jwildfire.create.tina.base.solidrender.ReflectionMapping;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.meshgen.filter.PreFilterType;
import org.jwildfire.create.tina.meshgen.render.MeshGenRenderOutputType;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randommovie.RandomMovieGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.render.ChannelMixerMode;
import org.jwildfire.create.tina.render.dof.DOFBlurShapeType;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.render.filter.FilteringType;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelControlStyle;
import org.jwildfire.swing.JWildfire;
import org.jwildfire.swing.StandardErrorHandler;

public class MainEditorFrame extends JFrame {
  private TinaController tinaController;
  private TinaNonlinearControlsRow[] nonlinearControlsRows;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;

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

  private JLabel tinaGammaThresholdLbl;

  private JLabel tinaPixelsPerUnitLbl = null;

  private JWFNumberField tinaPixelsPerUnitREd = null;

  private JSlider tinaPixelsPerUnitSlider = null;

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
  private JPanel previewEastMainPanel = null;
  private JToggleButton mouseTransformMoveTrianglesButton = null;
  private JToggleButton mouseTransformEditFocusPointButton = null;
  private JPanel centerNorthPanel = null;
  private JPanel centerWestPanel = null;
  private JPanel centerCenterPanel = null;
  private JTextArea centerDescLabel = null;
  private JComboBox randomStyleCmb = null;
  private JLabel randomStyleLbl = null;
  private JToggleButton affineEditPostTransformButton = null;
  private JToggleButton affineEditPostTransformSmallButton = null;
  private JToggleButton toggleVariationsButton = null;
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
  private JButton loadFromClipboardFlameButton = null;
  private JButton saveFlameToClipboardButton = null;
  private JToggleButton mouseTransformSlowButton = null;
  private JTabbedPane rootTabbedPane = null;
  private JButton affineFlipHorizontalButton = null;
  private JButton affineFlipVerticalButton = null;
  private JPanel blurShadingPanel = null;
  private JLabel shadingBlurRadiusLbl = null;
  private JWFNumberField postBlurRadiusREd = null;
  private JSlider postBlurRadiusSlider = null;
  private JLabel shadingBlurFadeLbl = null;
  private JWFNumberField postBlurFadeREd = null;
  private JSlider postBlurFadeSlider = null;
  public JWFNumberField postBlurFallOffREd;
  public JSlider postBlurFallOffSlider;
  private JLabel shadingBlurFallOffLbl = null;
  private JPanel scriptPanel = null;
  private JScrollPane scriptScrollPane = null;
  private JTextArea scriptTextArea = null;
  private JToggleButton affineScaleXButton = null;
  private JToggleButton affineScaleYButton = null;
  private JButton randomizeColorsButton = null;
  private JPanel gradientLibraryPanel = null;
  private JPanel gradientLibraryCenterPanel = null;

  public MainEditorFrame() {
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
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Fractal flames");
    this.setVisible(false);
    this.setResizable(true);
    this.setContentPane(getJContentPane());
    getGradientEditorFncPnl().setVisible(false);
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
      jContentPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      jContentPane.add(getRootPanel(), BorderLayout.CENTER);
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
      rootPanel.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaNorthPanel.add(getPanel_7());
      tinaNorthPanel.add(getNewFlameButton());
      tinaNorthPanel.add(getPanel_6());
      tinaNorthPanel.add(getPanel_13());
      tinaNorthPanel.add(getRenderMainButton());
      tinaNorthPanel.add(getPanel_15());
      tinaNorthPanel.add(getFlameToBatchButton());

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
      addPopup(randomBatchPanel, getThumbnailSelectPopupMenu());
      addPopup(randomBatchPanel, getThumbnailRemovePopupMenu());
      randomBatchPanel.setLayout(new BorderLayout());
      randomBatchPanel.setPreferredSize(new Dimension(144, 0));
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
      tinaEastPanel = new JPanel();
      tinaEastPanel.setLayout(new BorderLayout());
      tinaEastPanel.setPreferredSize(new Dimension(328, 0));
      if (Tools.OSType.MAC.equals(Tools.getOSType())) {
        tinaEastPanel.setPreferredSize(new Dimension(tinaEastPanel.getPreferredSize().width + 32, tinaEastPanel.getPreferredSize().height));
      }
      tinaEastPanel.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaSouthPanel.setPreferredSize(new Dimension(0, 212));
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
      tinaCenterPanel.add(getPreviewEastMainPanel(), BorderLayout.EAST);
      tinaCenterPanel.add(getCenterNorthPanel(), BorderLayout.NORTH);
      tinaCenterPanel.add(getCenterWestPanel(), BorderLayout.WEST);
      tinaCenterPanel.add(getCenterCenterPanel(), BorderLayout.CENTER);
      tinaCenterPanel.add(getMacroButtonHorizRootPanel(), BorderLayout.SOUTH);
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
      tinaSouthTabbedPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSouthTabbedPane.addTab("Camera ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/modify_view.png")), getTinaCameraPanel(), null);

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
      dofDOFREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      dofDOFREd.setBounds(102, 28, 100, 24);
      tinaDOFPanel.add(dofDOFREd);

      JLabel lblDepthOfField = new JLabel();
      lblDepthOfField.setName("lblDepthOfField");
      lblDepthOfField.setText("Amount");
      lblDepthOfField.setSize(new Dimension(94, 22));
      lblDepthOfField.setPreferredSize(new Dimension(94, 22));
      lblDepthOfField.setLocation(new Point(4, 98));
      lblDepthOfField.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblDepthOfField.setBounds(6, 28, 94, 22);
      tinaDOFPanel.add(lblDepthOfField);

      dofNewDOFCBx = new JCheckBox("New DOF");
      dofNewDOFCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.newDOFCBx_changed();
          }
        }
      });
      dofNewDOFCBx.setBounds(102, 6, 104, 18);
      dofNewDOFCBx.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaDOFPanel.add(dofNewDOFCBx);

      JLabel lblArea = new JLabel();
      lblArea.setName("lblArea");
      lblArea.setText("Area");
      lblArea.setSize(new Dimension(94, 22));
      lblArea.setPreferredSize(new Dimension(94, 22));
      lblArea.setLocation(new Point(4, 98));
      lblArea.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dofDOFAreaREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      dofDOFAreaREd.setBounds(102, 52, 100, 24);
      tinaDOFPanel.add(dofDOFAreaREd);

      JLabel lblExponent = new JLabel();
      lblExponent.setName("lblExponent");
      lblExponent.setText("Exponent");
      lblExponent.setSize(new Dimension(94, 22));
      lblExponent.setPreferredSize(new Dimension(94, 22));
      lblExponent.setLocation(new Point(4, 98));
      lblExponent.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dofDOFExponentREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      dofDOFExponentREd.setBounds(102, 76, 100, 24);
      tinaDOFPanel.add(dofDOFExponentREd);

      JLabel lblCameraDistance = new JLabel();
      lblCameraDistance.setName("lblCameraDistance");
      lblCameraDistance.setText("Camera distance");
      lblCameraDistance.setSize(new Dimension(94, 22));
      lblCameraDistance.setPreferredSize(new Dimension(94, 22));
      lblCameraDistance.setLocation(new Point(4, 98));
      lblCameraDistance.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dofCamZREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      dofCamZREd.setBounds(543, 4, 100, 24);
      tinaDOFPanel.add(dofCamZREd);

      JLabel lblFocusx = new JLabel();
      lblFocusx.setName("lblFocusx");
      lblFocusx.setText("FocusX");
      lblFocusx.setSize(new Dimension(94, 22));
      lblFocusx.setPreferredSize(new Dimension(94, 22));
      lblFocusx.setLocation(new Point(4, 98));
      lblFocusx.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dofFocusXREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      lblFocusy.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dofFocusYREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      lblFocusz.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dofFocusZREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      dofFocusZREd.setBounds(543, 100, 100, 24);
      tinaDOFPanel.add(dofFocusZREd);

      JLabel lblDimishz = new JLabel();
      lblDimishz.setName("lblDimishz");
      lblDimishz.setText("DiminishZ");
      lblDimishz.setSize(new Dimension(94, 22));
      lblDimishz.setPreferredSize(new Dimension(94, 22));
      lblDimishz.setLocation(new Point(4, 98));
      lblDimishz.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      camDimishZREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaDOFPanel.add(getResetDOFSettingsButton());
      tinaSouthTabbedPane.addTab("DOF / Bokeh ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/games-config-background.png")), getPanel_92(), null);

      tinaSouthTabbedPane.addTab("Coloring ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/color-wheel.png")), getTinaColoringPanel(), null);
      tinaSouthTabbedPane.addTab("Anti-Aliasing / Filter", null, getAntialiasPanel(), null);

      tinaSouthTabbedPane.addTab("Gradient ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-graphics-2.png")), getTinaPalettePanel(), null);
      tinaSouthTabbedPane.addTab("3D rendering", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/kwikdisk-4.png")), getPanel_59(), null);

      tinaSouthTabbedPane.addTab("Stereo3d ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/layer-novisible.png")), getPanel_82(), null);
      tinaSouthTabbedPane.addTab("Post symmetry", null, getPanel_34(), null);
      tinaSouthTabbedPane.addTab("FPS / Motion blur", null, getMotionBlurPanel(), null);
      tinaSouthTabbedPane.addTab("Layers ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/emblem-photos.png")), getPanel_74(), null);
      tinaSouthTabbedPane.addTab("Channel mixer ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/color-fill.png")), getChannelMixerPanel(), null);

      JPanel panel_1 = new JPanel();
      tinaSouthTabbedPane.addTab("Leap Motion", null, panel_1, null);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(114, 10));
      panel_1.add(panel_2, BorderLayout.WEST);
      panel_2.setLayout(null);

      leapMotionResetConfigButton = new JButton();
      leapMotionResetConfigButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getLeapMotionMainEditorController().resetConfigButton_clicked();
        }
      });
      leapMotionResetConfigButton.setToolTipText("Reset the post-symmetry-options to the defaults");
      leapMotionResetConfigButton.setText("Reset");
      leapMotionResetConfigButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
      leapMotionResetConfigButton.setPreferredSize(new Dimension(125, 24));
      leapMotionResetConfigButton.setMinimumSize(new Dimension(100, 24));
      leapMotionResetConfigButton.setMaximumSize(new Dimension(32000, 24));
      leapMotionResetConfigButton.setIconTextGap(2);
      leapMotionResetConfigButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      leapMotionResetConfigButton.setBounds(6, 114, 100, 24);
      panel_2.add(leapMotionResetConfigButton);
      panel_2.add(getLeapMotionToggleButton());

      JPanel panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(340, 10));
      panel_1.add(panel_3, BorderLayout.EAST);
      panel_3.setLayout(null);

      leapMotionAddButton = new JButton();
      leapMotionAddButton.setToolTipText("Add new motion listener");
      leapMotionAddButton.setText("Add");
      leapMotionAddButton.setPreferredSize(new Dimension(56, 24));
      leapMotionAddButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      leapMotionAddButton.setBounds(6, 6, 90, 24);
      leapMotionAddButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getLeapMotionMainEditorController().leapMotionAddButton_clicked();
        }
      });
      panel_3.add(leapMotionAddButton);

      leapMotionDeleteButton = new JButton();
      leapMotionDeleteButton.setToolTipText("Delete motion listener");
      leapMotionDeleteButton.setText("Delete");
      leapMotionDeleteButton.setPreferredSize(new Dimension(90, 24));
      leapMotionDeleteButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      leapMotionDeleteButton.setBounds(6, 80, 90, 24);
      leapMotionDeleteButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getLeapMotionMainEditorController().leapMotionDeleteButton_clicked();
        }
      });
      panel_3.add(leapMotionDeleteButton);

      leapMotionDuplicateButton = new JButton();
      leapMotionDuplicateButton.setToolTipText("Duplicate motion listener");
      leapMotionDuplicateButton.setText("Duplicate");
      leapMotionDuplicateButton.setPreferredSize(new Dimension(90, 24));
      leapMotionDuplicateButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      leapMotionDuplicateButton.setBounds(6, 32, 90, 24);
      leapMotionDuplicateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getLeapMotionMainEditorController().leapMotionDuplicateButton_clicked();
        }
      });
      panel_3.add(leapMotionDuplicateButton);

      JTextArea txtrPleaseNoteThat = new JTextArea();
      txtrPleaseNoteThat.setEditable(false);
      txtrPleaseNoteThat.setLineWrap(true);
      txtrPleaseNoteThat.setWrapStyleWord(true);
      txtrPleaseNoteThat.setText("Please note that all the settings here do NOT belong to a certain flame and are NOT saved when you save a flame. Currently, they can NOT be saved at all.");
      txtrPleaseNoteThat.setBounds(108, 3, 226, 122);
      panel_3.add(txtrPleaseNoteThat);

      leapMotionClearButton = new JButton();
      leapMotionClearButton.setToolTipText("Clear all motion listeners");
      leapMotionClearButton.setText("Clear");
      leapMotionClearButton.setPreferredSize(new Dimension(90, 24));
      leapMotionClearButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      leapMotionClearButton.setBounds(6, 106, 90, 24);
      leapMotionClearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getLeapMotionMainEditorController().leapMotionClearButton_clicked();
        }
      });
      panel_3.add(leapMotionClearButton);
      panel_1.add(getPanel_111(), BorderLayout.CENTER);

      tinaSouthTabbedPane.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            switch (tinaSouthTabbedPane.getSelectedIndex()) {
              case 4:
                if (tinaController.getGradientController() != null) {
                  tinaController.getGradientController().onActivate();
                }
                break;
              default: // nothing to do
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
      tinaPixelsPerUnitLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPixelsPerUnitLbl.setLocation(new Point(390, 78));
      tinaPixelsPerUnitLbl.setSize(new Dimension(68, 22));
      tinaPixelsPerUnitLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraZoomLbl = new JLabel();
      tinaCameraZoomLbl.setName("tinaCameraZoomLbl");
      tinaCameraZoomLbl.setText("Zoom");
      tinaCameraZoomLbl.setLocation(new Point(390, 52));
      tinaCameraZoomLbl.setSize(new Dimension(68, 22));
      tinaCameraZoomLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaCameraZoomLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCentreYLbl = new JLabel();
      tinaCameraCentreYLbl.setName("tinaCameraCentreYLbl");
      tinaCameraCentreYLbl.setText("CentreY");
      tinaCameraCentreYLbl.setLocation(new Point(390, 28));
      tinaCameraCentreYLbl.setSize(new Dimension(68, 22));
      tinaCameraCentreYLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaCameraCentreYLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraCentreXLbl = new JLabel();
      tinaCameraCentreXLbl.setName("tinaCameraCentreXLbl");
      tinaCameraCentreXLbl.setText("CentreX");
      tinaCameraCentreXLbl.setLocation(new Point(390, 6));
      tinaCameraCentreXLbl.setSize(new Dimension(68, 22));
      tinaCameraCentreXLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaCameraCentreXLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraPerspectiveLbl = new JLabel();
      tinaCameraPerspectiveLbl.setName("tinaCameraPerspectiveLbl");
      tinaCameraPerspectiveLbl.setText("Perspective");
      tinaCameraPerspectiveLbl.setLocation(new Point(4, 76));
      tinaCameraPerspectiveLbl.setSize(new Dimension(68, 22));
      tinaCameraPerspectiveLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaCameraPerspectiveLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraYawLbl = new JLabel();
      tinaCameraYawLbl.setName("tinaCameraYawLbl");
      tinaCameraYawLbl.setText("Yaw");
      tinaCameraYawLbl.setLocation(new Point(4, 52));
      tinaCameraYawLbl.setSize(new Dimension(68, 22));
      tinaCameraYawLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaCameraYawLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraPitchLbl = new JLabel();
      tinaCameraPitchLbl.setName("tinaCameraPitchLbl");
      tinaCameraPitchLbl.setText("Pitch");
      tinaCameraPitchLbl.setLocation(new Point(4, 28));
      tinaCameraPitchLbl.setSize(new Dimension(68, 22));
      tinaCameraPitchLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaCameraPitchLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraRollLbl = new JLabel();
      tinaCameraRollLbl.setName("tinaCameraRollLbl");
      tinaCameraRollLbl.setText("Roll");
      tinaCameraRollLbl.setLocation(new Point(4, 4));
      tinaCameraRollLbl.setSize(new Dimension(68, 22));
      tinaCameraRollLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaCameraCamPosXREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaCameraCamPosXLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaCameraCamPosYLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaCameraCamPosYREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaCameraCamPosZLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaCameraCamPosZREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaCameraPanel.add(getResetCameraSettingsBtn());
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
      tinaGammaThresholdLbl.setText("Gamma threshold*");
      tinaGammaThresholdLbl.setLocation(new Point(4, 73));
      tinaGammaThresholdLbl.setSize(new Dimension(94, 22));
      tinaGammaThresholdLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaGammaThresholdLbl.setPreferredSize(new Dimension(94, 22));
      tinaVibrancyLbl = new JLabel();
      tinaVibrancyLbl.setName("tinaVibrancyLbl");
      tinaVibrancyLbl.setText("Vibrancy*");
      tinaVibrancyLbl.setLocation(new Point(409, 96));
      tinaVibrancyLbl.setSize(new Dimension(94, 22));
      tinaVibrancyLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaVibrancyLbl.setPreferredSize(new Dimension(94, 22));
      tinaGammaLbl = new JLabel();
      tinaGammaLbl.setName("tinaGammaLbl");
      tinaGammaLbl.setText("Gamma*");
      tinaGammaLbl.setLocation(new Point(4, 50));
      tinaGammaLbl.setSize(new Dimension(94, 22));
      tinaGammaLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaGammaLbl.setPreferredSize(new Dimension(94, 22));
      tinaContrastLbl = new JLabel();
      tinaContrastLbl.setName("tinaContrastLbl");
      tinaContrastLbl.setText("Contrast*");
      tinaContrastLbl.setLocation(new Point(4, 96));
      tinaContrastLbl.setSize(new Dimension(94, 22));
      tinaContrastLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaContrastLbl.setPreferredSize(new Dimension(94, 22));
      tinaBrightnessLbl = new JLabel();
      tinaBrightnessLbl.setName("tinaBrightnessLbl");
      tinaBrightnessLbl.setText("Brightness*");
      tinaBrightnessLbl.setLocation(new Point(4, 4));
      tinaBrightnessLbl.setSize(new Dimension(94, 22));
      tinaBrightnessLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bgTransparencyCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().flameTransparencyCbx_changed();
          }
        }
      });
      bgTransparencyCBx.setBounds(873, 118, 169, 18);
      bgTransparencyCBx.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaColoringPanel.add(bgTransparencyCBx);
      tinaColoringPanel.add(getBackgroundColorIndicatorBtn());

      JLabel lblBackgroundColor = new JLabel();
      lblBackgroundColor.setToolTipText("Background color");
      lblBackgroundColor.setText("Bg color*");
      lblBackgroundColor.setSize(new Dimension(94, 22));
      lblBackgroundColor.setPreferredSize(new Dimension(94, 22));
      lblBackgroundColor.setLocation(new Point(4, 4));
      lblBackgroundColor.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBackgroundColor.setBounds(806, 4, 65, 22);
      tinaColoringPanel.add(lblBackgroundColor);

      JLabel tinaSaturationLbl = new JLabel();
      tinaSaturationLbl.setText("Saturation*");
      tinaSaturationLbl.setSize(new Dimension(94, 22));
      tinaSaturationLbl.setPreferredSize(new Dimension(94, 22));
      tinaSaturationLbl.setName("tinaSaturationLbl");
      tinaSaturationLbl.setLocation(new Point(4, 100));
      tinaSaturationLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSaturationLbl.setBounds(409, 118, 94, 22);
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
      tinaSaturationREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSaturationREd.setBounds(505, 118, 100, 24);
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
      tinaSaturationSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSaturationSlider.setBounds(607, 118, 195, 19);
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
      tinaColoringPanel.add(getResetColoringOptionsButton());

      JLabel lblFadeToWhite = new JLabel();
      lblFadeToWhite.setToolTipText("Color Level at which colors are faded to white");
      lblFadeToWhite.setText("Fade to White*");
      lblFadeToWhite.setSize(new Dimension(94, 22));
      lblFadeToWhite.setPreferredSize(new Dimension(94, 22));
      lblFadeToWhite.setName("tinaWhiteLevelLbl");
      lblFadeToWhite.setLocation(new Point(4, 100));
      lblFadeToWhite.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblFadeToWhite.setBounds(409, 73, 94, 22);
      tinaColoringPanel.add(lblFadeToWhite);

      tinaWhiteLevelREd = new JWFNumberField();
      tinaWhiteLevelREd.setValueStep(3.0);
      tinaWhiteLevelREd.setText("");
      tinaWhiteLevelREd.setSize(new Dimension(100, 24));
      tinaWhiteLevelREd.setPreferredSize(new Dimension(100, 24));
      tinaWhiteLevelREd.setMotionPropertyName("saturation");
      tinaWhiteLevelREd.setMinValue(20);
      tinaWhiteLevelREd.setMaxValue(500.0);
      tinaWhiteLevelREd.setLocation(new Point(100, 100));
      tinaWhiteLevelREd.setLinkedMotionControlName("tinaWhiteLevelSlider");
      tinaWhiteLevelREd.setLinkedLabelControlName("tinaWhiteLevelLbl");
      tinaWhiteLevelREd.setHasMinValue(true);
      tinaWhiteLevelREd.setHasMaxValue(true);
      tinaWhiteLevelREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaWhiteLevelREd.setBounds(505, 73, 100, 24);
      tinaWhiteLevelREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaWhiteLevelREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaWhiteLevelREd.isMouseAdjusting() || tinaWhiteLevelREd.getMouseChangeCount() == 0) {
              if (!tinaWhiteLevelSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().whiteLevelREd_changed();
          }
        }
      });

      tinaColoringPanel.add(tinaWhiteLevelREd);

      tinaWhiteLevelSlider = new JSlider();
      tinaWhiteLevelSlider.setValue(0);
      tinaWhiteLevelSlider.setSize(new Dimension(220, 19));
      tinaWhiteLevelSlider.setPreferredSize(new Dimension(220, 19));
      tinaWhiteLevelSlider.setName("tinaWhiteLevelSlider");
      tinaWhiteLevelSlider.setMinimum(20);
      tinaWhiteLevelSlider.setMaximum(500);
      tinaWhiteLevelSlider.setLocation(new Point(202, 100));
      tinaWhiteLevelSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaWhiteLevelSlider.setBounds(607, 73, 195, 19);
      tinaWhiteLevelSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaWhiteLevelSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().whiteLevelSlider_stateChanged(e);
        }
      });
      tinaColoringPanel.add(tinaWhiteLevelSlider);

      backgroundSelectImageBtn = new JButton();
      backgroundSelectImageBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.selectImageForBackgroundButton_actionPerformed(e);
        }
      });
      backgroundSelectImageBtn.setToolTipText("Select an image to use as a background");
      backgroundSelectImageBtn.setText("Select image...");
      backgroundSelectImageBtn.setPreferredSize(new Dimension(190, 24));
      backgroundSelectImageBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      backgroundSelectImageBtn.setBounds(873, 70, 148, 24);
      tinaColoringPanel.add(backgroundSelectImageBtn);

      backgroundRemoveImageBtn = new JButton();
      backgroundRemoveImageBtn.setToolTipText("Remove the currently used image");
      backgroundRemoveImageBtn.setText("Remove image");
      backgroundRemoveImageBtn.setPreferredSize(new Dimension(190, 24));
      backgroundRemoveImageBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      backgroundRemoveImageBtn.setBounds(1022, 70, 148, 24);
      backgroundRemoveImageBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.removeBackgroundImageButton_actionPerformed(e);
        }
      });
      tinaColoringPanel.add(backgroundRemoveImageBtn);

      foregroundOpacityField = new JWFNumberField();
      foregroundOpacityField.setValueStep(0.05);
      foregroundOpacityField.setText("");
      foregroundOpacityField.setSize(new Dimension(100, 24));
      foregroundOpacityField.setPreferredSize(new Dimension(100, 24));
      foregroundOpacityField.setMaxValue(2.0);
      foregroundOpacityField.setLocation(new Point(584, 2));
      foregroundOpacityField.setLinkedMotionControlName("foregroundOpacitySlider");
      foregroundOpacityField.setHasMinValue(true);
      foregroundOpacityField.setHasMaxValue(true);
      foregroundOpacityField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      foregroundOpacityField.setEditable(true);
      foregroundOpacityField.setBounds(873, 94, 100, 24);
      foregroundOpacityField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!foregroundOpacityField.isMouseAdjusting() || foregroundOpacityField.getMouseChangeCount() == 0) {
              if (!foregroundOpacitySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().foregroundOpacityREd_changed();
          }
        }
      });

      tinaColoringPanel.add(foregroundOpacityField);

      JLabel lblOpacity = new JLabel();
      lblOpacity.setToolTipText("Foreground opacity");
      lblOpacity.setText("Fg opacity*");
      lblOpacity.setSize(new Dimension(94, 22));
      lblOpacity.setPreferredSize(new Dimension(94, 22));
      lblOpacity.setLocation(new Point(488, 2));
      lblOpacity.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblOpacity.setBounds(806, 94, 65, 22);
      tinaColoringPanel.add(lblOpacity);

      foregroundOpacitySlider = new JSlider();
      foregroundOpacitySlider.setValue(0);
      foregroundOpacitySlider.setSize(new Dimension(220, 19));
      foregroundOpacitySlider.setPreferredSize(new Dimension(220, 19));
      foregroundOpacitySlider.setName("foregroundOpacitySlider");
      foregroundOpacitySlider.setMaximum(2000);
      foregroundOpacitySlider.setLocation(new Point(686, 2));
      foregroundOpacitySlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      foregroundOpacitySlider.setBounds(975, 94, 195, 24);
      foregroundOpacitySlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().foregroundOpacitySlider_stateChanged(e);
          }
        }
      });
      foregroundOpacitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });

      tinaColoringPanel.add(foregroundOpacitySlider);

      lowDensityBrightnessREd = new JWFNumberField();
      lowDensityBrightnessREd.setValueStep(0.01);
      lowDensityBrightnessREd.setText("");
      lowDensityBrightnessREd.setSize(new Dimension(100, 24));
      lowDensityBrightnessREd.setPreferredSize(new Dimension(100, 24));
      lowDensityBrightnessREd.setHasMinValue(true);
      lowDensityBrightnessREd.setMinValue(-20.0);
      lowDensityBrightnessREd.setMaxValue(20.0);
      lowDensityBrightnessREd.setLocation(new Point(100, 100));
      lowDensityBrightnessREd.setLinkedMotionControlName("lowDensityBrightnessSlider");
      lowDensityBrightnessREd.setLinkedLabelControlName("lowDensityBrightnessLbl");
      lowDensityBrightnessREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      lowDensityBrightnessREd.setBounds(100, 27, 100, 24);
      lowDensityBrightnessREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      lowDensityBrightnessREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!lowDensityBrightnessREd.isMouseAdjusting() || lowDensityBrightnessREd.getMouseChangeCount() == 0) {
              if (!lowDensityBrightnessSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().lowDensityBrightnessREd_changed();
          }
        }
      });

      tinaColoringPanel.add(lowDensityBrightnessREd);

      lowDensityBrightnessSlider = new JSlider();
      lowDensityBrightnessSlider.setValue(0);
      lowDensityBrightnessSlider.setSize(new Dimension(220, 19));
      lowDensityBrightnessSlider.setPreferredSize(new Dimension(220, 19));
      lowDensityBrightnessSlider.setName("lowDensityBrightnessSlider");
      lowDensityBrightnessSlider.setMinimum(-1000);
      lowDensityBrightnessSlider.setMaximum(1000);
      lowDensityBrightnessSlider.setLocation(new Point(202, 100));
      lowDensityBrightnessSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lowDensityBrightnessSlider.setBounds(202, 27, 195, 19);
      lowDensityBrightnessSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      lowDensityBrightnessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().lowDensityBrightnessSlider_stateChanged(e);
        }
      });

      tinaColoringPanel.add(lowDensityBrightnessSlider);

      JLabel lblBgBrightness = new JLabel();
      lblBgBrightness.setToolTipText("Increase the brightness of areas with low density");
      lblBgBrightness.setText("Low brightness*");
      lblBgBrightness.setSize(new Dimension(94, 22));
      lblBgBrightness.setPreferredSize(new Dimension(94, 22));
      lblBgBrightness.setName("lowDensityBrightnessLbl");
      lblBgBrightness.setLocation(new Point(4, 100));
      lblBgBrightness.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBgBrightness.setBounds(4, 27, 94, 22);
      tinaColoringPanel.add(lblBgBrightness);

      balanceRedREd = new JWFNumberField();
      balanceRedREd.setMouseSpeed(0.1);
      balanceRedREd.setForeground(Color.BLACK);
      balanceRedREd.setValueStep(0.01);
      balanceRedREd.setText("");
      balanceRedREd.setSize(new Dimension(100, 24));
      balanceRedREd.setPreferredSize(new Dimension(100, 24));
      balanceRedREd.setMinValue(0.0);
      balanceRedREd.setMaxValue(2.0);
      balanceRedREd.setLocation(new Point(100, 100));
      balanceRedREd.setLinkedMotionControlName("balanceRedSlider");
      balanceRedREd.setLinkedLabelControlName("balanceRedLbl");
      balanceRedREd.setHasMinValue(true);
      balanceRedREd.setHasMaxValue(true);
      balanceRedREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      balanceRedREd.setBounds(505, 4, 100, 24);
      balanceRedREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      balanceRedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!balanceRedREd.isMouseAdjusting() || balanceRedREd.getMouseChangeCount() == 0) {
              if (!balanceRedSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().balanceRedREd_changed();
          }
        }
      });

      tinaColoringPanel.add(balanceRedREd);

      JLabel lblRedBalance = new JLabel();
      lblRedBalance.setForeground(new Color(165, 42, 42));
      lblRedBalance.setToolTipText("");
      lblRedBalance.setText("Red balance*");
      lblRedBalance.setSize(new Dimension(94, 22));
      lblRedBalance.setPreferredSize(new Dimension(94, 22));
      lblRedBalance.setName("balanceRedLbl");
      lblRedBalance.setLocation(new Point(4, 100));
      lblRedBalance.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblRedBalance.setBounds(409, 4, 94, 22);
      tinaColoringPanel.add(lblRedBalance);

      balanceRedSlider = new JSlider();
      balanceRedSlider.setValue(0);
      balanceRedSlider.setSize(new Dimension(220, 19));
      balanceRedSlider.setPreferredSize(new Dimension(220, 19));
      balanceRedSlider.setName("balanceRedSlider");
      balanceRedSlider.setMaximum(300);
      balanceRedSlider.setLocation(new Point(202, 100));
      balanceRedSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      balanceRedSlider.setBounds(607, 4, 195, 19);
      balanceRedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      balanceRedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().balanceRedSlider_stateChanged(e);
        }
      });

      tinaColoringPanel.add(balanceRedSlider);

      balanceGreenREd = new JWFNumberField();
      balanceGreenREd.setMouseSpeed(0.1);
      balanceGreenREd.setValueStep(0.01);
      balanceGreenREd.setText("");
      balanceGreenREd.setSize(new Dimension(100, 24));
      balanceGreenREd.setPreferredSize(new Dimension(100, 24));
      balanceGreenREd.setMinValue(0.0);
      balanceGreenREd.setMaxValue(2.0);
      balanceGreenREd.setLocation(new Point(100, 100));
      balanceGreenREd.setLinkedMotionControlName("balanceGreenSlider");
      balanceGreenREd.setLinkedLabelControlName("balanceGreenLbl");
      balanceGreenREd.setHasMinValue(true);
      balanceGreenREd.setHasMaxValue(true);
      balanceGreenREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      balanceGreenREd.setBounds(505, 27, 100, 24);
      balanceGreenREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      balanceGreenREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!balanceGreenREd.isMouseAdjusting() || balanceGreenREd.getMouseChangeCount() == 0) {
              if (!balanceGreenSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().balanceGreenREd_changed();
          }
        }
      });
      tinaColoringPanel.add(balanceGreenREd);

      JLabel lblGreenBalance = new JLabel();
      lblGreenBalance.setForeground(new Color(34, 139, 34));
      lblGreenBalance.setToolTipText("");
      lblGreenBalance.setText("Green balance*");
      lblGreenBalance.setSize(new Dimension(94, 22));
      lblGreenBalance.setPreferredSize(new Dimension(94, 22));
      lblGreenBalance.setName("balanceGreenLbl");
      lblGreenBalance.setLocation(new Point(4, 100));
      lblGreenBalance.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGreenBalance.setBounds(409, 27, 94, 22);
      tinaColoringPanel.add(lblGreenBalance);

      balanceGreenSlider = new JSlider();
      balanceGreenSlider.setValue(0);
      balanceGreenSlider.setSize(new Dimension(220, 19));
      balanceGreenSlider.setPreferredSize(new Dimension(220, 19));
      balanceGreenSlider.setName("balanceGreenSlider");
      balanceGreenSlider.setMaximum(300);
      balanceGreenSlider.setLocation(new Point(202, 100));
      balanceGreenSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      balanceGreenSlider.setBounds(607, 27, 195, 19);
      balanceGreenSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      balanceGreenSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().balanceGreenSlider_stateChanged(e);
        }
      });

      tinaColoringPanel.add(balanceGreenSlider);

      balanceBlueREd = new JWFNumberField();
      balanceBlueREd.setMouseSpeed(0.1);
      balanceBlueREd.setValueStep(0.01);
      balanceBlueREd.setText("");
      balanceBlueREd.setSize(new Dimension(100, 24));
      balanceBlueREd.setPreferredSize(new Dimension(100, 24));
      balanceBlueREd.setMinValue(0.0);
      balanceBlueREd.setMaxValue(2.0);
      balanceBlueREd.setLocation(new Point(100, 100));
      balanceBlueREd.setLinkedMotionControlName("balanceBlueSlider");
      balanceBlueREd.setLinkedLabelControlName("balanceBlueLbl");
      balanceBlueREd.setHasMinValue(true);
      balanceBlueREd.setHasMaxValue(true);
      balanceBlueREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      balanceBlueREd.setBounds(505, 50, 100, 24);
      balanceBlueREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      balanceBlueREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!balanceBlueREd.isMouseAdjusting() || balanceBlueREd.getMouseChangeCount() == 0) {
              if (!balanceBlueSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().balanceBlueREd_changed();
          }
        }
      });

      tinaColoringPanel.add(balanceBlueREd);

      JLabel lblRedBalancing = new JLabel();
      lblRedBalancing.setForeground(new Color(0, 0, 205));
      lblRedBalancing.setToolTipText("");
      lblRedBalancing.setText("Blue balance*");
      lblRedBalancing.setSize(new Dimension(94, 22));
      lblRedBalancing.setPreferredSize(new Dimension(94, 22));
      lblRedBalancing.setName("balanceBlueLbl");
      lblRedBalancing.setLocation(new Point(4, 100));
      lblRedBalancing.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblRedBalancing.setBounds(409, 50, 94, 22);
      tinaColoringPanel.add(lblRedBalancing);

      balanceBlueSlider = new JSlider();
      balanceBlueSlider.setValue(0);
      balanceBlueSlider.setSize(new Dimension(220, 19));
      balanceBlueSlider.setPreferredSize(new Dimension(220, 19));
      balanceBlueSlider.setName("balanceBlueSlider");
      balanceBlueSlider.setMaximum(300);
      balanceBlueSlider.setLocation(new Point(202, 100));
      balanceBlueSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      balanceBlueSlider.setBounds(607, 50, 195, 19);
      balanceBlueSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      balanceBlueSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().balanceBlueSlider_stateChanged(e);
        }
      });

      tinaColoringPanel.add(balanceBlueSlider);

      JLabel lblBgImage = new JLabel();
      lblBgImage.setToolTipText("Background image");
      lblBgImage.setText("Bg image*");
      lblBgImage.setSize(new Dimension(94, 22));
      lblBgImage.setPreferredSize(new Dimension(94, 22));
      lblBgImage.setLocation(new Point(4, 4));
      lblBgImage.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBgImage.setBounds(806, 73, 65, 22);
      tinaColoringPanel.add(lblBgImage);

      backgroundColorTypeCmb = new JComboBox();
      backgroundColorTypeCmb.setSize(new Dimension(125, 22));
      backgroundColorTypeCmb.setPreferredSize(new Dimension(125, 22));
      backgroundColorTypeCmb.setLocation(new Point(100, 4));
      backgroundColorTypeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      backgroundColorTypeCmb.setBounds(873, 3, 150, 24);
      backgroundColorTypeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.backgroundColorTypeCmb_changed();
          }
        }
      });

      tinaColoringPanel.add(backgroundColorTypeCmb);

      backgroundColorLLIndicatorBtn = new JButton();
      backgroundColorLLIndicatorBtn.setToolTipText("Set the background color of your fractal");
      backgroundColorLLIndicatorBtn.setPreferredSize(new Dimension(190, 24));
      backgroundColorLLIndicatorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      backgroundColorLLIndicatorBtn.setBackground(Color.BLACK);
      backgroundColorLLIndicatorBtn.setBounds(1027, 28, 30, 24);
      backgroundColorLLIndicatorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.backgroundColorLLBtn_clicked();
        }
      });
      tinaColoringPanel.add(backgroundColorLLIndicatorBtn);

      backgroundColorURIndicatorBtn = new JButton();
      backgroundColorURIndicatorBtn.setToolTipText("Set the background color of your fractal");
      backgroundColorURIndicatorBtn.setPreferredSize(new Dimension(190, 24));
      backgroundColorURIndicatorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      backgroundColorURIndicatorBtn.setBackground(Color.BLACK);
      backgroundColorURIndicatorBtn.setBounds(1086, 4, 30, 24);
      backgroundColorURIndicatorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.backgroundColorURBtn_clicked();
        }
      });
      tinaColoringPanel.add(backgroundColorURIndicatorBtn);

      backgroundColorLRIndicatorBtn = new JButton();
      backgroundColorLRIndicatorBtn.setToolTipText("Set the background color of your fractal");
      backgroundColorLRIndicatorBtn.setPreferredSize(new Dimension(190, 24));
      backgroundColorLRIndicatorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      backgroundColorLRIndicatorBtn.setBackground(Color.BLACK);
      backgroundColorLRIndicatorBtn.setBounds(1086, 28, 30, 24);
      backgroundColorLRIndicatorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.backgroundColorLRBtn_clicked();
        }
      });
      tinaColoringPanel.add(backgroundColorLRIndicatorBtn);

      backgroundColorCCIndicatorBtn = new JButton();
      backgroundColorCCIndicatorBtn.setToolTipText("Set the background color of your fractal");
      backgroundColorCCIndicatorBtn.setPreferredSize(new Dimension(190, 24));
      backgroundColorCCIndicatorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      backgroundColorCCIndicatorBtn.setBackground(Color.BLACK);
      backgroundColorCCIndicatorBtn.setBounds(1057, 12, 30, 32);
      backgroundColorCCIndicatorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.backgroundColorCCBtn_clicked();
        }
      });
      tinaColoringPanel.add(backgroundColorCCIndicatorBtn);
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
      tinaCameraRollREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaCameraPitchREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaCameraYawREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaCameraPerspectiveREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      //tinaLoadFlameButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/document-open-5.png")));
      tinaLoadFlameButton.setMinimumSize(new Dimension(100, 24));
      tinaLoadFlameButton.setMaximumSize(new Dimension(32000, 24));
      tinaLoadFlameButton.setText("Load Flame...");
      tinaLoadFlameButton.setPreferredSize(new Dimension(125, 24));
      tinaLoadFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      // tinaSaveFlameButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/document-export-3.png")));
      tinaSaveFlameButton.setMinimumSize(new Dimension(100, 24));
      tinaSaveFlameButton.setMaximumSize(new Dimension(32000, 24));
      tinaSaveFlameButton.setText("Save...");
      tinaSaveFlameButton.setPreferredSize(new Dimension(125, 24));
      tinaSaveFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaRenderFlameButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive3.png")));
      tinaRenderFlameButton.setMnemonic(KeyEvent.VK_R);
      tinaRenderFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
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
      renderMainButton.setToolTipText("Render the fractal at the size chosen to the right and save the result");
      renderMainButton.setMinimumSize(new Dimension(125, 52));
      renderMainButton.setMaximumSize(new Dimension(32000, 52));
      renderMainButton.setText("Render Fractal");
      renderMainButton.setPreferredSize(new Dimension(115, 24));
      renderMainButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      renderMainButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive3.png")));
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
      tinaCameraCentreXREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaCameraCentreYREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaCameraZoomREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaBrightnessREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaBrightnessSlider.setSize(new Dimension(195, 19));
      tinaBrightnessSlider.setPreferredSize(new Dimension(220, 19));
      tinaBrightnessSlider.setValue(0);
      tinaBrightnessSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPixelsPerUnitREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaContrastREd.setLocation(new Point(100, 96));
      tinaContrastREd.setSize(new Dimension(100, 24));
      tinaContrastREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaGammaREd.setLocation(new Point(100, 50));
      tinaGammaREd.setSize(new Dimension(100, 24));
      tinaGammaREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaVibrancyREd.setLocation(new Point(505, 96));
      tinaVibrancyREd.setSize(new Dimension(100, 24));
      tinaVibrancyREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaGammaThresholdREd.setLocation(new Point(100, 73));
      tinaGammaThresholdREd.setSize(new Dimension(100, 24));
      tinaGammaThresholdREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaContrastSlider.setMinimum(0);
      tinaContrastSlider.setValue(0);
      tinaContrastSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaContrastSlider.setSize(new Dimension(195, 19));
      tinaContrastSlider.setLocation(new Point(202, 96));
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
      tinaGammaSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaGammaSlider.setSize(new Dimension(195, 19));
      tinaGammaSlider.setLocation(new Point(202, 50));
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
      tinaVibrancySlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaVibrancySlider.setSize(new Dimension(195, 19));
      tinaVibrancySlider.setLocation(new Point(607, 96));
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
      tinaGammaThresholdSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaGammaThresholdSlider.setSize(new Dimension(195, 19));
      tinaGammaThresholdSlider.setLocation(new Point(202, 73));
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
      tinaAddTransformationButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
              default: // nothing to do
                break;
            }
          }
        }
      });
      tinaEastTabbedPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaEastTabbedPane.addTab("Transformations", null, getTinaTransformationsPanel(), null);
      tinaEastTabbedPane.addTab("Scripts", null, getScriptPanel(), null);

      JPanel panel_1 = new JPanel();
      tinaEastTabbedPane.addTab("Misc", null, panel_1, null);
      panel_1.setLayout(null);

      randomizeBtn = new JButton();
      randomizeBtn.setBounds(6, 6, 213, 24);
      panel_1.add(randomizeBtn);
      randomizeBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.randomizeBtn_clicked();
        }
      });
      randomizeBtn.setToolTipText("Randomize random parameters of the current flame");
      randomizeBtn.setText("Randomize all");
      randomizeBtn.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      randomizeBtn.setSelected(false);
      randomizeBtn.setPreferredSize(new Dimension(42, 24));
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
      tinaTransformationsPanel.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaDeleteTransformationButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaDuplicateTransformationButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaTransformationsScrollPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaTransformationsTable.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaTransformationsTabbedPane.setBorder(null);
      tinaTransformationsTabbedPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaTransformationsTabbedPane.addTab("Affine", null, getTinaAffineTransformationPanel(), null);
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
      xFormModGammaSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModGammaSlider.setBounds(125, 2, 156, 22);
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
      xFormModGammaREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormModGammaREd.setBounds(68, 2, 55, 22);
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
      label.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label.setBounds(4, 2, 64, 22);
      panel_1.add(label);

      JLabel label_2 = new JLabel();
      label_2.setToolTipText("Blending of local gamma change");
      label_2.setText("Gamma Spd");
      label_2.setSize(new Dimension(64, 22));
      label_2.setPreferredSize(new Dimension(64, 22));
      label_2.setLocation(new Point(6, 47));
      label_2.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label_2.setBounds(4, 22, 64, 22);
      panel_1.add(label_2);

      xFormModGammaSpeedSlider = new JSlider();
      xFormModGammaSpeedSlider.setValue(0);
      xFormModGammaSpeedSlider.setSize(new Dimension(172, 22));
      xFormModGammaSpeedSlider.setPreferredSize(new Dimension(172, 22));
      xFormModGammaSpeedSlider.setMinimum(-100);
      xFormModGammaSpeedSlider.setMaximum(100);
      xFormModGammaSpeedSlider.setLocation(new Point(125, 47));
      xFormModGammaSpeedSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModGammaSpeedSlider.setBounds(125, 22, 156, 22);
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
      xFormModGammaSpeedREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormModGammaSpeedREd.setBounds(68, 22, 55, 22);
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
      lblContrast.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblContrast.setBounds(4, 46, 64, 22);
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
      xFormModContrastREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormModContrastREd.setBounds(68, 46, 55, 22);
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
      xFormModContrastSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModContrastSlider.setBounds(125, 46, 156, 22);
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
      lblContrstSpd.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblContrstSpd.setBounds(4, 66, 64, 22);
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
      xFormModContrastSpeedREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormModContrastSpeedREd.setBounds(68, 66, 55, 22);
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
      xFormModContrastSpeedSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModContrastSpeedSlider.setBounds(125, 66, 156, 22);
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
      lblSaturation.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSaturation.setBounds(4, 90, 64, 22);
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
      xFormModSaturationREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormModSaturationREd.setBounds(68, 90, 55, 22);
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
      xFormModSaturationSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModSaturationSlider.setBounds(125, 90, 156, 22);
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
      lblSaturatSpd.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSaturatSpd.setBounds(4, 110, 64, 22);
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
      xFormModSaturationSpeedREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormModSaturationSpeedREd.setBounds(68, 110, 55, 22);
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
      xFormModSaturationSpeedSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModSaturationSpeedSlider.setBounds(125, 110, 156, 22);
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
          tinaController.xFormModLocalGammaRandomizeAllBtn_Clicked(getXFormModGammaWholeFractalCBx().isSelected());
        }
      });
      xFormModGammaRandomizeBtn.setToolTipText("Randomize all local color-changing effects, either of the whole fractal or the selected transform");
      xFormModGammaRandomizeBtn.setText("Randomize all");
      xFormModGammaRandomizeBtn.setPreferredSize(new Dimension(104, 24));
      xFormModGammaRandomizeBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModGammaRandomizeBtn.setBounds(4, 176, 104, 24);
      panel_1.add(xFormModGammaRandomizeBtn);

      xFormModGammaResetBtn = new JButton();
      xFormModGammaResetBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.xFormModLocalGammaResetAllBtn_Clicked(getXFormModGammaWholeFractalCBx().isSelected());
        }
      });
      xFormModGammaResetBtn.setToolTipText("Reset local color-changing effects, either of the whole fractal or the selected transform");
      xFormModGammaResetBtn.setText("Reset all");
      xFormModGammaResetBtn.setPreferredSize(new Dimension(190, 24));
      xFormModGammaResetBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModGammaResetBtn.setBounds(111, 176, 104, 24);
      panel_1.add(xFormModGammaResetBtn);

      xFormModGammaWholeFractalCBx = new JCheckBox("Whole fractal");
      xFormModGammaWholeFractalCBx.setSelected(true);
      xFormModGammaWholeFractalCBx.setToolTipText("Check if Randomize/Reset should apply to the whole fractal rather than only to the selected transform");
      xFormModGammaWholeFractalCBx.setBounds(218, 178, 104, 18);
      panel_1.add(xFormModGammaWholeFractalCBx);

      xFormModHueREd = new JWFNumberField();
      xFormModHueREd.setValueStep(0.01);
      xFormModHueREd.setText("");
      xFormModHueREd.setSize(new Dimension(55, 22));
      xFormModHueREd.setPreferredSize(new Dimension(55, 22));
      xFormModHueREd.setMinValue(-2.0);
      xFormModHueREd.setMaxValue(2.0);
      xFormModHueREd.setLocation(new Point(70, 21));
      xFormModHueREd.setHasMinValue(true);
      xFormModHueREd.setHasMaxValue(true);
      xFormModHueREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormModHueREd.setBounds(68, 134, 55, 22);
      xFormModHueREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormModHueREd.isMouseAdjusting() || xFormModHueREd.getMouseChangeCount() == 0) {
              if (!xFormModHueSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormModHueREd_changed();
          }
        }
      });
      panel_1.add(xFormModHueREd);

      JLabel lblHue = new JLabel();
      lblHue.setToolTipText("Local modification of hue");
      lblHue.setText("Hue");
      lblHue.setSize(new Dimension(64, 22));
      lblHue.setPreferredSize(new Dimension(64, 22));
      lblHue.setLocation(new Point(6, 21));
      lblHue.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblHue.setBounds(4, 134, 64, 22);
      panel_1.add(lblHue);

      JLabel lblHueSpd = new JLabel();
      lblHueSpd.setToolTipText("Blending of local hue change");
      lblHueSpd.setText("Hue Spd");
      lblHueSpd.setSize(new Dimension(64, 22));
      lblHueSpd.setPreferredSize(new Dimension(64, 22));
      lblHueSpd.setLocation(new Point(6, 47));
      lblHueSpd.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblHueSpd.setBounds(4, 154, 64, 22);
      panel_1.add(lblHueSpd);

      xFormModHueSpeedREd = new JWFNumberField();
      xFormModHueSpeedREd.setValueStep(0.01);
      xFormModHueSpeedREd.setText("");
      xFormModHueSpeedREd.setSize(new Dimension(55, 22));
      xFormModHueSpeedREd.setPreferredSize(new Dimension(55, 22));
      xFormModHueSpeedREd.setMinValue(-1.0);
      xFormModHueSpeedREd.setMaxValue(1.0);
      xFormModHueSpeedREd.setLocation(new Point(70, 47));
      xFormModHueSpeedREd.setHasMinValue(true);
      xFormModHueSpeedREd.setHasMaxValue(true);
      xFormModHueSpeedREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormModHueSpeedREd.setBounds(68, 154, 55, 22);
      xFormModHueSpeedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormModHueSpeedREd.isMouseAdjusting() || xFormModHueSpeedREd.getMouseChangeCount() == 0) {
              if (!xFormModHueSpeedSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormModHueSpeedREd_changed();
          }
        }
      });
      panel_1.add(xFormModHueSpeedREd);

      xFormModHueSlider = new JSlider();
      xFormModHueSlider.setValue(0);
      xFormModHueSlider.setSize(new Dimension(172, 22));
      xFormModHueSlider.setPreferredSize(new Dimension(172, 22));
      xFormModHueSlider.setMinimum(-100);
      xFormModHueSlider.setMaximum(100);
      xFormModHueSlider.setLocation(new Point(125, 21));
      xFormModHueSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModHueSlider.setBounds(125, 134, 156, 22);
      xFormModHueSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormModHueSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormModHueSlider_changed();
        }
      });
      panel_1.add(xFormModHueSlider);

      xFormModHueSpeedSlider = new JSlider();
      xFormModHueSpeedSlider.setValue(0);
      xFormModHueSpeedSlider.setSize(new Dimension(172, 22));
      xFormModHueSpeedSlider.setPreferredSize(new Dimension(172, 22));
      xFormModHueSpeedSlider.setMinimum(-100);
      xFormModHueSpeedSlider.setMaximum(100);
      xFormModHueSpeedSlider.setLocation(new Point(125, 47));
      xFormModHueSpeedSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormModHueSpeedSlider.setBounds(125, 154, 156, 22);
      xFormModHueSpeedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormModHueSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormModHueSpeedSlider_changed();
        }
      });
      panel_1.add(xFormModHueSpeedSlider);

      JButton toggleButton = new JButton();
      toggleButton.setToolTipText("Randomize local gamma");
      toggleButton.setSize(new Dimension(95, 24));
      toggleButton.setSelected(false);
      toggleButton.setPreferredSize(new Dimension(42, 24));
      toggleButton.setLocation(new Point(4, 4));
      toggleButton.setBounds(282, 10, 42, 24);
      toggleButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      toggleButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.xFormModGammaRandomizeBtn_Clicked(getXFormModGammaWholeFractalCBx().isSelected());
        }
      });
      panel_1.add(toggleButton);

      JButton toggleButton_1 = new JButton();
      toggleButton_1.setToolTipText("Randomize local contrast");
      toggleButton_1.setSize(new Dimension(95, 24));
      toggleButton_1.setSelected(false);
      toggleButton_1.setPreferredSize(new Dimension(42, 24));
      toggleButton_1.setLocation(new Point(4, 4));
      toggleButton_1.setBounds(282, 54, 42, 24);
      toggleButton_1.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      toggleButton_1.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.xFormModContrastRandomizeBtn_Clicked(getXFormModGammaWholeFractalCBx().isSelected());
        }
      });
      panel_1.add(toggleButton_1);

      JButton toggleButton_2 = new JButton();
      toggleButton_2.setToolTipText("Randomize local saturation");
      toggleButton_2.setSize(new Dimension(95, 24));
      toggleButton_2.setSelected(false);
      toggleButton_2.setPreferredSize(new Dimension(42, 24));
      toggleButton_2.setLocation(new Point(4, 4));
      toggleButton_2.setBounds(282, 98, 42, 24);
      toggleButton_2.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      toggleButton_2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.xFormModSaturationRandomizeBtn_Clicked(getXFormModGammaWholeFractalCBx().isSelected());
        }
      });
      panel_1.add(toggleButton_2);

      JButton toggleButton_3 = new JButton();
      toggleButton_3.setToolTipText("Randomize local hue");
      toggleButton_3.setSize(new Dimension(95, 24));
      toggleButton_3.setSelected(false);
      toggleButton_3.setPreferredSize(new Dimension(42, 24));
      toggleButton_3.setLocation(new Point(4, 4));
      toggleButton_3.setBounds(282, 142, 42, 24);
      toggleButton_3.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      toggleButton_3.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.xFormModHueRandomizeBtn_Clicked(getXFormModGammaWholeFractalCBx().isSelected());
        }
      });
      panel_1.add(toggleButton_3);
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
      tinaAffineTransformationPanel = new JPanel();
      tinaAffineTransformationPanel.setLayout(null);
      tinaAffineTransformationPanel.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaAffineTransformationPanel.add(getAffineC00Lbl(), null);
      tinaAffineTransformationPanel.add(getAffineC00REd(), null);
      tinaAffineTransformationPanel.add(getAffineC01Lbl(), null);
      tinaAffineTransformationPanel.add(getAffineC01REd(), null);
      tinaAffineTransformationPanel.add(getAffineC10Lbl(), null);
      tinaAffineTransformationPanel.add(getAffineC11Lbl(), null);
      tinaAffineTransformationPanel.add(getAffineC10REd(), null);
      tinaAffineTransformationPanel.add(getAffineC11REd(), null);
      tinaAffineTransformationPanel.add(getAffineC20Lbl(), null);
      tinaAffineTransformationPanel.add(getAffineC21Lbl(), null);
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
      affinePreserveZButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affinePreserveZButton.setBounds(218, 155, 104, 24);
      tinaAffineTransformationPanel.add(affinePreserveZButton);
      // tinaAffineTransformationPanel.add(new JToggleButton("whatever"));
      tinaAffineTransformationPanel.add(this.getAffineMirrorPrePostTranslationsButton(), null);

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
      affineRotateEditMotionCurveBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      nonlinearParams1Lbl.setSize(new Dimension(38, 22));
      nonlinearParams1Lbl.setLocation(new Point(4, 26));
      nonlinearParams1Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar1Lbl = new JLabel();
      nonlinearVar1Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar1Lbl.setText("Var 1");
      nonlinearVar1Lbl.setSize(new Dimension(38, 22));
      nonlinearVar1Lbl.setLocation(new Point(4, 2));
      nonlinearVar1Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

      JPanel panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(10, 24));
      panel_1.setMinimumSize(new Dimension(10, 24));
      tinaModifiedWeightsPanel.add(panel_1, BorderLayout.NORTH);
      panel_1.setLayout(null);

      xaosViewAsToBtn = new JRadioButton("View as \"to\"");
      xaosViewAsToBtn.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null)
            tinaController.xaosViewAsChanged();
        }
      });
      xaosViewAsToBtn.setSelected(true);
      xaosViewAsToBtn.setBounds(6, 2, 86, 18);
      panel_1.add(xaosViewAsToBtn);

      xaosViewAsFromBtn = new JRadioButton("View as \"from\"");
      xaosViewAsFromBtn.setBounds(120, 2, 101, 18);
      xaosViewAsFromBtn.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null)
            tinaController.xaosViewAsChanged();
        }
      });
      panel_1.add(xaosViewAsFromBtn);
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
      xFormDrawModeLbl.setToolTipText("");
      xFormDrawModeLbl.setPreferredSize(new Dimension(64, 22));
      xFormDrawModeLbl.setText("Draw mode");
      xFormDrawModeLbl.setSize(new Dimension(119, 22));
      xFormDrawModeLbl.setLocation(new Point(6, 75));
      xFormDrawModeLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormOpacityLbl = new JLabel();
      xFormOpacityLbl.setName("xFormOpacityLbl");
      xFormOpacityLbl.setPreferredSize(new Dimension(64, 22));
      xFormOpacityLbl.setText("Opacity");
      xFormOpacityLbl.setSize(new Dimension(49, 22));
      xFormOpacityLbl.setLocation(new Point(6, 101));
      xFormOpacityLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormSymmetryLbl = new JLabel();
      xFormSymmetryLbl.setName("xFormSymmetryLbl");
      xFormSymmetryLbl.setToolTipText("Color speed");
      xFormSymmetryLbl.setPreferredSize(new Dimension(64, 22));
      xFormSymmetryLbl.setText("Speed");
      xFormSymmetryLbl.setSize(new Dimension(49, 22));
      xFormSymmetryLbl.setLocation(new Point(6, 47));
      xFormSymmetryLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormColorLbl = new JLabel();
      xFormColorLbl.setName("xFormColorLbl");
      xFormColorLbl.setPreferredSize(new Dimension(64, 22));
      xFormColorLbl.setText("Color");
      xFormColorLbl.setSize(new Dimension(49, 22));
      xFormColorLbl.setLocation(new Point(6, 21));
      xFormColorLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaColorChooserPaletteImgPanel.setBounds(125, 10, 195, 10);
      tinaTransformationColorPanel.add(tinaColorChooserPaletteImgPanel);
      tinaColorChooserPaletteImgPanel.setLayout(new BorderLayout(0, 0));

      JLabel xFormMaterialLbl = new JLabel();
      xFormMaterialLbl.setText("Material");
      xFormMaterialLbl.setSize(new Dimension(49, 22));
      xFormMaterialLbl.setPreferredSize(new Dimension(64, 22));
      xFormMaterialLbl.setName("xFormMaterialLbl");
      xFormMaterialLbl.setLocation(new Point(6, 21));
      xFormMaterialLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormMaterialLbl.setBounds(6, 146, 49, 22);
      tinaTransformationColorPanel.add(xFormMaterialLbl);

      xFormMaterialREd = new JWFNumberField();
      xFormMaterialREd.setValueStep(0.01);
      xFormMaterialREd.setText("");
      xFormMaterialREd.setSize(new Dimension(70, 24));
      xFormMaterialREd.setPreferredSize(new Dimension(70, 24));
      xFormMaterialREd.setMotionPropertyName("material");
      xFormMaterialREd.setLocation(new Point(55, 21));
      xFormMaterialREd.setLinkedMotionControlName("xFormMaterialSlider");
      xFormMaterialREd.setLinkedLabelControlName("xFormMaterialLbl");
      xFormMaterialREd.setHasMinValue(true);
      xFormMaterialREd.setHasMaxValue(false);
      xFormMaterialREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormMaterialREd.setBounds(55, 146, 70, 24);
      xFormMaterialREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      xFormMaterialREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormMaterialREd.isMouseAdjusting() || xFormMaterialREd.getMouseChangeCount() == 0) {
              if (!xFormMaterialSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormMaterialREd_changed();
          }
        }
      });
      tinaTransformationColorPanel.add(xFormMaterialREd);

      xFormMaterialSlider = new JSlider();
      xFormMaterialSlider.setValue(0);
      xFormMaterialSlider.setSize(new Dimension(195, 22));
      xFormMaterialSlider.setPreferredSize(new Dimension(195, 22));
      xFormMaterialSlider.setName("xFormMaterialSlider");
      xFormMaterialSlider.setMinimum(0);
      xFormMaterialSlider.setMaximum(300);
      xFormMaterialSlider.setLocation(new Point(125, 21));
      xFormMaterialSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormMaterialSlider.setBounds(125, 146, 195, 22);
      xFormMaterialSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormMaterialSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormMaterialSlider_changed();
        }
      });
      tinaTransformationColorPanel.add(xFormMaterialSlider);

      tinaMaterialChooserPaletteImgPanel = new JPanel();
      tinaMaterialChooserPaletteImgPanel.setBounds(125, 135, 195, 10);
      tinaTransformationColorPanel.add(tinaMaterialChooserPaletteImgPanel);
      tinaMaterialChooserPaletteImgPanel.setLayout(new BorderLayout(0, 0));

      JLabel xFormMaterialSpeedLbl = new JLabel();
      xFormMaterialSpeedLbl.setToolTipText("Material speed");
      xFormMaterialSpeedLbl.setText("Mat Spd");
      xFormMaterialSpeedLbl.setSize(new Dimension(49, 22));
      xFormMaterialSpeedLbl.setPreferredSize(new Dimension(64, 22));
      xFormMaterialSpeedLbl.setName("xFormMaterialSpeedLbl");
      xFormMaterialSpeedLbl.setLocation(new Point(6, 47));
      xFormMaterialSpeedLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormMaterialSpeedLbl.setBounds(6, 172, 49, 22);
      tinaTransformationColorPanel.add(xFormMaterialSpeedLbl);

      xFormMaterialSpeedREd = new JWFNumberField();
      xFormMaterialSpeedREd.setValueStep(0.01);
      xFormMaterialSpeedREd.setText("");
      xFormMaterialSpeedREd.setSize(new Dimension(70, 24));
      xFormMaterialSpeedREd.setPreferredSize(new Dimension(55, 24));
      xFormMaterialSpeedREd.setMotionPropertyName("materialSpeed");
      xFormMaterialSpeedREd.setMinValue(-1.0);
      xFormMaterialSpeedREd.setMaxValue(1.0);
      xFormMaterialSpeedREd.setLocation(new Point(55, 47));
      xFormMaterialSpeedREd.setLinkedMotionControlName("xFormMaterialSpeedSlider");
      xFormMaterialSpeedREd.setLinkedLabelControlName("xFormMaterialSpeedLbl");
      xFormMaterialSpeedREd.setHasMinValue(true);
      xFormMaterialSpeedREd.setHasMaxValue(true);
      xFormMaterialSpeedREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormMaterialSpeedREd.setBounds(55, 172, 70, 24);
      xFormMaterialSpeedREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      xFormMaterialSpeedREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!xFormMaterialSpeedREd.isMouseAdjusting() || xFormMaterialSpeedREd.getMouseChangeCount() == 0) {
              if (!xFormMaterialSpeedSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.xFormMaterialSpeedREd_changed();
          }
        }
      });

      tinaTransformationColorPanel.add(xFormMaterialSpeedREd);

      xFormMaterialSpeedSlider = new JSlider();
      xFormMaterialSpeedSlider.setValue(0);
      xFormMaterialSpeedSlider.setSize(new Dimension(195, 22));
      xFormMaterialSpeedSlider.setPreferredSize(new Dimension(195, 22));
      xFormMaterialSpeedSlider.setName("xFormMaterialSpeedSlider");
      xFormMaterialSpeedSlider.setMinimum(-100);
      xFormMaterialSpeedSlider.setMaximum(100);
      xFormMaterialSpeedSlider.setLocation(new Point(125, 47));
      xFormMaterialSpeedSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormMaterialSpeedSlider.setBounds(125, 172, 195, 22);
      xFormMaterialSpeedSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormMaterialSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.xFormMaterialSpeedSlider_changed();
        }
      });
      tinaTransformationColorPanel.add(xFormMaterialSpeedSlider);
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
      tinaPaletteRandomPointsREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaRandomPaletteButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      distributeColorsButton.setToolTipText("Distribute colors");
      distributeColorsButton.setBounds(243, 58, 79, 24);
      distributeColorsButton.setText("Distrib clr");
      distributeColorsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteShiftLbl.setName("tinaPaletteShiftLbl");
      tinaPaletteShiftLbl.setBounds(116, 7, 29, 22);
      tinaPaletteSubSouthPanel.add(tinaPaletteShiftLbl);
      tinaPaletteShiftLbl.setText("Shift");
      tinaPaletteShiftLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteShiftLbl.setPreferredSize(new Dimension(64, 22));

      randomizeColorSpeedButton = new JButton();
      randomizeColorSpeedButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.randomizeColorSpeedBtn_clicked();
        }
      });
      randomizeColorSpeedButton.setToolTipText("Randomize color speed");
      randomizeColorSpeedButton.setText("Rnd spd");
      randomizeColorSpeedButton.setPreferredSize(new Dimension(190, 24));
      randomizeColorSpeedButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      randomizeColorSpeedButton.setBounds(111, 58, 79, 24);
      tinaPaletteSubSouthPanel.add(randomizeColorSpeedButton);

      JButton randomizeColorShiftButton = new JButton();
      randomizeColorShiftButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.randomizeColorShiftBtn_clicked();
        }
      });
      randomizeColorShiftButton.setToolTipText("Randomize color shift");
      randomizeColorShiftButton.setText("Rnd shift");
      randomizeColorShiftButton.setPreferredSize(new Dimension(190, 24));
      randomizeColorShiftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      randomizeColorShiftButton.setBounds(243, 6, 79, 24);
      tinaPaletteSubSouthPanel.add(randomizeColorShiftButton);

      JButton randomizeGradientButton = new JButton();
      randomizeGradientButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGradientController().selectRandomGradient();
        }
      });
      randomizeGradientButton.setToolTipText("Randomly select a gradient from the currently selected folder");
      randomizeGradientButton.setText("Rnd grd");
      randomizeGradientButton.setPreferredSize(new Dimension(190, 24));
      randomizeGradientButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      randomizeGradientButton.setBounds(6, 6, 79, 24);
      tinaPaletteSubSouthPanel.add(randomizeGradientButton);
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
      tinaPaletteSubTabbedPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteSubTabbedPane.addTab("Gradient Library", null, getGradientLibraryPanel(), null);
      tinaPaletteSubTabbedPane.addTab("Create new", null, getTinaPaletteCreatePanel(), "Create or import a gradient");
      tinaPaletteSubTabbedPane.addTab("Modify gradient", null, getTinaPaletteTransformPanel(), "Apply general modifications to the gradient");
      tinaPaletteSubTabbedPane.addTab("Balancing", null, getTinaPaletteBalancingPanel(), "Apply common color balancing options to the gradient");
      tinaPaletteSubTabbedPane.addTab("Color map", null, getGradientColorMapPnl(), null);
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
      tinaGrabPaletteFromImageButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteCreatePanel.add(tinaGrabPaletteFromImageButton);
      tinaPaletteRandomPointsLbl = new JLabel();
      tinaPaletteRandomPointsLbl.setBounds(163, 34, 100, 14);
      tinaPaletteCreatePanel.add(tinaPaletteRandomPointsLbl);
      tinaPaletteRandomPointsLbl.setText("Random points");
      tinaPaletteRandomPointsLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteRandomPointsLbl.setPreferredSize(new Dimension(100, 22));
      tinaPaletteCreatePanel.add(getTinaPaletteRandomPointsREd());
      tinaPaletteCreatePanel.add(getCreatePaletteScrollPane());

      tinaPaletteFadeColorsCBx = new JCheckBox("Fade colors");
      tinaPaletteFadeColorsCBx.setSelected(true);
      tinaPaletteFadeColorsCBx.setToolTipText("Create a gradient were each key-frame-color is faded into the next one");
      tinaPaletteFadeColorsCBx.setBounds(199, 56, 140, 18);
      tinaPaletteCreatePanel.add(tinaPaletteFadeColorsCBx);

      tinaPaletteUniformWidthCBx = new JCheckBox("Uniform widths");
      tinaPaletteUniformWidthCBx.setSelected(false);
      tinaPaletteUniformWidthCBx.setToolTipText("Make all colors the same width");
      tinaPaletteUniformWidthCBx.setBounds(199, 80, 140, 18);
      tinaPaletteCreatePanel.add(tinaPaletteUniformWidthCBx);

      JLabel lblGradientGenerator = new JLabel();
      lblGradientGenerator.setText("Gradient generator");
      lblGradientGenerator.setPreferredSize(new Dimension(100, 22));
      lblGradientGenerator.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGradientGenerator.setAlignmentX(1.0f);
      lblGradientGenerator.setBounds(163, 8, 100, 14);
      tinaPaletteCreatePanel.add(lblGradientGenerator);

      tinaPaletteRandomGeneratorCmb = new JComboBox();
      tinaPaletteRandomGeneratorCmb.setToolTipText("Random-Symmetry-Geneator");
      tinaPaletteRandomGeneratorCmb.setPreferredSize(new Dimension(50, 24));
      tinaPaletteRandomGeneratorCmb.setMinimumSize(new Dimension(100, 24));
      tinaPaletteRandomGeneratorCmb.setMaximumSize(new Dimension(32767, 24));
      tinaPaletteRandomGeneratorCmb.setMaximumRowCount(32);
      tinaPaletteRandomGeneratorCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteBrightnessLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteBrightnessLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteGammaLbl = new JLabel();
      tinaPaletteGammaLbl.setText("Gamma");
      tinaPaletteGammaLbl.setSize(new Dimension(56, 22));
      tinaPaletteGammaLbl.setLocation(new Point(334, 58));
      tinaPaletteGammaLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteGammaLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteContrastLbl = new JLabel();
      tinaPaletteContrastLbl.setText("Contrast");
      tinaPaletteContrastLbl.setSize(new Dimension(56, 22));
      tinaPaletteContrastLbl.setLocation(new Point(334, 32));
      tinaPaletteContrastLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteContrastLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteSaturationLbl = new JLabel();
      tinaPaletteSaturationLbl.setText("Saturation");
      tinaPaletteSaturationLbl.setSize(new Dimension(56, 22));
      tinaPaletteSaturationLbl.setLocation(new Point(334, 6));
      tinaPaletteSaturationLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteSaturationLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteHueLbl = new JLabel();
      tinaPaletteHueLbl.setText("Hue");
      tinaPaletteHueLbl.setSize(new Dimension(56, 22));
      tinaPaletteHueLbl.setLocation(new Point(6, 84));
      tinaPaletteHueLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteHueLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteBlueLbl = new JLabel();
      tinaPaletteBlueLbl.setText("Blue");
      tinaPaletteBlueLbl.setSize(new Dimension(56, 22));
      tinaPaletteBlueLbl.setLocation(new Point(6, 58));
      tinaPaletteBlueLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteBlueLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteGreenLbl = new JLabel();
      tinaPaletteGreenLbl.setText("Green");
      tinaPaletteGreenLbl.setSize(new Dimension(56, 22));
      tinaPaletteGreenLbl.setLocation(new Point(6, 32));
      tinaPaletteGreenLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteGreenLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteRedLbl = new JLabel();
      tinaPaletteRedLbl.setText("Red");
      tinaPaletteRedLbl.setSize(new Dimension(56, 22));
      tinaPaletteRedLbl.setLocation(new Point(6, 6));
      tinaPaletteRedLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteShiftREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGradientControls().editMotionCurve(e);
        }
      });
      tinaPaletteShiftREd.setMotionPropertyName("modShift");
      tinaPaletteShiftREd.setLinkedMotionControlName("tinaPaletteShiftSlider");
      tinaPaletteShiftREd.setLinkedLabelControlName("tinaPaletteShiftLbl");
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
      tinaPaletteShiftREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteRedREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteGreenREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteBlueREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteHueREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteSaturationREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteContrastREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteGammaREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteBrightnessREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteShiftSlider.setName("tinaPaletteShiftSlider");
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
      tinaPaletteShiftSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteRedSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteGreenSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteBlueSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteHueSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteSaturationSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteContrastSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteGammaSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteBrightnessSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteBrightnessSlider.setPreferredSize(new Dimension(86, 22));
      tinaPaletteBrightnessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.paletteBrightnessSlider_stateChanged(e);
        }
      });
    }
    return tinaPaletteBrightnessSlider;
  }

  public TinaController createController(JWildfire pDesktop, StandardErrorHandler pErrorHandler, Prefs pPrefs, MutaGenFrame mutaGenFrame,
                                         FlameBrowserFrame flameBrowserFrame, EasyMovieMakerFrame easyMovieMakerFrame,
                                         DancingFlamesFrame dancingFlamesFrame, BatchFlameRendererFrame batchFlameRendererFrame,
                                         MeshGenInternalFrame meshGenFrame, InteractiveRendererFrame interactiveRendererFrame, FlamesGPURenderFrame gpuRendererFrame, HelpFrame helpFrame) {
    nonlinearControlsRows = new TinaNonlinearControlsRow[12];
    nonlinearControlsRows[0] = new TinaNonlinearControlsRow(0, getNonlinearVar1Panel(), nonlinearVar1Lbl, getNonlinearVar1Cmb(), getNonlinearParams1Cmb(), getNonlinearVar1REd(),
        getNonlinearParams1REd(), getNonlinearParams1LeftButton(), getNonlinearParams1PreButton(), getNonlinearParams1PostButton(), null,
        getNonlinearParams1ToggleParamsPnlButton());
    nonlinearControlsRows[1] = new TinaNonlinearControlsRow(1, getNonlinearVar2Panel(), nonlinearVar2Lbl, getNonlinearVar2Cmb(), getNonlinearParams2Cmb(), getNonlinearVar2REd(),
        getNonlinearParams2REd(), getNonlinearParams2LeftButton(), getNonlinearParams2PreButton(), getNonlinearParams2PostButton(), getNonlinearParams2UpButton(),
        getNonlinearParams2ToggleParamsPnlButton());
    nonlinearControlsRows[2] = new TinaNonlinearControlsRow(2, getNonlinearVar3Panel(), nonlinearVar3Lbl, getNonlinearVar3Cmb(), getNonlinearParams3Cmb(), getNonlinearVar3REd(),
        getNonlinearParams3REd(), getNonlinearParams3LeftButton(), getNonlinearParams3PreButton(), getNonlinearParams3PostButton(), getNonlinearParams3UpButton(),
        getNonlinearParams3ToggleParamsPnlButton());
    nonlinearControlsRows[3] = new TinaNonlinearControlsRow(3, getNonlinearVar4Panel(), nonlinearVar4Lbl, getNonlinearVar4Cmb(), getNonlinearParams4Cmb(), getNonlinearVar4REd(),
        getNonlinearParams4REd(), getNonlinearParams4LeftButton(), getNonlinearParams4PreButton(), getNonlinearParams4PostButton(), getNonlinearParams4UpButton(),
        getNonlinearParams4ToggleParamsPnlButton());
    nonlinearControlsRows[4] = new TinaNonlinearControlsRow(4, getNonlinearVar5Panel(), nonlinearVar5Lbl, getNonlinearVar5Cmb(), getNonlinearParams5Cmb(), getNonlinearVar5REd(),
        getNonlinearParams5REd(), getNonlinearParams5LeftButton(), getNonlinearParams5PreButton(), getNonlinearParams5PostButton(), getNonlinearParams5UpButton(),
        getNonlinearParams5ToggleParamsPnlButton());
    nonlinearControlsRows[5] = new TinaNonlinearControlsRow(5, getNonlinearVar6Panel(), nonlinearVar6Lbl, getNonlinearVar6Cmb(), getNonlinearParams6Cmb(), getNonlinearVar6REd(),
        getNonlinearParams6REd(), getNonlinearParams6LeftButton(), getNonlinearParams6PreButton(), getNonlinearParams6PostButton(), getNonlinearParams6UpButton(),
        getNonlinearParams6ToggleParamsPnlButton());
    nonlinearControlsRows[6] = new TinaNonlinearControlsRow(6, getNonlinearVar7Panel(), nonlinearVar7Lbl, getNonlinearVar7Cmb(), getNonlinearParams7Cmb(), getNonlinearVar7REd(),
        getNonlinearParams7REd(), getNonlinearParams7LeftButton(), getNonlinearParams7PreButton(), getNonlinearParams7PostButton(), getNonlinearParams7UpButton(),
        getNonlinearParams7ToggleParamsPnlButton());
    nonlinearControlsRows[7] = new TinaNonlinearControlsRow(7, getNonlinearVar8Panel(), nonlinearVar8Lbl, getNonlinearVar8Cmb(), getNonlinearParams8Cmb(), getNonlinearVar8REd(),
        getNonlinearParams8REd(), getNonlinearParams8LeftButton(), getNonlinearParams8PreButton(), getNonlinearParams8PostButton(), getNonlinearParams8UpButton(),
        getNonlinearParams8ToggleParamsPnlButton());
    nonlinearControlsRows[8] = new TinaNonlinearControlsRow(8, getNonlinearVar9Panel(), nonlinearVar9Lbl, getNonlinearVar9Cmb(), getNonlinearParams9Cmb(), getNonlinearVar9REd(),
        getNonlinearParams9REd(), getNonlinearParams9LeftButton(), getNonlinearParams9PreButton(), getNonlinearParams9PostButton(), getNonlinearParams9UpButton(),
        getNonlinearParams9ToggleParamsPnlButton());
    nonlinearControlsRows[9] = new TinaNonlinearControlsRow(9, getNonlinearVar10Panel(), nonlinearVar10Lbl, getNonlinearVar10Cmb(), getNonlinearParams10Cmb(), getNonlinearVar10REd(),
        getNonlinearParams10REd(), getNonlinearParams10LeftButton(), getNonlinearParams10PreButton(), getNonlinearParams10PostButton(), getNonlinearParams10UpButton(),
        getNonlinearParams10ToggleParamsPnlButton());
    nonlinearControlsRows[10] = new TinaNonlinearControlsRow(10, getNonlinearVar11Panel(), nonlinearVar11Lbl, getNonlinearVar11Cmb(), getNonlinearParams11Cmb(), getNonlinearVar11REd(),
        getNonlinearParams11REd(), getNonlinearParams11LeftButton(), getNonlinearParams11PreButton(), getNonlinearParams11PostButton(), getNonlinearParams11UpButton(),
        getNonlinearParams11ToggleParamsPnlButton());
    nonlinearControlsRows[11] = new TinaNonlinearControlsRow(11, getNonlinearVar12Panel(), nonlinearVar12Lbl, getNonlinearVar12Cmb(), getNonlinearParams12Cmb(), getNonlinearVar12REd(),
        getNonlinearParams12REd(), getNonlinearParams12LeftButton(), getNonlinearParams12PreButton(), getNonlinearParams12PostButton(), getNonlinearParams12UpButton(),
        getNonlinearParams12ToggleParamsPnlButton());

    initFilterKernelCmb(getPostBokehFilterKernelCmb());

    initFilterTypeCmb(getTinaFilterTypeCmb());
    initPostSymmetryTypeCmb(getPostSymmetryTypeCmb());
    initRandomGenCmb(getRandomStyleCmb());
    initRandomSymmetryCmb(getRandomSymmetryCmb());
    initRandomMovieGenCmb(easyMovieMakerFrame.getSwfAnimatorRandGenCmb());
    initStereo3dModeCmb(getStereo3dModeCmb());
    initStereo3dPreviewCmb(getStereo3dPreviewCmb());
    initStereo3dColorCmb(getStereo3dLeftEyeColorCmb(), Stereo3dColor.RED);
    initStereo3dColorCmb(getStereo3dRightEyeColorCmb(), Stereo3dColor.CYAN);
    initRandomGradientCmb(getRandomGradientCmb());
    initRandomGradientCmb(getTinaPaletteRandomGeneratorCmb());
    initPreFilterTypeCmb(meshGenFrame.getMeshGenPreFilter1Cmb());
    initPreFilterTypeCmb(meshGenFrame.getMeshGenPreFilter2Cmb());
    initDOFBlurShapeCmb(getDofDOFShapeCmb());
    initBGColorTypoCmb(getBackgroundColorTypeCmb());
    initMeshGenOutputTypeCmb(meshGenFrame.getMeshGenOutputTypeCmb());

    TinaControllerParameter params = new TinaControllerParameter();

    params.setMutaGenParams(
        mutaGenFrame.getMutaGen01Pnl(), mutaGenFrame.getMutaGen02Pnl(), mutaGenFrame.getMutaGen03Pnl(), mutaGenFrame.getMutaGen04Pnl(), mutaGenFrame.getMutaGen05Pnl(),
        mutaGenFrame.getMutaGen06Pnl(), mutaGenFrame.getMutaGen07Pnl(), mutaGenFrame.getMutaGen08Pnl(), mutaGenFrame.getMutaGen09Pnl(), mutaGenFrame.getMutaGen10Pnl(),
        mutaGenFrame.getMutaGen11Pnl(), mutaGenFrame.getMutaGen12Pnl(), mutaGenFrame.getMutaGen13Pnl(), mutaGenFrame.getMutaGen14Pnl(), mutaGenFrame.getMutaGen15Pnl(),
        mutaGenFrame.getMutaGen16Pnl(), mutaGenFrame.getMutaGen17Pnl(), mutaGenFrame.getMutaGen18Pnl(), mutaGenFrame.getMutaGen19Pnl(), mutaGenFrame.getMutaGen20Pnl(),
        mutaGenFrame.getMutaGen21Pnl(), mutaGenFrame.getMutaGen22Pnl(), mutaGenFrame.getMutaGen23Pnl(), mutaGenFrame.getMutaGen24Pnl(), mutaGenFrame.getMutaGen25Pnl(),
        mutaGenFrame.getMutaGenLoadFlameFromEditorBtn(), mutaGenFrame.getMutaGenLoadFlameFromFileBtn(), mutaGenFrame.getMutaGenProgressBar(), mutaGenFrame.getMutaGenAmountREd(),
        mutaGenFrame.getMutaGenHorizontalTrend1Cmb(), mutaGenFrame.getMutaGenHorizontalTrend2Cmb(), mutaGenFrame.getMutaGenVerticalTrend1Cmb(), mutaGenFrame.getMutaGenVerticalTrend2Cmb(),
        mutaGenFrame.getMutaGenBackBtn(), mutaGenFrame.getMutaGenForwardBtn(), mutaGenFrame.getMutaGenHintPane(), mutaGenFrame.getMutaGenSaveFlameToEditorBtn(),
        mutaGenFrame.getMutaGenSaveFlameToFileBtn());

    params.setFlameBrowserParams(flameBrowserFrame.getFlameBrowserTree(), flameBrowserFrame.getFlameBrowserImagesPanel(),
        flameBrowserFrame.getFlameBrowserRefreshBtn(), flameBrowserFrame.getFlameBrowserChangeFolderBtn(), flameBrowserFrame.getFlameBrowserToEditorBtn(),
        flameBrowserFrame.getFlameBrowserToBatchRendererBtn(), flameBrowserFrame.getFlameBrowserDeleteBtn(),
        flameBrowserFrame.getFlameBrowserRenameBtn(), flameBrowserFrame.getFlameBrowserCopyToBtn(), flameBrowserFrame.getFlameBrowserMoveToBtn(),
        flameBrowserFrame.getFlameBrowserToMeshGenBtn());

    params.setEasyMovieMakerParams(easyMovieMakerFrame.getSwfAnimatorResolutionProfileCmb(), easyMovieMakerFrame.getSwfAnimatorQualityProfileCmb());

    params.setDancingFlamesParams(dancingFlamesFrame.getRealtimeFlamePnl(), dancingFlamesFrame.getRealtimeGraph1Pnl(), dancingFlamesFrame.getDancingFlamesLoadSoundBtn(),
        dancingFlamesFrame.getDancingFlamesAddFromClipboardBtn(), dancingFlamesFrame.getDancingFlamesAddFromEditorBtn(), dancingFlamesFrame.getDancingFlamesAddFromDiscBtn(),
        dancingFlamesFrame.getDancingFlamesRandomCountIEd(), dancingFlamesFrame.getDancingFlamesGenRandFlamesBtn(), dancingFlamesFrame.getDancingFlamesRandomGenCmb(),
        dancingFlamesFrame.getDancingFlamesPoolFlamePreviewPnl(), dancingFlamesFrame.getDancingFlamesBorderSizeSlider(), dancingFlamesFrame.getDancingFlamesFlameToEditorBtn(),
        dancingFlamesFrame.getDancingFlamesDeleteFlameBtn(), dancingFlamesFrame.getDancingFlamesFramesPerSecondIEd(), dancingFlamesFrame.getDancingFlamesMorphFrameCountIEd(),
        dancingFlamesFrame.getDancingFlamesStartShowBtn(), dancingFlamesFrame.getDancingFlamesStopShowBtn(), dancingFlamesFrame.getDancingFlamesDoRecordCBx(),
        dancingFlamesFrame.getDancingFlamesFlameCmb(), dancingFlamesFrame.getDancingFlamesDrawTrianglesCBx(), dancingFlamesFrame.getDancingFlamesDrawFFTCBx(),
        dancingFlamesFrame.getDancingFlamesDrawFPSCBx(), dancingFlamesFrame.getDancingFlamesFlamePropertiesTree(), dancingFlamesFrame.getDancingFlamesMotionPropertyPnl(),
        dancingFlamesFrame.getDancingFlamesMotionTable(), dancingFlamesFrame.getDancingFlamesAddMotionCmb(), dancingFlamesFrame.getDancingFlamesAddMotionBtn(),
        dancingFlamesFrame.getDancingFlamesDeleteMotionBtn(), dancingFlamesFrame.getDancingFlamesLinkMotionBtn(), dancingFlamesFrame.getDancingFlamesUnlinkMotionBtn(),
        dancingFlamesFrame.getDancingFlamesCreateMotionsCmb(), dancingFlamesFrame.getDancingFlamesClearMotionsBtn(), dancingFlamesFrame.getDancingFlamesLoadProjectBtn(),
        dancingFlamesFrame.getDancingFlamesSaveProjectBtn(), dancingFlamesFrame.getDancingFlamesMotionLinksTable(), dancingFlamesFrame.getDancingFlamesReplaceFlameFromEditorBtn(),
        dancingFlamesFrame.getDancingFlamesRenameFlameBtn(), dancingFlamesFrame.getDancingFlamesRenameMotionBtn(), dancingFlamesFrame.getDancingFlamesMutedCBx());

    params.setBatchFlameRendererParams(batchFlameRendererFrame.getRenderBatchJobsTable(), batchFlameRendererFrame.getBatchPreviewRootPanel(),
        batchFlameRendererFrame.getBatchRenderJobProgressBar(), batchFlameRendererFrame.getBatchRenderTotalProgressBar(),
        new JobProgressUpdater(batchFlameRendererFrame), batchFlameRendererFrame.getBatchRenderAddFilesButton(), batchFlameRendererFrame.getBatchRenderFilesMoveDownButton(),
        batchFlameRendererFrame.getBatchRenderFilesMoveUpButton(), batchFlameRendererFrame.getBatchRenderFilesRemoveButton(),
        batchFlameRendererFrame.getBatchRenderFilesRemoveAllButton(), batchFlameRendererFrame.getBatchRenderStartButton(),
        batchFlameRendererFrame.getBatchQualityProfileCmb(), batchFlameRendererFrame.getBatchResolutionProfileCmb(),
        batchFlameRendererFrame.getBatchRenderOverrideCBx(), batchFlameRendererFrame.getBatchRenderShowImageBtn(),
        batchFlameRendererFrame.getEnableOpenClBtn());

    params.setMeshGenParams(meshGenFrame.getMeshGenFromEditorBtn(), meshGenFrame.getMeshGenFromClipboardBtn(), meshGenFrame.getMeshGenLoadFlameBtn(),
        meshGenFrame.getMeshGenSliceCountREd(), meshGenFrame.getMeshGenSlicesPerRenderREd(), meshGenFrame.getMeshGenRenderWidthREd(),
        meshGenFrame.getMeshGenRenderHeightREd(), meshGenFrame.getMeshGenRenderQualityREd(), meshGenFrame.getMeshGenProgressbar(),
        meshGenFrame.getMeshGenGenerateBtn(), meshGenFrame.getMeshGenTopViewRootPnl(), meshGenFrame.getMeshGenFrontViewRootPnl(),
        meshGenFrame.getMeshGenPerspectiveViewRootPnl(), meshGenFrame.getMeshGenCentreXREd(),
        meshGenFrame.getMeshGenCentreXSlider(), meshGenFrame.getMeshGenCentreYREd(), meshGenFrame.getMeshGenCentreYSlider(),
        meshGenFrame.getMeshGenZoomREd(), meshGenFrame.getMeshGenZoomSlider(), meshGenFrame.getMeshGenZMinREd(),
        meshGenFrame.getMeshGenZMinSlider(), meshGenFrame.getMeshGenZMaxREd(), meshGenFrame.getMeshGenZMaxSlider(),
        meshGenFrame.getMeshGenCellSizeREd(), meshGenFrame.getMeshGenTopViewRenderBtn(), meshGenFrame.getMeshGenFrontViewRenderBtn(),
        meshGenFrame.getMeshGenPerspectiveViewRenderBtn(), meshGenFrame.getMeshGenTopViewToEditorBtn(), meshGenFrame.getMeshGenLoadSequenceBtn(),
        meshGenFrame.getMeshGenSequenceWidthREd(), meshGenFrame.getMeshGenSequenceHeightREd(), meshGenFrame.getMeshGenSequenceSlicesREd(),
        meshGenFrame.getMeshGenSequenceDownSampleREd(), meshGenFrame.getMeshGenSequenceFilterRadiusREd(), meshGenFrame.getMeshGenGenerateMeshProgressbar(),
        meshGenFrame.getMeshGenGenerateMeshBtn(), meshGenFrame.getMeshGenSequenceFromRendererBtn(), meshGenFrame.getMeshGenSequenceThresholdREd(),
        meshGenFrame.getMeshGenSequenceLbl(), meshGenFrame.getMeshGenPreviewRootPanel(), meshGenFrame.getMeshGenAutoPreviewCBx(),
        meshGenFrame.getMeshGenPreviewImportLastGeneratedMeshBtn(), meshGenFrame.getMeshGenPreviewImportFromFileBtn(),
        meshGenFrame.getMeshGenClearPreviewBtn(), meshGenFrame.getMeshGenPreviewPositionXREd(), meshGenFrame.getMeshGenPreviewPositionYREd(),
        meshGenFrame.getMeshGenPreviewSizeREd(), meshGenFrame.getMeshGenPreviewScaleZREd(), meshGenFrame.getMeshGenPreviewRotateAlphaREd(),
        meshGenFrame.getMeshGenPreviewRotateBetaREd(), meshGenFrame.getMeshGenPreviewPointsREd(), meshGenFrame.getMeshGenPreviewPolygonsREd(),
        meshGenFrame.getMeshGenRefreshPreviewBtn(), meshGenFrame.getMeshGenPreviewSunflowExportBtn(), meshGenFrame.getMeshGenPreFilter1Cmb(),
        meshGenFrame.getMeshGenPreFilter2Cmb(), meshGenFrame.getMeshGenImageStepREd(), meshGenFrame.getMeshGenOutputTypeCmb(),
        meshGenFrame.getMeshGenTaubinSmoothCbx(), meshGenFrame.getMeshGenSmoothPassesREd(), meshGenFrame.getMeshGenSmoothLambdaREd(),
        meshGenFrame.getMeshGenSmoothMuREd());

    params.setHelpParams(helpFrame.getMeshGenHintPane(), helpFrame.getHelpPane(), helpFrame.getApophysisHintsPane());

    params.setParams1(pDesktop, this, pErrorHandler, pPrefs, /* getCenterCenterPanel()*/getMainPrevievPnl(), getTinaCameraRollREd(), getTinaCameraRollSlider(), getTinaCameraPitchREd(),
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
        getTinaTransformationsTable(), 
        getAffineC00Lbl(), getAffineC01Lbl(), getAffineC10Lbl(), getAffineC11Lbl(), getAffineC00REd(),
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
        getAffineResetTransformButton(), getCreatePaletteColorsTable(),
        getMouseTransformSlowButton(), getRootPanel(), getAffineFlipHorizontalButton(), getAffineFlipVerticalButton(),
        getPostBlurRadiusREd(), getPostBlurRadiusSlider(), getPostBlurFadeREd(), getPostBlurFadeSlider(), getPostBlurFallOffREd(), getPostBlurFallOffSlider(),
        getAffineScaleXButton(), getAffineScaleYButton(), gradientLibraryThumbnailPnl,
        getToggleVariationsButton(), getToggleTransparencyButton(), getAffinePreserveZButton(), getAffineMirrorPrePostTranslationsButton(),
        getQualityProfileCmb(), getResolutionProfileCmb(),
        interactiveRendererFrame.getInteractiveResolutionProfileCmb(), getTinaRenderFlameButton(), getRenderMainButton(), getTinaAppendToMovieButton(),
        getTransformationWeightREd(), getUndoButton(), getRedoButton(),
        getXFormAntialiasAmountREd(), getXFormAntialiasAmountSlider(), getXFormAntialiasRadiusREd(), getXFormAntialiasRadiusSlider(),
        getTinaZBufferScaleREd(), getTinaZBufferScaleSlider(), getTinaFilterTypeCmb(), getTinaFilterKernelCmbLbl(), getTinaFilterRadiusLbl(),
        getTinaFilterIndicatorCBx(), getThumbnailSelectPopupMenu(), getThumbnailRemovePopupMenu(), getTinaFilterSharpnessREd(),
        getTinaFilterSharpnessSlider(), getTinaFilterLowDensityREd(), getTinaFilterLowDensitySlider());

    params.setParams2(getEditTransformCaptionBtn(), getEditFlameTitleBtn(), getSnapShotButton(), getBtnQsave(), getSendFlameToIRButton(),
        getTinaAppendToMovieButton(), getMouseTransformSlowButton(), getToggleTransparencyButton(), getMouseTransformRotateTrianglesButton(),
        getMouseTransformScaleTrianglesButton(), getScriptTree(), getScriptDescriptionTextArea(), getScriptTextArea(), getRescanScriptsBtn(),
        getNewScriptBtn(), getNewScriptFromFlameBtn(), getDeleteScriptBtn(), getScriptRenameBtn(), getDuplicateScriptBtn(), getScriptRunBtn(),
        getMouseTransformEditGradientButton(), getGradientLibTree(), getGradientLibraryRescanBtn(), getGradientLibraryNewFolderBtn(), getGradientLibraryRenameFolderBtn(),
        getGradientsList(), getBackgroundColorIndicatorBtn(), getRandomizeBtn(),
        getTinaPaletteFadeColorsCBx(), getTinaPaletteUniformWidthCBx(), getLayerWeightEd(), getLayerAddBtn(), getLayerDuplicateBtn(), getLayerDeleteBtn(),
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
        getMouseTransformEditTriangleViewButton(), getTinaPaletteRandomGeneratorCmb(), getToggleTriangleWithColorsButton(),
        getAffineRotateEditMotionCurveBtn(), getAffineScaleEditMotionCurveBtn(),
        getTriangleStyleCmb(), getXFormModGammaREd(), getXFormModGammaSlider(), getXFormModGammaSpeedREd(), getXFormModGammaSpeedSlider(),
        getXFormModContrastREd(), getXFormModContrastSlider(), getXFormModContrastSpeedREd(), getXFormModContrastSpeedSlider(),
        getXFormModSaturationREd(), getXFormModSaturationSlider(), getXFormModSaturationSpeedREd(), getXFormModSaturationSpeedSlider(),
        getBtnAllSave());

    params.setParams3(getChannelMixerResetBtn(), getChannelMixerModeCmb(),
        getChannelMixerRedRedRootPanel(), getChannelMixerRedGreenRootPanel(), getChannelMixerRedBlueRootPanel(), getChannelMixerGreenRedRootPanel(),
        getChannelMixerGreenGreenRootPanel(), getChannelMixerGreenBlueRootPanel(), getChannelMixerBlueRedRootPanel(), getChannelMixerBlueGreenRootPanel(),
        getChannelMixerBlueBlueRootPanel(), getMotionCurvePlayPreviewButton(), getDofDOFShapeCmb(), getDofDOFScaleREd(), getDofDOFScaleSlider(), getDofDOFAngleREd(), getDofDOFAngleSlider(),
        getDofDOFFadeREd(), getDofDOFFadeSlider(), getDofDOFParam1REd(), getDofDOFParam1Slider(), getDofDOFParam1Lbl(), getDofDOFParam2REd(),
        getDofDOFParam2Slider(), getDofDOFParam2Lbl(), getDofDOFParam3REd(), getDofDOFParam3Slider(), getDofDOFParam3Lbl(), getDofDOFParam4REd(),
        getDofDOFParam4Slider(), getDofDOFParam4Lbl(), getDofDOFParam5REd(), getDofDOFParam5Slider(), getDofDOFParam5Lbl(), getDofDOFParam6REd(),
        getDofDOFParam6Slider(), getDofDOFParam6Lbl(), getBokehBtn(),
        getResetCameraSettingsBtn(), getResetDOFSettingsButton(), getResetBokehOptionsButton(), getResetColoringOptionsButton(),
        getResetAntialiasOptionsButton(), getResetPostBlurSettingsBtn(), getResetStereo3DSettingsBtn(), getResetPostSymmetrySettingsBtn(),
        getResetMotionBlurSettingsBtn(), getXaosViewAsToBtn(), getXaosViewAsFromBtn(), getToggleDrawGuidesButton(), getPreviewEastMainPanel(),
        getMacroButtonPanel(), getScriptAddButtonBtn(), getMacroButtonsTable(), getMacroButtonMoveUpBtn(), getMacroButtonMoveDownBtn(),
        getMacroButtonDeleteBtn(), getToggleDetachedPreviewButton(), getGradientResetBtn(), getTinaWhiteLevelREd(), getTinaWhiteLevelSlider(),
        getMacroButtonHorizPanel(), getMacroButtonHorizRootPanel(), getAffineXYEditPlaneToggleBtn(), getAffineYZEditPlaneToggleBtn(), getAffineZXEditPlaneToggleBtn(),
        getGradientColorMapHorizOffsetREd(), getGradientColorMapHorizOffsetSlider(), getGradientColorMapHorizScaleREd(),
        getGradientColorMapHorizScaleSlider(), getGradientColorMapVertOffsetREd(), getGradientColorMapVertOffsetSlider(),
        getGradientColorMapVertScaleREd(), getGradientColorMapVertScaleSlider(), getGradientColorMapLocalColorAddREd(),
        getGradientColorMapLocalColorAddSlider(), getGradientColorMapLocalColorScaleREd(), getGradientColorMapLocalColorScaleSlider(),
        getFlameFPSField(), getLeapMotionToggleButton(), getLeapMotionConfigTable(),
        getLeapMotionHandCmb(), getLeapMotionInputChannelCmb(), getLeapMotionOutputChannelCmb(), getLeapMotionIndex1Field(),
        getLeapMotionIndex2Field(), getLeapMotionIndex3Field(), getLeapMotionInvScaleField(),
        getLeapMotionOffsetField(), getLeapMotionAddButton(), getLeapMotionDuplicateButton(),
        getLeapMotionDeleteButton(), getLeapMotionClearButton(), getLeapMotionResetConfigButton(),
        getFilterKernelPreviewRootPnl(), getTinaSpatialOversamplingREd(), getTinaSpatialOversamplingSlider(),
        getFilterKernelFlatPreviewBtn(),
        getTinaPostNoiseFilterCheckBox(), getTinaPostNoiseThresholdField(), getTinaPostNoiseThresholdSlider(),
        getForegroundOpacityField(), getForegroundOpacitySlider(), getScriptEditBtn(), getRealtimePreviewToggleButton(),
        getSolidRenderingToggleBtn(), getTinaSolidRenderingEnableAOCBx(), getTinaSolidRenderingAOIntensityREd(),
        getTinaSolidRenderingAOIntensitySlider(), getTinaSolidRenderingAOSearchRadiusREd(), getTinaSolidRenderingAOSearchRadiusSlider(),
        getTinaSolidRenderingAOBlurRadiusREd(), getTinaSolidRenderingAOBlurRadiusSlider(), getTinaSolidRenderingAOFalloffREd(),
        getTinaSolidRenderingAOFalloffSlider(), getTinaSolidRenderingAORadiusSamplesREd(), getTinaSolidRenderingAORadiusSamplesSlider(),
        getTinaSolidRenderingAOAzimuthSamplesREd(), getTinaSolidRenderingAOAzimuthSamplesSlider(),
        getTinaSolidRenderingAOAffectDiffuseREd(), getTinaSolidRenderingAOAffectDiffuseSlider(),
        getResetSolidRenderingMaterialsBtn(), getResetSolidRenderingLightsBtn(),
        getTinaSolidRenderingSelectedLightCmb(), getTinaSolidRenderingAddLightBtn(), getTinaSolidRenderingDeleteLightBtn(),
        getTinaSolidRenderingLightAltitudeREd(), getTinaSolidRenderingLightAzimuthREd(),
        getTinaSolidRenderingLightAltitudeSlider(), getTinaSolidRenderingLightAzimuthSlider(),
        getTinaSolidRenderingLightColorBtn(), getTinaSolidRenderingLightCastShadowsCBx(), getTinaSolidRenderingLightIntensityREd0(),
        getTinaSolidRenderingLightIntensitySlider(), getTinaSolidRenderingShadowIntensityREd(),
        getTinaSolidRenderingShadowIntensitySlider(), getTinaSolidRenderingSelectedMaterialCmb(), getTinaSolidRenderingAddMaterialBtn(),
        getTinaSolidRenderingDeleteMaterialBtn(), getTinaSolidRenderingMaterialDiffuseREd(), getTinaSolidRenderingMaterialDiffuseSlider(),
        getTinaSolidRenderingMaterialAmbientREd(), getTinaSolidRenderingMaterialAmbientSlider(), getTinaSolidRenderingMaterialSpecularREd(),
        getTinaSolidRenderingMaterialSpecularSlider(), getTinaSolidRenderingMaterialSpecularSharpnessREd(),
        getTinaSolidRenderingMaterialSpecularSharpnessSlider(), getTinaSolidRenderingMaterialSpecularColorBtn(),
        getTinaSolidRenderingMaterialDiffuseResponseCmb(), getTinaSolidRenderingMaterialReflectionMapIntensityREd(),
        getTinaSolidRenderingMaterialReflectionMapIntensitySlider(), getTinaSolidRenderingMaterialReflMapBtn(),
        getTinaSolidRenderingMaterialSelectReflMapBtn(), getTinaSolidRenderingMaterialRemoveReflMapBtn(),
        getTinaSolidRenderingMaterialReflectionMappingCmb(),
        getXFormModHueREd(), getXFormModHueSlider(), getXFormModHueSpeedREd(), getXFormModHueSpeedSlider(),
        getXFormMaterialREd(), getXFormMaterialSlider(), getXFormMaterialSpeedREd(), getXFormMaterialSpeedSlider(),
        getResetSolidRenderingHardShadowOptionsBtn(), getResetSolidRenderingAmbientShadowOptionsBtn(),
        getTinaSolidRenderingShadowTypeCmb(), getTinaSolidRenderingShadowmapSizeCmb(), getTinaSolidRenderingShadowSmoothRadiusREd(),
        getTinaSolidRenderingShadowSmoothRadiusSlider(), getTinaSolidRenderingShadowmapBiasREd(), getTinaSolidRenderingShadowmapBiasSlider(),
        getBokehSettingsPnl(), getPostBokehSettingsPnl(), getResetPostBokehSettingsBtn(), getPostBokehIntensityREd(), getPostBokehIntensitySlider(),
        getPostBokehBrightnessREd(), getPostBokehBrightnessSlider(), getPostBokehSizeREd(), getPostBokehSizeSlider(), getPostBokehActivationREd(),
        getPostBokehActivationSlider(), getPostBokehFilterKernelCmb(), gpuRendererFrame.getInteractiveResolutionProfileCmb(),
        gpuRendererFrame.getInteractiveQualityProfileCmb(),
        getLowDensityBrightnessREd(), getLowDensityBrightnessSlider(), getBalanceRedREd(), getBalanceRedSlider(),
        getBalanceGreenREd(), getBalanceGreenSlider(), getBalanceBlueREd(), getBalanceBlueSlider(),
        getBackgroundColorURIndicatorBtn(), getBackgroundColorLLIndicatorBtn(), getBackgroundColorLRIndicatorBtn(), getBackgroundColorTypeCmb(),
        getBackgroundColorCCIndicatorBtn());

    tinaController = new TinaController(params);

    for (TinaNonlinearControlsRow row : nonlinearControlsRows) {
      row.setTinaController(tinaController);
    }

    getFilterKernelFlatPreviewBtn().setSelected(Prefs.getPrefs().isTinaDefaultFilterVisualisationFlat());

    VariationControlsDelegate[] variationControlsDelegates = new VariationControlsDelegate[12];
    for (int i = 0; i < variationControlsDelegates.length; i++) {
      variationControlsDelegates[i] = new VariationControlsDelegate(tinaController, tinaController.getData(), getRootPanel(), i);
    }
    tinaController.setVariationControlsDelegates(variationControlsDelegates);

    tinaController.refreshing = tinaController.cmbRefreshing = tinaController.gridRefreshing = true;
    try {
      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add(getXaosViewAsToBtn());
      buttonGroup.add(getXaosViewAsFromBtn());

      for (TinaNonlinearControlsRow row : nonlinearControlsRows) {
        row.initControls();
      }

      getTinaSolidRenderingShadowTypeCmb().removeAllItems();
      getTinaSolidRenderingShadowTypeCmb().addItem(ShadowType.OFF);
      getTinaSolidRenderingShadowTypeCmb().addItem(ShadowType.FAST);
      getTinaSolidRenderingShadowTypeCmb().addItem(ShadowType.SMOOTH);

      getTinaSolidRenderingShadowmapSizeCmb().removeAllItems();
      getTinaSolidRenderingShadowmapSizeCmb().addItem("512");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("1024");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("1536");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("2048");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("2560");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("3072");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("3584");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("4096");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("4608");
      getTinaSolidRenderingShadowmapSizeCmb().addItem("5120");

      getXFormDrawModeCmb().removeAllItems();
      getXFormDrawModeCmb().addItem(DrawMode.NORMAL);
      getXFormDrawModeCmb().addItem(DrawMode.OPAQUE);
      getXFormDrawModeCmb().addItem(DrawMode.HIDDEN);

      getTinaSolidRenderingMaterialDiffuseResponseCmb().removeAllItems();
      getTinaSolidRenderingMaterialDiffuseResponseCmb().addItem(LightDiffFuncPreset.COSA);
      getTinaSolidRenderingMaterialDiffuseResponseCmb().addItem(LightDiffFuncPreset.COSA_SQUARE);
      getTinaSolidRenderingMaterialDiffuseResponseCmb().addItem(LightDiffFuncPreset.COSA_HALVE);
      getTinaSolidRenderingMaterialDiffuseResponseCmb().addItem(LightDiffFuncPreset.COSA_HALVE_SQUARE);

      getTinaSolidRenderingMaterialReflectionMappingCmb().removeAllItems();
      getTinaSolidRenderingMaterialReflectionMappingCmb().addItem(ReflectionMapping.BLINN_NEWELL);
      getTinaSolidRenderingMaterialReflectionMappingCmb().addItem(ReflectionMapping.SPHERICAL);

      getChannelMixerModeCmb().removeAllItems();
      getChannelMixerModeCmb().addItem(ChannelMixerMode.OFF);
      getChannelMixerModeCmb().addItem(ChannelMixerMode.BRIGHTNESS);
      getChannelMixerModeCmb().addItem(ChannelMixerMode.RGB);
      getChannelMixerModeCmb().addItem(ChannelMixerMode.FULL);

      initTriangleStyleCmb(getTriangleStyleCmb(), pPrefs);
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript1Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript2Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript3Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript4Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript5Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript6Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript7Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript8Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript9Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript10Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript11Cmb());
      initGlobalScriptCmb(easyMovieMakerFrame.getSwfAnimatorGlobalScript12Cmb());

      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript1Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript2Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript3Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript4Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript5Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript6Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript7Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript8Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript9Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript10Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript11Cmb());
      initXFormScriptCmb(easyMovieMakerFrame.getSwfAnimatorXFormScript12Cmb());

      initSequenceOutputType(easyMovieMakerFrame.getSwfAnimatorOutputTypeCmb());

      tinaController.setInteractiveRendererCtrl(new TinaInteractiveRendererController(tinaController, pErrorHandler, pPrefs,
          interactiveRendererFrame.getInteractiveLoadFlameButton(), interactiveRendererFrame.getInteractiveLoadFlameFromClipboardButton(),
          interactiveRendererFrame.getInteractiveNextButton(), interactiveRendererFrame.getInteractiveStopButton(),
          interactiveRendererFrame.getInteractiveFlameToClipboardButton(), interactiveRendererFrame.getInteractiveSaveImageButton(),
          interactiveRendererFrame.getInteractiveSaveZBufferButton(), interactiveRendererFrame.getInteractiveAutoLoadImageCBx(),
          interactiveRendererFrame.getInteractiveSaveFlameButton(), interactiveRendererFrame.getInteractiveRandomStyleCmb(),
          interactiveRendererFrame.getInteractiveCenterTopPanel(), interactiveRendererFrame.getInteractiveStatsTextArea(),
          interactiveRendererFrame.getInteractiveHalveSizeButton(), interactiveRendererFrame.getInteractiveQuarterSizeButton(),
          interactiveRendererFrame.getInteractiveFullSizeButton(), interactiveRendererFrame.getInteractiveResolutionProfileCmb(),
          interactiveRendererFrame.getInteractivePauseButton(), interactiveRendererFrame.getInteractiveResumeButton(),
          interactiveRendererFrame.getInteractiveRendererShowStatsButton(), interactiveRendererFrame.getInteractiveRendererShowPreviewButton()));
      tinaController.getInteractiveRendererCtrl().enableControls();
      interactiveRendererFrame.getInteractiveAutoLoadImageCBx().setSelected(Prefs.getPrefs().isTinaAutoloadSavedImagesInIR());

      tinaController.setGpuRendererCtrl(new FlamesGPURenderController(tinaController, pErrorHandler, pPrefs,
          gpuRendererFrame.getInteractiveLoadFlameButton(), gpuRendererFrame.getInteractiveLoadFlameFromClipboardButton(),
          gpuRendererFrame.getInteractiveFlameToClipboardButton(), gpuRendererFrame.getInteractiveSaveImageButton(),
          gpuRendererFrame.getInteractiveSaveFlameButton(), gpuRendererFrame.getInteractiveFlameToEditorButton(),
          gpuRendererFrame.getInteractiveCenterTopPanel(), gpuRendererFrame.getInteractiveStatsTextArea(),
          gpuRendererFrame.getInteractiveHalveSizeButton(), gpuRendererFrame.getInteractiveQuarterSizeButton(),
          gpuRendererFrame.getInteractiveFullSizeButton(), gpuRendererFrame.getInteractiveResolutionProfileCmb(),
          gpuRendererFrame.getInteractiveQualityProfileCmb(), gpuRendererFrame.getLblGpuRenderInfo(), gpuRendererFrame.getPanel_32(),
          gpuRendererFrame.getInteractiveLoadFlameFromMainButton()));
      tinaController.getGpuRendererCtrl().enableControls();

      JComboBox[] globalScriptCmbArray = {
          easyMovieMakerFrame.getSwfAnimatorGlobalScript1Cmb(), easyMovieMakerFrame.getSwfAnimatorGlobalScript2Cmb(), easyMovieMakerFrame.getSwfAnimatorGlobalScript3Cmb(),
          easyMovieMakerFrame.getSwfAnimatorGlobalScript4Cmb(), easyMovieMakerFrame.getSwfAnimatorGlobalScript5Cmb(), easyMovieMakerFrame.getSwfAnimatorGlobalScript6Cmb(),
          easyMovieMakerFrame.getSwfAnimatorGlobalScript7Cmb(), easyMovieMakerFrame.getSwfAnimatorGlobalScript8Cmb(), easyMovieMakerFrame.getSwfAnimatorGlobalScript9Cmb(),
          easyMovieMakerFrame.getSwfAnimatorGlobalScript10Cmb(), easyMovieMakerFrame.getSwfAnimatorGlobalScript11Cmb(), easyMovieMakerFrame.getSwfAnimatorGlobalScript12Cmb() };
      JWFNumberField[] globalScriptREdArray = {
          easyMovieMakerFrame.getSwfAnimatorGlobalScript1REd(), easyMovieMakerFrame.getSwfAnimatorGlobalScript2REd(), easyMovieMakerFrame.getSwfAnimatorGlobalScript3REd(),
          easyMovieMakerFrame.getSwfAnimatorGlobalScript4REd(), easyMovieMakerFrame.getSwfAnimatorGlobalScript5REd(), easyMovieMakerFrame.getSwfAnimatorGlobalScript6REd(),
          easyMovieMakerFrame.getSwfAnimatorGlobalScript7REd(), easyMovieMakerFrame.getSwfAnimatorGlobalScript8REd(), easyMovieMakerFrame.getSwfAnimatorGlobalScript9REd(),
          easyMovieMakerFrame.getSwfAnimatorGlobalScript10REd(), easyMovieMakerFrame.getSwfAnimatorGlobalScript11REd(), easyMovieMakerFrame.getSwfAnimatorGlobalScript12REd() };
      JComboBox[] xFormScriptCmbArray = {
          easyMovieMakerFrame.getSwfAnimatorXFormScript1Cmb(), easyMovieMakerFrame.getSwfAnimatorXFormScript2Cmb(), easyMovieMakerFrame.getSwfAnimatorXFormScript3Cmb(),
          easyMovieMakerFrame.getSwfAnimatorXFormScript4Cmb(), easyMovieMakerFrame.getSwfAnimatorXFormScript5Cmb(), easyMovieMakerFrame.getSwfAnimatorXFormScript6Cmb(),
          easyMovieMakerFrame.getSwfAnimatorXFormScript7Cmb(), easyMovieMakerFrame.getSwfAnimatorXFormScript8Cmb(), easyMovieMakerFrame.getSwfAnimatorXFormScript9Cmb(),
          easyMovieMakerFrame.getSwfAnimatorXFormScript10Cmb(), easyMovieMakerFrame.getSwfAnimatorXFormScript11Cmb(), easyMovieMakerFrame.getSwfAnimatorXFormScript12Cmb() };
      JWFNumberField[] xFormScriptREdArray = {
          easyMovieMakerFrame.getSwfAnimatorXFormScript1REd(), easyMovieMakerFrame.getSwfAnimatorXFormScript2REd(), easyMovieMakerFrame.getSwfAnimatorXFormScript3REd(),
          easyMovieMakerFrame.getSwfAnimatorXFormScript4REd(), easyMovieMakerFrame.getSwfAnimatorXFormScript5REd(), easyMovieMakerFrame.getSwfAnimatorXFormScript6REd(),
          easyMovieMakerFrame.getSwfAnimatorXFormScript7REd(), easyMovieMakerFrame.getSwfAnimatorXFormScript8REd(), easyMovieMakerFrame.getSwfAnimatorXFormScript9REd(),
          easyMovieMakerFrame.getSwfAnimatorXFormScript10REd(), easyMovieMakerFrame.getSwfAnimatorXFormScript11REd(), easyMovieMakerFrame.getSwfAnimatorXFormScript12REd() };

      tinaController.setSwfAnimatorCtrl(new TinaSWFAnimatorController(tinaController, pErrorHandler, pPrefs,
          globalScriptCmbArray, globalScriptREdArray,
          xFormScriptCmbArray, xFormScriptREdArray,
          easyMovieMakerFrame.getSwfAnimatorFramesREd(), easyMovieMakerFrame.getSwfAnimatorFramesPerSecondREd(),
          easyMovieMakerFrame.getSwfAnimatorGenerateButton(), easyMovieMakerFrame.getSwfAnimatorResolutionProfileCmb(),
          easyMovieMakerFrame.getSwfAnimatorLoadFlameFromMainButton(),
          easyMovieMakerFrame.getSwfAnimatorLoadFlameFromClipboardButton(), easyMovieMakerFrame.getSwfAnimatorLoadFlameButton(),
          easyMovieMakerFrame.getSwfAnimatorProgressBar(), easyMovieMakerFrame.getSwfAnimatorCancelButton(),
          new SWFAnimatorProgressUpdater(easyMovieMakerFrame), easyMovieMakerFrame.getSwfAnimatorPreviewRootPanel(),
          easyMovieMakerFrame.getSwfAnimatorFrameSlider(), easyMovieMakerFrame.getSwfAnimatorFrameREd(),
          easyMovieMakerFrame.getSwfAnimatorFlamesPanel(), easyMovieMakerFrame.getSwfAnimatorFlamesButtonGroup(),
          easyMovieMakerFrame.getSwfAnimatorMoveUpButton(), easyMovieMakerFrame.getSwfAnimatorMoveDownButton(),
          easyMovieMakerFrame.getSwfAnimatorRemoveFlameButton(),
          easyMovieMakerFrame.getSwfAnimatorRemoveAllFlamesButton(), easyMovieMakerFrame.getSwfAnimatorMovieFromClipboardButton(),
          easyMovieMakerFrame.getSwfAnimatorMovieFromDiscButton(),
          easyMovieMakerFrame.getSwfAnimatorMovieToClipboardButton(), easyMovieMakerFrame.getSwfAnimatorMovieToDiscButton(),
          easyMovieMakerFrame.getSwfAnimatorFrameToEditorBtn(),
          easyMovieMakerFrame.getSwfAnimatorPlayButton(), easyMovieMakerFrame.getSwfAnimatorMotionBlurLengthREd(),
          easyMovieMakerFrame.getSwfAnimatorMotionBlurTimeStepREd(), easyMovieMakerFrame.getRandomMoviePanel(),
          easyMovieMakerFrame.getSwfAnimatorQualityProfileCmb(), easyMovieMakerFrame.getSwfAnimatorOutputTypeCmb()));
      tinaController.getSwfAnimatorCtrl().enableControls();
      tinaController.getSwfAnimatorCtrl().refreshControls();
      getToggleTriangleWithColorsButton().setSelected(pPrefs.isTinaEditorControlsWithColor());
      getRealtimePreviewToggleButton().setSelected(pPrefs.isTinaEditorProgressivePreview());

      tinaController.getJwfScriptController().refreshControls();

      tinaController.refreshMacroButtonsPanel();
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

  private void initMeshGenOutputTypeCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(MeshGenRenderOutputType.VOXELSTACK);
    pCmb.addItem(MeshGenRenderOutputType.POINTCLOUD);
    pCmb.setSelectedItem(MeshGenRenderOutputType.VOXELSTACK);
  }

  private void initDOFBlurShapeCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(DOFBlurShapeType.BUBBLE);
    pCmb.addItem(DOFBlurShapeType.CANNABISCURVE);
    pCmb.addItem(DOFBlurShapeType.CLOVERLEAF);
    pCmb.addItem(DOFBlurShapeType.FLOWER);
    pCmb.addItem(DOFBlurShapeType.HEART);
    pCmb.addItem(DOFBlurShapeType.NBLUR);
    pCmb.addItem(DOFBlurShapeType.PERLIN_NOISE);
    pCmb.addItem(DOFBlurShapeType.RECT);
    pCmb.addItem(DOFBlurShapeType.SINEBLUR);
    pCmb.addItem(DOFBlurShapeType.STARBLUR);
    pCmb.addItem(DOFBlurShapeType.TAURUS);
    pCmb.addItem(DOFBlurShapeType.SUB_FLAME);
    pCmb.setSelectedItem(DOFBlurShapeType.BUBBLE);
  }

  private void initBGColorTypoCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(BGColorType.SINGLE_COLOR);
    pCmb.addItem(BGColorType.GRADIENT_2X2);
    pCmb.addItem(BGColorType.GRADIENT_2X2_C);
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

  private void initSequenceOutputType(JComboBox pCmb) {
    pCmb.addItem(SequenceOutputType.FLAMES);
    pCmb.addItem(SequenceOutputType.PNG_IMAGES);
    pCmb.addItem(SequenceOutputType.ANB);
    pCmb.setSelectedItem(SequenceOutputType.FLAMES);
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

  private void initFilterTypeCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(FilteringType.GLOBAL_SHARPENING);
    pCmb.addItem(FilteringType.GLOBAL_SMOOTHING);
    pCmb.addItem(FilteringType.ADAPTIVE);
    pCmb.setSelectedItem(FilteringType.GLOBAL_SHARPENING);
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
    pCmb.addItem(FilterKernelType.SINEPOW5);
    pCmb.addItem(FilterKernelType.SINEPOW10);
    pCmb.addItem(FilterKernelType.SINEPOW15);
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
      tinaAddFinalTransformationButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

  private JLabel getAffineC00Lbl() {
    if (affineC00Lbl == null) {
      affineC00Lbl = new JLabel();
      affineC00Lbl.setName("affineC00Lbl");
      affineC00Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      affineC00Lbl.setText("X1");
      affineC00Lbl.setLocation(new Point(0, 6));
      affineC00Lbl.setSize(new Dimension(20, 22));
      affineC00Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affineC00Lbl.setPreferredSize(new Dimension(24, 22));
    }
    return affineC00Lbl;
  }
  
  private JLabel getAffineC01Lbl() {
	    if (affineC01Lbl == null) {
	        affineC01Lbl = new JLabel();
	        affineC01Lbl.setName("affineC01Lbl");
	        affineC01Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
	        affineC01Lbl.setText("X2");
	        affineC01Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
	        affineC01Lbl.setLocation(new Point(0, 30));
	        affineC01Lbl.setSize(new Dimension(20, 22));
	        affineC01Lbl.setPreferredSize(new Dimension(24, 22));
	    }
	    return affineC01Lbl;
	  }
	  
  private JLabel getAffineC10Lbl() {
	    if (affineC10Lbl == null) {
	        affineC10Lbl = new JLabel();
	        affineC10Lbl.setName("affineC10Lbl");
	        affineC10Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
	        affineC10Lbl.setText("Y1");
	        affineC10Lbl.setLocation(new Point(108, 6));
	        affineC10Lbl.setSize(new Dimension(20, 22));
	        affineC10Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
	        affineC10Lbl.setPreferredSize(new Dimension(24, 22));
	    }
	    return affineC10Lbl;
	  }
	  
  private JLabel getAffineC11Lbl() {
	    if (affineC11Lbl == null) {
	        affineC11Lbl = new JLabel();
	        affineC11Lbl.setName("affineC11Lbl");
	        affineC11Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
	        affineC11Lbl.setText("Y2");
	        affineC11Lbl.setLocation(new Point(108, 30));
	        affineC11Lbl.setSize(new Dimension(20, 22));
	        affineC11Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
	        affineC11Lbl.setPreferredSize(new Dimension(24, 22));
	    }
	    return affineC11Lbl;
	  }
	  
  private JLabel getAffineC20Lbl() {
	    if (affineC20Lbl == null) {
	        affineC20Lbl = new JLabel();
	        affineC20Lbl.setName("affineC20Lbl");
	        affineC20Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
	        affineC20Lbl.setText("O1");
	        affineC20Lbl.setLocation(new Point(216, 6));
	        affineC20Lbl.setSize(new Dimension(20, 22));
	        affineC20Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
	        affineC20Lbl.setPreferredSize(new Dimension(24, 22));
	    }
	    return affineC20Lbl;
	  }
	  
private JLabel getAffineC21Lbl() {
	    if (affineC21Lbl == null) {
	        affineC21Lbl = new JLabel();
	        affineC21Lbl.setName("affineC21Lbl");
	        affineC21Lbl.setHorizontalAlignment(SwingConstants.RIGHT);
	        affineC21Lbl.setText("O2");
	        affineC21Lbl.setLocation(new Point(216, 30));
	        affineC21Lbl.setSize(new Dimension(20, 22));
	        affineC21Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
	        affineC21Lbl.setPreferredSize(new Dimension(24, 22));
	    }
	    return affineC21Lbl;
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
      affineC00REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      affineC01REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      affineC10REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      affineC11REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      affineC20REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      affineC21REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      affineRotateLeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affineRotateLeftButton.setPreferredSize(new Dimension(55, 24));
      affineRotateLeftButton.setSize(new Dimension(70, 24));
      affineRotateLeftButton.setLocation(new Point(0, 57));
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
      affineRotateRightButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      affineEnlargeButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 8));
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
      affineShrinkButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 8));
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
      affineRotateAmountREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      transformationsNorthPanel.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      trnsformationsEastPanel.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      transformationsSplitPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      affineScaleAmountREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      affineMoveUpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      affineMoveDownButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      affineMoveLeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      affineMoveRightButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      affineMoveVertAmountREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      randomBatchButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      randomBatchButton.setText("Random batch");
      randomBatchButton.setPreferredSize(new Dimension(115, 46));
      randomBatchButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      randomBatchButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance((String) randomStyleCmb.getSelectedItem(), true);
          RandomSymmetryGenerator randSymmGen = RandomSymmetryGeneratorList.getRandomSymmetryGeneratorInstance((String) randomSymmetryCmb.getSelectedItem(), true);
          RandomGradientGenerator randGradientGen = RandomGradientGeneratorList.getRandomGradientGeneratorInstance((String) randomGradientCmb.getSelectedItem(), true);
          if (tinaController.createRandomBatch(-1, randGen, randSymmGen, randGradientGen, RandomBatchQuality.NORMAL)) {
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
      nonlinearVar1Cmb.setSize(new Dimension(108, 24));
      nonlinearVar1Cmb.setLocation(new Point(62, 2));
      nonlinearVar1Cmb.setMaximumRowCount(22);
      nonlinearVar1Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      nonlinearVar1REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams1Cmb.setSize(new Dimension(128, 24));
      nonlinearParams1Cmb.setLocation(new Point(62, 26));
      nonlinearParams1Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      nonlinearParams1REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
              java.awt.Desktop.getDesktop().browse(new URI("https://1drv.ms/x/s!AhabogcLehGXjHG9QNEcSPkfkkrq"));
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
      button.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-4-modified.png")));
      button.setText("");
      button.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      button.setBounds(269, 2, 22, 24);
      nonlinearVar1Panel.add(button);

      nonlinearParams1PreButton = new JToggleButton();
      nonlinearParams1PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams1PreButton.setText("");
      nonlinearParams1PreButton.setSize(new Dimension(22, 22));
      nonlinearParams1PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams1PreButton.setLocation(new Point(269, 26));
      nonlinearParams1PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams1PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams1PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams1PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(0);
        }
      });
      nonlinearVar1Panel.add(nonlinearParams1PreButton);

      nonlinearParams1PostButton = new JToggleButton();
      nonlinearParams1PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams1PostButton.setText("");
      nonlinearParams1PostButton.setSize(new Dimension(22, 22));
      nonlinearParams1PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams1PostButton.setLocation(new Point(168, 2));
      nonlinearParams1PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams1PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams1PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams1PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(0);
        }
      });
      nonlinearVar1Panel.add(nonlinearParams1PostButton);

      nonlinearParams1ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams1ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams1ToggleParamsPnlButton.setText("");
      nonlinearParams1ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams1ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams1ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams1ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams1ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams1ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams1ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(0);
        }
      });
      nonlinearVar1Panel.add(nonlinearParams1ToggleParamsPnlButton);
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
      nonlinearParams1LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams1LeftButton.setText("");
      nonlinearParams1LeftButton.setSize(new Dimension(22, 24));
      nonlinearParams1LeftButton.setLocation(new Point(269, 26));
      nonlinearParams1LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      nonlinearParams2Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams2Lbl.setText("Params");
      nonlinearParams2Lbl.setSize(new Dimension(38, 22));
      nonlinearParams2Lbl.setLocation(new Point(4, 26));
      nonlinearVar2Lbl = new JLabel();
      nonlinearVar2Lbl.setLocation(new Point(4, 2));
      nonlinearVar2Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar2Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar2Lbl.setText("Var 2");
      nonlinearVar2Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams2PreButton = new JToggleButton();
      nonlinearParams2PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams2PreButton.setText("");
      nonlinearParams2PreButton.setSize(new Dimension(22, 22));
      nonlinearParams2PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams2PreButton.setLocation(new Point(269, 26));
      nonlinearParams2PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams2PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams2PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams2PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(1);
        }
      });
      nonlinearVar2Panel.add(nonlinearParams2PreButton);

      nonlinearParams2PostButton = new JToggleButton();
      nonlinearParams2PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams2PostButton.setText("");
      nonlinearParams2PostButton.setSize(new Dimension(22, 22));
      nonlinearParams2PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams2PostButton.setLocation(new Point(168, 2));
      nonlinearParams2PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams2PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams2PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams2PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(1);
        }
      });
      nonlinearVar2Panel.add(nonlinearParams2PostButton);

      nonlinearParams2UpButton = new JButton();
      nonlinearParams2UpButton.setToolTipText("Move this variation up");
      nonlinearParams2UpButton.setText("");
      nonlinearParams2UpButton.setSize(new Dimension(22, 22));
      nonlinearParams2UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams2UpButton.setLocation(new Point(269, 26));
      nonlinearParams2UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams2UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams2UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams2UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(1);
        }
      });
      nonlinearVar2Panel.add(nonlinearParams2UpButton);

      nonlinearParams2ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams2ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams2ToggleParamsPnlButton.setText("");
      nonlinearParams2ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams2ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams2ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams2ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams2ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams2ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams2ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(1);
        }
      });
      nonlinearVar2Panel.add(nonlinearParams2ToggleParamsPnlButton);
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
      nonlinearVar2Cmb.setLocation(new Point(62, 2));
      nonlinearVar2Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar2Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar2Cmb.setMaximumRowCount(22);
      nonlinearVar2Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar2REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams2Cmb.setLocation(new Point(62, 26));
      nonlinearParams2Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams2Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams2Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams2REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams2LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams2LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams2LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams2LeftButton.setText("");
      nonlinearParams2LeftButton.setSize(new Dimension(22, 24));
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
      nonlinearParams3Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams3Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams3Lbl.setText("Params");
      nonlinearParams3Lbl.setSize(new Dimension(38, 22));
      nonlinearParams3Lbl.setLocation(new Point(4, 26));
      nonlinearVar3Lbl = new JLabel();
      nonlinearVar3Lbl.setLocation(new Point(4, 2));
      nonlinearVar3Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar3Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar3Lbl.setText("Var 3");
      nonlinearVar3Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams3PreButton = new JToggleButton();
      nonlinearParams3PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams3PreButton.setText("");
      nonlinearParams3PreButton.setSize(new Dimension(22, 22));
      nonlinearParams3PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams3PreButton.setLocation(new Point(269, 26));
      nonlinearParams3PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams3PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams3PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams3PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(2);
        }
      });
      nonlinearVar3Panel.add(nonlinearParams3PreButton);

      nonlinearParams3PostButton = new JToggleButton();
      nonlinearParams3PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams3PostButton.setText("");
      nonlinearParams3PostButton.setSize(new Dimension(22, 22));
      nonlinearParams3PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams3PostButton.setLocation(new Point(168, 2));
      nonlinearParams3PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams3PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams3PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams3PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(2);
        }
      });
      nonlinearVar3Panel.add(nonlinearParams3PostButton);

      nonlinearParams3UpButton = new JButton();
      nonlinearParams3UpButton.setToolTipText("Move this variation up");
      nonlinearParams3UpButton.setText("");
      nonlinearParams3UpButton.setSize(new Dimension(22, 22));
      nonlinearParams3UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams3UpButton.setLocation(new Point(269, 26));
      nonlinearParams3UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams3UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams3UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams3UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(2);
        }
      });
      nonlinearVar3Panel.add(nonlinearParams3UpButton);

      nonlinearParams3ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams3ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams3ToggleParamsPnlButton.setText("");
      nonlinearParams3ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams3ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams3ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams3ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams3ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams3ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams3ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(2);
        }
      });
      nonlinearVar3Panel.add(nonlinearParams3ToggleParamsPnlButton);
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
      nonlinearVar3Cmb.setLocation(new Point(62, 2));
      nonlinearVar3Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar3Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar3Cmb.setMaximumRowCount(22);
      nonlinearVar3Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar3REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams3Cmb.setLocation(new Point(62, 26));
      nonlinearParams3Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams3Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams3Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams3REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams3LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams3LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams3LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams3LeftButton.setText("");
      nonlinearParams3LeftButton.setSize(new Dimension(22, 24));
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
      xFormColorREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      xFormColorREd.setMotionPropertyName("color");
      xFormColorREd.setLinkedMotionControlName("xFormColorSlider");
      xFormColorREd.setLinkedLabelControlName("xFormColorLbl");
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
      xFormColorREd.setPreferredSize(new Dimension(70, 24));
      xFormColorREd.setText("");
      xFormColorREd.setSize(new Dimension(70, 24));
      xFormColorREd.setLocation(new Point(55, 21));
      xFormColorREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      xFormColorSlider.setName("xFormColorSlider");
      xFormColorSlider.setPreferredSize(new Dimension(195, 22));
      xFormColorSlider.setMaximum(100);
      xFormColorSlider.setMinimum(0);
      xFormColorSlider.setValue(0);
      xFormColorSlider.setSize(new Dimension(195, 22));
      xFormColorSlider.setLocation(new Point(125, 21));
      xFormColorSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      xFormSymmetryREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      xFormSymmetryREd.setMotionPropertyName("colorSymmetry");
      xFormSymmetryREd.setLinkedMotionControlName("xFormSymmetrySlider");
      xFormSymmetryREd.setLinkedLabelControlName("xFormSymmetryLbl");
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
      xFormSymmetryREd.setPreferredSize(new Dimension(55, 24));
      xFormSymmetryREd.setText("");
      xFormSymmetryREd.setSize(new Dimension(70, 24));
      xFormSymmetryREd.setLocation(new Point(55, 47));
      xFormSymmetryREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      xFormSymmetrySlider.setName("xFormSymmetrySlider");
      xFormSymmetrySlider.setPreferredSize(new Dimension(195, 22));
      xFormSymmetrySlider.setMaximum(100);
      xFormSymmetrySlider.setMinimum(-100);
      xFormSymmetrySlider.setValue(0);
      xFormSymmetrySlider.setLocation(new Point(125, 47));
      xFormSymmetrySlider.setSize(new Dimension(195, 22));
      xFormSymmetrySlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      xFormOpacityREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      xFormOpacityREd.setLinkedMotionControlName("xFormOpacitySlider");
      xFormOpacityREd.setLinkedLabelControlName("xFormOpacityLbl");
      xFormOpacityREd.setMotionPropertyName("opacity");
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
      xFormOpacityREd.setPreferredSize(new Dimension(55, 24));
      xFormOpacityREd.setText("");
      xFormOpacityREd.setSize(new Dimension(70, 24));
      xFormOpacityREd.setLocation(new Point(55, 101));
      xFormOpacityREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      xFormOpacitySlider.setName("xFormOpacitySlider");
      xFormOpacitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      xFormOpacitySlider.setPreferredSize(new Dimension(195, 22));
      xFormOpacitySlider.setMaximum(100);
      xFormOpacitySlider.setMinimum(0);
      xFormOpacitySlider.setValue(0);
      xFormOpacitySlider.setSize(new Dimension(195, 22));
      xFormOpacitySlider.setLocation(new Point(125, 101));
      xFormOpacitySlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      xFormDrawModeCmb.setLocation(new Point(122, 77));
      xFormDrawModeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      relWeightREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      relWeightREd.setBounds(6, 6, 81, 24);
      relWeightsEastPanel.add(relWeightREd);

      relWeightsResetButton = new JButton();
      relWeightsResetButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.saveUndoPoint();
          tinaController.relWeightsResetButton_clicked();
        }
      });
      relWeightsResetButton.setToolTipText("Reset all Xaos-values of the current transform to 1");
      relWeightsResetButton.setText("Reset");
      relWeightsResetButton.setPreferredSize(new Dimension(90, 24));
      relWeightsResetButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      relWeightsResetButton.setBounds(6, 90, 79, 24);
      relWeightsEastPanel.add(relWeightsResetButton);

      relWeightsResetAllButton = new JButton();
      relWeightsResetAllButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.saveUndoPoint();
          tinaController.relWeightsResetAllButton_clicked();
        }
      });
      relWeightsResetAllButton.setToolTipText("Reset all Xaos-values of all transforms to 1");
      relWeightsResetAllButton.setText("Reset all");
      relWeightsResetAllButton.setPreferredSize(new Dimension(90, 24));
      relWeightsResetAllButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      relWeightsResetAllButton.setBounds(6, 148, 79, 24);
      relWeightsEastPanel.add(relWeightsResetAllButton);
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
      relWeightsZeroButton.setLocation(new Point(6, 37));
      relWeightsZeroButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      relWeightsOneButton.setLocation(new Point(51, 37));
      relWeightsOneButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      relWeightsTable.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      newFlameButton.setPreferredSize(new Dimension(115, 52));
      newFlameButton.setMnemonic(KeyEvent.VK_N);
      newFlameButton.setText("New from scratch");
      newFlameButton.setActionCommand("New from scratch");
      newFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      newFlameButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/document-new-7.png")));
      newFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.newFlameButton_clicked();
        }
      });
    }
    return newFlameButton;
  }

  private JPanel getPreviewEastMainPanel() {
    if (previewEastMainPanel == null) {
      previewEastMainPanel = new JPanel();
      previewEastMainPanel.setPreferredSize(new Dimension(104, 0));
      previewEastMainPanel.setLayout(new BorderLayout(0, 0));

      previewEastDefaultPanel = new JPanel();
      previewEastDefaultPanel.setMaximumSize(new Dimension(52, 32767));
      previewEastDefaultPanel.setMinimumSize(new Dimension(52, 10));
      previewEastDefaultPanel.setPreferredSize(new Dimension(52, 10));
      FlowLayout fl_previewEastDefaultPanel = (FlowLayout) previewEastDefaultPanel.getLayout();
      fl_previewEastDefaultPanel.setVgap(1);
      previewEastMainPanel.add(previewEastDefaultPanel, BorderLayout.WEST);

      mouseTransformEditViewButton = new JToggleButton();
      previewEastDefaultPanel.add(mouseTransformEditViewButton);
      mouseTransformEditViewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformViewButton_clicked();
        }
      });
      mouseTransformEditViewButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/modify_view.png")));
      mouseTransformEditViewButton.setToolTipText("Enable view editing mode (Left mouse: move, right mouse: rotate, middle/wheel: zoom)");
      mouseTransformEditViewButton.setPreferredSize(new Dimension(42, 24));
      previewEastDefaultPanel.add(getMouseTransformMoveTrianglesButton());

      mouseTransformRotateTrianglesButton = new JToggleButton();
      previewEastDefaultPanel.add(mouseTransformRotateTrianglesButton);
      mouseTransformRotateTrianglesButton.setToolTipText("Rotate triangles using the left mouse button");
      mouseTransformRotateTrianglesButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformRotateTrianglesButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/object-rotate-right-3.png")));

      mouseTransformScaleTrianglesButton = new JToggleButton();
      previewEastDefaultPanel.add(mouseTransformScaleTrianglesButton);
      mouseTransformScaleTrianglesButton.setToolTipText("Scale triangles using the left mouse button");
      mouseTransformScaleTrianglesButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformScaleTrianglesButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-inout.png")));

      mouseTransformEditPointsButton = new JToggleButton();
      previewEastDefaultPanel.add(mouseTransformEditPointsButton);
      mouseTransformEditPointsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformEditPointsButton_clicked();
        }
      });
      mouseTransformEditPointsButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle_points.png")));
      mouseTransformEditPointsButton.setToolTipText("Enable free triangle editing mode");
      mouseTransformEditPointsButton.setPreferredSize(new Dimension(42, 24));

      mouseTransformEditTriangleViewButton = new JToggleButton();
      previewEastDefaultPanel.add(mouseTransformEditTriangleViewButton);
      mouseTransformEditTriangleViewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformTriangleViewButton_clicked();
        }
      });
      mouseTransformEditTriangleViewButton.setToolTipText("Edit the view of the controls (drag mouse to move, mouse-wheel or hold <Alt> to zoom)");
      mouseTransformEditTriangleViewButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformEditTriangleViewButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle_view.png")));
      previewEastDefaultPanel.add(getMouseTransformEditFocusPointButton());
      previewEastDefaultPanel.add(getLabel_1());
      previewEastDefaultPanel.add(getAffineXYEditPlaneToggleBtn());
      previewEastDefaultPanel.add(getAffineYZEditPlaneToggleBtn());
      previewEastDefaultPanel.add(getAffineZXEditPlaneToggleBtn());
      previewEastDefaultPanel.add(getLabel_3());
      previewEastDefaultPanel.add(getToggleDetachedPreviewButton());
      previewEastDefaultPanel.add(getTinaRenderFlameButton());
      previewEastMainPanel.add(getMacroButtonRootPanel(), BorderLayout.CENTER);
      mouseTransformScaleTrianglesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformScaleTrianglesButton_clicked();
        }
      });
      mouseTransformRotateTrianglesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(false);
          tinaController.mouseTransformRotateTrianglesButton_clicked();
        }
      });
    }
    return previewEastMainPanel;
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
      mouseTransformMoveTrianglesButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle.png")));
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

      toggleDrawGridButton = new JToggleButton();
      toggleDrawGridButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          if (e.getClickCount() == 2) {
            tinaController.resetGridToDefaults();
          }
        }
      });
      toggleDrawGridButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.toggleDrawGridButton_clicked();
        }
      });
      toggleDrawGridButton.setToolTipText("Turn grid on/off, double-click to rest grid-size and -position");
      toggleDrawGridButton.setSize(new Dimension(95, 24));
      toggleDrawGridButton.setSelected(false);
      toggleDrawGridButton.setPreferredSize(new Dimension(42, 24));
      toggleDrawGridButton.setLocation(new Point(4, 4));
      toggleDrawGridButton.setBounds(451, 4, 42, 24);
      toggleDrawGridButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/grid.png")));
      centerNorthPanel.add(toggleDrawGridButton);
      centerNorthPanel.add(getAffineEditPostTransformSmallButton());

      toggleTriangleWithColorsButton = new JToggleButton();
      toggleTriangleWithColorsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.toggleTriangleWithColorsButton_clicked();
        }
      });
      toggleTriangleWithColorsButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/atomic.png")));
      toggleTriangleWithColorsButton.setToolTipText("Toggle monochrome/colored controls");
      toggleTriangleWithColorsButton.setPreferredSize(new Dimension(42, 24));
      toggleTriangleWithColorsButton.setMnemonic(KeyEvent.VK_P);
      toggleTriangleWithColorsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      toggleTriangleWithColorsButton.setBounds(605, 4, 42, 24);
      centerNorthPanel.add(toggleTriangleWithColorsButton);

      triangleStyleCmb = new JComboBox();
      triangleStyleCmb.setPreferredSize(new Dimension(125, 24));
      triangleStyleCmb.setMinimumSize(new Dimension(100, 24));
      triangleStyleCmb.setMaximumSize(new Dimension(32767, 24));
      triangleStyleCmb.setMaximumRowCount(32);
      triangleStyleCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      triangleStyleCmb.setBounds(537, 4, 68, 24);
      triangleStyleCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.triangleStyleCmb_changed();
          }
        }
      });

      centerNorthPanel.add(triangleStyleCmb);

      toggleDrawGuidesButton = new JToggleButton();
      toggleDrawGuidesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.toggleDrawGuidesButton_clicked();
        }
      });
      toggleDrawGuidesButton.setToolTipText("Turn guides (center point, rule of thirds and the golden ratio) on/off");
      toggleDrawGuidesButton.setSize(new Dimension(95, 24));
      toggleDrawGuidesButton.setSelected(false);
      toggleDrawGuidesButton.setPreferredSize(new Dimension(42, 24));
      toggleDrawGuidesButton.setLocation(new Point(4, 4));
      toggleDrawGuidesButton.setBounds(494, 4, 42, 24);
      toggleDrawGuidesButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/guides.png")));
      centerNorthPanel.add(toggleDrawGuidesButton);

      realtimePreviewToggleButton = new JToggleButton();
      realtimePreviewToggleButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.realtimePreviewToggleButton_clicked();
        }
      });
      realtimePreviewToggleButton.setToolTipText("Turn realtime (progressive) preview on/off");
      realtimePreviewToggleButton.setText("Realtime");
      realtimePreviewToggleButton.setSize(new Dimension(68, 24));
      realtimePreviewToggleButton.setSelected(false);
      realtimePreviewToggleButton.setPreferredSize(new Dimension(42, 24));
      realtimePreviewToggleButton.setLocation(new Point(402, 4));
      realtimePreviewToggleButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      realtimePreviewToggleButton.setBounds(181, 4, 90, 24);
      realtimePreviewToggleButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/fraqtive3.png")));

      centerNorthPanel.add(realtimePreviewToggleButton);
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
      toggleVariationsButton.setBounds(3, 4, 42, 24);
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
      centerWestPanel.add(getSendFlameToIRButton());
      centerWestPanel.add(getTinaAppendToMovieButton());
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
      motionCurveEditModeButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      motionCurveEditModeButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/video-x-generic-2.png")));
      centerWestPanel.add(getSolidRenderingToggleBtn());
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
      centerCenterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      centerDescLabel = new JTextArea();
      centerDescLabel.setEditable(false);
      centerCenterPanel.add(centerDescLabel, BorderLayout.NORTH);
      centerDescLabel.setText("\r\nWelcome to JWildfire!\r\n\r\nTo get started just double-click (or right-click) on a thumbnail at the left to load it into main editor.\r\n\r\nHappy fractalin'!\r\n\r\nVisit the official forum at http://jwildfire.org/forum/");
      centerDescLabel.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      centerCenterPanel.add(getGradientEditorFncPnl(), BorderLayout.SOUTH);
      centerCenterPanel.add(getMainPrevievPnl(), BorderLayout.CENTER);
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
      randomStyleCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      randomStyleCmb.setMaximumRowCount(48);
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
      affineEditPostTransformButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affineEditPostTransformButton.setLocation(new Point(0, 155));
      affineEditPostTransformButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle_post.png")));
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
      affineEditPostTransformSmallButton.setBounds(647, 4, 42, 24);
      affineEditPostTransformSmallButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/edit_triangle_post.png")));
      affineEditPostTransformSmallButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

  public JProgressBar getRenderProgressBar() {
    if (renderProgressBar == null) {
      renderProgressBar = new JProgressBar();
      renderProgressBar.setValue(0);
      renderProgressBar.setSize(new Dimension(175, 14));
      renderProgressBar.setLocation(new Point(273, 9));
      renderProgressBar.setPreferredSize(new Dimension(169, 14));
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
      affineResetTransformButton.setToolTipText("Reset affine transform to defaults");
      affineResetTransformButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affineResetTransformButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
      affineResetTransformButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.affineResetTransformButton_clicked();
        }
      });
    }
    return affineResetTransformButton;
  }

  private boolean defaultMirrorPrePostTranslations = false;
  private JToggleButton affineMirrorPrePostTranslationsButton;

  private JToggleButton getAffineMirrorPrePostTranslationsButton() {
    if (affineMirrorPrePostTranslationsButton == null) {
      affineMirrorPrePostTranslationsButton = new JToggleButton();
      affineMirrorPrePostTranslationsButton.setSelected(defaultMirrorPrePostTranslations);
      affineMirrorPrePostTranslationsButton.setPreferredSize(new Dimension(136, 24));
      affineMirrorPrePostTranslationsButton.setText("Mirror Translations");
      affineMirrorPrePostTranslationsButton.setLocation(new Point(109, 185));
      affineMirrorPrePostTranslationsButton.setSize(new Dimension(104, 24));
      affineMirrorPrePostTranslationsButton.setToolTipText("EXPERIMENTAL: Mirror pre- and post- translations (post reverses pre)");
      affineMirrorPrePostTranslationsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affineMirrorPrePostTranslationsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.affineMirrorPrePostChanged(affineMirrorPrePostTranslationsButton.isSelected());
        }
      });
    }
    return affineMirrorPrePostTranslationsButton;
  }

  /**
   * This method initializes nonlinearVar4Panel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNonlinearVar4Panel() {
    if (nonlinearVar4Panel == null) {
      nonlinearParams4Lbl = new JLabel();
      nonlinearParams4Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams4Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams4Lbl.setText("Params");
      nonlinearParams4Lbl.setSize(new Dimension(38, 22));
      nonlinearParams4Lbl.setLocation(new Point(4, 26));
      nonlinearVar4Lbl = new JLabel();
      nonlinearVar4Lbl.setLocation(new Point(4, 2));
      nonlinearVar4Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar4Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar4Lbl.setText("Var 4");
      nonlinearVar4Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams4PreButton = new JToggleButton();
      nonlinearParams4PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams4PreButton.setText("");
      nonlinearParams4PreButton.setSize(new Dimension(22, 22));
      nonlinearParams4PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams4PreButton.setLocation(new Point(269, 26));
      nonlinearParams4PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams4PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams4PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams4PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(3);
        }
      });
      nonlinearVar4Panel.add(nonlinearParams4PreButton);

      nonlinearParams4PostButton = new JToggleButton();
      nonlinearParams4PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams4PostButton.setText("");
      nonlinearParams4PostButton.setSize(new Dimension(22, 22));
      nonlinearParams4PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams4PostButton.setLocation(new Point(168, 2));
      nonlinearParams4PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams4PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams4PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams4PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(3);
        }
      });
      nonlinearVar4Panel.add(nonlinearParams4PostButton);

      nonlinearParams4UpButton = new JButton();
      nonlinearParams4UpButton.setToolTipText("Move this variation up");
      nonlinearParams4UpButton.setText("");
      nonlinearParams4UpButton.setSize(new Dimension(22, 22));
      nonlinearParams4UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams4UpButton.setLocation(new Point(269, 26));
      nonlinearParams4UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams4UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams4UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams4UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(3);
        }
      });
      nonlinearVar4Panel.add(nonlinearParams4UpButton);

      nonlinearParams4ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams4ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams4ToggleParamsPnlButton.setText("");
      nonlinearParams4ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams4ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams4ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams4ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams4ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams4ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams4ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(3);
        }
      });
      nonlinearVar4Panel.add(nonlinearParams4ToggleParamsPnlButton);

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
      nonlinearVar4Cmb.setLocation(new Point(62, 2));
      nonlinearVar4Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar4Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar4Cmb.setMaximumRowCount(22);
      nonlinearVar4Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar4REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams4Cmb.setLocation(new Point(62, 26));
      nonlinearParams4Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams4Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams4Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams4REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams4LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams4LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams4LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams4LeftButton.setText("");
      nonlinearParams4LeftButton.setSize(new Dimension(22, 24));
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
      createPaletteColorsTable.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearScrollPane.setViewportBorder(null);
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
      nonlinearParams5Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams5Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams5Lbl.setText("Params");
      nonlinearParams5Lbl.setSize(new Dimension(38, 22));
      nonlinearParams5Lbl.setLocation(new Point(4, 26));
      nonlinearVar5Lbl = new JLabel();
      nonlinearVar5Lbl.setLocation(new Point(4, 2));
      nonlinearVar5Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar5Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar5Lbl.setText("Var 5");
      nonlinearVar5Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams5PreButton = new JToggleButton();
      nonlinearParams5PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams5PreButton.setText("");
      nonlinearParams5PreButton.setSize(new Dimension(22, 22));
      nonlinearParams5PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams5PreButton.setLocation(new Point(269, 26));
      nonlinearParams5PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams5PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams5PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams5PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(4);
        }
      });
      nonlinearVar5Panel.add(nonlinearParams5PreButton);

      nonlinearParams5PostButton = new JToggleButton();
      nonlinearParams5PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams5PostButton.setText("");
      nonlinearParams5PostButton.setSize(new Dimension(22, 22));
      nonlinearParams5PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams5PostButton.setLocation(new Point(168, 2));
      nonlinearParams5PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams5PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams5PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams5PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(4);
        }
      });
      nonlinearVar5Panel.add(nonlinearParams5PostButton);

      nonlinearParams5UpButton = new JButton();
      nonlinearParams5UpButton.setToolTipText("Move this variation up");
      nonlinearParams5UpButton.setText("");
      nonlinearParams5UpButton.setSize(new Dimension(22, 22));
      nonlinearParams5UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams5UpButton.setLocation(new Point(269, 26));
      nonlinearParams5UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams5UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams5UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams5UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(4);
        }
      });
      nonlinearVar5Panel.add(nonlinearParams5UpButton);

      nonlinearParams5ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams5ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams5ToggleParamsPnlButton.setText("");
      nonlinearParams5ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams5ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams5ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams5ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams5ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams5ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams5ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(4);
        }
      });
      nonlinearVar5Panel.add(nonlinearParams5ToggleParamsPnlButton);
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
      nonlinearVar5Cmb.setLocation(new Point(62, 2));
      nonlinearVar5Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar5Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar5Cmb.setMaximumRowCount(22);
      nonlinearVar5Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar5REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams5Cmb.setLocation(new Point(62, 26));
      nonlinearParams5Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams5Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams5Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams5REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams5LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams5LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams5LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams5LeftButton.setText("");
      nonlinearParams5LeftButton.setSize(new Dimension(22, 24));
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
      nonlinearParams6Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams6Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams6Lbl.setText("Params");
      nonlinearParams6Lbl.setSize(new Dimension(38, 22));
      nonlinearParams6Lbl.setLocation(new Point(4, 26));
      nonlinearVar6Lbl = new JLabel();
      nonlinearVar6Lbl.setLocation(new Point(4, 2));
      nonlinearVar6Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar6Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar6Lbl.setText("Var 6");
      nonlinearVar6Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams6PreButton = new JToggleButton();
      nonlinearParams6PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams6PreButton.setText("");
      nonlinearParams6PreButton.setSize(new Dimension(22, 22));
      nonlinearParams6PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams6PreButton.setLocation(new Point(269, 26));
      nonlinearParams6PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams6PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams6PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams6PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(5);
        }
      });
      nonlinearVar6Panel.add(nonlinearParams6PreButton);

      nonlinearParams6PostButton = new JToggleButton();
      nonlinearParams6PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams6PostButton.setText("");
      nonlinearParams6PostButton.setSize(new Dimension(22, 22));
      nonlinearParams6PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams6PostButton.setLocation(new Point(168, 2));
      nonlinearParams6PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams6PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams6PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams6PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(5);
        }
      });
      nonlinearVar6Panel.add(nonlinearParams6PostButton);

      nonlinearParams6UpButton = new JButton();
      nonlinearParams6UpButton.setToolTipText("Move this variation up");
      nonlinearParams6UpButton.setText("");
      nonlinearParams6UpButton.setSize(new Dimension(22, 22));
      nonlinearParams6UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams6UpButton.setLocation(new Point(269, 26));
      nonlinearParams6UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams6UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams6UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams6UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(5);
        }
      });
      nonlinearVar6Panel.add(nonlinearParams6UpButton);

      nonlinearParams6ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams6ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams6ToggleParamsPnlButton.setText("");
      nonlinearParams6ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams6ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams6ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams6ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams6ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams6ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams6ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(5);
        }
      });
      nonlinearVar6Panel.add(nonlinearParams6ToggleParamsPnlButton);
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
      nonlinearVar6Cmb.setLocation(new Point(62, 2));
      nonlinearVar6Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar6Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar6Cmb.setMaximumRowCount(22);
      nonlinearVar6Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar6REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams6Cmb.setLocation(new Point(62, 26));
      nonlinearParams6Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams6Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams6Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams6REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams6LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams6LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams6LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams6LeftButton.setText("");
      nonlinearParams6LeftButton.setSize(new Dimension(22, 24));
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
      nonlinearParams7Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams7Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams7Lbl.setText("Params");
      nonlinearParams7Lbl.setSize(new Dimension(38, 22));
      nonlinearParams7Lbl.setLocation(new Point(4, 26));
      nonlinearVar7Lbl = new JLabel();
      nonlinearVar7Lbl.setLocation(new Point(4, 2));
      nonlinearVar7Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar7Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar7Lbl.setText("Var 7");
      nonlinearVar7Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams7PreButton = new JToggleButton();
      nonlinearParams7PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams7PreButton.setText("");
      nonlinearParams7PreButton.setSize(new Dimension(22, 22));
      nonlinearParams7PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams7PreButton.setLocation(new Point(269, 26));
      nonlinearParams7PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams7PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams7PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams7PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(6);
        }
      });
      nonlinearVar7Panel.add(nonlinearParams7PreButton);

      nonlinearParams7PostButton = new JToggleButton();
      nonlinearParams7PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams7PostButton.setText("");
      nonlinearParams7PostButton.setSize(new Dimension(22, 22));
      nonlinearParams7PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams7PostButton.setLocation(new Point(168, 2));
      nonlinearParams7PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams7PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams7PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams7PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(6);
        }
      });
      nonlinearVar7Panel.add(nonlinearParams7PostButton);

      nonlinearParams7UpButton = new JButton();
      nonlinearParams7UpButton.setToolTipText("Move this variation up");
      nonlinearParams7UpButton.setText("");
      nonlinearParams7UpButton.setSize(new Dimension(22, 22));
      nonlinearParams7UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams7UpButton.setLocation(new Point(269, 26));
      nonlinearParams7UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams7UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams7UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams7UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(6);
        }
      });
      nonlinearVar7Panel.add(nonlinearParams7UpButton);

      nonlinearParams7ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams7ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams7ToggleParamsPnlButton.setText("");
      nonlinearParams7ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams7ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams7ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams7ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams7ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams7ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams7ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(6);
        }
      });
      nonlinearVar7Panel.add(nonlinearParams7ToggleParamsPnlButton);

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
      nonlinearVar7Cmb.setLocation(new Point(62, 2));
      nonlinearVar7Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar7Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar7Cmb.setMaximumRowCount(22);
      nonlinearVar7Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar7REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams7Cmb.setLocation(new Point(62, 26));
      nonlinearParams7Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams7Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams7Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams7REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams7LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams7LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams7LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams7LeftButton.setText("");
      nonlinearParams7LeftButton.setSize(new Dimension(22, 24));
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
      nonlinearParams8Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams8Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams8Lbl.setText("Params");
      nonlinearParams8Lbl.setSize(new Dimension(38, 22));
      nonlinearParams8Lbl.setLocation(new Point(4, 26));
      nonlinearVar8Lbl = new JLabel();
      nonlinearVar8Lbl.setLocation(new Point(4, 2));
      nonlinearVar8Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar8Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar8Lbl.setText("Var 8");
      nonlinearVar8Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams8PreButton = new JToggleButton();
      nonlinearParams8PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams8PreButton.setText("");
      nonlinearParams8PreButton.setSize(new Dimension(22, 22));
      nonlinearParams8PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams8PreButton.setLocation(new Point(269, 26));
      nonlinearParams8PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams8PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams8PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams8PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(7);
        }
      });
      nonlinearVar8Panel.add(nonlinearParams8PreButton);

      nonlinearParams8PostButton = new JToggleButton();
      nonlinearParams8PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams8PostButton.setText("");
      nonlinearParams8PostButton.setSize(new Dimension(22, 22));
      nonlinearParams8PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams8PostButton.setLocation(new Point(168, 2));
      nonlinearParams8PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams8PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams8PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams8PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(7);
        }
      });
      nonlinearVar8Panel.add(nonlinearParams8PostButton);

      nonlinearParams8UpButton = new JButton();
      nonlinearParams8UpButton.setToolTipText("Move this variation up");
      nonlinearParams8UpButton.setText("");
      nonlinearParams8UpButton.setSize(new Dimension(22, 22));
      nonlinearParams8UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams8UpButton.setLocation(new Point(269, 26));
      nonlinearParams8UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams8UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams8UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams8UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(7);
        }
      });
      nonlinearVar8Panel.add(nonlinearParams8UpButton);

      nonlinearParams8ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams8ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams8ToggleParamsPnlButton.setText("");
      nonlinearParams8ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams8ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams8ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams8ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams8ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams8ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams8ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(7);
        }
      });
      nonlinearVar8Panel.add(nonlinearParams8ToggleParamsPnlButton);

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
      nonlinearVar8Cmb.setLocation(new Point(62, 2));
      nonlinearVar8Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar8Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar8Cmb.setMaximumRowCount(22);
      nonlinearVar8Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar8REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams8Cmb.setLocation(new Point(62, 26));
      nonlinearParams8Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams8Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams8Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams8REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams8LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams8LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams8LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams8LeftButton.setText("");
      nonlinearParams8LeftButton.setSize(new Dimension(22, 24));
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
      nonlinearParams9Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams9Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams9Lbl.setText("Params");
      nonlinearParams9Lbl.setSize(new Dimension(38, 22));
      nonlinearParams9Lbl.setLocation(new Point(4, 26));
      nonlinearVar9Lbl = new JLabel();
      nonlinearVar9Lbl.setLocation(new Point(4, 2));
      nonlinearVar9Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar9Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar9Lbl.setText("Var 9");
      nonlinearVar9Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams9PreButton = new JToggleButton();
      nonlinearParams9PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams9PreButton.setText("");
      nonlinearParams9PreButton.setSize(new Dimension(22, 22));
      nonlinearParams9PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams9PreButton.setLocation(new Point(269, 26));
      nonlinearParams9PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams9PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams9PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams9PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(8);
        }
      });
      nonlinearVar9Panel.add(nonlinearParams9PreButton);

      nonlinearParams9PostButton = new JToggleButton();
      nonlinearParams9PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams9PostButton.setText("");
      nonlinearParams9PostButton.setSize(new Dimension(22, 22));
      nonlinearParams9PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams9PostButton.setLocation(new Point(168, 2));
      nonlinearParams9PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams9PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams9PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams9PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(9);
        }
      });
      nonlinearVar9Panel.add(nonlinearParams9PostButton);

      nonlinearParams9UpButton = new JButton();
      nonlinearParams9UpButton.setToolTipText("Move this variation up");
      nonlinearParams9UpButton.setText("");
      nonlinearParams9UpButton.setSize(new Dimension(22, 22));
      nonlinearParams9UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams9UpButton.setLocation(new Point(269, 26));
      nonlinearParams9UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams9UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams9UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams9UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(8);
        }
      });
      nonlinearVar9Panel.add(nonlinearParams9UpButton);

      nonlinearParams9ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams9ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams9ToggleParamsPnlButton.setText("");
      nonlinearParams9ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams9ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams9ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams9ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams9ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams9ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams9ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(8);
        }
      });
      nonlinearVar9Panel.add(nonlinearParams9ToggleParamsPnlButton);

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
      nonlinearVar9Cmb.setLocation(new Point(62, 2));
      nonlinearVar9Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar9Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar9Cmb.setMaximumRowCount(22);
      nonlinearVar9Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar9REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams9Cmb.setLocation(new Point(62, 26));
      nonlinearParams9Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams9Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams9Cmb.setSize(new Dimension(129, 24));
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
      nonlinearParams9REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams9LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams9LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams9LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams9LeftButton.setText("");
      nonlinearParams9LeftButton.setSize(new Dimension(22, 24));
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
      nonlinearParams10Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams10Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams10Lbl.setText("Params");
      nonlinearParams10Lbl.setSize(new Dimension(38, 22));
      nonlinearParams10Lbl.setLocation(new Point(4, 26));
      nonlinearVar10Lbl = new JLabel();
      nonlinearVar10Lbl.setLocation(new Point(4, 2));
      nonlinearVar10Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar10Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar10Lbl.setText("Var 10");
      nonlinearVar10Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams10PreButton = new JToggleButton();
      nonlinearParams10PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams10PreButton.setText("");
      nonlinearParams10PreButton.setSize(new Dimension(22, 22));
      nonlinearParams10PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams10PreButton.setLocation(new Point(269, 26));
      nonlinearParams10PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams10PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams10PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams10PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(9);
        }
      });
      nonlinearVar10Panel.add(nonlinearParams10PreButton);

      nonlinearParams10PostButton = new JToggleButton();
      nonlinearParams10PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams10PostButton.setText("");
      nonlinearParams10PostButton.setSize(new Dimension(22, 22));
      nonlinearParams10PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams10PostButton.setLocation(new Point(168, 2));
      nonlinearParams10PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams10PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams10PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams10PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(9);
        }
      });
      nonlinearVar10Panel.add(nonlinearParams10PostButton);

      nonlinearParams10UpButton = new JButton();
      nonlinearParams10UpButton.setToolTipText("Move this variation up");
      nonlinearParams10UpButton.setText("");
      nonlinearParams10UpButton.setSize(new Dimension(22, 22));
      nonlinearParams10UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams10UpButton.setLocation(new Point(269, 26));
      nonlinearParams10UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams10UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams10UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams10UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(9);
        }
      });
      nonlinearVar10Panel.add(nonlinearParams10UpButton);

      nonlinearParams10ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams10ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams10ToggleParamsPnlButton.setText("");
      nonlinearParams10ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams10ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams10ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams10ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams10ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams10ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams10ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(9);
        }
      });
      nonlinearVar10Panel.add(nonlinearParams10ToggleParamsPnlButton);
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
      nonlinearVar10Cmb.setLocation(new Point(62, 2));
      nonlinearVar10Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar10Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar10Cmb.setMaximumRowCount(22);
      nonlinearVar10Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar10REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams10Cmb.setLocation(new Point(62, 26));
      nonlinearParams10Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams10Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams10Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams10REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams10LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams10LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams10LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams10LeftButton.setText("");
      nonlinearParams10LeftButton.setSize(new Dimension(22, 24));
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
      nonlinearParams11Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams11Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams11Lbl.setText("Params");
      nonlinearParams11Lbl.setSize(new Dimension(38, 22));
      nonlinearParams11Lbl.setLocation(new Point(4, 26));
      nonlinearVar11Lbl = new JLabel();
      nonlinearVar11Lbl.setLocation(new Point(4, 2));
      nonlinearVar11Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar11Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar11Lbl.setText("Var 11");
      nonlinearVar11Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams11PreButton = new JToggleButton();
      nonlinearParams11PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams11PreButton.setText("");
      nonlinearParams11PreButton.setSize(new Dimension(22, 22));
      nonlinearParams11PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams11PreButton.setLocation(new Point(269, 26));
      nonlinearParams11PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams11PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams11PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams11PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(10);
        }
      });
      nonlinearVar11Panel.add(nonlinearParams11PreButton);

      nonlinearParams11PostButton = new JToggleButton();
      nonlinearParams11PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams11PostButton.setText("");
      nonlinearParams11PostButton.setSize(new Dimension(22, 22));
      nonlinearParams11PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams11PostButton.setLocation(new Point(168, 2));
      nonlinearParams11PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams11PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams11PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams11PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(10);
        }
      });
      nonlinearVar11Panel.add(nonlinearParams11PostButton);

      nonlinearParams11UpButton = new JButton();
      nonlinearParams11UpButton.setToolTipText("Move this variation up");
      nonlinearParams11UpButton.setText("");
      nonlinearParams11UpButton.setSize(new Dimension(22, 22));
      nonlinearParams11UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams11UpButton.setLocation(new Point(269, 26));
      nonlinearParams11UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams11UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams11UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams11UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(10);
        }
      });
      nonlinearVar11Panel.add(nonlinearParams11UpButton);

      nonlinearParams11ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams11ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams11ToggleParamsPnlButton.setText("");
      nonlinearParams11ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams11ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams11ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams11ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams11ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams11ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams11ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(10);
        }
      });
      nonlinearVar11Panel.add(nonlinearParams11ToggleParamsPnlButton);

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
      nonlinearVar11Cmb.setLocation(new Point(62, 2));
      nonlinearVar11Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar11Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar11Cmb.setMaximumRowCount(22);
      nonlinearVar11Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar11REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams11Cmb.setLocation(new Point(62, 26));
      nonlinearParams11Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams11Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams11Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams11REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams11LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams11LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams11LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams11LeftButton.setText("");
      nonlinearParams11LeftButton.setSize(new Dimension(22, 24));
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
      nonlinearParams12Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams12Lbl.setPreferredSize(new Dimension(50, 22));
      nonlinearParams12Lbl.setText("Params");
      nonlinearParams12Lbl.setSize(new Dimension(38, 22));
      nonlinearParams12Lbl.setLocation(new Point(4, 26));
      nonlinearVar12Lbl = new JLabel();
      nonlinearVar12Lbl.setLocation(new Point(4, 2));
      nonlinearVar12Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar12Lbl.setPreferredSize(new Dimension(60, 22));
      nonlinearVar12Lbl.setText("Var 12");
      nonlinearVar12Lbl.setSize(new Dimension(38, 22));
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

      nonlinearParams12PreButton = new JToggleButton();
      nonlinearParams12PreButton.setToolTipText("Make the current variation a \"pre_\"-variation");
      nonlinearParams12PreButton.setText("");
      nonlinearParams12PreButton.setSize(new Dimension(22, 22));
      nonlinearParams12PreButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams12PreButton.setLocation(new Point(269, 26));
      nonlinearParams12PreButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams12PreButton.setBounds(42, 2, 22, 24);
      nonlinearParams12PreButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_pre.png")));
      nonlinearParams12PreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPreButtonClicked(11);
        }
      });
      nonlinearVar12Panel.add(nonlinearParams12PreButton);

      nonlinearParams12PostButton = new JToggleButton();
      nonlinearParams12PostButton.setToolTipText("Make the current variation a \"post_\"-variation");
      nonlinearParams12PostButton.setText("");
      nonlinearParams12PostButton.setSize(new Dimension(22, 22));
      nonlinearParams12PostButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams12PostButton.setLocation(new Point(168, 2));
      nonlinearParams12PostButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams12PostButton.setBounds(168, 2, 22, 24);
      nonlinearParams12PostButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/code-block_post.png")));
      nonlinearParams12PostButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsPostButtonClicked(11);
        }
      });
      nonlinearVar12Panel.add(nonlinearParams12PostButton);

      nonlinearParams12UpButton = new JButton();
      nonlinearParams12UpButton.setToolTipText("Move this variation up");
      nonlinearParams12UpButton.setText("");
      nonlinearParams12UpButton.setSize(new Dimension(22, 22));
      nonlinearParams12UpButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams12UpButton.setLocation(new Point(269, 26));
      nonlinearParams12UpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams12UpButton.setBounds(269, 2, 22, 24);
      nonlinearParams12UpButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/draw-arrow-up.png")));
      nonlinearParams12UpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsUpButtonClicked(11);
        }
      });
      nonlinearVar12Panel.add(nonlinearParams12UpButton);

      nonlinearParams12ToggleParamsPnlButton = new JToggleButton();
      nonlinearParams12ToggleParamsPnlButton.setToolTipText("Expand the panel to access all available params");
      nonlinearParams12ToggleParamsPnlButton.setText("");
      nonlinearParams12ToggleParamsPnlButton.setSize(new Dimension(22, 22));
      nonlinearParams12ToggleParamsPnlButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams12ToggleParamsPnlButton.setLocation(new Point(269, 26));
      nonlinearParams12ToggleParamsPnlButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams12ToggleParamsPnlButton.setBounds(42, 26, 22, 24);
      nonlinearParams12ToggleParamsPnlButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/arrow-down-3.png")));
      nonlinearParams12ToggleParamsPnlButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsToggleParamsPnlClicked(11);
        }
      });
      nonlinearVar12Panel.add(nonlinearParams12ToggleParamsPnlButton);
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
      nonlinearVar12Cmb.setLocation(new Point(62, 2));
      nonlinearVar12Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearVar12Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearVar12Cmb.setMaximumRowCount(22);
      nonlinearVar12Cmb.setSize(new Dimension(108, 24));
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
      nonlinearVar12REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams12Cmb.setLocation(new Point(62, 26));
      nonlinearParams12Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams12Cmb.setPreferredSize(new Dimension(120, 22));
      nonlinearParams12Cmb.setSize(new Dimension(128, 24));
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
      nonlinearParams12REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      nonlinearParams12LeftButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      nonlinearParams12LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams12LeftButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-system.png")));
      nonlinearParams12LeftButton.setText("");
      nonlinearParams12LeftButton.setSize(new Dimension(22, 24));
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
      tinaGrabPaletteFromFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      loadFromClipboardFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      saveFlameToClipboardButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      mouseTransformSlowButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      mouseTransformSlowButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/click.png")));
      mouseTransformSlowButton.setSelected(false);
      mouseTransformSlowButton.setText("Fine Edit");
      mouseTransformSlowButton.setSize(new Dimension(92, 24));
      mouseTransformSlowButton.setLocation(new Point(88, 4));
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

  private JToggleButton affinePreserveZButton;
  private JButton qualityProfileBtn;
  private JButton resolutionProfileBtn;
  private JComboBox qualityProfileCmb;
  private JComboBox resolutionProfileCmb;
  private ButtonGroup swfAnimatorFlamesButtonGroup;
  private JButton tinaAppendToMovieButton;
  private JWFNumberField transformationWeightREd;
  private JWFNumberField relWeightREd;
  private JToggleButton mouseTransformEditPointsButton;
  private JButton undoButton;
  private JLabel label_5;
  private JButton redoButton;
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
  private JButton btnAllSave;
  private JLabel label_6;
  private JToggleButton toggleTransparencyButton;
  private JCheckBox bgTransparencyCBx;
  private JToggleButton mouseTransformEditViewButton;
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
  private JPanel tinaColorChooserPaletteImgPanel;
  private JWFNumberField tinaFilterRadiusREd;
  private JSlider tinaFilterRadiusSlider;
  private JComboBox tinaFilterKernelCmb;
  private JWFNumberField camDimishZREd;
  private JSlider camDimishZSlider;
  private JButton tinaAddLinkedTransformationButton;
  private JProgressBar mutaGenProgressBar;
  private JButton editFlameTitleBtn;
  private JLabel label_8;
  private JButton editTransformCaptionBtn;
  private JToggleButton mouseTransformRotateTrianglesButton;
  private JToggleButton mouseTransformScaleTrianglesButton;
  private JTree scriptTree;
  private JButton newScriptBtn;
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
  private JPanel panel_73;
  private JCheckBox tinaPaletteFadeColorsCBx;
  private JCheckBox tinaPaletteUniformWidthCBx;
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
  private JToggleButton layerPreviewBtn;
  private JPanel motionBlurPanel;
  private JPanel panel_79;
  private JPanel panel_80;
  private JWFNumberField keyframesFrameField;
  private JWFNumberField keyframesFrameCountField;
  private JSlider keyframesFrameSlider;
  private JToggleButton motionCurveEditModeButton;
  private JPanel panel_66;
  private JPanel panel_66a;
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
  private JComboBox stereo3dPreviewCmb;
  private JLabel lblPreviewMode;
  private JWFNumberField stereo3dFocalOffsetREd;
  private JSlider stereo3dFocalOffsetSlider;
  private JCheckBox stereo3dSwapSidesCBx;
  private JWFNumberField tinaCameraCamPosXREd;
  private JWFNumberField tinaCameraCamPosYREd;
  private JWFNumberField tinaCameraCamPosZREd;
  private JSlider tinaCameraCamPosXSlider;
  private JSlider tinaCameraCamPosYSlider;
  private JSlider tinaCameraCamPosZSlider;
  private JSlider tinaSaturationSlider;
  private JWFNumberField tinaSaturationREd;
  private JToggleButton toggleDrawGridButton;
  private JToggleButton mouseTransformEditTriangleViewButton;
  private JComboBox randomGradientCmb;
  private JComboBox tinaPaletteRandomGeneratorCmb;
  private JToggleButton toggleTriangleWithColorsButton;
  private JButton affineScaleEditMotionCurveBtn;
  private JButton affineRotateEditMotionCurveBtn;
  private JComboBox triangleStyleCmb;
  private JPanel panel_19;
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
  private JPanel channelMixerPanel;
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
  private JButton motionCurvePlayPreviewButton;
  private JPanel panel_92;
  private JTabbedPane tabbedPane_3;
  private JPanel bokehSettingsPnl;
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
  private JButton bokehBtn;
  private JButton resetCameraSettingsBtn;
  private JButton resetDOFSettingsButton;
  private JButton resetBokehOptionsButton;
  private JButton resetColoringOptionsButton;
  private JButton resetAntialiasOptionsButton;
  private JButton resetPostBlurSettingsBtn;
  private JButton resetStereo3DSettingsBtn;
  private JButton resetPostSymmetrySettingsBtn;
  private JButton resetMotionBlurSettingsBtn;
  private JRadioButton xaosViewAsToBtn;
  private JRadioButton xaosViewAsFromBtn;
  private JToggleButton toggleDrawGuidesButton;
  private JPanel macroButtonRootPanel;
  private JPanel previewEastDefaultPanel;
  private JButton scriptAddButtonBtn;
  private JPanel panel_108;
  private JTable macroButtonsTable;
  private JButton macroButtonMoveUpBtn;
  private JButton macroButtonDeleteBtn;
  private JButton macroButtonMoveDownBtn;
  private JPanel macroButtonPanel;
  private JToggleButton toggleDetachedPreviewButton;
  private JButton gradientResetBtn;
  private JWFNumberField tinaWhiteLevelREd;
  private JSlider tinaWhiteLevelSlider;
  private JPanel macroButtonHorizRootPanel;
  private JScrollPane macroButtonsHorizScrollPane;
  private JPanel macroButtonHorizPanel;
  private JButton randomizeColorSpeedButton;
  private JButton tinaClearGradientImageButton;
  private JButton backgroundSelectImageBtn;
  private JButton backgroundRemoveImageBtn;
  private JPanel gradientColorMapPnl;
  private JLabel lblHoffset;
  private JLabel lblHscale;
  private JLabel lblLocalCAdd;
  private JLabel lblLocalScl;
  private JLabel lblVoffset;
  private JLabel lblVScale;
  private JLabel label_15;
  private JLabel label_16;
  private JWFNumberField gradientColorMapHorizOffsetREd;
  private JWFNumberField gradientColorMapHorizScaleREd;
  private JWFNumberField gradientColorMapLocalColorAddREd;
  private JWFNumberField gradientColorMapLocalColorScaleREd;
  private JWFNumberField gradientColorMapVertOffsetREd;
  private JWFNumberField gradientColorMapVertScaleREd;
  private JWFNumberField numberField_6;
  private JWFNumberField numberField_7;
  private JSlider gradientColorMapHorizOffsetSlider;
  private JSlider gradientColorMapHorizScaleSlider;
  private JSlider gradientColorMapLocalColorAddSlider;
  private JSlider gradientColorMapLocalColorScaleSlider;
  private JSlider gradientColorMapVertOffsetSlider;
  private JSlider gradientColorMapVertScaleSlider;
  private JSlider slider_6;
  private JSlider slider_7;
  private JWFNumberField flameFPSField;
  private JToggleButton leapMotionToggleButton;
  private JPanel panel_111;
  private JPanel panel_112;
  private JTable leapMotionConfigTable;
  private JComboBox leapMotionHandCmb;
  private JComboBox leapMotionInputChannelCmb;
  private JLabel lblInputChannel;
  private JLabel lblFlameproperty;
  private JComboBox leapMotionOutputChannelCmb;
  private JLabel lblFlamepropertyindex;
  private JLabel lblHand;
  private JWFNumberField leapMotionIndex1Field;
  private JWFNumberField leapMotionInvScaleField;
  private JWFNumberField leapMotionOffsetField;
  private JButton leapMotionAddButton;
  private JButton leapMotionDuplicateButton;
  private JButton leapMotionDeleteButton;
  private JButton leapMotionClearButton;
  private JWFNumberField leapMotionIndex2Field;
  private JWFNumberField leapMotionIndex3Field;
  private JButton leapMotionResetConfigButton;
  private JWFNumberField tinaSpatialOversamplingREd;
  private JSlider tinaSpatialOversamplingSlider;
  private JPanel filterKernelPreviewRootPnl;
  private JToggleButton filterKernelFlatPreviewBtn;
  private JCheckBox tinaPostNoiseFilterCheckBox;
  private JWFNumberField tinaPostNoiseThresholdField;
  private JSlider tinaPostNoiseThresholdSlider;
  private JWFNumberField foregroundOpacityField;
  private JSlider foregroundOpacitySlider;
  private JButton scriptEditBtn;
  private JPanel panel_113;
  private JToggleButton nonlinearParams1PreButton;
  private JToggleButton nonlinearParams1PostButton;
  private JButton nonlinearParams11UpButton;
  private JToggleButton nonlinearParams9PreButton;
  private JToggleButton nonlinearParams2PostButton;
  private JToggleButton nonlinearParams10PostButton;
  private JButton nonlinearParams7UpButton;
  private JToggleButton nonlinearParams8PostButton;
  private JButton nonlinearParams12UpButton;
  private JToggleButton nonlinearParams5PostButton;
  private JToggleButton nonlinearParams12PreButton;
  private JToggleButton nonlinearParams5PreButton;
  private JButton nonlinearParams4UpButton;
  private JButton nonlinearParams6UpButton;
  private JToggleButton nonlinearParams7PreButton;
  private JToggleButton nonlinearParams8PreButton;
  private JButton nonlinearParams9UpButton;
  private JButton nonlinearParams10UpButton;
  private JToggleButton nonlinearParams12PostButton;
  private JToggleButton nonlinearParams2PreButton;
  private JToggleButton nonlinearParams6PreButton;
  private JToggleButton nonlinearParams4PreButton;
  private JToggleButton nonlinearParams4PostButton;
  private JToggleButton nonlinearParams6PostButton;
  private JToggleButton nonlinearParams11PreButton;
  private JButton nonlinearParams2UpButton;
  private JToggleButton nonlinearParams10PreButton;
  private JButton nonlinearParams5UpButton;
  private JToggleButton nonlinearParams9PostButton;
  private JButton nonlinearParams8UpButton;
  private JButton nonlinearParams3UpButton;
  private JToggleButton nonlinearParams3PreButton;
  private JToggleButton nonlinearParams11PostButton;
  private JToggleButton nonlinearParams7PostButton;
  private JToggleButton nonlinearParams3PostButton;
  private JButton flameToBatchButton;
  private JPanel mainPrevievPnl;
  private JToggleButton realtimePreviewToggleButton;
  private JButton relWeightsResetButton;
  private JButton relWeightsResetAllButton;
  private JPanel panel_59;
  private JTabbedPane tinaSolidRenderingPane;
  private JButton resetSolidRenderingMaterialsBtn;
  private JPanel panel_114;
  private JPanel panel_115;
  private JButton resetSolidRenderingLightsBtn;
  private JComboBox tinaSolidRenderingSelectedLightCmb;
  private JButton tinaSolidRenderingAddLightBtn;
  private JWFNumberField tinaSolidRenderingLightAltitudeREd;
  private JWFNumberField tinaSolidRenderingLightAzimuthREd;
  private JSlider tinaSolidRenderingLightAltitudeSlider;
  private JSlider tinaSolidRenderingLightAzimuthSlider;
  private JButton tinaSolidRenderingLightColorBtn;
  private JCheckBox tinaSolidRenderingLightCastShadowsCBx;
  private JWFNumberField tinaSolidRenderingLightIntensityREd;
  private JSlider tinaSolidRenderingLightIntensitySlider;
  private JComboBox tinaSolidRenderingSelectedMaterialCmb;
  private JButton tinaSolidRenderingAddMaterialBtn;
  private JButton tinaSolidRenderingDeleteMaterialBtn;
  private JWFNumberField tinaSolidRenderingMaterialDiffuseREd;
  private JSlider tinaSolidRenderingMaterialDiffuseSlider;
  private JWFNumberField tinaSolidRenderingMaterialAmbientREd;
  private JSlider tinaSolidRenderingMaterialAmbientSlider;
  private JSlider tinaSolidRenderingMaterialSpecularSlider;
  private JWFNumberField tinaSolidRenderingMaterialSpecularSharpnessREd;
  private JSlider tinaSolidRenderingMaterialSpecularSharpnessSlider;
  private JComboBox tinaSolidRenderingMaterialDiffuseResponseCmb;
  private JButton tinaSolidRenderingDeleteLightBtn;
  private JWFNumberField tinaSolidRenderingMaterialSpecularREd;
  private JButton tinaSolidRenderingMaterialSpecularColorBtn;
  private JButton tinaSolidRenderingMaterialReflMapBtn;
  private JButton tinaSolidRenderingMaterialSelectReflMapBtn;
  private JButton tinaSolidRenderingMaterialRemoveReflMapBtn;
  private JLabel lblReflectionMap;
  private JWFNumberField tinaSolidRenderingMaterialReflectionMapIntensityREd;
  private JSlider tinaSolidRenderingMaterialReflectionMapIntensitySlider;
  private JWFNumberField xFormModHueREd;
  private JWFNumberField xFormModHueSpeedREd;
  private JSlider xFormModHueSlider;
  private JSlider xFormModHueSpeedSlider;
  private JWFNumberField xFormMaterialREd;
  private JSlider xFormMaterialSlider;
  private JPanel tinaMaterialChooserPaletteImgPanel;
  private JWFNumberField xFormMaterialSpeedREd;
  private JSlider xFormMaterialSpeedSlider;
  private JPanel panel;
  private JCheckBox tinaSolidRenderingEnableAOCBx;
  private JWFNumberField tinaSolidRenderingAOIntensityREd;
  private JSlider tinaSolidRenderingAOIntensitySlider;
  private JWFNumberField tinaSolidRenderingAOSearchRadiusREd;
  private JWFNumberField tinaSolidRenderingAOBlurRadiusREd;
  private JWFNumberField tinaSolidRenderingAOFalloffREd;
  private JWFNumberField tinaSolidRenderingAORadiusSamplesREd;
  private JWFNumberField tinaSolidRenderingAOAzimuthSamplesREd;
  private JSlider tinaSolidRenderingAOSearchRadiusSlider;
  private JSlider tinaSolidRenderingAOBlurRadiusSlider;
  private JSlider tinaSolidRenderingAOFalloffSlider;
  private JSlider tinaSolidRenderingAORadiusSamplesSlider;
  private JSlider tinaSolidRenderingAOAzimuthSamplesSlider;
  private JToggleButton solidRenderingToggleBtn;
  private JLabel label_1;
  private JLabel label_3;
  private JToggleButton affineXYEditPlaneToggleBtn;
  private JToggleButton affineYZEditPlaneToggleBtn;
  private JToggleButton affineZXEditPlaneToggleBtn;
  private JPanel panel_4;
  private JWFNumberField tinaSolidRenderingAOAffectDiffuseREd;
  private JSlider tinaSolidRenderingAOAffectDiffuseSlider;
  private JLabel lblHintAmbientShadows;
  private JComboBox tinaSolidRenderingMaterialReflectionMappingCmb;
  private JButton sendFlameToIRButton;
  private JWFNumberField tinaSolidRenderingShadowIntensityREd;
  private JSlider tinaSolidRenderingShadowIntensitySlider;
  private JComboBox tinaSolidRenderingShadowmapSizeCmb;
  private JComboBox tinaSolidRenderingShadowTypeCmb;
  private JWFNumberField tinaSolidRenderingShadowSmoothRadiusREd;
  private JSlider tinaSolidRenderingShadowSmoothRadiusSlider;
  private JButton resetSolidRenderingHardShadowOptionsBtn;
  private JButton resetSolidRenderingAmbientShadowOptionsBtn;
  private JWFNumberField tinaSolidRenderingShadowmapBiasREd;
  private JSlider tinaSolidRenderingShadowmapBiasSlider;
  private JPanel panel_5;
  private JWFNumberField tinaZBufferScaleREd;
  private JSlider tinaZBufferScaleSlider;
  private JButton button_1;
  private JPanel postBokehSettingsPnl;
  private JWFNumberField postBokehBrightnessREd;
  private JLabel lblBokehBrightness;
  private JSlider postBokehBrightnessSlider;
  private JLabel lblBokehActivation;
  private JWFNumberField postBokehActivationREd;
  private JSlider postBokehActivationSlider;
  private JWFNumberField postBokehIntensityREd;
  private JSlider postBokehIntensitySlider;
  private JSlider postBokehSizeSlider;
  private JWFNumberField postBokehSizeREd;
  private JComboBox postBokehFilterKernelCmb;
  private JButton resetPostBokehSettingsBtn;
  private JToggleButton nonlinearParams1ToggleParamsPnlButton;
  private JToggleButton nonlinearParams2ToggleParamsPnlButton;
  private JToggleButton nonlinearParams3ToggleParamsPnlButton;
  private JToggleButton nonlinearParams4ToggleParamsPnlButton;
  private JToggleButton nonlinearParams5ToggleParamsPnlButton;
  private JToggleButton nonlinearParams6ToggleParamsPnlButton;
  private JToggleButton nonlinearParams7ToggleParamsPnlButton;
  private JToggleButton nonlinearParams8ToggleParamsPnlButton;
  private JToggleButton nonlinearParams9ToggleParamsPnlButton;
  private JToggleButton nonlinearParams10ToggleParamsPnlButton;
  private JToggleButton nonlinearParams11ToggleParamsPnlButton;
  private JToggleButton nonlinearParams12ToggleParamsPnlButton;
  private JWFNumberField lowDensityBrightnessREd;
  private JSlider lowDensityBrightnessSlider;
  private JSlider balanceRedSlider;
  private JWFNumberField balanceRedREd;
  private JSlider balanceGreenSlider;
  private JWFNumberField balanceGreenREd;
  private JSlider balanceBlueSlider;
  private JWFNumberField balanceBlueREd;
  private JComboBox backgroundColorTypeCmb;
  private JButton backgroundColorURIndicatorBtn;
  private JButton backgroundColorLLIndicatorBtn;
  private JButton backgroundColorLRIndicatorBtn;
  private JButton backgroundColorCCIndicatorBtn;
  private JComboBox tinaFilterTypeCmb;
  private JLabel tinaFilterKernelLbl;
  private JLabel tinaFilterRadiusLbl;
  private JCheckBox tinaFilterIndicatorCBx;
  private JPopupMenu thumbnailSelectPopupMenu;
  private JPopupMenu thumbnailRemovePopupMenu;
  private JMenuItem mntmNewMenuItem;
  private JMenuItem mntmRemoveAll;
  private JMenuItem mntmDeselectAll;
  private JMenuItem mntmRemoveSelected;
  private JWFNumberField tinaFilterSharpnessREd;
  private JSlider tinaFilterSharpnessSlider;
  private JSlider tinaFilterLowDensitySlider;
  private JWFNumberField tinaFilterLowDensityREd;

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
      affineFlipHorizontalButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 8));
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
      affineFlipVerticalButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 8));
      affineFlipVerticalButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_flipVertical();
        }
      });
    }
    return affineFlipVerticalButton;
  }

  /**
   * This method initializes blurShadingPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getBlurShadingPanel() {
    if (blurShadingPanel == null) {
      blurShadingPanel = new JPanel();
      blurShadingPanel.setLayout(null);
    }
    return blurShadingPanel;
  }

  /**
   * This method initializes shadingBlurRadiusREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getPostBlurRadiusREd() {
    if (postBlurRadiusREd == null) {
      postBlurRadiusREd = new JWFNumberField();
      postBlurRadiusREd.setMaxValue(10.0);
      postBlurRadiusREd.setOnlyIntegers(true);
      postBlurRadiusREd.setValueStep(1.0);
      postBlurRadiusREd.setHasMinValue(true);
      postBlurRadiusREd.setHasMaxValue(true);
      postBlurRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!postBlurRadiusREd.isMouseAdjusting() || postBlurRadiusREd.getMouseChangeCount() == 0) {
            if (!postBlurRadiusSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().postBlurRadiusREd_changed();
        }
      });
      postBlurRadiusREd.setPreferredSize(new Dimension(100, 24));
      postBlurRadiusREd.setText("");
      postBlurRadiusREd.setSize(new Dimension(100, 24));
      postBlurRadiusREd.setLocation(new Point(102, 6));
      postBlurRadiusREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    }
    return postBlurRadiusREd;
  }

  /**
   * This method initializes shadingBlurRadiusSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getPostBlurRadiusSlider() {
    if (postBlurRadiusSlider == null) {
      postBlurRadiusSlider = new JSlider();
      postBlurRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postBlurRadiusSlider.setMaximum(6);
      postBlurRadiusSlider.setMinimum(0);
      postBlurRadiusSlider.setValue(0);
      postBlurRadiusSlider.setSize(new Dimension(220, 19));
      postBlurRadiusSlider.setLocation(new Point(204, 6));
      postBlurRadiusSlider.setPreferredSize(new Dimension(120, 19));
      postBlurRadiusSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().postBlurRadiusSlider_changed();
        }
      });
    }
    return postBlurRadiusSlider;
  }

  /**
   * This method initializes shadingBlurFadeREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JWFNumberField getPostBlurFadeREd() {
    if (postBlurFadeREd == null) {
      postBlurFadeREd = new JWFNumberField();
      postBlurFadeREd.setValueStep(0.005);
      postBlurFadeREd.setMaxValue(1.0);
      postBlurFadeREd.setHasMinValue(true);
      postBlurFadeREd.setHasMaxValue(true);
      postBlurFadeREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!postBlurFadeREd.isMouseAdjusting() || postBlurFadeREd.getMouseChangeCount() == 0) {
            if (!postBlurFadeSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().postBlurFadeREd_changed();
        }
      });
      postBlurFadeREd.setPreferredSize(new Dimension(100, 24));
      postBlurFadeREd.setText("");
      postBlurFadeREd.setSize(new Dimension(100, 24));
      postBlurFadeREd.setLocation(new Point(102, 30));
      postBlurFadeREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    }
    return postBlurFadeREd;
  }

  /**
   * This method initializes shadingBlurFadeSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getPostBlurFadeSlider() {
    if (postBlurFadeSlider == null) {
      postBlurFadeSlider = new JSlider();
      postBlurFadeSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postBlurFadeSlider.setMaximum(100);
      postBlurFadeSlider.setMinimum(0);
      postBlurFadeSlider.setValue(0);
      postBlurFadeSlider.setSize(new Dimension(220, 19));
      postBlurFadeSlider.setLocation(new Point(204, 30));
      postBlurFadeSlider.setPreferredSize(new Dimension(120, 19));
      postBlurFadeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().postBlurFadeSlider_changed();
        }
      });
    }
    return postBlurFadeSlider;
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
      panel_1.setMaximumSize(new Dimension(32767, 172));
      panel_1.setPreferredSize(new Dimension(210, 220));
      scriptPanel.add(panel_1, BorderLayout.NORTH);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(124, 10));
      panel_1.add(panel_2, BorderLayout.EAST);
      panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 1));

      rescanScriptsBtn = new JButton();
      rescanScriptsBtn.setMinimumSize(new Dimension(116, 12));
      rescanScriptsBtn.setMaximumSize(new Dimension(116, 12));
      rescanScriptsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().rescanBtn_clicked();
        }
      });
      panel_2.add(rescanScriptsBtn);
      rescanScriptsBtn.setToolTipText("Rescan script-folder");
      rescanScriptsBtn.setText("Rescan");
      rescanScriptsBtn.setPreferredSize(new Dimension(116, 24));
      rescanScriptsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      rescanScriptsBtn.setBounds(new Rectangle(9, 280, 125, 24));

      JPanel panel_8 = new JPanel();
      panel_8.setPreferredSize(new Dimension(116, 4));
      panel_8.setMinimumSize(new Dimension(116, 8));
      panel_8.setMaximumSize(new Dimension(32767, 8));
      panel_2.add(panel_8);
      panel_2.add(getNewScriptBtn());
      panel_2.add(getDuplicateScriptBtn());
      panel_2.add(getNewScriptFromFlameBtn());

      deleteScriptBtn = new JButton();
      deleteScriptBtn.setMinimumSize(new Dimension(58, 12));
      deleteScriptBtn.setMaximumSize(new Dimension(58, 12));
      deleteScriptBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().deleteScriptBtn_clicked();
        }
      });
      deleteScriptBtn.setToolTipText("Delete script");
      deleteScriptBtn.setText("Del");
      deleteScriptBtn.setPreferredSize(new Dimension(58, 24));
      deleteScriptBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      deleteScriptBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_2.add(deleteScriptBtn);

      scriptRenameBtn = new JButton();
      scriptRenameBtn.setMinimumSize(new Dimension(58, 12));
      scriptRenameBtn.setMaximumSize(new Dimension(58, 12));
      scriptRenameBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().scriptRename_clicked();
        }
      });
      scriptRenameBtn.setToolTipText("Rename script");
      scriptRenameBtn.setText("Ren");
      scriptRenameBtn.setPreferredSize(new Dimension(58, 24));
      scriptRenameBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      scriptRenameBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_2.add(scriptRenameBtn);

      JPanel panel_3 = new JPanel();
      panel_3.setMaximumSize(new Dimension(32767, 8));
      panel_3.setMinimumSize(new Dimension(10, 8));
      panel_3.setPreferredSize(new Dimension(116, 8));
      panel_2.add(panel_3);

      scriptRunBtn = new JButton();
      scriptRunBtn.setMnemonic('u');
      scriptRunBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().scriptRunBtn_clicked();
        }
      });
      panel_2.add(getScriptAddButtonBtn());
      panel_2.add(getPanel_108());
      scriptRunBtn.setToolTipText("Run script");
      scriptRunBtn.setText("Run");
      scriptRunBtn.setPreferredSize(new Dimension(116, 24));
      scriptRunBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      scriptRunBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_2.add(scriptRunBtn);
      panel_2.add(getPanel_113());
      panel_2.add(getScriptEditBtn());

      JScrollPane scrollPane_2 = new JScrollPane();
      panel_1.add(scrollPane_2, BorderLayout.CENTER);

      scriptTree = new JTree();
      scriptTree.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 10));
      scriptTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          if (tinaController != null) {
            tinaController.getJwfScriptController().scriptPropertiesTree_changed(e);
          }
        }
      });
      scrollPane_2.setViewportView(scriptTree);
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
      scriptTextArea.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 10));
      scriptTextArea.setText("");
    }
    return scriptTextArea;
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
      affineScaleXButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      affineScaleYButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      randomizeColorsButton.setToolTipText("Randomize colors");
      randomizeColorsButton.setBounds(6, 58, 79, 24);
      randomizeColorsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      randomizeColorsButton.setText("Rnd clr");
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
      qualityProfileBtn.setPreferredSize(new Dimension(32, 24));
      qualityProfileBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      resolutionProfileBtn.setPreferredSize(new Dimension(32, 24));
      resolutionProfileBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return resolutionProfileBtn;
  }

  public JComboBox getQualityProfileCmb() {
    return qualityProfileCmb;
  }

  public JComboBox getResolutionProfileCmb() {
    return resolutionProfileCmb;
  }

  private ButtonGroup getSwfAnimatorFlamesButtonGroup() {
    if (swfAnimatorFlamesButtonGroup == null) {
      swfAnimatorFlamesButtonGroup = new ButtonGroup();
    }
    return swfAnimatorFlamesButtonGroup;
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
      tinaAppendToMovieButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      tinaAppendToMovieButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/applications-multimedia.png")));
    }
    return tinaAppendToMovieButton;
  }

  private JWFNumberField getTransformationWeightREd() {
    if (transformationWeightREd == null) {
      transformationWeightREd = new JWFNumberField();
      transformationWeightREd.setLinkedLabelControlName("Weight");
      transformationWeightREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getXFormControls().editMotionCurve(e);
        }
      });
      transformationWeightREd.setMotionPropertyName("weight");
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
      transformationWeightREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    }
    return transformationWeightREd;
  }

  public JWFNumberField getRelWeightREd() {
    return relWeightREd;
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
      undoButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      undoButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return undoButton;
  }

  private JLabel getLabel_5() {
    if (label_5 == null) {
      label_5 = new JLabel();
      label_5.setText("");
      label_5.setPreferredSize(new Dimension(42, 12));
      label_5.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      redoButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      redoButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-redo-6.png")));
    }
    return redoButton;
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

      tinaFilterRadiusLbl = new JLabel();
      tinaFilterRadiusLbl.setText("Filter radius");
      tinaFilterRadiusLbl.setSize(new Dimension(94, 22));
      tinaFilterRadiusLbl.setPreferredSize(new Dimension(94, 22));
      tinaFilterRadiusLbl.setLocation(new Point(488, 2));
      tinaFilterRadiusLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaFilterRadiusLbl.setBounds(6, 76, 107, 22);
      antialiasPanel.add(tinaFilterRadiusLbl);

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
      tinaFilterRadiusREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaFilterRadiusREd.setEditable(true);
      tinaFilterRadiusREd.setBounds(115, 76, 100, 24);
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
      tinaFilterRadiusSlider.setMaximum(200);
      tinaFilterRadiusSlider.setLocation(new Point(686, 2));
      tinaFilterRadiusSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaFilterRadiusSlider.setBounds(217, 76, 220, 24);
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
      tinaFilterKernelCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaFilterKernelCmb.setBounds(115, 52, 322, 24);
      antialiasPanel.add(tinaFilterKernelCmb);

      tinaFilterKernelLbl = new JLabel();
      tinaFilterKernelLbl.setText("Filter kernel");
      tinaFilterKernelLbl.setSize(new Dimension(94, 22));
      tinaFilterKernelLbl.setPreferredSize(new Dimension(94, 22));
      tinaFilterKernelLbl.setLocation(new Point(488, 2));
      tinaFilterKernelLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaFilterKernelLbl.setBounds(6, 52, 107, 22);
      antialiasPanel.add(tinaFilterKernelLbl);

      resetAntialiasOptionsButton = new JButton();
      resetAntialiasOptionsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetAntialiasingSettings();
        }
      });
      resetAntialiasOptionsButton.setToolTipText("Reset the antialiasing-options to the defaults");
      resetAntialiasOptionsButton.setText("Reset");
      resetAntialiasOptionsButton.setPreferredSize(new Dimension(125, 24));
      resetAntialiasOptionsButton.setMinimumSize(new Dimension(100, 24));
      resetAntialiasOptionsButton.setMaximumSize(new Dimension(32000, 24));
      resetAntialiasOptionsButton.setIconTextGap(2);
      resetAntialiasOptionsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetAntialiasOptionsButton.setBounds(1018, 6, 100, 24);
      resetAntialiasOptionsButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
      antialiasPanel.add(resetAntialiasOptionsButton);

      JLabel lblSpatialOversampling = new JLabel();
      lblSpatialOversampling.setToolTipText("");
      lblSpatialOversampling.setText("Spatial oversampling");
      lblSpatialOversampling.setSize(new Dimension(94, 22));
      lblSpatialOversampling.setPreferredSize(new Dimension(94, 22));
      lblSpatialOversampling.setLocation(new Point(488, 2));
      lblSpatialOversampling.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSpatialOversampling.setBounds(6, 4, 107, 22);
      antialiasPanel.add(lblSpatialOversampling);

      tinaSpatialOversamplingREd = new JWFNumberField();
      tinaSpatialOversamplingREd.setLinkedMotionControlName("tinaSpatialOversamplingSlider");
      tinaSpatialOversamplingREd.setMinValue(1.0);
      tinaSpatialOversamplingREd.setOnlyIntegers(true);
      tinaSpatialOversamplingREd.setValueStep(1.0);
      tinaSpatialOversamplingREd.setText("");
      tinaSpatialOversamplingREd.setSize(new Dimension(100, 24));
      tinaSpatialOversamplingREd.setPreferredSize(new Dimension(100, 24));
      tinaSpatialOversamplingREd.setMaxValue(6.0);
      tinaSpatialOversamplingREd.setLocation(new Point(584, 2));
      tinaSpatialOversamplingREd.setHasMinValue(true);
      tinaSpatialOversamplingREd.setHasMaxValue(true);
      tinaSpatialOversamplingREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSpatialOversamplingREd.setEditable(true);
      tinaSpatialOversamplingREd.setBounds(115, 4, 100, 24);
      tinaSpatialOversamplingREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaSpatialOversamplingREd.isMouseAdjusting() || tinaSpatialOversamplingREd.getMouseChangeCount() == 0) {
              if (!tinaSpatialOversamplingSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().spatialOversamplingREd_changed();
          }
        }
      });
      antialiasPanel.add(tinaSpatialOversamplingREd);

      tinaSpatialOversamplingSlider = new JSlider();
      tinaSpatialOversamplingSlider.setValue(0);
      tinaSpatialOversamplingSlider.setSize(new Dimension(220, 19));
      tinaSpatialOversamplingSlider.setPreferredSize(new Dimension(220, 19));
      tinaSpatialOversamplingSlider.setName("tinaSpatialOversamplingSlider");
      tinaSpatialOversamplingSlider.setMinimum(1);
      tinaSpatialOversamplingSlider.setMaximum(6);
      tinaSpatialOversamplingSlider.setLocation(new Point(686, 2));
      tinaSpatialOversamplingSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSpatialOversamplingSlider.setBounds(217, 4, 220, 24);
      tinaSpatialOversamplingSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().spatialOversamplingSlider_stateChanged(e);
          }
        }
      });
      tinaSpatialOversamplingSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      antialiasPanel.add(tinaSpatialOversamplingSlider);

      filterKernelPreviewRootPnl = new JPanel();
      filterKernelPreviewRootPnl.setBounds(449, 6, 104, 104);
      antialiasPanel.add(filterKernelPreviewRootPnl);
      filterKernelPreviewRootPnl.setLayout(new BorderLayout(0, 0));

      filterKernelFlatPreviewBtn = new JToggleButton();
      filterKernelFlatPreviewBtn.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.filterKernelFlatPreviewBtn_clicked();
          }
        }
      });
      filterKernelFlatPreviewBtn.setToolTipText("Draw a flat (and faster) visualization of the filter kernel, red parts indicate negative values");
      filterKernelFlatPreviewBtn.setText("Flat preview");
      filterKernelFlatPreviewBtn.setSize(new Dimension(32, 24));
      filterKernelFlatPreviewBtn.setPreferredSize(new Dimension(26, 24));
      filterKernelFlatPreviewBtn.setMnemonic(KeyEvent.VK_P);
      filterKernelFlatPreviewBtn.setLocation(new Point(92, 127));
      filterKernelFlatPreviewBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      filterKernelFlatPreviewBtn.setBounds(449, 114, 107, 24);
      antialiasPanel.add(filterKernelFlatPreviewBtn);

      tinaPostNoiseFilterCheckBox = new JCheckBox("Post noise reduction");
      tinaPostNoiseFilterCheckBox.setBounds(674, 64, 169, 18);
      tinaPostNoiseFilterCheckBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().postNoiseFilterCheckBox_changed();
          }
        }
      });
      tinaPostNoiseFilterCheckBox.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      antialiasPanel.add(tinaPostNoiseFilterCheckBox);

      tinaPostNoiseThresholdField = new JWFNumberField();
      tinaPostNoiseThresholdField.setMouseSpeed(0.1);
      tinaPostNoiseThresholdField.setValueStep(0.05);
      tinaPostNoiseThresholdField.setText("");
      tinaPostNoiseThresholdField.setSize(new Dimension(100, 24));
      tinaPostNoiseThresholdField.setPreferredSize(new Dimension(100, 24));
      tinaPostNoiseThresholdField.setMaxValue(1.0);
      tinaPostNoiseThresholdField.setLocation(new Point(584, 2));
      tinaPostNoiseThresholdField.setLinkedMotionControlName("tinaPostNoiseThresholdSlider");
      tinaPostNoiseThresholdField.setHasMinValue(true);
      tinaPostNoiseThresholdField.setHasMaxValue(true);
      tinaPostNoiseThresholdField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaPostNoiseThresholdField.setEditable(true);
      tinaPostNoiseThresholdField.setBounds(674, 85, 100, 24);
      tinaPostNoiseThresholdField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaPostNoiseThresholdField.isMouseAdjusting() || tinaPostNoiseThresholdField.getMouseChangeCount() == 0) {
              if (!tinaPostNoiseThresholdSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().postNoiseFilterThresholdREd_changed();
          }
        }
      });

      antialiasPanel.add(tinaPostNoiseThresholdField);

      JLabel lblNoiseThreshold = new JLabel();
      lblNoiseThreshold.setToolTipText("");
      lblNoiseThreshold.setText("Noise threshold");
      lblNoiseThreshold.setSize(new Dimension(94, 22));
      lblNoiseThreshold.setPreferredSize(new Dimension(94, 22));
      lblNoiseThreshold.setLocation(new Point(488, 2));
      lblNoiseThreshold.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblNoiseThreshold.setBounds(565, 85, 107, 22);
      antialiasPanel.add(lblNoiseThreshold);

      tinaPostNoiseThresholdSlider = new JSlider();
      tinaPostNoiseThresholdSlider.setMaximum(1000);
      tinaPostNoiseThresholdSlider.setValue(0);
      tinaPostNoiseThresholdSlider.setSize(new Dimension(220, 19));
      tinaPostNoiseThresholdSlider.setPreferredSize(new Dimension(220, 19));
      tinaPostNoiseThresholdSlider.setName("tinaPostNoiseThresholdSlider");
      tinaPostNoiseThresholdSlider.setLocation(new Point(686, 2));
      tinaPostNoiseThresholdSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPostNoiseThresholdSlider.setBounds(776, 85, 220, 24);
      tinaPostNoiseThresholdSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().postNoiseFilterThresholdSlider_stateChanged(e);
          }
        }
      });
      tinaPostNoiseThresholdSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });

      antialiasPanel.add(tinaPostNoiseThresholdSlider);

      tinaFilterTypeCmb = new JComboBox();
      tinaFilterTypeCmb.setSize(new Dimension(125, 22));
      tinaFilterTypeCmb.setPreferredSize(new Dimension(125, 22));
      tinaFilterTypeCmb.setLocation(new Point(100, 4));
      tinaFilterTypeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaFilterTypeCmb.setBounds(115, 28, 322, 24);
      tinaFilterTypeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.spatialFilterTypeCmb_changed();
          }
        }
      });

      antialiasPanel.add(tinaFilterTypeCmb);

      JLabel lblFiltering = new JLabel();
      lblFiltering.setText("Filtering");
      lblFiltering.setSize(new Dimension(94, 22));
      lblFiltering.setPreferredSize(new Dimension(94, 22));
      lblFiltering.setLocation(new Point(488, 2));
      lblFiltering.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblFiltering.setBounds(6, 28, 107, 22);
      antialiasPanel.add(lblFiltering);

      tinaFilterIndicatorCBx = new JCheckBox("Indicator (red=sharp, green=smooth, blue=low density, displays only at the next-quickrender)");
      tinaFilterIndicatorCBx.setToolTipText("");
      tinaFilterIndicatorCBx.setBounds(565, 117, 553, 18);
      tinaFilterIndicatorCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().filterIndicatorCheckBox_changed();
          }
        }
      });
      tinaFilterIndicatorCBx.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      antialiasPanel.add(tinaFilterIndicatorCBx);

      JLabel lblSharpness = new JLabel();
      lblSharpness.setToolTipText("The lower this value the more likely an area is sharpened");
      lblSharpness.setText("Sharpness indicator");
      lblSharpness.setSize(new Dimension(94, 22));
      lblSharpness.setPreferredSize(new Dimension(94, 22));
      lblSharpness.setLocation(new Point(488, 2));
      lblSharpness.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSharpness.setBounds(6, 100, 107, 22);
      antialiasPanel.add(lblSharpness);

      tinaFilterSharpnessREd = new JWFNumberField();
      tinaFilterSharpnessREd.setToolTipText("");
      tinaFilterSharpnessREd.setValueStep(0.05);
      tinaFilterSharpnessREd.setText("");
      tinaFilterSharpnessREd.setSize(new Dimension(100, 24));
      tinaFilterSharpnessREd.setPreferredSize(new Dimension(100, 24));
      tinaFilterSharpnessREd.setMaxValue(20.0);
      tinaFilterSharpnessREd.setLocation(new Point(584, 2));
      tinaFilterSharpnessREd.setLinkedMotionControlName("tinaFilterSharpnessSlider");
      tinaFilterSharpnessREd.setHasMinValue(true);
      tinaFilterSharpnessREd.setHasMaxValue(true);
      tinaFilterSharpnessREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaFilterSharpnessREd.setEditable(true);
      tinaFilterSharpnessREd.setBounds(115, 100, 100, 24);
      tinaFilterSharpnessREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaFilterSharpnessREd.isMouseAdjusting() || tinaFilterSharpnessREd.getMouseChangeCount() == 0) {
              if (!tinaFilterSharpnessSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().filterSharpnessREd_changed();
          }
        }
      });
      antialiasPanel.add(tinaFilterSharpnessREd);

      tinaFilterSharpnessSlider = new JSlider();
      tinaFilterSharpnessSlider.setValue(0);
      tinaFilterSharpnessSlider.setSize(new Dimension(220, 19));
      tinaFilterSharpnessSlider.setPreferredSize(new Dimension(220, 19));
      tinaFilterSharpnessSlider.setName("tinaFilterSharpnessSlider");
      tinaFilterSharpnessSlider.setMinimum(0);
      tinaFilterSharpnessSlider.setMaximum(1000);
      tinaFilterSharpnessSlider.setLocation(new Point(686, 2));
      tinaFilterSharpnessSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaFilterSharpnessSlider.setBounds(217, 100, 220, 24);
      tinaFilterSharpnessSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().filterSharpnessSlider_stateChanged(e);
          }
        }
      });
      tinaFilterSharpnessSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      antialiasPanel.add(tinaFilterSharpnessSlider);

      JLabel lblLowDensity = new JLabel();
      lblLowDensity.setToolTipText("The higher this value the more likely an area is threated as low density and is smoothed more than a regular smooth area");
      lblLowDensity.setText("Low density");
      lblLowDensity.setSize(new Dimension(94, 22));
      lblLowDensity.setPreferredSize(new Dimension(94, 22));
      lblLowDensity.setLocation(new Point(488, 2));
      lblLowDensity.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblLowDensity.setBounds(6, 125, 107, 22);
      antialiasPanel.add(lblLowDensity);

      tinaFilterLowDensityREd = new JWFNumberField();
      tinaFilterLowDensityREd.setToolTipText("");
      tinaFilterLowDensityREd.setValueStep(0.05);
      tinaFilterLowDensityREd.setText("");
      tinaFilterLowDensityREd.setSize(new Dimension(100, 24));
      tinaFilterLowDensityREd.setPreferredSize(new Dimension(100, 24));
      tinaFilterLowDensityREd.setMaxValue(1.0);
      tinaFilterLowDensityREd.setLocation(new Point(584, 2));
      tinaFilterLowDensityREd.setLinkedMotionControlName("tinaFilterLowDensitySlider");
      tinaFilterLowDensityREd.setHasMinValue(true);
      tinaFilterLowDensityREd.setHasMaxValue(true);
      tinaFilterLowDensityREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaFilterLowDensityREd.setEditable(true);
      tinaFilterLowDensityREd.setBounds(115, 125, 100, 24);
      tinaFilterLowDensityREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!tinaFilterLowDensityREd.isMouseAdjusting() || tinaFilterLowDensityREd.getMouseChangeCount() == 0) {
              if (!tinaFilterLowDensitySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().filterLowDensityREd_changed();
          }
        }
      });

      antialiasPanel.add(tinaFilterLowDensityREd);

      tinaFilterLowDensitySlider = new JSlider();
      tinaFilterLowDensitySlider.setValue(0);
      tinaFilterLowDensitySlider.setSize(new Dimension(220, 19));
      tinaFilterLowDensitySlider.setPreferredSize(new Dimension(220, 19));
      tinaFilterLowDensitySlider.setName("tinaFilterLowDensitySlider");
      tinaFilterLowDensitySlider.setMinimum(0);
      tinaFilterLowDensitySlider.setMaximum(20);
      tinaFilterLowDensitySlider.setLocation(new Point(686, 2));
      tinaFilterLowDensitySlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaFilterLowDensitySlider.setBounds(217, 125, 220, 24);
      tinaFilterLowDensitySlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().filterLowDensitySlider_stateChanged(e);
          }
        }
      });
      tinaFilterLowDensitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      antialiasPanel.add(tinaFilterLowDensitySlider);
    }
    return antialiasPanel;
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
      panel_13.add(getPanel_66a());
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
      resolutionProfileCmb.setPreferredSize(new Dimension(85, 24));
      resolutionProfileCmb.setMaximumRowCount(32);
      resolutionProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      qualityProfileCmb.setPreferredSize(new Dimension(85, 24));
      qualityProfileCmb.setMaximumRowCount(32);
      qualityProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteSwapRGBLbl_1.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteSwapRGBREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteSwapRGBSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteSwapRGBSlider.setBounds(118, 6, 204, 22);
      tinaPaletteTransformPanel.add(tinaPaletteSwapRGBSlider);

      JLabel tinaPaletteFrequencyLbl = new JLabel();
      tinaPaletteFrequencyLbl.setText("Frequency");
      tinaPaletteFrequencyLbl.setSize(new Dimension(56, 22));
      tinaPaletteFrequencyLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteFrequencyLbl.setLocation(new Point(6, 214));
      tinaPaletteFrequencyLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteFrequencyREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteFrequencySlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaPaletteFrequencySlider.setBounds(118, 31, 204, 22);
      tinaPaletteTransformPanel.add(tinaPaletteFrequencySlider);

      JLabel tinaPaletteBlurLbl = new JLabel();
      tinaPaletteBlurLbl.setText("Blur");
      tinaPaletteBlurLbl.setSize(new Dimension(56, 22));
      tinaPaletteBlurLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteBlurLbl.setLocation(new Point(6, 214));
      tinaPaletteBlurLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteBlurREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      tinaPaletteBlurSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteInvertBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteReverseBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      tinaPaletteSortBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientApplyTXBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      snapShotButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return snapShotButton;
  }

  private JButton getBtnAllSave() {
    if (btnAllSave == null) {
      btnAllSave = new JButton();
      btnAllSave.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.allsaveButton_clicked();
        }
      });
      btnAllSave.setToolTipText("Save all selected flames");
      btnAllSave.setText("A");
      btnAllSave.setPreferredSize(new Dimension(42, 24));
      btnAllSave.setMnemonic(KeyEvent.VK_A);
      btnAllSave.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return btnAllSave;
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
      btnQsave.setPreferredSize(new Dimension(42, 24));
      btnQsave.setMnemonic(KeyEvent.VK_Q);
      btnQsave.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return btnQsave;
  }

  private JLabel getLabel_6() {
    if (label_6 == null) {
      label_6 = new JLabel();
      label_6.setText("");
      label_6.setPreferredSize(new Dimension(42, 12));
      label_6.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return label_6;
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
      toggleTransparencyButton.setBounds(46, 4, 42, 24);
    }
    return toggleTransparencyButton;
  }

  public JCheckBox getBgTransparencyCBx() {
    return bgTransparencyCBx;
  }

  public JToggleButton getMouseTransformViewButton() {
    return mouseTransformEditViewButton;
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
      tinaAddLinkedTransformationButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return tinaAddLinkedTransformationButton;
  }

  public JProgressBar getMutaGenProgressBar() {
    return mutaGenProgressBar;
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
      editFlameTitleBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return editFlameTitleBtn;
  }

  private JLabel getLabel_8() {
    if (label_8 == null) {
      label_8 = new JLabel();
      label_8.setText("");
      label_8.setPreferredSize(new Dimension(42, 4));
      label_8.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      newScriptBtn.setMinimumSize(new Dimension(58, 12));
      newScriptBtn.setMaximumSize(new Dimension(58, 12));
      newScriptBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().newScriptBtn_clicked();
        }
      });
      newScriptBtn.setToolTipText("Create a new script from scratch");
      newScriptBtn.setText("New");
      newScriptBtn.setPreferredSize(new Dimension(58, 24));
      newScriptBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      newScriptBtn.setBounds(new Rectangle(9, 280, 125, 24));
    }
    return newScriptBtn;
  }

  private JTabbedPane getTabbedPane() {
    if (tabbedPane == null) {
      tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane.addTab("Description", null, getPanel_60(), null);
      tabbedPane.addTab("Code preview", null, getPanel_61(), null);

      JPanel panel_1 = new JPanel();
      tabbedPane.addTab("Macro buttons", null, panel_1, null);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(120, 10));
      panel_1.add(panel_2, BorderLayout.CENTER);
      panel_2.setLayout(new BorderLayout(0, 0));

      JScrollPane scrollPane_2 = new JScrollPane();
      scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane_2.setBorder(null);
      panel_2.add(scrollPane_2, BorderLayout.CENTER);

      macroButtonsTable = new JTable();
      macroButtonsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      macroButtonsTable.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      macroButtonsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            tinaController.getJwfScriptController().macroButtonsTableClicked();
          }
        }

      });

      scrollPane_2.setViewportView(macroButtonsTable);

      JPanel panel_3 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
      flowLayout.setHgap(0);
      flowLayout.setVgap(1);
      panel_3.setPreferredSize(new Dimension(124, 10));
      panel_1.add(panel_3, BorderLayout.EAST);

      JPanel panel_9 = new JPanel();
      panel_9.setPreferredSize(new Dimension(116, 4));
      panel_9.setMinimumSize(new Dimension(116, 8));
      panel_9.setMaximumSize(new Dimension(32767, 8));
      panel_3.add(panel_9);

      macroButtonMoveUpBtn = new JButton();
      macroButtonMoveUpBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().macroButtonMoveUp();
        }
      });
      macroButtonMoveUpBtn.setToolTipText("Move the button one up in the list");
      macroButtonMoveUpBtn.setText("Up");
      macroButtonMoveUpBtn.setPreferredSize(new Dimension(58, 24));
      macroButtonMoveUpBtn.setMinimumSize(new Dimension(58, 12));
      macroButtonMoveUpBtn.setMaximumSize(new Dimension(58, 12));
      macroButtonMoveUpBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      macroButtonMoveUpBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_3.add(macroButtonMoveUpBtn);

      macroButtonMoveDownBtn = new JButton();
      macroButtonMoveDownBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().macroButtonMoveDown();
        }
      });
      macroButtonMoveDownBtn.setToolTipText("Move the button one down in the list");
      macroButtonMoveDownBtn.setText("Down");
      macroButtonMoveDownBtn.setPreferredSize(new Dimension(58, 24));
      macroButtonMoveDownBtn.setMinimumSize(new Dimension(58, 12));
      macroButtonMoveDownBtn.setMaximumSize(new Dimension(58, 12));
      macroButtonMoveDownBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      macroButtonMoveDownBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_3.add(macroButtonMoveDownBtn);

      JPanel panel_8 = new JPanel();
      panel_8.setPreferredSize(new Dimension(116, 4));
      panel_8.setMinimumSize(new Dimension(116, 8));
      panel_8.setMaximumSize(new Dimension(32767, 8));
      panel_3.add(panel_8);

      macroButtonDeleteBtn = new JButton();
      macroButtonDeleteBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().macroButtonDelete();
        }
      });
      macroButtonDeleteBtn.setToolTipText("Delete the button");
      macroButtonDeleteBtn.setText("Delete");
      macroButtonDeleteBtn.setPreferredSize(new Dimension(116, 24));
      macroButtonDeleteBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      macroButtonDeleteBtn.setBounds(new Rectangle(9, 280, 125, 24));
      panel_3.add(macroButtonDeleteBtn);
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
      scriptDescriptionTextArea.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 10));
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
      duplicateScriptBtn.setMinimumSize(new Dimension(58, 12));
      duplicateScriptBtn.setMaximumSize(new Dimension(58, 12));
      duplicateScriptBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().duplicateScriptBtn_clicked();
        }
      });
      duplicateScriptBtn.setToolTipText("Create a copy of the currently selected script");
      duplicateScriptBtn.setText("Dupl");
      duplicateScriptBtn.setPreferredSize(new Dimension(58, 24));
      duplicateScriptBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      duplicateScriptBtn.setBounds(new Rectangle(9, 280, 125, 24));
    }
    return duplicateScriptBtn;
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
      newScriptFromFlameBtn.setPreferredSize(new Dimension(116, 24));
      newScriptFromFlameBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientEditorFncPnl.add(getGradientResetBtn());
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
      gradientInvertBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientReverseBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientSortBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientFadeBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientSelectAllBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientApplyBalancingBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientCopyRangeBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientPasteRangeBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientEraseRangeBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientMonochromeRangeBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientFadeAllBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientLibraryRescanBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientLibraryNewFolderBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientLibraryRenameFolderBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      gradientSaveBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      backgroundColorIndicatorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      backgroundColorIndicatorBtn.setBounds(1027, 3, 30, 24);
    }
    return backgroundColorIndicatorBtn;
  }

  public JButton getRandomizeBtn() {
    return randomizeBtn;
  }

  private JPanel getPanel_73() {
    if (panel_73 == null) {
      panel_73 = new JPanel();
      panel_73.setPreferredSize(new Dimension(10, 24));
      panel_73.setMinimumSize(new Dimension(10, 24));
      panel_73.setLayout(null);

      mouseTransformEditGradientButton = new JToggleButton();
      mouseTransformEditGradientButton.setBounds(72, 0, 163, 24);
      panel_73.add(mouseTransformEditGradientButton);
      mouseTransformEditGradientButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      mouseTransformEditGradientButton.setText("Edit gradient");
      mouseTransformEditGradientButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-graphics-2.png")));

      mouseTransformEditGradientButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          expandGradientEditorFncPnl(mouseTransformEditGradientButton.isSelected());
          tinaController.mouseTransformEditGradientButton_clicked();
        }
      });
      mouseTransformEditGradientButton.setToolTipText("Edit gradient (use cursor-left and -right to control marker 1 and cursor-up and -down to control marker 2, press the 1 or 2 key for color choosers)");
      mouseTransformEditGradientButton.setPreferredSize(new Dimension(72, 24));
    }
    return panel_73;
  }

  public JCheckBox getTinaPaletteFadeColorsCBx() {
    return tinaPaletteFadeColorsCBx;
  }
  
  public JCheckBox getTinaPaletteUniformWidthCBx() {
	return tinaPaletteUniformWidthCBx;
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
      layerAddBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      layerAddBtn.setBounds(6, 81, 90, 24);
      panel_75.add(layerAddBtn);

      layerWeightEd = new JWFNumberField();
      layerWeightEd.setValueStep(0.05);
      layerWeightEd.setText("");
      layerWeightEd.setSize(new Dimension(81, 24));
      layerWeightEd.setPreferredSize(new Dimension(56, 24));
      layerWeightEd.setLocation(new Point(238, 6));
      layerWeightEd.setHasMinValue(true);
      layerWeightEd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      layerDuplicateBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      layerDeleteBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      layerVisibleBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      layerVisibleBtn.setBounds(6, 7, 86, 24);
      panel_75.add(layerVisibleBtn);

      JLabel lblWeight = new JLabel();
      lblWeight.setText("Weight");
      lblWeight.setSize(new Dimension(20, 22));
      lblWeight.setPreferredSize(new Dimension(24, 22));
      lblWeight.setLocation(new Point(212, 6));
      lblWeight.setHorizontalAlignment(SwingConstants.RIGHT);
      lblWeight.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      layerHideOthersBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      layerShowAllBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      layerAppendBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      layersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
      layerPreviewBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblBlurLength.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      motionBlurLengthField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      motionBlurTimeStepField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      motionBlurTimeStepField.setBounds(102, 30, 100, 24);
      motionBlurPanel.add(motionBlurTimeStepField);

      JLabel lblTimeStep = new JLabel();
      lblTimeStep.setText("Time step");
      lblTimeStep.setSize(new Dimension(94, 22));
      lblTimeStep.setPreferredSize(new Dimension(94, 22));
      lblTimeStep.setName("");
      lblTimeStep.setLocation(new Point(4, 28));
      lblTimeStep.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblTimeStep.setBounds(6, 30, 94, 22);
      motionBlurPanel.add(lblTimeStep);

      JLabel lblDecay = new JLabel();
      lblDecay.setText("Decay");
      lblDecay.setSize(new Dimension(94, 22));
      lblDecay.setPreferredSize(new Dimension(94, 22));
      lblDecay.setName("");
      lblDecay.setLocation(new Point(4, 52));
      lblDecay.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      motionBlurDecayField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      motionBlurPanel.add(getResetMotionBlurSettingsBtn());

      flameFPSField = new JWFNumberField();
      flameFPSField.setHasMinValue(true);
      flameFPSField.setHasMaxValue(true);
      flameFPSField.setMaxValue(500.0);
      flameFPSField.setMinValue(1.0);
      flameFPSField.setOnlyIntegers(true);
      flameFPSField.setValueStep(1.0);
      flameFPSField.setText("");
      flameFPSField.setSize(new Dimension(100, 24));
      flameFPSField.setPreferredSize(new Dimension(100, 24));
      flameFPSField.setLocation(new Point(100, 52));
      flameFPSField.setLinkedMotionControlName("motionBlurDecaySlider");
      flameFPSField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      flameFPSField.setBounds(625, 6, 100, 24);
      flameFPSField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null) {
            if (!flameFPSField.isMouseAdjusting() || flameFPSField.getMouseChangeCount() == 0) {
              tinaController.saveUndoPoint();
            }
            tinaController.getFlameControls().flameFPSField_changed();
          }
        }
      });
      motionBlurPanel.add(flameFPSField);

      JLabel lblFps = new JLabel();
      lblFps.setText("FPS");
      lblFps.setSize(new Dimension(94, 22));
      lblFps.setPreferredSize(new Dimension(94, 22));
      lblFps.setName("");
      lblFps.setLocation(new Point(4, 52));
      lblFps.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblFps.setBounds(529, 6, 94, 22);
      motionBlurPanel.add(lblFps);
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
      keyframesFrameLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      keyframesFrameField = new JWFNumberField();
      keyframesFrameField.setText("1");
      keyframesFrameField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getAnimationController() != null) {
            tinaController.getAnimationController().keyFrameFieldChanged();
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
      keyframesFrameField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
              int frameCount = keyframesFrameCountField.getIntValue();
              if (keyframesFrameSlider.getValue() > frameCount) {
                keyframesFrameSlider.setValue(frameCount);
              }
              keyframesFrameSlider.setMaximum(frameCount);
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
      keyframesFrameCountField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      keyframesFrameCountField.setBounds(144, 0, 70, 24);
      panel_80.add(keyframesFrameCountField);

      keyframesFrameCountLbl = new JLabel();
      keyframesFrameCountLbl.setText("Frame count");
      keyframesFrameCountLbl.setPreferredSize(new Dimension(94, 22));
      keyframesFrameCountLbl.setHorizontalAlignment(SwingConstants.RIGHT);
      keyframesFrameCountLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      keyframesFrameCountLbl.setBounds(70, 0, 70, 22);
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
      motionCurvePlayPreviewButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      panel_66.setLayout(new BorderLayout(0, 0));
      panel_66.add(getTinaSaveFlameButton());
      panel_66.add(getBtnAllSave(), BorderLayout.EAST);
    }
    return panel_66;
  }

  private JPanel getPanel_66a() {
    if (panel_66a == null) {
      panel_66a = new JPanel();
      panel_66a.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel_66a.setMaximumSize(new Dimension(200, 24));
      panel_66a.setPreferredSize(new Dimension(125, 24));
      panel_66a.setLayout(new BorderLayout(0, 0));
      panel_66a.add(getSaveFlameToClipboardButton());
      panel_66a.add(getBtnQsave(), BorderLayout.EAST);
    }
    return panel_66a;
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
      postSymmetryTypeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryTypeLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryDistanceREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      postSymmetryDistanceLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      postSymmetryDistanceLbl.setBounds(6, 31, 94, 22);
      panel_34.add(postSymmetryDistanceLbl);

      JLabel postSymmetryRotationLbl = new JLabel();
      postSymmetryRotationLbl.setName("postSymmetryRotationLbl");
      postSymmetryRotationLbl.setText("Rotation");
      postSymmetryRotationLbl.setSize(new Dimension(94, 22));
      postSymmetryRotationLbl.setPreferredSize(new Dimension(94, 22));
      postSymmetryRotationLbl.setLocation(new Point(488, 2));
      postSymmetryRotationLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryRotationREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      postSymmetryDistanceSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryRotationSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryOrderREd.setText("");
      postSymmetryOrderREd.setSize(new Dimension(100, 24));
      postSymmetryOrderREd.setPreferredSize(new Dimension(100, 24));
      postSymmetryOrderREd.setMaxValue(36.0);
      postSymmetryOrderREd.setLocation(new Point(584, 2));
      postSymmetryOrderREd.setLinkedMotionControlName("postSymmetryOrderSlider");
      postSymmetryOrderREd.setHasMinValue(true);
      postSymmetryOrderREd.setHasMaxValue(true);
      postSymmetryOrderREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      postSymmetryOrderLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      postSymmetryOrderLbl.setBounds(436, 6, 94, 22);
      panel_34.add(postSymmetryOrderLbl);

      JLabel postSymmetryCentreXLbl = new JLabel();
      postSymmetryCentreXLbl.setName("postSymmetryCentreXLbl");
      postSymmetryCentreXLbl.setText("Centre X");
      postSymmetryCentreXLbl.setSize(new Dimension(94, 22));
      postSymmetryCentreXLbl.setPreferredSize(new Dimension(94, 22));
      postSymmetryCentreXLbl.setLocation(new Point(488, 2));
      postSymmetryCentreXLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryCentreXREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      postSymmetryOrderSlider.setMaximum(36);
      postSymmetryOrderSlider.setLocation(new Point(686, 2));
      postSymmetryOrderSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryCentreXSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryCentreYLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      postSymmetryCentreYREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      postSymmetryCentreYSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      panel_34.add(getResetPostSymmetrySettingsBtn());
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
      randomStyleLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      randomGradientCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_81.add(randomGradientCmb);
    }
    return panel_81;
  }

  private JLabel getLblSymmetry() {
    if (lblSymmetry == null) {
      lblSymmetry = new JLabel();
      lblSymmetry.setText("  Symmetry/Gradient");
      lblSymmetry.setPreferredSize(new Dimension(100, 22));
      lblSymmetry.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      randomSymmetryCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      stereo3dModeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblStereodMode.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblStereodMode.setBounds(6, 6, 94, 22);
      panel_82.add(lblStereodMode);

      JLabel lblAngle = new JLabel();
      lblAngle.setText("View angle");
      lblAngle.setSize(new Dimension(94, 22));
      lblAngle.setPreferredSize(new Dimension(94, 22));
      lblAngle.setName("lblAngle");
      lblAngle.setLocation(new Point(488, 2));
      lblAngle.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      stereo3dAngleREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      stereo3dAngleSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblImageCount.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      stereo3dInterpolatedImageCountREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      stereo3dInterpolatedImageCountSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblEyeDistance.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      stereo3dEyeDistREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      stereo3dEyeDistSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      stereo3dLeftEyeColorCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblLeftEyeColor.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblLeftEyeColor.setBounds(437, 31, 94, 22);
      panel_82.add(lblLeftEyeColor);

      stereo3dRightEyeColorCmb = new JComboBox();
      stereo3dRightEyeColorCmb.setSize(new Dimension(125, 22));
      stereo3dRightEyeColorCmb.setPreferredSize(new Dimension(125, 22));
      stereo3dRightEyeColorCmb.setLocation(new Point(100, 4));
      stereo3dRightEyeColorCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblRightEyeColor.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      stereo3dFocalOffsetREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      lblFocalOffset.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      stereo3dFocalOffsetSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      stereo3dSwapSidesCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getFlameControls().stereo3dSwapSidesCBx_changed();
          }
        }
      });

      panel_82.add(stereo3dSwapSidesCBx);
      panel_82.add(getResetStereo3DSettingsBtn());
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

  private JComboBox getStereo3dPreviewCmb() {
    if (stereo3dPreviewCmb == null) {
      stereo3dPreviewCmb = new JComboBox();
      stereo3dPreviewCmb.setSize(new Dimension(125, 22));
      stereo3dPreviewCmb.setPreferredSize(new Dimension(125, 22));
      stereo3dPreviewCmb.setLocation(new Point(100, 4));
      stereo3dPreviewCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblPreviewMode.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

  public JCheckBox getStereo3dSwapSidesCBx() {
    return stereo3dSwapSidesCBx;
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
      affineScaleEditMotionCurveBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      editTransformCaptionBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return panel_19;
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
      channelMixerModeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      channelMixerResetBtn.setToolTipText("Reset curves to defaults");
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
      channelMixerResetBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      channelMixerResetBtn.setBounds(6, 75, 100, 24);
      channelMixerResetBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
      panel_1.add(channelMixerResetBtn);

      JLabel lblMode = new JLabel();
      lblMode.setText("Mixer mode*");
      lblMode.setSize(new Dimension(20, 22));
      lblMode.setPreferredSize(new Dimension(24, 22));
      lblMode.setName("affineC00Lbl");
      lblMode.setLocation(new Point(0, 6));
      lblMode.setHorizontalAlignment(SwingConstants.LEFT);
      lblMode.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblRed.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblGreen.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblBlue.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return lblBlue;
  }

  public JButton getChannelMixerResetBtn() {
    return channelMixerResetBtn;
  }

  public JComboBox getChannelMixerModeCmb() {
    return channelMixerModeCmb;
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
      tabbedPane_3 = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane_3.addTab("DOF", null, tinaDOFPanel, null);
      tabbedPane_3.addTab("Bokeh", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/distributions-parsix_linux.png")), getBokehSettingsPnl(), null);

      postBokehSettingsPnl = new JPanel();
      tabbedPane_3.addTab("Post bokeh", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/kwikdisk-4.png")), postBokehSettingsPnl, null);
      postBokehSettingsPnl.setLayout(null);

      JLabel label = new JLabel();
      label.setText("Note: Post-bokeh-calculation is a post-effect which is applied after rendering, so it can not be displayed during progressive rendering.");
      label.setSize(new Dimension(68, 22));
      label.setPreferredSize(new Dimension(94, 22));
      label.setName("tinaCameraRollLbl");
      label.setLocation(new Point(4, 4));
      label.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label.setBounds(115, 6, 765, 22);
      postBokehSettingsPnl.add(label);

      postBokehFilterKernelCmb = new JComboBox();
      postBokehFilterKernelCmb.setSize(new Dimension(125, 22));
      postBokehFilterKernelCmb.setPreferredSize(new Dimension(125, 22));
      postBokehFilterKernelCmb.setLocation(new Point(100, 4));
      postBokehFilterKernelCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      postBokehFilterKernelCmb.setBounds(115, 72, 322, 24);
      postBokehFilterKernelCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingPostBokehFilterKernelCmb_changed();
          }
        }
      });

      postBokehSettingsPnl.add(postBokehFilterKernelCmb);

      JLabel lblBokehFilterKernel = new JLabel();
      lblBokehFilterKernel.setText("Bokeh filter kernel");
      lblBokehFilterKernel.setSize(new Dimension(94, 22));
      lblBokehFilterKernel.setPreferredSize(new Dimension(94, 22));
      lblBokehFilterKernel.setLocation(new Point(488, 2));
      lblBokehFilterKernel.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBokehFilterKernel.setBounds(6, 72, 107, 22);
      postBokehSettingsPnl.add(lblBokehFilterKernel);

      JLabel lblBokehIntensity = new JLabel();
      lblBokehIntensity.setName("postBokehSizeLbl");
      lblBokehIntensity.setText("Bokeh size");
      lblBokehIntensity.setSize(new Dimension(94, 22));
      lblBokehIntensity.setPreferredSize(new Dimension(94, 22));
      lblBokehIntensity.setLocation(new Point(488, 2));
      lblBokehIntensity.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBokehIntensity.setBounds(6, 50, 107, 22);
      postBokehSettingsPnl.add(lblBokehIntensity);

      postBokehSizeREd = new JWFNumberField();
      postBokehSizeREd.setLinkedLabelControlName("postBokehSizeLbl");
      postBokehSizeREd.setValueStep(0.05);
      postBokehSizeREd.setText("");
      postBokehSizeREd.setSize(new Dimension(100, 24));
      postBokehSizeREd.setPreferredSize(new Dimension(100, 24));
      postBokehSizeREd.setMaxValue(5.0);
      postBokehSizeREd.setLocation(new Point(584, 2));
      postBokehSizeREd.setLinkedMotionControlName("postBokehSizeSlider");
      postBokehSizeREd.setHasMinValue(true);
      postBokehSizeREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      postBokehSizeREd.setEditable(true);
      postBokehSizeREd.setBounds(115, 50, 100, 24);
      postBokehSizeREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      postBokehSizeREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!postBokehSizeREd.isMouseAdjusting() || postBokehSizeREd.getMouseChangeCount() == 0) {
              if (!postBokehSizeSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingPostBokehSizeREd_changed();
          }
        }
      });

      postBokehSettingsPnl.add(postBokehSizeREd);

      postBokehSizeSlider = new JSlider();
      postBokehSizeSlider.setValue(0);
      postBokehSizeSlider.setSize(new Dimension(220, 19));
      postBokehSizeSlider.setPreferredSize(new Dimension(220, 19));
      postBokehSizeSlider.setName("tinaFilterRadiusSlider");
      postBokehSizeSlider.setMinimum(0);
      postBokehSizeSlider.setMaximum(20000);
      postBokehSizeSlider.setLocation(new Point(686, 2));
      postBokehSizeSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      postBokehSizeSlider.setBounds(217, 50, 220, 24);
      postBokehSizeSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postBokehSizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingPostBokehSizeSlider_stateChanged(e);
        }
      });
      postBokehSettingsPnl.add(postBokehSizeSlider);

      JLabel label_2 = new JLabel();
      label_2.setName("postBokehIntensityLbl");
      label_2.setText("Bokeh intensity");
      label_2.setSize(new Dimension(94, 22));
      label_2.setPreferredSize(new Dimension(94, 22));
      label_2.setLocation(new Point(488, 2));
      label_2.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label_2.setBounds(6, 28, 107, 22);
      postBokehSettingsPnl.add(label_2);

      postBokehIntensityREd = new JWFNumberField();
      postBokehIntensityREd.setLinkedLabelControlName("postBokehIntensityLbl");
      postBokehIntensityREd.setValueStep(0.005);
      postBokehIntensityREd.setText("");
      postBokehIntensityREd.setSize(new Dimension(100, 24));
      postBokehIntensityREd.setPreferredSize(new Dimension(100, 24));
      postBokehIntensityREd.setMaxValue(5.0);
      postBokehIntensityREd.setLocation(new Point(584, 2));
      postBokehIntensityREd.setLinkedMotionControlName("postBokehIntensitySlider");
      postBokehIntensityREd.setHasMinValue(true);
      postBokehIntensityREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      postBokehIntensityREd.setEditable(true);
      postBokehIntensityREd.setBounds(115, 28, 100, 24);
      postBokehIntensityREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      postBokehIntensityREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!postBokehIntensityREd.isMouseAdjusting() || postBokehIntensityREd.getMouseChangeCount() == 0) {
              if (!postBokehIntensitySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingPostBokehIntensityREd_changed();
          }
        }
      });

      postBokehSettingsPnl.add(postBokehIntensityREd);

      postBokehIntensitySlider = new JSlider();
      postBokehIntensitySlider.setValue(0);
      postBokehIntensitySlider.setSize(new Dimension(220, 19));
      postBokehIntensitySlider.setPreferredSize(new Dimension(220, 19));
      postBokehIntensitySlider.setName("tinaFilterRadiusSlider");
      postBokehIntensitySlider.setMinimum(0);
      postBokehIntensitySlider.setMaximum(20000);
      postBokehIntensitySlider.setLocation(new Point(686, 2));
      postBokehIntensitySlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      postBokehIntensitySlider.setBounds(217, 28, 220, 24);
      postBokehIntensitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postBokehIntensitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingPostBokehIntensitySlider_stateChanged(e);
        }
      });

      postBokehSettingsPnl.add(postBokehIntensitySlider);

      resetPostBokehSettingsBtn = new JButton();
      resetPostBokehSettingsBtn.setToolTipText("Reset the post-bokeh-options to the defaults");
      resetPostBokehSettingsBtn.setText("Reset");
      resetPostBokehSettingsBtn.setPreferredSize(new Dimension(125, 24));
      resetPostBokehSettingsBtn.setMinimumSize(new Dimension(100, 24));
      resetPostBokehSettingsBtn.setMaximumSize(new Dimension(32000, 24));
      resetPostBokehSettingsBtn.setIconTextGap(2);
      resetPostBokehSettingsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetPostBokehSettingsBtn.setBounds(892, 5, 100, 24);
      resetPostBokehSettingsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
      resetPostBokehSettingsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetPostBokehSettings();
        }
      });

      postBokehSettingsPnl.add(resetPostBokehSettingsBtn);
      postBokehSettingsPnl.add(getPostBokehBrightnessREd());
      postBokehSettingsPnl.add(getLblBokehBrightness());
      postBokehSettingsPnl.add(getPostBokehBrightnessSlider());
      postBokehSettingsPnl.add(getLblBokehActivation());
      postBokehSettingsPnl.add(getPostBokehActivationREd());
      postBokehSettingsPnl.add(getPostBokehActivationSlider());

      JPanel panel_1 = new JPanel();
      tabbedPane_3.addTab("Post blur", null, panel_1, null);
      panel_1.setLayout(null);
      shadingBlurFadeLbl = new JLabel();
      shadingBlurFadeLbl.setPreferredSize(new Dimension(94, 22));
      shadingBlurFadeLbl.setText("Blur fade");
      shadingBlurFadeLbl.setSize(new Dimension(94, 22));
      shadingBlurFadeLbl.setLocation(new Point(6, 30));
      shadingBlurFadeLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      shadingBlurRadiusLbl = new JLabel();
      shadingBlurRadiusLbl.setPreferredSize(new Dimension(94, 22));
      shadingBlurRadiusLbl.setText("Blur radius");
      shadingBlurRadiusLbl.setSize(new Dimension(94, 22));
      shadingBlurRadiusLbl.setLocation(new Point(6, 6));
      shadingBlurRadiusLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      shadingBlurFallOffLbl = new JLabel();
      shadingBlurFallOffLbl.setPreferredSize(new Dimension(94, 22));
      shadingBlurFallOffLbl.setText("Blur falloff");
      shadingBlurFallOffLbl.setSize(new Dimension(94, 22));
      shadingBlurFallOffLbl.setLocation(new Point(6, 54));
      shadingBlurFallOffLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      panel_1.add(shadingBlurRadiusLbl, null);
      panel_1.add(getPostBlurRadiusREd(), null);
      panel_1.add(getPostBlurRadiusSlider(), null);
      panel_1.add(shadingBlurFadeLbl, null);
      panel_1.add(getPostBlurFadeREd(), null);
      panel_1.add(getPostBlurFadeSlider(), null);
      panel_1.add(shadingBlurFallOffLbl, null);
      panel_1.add(getPostBlurFallOffREd(), null);
      panel_1.add(getPostBlurFallOffSlider(), null);
      panel_1.add(getResetPostBlurSettingsBtn());
      tabbedPane_3.addTab("ZBuffer", null, getPanel_5(), null);
    }
    return tabbedPane_3;
  }

  private JPanel getBokehSettingsPnl() {
    if (bokehSettingsPnl == null) {
      bokehSettingsPnl = new JPanel();
      bokehSettingsPnl.setLayout(null);

      JLabel lblShape = new JLabel();
      lblShape.setText("Shape");
      lblShape.setSize(new Dimension(94, 22));
      lblShape.setPreferredSize(new Dimension(94, 22));
      lblShape.setLocation(new Point(488, 2));
      lblShape.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblShape.setBounds(6, 6, 94, 22);
      bokehSettingsPnl.add(lblShape);

      dofDOFScaleLbl = new JLabel();
      dofDOFScaleLbl.setText("Scale");
      dofDOFScaleLbl.setSize(new Dimension(94, 22));
      dofDOFScaleLbl.setPreferredSize(new Dimension(94, 22));
      dofDOFScaleLbl.setName("dofDOFScaleLbl");
      dofDOFScaleLbl.setLocation(new Point(488, 2));
      dofDOFScaleLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFScaleLbl.setBounds(6, 29, 94, 22);
      bokehSettingsPnl.add(dofDOFScaleLbl);

      dofDOFScaleREd = new JWFNumberField();
      dofDOFScaleREd.setMotionPropertyName("camDOFScale");
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
      dofDOFScaleREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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

      bokehSettingsPnl.add(dofDOFScaleREd);

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
      dofDOFShapeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFShapeCmb.setBounds(102, 6, 322, 24);
      bokehSettingsPnl.add(dofDOFShapeCmb);

      dofDOFScaleSlider = new JSlider();
      dofDOFScaleSlider.setValue(0);
      dofDOFScaleSlider.setSize(new Dimension(220, 19));
      dofDOFScaleSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFScaleSlider.setName("stereo3dAngleSlider");
      dofDOFScaleSlider.setMinimum(-30000);
      dofDOFScaleSlider.setMaximum(30000);
      dofDOFScaleSlider.setLocation(new Point(686, 2));
      dofDOFScaleSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

      bokehSettingsPnl.add(dofDOFScaleSlider);

      dofDOFAngleREd = new JWFNumberField();
      dofDOFAngleREd.setMotionPropertyName("camDOFAngle");
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
      dofDOFAngleREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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

      bokehSettingsPnl.add(dofDOFAngleREd);

      dofDOFAngleLbl = new JLabel();
      dofDOFAngleLbl.setText("Rotate");
      dofDOFAngleLbl.setSize(new Dimension(94, 22));
      dofDOFAngleLbl.setPreferredSize(new Dimension(94, 22));
      dofDOFAngleLbl.setName("dofDOFAngleLbl");
      dofDOFAngleLbl.setLocation(new Point(488, 2));
      dofDOFAngleLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFAngleLbl.setBounds(6, 52, 94, 22);
      bokehSettingsPnl.add(dofDOFAngleLbl);

      dofDOFAngleSlider = new JSlider();
      dofDOFAngleSlider.setValue(0);
      dofDOFAngleSlider.setSize(new Dimension(220, 19));
      dofDOFAngleSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFAngleSlider.setName("stereo3dAngleSlider");
      dofDOFAngleSlider.setMinimum(-30000);
      dofDOFAngleSlider.setMaximum(30000);
      dofDOFAngleSlider.setLocation(new Point(686, 2));
      dofDOFAngleSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bokehSettingsPnl.add(dofDOFAngleSlider);

      dofDOFFadeLbl = new JLabel();
      dofDOFFadeLbl.setText("Fade");
      dofDOFFadeLbl.setSize(new Dimension(94, 22));
      dofDOFFadeLbl.setPreferredSize(new Dimension(94, 22));
      dofDOFFadeLbl.setName("dofDOFFadeLbl");
      dofDOFFadeLbl.setLocation(new Point(488, 2));
      dofDOFFadeLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFFadeLbl.setBounds(6, 75, 94, 22);
      bokehSettingsPnl.add(dofDOFFadeLbl);

      dofDOFFadeREd = new JWFNumberField();
      dofDOFFadeREd.setMotionPropertyName("camDOFFade");
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
      dofDOFFadeREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      bokehSettingsPnl.add(dofDOFFadeREd);

      dofDOFFadeSlider = new JSlider();
      dofDOFFadeSlider.setValue(0);
      dofDOFFadeSlider.setSize(new Dimension(220, 19));
      dofDOFFadeSlider.setPreferredSize(new Dimension(220, 19));
      dofDOFFadeSlider.setName("stereo3dAngleSlider");
      dofDOFFadeSlider.setMinimum(-30000);
      dofDOFFadeSlider.setMaximum(30000);
      dofDOFFadeSlider.setLocation(new Point(686, 2));
      dofDOFFadeSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bokehSettingsPnl.add(dofDOFFadeSlider);

      dofDOFParam1REd = new JWFNumberField();
      dofDOFParam1REd.setMotionPropertyName("camDOFParam1");
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
      dofDOFParam1REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      bokehSettingsPnl.add(dofDOFParam1REd);

      dofDOFParam1Lbl = new JLabel();
      dofDOFParam1Lbl.setText("Param 1");
      dofDOFParam1Lbl.setSize(new Dimension(94, 22));
      dofDOFParam1Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam1Lbl.setName("dofDOFParam1Lbl");
      dofDOFParam1Lbl.setLocation(new Point(488, 2));
      dofDOFParam1Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFParam1Lbl.setBounds(6, 98, 94, 22);
      bokehSettingsPnl.add(dofDOFParam1Lbl);

      dofDOFParam1Slider = new JSlider();
      dofDOFParam1Slider.setValue(0);
      dofDOFParam1Slider.setSize(new Dimension(220, 19));
      dofDOFParam1Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam1Slider.setName("dofDOFParam1Slider");
      dofDOFParam1Slider.setMinimum(-30000);
      dofDOFParam1Slider.setMaximum(30000);
      dofDOFParam1Slider.setLocation(new Point(686, 2));
      dofDOFParam1Slider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bokehSettingsPnl.add(dofDOFParam1Slider);

      dofDOFParam3REd = new JWFNumberField();
      dofDOFParam3REd.setMotionPropertyName("camDOFParam3");
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
      dofDOFParam3REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      bokehSettingsPnl.add(dofDOFParam3REd);

      dofDOFParam3Lbl = new JLabel();
      dofDOFParam3Lbl.setText("Param 3");
      dofDOFParam3Lbl.setSize(new Dimension(94, 22));
      dofDOFParam3Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam3Lbl.setName("dofDOFParam3Lbl");
      dofDOFParam3Lbl.setLocation(new Point(488, 2));
      dofDOFParam3Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFParam3Lbl.setBounds(466, 29, 94, 22);
      bokehSettingsPnl.add(dofDOFParam3Lbl);

      dofDOFParam4Lbl = new JLabel();
      dofDOFParam4Lbl.setText("Param 4");
      dofDOFParam4Lbl.setSize(new Dimension(94, 22));
      dofDOFParam4Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam4Lbl.setName("dofDOFParam4Lbl");
      dofDOFParam4Lbl.setLocation(new Point(488, 2));
      dofDOFParam4Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFParam4Lbl.setBounds(466, 52, 94, 22);
      bokehSettingsPnl.add(dofDOFParam4Lbl);

      dofDOFParam4REd = new JWFNumberField();
      dofDOFParam4REd.setMotionPropertyName("camDOFParam4");
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
      dofDOFParam4REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      bokehSettingsPnl.add(dofDOFParam4REd);

      dofDOFParam3Slider = new JSlider();
      dofDOFParam3Slider.setValue(0);
      dofDOFParam3Slider.setSize(new Dimension(220, 19));
      dofDOFParam3Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam3Slider.setName("dofDOFParam3Slider");
      dofDOFParam3Slider.setMinimum(-30000);
      dofDOFParam3Slider.setMaximum(30000);
      dofDOFParam3Slider.setLocation(new Point(686, 2));
      dofDOFParam3Slider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bokehSettingsPnl.add(dofDOFParam3Slider);

      dofDOFParam4Slider = new JSlider();
      dofDOFParam4Slider.setValue(0);
      dofDOFParam4Slider.setSize(new Dimension(220, 19));
      dofDOFParam4Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam4Slider.setName("dofDOFParam4Slider");
      dofDOFParam4Slider.setMinimum(-30000);
      dofDOFParam4Slider.setMaximum(30000);
      dofDOFParam4Slider.setLocation(new Point(686, 2));
      dofDOFParam4Slider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bokehSettingsPnl.add(dofDOFParam4Slider);

      dofDOFParam5REd = new JWFNumberField();
      dofDOFParam5REd.setMotionPropertyName("camDOFParam5");
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
      dofDOFParam5REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      bokehSettingsPnl.add(dofDOFParam5REd);

      dofDOFParam5Lbl = new JLabel();
      dofDOFParam5Lbl.setText("Param 5");
      dofDOFParam5Lbl.setSize(new Dimension(94, 22));
      dofDOFParam5Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam5Lbl.setName("dofDOFParam5Lbl");
      dofDOFParam5Lbl.setLocation(new Point(488, 2));
      dofDOFParam5Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFParam5Lbl.setBounds(466, 75, 94, 22);
      bokehSettingsPnl.add(dofDOFParam5Lbl);

      dofDOFParam5Slider = new JSlider();
      dofDOFParam5Slider.setValue(0);
      dofDOFParam5Slider.setSize(new Dimension(220, 19));
      dofDOFParam5Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam5Slider.setName("dofDOFParam5Slider");
      dofDOFParam5Slider.setMinimum(-30000);
      dofDOFParam5Slider.setMaximum(30000);
      dofDOFParam5Slider.setLocation(new Point(686, 2));
      dofDOFParam5Slider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bokehSettingsPnl.add(dofDOFParam5Slider);

      dofDOFParam6Lbl = new JLabel();
      dofDOFParam6Lbl.setText("Param 6");
      dofDOFParam6Lbl.setSize(new Dimension(94, 22));
      dofDOFParam6Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam6Lbl.setName("dofDOFParam6Lbl");
      dofDOFParam6Lbl.setLocation(new Point(488, 2));
      dofDOFParam6Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFParam6Lbl.setBounds(466, 100, 98, 22);
      bokehSettingsPnl.add(dofDOFParam6Lbl);

      dofDOFParam6REd = new JWFNumberField();
      dofDOFParam6REd.setMotionPropertyName("camDOFParam6");
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
      dofDOFParam6REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      bokehSettingsPnl.add(dofDOFParam6REd);

      dofDOFParam6Slider = new JSlider();
      dofDOFParam6Slider.setValue(0);
      dofDOFParam6Slider.setSize(new Dimension(220, 19));
      dofDOFParam6Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam6Slider.setName("dofDOFParam6Slider");
      dofDOFParam6Slider.setMinimum(-30000);
      dofDOFParam6Slider.setMaximum(30000);
      dofDOFParam6Slider.setLocation(new Point(686, 2));
      dofDOFParam6Slider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bokehSettingsPnl.add(dofDOFParam6Slider);

      dofDOFParam2Lbl = new JLabel();
      dofDOFParam2Lbl.setText("Param 2");
      dofDOFParam2Lbl.setSize(new Dimension(94, 22));
      dofDOFParam2Lbl.setPreferredSize(new Dimension(94, 22));
      dofDOFParam2Lbl.setName("dofDOFParam2Lbl");
      dofDOFParam2Lbl.setLocation(new Point(488, 2));
      dofDOFParam2Lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dofDOFParam2Lbl.setBounds(466, 6, 94, 22);
      bokehSettingsPnl.add(dofDOFParam2Lbl);

      dofDOFParam2REd = new JWFNumberField();
      dofDOFParam2REd.setMotionPropertyName("camDOFParam2");
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
      dofDOFParam2REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      bokehSettingsPnl.add(dofDOFParam2REd);

      dofDOFParam2Slider = new JSlider();
      dofDOFParam2Slider.setValue(0);
      dofDOFParam2Slider.setSize(new Dimension(220, 19));
      dofDOFParam2Slider.setPreferredSize(new Dimension(220, 19));
      dofDOFParam2Slider.setName("dofDOFParam2Slider");
      dofDOFParam2Slider.setMinimum(-30000);
      dofDOFParam2Slider.setMaximum(30000);
      dofDOFParam2Slider.setLocation(new Point(686, 2));
      dofDOFParam2Slider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      bokehSettingsPnl.add(dofDOFParam2Slider);
      bokehSettingsPnl.add(getResetBokehOptionsButton());
    }
    return bokehSettingsPnl;
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
      bokehBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      //bokehBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/games-config-background.png")));
      bokehBtn.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
    }
    return bokehBtn;
  }

  private JButton getResetCameraSettingsBtn() {
    if (resetCameraSettingsBtn == null) {
      resetCameraSettingsBtn = new JButton();
      resetCameraSettingsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetCameraSettings();
        }
      });
      resetCameraSettingsBtn.setToolTipText("Reset the camera-options to the defaults");
      resetCameraSettingsBtn.setText("Reset");
      resetCameraSettingsBtn.setPreferredSize(new Dimension(125, 24));
      resetCameraSettingsBtn.setMinimumSize(new Dimension(100, 24));
      resetCameraSettingsBtn.setMaximumSize(new Dimension(32000, 24));
      resetCameraSettingsBtn.setIconTextGap(2);
      resetCameraSettingsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetCameraSettingsBtn.setBounds(71, 100, 100, 24);
      resetCameraSettingsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetCameraSettingsBtn;
  }

  private JButton getResetDOFSettingsButton() {
    if (resetDOFSettingsButton == null) {
      resetDOFSettingsButton = new JButton();
      resetDOFSettingsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetDOFSettings();
        }
      });
      resetDOFSettingsButton.setToolTipText("Reset the DOF-options to the defaults");
      resetDOFSettingsButton.setText("Reset");
      resetDOFSettingsButton.setPreferredSize(new Dimension(125, 24));
      resetDOFSettingsButton.setMinimumSize(new Dimension(100, 24));
      resetDOFSettingsButton.setMaximumSize(new Dimension(32000, 24));
      resetDOFSettingsButton.setIconTextGap(2);
      resetDOFSettingsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetDOFSettingsButton.setBounds(102, 99, 100, 24);
      resetDOFSettingsButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetDOFSettingsButton;
  }

  private JButton getResetBokehOptionsButton() {
    if (resetBokehOptionsButton == null) {
      resetBokehOptionsButton = new JButton();
      resetBokehOptionsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetBokehSettings();
        }
      });
      resetBokehOptionsButton.setToolTipText("Reset the camera-options to the defaults");
      resetBokehOptionsButton.setText("Reset");
      resetBokehOptionsButton.setPreferredSize(new Dimension(125, 24));
      resetBokehOptionsButton.setMinimumSize(new Dimension(100, 24));
      resetBokehOptionsButton.setMaximumSize(new Dimension(32000, 24));
      resetBokehOptionsButton.setIconTextGap(2);
      resetBokehOptionsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetBokehOptionsButton.setBounds(896, 5, 100, 24);
      resetBokehOptionsButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetBokehOptionsButton;
  }

  private JButton getResetColoringOptionsButton() {
    if (resetColoringOptionsButton == null) {
      resetColoringOptionsButton = new JButton();
      resetColoringOptionsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetColoringSettings();
        }
      });
      resetColoringOptionsButton.setToolTipText("Reset the coloring-options to the defaults");
      resetColoringOptionsButton.setText("Reset");
      resetColoringOptionsButton.setPreferredSize(new Dimension(125, 24));
      resetColoringOptionsButton.setMinimumSize(new Dimension(100, 24));
      resetColoringOptionsButton.setMaximumSize(new Dimension(32000, 24));
      resetColoringOptionsButton.setIconTextGap(2);
      resetColoringOptionsButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetColoringOptionsButton.setBounds(100, 119, 100, 24);
      resetColoringOptionsButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetColoringOptionsButton;
  }

  public JButton getResetAntialiasOptionsButton() {
    return resetAntialiasOptionsButton;
  }

  private JButton getResetPostBlurSettingsBtn() {
    if (resetPostBlurSettingsBtn == null) {
      resetPostBlurSettingsBtn = new JButton();
      resetPostBlurSettingsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetPostBlurSettings();
        }
      });
      resetPostBlurSettingsBtn.setToolTipText("Reset the shading-options to the defaults");
      resetPostBlurSettingsBtn.setText("Reset");
      resetPostBlurSettingsBtn.setPreferredSize(new Dimension(125, 24));
      resetPostBlurSettingsBtn.setMinimumSize(new Dimension(100, 24));
      resetPostBlurSettingsBtn.setMaximumSize(new Dimension(32000, 24));
      resetPostBlurSettingsBtn.setIconTextGap(2);
      resetPostBlurSettingsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetPostBlurSettingsBtn.setBounds(102, 81, 100, 24);
      resetPostBlurSettingsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetPostBlurSettingsBtn;
  }

  private JButton getResetStereo3DSettingsBtn() {
    if (resetStereo3DSettingsBtn == null) {
      resetStereo3DSettingsBtn = new JButton();
      resetStereo3DSettingsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetStereo3DSettings();
        }
      });
      resetStereo3DSettingsBtn.setToolTipText("Reset the stereo3d-options to the defaults");
      resetStereo3DSettingsBtn.setText("Reset");
      resetStereo3DSettingsBtn.setPreferredSize(new Dimension(125, 24));
      resetStereo3DSettingsBtn.setMinimumSize(new Dimension(100, 24));
      resetStereo3DSettingsBtn.setMaximumSize(new Dimension(32000, 24));
      resetStereo3DSettingsBtn.setIconTextGap(2);
      resetStereo3DSettingsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetStereo3DSettingsBtn.setBounds(102, 104, 100, 24);
      resetStereo3DSettingsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetStereo3DSettingsBtn;
  }

  private JButton getResetPostSymmetrySettingsBtn() {
    if (resetPostSymmetrySettingsBtn == null) {
      resetPostSymmetrySettingsBtn = new JButton();
      resetPostSymmetrySettingsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetPostSymmetrySettings();
        }
      });
      resetPostSymmetrySettingsBtn.setToolTipText("Reset the post-symmetry-options to the defaults");
      resetPostSymmetrySettingsBtn.setText("Reset");
      resetPostSymmetrySettingsBtn.setPreferredSize(new Dimension(125, 24));
      resetPostSymmetrySettingsBtn.setMinimumSize(new Dimension(100, 24));
      resetPostSymmetrySettingsBtn.setMaximumSize(new Dimension(32000, 24));
      resetPostSymmetrySettingsBtn.setIconTextGap(2);
      resetPostSymmetrySettingsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetPostSymmetrySettingsBtn.setBounds(102, 80, 100, 24);
      resetPostSymmetrySettingsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetPostSymmetrySettingsBtn;
  }

  private JButton getResetMotionBlurSettingsBtn() {
    if (resetMotionBlurSettingsBtn == null) {
      resetMotionBlurSettingsBtn = new JButton();
      resetMotionBlurSettingsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.resetMotionBlurSettings();
        }
      });
      resetMotionBlurSettingsBtn.setToolTipText("Reset the motion-blur-options to the defaults");
      resetMotionBlurSettingsBtn.setText("Reset");
      resetMotionBlurSettingsBtn.setPreferredSize(new Dimension(125, 24));
      resetMotionBlurSettingsBtn.setMinimumSize(new Dimension(100, 24));
      resetMotionBlurSettingsBtn.setMaximumSize(new Dimension(32000, 24));
      resetMotionBlurSettingsBtn.setIconTextGap(2);
      resetMotionBlurSettingsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetMotionBlurSettingsBtn.setBounds(102, 99, 100, 24);
      resetMotionBlurSettingsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetMotionBlurSettingsBtn;
  }

  public JRadioButton getXaosViewAsToBtn() {
    return xaosViewAsToBtn;
  }

  public JRadioButton getXaosViewAsFromBtn() {
    return xaosViewAsFromBtn;
  }

  public JToggleButton getToggleDrawGuidesButton() {
    return toggleDrawGuidesButton;
  }

  private JPanel getMacroButtonRootPanel() {
    if (macroButtonRootPanel == null) {
      macroButtonRootPanel = new JPanel();
      macroButtonRootPanel.setBorder(null);
      macroButtonRootPanel.setLayout(new BorderLayout(0, 0));

      JScrollPane macroButtonsScrollPane = new JScrollPane();
      macroButtonsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      macroButtonRootPanel.add(macroButtonsScrollPane, BorderLayout.CENTER);

      macroButtonPanel = new JPanel();
      FlowLayout flowLayout = (FlowLayout) macroButtonPanel.getLayout();
      flowLayout.setAlignment(FlowLayout.LEFT);
      flowLayout.setVgap(1);
      macroButtonsScrollPane.setViewportView(macroButtonPanel);
    }
    return macroButtonRootPanel;
  }

  public JPanel getPreviewEastDefaultPanel() {
    return previewEastDefaultPanel;
  }

  private JButton getScriptAddButtonBtn() {
    if (scriptAddButtonBtn == null) {
      scriptAddButtonBtn = new JButton();
      scriptAddButtonBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().addMacroButtonBtn_clicked();
        }
      });
      scriptAddButtonBtn.setToolTipText("Add this script to the macro-toolbar");
      scriptAddButtonBtn.setText("Add macro button");
      scriptAddButtonBtn.setPreferredSize(new Dimension(116, 24));
      scriptAddButtonBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      scriptAddButtonBtn.setBounds(new Rectangle(9, 280, 125, 24));
    }
    return scriptAddButtonBtn;
  }

  private JPanel getPanel_108() {
    if (panel_108 == null) {
      panel_108 = new JPanel();
      panel_108.setPreferredSize(new Dimension(116, 8));
      panel_108.setMinimumSize(new Dimension(10, 8));
      panel_108.setMaximumSize(new Dimension(32767, 8));
    }
    return panel_108;
  }

  public JTable getMacroButtonsTable() {
    return macroButtonsTable;
  }

  public JButton getMacroButtonMoveUpBtn() {
    return macroButtonMoveUpBtn;
  }

  public JButton getMacroButtonDeleteBtn() {
    return macroButtonDeleteBtn;
  }

  public JButton getMacroButtonMoveDownBtn() {
    return macroButtonMoveDownBtn;
  }

  public JPanel getMacroButtonPanel() {
    return macroButtonPanel;
  }

  private JToggleButton getToggleDetachedPreviewButton() {
    if (toggleDetachedPreviewButton == null) {
      toggleDetachedPreviewButton = new JToggleButton();
      toggleDetachedPreviewButton.setMnemonic(KeyEvent.VK_V);
      toggleDetachedPreviewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (toggleDetachedPreviewButton.isSelected()) {
            tinaController.openDetachedPreview();
          }
          else {
            tinaController.closeDetachedPreview();
          }
        }
      });
      toggleDetachedPreviewButton.setToolTipText("Additionally show changes in external window (Press <Alt+V>)");

      toggleDetachedPreviewButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/view-preview.png")));
      toggleDetachedPreviewButton.setPreferredSize(new Dimension(42, 24));
    }
    return toggleDetachedPreviewButton;
  }

  private JButton getGradientResetBtn() {
    if (gradientResetBtn == null) {
      gradientResetBtn = new JButton();
      gradientResetBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.gradientResetBtn_clicked();
        }
      });
      gradientResetBtn.setToolTipText("Reset the gradient to the initial state");
      gradientResetBtn.setText("R");
      gradientResetBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
      gradientResetBtn.setSize(new Dimension(128, 50));
      gradientResetBtn.setPreferredSize(new Dimension(60, 48));
      gradientResetBtn.setLocation(new Point(4, 181));
      gradientResetBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return gradientResetBtn;
  }

  public JWFNumberField getTinaWhiteLevelREd() {
    return tinaWhiteLevelREd;
  }

  public JSlider getTinaWhiteLevelSlider() {
    return tinaWhiteLevelSlider;
  }

  private JPanel getMacroButtonHorizRootPanel() {
    if (macroButtonHorizRootPanel == null) {
      macroButtonHorizRootPanel = new JPanel();
      macroButtonHorizRootPanel.setPreferredSize(new Dimension(10, 28));
      macroButtonHorizRootPanel.setLayout(new BorderLayout(0, 0));
      macroButtonHorizRootPanel.add(getMacroButtonsHorizScrollPane(), BorderLayout.CENTER);
    }
    return macroButtonHorizRootPanel;
  }

  private JScrollPane getMacroButtonsHorizScrollPane() {
    if (macroButtonsHorizScrollPane == null) {
      macroButtonsHorizScrollPane = new JScrollPane();
      macroButtonsHorizScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
      macroButtonsHorizScrollPane.setViewportView(getMacroButtonHorizPanel());
    }
    return macroButtonsHorizScrollPane;
  }

  private JPanel getMacroButtonHorizPanel() {
    if (macroButtonHorizPanel == null) {
      macroButtonHorizPanel = new JPanel();
      FlowLayout flowLayout = (FlowLayout) macroButtonHorizPanel.getLayout();
      flowLayout.setHgap(1);
      flowLayout.setVgap(0);
      macroButtonHorizPanel.setPreferredSize(new Dimension(10, 28));
    }
    return macroButtonHorizPanel;
  }

  public JButton getRandomizeColorSpeedButton() {
    return randomizeColorSpeedButton;
  }

  private JButton getTinaClearGradientImageButton() {
    if (tinaClearGradientImageButton == null) {
      tinaClearGradientImageButton = new JButton();
      tinaClearGradientImageButton.setBounds(334, 6, 148, 24);
      tinaClearGradientImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.clearImageForGradientButton_actionPerformed(e);
        }
      });
      tinaClearGradientImageButton.setToolTipText("Remove the currently used image");
      tinaClearGradientImageButton.setText("Remove image");
      tinaClearGradientImageButton.setPreferredSize(new Dimension(190, 24));
      tinaClearGradientImageButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return tinaClearGradientImageButton;
  }

  public JButton getBackgroundSelectImageBtn() {
    return backgroundSelectImageBtn;
  }

  public JButton getBackgroundRemoveImageBtn() {
    return backgroundRemoveImageBtn;
  }

  private JPanel getGradientColorMapPnl() {
    if (gradientColorMapPnl == null) {
      gradientColorMapPnl = new JPanel();
      gradientColorMapPnl.setLayout(null);
      gradientColorMapPnl.add(getLblHoffset());
      gradientColorMapPnl.add(getLblHscale());
      gradientColorMapPnl.add(getLblLocalCAdd());
      gradientColorMapPnl.add(getLblLocalScl());
      gradientColorMapPnl.add(getLblVoffset());
      gradientColorMapPnl.add(getLblVScale());
      gradientColorMapPnl.add(getLabel_15());
      gradientColorMapPnl.add(getLabel_16());
      gradientColorMapPnl.add(getGradientColorMapHorizOffsetREd());
      gradientColorMapPnl.add(getGradientColorMapHorizScaleREd());
      gradientColorMapPnl.add(getGradientColorMapLocalColorAddREd());
      gradientColorMapPnl.add(getGradientColorMapLocalColorScaleREd());
      gradientColorMapPnl.add(getGradientColorMapVertOffsetREd());
      gradientColorMapPnl.add(getGradientColorMapVertScaleREd());
      gradientColorMapPnl.add(getNumberField_6());
      gradientColorMapPnl.add(getNumberField_7());
      gradientColorMapPnl.add(getGradientColorMapHorizOffsetSlider());
      gradientColorMapPnl.add(getGradientColorMapHorizScaleSlider());
      gradientColorMapPnl.add(getGradientColorMapLocalColorAddSlider());
      gradientColorMapPnl.add(getGradientColorMapLocalColorScaleSlider());
      gradientColorMapPnl.add(getGradientColorMapVertOffsetSlider());
      gradientColorMapPnl.add(getGradientColorMapVertScaleSlider());
      gradientColorMapPnl.add(getSlider_6());
      gradientColorMapPnl.add(getSlider_7());

      JButton tinaSelectGradientImageButton = new JButton();
      tinaSelectGradientImageButton.setBounds(6, 6, 148, 24);
      gradientColorMapPnl.add(tinaSelectGradientImageButton);
      tinaSelectGradientImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.selectImageForGradientButton_actionPerformed(e);
        }
      });
      tinaSelectGradientImageButton.setToolTipText("Select an image to use as a 2D-gradient");
      tinaSelectGradientImageButton.setText("Select image...");
      tinaSelectGradientImageButton.setPreferredSize(new Dimension(190, 24));
      tinaSelectGradientImageButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      gradientColorMapPnl.add(getTinaClearGradientImageButton());
    }
    return gradientColorMapPnl;
  }

  private JLabel getLblHoffset() {
    if (lblHoffset == null) {
      lblHoffset = new JLabel();
      lblHoffset.setToolTipText("Horizontal offset");
      lblHoffset.setText("H Offset");
      lblHoffset.setSize(new Dimension(56, 22));
      lblHoffset.setPreferredSize(new Dimension(64, 22));
      lblHoffset.setLocation(new Point(6, 6));
      lblHoffset.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblHoffset.setBounds(6, 36, 56, 22);
    }
    return lblHoffset;
  }

  private JLabel getLblHscale() {
    if (lblHscale == null) {
      lblHscale = new JLabel();
      lblHscale.setToolTipText("Horizontal scale");
      lblHscale.setText("HScale");
      lblHscale.setSize(new Dimension(56, 22));
      lblHscale.setPreferredSize(new Dimension(64, 22));
      lblHscale.setLocation(new Point(6, 32));
      lblHscale.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblHscale.setBounds(6, 62, 56, 22);
    }
    return lblHscale;
  }

  private JLabel getLblLocalCAdd() {
    if (lblLocalCAdd == null) {
      lblLocalCAdd = new JLabel();
      lblLocalCAdd.setToolTipText("Local color add");
      lblLocalCAdd.setText("Local Add");
      lblLocalCAdd.setSize(new Dimension(56, 22));
      lblLocalCAdd.setPreferredSize(new Dimension(64, 22));
      lblLocalCAdd.setLocation(new Point(6, 58));
      lblLocalCAdd.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblLocalCAdd.setBounds(6, 88, 56, 22);
    }
    return lblLocalCAdd;
  }

  private JLabel getLblLocalScl() {
    if (lblLocalScl == null) {
      lblLocalScl = new JLabel();
      lblLocalScl.setToolTipText("Local color scale");
      lblLocalScl.setText("Local Scl");
      lblLocalScl.setSize(new Dimension(56, 22));
      lblLocalScl.setPreferredSize(new Dimension(64, 22));
      lblLocalScl.setLocation(new Point(6, 84));
      lblLocalScl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblLocalScl.setBounds(6, 114, 56, 22);
    }
    return lblLocalScl;
  }

  private JLabel getLblVoffset() {
    if (lblVoffset == null) {
      lblVoffset = new JLabel();
      lblVoffset.setToolTipText("Vertical offset");
      lblVoffset.setText("V Offset");
      lblVoffset.setSize(new Dimension(56, 22));
      lblVoffset.setPreferredSize(new Dimension(64, 22));
      lblVoffset.setLocation(new Point(334, 6));
      lblVoffset.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblVoffset.setBounds(334, 36, 56, 22);
    }
    return lblVoffset;
  }

  private JLabel getLblVScale() {
    if (lblVScale == null) {
      lblVScale = new JLabel();
      lblVScale.setText("V Scale");
      lblVScale.setSize(new Dimension(56, 22));
      lblVScale.setPreferredSize(new Dimension(64, 22));
      lblVScale.setLocation(new Point(334, 32));
      lblVScale.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblVScale.setBounds(334, 62, 56, 22);
    }
    return lblVScale;
  }

  private JLabel getLabel_15() {
    if (label_15 == null) {
      label_15 = new JLabel();
      label_15.setText(" ");
      label_15.setSize(new Dimension(56, 22));
      label_15.setPreferredSize(new Dimension(64, 22));
      label_15.setLocation(new Point(334, 58));
      label_15.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label_15.setBounds(334, 88, 56, 22);
    }
    return label_15;
  }

  private JLabel getLabel_16() {
    if (label_16 == null) {
      label_16 = new JLabel();
      label_16.setText(" ");
      label_16.setSize(new Dimension(56, 22));
      label_16.setPreferredSize(new Dimension(64, 22));
      label_16.setLocation(new Point(334, 84));
      label_16.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label_16.setBounds(334, 114, 56, 22);
    }
    return label_16;
  }

  private JWFNumberField getGradientColorMapHorizOffsetREd() {
    if (gradientColorMapHorizOffsetREd == null) {
      gradientColorMapHorizOffsetREd = new JWFNumberField();
      gradientColorMapHorizOffsetREd.setValueStep(0.05);
      gradientColorMapHorizOffsetREd.setText("0");
      gradientColorMapHorizOffsetREd.setSize(new Dimension(56, 24));
      gradientColorMapHorizOffsetREd.setPreferredSize(new Dimension(36, 22));
      gradientColorMapHorizOffsetREd.setMinValue(-255.0);
      gradientColorMapHorizOffsetREd.setMaxValue(255.0);
      gradientColorMapHorizOffsetREd.setLocation(new Point(62, 6));
      gradientColorMapHorizOffsetREd.setHasMinValue(true);
      gradientColorMapHorizOffsetREd.setHasMaxValue(true);
      gradientColorMapHorizOffsetREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      gradientColorMapHorizOffsetREd.setBounds(62, 36, 56, 24);
      gradientColorMapHorizOffsetREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!gradientColorMapHorizOffsetREd.isMouseAdjusting() || gradientColorMapHorizOffsetREd.getMouseChangeCount() == 0) {
            if (!gradientColorMapHorizOffsetSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.gradientColorMapHorizOffsetREd_changed();
        }
      });
    }
    return gradientColorMapHorizOffsetREd;
  }

  private JWFNumberField getGradientColorMapHorizScaleREd() {
    if (gradientColorMapHorizScaleREd == null) {
      gradientColorMapHorizScaleREd = new JWFNumberField();
      gradientColorMapHorizScaleREd.setValueStep(0.05);
      gradientColorMapHorizScaleREd.setText("0");
      gradientColorMapHorizScaleREd.setSize(new Dimension(56, 24));
      gradientColorMapHorizScaleREd.setPreferredSize(new Dimension(36, 22));
      gradientColorMapHorizScaleREd.setMinValue(-255.0);
      gradientColorMapHorizScaleREd.setMaxValue(255.0);
      gradientColorMapHorizScaleREd.setLocation(new Point(62, 32));
      gradientColorMapHorizScaleREd.setHasMinValue(true);
      gradientColorMapHorizScaleREd.setHasMaxValue(true);
      gradientColorMapHorizScaleREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      gradientColorMapHorizScaleREd.setBounds(62, 62, 56, 24);
      gradientColorMapHorizScaleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!gradientColorMapHorizScaleREd.isMouseAdjusting() || gradientColorMapHorizScaleREd.getMouseChangeCount() == 0) {
            if (!gradientColorMapHorizScaleSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.gradientColorMapHorizScaleREd_changed();
        }
      });
    }
    return gradientColorMapHorizScaleREd;
  }

  private JWFNumberField getGradientColorMapLocalColorAddREd() {
    if (gradientColorMapLocalColorAddREd == null) {
      gradientColorMapLocalColorAddREd = new JWFNumberField();
      gradientColorMapLocalColorAddREd.setValueStep(0.05);
      gradientColorMapLocalColorAddREd.setText("0");
      gradientColorMapLocalColorAddREd.setSize(new Dimension(56, 24));
      gradientColorMapLocalColorAddREd.setPreferredSize(new Dimension(36, 22));
      gradientColorMapLocalColorAddREd.setMinValue(-255.0);
      gradientColorMapLocalColorAddREd.setMaxValue(255.0);
      gradientColorMapLocalColorAddREd.setLocation(new Point(62, 58));
      gradientColorMapLocalColorAddREd.setHasMinValue(true);
      gradientColorMapLocalColorAddREd.setHasMaxValue(true);
      gradientColorMapLocalColorAddREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      gradientColorMapLocalColorAddREd.setBounds(62, 88, 56, 24);
      gradientColorMapLocalColorAddREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!gradientColorMapLocalColorAddREd.isMouseAdjusting() || gradientColorMapLocalColorAddREd.getMouseChangeCount() == 0) {
            if (!gradientColorMapLocalColorAddSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.gradientColorMapLocalColorAddREd_changed();
        }
      });
    }
    return gradientColorMapLocalColorAddREd;
  }

  private JWFNumberField getGradientColorMapLocalColorScaleREd() {
    if (gradientColorMapLocalColorScaleREd == null) {
      gradientColorMapLocalColorScaleREd = new JWFNumberField();
      gradientColorMapLocalColorScaleREd.setValueStep(0.05);
      gradientColorMapLocalColorScaleREd.setText("0");
      gradientColorMapLocalColorScaleREd.setSize(new Dimension(56, 24));
      gradientColorMapLocalColorScaleREd.setPreferredSize(new Dimension(36, 22));
      gradientColorMapLocalColorScaleREd.setMinValue(-255.0);
      gradientColorMapLocalColorScaleREd.setMaxValue(255.0);
      gradientColorMapLocalColorScaleREd.setLocation(new Point(62, 84));
      gradientColorMapLocalColorScaleREd.setHasMinValue(true);
      gradientColorMapLocalColorScaleREd.setHasMaxValue(true);
      gradientColorMapLocalColorScaleREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      gradientColorMapLocalColorScaleREd.setBounds(62, 114, 56, 24);
      gradientColorMapLocalColorScaleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!gradientColorMapLocalColorScaleREd.isMouseAdjusting() || gradientColorMapLocalColorScaleREd.getMouseChangeCount() == 0) {
            if (!gradientColorMapLocalColorScaleSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.gradientColorMapLocalColorScaleREd_changed();
        }
      });
    }
    return gradientColorMapLocalColorScaleREd;
  }

  private JWFNumberField getGradientColorMapVertOffsetREd() {
    if (gradientColorMapVertOffsetREd == null) {
      gradientColorMapVertOffsetREd = new JWFNumberField();
      gradientColorMapVertOffsetREd.setValueStep(0.05);
      gradientColorMapVertOffsetREd.setText("0");
      gradientColorMapVertOffsetREd.setSize(new Dimension(56, 24));
      gradientColorMapVertOffsetREd.setPreferredSize(new Dimension(36, 22));
      gradientColorMapVertOffsetREd.setMinValue(-255.0);
      gradientColorMapVertOffsetREd.setMaxValue(255.0);
      gradientColorMapVertOffsetREd.setLocation(new Point(390, 6));
      gradientColorMapVertOffsetREd.setHasMinValue(true);
      gradientColorMapVertOffsetREd.setHasMaxValue(true);
      gradientColorMapVertOffsetREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      gradientColorMapVertOffsetREd.setBounds(390, 36, 56, 24);
      gradientColorMapVertOffsetREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!gradientColorMapVertOffsetREd.isMouseAdjusting() || gradientColorMapVertOffsetREd.getMouseChangeCount() == 0) {
            if (!gradientColorMapVertOffsetSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.gradientColorMapVertOffsetREd_changed();
        }
      });
    }
    return gradientColorMapVertOffsetREd;
  }

  private JWFNumberField getGradientColorMapVertScaleREd() {
    if (gradientColorMapVertScaleREd == null) {
      gradientColorMapVertScaleREd = new JWFNumberField();
      gradientColorMapVertScaleREd.setValueStep(0.05);
      gradientColorMapVertScaleREd.setText("0");
      gradientColorMapVertScaleREd.setSize(new Dimension(56, 24));
      gradientColorMapVertScaleREd.setPreferredSize(new Dimension(36, 22));
      gradientColorMapVertScaleREd.setMinValue(-255.0);
      gradientColorMapVertScaleREd.setMaxValue(255.0);
      gradientColorMapVertScaleREd.setLocation(new Point(390, 32));
      gradientColorMapVertScaleREd.setHasMinValue(true);
      gradientColorMapVertScaleREd.setHasMaxValue(true);
      gradientColorMapVertScaleREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      gradientColorMapVertScaleREd.setBounds(390, 62, 56, 24);
      gradientColorMapVertScaleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!gradientColorMapVertScaleREd.isMouseAdjusting() || gradientColorMapVertScaleREd.getMouseChangeCount() == 0) {
            if (!gradientColorMapVertScaleSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.gradientColorMapVertScaleREd_changed();
        }
      });
    }
    return gradientColorMapVertScaleREd;
  }

  private JWFNumberField getNumberField_6() {
    if (numberField_6 == null) {
      numberField_6 = new JWFNumberField();
      numberField_6.setVisible(false);
      numberField_6.setText("0");
      numberField_6.setSize(new Dimension(56, 24));
      numberField_6.setPreferredSize(new Dimension(36, 22));
      numberField_6.setMinValue(-255.0);
      numberField_6.setMaxValue(255.0);
      numberField_6.setLocation(new Point(390, 58));
      numberField_6.setHasMinValue(true);
      numberField_6.setHasMaxValue(true);
      numberField_6.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      numberField_6.setBounds(390, 88, 56, 24);
    }
    return numberField_6;
  }

  private JWFNumberField getNumberField_7() {
    if (numberField_7 == null) {
      numberField_7 = new JWFNumberField();
      numberField_7.setVisible(false);
      numberField_7.setText("0");
      numberField_7.setSize(new Dimension(56, 24));
      numberField_7.setPreferredSize(new Dimension(36, 22));
      numberField_7.setMinValue(-255.0);
      numberField_7.setMaxValue(255.0);
      numberField_7.setLocation(new Point(390, 84));
      numberField_7.setHasMinValue(true);
      numberField_7.setHasMaxValue(true);
      numberField_7.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      numberField_7.setBounds(390, 114, 56, 24);
    }
    return numberField_7;
  }

  private JSlider getGradientColorMapHorizOffsetSlider() {
    if (gradientColorMapHorizOffsetSlider == null) {
      gradientColorMapHorizOffsetSlider = new JSlider();
      gradientColorMapHorizOffsetSlider.setValue(0);
      gradientColorMapHorizOffsetSlider.setSize(new Dimension(204, 22));
      gradientColorMapHorizOffsetSlider.setPreferredSize(new Dimension(86, 22));
      gradientColorMapHorizOffsetSlider.setMinimum(-25000);
      gradientColorMapHorizOffsetSlider.setMaximum(25000);
      gradientColorMapHorizOffsetSlider.setLocation(new Point(118, 6));
      gradientColorMapHorizOffsetSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      gradientColorMapHorizOffsetSlider.setBounds(118, 36, 204, 22);
      gradientColorMapHorizOffsetSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      gradientColorMapHorizOffsetSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.gradientColorMapHorizOffsetSlider_changed();
        }
      });
    }
    return gradientColorMapHorizOffsetSlider;
  }

  private JSlider getGradientColorMapHorizScaleSlider() {
    if (gradientColorMapHorizScaleSlider == null) {
      gradientColorMapHorizScaleSlider = new JSlider();
      gradientColorMapHorizScaleSlider.setValue(0);
      gradientColorMapHorizScaleSlider.setSize(new Dimension(204, 22));
      gradientColorMapHorizScaleSlider.setPreferredSize(new Dimension(86, 22));
      gradientColorMapHorizScaleSlider.setMaximum(25000);
      gradientColorMapHorizScaleSlider.setLocation(new Point(118, 32));
      gradientColorMapHorizScaleSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      gradientColorMapHorizScaleSlider.setBounds(118, 62, 204, 22);
      gradientColorMapHorizScaleSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      gradientColorMapHorizScaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.gradientColorMapHorizScaleSlider_changed();
        }
      });
    }
    return gradientColorMapHorizScaleSlider;
  }

  private JSlider getGradientColorMapLocalColorAddSlider() {
    if (gradientColorMapLocalColorAddSlider == null) {
      gradientColorMapLocalColorAddSlider = new JSlider();
      gradientColorMapLocalColorAddSlider.setValue(0);
      gradientColorMapLocalColorAddSlider.setSize(new Dimension(204, 22));
      gradientColorMapLocalColorAddSlider.setPreferredSize(new Dimension(86, 22));
      gradientColorMapLocalColorAddSlider.setMinimum(-25000);
      gradientColorMapLocalColorAddSlider.setMaximum(25000);
      gradientColorMapLocalColorAddSlider.setLocation(new Point(118, 58));
      gradientColorMapLocalColorAddSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      gradientColorMapLocalColorAddSlider.setBounds(118, 88, 204, 22);
      gradientColorMapLocalColorAddSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      gradientColorMapLocalColorAddSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.gradientColorMapLocalColorAddSlider_changed();
        }
      });
    }
    return gradientColorMapLocalColorAddSlider;
  }

  private JSlider getGradientColorMapLocalColorScaleSlider() {
    if (gradientColorMapLocalColorScaleSlider == null) {
      gradientColorMapLocalColorScaleSlider = new JSlider();
      gradientColorMapLocalColorScaleSlider.setValue(0);
      gradientColorMapLocalColorScaleSlider.setSize(new Dimension(204, 22));
      gradientColorMapLocalColorScaleSlider.setPreferredSize(new Dimension(86, 22));
      gradientColorMapLocalColorScaleSlider.setMinimum(-25000);
      gradientColorMapLocalColorScaleSlider.setMaximum(25000);
      gradientColorMapLocalColorScaleSlider.setLocation(new Point(118, 84));
      gradientColorMapLocalColorScaleSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      gradientColorMapLocalColorScaleSlider.setBounds(118, 114, 204, 22);
      gradientColorMapLocalColorScaleSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      gradientColorMapLocalColorScaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.gradientColorMapLocalColorScaleSlider_changed();
        }
      });
    }
    return gradientColorMapLocalColorScaleSlider;
  }

  private JSlider getGradientColorMapVertOffsetSlider() {
    if (gradientColorMapVertOffsetSlider == null) {
      gradientColorMapVertOffsetSlider = new JSlider();
      gradientColorMapVertOffsetSlider.setValue(0);
      gradientColorMapVertOffsetSlider.setSize(new Dimension(204, 22));
      gradientColorMapVertOffsetSlider.setPreferredSize(new Dimension(86, 22));
      gradientColorMapVertOffsetSlider.setMinimum(-25000);
      gradientColorMapVertOffsetSlider.setMaximum(25000);
      gradientColorMapVertOffsetSlider.setLocation(new Point(446, 6));
      gradientColorMapVertOffsetSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      gradientColorMapVertOffsetSlider.setBounds(446, 36, 204, 22);
      gradientColorMapVertOffsetSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      gradientColorMapVertOffsetSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.gradientColorMapVertOffsetSlider_changed();
        }
      });
    }
    return gradientColorMapVertOffsetSlider;
  }

  private JSlider getGradientColorMapVertScaleSlider() {
    if (gradientColorMapVertScaleSlider == null) {
      gradientColorMapVertScaleSlider = new JSlider();
      gradientColorMapVertScaleSlider.setValue(0);
      gradientColorMapVertScaleSlider.setSize(new Dimension(204, 22));
      gradientColorMapVertScaleSlider.setPreferredSize(new Dimension(86, 22));
      gradientColorMapVertScaleSlider.setMaximum(25000);
      gradientColorMapVertScaleSlider.setLocation(new Point(446, 32));
      gradientColorMapVertScaleSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      gradientColorMapVertScaleSlider.setBounds(446, 62, 204, 22);
      gradientColorMapVertScaleSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      gradientColorMapVertScaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.gradientColorMapVertScaleSlider_changed();
        }
      });
    }
    return gradientColorMapVertScaleSlider;
  }

  private JSlider getSlider_6() {
    if (slider_6 == null) {
      slider_6 = new JSlider();
      slider_6.setVisible(false);
      slider_6.setValue(0);
      slider_6.setSize(new Dimension(204, 22));
      slider_6.setPreferredSize(new Dimension(86, 22));
      slider_6.setMinimum(-255);
      slider_6.setMaximum(255);
      slider_6.setLocation(new Point(446, 58));
      slider_6.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      slider_6.setBounds(446, 88, 204, 22);
    }
    return slider_6;
  }

  private JSlider getSlider_7() {
    if (slider_7 == null) {
      slider_7 = new JSlider();
      slider_7.setVisible(false);
      slider_7.setValue(0);
      slider_7.setSize(new Dimension(204, 22));
      slider_7.setPreferredSize(new Dimension(86, 22));
      slider_7.setMinimum(-255);
      slider_7.setMaximum(255);
      slider_7.setLocation(new Point(446, 84));
      slider_7.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      slider_7.setBounds(446, 114, 204, 22);
    }
    return slider_7;
  }

  public JWFNumberField getFlameFPSField() {
    return flameFPSField;
  }

  private JToggleButton getLeapMotionToggleButton() {
    if (leapMotionToggleButton == null) {
      leapMotionToggleButton = new JToggleButton();
      leapMotionToggleButton.setText("Record");
      leapMotionToggleButton.setBounds(6, 6, 100, 42);
      leapMotionToggleButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getLeapMotionMainEditorController().toggleLeapMotionMode();
        }
      });
      leapMotionToggleButton.setToolTipText("Control the current fractal by using the Leap Motion controller");
      leapMotionToggleButton.setPreferredSize(new Dimension(72, 42));
      leapMotionToggleButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/video-x-generic-2.png")));
      leapMotionToggleButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return leapMotionToggleButton;
  }

  private JPanel getPanel_111() {
    if (panel_111 == null) {
      panel_111 = new JPanel();
      panel_111.setLayout(new BorderLayout(0, 0));
      panel_111.add(getPanel_112(), BorderLayout.NORTH);

      JPanel panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(10, 8));
      panel_111.add(panel_1, BorderLayout.SOUTH);

      JScrollPane scrollPane_1 = new JScrollPane();
      panel_111.add(scrollPane_1, BorderLayout.CENTER);

      leapMotionConfigTable = new JTable();
      leapMotionConfigTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting() && tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().configTableClicked();
          }
        }

      });

      scrollPane_1.setViewportView(leapMotionConfigTable);
    }
    return panel_111;
  }

  private JPanel getPanel_112() {
    if (panel_112 == null) {
      panel_112 = new JPanel();
      panel_112.setPreferredSize(new Dimension(10, 64));
      panel_112.setLayout(null);

      leapMotionIndex1Field = new JWFNumberField();
      leapMotionIndex1Field.setHasMinValue(true);
      leapMotionIndex1Field.setOnlyIntegers(true);
      leapMotionIndex1Field.setValueStep(1.0);
      leapMotionIndex1Field.setText("");
      leapMotionIndex1Field.setSize(new Dimension(100, 24));
      leapMotionIndex1Field.setPreferredSize(new Dimension(100, 24));
      leapMotionIndex1Field.setLocation(new Point(584, 2));
      leapMotionIndex1Field.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      leapMotionIndex1Field.setEditable(true);
      leapMotionIndex1Field.setBounds(357, 34, 60, 24);
      leapMotionIndex1Field.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().leapMotionIndex1Field_changed();
          }
        }
      });

      panel_112.add(leapMotionIndex1Field);

      leapMotionHandCmb = new JComboBox();
      leapMotionHandCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().leapMotionHandCmb_changed();
          }
        }
      });
      leapMotionHandCmb.setSize(new Dimension(125, 22));
      leapMotionHandCmb.setPreferredSize(new Dimension(125, 22));
      leapMotionHandCmb.setLocation(new Point(100, 4));
      leapMotionHandCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      leapMotionHandCmb.setBounds(96, 7, 135, 24);
      panel_112.add(leapMotionHandCmb);
      panel_112.add(getLeapMotionInputChannelCmb());
      panel_112.add(getLblInputChannel());
      panel_112.add(getLblFlameproperty());
      panel_112.add(getLeapMotionOutputChannelCmb());
      panel_112.add(getLblFlamepropertyindex());
      panel_112.add(getLblHand());

      leapMotionInvScaleField = new JWFNumberField();
      leapMotionInvScaleField.setValueStep(10.0);
      leapMotionInvScaleField.setText("");
      leapMotionInvScaleField.setSize(new Dimension(100, 24));
      leapMotionInvScaleField.setPreferredSize(new Dimension(100, 24));
      leapMotionInvScaleField.setLocation(new Point(584, 2));
      leapMotionInvScaleField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      leapMotionInvScaleField.setEditable(true);
      leapMotionInvScaleField.setBounds(616, 7, 100, 24);
      leapMotionInvScaleField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().leapMotionInvScaleField_changed();
          }
        }
      });
      panel_112.add(leapMotionInvScaleField);

      JLabel lblAmplitude = new JLabel();
      lblAmplitude.setText("1/Intensity");
      lblAmplitude.setSize(new Dimension(94, 22));
      lblAmplitude.setPreferredSize(new Dimension(94, 22));
      lblAmplitude.setLocation(new Point(488, 2));
      lblAmplitude.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblAmplitude.setBounds(555, 9, 60, 22);
      panel_112.add(lblAmplitude);

      JLabel lblBias = new JLabel();
      lblBias.setVisible(false);
      lblBias.setText("Bias");
      lblBias.setSize(new Dimension(94, 22));
      lblBias.setPreferredSize(new Dimension(94, 22));
      lblBias.setLocation(new Point(488, 2));
      lblBias.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBias.setBounds(555, 36, 60, 22);
      panel_112.add(lblBias);

      leapMotionOffsetField = new JWFNumberField();
      leapMotionOffsetField.setVisible(false);
      leapMotionOffsetField.setValueStep(0.05);
      leapMotionOffsetField.setText("");
      leapMotionOffsetField.setSize(new Dimension(100, 24));
      leapMotionOffsetField.setPreferredSize(new Dimension(100, 24));
      leapMotionOffsetField.setLocation(new Point(584, 2));
      leapMotionOffsetField.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      leapMotionOffsetField.setEditable(true);
      leapMotionOffsetField.setBounds(616, 34, 100, 24);
      leapMotionOffsetField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().leapMotionOffsetField_changed();
          }
        }
      });
      panel_112.add(leapMotionOffsetField);

      leapMotionIndex2Field = new JWFNumberField();
      leapMotionIndex2Field.setValueStep(1.0);
      leapMotionIndex2Field.setText("");
      leapMotionIndex2Field.setSize(new Dimension(100, 24));
      leapMotionIndex2Field.setPreferredSize(new Dimension(100, 24));
      leapMotionIndex2Field.setOnlyIntegers(true);
      leapMotionIndex2Field.setLocation(new Point(584, 2));
      leapMotionIndex2Field.setHasMinValue(true);
      leapMotionIndex2Field.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      leapMotionIndex2Field.setEditable(true);
      leapMotionIndex2Field.setBounds(417, 34, 60, 24);
      leapMotionIndex2Field.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().leapMotionIndex2Field_changed();
          }
        }
      });
      panel_112.add(leapMotionIndex2Field);

      leapMotionIndex3Field = new JWFNumberField();
      leapMotionIndex3Field.setValueStep(1.0);
      leapMotionIndex3Field.setText("");
      leapMotionIndex3Field.setSize(new Dimension(100, 24));
      leapMotionIndex3Field.setPreferredSize(new Dimension(100, 24));
      leapMotionIndex3Field.setOnlyIntegers(true);
      leapMotionIndex3Field.setLocation(new Point(584, 2));
      leapMotionIndex3Field.setHasMinValue(true);
      leapMotionIndex3Field.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      leapMotionIndex3Field.setEditable(true);
      leapMotionIndex3Field.setBounds(477, 34, 60, 24);
      leapMotionIndex3Field.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().leapMotionIndex3Field_changed();
          }
        }
      });
      panel_112.add(leapMotionIndex3Field);
    }
    return panel_112;
  }

  public JTable getLeapMotionConfigTable() {
    return leapMotionConfigTable;
  }

  public JComboBox getLeapMotionHandCmb() {
    return leapMotionHandCmb;
  }

  private JComboBox getLeapMotionInputChannelCmb() {
    if (leapMotionInputChannelCmb == null) {
      leapMotionInputChannelCmb = new JComboBox();
      leapMotionInputChannelCmb.setSize(new Dimension(125, 22));
      leapMotionInputChannelCmb.setPreferredSize(new Dimension(125, 22));
      leapMotionInputChannelCmb.setLocation(new Point(100, 4));
      leapMotionInputChannelCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      leapMotionInputChannelCmb.setBounds(96, 34, 135, 24);
      leapMotionInputChannelCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().leapMotionInputChannelCmb_changed();
          }
        }
      });
    }
    return leapMotionInputChannelCmb;
  }

  private JLabel getLblInputChannel() {
    if (lblInputChannel == null) {
      lblInputChannel = new JLabel();
      lblInputChannel.setText("Motion-property");
      lblInputChannel.setSize(new Dimension(94, 22));
      lblInputChannel.setPreferredSize(new Dimension(94, 22));
      lblInputChannel.setLocation(new Point(488, 2));
      lblInputChannel.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblInputChannel.setBounds(4, 36, 86, 22);
    }
    return lblInputChannel;
  }

  private JLabel getLblFlameproperty() {
    if (lblFlameproperty == null) {
      lblFlameproperty = new JLabel();
      lblFlameproperty.setText("Linked Flame-property");
      lblFlameproperty.setSize(new Dimension(94, 22));
      lblFlameproperty.setPreferredSize(new Dimension(94, 22));
      lblFlameproperty.setLocation(new Point(488, 2));
      lblFlameproperty.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblFlameproperty.setBounds(242, 9, 114, 22);
    }
    return lblFlameproperty;
  }

  private JComboBox getLeapMotionOutputChannelCmb() {
    if (leapMotionOutputChannelCmb == null) {
      leapMotionOutputChannelCmb = new JComboBox();
      leapMotionOutputChannelCmb.setSize(new Dimension(125, 22));
      leapMotionOutputChannelCmb.setPreferredSize(new Dimension(125, 22));
      leapMotionOutputChannelCmb.setLocation(new Point(100, 4));
      leapMotionOutputChannelCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      leapMotionOutputChannelCmb.setBounds(357, 7, 180, 24);
      leapMotionOutputChannelCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getLeapMotionMainEditorController() != null) {
            tinaController.getLeapMotionMainEditorController().leapMotionOutputChannelCmb_changed();
          }
        }
      });
    }
    return leapMotionOutputChannelCmb;
  }

  private JLabel getLblFlamepropertyindex() {
    if (lblFlamepropertyindex == null) {
      lblFlamepropertyindex = new JLabel();
      lblFlamepropertyindex.setText("Flame-property-index");
      lblFlamepropertyindex.setSize(new Dimension(94, 22));
      lblFlamepropertyindex.setPreferredSize(new Dimension(94, 22));
      lblFlamepropertyindex.setLocation(new Point(488, 2));
      lblFlamepropertyindex.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblFlamepropertyindex.setBounds(242, 36, 114, 22);
    }
    return lblFlamepropertyindex;
  }

  private JLabel getLblHand() {
    if (lblHand == null) {
      lblHand = new JLabel();
      lblHand.setText("Hand");
      lblHand.setSize(new Dimension(94, 22));
      lblHand.setPreferredSize(new Dimension(94, 22));
      lblHand.setLocation(new Point(488, 2));
      lblHand.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblHand.setBounds(4, 8, 86, 22);
    }
    return lblHand;
  }

  public JWFNumberField getLeapMotionIndex1Field() {
    return leapMotionIndex1Field;
  }

  public JWFNumberField getLeapMotionInvScaleField() {
    return leapMotionInvScaleField;
  }

  public JWFNumberField getLeapMotionOffsetField() {
    return leapMotionOffsetField;
  }

  public JButton getLeapMotionAddButton() {
    return leapMotionAddButton;
  }

  public JButton getLeapMotionDuplicateButton() {
    return leapMotionDuplicateButton;
  }

  public JButton getLeapMotionDeleteButton() {
    return leapMotionDeleteButton;
  }

  public JButton getLeapMotionClearButton() {
    return leapMotionClearButton;
  }

  public JWFNumberField getLeapMotionIndex2Field() {
    return leapMotionIndex2Field;
  }

  public JWFNumberField getLeapMotionIndex3Field() {
    return leapMotionIndex3Field;
  }

  public JButton getLeapMotionResetConfigButton() {
    return leapMotionResetConfigButton;
  }

  public JWFNumberField getTinaSpatialOversamplingREd() {
    return tinaSpatialOversamplingREd;
  }

  public JSlider getTinaSpatialOversamplingSlider() {
    return tinaSpatialOversamplingSlider;
  }

  public JPanel getFilterKernelPreviewRootPnl() {
    return filterKernelPreviewRootPnl;
  }

  public JToggleButton getFilterKernelFlatPreviewBtn() {
    return filterKernelFlatPreviewBtn;
  }

  public JCheckBox getTinaPostNoiseFilterCheckBox() {
    return tinaPostNoiseFilterCheckBox;
  }

  public JWFNumberField getTinaPostNoiseThresholdField() {
    return tinaPostNoiseThresholdField;
  }

  public JSlider getTinaPostNoiseThresholdSlider() {
    return tinaPostNoiseThresholdSlider;
  }

  public JWFNumberField getForegroundOpacityField() {
    return foregroundOpacityField;
  }

  public JSlider getForegroundOpacitySlider() {
    return foregroundOpacitySlider;
  }

  private JWFNumberField getXFormAntialiasAmountREd() {
    if (xFormAntialiasAmountREd == null) {
      xFormAntialiasAmountREd = new JWFNumberField();
      xFormAntialiasAmountREd.setMouseSpeed(0.1);
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
      xFormAntialiasAmountREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormAntialiasAmountREd.setBounds(675, 2, 100, 22);
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
      xFormAntialiasAmountLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormAntialiasAmountLbl.setBounds(565, 2, 113, 22);
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
      xFormAntialiasAmountSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormAntialiasAmountSlider.setBounds(777, 2, 220, 22);
    }
    return xFormAntialiasAmountSlider;
  }

  private JWFNumberField getXFormAntialiasRadiusREd() {
    if (xFormAntialiasRadiusREd == null) {
      xFormAntialiasRadiusREd = new JWFNumberField();
      xFormAntialiasRadiusREd.setMouseSpeed(0.1);
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
      xFormAntialiasRadiusREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      xFormAntialiasRadiusREd.setBounds(675, 28, 100, 22);
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
      xFormAntialiasRadiusLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormAntialiasRadiusLbl.setBounds(565, 28, 113, 22);
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
      xFormAntialiasRadiusSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      xFormAntialiasRadiusSlider.setBounds(777, 28, 220, 22);
    }
    return xFormAntialiasRadiusSlider;
  }

  private JButton getScriptEditBtn() {
    if (scriptEditBtn == null) {
      scriptEditBtn = new JButton();
      scriptEditBtn.setToolTipText("Edit script in a separate window");
      scriptEditBtn.setText("Edit...");
      scriptEditBtn.setPreferredSize(new Dimension(116, 24));
      scriptEditBtn.setMnemonic('u');
      scriptEditBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      scriptEditBtn.setBounds(new Rectangle(9, 280, 125, 24));
      scriptEditBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getJwfScriptController().editScriptBtn_clicked();
        }
      });
    }
    return scriptEditBtn;
  }

  private JPanel getPanel_113() {
    if (panel_113 == null) {
      panel_113 = new JPanel();
      panel_113.setPreferredSize(new Dimension(116, 8));
      panel_113.setMinimumSize(new Dimension(10, 8));
      panel_113.setMaximumSize(new Dimension(32767, 8));
    }
    return panel_113;
  }

  public JToggleButton getNonlinearParams1PreButton() {
    return nonlinearParams1PreButton;
  }

  public JToggleButton getNonlinearParams1PostButton() {
    return nonlinearParams1PostButton;
  }

  public JButton getNonlinearParams11UpButton() {
    return nonlinearParams11UpButton;
  }

  public JToggleButton getNonlinearParams9PreButton() {
    return nonlinearParams9PreButton;
  }

  public JToggleButton getNonlinearParams2PostButton() {
    return nonlinearParams2PostButton;
  }

  public JToggleButton getNonlinearParams10PostButton() {
    return nonlinearParams10PostButton;
  }

  public JButton getNonlinearParams7UpButton() {
    return nonlinearParams7UpButton;
  }

  public JToggleButton getNonlinearParams8PostButton() {
    return nonlinearParams8PostButton;
  }

  public JButton getNonlinearParams12UpButton() {
    return nonlinearParams12UpButton;
  }

  public JToggleButton getNonlinearParams5PostButton() {
    return nonlinearParams5PostButton;
  }

  public JToggleButton getNonlinearParams12PreButton() {
    return nonlinearParams12PreButton;
  }

  public JToggleButton getNonlinearParams5PreButton() {
    return nonlinearParams5PreButton;
  }

  public JButton getNonlinearParams4UpButton() {
    return nonlinearParams4UpButton;
  }

  public JButton getNonlinearParams6UpButton() {
    return nonlinearParams6UpButton;
  }

  public JToggleButton getNonlinearParams7PreButton() {
    return nonlinearParams7PreButton;
  }

  public JToggleButton getNonlinearParams8PreButton() {
    return nonlinearParams8PreButton;
  }

  public JButton getNonlinearParams9UpButton() {
    return nonlinearParams9UpButton;
  }

  public JButton getNonlinearParams10UpButton() {
    return nonlinearParams10UpButton;
  }

  public JToggleButton getNonlinearParams12PostButton() {
    return nonlinearParams12PostButton;
  }

  public JToggleButton getNonlinearParams2PreButton() {
    return nonlinearParams2PreButton;
  }

  public JToggleButton getNonlinearParams6PreButton() {
    return nonlinearParams6PreButton;
  }

  public JToggleButton getNonlinearParams4PreButton() {
    return nonlinearParams4PreButton;
  }

  public JToggleButton getNonlinearParams4PostButton() {
    return nonlinearParams4PostButton;
  }

  public JToggleButton getNonlinearParams6PostButton() {
    return nonlinearParams6PostButton;
  }

  public JToggleButton getNonlinearParams11PreButton() {
    return nonlinearParams11PreButton;
  }

  public JButton getNonlinearParams2UpButton() {
    return nonlinearParams2UpButton;
  }

  public JToggleButton getNonlinearParams10PreButton() {
    return nonlinearParams10PreButton;
  }

  public JButton getNonlinearParams5UpButton() {
    return nonlinearParams5UpButton;
  }

  public JToggleButton getNonlinearParams9PostButton() {
    return nonlinearParams9PostButton;
  }

  public JButton getNonlinearParams8UpButton() {
    return nonlinearParams8UpButton;
  }

  public JButton getNonlinearParams3UpButton() {
    return nonlinearParams3UpButton;
  }

  public JToggleButton getNonlinearParams3PreButton() {
    return nonlinearParams3PreButton;
  }

  public JToggleButton getNonlinearParams11PostButton() {
    return nonlinearParams11PostButton;
  }

  public JToggleButton getNonlinearParams7PostButton() {
    return nonlinearParams7PostButton;
  }

  public JToggleButton getNonlinearParams3PostButton() {
    return nonlinearParams3PostButton;
  }

  private JButton getFlameToBatchButton() {
    if (flameToBatchButton == null) {
      flameToBatchButton = new JButton();
      flameToBatchButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.sendCurrentFlameToBatchRenderer();
        }
      });
      flameToBatchButton.setToolTipText("Send the current flame to the Batch Renderer (you must start the Batch Renderer later to actually render the fractal)");
      flameToBatchButton.setText("Batch Renderer");
      flameToBatchButton.setPreferredSize(new Dimension(115, 24));
      flameToBatchButton.setMinimumSize(new Dimension(125, 52));
      flameToBatchButton.setMaximumSize(new Dimension(32000, 52));
      flameToBatchButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      flameToBatchButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/images.png")));
    }
    return flameToBatchButton;
  }

  private JPanel getMainPrevievPnl() {
    if (mainPrevievPnl == null) {
      mainPrevievPnl = new JPanel();
      mainPrevievPnl.setLayout(null);
    }
    return mainPrevievPnl;
  }

  public JToggleButton getRealtimePreviewToggleButton() {
    return realtimePreviewToggleButton;
  }

  public JButton getRelWeightsResetButton() {
    return relWeightsResetButton;
  }

  public JButton getRelWeightsResetAllButton() {
    return relWeightsResetAllButton;
  }

  private JPanel getPanel_59() {
    if (panel_59 == null) {
      panel_59 = new JPanel();
      panel_59.setLayout(new BorderLayout(0, 0));
      panel_59.add(getTinaSolidRenderingPane(), BorderLayout.CENTER);
    }

    return panel_59;
  }

  private JTabbedPane getTinaSolidRenderingPane() {
    if (tinaSolidRenderingPane == null) {
      tinaSolidRenderingPane = new JTabbedPane(JTabbedPane.TOP);

      tinaSolidRenderingPane.addTab("Ambient shadows", null, getPanel(), null);
      tinaSolidRenderingPane.addTab("Hard shadows", null, getPanel_4(), null);

      JPanel tinaSolidRenderingMaterialPnl = new JPanel();
      tinaSolidRenderingPane.addTab("Material settings", null, tinaSolidRenderingMaterialPnl, null);
      tinaSolidRenderingMaterialPnl.setLayout(new BorderLayout(0, 0));
      tinaSolidRenderingMaterialPnl.add(getPanel_114(), BorderLayout.CENTER);
      tinaSolidRenderingMaterialPnl.add(getPanel_115(), BorderLayout.EAST);

      JPanel tinaSolidRenderingLightPnl = new JPanel();
      tinaSolidRenderingPane.addTab("Light settings", null, tinaSolidRenderingLightPnl, null);
      tinaSolidRenderingLightPnl.setLayout(null);
      tinaSolidRenderingLightPnl.add(getResetSolidRenderingMaterialsBtn());

      tinaSolidRenderingAddLightBtn = new JButton();
      tinaSolidRenderingAddLightBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingAddLightBtn_clicked();
          }
        }
      });
      tinaSolidRenderingAddLightBtn.setToolTipText("Add new light");
      tinaSolidRenderingAddLightBtn.setText("Add");
      tinaSolidRenderingAddLightBtn.setPreferredSize(new Dimension(56, 24));
      tinaSolidRenderingAddLightBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingAddLightBtn.setBounds(265, 6, 56, 24);
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingAddLightBtn);

      JLabel lblSelectLight = new JLabel();
      lblSelectLight.setText("Selected Light");
      lblSelectLight.setPreferredSize(new Dimension(100, 22));
      lblSelectLight.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSelectLight.setAlignmentX(1.0f);
      lblSelectLight.setBounds(6, 11, 100, 14);
      tinaSolidRenderingLightPnl.add(lblSelectLight);

      tinaSolidRenderingSelectedLightCmb = new JComboBox();
      tinaSolidRenderingSelectedLightCmb.setToolTipText("");
      tinaSolidRenderingSelectedLightCmb.setPreferredSize(new Dimension(110, 24));
      tinaSolidRenderingSelectedLightCmb.setMinimumSize(new Dimension(110, 24));
      tinaSolidRenderingSelectedLightCmb.setMaximumSize(new Dimension(32767, 24));
      tinaSolidRenderingSelectedLightCmb.setMaximumRowCount(48);
      tinaSolidRenderingSelectedLightCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingSelectedLightCmb.setBounds(106, 6, 156, 24);
      tinaSolidRenderingSelectedLightCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingSelectedLightCmb_changed();
          }
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingSelectedLightCmb);

      JLabel tinaSolidRenderingLightAltitudeLbl = new JLabel();
      tinaSolidRenderingLightAltitudeLbl.setToolTipText("");
      tinaSolidRenderingLightAltitudeLbl.setText("Altitude*");
      tinaSolidRenderingLightAltitudeLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingLightAltitudeLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingLightAltitudeLbl.setName("tinaSolidRenderingLightAltitudeLbl");
      tinaSolidRenderingLightAltitudeLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingLightAltitudeLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingLightAltitudeLbl.setBounds(6, 37, 68, 22);
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightAltitudeLbl);

      tinaSolidRenderingLightAltitudeREd = new JWFNumberField();
      tinaSolidRenderingLightAltitudeREd.setMouseSpeed(0.1);
      tinaSolidRenderingLightAltitudeREd.setText("");
      tinaSolidRenderingLightAltitudeREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingLightAltitudeREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingLightAltitudeREd.setMotionPropertyName("altitude");
      tinaSolidRenderingLightAltitudeREd.setLocation(new Point(456, 4));
      tinaSolidRenderingLightAltitudeREd.setLinkedMotionControlName("tinaSolidRenderingLightAltitudeSlider");
      tinaSolidRenderingLightAltitudeREd.setLinkedLabelControlName("tinaSolidRenderingLightAltitudeLbl");
      tinaSolidRenderingLightAltitudeREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingLightAltitudeREd.setBounds(72, 37, 100, 24);
      tinaSolidRenderingLightAltitudeREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingLightAltitudeREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingLightAltitudeREd.isMouseAdjusting() || tinaSolidRenderingLightAltitudeREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingLightAltitudeSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingLightAltitudeREd_changed();
          }
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightAltitudeREd);

      tinaSolidRenderingLightAltitudeSlider = new JSlider();
      tinaSolidRenderingLightAltitudeSlider.setValue(0);
      tinaSolidRenderingLightAltitudeSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingLightAltitudeSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingLightAltitudeSlider.setName("tinaSolidRenderingLightAltitudeSlider");
      tinaSolidRenderingLightAltitudeSlider.setMinimum(-1800000);
      tinaSolidRenderingLightAltitudeSlider.setMaximum(1800000);
      tinaSolidRenderingLightAltitudeSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingLightAltitudeSlider.setBounds(174, 37, 205, 19);
      tinaSolidRenderingLightAltitudeSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingLightAltitudeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingLightAltitudeSlider_stateChanged(e);
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightAltitudeSlider);

      JLabel tinaSolidRenderingLightAzimuthLbl = new JLabel();
      tinaSolidRenderingLightAzimuthLbl.setText("Azimuth*");
      tinaSolidRenderingLightAzimuthLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingLightAzimuthLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingLightAzimuthLbl.setName("tinaSolidRenderingLightAzimuthLbl");
      tinaSolidRenderingLightAzimuthLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingLightAzimuthLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingLightAzimuthLbl.setBounds(6, 61, 68, 22);
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightAzimuthLbl);

      tinaSolidRenderingLightAzimuthREd = new JWFNumberField();
      tinaSolidRenderingLightAzimuthREd.setMouseSpeed(0.1);
      tinaSolidRenderingLightAzimuthREd.setText("");
      tinaSolidRenderingLightAzimuthREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingLightAzimuthREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingLightAzimuthREd.setMotionPropertyName("azimuth");
      tinaSolidRenderingLightAzimuthREd.setLocation(new Point(456, 4));
      tinaSolidRenderingLightAzimuthREd.setLinkedMotionControlName("tinaSolidRenderingLightAzimuthSlider");
      tinaSolidRenderingLightAzimuthREd.setLinkedLabelControlName("tinaSolidRenderingLightAzimuthLbl");
      tinaSolidRenderingLightAzimuthREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingLightAzimuthREd.setBounds(72, 61, 100, 24);
      tinaSolidRenderingLightAzimuthREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingLightAzimuthREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingLightAzimuthREd.isMouseAdjusting() || tinaSolidRenderingLightAzimuthREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingLightAzimuthSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingLightAzimuthREd_changed();
          }
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightAzimuthREd);

      tinaSolidRenderingLightAzimuthSlider = new JSlider();
      tinaSolidRenderingLightAzimuthSlider.setValue(0);
      tinaSolidRenderingLightAzimuthSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingLightAzimuthSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingLightAzimuthSlider.setName("tinaSolidRenderingLightAzimuthSlider");
      tinaSolidRenderingLightAzimuthSlider.setMinimum(-1800000);
      tinaSolidRenderingLightAzimuthSlider.setMaximum(1800000);
      tinaSolidRenderingLightAzimuthSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingLightAzimuthSlider.setBounds(174, 61, 205, 19);
      tinaSolidRenderingLightAzimuthSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingLightAzimuthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingLightAzimuthSlider_stateChanged(e);
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightAzimuthSlider);

      tinaSolidRenderingLightColorBtn = new JButton();
      tinaSolidRenderingLightColorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingLightColorBtn_clicked();
          }
        }
      });
      tinaSolidRenderingLightColorBtn.setToolTipText("Set the color of the light");
      tinaSolidRenderingLightColorBtn.setPreferredSize(new Dimension(190, 24));
      tinaSolidRenderingLightColorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingLightColorBtn.setBackground(Color.BLACK);
      tinaSolidRenderingLightColorBtn.setBounds(526, 12, 56, 24);
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightColorBtn);

      JLabel lblLightColor = new JLabel();
      lblLightColor.setText("Light color*");
      lblLightColor.setSize(new Dimension(88, 22));
      lblLightColor.setPreferredSize(new Dimension(88, 22));
      lblLightColor.setLocation(new Point(4, 4));
      lblLightColor.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblLightColor.setBounds(434, 12, 90, 22);
      tinaSolidRenderingLightPnl.add(lblLightColor);

      JLabel tinaSolidRenderingLightIntensityLbl0 = new JLabel();
      tinaSolidRenderingLightIntensityLbl0.setText("Light intensity*");
      tinaSolidRenderingLightIntensityLbl0.setSize(new Dimension(68, 22));
      tinaSolidRenderingLightIntensityLbl0.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingLightIntensityLbl0.setName("tinaSolidRenderingLightIntensityLbl0");
      tinaSolidRenderingLightIntensityLbl0.setLocation(new Point(390, 6));
      tinaSolidRenderingLightIntensityLbl0.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingLightIntensityLbl0.setBounds(434, 37, 90, 22);
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightIntensityLbl0);

      tinaSolidRenderingLightIntensityREd = new JWFNumberField();
      tinaSolidRenderingLightIntensityREd.setValueStep(0.05);
      tinaSolidRenderingLightIntensityREd.setText("");
      tinaSolidRenderingLightIntensityREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingLightIntensityREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingLightIntensityREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingLightIntensityREd.setLocation(new Point(456, 4));
      tinaSolidRenderingLightIntensityREd.setLinkedMotionControlName("tinaSolidRenderingLightIntensitySlider");
      tinaSolidRenderingLightIntensityREd.setLinkedLabelControlName("tinaSolidRenderingLightIntensityLbl");
      tinaSolidRenderingLightIntensityREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingLightIntensityREd.setBounds(526, 37, 100, 24);
      tinaSolidRenderingLightIntensityREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingLightIntensityREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingLightIntensityREd.isMouseAdjusting() || tinaSolidRenderingLightIntensityREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingLightIntensitySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingLightIntensityREd_changed();
          }
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightIntensityREd);

      tinaSolidRenderingLightIntensitySlider = new JSlider();
      tinaSolidRenderingLightIntensitySlider.setValue(0);
      tinaSolidRenderingLightIntensitySlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingLightIntensitySlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingLightIntensitySlider.setName("tinaSolidRenderingLightIntensitySlider");
      tinaSolidRenderingLightIntensitySlider.setMaximum(7500);
      tinaSolidRenderingLightIntensitySlider.setLocation(new Point(558, 4));
      tinaSolidRenderingLightIntensitySlider.setBounds(628, 37, 205, 19);
      tinaSolidRenderingLightIntensitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingLightIntensitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingLightIntensitySlider_stateChanged(e);
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightIntensitySlider);

      tinaSolidRenderingDeleteLightBtn = new JButton();
      tinaSolidRenderingDeleteLightBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingDeleteLightBtn_clicked();
          }
        }
      });
      tinaSolidRenderingDeleteLightBtn.setToolTipText("Delete light");
      tinaSolidRenderingDeleteLightBtn.setText("Del");
      tinaSolidRenderingDeleteLightBtn.setPreferredSize(new Dimension(56, 24));
      tinaSolidRenderingDeleteLightBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingDeleteLightBtn.setBounds(323, 6, 56, 24);
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingDeleteLightBtn);

      tinaSolidRenderingLightCastShadowsCBx = new JCheckBox("Cast shadows");
      tinaSolidRenderingLightCastShadowsCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingLightCastShadowsCBx_changed();
          }
        }
      });

      tinaSolidRenderingLightCastShadowsCBx.setActionCommand("");
      tinaSolidRenderingLightCastShadowsCBx.setBounds(664, 12, 169, 18);

      tinaSolidRenderingLightPnl.add(tinaSolidRenderingLightCastShadowsCBx);

      JLabel tinaSolidRenderingShadowIntensityLbl0 = new JLabel();
      tinaSolidRenderingShadowIntensityLbl0.setText("Shadow intensity");
      tinaSolidRenderingShadowIntensityLbl0.setSize(new Dimension(68, 22));
      tinaSolidRenderingShadowIntensityLbl0.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingShadowIntensityLbl0.setName("tinaSolidRenderingShadowIntensityLbl0");
      tinaSolidRenderingShadowIntensityLbl0.setLocation(new Point(390, 6));
      tinaSolidRenderingShadowIntensityLbl0.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingShadowIntensityLbl0.setBounds(434, 61, 90, 22);
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingShadowIntensityLbl0);

      tinaSolidRenderingShadowIntensityREd = new JWFNumberField();
      tinaSolidRenderingShadowIntensityREd.setMaxValue(1.0);
      tinaSolidRenderingShadowIntensityREd.setHasMinValue(true);
      tinaSolidRenderingShadowIntensityREd.setHasMaxValue(true);
      tinaSolidRenderingShadowIntensityREd.setValueStep(0.05);
      tinaSolidRenderingShadowIntensityREd.setText("");
      tinaSolidRenderingShadowIntensityREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingShadowIntensityREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingShadowIntensityREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingShadowIntensityREd.setLocation(new Point(456, 4));
      tinaSolidRenderingShadowIntensityREd.setLinkedMotionControlName("tinaSolidRenderingShadowIntensitySlider");
      tinaSolidRenderingShadowIntensityREd.setLinkedLabelControlName("tinaSolidRenderingShadowIntensityLbl");
      tinaSolidRenderingShadowIntensityREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingShadowIntensityREd.setBounds(526, 61, 100, 24);
      tinaSolidRenderingShadowIntensityREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingShadowIntensityREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingShadowIntensityREd.isMouseAdjusting() || tinaSolidRenderingShadowIntensityREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingShadowIntensitySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingShadowIntensityREd_changed();
          }
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingShadowIntensityREd);

      tinaSolidRenderingShadowIntensitySlider = new JSlider();
      tinaSolidRenderingShadowIntensitySlider.setValue(0);
      tinaSolidRenderingShadowIntensitySlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingShadowIntensitySlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingShadowIntensitySlider.setName("tinaSolidRenderingShadowIntensitySlider");
      tinaSolidRenderingShadowIntensitySlider.setMaximum(5000);
      tinaSolidRenderingShadowIntensitySlider.setLocation(new Point(558, 4));
      tinaSolidRenderingShadowIntensitySlider.setBounds(628, 61, 205, 19);
      tinaSolidRenderingShadowIntensitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingShadowIntensitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingShadowIntensitySlider_stateChanged(e);
        }
      });
      tinaSolidRenderingLightPnl.add(tinaSolidRenderingShadowIntensitySlider);

      JButton button = new JButton();
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().randomizeLightPosition();
          }
        }
      });
      button.setToolTipText("Randomize light position");
      button.setSize(new Dimension(95, 24));
      button.setSelected(false);
      button.setPreferredSize(new Dimension(42, 24));
      button.setLocation(new Point(4, 4));
      button.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      button.setBounds(380, 59, 42, 24);
      tinaSolidRenderingLightPnl.add(button);
      tinaSolidRenderingLightPnl.add(getButton_1());
    }
    return tinaSolidRenderingPane;
  }

  private JButton getResetSolidRenderingMaterialsBtn() {
    if (resetSolidRenderingMaterialsBtn == null) {
      resetSolidRenderingMaterialsBtn = new JButton();
      resetSolidRenderingMaterialsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingResetLightsBtn_clicked();
          }
        }
      });
      resetSolidRenderingMaterialsBtn.setToolTipText("Reset the light settings to the defaults");
      resetSolidRenderingMaterialsBtn.setText("Reset");
      resetSolidRenderingMaterialsBtn.setPreferredSize(new Dimension(125, 24));
      resetSolidRenderingMaterialsBtn.setMinimumSize(new Dimension(100, 24));
      resetSolidRenderingMaterialsBtn.setMaximumSize(new Dimension(32000, 24));
      resetSolidRenderingMaterialsBtn.setIconTextGap(2);
      resetSolidRenderingMaterialsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetSolidRenderingMaterialsBtn.setBounds(948, 6, 100, 24);
      resetSolidRenderingMaterialsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetSolidRenderingMaterialsBtn;
  }

  private JPanel getPanel_114() {
    if (panel_114 == null) {
      panel_114 = new JPanel();
      panel_114.setPreferredSize(new Dimension(840, 10));
      panel_114.setMinimumSize(new Dimension(600, 10));
      panel_114.setLayout(null);
      panel_114.add(getResetSolidRenderingLightsBtn());

      tinaSolidRenderingMaterialSpecularColorBtn = new JButton();
      tinaSolidRenderingMaterialSpecularColorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingMaterialSpecularColorBtn_clicked();
          }
        }
      });
      tinaSolidRenderingMaterialSpecularColorBtn.setToolTipText("");
      tinaSolidRenderingMaterialSpecularColorBtn.setPreferredSize(new Dimension(190, 24));
      tinaSolidRenderingMaterialSpecularColorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialSpecularColorBtn.setBackground(Color.BLACK);
      tinaSolidRenderingMaterialSpecularColorBtn.setBounds(487, 46, 100, 24);
      panel_114.add(tinaSolidRenderingMaterialSpecularColorBtn);

      JLabel lblSpecularColor = new JLabel();
      lblSpecularColor.setText("Specular color*");
      lblSpecularColor.setSize(new Dimension(88, 22));
      lblSpecularColor.setPreferredSize(new Dimension(88, 22));
      lblSpecularColor.setLocation(new Point(4, 4));
      lblSpecularColor.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSpecularColor.setBounds(391, 46, 94, 22);
      panel_114.add(lblSpecularColor);

      JLabel tinaSolidRenderingMaterialSpecularSharpnessLbl = new JLabel();
      tinaSolidRenderingMaterialSpecularSharpnessLbl.setToolTipText("Specular sharpness");
      tinaSolidRenderingMaterialSpecularSharpnessLbl.setText("Spec size*");
      tinaSolidRenderingMaterialSpecularSharpnessLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingMaterialSpecularSharpnessLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingMaterialSpecularSharpnessLbl.setName("tinaSolidRenderingMaterialSpecularSharpnessLbl");
      tinaSolidRenderingMaterialSpecularSharpnessLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingMaterialSpecularSharpnessLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialSpecularSharpnessLbl.setBounds(6, 90, 68, 22);
      panel_114.add(tinaSolidRenderingMaterialSpecularSharpnessLbl);

      tinaSolidRenderingMaterialSpecularSharpnessREd = new JWFNumberField();
      tinaSolidRenderingMaterialSpecularSharpnessREd.setMinValue(1.0);
      tinaSolidRenderingMaterialSpecularSharpnessREd.setHasMinValue(true);
      tinaSolidRenderingMaterialSpecularSharpnessREd.setValueStep(0.05);
      tinaSolidRenderingMaterialSpecularSharpnessREd.setText("");
      tinaSolidRenderingMaterialSpecularSharpnessREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialSpecularSharpnessREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialSpecularSharpnessREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingMaterialSpecularSharpnessREd.setLocation(new Point(456, 4));
      tinaSolidRenderingMaterialSpecularSharpnessREd.setLinkedMotionControlName("tinaSolidRenderingMaterialSpecularSharpnessSlider");
      tinaSolidRenderingMaterialSpecularSharpnessREd.setLinkedLabelControlName("tinaSolidRenderingMaterialSpecularSharpnessLbl");
      tinaSolidRenderingMaterialSpecularSharpnessREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingMaterialSpecularSharpnessREd.setBounds(72, 90, 100, 24);
      tinaSolidRenderingMaterialSpecularSharpnessREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingMaterialSpecularSharpnessREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingMaterialSpecularSharpnessREd.isMouseAdjusting() || tinaSolidRenderingMaterialSpecularSharpnessREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingMaterialSpecularSharpnessSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingMaterialSpecularSharpnessREd_changed();
          }
        }
      });

      panel_114.add(tinaSolidRenderingMaterialSpecularSharpnessREd);

      tinaSolidRenderingMaterialSpecularSharpnessSlider = new JSlider();
      tinaSolidRenderingMaterialSpecularSharpnessSlider.setMinimum(5000);
      tinaSolidRenderingMaterialSpecularSharpnessSlider.setValue(0);
      tinaSolidRenderingMaterialSpecularSharpnessSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingMaterialSpecularSharpnessSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingMaterialSpecularSharpnessSlider.setName("tinaSolidRenderingMaterialSpecularSharpnessSlider");
      tinaSolidRenderingMaterialSpecularSharpnessSlider.setMaximum(250000);
      tinaSolidRenderingMaterialSpecularSharpnessSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingMaterialSpecularSharpnessSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingMaterialSpecularSharpnessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingMaterialSpecularSharpnessSlider_stateChanged(e);
        }
      });
      tinaSolidRenderingMaterialSpecularSharpnessSlider.setBounds(174, 90, 205, 19);
      panel_114.add(tinaSolidRenderingMaterialSpecularSharpnessSlider);

      tinaSolidRenderingSelectedMaterialCmb = new JComboBox();
      tinaSolidRenderingSelectedMaterialCmb.setToolTipText("");
      tinaSolidRenderingSelectedMaterialCmb.setPreferredSize(new Dimension(110, 24));
      tinaSolidRenderingSelectedMaterialCmb.setMinimumSize(new Dimension(110, 24));
      tinaSolidRenderingSelectedMaterialCmb.setMaximumSize(new Dimension(32767, 24));
      tinaSolidRenderingSelectedMaterialCmb.setMaximumRowCount(48);
      tinaSolidRenderingSelectedMaterialCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingSelectedMaterialCmb.setBounds(106, 1, 157, 24);
      tinaSolidRenderingSelectedMaterialCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingSelectedMaterialCmb_changed();
          }
        }
      });

      panel_114.add(tinaSolidRenderingSelectedMaterialCmb);

      JLabel lblSelectedMaterial = new JLabel();
      lblSelectedMaterial.setText("Selected Material");
      lblSelectedMaterial.setPreferredSize(new Dimension(100, 22));
      lblSelectedMaterial.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSelectedMaterial.setAlignmentX(1.0f);
      lblSelectedMaterial.setBounds(6, 6, 100, 14);
      panel_114.add(lblSelectedMaterial);

      tinaSolidRenderingAddMaterialBtn = new JButton();
      tinaSolidRenderingAddMaterialBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingAddMaterialBtn_clicked();
          }
        }
      });
      tinaSolidRenderingAddMaterialBtn.setToolTipText("Add new material");
      tinaSolidRenderingAddMaterialBtn.setText("Add");
      tinaSolidRenderingAddMaterialBtn.setPreferredSize(new Dimension(56, 24));
      tinaSolidRenderingAddMaterialBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingAddMaterialBtn.setBounds(265, 1, 56, 24);
      panel_114.add(tinaSolidRenderingAddMaterialBtn);

      tinaSolidRenderingDeleteMaterialBtn = new JButton();
      tinaSolidRenderingDeleteMaterialBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingDeleteMaterialBtn_clicked();
          }
        }
      });
      tinaSolidRenderingDeleteMaterialBtn.setToolTipText("Delete material");
      tinaSolidRenderingDeleteMaterialBtn.setText("Del");
      tinaSolidRenderingDeleteMaterialBtn.setPreferredSize(new Dimension(56, 24));
      tinaSolidRenderingDeleteMaterialBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingDeleteMaterialBtn.setBounds(323, 1, 56, 24);
      panel_114.add(tinaSolidRenderingDeleteMaterialBtn);

      tinaSolidRenderingMaterialDiffuseREd = new JWFNumberField();
      tinaSolidRenderingMaterialDiffuseREd.setHasMinValue(true);
      tinaSolidRenderingMaterialDiffuseREd.setValueStep(0.05);
      tinaSolidRenderingMaterialDiffuseREd.setText("");
      tinaSolidRenderingMaterialDiffuseREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialDiffuseREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialDiffuseREd.setMotionPropertyName("camPosX");
      tinaSolidRenderingMaterialDiffuseREd.setLocation(new Point(456, 4));
      tinaSolidRenderingMaterialDiffuseREd.setLinkedMotionControlName("tinaSolidRenderingMaterialDiffuseSlider");
      tinaSolidRenderingMaterialDiffuseREd.setLinkedLabelControlName("tinaSolidRenderingMaterialDiffuseLbl");
      tinaSolidRenderingMaterialDiffuseREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingMaterialDiffuseREd.setBounds(72, 24, 100, 24);
      tinaSolidRenderingMaterialDiffuseREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingMaterialDiffuseREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingMaterialDiffuseREd.isMouseAdjusting() || tinaSolidRenderingMaterialDiffuseREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingMaterialDiffuseSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingMaterialDiffuseREd_changed();
          }
        }
      });

      panel_114.add(tinaSolidRenderingMaterialDiffuseREd);

      JLabel tinaSolidRenderingMaterialDiffuseLbl = new JLabel();
      tinaSolidRenderingMaterialDiffuseLbl.setToolTipText("");
      tinaSolidRenderingMaterialDiffuseLbl.setText("Diffuse*");
      tinaSolidRenderingMaterialDiffuseLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingMaterialDiffuseLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingMaterialDiffuseLbl.setName("tinaSolidRenderingMaterialDiffuseLbl");
      tinaSolidRenderingMaterialDiffuseLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingMaterialDiffuseLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialDiffuseLbl.setBounds(6, 24, 68, 22);
      panel_114.add(tinaSolidRenderingMaterialDiffuseLbl);

      tinaSolidRenderingMaterialDiffuseSlider = new JSlider();
      tinaSolidRenderingMaterialDiffuseSlider.setValue(0);
      tinaSolidRenderingMaterialDiffuseSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingMaterialDiffuseSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingMaterialDiffuseSlider.setName("tinaSolidRenderingMaterialDiffuseSlider");
      tinaSolidRenderingMaterialDiffuseSlider.setMaximum(5000);
      tinaSolidRenderingMaterialDiffuseSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingMaterialDiffuseSlider.setBounds(174, 24, 205, 19);
      tinaSolidRenderingMaterialDiffuseSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingMaterialDiffuseSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingMaterialDiffuseSlider_stateChanged(e);
        }
      });
      panel_114.add(tinaSolidRenderingMaterialDiffuseSlider);

      JLabel tinaSolidRenderingMaterialAmbientLbl = new JLabel();
      tinaSolidRenderingMaterialAmbientLbl.setText("Ambient*");
      tinaSolidRenderingMaterialAmbientLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingMaterialAmbientLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingMaterialAmbientLbl.setName("tinaSolidRenderingMaterialAmbientLbl");
      tinaSolidRenderingMaterialAmbientLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingMaterialAmbientLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialAmbientLbl.setBounds(6, 46, 68, 22);
      panel_114.add(tinaSolidRenderingMaterialAmbientLbl);

      tinaSolidRenderingMaterialAmbientREd = new JWFNumberField();
      tinaSolidRenderingMaterialAmbientREd.setHasMinValue(true);
      tinaSolidRenderingMaterialAmbientREd.setValueStep(0.05);
      tinaSolidRenderingMaterialAmbientREd.setText("");
      tinaSolidRenderingMaterialAmbientREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialAmbientREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialAmbientREd.setMotionPropertyName("camPosY");
      tinaSolidRenderingMaterialAmbientREd.setLocation(new Point(456, 4));
      tinaSolidRenderingMaterialAmbientREd.setLinkedMotionControlName("tinaSolidRenderingMaterialAmbientSlider");
      tinaSolidRenderingMaterialAmbientREd.setLinkedLabelControlName("tinaSolidRenderingMaterialAmbientLbl");
      tinaSolidRenderingMaterialAmbientREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingMaterialAmbientREd.setBounds(72, 46, 100, 24);
      tinaSolidRenderingMaterialAmbientREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingMaterialAmbientREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingMaterialAmbientREd.isMouseAdjusting() || tinaSolidRenderingMaterialAmbientREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingMaterialAmbientSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingMaterialAmbientREd_changed();
          }
        }
      });
      panel_114.add(tinaSolidRenderingMaterialAmbientREd);

      tinaSolidRenderingMaterialAmbientSlider = new JSlider();
      tinaSolidRenderingMaterialAmbientSlider.setValue(0);
      tinaSolidRenderingMaterialAmbientSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingMaterialAmbientSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingMaterialAmbientSlider.setName("tinaSolidRenderingMaterialAmbientSlider");
      tinaSolidRenderingMaterialAmbientSlider.setMaximum(5000);
      tinaSolidRenderingMaterialAmbientSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingMaterialAmbientSlider.setBounds(174, 46, 205, 19);
      tinaSolidRenderingMaterialAmbientSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingMaterialAmbientSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingMaterialAmbientSlider_stateChanged(e);
        }
      });
      panel_114.add(tinaSolidRenderingMaterialAmbientSlider);

      JLabel tinaSolidRenderingMaterialSpecularLbl = new JLabel();
      tinaSolidRenderingMaterialSpecularLbl.setText("Specular*");
      tinaSolidRenderingMaterialSpecularLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingMaterialSpecularLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingMaterialSpecularLbl.setName("tinaSolidRenderingMaterialSpecularLbl");
      tinaSolidRenderingMaterialSpecularLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingMaterialSpecularLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialSpecularLbl.setBounds(6, 68, 68, 22);
      panel_114.add(tinaSolidRenderingMaterialSpecularLbl);

      tinaSolidRenderingMaterialSpecularREd = new JWFNumberField();
      tinaSolidRenderingMaterialSpecularREd.setHasMinValue(true);
      tinaSolidRenderingMaterialSpecularREd.setValueStep(0.05);
      tinaSolidRenderingMaterialSpecularREd.setText("");
      tinaSolidRenderingMaterialSpecularREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialSpecularREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialSpecularREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingMaterialSpecularREd.setLocation(new Point(456, 4));
      tinaSolidRenderingMaterialSpecularREd.setLinkedMotionControlName("tinaSolidRenderingMaterialSpecularSlider");
      tinaSolidRenderingMaterialSpecularREd.setLinkedLabelControlName("tinaSolidRenderingMaterialSpecularLbl");
      tinaSolidRenderingMaterialSpecularREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingMaterialSpecularREd.setBounds(72, 68, 100, 24);
      tinaSolidRenderingMaterialSpecularREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingMaterialSpecularREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingMaterialSpecularREd.isMouseAdjusting() || tinaSolidRenderingMaterialSpecularREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingMaterialSpecularSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingMaterialSpecularREd_changed();
          }
        }
      });
      panel_114.add(tinaSolidRenderingMaterialSpecularREd);

      tinaSolidRenderingMaterialSpecularSlider = new JSlider();
      tinaSolidRenderingMaterialSpecularSlider.setValue(0);
      tinaSolidRenderingMaterialSpecularSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingMaterialSpecularSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingMaterialSpecularSlider.setName("tinaSolidRenderingMaterialSpecularSlider");
      tinaSolidRenderingMaterialSpecularSlider.setMaximum(5000);
      tinaSolidRenderingMaterialSpecularSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingMaterialSpecularSlider.setBounds(174, 68, 205, 19);
      tinaSolidRenderingMaterialSpecularSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingMaterialSpecularSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingMaterialSpecularSlider_stateChanged(e);
        }
      });
      panel_114.add(tinaSolidRenderingMaterialSpecularSlider);

      tinaSolidRenderingMaterialDiffuseResponseCmb = new JComboBox();
      tinaSolidRenderingMaterialDiffuseResponseCmb.setToolTipText("");
      tinaSolidRenderingMaterialDiffuseResponseCmb.setPreferredSize(new Dimension(110, 24));
      tinaSolidRenderingMaterialDiffuseResponseCmb.setMinimumSize(new Dimension(110, 24));
      tinaSolidRenderingMaterialDiffuseResponseCmb.setMaximumSize(new Dimension(32767, 24));
      tinaSolidRenderingMaterialDiffuseResponseCmb.setMaximumRowCount(48);
      tinaSolidRenderingMaterialDiffuseResponseCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialDiffuseResponseCmb.setBounds(487, 24, 100, 24);
      tinaSolidRenderingMaterialDiffuseResponseCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingMaterialDiffuseResponseCmb_changed();
          }
        }
      });
      panel_114.add(tinaSolidRenderingMaterialDiffuseResponseCmb);

      JLabel lblDiffuseResponse = new JLabel();
      lblDiffuseResponse.setText("Diffuse response*");
      lblDiffuseResponse.setSize(new Dimension(68, 22));
      lblDiffuseResponse.setPreferredSize(new Dimension(94, 22));
      lblDiffuseResponse.setName("tinaSolidRenderingMaterialSpecularSharpnessLbl0");
      lblDiffuseResponse.setLocation(new Point(390, 6));
      lblDiffuseResponse.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblDiffuseResponse.setBounds(391, 24, 94, 22);
      panel_114.add(lblDiffuseResponse);
      panel_114.add(getTinaSolidRenderingMaterialReflMapBtn());
      panel_114.add(getTinaSolidRenderingMaterialSelectReflMapBtn());
      panel_114.add(getTinaSolidRenderingMaterialRemoveReflMapBtn());
      panel_114.add(getLblReflectionMap());

      JLabel tinaSolidRenderingMaterialReflectionMapIntensityLbl = new JLabel();
      tinaSolidRenderingMaterialReflectionMapIntensityLbl.setToolTipText("Reflection map intensity");
      tinaSolidRenderingMaterialReflectionMapIntensityLbl.setText("Refl intensity*");
      tinaSolidRenderingMaterialReflectionMapIntensityLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingMaterialReflectionMapIntensityLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingMaterialReflectionMapIntensityLbl.setName("tinaSolidRenderingMaterialReflectionMapIntensityLbl");
      tinaSolidRenderingMaterialReflectionMapIntensityLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingMaterialReflectionMapIntensityLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialReflectionMapIntensityLbl.setBounds(606, 68, 94, 22);
      panel_114.add(tinaSolidRenderingMaterialReflectionMapIntensityLbl);

      tinaSolidRenderingMaterialReflectionMapIntensityREd = new JWFNumberField();
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setValueStep(0.05);
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setText("");
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setLocation(new Point(456, 4));
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setLinkedMotionControlName("tinaSolidRenderingMaterialReflectionMapIntensitySlider");
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setLinkedLabelControlName("tinaSolidRenderingMaterialReflectionMapIntensityLbl");
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setHasMinValue(true);
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingMaterialReflectionMapIntensityREd.setBounds(705, 68, 100, 24);
      tinaSolidRenderingMaterialReflectionMapIntensityREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingMaterialReflectionMapIntensityREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingMaterialReflectionMapIntensityREd.isMouseAdjusting() || tinaSolidRenderingMaterialReflectionMapIntensityREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingMaterialReflectionMapIntensitySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingMaterialReflectionMapIntensityREd_changed();
          }
        }
      });
      panel_114.add(tinaSolidRenderingMaterialReflectionMapIntensityREd);

      tinaSolidRenderingMaterialReflectionMapIntensitySlider = new JSlider();
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.setValue(0);
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.setName("tinaSolidRenderingMaterialReflectionMapIntensitySlider");
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.setMaximum(5000);
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.setLocation(new Point(558, 4));
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.setBounds(807, 68, 205, 19);
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingMaterialReflectionMapIntensitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingMaterialReflectionMapIntensitySlider_stateChanged(e);
        }
      });
      panel_114.add(tinaSolidRenderingMaterialReflectionMapIntensitySlider);

      tinaSolidRenderingMaterialReflectionMappingCmb = new JComboBox();
      tinaSolidRenderingMaterialReflectionMappingCmb.setToolTipText("");
      tinaSolidRenderingMaterialReflectionMappingCmb.setPreferredSize(new Dimension(110, 24));
      tinaSolidRenderingMaterialReflectionMappingCmb.setMinimumSize(new Dimension(110, 24));
      tinaSolidRenderingMaterialReflectionMappingCmb.setMaximumSize(new Dimension(32767, 24));
      tinaSolidRenderingMaterialReflectionMappingCmb.setMaximumRowCount(48);
      tinaSolidRenderingMaterialReflectionMappingCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialReflectionMappingCmb.setBounds(705, 46, 246, 24);
      tinaSolidRenderingMaterialReflectionMappingCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingMaterialReflectionMappingCmb_changed();
          }
        }
      });
      panel_114.add(tinaSolidRenderingMaterialReflectionMappingCmb);

      JLabel lblReflectionMapping = new JLabel();
      lblReflectionMapping.setText("Refl mapping*");
      lblReflectionMapping.setSize(new Dimension(68, 22));
      lblReflectionMapping.setPreferredSize(new Dimension(94, 22));
      lblReflectionMapping.setName("tinaSolidRenderingMaterialReflectionMappingLbl");
      lblReflectionMapping.setLocation(new Point(390, 6));
      lblReflectionMapping.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblReflectionMapping.setBounds(606, 47, 94, 22);
      panel_114.add(lblReflectionMapping);
    }
    return panel_114;
  }

  private JPanel getPanel_115() {
    if (panel_115 == null) {
      panel_115 = new JPanel();
      panel_115.setPreferredSize(new Dimension(20, 10));
    }
    return panel_115;
  }

  private JButton getResetSolidRenderingLightsBtn() {
    if (resetSolidRenderingLightsBtn == null) {
      resetSolidRenderingLightsBtn = new JButton();
      resetSolidRenderingLightsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingResetMaterialsBtn_clicked();
          }
        }
      });
      resetSolidRenderingLightsBtn.setToolTipText("Reset the light settings to the defaults");
      resetSolidRenderingLightsBtn.setText("Reset");
      resetSolidRenderingLightsBtn.setPreferredSize(new Dimension(125, 24));
      resetSolidRenderingLightsBtn.setMinimumSize(new Dimension(100, 24));
      resetSolidRenderingLightsBtn.setMaximumSize(new Dimension(32000, 24));
      resetSolidRenderingLightsBtn.setIconTextGap(2);
      resetSolidRenderingLightsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetSolidRenderingLightsBtn.setBounds(704, 1, 100, 24);
      resetSolidRenderingLightsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
    }
    return resetSolidRenderingLightsBtn;
  }

  public JComboBox getTinaSolidRenderingSelectedLightCmb() {
    return tinaSolidRenderingSelectedLightCmb;
  }

  public JButton getTinaSolidRenderingAddLightBtn() {
    return tinaSolidRenderingAddLightBtn;
  }

  public JWFNumberField getTinaSolidRenderingLightAltitudeREd() {
    return tinaSolidRenderingLightAltitudeREd;
  }

  public JWFNumberField getTinaSolidRenderingLightAzimuthREd() {
    return tinaSolidRenderingLightAzimuthREd;
  }

  public JSlider getTinaSolidRenderingLightAltitudeSlider() {
    return tinaSolidRenderingLightAltitudeSlider;
  }

  public JSlider getTinaSolidRenderingLightAzimuthSlider() {
    return tinaSolidRenderingLightAzimuthSlider;
  }

  public JButton getTinaSolidRenderingLightColorBtn() {
    return tinaSolidRenderingLightColorBtn;
  }

  public JCheckBox getTinaSolidRenderingLightCastShadowsCBx() {
    return tinaSolidRenderingLightCastShadowsCBx;
  }

  public JWFNumberField getTinaSolidRenderingLightIntensityREd0() {
    return tinaSolidRenderingLightIntensityREd;
  }

  public JSlider getTinaSolidRenderingLightIntensitySlider() {
    return tinaSolidRenderingLightIntensitySlider;
  }

  public JComboBox getTinaSolidRenderingSelectedMaterialCmb() {
    return tinaSolidRenderingSelectedMaterialCmb;
  }

  public JButton getTinaSolidRenderingAddMaterialBtn() {
    return tinaSolidRenderingAddMaterialBtn;
  }

  public JButton getTinaSolidRenderingDeleteMaterialBtn() {
    return tinaSolidRenderingDeleteMaterialBtn;
  }

  public JWFNumberField getTinaSolidRenderingMaterialDiffuseREd() {
    return tinaSolidRenderingMaterialDiffuseREd;
  }

  public JSlider getTinaSolidRenderingMaterialDiffuseSlider() {
    return tinaSolidRenderingMaterialDiffuseSlider;
  }

  public JWFNumberField getTinaSolidRenderingMaterialAmbientREd() {
    return tinaSolidRenderingMaterialAmbientREd;
  }

  public JSlider getTinaSolidRenderingMaterialAmbientSlider() {
    return tinaSolidRenderingMaterialAmbientSlider;
  }

  public JSlider getTinaSolidRenderingMaterialSpecularSlider() {
    return tinaSolidRenderingMaterialSpecularSlider;
  }

  public JWFNumberField getTinaSolidRenderingMaterialSpecularSharpnessREd() {
    return tinaSolidRenderingMaterialSpecularSharpnessREd;
  }

  public JSlider getTinaSolidRenderingMaterialSpecularSharpnessSlider() {
    return tinaSolidRenderingMaterialSpecularSharpnessSlider;
  }

  public JComboBox getTinaSolidRenderingMaterialDiffuseResponseCmb() {
    return tinaSolidRenderingMaterialDiffuseResponseCmb;
  }

  public JButton getTinaSolidRenderingDeleteLightBtn() {
    return tinaSolidRenderingDeleteLightBtn;
  }

  public JWFNumberField getTinaSolidRenderingMaterialSpecularREd() {
    return tinaSolidRenderingMaterialSpecularREd;
  }

  public JButton getTinaSolidRenderingMaterialSpecularColorBtn() {
    return tinaSolidRenderingMaterialSpecularColorBtn;
  }

  private JWFNumberField getPostBlurFallOffREd() {
    if (postBlurFallOffREd == null) {
      postBlurFallOffREd = new JWFNumberField();
      postBlurFallOffREd.setValueStep(0.1);
      postBlurFallOffREd.setHasMinValue(true);
      postBlurFallOffREd.setHasMaxValue(true);
      postBlurFallOffREd.setMaxValue(10.0);
      postBlurFallOffREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!postBlurFallOffREd.isMouseAdjusting() || postBlurFallOffREd.getMouseChangeCount() == 0) {
            if (!postBlurFallOffSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().postBlurFallOffREd_changed();
        }
      });

      postBlurFallOffREd.setPreferredSize(new Dimension(100, 24));
      postBlurFallOffREd.setText("");
      postBlurFallOffREd.setSize(new Dimension(100, 24));
      postBlurFallOffREd.setLocation(new Point(102, 54));
      postBlurFallOffREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    }
    return postBlurFallOffREd;
  }

  private JSlider getPostBlurFallOffSlider() {
    if (postBlurFallOffSlider == null) {
      postBlurFallOffSlider = new JSlider();
      postBlurFallOffSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postBlurFallOffSlider.setMaximum(100);
      postBlurFallOffSlider.setMinimum(0);
      postBlurFallOffSlider.setValue(0);
      postBlurFallOffSlider.setSize(new Dimension(220, 19));
      postBlurFallOffSlider.setLocation(new Point(204, 54));
      postBlurFallOffSlider.setPreferredSize(new Dimension(120, 19));
      postBlurFallOffSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().postBlurFallOffSlider_changed();
        }
      });
    }
    return postBlurFallOffSlider;
  }

  private JButton getTinaSolidRenderingMaterialReflMapBtn() {
    if (tinaSolidRenderingMaterialReflMapBtn == null) {
      tinaSolidRenderingMaterialReflMapBtn = new JButton();
      tinaSolidRenderingMaterialReflMapBtn.setToolTipText("The average reflection map color");
      tinaSolidRenderingMaterialReflMapBtn.setPreferredSize(new Dimension(190, 24));
      tinaSolidRenderingMaterialReflMapBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialReflMapBtn.setBackground(Color.BLACK);
      tinaSolidRenderingMaterialReflMapBtn.setBounds(704, 24, 100, 24);
    }
    return tinaSolidRenderingMaterialReflMapBtn;
  }

  private JButton getTinaSolidRenderingMaterialSelectReflMapBtn() {
    if (tinaSolidRenderingMaterialSelectReflMapBtn == null) {
      tinaSolidRenderingMaterialSelectReflMapBtn = new JButton();
      tinaSolidRenderingMaterialSelectReflMapBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingMaterialSelectReflMapBtn_clicked();
          }
        }
      });

      tinaSolidRenderingMaterialSelectReflMapBtn.setToolTipText("Select an image to use as reflection map");
      tinaSolidRenderingMaterialSelectReflMapBtn.setText("Select image...");
      tinaSolidRenderingMaterialSelectReflMapBtn.setPreferredSize(new Dimension(190, 24));
      tinaSolidRenderingMaterialSelectReflMapBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialSelectReflMapBtn.setBounds(803, 24, 148, 24);
    }
    return tinaSolidRenderingMaterialSelectReflMapBtn;
  }

  private JButton getTinaSolidRenderingMaterialRemoveReflMapBtn() {
    if (tinaSolidRenderingMaterialRemoveReflMapBtn == null) {
      tinaSolidRenderingMaterialRemoveReflMapBtn = new JButton();
      tinaSolidRenderingMaterialRemoveReflMapBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingMaterialRemoveReflMapBtn_clicked();
          }
        }
      });
      tinaSolidRenderingMaterialRemoveReflMapBtn.setToolTipText("Remove the currently used image");
      tinaSolidRenderingMaterialRemoveReflMapBtn.setText("Remove image");
      tinaSolidRenderingMaterialRemoveReflMapBtn.setPreferredSize(new Dimension(190, 24));
      tinaSolidRenderingMaterialRemoveReflMapBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingMaterialRemoveReflMapBtn.setBounds(952, 24, 148, 24);
    }
    return tinaSolidRenderingMaterialRemoveReflMapBtn;
  }

  private JLabel getLblReflectionMap() {
    if (lblReflectionMap == null) {
      lblReflectionMap = new JLabel();
      lblReflectionMap.setText("Reflection map");
      lblReflectionMap.setSize(new Dimension(94, 22));
      lblReflectionMap.setPreferredSize(new Dimension(94, 22));
      lblReflectionMap.setLocation(new Point(4, 4));
      lblReflectionMap.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblReflectionMap.setBounds(606, 24, 94, 22);
    }
    return lblReflectionMap;
  }

  public JWFNumberField getTinaSolidRenderingMaterialReflectionMapIntensityREd() {
    return tinaSolidRenderingMaterialReflectionMapIntensityREd;
  }

  public JSlider getTinaSolidRenderingMaterialReflectionMapIntensitySlider() {
    return tinaSolidRenderingMaterialReflectionMapIntensitySlider;
  }

  public JWFNumberField getXFormModHueREd() {
    return xFormModHueREd;
  }

  public JWFNumberField getXFormModHueSpeedREd() {
    return xFormModHueSpeedREd;
  }

  public JSlider getXFormModHueSlider() {
    return xFormModHueSlider;
  }

  public JSlider getXFormModHueSpeedSlider() {
    return xFormModHueSpeedSlider;
  }

  public JWFNumberField getXFormMaterialREd() {
    return xFormMaterialREd;
  }

  public JSlider getXFormMaterialSlider() {
    return xFormMaterialSlider;
  }

  public JPanel getTinaMaterialChooserPaletteImgPanel() {
    return tinaMaterialChooserPaletteImgPanel;
  }

  public JWFNumberField getXFormMaterialSpeedREd() {
    return xFormMaterialSpeedREd;
  }

  public JSlider getXFormMaterialSpeedSlider() {
    return xFormMaterialSpeedSlider;
  }

  private JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel();
      panel.setLayout(null);

      tinaSolidRenderingEnableAOCBx = new JCheckBox("Enable ambient shadows");
      tinaSolidRenderingEnableAOCBx.setBounds(16, 2, 169, 18);
      tinaSolidRenderingEnableAOCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingEnableAOCBx_changed();
          }
        }
      });
      tinaSolidRenderingEnableAOCBx.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      panel.add(tinaSolidRenderingEnableAOCBx);

      JLabel tinaSolidRenderingAOIntensityLbl = new JLabel();
      tinaSolidRenderingAOIntensityLbl.setText("Amb shadow intensity*");
      tinaSolidRenderingAOIntensityLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingAOIntensityLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingAOIntensityLbl.setName("tinaSolidRenderingAOIntensityLbl");
      tinaSolidRenderingAOIntensityLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingAOIntensityLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingAOIntensityLbl.setBounds(16, 25, 124, 22);
      panel.add(tinaSolidRenderingAOIntensityLbl);

      tinaSolidRenderingAOIntensityREd = new JWFNumberField();
      tinaSolidRenderingAOIntensityREd.setValueStep(0.05);
      tinaSolidRenderingAOIntensityREd.setText("");
      tinaSolidRenderingAOIntensityREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingAOIntensityREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingAOIntensityREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingAOIntensityREd.setLocation(new Point(456, 4));
      tinaSolidRenderingAOIntensityREd.setLinkedMotionControlName("tinaSolidRenderingAOIntensitySlider");
      tinaSolidRenderingAOIntensityREd.setLinkedLabelControlName("tinaSolidRenderingAOIntensityLbl");
      tinaSolidRenderingAOIntensityREd.setHasMinValue(true);
      tinaSolidRenderingAOIntensityREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingAOIntensityREd.setBounds(138, 25, 100, 24);
      tinaSolidRenderingAOIntensityREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingAOIntensityREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingAOIntensityREd.isMouseAdjusting() || tinaSolidRenderingAOIntensityREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingAOIntensitySlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingAOIntensityREd_changed();
          }
        }
      });
      panel.add(tinaSolidRenderingAOIntensityREd);

      tinaSolidRenderingAOIntensitySlider = new JSlider();
      tinaSolidRenderingAOIntensitySlider.setValue(0);
      tinaSolidRenderingAOIntensitySlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingAOIntensitySlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingAOIntensitySlider.setName("tinaSolidRenderingAOIntensitySlider");
      tinaSolidRenderingAOIntensitySlider.setMaximum(25000);
      tinaSolidRenderingAOIntensitySlider.setLocation(new Point(558, 4));
      tinaSolidRenderingAOIntensitySlider.setBounds(240, 27, 205, 19);
      tinaSolidRenderingAOIntensitySlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingAOIntensitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingAOIntensitySlider_stateChanged(e);
        }
      });

      panel.add(tinaSolidRenderingAOIntensitySlider);

      JLabel tinaSolidRenderingAOSearchRadiusLbl = new JLabel();
      tinaSolidRenderingAOSearchRadiusLbl.setText("Search radius");
      tinaSolidRenderingAOSearchRadiusLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingAOSearchRadiusLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingAOSearchRadiusLbl.setName("tinaSolidRenderingAOSearchRadiusLbl");
      tinaSolidRenderingAOSearchRadiusLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingAOSearchRadiusLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingAOSearchRadiusLbl.setBounds(16, 47, 124, 22);
      panel.add(tinaSolidRenderingAOSearchRadiusLbl);

      tinaSolidRenderingAOSearchRadiusREd = new JWFNumberField();
      tinaSolidRenderingAOSearchRadiusREd.setValueStep(0.05);
      tinaSolidRenderingAOSearchRadiusREd.setText("");
      tinaSolidRenderingAOSearchRadiusREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingAOSearchRadiusREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingAOSearchRadiusREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingAOSearchRadiusREd.setLocation(new Point(456, 4));
      tinaSolidRenderingAOSearchRadiusREd.setLinkedMotionControlName("tinaSolidRenderingAOSearchRadiusSlider");
      tinaSolidRenderingAOSearchRadiusREd.setLinkedLabelControlName("tinaSolidRenderingAOSearchRadiusLbl");
      tinaSolidRenderingAOSearchRadiusREd.setHasMinValue(true);
      tinaSolidRenderingAOSearchRadiusREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingAOSearchRadiusREd.setBounds(138, 47, 100, 24);
      tinaSolidRenderingAOSearchRadiusREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingAOSearchRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingAOSearchRadiusREd.isMouseAdjusting() || tinaSolidRenderingAOSearchRadiusREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingAOSearchRadiusSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingAOSearchRadiusREd_changed();
          }
        }
      });
      panel.add(tinaSolidRenderingAOSearchRadiusREd);

      tinaSolidRenderingAOSearchRadiusSlider = new JSlider();
      tinaSolidRenderingAOSearchRadiusSlider.setValue(0);
      tinaSolidRenderingAOSearchRadiusSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingAOSearchRadiusSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingAOSearchRadiusSlider.setName("tinaSolidRenderingAOSearchRadiusSlider");
      tinaSolidRenderingAOSearchRadiusSlider.setMaximum(150000);
      tinaSolidRenderingAOSearchRadiusSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingAOSearchRadiusSlider.setBounds(240, 49, 205, 19);
      tinaSolidRenderingAOSearchRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingAOSearchRadiusSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingAOSearchRadiusSlider_stateChanged(e);
        }
      });
      panel.add(tinaSolidRenderingAOSearchRadiusSlider);

      JLabel tinaSolidRenderingAOBlurRadiusLbl = new JLabel();
      tinaSolidRenderingAOBlurRadiusLbl.setText("Blur radius");
      tinaSolidRenderingAOBlurRadiusLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingAOBlurRadiusLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingAOBlurRadiusLbl.setName("tinaSolidRenderingAOBlurRadiusLbl");
      tinaSolidRenderingAOBlurRadiusLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingAOBlurRadiusLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingAOBlurRadiusLbl.setBounds(16, 69, 124, 22);
      panel.add(tinaSolidRenderingAOBlurRadiusLbl);

      tinaSolidRenderingAOBlurRadiusREd = new JWFNumberField();
      tinaSolidRenderingAOBlurRadiusREd.setValueStep(0.05);
      tinaSolidRenderingAOBlurRadiusREd.setText("");
      tinaSolidRenderingAOBlurRadiusREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingAOBlurRadiusREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingAOBlurRadiusREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingAOBlurRadiusREd.setLocation(new Point(456, 4));
      tinaSolidRenderingAOBlurRadiusREd.setLinkedMotionControlName("tinaSolidRenderingAOBlurRadiusSlider");
      tinaSolidRenderingAOBlurRadiusREd.setLinkedLabelControlName("tinaSolidRenderingAOBlurRadiusLbl");
      tinaSolidRenderingAOBlurRadiusREd.setHasMinValue(true);
      tinaSolidRenderingAOBlurRadiusREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingAOBlurRadiusREd.setBounds(138, 69, 100, 24);
      tinaSolidRenderingAOBlurRadiusREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingAOBlurRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingAOBlurRadiusREd.isMouseAdjusting() || tinaSolidRenderingAOBlurRadiusREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingAOBlurRadiusSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingAOBlurRadiusREd_changed();
          }
        }
      });
      panel.add(tinaSolidRenderingAOBlurRadiusREd);

      tinaSolidRenderingAOBlurRadiusSlider = new JSlider();
      tinaSolidRenderingAOBlurRadiusSlider.setValue(0);
      tinaSolidRenderingAOBlurRadiusSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingAOBlurRadiusSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingAOBlurRadiusSlider.setName("tinaSolidRenderingAOBlurRadiusSlider");
      tinaSolidRenderingAOBlurRadiusSlider.setMaximum(25000);
      tinaSolidRenderingAOBlurRadiusSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingAOBlurRadiusSlider.setBounds(240, 71, 205, 19);
      tinaSolidRenderingAOBlurRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingAOBlurRadiusSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingAOBlurRadiusSlider_stateChanged(e);
        }
      });
      panel.add(tinaSolidRenderingAOBlurRadiusSlider);

      JLabel tinaSolidRenderingAOFalloffLbl = new JLabel();
      tinaSolidRenderingAOFalloffLbl.setText("Falloff");
      tinaSolidRenderingAOFalloffLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingAOFalloffLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingAOFalloffLbl.setName("tinaSolidRenderingAOFalloffLbl");
      tinaSolidRenderingAOFalloffLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingAOFalloffLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingAOFalloffLbl.setBounds(16, 90, 124, 22);
      panel.add(tinaSolidRenderingAOFalloffLbl);

      tinaSolidRenderingAOFalloffREd = new JWFNumberField();
      tinaSolidRenderingAOFalloffREd.setValueStep(0.05);
      tinaSolidRenderingAOFalloffREd.setText("");
      tinaSolidRenderingAOFalloffREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingAOFalloffREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingAOFalloffREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingAOFalloffREd.setLocation(new Point(456, 4));
      tinaSolidRenderingAOFalloffREd.setLinkedMotionControlName("tinaSolidRenderingAOFalloffSlider");
      tinaSolidRenderingAOFalloffREd.setLinkedLabelControlName("tinaSolidRenderingAOFalloffLbl");
      tinaSolidRenderingAOFalloffREd.setHasMinValue(true);
      tinaSolidRenderingAOFalloffREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingAOFalloffREd.setBounds(138, 90, 100, 24);
      tinaSolidRenderingAOFalloffREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingAOFalloffREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingAOFalloffREd.isMouseAdjusting() || tinaSolidRenderingAOFalloffREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingAOFalloffSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingAOFalloffREd_changed();
          }
        }
      });

      panel.add(tinaSolidRenderingAOFalloffREd);

      tinaSolidRenderingAOFalloffSlider = new JSlider();
      tinaSolidRenderingAOFalloffSlider.setValue(0);
      tinaSolidRenderingAOFalloffSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingAOFalloffSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingAOFalloffSlider.setName("tinaSolidRenderingAOFalloffSlider");
      tinaSolidRenderingAOFalloffSlider.setMaximum(25000);
      tinaSolidRenderingAOFalloffSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingAOFalloffSlider.setBounds(240, 92, 205, 19);
      tinaSolidRenderingAOFalloffSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingAOFalloffSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingAOFalloffSlider_stateChanged(e);
        }
      });
      panel.add(tinaSolidRenderingAOFalloffSlider);

      JLabel tinaSolidRenderingAORadiusSamplesLbl = new JLabel();
      tinaSolidRenderingAORadiusSamplesLbl.setText("Radius samples");
      tinaSolidRenderingAORadiusSamplesLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingAORadiusSamplesLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingAORadiusSamplesLbl.setName("tinaSolidRenderingAORadiusSamplesLbl");
      tinaSolidRenderingAORadiusSamplesLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingAORadiusSamplesLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingAORadiusSamplesLbl.setBounds(475, 23, 124, 22);
      panel.add(tinaSolidRenderingAORadiusSamplesLbl);

      tinaSolidRenderingAORadiusSamplesREd = new JWFNumberField();
      tinaSolidRenderingAORadiusSamplesREd.setMinValue(1.0);
      tinaSolidRenderingAORadiusSamplesREd.setMaxValue(128.0);
      tinaSolidRenderingAORadiusSamplesREd.setHasMaxValue(true);
      tinaSolidRenderingAORadiusSamplesREd.setOnlyIntegers(true);
      tinaSolidRenderingAORadiusSamplesREd.setValueStep(0.05);
      tinaSolidRenderingAORadiusSamplesREd.setText("");
      tinaSolidRenderingAORadiusSamplesREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingAORadiusSamplesREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingAORadiusSamplesREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingAORadiusSamplesREd.setLocation(new Point(456, 4));
      tinaSolidRenderingAORadiusSamplesREd.setLinkedMotionControlName("tinaSolidRenderingAORadiusSamplesSlider");
      tinaSolidRenderingAORadiusSamplesREd.setLinkedLabelControlName("tinaSolidRenderingAORadiusSamplesLbl");
      tinaSolidRenderingAORadiusSamplesREd.setHasMinValue(true);
      tinaSolidRenderingAORadiusSamplesREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingAORadiusSamplesREd.setBounds(597, 23, 100, 24);
      tinaSolidRenderingAORadiusSamplesREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingAORadiusSamplesREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingAORadiusSamplesREd.isMouseAdjusting() || tinaSolidRenderingAORadiusSamplesREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingAORadiusSamplesSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingAORadiusSamplesREd_changed();
          }
        }
      });

      panel.add(tinaSolidRenderingAORadiusSamplesREd);

      tinaSolidRenderingAORadiusSamplesSlider = new JSlider();
      tinaSolidRenderingAORadiusSamplesSlider.setMinimum(1);
      tinaSolidRenderingAORadiusSamplesSlider.setValue(0);
      tinaSolidRenderingAORadiusSamplesSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingAORadiusSamplesSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingAORadiusSamplesSlider.setName("tinaSolidRenderingAORadiusSamplesSlider");
      tinaSolidRenderingAORadiusSamplesSlider.setMaximum(128);
      tinaSolidRenderingAORadiusSamplesSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingAORadiusSamplesSlider.setBounds(699, 25, 205, 19);
      tinaSolidRenderingAORadiusSamplesSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingAORadiusSamplesSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingAORadiusSamplesSlider_stateChanged(e);
        }
      });
      panel.add(tinaSolidRenderingAORadiusSamplesSlider);

      JLabel tinaSolidRenderingAOAzimuthSamplesLbl = new JLabel();
      tinaSolidRenderingAOAzimuthSamplesLbl.setText("Azimuth samples");
      tinaSolidRenderingAOAzimuthSamplesLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingAOAzimuthSamplesLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingAOAzimuthSamplesLbl.setName("tinaSolidRenderingAOAzimuthSamplesLbl");
      tinaSolidRenderingAOAzimuthSamplesLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingAOAzimuthSamplesLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingAOAzimuthSamplesLbl.setBounds(475, 47, 124, 22);
      panel.add(tinaSolidRenderingAOAzimuthSamplesLbl);

      tinaSolidRenderingAOAzimuthSamplesREd = new JWFNumberField();
      tinaSolidRenderingAOAzimuthSamplesREd.setHasMaxValue(true);
      tinaSolidRenderingAOAzimuthSamplesREd.setMinValue(1.0);
      tinaSolidRenderingAOAzimuthSamplesREd.setMaxValue(128.0);
      tinaSolidRenderingAOAzimuthSamplesREd.setOnlyIntegers(true);
      tinaSolidRenderingAOAzimuthSamplesREd.setValueStep(0.05);
      tinaSolidRenderingAOAzimuthSamplesREd.setText("");
      tinaSolidRenderingAOAzimuthSamplesREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingAOAzimuthSamplesREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingAOAzimuthSamplesREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingAOAzimuthSamplesREd.setLocation(new Point(456, 4));
      tinaSolidRenderingAOAzimuthSamplesREd.setLinkedMotionControlName("tinaSolidRenderingAOAzimuthSamplesSlider");
      tinaSolidRenderingAOAzimuthSamplesREd.setLinkedLabelControlName("tinaSolidRenderingAOAzimuthSamplesLbl");
      tinaSolidRenderingAOAzimuthSamplesREd.setHasMinValue(true);
      tinaSolidRenderingAOAzimuthSamplesREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingAOAzimuthSamplesREd.setBounds(597, 47, 100, 24);
      tinaSolidRenderingAOAzimuthSamplesREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingAOAzimuthSamplesREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingAOAzimuthSamplesREd.isMouseAdjusting() || tinaSolidRenderingAOAzimuthSamplesREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingAOAzimuthSamplesSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingAOAzimuthSamplesREd_changed();
          }
        }
      });
      panel.add(tinaSolidRenderingAOAzimuthSamplesREd);

      tinaSolidRenderingAOAzimuthSamplesSlider = new JSlider();
      tinaSolidRenderingAOAzimuthSamplesSlider.setMinimum(1);
      tinaSolidRenderingAOAzimuthSamplesSlider.setValue(0);
      tinaSolidRenderingAOAzimuthSamplesSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingAOAzimuthSamplesSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingAOAzimuthSamplesSlider.setName("tinaSolidRenderingAOAzimuthSamplesSlider");
      tinaSolidRenderingAOAzimuthSamplesSlider.setMaximum(128);
      tinaSolidRenderingAOAzimuthSamplesSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingAOAzimuthSamplesSlider.setBounds(699, 49, 205, 19);
      tinaSolidRenderingAOAzimuthSamplesSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingAOAzimuthSamplesSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingAOAzimuthSamplesSlider_stateChanged(e);
        }
      });
      panel.add(tinaSolidRenderingAOAzimuthSamplesSlider);

      JLabel lblAffectDiffuse = new JLabel();
      lblAffectDiffuse.setToolTipText("Affect diffuse lighting component for more dramatic effects");
      lblAffectDiffuse.setText("Affect diffuse");
      lblAffectDiffuse.setSize(new Dimension(68, 22));
      lblAffectDiffuse.setPreferredSize(new Dimension(94, 22));
      lblAffectDiffuse.setName("tinaSolidRenderingAOBlurRadiusLbl");
      lblAffectDiffuse.setLocation(new Point(390, 6));
      lblAffectDiffuse.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblAffectDiffuse.setBounds(475, 88, 124, 22);
      panel.add(lblAffectDiffuse);

      tinaSolidRenderingAOAffectDiffuseREd = new JWFNumberField();
      tinaSolidRenderingAOAffectDiffuseREd.setMaxValue(1.0);
      tinaSolidRenderingAOAffectDiffuseREd.setHasMaxValue(true);
      tinaSolidRenderingAOAffectDiffuseREd.setValueStep(0.05);
      tinaSolidRenderingAOAffectDiffuseREd.setText("");
      tinaSolidRenderingAOAffectDiffuseREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingAOAffectDiffuseREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingAOAffectDiffuseREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingAOAffectDiffuseREd.setLocation(new Point(456, 4));
      tinaSolidRenderingAOAffectDiffuseREd.setLinkedMotionControlName("tinaSolidRenderingAOAffectDiffuseSlider");
      tinaSolidRenderingAOAffectDiffuseREd.setLinkedLabelControlName("tinaSolidRenderingAOAffectDiffuseLbl");
      tinaSolidRenderingAOAffectDiffuseREd.setHasMinValue(true);
      tinaSolidRenderingAOAffectDiffuseREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingAOAffectDiffuseREd.setBounds(597, 88, 100, 24);
      tinaSolidRenderingAOAffectDiffuseREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingAOAffectDiffuseREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingAOAffectDiffuseREd.isMouseAdjusting() || tinaSolidRenderingAOAffectDiffuseREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingAOAffectDiffuseSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingAOAffectDiffuseREd_changed();
          }
        }
      });

      panel.add(tinaSolidRenderingAOAffectDiffuseREd);

      tinaSolidRenderingAOAffectDiffuseSlider = new JSlider();
      tinaSolidRenderingAOAffectDiffuseSlider.setValue(0);
      tinaSolidRenderingAOAffectDiffuseSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingAOAffectDiffuseSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingAOAffectDiffuseSlider.setName("tinaSolidRenderingAOAffectDiffuseSlider");
      tinaSolidRenderingAOAffectDiffuseSlider.setMaximum(5000);
      tinaSolidRenderingAOAffectDiffuseSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingAOAffectDiffuseSlider.setBounds(699, 90, 205, 19);
      tinaSolidRenderingAOAffectDiffuseSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingAOAffectDiffuseSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingAOAffectDiffuseSlider_stateChanged(e);
        }
      });

      panel.add(tinaSolidRenderingAOAffectDiffuseSlider);
      panel.add(getLblHintAmbientShadows());

      resetSolidRenderingAmbientShadowOptionsBtn = new JButton();
      resetSolidRenderingAmbientShadowOptionsBtn.setToolTipText("Reset the ambient-shadow-settings to the defaults");
      resetSolidRenderingAmbientShadowOptionsBtn.setText("Reset");
      resetSolidRenderingAmbientShadowOptionsBtn.setPreferredSize(new Dimension(125, 24));
      resetSolidRenderingAmbientShadowOptionsBtn.setMinimumSize(new Dimension(100, 24));
      resetSolidRenderingAmbientShadowOptionsBtn.setMaximumSize(new Dimension(32000, 24));
      resetSolidRenderingAmbientShadowOptionsBtn.setIconTextGap(2);
      resetSolidRenderingAmbientShadowOptionsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetSolidRenderingAmbientShadowOptionsBtn.setBounds(948, 6, 100, 24);
      resetSolidRenderingAmbientShadowOptionsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
      resetSolidRenderingAmbientShadowOptionsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingResetAmbientShadowsBtn_clicked();
          }
        }
      });
      panel.add(resetSolidRenderingAmbientShadowOptionsBtn);
    }
    return panel;
  }

  public JCheckBox getTinaSolidRenderingEnableAOCBx() {
    return tinaSolidRenderingEnableAOCBx;
  }

  public JWFNumberField getTinaSolidRenderingAOIntensityREd() {
    return tinaSolidRenderingAOIntensityREd;
  }

  public JSlider getTinaSolidRenderingAOIntensitySlider() {
    return tinaSolidRenderingAOIntensitySlider;
  }

  public JWFNumberField getTinaSolidRenderingAOSearchRadiusREd() {
    return tinaSolidRenderingAOSearchRadiusREd;
  }

  public JWFNumberField getTinaSolidRenderingAOBlurRadiusREd() {
    return tinaSolidRenderingAOBlurRadiusREd;
  }

  public JWFNumberField getTinaSolidRenderingAOFalloffREd() {
    return tinaSolidRenderingAOFalloffREd;
  }

  public JWFNumberField getTinaSolidRenderingAORadiusSamplesREd() {
    return tinaSolidRenderingAORadiusSamplesREd;
  }

  public JWFNumberField getTinaSolidRenderingAOAzimuthSamplesREd() {
    return tinaSolidRenderingAOAzimuthSamplesREd;
  }

  public JSlider getTinaSolidRenderingAOSearchRadiusSlider() {
    return tinaSolidRenderingAOSearchRadiusSlider;
  }

  public JSlider getTinaSolidRenderingAOBlurRadiusSlider() {
    return tinaSolidRenderingAOBlurRadiusSlider;
  }

  public JSlider getTinaSolidRenderingAOFalloffSlider() {
    return tinaSolidRenderingAOFalloffSlider;
  }

  public JSlider getTinaSolidRenderingAORadiusSamplesSlider() {
    return tinaSolidRenderingAORadiusSamplesSlider;
  }

  public JSlider getTinaSolidRenderingAOAzimuthSamplesSlider() {
    return tinaSolidRenderingAOAzimuthSamplesSlider;
  }

  private JToggleButton getSolidRenderingToggleBtn() {
    if (solidRenderingToggleBtn == null) {
      solidRenderingToggleBtn = new JToggleButton();
      solidRenderingToggleBtn.setText("3D");
      solidRenderingToggleBtn.setToolTipText("Enable solid rendering");
      solidRenderingToggleBtn.setPreferredSize(new Dimension(72, 42));
      solidRenderingToggleBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/kwikdisk-4.png")));
      solidRenderingToggleBtn.setPreferredSize(new Dimension(72, 42));
      solidRenderingToggleBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      solidRenderingToggleBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingCBx_changed();
          }
        }
      });
    }
    return solidRenderingToggleBtn;
  }

  private JLabel getLabel_1() {
    if (label_1 == null) {
      label_1 = new JLabel();
      label_1.setText("");
      label_1.setPreferredSize(new Dimension(42, 12));
      label_1.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return label_1;
  }

  private JLabel getLabel_3() {
    if (label_3 == null) {
      label_3 = new JLabel();
      label_3.setText("");
      label_3.setPreferredSize(new Dimension(42, 12));
      label_3.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return label_3;
  }

  private JToggleButton getAffineXYEditPlaneToggleBtn() {
    if (affineXYEditPlaneToggleBtn == null) {
      affineXYEditPlaneToggleBtn = new JToggleButton();
      affineXYEditPlaneToggleBtn.setText("XY");
      affineXYEditPlaneToggleBtn.setToolTipText("Edit affine transforms in the XY-plane ");
      affineXYEditPlaneToggleBtn.setPreferredSize(new Dimension(42, 24));
      affineXYEditPlaneToggleBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affineXYEditPlaneToggleBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null) {
            tinaController.changeAffineEditPlane(EditPlane.XY);
          }
        }
      });
    }
    return affineXYEditPlaneToggleBtn;
  }

  private JToggleButton getAffineYZEditPlaneToggleBtn() {
    if (affineYZEditPlaneToggleBtn == null) {
      affineYZEditPlaneToggleBtn = new JToggleButton();
      affineYZEditPlaneToggleBtn.setText("YZ");
      affineYZEditPlaneToggleBtn.setToolTipText("Edit affine transforms in the YZ-plane ");
      affineYZEditPlaneToggleBtn.setPreferredSize(new Dimension(42, 24));
      affineYZEditPlaneToggleBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affineYZEditPlaneToggleBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null) {
            tinaController.changeAffineEditPlane(EditPlane.YZ);
          }
        }
      });
    }
    return affineYZEditPlaneToggleBtn;
  }

  private JToggleButton getAffineZXEditPlaneToggleBtn() {
    if (affineZXEditPlaneToggleBtn == null) {
      affineZXEditPlaneToggleBtn = new JToggleButton();
      affineZXEditPlaneToggleBtn.setText("ZX");
      affineZXEditPlaneToggleBtn.setToolTipText("Edit affine transforms in the ZX-plane ");
      affineZXEditPlaneToggleBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      affineZXEditPlaneToggleBtn.setPreferredSize(new Dimension(42, 24));
      affineZXEditPlaneToggleBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null) {
            tinaController.changeAffineEditPlane(EditPlane.ZX);
          }
        }
      });
    }
    return affineZXEditPlaneToggleBtn;
  }

  private JPanel getPanel_4() {
    if (panel_4 == null) {
      panel_4 = new JPanel();
      panel_4.setLayout(null);

      tinaSolidRenderingShadowSmoothRadiusREd = new JWFNumberField();
      tinaSolidRenderingShadowSmoothRadiusREd.setMouseSpeed(0.08);
      tinaSolidRenderingShadowSmoothRadiusREd.setValueStep(0.05);
      tinaSolidRenderingShadowSmoothRadiusREd.setText("");
      tinaSolidRenderingShadowSmoothRadiusREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingShadowSmoothRadiusREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingShadowSmoothRadiusREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingShadowSmoothRadiusREd.setLocation(new Point(456, 4));
      tinaSolidRenderingShadowSmoothRadiusREd.setLinkedMotionControlName("tinaSolidRenderingShadowSmoothRadiusSlider");
      tinaSolidRenderingShadowSmoothRadiusREd.setLinkedLabelControlName("tinaSolidRenderingShadowSmoothRadiusLbl");
      tinaSolidRenderingShadowSmoothRadiusREd.setHasMinValue(true);
      tinaSolidRenderingShadowSmoothRadiusREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingShadowSmoothRadiusREd.setBounds(98, 31, 100, 24);
      tinaSolidRenderingShadowSmoothRadiusREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingShadowSmoothRadiusREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingShadowSmoothRadiusREd.isMouseAdjusting() || tinaSolidRenderingShadowSmoothRadiusREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingShadowSmoothRadiusSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingShadowSmoothRadiusREd_changed();
          }
        }
      });
      panel_4.add(tinaSolidRenderingShadowSmoothRadiusREd);

      JLabel tinaSolidRenderingShadowSmoothRadiusLbl = new JLabel();
      tinaSolidRenderingShadowSmoothRadiusLbl.setText("Smooth radius");
      tinaSolidRenderingShadowSmoothRadiusLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingShadowSmoothRadiusLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingShadowSmoothRadiusLbl.setName("tinaSolidRenderingShadowSmoothRadiusLbl");
      tinaSolidRenderingShadowSmoothRadiusLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingShadowSmoothRadiusLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingShadowSmoothRadiusLbl.setBounds(6, 31, 90, 22);
      panel_4.add(tinaSolidRenderingShadowSmoothRadiusLbl);

      tinaSolidRenderingShadowSmoothRadiusSlider = new JSlider();
      tinaSolidRenderingShadowSmoothRadiusSlider.setValue(0);
      tinaSolidRenderingShadowSmoothRadiusSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingShadowSmoothRadiusSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingShadowSmoothRadiusSlider.setName("tinaSolidRenderingShadowSmoothRadiusSlider");
      tinaSolidRenderingShadowSmoothRadiusSlider.setMaximum(25000);
      tinaSolidRenderingShadowSmoothRadiusSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingShadowSmoothRadiusSlider.setBounds(200, 31, 205, 19);
      tinaSolidRenderingShadowSmoothRadiusSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingShadowSmoothRadiusSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingShadowSmoothRadiusSlider_stateChanged(e);
        }
      });
      panel_4.add(tinaSolidRenderingShadowSmoothRadiusSlider);

      tinaSolidRenderingShadowTypeCmb = new JComboBox();
      tinaSolidRenderingShadowTypeCmb.setToolTipText("");
      tinaSolidRenderingShadowTypeCmb.setPreferredSize(new Dimension(110, 24));
      tinaSolidRenderingShadowTypeCmb.setMinimumSize(new Dimension(110, 24));
      tinaSolidRenderingShadowTypeCmb.setMaximumSize(new Dimension(32767, 24));
      tinaSolidRenderingShadowTypeCmb.setMaximumRowCount(48);
      tinaSolidRenderingShadowTypeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingShadowTypeCmb.setBounds(98, 5, 100, 24);
      tinaSolidRenderingShadowTypeCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingShadowTypeCmb_changed();
          }
        }
      });
      panel_4.add(tinaSolidRenderingShadowTypeCmb);

      JLabel tinaSolidRenderingShadowTypeLbl = new JLabel();
      tinaSolidRenderingShadowTypeLbl.setText("Shadow type");
      tinaSolidRenderingShadowTypeLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingShadowTypeLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingShadowTypeLbl.setName("tinaSolidRenderingShadowTypeLbl");
      tinaSolidRenderingShadowTypeLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingShadowTypeLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingShadowTypeLbl.setBounds(6, 6, 90, 22);
      panel_4.add(tinaSolidRenderingShadowTypeLbl);

      JLabel tinaSolidRenderingShadowmapSizeLbl = new JLabel();
      tinaSolidRenderingShadowmapSizeLbl.setText("Shadowmap size");
      tinaSolidRenderingShadowmapSizeLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingShadowmapSizeLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingShadowmapSizeLbl.setName("tinaSolidRenderingShadowmapSizeLbl");
      tinaSolidRenderingShadowmapSizeLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingShadowmapSizeLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingShadowmapSizeLbl.setBounds(417, 6, 90, 22);
      panel_4.add(tinaSolidRenderingShadowmapSizeLbl);

      tinaSolidRenderingShadowmapSizeCmb = new JComboBox();
      tinaSolidRenderingShadowmapSizeCmb.setToolTipText("");
      tinaSolidRenderingShadowmapSizeCmb.setPreferredSize(new Dimension(110, 24));
      tinaSolidRenderingShadowmapSizeCmb.setMinimumSize(new Dimension(110, 24));
      tinaSolidRenderingShadowmapSizeCmb.setMaximumSize(new Dimension(32767, 24));
      tinaSolidRenderingShadowmapSizeCmb.setMaximumRowCount(48);
      tinaSolidRenderingShadowmapSizeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingShadowmapSizeCmb.setBounds(509, 5, 100, 24);
      tinaSolidRenderingShadowmapSizeCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingShadowmapSizeCmb_changed();
          }
        }
      });
      panel_4.add(tinaSolidRenderingShadowmapSizeCmb);

      JLabel tinaSolidRenderingShadowmapBiasLbl = new JLabel();
      tinaSolidRenderingShadowmapBiasLbl.setText("Shadowmap bias");
      tinaSolidRenderingShadowmapBiasLbl.setSize(new Dimension(68, 22));
      tinaSolidRenderingShadowmapBiasLbl.setPreferredSize(new Dimension(94, 22));
      tinaSolidRenderingShadowmapBiasLbl.setName("tinaSolidRenderingShadowmapBiasLbl");
      tinaSolidRenderingShadowmapBiasLbl.setLocation(new Point(390, 6));
      tinaSolidRenderingShadowmapBiasLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      tinaSolidRenderingShadowmapBiasLbl.setBounds(417, 31, 90, 22);
      panel_4.add(tinaSolidRenderingShadowmapBiasLbl);

      tinaSolidRenderingShadowmapBiasREd = new JWFNumberField();
      tinaSolidRenderingShadowmapBiasREd.setMouseSpeed(0.001);
      tinaSolidRenderingShadowmapBiasREd.setToolTipText("Change this value if you see stripes or unwanted dots in shadow regions, depends on fractal scale and shadowmap-size");
      tinaSolidRenderingShadowmapBiasREd.setValueStep(0.05);
      tinaSolidRenderingShadowmapBiasREd.setText("");
      tinaSolidRenderingShadowmapBiasREd.setSize(new Dimension(100, 24));
      tinaSolidRenderingShadowmapBiasREd.setPreferredSize(new Dimension(100, 24));
      tinaSolidRenderingShadowmapBiasREd.setMotionPropertyName("camPosZ");
      tinaSolidRenderingShadowmapBiasREd.setLocation(new Point(456, 4));
      tinaSolidRenderingShadowmapBiasREd.setLinkedMotionControlName("tinaSolidRenderingShadowmapBiasSlider");
      tinaSolidRenderingShadowmapBiasREd.setLinkedLabelControlName("tinaSolidRenderingShadowmapBiasLbl");
      tinaSolidRenderingShadowmapBiasREd.setHasMinValue(true);
      tinaSolidRenderingShadowmapBiasREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaSolidRenderingShadowmapBiasREd.setBounds(509, 31, 100, 24);
      tinaSolidRenderingShadowmapBiasREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      tinaSolidRenderingShadowmapBiasREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!tinaSolidRenderingShadowmapBiasREd.isMouseAdjusting() || tinaSolidRenderingShadowmapBiasREd.getMouseChangeCount() == 0) {
              if (!tinaSolidRenderingShadowmapBiasSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingShadowmapBiasREd_changed();
          }
        }
      });
      panel_4.add(tinaSolidRenderingShadowmapBiasREd);

      tinaSolidRenderingShadowmapBiasSlider = new JSlider();
      tinaSolidRenderingShadowmapBiasSlider.setValue(0);
      tinaSolidRenderingShadowmapBiasSlider.setSize(new Dimension(205, 19));
      tinaSolidRenderingShadowmapBiasSlider.setPreferredSize(new Dimension(220, 19));
      tinaSolidRenderingShadowmapBiasSlider.setName("tinaSolidRenderingShadowmapBiasSlider");
      tinaSolidRenderingShadowmapBiasSlider.setMaximum(500);
      tinaSolidRenderingShadowmapBiasSlider.setLocation(new Point(558, 4));
      tinaSolidRenderingShadowmapBiasSlider.setBounds(611, 31, 205, 19);
      tinaSolidRenderingShadowmapBiasSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      tinaSolidRenderingShadowmapBiasSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingShadowmapBiasSlider_stateChanged(e);
        }
      });
      panel_4.add(tinaSolidRenderingShadowmapBiasSlider);

      resetSolidRenderingHardShadowOptionsBtn = new JButton();
      resetSolidRenderingHardShadowOptionsBtn.setToolTipText("Reset the hard-shadow-settings to the defaults");
      resetSolidRenderingHardShadowOptionsBtn.setText("Reset");
      resetSolidRenderingHardShadowOptionsBtn.setPreferredSize(new Dimension(125, 24));
      resetSolidRenderingHardShadowOptionsBtn.setMinimumSize(new Dimension(100, 24));
      resetSolidRenderingHardShadowOptionsBtn.setMaximumSize(new Dimension(32000, 24));
      resetSolidRenderingHardShadowOptionsBtn.setIconTextGap(2);
      resetSolidRenderingHardShadowOptionsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      resetSolidRenderingHardShadowOptionsBtn.setBounds(948, 5, 100, 24);
      resetSolidRenderingHardShadowOptionsBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));
      resetSolidRenderingHardShadowOptionsBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().solidRenderingResetHardShadowsBtn_clicked();
          }
        }
      });
      panel_4.add(resetSolidRenderingHardShadowOptionsBtn);
    }
    return panel_4;
  }

  public JWFNumberField getTinaSolidRenderingAOAffectDiffuseREd() {
    return tinaSolidRenderingAOAffectDiffuseREd;
  }

  public JSlider getTinaSolidRenderingAOAffectDiffuseSlider() {
    return tinaSolidRenderingAOAffectDiffuseSlider;
  }

  private JLabel getLblHintAmbientShadows() {
    if (lblHintAmbientShadows == null) {
      lblHintAmbientShadows = new JLabel();
      lblHintAmbientShadows.setText("Note: Ambient-shadow-calculation is a post-effect which is applied after rendering, so it can not be displayed during progressive rendering");
      lblHintAmbientShadows.setSize(new Dimension(68, 22));
      lblHintAmbientShadows.setPreferredSize(new Dimension(94, 22));
      lblHintAmbientShadows.setName("tinaCameraRollLbl");
      lblHintAmbientShadows.setLocation(new Point(4, 4));
      lblHintAmbientShadows.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblHintAmbientShadows.setBounds(250, 2, 718, 22);
    }
    return lblHintAmbientShadows;
  }

  public JComboBox getTinaSolidRenderingMaterialReflectionMappingCmb() {
    return tinaSolidRenderingMaterialReflectionMappingCmb;
  }

  private JButton getSendFlameToIRButton() {
    if (sendFlameToIRButton == null) {
      sendFlameToIRButton = new JButton();
      sendFlameToIRButton.setToolTipText("Send the current flame to the Interactive Renderer");
      sendFlameToIRButton.setText("IR");
      sendFlameToIRButton.setPreferredSize(new Dimension(72, 24));
      sendFlameToIRButton.setMnemonic(KeyEvent.VK_I);
      sendFlameToIRButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      sendFlameToIRButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/fraqtive.png")));
      sendFlameToIRButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.sendFlameToIRButton_clicked();
        }
      });
    }
    return sendFlameToIRButton;
  }

  public JWFNumberField getTinaSolidRenderingShadowIntensityREd() {
    return tinaSolidRenderingShadowIntensityREd;
  }

  public JSlider getTinaSolidRenderingShadowIntensitySlider() {
    return tinaSolidRenderingShadowIntensitySlider;
  }

  public JComboBox getTinaSolidRenderingShadowmapSizeCmb() {
    return tinaSolidRenderingShadowmapSizeCmb;
  }

  public JComboBox getTinaSolidRenderingShadowTypeCmb() {
    return tinaSolidRenderingShadowTypeCmb;
  }

  public JWFNumberField getTinaSolidRenderingShadowSmoothRadiusREd() {
    return tinaSolidRenderingShadowSmoothRadiusREd;
  }

  public JSlider getTinaSolidRenderingShadowSmoothRadiusSlider() {
    return tinaSolidRenderingShadowSmoothRadiusSlider;
  }

  public JButton getResetSolidRenderingHardShadowOptionsBtn() {
    return resetSolidRenderingHardShadowOptionsBtn;
  }

  public JButton getResetSolidRenderingAmbientShadowOptionsBtn() {
    return resetSolidRenderingAmbientShadowOptionsBtn;
  }

  public JWFNumberField getTinaSolidRenderingShadowmapBiasREd() {
    return tinaSolidRenderingShadowmapBiasREd;
  }

  public JSlider getTinaSolidRenderingShadowmapBiasSlider() {
    return tinaSolidRenderingShadowmapBiasSlider;
  }

  private JPanel getPanel_5() {
    if (panel_5 == null) {
      panel_5 = new JPanel();
      panel_5.setLayout(null);

      tinaZBufferScaleREd = new JWFNumberField();
      tinaZBufferScaleREd.setToolTipText("Scale applied to the z-buffer before exporting it. Positive values: black background, white near camera. Negative values: vice versa");
      tinaZBufferScaleREd.setValueStep(0.05);
      tinaZBufferScaleREd.setText("");
      tinaZBufferScaleREd.setSize(new Dimension(100, 24));
      tinaZBufferScaleREd.setPreferredSize(new Dimension(100, 24));
      tinaZBufferScaleREd.setMotionPropertyName("centreX");
      tinaZBufferScaleREd.setLocation(new Point(456, 4));
      tinaZBufferScaleREd.setLinkedMotionControlName("tinaZBufferScaleSlider");
      tinaZBufferScaleREd.setLinkedLabelControlName("tinaZBufferScaleLbl");
      tinaZBufferScaleREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      tinaZBufferScaleREd.setBounds(90, 8, 100, 24);
      tinaZBufferScaleREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (!tinaZBufferScaleREd.isMouseAdjusting() || tinaZBufferScaleREd.getMouseChangeCount() == 0) {
            if (!tinaZBufferScaleSlider.getValueIsAdjusting()) {
              tinaController.saveUndoPoint();
            }
          }
          tinaController.getFlameControls().zBufferScaleREd_changed();
        }
      });

      panel_5.add(tinaZBufferScaleREd);

      JLabel lblZbufferScale = new JLabel();
      lblZbufferScale.setText("ZBuffer scale");
      lblZbufferScale.setSize(new Dimension(68, 22));
      lblZbufferScale.setPreferredSize(new Dimension(94, 22));
      lblZbufferScale.setName("tinaZBufferScaleLbl");
      lblZbufferScale.setLocation(new Point(390, 6));
      lblZbufferScale.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblZbufferScale.setBounds(6, 8, 84, 22);
      panel_5.add(lblZbufferScale);

      tinaZBufferScaleSlider = new JSlider();
      tinaZBufferScaleSlider.setValue(0);
      tinaZBufferScaleSlider.setSize(new Dimension(205, 19));
      tinaZBufferScaleSlider.setPreferredSize(new Dimension(220, 19));
      tinaZBufferScaleSlider.setName("tinaZBufferScaleSlider");
      tinaZBufferScaleSlider.setMinimum(-25000);
      tinaZBufferScaleSlider.setMaximum(25000);
      tinaZBufferScaleSlider.setLocation(new Point(558, 4));
      tinaZBufferScaleSlider.setBounds(192, 8, 205, 19);
      tinaZBufferScaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().zBufferScaleSlider_changed();
        }
      });

      panel_5.add(tinaZBufferScaleSlider);
    }
    return panel_5;
  }

  public JWFNumberField getTinaZBufferScaleREd() {
    return tinaZBufferScaleREd;
  }

  public JSlider getTinaZBufferScaleSlider() {
    return tinaZBufferScaleSlider;
  }

  private JButton getButton_1() {
    if (button_1 == null) {
      button_1 = new JButton();
      button_1.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            tinaController.getFlameControls().randomizeLightColor();
          }
        }
      });
      button_1.setToolTipText("Randomize light color");
      button_1.setSize(new Dimension(95, 24));
      button_1.setSelected(false);
      button_1.setPreferredSize(new Dimension(42, 24));
      button_1.setLocation(new Point(4, 4));
      button_1.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/roll.png")));
      button_1.setBounds(582, 12, 42, 24);
    }
    return button_1;
  }

  public JPanel getPostBokehSettingsPnl() {
    return postBokehSettingsPnl;
  }

  private JWFNumberField getPostBokehBrightnessREd() {
    if (postBokehBrightnessREd == null) {
      postBokehBrightnessREd = new JWFNumberField();
      postBokehBrightnessREd.setLinkedLabelControlName("postBokehBrightnessLbl");
      postBokehBrightnessREd.setValueStep(0.05);
      postBokehBrightnessREd.setText("");
      postBokehBrightnessREd.setSize(new Dimension(100, 24));
      postBokehBrightnessREd.setPreferredSize(new Dimension(100, 24));
      postBokehBrightnessREd.setMaxValue(5.0);
      postBokehBrightnessREd.setLocation(new Point(584, 2));
      postBokehBrightnessREd.setLinkedMotionControlName("postBokehBrightnessSlider");
      postBokehBrightnessREd.setHasMinValue(true);
      postBokehBrightnessREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      postBokehBrightnessREd.setEditable(true);
      postBokehBrightnessREd.setBounds(558, 28, 100, 24);
      postBokehBrightnessREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      postBokehBrightnessREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!postBokehBrightnessREd.isMouseAdjusting() || postBokehBrightnessREd.getMouseChangeCount() == 0) {
              if (!postBokehBrightnessSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingPostBokehBrightnessREd_changed();
          }
        }
      });
    }
    return postBokehBrightnessREd;
  }

  private JLabel getLblBokehBrightness() {
    if (lblBokehBrightness == null) {
      lblBokehBrightness = new JLabel();
      lblBokehBrightness.setName("postBokehBrightnessLbl");
      lblBokehBrightness.setText("Bokeh brightness");
      lblBokehBrightness.setSize(new Dimension(94, 22));
      lblBokehBrightness.setPreferredSize(new Dimension(94, 22));
      lblBokehBrightness.setLocation(new Point(488, 2));
      lblBokehBrightness.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBokehBrightness.setBounds(449, 28, 107, 22);
    }
    return lblBokehBrightness;
  }

  private JSlider getPostBokehBrightnessSlider() {
    if (postBokehBrightnessSlider == null) {
      postBokehBrightnessSlider = new JSlider();
      postBokehBrightnessSlider.setValue(0);
      postBokehBrightnessSlider.setSize(new Dimension(220, 19));
      postBokehBrightnessSlider.setPreferredSize(new Dimension(220, 19));
      postBokehBrightnessSlider.setName("tinaFilterRadiusSlider");
      postBokehBrightnessSlider.setMinimum(0);
      postBokehBrightnessSlider.setMaximum(20000);
      postBokehBrightnessSlider.setLocation(new Point(686, 2));
      postBokehBrightnessSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      postBokehBrightnessSlider.setBounds(660, 28, 220, 24);
      postBokehBrightnessSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postBokehBrightnessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingPostBokehBrightnessSlider_stateChanged(e);
        }
      });
    }
    return postBokehBrightnessSlider;
  }

  private JLabel getLblBokehActivation() {
    if (lblBokehActivation == null) {
      lblBokehActivation = new JLabel();
      lblBokehActivation.setName("postBokehActivationLbl");
      lblBokehActivation.setText("Bokeh activation");
      lblBokehActivation.setSize(new Dimension(94, 22));
      lblBokehActivation.setPreferredSize(new Dimension(94, 22));
      lblBokehActivation.setLocation(new Point(488, 2));
      lblBokehActivation.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBokehActivation.setBounds(449, 50, 107, 22);
    }
    return lblBokehActivation;
  }

  private JWFNumberField getPostBokehActivationREd() {
    if (postBokehActivationREd == null) {
      postBokehActivationREd = new JWFNumberField();
      postBokehActivationREd.setLinkedLabelControlName("postBokehActivationLbl");
      postBokehActivationREd.setValueStep(0.05);
      postBokehActivationREd.setText("");
      postBokehActivationREd.setSize(new Dimension(100, 24));
      postBokehActivationREd.setPreferredSize(new Dimension(100, 24));
      postBokehActivationREd.setMaxValue(5.0);
      postBokehActivationREd.setLocation(new Point(584, 2));
      postBokehActivationREd.setLinkedMotionControlName("postBokehActivationSlider");
      postBokehActivationREd.setHasMinValue(true);
      postBokehActivationREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      postBokehActivationREd.setEditable(true);
      postBokehActivationREd.setBounds(558, 50, 100, 24);
      postBokehActivationREd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameControls().editMotionCurve(e);
        }
      });
      postBokehActivationREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getFlameControls() != null) {
            if (!postBokehActivationREd.isMouseAdjusting() || postBokehActivationREd.getMouseChangeCount() == 0) {
              if (!postBokehActivationSlider.getValueIsAdjusting()) {
                tinaController.saveUndoPoint();
              }
            }
            tinaController.getFlameControls().solidRenderingPostBokehActivationREd_changed();
          }
        }
      });
    }
    return postBokehActivationREd;
  }

  private JSlider getPostBokehActivationSlider() {
    if (postBokehActivationSlider == null) {
      postBokehActivationSlider = new JSlider();
      postBokehActivationSlider.setValue(0);
      postBokehActivationSlider.setSize(new Dimension(220, 19));
      postBokehActivationSlider.setPreferredSize(new Dimension(220, 19));
      postBokehActivationSlider.setName("tinaFilterRadiusSlider");
      postBokehActivationSlider.setMinimum(0);
      postBokehActivationSlider.setMaximum(255000);
      postBokehActivationSlider.setLocation(new Point(686, 2));
      postBokehActivationSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      postBokehActivationSlider.setBounds(660, 50, 220, 24);
      postBokehActivationSlider.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          tinaController.saveUndoPoint();
        }
      });
      postBokehActivationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.getFlameControls().solidRenderingPostBokehActivationSlider_stateChanged(e);
        }
      });
    }
    return postBokehActivationSlider;
  }

  public JWFNumberField getPostBokehIntensityREd() {
    return postBokehIntensityREd;
  }

  public JSlider getPostBokehIntensitySlider() {
    return postBokehIntensitySlider;
  }

  public JSlider getPostBokehSizeSlider() {
    return postBokehSizeSlider;
  }

  public JWFNumberField getPostBokehSizeREd() {
    return postBokehSizeREd;
  }

  public JComboBox getPostBokehFilterKernelCmb() {
    return postBokehFilterKernelCmb;
  }

  public JButton getResetPostBokehSettingsBtn() {
    return resetPostBokehSettingsBtn;
  }

  public JToggleButton getNonlinearParams1ToggleParamsPnlButton() {
    return nonlinearParams1ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams2ToggleParamsPnlButton() {
    return nonlinearParams2ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams3ToggleParamsPnlButton() {
    return nonlinearParams3ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams4ToggleParamsPnlButton() {
    return nonlinearParams4ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams5ToggleParamsPnlButton() {
    return nonlinearParams5ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams6ToggleParamsPnlButton() {
    return nonlinearParams6ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams7ToggleParamsPnlButton() {
    return nonlinearParams7ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams8ToggleParamsPnlButton() {
    return nonlinearParams8ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams9ToggleParamsPnlButton() {
    return nonlinearParams9ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams10ToggleParamsPnlButton() {
    return nonlinearParams10ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams11ToggleParamsPnlButton() {
    return nonlinearParams11ToggleParamsPnlButton;
  }

  public JToggleButton getNonlinearParams12ToggleParamsPnlButton() {
    return nonlinearParams12ToggleParamsPnlButton;
  }

  public JWFNumberField getLowDensityBrightnessREd() {
    return lowDensityBrightnessREd;
  }

  public JSlider getLowDensityBrightnessSlider() {
    return lowDensityBrightnessSlider;
  }

  public JSlider getBalanceRedSlider() {
    return balanceRedSlider;
  }

  public JWFNumberField getBalanceRedREd() {
    return balanceRedREd;
  }

  public JSlider getBalanceGreenSlider() {
    return balanceGreenSlider;
  }

  public JWFNumberField getBalanceGreenREd() {
    return balanceGreenREd;
  }

  public JSlider getBalanceBlueSlider() {
    return balanceBlueSlider;
  }

  public JWFNumberField getBalanceBlueREd() {
    return balanceBlueREd;
  }

  public JComboBox getBackgroundColorTypeCmb() {
    return backgroundColorTypeCmb;
  }

  public JButton getBackgroundColorURIndicatorBtn() {
    return backgroundColorURIndicatorBtn;
  }

  public JButton getBackgroundColorLLIndicatorBtn() {
    return backgroundColorLLIndicatorBtn;
  }

  public JButton getBackgroundColorLRIndicatorBtn() {
    return backgroundColorLRIndicatorBtn;
  }

  public JButton getBackgroundColorCCIndicatorBtn() {
    return backgroundColorCCIndicatorBtn;
  }

  public JComboBox getTinaFilterTypeCmb() {
    return tinaFilterTypeCmb;
  }

  public JLabel getTinaFilterKernelCmbLbl() {
    return tinaFilterKernelLbl;
  }

  public JLabel getTinaFilterRadiusLbl() {
    return tinaFilterRadiusLbl;
  }

  public JCheckBox getTinaFilterIndicatorCBx() {
    return tinaFilterIndicatorCBx;
  }

  private JPopupMenu getThumbnailSelectPopupMenu() {
    if (thumbnailSelectPopupMenu == null) {
      thumbnailSelectPopupMenu = new JPopupMenu();
      thumbnailSelectPopupMenu.add(getMntmNewMenuItem());
      thumbnailSelectPopupMenu.add(getMntmDeselectAll());
    }
    return thumbnailSelectPopupMenu;
  }

  private JPopupMenu getThumbnailRemovePopupMenu() {
    if (thumbnailRemovePopupMenu == null) {
      thumbnailRemovePopupMenu = new JPopupMenu();
      thumbnailRemovePopupMenu.add(getMntmRemoveSelected());
      thumbnailRemovePopupMenu.add(getMntmRemoveAll());
    }
    return thumbnailRemovePopupMenu;
  }

  private static void addPopup(Component component, final JPopupMenu popup) {
    component.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showMenu(e);
        }
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showMenu(e);
        }
      }

      private void showMenu(MouseEvent e) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    });
  }

  private JMenuItem getMntmNewMenuItem() {
    if (mntmNewMenuItem == null) {
      mntmNewMenuItem = new JMenuItem("Toggle all");
      mntmNewMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.toggleThumbnailSelectionAll();
        }
      });
    }
    return mntmNewMenuItem;
  }

  private JMenuItem getMntmRemoveAll() {
    if (mntmRemoveAll == null) {
      mntmRemoveAll = new JMenuItem("Remove all");
      mntmRemoveAll.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.removeAllThumbnails();
        }
      });
    }
    return mntmRemoveAll;
  }

  private JMenuItem getMntmDeselectAll() {
    if (mntmDeselectAll == null) {
      mntmDeselectAll = new JMenuItem("Deselect all");
      mntmDeselectAll.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.deselectAllThumbnails();
        }
      });
    }
    return mntmDeselectAll;
  }

  private JMenuItem getMntmRemoveSelected() {
    if (mntmRemoveSelected == null) {
      mntmRemoveSelected = new JMenuItem("Remove selected");
      mntmRemoveSelected.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.removeSelectedThumbnails();
        }
      });

    }
    return mntmRemoveSelected;
  }

  public JWFNumberField getTinaFilterSharpnessREd() {
    return tinaFilterSharpnessREd;
  }

  public JSlider getTinaFilterSharpnessSlider() {
    return tinaFilterSharpnessSlider;
  }

  public JSlider getTinaFilterLowDensitySlider() {
    return tinaFilterLowDensitySlider;
  }

  public JWFNumberField getTinaFilterLowDensityREd() {
    return tinaFilterLowDensityREd;
  }
} //  @jve:decl-index=0:visual-constraint="10,10"
