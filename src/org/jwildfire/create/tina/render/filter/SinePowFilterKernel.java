/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.acos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.log10;

@SuppressWarnings("serial")
public abstract class SinePowFilterKernel extends FilterKernel {

  @Override
  public double getSpatialSupport() {
    return 1.0;
  }

  abstract int getPower();

  @Override
  public double getFilterCoeff(double x) {
    double res = acos(2.0 * exp((double) getPower() * log10(x)) * 2 - 1) / M_PI;
    return !Double.isInfinite(res) && !Double.isNaN(res) ? res : 0.0;
  }

}
