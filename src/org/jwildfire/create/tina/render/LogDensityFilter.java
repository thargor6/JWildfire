/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.render.filter.FilterKernel;
import org.jwildfire.create.tina.swing.ChannelMixerCurves;

public class LogDensityFilter {
  private final Flame flame;
  private final ColorFunc colorFunc;

  private AbstractRasterPoint[][] raster;
  private int rasterWidth, rasterHeight, rasterSize;
  private final int PRECALC_LOG_ARRAY_SIZE = 512;
  private double filter[][];
  private int noiseFilterSize;
  private double precalcLogArray[];
  private double k1, k2;
  private final FilterKernel filterKernel;
  private double motionBlurScl;

  public LogDensityFilter(Flame pFlame, AbstractRandomGenerator pRandGen) {
    flame = pFlame;
    colorFunc = pFlame.getChannelMixerMode().getColorFunc(pFlame, pRandGen);
    filterKernel = pFlame.getSpatialFilterKernel().createFilterInstance();
    noiseFilterSize = filterKernel.getFilterSize(pFlame.getSpatialFilterRadius());
    filter = new double[noiseFilterSize][noiseFilterSize];
    initFilter(pFlame.getSpatialFilterRadius(), noiseFilterSize, filter);
    motionBlurScl = flame.getMotionBlurLength() <= 0 ? 1.0 : 1.0 / (flame.getMotionBlurLength() + 1.0);
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
    k1 = flame.getContrast() * 2.0 * flame.getBrightness();
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double area = ((double) pImageWidth * (double) pImageHeight) / (pixelsPerUnit * pixelsPerUnit);
    k2 = 1.0 / (flame.getContrast() * area * flame.getSampleDensity());

    precalcLogArray = new double[PRECALC_LOG_ARRAY_SIZE + 1];
    for (int i = 1; i <= PRECALC_LOG_ARRAY_SIZE; i++) {
      double x = i * motionBlurScl;
      precalcLogArray[i] = (k1 * log10(1 + x * k2)) / (flame.getWhiteLevel() * x);
    }
  }

  public void transformPointHDR(LogDensityPoint pFilteredPnt, int pX, int pY) {
    transformPointSimple(pFilteredPnt, pX, pY);
  }

  public void transformPointSimple(LogDensityPoint pFilteredPnt, int pX, int pY) {
    AbstractRasterPoint point = getRasterPoint(pX, pY);
    double logScale;
    long pCount = point.getCount();
    if (pCount < precalcLogArray.length) {
      logScale = precalcLogArray[(int) pCount];
    }
    else {
      logScale = (k1 * log10(1.0 + pCount * motionBlurScl * k2)) / (flame.getWhiteLevel() * pCount * motionBlurScl);
    }
    if (pCount > 0) {
      if (colorFunc == ColorFunc.NULL) {
        pFilteredPnt.red = logScale * point.getRed();
        pFilteredPnt.green = logScale * point.getGreen();
        pFilteredPnt.blue = logScale * point.getBlue();
      }
      else {
        final double scale = ChannelMixerCurves.FILTER_SCALE;
        double rawR = point.getRed() * scale / pCount;
        double rawG = point.getGreen() * scale / pCount;
        double rawB = point.getBlue() * scale / pCount;

        pFilteredPnt.red = logScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount / scale;
        pFilteredPnt.green = logScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount / scale;
        pFilteredPnt.blue = logScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount / scale;
      }
      pFilteredPnt.intensity = logScale * point.getCount() * flame.getWhiteLevel();
    }
    else {
      pFilteredPnt.red = pFilteredPnt.green = pFilteredPnt.blue = 0;
      pFilteredPnt.intensity = 0;
    }
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
      if (colorFunc == ColorFunc.NULL) {
        pFilteredPnt.clear();
        for (int i = 0; i < noiseFilterSize; i++) {
          for (int j = 0; j < noiseFilterSize; j++) {
            AbstractRasterPoint point = getRasterPoint(pX + j, pY + i);
            int pIdx = (int) point.getCount();
            if (pIdx > 0) {
              double logScale;
              if (pIdx < precalcLogArray.length) {
                logScale = precalcLogArray[pIdx];
              }
              else {
                logScale = (k1 * log10(1.0 + point.getCount() * motionBlurScl * k2)) / (flame.getWhiteLevel() * point.getCount() * motionBlurScl);
              }
              pFilteredPnt.red += filter[i][j] * logScale * point.getRed();
              pFilteredPnt.green += filter[i][j] * logScale * point.getGreen();
              pFilteredPnt.blue += filter[i][j] * logScale * point.getBlue();
              pFilteredPnt.intensity += filter[i][j] * logScale * point.getCount() * flame.getWhiteLevel();
            }
          }
        }
      }
      else {
        pFilteredPnt.clear();
        double rawR = 0.0, rawG = 0.0, rawB = 0.0;
        double pCount = 0.0, avgLogScale = 0.0;
        for (int i = 0; i < noiseFilterSize; i++) {
          for (int j = 0; j < noiseFilterSize; j++) {
            AbstractRasterPoint point = getRasterPoint(pX + j, pY + i);
            int pIdx = (int) point.getCount();
            if (pIdx > 0) {
              double logScale;
              if (pIdx < precalcLogArray.length) {
                logScale = precalcLogArray[pIdx];
              }
              else {
                logScale = (k1 * log10(1.0 + point.getCount() * motionBlurScl * k2)) / (flame.getWhiteLevel() * point.getCount() * motionBlurScl);
              }
              rawR += filter[i][j] * point.getRed();
              rawG += filter[i][j] * point.getGreen();
              rawB += filter[i][j] * point.getBlue();
              double lPCount = filter[i][j] * point.getCount();
              avgLogScale += filter[i][j] * logScale;
              pCount += lPCount;
              pFilteredPnt.intensity += logScale * lPCount * flame.getWhiteLevel();
            }
          }
        }
        final double scale = ChannelMixerCurves.FILTER_SCALE;
        rawR = rawR * scale / pCount;
        rawG = rawG * scale / pCount;
        rawB = rawB * scale / pCount;
        pFilteredPnt.red = avgLogScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount / scale;
        pFilteredPnt.green = avgLogScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount / scale;
        pFilteredPnt.blue = avgLogScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount / scale;
      }
    }
    else {
      AbstractRasterPoint point = getRasterPoint(pX, pY);
      double logScale;
      long pCount = point.getCount();
      if (pCount < precalcLogArray.length) {
        logScale = precalcLogArray[(int) pCount];
      }
      else {
        logScale = (k1 * log10(1.0 + pCount * motionBlurScl * k2)) / (flame.getWhiteLevel() * pCount * motionBlurScl);
      }
      if (pCount > 0) {
        if (colorFunc == ColorFunc.NULL) {
          pFilteredPnt.red = logScale * point.getRed();
          pFilteredPnt.green = logScale * point.getGreen();
          pFilteredPnt.blue = logScale * point.getBlue();
        }
        else {
          final double scale = ChannelMixerCurves.FILTER_SCALE;
          double rawR = point.getRed() * scale / pCount;
          double rawG = point.getGreen() * scale / pCount;
          double rawB = point.getBlue() * scale / pCount;

          pFilteredPnt.red = logScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount / scale;
          pFilteredPnt.green = logScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount / scale;
          pFilteredPnt.blue = logScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount / scale;
        }
        pFilteredPnt.intensity = logScale * point.getCount() * flame.getWhiteLevel();
      }
      else {
        pFilteredPnt.red = pFilteredPnt.green = pFilteredPnt.blue = 0;
        pFilteredPnt.intensity = 0;
      }
    }
  }

}
