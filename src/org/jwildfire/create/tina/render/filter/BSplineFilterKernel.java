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
package org.jwildfire.create.tina.render.filter;

// Filter based on code of the flam3 project: http://flam3.com/index.cgi?&menu=code
public class BSplineFilterKernel extends FilterKernel {

  @Override
  public double getSpatialSupport() {
    return 2.0;
  }

  @Override
  public double getFilterCoeff(double t) {
    /* box (*) box (*) box (*) box */
    double tt;

    if (t < 0)
      t = -t;
    if (t < 1) {
      tt = t * t;
      return ((.5 * tt * t) - tt + (2.0 / 3.0));
    }
    else if (t < 2) {
      t = 2 - t;
      return ((1.0 / 6.0) * (t * t * t));
    }
    return (0.0);
  }

}
