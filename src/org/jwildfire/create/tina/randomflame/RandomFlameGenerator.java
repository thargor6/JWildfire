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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;

public abstract class RandomFlameGenerator {

  protected abstract Flame createFlame(RandomFlameGeneratorState pState);

  public RandomFlameGeneratorState initState() {
    return new RandomFlameGeneratorState();
  }

  public abstract String getName();

  public Flame createFlame(Prefs pPrefs, RandomFlameGeneratorState pState) {
    Flame flame = createFlame(pState);
    if (flame.getName() == null || flame.getName().length() == 0)
      flame.setName(this.getName() + " - " + flame.hashCode());
    flame.setBGTransparency(pPrefs.isTinaDefaultBGTransparency());
    flame.setBGColorRed(pPrefs.getTinaRandomBatchBGColorRed());
    flame.setBGColorGreen(pPrefs.getTinaRandomBatchBGColorGreen());
    flame.setBGColorBlue(pPrefs.getTinaRandomBatchBGColorBlue());
    if (pPrefs.getTinaDefaultDEMaxRadius() < EPSILON) {
      flame.setDeFilterEnabled(false);
    }
    else {
      flame.setDeFilterEnabled(true);
      flame.setDeFilterMaxRadius(pPrefs.getTinaDefaultDEMaxRadius());
    }
    flame.setAntialiasAmount(pPrefs.getTinaDefaultAntialiasingAmount());
    flame.setAntialiasRadius(pPrefs.getTinaDefaultAntialiasingRadius());
    return flame;
  }

  public abstract boolean isUseFilter(RandomFlameGeneratorState pState);

}
