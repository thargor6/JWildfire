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


public class XYZPoint {
  public double x;
  public double y;
  public double z;
  public double color;
  // often (but not always) used properties, calculation only if needed
  protected double radius;
  protected boolean validRadius;
  protected double alpha;
  protected boolean validAlpha;
  protected double sinA;
  protected boolean validSinA;
  protected double cosA;
  protected boolean validCosA;

  public void assign(XYZPoint p) {
    x = p.x;
    y = p.y;
    z = p.z;
    color = p.color;
    radius = p.radius;
    validRadius = p.validRadius;
    alpha = p.alpha;
    validAlpha = p.validAlpha;
    sinA = p.sinA;
    validSinA = p.validSinA;
    cosA = p.cosA;
    validCosA = p.validCosA;
  }

  public void invalidate() {
    validRadius = validAlpha = validSinA = validCosA = false;
  }

  public void clear() {
    x = y = z = color = 0.0;
    radius = alpha = sinA = cosA = 0.0;
    validRadius = validAlpha = validSinA = validCosA = false;
  }

  public double getRadius() {
    if (!validRadius) {
      radius = Math.sqrt(x * x + y * y) + Constants.EPSILON;
      validRadius = true;
    }
    return radius;
  }

  public double getAlpha() {
    if (!validAlpha) {
      alpha = Math.atan2(x, y);
      validAlpha = true;
    }
    return alpha;
  }

  public double getSinA() {
    if (!validSinA) {
      sinA = x / getRadius();
      validSinA = true;
    }
    return sinA;
  }

  public double getCosA() {
    if (!validCosA) {
      cosA = y / getRadius();
      validCosA = true;
    }
    return cosA;
  }

}
