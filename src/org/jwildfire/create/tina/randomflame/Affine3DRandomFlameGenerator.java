/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Linear3DFunc;

public class LinearRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    int maxXForms = (int) (1.0 + Math.random() * 5.0);
    double scl = 1.0;
    double tscl = 2.0;

    boolean contRot = Math.random() < 0.5;
    double r0 = 0;

    for (int i = 0; i < maxXForms; i++) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      if (contRot) {
        r0 += 45.0 * Math.random() - 9.0 * Math.random();
        XFormTransformService.rotate(xForm, r0);
      }
      else {
        if (Math.random() < 0.5) {
          XFormTransformService.rotate(xForm, 360.0 * Math.random());
        }
        else {
          XFormTransformService.rotate(xForm, -360.0 * Math.random());
        }
      }
      XFormTransformService.localTranslate(xForm, (2.0 * Math.random() - 1.0) * tscl, (2.0 * Math.random() - 1.0) * tscl);
      scl *= 0.8 + Math.random() * 0.1;
      tscl *= 0.8 + Math.random() * 0.1;

      XFormTransformService.scale(xForm, scl, true, true);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.5 + 0.5, new Linear3DFunc());
      xForm.setWeight(scl * Math.random() * 19.9 + 0.1);
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Linear only";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }
}
