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
      xForm = pLayer.getXForms().get((int) (pLayer.getXForms().size() * Math.random()));
    }
    else {
      xForm = pLayer.getFinalXForms().get((int) (pLayer.getFinalXForms().size() * Math.random()));
    }
    if (Math.random() < 0.5) {
      if (Math.random() < 0.75) {
        if (Math.random() < 0.5) {
          xForm.setCoeff00(xForm.getCoeff00() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setCoeff01(xForm.getCoeff01() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setCoeff10(xForm.getCoeff10() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setCoeff11(xForm.getCoeff11() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setCoeff20(xForm.getCoeff20() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setCoeff21(xForm.getCoeff21() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
      }
      else {
        if (Math.random() < 0.5) {
          xForm.setPostCoeff00(xForm.getPostCoeff00() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setPostCoeff01(xForm.getPostCoeff01() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setPostCoeff10(xForm.getPostCoeff10() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setPostCoeff11(xForm.getPostCoeff11() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setPostCoeff20(xForm.getPostCoeff20() + pAmount * (-0.25 + 0.5 * Math.random()));
        }
        if (Math.random() < 0.5) {
          xForm.setPostCoeff21(xForm.getPostCoeff21() + pAmount * (-0.25 + 0.5 * Math.random()));
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
