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

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.weightingfield.ImageMapWeightingField;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.ImageFileChooser;

import javax.swing.*;
import java.io.File;

public class ImageMapWeightingFieldControlsUpdater extends WeightingFieldControlsUpdater {

  public ImageMapWeightingFieldControlsUpdater(TinaController controller, TinaWeightingFieldControllerData controls) {
    super(controller, controls);
  }

  @Override
  public void weightingFieldColorMapFilenameBtn_clicked() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null && controls.weightingFieldInputCmb.getSelectedItem() != null) {
      JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
      if (Prefs.getPrefs().getInputImagePath() != null) {
        try {
          if (xForm.getWeightingFieldColorMapFilename()!=null && xForm.getWeightingFieldColorMapFilename().length() > 0) {
            chooser.setSelectedFile(new File(xForm.getWeightingFieldColorMapFilename()));
          }
          else {
            chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputImagePath()));
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(controller.getFlamePanel()) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        try {
          String filename = file.getAbsolutePath();
          WFImage img = RessourceManager.getImage(filename);
          if (img.getImageWidth() < 2 || img.getImageHeight() < 2 || !(img instanceof SimpleImage)) {
            throw new Exception("Invalid image");
          }
          Prefs.getPrefs().setLastInputImageFile(file);

          controller.saveUndoPoint();
          xForm.setWeightingFieldColorMapFilename(filename);
          updateControls(xForm);
          controller.refreshFlameImage(true, false, 1, true, false);
          refreshFieldPreviewImage(controller.getCurrXForm());
        }
        catch (Throwable ex) {
          controller.errorHandler.handleError(ex);
        }
      }
    }
  }

  @Override
  public void updateControls(XForm xform) {
    super.updateControls(xform);
    updateColorMapFilenameInfoLbl(xform);

    controls.weightingFieldParam01REd.setHasMinValue(false);
    controls.weightingFieldParam01REd.setOnlyIntegers(false);
    controls.weightingFieldParam01REd.setText(Tools.doubleToString(xform.getWeightingFieldColorMapXCentre()));

    controls.weightingFieldParam05REd.setHasMinValue(false);
    controls.weightingFieldParam05REd.setOnlyIntegers(false);
    controls.weightingFieldParam05REd.setText(Tools.doubleToString(xform.getWeightingFieldColorMapYCentre()));

    controls.weightingFieldParam02REd.setHasMinValue(false);
    controls.weightingFieldParam02REd.setOnlyIntegers(false);
    controls.weightingFieldParam02REd.setText(Tools.doubleToString(xform.getWeightingFieldColorMapXSize()));

    controls.weightingFieldParam06REd.setHasMinValue(false);
    controls.weightingFieldParam06REd.setOnlyIntegers(false);
    controls.weightingFieldParam06REd.setText(Tools.doubleToString(xform.getWeightingFieldColorMapYSize()));
  }

  private void updateColorMapFilenameInfoLbl(XForm xform) {
    controls.weightingFieldColorMapFilenameInfoLbl.setText(xform.getWeightingFieldColorMapFilename() != null && xform.getWeightingFieldColorMapFilename().length() > 0 ? new File(xform.getWeightingFieldColorMapFilename()).getName() : ImageMapWeightingField.DFLT_IMAGE_FILE_NAME);
  }

  @Override
  public void enableControls(XForm xform, boolean enabled) {
    super.enableControls(xform, enabled);
    controls.weightingFieldColorMapFilenameLbl.setVisible(true);
    controls.weightingFieldColorMapFilenameBtn.setVisible(true);
    controls.weightingFieldColorMapFilenameInfoLbl.setVisible(true);

    controls.weightingFieldParam01REd.setVisible(true);
    controls.weightingFieldParam01REd.setMotionPropertyName("weightingFieldColorMapXCentre");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam01REd, !enabled);
    controls.weightingFieldParam01Lbl.setVisible(true);
    controls.weightingFieldParam01Lbl.setText("Centre X");
    controls.weightingFieldParam01Lbl.setToolTipText("Move image horizontally");

    controls.weightingFieldParam02REd.setVisible(true);
    controls.weightingFieldParam02REd.setMotionPropertyName("weightingFieldColorMapXSize");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam02REd, !enabled);
    controls.weightingFieldParam02Lbl.setVisible(true);
    controls.weightingFieldParam02Lbl.setText("Size X");
    controls.weightingFieldParam02Lbl.setToolTipText("Horizontal scale factor");

    controls.weightingFieldParam03REd.setVisible(false);
    controls.weightingFieldParam03Lbl.setVisible(false);

    controls.weightingFieldParam04Cmb.setVisible(false);
    controls.weightingFieldParam04Lbl.setVisible(false);

    controls.weightingFieldParam05REd.setVisible(true);
    controls.weightingFieldParam05REd.setMotionPropertyName("weightingFieldColorMapYCentre");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam05REd, !enabled);
    controls.weightingFieldParam05Lbl.setVisible(true);
    controls.weightingFieldParam05Lbl.setText("Centre Y");
    controls.weightingFieldParam05Lbl.setToolTipText("Move image vertically");

    controls.weightingFieldParam06REd.setVisible(true);
    controls.weightingFieldParam06REd.setMotionPropertyName("weightingFieldColorMapYSize");
    xFormControlsDelegate.enableControl(controls.weightingFieldParam06REd, !enabled);
    controls.weightingFieldParam06Lbl.setVisible(true);
    controls.weightingFieldParam06Lbl.setText("Size Y");
    controls.weightingFieldParam06Lbl.setToolTipText("Vertical scale factor");

    controls.weightingFieldParam07REd.setVisible(false);
    controls.weightingFieldParam07Lbl.setVisible(false);

    controls.weightingFieldParam08Cmb.setVisible(false);
    controls.weightingFieldParam08Lbl.setVisible(false);
  }

  @Override
  public void weightingFieldParam01REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam01REd, "weightingFieldColorMapXCentre", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
  }

  @Override
  public void weightingFieldParam02REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam02REd, "weightingFieldColorMapXSize", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
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
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam05REd, "weightingFieldColorMapYCentre", 1.0);
    refreshFieldPreviewImage(controller.getCurrXForm());
  }

  @Override
  public void weightingFieldParam06REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam06REd, "weightingFieldColorMapYSize", 1.0);
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
      xForm.setWeightingFieldColorMapXCentre(new XForm().getWeightingFieldColorMapXCentre());
      controls.weightingFieldParam01REd.setText(Tools.doubleToString(xForm.getWeightingFieldColorMapXCentre()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  @Override
  public void weightMapParam02REd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldColorMapXSize(new XForm().getWeightingFieldColorMapXSize());
      controls.weightingFieldParam02REd.setText(Tools.doubleToString(xForm.getWeightingFieldColorMapXSize()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
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
      xForm.setWeightingFieldColorMapYCentre(new XForm().getWeightingFieldColorMapYCentre());
      controls.weightingFieldParam05REd.setText(Tools.doubleToString(xForm.getWeightingFieldColorMapYCentre()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  @Override
  public void weightMapParam06REd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldColorMapYSize(new XForm().getWeightingFieldColorMapYSize());
      controls.weightingFieldParam06REd.setText(Tools.doubleToString(xForm.getWeightingFieldColorMapYSize()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
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
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldColorMapFilename(new XForm().getWeightingFieldColorMapFilename());
      updateColorMapFilenameInfoLbl(xForm);
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }
}
