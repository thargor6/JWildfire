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
import org.jwildfire.create.tina.mutagen.RandomGradientMutation;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class JulianRingsRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    // set the flame main attributes
    flame.setCamRoll(Math.random()*360.0);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0.0);
    flame.setCamPerspective(0);
    flame.setWidth(962);
    flame.setHeight(541);
    flame.setPixelsPerUnit(1228.6875);
    flame.setCamZoom(3.0+Math.random()*2.0);
    flame.setBGTransparency(false);

    if(Math.random()>0.5) {
      flame.setBgColorType(BGColorType.SINGLE_COLOR);
      flame.setBgColorRed(255);
      flame.setBgColorGreen(255);
      flame.setBgColorBlue(255);
    }
    // create layer 1
    {
      Layer layer = flame.getFirstLayer();
      // create a random gradient
      new RandomGradientMutation().execute(layer);
      // create transform 1
      {
        XForm xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(1.0+Math.random());
        xForm.setColor(Math.random());
        xForm.setColorSymmetry(-1.0+2.0*Math.random());
        xForm.setMaterial(0);
        xForm.setMaterialSpeed(0);

        xForm.setCoeff00(1.87375); // a
        xForm.setCoeff10(0); // b
        xForm.setCoeff20(0); // e
        xForm.setCoeff01(0); // c
        xForm.setCoeff11(1.873754); // d
        xForm.setCoeff21(0); // f

        xForm.setPostCoeff00(1);
        xForm.setPostCoeff10(0);
        xForm.setPostCoeff01(0);
        xForm.setPostCoeff11(1);
        xForm.setPostCoeff20(0);
        xForm.setPostCoeff21(0);

        // variation 1
        {
          String variations[] = {"julia3D", "jubiQ", "julia3Dq", "julia3Dz", "julian", "julian2", "julian3Dx", "juliaq", "juliascope"};
          int variationIdx = Math.min((int)(Math.random()*variations.length), variations.length-1);
          VariationFunc varFunc=VariationFuncList.getVariationFuncInstance(variations[variationIdx], true);
          varFunc.setParameter("power",  Math.random()>0.5 ? 10+ Math.random()* 5000.0 : -2.0 - Math.random()* 100.0);
          xForm.addVariation(1, varFunc);
        }
        if(Math.random()>0.75) {
          VariationFunc varFunc=VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
          xForm.addVariation(-0.125 + Math.random()*0.25, varFunc);
        }
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
        xForm.setWeight(25.0+ Math.random()*25.0);
        xForm.setColor(Math.random());
        xForm.setColorSymmetry(-1.0+2.0*Math.random());
        xForm.setMaterial(0);
        xForm.setMaterialSpeed(0);

        xForm.setCoeff00(0.990702); // a
        xForm.setCoeff10(0.154938); // b
        xForm.setCoeff20(-0.140437); // e
        xForm.setCoeff01(-0.136053); // c
        xForm.setCoeff11(0.92994); // d
        xForm.setCoeff21(0.159563); // f

        xForm.setPostCoeff00(1);
        xForm.setPostCoeff10(0);
        xForm.setPostCoeff01(0);
        xForm.setPostCoeff11(1);
        xForm.setPostCoeff20(0);
        xForm.setPostCoeff21(0);

        // variation 1
        {
          VariationFunc varFunc=VariationFuncList.getVariationFuncInstance("rings2", true);
          varFunc.setParameter("val", 1.0);
          xForm.addVariation(Math.random()>0.33 ? 1.0 : 0.9 + Math.random()*0.3, varFunc);
        }
        if(Math.random()>0.5) {
          VariationFunc varFunc=VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
          xForm.addVariation(-0.25 + Math.random()*0.5, varFunc);
        }
        // set default edit plane
        flame.setEditPlane(EditPlane.XY);
        // random affine transforms (uncomment to play around)
        //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
          XFormTransformService.rotate(xForm, 45.0 - 90.0*Math.random(), false);
           XFormTransformService.localTranslate(xForm, -0.125 + 0.25*Math.random(), -0.125 + 0.25*Math.random(), false);
        // random affine post transforms (uncomment to play around)
        //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
        //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
        //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
      }
    }
    return flame;
  }

  @Override
  public String getName() {
    return "JulianRings";
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
