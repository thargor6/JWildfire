/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2019 Andreas Maschke

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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldInputType;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldVariationBlackList;
import org.jwildfire.create.tina.variation.Variation;

import java.util.HashSet;
import java.util.Set;
import javax.swing.*;


public abstract class WeightingFieldControlsUpdater {
  public static final String VAR_NAME_VAR_PARAM_SEPARATOR = ".";


  protected final TinaController controller;
  protected final TinaWeightingFieldControllerData controls;

  protected WeightingFieldControlsUpdater(TinaController controller, TinaWeightingFieldControllerData controls) {
    this.controller = controller;
    this.controls = controls;
  }

  public void clearComponents() {
    controls.weightingFieldTypeCmb.setSelectedIndex(-1);
    controls.weightingFieldTypeCmb.setEnabled(false);
    controls.weightingFieldInputCmb.setSelectedIndex(-1);
    controls.weightingFieldInputCmb.setEnabled(false);
    controls.weightingFieldColorIntensityREd.setValue(0.0);
    controls.weightingFieldColorIntensityREd.setEnabled(false);
    controls.weightingFieldVariationIntensityREd.setValue(0.0);
    controls.weightingFieldVariationIntensityREd.setEnabled(false);
    controls.weightingFieldVarParam1AmountREd.setValue(0.0);
    controls.weightingFieldVarParam1AmountREd.setEnabled(false);
    controls.weightingFieldVarParam2AmountREd.setValue(0.0);
    controls.weightingFieldVarParam2AmountREd.setEnabled(false);
    controls.weightingFieldVarParam3AmountREd.setValue(0.0);
    controls.weightingFieldVarParam3AmountREd.setEnabled(false);
    controls.weightingFieldVarParam1NameCmb.setSelectedIndex(-1);
    controls.weightingFieldVarParam1NameCmb.setEnabled(false);
    controls.weightingFieldVarParam2NameCmb.setSelectedIndex(-1);
    controls.weightingFieldVarParam2NameCmb.setEnabled(false);
    controls.weightingFieldVarParam3NameCmb.setSelectedIndex(-1);
    controls.weightingFieldVarParam3NameCmb.setEnabled(false);
    controls.weightingFieldColorMapFilenameLbl.setVisible(false);
    controls.weightingFieldColorMapFilenameBtn.setVisible(false);
    controls.weightingFieldColorMapFilenameInfoLbl.setVisible(false);
    controls.weightingFieldParam01REd.setVisible(false);
    controls.weightingFieldParam01Lbl.setVisible(false);
    controls.weightingFieldParam02REd.setVisible(false);
    controls.weightingFieldParam02Lbl.setVisible(false);
    controls.weightingFieldParam03REd.setVisible(false);
    controls.weightingFieldParam03Lbl.setVisible(false);
    controls.weightingFieldParam04Cmb.setVisible(false);
    controls.weightingFieldParam04Lbl.setVisible(false);
    controls.weightingFieldParam05REd.setVisible(false);
    controls.weightingFieldParam05Lbl.setVisible(false);
    controls.weightingFieldParam06REd.setVisible(false);
    controls.weightingFieldParam06Lbl.setVisible(false);
    controls.weightingFieldParam07REd.setVisible(false);
    controls.weightingFieldParam07Lbl.setVisible(false);
    controls.weightingFieldParam08Cmb.setVisible(false);
    controls.weightingFieldParam08Lbl.setVisible(false);
    clearFieldPreviewImage();
  }

  public void updateControls(XForm xform) {
    controls.weightingFieldTypeCmb.setSelectedItem(xform.getWeightingFieldType());
    controls.weightingFieldInputCmb.setSelectedItem(xform.getWeightingFieldInput());
    controls.weightingFieldColorIntensityREd.setText(Tools.doubleToString(xform.getWeightingFieldColorIntensity()));
    controls.weightingFieldVariationIntensityREd.setText(Tools.doubleToString(xform.getWeightingFieldVarAmountIntensity()));

    controls.weightingFieldVarParam1AmountREd.setText(Tools.doubleToString(xform.getWeightingFieldVarParam1Intensity()));
    controls.weightingFieldVarParam2AmountREd.setText(Tools.doubleToString(xform.getWeightingFieldVarParam2Intensity()));
    controls.weightingFieldVarParam3AmountREd.setText(Tools.doubleToString(xform.getWeightingFieldVarParam3Intensity()));
    fillVarParamCmb(xform, controls.weightingFieldVarParam1NameCmb);
    controls.weightingFieldVarParam1NameCmb.setSelectedItem(encodeVarNameParamName(xform.getWeightingFieldVarParam1VarName(), xform.getWeightingFieldVarParam1ParamName()));
    fillVarParamCmb(xform, controls.weightingFieldVarParam2NameCmb);
    controls.weightingFieldVarParam2NameCmb.setSelectedItem(encodeVarNameParamName(xform.getWeightingFieldVarParam2VarName(), xform.getWeightingFieldVarParam2ParamName()));
    fillVarParamCmb(xform, controls.weightingFieldVarParam3NameCmb);
    controls.weightingFieldVarParam3NameCmb.setSelectedItem(encodeVarNameParamName(xform.getWeightingFieldVarParam3VarName(), xform.getWeightingFieldVarParam3ParamName()));
  }

