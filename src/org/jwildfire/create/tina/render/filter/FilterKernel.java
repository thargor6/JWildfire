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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.sin;

public abstract class FilterKernel {
  public abstract double getSpatialSupport();

  public abstract double getFilterCoeff(double x);

  protected double sinc(double x) {
    x *= M_PI;
    if (x != 0)
      return (sin(x) / x);
    return (1.0);
  }

  public double getFilterAdjust(double pFilterRadius, int pOversample) {
    if (pFilterRadius < EPSILON) {
      return 0.0;
    }
    else {
      int fw = (int) (2.0 * pOversample * getSpatialSupport() * pFilterRadius);
      int filterSize = fw + 1;
      if ((filterSize + pOversample) % 2 != 0)
        filterSize++;
      if (fw > 0) {
        return (1.0 * getSpatialSupport() * filterSize) / fw;
      }
      else {
        return 1.0;
      }
    }
  }

  public int getFilterSize(double pFilterRadius, int pOversample) {
    if (pFilterRadius < EPSILON) {
      return 0;
    }
    else {
      int fw = (int) (2.0 * pOversample * getSpatialSupport() * pFilterRadius);
      int filterSize = fw + 1;
      if ((filterSize + pOversample) % 2 != 0)
        filterSize++;
      return filterSize;
    }
  }

}
