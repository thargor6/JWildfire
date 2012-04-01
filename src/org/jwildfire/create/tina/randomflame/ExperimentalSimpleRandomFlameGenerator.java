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
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class ExperimentalSimpleRandomFlameGenerator extends
    RandomFlameGenerator {

  protected static final String FNCLST_EXPERIMENTAL[] = { "blur3D", "bubble",
      "escher", "rays", "epispiral_wf", "curl3D", "diamond",
      "cloverleaf_wf", "disc", "sech", "loonie", "exp", "cosh", "split",
      "wedge_sph", "circlize", "heart_wf", "bwraps7", "colorscale_wf",
      "mandelbrot", "spirograph", "eclipse", "butterfly3D", "cpow",
      "conic", "julia3D", "cell", "stripes", "post_mirror_wf",
      "colorscale_wf", "crackle", "truchet", "cannabiscurve_wf",
      "glynnSim3", "flower", "heart", "julia3D", "disc2", "polar2",
      "foci", "scry", "flux", "bwraps7", "colorscale_wf",
      "cloverleaf_wf", "kaleidoscope", "eclipse", "hemisphere",
      "popcorn2", "sec", "lazysusan", "sin", "separation",
      "post_mirror_wf", "heart_wf", "mandelbrot", "cannabiscurve_wf",
      "rose_wf", "edisc", "waves2", "twintrian", "coth", "super_shape",
      "auger", "pre_wave3D_wf", "hexes", "spirograph", "truchet",
      "glynnSim2", "tanh", "bipolar", "cot", "horseshoe", "wedge",
      "modulus", "mobius", "bwraps7", "colorscale_wf", "truchet",
      "kaleidoscope", "glynnSim2", "cross", "tangent3D", "csc", "curve",
      "csch", "bent2", "splits", "whorl", "post_mirror_wf", "mandelbrot",
      "cloverleaf_wf", "cannabiscurve_wf", "tan", "blob3D", "julia3D",
      "log", "cos", "oscilloscope", "wedge_julia", "bwraps7", "heart_wf",
      "hexes", "truchet", "spirograph", "glynnSim3", "pdj", "popcorn",
      "parabola", "rings2", "spherical3D", "spiral", "rectangles",
      "elliptic", "waves", "swirl", "glynnSim1", "eclipse", "bwraps7",
      "heart_wf", "colorscale_wf", "boarders", "secant2", "waffle", "lissajous" };

  @Override
  protected Flame createFlame() {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();

    int maxXForms = (int) (1.0 + Math.random() * 5.0);
    double scl = 1.0;
    for (int i = 0; i < maxXForms; i++) {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
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

}
