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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;

// Inspired by the "B&W converter"-script by Brad Stefanov and Mick Hogan
public class BlackAndWhiteRandomFlameGenerator extends RandomFlameGenerator {
  private static List<RandomFlameGenerator> generators;

  static {
    generators = new ArrayList<RandomFlameGenerator>();
    generators.add(new BrokatRandomFlameGenerator());
    generators.add(new Brokat3DRandomFlameGenerator());
    generators.add(new BubblesRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new Bubbles3DRandomFlameGenerator());
    generators.add(new CrossRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new DuckiesRandomFlameGenerator());
    generators.add(new ExperimentalBubbles3DRandomFlameGenerator());
    generators.add(new ExperimentalGnarlRandomFlameGenerator());
    generators.add(new ExperimentalSimpleRandomFlameGenerator());
    generators.add(new FilledFlowers3DRandomFlameGenerator());
    generators.add(new Flowers3DRandomFlameGenerator());
    generators.add(new GnarlRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new Gnarl3DRandomFlameGenerator());
    generators.add(new JulianDiscRandomFlameGenerator());
    generators.add(new LayerzRandomFlameGenerator());
    generators.add(new LinearRandomFlameGenerator());
    generators.add(new MandelbrotRandomFlameGenerator());
    generators.add(new RaysRandomFlameGenerator());
    generators.add(new SimpleRandomFlameGenerator());
    generators.add(new SimpleTilingRandomFlameGenerator());
    generators.add(new SierpinskyRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new SphericalRandomFlameGenerator());
    generators.add(new Spherical3DRandomFlameGenerator());
    generators.add(new SplitsRandomFlameGenerator());
    generators.add(new SubFlameRandomFlameGenerator());
    generators.add(new SynthRandomFlameGenerator());
    generators.add(new TentacleRandomFlameGenerator());
    generators.add(new DualityRandomFlameGenerator());
    generators.add(new XenomorphRandomFlameGenerator());
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
  protected Flame prepareFlame(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = createRandGen(pState);
    RandomFlameGeneratorState subState = generator.initState(pState.getPrefs(), pState.getGradientGenerator());
    Flame flame = generator.prepareFlame(subState);
    flame.setName(generator.getName() + " - " + flame.hashCode());
    return flame;
  }

  private RandomFlameGenerator createRandGen(RandomFlameGeneratorState pState) {
    RandomFlameGenerator generator = (RandomFlameGenerator) pState.getParams().get(RANDGEN);
    return generator;
  }

  @Override
  public String getName() {
    return "Black&White";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  @Override
  protected Flame postProcessFlame(RandomFlameGeneratorState pState, Flame pFlame) {
    if (Math.random() < 0.42) {
      pFlame.setBGColorRed(0);
      pFlame.setBGColorGreen(0);
      pFlame.setBGColorBlue(0);
      List<RGBColor> colors = new ArrayList<RGBColor>();
      colors.add(new RGBColor(255, 255, 255));
      RGBPalette gradient = RandomGradientGenerator.generatePalette(colors, true);
      for (Layer layer : pFlame.getLayers()) {
        layer.setPalette(gradient);
      }
    }
    else {
      pFlame.setBGColorRed(255);
      pFlame.setBGColorGreen(255);
      pFlame.setBGColorBlue(255);
      List<RGBColor> colors = new ArrayList<RGBColor>();
      colors.add(new RGBColor(0, 0, 0));
      RGBPalette gradient = RandomGradientGenerator.generatePalette(colors, true);
      for (Layer layer : pFlame.getLayers()) {
        layer.setPalette(gradient);
      }
    }

    pFlame.setGamma(0.45 + Math.random() * 1.5);
    pFlame.setGammaThreshold(0.2 * Math.random());
    pFlame.setSaturation(1.0);
    pFlame.setBrightness(3.0 + Math.random() * 2.0);
    pFlame.setContrast(2.5);
    pFlame.setBGTransparency(false);

    return pFlame;
  }

}
