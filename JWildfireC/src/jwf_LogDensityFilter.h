/*
  JWildfireC - an external C-based fractal-flame-renderer for JWildfire 
  Copyright (C) 2012 Andreas Maschke

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
#ifndef __JWF_LOG_DENSITY_FILTER_H__
#define __JWF_LOG_DENSITY_FILTER_H__

struct LogDensityFilter {
  Flame *flame;
  RasterPoint **raster;
  int rasterWidth, rasterHeight, rasterSize;
  #define FILTER_CUTOFF  1.8f
  #define FILTER_WHITE  (1 << 26)
  #define BRIGHTNESS_SCALE  2.3f * 268.0f
  #define PRECALC_LOG_ARRAY_SIZE 6000
  float **filter;
  int noiseFilterSize;
  float *precalcLogArray; // precalculated log-values
  float k1, k2;
  RasterPoint *emptyRasterPoint;

  void create(Flame *pFlame) {
    raster = NULL;
    rasterWidth = rasterHeight = rasterSize = 0;
    filter = NULL;
    noiseFilterSize = 0;
    precalcLogArray = NULL;
    k1 = k2 = 0.0f;
    emptyRasterPoint = NULL;
    //
    flame = pFlame;
    initFilter(pFlame);
    hostMalloc((void**)&emptyRasterPoint,sizeof(RasterPoint));
    emptyRasterPoint->clear();
  }

  void free() {
    if(emptyRasterPoint!=NULL) {
      hostFree(emptyRasterPoint);
      emptyRasterPoint=NULL;
    }
    if(precalcLogArray!=NULL) {
      hostFree(precalcLogArray);
      precalcLogArray=NULL;
    }
    if(filter!=NULL) {
      for(int i=0;i<noiseFilterSize;i++) {
        hostFree(filter[i]);
      }
      hostFree(filter);
      filter=NULL;
    }
  }

  void initFilter(Flame *pFlame) {
    int fw = (int) (2.0f * FILTER_CUTOFF * pFlame->spatialFilterRadius);
    noiseFilterSize = fw + 1;
    if (noiseFilterSize % 2 == 0)
      noiseFilterSize++;
    float adjust;
    if (fw > 0) {
      adjust = (1.0f * FILTER_CUTOFF * noiseFilterSize) / fw;
    }
    else {
      adjust = 1.0f;
    }
    hostMalloc((void**)&filter, noiseFilterSize*sizeof(float*));
    for(int i=0;i<noiseFilterSize;i++) {
      hostMalloc((void**)&filter[i], noiseFilterSize*sizeof(float));
    }
    for (int i = 0; i < noiseFilterSize; i++) {
      for (int j = 0; j < noiseFilterSize; j++) {
        float ii = ((2.0f * i + 1.0f) / noiseFilterSize - 1.0f) * adjust;
        float jj = ((2.0f * j + 1.0f) / noiseFilterSize - 1.0f) * adjust;
        filter[i][j] = expf(-2.0f * (ii * ii + jj * jj));
      }
    }
    // normalize
    {
      float t = 0.0f;
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

  void setRaster(RasterPoint **pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight) {
    raster = pRaster;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    rasterSize = rasterWidth * rasterHeight;
    k1 = (flame->contrast * BRIGHTNESS_SCALE * flame->brightness * FILTER_WHITE) / 256.0f;
    float pixelsPerUnit = flame->pixelsPerUnit * flame->camZoom;
    float area = ((float) pImageWidth * (float) pImageHeight) / (pixelsPerUnit * pixelsPerUnit);
    k2 = 1.0f / (flame->contrast * area * (float) flame->whiteLevel * flame->sampleDensity);

    hostMalloc((void**)&precalcLogArray, (PRECALC_LOG_ARRAY_SIZE + 1)*sizeof(float));
    for (int i = 1; i <= PRECALC_LOG_ARRAY_SIZE; i++) {
      precalcLogArray[i] = (k1 * log10(1 + flame->whiteLevel * i * k2)) / (flame->whiteLevel * i);
    }
  }

  void transformPoint(LogDensityPoint *pFilteredPnt, int pX, int pY) {
    if (noiseFilterSize > 1) {
      pFilteredPnt->clear();
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          RasterPoint *point = getRasterPoint(pX + j, pY + i);
          float logScale;
          if (point->count < PRECALC_LOG_ARRAY_SIZE) {
            logScale = precalcLogArray[(int) point->count];
          }
          else {
            logScale = (k1 * log10(1.0f + flame->whiteLevel * point->count * k2)) / (flame->whiteLevel * point->count);
          }
          pFilteredPnt->red += filter[i][j] * logScale * point->red;
          pFilteredPnt->green += filter[i][j] * logScale * point->green;
          pFilteredPnt->blue += filter[i][j] * logScale * point->blue;
          pFilteredPnt->intensity += filter[i][j] * logScale * point->count;
        }
      }

      pFilteredPnt->red /= FILTER_WHITE;
      pFilteredPnt->green /= FILTER_WHITE;
      pFilteredPnt->blue /= FILTER_WHITE;
      pFilteredPnt->intensity = flame->whiteLevel * pFilteredPnt->intensity / FILTER_WHITE;
    }
    else {
      RasterPoint *point = getRasterPoint(pX, pY);
      float ls;
      if (point->count < PRECALC_LOG_ARRAY_SIZE) {
        ls = precalcLogArray[(int) point->count] / FILTER_WHITE;
      }
      else {
        ls = (k1 * log10(1.0f + flame->whiteLevel * point->count * k2)) / (flame->whiteLevel * point->count) / FILTER_WHITE;
      }
      pFilteredPnt->red = ls * point->red;
      pFilteredPnt->green = ls * point->green;
      pFilteredPnt->blue = ls * point->blue;
      pFilteredPnt->intensity = ls * point->count * flame->whiteLevel;
    }
  }

  void transformPointHDR(LogDensityPoint *pFilteredPnt, int pX, int pY) {
    if (noiseFilterSize > 1) {
      pFilteredPnt->clear();
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          RasterPoint *point = getRasterPoint(pX + j, pY + i);
          pFilteredPnt->red += filter[i][j] * point->red;
          pFilteredPnt->green += filter[i][j] * point->green;
          pFilteredPnt->blue += filter[i][j] * point->blue;
          pFilteredPnt->intensity += filter[i][j] * point->count;
        }
      }

      pFilteredPnt->red /= FILTER_WHITE;
      pFilteredPnt->green /= FILTER_WHITE;
      pFilteredPnt->blue /= FILTER_WHITE;
      pFilteredPnt->intensity = flame->whiteLevel * pFilteredPnt->intensity / FILTER_WHITE;
    }
    else {
      RasterPoint *point = getRasterPoint(pX, pY);
      pFilteredPnt->red = point->red;
      pFilteredPnt->green = point->green;
      pFilteredPnt->blue = point->blue;
      pFilteredPnt->intensity = (float)point->count * flame->whiteLevel;
    }
  }

  void transformPointSimple(LogDensityPoint *pFilteredPnt, int pX, int pY) {
    RasterPoint *point = getRasterPoint(pX, pY);
    float ls;
    if (point->count < PRECALC_LOG_ARRAY_SIZE) {
      ls = precalcLogArray[(int) point->count] / FILTER_WHITE;
    }
    else {
      ls = (k1 * log10f(1.0f + flame->whiteLevel * point->count * k2)) / (flame->whiteLevel * point->count) / FILTER_WHITE;
    }
    pFilteredPnt->red = ls * point->red;
    pFilteredPnt->green = ls * point->green;
    pFilteredPnt->blue = ls * point->blue;
    pFilteredPnt->intensity = ls * point->count * flame->whiteLevel;
  }

  RasterPoint *getRasterPoint(int pX, int pY) {
    if (pX < 0 || pX >= rasterWidth || pY < 0 || pY >= rasterHeight)
      return emptyRasterPoint;
    else
      return &raster[pY][pX];
  }

  float calcDensity(long pSampleCount, long pRasterSize) {
    return (float) pSampleCount / (float) pRasterSize;
  }

  float calcDensity(long pSampleCount) {
    if (rasterSize == 0) {
      return 0.0f;
    }
    return (float)((double) pSampleCount / (double) rasterSize);
  }
};

#endif // __JWF_LOG_DENSITY_FILTER_H__
