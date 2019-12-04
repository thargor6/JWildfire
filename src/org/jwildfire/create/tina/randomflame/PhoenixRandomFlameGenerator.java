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

import org.jwildfire.create.tina.base.*;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class PhoenixRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    // Bases loosely on the W2R Batch Script by parrotdolphin.deviantart.com */ 
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCamRoll(0);
    flame.setCentreX(-0.43687754);
    flame.setCentreY(-0.84902392);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0.0);
    flame.setCamPerspective(0);
    flame.setWidth(1920);
    flame.setHeight(1080);
    flame.setCamZoom(0.4 + Math.random()*0.5);
    flame.setBGTransparency(false);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();


    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColor(0);
      xForm.setColorSymmetry(0);
      xForm.setDrawMode(DrawMode.NORMAL);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(0.465496); // a
      xForm.setCoeff10(-0.702038); // b
      xForm.setCoeff20(-0.554347); // e
      xForm.setCoeff01(0.174105); // c
      xForm.setCoeff11(0.956244); // d
      xForm.setCoeff21(-0.366901); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear", true));
      flame.setEditPlane(EditPlane.XY);
      XFormTransformService.scale(xForm, 1.125-Math.random()*0.25, true, true, false);
      XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      if(Math.random()>0.5) {
        flame.setEditPlane(EditPlane.YZ);
        XFormTransformService.scale(xForm, 1.125-Math.random()*0.25, true, true, false);
        XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      }
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColor(1);
      xForm.setColorSymmetry(0);
      xForm.setDrawMode(DrawMode.NORMAL);
      xForm.setMaterial(0);
      xForm.setMaterialSpeed(0);

      xForm.setCoeff00(0.706669); // a
      xForm.setCoeff10(-0.515362); // b
      xForm.setCoeff20(-1.045926); // e
      xForm.setCoeff01(0.515362); // c
      xForm.setCoeff11(0.706669); // d
      xForm.setCoeff21(0.383008); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      xForm.getModifiedWeights()[0] = 0.25+Math.random()*0.1;
      xForm.getModifiedWeights()[1] = 1.15+Math.random()*0.2;

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear", true));
      // variation 2
      {
        VariationFunc varFunc=VariationFuncList.getVariationFuncInstance("curl", true);
        varFunc.setParameter("c1", 0.05+Math.random()*0.1);
        varFunc.setParameter("c2", 0.86+Math.random()*0.1);
        xForm.addVariation(1, varFunc);
      }
      flame.setEditPlane(EditPlane.XY);
      XFormTransformService.localTranslate(xForm, 0.5-1.0*Math.random(), 0.5-1.0*Math.random(), false);
    }


    layer.randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "Phoenix";
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
