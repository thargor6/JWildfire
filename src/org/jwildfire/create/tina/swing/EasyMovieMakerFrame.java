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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class EasyMovieMakerFrame extends JFrame {
  private TinaController tinaController;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;

  private JPanel tinaSWFAnimatorPanel = null;
  private JButton swfAnimatorGenerateButton = null;
  private JWFNumberField swfAnimatorFramesREd = null;
  private JLabel animateFramesLbl = null;
  private JLabel animateGlobalScriptLbl = null;
  private JComboBox swfAnimatorGlobalScript1Cmb = null;
  private JLabel animateXFormScriptLbl = null;
  private JComboBox swfAnimatorXFormScript1Cmb = null;
  private JTabbedPane rootTabbedPane = null;
  private JPanel blurShadingPanel = null;

  public EasyMovieMakerFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1220, 600);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP + 80));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Easy movie maker");
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
      jContentPane.add(getTinaSWFAnimatorPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes tinaAnimatePanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTinaSWFAnimatorPanel() {
    if (tinaSWFAnimatorPanel == null) {
      tinaSWFAnimatorPanel = new JPanel();
      tinaSWFAnimatorPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(200, 10));
      panel_1.setMinimumSize(new Dimension(200, 10));
      tinaSWFAnimatorPanel.add(panel_1, BorderLayout.CENTER);
      panel_1.setLayout(new BorderLayout(0, 0));
      panel_1.add(getPanel_5(), BorderLayout.NORTH);

      JPanel panel_2 = new JPanel();
      panel_2.setBorder(null);
      panel_1.add(panel_2, BorderLayout.CENTER);
      panel_2.setLayout(new BorderLayout(0, 0));

      JPanel panel_8 = new JPanel();
      panel_2.add(panel_8, BorderLayout.NORTH);
      panel_8.setLayout(new BorderLayout(0, 0));

      JPanel panel_9 = new JPanel();
      panel_9.setBorder(new TitledBorder(null, "Flames", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_9.setPreferredSize(new Dimension(10, 260));
      panel_2.add(panel_9, BorderLayout.SOUTH);
      panel_9.setLayout(new BorderLayout(0, 0));

      JScrollPane swfAnimatorFlamesScrollPane = new JScrollPane();
      panel_9.add(swfAnimatorFlamesScrollPane, BorderLayout.CENTER);
      swfAnimatorFlamesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

      swfAnimatorFlamesPanel = new JPanel();
      swfAnimatorFlamesScrollPane.setViewportView(swfAnimatorFlamesPanel);
      swfAnimatorFlamesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

      JPanel panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(160, 10));
      panel_9.add(panel_3, BorderLayout.EAST);
      panel_3.add(getSwfAnimatorLoadFlameFromMainButton());
      panel_3.add(getSwfAnimatorLoadFlameFromClipboardButton());
      panel_3.add(getSwfAnimatorLoadFlameButton());
      panel_3.add(getSwfAnimatorMoveUpButton());
      panel_3.add(getSwfAnimatorMoveDownButton());
      panel_3.add(getSwfAnimatorRemoveFlameButton());
      panel_3.add(getSwfAnimatorRemoveAllFlamesButton());
      panel_3.add(getSwfAnimatorCompatCBx());

      swfAnimatorPanel_1 = new JPanel();
      swfAnimatorPanel_1.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_2.add(swfAnimatorPanel_1, BorderLayout.CENTER);
      swfAnimatorPanel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_83 = new JPanel();
      panel_83.setPreferredSize(new Dimension(10, 74));
      swfAnimatorPanel_1.add(panel_83, BorderLayout.SOUTH);
      panel_83.setLayout(new BorderLayout(0, 0));

      swfAnimatorFrameSlider = new JSlider();
      swfAnimatorFrameSlider.setBorder(new EmptyBorder(0, 4, 4, 0));
      panel_83.add(swfAnimatorFrameSlider, BorderLayout.CENTER);
      swfAnimatorFrameSlider.setMinorTickSpacing(5);
      swfAnimatorFrameSlider.setMinimum(1);
      swfAnimatorFrameSlider.setMajorTickSpacing(10);
      swfAnimatorFrameSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().previewFlameImage();
          }
        }
      });
      swfAnimatorFrameSlider.setValue(0);
      swfAnimatorFrameSlider.setPreferredSize(new Dimension(340, 19));
      swfAnimatorFrameSlider.setMaximum(60);
      panel_83.add(getPanel_84(), BorderLayout.SOUTH);
      panel_83.add(getPanel_12(), BorderLayout.NORTH);

      swfAnimatorPreviewRootPanel = new JPanel();
      swfAnimatorPanel_1.add(swfAnimatorPreviewRootPanel, BorderLayout.CENTER);
      swfAnimatorPreviewRootPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_10 = new JPanel();
      panel_10.setBorder(null);
      panel_10.setPreferredSize(new Dimension(400, 10));
      panel_2.add(panel_10, BorderLayout.EAST);
      panel_10.setLayout(new BoxLayout(panel_10, BoxLayout.Y_AXIS));

      JScrollPane scrollPane_2 = new JScrollPane();
      scrollPane_2.setBorder(new TitledBorder(null, "Global scripts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_10.add(scrollPane_2);

      JPanel panel_85 = new JPanel();
      panel_85.setPreferredSize(new Dimension(315, 280));
      scrollPane_2.setViewportView(panel_85);
      panel_85.setLayout(null);
      panel_85.add(getSwfAnimatorGlobalScript1Cmb());
      animateGlobalScriptLbl = new JLabel();
      animateGlobalScriptLbl.setBounds(0, 0, 21, 22);
      panel_85.add(animateGlobalScriptLbl);
      animateGlobalScriptLbl.setName("animateGlobalScriptLbl");
      animateGlobalScriptLbl.setPreferredSize(new Dimension(94, 22));
      animateGlobalScriptLbl.setText("01");
      animateGlobalScriptLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript1REd = new JWFNumberField();
      swfAnimatorGlobalScript1REd.setBounds(213, 0, 100, 24);
      panel_85.add(swfAnimatorGlobalScript1REd);
      swfAnimatorGlobalScript1REd.setValue(1.0);
      swfAnimatorGlobalScript1REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript1REd.setValueStep(0.1);
      swfAnimatorGlobalScript1REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript1REd.setMotionPropertyName("globalScript1");
      swfAnimatorGlobalScript1REd.setLinkedLabelControlName("animateGlobalScriptLbl");
      swfAnimatorGlobalScript1REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      JLabel lblGlobalScript = new JLabel();
      lblGlobalScript.setBounds(0, 23, 21, 22);
      panel_85.add(lblGlobalScript);
      lblGlobalScript.setName("lblGlobalScript");
      lblGlobalScript.setText("02");
      lblGlobalScript.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript2Cmb = new JComboBox();
      swfAnimatorGlobalScript2Cmb.setBounds(22, 23, 186, 24);
      panel_85.add(swfAnimatorGlobalScript2Cmb);
      swfAnimatorGlobalScript2Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript2Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript2Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript2REd = new JWFNumberField();
      swfAnimatorGlobalScript2REd.setBounds(213, 23, 100, 24);
      panel_85.add(swfAnimatorGlobalScript2REd);
      swfAnimatorGlobalScript2REd.setValue(1.0);
      swfAnimatorGlobalScript2REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript2REd.setValueStep(0.1);
      swfAnimatorGlobalScript2REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript2REd.setMotionPropertyName("globalScript2");
      swfAnimatorGlobalScript2REd.setLinkedLabelControlName("lblGlobalScript");
      swfAnimatorGlobalScript2REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      JLabel lblGlobalScript_1 = new JLabel();
      lblGlobalScript_1.setBounds(0, 46, 21, 22);
      panel_85.add(lblGlobalScript_1);
      lblGlobalScript_1.setName("lblGlobalScript_1");
      lblGlobalScript_1.setText("03");
      lblGlobalScript_1.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_1.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript3Cmb = new JComboBox();
      swfAnimatorGlobalScript3Cmb.setBounds(22, 46, 186, 24);
      panel_85.add(swfAnimatorGlobalScript3Cmb);
      swfAnimatorGlobalScript3Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript3Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript3Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript3REd = new JWFNumberField();
      swfAnimatorGlobalScript3REd.setBounds(213, 46, 100, 24);
      panel_85.add(swfAnimatorGlobalScript3REd);
      swfAnimatorGlobalScript3REd.setValue(1.0);
      swfAnimatorGlobalScript3REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript3REd.setValueStep(0.1);
      swfAnimatorGlobalScript3REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript3REd.setMotionPropertyName("globalScript3");
      swfAnimatorGlobalScript3REd.setLinkedLabelControlName("lblGlobalScript_1");
      swfAnimatorGlobalScript3REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      JLabel lblGlobalScript_2 = new JLabel();
      lblGlobalScript_2.setBounds(0, 69, 21, 22);
      panel_85.add(lblGlobalScript_2);
      lblGlobalScript_2.setName("lblGlobalScript_2");
      lblGlobalScript_2.setText("04");
      lblGlobalScript_2.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_2.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript4Cmb = new JComboBox();
      swfAnimatorGlobalScript4Cmb.setBounds(22, 69, 186, 24);
      panel_85.add(swfAnimatorGlobalScript4Cmb);
      swfAnimatorGlobalScript4Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript4Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript4Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript4REd = new JWFNumberField();
      swfAnimatorGlobalScript4REd.setBounds(213, 69, 100, 24);
      panel_85.add(swfAnimatorGlobalScript4REd);
      swfAnimatorGlobalScript4REd.setValue(1.0);
      swfAnimatorGlobalScript4REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript4REd.setValueStep(0.1);
      swfAnimatorGlobalScript4REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript4REd.setMotionPropertyName("globalScript4");
      swfAnimatorGlobalScript4REd.setLinkedLabelControlName("lblGlobalScript_2");
      swfAnimatorGlobalScript4REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      JLabel lblGlobalScript_3 = new JLabel();
      lblGlobalScript_3.setBounds(0, 92, 21, 22);
      panel_85.add(lblGlobalScript_3);
      lblGlobalScript_3.setName("lblGlobalScript_3");
      lblGlobalScript_3.setText("05");
      lblGlobalScript_3.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_3.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript5Cmb = new JComboBox();
      swfAnimatorGlobalScript5Cmb.setBounds(22, 92, 186, 24);
      panel_85.add(swfAnimatorGlobalScript5Cmb);
      swfAnimatorGlobalScript5Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript5Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript5Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorGlobalScript5REd = new JWFNumberField();
      swfAnimatorGlobalScript5REd.setBounds(213, 92, 100, 24);
      panel_85.add(swfAnimatorGlobalScript5REd);
      swfAnimatorGlobalScript5REd.setValue(1.0);
      swfAnimatorGlobalScript5REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript5REd.setValueStep(0.1);
      swfAnimatorGlobalScript5REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript5REd.setMotionPropertyName("globalScript5");
      swfAnimatorGlobalScript5REd.setLinkedLabelControlName("lblGlobalScript_3");
      swfAnimatorGlobalScript5REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      lblGlobalScript_4 = new JLabel();
      lblGlobalScript_4.setText("06");
      lblGlobalScript_4.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_4.setName("lblGlobalScript_4");
      lblGlobalScript_4.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGlobalScript_4.setBounds(0, 115, 21, 22);
      panel_85.add(lblGlobalScript_4);

      swfAnimatorGlobalScript6Cmb = new JComboBox();
      swfAnimatorGlobalScript6Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript6Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript6Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript6Cmb.setBounds(22, 115, 186, 24);
      swfAnimatorGlobalScript6Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript6Cmb);

      swfAnimatorGlobalScript6REd = new JWFNumberField();
      swfAnimatorGlobalScript6REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript6REd.setValueStep(0.1);
      swfAnimatorGlobalScript6REd.setValue(1.0);
      swfAnimatorGlobalScript6REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript6REd.setMotionPropertyName("globalScript6");
      swfAnimatorGlobalScript6REd.setLinkedLabelControlName("lblGlobalScript_4");
      swfAnimatorGlobalScript6REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript6REd.setBounds(213, 115, 100, 24);
      swfAnimatorGlobalScript6REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript6REd());
        }
      });
      swfAnimatorGlobalScript6REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript6REd);

      swfAnimatorGlobalScript7Cmb = new JComboBox();
      swfAnimatorGlobalScript7Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript7Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript7Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript7Cmb.setBounds(22, 138, 186, 24);
      swfAnimatorGlobalScript7Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript7Cmb);

      lblGlobalScript_5 = new JLabel();
      lblGlobalScript_5.setText("07");
      lblGlobalScript_5.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_5.setName("lblGlobalScript_5");
      lblGlobalScript_5.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGlobalScript_5.setBounds(0, 138, 21, 22);
      panel_85.add(lblGlobalScript_5);

      swfAnimatorGlobalScript7REd = new JWFNumberField();
      swfAnimatorGlobalScript7REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript7REd.setValueStep(0.1);
      swfAnimatorGlobalScript7REd.setValue(1.0);
      swfAnimatorGlobalScript7REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript7REd.setMotionPropertyName("globalScript7");
      swfAnimatorGlobalScript7REd.setLinkedLabelControlName("lblGlobalScript_5");
      swfAnimatorGlobalScript7REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript7REd.setBounds(213, 138, 100, 24);
      swfAnimatorGlobalScript7REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript7REd());
        }
      });
      swfAnimatorGlobalScript7REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript7REd);

      lblGlobalScript_6 = new JLabel();
      lblGlobalScript_6.setText("08");
      lblGlobalScript_6.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_6.setName("lblGlobalScript_6");
      lblGlobalScript_6.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGlobalScript_6.setBounds(0, 161, 21, 22);
      panel_85.add(lblGlobalScript_6);

      swfAnimatorGlobalScript8Cmb = new JComboBox();
      swfAnimatorGlobalScript8Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript8Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript8Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript8Cmb.setBounds(22, 161, 186, 24);
      swfAnimatorGlobalScript8Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript8Cmb);

      swfAnimatorGlobalScript8REd = new JWFNumberField();
      swfAnimatorGlobalScript8REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript8REd.setValueStep(0.1);
      swfAnimatorGlobalScript8REd.setValue(1.0);
      swfAnimatorGlobalScript8REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript8REd.setMotionPropertyName("globalScript8");
      swfAnimatorGlobalScript8REd.setLinkedLabelControlName("lblGlobalScript_6");
      swfAnimatorGlobalScript8REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript8REd.setBounds(213, 161, 100, 24);
      swfAnimatorGlobalScript8REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript8REd());
        }
      });
      swfAnimatorGlobalScript8REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript8REd);

      lblGlobalScript_7 = new JLabel();
      lblGlobalScript_7.setText("09");
      lblGlobalScript_7.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_7.setName("lblGlobalScript_7");
      lblGlobalScript_7.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGlobalScript_7.setBounds(0, 184, 21, 22);
      panel_85.add(lblGlobalScript_7);

      swfAnimatorGlobalScript9Cmb = new JComboBox();
      swfAnimatorGlobalScript9Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript9Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript9Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript9Cmb.setBounds(22, 184, 186, 24);
      swfAnimatorGlobalScript9Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript9Cmb);

      swfAnimatorGlobalScript9REd = new JWFNumberField();
      swfAnimatorGlobalScript9REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript9REd.setValueStep(1.0);
      swfAnimatorGlobalScript9REd.setValue(1.0);
      swfAnimatorGlobalScript9REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript9REd.setMotionPropertyName("globalScript9");
      swfAnimatorGlobalScript9REd.setLinkedLabelControlName("lblGlobalScript_7");
      swfAnimatorGlobalScript9REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript9REd.setBounds(213, 184, 100, 24);
      swfAnimatorGlobalScript9REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript9REd());
        }
      });
      swfAnimatorGlobalScript9REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript9REd);

      lblGlobalScript_8 = new JLabel();
      lblGlobalScript_8.setText("10");
      lblGlobalScript_8.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_8.setName("lblGlobalScript_8");
      lblGlobalScript_8.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGlobalScript_8.setBounds(0, 207, 21, 22);
      panel_85.add(lblGlobalScript_8);

      swfAnimatorGlobalScript10Cmb = new JComboBox();
      swfAnimatorGlobalScript10Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript10Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript10Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript10Cmb.setBounds(22, 207, 186, 24);
      swfAnimatorGlobalScript10Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript10Cmb);

      swfAnimatorGlobalScript10REd = new JWFNumberField();
      swfAnimatorGlobalScript10REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript10REd.setValueStep(0.1);
      swfAnimatorGlobalScript10REd.setValue(1.0);
      swfAnimatorGlobalScript10REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript10REd.setMotionPropertyName("globalScript10");
      swfAnimatorGlobalScript10REd.setLinkedLabelControlName("lblGlobalScript_8");
      swfAnimatorGlobalScript10REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript10REd.setBounds(213, 207, 100, 24);
      swfAnimatorGlobalScript10REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript10REd());
        }
      });
      swfAnimatorGlobalScript10REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript10REd);

      JLabel lblGlobalScript_9 = new JLabel();
      lblGlobalScript_9.setText("11");
      lblGlobalScript_9.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_9.setName("lblGlobalScript_9");
      lblGlobalScript_9.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGlobalScript_9.setBounds(0, 230, 21, 22);
      panel_85.add(lblGlobalScript_9);

      swfAnimatorGlobalScript11Cmb = new JComboBox();
      swfAnimatorGlobalScript11Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript11Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript11Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript11Cmb.setBounds(22, 230, 186, 24);
      swfAnimatorGlobalScript11Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript11Cmb);

      swfAnimatorGlobalScript11REd = new JWFNumberField();
      swfAnimatorGlobalScript11REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript11REd.setValueStep(0.1);
      swfAnimatorGlobalScript11REd.setValue(1.0);
      swfAnimatorGlobalScript11REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript11REd.setMotionPropertyName("globalScript11");
      swfAnimatorGlobalScript11REd.setLinkedLabelControlName("lblGlobalScript_9");
      swfAnimatorGlobalScript11REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript11REd.setBounds(213, 230, 100, 24);
      swfAnimatorGlobalScript11REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript11REd());
        }
      });
      swfAnimatorGlobalScript11REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript11REd);

      JLabel lblGlobalScript_10 = new JLabel();
      lblGlobalScript_10.setText("12");
      lblGlobalScript_10.setPreferredSize(new Dimension(94, 22));
      lblGlobalScript_10.setName("lblGlobalScript_10");
      lblGlobalScript_10.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblGlobalScript_10.setBounds(0, 253, 21, 22);
      panel_85.add(lblGlobalScript_10);

      swfAnimatorGlobalScript12Cmb = new JComboBox();
      swfAnimatorGlobalScript12Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript12Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript12Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript12Cmb.setBounds(22, 253, 186, 24);
      swfAnimatorGlobalScript12Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript12Cmb);

      swfAnimatorGlobalScript12REd = new JWFNumberField();
      swfAnimatorGlobalScript12REd.setWithMotionCurve(true);
      swfAnimatorGlobalScript12REd.setValueStep(0.1);
      swfAnimatorGlobalScript12REd.setValue(1.0);
      swfAnimatorGlobalScript12REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorGlobalScript12REd.setMotionPropertyName("globalScript12");
      swfAnimatorGlobalScript12REd.setLinkedLabelControlName("lblGlobalScript_10");
      swfAnimatorGlobalScript12REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorGlobalScript12REd.setBounds(213, 253, 100, 24);
      swfAnimatorGlobalScript12REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript12REd());
        }
      });
      swfAnimatorGlobalScript12REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_85.add(swfAnimatorGlobalScript12REd);
      panel_10.add(getScrollPane_9());
      panel_10.add(getPanel_87());
      swfAnimatorGlobalScript5REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript5REd());
        }
      });
      swfAnimatorGlobalScript5REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript5Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript4REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript4REd());
        }
      });
      swfAnimatorGlobalScript4REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript4Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript3REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript3REd());
        }
      });
      swfAnimatorGlobalScript3REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript3Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript2REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript2REd());
        }
      });
      swfAnimatorGlobalScript2REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript2Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorGlobalScript1REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editGlobalMotionCurve(getSwfAnimatorGlobalScript1REd());
        }
      });
      swfAnimatorGlobalScript1REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });

      JPanel panel_11 = new JPanel();
      panel_11.setBorder(null);
      panel_11.setPreferredSize(new Dimension(400, 10));
      panel_2.add(panel_11, BorderLayout.WEST);
      panel_11.setLayout(new BorderLayout(0, 0));
      panel_11.add(getPanel_14(), BorderLayout.CENTER);
    }
    return tinaSWFAnimatorPanel;
  }

  /**
   * This method initializes animationGenerateButton	
   * 	
   * @return javax.swing.JButton	
   */
  JButton getSwfAnimatorGenerateButton() {
    if (swfAnimatorGenerateButton == null) {
      swfAnimatorGenerateButton = new JButton();
      swfAnimatorGenerateButton.setBounds(993, 32, 177, 26);
      swfAnimatorGenerateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().generateButton_clicked();
        }
      });
      swfAnimatorGenerateButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorGenerateButton.setText("Generate sequence");
      swfAnimatorGenerateButton.setToolTipText("Generate a sequence; the type is defined by Output");
      swfAnimatorGenerateButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorGenerateButton;
  }

  /**
   * This method initializes animateFramesREd	
   * 	
   * @return javax.swing.JTextField	
   */
  JWFNumberField getSwfAnimatorFramesREd() {
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
   * This method initializes animateScriptCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  JComboBox getSwfAnimatorGlobalScript1Cmb() {
    if (swfAnimatorGlobalScript1Cmb == null) {
      swfAnimatorGlobalScript1Cmb = new JComboBox();
      swfAnimatorGlobalScript1Cmb.setBounds(22, 0, 186, 24);
      swfAnimatorGlobalScript1Cmb.setMaximumRowCount(16);
      swfAnimatorGlobalScript1Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorGlobalScript1Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorGlobalScript1Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
    }
    return swfAnimatorGlobalScript1Cmb;
  }

  /**
   * This method initializes animateXFormScriptCmb	
   * 	
   * @return javax.swing.JComboBox	
   */
  JComboBox getSwfAnimatorXFormScript1Cmb() {
    if (swfAnimatorXFormScript1Cmb == null) {
      swfAnimatorXFormScript1Cmb = new JComboBox();
      swfAnimatorXFormScript1Cmb.setBounds(22, 0, 186, 24);
      swfAnimatorXFormScript1Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript1Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript1Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript1Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
    }
    return swfAnimatorXFormScript1Cmb;
  }

  private JWFNumberField swfAnimatorFramesPerSecondREd;
  private JPanel panel_5;
  private JButton swfAnimatorLoadFlameFromMainButton;
  private JButton swfAnimatorLoadFlameFromClipboardButton;
  private JButton swfAnimatorLoadFlameButton;
  private JComboBox swfAnimatorResolutionProfileCmb;
  private JLabel label_3;
  private JProgressBar swfAnimatorProgressBar;
  private JButton swfAnimatorCancelButton;
  private JPanel swfAnimatorPanel_1;
  private JSlider swfAnimatorFrameSlider;
  private JWFNumberField swfAnimatorFrameREd;
  private JPanel swfAnimatorFlamesPanel;
  private ButtonGroup swfAnimatorFlamesButtonGroup;
  private JPanel panel_14;
  private JPanel swfAnimatorPreviewRootPanel;
  private JPanel panel_4;
  private JButton swfAnimatorRemoveFlameButton;
  private JButton swfAnimatorRemoveAllFlamesButton;
  private JButton swfAnimatorMoveUpButton;
  private JButton swfAnimatorMoveDownButton;
  private JButton swfAnimatorMovieFromClipboardButton;
  private JButton swfAnimatorMovieFromDiscButton;
  private JButton swfAnimatorMovieToClipboardButton;
  private JButton swfAnimatorMovieToDiscButton;
  private JButton swfAnimatorFrameToEditorBtn;
  private JButton swfAnimatorPlayButton;
  private JComboBox swfAnimatorGlobalScript2Cmb;
  private JComboBox swfAnimatorGlobalScript3Cmb;
  private JComboBox swfAnimatorGlobalScript4Cmb;
  private JComboBox swfAnimatorGlobalScript5Cmb;
  private JComboBox swfAnimatorXFormScript3Cmb;
  private JComboBox swfAnimatorXFormScript4Cmb;
  private JComboBox swfAnimatorXFormScript5Cmb;
  private JComboBox swfAnimatorXFormScript2Cmb;
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
  private JPanel randomMoviePanel;
  private JButton swfAnimatorGenRandomBatchBtn;
  private JComboBox swfAnimatorRandGenCmb;
  private JPanel panel_84;
  private JScrollPane scrollPane_9;
  private JPanel panel_86;
  private JPanel panel_87;
  private JComboBox swfAnimatorXFormScript6Cmb;
  private JComboBox swfAnimatorXFormScript7Cmb;
  private JComboBox swfAnimatorXFormScript8Cmb;
  private JComboBox swfAnimatorXFormScript9Cmb;
  private JComboBox swfAnimatorXFormScript10Cmb;
  private JComboBox swfAnimatorXFormScript11Cmb;
  private JComboBox swfAnimatorXFormScript12Cmb;
  private JWFNumberField swfAnimatorXFormScript6REd;
  private JWFNumberField swfAnimatorXFormScript7REd;
  private JWFNumberField swfAnimatorXFormScript8REd;
  private JWFNumberField swfAnimatorXFormScript9REd;
  private JWFNumberField swfAnimatorXFormScript10REd;
  private JWFNumberField swfAnimatorXFormScript11REd;
  private JWFNumberField swfAnimatorXFormScript12REd;
  private JLabel lblGlobalScript_4;
  private JLabel lblGlobalScript_5;
  private JLabel lblGlobalScript_6;
  private JLabel lblGlobalScript_7;
  private JLabel lblGlobalScript_8;
  private JComboBox swfAnimatorGlobalScript6Cmb;
  private JComboBox swfAnimatorGlobalScript7Cmb;
  private JComboBox swfAnimatorGlobalScript8Cmb;
  private JComboBox swfAnimatorGlobalScript9Cmb;
  private JComboBox swfAnimatorGlobalScript10Cmb;
  private JComboBox swfAnimatorGlobalScript11Cmb;
  private JComboBox swfAnimatorGlobalScript12Cmb;
  private JWFNumberField swfAnimatorGlobalScript6REd;
  private JWFNumberField swfAnimatorGlobalScript7REd;
  private JWFNumberField swfAnimatorGlobalScript8REd;
  private JWFNumberField swfAnimatorGlobalScript9REd;
  private JWFNumberField swfAnimatorGlobalScript10REd;
  private JWFNumberField swfAnimatorGlobalScript11REd;
  private JWFNumberField swfAnimatorGlobalScript12REd;
  private JButton btnRender;
  private JPanel panel_12;
  private JPanel panel_93;
  private JComboBox swfAnimatorQualityProfileCmb;
  private JComboBox swfAnimatorOutputTypeCmb;
  private JCheckBox swfAnimatorCompatCBx;

  public JWFNumberField getSwfAnimatorFramesPerSecondREd() {
    return swfAnimatorFramesPerSecondREd;
  }

  ButtonGroup getSwfAnimatorFlamesButtonGroup() {
    if (swfAnimatorFlamesButtonGroup == null) {
      swfAnimatorFlamesButtonGroup = new ButtonGroup();
    }
    return swfAnimatorFlamesButtonGroup;
  }

  private JPanel getPanel_5() {
    if (panel_5 == null) {
      panel_5 = new JPanel();
      panel_5.setBorder(null);
      panel_5.setPreferredSize(new Dimension(10, 62));
      panel_5.setLayout(null);

      swfAnimatorProgressBar = new JProgressBar();
      swfAnimatorProgressBar.setBounds(578, 35, 369, 21);
      panel_5.add(swfAnimatorProgressBar);
      swfAnimatorProgressBar.setValue(0);
      swfAnimatorProgressBar.setStringPainted(true);
      swfAnimatorProgressBar.setPreferredSize(new Dimension(568, 21));
      panel_5.add(getSwfAnimatorGenerateButton());

      swfAnimatorCancelButton = new JButton();
      swfAnimatorCancelButton.setBounds(993, 32, 177, 26);
      panel_5.add(swfAnimatorCancelButton);
      swfAnimatorCancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().cancelButton_clicked();
        }
      });
      swfAnimatorCancelButton.setText("Cancel");
      swfAnimatorCancelButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorCancelButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_5.add(getLabel_3());
      panel_5.add(getSwfAnimatorResolutionProfileCmb());
      panel_5.add(getSwfAnimatorMovieFromClipboardButton());
      panel_5.add(getSwfAnimatorMovieFromDiscButton());
      panel_5.add(getSwfAnimatorMovieToClipboardButton());
      panel_5.add(getSwfAnimatorMovieToDiscButton());

      swfAnimatorGenRandomBatchBtn = new JButton();
      swfAnimatorGenRandomBatchBtn.setBounds(6, 6, 119, 50);
      panel_5.add(swfAnimatorGenRandomBatchBtn);
      swfAnimatorGenRandomBatchBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (tinaController.getSwfAnimatorCtrl().createRandomBatch(-1, (String) swfAnimatorRandGenCmb.getSelectedItem())) {
            tinaController.getSwfAnimatorCtrl().importFromRandomBatch(0);
          }

        }
      });
      swfAnimatorGenRandomBatchBtn.setToolTipText("Create a batch of random movies");
      swfAnimatorGenRandomBatchBtn.setText("Random movies");
      swfAnimatorGenRandomBatchBtn.setPreferredSize(new Dimension(105, 46));
      swfAnimatorGenRandomBatchBtn.setMinimumSize(new Dimension(100, 46));
      swfAnimatorGenRandomBatchBtn.setMaximumSize(new Dimension(32000, 46));
      swfAnimatorGenRandomBatchBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      JLabel label = new JLabel();
      label.setBounds(132, 13, 80, 14);
      panel_5.add(label);
      label.setText("  Rnd Generator");
      label.setToolTipText("Select type of random movies to generate");
      label.setPreferredSize(new Dimension(80, 22));
      label.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      label.setAlignmentX(1.0f);

      swfAnimatorRandGenCmb = new JComboBox();
      swfAnimatorRandGenCmb.setBounds(132, 31, 160, 24);
      panel_5.add(swfAnimatorRandGenCmb);
      swfAnimatorRandGenCmb.setToolTipText("Random movie generator");
      swfAnimatorRandGenCmb.setPreferredSize(new Dimension(100, 24));
      swfAnimatorRandGenCmb.setMinimumSize(new Dimension(100, 24));
      swfAnimatorRandGenCmb.setMaximumSize(new Dimension(32767, 24));
      swfAnimatorRandGenCmb.setMaximumRowCount(32);
      swfAnimatorRandGenCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorOutputTypeCmb = new JComboBox();
      swfAnimatorOutputTypeCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorOutputTypeCmb.setPreferredSize(new Dimension(125, 24));
      swfAnimatorOutputTypeCmb.setMinimumSize(new Dimension(33, 24));
      swfAnimatorOutputTypeCmb.setMaximumRowCount(32);
      swfAnimatorOutputTypeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorOutputTypeCmb.setBounds(1045, 5, 125, 24);
      panel_5.add(swfAnimatorOutputTypeCmb);

      JLabel lblOutput = new JLabel();
      lblOutput.setText("Output");
      lblOutput.setToolTipText("<html>FLAMES: save sequence of (unrendered) flame files<br>" 
          + "PNG_IMAGES: render sequence of PNG images (combine into a movie with a separate program)<br>"
          + "ANB: create PD Howler AnimBrush");
      lblOutput.setPreferredSize(new Dimension(94, 22));
      lblOutput.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblOutput.setBounds(994, 6, 52, 22);
      panel_5.add(lblOutput);

      swfAnimatorQualityProfileCmb = new JComboBox();
      swfAnimatorQualityProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorQualityProfileCmb.setPreferredSize(new Dimension(125, 24));
      swfAnimatorQualityProfileCmb.setMinimumSize(new Dimension(33, 24));
      swfAnimatorQualityProfileCmb.setMaximumRowCount(32);
      swfAnimatorQualityProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorQualityProfileCmb.setBounds(822, 6, 125, 24);

      panel_5.add(swfAnimatorQualityProfileCmb);

      JLabel lblQuality = new JLabel();
      lblQuality.setText("Quality");
      lblQuality.setToolTipText("Quality profile to use when rendering movie");
      lblQuality.setPreferredSize(new Dimension(94, 22));
      lblQuality.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblQuality.setBounds(772, 6, 45, 22);
      panel_5.add(lblQuality);
    }
    return panel_5;
  }

  JButton getSwfAnimatorLoadFlameFromMainButton() {
    if (swfAnimatorLoadFlameFromMainButton == null) {
      swfAnimatorLoadFlameFromMainButton = new JButton();
      swfAnimatorLoadFlameFromMainButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().loadFlameFromMainButton_clicked();
        }
      });
      swfAnimatorLoadFlameFromMainButton.setToolTipText("Load flame from Editor and add it to the movie");
      swfAnimatorLoadFlameFromMainButton.setText("Add from Editor");
      swfAnimatorLoadFlameFromMainButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorLoadFlameFromMainButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorLoadFlameFromMainButton;
  }

  JButton getSwfAnimatorLoadFlameFromClipboardButton() {
    if (swfAnimatorLoadFlameFromClipboardButton == null) {
      swfAnimatorLoadFlameFromClipboardButton = new JButton();
      swfAnimatorLoadFlameFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().loadFlameFromClipboardButton_clicked();
        }
      });
      swfAnimatorLoadFlameFromClipboardButton.setToolTipText("Load flame from clipboard and add it to the movie");
      swfAnimatorLoadFlameFromClipboardButton.setText("Add from Clipboard");
      swfAnimatorLoadFlameFromClipboardButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorLoadFlameFromClipboardButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorLoadFlameFromClipboardButton;
  }

  JButton getSwfAnimatorLoadFlameButton() {
    if (swfAnimatorLoadFlameButton == null) {
      swfAnimatorLoadFlameButton = new JButton();
      swfAnimatorLoadFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().loadFlameButton_clicked();
        }
      });
      swfAnimatorLoadFlameButton.setToolTipText("Load flame from file and add it to the movie");
      swfAnimatorLoadFlameButton.setText("Add flame from disc");
      swfAnimatorLoadFlameButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorLoadFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorLoadFlameButton;
  }

  JComboBox getSwfAnimatorResolutionProfileCmb() {
    if (swfAnimatorResolutionProfileCmb == null) {
      swfAnimatorResolutionProfileCmb = new JComboBox();
      swfAnimatorResolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null) {
            tinaController.getSwfAnimatorCtrl().resolutionProfileCmb_changed();
          }
        }
      });
      swfAnimatorResolutionProfileCmb.setMinimumSize(new Dimension(33, 24));
      swfAnimatorResolutionProfileCmb.setBounds(644, 6, 125, 24);
      swfAnimatorResolutionProfileCmb.setPreferredSize(new Dimension(125, 24));
      swfAnimatorResolutionProfileCmb.setMaximumRowCount(32);
      swfAnimatorResolutionProfileCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorResolutionProfileCmb;
  }

  private JLabel getLabel_3() {
    if (label_3 == null) {
      label_3 = new JLabel();
      label_3.setBounds(578, 6, 65, 22);
      label_3.setText("Resolution");
      label_3.setToolTipText("Resolution profile for this movie");
      label_3.setPreferredSize(new Dimension(94, 22));
      label_3.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return label_3;
  }

  public JProgressBar getSwfAnimatorProgressBar() {
    return swfAnimatorProgressBar;
  }

  public JButton getSwfAnimatorCancelButton() {
    return swfAnimatorCancelButton;
  }

  public JSlider getSwfAnimatorFrameSlider() {
    return swfAnimatorFrameSlider;
  }

  public JWFNumberField getSwfAnimatorFrameREd() {
    return swfAnimatorFrameREd;
  }

  public JPanel getSwfAnimatorFlamesPanel() {
    return swfAnimatorFlamesPanel;
  }

  private JPanel getPanel_14() {
    if (panel_14 == null) {
      panel_14 = new JPanel();
      panel_14.setBorder(null);
      panel_14.setLayout(new BorderLayout(0, 0));
      panel_14.add(getPanel_4(), BorderLayout.CENTER);
    }
    return panel_14;
  }

  public JPanel getSwfAnimatorPreviewRootPanel() {
    return swfAnimatorPreviewRootPanel;
  }

  private JPanel getPanel_4() {
    if (panel_4 == null) {
      panel_4 = new JPanel();
      panel_4.setBorder(new TitledBorder(null, "Movies", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_4.setLayout(new BorderLayout(0, 0));

      randomMoviePanel = new JPanel();
      panel_4.add(randomMoviePanel, BorderLayout.CENTER);
      randomMoviePanel.setLayout(new BorderLayout(0, 0));
    }
    return panel_4;
  }

  JButton getSwfAnimatorRemoveFlameButton() {
    if (swfAnimatorRemoveFlameButton == null) {
      swfAnimatorRemoveFlameButton = new JButton();
      swfAnimatorRemoveFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().removeFlameButton_clicked();
        }
      });
      swfAnimatorRemoveFlameButton.setToolTipText("Remove the currently selected flame from the movie");
      swfAnimatorRemoveFlameButton.setText("Remove flame");
      swfAnimatorRemoveFlameButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorRemoveFlameButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorRemoveFlameButton;
  }

  JButton getSwfAnimatorRemoveAllFlamesButton() {
    if (swfAnimatorRemoveAllFlamesButton == null) {
      swfAnimatorRemoveAllFlamesButton = new JButton();
      swfAnimatorRemoveAllFlamesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().clearAllFlamesButton_clicked();
        }
      });
      swfAnimatorRemoveAllFlamesButton.setToolTipText("Remove all flames from the movie");
      swfAnimatorRemoveAllFlamesButton.setText("Remove all");
      swfAnimatorRemoveAllFlamesButton.setPreferredSize(new Dimension(135, 24));
      swfAnimatorRemoveAllFlamesButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorRemoveAllFlamesButton;
  }
  
  JCheckBox getSwfAnimatorCompatCBx() {
    if (swfAnimatorCompatCBx == null) {
      swfAnimatorCompatCBx = new JCheckBox();
      swfAnimatorCompatCBx.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().compatCBx_changed();
        }
      });
      swfAnimatorCompatCBx.setToolTipText("Make fading compatible with previous versions (fade to black instead of fade to invisible)");
      swfAnimatorCompatCBx.setText("Compatibility");
      swfAnimatorCompatCBx.setPreferredSize(new Dimension(135, 24));
      swfAnimatorCompatCBx.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorCompatCBx;
  }

  JButton getSwfAnimatorMoveUpButton() {
    if (swfAnimatorMoveUpButton == null) {
      swfAnimatorMoveUpButton = new JButton();
      swfAnimatorMoveUpButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().flameMoveUpButton_clicked();
        }
      });
      swfAnimatorMoveUpButton.setToolTipText("Move the currently selected flame up in the movie");
      swfAnimatorMoveUpButton.setText("Up");
      swfAnimatorMoveUpButton.setPreferredSize(new Dimension(65, 24));
      swfAnimatorMoveUpButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorMoveUpButton;
  }

  JButton getSwfAnimatorMoveDownButton() {
    if (swfAnimatorMoveDownButton == null) {
      swfAnimatorMoveDownButton = new JButton();
      swfAnimatorMoveDownButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().flameMoveDownButton_clicked();
        }
      });
      swfAnimatorMoveDownButton.setToolTipText("Move the currently selected flame down in the movie");
      swfAnimatorMoveDownButton.setText("Down");
      swfAnimatorMoveDownButton.setPreferredSize(new Dimension(65, 24));
      swfAnimatorMoveDownButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return swfAnimatorMoveDownButton;
  }

  JButton getSwfAnimatorMovieFromClipboardButton() {
    if (swfAnimatorMovieFromClipboardButton == null) {
      swfAnimatorMovieFromClipboardButton = new JButton();
      swfAnimatorMovieFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieFromClipboardButton_clicked();
        }
      });
      swfAnimatorMovieFromClipboardButton.setText("From Clipboard");
      swfAnimatorMovieFromClipboardButton.setToolTipText("Load a movie from the clipboard");
      swfAnimatorMovieFromClipboardButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieFromClipboardButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorMovieFromClipboardButton.setBounds(new Rectangle(504, 7, 125, 24));
      swfAnimatorMovieFromClipboardButton.setBounds(304, 6, 125, 24);
    }
    return swfAnimatorMovieFromClipboardButton;
  }

  JButton getSwfAnimatorMovieFromDiscButton() {
    if (swfAnimatorMovieFromDiscButton == null) {
      swfAnimatorMovieFromDiscButton = new JButton();
      swfAnimatorMovieFromDiscButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieFromDiscButton_clicked();
        }
      });
      swfAnimatorMovieFromDiscButton.setText("Load Movie");
      swfAnimatorMovieFromDiscButton.setToolTipText("Load a movie from a file");
      swfAnimatorMovieFromDiscButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieFromDiscButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorMovieFromDiscButton.setBounds(new Rectangle(504, 35, 125, 24));
      swfAnimatorMovieFromDiscButton.setBounds(304, 31, 125, 24);
    }
    return swfAnimatorMovieFromDiscButton;
  }

  JButton getSwfAnimatorMovieToClipboardButton() {
    if (swfAnimatorMovieToClipboardButton == null) {
      swfAnimatorMovieToClipboardButton = new JButton();
      swfAnimatorMovieToClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieToClipboardButton_clicked();
        }
      });
      swfAnimatorMovieToClipboardButton.setText("To Clipboard");
      swfAnimatorMovieToClipboardButton.setToolTipText("Save the current movie to the clipboard");
      swfAnimatorMovieToClipboardButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieToClipboardButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorMovieToClipboardButton.setBounds(new Rectangle(643, 7, 125, 24));
      swfAnimatorMovieToClipboardButton.setBounds(441, 6, 125, 24);
    }
    return swfAnimatorMovieToClipboardButton;
  }

  JButton getSwfAnimatorMovieToDiscButton() {
    if (swfAnimatorMovieToDiscButton == null) {
      swfAnimatorMovieToDiscButton = new JButton();
      swfAnimatorMovieToDiscButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().movieToDiscButton_clicked();
        }
      });
      swfAnimatorMovieToDiscButton.setText("Save Movie");
      swfAnimatorMovieToDiscButton.setToolTipText("Save the current movie to a file");
      swfAnimatorMovieToDiscButton.setPreferredSize(new Dimension(125, 24));
      swfAnimatorMovieToDiscButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorMovieToDiscButton.setBounds(new Rectangle(643, 35, 125, 24));
      swfAnimatorMovieToDiscButton.setBounds(441, 31, 125, 24);
    }
    return swfAnimatorMovieToDiscButton;
  }

  public JButton getSwfAnimatorFrameToEditorBtn() {
    return swfAnimatorFrameToEditorBtn;
  }

  public JButton getSwfAnimatorPlayButton() {
    return swfAnimatorPlayButton;
  }

  public JComboBox getSwfAnimatorGlobalScript2Cmb() {
    return swfAnimatorGlobalScript2Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript3Cmb() {
    return swfAnimatorGlobalScript3Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript4Cmb() {
    return swfAnimatorGlobalScript4Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript5Cmb() {
    return swfAnimatorGlobalScript5Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript3Cmb() {
    return swfAnimatorXFormScript3Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript4Cmb() {
    return swfAnimatorXFormScript4Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript5Cmb() {
    return swfAnimatorXFormScript5Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript2Cmb() {
    return swfAnimatorXFormScript2Cmb;
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

  public JPanel getRandomMoviePanel() {
    return randomMoviePanel;
  }

  public JButton getSwfAnimatorGenRandomBatchBtn() {
    return swfAnimatorGenRandomBatchBtn;
  }

  public JComboBox getSwfAnimatorRandGenCmb() {
    return swfAnimatorRandGenCmb;
  }

  private JPanel getPanel_84() {
    if (panel_84 == null) {
      panel_84 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_84.getLayout();
      flowLayout.setVgap(2);
      flowLayout.setHgap(4);
      panel_84.setBorder(null);

      swfAnimatorPlayButton = new JButton();
      swfAnimatorPlayButton.setIconTextGap(2);
      panel_84.add(swfAnimatorPlayButton);
      swfAnimatorPlayButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().playButton_clicked();
        }
      });
      swfAnimatorPlayButton.setText("Play");
      swfAnimatorPlayButton.setPreferredSize(new Dimension(82, 28));
      swfAnimatorPlayButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      swfAnimatorPlayButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/applications-multimedia.png")));

      swfAnimatorFrameToEditorBtn = new JButton();
      panel_84.add(swfAnimatorFrameToEditorBtn);
      swfAnimatorFrameToEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().swfAnimatorFrameToEditorBtn_clicked();
        }
      });
      swfAnimatorFrameToEditorBtn.setToolTipText("Copy current flame into Editor");
      swfAnimatorFrameToEditorBtn.setText("E");
      swfAnimatorFrameToEditorBtn.setPreferredSize(new Dimension(42, 28));
      swfAnimatorFrameToEditorBtn.setMnemonic(KeyEvent.VK_E);
      swfAnimatorFrameToEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_84.add(getBtnRender());
    }
    return panel_84;
  }

  private JScrollPane getScrollPane_9() {
    if (scrollPane_9 == null) {
      scrollPane_9 = new JScrollPane();
      scrollPane_9.setBorder(new TitledBorder(null, "XForm scripts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      scrollPane_9.setViewportView(getPanel_86());
    }
    return scrollPane_9;
  }

  private JPanel getPanel_86() {
    if (panel_86 == null) {
      panel_86 = new JPanel();
      panel_86.setPreferredSize(new Dimension(315, 280));
      panel_86.setLayout(null);
      animateXFormScriptLbl = new JLabel();
      animateXFormScriptLbl.setBounds(0, 0, 21, 22);
      panel_86.add(animateXFormScriptLbl);
      animateXFormScriptLbl.setName("animateXFormScriptLbl");
      animateXFormScriptLbl.setPreferredSize(new Dimension(94, 22));
      animateXFormScriptLbl.setText("01");
      animateXFormScriptLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      panel_86.add(getSwfAnimatorXFormScript1Cmb());

      swfAnimatorXFormScript1REd = new JWFNumberField();
      swfAnimatorXFormScript1REd.setBounds(213, 0, 100, 24);
      panel_86.add(swfAnimatorXFormScript1REd);
      swfAnimatorXFormScript1REd.setValue(1.0);
      swfAnimatorXFormScript1REd.setWithMotionCurve(true);
      swfAnimatorXFormScript1REd.setValueStep(0.1);
      swfAnimatorXFormScript1REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript1REd.setMotionPropertyName("xFormScript1");
      swfAnimatorXFormScript1REd.setLinkedLabelControlName("animateXFormScriptLbl");
      swfAnimatorXFormScript1REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      JLabel lblXformScript = new JLabel();
      lblXformScript.setBounds(0, 23, 21, 22);
      panel_86.add(lblXformScript);
      lblXformScript.setName("lblXformScript");
      lblXformScript.setText("02");
      lblXformScript.setPreferredSize(new Dimension(94, 22));
      lblXformScript.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript2Cmb = new JComboBox();
      swfAnimatorXFormScript2Cmb.setBounds(22, 23, 186, 24);
      panel_86.add(swfAnimatorXFormScript2Cmb);
      swfAnimatorXFormScript2Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript2Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript2Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript2REd = new JWFNumberField();
      swfAnimatorXFormScript2REd.setBounds(213, 23, 100, 24);
      panel_86.add(swfAnimatorXFormScript2REd);
      swfAnimatorXFormScript2REd.setValue(1.0);
      swfAnimatorXFormScript2REd.setWithMotionCurve(true);
      swfAnimatorXFormScript2REd.setValueStep(0.1);
      swfAnimatorXFormScript2REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript2REd.setMotionPropertyName("xFormScript2");
      swfAnimatorXFormScript2REd.setLinkedLabelControlName("lblXformScript");
      swfAnimatorXFormScript2REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      JLabel lblXformScript_1 = new JLabel();
      lblXformScript_1.setBounds(0, 46, 21, 22);
      panel_86.add(lblXformScript_1);
      lblXformScript_1.setName("lblXformScript_1");
      lblXformScript_1.setText("03");
      lblXformScript_1.setPreferredSize(new Dimension(94, 22));
      lblXformScript_1.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript3Cmb = new JComboBox();
      swfAnimatorXFormScript3Cmb.setBounds(22, 46, 186, 24);
      panel_86.add(swfAnimatorXFormScript3Cmb);
      swfAnimatorXFormScript3Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript3Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript3Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript3REd = new JWFNumberField();
      swfAnimatorXFormScript3REd.setBounds(213, 46, 100, 24);
      panel_86.add(swfAnimatorXFormScript3REd);
      swfAnimatorXFormScript3REd.setValue(1.0);
      swfAnimatorXFormScript3REd.setWithMotionCurve(true);
      swfAnimatorXFormScript3REd.setValueStep(0.1);
      swfAnimatorXFormScript3REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript3REd.setMotionPropertyName("xFormScript3");
      swfAnimatorXFormScript3REd.setLinkedLabelControlName("lblXformScript_1");
      swfAnimatorXFormScript3REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      swfAnimatorXFormScript4Cmb = new JComboBox();
      swfAnimatorXFormScript4Cmb.setBounds(22, 69, 186, 24);
      panel_86.add(swfAnimatorXFormScript4Cmb);
      swfAnimatorXFormScript4Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript4Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript4Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      JLabel lblXformScript_2 = new JLabel();
      lblXformScript_2.setBounds(0, 69, 21, 22);
      panel_86.add(lblXformScript_2);
      lblXformScript_2.setName("lblXformScript_2");
      lblXformScript_2.setText("04");
      lblXformScript_2.setPreferredSize(new Dimension(94, 22));
      lblXformScript_2.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      JLabel lblXformScript_3 = new JLabel();
      lblXformScript_3.setBounds(0, 92, 21, 22);
      panel_86.add(lblXformScript_3);
      lblXformScript_3.setName("lblXformScript_3");
      lblXformScript_3.setText("05");
      lblXformScript_3.setPreferredSize(new Dimension(94, 22));
      lblXformScript_3.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript5Cmb = new JComboBox();
      swfAnimatorXFormScript5Cmb.setBounds(22, 92, 186, 24);
      panel_86.add(swfAnimatorXFormScript5Cmb);
      swfAnimatorXFormScript5Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript5Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript5Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorXFormScript5REd = new JWFNumberField();
      swfAnimatorXFormScript5REd.setBounds(213, 92, 100, 24);
      panel_86.add(swfAnimatorXFormScript5REd);
      swfAnimatorXFormScript5REd.setValue(1.0);
      swfAnimatorXFormScript5REd.setWithMotionCurve(true);
      swfAnimatorXFormScript5REd.setValueStep(0.1);
      swfAnimatorXFormScript5REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript5REd.setMotionPropertyName("xFormScript5");
      swfAnimatorXFormScript5REd.setLinkedLabelControlName("lblXformScript_3");
      swfAnimatorXFormScript5REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      swfAnimatorXFormScript4REd = new JWFNumberField();
      swfAnimatorXFormScript4REd.setBounds(213, 69, 100, 24);
      panel_86.add(swfAnimatorXFormScript4REd);
      swfAnimatorXFormScript4REd.setValue(1.0);
      swfAnimatorXFormScript4REd.setWithMotionCurve(true);
      swfAnimatorXFormScript4REd.setValueStep(0.1);
      swfAnimatorXFormScript4REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript4REd.setMotionPropertyName("xFormScript4");
      swfAnimatorXFormScript4REd.setLinkedLabelControlName("lblXformScript_2");
      swfAnimatorXFormScript4REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      JLabel lblXformScript_4 = new JLabel();
      lblXformScript_4.setText("06");
      lblXformScript_4.setPreferredSize(new Dimension(94, 22));
      lblXformScript_4.setName("lblXformScript_4");
      lblXformScript_4.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblXformScript_4.setBounds(0, 115, 21, 22);
      panel_86.add(lblXformScript_4);

      swfAnimatorXFormScript6Cmb = new JComboBox();
      swfAnimatorXFormScript6Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript6Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript6Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript6Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript6Cmb.setBounds(22, 115, 186, 24);
      panel_86.add(swfAnimatorXFormScript6Cmb);

      swfAnimatorXFormScript6REd = new JWFNumberField();
      swfAnimatorXFormScript6REd.setWithMotionCurve(true);
      swfAnimatorXFormScript6REd.setValueStep(0.1);
      swfAnimatorXFormScript6REd.setValue(1.0);
      swfAnimatorXFormScript6REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript6REd.setMotionPropertyName("xFormScript6");
      swfAnimatorXFormScript6REd.setLinkedLabelControlName("lblXformScript_4");
      swfAnimatorXFormScript6REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript6REd.setBounds(213, 115, 100, 24);
      swfAnimatorXFormScript6REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript6REd());
        }
      });
      swfAnimatorXFormScript6REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });

      panel_86.add(swfAnimatorXFormScript6REd);

      JLabel lblXformScript_5 = new JLabel();
      lblXformScript_5.setText("07");
      lblXformScript_5.setPreferredSize(new Dimension(94, 22));
      lblXformScript_5.setName("lblXformScript_5");
      lblXformScript_5.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblXformScript_5.setBounds(0, 138, 21, 22);
      panel_86.add(lblXformScript_5);

      swfAnimatorXFormScript7Cmb = new JComboBox();
      swfAnimatorXFormScript7Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript7Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript7Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript7Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript7Cmb.setBounds(22, 138, 186, 24);
      panel_86.add(swfAnimatorXFormScript7Cmb);

      swfAnimatorXFormScript7REd = new JWFNumberField();
      swfAnimatorXFormScript7REd.setWithMotionCurve(true);
      swfAnimatorXFormScript7REd.setValueStep(0.1);
      swfAnimatorXFormScript7REd.setValue(1.0);
      swfAnimatorXFormScript7REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript7REd.setMotionPropertyName("xFormScript7");
      swfAnimatorXFormScript7REd.setLinkedLabelControlName("lblXformScript_5");
      swfAnimatorXFormScript7REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript7REd.setBounds(213, 138, 100, 24);
      swfAnimatorXFormScript7REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript7REd());
        }
      });
      swfAnimatorXFormScript7REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });

      panel_86.add(swfAnimatorXFormScript7REd);

      JLabel lblXformScript_6 = new JLabel();
      lblXformScript_6.setText("08");
      lblXformScript_6.setPreferredSize(new Dimension(94, 22));
      lblXformScript_6.setName("lblXformScript_6");
      lblXformScript_6.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblXformScript_6.setBounds(0, 161, 21, 22);
      panel_86.add(lblXformScript_6);

      swfAnimatorXFormScript8Cmb = new JComboBox();
      swfAnimatorXFormScript8Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript8Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript8Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript8Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript8Cmb.setBounds(22, 161, 186, 24);
      panel_86.add(swfAnimatorXFormScript8Cmb);

      swfAnimatorXFormScript8REd = new JWFNumberField();
      swfAnimatorXFormScript8REd.setWithMotionCurve(true);
      swfAnimatorXFormScript8REd.setValueStep(0.1);
      swfAnimatorXFormScript8REd.setValue(1.0);
      swfAnimatorXFormScript8REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript8REd.setMotionPropertyName("xFormScript8");
      swfAnimatorXFormScript8REd.setLinkedLabelControlName("lblXformScript_6");
      swfAnimatorXFormScript8REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript8REd.setBounds(213, 161, 100, 24);
      swfAnimatorXFormScript8REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript8REd());
        }
      });
      swfAnimatorXFormScript8REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript8REd);

      JLabel lblXformScript_7 = new JLabel();
      lblXformScript_7.setText("09");
      lblXformScript_7.setPreferredSize(new Dimension(94, 22));
      lblXformScript_7.setName("lblXformScript_7");
      lblXformScript_7.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblXformScript_7.setBounds(0, 184, 21, 22);
      panel_86.add(lblXformScript_7);

      swfAnimatorXFormScript9Cmb = new JComboBox();
      swfAnimatorXFormScript9Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript9Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript9Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript9Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript9Cmb.setBounds(22, 184, 186, 24);
      panel_86.add(swfAnimatorXFormScript9Cmb);

      swfAnimatorXFormScript9REd = new JWFNumberField();
      swfAnimatorXFormScript9REd.setWithMotionCurve(true);
      swfAnimatorXFormScript9REd.setValueStep(0.1);
      swfAnimatorXFormScript9REd.setValue(1.0);
      swfAnimatorXFormScript9REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript9REd.setMotionPropertyName("xFormScript9");
      swfAnimatorXFormScript9REd.setLinkedLabelControlName("lblXformScript_7");
      swfAnimatorXFormScript9REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript9REd.setBounds(213, 184, 100, 24);
      swfAnimatorXFormScript9REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript9REd());
        }
      });
      swfAnimatorXFormScript9REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript9REd);

      JLabel lblXformScript_8 = new JLabel();
      lblXformScript_8.setText("10");
      lblXformScript_8.setPreferredSize(new Dimension(94, 22));
      lblXformScript_8.setName("lblXformScript_8");
      lblXformScript_8.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblXformScript_8.setBounds(0, 207, 21, 22);
      panel_86.add(lblXformScript_8);

      swfAnimatorXFormScript10Cmb = new JComboBox();
      swfAnimatorXFormScript10Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript10Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript10Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript10Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript10Cmb.setBounds(22, 207, 186, 24);
      panel_86.add(swfAnimatorXFormScript10Cmb);

      swfAnimatorXFormScript10REd = new JWFNumberField();
      swfAnimatorXFormScript10REd.setWithMotionCurve(true);
      swfAnimatorXFormScript10REd.setValueStep(0.1);
      swfAnimatorXFormScript10REd.setValue(1.0);
      swfAnimatorXFormScript10REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript10REd.setMotionPropertyName("xFormScript10");
      swfAnimatorXFormScript10REd.setLinkedLabelControlName("lblXformScript_8");
      swfAnimatorXFormScript10REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript10REd.setBounds(213, 207, 100, 24);
      swfAnimatorXFormScript10REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript10REd());
        }
      });
      swfAnimatorXFormScript10REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript10REd);

      JLabel lblXformScript_9 = new JLabel();
      lblXformScript_9.setText("11");
      lblXformScript_9.setPreferredSize(new Dimension(94, 22));
      lblXformScript_9.setName("lblXformScript_9");
      lblXformScript_9.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblXformScript_9.setBounds(0, 230, 21, 22);
      panel_86.add(lblXformScript_9);

      swfAnimatorXFormScript11Cmb = new JComboBox();
      swfAnimatorXFormScript11Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript11Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript11Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript11Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript11Cmb.setBounds(22, 230, 186, 24);
      panel_86.add(swfAnimatorXFormScript11Cmb);

      swfAnimatorXFormScript11REd = new JWFNumberField();
      swfAnimatorXFormScript11REd.setWithMotionCurve(true);
      swfAnimatorXFormScript11REd.setValueStep(0.1);
      swfAnimatorXFormScript11REd.setValue(1.0);
      swfAnimatorXFormScript11REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript11REd.setMotionPropertyName("xFormScript11");
      swfAnimatorXFormScript11REd.setLinkedLabelControlName("lblXformScript_9");
      swfAnimatorXFormScript11REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript11REd.setBounds(213, 230, 100, 24);
      swfAnimatorXFormScript11REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript11REd());
        }
      });
      swfAnimatorXFormScript11REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript11REd);

      JLabel lblXformScript_10 = new JLabel();
      lblXformScript_10.setText("12");
      lblXformScript_10.setPreferredSize(new Dimension(94, 22));
      lblXformScript_10.setName("lblXformScript_10");
      lblXformScript_10.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      lblXformScript_10.setBounds(0, 253, 21, 22);
      panel_86.add(lblXformScript_10);

      swfAnimatorXFormScript12Cmb = new JComboBox();
      swfAnimatorXFormScript12Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript12Cmb.setPreferredSize(new Dimension(275, 22));
      swfAnimatorXFormScript12Cmb.setMaximumRowCount(16);
      swfAnimatorXFormScript12Cmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      swfAnimatorXFormScript12Cmb.setBounds(22, 253, 186, 24);
      panel_86.add(swfAnimatorXFormScript12Cmb);

      swfAnimatorXFormScript12REd = new JWFNumberField();
      swfAnimatorXFormScript12REd.setWithMotionCurve(true);
      swfAnimatorXFormScript12REd.setValueStep(0.1);
      swfAnimatorXFormScript12REd.setValue(1.0);
      swfAnimatorXFormScript12REd.setPreferredSize(new Dimension(100, 24));
      swfAnimatorXFormScript12REd.setMotionPropertyName("xFormScript12");
      swfAnimatorXFormScript12REd.setLinkedLabelControlName("lblXformScript_10");
      swfAnimatorXFormScript12REd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorXFormScript12REd.setBounds(213, 253, 100, 24);
      swfAnimatorXFormScript12REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript12REd());
        }
      });
      swfAnimatorXFormScript12REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      panel_86.add(swfAnimatorXFormScript12REd);
      swfAnimatorXFormScript4REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript4REd());
        }
      });
      swfAnimatorXFormScript4REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript5REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript5REd());
        }
      });
      swfAnimatorXFormScript5REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript5Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript4Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript3REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript3REd());
        }
      });
      swfAnimatorXFormScript3REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript3Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript2REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript2REd());
        }
      });
      swfAnimatorXFormScript2REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript2Cmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorXFormScript1REd.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().editXFormMotionCurve(getSwfAnimatorXFormScript1REd());
        }
      });
      swfAnimatorXFormScript1REd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
    }
    return panel_86;
  }

  private JPanel getPanel_87() {
    if (panel_87 == null) {
      panel_87 = new JPanel();
      panel_87.setMaximumSize(new Dimension(32767, 120));
      panel_87.setBorder(new TitledBorder(null, "Speed / Motion blur", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      GridBagLayout gbl_panel_87 = new GridBagLayout();
      gbl_panel_87.columnWidths = new int[] { 0, 0, 0, 0 };
      gbl_panel_87.rowHeights = new int[] { 0, 0 };
      gbl_panel_87.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
      gbl_panel_87.rowWeights = new double[] { 0.0, 0.0 };
      panel_87.setLayout(gbl_panel_87);
      animateFramesLbl = new JLabel();
      GridBagConstraints gbc_animateFramesLbl = new GridBagConstraints();
      gbc_animateFramesLbl.fill = GridBagConstraints.HORIZONTAL;
      gbc_animateFramesLbl.insets = new Insets(0, 0, 5, 5);
      gbc_animateFramesLbl.gridx = 0;
      gbc_animateFramesLbl.gridy = 0;
      panel_87.add(animateFramesLbl, gbc_animateFramesLbl);
      animateFramesLbl.setHorizontalAlignment(SwingConstants.LEFT);
      animateFramesLbl.setPreferredSize(new Dimension(94, 22));
      animateFramesLbl.setText("Total frame count");
      animateFramesLbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      GridBagConstraints gbc_swfAnimatorFramesREd = new GridBagConstraints();
      gbc_swfAnimatorFramesREd.insets = new Insets(0, 0, 5, 5);
      gbc_swfAnimatorFramesREd.gridx = 1;
      gbc_swfAnimatorFramesREd.gridy = 0;
      panel_87.add(getSwfAnimatorFramesREd(), gbc_swfAnimatorFramesREd);

      JLabel lblFramesPerSecond = new JLabel();
      GridBagConstraints gbc_lblFramesPerSecond = new GridBagConstraints();
      gbc_lblFramesPerSecond.fill = GridBagConstraints.HORIZONTAL;
      gbc_lblFramesPerSecond.insets = new Insets(0, 0, 5, 5);
      gbc_lblFramesPerSecond.gridx = 2;
      gbc_lblFramesPerSecond.gridy = 0;
      panel_87.add(lblFramesPerSecond, gbc_lblFramesPerSecond);
      lblFramesPerSecond.setHorizontalAlignment(SwingConstants.LEFT);
      lblFramesPerSecond.setText("Frames per second");
      lblFramesPerSecond.setPreferredSize(new Dimension(110, 22));
      lblFramesPerSecond.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorFramesPerSecondREd = new JWFNumberField();
      GridBagConstraints gbc_swfAnimatorFramesPerSecondREd = new GridBagConstraints();
      gbc_swfAnimatorFramesPerSecondREd.insets = new Insets(0, 0, 5, 0);
      gbc_swfAnimatorFramesPerSecondREd.gridx = 3;
      gbc_swfAnimatorFramesPerSecondREd.gridy = 0;
      panel_87.add(swfAnimatorFramesPerSecondREd, gbc_swfAnimatorFramesPerSecondREd);
      swfAnimatorFramesPerSecondREd.setMaxValue(120.0);
      swfAnimatorFramesPerSecondREd.setMinValue(1.0);
      swfAnimatorFramesPerSecondREd.setHasMaxValue(true);
      swfAnimatorFramesPerSecondREd.setHasMinValue(true);
      swfAnimatorFramesPerSecondREd.setEditable(true);
      swfAnimatorFramesPerSecondREd.setText("12");
      swfAnimatorFramesPerSecondREd.setPreferredSize(new Dimension(64, 24));
      swfAnimatorFramesPerSecondREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));

      JLabel lblMotionBlurLength = new JLabel();
      GridBagConstraints gbc_lblMotionBlurLength = new GridBagConstraints();
      gbc_lblMotionBlurLength.fill = GridBagConstraints.HORIZONTAL;
      gbc_lblMotionBlurLength.insets = new Insets(0, 0, 0, 5);
      gbc_lblMotionBlurLength.gridx = 0;
      gbc_lblMotionBlurLength.gridy = 1;
      panel_87.add(lblMotionBlurLength, gbc_lblMotionBlurLength);
      lblMotionBlurLength.setText("Motion blur length");
      lblMotionBlurLength.setPreferredSize(new Dimension(94, 22));
      lblMotionBlurLength.setHorizontalAlignment(SwingConstants.LEFT);
      lblMotionBlurLength.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorMotionBlurLengthREd = new JWFNumberField();
      swfAnimatorMotionBlurLengthREd.setMinimumSize(new Dimension(40, 28));
      GridBagConstraints gbc_swfAnimatorMotionBlurLengthREd = new GridBagConstraints();
      gbc_swfAnimatorMotionBlurLengthREd.insets = new Insets(0, 0, 0, 5);
      gbc_swfAnimatorMotionBlurLengthREd.gridx = 1;
      gbc_swfAnimatorMotionBlurLengthREd.gridy = 1;
      panel_87.add(swfAnimatorMotionBlurLengthREd, gbc_swfAnimatorMotionBlurLengthREd);
      swfAnimatorMotionBlurLengthREd.setValueStep(1.0);
      swfAnimatorMotionBlurLengthREd.setOnlyIntegers(true);
      swfAnimatorMotionBlurLengthREd.setText("12");
      swfAnimatorMotionBlurLengthREd.setPreferredSize(new Dimension(64, 24));
      swfAnimatorMotionBlurLengthREd.setMaxValue(120.0);
      swfAnimatorMotionBlurLengthREd.setHasMinValue(true);
      swfAnimatorMotionBlurLengthREd.setHasMaxValue(true);
      swfAnimatorMotionBlurLengthREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorMotionBlurLengthREd.setEditable(true);

      JLabel lblMotionBlurTimestep = new JLabel();
      GridBagConstraints gbc_lblMotionBlurTimestep = new GridBagConstraints();
      gbc_lblMotionBlurTimestep.fill = GridBagConstraints.HORIZONTAL;
      gbc_lblMotionBlurTimestep.insets = new Insets(0, 0, 0, 5);
      gbc_lblMotionBlurTimestep.gridx = 2;
      gbc_lblMotionBlurTimestep.gridy = 1;
      panel_87.add(lblMotionBlurTimestep, gbc_lblMotionBlurTimestep);
      lblMotionBlurTimestep.setText("Motion blur timestep");
      lblMotionBlurTimestep.setPreferredSize(new Dimension(110, 22));
      lblMotionBlurTimestep.setHorizontalAlignment(SwingConstants.LEFT);
      lblMotionBlurTimestep.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorMotionBlurTimeStepREd = new JWFNumberField();
      GridBagConstraints gbc_swfAnimatorMotionBlurTimeStepREd = new GridBagConstraints();
      gbc_swfAnimatorMotionBlurTimeStepREd.gridx = 3;
      gbc_swfAnimatorMotionBlurTimeStepREd.gridy = 1;
      panel_87.add(swfAnimatorMotionBlurTimeStepREd, gbc_swfAnimatorMotionBlurTimeStepREd);
      swfAnimatorMotionBlurTimeStepREd.setValueStep(0.01);
      swfAnimatorMotionBlurTimeStepREd.setText("0.15");
      swfAnimatorMotionBlurTimeStepREd.setPreferredSize(new Dimension(64, 24));
      swfAnimatorMotionBlurTimeStepREd.setMaxValue(1.0);
      swfAnimatorMotionBlurTimeStepREd.setHasMinValue(true);
      swfAnimatorMotionBlurTimeStepREd.setHasMaxValue(true);
      swfAnimatorMotionBlurTimeStepREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      swfAnimatorMotionBlurTimeStepREd.setEditable(true);
      swfAnimatorMotionBlurTimeStepREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
      swfAnimatorMotionBlurLengthREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            tinaController.getSwfAnimatorCtrl().moviePropertyChanged();
          }
        }
      });
    }
    return panel_87;
  }

  public JComboBox getSwfAnimatorXFormScript6Cmb() {
    return swfAnimatorXFormScript6Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript7Cmb() {
    return swfAnimatorXFormScript7Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript8Cmb() {
    return swfAnimatorXFormScript8Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript9Cmb() {
    return swfAnimatorXFormScript9Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript10Cmb() {
    return swfAnimatorXFormScript10Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript11Cmb() {
    return swfAnimatorXFormScript11Cmb;
  }

  public JComboBox getSwfAnimatorXFormScript12Cmb() {
    return swfAnimatorXFormScript12Cmb;
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

  public JLabel getLblGlobalScript_4() {
    return lblGlobalScript_4;
  }

  public JLabel getLblGlobalScript_5() {
    return lblGlobalScript_5;
  }

  public JLabel getLblGlobalScript_6() {
    return lblGlobalScript_6;
  }

  public JLabel getLblGlobalScript_7() {
    return lblGlobalScript_7;
  }

  public JLabel getLblGlobalScript_8() {
    return lblGlobalScript_8;
  }

  public JComboBox getSwfAnimatorGlobalScript6Cmb() {
    return swfAnimatorGlobalScript6Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript7Cmb() {
    return swfAnimatorGlobalScript7Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript8Cmb() {
    return swfAnimatorGlobalScript8Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript9Cmb() {
    return swfAnimatorGlobalScript9Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript10Cmb() {
    return swfAnimatorGlobalScript10Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript11Cmb() {
    return swfAnimatorGlobalScript11Cmb;
  }

  public JComboBox getSwfAnimatorGlobalScript12Cmb() {
    return swfAnimatorGlobalScript12Cmb;
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

  private JButton getBtnRender() {
    if (btnRender == null) {
      btnRender = new JButton();
      btnRender.setIconTextGap(2);
      btnRender.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getSwfAnimatorCtrl().renderFlameImage();
        }
      });
      btnRender.setToolTipText("Render the current frame in higher qualiry");
      btnRender.setText("Render");
      btnRender.setPreferredSize(new Dimension(82, 28));
      btnRender.setMnemonic(KeyEvent.VK_R);
      btnRender.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 9));
      btnRender.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/fraqtive.png")));
    }
    return btnRender;
  }

  private JPanel getPanel_12() {
    if (panel_12 == null) {
      panel_12 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_12.getLayout();
      flowLayout.setVgap(1);
      panel_12.setPreferredSize(new Dimension(10, 24));

      JLabel lblFrame = new JLabel();
      panel_12.add(lblFrame);
      lblFrame.setHorizontalAlignment(SwingConstants.RIGHT);
      lblFrame.setText("Frame");
      lblFrame.setPreferredSize(new Dimension(38, 22));
      lblFrame.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

      swfAnimatorFrameREd = new JWFNumberField();
      panel_12.add(swfAnimatorFrameREd);
      swfAnimatorFrameREd.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (tinaController != null && tinaController.getSwfAnimatorCtrl() != null) {
            try {
              swfAnimatorFrameSlider.setValue(swfAnimatorFrameREd.getIntValue());
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      swfAnimatorFrameREd.setMaxValue(120.0);
      swfAnimatorFrameREd.setMinValue(1.0);
      swfAnimatorFrameREd.setHasMaxValue(true);
      swfAnimatorFrameREd.setHasMinValue(true);
      swfAnimatorFrameREd.setOnlyIntegers(true);
      swfAnimatorFrameREd.setText("60");
      swfAnimatorFrameREd.setPreferredSize(new Dimension(56, 22));
      swfAnimatorFrameREd.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    }
    return panel_12;
  }

  public JPanel getPanel_1() {
    return panel_93;
  }

  public JComboBox getSwfAnimatorQualityProfileCmb() {
    return swfAnimatorQualityProfileCmb;
  }

  public JComboBox getSwfAnimatorOutputTypeCmb() {
    return swfAnimatorOutputTypeCmb;
  }

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }

}
