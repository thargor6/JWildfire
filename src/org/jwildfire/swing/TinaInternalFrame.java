package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jwildfire.base.Prefs;

public class TinaInternalFrame extends JInternalFrame {
  private TINAController tinaController; //  @jve:decl-index=0:
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

  private JButton tinaRandomFlameButton = null;

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

  private JLabel tinaGammaThresholdLbl = null;

  private JLabel tinaWhiteLevelLbl = null;

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

  private JTextField tinaGammaThresholdREd = null;

  private JTextField tinaWhiteLevelREd = null;

  private JSlider tinaContrastSlider = null;

  private JSlider tinaGammaSlider = null;

  private JSlider tinaVibrancySlider = null;

  private JSlider tinaFilterRadiusSlider = null;

  private JSlider tinaOversampleSlider = null;

  private JSlider tinaGammaThresholdSlider = null;

  private JSlider tinaWhiteLevelSlider = null;

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
    this.setLocation(new Point(561, 116));
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
      tinaNorthPanel.setPreferredSize(new Dimension(0, 120));
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
      tinaSouthPanel.setPreferredSize(new Dimension(0, 180));
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
      tinaPixelsPerUnitLbl.setBounds(new Rectangle(491, 91, 110, 26));
      tinaPixelsPerUnitLbl.setText("Pixels per unit");
      tinaPixelsPerUnitLbl.setPreferredSize(new Dimension(110, 26));
      tinaCameraZoomLbl = new JLabel();
      tinaCameraZoomLbl.setText("Zoom");
      tinaCameraZoomLbl.setLocation(new Point(491, 64));
      tinaCameraZoomLbl.setSize(new Dimension(110, 26));
      tinaCameraZoomLbl.setPreferredSize(new Dimension(110, 26));
      tinaCameraCentreYLbl = new JLabel();
      tinaCameraCentreYLbl.setText("CentreY");
      tinaCameraCentreYLbl.setLocation(new Point(490, 35));
      tinaCameraCentreYLbl.setSize(new Dimension(110, 26));
      tinaCameraCentreYLbl.setPreferredSize(new Dimension(110, 26));
      tinaCameraCentreXLbl = new JLabel();
      tinaCameraCentreXLbl.setText("CentreX");
      tinaCameraCentreXLbl.setLocation(new Point(490, 7));
      tinaCameraCentreXLbl.setSize(new Dimension(110, 26));
      tinaCameraCentreXLbl.setPreferredSize(new Dimension(110, 26));
      tinaCameraPerspectiveLbl = new JLabel();
      tinaCameraPerspectiveLbl.setText("Perspective");
      tinaCameraPerspectiveLbl.setLocation(new Point(5, 100));
      tinaCameraPerspectiveLbl.setSize(new Dimension(110, 26));
      tinaCameraPerspectiveLbl.setPreferredSize(new Dimension(110, 26));
      tinaCameraYawLbl = new JLabel();
      tinaCameraYawLbl.setText("Yaw");
      tinaCameraYawLbl.setLocation(new Point(5, 68));
      tinaCameraYawLbl.setSize(new Dimension(110, 26));
      tinaCameraYawLbl.setPreferredSize(new Dimension(110, 26));
      tinaCameraPitchLbl = new JLabel();
      tinaCameraPitchLbl.setText("Pitch");
      tinaCameraPitchLbl.setLocation(new Point(5, 36));
      tinaCameraPitchLbl.setSize(new Dimension(110, 26));
      tinaCameraPitchLbl.setPreferredSize(new Dimension(110, 26));
      tinaCameraRollLbl = new JLabel();
      tinaCameraRollLbl.setText("Roll");
      tinaCameraRollLbl.setLocation(new Point(6, 5));
      tinaCameraRollLbl.setSize(new Dimension(110, 26));
      tinaCameraRollLbl.setPreferredSize(new Dimension(110, 26));
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
      tinaBGColorLbl.setBounds(new Rectangle(469, 89, 110, 26));
      tinaBGColorLbl.setText("Background color");
      tinaBGColorLbl.setPreferredSize(new Dimension(110, 26));
      tinaWhiteLevelLbl = new JLabel();
      tinaWhiteLevelLbl.setBounds(new Rectangle(469, 59, 110, 26));
      tinaWhiteLevelLbl.setText("White level");
      tinaWhiteLevelLbl.setPreferredSize(new Dimension(110, 26));
      tinaGammaThresholdLbl = new JLabel();
      tinaGammaThresholdLbl.setText("Gamma threshold");
      tinaGammaThresholdLbl.setLocation(new Point(467, 31));
      tinaGammaThresholdLbl.setSize(new Dimension(110, 26));
      tinaGammaThresholdLbl.setPreferredSize(new Dimension(110, 26));
      tinaOversampleLbl = new JLabel();
      tinaOversampleLbl.setText("Oversample");
      tinaOversampleLbl.setLocation(new Point(467, 2));
      tinaOversampleLbl.setSize(new Dimension(110, 26));
      tinaOversampleLbl.setPreferredSize(new Dimension(110, 26));
      tinaFilterRadiusLbl = new JLabel();
      tinaFilterRadiusLbl.setText("Filter radius");
      tinaFilterRadiusLbl.setLocation(new Point(1, 114));
      tinaFilterRadiusLbl.setSize(new Dimension(110, 26));
      tinaFilterRadiusLbl.setPreferredSize(new Dimension(110, 26));
      tinaVibrancyLbl = new JLabel();
      tinaVibrancyLbl.setText("Vibrancy");
      tinaVibrancyLbl.setLocation(new Point(1, 86));
      tinaVibrancyLbl.setSize(new Dimension(110, 26));
      tinaVibrancyLbl.setPreferredSize(new Dimension(110, 26));
      tinaGammaLbl = new JLabel();
      tinaGammaLbl.setText("Gamma");
      tinaGammaLbl.setLocation(new Point(1, 58));
      tinaGammaLbl.setSize(new Dimension(110, 26));
      tinaGammaLbl.setPreferredSize(new Dimension(110, 26));
      tinaContrastLbl = new JLabel();
      tinaContrastLbl.setText("Contrast");
      tinaContrastLbl.setLocation(new Point(2, 32));
      tinaContrastLbl.setSize(new Dimension(110, 26));
      tinaContrastLbl.setPreferredSize(new Dimension(110, 26));
      tinaBrightnessLbl = new JLabel();
      tinaBrightnessLbl.setText("Brightness");
      tinaBrightnessLbl.setLocation(new Point(3, 4));
      tinaBrightnessLbl.setSize(new Dimension(110, 26));
      tinaBrightnessLbl.setPreferredSize(new Dimension(110, 26));
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
      tinaColoringPanel.add(tinaGammaThresholdLbl, null);
      tinaColoringPanel.add(tinaWhiteLevelLbl, null);
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
      tinaColoringPanel.add(getTinaGammaThresholdREd(), null);
      tinaColoringPanel.add(getTinaWhiteLevelREd(), null);
      tinaColoringPanel.add(getTinaContrastSlider(), null);
      tinaColoringPanel.add(getTinaGammaSlider(), null);
      tinaColoringPanel.add(getTinaVibrancySlider(), null);
      tinaColoringPanel.add(getTinaFilterRadiusSlider(), null);
      tinaColoringPanel.add(getTinaOversampleSlider(), null);
      tinaColoringPanel.add(getTinaGammaThresholdSlider(), null);
      tinaColoringPanel.add(getTinaWhiteLevelSlider(), null);
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
      tinaCameraRollREd.setPreferredSize(new Dimension(120, 26));
      tinaCameraRollREd.setText("");
      tinaCameraRollREd.setLocation(new Point(119, 5));
      tinaCameraRollREd.setSize(new Dimension(120, 26));
      tinaCameraRollREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaCameraPitchREd.setPreferredSize(new Dimension(120, 26));
      tinaCameraPitchREd.setText("");
      tinaCameraPitchREd.setLocation(new Point(120, 37));
      tinaCameraPitchREd.setSize(new Dimension(120, 26));
      tinaCameraPitchREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaCameraYawREd.setPreferredSize(new Dimension(120, 26));
      tinaCameraYawREd.setText("");
      tinaCameraYawREd.setLocation(new Point(120, 67));
      tinaCameraYawREd.setSize(new Dimension(120, 26));
      tinaCameraYawREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaCameraPerspectiveREd.setPreferredSize(new Dimension(120, 26));
      tinaCameraPerspectiveREd.setText("");
      tinaCameraPerspectiveREd.setLocation(new Point(120, 100));
      tinaCameraPerspectiveREd.setSize(new Dimension(120, 26));
      tinaCameraPerspectiveREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaCameraRollSlider.setLocation(new Point(240, 7));
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
      tinaCameraPitchSlider.setLocation(new Point(242, 40));
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
      tinaCameraYawSlider.setLocation(new Point(242, 69));
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
      tinaCameraPerspectiveSlider.setLocation(new Point(240, 104));
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
      tinaLoadFlameButton.setBounds(new Rectangle(10, 9, 154, 28));
      tinaLoadFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.loadFlameButton_actionPerformed(e);
        }
      });
    }
    return tinaLoadFlameButton;
  }

  /**
   * This method initializes tinaRandomFlameButton  
   *  
   * @return javax.swing.JButton  
   */
  private JButton getTinaRandomFlameButton() {
    if (tinaRandomFlameButton == null) {
      tinaRandomFlameButton = new JButton();
      tinaRandomFlameButton.setText("Random Flame");
      tinaRandomFlameButton.setMnemonic(KeyEvent.VK_D);
      tinaRandomFlameButton.setBounds(new Rectangle(176, 9, 155, 26));
      tinaRandomFlameButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.randomFlameButton_actionPerformed(e);
        }
      });
    }
    return tinaRandomFlameButton;
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
      tinaSaveFlameButton.setBounds(new Rectangle(343, 8, 151, 30));
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
      tinaRenderFlameButton.setText("Render Flame");
      tinaRenderFlameButton.setMnemonic(KeyEvent.VK_R);
      tinaRenderFlameButton.setBounds(new Rectangle(343, 52, 148, 26));
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
      tinaPreviewQualityREd.setPreferredSize(new Dimension(55, 26));
      tinaPreviewQualityREd.setText("3");
      tinaPreviewQualityREd.setBounds(new Rectangle(961, 7, 55, 26));
      tinaPreviewQualityREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaExportImageButton.setBounds(new Rectangle(563, 12, 149, 33));
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
      tinaCameraCentreXREd.setPreferredSize(new Dimension(120, 26));
      tinaCameraCentreXREd.setText("");
      tinaCameraCentreXREd.setLocation(new Point(602, 7));
      tinaCameraCentreXREd.setSize(new Dimension(120, 26));
      tinaCameraCentreXREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaCameraCentreYREd.setPreferredSize(new Dimension(120, 26));
      tinaCameraCentreYREd.setText("");
      tinaCameraCentreYREd.setLocation(new Point(601, 34));
      tinaCameraCentreYREd.setSize(new Dimension(120, 26));
      tinaCameraCentreYREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaCameraCentreXSlider.setLocation(new Point(723, 12));
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
      tinaCameraCentreYSlider.setLocation(new Point(723, 37));
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
      tinaCameraZoomREd.setPreferredSize(new Dimension(120, 26));
      tinaCameraZoomREd.setText("");
      tinaCameraZoomREd.setLocation(new Point(603, 63));
      tinaCameraZoomREd.setSize(new Dimension(120, 26));
      tinaCameraZoomREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaCameraZoomSlider.setLocation(new Point(724, 66));
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
      tinaBrightnessREd.setPreferredSize(new Dimension(120, 26));
      tinaBrightnessREd.setText("");
      tinaBrightnessREd.setSize(new Dimension(120, 26));
      tinaBrightnessREd.setLocation(new Point(117, 5));
      tinaBrightnessREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaBrightnessSlider.setLocation(new Point(238, 9));
      tinaBrightnessSlider.setSize(new Dimension(220, 19));
      tinaBrightnessSlider.setPreferredSize(new Dimension(220, 19));
      tinaBrightnessSlider.setValue(0);
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
      tinaPixelsPerUnitREd.setBounds(new Rectangle(604, 91, 120, 26));
      tinaPixelsPerUnitREd.setPreferredSize(new Dimension(120, 26));
      tinaPixelsPerUnitREd.setText("");
      tinaPixelsPerUnitREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPixelsPerUnitSlider.setBounds(new Rectangle(726, 93, 220, 19));
      tinaPixelsPerUnitSlider.setMaximum(1000);
      tinaPixelsPerUnitSlider.setMinimum(0);
      tinaPixelsPerUnitSlider.setValue(0);
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
      tinaRenderQualityREd.setPreferredSize(new Dimension(55, 26));
      tinaRenderQualityREd.setText("100");
      tinaRenderQualityREd.setBounds(new Rectangle(960, 42, 55, 26));
      tinaRenderQualityREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaBGColorRedREd.setPreferredSize(new Dimension(55, 26));
      tinaBGColorRedREd.setText("");
      tinaBGColorRedREd.setLocation(new Point(582, 89));
      tinaBGColorRedREd.setSize(new Dimension(55, 26));
      tinaBGColorRedREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaBGColorGreenREd.setBounds(new Rectangle(724, 88, 55, 26));
      tinaBGColorGreenREd.setPreferredSize(new Dimension(55, 26));
      tinaBGColorGreenREd.setText("");
      tinaBGColorGreenREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaBGColorBlueREd.setBounds(new Rectangle(862, 88, 55, 26));
      tinaBGColorBlueREd.setPreferredSize(new Dimension(55, 26));
      tinaBGColorBlueREd.setText("");
      tinaBGColorBlueREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaBGColorRedSlider.setBounds(new Rectangle(641, 92, 80, 19));
      tinaBGColorRedSlider.setMaximum(255);
      tinaBGColorRedSlider.setMinimum(0);
      tinaBGColorRedSlider.setValue(0);
      tinaBGColorRedSlider.setPreferredSize(new Dimension(80, 19));
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
      tinaBGColorGreenSlider.setBounds(new Rectangle(780, 91, 80, 19));
      tinaBGColorGreenSlider.setMaximum(255);
      tinaBGColorGreenSlider.setMinimum(0);
      tinaBGColorGreenSlider.setValue(0);
      tinaBGColorGreenSlider.setPreferredSize(new Dimension(80, 19));
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
      tinaBGColorBlueSlider.setBounds(new Rectangle(918, 91, 80, 19));
      tinaBGColorBlueSlider.setMaximum(255);
      tinaBGColorBlueSlider.setMinimum(0);
      tinaBGColorBlueSlider.setValue(0);
      tinaBGColorBlueSlider.setPreferredSize(new Dimension(80, 19));
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
      tinaContrastREd.setBounds(new Rectangle(116, 32, 120, 26));
      tinaContrastREd.setPreferredSize(new Dimension(120, 26));
      tinaContrastREd.setText("");
      tinaContrastREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaGammaREd.setBounds(new Rectangle(115, 59, 120, 26));
      tinaGammaREd.setPreferredSize(new Dimension(120, 26));
      tinaGammaREd.setText("");
      tinaGammaREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaVibrancyREd.setBounds(new Rectangle(114, 87, 120, 26));
      tinaVibrancyREd.setPreferredSize(new Dimension(120, 26));
      tinaVibrancyREd.setText("");
      tinaVibrancyREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaFilterRadiusREd.setBounds(new Rectangle(114, 114, 120, 26));
      tinaFilterRadiusREd.setPreferredSize(new Dimension(120, 26));
      tinaFilterRadiusREd.setText("");
      tinaFilterRadiusREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaOversampleREd.setBounds(new Rectangle(581, 3, 120, 26));
      tinaOversampleREd.setPreferredSize(new Dimension(120, 26));
      tinaOversampleREd.setText("");
      tinaOversampleREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
   * This method initializes tinaGammaThresholdREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaGammaThresholdREd() {
    if (tinaGammaThresholdREd == null) {
      tinaGammaThresholdREd = new JTextField();
      tinaGammaThresholdREd.setBounds(new Rectangle(582, 31, 120, 26));
      tinaGammaThresholdREd.setPreferredSize(new Dimension(120, 26));
      tinaGammaThresholdREd.setText("");
      tinaGammaThresholdREd.setFont(new Font("Dialog", Font.PLAIN, 12));
      tinaGammaThresholdREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.gammaThresholdREd_changed();
        }
      });
      tinaGammaThresholdREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.gammaThresholdREd_changed();
        }
      });
    }
    return tinaGammaThresholdREd;
  }

  /**
   * This method initializes tinaWhiteLevelREd  
   *  
   * @return javax.swing.JTextField 
   */
  private JTextField getTinaWhiteLevelREd() {
    if (tinaWhiteLevelREd == null) {
      tinaWhiteLevelREd = new JTextField();
      tinaWhiteLevelREd.setBounds(new Rectangle(583, 60, 120, 26));
      tinaWhiteLevelREd.setPreferredSize(new Dimension(120, 26));
      tinaWhiteLevelREd.setText("");
      tinaWhiteLevelREd.setFont(new Font("Dialog", Font.PLAIN, 12));
      tinaWhiteLevelREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.whiteLevelREd_changed();
        }
      });
      tinaWhiteLevelREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          tinaController.whiteLevelREd_changed();
        }
      });
    }
    return tinaWhiteLevelREd;
  }

  /**
   * This method initializes tinaContrastSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaContrastSlider() {
    if (tinaContrastSlider == null) {
      tinaContrastSlider = new JSlider();
      tinaContrastSlider.setBounds(new Rectangle(237, 35, 220, 19));
      tinaContrastSlider.setMaximum(500);
      tinaContrastSlider.setMinimum(0);
      tinaContrastSlider.setValue(0);
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
      tinaGammaSlider.setBounds(new Rectangle(237, 61, 220, 19));
      tinaGammaSlider.setMaximum(1000);
      tinaGammaSlider.setMinimum(0);
      tinaGammaSlider.setValue(0);
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
      tinaVibrancySlider.setBounds(new Rectangle(235, 89, 220, 19));
      tinaVibrancySlider.setMaximum(100);
      tinaVibrancySlider.setMinimum(0);
      tinaVibrancySlider.setValue(0);
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
      tinaFilterRadiusSlider.setBounds(new Rectangle(236, 117, 220, 19));
      tinaFilterRadiusSlider.setMaximum(500);
      tinaFilterRadiusSlider.setMinimum(0);
      tinaFilterRadiusSlider.setValue(0);
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
      tinaOversampleSlider.setBounds(new Rectangle(703, 6, 220, 19));
      tinaOversampleSlider.setMaximum(10);
      tinaOversampleSlider.setMinimum(0);
      tinaOversampleSlider.setValue(0);
      tinaOversampleSlider.setMajorTickSpacing(1);
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
   * This method initializes tinaGammaThresholdSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaGammaThresholdSlider() {
    if (tinaGammaThresholdSlider == null) {
      tinaGammaThresholdSlider = new JSlider();
      tinaGammaThresholdSlider.setBounds(new Rectangle(703, 35, 220, 19));
      tinaGammaThresholdSlider.setMaximum(500);
      tinaGammaThresholdSlider.setMinimum(0);
      tinaGammaThresholdSlider.setValue(0);
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
   * This method initializes tinaWhiteLevelSlider 
   *  
   * @return javax.swing.JSlider  
   */
  private JSlider getTinaWhiteLevelSlider() {
    if (tinaWhiteLevelSlider == null) {
      tinaWhiteLevelSlider = new JSlider();
      tinaWhiteLevelSlider.setBounds(new Rectangle(704, 63, 220, 19));
      tinaWhiteLevelSlider.setMaximum(255);
      tinaWhiteLevelSlider.setMinimum(0);
      tinaWhiteLevelSlider.setValue(0);
      tinaWhiteLevelSlider.setPreferredSize(new Dimension(220, 19));
      tinaWhiteLevelSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent e) {
          tinaController.whiteLevelSlider_stateChanged(e);
        }
      });
    }
    return tinaWhiteLevelSlider;
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
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      tinaNorthTabbedPane.addTab("Main", null, getTinaNorthMainPanel(), null);
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
      tinaRenderSizeLbl = new JLabel();
      tinaRenderSizeLbl.setBounds(new Rectangle(560, 53, 110, 26));
      tinaRenderSizeLbl.setText("Export size");
      tinaRenderSizeLbl.setPreferredSize(new Dimension(110, 26));
      tinaNorthMainPanel = new JPanel();
      tinaNorthMainPanel.setLayout(null);
      tinaCameraRenderQualityLbl = new JLabel();
      tinaCameraRenderQualityLbl.setText("Render Quality");
      tinaCameraRenderQualityLbl.setBounds(new Rectangle(844, 47, 110, 26));
      tinaCameraRenderQualityLbl.setPreferredSize(new Dimension(110, 26));
      tinaCameraPreviewQualityLbl = new JLabel();
      tinaCameraPreviewQualityLbl.setText("Preview Quality");
      tinaCameraPreviewQualityLbl.setBounds(new Rectangle(846, 5, 110, 26));
      tinaCameraPreviewQualityLbl.setPreferredSize(new Dimension(110, 26));
      tinaNorthMainPanel.add(getTinaLoadFlameButton(), null);
      tinaNorthMainPanel.add(getTinaSaveFlameButton(), null);
      tinaNorthMainPanel.add(getTinaRandomFlameButton(), null);
      tinaNorthMainPanel.add(getTinaExportImageButton(), null);
      tinaNorthMainPanel.add(getTinaRenderFlameButton(), null);
      tinaNorthMainPanel.add(tinaCameraPreviewQualityLbl, null);
      tinaNorthMainPanel.add(getTinaPreviewQualityREd(), null);
      tinaNorthMainPanel.add(tinaCameraRenderQualityLbl, null);
      tinaNorthMainPanel.add(getTinaRenderQualityREd(), null);
      tinaNorthMainPanel.add(tinaRenderSizeLbl, null);
      tinaNorthMainPanel.add(getTinaRenderWidthREd(), null);
      tinaNorthMainPanel.add(getTinaRenderHeightREd(), null);
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
      tinaPaletteRandomPointsLbl.setBounds(new Rectangle(7, 7, 110, 26));
      tinaPaletteRandomPointsLbl.setPreferredSize(new Dimension(110, 26));
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
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      tinaVariationPanel = new JPanel();
      tinaVariationPanel.setLayout(null);
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
      tinaModifiedWeightsPanel.setLayout(null);
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
      tinaTransformationColorPanel = new JPanel();
      tinaTransformationColorPanel.setLayout(null);
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
      tinaPaletteRandomPointsREd.setPreferredSize(new Dimension(55, 26));
      tinaPaletteRandomPointsREd.setText("7");
      tinaPaletteRandomPointsREd.setBounds(new Rectangle(118, 7, 55, 26));
      tinaPaletteRandomPointsREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaRandomPaletteButton.setBounds(new Rectangle(6, 36, 168, 26));
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
      tinaGrabPaletteFromImageButton.setBounds(new Rectangle(10, 91, 171, 26));
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
      tinaPaletteCreatePanel = new JPanel();
      tinaPaletteCreatePanel.setLayout(null);
      tinaPaletteCreatePanel.add(getTinaPaletteRandomPointsREd(), null);
      tinaPaletteCreatePanel.add(getTinaRandomPaletteButton(), null);
      tinaPaletteCreatePanel.add(tinaPaletteRandomPointsLbl, null);
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
      tinaPaletteBrightnessLbl.setSize(new Dimension(68, 26));
      tinaPaletteBrightnessLbl.setLocation(new Point(0, 208));
      tinaPaletteBrightnessLbl.setPreferredSize(new Dimension(68, 26));
      tinaPaletteGammaLbl = new JLabel();
      tinaPaletteGammaLbl.setText("Gamma");
      tinaPaletteGammaLbl.setSize(new Dimension(68, 26));
      tinaPaletteGammaLbl.setLocation(new Point(0, 182));
      tinaPaletteGammaLbl.setPreferredSize(new Dimension(68, 26));
      tinaPaletteContrastLbl = new JLabel();
      tinaPaletteContrastLbl.setText("Contrast");
      tinaPaletteContrastLbl.setSize(new Dimension(68, 26));
      tinaPaletteContrastLbl.setLocation(new Point(0, 156));
      tinaPaletteContrastLbl.setPreferredSize(new Dimension(68, 26));
      tinaPaletteSaturationLbl = new JLabel();
      tinaPaletteSaturationLbl.setText("Saturation");
      tinaPaletteSaturationLbl.setSize(new Dimension(68, 26));
      tinaPaletteSaturationLbl.setLocation(new Point(0, 130));
      tinaPaletteSaturationLbl.setPreferredSize(new Dimension(68, 26));
      tinaPaletteHueLbl = new JLabel();
      tinaPaletteHueLbl.setText("Hue");
      tinaPaletteHueLbl.setSize(new Dimension(68, 26));
      tinaPaletteHueLbl.setLocation(new Point(0, 104));
      tinaPaletteHueLbl.setPreferredSize(new Dimension(68, 26));
      tinaPaletteBlueLbl = new JLabel();
      tinaPaletteBlueLbl.setText("Blue");
      tinaPaletteBlueLbl.setSize(new Dimension(68, 26));
      tinaPaletteBlueLbl.setLocation(new Point(0, 78));
      tinaPaletteBlueLbl.setPreferredSize(new Dimension(68, 26));
      tinaPaletteGreenLbl = new JLabel();
      tinaPaletteGreenLbl.setText("Green");
      tinaPaletteGreenLbl.setSize(new Dimension(68, 26));
      tinaPaletteGreenLbl.setLocation(new Point(0, 52));
      tinaPaletteGreenLbl.setPreferredSize(new Dimension(68, 26));
      tinaPaletteRedLbl = new JLabel();
      tinaPaletteRedLbl.setText("Red");
      tinaPaletteRedLbl.setSize(new Dimension(68, 26));
      tinaPaletteRedLbl.setLocation(new Point(0, 26));
      tinaPaletteRedLbl.setPreferredSize(new Dimension(68, 26));
      tinaPaletteShiftLbl = new JLabel();
      tinaPaletteShiftLbl.setText("Shift");
      tinaPaletteShiftLbl.setSize(new Dimension(68, 26));
      tinaPaletteShiftLbl.setLocation(new Point(0, 0));
      tinaPaletteShiftLbl.setPreferredSize(new Dimension(68, 26));
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
      tinaPaletteShiftREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteShiftREd.setText("0");
      tinaPaletteShiftREd.setLocation(new Point(68, 0));
      tinaPaletteShiftREd.setSize(new Dimension(36, 26));
      tinaPaletteShiftREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteRedREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteRedREd.setText("0");
      tinaPaletteRedREd.setSize(new Dimension(36, 26));
      tinaPaletteRedREd.setLocation(new Point(68, 26));
      tinaPaletteRedREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteGreenREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteGreenREd.setText("0");
      tinaPaletteGreenREd.setSize(new Dimension(36, 26));
      tinaPaletteGreenREd.setLocation(new Point(68, 52));
      tinaPaletteGreenREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteBlueREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteBlueREd.setText("0");
      tinaPaletteBlueREd.setSize(new Dimension(36, 26));
      tinaPaletteBlueREd.setLocation(new Point(68, 78));
      tinaPaletteBlueREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteHueREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteHueREd.setText("0");
      tinaPaletteHueREd.setSize(new Dimension(36, 26));
      tinaPaletteHueREd.setLocation(new Point(68, 104));
      tinaPaletteHueREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteSaturationREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteSaturationREd.setText("0");
      tinaPaletteSaturationREd.setSize(new Dimension(36, 26));
      tinaPaletteSaturationREd.setLocation(new Point(68, 130));
      tinaPaletteSaturationREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteContrastREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteContrastREd.setText("0");
      tinaPaletteContrastREd.setSize(new Dimension(36, 26));
      tinaPaletteContrastREd.setLocation(new Point(68, 156));
      tinaPaletteContrastREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteGammaREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteGammaREd.setText("0");
      tinaPaletteGammaREd.setSize(new Dimension(36, 26));
      tinaPaletteGammaREd.setLocation(new Point(68, 182));
      tinaPaletteGammaREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteBrightnessREd.setPreferredSize(new Dimension(36, 26));
      tinaPaletteBrightnessREd.setText("0");
      tinaPaletteBrightnessREd.setSize(new Dimension(36, 26));
      tinaPaletteBrightnessREd.setLocation(new Point(68, 208));
      tinaPaletteBrightnessREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaPaletteShiftSlider.setLocation(new Point(104, 0));
      tinaPaletteShiftSlider.setSize(new Dimension(86, 26));
      tinaPaletteShiftSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaPaletteRedSlider.setSize(new Dimension(86, 26));
      tinaPaletteRedSlider.setLocation(new Point(104, 26));
      tinaPaletteRedSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaPaletteGreenSlider.setSize(new Dimension(86, 26));
      tinaPaletteGreenSlider.setLocation(new Point(104, 52));
      tinaPaletteGreenSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaPaletteBlueSlider.setSize(new Dimension(86, 26));
      tinaPaletteBlueSlider.setLocation(new Point(104, 78));
      tinaPaletteBlueSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaPaletteHueSlider.setSize(new Dimension(86, 26));
      tinaPaletteHueSlider.setLocation(new Point(104, 104));
      tinaPaletteHueSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaPaletteSaturationSlider.setSize(new Dimension(86, 26));
      tinaPaletteSaturationSlider.setLocation(new Point(104, 130));
      tinaPaletteSaturationSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaPaletteContrastSlider.setSize(new Dimension(86, 26));
      tinaPaletteContrastSlider.setLocation(new Point(104, 156));
      tinaPaletteContrastSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaPaletteGammaSlider.setSize(new Dimension(86, 26));
      tinaPaletteGammaSlider.setLocation(new Point(104, 182));
      tinaPaletteGammaSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaPaletteBrightnessSlider.setSize(new Dimension(86, 26));
      tinaPaletteBrightnessSlider.setLocation(new Point(104, 208));
      tinaPaletteBrightnessSlider.setPreferredSize(new Dimension(86, 26));
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
      tinaRenderWidthREd.setBounds(new Rectangle(671, 54, 55, 26));
      tinaRenderWidthREd.setPreferredSize(new Dimension(55, 26));
      tinaRenderWidthREd.setText("800");
      tinaRenderWidthREd.setFont(new Font("Dialog", Font.PLAIN, 12));
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
      tinaRenderHeightREd.setBounds(new Rectangle(726, 55, 55, 26));
      tinaRenderHeightREd.setPreferredSize(new Dimension(55, 26));
      tinaRenderHeightREd.setText("600");
      tinaRenderHeightREd.setFont(new Font("Dialog", Font.PLAIN, 12));
    }
    return tinaRenderHeightREd;
  }

  public TINAController createController(StandardErrorHandler pErrorHandler, Prefs pPrefs) {
    tinaController = new TINAController(pErrorHandler, pPrefs, getTinaCenterPanel(), getTinaCameraRollREd(), getTinaCameraRollSlider(), getTinaCameraPitchREd(),
        getTinaCameraPitchSlider(), getTinaCameraYawREd(), getTinaCameraYawSlider(), getTinaCameraPerspectiveREd(), getTinaCameraPerspectiveSlider(),
        getTinaPreviewQualityREd(), getTinaRenderQualityREd(), getTinaCameraCentreXREd(), getTinaCameraCentreXSlider(), getTinaCameraCentreYREd(),
        getTinaCameraCentreYSlider(), getTinaCameraZoomREd(), getTinaCameraZoomSlider(), getTinaPixelsPerUnitREd(), getTinaPixelsPerUnitSlider(),
        getTinaBrightnessREd(), getTinaBrightnessSlider(), getTinaContrastREd(), getTinaContrastSlider(), getTinaGammaREd(), getTinaGammaSlider(),
        getTinaVibrancyREd(), getTinaVibrancySlider(), getTinaFilterRadiusREd(), getTinaFilterRadiusSlider(), getTinaOversampleREd(),
        getTinaOversampleSlider(), getTinaGammaThresholdREd(), getTinaGammaThresholdSlider(), getTinaWhiteLevelREd(), getTinaWhiteLevelSlider(),
        getTinaBGColorRedREd(), getTinaBGColorRedSlider(), getTinaBGColorGreenREd(), getTinaBGColorGreenSlider(), getTinaBGColorBlueREd(),
        getTinaBGColorBlueSlider(), getTinaPaletteRandomPointsREd(), getTinaPaletteImgPanel(), getTinaPaletteShiftREd(), getTinaPaletteShiftSlider(),
        getTinaPaletteRedREd(), getTinaPaletteRedSlider(), getTinaPaletteGreenREd(), getTinaPaletteGreenSlider(), getTinaPaletteBlueREd(),
        getTinaPaletteBlueSlider(), getTinaPaletteHueREd(), getTinaPaletteHueSlider(), getTinaPaletteSaturationREd(), getTinaPaletteSaturationSlider(),
        getTinaPaletteContrastREd(), getTinaPaletteContrastSlider(), getTinaPaletteGammaREd(), getTinaPaletteGammaSlider(), getTinaPaletteBrightnessREd(),
        getTinaPaletteBrightnessSlider(), getTinaRenderWidthREd(), getTinaRenderHeightREd(), getTinaTransformationsTable(), getAffineC00REd(),
        getAffineC01REd(), getAffineC10REd(), getAffineC11REd(), getAffineC20REd(), getAffineC21REd(), getAffineRotateAmountREd(), getAffineScaleAmountREd(),
        getAffineMoveAmountREd(), getAffineRotateLeftButton(), getAffineRotateRightButton(), getAffineEnlargeButton(), getAffineShrinkButton(),
        getAffineMoveUpButton(), getAffineMoveLeftButton(), getAffineMoveRightButton(), getAffineMoveDownButton(), getTinaAddTransformationButton(),
        getTinaDuplicateTransformationButton(), getTinaDeleteTransformationButton(), getTinaAddFinalTransformationButton());
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
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      affineRotateLeftButton.setPreferredSize(new Dimension(55, 26));
      affineRotateLeftButton.setSize(new Dimension(55, 26));
      affineRotateLeftButton.setLocation(new Point(4, 56));
      affineRotateLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/turnLeft.gif")));
      affineRotateLeftButton.setText("");
      affineRotateLeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      affineRotateRightButton.setPreferredSize(new Dimension(55, 26));
      affineRotateRightButton.setLocation(new Point(4, 105));
      affineRotateRightButton.setSize(new Dimension(55, 26));
      affineRotateRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/turnRight.gif")));
      affineRotateRightButton.setText("");
      affineRotateRightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      affineEnlargeButton.setPreferredSize(new Dimension(55, 26));
      affineEnlargeButton.setLocation(new Point(66, 56));
      affineEnlargeButton.setSize(new Dimension(55, 26));
      affineEnlargeButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/enlarge.gif")));
      affineEnlargeButton.setText("");
      affineEnlargeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      affineShrinkButton.setPreferredSize(new Dimension(55, 26));
      affineShrinkButton.setLocation(new Point(66, 105));
      affineShrinkButton.setSize(new Dimension(55, 26));
      affineShrinkButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/shrink.gif")));
      affineShrinkButton.setText("");
      affineShrinkButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      trnsformationsEastPanel.setLayout(flowLayout);
      trnsformationsEastPanel.setPreferredSize(new Dimension(91, 0));
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
      transformationsSplitPane.setDividerLocation(146);
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
      affineScaleAmountREd.setText("0.1");
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
      affineMoveUpButton.setPreferredSize(new Dimension(55, 26));
      affineMoveUpButton.setLocation(new Point(182, 56));
      affineMoveUpButton.setSize(new Dimension(55, 26));
      affineMoveUpButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveUp.gif")));
      affineMoveUpButton.setText("");
      affineMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      affineMoveDownButton.setPreferredSize(new Dimension(55, 26));
      affineMoveDownButton.setLocation(new Point(182, 105));
      affineMoveDownButton.setSize(new Dimension(55, 26));
      affineMoveDownButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveDown.gif")));
      affineMoveDownButton.setText("");
      affineMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      affineMoveLeftButton.setPreferredSize(new Dimension(55, 26));
      affineMoveLeftButton.setLocation(new Point(126, 83));
      affineMoveLeftButton.setSize(new Dimension(55, 26));
      affineMoveLeftButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveLeft.gif")));
      affineMoveLeftButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineMoveLeftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
      affineMoveRightButton.setSize(new Dimension(55, 26));
      affineMoveRightButton.setPreferredSize(new Dimension(55, 26));
      affineMoveRightButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/moveRight.gif")));
      affineMoveRightButton.setFont(new Font("Dialog", Font.BOLD, 10));
      affineMoveRightButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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

} //  @jve:decl-index=0:visual-constraint="10,10"
