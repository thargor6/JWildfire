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

public class BubblesRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setCamZoom(0.5);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    int fncCount = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length;
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(12.0 + Math.random() * 80.0);
      xForm.addVariation(2 + Math.random() * 4, VariationFuncList.getVariationFuncInstance("spherical", true));

      String fName;
      if (Math.random() < 0.15) {
        fName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(fncCount)];
      }
      else {
        fName = Math.random() < 0.8 ? "eyefish" : "fisheye";
      }
      xForm.addVariation(0.05 + Math.random() * 0.5, VariationFuncList.getVariationFuncInstance(fName, true));
      xForm.setColorSymmetry(0.991 + Math.random() * 0.08);
      XFormTransformService.scale(xForm, 0.5 - Math.random() * 0.5, true, true, false);
      XFormTransformService.rotate(xForm, 180 - Math.random() * 360.0, false);
      XFormTransformService.localTranslate(xForm, 3.0 - 6.0 * Math.random(), 3.0 - 6.0 * Math.random(), false);
      if (Math.random() < 0.33) {
        XFormTransformService.localTranslate(xForm, 0.75 - 1.5 * Math.random(), 0.75 - 1.5 * Math.random(), true);
      }
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + Math.random() * 0.8);
      if (Math.random() < 0.05) {
        xForm.addVariation(0.01 + Math.random() * 0.4, VariationFuncList.getVariationFuncInstance("bubble", true));
        VariationFunc checks = VariationFuncList.getVariationFuncInstance("checks", true);
        checks.setParameter("size", 5.0);
        checks.setParameter("x", 3.0);
        checks.setParameter("y", 3.0);
        xForm.addVariation(0.01 + Math.random() * 0.04, checks);
      }
      else {
        xForm.addVariation(0.1 + Math.random() * 0.5, VariationFuncList.getVariationFuncInstance("bubble", true));
      }
      xForm.addVariation(4 + Math.random() * 2, VariationFuncList.getVariationFuncInstance("pre_blur", true));
      xForm.setColorSymmetry(-0.5);
      XFormTransformService.scale(xForm, 1.1 + Math.random() * 1.9, true, true, false);
      XFormTransformService.localTranslate(xForm, 0.75 - 1.50 * Math.random(), 0.75 - 1.50 * Math.random(), false);
      XFormTransformService.rotate(xForm, 30 - Math.random() * 60.0, false);
    }

    // 3rd xForm
    if (Math.random() > 0.25) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + Math.random() * 1.5);
      String fName;
      if (Math.random() > 0.8) {
        fName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(fncCount)];
      }
      else {
        fName = Math.random() > 0.5 ? "linear3D" : "noise";
      }
      xForm.addVariation(0.5, VariationFuncList.getVariationFuncInstance(fName, true));
      xForm.setColorSymmetry(-0.5);
      XFormTransformService.rotate(xForm, 30.0 - Math.random() * 60.0, false);
      if (Math.random() < 0.5) {
        XFormTransformService.scale(xForm, 0.5 + Math.random() * 1.5, true, true, false);
      }
    }
    // 4th xForm
    if (Math.random() > 0.5) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + Math.random() * 1.5);
      String fName;
      if (Math.random() > 0.8) {
        fName = Math.random() > 0.75 ? VariationFuncList.getRandomVariationname() : ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(fncCount)];
      }
      else {
        fName = Math.random() > 0.5 ? "linear3D" : "gaussian_blur";
      }
      xForm.addVariation(0.5, VariationFuncList.getVariationFuncInstance(fName, true));
      xForm.setColorSymmetry(-0.5);
      XFormTransformService.rotate(xForm, 30.0 - Math.random() * 60.0, false);
      if (Math.random() < 0.5) {
        XFormTransformService.scale(xForm, 0.15 + Math.random() * 1.25, true, true, false);
      }
    }
    flame.getFirstLayer().distributeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "Bubbles";
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
