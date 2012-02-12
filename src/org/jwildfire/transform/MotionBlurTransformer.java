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
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class MotionBlurTransformer extends Mesh2DTransformer {

  @Property(description = "Blur amount in horizontal direction")
  private int deltaX = 3;

  @Property(description = "Blur amount in vertical direction")
  private int deltaY = 1;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage bgImg = (SimpleImage) pImg;
    SimpleImage fgImg = srcImg;

    int bgWidth = bgImg.getImageWidth();
    int bgHeight = bgImg.getImageHeight();
    int x1 = 0, y1 = 0;
    int x2 = this.deltaX;
    int y2 = this.deltaY;
    if ((x2 == 0) && (y2 == 0))
      return;
    int n;
    if (x2 > 0)
      n = x2;
    else
      n = 0 - x2;
    if (y2 > 0)
      n += y2;
    else
      n += 0 - y2;
    n++;
    n *= 2;
    int x[] = new int[n];
    int y[] = new int[n];
    n = Tools.bresenham(x1, y1, x2, y2, x, y);

    int mix = 25;
    int m1 = 100 - mix;
    int m2 = mix;

    for (int k = 1; k < n; k++) {
      int left = x[k];
      int top = y[k];
      /* case 1 */
      int bgLeft = 0, bgTop = 0, fgLeft = 0, fgTop = 0;
      int hSize = 0, vSize = 0;
      if ((left >= 0) && (top >= 0)) {
        if ((left >= bgWidth) || (top >= bgHeight))
          return;
        hSize = bgWidth - left;
        if (hSize > bgWidth)
          hSize = bgWidth;
        vSize = bgHeight - top;
        if (vSize > bgHeight)
          vSize = bgHeight;
        bgLeft = left;
        bgTop = top;
        fgLeft = 0;
        fgTop = 0;
      }
      /* case 2 */
      else if ((left < 0) && (top >= 0)) {
        if ((left <= (0 - bgWidth)) || (top >= bgHeight))
          return;

        hSize = bgWidth + left;
        if (hSize > bgWidth)
          hSize = bgWidth;
        vSize = bgHeight - top;
        if (vSize > bgHeight)
          vSize = bgHeight;

        bgLeft = 0;
        bgTop = top;

        fgLeft = 0 - left;
        fgTop = 0;
      }
      /* case 3 */
      else if ((left >= 0) && (top < 0)) {
        if ((left >= bgWidth) || (top <= (0 - bgHeight)))
          return;
        hSize = bgWidth - left;
        if (hSize > bgWidth)
          hSize = bgWidth;
        vSize = bgHeight + top;
        if (vSize > bgHeight)
          vSize = bgHeight;

        bgLeft = left;
        bgTop = 0;
        fgLeft = 0;
        fgTop = 0 - top;
      }
      /* case 4 */
      else if ((left < 0) && (top < 0)) {
        if ((left <= (0 - bgWidth)) || (top <= (0 - bgHeight)))
          return;

        hSize = bgWidth + left;
        if (hSize > bgWidth)
          hSize = bgWidth;
        vSize = bgHeight + top;
        if (vSize > bgHeight)
          vSize = bgHeight;

        bgLeft = 0;
        bgTop = 0;

        fgTop = 0 - top;
        fgLeft = 0 - left;
      }

      Pixel bgPixel = new Pixel();
      Pixel fgPixel = new Pixel();
      for (int i = 0; i < vSize; i++) {
        for (int j = 0; j < hSize; j++) {
          bgPixel.setARGBValue(bgImg.getARGBValue(j + bgLeft, i + bgTop));
          fgPixel.setARGBValue(fgImg.getARGBValue(j + fgLeft, i + fgTop));
          bgPixel.r = ((int) (bgPixel.r * m1) + (int) (fgPixel.r) * m2) / (int) 100;
          bgPixel.g = ((int) (bgPixel.g * m1) + (int) (fgPixel.g) * m2) / (int) 100;
          bgPixel.b = ((int) (bgPixel.b * m1) + (int) (fgPixel.b) * m2) / (int) 100;
          bgImg.setRGB(j + bgLeft, i + bgTop, bgPixel);
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    deltaX = -30;
    deltaY = 12;
  }

  public int getDeltaX() {
    return deltaX;
  }

  public void setDeltaX(int deltaX) {
    this.deltaX = deltaX;
  }

  public int getDeltaY() {
    return deltaY;
  }

  public void setDeltaY(int deltaY) {
    this.deltaY = deltaY;
  }
}
