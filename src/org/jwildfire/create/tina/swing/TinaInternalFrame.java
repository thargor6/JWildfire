package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.RandomFlameGeneratorStyle;
import org.jwildfire.create.tina.render.AffineZStyle;
import org.jwildfire.create.tina.swing.TinaController.NonlinearControlsRow;
import org.jwildfire.create.tina.transform.AnimationService;
import org.jwildfire.swing.StandardErrorHandler;

public class TinaInternalFrame extends JInternalFrame {
  private TinaController tinaController; //  @jve:decl-index=0:
  private NonlinearControlsRow[] nonlinearControlsRows;//  @jve:decl-index=0:
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;

  private JPanel tinaNorthPanel = null;

  private JPanel tinaWestPanel = null;

  private JPanel tinaEastPanel = null;

  private JPanel tinaSouthPanel = null;

  private JPanel tinaCenterPanel = null;

  private JTabbedPane tinaSouthTabbedPane = null;

  private JPanel tinaCameraPanel = null;

  private JPanel tinaColoringPanel = null;

  private JLabel tinaCameraRollLbl = null;

  private JTextField tinaCameraRollREd = null;

  private JLabel tinaCameraPitchLbl = null;

  private JTextField tinaCameraPitchREd = null;

  private JLabel tinaCameraYawLbl = null;

  private JTextField tinaCameraYawREd = null;

  private JLabel tinaCameraPerspectiveLbl = null;

  private JTextField tinaCameraPerspectiveREd = null;

  private JSlider tinaCameraRollSlider = null;

  private JSlider tinaCameraPitchSlider = null;

  private JSlider tinaCameraYawSlider = null;

  private JSlider tinaCameraPerspectiveSlider = null;

  private JButton tinaLoadFlameButton = null;

  private JButton tinaSaveFlameButton = null;

  private JButton tinaRenderFlameButton = null;

  private JLabel tinaCameraPreviewQualityLbl = null;

  private JTextField tinaPreviewQualityREd = null;

  private JButton tinaExportImageButton = null;

  private JLabel tinaCameraCentreXLbl = null;

  private JTextField tinaCameraCentreXREd = null;

  private JLabel tinaCameraCentreYLbl = null;

  private JTextField tinaCameraCentreYREd = null;

  private JSlider tinaCameraCentreXSlider = null;

  private JSlider tinaCameraCentreYSlider = null;

  private JLabel tinaCameraZoomLbl = null;

  private JTextField tinaCameraZoomREd = null;

  private JSlider tinaCameraZoomSlider = null;

  private JLabel tinaBrightnessLbl = null;

  private JTextField tinaBrightnessREd = null;

  private JSlider tinaBrightnessSlider = null;

  private JLabel tinaContrastLbl = null;

  private JLabel tinaGammaLbl = null;

  private JLabel tinaVibrancyLbl = null;

  private JLabel tinaFilterRadiusLbl = null;

  private JLabel tinaOversampleLbl = null;

  private JLabel tinaPixelsPerUnitLbl = null;

  private JTextField tinaPixelsPerUnitREd = null;

  private JSlider tinaPixelsPerUnitSlider = null;

  private JLabel tinaCameraRenderQualityLbl = null;

  private JTextField tinaRenderQualityREd = null;

  private JLabel tinaBGColorLbl = null;

  private JTextField tinaBGColorRedREd = null;

  private JTextField tinaBGColorGreenREd = null;

  private JTextField tinaBGColorBlueREd = null;

  private JSlider tinaBGColorRedSlider = null;

  private JSlider tinaBGColorGreenSlider = null;

  private JSlider tinaBGColorBlueSlider = null;

  private JTextField tinaContrastREd = null;

  private JTextField tinaGammaREd = null;

  private JTextField tinaVibrancyREd = null;

  private JTextField tinaFilterRadiusREd = null;

  private JTextField tinaOversampleREd = null;

  private JSlider tinaContrastSlider = null;

  private JSlider tinaGammaSlider = null;

  private JSlider tinaVibrancySlider = null;

  private JSlider tinaFilterRadiusSlider = null;

  private JSlider tinaOversampleSlider = null;

  private JButton tinaAddTransformationButton = null;

  private JTabbedPane tinaNorthTabbedPane = null;

  private JPanel tinaNorthMainPanel = null;

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

  private JButton tinaGrabPaletteFromImageButton = null;

  private JPanel tinaPaletteImgPanel = null;

  private JPanel tinaPaletteSubNorthPanel = null;

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

  private JTextField tinaPaletteShiftREd = null;

  private JTextField tinaPaletteRedREd = null;

  private JTextField tinaPaletteGreenREd = null;

  private JTextField tinaPaletteBlueREd = null;

  private JTextField tinaPaletteHueREd = null;

  private JTextField tinaPaletteSaturationREd = null;

  private JTextField tinaPaletteContrastREd = null;

  private JTextField tinaPaletteGammaREd = null;

  private JTextField tinaPaletteBrightnessREd = null;

  private JSlider tinaPaletteShiftSlider = null;

  private JSlider tinaPaletteRedSlider = null;

  private JSlider tinaPaletteGreenSlider = null;

  private JSlider tinaPaletteBlueSlider = null;

  private JSlider tinaPaletteHueSlider = null;

  private JSlider tinaPaletteSaturationSlider = null;

  private JSlider tinaPaletteContrastSlider = null;

  private JSlider tinaPaletteGammaSlider = null;

  private JSlider tinaPaletteBrightnessSlider = null;

  private JLabel tinaRenderSizeLbl = null;

  private JTextField tinaRenderWidthREd = null;

  private JTextField tinaRenderHeightREd = null;
  private JButton tinaAddFinalTransformationButton = null;
  private JLabel affineC00Lbl = null;
  private JTextField affineC00REd = null;
  private JLabel affineC01Lbl = null;
  private JTextField affineC01REd = null;
  private JLabel affineC10Lbl = null;
  private JLabel affineC11Lbl = null;
  private JTextField affineC10REd = null;
  private JTextField affineC11REd = null;
  private JLabel affineC20Lbl = null;
  private JLabel affineC21Lbl = null;
  private JTextField affineC20REd = null;
  private JTextField affineC21REd = null;
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
  private JPanel createGapPanel = null;
  private JPanel randomBatchPanel = null;
  private JButton randomBatchButton = null;
  private JLabel nonlinearVar1Lbl = null;
  private JComboBox nonlinearVar1Cmb = null;
  private JTextField nonlinearVar1REd = null;
  private JLabel nonlinearParams1Lbl = null;
  private JComboBox nonlinearParams1Cmb = null;
  private JTextField nonlinearParams1REd = null;
  private JPanel nonlinearVar1Panel = null;
  private JButton nonlinearVar1LeftButton = null;
  private JButton nonlinarVar1RightButton = null;
  private JButton nonlinearParams1LeftButton = null;
  private JButton nonlinarParams1RightButton = null;
  private JPanel nonlinearVar2Panel = null;
  private JLabel nonlinearVar2Lbl = null;
  private JComboBox nonlinearVar2Cmb = null;
  private JTextField nonlinearVar2REd = null;
  private JLabel nonlinearParams2Lbl = null;
  private JComboBox nonlinearParams2Cmb = null;
  private JTextField nonlinearParams2REd = null;
  private JButton nonlinearVar2LeftButton = null;
  private JButton nonlinarVar2RightButton = null;
  private JButton nonlinearParams2LeftButton = null;
  private JButton nonlinarParams2RightButton = null;
  private JPanel nonlinearVar3Panel = null;
  private JLabel nonlinearVar3Lbl = null;
  private JComboBox nonlinearVar3Cmb = null;
  private JTextField nonlinearVar3REd = null;
  private JLabel nonlinearParams3Lbl = null;
  private JComboBox nonlinearParams3Cmb = null;
  private JTextField nonlinearParams3REd = null;
  private JButton nonlinearVar3LeftButton = null;
  private JButton nonlinarVar3RightButton = null;
  private JButton nonlinearParams3LeftButton = null;
  private JButton nonlinarParams3RightButton = null;
  private JLabel xFormColorLbl = null;
  private JTextField xFormColorREd = null;
  private JSlider xFormColorSlider = null;
  private JLabel xFormSymmetryLbl = null;
  private JTextField xFormSymmetryREd = null;
  private JSlider xFormSymmetrySlider = null;
  private JLabel xFormOpacityLbl = null;
  private JTextField xFormOpacityREd = null;
  private JSlider xFormOpacitySlider = null;
  private JLabel xFormDrawModeLbl = null;
  private JComboBox xFormDrawModeCmb = null;
  private JPanel relWeightsEastPanel = null;
  private JButton relWeightsLeftButton = null;
  private JButton relWeightsRightButton = null;
  private JScrollPane relWeightsScrollPane = null;
  private JTable relWeightsTable = null;
  private JPanel transformationWeightPanel = null;
  private JButton transformationWeightLeftButton = null;
  private JButton transformationWeightRightButton = null;
  private JButton newFlameButton = null;
  private JLabel zStyleLbl = null;
  private JComboBox zStyleCmb = null;
  private JPanel tinaAnimatePanel = null;
  private JPanel tinaMorphPanel = null;
  private JButton setMorphFlame1Button = null;
  private JButton setMorphFlame2Button = null;
  private JLabel morphFramesLbl = null;
  private JTextField morphFramesREd = null;
  private JLabel morphFrameLbl = null;
  private JTextField morphFrameREd = null;
  private JCheckBox morphCheckBox = null;
  private JSlider morphFrameSlider = null;
  private JButton importMorphedFlameButton = null;
  private JButton animationGenerateButton = null;
  private JTextField animateFramesREd = null;
  private JLabel animateFramesLbl = null;
  private JTextField animateOutputREd = null;
  private JLabel animateOutputLbl = null;
  private JLabel animateGlobalScriptLbl = null;
  private JComboBox animateScriptCmb = null;
  private JLabel animateXFormScriptLbl = null;
  private JComboBox animateXFormScriptCmb = null;
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

