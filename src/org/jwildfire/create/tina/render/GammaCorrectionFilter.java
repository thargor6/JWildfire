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
package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.pow;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;

public class GammaCorrectionFilter {
  private final HSLRGBConverter hslrgbConverter = new HSLRGBConverter();
  private final Flame flame;
  private int vibInt;
  private int inverseVibInt;
  private double vibDouble;
  private double inverseVibDouble;
  private double gamma;
  private double sclGamma;
  private int bgRed, bgGreen, bgBlue;
  private boolean withAlpha;
  private double modSaturation;

  public static class ColorF {
    public double r, g, b;
  }

  public static class ColorI {
    public int r, g, b;
  }

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
    if (flame.getGammaThreshold() != 0.0) {
      sclGamma = pow(flame.getGammaThreshold(), gamma - 1);
    }

    bgRed = flame.getBGColorRed();
    bgGreen = flame.getBGColorGreen();
    bgBlue = flame.getBGColorBlue();

    modSaturation = flame.getSaturation() - 1.0;
    if (modSaturation < -1.0)
      modSaturation = -1.0;
  }

  public void transformPoint(LogDensityPoint logDensityPnt, GammaCorrectedRGBPoint pRGBPoint) {
    double logScl;
    int inverseAlphaInt;
    if (logDensityPnt.intensity > 0.0) {
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

      ColorF transfColor = applyLogScale(logDensityPnt, logScl);
      ColorI finalColor = addBackground(transfColor, inverseAlphaInt);

      pRGBPoint.red = finalColor.r;
      pRGBPoint.green = finalColor.g;
      pRGBPoint.blue = finalColor.b;
    }
    else {
      pRGBPoint.red = bgRed;
      pRGBPoint.green = bgGreen;
      pRGBPoint.blue = bgBlue;
      pRGBPoint.alpha = withAlpha ? 0 : 255;
    }

    if (modSaturation != 0) {
      applyModSaturation(pRGBPoint, modSaturation);
    }
  }

  private ColorI addBackground(ColorF pTransfColor, int pInverseAlphaInt) {
    ColorI res = new ColorI();

    res.r = (int) (pTransfColor.r + 0.5) + ((pInverseAlphaInt * bgRed) >> 8);
    if (res.r < 0)
      res.r = 0;
    else if (res.r > 255)
      res.r = 255;

    res.g = (int) (pTransfColor.g + 0.5) + ((pInverseAlphaInt * bgGreen) >> 8);
    if (res.g < 0)
      res.g = 0;
    else if (res.g > 255)
      res.g = 255;

    res.b = (int) (pTransfColor.b + 0.5) + ((pInverseAlphaInt * bgBlue) >> 8);
    if (res.b < 0)
      res.b = 0;
    else if (res.b > 255)
      res.b = 255;
    return res;
  }

  private ColorF applyLogScale(LogDensityPoint pLogDensityPnt, double pLogScl) {
    ColorF res = new ColorF();
    double rawRed, rawGreen, rawBlue;
    if (inverseVibInt > 0) {
      rawRed = pLogScl * pLogDensityPnt.red + inverseVibInt * pow(pLogDensityPnt.red, gamma);
      rawGreen = pLogScl * pLogDensityPnt.green + inverseVibInt * pow(pLogDensityPnt.green, gamma);
      rawBlue = pLogScl * pLogDensityPnt.blue + inverseVibInt * pow(pLogDensityPnt.blue, gamma);
    }
    else {
      rawRed = pLogScl * pLogDensityPnt.red;
      rawGreen = pLogScl * pLogDensityPnt.green;
      rawBlue = pLogScl * pLogDensityPnt.blue;
    }
    res.r = colorRedFunc(rawRed, rawGreen, rawBlue);
    res.g = colorGreenFunc(rawRed, rawGreen, rawBlue);
    res.b = colorBlueFunc(rawRed, rawGreen, rawBlue);
    return res;
  }

  private double colorRedFunc(double pRed, double pGreen, double pBlue) {
    // TODO Auto-generated method stub
    return pRed;
  }

  private double colorGreenFunc(double pRed, double pGreen, double pBlue) {
    // TODO Auto-generated method stub
    return pGreen;
  }

  private double colorBlueFunc(double pRed, double pGreen, double pBlue) {
    // TODO Auto-generated method stub
    return pBlue;
  }

  private void applyModSaturation(GammaCorrectedRGBPoint pRGBPoint, double currModSaturation) {
    hslrgbConverter.fromRgb(pRGBPoint.red / COLORSCL, pRGBPoint.green / COLORSCL, pRGBPoint.blue / COLORSCL);
    hslrgbConverter.fromHsl(hslrgbConverter.getHue(), hslrgbConverter.getSaturation() + currModSaturation, hslrgbConverter.getLuminosity());
    pRGBPoint.red = Tools.roundColor(hslrgbConverter.getRed() * COLORSCL);
    pRGBPoint.green = Tools.roundColor(hslrgbConverter.getGreen() * COLORSCL);
    pRGBPoint.blue = Tools.roundColor(hslrgbConverter.getBlue() * COLORSCL);
  }

  private static final double COLORSCL = 255.0;

  public void transformPointHDR(LogDensityPoint logDensityPnt, GammaCorrectedHDRPoint pHDRPoint) {
    double logScl;
    if (logDensityPnt.intensity > 0.0) {
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
      pHDRPoint.red = (float) red;
      pHDRPoint.green = (float) green;
      pHDRPoint.blue = (float) blue;
    }
    else {
      pHDRPoint.red = (float) -1.0;
      pHDRPoint.green = (float) -1.0;
      pHDRPoint.blue = (float) -1.0;
    }
  }

  public static class HSLRGBConverter {
    private double red, green, blue;
    private double hue, saturation, luminosity;

    public void fromHsl(double pHue, double pSaturation, double pLuminosity) {
      hue = limitVal(pHue, 0.0, 1.0);
      saturation = limitVal(pSaturation, 0.0, 1.0);
      luminosity = limitVal(pLuminosity, 0.0, 1.0);
      double v = (luminosity <= 0.5) ? (luminosity * (1.0 + saturation))
          : (luminosity + saturation - luminosity * saturation);
      if (v <= 0) {
        red = 0.0;
        green = 0.0;
        blue = 0.0;
        return;
      }
      hue *= 6.0;
      if (hue < 0.0)
        hue = 0.0;
      else if (hue > 6.0)
        hue = 6.0;
      double y = luminosity + luminosity - v;
      double x = y + (v - y) * (hue - (int) hue);
      double z = v - (v - y) * (hue - (int) hue);

      switch ((int) hue) {
        case 0:
          red = v;
          green = x;
          blue = y;
          break;
        case 1:
          red = z;
          green = v;
          blue = y;
          break;
        case 2:
          red = y;
          green = v;
          blue = x;
          break;
        case 3:
          red = y;
          green = z;
          blue = v;
          break;
        case 4:
          red = x;
          green = y;
          blue = v;
          break;
        case 5:
          red = v;
          green = y;
          blue = z;
          break;
        default:
          red = v;
          green = x;
          blue = y;
          //          red = v;
          //          green = y;
          //          blue = z;
      }
    }

    public void fromRgb(double pRed, double pGreen, double pBlue) {
      hue = 1.0;
      saturation = 0.0;
      red = limitVal(pRed, 0.0, 1.0);
      green = limitVal(pGreen, 0.0, 1.0);
      blue = limitVal(pBlue, 0.0, 1.0);
      double max = Math.max(red, Math.max(green, blue));
      double min = Math.min(red, Math.min(green, blue));
      luminosity = (min + max) / 2.0;
      if (Math.abs(luminosity) <= MathLib.EPSILON)
        return;
      saturation = max - min;

      if (Math.abs(saturation) <= MathLib.EPSILON)
        return;

      saturation /= ((luminosity) <= 0.5) ? (min + max) : (2.0 - max - min);
      if (Math.abs(red - max) < MathLib.EPSILON) {
        hue = ((green == min) ? 5.0 + (max - blue) / (max - min) : 1.0 - (max - green) / (max - min));
      }
      else {
        if (Math.abs(green - max) < MathLib.EPSILON) {
          hue = ((blue == min) ? 1.0 + (max - red) / (max - min) : 3.0 - (max - blue) / (max - min));
        }
        else {
          hue = ((red == min) ? 3.0 + (max - green) / (max - min) : 5.0 - (max - red) / (max - min));
        }
      }
      hue /= 6.0;
    }

    private double limitVal(double pValue, double pMin, double pMax) {
      return pValue < pMin ? pMin : pValue > pMax ? pMax : pValue;
    }

    public double getRed() {
      return red;
    }

    public double getGreen() {
      return green;
    }

    public double getBlue() {
      return blue;
    }

    public double getHue() {
      return hue;
    }

    public double getSaturation() {
      return saturation;
    }

    public double getLuminosity() {
      return luminosity;
    }
  }

}
