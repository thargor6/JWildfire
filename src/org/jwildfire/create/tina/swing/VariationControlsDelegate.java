/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import javax.swing.JPanel;

import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.variation.Variation;

public class VariationControlsDelegate extends AbstractControlsDelegate {
  private final int variationIndex;

  public VariationControlsDelegate(TinaController pOwner, TinaControllerData pData, JPanel pRootPanel, int pVariationIndex) {
    super(pOwner, pData, pRootPanel, true);
    variationIndex = pVariationIndex;
  }

  private String formatPropertyName(String pName) {
    return "variation property \"" + pName + "\"";
  }

  @Override
  public String getEditingTitle(JWFNumberField sender) {
    return formatPropertyName(sender.getLinkedLabelControl().getText());
  }

  @Override
  public MotionCurve getCurveToEdit(String pPropName) {
    XForm xForm = owner.getCurrXForm();
    Variation var = xForm.getVariation(variationIndex);
    return AnimationService.getPropertyCurve(var, pPropName);
  }

  @Override
  public double getInitialValue(String pPropName) {
    XForm xForm = owner.getCurrXForm();
    Variation var = xForm.getVariation(variationIndex);
    return AnimationService.getPropertyValue(var, pPropName);
  }

  @Override
  public boolean isEnabled() {
    return owner.getCurrXForm() != null && variationIndex < owner.getCurrXForm().getVariationCount();
  }

}
