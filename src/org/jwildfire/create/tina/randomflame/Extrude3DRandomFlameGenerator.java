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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class Extrude3DRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame createFlame(RandomFlameGeneratorState pState) {
    RandomFlameGenerator randGen = createRandGen(pState);
    Flame flame = randGen.createFlame(pState);
    flame = postProcessFlame(flame, randGen);

    flame.setCamRoll(0);
    flame.setCamPitch(25.0 + Math.random() * 30.0);
    flame.setCamYaw(10 + Math.random() * 20.0);
    flame.setCamPerspective(0.1 + Math.random() * 0.3);
    flame.setCamZoom(0.5);
    flame.setPreserveZ(true);
    if (Math.random() < 0.5) {
      flame.setCamDOF(0.1 + Math.random() * 0.2);
    }

    Layer layer = flame.getFirstLayer();

    XForm xForm = new XForm();
    layer.getFinalXForms().add(xForm);
    xForm.addVariation(0.25 + Math.random() * 0.25, VariationFuncList.getVariationFuncInstance("linear3D", true));
    VariationFunc post_dcztransl = VariationFuncList.getVariationFuncInstance("post_dcztransl", true);
    xForm.addVariation(0.75 + Math.random() * 0.25, post_dcztransl);
    double factor = 0.5 + Math.random() * 2.0;
    if (Math.random() < 0.42) {
      factor = 0.0 - factor;
    }
    post_dcztransl.setParameter("factor", factor);

    return flame;
  }

  @Override
  public String getName() {
    return "Extrude3D";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return true;
  }

  private static final String RANDGEN = "RANDGEN";

  @Override
  public RandomFlameGeneratorState initState() {
    RandomFlameGeneratorState state = super.initState();
    RandomFlameGenerator generator;
    switch ((int) (Math.random() * 14.0)) {
      case 12:
      case 4:
        generator = new SphericalRandomFlameGenerator();
        break;
      case 1:
      case 3:
        generator = new BrokatRandomFlameGenerator();
        break;
      case 10:
      case 2:
        generator = new ExperimentalGnarlRandomFlameGenerator();
        break;
      case 11:
      case 7:
        generator = new JulianDiscRandomFlameGenerator();
        break;
      case 13:
      case 9:
        generator = new DuckiesRandomFlameGenerator();
        break;
      case 0:
      default:
        generator = new GnarlRandomFlameGenerator();
        break;
    }
    state.getParams().put(RANDGEN, generator);
    return state;
  }

  private RandomFlameGenerator createRandGen(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = (RandomFlameGenerator) pState.getParams().get(RANDGEN);
    return generator;
  }

  private Flame postProcessFlame(Flame pFlame, RandomFlameGenerator pGenerator) {
    if (GnarlRandomFlameGenerator.class.equals(pGenerator.getClass())) {
      Layer layer = pFlame.getFirstLayer();
      while (layer.getXForms().size() > 2) {
        layer.getXForms().remove(layer.getXForms().size() - 1);
      }
    }
    else if (DuckiesRandomFlameGenerator.class.equals(pGenerator.getClass()) || BrokatRandomFlameGenerator.class.equals(pGenerator.getClass())) {
      Layer layer = pFlame.getFirstLayer();
      for (XForm xForm : layer.getXForms()) {
        if (!xForm.hasVariation("flatten")) {
          xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("flatten", true));
        }
      }
    }
    return pFlame;
  }
}
