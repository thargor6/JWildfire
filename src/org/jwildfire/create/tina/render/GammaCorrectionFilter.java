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
package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.pow;

import org.jwildfire.create.tina.base.Flame;

public class GammaCorrectionFilter {
  private final Flame flame;
  private int vibInt;
  private int inverseVibInt;
  private double vibDouble;
  private double inverseVibDouble;
  private double gamma;
  private double sclGamma;
  private int bgRed, bgGreen, bgBlue;
  private boolean withAlpha;

  private float bgRedDouble, bgGreenDouble, bgBlueDouble;

  public GammaCorrectionFilter(Flame pFlame, boolean pWithAlpha) {
    flame = pFlame;
    withAlpha = pWithAlpha;
    initFilter();
  }

  private void initFilter() {
    gamma = (flame.getGamma() == 0.0) ? flame.getGamma() : 1.0 / flame.getGamma();

    vibInt = (int) (flame.getVibrancy() * 256.0 + 0.5);
    if (vibInt < 0) {
      vibInt = 0;
    }
    else if (vibInt > 256) {
      vibInt = 256;
    }
    inverseVibInt = 256 - vibInt;

    vibDouble = flame.getVibrancy();
    if (vibDouble < 0.0) {
      vibDouble = 0.0;
    }
    else if (vibDouble > 1.0) {
      vibDouble = 1.0;
    }

    sclGamma = 0.0;
    if (flame.getGammaThreshold() != 0) {
      sclGamma = pow(flame.getGammaThreshold(), gamma - 1);
    }

    bgRed = flame.getBGColorRed();
    bgGreen = flame.getBGColorGreen();
    bgBlue = flame.getBGColorBlue();

    bgRedDouble = bgRed / 255.0f;
    bgGreenDouble = bgGreen / 255.0f;
    bgBlueDouble = bgBlue / 255.0f;
  }

  public void transformPoint(LogDensityPoint logDensityPnt, GammaCorrectedRGBPoint pRGBPoint) {
    double logScl;
    int inverseAlphaInt;
    if (logDensityPnt.intensity > 0.0) {
      // gamma linearization
      double alpha;
      if (logDensityPnt.intensity <= flame.getGammaThreshold()) {
        double frac = logDensityPnt.intensity / flame.getGammaThreshold();
        alpha = (1.0 - frac) * logDensityPnt.intensity * sclGamma + frac * pow(logDensityPnt.intensity, gamma);
      }
      else {
        alpha = pow(logDensityPnt.intensity, gamma);
      }
      logScl = vibInt * alpha / logDensityPnt.intensity;
      int alphaInt = (int) (alpha * 256 + 0.5);
      if (alphaInt < 0)
        alphaInt = 0;
      else if (alphaInt > 255)
        alphaInt = 255;
      inverseAlphaInt = 255 - alphaInt;
      pRGBPoint.alpha = withAlpha ? alphaInt : 255;
    }
    else {
      pRGBPoint.red = bgRed;
      pRGBPoint.green = bgGreen;
      pRGBPoint.blue = bgBlue;
      pRGBPoint.alpha = withAlpha ? 0 : 255;
      return;
    }

    int red, green, blue;
    if (inverseVibInt > 0) {
      red = (int) (logScl * logDensityPnt.red + inverseVibInt * pow(logDensityPnt.red, gamma) + 0.5);
      green = (int) (logScl * logDensityPnt.green + inverseVibInt * pow(logDensityPnt.green, gamma) + 0.5);
      blue = (int) (logScl * logDensityPnt.blue + inverseVibInt * pow(logDensityPnt.blue, gamma) + 0.5);
    }
    else {
      red = (int) (logScl * logDensityPnt.red + 0.5);
      green = (int) (logScl * logDensityPnt.green + 0.5);
      blue = (int) (logScl * logDensityPnt.blue + 0.5);
    }

    red = red + ((inverseAlphaInt * bgRed) >> 8);
    if (red < 0)
      red = 0;
    else if (red > 255)
      red = 255;

    green = green + ((inverseAlphaInt * bgGreen) >> 8);
    if (green < 0)
      green = 0;
    else if (green > 255)
      green = 255;

    blue = blue + ((inverseAlphaInt * bgBlue) >> 8);
    if (blue < 0)
      blue = 0;
    else if (blue > 255)
      blue = 255;

    pRGBPoint.red = red;
    pRGBPoint.green = green;
    pRGBPoint.blue = blue;
  }

