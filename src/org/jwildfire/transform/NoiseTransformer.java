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
import org.jwildfire.image.WFImage;

public class NoiseTransformer extends PixelTransformer {

  @Property(description = "Intensity of the effect")
  @PropertyMin(0)
  @PropertyMax(500)
  private int intensity = 20;

  @Property(description = "Noise probability")
  @PropertyMin(0)
  @PropertyMax(100)
  private int probability = 60;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed = 0;

  @Override
  protected void initTransformation(WFImage pImg) {
    super.initTransformation(pImg);
    srand123(seed);
  }

  @Override
  protected void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    double rprob = (double) ((double) 1.0 - (double) probability / (double) 100.0);
    int intensity = this.intensity;

    if (this.probability != 100) {
      int sr = pPixel.r;
      int sg = pPixel.g;
      int sb = pPixel.b;
      if (drand() >= rprob) {
        double am = (int) (drand() * intensity + 0.5);

        if (drand() > 0.5) {
          sr += am;
          if (sr > 255)
            sr = 255;
          sg += am;
          if (sg > 255)
            sg = 255;
          sb += am;
          if (sb > 255)
            sb = 255;
        }
        else {
          sr -= am;
          if (sr < 0)
            sr = 0;
          sg -= am;
          if (sg < 0)
            sg = 0;
          sb -= am;
          if (sb < 0)
            sb = 0;
        }
        pPixel.r = sr;
        pPixel.g = sg;
        pPixel.b = sb;
      }
    }
    else {
      int sr = pPixel.r;
      int sg = pPixel.g;
      int sb = pPixel.b;
      int am = (int) (drand() * intensity + 0.5);
      if (drand() > 0.5) {
        sr += am;
        if (sr > 255)
          sr = 255;
        sg += am;
        if (sg > 255)
          sg = 255;
        sb += am;
        if (sb > 255)
          sb = 255;
      }
      else {
        sr -= am;
        if (sr < 0)
          sr = 0;
        sg -= am;
        if (sg < 0)
          sg = 0;
        sb -= am;
        if (sb < 0)
          sb = 0;
      }
      pPixel.r = sr;
      pPixel.g = sg;
      pPixel.b = sb;
    }
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

  @Override
  public void initDefaultParams(WFImage pImg) {
    intensity = 20;
    probability = 60;
    seed = 0;
  }

  public int getIntensity() {
    return intensity;
  }

  public void setIntensity(int intensity) {
    this.intensity = intensity;
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
