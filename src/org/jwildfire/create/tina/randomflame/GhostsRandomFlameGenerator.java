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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class GhostsRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setCamZoom(2.0);
    flame.setCamRoll(-90.0);
    Layer layer = flame.getFirstLayer();
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.5 + Math.random());
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("spherical", true);
        xForm.addVariation(0.5 + Math.random(), varFunc);
      }
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.05 + Math.random() * 0.15, varFunc).setPriority(Math.random() < 0.5 ? -1 : 1);
      }

      xForm.setColor(0.4 + Math.random() * 0.2);
      xForm.setColorSymmetry(0.82 + Math.random() * 0.16);
      XFormTransformService.rotate(xForm, 180, false);
      XFormTransformService.scale(xForm, 2.0 + Math.random() * 25.0, true, true, false);
      XFormTransformService.localTranslate(xForm, 0.5 * (0.5 - 1.0 * Math.random()), 0.5 - 1.0 * Math.random(), false);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.5 + Math.random());
      xForm.setColor(Math.random());
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("linear", true);
        xForm.addVariation(0.05 + Math.random() * 0.25, varFunc);
      }

      if (Math.random() < 0.66) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("radial_blur", true);
        varFunc.setParameter("angle", Math.random() * MathLib.M_PI);
        xForm.addVariation(0.025 + Math.random() * 0.1, varFunc);
      }
      else {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.05 + Math.random() * 0.5, varFunc);
      }

      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.05 + Math.random() * 0.5, varFunc).setPriority(Math.random() < 0.5 ? -1 : 1);
      }

      XFormTransformService.scale(xForm, 0.1 + Math.random() * 0.8, false, true, false);
      XFormTransformService.scale(xForm, 1.0 + Math.random() * 3.0, true, false, false);
      XFormTransformService.scale(xForm, 0.1 + Math.random() * 0.8, true, true, false);
      XFormTransformService.rotate(xForm, 180, false);
      XFormTransformService.localTranslate(xForm, 2.0 - 4.0 * Math.random(), 0.5 * (2.0 - 4.0 * Math.random()), false);

      xForm.setColorSymmetry(Math.random());
    }

    return flame;
  }

  @Override
  public String getName() {
    return "Ghosts";
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
