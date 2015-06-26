/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen.marchingcubes;

public class Point3f {
  public float x, y, z;

  public Point3f() {

  }

  public Point3f(Point3f pSrc) {
    x = pSrc.x;
    y = pSrc.y;
    z = pSrc.z;
  }

  public Point3f(double pX, double pY, double pZ) {
    set((float) pX, (float) pY, (float) pZ);
  }

  public Point3f(float pX, float pY, float pZ) {
    set(pX, pY, pZ);
  }

  public void set(float pX, float pY, float pZ) {
    x = pX;
    y = pY;
    z = pZ;
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
    Point3f other = (Point3f) obj;
    if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
      return false;
    if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
      return false;
    if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
      return false;
    return true;
  }
}
