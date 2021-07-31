/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.mutagen;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;

public class RandomAffineZMutation extends AbstractMutation {

  @Override
  public void execute(Layer pLayer, double mutationStrength) {
    Flame flame = pLayer.getOwner();

    if(Math.abs(flame.getCamPitch())<2.5 && Math.abs(flame.getCamBank())<2.5) {
      flame.setCamPitch(30 + Math.random() * 20.0);
      flame.setCamYaw(15 - Math.random() * 30.0);
      flame.setCamBank(15 - Math.random() * 30.0);
      if (Math.random() < 0.5) {
        flame.setCamPerspective(0.05 + Math.random() * 0.2);
        flame.setCamDOFArea(0.2 + Math.random() * 0.5);
      }
    }
    if (!flame.isPreserveZ() && Math.random() > 0.33) {
      flame.setPreserveZ(true);
    }

    EditPlane editPlane = flame.getEditPlane();
    try {
      flame.setEditPlane(Math.random() < 0.5 ? EditPlane.YZ : EditPlane.ZX);
      apply(pLayer, 0.2*mutationStrength);
      flame.setEditPlane(Math.random() < 0.5 ? EditPlane.YZ : EditPlane.ZX);
      apply(pLayer, 0.1*mutationStrength);
      flame.setEditPlane(Math.random() < 0.5 ? EditPlane.YZ : EditPlane.ZX);
      apply(pLayer, 0.05*mutationStrength);
    }
    finally {
      flame.setEditPlane(editPlane!=null ? editPlane: EditPlane.XY);
    }
  }

  private void apply(Layer pLayer, double pAmount) {
    XForm xForm;
    if (pLayer.getFinalXForms().size() == 0 || Math.random() < 0.75) {
      xForm = pLayer.getXForms().get(Tools.randomInt(pLayer.getXForms().size()));
    }
    else {
      xForm = pLayer.getFinalXForms().get(Tools.randomInt(pLayer.getFinalXForms().size()));
    }

    if (Math.random() < 0.75) {
      XFormTransformService.globalTranslate(xForm, -pAmount + 2 * Math.random() * pAmount, -pAmount + 2 * Math.random() * pAmount, Math.random() < 0.25);
    }
    if (Math.random() < 0.5) {
      XFormTransformService.rotate(xForm, -pAmount * 10.0 + 20.0 * Math.random() * pAmount, Math.random() < 0.25);
    }
    if (Math.random() < 0.5) {
      XFormTransformService.scale(xForm, -pAmount + 2 * Math.random() * pAmount, Math.random() < 0.75, Math.random() < 0.75, Math.random() < 0.25);
    }
  }

}
