/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.mathlib.MathLib;

import java.io.Serializable;

public class BaseFlame implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final double DLTF_SIZE_SCALE = 0.01;
  private final double intensity;
  private final double r, g, b;
  private final double brightness;
  private final double size;
  private final transient IFlamesIterator iterator;
  private final int previewColorR, previewColorG, previewColorB;
  private final DynamicProperties motionProperties;

  public BaseFlame(double pIntensity, double pR, double pG, double pB, double pBrightness, ImageParams pImageParams, FlameParams pParams, IFlamesIterator pIterator, DynamicProperties pMotionProperties) {
    intensity = pIntensity;
    r = pR;
    g = pG;
    b = pB;
    brightness = pBrightness;
    iterator = pIterator;
    motionProperties = pMotionProperties;
    previewColorR = pParams.getPreviewR();
    previewColorG = pParams.getPreviewG();
    previewColorB = pParams.getPreviewB();

    double baseSize = pParams.getSize();
    double sizeVar = pParams.getSizeVar() >= 0.0 ? pParams.getSizeVar() / 100.0 : 0.0;
    size = DLTF_SIZE_SCALE * (sizeVar > MathLib.EPSILON ? baseSize * (1.0 + sizeVar * (0.5 - pImageParams.getRandGen().random())) : baseSize);
  }

  public IFlamesIterator getIterator() {
    return iterator;
  }

  public double getIntensity() {
    return intensity;
  }

  public double getSize() {
    return size;
  }

  public double getR() {
    return r;
  }

  public double getG() {
    return g;
  }

  public double getB() {
    return b;
  }

  public DynamicProperties getMotionProperties() {
    return motionProperties;
  }

  public int getPreviewColorR() {
    return previewColorR;
  }

  public int getPreviewColorG() {
    return previewColorG;
  }

  public int getPreviewColorB() {
    return previewColorB;
  }

  public double getBrightness() {
    return brightness;
  }

}
