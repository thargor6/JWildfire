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

import org.jwildfire.create.tina.base.Flame;

public class AllRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame createFlame() {
    double r = Math.random();
    if (r < 0.10) {
      return new LinearRandomFlameGenerator().createFlame();
    }
    else if (r < 0.20) {
      return new SimpleRandomFlameGenerator().createFlame();
    }
    else if (r < 0.30) {
      return new GnarlRandomFlameGenerator().createFlame();
    }
    else if (r < 0.40) {
      return new ExperimentalGnarlRandomFlameGenerator().createFlame();
    }
    else if (r > 0.70) {
      return new TentacleRandomFlameGenerator().createFlame();
    }
    else if (r > 0.80) {
      return new ThreeDOnlyRandomFlameGenerator().createFlame();
    }
    else {
      return new ExperimentalSimpleRandomFlameGenerator().createFlame();
    }
  }

  @Override
  public String getName() {
    return "All";
  }

}
