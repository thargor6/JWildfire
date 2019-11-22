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

import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.mutagen.RandomGradientMutation;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class OrchidsRandomFlameGenerator extends RandomFlameGenerator {

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
    flame.setPixelsPerUnit(315.33902046);
    flame.setCamZoom(2.2 + Math.random() * 0.6);
    // create layer 1
    {
      Layer layer = flame.getFirstLayer();
      // create a random gradient
      new RandomGradientMutation().execute(layer);
      // create transform 1
      {
        XForm xForm = new XForm();
        layer.getXForms().add(xForm);
        xForm.setWeight(4 + Math.random() * 2.0);
        xForm.setColor(0.6 * Math.random());
        xForm.setColorSymmetry(0);
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

        // variation 1
        xForm.addVariation(1.5 + Math.random(), VariationFuncList.getVariationFuncInstance("elliptic", true));
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
        xForm.setWeight(1.5 + Math.random());
        xForm.setColor(0.5 + Math.random() * 0.2);
        xForm.setColorSymmetry(0);
        xForm.setMaterial(0);
        xForm.setMaterialSpeed(0);

        xForm.setCoeff00(-0.00283201); // a
        xForm.setCoeff10(0.99999599); // b
        xForm.setCoeff20(0); // e
        xForm.setCoeff01(-0.99999599); // c
        xForm.setCoeff11(-0.00283201); // d
        xForm.setCoeff21(0); // f

        xForm.setPostCoeff00(1);
        xForm.setPostCoeff10(0);
        xForm.setPostCoeff01(0);
        xForm.setPostCoeff11(1);
        xForm.setPostCoeff20(0);
        xForm.setPostCoeff21(0);

        // variation 1
        {
          VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("poincare3D", true);
          varFunc.setParameter("r", 0);
          varFunc.setParameter("a", 0);
          varFunc.setParameter("b", 0);
          xForm.addVariation(Math.random() + 1.0, varFunc);
        }
        // variation 2
        xForm.addVariation(10.0 * Math.random() - 5.0, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));
        // set default edit plane
        flame.setEditPlane(EditPlane.XY);
        // random affine transforms (uncomment to play around)
        XFormTransformService.scale(xForm, 0.5 + 2.0 * Math.random(), true, true, false);
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
        xForm.setWeight(1.2 + Math.random());
        xForm.setColor(0.2 + Math.random() * 0.5);
        xForm.setColorSymmetry(0);
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

        // variation 1
        xForm.addVariation(0.75, VariationFuncList.getVariationFuncInstance("rays", true));
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
      // create final transform 1
      {
        XForm xForm = new XForm();
        layer.getFinalXForms().add(xForm);
        xForm.setWeight(0);
        xForm.setColor(0.96);
        xForm.setColorSymmetry(0);
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

        // variation 1
        {
          VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("hypertile1", true);
          varFunc.setParameter("p", 4);
          varFunc.setParameter("q", 6);
          xForm.addVariation(0.3 + Math.random() * 0.8, varFunc);
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
      // create final transform 2
      {
        XForm xForm = new XForm();
        layer.getFinalXForms().add(xForm);
        xForm.setWeight(0);
        xForm.setColor(0);
        xForm.setColorSymmetry(0);
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

        // variation 1
        if (Math.random() > 0.25) {
          xForm.addVariation(1, VariationFuncList.getVariationFuncInstance(Math.random() < 0.5 ? "polar" : "polar2", true));
        }
        else {
          xForm.addVariation(1, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));
        }
      }
      if (Math.random() > 0.666) {
        layer.getXForms().get(0).getModifiedWeights()[0] = 1.0 + Math.random();
        layer.getXForms().get(0).getModifiedWeights()[2] = 0.2 * Math.random();
      }
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Orchids";
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
