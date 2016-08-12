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
package org.jwildfire.create.tina.render.postdof;

public class PostDOFSample {
  private final int x, y;
  private final float z, dofDist;
  private int r, g, b;

  public PostDOFSample(int x, int y, float z, float dofDist, int r, int g, int b) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.dofDist = dofDist;
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public float getZ() {
    return z;
  }

  public float getDofDist() {
    return dofDist;
  }

  public int getR() {
    return r;
  }

  public int getG() {
    return g;
  }

  public int getB() {
    return b;
  }

  public void setR(int r) {
    this.r = r;
  }

  public void setG(int g) {
    this.g = g;
  }

  public void setB(int b) {
    this.b = b;
  }
}
