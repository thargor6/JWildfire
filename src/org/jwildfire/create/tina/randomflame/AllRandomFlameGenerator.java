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
  private static List<RandomFlameGenerator> allGenerators;
  private static List<RandomFlameGenerator> simpleGenerators;
  private boolean useSimpleGenerators = false;

  static {
    allGenerators = new ArrayList<RandomFlameGenerator>();
    allGenerators.add(new LinearRandomFlameGenerator());
    allGenerators.add(new FilledFlowers3DRandomFlameGenerator());
    allGenerators.add(new SimpleRandomFlameGenerator());
    allGenerators.add(new SphericalRandomFlameGenerator());
    allGenerators.add(new Spherical3DRandomFlameGenerator());
    allGenerators.add(new BrokatRandomFlameGenerator());
    allGenerators.add(new GnarlRandomFlameGenerator());
    allGenerators.add(new ExperimentalGnarlRandomFlameGenerator());
    allGenerators.add(new BubblesRandomFlameGenerator());
    allGenerators.add(new Spherical3DRandomFlameGenerator());
    allGenerators.add(new BrokatRandomFlameGenerator());
    allGenerators.add(new SubFlameRandomFlameGenerator());
    allGenerators.add(new Bubbles3DRandomFlameGenerator());
    allGenerators.add(new SphericalRandomFlameGenerator());
    allGenerators.add(new MandelbrotRandomFlameGenerator());
    allGenerators.add(new SplitsRandomFlameGenerator());
    allGenerators.add(new GnarlRandomFlameGenerator());
    allGenerators.add(new Flowers3DRandomFlameGenerator());
    allGenerators.add(new FilledFlowers3DRandomFlameGenerator());
    allGenerators.add(new ExperimentalBubbles3DRandomFlameGenerator());
    allGenerators.add(new Spherical3DRandomFlameGenerator());
    allGenerators.add(new SubFlameRandomFlameGenerator());
    allGenerators.add(new JulianDiscRandomFlameGenerator());
    allGenerators.add(new BrokatRandomFlameGenerator());
    allGenerators.add(new FilledFlowers3DRandomFlameGenerator());
    allGenerators.add(new ExperimentalSimpleRandomFlameGenerator());
    allGenerators.add(new TentacleRandomFlameGenerator());

    simpleGenerators = new ArrayList<RandomFlameGenerator>();
    simpleGenerators.add(new LinearRandomFlameGenerator());
    simpleGenerators.add(new FilledFlowers3DRandomFlameGenerator());
    simpleGenerators.add(new SphericalRandomFlameGenerator());
    simpleGenerators.add(new SplitsRandomFlameGenerator());
    simpleGenerators.add(new Spherical3DRandomFlameGenerator());
    simpleGenerators.add(new BrokatRandomFlameGenerator());
    simpleGenerators.add(new GnarlRandomFlameGenerator());
    simpleGenerators.add(new BubblesRandomFlameGenerator());
    simpleGenerators.add(new Spherical3DRandomFlameGenerator());
    simpleGenerators.add(new Flowers3DRandomFlameGenerator());
  }

  @Override
  protected Flame createFlame() {
    List<RandomFlameGenerator> generators = useSimpleGenerators ? simpleGenerators : allGenerators;
    RandomFlameGenerator generator = generators.get((int) (Math.random() * generators.size()));

    Flame flame = generator.createFlame();
    flame.setName(generator.getName() + " - " + flame.hashCode());
    return flame;
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

}
