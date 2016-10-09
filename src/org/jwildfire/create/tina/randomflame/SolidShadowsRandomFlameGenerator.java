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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.PostSymmetryType;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.mutagen.RandomGradientMutation;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class SolidShadowsRandomFlameGenerator extends RandomFlameGenerator {
  private static List<RandomFlameGenerator> generators;

  static {
    generators = new ArrayList<RandomFlameGenerator>();
    generators.add(new SolidStunningRandomFlameGenerator());
    generators.add(new SolidExperimentalRandomFlameGenerator());
    generators.add(new SolidStunningRandomFlameGenerator());
    generators.add(new SolidStunningRandomFlameGenerator());
    generators.add(new SolidJulia3DRandomFlameGenerator());
    generators.add(new SolidStunningRandomFlameGenerator());
  }

  private static final String RANDGEN = "RANDGEN";

  @Override
  public RandomFlameGeneratorState initState(Prefs pPrefs, RandomGradientGenerator pRandomGradientGenerator) {
    RandomFlameGeneratorState state = super.initState(pPrefs, pRandomGradientGenerator);
    RandomFlameGenerator generator = generators.get((int) (Math.random() * generators.size()));
    state.getParams().put(RANDGEN, generator);
    return state;
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = createRandGen(pState);
    RandomFlameGeneratorState subState = generator.initState(pState.getPrefs(), pState.getGradientGenerator());
    Flame flame = generator.prepareFlame(subState);
    flame.setName(getName() + " - " + flame.hashCode());
    return flame;
  }

  private RandomFlameGenerator createRandGen(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = (RandomFlameGenerator) pState.getParams().get(RANDGEN);
    return generator;
  }

  @Override
  public String getName() {
    return "Solid (shadows)";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    RandomFlameGenerator generator = createRandGen(pState);
    pFlame = generator.postProcessFlameBeforeRendering(pState, pFlame);
    pFlame.setPostSymmetryType(PostSymmetryType.NONE);
    return pFlame;
  }

  @Override
  public boolean supportsSymmetry() {
    return false;
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {

    Layer layer = new Layer();
    layer.setWeight(0.25 + Math.random() * 0.5);
    pFlame.getLayers().add(layer);
    new RandomGradientMutation().execute(layer);
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      if (Math.random() < 0.5) {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("plane_wf", true);
        varFunc.setParameter("axis", 0);
        varFunc.setParameter("position", -Math.random() * 2.0 - 0.25);
        varFunc.setParameter("color_mode", Tools.FTOI(4 * Math.random()));
        xForm.addVariation(1.0, varFunc);
      }
      else {
        VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("checkerboard_wf", true);
        varFunc.setParameter("axis", 0);
        varFunc.setParameter("position", -Math.random() * 2.0 - 0.25);
        varFunc.setParameter("checker_size", 0.1 + Math.random() * 0.1);
        varFunc.setParameter("displ_amount", 0.01 + Math.random() * 0.05);
        xForm.addVariation(1.0, varFunc);
      }
    }

    pFlame.getSolidRenderSettings().setShadowType(Math.random() < 0.5 ? ShadowType.SMOOTH : ShadowType.FAST);
    return pFlame;
  }
}
