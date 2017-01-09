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
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;

public class SimpleHDRImage implements WFImage, Cloneable {
  private Pixel toolPixel = new Pixel();
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

  @Override
  public int getImageWidth() {
    return imageWidth;
  }

  @Override
  public int getImageHeight() {
    return imageHeight;
  }

  @Override
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

  protected static int convertRGBToRGBE(float pR, float pG, float pB) {
    float mVal = max(pR, pG, pB);
    if (mVal < MathLib.EPSILON) {
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

  public void getRGBValues(float pRGB[], int pX, int pY) {
    int offset = getOffset(pX, pY);
    pRGB[0] = rBuffer[offset];
    pRGB[1] = gBuffer[offset];
    pRGB[2] = bBuffer[offset];
  }

  public static float calcLum(float pR, float pG, float pB) {
    return 0.299f * pR + 0.588f * pG + 0.113f * pB;
  }

  public float getLum(int pX, int pY) {
    int offset = getOffset(pX, pY);
    return calcLum(rBuffer[offset], gBuffer[offset], bBuffer[offset]);
  }

  public float getRValue(int pX, int pY) {
    return rBuffer[getOffset(pX, pY)];
  }

  public float getGValue(int pX, int pY) {
    return gBuffer[getOffset(pX, pY)];
  }

  public float getBValue(int pX, int pY) {
    return bBuffer[getOffset(pX, pY)];
  }

  public void sampleDown(int pOversample) {
    if (pOversample == 1) {
      return;
    }
    if (pOversample < 1 || (pOversample > 1 && (imageWidth % pOversample != 0 || imageHeight % pOversample != 0))) {
      throw new IllegalArgumentException("oversample " + pOversample);
    }
    int newWidth = imageWidth / pOversample;
    int newHeight = imageHeight / pOversample;
    int newSize = newWidth * newHeight;
    double div = pOversample * pOversample;
    float rNew[] = new float[newSize];
    float gNew[] = new float[newSize];
    float bNew[] = new float[newSize];
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
    rBuffer = rNew;
    gBuffer = gNew;
    bBuffer = bNew;
  }

  public void assignImage(SimpleHDRImage pHDRImg) {
    imageWidth = pHDRImg.imageWidth;
    imageHeight = pHDRImg.imageHeight;
    rBuffer = pHDRImg.rBuffer;
    gBuffer = pHDRImg.gBuffer;
    bBuffer = pHDRImg.bBuffer;
  }

  private static final float[] EXPONENT = new float[256];

  static {
    EXPONENT[0] = 0;
    for (int i = 1; i < 256; i++) {
      float f = 1.0f;
      int e = i - (128 + 8);
      if (e > 0)
        for (int j = 0; j < e; j++)
          f *= 2.0f;
      else
        for (int j = 0; j < -e; j++)
          f *= 0.5f;
      EXPONENT[i] = f;
    }
  }

  public void setRGBEValue(int pX, int pY, int pR, int pG, int pB, int pE) {
    float e = EXPONENT[pE];
    int off = getOffset(pX, pY);
    rBuffer[off] = e * (pR + 0.5f);
    gBuffer[off] = e * (pG + 0.5f);
    bBuffer[off] = e * (pB + 0.5f);
  }

  public void getMinMaxLum(float[] pLum) {
    float minLum = Float.MAX_VALUE;
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

  public double getLumIgnoreBounds(int pX, int pY) {
    if (pX >= 0 && pX < imageWidth && pY >= 0 && pY < imageHeight) {
      return getLum(pX, pY);
    }
    else {
      return 0;
    }
  }

  public void fillBackground(SimpleImage pImage) {
    if (pImage.getImageWidth() == imageWidth && pImage.getImageHeight() == imageHeight) {
      for (int i = 0; i < imageHeight; i++) {
        for (int j = 0; j < imageWidth; j++) {
          toolPixel.setARGBValue(pImage.getARGBValue(j, i));
          setRGB(j, i, toolPixel.r, toolPixel.g, toolPixel.b);
        }
      }
    }
    else {
      for (int i = 0; i < imageHeight; i++) {
        for (int j = 0; j < imageWidth; j++) {
          double xCoord = (double) j * (double) (pImage.getImageWidth() - 1) / (double) (imageWidth - 1);
          double yCoord = (double) i * (double) (pImage.getImageHeight() - 1) / (double) (imageHeight - 1);

          toolPixel.setARGBValue(pImage.getARGBValueIgnoreBounds((int) xCoord, (int) yCoord));
          int luR = toolPixel.r;
          int luG = toolPixel.g;
          int luB = toolPixel.b;

          toolPixel.setARGBValue(pImage.getARGBValueIgnoreBounds(((int) xCoord) + 1, (int) yCoord));
          int ruR = toolPixel.r;
          int ruG = toolPixel.g;
          int ruB = toolPixel.b;
          toolPixel.setARGBValue(pImage.getARGBValueIgnoreBounds((int) xCoord, ((int) yCoord) + 1));
          int lbR = toolPixel.r;
          int lbG = toolPixel.g;
          int lbB = toolPixel.b;
          toolPixel.setARGBValue(pImage.getARGBValueIgnoreBounds(((int) xCoord) + 1, ((int) yCoord) + 1));
          int rbR = toolPixel.r;
          int rbG = toolPixel.g;
          int rbB = toolPixel.b;

          double x = MathLib.frac(xCoord);
          double y = MathLib.frac(yCoord);
          float r = (float) Tools.roundColor(GfxMathLib.blerp(luR, ruR, lbR, rbR, x, y)) / 255.0f;
          float g = (float) Tools.roundColor(GfxMathLib.blerp(luG, ruG, lbG, rbG, x, y)) / 255.f;
          float b = (float) Tools.roundColor(GfxMathLib.blerp(luB, ruB, lbB, rbB, x, y)) / 255.f;
          setRGB(j, i, r, g, b);
        }
      }
    }
  }

}
