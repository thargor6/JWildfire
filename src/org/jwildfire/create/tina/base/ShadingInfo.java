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
package org.jwildfire.create.tina.base;

import org.jwildfire.create.tina.edit.PropertyChangeListener;

public class ShadingInfo {
  // pseudo3D
  private static final int MAXLIGHTS = 4;
  private Shading shading;
  private double ambient;
  private double diffuse;
  private double phong;
  private double phongSize;
  protected double lightPosX[] = new double[MAXLIGHTS];
  protected double lightPosY[] = new double[MAXLIGHTS];
  protected double lightPosZ[] = new double[MAXLIGHTS];
  protected int lightRed[] = new int[MAXLIGHTS];
  protected int lightGreen[] = new int[MAXLIGHTS];
  protected int lightBlue[] = new int[MAXLIGHTS];
  // blur
  private int blurRadius;
  private double blurFade;
  private double blurFallOff;
  private final Flame owner;

  public ShadingInfo(Flame owner) {
    this.owner = owner;
  }

  protected void init() {
    shading = Shading.FLAT;
    // pseudo3d
    ambient = 0.18;
    diffuse = 0.82;
    phong = 0.75;
    phongSize = 30;
    lightPosX[0] = -0.2;
    lightPosY[0] = -0.2;
    lightPosZ[0] = -4;
    lightPosX[1] = 0.2;
    lightPosY[1] = -0.2;
    lightPosZ[1] = -3;
    lightPosX[2] = -0.2;
    lightPosY[2] = 0.2;
    lightPosZ[2] = -2;
    lightPosX[3] = 0.2;
    lightPosY[3] = 0.2;
    lightPosZ[3] = -1;
    lightRed[0] = 240;
    lightGreen[0] = 220;
    lightBlue[0] = 220;
    lightRed[1] = 0;
    lightGreen[1] = 0;
    lightBlue[1] = 0;
    lightRed[2] = 0;
    lightGreen[2] = 0;
    lightBlue[2] = 0;
    lightRed[3] = 0;
    lightGreen[3] = 0;
    lightBlue[3] = 0;
    // blur
    blurRadius = 2;
    blurFade = 0.95;
    blurFallOff = 2.0;
  }

  public Shading getShading() {
    return shading;
  }

  public void setShading(Shading shading) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.shading", this.shading, shading);
    }
    this.shading = shading;
  }

  public double getAmbient() {
    return ambient;
  }

  public void setAmbient(double ambient) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.ambient", this.ambient, ambient);
    }
    this.ambient = ambient;
  }

  public double getDiffuse() {
    return diffuse;
  }

  public void setDiffuse(double diffuse) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.diffuse", this.diffuse, diffuse);
    }
    this.diffuse = diffuse;
  }

  public int getLightCount() {
    int res = 0;
    for (int i = 0; i < lightRed.length; i++) {
      if (lightRed[i] > 0 || lightGreen[i] > 0 || lightBlue[i] > 0) {
        res++;
      }
      else {
        break;
      }
    }
    return res;
  }

  public double getPhong() {
    return phong;
  }

  public void setPhong(double phong) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.phong", this.phong, phong);
    }
    this.phong = phong;
  }

  public double getPhongSize() {
    return phongSize;
  }

  public void setPhongSize(double phongSize) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.phongSize", this.phongSize, phongSize);
    }
    this.phongSize = phongSize;
  }

  public double[] getLightPosX() {
    return lightPosX;
  }

  public void setLightPosX(int pIdx, double pLightPosX) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.lightPosX." + pIdx, this.lightPosX[pIdx], pLightPosX);
    }
    this.lightPosX[pIdx] = pLightPosX;
  }

  public double[] getLightPosY() {
    return lightPosY;
  }

  public void setLightPosY(int pIdx, double pLightPosY) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.lightPosY." + pIdx, this.lightPosY[pIdx], pLightPosY);
    }
    this.lightPosY[pIdx] = pLightPosY;
  }

  public double[] getLightPosZ() {
    return lightPosZ;
  }

  public void setLightPosZ(int pIdx, double pLightPosZ) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.lightPosZ." + pIdx, this.lightPosZ[pIdx], pLightPosZ);
    }
    this.lightPosZ[pIdx] = pLightPosZ;
  }

  public int[] getLightRed() {
    return lightRed;
  }

  public void setLightRed(int pIdx, int pLightRed) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.lightRed." + pIdx, this.lightRed[pIdx], pLightRed);
    }
    this.lightRed[pIdx] = pLightRed;
  }

  public int[] getLightGreen() {
    return lightGreen;
  }

  public void setLightGreen(int pIdx, int pLightGreen) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.lightGreen." + pIdx, this.lightGreen[pIdx], pLightGreen);
    }
    this.lightGreen[pIdx] = pLightGreen;
  }

  public int[] getLightBlue() {
    return lightBlue;
  }

  public void setLightBlue(int pIdx, int pLightBlue) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.lightBlue." + pIdx, this.lightBlue[pIdx], pLightBlue);
    }
    this.lightBlue[pIdx] = pLightBlue;
  }

  public void assign(ShadingInfo pSrc) {
    shading = pSrc.shading;
    ambient = pSrc.ambient;
    diffuse = pSrc.diffuse;
    phong = pSrc.phong;
    phongSize = pSrc.phongSize;
    for (int i = 0; i < lightPosX.length; i++) {
      lightPosX[i] = pSrc.lightPosX[i];
      lightPosY[i] = pSrc.lightPosY[i];
      lightPosZ[i] = pSrc.lightPosZ[i];
      lightRed[i] = pSrc.lightRed[i];
      lightGreen[i] = pSrc.lightGreen[i];
      lightBlue[i] = pSrc.lightBlue[i];
    }
    blurRadius = pSrc.blurRadius;
    blurFade = pSrc.blurFade;
    blurFallOff = pSrc.blurFallOff;
  }

  public double[][] createBlurKernel() {
    double kernel[][];
    if (blurRadius <= 0) {
      kernel = new double[1][1];
      kernel[0][0] = 1.0;
    }
    else {
      kernel = new double[2 * blurRadius + 1][2 * blurRadius + 1];
      kernel[0][0] = 1.0;
      for (int y = -blurRadius, i = 0; y <= blurRadius; y++, i++) {
        for (int x = -blurRadius, j = 0; x <= blurRadius; x++, j++) {
          double r = Math.sqrt(x * x + y * y) / (double) (blurRadius);
          kernel[i][j] = Math.pow(2, -r * blurFallOff);
          //          System.out.println(i + " " + j + " " + " r=" + r + ", k=" + kernel[i][j]);
        }
      }
    }
    return kernel;
  }

  public int getBlurRadius() {
    return blurRadius;
  }

  public void setBlurRadius(int blurRadius) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.blurRadius", this.blurRadius, blurRadius);
    }
    this.blurRadius = blurRadius;
  }

  public double getBlurFade() {
    return blurFade;
  }

  public void setBlurFade(double blurFade) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.blurFade", this.blurFade, blurFade);
    }
    this.blurFade = blurFade;
  }

  public double getBlurFallOff() {
    return blurFallOff;
  }

  public void setBlurFallOff(double blurFallOff) {
    for (PropertyChangeListener<Flame> listener : owner.getChangeListeners()) {
      listener.propertyChanged(owner, "shading.blurFallOff", this.blurFallOff, blurFallOff);
    }
    this.blurFallOff = blurFallOff;
  }
}
