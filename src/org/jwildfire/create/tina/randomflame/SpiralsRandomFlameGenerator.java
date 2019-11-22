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

public class SpiralsRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    flame.setCentreX(-Math.random() * 0.5);
    flame.setCentreY(-Math.random() * 0.5);
    flame.setPixelsPerUnit(200);
    flame.setCamRoll(90.0 - Math.random() * 180);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0);
    flame.setCamPerspective(0);
    flame.setCamZoom(2.0);

    Layer layer = flame.getFirstLayer();
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);

      xForm.setWeight(25.0 + Math.random() * 55.0);
      xForm.setColor(0.9);
      xForm.setColorSymmetry(0.93185856);

      xForm.setCoeff00(0.23168009); // a
      xForm.setCoeff10(-0.87153216); // b
      xForm.setCoeff20(-1.09851548); // e
      xForm.setCoeff01(1.01859563); // c
      xForm.setCoeff11(0.23718475); // d
      xForm.setCoeff21(0.30609214); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("waves2", true);
        varFunc.setParameter("scalex", 0.04933602 + Math.random() * 0.04);
        varFunc.setParameter("scaley", 0.06933602);
        varFunc.setParameter("freqx", 2.98088993);
        varFunc.setParameter("freqy", 2.98088993);
        xForm.addVariation(1, varFunc);
      }
      // variation 2
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.001 + Math.random() * 0.001, varFunc);
      }
      // variation 3
      if (Math.random() < 0.33) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.0001 + Math.random() * 0.0001, varFunc).setPriority(-1);
      }

      xForm.setColor(0.4 + Math.random() * 0.2);
      xForm.setColorSymmetry(0.82 + Math.random() * 0.16);
      //      XFormTransformService.rotate(xForm, 180, false);
      //      XFormTransformService.scale(xForm, 2.0 + Math.random() * 25.0, true, true, false);
      XFormTransformService.localTranslate(xForm, 0.5 * (0.5 - 1.0 * Math.random()), 0.5 - 1.0 * Math.random(), true);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);

      xForm.setWeight(0.5);
      xForm.setColor(0);
      xForm.setColorSymmetry(-1);

      xForm.setCoeff00(1.1144261); // a
      xForm.setCoeff10(-0.1144261); // b
      xForm.setCoeff20(-0.1144261); // e
      xForm.setCoeff01(0.03033403); // c
      xForm.setCoeff11(0.96966597); // d
      xForm.setCoeff21(-0.03033403); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.2 + Math.random() * 0.2, varFunc);
      }
      // variation 2
      if (Math.random() > 0.42) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.1 + Math.random() * 0.1, varFunc).setPriority(-1);
      }
      // variation 3
      if (Math.random() > 0.42) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.0001 + Math.random() * 0.0001, varFunc).setPriority(+1);
      }

      xForm.setColor(Math.random());
      xForm.setColorSymmetry(Math.random());
    }

    return flame;
  }

  @Override
  public String getName() {
    return "Spirals";
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
