/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.SoftBevelBorder;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.WindowPrefs;
import org.jwildfire.create.eden.swing.EDENInternalFrame;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.TinaInternalFrame;

public class Desktop extends JApplet {
  private static final long serialVersionUID = 1L;

  private JFrame jFrame = null; // @jve:decl-index=0:visual-constraint="10,10"
  private JPanel jContentPane = null;
  private JMenuBar mainJMenuBar = null;
  private JMenu fileMenu = null;
  private JMenu helpMenu = null;
  private JMenuItem exitMenuItem = null;
  private JMenuItem welcomeMenuItem = null;
  private JMenuItem systemInfoMenuItem = null;
  private JMenuItem saveMenuItem = null;
  private JDesktopPane mainDesktopPane = null;
  private JMenu windowMenu = null;
  private JCheckBoxMenuItem operatorsMenuItem = null;
  private JCheckBoxMenuItem tinaMenuItem = null;
  private JCheckBoxMenuItem edenMenuItem = null;
  private JMenuItem openMenuItem = null;
  private Prefs prefs = null;

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
      mainDesktopPane.add(getEDENInternalFrame(), null);
      mainDesktopPane.add(getPreferencesInternalFrame(), null);
      mainDesktopPane.add(getWelcomeInternalFrame(), null);
      mainDesktopPane.add(getSystemInfoInternalFrame(), null);
      if (!welcomeInternalFrame.isVisible()) {
        welcomeInternalFrame.setVisible(true);
      }
      errorHandler = new StandardErrorHandler(mainDesktopPane, getShowErrorDlg(), getShowErrorDlgMessageTextArea(),
          getShowErrorDlgStacktraceTextArea());

      TinaInternalFrame tinaFrame = (TinaInternalFrame) getTinaInternalFrame();
      tinaController = tinaFrame.createController(errorHandler, prefs);
      try {
        tinaController.createRandomBatch(3, RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

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

      PreferencesInternalFrame preferencesFrame = (PreferencesInternalFrame) getPreferencesInternalFrame();
      preferencesFrame.setDesktop(this);
      preferencesFrame.setPrefs(prefs);
      preferencesFrame.setMainController(mainController);

      mainController = new MainController(prefs, errorHandler, mainDesktopPane,
          getWindowMenu(), operatorsFrame.getTransformerInputCmb(), operatorsFrame.getTransformerPresetCmb(), operatorsFrame.getCreatorPresetCmb(),
          getShowMessageDlg(), getShowMessageDlgTextArea(), scriptFrame.getScriptTable(),
          scriptFrame.getScriptActionTextArea(), scriptFrame.getScriptFrameSlider(),
          scriptFrame.getScriptFramesREd(), scriptFrame.getScriptFrameREd(),
          scriptFrame.getEnvelopeController(), renderController);
      renderController.setActionList(mainController.getActionList());

      EDENInternalFrame edenFrame = (EDENInternalFrame) getEDENInternalFrame();
      edenFrame.createController(mainController, errorHandler, prefs).newEmptyScene();

      tinaController.setMainController(mainController);

      scriptFrame.setMainController(mainController);
      scriptFrame.setOperatorsFrame(operatorsFrame);
      operatorsFrame.setMainController(mainController);
      formulaExplorerFrame.setMainController(mainController);
      formulaExplorerFrame.setFormulaExplorerController(formulaExplorerController);

      setupShowSysInfoThread();

    }
    return mainDesktopPane;
  }

