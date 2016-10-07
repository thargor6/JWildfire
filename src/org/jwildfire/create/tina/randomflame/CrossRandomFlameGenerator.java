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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class CrossRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    // Bases loosely on the W2R Batch Script by parrotdolphin.deviantart.com */ 
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    if (Math.random() > 0.6) {
      flame.setNewCamDOF(true);
      flame.setCamDOF(Math.random() * 0.07);
    }
    else {
      flame.setNewCamDOF(false);
      flame.setCamDOF(Math.random() * 0.2);
    }
    flame.setCamPitch(10 + Math.random() * 50);
    flame.setPreserveZ(Math.random() > 0.33);
    flame.setCamPerspective(0.10 + Math.random() * 0.5);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColorSymmetry(0.99 - Math.random() * 0.2);

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
      xForm.setPostCoeff21(-1);

      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColorSymmetry(0.8 - Math.random() * 0.1);

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
      xForm.setPostCoeff20(-1);
      xForm.setPostCoeff21(-1);

      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
    }
    // create transform 3
    {
      XForm xForm = new XForm();
      xForm.setWeight(0.5);
      layer.getXForms().add(xForm);
      xForm.setColor(0.1 + Math.random() * 0.1);
      xForm.setColorSymmetry(0.92);

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
      xForm.setPostCoeff20(-1);
      xForm.setPostCoeff21(1);

      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
    }
    // create transform 4
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setColorSymmetry(0.9 - Math.random() * 0.2);

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
      xForm.setPostCoeff21(1);

      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
    }
    // create transform 5
    XForm xForm5;
    {
      XForm xForm = xForm5 = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.05 + Math.random() * 0.2);
      xForm.setColorSymmetry(0.10 + Math.random() * 0.2);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(0); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(0); // f

      xForm.setPostCoeff00(0.70711);
      xForm.setPostCoeff10(0.70711);
      xForm.setPostCoeff01(-0.70711);
      xForm.setPostCoeff11(0.70711);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.45, varFunc);
      }
    }
    // create optional linked transform 6
    if (Math.random() > 0.75) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.05 + Math.random() * 0.2);
      xForm.setColorSymmetry(0.10 + Math.random() * 0.2);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(0); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(0); // f

      xForm.setPostCoeff00(0.70711);
      xForm.setPostCoeff10(0.70711);
      xForm.setPostCoeff01(-0.70711);
      xForm.setPostCoeff11(0.70711);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      xForm.getModifiedWeights()[5] = 0;

      xForm5.getModifiedWeights()[0] = 0;
      xForm5.getModifiedWeights()[1] = 0;
      xForm5.getModifiedWeights()[2] = 0;
      xForm5.getModifiedWeights()[3] = 0;
      xForm5.getModifiedWeights()[4] = 0;

      {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true);
        xForm.addVariation(0.1 + Math.random() * 0.6, varFunc);
      }
    }
    // create final transform 1
    {
      XForm xForm = new XForm();
      layer.getFinalXForms().add(xForm);
      xForm.setWeight(0);
      xForm.setColorSymmetry(0);

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

      XFormTransformService.rotate(xForm, Math.random() * 360.0, true);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), true);

      if (Math.random() > 0.75) {
        String variations[] = { "cross", "boarders2", "boarders", "butterfly", "cos", "cosh", "cosine", "csc", "cylinder", "cylinder_apo", "dc_ztransl", "elliptic", "eyefish", "fibonacci2", "heart_wf", "hypertile1", "loonie", "mobius", "perspective", "popcorn", "popcorn2_3D", "ripple", "roundspher3D", "scry_3D", "sec", "secant2", "separation", "shredlin", "sin", "spherical", "spiral", "stripes", "unpolar", "waves2", "waves4_wf", "whorl", "xtrb", "rays1", "rays2", "rays3" };
        String varName = Math.random() < 0.25 ? "cross" : Math.random() < 0.25 ? "rays2" : variations[(int) (Math.random() * variations.length)];
        xForm.addVariation(1.57, VariationFuncList.getVariationFuncInstance(varName, true));
      }
      else {
        xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));
      }

    }
    layer.randomizeColors();
    return flame;
  }

  @Override
  public String getName() {
    return "Cross";
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
