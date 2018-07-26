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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class MutaGenFrame extends JFrame {
  private TinaController tinaController;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;

  public MutaGenFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1000, 700);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT + 80, JWildfire.DEFAULT_WINDOW_TOP + 20));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("MutaGen");
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
      jContentPane.add(getPanel_16(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  private JWFNumberField swfAnimatorFramesPerSecondREd;
  private JWFNumberField swfAnimatorFrameREd;
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

  public JWFNumberField getSwfAnimatorFramesPerSecondREd() {
    return swfAnimatorFramesPerSecondREd;
  }

  public JWFNumberField getSwfAnimatorFrameREd() {
    return swfAnimatorFrameREd;
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

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }

}
