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
package org.jwildfire.base.mathlib;

import odk.lang.DoubleWrapper;

public interface BaseMathLib {

  public double sin(double a);

  public double cos(double a);

  public void sinAndCos(double a, DoubleWrapper sine, DoubleWrapper cosine);

  public double tan(double a);

  public double atan2(double y, double x);

  public double exp(double a);

  public double sqrt(double a);

  public double pow(double value, double power);

  public double floor(double value);

  public double round(double value);

  public double log10(double value);

  public double log(double value);

  public double sinh(double value);

  public double cosh(double value);

  public double tanh(double value);

  public double atan(double value);

  public double acos(double value);

  public double asin(double value);
}
