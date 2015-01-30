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
package org.jwildfire.create.iflames.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.swing.MainController;
import org.jwildfire.swing.StandardErrorHandler;

public class IFlamesInternalFrame extends JInternalFrame {
  private static final long serialVersionUID = 1L;
  private IFlamesController iflamesController;
  private JPanel jContentPane = null;
  private JPanel mainLeftPanel;
  private JPanel mainCenterPanel;
  private JPanel mainRightPanel;
  private JPanel mainBottomPanel;
  private JPanel mainTopPanel;
  private JButton newButton;
  private JButton loadFlamesButton;
  private JButton loadImagesButton;
  private JButton renderFlameButton;
  private JButton undoButton;
  private JButton redoButton;
  private JComboBox baseFlameCmb;
  private JButton loadIFlameFromClipboardButton;
  private JButton loadIFlameButton;
  private JButton saveIFlameToClipboardButton;
  private JButton saveIFlameButton;
  private JToggleButton autoRefreshButton;
  private JPanel baseFlamePreviewRootPnl;
  private JButton refreshIFlameButton;
  private JProgressBar mainProgressBar;
  private JPanel panel;
  private JPanel flameStackRootPanel;
  private JPanel panel_1;
  private JPanel imageStackRootPanel;
  private JComboBox resolutionProfileCmb;
  private JPanel panel_2;
  private JToggleButton edgesNorthButton;
  private JToggleButton edgesWestButton;
  private JToggleButton edgesEastButton;
  private JToggleButton edgesSouthButton;
  private JToggleButton erodeButton;
  private JPanel panel_5;
  private JPanel panel_6;
  private JToggleButton displayPreprocessedImageButton;

