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

public class ShadingInfo {
  private static final int MAXLIGHTS = 4;
  private Shading shading;
  private double ambient;
  private double diffuse;
  private int lightCount;
  private double phong;
  private double phongSize;
  protected double lightPosX[] = new double[MAXLIGHTS];
  protected double lightPosY[] = new double[MAXLIGHTS];
  protected double lightPosZ[] = new double[MAXLIGHTS];
  protected int lightRed[] = new int[MAXLIGHTS];
  protected int lightGreen[] = new int[MAXLIGHTS];
  protected int lightBlue[] = new int[MAXLIGHTS];

  protected void init() {
    shading = Shading.FLAT;
    ambient = 0.06;
    diffuse = 0.94;
    phong = 0.9;
    phongSize = 30;
    lightCount = 2;
    lightPosX[0] = -0.2;
    lightPosY[0] = -0.2;
    lightPosZ[0] = -1;
    lightPosX[1] = 0.2;
    lightPosY[1] = -0.2;
    lightPosZ[1] = -1;
    lightPosX[1] = -0.2;
    lightPosY[1] = 0.2;
    lightPosZ[1] = -1;
    lightPosX[1] = 0.2;
    lightPosY[1] = 0.2;
    lightPosZ[1] = -1;
    lightRed[0] = 240;
    lightGreen[0] = 220;
    lightBlue[0] = 220;
    lightRed[1] = 160;
    lightGreen[1] = 155;
    lightBlue[1] = 150;
    lightRed[2] = 160;
    lightGreen[2] = 155;
    lightBlue[2] = 150;
    lightRed[3] = 160;
    lightGreen[3] = 155;
    lightBlue[3] = 150;
  }

  public Shading getShading() {
    return shading;
  }

  public void setShading(Shading shading) {
    this.shading = shading;
  }

  public double getAmbient() {
    return ambient;
  }

  public void setAmbient(double ambient) {
    this.ambient = ambient;
  }

  public double getDiffuse() {
    return diffuse;
  }

  public void setDiffuse(double diffuse) {
    this.diffuse = diffuse;
  }

  public int getLightCount() {
    return lightCount;
  }

  public void setLightCount(int lightCount) {
    this.lightCount = lightCount;
  }

  public double getPhong() {
    return phong;
  }

  public void setPhong(double phong) {
    this.phong = phong;
  }

  public double getPhongSize() {
    return phongSize;
  }

  public void setPhongSize(double phongSize) {
    this.phongSize = phongSize;
  }

  public double[] getLightPosX() {
    return lightPosX;
  }

  public void setLightPosX(double[] lightPosX) {
    this.lightPosX = lightPosX;
  }

  public double[] getLightPosY() {
    return lightPosY;
  }

  public void setLightPosY(double[] lightPosY) {
    this.lightPosY = lightPosY;
  }

  public double[] getLightPosZ() {
    return lightPosZ;
  }

  public void setLightPosZ(double[] lightPosZ) {
    this.lightPosZ = lightPosZ;
  }

  public int[] getLightRed() {
    return lightRed;
  }

  public void setLightRed(int[] lightRed) {
    this.lightRed = lightRed;
  }

  public int[] getLightGreen() {
    return lightGreen;
  }

  public void setLightGreen(int[] lightGreen) {
    this.lightGreen = lightGreen;
  }

  public int[] getLightBlue() {
    return lightBlue;
  }

  public void setLightBlue(int[] lightBlue) {
    this.lightBlue = lightBlue;
  }
}
