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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class FlamesGPURenderFrame extends JFrame {
  private TinaController tinaController; //  @jve:decl-index=0:
  private JPanel jContentPane = null; //  @jve:decl-index=0:visual-constraint="10,10"

  private JPanel interactiveRenderPanel = null;

  public FlamesGPURenderFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1086, 697);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT + 200, JWildfire.DEFAULT_WINDOW_TOP + 80));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("GPU renderer");
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
      jContentPane.add(getInteractiveRenderPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
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

  private JPanel interactiveNorthPanel;
  private JPanel interactiveWestPanel;
  private JPanel interactiveEastPanel;
  private JPanel interactiveCenterPanel;
  private JButton interactiveLoadFlameFromClipboardButton;
  private JButton interactiveLoadFlameButton;
  private JButton interactiveFlameToClipboardButton;
  private JButton interactiveSaveFlameButton;
  private JButton interactiveSaveImageButton;
  private JSplitPane interactiveCenterSplitPane;
  private JPanel interactiveCenterTopPanel;
  private JPanel interactiveCenterSouthPanel;
  private JScrollPane interactiveStatsScrollPane;
  private JTextArea interactiveStatsTextArea;
  private JToggleButton interactiveHalfSizeButton;
  private JComboBox interactiveResolutionProfileCmb;
  private JComboBox interactiveQualityProfileCmb;
  private JButton interactiveFlameToEditorButton;
  private JButton interactiveLoadFlameFromMainButton;
  private JLabel label_1;
  private JLabel label_2;
  private JPanel panel_27;
  private JPanel panel_28;
  private JPanel panel_29;
  private JPanel panel_36;
  private JPanel panel_37;
  private JPanel panel_32;
  private JPanel panel_33;
  private JPanel panel_34;
  private JPanel panel_35;
  private JPanel panel_110;
  private JToggleButton interactiveFullSizeButton;
  private JToggleButton interactiveQuarterSizeButton;
  private JPanel panel;
  private JLabel lblGpuRenderInfo;
  private JCheckBox aiPostDenoiserDisableCheckbox;
  private JPanel panel_1;
  private JTabbedPane tabbedPane;
  private JScrollPane scrollPane;
  private JTextArea flameParamsTextArea;
  private JCheckBox autoSyncCheckbox;
  private JButton renderImageButton;
  private JCheckBox autoRenderCBx;
  private JPanel progressIndicatorPnl;

  private JPanel getInteractiveNorthPanel() {
    if (interactiveNorthPanel == null) {
      interactiveNorthPanel = new JPanel();
      interactiveNorthPanel.setBorder(new EmptyBorder(5, 5, 0, 0));
      interactiveNorthPanel.setPreferredSize(new Dimension(0, 86));
      interactiveNorthPanel.setSize(new Dimension(0, 42));
      interactiveNorthPanel.setLayout(new BoxLayout(interactiveNorthPanel, BoxLayout.X_AXIS));
      interactiveNorthPanel.add(getPanel_27());
      interactiveNorthPanel.add(getPanel_1());
      interactiveNorthPanel.add(getPanel_28());
      interactiveNorthPanel.add(getPanel_32());
      interactiveNorthPanel.add(getProgressIndicatorPnl());
      interactiveNorthPanel.add(getPanel_33());
      interactiveNorthPanel.add(getPanel_34());
      interactiveNorthPanel.add(getPanel_35());
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

  JButton getInteractiveLoadFlameFromClipboardButton() {
    if (interactiveLoadFlameFromClipboardButton == null) {
      interactiveLoadFlameFromClipboardButton = new JButton();
      interactiveLoadFlameFromClipboardButton.setMinimumSize(new Dimension(100, 24));
      interactiveLoadFlameFromClipboardButton.setMaximumSize(new Dimension(32000, 24));
      interactiveLoadFlameFromClipboardButton.setToolTipText("Load flame from clipboard and render");
      interactiveLoadFlameFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().fromClipboardButton_clicked();
        }
      });
      interactiveLoadFlameFromClipboardButton.setText("From Clipboard");
      interactiveLoadFlameFromClipboardButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameFromClipboardButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveLoadFlameFromClipboardButton;
  }

  JButton getInteractiveLoadFlameButton() {
    if (interactiveLoadFlameButton == null) {
      interactiveLoadFlameButton = new JButton();
      interactiveLoadFlameButton.setMinimumSize(new Dimension(100, 24));
      interactiveLoadFlameButton.setMaximumSize(new Dimension(32000, 24));
      interactiveLoadFlameButton.setToolTipText("Load flame from file and render");
      interactiveLoadFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().loadFlameButton_clicked();
        }
      });
      interactiveLoadFlameButton.setText("Load Flame");
      interactiveLoadFlameButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveLoadFlameButton;
  }

  JButton getInteractiveFlameToClipboardButton() {
    if (interactiveFlameToClipboardButton == null) {
      interactiveFlameToClipboardButton = new JButton();
      interactiveFlameToClipboardButton.setMinimumSize(new Dimension(100, 24));
      interactiveFlameToClipboardButton.setMaximumSize(new Dimension(32000, 24));
      interactiveFlameToClipboardButton.setToolTipText("Copy the current fractal into the clipboard");
      interactiveFlameToClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().toClipboardButton_clicked();
        }
      });
      interactiveFlameToClipboardButton.setText("To Clipboard");
      interactiveFlameToClipboardButton.setPreferredSize(new Dimension(125, 24));
      interactiveFlameToClipboardButton.setMnemonic(KeyEvent.VK_D);
      interactiveFlameToClipboardButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveFlameToClipboardButton;
  }

  JButton getInteractiveSaveFlameButton() {
    if (interactiveSaveFlameButton == null) {
      interactiveSaveFlameButton = new JButton();
      interactiveSaveFlameButton.setMinimumSize(new Dimension(100, 24));
      interactiveSaveFlameButton.setMaximumSize(new Dimension(32000, 24));
      interactiveSaveFlameButton.setToolTipText("Save the current fractal");
      interactiveSaveFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().saveFlameButton_clicked();
        }
      });
      interactiveSaveFlameButton.setText("Save Flame");
      interactiveSaveFlameButton.setPreferredSize(new Dimension(125, 24));
      interactiveSaveFlameButton.setMnemonic(KeyEvent.VK_D);
      interactiveSaveFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveSaveFlameButton;
  }

  JButton getInteractiveSaveImageButton() {
    if (interactiveSaveImageButton == null) {
      interactiveSaveImageButton = new JButton();
      interactiveSaveImageButton.setMinimumSize(new Dimension(140, 24));
      interactiveSaveImageButton.setMaximumSize(new Dimension(160, 48));
      interactiveSaveImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().saveImageButton_clicked();
        }
      });
      interactiveSaveImageButton.setText("Save image");
      interactiveSaveImageButton.setPreferredSize(new Dimension(125, 48));
      interactiveSaveImageButton.setMnemonic(KeyEvent.VK_I);
      interactiveSaveImageButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveSaveImageButton;
  }

  private JSplitPane getInteractiveCenterSplitPane() {
    if (interactiveCenterSplitPane == null) {
      interactiveCenterSplitPane = new JSplitPane();
      interactiveCenterSplitPane.setMinimumSize(new Dimension(320, 30));
      interactiveCenterSplitPane.setPreferredSize(new Dimension(320, 30));
      interactiveCenterSplitPane.setDividerSize(6);
      interactiveCenterSplitPane.setLeftComponent(getInteractiveCenterSouthPanel());
      interactiveCenterSplitPane.setRightComponent(getInteractiveCenterTopPanel());
      interactiveCenterSplitPane.setDividerLocation(256);
    }
    return interactiveCenterSplitPane;
  }

  JPanel getInteractiveCenterTopPanel() {
    if (interactiveCenterTopPanel == null) {
      interactiveCenterTopPanel = new JPanel();
      interactiveCenterTopPanel.setBorder(new TitledBorder(null, "GPU render result  ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      interactiveCenterTopPanel.setLayout(new BorderLayout(0, 0));
    }
    return interactiveCenterTopPanel;
  }

  private JPanel getInteractiveCenterSouthPanel() {
    if (interactiveCenterSouthPanel == null) {
      interactiveCenterSouthPanel = new JPanel();
      interactiveCenterSouthPanel.setLayout(new BorderLayout(0, 0));
      interactiveCenterSouthPanel.add(getTabbedPane(), BorderLayout.CENTER);
    }
    return interactiveCenterSouthPanel;
  }

  private JScrollPane getInteractiveStatsScrollPane() {
    if (interactiveStatsScrollPane == null) {
      interactiveStatsScrollPane = new JScrollPane();
      interactiveStatsScrollPane.setViewportView(getInteractiveStatsTextArea());
      interactiveStatsScrollPane.setColumnHeaderView(getPanel_110());
    }
    return interactiveStatsScrollPane;
  }

  JTextArea getInteractiveStatsTextArea() {
    if (interactiveStatsTextArea == null) {
      interactiveStatsTextArea = new JTextArea();
      interactiveStatsTextArea.setEditable(false);
    }
    return interactiveStatsTextArea;
  }

  public JToggleButton getInteractiveHalveSizeButton() {
    return interactiveHalfSizeButton;
  }

  JComboBox getInteractiveResolutionProfileCmb() {
    if (interactiveResolutionProfileCmb == null) {
      interactiveResolutionProfileCmb = new JComboBox();
      interactiveResolutionProfileCmb.setMaximumSize(new Dimension(32767, 24));
      interactiveResolutionProfileCmb.setMinimumSize(new Dimension(100, 24));
      interactiveResolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED && tinaController != null && tinaController.getGpuRendererCtrl() != null) {
            tinaController.getGpuRendererCtrl().resolutionProfile_changed();
          }
        }
      });
      interactiveResolutionProfileCmb.setPreferredSize(new Dimension(125, 24));
      interactiveResolutionProfileCmb.setMaximumRowCount(32);
      interactiveResolutionProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveResolutionProfileCmb;
  }

  JComboBox getInteractiveQualityProfileCmb() {
    if (interactiveQualityProfileCmb == null) {
      interactiveQualityProfileCmb = new JComboBox();
      interactiveQualityProfileCmb.setMaximumSize(new Dimension(32767, 24));
      interactiveQualityProfileCmb.setMinimumSize(new Dimension(100, 24));
      interactiveQualityProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED && tinaController != null && tinaController.getGpuRendererCtrl() != null) {
            tinaController.getGpuRendererCtrl().qualityProfile_changed();
          }
        }
      });
      interactiveQualityProfileCmb.setPreferredSize(new Dimension(125, 24));
      interactiveQualityProfileCmb.setMaximumRowCount(32);
      interactiveQualityProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveQualityProfileCmb;
  }

  public JButton getInteractiveFlameToEditorButton() {
    return interactiveFlameToEditorButton;
  }

  public JButton getInteractiveLoadFlameFromMainButton() {
    return interactiveLoadFlameFromMainButton;
  }

  private JLabel getLabel_1() {
    if (label_1 == null) {
      label_1 = new JLabel();
      label_1.setMinimumSize(new Dimension(80, 22));
      label_1.setText("Resolution");
      label_1.setPreferredSize(new Dimension(62, 22));
      label_1.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return label_1;
  }

  private JLabel getLabel_2() {
    if (label_2 == null) {
      label_2 = new JLabel();
      label_2.setMinimumSize(new Dimension(80, 22));
      label_2.setText("Quality");
      label_2.setPreferredSize(new Dimension(62, 22));
      label_2.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return label_2;
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
          tinaController.getGpuRendererCtrl().fromEditorButton_clicked();
        }
      });
      interactiveLoadFlameFromMainButton.setToolTipText("Load flame from Editor and render");
      interactiveLoadFlameFromMainButton.setText("From Editor");
      interactiveLoadFlameFromMainButton.setPreferredSize(new Dimension(125, 24));
      interactiveLoadFlameFromMainButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_27.add(getInteractiveLoadFlameFromClipboardButton());
      panel_27.add(getInteractiveLoadFlameButton());
    }
    return panel_27;
  }

  private JPanel getPanel_28() {
    if (panel_28 == null) {
      panel_28 = new JPanel();
      panel_28.setPreferredSize(new Dimension(300, 10));
      panel_28.setBorder(new EmptyBorder(0, 6, 0, 6));
      panel_28.setMaximumSize(new Dimension(300, 32767));
      panel_28.setMinimumSize(new Dimension(300, 10));
      panel_28.setLayout(new BoxLayout(panel_28, BoxLayout.Y_AXIS));
      panel_28.add(getPanel_36());
      panel_28.add(getPanel_29());
      panel_28.add(getPanel_37());
    }
    return panel_28;
  }

  private JPanel getPanel_29() {
    if (panel_29 == null) {
      panel_29 = new JPanel();
      panel_29.setLayout(new BoxLayout(panel_29, BoxLayout.X_AXIS));
      panel_29.add(getLabel_1());
      panel_29.add(getInteractiveResolutionProfileCmb());
      panel_29.add(getAiPostDenoiserDisableCheckbox());
    }
    return panel_29;
  }

  private JPanel getPanel_37() {
    if (panel_37 == null) {
      panel_37 = new JPanel();
      panel_37.setLayout(new BoxLayout(panel_37, BoxLayout.X_AXIS));
      panel_37.add(getLabel_2());
      panel_37.add(getInteractiveQualityProfileCmb());
    }
    return panel_37;
  }

  private JPanel getPanel_36() {
    if (panel_36 == null) {
      panel_36 = new JPanel();
      panel_36.setLayout(new BoxLayout(panel_36, BoxLayout.X_AXIS));

      interactiveHalfSizeButton = new JToggleButton();
      panel_36.add(interactiveHalfSizeButton);
      interactiveHalfSizeButton.setMinimumSize(new Dimension(48, 24));
      interactiveHalfSizeButton.setMaximumSize(new Dimension(32000, 24));
      interactiveHalfSizeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().halveRenderSizeButton_clicked();
        }
      });
      interactiveHalfSizeButton.setToolTipText("Switch to half render resolution");
      interactiveHalfSizeButton.setText("1:2");
      interactiveHalfSizeButton.setPreferredSize(new Dimension(48, 24));
      interactiveHalfSizeButton.setMnemonic(KeyEvent.VK_M);
      interactiveHalfSizeButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_36.add(getInteractiveQuarterSizeButton());
      panel_36.add(getInteractiveFullSizeButton());
    }
    return panel_36;
  }

  JPanel getPanel_32() {
    if (panel_32 == null) {
      panel_32 = new JPanel();
      panel_32.setPreferredSize(new Dimension(140, 10));
      panel_32.setBorder(new EmptyBorder(0, 11, 9, 11));
      panel_32.setMinimumSize(new Dimension(140, 10));
      panel_32.setMaximumSize(new Dimension(140, 32767));
      panel_32.setLayout(null);

      autoRenderCBx = new JCheckBox("Auto Render");
      autoRenderCBx.setSelected(true);
      autoRenderCBx.setToolTipText("Automatic start of rendering");
      autoRenderCBx.setFont(null);
      autoRenderCBx.setBounds(8, 52, 120, 18);
      panel_32.add(autoRenderCBx);

      renderImageButton = new JButton();
      renderImageButton.setText("Render image");
      renderImageButton.setPreferredSize(new Dimension(125, 48));
      renderImageButton.setMnemonic(KeyEvent.VK_I);
      renderImageButton.setMinimumSize(new Dimension(100, 24));
      renderImageButton.setMaximumSize(new Dimension(160, 48));
      renderImageButton.setFont(null);
      renderImageButton.setBounds(6, 0, 133, 48);
      renderImageButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive4.png")));
      renderImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().renderFlameButtonClicked();
        }
      });

      panel_32.add(renderImageButton);
    }
    return panel_32;
  }

  private JPanel getPanel_34() {
    if (panel_34 == null) {
      panel_34 = new JPanel();
      panel_34.setPreferredSize(new Dimension(50, 10));
      panel_34.setBorder(new EmptyBorder(0, 11, 9, 11));
      panel_34.setMinimumSize(new Dimension(50, 10));
      panel_34.setMaximumSize(new Dimension(1000, 32767));
      panel_34.setLayout(new BoxLayout(panel_34, BoxLayout.Y_AXIS));
    }
    return panel_34;
  }

  private JPanel getPanel_33() {
    if (panel_33 == null) {
      panel_33 = new JPanel();
      panel_33.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel_33.setBorder(new EmptyBorder(0, 3, 0, 3));
      panel_33.setMaximumSize(new Dimension(150, 32767));
      panel_33.setLayout(new BoxLayout(panel_33, BoxLayout.Y_AXIS));
      panel_33.add(getPanel());
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
          tinaController.getGpuRendererCtrl().toEditorButton_clicked();
        }
      });
      interactiveFlameToEditorButton.setToolTipText("Copy the current fractal into the Editor");
      interactiveFlameToEditorButton.setText("To Editor");
      interactiveFlameToEditorButton.setPreferredSize(new Dimension(125, 24));
      interactiveFlameToEditorButton.setMnemonic(KeyEvent.VK_D);
      interactiveFlameToEditorButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_35.add(getInteractiveFlameToClipboardButton());
      panel_35.add(getInteractiveSaveFlameButton());
    }
    return panel_35;
  }

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }

  private JPanel getPanel_110() {
    if (panel_110 == null) {
      panel_110 = new JPanel();
      panel_110.setPreferredSize(new Dimension(10, 24));
      panel_110.setLayout(null);

      lblGpuRenderInfo = new JLabel();
      lblGpuRenderInfo.setText("GPU render info");
      lblGpuRenderInfo.setPreferredSize(new Dimension(62, 22));
      lblGpuRenderInfo.setMinimumSize(new Dimension(80, 22));
      lblGpuRenderInfo.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGpuRenderInfo.setBounds(6, 4, 181, 14);
      panel_110.add(lblGpuRenderInfo);
    }
    return panel_110;
  }

  public JToggleButton getInteractiveFullSizeButton() {
    if (interactiveFullSizeButton == null) {
      interactiveFullSizeButton = new JToggleButton();
      interactiveFullSizeButton.setSelected(true);
      interactiveFullSizeButton.setToolTipText("Switch to full render resolution");
      interactiveFullSizeButton.setText("Full");
      interactiveFullSizeButton.setPreferredSize(new Dimension(48, 24));
      interactiveFullSizeButton.setMnemonic(KeyEvent.VK_M);
      interactiveFullSizeButton.setMinimumSize(new Dimension(48, 24));
      interactiveFullSizeButton.setMaximumSize(new Dimension(32000, 24));
      interactiveFullSizeButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      interactiveFullSizeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().fullRenderSizeButton_clicked();
        }
      });
    }
    return interactiveFullSizeButton;
  }

  public JToggleButton getInteractiveQuarterSizeButton() {
    if (interactiveQuarterSizeButton == null) {
      interactiveQuarterSizeButton = new JToggleButton();
      interactiveQuarterSizeButton.setToolTipText("Switch to quarter render resolution");
      interactiveQuarterSizeButton.setText("1:4");
      interactiveQuarterSizeButton.setPreferredSize(new Dimension(48, 24));
      interactiveQuarterSizeButton.setMnemonic(KeyEvent.VK_M);
      interactiveQuarterSizeButton.setMinimumSize(new Dimension(48, 24));
      interactiveQuarterSizeButton.setMaximumSize(new Dimension(32000, 24));
      interactiveQuarterSizeButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      interactiveQuarterSizeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getGpuRendererCtrl().quarterRenderSizeButton_clicked();
        }
      });
    }
    return interactiveQuarterSizeButton;
  }

  private JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel();
      panel.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      panel.add(getInteractiveSaveImageButton());
    }
    return panel;
  }

  public JLabel getLblGpuRenderInfo() {
    return lblGpuRenderInfo;
  }

  protected JCheckBox getAiPostDenoiserDisableCheckbox() {
    if (aiPostDenoiserDisableCheckbox == null) {
      aiPostDenoiserDisableCheckbox = new JCheckBox("Denoiser OFF");
      aiPostDenoiserDisableCheckbox.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      aiPostDenoiserDisableCheckbox.setSelected(true);
      aiPostDenoiserDisableCheckbox.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (tinaController != null && tinaController.getGpuRendererCtrl() != null) {
                tinaController.getGpuRendererCtrl().changeRenderSizeButton_clicked();
              }
            }
          });
      aiPostDenoiserDisableCheckbox.setToolTipText("Disable AI-Post-Denoiser, in case it is enabled in the flame-parameters");
    }
    return aiPostDenoiserDisableCheckbox;
  }

  private JPanel getPanel_1() {
    if (panel_1 == null) {
      panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(70, 10));
      panel_1.setMaximumSize(new Dimension(70, 32767));
      panel_1.setMinimumSize(new Dimension(120, 10));
      panel_1.setLayout(null);

      autoSyncCheckbox = new JCheckBox("AutoSync");
      autoSyncCheckbox.setToolTipText("Automatic synchronization of the flame in the main editor with the GPU renderer");
      autoSyncCheckbox.setFont(null);
      autoSyncCheckbox.setBounds(0, 28, 82, 18);
      autoSyncCheckbox.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (tinaController != null && tinaController.getGpuRendererCtrl() != null) {
                tinaController.getGpuRendererCtrl().autoSyncCheckbox_clicked();
              }
            }
          });
      panel_1.add(autoSyncCheckbox);
    }
    return panel_1;
  }

  private JTabbedPane getTabbedPane() {
    if (tabbedPane == null) {
      tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane.addTab("GPU render info", null, getInteractiveStatsScrollPane(), null);
      tabbedPane.addTab("GPU flame params", null, getScrollPane(), null);
    }
    return tabbedPane;
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getFlameParamsTextArea());
    }
    return scrollPane;
  }

  JTextArea getFlameParamsTextArea() {
    if (flameParamsTextArea == null) {
      flameParamsTextArea = new JTextArea();
      flameParamsTextArea.setEditable(false);
    }
    return flameParamsTextArea;
  }

  public JCheckBox getAutoSyncCheckbox() {
    return autoSyncCheckbox;
  }

  public JButton getRenderImageButton() {
    return renderImageButton;
  }

  public JCheckBox getAutoRenderCBx() {
    return autoRenderCBx;
  }

  JPanel getProgressIndicatorPnl() {
    if (progressIndicatorPnl == null) {
      progressIndicatorPnl = new JPanel();
      progressIndicatorPnl.setLayout(new BorderLayout(0, 0));
    }
    return progressIndicatorPnl;
  }
}
