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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class JulianDiscRandomFlameGenerator extends RandomFlameGenerator {

  private String getNonBlurRandomFunc() {
    while (true) {
      String res = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[(int) (ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length * Math.random())];
      if (res.indexOf("blur") < 0) {
        return res;
      }
    }
  }

  @Override
  protected Flame createFlame() {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setCamPitch(0.0);
    flame.setCamYaw(0.0);
    flame.setCamZoom(1.0);
    flame.setCamPerspective(0.0);
    flame.setPixelsPerUnit(100);
    flame.setFinalXForm(null);
    flame.getXForms().clear();
    // 1st xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(0.5);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("julian", true);
      int power = Tools.FTOI(100.0 - Math.random() * 200.0);
      if (power == 0 || power == 1 || power == -1) {
        power = -30;
      }
      varFunc.setParameter("power", power);
      xForm.addVariation(0.5, varFunc);
      //      {
      //        String fncName = Math.random() > 0.5 ? getNonBlurRandomFunc() : "gaussian_blur";
      //        xForm.addVariation(0.001 + Math.random() * 0.039, VariationFuncList.getVariationFuncInstance(fncName, true));
      //      }
      xForm.setColor(0.0);
      xForm.setColorSymmetry(0.0);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(2.0 + Math.random() * 24.0);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("disc", true));
      if (Math.random() < 0.33) {
        String fncName = Math.random() > 0.5 ? getNonBlurRandomFunc() : "gaussian_blur";
        xForm.addVariation(0.001 + Math.random() * 0.039, VariationFuncList.getVariationFuncInstance(fncName, true));
      }
      xForm.setColor(0.5 + Math.random() * 0.5);
      xForm.setColorSymmetry(0.6 + Math.random() * 0.33);
      if (Math.random() < 0.5) {
        XFormTransformService.globalTranslate(xForm, Math.random() < 0.5 ? -0.025 + 0.05 * Math.random() : -0.5 + Math.random(), Math.random() >= 0.5 ? -0.025 + 0.05 * Math.random() : -0.5 + Math.random(), false);
      }
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, 6.0 - Math.random() * 12.0, false);
      }
      XFormTransformService.scale(xForm, 0.2 + Math.random() * 1.2, true, true, false);
    }
    // final
    if (Math.random() < 0.5) {
      XForm xForm = new XForm();
      flame.setFinalXForm(xForm);
      if (Math.random() < 0.5) {
        String varName = Math.random() < 0.67 ? "polar" : "polar2";
        xForm.addVariation(2.0 + Math.random() * 2.0, VariationFuncList.getVariationFuncInstance(varName, true));
        double dy = -0.5 + 2.5 * Math.random();
        XFormTransformService.globalTranslate(xForm, 0, dy, true);
        flame.setCentreY(-(2.0 - dy));
      }
      else {
        String[] fncList = { "auger", "bent", "bent2", "boarders", "bubble", "butterfly", "bwraps7", "cosine",
            "curve", "cylinder", "diamond", "disc", "eclipse", "edisc", "elliptic", "ex", "exp", "exponential",
            "eyefish", "fan", "fan2", "fisheye", "heart_wf", "hemisphere", "horseshoe", "hyperbolic", "julia",
            "julia3D", "julia3Dz", "julian", "juliascope", "linearT", "log", "mobius", "ngon", "oscilloscope",
            "rings", "rings2", "scry", "sec", "sin", "sinh", "sinusoidal", "spherical", "swirl", "tan", "tangent",
            "tanh", "boarders2" };
        String varName = fncList[(int) (Math.random() * fncList.length)];
        xForm.addVariation(2.0 + Math.random() * 2.0, VariationFuncList.getVariationFuncInstance(varName, true));
      }
    }
    flame.randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "JulianDisc";
  }

}
