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

public class RasterRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(2.45);
    flame.setCentreY(1.37);
    flame.setCamZoom(1.25 + Math.random() * 0.5);
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
      xForm.setWeight(0.05);
      xForm.setColor(0);
      xForm.setColorSymmetry(0.89103422);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(0); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(0); // f

      xForm.setPostCoeff00(0.45811152);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(0.45811152);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.5 + Math.random() * 0.65, varFunc);
      }
      // variation 2
      if (Math.random() < 0.20) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.4 + Math.random() * 0.5, varFunc);
      }
      // variation 3
      if (Math.random() < 0.10) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.3 + Math.random() * 0.4, varFunc);
      }
      // set default edit plane
      flame.setEditPlane(EditPlane.XY);
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, 360.0 * Math.random(), false);
      }
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
      // random affine post transforms (uncomment to play around)
      XFormTransformService.scale(xForm, 1.15 - Math.random() * 0.65, true, true, true);
      XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), true);
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColor(0.33333333);
      xForm.setColorSymmetry(0.62668861);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(0); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(0); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(1);
      xForm.setPostCoeff21(0);

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
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
    // create transform 3
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColor(0.66666667);
      xForm.setColorSymmetry(0.33786964);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(0); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(0); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(1);

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
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
    // create transform 4
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.05);
      xForm.setColor(1);
      xForm.setColorSymmetry(0.95291095);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(0); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(0); // f

      xForm.setPostCoeff00(0.0423868);
      xForm.setPostCoeff10(0.14299816);
      xForm.setPostCoeff01(-0.14299816);
      xForm.setPostCoeff11(0.0423868);
      xForm.setPostCoeff20(0.5);
      xForm.setPostCoeff21(0.5);

      // variation 1
      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.4 + Math.random() * 0.65, varFunc);
      }
      // variation 2
      if (Math.random() < 0.10) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.4 + Math.random() * 0.5, varFunc);
      }
      // variation 3
      if (Math.random() < 0.05) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.3 + Math.random() * 0.4, varFunc);
      }
      // set default edit plane
      flame.setEditPlane(EditPlane.XY);
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      if (Math.random() < 0.25) {
        XFormTransformService.rotate(xForm, 360.0 * Math.random(), false);
      }
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
      // random affine post transforms (uncomment to play around)
      XFormTransformService.scale(xForm, 1.15 - Math.random() * 0.75, true, true, true);
      XFormTransformService.rotate(xForm, 360.0 * Math.random(), true);
      XFormTransformService.localTranslate(xForm, 0.25 - 0.5 * Math.random(), 0.25 - 0.5 * Math.random(), true);
    }

    flame.getFirstLayer().randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "Raster";
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

  @Override
  public boolean supportsSymmetry() {
    return false;
  }

}
