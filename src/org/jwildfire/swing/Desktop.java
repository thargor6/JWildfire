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
package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.WindowPrefs;
import org.jwildfire.create.eden.swing.EDENInternalFrame;
import org.jwildfire.create.iflames.swing.IFlamesInternalFrame;
import org.jwildfire.create.tina.random.RandomGeneratorFactory;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.swing.BatchFlameRendererInternalFrame;
import org.jwildfire.create.tina.swing.DancingFlamesInternalFrame;
import org.jwildfire.create.tina.swing.EasyMovieMakerInternalFrame;
import org.jwildfire.create.tina.swing.FlameBrowserInternalFrame;
import org.jwildfire.create.tina.swing.HelpInternalFrame;
import org.jwildfire.create.tina.swing.InteractiveRendererInternalFrame;
import org.jwildfire.create.tina.swing.MeshGenInternalFrame;
import org.jwildfire.create.tina.swing.MutaGenInternalFrame;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.TinaInternalFrame;

public class Desktop extends JApplet {
  private final List<InternalFrameHolder<?>> internalFrames;

  public Desktop() {
    internalFrames = new ArrayList<>();
    internalFrames.add(new InternalFrameHolder<>(MutaGenInternalFrame.class, this, WindowPrefs.WINDOW_MUTAGEN, "Fractal flames: MutaGen"));
    internalFrames.add(new InternalFrameHolder<>(InteractiveRendererInternalFrame.class, this, WindowPrefs.WINDOW_INTERACTIVERENDERER, "Fractal flames: Interactive renderer"));
    internalFrames.add(new InternalFrameHolder<>(FlameBrowserInternalFrame.class, this, WindowPrefs.WINDOW_FLAMEBROWSER, "Fractal flames: Flame browser"));
    internalFrames.add(new InternalFrameHolder<>(EasyMovieMakerInternalFrame.class, this, WindowPrefs.WINDOW_FLAMEBROWSER, "Fractal flames: Easy movie maker"));
    internalFrames.add(new InternalFrameHolder<>(DancingFlamesInternalFrame.class, this, WindowPrefs.WINDOW_DANCINGFLAMES, "Fractal flames: Dancing flames"));
    internalFrames.add(new InternalFrameHolder<>(BatchFlameRendererInternalFrame.class, this, WindowPrefs.WINDOW_BATCHFLAMERENDERER, "Fractal flames: Batch renderer"));
    internalFrames.add(new InternalFrameHolder<>(MeshGenInternalFrame.class, this, WindowPrefs.WINDOW_MESHGEN, "Fractal flames: Mesh generator"));
    internalFrames.add(new InternalFrameHolder<>(HelpInternalFrame.class, this, WindowPrefs.WINDOW_HELP, "Fractal flames: Help"));
    internalFrames.add(new InternalFrameHolder<>(IFlamesInternalFrame.class, this, WindowPrefs.WINDOW_IFLAMES, "IFlames"));

    internalFrames.add(new InternalFrameHolder<>(OperatorsInternalFrame.class, this, WindowPrefs.WINDOW_IMAGEPROCESSING, "Image processing"));
    internalFrames.add(new InternalFrameHolder<>(FormulaExplorerInternalFrame.class, this, WindowPrefs.WINDOW_FORMULAEXPLORER, "Formula explorer"));
  }

  private static final long serialVersionUID = 1L;

  private JFrame jFrame = null;
  private JPanel jContentPane = null;
  private JMenuBar mainJMenuBar = null;
  private JMenu fileMenu = null;
  private JMenu helpMenu = null;
  private JMenuItem exitMenuItem = null;
  private JMenuItem welcomeMenuItem = null;
  private JMenuItem systemInfoMenuItem = null;
  private JCheckBoxMenuItem lookAndFeelMenuItem = null;
  private JMenuItem saveMenuItem = null;
  private JDesktopPane mainDesktopPane = null;
  private JMenu windowMenu = null;
  private JCheckBoxMenuItem tinaMenuItem = null;
  private JMenuItem openMenuItem = null;
  private Prefs prefs = null;

  private StandardErrorHandler errorHandler = null;

