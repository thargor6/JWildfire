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

import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer.BufferType;

public abstract class Mesh2DTransformer extends Transformer {
  protected SimpleImage srcImg;
  protected Pixel srcP = new Pixel();
  protected Pixel srcQ = new Pixel();
  protected Pixel srcR = new Pixel();
  protected Pixel srcS = new Pixel();

  protected abstract void performPixelTransformation(WFImage pImg);

  @Override
  public boolean acceptsInputBufferType(BufferType pBufferType) {
    return pBufferType == BufferType.IMAGE;
  }

  protected void readSrcPixels(double pX, double pY) {
    int x = (int) (pX + 0.5);
    int y = (int) (pY + 0.5);
    int w = srcImg.getImageWidth();
    int h = srcImg.getImageHeight();
    if ((x < 0) || (x >= w) || (y < 0) || (y >= h)) {
      srcP.clear();
      srcQ.clear();
      srcR.clear();
      srcS.clear();
    }
    else {
      srcP.setARGBValue(srcImg.getBufferedImg().getRGB(x, y));
      if (x < w - 1)
        srcQ.setARGBValue(srcImg.getBufferedImg().getRGB(x + 1, y));
      else
        srcQ.assign(srcP);
      if (y < h - 1) {
        srcR.setARGBValue(srcImg.getBufferedImg().getRGB(x, y + 1));
        if (x < w - 1)
          srcS.setARGBValue(srcImg.getBufferedImg().getRGB(x + 1, y + 1));
        else
          srcS.assign(srcQ);
      }
      else {
        srcR.assign(srcP);
        srcS.assign(srcQ);
      }
    }
  }

  @Override
  protected void initTransformation(WFImage pImg) {
    super.initTransformation(pImg);
    srcImg = ((SimpleImage) pImg).clone();
  }

  @Override
  protected void cleanupTransformation(WFImage pImg) {
    srcImg = null;
    super.cleanupTransformation(pImg);
  }

  @Override
  protected void performImageTransformation(WFImage pImg) {
    performPixelTransformation(pImg);
  }

  protected void applySmoothing(SimpleImage pImg, int pSmoothingAmount) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel pixel = new Pixel();
    for (int k = 0; k < pSmoothingAmount; k++) {
      for (int i = 1; i < (height - 1); i++) {
        /* process pixels 2..width-1 */
        for (int j = 1; j < (width - 1); j++) {
          pixel.setARGBValue(pImg.getARGBValue(j, i - 1));
          int r1 = pixel.r;
          int g1 = pixel.g;
          int b1 = pixel.b;
          pixel.setARGBValue(pImg.getARGBValue(j + 1, i));
          int r2 = pixel.r;
          int g2 = pixel.g;
          int b2 = pixel.b;
          pixel.setARGBValue(pImg.getARGBValue(j, i + 1));
          int r3 = pixel.r;
          int g3 = pixel.g;
          int b3 = pixel.b;
          pixel.setARGBValue(pImg.getARGBValue(j, i));
          int r = pixel.r;
          int g = pixel.g;
          int b = pixel.b;

          int av1 = (r1 + g1 + b1);
          int av2 = (r2 + g2 + b2);
          int av3 = (r3 + g3 + b3);
          int av = (r + g + b);

          int d1 = av1 - av;
          if (d1 < 0)
            d1 = 0 - d1;
          int d2 = av2 - av;
          if (d2 < 0)
            d2 = 0 - d2;
          int d3 = av3 - av;
          if (d3 < 0)
            d3 = 0 - d3;

          if ((d1 > 64) && (d2 > 64)) {
            int rv = r1 * d1 + r2 * d2;
            rv /= (d1 + d2);
            rv += r;
            rv /= 2;
            int gv = g1 * d1 + g2 * d2;
            gv /= (d1 + d2);
            gv += g;
            gv /= 2;
            int bv = b1 * d1 + b2 * d2;
            bv /= (d1 + d2);
            bv += b;
            bv /= 2;
            pImg.setRGB(j, i, rv, gv, bv);
          }
          else if ((d2 > 64) && (d3 > 64)) {
            int rv = r2 * d2 + r3 * d3;
            rv /= (d2 + d3);
            rv += r;
            rv /= 2;
            int gv = g2 * d2 + g3 * d3;
            gv /= (d2 + d3);
            gv += g;
            gv /= 2;
            int bv = b2 * d2 + b3 * d3;
            bv /= (d2 + d3);
            bv += b;
            bv /= 2;
            pImg.setRGB(j, i, rv, gv, bv);
          }
        }
      }
    }
  }

  @Override
  public boolean supports3DOutput() {
    return false;
  }
}
