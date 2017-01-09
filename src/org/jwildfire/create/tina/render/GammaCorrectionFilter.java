/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;

public class GammaCorrectionFilter {
  private final Flame flame;
  private int vibInt;
  private int inverseVibInt;
  private double gamma;
  private double sclGamma;
  private boolean withAlpha;
  private double modSaturation;
  private final double alphaScale;

  public static class ColorF {
    public double r, g, b;
  }

  public static class ColorI {
    public int r, g, b;
  }

  public GammaCorrectionFilter(Flame pFlame, boolean pWithAlpha, int pRasterWidth, int pRasterHeight) {
    flame = pFlame;
    withAlpha = pWithAlpha;
    alphaScale = 1.0 - MathLib.atan(3.0 * (pFlame.getForegroundOpacity() - 1.0)) / 1.25;
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

    sclGamma = 0.0;
    if (flame.getGammaThreshold() != 0.0) {
      sclGamma = pow(flame.getGammaThreshold(), gamma - 1);
    }

    modSaturation = flame.getSaturation() - 1.0;
    if (modSaturation < -1.0)
      modSaturation = -1.0;
  }

  public void transformPoint(LogDensityPoint logDensityPnt, GammaCorrectedRGBPoint pRGBPoint, int pX, int pY) {
    pRGBPoint.bgRed = logDensityPnt.bgRed;
    pRGBPoint.bgGreen = logDensityPnt.bgGreen;
    pRGBPoint.bgBlue = logDensityPnt.bgBlue;
    double logScl;
    int inverseAlphaInt;
    if (logDensityPnt.intensity > 0.0 || logDensityPnt.hasSolidColors || logDensityPnt.receiveOnlyShadows) {
      ColorF transfColor;
      if (logDensityPnt.hasSolidColors) {
        transfColor = new ColorF();
        transfColor.r = logDensityPnt.solidRed;
        transfColor.g = logDensityPnt.solidGreen;
        transfColor.b = logDensityPnt.solidBlue;

        double fixedGamma = 1.0 + gamma;
        double alpha = pow(logDensityPnt.intensity, fixedGamma);
        int alphaInt = (int) (alpha * 255 * alphaScale + 0.5);
        if (alphaInt < 0)
          alphaInt = 0;
        else if (alphaInt > 255)
          alphaInt = 255;
        inverseAlphaInt = 255 - alphaInt;
        pRGBPoint.alpha = withAlpha ? alphaInt : 255;

        ColorI finalColor = addBackground(pRGBPoint, transfColor, inverseAlphaInt);
        pRGBPoint.red = finalColor.r;
        pRGBPoint.green = finalColor.g;
        pRGBPoint.blue = finalColor.b;
      }
      else if (logDensityPnt.receiveOnlyShadows) {
        transfColor = new ColorF();
        double shadowInt = GfxMathLib.clamp(logDensityPnt.red);
        if (shadowInt > MathLib.EPSILON && shadowInt < 1.0 - MathLib.EPSILON) {
          int alphaInt = Tools.roundColor((1.0 - shadowInt) * 255.0);
          inverseAlphaInt = 255 - alphaInt;

          transfColor.r = 0.0;
          transfColor.g = 0.0;
          transfColor.b = 0.0;
          pRGBPoint.alpha = withAlpha ? alphaInt : 255;

          ColorI finalColor = addBackground(pRGBPoint, transfColor, inverseAlphaInt);
          pRGBPoint.red = finalColor.r;
          pRGBPoint.green = finalColor.g;
          pRGBPoint.blue = finalColor.b;
        }
        else {
          pRGBPoint.red = pRGBPoint.bgRed;
          pRGBPoint.green = pRGBPoint.bgGreen;
          pRGBPoint.blue = pRGBPoint.bgBlue;
          pRGBPoint.alpha = withAlpha ? 0 : 255;
        }
      }
      else {
        double alpha;
        if (logDensityPnt.intensity <= flame.getGammaThreshold()) {
          double frac = logDensityPnt.intensity / flame.getGammaThreshold();
          alpha = (1.0 - frac) * logDensityPnt.intensity * sclGamma + frac * pow(logDensityPnt.intensity, gamma);
        }
        else {
          alpha = pow(logDensityPnt.intensity, gamma);
        }
        logScl = vibInt * alpha / logDensityPnt.intensity;
        int alphaInt = (int) (alpha * 255 * alphaScale + 0.5);
        if (alphaInt < 0)
          alphaInt = 0;
        else if (alphaInt > 255)
          alphaInt = 255;
        inverseAlphaInt = 255 - alphaInt;
        pRGBPoint.alpha = withAlpha ? alphaInt : 255;

        transfColor = applyLogScale(logDensityPnt, logScl);

        ColorI finalColor = addBackground(pRGBPoint, transfColor, inverseAlphaInt);
        pRGBPoint.red = finalColor.r;
        pRGBPoint.green = finalColor.g;
        pRGBPoint.blue = finalColor.b;
      }
    }
    else {
      pRGBPoint.red = pRGBPoint.bgRed;
      pRGBPoint.green = pRGBPoint.bgGreen;
      pRGBPoint.blue = pRGBPoint.bgBlue;
      pRGBPoint.alpha = withAlpha ? 0 : 255;
    }

    if (modSaturation != 0) {
      applyModSaturation(pRGBPoint, modSaturation);
    }
  }

