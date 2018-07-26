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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class FlameBrowserFrame extends JFrame {
  private TinaController tinaController;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;

  public FlameBrowserFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(800, 600);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT + 160, JWildfire.DEFAULT_WINDOW_TOP + 80));
    //this.setClosable(true);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    //this.setIconifiable(true);
    this.setTitle("Flame browser");
    this.setVisible(false);
    this.setResizable(true);
    //this.setMaximizable(false);
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
      jContentPane.add(getPanel_72(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  private JPanel panel_72;
  private JPanel flameBrowserRootTopPanel;
  private JPanel flameBrowserRootBottomPanel;
  private JPanel flameBrowserTreePanel;
  private JPanel flameBrowserDetailPanel;
  private JPanel flameBrowserImagesPanel;
  private JScrollPane flameBrowserTreeScrollPane;
  private JTree flameBrowserTree;
  private JButton flameBrowserRefreshBtn;
  private JButton flameBrowserToEditorBtn;
  private JButton flameBrowserDeleteBtn;
  private JButton flameBrowserRenameBtn;
  private JButton flameBrowserChangeFolderBtn;
  private JButton flameBrowserToBatchRendererBtn;
  private JButton flameBrowserCopyToBtn;
  private JButton flameBrowserMoveToBtn;
  private JButton flameBrowserToMeshGenBtn;

  private JPanel getPanel_72() {
    if (panel_72 == null) {
      panel_72 = new JPanel();
      panel_72.setLayout(new BorderLayout(0, 0));
      panel_72.add(getFlameBrowserRootTopPanel(), BorderLayout.NORTH);
      panel_72.add(getFlameBrowserRootBottomPanel(), BorderLayout.CENTER);
    }
    return panel_72;
  }

  private JPanel getFlameBrowserRootTopPanel() {
    if (flameBrowserRootTopPanel == null) {
      flameBrowserRootTopPanel = new JPanel();
      flameBrowserRootTopPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
      flameBrowserRootTopPanel.setPreferredSize(new Dimension(10, 36));
      flameBrowserRootTopPanel.setLayout(new BorderLayout(0, 0));
      flameBrowserRootTopPanel.add(getFlameBrowserRefreshBtn(), BorderLayout.WEST);
      flameBrowserRootTopPanel.add(getFlameBrowserChangeFolderBtn());
    }
    return flameBrowserRootTopPanel;
  }

  private JPanel getFlameBrowserRootBottomPanel() {
    if (flameBrowserRootBottomPanel == null) {
      flameBrowserRootBottomPanel = new JPanel();
      flameBrowserRootBottomPanel.setLayout(new BorderLayout(0, 0));
      flameBrowserRootBottomPanel.add(getFlameBrowserTreePanel(), BorderLayout.WEST);
      flameBrowserRootBottomPanel.add(getFlameBrowserDetailPanel(), BorderLayout.EAST);
      flameBrowserRootBottomPanel.add(getFlameBrowserImagesPanel(), BorderLayout.CENTER);
    }
    return flameBrowserRootBottomPanel;
  }

  private JPanel getFlameBrowserTreePanel() {
    if (flameBrowserTreePanel == null) {
      flameBrowserTreePanel = new JPanel();
      flameBrowserTreePanel.setPreferredSize(new Dimension(200, 10));
      flameBrowserTreePanel.setLayout(new BorderLayout(0, 0));
      flameBrowserTreePanel.add(getFlameBrowserTreeScrollPane(), BorderLayout.CENTER);
    }
    return flameBrowserTreePanel;
  }

  private JPanel getFlameBrowserDetailPanel() {
    if (flameBrowserDetailPanel == null) {
      flameBrowserDetailPanel = new JPanel();
      flameBrowserDetailPanel.setPreferredSize(new Dimension(120, 10));
      flameBrowserDetailPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
      flameBrowserDetailPanel.add(getFlameBrowserToEditorBtn());
      flameBrowserDetailPanel.add(getFlameBrowserToBatchRendererBtn());
      flameBrowserDetailPanel.add(getFlameBrowserToMeshGenBtn());
      flameBrowserDetailPanel.add(getFlameBrowserDeleteBtn());
      flameBrowserDetailPanel.add(getFlameBrowserRenameBtn());
      flameBrowserDetailPanel.add(getFlameBrowserCopyToBtn());
      flameBrowserDetailPanel.add(getFlameBrowserMoveToBtn());
    }
    return flameBrowserDetailPanel;
  }

  JPanel getFlameBrowserImagesPanel() {
    if (flameBrowserImagesPanel == null) {
      flameBrowserImagesPanel = new JPanel();
      flameBrowserImagesPanel.setLayout(new BorderLayout(0, 0));
    }
    return flameBrowserImagesPanel;
  }

  private JScrollPane getFlameBrowserTreeScrollPane() {
    if (flameBrowserTreeScrollPane == null) {
      flameBrowserTreeScrollPane = new JScrollPane();
      flameBrowserTreeScrollPane.setViewportView(getFlameBrowserTree());
    }
    return flameBrowserTreeScrollPane;
  }

  JTree getFlameBrowserTree() {
    if (flameBrowserTree == null) {
      flameBrowserTree = new JTree();
      flameBrowserTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          if (tinaController != null) {
            tinaController.getFlameBrowserController().flamesTree_changed();
          }
        }
      });
    }
    return flameBrowserTree;
  }

  JButton getFlameBrowserRefreshBtn() {
    if (flameBrowserRefreshBtn == null) {
      flameBrowserRefreshBtn = new JButton();
      flameBrowserRefreshBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().refreshBtn_clicked();
        }
      });
      flameBrowserRefreshBtn.setText("Refresh");
      flameBrowserRefreshBtn.setPreferredSize(new Dimension(192, 24));
      flameBrowserRefreshBtn.setMnemonic(KeyEvent.VK_R);
      flameBrowserRefreshBtn.setMinimumSize(new Dimension(100, 46));
      flameBrowserRefreshBtn.setMaximumSize(new Dimension(32000, 46));
      flameBrowserRefreshBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserRefreshBtn;
  }

  JButton getFlameBrowserToEditorBtn() {
    if (flameBrowserToEditorBtn == null) {
      flameBrowserToEditorBtn = new JButton();
      flameBrowserToEditorBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().toEditorBtn_clicked();
        }
      });
      flameBrowserToEditorBtn.setToolTipText("Copy the current fractal into the Editor");
      flameBrowserToEditorBtn.setText("To Editor");
      flameBrowserToEditorBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserToEditorBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserToEditorBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserToEditorBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserToEditorBtn;
  }

  JButton getFlameBrowserDeleteBtn() {
    if (flameBrowserDeleteBtn == null) {
      flameBrowserDeleteBtn = new JButton();
      flameBrowserDeleteBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().deleteBtn_clicked();
        }
      });
      flameBrowserDeleteBtn.setToolTipText("Delete the currently selected flame");
      flameBrowserDeleteBtn.setText("Delete");
      flameBrowserDeleteBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserDeleteBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserDeleteBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserDeleteBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserDeleteBtn;
  }

  JButton getFlameBrowserRenameBtn() {
    if (flameBrowserRenameBtn == null) {
      flameBrowserRenameBtn = new JButton();
      flameBrowserRenameBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().renameBtn_clicked();
        }
      });
      flameBrowserRenameBtn.setToolTipText("Rename the currently selected flame");
      flameBrowserRenameBtn.setText("Rename...");
      flameBrowserRenameBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserRenameBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserRenameBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserRenameBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserRenameBtn;
  }

  JButton getFlameBrowserChangeFolderBtn() {
    if (flameBrowserChangeFolderBtn == null) {
      flameBrowserChangeFolderBtn = new JButton();
      flameBrowserChangeFolderBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().changeFolderBtn_clicked();
        }
      });
      flameBrowserChangeFolderBtn.setText("Change folder...");
      flameBrowserChangeFolderBtn.setPreferredSize(new Dimension(125, 46));
      flameBrowserChangeFolderBtn.setMnemonic(KeyEvent.VK_F);
      flameBrowserChangeFolderBtn.setMinimumSize(new Dimension(100, 46));
      flameBrowserChangeFolderBtn.setMaximumSize(new Dimension(32000, 46));
      flameBrowserChangeFolderBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserChangeFolderBtn;
  }

  JButton getFlameBrowserToBatchRendererBtn() {
    if (flameBrowserToBatchRendererBtn == null) {
      flameBrowserToBatchRendererBtn = new JButton();
      flameBrowserToBatchRendererBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().toBatchRendererBtn_clicked();
        }
      });
      flameBrowserToBatchRendererBtn.setToolTipText("Schedule the current fractal for rendering in the Batch Renderer");
      flameBrowserToBatchRendererBtn.setText("To Batch Renderer");
      flameBrowserToBatchRendererBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserToBatchRendererBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserToBatchRendererBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserToBatchRendererBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserToBatchRendererBtn;
  }

  JButton getFlameBrowserCopyToBtn() {
    if (flameBrowserCopyToBtn == null) {
      flameBrowserCopyToBtn = new JButton();
      flameBrowserCopyToBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().copyToBtnClicked();
        }
      });
      flameBrowserCopyToBtn.setToolTipText("Copy the currently selected flame into another directory");
      flameBrowserCopyToBtn.setText("Copy to...");
      flameBrowserCopyToBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserCopyToBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserCopyToBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserCopyToBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserCopyToBtn;
  }

  JButton getFlameBrowserMoveToBtn() {
    if (flameBrowserMoveToBtn == null) {
      flameBrowserMoveToBtn = new JButton();
      flameBrowserMoveToBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().moveToBtnClicked();
        }
      });
      flameBrowserMoveToBtn.setToolTipText("Move the currently selected flame to another directory");
      flameBrowserMoveToBtn.setText("Move to...");
      flameBrowserMoveToBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserMoveToBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserMoveToBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserMoveToBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserMoveToBtn;
  }

  JButton getFlameBrowserToMeshGenBtn() {
    if (flameBrowserToMeshGenBtn == null) {
      flameBrowserToMeshGenBtn = new JButton();
      flameBrowserToMeshGenBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tinaController.getFlameBrowserController().toMeshGenBtn_clicked();
        }
      });
      flameBrowserToMeshGenBtn.setToolTipText("Import this flame into the 3DMesh Generator module");
      flameBrowserToMeshGenBtn.setText("To Mesh Gen");
      flameBrowserToMeshGenBtn.setPreferredSize(new Dimension(112, 24));
      flameBrowserToMeshGenBtn.setMinimumSize(new Dimension(100, 24));
      flameBrowserToMeshGenBtn.setMaximumSize(new Dimension(32000, 24));
      flameBrowserToMeshGenBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    }
    return flameBrowserToMeshGenBtn;
  }

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }

}
