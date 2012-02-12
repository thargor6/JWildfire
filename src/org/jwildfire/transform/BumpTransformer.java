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
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;

public class BumpTransformer extends Mesh2DTransformer {
  @Property(category = PropertyCategory.PRIMARY, description = "Image which holds the height information", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer heightMap;
  @Property(category = PropertyCategory.SECONDARY, description = "Left offset of the height map")
  private int left;
  @Property(category = PropertyCategory.SECONDARY, description = "Top offset of the height map")
  private int top;
  @Property(category = PropertyCategory.PRIMARY, description = "Strength of the effect")
  private double amount;
  @Property(category = PropertyCategory.SECONDARY, description = "x-coordinate of the light source")
  private int lightX;
  @Property(category = PropertyCategory.SECONDARY, description = "y-coordinate of the light source")
  private int lightY;
  @Property(category = PropertyCategory.SECONDARY, description = "z-coordinate of the light source")
  private int lightZ;
  @Property(category = PropertyCategory.SECONDARY, description = "intensity of the light")
  private double intensity;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int intensity = (int) (this.intensity * (double) VPREC + 0.5);
    int amount = (int) (this.amount * (double) VPREC + 0.5);

    int left = this.left;
    int top = this.top;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Buffer heightMapBuffer = this.heightMap;
    SimpleImage heightMap = heightMapBuffer.getImage();

    int bwidth = heightMap.getImageWidth();
    int bheight = heightMap.getImageHeight();

    /* compute the light-vector */
    int plCosA, lvx, lvy, lvz;
    {
      double lx = 0.0 - (double) lightX;
      double ly = 0.0 - (double) lightY;
      double lz = (double) 0.0 - (double) lightZ;

      double r = Math.sqrt(lx * lx + ly * ly + lz * lz);
      if (r != (float) 0.0) {
        lx /= r;
        ly /= r;
        lz /= r;
      }
      else {
        lx = (double) 0.0;
        ly = (double) 0.0;
        lz = (double) 1.0;
      }

      lvx = (int) (lx * (double) VPREC + 0.5);
      lvy = (int) (ly * (double) VPREC + 0.5);
      lvz = (int) (lz * (double) VPREC + 0.5);

      int nz = 0 - VPREC;
      int nx = 0;
      int ny = 0;
      plCosA = ((lvx * nx + VPREC2) >> SPREC) + ((lvy * ny + VPREC2) >> SPREC)
          + ((lvz * nz + VPREC2) >> SPREC);
      plCosA = (plCosA * intensity + VPREC2) >> SPREC;
      if (plCosA < 0)
        plCosA = 0 - plCosA;
      else
        plCosA = 0;
    }

    int x1 = -VPREC;
    int y1 = -VPREC;
    int x2 = VPREC;
    int y2 = -VPREC;
    int x3 = -VPREC;
    int y3 = VPREC;
    int x4 = VPREC;
    int y4 = VPREC;
    int x5 = 0;
    int y5 = 0;

    int a1x = x5 - x1;
    int a1y = y5 - y1;
    int b1x = x5 - x2;
    int b1y = y5 - y2;

    int a2x = x5 - x2;
    int a2y = y5 - y2;
    int b2x = x5 - x4;
    int b2y = y5 - y4;

    int a3x = x5 - x4;
    int a3y = y5 - y4;
    int b3x = x5 - x3;
    int b3y = y5 - y3;

    int a4x = x5 - x3;
    int a4y = y5 - y3;
    int b4x = x5 - x1;
    int b4y = y5 - y1;

    Pixel sPixel = new Pixel();
    Pixel hPixel = new Pixel();
    Pixel hLUPixel = new Pixel();
    Pixel hRUPixel = new Pixel();
    Pixel hLBPixel = new Pixel();
    Pixel hRBPixel = new Pixel();

    for (int i = 0; i < height; i++) {
      int biy = i - top;
      for (int j = 0; j < width; j++) {
        int bix = j - left;
        int z1, z2, z3, z4, z5;
        if ((bix >= 0) && (bix < bwidth) && (biy >= 0) && (biy < bheight)) {
          hPixel.setARGBValue(heightMap.getARGBValue(bix, biy));

          hLUPixel.setARGBValue(heightMap.getARGBValueIgnoreBounds(bix - 1, biy - 1));
          hRUPixel.setARGBValue(heightMap.getARGBValueIgnoreBounds(bix + 1, biy - 1));

          hLBPixel.setARGBValue(heightMap.getARGBValueIgnoreBounds(bix - 1, biy + 1));
          hRBPixel.setARGBValue(heightMap.getARGBValueIgnoreBounds(bix + 1, biy + 1));

          z5 = 0 - ((int) (hPixel.r) << SPREC) / 255;
          if (biy > 0) {
            if (bix > 0)
              z1 = 0 - ((int) (hLUPixel.r) << SPREC) / 255;
            else
              z1 = 0;
            if (bix < (width - 2))
              z2 = 0 - ((int) (hRUPixel.r) << SPREC) / 255;
            else
              z2 = 0;
          }
          else {
            z1 = z2 = 0;
          }
          if (biy < (bheight - 1)) {
            if (bix > 0)
              z3 = 0 - ((int) (hLBPixel.r) << SPREC) / 255;
            else
              z3 = 0;
            if (bix < (width - 1))
              z4 = 0 - ((int) (hRBPixel.r) << SPREC) / 255;
            else
              z4 = 0;
          }
          else {
            z3 = z4 = 0;
          }
        }
        else {
          z1 = z2 = z3 = z4 = z5 = 0;
        }

        z1 = (z1 * amount + VPREC2) >> SPREC;
        z2 = (z2 * amount + VPREC2) >> SPREC;
        z3 = (z3 * amount + VPREC2) >> SPREC;
        z4 = (z4 * amount + VPREC2) >> SPREC;
        z5 = (z5 * amount + VPREC2) >> SPREC;

        int a1z = z5 - z1;
        int b1z = z5 - z2;

        int a2z = z5 - z2;
        int b2z = z5 - z4;

        int a3z = z5 - z4;
        int b3z = z5 - z3;

        int a4z = z5 - z3;
        int b4z = z5 - z1;

        /* triangle 1 */
        int nx = (((a1y + VPREC2) >> SPREC) * b1z) - (((a1z + VPREC2) >> SPREC) * b1y);
        int ny = (((a1z + VPREC2) >> SPREC) * b1x) - (((a1x + VPREC2) >> SPREC) * b1z);
        int nz = (((a1x + VPREC2) >> SPREC) * b1y) - (((a1y + VPREC2) >> SPREC) * b1x);
        int dr = (((nx + VPREC2) >> SPREC) * nx) + (((ny + VPREC2) >> SPREC) * ny)
            + (((nz + VPREC2) >> SPREC) * nz);
        /*   #ifdef PPC
           ff=sqrt((float)dr);dr=(int)(ff*32.0+0.5);
           #else
           dr=wfm->iSqrt(dr);
           #endif*/
        dr = iSqrt(dr);
        if (dr != 0) {
          nx = ((nx << SPREC) + VPREC2) / dr;
          ny = ((ny << SPREC) + VPREC2) / dr;
          nz = ((nz << SPREC) + VPREC2) / dr;
        }
        else {
          nz = -VPREC;
          nx = ny = 0;
        }
        int lCosA = ((lvx * nx + VPREC2) >> SPREC) + ((lvy * ny + VPREC2) >> SPREC)
            + ((lvz * nz + VPREC2) >> SPREC);
        lCosA = (lCosA * intensity + VPREC2) >> SPREC;
        if (lCosA < 0)
          lCosA = 0;
        int lc1 = lCosA;
        /* triangle 2 */
        nx = (((a2y + VPREC2) >> SPREC) * b2z) - (((a2z + VPREC2) >> SPREC) * b2y);
        ny = (((a2z + VPREC2) >> SPREC) * b2x) - (((a2x + VPREC2) >> SPREC) * b2z);
        nz = (((a2x + VPREC2) >> SPREC) * b2y) - (((a2y + VPREC2) >> SPREC) * b2x);

        dr = (((nx + VPREC2) >> SPREC) * nx) + (((ny + VPREC2) >> SPREC) * ny)
            + (((nz + VPREC2) >> SPREC) * nz);
        /*   #ifdef PPC
           ff=sqrt((float)dr);dr=(int)(ff*32.0+0.5);
           #else
           dr=wfm->iSqrt(dr);
           #endif     */
        dr = iSqrt(dr);
        if (dr != 0) {
          nx = ((nx << SPREC) + VPREC2) / dr;
          ny = ((ny << SPREC) + VPREC2) / dr;
          nz = ((nz << SPREC) + VPREC2) / dr;
        }
        else {
          nz = -VPREC;
          nx = ny = 0;
        }
        lCosA = ((lvx * nx + VPREC2) >> SPREC) + ((lvy * ny + VPREC2) >> SPREC)
            + ((lvz * nz + VPREC2) >> SPREC);
        lCosA = (lCosA * intensity + VPREC2) >> SPREC;
        if (lCosA < 0)
          lCosA = 0;
        int lc2 = lCosA;
        /* triangle 3 */
        nx = (((a3y + VPREC2) >> SPREC) * b3z) - (((a3z + VPREC2) >> SPREC) * b3y);
        ny = (((a3z + VPREC2) >> SPREC) * b3x) - (((a3x + VPREC2) >> SPREC) * b3z);
        nz = (((a3x + VPREC2) >> SPREC) * b3y) - (((a3y + VPREC2) >> SPREC) * b3x);

        dr = (((nx + VPREC2) >> SPREC) * nx) + (((ny + VPREC2) >> SPREC) * ny)
            + (((nz + VPREC2) >> SPREC) * nz);
        /*   #ifdef PPC
           ff=sqrt((float)dr);dr=(int)(ff*32.0+0.5);
           #else
           dr=wfm->iSqrt(dr);
           #endif*/
        dr = iSqrt(dr);
        if (dr != 0) {
          nx = ((nx << SPREC) + VPREC2) / dr;
          ny = ((ny << SPREC) + VPREC2) / dr;
          nz = ((nz << SPREC) + VPREC2) / dr;
        }
        else {
          nz = -VPREC;
          nx = ny = 0;
        }
        lCosA = ((lvx * nx + VPREC2) >> SPREC) + ((lvy * ny + VPREC2) >> SPREC)
            + ((lvz * nz + VPREC2) >> SPREC);
        lCosA = (lCosA * intensity + VPREC2) >> SPREC;
        if (lCosA < 0)
          lCosA = 0;
        int lc3 = lCosA;
        /* triangle 4 */
        nx = (((a4y + VPREC2) >> SPREC) * b4z) - (((a4z + VPREC2) >> SPREC) * b4y);
        ny = (((a4z + VPREC2) >> SPREC) * b4x) - (((a4x + VPREC2) >> SPREC) * b4z);
        nz = (((a4x + VPREC2) >> SPREC) * b4y) - (((a4y + VPREC2) >> SPREC) * b4x);

        dr = (((nx + VPREC2) >> SPREC) * nx) + (((ny + VPREC2) >> SPREC) * ny)
            + (((nz + VPREC2) >> SPREC) * nz);
        /*   #ifdef PPC
           ff=sqrt((float)dr);dr=(int)(ff*32.0+0.5);
           #else
           dr=wfm->iSqrt(dr);
           #endif*/
        dr = iSqrt(dr);
        if (dr != 0) {
          nx = ((nx << SPREC) + VPREC2) / dr;
          ny = ((ny << SPREC) + VPREC2) / dr;
          nz = ((nz << SPREC) + VPREC2) / dr;
        }
        else {
          nz = -VPREC;
          nx = ny = 0;
        }
        lCosA = ((lvx * nx + VPREC2) >> SPREC) + ((lvy * ny + VPREC2) >> SPREC)
            + ((lvz * nz + VPREC2) >> SPREC);
        lCosA = (lCosA * intensity + VPREC2) >> SPREC;
        if (lCosA < 0)
          lCosA = 0;
        int lc4 = lCosA;

        sPixel.setARGBValue(img.getARGBValue(j, i));
        int rv4, rv3, rv2, rv1;
        rv4 = rv3 = rv2 = rv1 = sPixel.r;
        int gv4, gv3, gv2, gv1;
        gv4 = gv3 = gv2 = gv1 = sPixel.g;
        int bv4, bv3, bv2, bv1;
        bv4 = bv3 = bv2 = bv1 = sPixel.b;
        rv1 = ((int) rv1 * lc1 + VPREC2) >> SPREC;
        gv1 = ((int) gv1 * lc1 + VPREC2) >> SPREC;
        bv1 = ((int) bv1 * lc1 + VPREC2) >> SPREC;
        rv2 = ((int) rv2 * lc2 + VPREC2) >> SPREC;
        gv2 = ((int) gv2 * lc2 + VPREC2) >> SPREC;
        bv2 = ((int) bv2 * lc2 + VPREC2) >> SPREC;
        rv3 = ((int) rv3 * lc3 + VPREC2) >> SPREC;
        gv3 = ((int) gv3 * lc1 + VPREC2) >> SPREC;
        bv3 = ((int) bv3 * lc3 + VPREC2) >> SPREC;
        rv4 = ((int) rv4 * lc4 + VPREC2) >> SPREC;
        gv4 = ((int) gv4 * lc2 + VPREC2) >> SPREC;
        bv4 = ((int) bv4 * lc4 + VPREC2) >> SPREC;

        int rv = (rv1 + rv2 + rv3 + rv4) >> 2;
        int gv = (gv1 + gv2 + gv3 + gv4) >> 2;
        int bv = (bv1 + bv2 + bv3 + bv4) >> 2;
        if (rv > 255)
          rv = 255;
        if (gv > 255)
          gv = 255;
        if (bv > 255)
          bv = 255;

        img.setRGB(j, i, rv, gv, bv);
      }
    }

  }