  private void setupShowSysInfoThread() {
    final JInternalFrame tinaFrame = getTinaInternalFrame();
    final String title = tinaFrame.getTitle();
    new Thread(new Runnable() {

      @Override
      public void run() {
        while (true) {
          try {
            SystemInfo sysInfo = new SystemInfo();
            tinaFrame.setTitle(title + "(" + sysInfo.getUsedMemMB() + "MB / " + sysInfo.getMaxMemMB() + "MB)");
            Thread.sleep(1000);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
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
      windowMenu.add(getEDENMenuItem());
      windowMenu.add(getPreferencesMenuItem());
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
      tinaMenuItem.setText("Fractal flames");
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
   * This method initializes operatorsMenuItem
   * 
   * @return javax.swing.JCheckBoxMenuItem
   */
  private JCheckBoxMenuItem getEDENMenuItem() {
    if (edenMenuItem == null) {
      edenMenuItem = new JCheckBoxMenuItem();
      edenMenuItem.setText("Structure Syntesizer");
      edenMenuItem.setEnabled(true);
      edenMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          edenMenuItem_actionPerformed(e);
        }
      });
    }
    return edenMenuItem;
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
   * This method initializes preferencesInternalFrame
   * 
   * @return javax.swing.JInternalFrame
   */
  private JInternalFrame getPreferencesInternalFrame() {
    if (preferencesInternalFrame == null) {
      preferencesInternalFrame = new PreferencesInternalFrame();
      preferencesInternalFrame
          .addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameDeactivated(
                javax.swing.event.InternalFrameEvent e) {
              preferencesInternalFrame_internalFrameDeactivated(e);
            }

            public void internalFrameClosed(
                javax.swing.event.InternalFrameEvent e) {
              preferencesInternalFrame_internalFrameClosed(e);
            }
          });
    }
    return preferencesInternalFrame;
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
   * This method initializes scriptMenuItem
   * 
   * @return javax.swing.JMenuItem
   */
  private JCheckBoxMenuItem getPreferencesMenuItem() {
    if (preferencesMenuItem == null) {
      preferencesMenuItem = new JCheckBoxMenuItem();
      preferencesMenuItem.setText("Preferences");
      preferencesMenuItem.setSelected(true);
      preferencesMenuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              preferencesMenuItem_actionPerformed(e);
            }
          });
    }
    return preferencesMenuItem;
  }

