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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.render.PlotSample;

public class RasterFloatIntWithAccumulatedZBuffer extends RasterFloatInt {
  private static final long serialVersionUID = 1L;

  private float zBuf[][];

  private float nzBuf[][];

  private NormalsCalculator normalsCalculator;

  private final float ZBUF_ZMAX = -Float.MAX_VALUE;

  @Override
  public void allocRaster(int pWidth, int pHeight) {
    super.allocRaster(pWidth, pHeight);

    zBuf = new float[rasterWidth][rasterHeight];
    nzBuf = new float[rasterWidth][rasterHeight];

    // using the same buffer for x and y and z and assuming that z ist written the last
    normalsCalculator = new NormalsCalculator(rasterWidth, rasterHeight, zBuf, nzBuf, nzBuf, nzBuf);

    for (int i = 0; i < pWidth; i++) {
      for (int j = 0; j < pHeight; j++) {
        zBuf[i][j] = ZBUF_ZMAX;
      }
    }

  }

  @Override
  public synchronized void addSamples(PlotSample[] pPlotBuffer, int pCount) {
    for (int i = 0; i < pCount; i++) {
      PlotSample sample = pPlotBuffer[i];

      final int x = sample.screenX, y = sample.screenY;
      if (sample.material < 0) {
        red[x][y] += (float) sample.r;
        green[x][y] += (float) sample.g;
        blue[x][y] += (float) sample.b;
        count[x][y]++;
      }
      else {

        double EPS = 0.2;
        if (nzBuf[x][y] != ZBUF_ZMAX && MathLib.fabs(nzBuf[x][y]) > 0.01) {
          EPS = EPS / nzBuf[x][y];
        }

        final double z = sample.z;
        double zBufPos = zBuf[x][y];

        if (z >= zBufPos - EPS && z <= zBufPos + EPS) {
          red[x][y] += (float) sample.r;
          green[x][y] += (float) sample.g;
          blue[x][y] += (float) sample.b;
          count[x][y]++;
          if (z > zBufPos)
            zBuf[x][y] = (float) z;
          if (count[x][y] % 2 == 0)
            normalsCalculator.refreshNormalsAtLocation(x, y);
        }
        else if (z > zBufPos + EPS) {
          count[x][y] = 1;
          red[x][y] = (float) sample.r;
          green[x][y] = (float) sample.g;
          blue[x][y] = (float) sample.b;
          zBuf[x][y] = (float) z;
        }
      }
    }
  }

  @Override
  public void readRasterPoint(int pX, int pY, RasterPoint pDestRasterPoint) {
    pDestRasterPoint.clear();
    // flame density
    pDestRasterPoint.red = red[pX][pY];
    pDestRasterPoint.green = green[pX][pY];
    pDestRasterPoint.blue = blue[pX][pY];
    pDestRasterPoint.count = count[pX][pY];
  }

}
