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
import org.jwildfire.create.tina.variation.VariationFuncType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
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
    VariationProfileRepository.updateVariationProfiles(this.currProfiles);
    this.currProfiles = null;
    int row = profilesTable.getSelectedRow();
    refreshProfilesTable();
    if(row>=0) {
      try {
        profilesTable.getSelectionModel().setSelectionInterval(row, row);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    profilesTableClicked();
  }

  public void profileNameChanged() {
    if(!noRefresh && getCurrProfile()!=null) {
      getCurrProfile().setName(profileNameEdit.getText());
    }
  }

  public void profileTypeCmbChanged() {
    if(!noRefresh && getCurrProfile()!=null) {
      getCurrProfile().setVariationProfileType((VariationProfileType) profileTypeCmb.getSelectedItem());
      refreshVariationsPanel();
      refreshStatusText();
    }
  }

  private void refreshStatusText() {
    profileStatusEdit.setText(getStatusText());
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
      refreshStatusText();
      defaultCheckbox.setSelected(profile.isDefaultProfile());
    } else {
      profileNameEdit.setText("");
      profileTypeCmb.setSelectedIndex(-1);
      profileStatusEdit.setText("");
      defaultCheckbox.setSelected(false);
    }
    refreshVariationsPanel();
    enableControls();
  }

  private String getVariationCaption(int count) {
    return count <= 1 ? "variation" : "variations";
  }

  private String getVariationTypeCaption(int count) {
    return count <= 1 ? "variation type" : "variation types";
  }

  private String getStatusText() {
    VariationProfile profile = getCurrProfile();
    if(profile!=null && profile.getVariationProfileType()!=null) {
       int selected, inverse;
       String excludedMsgFormat = "%d %s excluded / %d %s active";
       String includedMsgFormat = "%d %s included / %d %s inactive";
       switch(profile.getVariationProfileType()) {
         case EXCLUDE_TYPES:
           selected = profile.getVariationTypes().size();
           inverse = VariationFuncType.values().length - profile.getVariationTypes().size();
           return String.format( excludedMsgFormat, selected, getVariationTypeCaption(selected), inverse, getVariationTypeCaption(inverse));
         case INCLUDE_TYPES:
           selected = profile.getVariationTypes().size();
           inverse = VariationFuncType.values().length - profile.getVariationTypes().size();
           return String.format( includedMsgFormat, selected, getVariationTypeCaption(selected), inverse, getVariationTypeCaption(inverse));
         case EXCLUDE_VARIATIONS:
           selected = profile.getVariations().size();
           inverse = VariationFuncList.getNameList().size() - profile.getVariations().size();
           return String.format( excludedMsgFormat, selected, getVariationCaption(selected), inverse, getVariationCaption(inverse));
         case INCLUDE_VARIATIONS:
           selected = profile.getVariations().size();
           inverse = VariationFuncList.getNameList().size() - profile.getVariations().size();
           return String.format( includedMsgFormat, selected, getVariationCaption(selected), inverse, getVariationCaption(inverse));
         default:
           return "";
       }
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

    VariationProfile profile = getCurrProfile();
    if (profile != null && profile.getVariationProfileType()!=null) {
      variationsPanel.setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();

      List<String> currNames;
      switch(profile.getVariationProfileType()) {
        case EXCLUDE_TYPES:
        case INCLUDE_TYPES:
          currNames = Arrays.asList(VariationFuncType.values()).stream().map(VariationFuncType::getCaption).collect(Collectors.toList());
          break;
        case EXCLUDE_VARIATIONS:
        case INCLUDE_VARIATIONS:
          currNames = VariationFuncList.getNameList();
          break;
        default:
          currNames = new ArrayList<>();
          break;
      }
      Collections.sort(currNames);

      int colCount = 4;
      int currRow = 0;
      int idx = 0;
      while(idx < currNames.size()) {
        for (int currCol = 0; currCol < colCount; currCol++) {
          if (idx < currNames.size()) {
            constraints.gridx = currCol;
            constraints.gridy = currRow;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            String currName = currNames.get(idx++);
            JCheckBox cbx = new JCheckBox(currName);
            switch (profile.getVariationProfileType()) {
              case EXCLUDE_TYPES:
              case INCLUDE_TYPES:
                cbx.setSelected( profile.getVariationTypes().contains(getVariationTypeByCaption(currName) ) );
                break;
              case EXCLUDE_VARIATIONS:
              case INCLUDE_VARIATIONS:
                cbx.setSelected( profile.getVariations().contains(currName));
                break;
            }
            cbx.addItemListener(
                new ItemListener() {
                  public void itemStateChanged(ItemEvent e) {
                    VariationProfile currProfile = getCurrProfile();
                    if (currProfile != null && currProfile.getVariationProfileType() != null) {
                      switch (currProfile.getVariationProfileType()) {
                        case EXCLUDE_TYPES:
                        case INCLUDE_TYPES:
                          if (((JCheckBox) e.getItem()).isSelected()) {
                            currProfile
                                .getVariationTypes()
                                .add(getVariationTypeByCaption(currName));
                          } else {
                            currProfile
                                .getVariationTypes()
                                .remove(getVariationTypeByCaption(currName));
                          }
                          refreshStatusText();
                          break;
                        case EXCLUDE_VARIATIONS:
                        case INCLUDE_VARIATIONS:
                          if (((JCheckBox) e.getItem()).isSelected()) {
                            currProfile.getVariations().add(currName);
                          }
                          else {
                            currProfile.getVariations().remove(currName);
                          }
                          refreshStatusText();
                          break;
                      }
                    }
                  }
                });

            currCheckboxes.add(cbx);
            variationsPanel.add(cbx, constraints);
          }
        }
        currRow++;
      }
    }
    variationsScrollPane.setViewportView(variationsPanel);
    variationsScrollPane.invalidate();
    variationsScrollPane.validate();
  }

  private Map<String, VariationFuncType> variationTypeCaptions = null;

  private VariationFuncType getVariationTypeByCaption(String caption) {
    if(variationTypeCaptions==null) {
      variationTypeCaptions = new HashMap<>();
      Arrays.asList(VariationFuncType.values()).stream().forEach(vt -> variationTypeCaptions.put(vt.getCaption(), vt) );
    }
    return variationTypeCaptions.get(caption);
  }

  public void refreshMainWindow() {
    boolean currNoRefresh = parentController.isNoRefresh();
    parentController.setNoRefresh(true);
    try {
      ComboboxTools.initVariationProfileCmb(parentController.data.tinaVariationProfile1Cmb, true, false);
      ComboboxTools.initVariationProfileCmb(parentController.data.tinaVariationProfile2Cmb, false, true);
    }
    finally {
      parentController.setNoRefresh(currNoRefresh);
    }
    parentController.tinaVariationProfile1Cmb_changed();
  }
}

