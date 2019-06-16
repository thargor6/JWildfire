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

import org.jwildfire.create.tina.base.WeightingFieldInputType;
import org.jwildfire.create.tina.base.WeightingFieldType;
import org.jwildfire.create.tina.base.XForm;

public abstract class WeightingFieldControlsUpdater {
  protected final TinaController controller;
  protected final TinaWeightingFieldControllerData controls;

  protected WeightingFieldControlsUpdater(TinaController controller, TinaWeightingFieldControllerData controls) {
    this.controller = controller;
    this.controls = controls;
  }

  public void clearComponents() {
    controls.weightingFieldTypeCmb.setSelectedIndex(-1);
    controls.weightingFieldInputCmb.setSelectedIndex(-1);
    controls.weightingFieldColorIntensityREd.setValue(0.0);
    controls.weightingFieldVariationIntensityREd.setValue(0.0);
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
  }

  public abstract void weightingFieldColorMapFilenameBtn_clicked();

  public abstract void updateControls(XForm xform);

  public abstract void enableControls(XForm xform, boolean enabled);

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

}