  /**
  * This method initializes operatorsInternalFrame
  * 
  * @return javax.swing.JInternalFrame
  */
  private JInternalFrame getOperatorsInternalFrame() {
    if (operatorsInternalFrame == null) {
      operatorsInternalFrame = new OperatorsInternalFrame();
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
      tinaInternalFrame = new TinaInternalFrame();
      WindowPrefs wPrefs = prefs.getWindowPrefs(WindowPrefs.WINDOW_TINA);
      tinaInternalFrame.setLocation(wPrefs.getLeft(), wPrefs.getTop());
      tinaInternalFrame.setSize(wPrefs.getWidth(1188), wPrefs.getHeight(740));
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
          tinaController.refreshFlameImage(false);
        }
      });
      try {
        tinaInternalFrame.setSelected(true);
        tinaInternalFrame.setMaximum(wPrefs.isMaximized());
      }
      catch (Exception e) {
        // e.printStackTrace();
      }
    }
    return tinaInternalFrame;
  }

  /**
   * This method initializes edenInternalFrame	
   * 	
   * @return javax.swing.JInternalFrame	
   */
  private JInternalFrame getEDENInternalFrame() {
    if (edenInternalFrame == null) {
      edenInternalFrame = new EDENInternalFrame();
      edenInternalFrame
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
    return edenInternalFrame;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        LookAndFeel.setLookAndFeel();
        Desktop application = new Desktop();
        application.getJFrame().setVisible(true);
        application.initApp();
        try {
          application.getWelcomeInternalFrame().setSelected(true);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
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
      prefs = new Prefs();
      try {
        prefs.loadFromFile();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

      jFrame = new JFrame();
      jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jFrame.setJMenuBar(getMainJMenuBar());
      WindowPrefs wPrefs = prefs.getWindowPrefs(WindowPrefs.WINDOW_DESKTOP);
      jFrame.setLocation(wPrefs.getLeft(), wPrefs.getTop());
      jFrame.setSize(wPrefs.getWidth(1240), wPrefs.getHeight(800));
      jFrame.setContentPane(getJContentPane());
      jFrame.setTitle(Tools.APP_TITLE + " " + Tools.APP_VERSION);

      jFrame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          {
            Dimension size = jFrame.getSize();
            Point pos = jFrame.getLocation();
            WindowPrefs wPrefs = prefs.getWindowPrefs(WindowPrefs.WINDOW_DESKTOP);
            wPrefs.setLeft(pos.x);
            wPrefs.setTop(pos.y);
            wPrefs.setWidth(size.width);
            wPrefs.setHeight(size.height);
          }
          if (tinaInternalFrame != null && tinaInternalFrame.isVisible()) {
            Dimension size = tinaInternalFrame.getSize();
            Point pos = tinaInternalFrame.getLocation();
            WindowPrefs wPrefs = prefs.getWindowPrefs(WindowPrefs.WINDOW_TINA);
            wPrefs.setLeft(pos.x);
            wPrefs.setTop(pos.y);
            wPrefs.setWidth(size.width);
            wPrefs.setHeight(size.height);
            wPrefs.setMaximized(tinaInternalFrame.isMaximum());
          }
          try {
            prefs.saveToFromFile();
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }

          try {
            prefs.saveToFromFile();
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      });

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
      helpMenu.add(getWelcomeMenuItem());
      helpMenu.add(getSystemInfoMenuItem());
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
          try {
            if (prefs != null) {
              prefs.saveToFromFile();
            }
          }
          catch (Exception ex) {
            System.out.println(ex);
          }
          System.exit(0);
        }
      });
    }
    return exitMenuItem;
  }

  private JMenuItem getWelcomeMenuItem() {
    if (welcomeMenuItem == null) {
      welcomeMenuItem = new JMenuItem();
      welcomeMenuItem.setText("Welcome to " + Tools.APP_TITLE);
      welcomeMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (!welcomeInternalFrame.isVisible()) {
            welcomeInternalFrame.setVisible(true);
          }
          try {
            welcomeInternalFrame.setSelected(true);
          }
          catch (PropertyVetoException ex) {
            ex.printStackTrace();
          }
        }
      });
    }
    return welcomeMenuItem;
  }

  private JMenuItem getSystemInfoMenuItem() {
    if (systemInfoMenuItem == null) {
      systemInfoMenuItem = new JMenuItem();
      systemInfoMenuItem.setText("System Information");
      systemInfoMenuItem.setVisible(false);
      systemInfoMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (!systemInfoInternalFrame.isVisible()) {
            systemInfoInternalFrame.setVisible(true);
          }
          try {

            systemInfoInternalFrame.setSelected(true);
          }
          catch (PropertyVetoException ex) {
            ex.printStackTrace();
          }
        }
      });
    }
    return systemInfoMenuItem;
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
    LookAndFeel.setLookAndFeel();
    appletApplication = new Desktop();
    appletApplication.getJFrame().setVisible(true);
    appletApplication.initApp();
    try {
      appletApplication.getWelcomeInternalFrame().setSelected(true);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    super.start();
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
  private TinaController tinaController = null; // @jve:decl-index=0:

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

  private void edenMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (edenMenuItem.isSelected()) {
      edenInternalFrame.setVisible(true);
      try {
        edenInternalFrame.setSelected(true);
      }
      catch (PropertyVetoException ex) {
        ex.printStackTrace();
      }
    }
    else {
      edenInternalFrame.setVisible(false);
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
      PreferencesInternalFrame preferencesFrame = (PreferencesInternalFrame) getPreferencesInternalFrame();
      preferencesFrame.initApp();
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

  private JMenuItem closeAllMenuItem = null;

  private JInternalFrame scriptInternalFrame = null;

  private JInternalFrame preferencesInternalFrame = null;

  private JCheckBoxMenuItem scriptMenuItem = null;

  private JCheckBoxMenuItem preferencesMenuItem = null;

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

  private JInternalFrame edenInternalFrame = null;
  private JInternalFrame welcomeInternalFrame = null;
  private JInternalFrame systemInfoInternalFrame = null;

  void enableControls() {
    edenMenuItem.setSelected(edenInternalFrame.isVisible());
    tinaMenuItem.setSelected(tinaInternalFrame.isVisible());
    operatorsMenuItem.setSelected(operatorsInternalFrame.isVisible());
    scriptMenuItem.setSelected(scriptInternalFrame.isVisible());
    preferencesMenuItem.setSelected(preferencesInternalFrame.isVisible());
    formulaExplorerMenuItem.setSelected(formulaExplorerInternalFrame
        .isVisible());
    closeAllMenuItem.setEnabled(mainController.getBufferList().size() > 0);
    {
      ScriptInternalFrame scriptFrame = (ScriptInternalFrame) getScriptInternalFrame();
      scriptFrame.enableControls();
    }
    {
      PreferencesInternalFrame preferencesFrame = (PreferencesInternalFrame) getPreferencesInternalFrame();
      preferencesFrame.enableControls();
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

  private void preferencesMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (preferencesMenuItem.isSelected()) {
      preferencesInternalFrame.setVisible(true);
      try {
        preferencesInternalFrame.setSelected(true);
      }
      catch (PropertyVetoException ex) {
        ex.printStackTrace();
      }
    }
    else {
      preferencesInternalFrame.setVisible(false);
    }
  }

  private void preferencesInternalFrame_internalFrameClosed(
      javax.swing.event.InternalFrameEvent e) {
    enableControls();
  }

  private void preferencesInternalFrame_internalFrameDeactivated(
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

  private JInternalFrame getWelcomeInternalFrame() {
    if (welcomeInternalFrame == null) {
      welcomeInternalFrame = new WelcomeInternalFrame();
    }
    return welcomeInternalFrame;
  }

  private JInternalFrame getSystemInfoInternalFrame() {
    if (systemInfoInternalFrame == null) {
      systemInfoInternalFrame = new SystemInfoInternalFrame();
    }
    return systemInfoInternalFrame;
  }
}
