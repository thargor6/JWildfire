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
package org.jwildfire.create.tina.meshgen.marchingcubes;

public class Point2f {
  public float u, v;

  public Point2f() {

  }

  public Point2f(double pU, double pV) {
    set((float) pU, (float) pV);
  }

  public Point2f(float pU, float pV) {
    set(pU, pV);
  }

  public void set(float pU, float pV) {
    u = pU;
    v = pV;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(u);
    result = prime * result + Float.floatToIntBits(v);
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
    Point2f other = (Point2f) obj;
    if (Float.floatToIntBits(u) != Float.floatToIntBits(other.u))
      return false;
    if (Float.floatToIntBits(v) != Float.floatToIntBits(other.v))
      return false;
    return true;
  }
}