  private final static int VPREC1000 = 1024000;
  private final static int SPREC = 10;
  private final static int VPREC = 1024;
  private final static int VPREC2 = 512;

  private int iSqrt(int a) {
    int x0, x, res, pot = 1;
    while (a > VPREC1000) {
      a = a >> 6;
      pot = pot << 3;
    }
    if (a <= 0)
      return (0);
    x0 = a;
    do {
      x = (x0 + (a << SPREC) / x0) >> 1;
      res = x0 - x;
      x0 = x;
    }
    while (res > 0);
    return (x * pot);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    left = 0;
    top = 0;
    amount = 10.0;
    lightX = -200;
    lightY = 50;
    lightZ = -100;
    intensity = 2.4;
  }

  public Buffer getHeightMap() {
    return heightMap;
  }

  public void setHeightMap(Buffer heightMap) {
    this.heightMap = heightMap;
  }

  public int getLeft() {
    return left;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public int getLightX() {
    return lightX;
  }

  public void setLightX(int lightX) {
    this.lightX = lightX;
  }

  public int getLightY() {
    return lightY;
  }

  public void setLightY(int lightY) {
    this.lightY = lightY;
  }

  public int getLightZ() {
    return lightZ;
  }

  public void setLightZ(int lightZ) {
    this.lightZ = lightZ;
  }

  public double getIntensity() {
    return intensity;
  }

  public void setIntensity(double intensity) {
    this.intensity = intensity;
  }

}
