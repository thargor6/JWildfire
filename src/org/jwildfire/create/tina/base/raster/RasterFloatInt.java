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

import org.jwildfire.create.tina.render.PlotSample;

public class RasterFloatInt implements AbstractRaster, Serializable {
  private static final long serialVersionUID = 1L;
  private float red[][];
  private float green[][];
  private float blue[][];
  private int count[][];
  private int rasterWidth, rasterHeight;

  @Override
  public void incCount(int pX, int pY) {
    count[pX][pY]++;
  }

  @Override
  public void allocRaster(int pWidth, int pHeight) {
    rasterWidth = pWidth;
    rasterHeight = pHeight;
    red = new float[pWidth][pHeight];
    green = new float[pWidth][pHeight];
    blue = new float[pWidth][pHeight];
    count = new int[pWidth][pHeight];
  }

  @Override
  public void addSample(int pX, int pY, double pRed, double pGreen, double pBlue) {
    red[pX][pY] += (float) pRed;
    green[pX][pY] += (float) pGreen;
    blue[pX][pY] += (float) pBlue;
    count[pX][pY]++;
  }

  @Override
  public long calcSampleCount() {
    long res = 0;
    for (int i = 0; i < rasterWidth; i++) {
      for (int j = 0; j < rasterHeight; j++) {
        res += count[i][j];
      }
    }
    return res;
  }

  @Override
  public void readRasterPoint(int pX, int pY, RasterPoint pDestRasterPoint) {
    pDestRasterPoint.red = red[pX][pY];
    pDestRasterPoint.green = green[pX][pY];
    pDestRasterPoint.blue = blue[pX][pY];
    pDestRasterPoint.count = count[pX][pY];
  }

  @Override
  public void readRasterPointSafe(int pX, int pY, RasterPoint pDestRasterPoint) {
    if (pX >= 0 && pX < rasterWidth && pY >= 0 && pY < rasterHeight) {
      pDestRasterPoint.red = red[pX][pY];
      pDestRasterPoint.green = green[pX][pY];
      pDestRasterPoint.blue = blue[pX][pY];
      pDestRasterPoint.count = count[pX][pY];
    }
    else {
      pDestRasterPoint.red = pDestRasterPoint.green = pDestRasterPoint.blue = 0;
      pDestRasterPoint.count = 0;
    }
  }

  @Override
  public void addSamples(PlotSample[] pPlotBuffer, int pCount) {
    for (int i = 0; i < pCount; i++) {
      PlotSample sample = pPlotBuffer[i];
      int x = sample.x, y = sample.y;
      red[x][y] += (float) sample.r;
      green[x][y] += (float) sample.g;
      blue[x][y] += (float) sample.b;
      count[x][y]++;
    }
  }
}
