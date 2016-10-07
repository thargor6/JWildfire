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
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class DuckiesRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + Math.random());
      xForm.addVariation(1.5 + Math.random(), VariationFuncList.getVariationFuncInstance(Math.random() < 0.12 ? VariationFuncList.getRandomVariationname() : "spherical", true));
      xForm.setColorSymmetry(-0.5);

      XFormTransformService.localTranslate(xForm, 0.75 - 5.50 * Math.random(), 0.75 - 1.50 * Math.random(), false);
      XFormTransformService.rotate(xForm, -60.0 + Math.random() * 30.0, false);
      XFormTransformService.scale(xForm, 0.1 + Math.random() * 0.4, true, true, false);

    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.0 + Math.random() * 100.0);

      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(Math.random() < 0.8 ? "juliascope" : "julian", true);
      varFunc.setParameter("power", Math.random() < 0.8 ? 2 : 2 + Math.random() * 10.0);
      varFunc.setParameter("dist", Math.random() < 0.8 ? 1.0 : -2.0 + 4.0 * Math.random());
      xForm.addVariation(0.5 + Math.random(), varFunc);
      xForm.setColorSymmetry(0.5);

      XFormTransformService.rotate(xForm, Math.random() * 360.0, false);
      XFormTransformService.localTranslate(xForm, 1.75 - 3.50 * Math.random(), 0.75 - 5.50 * Math.random(), false);
      XFormTransformService.scale(xForm, 1.1 + Math.random() * 2.0, true, true, false);
    }

    layer.getXForms().get(0).getModifiedWeights()[0] = 0.0;
    layer.getXForms().get(0).getModifiedWeights()[1] = 1.0;

    if (Math.random() > 0.667) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.0 + Math.random() * 100.0);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
      xForm.addVariation(0.25 + 1.25 * Math.random(), varFunc);

      XFormTransformService.rotate(xForm, -12.0 + Math.random() * 24.0, true);
      XFormTransformService.localTranslate(xForm, -0.125 + Math.random() * 0.25, -0.125 + Math.random() * 0.25, true);
      XFormTransformService.scale(xForm, 0.9 + Math.random() * 0.2, true, true, true);

      layer.getXForms().get(0).getModifiedWeights()[1] = 0.0;
      layer.getXForms().get(1).getModifiedWeights()[2] = 0.0;
      layer.getXForms().get(2).getModifiedWeights()[2] = 0.0;

      if (Math.random() > 0.667) {
        xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(0.50 + Math.random() * 50.0);
        varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.125 + 0.75 * Math.random(), varFunc);

        XFormTransformService.rotate(xForm, -24.0 + Math.random() * 48.0, true);
        XFormTransformService.localTranslate(xForm, -0.25 + Math.random() * 0.5, -0.25 + Math.random() * 0.5, true);
        XFormTransformService.scale(xForm, 0.5 + Math.random() * 0.25, true, true, true);

        layer.getXForms().get(0).getModifiedWeights()[2] = 0.0;
        layer.getXForms().get(2).getModifiedWeights()[2] = 0.0;
        layer.getXForms().get(1).getModifiedWeights()[3] = 0.0;
        layer.getXForms().get(2).getModifiedWeights()[3] = 0.0;
        layer.getXForms().get(3).getModifiedWeights()[3] = 0.0;
      }
    }

    flame.getFirstLayer().randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "Duckies";
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
