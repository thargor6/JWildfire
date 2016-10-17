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

public class SimpleRandomGenerator extends AbstractRandomGenerator {

  private int a = 1;

  private static final int RAND_MAX123 = 0x7fffffff;

  private static final double rrmax = 1.0 / (double) RAND_MAX123;

  @Override
  public void randomize(long pSeed) {
    a = (int) pSeed;
  }

  @Override
  public double random() {
    a = (a * 1103515245 + 12345) % RAND_MAX123;
    double res = (double) (a * rrmax);
    if (res < 0) {
      res = 0.0 - res;
    }
    return res;
  }

  @Override
  public void cleanup() {
    // empty
  }

}
