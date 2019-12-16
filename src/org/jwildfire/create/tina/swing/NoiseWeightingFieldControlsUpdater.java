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

public class NoiseWeightingFieldControlsUpdater extends WeightingFieldControlsUpdater {

  public NoiseWeightingFieldControlsUpdater(TinaController controller, TinaWeightingFieldControllerData controls) {
    super(controller, controls);
  }

  @Override
  public void weightingFieldColorMapFilenameBtn_clicked() {
    // EMPTY
  }

  @Override
  public void updateControls(XForm xform) {
    super.updateControls(xform);

    controls.weightingFieldParam01REd.setMinValue(1);
    controls.weightingFieldParam01REd.setHasMinValue(true);
    controls.weightingFieldParam01REd.setOnlyIntegers(true);
    controls.weightingFieldParam01REd.setText(String.valueOf(xform.getWeightingFieldNoiseSeed()));

    controls.weightingFieldParam05REd.setHasMinValue(false);
    controls.weightingFieldParam05REd.setOnlyIntegers(false);
    controls.weightingFieldParam05REd.setText(Tools.doubleToString(xform.getWeightingFieldNoiseFrequency()));
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

    controls.weightingFieldParam02REd.setVisible(false);
    controls.weightingFieldParam02Lbl.setVisible(false);

    controls.weightingFieldParam03REd.setVisible(false);
    controls.weightingFieldParam03Lbl.setVisible(false);

    controls.weightingFieldParam04Cmb.setVisible(false);
    controls.weightingFieldParam04Lbl.setVisible(false);

    controls.weightingFieldParam05REd.setVisible(true);
    controls.weightingFieldParam05REd.setMotionPropertyName("weightingFieldNoiseFrequency");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam05REd, !enabled);
    controls.weightingFieldParam05Lbl.setVisible(true);
    controls.weightingFieldParam05Lbl.setText("Frequency");
    controls.weightingFieldParam05Lbl.setToolTipText("Noise frequency; increase to put peaks closer together");

    controls.weightingFieldParam06REd.setVisible(false);
    controls.weightingFieldParam06Lbl.setVisible(false);

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
    // EMPTY
  }

  @Override
  public void weightingFieldParam03REd_changed() {
    // EMPTY
  }

  @Override
  public void weightingFieldParam04Cmb_changed() {
    // EMPTY
  }

  @Override
  public void weightingFieldParam05REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam05REd, "weightingFieldNoiseFrequency", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
  }

  @Override
  public void weightingFieldParam06REd_changed() {
    // EMPTY
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
      refreshFieldPreviewImage(xForm);
      controller.refreshFlameImage(true, false, 1, true, false);
    }
  }

  @Override
  public void weightMapParam02REd_reset() {
    // EMPTY
  }

  @Override
  public void weightMapParam03REd_reset() {
    // EMPTY
  }

  @Override
  public void weightMapParam05REd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldNoiseFrequency(new XForm().getWeightingFieldNoiseFrequency());
      controls.weightingFieldParam05REd.setText(Tools.doubleToString(xForm.getWeightingFieldNoiseFrequency()));
      refreshFieldPreviewImage(xForm);
      controller.refreshFlameImage(true, false, 1, true, false);
    }
  }

  @Override
  public void weightMapParam06REd_reset() {
    // EMPTY
  }

  @Override
  public void weightMapParam04Cmb_reset() {
    // EMPTY
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
