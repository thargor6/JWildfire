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
    x = pX;
  }

  public double getY() {
    return y;
  }

  public void setY(double pY) {
    y = pY;
  }

  public double getZ() {
    return z;
  }

  public void setZ(double pZ) {
    z = pZ;
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
}
