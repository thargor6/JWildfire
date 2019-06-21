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
package org.jwildfire.create.tina.randommovie;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.swing.RandomBatchQuality;

public abstract class RandomMovieGenerator {

  protected abstract FlameMovie prepareMovie(Prefs pPrefs);

  public abstract String getName();

  public final FlameMovie createMovie(Prefs pPrefs) {
    FlameMovie movie = prepareMovie(pPrefs);
    if (movie.getName() == null || movie.getName().length() == 0)
      movie.setName(this.getName() + " - " + movie.hashCode());
    return movie;
  }

  protected Flame genRandomFlame(RandomFlameGenerator pRandGen, Prefs pPrefs) {
    final int IMG_WIDTH = 80;
    final int IMG_HEIGHT = 60;
    int palettePoints = 3 + Tools.randomInt(68);
    boolean fadePaletteColors = Math.random() > 0.33;
    boolean uniformWidth = Math.random() > 0.75;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, pPrefs, pRandGen, RandomSymmetryGeneratorList.NONE, RandomGradientGeneratorList.DEFAULT, RandomWeightingFieldGeneratorList.NONE, palettePoints, fadePaletteColors, uniformWidth, RandomBatchQuality.NORMAL);
    return sampler.createSample().getFlame();
  }
}
