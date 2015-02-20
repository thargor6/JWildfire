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
package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jwildfire.base.Tools;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.EnvelopePanel;

public class ScriptInternalFrame extends JInternalFrame {
  private static final long serialVersionUID = 1L; // @jve:decl-index=0:
  private MainController mainController = null; // @jve:decl-index=0:
  private EnvelopeController envelopeController = null; // @jve:decl-index=0:
  private RenderController renderController = null; // @jve:decl-index=0:
  private OperatorsInternalFrame operatorsFrame = null;// @jve:decl-index=0:
  private Desktop desktop = null;// @jve:decl-index=0:

  private JButton renderScriptButton = null;

  private JScrollPane scriptTableScrollPane = null;

  private JTable scriptTable = null;

  private JScrollPane scriptActionScrollPane = null;

  private JTextArea scriptActionTextArea = null;

  private JPanel scriptActionRightPanel = null;

  private JButton scriptActionSaveButton = null;

  private JButton scriptActionRevertButton = null;

  private JButton syncActionButton = null;

  private JPanel scriptActionPanel = null;

  private JSplitPane scriptCenterSplitPane = null;

  private JButton replayScriptButton = null;

  private JButton saveScriptButton = null;

  private JButton loadScriptButton = null;

  private JButton clearScriptButton = null;

  private JButton renderFrameButton = null;

  private JPanel scriptTopPanel = null;

  private JPanel envelopeTopPanel = null;

  private JPanel envelopeRightPanel = null;

  private JPanel envelopeBottomPanel = null;

  private JLabel envelopeXLabel = null;

  private JTextField envelopeXREd = null;

  private JLabel envelopeYLabel = null;

  private JTextField envelopeYREd = null;

  private JLabel envelopeXMinLabel = null;

  private JLabel envelopeXMaxLabel = null;

  private JLabel envelopeYMinLabel = null;

  private JLabel envelopeYMaxLabel = null;

  private JTextField envelopeXMinREd = null;

  private JTextField envelopeXMaxREd = null;

  private JCheckBox envelopeLockCheckBox = null;

  private JTextField envelopeYMinREd = null;

  private JTextField envelopeYMaxREd = null;

  private JLabel envelopePropertyLabel = null;

  private JComboBox envelopePropertyCmb = null;

  private JComboBox envelopeInterpolationCmb = null;

  private JLabel envelopeInterpolationLabel = null;

  private JButton envelopeAddPointButton = null;

  private JButton envelopeRemovePointButton = null;

  private JButton envelopeClearButton = null;

  private JButton envelopeCreateButton = null;

  private JButton envelopeRemoveButton = null;

  private JButton envelopeViewAllButton = null;

  private JButton envelopeViewUpButton = null;

  private JButton envelopeViewDownButton = null;

  private JButton envelopeViewLeftButton = null;

  private JButton envelopeViewRightButton = null;

  private JPanel scriptContentPane = null;

  private JPanel scriptRootTopPanel = null;

  private JTabbedPane scriptRootTabbedPane = null;

  private JPanel scriptFramePanel = null;

  private JPanel scriptFramesPanel = null;

  private JPanel scriptFrameSliderPanel = null;

  private JSlider scriptFrameSlider = null;

  private JLabel scriptFrameLabel = null;

  private JTextField scriptFrameREd = null;

  private JLabel scriptFramesLabel = null;

  private JTextField scriptFramesREd = null;

  private JPanel scriptPanel = null;

  private JPanel envelopeRootPanel = null;

