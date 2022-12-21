/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2022 Andreas Maschke

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
package org.jwildfire.create.tina.render;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;

public class LogScaleCalculator {
  private static final int PRECALC_LOG_ARRAY_SIZE = 512;

  private final int oversample;
  private final double motionBlurScl, whiteLevel;
  private final double k1, k2;
  private final double bg_glow, balanceRed, balanceGreen, balanceBlue;

  public LogScaleCalculator(Flame pFlame, int pImageWidth, int pImageHeight, int pOversample) {
    this.oversample = pOversample;
    this.whiteLevel = pFlame.getWhiteLevel();
    final double brightness = pFlame.getBrightness();
    double _k1 = pFlame.getContrast() * 2.0 * brightness / (double) (oversample);
    switch (pFlame.getPostSymmetryType()) {
      case POINT:
        _k1 /= (double) pFlame.getPostSymmetryOrder();
        break;
      case X_AXIS:
      case Y_AXIS:
        _k1 /= 2.0;
        break;
      default: // nothing to do
        break;
    }
    double pixelsPerUnit = pFlame.getPixelsPerUnit() * pFlame.getCamZoom() * pFlame.getPixelsPerUnitScale();
    double area = ((double) pImageWidth * (double) pImageHeight) / (pixelsPerUnit * pixelsPerUnit);
    this.k1 = _k1;
    this.k2 = /* brightness */ 1.0 / (pFlame.getContrast() * area * pFlame.getSampleDensity());
    this.motionBlurScl = pFlame.getMotionBlurLength() <= 0 ? 1.0 : 1.0 / (pFlame.getMotionBlurLength() + 1.0);

    bg_glow = pFlame.getLowDensityBrightness() * k2 * area / (double) (oversample);
    balanceRed = pFlame.getBalanceRed();
    balanceGreen = pFlame.getBalanceGreen();
    balanceBlue = pFlame.getBalanceBlue();
  }

  public double[] precalcLogArray() {
    double precalcLogArray[] = new double[PRECALC_LOG_ARRAY_SIZE + 1];
    for (int i = 0; i <= PRECALC_LOG_ARRAY_SIZE; i++) {
      double x = i * motionBlurScl;
      precalcLogArray[i] = (k1 * MathLib.log10(1.0 + x * k2) + bg_glow / (x + 1.0)) / (whiteLevel * x);
    }
    return precalcLogArray;
  }

  public double getK1() {
    return k1;
  }

  public double getK2() {
    return k2;
  }

  public double calcLogScale(long pCount) {
    double x = pCount * motionBlurScl;
    return (k1 * MathLib.log10(1.0 + x * k2) + bg_glow / (x + 1.0)) / (whiteLevel * x);
  }

  public double getBalanceRed() {
    return balanceRed;
  }

  public double getBalanceGreen() {
    return balanceGreen;
  }

  public double getBalanceBlue() {
    return balanceBlue;
  }

}
