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

import static org.jwildfire.base.MathLib.EPSILON;
import static org.jwildfire.base.MathLib.exp;
import static org.jwildfire.base.MathLib.log10;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;

public class LogDensityFilter {
  private final Flame flame;
  private RasterPoint[][] raster;
  private int rasterWidth, rasterHeight, rasterSize;
  private final static double FILTER_CUTOFF = 1.8;
  public static final int FILTER_WHITE = (1 << 26);
  public final static double BRIGHTNESS_SCALE = 2.3 * 268.0;
  private final int PRECALC_LOG_ARRAY_SIZE = 6000;
  private double filter[][];
  private int noiseFilterSize;
  private double precalcLogArray[]; // precalculated log-values
  private double k1, k2;

  public LogDensityFilter(Flame pFlame) {
    flame = pFlame;
    initFilter(pFlame);
  }

  private int calcFilterSize(double pFilterRadius) {
    if (pFilterRadius < EPSILON) {
      return 0;
    }
    else {
      int fw = (int) (2.0 * FILTER_CUTOFF * pFilterRadius);
      int filterSize = fw + 1;
      if (filterSize % 2 == 0)
        filterSize++;
      return filterSize;
    }
  }

  private double calcFilterAdjust(double pFilterRadius) {
    if (pFilterRadius < EPSILON) {
      return 0.0;
    }
    else {
      int fw = (int) (2.0 * FILTER_CUTOFF * pFilterRadius);
      int filterSize = fw + 1;
      if (filterSize % 2 == 0)
        filterSize++;
      if (fw > 0) {
        return (1.0 * FILTER_CUTOFF * filterSize) / fw;
      }
      else {
        return 1.0;
      }
    }
  }

  private void initFilter(Flame pFlame) {
    noiseFilterSize = calcFilterSize(pFlame.getSpatialFilterRadius());
    filter = new double[noiseFilterSize][noiseFilterSize];
    initFilter(pFlame.getSpatialFilterRadius(), noiseFilterSize, filter);
  }

  private void initFilter(double pFilterRadius, int pFilterSize, double[][] pFilter) {
    double adjust = calcFilterAdjust(pFilterRadius);
    for (int i = 0; i < pFilterSize; i++) {
      for (int j = 0; j < pFilterSize; j++) {
        double ii = ((2.0 * i + 1.0) / pFilterSize - 1.0) * adjust;
        double jj = ((2.0 * j + 1.0) / pFilterSize - 1.0) * adjust;
        pFilter[i][j] = exp(-2.0 * (ii * ii + jj * jj));
      }
    }
    // normalize
    {
      double t = 0.0;
      for (int i = 0; i < pFilterSize; i++) {
        for (int j = 0; j < pFilterSize; j++) {
          t += pFilter[i][j];
        }
      }
      for (int i = 0; i < pFilterSize; i++) {
        for (int j = 0; j < pFilterSize; j++) {
          pFilter[i][j] = pFilter[i][j] / t;
        }
      }
    }
  }

  public int getNoiseFilterSize() {
    return noiseFilterSize;
  }

  public void setRaster(RasterPoint[][] pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight) {
    raster = pRaster;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    rasterSize = rasterWidth * rasterHeight;
    k1 = (flame.getContrast() * BRIGHTNESS_SCALE * flame.getBrightness() * FILTER_WHITE) / 256.0;
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double area = ((double) pImageWidth * (double) pImageHeight) / (pixelsPerUnit * pixelsPerUnit);
    k2 = 1.0 / (flame.getContrast() * area * (double) flame.getWhiteLevel() * flame.getSampleDensity());

    precalcLogArray = new double[PRECALC_LOG_ARRAY_SIZE + 1];
    for (int i = 1; i <= PRECALC_LOG_ARRAY_SIZE; i++) {
      precalcLogArray[i] = (k1 * log10(1 + flame.getWhiteLevel() * i * k2)) / (flame.getWhiteLevel() * i);
    }
  }