  /**
   * This is the xxx default constructor
   */
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
    this.setSize(988, 680);
    this.setFont(new Font("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(0, 0));
    this.setClosable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setIconifiable(true);
    this.setTitle("Fractal flames");
    this.setVisible(true);
    this.setResizable(true);
    this.setContentPane(getJContentPane());
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
      jContentPane.add(getTinaNorthPanel(), BorderLayout.NORTH);
      jContentPane.add(getTinaWestPanel(), BorderLayout.WEST);
      jContentPane.add(getTinaEastPanel(), BorderLayout.EAST);
      jContentPane.add(getTinaSouthPanel(), BorderLayout.SOUTH);
      jContentPane.add(getTinaCenterPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes tinaNorthPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaNorthPanel() {
    if (tinaNorthPanel == null) {
      tinaNorthPanel = new JPanel();
      tinaNorthPanel.setLayout(new BorderLayout());
      tinaNorthPanel.setPreferredSize(new Dimension(0, 86));
      tinaNorthPanel.add(getTinaNorthTabbedPane(), BorderLayout.CENTER);
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
      tinaEastPanel.setPreferredSize(new Dimension(310, 0));
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
      tinaSouthPanel.setPreferredSize(new Dimension(0, 128));
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
      tinaOversampleLbl = new JLabel();
      tinaOversampleLbl.setText("Oversample");
      tinaOversampleLbl.setLocation(new Point(488, 4));
      tinaOversampleLbl.setSize(new Dimension(94, 22));
      tinaOversampleLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaOversampleLbl.setPreferredSize(new Dimension(94, 22));
      tinaFilterRadiusLbl = new JLabel();
      tinaFilterRadiusLbl.setText("Filter radius");
      tinaFilterRadiusLbl.setLocation(new Point(488, 28));
      tinaFilterRadiusLbl.setSize(new Dimension(94, 22));
      tinaFilterRadiusLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaFilterRadiusLbl.setPreferredSize(new Dimension(94, 22));
      tinaVibrancyLbl = new JLabel();
      tinaVibrancyLbl.setText("Vibrancy");
      tinaVibrancyLbl.setLocation(new Point(4, 76));
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
      tinaColoringPanel.add(tinaOversampleLbl, null);
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
      tinaColoringPanel.add(getTinaOversampleREd(), null);
      tinaColoringPanel.add(getTinaContrastSlider(), null);
      tinaColoringPanel.add(getTinaGammaSlider(), null);
      tinaColoringPanel.add(getTinaVibrancySlider(), null);
      tinaColoringPanel.add(getTinaFilterRadiusSlider(), null);
      tinaColoringPanel.add(getTinaOversampleSlider(), null);
    }
    return tinaColoringPanel;
  }

  /**
   * This method initializes tinaCameraRollREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaCameraRollREd() {
    if (tinaCameraRollREd == null) {
      tinaCameraRollREd = new JTextField();
      tinaCameraRollREd.setPreferredSize(new Dimension(100, 22));
      tinaCameraRollREd.setText("");
      tinaCameraRollREd.setLocation(new Point(100, 4));
      tinaCameraRollREd.setSize(new Dimension(100, 22));
      tinaCameraRollREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraRollREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.cameraRollREd_changed();
        }
      });
      tinaCameraRollREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.cameraRollREd_changed();
        }
      });
    }
    return tinaCameraRollREd;
  }

  /**
   * This method initializes tinaCameraPitchREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaCameraPitchREd() {
    if (tinaCameraPitchREd == null) {
      tinaCameraPitchREd = new JTextField();
      tinaCameraPitchREd.setPreferredSize(new Dimension(100, 22));
      tinaCameraPitchREd.setText("");
      tinaCameraPitchREd.setLocation(new Point(100, 28));
      tinaCameraPitchREd.setSize(new Dimension(100, 22));
      tinaCameraPitchREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraPitchREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.cameraPitchREd_changed();
        }
      });
      tinaCameraPitchREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.cameraPitchREd_changed();
        }
      });
    }
    return tinaCameraPitchREd;
  }

  /**
   * This method initializes tinaCameraYawREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaCameraYawREd() {
    if (tinaCameraYawREd == null) {
      tinaCameraYawREd = new JTextField();
      tinaCameraYawREd.setPreferredSize(new Dimension(100, 22));
      tinaCameraYawREd.setText("");
      tinaCameraYawREd.setLocation(new Point(100, 52));
      tinaCameraYawREd.setSize(new Dimension(100, 22));
      tinaCameraYawREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraYawREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.cameraYawREd_changed();
        }
      });
      tinaCameraYawREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.cameraYawREd_changed();
        }
      });
    }
    return tinaCameraYawREd;
  }

  /**
   * This method initializes tinaCameraPerspectiveREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaCameraPerspectiveREd() {
    if (tinaCameraPerspectiveREd == null) {
      tinaCameraPerspectiveREd = new JTextField();
      tinaCameraPerspectiveREd.setPreferredSize(new Dimension(100, 22));
      tinaCameraPerspectiveREd.setText("");
      tinaCameraPerspectiveREd.setLocation(new Point(100, 76));
      tinaCameraPerspectiveREd.setSize(new Dimension(100, 22));
      tinaCameraPerspectiveREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraPerspectiveREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.cameraPerspectiveREd_changed();
        }
      });
      tinaCameraPerspectiveREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.cameraPerspectiveREd_changed();
        }
      });
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
      tinaLoadFlameButton.setLocation(new Point(497, 4));
      tinaLoadFlameButton.setSize(new Dimension(125, 24));
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
      tinaSaveFlameButton.setText("Save Flame");
      tinaSaveFlameButton.setLocation(new Point(497, 32));
      tinaSaveFlameButton.setSize(new Dimension(125, 24));
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
      tinaRenderFlameButton.setText("R");
      tinaRenderFlameButton.setMnemonic(KeyEvent.VK_R);
      tinaRenderFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaRenderFlameButton.setToolTipText("Render");
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
   * This method initializes tinaPreviewQualityREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPreviewQualityREd() {
    if (tinaPreviewQualityREd == null) {
      tinaPreviewQualityREd = new JTextField();
      tinaPreviewQualityREd.setPreferredSize(new Dimension(56, 22));
      tinaPreviewQualityREd.setText("3");
      tinaPreviewQualityREd.setLocation(new Point(727, 4));
      tinaPreviewQualityREd.setSize(new Dimension(56, 22));
      tinaPreviewQualityREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPreviewQualityREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.previewQualityREd_changed();
        }
      });
      tinaPreviewQualityREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.previewQualityREd_changed();
        }
      });
    }
    return tinaPreviewQualityREd;
  }

  /**
   * This method initializes tinaExportImageButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaExportImageButton() {
    if (tinaExportImageButton == null) {
      tinaExportImageButton = new JButton();
      tinaExportImageButton.setText("Export image");
      tinaExportImageButton.setLocation(new Point(790, 32));
      tinaExportImageButton.setSize(new Dimension(180, 24));
      tinaExportImageButton.setPreferredSize(new Dimension(180, 24));
      tinaExportImageButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaExportImageButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.exportImageButton_actionPerformed(e);
        }
      });
    }
    return tinaExportImageButton;
  }

  /**
   * This method initializes tinaCameraCentreXREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaCameraCentreXREd() {
    if (tinaCameraCentreXREd == null) {
      tinaCameraCentreXREd = new JTextField();
      tinaCameraCentreXREd.setPreferredSize(new Dimension(100, 22));
      tinaCameraCentreXREd.setText("");
      tinaCameraCentreXREd.setLocation(new Point(584, 4));
      tinaCameraCentreXREd.setSize(new Dimension(100, 22));
      tinaCameraCentreXREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraCentreXREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.cameraCentreXREd_changed();
        }
      });
      tinaCameraCentreXREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.cameraCentreXREd_changed();
        }
      });
    }
    return tinaCameraCentreXREd;
  }

  /**
   * This method initializes tinaCameraCentreYREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaCameraCentreYREd() {
    if (tinaCameraCentreYREd == null) {
      tinaCameraCentreYREd = new JTextField();
      tinaCameraCentreYREd.setPreferredSize(new Dimension(100, 22));
      tinaCameraCentreYREd.setText("");
      tinaCameraCentreYREd.setLocation(new Point(584, 28));
      tinaCameraCentreYREd.setSize(new Dimension(100, 22));
      tinaCameraCentreYREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraCentreYREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.cameraCentreYREd_changed();
        }
      });
      tinaCameraCentreYREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.cameraCentreYREd_changed();
        }
      });
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
      tinaCameraCentreXSlider.setMinimum(-10);
      tinaCameraCentreXSlider.setLocation(new Point(686, 4));
      tinaCameraCentreXSlider.setSize(new Dimension(220, 19));
      tinaCameraCentreXSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCentreXSlider.setValue(0);
      tinaCameraCentreXSlider.setMaximum(10);
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
      tinaCameraCentreYSlider.setMinimum(-10);
      tinaCameraCentreYSlider.setLocation(new Point(686, 28));
      tinaCameraCentreYSlider.setSize(new Dimension(220, 19));
      tinaCameraCentreYSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraCentreYSlider.setValue(0);
      tinaCameraCentreYSlider.setMaximum(10);
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
  private JTextField getTinaCameraZoomREd() {
    if (tinaCameraZoomREd == null) {
      tinaCameraZoomREd = new JTextField();
      tinaCameraZoomREd.setPreferredSize(new Dimension(100, 22));
      tinaCameraZoomREd.setText("");
      tinaCameraZoomREd.setLocation(new Point(584, 52));
      tinaCameraZoomREd.setSize(new Dimension(100, 22));
      tinaCameraZoomREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaCameraZoomREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.cameraZoomREd_changed();
        }
      });
      tinaCameraZoomREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.cameraZoomREd_changed();
        }
      });
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
      tinaCameraZoomSlider.setMinimum(0);
      tinaCameraZoomSlider.setLocation(new Point(686, 52));
      tinaCameraZoomSlider.setSize(new Dimension(220, 19));
      tinaCameraZoomSlider.setPreferredSize(new Dimension(220, 19));
      tinaCameraZoomSlider.setValue(0);
      tinaCameraZoomSlider.setMaximum(100);
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
  private JTextField getTinaBrightnessREd() {
    if (tinaBrightnessREd == null) {
      tinaBrightnessREd = new JTextField();
      tinaBrightnessREd.setPreferredSize(new Dimension(100, 22));
      tinaBrightnessREd.setText("");
      tinaBrightnessREd.setSize(new Dimension(100, 22));
      tinaBrightnessREd.setLocation(new Point(100, 4));
      tinaBrightnessREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaBrightnessREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.brightnessREd_changed();
        }
      });
      tinaBrightnessREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.brightnessREd_changed();
        }
      });
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
      tinaBrightnessSlider.setMinimum(0);
      tinaBrightnessSlider.setLocation(new Point(202, 4));
      tinaBrightnessSlider.setSize(new Dimension(220, 19));
      tinaBrightnessSlider.setPreferredSize(new Dimension(220, 19));
      tinaBrightnessSlider.setValue(0);
      tinaBrightnessSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaBrightnessSlider.setMaximum(500);
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
  private JTextField getTinaPixelsPerUnitREd() {
    if (tinaPixelsPerUnitREd == null) {
      tinaPixelsPerUnitREd = new JTextField();
      tinaPixelsPerUnitREd.setPreferredSize(new Dimension(100, 22));
      tinaPixelsPerUnitREd.setText("");
      tinaPixelsPerUnitREd.setLocation(new Point(584, 76));
      tinaPixelsPerUnitREd.setSize(new Dimension(100, 22));
      tinaPixelsPerUnitREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPixelsPerUnitREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.pixelsPerUnitREd_changed();
        }
      });
      tinaPixelsPerUnitREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.pixelsPerUnitREd_changed();
        }
      });
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
   * This method initializes tinaRenderQualityREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaRenderQualityREd() {
    if (tinaRenderQualityREd == null) {
      tinaRenderQualityREd = new JTextField();
      tinaRenderQualityREd.setPreferredSize(new Dimension(56, 22));
      tinaRenderQualityREd.setText("100");
      tinaRenderQualityREd.setLocation(new Point(727, 32));
      tinaRenderQualityREd.setSize(new Dimension(56, 22));
      tinaRenderQualityREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaRenderQualityREd;
  }

  /**
   * This method initializes tinaBGColorRedREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaBGColorRedREd() {
    if (tinaBGColorRedREd == null) {
      tinaBGColorRedREd = new JTextField();
      tinaBGColorRedREd.setPreferredSize(new Dimension(56, 22));
      tinaBGColorRedREd.setText("");
      tinaBGColorRedREd.setLocation(new Point(584, 76));
      tinaBGColorRedREd.setSize(new Dimension(56, 22));
      tinaBGColorRedREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaBGColorRedREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.bgColorRedREd_changed();
        }
      });
      tinaBGColorRedREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.bgColorRedREd_changed();
        }
      });
    }
    return tinaBGColorRedREd;
  }

  /**
   * This method initializes tinaBGColorGreenREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaBGColorGreenREd() {
    if (tinaBGColorGreenREd == null) {
      tinaBGColorGreenREd = new JTextField();
      tinaBGColorGreenREd.setPreferredSize(new Dimension(56, 22));
      tinaBGColorGreenREd.setText("");
      tinaBGColorGreenREd.setLocation(new Point(712, 76));
      tinaBGColorGreenREd.setSize(new Dimension(56, 22));
      tinaBGColorGreenREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaBGColorGreenREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.bgColorGreenREd_changed();
        }
      });
      tinaBGColorGreenREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.bgColorGreenREd_changed();
        }
      });
    }
    return tinaBGColorGreenREd;
  }

  /**
   * This method initializes tinaBGColorBlueREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaBGColorBlueREd() {
    if (tinaBGColorBlueREd == null) {
      tinaBGColorBlueREd = new JTextField();
      tinaBGColorBlueREd.setPreferredSize(new Dimension(56, 22));
      tinaBGColorBlueREd.setText("");
      tinaBGColorBlueREd.setLocation(new Point(840, 76));
      tinaBGColorBlueREd.setSize(new Dimension(56, 22));
      tinaBGColorBlueREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaBGColorBlueREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.bgBGColorBlueREd_changed();
        }
      });
      tinaBGColorBlueREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.bgBGColorBlueREd_changed();
        }
      });
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
  private JTextField getTinaContrastREd() {
    if (tinaContrastREd == null) {
      tinaContrastREd = new JTextField();
      tinaContrastREd.setPreferredSize(new Dimension(100, 22));
      tinaContrastREd.setText("");
      tinaContrastREd.setLocation(new Point(100, 28));
      tinaContrastREd.setSize(new Dimension(100, 22));
      tinaContrastREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaContrastREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.contrastREd_changed();
        }
      });
      tinaContrastREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.contrastREd_changed();
        }
      });
    }
    return tinaContrastREd;
  }

  /**
   * This method initializes tinaGammaREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaGammaREd() {
    if (tinaGammaREd == null) {
      tinaGammaREd = new JTextField();
      tinaGammaREd.setPreferredSize(new Dimension(100, 22));
      tinaGammaREd.setText("");
      tinaGammaREd.setLocation(new Point(100, 52));
      tinaGammaREd.setSize(new Dimension(100, 22));
      tinaGammaREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaGammaREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.gammaREd_changed();
        }
      });
      tinaGammaREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.gammaREd_changed();
        }
      });
    }
    return tinaGammaREd;
  }

  /**
   * This method initializes tinaVibrancyREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaVibrancyREd() {
    if (tinaVibrancyREd == null) {
      tinaVibrancyREd = new JTextField();
      tinaVibrancyREd.setPreferredSize(new Dimension(100, 22));
      tinaVibrancyREd.setText("");
      tinaVibrancyREd.setLocation(new Point(100, 76));
      tinaVibrancyREd.setSize(new Dimension(100, 22));
      tinaVibrancyREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaVibrancyREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.vibrancyREd_changed();
        }
      });
      tinaVibrancyREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.vibrancyREd_changed();
        }
      });
    }
    return tinaVibrancyREd;
  }

  /**
   * This method initializes tinaFilterRadiusREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaFilterRadiusREd() {
    if (tinaFilterRadiusREd == null) {
      tinaFilterRadiusREd = new JTextField();
      tinaFilterRadiusREd.setPreferredSize(new Dimension(100, 22));
      tinaFilterRadiusREd.setText("");
      tinaFilterRadiusREd.setLocation(new Point(584, 28));
      tinaFilterRadiusREd.setSize(new Dimension(100, 22));
      tinaFilterRadiusREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaFilterRadiusREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.filterRadiusREd_changed();
        }
      });
      tinaFilterRadiusREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.filterRadiusREd_changed();
        }
      });
    }
    return tinaFilterRadiusREd;
  }

  /**
   * This method initializes tinaOversampleREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaOversampleREd() {
    if (tinaOversampleREd == null) {
      tinaOversampleREd = new JTextField();
      tinaOversampleREd.setPreferredSize(new Dimension(100, 22));
      tinaOversampleREd.setText("");
      tinaOversampleREd.setLocation(new Point(584, 4));
      tinaOversampleREd.setSize(new Dimension(100, 22));
      tinaOversampleREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaOversampleREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.oversampleREd_changed();
        }
      });
      tinaOversampleREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.oversampleREd_changed();
        }
      });
    }
    return tinaOversampleREd;
  }

  /**
   * This method initializes tinaContrastSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaContrastSlider() {
    if (tinaContrastSlider == null) {
      tinaContrastSlider = new JSlider();
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
      tinaVibrancySlider.setMaximum(100);
      tinaVibrancySlider.setMinimum(0);
      tinaVibrancySlider.setValue(0);
      tinaVibrancySlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaVibrancySlider.setSize(new Dimension(220, 19));
      tinaVibrancySlider.setLocation(new Point(202, 76));
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
      tinaFilterRadiusSlider.setMaximum(500);
      tinaFilterRadiusSlider.setMinimum(0);
      tinaFilterRadiusSlider.setValue(0);
      tinaFilterRadiusSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaFilterRadiusSlider.setSize(new Dimension(220, 19));
      tinaFilterRadiusSlider.setLocation(new Point(686, 28));
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
  private JSlider getTinaOversampleSlider() {
    if (tinaOversampleSlider == null) {
      tinaOversampleSlider = new JSlider();
      tinaOversampleSlider.setMaximum(10);
      tinaOversampleSlider.setMinimum(0);
      tinaOversampleSlider.setValue(0);
      tinaOversampleSlider.setMajorTickSpacing(1);
      tinaOversampleSlider.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaOversampleSlider.setSize(new Dimension(220, 19));
      tinaOversampleSlider.setLocation(new Point(686, 4));
      tinaOversampleSlider.setPreferredSize(new Dimension(220, 19));
      tinaOversampleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.oversampleSlider_stateChanged(e);
        }
      });
    }
    return tinaOversampleSlider;
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
   * This method initializes tinaNorthTabbedPane  
   *  
   * @return javax.swing.JTabbedPane  
   */
  private JTabbedPane getTinaNorthTabbedPane() {
    if (tinaNorthTabbedPane == null) {
      tinaNorthTabbedPane = new JTabbedPane();
      tinaNorthTabbedPane.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaNorthTabbedPane.addTab("Main", null, getTinaNorthMainPanel(), null);
      tinaNorthTabbedPane.addTab("Morph", null, getTinaMorphPanel(), null);
      tinaNorthTabbedPane.addTab("Animate", null, getTinaAnimatePanel(), null);
    }
    return tinaNorthTabbedPane;
  }

  /**
   * This method initializes tinaNorthMainPanel 
   *  
   * @return javax.swing.JPanel 
   */
  private JPanel getTinaNorthMainPanel() {
    if (tinaNorthMainPanel == null) {
      randomStyleLbl = new JLabel();
      randomStyleLbl.setPreferredSize(new Dimension(94, 22));
      randomStyleLbl.setText("Random style");
      randomStyleLbl.setSize(new Dimension(94, 22));
      randomStyleLbl.setLocation(new Point(4, 4));
      randomStyleLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      zStyleLbl = new JLabel();
      zStyleLbl.setPreferredSize(new Dimension(94, 22));
      zStyleLbl.setText("Z style");
      zStyleLbl.setBounds(new Rectangle(110, 5, 94, 22));
      zStyleLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaRenderSizeLbl = new JLabel();
      tinaRenderSizeLbl.setText("Export size");
      tinaRenderSizeLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaRenderSizeLbl.setLocation(new Point(790, 4));
      tinaRenderSizeLbl.setSize(new Dimension(66, 22));
      tinaRenderSizeLbl.setPreferredSize(new Dimension(66, 22));
      tinaNorthMainPanel = new JPanel();
      tinaNorthMainPanel.setLayout(null);
      tinaCameraRenderQualityLbl = new JLabel();
      tinaCameraRenderQualityLbl.setText("Render Quality");
      tinaCameraRenderQualityLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraRenderQualityLbl.setLocation(new Point(631, 32));
      tinaCameraRenderQualityLbl.setSize(new Dimension(94, 22));
      tinaCameraRenderQualityLbl.setPreferredSize(new Dimension(94, 22));
      tinaCameraPreviewQualityLbl = new JLabel();
      tinaCameraPreviewQualityLbl.setText("Preview Quality");
      tinaCameraPreviewQualityLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaCameraPreviewQualityLbl.setLocation(new Point(631, 4));
      tinaCameraPreviewQualityLbl.setSize(new Dimension(94, 22));
      tinaCameraPreviewQualityLbl.setPreferredSize(new Dimension(94, 22));
      tinaNorthMainPanel.add(getTinaLoadFlameButton(), null);
      tinaNorthMainPanel.add(getTinaSaveFlameButton(), null);
      tinaNorthMainPanel.add(getTinaExportImageButton(), null);
      tinaNorthMainPanel.add(tinaCameraPreviewQualityLbl, null);
      tinaNorthMainPanel.add(getTinaPreviewQualityREd(), null);
      tinaNorthMainPanel.add(tinaCameraRenderQualityLbl, null);
      tinaNorthMainPanel.add(getTinaRenderQualityREd(), null);
      tinaNorthMainPanel.add(tinaRenderSizeLbl, null);
      tinaNorthMainPanel.add(getTinaRenderWidthREd(), null);
      tinaNorthMainPanel.add(getTinaRenderHeightREd(), null);
      tinaNorthMainPanel.add(getRandomBatchButton(), null);
      tinaNorthMainPanel.add(getNewFlameButton(), null);
      tinaNorthMainPanel.add(getRandomStyleCmb(), null);
      tinaNorthMainPanel.add(randomStyleLbl, null);
    }
    return tinaNorthMainPanel;
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
      tinaWestTabbedPane.addTab("Palette", null, getTinaPalettePanel(), null);
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
          tinaController.transformationTableClicked();
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
      affineC21Lbl.setText("c21");
      affineC21Lbl.setLocation(new Point(188, 28));
      affineC21Lbl.setSize(new Dimension(24, 22));
      affineC21Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC21Lbl.setPreferredSize(new Dimension(24, 22));
      affineC20Lbl = new JLabel();
      affineC20Lbl.setText("c20");
      affineC20Lbl.setLocation(new Point(188, 4));
      affineC20Lbl.setSize(new Dimension(24, 22));
      affineC20Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC20Lbl.setPreferredSize(new Dimension(24, 22));
      affineC11Lbl = new JLabel();
      affineC11Lbl.setText("c11");
      affineC11Lbl.setLocation(new Point(96, 28));
      affineC11Lbl.setSize(new Dimension(24, 22));
      affineC11Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC11Lbl.setPreferredSize(new Dimension(24, 22));
      affineC10Lbl = new JLabel();
      affineC10Lbl.setText("c10");
      affineC10Lbl.setLocation(new Point(96, 4));
      affineC10Lbl.setSize(new Dimension(24, 22));
      affineC10Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC10Lbl.setPreferredSize(new Dimension(24, 22));
      affineC01Lbl = new JLabel();
      affineC01Lbl.setText("c01");
      affineC01Lbl.setFont(new Font("Dialog", Font.BOLD, 10));
      affineC01Lbl.setLocation(new Point(4, 28));
      affineC01Lbl.setSize(new Dimension(24, 22));
      affineC01Lbl.setPreferredSize(new Dimension(24, 22));
      affineC00Lbl = new JLabel();
      affineC00Lbl.setText("c00");
      affineC00Lbl.setLocation(new Point(4, 4));
      affineC00Lbl.setSize(new Dimension(24, 22));
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
      tinaVariationPanel.setLayout(null);
      tinaVariationPanel.add(getNonlinearVar1Panel(), null);
      tinaVariationPanel.add(getNonlinearVar2Panel(), null);
      tinaVariationPanel.add(getNonlinearVar3Panel(), null);
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
      tinaRandomPaletteButton.setText("Random Palette");
      tinaRandomPaletteButton.setPreferredSize(new Dimension(180, 24));
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
   * This method initializes tinaGrabPaletteFromImageButton 
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaGrabPaletteFromImageButton() {
    if (tinaGrabPaletteFromImageButton == null) {
      tinaGrabPaletteFromImageButton = new JButton();
      tinaGrabPaletteFromImageButton.setText("Grab from image");
      tinaGrabPaletteFromImageButton.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaGrabPaletteFromImageButton.setPreferredSize(new Dimension(180, 24));
      tinaGrabPaletteFromImageButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.grabPaletteFromImageButton_actionPerformed(e);
        }
      });
    }
    return tinaGrabPaletteFromImageButton;
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
      tinaPaletteCreatePanel.add(getCreateGapPanel(), null);
      tinaPaletteCreatePanel.add(getTinaGrabPaletteFromImageButton(), null);
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
      tinaPaletteBrightnessLbl.setSize(new Dimension(64, 22));
      tinaPaletteBrightnessLbl.setLocation(new Point(4, 212));
      tinaPaletteBrightnessLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBrightnessLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteGammaLbl = new JLabel();
      tinaPaletteGammaLbl.setText("Gamma");
      tinaPaletteGammaLbl.setSize(new Dimension(64, 22));
      tinaPaletteGammaLbl.setLocation(new Point(4, 186));
      tinaPaletteGammaLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteGammaLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteContrastLbl = new JLabel();
      tinaPaletteContrastLbl.setText("Contrast");
      tinaPaletteContrastLbl.setSize(new Dimension(64, 22));
      tinaPaletteContrastLbl.setLocation(new Point(4, 160));
      tinaPaletteContrastLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteContrastLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteSaturationLbl = new JLabel();
      tinaPaletteSaturationLbl.setText("Saturation");
      tinaPaletteSaturationLbl.setSize(new Dimension(64, 22));
      tinaPaletteSaturationLbl.setLocation(new Point(4, 134));
      tinaPaletteSaturationLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteSaturationLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteHueLbl = new JLabel();
      tinaPaletteHueLbl.setText("Hue");
      tinaPaletteHueLbl.setSize(new Dimension(64, 22));
      tinaPaletteHueLbl.setLocation(new Point(4, 108));
      tinaPaletteHueLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteHueLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteBlueLbl = new JLabel();
      tinaPaletteBlueLbl.setText("Blue");
      tinaPaletteBlueLbl.setSize(new Dimension(64, 22));
      tinaPaletteBlueLbl.setLocation(new Point(4, 82));
      tinaPaletteBlueLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteBlueLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteGreenLbl = new JLabel();
      tinaPaletteGreenLbl.setText("Green");
      tinaPaletteGreenLbl.setSize(new Dimension(64, 22));
      tinaPaletteGreenLbl.setLocation(new Point(4, 56));
      tinaPaletteGreenLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteGreenLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteRedLbl = new JLabel();
      tinaPaletteRedLbl.setText("Red");
      tinaPaletteRedLbl.setSize(new Dimension(64, 22));
      tinaPaletteRedLbl.setLocation(new Point(4, 30));
      tinaPaletteRedLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteRedLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteShiftLbl = new JLabel();
      tinaPaletteShiftLbl.setText("Shift");
      tinaPaletteShiftLbl.setSize(new Dimension(64, 22));
      tinaPaletteShiftLbl.setLocation(new Point(4, 4));
      tinaPaletteShiftLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaPaletteShiftLbl.setPreferredSize(new Dimension(64, 22));
      tinaPaletteBalancingPanel = new JPanel();
      tinaPaletteBalancingPanel.setLayout(null);
      tinaPaletteBalancingPanel.add(tinaPaletteShiftLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteRedLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteGreenLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteBlueLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteHueLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteSaturationLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteContrastLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteGammaLbl, null);
      tinaPaletteBalancingPanel.add(tinaPaletteBrightnessLbl, null);
      tinaPaletteBalancingPanel.add(getTinaPaletteShiftREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteRedREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteGreenREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteBlueREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteHueREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteSaturationREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteContrastREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteGammaREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteBrightnessREd(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteShiftSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteRedSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteGreenSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteBlueSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteHueSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteSaturationSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteContrastSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteGammaSlider(), null);
      tinaPaletteBalancingPanel.add(getTinaPaletteBrightnessSlider(), null);
    }
    return tinaPaletteBalancingPanel;
  }

  /**
   * This method initializes tinaPaletteShiftREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteShiftREd() {
    if (tinaPaletteShiftREd == null) {
      tinaPaletteShiftREd = new JTextField();
      tinaPaletteShiftREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteShiftREd.setText("0");
      tinaPaletteShiftREd.setLocation(new Point(68, 4));
      tinaPaletteShiftREd.setSize(new Dimension(36, 22));
      tinaPaletteShiftREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteShiftREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteShiftREd_changed();
        }
      });
      tinaPaletteShiftREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteShiftREd_changed();
        }
      });
    }
    return tinaPaletteShiftREd;
  }

  /**
   * This method initializes tinaPaletteRedREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteRedREd() {
    if (tinaPaletteRedREd == null) {
      tinaPaletteRedREd = new JTextField();
      tinaPaletteRedREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteRedREd.setText("0");
      tinaPaletteRedREd.setSize(new Dimension(36, 22));
      tinaPaletteRedREd.setLocation(new Point(68, 30));
      tinaPaletteRedREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteRedREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteRedREd_changed();
        }
      });
      tinaPaletteRedREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteRedREd_changed();
        }
      });
    }
    return tinaPaletteRedREd;
  }

  /**
   * This method initializes tinaPaletteGreenREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteGreenREd() {
    if (tinaPaletteGreenREd == null) {
      tinaPaletteGreenREd = new JTextField();
      tinaPaletteGreenREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteGreenREd.setText("0");
      tinaPaletteGreenREd.setSize(new Dimension(36, 22));
      tinaPaletteGreenREd.setLocation(new Point(68, 56));
      tinaPaletteGreenREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteGreenREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteGreenREd_changed();
        }
      });
      tinaPaletteGreenREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteGreenREd_changed();
        }
      });
    }
    return tinaPaletteGreenREd;
  }

  /**
   * This method initializes tinaPaletteBlueREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteBlueREd() {
    if (tinaPaletteBlueREd == null) {
      tinaPaletteBlueREd = new JTextField();
      tinaPaletteBlueREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteBlueREd.setText("0");
      tinaPaletteBlueREd.setSize(new Dimension(36, 22));
      tinaPaletteBlueREd.setLocation(new Point(68, 82));
      tinaPaletteBlueREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteBlueREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteBlueREd_changed();
        }
      });
      tinaPaletteBlueREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteBlueREd_changed();
        }
      });
    }
    return tinaPaletteBlueREd;
  }

  /**
   * This method initializes tinaPaletteHueREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteHueREd() {
    if (tinaPaletteHueREd == null) {
      tinaPaletteHueREd = new JTextField();
      tinaPaletteHueREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteHueREd.setText("0");
      tinaPaletteHueREd.setSize(new Dimension(36, 22));
      tinaPaletteHueREd.setLocation(new Point(68, 108));
      tinaPaletteHueREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteHueREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteHueREd_changed();
        }
      });
      tinaPaletteHueREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteHueREd_changed();
        }
      });
    }
    return tinaPaletteHueREd;
  }

  /**
   * This method initializes tinaPaletteSaturationREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteSaturationREd() {
    if (tinaPaletteSaturationREd == null) {
      tinaPaletteSaturationREd = new JTextField();
      tinaPaletteSaturationREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteSaturationREd.setText("0");
      tinaPaletteSaturationREd.setSize(new Dimension(36, 22));
      tinaPaletteSaturationREd.setLocation(new Point(68, 134));
      tinaPaletteSaturationREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteSaturationREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteSaturationREd_changed();
        }
      });
      tinaPaletteSaturationREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteSaturationREd_changed();
        }
      });
    }
    return tinaPaletteSaturationREd;
  }

  /**
   * This method initializes tinaPaletteContrastREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteContrastREd() {
    if (tinaPaletteContrastREd == null) {
      tinaPaletteContrastREd = new JTextField();
      tinaPaletteContrastREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteContrastREd.setText("0");
      tinaPaletteContrastREd.setSize(new Dimension(36, 22));
      tinaPaletteContrastREd.setLocation(new Point(68, 160));
      tinaPaletteContrastREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteContrastREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteContrastREd_changed();
        }
      });
      tinaPaletteContrastREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteContrastREd_changed();
        }
      });
    }
    return tinaPaletteContrastREd;
  }

  /**
   * This method initializes tinaPaletteGammaREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteGammaREd() {
    if (tinaPaletteGammaREd == null) {
      tinaPaletteGammaREd = new JTextField();
      tinaPaletteGammaREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteGammaREd.setText("0");
      tinaPaletteGammaREd.setSize(new Dimension(36, 22));
      tinaPaletteGammaREd.setLocation(new Point(68, 186));
      tinaPaletteGammaREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteGammaREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteGammaREd_changed();
        }
      });
      tinaPaletteGammaREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteGammaREd_changed();
        }
      });
    }
    return tinaPaletteGammaREd;
  }

  /**
   * This method initializes tinaPaletteBrightnessREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaPaletteBrightnessREd() {
    if (tinaPaletteBrightnessREd == null) {
      tinaPaletteBrightnessREd = new JTextField();
      tinaPaletteBrightnessREd.setPreferredSize(new Dimension(36, 22));
      tinaPaletteBrightnessREd.setText("0");
      tinaPaletteBrightnessREd.setSize(new Dimension(36, 22));
      tinaPaletteBrightnessREd.setLocation(new Point(68, 212));
      tinaPaletteBrightnessREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      tinaPaletteBrightnessREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.paletteBrightnessREd_changed();
        }
      });
      tinaPaletteBrightnessREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.paletteBrightnessREd_changed();
        }
      });
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
      tinaPaletteShiftSlider.setMaximum(255);
      tinaPaletteShiftSlider.setMinimum(-255);
      tinaPaletteShiftSlider.setValue(0);
      tinaPaletteShiftSlider.setLocation(new Point(104, 4));
      tinaPaletteShiftSlider.setSize(new Dimension(86, 22));
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
      tinaPaletteRedSlider.setMaximum(255);
      tinaPaletteRedSlider.setMinimum(-255);
      tinaPaletteRedSlider.setValue(0);
      tinaPaletteRedSlider.setSize(new Dimension(86, 22));
      tinaPaletteRedSlider.setLocation(new Point(104, 30));
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
      tinaPaletteGreenSlider.setMaximum(255);
      tinaPaletteGreenSlider.setMinimum(-255);
      tinaPaletteGreenSlider.setValue(0);
      tinaPaletteGreenSlider.setSize(new Dimension(86, 22));
      tinaPaletteGreenSlider.setLocation(new Point(104, 56));
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
      tinaPaletteBlueSlider.setMaximum(255);
      tinaPaletteBlueSlider.setMinimum(-255);
      tinaPaletteBlueSlider.setValue(0);
      tinaPaletteBlueSlider.setSize(new Dimension(86, 22));
      tinaPaletteBlueSlider.setLocation(new Point(104, 82));
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
      tinaPaletteHueSlider.setMaximum(255);
      tinaPaletteHueSlider.setMinimum(-255);
      tinaPaletteHueSlider.setValue(0);
      tinaPaletteHueSlider.setSize(new Dimension(86, 22));
      tinaPaletteHueSlider.setLocation(new Point(104, 108));
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
      tinaPaletteSaturationSlider.setMaximum(255);
      tinaPaletteSaturationSlider.setMinimum(-255);
      tinaPaletteSaturationSlider.setValue(0);
      tinaPaletteSaturationSlider.setSize(new Dimension(86, 22));
      tinaPaletteSaturationSlider.setLocation(new Point(104, 134));
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
      tinaPaletteContrastSlider.setMaximum(255);
      tinaPaletteContrastSlider.setMinimum(-255);
      tinaPaletteContrastSlider.setValue(0);
      tinaPaletteContrastSlider.setSize(new Dimension(86, 22));
      tinaPaletteContrastSlider.setLocation(new Point(104, 160));
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
      tinaPaletteGammaSlider.setMaximum(255);
      tinaPaletteGammaSlider.setMinimum(-255);
      tinaPaletteGammaSlider.setValue(0);
      tinaPaletteGammaSlider.setSize(new Dimension(86, 22));
      tinaPaletteGammaSlider.setLocation(new Point(104, 186));
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
      tinaPaletteBrightnessSlider.setMaximum(255);
      tinaPaletteBrightnessSlider.setMinimum(-255);
      tinaPaletteBrightnessSlider.setValue(0);
      tinaPaletteBrightnessSlider.setSize(new Dimension(86, 22));
      tinaPaletteBrightnessSlider.setLocation(new Point(104, 212));
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

  /**
   * This method initializes tinaRenderWidthREd 
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaRenderWidthREd() {
    if (tinaRenderWidthREd == null) {
      tinaRenderWidthREd = new JTextField();
      tinaRenderWidthREd.setPreferredSize(new Dimension(56, 22));
      tinaRenderWidthREd.setText("800");
      tinaRenderWidthREd.setLocation(new Point(858, 4));
      tinaRenderWidthREd.setSize(new Dimension(56, 22));
      tinaRenderWidthREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaRenderWidthREd;
  }

  /**
   * This method initializes tinaRenderHeightREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaRenderHeightREd() {
    if (tinaRenderHeightREd == null) {
      tinaRenderHeightREd = new JTextField();
      tinaRenderHeightREd.setPreferredSize(new Dimension(56, 22));
      tinaRenderHeightREd.setText("600");
      tinaRenderHeightREd.setLocation(new Point(914, 4));
      tinaRenderHeightREd.setSize(new Dimension(56, 22));
      tinaRenderHeightREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return tinaRenderHeightREd;
  }

  public TinaController createController(StandardErrorHandler pErrorHandler, Prefs pPrefs) {
    nonlinearControlsRows = new NonlinearControlsRow[3];
    nonlinearControlsRows[0] = new NonlinearControlsRow(getNonlinearVar1Cmb(), getNonlinearParams1Cmb(), getNonlinearVar1REd(),
        getNonlinearParams1REd(), getNonlinearVar1LeftButton(), getNonlinarVar1RightButton(), getNonlinearParams1LeftButton(), getNonlinarParams1RightButton());
    nonlinearControlsRows[1] = new NonlinearControlsRow(getNonlinearVar2Cmb(), getNonlinearParams2Cmb(), getNonlinearVar2REd(),
        getNonlinearParams2REd(), getNonlinearVar2LeftButton(), getNonlinarVar2RightButton(), getNonlinearParams2LeftButton(), getNonlinarParams2RightButton());
    nonlinearControlsRows[2] = new NonlinearControlsRow(getNonlinearVar3Cmb(), getNonlinearParams3Cmb(), getNonlinearVar3REd(),
        getNonlinearParams3REd(), getNonlinearVar3LeftButton(), getNonlinarVar3RightButton(), getNonlinearParams3LeftButton(), getNonlinarParams3RightButton());
    tinaController = new TinaController(pErrorHandler, pPrefs, getCenterCenterPanel(), getTinaCameraRollREd(), getTinaCameraRollSlider(), getTinaCameraPitchREd(),
        getTinaCameraPitchSlider(), getTinaCameraYawREd(), getTinaCameraYawSlider(), getTinaCameraPerspectiveREd(), getTinaCameraPerspectiveSlider(),
        getTinaPreviewQualityREd(), getTinaRenderQualityREd(), getTinaCameraCentreXREd(), getTinaCameraCentreXSlider(), getTinaCameraCentreYREd(),
        getTinaCameraCentreYSlider(), getTinaCameraZoomREd(), getTinaCameraZoomSlider(), getTinaPixelsPerUnitREd(), getTinaPixelsPerUnitSlider(),
        getTinaBrightnessREd(), getTinaBrightnessSlider(), getTinaContrastREd(), getTinaContrastSlider(), getTinaGammaREd(), getTinaGammaSlider(),
        getTinaVibrancyREd(), getTinaVibrancySlider(), getTinaFilterRadiusREd(), getTinaFilterRadiusSlider(), getTinaOversampleREd(),
        getTinaOversampleSlider(), getTinaBGColorRedREd(), getTinaBGColorRedSlider(), getTinaBGColorGreenREd(), getTinaBGColorGreenSlider(), getTinaBGColorBlueREd(),
        getTinaBGColorBlueSlider(), getTinaPaletteRandomPointsREd(), getTinaPaletteImgPanel(), getTinaPaletteShiftREd(), getTinaPaletteShiftSlider(),
        getTinaPaletteRedREd(), getTinaPaletteRedSlider(), getTinaPaletteGreenREd(), getTinaPaletteGreenSlider(), getTinaPaletteBlueREd(),
        getTinaPaletteBlueSlider(), getTinaPaletteHueREd(), getTinaPaletteHueSlider(), getTinaPaletteSaturationREd(), getTinaPaletteSaturationSlider(),
        getTinaPaletteContrastREd(), getTinaPaletteContrastSlider(), getTinaPaletteGammaREd(), getTinaPaletteGammaSlider(), getTinaPaletteBrightnessREd(),
        getTinaPaletteBrightnessSlider(), getTinaRenderWidthREd(), getTinaRenderHeightREd(), getTinaTransformationsTable(), getAffineC00REd(),
        getAffineC01REd(), getAffineC10REd(), getAffineC11REd(), getAffineC20REd(), getAffineC21REd(), getAffineRotateAmountREd(), getAffineScaleAmountREd(),
        getAffineMoveAmountREd(), getAffineRotateLeftButton(), getAffineRotateRightButton(), getAffineEnlargeButton(), getAffineShrinkButton(),
        getAffineMoveUpButton(), getAffineMoveLeftButton(), getAffineMoveRightButton(), getAffineMoveDownButton(), getTinaAddTransformationButton(),
        getTinaDuplicateTransformationButton(), getTinaDeleteTransformationButton(), getTinaAddFinalTransformationButton(), getRandomBatchPanel(),
        nonlinearControlsRows, getXFormColorREd(), getXFormColorSlider(), getXFormSymmetryREd(), getXFormSymmetrySlider(), getXFormOpacityREd(),
        getXFormOpacitySlider(), getXFormDrawModeCmb(), getRelWeightsTable(), getRelWeightsLeftButton(), getRelWeightsRightButton(),
        getTransformationWeightLeftButton(), getTransformationWeightRightButton(), getZStyleCmb(), getSetMorphFlame1Button(),
        getSetMorphFlame2Button(), getMorphFrameREd(), getMorphFramesREd(), getMorphCheckBox(), getMorphFrameSlider(), getImportMorphedFlameButton(),
        getAnimateOutputREd(), getAnimateFramesREd(), getAnimateScriptCmb(), getAnimationGenerateButton(), getAnimateXFormScriptCmb(), getMouseTransformMoveButton(),
        getMouseTransformRotateButton(), getMouseTransformScaleButton(), getAffineEditPostTransformButton());
    tinaController.refreshing = tinaController.cmbRefreshing = tinaController.gridRefreshing = true;
    try {
      for (NonlinearControlsRow row : nonlinearControlsRows) {
        row.initControls();
      }
      getXFormDrawModeCmb().removeAllItems();
      getXFormDrawModeCmb().addItem(DrawMode.NORMAL);
      getXFormDrawModeCmb().addItem(DrawMode.OPAQUE);
      getXFormDrawModeCmb().addItem(DrawMode.HIDDEN);

      getZStyleCmb().removeAllItems();
      getZStyleCmb().addItem(AffineZStyle.FLAT);
      getZStyleCmb().addItem(AffineZStyle.Z1);
      getZStyleCmb().addItem(AffineZStyle.Z2);
      getZStyleCmb().addItem(AffineZStyle.Z3);
      getZStyleCmb().addItem(AffineZStyle.Z4);
      getZStyleCmb().addItem(AffineZStyle.Z5);
      getZStyleCmb().addItem(AffineZStyle.Z6);

      getMorphFrameSlider().setMaximum(Integer.parseInt(getMorphFramesREd().getText()));
      getMorphFrameSlider().setValue(Integer.parseInt(getMorphFrameREd().getText()));

      getAnimateScriptCmb().removeAllItems();
      getAnimateScriptCmb().addItem(AnimationService.GlobalScript.NONE);
      getAnimateScriptCmb().addItem(AnimationService.GlobalScript.ROTATE_PITCH);
      getAnimateScriptCmb().addItem(AnimationService.GlobalScript.ROTATE_PITCH_YAW);
      getAnimateScriptCmb().setSelectedItem(AnimationService.GlobalScript.ROTATE_PITCH_YAW);

      getAnimateXFormScriptCmb().removeAllItems();
      getAnimateXFormScriptCmb().addItem(AnimationService.XFormScript.NONE);
      getAnimateXFormScriptCmb().addItem(AnimationService.XFormScript.ROTATE_FULL);
      getAnimateXFormScriptCmb().addItem(AnimationService.XFormScript.ROTATE_SLIGHTLY);
      getAnimateXFormScriptCmb().setSelectedItem(AnimationService.XFormScript.ROTATE_SLIGHTLY);
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
  private JTextField getAffineC00REd() {
    if (affineC00REd == null) {
      affineC00REd = new JTextField();
      affineC00REd.setPreferredSize(new Dimension(56, 22));
      affineC00REd.setText("");
      affineC00REd.setLocation(new Point(30, 4));
      affineC00REd.setSize(new Dimension(56, 22));
      affineC00REd.setEditable(false);
      affineC00REd.setHorizontalAlignment(JTextField.RIGHT);
      affineC00REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC00REd;
  }

  /**
   * This method initializes affineC01REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getAffineC01REd() {
    if (affineC01REd == null) {
      affineC01REd = new JTextField();
      affineC01REd.setPreferredSize(new Dimension(56, 22));
      affineC01REd.setText("");
      affineC01REd.setLocation(new Point(30, 28));
      affineC01REd.setSize(new Dimension(56, 22));
      affineC01REd.setEditable(false);
      affineC01REd.setHorizontalAlignment(JTextField.RIGHT);
      affineC01REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC01REd;
  }

  /**
   * This method initializes affineC10REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getAffineC10REd() {
    if (affineC10REd == null) {
      affineC10REd = new JTextField();
      affineC10REd.setPreferredSize(new Dimension(56, 22));
      affineC10REd.setText("");
      affineC10REd.setLocation(new Point(122, 4));
      affineC10REd.setSize(new Dimension(56, 22));
      affineC10REd.setEditable(false);
      affineC10REd.setHorizontalAlignment(JTextField.RIGHT);
      affineC10REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC10REd;
  }

  /**
   * This method initializes affineC11REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getAffineC11REd() {
    if (affineC11REd == null) {
      affineC11REd = new JTextField();
      affineC11REd.setPreferredSize(new Dimension(56, 22));
      affineC11REd.setText("");
      affineC11REd.setLocation(new Point(122, 28));
      affineC11REd.setSize(new Dimension(56, 22));
      affineC11REd.setEditable(false);
      affineC11REd.setHorizontalAlignment(JTextField.RIGHT);
      affineC11REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC11REd;
  }

  /**
   * This method initializes affineC20REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getAffineC20REd() {
    if (affineC20REd == null) {
      affineC20REd = new JTextField();
      affineC20REd.setPreferredSize(new Dimension(56, 22));
      affineC20REd.setText("");
      affineC20REd.setLocation(new Point(214, 4));
      affineC20REd.setSize(new Dimension(56, 22));
      affineC20REd.setEditable(false);
      affineC20REd.setHorizontalAlignment(JTextField.RIGHT);
      affineC20REd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return affineC20REd;
  }

  /**
   * This method initializes affineC21REd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getAffineC21REd() {
    if (affineC21REd == null) {
      affineC21REd = new JTextField();
      affineC21REd.setPreferredSize(new Dimension(56, 22));
      affineC21REd.setText("");
      affineC21REd.setLocation(new Point(214, 28));
      affineC21REd.setSize(new Dimension(56, 22));
      affineC21REd.setEditable(false);
      affineC21REd.setHorizontalAlignment(JTextField.RIGHT);
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
      affineRotateLeftButton.setLocation(new Point(4, 56));
      affineRotateLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/turnLeft.gif")));
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
      affineRotateRightButton.setLocation(new Point(4, 105));
      affineRotateRightButton.setSize(new Dimension(55, 24));
      affineRotateRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/turnRight.gif")));
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
      affineEnlargeButton.setLocation(new Point(66, 56));
      affineEnlargeButton.setSize(new Dimension(55, 24));
      affineEnlargeButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/enlarge.gif")));
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
      affineShrinkButton.setLocation(new Point(66, 105));
      affineShrinkButton.setSize(new Dimension(55, 24));
      affineShrinkButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/shrink.gif")));
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
      affineRotateAmountREd.setPreferredSize(new Dimension(56, 22));
      affineRotateAmountREd.setText("5");
      affineRotateAmountREd.setHorizontalAlignment(JTextField.RIGHT);
      affineRotateAmountREd.setSize(new Dimension(56, 22));
      affineRotateAmountREd.setLocation(new Point(4, 83));
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
      trnsformationsEastPanel.add(getTransformationWeightPanel(), null);
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
      transformationsSplitPane.setDividerLocation(166);
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
      affineScaleAmountREd.setPreferredSize(new Dimension(56, 22));
      affineScaleAmountREd.setText("0.05");
      affineScaleAmountREd.setHorizontalAlignment(JTextField.RIGHT);
      affineScaleAmountREd.setSize(new Dimension(56, 22));
      affineScaleAmountREd.setLocation(new Point(66, 83));
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
      affineMoveUpButton.setLocation(new Point(182, 56));
      affineMoveUpButton.setSize(new Dimension(55, 24));
      affineMoveUpButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveUp.gif")));
      affineMoveUpButton.setText("");
      affineMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_moveUp();
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
      affineMoveDownButton.setLocation(new Point(182, 105));
      affineMoveDownButton.setSize(new Dimension(55, 24));
      affineMoveDownButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveDown.gif")));
      affineMoveDownButton.setText("");
      affineMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_moveDown();
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
      affineMoveLeftButton.setLocation(new Point(126, 83));
      affineMoveLeftButton.setSize(new Dimension(55, 24));
      affineMoveLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      affineMoveLeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineMoveLeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_moveLeft();
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
      affineMoveRightButton.setLocation(new Point(238, 83));
      affineMoveRightButton.setSize(new Dimension(55, 24));
      affineMoveRightButton.setPreferredSize(new Dimension(55, 24));
      affineMoveRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      affineMoveRightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineMoveRightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xForm_moveRight();
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
      affineMoveAmountREd.setPreferredSize(new Dimension(56, 22));
      affineMoveAmountREd.setText("0.1");
      affineMoveAmountREd.setHorizontalAlignment(JTextField.RIGHT);
      affineMoveAmountREd.setSize(new Dimension(56, 22));
      affineMoveAmountREd.setLocation(new Point(182, 83));
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
   * This method initializes createGapPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCreateGapPanel() {
    if (createGapPanel == null) {
      createGapPanel = new JPanel();
      createGapPanel.setLayout(new GridBagLayout());
      createGapPanel.setPreferredSize(new Dimension(180, 26));
    }
    return createGapPanel;
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
      randomBatchButton.setSize(new Dimension(221, 26));
      randomBatchButton.setLocation(new Point(4, 30));
      randomBatchButton.setPreferredSize(new Dimension(125, 52));
      randomBatchButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.createRandomBatch(-1, (RandomFlameGeneratorStyle) randomStyleCmb.getSelectedItem());
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
      nonlinearVar1Cmb.setSize(new Dimension(120, 22));
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
  private JTextField getNonlinearVar1REd() {
    if (nonlinearVar1REd == null) {
      nonlinearVar1REd = new JTextField();
      nonlinearVar1REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar1REd.setText("");
      nonlinearVar1REd.setSize(new Dimension(55, 22));
      nonlinearVar1REd.setLocation(new Point(188, 2));
      nonlinearVar1REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar1REd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarREdChanged(0);
        }
      });
      nonlinearVar1REd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.nonlinearVarREdChanged(0);
        }
      });
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
      nonlinearParams1Cmb.setSize(new Dimension(120, 22));
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
  private JTextField getNonlinearParams1REd() {
    if (nonlinearParams1REd == null) {
      nonlinearParams1REd = new JTextField();
      nonlinearParams1REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams1REd.setText("");
      nonlinearParams1REd.setSize(new Dimension(55, 22));
      nonlinearParams1REd.setLocation(new Point(188, 26));
      nonlinearParams1REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams1REd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsREdChanged(0);
        }
      });
      nonlinearParams1REd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.nonlinearParamsREdChanged(0);
        }
      });
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
      nonlinearVar1Panel.setSize(new Dimension(292, 52));
      nonlinearVar1Panel.setLocation(new Point(2, 2));
      nonlinearVar1Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar1Panel.add(nonlinearVar1Lbl, null);
      nonlinearVar1Panel.add(getNonlinearVar1Cmb(), null);
      nonlinearVar1Panel.add(getNonlinearVar1REd(), null);
      nonlinearVar1Panel.add(nonlinearParams1Lbl, null);
      nonlinearVar1Panel.add(getNonlinearParams1Cmb(), null);
      nonlinearVar1Panel.add(getNonlinearParams1REd(), null);
      nonlinearVar1Panel.add(getNonlinearVar1LeftButton(), null);
      nonlinearVar1Panel.add(getNonlinarVar1RightButton(), null);
      nonlinearVar1Panel.add(getNonlinearParams1LeftButton(), null);
      nonlinearVar1Panel.add(getNonlinarParams1RightButton(), null);
    }
    return nonlinearVar1Panel;
  }

  /**
   * This method initializes nonlinearVar1LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearVar1LeftButton() {
    if (nonlinearVar1LeftButton == null) {
      nonlinearVar1LeftButton = new JButton();
      nonlinearVar1LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearVar1LeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      nonlinearVar1LeftButton.setText("");
      nonlinearVar1LeftButton.setLocation(new Point(244, 2));
      nonlinearVar1LeftButton.setSize(new Dimension(22, 22));
      nonlinearVar1LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar1LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarLeftButtonClicked(0);
        }
      });
    }
    return nonlinearVar1LeftButton;
  }

  /**
   * This method initializes nonlinarVar1RightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinarVar1RightButton() {
    if (nonlinarVar1RightButton == null) {
      nonlinarVar1RightButton = new JButton();
      nonlinarVar1RightButton.setPreferredSize(new Dimension(22, 22));
      nonlinarVar1RightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      nonlinarVar1RightButton.setText("");
      nonlinarVar1RightButton.setLocation(new Point(268, 2));
      nonlinarVar1RightButton.setSize(new Dimension(22, 22));
      nonlinarVar1RightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinarVar1RightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarRightButtonClicked(0);
        }
      });
    }
    return nonlinarVar1RightButton;
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
      nonlinearParams1LeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      nonlinearParams1LeftButton.setText("");
      nonlinearParams1LeftButton.setSize(new Dimension(22, 22));
      nonlinearParams1LeftButton.setLocation(new Point(244, 26));
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
   * This method initializes nonlinarParams1RightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinarParams1RightButton() {
    if (nonlinarParams1RightButton == null) {
      nonlinarParams1RightButton = new JButton();
      nonlinarParams1RightButton.setPreferredSize(new Dimension(22, 22));
      nonlinarParams1RightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      nonlinarParams1RightButton.setText("");
      nonlinarParams1RightButton.setSize(new Dimension(22, 22));
      nonlinarParams1RightButton.setLocation(new Point(268, 26));
      nonlinarParams1RightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinarParams1RightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsRightButtonClicked(0);
        }
      });
    }
    return nonlinarParams1RightButton;
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
      nonlinearVar2Panel.setLocation(new Point(2, 57));
      nonlinearVar2Panel.setSize(new Dimension(292, 52));
      nonlinearVar2Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar2Panel.add(nonlinearVar2Lbl, null);
      nonlinearVar2Panel.add(getNonlinearVar2Cmb(), null);
      nonlinearVar2Panel.add(getNonlinearVar2REd(), null);
      nonlinearVar2Panel.add(nonlinearParams2Lbl, null);
      nonlinearVar2Panel.add(getNonlinearParams2Cmb(), null);
      nonlinearVar2Panel.add(getNonlinearParams2REd(), null);
      nonlinearVar2Panel.add(getNonlinearVar2LeftButton(), null);
      nonlinearVar2Panel.add(getNonlinarVar2RightButton(), null);
      nonlinearVar2Panel.add(getNonlinearParams2LeftButton(), null);
      nonlinearVar2Panel.add(getNonlinarParams2RightButton(), null);
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
      nonlinearVar2Cmb.setSize(new Dimension(120, 22));
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
  private JTextField getNonlinearVar2REd() {
    if (nonlinearVar2REd == null) {
      nonlinearVar2REd = new JTextField();
      nonlinearVar2REd.setLocation(new Point(188, 2));
      nonlinearVar2REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar2REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar2REd.setText("");
      nonlinearVar2REd.setSize(new Dimension(55, 22));
      nonlinearVar2REd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarREdChanged(1);
        }
      });
      nonlinearVar2REd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.nonlinearVarREdChanged(1);
        }
      });
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
      nonlinearParams2Cmb.setSize(new Dimension(120, 22));
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
  private JTextField getNonlinearParams2REd() {
    if (nonlinearParams2REd == null) {
      nonlinearParams2REd = new JTextField();
      nonlinearParams2REd.setLocation(new Point(188, 26));
      nonlinearParams2REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams2REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams2REd.setText("");
      nonlinearParams2REd.setSize(new Dimension(55, 22));
      nonlinearParams2REd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsREdChanged(1);
        }
      });
      nonlinearParams2REd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.nonlinearParamsREdChanged(1);
        }
      });
    }
    return nonlinearParams2REd;
  }

  /**
   * This method initializes nonlinearVar2LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearVar2LeftButton() {
    if (nonlinearVar2LeftButton == null) {
      nonlinearVar2LeftButton = new JButton();
      nonlinearVar2LeftButton.setLocation(new Point(244, 2));
      nonlinearVar2LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar2LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearVar2LeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      nonlinearVar2LeftButton.setText("");
      nonlinearVar2LeftButton.setSize(new Dimension(22, 22));
      nonlinearVar2LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarLeftButtonClicked(1);
        }
      });
    }
    return nonlinearVar2LeftButton;
  }

  /**
   * This method initializes nonlinarVar2RightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinarVar2RightButton() {
    if (nonlinarVar2RightButton == null) {
      nonlinarVar2RightButton = new JButton();
      nonlinarVar2RightButton.setLocation(new Point(268, 2));
      nonlinarVar2RightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinarVar2RightButton.setPreferredSize(new Dimension(22, 22));
      nonlinarVar2RightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      nonlinarVar2RightButton.setText("");
      nonlinarVar2RightButton.setSize(new Dimension(22, 22));
      nonlinarVar2RightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarRightButtonClicked(1);
        }
      });
    }
    return nonlinarVar2RightButton;
  }

  /**
   * This method initializes nonlinearParams2LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams2LeftButton() {
    if (nonlinearParams2LeftButton == null) {
      nonlinearParams2LeftButton = new JButton();
      nonlinearParams2LeftButton.setLocation(new Point(244, 26));
      nonlinearParams2LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams2LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams2LeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
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
   * This method initializes nonlinarParams2RightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinarParams2RightButton() {
    if (nonlinarParams2RightButton == null) {
      nonlinarParams2RightButton = new JButton();
      nonlinarParams2RightButton.setLocation(new Point(268, 26));
      nonlinarParams2RightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinarParams2RightButton.setPreferredSize(new Dimension(22, 22));
      nonlinarParams2RightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      nonlinarParams2RightButton.setText("");
      nonlinarParams2RightButton.setSize(new Dimension(22, 22));
      nonlinarParams2RightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsRightButtonClicked(1);
        }
      });
    }
    return nonlinarParams2RightButton;
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
      nonlinearVar3Panel.setLocation(new Point(2, 112));
      nonlinearVar3Panel.setSize(new Dimension(292, 52));
      nonlinearVar3Panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
      nonlinearVar3Panel.add(nonlinearVar3Lbl, null);
      nonlinearVar3Panel.add(getNonlinearVar3Cmb(), null);
      nonlinearVar3Panel.add(getNonlinearVar3REd(), null);
      nonlinearVar3Panel.add(nonlinearParams3Lbl, null);
      nonlinearVar3Panel.add(getNonlinearParams3Cmb(), null);
      nonlinearVar3Panel.add(getNonlinearParams3REd(), null);
      nonlinearVar3Panel.add(getNonlinearVar3LeftButton(), null);
      nonlinearVar3Panel.add(getNonlinarVar3RightButton(), null);
      nonlinearVar3Panel.add(getNonlinearParams3LeftButton(), null);
      nonlinearVar3Panel.add(getNonlinarParams3RightButton(), null);
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
      nonlinearVar3Cmb.setSize(new Dimension(120, 22));
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
  private JTextField getNonlinearVar3REd() {
    if (nonlinearVar3REd == null) {
      nonlinearVar3REd = new JTextField();
      nonlinearVar3REd.setLocation(new Point(188, 2));
      nonlinearVar3REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearVar3REd.setPreferredSize(new Dimension(55, 22));
      nonlinearVar3REd.setText("");
      nonlinearVar3REd.setSize(new Dimension(55, 22));
      nonlinearVar3REd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarREdChanged(2);
        }
      });
      nonlinearVar3REd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.nonlinearVarREdChanged(2);
        }
      });
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
      nonlinearParams3Cmb.setSize(new Dimension(120, 22));
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
  private JTextField getNonlinearParams3REd() {
    if (nonlinearParams3REd == null) {
      nonlinearParams3REd = new JTextField();
      nonlinearParams3REd.setLocation(new Point(188, 26));
      nonlinearParams3REd.setFont(new Font("Dialog", Font.PLAIN, 10));
      nonlinearParams3REd.setPreferredSize(new Dimension(55, 22));
      nonlinearParams3REd.setText("");
      nonlinearParams3REd.setSize(new Dimension(55, 22));
      nonlinearParams3REd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsREdChanged(2);
        }
      });
      nonlinearParams3REd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.nonlinearParamsREdChanged(2);
        }
      });
    }
    return nonlinearParams3REd;
  }

  /**
   * This method initializes nonlinearVar3LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearVar3LeftButton() {
    if (nonlinearVar3LeftButton == null) {
      nonlinearVar3LeftButton = new JButton();
      nonlinearVar3LeftButton.setLocation(new Point(244, 2));
      nonlinearVar3LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearVar3LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearVar3LeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      nonlinearVar3LeftButton.setText("");
      nonlinearVar3LeftButton.setSize(new Dimension(22, 22));
      nonlinearVar3LeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarLeftButtonClicked(2);
        }
      });
    }
    return nonlinearVar3LeftButton;
  }

  /**
   * This method initializes nonlinarVar3RightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinarVar3RightButton() {
    if (nonlinarVar3RightButton == null) {
      nonlinarVar3RightButton = new JButton();
      nonlinarVar3RightButton.setLocation(new Point(268, 2));
      nonlinarVar3RightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinarVar3RightButton.setPreferredSize(new Dimension(22, 22));
      nonlinarVar3RightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      nonlinarVar3RightButton.setText("");
      nonlinarVar3RightButton.setSize(new Dimension(22, 22));
      nonlinarVar3RightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearVarRightButtonClicked(2);
        }
      });
    }
    return nonlinarVar3RightButton;
  }

  /**
   * This method initializes nonlinearParams3LeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinearParams3LeftButton() {
    if (nonlinearParams3LeftButton == null) {
      nonlinearParams3LeftButton = new JButton();
      nonlinearParams3LeftButton.setLocation(new Point(244, 26));
      nonlinearParams3LeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinearParams3LeftButton.setPreferredSize(new Dimension(22, 22));
      nonlinearParams3LeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
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
   * This method initializes nonlinarParams3RightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNonlinarParams3RightButton() {
    if (nonlinarParams3RightButton == null) {
      nonlinarParams3RightButton = new JButton();
      nonlinarParams3RightButton.setLocation(new Point(268, 26));
      nonlinarParams3RightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      nonlinarParams3RightButton.setPreferredSize(new Dimension(22, 22));
      nonlinarParams3RightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      nonlinarParams3RightButton.setText("");
      nonlinarParams3RightButton.setSize(new Dimension(22, 22));
      nonlinarParams3RightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.nonlinearParamsRightButtonClicked(2);
        }
      });
    }
    return nonlinarParams3RightButton;
  }

  /**
   * This method initializes xFormColorREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getXFormColorREd() {
    if (xFormColorREd == null) {
      xFormColorREd = new JTextField();
      xFormColorREd.setPreferredSize(new Dimension(55, 22));
      xFormColorREd.setText("");
      xFormColorREd.setSize(new Dimension(55, 22));
      xFormColorREd.setLocation(new Point(68, 4));
      xFormColorREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormColorREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xFormColorREd_changed();
        }
      });
      xFormColorREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.xFormColorREd_changed();
        }
      });
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
  private JTextField getXFormSymmetryREd() {
    if (xFormSymmetryREd == null) {
      xFormSymmetryREd = new JTextField();
      xFormSymmetryREd.setPreferredSize(new Dimension(55, 22));
      xFormSymmetryREd.setText("");
      xFormSymmetryREd.setSize(new Dimension(55, 22));
      xFormSymmetryREd.setLocation(new Point(68, 30));
      xFormSymmetryREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormSymmetryREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xFormSymmetryREd_changed();
        }
      });
      xFormSymmetryREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.xFormSymmetryREd_changed();
        }
      });
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
      xFormSymmetrySlider.setMinimum(0);
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
  private JTextField getXFormOpacityREd() {
    if (xFormOpacityREd == null) {
      xFormOpacityREd = new JTextField();
      xFormOpacityREd.setPreferredSize(new Dimension(55, 22));
      xFormOpacityREd.setText("");
      xFormOpacityREd.setSize(new Dimension(55, 22));
      xFormOpacityREd.setLocation(new Point(68, 56));
      xFormOpacityREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      xFormOpacityREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.xFormOpacityREd_changed();
        }
      });
      xFormOpacityREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.xFormOpacityREd_changed();
        }
      });
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
      relWeightsEastPanel.setPreferredSize(new Dimension(54, 0));
      relWeightsEastPanel.add(getRelWeightsLeftButton(), null);
      relWeightsEastPanel.add(getRelWeightsRightButton(), null);
    }
    return relWeightsEastPanel;
  }

  /**
   * This method initializes relWeightsLeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRelWeightsLeftButton() {
    if (relWeightsLeftButton == null) {
      relWeightsLeftButton = new JButton();
      relWeightsLeftButton.setPreferredSize(new Dimension(22, 22));
      relWeightsLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      relWeightsLeftButton.setText("");
      relWeightsLeftButton.setSize(new Dimension(22, 22));
      relWeightsLeftButton.setLocation(new Point(4, 4));
      relWeightsLeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      relWeightsLeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.relWeightsLeftButton_clicked();
        }
      });
    }
    return relWeightsLeftButton;
  }

  /**
   * This method initializes relWeightsRightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRelWeightsRightButton() {
    if (relWeightsRightButton == null) {
      relWeightsRightButton = new JButton();
      relWeightsRightButton.setPreferredSize(new Dimension(22, 22));
      relWeightsRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      relWeightsRightButton.setText("");
      relWeightsRightButton.setSize(new Dimension(22, 22));
      relWeightsRightButton.setLocation(new Point(28, 4));
      relWeightsRightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      relWeightsRightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.relWeightsRightButton_clicked();
        }
      });
    }
    return relWeightsRightButton;
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
    }
    return relWeightsTable;
  }

  /**
   * This method initializes transformationWeightPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTransformationWeightPanel() {
    if (transformationWeightPanel == null) {
      transformationWeightPanel = new JPanel();
      transformationWeightPanel.setLayout(null);
      transformationWeightPanel.setPreferredSize(new Dimension(81, 22));
      transformationWeightPanel.add(getTransformationWeightLeftButton(), null);
      transformationWeightPanel.add(getTransformationWeightRightButton(), null);
    }
    return transformationWeightPanel;
  }

  /**
   * This method initializes transformationWeightLeftButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getTransformationWeightLeftButton() {
    if (transformationWeightLeftButton == null) {
      transformationWeightLeftButton = new JButton();
      transformationWeightLeftButton.setPreferredSize(new Dimension(40, 22));
      transformationWeightLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      transformationWeightLeftButton.setText("");
      transformationWeightLeftButton.setSize(new Dimension(40, 22));
      transformationWeightLeftButton.setLocation(new Point(0, 0));
      transformationWeightLeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      transformationWeightLeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.transformationWeightLeftButton_clicked();
        }
      });
    }
    return transformationWeightLeftButton;
  }

  /**
   * This method initializes transformationWeightRightButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getTransformationWeightRightButton() {
    if (transformationWeightRightButton == null) {
      transformationWeightRightButton = new JButton();
      transformationWeightRightButton.setPreferredSize(new Dimension(40, 22));
      transformationWeightRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      transformationWeightRightButton.setText("");
      transformationWeightRightButton.setSize(new Dimension(40, 22));
      transformationWeightRightButton.setLocation(new Point(41, 0));
      transformationWeightRightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      transformationWeightRightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.transformationWeightRightButton_clicked();
        }
      });
    }
    return transformationWeightRightButton;
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
      newFlameButton.setLocation(new Point(228, 4));
      newFlameButton.setSize(new Dimension(125, 52));
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
   * This method initializes zStyleCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getZStyleCmb() {
    if (zStyleCmb == null) {
      zStyleCmb = new JComboBox();
      zStyleCmb.setPreferredSize(new Dimension(125, 22));
      zStyleCmb.setBounds(new Rectangle(206, 5, 125, 22));
      zStyleCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      zStyleCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          tinaController.zStyleCmb_changed();
        }
      });
    }
    return zStyleCmb;
  }

  /**
   * This method initializes tinaAnimatePanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTinaAnimatePanel() {
    if (tinaAnimatePanel == null) {
      animateXFormScriptLbl = new JLabel();
      animateXFormScriptLbl.setPreferredSize(new Dimension(94, 22));
      animateXFormScriptLbl.setText("XForm script");
      animateXFormScriptLbl.setSize(new Dimension(94, 22));
      animateXFormScriptLbl.setLocation(new Point(156, 32));
      animateXFormScriptLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      animateGlobalScriptLbl = new JLabel();
      animateGlobalScriptLbl.setPreferredSize(new Dimension(94, 22));
      animateGlobalScriptLbl.setText("Global script");
      animateGlobalScriptLbl.setSize(new Dimension(94, 22));
      animateGlobalScriptLbl.setLocation(new Point(156, 4));
      animateGlobalScriptLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      animateOutputLbl = new JLabel();
      animateOutputLbl.setPreferredSize(new Dimension(94, 22));
      animateOutputLbl.setText("Output");
      animateOutputLbl.setSize(new Dimension(94, 22));
      animateOutputLbl.setLocation(new Point(530, 4));
      animateOutputLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      animateFramesLbl = new JLabel();
      animateFramesLbl.setPreferredSize(new Dimension(94, 22));
      animateFramesLbl.setText("Frames");
      animateFramesLbl.setSize(new Dimension(94, 22));
      animateFramesLbl.setLocation(new Point(816, 4));
      animateFramesLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaAnimatePanel = new JPanel();
      tinaAnimatePanel.setLayout(null);
      tinaAnimatePanel.add(getAnimationGenerateButton(), null);
      tinaAnimatePanel.add(getAnimateFramesREd(), null);
      tinaAnimatePanel.add(animateFramesLbl, null);
      tinaAnimatePanel.add(getAnimateOutputREd(), null);
      tinaAnimatePanel.add(animateOutputLbl, null);
      tinaAnimatePanel.add(animateGlobalScriptLbl, null);
      tinaAnimatePanel.add(getAnimateScriptCmb(), null);
      tinaAnimatePanel.add(animateXFormScriptLbl, null);
      tinaAnimatePanel.add(getAnimateXFormScriptCmb(), null);
    }
    return tinaAnimatePanel;
  }

  /**
   * This method initializes tinaMorphPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTinaMorphPanel() {
    if (tinaMorphPanel == null) {
      morphFrameLbl = new JLabel();
      morphFrameLbl.setPreferredSize(new Dimension(94, 22));
      morphFrameLbl.setText("Frames");
      morphFrameLbl.setSize(new Dimension(94, 22));
      morphFrameLbl.setLocation(new Point(263, 4));
      morphFrameLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      morphFramesLbl = new JLabel();
      morphFramesLbl.setPreferredSize(new Dimension(94, 22));
      morphFramesLbl.setText("Frames");
      morphFramesLbl.setSize(new Dimension(94, 22));
      morphFramesLbl.setLocation(new Point(816, 4));
      morphFramesLbl.setFont(new Font("Dialog", Font.BOLD, 10));
      tinaMorphPanel = new JPanel();
      tinaMorphPanel.setLayout(null);
      tinaMorphPanel.add(getSetMorphFlame1Button(), null);
      tinaMorphPanel.add(getSetMorphFlame2Button(), null);
      tinaMorphPanel.add(morphFramesLbl, null);
      tinaMorphPanel.add(getMorphFramesREd(), null);
      tinaMorphPanel.add(morphFrameLbl, null);
      tinaMorphPanel.add(getMorphFrameREd(), null);
      tinaMorphPanel.add(getMorphCheckBox(), null);
      tinaMorphPanel.add(getMorphFrameSlider(), null);
      tinaMorphPanel.add(getImportMorphedFlameButton(), null);
    }
    return tinaMorphPanel;
  }

  /**
   * This method initializes setMorphFlame1Button	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getSetMorphFlame1Button() {
    if (setMorphFlame1Button == null) {
      setMorphFlame1Button = new JButton();
      setMorphFlame1Button.setBounds(new Rectangle(4, 4, 125, 24));
      setMorphFlame1Button.setPreferredSize(new Dimension(125, 24));
      setMorphFlame1Button.setText("Set Flame 1");
      setMorphFlame1Button.setFont(new Font("Dialog", Font.BOLD, 10));
      setMorphFlame1Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.setMorphFlame1();
        }
      });
    }
    return setMorphFlame1Button;
  }

  /**
   * This method initializes setMorphFlame2Button	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getSetMorphFlame2Button() {
    if (setMorphFlame2Button == null) {
      setMorphFlame2Button = new JButton();
      setMorphFlame2Button.setPreferredSize(new Dimension(125, 24));
      setMorphFlame2Button.setText("Set Flame 2");
      setMorphFlame2Button.setSize(new Dimension(125, 24));
      setMorphFlame2Button.setLocation(new Point(4, 32));
      setMorphFlame2Button.setFont(new Font("Dialog", Font.BOLD, 10));
      setMorphFlame2Button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.setMorphFlame2();
        }
      });
    }
    return setMorphFlame2Button;
  }

  /**
   * This method initializes morphFramesREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getMorphFramesREd() {
    if (morphFramesREd == null) {
      morphFramesREd = new JTextField();
      morphFramesREd.setPreferredSize(new Dimension(56, 22));
      morphFramesREd.setText("72");
      morphFramesREd.setSize(new Dimension(56, 22));
      morphFramesREd.setLocation(new Point(912, 4));
      morphFramesREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      morphFramesREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.morphFramesREd_changed();
        }
      });
      morphFramesREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.morphFramesREd_changed();
        }
      });
    }
    return morphFramesREd;
  }

  /**
   * This method initializes morphFrameREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getMorphFrameREd() {
    if (morphFrameREd == null) {
      morphFrameREd = new JTextField();
      morphFrameREd.setPreferredSize(new Dimension(56, 22));
      morphFrameREd.setText("33");
      morphFrameREd.setSize(new Dimension(56, 22));
      morphFrameREd.setLocation(new Point(359, 4));
      morphFrameREd.setFont(new Font("Dialog", Font.PLAIN, 10));
      morphFrameREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.morphFrameREd_changed();
        }
      });
      morphFrameREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.morphFrameREd_changed();
        }
      });
    }
    return morphFrameREd;
  }

  /**
   * This method initializes morphCheckBox	
   * 	
   * @return javax.swing.JCheckBox	
   */
  private JCheckBox getMorphCheckBox() {
    if (morphCheckBox == null) {
      morphCheckBox = new JCheckBox();
      morphCheckBox.setPreferredSize(new Dimension(150, 22));
      morphCheckBox.setLocation(new Point(263, 32));
      morphCheckBox.setSize(new Dimension(150, 22));
      morphCheckBox.setText("Morphing enabled");
      morphCheckBox.setFont(new Font("Dialog", Font.BOLD, 10));
      morphCheckBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.morphCheckBox_changed();
        }
      });
    }
    return morphCheckBox;
  }

  /**
   * This method initializes morphFrameSlider	
   * 	
   * @return javax.swing.JSlider	
   */
  private JSlider getMorphFrameSlider() {
    if (morphFrameSlider == null) {
      morphFrameSlider = new JSlider();
      morphFrameSlider.setMaximum(100);
      morphFrameSlider.setMinimum(1);
      morphFrameSlider.setValue(0);
      morphFrameSlider.setSize(new Dimension(220, 19));
      morphFrameSlider.setLocation(new Point(416, 4));
      morphFrameSlider.setPreferredSize(new Dimension(220, 19));
      morphFrameSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.morphFrameSlider_changed();
        }
      });
    }
    return morphFrameSlider;
  }

  /**
   * This method initializes importMorphedFlameButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getImportMorphedFlameButton() {
    if (importMorphedFlameButton == null) {
      importMorphedFlameButton = new JButton();
      importMorphedFlameButton.setBounds(new Rectangle(132, 4, 125, 24));
      importMorphedFlameButton.setPreferredSize(new Dimension(125, 24));
      importMorphedFlameButton.setText("Import morphed");
      importMorphedFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      importMorphedFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.importMorphedFlameButton_clicked();
        }
      });
    }
    return importMorphedFlameButton;
  }

  /**
   * This method initializes animationGenerateButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getAnimationGenerateButton() {
    if (animationGenerateButton == null) {
      animationGenerateButton = new JButton();
      animationGenerateButton.setPreferredSize(new Dimension(125, 52));
      animationGenerateButton.setText("Generate");
      animationGenerateButton.setSize(new Dimension(125, 52));
      animationGenerateButton.setLocation(new Point(4, 4));
      animationGenerateButton.setFont(new Font("Dialog", Font.BOLD, 10));
      animationGenerateButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.animationGenerateButton_clicked();
        }
      });
    }
    return animationGenerateButton;
  }

  /**
   * This method initializes animateFramesREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getAnimateFramesREd() {
    if (animateFramesREd == null) {
      animateFramesREd = new JTextField();
      animateFramesREd.setPreferredSize(new Dimension(56, 22));
      animateFramesREd.setText("72");
      animateFramesREd.setSize(new Dimension(56, 22));
      animateFramesREd.setLocation(new Point(912, 4));
      animateFramesREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return animateFramesREd;
  }

  /**
   * This method initializes animateOutputREd	
   * 	
   * @return javax.swing.JTextField	
   */
  private JTextField getAnimateOutputREd() {
    if (animateOutputREd == null) {
      animateOutputREd = new JTextField();
      animateOutputREd.setBounds(new Rectangle(626, 4, 185, 22));
      animateOutputREd.setPreferredSize(new Dimension(56, 22));
      animateOutputREd.setText("C:\\TMP\\wf\\rotate1\\Img");
      animateOutputREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    }
    return animateOutputREd;
  }

  /**
   * This method initializes animateScriptCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getAnimateScriptCmb() {
    if (animateScriptCmb == null) {
      animateScriptCmb = new JComboBox();
      animateScriptCmb.setPreferredSize(new Dimension(275, 22));
      animateScriptCmb.setSize(new Dimension(275, 22));
      animateScriptCmb.setLocation(new Point(253, 4));
      animateScriptCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return animateScriptCmb;
  }

  /**
   * This method initializes animateXFormScriptCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  private JComboBox getAnimateXFormScriptCmb() {
    if (animateXFormScriptCmb == null) {
      animateXFormScriptCmb = new JComboBox();
      animateXFormScriptCmb.setPreferredSize(new Dimension(275, 22));
      animateXFormScriptCmb.setSize(new Dimension(275, 22));
      animateXFormScriptCmb.setLocation(new Point(253, 32));
      animateXFormScriptCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    }
    return animateXFormScriptCmb;
  }

  /**
   * This method initializes triangleOperationsPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTriangleOperationsPanel() {
    if (triangleOperationsPanel == null) {
      triangleOperationsPanel = new JPanel();
      triangleOperationsPanel.setLayout(new FlowLayout());
      triangleOperationsPanel.setPreferredSize(new Dimension(52, 0));
      triangleOperationsPanel.add(getMouseTransformMoveButton(), null);
      triangleOperationsPanel.add(getMouseTransformRotateButton(), null);
      triangleOperationsPanel.add(getMouseTransformScaleButton(), null);
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
      mouseTransformMoveButton.setPreferredSize(new Dimension(42, 24));
      mouseTransformMoveButton.setSelected(true);
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
      mouseTransformRotateButton.setPreferredSize(new Dimension(42, 24));
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
      mouseTransformScaleButton.setPreferredSize(new Dimension(42, 24));
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
      centerNorthPanel.add(getZStyleCmb(), null);
      centerNorthPanel.add(zStyleLbl, null);
    }
    return centerNorthPanel;
  }

  /**
   * This method initializes centerWestPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getCenterWestPanel() {
    if (centerWestPanel == null) {
      centerWestPanel = new JPanel();
      centerWestPanel.setLayout(new GridBagLayout());
      centerWestPanel.setPreferredSize(new Dimension(10, 0));
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
      centerDescLabel = new JLabel();
      centerDescLabel.setText("  (just double-click on thumbnail to load it into main area)");
      centerDescLabel.setHorizontalAlignment(SwingConstants.CENTER);
      centerDescLabel.setFont(new Font("Dialog", Font.BOLD, 10));
      centerCenterPanel = new JPanel();
      centerCenterPanel.setLayout(new BorderLayout());
      centerCenterPanel.setBackground(Color.gray);
      centerCenterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      centerCenterPanel.add(centerDescLabel, BorderLayout.SOUTH);
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
      randomStyleCmb.setSize(new Dimension(125, 22));
      randomStyleCmb.setLocation(new Point(100, 4));
      randomStyleCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      randomStyleCmb.removeAllItems();
      randomStyleCmb.addItem(RandomFlameGeneratorStyle.V0);
      randomStyleCmb.addItem(RandomFlameGeneratorStyle.V1);
      randomStyleCmb.addItem(RandomFlameGeneratorStyle.DDD);
      randomStyleCmb.addItem(RandomFlameGeneratorStyle.V0_SYMM);
      randomStyleCmb.addItem(RandomFlameGeneratorStyle.V1_SYMM);
      randomStyleCmb.addItem(RandomFlameGeneratorStyle.DDD_SYMM);
      randomStyleCmb.addItem(RandomFlameGeneratorStyle.ALL);
      randomStyleCmb.setSelectedItem(RandomFlameGeneratorStyle.ALL);
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
      affineEditPostTransformButton.setPreferredSize(new Dimension(125, 24));
      affineEditPostTransformButton.setSize(new Dimension(171, 24));
      affineEditPostTransformButton.setText("Edit Post  Transform");
      affineEditPostTransformButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineEditPostTransformButton.setLocation(new Point(66, 150));
      affineEditPostTransformButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.affineEditPostTransformButton_clicked();
        }
      });
    }
    return affineEditPostTransformButton;
  }

} //  @jve:decl-index=0:visual-constraint="10,10"
