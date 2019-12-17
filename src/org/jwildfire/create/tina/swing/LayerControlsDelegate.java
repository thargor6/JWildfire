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

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.*;
import org.jwildfire.create.tina.base.motion.MotionCurve;

public class LayerControlsDelegate extends AbstractControlsDelegate {

  public LayerControlsDelegate(TinaController pOwner, TinaControllerData pData, JPanel pRootPanel) {
    super(pOwner, pData, pRootPanel, true);
  }

  private String formatPropertyName(String pName) {
    return "layer property \"" + pName + "\"";
  }

  @Override
  public String getEditingTitle(JWFNumberField sender) {
    JLabel label = sender.getLinkedLabelControl();
    return label != null ? formatPropertyName(sender.getLinkedLabelControl().getText()) : sender.getLinkedLabelControlName();
  }

  @Override
  public MotionCurve getCurveToEdit(String pPropName) {
    return AnimationService.getPropertyCurve(owner.getCurrLayer(), pPropName);
  }

  @Override
  public double getInitialValue(String pPropName) {
    return AnimationService.getPropertyValue(owner.getCurrLayer(), pPropName);
  }

  @Override
  public boolean isEnabled() {
    return owner.getCurrLayer() != null;
  }

  public void enableControls() {
    Flame flame = owner.getCurrFlame();
    Layer layer = owner.getCurrLayer();
    enableControl(data.layerDensityREd, layer == null);
    enableControl(data.layerWeightEd, layer == null);
    data.layerAddBtn.setEnabled(flame != null);
    data.layerDuplicateBtn.setEnabled(layer != null);
    data.layerDeleteBtn.setEnabled(flame != null && layer != null && flame.getLayers().size() > 1);
    data.layerExtractBtn.setEnabled(layer != null);
    data.layersTable.setEnabled(flame != null);
    data.layerVisibleBtn.setEnabled(layer != null);
    data.layerAppendBtn.setEnabled(flame != null);
    data.layerPreviewBtn.setEnabled(flame != null);
    data.layerHideOthersBtn.setEnabled(layer != null);
    data.layerShowAllBtn.setEnabled(flame != null);
  }


}
