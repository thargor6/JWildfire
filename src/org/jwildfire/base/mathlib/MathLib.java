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
package org.jwildfire.base.mathlib;

import odk.lang.DoubleWrapper;

public final class MathLib {
  public final static double SMALL_EPSILON = 1.0e-300;
  public static final double EPSILON = 0.00000001;
  public final static int TRUE = 1;
  public final static int FALSE = 0;
  public final static double M_PI = Math.PI;
  public final static double M_PI_2 = M_PI * 0.5;
  public final static double M_PI_4 = M_PI * 0.25;
  public final static double M_1_PI = 1.0 / M_PI;
  public final static double M_2_PI = 2.0 / M_PI;
  public static final double M_2PI = 2.0 * M_PI;
  public static final double M_1_2PI = M_1_PI / 2.0;
  public static final double M_SQRT2 = Math.sqrt(2.0);
  public final static double M_E = 2.7182818284590452354;
  public final static double M_LOG2E = 1.4426950408889634074;
  public final static double C_255 = 255.0;

  private static BaseMathLib baseLib = BaseMathLibType.FAST_MATH.createInstance();

  public static final int iabs(int var) {
    return Math.abs(var);
  }

  public static final double fabs(double var) {
    return Math.abs(var);
  }

  public static final int sign(double val) {
    if (val > 0.0)
      return 1;
    else if (val < 0.0)
      return -1;
    else
      return 0;
  }

  public static final void sinAndCos(double a, DoubleWrapper sine, DoubleWrapper cosine) {
    baseLib.sinAndCos(a, sine, cosine);
  }

  public static final double sin(double a) {
    return baseLib.sin(a);
  }

  public static final double cos(double a) {
    return baseLib.cos(a);
  }

  public static final double tan(double a) {
    return baseLib.tan(a);
  }

  public static final double fmod(double a, double b) {
    return a % b;
  }

  public static final double sqr(double a) {
    return a * a;
  }

  public static final double atan2(double y, double x) {
    return baseLib.atan2(y, x);
  }

  public static final double exp(double a) {
    return baseLib.exp(a);
  }

  public static final double sqrt(double a) {
    return baseLib.sqrt(a);
  }

  public static final double pow(double value, double power) {
    return baseLib.pow(value, power);
  }

  public static final double floor(double value) {
    return baseLib.floor(value);
  }

  public static final double round(double value) {
    return baseLib.round(value);
  }

  public static final double log10(double value) {
    return baseLib.log10(value);
  }

  public static final double log(double value) {
    return baseLib.log(value);
  }

  public static final double sinh(double value) {
    return baseLib.sinh(value);
  }

  public static final double cosh(double value) {
    return baseLib.cosh(value);
  }

  public static final double tanh(double value) {
    return baseLib.tanh(value);
  }

  public static final double min(double a, double b) {
    return Math.min(a, b);
  }

  public static final int iMin(int a, int b) {
    return a < b ? a : b;
  }

  public static final int iMax(int a, int b) {
    return a > b ? a : b;
  }

  public static final double max(double a, double b) {
    return Math.max(a, b);
  }

  public static final double atan(double value) {
    return baseLib.atan(value);
  }

  public static final double acos(double value) {
    return baseLib.acos(value);
  }

  public static final double asin(double value) {
    return baseLib.asin(value);
  }

  public static final double rint(double value) {
    return Math.rint(value);
  }

  public static final double trunc(double value) {
    return (value < 0) ? Math.ceil(value) : Math.floor(value);
  }

  public static final double frac(double value) {
    return value - trunc(value);
  }

  public static final double acosh(double d) {
    return log(sqrt(pow(d, 2.0) - 1.0) + d);
  }

  // Quick erf code taken from http://introcs.cs.princeton.edu/java/21function/ErrorFunction.java.html
  // Copyright (C) 2000-2010, Robert Sedgewick and Kevin Wayne. 
  //fractional error in math formula less than 1.2 * 10 ^ -7.
  // although subject to catastrophic cancellation when z in very close to 0
  // from Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2
  public static double erf(double z) {
    double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

    // use Horner's method
    double ans = 1 - t * Math.exp(-z * z - 1.26551223 +
        t * (1.00002368 +
        t * (0.37409196 +
            t * (0.09678418 +
                t * (-0.18628806 +
                    t * (0.27886807 +
                        t * (-1.13520398 +
                            t * (1.48851587 +
                                t * (-0.82215223 +
                                    t * (0.17087277))))))))));
    if (z >= 0)
      return ans;
    else
      return -ans;
  }
  //by Robert Sedgewick and Kevin Wayne copyright etc...
  public static final double lgamma(double x) {
     double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
     double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
                      + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
                      +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
     return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
  }

  public static void setBaseMathLibType(BaseMathLibType pType) {
    baseLib = pType.createInstance();
  }
}
