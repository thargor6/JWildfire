/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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

import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class OutlinesRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.1 - Math.random() * 0.2);
    flame.setCentreY(0.1 - Math.random() * 0.2);
    flame.setCamZoom(1.05 + Math.random() * 0.15);
    flame.setCamRoll(0);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0.0);
    flame.setCamPerspective(0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(26.32600695);
      xForm.setColor(0.74488914);
      xForm.setColorSymmetry(0);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(1.25325706); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(-0.93791588); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("spherical3D", true));
      // set default edit plane
      flame.setEditPlane(EditPlane.XY);
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(224.48613855);
      xForm.setColor(0.90312262);
      xForm.setColorSymmetry(0.95);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(1.07039553); // a
      xForm.setCoeff10(0.4803511); // b
      xForm.setCoeff20(-0.2742276); // e
      xForm.setCoeff01(-0.4803511); // c
      xForm.setCoeff11(1.07039553); // d
      xForm.setCoeff21(-0.04097021); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      if (Math.random() > 0.1) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("loonie2", true);
        varFunc.setParameter("sides", 3 + (int) (Math.random() * 6));
        varFunc.setParameter("star", 0.1 + Math.random() * 0.3);
        varFunc.setParameter("circle", 0.1 + Math.random() * 0.3);
        xForm.addVariation(1, varFunc);
      }
      else {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(1, varFunc);
      }
      // set default edit plane
      flame.setEditPlane(EditPlane.XY);
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }
    // create transform 3
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(146.3720822);
      xForm.setColor(0.6312262);
      xForm.setColorSymmetry(0.12);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(0.9031166); // a
      xForm.setCoeff10(0.38124873); // b
      xForm.setCoeff20(0.37716179); // e
      xForm.setCoeff01(-0.38124873); // c
      xForm.setCoeff11(0.9031166); // d
      xForm.setCoeff21(-0.53412667); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // change relative weights
      xForm.getModifiedWeights()[2] = 0;

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.4 + Math.random() * 0.2, varFunc);
      }
      // set default edit plane
      flame.setEditPlane(EditPlane.XY);
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, true);
      XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }

    return flame;
  }

  @Override
  public String getName() {
    return "Outlines";
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

  @Override
  public boolean supportsSymmetry() {
    return false;
  }

}
