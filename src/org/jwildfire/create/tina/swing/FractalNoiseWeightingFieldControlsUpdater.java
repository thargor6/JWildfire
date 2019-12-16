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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.weightingfield.FractalType;

public class FractalNoiseWeightingFieldControlsUpdater extends WeightingFieldControlsUpdater {
  private boolean fillingComboBox;

  public FractalNoiseWeightingFieldControlsUpdater(TinaController controller, TinaWeightingFieldControllerData controls) {
    super(controller, controls);
  }

  @Override
  public void weightingFieldColorMapFilenameBtn_clicked() {
    // EMPTY
  }

  @Override
  public void updateControls(XForm xform) {
    super.updateControls(xform);
    controls.weightingFieldParam01REd.setOnlyIntegers(true);
    controls.weightingFieldParam01REd.setMinValue(1);
    controls.weightingFieldParam01REd.setHasMinValue(true);
    controls.weightingFieldParam01REd.setText(String.valueOf(xform.getWeightingFieldNoiseSeed()));

    controls.weightingFieldParam02REd.setHasMinValue(false);
    controls.weightingFieldParam02REd.setOnlyIntegers(false);
    controls.weightingFieldParam02REd.setText(String.valueOf(xform.getWeightingFieldFractalNoiseGain()));

    controls.weightingFieldParam03REd.setMinValue(1);
    controls.weightingFieldParam03REd.setHasMinValue(true);
    controls.weightingFieldParam03REd.setOnlyIntegers(true);
    controls.weightingFieldParam03REd.setText(String.valueOf(xform.getWeightingFieldFractalNoiseOctaves()));

    controls.weightingFieldParam04Cmb.setSelectedItem(xform.getWeightingFieldFractalType());

    controls.weightingFieldParam05REd.setHasMinValue(false);
    controls.weightingFieldParam05REd.setOnlyIntegers(false);
    controls.weightingFieldParam05REd.setText(Tools.doubleToString(xform.getWeightingFieldNoiseFrequency()));

    controls.weightingFieldParam06REd.setHasMinValue(false);
    controls.weightingFieldParam06REd.setOnlyIntegers(false);
    controls.weightingFieldParam06REd.setText(Tools.doubleToString(xform.getWeightingFieldFractalNoiseLacunarity()));
  }

  @Override
  public void enableControls(XForm xform, boolean enabled) {
    super.enableControls(xform, enabled);

    controls.weightingFieldColorMapFilenameLbl.setVisible(false);
    controls.weightingFieldColorMapFilenameBtn.setVisible(false);
    controls.weightingFieldColorMapFilenameInfoLbl.setVisible(false);

    controls.weightingFieldParam01REd.setVisible(true);
    controls.weightingFieldParam01REd.setMotionPropertyName("weightingFieldNoiseSeed");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam01REd, !enabled);
    controls.weightingFieldParam01Lbl.setVisible(true);
    controls.weightingFieldParam01Lbl.setText("Seed");
    controls.weightingFieldParam01Lbl.setToolTipText("Random number generator seed");

