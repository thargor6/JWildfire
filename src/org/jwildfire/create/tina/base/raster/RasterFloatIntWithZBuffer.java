/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.solidrender.AOCalculator;
import org.jwildfire.create.tina.base.solidrender.ShadowCalculator;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.render.LightViewCalculator;
import org.jwildfire.create.tina.render.PlotSample;
import org.jwildfire.create.tina.render.ZBufferSample;

public class RasterFloatIntWithZBuffer extends RasterFloatInt {

  private float zBuf[][];

  @Override
  public void allocRaster(Flame flame, int pWidth, int pHeight, int pOversample, double pSampleDensity) {
    super.allocRaster(flame, pWidth, pHeight, pOversample, pSampleDensity);
    zBuf = new float[rasterWidth][rasterHeight];
    for (int i = 0; i < pWidth; i++) {
      for (int j = 0; j < pHeight; j++) {
        zBuf[i][j] = NormalsCalculator.ZBUF_ZMIN;
      }
    }
  }

  @Override
  public /* synchronized */ void addSamples(PlotSample[] pPlotBuffer, int pCount) {
    for (int i = 0; i < pCount; i++) {
      PlotSample sample = pPlotBuffer[i];
      int x = sample.screenX, y = sample.screenY;
      red[x][y] += (float) sample.r;
      green[x][y] += (float) sample.g;
      blue[x][y] += (float) sample.b;
      count[x][y]++;
      final float z = (float) sample.z;


      if (z > zBuf[x][y]) {
        double luminosity = 0.299 * sample.r + 0.588 * sample.g + 0.113 * sample.b;
        if (luminosity > 8) {
          zBuf[x][y] = z;
        }
      }
    }
  }

  @Override
  public void readZBuffer(int pX, int pY, ZBufferSample pDest) {
    pDest.clear();
    if (zBuf[pX][pY] != NormalsCalculator.ZBUF_ZMIN) {
      pDest.hasZ = true;
      pDest.z = zBuf[pX][pY];
    }
  }

}
