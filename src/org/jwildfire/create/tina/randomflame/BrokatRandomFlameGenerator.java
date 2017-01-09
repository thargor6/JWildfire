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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class BrokatRandomFlameGenerator extends RandomFlameGenerator {

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
      xForm.setWeight(1.5 + Math.random() * 1.0);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("curl", true);
      varFunc.setParameter("c1", -1.0);
      varFunc.setParameter("c2", 0.001 + Math.random() * 0.0199);
      xForm.addVariation(1.6 + Math.random() * 0.8, varFunc);

      xForm.setColor(0.4 + Math.random() * 0.2);
      xForm.setColorSymmetry(0.82 + Math.random() * 0.16);
      XFormTransformService.rotate(xForm, 180, false);
      XFormTransformService.localTranslate(xForm, 1.0, 0.0, true);

      xForm.getModifiedWeights()[0] = 0.0;
      xForm.getModifiedWeights()[1] = 1.0;
      xForm.getModifiedWeights()[2] = 0.0;
      xForm.getModifiedWeights()[3] = 0.0;
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.05 + Math.random() * 0.35);
      String fncNames[] = { "juliascope", "julia3D", "julia3Dz", "julian" };
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(fncNames[Tools.randomInt(fncNames.length)], true);
      varFunc.setParameter("power", Math.random() < 0.33 ? 2 : Math.random() < 0.5 ? 3 : 4);
      xForm.addVariation(1.0, varFunc);
      xForm.setColor(0.5 + Math.random() * 0.5);
      xForm.setColorSymmetry(0.5);
      xForm.getModifiedWeights()[0] = 1.0;
      xForm.getModifiedWeights()[1] = 0.0;
      xForm.getModifiedWeights()[2] = 1.0;
      xForm.getModifiedWeights()[3] = 1.0;
    }

    // 3rd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.4 + Math.random() * 0.2);
      String fncName;
      if (Math.random() < 0.33) {
        fncName = "bubble";
      }
      else {
        fncName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length)];
      }
      xForm.addVariation(0.01 + Math.random() * 0.04, VariationFuncList.getVariationFuncInstance(fncName, true));
      xForm.addVariation(5.0 + Math.random() * 10.0, VariationFuncList.getVariationFuncInstance("pre_blur", true));
      xForm.setColor(0.1 + Math.random() * 0.3);
      xForm.setColorSymmetry(0);
      XFormTransformService.localTranslate(xForm, -1.0, 0.0, true);
      xForm.getModifiedWeights()[0] = 1.0;
      xForm.getModifiedWeights()[1] = 1.0;
      xForm.getModifiedWeights()[2] = 0.0;
      xForm.getModifiedWeights()[3] = 0.0;

    }
    // 4th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.4 + Math.random() * 0.2);
      String fncName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length)];
      xForm.addVariation(0.01 + Math.random() * 0.04, VariationFuncList.getVariationFuncInstance(fncName, true));
      if (Math.random() > 0.5) {
        xForm.addVariation((0.01 + Math.random() * 0.04) * 0.5, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true)).setPriority(-1);
      }

      xForm.setColor(0.1 + Math.random() * 0.3);
      xForm.setColorSymmetry(0);
      XFormTransformService.rotate(xForm, Math.random() * 360.0, true);
      XFormTransformService.scale(xForm, 1.1 + Math.random() * 3.0, true, true, true);
      xForm.getModifiedWeights()[0] = 1.0;
      xForm.getModifiedWeights()[1] = 1.0;
      xForm.getModifiedWeights()[2] = 1.0;
      xForm.getModifiedWeights()[3] = 1.0;

    }

    return flame;
  }

  @Override
  public String getName() {
    return "Brokat";
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
