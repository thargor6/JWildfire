/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019 Andreas Maschke

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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.swing.RandomBatchQuality;

public class LayersRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = createSubFlame(pState);
    int layerCount = 2 + ((Math.random() > 0.66) ? 1 : 0);
    for (int i = 1; i < layerCount; i++) {
      Flame subFlame = createSubFlame(pState);
      flame.getLayers().add(subFlame.getFirstLayer());
      flame.getLayers().get(0).setWeight(0.85 + Math.random() * 0.3);
    }
    flame.setPixelsPerUnit(200);
    return flame;
  }

  private Flame createSubFlame(RandomFlameGeneratorState pState) {
    AllRandomFlameGenerator randGen = new AllRandomFlameGenerator();
    randGen.setUseSimpleGenerators(true);
    final int IMG_WIDTH = 120;
    final int IMG_HEIGHT = 90;
    int palettePoints = 3 + Tools.randomInt(17);
    boolean fadePaletteColors = Math.random() > 0.125;
    boolean uniformWidth = Math.random() > 0.75;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, pState.getPrefs(), randGen, RandomSymmetryGeneratorList.NONE, pState.getGradientGenerator(), RandomWeightingFieldGeneratorList.NONE, palettePoints, fadePaletteColors, uniformWidth, RandomBatchQuality.LOW);
    return sampler.createSample().getFlame();
  }

  @Override
  public String getName() {
    return "Layers";
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
