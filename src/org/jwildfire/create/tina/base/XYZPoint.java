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
package org.jwildfire.create.tina.base;

import static org.jwildfire.base.MathLib.SMALL_EPSILON;
import static org.jwildfire.base.MathLib.atan2;
import static org.jwildfire.base.MathLib.sqrt;

import org.jwildfire.create.tina.variation.FlameTransformationContext;

public final class XYZPoint {
  public double x;
  public double y;
  public double z;
  public double color;
  // custom RGB colors
  public boolean rgbColor = false;
  public double redColor;
  public double greenColor;
  public double blueColor;

  // often (but not always) used properties, calculation only if needed
  protected double sumsq;
  protected boolean validSumsq;
  protected double sqrt;
  protected boolean validSqrt;
  protected double atan;
  protected boolean validAtan;
  protected double atanYX;
  protected boolean validAtanYX;
  protected double sinA;
  protected boolean validSinA;
  protected double cosA;
  protected boolean validCosA;

  public void assign(XYZPoint p) {
    x = p.x;
    y = p.y;
    z = p.z;
    color = p.color;
    sumsq = p.sumsq;
    validSumsq = p.validSumsq;
    sqrt = p.sqrt;
    validSqrt = p.validSqrt;
    atan = p.atan;
    validAtan = p.validAtan;
    atanYX = p.atanYX;
    validAtanYX = p.validAtanYX;
    sinA = p.sinA;
    validSinA = p.validSinA;
    cosA = p.cosA;
    validCosA = p.validCosA;
    rgbColor = p.rgbColor;
    redColor = p.redColor;
    greenColor = p.greenColor;
    blueColor = p.blueColor;
  }

  public void invalidate() {
    validSumsq = validSqrt = validAtan = validAtanYX = validSinA = validCosA = false;
  }

  public void clear() {
    rgbColor = false;
    redColor = greenColor = blueColor = 0.0;
    x = y = z = color = 0.0;
    sumsq = sqrt = atan = atanYX = sinA = cosA = 0.0;
    validSumsq = validSqrt = validAtan = validAtanYX = validSinA = validCosA = false;
  }

  public double getPrecalcSumsq() {
    if (!validSumsq) {
      sumsq = x * x + y * y;
      validSumsq = true;
    }
    return sumsq;
  }

  public double getPrecalcSqrt(FlameTransformationContext pContext) {
    if (!validSqrt) {
      sqrt = sqrt(x * x + y * y) + SMALL_EPSILON;
      validSqrt = true;
    }
    return sqrt;
  }

  public double getPrecalcAtan(FlameTransformationContext pContext) {
    if (!validAtan) {
      atan = atan2(x, y);
      validAtan = true;
    }
    return atan;
  }

  public double getPrecalcAtanYX(FlameTransformationContext pContext) {
    if (!validAtanYX) {
      atanYX = atan2(y, x);
      validAtanYX = true;
    }
    return atanYX;
  }

  public double getPrecalcSinA(FlameTransformationContext pContext) {
    if (!validSinA) {
      sinA = x / getPrecalcSqrt(pContext);
      validSinA = true;
    }
    return sinA;
  }

  public double getPrecalcCosA(FlameTransformationContext pContext) {
    if (!validCosA) {
      cosA = y / getPrecalcSqrt(pContext);
      validCosA = true;
    }
    return cosA;
  }

}