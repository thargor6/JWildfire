/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.log10;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.raster.AbstractRaster;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.swing.ChannelMixerCurves;

public class LogDensityFilter extends FilterHolder {
  private final ColorFunc colorFunc;

  private AbstractRaster raster;
  private int rasterWidth, rasterHeight, rasterSize;
  private final int PRECALC_LOG_ARRAY_SIZE = 512;
  private double precalcLogArray[];
  private double k1, k2;
  private double motionBlurScl;
  private final AbstractRandomGenerator randGen;
  private final boolean jitter;
  private final int colorOversampling;

  public LogDensityFilter(Flame pFlame, AbstractRandomGenerator pRandGen) {
    super(pFlame);
    colorFunc = pFlame.getChannelMixerMode().getColorFunc(pFlame, pRandGen);
    motionBlurScl = flame.getMotionBlurLength() <= 0 ? 1.0 : 1.0 / (flame.getMotionBlurLength() + 1.0);
    jitter = pFlame.isSampleJittering();
    colorOversampling = jitter ? pFlame.getColorOversampling() : 1;
    if (jitter) {
      randGen = new MarsagliaRandomGenerator();
      randGen.randomize(pFlame.hashCode());
    }
    else {
      randGen = null;
    }
  }

  public void setRaster(AbstractRaster pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight) {
    raster = pRaster;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    rasterSize = rasterWidth * rasterHeight;
    k1 = flame.getContrast() * 2.0 * flame.getBrightness() / (double) (oversample);
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double area = ((double) pImageWidth * (double) pImageHeight) / (pixelsPerUnit * pixelsPerUnit);
    k2 = 1.0 / (flame.getContrast() * area * flame.getSampleDensity());

    precalcLogArray = new double[PRECALC_LOG_ARRAY_SIZE + 1];
    for (int i = 1; i <= PRECALC_LOG_ARRAY_SIZE; i++) {
      double x = i * motionBlurScl;
      precalcLogArray[i] = (k1 * log10(1 + x * k2)) / (flame.getWhiteLevel() * x);
    }
  }

  public void transformPointSimple(LogDensityPoint pFilteredPnt, int pX, int pY) {
    pFilteredPnt.red = pFilteredPnt.green = pFilteredPnt.blue = 0;
    pFilteredPnt.intensity = 0;
    for (int px = 0; px < oversample; px++) {
      for (int py = 0; py < oversample; py++) {
        getSample(pFilteredPnt, pX * oversample + px, pY * oversample + py);
        double logScale;
        long pCount = pFilteredPnt.rp.count;
        if (pCount < precalcLogArray.length) {
          logScale = precalcLogArray[(int) pCount];
        }
        else {
          logScale = (k1 * log10(1.0 + pCount * motionBlurScl * k2)) / (flame.getWhiteLevel() * pCount * motionBlurScl);
        }
        if (pCount > 0) {
          if (colorFunc == ColorFunc.NULL) {
            pFilteredPnt.red += logScale * pFilteredPnt.rp.red;
            pFilteredPnt.green += logScale * pFilteredPnt.rp.green;
            pFilteredPnt.blue += logScale * pFilteredPnt.rp.blue;
          }
          else {
            final double scale = ChannelMixerCurves.FILTER_SCALE;
            double rawR = pFilteredPnt.rp.red * scale / pCount;
            double rawG = pFilteredPnt.rp.green * scale / pCount;
            double rawB = pFilteredPnt.rp.blue * scale / pCount;

            pFilteredPnt.red += logScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount / scale;
            pFilteredPnt.green += logScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount / scale;
            pFilteredPnt.blue += logScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount / scale;
          }
          pFilteredPnt.intensity += logScale * pCount * flame.getWhiteLevel();
        }
      }
    }
  }

