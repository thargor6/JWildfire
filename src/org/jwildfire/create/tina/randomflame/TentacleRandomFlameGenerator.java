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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class TentacleRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    int maxXFormsX = (int) (2.0 + Math.random() * 3.0);
    int maxXFormsY = (int) (2.0 + Math.random() * 3.0);
    double xMin = -(double) maxXFormsX * 0.5;
    double yMin = -(double) maxXFormsY * 0.5 + 1;

    String[] fnc = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL;

    double scl = 1.0;
    for (int y = 0; y < maxXFormsY; y++) {
      for (int x = 0; x < maxXFormsX; x++) {
        XForm xForm = new XForm();
        xForm.setWeight(0.5 + Math.random() * 99.5);
        layer.getXForms().add(xForm);
        XFormTransformService.globalTranslate(xForm, xMin + x, yMin + y, false);
        if (Math.random() < 0.5) {
          XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
        }
        else {
          XFormTransformService.rotate(xForm, -360.0 * Math.random(), true);
        }
        XFormTransformService.localTranslate(xForm, -1.0 + 2.0 * Math.random(), -1.0 + 2.0 * Math.random(), true);
        scl *= 0.75 + Math.random() / 4;
        XFormTransformService.scale(xForm, scl, true, true, true);
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(Math.random() * 0.9 + 0.1, VariationFuncList.getVariationFuncInstance(Math.random() > 0.25 ? fnc[fncIdx] : "linear3D", true));
        xForm.setColor(Math.random());
      }
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Tentacle";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return true;
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
