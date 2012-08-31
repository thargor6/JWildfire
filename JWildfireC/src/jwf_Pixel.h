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
#ifndef __JWF_PIXEL_H__
#define __JWF_PIXEL_H__

struct Pixel {
  int a, r, g, b;

  void create() {
    clear();
  }

  void init(int pARGB) {
    setARGBValue(pARGB);
  }

  void clear() {
    a = r = g = b = 0;
  }

  void setARGBValue(int pValue) {
    a = (pValue >> 24) & 0xff;
    r = (pValue >> 16) & 0xff;
    g = (pValue >> 8) & 0xff;
    b = pValue & 0xff;
  }

  int getARGBValue() {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  void assign(Pixel *pSrcP) {
    r = pSrcP->r;
    g = pSrcP->g;
    b = pSrcP->b;
    a = pSrcP->a;
  }

  void setRGB(int pR, int pG, int pB) {
    r = pR;
    g = pG;
    b = pB;
    a = 100;
  }
};


#endif // __JWF_PIXEL_H__