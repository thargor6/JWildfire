/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.leapmotion;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.ErrorHandler;

public class LeapMotionMainEditorController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JWFNumberField flameFPSField;
  private final JToggleButton leapMotionToggleButton;
  private final JTable leapMotionConfigTable;
  private final JComboBox leapMotionHandCmb;
  private final JComboBox leapMotionInputChannelCmb;
  private final JComboBox leapMotionOutputChannelCmb;
  private final JWFNumberField leapMotionIndex1Field;
  private final JWFNumberField leapMotionIndex2Field;
  private final JWFNumberField leapMotionIndex3Field;
  private final JWFNumberField leapMotionInvScaleField;
  private final JWFNumberField leapMotionOffsetField;
  private final JButton leapMotionAddButton;
  private final JButton leapMotionDuplicateButton;
  private final JButton leapMotionDeleteButton;
  private final JButton leapMotionClearButton;
  private final JButton leapMotionResetConfigButton;
  private boolean gridRefreshing;
  private boolean cmbRefreshing;
  private boolean leapMotionEnabled;

  public LeapMotionMainEditorController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel,
      JWFNumberField pFlameFPSField, JToggleButton pLeapMotionToggleButton, JTable pLeapMotionConfigTable, JComboBox pLeapMotionHandCmb,
      JComboBox pLeapMotionInputChannelCmb, JComboBox pLeapMotionOutputChannelCmb, JWFNumberField pLeapMotionIndex1Field,
      JWFNumberField pLeapMotionIndex2Field, JWFNumberField pLeapMotionIndex3Field,
      JWFNumberField pLeapMotionInvScaleField, JWFNumberField pLeapMotionOffsetField, JButton pLeapMotionAddButton,
      JButton pLeapMotionDuplicateButton, JButton pLeapMotionDeleteButton, JButton pLeapMotionClearButton,
      JButton pLeapMotionResetConfigButton) {
    gridRefreshing = cmbRefreshing = true;
    try {
      tinaController = pTinaController;
      errorHandler = pErrorHandler;
      prefs = pPrefs;
      rootPanel = pRootPanel;
      leapMotionToggleButton = pLeapMotionToggleButton;
      flameFPSField = pFlameFPSField;
      leapMotionConfigTable = pLeapMotionConfigTable;
      leapMotionHandCmb = pLeapMotionHandCmb;
      leapMotionInputChannelCmb = pLeapMotionInputChannelCmb;
      leapMotionOutputChannelCmb = pLeapMotionOutputChannelCmb;
      leapMotionIndex1Field = pLeapMotionIndex1Field;
      leapMotionIndex2Field = pLeapMotionIndex2Field;
      leapMotionIndex3Field = pLeapMotionIndex3Field;
      leapMotionInvScaleField = pLeapMotionInvScaleField;
      leapMotionOffsetField = pLeapMotionOffsetField;
      leapMotionAddButton = pLeapMotionAddButton;
      leapMotionDuplicateButton = pLeapMotionDuplicateButton;
      leapMotionDeleteButton = pLeapMotionDeleteButton;
      leapMotionClearButton = pLeapMotionClearButton;
      leapMotionResetConfigButton = pLeapMotionResetConfigButton;

      leapMotionHandCmb.removeAllItems();
      for (LeapMotionHand value : LeapMotionHand.values()) {
        leapMotionHandCmb.addItem(value);
      }
      leapMotionInputChannelCmb.removeAllItems();
      for (LeapMotionInputChannel value : LeapMotionInputChannel.values()) {
        leapMotionInputChannelCmb.addItem(value);
      }
      leapMotionOutputChannelCmb.removeAllItems();
      for (LeapMotionOutputChannel value : LeapMotionOutputChannel.values()) {
        leapMotionOutputChannelCmb.addItem(value);
      }

      try {
        String libPath = System.getProperty("java.library.path").trim();
        leapMotionEnabled = libPath.length() > 0 && new File(libPath).exists();
      }
      catch (Exception ex) {
        leapMotionEnabled = false;
        ex.printStackTrace();
      }

      refreshConfigTable();
      leapMotionConfigTable.getSelectionModel().setSelectionInterval(0, 0);
      configTableClicked();
    }
    finally {
      gridRefreshing = cmbRefreshing = false;
    }
  }

  private LeapMotionControllerHolder leapMotionControllerHolder;

  private LeapMotionConnectedProperties config = LeapMotionConnectedProperties.getDefaultConfig();

  public void toggleLeapMotionMode() {
    if (leapMotionToggleButton.isSelected()) {
      if (leapMotionControllerHolder == null) {
        leapMotionControllerHolder = new LeapMotionControllerHolder(tinaController, errorHandler, rootPanel);
      }
      leapMotionControllerHolder.startLeapMotionListener(config, flameFPSField.getIntValue());
    }
    else {
      if (leapMotionControllerHolder != null) {
        leapMotionControllerHolder.stopLeapMotionListener();
      }
    }
  }

  public void leapMotionIndex1Field_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        pProperty.setIndex1(leapMotionIndex1Field.getIntValue());
      }
    });
  }

  public void leapMotionIndex2Field_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        pProperty.setIndex2(leapMotionIndex2Field.getIntValue());
      }
    });
  }

  public void leapMotionIndex3Field_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        pProperty.setIndex3(leapMotionIndex3Field.getIntValue());
      }
    });
  }

  public void leapMotionInvScaleField_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        pProperty.setInvScale(leapMotionInvScaleField.getDoubleValue());
      }
    });
  }

  public void leapMotionOffsetField_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        //        pProperty.setOffset(leapMotionOffsetField.getDoubleValue());
      }
    });
  }

  public void leapMotionOutputChannelCmb_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        pProperty.setOutputChannel((LeapMotionOutputChannel) leapMotionOutputChannelCmb.getSelectedItem());
      }
    });
  }

  public void leapMotionInputChannelCmb_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        pProperty.setInputChannel((LeapMotionInputChannel) leapMotionInputChannelCmb.getSelectedItem());
      }
    });
  }

  interface PropertySetter {
    abstract void doSet(LeapMotionConnectedProperty pProperty);
  }

  public void leapMotionHandCmb_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        pProperty.setLeapMotionHand((LeapMotionHand) leapMotionHandCmb.getSelectedItem());
      }
    });
  }

  public void value_changed(PropertySetter pSetter) {
    if (!cmbRefreshing) {
      LeapMotionConnectedProperty property = getCurrProperty();
      if (property != null) {
        pSetter.doSet(property);
        int row = leapMotionConfigTable.getSelectedRow();
        try {
          refreshConfigTable();
          leapMotionConfigTable.getSelectionModel().setSelectionInterval(row, row);
        }
        finally {
          gridRefreshing = false;
        }
      }
    }
  }

  public void resetConfigButton_clicked() {
    config = LeapMotionConnectedProperties.getDefaultConfig();
    try {
      refreshConfigTable();
      leapMotionConfigTable.getSelectionModel().setSelectionInterval(0, 0);
    }
    finally {
      gridRefreshing = false;
    }
    configTableClicked();
  }

  public void leapMotionDeleteButton_clicked() {
    int row = leapMotionConfigTable.getSelectedRow();
    config.getProperties().remove(row);
    gridRefreshing = true;
    try {
      refreshConfigTable();
      leapMotionConfigTable.getSelectionModel().setSelectionInterval(0, 0);
    }
    finally {
      gridRefreshing = false;
    }
    configTableClicked();
  }

  public void leapMotionClearButton_clicked() {
    config.getProperties().clear();
    gridRefreshing = true;
    try {
      refreshConfigTable();
    }
    finally {
      gridRefreshing = false;
    }
    configTableClicked();
  }

  private LeapMotionConnectedProperty getCurrProperty() {
    int row = leapMotionConfigTable.getSelectedRow();
    return row >= 0 && row < config.getProperties().size() ? config.getProperties().get(row) : null;
  }

  public void leapMotionDuplicateButton_clicked() {
    int row = leapMotionConfigTable.getSelectedRow();
    LeapMotionConnectedProperty src = config.getProperties().get(row);
    LeapMotionConnectedProperty newProperty = new LeapMotionConnectedProperty(src.getLeapMotionHand(), src.getInputChannel(),
        src.getOutputChannel(), src.getIndex1(), src.getIndex2(), src.getIndex3(), src.getOffset(), src.getInvScale());
    addProperty(newProperty);
  }

  public void leapMotionAddButton_clicked() {
    addProperty(new LeapMotionConnectedProperty());
  }

  private void addProperty(LeapMotionConnectedProperty pProperty) {
    config.getProperties().add(pProperty);
    int row = config.getProperties().size() - 1;
    gridRefreshing = true;
    try {
      refreshConfigTable();
      leapMotionConfigTable.getSelectionModel().setSelectionInterval(row, row);
    }
    finally {
      gridRefreshing = false;
    }
    configTableClicked();
  }

  private void refreshConfigTable() {
    final int COL_LISTENER_ID = 0;
    final int COL_HAND = 1;
    final int COL_INPUT_CHANNEL = 2;
    final int COL_OUTPUT_CHANNEL = 3;
    final int COL_INDEX = 4;
    final int COL_INVSCALE = 5;
    //    final int COL_OFFSET = 6;
    leapMotionConfigTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return config.getProperties().size();
      }

      @Override
      public int getColumnCount() {
        return 6;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_LISTENER_ID:
            return "Id";
          case COL_HAND:
            return "Hand";
          case COL_INPUT_CHANNEL:
            return "Motion-property";
          case COL_OUTPUT_CHANNEL:
            return "Linked Flame-property";
          case COL_INDEX:
            return "Flame-property-index";
          case COL_INVSCALE:
            return "1/Intensity";
            //          case COL_OFFSET:
            //            return "Bias";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        LeapMotionConnectedProperty property = rowIndex >= 0 && rowIndex < config.getProperties().size() ? config.getProperties().get(rowIndex) : null;
        if (property != null) {
          switch (columnIndex) {
            case COL_LISTENER_ID:
              return rowIndex + 1;
            case COL_HAND:
              return property.getLeapMotionHand();
            case COL_INPUT_CHANNEL:
              return property.getInputChannel();
            case COL_OUTPUT_CHANNEL:
              return property.getOutputChannel();
            case COL_INDEX:
              return getIndexAsDisplayString(property);
            case COL_INVSCALE:
              return Tools.doubleToString(property.getInvScale());
              //            case COL_OFFSET:
              //              return Tools.doubleToString(property.getOffset());
          }
        }
        return null;
      }

      private Object getIndexAsDisplayString(LeapMotionConnectedProperty pProperty) {
        switch (pProperty.getIndexCount()) {
          case 3:
            return pProperty.getIndex1() + " / " + pProperty.getIndex2() + " / " + pProperty.getIndex3();
          case 2:
            return pProperty.getIndex1() + " / " + pProperty.getIndex2();
          case 1:
            return pProperty.getIndex1();
          default:
            return null;
        }
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }

    });
    leapMotionConfigTable.getTableHeader().setFont(leapMotionConfigTable.getFont());
  }

  public void enableControls() {
    LeapMotionConnectedProperty property = getCurrProperty();
    leapMotionToggleButton.setEnabled(leapMotionEnabled);
    leapMotionHandCmb.setEnabled(leapMotionEnabled && property != null);
    leapMotionInputChannelCmb.setEnabled(leapMotionEnabled && property != null);
    leapMotionOutputChannelCmb.setEnabled(leapMotionEnabled && property != null);
    leapMotionIndex1Field.setEnabled(leapMotionEnabled && property != null && property.getIndexCount() > 0);
    leapMotionIndex2Field.setEnabled(leapMotionEnabled && property != null && property.getIndexCount() > 1);
    leapMotionIndex3Field.setEnabled(leapMotionEnabled && property != null && property.getIndexCount() > 2);
    leapMotionInvScaleField.setEnabled(leapMotionEnabled && property != null);
    //    leapMotionOffsetField.setEnabled(leapMotionEnabled && property != null);
    leapMotionAddButton.setEnabled(leapMotionEnabled);
    leapMotionDuplicateButton.setEnabled(leapMotionEnabled && property != null);
    leapMotionDeleteButton.setEnabled(leapMotionEnabled && property != null);
    leapMotionClearButton.setEnabled(leapMotionEnabled && config.getProperties().size() > 0);
    leapMotionResetConfigButton.setEnabled(leapMotionEnabled);
    leapMotionConfigTable.setEnabled(leapMotionEnabled);
  }

  public void configTableClicked() {
    boolean oldCmbRefreshing = cmbRefreshing;
    gridRefreshing = cmbRefreshing = true;
    try {
      LeapMotionConnectedProperty property = getCurrProperty();
      if (property != null) {
        leapMotionHandCmb.setSelectedItem(property.getLeapMotionHand());
        leapMotionInputChannelCmb.setSelectedItem(property.getInputChannel());
        leapMotionOutputChannelCmb.setSelectedItem(property.getOutputChannel());
        leapMotionIndex1Field.setValue(property.getIndex1());
        leapMotionIndex2Field.setValue(property.getIndex2());
        leapMotionIndex3Field.setValue(property.getIndex3());
        leapMotionInvScaleField.setValue(property.getInvScale());
        //        leapMotionOffsetField.setValue(property.getOffset());
      }
      else {
        leapMotionHandCmb.setSelectedItem(null);
        leapMotionInputChannelCmb.setSelectedItem(null);
        leapMotionOutputChannelCmb.setSelectedItem(null);
        leapMotionIndex1Field.setValue(0.0);
        leapMotionIndex2Field.setValue(0.0);
        leapMotionIndex3Field.setValue(0.0);
        leapMotionInvScaleField.setValue(1.0);
        //        leapMotionOffsetField.setValue(0.0);
      }
      enableControls();
    }
    finally {
      cmbRefreshing = oldCmbRefreshing;
    }
  }
}
