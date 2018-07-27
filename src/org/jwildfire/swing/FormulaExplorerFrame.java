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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class FormulaExplorerFrame extends JFrame {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private MainController mainController = null;
  private FormulaExplorerController formulaExplorerController;
  private JPanel jContentPane = null;
  private JPanel formulaExplorerDrawPanel = null;

  private JPanel formulaExplorerBottomPanel = null;

  private JPanel formulaExplorerCenterPanel = null;

  private JTextField formulaExplorerFormula1REd = null;

  private JTextField formulaExplorerFormulaXMinREd = null;

  private JTextField formulaExplorerFormulaXMaxREd = null;

  private JTextField formulaExplorerFormulaXCountREd = null;

  private JButton formulaExplorerCalculateBtn = null;

  private JSplitPane formulaExplorerSplitPane = null;
  private JPanel formulaExplorerValuesPanel = null;

  private JScrollPane formulaExplorerValuesScrollPane = null;

  private JTextArea formulaExplorerValuesTextArea = null;

  private JLabel formulaExplorerXRangeLbl = null;

  private JLabel formulaExplorerXCountLbl = null;

  private JLabel formulaExplorerFormula2Lbl = null;

  private JLabel formulaExplorerFormula3Lbl = null;

  private JTextField formulaExplorerFormula2REd = null;

  private JTextField formulaExplorerFormula3REd = null;

  private JLabel formulaExplorerFormula1Lbl = null;

  /**
   * This is the xxx default constructor
   */
  public FormulaExplorerFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(596, 605);
    this.setBounds(new Rectangle(650, 36, 596, 605));
    this.setResizable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setVisible(false);
    this.setTitle("Formula Explorer");
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
      jContentPane.add(getFormulaExplorerBottomPanel(),
          BorderLayout.SOUTH);
      jContentPane.add(getFormulaExplorerCenterPanel(),
          BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes formulaExplorerBottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getFormulaExplorerBottomPanel() {
    if (formulaExplorerBottomPanel == null) {
      formulaExplorerFormula1Lbl = new JLabel();
      formulaExplorerFormula1Lbl.setBounds(new Rectangle(5, 9, 133, 26));
      formulaExplorerFormula1Lbl
          .setHorizontalAlignment(SwingConstants.RIGHT);
      formulaExplorerFormula1Lbl.setText("Formula1 (yellow)");
      formulaExplorerFormula1Lbl.setPreferredSize(new Dimension(50, 26));
      formulaExplorerFormula3Lbl = new JLabel();
      formulaExplorerFormula3Lbl.setBounds(new Rectangle(8, 66, 134, 26));
      formulaExplorerFormula3Lbl
          .setHorizontalAlignment(SwingConstants.RIGHT);
      formulaExplorerFormula3Lbl.setText("Formula3 (green)");
      formulaExplorerFormula3Lbl.setPreferredSize(new Dimension(50, 26));
      formulaExplorerFormula2Lbl = new JLabel();
      formulaExplorerFormula2Lbl.setBounds(new Rectangle(6, 38, 136, 26));
      formulaExplorerFormula2Lbl
          .setHorizontalAlignment(SwingConstants.RIGHT);
      formulaExplorerFormula2Lbl.setText("Formula2 (red)");
      formulaExplorerFormula2Lbl.setPreferredSize(new Dimension(50, 26));
      formulaExplorerXCountLbl = new JLabel();
      formulaExplorerXCountLbl.setBounds(new Rectangle(381, 106, 50, 26));
      formulaExplorerXCountLbl
          .setHorizontalAlignment(SwingConstants.RIGHT);
      formulaExplorerXCountLbl.setText("Count");
      formulaExplorerXCountLbl.setPreferredSize(new Dimension(50, 26));
      formulaExplorerXRangeLbl = new JLabel();
      formulaExplorerXRangeLbl.setBounds(new Rectangle(5, 109, 62, 26));
      formulaExplorerXRangeLbl
          .setHorizontalAlignment(SwingConstants.RIGHT);
      formulaExplorerXRangeLbl.setText("X-Range");
      formulaExplorerXRangeLbl.setPreferredSize(new Dimension(50, 26));
      formulaExplorerBottomPanel = new JPanel();
      formulaExplorerBottomPanel.setLayout(null);
      formulaExplorerBottomPanel.setPreferredSize(new Dimension(0, 170));
      formulaExplorerBottomPanel.add(getFormulaExplorerCalculateBtn(),
          null);
      formulaExplorerBottomPanel.add(getFormulaExplorerFormula1REd(),
          null);
      formulaExplorerBottomPanel.add(getFormulaExplorerFormulaXMinREd(),
          null);
      formulaExplorerBottomPanel.add(getFormulaExplorerFormulaXMaxREd(),
          null);
      formulaExplorerBottomPanel.add(
          getFormulaExplorerFormulaXCountREd(), null);
      formulaExplorerBottomPanel.add(formulaExplorerXRangeLbl, null);
      formulaExplorerBottomPanel.add(formulaExplorerXCountLbl, null);
      formulaExplorerBottomPanel.add(formulaExplorerFormula2Lbl, null);
      formulaExplorerBottomPanel.add(formulaExplorerFormula3Lbl, null);
      formulaExplorerBottomPanel.add(getFormulaExplorerFormula2REd(),
          null);
      formulaExplorerBottomPanel.add(getFormulaExplorerFormula3REd(),
          null);
      formulaExplorerBottomPanel.add(formulaExplorerFormula1Lbl, null);
    }
    return formulaExplorerBottomPanel;
  }

  /**
   * This method initializes formulaExplorerCenterPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getFormulaExplorerCenterPanel() {
    if (formulaExplorerCenterPanel == null) {
      GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
      gridBagConstraints2.fill = GridBagConstraints.BOTH;
      gridBagConstraints2.gridy = 0;
      gridBagConstraints2.weightx = 1.0;
      gridBagConstraints2.weighty = 1.0;
      gridBagConstraints2.gridx = 0;
      formulaExplorerCenterPanel = new JPanel();
      formulaExplorerCenterPanel.setLayout(new GridBagLayout());
      formulaExplorerCenterPanel.add(getFormulaExplorerSplitPane(),
          gridBagConstraints2);
    }
    return formulaExplorerCenterPanel;
  }

  /**
   * This method initializes formulaExplorerFormula1REd
   * 
   * @return javax.swing.JTextField
   */
  JTextField getFormulaExplorerFormula1REd() {
    if (formulaExplorerFormula1REd == null) {
      formulaExplorerFormula1REd = new JTextField();
      formulaExplorerFormula1REd.setPreferredSize(new Dimension(60, 26));
      formulaExplorerFormula1REd.setText("rect(5*x-7)");
      formulaExplorerFormula1REd
          .setBounds(new Rectangle(143, 8, 427, 26));
      formulaExplorerFormula1REd.setFont(new Font("Dialog", Font.PLAIN,
          12));
    }
    return formulaExplorerFormula1REd;
  }

  /**
   * This method initializes formulaExplorerFormulaXMinREd
   * 
   * @return javax.swing.JTextField
   */
  JTextField getFormulaExplorerFormulaXMinREd() {
    if (formulaExplorerFormulaXMinREd == null) {
      formulaExplorerFormulaXMinREd = new JTextField();
      formulaExplorerFormulaXMinREd
          .setPreferredSize(new Dimension(60, 26));
      formulaExplorerFormulaXMinREd.setText("0.0");
      formulaExplorerFormulaXMinREd.setBounds(new Rectangle(70, 106, 134,
          26));
      formulaExplorerFormulaXMinREd.setFont(new Font("Dialog",
          Font.PLAIN, 12));
    }
    return formulaExplorerFormulaXMinREd;
  }

  /**
   * This method initializes formulaExplorerFormulaXMaxREd
   * 
   * @return javax.swing.JTextField
   */
  JTextField getFormulaExplorerFormulaXMaxREd() {
    if (formulaExplorerFormulaXMaxREd == null) {
      formulaExplorerFormulaXMaxREd = new JTextField();
      formulaExplorerFormulaXMaxREd
          .setPreferredSize(new Dimension(60, 26));
      formulaExplorerFormulaXMaxREd.setText("2");
      formulaExplorerFormulaXMaxREd.setLocation(new Point(206, 106));
      formulaExplorerFormulaXMaxREd.setSize(new Dimension(134, 26));
      formulaExplorerFormulaXMaxREd.setFont(new Font("Dialog",
          Font.PLAIN, 12));
    }
    return formulaExplorerFormulaXMaxREd;
  }

  /**
   * This method initializes formulaExplorerFormulaXCountREd
   * 
   * @return javax.swing.JTextField
   */
  JTextField getFormulaExplorerFormulaXCountREd() {
    if (formulaExplorerFormulaXCountREd == null) {
      formulaExplorerFormulaXCountREd = new JTextField();
      formulaExplorerFormulaXCountREd.setPreferredSize(new Dimension(60,
          26));
      formulaExplorerFormulaXCountREd.setText("500");
      formulaExplorerFormulaXCountREd.setLocation(new Point(434, 107));
      formulaExplorerFormulaXCountREd.setSize(new Dimension(134, 26));
      formulaExplorerFormulaXCountREd.setFont(new Font("Dialog",
          Font.PLAIN, 12));
    }
    return formulaExplorerFormulaXCountREd;
  }

  /**
   * This method initializes formulaExplorerCalculateBtn
   * 
   * @return javax.swing.JButton
   */
  private JButton getFormulaExplorerCalculateBtn() {
    if (formulaExplorerCalculateBtn == null) {
      formulaExplorerCalculateBtn = new JButton();
      formulaExplorerCalculateBtn.setMnemonic(KeyEvent.VK_C);
      formulaExplorerCalculateBtn.setText("Calculate");
      formulaExplorerCalculateBtn.setActionCommand("Calculate");
      formulaExplorerCalculateBtn.setBounds(new Rectangle(144, 138, 303,
          26));
      formulaExplorerCalculateBtn
          .setPreferredSize(new Dimension(120, 26));
      formulaExplorerCalculateBtn
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              try {
                formulaExplorerController.calculate();
              }
              catch (Throwable ex) {
                mainController.handleError(ex);
              }
            }
          });
    }
    return formulaExplorerCalculateBtn;
  }

  /**
   * This method initializes formulaExplorerSplitPane
   * 
   * @return javax.swing.JSplitPane
   */
  private JSplitPane getFormulaExplorerSplitPane() {
    if (formulaExplorerSplitPane == null) {
      formulaExplorerSplitPane = new JSplitPane();
      formulaExplorerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
      formulaExplorerSplitPane
          .setTopComponent(getFormulaExplorerDrawPanel());
      formulaExplorerSplitPane
          .setBottomComponent(getFormulaExplorerValuesPanel());
      formulaExplorerSplitPane.setDividerLocation(300);
    }
    return formulaExplorerSplitPane;
  }

  /**
   * This method initializes formulaExplorerDrawPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getFormulaExplorerDrawPanel() {
    if (formulaExplorerDrawPanel == null) {
      formulaExplorerDrawPanel = new JPanel();
      formulaExplorerDrawPanel.setLayout(new BorderLayout());
    }
    return formulaExplorerDrawPanel;
  }

  /**
   * This method initializes formulaExplorerValuesPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getFormulaExplorerValuesPanel() {
    if (formulaExplorerValuesPanel == null) {
      GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
      gridBagConstraints3.fill = GridBagConstraints.BOTH;
      gridBagConstraints3.gridy = 0;
      gridBagConstraints3.weightx = 1.0;
      gridBagConstraints3.weighty = 1.0;
      gridBagConstraints3.gridx = 0;
      formulaExplorerValuesPanel = new JPanel();
      formulaExplorerValuesPanel.setLayout(new GridBagLayout());
      formulaExplorerValuesPanel.add(
          getFormulaExplorerValuesScrollPane(), gridBagConstraints3);
    }
    return formulaExplorerValuesPanel;
  }

  /**
   * This method initializes formulaExplorerValuesScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getFormulaExplorerValuesScrollPane() {
    if (formulaExplorerValuesScrollPane == null) {
      formulaExplorerValuesScrollPane = new JScrollPane();
      formulaExplorerValuesScrollPane
          .setViewportView(getFormulaExplorerValuesTextArea());
    }
    return formulaExplorerValuesScrollPane;
  }

  /**
   * This method initializes formulaExplorerValuesTextArea
   * 
   * @return javax.swing.JTextArea
   */
  JTextArea getFormulaExplorerValuesTextArea() {
    if (formulaExplorerValuesTextArea == null) {
      formulaExplorerValuesTextArea = new JTextArea();
    }
    return formulaExplorerValuesTextArea;
  }

  /**
   * This method initializes formulaExplorerFormula2REd
   * 
   * @return javax.swing.JTextField
   */
  JTextField getFormulaExplorerFormula2REd() {
    if (formulaExplorerFormula2REd == null) {
      formulaExplorerFormula2REd = new JTextField();
      formulaExplorerFormula2REd
          .setBounds(new Rectangle(143, 40, 424, 26));
      formulaExplorerFormula2REd.setPreferredSize(new Dimension(60, 26));
      formulaExplorerFormula2REd.setText("sin(5*x-7)");
      formulaExplorerFormula2REd.setFont(new Font("Dialog", Font.PLAIN,
          12));
    }
    return formulaExplorerFormula2REd;
  }

  /**
   * This method initializes formulaExplorerFormula3REd
   * 
   * @return javax.swing.JTextField
   */
  JTextField getFormulaExplorerFormula3REd() {
    if (formulaExplorerFormula3REd == null) {
      formulaExplorerFormula3REd = new JTextField();
      formulaExplorerFormula3REd
          .setBounds(new Rectangle(144, 68, 419, 26));
      formulaExplorerFormula3REd.setPreferredSize(new Dimension(60, 26));
      formulaExplorerFormula3REd.setText("triangle(5*x-7)*sin(5*x-7)");
      formulaExplorerFormula3REd.setFont(new Font("Dialog", Font.PLAIN,
          12));
    }
    return formulaExplorerFormula3REd;
  }

  public void initApp() {
    formulaExplorerDrawPanel.add((JPanel) getFormulaPanel(),
        BorderLayout.CENTER);
  }

  // hide from Editor
  private Object formulaPanel; // @jve:decl-index=0:

  Object getFormulaPanel() {
    if (formulaPanel == null) {
      formulaPanel = new FormulaPanel();
      ((JPanel) formulaPanel).setLayout(null);
    }
    return formulaPanel;
  }

  public void setMainController(MainController pMainController) {
    mainController = pMainController;

  }

  public void setFormulaExplorerController(FormulaExplorerController pFormulaExplorerController) {
    formulaExplorerController = pFormulaExplorerController;
  }

} //  @jve:decl-index=0:visual-constraint="10,10"
