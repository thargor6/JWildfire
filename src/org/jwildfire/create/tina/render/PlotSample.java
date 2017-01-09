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
package org.jwildfire.create.tina.render;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PlotSample implements Serializable {
  public int screenX, screenY;
  public double r, g, b;
  public double x, y, z;
  public double originalX, originalY, originalZ;
  public double material;
  public double dofDist;
  public boolean receiveOnlyShadows;

  public void set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void set(int screenX, int screenY, double r, double g, double b, double x, double y, double z, double material, double dofDist, double originalX, double originalY, double originalZ, boolean receiveOnlyShadows) {
    this.screenX = screenX;
    this.screenY = screenY;
    this.r = r;
    this.g = g;
    this.b = b;
    this.x = x;
    this.y = y;
    this.z = z;
    this.originalX = originalX;
    this.originalY = originalY;
    this.originalZ = originalZ;
    this.material = material;
    this.dofDist = dofDist;
    this.receiveOnlyShadows = receiveOnlyShadows;
  }
}
