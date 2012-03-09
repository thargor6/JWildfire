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
package org.jwildfire.create.eden.primitive;

import org.jwildfire.base.Tools;

public class Point {
  private double x;
  private double y;
  private double z;

  public Point() {
  }

  public Point(double pX, double pY, double pZ) {
    x = pX;
    y = pY;
    z = pZ;
  }

  public double getX() {
    return x;
  }

  public void setX(double pX) {
    x = limitDegress(pX);
  }

  public double getY() {
    return y;
  }

  public void setY(double pY) {
    y = limitDegress(pY);
  }

  public double getZ() {
    return z;
  }

  public void setZ(double pZ) {
    z = limitDegress(pZ);
  }

  private double limitDegress(double pZ) {
    double deg = pZ;
    while (deg > 360.0) {
      deg -= 360.0;
    }
    while (deg < -360.0) {
      deg += 360.0;
    }
    return deg;
  }

  public void setValue(double pX, double pY, double pZ) {
    x = pX;
    y = pY;
    z = pZ;
  }

  public void setValue(double pValue) {
    x = y = z = pValue;
  }

  public void assign(Point pSrc) {
    x = pSrc.getX();
    y = pSrc.getY();
    z = pSrc.getZ();
  }

  public void translate(double pX, double pY, double pZ) {
    x += pX;
    y += pY;
    z += pZ;
  }

  public void scale(double pX, double pY, double pZ) {
    x *= pX;
    y *= pZ;
    z *= pZ;
  }

  public void rotate(double pX, double pY, double pZ) {
    if (Math.abs(pX) > Tools.EPSILON) {
      double a = Math.toRadians(pX);
      double sinX = Math.sin(a);
      double cosX = Math.cos(a);
      double yr = cosX * y - sinX * z;
      double zr = sinX * y + cosX * z;
      y = yr;
      z = zr;
    }
    if (Math.abs(pY) > Tools.EPSILON) {
      double a = Math.toRadians(pY);
      double sinY = Math.sin(a);
      double cosY = Math.cos(a);
      double xr = cosY * x + sinY * z;
      double zr = -sinY * x + cosY * z;
      x = xr;
      z = zr;
    }
    if (Math.abs(pZ) > Tools.EPSILON) {
      double a = Math.toRadians(pZ);
      double sinZ = Math.sin(a);
      double cosZ = Math.cos(a);
      double xr = cosZ * x - sinZ * y;
      double yr = sinZ * x + cosZ * y;
      x = xr;
      y = yr;
    }

  }
}