  public void enableControls(XForm xform, boolean enabled) {
    controls.weightingFieldTypeCmb.setEnabled(enabled);
    WeightingFieldType weightingFieldType = (WeightingFieldType) controls.weightingFieldTypeCmb.getSelectedItem();
    boolean hasWeightingFieldType = weightingFieldType !=null && !WeightingFieldType.NONE.equals(weightingFieldType);
    controls.weightingFieldInputCmb.setEnabled(enabled && hasWeightingFieldType);
    controls.weightingFieldColorIntensityREd.setEnabled(enabled && hasWeightingFieldType);
    controls.weightingFieldVariationIntensityREd.setEnabled(enabled && hasWeightingFieldType);

    controls.weightingFieldVarParam1AmountREd.setEnabled(enabled && hasWeightingFieldType);
    controls.weightingFieldVarParam2AmountREd.setEnabled(enabled && hasWeightingFieldType);
    controls.weightingFieldVarParam3AmountREd.setEnabled(enabled && hasWeightingFieldType);

    fillVarParamCmb(xform, controls.weightingFieldVarParam1NameCmb);
    controls.weightingFieldVarParam1NameCmb.setEnabled(enabled && hasWeightingFieldType);
    fillVarParamCmb(xform, controls.weightingFieldVarParam2NameCmb);
    controls.weightingFieldVarParam2NameCmb.setEnabled(enabled && hasWeightingFieldType);
    fillVarParamCmb(xform, controls.weightingFieldVarParam3NameCmb);
    controls.weightingFieldVarParam3NameCmb.setEnabled(enabled && hasWeightingFieldType);
  }

    private void clearFieldPreviewImage() {
    // TODO
  }

  protected void refreshFieldPreviewImage() {
    // TODO
  }

  public abstract void weightingFieldColorMapFilenameBtn_clicked();

  public abstract void weightingFieldParam01REd_changed();

  public abstract void weightingFieldParam02REd_changed();

  public abstract void weightingFieldParam03REd_changed();

  public abstract void weightingFieldParam04Cmb_changed();

  public abstract void weightingFieldParam05REd_changed();

  public abstract void weightingFieldParam06REd_changed();

  public abstract void weightingFieldParam07REd_changed();

  public abstract void weightingFieldParam08Cmb_changed();

