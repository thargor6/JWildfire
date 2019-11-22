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

public class SplitsRandomFlameGenerator extends RandomFlameGenerator {

  protected static final String FNCLST_TX1[] = { "bipolar", "boarders", "boarders2", "bubble", "butterfly", "bwraps7",
      "cannabiscurve_wf", "circlize", "cloverleaf_wf", "collideoscope", "cos", "cosh", "cot", "coth", "csc", "csch",
      "cubic3D", "cubicLattice_3D", "dc_perlin", "diamond", "disc2", "eJulia", "edisc", "elliptic", "epispiral", "epispiral_wf",
      "ex", "exp", "exponential", "eyefish", "flower", "foci", "xtrb", "glynnSim1", "glynnia", "heart", "hexnix3D", "julia",
      "julian", "julia3D", "julia3Dz", "juliascope", "layered_spiral", "mobius", "ngon", "phoenix_julia", "pie", "popcorn2_3D",
      "radial_blur", "rays", "ripple", "spherical", "sphericalN", "spiral", "tan", "tanh", "waves2_3D", "wedge_sph",
      "whorl", "xheart" };

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setCamPitch(0.0);
    flame.setCamRoll(00.0);
    flame.setCamYaw(0.0);
    flame.setCamBank(0);
    flame.setCamZoom(2.0);
    flame.setGamma(2.7);
    flame.setCamPerspective(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    VariationFunc varFunc;
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5 + 0.4 * Math.random());
      String fncName = FNCLST_TX1[Tools.randomInt(FNCLST_TX1.length)];
      varFunc = VariationFuncList.getVariationFuncInstance(fncName, true);
      xForm.addVariation(1.0, varFunc);
      XFormTransformService.rotate(xForm, Math.random() < 0.5 ? 90.0 : -90.0, false);
      xForm.setColor(0.60 + Math.random() * 0.20);
      xForm.setColorSymmetry(Math.random() * 0.2);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      varFunc = VariationFuncList.getVariationFuncInstance("splits", true);
      if (Math.random() < 0.25) {
        varFunc.setParameter("x", -0.5 + Math.random() * 1.0);
        varFunc.setParameter("y", -0.5 + Math.random() * 1.0);
      }
      else {
        varFunc.setParameter("x", -1.0 + Math.random() * 4.0);
        varFunc.setParameter("y", 0.0);
      }
      xForm.addVariation(1.0, varFunc);
      XFormTransformService.scale(xForm, 0.6 + Math.random() * 0.8, true, true, false);
      XFormTransformService.scale(xForm, 1.0 + Math.random() * 0.8, true, true, true);
      xForm.setColor(0.40 + Math.random() * 0.20);
    }
    // 3th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.2 + 0.4 * Math.random());
      String fncName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length)];
      //String fncName = Math.random() < 0.5 ? "linear" : "cylinder";
      varFunc = VariationFuncList.getVariationFuncInstance(fncName, true);
      xForm.addVariation(1.0, varFunc);
      //      XFormTransformService.scale(xForm, 5.0 + Math.random() * 10.0, false, true, true);
      //      XFormTransformService.scale(xForm, 0.5 + Math.random() * 0.75, true, false, true);
      //      XFormTransformService.scale(xForm, 0.2 + Math.random() * 0.8, true, true, false);
      XFormTransformService.rotate(xForm, Math.random() < 0.5 ? 90.0 : -90.0, false);
      xForm.setColor(0.60 + Math.random() * 0.20);
      xForm.setColorSymmetry(Math.random() * 0.2);
    }
    // 4th xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.3 + 0.8 * Math.random());
      //String fncName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[(int) (ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length * Math.random())];
      String fncName = Math.random() < 0.5 ? "noise" : "cylinder";
      varFunc = VariationFuncList.getVariationFuncInstance(fncName, true);
      xForm.addVariation(1.0, varFunc);
      XFormTransformService.scale(xForm, 5.0 + Math.random() * 10.0, false, true, true);
      XFormTransformService.scale(xForm, 0.5 + Math.random() * 0.75, true, false, true);
      XFormTransformService.scale(xForm, 0.2 + Math.random() * 0.8, true, true, false);
      xForm.setColor(0.60 + Math.random() * 0.20);
      xForm.setColorSymmetry(Math.random() * 0.2);
    }
    //    flame.randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "Splits";
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
