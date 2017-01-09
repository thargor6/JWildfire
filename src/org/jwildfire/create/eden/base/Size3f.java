/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.eden.base;

public class Size3f {
  private float x, y, z;

  public static final Size3f OPTIONAL = new Size3f(1.0f, 1.0f, 1.0f);

  public Size3f() {

  }

  public Size3f(float pX, float pY, float pZ) {
    x = pX;
    y = pY;
    z = pZ;
  }

  public Size3f(double pX, double pY, double pZ) {
    x = (float) pX;
    y = (float) pY;
    z = (float) pZ;
  }

  public float getX() {
    return x;
  }

  public void setX(float pX) {
    x = pX;
  }

  public float getY() {
    return y;
  }

  public void setY(float pY) {
    y = pY;
  }

  public float getZ() {
    return z;
  }

  public void setZ(float pZ) {
    z = pZ;
  }

  public void assign(Size3f pSource) {
    x = pSource.getX();
    y = pSource.getY();
    z = pSource.getZ();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(x);
    result = prime * result + Float.floatToIntBits(y);
    result = prime * result + Float.floatToIntBits(z);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Size3f other = (Size3f) obj;
    if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
      return false;
    if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
      return false;
    if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
      return false;
    return true;
  }

}
