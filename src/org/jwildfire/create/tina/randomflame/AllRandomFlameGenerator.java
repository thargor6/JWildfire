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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;

public class AllRandomFlameGenerator extends RandomFlameGenerator {
  private static List<RandomFlameGenerator> generators;

  static {
    generators = new ArrayList<RandomFlameGenerator>();
    generators.add(new LinearRandomFlameGenerator());
    generators.add(new FilledFlowers3DRandomFlameGenerator());
    generators.add(new SimpleRandomFlameGenerator());
    generators.add(new SphericalRandomFlameGenerator());
    generators.add(new Spherical3DRandomFlameGenerator());
    generators.add(new BrokatRandomFlameGenerator());
    generators.add(new GnarlRandomFlameGenerator());
    generators.add(new ExperimentalGnarlRandomFlameGenerator());
    generators.add(new BubblesRandomFlameGenerator());
    generators.add(new Spherical3DRandomFlameGenerator());
    generators.add(new BrokatRandomFlameGenerator());
    generators.add(new SubFlameRandomFlameGenerator());
    generators.add(new Bubbles3DRandomFlameGenerator());
    generators.add(new SphericalRandomFlameGenerator());
    generators.add(new GnarlRandomFlameGenerator());
    generators.add(new Flowers3DRandomFlameGenerator());
    generators.add(new FilledFlowers3DRandomFlameGenerator());
    generators.add(new ExperimentalBubbles3DRandomFlameGenerator());
    generators.add(new Spherical3DRandomFlameGenerator());
    generators.add(new SubFlameRandomFlameGenerator());
    generators.add(new JulianDiscRandomFlameGenerator());
    generators.add(new BrokatRandomFlameGenerator());
    generators.add(new FilledFlowers3DRandomFlameGenerator());
    generators.add(new ExperimentalSimpleRandomFlameGenerator());
    generators.add(new TentacleRandomFlameGenerator());
  }

  @Override
  protected Flame createFlame() {
    return generators.get((int) (Math.random() * generators.size())).createFlame();
  }

  @Override
  public String getName() {
    return "All";
  }

}
