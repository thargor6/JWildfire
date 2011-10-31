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

public class SplineInterpolation extends Interpolation {

  @Override
  public void interpolate() {
    if (snum < 3)
      throw new IllegalArgumentException(String.valueOf(snum));
    double du = (double) 1.0 / (double) subdiv;
    dnum = snum * (subdiv + 5);
    dest = new double[dnum];

    int j = 0;
    int i = 0;
    do {
      double u = 0.0;
      do {
        if (i == 0) {
          dest[j] = evalSpline(u, src[i], src[i], src[i + 1], src[i + 2]);
        }
        else if (i == (snum - 2)) {
          dest[j] = evalSpline(u, src[i - 1], src[i], src[i + 1], src[i + 1]);
        }
        else {
          dest[j] = evalSpline(u, src[i - 1], src[i], src[i + 1], src[i + 2]);
        }
        u += du;
        j++;
        if (j >= dnum)
          throw new IllegalStateException();
      }
      while (u < (1.0 + SMALL));
      i++;
    }
    while (i < (snum - 1));
    dnum = j;
  }

  double evalSpline(double u, double xa, double xb, double xc, double xd) {
    double c, B = 0.5;
    if ((u < (0.0 - SMALL)) || (u > (1.0 + SMALL)))
      throw new IllegalStateException("evalCatmullRom");
    c = u * u * u * (-B * xa + (2.0 - B) * xb + (B - 2.0) * xc + B * xd);
    c = c + u * u * (2 * B * xa + (B - 3.0) * xb + (3.0 - 2.0 * B) * xc - B * xd);
    c = c + u * (-B * xa + B * xc);
    c = c + (xb);
    return (c);
  }

}
