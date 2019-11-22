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
import org.jwildfire.create.tina.randomgradient.TwoColorsRandomGradientGenerator;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class RaysRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCamRoll(Math.random() * 360.0);
    flame.setCamZoom(1.0 + Math.random() * 0.75);
    flame.setGamma(1.2 + Math.random() * 0.8);
    flame.setGammaThreshold(Math.random() * 0.15);
    flame.setBrightness(2.5 + Math.random() * 1.5);

    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0.0);
    flame.setCamPerspective(0);
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    // create a random gradient
    layer.setPalette(new TwoColorsRandomGradientGenerator().generatePalette(2, true, true));

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.1 + Math.random() * 0.7);
      xForm.setColor(0);
      xForm.setColorSymmetry(0);

      xForm.setCoeff00(-0.43686351); // a
      xForm.setCoeff10(0.63649767); // b
      xForm.setCoeff20(0.7325555); // e
      xForm.setCoeff01(-0.59864436); // c
      xForm.setCoeff11(-0.50111868); // d
      xForm.setCoeff21(0.08008955); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      xForm.addVariation(0.125 + Math.random() * 0.125, VariationFuncList.getVariationFuncInstance("linear", true));
      // variation 2
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("julian", true);
        varFunc.setParameter("power", 3);
        varFunc.setParameter("dist", 1);
        xForm.addVariation(0.6 + Math.random() * 0.4, varFunc);
      }
      // variation 3
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("juliascope", true);
        varFunc.setParameter("power", 6);
        varFunc.setParameter("dist", 1);
        xForm.addVariation(0.6 + Math.random() * 0.3, varFunc);
      }
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);
      XFormTransformService.scale(xForm, 0.9 + Math.random() * 0.5, true, true, false);
      XFormTransformService.rotate(xForm, 360.0 * Math.random(), false);
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.23272111);
      xForm.setColor(0.8);
      xForm.setColorSymmetry(0);

      xForm.setCoeff00(-0.15514012); // a
      xForm.setCoeff10(0.20816567); // b
      xForm.setCoeff20(0.19004075); // e
      xForm.setCoeff01(-0.15087636); // c
      xForm.setCoeff11(0.20174952); // d
      xForm.setCoeff21(-0.04671182); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear", true));
      // variation 2
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("juliascope", true);
        varFunc.setParameter("power", 2);
        varFunc.setParameter("dist", 1);
        xForm.addVariation(0.25 + Math.random() * 0.5, varFunc);
      }

      String fName;
      if (Math.random() < 0.33) {
        int idx = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length;
        fName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(idx)];
      }
      else {
        fName = VariationFuncList.getRandomVariationname();
      }
      xForm.addVariation(0.18 - Math.random() * 0.36, VariationFuncList.getVariationFuncInstance(fName, true));
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);
      XFormTransformService.scale(xForm, 0.9 + Math.random() * 0.5, true, true, false);
      XFormTransformService.rotate(xForm, 360.0 * Math.random(), false);
    }
    // create transform 3
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.04166667);
      xForm.setColor(0.1);
      xForm.setColorSymmetry(0.75);

      xForm.setCoeff00(0.92931929); // a
      xForm.setCoeff10(0.17076568); // b
      xForm.setCoeff20(0.15869232); // e
      xForm.setCoeff01(-0.02999303); // c
      xForm.setCoeff11(0.67104838); // d
      xForm.setCoeff21(-0.19510192); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance(Math.random() > 0.5 ? "linear" : getRandomVariationName(), true));
    }
    // create transform 4
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.04166667);
      xForm.setColor(0.15);
      xForm.setColorSymmetry(0.75);

      xForm.setCoeff00(0.53699507); // a
      xForm.setCoeff10(0.12206942); // b
      xForm.setCoeff20(-0.0380713); // e
      xForm.setCoeff01(0.23469299); // c
      xForm.setCoeff11(0.79862481); // d
      xForm.setCoeff21(0.04291273); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance(Math.random() > 0.5 ? "linear" : getRandomVariationName(), true));
    }
    // create transform 5
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.04166667);
      xForm.setColor(0.2);
      xForm.setColorSymmetry(0);

      xForm.setCoeff00(0.89779523); // a
      xForm.setCoeff10(-0.23112747); // b
      xForm.setCoeff20(-0.4497571); // e
      xForm.setCoeff01(0.1734196); // c
      xForm.setCoeff11(0.94619577); // d
      xForm.setCoeff21(-0.0672393); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance(Math.random() > 0.5 ? "linear" : getRandomVariationName(), true));
    }
    // create transform 6
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.04166667);
      xForm.setColor(0.25);
      xForm.setColorSymmetry(0);

      xForm.setCoeff00(0.70624241); // a
      xForm.setCoeff10(0.15652237); // b
      xForm.setCoeff20(0.43007701); // e
      xForm.setCoeff01(-0.04953437); // c
      xForm.setCoeff11(0.78838922); // d
      xForm.setCoeff21(0.21371425); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance(Math.random() > 0.5 ? "linear" : getRandomVariationName(), true));
    }
    // final transform
    if (Math.random() > 0.33) {
      XForm xForm = new XForm();
      layer.getFinalXForms().add(xForm);
      String finals[] = { "falloff2", "bwrands", "bwraps7", "cosine", "falloff3", "bwrands", "bwrands", "falloff2" };
      VariationFunc var = VariationFuncList.getVariationFuncInstance(finals[(int) (Math.random() * finals.length)], true);
      if (var.getName().equals("bwrands")) {
        var.setParameter("seed", Tools.randomInt(Short.MAX_VALUE));
        var.setParameter("minpetals", (int) (3 + Math.random() * 3));
        var.setParameter("maxpetals", (int) (6 + Math.random() * 12));
      }
      if (var.getName().equals("bwrands") || var.getName().equals("bwraps7")) {
        var.setParameter("cellsize", 1.0 - Math.random() * 0.9);
        var.setParameter("space", Math.random() * 0.2);
        if (Math.random() > 0.5) {
          var.setParameter("inner_twist", 0.2 - Math.random() * 0.4);
          var.setParameter("outer_twist", 2.0 - Math.random() * 4.0);
        }
      }
      xForm.addVariation(1, var);
    }
    return flame;
  }

  private String getRandomVariationName() {
    while (true) {
      String name = VariationFuncList.getRandomVariationname();
      if (!name.startsWith("fract") && !name.startsWith("inflate") && !name.startsWith("pre_") && !name.startsWith("post_") 
          && !name.startsWith("prepost_") && !name.equals("flatten")) {
        return name;
      }
    }
  }

  @Override
  public String getName() {
    return "Rays";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return Math.random() > 0.5;
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
