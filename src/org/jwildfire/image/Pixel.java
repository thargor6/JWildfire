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
package org.jwildfire.image;


public class Pixel {
  public int a, r, g, b;

  public Pixel() {

  }

  public Pixel(int pARGB) {
    setARGBValue(pARGB);
  }

  public void clear() {
    a = r = g = b = 0;
  }

  public void setARGBValue(int pValue) {
    a = (pValue >>> 24) & 0xff;
    r = (pValue >>> 16) & 0xff;
    g = (pValue >>> 8) & 0xff;
    b = pValue & 0xff;
  }

  public int getARGBValue() {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  public void assign(Pixel pSrcP) {
    r = pSrcP.r;
    g = pSrcP.g;
    b = pSrcP.b;
    a = pSrcP.a;
  }

  public void setRGB(int pR, int pG, int pB) {
    r = pR;
    g = pG;
    b = pB;
    a = 100;
  }

}
