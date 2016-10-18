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
import org.jwildfire.create.tina.variation.JuliaNFunc;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class GnarlRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    // Bases loosely on the W2R Batch Script by parrotdolphin.deviantart.com */ 
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
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

    double freqX, freqY;

    if (Math.random() < 0.5) {
      freqX = Math.random() * 2.0 + 2.0;
      if (Math.random() > 0.5) {
        freqY = freqX;
      }
      else {
        freqY = Math.random() * 2.0 + 2.0;
      }
    }
    else {
      freqX = -Math.random() * 2.0 + 2.0;
      if (Math.random() > 0.5) {
        freqY = freqX;
      }
      else {
        freqY = -Math.random() * 2.0 + 16.0;
      }
    }

    double blurAmount = 0.0025 * Math.random();
    if (Math.random() < 0.33) {
      blurAmount = 0.0 - blurAmount;
    }
    double wavesWeight = Math.random() * 15 + 135;
    double _2ndWeight = 0.5;
    double _3rdWeight = 0.5;
    double symmetry = 0.7 + Math.random() * 0.3;
    int sides = (int) (Math.random() * 8.0 + 3.0);
    // 1st XForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(wavesWeight);
      VariationFunc w2 = Math.random() > 0.89 ? createWaves2Variation(scaleX, scaleY, freqX, freqY) : createWaves2BVariation(scaleX, scaleY, freqX, freqY);
      xForm.addVariation(1 + Math.random() * 0.001, w2);

      String varLst[] = { "blur", "cos", "exp", "exponential", "lazysusan", "ngon", "sech", "sinh", "epispiral_wf", "tanh", "twintrian", "epispiral" };
      String varName = Math.random() > 0.25 ? varLst[(int) (Math.random() * varLst.length)] : VariationFuncList.getRandomVariationname();
      xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance(varName, true));
      xForm.setColorSymmetry(symmetry);
      xForm.setColor(0.9);
      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 0.995, true, true);
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
      XFormTransformService.localTranslate(xForm, tx * 1.5, ty * 1.5);
    }
    // 2nd XForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(_2ndWeight);
      int f = Tools.randomInt(3);
      switch (f) {
        case 0:
          xForm.addVariation(Math.random() * 0.37 + 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          break;
        case 1:
          xForm.addVariation(Math.random() * 0.37 + 0.3, VariationFuncList.getVariationFuncInstance("bubble", true));
          xForm.addVariation(Math.random() * 0.37 + 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          break;
        case 2:
          xForm.addVariation(Math.random() * 0.1, VariationFuncList.getVariationFuncInstance("radial_blur", true));
          JuliaNFunc juliaN = (JuliaNFunc) VariationFuncList.getVariationFuncInstance("julian", true);
          juliaN.setParameter("power", 50 - Math.random() * 100);
          juliaN.setParameter("dist", Math.random() * 10 - 2);
          xForm.addVariation(Math.random() * 0.06 + 0.1, juliaN);
          break;
        default: // nothing to do
          break;
      }
      xForm.setColorSymmetry(-1);
    }
    // 3rd XForm
    if (Math.random() > 0.75) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(_3rdWeight);
      xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      XFormTransformService.rotate(xForm, 180 - Math.random() * 360.0);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random());
      xForm.setColorSymmetry(-1);
    }
    return flame;
  }

  public static VariationFunc createWaves2BVariation(double scaleX, double scaleY, double freqX, double freqY) {
    VariationFunc w2;
    w2 = VariationFuncList.getVariationFuncInstance("waves2b", true);
    double pwx = 3 * Math.random();
    if (pwx < 1) {
      pwx = 1; // typical setting
    }
    else if (pwx < 2) {
      pwx = ((pwx - 1.5) > 0 ? -1.0 : +1.0) * (0.5 + 2.0 * Math.random()); // advanced
    }
    else {
      pwx = ((pwx - 2.5) > 0 ? -1.0 : +1.0) * 1e-6 * (0.9 - Math.random() * 0.2); // geeky functions
    }
    double pwy = 3 * Math.random();
    if (pwy < 1.5) {
      pwy = 1; // typical setting
    }
    else if (pwy < 2.25) {
      pwy = ((pwy - 1.5) > 0 ? -1.0 : +1.0) * (0.5 + 2.0 * Math.random()); // advanced
    }
    else {
      pwy = ((pwy - 2.5) > 0 ? -1.0 : +1.0) * 1e-6; // geeky functions
    }
    w2.setParameter("pwx", pwx);
    w2.setParameter("pwy", pwy);
    double scaleinfx = Math.random() < 0.5 ? scaleX : 0.5 - Math.random();
    double scaleinfy = Math.random() < 0.5 ? scaleY : 0.5 - Math.random();
    w2.setParameter("scaleinfx", scaleinfx);
    w2.setParameter("scaleinfy", scaleinfy);
    if (Math.random() > 0.75) {
      double unity = Math.random() * 10;
      unity = unity < 5.0 ? unity - 1.0 : unity + 5.0;
      w2.setParameter("unity", unity);
    }
    if (Math.random() < 0.33) {
      double jacok = 0.375 - Math.random() * 0.75;
      w2.setParameter("jacok", jacok);
    }
    w2.setParameter("freqx", freqX);
    w2.setParameter("scalex", scaleX);
    w2.setParameter("freqy", freqY);
    w2.setParameter("scaley", scaleY);
    return w2;
  }

  public static VariationFunc createWaves2Variation(double scaleX, double scaleY, double freqX, double freqY) {
    VariationFunc w2;
    w2 = VariationFuncList.getVariationFuncInstance("waves2", true);
    w2.setParameter("freqx", freqX);
    w2.setParameter("scalex", scaleX);
    w2.setParameter("freqy", freqY);
    w2.setParameter("scaley", scaleY);
    return w2;
  }

  @Override
  public String getName() {
    return "Gnarl";
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
