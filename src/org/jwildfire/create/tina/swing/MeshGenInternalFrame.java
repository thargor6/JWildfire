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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

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
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.Desktop;

public class MeshGenInternalFrame extends JInternalFrame {
  private TinaController tinaController;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JTabbedPane rootTabbedPane = null;

  public MeshGenInternalFrame() {
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
    this.setLocation(new Point(Desktop.DEFAULT_WINDOW_LEFT, Desktop.DEFAULT_WINDOW_TOP));
    this.setClosable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setIconifiable(true);
    this.setTitle("3DMesh generation");
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
   * This method initializes rootTabbedPane	
   * 	
   * @return javax.swing.JTabbedPane	
   */
  private JTabbedPane getRootTabbedPane() {
    if (rootTabbedPane == null) {
      rootTabbedPane = new JTabbedPane();
      rootTabbedPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      rootTabbedPane.setEnabled(true);
      rootTabbedPane.addTab("3DMesh Generation ", new ImageIcon(TinaInternalFrame.class.getResource("/org/jwildfire/swing/icons/new/sports-soccer.png")), getPanel_88(), null);
    }
    return rootTabbedPane;
  }

  private JWFNumberField swfAnimatorFramesPerSecondREd;
  private JWFNumberField swfAnimatorFrameREd;
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
  private JButton channelMixerResetBtn;
  private JComboBox channelMixerModeCmb;
  private JWFNumberField meshGenSliceThicknessSamplesREd;
  private JComboBox meshGenPreFilter1Cmb;
  private JComboBox meshGenPreFilter2Cmb;
  private JWFNumberField meshGenImageStepREd;
  private JComboBox meshGenOutputTypeCmb;

  public JWFNumberField getSwfAnimatorFramesPerSecondREd() {
    return swfAnimatorFramesPerSecondREd;
  }

  public JWFNumberField getSwfAnimatorFrameREd() {
    return swfAnimatorFrameREd;
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

  JPanel getMeshGenTopViewRootPnl() {
    if (meshGenTopViewRootPnl == null) {
      meshGenTopViewRootPnl = new JPanel();
      meshGenTopViewRootPnl.setLayout(new BorderLayout(0, 0));
    }
    return meshGenTopViewRootPnl;
  }

  JPanel getMeshGenFrontViewRootPnl() {
    if (meshGenFrontViewRootPnl == null) {
      meshGenFrontViewRootPnl = new JPanel();
      meshGenFrontViewRootPnl.setLayout(new BorderLayout(0, 0));
    }
    return meshGenFrontViewRootPnl;
  }

  JPanel getMeshGenPerspectiveViewRootPnl() {
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

  JPanel getMeshGenPreviewRootPanel() {
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

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }

  public JComboBox getMeshGenOutputTypeCmb() {
    return meshGenOutputTypeCmb;
  }

}
