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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExperimentalSimpleRandomFlameGenerator extends
    RandomFlameGenerator {

  public static final String FNCLST_EXPERIMENTAL[];

  private static final String FNCLST_EXPERIMENTAL_INTERNAL[] = { "blur3D", "bubble",
      "escher", "rays", "epispiral_wf", "curl3D", "diamond", "juliaq", "julia3Dq", "post_juliaq", "post_julia3Dq",
      "cloverleaf_wf", "disc", "sech", "loonie", "exp", "cosh", "split", "waves2_3D",
      "wedge_sph", "circlize", "heart_wf", "bwraps7", "colorscale_wf", "gdoffs", "taurus", "dc_crackle_wf",
      "mandelbrot", "spirograph", "target", "eclipse", "butterfly3D", "cpow", "pre_subflame_wf",
      "conic", "julia3D", "cell", "dc_hexes_wf", "stripes", "post_mirror_wf", "flipcircle", "waves2_3D", "juliac",
      "colorscale_wf", "crackle", "truchet", "cannabiscurve_wf", "cpow", "subflame_wf", "post_smartcrop",
      "glynnSim3", "flower", "fourth", "heart", "julia3D", "disc2", "polar2", "farblur", "waves3_wf", "waves2b",
      "foci", "scry", "flux", "bwraps7", "splitbrdr", "checks", "colorscale_wf", "falloff2", "sinusoidal3d",
      "cloverleaf_wf", "lazyTravis", "kaleidoscope", "eclipse", "hemisphere", "flipy", "phoenix_julia",
      "popcorn2", "sec", "lazysusan", "sin", "separation", "bi_linear", "hexnix3D", "popcorn2_3D", "julian3Dx",
      "post_mirror_wf", "heart_wf", "mcarpet", "mandelbrot", "cannabiscurve_wf", "colormap_wf", "juliac",
      "rose_wf", "edisc", "blocky", "octagon", "murl", "waves2", "twintrian", "coth", "super_shape", "post_colormap_wf", "waves2_3D",
      "auger", "pre_wave3D_wf", "hexes", "dc_hexes_wf", "barycentroid", "spirograph", "truchet", "epispiral", "waves4_wf",
      "glynnSim2", "tanh", "bipolar", "cot", "horseshoe", "target", "wedge", "unpolar", "pre_boarders2",
      "modulus", "mobius", "bubble2", "bwraps7", "colorscale_wf", "truchet", "collideoscope", "xheart", "waves2b",
      "kaleidoscope", "glynnSim2", "twoface", "cross", "tangent3D", "csc", "curve", "boarders2", "julian3Dx",
      "csch", "bent2", "splits", "julian3Dx", "whorl", "xtrb", "post_mirror_wf", "mandelbrot", "sphericalN", "waves2_3D",
      "cloverleaf_wf", "cannabiscurve_wf", "tan", "blob3D", "julia3D", "hypertile1", "svf", "dc_crackle_wf",
      "log", "cos", "oscilloscope", "wedge_julia", "bwraps7", "heart_wf", "linearT3D", "juliac",
      "hexes", "truchet", "spirograph", "glynnSim3", "pdj", "popcorn", "hypertile2", "waves2_3D",
      "parabola", "rings2", "spherical3D", "spiral", "rectangles", "foci_3D", "sintrange", "waves2b",
      "elliptic", "waves", "swirl", "glynnSim1", "eclipse", "bwraps7", "layered_spiral",
      "heart_wf", "colorscale_wf", "boarders", "secant2", "waffle", "lissajous", "hypertile",
      "circus", "lazyTravis", "ovoid3d", "circleblur", "sineblur", "starblur" };

  static {
    List<String> validVariations = new ArrayList<>();
    for(String variationName: FNCLST_EXPERIMENTAL_INTERNAL) {
      if(VariationFuncList.isValidRandomVariation(variationName)) {
        validVariations.add(variationName);
      }
    }
    FNCLST_EXPERIMENTAL = validVariations.toArray(new String[validVariations.size()]);
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    int maxXForms = (int) (1.0 + Math.random() * 5.0);
    double scl = 1.0;
    for (int i = 0; i < maxXForms; i++) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, 360.0 * Math.random());
      }
      else {
        XFormTransformService.rotate(xForm, -360.0 * Math.random());
      }
      XFormTransformService.localTranslate(xForm, Math.random() - 1.0,
          Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl, true, true);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.3 + 0.2, new Linear3DFunc());
      if (Math.random() > 0.1) {
        String[] fnc = FNCLST_EXPERIMENTAL;
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(0.2 + Math.random() * 0.6, VariationFuncList
            .getVariationFuncInstance(fnc[fncIdx], true));
      }

      xForm.setWeight(Math.random() * 0.9 + 0.1);
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Simple (experimental)";
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
