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
import org.jwildfire.create.tina.base.weightingfield.WeightingField;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldInputType;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

import java.awt.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.swing.*;


public abstract class WeightingFieldControlsUpdater {
  public static final String VAR_NAME_VAR_PARAM_SEPARATOR = ".";


  protected final TinaController controller;
  protected final TinaWeightingFieldControllerData controls;
  protected final XFormControlsDelegate xFormControlsDelegate;

  protected WeightingFieldControlsUpdater(TinaController controller, TinaWeightingFieldControllerData controls) {
    this.controller = controller;
    this.controls = controls;
    this.xFormControlsDelegate = controller.xFormControls;
  }

  public void clearComponents() {
    controls.weightingFieldTypeCmb.setSelectedIndex(-1);
    controls.weightingFieldTypeCmb.setEnabled(false);
    controls.weightingFieldInputCmb.setSelectedIndex(-1);
    controls.weightingFieldInputCmb.setEnabled(false);
    controls.weightingFieldColorIntensityREd.setValue(0.0);
    xFormControlsDelegate.enableControl(controls.weightingFieldColorIntensityREd, true);
    controls.weightingFieldVariationIntensityREd.setValue(0.0);
    xFormControlsDelegate.enableControl(controls.weightingFieldVariationIntensityREd, true);
    controls.weightingFieldJitterIntensityREd.setValue(0.0);
    xFormControlsDelegate.enableControl(controls.weightingFieldJitterIntensityREd, true);
    controls.weightingFieldVarParam1AmountREd.setValue(0.0);
    xFormControlsDelegate.enableControl(controls.weightingFieldVarParam1AmountREd, true);
    controls.weightingFieldVarParam2AmountREd.setValue(0.0);
    xFormControlsDelegate.enableControl(controls.weightingFieldVarParam2AmountREd, true);
    controls.weightingFieldVarParam3AmountREd.setValue(0.0);
    xFormControlsDelegate.enableControl(controls.weightingFieldVarParam3AmountREd, true);
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
    controls.weightingFieldParam01REd.setMotionPropertyName("");
    controls.weightingFieldParam01Lbl.setVisible(false);
    controls.weightingFieldParam02REd.setVisible(false);
    controls.weightingFieldParam02REd.setMotionPropertyName("");
    controls.weightingFieldParam02Lbl.setVisible(false);
    controls.weightingFieldParam03REd.setVisible(false);
    controls.weightingFieldParam03REd.setMotionPropertyName("");
    controls.weightingFieldParam03Lbl.setVisible(false);
    controls.weightingFieldParam04Cmb.setVisible(false);
    controls.weightingFieldParam04Lbl.setVisible(false);
    controls.weightingFieldParam05REd.setVisible(false);
    controls.weightingFieldParam05REd.setMotionPropertyName("");
    controls.weightingFieldParam05Lbl.setVisible(false);
    controls.weightingFieldParam06REd.setVisible(false);
    controls.weightingFieldParam06REd.setMotionPropertyName("");
    controls.weightingFieldParam06Lbl.setVisible(false);
    controls.weightingFieldParam07REd.setVisible(false);
    controls.weightingFieldParam07REd.setMotionPropertyName("");
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
    controls.weightingFieldJitterIntensityREd.setText(Tools.doubleToString(xform.getWeightingFieldJitterIntensity()));

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
    xFormControlsDelegate.enableControl(controls.weightingFieldColorIntensityREd, !(enabled && hasWeightingFieldType));
    xFormControlsDelegate.enableControl(controls.weightingFieldVariationIntensityREd, !(enabled && hasWeightingFieldType));
    xFormControlsDelegate.enableControl(controls.weightingFieldJitterIntensityREd, !(enabled && hasWeightingFieldType));

    xFormControlsDelegate.enableControl(controls.weightingFieldVarParam1AmountREd, !(enabled && hasWeightingFieldType));
    xFormControlsDelegate.enableControl(controls.weightingFieldVarParam2AmountREd ,!(enabled && hasWeightingFieldType));
    xFormControlsDelegate.enableControl(controls.weightingFieldVarParam3AmountREd, !(enabled && hasWeightingFieldType));

    //fillVarParamCmb(xform, controls.weightingFieldVarParam1NameCmb);
    controls.weightingFieldVarParam1NameCmb.setEnabled(enabled && hasWeightingFieldType);
    //fillVarParamCmb(xform, controls.weightingFieldVarParam2NameCmb);
    controls.weightingFieldVarParam2NameCmb.setEnabled(enabled && hasWeightingFieldType);
    //fillVarParamCmb(xform, controls.weightingFieldVarParam3NameCmb);
    controls.weightingFieldVarParam3NameCmb.setEnabled(enabled && hasWeightingFieldType);
    refreshFieldPreviewImage(xform);
  }

