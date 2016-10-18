/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;

public class AffineMutation implements Mutation {

  @Override
  public void execute(Layer pLayer) {
    apply(pLayer, 0.75);
    apply(pLayer, 0.2);
    apply(pLayer, 0.1);
  }

  private void apply(Layer pLayer, double pAmount) {
    XForm xForm;
    if (pLayer.getFinalXForms().size() == 0 || Math.random() < 0.5) {
      xForm = pLayer.getXForms().get(Tools.randomInt(pLayer.getXForms().size()));
    }
    else {
      xForm = pLayer.getFinalXForms().get(Tools.randomInt(pLayer.getFinalXForms().size()));
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
