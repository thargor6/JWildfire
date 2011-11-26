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
package org.jwildfire.create.tina.render;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.image.SimpleImage;

public class LogDensityFilter {
  private final Flame flame;
  private RasterPoint[][] raster;
  private int rasterWidth, rasterHeight;
  private final static double FILTER_CUTOFF = 1.8;
  private final int FILTER_WHITE = (1 << 26);
  private final double BRIGHTNESS_SCALE = 2.3 * 268.0;
  private final int PRECALC_LOG_ARRAY_SIZE = 6000;
  private double filter[][];
  private int noiseFilterSize;
  private double precalcLogArray[]; // precalculated log-values
  private double k1, k2;

  public LogDensityFilter(Flame pFlame) {
    flame = pFlame;
    initFilter(pFlame);
  }

  private void initFilter(Flame pFlame) {
    int fw = (int) (2.0 * FILTER_CUTOFF * pFlame.getSpatialFilterRadius());
    noiseFilterSize = fw + 1;
    if (noiseFilterSize % 2 == 0)
      noiseFilterSize++;
    double adjust;
    if (fw > 0) {
      adjust = (1.0 * FILTER_CUTOFF * noiseFilterSize) / fw;
    }
    else {
      adjust = 1.0;
    }
    filter = new double[noiseFilterSize][noiseFilterSize];
    for (int i = 0; i < noiseFilterSize; i++) {
      for (int j = 0; j < noiseFilterSize; j++) {
        double ii = ((2.0 * i + 1.0) / noiseFilterSize - 1.0) * adjust;
        double jj = ((2.0 * j + 1.0) / noiseFilterSize - 1.0) * adjust;
        filter[i][j] = Math.exp(-2.0 * (ii * ii + jj * jj));
      }
    }
    // normalize
    {
      double t = 0;
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          t += filter[i][j];
        }
      }
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          filter[i][j] = filter[i][j] / t;
        }
      }
    }
  }

  public int getNoiseFilterSize() {
    return noiseFilterSize;
  }

  public void setRaster(RasterPoint[][] pRaster, int pRasterWidth, int pRasterHeight, SimpleImage pImage) {
    raster = pRaster;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    k1 = (flame.getContrast() * BRIGHTNESS_SCALE * flame.getBrightness() * FILTER_WHITE) / 256.0;
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double area = ((double) pImage.getImageWidth() * (double) pImage.getImageHeight()) / (pixelsPerUnit * pixelsPerUnit);
    k2 = 1.0 / (flame.getContrast() * area * (double) flame.getWhiteLevel() * flame.getSampleDensity());

    precalcLogArray = new double[PRECALC_LOG_ARRAY_SIZE + 1];
    for (int i = 1; i <= PRECALC_LOG_ARRAY_SIZE; i++) {
      precalcLogArray[i] = (k1 * Math.log10(1 + flame.getWhiteLevel() * i * k2)) / (flame.getWhiteLevel() * i);
    }
  }

  public void transformPoint(LogDensityPoint pFilteredPnt, int pX, int pY) {
    if (noiseFilterSize > 1) {
      pFilteredPnt.clear();
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          RasterPoint point = getRasterPoint(pX + j, pY + i);
          double logScale;
          if (point.count < precalcLogArray.length) {
            logScale = precalcLogArray[point.count];
          }
          else {
            logScale = (k1 * Math.log10(1.0 + flame.getWhiteLevel() * point.count * k2)) / (flame.getWhiteLevel() * point.count);
          }
          pFilteredPnt.red += filter[i][j] * logScale * point.red;
          pFilteredPnt.green += filter[i][j] * logScale * point.green;
          pFilteredPnt.blue += filter[i][j] * logScale * point.blue;
          pFilteredPnt.intensity += filter[i][j] * logScale * point.count;
        }
      }

      pFilteredPnt.red /= FILTER_WHITE;
      pFilteredPnt.green /= FILTER_WHITE;
      pFilteredPnt.blue /= FILTER_WHITE;
      pFilteredPnt.intensity = flame.getWhiteLevel() * pFilteredPnt.intensity / FILTER_WHITE;
    }
    else {
      RasterPoint point = getRasterPoint(pX, pY);
      double ls;
      if (point.count < precalcLogArray.length) {
        ls = precalcLogArray[point.count] / FILTER_WHITE;
      }
      else {
        ls = (k1 * Math.log10(1.0 + flame.getWhiteLevel() * point.count * k2)) / (flame.getWhiteLevel() * point.count) / FILTER_WHITE;
      }
      pFilteredPnt.red = ls * point.red;
      pFilteredPnt.green = ls * point.green;
      pFilteredPnt.blue = ls * point.blue;
      pFilteredPnt.intensity = ls * point.count * flame.getWhiteLevel();
    }
  }

  public void transformPointSimple(LogDensityPoint pFilteredPnt, int pX, int pY) {
    RasterPoint point = getRasterPoint(pX, pY);
    double ls;
    if (point.count < precalcLogArray.length) {
      ls = precalcLogArray[point.count] / FILTER_WHITE;
    }
    else {
      ls = (k1 * Math.log10(1.0 + flame.getWhiteLevel() * point.count * k2)) / (flame.getWhiteLevel() * point.count) / FILTER_WHITE;
    }
    pFilteredPnt.red = ls * point.red;
    pFilteredPnt.green = ls * point.green;
    pFilteredPnt.blue = ls * point.blue;
    pFilteredPnt.intensity = ls * point.count * flame.getWhiteLevel();
  }

  private RasterPoint emptyRasterPoint = new RasterPoint();

  private RasterPoint getRasterPoint(int pX, int pY) {
    if (pX < 0 || pX >= rasterWidth || pY < 0 || pY >= rasterHeight)
      return emptyRasterPoint;
    else
      return raster[pY][pX];
  }

}
