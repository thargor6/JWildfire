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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class BatchFlameRendererFrame extends JFrame {
  private TinaController tinaController;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JPanel batchRenderPanel = null;
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

  public BatchFlameRendererFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(600, 620);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Batch flame renderer");
    this.setVisible(false);
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
      jContentPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      jContentPane.add(getBatchRenderPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
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
      panel_8.setMaximumSize(new Dimension(159, 56));
      panel_2.add(panel_8);
      panel_8.setLayout(null);

      batchRenderOverrideCBx = new JCheckBox("Overwrite images");
      batchRenderOverrideCBx.setBounds(4, 4, 149, 18);
      panel_8.add(batchRenderOverrideCBx);
      batchRenderOverrideCBx.setToolTipText("Overwrite already rendered images");

      enableOpenClBtn = new JToggleButton();
      enableOpenClBtn.setToolTipText("Use the external GPU-renderer for rendering, may not work for all flames, but is much faster");
      enableOpenClBtn.setText("GPU");
      enableOpenClBtn.setPreferredSize(new Dimension(72, 24));
      enableOpenClBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      enableOpenClBtn.setBounds(0, 24, 160, 24);
      enableOpenClBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/opencl.png")));
      panel_8.add(enableOpenClBtn);

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

  private JComboBox batchQualityProfileCmb;
  private JComboBox batchResolutionProfileCmb;
  private JWFNumberField swfAnimatorFramesPerSecondREd;
  private JWFNumberField swfAnimatorFrameREd;
  private JPanel panel_20;
  private JPanel panel_21;
  private JPanel panel_22;
  private JPanel panel_23;
  private JPanel panel_24;
  private JPanel panel_25;
  private JPanel panel_26;
  private JPanel batchPreviewRootPanel;
  private JCheckBox batchRenderOverrideCBx;
  private JButton batchRenderShowImageBtn;
  private JToggleButton enableOpenClBtn;

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
  JTable getRenderBatchJobsTable() {
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
  JButton getBatchRenderAddFilesButton() {
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
  JButton getBatchRenderFilesMoveUpButton() {
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
  JButton getBatchRenderFilesMoveDownButton() {
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
  JProgressBar getBatchRenderTotalProgressBar() {
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
  JButton getBatchRenderFilesRemoveButton() {
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
  JButton getBatchRenderFilesRemoveAllButton() {
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
  JButton getBatchRenderStartButton() {
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

  public JComboBox getBatchQualityProfileCmb() {
    return batchQualityProfileCmb;
  }

  public JComboBox getBatchResolutionProfileCmb() {
    return batchResolutionProfileCmb;
  }

  public JWFNumberField getSwfAnimatorFramesPerSecondREd() {
    return swfAnimatorFramesPerSecondREd;
  }

  public JWFNumberField getSwfAnimatorFrameREd() {
    return swfAnimatorFrameREd;
  }

  private JPanel getPanel_20() {
    if (panel_20 == null) {
      panel_20 = new JPanel();
      panel_20.setMaximumSize(new Dimension(159, 32));
    }
    return panel_20;
  }

  private JPanel getPanel_21() {
    if (panel_21 == null) {
      panel_21 = new JPanel();
      panel_21.setMaximumSize(new Dimension(159, 32));
    }
    return panel_21;
  }

  private JPanel getPanel_22() {
    if (panel_22 == null) {
      panel_22 = new JPanel();
      panel_22.setMaximumSize(new Dimension(32767, 32));
      panel_22.setLayout(null);

      JLabel lblGlobalSettings = new JLabel();
      lblGlobalSettings.setText("Global settings:");
      lblGlobalSettings.setPreferredSize(new Dimension(100, 22));
      lblGlobalSettings.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGlobalSettings.setAlignmentX(1.0f);
      lblGlobalSettings.setBounds(6, 16, 100, 14);
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

  JPanel getBatchPreviewRootPanel() {
    if (batchPreviewRootPanel == null) {
      batchPreviewRootPanel = new JPanel();
      batchPreviewRootPanel.setMaximumSize(new Dimension(32767, 120));
      batchPreviewRootPanel.setLayout(new BorderLayout(0, 0));
    }
    return batchPreviewRootPanel;
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

  public JToggleButton getEnableOpenClBtn() {
    return enableOpenClBtn;
  }
}