    controls.weightingFieldParam02REd.setVisible(true);
    controls.weightingFieldParam02REd.setMotionPropertyName("weightingFieldFractalNoiseGain");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam02REd, !enabled);
    controls.weightingFieldParam02Lbl.setVisible(true);
    controls.weightingFieldParam02Lbl.setText("Gain");
    controls.weightingFieldParam02Lbl.setToolTipText("Multiply amplitude by this value for each octave");

    controls.weightingFieldParam03REd.setVisible(true);
    controls.weightingFieldParam03REd.setMotionPropertyName("weightingFieldFractalNoiseOctaves");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam03REd, !enabled);
    controls.weightingFieldParam03Lbl.setVisible(true);
    controls.weightingFieldParam03Lbl.setText("Octaves");
    controls.weightingFieldParam03Lbl.setToolTipText("Number of components for fractal noise");

    controls.weightingFieldParam04Cmb.setVisible(true);
    controls.weightingFieldParam04Cmb.setEnabled(enabled);
    controls.weightingFieldParam04Lbl.setVisible(true);
    controls.weightingFieldParam04Lbl.setText("Noise Type");
    controls.weightingFieldParam04Lbl.setToolTipText("Type of fractal noise to use");
    fillingComboBox = true;
    try {
      controls.weightingFieldParam04Cmb.removeAllItems();
      controls.weightingFieldParam04Cmb.addItem(FractalType.FBM);
      controls.weightingFieldParam04Cmb.addItem(FractalType.BILLOW);
      controls.weightingFieldParam04Cmb.addItem(FractalType.RIGID_MULTI);
    }
    finally {
      fillingComboBox = false;
    }

    controls.weightingFieldParam05REd.setVisible(true);
    controls.weightingFieldParam05REd.setMotionPropertyName("weightingFieldNoiseFrequency");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam05REd, !enabled);
    controls.weightingFieldParam05Lbl.setVisible(true);
    controls.weightingFieldParam05Lbl.setText("Frequency");
    controls.weightingFieldParam05Lbl.setToolTipText("Noise frequency; increase to put peaks closer together");

    controls.weightingFieldParam06REd.setVisible(true);
    controls.weightingFieldParam06REd.setMotionPropertyName("weightingFieldFractalNoiseLacunarity");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam06REd, !enabled);
    controls.weightingFieldParam06Lbl.setVisible(true);
    controls.weightingFieldParam06Lbl.setText("Lacunarity");
    controls.weightingFieldParam06Lbl.setToolTipText("Multiply frequency by this value for each octave");

    controls.weightingFieldParam07REd.setVisible(false);
    controls.weightingFieldParam07Lbl.setVisible(false);
    controls.weightingFieldParam08Cmb.setVisible(false);
    controls.weightingFieldParam08Lbl.setVisible(false);
  }

  @Override
  public void weightingFieldParam01REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam01REd, "weightingFieldNoiseSeed", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
  }

  @Override
  public void weightingFieldParam02REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam02REd, "weightingFieldFractalNoiseGain", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
  }

  @Override
  public void weightingFieldParam03REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam03REd, "weightingFieldFractalNoiseOctaves", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
  }

  @Override
  public void weightingFieldParam04Cmb_changed() {
    if (controller.gridRefreshing || fillingComboBox)
      return;
    XForm xForm = controller.getCurrXForm();
    if (xForm != null && controls.weightingFieldParam04Cmb.getSelectedItem() != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldFractalType((FractalType) controls.weightingFieldParam04Cmb.getSelectedItem());
      controller.xFormControls.enableControls(xForm);
      controller.refreshXFormUI(xForm);
      controller.refreshFlameImage(true, false, 1, true, false);
    }
  }

  @Override
  public void weightingFieldParam05REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam05REd, "weightingFieldNoiseFrequency", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
  }

  @Override
  public void weightingFieldParam06REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam06REd, "weightingFieldFractalNoiseLacunarity", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
  }

  @Override
  public void weightingFieldParam07REd_changed() {
    // EMPTY
  }

  @Override
  public void weightingFieldParam08Cmb_changed() {
    // EMPTY
  }

  @Override
  public void weightMapParam01REd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldNoiseSeed(new XForm().getWeightingFieldNoiseSeed());
      controls.weightingFieldParam01REd.setText(String.valueOf(xForm.getWeightingFieldNoiseSeed()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  @Override
  public void weightMapParam02REd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldFractalNoiseGain(new XForm().getWeightingFieldFractalNoiseGain());
      controls.weightingFieldParam02REd.setText(String.valueOf(xForm.getWeightingFieldFractalNoiseGain()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  @Override
  public void weightMapParam03REd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldFractalNoiseOctaves(new XForm().getWeightingFieldFractalNoiseOctaves());
      controls.weightingFieldParam03REd.setText(String.valueOf(xForm.getWeightingFieldFractalNoiseOctaves()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  @Override
  public void weightMapParam05REd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldNoiseFrequency(new XForm().getWeightingFieldNoiseFrequency());
      controls.weightingFieldParam05REd.setText(Tools.doubleToString(xForm.getWeightingFieldNoiseFrequency()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  @Override
  public void weightMapParam06REd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldFractalNoiseLacunarity(new XForm().getWeightingFieldFractalNoiseLacunarity());
      controls.weightingFieldParam06REd.setText(Tools.doubleToString(xForm.getWeightingFieldFractalNoiseLacunarity()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  @Override
  public void weightMapParam04Cmb_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldFractalType(new XForm().getWeightingFieldFractalType());
      controls.weightingFieldParam04Cmb.setSelectedItem(xForm.getWeightingFieldFractalType());
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  @Override
  public void weightMapParam07REd_reset() {
    // EMPTY
  }

  @Override
  public void weightMapParam08Cmb_reset() {
    // EMPTY
  }

  @Override
  public void weightMapColorMapFilename_reset() {
    // EMPTY
  }
}