  public void transformPointHDR(LogDensityPoint pFilteredPnt, int pX, int pY) {
    if (noiseFilterSize > 1) {
      pFilteredPnt.clear();
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          RasterPoint point = getRasterPoint(pX + j, pY + i);
          pFilteredPnt.red += filter[i][j] * point.red;
          pFilteredPnt.green += filter[i][j] * point.green;
          pFilteredPnt.blue += filter[i][j] * point.blue;
          pFilteredPnt.intensity += filter[i][j] * point.count;
        }
      }
      pFilteredPnt.red /= FILTER_WHITE;
      pFilteredPnt.green /= FILTER_WHITE;
      pFilteredPnt.blue /= FILTER_WHITE;
      pFilteredPnt.intensity = flame.getWhiteLevel() * pFilteredPnt.intensity / FILTER_WHITE;
    }
    else {
      RasterPoint point = getRasterPoint(pX, pY);
      pFilteredPnt.red = point.red;
      pFilteredPnt.green = point.green;
      pFilteredPnt.blue = point.blue;
      pFilteredPnt.intensity = point.count * flame.getWhiteLevel();
    }
  }

  public void transformPointSimple(LogDensityPoint pFilteredPnt, int pX, int pY) {
    RasterPoint point = getRasterPoint(pX, pY);
    double ls;
    if (point.count < precalcLogArray.length) {
      ls = precalcLogArray[(int) point.count] / FILTER_WHITE;
    }
    else {
      ls = (k1 * log10(1.0 + flame.getWhiteLevel() * point.count * k2)) / (flame.getWhiteLevel() * point.count) / FILTER_WHITE;
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

  public double calcDensity(long pSampleCount, long pRasterSize) {
    return (double) pSampleCount / (double) pRasterSize;
  }

  public double calcDensity(long pSampleCount) {
    if (rasterSize == 0) {
      throw new IllegalStateException();
    }
    return (double) pSampleCount / (double) rasterSize;
  }

  public void transformPoint(LogDensityPoint pFilteredPnt, int pX, int pY) {
    if (noiseFilterSize > 1) {
      pFilteredPnt.clear();
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          RasterPoint point = getRasterPoint(pX + j, pY + i);
          double logScale;
          if (point.count < precalcLogArray.length) {
            logScale = precalcLogArray[(int) point.count];
          }
          else {
            logScale = (k1 * log10(1.0 + flame.getWhiteLevel() * point.count * k2)) / (flame.getWhiteLevel() * point.count);
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
        ls = precalcLogArray[(int) point.count] / FILTER_WHITE;
      }
      else {
        ls = (k1 * log10(1.0 + flame.getWhiteLevel() * point.count * k2)) / (flame.getWhiteLevel() * point.count) / FILTER_WHITE;
      }
      pFilteredPnt.red = ls * point.red;
      pFilteredPnt.green = ls * point.green;
      pFilteredPnt.blue = ls * point.blue;
      pFilteredPnt.intensity = ls * point.count * flame.getWhiteLevel();
    }
  }

  public void transformPointPastDE(LogDensityPoint pFilteredPnt, int pX, int pY) {
    if (noiseFilterSize > 1) {
      pFilteredPnt.clear();
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          RasterPoint point = getRasterPoint(pX + j, pY + i);
          pFilteredPnt.red += filter[i][j] * point.red;
          pFilteredPnt.green += filter[i][j] * point.green;
          pFilteredPnt.blue += filter[i][j] * point.blue;
          pFilteredPnt.intensity += filter[i][j] * point.count;
        }
      }
      pFilteredPnt.red /= FILTER_WHITE;
      pFilteredPnt.green /= FILTER_WHITE;
      pFilteredPnt.blue /= FILTER_WHITE;
      pFilteredPnt.intensity = flame.getWhiteLevel() * pFilteredPnt.intensity / FILTER_WHITE;
    }
    else {
      RasterPoint point = getRasterPoint(pX, pY);
      pFilteredPnt.red = point.red / (double) FILTER_WHITE;
      pFilteredPnt.green = point.green / (double) FILTER_WHITE;
      pFilteredPnt.blue = point.blue / (double) FILTER_WHITE;
      pFilteredPnt.intensity = point.count * flame.getWhiteLevel() / (double) FILTER_WHITE;
    }
  }
}
