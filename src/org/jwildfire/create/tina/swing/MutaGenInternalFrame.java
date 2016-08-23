/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;

public class MutaGenInternalFrame extends JInternalFrame {
  private TinaController tinaController;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JWFNumberField swfAnimatorFramesREd = null;
  private JLabel animateFramesLbl = null;
  private JLabel animateGlobalScriptLbl = null;
  private JLabel animateXFormScriptLbl = null;
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
  private JPanel blurShadingPanel = null;

  public MutaGenInternalFrame() {
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
    this.setLocation(new Point(0, 0));
    this.setClosable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setIconifiable(true);
    this.setTitle("Fractal flames: MutaGen");
    this.setVisible(false);
    this.setResizable(true);
    this.setMaximizable(true);
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
      jContentPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      jContentPane.add(getRootTabbedPane(), BorderLayout.CENTER);
    }
    return jContentPane;
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
      swfAnimatorFramesREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    }
    return swfAnimatorFramesREd;
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
      batchResolutionProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      batchQualityProfileCmb = new JComboBox();
      panel_2.add(batchQualityProfileCmb);
      batchQualityProfileCmb.setMinimumSize(new Dimension(159, 24));
      batchQualityProfileCmb.setMaximumSize(new Dimension(159, 24));
      batchQualityProfileCmb.setPreferredSize(new Dimension(159, 24));
      batchQualityProfileCmb.setMaximumRowCount(32);
      batchQualityProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      batchRenderShowImageBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      rootTabbedPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      rootTabbedPane.setEnabled(true);
      rootTabbedPane.addTab("MutaGen ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/kdissert.png")), getPanel_16(), null);
      rootTabbedPane.addTab("Dancing Flames Movies ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/kipina.png")), getPanel_36(), null);
      rootTabbedPane.addTab("Batch Flame Renderer ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/images.png")), getBatchRenderPanel(), null);
      rootTabbedPane.addTab("3DMesh Generation ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/sports-soccer.png")), getPanel_88(), null);
    }
    return rootTabbedPane;
  }

  private JComboBox batchQualityProfileCmb;
  private JComboBox batchResolutionProfileCmb;
  private JWFNumberField swfAnimatorFramesPerSecondREd;
  private JWFNumberField swfAnimatorFrameREd;
  private ButtonGroup swfAnimatorFlamesButtonGroup;
  private JPanel panel_20;
  private JPanel panel_21;
  private JPanel panel_22;
  private JPanel panel_23;
  private JPanel panel_24;
  private JPanel panel_25;
  private JPanel panel_26;
  private JPanel panel_36;
  private JPanel dancingFlamesFlamePnl;
  private JButton dancingFlamesStopShowBtn;
  private JPanel dancingFlamesGraph1Pnl;
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
  private JPanel panel_49;
  private JComboBox dancingFlamesFlameCmb;
  private JCheckBox dancingFlamesDrawTrianglesCBx;
  private JCheckBox dancingFlamesDrawFFTCBx;
  private JCheckBox dancingFlamesDrawFPSCBx;
  private JTree dancingFlamesFlamePropertiesTree;
  private JPanel panel_42;
  private JPanel panel_40;
  private JPanel panel_44;
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
  private JButton dancingFlamesReplaceFlameFromEditorBtn;
  private JButton dancingFlamesRenameFlameBtn;
  private JButton dancingFlamesRenameMotionBtn;
  private JCheckBox dancingFlamesMutedCBx;
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
  private JWFNumberField swfAnimatorXFormScript6REd;
  private JWFNumberField swfAnimatorXFormScript7REd;
  private JWFNumberField swfAnimatorXFormScript8REd;
  private JWFNumberField swfAnimatorXFormScript9REd;
  private JWFNumberField swfAnimatorXFormScript10REd;
  private JWFNumberField swfAnimatorXFormScript11REd;
  private JWFNumberField swfAnimatorXFormScript12REd;
  private JWFNumberField swfAnimatorGlobalScript6REd;
  private JWFNumberField swfAnimatorGlobalScript7REd;
  private JWFNumberField swfAnimatorGlobalScript8REd;
  private JWFNumberField swfAnimatorGlobalScript9REd;
  private JWFNumberField swfAnimatorGlobalScript10REd;
  private JWFNumberField swfAnimatorGlobalScript11REd;
  private JWFNumberField swfAnimatorGlobalScript12REd;
  private JPanel panel_88;
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
  private JButton meshGenPreviewSunflowExportBtn;
  private JWFNumberField meshGenSliceThicknessModREd;
  private JPanel channelMixerRedGreenRootPanel;
  private JPanel channelMixerRedBlueRootPanel;
  private JPanel channelMixerGreenBlueRootPanel;
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
  private JCheckBox batchRenderOverrideCBx;
  private JButton batchRenderShowImageBtn;
  private JComboBox meshGenOutputTypeCmb;

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
      batchRenderAddFilesButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      batchRenderFilesMoveUpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      batchRenderFilesMoveDownButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      batchRenderFilesRemoveButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      batchRenderFilesRemoveAllButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      batchRenderStartButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      batchRenderStartButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          tinaController.getBatchRendererController().batchRenderStartButton_clicked();
        }
      });
    }
    return batchRenderStartButton;
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

  public JComboBox getBatchQualityProfileCmb() {
    return batchQualityProfileCmb;
  }

  public JComboBox getBatchResolutionProfileCmb() {
    return batchResolutionProfileCmb;
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

  public JWFNumberField getSwfAnimatorFrameREd() {
    return swfAnimatorFrameREd;
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
      lblGlobalSettings.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      batchRenderJobProgressLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      batchRenderTotalProgressLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesStopShowBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return dancingFlamesStopShowBtn;
  }

  public JPanel getRealtimeFlamePnl() {
    return dancingFlamesFlamePnl;
  }

  public JPanel getRealtimeGraph1Pnl() {
    return dancingFlamesGraph1Pnl;
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
      dancingFlamesAddFromEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesAddFromClipboardBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesAddFromDiscBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblRandomGenerator.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesMutedCBx.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
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
      lblBorderSize.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesFramesPerSecondIEd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    }
    return dancingFlamesFramesPerSecondIEd;
  }

  private JLabel getLabel_7() {
    if (label_7 == null) {
      label_7 = new JLabel();
      label_7.setText("Frames per second");
      label_7.setPreferredSize(new Dimension(110, 22));
      label_7.setHorizontalAlignment(SwingConstants.RIGHT);
      label_7.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesMorphFrameCountIEd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      lblMorphFrames.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesDoRecordCBx.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      dancingFlamesBorderSizeSlider.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return panel_41;
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
      dancingFlamesFlameCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      dancingFlamesFlameCmb.setAlignmentX(1.0f);

      JLabel lblFlame = new JLabel();
      lblFlame.setBounds(154, 58, 47, 22);
      panel_49.add(lblFlame);
      lblFlame.setText("Flame");
      lblFlame.setPreferredSize(new Dimension(120, 22));
      lblFlame.setHorizontalAlignment(SwingConstants.RIGHT);
      lblFlame.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesLoadSoundBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      dancingFlamesStartShowBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesLoadProjectBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesSaveProjectBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesRandomGenCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblCount.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      dancingFlamesRandomCountIEd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      dancingFlamesGenRandFlamesBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesDeleteFlameBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      dancingFlamesFlameToEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesAddMotionCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesAddMotionBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesDeleteMotionBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesRenameMotionBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesCreateMotionsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesCreateMotionsCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesClearMotionsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesLinkMotionBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      dancingFlamesUnlinkMotionBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return panel_50;
  }

  public JTable getDancingFlamesMotionLinksTable() {
    return dancingFlamesMotionLinksTable;
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
      mutaGenLoadFlameFromEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      mutaGenLoadFlameFromFileBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      mutaGenSaveFlameToEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      mutaGenSaveFlameToFileBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      mutaGenBackBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblTrendVertical.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      mutaGenHorizontalTrend1Cmb = new JComboBox();
      panel_9.add(mutaGenHorizontalTrend1Cmb);
      mutaGenHorizontalTrend1Cmb.setPreferredSize(new Dimension(125, 24));
      mutaGenHorizontalTrend1Cmb.setMinimumSize(new Dimension(125, 24));
      mutaGenHorizontalTrend1Cmb.setMaximumSize(new Dimension(30000, 24));
      mutaGenHorizontalTrend1Cmb.setMaximumRowCount(32);
      mutaGenHorizontalTrend1Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

  JPanel getMutaGen05Pnl() {
    if (mutaGen05Pnl == null) {
      mutaGen05Pnl = new JPanel();
      mutaGen05Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen05Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen05Pnl;
  }

  JPanel getMutaGen01Pnl() {
    if (mutaGen01Pnl == null) {
      mutaGen01Pnl = new JPanel();
      mutaGen01Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen01Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen01Pnl;
  }

  JPanel getMutaGen03Pnl() {
    if (mutaGen03Pnl == null) {
      mutaGen03Pnl = new JPanel();
      mutaGen03Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen03Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen03Pnl;
  }

  JPanel getMutaGen04Pnl() {
    if (mutaGen04Pnl == null) {
      mutaGen04Pnl = new JPanel();
      mutaGen04Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen04Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen04Pnl;
  }

  JPanel getMutaGen06Pnl() {
    if (mutaGen06Pnl == null) {
      mutaGen06Pnl = new JPanel();
      mutaGen06Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen06Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen06Pnl;
  }

  JPanel getMutaGen07Pnl() {
    if (mutaGen07Pnl == null) {
      mutaGen07Pnl = new JPanel();
      mutaGen07Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen07Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen07Pnl;
  }

  JPanel getMutaGen08Pnl() {
    if (mutaGen08Pnl == null) {
      mutaGen08Pnl = new JPanel();
      mutaGen08Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen08Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen08Pnl;
  }

  JPanel getMutaGen09Pnl() {
    if (mutaGen09Pnl == null) {
      mutaGen09Pnl = new JPanel();
      mutaGen09Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen09Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen09Pnl;
  }

  JPanel getMutaGen10Pnl() {
    if (mutaGen10Pnl == null) {
      mutaGen10Pnl = new JPanel();
      mutaGen10Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen10Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen10Pnl;
  }

  JPanel getMutaGen12Pnl() {
    if (mutaGen12Pnl == null) {
      mutaGen12Pnl = new JPanel();
      mutaGen12Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen12Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen12Pnl;
  }

  JPanel getMutaGen13Pnl() {
    if (mutaGen13Pnl == null) {
      mutaGen13Pnl = new JPanel();
      mutaGen13Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen13Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen13Pnl;
  }

  JPanel getMutaGen14Pnl() {
    if (mutaGen14Pnl == null) {
      mutaGen14Pnl = new JPanel();
      mutaGen14Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen14Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen14Pnl;
  }

  JPanel getMutaGen15Pnl() {
    if (mutaGen15Pnl == null) {
      mutaGen15Pnl = new JPanel();
      mutaGen15Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen15Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen15Pnl;
  }

  JPanel getMutaGen16Pnl() {
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

  JPanel getMutaGen17Pnl() {
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

  JPanel getMutaGen18Pnl() {
    if (mutaGen18Pnl == null) {
      mutaGen18Pnl = new JPanel();
      mutaGen18Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen18Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen18Pnl;
  }

  JPanel getMutaGen19Pnl() {
    if (mutaGen19Pnl == null) {
      mutaGen19Pnl = new JPanel();
      mutaGen19Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen19Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen19Pnl;
  }

  JPanel getMutaGen20Pnl() {
    if (mutaGen20Pnl == null) {
      mutaGen20Pnl = new JPanel();
      mutaGen20Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen20Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen20Pnl;
  }

  JPanel getMutaGen02Pnl() {
    if (mutaGen02Pnl == null) {
      mutaGen02Pnl = new JPanel();
      mutaGen02Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen02Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen02Pnl;
  }

  JPanel getMutaGen21Pnl() {
    if (mutaGen21Pnl == null) {
      mutaGen21Pnl = new JPanel();
      mutaGen21Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen21Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen21Pnl;
  }

  JPanel getMutaGen22Pnl() {
    if (mutaGen22Pnl == null) {
      mutaGen22Pnl = new JPanel();
      mutaGen22Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen22Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen22Pnl;
  }

  JPanel getMutaGen23Pnl() {
    if (mutaGen23Pnl == null) {
      mutaGen23Pnl = new JPanel();
      mutaGen23Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen23Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen23Pnl;
  }

  JPanel getMutaGen25Pnl() {
    if (mutaGen25Pnl == null) {
      mutaGen25Pnl = new JPanel();
      mutaGen25Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen25Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen25Pnl;
  }

  JPanel getMutaGen24Pnl() {
    if (mutaGen24Pnl == null) {
      mutaGen24Pnl = new JPanel();
      mutaGen24Pnl.setBorder(new LineBorder(new Color(0, 0, 0)));
      mutaGen24Pnl.setLayout(new BorderLayout(0, 0));
    }
    return mutaGen24Pnl;
  }

  JPanel getMutaGen11Pnl() {
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
      mutaGenHorizontalTrend2Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblH.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      mutaGenVerticalTrend1Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblV.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblVertical.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return lblVertical;
  }

  JComboBox getMutaGenVerticalTrend2Cmb() {
    if (mutaGenVerticalTrend2Cmb == null) {
      mutaGenVerticalTrend2Cmb = new JComboBox();
      mutaGenVerticalTrend2Cmb.setPreferredSize(new Dimension(125, 24));
      mutaGenVerticalTrend2Cmb.setMinimumSize(new Dimension(125, 24));
      mutaGenVerticalTrend2Cmb.setMaximumSize(new Dimension(30000, 24));
      mutaGenVerticalTrend2Cmb.setMaximumRowCount(32);
      mutaGenVerticalTrend2Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblAmount.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_58.add(getMutaGenAmountREd());
      panel_58.add(getMutaGenRefreshBtn());
    }
    return panel_58;
  }

  JButton getMutaGenForwardBtn() {
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
      mutaGenForwardBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return mutaGenForwardBtn;
  }

  JTextPane getMutaGenHintPane() {
    if (mutaGenHintPane == null) {
      mutaGenHintPane = new JTextPane();
      mutaGenHintPane.setBackground(SystemColor.menu);
      mutaGenHintPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 14));
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

  JWFNumberField getMutaGenAmountREd() {
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
      mutaGenAmountREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      mutaGenRefreshBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return mutaGenRefreshBtn;
  }

  public JButton getMutaGenSaveFlameToEditorBtn() {
    return mutaGenSaveFlameToEditorBtn;
  }

  public JButton getMutaGenSaveFlameToFileBtn() {
    return mutaGenSaveFlameToFileBtn;
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
      dancingFlamesReplaceFlameFromEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      dancingFlamesRenameFlameBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return dancingFlamesRenameFlameBtn;
  }

  public JButton getDancingFlamesRenameMotionBtn() {
    return dancingFlamesRenameMotionBtn;
  }

  public JCheckBox getDancingFlamesMutedCBx() {
    return dancingFlamesMutedCBx;
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
      meshGenFrontViewRenderBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
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
      meshGenPerspectiveViewRenderBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      meshGenPerspectiveViewRenderBtn.setBounds(6, 99, 42, 24);
      panel_11.add(meshGenPerspectiveViewRenderBtn);
    }
    return panel_88;
  }

  private JPanel getPanel_89() {
    if (panel_89 == null) {
      panel_89 = new JPanel();
      panel_89.setPreferredSize(new Dimension(10, 130));
      panel_89.setLayout(new BorderLayout(0, 0));
      panel_89.add(getPanel_90(), BorderLayout.WEST);

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new TitledBorder(null, "Voxelstack/Depthmap generation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
      lblRenderWidth.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblRenderWidth.setBounds(252, 26, 79, 22);
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
      meshGenRenderWidthREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenRenderWidthREd.setBounds(328, 24, 100, 24);
      panel_3.add(meshGenRenderWidthREd);

      JLabel lblRenderHeight = new JLabel();
      lblRenderHeight.setText("Render height");
      lblRenderHeight.setSize(new Dimension(68, 22));
      lblRenderHeight.setPreferredSize(new Dimension(94, 22));
      lblRenderHeight.setName("");
      lblRenderHeight.setLocation(new Point(4, 76));
      lblRenderHeight.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblRenderHeight.setBounds(252, 50, 79, 22);
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
      meshGenRenderHeightREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenRenderHeightREd.setBounds(328, 48, 100, 24);
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
      meshGenSliceCountREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenSliceCountREd.setBounds(148, 24, 100, 24);
      panel_3.add(meshGenSliceCountREd);

      JLabel lblNumberOfSlices = new JLabel();
      lblNumberOfSlices.setText("Total number of slices");
      lblNumberOfSlices.setSize(new Dimension(68, 22));
      lblNumberOfSlices.setPreferredSize(new Dimension(94, 22));
      lblNumberOfSlices.setName("");
      lblNumberOfSlices.setLocation(new Point(6, 76));
      lblNumberOfSlices.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblNumberOfSlices.setBounds(0, 26, 151, 22);
      panel_3.add(lblNumberOfSlices);

      JLabel lblSlicesPerPass = new JLabel();
      lblSlicesPerPass.setText("Slices per pass (for speedup)");
      lblSlicesPerPass.setSize(new Dimension(68, 22));
      lblSlicesPerPass.setPreferredSize(new Dimension(94, 22));
      lblSlicesPerPass.setName("");
      lblSlicesPerPass.setLocation(new Point(4, 76));
      lblSlicesPerPass.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSlicesPerPass.setBounds(0, 50, 151, 22);
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
      meshGenSlicesPerRenderREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenSlicesPerRenderREd.setBounds(148, 48, 100, 24);
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
      meshGenRenderQualityREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenRenderQualityREd.setBounds(508, 26, 100, 24);
      panel_3.add(meshGenRenderQualityREd);

      JLabel lblRenderQuality = new JLabel();
      lblRenderQuality.setText("Render quality");
      lblRenderQuality.setSize(new Dimension(68, 22));
      lblRenderQuality.setPreferredSize(new Dimension(94, 22));
      lblRenderQuality.setName("");
      lblRenderQuality.setLocation(new Point(4, 76));
      lblRenderQuality.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblRenderQuality.setBounds(428, 26, 79, 22);
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
      meshGenSliceThicknessModREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenSliceThicknessModREd.setBounds(708, 26, 100, 24);
      panel_3.add(meshGenSliceThicknessModREd);

      JLabel lblSliceThickness = new JLabel();
      lblSliceThickness.setToolTipText("Thickness modifier of the generated model");
      lblSliceThickness.setText("Thickness mod");
      lblSliceThickness.setSize(new Dimension(68, 22));
      lblSliceThickness.setPreferredSize(new Dimension(94, 22));
      lblSliceThickness.setName("");
      lblSliceThickness.setLocation(new Point(4, 76));
      lblSliceThickness.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSliceThickness.setBounds(611, 28, 100, 22);
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
      meshGenSliceThicknessSamplesREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenSliceThicknessSamplesREd.setBounds(708, 48, 100, 24);
      panel_3.add(meshGenSliceThicknessSamplesREd);

      JLabel lblThicknessSamples = new JLabel();
      lblThicknessSamples.setToolTipText("Number of samples for calculating thickness mod");
      lblThicknessSamples.setText("Thickness samples");
      lblThicknessSamples.setSize(new Dimension(68, 22));
      lblThicknessSamples.setPreferredSize(new Dimension(94, 22));
      lblThicknessSamples.setName("");
      lblThicknessSamples.setLocation(new Point(4, 76));
      lblThicknessSamples.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblThicknessSamples.setBounds(611, 50, 100, 22);
      panel_3.add(lblThicknessSamples);

      meshGenOutputTypeCmb = new JComboBox();
      meshGenOutputTypeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getMeshGenController() != null) {
            tinaController.getMeshGenController().outputTypeCmb_changed();
          }
        }
      });
      meshGenOutputTypeCmb.setToolTipText("Random-flame-generator");
      meshGenOutputTypeCmb.setPreferredSize(new Dimension(110, 24));
      meshGenOutputTypeCmb.setMinimumSize(new Dimension(110, 24));
      meshGenOutputTypeCmb.setMaximumSize(new Dimension(32767, 24));
      meshGenOutputTypeCmb.setMaximumRowCount(48);
      meshGenOutputTypeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      meshGenOutputTypeCmb.setBounds(149, 0, 279, 24);
      panel_3.add(meshGenOutputTypeCmb);

      JLabel lblOutputType = new JLabel();
      lblOutputType.setText("Output type");
      lblOutputType.setSize(new Dimension(68, 22));
      lblOutputType.setPreferredSize(new Dimension(94, 22));
      lblOutputType.setName("");
      lblOutputType.setLocation(new Point(6, 76));
      lblOutputType.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblOutputType.setBounds(0, 2, 151, 22);
      panel_3.add(lblOutputType);

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
      meshGenGenerateBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenFromEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      meshGenFromClipboardBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      meshGenLoadFlameBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      label_2.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label_2.setBounds(16, 46, 68, 22);
      panel_1.add(label_2);

      meshGenCentreYREd = new JWFNumberField();
      meshGenCentreYREd.setValueStep(0.05);
      meshGenCentreYREd.setText("");
      meshGenCentreYREd.setSize(new Dimension(100, 24));
      meshGenCentreYREd.setPreferredSize(new Dimension(100, 24));
      meshGenCentreYREd.setLocation(new Point(456, 28));
      meshGenCentreYREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      label_4.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label_4.setBounds(16, 70, 68, 22);
      panel_1.add(label_4);

      meshGenZoomREd = new JWFNumberField();
      meshGenZoomREd.setValueStep(0.01);
      meshGenZoomREd.setText("");
      meshGenZoomREd.setSize(new Dimension(100, 24));
      meshGenZoomREd.setPreferredSize(new Dimension(100, 24));
      meshGenZoomREd.setLocation(new Point(456, 52));
      meshGenZoomREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      lblSlice.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      meshGenCentreXREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

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
      lblStartz.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblStartz.setBounds(16, 32, 68, 22);
      panel_2.add(lblStartz);

      meshGenZMinREd = new JWFNumberField();
      meshGenZMinREd.setMouseSpeed(0.01);
      meshGenZMinREd.setValueStep(0.05);
      meshGenZMinREd.setText("");
      meshGenZMinREd.setSize(new Dimension(100, 24));
      meshGenZMinREd.setPreferredSize(new Dimension(100, 24));
      meshGenZMinREd.setLocation(new Point(456, 4));
      meshGenZMinREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      lblEndz.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblEndz.setBounds(16, 54, 68, 22);
      panel_2.add(lblEndz);

      meshGenZMaxREd = new JWFNumberField();
      meshGenZMaxREd.setMouseSpeed(0.01);
      meshGenZMaxREd.setValueStep(0.05);
      meshGenZMaxREd.setText("");
      meshGenZMaxREd.setSize(new Dimension(100, 24));
      meshGenZMaxREd.setPreferredSize(new Dimension(100, 24));
      meshGenZMaxREd.setLocation(new Point(456, 28));
      meshGenZMaxREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      meshGenTopViewRenderBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
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
      meshGenTopViewToEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
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

  public JPanel getPanel_1() {
    return panel_93;
  }

  public JPanel getPanel_2() {
    return panel_94;
  }

  private JTabbedPane getTabbedPane_2() {
    if (tabbedPane_2 == null) {
      tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane_2.addTab("Voxelstack/Depthmap rendering", null, getPanel_95(), null);
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
      lblImageDownsample.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenSequenceDownSampleREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenSequenceDownSampleREd.setBounds(113, 6, 100, 24);
      panel_3.add(meshGenSequenceDownSampleREd);

      JLabel lblSpatialFilterRadius_1 = new JLabel();
      lblSpatialFilterRadius_1.setText("Spatial filter radius");
      lblSpatialFilterRadius_1.setSize(new Dimension(68, 22));
      lblSpatialFilterRadius_1.setPreferredSize(new Dimension(94, 22));
      lblSpatialFilterRadius_1.setName("");
      lblSpatialFilterRadius_1.setLocation(new Point(4, 76));
      lblSpatialFilterRadius_1.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenSequenceFilterRadiusREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      meshGenSequenceThresholdREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenSequenceThresholdREd.setBounds(325, 6, 100, 24);
      panel_3.add(meshGenSequenceThresholdREd);

      JLabel lblBrightnessThreshold = new JLabel();
      lblBrightnessThreshold.setText("Brightness threshold");
      lblBrightnessThreshold.setSize(new Dimension(68, 22));
      lblBrightnessThreshold.setPreferredSize(new Dimension(94, 22));
      lblBrightnessThreshold.setName("");
      lblBrightnessThreshold.setLocation(new Point(6, 76));
      lblBrightnessThreshold.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblBrightnessThreshold.setBounds(218, 8, 108, 22);
      panel_3.add(lblBrightnessThreshold);

      JLabel lblPrefilter = new JLabel();
      lblPrefilter.setText("PreFilter 1");
      lblPrefilter.setSize(new Dimension(68, 22));
      lblPrefilter.setPreferredSize(new Dimension(94, 22));
      lblPrefilter.setName("");
      lblPrefilter.setLocation(new Point(6, 76));
      lblPrefilter.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblPrefilter.setBounds(428, 8, 59, 22);
      panel_3.add(lblPrefilter);

      meshGenPreFilter1Cmb = new JComboBox();
      meshGenPreFilter1Cmb.setToolTipText("Random-Symmetry-Geneator");
      meshGenPreFilter1Cmb.setPreferredSize(new Dimension(50, 24));
      meshGenPreFilter1Cmb.setMinimumSize(new Dimension(100, 24));
      meshGenPreFilter1Cmb.setMaximumSize(new Dimension(32767, 24));
      meshGenPreFilter1Cmb.setMaximumRowCount(32);
      meshGenPreFilter1Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      meshGenPreFilter1Cmb.setBounds(487, 6, 117, 24);
      panel_3.add(meshGenPreFilter1Cmb);

      meshGenPreFilter2Cmb = new JComboBox();
      meshGenPreFilter2Cmb.setToolTipText("Random-Symmetry-Geneator");
      meshGenPreFilter2Cmb.setPreferredSize(new Dimension(50, 24));
      meshGenPreFilter2Cmb.setMinimumSize(new Dimension(100, 24));
      meshGenPreFilter2Cmb.setMaximumSize(new Dimension(32767, 24));
      meshGenPreFilter2Cmb.setMaximumRowCount(32);
      meshGenPreFilter2Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      meshGenPreFilter2Cmb.setBounds(487, 30, 117, 24);
      panel_3.add(meshGenPreFilter2Cmb);

      JLabel lblPrefilter_1 = new JLabel();
      lblPrefilter_1.setText("PreFilter 2");
      lblPrefilter_1.setSize(new Dimension(68, 22));
      lblPrefilter_1.setPreferredSize(new Dimension(94, 22));
      lblPrefilter_1.setName("");
      lblPrefilter_1.setLocation(new Point(6, 76));
      lblPrefilter_1.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenImageStepREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenImageStepREd.setBounds(325, 30, 100, 24);
      panel_3.add(meshGenImageStepREd);

      JLabel lblImageStep = new JLabel();
      lblImageStep.setText("Image step");
      lblImageStep.setSize(new Dimension(68, 22));
      lblImageStep.setPreferredSize(new Dimension(94, 22));
      lblImageStep.setName("");
      lblImageStep.setLocation(new Point(6, 76));
      lblImageStep.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblImageStep.setBounds(218, 32, 108, 22);
      panel_3.add(lblImageStep);

      JPanel panel_8 = new JPanel();
      panel_8.getLayout();
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
      meshGenGenerateMeshBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenLoadSequenceBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenSequenceWidthREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenSequenceWidthREd.setBounds(282, 23, 100, 24);
      panel_99.add(meshGenSequenceWidthREd);

      JLabel lblWidth = new JLabel();
      lblWidth.setText("Width");
      lblWidth.setSize(new Dimension(68, 22));
      lblWidth.setPreferredSize(new Dimension(94, 22));
      lblWidth.setName("");
      lblWidth.setLocation(new Point(4, 76));
      lblWidth.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblWidth.setBounds(206, 25, 79, 22);
      panel_99.add(lblWidth);

      JLabel lblHeight = new JLabel();
      lblHeight.setText("Height");
      lblHeight.setSize(new Dimension(68, 22));
      lblHeight.setPreferredSize(new Dimension(94, 22));
      lblHeight.setName("");
      lblHeight.setLocation(new Point(4, 76));
      lblHeight.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenSequenceHeightREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenSequenceHeightREd.setBounds(282, 47, 100, 24);
      panel_99.add(meshGenSequenceHeightREd);

      JLabel lblSlices = new JLabel();
      lblSlices.setText("Slices");
      lblSlices.setSize(new Dimension(68, 22));
      lblSlices.setPreferredSize(new Dimension(94, 22));
      lblSlices.setName("");
      lblSlices.setLocation(new Point(4, 76));
      lblSlices.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenSequenceSlicesREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      meshGenSequenceFromRendererBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      meshGenSequenceFromRendererBtn.setBounds(18, 23, 176, 24);
      panel_99.add(meshGenSequenceFromRendererBtn);

      meshGenSequenceLbl = new JLabel();
      meshGenSequenceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
      meshGenSequenceLbl.setText("(none)");
      meshGenSequenceLbl.setSize(new Dimension(68, 22));
      meshGenSequenceLbl.setPreferredSize(new Dimension(94, 22));
      meshGenSequenceLbl.setName("");
      meshGenSequenceLbl.setLocation(new Point(4, 76));
      meshGenSequenceLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      lblPositionY.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblPositionY.setBounds(6, 30, 78, 22);
      panel_101.add(lblPositionY);

      meshGenPreviewPositionYREd = new JWFNumberField();
      meshGenPreviewPositionYREd.setMouseSpeed(0.05);
      meshGenPreviewPositionYREd.setValueStep(0.1);
      meshGenPreviewPositionYREd.setText("");
      meshGenPreviewPositionYREd.setSize(new Dimension(100, 24));
      meshGenPreviewPositionYREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewPositionYREd.setLocation(new Point(71, 76));
      meshGenPreviewPositionYREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      meshGenPreviewPositionXREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      label_2.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenPreviewSizeREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      lblSize.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblSize.setBounds(6, 75, 78, 22);
      panel_101.add(lblSize);

      JLabel lblScaleZ = new JLabel();
      lblScaleZ.setText("Scale Z");
      lblScaleZ.setSize(new Dimension(68, 22));
      lblScaleZ.setPreferredSize(new Dimension(94, 22));
      lblScaleZ.setName("");
      lblScaleZ.setLocation(new Point(6, 76));
      lblScaleZ.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenPreviewScaleZREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      meshGenPreviewRotateAlphaREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      lblRotateAlpha.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblRotateAlpha.setBounds(6, 146, 78, 22);
      panel_101.add(lblRotateAlpha);

      JLabel lblRotateBeta = new JLabel();
      lblRotateBeta.setText("Rotate Beta");
      lblRotateBeta.setSize(new Dimension(68, 22));
      lblRotateBeta.setPreferredSize(new Dimension(94, 22));
      lblRotateBeta.setName("");
      lblRotateBeta.setLocation(new Point(6, 76));
      lblRotateBeta.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblRotateBeta.setBounds(6, 170, 78, 22);
      panel_101.add(lblRotateBeta);

      meshGenPreviewRotateBetaREd = new JWFNumberField();
      meshGenPreviewRotateBetaREd.setValueStep(1.0);
      meshGenPreviewRotateBetaREd.setText("");
      meshGenPreviewRotateBetaREd.setSize(new Dimension(100, 24));
      meshGenPreviewRotateBetaREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewRotateBetaREd.setLocation(new Point(71, 76));
      meshGenPreviewRotateBetaREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      meshGenRefreshPreviewBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenPreviewSunflowExportBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      meshGenPreviewImportLastGeneratedMeshBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      meshGenPreviewImportFromFileBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      meshGenClearPreviewBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      JLabel lblModelReduction = new JLabel();
      lblModelReduction.setBounds(4, 194, 78, 22);
      panel_102.add(lblModelReduction);
      lblModelReduction.setToolTipText("");
      lblModelReduction.setText("Points");
      lblModelReduction.setPreferredSize(new Dimension(94, 22));
      lblModelReduction.setName("");
      lblModelReduction.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

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
      meshGenPreviewPointsREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      meshGenPreviewPolygonsREd = new JWFNumberField();
      meshGenPreviewPolygonsREd.setValueStep(1.0);
      meshGenPreviewPolygonsREd.setText("");
      meshGenPreviewPolygonsREd.setPreferredSize(new Dimension(100, 24));
      meshGenPreviewPolygonsREd.setOnlyIntegers(true);
      meshGenPreviewPolygonsREd.setMinValue(1.0);
      meshGenPreviewPolygonsREd.setMaxValue(12.0);
      meshGenPreviewPolygonsREd.setHasMinValue(true);
      meshGenPreviewPolygonsREd.setHasMaxValue(true);
      meshGenPreviewPolygonsREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      meshGenPreviewPolygonsREd.setBounds(82, 216, 100, 24);
      panel_102.add(meshGenPreviewPolygonsREd);

      JLabel lblPolygons = new JLabel();
      lblPolygons.setToolTipText("");
      lblPolygons.setText("Polygons");
      lblPolygons.setPreferredSize(new Dimension(94, 22));
      lblPolygons.setName("");
      lblPolygons.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

  public JButton getMeshGenPreviewSunflowExportBtn() {
    return meshGenPreviewSunflowExportBtn;
  }

  public JWFNumberField getMeshGenThicknessREd() {
    return meshGenSliceThicknessModREd;
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

  private JPanel getChannelMixerGreenBlueRootPanel() {
    if (channelMixerGreenBlueRootPanel == null) {
      channelMixerGreenBlueRootPanel = new JPanel();
      channelMixerGreenBlueRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return channelMixerGreenBlueRootPanel;
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

  public JCheckBox getBatchRenderOverrideCBx() {
    return batchRenderOverrideCBx;
  }

  public JButton getBatchRenderShowImageBtn() {
    return batchRenderShowImageBtn;
  }

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }

}
