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

public final class JavaMathLibImpl implements BaseMathLib {

  @Override
  public double sin(double a) {
    return Math.sin(a);
  }

  @Override
  public double cos(double a) {
    return Math.cos(a);
  }

  @Override
  public void sinAndCos(double a, DoubleWrapper sine, DoubleWrapper cosine) {
    sine.value = Math.sin(a);
    cosine.value = Math.cos(a);
  }

  @Override
  public double tan(double a) {
    return Math.tan(a);
  }

  @Override
  public double atan2(double y, double x) {
    return Math.atan2(y, x);
  }

  @Override
  public double exp(double a) {
    return Math.exp(a);
  }

  @Override
  public double sqrt(double a) {
    return Math.sqrt(a);
  }

  @Override
  public double pow(double value, double power) {
    return Math.pow(value, power);
  }

  @Override
  public double floor(double value) {
    return Math.floor(value);
  }

  @Override
  public double round(double value) {
    return Math.round(value);
  }

  @Override
  public double log10(double value) {
    return Math.log10(value);
  }

  @Override
  public double log(double value) {
    return Math.log(value);
  }

  @Override
  public double sinh(double value) {
    return Math.sinh(value);
  }

  @Override
  public double cosh(double value) {
    return Math.cosh(value);
  }

  @Override
  public double tanh(double value) {
    return Math.tanh(value);
  }

  @Override
  public double atan(double value) {
    return Math.atan(value);
  }

  @Override
  public double acos(double value) {
    return Math.acos(value);
  }

  @Override
  public double asin(double value) {
    return Math.asin(value);
  }

}
