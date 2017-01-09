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

import org.jwildfire.create.tina.base.raster.RasterPoint;
import org.jwildfire.image.Pixel;

public class LogDensityPoint {
  public double red;
  public double green;
  public double blue;
  public double intensity;

  public boolean hasSolidColors;
  public double solidRed;
  public double solidGreen;
  public double solidBlue;

  public double nx, ny, nz;
  public double ao;
  public double material;
  public double dofDist;
  public boolean receiveOnlyShadows;

  public final RasterPoint rp;
  public final RasterPoint lu;
  public final RasterPoint ru;
  public final RasterPoint lb;
  public final RasterPoint rb;

  public final Pixel toolPixel = new Pixel();
  public int bgRed, bgGreen, bgBlue;

  public LogDensityPoint(int lightCount) {
    rp = new RasterPoint(lightCount);
    lu = new RasterPoint(lightCount);
    ru = new RasterPoint(lightCount);
    lb = new RasterPoint(lightCount);
    rb = new RasterPoint(lightCount);
  }

  public void clear() {
    red = green = blue = intensity = 0.0;
    solidRed = solidGreen = solidBlue = 0.0;
    hasSolidColors = receiveOnlyShadows = false;
    nx = ny = nz = ao = material = dofDist = 0.0;
  }

  public void clip() {
    if (red < 0.0) {
      red = 0.0;
    }
    if (green < 0.0) {
      green = 0.0;
    }
    if (blue < 0.0) {
      blue = 0.0;
    }
    if (intensity < 0.0) {
      intensity = 0.0;
    }
  }

}
