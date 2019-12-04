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

public class Spherical3DRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(-0.20);
    flame.setCamPitch(48.0);
    flame.setCamYaw(112.0);
    flame.setCamBank(0.0);
    flame.setCamZoom(3.6);
    flame.setCamPerspective(0.32);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    flame.setPreserveZ(Math.random() < 0.5);

    boolean invert = Math.random() > 0.5;
    VariationFunc varFunc;

    // 1st xForm
    XForm xForm1;
    {
      XForm xForm = xForm1 = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.0 + 4.0 * Math.random());

      varFunc = VariationFuncList.getVariationFuncInstance("spherical3D_wf", true);
      if (invert) {
        varFunc.setParameter("invert", 1);
      }
      xForm.addVariation(1.0, varFunc);
      XFormTransformService.rotate(xForm, Math.random() < 0.5 ? 90.0 : -90.0, false);
      XFormTransformService.globalTranslate(xForm, 1.0, 0.0, false);

      xForm.setColor(1.0);
      xForm.setColorSymmetry(0.9 + Math.random() * 0.1);
    }
    // 2nd xForm
    XForm xForm2;
    {
      XForm xForm = xForm2 = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + Math.random() * 4.5);
      varFunc = VariationFuncList.getVariationFuncInstance("spherical3D_wf", true);
      if (invert) {
        varFunc.setParameter("invert", 1);
      }
      xForm.addVariation(1.0, varFunc);
      XFormTransformService.rotate(xForm, 90.0, false);
      xForm.setColor(0.5);
      xForm.setColorSymmetry(0.9 + Math.random() * 0.1);
    }

    String cylinderVar = Math.random() < 0.5 ? "cylinder" : "cylinder_apo";

    // 3rd xForm
    String fncList[] = { "bipolar", "blade", "blur", "blur3D", "cannabiscurve_wf", "crackle", "cylinder", "cylinder_apo", "edisc", "flower", "glynnSim2",
        "julia3D", "mandelbrot", "modulus", "noise", "parabola", "pie", "pie3D", "checks", "pre_subflame_wf", "radial_blur", "rays",
        "rings", "rose_wf", "secant2", "sinusoidal", "spiral", "spirograph", "splits", "square", "twintrian", "wedge_julia" };
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(0.25 + 0.25 * Math.random(), VariationFuncList.getVariationFuncInstance("pre_blur", true));
      xForm.addVariation(0.01 + Math.random() * 0.39, VariationFuncList.getVariationFuncInstance("ztranslate", true));
      String fnc = Math.random() < 0.66 ? cylinderVar : fncList[(int) (Math.random() * fncList.length)];
      xForm.addVariation(0.01 + Math.random() * 0.39, VariationFuncList.getVariationFuncInstance(fnc, true));
      XFormTransformService.scale(xForm, 5.0, false, true, true);
      xForm.setColor(1.0);
      xForm.setColorSymmetry(-1.0);
    }

    // 4th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.addVariation(0.025 + 0.025 * Math.random(), VariationFuncList.getVariationFuncInstance("pre_blur", true));
      xForm.addVariation(0.01 + Math.random() * 0.39, VariationFuncList.getVariationFuncInstance("ztranslate", true));
      String fnc = Math.random() < 0.33 ? cylinderVar : fncList[(int) (Math.random() * fncList.length)];
      xForm.addVariation(0.01 + Math.random() * 0.39, VariationFuncList.getVariationFuncInstance(fnc, true));
      XFormTransformService.scale(xForm, 3.0, false, true, true);
      xForm.setColor(1.0);
      xForm.setColorSymmetry(-1.0);
    }

    // 5th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.1);
      xForm.addVariation(0.005 + Math.random() * 0.005, VariationFuncList.getVariationFuncInstance("gaussian_blur", true));
      xForm.addVariation(0.005 + Math.random() * 0.005, VariationFuncList.getVariationFuncInstance("ztranslate", true));
      XFormTransformService.globalTranslate(xForm, 0.3, 0.0, true);
      xForm.setColor(1.0);
      xForm.setColorSymmetry(-1.0);
    }
    // 6th (final) xForm
    if (Math.random() < 0.5) {
      XForm xForm = new XForm();
      layer.getFinalXForms().add(xForm);
      xForm.setWeight(0.1);
      int power;
      int style = Tools.randomInt(3);
      switch (style) {
        case 0:
        case 1:
          varFunc = VariationFuncList.getVariationFuncInstance(style == 1 ? "julia3D" : "julia3Dz", true);
          power = -4 + Tools.randomInt(9);
          varFunc.setParameter("power", power != 0 ? power : 0);
          if (Math.random() > 0.5) {
            xForm.addVariation(0.005 + Math.random() * 0.5, VariationFuncList.getVariationFuncInstance("linear3D", true));
          }
          break;
        default:
          varFunc = VariationFuncList.getVariationFuncInstance("spherical3D_wf", true);
          if (Math.random() < 0.5) {
            varFunc.setParameter("invert", 1);
          }
      }
      xForm.addVariation(0.25 + Math.random() * 0.75, varFunc);

      XFormTransformService.rotate(xForm, 45.0, false);
      XFormTransformService.globalTranslate(xForm, 0.0, 0.25, false);
      XFormTransformService.globalTranslate(xForm, 0.0, -0.25, true);
      xForm.setColor(1.0);
      xForm.setColorSymmetry(0.5 + Math.random() * 0.5);

    }
    if (Math.random() > 0.5) {
      xForm1.getModifiedWeights()[3] = 0;
      xForm1.getModifiedWeights()[4] = 0;
      xForm2.getModifiedWeights()[3] = 0;
      xForm2.getModifiedWeights()[4] = 0;
    }
    flame.getFirstLayer().randomizeColors();

    return flame;
  }

  @Override
  public String getName() {
    return "Spherical3D";
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
