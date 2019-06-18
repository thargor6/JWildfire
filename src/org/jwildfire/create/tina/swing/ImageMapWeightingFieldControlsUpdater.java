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
    controls.weightingFieldColorMapFilenameInfoLbl.setText(xform.getWeightingFieldColorMapFilename() != null && xform.getWeightingFieldColorMapFilename().length() > 0 ? new File(xform.getWeightingFieldColorMapFilename()).getName() : "(empty)");
    controls.weightingFieldParam01REd.setText(Tools.doubleToString(xform.getWeightingFieldColorMapXCentre()));
    controls.weightingFieldParam05REd.setText(Tools.doubleToString(xform.getWeightingFieldColorMapYCentre()));
    controls.weightingFieldParam02REd.setText(Tools.doubleToString(xform.getWeightingFieldColorMapXSize()));
    controls.weightingFieldParam06REd.setText(Tools.doubleToString(xform.getWeightingFieldColorMapYSize()));
  }

  @Override
  public void enableControls(XForm xform, boolean enabled) {
    super.enableControls(xform, enabled);
    controls.weightingFieldColorMapFilenameLbl.setVisible(true);
    controls.weightingFieldColorMapFilenameBtn.setVisible(true);
    controls.weightingFieldColorMapFilenameInfoLbl.setVisible(true);

    controls.weightingFieldParam01REd.setVisible(true);
    controls.weightingFieldParam01REd.setEnabled(enabled);
    controls.weightingFieldParam01Lbl.setVisible(true);
    controls.weightingFieldParam01Lbl.setText("Centre X");

    controls.weightingFieldParam02REd.setVisible(true);
    controls.weightingFieldParam02REd.setEnabled(enabled);
    controls.weightingFieldParam02Lbl.setVisible(true);
    controls.weightingFieldParam02Lbl.setText("Size X");

    controls.weightingFieldParam03REd.setVisible(false);
    controls.weightingFieldParam03Lbl.setVisible(false);

    controls.weightingFieldParam04Cmb.setVisible(false);
    controls.weightingFieldParam04Lbl.setVisible(false);

    controls.weightingFieldParam05REd.setVisible(true);
    controls.weightingFieldParam05REd.setEnabled(enabled);
    controls.weightingFieldParam05Lbl.setVisible(true);
    controls.weightingFieldParam05Lbl.setText("Centre Y");

    controls.weightingFieldParam06REd.setVisible(true);
    controls.weightingFieldParam06REd.setEnabled(enabled);
    controls.weightingFieldParam06Lbl.setVisible(true);
    controls.weightingFieldParam06Lbl.setText("Size Y");

    controls.weightingFieldParam07REd.setVisible(false);
    controls.weightingFieldParam07Lbl.setVisible(false);

    controls.weightingFieldParam08Cmb.setVisible(false);
    controls.weightingFieldParam08Lbl.setVisible(false);
  }

  @Override
  public void weightingFieldParam01REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam01REd, "weightingFieldColorMapXCentre", 1.0);
  }

  @Override
  public void weightingFieldParam02REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam02REd, "weightingFieldColorMapXSize", 1.0);
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
  }

  @Override
  public void weightingFieldParam06REd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldParam06REd, "weightingFieldColorMapYSize", 1.0);
  }

  @Override
  public void weightingFieldParam07REd_changed() {
    // EMPTY
  }

  @Override
  public void weightingFieldParam08Cmb_changed() {
    // EMPTY
  }

}
