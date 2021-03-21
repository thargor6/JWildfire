/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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

import org.jwildfire.base.VariationProfile;
import org.jwildfire.base.VariationProfileRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VariationProfilesController {
  private final TinaController parentController;
  private JButton newProfileBtn;
  private JButton duplicateProfileBtn;
  private JButton deleteProfileBtn;
  private JTable profilesTable;
  private JTextField profileNameEdit;
  private JComboBox profileTypeCmb;
  private JTextField profileStatusEdit;
  private JCheckBox defaultCheckbox;
  private int selectedVariationProfileIdx = -1;

  public VariationProfilesController(TinaController parentController) {
    this.parentController = parentController;
  }

  public void setControls(
      JButton newProfileBtn,
      JButton duplicateProfileBtn,
      JButton deleteProfileBtn,
      JTable profilesTable,
      JTextField profileNameEdit,
      JComboBox profileTypeCmb,
      JTextField profileStatusEdit,
      JCheckBox defaultCheckbox) {
    this.newProfileBtn = newProfileBtn;
    this.duplicateProfileBtn = duplicateProfileBtn;
    this.deleteProfileBtn = deleteProfileBtn;
    this.profilesTable = profilesTable;
    this.profileNameEdit = profileNameEdit;
    this.profileTypeCmb = profileTypeCmb;
    this.profileStatusEdit = profileStatusEdit;
    this.defaultCheckbox = defaultCheckbox;
    refreshProfilesTable();
    profilesTableClicked();
  }

  private void refreshProfilesTable() {
    profilesTable.setModel(
        new DefaultTableModel() {
          private static final long serialVersionUID = 1L;

          @Override
          public int getRowCount() {
            return getProfiles().size();
          }

          @Override
          public int getColumnCount() {
            return 2;
          }

          @Override
          public String getColumnName(int columnIndex) {
            switch (columnIndex) {
              case 0:
                return "Profile name";
              case 1:
                return "Profile type";
            }
            return null;
          }

          @Override
          public Object getValueAt(int rowIndex, int columnIndex) {
            VariationProfile profile =
                rowIndex < getProfiles().size() ? getProfiles().get(rowIndex) : null;
            if (profile != null) {
              switch (columnIndex) {
                case 0:
                  return profile.getName();
                case 1:
                  return profile.getVariationProfileType() != null
                      ? profile.getVariationProfileType().toString()
                      : "";
              }
            }
            return null;
          }

          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        });
  }

  public void saveAndApplyChanges() {
    // TODO
  }

  public void profileNameChanged() {
    // TODO
  }

  public void profileTypeCmbChanged() {
    // TODO
  }

  public void defaultCheckboxChanged() {
    // TODO
  }

  public void newProfileClicked() {
    // TODO
  }

  public void duplicateProfileClicked() {
    // TODO
  }

  public void deleteProfileClicked() {
    // TODO
  }

  public void profilesTableClicked() {
    selectedVariationProfileIdx = profilesTable.getSelectedRow();
    VariationProfile profile = selectedVariationProfileIdx >= 0 && selectedVariationProfileIdx < getProfiles().size() ? getProfiles().get(selectedVariationProfileIdx) : null;
    if(profile!=null) {
      profileNameEdit.setText(profile.getName());
      profileTypeCmb.setSelectedItem(profile.getVariationProfileType());
      // TODO
      //profileStatusEdit = profileStatusEdit;
      defaultCheckbox.setSelected(profile.isDefaultProfile());
    }
    else {
      profileNameEdit.setText("");
      profileTypeCmb.setSelectedIndex(-1);
      profileStatusEdit.setText("");
      defaultCheckbox.setSelected(false);
    }
    enableControls();
  }

  private List<VariationProfile> getProfiles() {
    return VariationProfileRepository.getProfiles();
  }

  public void enableControls() {
    boolean enabled =
        selectedVariationProfileIdx >= 0 && selectedVariationProfileIdx < getProfiles().size();
    duplicateProfileBtn.setEnabled(enabled);
    deleteProfileBtn.setEnabled(enabled);
    profileNameEdit.setEnabled(enabled);
    profileTypeCmb.setEnabled(enabled);
    defaultCheckbox.setEnabled(enabled);
  }

  public void refreshControls() {
    enableControls();
    refreshProfilesTable();
    profilesTableClicked();
  }

  public void cancelChanges() {
    // TODO
  }
}

/*
   try {
     List<String> excludedVariations = new ArrayList<>();
     if (!checkboxes.isEmpty() && variationNames != null && checkboxes.size() == variationNames.size()) {
       for (int i = 0; i < checkboxes.size(); i++) {
         if (!checkboxes.get(i).isSelected()) {
           excludedVariations.add(variationNames.get(i));
         }
       }
     }
     //Prefs.getPrefs().setTinaExcludedVariations(excludedVariations);
     //Prefs.getPrefs().saveToFile();
     //VariationFuncList.invalidateExcludedNameList();
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

*/
