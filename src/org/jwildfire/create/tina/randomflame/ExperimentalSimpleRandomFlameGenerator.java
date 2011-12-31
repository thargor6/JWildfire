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

public class ExperimentalSimpleRandomFlameGenerator extends RandomFlameGenerator {

  protected static final String FNCLST_EXPERIMENTAL[] = { "blur3D", "bubble", "escher", "rays", "t_epispiral", "curl3D", "diamond",
      "disc", "sech", "loonie", "exp", "cosh", "split", "wedge_sph", "circlize", "t_heart", "bwraps7", "t_colorscale",
      "butterfly3D", "cpow", "conic", "julia3D", "cell", "stripes", "t_post_mirror", "t_colorscale", "crackle",
      "flower", "heart", "julia3D", "disc2", "polar2", "foci", "scry", "flux", "bwraps7", "t_colorscale",
      "hemisphere", "popcorn2", "sec", "lazysusan", "sin", "separation", "t_post_mirror", "t_heart",
      "t_rose", "edisc", "waves2", "twintrian", "coth", "super_shape", "auger", "t_pre_wave3D", "hexes",
      "tanh", "bipolar", "cot", "horseshoe", "wedge", "sinh", "modulus", "mobius", "bwraps7", "t_colorscale",
      "cross", "tangent3D", "csc", "curve", "csch", "bent2", "splits", "whorl", "t_post_mirror",
      "tan", "blob3D", "julia3D", "log", "cos", "oscilloscope", "wedge_julia", "bwraps7", "t_heart", "hexes",
      "pdj", "popcorn", "parabola", "rings2", "spherical3D", "spiral", "rectangles", "elliptic", "waves", "swirl",
      "bwraps7", "t_heart", "t_colorscale",
      "boarders", "secant2" };

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
      XFormTransformService.localTranslate(xForm, Math.random() - 1.0, Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl);

      xForm.setColor(Math.random());
      xForm.addVariation(Math.random() * 0.3 + 0.2, new Linear3DFunc());
      if (Math.random() > 0.1) {
        String[] fnc = FNCLST_EXPERIMENTAL;
        int fncIdx = (int) (Math.random() * fnc.length);
        xForm.addVariation(0.2 + Math.random() * 0.6, VariationFuncList.getVariationFuncInstance(fnc[fncIdx], true));
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
