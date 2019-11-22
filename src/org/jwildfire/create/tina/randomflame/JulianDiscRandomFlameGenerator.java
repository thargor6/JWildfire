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

public class JulianDiscRandomFlameGenerator extends RandomFlameGenerator {

  private String getNonBlurRandomFunc() {
    while (true) {
      String res = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length)];
      if (res.indexOf("blur") < 0 && res.indexOf("pre_") < 0 && res.indexOf("post_") < 0 && res.indexOf("prepost_") < 0 
          && !res.equals("flatten") && res.indexOf("inflate") < 0) {
        return res;
      }
    }
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setCamPitch(0.0);
    flame.setCamYaw(0.0);
    flame.setCamBank(0.0);
    flame.setCamZoom(1.0);
    flame.setCamPerspective(0.0);
    flame.setPixelsPerUnit(100);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + Math.random() * 0.5);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("julian", true);
      int power = Tools.FTOI(200.0 - Math.random() * 400.0);
      if (power == 0 || power == 1 || power == -1) {
        power = -30;
      }
      varFunc.setParameter("power", power);
      double amounts[] = { 0.25, 0.5, 0.75 };

      xForm.addVariation(amounts[(int) (Math.random() * amounts.length)], varFunc);
      if (Math.random() < 0.5) {
        String fncName = Math.random() > 0.5 ? getNonBlurRandomFunc() : "gaussian_blur";
        xForm.addVariation(0.001 + Math.random() * 0.039, VariationFuncList.getVariationFuncInstance(fncName, true));
      }
      xForm.setColorSymmetry(-1.0);
      xForm.setColor(0.0);

      if (Math.random() < 0.33) {
        XFormTransformService.globalTranslate(xForm, -0.0125 + 0.025 * Math.random(), -0.0125 + 0.025 * Math.random(), false);
      }
      else if (Math.random() < 0.75) {
        XFormTransformService.globalTranslate(xForm, -0.125 + 0.25 * Math.random(), -0.125 + 0.25 * Math.random(), false);
      }
      if (Math.random() < 0.15) {
        XFormTransformService.rotate(xForm, 6.0 - Math.random() * 12.0, false);
      }
      else if (Math.random() < 0.3) {
        XFormTransformService.rotate(xForm, -45.0, false);
      }
      else if (Math.random() < 0.75) {
        XFormTransformService.rotate(xForm, 90.0 - Math.random() * 180.0, false);
      }

      if (Math.random() < 0.5) {
        XFormTransformService.scale(xForm, 1.2 - Math.random() * 0.4, true, true, false);
      }
      if (Math.random() < 0.5) {
        XFormTransformService.scale(xForm, 1.2 - Math.random() * 0.4, true, true, true);
      }

    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(2.0 + Math.random() * 24.0);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("disc", true));
      if (Math.random() < 0.125) {
        String fncName = Math.random() > 0.5 ? getNonBlurRandomFunc() : "gaussian_blur";
        xForm.addVariation(0.001 + Math.random() * 0.039, VariationFuncList.getVariationFuncInstance(fncName, true));
      }
      xForm.setColor(0.5 + Math.random() * 0.5);
      xForm.setColorSymmetry(0.6 + Math.random() * 0.33);
      if (Math.random() < 0.33) {
        XFormTransformService.globalTranslate(xForm, -0.0125 + 0.025 * Math.random(), -0.0125 + 0.025 * Math.random(), false);
      }
      else if (Math.random() < 0.75) {
        XFormTransformService.globalTranslate(xForm, -0.125 + 0.25 * Math.random(), -0.125 + 0.25 * Math.random(), false);
      }
      if (Math.random() < 0.15) {
        XFormTransformService.rotate(xForm, 6.0 - Math.random() * 12.0, false);
      }
      else if (Math.random() < 0.3) {
        XFormTransformService.rotate(xForm, -45.0, false);
      }
      else if (Math.random() < 0.75) {
        XFormTransformService.rotate(xForm, 90.0 - Math.random() * 180.0, false);
      }

    }
    // final
    if (Math.random() < 0.15) {
      XForm xForm = new XForm();
      layer.getFinalXForms().add(xForm);
      if (Math.random() < 0.5) {
        String[] fncList = { "auger", "bent", "bent2", "boarders", "bubble", "butterfly", "bwraps7", "cosine",
            "curve", "cylinder", "diamond", "disc", "eclipse", "edisc", "elliptic", "ex", "exp", "exponential",
            "eyefish", "fan", "fan2", "fisheye", "heart_wf", "hemisphere", "horseshoe", "hyperbolic", "julia",
            "julia3D", "julia3Dz", "julian", "juliascope", "linearT", "log", "mobius", "ngon", "oscilloscope",
            "rings", "rings2", "scry", "xtrb", "sec", "sin", "sinh", "sinusoidal", "spherical", "swirl", "tan",
            "tangent", "tanh", "boarders2", "polar" };
        String varName = fncList[(int) (Math.random() * fncList.length)];
        xForm.addVariation(2.0 + Math.random() * 2.0, VariationFuncList.getVariationFuncInstance(varName, true));
      }
    }
    flame.getFirstLayer().randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "JulianDisc";
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
