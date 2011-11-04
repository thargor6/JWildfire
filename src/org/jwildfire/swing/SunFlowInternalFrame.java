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
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.sunflow.system.ImagePanel;

public class SunflowInternalFrame extends JInternalFrame {
  private static final long serialVersionUID = 1L;
  private SunflowController sunflowController;
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
  public SunflowInternalFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1030, 627);
    this.setClosable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setIconifiable(true);
    this.setTitle("SunFlow");
    this.setVisible(true);
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
      jContentPane.add(getNorthPanel(), BorderLayout.NORTH);
      jContentPane.add(getSouthPanel(), BorderLayout.SOUTH);
      jContentPane.add(getHSplitPane(), BorderLayout.CENTER);
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
      northPanel = new JPanel();
      northPanel.setLayout(new GridBagLayout());
      northPanel.setPreferredSize(new Dimension(0, 26));
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
      hSplitPane.setDividerSize(10);
      hSplitPane.setLeftComponent(getLeftSplitPanel());
      hSplitPane.setRightComponent(getRightSplitPanel());
      hSplitPane.setDividerLocation(500);
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
  private JTextArea editorTextArea = null;
  private JTextArea consoleTextArea = null;
  private JButton buildSceneButton = null;
  private JButton loadSceneButton = null;
  private JButton renderSceneButton = null;

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
      rightTabbedPane.addTab("Editor", null, getEditorPanel(), null);
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
      editorNorthPanel.add(getRenderSceneButton(), null);
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
      consoleNorthPanel = new JPanel();
      consoleNorthPanel.setLayout(new GridBagLayout());
      consoleNorthPanel.setPreferredSize(new Dimension(0, 50));
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
      consoleCenterPanel.add(getConsoleTextArea(), BorderLayout.CENTER);
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
      editorCenterPanel.add(getEditorTextArea(), BorderLayout.CENTER);
    }
    return editorCenterPanel;
  }

  /**
   * This method initializes editorTextArea	
   * 	
   * @return javax.swing.JTextArea	
   */
  JTextArea getEditorTextArea() {
    if (editorTextArea == null) {
      editorTextArea = new JTextArea();
    }
    return editorTextArea;
  }

  /**
   * This method initializes consoleTextArea	
   * 	
   * @return javax.swing.JTextArea	
   */
  JTextArea getConsoleTextArea() {
    if (consoleTextArea == null) {
      consoleTextArea = new JTextArea();
    }
    return consoleTextArea;
  }

  /**
   * This method initializes buildSceneButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getBuildSceneButton() {
    if (buildSceneButton == null) {
      buildSceneButton = new JButton();
      buildSceneButton.setPreferredSize(new Dimension(141, 26));
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
      loadSceneButton.setPreferredSize(new Dimension(141, 26));
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
   * This method initializes renderSceneButton	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getRenderSceneButton() {
    if (renderSceneButton == null) {
      renderSceneButton = new JButton();
      renderSceneButton.setPreferredSize(new Dimension(141, 26));
      renderSceneButton.setMnemonic(KeyEvent.VK_R);
      renderSceneButton.setText("Render Scene");
      renderSceneButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          sunflowController.renderScene();
        }
      });
    }
    return renderSceneButton;
  }

  public void setSunflowController(SunflowController sunflowController) {
    this.sunflowController = sunflowController;
  }

  public ImagePanel getImagePanel() {
    getLeftSplitPanel();
    return imagePanel;
  }

} //  @jve:decl-index=0:visual-constraint="10,10"
