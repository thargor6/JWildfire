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

public class SphericalRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setCamPitch(0.0);
    flame.setCamRoll(90.0);
    flame.setCamYaw(0.0);
    flame.setCamBank(0.0);
    flame.setCamZoom(2.4);
    flame.setCamPerspective(0.32);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    VariationFunc varFunc;
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(4.0 + 12.0 * Math.random());
      varFunc = VariationFuncList.getVariationFuncInstance("spherical3D", true);
      xForm.addVariation(1.0, varFunc);
      //      XFormTransformService.rotate(xForm, Math.random() < 0.5 ? 90.0 : -90.0, false);
      XFormTransformService.rotate(xForm, Math.random() < 0.5 ? Math.random() < 0.5 ? 180.0 : 90 : -90.0, false);
      XFormTransformService.globalTranslate(xForm, 1.0, 0.0, false);

      xForm.setColor(1.0);
      xForm.setColorSymmetry(0.9 + Math.random() * 0.2);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(3.0 + 11.0 * Math.random());
      varFunc = VariationFuncList.getVariationFuncInstance("spherical3D", true);
      xForm.addVariation(1.0, varFunc);
      //      XFormTransformService.rotate(xForm, 90.0, false);
      XFormTransformService.rotate(xForm, Math.random() < 0.5 ? Math.random() < 0.5 ? 180.0 : 90 : -90.0, false);

      xForm.setColor(0.5);
      xForm.setColorSymmetry(0.9 + Math.random() * 0.2);
    }
    // 3rd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.3 + 0.2 * Math.random());
      varFunc = VariationFuncList.getVariationFuncInstance("linear3D", true);
      xForm.addVariation(1.0, varFunc);
      XFormTransformService.rotate(xForm, 90.0, false);
      XFormTransformService.globalTranslate(xForm, (int) (2.0 + Math.random() * 2.0), 0.0, false);
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(0);
    }
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.2 + 0.15 * Math.random());
      varFunc = VariationFuncList.getVariationFuncInstance("linear3D", true);
      xForm.addVariation(1.0, varFunc);
      XFormTransformService.rotate(xForm, 90.0, false);
      XFormTransformService.globalTranslate(xForm, -(int) (2.0 + Math.random() * 2.0), 0.0, false);
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(0);
    }

    int max = Tools.randomInt(4);
    for (int i = 0; i < max; i++) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + 0.3 * Math.random());
      String fncName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length)];

      varFunc = VariationFuncList.getVariationFuncInstance(fncName, true);
      xForm.addVariation(1.0, varFunc);
      XFormTransformService.rotate(xForm, 90.0 - Math.random() * 180.0, false);
      XFormTransformService.scale(xForm, 0.2 + 0.2 * Math.random(), true, true);
      XFormTransformService.globalTranslate(xForm, 0.25 - Math.random() * 0.5, 0.25 - Math.random() * 0.5, false);
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(0);
    }
    flame.getFirstLayer().randomizeColors();

    return flame;
  }

  @Override
  public String getName() {
    return "Spherical";
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
