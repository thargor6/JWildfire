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
package org.jwildfire.create.tina.random;

public class SimpleRandomNumberGenerator extends RandomNumberGenerator {
  private static int a123 = 1;

  private static final int RAND_MAX123 = 0x7fffffff;

  private static int rand123() {
    return (a123 = a123 * 1103515245 + 12345) % RAND_MAX123;
  }

  private static double rrmax = 1.0 / (double) RAND_MAX123;

  @Override
  public void randomize(long pSeed) {
    a123 = (int) pSeed;
  }

  @Override
  public double random() {
    double res = ((double) (rand123() * rrmax));
    return (res < 0) ? 0.0 - res : res;
  }

}
