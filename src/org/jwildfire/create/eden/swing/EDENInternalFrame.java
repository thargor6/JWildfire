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
package org.jwildfire.create.eden.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.MainController;
import org.sunflow.system.ImagePanel;

public class EDENInternalFrame extends JInternalFrame {
  private static final long serialVersionUID = 1L;
  private EDENController sunflowController;
  private JPanel jContentPane = null;
  private JPanel northPanel = null;
  private JPanel southPanel = null;
  private JSplitPane hSplitPane = null;
  private JPanel leftSplitPanel = null;
  private JPanel rightSplitPanel = null;
  private JTabbedPane rightTabbedPane = null;
  private JPanel editorPanel = null;
  private JPanel consolePanel = null;

  /**
   * This is the xxx default constructor
   */
  public EDENInternalFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1188, 740);
    this.setClosable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setIconifiable(true);
    this.setTitle("Structure Synthesizer");
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
      try {
        jContentPane.setLayout(new BorderLayout());
        jContentPane.add(getNorthPanel(), BorderLayout.NORTH);
        jContentPane.add(getSouthPanel(), BorderLayout.SOUTH);
        jContentPane.add(getHSplitPane(), BorderLayout.CENTER);
      }
      catch (Throwable ex) {
        //ex.printStackTrace();
      }
    }
    return jContentPane;
  }

  /**
   * This method initializes northPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getNorthPanel() {
    if (northPanel == null) {
      FlowLayout flowLayout1 = new FlowLayout();
      flowLayout1.setAlignment(FlowLayout.RIGHT);
      northPanel = new JPanel();
      northPanel.setLayout(flowLayout1);
      northPanel.setPreferredSize(new Dimension(0, 36));
      northPanel.add(getRenderButton(), null);
      northPanel.add(getIprButton(), null);
      northPanel.add(getCancelRenderButton(), null);
    }
    return northPanel;
  }

  /**
   * This method initializes southPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getSouthPanel() {
    if (southPanel == null) {
      southPanel = new JPanel();
      southPanel.setLayout(new GridBagLayout());
      southPanel.setPreferredSize(new Dimension(0, 26));
    }
    return southPanel;
  }

  /**
   * This method initializes hSplitPane	
   * 	
   * @return javax.swing.JSplitPane	
   */
  private JSplitPane getHSplitPane() {
    if (hSplitPane == null) {
      hSplitPane = new JSplitPane();
      hSplitPane.setDividerSize(8);
      hSplitPane.setOneTouchExpandable(true);
      hSplitPane.setLeftComponent(getLeftSplitPanel());
      hSplitPane.setRightComponent(getRightSplitPanel());
      hSplitPane.setDividerLocation(700);
    }
    return hSplitPane;
  }

  /**
   * This method initializes leftSplitPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private ImagePanel imagePanel;
  private JPanel editorNorthPanel = null;
  private JPanel consoleNorthPanel = null;
  private JPanel consoleCenterPanel = null;
  private JPanel editorCenterPanel = null;
  private JButton buildSceneButton = null;
  private JButton loadSceneButton = null;
  private JButton renderButton = null;
  private JButton cancelRenderButton = null;
  private JButton iprButton = null;
  private JButton saveSceneButton = null;
  private JButton clearConsoleButton = null;
  private JScrollPane editorScrollPane = null;
  private JTextArea editorTextArea1 = null;
  private JScrollPane consoleScrollPane = null;
  private JTextArea consoleTextArea1 = null;
  private JButton newSceneButton = null;

  private JPanel getLeftSplitPanel() {
    if (leftSplitPanel == null) {
      leftSplitPanel = new JPanel();
      leftSplitPanel.setLayout(new BorderLayout());
      imagePanel = new ImagePanel();
      leftSplitPanel.add(imagePanel, BorderLayout.CENTER);
    }
    return leftSplitPanel;
  }

  /**
   * This method initializes rightSplitPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getRightSplitPanel() {
    if (rightSplitPanel == null) {
      rightSplitPanel = new JPanel();
      rightSplitPanel.setLayout(new BorderLayout());
      rightSplitPanel.add(getRightTabbedPane(), BorderLayout.CENTER);
    }
    return rightSplitPanel;
  }

  /**
   * This method initializes rightTabbedPane	
   * 	
   * @return javax.swing.JTabbedPane	
   */
  private JTabbedPane getRightTabbedPane() {
    if (rightTabbedPane == null) {
      rightTabbedPane = new JTabbedPane();
      rightTabbedPane.addTab("Scene Editor", null, getEditorPanel(), null);
      rightTabbedPane.addTab("Console", null, getConsolePanel(), null);
    }
    return rightTabbedPane;
  }

  /**
   * This method initializes editorPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getEditorPanel() {
    if (editorPanel == null) {
      editorPanel = new JPanel();
      editorPanel.setLayout(new BorderLayout());
      editorPanel.add(getEditorNorthPanel(), BorderLayout.NORTH);
      editorPanel.add(getEditorCenterPanel(), BorderLayout.CENTER);
    }
    return editorPanel;
  }

  /**
   * This method initializes consolePanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getConsolePanel() {
    if (consolePanel == null) {
      consolePanel = new JPanel();
      consolePanel.setLayout(new BorderLayout());
      consolePanel.add(getConsoleNorthPanel(), BorderLayout.NORTH);
      consolePanel.add(getConsoleCenterPanel(), BorderLayout.CENTER);
    }
    return consolePanel;
  }

  /**
   * This method initializes editorNorthPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getEditorNorthPanel() {
    if (editorNorthPanel == null) {
      FlowLayout flowLayout = new FlowLayout();
      flowLayout.setAlignment(FlowLayout.LEFT);
      editorNorthPanel = new JPanel();
      editorNorthPanel.setLayout(flowLayout);
      editorNorthPanel.setPreferredSize(new Dimension(0, 36));
      editorNorthPanel.add(getBuildSceneButton(), null);
      editorNorthPanel.add(getLoadSceneButton(), null);
      editorNorthPanel.add(getSaveSceneButton(), null);
      editorNorthPanel.add(getNewSceneButton(), null);
    }
    return editorNorthPanel;
  }

  /**
   * This method initializes consoleNorthPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getConsoleNorthPanel() {
    if (consoleNorthPanel == null) {
      FlowLayout flowLayout2 = new FlowLayout();
      flowLayout2.setAlignment(FlowLayout.LEFT);
      consoleNorthPanel = new JPanel();
      consoleNorthPanel.setLayout(flowLayout2);
      consoleNorthPanel.setPreferredSize(new Dimension(0, 36));
      consoleNorthPanel.add(getClearConsoleButton(), null);
    }
    return consoleNorthPanel;
  }

  /**
   * This method initializes consoleCenterPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getConsoleCenterPanel() {
    if (consoleCenterPanel == null) {
      consoleCenterPanel = new JPanel();
      consoleCenterPanel.setLayout(new BorderLayout());
      consoleCenterPanel.add(getConsoleScrollPane(), BorderLayout.CENTER);
    }
    return consoleCenterPanel;
  }

  /**
   * This method initializes editorCenterPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getEditorCenterPanel() {
    if (editorCenterPanel == null) {
      editorCenterPanel = new JPanel();
      editorCenterPanel.setLayout(new BorderLayout());
      editorCenterPanel.add(getEditorScrollPane(), BorderLayout.CENTER);
    }
    return editorCenterPanel;
  }

  /**
   * This method initializes buildSceneButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBuildSceneButton() {
    if (buildSceneButton == null) {
      buildSceneButton = new JButton();
      buildSceneButton.setPreferredSize(new Dimension(111, 26));
      buildSceneButton.setMnemonic(KeyEvent.VK_B);
      buildSceneButton.setText("Build Scene");
      buildSceneButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.buildScene();
        }
      });
    }
    return buildSceneButton;
  }

  /**
   * This method initializes loadSceneButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getLoadSceneButton() {
    if (loadSceneButton == null) {
      loadSceneButton = new JButton();
      loadSceneButton.setPreferredSize(new Dimension(111, 26));
      loadSceneButton.setMnemonic(KeyEvent.VK_L);
      loadSceneButton.setText("Load Scene");
      loadSceneButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.loadScene();
        }
      });
    }
    return loadSceneButton;
  }

  /**
   * This method initializes renderButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRenderButton() {
    if (renderButton == null) {
      renderButton = new JButton();
      renderButton.setPreferredSize(new Dimension(141, 26));
      renderButton.setMnemonic(KeyEvent.VK_R);
      renderButton.setText("Render");
      renderButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.renderScene();
        }
      });
    }
    return renderButton;
  }

  public void setSunflowController(EDENController sunflowController) {
    this.sunflowController = sunflowController;
  }

  private ImagePanel getImagePanel() {
    getLeftSplitPanel();
    return imagePanel;
  }

  /**
   * This method initializes cancelRenderButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getCancelRenderButton() {
    if (cancelRenderButton == null) {
      cancelRenderButton = new JButton();
      cancelRenderButton.setPreferredSize(new Dimension(141, 26));
      cancelRenderButton.setText("Cancel");
      cancelRenderButton.setMnemonic(KeyEvent.VK_C);
      cancelRenderButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.cancelRendering();
        }
      });
    }
    return cancelRenderButton;
  }

  /**
   * This method initializes iprButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getIprButton() {
    if (iprButton == null) {
      iprButton = new JButton();
      iprButton.setPreferredSize(new Dimension(141, 26));
      iprButton.setText("IPR");
      iprButton.setMnemonic(KeyEvent.VK_I);
      iprButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.iprScene();
        }
      });
    }
    return iprButton;
  }

  /**
   * This method initializes saveSceneButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getSaveSceneButton() {
    if (saveSceneButton == null) {
      saveSceneButton = new JButton();
      saveSceneButton.setPreferredSize(new Dimension(111, 26));
      saveSceneButton.setText("Save Scene");
      saveSceneButton.setMnemonic(KeyEvent.VK_S);
      saveSceneButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.saveScene();
        }
      });
    }
    return saveSceneButton;
  }

  /**
   * This method initializes clearConsoleButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getClearConsoleButton() {
    if (clearConsoleButton == null) {
      clearConsoleButton = new JButton();
      clearConsoleButton.setPreferredSize(new Dimension(141, 26));
      clearConsoleButton.setText("Clear");
      clearConsoleButton.setMnemonic(KeyEvent.VK_L);
      clearConsoleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.clearConsole();
        }
      });
    }
    return clearConsoleButton;
  }

  /**
   * This method initializes editorScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getEditorScrollPane() {
    if (editorScrollPane == null) {
      editorScrollPane = new JScrollPane();
      editorScrollPane.setViewportView(getEditorTextArea());
    }
    return editorScrollPane;
  }

  /**
   * This method initializes editorTextArea1	
   * 	
   * @return javax.swing.JTextArea	
   */
  private JTextArea getEditorTextArea() {
    if (editorTextArea1 == null) {
      editorTextArea1 = new JTextArea();
    }
    return editorTextArea1;
  }

  /**
   * This method initializes consoleScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getConsoleScrollPane() {
    if (consoleScrollPane == null) {
      consoleScrollPane = new JScrollPane();
      consoleScrollPane.setViewportView(getConsoleTextArea());
    }
    return consoleScrollPane;
  }

  /**
   * This method initializes consoleTextArea1	
   * 	
   * @return javax.swing.JTextArea	
   */
  private JTextArea getConsoleTextArea() {
    if (consoleTextArea1 == null) {
      consoleTextArea1 = new JTextArea();
    }
    return consoleTextArea1;
  }

  public EDENController createController(MainController pMainController, ErrorHandler pErrorHandler, Prefs pPrefs) {
    sunflowController = new EDENController(pMainController, pErrorHandler, pPrefs, getEditorTextArea(), getConsoleTextArea(),
        getImagePanel(), getRenderButton(), getIprButton(), getLoadSceneButton(), getCancelRenderButton(),
        getBuildSceneButton(), getSaveSceneButton(), getClearConsoleButton(), getNewSceneButton());
    return sunflowController;
  }

  /**
   * This method initializes newSceneButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getNewSceneButton() {
    if (newSceneButton == null) {
      newSceneButton = new JButton();
      newSceneButton.setPreferredSize(new Dimension(111, 26));
      newSceneButton.setText("New Scene");
      newSceneButton.setMnemonic(KeyEvent.VK_N);
      newSceneButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.newScene();
        }
      });
    }
    return newSceneButton;
  }
} //  @jve:decl-index=0:visual-constraint="10,10"
