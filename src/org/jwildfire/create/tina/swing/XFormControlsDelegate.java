/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.*;
import org.jwildfire.create.tina.base.motion.MotionCurve;

public class XFormControlsDelegate extends AbstractControlsDelegate {
  private final TinaWeightingFieldControllerData weightMapData;

  public XFormControlsDelegate(TinaController pOwner, TinaControllerData pData, TinaWeightingFieldControllerData pWeightMapData, JPanel pRootPanel) {
    super(pOwner, pData, pRootPanel, true);
    weightMapData = pWeightMapData;
    setUpMotionControls();
  }

  private String formatPropertyName(String pName) {
    return "xform property \"" + pName + "\"";
  }

  @Override
  public String getEditingTitle(JWFNumberField sender) {
    JLabel label = sender.getLinkedLabelControl();
    return label != null ? formatPropertyName(sender.getLinkedLabelControl().getText()) : sender.getLinkedLabelControlName();
  }

  @Override
  public MotionCurve getCurveToEdit(String pPropName) {
    return AnimationService.getPropertyCurve(owner.getCurrXForm(), pPropName);
  }

  @Override
  public double getInitialValue(String pPropName) {
    return AnimationService.getPropertyValue(owner.getCurrXForm(), pPropName);
  }

  @Override
  public boolean isEnabled() {
    return owner.getCurrXForm() != null;
  }

  public void setUpMotionControls() {
    boolean postTransform = isPostTransform();
    switch (getEditPlane()) {
      case XY:
        data.affineC00REd.setMotionPropertyName(postTransform ? "xyPostCoeff00" : "xyCoeff00");
        data.affineC01REd.setMotionPropertyName(postTransform ? "xyPostCoeff01" : "xyCoeff01");
        data.affineC10REd.setMotionPropertyName(postTransform ? "xyPostCoeff10" : "xyCoeff10");
        data.affineC11REd.setMotionPropertyName(postTransform ? "xyPostCoeff11" : "xyCoeff11");
        data.affineC20REd.setMotionPropertyName(postTransform ? "xyPostCoeff20" : "xyCoeff20");
        data.affineC21REd.setMotionPropertyName(postTransform ? "xyPostCoeff21" : "xyCoeff21");
        break;
      case YZ:
        data.affineC00REd.setMotionPropertyName(postTransform ? "yzPostCoeff00" : "yzCoeff00");
        data.affineC01REd.setMotionPropertyName(postTransform ? "yzPostCoeff01" : "yzCoeff01");
        data.affineC10REd.setMotionPropertyName(postTransform ? "yzPostCoeff10" : "yzCoeff10");
        data.affineC11REd.setMotionPropertyName(postTransform ? "yzPostCoeff11" : "yzCoeff11");
        data.affineC20REd.setMotionPropertyName(postTransform ? "yzPostCoeff20" : "yzCoeff20");
        data.affineC21REd.setMotionPropertyName(postTransform ? "yzPostCoeff21" : "yzCoeff21");
        break;
      default:
        data.affineC00REd.setMotionPropertyName(postTransform ? "zxPostCoeff00" : "zxCoeff00");
        data.affineC01REd.setMotionPropertyName(postTransform ? "zxPostCoeff01" : "zxCoeff01");
        data.affineC10REd.setMotionPropertyName(postTransform ? "zxPostCoeff10" : "zxCoeff10");
        data.affineC11REd.setMotionPropertyName(postTransform ? "zxPostCoeff11" : "zxCoeff11");
        data.affineC20REd.setMotionPropertyName(postTransform ? "zxPostCoeff20" : "zxCoeff20");
        data.affineC21REd.setMotionPropertyName(postTransform ? "zxPostCoeff21" : "zxCoeff21");
        break;
    }
  }

  private EditPlane getEditPlane() {
    if (owner == null || owner.getCurrFlame() == null)
      return EditPlane.XY;
    else
      return owner.getCurrFlame().getEditPlane();
  }

