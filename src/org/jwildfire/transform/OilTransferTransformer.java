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
package org.jwildfire.transform;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class OilTransferTransformer extends PixelTransformer {
  protected SimpleImage srcImg;

  @Property(description = "Size of the rect to compute the most popular color")
  @PropertyMin(1)
  @PropertyMax(16)
  private int rect = 6;

  @Override
  protected void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    int nn = rect;
    if (nn < 1) {
      pPixel.setARGBValue(srcImg.getARGBValue(pX, pY));
      return;
    }
    int n = nn;
    if ((n % 2) != 0)
      n++;
    int n2 = n / 2;
    int width = pImageWidth;
    int height = pImageHeight;

    int[] color = new int[n * n];

    /* fill the rect with valid pixels */
    for (int i = pY - n2, k = 0; i < (pY + n2); i++) {
      for (int j = pX - n2; j < (pX + n2); j++, k++) {
        if ((i >= 0) && (i < height) && (j >= 0) && (j < width)) {
          color[k] = srcImg.getARGBValue(j, i);
        }
        else {
          color[k] = -1;
        }
      }
    }
    /* find the most popular color */
    int maxcnt = 0;
    int cnt = 0;
    int col = 0;
    for (int i = 0; i < n * n; i++) {
      if (color[i] != -1) {
        cnt = 1;
        for (int j = i + 1; j < n * n; j++) {
          if (color[i] == color[j])
            cnt++;
        }
        if (cnt > maxcnt) {
          col = i;
          maxcnt = cnt;
        }
      }
    }
    pPixel.setARGBValue(color[col]);
  }

  @Override
  protected void initTransformation(WFImage pImg) {
    srcImg = ((SimpleImage) pImg).clone();
  }

  @Override
  protected void cleanupTransformation(WFImage pImg) {
    srcImg = null;
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    rect = 6;
  }

  public int getRect() {
    return rect;
  }

  public void setRect(int rect) {
    this.rect = rect;
  }
}
