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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.swing.JWildfire;

public class InteractiveRendererFrame extends JFrame {
  private TinaController tinaController; //  @jve:decl-index=0:
  private JPanel jContentPane = null; //  @jve:decl-index=0:visual-constraint="10,10"

  private JPanel interactiveRenderPanel = null;

  public InteractiveRendererFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(917, 600);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT + 200, JWildfire.DEFAULT_WINDOW_TOP + 80));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Interactive renderer");
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
  private JComboBox interactiveResolutionProfileCmb;
  private JButton interactiveFlameToEditorButton;
  private JButton interactiveLoadFlameFromMainButton;
  private JLabel label_1;
  private JButton interactivePauseButton;
  private JButton interactiveResumeButton;
  private JPanel panel_27;
  private JPanel panel_28;
  private JPanel panel_29;
  private JPanel panel_36;
  private JPanel panel_30;
  private JPanel panel_31;
  private JPanel panel_32;
  private JPanel panel_33;
  private JPanel panel_35;
  private JPanel panel_17;
  private JPanel panel_18;
  private JPanel panel_110;
  private JToggleButton interactiveRendererShowStatsButton;
  private JToggleButton interactiveRendererShowPreviewButton;
  private JToggleButton interactiveFullSizeButton;
  private JToggleButton interactiveQuarterSizeButton;
  private JPanel panel;
  private JButton interactiveSaveZBufferButton;
  private JCheckBox interactiveAutoLoadImageCBx;

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

  JButton getInteractiveNextButton() {
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
      interactiveNextButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveNextButton;
  }

  JButton getInteractiveLoadFlameFromClipboardButton() {
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
          tinaController.getInteractiveRendererCtrl().loadFlameButton_clicked();
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
          tinaController.getInteractiveRendererCtrl().toClipboardButton_clicked();
        }
      });
      interactiveFlameToClipboardButton.setText("To Clipboard");
      interactiveFlameToClipboardButton.setPreferredSize(new Dimension(125, 24));
      interactiveFlameToClipboardButton.setMnemonic(KeyEvent.VK_D);
      interactiveFlameToClipboardButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveFlameToClipboardButton;
  }

  JButton getInteractiveStopButton() {
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
      interactiveStopButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveStopButton;
  }

  JButton getInteractiveSaveFlameButton() {
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
      interactiveSaveFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveSaveFlameButton;
  }

  JButton getInteractiveSaveImageButton() {
    if (interactiveSaveImageButton == null) {
      interactiveSaveImageButton = new JButton();
      interactiveSaveImageButton.setMinimumSize(new Dimension(100, 24));
      interactiveSaveImageButton.setMaximumSize(new Dimension(160, 24));
      interactiveSaveImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().saveImageButton_clicked();
        }
      });
      interactiveSaveImageButton.setText("Save image");
      interactiveSaveImageButton.setPreferredSize(new Dimension(125, 24));
      interactiveSaveImageButton.setMnemonic(KeyEvent.VK_I);
      interactiveSaveImageButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

  JPanel getInteractiveCenterTopPanel() {
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

  public JComboBox getInteractiveRandomStyleCmb() {
    return interactiveRandomStyleCmb;
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
          if (tinaController != null && tinaController.getInteractiveRendererCtrl() != null) {
            tinaController.getInteractiveRendererCtrl().resolutionProfile_changed();
          }
        }
      });
      interactiveResolutionProfileCmb.setPreferredSize(new Dimension(125, 24));
      interactiveResolutionProfileCmb.setMaximumRowCount(32);
      interactiveResolutionProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveResolutionProfileCmb;
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

  public JButton getInteractivePauseButton() {
    return interactivePauseButton;
  }

  public JButton getInteractiveResumeButton() {
    return interactiveResumeButton;
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
      interactiveLoadFlameFromMainButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      panel_28.add(getPanel_36());
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
          tinaController.getInteractiveRendererCtrl().halveRenderSizeButton_clicked();
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
      label.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      interactiveRandomStyleCmb = new JComboBox();
      panel_31.add(interactiveRandomStyleCmb);
      interactiveRandomStyleCmb.setMinimumSize(new Dimension(100, 24));
      interactiveRandomStyleCmb.setMaximumSize(new Dimension(32767, 24));
      interactiveRandomStyleCmb.setPreferredSize(new Dimension(125, 24));
      interactiveRandomStyleCmb.setMaximumRowCount(32);
      interactiveRandomStyleCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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
      panel_33.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel_33.setBorder(new EmptyBorder(0, 3, 0, 3));
      panel_33.setMaximumSize(new Dimension(150, 32767));
      panel_33.setLayout(new BoxLayout(panel_33, BoxLayout.Y_AXIS));

      interactivePauseButton = new JButton();
      panel_33.add(interactivePauseButton);
      interactivePauseButton.setMinimumSize(new Dimension(112, 24));
      interactivePauseButton.setMaximumSize(new Dimension(160, 24));
      interactivePauseButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().pauseBtn_clicked();
        }
      });
      interactivePauseButton.setToolTipText("Save the current state for later resuming");
      interactivePauseButton.setText("Save render state");
      interactivePauseButton.setPreferredSize(new Dimension(160, 24));
      interactivePauseButton.setMnemonic(KeyEvent.VK_T);
      interactivePauseButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_33.add(getPanel());
      panel_33.add(getInteractiveAutoLoadImageCBx());
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
      interactiveFlameToEditorButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_35.add(getInteractiveFlameToClipboardButton());
      panel_35.add(getInteractiveSaveFlameButton());
    }
    return panel_35;
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
      interactiveResumeButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
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

  private JPanel getPanel_110() {
    if (panel_110 == null) {
      panel_110 = new JPanel();
      panel_110.setPreferredSize(new Dimension(10, 32));
      panel_110.setLayout(null);

      interactiveRendererShowStatsButton = new JToggleButton();
      interactiveRendererShowStatsButton.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 12));
      interactiveRendererShowStatsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null) {
            tinaController.getInteractiveRendererCtrl().showStatsBtn_changed();
          }
        }
      });
      interactiveRendererShowStatsButton.setText("Stats");
      interactiveRendererShowStatsButton.setToolTipText("Show statistics (may slightly slow down rendering)");
      interactiveRendererShowStatsButton.setSelected(true);
      interactiveRendererShowStatsButton.setPreferredSize(new Dimension(42, 24));
      interactiveRendererShowStatsButton.setBounds(4, 4, 72, 24);
      panel_110.add(interactiveRendererShowStatsButton);

      interactiveRendererShowPreviewButton = new JToggleButton();
      interactiveRendererShowPreviewButton.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 12));
      interactiveRendererShowPreviewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController != null) {
            tinaController.getInteractiveRendererCtrl().showPreviewBtn_changed();
          }
        }
      });
      interactiveRendererShowPreviewButton.setToolTipText("Show previews (may slow down rendering)");
      interactiveRendererShowPreviewButton.setText("Preview");
      interactiveRendererShowPreviewButton.setSelected(true);
      interactiveRendererShowPreviewButton.setPreferredSize(new Dimension(42, 24));
      interactiveRendererShowPreviewButton.setBounds(81, 4, 72, 24);
      panel_110.add(interactiveRendererShowPreviewButton);
    }
    return panel_110;
  }

  public JToggleButton getInteractiveRendererShowStatsButton() {
    return interactiveRendererShowStatsButton;
  }

  public JToggleButton getInteractiveRendererShowPreviewButton() {
    return interactiveRendererShowPreviewButton;
  }

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
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
          tinaController.getInteractiveRendererCtrl().fullRenderSizeButton_clicked();
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
          tinaController.getInteractiveRendererCtrl().quarterRenderSizeButton_clicked();
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
      panel.add(getInteractiveSaveZBufferButton());
    }
    return panel;
  }

  JButton getInteractiveSaveZBufferButton() {
    if (interactiveSaveZBufferButton == null) {
      interactiveSaveZBufferButton = new JButton();
      interactiveSaveZBufferButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getInteractiveRendererCtrl().saveZBufferButton_clicked();
        }
      });
      interactiveSaveZBufferButton.setToolTipText("Save Z-Buffer");
      interactiveSaveZBufferButton.setText("Z");
      interactiveSaveZBufferButton.setPreferredSize(new Dimension(36, 24));
      interactiveSaveZBufferButton.setMnemonic(KeyEvent.VK_I);
      interactiveSaveZBufferButton.setMinimumSize(new Dimension(36, 24));
      interactiveSaveZBufferButton.setMaximumSize(new Dimension(36, 24));
      interactiveSaveZBufferButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return interactiveSaveZBufferButton;
  }

  JCheckBox getInteractiveAutoLoadImageCBx() {
    if (interactiveAutoLoadImageCBx == null) {
      interactiveAutoLoadImageCBx = new JCheckBox("Autoload saved image");
      interactiveAutoLoadImageCBx.setToolTipText("Automatically load a save image to immediately see the result");
    }
    return interactiveAutoLoadImageCBx;
  }
}
