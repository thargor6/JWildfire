/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class XenomorphRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.1 + Math.random() * 0.4);
      xForm.setColor(0.74488914);
      xForm.setColorSymmetry(0);

      xForm.setCoeff00(0.75610133); // a
      xForm.setCoeff10(-0.74186252); // b
      xForm.setCoeff20(5.25778565); // e
      xForm.setCoeff01(0.74186252); // c
      xForm.setCoeff11(0.75610133); // d
      xForm.setCoeff21(-0.34949139); // f

      xForm.setPostCoeff00(-0.42606416);
      xForm.setPostCoeff10(0.44290131);
      xForm.setPostCoeff01(-0.10610689);
      xForm.setPostCoeff11(-0.40885976);
      xForm.setPostCoeff20(-2.81712);
      xForm.setPostCoeff21(7.390367);

      // variation 1
      xForm.addVariation(1.43, VariationFuncList.getVariationFuncInstance("bubble", true));
      // variation 2
      xForm.addVariation(0.012, VariationFuncList.getVariationFuncInstance("linear", true));
      // variation 3
      if (Math.random() > 0.6) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("radial_blur", true);
        varFunc.setParameter("angle", 0.609835);
        xForm.addVariation(-0.249, varFunc);
      }
      else {
        xForm.addVariation(Math.random() < 0.5 ? -0.249 : 0.5 - Math.random(), VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));
      }
      // variation 4
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("power", true));
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(10.0 + Math.random() * 8.0);
      xForm.setColor(0.90312262);
      xForm.setColorSymmetry(0.95);

      xForm.setCoeff00(-0.85421287); // a
      xForm.setCoeff10(-0.63343313); // b
      xForm.setCoeff20(1.09379129); // e
      xForm.setCoeff01(-0.63343313); // c
      xForm.setCoeff11(0.85421287); // d
      xForm.setCoeff21(-0.20406326); // f

      xForm.setPostCoeff00(0.84389756);
      xForm.setPostCoeff10(-0.35800434);
      xForm.setPostCoeff01(0.43174917);
      xForm.setPostCoeff11(0.89637273);
      xForm.setPostCoeff20(-0.945758);
      xForm.setPostCoeff21(-0.4502584);

      // change relative weights
      xForm.getModifiedWeights()[0] = 2.05;
      xForm.getModifiedWeights()[1] = 1.25;
      xForm.getModifiedWeights()[2] = 0.9;

      // variation 1
      xForm.addVariation(0.008, VariationFuncList.getVariationFuncInstance("linear", true));
      // variation 2
      xForm.addVariation(10.72, VariationFuncList.getVariationFuncInstance("spherical", true));
      // random affine transforms (uncomment to play around)
      XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, false);
      XFormTransformService.rotate(xForm, 36.0 * Math.random(), false);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }
    // create transform 3
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.7);
      xForm.setColor(0.47272985);
      xForm.setColorSymmetry(0);

      xForm.setCoeff00(0.68724662); // a
      xForm.setCoeff10(-0.72642418); // b
      xForm.setCoeff20(-2.14812602); // e
      xForm.setCoeff01(0.72642418); // c
      xForm.setCoeff11(0.68724662); // d
      xForm.setCoeff21(2.39994214); // f

      xForm.setPostCoeff00(0.60646395);
      xForm.setPostCoeff10(0.79511098);
      xForm.setPostCoeff01(-0.79511098);
      xForm.setPostCoeff11(0.60646395);
      xForm.setPostCoeff20(-1.06135064);
      xForm.setPostCoeff21(-0.6369509);

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("bipolar", true);
        varFunc.setParameter("shift", 0);
        xForm.addVariation(1, varFunc);
      }
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      //    XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }

    return flame;
  }

  @Override
  public String getName() {
    return "Xenomorph";
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
