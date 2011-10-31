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
package org.jwildfire.envelope;

public class LinearInterpolation extends Interpolation {

  @Override
  public void interpolate() {
    if (snum < 2)
      throw new IllegalArgumentException(String.valueOf(snum));
    dnum = snum * (subdiv + 5);
    dest = new double[dnum];
    int curr = 0;
    for (int i = 0; i < (snum - 1); i++) {
      double x0 = src[i];
      double x1 = src[i + 1];
      double dx = (x1 - x0) / (double) (subdiv - 1);
      for (int j = 0; j < subdiv; j++) {
        dest[curr++] = x0;
        x0 += dx;
      }
    }
    dnum = curr;
  }

}
