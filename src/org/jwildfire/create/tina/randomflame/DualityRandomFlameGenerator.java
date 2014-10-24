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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.mutagen.RandomParamMutation;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class DualityRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.01 + Math.random() * 50);
      xForm.setColor(0.74488914);
      xForm.setColorSymmetry(0);

      // variation 1
      xForm.addVariation(1.0, getRandomVariation());
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      XFormTransformService.localTranslate(xForm, 2.0 - 4.0 * Math.random(), 2.0 - 4.0 * Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.1 + Math.random() * 400.0);
      xForm.setColor(0.90312262);
      xForm.setColorSymmetry(0.95);

      xForm.addVariation(1.0, getRandomVariation());

      // random affine transforms (uncomment to play around)
      XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, false);
      XFormTransformService.rotate(xForm, 36.0 * Math.random(), false);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }

    if (Math.random() > 0.66) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }

    return flame;
  }

  private VariationFunc getRandomVariation() {
    String name = "";
    while (true) {
      name = VariationFuncList.getRandomVariationname();
      if (!name.startsWith("fract"))
        break;
    }
    return VariationFuncList.getVariationFuncInstance(name, true);
  }

  @Override
  public String getName() {
    return "Duality";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return true;
  }

  @Override
  protected Flame postProcessFlame(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }
}
