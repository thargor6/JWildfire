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

public class ExperimentalGnarlRandomFlameGenerator extends RandomFlameGenerator {

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

    double freqX = Math.random() * 2.0 + 7.0;
    double freqY;
    if (Math.random() < 0.25) {
      freqY = freqX;
    }
    else {
      freqY = Math.random() * 2.0 + 7.0;
    }

    double blurAmount = 0.0025 * Math.random();
    double nonBlurAmount = 0.25 * Math.random();
    double wavesWeight = Math.random() * 10 + 75;
    double _2ndWeight = 0.5;
    double _3rdWeight = 0.5;
    double symmetry = 0.6 + Math.random() * 0.4;
    int sides = (int) (Math.random() * 11.0 + 2.0);
    // 1st XForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(wavesWeight);
      double varRnd = Math.random();
      VariationFunc w2;
      if (Math.random() > 0.5) {
        w2 = createExpWaves2Variation(scaleX, scaleY, freqX, freqY, varRnd);
      }
      else {
        if (Math.random() < 0.25) {
          w2 = GnarlRandomFlameGenerator.createWaves2Variation(scaleX, scaleY, freqX, freqY);
        }
        else {
          w2 = GnarlRandomFlameGenerator.createWaves2BVariation(scaleX, scaleY, freqX, freqY);
        }
      }

      xForm.addVariation(1, w2);

      switch (Tools.randomInt(36)) {
        case 0:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("blur", true));
          break;
        case 1:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("cos", true));
          break;
        case 2:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("exp", true));
          break;
        case 3:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("exponential", true));
          break;
        case 4:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("lazysusan", true));
          break;
        case 5:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("ngon", true));
          break;
        case 6:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("sech", true));
          break;
        case 7:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("sinh", true));
          break;
        case 8:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("epispiral_wf", true));
          break;
        case 9:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("tanh", true));
          break;
        case 10:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("twintrian", true));
          break;
        case 11:
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance("bubble", true));
          break;
        case 12:
          xForm.addVariation(nonBlurAmount, VariationFuncList.getVariationFuncInstance("epispiral", true));
          break;
        default: {
          int l = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length;
          String fName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(l)];
          xForm.addVariation(blurAmount, VariationFuncList.getVariationFuncInstance(fName, true));
        }
      }

      xForm.setColorSymmetry(symmetry);
      xForm.setColor(0.9);
      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 0.9 + Math.random() * 0.09, true, true);
      }
      double angle, tx, ty;
      switch (sides) {
        case 2:
          angle = -180;
          tx = Math.random() * 8 - 4;
          ty = Math.random() * 8 - 4;
          break;
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
        case 11:
          angle = -32.73;
          tx = Math.random() * 4.2 - 3;
          ty = Math.random() * 4.2 - 3;
          break;
        case 12:
          angle = -30;
          tx = Math.random() * 4.2 - 3;
          ty = Math.random() * 4.2 - 3;
          break;
        default:
          throw new IllegalStateException();
      }
      XFormTransformService.rotate(xForm, angle);
      XFormTransformService.localTranslate(xForm, tx, ty);
    }
    // 2nd XForm
    XForm secondXForm;
    {
      XForm xForm = secondXForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(_2ndWeight);
      int f = Tools.randomInt(4);
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
        case 3:
          xForm.addVariation(Math.random() * 0.7 + 0.3, VariationFuncList.getVariationFuncInstance("spherical3D", true));
          break;
        default: // nothing to do
          break;
      }
      XFormTransformService.rotate(xForm, 180 - Math.random() * 360.0);
      if (Math.random() > 0.5) {
        XFormTransformService.scale(xForm, 0.5 + Math.random() * 1.0, true, true);
      }
      xForm.setColorSymmetry(-1);
    }
    // 3rd XForm
    if (Math.random() > 0.75) {
      if (Math.random() > 0.5) {
        secondXForm.setWeight(5 + Math.random() * 20.0);
      }
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

  private VariationFunc createExpWaves2Variation(double scaleX, double scaleY, double freqX, double freqY, double varRnd) {
    VariationFunc w2;
    String varName;
    if (varRnd < 0.6) {
      varName = "waves2_wf";
    }
    else if (varRnd < 0.8) {
      varName = "waves3_wf";
    }
    else {
      varName = "waves4_wf";
    }
    w2 = VariationFuncList.getVariationFuncInstance(varName, true);
    if (Math.random() < 0.25) {
      w2.setParameter("use_cos_x", 1);
    }
    else {
      w2.setParameter("use_cos_x", 0);
    }
    if (Math.random() < 0.25) {
      w2.setParameter("use_cos_y", 1);
    }
    else {
      w2.setParameter("use_cos_y", 0);
    }
    if (Math.random() < 0.25) {
      w2.setParameter("dampx", -(0.01 + Math.random() * 0.39));
    }
    if (Math.random() < 0.25) {
      w2.setParameter("dampy", -(0.01 + Math.random() * 0.39));
    }
    w2.setParameter("freqx", freqX);
    w2.setParameter("scalex", scaleX);
    w2.setParameter("freqy", freqY);
    w2.setParameter("scaley", scaleY);
    return w2;
  }

  @Override
  public String getName() {
    return "Gnarl (experimental)";
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
