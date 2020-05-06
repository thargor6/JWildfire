/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
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
import org.jwildfire.create.iflames.swing.IFlamesFrame;
import org.jwildfire.create.tina.faclrender.FACLRenderTools;
import org.jwildfire.create.tina.quilt.QuiltFlameRendererFrame;
import org.jwildfire.create.tina.random.RandomGeneratorFactory;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGenerator;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.swing.*;
import org.jwildfire.create.tina.swing.MainEditorFrame;

public class JWildfire extends JApplet {
  private final List<FrameHolder> mainInternalFrames;
  private final List<FrameHolder> settingsInternalFrames;
  private final List<FrameHolder> helpInternalFrames;

  public JWildfire() {
    mainInternalFrames = new ArrayList<>();

    mainInternalFrames.add(new DefaultJFrameHolder<>(MainEditorFrame.class, this, WindowPrefs.WINDOW_TINA, "Fractal flames: Editor"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(MutaGenFrame.class, this, WindowPrefs.WINDOW_MUTAGEN, "Fractal flames: MutaGen"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(InteractiveRendererFrame.class, this, WindowPrefs.WINDOW_INTERACTIVERENDERER, "Fractal flames: Interactive renderer"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(FlameBrowserFrame.class, this, WindowPrefs.WINDOW_FLAMEBROWSER, "Fractal flames: Flame browser"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(EasyMovieMakerFrame.class, this, WindowPrefs.WINDOW_FLAMEBROWSER, "Fractal flames: Easy movie maker"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(DancingFlamesFrame.class, this, WindowPrefs.WINDOW_DANCINGFLAMES, "Fractal flames: Dancing flames"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(BatchFlameRendererFrame.class, this, WindowPrefs.WINDOW_BATCHFLAMERENDERER, "Fractal flames: Batch renderer"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(QuiltFlameRendererFrame.class, this, WindowPrefs.WINDOW_QUILTFLAMERENDERER, "Fractal flames: Quilt renderer"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(MeshGenInternalFrame.class, this, WindowPrefs.WINDOW_MESHGEN, "Fractal flames: Mesh generator"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(VariationsSettingsFrame.class, this, WindowPrefs.WINDOW_VARIATIONSSETTINGS, "Fractal flames: Variations settings"));
    if (FACLRenderTools.isFaclRenderAvalailable()) {
      mainInternalFrames.add(new DefaultJFrameHolder<>(FlamesGPURenderFrame.class, this, WindowPrefs.WINDOW_FLAMES_GPU, "Fractal flames: GPU render"));
    }
    mainInternalFrames.add(new DefaultJFrameHolder<>(HelpFrame.class, this, WindowPrefs.WINDOW_HELP, "Fractal flames: Help"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(IFlamesFrame.class, this, WindowPrefs.WINDOW_IFLAMES, "IFlames"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(OperatorsFrame.class, this, WindowPrefs.WINDOW_IMAGEPROCESSING, "Image processing"));
    mainInternalFrames.add(new DefaultJFrameHolder<>(FormulaExplorerFrame.class, this, WindowPrefs.WINDOW_FORMULAEXPLORER, "Formula explorer"));

    settingsInternalFrames = new ArrayList<>();
    settingsInternalFrames.add(new DefaultJFrameHolder<LookAndFeelFrame>(LookAndFeelFrame.class, this, WindowPrefs.WINDOW_LOOKANDFEEL, "UI Theme (Look and Feel)") {

      @Override
      protected LookAndFeelFrame createFrame() {
        LookAndFeelFrame frame = new LookAndFeelFrame(JWildfire.this, getMainFrame(), errorHandler, Prefs.getPrefs());

        applyWindowPrefs(frame);

        frame.addWindowListener(new WindowAdapter() {
          public void windowDeactivated(WindowEvent e) {
            enableControls();
          }

          public void windowClosed(WindowEvent e) {
            enableControls();
          }
        });

        return frame;
      }

    });
    settingsInternalFrames.add(new DefaultJFrameHolder<>(PreferencesFrame.class, this, WindowPrefs.WINDOW_PREFERENCES, "Preferences"));

    helpInternalFrames = new ArrayList<>();
    helpInternalFrames.add(new DefaultJFrameHolder<>(SystemInfoFrame.class, this, WindowPrefs.WINDOW_SYSTEMINFO, "System Information"));
    helpInternalFrames.add(new DefaultJFrameHolder<>(WelcomeFrame.class, this, WindowPrefs.WINDOW_WELCOME, "Welcome to " + Tools.APP_TITLE));
    helpInternalFrames.add(new DefaultJFrameHolder<>(ListOfChangesFrame.class, this, WindowPrefs.WINDOW_LIST_OF_CHANGES, "List of changes"));
    helpInternalFrames.add(new DefaultJFrameHolder<>(GPURenderInfoFrame.class, this, WindowPrefs.WINDOW_GPU_RENDERING, "GPU rendering"));
    helpInternalFrames.add(new DefaultJFrameHolder<>(OptiXDenoiseInfoFrame.class, this, WindowPrefs.WINDOW_OPTIX_DENOISER_INFO, "OptiX denoiser"));
    helpInternalFrames.add(new DefaultJFrameHolder<>(TipOfTheDayFrame.class, this, WindowPrefs.WINDOW_TIPOFTHEDAY, "Tip of the day"));
  }

  private static final long serialVersionUID = 1L;
  public static final int DEFAULT_WINDOW_LEFT = 120;
  public static final int DEFAULT_WINDOW_TOP = 0;

  private JMenuBar mainJMenuBar = null;
  private JMenu fileMenu = null;
  private JMenu helpMenu = null;
  private JMenu settingsMenu = null;
  private JMenuItem exitMenuItem = null;
  private JMenu windowMenu = null;
  private JMenuItem openImageMenuItem = null;
  private JMenuItem openFlameMenuItem = null;
  private Prefs prefs = null;

  private StandardErrorHandler errorHandler = null;

  private void initUI() {
     prefs = Prefs.getPrefs();
    {
      MainEditorFrame tinaInternalFrame = getJFrame(MainEditorFrame.class);
      tinaInternalFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      tinaInternalFrame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          closeApp();
        }
      });
      tinaInternalFrame.setJMenuBar(getMainJMenuBar());

      errorHandler = new StandardErrorHandler(getMainFrame(), getShowErrorDlg(), getShowErrorDlgMessageTextArea(),
          getShowErrorDlgStacktraceTextArea());

/*
            WelcomeFrame welcomeInternalFrame = getJFrame(WelcomeFrame.class);
            if (!welcomeInternalFrame.isVisible()) {
              welcomeInternalFrame.setVisible(true);
            }
            welcomeInternalFrame.toFront();
*/
      try {
        getJFrame(WelcomeFrame.class);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

/*
      TipOfTheDayFrame tipOfTheDayFrame = getJFrame(TipOfTheDayFrame.class);
      if (!tipOfTheDayFrame.isVisible() && Prefs.getPrefs().isShowTipsAtStartup()) {
        tipOfTheDayFrame.setVisible(true);
      }
      tipOfTheDayFrame.toFront();
*/
      
     try {
        getJFrame(ListOfChangesFrame.class).initChangesPane();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        getJFrame(GPURenderInfoFrame.class).initChangesPane();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      try {
        getJFrame(OptiXDenoiseInfoFrame.class).initChangesPane();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

      getJFrame(LookAndFeelFrame.class);
      getJFrame(SystemInfoFrame.class);

      MutaGenFrame mutaGenFrame = getJFrame(MutaGenFrame.class);
      FlameBrowserFrame flameBrowserFrame = getJFrame(FlameBrowserFrame.class);
      EasyMovieMakerFrame easyMovieMakerFrame = getJFrame(EasyMovieMakerFrame.class);
      DancingFlamesFrame dancingFlamesFrame = getJFrame(DancingFlamesFrame.class);
      BatchFlameRendererFrame batchFlameRendererFrame = getJFrame(BatchFlameRendererFrame.class);
      QuiltFlameRendererFrame quiltFlameRendererFrame = getJFrame(QuiltFlameRendererFrame.class);
      MeshGenInternalFrame meshGenFrame = getJFrame(MeshGenInternalFrame.class);
      InteractiveRendererFrame interactiveRendererFrame = getJFrame(InteractiveRendererFrame.class);
      VariationsSettingsFrame variationsSettingsFrame = getJFrame(VariationsSettingsFrame.class);
      // create some unregistered window even when FACLRender is not available, in order to avoid further null-checks etc.
      FlamesGPURenderFrame gpuRendererFrame = FACLRenderTools.isFaclRenderAvalailable() ? getJFrame(FlamesGPURenderFrame.class) : new FlamesGPURenderFrame();
      HelpFrame helpFrame = getJFrame(HelpFrame.class);

      MainEditorFrame mainEditorFrame = getJFrame(MainEditorFrame.class);
      tinaController = mainEditorFrame.createController(this, errorHandler, prefs, mutaGenFrame, flameBrowserFrame, easyMovieMakerFrame, dancingFlamesFrame, batchFlameRendererFrame, quiltFlameRendererFrame, meshGenFrame, interactiveRendererFrame, gpuRendererFrame, helpFrame);

      flameBrowserFrame.setTinaController(tinaController);
      mutaGenFrame.setTinaController(tinaController);
      easyMovieMakerFrame.setTinaController(tinaController);
      dancingFlamesFrame.setTinaController(tinaController);
      batchFlameRendererFrame.setTinaController(tinaController);
      quiltFlameRendererFrame.setTinaController(tinaController);
      variationsSettingsFrame.setTinaController(tinaController);
      meshGenFrame.setTinaController(tinaController);
      interactiveRendererFrame.setTinaController(tinaController);
      gpuRendererFrame.setTinaController(tinaController);
      helpFrame.setTinaController(tinaController);

      FormulaExplorerFrame formulaExplorerFrame = getJFrame(FormulaExplorerFrame.class);

      formulaExplorerController = new FormulaExplorerController(
          (FormulaPanel) formulaExplorerFrame.getFormulaPanel(),
          formulaExplorerFrame.getFormulaExplorerFormula1REd(),
          formulaExplorerFrame.getFormulaExplorerFormula2REd(),
          formulaExplorerFrame.getFormulaExplorerFormula3REd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXMinREd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXMaxREd(),
          formulaExplorerFrame.getFormulaExplorerFormulaXCountREd(),
          formulaExplorerFrame.getFormulaExplorerValuesTextArea());

      OperatorsFrame operatorsFrame = getJFrame(OperatorsFrame.class);
      operatorsFrame.setDesktop(this);

      PreferencesFrame preferencesFrame = getJFrame(PreferencesFrame.class);
      preferencesFrame.setDesktop(this);
      preferencesFrame.setPrefs(prefs);
      preferencesFrame.setMainController(mainController);

      mainController = new MainController(prefs, errorHandler, getMainFrame(),
          getWindowMenu(), operatorsFrame.getTransformerInputCmb(), operatorsFrame.getTransformerPresetCmb(), operatorsFrame.getCreatorPresetCmb(),
          getShowMessageDlg(), getShowMessageDlgTextArea(), null, null, null, null, null, null, mainInternalFrames.size());

      /*
      EDENInternalFrame edenFrame = (EDENInternalFrame) getEDENInternalFrame();
      edenFrame.createController(mainController, errorHandler, prefs).newEmptyScene();
      */
      IFlamesFrame iflamesFrame = getJFrame(IFlamesFrame.class);
      iflamesFrame.createController(mainController, tinaController, errorHandler);

      tinaController.setMainController(mainController);

      operatorsFrame.setMainController(mainController);
      formulaExplorerFrame.setMainController(mainController);
      formulaExplorerFrame.setFormulaExplorerController(formulaExplorerController);
    }

    try {
      getJFrame(MainEditorFrame.class).setTitle("Welcome to " + Tools.APP_TITLE+" "+Tools.APP_VERSION+"!");
      getJFrame(MainEditorFrame.class).toFront();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if(Prefs.getPrefs().getTinaInitialRandomBatchSize()>0) {
          try {
            RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance(RandomFlameGeneratorList.DEFAULT_GENERATOR_NAME, true);
            if (randGen instanceof AllRandomFlameGenerator) {
              ((AllRandomFlameGenerator) randGen).setUseSimpleGenerators(true);
            }
            RandomSymmetryGenerator randSymmGen = RandomSymmetryGeneratorList.getRandomSymmetryGeneratorInstance(RandomSymmetryGeneratorList.DEFAULT_GENERATOR_NAME, true);
            RandomGradientGenerator randGradientGen = RandomGradientGeneratorList.getRandomGradientGeneratorInstance((RandomGradientGeneratorList.DEFAULT_GENERATOR_NAME), true);
            RandomWeightingFieldGenerator randWeightingFieldGen = RandomWeightingFieldGeneratorList.getRandomWeightingFieldGeneratorInstance(RandomWeightingFieldGeneratorList.DEFAULT_GENERATOR_NAME, true);

            tinaController.createRandomBatch(Prefs.getPrefs().getTinaInitialRandomBatchSize(), randGen, randSymmGen, randGradientGen, randWeightingFieldGen, RandomBatchQuality.LOW);

            MainEditorFrame tinaInternalFrame = getJFrame(MainEditorFrame.class);
            tinaInternalFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
              public void componentResized(java.awt.event.ComponentEvent e) {
                triggerRefreshFlameImage();
                //tinaController.refreshFlameImage(true, false, 1, true, false);
              }
            });
            if (!tinaInternalFrame.isVisible()) {
              tinaInternalFrame.setVisible(true);
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        TipOfTheDayFrame tipOfTheDayFrame = getJFrame(TipOfTheDayFrame.class);
        if (!tipOfTheDayFrame.isVisible() && Prefs.getPrefs().isShowTipsAtStartup()) {
          tipOfTheDayFrame.setVisible(true);
        }
        tipOfTheDayFrame.toFront();

      }
    });
  }


  class RefreshFlameImageThread implements Runnable {
    private AtomicBoolean cancelSignalled = new AtomicBoolean();
    private boolean done;

    @Override
    public void run() {
      done = false;
      try {
        cancelSignalled.set(false);
        for (int i = 0; i < 40 && !cancelSignalled.get(); i++) {
          try {
            TimeUnit.MILLISECONDS.sleep(5);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        if (!cancelSignalled.get()) {
          tinaController.fastRefreshFlameImage(true, false, 1);
        }
      }
      finally {
        done = true;
      }
    }

    public boolean isDone() {
      return done;
    }

    public void signalCancel() {
      cancelSignalled.set(true);
    }
  }

  private RefreshFlameImageThread resizeRefreshFlameImageThread = null;

  void triggerRefreshFlameImage() {
    if(resizeRefreshFlameImageThread!=null) {
      resizeRefreshFlameImageThread.signalCancel();
    }
    resizeRefreshFlameImageThread = new RefreshFlameImageThread();
    new Thread(resizeRefreshFlameImageThread).start();
  }


  private BufferedImage backgroundImage;

  @SuppressWarnings("unchecked")
  public <T extends JFrame> JFrameHolder<T> getJFrameHolder(Class<T> frameType) {
    for (FrameHolder internalFrame : mainInternalFrames) {
      if(internalFrame instanceof  DefaultJFrameHolder) {
        if (frameType.equals(((DefaultJFrameHolder)internalFrame).getFrameType())) {
          return (JFrameHolder<T>) internalFrame;
        }
      }
    }
    for (FrameHolder internalFrame : settingsInternalFrames) {
      if(internalFrame instanceof  DefaultJFrameHolder) {
        if (frameType.equals(((DefaultJFrameHolder)internalFrame).getFrameType())) {
          return (JFrameHolder<T>) internalFrame;
        }
      }
    }
    for (FrameHolder internalFrame : helpInternalFrames) {
      if(internalFrame instanceof  DefaultJFrameHolder) {
        if (frameType.equals(((DefaultJFrameHolder)internalFrame).getFrameType())) {
          return (JFrameHolder<T>) internalFrame;
        }
      }
    }
    return null;
  }

  public <T extends JFrame> T getJFrame(Class<T> frameType) {
    JFrameHolder<T> frameHolder = getJFrameHolder(frameType);
    return frameHolder != null ? frameHolder.getFrame() : null;
  }

  private JMenu getWindowMenu() {
    if (windowMenu == null) {
      windowMenu = new JMenu();
      windowMenu.setText("Windows");
      for (FrameHolder internalFrame : mainInternalFrames) {
        windowMenu.add(internalFrame.getMenuItem());
      }
    }
    return windowMenu;
  }

  private JMenuItem getOpenImageMenuItem() {
    if (openImageMenuItem == null) {
      openImageMenuItem = new JMenuItem();
      openImageMenuItem.setText("Open image...");
      openImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          openImageMenuItem_actionPerformed(e);
        }
      });
    }
    return openImageMenuItem;
  }

  private JMenuItem getOpenFlameMenuItem() {
    if (openFlameMenuItem == null) {
      openFlameMenuItem = new JMenuItem();
      openFlameMenuItem.setText("Open flame...");
      openFlameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
              Event.CTRL_MASK, true));
      openFlameMenuItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          openFlameMenuItem_actionPerformed(e);
        }
      });
    }
    return openFlameMenuItem;
  }

  private JPanel getShowMessageTopPnl() {
    if (showMessageTopPnl == null) {
      showMessageTopPnl = new JPanel();
      showMessageTopPnl.setLayout(new GridBagLayout());
      showMessageTopPnl.setPreferredSize(new Dimension(0, 10));
    }
    return showMessageTopPnl;
  }

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

  private JPanel getShowMessageLeftPnl() {
    if (showMessageLeftPnl == null) {
      showMessageLeftPnl = new JPanel();
      showMessageLeftPnl.setLayout(new GridBagLayout());
      showMessageLeftPnl.setPreferredSize(new Dimension(10, 0));
    }
    return showMessageLeftPnl;
  }

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
      showMessageDlg = new JDialog(getMainFrame());
      showMessageDlg.setSize(new Dimension(355, 205));
      showMessageDlg
          .setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      showMessageDlg.setModal(true);
      showMessageDlg.setTitle("System message");
      showMessageDlg.setContentPane(getShowMessageDlgContentPanel());
    }
    return showMessageDlg;
  }


  private JFrame getMainFrame() {
    return getJFrame(MainEditorFrame.class);
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
      showErrorDlg = new JDialog(getMainFrame());
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

  private static JWildfire application;

  public static JWildfire getInstance() {
    return application;
  }

  public static void main(final String[] args) {
    {
      Logger root = Logger.getLogger("");
      root.setLevel(Level.OFF);
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setUserLookAndFeel();
        application = new JWildfire();
        application.initUI();
        application.getMainFrame().setVisible(true);
        application.initApp();
        try {
          application.getJFrame(MainEditorFrame.class).toFront();
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
    if (!Tools.OSType.MAC.equals(Tools.getOSType())) {
      try {
        LookAndFeelType.NIMBUS.changeTo();
        return;
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
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
      mainJMenuBar.add(getSettingsMenu());
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
      fileMenu.add(getOpenFlameMenuItem());
      fileMenu.add(getOpenImageMenuItem());
      fileMenu.add(getExitMenuItem());
    }
    return fileMenu;
  }

  private JMenu getHelpMenu() {
    if (helpMenu == null) {
      helpMenu = new JMenu();
      helpMenu.setText("Help");
      for (FrameHolder internalFrame : helpInternalFrames) {
        helpMenu.add(internalFrame.getMenuItem());
      }
    }
    return helpMenu;
  }

  private JMenu getSettingsMenu() {
    if (settingsMenu == null) {
      settingsMenu = new JMenu();
      settingsMenu.setText("Settings");
      for (FrameHolder internalFrame : settingsInternalFrames) {
          settingsMenu.add(internalFrame.getMenuItem());
      }
    }
    return settingsMenu;
  }

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

  private void openImageMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    try {
      mainController.loadImage(true);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void openFlameMenuItem_actionPerformed(java.awt.event.ActionEvent e) {
    try {
      tinaController.loadFlameButton_actionPerformed(e);
    }
    catch (Throwable ex) {
      mainController.handleError(ex);
    }
    enableControls();
  }

  private void initApp() {
    getJFrame(PreferencesFrame.class).initApp();
    getJFrame(OperatorsFrame.class).initApp();
    getJFrame(FormulaExplorerFrame.class).initApp();

    mainController.refreshWindowMenu();

    try {
      formulaExplorerController.calculate();
    }
    catch (Exception e) {
      e.printStackTrace();
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

  void enableControls() {
    for (FrameHolder internalFrame : mainInternalFrames) {
      internalFrame.enableMenu();
    }
    for (FrameHolder internalFrame : settingsInternalFrames) {
      internalFrame.enableMenu();
    }
    for (FrameHolder internalFrame : helpInternalFrames) {
      internalFrame.enableMenu();
    }
    getJFrame(PreferencesFrame.class).enableControls();
    getJFrame(OperatorsFrame.class).enableControls();
  }

  private void closeApp() {
    String ObjButtons[] = { "Yes", "No" };
    int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
    if (PromptResult != JOptionPane.YES_OPTION) {
      return;
    }
/*
    {
      Dimension size = jFrame.getSize();
      Point pos = jFrame.getLocation();
      WindowPrefs wPrefs = prefs.getWindowPrefs(WindowPrefs.WINDOW_DESKTOP);
      wPrefs.setLeft(pos.x);
      wPrefs.setTop(pos.y);
      wPrefs.setWidth(size.width);
      wPrefs.setHeight(size.height);
    }
*/
    for (FrameHolder internalFrame : mainInternalFrames) {
      internalFrame.saveWindowPrefs();
    }
    for (FrameHolder internalFrame : settingsInternalFrames) {
      internalFrame.saveWindowPrefs();
    }
    for (FrameHolder internalFrame : helpInternalFrames) {
      internalFrame.saveWindowPrefs();
    }
    try {
      prefs.saveToFile();
      tinaController.saveScriptProps();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    RandomGeneratorFactory.cleanup();
    System.exit(0);
  }

  public <T extends JFrame> void showJFrame(Class<T> frameType) {
    try {
      JFrameHolder<T> frameHolder = getJFrameHolder(frameType);
      if (frameHolder != null) {
        T frame = frameHolder.getFrame();
        if (!frame.isVisible()) {
          frameHolder.getMenuItem().doClick();
        }
        frame.toFront();
      }
    }
    finally {
      enableControls();
    }
  }

  public void removeImageBuffer(JFrame frame) {
    mainController.refreshWindowMenu();
    enableControls();
  }

  public void saveImageBuffer(JFrame frame) {
    mainController.saveImageBuffer(frame);
  }
}