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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.SimpleImage;


public class Mesh3D implements Cloneable {
  private double x[];
  private double y[];
  private double z[];
  private int pCount;

  private int pp1[];
  private int pp2[];
  private int pp3[];
  private int color[];
  private int fCount;

  private SimpleImage texture;
  private double u[], v[];

  private int imageWidth;
  private int imageHeight;

  // TODO change this (circular reference) all the needed parameters are known:
  private Mesh3DTransformer lastTransformation;

  public double[] getX() {
    return x;
  }

  public double[] getY() {
    return y;
  }

  public double[] getZ() {
    return z;
  }

  public int getPCount() {
    return pCount;
  }

  public int getFCount() {
    return fCount;
  }

  public void readPixels(SimpleImage pImg, double pQuant) {
    this.imageWidth = pImg.getImageWidth();
    this.imageHeight = pImg.getImageHeight();
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    int width2, height2;
    if (pQuant < 0.1)
      pQuant = 1.0;
    width2 = (int) ((double) width / pQuant + 0.5);
    height2 = (int) ((double) height / pQuant + 0.5);
    if ((width2 == width) && (height2 == height)) {
      pCount = (int) (width + 1) * (height + 1);
      fCount = (int) 2 * (int) width * (int) height;
      x = new double[pCount];
      y = new double[pCount];
      z = new double[pCount];

      pp1 = new int[fCount];
      pp2 = new int[fCount];
      pp3 = new int[fCount];

      if (Tools.USE_TEXTURES) {
        texture = pImg.clone();
        u = new double[pCount];
        v = new double[pCount];
        double du = 1.0 / (double) (width + 1);
        double dv = 1.0 / (double) (height + 1);

        color = null;
        int curr = 0;
        double currV = 0.0;
        for (int i = 0; i <= height; i++) {
          double currU = 0.0;
          for (int j = 0; j <= width; j++) {
            u[curr] = currU;
            v[curr++] = currV;
            currU += du;
          }
          currV += dv;
        }
      }
      else {
        u = v = null;
        texture = null;
        color = new int[fCount];
        int curr = 0;
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int argb = pImg.getARGBValue(j, i);
            /* two faces with same color */
            color[curr++] = argb;
            color[curr++] = argb;
          }
        }
      }
      /* create the points */
      double cx = (double) (width) / 2.0;
      double cy = (double) (height) / 2.0;
      double xmin0 = 0.0 - cx;
      double ymin0 = 0.0 - cy;
      double ymin = ymin0;
      int curr = 0;
      for (int i = 0; i <= height; i++) {
        double xmin = xmin0;
        for (int j = 0; j <= width; j++) {
          x[curr] = xmin;
          xmin += 1.0;
          y[curr] = ymin;
          z[curr++] = 0.0;
        }
        ymin += 1.0;
      }
      /* create the faces */
      curr = 0;
      for (int i = 0; i < height; i++) {
        int p0 = i * (width + 1);
        int p1 = (i + 1) * (width + 1);
        for (int j = 0; j < width; j++) {
          pp3[curr] = p0;
          pp2[curr] = p0 + 1;
          pp1[curr++] = p1 + 1;

          pp3[curr] = p0++;
          pp2[curr] = p1 + 1;
          pp1[curr++] = p1++;
        }
      }
    }
    else {
      //
      pCount = (int) (width2 + 1) * (height2 + 1);
      fCount = (int) 2 * (int) width2 * (int) height2;
      color = new int[fCount];

      // copy the colors
      if (Tools.USE_TEXTURES) {
        texture = pImg.clone();
        u = new double[pCount];
        v = new double[pCount];
        color = null;
        double du = 1.0 / (double) (width2 + 1);
        double dv = 1.0 / (double) (height2 + 1);
        int curr = 0;
        double currV = 0.0;
        for (int i = 0; i <= height2; i++) {
          double currU = 0.0;
          for (int j = 0; j <= width2; j++) {
            u[curr] = currU;
            v[curr++] = currV;
            currU += du;
          }
          currV += dv;
        }
      }
      else {
        u = v = null;
        texture = null;
        // Scale the image        
        SimpleImage sImg = pImg.clone();
        ScaleTransformer scaleT = new ScaleTransformer();
        scaleT.setAspect(ScaleAspect.IGNORE);
        scaleT.setUnit(ScaleTransformer.Unit.PIXELS);
        scaleT.setScaleWidth(width2);
        scaleT.setScaleHeight(height2);
        scaleT.performImageTransformation(sImg);
        int curr = 0;
        for (int i = 0; i < height2; i++) {
          for (int j = 0; j < width2; j++) {
            int argb = sImg.getARGBValue(j, i);
            /* two faces with same color */
            color[curr++] = argb;
            color[curr++] = argb;
          }
        }
        sImg = null;
      }
      //
      x = new double[pCount];
      y = new double[pCount];
      z = new double[pCount];

      pp1 = new int[fCount];
      pp2 = new int[fCount];
      pp3 = new int[fCount];

      // create the points 
      double dx = (double) width / (double) width2;
      double dy = (double) height / (double) height2;
      double cx = (double) (width) / 2.0;
      double cy = (double) (height) / 2.0;
      double xmin0 = 0.0 - cx;
      double ymin0 = 0.0 - cy;
      double ymin = ymin0;
      int curr = 0;
      for (int i = 0; i <= height2; i++) {
        double xmin = xmin0;
        for (int j = 0; j <= width2; j++) {
          x[curr] = xmin;
          xmin += dx;
          y[curr] = ymin;
          z[curr++] = 0.0;
        }
        ymin += dy;
      }

      /* create the faces */
      curr = 0;
      for (int i = 0; i < height2; i++) {
        int p0 = (int) i * (int) (width2 + 1);
        int p1 = (int) (i + 1) * (int) (width2 + 1);
        for (int j = 0; j < width2; j++) {
          pp3[curr] = p0;
          pp2[curr] = p0 + 1;
          pp1[curr++] = p1 + 1;

          pp3[curr] = p0++;
          pp2[curr] = p1 + 1;
          pp1[curr++] = p1++;
        }
      }
    }
  }

  public int[] getPP1() {
    return pp1;
  }

  public int[] getPP2() {
    return pp2;
  }

  public int[] getPP3() {
    return pp3;
  }

  public int[] getColor() {
    return color;
  }

  public void setReducedFCount(int pFCount) {
    if (pFCount > fCount)
      throw new IllegalArgumentException();
    fCount = pFCount;
  }

  @Override
  public Mesh3D clone() {
    Mesh3D res = new Mesh3D();
    res.x = new double[x.length];
    System.arraycopy(x, 0, res.x, 0, x.length);
    res.y = new double[y.length];
    System.arraycopy(y, 0, res.y, 0, y.length);
    res.z = new double[z.length];
    System.arraycopy(z, 0, res.z, 0, z.length);

    res.pp1 = new int[pp1.length];
    System.arraycopy(pp1, 0, res.pp1, 0, pp1.length);
    res.pp2 = new int[pp2.length];
    System.arraycopy(pp2, 0, res.pp2, 0, pp2.length);
    res.pp3 = new int[pp3.length];
    System.arraycopy(pp3, 0, res.pp3, 0, pp3.length);

    if (color != null && color.length > 0) {
      res.color = new int[color.length];
      System.arraycopy(color, 0, res.color, 0, color.length);
    }
    else {
      res.color = null;
    }
    if (u != null && u.length > 0) {
      res.u = new double[u.length];
      System.arraycopy(u, 0, res.u, 0, u.length);
    }
    else {
      res.u = null;
    }
    if (v != null && v.length > 0) {
      res.v = new double[v.length];
      System.arraycopy(v, 0, res.v, 0, v.length);
    }
    else {
      res.v = null;
    }
    if (texture != null) {
      res.texture = texture.clone();
    }
    else {
      res.texture = null;
    }

    res.pCount = pCount;
    res.fCount = fCount;
    res.imageWidth = imageWidth;
    res.imageHeight = imageHeight;

    return res;
  }

  public Mesh3D clone(double pScale) {
    Mesh3D res = clone();
    if (Math.abs(pScale - 1.0) > MathLib.EPSILON) {
      for (int i = 0; i < pCount; i++) {
        res.x[i] *= pScale;
        res.y[i] *= pScale;
        res.z[i] *= pScale;
      }
      res.imageWidth = (int) ((double) res.imageWidth * pScale + 0.5);
      res.imageHeight = (int) ((double) res.imageHeight * pScale + 0.5);
    }
    return res;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public void setFCount(int pFCount) {
    fCount = pFCount;

  }

  public void setPP1(int[] pPP1) {
    pp1 = pPP1;
  }

  public void setPP2(int[] pPP2) {
    pp2 = pPP2;
  }

  public void setPP3(int[] pPP3) {
    pp3 = pPP3;
  }

  public void setColor(int[] pColor) {
    color = pColor;
  }

  public void setPCount(int pPCount) {
    pCount = pPCount;
  }

  public void setX(double[] pX) {
    x = pX;
  }

  public void setY(double[] pY) {
    y = pY;
  }

  public void setZ(double[] pZ) {
    z = pZ;
  }

  public void setImageWidth(int imageWidth) {
    this.imageWidth = imageWidth;
  }

  public void setImageHeight(int imageHeight) {
    this.imageHeight = imageHeight;
  }

  public Mesh3DTransformer getLastTransformation() {
    return lastTransformation;
  }

  public void setLastTransformation(Mesh3DTransformer lastTransformation) {
    this.lastTransformation = lastTransformation;
  }

  public SimpleImage getTexture() {
    return texture;
  }

  public void setTexture(SimpleImage texture) {
    this.texture = texture;
  }

  public double[] getU() {
    return u;
  }

  public void setU(double[] u) {
    this.u = u;
  }

  public double[] getV() {
    return v;
  }

  public void setV(double[] v) {
    this.v = v;
  }

}
