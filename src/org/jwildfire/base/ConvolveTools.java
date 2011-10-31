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
package org.jwildfire.base;

import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

/**
 * Some utility routines related to applying convolution matrices
 * 
  @author Andreas Maschke
*/
public class ConvolveTools {

  public static void convolve_3x3_grey(SimpleImage pSrcImg, SimpleImage pImg, int[][] pMatrix,
      int pCVAdd) {
    int width = pSrcImg.getImageWidth();
    int height = pSrcImg.getImageHeight();
    int sizeM = pMatrix.length;
    int sizeMHalve = sizeM / 2;
    int sumM = 0;
    for (int i = 0; i < sizeM; i++) {
      for (int j = 0; j < sizeM; j++) {
        sumM += pMatrix[i][j];
      }
    }
    if (sumM == 0)
      sumM = 1;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int intSum = 0;
        for (int k = 0; k < sizeM; k++) {
          int y = i - sizeMHalve + k;
          for (int l = 0; l < sizeM; l++) {
            int x = j - sizeMHalve + l;
            intSum += pSrcImg.getRValueIgnoreBounds(x, y) * pMatrix[k][l];
          }
        }
        intSum = (intSum / sumM) + pCVAdd;
        if (intSum < 0)
          intSum = 0;
        else if (intSum > 255)
          intSum = 255;
        pImg.setRGB(j, i, intSum, intSum, intSum);
      }
    }
  }

  public static void convolve_3x3_color(SimpleImage pSrcImg, SimpleImage pImg, int[][] pMatrix,
      int pCVAdd) {
    int width = pSrcImg.getImageWidth();
    int height = pSrcImg.getImageHeight();
    int sizeM = pMatrix.length;
    int sizeMHalve = sizeM / 2;
    int sumM = 0;
    for (int i = 0; i < sizeM; i++) {
      for (int j = 0; j < sizeM; j++) {
        sumM += pMatrix[i][j];
      }
    }
    if (sumM == 0)
      sumM = 1;
    Pixel pixel = new Pixel();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int intSumR = 0;
        int intSumG = 0;
        int intSumB = 0;
        for (int k = 0; k < sizeM; k++) {
          int y = i - sizeMHalve + k;
          for (int l = 0; l < sizeM; l++) {
            int x = j - sizeMHalve + l;
            pixel.setARGBValue(pSrcImg.getARGBValueIgnoreBounds(x, y));
            intSumR += pixel.r * pMatrix[k][l];
            intSumG += pixel.g * pMatrix[k][l];
            intSumB += pixel.b * pMatrix[k][l];
          }
        }
        intSumR = Tools.limitColor((intSumR / sumM) + pCVAdd);
        intSumG = Tools.limitColor((intSumG / sumM) + pCVAdd);
        intSumB = Tools.limitColor((intSumB / sumM) + pCVAdd);
        pImg.setRGB(j, i, intSumR, intSumG, intSumB);
      }
    }
  }
}
