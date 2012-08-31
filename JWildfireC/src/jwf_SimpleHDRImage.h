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
#ifndef __JWF_SIMPLE_HDR_IMAGE_H__
#define __JWF_SIMPLE_HDR_IMAGE_H__

struct SimpleHDRImage {
  int imageWidth;
  int imageHeight;
  float *rBuffer, *gBuffer, *bBuffer;
  float *EXPONENT;



  void create() {
    imageWidth=0;
    imageHeight=0;
    rBuffer = gBuffer = bBuffer = NULL;
    EXPONENT=NULL;

    hostMalloc((void**)&EXPONENT,256*sizeof(float));
    EXPONENT[0] = 0;
    for (int i = 1; i < 256; i++) {
      float f = 1.0f;
      int e = i - (128 + 8);
      if (e > 0) {
        for (int j = 0; j < e; j++) {
          f *= 2.0f;
        }
      }
      else {
        for (int j = 0; j < -e; j++) {
          f *= 0.5f;
        }
      }
      EXPONENT[i] = f;
    }

  }

  void free() {
    if(EXPONENT!=NULL) {
      hostFree(EXPONENT);
      EXPONENT=NULL;
    }
    if(rBuffer!=NULL) {
      hostFree(rBuffer);
      rBuffer=NULL;
    }
    if(gBuffer!=NULL) {
      hostFree(gBuffer);
      gBuffer=NULL;
    }
    if(bBuffer!=NULL) {
      hostFree(bBuffer);
      bBuffer=NULL;
    }
  }

  void init(int pWidth, int pHeight) {
    if(rBuffer!=NULL) {
      hostFree(rBuffer);
      rBuffer=NULL;
    }
    if(gBuffer!=NULL) {
      hostFree(gBuffer);
      gBuffer=NULL;
    }
    if(bBuffer!=NULL) {
      hostFree(bBuffer);
      bBuffer=NULL;
    }
    int size = (int) pWidth * (int) pHeight;
    if (size > 0) {
      hostMalloc((void**)&rBuffer, size*sizeof(float));
      hostMalloc((void**)&gBuffer, size*sizeof(float));
      hostMalloc((void**)&bBuffer, size*sizeof(float));
    }
    imageWidth = pWidth;
    imageHeight = pHeight;
  }

  int getRGBEValue(int pX, int pY) {
    int offset = pY * imageWidth + pX;
    return convertRGBToRGBE(rBuffer[offset], gBuffer[offset], bBuffer[offset]);
  }

  float max3(float pA, float pB, float pC) {
    if (pA < pB) {
      pA = pB;
    }
    if (pA < pC) {
      pA = pC;
    }
    return pA;
  }

  int convertRGBToRGBE(float pR, float pG, float pB) {
    float mVal = max3(pR, pG, pB);
    if (mVal < EPSILON) {
      return 0;
    }
    float mantissa = mVal;
    int exponent = 0;
    if (mVal > 1.0f) {
      while (mantissa > 1.0f) {
        mantissa *= 0.5f;
        exponent++;
      }
    }
    else if (mVal <= 0.5f) {
      while (mantissa <= 0.5f) {
        mantissa *= 2.0f;
        exponent--;
      }
    }
    mVal = (mantissa * 255.0f) / mVal;
    int res = (exponent + 128);
    res |= ((int) (pR * mVal) << 24);
    res |= ((int) (pG * mVal) << 16);
    res |= ((int) (pB * mVal) << 8);
    return res;
  }


  void fillBackground(int pRed, int pGreen, int pBlue) {
    float r = (float) pRed / 255.0f;
    float g = (float) pGreen / 255.0f;
    float b = (float) pBlue / 255.0f;
    int size = imageWidth * imageHeight;
    for (int i = 0; i < size; i++) {
      rBuffer[i] = r;
      gBuffer[i] = g;
      bBuffer[i] = b;
    }
  }

  int getOffset(int pX, int pY) {
    return pY * imageWidth + pX;
  }

  void setRGB(int pX, int pY, float pR, float pG, float pB) {
    int offset = getOffset(pX, pY);
    rBuffer[offset] = pR;
    gBuffer[offset] = pG;
    bBuffer[offset] = pB;
  }

