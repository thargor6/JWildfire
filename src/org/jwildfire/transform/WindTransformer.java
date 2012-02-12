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
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class WindTransformer extends Mesh2DTransformer {
  public enum Direction {
    LEFT, RIGHT
  }

  @Property(description = "Probability of the effect")
  @PropertyMin(0)
  @PropertyMax(100)
  private int probability = 20;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed = 123;

  @Property(description = "Intensity of the effect")
  private int intensity = 40;

  @Property(description = "Wind direction", editorClass = DirectionEditor.class)
  private Direction direction = Direction.RIGHT;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    int contrast = 0;
    short smask[][] = new short[height][width];
    int rs = 2990;
    int gs = 5880;
    int bs = 1130;
    rs = (rs * Tools.VPREC) / 10000;
    gs = (gs * Tools.VPREC) / 10000;
    bs = (bs * Tools.VPREC) / 10000;
    Pixel p0 = new Pixel();
    Pixel p1 = new Pixel();
    Tools.srand123(this.seed);

    /* flip the image */
    if (direction == Direction.LEFT) {
      srcImg = srcImg.clone();
      FlipTransformer ft = new FlipTransformer();
      ft.setAxis(FlipTransformer.Axis.X);
      ft.transformImage(srcImg);
      ft.setAxis(FlipTransformer.Axis.X);
      ft.transformImage(img);
    }

    /** 1st line **/
    for (int i = 0; i < width; i++)
      smask[0][i] = 1;
    /** lines 2...(height-2) **/
    for (int i = 1; i < (height - 1); i++) {
      /* 1st pixel */
      smask[i][0] = 1;
      /* process pixels 2..width-1 */
      for (int j = 1; j < (width - 1); j++) {
        int dd = 0, int1;
        p1.setARGBValue(srcImg.getARGBValue(j - 1, i - 1));
        int1 = (rs * p1.r + gs * p1.g + bs * p1.b) >> Tools.SPREC;
        dd += int1;

        p1.setARGBValue(srcImg.getARGBValue(j + 1, i - 1));
        int1 = (rs * p1.r + gs * p1.g + bs * p1.b) >> Tools.SPREC;
        dd -= int1;

        p1.setARGBValue(srcImg.getARGBValue(j - 1, i));
        int1 = (rs * p1.r + gs * p1.g + bs * p1.b) >> Tools.SPREC;
        dd += int1;

        p1.setARGBValue(srcImg.getARGBValue(j + 1, i));
        int1 = (rs * p1.r + gs * p1.g + bs * p1.b) >> Tools.SPREC;
        dd -= int1;

        p1.setARGBValue(srcImg.getARGBValue(j - 1, i + 1));
        int1 = (rs * p1.r + gs * p1.g + bs * p1.b) >> Tools.SPREC;
        dd += int1;

        p1.setARGBValue(srcImg.getARGBValue(j + 1, i + 1));
        int1 = (rs * p1.r + gs * p1.g + bs * p1.b) >> Tools.SPREC;
        dd -= int1;

        if (dd < contrast) {
          smask[i][j] = 1;
        }
      }
      /* last pixel */
      smask[i][width - 1] = 1;
    }
    /** last line **/
    for (int i = 0; i < width; i++)
      smask[height - 1][i] = 1;

    /** accept only "thick" lines **/
    for (int i = 1; i < (height - 1); i++) {
      for (int j = 1; j < (width - 1); j++) {
        if (smask[i][j] == 0) {
          if (smask[i][j + 1] != 0)
            smask[i][j] = 1;
        }
      }
    }

    /** create "thin" lines **/
    for (int i = 1; i < (height - 1); i++) {
      nextLine: for (int j = (width - 2); j >= 1; j--) {
        if (smask[i][j] == 0) {
          int k = j - 1;
          while (smask[i][k] == 0) {
            smask[i][k] = 1;
            k--;
            if (k < 1)
              continue nextLine;
          }
        }
      }
    }

    /** remove isolated pixels **/
    for (int i = 1; i < (height - 1); i++) {
      for (int j = 1; j < (width - 1); j++) {
        if (smask[i][j] == 0) {
          int dd = 0;
          dd += smask[i][j - 1];
          dd += smask[i][j + 1];
          dd += smask[i - 1][j];
          dd += smask[i + 1][j];
          dd += smask[i + 1][j - 1];
          dd += smask[i + 1][j + 1];
          dd += smask[i - 1][j - 1];
          dd += smask[i - 1][j + 1];
          if (dd == 8)
            smask[i][j] = 1;
        }
      }
    }

    /** apply the changes **/
    int intensity0 = this.intensity;
    int intensity1 = intensity0 / 2;

    {
      double rprob = (double) ((double) 1.0 - (double) this.probability / (double) 100.0);
      for (int i = 0; i < height; i++) {
        cont: for (int j = (width - 1); j >= 0; j--) {
          if ((smask[i][j] == 0) && (Tools.drand() >= rprob)) {
            if (j < 1) {
              continue cont;
            }
            int intensity = intensity0 + (int) (Tools.drand() * intensity1 + 0.5);
            double dm = 100.0 / (double) (intensity - 1);
            int kmax = intensity;
            if ((j + kmax) >= width)
              kmax = width - j;
            for (int k = 0; k < kmax; k++) {
              int mix = (int) (100.0 - (double) k * dm + 0.5);
              int m1 = 100 - mix;
              int m2 = mix;
              p0.setARGBValue(srcImg.getARGBValue(j, i));
              p1.setARGBValue(srcImg.getARGBValue(j + k, i));
              int rv = ((int) ((int) p1.r * m1) + (int) p0.r * m2) / (int) 100;
              int gv = ((int) ((int) p1.g * m1) + (int) p0.g * m2) / (int) 100;
              int bv = ((int) ((int) p1.b * m1) + (int) p0.b * m2) / (int) 100;
              img.setRGB(j + k, i, rv, gv, bv);
            }
          }
        }
      }
    }

    /* flip the image */
    if (direction == Direction.LEFT) {
      FlipTransformer ft = new FlipTransformer();
      ft.setAxis(FlipTransformer.Axis.X);
      ft.transformImage(img);
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    direction = Direction.RIGHT;
    intensity = 40;
    probability = 20;
    seed = 123;
  }

  public static class DirectionEditor extends ComboBoxPropertyEditor {
    public DirectionEditor() {
      super();
      setAvailableValues(new Direction[] { Direction.LEFT, Direction.RIGHT });
    }
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

  public int getIntensity() {
    return intensity;
  }

  public void setIntensity(int intensity) {
    this.intensity = intensity;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

}
