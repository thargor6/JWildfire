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
package org.jwildfire.create.tina.randommovie;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.FlameMovie;

public class AllRandomMovieGenerator extends RandomMovieGenerator {
  private static List<RandomMovieGenerator> generators;

  static {
    generators = new ArrayList<RandomMovieGenerator>();
    generators.add(new RotatingMandelbrotRandomMovieGenerator());
    generators.add(new TransformingDuckiesRandomMovieGenerator());
    generators.add(new TransformingBubblesRandomMovieGenerator());
  }

  @Override
  protected FlameMovie prepareMovie(Prefs pPrefs) {
    RandomMovieGenerator generator = createRandGen();
    FlameMovie movie = generator.createMovie(pPrefs);
    movie.setName(generator.getName() + " - " + movie.hashCode());
    return movie;
  }

  private RandomMovieGenerator createRandGen() {
    RandomMovieGenerator generator = generators.get((int) (Math.random() * generators.size()));
    return generator;
  }

  @Override
  public String getName() {
    return "All";
  }

}