  public IFlamesInternalFrame() {
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
    this.setTitle("IFlames");
    this.setVisible(false);
    this.setResizable(true);
    this.setContentPane(getJContentPane());
  }

  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());

      mainTopPanel = new JPanel();
      mainTopPanel.setBorder(new TitledBorder(null, "Main", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      mainTopPanel.setPreferredSize(new Dimension(10, 70));
      jContentPane.add(mainTopPanel, BorderLayout.NORTH);
      mainTopPanel.setLayout(null);

      newButton = new JButton();
      newButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.newButton_clicked();
        }
      });
      newButton.setText("New IFlame");
      newButton.setPreferredSize(new Dimension(125, 52));
      newButton.setMnemonic(KeyEvent.VK_N);
      newButton.setMinimumSize(new Dimension(100, 52));
      newButton.setMaximumSize(new Dimension(32000, 52));
      newButton.setFont(new Font("Dialog", Font.BOLD, 10));
      newButton.setActionCommand("New from scratch");
      newButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/document-new-7.png")));

      newButton.setBounds(205, 10, 143, 48);
      mainTopPanel.add(newButton);

      loadIFlameFromClipboardButton = new JButton();
      loadIFlameFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.loadIFlameFromClipboardButton_clicked();
        }
      });
      loadIFlameFromClipboardButton.setText("From Clipboard");
      loadIFlameFromClipboardButton.setPreferredSize(new Dimension(125, 24));
      loadIFlameFromClipboardButton.setMinimumSize(new Dimension(100, 24));
      loadIFlameFromClipboardButton.setMaximumSize(new Dimension(32000, 24));
      loadIFlameFromClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      loadIFlameFromClipboardButton.setBounds(360, 10, 143, 24);
      mainTopPanel.add(loadIFlameFromClipboardButton);

      saveIFlameToClipboardButton = new JButton();
      saveIFlameToClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.saveIFlameToClipboardButton_clicked();
        }
      });
      saveIFlameToClipboardButton.setText("To Clipboard");
      saveIFlameToClipboardButton.setPreferredSize(new Dimension(125, 24));
      saveIFlameToClipboardButton.setMinimumSize(new Dimension(100, 24));
      saveIFlameToClipboardButton.setMaximumSize(new Dimension(32000, 24));
      saveIFlameToClipboardButton.setIconTextGap(2);
      saveIFlameToClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      saveIFlameToClipboardButton.setBounds(503, 10, 143, 24);
      mainTopPanel.add(saveIFlameToClipboardButton);

      loadIFlameButton = new JButton();
      loadIFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.loadIFlameButton_clicked();
        }
      });
      loadIFlameButton.setText("Load IFlame...");
      loadIFlameButton.setPreferredSize(new Dimension(125, 24));
      loadIFlameButton.setMinimumSize(new Dimension(100, 24));
      loadIFlameButton.setMaximumSize(new Dimension(32000, 24));
      loadIFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      loadIFlameButton.setBounds(360, 34, 143, 24);
      mainTopPanel.add(loadIFlameButton);

      saveIFlameButton = new JButton();
      saveIFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.saveIFlameButton_clicked();
        }
      });
      saveIFlameButton.setText("Save IFlame...");
      saveIFlameButton.setPreferredSize(new Dimension(125, 24));
      saveIFlameButton.setMinimumSize(new Dimension(100, 24));
      saveIFlameButton.setMaximumSize(new Dimension(32000, 24));
      saveIFlameButton.setIconTextGap(2);
      saveIFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));
      saveIFlameButton.setBounds(503, 34, 143, 24);
      mainTopPanel.add(saveIFlameButton);

      resolutionProfileCmb = new JComboBox();
      resolutionProfileCmb.setPreferredSize(new Dimension(125, 24));
      resolutionProfileCmb.setMinimumSize(new Dimension(100, 24));
      resolutionProfileCmb.setMaximumSize(new Dimension(32767, 24));
      resolutionProfileCmb.setMaximumRowCount(32);
      resolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      resolutionProfileCmb.setBounds(837, 22, 143, 24);
      resolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (iflamesController != null) {
            iflamesController.saveUndoPoint();
            iflamesController.resolutionProfileCmb_changed();
          }
        }
      });
      mainTopPanel.add(resolutionProfileCmb);

      mainProgressBar = new JProgressBar();
      mainProgressBar.setBounds(988, 26, 169, 14);
      mainTopPanel.add(mainProgressBar);
      mainProgressBar.setValue(0);
      mainProgressBar.setStringPainted(true);
      mainProgressBar.setPreferredSize(new Dimension(169, 14));

      mainLeftPanel = new JPanel();
      mainLeftPanel.setBorder(new TitledBorder(null, "Image Library", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      mainLeftPanel.setPreferredSize(new Dimension(158, 10));
      jContentPane.add(mainLeftPanel, BorderLayout.WEST);
      mainLeftPanel.setLayout(new BorderLayout(0, 0));

      panel_1 = new JPanel();
      panel_1.setPreferredSize(new Dimension(10, 32));
      mainLeftPanel.add(panel_1, BorderLayout.NORTH);
      panel_1.setLayout(null);

      loadImagesButton = new JButton();
      loadImagesButton.setBounds(2, 6, 124, 24);
      panel_1.add(loadImagesButton);
      loadImagesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.loadImagesButton_clicked();
        }
      });
      loadImagesButton.setText("Add Images...");
      loadImagesButton.setPreferredSize(new Dimension(125, 24));
      loadImagesButton.setMinimumSize(new Dimension(100, 24));
      loadImagesButton.setMaximumSize(new Dimension(32000, 24));
      loadImagesButton.setFont(new Font("Dialog", Font.BOLD, 10));
      loadImagesButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/document-open-5.png")));

      imageStackRootPanel = new JPanel();
      mainLeftPanel.add(imageStackRootPanel, BorderLayout.CENTER);

      mainRightPanel = new JPanel();
      mainRightPanel.setBorder(new TitledBorder(null, "Flame Library", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      mainRightPanel.setPreferredSize(new Dimension(158, 10));
      jContentPane.add(mainRightPanel, BorderLayout.EAST);
      mainRightPanel.setLayout(new BorderLayout(0, 0));

      panel = new JPanel();
      panel.setPreferredSize(new Dimension(10, 32));
      mainRightPanel.add(panel, BorderLayout.NORTH);
      panel.setLayout(null);

      loadFlamesButton = new JButton();
      loadFlamesButton.setBounds(2, 6, 124, 24);
      panel.add(loadFlamesButton);
      loadFlamesButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.loadFlamesButton_clicked();
        }
      });
      loadFlamesButton.setText("Add Flames...");
      loadFlamesButton.setPreferredSize(new Dimension(125, 24));
      loadFlamesButton.setMinimumSize(new Dimension(100, 24));
      loadFlamesButton.setMaximumSize(new Dimension(32000, 24));
      loadFlamesButton.setFont(new Font("Dialog", Font.BOLD, 10));
      loadFlamesButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/document-open-5.png")));

      flameStackRootPanel = new JPanel();
      mainRightPanel.add(flameStackRootPanel, BorderLayout.CENTER);
      flameStackRootPanel.setLayout(new BorderLayout(0, 0));

      mainBottomPanel = new JPanel();
      mainBottomPanel.setBorder(new TitledBorder(null, "Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      mainBottomPanel.setPreferredSize(new Dimension(10, 200));
      jContentPane.add(mainBottomPanel, BorderLayout.SOUTH);
      mainBottomPanel.setLayout(new BorderLayout(0, 0));

      panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(240, 10));
      mainBottomPanel.add(panel_2, BorderLayout.WEST);
      panel_2.setLayout(null);

      JLabel lblFlame = new JLabel();
      lblFlame.setBounds(6, 6, 94, 22);
      panel_2.add(lblFlame);
      lblFlame.setText("Base-Flame");
      lblFlame.setPreferredSize(new Dimension(94, 22));
      lblFlame.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameCmb = new JComboBox();
      baseFlameCmb.setBounds(94, 6, 125, 22);
      panel_2.add(baseFlameCmb);
      baseFlameCmb.setPreferredSize(new Dimension(125, 22));
      baseFlameCmb.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlamePreviewRootPnl = new JPanel();
      baseFlamePreviewRootPnl.setBounds(6, 33, 213, 131);
      panel_2.add(baseFlamePreviewRootPnl);
      baseFlamePreviewRootPnl.setPreferredSize(new Dimension(240, 130));
      baseFlamePreviewRootPnl.setMinimumSize(new Dimension(160, 100));
      baseFlamePreviewRootPnl.setMaximumSize(new Dimension(32767, 160));
      baseFlamePreviewRootPnl.setLayout(new BorderLayout(0, 0));

      JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      mainBottomPanel.add(tabbedPane, BorderLayout.CENTER);

      JPanel panel_3 = new JPanel();
      tabbedPane.addTab("Edge Finding", null, panel_3, null);
      panel_3.setLayout(null);

      JWFNumberField erodeSizeField = new JWFNumberField();
      erodeSizeField.setMinValue(3.0);
      erodeSizeField.setMaxValue(9.0);
      erodeSizeField.setOnlyIntegers(true);
      erodeSizeField.setBounds(157, 108, 100, 24);
      panel_3.add(erodeSizeField);
      erodeSizeField.setValueStep(0.05);
      erodeSizeField.setText("");
      erodeSizeField.setPreferredSize(new Dimension(100, 24));
      erodeSizeField.setMotionPropertyName("camPosX");
      erodeSizeField.setLinkedMotionControlName("tinaCameraCamPosXSlider");
      erodeSizeField.setLinkedLabelControlName("tinaCameraCamPosXLbl");
      erodeSizeField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblSize = new JLabel();
      lblSize.setBounds(124, 108, 35, 22);
      panel_3.add(lblSize);
      lblSize.setToolTipText("");
      lblSize.setText("Size");
      lblSize.setPreferredSize(new Dimension(94, 22));
      lblSize.setName("tinaCameraCamPosXLbl");
      lblSize.setFont(new Font("Dialog", Font.BOLD, 10));

      edgesNorthButton = new JToggleButton();
      edgesNorthButton.setToolTipText("");
      edgesNorthButton.setText("Edges North");
      edgesNorthButton.setPreferredSize(new Dimension(136, 24));
      edgesNorthButton.setFont(new Font("Dialog", Font.BOLD, 10));
      edgesNorthButton.setBounds(80, 6, 105, 24);
      panel_3.add(edgesNorthButton);

      edgesWestButton = new JToggleButton();
      edgesWestButton.setToolTipText("");
      edgesWestButton.setText("Edges West");
      edgesWestButton.setPreferredSize(new Dimension(136, 24));
      edgesWestButton.setFont(new Font("Dialog", Font.BOLD, 10));
      edgesWestButton.setBounds(6, 32, 105, 24);
      panel_3.add(edgesWestButton);

      edgesEastButton = new JToggleButton();
      edgesEastButton.setToolTipText("");
      edgesEastButton.setText("Edges East");
      edgesEastButton.setPreferredSize(new Dimension(136, 24));
      edgesEastButton.setFont(new Font("Dialog", Font.BOLD, 10));
      edgesEastButton.setBounds(152, 32, 105, 24);
      panel_3.add(edgesEastButton);

      edgesSouthButton = new JToggleButton();
      edgesSouthButton.setToolTipText("");
      edgesSouthButton.setText("Edges South");
      edgesSouthButton.setPreferredSize(new Dimension(136, 24));
      edgesSouthButton.setFont(new Font("Dialog", Font.BOLD, 10));
      edgesSouthButton.setBounds(80, 58, 105, 24);
      panel_3.add(edgesSouthButton);

      erodeButton = new JToggleButton();
      erodeButton.setToolTipText("");
      erodeButton.setText("Erode");
      erodeButton.setPreferredSize(new Dimension(136, 24));
      erodeButton.setFont(new Font("Dialog", Font.BOLD, 10));
      erodeButton.setBounds(7, 108, 105, 24);
      panel_3.add(erodeButton);

      displayPreprocessedImageButton = new JToggleButton();
      displayPreprocessedImageButton.setToolTipText("Display the pre-processed image");
      displayPreprocessedImageButton.setText("Dsp Preprocessed");
      displayPreprocessedImageButton.setPreferredSize(new Dimension(136, 24));
      displayPreprocessedImageButton.setFont(new Font("Dialog", Font.BOLD, 10));
      displayPreprocessedImageButton.setBounds(372, 55, 136, 24);
      panel_3.add(displayPreprocessedImageButton);

      panel_5 = new JPanel();
      tabbedPane.addTab("Global Structure", null, panel_5, null);
      panel_5.setLayout(null);

      JWFNumberField numberField = new JWFNumberField();
      numberField.setValueStep(0.05);
      numberField.setText("");
      numberField.setPreferredSize(new Dimension(100, 24));
      numberField.setOnlyIntegers(true);
      numberField.setMotionPropertyName("camPosX");
      numberField.setMinValue(3.0);
      numberField.setMaxValue(9.0);
      numberField.setLinkedMotionControlName("tinaCameraCamPosXSlider");
      numberField.setLinkedLabelControlName("tinaCameraCamPosXLbl");
      numberField.setFont(new Font("Dialog", Font.PLAIN, 10));
      numberField.setBounds(101, 4, 100, 24);
      panel_5.add(numberField);

      JLabel label = new JLabel();
      label.setToolTipText("");
      label.setText("Size");
      label.setPreferredSize(new Dimension(94, 22));
      label.setName("tinaCameraCamPosXLbl");
      label.setFont(new Font("Dialog", Font.BOLD, 10));
      label.setBounds(6, 6, 93, 22);
      panel_5.add(label);

      panel_6 = new JPanel();
      panel_6.setPreferredSize(new Dimension(128, 10));
      mainBottomPanel.add(panel_6, BorderLayout.EAST);
      panel_6.setLayout(null);

      refreshIFlameButton = new JButton();
      refreshIFlameButton.setBounds(6, 42, 105, 24);
      panel_6.add(refreshIFlameButton);
      refreshIFlameButton.setToolTipText("Rebuild and refresh IFlame");
      refreshIFlameButton.setText("Refresh");
      refreshIFlameButton.setPreferredSize(new Dimension(125, 24));
      refreshIFlameButton.setMinimumSize(new Dimension(100, 24));
      refreshIFlameButton.setMaximumSize(new Dimension(32000, 24));
      refreshIFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));

      autoRefreshButton = new JToggleButton();
      autoRefreshButton.setBounds(6, 70, 105, 24);
      panel_6.add(autoRefreshButton);
      autoRefreshButton.setSelected(true);
      autoRefreshButton.setToolTipText("Automatically rebuild the IFlame after changes, which may be slow");
      autoRefreshButton.setText("Auto Refresh");
      autoRefreshButton.setPreferredSize(new Dimension(136, 24));
      autoRefreshButton.setFont(new Font("Dialog", Font.BOLD, 10));

      undoButton = new JButton();
      undoButton.setBounds(6, 115, 105, 24);
      panel_6.add(undoButton);
      undoButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.undoAction();
        }
      });
      undoButton.setToolTipText("Undo");
      undoButton.setText("Undo");
      undoButton.setPreferredSize(new Dimension(72, 24));
      undoButton.setMnemonic(KeyEvent.VK_Z);
      undoButton.setIconTextGap(0);
      undoButton.setFont(new Font("Dialog", Font.BOLD, 9));
      undoButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-undo-6.png")));

      redoButton = new JButton();
      redoButton.setBounds(6, 138, 105, 24);
      panel_6.add(redoButton);
      redoButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.redoAction();
        }
      });
      redoButton.setToolTipText("Redo");
      redoButton.setText("Redo");
      redoButton.setPreferredSize(new Dimension(72, 24));
      redoButton.setMnemonic(KeyEvent.VK_Y);
      redoButton.setIconTextGap(0);
      redoButton.setFont(new Font("Dialog", Font.BOLD, 9));
      redoButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/edit-redo-6.png")));

      renderFlameButton = new JButton();
      renderFlameButton.setBounds(6, 6, 103, 24);
      panel_6.add(renderFlameButton);
      renderFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.renderFlameButton_clicked();
        }
      });
      renderFlameButton.setToolTipText("Render image");
      renderFlameButton.setPreferredSize(new Dimension(42, 24));
      renderFlameButton.setMnemonic(KeyEvent.VK_R);
      renderFlameButton.setIconTextGap(0);
      renderFlameButton.setFont(new Font("Dialog", Font.BOLD, 9));
      renderFlameButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/fraqtive.png")));
      baseFlameCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameCmb_changed();
          }
        }
      });

      mainCenterPanel = new JPanel();
      mainCenterPanel.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      jContentPane.add(mainCenterPanel, BorderLayout.CENTER);
      mainCenterPanel.setLayout(new BorderLayout(0, 0));
    }
    return jContentPane;
  }

  public void createController(MainController pMainController, StandardErrorHandler pErrorHandler) {
    iflamesController = new IFlamesController(pMainController, pErrorHandler, this, getMainCenterPanel(),
        getUndoButton(), getRedoButton(), getRenderFlameButton(), getImageStackRootPanel(), getFlameStackRootPanel(),
        getLoadIFlameButton(), getLoadIFlameFromClipboardButton(), getSaveIFlameToClipboardButton(),
        getSaveIFlameButton(), getRefreshIFlameButton(), getMainProgressBar(), getAutoRefreshButton(),
        getBaseFlameCmb(), getBaseFlamePreviewRootPnl(), getResolutionProfileCmb());
  }

  public JPanel getMainLeftPanel() {
    return mainLeftPanel;
  }

  public JPanel getMainCenterPanel() {
    return mainCenterPanel;
  }

  public JPanel getMainRightPanel() {
    return mainRightPanel;
  }

  public JPanel getMainBottomPanel() {
    return mainBottomPanel;
  }

  public JPanel getMainTopPanel() {
    return mainTopPanel;
  }

  public JButton getNewButton() {
    return newButton;
  }

  public JButton getLoadFlameButton() {
    return loadFlamesButton;
  }

  public JButton getLoadImageButton() {
    return loadImagesButton;
  }

  public JButton getRenderFlameButton() {
    return renderFlameButton;
  }

  public JButton getUndoButton() {
    return undoButton;
  }

  public JButton getRedoButton() {
    return redoButton;
  }

  public JComboBox getBaseFlameCmb() {
    return baseFlameCmb;
  }

  public JButton getLoadIFlameButton() {
    return loadIFlameButton;
  }

  public JButton getSaveIFlameToClipboardButton() {
    return saveIFlameToClipboardButton;
  }

  public JButton getSaveIFlameButton() {
    return saveIFlameButton;
  }

  public JToggleButton getAutoRefreshButton() {
    return autoRefreshButton;
  }

  public JPanel getBaseFlamePreviewRootPnl() {
    return baseFlamePreviewRootPnl;
  }

  public JButton getRefreshIFlameButton() {
    return refreshIFlameButton;
  }

  public JProgressBar getMainProgressBar() {
    return mainProgressBar;
  }

  public JButton getLoadIFlameFromClipboardButton() {
    return loadIFlameFromClipboardButton;
  }

  public JPanel getFlameStackRootPanel() {
    return flameStackRootPanel;
  }

  public JPanel getImageStackRootPanel() {
    return imageStackRootPanel;
  }

  public JComboBox getResolutionProfileCmb() {
    return resolutionProfileCmb;
  }

  public JToggleButton getEdgesNorthButton() {
    return edgesNorthButton;
  }

  public JToggleButton getEdgesSouthButton() {
    return edgesSouthButton;
  }

  public JToggleButton getEdgesEastButton() {
    return edgesEastButton;
  }

  public JToggleButton getEdgesWestButton() {
    return edgesWestButton;
  }

  public JToggleButton getErodeButton() {
    return erodeButton;
  }

  public JToggleButton getDisplayPreprocessedImageButton() {
    return displayPreprocessedImageButton;
  }
}
