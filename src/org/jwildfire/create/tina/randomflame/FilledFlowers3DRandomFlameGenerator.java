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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class FilledFlowers3DRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame createFlame() {
    Flame flame = new Flame();
    flame.setCentreX(0);
    flame.setCentreY(0);
    flame.setCamPitch(37);
    flame.setCamRoll(0);
    flame.setCamYaw(0);
    flame.setPixelsPerUnit(200);
    flame.setCamZoom(2.0);
    flame.setCamPerspective(0.32);
    flame.getFinalXForms().clear();
    flame.getXForms().clear();
    // 1st xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(0.25 + Math.random() * 0.5);
      {
        xForm.addVariation(0.25 + Math.random() * 0.5, VariationFuncList.getVariationFuncInstance("gaussian_blur", true));
      }
      xForm.setColor(0);
      xForm.setColorSymmetry(0);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(1 + Math.random() * 50.0);

      XFormTransformService.localTranslate(xForm, 0.3 - 0.6 * Math.random(), 0.3 - 0.6 * Math.random(), false);
      XFormTransformService.rotate(xForm, 90.0 + Math.random() * 180.0, false);
      XFormTransformService.scale(xForm, 1.25 + Math.random() * 1.25, true, true, false);

      {
        VariationFunc varFunc;
        xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
        xForm.addVariation(0.01 - Math.random() * 0.02, VariationFuncList.getVariationFuncInstance("epispiral_wf", true));
        xForm.addVariation(0.1 + Math.random() * 0.1, VariationFuncList.getVariationFuncInstance("ztranslate", true));
        xForm.addVariation(0.00001 - Math.random() * 0.00002, VariationFuncList.getVariationFuncInstance("zcone", true));
        {
          String fncLst[] = { "bubble", "log" };
          xForm.addVariation(0.001 + Math.random() * 0.099, VariationFuncList.getVariationFuncInstance(fncLst[(int) (fncLst.length * Math.random())], true));
        }
        {
          String fncLst[] = { "arch", "bipolar", "hyperbolic", "butterfly", "cannabiscurve_wf", "cell", "checks", "circlize", "conic",
              "coth", "cpow", "ex", "falloff2", "fan", "flux", "foci", "heart", "kaleidoscope", "log", "mobius", "ngon", "pdj",
              "oscilloscope", "spherical", "spiral" };
          xForm.addVariation(0.001 + Math.random() * 0.099, VariationFuncList.getVariationFuncInstance(fncLst[(int) (fncLst.length * Math.random())], true));
        }
        {
          String fncLst[] = { "waves2", "waves2_wf", "waves3_wf", "waves4_wf" };

          varFunc = VariationFuncList.getVariationFuncInstance(fncLst[(int) (fncLst.length * Math.random())], true);
          varFunc.setParameter("scalex", 0.5 + Math.random());
          varFunc.setParameter("scaley", 0.5 + Math.random());
          varFunc.setParameter("freqx", 1.0 + Math.random() * 2.0);
          varFunc.setParameter("freqy", 1.0 + Math.random() * 2.0);
          xForm.addVariation(0.05 - Math.random() * 0.1, varFunc);
        }
        {
          String fncLst[] = { "cross", "checks", "conic", "kaleidoscope", "lazysusan", "log" };
          xForm.addVariation(0.001 + Math.random() * 0.015, VariationFuncList.getVariationFuncInstance(fncLst[(int) (fncLst.length * Math.random())], true));
        }
      }
      xForm.setColor(0.33);
      xForm.setColorSymmetry(0);
    }
    // blur xForms
    {
      int blurCount = (int) (Math.random() * 5.0);
      double weight = 0.25 + Math.random() * 0.5;
      for (int i = 0; i < blurCount; i++) {
        XForm xForm = new XForm();
        flame.getXForms().add(xForm);
        xForm.setWeight(weight);
        weight *= 0.75;
        xForm.addVariation(0.25 + Math.random() * 0.5, VariationFuncList.getVariationFuncInstance("gaussian_blur", true));
        xForm.setColor(0);
        xForm.setColorSymmetry(0);
        XFormTransformService.localTranslate(xForm, 0.3 - 0.6 * Math.random(), 0.3 - 0.6 * Math.random(), true);

      }
    }
    // satellite xForm
    {
      String fncLst[] = { "blade", "blur", "blur3D", "bubble_wf", "cannabiscurve_wf", "circlecrop", "cloverleaf_wf", "conic", "crop", "cross",
          "flower", "flux", "hemisphere", "hyperbolic", "julia3D", "julia3Dz", "lazysusan", "lissajous", "log", "mandelbrot", "mobius", "npolar",
          "pdj", "perspective", "pie", "pie3D", "polar", "polar2", "power", "pre_subflame_wf", "radial_blur", "scry", "separation", "spiral",
          "spirograph", "split", "tangent", "tangent3D", "twintrian", "unpolar", "wedge_sph", "zblur" };
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(0.1 + Math.random() * 1.4);
      XFormTransformService.localTranslate(xForm, 0.3 - 0.6 * Math.random(), 0.3 - 0.6 * Math.random(), true);
      switch ((int) (Math.random() * 3.0)) {
        case 0:
          xForm.addVariation(0.05 + Math.random() * 0.15, VariationFuncList.getVariationFuncInstance("bubble", true));
          xForm.addVariation(0.01 + Math.random() * 0.5, VariationFuncList.getVariationFuncInstance(fncLst[(int) (fncLst.length * Math.random())], true));
          break;
        case 1:
          xForm.addVariation(0.05 + Math.random() * 0.15, VariationFuncList.getVariationFuncInstance("bubble", true));
          break;
        default:
          xForm.addVariation(0.05 + Math.random() * 0.15, VariationFuncList.getVariationFuncInstance(fncLst[(int) (fncLst.length * Math.random())], true));
          break;
      }

      xForm.addVariation(0.05 + Math.random() * 0.15, VariationFuncList.getVariationFuncInstance("bubble", true));
      xForm.setColor(0);
      xForm.setColorSymmetry(0.8 + Math.random() * 0.2);
    }
    // final xForm
    {
      XForm xForm = new XForm();
      flame.getFinalXForms().add(xForm);
      {
        VariationFunc varFunc;
        xForm.addVariation(0.05 + Math.random() * 0.2, VariationFuncList.getVariationFuncInstance("zscale", true));
        varFunc = VariationFuncList.getVariationFuncInstance("julia3D", true);
        varFunc.setParameter("power", Math.random() < 0.5 ? -3 : -4);
        xForm.addVariation(1, varFunc);
        varFunc = VariationFuncList.getVariationFuncInstance("pre_circlecrop", true);
        varFunc.setParameter("radius", 10000);
        varFunc.setParameter("zero", Math.random() < 0.5 ? 1 : 0);
        xForm.addVariation(1, varFunc);
      }
    }

    return flame;
  }

  @Override
  public String getName() {
    return "Flowers3D (filled)";
  }

}
