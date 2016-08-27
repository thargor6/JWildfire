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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.palette.RGBPalette;

public class GradientControlsDelegate extends AbstractControlsDelegate {

  public GradientControlsDelegate(TinaController pOwner, TinaControllerData pData, JPanel pRootPanel) {
    super(pOwner, pData, pRootPanel, true);
  }

  @Override
  public String getEditingTitle(JWFNumberField sender) {
    return "gradient property \"" + sender.getLinkedLabelControl().getText() + "\"";
  }

  @Override
  public MotionCurve getCurveToEdit(String pPropName) {
    return AnimationService.getPropertyCurve(owner.getCurrLayer().getPalette(), pPropName);
  }

  @Override
  public double getInitialValue(String pPropName) {
    return AnimationService.getPropertyValue(owner.getCurrLayer().getPalette(), pPropName);
  }

  @Override
  public boolean isEnabled() {
    return owner.getCurrLayer() != null && owner.getCurrLayer().getPalette() != null;
  }

  public List<JWFNumberField> getMotionControls() {
    List<JWFNumberField> res = new ArrayList<JWFNumberField>();
    res.add(data.paletteShiftREd);
    return res;
  }

  public void enableControls() {
    enableControl(data.paletteShiftREd, false);
  }

  private RGBPalette getCurrPalette() {
    return owner.getCurrLayer().getPalette();
  }

  private boolean isNoRefresh() {
    return owner.isNoRefresh();
  }

  private void setNoRefresh(boolean pNoRefresh) {
    owner.setNoRefresh(pNoRefresh);
  }

}
