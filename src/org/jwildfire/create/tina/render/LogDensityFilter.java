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

import static org.jwildfire.base.mathlib.MathLib.log10;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.base.raster.RasterPoint;
import org.jwildfire.create.tina.render.filter.FilterKernel;

public class LogDensityFilter {
  private final Flame flame;
  private AbstractRasterPoint[][] raster;
  private int rasterWidth, rasterHeight, rasterSize;
  public static final int FILTER_WHITE = (1 << 26);
  public final static double BRIGHTNESS_SCALE = 2.0 * 268.0;
  private final int PRECALC_LOG_ARRAY_SIZE = 256;
  private double filter[][];
  private int noiseFilterSize;
  private double precalcLogArray[]; // precalculated log-values
  private double k1, k2;
  private final FilterKernel filterKernel;

  public LogDensityFilter(Flame pFlame) {
    flame = pFlame;
    filterKernel = pFlame.getSpatialFilterKernel().createFilterInstance();
    noiseFilterSize = filterKernel.getFilterSize(pFlame.getSpatialFilterRadius());
    filter = new double[noiseFilterSize][noiseFilterSize];
    initFilter(pFlame.getSpatialFilterRadius(), noiseFilterSize, filter);
  }

  private void initFilter(double pFilterRadius, int pFilterSize, double[][] pFilter) {
    double adjust = filterKernel.getFilterAdjust(pFilterRadius);
    for (int i = 0; i < pFilterSize; i++) {
      for (int j = 0; j < pFilterSize; j++) {
        double ii = ((2.0 * i + 1.0) / pFilterSize - 1.0) * adjust;
        double jj = ((2.0 * j + 1.0) / pFilterSize - 1.0) * adjust;
        pFilter[i][j] = filterKernel.getFilterCoeff(ii) * filterKernel.getFilterCoeff(jj);
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

  public void setRaster(AbstractRasterPoint[][] pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight) {
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
          AbstractRasterPoint point = getRasterPoint(pX + j, pY + i);
          pFilteredPnt.red += filter[i][j] * point.getRed();
          pFilteredPnt.green += filter[i][j] * point.getGreen();
          pFilteredPnt.blue += filter[i][j] * point.getBlue();
          pFilteredPnt.intensity += filter[i][j] * point.getCount();
        }
      }
      pFilteredPnt.red /= FILTER_WHITE;
      pFilteredPnt.green /= FILTER_WHITE;
      pFilteredPnt.blue /= FILTER_WHITE;
      pFilteredPnt.intensity = flame.getWhiteLevel() * pFilteredPnt.intensity / FILTER_WHITE;
    }
    else {
      AbstractRasterPoint point = getRasterPoint(pX, pY);
      pFilteredPnt.red = point.getRed();
      pFilteredPnt.green = point.getGreen();
      pFilteredPnt.blue = point.getBlue();
      pFilteredPnt.intensity = point.getCount() * flame.getWhiteLevel();
    }
  }

  public void transformPointSimple(LogDensityPoint pFilteredPnt, int pX, int pY) {
    AbstractRasterPoint point = getRasterPoint(pX, pY);
    double logScale;
    if (point.getCount() < precalcLogArray.length) {
      logScale = precalcLogArray[(int) point.getCount()] / FILTER_WHITE;
    }
    else {
      logScale = (k1 * log10(1.0 + flame.getWhiteLevel() * point.getCount() * k2)) / (flame.getWhiteLevel() * point.getCount()) / FILTER_WHITE;
    }
    pFilteredPnt.red = logScale * point.getRed();
    pFilteredPnt.green = logScale * point.getGreen();
    pFilteredPnt.blue = logScale * point.getBlue();
    pFilteredPnt.intensity = logScale * point.getCount() * flame.getWhiteLevel();
  }

  private AbstractRasterPoint emptyRasterPoint = new RasterPoint();

  private AbstractRasterPoint getRasterPoint(int pX, int pY) {
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
          AbstractRasterPoint point = getRasterPoint(pX + j, pY + i);
          double logScale;
          int pIdx = (int) point.getCount();
          if (pIdx < precalcLogArray.length) {
            logScale = precalcLogArray[pIdx];
          }
          else {
            logScale = (k1 * log10(1.0 + flame.getWhiteLevel() * point.getCount() * k2)) / (flame.getWhiteLevel() * point.getCount());
          }
          pFilteredPnt.red += filter[i][j] * logScale * point.getRed();
          pFilteredPnt.green += filter[i][j] * logScale * point.getGreen();
          pFilteredPnt.blue += filter[i][j] * logScale * point.getBlue();
          pFilteredPnt.intensity += filter[i][j] * logScale * point.getCount();
        }
      }

      pFilteredPnt.red /= FILTER_WHITE;
      pFilteredPnt.green /= FILTER_WHITE;
      pFilteredPnt.blue /= FILTER_WHITE;
      pFilteredPnt.intensity = flame.getWhiteLevel() * pFilteredPnt.intensity / FILTER_WHITE;
    }
    else {
      AbstractRasterPoint point = getRasterPoint(pX, pY);
      double logScale;
      if (point.getCount() < precalcLogArray.length) {
        logScale = precalcLogArray[(int) point.getCount()] / FILTER_WHITE;
      }
      else {
        logScale = (k1 * log10(1.0 + flame.getWhiteLevel() * point.getCount() * k2)) / (flame.getWhiteLevel() * point.getCount()) / FILTER_WHITE;
      }
      pFilteredPnt.red = logScale * point.getRed();
      pFilteredPnt.green = logScale * point.getGreen();
      pFilteredPnt.blue = logScale * point.getBlue();
      pFilteredPnt.intensity = logScale * point.getCount() * flame.getWhiteLevel();
    }
  }

  public void transformPointPastDE(LogDensityPoint pFilteredPnt, int pX, int pY) {
    if (noiseFilterSize > 1) {
      pFilteredPnt.clear();
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          AbstractRasterPoint point = getRasterPoint(pX + j, pY + i);
          pFilteredPnt.red += filter[i][j] * point.getRed();
          pFilteredPnt.green += filter[i][j] * point.getGreen();
          pFilteredPnt.blue += filter[i][j] * point.getBlue();
          pFilteredPnt.intensity += filter[i][j] * point.getCount();
        }
      }
      pFilteredPnt.red /= FILTER_WHITE;
      pFilteredPnt.green /= FILTER_WHITE;
      pFilteredPnt.blue /= FILTER_WHITE;
      pFilteredPnt.intensity = flame.getWhiteLevel() * pFilteredPnt.intensity / FILTER_WHITE;
    }
    else {
      AbstractRasterPoint point = getRasterPoint(pX, pY);
      pFilteredPnt.red = point.getRed() / (double) FILTER_WHITE;
      pFilteredPnt.green = point.getGreen() / (double) FILTER_WHITE;
      pFilteredPnt.blue = point.getBlue() / (double) FILTER_WHITE;
      pFilteredPnt.intensity = point.getCount() * flame.getWhiteLevel() / (double) FILTER_WHITE;

    }
  }
}
