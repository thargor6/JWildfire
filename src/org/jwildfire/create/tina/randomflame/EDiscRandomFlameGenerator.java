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

public class EDiscRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    flame.setCamRoll(-0);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0.0);
    flame.setCamPerspective(0);
    flame.setWidth(638);
    flame.setHeight(359);
    flame.setCamRoll(360.0 * Math.random());
    flame.setCentreX(0.5-Math.random());
    flame.setCentreY(0.5-Math.random());
    flame.setPixelsPerUnit(90.0);
    flame.setCamZoom(0.55 + Math.random() * 0.25);
    flame.setPreserveZ(true);
    // create layer 1
    {
      Layer layer = flame.getFirstLayer();
      // create a random gradient
      new RandomGradientMutation().execute(layer);
      // create transform 1
      {
        XForm xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(3.5 + Math.random());
        xForm.setColor(Math.random());
        xForm.setColorSymmetry(0.9);
        xForm.setDrawMode(DrawMode.NORMAL);
        xForm.setMaterial(0);
        xForm.setMaterialSpeed(0);

        xForm.setCoeff00(0.88067445); // a
        xForm.setCoeff10(-0.50058358); // b
        xForm.setCoeff20(0.111166); // e
        xForm.setCoeff01(0.50058358); // c
        xForm.setCoeff11(0.88067445); // d
        xForm.setCoeff21(-0.873736); // f

        xForm.setPostCoeff00(-0.172546);
        xForm.setPostCoeff10(-1.052875);
        xForm.setPostCoeff01(1.052875);
        xForm.setPostCoeff11(-0.172546);
        xForm.setPostCoeff20(-0.057973);
        xForm.setPostCoeff21(0.051023);

        // change relative weights
        xForm.getModifiedWeights()[0] = 1.0 + Math.random() * 10.0;
        xForm.getModifiedWeights()[2] = 0;

        // variation 1
        xForm.addVariation(15, VariationFuncList.getVariationFuncInstance("edisc", true));
        // set default edit plane
        flame.setEditPlane(EditPlane.XY);
        // random affine transforms (uncomment to play around)
        XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
        XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
        // XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);
        // random affine post transforms (uncomment to play around)
        //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
        //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
        //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
      }
      boolean withTX3 = Math.random() > 0.666;
      // create transform 2
      {
        XForm xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(0.5 + Math.random());
        xForm.setColor(Math.random());
        xForm.setColorSymmetry(0);
        xForm.setDrawMode(DrawMode.HIDDEN);
        xForm.setMaterial(0);
        xForm.setMaterialSpeed(0);

        xForm.setCoeff00(1); // a
        xForm.setCoeff10(0); // b
        xForm.setCoeff20(0.166332); // e
        xForm.setCoeff01(0); // c
        xForm.setCoeff11(1); // d
        xForm.setCoeff21(-0.620971); // f

        xForm.setPostCoeff00(1);
        xForm.setPostCoeff10(0);
        xForm.setPostCoeff01(0);
        xForm.setPostCoeff11(1);
        xForm.setPostCoeff20(0);
        xForm.setPostCoeff21(0);

        // change relative weights
        if(withTX3) {
          xForm.getModifiedWeights()[0] = 0;
          xForm.getModifiedWeights()[1] = 0;
        }
        else {
          xForm.getModifiedWeights()[0] = 1;
          xForm.getModifiedWeights()[1] = 1.0 + Math.random() * 2.0;
        }

        // variation 1
        String variations[] = {"juliascope3Db", "juliascope", "julian", "julian2", "julian3Dx" };
        {
          int varIdx = Math.max((int)(Math.random() * variations.length), 0);
          VariationFunc varFunc=VariationFuncList.getVariationFuncInstance(variations[varIdx], true);
          varFunc.setParameter("power", 50.0 + Math.random() * 100.0);
          varFunc.setParameter("dist", 2.0 + Math.random() * 6.0);
          xForm.addVariation(5.0 + Math.random() * 20.0, varFunc);
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
      // create transform 3
      if(withTX3) {
        XForm xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(0.5);
        xForm.setColor(Math.random());
        xForm.setColorSymmetry(1);
        xForm.setDrawMode(DrawMode.NORMAL);
        xForm.setColorType(ColorType.NONE);
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
        xForm.setPostCoeff21(0);

        // change relative weights
        xForm.getModifiedWeights()[1] = 1.0 + Math.random() * 2.0;
        xForm.getModifiedWeights()[2] = 0;

        // variation 1
        {
          VariationFunc varFunc=VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
          xForm.addVariation(0.25 - Math.random() * 0.5, varFunc);
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
    }
    return flame;
  }

  @Override
  public String getName() {
    return "EDisc";
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
