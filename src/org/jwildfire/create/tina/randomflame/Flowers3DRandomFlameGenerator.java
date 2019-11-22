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
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class Flowers3DRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setCamPitch(49.0);
    flame.setCamYaw(12.0);
    flame.setCamBank(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    //    int fncCount = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length;
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + Math.random());
      xForm.addVariation(0.5, VariationFuncList.getVariationFuncInstance("gaussian_blur", true));
      xForm.setColor(0.0);
      xForm.setColorSymmetry(0.0);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(3.0 + Math.random() * 10.0);
      if (Math.random() < 0.33) {
        xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      }
      else {
        VariationFunc f = VariationFuncList.getVariationFuncInstance("linearT3D", true);
        double x = 2.0 * Math.random() - 0.5;
        f.setParameter("powX", x);
        f.setParameter("powY", x);
        f.setParameter("powZ", 2.0 * Math.random() - 0.5);
        xForm.addVariation(1.0, f);
      }
      xForm.addVariation(0.1 + Math.random() * 0.3, VariationFuncList.getVariationFuncInstance("spherical", true));
      xForm.addVariation(0.2 + Math.random() * 0.9, VariationFuncList.getVariationFuncInstance("zcone", true));
      xForm.addVariation(0.01 + 0.045 * Math.random(), VariationFuncList.getVariationFuncInstance("cross", true));
      if (Math.random() < 0.33) {
        VariationFunc ef = VariationFuncList.getVariationFuncInstance("epispiral_wf", true);
        ef.setParameter("waves", 3 + Tools.randomInt(10));
        xForm.addVariation(0.02 + 0.29 * Math.random(), ef);
        if (Math.random() < 0.33) {
          VariationFunc ef2 = VariationFuncList.getVariationFuncInstance("epispiral", true);
          ef2.setParameter("thickness", 0.05 + Math.random() * 0.15);
          ef2.setParameter("n", 3.0 + Math.random() * 10.0);
          xForm.addVariation(0.01 + 0.14 * Math.random(), ef2);
        }
      }
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(Math.random());
      XFormTransformService.scale(xForm, 1.0 + (0.1 - Math.random() * 0.2), true, true, false);
      XFormTransformService.rotate(xForm, 45.0 - Math.random() * 90.0, false);
      XFormTransformService.localTranslate(xForm, 0.01 - 0.02 * Math.random(), 0.01 - 0.02 * Math.random(), false);
    }
    // 3rd xForm
    boolean advStructure = Math.random() > 0.25;
    if (advStructure) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.3 + Math.random() * 0.3);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("blob3D", true);
      varFunc.setParameter("low", 0.1);
      varFunc.setParameter("high", 0.3);
      varFunc.setParameter("waves", 9.0);
      xForm.addVariation(0.05, varFunc);
      xForm.setColor(0.0);
      xForm.setColorSymmetry(1.0);
      // 4th xForm
    }
    if (advStructure && Math.random() > 0.25) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.1 + Math.random() * 0.3);
      xForm.addVariation(0.5, VariationFuncList.getVariationFuncInstance("blur3D", true));
      xForm.addVariation(15.0, VariationFuncList.getVariationFuncInstance("ztranslate", true));
      xForm.setColor(0.0);
      xForm.setColorSymmetry(1.0);
    }
    // final xForm
    {
      XForm xForm = new XForm();
      layer.getFinalXForms().add(xForm);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("julia3D", true);
      double power = -2.0;
      if (Math.random() < 0.25) {
        power -= Math.random() * 4.0;
      }
      varFunc.setParameter("power", power);
      xForm.addVariation(2.0 + (1.0 - 2.0 * Math.random()), varFunc);
      xForm.setColor(0.0);
      xForm.setColorSymmetry(0.0);
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Flowers3D (stunning)";
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
