/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2020 Andreas Maschke

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

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFuncList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class TinaNonlinearControlsRow {
  private TinaController tinaController;
  private final int index;
  private final JPanel rootPanel;
  private final JLabel nonlinearVarLbl;
  private final JComboBox nonlinearVarCmb;
  private final JComboBox nonlinearParamsCmb;
  private final JWFNumberField nonlinearVarREd;
  private final JWFNumberField nonlinearParamsREd;
  private final JButton nonlinearParamsLeftButton;
  private final JToggleButton nonlinearParamsPreButton;
  private final JToggleButton nonlinearParamsPostButton;
  private final JButton nonlinearParamsUpButton;
  private final JToggleButton toggleParamsPnlButton;
  private final List<JComponent> paramsPnlComponents = new ArrayList<>();
  private final Map<String, JWFNumberField> paramsPnlNumberFields = new HashMap<>();
  private final Map<String, JSlider> paramsPnlSliders = new HashMap<>();
  private final int rootPnlBaseWidth, rootPnlBaseHeight;
  private int extraPanelSize;
  private boolean noRefresh;

  public TinaNonlinearControlsRow(int pIndex, JPanel pRootPanel, JLabel pNonlinearVarLbl, JComboBox pNonlinearVarCmb, JComboBox pNonlinearParamsCmb, JWFNumberField pNonlinearVarREd, JWFNumberField pNonlinearParamsREd,
                                  JButton pNonlinearParamsLeftButton, JToggleButton pNonlinearParamsPreButton, JToggleButton pNonlinearParamsPostButton, JButton pNonlinearParamsUpButton,
                                  JToggleButton pToggleParamsPnlButton) {
    index = pIndex;
    rootPanel = pRootPanel;
    rootPnlBaseWidth = rootPanel.getPreferredSize().width;
    rootPnlBaseHeight = rootPanel.getPreferredSize().height;
    nonlinearVarLbl = pNonlinearVarLbl;
    nonlinearVarCmb = pNonlinearVarCmb;
    nonlinearParamsCmb = pNonlinearParamsCmb;
    nonlinearVarREd = pNonlinearVarREd;
    nonlinearParamsREd = pNonlinearParamsREd;
    nonlinearParamsLeftButton = pNonlinearParamsLeftButton;
    nonlinearParamsPreButton = pNonlinearParamsPreButton;
    nonlinearParamsPostButton = pNonlinearParamsPostButton;
    nonlinearParamsUpButton = pNonlinearParamsUpButton;
    toggleParamsPnlButton = pToggleParamsPnlButton;
  }

  public void initControls() {
    initVariationCmb();
    nonlinearVarCmb.setSelectedIndex(-1);

    nonlinearParamsCmb.removeAllItems();
    nonlinearParamsCmb.setSelectedIndex(-1);

    nonlinearParamsPreButton.setSelected(false);
    nonlinearParamsPostButton.setSelected(false);
  }

  public void initVariationCmb() {
    nonlinearVarCmb.removeAllItems();
    List<String> nameList = new ArrayList<String>();
    nameList.addAll(VariationFuncList.getNameList());
    Collections.sort(nameList);

    List<String> excludedNames = VariationFuncList.getExcludedNameList();

    nonlinearVarCmb.addItem("");
    for (String name : nameList) {
      if (!excludedNames.contains(name)) {
        nonlinearVarCmb.addItem(name);
      }
    }
  }

  public JComboBox getNonlinearVarCmb() {
    return nonlinearVarCmb;
  }

  public JComboBox getNonlinearParamsCmb() {
    return nonlinearParamsCmb;
  }

  public JWFNumberField getNonlinearVarREd() {
    return nonlinearVarREd;
  }

  public JWFNumberField getNonlinearParamsREd() {
    return nonlinearParamsREd;
  }

  public JButton getNonlinearParamsLeftButton() {
    return nonlinearParamsLeftButton;
  }

  public JToggleButton getNonlinearParamsPreButton() {
    return nonlinearParamsPreButton;
  }

  public JToggleButton getNonlinearParamsPostButton() {
    return nonlinearParamsPostButton;
  }

  public JButton getNonlinearParamsUpButton() {
    return nonlinearParamsUpButton;
  }

  public JToggleButton getToggleParamsPnlButton() {
    return toggleParamsPnlButton;
  }

  private void removeParamsPnlComponents() {
    for (JComponent comp : paramsPnlComponents) {
      try {
        rootPanel.remove(comp);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    paramsPnlComponents.clear();
    paramsPnlNumberFields.clear();
    paramsPnlSliders.clear();
  }

  private void refreshView() {
    rootPanel.invalidate();
    rootPanel.validate();
    rootPanel.repaint();

    rootPanel.getParent().invalidate();
    rootPanel.getParent().validate();
    rootPanel.getParent().repaint();

    rootPanel.getParent().getParent().invalidate();
    rootPanel.getParent().getParent().validate();
    rootPanel.getParent().getParent().repaint();
  }

  public void rebuildParamsPnl(int pIdx, XForm pXForm, Variation pVar) {
    removeParamsPnlComponents();
    resizeRootPnl(rootPnlBaseHeight);
    if (toggleParamsPnlButton.isSelected() && pXForm != null && pVar != null) {
      createParamsPnlComponents(pIdx, pXForm, pVar);
    }
    refreshView();
  }

  private final int ROW_HEIGHT = 22;
  private final int LBL_WIDTH = 72;
  private final int LBL_H_OFFSET = 12;
  private final int LBL_V_OFFSET = -2;

  private final int NUMBERFIELD_WIDTH = 72;
  private final int NUMBERFIELD_H_OFFSET = 92;
  private final int NUMBERFIELD_V_OFFSET = -2;

  private final int SLIDER_WIDTH = 112;
  private final int SLIDER_H_OFFSET = 170;
  private final int SLIDER_V_OFFSET = 0;

  private final int SLIDER_SCALE = 1000;

  private void createParamsPnlComponents(int pIdx, XForm pXForm, Variation pVar) {
    if (pXForm != null && pVar != null) {
      String[] paramNames = pVar.getFunc().getParameterNames();
      String[] resNames = pVar.getFunc().getRessourceNames();
      if (paramNames != null && (paramNames.length > 1 || resNames != null && resNames.length > 0 && paramNames.length > 0)) {
        int pnlHeight = rootPnlBaseHeight;
        Object[] paramValues = pVar.getFunc().getParameterValues();
        for (int i = 0; i < paramNames.length; i++) {
          addLabel(pIdx, paramNames[i], pnlHeight);
          addNumberField(paramNames[i], paramValues[i], pnlHeight);
          addSlider(paramNames[i], paramValues[i], pnlHeight);
          pnlHeight += ROW_HEIGHT;
        }
        pnlHeight += 2;
        resizeRootPnl(pnlHeight);
      }
    }
  }

  private void resizeRootPnl(int pnlHeight) {
    rootPanel.setSize(rootPnlBaseWidth, pnlHeight);
    rootPanel.setPreferredSize(new Dimension(rootPnlBaseWidth, pnlHeight));
    extraPanelSize = pnlHeight - rootPnlBaseHeight;
  }

  private void addLabel(final int pIdx, final String pName, int pOffset) {
    JLabel lbl = new JLabel();
    lbl.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          getTinaController().getNonlinearControls().nonlinearResetVarParam(pIdx, pName);
        }
      }
    });
    lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    lbl.setText(pName);
    if (lbl.getPreferredSize().getWidth() > LBL_WIDTH) lbl.setToolTipText(pName);
    lbl.setSize(new Dimension(LBL_WIDTH, 22));
    lbl.setLocation(new Point(LBL_H_OFFSET, LBL_V_OFFSET + pOffset));
    lbl.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    rootPanel.add(lbl, null);
    paramsPnlComponents.add(lbl);
  }

  private TinaController getTinaController() {
    return tinaController;
  }

  private void addNumberField(final String pParamName, Object pParamValue, int pOffset) {
    final JWFNumberField field = new JWFNumberField();
    field.setValueStep(0.01);
    field.setPreferredSize(new Dimension(56, 24));
    field.setText("");
    field.setSize(new Dimension(NUMBERFIELD_WIDTH, 24));
    field.setLocation(new Point(NUMBERFIELD_H_OFFSET, NUMBERFIELD_V_OFFSET + pOffset));
    field.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    if (pParamValue != null) {
      if (pParamValue instanceof Integer) {
        field.setOnlyIntegers(true);
        field.setValue((Integer) pParamValue);
      } else {
        field.setValue((Double) pParamValue);
      }
    }
    rootPanel.add(field, null);
    paramsPnlComponents.add(field);
    paramsPnlNumberFields.put(pParamName, field);

    field.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (tinaController != null && !isNoRefresh()) {
          if (!field.isMouseAdjusting() || field.getMouseChangeCount() == 0) {
            tinaController.saveUndoPoint();
          }
          double doubleVal = 0.0;
          Object fieldVal = field.getValue();
          if (fieldVal != null) {
            if (fieldVal instanceof Integer) {
              doubleVal = (Integer) fieldVal;
            } else {
              doubleVal = (Double) fieldVal;
            }
          }
          tinaController.getNonlinearControls().propertyPnlValueChanged(index, pParamName, doubleVal, field.isMouseAdjusting());
          refreshSliderWithoutRefresh(pParamName, doubleVal);
          refreshNonlinearParamsCmbWithoutRefresh(pParamName, doubleVal);
        }
      }
    });

  }

  private void refreshSliderWithoutRefresh(String pParamName, double pValue) {
    boolean oldNoRefresh = isNoRefresh();
    setNoRefresh(true);
    try {
      JSlider slider = paramsPnlSliders.get(pParamName);
      if (slider != null) {
        int val = Tools.FTOI(pValue * SLIDER_SCALE);
        refreshSliderLimits(slider, val);
        slider.setValue(val);
      }
    } finally {
      setNoRefresh(oldNoRefresh);
    }
  }

  private void refreshNumberFieldWithoutRefresh(String pParamName, double pValue) {
    boolean oldNoRefresh = isNoRefresh();
    setNoRefresh(true);
    try {
      JWFNumberField field = paramsPnlNumberFields.get(pParamName);
      if (field != null) {
        if (field.isOnlyIntegers()) {
          field.setValue(Tools.FTOI(pValue));
        } else {
          field.setValue(pValue);
        }
      }
    } finally {
      setNoRefresh(oldNoRefresh);
    }
  }

  private void refreshNonlinearParamsCmbWithoutRefresh(String pParamName, double pValue) {
    if (nonlinearParamsCmb.getSelectedItem() != null && pParamName.equals(nonlinearParamsCmb.getSelectedItem())) {
      boolean oldNoRefresh = isNoRefresh();
      setNoRefresh(true);
      try {
        boolean oldCtrlRefresh = tinaController.cmbRefreshing;
        tinaController.cmbRefreshing = true;
        try {
          if (nonlinearParamsREd.isOnlyIntegers()) {
            nonlinearParamsREd.setValue(Tools.FTOI(pValue));
          } else {
            nonlinearParamsREd.setValue(pValue);
          }
        } finally {
          tinaController.cmbRefreshing = oldCtrlRefresh;
        }
      } finally {
        setNoRefresh(oldNoRefresh);
      }
    }
  }

  private void addSlider(final String pParamName, final Object pParamValue, int pOffset) {
    final JSlider slider = new JSlider();
    slider.setSize(new Dimension(SLIDER_WIDTH, 19));
    slider.setLocation(new Point(SLIDER_H_OFFSET, SLIDER_V_OFFSET + pOffset));

    double min = -5.0;
    double max = 5.0;
    double val = 0.0;
    if (pParamValue != null) {
      if (pParamValue instanceof Integer) {
        Integer iVal = (Integer) pParamValue;
        val = iVal;
        min = val - 5;
        max = val + 5;
      } else {
        val = (Double) pParamValue;
        if (val < min + 1.0) {
          min = Tools.FTOI(val - 1.0);
        }
        if (val > max - 1.0) {
          max = Tools.FTOI(val + 1.0);
        }
      }
    }

    slider.setMinimum(Tools.FTOI(SLIDER_SCALE * min));
    slider.setMaximum(Tools.FTOI(SLIDER_SCALE * max));
    slider.setValue(Tools.FTOI(SLIDER_SCALE * val));

    slider.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        tinaController.saveUndoPoint();
      }
    });
    slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent e) {
        if (!isNoRefresh()) {
          double sliderVal = (double) slider.getValue() / (double) SLIDER_SCALE;
          tinaController.getNonlinearControls().propertyPnlValueChanged(index, pParamName, sliderVal, slider.getValueIsAdjusting());
          refreshNumberFieldWithoutRefresh(pParamName, sliderVal);
          refreshNonlinearParamsCmbWithoutRefresh(pParamName, sliderVal);
        }
      }
    });
    rootPanel.add(slider, null);
    paramsPnlComponents.add(slider);
    paramsPnlSliders.put(pParamName, slider);
  }

  public void refreshParamWithoutRefresh(String pParamName, double pValue) {
    JWFNumberField field = paramsPnlNumberFields.get(pParamName);
    JSlider slider = paramsPnlSliders.get(pParamName);
    if (field != null || slider != null) {
      boolean oldNoRefresh = isNoRefresh();
      setNoRefresh(true);
      try {
        if (field != null) {
          if (field.isOnlyIntegers()) {
            field.setValue(Tools.FTOI(pValue));
          } else {
            field.setValue(pValue);
          }
        }
        if (slider != null) {
          int val = Tools.FTOI(pValue * SLIDER_WIDTH);
          refreshSliderLimits(slider, val);
          slider.setValue(val);
        }
      } finally {
        setNoRefresh(oldNoRefresh);
      }
    }
  }

  private void refreshSliderLimits(JSlider pSlider, int pValue) {
    if (pValue < pSlider.getMinimum() + 10) {
      pSlider.setMinimum(pValue - 100);
    } else if (pValue > pSlider.getMaximum() - 10) {
      pSlider.setMaximum(pValue + 100);
    }
  }

  public void setTinaController(TinaController pTinaController) {
    tinaController = pTinaController;
  }

  public int getExtraPanelSize() {
    return extraPanelSize;
  }

  public JPanel getRootPanel() {
    return rootPanel;
  }

  public boolean isNoRefresh() {
    return noRefresh;
  }

  public void setNoRefresh(boolean pNoRefresh) {
    noRefresh = pNoRefresh;
  }

  public JLabel getNonlinearVarLbl() {
    return nonlinearVarLbl;
  }

}