  public void enableControls(XForm xForm) {
    boolean enabled = xForm != null;

    data.affineRotateAmountREd.setEnabled(enabled);
    data.affineScaleAmountREd.setEnabled(enabled);
    data.affineMoveHorizAmountREd.setEnabled(enabled);
    data.affineMoveVertAmountREd.setEnabled(enabled);
    data.affineRotateLeftButton.setEnabled(enabled);
    data.affineRotateRightButton.setEnabled(enabled);
    data.affineEnlargeButton.setEnabled(enabled);
    data.affineShrinkButton.setEnabled(enabled);
    data.affineMoveUpButton.setEnabled(enabled);
    data.affineMoveLeftButton.setEnabled(enabled);
    data.affineMoveRightButton.setEnabled(enabled);
    data.affineMoveDownButton.setEnabled(enabled);
    data.affineFlipHorizontalButton.setEnabled(enabled);
    data.affineFlipVerticalButton.setEnabled(enabled);

    data.addTransformationButton.setEnabled(owner.getCurrFlame() != null);
    data.addLinkedTransformationButton.setEnabled(enabled && owner.getCurrLayer() != null && owner.getCurrLayer().getXForms().indexOf(xForm) >= 0);
    data.duplicateTransformationButton.setEnabled(enabled);
    data.deleteTransformationButton.setEnabled(enabled);
    data.addFinalTransformationButton.setEnabled(owner.getCurrFlame() != null);

    data.affineEditPostTransformButton.setEnabled(owner.getCurrFlame() != null);
    data.affineEditPostTransformSmallButton.setEnabled(owner.getCurrFlame() != null);
    data.toggleVariationsButton.setEnabled(owner.getCurrFlame() != null);

    enableControl(data.affineC00REd, !enabled);
    enableControl(data.affineC01REd, !enabled);
    enableControl(data.affineC10REd, !enabled);
    enableControl(data.affineC11REd, !enabled);
    enableControl(data.affineC20REd, !enabled);
    enableControl(data.affineC21REd, !enabled);
    enableControl(data.affineCoordsViewTypeCmb, !enabled);
    enableControl(data.affineRotateLeftButton, data.affineRotateEditMotionCurveBtn, getRotateMotionCurveProperty(), !enabled);
    enableControl(data.affineEnlargeButton, data.affineScaleEditMotionCurveBtn, getScaleMotionCurveProperty(), !enabled);

    data.affineResetTransformButton.setEnabled(enabled);
    data.editTransformCaptionButton.setEnabled(enabled);

    enableControl(data.transformationWeightREd, !enabled);

    for (TinaNonlinearControlsRow rows : data.TinaNonlinearControlsRows) {
      rows.getNonlinearVarCmb().setEnabled(enabled);
      rows.getNonlinearVarREd().setEnabled(enabled);
      rows.getNonlinearParamsCmb().setEnabled(enabled);
      rows.getNonlinearParamsREd().setEnabled(enabled);
      // refreshing occurs in refreshXFormUI():
      // rows.getNonlinearParamsLeftButton().setEnabled(enabled);
      // rows.getNonlinearParamsRightButton().setEnabled(enabled);
    }
    boolean colorEnabled = enabled && (xForm.getColorType() != ColorType.NONE );
    data.xFormColorREd.setVisible(enabled && xForm.getColorType() != ColorType.TARGET);
    enableControl(data.xFormColorREd, !colorEnabled || xForm.getColorType() == ColorType.TARGET || xForm.getColorType() == ColorType.CYCLIC);
    data.xFormColorSlider.setVisible(data.xFormColorREd.isVisible());
    data.xFormColorSlider.setEnabled(data.xFormColorREd.isEnabled());
    data.xFormTargetColorBtn.setVisible(enabled && xForm.getColorType() == ColorType.TARGET);
    data.xFormTargetColorBtn.setEnabled(enabled && xForm.getColorType() == ColorType.TARGET);
    enableControl(data.xFormSymmetryREd, !colorEnabled);
    data.xFormSymmetrySlider.setEnabled(data.xFormSymmetryREd.isEnabled());
    enableControl(data.xFormMaterialREd, !enabled);
    data.xFormMaterialSlider.setEnabled(data.xFormMaterialREd.isEnabled());
    enableControl(data.xFormMaterialSpeedREd, !enabled);
    data.xFormMaterialSpeedSlider.setEnabled(data.xFormMaterialSpeedREd.isEnabled());
    data.xFormModGammaREd.setEnabled(enabled);
    data.xFormModGammaSlider.setEnabled(enabled);
    data.xFormModGammaSpeedREd.setEnabled(enabled);
    data.xFormModGammaSpeedSlider.setEnabled(enabled);
    data.xFormModContrastREd.setEnabled(enabled);
    data.xFormModContrastSlider.setEnabled(enabled);
    data.xFormModContrastSpeedREd.setEnabled(enabled);
    data.xFormModContrastSpeedSlider.setEnabled(enabled);
    data.xFormModSaturationREd.setEnabled(enabled);
    data.xFormModSaturationSlider.setEnabled(enabled);
    data.xFormModSaturationSpeedREd.setEnabled(enabled);
    data.xFormModSaturationSpeedSlider.setEnabled(enabled);
    data.xFormModHueREd.setEnabled(enabled);
    data.xFormModHueSlider.setEnabled(enabled);
    data.xFormModHueSpeedREd.setEnabled(enabled);
    data.xFormModHueSpeedSlider.setEnabled(enabled);
    enableControl(data.xFormOpacityREd, !(enabled && xForm.getDrawMode() == DrawMode.OPAQUE));
    data.xFormOpacitySlider.setEnabled(data.xFormOpacityREd.isEnabled());
    data.xFormDrawModeCmb.setEnabled(enabled);
    data.xFormColorTypeCmb.setEnabled(enabled);

    owner.getWeightMapControlsUpdater(xForm).enableControls(xForm, enabled);

    data.relWeightsTable.setEnabled(enabled);
    enableRelWeightsControls();
  }

