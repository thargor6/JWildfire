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
#ifndef __JWF_SIMPLE_IMAGE_H__
#define __JWF_SIMPLE_IMAGE_H__

struct SimpleImage{
  Pixel *toolPixel;
  int imageWidth;
  int imageHeight;
  int *bufferedImg;

  void create() {
    toolPixel=NULL;

    hostMalloc((void**)&toolPixel,sizeof(Pixel));
  }

  void free() {
    if(toolPixel!=NULL) {
      hostFree(toolPixel);
      toolPixel=NULL;
    }
    if(bufferedImg!=NULL) {
      hostFree(bufferedImg);
      bufferedImg=NULL;
    }
  }

  void init(int pWidth, int pHeight) {
    long size=pWidth*pHeight;
    if(size>0) {
      hostMalloc((void**)&bufferedImg,size*sizeof(int));
    }
    imageWidth = pWidth;
    imageHeight = pHeight;
  }

  float getAspect() {
    return imageHeight != 0 ? (float) imageWidth / (float) imageHeight : 0.0f;
  }

  int getScaledWidth(int pScaledHeight) {
    return (int) ((float) pScaledHeight * getAspect() + 0.5f);
  }

  int getScaledHeight(int pScaledWidth) {
    return (int) ((float) pScaledWidth / getAspect() + 0.5f);
  }

  void setRGB(int pX, int pY, int pR, int pG, int pB) {
    toolPixel->setRGB(pR, pG, pB);
    int argb = toolPixel->getARGBValue();
    bufferedImg[pX+pY*imageWidth]= argb;
  }

  void setARGBValue(int pX, int pY, int pARGBValue) {
    bufferedImg[pX+pY*imageWidth]= pARGBValue;
  }

  void setRGB(int pX, int pY, Pixel *pPixel) {
    bufferedImg[pX+pY*imageWidth]=pPixel->getARGBValue();
  }

  int getARGBValue(int pX, int pY) {
    return bufferedImg[pX+pY*imageWidth];
  }
 
  int getARGBValueIgnoreBounds(int pX, int pY) {
    if ((pX < 0) || (pX >= imageWidth) || (pY < 0) || (pY >= imageHeight))
      return 0;
    else
      return bufferedImg[pX+pY*imageWidth];
  }

  int getRValueIgnoreBounds(int pX, int pY) {
    if ((pX < 0) || (pX >= imageWidth) || (pY < 0) || (pY >= imageHeight))
      return 0;
    else
      return getRValue(pX, pY);
  }

  int getGValueIgnoreBounds(int pX, int pY) {
    if ((pX < 0) || (pX >= imageWidth) || (pY < 0) || (pY >= imageHeight))
      return 0;
    else
      return getGValue(pX, pY);
  }

  int getRValue(int pX, int pY) {
    toolPixel->setARGBValue(getARGBValue(pX, pY));
    return toolPixel->r;
  }

  int getGValue(int pX, int pY) {
    toolPixel->setARGBValue(getARGBValue(pX, pY));
    return toolPixel->g;
  }

  int getBValue(int pX, int pY) {
    toolPixel->setARGBValue(getARGBValue(pX, pY));
    return toolPixel->b;
  }

  void fillBackground(int pR, int pG, int pB) {
    toolPixel->r=pR;
    toolPixel->g=pG;
    toolPixel->b=pB;
    memset(bufferedImg,toolPixel->getARGBValue(),imageWidth*imageHeight*sizeof(int));
  }

};


#endif // __JWF_SIMPLE_IMAGE_H__

