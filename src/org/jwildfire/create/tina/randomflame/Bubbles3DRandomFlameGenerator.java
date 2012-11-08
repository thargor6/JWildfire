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

public class Bubbles3DRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame createFlame() {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setCamPitch(49.0);
    flame.setCamYaw(12.0);
    flame.setCamPerspective(0.12);
    flame.setPixelsPerUnit(200);
    flame.getFinalXForms().clear();
    flame.getXForms().clear();
    // 1st xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(1.0);
      xForm.addVariation(0.25 + Math.random() * 0.5, VariationFuncList.getVariationFuncInstance("bubble", true));
      xForm.addVariation(1.5 + 1.5 * Math.random(), VariationFuncList.getVariationFuncInstance("pre_blur", true));
      final String[] fncList = { "fan2", "blade", "blade3D", "blob", "blob3D", "bwraps7", "cell", "cannabiscurve_wf",
          "cloverleaf_wf", "cos", "cot", "coth", "cross", "csch", "diamond", "disc", "edisc", "epispiral_wf", "fan", "fisheye",
          "eyefish", "flux", "heart", "julia", "julian", "juliascope", "log", "parabola", "power", "epispiral",
          "pre_subflame_wf", "rectangles", "rose_wf", "sech", "separation", "split", "truchet", "wedge", "zcone" };

      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(fncList[(int) (fncList.length * Math.random())], true);
      xForm.addVariation(-0.2 + 0.4 * Math.random(), varFunc);

      double shape = Math.random();
      if (shape < 0.25) {
        xForm.addVariation(0.005 + Math.random() * 0.075, VariationFuncList.getVariationFuncInstance("hexes", true));
      }
      else if (shape < 0.5) {
        xForm.addVariation(0.005 + Math.random() * 0.075, VariationFuncList.getVariationFuncInstance("oscilloscope", true));
      }

      xForm.setColor(0.0);
      xForm.setColorSymmetry(0.0);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(1.0);
      int fncCount = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length;
      String fncName = Math.random() > 0.5 ? ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[(int) (fncCount * Math.random())] : "eyefish";
      xForm.addVariation(-1.5 + 2 * Math.random() * 3.0, VariationFuncList.getVariationFuncInstance(fncName, true));
      xForm.addVariation(1.0 - 3.5 * Math.random(), VariationFuncList.getVariationFuncInstance("hemisphere", true));
      xForm.setColor(0.0);
      xForm.setColorSymmetry(0.0);
    }
    // 3rd xForm    
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(1.0);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(Math.random() > 0.5 ? "julia3Dz" : "julia3D", true);
      int idx = Math.random() > 0.5 ? 2 : -7;
      varFunc.setParameter("power", (double) idx);
      xForm.addVariation(1.0, varFunc);
      xForm.setColor(0.0);
      xForm.setColorSymmetry(0.0);
      XFormTransformService.globalTranslate(xForm, -1.0 + 2.0 * Math.random(), -1.0 + 2.0 * Math.random(), false);
      XFormTransformService.globalTranslate(xForm, -0.1 + 0.2 * Math.random(), -0.1 + 0.2 * Math.random(), true);
      XFormTransformService.rotate(xForm, Math.random() * 360.0, false);
      XFormTransformService.scale(xForm, 0.8 + Math.random() * 0.4, true, true);
    }

    flame.randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "Bubbles3D";
  }

}
