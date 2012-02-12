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

public class DisplaceTransformer extends Mesh2DTransformer {

  @Property(description = "Intensity of pixel displacement")
  @PropertyMin(0)
  private int radius = 20;

  @Property(description = "Displacement probability")
  @PropertyMin(0)
  @PropertyMax(100)
  private int probability = 100;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed = 0;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    int probability = this.probability;
    int rrad = this.radius;
    srand123(seed);
    Pixel sPixel = new Pixel();
    if (probability == 100) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          sPixel.setARGBValue(img.getARGBValue(j, i));
          int sr = sPixel.r;
          int sg = sPixel.g;
          int sb = sPixel.b;
          int dx = (int) (rrad * drand() + 0.5);
          int dy = (int) (rrad * drand() + 0.5);
          int px = j + dx;
          int py = i + dy;
          if (px < 0)
            px = 0;
          else if (px >= width)
            px = width - 1;
          if (py < 0)
            py = 0;
          else if (py >= height)
            py = height - 1;
          sPixel.setARGBValue(img.getARGBValue(px, py));
          int rp = sPixel.r;
          int gp = sPixel.g;
          int bp = sPixel.b;
          img.setRGB(j, i, rp, gp, bp);
          img.setRGB(px, py, sr, sg, sb);
        }
      }
    }
    else {
      double rprob = (double) ((double) 1.0 - (double) probability / (double) 100.0);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          sPixel.setARGBValue(img.getARGBValue(j, i));
          int sr = sPixel.r;
          int sg = sPixel.g;
          int sb = sPixel.b;
          if (drand() >= rprob) {
            int dx = (int) (rrad * drand() + 0.5);
            int dy = (int) (rrad * drand() + 0.5);
            int px = j + dx;
            int py = i + dy;
            if (px < 0)
              px = 0;
            else if (px >= width)
              px = width - 1;
            if (py < 0)
              py = 0;
            else if (py >= height)
              py = height - 1;
            sPixel.setARGBValue(img.getARGBValue(px, py));
            int rp = sPixel.r;
            int gp = sPixel.g;
            int bp = sPixel.b;
            img.setRGB(j, i, rp, gp, bp);
            img.setRGB(px, py, sr, sg, sb);
          }
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    radius = 20;
    probability = 100;
    seed = 0;
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

  private double drand() {
    double res = ((double) (rand123() * rrmax));
    return (res < 0) ? 0.0 - res : res;
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public int getProbability() {
    return probability;
  }

  public void setProbability(int probability) {
    this.probability = probability;
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

}
