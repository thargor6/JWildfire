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
#ifndef __JWF_RGB_PALETTE_H__
#define __JWF_RGB_PALETTE_H__

struct RGBPalette {
  #define PALETTE_SIZE 256
  RGBColor *rawColors;

  void create() {
    rawColors = NULL;
    hostMalloc((void**)&rawColors,sizeof(RGBColor)*PALETTE_SIZE);
    memset(rawColors,0,sizeof(RGBColor)*PALETTE_SIZE);
  }

  void free() {
    if(rawColors!=NULL) {
      hostFree(rawColors);
      rawColors=NULL;
    }
  }

  void dump() {
    printf("  RGBPalette {\n");
    printf("   ");
    for(int i=0;i<10 /*PALETTE_SIZE*/;i++) {
      printf(" (%d %d %d)",rawColors[i].red,rawColors[i].green,rawColors[i].blue);
    }
    printf("\n");
	  printf("  }\n");
  }

  int getSize() {
    return PALETTE_SIZE;
  }

  void setColor(int pIndex, int pRed, int pGreen, int pBlue) {
    rawColors[pIndex].red=pRed;
    rawColors[pIndex].green=pGreen;
    rawColors[pIndex].blue=pBlue;
  }

  RenderColor* createRenderPalette(int pWhiteLevel) {
    //transformColors();
    RenderColor *res;
    hostMalloc((void**)&res,PALETTE_SIZE*sizeof(RenderColor));
    for (int i = 0; i < PALETTE_SIZE; i++) {
      res[i].red = (rawColors[i].red * pWhiteLevel) / 256.0f;
      res[i].green = (rawColors[i].green * pWhiteLevel) / 256.0f;
      res[i].blue = (rawColors[i].blue * pWhiteLevel) / 256.0f;
    }
    return res;
  }

};


#endif // __JWF_RGB_PALETTE_H__
