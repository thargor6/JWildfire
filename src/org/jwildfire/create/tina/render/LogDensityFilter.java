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
import static org.jwildfire.base.MathLib.fabs;
import static org.jwildfire.base.MathLib.log;
import static org.jwildfire.base.MathLib.log10;
import static org.jwildfire.base.MathLib.sqrt;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;

public class LogDensityFilter {
  private final Flame flame;
  private RasterPoint[][] raster;
  private int rasterWidth, rasterHeight, rasterSize;
  private final static double FILTER_CUTOFF = 1.8;
  private final int FILTER_WHITE = (1 << 26);
  private final double BRIGHTNESS_SCALE = 2.3 * 268.0;
  private final int PRECALC_LOG_ARRAY_SIZE = 6000;
  private final double MAX_FILTER_RADIUS = 7.0;
  private double filter[][];
  private Map<Integer, double[][]> filterArray = new HashMap<Integer, double[][]>();
  private int noiseFilterSize;
  private double precalcLogArray[]; // precalculated log-values
  private double k1, k2;

  public LogDensityFilter(Flame pFlame) {
    flame = pFlame;
    initFilter(pFlame);
  }

  private double[][] getFilter(double pFilterRadius) {
    if (pFilterRadius < 0.0)
      pFilterRadius = 0.0;
    else if (pFilterRadius > MAX_FILTER_RADIUS)
      pFilterRadius = MAX_FILTER_RADIUS;
    int filterSize = Integer.valueOf(calcFilterSize(pFilterRadius));
    double res[][] = filterArray.get(filterSize);

    if (res == null) {
      res = new double[filterSize][filterSize];
      initFilter(pFilterRadius, filterSize, res);
      filterArray.put(filterSize, res);
    }
    return res;
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

  public void transformPointHDRWithDEFilter(LogDensityPoint pFilteredPnt, int pX, int pY, double pMaxDensity) {
    double density;
    int densityRect = flame.getDEFilterRadius();
    int dr2 = densityRect / 2;
    double curve = flame.getDEFilterAmount();

    if (pX < dr2 || pY < dr2 || (pX >= rasterWidth - dr2) || (pY >= rasterHeight - dr2)) {
      density = getRasterPoint(pX, pY).count;
    }
    else {
      // 0.07  0.11  0.07
      // 0.11  0.28  0.11
      // 0.07  0.11  0.07
      /*
       density=0.28f*getRasterPoint(pX, pY)->count +
       0.11f*(getRasterPoint(pX-1, pY)->count + getRasterPoint(pX+1, pY)->count + getRasterPoint(pX, pY-1)->count + getRasterPoint(pX, pY+1)->count) +
       0.07f*(getRasterPoint(pX-1, pY-1)->count + getRasterPoint(pX-1, pY+1)->count + getRasterPoint(pX+1, pY-1)->count + getRasterPoint(pX+1, pY+1)->count);
       */
      // 0.01  0.015 0.035 0.015 0.01
      // 0.015 0.05  0.08  0.05  0.015
      // 0.035 0.08  0.18  0.08  0.035
      // 0.015 0.05  0.08  0.05  0.015
      // 0.01  0.015 0.035 0.015 0.01
      /*
       density=0.18f*getRasterPoint(pX, pY)->count +
       0.08f*(getRasterPoint(pX-1, pY)->count + getRasterPoint(pX+1, pY)->count + getRasterPoint(pX, pY-1)->count + getRasterPoint(pX, pY+1)->count) +
       0.05f*(getRasterPoint(pX-1, pY-1)->count + getRasterPoint(pX-1, pY+1)->count + getRasterPoint(pX+1, pY-1)->count + getRasterPoint(pX+1, pY+1)->count) +
       0.035f*(getRasterPoint(pX-2, pY)->count + getRasterPoint(pX+2, pY)->count + getRasterPoint(pX, pY-2)->count + getRasterPoint(pX, pY+2)->count) +
       0.015f*(getRasterPoint(pX-2, pY-1)->count + getRasterPoint(pX-1, pY-2)->count + getRasterPoint(pX+1, pY-2)->count + getRasterPoint(pX+2, pY-1)->count +
       getRasterPoint(pX-2, pY+1)->count + getRasterPoint(pX-1, pY+2)->count + getRasterPoint(pX+1, pY+2)->count + getRasterPoint(pX+2, pY+1)->count) +
       0.01f*(getRasterPoint(pX-2, pY-2)->count + getRasterPoint(pX+2, pY-2)->count + getRasterPoint(pX+2, pY+2)->count + getRasterPoint(pX-2, pY+2)->count);
       */

      density = 0.0;
      double kernelSum = 0.0;
      for (int y = 0; y < densityRect; y++) {
        for (int x = 0; x < densityRect; x++) {
          double intensity = 1.0 / (1.0 + (y - dr2) * (y - dr2) + (x - dr2) * (x - dr2));
          kernelSum += intensity;

          //printf("(%d %d) %f\n",x,y,intensity);

          density += intensity * getRasterPoint(pX + x - dr2, pY + y - dr2).count;
        }
      }
      density /= kernelSum;
    }

    double oDensity = density;

    density = log(density + 1);
    density /= log(pMaxDensity + 1);

    double minRadius = 0.05;
    double maxRadius = sqrt(rasterWidth * rasterWidth + rasterHeight * rasterHeight) * 0.5;
    if (maxRadius > 50.0) {
      maxRadius = 50.0;
    }
    double radius;
    radius = minRadius + (maxRadius - minRadius) * erf(1.0 / (1.0 + curve * density * density * density));
    if (radius < minRadius) {
      radius = minRadius;
    }
    else if (radius > maxRadius) {
      radius = maxRadius;
    }

    if (pX < dr2 || pY < dr2 || (pX >= rasterWidth - dr2) || (pY >= rasterHeight - dr2)) {

    }
    else {
      //printf("density= %f, avg=%f, max=%f, r=%d\n", density, JWF_LOG(1.0f+avgDensity), JWF_LOG(1.0f+maxDensity), calcFilterSize(radius));
    }

    radius = fabs(erf(oDensity - getRasterPoint(pX, pY).count) * curve);
    if (radius > 50.0)
      radius = 50.0;
    //  printf("density= %f, rad=%f, deltaDens= %f, final rad=%d\n", oDensity, radius, (oDensity-getRasterPoint(pX, pY)->count), calcFilterSize(radius));

    double noiseFilter[][] = getFilter(radius);
    int filterSize = noiseFilter.length;

    if (filterSize > 1) {
      pFilteredPnt.clear();
      for (int i = 0; i < filterSize; i++) {
        for (int j = 0; j < filterSize; j++) {
          RasterPoint point = getRasterPoint(pX + j, pY + i);
          pFilteredPnt.red += noiseFilter[i][j] * point.red;
          pFilteredPnt.green += noiseFilter[i][j] * point.green;
          pFilteredPnt.blue += noiseFilter[i][j] * point.blue;
          pFilteredPnt.intensity += noiseFilter[i][j] * point.count;
        }
      }

      pFilteredPnt.red /= FILTER_WHITE;
      pFilteredPnt.green /= FILTER_WHITE;
      pFilteredPnt.blue /= FILTER_WHITE;
      pFilteredPnt.intensity = flame.getWhiteLevel() * pFilteredPnt.intensity / FILTER_WHITE;
    }
    else {
      RasterPoint point = getRasterPoint(pX, pY);
      pFilteredPnt.red = point.red / FILTER_WHITE;
      pFilteredPnt.green = point.green / FILTER_WHITE;
      pFilteredPnt.blue = point.blue / FILTER_WHITE;
      pFilteredPnt.intensity = (double) (point.count * flame.getWhiteLevel() / FILTER_WHITE);
    }
  }

  public void transformPointWithDEFilter(LogDensityPoint pFilteredPnt, int pX, int pY, double pMaxDensity) {
    double density;
    int densityRect = flame.getDEFilterRadius();
    int dr2 = densityRect / 2;
    double curve = flame.getDEFilterAmount();
    boolean saveDens = false;

    if (pX < dr2 || pY < dr2 || (pX >= rasterWidth - dr2) || (pY >= rasterHeight - dr2)) {
      density = getRasterPoint(pX, pY).count;
      //    printf("%f ", density);
    }
    else {
      // 0.07  0.11  0.07
      // 0.11  0.28  0.11
      // 0.07  0.11  0.07
      /*
       density=0.28f*getRasterPoint(pX, pY)->count +
       0.11f*(getRasterPoint(pX-1, pY)->count + getRasterPoint(pX+1, pY)->count + getRasterPoint(pX, pY-1)->count + getRasterPoint(pX, pY+1)->count) +
       0.07f*(getRasterPoint(pX-1, pY-1)->count + getRasterPoint(pX-1, pY+1)->count + getRasterPoint(pX+1, pY-1)->count + getRasterPoint(pX+1, pY+1)->count);
       */
      // 0.01  0.015 0.035 0.015 0.01
      // 0.015 0.05  0.08  0.05  0.015
      // 0.035 0.08  0.18  0.08  0.035
      // 0.015 0.05  0.08  0.05  0.015
      // 0.01  0.015 0.035 0.015 0.01
      /*
       density=0.18f*getRasterPoint(pX, pY)->count +
       0.08f*(getRasterPoint(pX-1, pY)->count + getRasterPoint(pX+1, pY)->count + getRasterPoint(pX, pY-1)->count + getRasterPoint(pX, pY+1)->count) +
       0.05f*(getRasterPoint(pX-1, pY-1)->count + getRasterPoint(pX-1, pY+1)->count + getRasterPoint(pX+1, pY-1)->count + getRasterPoint(pX+1, pY+1)->count) +
       0.035f*(getRasterPoint(pX-2, pY)->count + getRasterPoint(pX+2, pY)->count + getRasterPoint(pX, pY-2)->count + getRasterPoint(pX, pY+2)->count) +
       0.015f*(getRasterPoint(pX-2, pY-1)->count + getRasterPoint(pX-1, pY-2)->count + getRasterPoint(pX+1, pY-2)->count + getRasterPoint(pX+2, pY-1)->count +
       getRasterPoint(pX-2, pY+1)->count + getRasterPoint(pX-1, pY+2)->count + getRasterPoint(pX+1, pY+2)->count + getRasterPoint(pX+2, pY+1)->count) +
       0.01f*(getRasterPoint(pX-2, pY-2)->count + getRasterPoint(pX+2, pY-2)->count + getRasterPoint(pX+2, pY+2)->count + getRasterPoint(pX-2, pY+2)->count);
       */

      density = 0.0;
      double kernelSum = 0.0;
      for (int y = 0; y < densityRect; y++) {
        for (int x = 0; x < densityRect; x++) {
          double intensity = 1.0 / (1.0 + (y - dr2) * (y - dr2) + (x - dr2) * (x - dr2));
          kernelSum += intensity;

          //printf("(%d %d) %f\n",x,y,intensity);

          density += intensity * getRasterPoint(pX + x - dr2, pY + y - dr2).count;
        }
      }
      density /= kernelSum;
    }

    double oDensity = density;

    density = log(density + 1);
    density /= log(pMaxDensity + 1);

    double minRadius = 0.05;
    double maxRadius = sqrt(rasterWidth * rasterWidth + rasterHeight * rasterHeight) * 0.5;
    if (maxRadius > 50.0) {
      maxRadius = 50.0;
    }
    double radius;
    radius = minRadius + (maxRadius - minRadius) * erf(1.0 / (1.0 + curve * density * density * density));
    if (radius < minRadius) {
      radius = minRadius;
    }
    else if (radius > maxRadius) {
      radius = maxRadius;
    }

    if (pX < dr2 || pY < dr2 || (pX >= rasterWidth - dr2) || (pY >= rasterHeight - dr2)) {

    }
    else {
      //printf("density= %f, avg=%f, max=%f, r=%d\n", density, JWF_LOG(1.0f+avgDensity), JWF_LOG(1.0f+maxDensity), calcFilterSize(radius));
    }

    radius = fabs(erf(oDensity - getRasterPoint(pX, pY).count) * curve);
    if (radius > 50.0)
      radius = 50.0;

    if (saveDens) {
      pFilteredPnt.red = radius;
      pFilteredPnt.green = radius;
      pFilteredPnt.blue = radius;
      pFilteredPnt.intensity = radius;
      return;
    }

    double noiseFilter[][] = getFilter(radius);
    int filterSize = noiseFilter.length;

    if (filterSize > 1) {
      pFilteredPnt.clear();
      for (int i = 0; i < filterSize; i++) {
        for (int j = 0; j < filterSize; j++) {
          RasterPoint point = getRasterPoint(pX + j - filterSize / 2, pY + i - filterSize / 2);
          double logScale;
          if (point.count < PRECALC_LOG_ARRAY_SIZE) {
            logScale = precalcLogArray[(int) point.count];
          }
          else {
            logScale = (k1 * log10(1.0 + flame.getWhiteLevel() * point.count * k2)) / (flame.getWhiteLevel() * point.count);
          }
          pFilteredPnt.red += noiseFilter[i][j] * logScale * point.red;
          pFilteredPnt.green += noiseFilter[i][j] * logScale * point.green;
          pFilteredPnt.blue += noiseFilter[i][j] * logScale * point.blue;
          pFilteredPnt.intensity += noiseFilter[i][j] * logScale * point.count;
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
      if (point.count < PRECALC_LOG_ARRAY_SIZE) {
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

  // Quick erf code taken from http://introcs.cs.princeton.edu/java/21function/ErrorFunction.java.html
  // Copyright © 2000–2010, Robert Sedgewick and Kevin Wayne. 
  //fractional error in math formula less than 1.2 * 10 ^ -7.
  // although subject to catastrophic cancellation when z in very close to 0
  // from Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2
  public static double erf(double z) {
    double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

    // use Horner's method
    double ans = 1 - t * Math.exp(-z * z - 1.26551223 +
        t * (1.00002368 +
        t * (0.37409196 +
            t * (0.09678418 +
                t * (-0.18628806 +
                    t * (0.27886807 +
                        t * (-1.13520398 +
                            t * (1.48851587 +
                                t * (-0.82215223 +
                                    t * (0.17087277))))))))));
    if (z >= 0)
      return ans;
    else
      return -ans;
  }

  public double calcMaxDensity() {
    double maxDensity = 0.0;
    for (int y = 0; y < rasterHeight; y++) {
      for (int x = 0; x < rasterWidth; x++) {
        RasterPoint p = raster[y][x];
        if (p.count > maxDensity) {
          maxDensity = p.count;
        }
      }
    }
    return maxDensity;
  }

}
