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
package org.jwildfire.create.tina.random;

import net.goui.util.MTRandom;

// A random generator using the Mersenne Twister implemtation by David Beaumont, http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/VERSIONS/JAVA/MTRandom.java
public class MersenneTwisterRandomGenerator extends AbstractRandomGenerator {
  private final MTRandom mtRandom = new MTRandom();

  @Override
  public void randomize(long pSeed) {
    mtRandom.setSeed(pSeed);
  }

  @Override
  public double random() {
    return mtRandom.nextDouble();
  }

  @Override
  public void cleanup() {
    // empty
  }
}
