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
package org.jwildfire.create.tina.base.raster;

import java.io.Serializable;

public class RasterPoint implements Serializable {
  private static final long serialVersionUID = 1L;
  public double red;
  public double green;
  public double blue;
  public long count;

  public double solidRed;
  public double solidGreen;
  public double solidBlue;
  public boolean hasSolidColors;

  public boolean hasNormals;
  public double nx, ny, nz, zBuf;
  public boolean hasSSAO;
  public double ao;
  public boolean hasMaterial;
  public double material;
  public double dofDist;
  public boolean hasShadows;
  public double visibility[];

  public RasterPoint(int lightCount) {
    if (lightCount > 0) {
      visibility = new double[lightCount];
    }
    else {
      visibility = null;
    }
  }

  public void clear() {
    red = green = blue = 0.0;
    solidRed = solidGreen = solidBlue = 0.0;
    count = 0;
    nx = ny = nz = zBuf = 0.0;
    ao = 0.0;
    material = 0.0;
    dofDist = 0.0;
    hasNormals = hasSSAO = hasMaterial = hasShadows = hasSolidColors = false;
  }
}
