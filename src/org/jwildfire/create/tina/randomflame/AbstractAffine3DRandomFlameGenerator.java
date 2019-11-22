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

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFuncList;

public abstract class AbstractAffine3DRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    RandomFlameGenerator randGen = createRandGen(pState);
    Flame flame = randGen.prepareFlame(pState);
    flame = preProcessFlame(flame);

    flame.setCamRoll(0);
    flame.setCamPitch(25.0 + Math.random() * 30.0);
    flame.setCamYaw(10 + Math.random() * 20.0);
    flame.setCamBank(5.0 + Math.random() * 10.0);
    flame.setCamPerspective(0.1 + Math.random() * 0.3);
    flame.setCamZoom(0.5);
    flame.setPreserveZ(true);
    if (Math.random() < 0.5) {
      flame.setCamDOF(0.1 + Math.random() * 0.2);
    }

    flame = postProcessFlame(flame);
    return flame;
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  private static final String AFFINE_RANDGEN = "AFFINE_RANDGEN";

  protected abstract RandomFlameGenerator selectRandGen();

  @Override
  public RandomFlameGeneratorState initState(Prefs pPrefs, RandomGradientGenerator pRandomGradientGenerator) {
    RandomFlameGeneratorState state = super.initState(pPrefs, pRandomGradientGenerator);
    RandomFlameGenerator generator = selectRandGen();
    state.getParams().put(AFFINE_RANDGEN, generator);
    return state;
  }

  private RandomFlameGenerator createRandGen(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = (RandomFlameGenerator) pState.getParams().get(AFFINE_RANDGEN);
    return generator;
  }

  protected abstract Flame preProcessFlame(Flame pFlame);

  protected abstract Flame postProcessFlame(Flame pFlame);

  protected void rotateXForm(Flame pFlame, int idx, double amp0) {
    Layer layer = pFlame.getFirstLayer();
    if (layer.getXForms().size() > idx) {
      pFlame.setEditPlane(EditPlane.ZX);
      XForm xform = layer.getXForms().get(idx);
      XFormTransformService.rotate(xform, (0.5 - Math.random()) * amp0);
      pFlame.setEditPlane(EditPlane.XY);
    }
  }

  protected void scaleXForm(Flame pFlame, int idx, double offset, double amp0) {
    Layer layer = pFlame.getFirstLayer();
    if (layer.getXForms().size() > idx) {
      pFlame.setEditPlane(EditPlane.ZX);
      XForm xform = layer.getXForms().get(idx);
      XFormTransformService.scale(xform, offset + (0.5 - Math.random()) * amp0, true, true);
      pFlame.setEditPlane(EditPlane.XY);
    }
  }

  protected void addFlatten(Flame pFlame, int idx) {
    Layer layer = pFlame.getFirstLayer();
    if (layer.getXForms().size() > idx) {
      XForm xform = layer.getXForms().get(idx);
      if (!xform.hasVariation("flatten")) {
        xform.addVariation(1.0, VariationFuncList.getVariationFuncInstance("flatten", true));
      }
    }
  }

}
