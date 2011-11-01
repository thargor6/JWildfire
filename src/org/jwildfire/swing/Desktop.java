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
import java.awt.Event;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.SoftBevelBorder;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;

public class Desktop extends JApplet {
  private static final long serialVersionUID = 1L;

  private JFrame jFrame = null; // @jve:decl-index=0:visual-constraint="10,10"
  private JPanel jContentPane = null;
  private JMenuBar mainJMenuBar = null;
  private JMenu fileMenu = null;
  private JMenu helpMenu = null;
  private JMenuItem exitMenuItem = null;
  private JMenuItem aboutMenuItem = null;
  private JMenuItem saveMenuItem = null;
  private JDesktopPane mainDesktopPane = null;
  private JMenu windowMenu = null;
  private JCheckBoxMenuItem operatorsMenuItem = null;
  private JCheckBoxMenuItem tinaMenuItem = null;
  private JMenuItem openMenuItem = null;

  private StandardErrorHandler errorHandler = null;

  /**
   * This method initializes mainDesktopPane
   * 
   * @return javax.swing.JDesktopPane
   */
  private JDesktopPane getMainDesktopPane() {
    if (mainDesktopPane == null) {
      mainDesktopPane = new JDesktopPane();
      mainDesktopPane.add(getScriptInternalFrame(), null);
      mainDesktopPane.add(getOperatorsInternalFrame(), null);
      mainDesktopPane.add(getFormulaExplorerInternalFrame(), null);
      mainDesktopPane.add(getTinaInternalFrame(), null);
      errorHandler = new StandardErrorHandler(mainDesktopPane, getShowErrorDlg(), getShowErrorDlgMessageTextArea(),
          getShowErrorDlgStacktraceTextArea());

      Prefs prefs = new Prefs();
      tinaController = new TINAController(errorHandler, prefs, getTinaCenterPanel(), getTinaCameraRollREd(), getTinaCameraRollSlider(), getTinaCameraPitchREd(),
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
          getTinaPaletteBrightnessSlider(), getTinaRenderWidthREd(), getTinaRenderHeightREd());

      renderController = new RenderController(errorHandler,
          mainDesktopPane, getRenderDialog(),
          getRenderCancelButton(), getRenderRenderButton(),
          getRenderCloseButton(), getRenderPrefixREd(),
          getRenderOutputREd(), renderFrameValueLabel,
          renderSizeValueLabel, getRenderProgressBar(),
          getRenderPreviewImg1Panel(), renderPreviewImg1Label,
          getRenderPreviewImg2Panel(), renderPreviewImg2Label,
          getRenderPreviewImg3Panel(), renderPreviewImg3Label,
          getRenderFrameStartREd(), getRenderFrameEndREd());

      FormulaExplorerInternalFrame formulaExplorerFrame = (FormulaExplorerInternalFrame) getFormulaExplorerInternalFrame();

      formulaExplorerController = new FormulaExplorerController(
          (FormulaPanel) formulaExplorerFrame.getFormulaPanel(),
          formulaExplorerFrame.getFormulaExplorerFormula1REd(),
          formulaExplorerFrame.getFormulaExplorerFormula2REd(),
          formulaExplorerFrame.getFormulaExplorerFormula3REd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXMinREd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXMaxREd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXCountREd(),
          formulaExplorerFrame.getFormulaExplorerValuesTextArea());

      OperatorsInternalFrame operatorsFrame = (OperatorsInternalFrame) getOperatorsInternalFrame();
      operatorsFrame.setDesktop(this);

      ScriptInternalFrame scriptFrame = (ScriptInternalFrame) getScriptInternalFrame();
      scriptFrame.setDesktop(this);
      scriptFrame.setRenderController(renderController);

      mainController = new MainController(prefs, errorHandler, mainDesktopPane,
          getWindowMenu(), operatorsFrame.getTransformerInputCmb(), operatorsFrame.getTransformerPresetCmb(), operatorsFrame.getCreatorPresetCmb(),
          getShowMessageDlg(), getShowMessageDlgTextArea(), scriptFrame.getScriptTable(),
          scriptFrame.getScriptActionTextArea(), scriptFrame.getScriptFrameSlider(),
          scriptFrame.getScriptFramesREd(), scriptFrame.getScriptFrameREd(),
          scriptFrame.getEnvelopeController(), renderController);
      renderController.setActionList(mainController.getActionList());

      scriptFrame.setMainController(mainController);
      scriptFrame.setOperatorsFrame(operatorsFrame);
      operatorsFrame.setMainController(mainController);
      formulaExplorerFrame.setMainController(mainController);
      formulaExplorerFrame.setFormulaExplorerController(formulaExplorerController);

    }
    return mainDesktopPane;
  }

  /**
   * This method initializes windowMenu
   * 
   * @return javax.swing.JMenu
   */
  private JMenu getWindowMenu() {
    if (windowMenu == null) {
      windowMenu = new JMenu();
      windowMenu.setText("Windows");
      windowMenu.add(getOperatorsMenuItem());
      windowMenu.add(getScriptMenuItem());
      windowMenu.add(getFormulaExplorerMenuItem());
      windowMenu.add(getTinaMenuItem());
    }
    return windowMenu;
  }