  private JDesktopPane getMainDesktopPane() {
    if (mainDesktopPane == null) {
      mainDesktopPane = createMainDesktopPane();
      mainDesktopPane.add(getTinaInternalFrame(), null);
      for (InternalFrameHolder<?> internalFrame : internalFrames) {
        mainDesktopPane.add(internalFrame.getInternalFrame());
      }
      //mainDesktopPane.add(getEDENInternalFrame(), null);
      mainDesktopPane.add(getPreferencesInternalFrame(), null);
      mainDesktopPane.add(getWelcomeInternalFrame(), null);
      mainDesktopPane.add(getSystemInfoInternalFrame(), null);
      errorHandler = new StandardErrorHandler(mainDesktopPane, getShowErrorDlg(), getShowErrorDlgMessageTextArea(),
          getShowErrorDlgStacktraceTextArea());
      mainDesktopPane.add(getLookAndFeelInternalFrame(mainDesktopPane, errorHandler, prefs), null);
      if (!welcomeInternalFrame.isVisible()) {
        welcomeInternalFrame.setVisible(true);
      }

      MutaGenInternalFrame mutaGenFrame = getInternalFrame(MutaGenInternalFrame.class);
      FlameBrowserInternalFrame flameBrowserFrame = getInternalFrame(FlameBrowserInternalFrame.class);
      EasyMovieMakerInternalFrame easyMovieMakerFrame = getInternalFrame(EasyMovieMakerInternalFrame.class);
      DancingFlamesInternalFrame dancingFlamesFrame = getInternalFrame(DancingFlamesInternalFrame.class);
      BatchFlameRendererInternalFrame batchFlameRendererFrame = getInternalFrame(BatchFlameRendererInternalFrame.class);
      MeshGenInternalFrame meshGenFrame = getInternalFrame(MeshGenInternalFrame.class);
      InteractiveRendererInternalFrame interactiveRendererFrame = getInternalFrame(InteractiveRendererInternalFrame.class);
      HelpInternalFrame helpFrame = getInternalFrame(HelpInternalFrame.class);

      TinaInternalFrame tinaFrame = (TinaInternalFrame) getTinaInternalFrame();
      tinaController = tinaFrame.createController(errorHandler, prefs, mutaGenFrame, flameBrowserFrame, easyMovieMakerFrame, dancingFlamesFrame, batchFlameRendererFrame, meshGenFrame, interactiveRendererFrame, helpFrame);
      try {
        tinaController.createRandomBatch(2, RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME, RandomSymmetryGeneratorList.DEFAULT_GENERATOR_NAME, RandomGradientGeneratorList.DEFAULT_GENERATOR_NAME, RandomBatchQuality.LOW);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

      flameBrowserFrame.setTinaController(tinaController);
      mutaGenFrame.setTinaController(tinaController);
      easyMovieMakerFrame.setTinaController(tinaController);
      dancingFlamesFrame.setTinaController(tinaController);
      batchFlameRendererFrame.setTinaController(tinaController);
      meshGenFrame.setTinaController(tinaController);
      interactiveRendererFrame.setTinaController(tinaController);
      helpFrame.setTinaController(tinaController);

      FormulaExplorerInternalFrame formulaExplorerFrame = getInternalFrame(FormulaExplorerInternalFrame.class);

      formulaExplorerController = new FormulaExplorerController(
          (FormulaPanel) formulaExplorerFrame.getFormulaPanel(),
          formulaExplorerFrame.getFormulaExplorerFormula1REd(),
          formulaExplorerFrame.getFormulaExplorerFormula2REd(),
          formulaExplorerFrame.getFormulaExplorerFormula3REd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXMinREd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXMaxREd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXCountREd(),
          formulaExplorerFrame.getFormulaExplorerValuesTextArea());

      OperatorsInternalFrame operatorsFrame = getInternalFrame(OperatorsInternalFrame.class);
      operatorsFrame.setDesktop(this);

      PreferencesInternalFrame preferencesFrame = (PreferencesInternalFrame) getPreferencesInternalFrame();
      preferencesFrame.setDesktop(this);
      preferencesFrame.setPrefs(prefs);
      preferencesFrame.setMainController(mainController);

      mainController = new MainController(prefs, errorHandler, mainDesktopPane,
          getWindowMenu(), operatorsFrame.getTransformerInputCmb(), operatorsFrame.getTransformerPresetCmb(), operatorsFrame.getCreatorPresetCmb(),
          getShowMessageDlg(), getShowMessageDlgTextArea(), null, null, null, null, null, null, null, internalFrames.size());

      EDENInternalFrame edenFrame = (EDENInternalFrame) getEDENInternalFrame();
      edenFrame.createController(mainController, errorHandler, prefs).newEmptyScene();

      IFlamesInternalFrame iflamesInternalFrame = getInternalFrame(IFlamesInternalFrame.class);
      iflamesInternalFrame.createController(mainController, tinaController, errorHandler);

      tinaController.setMainController(mainController);

      operatorsFrame.setMainController(mainController);
      formulaExplorerFrame.setMainController(mainController);
      formulaExplorerFrame.setFormulaExplorerController(formulaExplorerController);
    }
    return mainDesktopPane;
  }

  private BufferedImage backgroundImage;

  @SuppressWarnings("serial")
  private JDesktopPane createMainDesktopPane() {
    try {
      backgroundImage = ImageIO.read(this.getClass().getResource("/org/jwildfire/swing/backgrounds/wallpaper_pearls2.jpg"));
    }
    catch (Exception ex) {
      backgroundImage = null;
      ex.printStackTrace();
    }
    if (backgroundImage != null) {
      return new JDesktopPane() {
        @Override
        protected void paintComponent(Graphics grphcs) {
          super.paintComponent(grphcs);
          grphcs.drawImage(backgroundImage, 0, 0, null);
        }

        @Override
        public Dimension getPreferredSize() {
          return new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight());
        }
      };
    }
    else {
      return new JDesktopPane();
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends JInternalFrame> T getInternalFrame(Class<T> frameType) {
    for (InternalFrameHolder<?> internalFrame : internalFrames) {
      if (frameType.equals(internalFrame.getFrameType())) {
        return (T) internalFrame.getInternalFrame();
      }
    }
    return null;
  }

  private JMenu getWindowMenu() {
    if (windowMenu == null) {
      windowMenu = new JMenu();
      windowMenu.setText("Windows");
      //windowMenu.add(getScriptMenuItem());
      windowMenu.add(getTinaMenuItem());
      for (InternalFrameHolder<?> internalFrame : internalFrames) {
        windowMenu.add(internalFrame.getMenuItem());
      }
      //windowMenu.add(getEDENMenuItem());
      windowMenu.add(getPreferencesMenuItem());
      windowMenu.add(getLookAndFeelMenuItem());
    }
    return windowMenu;
  }

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

  private JInternalFrame getPreferencesInternalFrame() {
    if (preferencesInternalFrame == null) {
      preferencesInternalFrame = new PreferencesInternalFrame();
      preferencesInternalFrame
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
    return preferencesInternalFrame;
  }

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
              enableControls();
            }

            public void internalFrameClosed(
                javax.swing.event.InternalFrameEvent e) {
              enableControls();
            }
          });
      tinaInternalFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentResized(java.awt.event.ComponentEvent e) {
          tinaController.refreshFlameImage(true, false, 1, true, false);
        }
      });
      try {
        tinaInternalFrame.setSelected(true);
        tinaInternalFrame.setMaximum(wPrefs.isMaximized());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    return tinaInternalFrame;
  }

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

  public static void main(final String[] args) {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setUserLookAndFeel();
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

  public static void setUserLookAndFeel() {
    try {
      Prefs prefs = Prefs.getPrefs();
      if (prefs.getLookAndFeelType() != null) {
        prefs.getLookAndFeelType().changeTo();
        if (prefs.getLookAndFeelTheme() != null && prefs.getLookAndFeelTheme().length() > 0)
          prefs.getLookAndFeelType().changeTheme(prefs.getLookAndFeelTheme());
        return;
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
    try {
      LookAndFeelType.NIMBUS.changeTo();
      return;
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
    try {
      LookAndFeelType.SYSTEM.changeTo();
      return;
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method initializes jFrame
   * 
   * @return javax.swing.JFrame
   */
  private JFrame getJFrame() {
    if (jFrame == null) {
      prefs = Prefs.getPrefs();
      jFrame = new JFrame();
      jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jFrame.setJMenuBar(getMainJMenuBar());
      WindowPrefs wPrefs = prefs.getWindowPrefs(WindowPrefs.WINDOW_DESKTOP);
      jFrame.setLocation(wPrefs.getLeft(), wPrefs.getTop());
      jFrame.setSize(wPrefs.getWidth(1240), wPrefs.getHeight(800));
      jFrame.setContentPane(getJContentPane());
      jFrame.setTitle(Tools.APP_TITLE + " " + Tools.APP_VERSION);

      jFrame.addWindowListener(new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
          closeApp();
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
          closeApp();
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
      systemInfoMenuItem.setVisible(true);
      systemInfoMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (!systemInfoInternalFrame.isVisible()) {
            ((SystemInfoInternalFrame) systemInfoInternalFrame).refresh();
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

  private JCheckBoxMenuItem getLookAndFeelMenuItem() {
    if (lookAndFeelMenuItem == null) {
      lookAndFeelMenuItem = new JCheckBoxMenuItem();
      lookAndFeelMenuItem.setText("UI Theme (Look and Feel)");
      lookAndFeelMenuItem.setVisible(true);
      lookAndFeelMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          if (lookAndFeelMenuItem.isSelected()) {
            lookAndFeelInternalFrame.setVisible(true);
            try {
              lookAndFeelInternalFrame.setSelected(true);
            }
            catch (PropertyVetoException ex) {
              ex.printStackTrace();
            }
          }
          else {
            lookAndFeelInternalFrame.setVisible(false);
          }
        }
      });
    }
    return lookAndFeelMenuItem;
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
    setUserLookAndFeel();
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
  private MainController mainController = null;
  private FormulaExplorerController formulaExplorerController = null;
  private TinaController tinaController = null;

  private JPanel showMessageTopPnl = null;

  private JPanel showMessageBottomPnl = null;

  private JPanel showMessageLeftPnl = null;

  private JPanel showMessageRightPnl = null;

  private JButton showMessageCloseButton = null;

  private JDialog showMessageDlg = null;

  private JPanel showMessageDlgContentPanel = null;

  private JScrollPane showMessageDlgScrollPane = null;

  private JTextArea showMessageDlgTextArea = null;

  private JDialog showErrorDlg = null;

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
      PreferencesInternalFrame preferencesFrame = (PreferencesInternalFrame) getPreferencesInternalFrame();
      preferencesFrame.initApp();
    }
    getInternalFrame(OperatorsInternalFrame.class).initApp();
    getInternalFrame(FormulaExplorerInternalFrame.class).initApp();

    mainController.refreshWindowMenu();

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

  private JMenuItem closeAllMenuItem = null;

  private JInternalFrame preferencesInternalFrame = null;

  private JCheckBoxMenuItem preferencesMenuItem = null;

  private JInternalFrame tinaInternalFrame = null;

  private JInternalFrame edenInternalFrame = null;
  private JInternalFrame welcomeInternalFrame = null;
  private JInternalFrame systemInfoInternalFrame = null;
  private JInternalFrame lookAndFeelInternalFrame = null;

  void enableControls() {
    tinaMenuItem.setSelected(tinaInternalFrame.isVisible());
    for (InternalFrameHolder<?> internalFrame : internalFrames) {
      internalFrame.enableMenu();
    }
    preferencesMenuItem.setSelected(preferencesInternalFrame.isVisible());
    lookAndFeelMenuItem.setSelected(lookAndFeelInternalFrame.isVisible());
    closeAllMenuItem.setEnabled(mainController.getBufferList().size() > 0);
    {
      PreferencesInternalFrame preferencesFrame = (PreferencesInternalFrame) getPreferencesInternalFrame();
      preferencesFrame.enableControls();
    }
    getInternalFrame(OperatorsInternalFrame.class).enableControls();
  }

  private void closeAllMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (mainController.closeAll())
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

  private JInternalFrame getLookAndFeelInternalFrame(JDesktopPane pRootPane, ErrorHandler pErrorHandler, Prefs pPrefs) {
    if (lookAndFeelInternalFrame == null) {
      lookAndFeelInternalFrame = new LookAndFeelInternalFrame(this, pRootPane, pErrorHandler, pPrefs);
      lookAndFeelInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
        public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent e) {
          enableControls();
        }

        public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
          enableControls();
        }
      });
    }
    return lookAndFeelInternalFrame;
  }

  private void closeApp() {
    String ObjButtons[] = { "Yes", "No" };
    int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
    if (PromptResult != JOptionPane.YES_OPTION) {
      return;
    }

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
    for (InternalFrameHolder<?> internalFrame : internalFrames) {
      internalFrame.saveWindowPrefs();
    }
    try {
      prefs.saveToFromFile();
      tinaController.saveScriptProps();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    RandomGeneratorFactory.cleanup();
    System.exit(0);
  }

}