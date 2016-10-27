/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;

public class AllRandomFlameGenerator extends RandomFlameGenerator {
  private static List<RandomFlameGenerator> allGenerators;
  private static List<RandomFlameGenerator> simpleGenerators;
  private boolean useSimpleGenerators = false;

  static {
    allGenerators = new ArrayList<RandomFlameGenerator>();
    allGenerators.add(new BlackAndWhiteRandomFlameGenerator());
    allGenerators.add(new BokehRandomFlameGenerator());
    allGenerators.add(new BrokatRandomFlameGenerator());
    allGenerators.add(new Brokat3DRandomFlameGenerator());
    allGenerators.add(new BubblesRandomFlameGenerator());
    allGenerators.add(new Bubbles3DRandomFlameGenerator());
    allGenerators.add(new CrossRandomFlameGenerator());
    allGenerators.add(new DualityRandomFlameGenerator());
    allGenerators.add(new DuckiesRandomFlameGenerator());
    allGenerators.add(new ExperimentalBubbles3DRandomFlameGenerator());
    allGenerators.add(new ExperimentalGnarlRandomFlameGenerator());
    allGenerators.add(new ExperimentalSimpleRandomFlameGenerator());
    allGenerators.add(new FilledFlowers3DRandomFlameGenerator());
    allGenerators.add(new Flowers3DRandomFlameGenerator());
    allGenerators.add(new GalaxiesRandomFlameGenerator());
    allGenerators.add(new GhostsRandomFlameGenerator());
    allGenerators.add(new SpiralsRandomFlameGenerator());
    allGenerators.add(new Spirals3DRandomFlameGenerator());
    allGenerators.add(new GnarlRandomFlameGenerator());
    allGenerators.add(new Gnarl3DRandomFlameGenerator());
    allGenerators.add(new JulianDiscRandomFlameGenerator());
    allGenerators.add(new JuliansRandomFlameGenerator());
    allGenerators.add(new LinearRandomFlameGenerator());
    allGenerators.add(new MandelbrotRandomFlameGenerator());
    allGenerators.add(new RaysRandomFlameGenerator());
    allGenerators.add(new SimpleRandomFlameGenerator());
    allGenerators.add(new SimpleTilingRandomFlameGenerator());
    allGenerators.add(new SierpinskyRandomFlameGenerator());
    if (!Prefs.getPrefs().isTinaDisableSolidFlameRandGens()) {
      allGenerators.add(new SolidExperimentalRandomFlameGenerator());
      allGenerators.add(new SolidStunningRandomFlameGenerator());
      allGenerators.add(new SolidJulia3DRandomFlameGenerator());
      allGenerators.add(new SolidShadowsRandomFlameGenerator());
      allGenerators.add(new SolidLabyrinthRandomFlameGenerator());
    }
    allGenerators.add(new SphericalRandomFlameGenerator());
    allGenerators.add(new Spherical3DRandomFlameGenerator());
    allGenerators.add(new SplitsRandomFlameGenerator());
    allGenerators.add(new SubFlameRandomFlameGenerator());
    allGenerators.add(new SynthRandomFlameGenerator());
    allGenerators.add(new TentacleRandomFlameGenerator());
    allGenerators.add(new TileBallRandomFlameGenerator());
    allGenerators.add(new XenomorphRandomFlameGenerator());

    simpleGenerators = new ArrayList<RandomFlameGenerator>();
    simpleGenerators.addAll(allGenerators);
    int i = 0;
    while (i < simpleGenerators.size()) {
      Class<?> cls = simpleGenerators.get(i).getClass();
      if (LayersRandomFlameGenerator.class.equals(cls) || SubFlameRandomFlameGenerator.class.equals(cls) || WikimediaCommonsRandomFlameGenerator.class.equals(cls) || ColorMapRandomFlameGenerator.class.equals(cls)) {
        simpleGenerators.remove(i);
      }
      else {
        i++;
      }
    }
  }

  private static final String ALL_RANDGEN = "ALL_RANDGEN";

  @Override
  public RandomFlameGeneratorState initState(Prefs pPrefs, RandomGradientGenerator pRandomGradientGenerator) {
    RandomFlameGeneratorState state = super.initState(pPrefs, pRandomGradientGenerator);
    List<RandomFlameGenerator> generators = useSimpleGenerators ? simpleGenerators : allGenerators;
    RandomFlameGenerator generator = generators.get((int) (Math.random() * generators.size()));
    RandomFlameGeneratorState subState = generator.initState(pPrefs, pRandomGradientGenerator);
    state.mergeParams(subState);
    state.getParams().put(ALL_RANDGEN, generator);
    return state;
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = createRandGen(pState);
    RandomFlameGeneratorState subState = generator.initState(pState.getPrefs(), pState.getGradientGenerator());
    Flame flame = generator.prepareFlame(subState);
    flame.setName(generator.getName() + " - " + flame.hashCode());
    return flame;
  }

  private RandomFlameGenerator createRandGen(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = (RandomFlameGenerator) pState.getParams().get(ALL_RANDGEN);
    return generator;
  }

  @Override
  public String getName() {
    return "All";
  }

  public boolean isUseSimpleGenerators() {
    return useSimpleGenerators;
  }

  public void setUseSimpleGenerators(boolean useSimpleGenerators) {
    this.useSimpleGenerators = useSimpleGenerators;
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return createRandGen(pState).isUseFilter(pState);
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    RandomFlameGenerator generator = createRandGen(pState);
    return generator.postProcessFlameBeforeRendering(pState, pFlame);
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    RandomFlameGenerator generator = createRandGen(pState);
    return generator.postProcessFlameAfterRendering(pState, pFlame);
  }
}