  public void transformPointHDR(LogDensityPoint logDensityPnt, GammaCorrectedHDRPoint pHDRPoint) {
    double logScl;
    double inverseAlpha;
    if (logDensityPnt.intensity > 0.0) {
      // gamma linearization
      double alpha;
      if (logDensityPnt.intensity <= flame.getGammaThreshold()) {
        double frac = logDensityPnt.intensity / flame.getGammaThreshold();
        alpha = (1.0 - frac) * logDensityPnt.intensity * sclGamma + frac * pow(logDensityPnt.intensity, gamma);
      }
      else {
        alpha = pow(logDensityPnt.intensity, gamma);
      }
      logScl = vibDouble * alpha / logDensityPnt.intensity;

      if (alpha < 0)
        alpha = 0;
      else if (alpha > 1.0)
        alpha = 1.0;
      inverseAlpha = (1.0 - alpha);
    }
    else {
      pHDRPoint.red = (float) -1.0;
      pHDRPoint.green = (float) -1.0;
      pHDRPoint.blue = (float) -1.0;
      return;
    }

    double red, green, blue;
    if (inverseVibDouble > 0.0) {
      red = logScl * logDensityPnt.red + inverseVibDouble * pow(logDensityPnt.red, gamma);
      green = logScl * logDensityPnt.green + inverseVibDouble * pow(logDensityPnt.green, gamma);
      blue = logScl * logDensityPnt.blue + inverseVibDouble * pow(logDensityPnt.blue, gamma);
    }
    else {
      red = logScl * logDensityPnt.red;
      green = logScl * logDensityPnt.green;
      blue = logScl * logDensityPnt.blue;
    }
    red += inverseAlpha * bgRedDouble;
    green += inverseAlpha * bgGreenDouble;
    blue += inverseAlpha * bgBlueDouble;

    pHDRPoint.red = (float) red;
    pHDRPoint.green = (float) green;
    pHDRPoint.blue = (float) blue;
  }

  public void transformPointSimple(LogDensityPoint logDensityPnt, GammaCorrectedRGBPoint pRGBPoint) {
    double logScl;
    int inverseAlphaInt;
    if (logDensityPnt.intensity > 0.0) {
      // gamma linearization
      double alpha = pow(logDensityPnt.intensity, gamma);
      logScl = vibInt * alpha / logDensityPnt.intensity;
      int alphaInt = (int) (alpha * 256 + 0.5);
      if (alphaInt < 0)
        alphaInt = 0;
      else if (alphaInt > 255)
        alphaInt = 255;
      inverseAlphaInt = 255 - alphaInt;
      pRGBPoint.alpha = withAlpha ? alphaInt : 255;
    }
    else {
      pRGBPoint.alpha = withAlpha ? 0 : 255;
      pRGBPoint.red = bgRed;
      pRGBPoint.green = bgGreen;
      pRGBPoint.blue = bgBlue;
      return;
    }

    int red, green, blue;
    red = (int) (logScl * logDensityPnt.red + 0.5);
    green = (int) (logScl * logDensityPnt.green + 0.5);
    blue = (int) (logScl * logDensityPnt.blue + 0.5);

    red = red + ((inverseAlphaInt * bgRed) >> 8);
    if (red < 0)
      red = 0;
    else if (red > 255)
      red = 255;

    green = green + ((inverseAlphaInt * bgGreen) >> 8);
    if (green < 0)
      green = 0;
    else if (green > 255)
      green = 255;

    blue = blue + ((inverseAlphaInt * bgBlue) >> 8);
    if (blue < 0)
      blue = 0;
    else if (blue > 255)
      blue = 255;

    pRGBPoint.red = red;
    pRGBPoint.green = green;
    pRGBPoint.blue = blue;
  }

  public float getBgRedDouble() {
    return bgRedDouble;
  }

  public float getBgGreenDouble() {
    return bgGreenDouble;
  }

  public float getBgBlueDouble() {
    return bgBlueDouble;
  }
}
