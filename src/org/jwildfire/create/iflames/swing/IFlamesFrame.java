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
import java.awt.Point;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.JWildfire;
import org.jwildfire.swing.MainController;
import org.jwildfire.swing.StandardErrorHandler;

public class IFlamesFrame extends JFrame {
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
  private JWFNumberField erodeSizeField;
  private JWFNumberField maxImageWidthField;
  private JWFNumberField globalScaleXField;
  private JWFNumberField globalScaleYField;
  private JWFNumberField globalScaleZField;
  private JWFNumberField globalOffsetXField;
  private JWFNumberField globalOffsetYField;
  private JWFNumberField globalOffsetZField;
  private JWFNumberField structureThresholdField;
  private JWFNumberField structureDensityField;
  private JComboBox shapeDistributionCmb;
  private JButton refreshLibraryButton;
  private JPanel panel_4;
  private JWFNumberField iflameBrightnessField;
  private JWFNumberField imageBrightnessField;
  private JWFNumberField iflameDensityField;
  private JPanel panel_7;
  private JWFNumberField baseFlameSizeField;
  private JWFNumberField baseFlameSizeVariationField;
  private JWFNumberField baseFlameRotateAlphaField;
  private JWFNumberField baseFlameRotateAlphaVariationField;
  private JWFNumberField baseFlameCentreXField;
  private JWFNumberField baseFlameCentreYField;
  private JWFNumberField baseFlameRotateBetaField;
  private JWFNumberField baseFlameRotateBetaVariationField;
  private JWFNumberField baseFlameRotateGammaField;
  private JWFNumberField baseFlameRotateGammaVariationField;
  private JWFNumberField baseFlameCentreZField;
  private JButton baseFlameFromClipboardButton;
  private JButton baseFlameToClipboardButton;
  private JButton baseFlameClearButton;
  private JButton baseFlameClearAllButton;
  private JPanel panel_8;
  private JToggleButton previewButton;
  private JScrollPane scrollPane;
  private JTextPane introductionTextPane;
  private JTabbedPane tabbedPane_1;
  private JPanel panel_9;
  private JPanel panel_10;
  private JPanel panel_11;
  private JScrollPane scrollPane_1;
  private JTextArea statisticsTextArea;
  private JLabel baseFlameMinValueLabel;
  private JLabel baseFlameMaxValueLabel;
  private JWFNumberField baseFlameMinValueField;
  private JWFNumberField baseFlameMaxValueField;
  private JWFNumberField baseFlameWeightField;
  private JWFNumberField baseFlameGridXOffsetField;
  private JWFNumberField baseFlameGridYOffsetField;
  private JWFNumberField baseFlameGridXSizeField;
  private JWFNumberField baseFlameGridYSizeField;
  private JPanel panel_12;
  private JComboBox selectedMutationCmb;
  private JWFNumberField paramMaxValueField;
  private JTree paramPropertyPathTree;
  private JWFNumberField paramMinValueField;
  private JPanel panel_13;
  private JWFNumberField motionTimeField;
  private JWFNumberField motionLifeTimeField;
  private JWFNumberField motionLifeTimeVariationField;
  private JWFNumberField motionForceXField;
  private JWFNumberField motionForceYField;
  private JWFNumberField motionForceZField;
  private JButton iflameToEditorButton;
  private JPanel panel_17;
  private JWFNumberField speedXField;
  private JWFNumberField speedYField;
  private JWFNumberField speedZField;
  private JWFNumberField speedAlphaField;
  private JWFNumberField speedBetaField;
  private JWFNumberField speedGammaField;
  private JWFNumberField speedXVarField;
  private JWFNumberField speedYVarField;
  private JWFNumberField speedZVarField;
  private JWFNumberField speedAlphaVarField;
  private JWFNumberField speedBetaVarField;
  private JWFNumberField speedGammaVarField;
  private JWFNumberField radialAccelField;
  private JWFNumberField radialAccelVarField;
  private JWFNumberField tangentialAccelField;
  private JWFNumberField tangentialAccelVarField;
  private JPanel panel_18;
  private JPanel panel_19;
  private JWFNumberField forceCentreXField;
  private JWFNumberField forceCentreYField;
  private JWFNumberField forceCentreZField;
  private JButton copyDynamicsParamsToOthersButton;
  private JButton baseFlameClearOthersButton;
  private JButton clearCacheButton;
  private JPanel panel_20;
  private JWFNumberField baseFlameBrightnessMinField;
  private JWFNumberField baseFlameBrightnessMaxField;
  private JWFNumberField imageRedChangeField;
  private JWFNumberField imageGreenChangeField;
  private JWFNumberField imageBlueChangeField;
  private JWFNumberField imageHueChangeField;
  private JWFNumberField imageSaturationChangeField;
  private JWFNumberField imageLightnessChangeField;
  private JWFNumberField baseFlameBrightnessChangeField;
  private JCheckBox baseFlameInstancingCBx;
  private JButton copyBaseFlameParamsToOthersButton;

  public IFlamesFrame() {
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
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("IFlames");
    this.setVisible(false);
    this.setResizable(true);
    this.setContentPane(getJContentPane());
  }

  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setBorder(new EmptyBorder(2, 0, 0, 0));
      jContentPane.setLayout(new BorderLayout());

      mainTopPanel = new JPanel();
      mainTopPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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

      newButton.setBounds(12, 10, 143, 48);
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
      loadIFlameFromClipboardButton.setBounds(223, 10, 143, 24);
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
      saveIFlameToClipboardButton.setBounds(366, 10, 143, 24);
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
      loadIFlameButton.setBounds(223, 34, 143, 24);
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
      saveIFlameButton.setBounds(366, 34, 143, 24);
      mainTopPanel.add(saveIFlameButton);

