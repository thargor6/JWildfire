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
public class HermiteFilterKernel extends FilterKernel {

  @Override
  public double getSpatialSupport() {
    return 1.0;
  }

  @Override
  public double getFilterCoeff(double t) {
    /* f(t) = 2|t|^3 - 3|t|^2 + 1, -1 <= t <= 1 */
    if (t < 0.0)
      t = -t;
    if (t < 1.0)
      return ((2.0 * t - 3.0) * t * t + 1.0);
    return (0.0);
  }

}
