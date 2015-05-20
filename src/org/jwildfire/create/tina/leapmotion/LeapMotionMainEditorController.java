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
  private final JWFNumberField leapMotionIndexField;
  private final JWFNumberField leapMotionInvScaleField;
  private final JWFNumberField leapMotionOffsetField;
  private final JButton leapMotionAddButton;
  private final JButton leapMotionDuplicateButton;
  private final JButton leapMotionDeleteButton;
  private final JButton leapMotionClearButton;
  private boolean gridRefreshing;
  private boolean cmbRefreshing;

  public LeapMotionMainEditorController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel,
      JWFNumberField pFlameFPSField, JToggleButton pLeapMotionToggleButton, JTable pLeapMotionConfigTable, JComboBox pLeapMotionHandCmb,
      JComboBox pLeapMotionInputChannelCmb, JComboBox pLeapMotionOutputChannelCmb, JWFNumberField pLeapMotionIndexField,
      JWFNumberField pLeapMotionInvScaleField, JWFNumberField pLeapMotionOffsetField, JButton pLeapMotionAddButton,
      JButton pLeapMotionDuplicateButton, JButton pLeapMotionDeleteButton, JButton pLeapMotionClearButton) {
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
      leapMotionIndexField = pLeapMotionIndexField;
      leapMotionInvScaleField = pLeapMotionInvScaleField;
      leapMotionOffsetField = pLeapMotionOffsetField;
      leapMotionAddButton = pLeapMotionAddButton;
      leapMotionDuplicateButton = pLeapMotionDuplicateButton;
      leapMotionDeleteButton = pLeapMotionDeleteButton;
      leapMotionClearButton = pLeapMotionClearButton;

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

  public void leapMotionIndexField_changed() {
    value_changed(new PropertySetter() {
      @Override
      public void doSet(LeapMotionConnectedProperty pProperty) {
        pProperty.setIndex(leapMotionIndexField.getIntValue());
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
        pProperty.setOffset(leapMotionOffsetField.getDoubleValue());
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
    LeapMotionConnectedProperty newProperty = new LeapMotionConnectedProperty(src.getLeapMotionHand(), src.getInputChannel(), src.getOutputChannel(), src.getIndex(), src.getOffset(), src.getInvScale());
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
    final int COL_HAND = 0;
    final int COL_INPUT_CHANNEL = 1;
    final int COL_OUTPUT_CHANNEL = 2;
    final int COL_INDEX = 3;
    final int COL_INVSCALE = 4;
    final int COL_OFFSET = 5;
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
          case COL_OFFSET:
            return "Bias";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        LeapMotionConnectedProperty property = rowIndex >= 0 && rowIndex < config.getProperties().size() ? config.getProperties().get(rowIndex) : null;
        if (property != null) {
          switch (columnIndex) {
            case COL_HAND:
              return property.getLeapMotionHand();
            case COL_INPUT_CHANNEL:
              return property.getInputChannel();
            case COL_OUTPUT_CHANNEL:
              return property.getOutputChannel();
            case COL_INDEX:
              return Integer.valueOf(property.getIndex());
            case COL_INVSCALE:
              return Tools.doubleToString(property.getInvScale());
            case COL_OFFSET:
              return Tools.doubleToString(property.getOffset());
          }
        }
        return null;
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
    // TODO
    leapMotionToggleButton.setEnabled(true);
    //
    leapMotionHandCmb.setEnabled(property != null);
    leapMotionInputChannelCmb.setEnabled(property != null);
    leapMotionOutputChannelCmb.setEnabled(property != null);
    leapMotionIndexField.setEnabled(property != null);
    leapMotionInvScaleField.setEnabled(property != null);
    leapMotionOffsetField.setEnabled(property != null);
    leapMotionAddButton.setEnabled(true);
    leapMotionDuplicateButton.setEnabled(property != null);
    leapMotionDeleteButton.setEnabled(property != null);
    leapMotionClearButton.setEnabled(config.getProperties().size() > 0);
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
        leapMotionIndexField.setValue(property.getIndex());
        leapMotionInvScaleField.setValue(property.getInvScale());
        leapMotionOffsetField.setValue(property.getOffset());
      }
      else {
        leapMotionHandCmb.setSelectedItem(null);
        leapMotionInputChannelCmb.setSelectedItem(null);
        leapMotionOutputChannelCmb.setSelectedItem(null);
        leapMotionIndexField.setValue(0.0);
        leapMotionInvScaleField.setValue(1.0);
        leapMotionOffsetField.setValue(0.0);
      }
      enableControls();
    }
    finally {
      cmbRefreshing = oldCmbRefreshing;
    }
  }
}
