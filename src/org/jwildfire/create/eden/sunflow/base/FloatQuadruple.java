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
package org.jwildfire.create.eden.sunflow.base;

public class FloatQuadruple implements SceneElement {
  private final boolean empty;
  private final double w, x, y, z;

  public FloatQuadruple() {
    empty = true;
    w = x = y = z = 0.0;
  }

  public FloatQuadruple(double pW, double pX, double pY, double pZ) {
    w = pW;
    x = pX;
    y = pY;
    z = pZ;
    empty = false;
  }

  @Override
  public String toSceneStringPart() {
    return w + " " + x + " " + y + " " + z;
  }

  @Override
  public boolean isEmpty() {
    return empty;
  }

}