  public void enableRelWeightsControls() {
    int selRow = data.relWeightsTable.getSelectedRow();
    boolean enabled = data.relWeightsTable.isEnabled() && selRow >= 0 && owner.getCurrXForm() != null;
    data.relWeightsZeroButton.setEnabled(enabled);
    data.relWeightsOneButton.setEnabled(enabled);
    data.relWeightREd.setEnabled(enabled);
  }

  private boolean isPostTransform() {
    return data.affineEditPostTransformButton.isSelected();
  }

  private String getRotateMotionCurveProperty() {
    switch (getEditPlane()) {
      case XY:
        return isPostTransform() ? "xyPostRotate" : "xyRotate";
      case YZ:
        return isPostTransform() ? "yzPostRotate" : "yzRotate";
      default:
        return isPostTransform() ? "zxPostRotate" : "zxRotate";
    }
  }

  public void editRotateMotionCurve(ActionEvent e) {
    String propertyName = getRotateMotionCurveProperty();
    editMotionCurve(propertyName, formatPropertyName(propertyName));
    enableControl(data.affineRotateLeftButton, data.affineRotateEditMotionCurveBtn, propertyName, false);
  }

  private String getScaleMotionCurveProperty() {
    switch (getEditPlane()) {
      case XY:
        return isPostTransform() ? "xyPostScale" : "xyScale";
      case YZ:
        return isPostTransform() ? "yzPostScale" : "yzScale";
      default:
        return isPostTransform() ? "zxPostScale" : "zxScale";
    }
  }

  public void editScaleMotionCurve(ActionEvent e) {
    String propertyName = getScaleMotionCurveProperty();
    editMotionCurve(propertyName, formatPropertyName(propertyName));
    enableControl(data.affineRotateLeftButton, data.affineScaleEditMotionCurveBtn, propertyName, false);
  }

}
