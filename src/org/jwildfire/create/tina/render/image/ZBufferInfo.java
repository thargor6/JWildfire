/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.render.image;

public class ZBufferInfo {
  private final double zMin, zMax;
  private final double zBorderMin, zBorderMax;
  private final double coverage;
  private final int grayBorderMinValue, grayBorderMaxValue;
  private final double suggestedZScale, suggestedZShift, suggestedBias;


  public ZBufferInfo(double zMin, double zMax, double zBorderMin, double zBorderMax, int grayBorderMinValue, int grayBorderMaxValue, double coverage, double suggestedZScale, double suggestedZShift, double suggestedBias) {
    this.zMin = zMin;
    this.zMax = zMax;
    this.zBorderMin = zBorderMin;
    this.zBorderMax = zBorderMax;
    this.coverage = coverage;
    this.grayBorderMinValue = grayBorderMinValue;
    this.grayBorderMaxValue = grayBorderMaxValue;
    this.suggestedZScale = suggestedZScale;
    this.suggestedZShift = suggestedZShift;
    this.suggestedBias = suggestedBias;
  }

  public double getzMin() {
    return zMin;
  }

  public double getzMax() {
    return zMax;
  }

  public double getzBorderMin() {
    return zBorderMin;
  }

  public double getzBorderMax() {
    return zBorderMax;
  }

  public int getGrayBorderMinValue() {
    return grayBorderMinValue;
  }

  public int getGrayBorderMaxValue() {
    return grayBorderMaxValue;
  }

  public double getSuggestedZScale() {
    return suggestedZScale;
  }

  public double getSuggestedZShift() {
    return suggestedZShift;
  }

  public double getCoverage() {
    return coverage;
  }

  public double getSuggestedBias() {
    return suggestedBias;
  }
}
