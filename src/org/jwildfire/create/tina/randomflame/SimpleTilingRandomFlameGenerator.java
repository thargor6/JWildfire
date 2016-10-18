/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

public class SimpleTilingRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    // init
    // 1st XForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.4 + Math.random() * 50.0);

      xForm.addVariation(0.01 + 0.4 * Math.random(), VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));
      xForm.addVariation(0.02 + 0.2 * Math.random(), VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColor(0.87 + Math.random() * 0.1);
    }
    // 2nd XForm
    boolean twoPrimaryXForms = Math.random() > 0.33;
    boolean linkedXForms = Math.random() < 0.4;
    if (twoPrimaryXForms) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.4 + Math.random() * 72.0);

      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));
      xForm.setColorSymmetry(-1);
      xForm.setColor(0.89 + Math.random() * 0.06);
      XFormTransformService.scale(xForm, 0.75 + Math.random() * 0.25, Math.random() > 0.125, Math.random() < 0.875);
      XFormTransformService.rotate(xForm, -180.0 + Math.random() * 360.0);
    }
    // Tiling
    int nForms = 2 + Tools.randomInt(5);
    for (int i = 0; i < nForms; i++) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.25 + Math.random() * 0.5);
      xForm.addVariation(0.5 + Math.random() * 0.5, VariationFuncList.getVariationFuncInstance("linear3D", true));
      if (Math.random() > 0.75) {
        XFormTransformService.scale(xForm, 0.125 + Math.random() * 0.5, Math.random() > 0.5, Math.random() < 0.5);
      }
      else {
        XFormTransformService.scale(xForm, 0.75 + Math.random() * 0.25, Math.random() > 0.5, Math.random() < 0.5);
      }
      XFormTransformService.rotate(xForm, -45.0 + Math.random() * 90.0);
      XFormTransformService.localTranslate(xForm, -2.0 + i * 0.5 + Math.random() * 4.0, -2.0 + i * 0.5 + Math.random() * 4.0);
      xForm.setColor(Math.random());
    }
    if (linkedXForms) {
      for (int i = 0; i < layer.getXForms().size(); i++) {
        if (i == 0) {
          for (int j = 0; j < layer.getXForms().size(); j++) {
            layer.getXForms().get(i).getModifiedWeights()[j] = (j != 1) ? 0 : 1;
          }
        }
        else {
          for (int j = 0; j < layer.getXForms().size(); j++) {
            layer.getXForms().get(i).getModifiedWeights()[j] = (j != 1) ? 1 : 0;
          }
        }
      }
    }
    else {
      for (int i = 0; i < layer.getXForms().size(); i++) {
        if (i == 0) {
          double wg = Math.random() * 12.0;
          if (Math.random() < 0.25) {
            wg = 0.0;
          }
          for (int j = 0; j < layer.getXForms().size(); j++) {
            layer.getXForms().get(i).getModifiedWeights()[j] = (j != 1) ? wg : 1;
          }
        }
        else {
          double wg = Math.random() * 12.0;
          if (Math.random() < 0.25) {
            wg = 0.0;
          }
          for (int j = 0; j < layer.getXForms().size(); j++) {
            layer.getXForms().get(i).getModifiedWeights()[j] = (j != 1) ? 1 : wg;
          }
        }
      }
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Simple tiling";
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
