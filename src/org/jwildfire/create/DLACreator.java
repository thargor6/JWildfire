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

import java.awt.Color;
import java.awt.Graphics;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleImage;


public class DLACreator extends ImageCreator {

  @Property(description = "Foreground color")
  Color color = new Color(255, 0, 0);

  @Property(description = "Background color")
  Color bgColor = new Color(0, 0, 0);

  @Property(description = "Number of iterations")
  @PropertyMin(0)
  private int maxIter = 30000;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed = 0;

  @Override
  protected void fillImage(SimpleImage res) {
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    // fill the background
    {
      Graphics g = res.getBufferedImg().getGraphics();
      g.setColor(bgColor);
      g.fillRect(0, 0, width, height);
    }
    // create the object
    int cx = width / 2;
    int cy = height / 2;
    double pi2 = 2.0 * Math.PI;
    int w2 = width - 2;
    int h2 = height - 2;
    short q[][] = new short[height][width];
    Tools.srand123(this.seed);
    /* create the cluster */
    q[cy][cx] = 1;
    double r1 = 3.0;
    double r2 = 3.0 * r1;
    for (int i = 0; i < this.maxIter; i++) {
      double phi = pi2 * Tools.drand();
      double ri = r1 * Math.cos(phi);
      double rj = r1 * Math.sin(phi);
      int ci = cy + (int) (ri + 0.5);
      int cj = cx + (int) (rj + 0.5);
      short qt = 0;
      while (qt == 0) {
        double rr = Tools.drand();
        rr += rr;
        rr += rr;
        int rd = (int) rr;
        switch (rd) {
          case 0:
            ci++;
            break;
          case 1:
            cj--;
            break;
          case 2:
            ci--;
            break;
          default:
            cj++;
        }
        if ((ci < 1) || (ci > h2) || (cj < 1) || (cj > w2)) {
          qt = 1;
          i--;
        }
        else {
          int sum = q[ci - 1][cj] + q[ci + 1][cj] + q[ci][cj - 1] + q[ci][cj + 1];
          if (sum != 0) {
            q[ci][cj] = qt = 1;
            double r3 = (double) (ci - cy);
            double r4 = (double) (cj - cx);
            r3 *= r3;
            r4 *= r4;
            r3 += r4;
            r3 = Math.sqrt(r3);
            if (r3 > r1) {
              r1 = r3;
              r2 = 2.1 * r1;
            }
          }
          else {
            double r3 = (double) (ci - cy);
            double r4 = (double) (cj - cx);
            r3 *= r3;
            r4 *= r4;
            r3 += r4;
            r3 = Math.sqrt(r3);
            if (r3 > r2) {
              qt = 1;
              i--;
            }
          }
        }
      }
    }

    /* apply the cluster to the image */
    {
      int r = color.getRed();
      int g = color.getGreen();
      int b = color.getBlue();
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          if (q[i][j] != 0) {
            res.setRGB(j, i, r, g, b);
          }
        }
      }
    }

  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Color getBgColor() {
    return bgColor;
  }

  public void setBgColor(Color bgColor) {
    this.bgColor = bgColor;
  }

  public int getMaxIter() {
    return maxIter;
  }

  public void setMaxIter(int maxIter) {
    this.maxIter = maxIter;
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

}
