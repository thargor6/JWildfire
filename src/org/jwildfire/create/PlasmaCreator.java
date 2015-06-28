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
package org.jwildfire.create;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;


public class PlasmaCreator extends ImageCreator {
  @Property(description = "Inverse cluster size")
  @PropertyMin(0.01)
  private double dimension = 150.0;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed = 345;

  @Override
  protected void fillImage(SimpleImage res) {
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    double frac = this.dimension;
    if (frac <= 0.0)
      frac = 20.0;
    PixelColor pixel = new PixelColor();
    srand123(this.seed);

    int w2 = width;
    int h2 = height;
    double f13 = (float) 1.0 / (float) 3.0;
    double f14 = (float) 1.0 / (float) 4.0;
    double dimf = frac / ((float) h2 * (float) w2);
    int f[][] = new int[256][4];
    f[0][0] = 0;
    f[0][1] = 0;
    f[0][2] = w2 - 1;
    f[0][3] = h2 - 1;
    double currr, currg, currb;

    currr = drand48();
    currg = drand48();
    currb = drand48();
    fSetPixel(res, 0, 0, currr, currg, currb);

    currr = drand48();
    currg = drand48();
    currb = drand48();
    fSetPixel(res, w2 - 1, 0, currr, currg, currb);

    currr = drand48();
    currg = drand48();
    currb = drand48();
    fSetPixel(res, w2 - 1, h2 - 1, currr, currg, currb);

    currr = drand48();
    currg = drand48();
    currb = drand48();
    fSetPixel(res, 0, h2 - 1, currr, currg, currb);

    int ptr = 0;
    while (ptr > -1) {
      int dimx = f[ptr][2] - f[ptr][0];
      int dimy = f[ptr][3] - f[ptr][1];
      if ((dimx > 1) || (dimy > 1)) {
        double rndf = (dimx * dimy) * dimf;
        int x1 = f[ptr][0];
        int x3 = f[ptr][2];
        int x2 = x1 + ((x3 - x1) >> 1);
        int y1 = f[ptr][1];
        int y3 = f[ptr][3];
        int y2 = y1 + ((y3 - y1) >> 1);

        fGetPixel(res, x1, y1, pixel);
        double c1r = pixel.r;
        double c1g = pixel.g;
        double c1b = pixel.b;
        fGetPixel(res, x3, y1, pixel);
        double c3r = pixel.r;
        double c3g = pixel.g;
        double c3b = pixel.b;
        fGetPixel(res, x3, y3, pixel);
        double c9r = pixel.r;
        double c9g = pixel.g;
        double c9b = pixel.b;
        fGetPixel(res, x1, y3, pixel);
        double c7r = pixel.r;
        double c7g = pixel.g;
        double c7b = pixel.b;

        double c5r = (c1r + c3r + c7r + c9r) * f14 + (rndf * (0.5 - drand48()));
        if (c5r > 1.0)
          c5r = 1.0;
        if (c5r < 0.0)
          c5r = 0.0;
        double c5g = (c1g + c3g + c7g + c9g) * f14 + (rndf * (0.5 - drand48()));
        if (c5g > 1.0)
          c5g = 1.0;
        if (c5g < 0.0)
          c5g = 0.0;
        double c5b = (c1b + c3b + c7b + c9b) * f14 + (rndf * (0.5 - drand48()));
        if (c5b > 1.0)
          c5b = 1.0;
        if (c5b < 0.0)
          c5b = 0.0;

        fSetPixel(res, x2, y2, c5r, c5g, c5b);

        fGetPixel(res, x2, y1, pixel);
        currr = pixel.r;
        currb = pixel.g;
        currg = pixel.b;

        if ((currr == 0.0) && (currg == 0.0) && (currb == 0.0)) {
          currr = (c1r + c3r + c5r) * f13;
          currg = (c1g + c3g + c5g) * f13;
          currb = (c1b + c3b + c5b) * f13;
          fSetPixel(res, x2, y1, currr, currg, currb);
        }

        fGetPixel(res, x1, y2, pixel);
        currr = pixel.r;
        currg = pixel.g;
        currb = pixel.b;
        if ((currr == 0.0) && (currg == 0.0) && (currb == 0.0)) {
          currr = (c1r + c7r + c5r) * f13;
          currg = (c1g + c7g + c5g) * f13;
          currb = (c1b + c7b + c5b) * f13;
          fSetPixel(res, x1, y2, currr, currg, currb);
        }

        fGetPixel(res, x3, y2, pixel);
        currr = pixel.r;
        currg = pixel.g;
        currb = pixel.b;
        if ((currr == 0.0) && (currg == 0.0) && (currb == 0.0)) {
          currr = (c3r + c9r + c5r) * f13;
          currg = (c3g + c9g + c5g) * f13;
          currb = (c3b + c9b + c5b) * f13;
          fSetPixel(res, x3, y2, currr, currg, currb);
        }

        fGetPixel(res, x2, y3, pixel);
        currr = pixel.r;
        currg = pixel.g;
        currb = pixel.b;
        if ((currr == 0.0) && (currg == 0.0) && (currb == 0.0)) {
          currr = (c7r + c9r + c5r) * f13;
          currg = (c7g + c9g + c5g) * f13;
          currb = (c7b + c9b + c5b) * f13;
          fSetPixel(res, x2, y3, currr, currg, currb);
        }
        f[ptr][2] = x2;
        f[ptr][3] = y2;
        ptr++;
        f[ptr][0] = x2;
        f[ptr][1] = y1;
        f[ptr][2] = x3;
        f[ptr][3] = y2;
        ptr++;
        f[ptr][0] = x2;
        f[ptr][1] = y2;
        f[ptr][2] = x3;
        f[ptr][3] = y3;
        ptr++;
        f[ptr][0] = x1;
        f[ptr][1] = y2;
        f[ptr][2] = x2;
        f[ptr][3] = y3;
        if (ptr > 250) {
          /*    printf("stack overflow\n");*/
          ptr = -1;
        }
      }
      else
        ptr--;
    }
  }

  private static class PixelColor {
    public double r;
    public double g;
    public double b;
  }

  private void fSetPixel(SimpleImage pImg, int pX, int pY, double pR, double pG, double pB) {
    int r = (int) (255.0 * pR + 0.5);
    int g = (int) (255.0 * pG + 0.5);
    int b = (int) (255.0 * pB + 0.5);
    pImg.setRGB(pX, pY, r, g, b);
  }

  Pixel toolPixel = new Pixel();

  private void fGetPixel(SimpleImage pImg, int pX, int pY, PixelColor pPixelColor) {
    int argb = pImg.getARGBValue(pX, pY);
    toolPixel.setARGBValue(argb);
    pPixelColor.r = (float) toolPixel.r / 255.0;
    pPixelColor.g = (float) toolPixel.g / 255.0;
    pPixelColor.b = (float) toolPixel.b / 255.0;
  }

  private int a123 = 1;

  private final int RAND_MAX123 = 0x7fffffff;

  private int rand123() {
    return (a123 = a123 * 1103515245 + 12345) % RAND_MAX123;
  }

  private void srand123(int pSeed) {
    a123 = pSeed;
  }

  private double rrmax = 1.0 / (double) RAND_MAX123;

  private double drand48() {
    double res = ((double) (rand123() * rrmax));
    return (res < 0) ? 0.0 - res : res;
  }

  public double getDimension() {
    return dimension;
  }

  public void setDimension(double dimension) {
    this.dimension = dimension;
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

}