  private ColorI addBackground(GammaCorrectedRGBPoint pRGBPoint, ColorF pTransfColor, int pInverseAlphaInt) {
    ColorI res = new ColorI();

    res.r = (int) (pTransfColor.r + 0.5) + ((pInverseAlphaInt * pRGBPoint.bgRed) >> 8);
    if (res.r < 0)
      res.r = 0;
    else if (res.r > 255)
      res.r = 255;

    res.g = (int) (pTransfColor.g + 0.5) + ((pInverseAlphaInt * pRGBPoint.bgGreen) >> 8);
    if (res.g < 0)
      res.g = 0;
    else if (res.g > 255)
      res.g = 255;

    res.b = (int) (pTransfColor.b + 0.5) + ((pInverseAlphaInt * pRGBPoint.bgBlue) >> 8);
    if (res.b < 0)
      res.b = 0;
    else if (res.b > 255)
      res.b = 255;
    return res;
  }

  final static double ALPHA_RANGE = 256.0;

  private ColorF addBackgroundF(PointWithBackgroundColor pBGColor, ColorF pTransfColor, double pInverseAlphaInt) {
    ColorF res = new ColorF();

    res.r = pTransfColor.r + (pInverseAlphaInt * pBGColor.bgRed) / ALPHA_RANGE;
    if (res.r < 0.0)
      res.r = 0.0;

    res.g = pTransfColor.g + (pInverseAlphaInt * pBGColor.bgGreen) / ALPHA_RANGE;
    if (res.g < 0.0)
      res.g = 0.0;

    res.b = pTransfColor.b + (pInverseAlphaInt * pBGColor.bgBlue) / ALPHA_RANGE;
    if (res.b < 0.0)
      res.b = 0.0;
    return res;
  }

  private ColorF applyLogScale(LogDensityPoint pLogDensityPnt, double pLogScl) {
    ColorF res = new ColorF();
    double rawRed = 0.0, rawGreen = 0.0, rawBlue = 0.0;

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
    res.r = rawRed;
    res.g = rawGreen;
    res.b = rawBlue;

    return res;
  }

  private void applyModSaturation(GammaCorrectedRGBPoint pRGBPoint, double currModSaturation) {
    HSLRGBConverter hslrgbConverter = pRGBPoint.hslrgbConverter;
    hslrgbConverter.fromRgb(pRGBPoint.red / COLORSCL, pRGBPoint.green / COLORSCL, pRGBPoint.blue / COLORSCL);
    hslrgbConverter.fromHsl(hslrgbConverter.getHue(), hslrgbConverter.getSaturation() + currModSaturation, hslrgbConverter.getLuminosity());
    pRGBPoint.red = Tools.roundColor(hslrgbConverter.getRed() * COLORSCL);
    pRGBPoint.green = Tools.roundColor(hslrgbConverter.getGreen() * COLORSCL);
    pRGBPoint.blue = Tools.roundColor(hslrgbConverter.getBlue() * COLORSCL);
  }

  public static final double COLORSCL = 255.0;

  public void transformPointHDR(LogDensityPoint logDensityPnt, GammaCorrectedHDRPoint pHDRPoint, int pX, int pY) {
    pHDRPoint.bgRed = logDensityPnt.bgRed;
    pHDRPoint.bgGreen = logDensityPnt.bgGreen;
    pHDRPoint.bgBlue = logDensityPnt.bgBlue;
    double logScl;
    double inverseAlphaInt;

    if (logDensityPnt.intensity > 0.0 || logDensityPnt.hasSolidColors) {
      ColorF transfColor;
      if (logDensityPnt.hasSolidColors) {
        transfColor = new ColorF();
        transfColor.r = logDensityPnt.solidRed;
        transfColor.g = logDensityPnt.solidGreen;
        transfColor.b = logDensityPnt.solidBlue;

        double fixedGamma = 1.0 + gamma;
        double alpha = pow(logDensityPnt.intensity, fixedGamma);
        double alphaInt = (alpha * ALPHA_RANGE * alphaScale);
        if (alphaInt < 0.0)
          alphaInt = 0.0;
        else if (alphaInt > ALPHA_RANGE)
          alphaInt = ALPHA_RANGE;
        inverseAlphaInt = ALPHA_RANGE - alphaInt;
      }
      else {
        double alpha;
        if (logDensityPnt.intensity <= flame.getGammaThreshold()) {
          double frac = logDensityPnt.intensity / flame.getGammaThreshold();
          alpha = (1.0 - frac) * logDensityPnt.intensity * sclGamma + frac * pow(logDensityPnt.intensity, gamma);
        }
        else {
          alpha = pow(logDensityPnt.intensity, gamma);
        }

        logScl = vibInt * alpha / logDensityPnt.intensity;
        double alphaInt = alpha * ALPHA_RANGE;
        if (alphaInt < 0.0)
          alphaInt = 0.0;
        else if (alphaInt > ALPHA_RANGE)
          alphaInt = ALPHA_RANGE;
        inverseAlphaInt = ALPHA_RANGE - alphaInt;

        transfColor = applyLogScale(logDensityPnt, logScl);
      }
      ColorF finalColor = addBackgroundF(pHDRPoint, transfColor, inverseAlphaInt);

      pHDRPoint.red = (float) (finalColor.r / ALPHA_RANGE);
      pHDRPoint.green = (float) (finalColor.g / ALPHA_RANGE);
      pHDRPoint.blue = (float) (finalColor.b / ALPHA_RANGE);
    }
    else {
      pHDRPoint.red = (float) (pHDRPoint.bgRed / ALPHA_RANGE);
      pHDRPoint.green = (float) (pHDRPoint.bgGreen / ALPHA_RANGE);
      pHDRPoint.blue = (float) (pHDRPoint.bgBlue / ALPHA_RANGE);
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
