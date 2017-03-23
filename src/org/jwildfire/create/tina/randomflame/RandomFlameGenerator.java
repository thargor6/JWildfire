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

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;

public abstract class RandomFlameGenerator {

  public abstract Flame prepareFlame(RandomFlameGeneratorState pState);

  protected abstract Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame);

  protected abstract Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame);

  public RandomFlameGeneratorState initState(Prefs pPrefs, RandomGradientGenerator pRandomGradientGenerator) {
    return new RandomFlameGeneratorState(pPrefs, pRandomGradientGenerator);
  }

  public abstract String getName();

  public Flame createFlame(Prefs pPrefs, RandomFlameGeneratorState pState) {
    Flame flame = prepareFlame(pState);
    if (flame.getName() == null || flame.getName().length() == 0)
      flame.setName(this.getName() + " - " + flame.hashCode());
    flame.setBGTransparency(pPrefs.isTinaDefaultBGTransparency());
    flame.setBgColorType(BGColorType.SINGLE_COLOR);
    flame.setBgColorRed(pPrefs.getTinaRandomBatchBGColorRed());
    flame.setBgColorGreen(pPrefs.getTinaRandomBatchBGColorGreen());
    flame.setBgColorBlue(pPrefs.getTinaRandomBatchBGColorBlue());
    return flame;
  }

  public abstract boolean isUseFilter(RandomFlameGeneratorState pState);

  public boolean supportsSymmetry() {
    return true;
  }

  public double getMaxCoverage() {
    return 1.0;
  }

}
