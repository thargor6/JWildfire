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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.swing.RandomBatchQuality;

public class LayerzRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame createFlame(RandomFlameGeneratorState pState) {
    Prefs prefs = new Prefs();
    try {
      prefs.loadFromFile();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    Flame flame = createSubFlame(prefs);
    int layerCount = 2 + ((Math.random() > 0.66) ? 1 : 0);
    for (int i = 1; i < layerCount; i++) {
      Flame subFlame = createSubFlame(prefs);
      flame.getLayers().add(subFlame.getFirstLayer());
      flame.getLayers().get(0).setWeight(0.85 + Math.random() * 0.3);
    }
    flame.setPixelsPerUnit(200);
    return flame;
  }

  private Flame createSubFlame(Prefs prefs) {
    AllRandomFlameGenerator randGen = new AllRandomFlameGenerator();
    randGen.setUseSimpleGenerators(true);
    final int IMG_WIDTH = 120;
    final int IMG_HEIGHT = 90;
    int palettePoints = 3 + (int) (Math.random() * 17.0);
    boolean fadePaletteColors = Math.random() > 0.125;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, palettePoints, fadePaletteColors, RandomBatchQuality.LOW);
    return sampler.createSample().getFlame();
  }

  @Override
  public String getName() {
    return "Layerz";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

}
