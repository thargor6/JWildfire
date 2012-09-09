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


  #define MAX_FILTER_RADIUS 7.0f
  float ***filterArray;

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

    int maxFilterSize=calcFilterSize(MAX_FILTER_RADIUS);
    filterArray=(float***)malloc((maxFilterSize+1)*sizeof(float**));
    memset(filterArray, 0, (maxFilterSize+1)*sizeof(float**));
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

  int calcFilterSize(float pFilterRadius) {
  	if(pFilterRadius<EPSILON) {
  		return 0;
  	}
  	else {
      int fw = (int) (2.0f * FILTER_CUTOFF * pFilterRadius);
      int filterSize = fw + 1;
      if (filterSize % 2 == 0)
      	filterSize++;
      return filterSize;
  	}
  }

  float calcFilterAdjust(float pFilterRadius) {
  	if(pFilterRadius<EPSILON) {
  		return 0.0f;
  	}
  	else {
      int fw = (int) (2.0f * FILTER_CUTOFF * pFilterRadius);
      int filterSize = fw + 1;
      if (filterSize % 2 == 0)
      	filterSize++;
      if (fw > 0) {
        return (1.0f * FILTER_CUTOFF * filterSize) / fw;
      }
      else {
        return 1.0f;
      }
  	}
  }

  void getFilter(float pFilterRadius, int *pFilterSize, float ***pFilter) {
  	if(pFilterRadius<0.0f)
  		pFilterRadius=0.0f;
  	else if(pFilterRadius>MAX_FILTER_RADIUS)
  		pFilterRadius=MAX_FILTER_RADIUS;
  	*pFilterSize=calcFilterSize(pFilterRadius);
  	if(filterArray[*pFilterSize]==NULL) {
  		initFilter(pFilterRadius, *pFilterSize, &(filterArray[*pFilterSize]));
  	}
  	*pFilter=filterArray[*pFilterSize];
  }

  void initFilter(float pFilterRadius, int pFilterSize, float ***pFilter) {
  	float adjust=calcFilterAdjust(pFilterRadius);
    hostMalloc((void**)pFilter, pFilterSize*sizeof(float*));
    for(int i=0;i<pFilterSize;i++) {
      hostMalloc((void**)&(*pFilter)[i], pFilterSize*sizeof(float));
    }
    for (int i = 0; i < pFilterSize; i++) {
      for (int j = 0; j < pFilterSize; j++) {
        float ii = ((2.0f * i + 1.0f) / pFilterSize - 1.0f) * adjust;
        float jj = ((2.0f * j + 1.0f) / pFilterSize - 1.0f) * adjust;
        (*pFilter)[i][j] = expf(-2.0f * (ii * ii + jj * jj));
      }
    }
    // normalize
    {
      float t = 0.0f;
      for (int i = 0; i < pFilterSize; i++) {
        for (int j = 0; j < pFilterSize; j++) {
          t += (*pFilter)[i][j];
        }
      }
      for (int i = 0; i < pFilterSize; i++) {
        for (int j = 0; j < pFilterSize; j++) {
        	(*pFilter)[i][j] = (*pFilter)[i][j] / t;
        }
      }
    }
  }

  void initFilter(Flame *pFlame) {
  	noiseFilterSize=calcFilterSize(pFlame->spatialFilterRadius);
  	initFilter(pFlame->spatialFilterRadius, noiseFilterSize, &filter);
/*
  	float adjust;
    calcFilterSize(pFlame->spatialFilterRadius, &noiseFilterSize, &adjust);
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
*/
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

  void calcDensity(float *minDensity, float *maxDensity, float *avgDensity) {
  	*minDensity = FLT_MAX;
  	*maxDensity = *avgDensity = 0.0f;
  	for(int y=0;y<rasterHeight;y++) {
  		for(int x=0;x<rasterWidth;x++) {
  			RasterPoint p=raster[y][x];
  			if(p.count<(*minDensity)) {
  				*minDensity=p.count;
  			}
  			if(p.count>(*maxDensity)) {
  				*maxDensity=p.count;
  			}
  			*avgDensity+=p.count;
  		}
  	}
  	*avgDensity/=(float)(rasterWidth * rasterHeight);
  }

  void transformPointNewShit(LogDensityPoint *pFilteredPnt, int pX, int pY, float minDesity, float maxDensity, float avgDensity) {
    float density;
    int densityRect=7;
    int dr2=densityRect/2;
    float curve = 1.25f;
    bool saveDens=false;

    if(pX<dr2 || pY<dr2 || (pX>=rasterWidth-dr2) || (pY>=rasterHeight-dr2)) {
      density=getRasterPoint(pX, pY)->count;
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



    	density =0.0f;
    	float kernelSum=0.0f;
    	for(int y=0;y<densityRect;y++) {
      	for(int x=0;x<densityRect;x++) {
      		float intensity=1.0f/(1.0f+(y-dr2)*(y-dr2)+(x-dr2)*(x-dr2));
      		kernelSum+=intensity;

      		//printf("(%d %d) %f\n",x,y,intensity);

      		density+=intensity*getRasterPoint(pX+x-dr2, pY+y-dr2)->count;
      	}
    	}
    	density/=kernelSum;
    }


    float oDensity=density;

    density=logf(density+1);
    density/=logf(maxDensity+1);

      float minRadius=0.05;
      float maxRadius=sqrtf(rasterWidth*rasterWidth + rasterHeight*rasterHeight)*0.5f;
      if(maxRadius>50.0f) {
      	maxRadius=50.0f;
      }
      float radius;
     	radius=minRadius+(maxRadius-minRadius)*erff(1.0f/(1.0f+ curve*density*density*density));
     	if(radius<minRadius) {
    		radius=minRadius;
    	}
     	else if(radius>maxRadius) {
     		radius=maxRadius;
     	}

       if(pX<dr2 || pY<dr2 || (pX>=rasterWidth-dr2) || (pY>=rasterHeight-dr2)) {

        }
        else {
//printf("density= %f, avg=%f, max=%f, r=%d\n", density, log(1.0f+avgDensity), log(1.0f+maxDensity), calcFilterSize(radius));
        }

        radius=fabs(erf(oDensity-getRasterPoint(pX, pY)->count)*curve);
        if(radius>50.0f)
        	radius=50.0f;
      //  printf("density= %f, rad=%f, deltaDens= %f, final rad=%d\n", oDensity, radius, (oDensity-getRasterPoint(pX, pY)->count), calcFilterSize(radius));

        if(saveDens) {
          pFilteredPnt->red =radius;
          pFilteredPnt->green =radius;
          pFilteredPnt->blue =radius;
          pFilteredPnt->intensity = radius;
       		return;
        }

  	int filterSize;
  	float **noiseFilter;
  	getFilter(radius, &filterSize, &noiseFilter);


    if (filterSize > 1) {
      pFilteredPnt->clear();
      for (int i = 0; i < filterSize; i++) {
        for (int j = 0; j < filterSize; j++) {
          RasterPoint *point = getRasterPoint(pX + j-filterSize/2 , pY + i-filterSize/2 );
          float logScale;
          if (point->count < PRECALC_LOG_ARRAY_SIZE) {
            logScale = precalcLogArray[(int) point->count];
          }
          else {
            logScale = (k1 * log10(1.0f + flame->whiteLevel * point->count * k2)) / (flame->whiteLevel * point->count);
          }
          pFilteredPnt->red += noiseFilter[i][j] * logScale * point->red;
          pFilteredPnt->green += noiseFilter[i][j] * logScale * point->green;
          pFilteredPnt->blue += noiseFilter[i][j] * logScale * point->blue;
          pFilteredPnt->intensity += noiseFilter[i][j] * logScale * point->count;
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


};

#endif // __JWF_LOG_DENSITY_FILTER_H__
