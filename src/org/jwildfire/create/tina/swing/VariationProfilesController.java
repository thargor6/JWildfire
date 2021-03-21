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
import org.jwildfire.base.VariationProfileType;
import org.jwildfire.create.tina.variation.VariationFuncList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
  private JScrollPane variationsScrollPane;

  private List<VariationProfile> currProfiles = null;
  private boolean noRefresh;

  private JPanel variationsPanel;
  private List<String> currVariationNames = null;
  private List<JCheckBox> currCheckboxes = new ArrayList<>();

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
      JCheckBox defaultCheckbox,
      JScrollPane variationsScrollPane) {
    this.newProfileBtn = newProfileBtn;
    this.duplicateProfileBtn = duplicateProfileBtn;
    this.deleteProfileBtn = deleteProfileBtn;
    this.profilesTable = profilesTable;
    this.profileNameEdit = profileNameEdit;
    this.profileTypeCmb = profileTypeCmb;
    this.profileStatusEdit = profileStatusEdit;
    this.defaultCheckbox = defaultCheckbox;
    this.profileTypeCmb = profileTypeCmb;
    this.variationsScrollPane = variationsScrollPane;
    refreshProfilesCmb();
    refreshProfilesTable();
    profilesTableClicked();
  }

  private void refreshProfilesCmb() {
    boolean oldNoRefresh = noRefresh;
    try {
      noRefresh = true;
      profileTypeCmb.removeAllItems();
      profileTypeCmb.addItem(VariationProfileType.EXCLUDE_TYPES);
      profileTypeCmb.addItem(VariationProfileType.INCLUDE_TYPES);
      profileTypeCmb.addItem(VariationProfileType.EXCLUDE_VARIATIONS);
      profileTypeCmb.addItem(VariationProfileType.INCLUDE_VARIATIONS);
    } finally {
      noRefresh = oldNoRefresh;
    }
  }

  private void refreshProfilesTable() {
    boolean oldNoRefresh = noRefresh;
    try {
      noRefresh = true;
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
    } finally {
      noRefresh = oldNoRefresh;
    }
  }

  public void saveAndApplyChanges() {
    // TODO
  }

  public void profileNameChanged() {
    if(!noRefresh && getCurrProfile()!=null) {
      getCurrProfile().setName(profileNameEdit.getText());
    }
  }

  public void profileTypeCmbChanged() {
    if(!noRefresh && getCurrProfile()!=null) {
      getCurrProfile().setVariationProfileType((VariationProfileType) profileTypeCmb.getSelectedItem());
    }
  }

  public void defaultCheckboxChanged() {
    if(!noRefresh && getCurrProfile()!=null) {
      getCurrProfile().setDefaultProfile(defaultCheckbox.isSelected());
    }
  }

  private VariationProfile getCurrProfile() {
    int selectedIdx = profilesTable.getSelectedRow();
    return selectedIdx>=0 && selectedIdx<getProfiles().size() ? getProfiles().get(selectedIdx) : null;
  }

  public void newProfileClicked() {
    VariationProfile profile = new VariationProfile();
    profile.setVariationProfileType(VariationProfileType.INCLUDE_VARIATIONS);
    profile.setName(getUniqueName("New Profile"));
    getProfiles().add(profile);
    int selectedRow = getProfiles().size() - 1;
    refreshProfilesTable();
    profilesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
  }

  public void duplicateProfileClicked() {
    VariationProfile profile = getCurrProfile().makeCopy();
    profile.setName(getUniqueName(profile.getName()));
    getProfiles().add(profile);
    int selectedRow = getProfiles().size() - 1;
    refreshProfilesTable();
    profilesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
  }

  private String getUniqueName(String baseName) {
    int counter = 0;
    while (true) {
      String name = counter > 0 ? baseName + "-" + counter : baseName;
      counter++;
      if (!getProfiles().stream().filter(p -> name.equals(p.getName())).findAny().isPresent()) {
        return name;
      }
    }
  }

  public void deleteProfileClicked() {
    if(getCurrProfile()!=null && StandardDialogs.confirm(null, "Do you really want to remove this profile?")) {
      int selectedRow = profilesTable.getSelectedRow();
      getProfiles().remove( selectedRow );
      selectedRow = Math.min(selectedRow, getProfiles().size() -1);
      refreshProfilesTable();
      profilesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
    }
  }

  public void profilesTableClicked() {
    VariationProfile profile = getCurrProfile();
    if (profile != null) {
      profileNameEdit.setText(profile.getName());
      profileTypeCmb.setSelectedItem(profile.getVariationProfileType());
      profileStatusEdit.setText(refreshStatusText());
      defaultCheckbox.setSelected(profile.isDefaultProfile());
    } else {
      profileNameEdit.setText("");
      profileTypeCmb.setSelectedIndex(-1);
      profileStatusEdit.setText("");
      defaultCheckbox.setSelected(false);
    }
    enableControls();
  }

  private String refreshStatusText() {
    // TODO remove Array currVariationNames, use profile
    if (currVariationNames != null) {
      int excluded = 0;
      if (currCheckboxes != null) {
        for (JCheckBox cbx : currCheckboxes) {
          if (!cbx.isSelected()) {
            excluded++;
          }
        }
      }
      return (currVariationNames.size() - excluded)
          + "/"
          + currVariationNames.size()
          + " variations active";
    }
    else {
      return "";
    }
  }

  private List<VariationProfile> getProfiles() {
    if (currProfiles == null) {
      currProfiles =
          VariationProfileRepository.getProfiles().stream()
              .map(p -> p.makeCopy())
              .collect(Collectors.toList());
    }
    return currProfiles;
  }

  public void enableControls() {
    int selectedVariationProfileIdx = profilesTable.getSelectedRow();
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
    this.currProfiles = null;
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
    currCheckboxes.clear();
    variationsPanel = new JPanel();

    currVariationNames = VariationFuncList.getNameList();
    Collections.sort(currVariationNames);

    List<String> excludedVariations = new ArrayList<>();

    int xOffset = 8;
    int xSize = 160;
    int yOffset = 8;
    int ySize = 18;
    int yGap = 4;

    int currY = yOffset;
    for (String variation : currVariationNames) {
      JCheckBox cbx = new JCheckBox(variation);
      cbx.setSelected(!excludedVariations.contains(variation));
      cbx.setBounds(xOffset, currY, xSize, ySize);
      currCheckboxes.add(cbx);
      variationsPanel.add(cbx);
      currY += ySize + yGap;
    }
    int width = xSize + 2 * xOffset;
    int height = currY;
    variationsPanel.setBounds(0, 0, width, height);
    variationsPanel.setPreferredSize(new Dimension(width, height));

    variationsScrollPane.setViewportView(variationsPanel);
    variationsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    variationsScrollPane.invalidate();
    variationsScrollPane.validate();
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
