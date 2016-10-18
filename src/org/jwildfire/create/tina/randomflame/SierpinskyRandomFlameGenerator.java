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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class SierpinskyRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCamRoll(Tools.randomInt(8) * -45.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    flame.setCamZoom(4.56);
    double posx[] = { -0.5, 0.5, 0.5, -0.5 };
    double posy[] = { -0.5, -0.5, 0.5, 0.5 };

    for (int i = 0; i < posx.length; i++) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setCoeff20(posx[i]);
      xForm.setCoeff21(posy[i]);
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(Math.random());
      XFormTransformService.scale(xForm, 1.0, true, true);
      if (i == posx.length - 1) {
        if (Math.random() < 0.5) {
          xForm.addVariation(0.5, VariationFuncList.getVariationFuncInstance(Math.random() < 0.25 ? "dc_linear" : "linear3D", true));
          XFormTransformService.rotate(xForm, 5.0 - 10.0 * Math.random());
          XFormTransformService.localTranslate(xForm, 0.1 - 0.2 * Math.random(), 0.1 - 0.2 * Math.random());
          XFormTransformService.scale(xForm, 1.0 - 0.1 * Math.random(), Math.random() < 0.75, Math.random() > 0.25);
        }
        String fncName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[(int) (Math.random() * ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length)];
        double amount;
        if (Math.random() < 0.33) {
          amount = 0.5 - Math.random();
        }
        else if (Math.random() < 0.67) {
          amount = 0.25 + 0.5 * Math.random();
        }
        else {
          amount = 0.5;
        }
        xForm.addVariation(amount, VariationFuncList.getVariationFuncInstance(fncName, true));
        if (Math.random() < 0.5) {
          XFormTransformService.scale(xForm, 1.0 - 0.1 * Math.random(), Math.random() < 0.75, Math.random() > 0.25);
        }
      }
      else {
        xForm.addVariation(0.5, VariationFuncList.getVariationFuncInstance(Math.random() < 0.25 ? "dc_linear" : "linear3D", true));
      }
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Sierpinsky";
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