  /**
   * This method initializes operatorsMenuItem
   * 
   * @return javax.swing.JCheckBoxMenuItem
   */
  private JCheckBoxMenuItem getOperatorsMenuItem() {
    if (operatorsMenuItem == null) {
      operatorsMenuItem = new JCheckBoxMenuItem();
      operatorsMenuItem.setText("Operators");
      operatorsMenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              operatorsMenuItem_actionPerformed(e);
            }
          });
    }
    return operatorsMenuItem;
  }

  /**
   * This method initializes operatorsMenuItem
   * 
   * @return javax.swing.JCheckBoxMenuItem
   */
  private JCheckBoxMenuItem getTinaMenuItem() {
    if (tinaMenuItem == null) {
      tinaMenuItem = new JCheckBoxMenuItem();
      tinaMenuItem.setText("T.I.N.A.");
      tinaMenuItem.setEnabled(true);
      tinaMenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              tinaMenuItem_actionPerformed(e);
            }
          });
    }
    return tinaMenuItem;
  }

  /**
   * This method initializes openMenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getOpenMenuItem() {
    if (openMenuItem == null) {
      openMenuItem = new JMenuItem();
      openMenuItem.setText("Open...");
      openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
          Event.CTRL_MASK, true));
      openMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          openMenuItem_actionPerformed(e);
        }
      });
    }
    return openMenuItem;
  }

  /**
   * This method initializes openFavourite1MenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getOpenFavourite1MenuItem() {
    if (openFavourite1MenuItem == null) {
      openFavourite1MenuItem = new JMenuItem();
      openFavourite1MenuItem.setText("Favourite 1");
      openFavourite1MenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              openFavourite1MenuItem_actionPerformed(e);
            }
          });
    }
    return openFavourite1MenuItem;
  }

  /**
   * This method initializes openFavourite2MenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getOpenFavourite2MenuItem() {
    if (openFavourite2MenuItem == null) {
      openFavourite2MenuItem = new JMenuItem();
      openFavourite2MenuItem.setText("Favourite 2");
      openFavourite2MenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              openFavourite2MenuItem_actionPerformed(e);
            }
          });
    }
    return openFavourite2MenuItem;
  }

  /**
   * This method initializes openFavourite3MenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getOpenFavourite3MenuItem() {
    if (openFavourite3MenuItem == null) {
      openFavourite3MenuItem = new JMenuItem();
      openFavourite3MenuItem.setText("Favourite 3");
      openFavourite3MenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              openFavourite3MenuItem_actionPerformed(e);
            }
          });
    }
    return openFavourite3MenuItem;
  }

  /**
   * This method initializes showMessageTopPnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowMessageTopPnl() {
    if (showMessageTopPnl == null) {
      showMessageTopPnl = new JPanel();
      showMessageTopPnl.setLayout(new GridBagLayout());
      showMessageTopPnl.setPreferredSize(new Dimension(0, 10));
    }
    return showMessageTopPnl;
  }

  /**
   * This method initializes showMessageBottomPnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowMessageBottomPnl() {
    if (showMessageBottomPnl == null) {
      showMessageBottomPnl = new JPanel();
      showMessageBottomPnl.setLayout(new BorderLayout());
      showMessageBottomPnl.setPreferredSize(new Dimension(0, 50));
      showMessageBottomPnl.setBorder(BorderFactory.createEmptyBorder(10,
          100, 10, 100));
      showMessageBottomPnl.add(getShowMessageCloseButton(),
          BorderLayout.CENTER);
    }
    return showMessageBottomPnl;
  }

  /**
   * This method initializes showMessageLeftPnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowMessageLeftPnl() {
    if (showMessageLeftPnl == null) {
      showMessageLeftPnl = new JPanel();
      showMessageLeftPnl.setLayout(new GridBagLayout());
      showMessageLeftPnl.setPreferredSize(new Dimension(10, 0));
    }
    return showMessageLeftPnl;
  }

  /**
   * This method initializes showMessageRightPnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowMessageRightPnl() {
    if (showMessageRightPnl == null) {
      showMessageRightPnl = new JPanel();
      showMessageRightPnl.setLayout(new GridBagLayout());
      showMessageRightPnl.setPreferredSize(new Dimension(10, 0));
    }
    return showMessageRightPnl;
  }

  /**
   * This method initializes showMessageCloseButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getShowMessageCloseButton() {
    if (showMessageCloseButton == null) {
      showMessageCloseButton = new JButton();
      showMessageCloseButton.setName("showMessageCloseButton");
      showMessageCloseButton.setText("Close");
      showMessageCloseButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              showMessageCloseButton_actionPerformed(e);
            }
          });
    }
    return showMessageCloseButton;
  }

  /**
   * This method initializes showMessageDlg
   * 
   * @return javax.swing.JDialog
   */
  private JDialog getShowMessageDlg() {
    if (showMessageDlg == null) {
      showMessageDlg = new JDialog(getJFrame());
      showMessageDlg.setSize(new Dimension(355, 205));
      showMessageDlg
          .setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      showMessageDlg.setModal(true);
      showMessageDlg.setTitle("System message");
      showMessageDlg.setContentPane(getShowMessageDlgContentPanel());
    }
    return showMessageDlg;
  }

  /**
   * This method initializes showMessageDlgContentPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowMessageDlgContentPanel() {
    if (showMessageDlgContentPanel == null) {
      showMessageDlgContentPanel = new JPanel();
      showMessageDlgContentPanel.setLayout(new BorderLayout());
      showMessageDlgContentPanel.add(getShowMessageTopPnl(),
          BorderLayout.NORTH);
      showMessageDlgContentPanel.add(getShowMessageLeftPnl(),
          BorderLayout.WEST);
      showMessageDlgContentPanel.add(getShowMessageRightPnl(),
          BorderLayout.EAST);
      showMessageDlgContentPanel.add(getShowMessageBottomPnl(),
          BorderLayout.SOUTH);
      showMessageDlgContentPanel.add(getShowMessageDlgScrollPane(),
          BorderLayout.CENTER);
    }
    return showMessageDlgContentPanel;
  }

  /**
   * This method initializes showMessageDlgScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getShowMessageDlgScrollPane() {
    if (showMessageDlgScrollPane == null) {
      showMessageDlgScrollPane = new JScrollPane();
      showMessageDlgScrollPane.setEnabled(false);
      showMessageDlgScrollPane
          .setViewportView(getShowMessageDlgTextArea());
    }
    return showMessageDlgScrollPane;
  }

  /**
   * This method initializes showMessageDlgTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getShowMessageDlgTextArea() {
    if (showMessageDlgTextArea == null) {
      showMessageDlgTextArea = new JTextArea();
      showMessageDlgTextArea.setWrapStyleWord(false);
      showMessageDlgTextArea.setEditable(false);
      showMessageDlgTextArea.setLineWrap(true);
    }
    return showMessageDlgTextArea;
  }

  /**
   * This method initializes showErrorDlg
   * 
   * @return javax.swing.JDialog
   */
  private JDialog getShowErrorDlg() {
    if (showErrorDlg == null) {
      showErrorDlg = new JDialog(getJFrame());
      showErrorDlg.setSize(new Dimension(429, 247));
      showErrorDlg.setTitle("System error");
      showErrorDlg.setContentPane(getShowErrorDlgContentPane());
    }
    return showErrorDlg;
  }

  /**
   * This method initializes showErrorDlgContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowErrorDlgContentPane() {
    if (showErrorDlgContentPane == null) {
      showErrorDlgContentPane = new JPanel();
      showErrorDlgContentPane.setLayout(new BorderLayout());
      showErrorDlgContentPane.add(getShowErrorDlgTopPnl(),
          BorderLayout.NORTH);
      showErrorDlgContentPane.add(getShowErrorDlgBottomPnl(),
          BorderLayout.SOUTH);
      showErrorDlgContentPane.add(getShowErrorDlgLeftPnl(),
          BorderLayout.WEST);
      showErrorDlgContentPane.add(getShowErrorDlgRightPnl(),
          BorderLayout.EAST);
      showErrorDlgContentPane.add(getShowErrorDlgTabbedPane(),
          BorderLayout.CENTER);
    }
    return showErrorDlgContentPane;
  }

  /**
   * This method initializes showErrorDlgTopPnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowErrorDlgTopPnl() {
    if (showErrorDlgTopPnl == null) {
      showErrorDlgTopPnl = new JPanel();
      showErrorDlgTopPnl.setLayout(new GridBagLayout());
      showErrorDlgTopPnl.setPreferredSize(new Dimension(0, 10));
    }
    return showErrorDlgTopPnl;
  }

  /**
   * This method initializes showErrorDlgBottomPnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowErrorDlgBottomPnl() {
    if (showErrorDlgBottomPnl == null) {
      showErrorDlgBottomPnl = new JPanel();
      showErrorDlgBottomPnl.setLayout(new BorderLayout());
      showErrorDlgBottomPnl.setPreferredSize(new Dimension(0, 50));
      showErrorDlgBottomPnl.setBorder(BorderFactory.createEmptyBorder(10,
          120, 10, 120));
      showErrorDlgBottomPnl.add(getShowErrorCloseButton(),
          BorderLayout.CENTER);
    }
    return showErrorDlgBottomPnl;
  }

  /**
   * This method initializes showErrorDlgLeftPnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowErrorDlgLeftPnl() {
    if (showErrorDlgLeftPnl == null) {
      showErrorDlgLeftPnl = new JPanel();
      showErrorDlgLeftPnl.setLayout(new GridBagLayout());
      showErrorDlgLeftPnl.setPreferredSize(new Dimension(10, 0));
    }
    return showErrorDlgLeftPnl;
  }

  /**
   * This method initializes showErrorDlgRightPnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowErrorDlgRightPnl() {
    if (showErrorDlgRightPnl == null) {
      showErrorDlgRightPnl = new JPanel();
      showErrorDlgRightPnl.setLayout(new GridBagLayout());
      showErrorDlgRightPnl.setPreferredSize(new Dimension(10, 0));
    }
    return showErrorDlgRightPnl;
  }

  /**
   * This method initializes showErrorDlgTabbedPane
   * 
   * @return javax.swing.JTabbedPane
   */
  private JTabbedPane getShowErrorDlgTabbedPane() {
    if (showErrorDlgTabbedPane == null) {
      showErrorDlgTabbedPane = new JTabbedPane();
      showErrorDlgTabbedPane.addTab("Message", null,
          getShowErrorDlgMessagePnl(), null);
      showErrorDlgTabbedPane.addTab("Stacktrace", null,
          getShowErrorDlgStacktracePnl(), null);
    }
    return showErrorDlgTabbedPane;
  }

  /**
   * This method initializes showErrorDlgMessagePnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowErrorDlgMessagePnl() {
    if (showErrorDlgMessagePnl == null) {
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.BOTH;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.gridx = 0;
      showErrorDlgMessagePnl = new JPanel();
      showErrorDlgMessagePnl.setLayout(new GridBagLayout());
      showErrorDlgMessagePnl.add(getShowErrorDlgMessageScrollPane(),
          gridBagConstraints);
    }
    return showErrorDlgMessagePnl;
  }

  /**
   * This method initializes showErrorDlgStacktracePnl
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getShowErrorDlgStacktracePnl() {
    if (showErrorDlgStacktracePnl == null) {
      GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.fill = GridBagConstraints.BOTH;
      gridBagConstraints1.gridy = 0;
      gridBagConstraints1.weightx = 1.0;
      gridBagConstraints1.weighty = 1.0;
      gridBagConstraints1.gridx = 0;
      showErrorDlgStacktracePnl = new JPanel();
      showErrorDlgStacktracePnl.setLayout(new GridBagLayout());
      showErrorDlgStacktracePnl.add(
          getShowErrorDlgStacktraceScrollPane(), gridBagConstraints1);
    }
    return showErrorDlgStacktracePnl;
  }

  /**
   * This method initializes showErrorDlgMessageScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getShowErrorDlgMessageScrollPane() {
    if (showErrorDlgMessageScrollPane == null) {
      showErrorDlgMessageScrollPane = new JScrollPane();
      showErrorDlgMessageScrollPane
          .setViewportView(getShowErrorDlgMessageTextArea());
    }
    return showErrorDlgMessageScrollPane;
  }

  /**
   * This method initializes showErrorDlgStacktraceScrollPane
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getShowErrorDlgStacktraceScrollPane() {
    if (showErrorDlgStacktraceScrollPane == null) {
      showErrorDlgStacktraceScrollPane = new JScrollPane();
      showErrorDlgStacktraceScrollPane
          .setViewportView(getShowErrorDlgStacktraceTextArea());
    }
    return showErrorDlgStacktraceScrollPane;
  }

  /**
   * This method initializes showErrorDlgMessageTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getShowErrorDlgMessageTextArea() {
    if (showErrorDlgMessageTextArea == null) {
      showErrorDlgMessageTextArea = new JTextArea();
      showErrorDlgMessageTextArea.setEditable(false);
      showErrorDlgMessageTextArea.setLineWrap(true);
      showErrorDlgMessageTextArea.setWrapStyleWord(true);
    }
    return showErrorDlgMessageTextArea;
  }

  /**
   * This method initializes showErrorDlgStacktraceTextArea
   * 
   * @return javax.swing.JTextArea
   */
  private JTextArea getShowErrorDlgStacktraceTextArea() {
    if (showErrorDlgStacktraceTextArea == null) {
      showErrorDlgStacktraceTextArea = new JTextArea();
      showErrorDlgStacktraceTextArea.setEditable(false);
    }
    return showErrorDlgStacktraceTextArea;
  }

  /**
   * This method initializes showErrorCloseButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getShowErrorCloseButton() {
    if (showErrorCloseButton == null) {
      showErrorCloseButton = new JButton();
      showErrorCloseButton.setText("Close");
      showErrorCloseButton.setName("showMessageCloseButton");
      showErrorCloseButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              showErrorCloseButton_actionPerformed(e);
            }
          });
    }
    return showErrorCloseButton;
  }

  /**
  /**
   * This method initializes openFavourite4MenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getOpenFavourite4MenuItem() {
    if (openFavourite4MenuItem == null) {
      openFavourite4MenuItem = new JMenuItem();
      openFavourite4MenuItem.setText("Favourite 4");
      openFavourite4MenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              openFavourite4MenuItem_actionPerformed(e);
            }
          });
    }
    return openFavourite4MenuItem;
  }

  /**
   * This method initializes openFavourite5MenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getOpenFavourite5MenuItem() {
    if (openFavourite5MenuItem == null) {
      openFavourite5MenuItem = new JMenuItem();
      openFavourite5MenuItem.setText("Favourite 5");
      openFavourite5MenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              openFavourite5MenuItem_actionPerformed(e);
            }
          });
    }
    return openFavourite5MenuItem;
  }

  /**
   * This method initializes openFavourite6MenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getOpenFavourite6MenuItem() {
    if (openFavourite6MenuItem == null) {
      openFavourite6MenuItem = new JMenuItem();
      openFavourite6MenuItem.setText("Favourite 6");
      openFavourite6MenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              openFavourite6MenuItem_actionPerformed(e);
            }
          });
    }
    return openFavourite6MenuItem;
  }

  /**
   * This method initializes openFavourite7MenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getOpenFavourite7MenuItem() {
    if (openFavourite7MenuItem == null) {
      openFavourite7MenuItem = new JMenuItem();
      openFavourite7MenuItem.setText("Favourite 7");
      openFavourite7MenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              openFavourite7MenuItem_actionPerformed(e);
            }
          });
    }
    return openFavourite7MenuItem;
  }

  /**
   * This method initializes closeAllMenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getCloseAllMenuItem() {
    if (closeAllMenuItem == null) {
      closeAllMenuItem = new JMenuItem();
      closeAllMenuItem.setText("Close all");
      closeAllMenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              closeAllMenuItem_actionPerformed(e);
            }
          });
    }
    return closeAllMenuItem;
  }

  /**
   * This method initializes scriptInternalFrame
   * 
   * @return javax.swing.JInternalFrame
   */
  private JInternalFrame getScriptInternalFrame() {
    if (scriptInternalFrame == null) {
      scriptInternalFrame = new ScriptInternalFrame();
      scriptInternalFrame.setBounds(new Rectangle(385, 52, 792, 495));
      scriptInternalFrame.setTitle("Script");
      scriptInternalFrame.setResizable(true);
      scriptInternalFrame.setMaximizable(true);
      scriptInternalFrame.setIconifiable(true);
      scriptInternalFrame.setClosable(true);
      scriptInternalFrame.setVisible(true);
      scriptInternalFrame
          .setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      scriptInternalFrame
          .addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameDeactivated(
                javax.swing.event.InternalFrameEvent e) {
              scriptInternalFrame_internalFrameDeactivated(e);
            }

            public void internalFrameClosed(
                javax.swing.event.InternalFrameEvent e) {
              scriptInternalFrame_internalFrameClosed(e);
            }
          });
    }
    return scriptInternalFrame;
  }

  /**
   * This method initializes scriptMenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JCheckBoxMenuItem getScriptMenuItem() {
    if (scriptMenuItem == null) {
      scriptMenuItem = new JCheckBoxMenuItem();
      scriptMenuItem.setText("Script");
      scriptMenuItem.setSelected(true);
      scriptMenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              scriptMenuItem_actionPerformed(e);
            }
          });
    }
    return scriptMenuItem;
  }

  /**
  * This method initializes operatorsInternalFrame
  * 
  * @return javax.swing.JInternalFrame
  */
  private JInternalFrame getOperatorsInternalFrame() {
    if (operatorsInternalFrame == null) {
      operatorsInternalFrame = new OperatorsInternalFrame();
      operatorsInternalFrame.setBounds(new Rectangle(813, 14, 355, 652));
      operatorsInternalFrame
          .setTitle("Operators (<F2>/<DblClick> to edit)");
      operatorsInternalFrame
          .setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      operatorsInternalFrame.setClosable(true);
      operatorsInternalFrame.setIconifiable(true);
      operatorsInternalFrame.setVisible(true);
      operatorsInternalFrame.setResizable(true);
      operatorsInternalFrame
          .addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameDeactivated(
                javax.swing.event.InternalFrameEvent e) {
              operatorsInternalFrame_internalFrameDeactivated(e);
            }

            public void internalFrameClosed(
                javax.swing.event.InternalFrameEvent e) {
              operatorsInternalFrame_internalFrameClosed(e);
            }
          });
    }
    return operatorsInternalFrame;
  }

  /**
  * This method initializes renderDialog
  * 
  * @return javax.swing.JDialog
  */
  private JDialog getRenderDialog() {
    if (renderDialog == null) {
      renderDialog = new JDialog(getJFrame());
      renderDialog.setSize(new Dimension(597, 359));
      renderDialog.setTitle("Render Script");
      renderDialog.setModal(true);
      renderDialog.setContentPane(getRenderWindowContentPane());
    }
    return renderDialog;
  }

  /**
   * This method initializes renderWindowContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderWindowContentPane() {
    if (renderWindowContentPane == null) {
      renderWindowContentPane = new JPanel();
      renderWindowContentPane.setLayout(new BorderLayout());
      renderWindowContentPane
          .add(getRenderTopPanel(), BorderLayout.NORTH);
      renderWindowContentPane.add(getRenderBottomPanel(),
          BorderLayout.SOUTH);
      renderWindowContentPane.add(getRenderCenterPanel(),
          BorderLayout.CENTER);
    }
    return renderWindowContentPane;
  }

  /**
   * This method initializes renderTopPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderTopPanel() {
    if (renderTopPanel == null) {
      renderFrameEndLabel = new JLabel();
      renderFrameEndLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      renderFrameEndLabel.setText("End");
      renderFrameEndLabel.setSize(new Dimension(50, 26));
      renderFrameEndLabel.setLocation(new Point(226, 36));
      renderFrameEndLabel.setPreferredSize(new Dimension(50, 26));
      renderFrameStartLabel = new JLabel();
      renderFrameStartLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      renderFrameStartLabel.setText("Start");
      renderFrameStartLabel.setLocation(new Point(5, 36));
      renderFrameStartLabel.setSize(new Dimension(50, 26));
      renderFrameStartLabel.setPreferredSize(new Dimension(50, 26));
      renderPrefixLabel = new JLabel();
      renderPrefixLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      renderPrefixLabel.setText("Prefix");
      renderPrefixLabel.setLocation(new Point(438, 5));
      renderPrefixLabel.setSize(new Dimension(50, 26));
      renderPrefixLabel.setPreferredSize(new Dimension(50, 26));
      renderOutputLabel = new JLabel();
      renderOutputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      renderOutputLabel.setText("Output");
      renderOutputLabel.setLocation(new Point(5, 5));
      renderOutputLabel.setSize(new Dimension(50, 26));
      renderOutputLabel.setPreferredSize(new Dimension(50, 26));
      renderTopPanel = new JPanel();
      renderTopPanel.setLayout(null);
      renderTopPanel.setPreferredSize(new Dimension(0, 68));
      renderTopPanel.add(renderOutputLabel, null);
      renderTopPanel.add(getRenderOutputREd(), null);
      renderTopPanel.add(renderPrefixLabel, null);
      renderTopPanel.add(getRenderPrefixREd(), null);
      renderTopPanel.add(renderFrameStartLabel, null);
      renderTopPanel.add(getRenderFrameStartREd(), null);
      renderTopPanel.add(renderFrameEndLabel, null);
      renderTopPanel.add(getRenderFrameEndREd(), null);
    }
    return renderTopPanel;
  }

  /**
   * This method initializes renderBottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderBottomPanel() {
    if (renderBottomPanel == null) {
      renderBottomPanel = new JPanel();
      renderBottomPanel.setLayout(null);
      renderBottomPanel.setPreferredSize(new Dimension(0, 36));
      renderBottomPanel.add(getRenderRenderButton(), null);
      renderBottomPanel.add(getRenderCancelButton(), null);
      renderBottomPanel.add(getRenderCloseButton(), null);
    }
    return renderBottomPanel;
  }

  /**
   * This method initializes renderCenterPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderCenterPanel() {
    if (renderCenterPanel == null) {
      renderCenterPanel = new JPanel();
      renderCenterPanel.setLayout(new BorderLayout());
      renderCenterPanel.add(getRenderPreviewBottomPanel(),
          BorderLayout.SOUTH);
      renderCenterPanel.add(getRenderPreviewMainPanel(),
          BorderLayout.CENTER);
    }
    return renderCenterPanel;
  }

  /**
   * This method initializes renderRenderButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getRenderRenderButton() {
    if (renderRenderButton == null) {
      renderRenderButton = new JButton();
      renderRenderButton.setText("Render");
      renderRenderButton.setSize(new Dimension(120, 24));
      renderRenderButton.setLocation(new Point(5, 5));
      renderRenderButton.setPreferredSize(new Dimension(120, 26));
      renderRenderButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              try {
                renderController.render();
              }
              catch (Exception ex) {
                mainController.handleError(ex);
              }
            }
          });
    }
    return renderRenderButton;
  }

  /**
   * This method initializes renderCancelButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getRenderCancelButton() {
    if (renderCancelButton == null) {
      renderCancelButton = new JButton();
      renderCancelButton.setText("Cancel");
      renderCancelButton.setSize(new Dimension(120, 24));
      renderCancelButton.setLocation(new Point(233, 5));
      renderCancelButton.setPreferredSize(new Dimension(120, 26));
      renderCancelButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              renderController.cancel();
            }
          });
    }
    return renderCancelButton;
  }

  /**
   * This method initializes renderCloseButton
   * 
   * @return javax.swing.JButton
   */
  private JButton getRenderCloseButton() {
    if (renderCloseButton == null) {
      renderCloseButton = new JButton();
      renderCloseButton.setText("Close");
      renderCloseButton.setLocation(new Point(455, 5));
      renderCloseButton.setSize(new Dimension(120, 24));
      renderCloseButton.setPreferredSize(new Dimension(120, 26));
      renderCloseButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              renderController.close();
            }
          });
    }
    return renderCloseButton;
  }

  /**
   * This method initializes renderPreviewBottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewBottomPanel() {
    if (renderPreviewBottomPanel == null) {
      renderPreviewBottomPanel = new JPanel();
      renderPreviewBottomPanel.setLayout(new BorderLayout());
      renderPreviewBottomPanel.setPreferredSize(new Dimension(0, 64));
      renderPreviewBottomPanel.add(getRenderPreviewProgressTopPanel(),
          BorderLayout.NORTH);
      renderPreviewBottomPanel.add(getRenderPreviewProgressMainPanel(),
          BorderLayout.CENTER);
    }
    return renderPreviewBottomPanel;
  }

  /**
   * This method initializes renderPreviewMainPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewMainPanel() {
    if (renderPreviewMainPanel == null) {
      GridLayout gridLayout = new GridLayout(0, 3);
      gridLayout.setRows(0);
      gridLayout.setHgap(5);
      gridLayout.setVgap(0);
      gridLayout.setColumns(3);
      renderPreviewMainPanel = new JPanel();
      renderPreviewMainPanel.setBorder(BorderFactory.createEmptyBorder(0,
          5, 0, 5));
      renderPreviewMainPanel.setLayout(gridLayout);
      renderPreviewMainPanel.add(getRenderPreviewImg1Panel(), null);
      renderPreviewMainPanel.add(getRenderPreviewImg2Panel(), null);
      renderPreviewMainPanel.add(getRenderPreviewImg3Panel(), null);
    }
    return renderPreviewMainPanel;
  }

  /**
   * This method initializes renderPreviewProgressTopPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewProgressTopPanel() {
    if (renderPreviewProgressTopPanel == null) {
      renderSizeValueLabel = new JLabel();
      renderSizeValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      renderSizeValueLabel.setText("320x256");
      renderSizeValueLabel.setLocation(new Point(221, 5));
      renderSizeValueLabel.setSize(new Dimension(80, 26));
      renderSizeValueLabel.setPreferredSize(new Dimension(80, 26));
      renderSizeLabel = new JLabel();
      renderSizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      renderSizeLabel.setText("Size");
      renderSizeLabel.setLocation(new Point(167, 5));
      renderSizeLabel.setSize(new Dimension(50, 26));
      renderSizeLabel.setPreferredSize(new Dimension(50, 26));
      renderFrameValueLabel = new JLabel();
      renderFrameValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      renderFrameValueLabel.setText("1/60");
      renderFrameValueLabel.setLocation(new Point(58, 5));
      renderFrameValueLabel.setSize(new Dimension(80, 26));
      renderFrameValueLabel.setPreferredSize(new Dimension(80, 26));
      renderFrameLabel = new JLabel();
      renderFrameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      renderFrameLabel.setText("Frame");
      renderFrameLabel.setLocation(new Point(5, 5));
      renderFrameLabel.setSize(new Dimension(50, 26));
      renderFrameLabel.setPreferredSize(new Dimension(50, 26));
      renderPreviewProgressTopPanel = new JPanel();
      renderPreviewProgressTopPanel.setLayout(null);
      renderPreviewProgressTopPanel
          .setPreferredSize(new Dimension(0, 36));
      renderPreviewProgressTopPanel.add(renderFrameLabel, null);
      renderPreviewProgressTopPanel.add(renderFrameValueLabel, null);
      renderPreviewProgressTopPanel.add(renderSizeLabel, null);
      renderPreviewProgressTopPanel.add(renderSizeValueLabel, null);
    }
    return renderPreviewProgressTopPanel;
  }

  /**
   * This method initializes renderPreviewProgressMainPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewProgressMainPanel() {
    if (renderPreviewProgressMainPanel == null) {
      renderPreviewProgressMainPanel = new JPanel();
      renderPreviewProgressMainPanel.setLayout(new BorderLayout());
      renderPreviewProgressMainPanel.setBorder(BorderFactory
          .createEmptyBorder(5, 5, 5, 5));
      renderPreviewProgressMainPanel.setPreferredSize(new Dimension(158,
          34));
      renderPreviewProgressMainPanel.add(getRenderProgressBar(),
          BorderLayout.CENTER);
    }
    return renderPreviewProgressMainPanel;
  }

  /**
   * This method initializes renderProgressBar
   * 
   * @return javax.swing.JProgressBar
   */
  private JProgressBar getRenderProgressBar() {
    if (renderProgressBar == null) {
      renderProgressBar = new JProgressBar();
      renderProgressBar.setValue(33);
      renderProgressBar.setStringPainted(true);
    }
    return renderProgressBar;
  }

  /**
   * This method initializes renderOutputREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getRenderOutputREd() {
    if (renderOutputREd == null) {
      renderOutputREd = new JTextField();
      renderOutputREd.setText("C:\\TMP\\wf\\render");
      renderOutputREd.setLocation(new Point(58, 5));
      renderOutputREd.setSize(new Dimension(294, 26));
      renderOutputREd.setPreferredSize(new Dimension(294, 26));
    }
    return renderOutputREd;
  }

  /**
   * This method initializes renderPrefixREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getRenderPrefixREd() {
    if (renderPrefixREd == null) {
      renderPrefixREd = new JTextField();
      renderPrefixREd.setText("Img");
      renderPrefixREd.setLocation(new Point(490, 5));
      renderPrefixREd.setSize(new Dimension(86, 26));
      renderPrefixREd.setPreferredSize(new Dimension(86, 26));
    }
    return renderPrefixREd;
  }

  /**
   * This method initializes renderPreviewImg1Panel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewImg1Panel() {
    if (renderPreviewImg1Panel == null) {
      renderPreviewImg1Panel = new JPanel();
      renderPreviewImg1Panel.setLayout(new BorderLayout());
      renderPreviewImg1Panel.setBorder(new SoftBevelBorder(
          SoftBevelBorder.RAISED));
      renderPreviewImg1Panel.add(getRenderPreviewImg1BottomPanel(),
          BorderLayout.SOUTH);
    }
    return renderPreviewImg1Panel;
  }

  /**
   * This method initializes renderPreviewImg2Panel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewImg2Panel() {
    if (renderPreviewImg2Panel == null) {
      renderPreviewImg2Panel = new JPanel();
      renderPreviewImg2Panel.setLayout(new BorderLayout());
      renderPreviewImg2Panel.setBorder(new SoftBevelBorder(
          SoftBevelBorder.RAISED));
      renderPreviewImg2Panel.add(getRenderPreviewImg2BottomPanel(),
          BorderLayout.SOUTH);
    }
    return renderPreviewImg2Panel;
  }

  /**
   * This method initializes renderPreviewImg3Panel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewImg3Panel() {
    if (renderPreviewImg3Panel == null) {
      renderPreviewImg3Panel = new JPanel();
      renderPreviewImg3Panel.setLayout(new BorderLayout());
      renderPreviewImg3Panel.setBorder(new SoftBevelBorder(
          SoftBevelBorder.RAISED));
      renderPreviewImg3Panel.add(getRenderPreviewImg3BottomPanel(),
          BorderLayout.SOUTH);
    }
    return renderPreviewImg3Panel;
  }

  /**
   * This method initializes renderPreviewImg1BottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewImg1BottomPanel() {
    if (renderPreviewImg1BottomPanel == null) {
      renderPreviewImg1Label = new JLabel();
      renderPreviewImg1Label.setText("Img0001");
      renderPreviewImg1Label
          .setHorizontalAlignment(SwingConstants.CENTER);
      renderPreviewImg1BottomPanel = new JPanel();
      renderPreviewImg1BottomPanel.setLayout(new BorderLayout());
      renderPreviewImg1BottomPanel.setPreferredSize(new Dimension(0, 20));
      renderPreviewImg1BottomPanel.add(renderPreviewImg1Label,
          BorderLayout.CENTER);
    }
    return renderPreviewImg1BottomPanel;
  }

  /**
   * This method initializes renderPreviewImg2BottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewImg2BottomPanel() {
    if (renderPreviewImg2BottomPanel == null) {
      renderPreviewImg2Label = new JLabel();
      renderPreviewImg2Label
          .setHorizontalAlignment(SwingConstants.CENTER);
      renderPreviewImg2Label.setText("Img0002");
      renderPreviewImg2BottomPanel = new JPanel();
      renderPreviewImg2BottomPanel.setLayout(new BorderLayout());
      renderPreviewImg2BottomPanel.setPreferredSize(new Dimension(0, 20));
      renderPreviewImg2BottomPanel.add(renderPreviewImg2Label,
          BorderLayout.CENTER);
    }
    return renderPreviewImg2BottomPanel;
  }

  /**
   * This method initializes renderPreviewImg3BottomPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getRenderPreviewImg3BottomPanel() {
    if (renderPreviewImg3BottomPanel == null) {
      renderPreviewImg3Label = new JLabel();
      renderPreviewImg3Label
          .setHorizontalAlignment(SwingConstants.CENTER);
      renderPreviewImg3Label.setText("Img0003");
      renderPreviewImg3BottomPanel = new JPanel();
      renderPreviewImg3BottomPanel.setLayout(new BorderLayout());
      renderPreviewImg3BottomPanel.setPreferredSize(new Dimension(0, 20));
      renderPreviewImg3BottomPanel.add(renderPreviewImg3Label,
          BorderLayout.CENTER);
    }
    return renderPreviewImg3BottomPanel;
  }

  /**
   * This method initializes renderFrameStartREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getRenderFrameStartREd() {
    if (renderFrameStartREd == null) {
      renderFrameStartREd = new JTextField();
      renderFrameStartREd.setText("1");
      renderFrameStartREd.setSize(new Dimension(72, 26));
      renderFrameStartREd.setLocation(new Point(58, 36));
      renderFrameStartREd.setPreferredSize(new Dimension(72, 26));
      renderFrameStartREd
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              renderController.frameStartChanged();
            }
          });
      renderFrameStartREd
          .addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
              renderController.frameStartChanged();
            }
          });
    }
    return renderFrameStartREd;
  }

  /**
   * This method initializes renderFrameEndREd
   * 
   * @return javax.swing.JTextField
   */
  private JTextField getRenderFrameEndREd() {
    if (renderFrameEndREd == null) {
      renderFrameEndREd = new JTextField();
      renderFrameEndREd.setText("60");
      renderFrameEndREd.setLocation(new Point(279, 36));
      renderFrameEndREd.setSize(new Dimension(72, 26));
      renderFrameEndREd.setPreferredSize(new Dimension(72, 26));
      renderFrameEndREd
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              renderController.frameEndChanged();
            }
          });
      renderFrameEndREd
          .addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
              renderController.frameEndChanged();
            }
          });
    }
    return renderFrameEndREd;
  }

  /**
   * This method initializes formulaExplorerInternalFrame
   * 
   * @return javax.swing.JInternalFrame
   */
  private JInternalFrame getFormulaExplorerInternalFrame() {
    if (formulaExplorerInternalFrame == null) {
      formulaExplorerInternalFrame = new FormulaExplorerInternalFrame();
      formulaExplorerInternalFrame.setResizable(true);
      formulaExplorerInternalFrame.setClosable(true);
      formulaExplorerInternalFrame.setIconifiable(true);
      formulaExplorerInternalFrame
          .setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      formulaExplorerInternalFrame.setVisible(false);
      formulaExplorerInternalFrame
          .addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameDeactivated(
                javax.swing.event.InternalFrameEvent e) {
              enableControls();
            }

            public void internalFrameClosed(
                javax.swing.event.InternalFrameEvent e) {
              enableControls();
            }
          });
    }
    return formulaExplorerInternalFrame;
  }

  /**
   * This method initializes formulaExplorerMenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getFormulaExplorerMenuItem() {
    if (formulaExplorerMenuItem == null) {
      formulaExplorerMenuItem = new JCheckBoxMenuItem();
      formulaExplorerMenuItem.setActionCommand("Formula Explorer");
      formulaExplorerMenuItem.setSelected(true);
      formulaExplorerMenuItem.setText("Formula Explorer");
      formulaExplorerMenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              if (formulaExplorerMenuItem.isSelected()) {
                formulaExplorerInternalFrame.setVisible(true);
                try {
                  formulaExplorerInternalFrame
                      .setSelected(true);
                }
                catch (PropertyVetoException ex) {
                  ex.printStackTrace();
                }
              }
              else {
                formulaExplorerInternalFrame.setVisible(false);
              }
            }
          });
    }
    return formulaExplorerMenuItem;
  }

  /**
   * This method initializes tinaInternalFrame	
   * 	
   * @return javax.swing.JInternalFrame	
   */
  private JInternalFrame getTinaInternalFrame() {
    if (tinaInternalFrame == null) {
      tinaInternalFrame = new JInternalFrame();
      tinaInternalFrame.setBounds(new Rectangle(3, 41, 1039, 665));
      tinaInternalFrame.setClosable(true);
      tinaInternalFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      tinaInternalFrame.setIconifiable(true);
      tinaInternalFrame.setTitle("T.I.N.A.");
      tinaInternalFrame.setVisible(true);
      tinaInternalFrame.setResizable(true);
      tinaInternalFrame.setContentPane(getTinaContentPane1());
      tinaInternalFrame
          .addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameDeactivated(
                javax.swing.event.InternalFrameEvent e) {
              tinaInternalFrame_internalFrameDeactivated(e);
            }

            public void internalFrameClosed(
                javax.swing.event.InternalFrameEvent e) {
              tinaInternalFrame_internalFrameClosed(e);
            }
          });
      tinaInternalFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentResized(java.awt.event.ComponentEvent e) {
          tinaController.refreshFlameImage();
        }
      });
    }
    return tinaInternalFrame;
  }

  /**
   * This method initializes tinaContentPane1	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getTinaContentPane1() {
    if (tinaContentPane1 == null) {
      tinaContentPane1 = new JPanel();
      tinaContentPane1.setLayout(new BorderLayout());
      tinaContentPane1.add(getTinaNorthPanel(), BorderLayout.NORTH);
      tinaContentPane1.add(getTinaWestPanel(), BorderLayout.WEST);
      tinaContentPane1.add(getTinaEastPanel(), BorderLayout.EAST);
      tinaContentPane1.add(getTinaSouthPanel(), BorderLayout.SOUTH);
      tinaContentPane1.add(getTinaCenterPanel(), BorderLayout.CENTER);
    }
    return tinaContentPane1;
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
      tinaEastPanel.setPreferredSize(new Dimension(320, 0));
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
      tinaAddTransformationButton.setBounds(new Rectangle(6, 4, 70, 26));
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
      tinaTransformationsPanel.setLayout(null);
      tinaTransformationsPanel.setToolTipText("");
      tinaTransformationsPanel.add(getTinaAddTransformationButton(), null);
      tinaTransformationsPanel.add(getTinaResetTransformationButton(), null);
      tinaTransformationsPanel.add(getTinaDeleteTransformationButton(), null);
      tinaTransformationsPanel.add(getTinaDuplicateTransformationButton(), null);
      tinaTransformationsPanel.add(getTinaTransformationsScrollPane(), null);
      tinaTransformationsPanel.add(getTinaTransformationsTabbedPane(), null);
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
   * This method initializes tinaResetTransformationButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getTinaResetTransformationButton() {
    if (tinaResetTransformationButton == null) {
      tinaResetTransformationButton = new JButton();
      tinaResetTransformationButton.setBounds(new Rectangle(246, 4, 74, 26));
      tinaResetTransformationButton.setText("Reset");
    }
    return tinaResetTransformationButton;
  }

  /**
   * This method initializes tinaDeleteTransformationButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getTinaDeleteTransformationButton() {
    if (tinaDeleteTransformationButton == null) {
      tinaDeleteTransformationButton = new JButton();
      tinaDeleteTransformationButton.setBounds(new Rectangle(78, 4, 75, 26));
      tinaDeleteTransformationButton.setText("Delete");
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
      tinaDuplicateTransformationButton.setBounds(new Rectangle(155, 4, 89, 26));
      tinaDuplicateTransformationButton.setText("Duplicate");
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
      tinaTransformationsScrollPane.setBounds(new Rectangle(13, 46, 285, 72));
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
      tinaTransformationsTabbedPane.setBounds(new Rectangle(8, 124, 301, 170));
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
      tinaAffineTransformationPanel = new JPanel();
      tinaAffineTransformationPanel.setLayout(null);
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

  /**
   * @param args
   */
  public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setLookAndFeel();
        Desktop application = new Desktop();
        application.getJFrame().setVisible(true);
        application.initApp();
      }
    });
  }

  /**
   * This method initializes jFrame
   * 
   * @return javax.swing.JFrame
   */
  private JFrame getJFrame() {
    if (jFrame == null) {
      jFrame = new JFrame();
      jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jFrame.setJMenuBar(getMainJMenuBar());
      jFrame.setSize(1280, 728);
      jFrame.setContentPane(getJContentPane());
      jFrame.setTitle(Tools.APP_TITLE + " " + Tools.APP_VERSION);
    }
    return jFrame;
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
      jContentPane.add(getMainDesktopPane(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes mainJMenuBar
   * 
   * @return javax.swing.JMenuBar
   */
  private JMenuBar getMainJMenuBar() {
    if (mainJMenuBar == null) {
      mainJMenuBar = new JMenuBar();
      mainJMenuBar.add(getFileMenu());
      mainJMenuBar.add(getWindowMenu());
      mainJMenuBar.add(getHelpMenu());
    }
    return mainJMenuBar;
  }

  /**
   * This method initializes jMenu
   * 
   * @return javax.swing.JMenu
   */
  private JMenu getFileMenu() {
    if (fileMenu == null) {
      fileMenu = new JMenu();
      fileMenu.setText("File");
      fileMenu.add(getOpenMenuItem());
      fileMenu.add(getSaveMenuItem());
      fileMenu.add(getOpenFavourite1MenuItem());
      fileMenu.add(getOpenFavourite2MenuItem());
      fileMenu.add(getOpenFavourite3MenuItem());
      fileMenu.add(getOpenFavourite4MenuItem());
      fileMenu.add(getOpenFavourite5MenuItem());
      fileMenu.add(getOpenFavourite6MenuItem());
      fileMenu.add(getOpenFavourite7MenuItem());
      fileMenu.add(getCloseAllMenuItem());
      fileMenu.add(getExitMenuItem());
    }
    return fileMenu;
  }

  /**
   * This method initializes jMenu
   * 
   * @return javax.swing.JMenu
   */
  private JMenu getHelpMenu() {
    if (helpMenu == null) {
      helpMenu = new JMenu();
      helpMenu.setText("Help");
      helpMenu.add(getAboutMenuItem());
    }
    return helpMenu;
  }

  /**
   * This method initializes jMenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getExitMenuItem() {
    if (exitMenuItem == null) {
      exitMenuItem = new JMenuItem();
      exitMenuItem.setText("Exit");
      exitMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.exit(0);
        }
      });
    }
    return exitMenuItem;
  }

  /**
   * This method initializes jMenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getAboutMenuItem() {
    if (aboutMenuItem == null) {
      aboutMenuItem = new JMenuItem();
      aboutMenuItem.setText("About");
    }
    return aboutMenuItem;
  }

  /**
   * This method initializes jMenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JMenuItem getSaveMenuItem() {
    if (saveMenuItem == null) {
      saveMenuItem = new JMenuItem();
      saveMenuItem.setText("Save...");
      saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
          Event.CTRL_MASK, true));
      saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          saveMenuItem_actionPerformed(e);
        }
      });
    }
    return saveMenuItem;
  }

  /*-------------------------------------------------------------------------------*/
  /* applet code */
  /*-------------------------------------------------------------------------------*/
  Desktop appletApplication = null;

  @Override
  public void start() {
    setLookAndFeel();
    appletApplication = new Desktop();
    appletApplication.getJFrame().setVisible(true);
    appletApplication.initApp();
    super.start();
  }

  private static void setLookAndFeel() {
    try {
      // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");

      // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      // MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
      // MetalLookAndFeel.setCurrentTheme(new OceanTheme());
      UIManager
          .setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void stop() {
    appletApplication.getJFrame().setVisible(false);
    appletApplication = null;
    super.stop();
  }

  /*-------------------------------------------------------------------------------*/
  /* custom code */
  /*-------------------------------------------------------------------------------*/
  private MainController mainController = null; // @jve:decl-index=0:
  private RenderController renderController = null; // @jve:decl-index=0:
  private FormulaExplorerController formulaExplorerController = null; // @jve:decl-index=0:
  private TINAController tinaController = null; // @jve:decl-index=0:

  private JMenuItem openFavourite1MenuItem = null;

  private JMenuItem openFavourite2MenuItem = null;

  private JMenuItem openFavourite3MenuItem = null;

  private JPanel showMessageTopPnl = null;

  private JPanel showMessageBottomPnl = null;

  private JPanel showMessageLeftPnl = null;

  private JPanel showMessageRightPnl = null;

  private JButton showMessageCloseButton = null;

  private JDialog showMessageDlg = null; // @jve:decl-index=0:visual-constraint="1444,65"

  private JPanel showMessageDlgContentPanel = null;

  private JScrollPane showMessageDlgScrollPane = null;

  private JTextArea showMessageDlgTextArea = null;

  private JDialog showErrorDlg = null; // @jve:decl-index=0:visual-constraint="1805,12"

  private JPanel showErrorDlgContentPane = null;

  private JPanel showErrorDlgTopPnl = null;

  private JPanel showErrorDlgBottomPnl = null;

  private JPanel showErrorDlgLeftPnl = null;

  private JPanel showErrorDlgRightPnl = null;

  private JTabbedPane showErrorDlgTabbedPane = null;

  private JPanel showErrorDlgMessagePnl = null;

  private JPanel showErrorDlgStacktracePnl = null;

  private JScrollPane showErrorDlgMessageScrollPane = null;

  private JScrollPane showErrorDlgStacktraceScrollPane = null;

  private JTextArea showErrorDlgMessageTextArea = null;

  private JTextArea showErrorDlgStacktraceTextArea = null;

  private JButton showErrorCloseButton = null;

  private void operatorsMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (operatorsMenuItem.isSelected()) {
      operatorsInternalFrame.setVisible(true);
      try {
        operatorsInternalFrame.setSelected(true);
      }
      catch (PropertyVetoException ex) {
        ex.printStackTrace();
      }
    }
    else {
      operatorsInternalFrame.setVisible(false);
    }
  }

  private void tinaMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (tinaMenuItem.isSelected()) {
      tinaInternalFrame.setVisible(true);
      try {
        tinaInternalFrame.setSelected(true);
      }
      catch (PropertyVetoException ex) {
        ex.printStackTrace();
      }
    }
    else {
      tinaInternalFrame.setVisible(false);
    }
  }

  private void openMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage(true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void initApp() {
    {
      ScriptInternalFrame scriptFrame = (ScriptInternalFrame) getScriptInternalFrame();
      scriptFrame.initApp();
    }
    {
      OperatorsInternalFrame operatorsFrame = (OperatorsInternalFrame) getOperatorsInternalFrame();
      operatorsFrame.initApp();
    }
    {
      FormulaExplorerInternalFrame formulaExplorerFrame = (FormulaExplorerInternalFrame) getFormulaExplorerInternalFrame();
      formulaExplorerFrame.initApp();
    }

    mainController.refreshWindowMenu();
    mainController.scriptFrameChanged(1, null, "60");

    try {
      formulaExplorerController.calculate();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    enableControls();
  }

  private void saveMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    try {
      mainController.saveImage();
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void openFavourite1MenuItem_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage("C:\\TMP\\Eva_Mendez_8.jpg", true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void openFavourite2MenuItem_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage(
          "C:\\Data\\Fotos\\2011-01-15\\DSC_0075B.JPG", true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void openFavourite3MenuItem_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage("C:\\TMP\\nastya-019.jpg", true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void openFavourite4MenuItem_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage(
          "C:\\Data\\Hintergründe\\aktuell\\050403.jpg", true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void openFavourite5MenuItem_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage(
          "C:\\Data\\Hintergründe\\aktuell\\051303.jpg", true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void openFavourite6MenuItem_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage(
          "C:\\Data\\Hintergründe\\aktuell\\111002.jpg", true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void openFavourite7MenuItem_actionPerformed(
      java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage(
          "C:\\Data\\Hintergründe\\KBF\\6281044.jpg", true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void showMessageCloseButton_actionPerformed(
      java.awt.event.ActionEvent e) {
    mainController.closeShowMessageDlg();
    enableControls();
  }

  private void showErrorCloseButton_actionPerformed(
      java.awt.event.ActionEvent e) {
    errorHandler.closeShowErrorDlg();
    enableControls();
  }

  // type=Object -> hide from Visual Editor

  private JMenuItem openFavourite4MenuItem = null;

  private JMenuItem openFavourite5MenuItem = null;

  private JMenuItem openFavourite6MenuItem = null;

  private JMenuItem openFavourite7MenuItem = null;

  private JMenuItem closeAllMenuItem = null;

  private JInternalFrame scriptInternalFrame = null;

  private JCheckBoxMenuItem scriptMenuItem = null;

  private JInternalFrame operatorsInternalFrame = null;

  private JDialog renderDialog = null; //  @jve:decl-index=0:visual-constraint="2243,6"

  private JPanel renderWindowContentPane = null;

  private JPanel renderTopPanel = null;

  private JPanel renderBottomPanel = null;

  private JPanel renderCenterPanel = null;

  private JButton renderRenderButton = null;

  private JButton renderCancelButton = null;

  private JButton renderCloseButton = null;

  private JPanel renderPreviewBottomPanel = null;

  private JPanel renderPreviewMainPanel = null;

  private JPanel renderPreviewProgressTopPanel = null;

  private JPanel renderPreviewProgressMainPanel = null;

  private JProgressBar renderProgressBar = null;

  private JLabel renderOutputLabel = null;

  private JTextField renderOutputREd = null;

  private JLabel renderFrameLabel = null;

  private JLabel renderFrameValueLabel = null;

  private JLabel renderSizeLabel = null;

  private JLabel renderSizeValueLabel = null;

  private JLabel renderPrefixLabel = null;

  private JTextField renderPrefixREd = null;

  private JPanel renderPreviewImg1Panel = null;

  private JPanel renderPreviewImg2Panel = null;

  private JPanel renderPreviewImg3Panel = null;

  private JPanel renderPreviewImg1BottomPanel = null;

  private JPanel renderPreviewImg2BottomPanel = null;

  private JPanel renderPreviewImg3BottomPanel = null;

  private JLabel renderPreviewImg1Label = null;

  private JLabel renderPreviewImg2Label = null;

  private JLabel renderPreviewImg3Label = null;

  private JLabel renderFrameStartLabel = null;

  private JTextField renderFrameStartREd = null;

  private JLabel renderFrameEndLabel = null;

  private JTextField renderFrameEndREd = null;

  private JInternalFrame formulaExplorerInternalFrame = null;

  private JCheckBoxMenuItem formulaExplorerMenuItem = null;

  private JInternalFrame tinaInternalFrame = null;

  private JPanel tinaContentPane1 = null;

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

  private JButton tinaResetTransformationButton = null;

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

  void enableControls() {
    tinaMenuItem.setSelected(tinaInternalFrame.isVisible());
    operatorsMenuItem.setSelected(operatorsInternalFrame.isVisible());
    scriptMenuItem.setSelected(scriptInternalFrame.isVisible());
    formulaExplorerMenuItem.setSelected(formulaExplorerInternalFrame
        .isVisible());
    closeAllMenuItem.setEnabled(mainController.getBufferList().size() > 0);
    {
      ScriptInternalFrame scriptFrame = (ScriptInternalFrame) getScriptInternalFrame();
      scriptFrame.enableControls();
    }
    {
      OperatorsInternalFrame operatorsFrame = (OperatorsInternalFrame) getOperatorsInternalFrame();
      operatorsFrame.enableControls();
    }
  }

  private void closeAllMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (mainController.closeAll())
      enableControls();
  }

  private void scriptMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (scriptMenuItem.isSelected()) {
      scriptInternalFrame.setVisible(true);
      try {
        scriptInternalFrame.setSelected(true);
      }
      catch (PropertyVetoException ex) {
        ex.printStackTrace();
      }
    }
    else {
      scriptInternalFrame.setVisible(false);
    }
  }

  private void scriptInternalFrame_internalFrameClosed(
      javax.swing.event.InternalFrameEvent e) {
    enableControls();
  }

  private void scriptInternalFrame_internalFrameDeactivated(
      javax.swing.event.InternalFrameEvent e) {
    enableControls();
  }

  private void operatorsInternalFrame_internalFrameDeactivated(
      javax.swing.event.InternalFrameEvent e) {
    enableControls();
  }

  private void operatorsInternalFrame_internalFrameClosed(
      javax.swing.event.InternalFrameEvent e) {
    enableControls();
  }

  private void tinaInternalFrame_internalFrameClosed(
      javax.swing.event.InternalFrameEvent e) {
    enableControls();
  }

  private void tinaInternalFrame_internalFrameDeactivated(
      javax.swing.event.InternalFrameEvent e) {
    enableControls();
  }

  // TODO log window
  // TODO prefs window
  // TODO intro window
}
