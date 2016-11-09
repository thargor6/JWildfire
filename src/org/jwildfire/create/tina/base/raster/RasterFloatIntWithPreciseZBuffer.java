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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.solidrender.AOCalculator;
import org.jwildfire.create.tina.base.solidrender.ShadowCalculator;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.render.LightViewCalculator;
import org.jwildfire.create.tina.render.PlotSample;
import org.jwildfire.create.tina.render.ZBufferSample;

public class RasterFloatIntWithPreciseZBuffer extends RasterFloatInt {
  private static final long serialVersionUID = 1L;

  private float zBuf[][];

  private float nxBuf[][];
  private float nyBuf[][];
  private float nzBuf[][];

  private float aoBuf[][];
  private float dofBuf[][];

  private float materialBuf[][];

  private AbstractRandomGenerator randGen;

  private NormalsCalculator normalsCalculator;
  private AOCalculator aoCalculator;

  private boolean withDOF;
  private double dofAmount;

  private float[][] originXBuf, originYBuf, originZBuf;

  private double imgSize;

  private LightViewCalculator lightViewCalculator;
  private ShadowCalculator shadowCalculator;

  @Override
  public void allocRaster(Flame flame, int pWidth, int pHeight) {
    super.allocRaster(flame, pWidth, pHeight);

    imgSize = MathLib.sqrt(MathLib.sqr(pWidth) + MathLib.sqr(pHeight));

    zBuf = new float[rasterWidth][rasterHeight];

    nxBuf = new float[rasterWidth][rasterHeight];
    nyBuf = new float[rasterWidth][rasterHeight];
    nzBuf = new float[rasterWidth][rasterHeight];

    materialBuf = new float[rasterWidth][rasterHeight];

    for (int i = 0; i < pWidth; i++) {
      for (int j = 0; j < pHeight; j++) {
        zBuf[i][j] = NormalsCalculator.ZBUF_ZMIN;
      }
    }

    randGen = new MarsagliaRandomGenerator();
    randGen.randomize(System.currentTimeMillis());

    if (flame.getCamDOF() > MathLib.EPSILON) {
      withDOF = true;
      dofBuf = new float[rasterWidth][rasterHeight];
      dofAmount = flame.getCamDOF() * imgSize / (1000.0 * (double) flame.getSpatialOversampling());
    }
    else {
      withDOF = false;
    }

    originXBuf = new float[rasterWidth][rasterHeight];
    originYBuf = new float[rasterWidth][rasterHeight];
    originZBuf = new float[rasterWidth][rasterHeight];

    for (int k = 0; k < originZBuf.length; k++) {
      for (int l = 0; l < originZBuf[0].length; l++) {
        originXBuf[k][l] = NormalsCalculator.ZBUF_ZMIN;
        originYBuf[k][l] = NormalsCalculator.ZBUF_ZMIN;
        originZBuf[k][l] = NormalsCalculator.ZBUF_ZMIN;
      }
    }

    if (flame.getSolidRenderSettings().isAoEnabled()) {
      aoCalculator = new AOCalculator(flame, rasterWidth, rasterHeight, zBuf, nxBuf, nyBuf, nzBuf);
    }

    normalsCalculator = new NormalsCalculator(rasterWidth, rasterHeight, nxBuf, nyBuf, nzBuf, originXBuf, originYBuf, originZBuf);

    if (flame.isWithShadows()) {
      shadowCalculator = new ShadowCalculator(rasterWidth, rasterHeight, originXBuf, originYBuf, originZBuf, imgSize, flame);
    }
    else {
      shadowCalculator = null;
    }
  }

  @Override
  public void notifyInit(LightViewCalculator lightViewCalculator) {
    this.lightViewCalculator = lightViewCalculator;
    if (shadowCalculator != null) {
      shadowCalculator.setLightViewCalculator(lightViewCalculator);
    }
  }

