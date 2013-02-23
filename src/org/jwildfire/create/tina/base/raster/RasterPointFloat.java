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
package org.jwildfire.create.tina.base.raster;

import java.io.Serializable;

public class RasterPointFloat implements AbstractRasterPoint, Serializable {
  private static final long serialVersionUID = 1L;
  private float red;
  private float green;
  private float blue;
  private long count;

  @Override
  public double getRed() {
    return red;
  }

  @Override
  public void setRed(double pRed) {
    red = (float) pRed;
  }

  @Override
  public double getGreen() {
    return green;
  }

  @Override
  public void setGreen(double pGreen) {
    green = (float) pGreen;
  }

  @Override
  public double getBlue() {
    return blue;
  }

  @Override
  public void setBlue(double pBlue) {
    blue = (float) pBlue;
  }

  @Override
  public long getCount() {
    return count;
  }

  @Override
  public void setCount(long pCount) {
    count = pCount;
  }

  @Override
  public AbstractRasterPoint[][] allocRaster(int pWidth, int pHeight) {
    RasterPointFloat[][] raster = new RasterPointFloat[pHeight][pWidth];
    for (int i = 0; i < pHeight; i++) {
      for (int j = 0; j < pWidth; j++) {
        raster[i][j] = new RasterPointFloat();
      }
    }
    return raster;
  }

  @Override
  public void incCount() {
    count++;
  }

}
