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
import org.jwildfire.create.tina.variation.JuliaNFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.Waves2Func;

public class GnarlRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame createFlame() {
    // Bases loosely on the W2R Batch Script by parrotdolphin.deviantart.com */ 
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();
    // init
    double scaleX = Math.random() * 0.04 + 0.04;
    if (Math.random() > 0.75) {
      scaleX = 0 - scaleX;
    }

    double scaleY = Math.random() * 0.04 + 0.04;
    if (Math.random() > 0.75) {
      scaleY = 0 - scaleY;
    }
    else if (Math.random() < 0.25) {
      scaleY = scaleX;
    }

    double freqX = Math.random() * 2.0 + 2.0;
    double freqY;
    if (Math.random() > 0.5) {
      freqY = freqX;
    }
    else {
      freqY = Math.random() * 2.0 + 2.0;
    }

    double blurAmount = 0.0025 * Math.random();
    double wavesWeight = Math.random() * 20 + 35;
    double _2ndWeight = 0.5;
    double _3rdWeight = 0.5;
    double symmetry = 0.9 + Math.random() * 0.2 - Math.random() * 0.4;
    int sides = (int) (Math.random() * 8.0 + 3.0);
    // 1st XForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(wavesWeight);
      Waves2Func w2 = (Waves2Func) VariationFuncList.getVariationFuncInstance("waves2", true);
      w2.setParameter("freqx", freqX);
      w2.setParameter("scalex", scaleX);
      w2.setParameter("freqy", freqY);
      w2.setParameter("scaley", scaleY);
      xForm.addVariation(1, w2);

      switch ((int) (Math.random() * 11.0)) {
        case 0:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("blur", true));
          break;
        case 1:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("cos", true));
          break;
        case 2:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("exp", true));
          break;
        case 3:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("exponential", true));
          break;
        case 4:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("lazysusan", true));
          break;
        case 5:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("ngon", true));
          break;
        case 6:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("sech", true));
          break;
        case 7:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("sinh", true));
          break;
        case 8:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("t_epispiral", true));
          break;
        case 9:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("tanh", true));
          break;
        case 10:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("twintrian", true));
          break;
        default:
          throw new IllegalStateException();
      }
      xForm.setColorSymmetry(symmetry);
      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 0.995);
      }
      double angle, tx, ty;
      switch (sides) {
        case 3:
          angle = -120;
          tx = Math.random() * 8 - 4;
          ty = Math.random() * 8 - 4;
          break;
        case 4:
          angle = -90;
          tx = Math.random() * 8 - 4;
          ty = Math.random() * 8 - 4;
          break;
        case 5:
          angle = -72;
          tx = Math.random() * 7 - 3.5;
          ty = Math.random() * 7 - 3.5;
          break;
        case 6:
          angle = -60;
          tx = Math.random() * 7 - 3.5;
          ty = Math.random() * 7 - 3.5;
          break;
        case 7:
          angle = -51.42857;
          tx = Math.random() * 6 - 3;
          ty = Math.random() * 6 - 3;
          break;
        case 8:
          angle = -135;
          tx = Math.random() * 6 - 3;
          ty = Math.random() * 6 - 3;
          break;
        case 9:
          angle = -40;
          tx = Math.random() * 5 - 2.5;
          ty = Math.random() * 5 - 2.5;
          break;
        case 10:
          angle = -36;
          tx = Math.random() * 4 - 2;
          ty = Math.random() * 4 - 2;
          break;
        default:
          throw new IllegalStateException();
      }
      XFormTransformService.rotate(xForm, angle);
      XFormTransformService.localTranslate(xForm, tx, ty);
    }
    // 2nd XForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(_2ndWeight);
      int f = (int) (Math.random() * 3);
      switch (f) {
        case 0:
          xForm.addVariation(Math.random() * 0.7 + 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          break;
        case 1:
          xForm.addVariation(Math.random() * 0.7 + 0.3, VariationFuncList.getVariationFuncInstance("bubble", true));
          xForm.addVariation(Math.random() * 0.7 + 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          break;
        case 2:
          xForm.addVariation(Math.random() * 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          JuliaNFunc juliaN = (JuliaNFunc) VariationFuncList.getVariationFuncInstance("julian", true);
          juliaN.setParameter("power", 50 - Math.random() * 100);
          juliaN.setParameter("dist", Math.random() * 10 - 2);
          xForm.addVariation(Math.random() * 0.06 + 0.1, juliaN);
          break;
      }
      xForm.setColorSymmetry(-1);
    }
    // 3rd XForm
    if (Math.random() > 0.5) {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(_3rdWeight);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      XFormTransformService.rotate(xForm, 180 - Math.random() * 360.0);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random());
      xForm.setColorSymmetry(-1);
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Gnarl";
  }

}