  /**
   * This is the xxx default constructor
   */
  public ScriptInternalFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setBounds(new Rectangle(385, 52, 792, 495));
    this.setTitle("Script");
    this.setResizable(true);
    this.setMaximizable(true);
    this.setIconifiable(true);
    this.setClosable(true);
    this.setVisible(false);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setSize(822, 584);
    this.setContentPane(getJContentPane());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (scriptContentPane == null) {
      scriptContentPane = new JPanel();
      scriptContentPane.setLayout(new BorderLayout());
      scriptContentPane.setBorder(BorderFactory.createEmptyBorder(10, 10,
          10, 10));
      scriptContentPane.add(getScriptRootTopPanel(), BorderLayout.NORTH);
      scriptContentPane.add(getScriptRootTabbedPane(),
          BorderLayout.CENTER);

      envelopeController = new EnvelopeController(
          getEnvelopePropertyCmb(), getEnvelopeCreateButton(),
          getEnvelopeRemoveButton(), getEnvelopeAddPointButton(),
          getEnvelopeRemovePointButton(), getEnvelopeClearButton(),
          getEnvelopeXMinREd(), getEnvelopeXMaxREd(),
          getEnvelopeYMinREd(), getEnvelopeYMaxREd(),
          getEnvelopeXREd(), getEnvelopeYREd(),
          getEnvelopeInterpolationCmb(), getEnvelopeViewAllButton(),
          getEnvelopeViewLeftButton(), getEnvelopeViewRightButton(),
          getEnvelopeViewUpButton(), getEnvelopeViewDownButton(),
          getEnvelopeLockCheckBox(),
          (EnvelopePanel) getEnvelopePanel(), null);
    }
    return scriptContentPane;
  }

  /**
   * This method initializes scriptRootTopPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getScriptRootTopPanel() {
    if (scriptRootTopPanel == null) {
      scriptRootTopPanel = new JPanel();
      scriptRootTopPanel.setLayout(new BorderLayout());
      scriptRootTopPanel.setPreferredSize(new Dimension(0, 36));
      scriptRootTopPanel.add(getScriptFramePanel(), BorderLayout.WEST);
      scriptRootTopPanel.add(getScriptFramesPanel(), BorderLayout.EAST);
      scriptRootTopPanel.add(getScriptFrameSliderPanel(),
          BorderLayout.CENTER);
    }
    return scriptRootTopPanel;
  }

  /**
   * This method initializes scriptRootTabbedPane
   * 
   * @return javax.swing.JTabbedPane
   */
  private JTabbedPane getScriptRootTabbedPane() {
    if (scriptRootTabbedPane == null) {
      scriptRootTabbedPane = new JTabbedPane();
      scriptRootTabbedPane.addTab("Script", null, getScriptPanel(), null);
      scriptRootTabbedPane.addTab("Envelopes", null,
          getEnvelopeRootPanel(), null);
    }
    return scriptRootTabbedPane;
  }

  /**
   * This method initializes scriptFramePanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getScriptFramePanel() {
    if (scriptFramePanel == null) {
      scriptFrameLabel = new JLabel();
      scriptFrameLabel.setText("Frame");
      scriptFrameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      scriptFrameLabel.setSize(new Dimension(43, 26));
      scriptFrameLabel.setLocation(new Point(5, 5));
      scriptFrameLabel.setPreferredSize(new Dimension(43, 26));
      scriptFramePanel = new JPanel();
      scriptFramePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5,
          0));
      scriptFramePanel.setLayout(null);
      scriptFramePanel.setPreferredSize(new Dimension(114, 0));
      scriptFramePanel.add(scriptFrameLabel, null);
      scriptFramePanel.add(getScriptFrameREd(), null);
    }
    return scriptFramePanel;
  }

  /**
   * This method initializes scriptFramesPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getScriptFramesPanel() {
    if (scriptFramesPanel == null) {
      scriptFramesLabel = new JLabel();
      scriptFramesLabel.setText("Frames");
      scriptFramesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      scriptFramesLabel.setSize(new Dimension(50, 26));
      scriptFramesLabel.setLocation(new Point(5, 5));
      scriptFramesLabel.setPreferredSize(new Dimension(50, 26));
      scriptFramesPanel = new JPanel();
      scriptFramesPanel.setLayout(null);
      scriptFramesPanel.setPreferredSize(new Dimension(122, 0));
      scriptFramesPanel.setBorder(BorderFactory.createEmptyBorder(5, 0,
          5, 0));
      scriptFramesPanel.add(scriptFramesLabel, null);
      scriptFramesPanel.add(getScriptFramesREd(), null);
    }
    return scriptFramesPanel;
  }

  /**
   * This method initializes scriptFrameSliderPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getScriptFrameSliderPanel() {
    if (scriptFrameSliderPanel == null) {
      scriptFrameSliderPanel = new JPanel();
      scriptFrameSliderPanel.setLayout(new BorderLayout());
      scriptFrameSliderPanel.setBorder(BorderFactory.createEmptyBorder(5,
          5, 5, 5));
      scriptFrameSliderPanel.add(getScriptFrameSlider(),
          BorderLayout.CENTER);
    }
    return scriptFrameSliderPanel;
  }

  /**
   * This method initializes scriptFrameSlider
   * 
   * @return javax.swing.JSlider
   */
  JSlider getScriptFrameSlider() {
    if (scriptFrameSlider == null) {
      scriptFrameSlider = new JSlider();
      scriptFrameSlider
          .addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
              mainController.scriptFrameChanged(
                  scriptFrameSlider.getValue(), null,
                  scriptFramesREd.getText());
            }
          });
    }
    return scriptFrameSlider;
  }

  /**
   * This method initializes scriptFrameREd
   * 
   * @return javax.swing.JTextField
   */
  JTextField getScriptFrameREd() {
    if (scriptFrameREd == null) {
      scriptFrameREd = new JTextField();
      scriptFrameREd.setText("1");
      scriptFrameREd.setLocation(new Point(50, 5));
      scriptFrameREd.setSize(new Dimension(60, 26));
      scriptFrameREd.setFont(new Font("Dialog", Font.PLAIN, 12));
      scriptFrameREd.setPreferredSize(new Dimension(60, 26));
      scriptFrameREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          mainController.scriptFrameChanged(-1,
              scriptFrameREd.getText(), scriptFramesREd.getText());
        }
      });
      scriptFrameREd
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              mainController.scriptFrameChanged(-1,
                  scriptFrameREd.getText(),
                  scriptFramesREd.getText());
            }
          });
    }
    return scriptFrameREd;
  }

  /**
   * This method initializes scriptFramesREd
   * 
   * @return javax.swing.JTextField
   */
  JTextField getScriptFramesREd() {
    if (scriptFramesREd == null) {
      scriptFramesREd = new JTextField();
      scriptFramesREd.setText("60");
      scriptFramesREd.setLocation(new Point(58, 5));
      scriptFramesREd.setSize(new Dimension(60, 26));
      scriptFramesREd.setFont(new Font("Dialog", Font.PLAIN, 12));
      scriptFramesREd.setPreferredSize(new Dimension(60, 26));
      scriptFramesREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          mainController.scriptFrameChanged(-1,
              scriptFrameREd.getText(), scriptFramesREd.getText());
        }
      });
      scriptFramesREd
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              mainController.scriptFrameChanged(-1,
                  scriptFrameREd.getText(),
                  scriptFramesREd.getText());
            }
          });
    }
    return scriptFramesREd;
  }

  /**
   * This method initializes scriptPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getScriptPanel() {
    if (scriptPanel == null) {
      scriptPanel = new JPanel();
      scriptPanel.setLayout(new BorderLayout());
      scriptPanel.add(getScriptTopPanel(), BorderLayout.NORTH);
      scriptPanel.add(getScriptCenterSplitPane(), BorderLayout.CENTER);
    }
    return scriptPanel;
  }

  /**
   * This method initializes envelopeRootPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getEnvelopeRootPanel() {
    if (envelopeRootPanel == null) {
      envelopeRootPanel = new JPanel();
      envelopeRootPanel.setLayout(new BorderLayout());
      envelopeRootPanel.add(getEnvelopeTopPanel(), BorderLayout.NORTH);
      envelopeRootPanel.add(getEnvelopeRightPanel(), BorderLayout.EAST);
      envelopeRootPanel.add(getEnvelopeBottomPanel(), BorderLayout.SOUTH);
    }
    return envelopeRootPanel;
  }

  /**
   * This method initializes envelopeTopPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getEnvelopeTopPanel() {
    if (envelopeTopPanel == null) {
      envelopePropertyLabel = new JLabel();
      envelopePropertyLabel.setPreferredSize(new Dimension(59, 26));
      envelopePropertyLabel.setLocation(new Point(5, 5));
      envelopePropertyLabel.setSize(new Dimension(59, 26));
      envelopePropertyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      envelopePropertyLabel.setText("Property");
      envelopeTopPanel = new JPanel();
      envelopeTopPanel.setLayout(null);
      envelopeTopPanel.setPreferredSize(new Dimension(0, 36));
      envelopeTopPanel.add(envelopePropertyLabel, null);
      envelopeTopPanel.add(getEnvelopePropertyCmb(), null);
      envelopeTopPanel.add(getEnvelopeCreateButton(), null);
      envelopeTopPanel.add(getEnvelopeRemoveButton(), null);
      envelopeTopPanel.add(getEnvelopeLockCheckBox(), null);
    }
    return envelopeTopPanel;
  }

  /**
   * This method initializes envelopeRightPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getEnvelopeRightPanel() {
    if (envelopeRightPanel == null) {
      envelopeRightPanel = new JPanel();
      envelopeRightPanel.setLayout(null);
      envelopeRightPanel.setPreferredSize(new Dimension(152, 0));
      envelopeRightPanel.add(getEnvelopeAddPointButton(), null);
      envelopeRightPanel.add(getEnvelopeRemovePointButton(), null);
      envelopeRightPanel.add(getEnvelopeClearButton(), null);
      envelopeRightPanel.add(getEnvelopeViewAllButton(), null);
      envelopeRightPanel.add(getEnvelopeViewUpButton(), null);
      envelopeRightPanel.add(getEnvelopeViewDownButton(), null);
      envelopeRightPanel.add(getEnvelopeViewLeftButton(), null);
      envelopeRightPanel.add(getEnvelopeViewRightButton(), null);
    }
    return envelopeRightPanel;
  }

  /**
   * This method initializes envelopeBottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getEnvelopeBottomPanel() {
    if (envelopeBottomPanel == null) {
      envelopeInterpolationLabel = new JLabel();
      envelopeInterpolationLabel.setPreferredSize(new Dimension(81, 26));
      envelopeInterpolationLabel.setLocation(new Point(416, 5));
      envelopeInterpolationLabel.setSize(new Dimension(81, 26));
      envelopeInterpolationLabel
          .setHorizontalAlignment(SwingConstants.RIGHT);
      envelopeInterpolationLabel.setText("Interpolation");
      envelopeYMaxLabel = new JLabel();
      envelopeYMaxLabel.setPreferredSize(new Dimension(38, 26));
      envelopeYMaxLabel.setLocation(new Point(128, 34));
      envelopeYMaxLabel.setSize(new Dimension(38, 26));
      envelopeYMaxLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      envelopeYMaxLabel.setText("YMax");
      envelopeYMinLabel = new JLabel();
      envelopeYMinLabel.setPreferredSize(new Dimension(38, 26));
      envelopeYMinLabel.setLocation(new Point(5, 34));
      envelopeYMinLabel.setSize(new Dimension(38, 26));
      envelopeYMinLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      envelopeYMinLabel.setText("YMin");
      envelopeXMaxLabel = new JLabel();
      envelopeXMaxLabel.setPreferredSize(new Dimension(38, 26));
      envelopeXMaxLabel.setLocation(new Point(128, 5));
      envelopeXMaxLabel.setSize(new Dimension(38, 26));
      envelopeXMaxLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      envelopeXMaxLabel.setText("XMax");
      envelopeXMinLabel = new JLabel();
      envelopeXMinLabel.setPreferredSize(new Dimension(38, 26));
      envelopeXMinLabel.setLocation(new Point(5, 5));
      envelopeXMinLabel.setSize(new Dimension(38, 26));
      envelopeXMinLabel
          .setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
      envelopeXMinLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      envelopeXMinLabel.setText("XMin");
      envelopeYLabel = new JLabel();
      envelopeYLabel.setText("Y");
      envelopeYLabel.setSize(new Dimension(38, 26));
      envelopeYLabel.setLocation(new Point(277, 34));
      envelopeYLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      envelopeYLabel.setPreferredSize(new Dimension(38, 26));
      envelopeXLabel = new JLabel();
      envelopeXLabel.setText("X");
      envelopeXLabel.setSize(new Dimension(38, 26));
      envelopeXLabel.setLocation(new Point(277, 5));
      envelopeXLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      envelopeXLabel.setPreferredSize(new Dimension(38, 26));
      envelopeBottomPanel = new JPanel();
      envelopeBottomPanel.setLayout(null);
      envelopeBottomPanel.setPreferredSize(new Dimension(0, 64));
      envelopeBottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0,
          0, 0));
      envelopeBottomPanel.add(envelopeXLabel, null);
      envelopeBottomPanel.add(getEnvelopeXREd(), null);
      envelopeBottomPanel.add(envelopeYLabel, null);
      envelopeBottomPanel.add(getEnvelopeYREd(), null);
      envelopeBottomPanel.add(envelopeXMinLabel, null);
      envelopeBottomPanel.add(envelopeXMaxLabel, null);
      envelopeBottomPanel.add(envelopeYMinLabel, null);
      envelopeBottomPanel.add(envelopeYMaxLabel, null);
      envelopeBottomPanel.add(getEnvelopeXMinREd(), null);
      envelopeBottomPanel.add(getEnvelopeXMaxREd(), null);
      envelopeBottomPanel.add(getEnvelopeYMinREd(), null);
      envelopeBottomPanel.add(getEnvelopeYMaxREd(), null);
      envelopeBottomPanel.add(getEnvelopeInterpolationCmb(), null);
      envelopeBottomPanel.add(envelopeInterpolationLabel, null);
    }
    return envelopeBottomPanel;
  }

  /**
   * This method initializes envelopeXREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getEnvelopeXREd() {
    if (envelopeXREd == null) {
      envelopeXREd = new JTextField();
      envelopeXREd.setPreferredSize(new Dimension(80, 26));
      envelopeXREd.setLocation(new Point(317, 5));
      envelopeXREd.setSize(new Dimension(80, 26));
      envelopeXREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          envelopeController.editFieldChanged();
        }
      });
      envelopeXREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          envelopeController.editFieldChanged();
        }
      });
    }
    return envelopeXREd;
  }

  /**
   * This method initializes envelopeYREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getEnvelopeYREd() {
    if (envelopeYREd == null) {
      envelopeYREd = new JTextField();
      envelopeYREd.setPreferredSize(new Dimension(80, 26));
      envelopeYREd.setLocation(new Point(317, 34));
      envelopeYREd.setSize(new Dimension(80, 26));
      envelopeYREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          envelopeController.editFieldChanged();
        }
      });
      envelopeYREd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          envelopeController.editFieldChanged();
        }
      });
    }
    return envelopeYREd;
  }

  /**
   * This method initializes envelopeXMinREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getEnvelopeXMinREd() {
    if (envelopeXMinREd == null) {
      envelopeXMinREd = new JTextField();
      envelopeXMinREd.setPreferredSize(new Dimension(80, 26));
      envelopeXMinREd.setSize(new Dimension(80, 26));
      envelopeXMinREd.setLocation(new Point(45, 5));
      envelopeXMinREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          envelopeController.editFieldChanged();
        }
      });
      envelopeXMinREd
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.editFieldChanged();
            }
          });
    }
    return envelopeXMinREd;
  }

  /**
   * This method initializes envelopeXMaxREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getEnvelopeXMaxREd() {
    if (envelopeXMaxREd == null) {
      envelopeXMaxREd = new JTextField();
      envelopeXMaxREd.setPreferredSize(new Dimension(80, 26));
      envelopeXMaxREd.setSize(new Dimension(80, 26));
      envelopeXMaxREd.setLocation(new Point(168, 5));
      envelopeXMaxREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          envelopeController.editFieldChanged();
        }
      });
      envelopeXMaxREd
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.editFieldChanged();
            }
          });
    }
    return envelopeXMaxREd;
  }

  /**
   * This method initializes envelopeYMinREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getEnvelopeYMinREd() {
    if (envelopeYMinREd == null) {
      envelopeYMinREd = new JTextField();
      envelopeYMinREd.setPreferredSize(new Dimension(80, 26));
      envelopeYMinREd.setSize(new Dimension(80, 26));
      envelopeYMinREd.setLocation(new Point(45, 34));
      envelopeYMinREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          envelopeController.editFieldChanged();
        }
      });
      envelopeYMinREd
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.editFieldChanged();
            }
          });
    }
    return envelopeYMinREd;
  }

  /**
   * This method initializes envelopeYMaxREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getEnvelopeYMaxREd() {
    if (envelopeYMaxREd == null) {
      envelopeYMaxREd = new JTextField();
      envelopeYMaxREd.setPreferredSize(new Dimension(80, 26));
      envelopeYMaxREd.setSize(new Dimension(80, 26));
      envelopeYMaxREd.setLocation(new Point(168, 34));
      envelopeYMaxREd.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent e) {
          envelopeController.editFieldChanged();
        }
      });
      envelopeYMaxREd
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.editFieldChanged();
            }
          });
    }
    return envelopeYMaxREd;
  }

  /**
   * This method initializes envelopePropertyCmb
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getEnvelopePropertyCmb() {
    if (envelopePropertyCmb == null) {
      envelopePropertyCmb = new JComboBox();
      envelopePropertyCmb.setLocation(new Point(66, 5));
      envelopePropertyCmb.setPreferredSize(new Dimension(141, 26));
      envelopePropertyCmb.setSize(new Dimension(141, 26));
      envelopePropertyCmb
          .addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent e) {
              envelopeController.propertyCmbChanged();
            }
          });
    }
    return envelopePropertyCmb;
  }

  /**
   * This method initializes envelopeInterpolationCmb
   * 
   * @return javax.swing.JComboBox
   */
  private JComboBox getEnvelopeInterpolationCmb() {
    if (envelopeInterpolationCmb == null) {
      envelopeInterpolationCmb = new JComboBox();
      envelopeInterpolationCmb.setSize(new Dimension(141, 26));
      envelopeInterpolationCmb.setPreferredSize(new Dimension(141, 26));
      envelopeInterpolationCmb.setLocation(new Point(499, 5));
      envelopeInterpolationCmb
          .addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent e) {
              envelopeController.interpolationCmbChanged();
            }
          });
    }
    return envelopeInterpolationCmb;
  }

  /**
   * This method initializes envelopeAddPointButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeAddPointButton() {
    if (envelopeAddPointButton == null) {
      envelopeAddPointButton = new JButton();
      envelopeAddPointButton.setPreferredSize(new Dimension(141, 26));
      envelopeAddPointButton.setLocation(new Point(5, 5));
      envelopeAddPointButton.setSize(new Dimension(141, 26));
      envelopeAddPointButton.setText("Add Point");
      envelopeAddPointButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.addPoint();
            }
          });
    }
    return envelopeAddPointButton;
  }

  /**
   * This method initializes envelopeRemovePointButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeRemovePointButton() {
    if (envelopeRemovePointButton == null) {
      envelopeRemovePointButton = new JButton();
      envelopeRemovePointButton.setText("Remove Point");
      envelopeRemovePointButton.setSize(new Dimension(141, 26));
      envelopeRemovePointButton.setPreferredSize(new Dimension(141, 26));
      envelopeRemovePointButton.setLocation(new Point(5, 37));
      envelopeRemovePointButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.removePoint();
            }
          });
    }
    return envelopeRemovePointButton;
  }

  /**
   * This method initializes envelopeClearButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeClearButton() {
    if (envelopeClearButton == null) {
      envelopeClearButton = new JButton();
      envelopeClearButton.setText("Clear");
      envelopeClearButton.setSize(new Dimension(141, 26));
      envelopeClearButton.setPreferredSize(new Dimension(141, 26));
      envelopeClearButton.setLocation(new Point(5, 240));
      envelopeClearButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.clearEnvelope();
            }
          });
    }
    return envelopeClearButton;
  }

  /**
   * This method initializes envelopeCreateButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeCreateButton() {
    if (envelopeCreateButton == null) {
      envelopeCreateButton = new JButton();
      envelopeCreateButton.setLocation(new Point(213, 5));
      envelopeCreateButton.setText("Create Envelope");
      envelopeCreateButton.setPreferredSize(new Dimension(141, 26));
      envelopeCreateButton.setSize(new Dimension(141, 26));
      envelopeCreateButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.createEnvelope();
            }
          });
    }
    return envelopeCreateButton;
  }

  /**
   * This method initializes envelopeRemoveButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeRemoveButton() {
    if (envelopeRemoveButton == null) {
      envelopeRemoveButton = new JButton();
      envelopeRemoveButton.setLocation(new Point(360, 5));
      envelopeRemoveButton.setText("Remove Envelope");
      envelopeRemoveButton.setPreferredSize(new Dimension(141, 26));
      envelopeRemoveButton.setSize(new Dimension(141, 26));
      envelopeRemoveButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.removeEnvelope();
            }
          });
    }
    return envelopeRemoveButton;
  }

  /**
   * This method initializes envelopeViewAllButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeViewAllButton() {
    if (envelopeViewAllButton == null) {
      envelopeViewAllButton = new JButton();
      envelopeViewAllButton.setText("View all");
      envelopeViewAllButton.setLocation(new Point(5, 190));
      envelopeViewAllButton.setSize(new Dimension(141, 26));
      envelopeViewAllButton.setPreferredSize(new Dimension(141, 26));
      envelopeViewAllButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.viewAll();
            }
          });
    }
    return envelopeViewAllButton;
  }

  /**
   * This method initializes envelopeViewUpButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeViewUpButton() {
    if (envelopeViewUpButton == null) {
      envelopeViewUpButton = new JButton();
      envelopeViewUpButton.setText("^");
      envelopeViewUpButton.setLocation(new Point(51, 99));
      envelopeViewUpButton.setSize(new Dimension(48, 26));
      envelopeViewUpButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewUpButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.viewUp();
            }
          });
    }
    return envelopeViewUpButton;
  }

  /**
   * This method initializes envelopeViewDownButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeViewDownButton() {
    if (envelopeViewDownButton == null) {
      envelopeViewDownButton = new JButton();
      envelopeViewDownButton.setText("v");
      envelopeViewDownButton.setLocation(new Point(51, 153));
      envelopeViewDownButton.setSize(new Dimension(48, 26));
      envelopeViewDownButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewDownButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.viewDown();
            }
          });
    }
    return envelopeViewDownButton;
  }

  /**
   * This method initializes envelopeViewLeftButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeViewLeftButton() {
    if (envelopeViewLeftButton == null) {
      envelopeViewLeftButton = new JButton();
      envelopeViewLeftButton.setText("<");
      envelopeViewLeftButton.setLocation(new Point(5, 126));
      envelopeViewLeftButton.setSize(new Dimension(48, 26));
      envelopeViewLeftButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewLeftButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.viewLeft();
            }
          });
    }
    return envelopeViewLeftButton;
  }

  /**
   * This method initializes envelopeViewRightButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getEnvelopeViewRightButton() {
    if (envelopeViewRightButton == null) {
      envelopeViewRightButton = new JButton();
      envelopeViewRightButton.setText(">");
      envelopeViewRightButton.setLocation(new Point(98, 126));
      envelopeViewRightButton.setSize(new Dimension(48, 26));
      envelopeViewRightButton.setPreferredSize(new Dimension(48, 26));
      envelopeViewRightButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              envelopeController.viewRight();
            }
          });
    }
    return envelopeViewRightButton;
  }

  /**
   * This method initializes envelopeLockCheckBox
   * 
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getEnvelopeLockCheckBox() {
    if (envelopeLockCheckBox == null) {
      envelopeLockCheckBox = new JCheckBox();
      envelopeLockCheckBox.setPreferredSize(new Dimension(21, 26));
      envelopeLockCheckBox.setSize(new Dimension(133, 26));
      envelopeLockCheckBox.setText("Locked");
      envelopeLockCheckBox.setLocation(new Point(506, 5));
      envelopeLockCheckBox
          .addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
              envelopeController.lockEnvelope();
            }
          });
    }
    return envelopeLockCheckBox;
  }

  /**
   * This method initializes scriptTopPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getScriptTopPanel() {
    if (scriptTopPanel == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.LEFT);
      scriptTopPanel = new JPanel();
      scriptTopPanel.setLayout(flowLayout);
      scriptTopPanel.setPreferredSize(new Dimension(0, 37));
      scriptTopPanel.add(getSaveScriptButton(), null);
      scriptTopPanel.add(getLoadScriptButton(), null);
      scriptTopPanel.add(getReplayScriptButton(), null);
      scriptTopPanel.add(getClearScriptButton(), null);
      scriptTopPanel.add(getRenderFrameButton(), null);
      scriptTopPanel.add(getRenderScriptButton(), null);
    }
    return scriptTopPanel;
  }

  /**
   * This method initializes saveScriptButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getSaveScriptButton() {
    if (saveScriptButton == null) {
      saveScriptButton = new JButton();
      saveScriptButton.setPreferredSize(new Dimension(120, 26));
      saveScriptButton.setHorizontalTextPosition(SwingConstants.CENTER);
      saveScriptButton.setText("Save Script...");
      saveScriptButton.setMnemonic(KeyEvent.VK_S);
      saveScriptButton.setHorizontalAlignment(SwingConstants.CENTER);
      saveScriptButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              saveScriptButton_actionPerformed(e);
            }
          });
    }
    return saveScriptButton;
  }

  /**
   * This method initializes loadScriptButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getLoadScriptButton() {
    if (loadScriptButton == null) {
      loadScriptButton = new JButton();
      loadScriptButton.setPreferredSize(new Dimension(120, 26));
      loadScriptButton.setMnemonic(KeyEvent.VK_L);
      loadScriptButton.setText("Load Script...");
      loadScriptButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              loadScriptButton_actionPerformed(e);
            }
          });
    }
    return loadScriptButton;
  }

  /**
   * This method initializes clearScriptButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getClearScriptButton() {
    if (clearScriptButton == null) {
      clearScriptButton = new JButton();
      clearScriptButton.setPreferredSize(new Dimension(120, 26));
      clearScriptButton.setText("Clear Script");
      clearScriptButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              clearScriptButton_actionPerformed(e);
            }
          });
    }
    return clearScriptButton;
  }

  /**
   * This method initializes renderFrameButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getRenderFrameButton() {
    if (renderFrameButton == null) {
      renderFrameButton = new JButton();
      renderFrameButton.setPreferredSize(new Dimension(120, 26));
      renderFrameButton.setMnemonic(KeyEvent.VK_F);
      renderFrameButton.setText("Render Frame");
      renderFrameButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              try {
                mainController.renderFrame();
              }
              catch (Throwable ex) {
                mainController.handleError(ex);
              }
              desktop.enableControls();
            }
          });
    }
    return renderFrameButton;
  }

  /**
   * This method initializes scriptCenterSplitPane
   * 
   * @return javax.swing.JSplitPane
   */
  private JSplitPane getScriptCenterSplitPane() {
    if (scriptCenterSplitPane == null) {
      scriptCenterSplitPane = new JSplitPane();
      scriptCenterSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
      scriptCenterSplitPane.setTopComponent(getScriptTableScrollPane());
      scriptCenterSplitPane.setBottomComponent(getScriptActionPanel());
      scriptCenterSplitPane.setDividerLocation(120);
    }
    return scriptCenterSplitPane;
  }

  /**
   * This method initializes replayScriptButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getReplayScriptButton() {
    if (replayScriptButton == null) {
      replayScriptButton = new JButton();
      replayScriptButton.setPreferredSize(new Dimension(120, 26));
      replayScriptButton.setActionCommand("Replay Script");
      replayScriptButton.setMnemonic(KeyEvent.VK_R);
      replayScriptButton.setText("Replay Script");
      replayScriptButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              try {
                mainController.replayScript();
              }
              catch (Exception ex) {
                mainController.handleError(ex);
              }
            }
          });
    }
    return replayScriptButton;
  }

  private void saveScriptButton_actionPerformed(java.awt.event.ActionEvent e) {
    try {
      mainController.saveScript();
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    desktop.enableControls();
  }

  private void loadScriptButton_actionPerformed(java.awt.event.ActionEvent e) {
    try {
      mainController.loadScript();
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    desktop.enableControls();
  }

  private void clearScriptButton_actionPerformed(java.awt.event.ActionEvent e) {
    if (mainController.clearScript())
      desktop.enableControls();
  }

  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  public void setDesktop(Desktop desktop) {
    this.desktop = desktop;
  }

  /**
   * This method initializes scriptActionPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getScriptActionPanel() {
    if (scriptActionPanel == null) {
      scriptActionPanel = new JPanel();
      scriptActionPanel.setLayout(new BorderLayout());
      scriptActionPanel.setPreferredSize(new Dimension(0, 100));
      scriptActionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10,
          10, 10));
      scriptActionPanel.add(getScriptActionScrollPane(),
          BorderLayout.CENTER);
      scriptActionPanel.add(getScriptActionRightPanel(),
          BorderLayout.EAST);
    }
    return scriptActionPanel;
  }

  /**
   * This method initializes scriptTableScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getScriptTableScrollPane() {
    if (scriptTableScrollPane == null) {
      scriptTableScrollPane = new JScrollPane();
      scriptTableScrollPane.setViewportView(getScriptTable());
    }
    return scriptTableScrollPane;
  }

  /**
   * This method initializes scriptTable
   * 
   * @return javax.swing.JTable
   */
  JTable getScriptTable() {
    if (scriptTable == null) {
      scriptTable = new JTable();
      scriptTable.setShowGrid(true);
      scriptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    return scriptTable;
  }

  /**
   * This method initializes scriptActionScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getScriptActionScrollPane() {
    if (scriptActionScrollPane == null) {
      scriptActionScrollPane = new JScrollPane();
      scriptActionScrollPane.setViewportView(getScriptActionTextArea());
    }
    return scriptActionScrollPane;
  }

  /**
   * This method initializes scriptActionTextArea
   * 
   * @return javax.swing.JTextArea
   */
  JTextArea getScriptActionTextArea() {
    if (scriptActionTextArea == null) {
      scriptActionTextArea = new JTextArea();
    }
    return scriptActionTextArea;
  }

  /**
   * This method initializes scriptActionRightPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getScriptActionRightPanel() {
    if (scriptActionRightPanel == null) {
      FlowLayout flowLayout1 = new FlowLayout();
      flowLayout1.setAlignment(FlowLayout.LEFT);
      scriptActionRightPanel = new JPanel();
      scriptActionRightPanel.setLayout(flowLayout1);
      scriptActionRightPanel.setPreferredSize(new Dimension(140, 0));
      scriptActionRightPanel.add(getScriptActionSaveButton(), null);
      scriptActionRightPanel.add(getScriptActionRevertButton(), null);
      scriptActionRightPanel.add(getSyncActionButton(), null);
    }
    return scriptActionRightPanel;
  }

  /**
   * This method initializes scriptActionSaveButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getScriptActionSaveButton() {
    if (scriptActionSaveButton == null) {
      scriptActionSaveButton = new JButton();
      scriptActionSaveButton.setPreferredSize(new Dimension(130, 26));
      scriptActionSaveButton.setMnemonic(KeyEvent.VK_H);
      scriptActionSaveButton.setText("Apply Changes");
      scriptActionSaveButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              try {
                mainController.saveScriptAction();
              }
              catch (Throwable ex) {
                mainController.handleError(ex);
              }
            }
          });
    }
    return scriptActionSaveButton;
  }

  /**
   * This method initializes scriptActionRevertButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getScriptActionRevertButton() {
    if (scriptActionRevertButton == null) {
      scriptActionRevertButton = new JButton();
      scriptActionRevertButton.setPreferredSize(new Dimension(130, 26));
      scriptActionRevertButton.setMnemonic(KeyEvent.VK_G);
      scriptActionRevertButton.setText("Revert Changes");
      scriptActionRevertButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              scriptActionRevertButton_actionPerformed(e);
            }
          });
    }
    return scriptActionRevertButton;
  }

  /**
   * This method initializes syncActionButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getSyncActionButton() {
    if (syncActionButton == null) {
      syncActionButton = new JButton();
      syncActionButton.setPreferredSize(new Dimension(130, 26));
      syncActionButton.setText("Sync Params");
      syncActionButton.setActionCommand("Sync Params");
      syncActionButton.setMnemonic(KeyEvent.VK_A);
      syncActionButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              try {
                mainController.syncActionAction();
                if (mainController.getTransformer() != null) {
                  operatorsFrame.getTransformersList().setSelectedValue(
                      mainController.getTransformer()
                          .getName(), true);
                  operatorsFrame.switchTransformerPropertiesPanel();
                }
                if (mainController.getCreator() != null) {
                  operatorsFrame.getCreatorsList().setSelectedValue(
                      mainController.getCreator()
                          .getName(), true);
                  operatorsFrame.switchCreatorPropertiesPanel();
                }
                if (mainController.getLoader() != null) {
                  operatorsFrame.getLoadersList().setSelectedValue(mainController
                      .getLoader().getName(), true);
                  operatorsFrame.switchLoaderPropertiesPanel();
                }
              }
              catch (Throwable ex) {
                mainController.handleError(ex);
              }
            }
          });
    }
    return syncActionButton;
  }

  /**
   * This method initializes renderScriptButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getRenderScriptButton() {
    if (renderScriptButton == null) {
      renderScriptButton = new JButton();
      renderScriptButton.setPreferredSize(new Dimension(120, 26));
      renderScriptButton.setMnemonic(KeyEvent.VK_P);
      renderScriptButton.setText("Render Script");
      renderScriptButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              renderController.showRenderDialog(1, Tools
                  .stringToInt(scriptFramesREd.getText()));
            }
          });
    }
    return renderScriptButton;
  }

  private void scriptActionRevertButton_actionPerformed(
      java.awt.event.ActionEvent e) {
    mainController.revertScriptAction();
  }

  // hide from Editor
  private Object envelopePanel; // @jve:decl-index=0:

  private Object getEnvelopePanel() {
    if (envelopePanel == null) {
      envelopePanel = new EnvelopePanel();
      ((JPanel) envelopePanel).setLayout(null);
    }
    return envelopePanel;
  }

  public void initApp() {
    envelopeRootPanel.add((JPanel) getEnvelopePanel(), BorderLayout.CENTER);
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.SPLINE);
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.BEZIER);
    envelopeInterpolationCmb.addItem(Envelope.Interpolation.LINEAR);
  }

  public void enableControls() {
    renderScriptButton.setEnabled(mainController.getActionList().size() > 0);
    envelopeController.enableControls();
  }

  public EnvelopeController getEnvelopeController() {
    return envelopeController;
  }

  public void setRenderController(RenderController renderController) {
    this.renderController = renderController;
  }

  public void setOperatorsFrame(OperatorsInternalFrame pOperatorsFrame) {
    operatorsFrame = pOperatorsFrame;
  }
} //  @jve:decl-index=0:visual-constraint="21,13"
