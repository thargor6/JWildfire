/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.mutagen;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSample;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.swing.RandomBatchQuality;

public class RandomFlameMutation implements Mutation {

  @Override
  public void execute(Flame pFlame) {
    RandomFlameGenerator randGen = new AllRandomFlameGenerator();
    int palettePoints = 3 + (int) (Math.random() * 68.0);
    boolean fadePaletteColors = Math.random() > 0.33;
    int IMG_WIDTH = 80;
    int IMG_HEIGHT = 60;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, new Prefs(), randGen, palettePoints, fadePaletteColors, RandomBatchQuality.NORMAL);
    RandomFlameGeneratorSample sample = sampler.createSample();
    pFlame.assign(sample.getFlame());
  }

}
