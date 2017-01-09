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

// A random generator following ideas of George Marsaglia, http://programmingpraxis.com/2010/10/05/george-marsaglias-random-number-generators/
@SuppressWarnings("serial")
public class MarsagliaRandomGenerator extends AbstractRandomGenerator {
  private int u = 12244355;
  private int v = 34384;

  private static final int RAND_MAX = 0x7fffffff;
  private static final double RAND_MAX_MUL = 1.0 / (double) RAND_MAX;

  @Override
  public void randomize(long pSeed) {
    u = (int) (pSeed << 16);
    v = (int) (pSeed << 16) >> 16;
  }

  @Override
  public double random() {
    v = 36969 * (v & 65535) + (v >> 16);
    u = 18000 * (u & 65535) + (u >> 16);
    int rnd = (v << 16) + u;
    double res = (double) rnd * RAND_MAX_MUL;
    return res < 0 ? -res : res;
  }

  @Override
  public void cleanup() {
    // empty
  }
}
