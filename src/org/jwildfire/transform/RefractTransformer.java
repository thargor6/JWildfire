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

public class RefractTransformer extends Mesh2DTransformer {

  @Property(description = "Intensity of the effect in x-direction")
  private double scaleX = 0.3;

  @Property(description = "Intensity of the effect in y-direction")
  private double scaleY = 0.25;

  @Property(description = "Wrap the image at the borders")
  private boolean wrap = true;

  private double avg;
  private Pixel toolPixel = new Pixel();

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();

    double xscl = this.scaleX;
    double yscl = this.scaleY;

    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;

    Pixel pPixel = new Pixel();
    for (int pY = 0; pY < height; pY++) {
      for (int pX = 0; pX < width; pX++) {
        pPixel.setARGBValue(img.getARGBValue(pX, pY));
        int argb = srcImg.getARGBValue(pX, pY);
        toolPixel.setARGBValue(argb);
        double gint = (double) (toolPixel.r - avg);
        double x = (double) pX - xscl * gint;
        double y = (double) pY - yscl * gint;
        /* color-wrapping */
        if (this.wrap) {
          while (x >= ((double) width - 0.5))
            x -= (double) (width - 1);
          while ((int) x < 0.5)
            x += (double) (width - 1);
          while (y >= ((double) height - 0.5))
            y -= (double) (height - 1);
          while ((int) y < 0.5)
            y += (double) (height - 1);
        }
        /* render it */
        double xi = Tools.fmod33(x);
        double yi = Tools.fmod33(y);
        if ((x < 0.0) || (x > w1) || (y < 0.0) || (y > h1)) {
          pPixel.r = pPixel.g = pPixel.b = 0;
        }
        else {
          readSrcPixels(x, y);
          pPixel.r = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.r) + xi * (srcQ.r)) + yi
              * ((1.0 - xi) * (srcR.r) + xi * (srcS.r))));
          pPixel.g = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.g) + xi * (srcQ.g)) + yi
              * ((1.0 - xi) * (srcR.g) + xi * (srcS.g))));
          pPixel.b = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.b) + xi * (srcQ.b)) + yi
              * ((1.0 - xi) * (srcR.b) + xi * (srcS.b))));
        }
        img.setRGB(pX, pY, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  @Override
  protected void initTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    super.initTransformation(pImg);
    /* compute the average of the gray map */
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    long sum = 0;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        sum += img.getRValue(j, i);
      }
    }
    avg = (double) sum / (double) (width * height);
  }

  @Override
  protected void cleanupTransformation(WFImage pImg) {
    super.cleanupTransformation(pImg);
    applySmoothing((SimpleImage) pImg, 1);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    scaleX = 0.3;
    scaleY = 0.25;
    wrap = true;
  }

  public double getScaleX() {
    return scaleX;
  }

  public void setScaleX(double scaleX) {
    this.scaleX = scaleX;
  }

  public double getScaleY() {
    return scaleY;
  }

  public void setScaleY(double scaleY) {
    this.scaleY = scaleY;
  }

  public boolean isWrap() {
    return wrap;
  }

  public void setWrap(boolean wrap) {
    this.wrap = wrap;
  }

}