  private ImagePanel getFieldPreviewImagePanel() {
    if (controls.weightingFieldPreviewImgPanel == null) {
      int width =  Math.max(controls.weightingFieldPreviewImgRootPanel.getWidth(), 64);
      int height = Math.max(controls.weightingFieldPreviewImgRootPanel.getHeight(), 48);
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      controls.weightingFieldPreviewImgPanel = new ImagePanel(img, 0, 0, width);
      controls.weightingFieldPreviewImgRootPanel.add(controls.weightingFieldPreviewImgPanel, BorderLayout.CENTER);
      controls.weightingFieldPreviewImgRootPanel.getParent().validate();
    }
    return controls.weightingFieldPreviewImgPanel;
  }

  private void clearFieldPreviewImage() {
    ImagePanel imgPanel = getFieldPreviewImagePanel();
    int width = imgPanel.getWidth();
    int height = imgPanel.getHeight();
    if (width >= 4 && height >= 4) {
      SimpleImage img = imgPanel.getImage();
      img.fillBackground(128,128,128);
      imgPanel.getParent().validate();
      imgPanel.getParent().getParent().validate();
      imgPanel.repaint();
    }
  }

  public void weightMapVariationIntensityREd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldVarAmountIntensity(new XForm().getWeightingFieldVarAmountIntensity());
      controls.weightingFieldVariationIntensityREd.setText(Tools.doubleToString(xForm.getWeightingFieldVarAmountIntensity()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  public void weightingFieldJitterIntensityREd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldJitterIntensity(new XForm().getWeightingFieldJitterIntensity());
      controls.weightingFieldJitterIntensityREd.setText(Tools.doubleToString(xForm.getWeightingFieldJitterIntensity()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  public void weightingFieldTypeCmb_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldType(new XForm().getWeightingFieldType());
      controls.weightingFieldTypeCmb.setSelectedItem(xForm.getWeightingFieldType());
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  public void weightMapInputCmb_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldInput(new XForm().getWeightingFieldInput());
      controls.weightingFieldInputCmb.setSelectedItem(xForm.getWeightingFieldInput());
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  public void weightMapColorIntensityREd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldColorIntensity(new XForm().getWeightingFieldColorIntensity());
      controls.weightingFieldColorIntensityREd.setText(Tools.doubleToString(xForm.getWeightingFieldColorIntensity()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  public void weightingFieldVarParam1AmountREd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldVarParam1Intensity(new XForm().getWeightingFieldVarParam1Intensity());
      controls.weightingFieldVarParam1AmountREd.setText(Tools.doubleToString(xForm.getWeightingFieldVarParam1Intensity()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  public void weightingFieldVarParam2AmountREd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldVarParam2Intensity(new XForm().getWeightingFieldVarParam2Intensity());
      controls.weightingFieldVarParam2AmountREd.setText(Tools.doubleToString(xForm.getWeightingFieldVarParam2Intensity()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  public void weightingFieldVarParam3AmountREd_reset() {
    XForm xForm = controller.getCurrXForm();
    if (xForm != null) {
      controller.saveUndoPoint();
      xForm.setWeightingFieldVarParam3Intensity(new XForm().getWeightingFieldVarParam3Intensity());
      controls.weightingFieldVarParam3AmountREd.setText(Tools.doubleToString(xForm.getWeightingFieldVarParam3Intensity()));
      controller.refreshFlameImage(true, false, 1, true, false);
      refreshFieldPreviewImage(xForm);
    }
  }

  public abstract void weightMapParam01REd_reset();

  public abstract void weightMapParam02REd_reset();

  public abstract void weightMapParam03REd_reset();

  public abstract void weightMapParam05REd_reset();

  public abstract void weightMapParam06REd_reset();

  public abstract void weightMapParam04Cmb_reset();

  public abstract void weightMapParam07REd_reset();

  public abstract void weightMapParam08Cmb_reset();

  public abstract void weightMapColorMapFilename_reset();

  private class GenPreviewThread implements Runnable {
    private final SimpleImage img;
    private final XForm xform;
    private boolean done;
    private boolean cancelSignalled;

    public GenPreviewThread(SimpleImage img, XForm xform) {
      this.img = img;
      this.xform = xform;
    }

    @Override
    public void run() {
      done = cancelSignalled = false;
      try {
        final double xCentre = 0.0;
        final double yCentre = 0.0;
        final double xSize = 4.0;
        final double ySize = 4.0;
        final int width = img.getImageWidth();
        final int height = img.getImageHeight();

        final WeightingField weightingField = Optional.ofNullable(xform.getWeightingFieldType()).orElse(WeightingFieldType.NONE).getInstance(xform);

        for(int i=0;i<img.getImageHeight();i++) {
          if(cancelSignalled) {
            break;
          }
          double yCoord = (double)i/height*ySize+yCentre-ySize*0.5;
          for(int j=0;j<img.getImageWidth();j++) {
            if(cancelSignalled) {
              break;
            }
            double xCoord = (double)j/width*xSize+xCentre-xSize*0.5;
            double weight = weightingField.getValue(xCoord, yCoord, 0.0);
            int v =  Math.max(Math.min((int)(weight * 128.0 + 128.0), 255), 0);

            img.setRGB(j, i, 2*v/3, v, v/2);
          }
        }
      }
      finally {
        done = true;
        if(!cancelSignalled) {
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              ImagePanel imgPanel = getFieldPreviewImagePanel();
              imgPanel.setImage(img);
              imgPanel.getParent().validate();
              imgPanel.getParent().getParent().validate();
              imgPanel.repaint();
            }
          });
        }
      }
    }

    public void signalCancel() {
      cancelSignalled = true;
    }
  }

  private GenPreviewThread genPreviewThread;

  protected void refreshFieldPreviewImage(XForm xform) {
    if(xform == null || xform.getWeightingFieldType()==null || WeightingFieldType.NONE.equals(xform.getWeightingFieldType())) {
      clearFieldPreviewImage();
      return;
    }

    ImagePanel imgPanel = getFieldPreviewImagePanel();
    int width = imgPanel.getWidth();
    int height = imgPanel.getHeight();
    if (width >= 4 && height >= 4) {
      SimpleImage img = imgPanel.getImage();
      try {
        if (genPreviewThread != null) {
          genPreviewThread.signalCancel();
          genPreviewThread = null;
        }
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      genPreviewThread = new GenPreviewThread(imgPanel.getImage(), xform);
      new Thread(genPreviewThread).start();
    }
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

  public void weightingFieldJitterIntensityREd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldJitterIntensityREd, "weightingFieldJitterIntensity", 1.0);
  }

  public void weightingFieldVarParam1AmountREd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldVarParam1AmountREd, "weightingFieldVarParam1Intensity", 1.0);
  }

  public void weightingFieldVarParam2AmountREd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldVarParam2AmountREd, "weightingFieldVarParam2Intensity", 1.0);
  }

  public void weightingFieldVarParam3AmountREd_changed() {
    controller.xFormTextFieldChanged(null, controls.weightingFieldVarParam3AmountREd, "weightingFieldVarParam3Intensity", 1.0);
  }

  public void weightingFieldVarParam1NameCmb_changed() {
    if (controller.gridRefreshing)
      return;
    XForm xForm = controller.getCurrXForm();
    if (xForm != null && controls.weightingFieldVarParam1NameCmb.getSelectedItem() != null) {
      controller.saveUndoPoint();
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
      controller.saveUndoPoint();
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
      controller.saveUndoPoint();
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
    if(varName!=null && varName.length()>0 && paramName!=null && paramName.length()>0 && VariationFuncList.isValidVariationForWeightingFields(varName)) {
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
      controller.saveUndoPoint();
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
      controller.saveUndoPoint();
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
       if(xform!=null) {
         for (Variation var : xform.getVariations()) {
           {
             String entry = encodeVarNameParamName(var.getFunc().getName(), Variation.WFIELD_AMOUNT_PARAM);
             addCmbItem(cmb, items, entry);
           }
           for (String paramName : var.getFunc().getParameterNames()) {
             String entry = encodeVarNameParamName(var.getFunc().getName(), paramName);
             addCmbItem(cmb, items, entry);
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

  private void addCmbItem(JComboBox cmb, Set<String> items, String entry) {
    if (!"".equals(entry) && !items.contains(entry)) {
      if (cmb.getItemCount() == 0) {
        cmb.addItem("");
      }
      cmb.addItem(entry);
      items.add(entry);
    }
  }
}