  void getRGBValues(float pRGB[], int pX, int pY) {
    int offset = getOffset(pX, pY);
    pRGB[0] = rBuffer[offset];
    pRGB[1] = gBuffer[offset];
    pRGB[2] = bBuffer[offset];
  }

  float calcLum(float pR, float pG, float pB) {
    return 0.299f * pR + 0.588f * pG + 0.113f * pB;
  }

  float getLum(int pX, int pY) {
    int offset = getOffset(pX, pY);
    return calcLum(rBuffer[offset], gBuffer[offset], bBuffer[offset]);
  }

  float getRValue(int pX, int pY) {
    return rBuffer[getOffset(pX, pY)];
  }

  float getGValue(int pX, int pY) {
    return gBuffer[getOffset(pX, pY)];
  }

  float getBValue(int pX, int pY) {
    return bBuffer[getOffset(pX, pY)];
  }

  void sampleDown(int pOversample) {
    if (pOversample == 1) {
      return;
    }
    if (pOversample < 1 || (pOversample > 1 && (imageWidth % pOversample != 0 || imageHeight % pOversample != 0))) {
      printf("oversample %d\n" + pOversample);exit(-1);
    }
    int newWidth = imageWidth / pOversample;
    int newHeight = imageHeight / pOversample;
    int newSize = newWidth * newHeight;
    double div = pOversample * pOversample;
    float *rNew,*gNew,*bNew;
    hostMalloc((void**)&rNew,newSize*sizeof(float));
    hostMalloc((void**)&gNew,newSize*sizeof(float));
    hostMalloc((void**)&bNew,newSize*sizeof(float));
    for (int rowOld = 0, rowNew = 0; rowOld < imageHeight; rowOld += pOversample, rowNew++) {
      for (int colOld = 0, colNew = 0; colOld < imageWidth; colOld += pOversample, colNew++) {
        double r = 0.0, g = 0.0, b = 0.0;
        for (int i = rowOld; i < rowOld + pOversample; i++) {
          for (int j = colOld; j < colOld + pOversample; j++) {
            int off = getOffset(j, i);
            r += rBuffer[off];
            g += gBuffer[off];
            b += bBuffer[off];
          }
        }
        int off = rowNew * newWidth + colNew;
        rNew[off] = (float) (r / div);
        gNew[off] = (float) (g / div);
        bNew[off] = (float) (b / div);
      }
    }
    imageWidth = newWidth;
    imageHeight = newHeight;
    hostFree(rBuffer);
    rBuffer = rNew;
    hostFree(gBuffer);
    gBuffer = gNew;
    hostFree(bBuffer);
    bBuffer = bNew;
  }

  void assignImage(SimpleHDRImage *pHDRImg) {
    imageWidth = pHDRImg->imageWidth;
    imageHeight = pHDRImg->imageHeight;
    rBuffer = pHDRImg->rBuffer;
    gBuffer = pHDRImg->gBuffer;
    bBuffer = pHDRImg->bBuffer;
  }

  void setRGBEValue(int pX, int pY, int pR, int pG, int pB, int pE) {
    float e = EXPONENT[pE];
    int off = getOffset(pX, pY);
    rBuffer[off] = e * (pR + 0.5f);
    gBuffer[off] = e * (pG + 0.5f);
    bBuffer[off] = e * (pB + 0.5f);
  }

  void getMinMaxLum(float* pLum) {
    float minLum = FLT_MAX;
    float maxLum = 0.0f;
    for (int i = 0; i < imageHeight; i++) {
      for (int j = 0; j < imageWidth; j++) {
        float lum = getLum(j, i);
        if (lum < minLum) {
          minLum = lum;
        }
        if (lum > maxLum) {
          maxLum = lum;
        }
      }
    }
    pLum[0] = minLum;
    pLum[1] = maxLum;
  }

  float getLumIgnoreBounds(int pX, int pY) {
    if (pX >= 0 && pX < imageWidth && pY >= 0 && pY < imageHeight) {
      return getLum(pX, pY);
    }
    else {
      return 0;
    }
  }

};

#endif // __JWF_SIMPLE_HDR_IMAGE_H__