  private void getSample(LogDensityPoint pFilteredPnt, int pX, int pY) {
    if (jitter) {
      final double epsilon = 0.0001;
      final double radius = 0.25;
      double dr = log(randGen.random() + 0.1) + 1;
      if (dr < epsilon) {
        dr = epsilon;
      }
      else if (dr > 1.0 - epsilon) {
        dr = 1.0 - epsilon;
      }
      double da = epsilon + (randGen.random() - 2 * epsilon) * M_PI * 2.0;
      double x = dr * cos(da) * radius;
      int xi = x < 0 ? -1 : 1;
      x = MathLib.fabs(x);
      double y = dr * sin(da) * radius;
      int yi = y < 0 ? -1 : 1;
      y = MathLib.fabs(y);

      raster.readRasterPointSafe(pX, pY, pFilteredPnt.lu);
      raster.readRasterPointSafe(pX + xi, pY, pFilteredPnt.ru);
      raster.readRasterPointSafe(pX, pY + yi, pFilteredPnt.lb);
      raster.readRasterPointSafe(pX + xi, pY + yi, pFilteredPnt.rb);
      pFilteredPnt.rp.red = Tools.blerp(pFilteredPnt.lu.red, pFilteredPnt.ru.red, pFilteredPnt.lb.red, pFilteredPnt.rb.red, x, y);
      pFilteredPnt.rp.green = Tools.blerp(pFilteredPnt.lu.green, pFilteredPnt.ru.green, pFilteredPnt.lb.green, pFilteredPnt.rb.green, x, y);
      pFilteredPnt.rp.blue = Tools.blerp(pFilteredPnt.lu.blue, pFilteredPnt.ru.blue, pFilteredPnt.lb.blue, pFilteredPnt.rb.blue, x, y);
      pFilteredPnt.rp.count = Math.round(Tools.blerp(pFilteredPnt.lu.count, pFilteredPnt.ru.count, pFilteredPnt.lb.count, pFilteredPnt.rb.count, x, y));
      //      System.out.println(pFilteredPnt.rp.red + " (" + pFilteredPnt.lu.red + " " + pFilteredPnt.ru.red + " " + pFilteredPnt.lb.red + "," + pFilteredPnt.rb.red + ") at (" + x + " " + y + ")");
    }
    else {
      raster.readRasterPointSafe(pX, pY, pFilteredPnt.rp);
    }
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
    pFilteredPnt.clear();
    if (noiseFilterSize > 1) {
      if (colorFunc == ColorFunc.NULL) {
        for (int c = 0; c < colorOversampling; c++) {
          for (int i = 0; i < noiseFilterSize; i++) {
            for (int j = 0; j < noiseFilterSize; j++) {
              getSample(pFilteredPnt, pX * oversample + j, pY * oversample + i);
              long count = pFilteredPnt.rp.count;
              int pIdx = (int) count;
              if (pIdx > 0) {
                double logScale;
                if (pIdx < precalcLogArray.length) {
                  logScale = precalcLogArray[pIdx];
                }
                else {
                  logScale = (k1 * log10(1.0 + count * motionBlurScl * k2)) / (flame.getWhiteLevel() * count * motionBlurScl);
                }
                pFilteredPnt.red += filter[i][j] * logScale * pFilteredPnt.rp.red / (double) colorOversampling;
                pFilteredPnt.green += filter[i][j] * logScale * pFilteredPnt.rp.green / (double) colorOversampling;
                pFilteredPnt.blue += filter[i][j] * logScale * pFilteredPnt.rp.blue / (double) colorOversampling;
                pFilteredPnt.intensity += filter[i][j] * logScale * count * flame.getWhiteLevel() / (double) colorOversampling;
              }
            }
          }
        }
      }
      else {
        for (int c = 0; c < colorOversampling; c++) {
          for (int i = 0; i < noiseFilterSize; i++) {
            for (int j = 0; j < noiseFilterSize; j++) {
              getSample(pFilteredPnt, pX * oversample + j, pY * oversample + i);
              long count = pFilteredPnt.rp.count;
              int pIdx = (int) count;
              if (pIdx > 0) {
                double logScale;
                if (pIdx < precalcLogArray.length) {
                  logScale = precalcLogArray[pIdx];
                }
                else {
                  logScale = (k1 * log10(1.0 + count * motionBlurScl * k2)) / (flame.getWhiteLevel() * count * motionBlurScl);
                }
                final double scale = ChannelMixerCurves.FILTER_SCALE;
                double rawR = pFilteredPnt.rp.red * scale / (double) count;
                double rawG = pFilteredPnt.rp.green * scale / (double) count;
                double rawB = pFilteredPnt.rp.blue * scale / (double) count;
                double transR = colorFunc.mapRGBToR(rawR, rawG, rawB) * count / scale;
                double transG = colorFunc.mapRGBToG(rawR, rawG, rawB) * count / scale;
                double transB = colorFunc.mapRGBToB(rawR, rawG, rawB) * count / scale;
                pFilteredPnt.red += filter[i][j] * logScale * transR / (double) colorOversampling;
                pFilteredPnt.green += filter[i][j] * logScale * transG / (double) colorOversampling;
                pFilteredPnt.blue += filter[i][j] * logScale * transB / (double) colorOversampling;
                pFilteredPnt.intensity += filter[i][j] * logScale * count * flame.getWhiteLevel() / (double) colorOversampling;
              }
            }
          }
        }
      }
    }
    else {
      for (int c = 0; c < colorOversampling; c++) {
        for (int px = 0; px < oversample; px++) {
          for (int py = 0; py < oversample; py++) {
            getSample(pFilteredPnt, pX * oversample + px, pY * oversample + py);
            double logScale;
            long pCount = pFilteredPnt.rp.count;
            if (pCount > 0) {
              if (pCount < precalcLogArray.length) {
                logScale = precalcLogArray[(int) pCount];
              }
              else {
                logScale = (k1 * log10(1.0 + pCount * motionBlurScl * k2)) / (flame.getWhiteLevel() * pCount * motionBlurScl);
              }
              if (colorFunc == ColorFunc.NULL) {
                pFilteredPnt.red += logScale * pFilteredPnt.rp.red / (double) colorOversampling;
                pFilteredPnt.green += logScale * pFilteredPnt.rp.green / (double) colorOversampling;
                pFilteredPnt.blue += logScale * pFilteredPnt.rp.blue / (double) colorOversampling;
              }
              else {
                final double scale = ChannelMixerCurves.FILTER_SCALE;
                double rawR = pFilteredPnt.rp.red * scale / pCount;
                double rawG = pFilteredPnt.rp.green * scale / pCount;
                double rawB = pFilteredPnt.rp.blue * scale / pCount;

                pFilteredPnt.red += logScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount / scale / (double) colorOversampling;
                pFilteredPnt.green += logScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount / scale / (double) colorOversampling;
                pFilteredPnt.blue += logScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount / scale / (double) colorOversampling;
              }
              pFilteredPnt.intensity += logScale * pFilteredPnt.rp.count * flame.getWhiteLevel() / (double) colorOversampling;
            }
          }
        }
      }
    }
    pFilteredPnt.clip();
  }

}