  @Override
  public synchronized void addSamples(PlotSample[] pPlotBuffer, int pCount) {
    for (int i = 0; i < pCount; i++) {
      PlotSample sample = pPlotBuffer[i];
      final int x = sample.screenX, y = sample.screenY;
      final float material = (float) sample.material;
      final float z = (float) sample.z;
      if (z > zBuf[x][y]) {
        count[x][y] = 1;
        zBuf[x][y] = z;
        originXBuf[x][y] = (float) sample.originalX;
        originYBuf[x][y] = (float) sample.originalY;
        originZBuf[x][y] = (float) sample.originalZ;

        if (sample.r >= 0 && sample.g >= 0 && sample.b >= 0) {
          red[x][y] = (float) sample.r;
          green[x][y] = (float) sample.g;
          blue[x][y] = (float) sample.b;
        }
        else {
          red[x][y] = green[x][y] = blue[x][y] = 0;
        }

        if (sample.receiveOnlyShadows) {
          materialBuf[x][y] = -1;
        }
        else {
          materialBuf[x][y] = material;
        }
        if (withDOF) {
          dofBuf[x][y] = (float) sample.dofDist;
        }
        if ((normalsCalculator.hasNormalAtLocation(x, y) && randGen.random() > 0.85) || randGen.random() > 0.5) {
          normalsCalculator.refreshNormalAtLocation(x, y);
        }
      }
    }
  }

  @Override
  public void finalizeRaster() {
    normalsCalculator.refreshAllNormals();
    aoBuf = new float[rasterWidth][rasterHeight];
    if (aoCalculator != null) {
      aoCalculator.refreshAO(aoBuf);
    }
    if (shadowCalculator != null) {
      shadowCalculator.accelerateShadows();
      shadowCalculator.finalizeRaster();
    }
  }

  private double calcDOF(double dofBufVal) {
    return MathLib.fabs(dofBufVal * dofAmount);
  }

  @Override
  public void readRasterPoint(int pX, int pY, RasterPoint pDestRasterPoint) {
    pDestRasterPoint.clear();
    // flame density
    pDestRasterPoint.red = 0.0;
    pDestRasterPoint.green = 0.0;
    pDestRasterPoint.blue = 0.0;
    pDestRasterPoint.count = 0;

    // normal
    if (nxBuf[pX][pY] != NormalsCalculator.ZBUF_ZMIN) {
      // solid colors
      pDestRasterPoint.solidRed = red[pX][pY];
      pDestRasterPoint.solidGreen = green[pX][pY];
      pDestRasterPoint.solidBlue = blue[pX][pY];
      pDestRasterPoint.hasSolidColors = true;

      pDestRasterPoint.hasNormals = true;
      pDestRasterPoint.nx = nxBuf[pX][pY];
      pDestRasterPoint.ny = nyBuf[pX][pY];
      pDestRasterPoint.nz = nzBuf[pX][pY];
      pDestRasterPoint.zBuf = zBuf[pX][pY];
    }
    // ssao
    if (aoBuf != null) {
      pDestRasterPoint.hasSSAO = true;
      pDestRasterPoint.ao = aoBuf[pX][pY];
    }
    // mat
    pDestRasterPoint.hasMaterial = true;
    pDestRasterPoint.material = materialBuf[pX][pY];

    if (withDOF) {
      pDestRasterPoint.dofDist = calcDOF(dofBuf[pX][pY]);
    }
    else {
      pDestRasterPoint.dofDist = 0.0;
    }

    if (shadowCalculator != null) {
      shadowCalculator.calcShadows(pX, pY, pDestRasterPoint);
    }
    else {
      pDestRasterPoint.hasShadows = false;
    }
  }

  @Override
  public void addShadowMapSamples(int pShadowMapIdx, PlotSample[] pPlotBuffer, int pCount) {
    shadowCalculator.addShadowMapSamples(pShadowMapIdx, pPlotBuffer, pCount);
  }

  @Override
  public void readZBuffer(int pX, int pY, ZBufferSample pDest) {
    pDest.clear();
    if (zBuf[pX][pY] != NormalsCalculator.ZBUF_ZMIN) {
      pDest.hasZ = true;
      pDest.z = zBuf[pX][pY];
    }
  }

  @Override
  public LightViewCalculator getLightViewCalculator() {
    return lightViewCalculator;
  }

}
