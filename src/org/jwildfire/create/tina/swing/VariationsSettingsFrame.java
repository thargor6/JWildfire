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
package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.swing.JWildfire;

public class VariationsSettingsFrame extends JFrame {
  private TinaController tinaController;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JPanel batchRenderPanel = null;
  private JScrollPane variationsScrollPane = null;
  private List<String> variationNames = null;
  private List<JCheckBox> checkboxes = new ArrayList<>();

  public VariationsSettingsFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(298, 620);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Variations settings");
    this.setVisible(false);
    this.setResizable(true);
    this.setContentPane(getJContentPane());
    refreshVariationsPanel();
  }

  private void refreshVariationsPanel() {
    if (variationsPanel != null) {
      try {
        variationsScrollPane.setViewportView(null);
        variationsPanel.removeAll();
      }
      finally {
        variationsPanel = null;
      }
    }
    checkboxes.clear();
    variationsPanel = new JPanel();
    variationsPanel.setLayout(null);

    variationNames = VariationFuncList.getNameList();
    Collections.sort(variationNames);

    List<String> excludedVariations = Prefs.getPrefs().getTinaExcludedVariations();

    int xOffset = 8;
    int xSize = 160;
    int yOffset = 8;
    int ySize = 18;
    int yGap = 4;

    int currY = yOffset;
    for (String variation : variationNames) {
      JCheckBox cbx = new JCheckBox(variation);
      cbx.setSelected(!excludedVariations.contains(variation));
      cbx.setBounds(xOffset, currY, xSize, ySize);
      checkboxes.add(cbx);
      variationsPanel.add(cbx);
      currY += ySize + yGap;
    }
    int width = xSize + 2 * xOffset;
    int height = currY;
    variationsPanel.setBounds(0, 0, width, height);
    variationsPanel.setPreferredSize(new Dimension(width, height));

    variationsScrollPane.setViewportView(variationsPanel);
    variationsScrollPane.invalidate();
    variationsScrollPane.validate();

    refreshTitle();
  }

  private void refreshTitle() {
    int excluded = 0;
    if (checkboxes != null) {
      for (JCheckBox cbx : checkboxes) {
        if (!cbx.isSelected()) {
          excluded++;
        }
      }
    }
    this.setTitle((variationNames.size() - excluded) + "/" + variationNames.size() + " variations active");
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
      jContentPane.add(getBatchRenderPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes batchRenderPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getBatchRenderPanel() {
    if (batchRenderPanel == null) {
      batchRenderPanel = new JPanel();
      batchRenderPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new EmptyBorder(10, 10, 10, 10));
      batchRenderPanel.add(panel_1);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel panel_3 = new JPanel();
      panel_3.setPreferredSize(new Dimension(10, 32));
      panel_1.add(panel_3, BorderLayout.SOUTH);
      panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

      JButton saveBtn = new JButton();
      saveBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveAndApplyChanges();
        }
      });
      saveBtn.setMnemonic(KeyEvent.VK_S);
      saveBtn.setText("Save and Apply");
      saveBtn.setPreferredSize(new Dimension(125, 24));
      saveBtn.setMinimumSize(new Dimension(159, 24));
      saveBtn.setMaximumSize(new Dimension(159, 24));
      saveBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      saveBtn.setAlignmentX(0.5f);
      panel_3.add(saveBtn);

      JButton cancelBtn = new JButton();
      cancelBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          refreshVariationsPanel();
        }
      });
      cancelBtn.setMnemonic(KeyEvent.VK_C);
      cancelBtn.setText("Cancel Changes");
      cancelBtn.setPreferredSize(new Dimension(125, 24));
      cancelBtn.setMinimumSize(new Dimension(159, 24));
      cancelBtn.setMaximumSize(new Dimension(159, 24));
      cancelBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      cancelBtn.setAlignmentX(0.5f);
      panel_3.add(cancelBtn);
      panel_1.add(getVariationsScrollPane(), BorderLayout.CENTER);

      JPanel panel = new JPanel();
      panel.setPreferredSize(new Dimension(10, 32));
      panel_1.add(panel, BorderLayout.NORTH);

      JButton btnActivateAll = new JButton();
      btnActivateAll.setToolTipText("Does not save/apply automatically");
      btnActivateAll.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          activateAll();
        }
      });
      btnActivateAll.setText("Activate all Checkboxes");
      btnActivateAll.setPreferredSize(new Dimension(179, 24));
      btnActivateAll.setMnemonic(KeyEvent.VK_A);
      btnActivateAll.setMinimumSize(new Dimension(209, 24));
      btnActivateAll.setMaximumSize(new Dimension(209, 24));
      btnActivateAll.setFont(new Font("Dialog", Font.BOLD, 10));
      btnActivateAll.setAlignmentX(0.5f);
      panel.add(btnActivateAll);
    }
    return batchRenderPanel;
  }

  protected void activateAll() {
    for (JCheckBox cbx : checkboxes) {
      cbx.setSelected(true);
    }
    refreshTitle();
  }

  protected void saveAndApplyChanges() {
    try {
      List<String> excludedVariations = new ArrayList<>();
      if (!checkboxes.isEmpty() && variationNames != null && checkboxes.size() == variationNames.size()) {
        for (int i = 0; i < checkboxes.size(); i++) {
          if (!checkboxes.get(i).isSelected()) {
            excludedVariations.add(variationNames.get(i));
          }
        }
      }
      Prefs.getPrefs().setTinaExcludedVariations(excludedVariations);
      Prefs.getPrefs().saveToFile();
      VariationFuncList.invalidateExcludedNameList();
      boolean oldNoRefresh = tinaController.isNoRefresh();
      try {
        tinaController.setNoRefresh(true);
        tinaController.initNonlinearVariationCmb();
      }
      finally {
        tinaController.setNoRefresh(oldNoRefresh);
      }
      //      tinaController.transformationChanged(false);
      refreshTitle();
    }
    catch (Exception ex) {
      tinaController.errorHandler.handleError(ex);
    }
  }

  private JWFNumberField swfAnimatorFramesPerSecondREd;
  private JWFNumberField swfAnimatorFrameREd;
  private JPanel variationsPanel;

  /**
   * This method initializes renderBatchJobsScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  private JScrollPane getVariationsScrollPane() {
    if (variationsScrollPane == null) {
      variationsScrollPane = new JScrollPane();
      variationsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
      variationsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
    return variationsScrollPane;
  }

  public JWFNumberField getSwfAnimatorFramesPerSecondREd() {
    return swfAnimatorFramesPerSecondREd;
  }

  public JWFNumberField getSwfAnimatorFrameREd() {
    return swfAnimatorFrameREd;
  }

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }
}
