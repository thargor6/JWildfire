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

import org.jwildfire.base.Tools;

public class SimpleHDRImage {
  private int imageWidth = -1;
  private int imageHeight = -1;
  private float rBuffer[], gBuffer[], bBuffer[];

  public SimpleHDRImage(int pWidth, int pHeight) {
    int size = (int) pWidth * (int) pHeight;
    if (size > 0) {
      rBuffer = new float[size];
      gBuffer = new float[size];
      bBuffer = new float[size];
    }
    imageWidth = pWidth;
    imageHeight = pHeight;
  }

  public SimpleHDRImage() {
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public double getAspect() {
    return imageHeight != 0 ? (double) imageWidth / (double) imageHeight : 0.0;
  }

  @Override
  public SimpleHDRImage clone() {
    SimpleHDRImage res = new SimpleHDRImage(getImageWidth(), getImageHeight());
    res.imageWidth = getImageWidth();
    res.imageHeight = getImageHeight();
    if (res.rBuffer != null) {
      System.arraycopy(rBuffer, 0, res.rBuffer, 0, res.rBuffer.length);
      System.arraycopy(gBuffer, 0, res.gBuffer, 0, res.gBuffer.length);
      System.arraycopy(bBuffer, 0, res.bBuffer, 0, res.bBuffer.length);
    }
    return res;
  }

  public int getRGBEValue(int pX, int pY) {
    try {
      int offset = pY * imageWidth + pX;
      return convertRGBToRGBE(rBuffer[offset], gBuffer[offset], bBuffer[offset]);
    }
    catch (Exception ex) {
      throw new RuntimeException("(" + pX + ", " + pY + ") is out of bounds (0.."
          + (imageWidth - 1) + ", 0.." + (imageHeight - 1) + ")", ex);
    }
  }

  // bases on code from the HDRBitmapWriter class of Sunflow, just for testing now
  // TODO
  protected static int convertRGBToRGBE(float pR, float pG, float pB) {
    float v = max(pR, pG, pB);
    if (v < Tools.EPSILON)
      return 0;
    // get mantissa and exponent
    float m = v;
    int e = 0;
    if (v > 1.0f) {
      while (m > 1.0f) {
        m *= 0.5f;
        e++;
      }
    }
    else if (v <= 0.5f) {
      while (m <= 0.5f) {
        m *= 2.0f;
        e--;
      }
    }
    v = (m * 255.0f) / v;
    int c = (e + 128);
    c |= ((int) (pR * v) << 24);
    c |= ((int) (pG * v) << 16);
    c |= ((int) (pB * v) << 8);
    return c;
  }

  private static float max(float pA, float pB, float pC) {
    if (pA < pB) {
      pA = pB;
    }
    if (pA < pC) {
      pA = pC;
    }
    return pA;
  }

  public void fillBackground(int pRed, int pGreen, int pBlue) {
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

  private int getOffset(int pX, int pY) {
    return pY * imageWidth + pX;
  }

  public void setRGB(int pX, int pY, float pR, float pG, float pB) {
    int offset = getOffset(pX, pY);
    rBuffer[offset] = pR;
    gBuffer[offset] = pG;
    bBuffer[offset] = pB;
  }

  public double getRValue(int pX, int pY) {
    return rBuffer[getOffset(pX, pY)];
  }

  public double getGValue(int pX, int pY) {
    return gBuffer[getOffset(pX, pY)];
  }

  public double getBValue(int pX, int pY) {
    return bBuffer[getOffset(pX, pY)];
  }

}