  public void weightingFieldColorIntensityREd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldColorIntensityREd, "weightingFieldColorIntensity", 1.0);
  }

  public void weightingFieldVarAmountIntensityREd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldVariationIntensityREd, "weightingFieldVarAmountIntensity", 1.0);
  }

  public void weightingFieldVarParam1AmountREd_changed() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      String selectedItem = (String)controls.weightingFieldVarParam1NameCmb.getSelectedItem();
      xForm.setWeightingFieldVarParam1VarName(decodeVarName(selectedItem));
      xForm.setWeightingFieldVarParam1ParamName(decodeParamName(selectedItem));
    }
    controller.xFormTextFieldChanged(null, controls.weightingFieldVarParam1AmountREd, "weightingFieldVarParam1Intensity", 1.0);
  }

  public void weightingFieldVarParam2AmountREd_changed() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      String selectedItem = (String)controls.weightingFieldVarParam2NameCmb.getSelectedItem();
      xForm.setWeightingFieldVarParam2VarName(decodeVarName(selectedItem));
      xForm.setWeightingFieldVarParam2ParamName(decodeParamName(selectedItem));
    }
    controller.xFormTextFieldChanged(null, controls.weightingFieldVarParam2AmountREd, "weightingFieldVarParam2Intensity", 1.0);
  }

  public void weightingFieldVarParam3AmountREd_changed() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      String selectedItem = (String)controls.weightingFieldVarParam3NameCmb.getSelectedItem();
      xForm.setWeightingFieldVarParam3VarName(decodeVarName(selectedItem));
      xForm.setWeightingFieldVarParam3ParamName(decodeParamName(selectedItem));
    }
    controller.xFormTextFieldChanged(null, controls.weightingFieldVarParam3AmountREd, "weightingFieldVarParam3Intensity", 1.0);
  }

  public void weightingFieldVarParam1NameCmb_changed() {
    if (controller.gridRefreshing)
      return;
    XForm xForm = controller.getCurrXForm();
    if (xForm != null && controls.weightingFieldVarParam1NameCmb.getSelectedItem() != null) {
      String selectedItem = (String)controls.weightingFieldVarParam1NameCmb.getSelectedItem();
      xForm.setWeightingFieldVarParam1VarName(decodeVarName(selectedItem));
      xForm.setWeightingFieldVarParam1ParamName(decodeParamName(selectedItem));
      controller.xFormControls.enableControls(xForm);
      controller.refreshXFormUI(xForm);
      controller.refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void weightingFieldVarParam2NameCmb_changed() {
    if (controller.gridRefreshing)
      return;
    XForm xForm = controller.getCurrXForm();
    if (xForm != null && controls.weightingFieldVarParam2NameCmb.getSelectedItem() != null) {
      String selectedItem = (String)controls.weightingFieldVarParam2NameCmb.getSelectedItem();
      xForm.setWeightingFieldVarParam2VarName(decodeVarName(selectedItem));
      xForm.setWeightingFieldVarParam2ParamName(decodeParamName(selectedItem));
      controller.xFormControls.enableControls(xForm);
      controller.refreshXFormUI(xForm);
      controller.refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void weightingFieldVarParam3NameCmb_changed() {
    if (controller.gridRefreshing)
      return;
    XForm xForm = controller.getCurrXForm();
    if (xForm != null && controls.weightingFieldVarParam3NameCmb.getSelectedItem() != null) {
      String selectedItem = (String)controls.weightingFieldVarParam3NameCmb.getSelectedItem();
      xForm.setWeightingFieldVarParam3VarName(decodeVarName(selectedItem));
      xForm.setWeightingFieldVarParam3ParamName(decodeParamName(selectedItem));
      controller.xFormControls.enableControls(xForm);
      controller.refreshXFormUI(xForm);
      controller.refreshFlameImage(true, false, 1, true, false);
    }
  }

  private String decodeVarName(String selectedItem) {
    if(selectedItem!=null && selectedItem.length()>0) {
      return selectedItem.substring(0, selectedItem.indexOf(VAR_NAME_VAR_PARAM_SEPARATOR));
    }
    else {
      return "";
    }
  }

  private String decodeParamName(String selectedItem) {
    if(selectedItem!=null && selectedItem.length()>0) {
      return selectedItem.substring(selectedItem.indexOf(VAR_NAME_VAR_PARAM_SEPARATOR)+1, selectedItem.length());
    }
    else {
      return "";
    }
  }

  public static String encodeVarNameParamName(String varName, String paramName) {
    if(varName!=null && varName.length()>0 && paramName!=null && paramName.length()>0 && !WeightingFieldVariationBlackList.isBlackListed(varName)) {
      return varName + VAR_NAME_VAR_PARAM_SEPARATOR + paramName;
    }
    else {
      return "";
    }
  }

  public void weightingFieldTypeCmb_changed() {
    if (controller.gridRefreshing)
      return;
    XForm xForm = controller.getCurrXForm();
    if (xForm != null && controls.weightingFieldTypeCmb.getSelectedItem() != null) {
      xForm.setWeightingFieldType((WeightingFieldType) controls.weightingFieldTypeCmb.getSelectedItem());
      controller.xFormControls.enableControls(xForm);
      controller.refreshXFormUI(xForm);
      controller.refreshFlameImage(true, false, 1, true, false);
    }
  }

  public void weightingFieldInputCmb_changed() {
    if (controller.gridRefreshing)
      return;
    XForm xForm = controller.getCurrXForm();
    if (xForm != null && controls.weightingFieldInputCmb.getSelectedItem() != null) {
      xForm.setWeightingFieldInput((WeightingFieldInputType) controls.weightingFieldInputCmb.getSelectedItem());
      controller.refreshFlameImage(true, false, 1, true, false);
    }
  }

  protected void fillVarParamCmb(XForm xform, JComboBox cmb) {
     boolean oldGridRefreshing = controller.gridRefreshing;
     try {
       controller.gridRefreshing = true;
       Set<String> items = new HashSet<>();
       Object selectedItem = cmb.getSelectedItem();

       cmb.removeAllItems();
       cmb.addItem("");
       if(xform!=null) {
         for (Variation var : xform.getVariations()) {
           for (String paramName : var.getFunc().getParameterNames()) {
             String entry = encodeVarNameParamName(var.getFunc().getName(), paramName);
             if (!items.contains(entry)) {
               cmb.addItem(entry);
               items.add(entry);
             }
           }
         }
       }
       if(selectedItem!=null) {
         cmb.setSelectedItem(selectedItem);
       }
     }
     finally {
       controller.gridRefreshing = oldGridRefreshing;
     }
  }
}
