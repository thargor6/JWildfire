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

public class BezierInterpolation extends Interpolation {

  @Override
  public void interpolate() {
    if (snum < 3)
      throw new IllegalArgumentException(String.valueOf(snum));
    double du = (double) 1.0 / (double) subdiv;
    dnum = snum * (subdiv + 5);
    dest = new double[dnum];
    int j = 0;
    int i = 1;
    double xm1, xm2, xm3;
    do {
      double u = 0.0;
      do {
        if (i == 1)
          xm1 = src[i - 1];
        else
          xm1 = 0.5 * (src[i - 1] + src[i]);
        if (i == (snum - 2))
          xm3 = src[i + 1];
        else
          xm3 = 0.5 * (src[i] + src[i + 1]);
        xm2 = src[i];
        dest[j] = evalBezier(u, xm1, xm2, xm3);
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

  double evalBezier(double u, double xa, double xb, double xc) {
    double c, h;
    if ((u < (0.0 - SMALL)) || (u > (1.0 + SMALL)))
      throw new IllegalStateException();
    /* c= u*u*(     xa-2.0*xb+xc);
     c+=  u*(-2.0*xa+2.0*xb   );
     c+=    (     xa          );*/
    h = u * u;
    c = xa - xb - xb + xc;
    c *= h;
    h = -xa - xa + xb + xb;
    h *= u;
    c += h;
    c += xa;
    return (c);
  }

}
