/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

import javax.swing.JTabbedPane;

import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.motion.MotionCurve;

public class XFormControlsDelegate extends AbstractControlsDelegate {

  public XFormControlsDelegate(TinaController pOwner, TinaControllerData pData, JTabbedPane pRootTabbedPane) {
    super(pOwner, pData, pRootTabbedPane);
    setUpMotionControls();
  }

  @Override
  public String getEditingTitle(JWFNumberField sender) {
    return "xform property \"" + sender.getLinkedLabelControl().getText() + "\"";
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
    boolean postTransform = data.affineEditPostTransformButton.isSelected();
    data.affineC00REd.setMotionPropertyName(postTransform ? "postCoeff00" : "coeff00");
    data.affineC01REd.setMotionPropertyName(postTransform ? "postCoeff01" : "coeff01");
    data.affineC10REd.setMotionPropertyName(postTransform ? "postCoeff10" : "coeff10");
    data.affineC11REd.setMotionPropertyName(postTransform ? "postCoeff11" : "coeff11");
    data.affineC20REd.setMotionPropertyName(postTransform ? "postCoeff20" : "coeff20");
    data.affineC21REd.setMotionPropertyName(postTransform ? "postCoeff21" : "coeff21");
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
    data.mouseEditZoomInButton.setEnabled(owner.getCurrFlame() != null);
    data.mouseEditZoomOutButton.setEnabled(owner.getCurrFlame() != null);
    data.toggleVariationsButton.setEnabled(owner.getCurrFlame() != null);

    enableControl(data.affineC00REd, !enabled);
    enableControl(data.affineC01REd, !enabled);
    enableControl(data.affineC10REd, !enabled);
    enableControl(data.affineC11REd, !enabled);
    enableControl(data.affineC20REd, !enabled);
    enableControl(data.affineC21REd, !enabled);
    data.affineResetTransformButton.setEnabled(enabled);
    data.editTransformCaptionButton.setEnabled(enabled);

    data.transformationWeightREd.setEnabled(enabled);

    for (TinaNonlinearControlsRow rows : data.TinaNonlinearControlsRows) {
      rows.getNonlinearVarCmb().setEnabled(enabled);
      rows.getNonlinearVarREd().setEnabled(enabled);
      rows.getNonlinearParamsCmb().setEnabled(enabled);
      rows.getNonlinearParamsREd().setEnabled(enabled);
      // refreshing occurs in refreshXFormUI():
      // rows.getNonlinearParamsLeftButton().setEnabled(enabled);
      // rows.getNonlinearParamsRightButton().setEnabled(enabled);
    }
    data.xFormColorREd.setEnabled(enabled);
    data.xFormColorSlider.setEnabled(enabled);
    data.xFormSymmetryREd.setEnabled(enabled);
    data.xFormSymmetrySlider.setEnabled(enabled);
    data.xFormOpacityREd.setEnabled(enabled && xForm.getDrawMode() == DrawMode.OPAQUE);
    data.xFormOpacitySlider.setEnabled(data.xFormOpacityREd.isEnabled());
    data.xFormDrawModeCmb.setEnabled(enabled);

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

}
