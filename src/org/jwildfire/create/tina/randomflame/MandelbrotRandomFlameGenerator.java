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
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class MandelbrotRandomFlameGenerator extends RandomFlameGenerator {

  protected static final String FNCLST_FINAL[] = { "bipolar", "boarders", "boarders2", "bubble", "butterfly", "bwraps7",
      "circlize", "collideoscope", "cosh", "cross", "curl", "curl3D", "curve", "eJulia", "edisc", "elliptic",
      "eyefish", "flux", "foci", "foci_3D", "glynnia", "heart_wf", "xtrb", "hemisphere", "horseshoe", "hypertile",
      "hypertile1", "hypertile2", "julia", "julian", "juliascope", "loonie", "loonie_3D", "mobius",
      "npolar", "phoenix_julia", "popcorn2_3D", "power", "ripple", "scry", "scry_3D", "sec", "sech", "separation",
      "spherical", "spiral", "stripes", "unpolar", "waves2", "whorl", "xheart" };

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.45);
    flame.setCamPitch(49.0);
    flame.setCamRoll(00.0);
    flame.setCamYaw(0.0);
    flame.setCamBank(0.0);
    flame.setCamZoom(1.0);
    flame.setGamma(2.0);
    flame.setCamPerspective(0.05 + Math.random() * 0.12);
    flame.setPixelsPerUnit(200);
    flame.setPreserveZ(true);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    VariationFunc varFunc;
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      int varId = Tools.randomInt(6);
      switch (varId) {
        case 0:
          varFunc = VariationFuncList.getVariationFuncInstance("fract_dragon_wf", true);
          varFunc.setParameter("scale", 2.0);
          varFunc.setParameter("xmin", -2.0);
          varFunc.setParameter("xmax", 2.0);
          varFunc.setParameter("ymin", -2.0);
          varFunc.setParameter("ymax", 2.0);
          varFunc.setParameter("xseed", -1.0 + 2.0 * Math.random());
          varFunc.setParameter("yseed", -1.0 + 2.0 * Math.random());
          break;
        case 1:
          varFunc = VariationFuncList.getVariationFuncInstance("fract_julia_wf", true);
          varFunc.setParameter("scale", 2.0);
          varFunc.setParameter("xmin", -2.0);
          varFunc.setParameter("xmax", 2.0);
          varFunc.setParameter("ymin", -2.0);
          varFunc.setParameter("ymax", 2.0);
          varFunc.setParameter("xseed", -1.0 + 2.0 * Math.random());
          varFunc.setParameter("yseed", -1.0 + 2.0 * Math.random());
          break;
        case 2:
          varFunc = VariationFuncList.getVariationFuncInstance("fract_pearls_wf", true);
          varFunc.setParameter("scale", 2.0);
          varFunc.setParameter("xmin", -2.0);
          varFunc.setParameter("xmax", 2.0);
          varFunc.setParameter("ymin", -2.0);
          varFunc.setParameter("ymax", 2.0);
          varFunc.setParameter("xseed", -1.0 + 2.0 * Math.random());
          varFunc.setParameter("yseed", -1.0 + 2.0 * Math.random());
          break;
        case 3:
          varFunc = VariationFuncList.getVariationFuncInstance("fract_salamander_wf", true);
          varFunc.setParameter("scale", 2.0);
          varFunc.setParameter("xmin", -2.0);
          varFunc.setParameter("xmax", 2.0);
          varFunc.setParameter("ymin", -2.0);
          varFunc.setParameter("ymax", 2.0);
          varFunc.setParameter("xseed", -1.0 + 2.0 * Math.random());
          varFunc.setParameter("yseed", -1.0 + 2.0 * Math.random());
          break;
        case 4:
          varFunc = VariationFuncList.getVariationFuncInstance("fract_mandelbrot_wf", true);
          break;
        default:
          varFunc = VariationFuncList.getVariationFuncInstance("fract_meteors_wf", true);
          break;
      }
      if (Math.random() < 0.8) {
        double scale = 3 + Math.random() * 3;
        double xmin = -1.0 + 2.0 * Math.random();
        double ymin = -1.0 + 2.0 * Math.random();
        double xmax = xmin + 4.0 / scale;
        double ymax = ymin + 4.0 / scale;

        varFunc.setParameter("xmin", xmin);
        varFunc.setParameter("xmax", xmax);
        varFunc.setParameter("ymin", ymin);
        varFunc.setParameter("ymax", ymax);
        varFunc.setParameter("offsetx", -(xmax - xmin) * 0.5);
        varFunc.setParameter("offsety", -(ymax - ymin) * 0.5);
        varFunc.setParameter("scale", 2.0 * scale);

      }
      varFunc.setParameter("scalez", 1.0 + Math.random() * 10.0);
      xForm.addVariation(1.0, varFunc);
    }
    // final
    if (Math.random() < 0.75) {
      XForm xForm = new XForm();
      layer.getFinalXForms().add(xForm);
      varFunc = VariationFuncList.getVariationFuncInstance(FNCLST_FINAL[(int) (Math.random() * FNCLST_FINAL.length)], true);
      xForm.addVariation(1.0, varFunc);
    }
    flame.getFirstLayer().randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "Mandelbrot";
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
