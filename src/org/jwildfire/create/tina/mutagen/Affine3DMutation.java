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
package org.jwildfire.create.tina.mutagen;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.PostZScaleWFFunc;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class Affine3DMutation implements Mutation {

  @Override
  public void execute(Layer pLayer) {
    Flame flame = pLayer.getOwner();

    flame.setCamPitch(30 + Math.random() * 20.0);
    flame.setCamYaw(15 - Math.random() * 30.0);
    flame.setCamBank(15 - Math.random() * 30.0);
    flame.setCamPerspective(0.05 + Math.random() * 0.2);
    flame.setCamDOFArea(0.2 + Math.random() * 0.5);
    if (!flame.isPreserveZ() && Math.random() > 0.33) {
      flame.setPreserveZ(true);
    }
    EditPlane editPlane = flame.getEditPlane();
    try {
      flame.setEditPlane(Math.random() < 0.5 ? EditPlane.YZ : EditPlane.ZX);
      apply(pLayer, 0.75);
      flame.setEditPlane(Math.random() < 0.5 ? EditPlane.YZ : EditPlane.ZX);
      apply(pLayer, 0.2);
      flame.setEditPlane(Math.random() < 0.5 ? EditPlane.YZ : EditPlane.ZX);
      apply(pLayer, 0.1);
    }
    finally {
      flame.setEditPlane(editPlane);
    }
  }

  private void apply(Layer pLayer, double pAmount) {
    XForm xForm;
    if (pLayer.getFinalXForms().size() == 0 || Math.random() < 0.5) {
      xForm = pLayer.getXForms().get(Tools.randomInt(pLayer.getXForms().size()));
    }
    else {
      xForm = pLayer.getFinalXForms().get(Tools.randomInt(pLayer.getFinalXForms().size()));
    }
    if (pLayer.getOwner().isPreserveZ() && Math.random() > 0.25) {
      boolean hasZScale = false;
      for (Variation var : xForm.getVariations()) {
        if (PostZScaleWFFunc.VAR_NAME.equals(var.getFunc().getName())) {
          hasZScale = true;
          break;
        }
      }
      if (!hasZScale) {
        VariationFunc fnc = VariationFuncList.getVariationFuncInstance(PostZScaleWFFunc.VAR_NAME);
        xForm.addVariation(new Variation(0.001 + Math.random() * 0.1, fnc));
      }
    }

    if (Math.random() < 0.5) {
      if (Math.random() < 0.75) {
        if (Math.random() < 0.5) {
          xForm.setXYCoeff00(xForm.getXYCoeff00() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYCoeff01(xForm.getXYCoeff01() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYCoeff10(xForm.getXYCoeff10() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYCoeff11(xForm.getXYCoeff11() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYCoeff20(xForm.getXYCoeff20() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYCoeff21(xForm.getXYCoeff21() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
      }
      else {
        if (Math.random() < 0.5) {
          xForm.setXYPostCoeff00(xForm.getXYPostCoeff00() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYPostCoeff01(xForm.getXYPostCoeff01() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYPostCoeff10(xForm.getXYPostCoeff10() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYPostCoeff11(xForm.getXYPostCoeff11() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYPostCoeff20(xForm.getXYPostCoeff20() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setXYPostCoeff21(xForm.getXYPostCoeff21() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
      }
    }
    else {
      if (Math.random() < 0.33) {
        XFormTransformService.flipHorizontal(xForm, Math.random() < 0.5);
      }
      if (Math.random() > 0.67) {
        XFormTransformService.flipVertical(xForm, Math.random() < 0.5);
      }
      if (Math.random() < 0.75) {
        XFormTransformService.globalTranslate(xForm, -pAmount + 2 * Math.random() * pAmount, -pAmount + 2 * Math.random() * pAmount, Math.random() < 0.5);
      }
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, -pAmount * 10.0 + 20.0 * Math.random() * pAmount, Math.random() < 0.5);
      }
      if (Math.random() < 0.5) {
        XFormTransformService.scale(xForm, -pAmount + 2 * Math.random() * pAmount, Math.random() < 0.75, Math.random() < 0.75);
      }
    }
  }

}
