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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.SMALL_EPSILON;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.io.Serializable;

import org.jwildfire.create.tina.edit.Assignable;

public final class XYZPoint implements Serializable, Assignable<XYZPoint> {
  private static final long serialVersionUID = 1L;
  public double x;
  public double y;
  public double z;
  public double color;
  public double modGamma;
  public double modContrast;
  public double modSaturation;
  public double modHue;
  // custom RGB colors
  public boolean rgbColor;
  public double redColor;
  public double greenColor;
  public double blueColor;
  // material index for solid rendering
  public double material;
  public boolean receiveOnlyShadows;
  // supress sample from drawing
  public boolean doHide;
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
  public double weightMapValue;

  public XYZPoint() {

  }

  public XYZPoint(XYZPoint p) {
    assign(p);
  }

  @Override
  public void assign(XYZPoint p) {
    x = p.x;
    y = p.y;
    z = p.z;
    color = p.color;
    material = p.material;
    receiveOnlyShadows = p.receiveOnlyShadows;
    modGamma = p.modGamma;
    modContrast = p.modContrast;
    modSaturation = p.modSaturation;
    modHue = p.modHue;
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
    doHide = p.doHide;
    weightMapValue = p.weightMapValue;
  }

  @Override
  public XYZPoint makeCopy() {
    XYZPoint res = new XYZPoint();
    res.assign(this);
    return res;
  }

  public void invalidate() {
    validSumsq = validSqrt = validAtan = validAtanYX = validSinA = validCosA = false;
  }

  public void clear() {
    rgbColor = doHide = false;
    redColor = greenColor = blueColor = 0.0;
    x = y = z = color = material = modGamma = modContrast = modSaturation = modHue = 0.0;
    sumsq = sqrt = atan = atanYX = sinA = cosA = 0.0;
    validSumsq = validSqrt = validAtan = validAtanYX = validSinA = validCosA = receiveOnlyShadows = false;
  }

  public double getPrecalcSumsq() {
    if (!validSumsq) {
      sumsq = x * x + y * y;
      validSumsq = true;
    }
    return sumsq;
  }

  public double getPrecalcSqrt() {
    if (!validSqrt) {
      sqrt = sqrt(x * x + y * y) + SMALL_EPSILON;
      validSqrt = true;
    }
    return sqrt;
  }

  public double getPrecalcAtan() {
    if (!validAtan) {
      atan = atan2(x, y);
      validAtan = true;
    }
    return atan;
  }

  public double getPrecalcAtanYX() {
    if (!validAtanYX) {
      atanYX = atan2(y, x);
      validAtanYX = true;
    }
    return atanYX;
  }

  public double getPrecalcSinA() {
    if (!validSinA) {
      sinA = x / getPrecalcSqrt();
      validSinA = true;
    }
    return sinA;
  }

  public double getPrecalcCosA() {
    if (!validCosA) {
      cosA = y / getPrecalcSqrt();
      validCosA = true;
    }
    return cosA;
  }

  @Override
  public boolean isEqual(XYZPoint pSrc) {
    if (fabs(x - pSrc.x) > EPSILON || fabs(y - pSrc.y) > EPSILON ||
        fabs(z - pSrc.z) > EPSILON || fabs(color - pSrc.color) > EPSILON ||
        fabs(modGamma - pSrc.modGamma) > EPSILON ||
        fabs(modContrast - pSrc.modContrast) > EPSILON ||
        fabs(modSaturation - pSrc.modSaturation) > EPSILON ||
        fabs(modHue - pSrc.modHue) > EPSILON ||
        rgbColor != pSrc.rgbColor || fabs(redColor - pSrc.redColor) > EPSILON ||
        fabs(greenColor - pSrc.greenColor) > EPSILON || fabs(blueColor - pSrc.blueColor) > EPSILON ||
        doHide != pSrc.doHide || fabs(material - pSrc.material) > EPSILON || receiveOnlyShadows != pSrc.receiveOnlyShadows) {
      return false;
    }
    return true;
  }

  public boolean isNaN() {
    return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
  }

  public boolean isInfinite() {
    return Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z);
  }
}