      resolutionProfileCmb = new JComboBox();
      resolutionProfileCmb.setPreferredSize(new Dimension(125, 24));
      resolutionProfileCmb.setMinimumSize(new Dimension(100, 24));
      resolutionProfileCmb.setMaximumSize(new Dimension(32767, 24));
      resolutionProfileCmb.setMaximumRowCount(32);
      resolutionProfileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      resolutionProfileCmb.setBounds(882, 34, 143, 24);
      resolutionProfileCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (iflamesController != null) {
            iflamesController.saveUndoPoint();
            iflamesController.resolutionProfileCmb_changed();
          }
        }
      });
      mainTopPanel.add(resolutionProfileCmb);

      refreshLibraryButton = new JButton();
      refreshLibraryButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.reloadLibraryButton_clicked();
        }
      });
      refreshLibraryButton.setText("Refresh Library");
      refreshLibraryButton.setPreferredSize(new Dimension(125, 52));
      refreshLibraryButton.setMnemonic(KeyEvent.VK_N);
      refreshLibraryButton.setMinimumSize(new Dimension(100, 52));
      refreshLibraryButton.setMaximumSize(new Dimension(32000, 52));
      refreshLibraryButton.setFont(new Font("Dialog", Font.BOLD, 10));
      refreshLibraryButton.setActionCommand("New from scratch");
      refreshLibraryButton.setBounds(1027, 10, 143, 48);
      mainTopPanel.add(refreshLibraryButton);

      iflameToEditorButton = new JButton();
      iflameToEditorButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.iflameToEditorButton_clicked();
        }
      });
      iflameToEditorButton.setText("To Flames Editor");
      iflameToEditorButton.setPreferredSize(new Dimension(125, 52));
      iflameToEditorButton.setMnemonic(KeyEvent.VK_N);
      iflameToEditorButton.setMinimumSize(new Dimension(100, 52));
      iflameToEditorButton.setMaximumSize(new Dimension(32000, 52));
      iflameToEditorButton.setFont(new Font("Dialog", Font.BOLD, 10));
      iflameToEditorButton.setActionCommand("New from scratch");
      iflameToEditorButton.setBounds(557, 10, 143, 48);
      iflameToEditorButton.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/brick2.png")));
      mainTopPanel.add(iflameToEditorButton);

      clearCacheButton = new JButton();
      clearCacheButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.clearCacheButton_clicked();
        }
      });
      clearCacheButton.setToolTipText("Remove all cached data to free memory");
      clearCacheButton.setText("Clear cache");
      clearCacheButton.setPreferredSize(new Dimension(125, 24));
      clearCacheButton.setMinimumSize(new Dimension(100, 24));
      clearCacheButton.setMaximumSize(new Dimension(32000, 24));
      clearCacheButton.setIconTextGap(2);
      clearCacheButton.setFont(new Font("Dialog", Font.BOLD, 10));
      clearCacheButton.setBounds(882, 10, 143, 24);
      mainTopPanel.add(clearCacheButton);

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
      imageStackRootPanel.setLayout(new BorderLayout(0, 0));

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
      mainBottomPanel.setPreferredSize(new Dimension(10, 272));
      jContentPane.add(mainBottomPanel, BorderLayout.SOUTH);
      mainBottomPanel.setLayout(new BorderLayout(0, 0));

      panel_2 = new JPanel();
      panel_2.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      panel_2.setPreferredSize(new Dimension(346, 10));
      mainBottomPanel.add(panel_2, BorderLayout.WEST);
      panel_2.setLayout(null);

      JLabel lblFlame = new JLabel();
      lblFlame.setBounds(18, 6, 82, 22);
      panel_2.add(lblFlame);
      lblFlame.setText("Base-Flame");
      lblFlame.setPreferredSize(new Dimension(94, 22));
      lblFlame.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameCmb = new JComboBox();
      baseFlameCmb.setBounds(98, 6, 125, 22);
      panel_2.add(baseFlameCmb);
      baseFlameCmb.setPreferredSize(new Dimension(125, 22));
      baseFlameCmb.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlamePreviewRootPnl = new JPanel();
      baseFlamePreviewRootPnl.setBounds(18, 33, 204, 137);
      panel_2.add(baseFlamePreviewRootPnl);
      baseFlamePreviewRootPnl.setPreferredSize(new Dimension(240, 130));
      baseFlamePreviewRootPnl.setMinimumSize(new Dimension(160, 100));
      baseFlamePreviewRootPnl.setMaximumSize(new Dimension(32767, 160));
      baseFlamePreviewRootPnl.setLayout(new BorderLayout(0, 0));

      baseFlameFromClipboardButton = new JButton();
      baseFlameFromClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.baseFlameFromClipboardButton_clicked();
        }
      });
      baseFlameFromClipboardButton.setToolTipText("Replace the currently selected base-flame with the content of the clipboard");
      baseFlameFromClipboardButton.setText("From Clipboard");
      baseFlameFromClipboardButton.setPreferredSize(new Dimension(125, 24));
      baseFlameFromClipboardButton.setMinimumSize(new Dimension(100, 24));
      baseFlameFromClipboardButton.setMaximumSize(new Dimension(32000, 24));
      baseFlameFromClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      baseFlameFromClipboardButton.setBounds(229, 26, 105, 24);
      panel_2.add(baseFlameFromClipboardButton);

      baseFlameToClipboardButton = new JButton();
      baseFlameToClipboardButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.baseFlameToClipboardButton_clicked();
        }
      });
      baseFlameToClipboardButton.setToolTipText("Copy the currently select base-flame to the clipboard");
      baseFlameToClipboardButton.setText("To Clipboard");
      baseFlameToClipboardButton.setPreferredSize(new Dimension(125, 24));
      baseFlameToClipboardButton.setMinimumSize(new Dimension(100, 24));
      baseFlameToClipboardButton.setMaximumSize(new Dimension(32000, 24));
      baseFlameToClipboardButton.setFont(new Font("Dialog", Font.BOLD, 10));
      baseFlameToClipboardButton.setBounds(229, 52, 105, 24);
      panel_2.add(baseFlameToClipboardButton);

      baseFlameClearButton = new JButton();
      baseFlameClearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.baseFlameClearButton_clicked();
        }
      });
      baseFlameClearButton.setToolTipText("Clear the currently selected base-flame");
      baseFlameClearButton.setText("Clear");
      baseFlameClearButton.setPreferredSize(new Dimension(125, 24));
      baseFlameClearButton.setMinimumSize(new Dimension(100, 24));
      baseFlameClearButton.setMaximumSize(new Dimension(32000, 24));
      baseFlameClearButton.setFont(new Font("Dialog", Font.BOLD, 10));
      baseFlameClearButton.setBounds(229, 100, 105, 24);
      panel_2.add(baseFlameClearButton);

      baseFlameClearAllButton = new JButton();
      baseFlameClearAllButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.baseFlameClearAllButton_clicked();
        }
      });
      baseFlameClearAllButton.setToolTipText("Clear all base-flames");
      baseFlameClearAllButton.setText("Clear all");
      baseFlameClearAllButton.setPreferredSize(new Dimension(125, 24));
      baseFlameClearAllButton.setMinimumSize(new Dimension(100, 24));
      baseFlameClearAllButton.setMaximumSize(new Dimension(32000, 24));
      baseFlameClearAllButton.setFont(new Font("Dialog", Font.BOLD, 10));
      baseFlameClearAllButton.setBounds(229, 126, 105, 24);
      panel_2.add(baseFlameClearAllButton);

      baseFlameClearOthersButton = new JButton();
      baseFlameClearOthersButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.baseFlameClearOthersButton_clicked();
        }
      });
      baseFlameClearOthersButton.setToolTipText("Clear all other flames except the current one");
      baseFlameClearOthersButton.setText("Clear others");
      baseFlameClearOthersButton.setPreferredSize(new Dimension(125, 24));
      baseFlameClearOthersButton.setMinimumSize(new Dimension(100, 24));
      baseFlameClearOthersButton.setMaximumSize(new Dimension(32000, 24));
      baseFlameClearOthersButton.setFont(new Font("Dialog", Font.BOLD, 10));
      baseFlameClearOthersButton.setBounds(229, 152, 105, 24);
      panel_2.add(baseFlameClearOthersButton);

      JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      mainBottomPanel.add(tabbedPane, BorderLayout.CENTER);

      JPanel panel_3 = new JPanel();
      tabbedPane.addTab("Edge Finding", null, panel_3, null);
      panel_3.setLayout(null);

      erodeSizeField = new JWFNumberField();
      erodeSizeField.setHasMinValue(true);
      erodeSizeField.setHasMaxValue(true);
      erodeSizeField.setMinValue(3.0);
      erodeSizeField.setMaxValue(9.0);
      erodeSizeField.setOnlyIntegers(true);
      erodeSizeField.setBounds(157, 108, 100, 24);
      panel_3.add(erodeSizeField);
      erodeSizeField.setValueStep(2.0);
      erodeSizeField.setText("");
      erodeSizeField.setPreferredSize(new Dimension(100, 24));
      erodeSizeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      erodeSizeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.erodeSizeField_changed();
          }
        }
      });

      JLabel lblSize = new JLabel();
      lblSize.setBounds(124, 108, 35, 22);
      panel_3.add(lblSize);
      lblSize.setToolTipText("");
      lblSize.setText("Size");
      lblSize.setPreferredSize(new Dimension(94, 22));
      lblSize.setFont(new Font("Dialog", Font.BOLD, 10));

      edgesNorthButton = new JToggleButton();
      edgesNorthButton.setToolTipText("");
      edgesNorthButton.setText("Edges North");
      edgesNorthButton.setPreferredSize(new Dimension(136, 24));
      edgesNorthButton.setFont(new Font("Dialog", Font.BOLD, 10));
      edgesNorthButton.setBounds(80, 6, 105, 24);
      edgesNorthButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.edgesNorthButton_clicked();
        }
      });

      panel_3.add(edgesNorthButton);

      edgesWestButton = new JToggleButton();
      edgesWestButton.setToolTipText("");
      edgesWestButton.setText("Edges West");
      edgesWestButton.setPreferredSize(new Dimension(136, 24));
      edgesWestButton.setFont(new Font("Dialog", Font.BOLD, 10));
      edgesWestButton.setBounds(6, 32, 105, 24);
      edgesWestButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.edgesWestButton_clicked();
        }
      });
      panel_3.add(edgesWestButton);

      edgesEastButton = new JToggleButton();
      edgesEastButton.setToolTipText("");
      edgesEastButton.setText("Edges East");
      edgesEastButton.setPreferredSize(new Dimension(136, 24));
      edgesEastButton.setFont(new Font("Dialog", Font.BOLD, 10));
      edgesEastButton.setBounds(152, 32, 105, 24);
      edgesEastButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.edgesEastButton_clicked();
        }
      });
      panel_3.add(edgesEastButton);

      edgesSouthButton = new JToggleButton();
      edgesSouthButton.setToolTipText("");
      edgesSouthButton.setText("Edges South");
      edgesSouthButton.setPreferredSize(new Dimension(136, 24));
      edgesSouthButton.setFont(new Font("Dialog", Font.BOLD, 10));
      edgesSouthButton.setBounds(80, 58, 105, 24);
      edgesSouthButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.edgesSouthButton_clicked();
        }
      });
      panel_3.add(edgesSouthButton);

      erodeButton = new JToggleButton();
      erodeButton.setToolTipText("");
      erodeButton.setText("Erode");
      erodeButton.setPreferredSize(new Dimension(136, 24));
      erodeButton.setFont(new Font("Dialog", Font.BOLD, 10));
      erodeButton.setBounds(7, 108, 105, 24);
      erodeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.erodeButton_clicked();
        }
      });
      panel_3.add(erodeButton);

      displayPreprocessedImageButton = new JToggleButton();
      displayPreprocessedImageButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.displayPreprocessedImageButton_clicked();
        }
      });
      displayPreprocessedImageButton.setToolTipText("Display the pre-processed image");
      displayPreprocessedImageButton.setText("Display Preprocessed Image");
      displayPreprocessedImageButton.setPreferredSize(new Dimension(136, 24));
      displayPreprocessedImageButton.setFont(new Font("Dialog", Font.BOLD, 10));
      displayPreprocessedImageButton.setBounds(372, 32, 227, 24);
      panel_3.add(displayPreprocessedImageButton);

      maxImageWidthField = new JWFNumberField();
      maxImageWidthField.setHasMinValue(true);
      maxImageWidthField.setHasMaxValue(true);
      maxImageWidthField.setValueStep(50.0);
      maxImageWidthField.setText("");
      maxImageWidthField.setPreferredSize(new Dimension(100, 24));
      maxImageWidthField.setOnlyIntegers(true);
      maxImageWidthField.setMinValue(32.0);
      maxImageWidthField.setMaxValue(4096.0);
      maxImageWidthField.setFont(new Font("Dialog", Font.PLAIN, 10));
      maxImageWidthField.setBounds(499, 6, 100, 24);
      maxImageWidthField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.maxImageWidthField_changed();
          }
        }
      });
      panel_3.add(maxImageWidthField);

      JLabel lblMaximalImageSize = new JLabel();
      lblMaximalImageSize.setToolTipText("Reduce the image-width for faster preprocessing, ");
      lblMaximalImageSize.setText("Maximum Image Width");
      lblMaximalImageSize.setPreferredSize(new Dimension(94, 22));
      lblMaximalImageSize.setFont(new Font("Dialog", Font.BOLD, 10));
      lblMaximalImageSize.setBounds(372, 6, 125, 22);
      panel_3.add(lblMaximalImageSize);

      panel_5 = new JPanel();
      tabbedPane.addTab("Global Structure", null, panel_5, null);
      panel_5.setLayout(null);

      globalScaleXField = new JWFNumberField();
      globalScaleXField.setValueStep(0.05);
      globalScaleXField.setText("");
      globalScaleXField.setPreferredSize(new Dimension(100, 24));
      globalScaleXField.setFont(new Font("Dialog", Font.PLAIN, 10));
      globalScaleXField.setBounds(330, 6, 100, 24);
      globalScaleXField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.globalScaleXField_changed();
          }
        }
      });
      panel_5.add(globalScaleXField);

      JLabel lblScalex = new JLabel();
      lblScalex.setToolTipText("");
      lblScalex.setText("ScaleX");
      lblScalex.setPreferredSize(new Dimension(94, 22));
      lblScalex.setFont(new Font("Dialog", Font.BOLD, 10));
      lblScalex.setBounds(235, 8, 93, 22);
      panel_5.add(lblScalex);

      JLabel lblScaley = new JLabel();
      lblScaley.setToolTipText("");
      lblScaley.setText("ScaleY");
      lblScaley.setPreferredSize(new Dimension(94, 22));
      lblScaley.setFont(new Font("Dialog", Font.BOLD, 10));
      lblScaley.setBounds(235, 32, 93, 22);
      panel_5.add(lblScaley);

      globalScaleYField = new JWFNumberField();
      globalScaleYField.setValueStep(0.05);
      globalScaleYField.setText("");
      globalScaleYField.setPreferredSize(new Dimension(100, 24));
      globalScaleYField.setFont(new Font("Dialog", Font.PLAIN, 10));
      globalScaleYField.setBounds(330, 30, 100, 24);
      globalScaleYField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.globalScaleYField_changed();
          }
        }
      });
      panel_5.add(globalScaleYField);

      JLabel lblScalez = new JLabel();
      lblScalez.setToolTipText("");
      lblScalez.setText("ScaleZ");
      lblScalez.setPreferredSize(new Dimension(94, 22));
      lblScalez.setFont(new Font("Dialog", Font.BOLD, 10));
      lblScalez.setBounds(235, 56, 93, 22);
      panel_5.add(lblScalez);

      globalScaleZField = new JWFNumberField();
      globalScaleZField.setValueStep(0.05);
      globalScaleZField.setText("");
      globalScaleZField.setPreferredSize(new Dimension(100, 24));
      globalScaleZField.setFont(new Font("Dialog", Font.PLAIN, 10));
      globalScaleZField.setBounds(330, 54, 100, 24);
      globalScaleZField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.globalScaleZField_changed();
          }
        }
      });
      panel_5.add(globalScaleZField);

      globalOffsetXField = new JWFNumberField();
      globalOffsetXField.setValueStep(0.05);
      globalOffsetXField.setText("");
      globalOffsetXField.setPreferredSize(new Dimension(100, 24));
      globalOffsetXField.setFont(new Font("Dialog", Font.PLAIN, 10));
      globalOffsetXField.setBounds(566, 6, 100, 24);
      globalOffsetXField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.globalOffsetXField_changed();
          }
        }
      });
      panel_5.add(globalOffsetXField);

      JLabel lblOffsetx = new JLabel();
      lblOffsetx.setToolTipText("");
      lblOffsetx.setText("OffsetX");
      lblOffsetx.setPreferredSize(new Dimension(94, 22));
      lblOffsetx.setFont(new Font("Dialog", Font.BOLD, 10));
      lblOffsetx.setBounds(471, 8, 93, 22);
      panel_5.add(lblOffsetx);

      JLabel lblOffsety = new JLabel();
      lblOffsety.setToolTipText("");
      lblOffsety.setText("OffsetY");
      lblOffsety.setPreferredSize(new Dimension(94, 22));
      lblOffsety.setFont(new Font("Dialog", Font.BOLD, 10));
      lblOffsety.setBounds(471, 32, 93, 22);
      panel_5.add(lblOffsety);

      globalOffsetYField = new JWFNumberField();
      globalOffsetYField.setValueStep(0.05);
      globalOffsetYField.setText("");
      globalOffsetYField.setPreferredSize(new Dimension(100, 24));
      globalOffsetYField.setFont(new Font("Dialog", Font.PLAIN, 10));
      globalOffsetYField.setBounds(566, 30, 100, 24);
      globalOffsetYField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.globalOffsetYField_changed();
          }
        }
      });
      panel_5.add(globalOffsetYField);

      JLabel lblOffsetz = new JLabel();
      lblOffsetz.setToolTipText("");
      lblOffsetz.setText("OffsetZ");
      lblOffsetz.setPreferredSize(new Dimension(94, 22));
      lblOffsetz.setFont(new Font("Dialog", Font.BOLD, 10));
      lblOffsetz.setBounds(471, 56, 93, 22);
      panel_5.add(lblOffsetz);

      globalOffsetZField = new JWFNumberField();
      globalOffsetZField.setValueStep(0.05);
      globalOffsetZField.setText("");
      globalOffsetZField.setPreferredSize(new Dimension(100, 24));
      globalOffsetZField.setFont(new Font("Dialog", Font.PLAIN, 10));
      globalOffsetZField.setBounds(566, 54, 100, 24);
      globalOffsetZField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.globalOffsetZField_changed();
          }
        }
      });
      panel_5.add(globalOffsetZField);

      structureThresholdField = new JWFNumberField();
      structureThresholdField.setMaxValue(1.0);
      structureThresholdField.setHasMinValue(true);
      structureThresholdField.setHasMaxValue(true);
      structureThresholdField.setValueStep(0.05);
      structureThresholdField.setText("");
      structureThresholdField.setPreferredSize(new Dimension(100, 24));
      structureThresholdField.setFont(new Font("Dialog", Font.PLAIN, 10));
      structureThresholdField.setBounds(101, 6, 100, 24);
      structureThresholdField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.structureThresholdField_changed();
          }
        }
      });
      panel_5.add(structureThresholdField);

      JLabel lblThreshold = new JLabel();
      lblThreshold.setToolTipText("");
      lblThreshold.setText("Threshold");
      lblThreshold.setPreferredSize(new Dimension(94, 22));
      lblThreshold.setFont(new Font("Dialog", Font.BOLD, 10));
      lblThreshold.setBounds(6, 8, 93, 22);
      panel_5.add(lblThreshold);

      JLabel lblDensity = new JLabel();
      lblDensity.setToolTipText("");
      lblDensity.setText("Density");
      lblDensity.setPreferredSize(new Dimension(94, 22));
      lblDensity.setFont(new Font("Dialog", Font.BOLD, 10));
      lblDensity.setBounds(6, 34, 93, 22);
      panel_5.add(lblDensity);

      structureDensityField = new JWFNumberField();
      structureDensityField.setHasMinValue(true);
      structureDensityField.setHasMaxValue(true);
      structureDensityField.setMaxValue(1.0);
      structureDensityField.setValueStep(0.05);
      structureDensityField.setText("");
      structureDensityField.setPreferredSize(new Dimension(100, 24));
      structureDensityField.setFont(new Font("Dialog", Font.PLAIN, 10));
      structureDensityField.setBounds(101, 32, 100, 24);
      structureDensityField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.structureDensityField_changed();
          }
        }
      });
      panel_5.add(structureDensityField);

      shapeDistributionCmb = new JComboBox();
      shapeDistributionCmb.setPreferredSize(new Dimension(125, 22));
      shapeDistributionCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      shapeDistributionCmb.setBounds(101, 56, 100, 22);
      shapeDistributionCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (iflamesController != null) {
            iflamesController.shapeDistributionCmb_changed();
          }
        }
      });
      panel_5.add(shapeDistributionCmb);

      JLabel lblShapeDistribution = new JLabel();
      lblShapeDistribution.setToolTipText("");
      lblShapeDistribution.setText("Shape distribution");
      lblShapeDistribution.setPreferredSize(new Dimension(94, 22));
      lblShapeDistribution.setFont(new Font("Dialog", Font.BOLD, 10));
      lblShapeDistribution.setBounds(6, 58, 93, 22);
      panel_5.add(lblShapeDistribution);

      panel_4 = new JPanel();
      tabbedPane.addTab("Blending", null, panel_4, null);
      panel_4.setLayout(null);

      iflameBrightnessField = new JWFNumberField();
      iflameBrightnessField.setHasMinValue(true);
      iflameBrightnessField.setValueStep(0.05);
      iflameBrightnessField.setText("");
      iflameBrightnessField.setPreferredSize(new Dimension(100, 24));
      iflameBrightnessField.setFont(new Font("Dialog", Font.PLAIN, 10));
      iflameBrightnessField.setBounds(101, 6, 100, 24);
      iflameBrightnessField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.iflameBrightnessField_changed();
          }
        }
      });
      panel_4.add(iflameBrightnessField);

      JLabel lblIflameBrightness = new JLabel();
      lblIflameBrightness.setToolTipText("");
      lblIflameBrightness.setText("IFlame brightness");
      lblIflameBrightness.setPreferredSize(new Dimension(94, 22));
      lblIflameBrightness.setFont(new Font("Dialog", Font.BOLD, 10));
      lblIflameBrightness.setBounds(6, 8, 93, 22);
      panel_4.add(lblIflameBrightness);

      JLabel lblImageBrightness = new JLabel();
      lblImageBrightness.setToolTipText("");
      lblImageBrightness.setText("Image brightness");
      lblImageBrightness.setPreferredSize(new Dimension(94, 22));
      lblImageBrightness.setFont(new Font("Dialog", Font.BOLD, 10));
      lblImageBrightness.setBounds(6, 32, 93, 22);
      panel_4.add(lblImageBrightness);

      imageBrightnessField = new JWFNumberField();
      imageBrightnessField.setHasMinValue(true);
      imageBrightnessField.setValueStep(0.05);
      imageBrightnessField.setText("");
      imageBrightnessField.setPreferredSize(new Dimension(100, 24));
      imageBrightnessField.setFont(new Font("Dialog", Font.PLAIN, 10));
      imageBrightnessField.setBounds(101, 30, 100, 24);
      imageBrightnessField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.imageBrightnessField_changed();
          }
        }
      });
      panel_4.add(imageBrightnessField);

      JLabel lblIflameDensity = new JLabel();
      lblIflameDensity.setToolTipText("");
      lblIflameDensity.setText("IFlame density");
      lblIflameDensity.setPreferredSize(new Dimension(94, 22));
      lblIflameDensity.setFont(new Font("Dialog", Font.BOLD, 10));
      lblIflameDensity.setBounds(6, 56, 93, 22);
      panel_4.add(lblIflameDensity);

      iflameDensityField = new JWFNumberField();
      iflameDensityField.setMaxValue(1.0);
      iflameDensityField.setHasMinValue(true);
      iflameDensityField.setHasMaxValue(true);
      iflameDensityField.setValueStep(0.05);
      iflameDensityField.setText("");
      iflameDensityField.setPreferredSize(new Dimension(100, 24));
      iflameDensityField.setFont(new Font("Dialog", Font.PLAIN, 10));
      iflameDensityField.setBounds(101, 54, 100, 24);
      iflameDensityField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.iflameDensityField_changed();
          }
        }
      });
      panel_4.add(iflameDensityField);

      imageRedChangeField = new JWFNumberField();
      imageRedChangeField.setMinValue(-1.0);
      imageRedChangeField.setHasMaxValue(true);
      imageRedChangeField.setValueStep(0.05);
      imageRedChangeField.setText("");
      imageRedChangeField.setPreferredSize(new Dimension(100, 24));
      imageRedChangeField.setMaxValue(1.0);
      imageRedChangeField.setHasMinValue(true);
      imageRedChangeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      imageRedChangeField.setBounds(360, 5, 100, 24);
      imageRedChangeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.imageRedChangeField_changed();
          }
        }
      });
      panel_4.add(imageRedChangeField);

      JLabel lblRedChange = new JLabel();
      lblRedChange.setToolTipText("");
      lblRedChange.setText("Red change");
      lblRedChange.setPreferredSize(new Dimension(94, 22));
      lblRedChange.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRedChange.setBounds(228, 7, 130, 22);
      panel_4.add(lblRedChange);

      JLabel lblGreenChange = new JLabel();
      lblGreenChange.setToolTipText("");
      lblGreenChange.setText("Green change");
      lblGreenChange.setPreferredSize(new Dimension(94, 22));
      lblGreenChange.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGreenChange.setBounds(228, 31, 130, 22);
      panel_4.add(lblGreenChange);

      imageGreenChangeField = new JWFNumberField();
      imageGreenChangeField.setMinValue(-1.0);
      imageGreenChangeField.setHasMaxValue(true);
      imageGreenChangeField.setValueStep(0.05);
      imageGreenChangeField.setText("");
      imageGreenChangeField.setPreferredSize(new Dimension(100, 24));
      imageGreenChangeField.setMaxValue(1.0);
      imageGreenChangeField.setHasMinValue(true);
      imageGreenChangeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      imageGreenChangeField.setBounds(360, 29, 100, 24);
      imageGreenChangeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.imageGreenChangeField_changed();
          }
        }
      });
      panel_4.add(imageGreenChangeField);

      JLabel lblBlueChange = new JLabel();
      lblBlueChange.setToolTipText("");
      lblBlueChange.setText("Blue change");
      lblBlueChange.setPreferredSize(new Dimension(94, 22));
      lblBlueChange.setFont(new Font("Dialog", Font.BOLD, 10));
      lblBlueChange.setBounds(228, 56, 130, 22);
      panel_4.add(lblBlueChange);

      imageBlueChangeField = new JWFNumberField();
      imageBlueChangeField.setMinValue(-1.0);
      imageBlueChangeField.setValueStep(0.05);
      imageBlueChangeField.setText("");
      imageBlueChangeField.setPreferredSize(new Dimension(100, 24));
      imageBlueChangeField.setMaxValue(1.0);
      imageBlueChangeField.setHasMinValue(true);
      imageBlueChangeField.setHasMaxValue(true);
      imageBlueChangeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      imageBlueChangeField.setBounds(360, 54, 100, 24);
      imageBlueChangeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.imageBlueChangeField_changed();
          }
        }
      });
      panel_4.add(imageBlueChangeField);

      JLabel lblHslChange = new JLabel();
      lblHslChange.setToolTipText("");
      lblHslChange.setText("Hue change");
      lblHslChange.setPreferredSize(new Dimension(94, 22));
      lblHslChange.setFont(new Font("Dialog", Font.BOLD, 10));
      lblHslChange.setBounds(228, 88, 130, 22);
      panel_4.add(lblHslChange);

      imageHueChangeField = new JWFNumberField();
      imageHueChangeField.setValueStep(0.05);
      imageHueChangeField.setText("");
      imageHueChangeField.setPreferredSize(new Dimension(100, 24));
      imageHueChangeField.setMinValue(-1.0);
      imageHueChangeField.setMaxValue(1.0);
      imageHueChangeField.setHasMinValue(true);
      imageHueChangeField.setHasMaxValue(true);
      imageHueChangeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      imageHueChangeField.setBounds(360, 86, 100, 24);
      imageHueChangeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.imageHueChangeField_changed();
          }
        }
      });
      panel_4.add(imageHueChangeField);

      imageSaturationChangeField = new JWFNumberField();
      imageSaturationChangeField.setValueStep(0.05);
      imageSaturationChangeField.setText("");
      imageSaturationChangeField.setPreferredSize(new Dimension(100, 24));
      imageSaturationChangeField.setMinValue(-1.0);
      imageSaturationChangeField.setMaxValue(1.0);
      imageSaturationChangeField.setHasMinValue(true);
      imageSaturationChangeField.setHasMaxValue(true);
      imageSaturationChangeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      imageSaturationChangeField.setBounds(360, 110, 100, 24);
      imageSaturationChangeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.imageSaturationChangeField_changed();
          }
        }
      });
      panel_4.add(imageSaturationChangeField);

      JLabel lblSaturationChange = new JLabel();
      lblSaturationChange.setToolTipText("");
      lblSaturationChange.setText("Saturation change");
      lblSaturationChange.setPreferredSize(new Dimension(94, 22));
      lblSaturationChange.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSaturationChange.setBounds(228, 112, 130, 22);
      panel_4.add(lblSaturationChange);

      JLabel lblLightnessChange = new JLabel();
      lblLightnessChange.setToolTipText("");
      lblLightnessChange.setText("Lightness change");
      lblLightnessChange.setPreferredSize(new Dimension(94, 22));
      lblLightnessChange.setFont(new Font("Dialog", Font.BOLD, 10));
      lblLightnessChange.setBounds(228, 137, 130, 22);
      panel_4.add(lblLightnessChange);

      imageLightnessChangeField = new JWFNumberField();
      imageLightnessChangeField.setValueStep(0.05);
      imageLightnessChangeField.setText("");
      imageLightnessChangeField.setPreferredSize(new Dimension(100, 24));
      imageLightnessChangeField.setMinValue(-1.0);
      imageLightnessChangeField.setMaxValue(1.0);
      imageLightnessChangeField.setHasMinValue(true);
      imageLightnessChangeField.setHasMaxValue(true);
      imageLightnessChangeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      imageLightnessChangeField.setBounds(360, 135, 100, 24);
      imageLightnessChangeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.imageLightnessChangeField_changed();
          }
        }
      });
      panel_4.add(imageLightnessChangeField);

      panel_7 = new JPanel();
      tabbedPane.addTab("Base Flame Appearance", null, panel_7, null);
      panel_7.setLayout(new BorderLayout(0, 0));

      tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
      panel_7.add(tabbedPane_1);

      panel_9 = new JPanel();
      tabbedPane_1.addTab("Size and Orientation", null, panel_9, null);
      panel_9.setLayout(null);

      baseFlameSizeField = new JWFNumberField();
      baseFlameSizeField.setBounds(101, 6, 100, 24);
      panel_9.add(baseFlameSizeField);
      baseFlameSizeField.setValueStep(0.05);
      baseFlameSizeField.setText("");
      baseFlameSizeField.setPreferredSize(new Dimension(100, 24));
      baseFlameSizeField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblSize_1 = new JLabel();
      lblSize_1.setBounds(6, 8, 93, 22);
      panel_9.add(lblSize_1);
      lblSize_1.setToolTipText("");
      lblSize_1.setText("Size");
      lblSize_1.setPreferredSize(new Dimension(94, 22));
      lblSize_1.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel lblSizeVariaton = new JLabel();
      lblSizeVariaton.setBounds(6, 32, 93, 22);
      panel_9.add(lblSizeVariaton);
      lblSizeVariaton.setToolTipText("");
      lblSizeVariaton.setText("Size Variaton");
      lblSizeVariaton.setPreferredSize(new Dimension(94, 22));
      lblSizeVariaton.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameSizeVariationField = new JWFNumberField();
      baseFlameSizeVariationField.setHasMinValue(true);
      baseFlameSizeVariationField.setBounds(101, 30, 100, 24);
      panel_9.add(baseFlameSizeVariationField);
      baseFlameSizeVariationField.setValueStep(0.05);
      baseFlameSizeVariationField.setText("");
      baseFlameSizeVariationField.setPreferredSize(new Dimension(100, 24));
      baseFlameSizeVariationField.setFont(new Font("Dialog", Font.PLAIN, 10));

      baseFlameCentreXField = new JWFNumberField();
      baseFlameCentreXField.setBounds(101, 79, 100, 24);
      panel_9.add(baseFlameCentreXField);
      baseFlameCentreXField.setValueStep(0.05);
      baseFlameCentreXField.setText("");
      baseFlameCentreXField.setPreferredSize(new Dimension(100, 24));
      baseFlameCentreXField.setMaxValue(1.0);
      baseFlameCentreXField.setHasMinValue(true);
      baseFlameCentreXField.setHasMaxValue(true);
      baseFlameCentreXField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblCentrex = new JLabel();
      lblCentrex.setBounds(6, 81, 93, 22);
      panel_9.add(lblCentrex);
      lblCentrex.setToolTipText("");
      lblCentrex.setText("CentreX");
      lblCentrex.setPreferredSize(new Dimension(94, 22));
      lblCentrex.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel lblCentrey = new JLabel();
      lblCentrey.setBounds(6, 105, 93, 22);
      panel_9.add(lblCentrey);
      lblCentrey.setToolTipText("");
      lblCentrey.setText("CentreY");
      lblCentrey.setPreferredSize(new Dimension(94, 22));
      lblCentrey.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameCentreYField = new JWFNumberField();
      baseFlameCentreYField.setBounds(101, 103, 100, 24);
      panel_9.add(baseFlameCentreYField);
      baseFlameCentreYField.setValueStep(0.05);
      baseFlameCentreYField.setText("");
      baseFlameCentreYField.setPreferredSize(new Dimension(100, 24));
      baseFlameCentreYField.setMaxValue(1.0);
      baseFlameCentreYField.setHasMinValue(true);
      baseFlameCentreYField.setHasMaxValue(true);
      baseFlameCentreYField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblCentrez = new JLabel();
      lblCentrez.setBounds(6, 128, 93, 22);
      panel_9.add(lblCentrez);
      lblCentrez.setToolTipText("");
      lblCentrez.setText("CentreZ");
      lblCentrez.setPreferredSize(new Dimension(94, 22));
      lblCentrez.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameCentreZField = new JWFNumberField();
      baseFlameCentreZField.setBounds(101, 126, 100, 24);
      panel_9.add(baseFlameCentreZField);
      baseFlameCentreZField.setValueStep(0.05);
      baseFlameCentreZField.setText("");
      baseFlameCentreZField.setPreferredSize(new Dimension(100, 24));
      baseFlameCentreZField.setMaxValue(1.0);
      baseFlameCentreZField.setHasMinValue(true);
      baseFlameCentreZField.setHasMaxValue(true);
      baseFlameCentreZField.setFont(new Font("Dialog", Font.PLAIN, 10));

      baseFlameRotateAlphaField = new JWFNumberField();
      baseFlameRotateAlphaField.setBounds(345, 6, 100, 24);
      panel_9.add(baseFlameRotateAlphaField);
      baseFlameRotateAlphaField.setValueStep(0.05);
      baseFlameRotateAlphaField.setText("");
      baseFlameRotateAlphaField.setPreferredSize(new Dimension(100, 24));
      baseFlameRotateAlphaField.setMaxValue(1.0);
      baseFlameRotateAlphaField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblRotate = new JLabel();
      lblRotate.setBounds(213, 8, 130, 22);
      panel_9.add(lblRotate);
      lblRotate.setToolTipText("");
      lblRotate.setText("Rotate Alpha");
      lblRotate.setPreferredSize(new Dimension(94, 22));
      lblRotate.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel lblRotateVariation = new JLabel();
      lblRotateVariation.setBounds(213, 32, 130, 22);
      panel_9.add(lblRotateVariation);
      lblRotateVariation.setToolTipText("");
      lblRotateVariation.setText("Rotate Alpha Variation");
      lblRotateVariation.setPreferredSize(new Dimension(94, 22));
      lblRotateVariation.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameRotateAlphaVariationField = new JWFNumberField();
      baseFlameRotateAlphaVariationField.setHasMinValue(true);
      baseFlameRotateAlphaVariationField.setBounds(345, 30, 100, 24);
      panel_9.add(baseFlameRotateAlphaVariationField);
      baseFlameRotateAlphaVariationField.setValueStep(0.05);
      baseFlameRotateAlphaVariationField.setText("");
      baseFlameRotateAlphaVariationField.setPreferredSize(new Dimension(100, 24));
      baseFlameRotateAlphaVariationField.setMaxValue(1.0);
      baseFlameRotateAlphaVariationField.setFont(new Font("Dialog", Font.PLAIN, 10));

      baseFlameRotateBetaField = new JWFNumberField();
      baseFlameRotateBetaField.setBounds(345, 55, 100, 24);
      panel_9.add(baseFlameRotateBetaField);
      baseFlameRotateBetaField.setValueStep(0.05);
      baseFlameRotateBetaField.setText("");
      baseFlameRotateBetaField.setPreferredSize(new Dimension(100, 24));
      baseFlameRotateBetaField.setMaxValue(1.0);
      baseFlameRotateBetaField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblRotateBeta = new JLabel();
      lblRotateBeta.setBounds(213, 57, 130, 22);
      panel_9.add(lblRotateBeta);
      lblRotateBeta.setToolTipText("");
      lblRotateBeta.setText("Rotate Beta");
      lblRotateBeta.setPreferredSize(new Dimension(94, 22));
      lblRotateBeta.setFont(new Font("Dialog", Font.BOLD, 10));

      JLabel lblRotateBetaVariation = new JLabel();
      lblRotateBetaVariation.setBounds(213, 81, 130, 22);
      panel_9.add(lblRotateBetaVariation);
      lblRotateBetaVariation.setToolTipText("");
      lblRotateBetaVariation.setText("Rotate Beta Variation");
      lblRotateBetaVariation.setPreferredSize(new Dimension(94, 22));
      lblRotateBetaVariation.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameRotateBetaVariationField = new JWFNumberField();
      baseFlameRotateBetaVariationField.setHasMinValue(true);
      baseFlameRotateBetaVariationField.setBounds(345, 79, 100, 24);
      panel_9.add(baseFlameRotateBetaVariationField);
      baseFlameRotateBetaVariationField.setValueStep(0.05);
      baseFlameRotateBetaVariationField.setText("");
      baseFlameRotateBetaVariationField.setPreferredSize(new Dimension(100, 24));
      baseFlameRotateBetaVariationField.setMaxValue(1.0);
      baseFlameRotateBetaVariationField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblRotateGamma = new JLabel();
      lblRotateGamma.setBounds(213, 106, 130, 22);
      panel_9.add(lblRotateGamma);
      lblRotateGamma.setToolTipText("");
      lblRotateGamma.setText("Rotate Gamma");
      lblRotateGamma.setPreferredSize(new Dimension(94, 22));
      lblRotateGamma.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameRotateGammaField = new JWFNumberField();
      baseFlameRotateGammaField.setBounds(345, 104, 100, 24);
      panel_9.add(baseFlameRotateGammaField);
      baseFlameRotateGammaField.setValueStep(0.05);
      baseFlameRotateGammaField.setText("");
      baseFlameRotateGammaField.setPreferredSize(new Dimension(100, 24));
      baseFlameRotateGammaField.setMaxValue(1.0);
      baseFlameRotateGammaField.setFont(new Font("Dialog", Font.PLAIN, 10));

      baseFlameRotateGammaVariationField = new JWFNumberField();
      baseFlameRotateGammaVariationField.setHasMinValue(true);
      baseFlameRotateGammaVariationField.setBounds(345, 128, 100, 24);
      panel_9.add(baseFlameRotateGammaVariationField);
      baseFlameRotateGammaVariationField.setValueStep(0.05);
      baseFlameRotateGammaVariationField.setText("");
      baseFlameRotateGammaVariationField.setPreferredSize(new Dimension(100, 24));
      baseFlameRotateGammaVariationField.setMaxValue(1.0);
      baseFlameRotateGammaVariationField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblRotateGammaVariation = new JLabel();
      lblRotateGammaVariation.setBounds(213, 130, 130, 22);
      panel_9.add(lblRotateGammaVariation);
      lblRotateGammaVariation.setToolTipText("");
      lblRotateGammaVariation.setText("Rotate Gamma Variation");
      lblRotateGammaVariation.setPreferredSize(new Dimension(94, 22));
      lblRotateGammaVariation.setFont(new Font("Dialog", Font.BOLD, 10));

      baseFlameInstancingCBx = new JCheckBox("Instancing");
      baseFlameInstancingCBx.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameInstancingCbx_changed();
          }
        }
      });
      baseFlameInstancingCBx.setBounds(464, 6, 104, 18);
      panel_9.add(baseFlameInstancingCBx);

      copyBaseFlameParamsToOthersButton = new JButton();
      copyBaseFlameParamsToOthersButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.copyBaseFlameParamsToOthersButton_clicked();
        }
      });
      copyBaseFlameParamsToOthersButton.setToolTipText("Copy the parameters of this base-flame to all other flames");
      copyBaseFlameParamsToOthersButton.setText("Copy params to others");
      copyBaseFlameParamsToOthersButton.setPreferredSize(new Dimension(125, 24));
      copyBaseFlameParamsToOthersButton.setMinimumSize(new Dimension(100, 24));
      copyBaseFlameParamsToOthersButton.setMaximumSize(new Dimension(32000, 24));
      copyBaseFlameParamsToOthersButton.setFont(new Font("Dialog", Font.BOLD, 10));
      copyBaseFlameParamsToOthersButton.setBounds(464, 128, 192, 24);
      panel_9.add(copyBaseFlameParamsToOthersButton);

      panel_10 = new JPanel();
      tabbedPane_1.addTab("Distribution", null, panel_10, null);
      panel_10.setLayout(null);

      baseFlameMinValueField = new JWFNumberField();
      baseFlameMinValueField.setMaxValue(1.0);
      baseFlameMinValueField.setHasMinValue(true);
      baseFlameMinValueField.setHasMaxValue(true);
      baseFlameMinValueField.setValueStep(0.05);
      baseFlameMinValueField.setText("");
      baseFlameMinValueField.setPreferredSize(new Dimension(100, 24));
      baseFlameMinValueField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameMinValueField.setBounds(101, 6, 100, 24);
      baseFlameMinValueField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameMinValueField_changed();
          }
        }
      });

      panel_10.add(baseFlameMinValueField);

      baseFlameMinValueLabel = new JLabel();
      baseFlameMinValueLabel.setToolTipText("");
      baseFlameMinValueLabel.setText("Min Value");
      baseFlameMinValueLabel.setPreferredSize(new Dimension(94, 22));
      baseFlameMinValueLabel.setFont(new Font("Dialog", Font.BOLD, 10));
      baseFlameMinValueLabel.setBounds(6, 8, 93, 22);
      panel_10.add(baseFlameMinValueLabel);

      baseFlameMaxValueLabel = new JLabel();
      baseFlameMaxValueLabel.setToolTipText("");
      baseFlameMaxValueLabel.setText("Max Value");
      baseFlameMaxValueLabel.setPreferredSize(new Dimension(94, 22));
      baseFlameMaxValueLabel.setFont(new Font("Dialog", Font.BOLD, 10));
      baseFlameMaxValueLabel.setBounds(6, 32, 93, 22);
      panel_10.add(baseFlameMaxValueLabel);

      baseFlameMaxValueField = new JWFNumberField();
      baseFlameMaxValueField.setHasMinValue(true);
      baseFlameMaxValueField.setHasMaxValue(true);
      baseFlameMaxValueField.setMaxValue(1.0);
      baseFlameMaxValueField.setValueStep(0.05);
      baseFlameMaxValueField.setText("");
      baseFlameMaxValueField.setPreferredSize(new Dimension(100, 24));
      baseFlameMaxValueField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameMaxValueField.setBounds(101, 30, 100, 24);
      baseFlameMaxValueField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameMaxValueField_changed();
          }
        }
      });
      panel_10.add(baseFlameMaxValueField);

      baseFlameWeightField = new JWFNumberField();
      baseFlameWeightField.setHasMinValue(true);
      baseFlameWeightField.setValueStep(0.05);
      baseFlameWeightField.setText("");
      baseFlameWeightField.setPreferredSize(new Dimension(100, 24));
      baseFlameWeightField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameWeightField.setBounds(101, 66, 100, 24);
      baseFlameWeightField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameWeightField_changed();
          }
        }
      });
      panel_10.add(baseFlameWeightField);

      JLabel lblWeight = new JLabel();
      lblWeight.setToolTipText("");
      lblWeight.setText("Weight");
      lblWeight.setPreferredSize(new Dimension(94, 22));
      lblWeight.setFont(new Font("Dialog", Font.BOLD, 10));
      lblWeight.setBounds(6, 68, 93, 22);
      panel_10.add(lblWeight);

      baseFlameGridXOffsetField = new JWFNumberField();
      baseFlameGridXOffsetField.setOnlyIntegers(true);
      baseFlameGridXOffsetField.setValueStep(1.0);
      baseFlameGridXOffsetField.setText("");
      baseFlameGridXOffsetField.setPreferredSize(new Dimension(100, 24));
      baseFlameGridXOffsetField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameGridXOffsetField.setBounds(327, 6, 100, 24);
      baseFlameGridXOffsetField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameGridXOffsetField_changed();
          }
        }
      });
      panel_10.add(baseFlameGridXOffsetField);

      JLabel lblGridXOff = new JLabel();
      lblGridXOff.setToolTipText("");
      lblGridXOff.setText("Grid X Offset");
      lblGridXOff.setPreferredSize(new Dimension(94, 22));
      lblGridXOff.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGridXOff.setBounds(232, 8, 93, 22);
      panel_10.add(lblGridXOff);

      JLabel lblGridYOffset = new JLabel();
      lblGridYOffset.setToolTipText("");
      lblGridYOffset.setText("Grid Y Offset");
      lblGridYOffset.setPreferredSize(new Dimension(94, 22));
      lblGridYOffset.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGridYOffset.setBounds(232, 32, 93, 22);
      panel_10.add(lblGridYOffset);

      baseFlameGridYOffsetField = new JWFNumberField();
      baseFlameGridYOffsetField.setValueStep(1.0);
      baseFlameGridYOffsetField.setText("");
      baseFlameGridYOffsetField.setPreferredSize(new Dimension(100, 24));
      baseFlameGridYOffsetField.setOnlyIntegers(true);
      baseFlameGridYOffsetField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameGridYOffsetField.setBounds(327, 30, 100, 24);
      baseFlameGridYOffsetField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameGridYOffsetField_changed();
          }
        }
      });
      panel_10.add(baseFlameGridYOffsetField);

      JLabel lblGridXSize = new JLabel();
      lblGridXSize.setToolTipText("");
      lblGridXSize.setText("Grid X Size");
      lblGridXSize.setPreferredSize(new Dimension(94, 22));
      lblGridXSize.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGridXSize.setBounds(232, 68, 93, 22);
      panel_10.add(lblGridXSize);

      baseFlameGridXSizeField = new JWFNumberField();
      baseFlameGridXSizeField.setMinValue(1.0);
      baseFlameGridXSizeField.setHasMinValue(true);
      baseFlameGridXSizeField.setValueStep(1.0);
      baseFlameGridXSizeField.setText("");
      baseFlameGridXSizeField.setPreferredSize(new Dimension(100, 24));
      baseFlameGridXSizeField.setOnlyIntegers(true);
      baseFlameGridXSizeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameGridXSizeField.setBounds(327, 66, 100, 24);
      baseFlameGridXSizeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameGridXSizeField_changed();
          }
        }
      });
      panel_10.add(baseFlameGridXSizeField);

      JLabel lblGridYSize = new JLabel();
      lblGridYSize.setToolTipText("");
      lblGridYSize.setText("Grid Y Size");
      lblGridYSize.setPreferredSize(new Dimension(94, 22));
      lblGridYSize.setFont(new Font("Dialog", Font.BOLD, 10));
      lblGridYSize.setBounds(232, 92, 93, 22);
      panel_10.add(lblGridYSize);

      baseFlameGridYSizeField = new JWFNumberField();
      baseFlameGridYSizeField.setMinValue(1.0);
      baseFlameGridYSizeField.setValueStep(1.0);
      baseFlameGridYSizeField.setText("");
      baseFlameGridYSizeField.setPreferredSize(new Dimension(100, 24));
      baseFlameGridYSizeField.setOnlyIntegers(true);
      baseFlameGridYSizeField.setHasMinValue(true);
      baseFlameGridYSizeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameGridYSizeField.setBounds(327, 90, 100, 24);
      baseFlameGridYSizeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameGridYSizeField_changed();
          }
        }
      });
      panel_10.add(baseFlameGridYSizeField);

      panel_20 = new JPanel();
      tabbedPane_1.addTab("Blending", null, panel_20, null);
      panel_20.setLayout(null);

      JLabel lblBrightnessMin = new JLabel();
      lblBrightnessMin.setToolTipText("");
      lblBrightnessMin.setText("Brightness Min");
      lblBrightnessMin.setPreferredSize(new Dimension(94, 22));
      lblBrightnessMin.setFont(new Font("Dialog", Font.BOLD, 10));
      lblBrightnessMin.setBounds(6, 8, 130, 22);
      panel_20.add(lblBrightnessMin);

      baseFlameBrightnessMinField = new JWFNumberField();
      baseFlameBrightnessMinField.setHasMinValue(true);
      baseFlameBrightnessMinField.setValueStep(0.05);
      baseFlameBrightnessMinField.setText("");
      baseFlameBrightnessMinField.setPreferredSize(new Dimension(100, 24));
      baseFlameBrightnessMinField.setMaxValue(1.0);
      baseFlameBrightnessMinField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameBrightnessMinField.setBounds(138, 6, 100, 24);
      baseFlameBrightnessMinField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameBrightnessMinField_changed();
          }
        }
      });
      panel_20.add(baseFlameBrightnessMinField);

      JLabel lblBrightnessMax = new JLabel();
      lblBrightnessMax.setToolTipText("");
      lblBrightnessMax.setText("Brightness Max");
      lblBrightnessMax.setPreferredSize(new Dimension(94, 22));
      lblBrightnessMax.setFont(new Font("Dialog", Font.BOLD, 10));
      lblBrightnessMax.setBounds(6, 32, 130, 22);
      panel_20.add(lblBrightnessMax);

      baseFlameBrightnessMaxField = new JWFNumberField();
      baseFlameBrightnessMaxField.setValueStep(0.05);
      baseFlameBrightnessMaxField.setText("");
      baseFlameBrightnessMaxField.setPreferredSize(new Dimension(100, 24));
      baseFlameBrightnessMaxField.setMaxValue(1.0);
      baseFlameBrightnessMaxField.setHasMinValue(true);
      baseFlameBrightnessMaxField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameBrightnessMaxField.setBounds(138, 30, 100, 24);
      baseFlameBrightnessMaxField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameBrightnessMaxField_changed();
          }
        }
      });
      panel_20.add(baseFlameBrightnessMaxField);

      JLabel lblBrightnessChange = new JLabel();
      lblBrightnessChange.setToolTipText("Probability to change brightness to a value between min and max, otherwise the min value is choosen");
      lblBrightnessChange.setText("Brightness Change");
      lblBrightnessChange.setPreferredSize(new Dimension(94, 22));
      lblBrightnessChange.setFont(new Font("Dialog", Font.BOLD, 10));
      lblBrightnessChange.setBounds(6, 57, 130, 22);
      panel_20.add(lblBrightnessChange);

      baseFlameBrightnessChangeField = new JWFNumberField();
      baseFlameBrightnessChangeField.setHasMinValue(true);
      baseFlameBrightnessChangeField.setHasMaxValue(true);
      baseFlameBrightnessChangeField.setValueStep(0.05);
      baseFlameBrightnessChangeField.setText("");
      baseFlameBrightnessChangeField.setPreferredSize(new Dimension(100, 24));
      baseFlameBrightnessChangeField.setMaxValue(1.0);
      baseFlameBrightnessChangeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      baseFlameBrightnessChangeField.setBounds(138, 55, 100, 24);
      baseFlameBrightnessChangeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameBrightnessChangeField_changed();
          }
        }
      });
      panel_20.add(baseFlameBrightnessChangeField);

      panel_12 = new JPanel();
      tabbedPane_1.addTab("Mutations", null, panel_12, null);
      panel_12.setLayout(null);

      paramMinValueField = new JWFNumberField();
      paramMinValueField.setValueStep(0.05);
      paramMinValueField.setText("");
      paramMinValueField.setPreferredSize(new Dimension(100, 24));
      paramMinValueField.setFont(new Font("Dialog", Font.PLAIN, 10));
      paramMinValueField.setBounds(101, 98, 100, 24);
      paramMinValueField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.paramMinValueField_changed();
          }
        }
      });
      panel_12.add(paramMinValueField);

      JLabel label = new JLabel();
      label.setToolTipText("");
      label.setText("Min Value");
      label.setPreferredSize(new Dimension(94, 22));
      label.setFont(new Font("Dialog", Font.BOLD, 10));
      label.setBounds(6, 100, 93, 22);
      panel_12.add(label);

      JLabel label_1 = new JLabel();
      label_1.setToolTipText("");
      label_1.setText("Max Value");
      label_1.setPreferredSize(new Dimension(94, 22));
      label_1.setFont(new Font("Dialog", Font.BOLD, 10));
      label_1.setBounds(6, 124, 93, 22);
      panel_12.add(label_1);

      paramMaxValueField = new JWFNumberField();
      paramMaxValueField.setValueStep(0.05);
      paramMaxValueField.setText("");
      paramMaxValueField.setPreferredSize(new Dimension(100, 24));
      paramMaxValueField.setFont(new Font("Dialog", Font.PLAIN, 10));
      paramMaxValueField.setBounds(101, 122, 100, 24);
      paramMaxValueField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.paramMaxValueField_changed();
          }
        }
      });
      panel_12.add(paramMaxValueField);

      selectedMutationCmb = new JComboBox();
      selectedMutationCmb.setPreferredSize(new Dimension(125, 22));
      selectedMutationCmb.setFont(new Font("Dialog", Font.BOLD, 10));
      selectedMutationCmb.setBounds(101, 6, 100, 22);
      selectedMutationCmb.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (iflamesController != null) {
            iflamesController.selectedMutationCmb_changed();
          }
        }
      });
      panel_12.add(selectedMutationCmb);

      JLabel lblMutation = new JLabel();
      lblMutation.setText("Mutation");
      lblMutation.setPreferredSize(new Dimension(94, 22));
      lblMutation.setFont(new Font("Dialog", Font.BOLD, 10));
      lblMutation.setBounds(6, 6, 82, 22);
      panel_12.add(lblMutation);

      JScrollPane scrollPane_2 = new JScrollPane();
      scrollPane_2.setBounds(210, 6, 279, 140);
      panel_12.add(scrollPane_2);

      paramPropertyPathTree = new JTree();
      paramPropertyPathTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          if (iflamesController != null) {
            iflamesController.paramPropertyPathTree_changed();
          }
        }
      });
      paramPropertyPathTree.setRootVisible(false);

      scrollPane_2.setViewportView(paramPropertyPathTree);

      panel_17 = new JPanel();
      tabbedPane_1.addTab("Dynamics", null, panel_17, null);
      panel_17.setLayout(null);

      speedXField = new JWFNumberField();
      speedXField.setValueStep(0.05);
      speedXField.setText("");
      speedXField.setPreferredSize(new Dimension(100, 24));
      speedXField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedXField.setBounds(101, 6, 100, 24);
      speedXField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedXField_changed();
          }
        }
      });
      panel_17.add(speedXField);

      JLabel lblSpeedX = new JLabel();
      lblSpeedX.setToolTipText("");
      lblSpeedX.setText("Speed X");
      lblSpeedX.setPreferredSize(new Dimension(94, 22));
      lblSpeedX.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedX.setBounds(6, 8, 93, 22);
      panel_17.add(lblSpeedX);

      JLabel lblSpeedY = new JLabel();
      lblSpeedY.setToolTipText("");
      lblSpeedY.setText("Speed Y");
      lblSpeedY.setPreferredSize(new Dimension(94, 22));
      lblSpeedY.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedY.setBounds(6, 58, 93, 22);
      panel_17.add(lblSpeedY);

      speedYField = new JWFNumberField();
      speedYField.setValueStep(0.05);
      speedYField.setText("");
      speedYField.setPreferredSize(new Dimension(100, 24));
      speedYField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedYField.setBounds(101, 56, 100, 24);
      speedYField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedYField_changed();
          }
        }
      });
      panel_17.add(speedYField);

      JLabel lblSpeedZ = new JLabel();
      lblSpeedZ.setToolTipText("");
      lblSpeedZ.setText("Speed Z");
      lblSpeedZ.setPreferredSize(new Dimension(94, 22));
      lblSpeedZ.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedZ.setBounds(6, 106, 93, 22);
      panel_17.add(lblSpeedZ);

      speedZField = new JWFNumberField();
      speedZField.setValueStep(0.05);
      speedZField.setText("");
      speedZField.setPreferredSize(new Dimension(100, 24));
      speedZField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedZField.setBounds(101, 104, 100, 24);
      speedZField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedZField_changed();
          }
        }
      });
      panel_17.add(speedZField);

      speedXVarField = new JWFNumberField();
      speedXVarField.setHasMinValue(true);
      speedXVarField.setValueStep(0.05);
      speedXVarField.setText("");
      speedXVarField.setPreferredSize(new Dimension(100, 24));
      speedXVarField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedXVarField.setBounds(101, 30, 100, 24);
      speedXVarField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedXVarField_changed();
          }
        }
      });
      panel_17.add(speedXVarField);

      JLabel lblSpeedXVariation = new JLabel();
      lblSpeedXVariation.setToolTipText("");
      lblSpeedXVariation.setText("Speed X Variation");
      lblSpeedXVariation.setPreferredSize(new Dimension(94, 22));
      lblSpeedXVariation.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedXVariation.setBounds(6, 32, 93, 22);
      panel_17.add(lblSpeedXVariation);

      JLabel lblSpeedYVariation = new JLabel();
      lblSpeedYVariation.setToolTipText("");
      lblSpeedYVariation.setText("Speed Y Variation");
      lblSpeedYVariation.setPreferredSize(new Dimension(94, 22));
      lblSpeedYVariation.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedYVariation.setBounds(6, 82, 93, 22);
      panel_17.add(lblSpeedYVariation);

      speedYVarField = new JWFNumberField();
      speedYVarField.setHasMinValue(true);
      speedYVarField.setValueStep(0.05);
      speedYVarField.setText("");
      speedYVarField.setPreferredSize(new Dimension(100, 24));
      speedYVarField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedYVarField.setBounds(101, 80, 100, 24);
      speedYVarField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedYVarField_changed();
          }
        }
      });
      panel_17.add(speedYVarField);

      JLabel lblSpeedZVariation = new JLabel();
      lblSpeedZVariation.setToolTipText("");
      lblSpeedZVariation.setText("Speed Z Variation");
      lblSpeedZVariation.setPreferredSize(new Dimension(94, 22));
      lblSpeedZVariation.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedZVariation.setBounds(6, 130, 93, 22);
      panel_17.add(lblSpeedZVariation);

      speedZVarField = new JWFNumberField();
      speedZVarField.setHasMinValue(true);
      speedZVarField.setValueStep(0.05);
      speedZVarField.setText("");
      speedZVarField.setPreferredSize(new Dimension(100, 24));
      speedZVarField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedZVarField.setBounds(101, 128, 100, 24);
      speedZVarField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedZVarField_changed();
          }
        }
      });
      panel_17.add(speedZVarField);

      speedAlphaField = new JWFNumberField();
      speedAlphaField.setValueStep(0.05);
      speedAlphaField.setText("");
      speedAlphaField.setPreferredSize(new Dimension(100, 24));
      speedAlphaField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedAlphaField.setBounds(318, 6, 100, 24);
      speedAlphaField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedAlphaField_changed();
          }
        }
      });
      panel_17.add(speedAlphaField);

      JLabel lblSpeedAlpha = new JLabel();
      lblSpeedAlpha.setToolTipText("");
      lblSpeedAlpha.setText("Speed Alpha");
      lblSpeedAlpha.setPreferredSize(new Dimension(94, 22));
      lblSpeedAlpha.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedAlpha.setBounds(223, 8, 93, 22);
      panel_17.add(lblSpeedAlpha);

      JLabel lblSpeedBeta = new JLabel();
      lblSpeedBeta.setToolTipText("");
      lblSpeedBeta.setText("Speed Beta");
      lblSpeedBeta.setPreferredSize(new Dimension(94, 22));
      lblSpeedBeta.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedBeta.setBounds(223, 58, 93, 22);
      panel_17.add(lblSpeedBeta);

      speedBetaField = new JWFNumberField();
      speedBetaField.setValueStep(0.05);
      speedBetaField.setText("");
      speedBetaField.setPreferredSize(new Dimension(100, 24));
      speedBetaField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedBetaField.setBounds(318, 56, 100, 24);
      speedBetaField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedBetaField_changed();
          }
        }
      });
      panel_17.add(speedBetaField);

      JLabel lblSpeedGamma = new JLabel();
      lblSpeedGamma.setToolTipText("");
      lblSpeedGamma.setText("Speed Gamma");
      lblSpeedGamma.setPreferredSize(new Dimension(94, 22));
      lblSpeedGamma.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedGamma.setBounds(223, 108, 93, 22);
      panel_17.add(lblSpeedGamma);

      speedGammaField = new JWFNumberField();
      speedGammaField.setValueStep(0.05);
      speedGammaField.setText("");
      speedGammaField.setPreferredSize(new Dimension(100, 24));
      speedGammaField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedGammaField.setBounds(318, 106, 100, 24);
      speedGammaField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedGammaField_changed();
          }
        }
      });
      panel_17.add(speedGammaField);

      JLabel lblSpeedAlphaVar = new JLabel();
      lblSpeedAlphaVar.setToolTipText("Speed Alpha Variation");
      lblSpeedAlphaVar.setText("Speed Alpha Var");
      lblSpeedAlphaVar.setPreferredSize(new Dimension(94, 22));
      lblSpeedAlphaVar.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedAlphaVar.setBounds(223, 32, 93, 22);
      panel_17.add(lblSpeedAlphaVar);

      JLabel lblSpeedBetaVar = new JLabel();
      lblSpeedBetaVar.setToolTipText("");
      lblSpeedBetaVar.setText("Speed Beta Var");
      lblSpeedBetaVar.setPreferredSize(new Dimension(94, 22));
      lblSpeedBetaVar.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedBetaVar.setBounds(223, 84, 93, 22);
      panel_17.add(lblSpeedBetaVar);

      JLabel lblSpeedGammaVar = new JLabel();
      lblSpeedGammaVar.setToolTipText("");
      lblSpeedGammaVar.setText("Speed Gamma Var");
      lblSpeedGammaVar.setPreferredSize(new Dimension(94, 22));
      lblSpeedGammaVar.setFont(new Font("Dialog", Font.BOLD, 10));
      lblSpeedGammaVar.setBounds(223, 130, 93, 22);
      panel_17.add(lblSpeedGammaVar);

      speedAlphaVarField = new JWFNumberField();
      speedAlphaVarField.setHasMinValue(true);
      speedAlphaVarField.setValueStep(0.05);
      speedAlphaVarField.setText("");
      speedAlphaVarField.setPreferredSize(new Dimension(100, 24));
      speedAlphaVarField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedAlphaVarField.setBounds(318, 30, 100, 24);
      speedAlphaVarField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedAlphaVarField_changed();
          }
        }
      });
      panel_17.add(speedAlphaVarField);

      speedBetaVarField = new JWFNumberField();
      speedBetaVarField.setHasMinValue(true);
      speedBetaVarField.setValueStep(0.05);
      speedBetaVarField.setText("");
      speedBetaVarField.setPreferredSize(new Dimension(100, 24));
      speedBetaVarField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedBetaVarField.setBounds(318, 82, 100, 24);
      speedBetaVarField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedBetaVarField_changed();
          }
        }
      });
      panel_17.add(speedBetaVarField);

      speedGammaVarField = new JWFNumberField();
      speedGammaVarField.setHasMinValue(true);
      speedGammaVarField.setValueStep(0.05);
      speedGammaVarField.setText("");
      speedGammaVarField.setPreferredSize(new Dimension(100, 24));
      speedGammaVarField.setFont(new Font("Dialog", Font.PLAIN, 10));
      speedGammaVarField.setBounds(318, 128, 100, 24);
      speedGammaVarField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.speedGammaVarField_changed();
          }
        }
      });
      panel_17.add(speedGammaVarField);

      JLabel lblRadialAccel = new JLabel();
      lblRadialAccel.setToolTipText("");
      lblRadialAccel.setText("Radial Accel");
      lblRadialAccel.setPreferredSize(new Dimension(94, 22));
      lblRadialAccel.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRadialAccel.setBounds(440, 8, 93, 22);
      panel_17.add(lblRadialAccel);

      radialAccelField = new JWFNumberField();
      radialAccelField.setValueStep(0.05);
      radialAccelField.setText("");
      radialAccelField.setPreferredSize(new Dimension(100, 24));
      radialAccelField.setFont(new Font("Dialog", Font.PLAIN, 10));
      radialAccelField.setBounds(535, 6, 100, 24);
      radialAccelField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.radialAccelField_changed();
          }
        }
      });
      panel_17.add(radialAccelField);

      JLabel lblRadialAccelVar = new JLabel();
      lblRadialAccelVar.setToolTipText("");
      lblRadialAccelVar.setText("Radial Accel Var");
      lblRadialAccelVar.setPreferredSize(new Dimension(94, 22));
      lblRadialAccelVar.setFont(new Font("Dialog", Font.BOLD, 10));
      lblRadialAccelVar.setBounds(440, 32, 93, 22);
      panel_17.add(lblRadialAccelVar);

      radialAccelVarField = new JWFNumberField();
      radialAccelVarField.setValueStep(0.05);
      radialAccelVarField.setText("");
      radialAccelVarField.setPreferredSize(new Dimension(100, 24));
      radialAccelVarField.setHasMinValue(true);
      radialAccelVarField.setFont(new Font("Dialog", Font.PLAIN, 10));
      radialAccelVarField.setBounds(535, 30, 100, 24);
      radialAccelVarField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.radialAccelVarField_changed();
          }
        }
      });
      panel_17.add(radialAccelVarField);

      JLabel lblTangentialAccel = new JLabel();
      lblTangentialAccel.setToolTipText("");
      lblTangentialAccel.setText("Tangential Accel");
      lblTangentialAccel.setPreferredSize(new Dimension(94, 22));
      lblTangentialAccel.setFont(new Font("Dialog", Font.BOLD, 10));
      lblTangentialAccel.setBounds(440, 60, 93, 22);
      panel_17.add(lblTangentialAccel);

      tangentialAccelField = new JWFNumberField();
      tangentialAccelField.setValueStep(0.05);
      tangentialAccelField.setText("");
      tangentialAccelField.setPreferredSize(new Dimension(100, 24));
      tangentialAccelField.setFont(new Font("Dialog", Font.PLAIN, 10));
      tangentialAccelField.setBounds(535, 58, 100, 24);
      tangentialAccelField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.tangentialAccelField_changed();
          }
        }
      });
      panel_17.add(tangentialAccelField);

      JLabel lblTangentAccelVar = new JLabel();
      lblTangentAccelVar.setToolTipText("");
      lblTangentAccelVar.setText("Tangent Accel Var");
      lblTangentAccelVar.setPreferredSize(new Dimension(94, 22));
      lblTangentAccelVar.setFont(new Font("Dialog", Font.BOLD, 10));
      lblTangentAccelVar.setBounds(440, 84, 93, 22);
      panel_17.add(lblTangentAccelVar);

      tangentialAccelVarField = new JWFNumberField();
      tangentialAccelVarField.setValueStep(0.05);
      tangentialAccelVarField.setText("");
      tangentialAccelVarField.setPreferredSize(new Dimension(100, 24));
      tangentialAccelVarField.setHasMinValue(true);
      tangentialAccelVarField.setFont(new Font("Dialog", Font.PLAIN, 10));
      tangentialAccelVarField.setBounds(535, 82, 100, 24);
      tangentialAccelVarField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.tangentialAccelVarField_changed();
          }
        }
      });
      panel_17.add(tangentialAccelVarField);

      copyDynamicsParamsToOthersButton = new JButton();
      copyDynamicsParamsToOthersButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.copyDynamicsParamsToOthersButton_clicked();
        }
      });
      copyDynamicsParamsToOthersButton.setBounds(440, 129, 192, 24);
      panel_17.add(copyDynamicsParamsToOthersButton);
      copyDynamicsParamsToOthersButton.setToolTipText("Copy the parameters of this base-flame to all other flames");
      copyDynamicsParamsToOthersButton.setText("Copy params to others");
      copyDynamicsParamsToOthersButton.setPreferredSize(new Dimension(125, 24));
      copyDynamicsParamsToOthersButton.setMinimumSize(new Dimension(100, 24));
      copyDynamicsParamsToOthersButton.setMaximumSize(new Dimension(32000, 24));
      copyDynamicsParamsToOthersButton.setFont(new Font("Dialog", Font.BOLD, 10));

      panel_13 = new JPanel();
      tabbedPane.addTab("Dynamics", null, panel_13, null);
      panel_13.setLayout(new BorderLayout(0, 0));

      JPanel panel_14 = new JPanel();
      panel_14.setPreferredSize(new Dimension(10, 64));
      panel_13.add(panel_14, BorderLayout.NORTH);
      panel_14.setLayout(null);

      motionLifeTimeField = new JWFNumberField();
      motionLifeTimeField.setHasMinValue(true);
      motionLifeTimeField.setValueStep(0.05);
      motionLifeTimeField.setText("");
      motionLifeTimeField.setPreferredSize(new Dimension(100, 24));
      motionLifeTimeField.setFont(new Font("Dialog", Font.PLAIN, 10));
      motionLifeTimeField.setBounds(101, 6, 100, 24);
      motionLifeTimeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.motionLifeTimeField_changed();
          }
        }
      });
      panel_14.add(motionLifeTimeField);

      JLabel lblLifeTime = new JLabel();
      lblLifeTime.setToolTipText("");
      lblLifeTime.setText("Life time");
      lblLifeTime.setPreferredSize(new Dimension(94, 22));
      lblLifeTime.setFont(new Font("Dialog", Font.BOLD, 10));
      lblLifeTime.setBounds(6, 8, 93, 22);
      panel_14.add(lblLifeTime);

      JLabel lblLifeTimeVariation = new JLabel();
      lblLifeTimeVariation.setToolTipText("");
      lblLifeTimeVariation.setText("Life time variation");
      lblLifeTimeVariation.setPreferredSize(new Dimension(94, 22));
      lblLifeTimeVariation.setFont(new Font("Dialog", Font.BOLD, 10));
      lblLifeTimeVariation.setBounds(6, 32, 93, 22);
      panel_14.add(lblLifeTimeVariation);

      motionLifeTimeVariationField = new JWFNumberField();
      motionLifeTimeVariationField.setHasMinValue(true);
      motionLifeTimeVariationField.setValueStep(0.05);
      motionLifeTimeVariationField.setText("");
      motionLifeTimeVariationField.setPreferredSize(new Dimension(100, 24));
      motionLifeTimeVariationField.setFont(new Font("Dialog", Font.PLAIN, 10));
      motionLifeTimeVariationField.setBounds(101, 30, 100, 24);
      motionLifeTimeVariationField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.motionLifeTimeVariationField_changed();
          }
        }
      });
      panel_14.add(motionLifeTimeVariationField);

      JLabel lblPropertiesIndicatedBy = new JLabel();
      lblPropertiesIndicatedBy.setToolTipText("");
      lblPropertiesIndicatedBy.setText("Properties indicated by a star-symbol may be animated by using motion-curves");
      lblPropertiesIndicatedBy.setPreferredSize(new Dimension(94, 22));
      lblPropertiesIndicatedBy.setFont(new Font("Dialog", Font.BOLD, 10));
      lblPropertiesIndicatedBy.setBounds(213, 8, 399, 22);
      panel_14.add(lblPropertiesIndicatedBy);

      JPanel panel_15 = new JPanel();
      panel_13.add(panel_15, BorderLayout.CENTER);
      panel_15.setLayout(new BorderLayout(0, 0));

      JTabbedPane tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
      panel_15.add(tabbedPane_2, BorderLayout.CENTER);

      JPanel panel_16 = new JPanel();
      tabbedPane_2.addTab("Force", null, panel_16, null);
      panel_16.setLayout(null);

      motionForceXField = new JWFNumberField();
      motionForceXField.setValueStep(0.05);
      motionForceXField.setText("");
      motionForceXField.setPreferredSize(new Dimension(100, 24));
      motionForceXField.setFont(new Font("Dialog", Font.PLAIN, 10));
      motionForceXField.setBounds(101, 6, 100, 24);
      motionForceXField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.motionForceXField_changed();
          }
        }
      });
      panel_16.add(motionForceXField);

      motionForceYField = new JWFNumberField();
      motionForceYField.setValueStep(0.05);
      motionForceYField.setText("");
      motionForceYField.setPreferredSize(new Dimension(100, 24));
      motionForceYField.setFont(new Font("Dialog", Font.PLAIN, 10));
      motionForceYField.setBounds(101, 30, 100, 24);
      motionForceYField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.motionForceYField_changed();
          }
        }
      });
      panel_16.add(motionForceYField);

      JLabel lblForceX = new JLabel();
      lblForceX.setToolTipText("");
      lblForceX.setText("Force X*");
      lblForceX.setPreferredSize(new Dimension(94, 22));
      lblForceX.setFont(new Font("Dialog", Font.BOLD, 10));
      lblForceX.setBounds(6, 8, 93, 22);
      panel_16.add(lblForceX);

      JLabel lblForceY = new JLabel();
      lblForceY.setToolTipText("");
      lblForceY.setText("Force Y*");
      lblForceY.setPreferredSize(new Dimension(94, 22));
      lblForceY.setFont(new Font("Dialog", Font.BOLD, 10));
      lblForceY.setBounds(6, 32, 93, 22);
      panel_16.add(lblForceY);

      JLabel lblForceZ = new JLabel();
      lblForceZ.setToolTipText("");
      lblForceZ.setText("Force Z*");
      lblForceZ.setPreferredSize(new Dimension(94, 22));
      lblForceZ.setFont(new Font("Dialog", Font.BOLD, 10));
      lblForceZ.setBounds(6, 56, 93, 22);
      panel_16.add(lblForceZ);

      motionForceZField = new JWFNumberField();
      motionForceZField.setValueStep(0.05);
      motionForceZField.setText("");
      motionForceZField.setPreferredSize(new Dimension(100, 24));
      motionForceZField.setFont(new Font("Dialog", Font.PLAIN, 10));
      motionForceZField.setBounds(101, 54, 100, 24);
      motionForceZField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.motionForceZField_changed();
          }
        }
      });
      panel_16.add(motionForceZField);

      panel_19 = new JPanel();
      tabbedPane_2.addTab("Center of Radial Forces", null, panel_19, null);
      panel_19.setLayout(null);

      forceCentreXField = new JWFNumberField();
      forceCentreXField.setValueStep(0.05);
      forceCentreXField.setText("");
      forceCentreXField.setPreferredSize(new Dimension(100, 24));
      forceCentreXField.setFont(new Font("Dialog", Font.PLAIN, 10));
      forceCentreXField.setBounds(101, 6, 100, 24);
      forceCentreXField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.forceCentreXField_changed();
          }
        }
      });
      panel_19.add(forceCentreXField);

      JLabel lblCentreX = new JLabel();
      lblCentreX.setToolTipText("");
      lblCentreX.setText("Centre X*");
      lblCentreX.setPreferredSize(new Dimension(94, 22));
      lblCentreX.setFont(new Font("Dialog", Font.BOLD, 10));
      lblCentreX.setBounds(6, 8, 93, 22);
      panel_19.add(lblCentreX);

      JLabel lblCentreY = new JLabel();
      lblCentreY.setToolTipText("");
      lblCentreY.setText("Centre Y*");
      lblCentreY.setPreferredSize(new Dimension(94, 22));
      lblCentreY.setFont(new Font("Dialog", Font.BOLD, 10));
      lblCentreY.setBounds(6, 32, 93, 22);
      panel_19.add(lblCentreY);

      forceCentreYField = new JWFNumberField();
      forceCentreYField.setValueStep(0.05);
      forceCentreYField.setText("");
      forceCentreYField.setPreferredSize(new Dimension(100, 24));
      forceCentreYField.setFont(new Font("Dialog", Font.PLAIN, 10));
      forceCentreYField.setBounds(101, 30, 100, 24);
      forceCentreYField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.forceCentreYField_changed();
          }
        }
      });
      panel_19.add(forceCentreYField);

      JLabel lblCentreZ = new JLabel();
      lblCentreZ.setToolTipText("");
      lblCentreZ.setText("Centre Z*");
      lblCentreZ.setPreferredSize(new Dimension(94, 22));
      lblCentreZ.setFont(new Font("Dialog", Font.BOLD, 10));
      lblCentreZ.setBounds(6, 56, 93, 22);
      panel_19.add(lblCentreZ);

      forceCentreZField = new JWFNumberField();
      forceCentreZField.setValueStep(0.05);
      forceCentreZField.setText("");
      forceCentreZField.setPreferredSize(new Dimension(100, 24));
      forceCentreZField.setFont(new Font("Dialog", Font.PLAIN, 10));
      forceCentreZField.setBounds(101, 54, 100, 24);
      forceCentreZField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.forceCentreZField_changed();
          }
        }
      });
      panel_19.add(forceCentreZField);

      panel_11 = new JPanel();
      tabbedPane.addTab("Statistics", null, panel_11, null);
      panel_11.setLayout(new BorderLayout(0, 0));

      scrollPane_1 = new JScrollPane();
      panel_11.add(scrollPane_1, BorderLayout.CENTER);

      statisticsTextArea = new JTextArea();
      statisticsTextArea.setEditable(false);
      scrollPane_1.setViewportView(statisticsTextArea);
      baseFlameRotateGammaVariationField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameRotateGammaVariationField_changed();
          }
        }
      });
      baseFlameRotateGammaField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameRotateGammaField_changed();
          }
        }
      });
      baseFlameRotateBetaVariationField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameRotateBetaVariationField_changed();
          }
        }
      });
      baseFlameRotateBetaField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameRotateBetaField_changed();
          }
        }
      });
      baseFlameRotateAlphaVariationField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameRotateAlphaVariationField_changed();
          }
        }
      });
      baseFlameRotateAlphaField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameRotateAlphaField_changed();
          }
        }
      });
      baseFlameCentreZField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameCentreZField_changed();
          }
        }
      });
      baseFlameCentreYField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameCentreYField_changed();
          }
        }
      });
      baseFlameCentreXField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameCentreXField_changed();
          }
        }
      });
      baseFlameSizeVariationField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameSizeVariationField_changed();
          }
        }
      });
      baseFlameSizeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.baseFlameSizeField_changed();
          }
        }
      });

      panel_6 = new JPanel();
      panel_6.setPreferredSize(new Dimension(128, 10));
      mainBottomPanel.add(panel_6, BorderLayout.EAST);
      panel_6.setLayout(null);

      refreshIFlameButton = new JButton();
      refreshIFlameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.refreshIFlameButton_clicked();
        }
      });
      refreshIFlameButton.setBounds(6, 66, 105, 24);
      panel_6.add(refreshIFlameButton);
      refreshIFlameButton.setToolTipText("Rebuild and refresh IFlame");
      refreshIFlameButton.setText("Refresh");
      refreshIFlameButton.setPreferredSize(new Dimension(125, 24));
      refreshIFlameButton.setMinimumSize(new Dimension(100, 24));
      refreshIFlameButton.setMaximumSize(new Dimension(32000, 24));
      refreshIFlameButton.setFont(new Font("Dialog", Font.BOLD, 10));

      autoRefreshButton = new JToggleButton();
      autoRefreshButton.setBounds(6, 92, 105, 24);
      panel_6.add(autoRefreshButton);
      autoRefreshButton.setSelected(true);
      autoRefreshButton.setToolTipText("Automatically rebuild the IFlame after changes, which may be slow");
      autoRefreshButton.setText("Auto Refresh");
      autoRefreshButton.setPreferredSize(new Dimension(136, 24));
      autoRefreshButton.setFont(new Font("Dialog", Font.BOLD, 10));

      undoButton = new JButton();
      undoButton.setBounds(6, 130, 105, 24);
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
      redoButton.setBounds(6, 156, 105, 24);
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

      previewButton = new JToggleButton();
      previewButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          iflamesController.previewButton_clicked();
        }
      });
      previewButton.setToolTipText("Draw circles instead of real fractals");
      previewButton.setText("Preview");
      previewButton.setPreferredSize(new Dimension(136, 24));
      previewButton.setFont(new Font("Dialog", Font.BOLD, 10));
      previewButton.setBounds(6, 32, 105, 24);
      panel_6.add(previewButton);

      panel_8 = new JPanel();
      panel_8.setPreferredSize(new Dimension(10, 26));
      mainBottomPanel.add(panel_8, BorderLayout.NORTH);
      panel_8.setLayout(new BorderLayout(0, 0));

      mainProgressBar = new JProgressBar();
      mainProgressBar.setBorder(null);
      panel_8.add(mainProgressBar, BorderLayout.CENTER);
      mainProgressBar.setValue(0);
      mainProgressBar.setStringPainted(true);
      mainProgressBar.setPreferredSize(new Dimension(169, 14));

      panel_18 = new JPanel();
      panel_18.setPreferredSize(new Dimension(346, 10));
      panel_8.add(panel_18, BorderLayout.WEST);
      panel_18.setLayout(null);

      motionTimeField = new JWFNumberField();
      motionTimeField.setBounds(240, 0, 100, 24);
      panel_18.add(motionTimeField);
      motionTimeField.setHasMinValue(true);
      motionTimeField.setValueStep(0.05);
      motionTimeField.setText("");
      motionTimeField.setPreferredSize(new Dimension(100, 24));
      motionTimeField.setFont(new Font("Dialog", Font.PLAIN, 10));

      JLabel lblTime = new JLabel();
      lblTime.setBounds(145, 2, 93, 22);
      panel_18.add(lblTime);
      lblTime.setToolTipText("");
      lblTime.setText("Time");
      lblTime.setPreferredSize(new Dimension(94, 22));
      lblTime.setFont(new Font("Dialog", Font.BOLD, 10));
      motionTimeField.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          if (iflamesController != null) {
            iflamesController.motionTimeField_changed();
          }
        }
      });
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

      scrollPane = new JScrollPane();
      mainCenterPanel.add(scrollPane, BorderLayout.CENTER);

      introductionTextPane = new JTextPane();
      introductionTextPane.setEditable(false);
      scrollPane.setViewportView(introductionTextPane);
    }
    return jContentPane;
  }

  public void createController(MainController pMainController, TinaController pTinaController, StandardErrorHandler pErrorHandler) {
    iflamesController = new IFlamesController(pMainController, pTinaController, pErrorHandler, this, getMainCenterPanel(),
        getUndoButton(), getRedoButton(), getRenderFlameButton(), getImageStackRootPanel(), getFlameStackRootPanel(),
        getLoadIFlameButton(), getLoadIFlameFromClipboardButton(), getSaveIFlameToClipboardButton(),
        getSaveIFlameButton(), getRefreshIFlameButton(), getMainProgressBar(), getAutoRefreshButton(),
        getBaseFlameCmb(), getBaseFlamePreviewRootPnl(), getResolutionProfileCmb(), getEdgesNorthButton(),
        getEdgesWestButton(), getEdgesEastButton(), getEdgesSouthButton(), getErodeButton(), getDisplayPreprocessedImageButton(),
        getErodeSizeField(), getMaxImageWidthField(), getStructureThresholdField(), getStructureDensityField(),
        getGlobalScaleXField(), getGlobalScaleYField(), getGlobalScaleZField(), getGlobalOffsetXField(),
        getGlobalOffsetYField(), getGlobalOffsetZField(), getShapeDistributionCmb(), getIflameBrightnessField(),
        getImageBrightnessField(), getIflameDensityField(), getBaseFlameSizeField(), getBaseFlameSizeVariationField(),
        getBaseFlameRotateAlphaField(), getBaseFlameRotateAlphaVariationField(), getBaseFlameRotateBetaField(),
        getBaseFlameRotateBetaVariationField(), getBaseFlameRotateGammaField(), getBaseFlameRotateGammaVariationField(),
        getBaseFlameCentreXField(), getBaseFlameCentreYField(), getBaseFlameCentreZField(), getBaseFlameFromClipboardButton(),
        getBaseFlameToClipboardButton(), getBaseFlameClearButton(), getBaseFlameClearAllButton(), getPreviewButton(),
        getRenderFlameButton(), getIntroductionTextPane(), getBaseFlameMinValueField(), getBaseFlameMinValueLabel(),
        getBaseFlameMaxValueField(), getBaseFlameMaxValueLabel(), getStatisticsTextArea(), getBaseFlameWeightField(),
        getBaseFlameGridXOffsetField(), getBaseFlameGridYOffsetField(), getBaseFlameGridXSizeField(),
        getBaseFlameGridYSizeField(), getSelectedMutationCmb(), getParamMinValueField(), getParamMaxValueField(),
        getParamPropertyPathTree(), getIflameToEditorButton(), getMotionTimeField(), getMotionLifeTimeField(),
        getMotionLifeTimeVariationField(), getMotionForceXField(), getMotionForceYField(), getMotionForceZField(),
        getSpeedXField(), getSpeedYField(), getSpeedZField(), getSpeedXVarField(), getSpeedYVarField(), getSpeedZVarField(),
        getSpeedAlphaField(), getSpeedBetaField(), getSpeedGammaField(), getSpeedAlphaVarField(), getSpeedBetaVarField(),
        getSpeedGammaVarField(), getRadialAccelField(), getRadialAccelVarField(), getTangentialAccelField(),
        getTangentialAccelVarField(), getForceCentreXField(), getForceCentreYField(), getForceCentreZField(),
        getBaseFlameClearOthersButton(), getCopyDynamicsParamsToOthersButton(), getBaseFlameBrightnessMinField(),
        getBaseFlameBrightnessMaxField(), getBaseFlameBrightnessChangeField(), getImageRedChangeField(),
        getImageGreenChangeField(), getImageBlueChangeField(), getImageHueChangeField(), getImageSaturationChangeField(),
        getImageLightnessChangeField(), getBaseFlameInstancingCBx());
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

  public JWFNumberField getErodeSizeField() {
    return erodeSizeField;
  }

  public JWFNumberField getMaxImageWidthField() {
    return maxImageWidthField;
  }

  public JWFNumberField getGlobalScaleXField() {
    return globalScaleXField;
  }

  public JWFNumberField getGlobalScaleYField() {
    return globalScaleYField;
  }

  public JWFNumberField getGlobalScaleZField() {
    return globalScaleZField;
  }

  public JWFNumberField getGlobalOffsetXField() {
    return globalOffsetXField;
  }

  public JWFNumberField getGlobalOffsetYField() {
    return globalOffsetYField;
  }

  public JWFNumberField getGlobalOffsetZField() {
    return globalOffsetZField;
  }

  public JWFNumberField getStructureThresholdField() {
    return structureThresholdField;
  }

  public JWFNumberField getStructureDensityField() {
    return structureDensityField;
  }

  public JComboBox getShapeDistributionCmb() {
    return shapeDistributionCmb;
  }

  public JButton getRefreshLibraryButton() {
    return refreshLibraryButton;
  }

  public IFlamesController getIflamesController() {
    return iflamesController;
  }

  public JWFNumberField getIflameBrightnessField() {
    return iflameBrightnessField;
  }

  public JWFNumberField getImageBrightnessField() {
    return imageBrightnessField;
  }

  public JWFNumberField getIflameDensityField() {
    return iflameDensityField;
  }

  public JWFNumberField getBaseFlameSizeField() {
    return baseFlameSizeField;
  }

  public JWFNumberField getBaseFlameSizeVariationField() {
    return baseFlameSizeVariationField;
  }

  public JWFNumberField getBaseFlameCentreXField() {
    return baseFlameCentreXField;
  }

  public JWFNumberField getBaseFlameCentreYField() {
    return baseFlameCentreYField;
  }

  public JWFNumberField getBaseFlameRotateAlphaField() {
    return baseFlameRotateAlphaField;
  }

  public JWFNumberField getBaseFlameRotateAlphaVariationField() {
    return baseFlameRotateAlphaVariationField;
  }

  public JWFNumberField getBaseFlameRotateBetaField() {
    return baseFlameRotateBetaField;
  }

  public JWFNumberField getBaseFlameRotateBetaVariationField() {
    return baseFlameRotateBetaVariationField;
  }

  public JWFNumberField getBaseFlameRotateGammaField() {
    return baseFlameRotateGammaField;
  }

  public JWFNumberField getBaseFlameRotateGammaVariationField() {
    return baseFlameRotateGammaVariationField;
  }

  public JWFNumberField getBaseFlameCentreZField() {
    return baseFlameCentreZField;
  }

  public JButton getBaseFlameFromClipboardButton() {
    return baseFlameFromClipboardButton;
  }

  public JButton getBaseFlameToClipboardButton() {
    return baseFlameToClipboardButton;
  }

  public JButton getBaseFlameClearButton() {
    return baseFlameClearButton;
  }

  public JButton getBaseFlameClearAllButton() {
    return baseFlameClearAllButton;
  }

  public JToggleButton getPreviewButton() {
    return previewButton;
  }

  public JTextPane getIntroductionTextPane() {
    return introductionTextPane;
  }

  public JTextArea getStatisticsTextArea() {
    return statisticsTextArea;
  }

  public JLabel getBaseFlameMinValueLabel() {
    return baseFlameMinValueLabel;
  }

  public JLabel getBaseFlameMaxValueLabel() {
    return baseFlameMaxValueLabel;
  }

  public JWFNumberField getBaseFlameMinValueField() {
    return baseFlameMinValueField;
  }

  public JWFNumberField getBaseFlameMaxValueField() {
    return baseFlameMaxValueField;
  }

  public JWFNumberField getBaseFlameWeightField() {
    return baseFlameWeightField;
  }

  public JWFNumberField getBaseFlameGridXOffsetField() {
    return baseFlameGridXOffsetField;
  }

  public JWFNumberField getBaseFlameGridYOffsetField() {
    return baseFlameGridYOffsetField;
  }

  public JWFNumberField getBaseFlameGridXSizeField() {
    return baseFlameGridXSizeField;
  }

  public JWFNumberField getBaseFlameGridYSizeField() {
    return baseFlameGridYSizeField;
  }

  public JComboBox getSelectedMutationCmb() {
    return selectedMutationCmb;
  }

  public JWFNumberField getParamMaxValueField() {
    return paramMaxValueField;
  }

  public JTree getParamPropertyPathTree() {
    return paramPropertyPathTree;
  }

  public JWFNumberField getParamMinValueField() {
    return paramMinValueField;
  }

  public JWFNumberField getMotionTimeField() {
    return motionTimeField;
  }

  public JWFNumberField getMotionLifeTimeField() {
    return motionLifeTimeField;
  }

  public JWFNumberField getMotionLifeTimeVariationField() {
    return motionLifeTimeVariationField;
  }

  public JWFNumberField getMotionForceXField() {
    return motionForceXField;
  }

  public JWFNumberField getMotionForceYField() {
    return motionForceYField;
  }

  public JWFNumberField getMotionForceZField() {
    return motionForceZField;
  }

  public JButton getIflameToEditorButton() {
    return iflameToEditorButton;
  }

  public JWFNumberField getSpeedXField() {
    return speedXField;
  }

  public JWFNumberField getSpeedYField() {
    return speedYField;
  }

  public JWFNumberField getSpeedZField() {
    return speedZField;
  }

  public JWFNumberField getSpeedAlphaField() {
    return speedAlphaField;
  }

  public JWFNumberField getSpeedBetaField() {
    return speedBetaField;
  }

  public JWFNumberField getSpeedGammaField() {
    return speedGammaField;
  }

  public JWFNumberField getSpeedXVarField() {
    return speedXVarField;
  }

  public JWFNumberField getSpeedYVarField() {
    return speedYVarField;
  }

  public JWFNumberField getSpeedZVarField() {
    return speedZVarField;
  }

  public JWFNumberField getSpeedAlphaVarField() {
    return speedAlphaVarField;
  }

  public JWFNumberField getSpeedBetaVarField() {
    return speedBetaVarField;
  }

  public JWFNumberField getSpeedGammaVarField() {
    return speedGammaVarField;
  }

  public JWFNumberField getRadialAccelField() {
    return radialAccelField;
  }

  public JWFNumberField getRadialAccelVarField() {
    return radialAccelVarField;
  }

  public JWFNumberField getTangentialAccelField() {
    return tangentialAccelField;
  }

  public JWFNumberField getTangentialAccelVarField() {
    return tangentialAccelVarField;
  }

  public JWFNumberField getForceCentreXField() {
    return forceCentreXField;
  }

  public JWFNumberField getForceCentreYField() {
    return forceCentreYField;
  }

  public JWFNumberField getForceCentreZField() {
    return forceCentreZField;
  }

  public JButton getCopyDynamicsParamsToOthersButton() {
    return copyDynamicsParamsToOthersButton;
  }

  public JButton getBaseFlameClearOthersButton() {
    return baseFlameClearOthersButton;
  }

  public JButton getClearCacheButton() {
    return clearCacheButton;
  }

  public JWFNumberField getBaseFlameBrightnessMinField() {
    return baseFlameBrightnessMinField;
  }

  public JWFNumberField getBaseFlameBrightnessMaxField() {
    return baseFlameBrightnessMaxField;
  }

  public JWFNumberField getImageRedChangeField() {
    return imageRedChangeField;
  }

  public JWFNumberField getImageGreenChangeField() {
    return imageGreenChangeField;
  }

  public JWFNumberField getImageBlueChangeField() {
    return imageBlueChangeField;
  }

  public JWFNumberField getImageHueChangeField() {
    return imageHueChangeField;
  }

  public JWFNumberField getImageSaturationChangeField() {
    return imageSaturationChangeField;
  }

  public JWFNumberField getImageLightnessChangeField() {
    return imageLightnessChangeField;
  }

  public JWFNumberField getBaseFlameBrightnessChangeField() {
    return baseFlameBrightnessChangeField;
  }

  public JCheckBox getBaseFlameInstancingCBx() {
    return baseFlameInstancingCBx;
  }

  public JButton getCopyBaseFlameParamsToOthersButton() {
    return copyBaseFlameParamsToOthersButton;
  }
}
