/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019 Andreas Maschke

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
package org.jwildfire.create.tina.palette;

import java.io.Serializable;
import java.awt.Color;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.edit.Assignable;

public class RGBColor implements Assignable<RGBColor>, Serializable /*, Comparable<RGBColor>*/{
  private static final long serialVersionUID = 1L;
  private int red;
  private int green;
  private int blue;

  public RGBColor(int pRed, int pGreen, int pBlue) {
    red = pRed;
    green = pGreen;
    blue = pBlue;
  }

  public RGBColor() {
  }

  public int getRed() {
    return red;
  }

  public void setRed(int red) {
    this.red = red;
  }

  public int getGreen() {
    return green;
  }

  public void setGreen(int green) {
    this.green = green;
  }

  public int getBlue() {
    return blue;
  }

  public void setBlue(int blue) {
    this.blue = blue;
  }
  
  public Color getColor() {
    Color color = new Color(red, green, blue);
    return color;
  }
  
  public void setColor(Color color) {
    this.red = color.getRed();
    this.green = color.getGreen();
    this.blue = color.getBlue();
  }
  
  @Override
  public RGBColor makeCopy() {
    RGBColor res = new RGBColor();
    res.assign(this);
    return res;
  }

  @Override
  public void assign(RGBColor pRGBColor) {
    red = pRGBColor.red;
    green = pRGBColor.green;
    blue = pRGBColor.blue;
  }

  @Override
  public boolean isEqual(RGBColor pSrc) {
    if (red != pSrc.red || green != pSrc.green || blue != pSrc.blue) {
      return false;
    }
    return true;
  }

  private static class HSV {
    public double h;
    @SuppressWarnings("unused")
    public double s;
    public double v;
  }

  protected HSV toHSV() {
    double r = red / 255.0;
    double g = green / 255.0;
    double b = blue / 255.0;
    HSV hsv = new HSV();

    double min = Math.min(Math.min(r, g), b);
    double max = Math.max(Math.max(r, g), b);
    double delta = max - min;

    hsv.v = max;
    if (Math.abs(delta) <= MathLib.EPSILON) {
      hsv.h = 0;
      hsv.s = 0;
    }
    else {
      hsv.s = delta / max;

      double dR = (((max - r) / 6.0) + (delta / 2.0)) / delta;
      double dG = (((max - g) / 6.0) + (delta / 2.0)) / delta;
      double dB = (((max - b) / 6.0) + (delta / 2.0)) / delta;

      if (MathLib.fabs(r - max) < MathLib.EPSILON) {
        hsv.h = dB - dG;
      }
      else if (MathLib.fabs(g - max) < MathLib.EPSILON) {
        hsv.h = (1.0 / 3.0) + dR - dB;
      }
      else {
        hsv.h = (2.0 / 3.0) + dG - dR;
      }
      if (hsv.h < 0.0 - MathLib.EPSILON) {
        hsv.h += 1.0;
      }
      if (hsv.h > 1.0 + MathLib.EPSILON) {
        hsv.h -= 1.0;
      }
    }
    return hsv;
  }

  public int compareToRGBColor(RGBColor o) {
    HSV hsv = toHSV();
    HSV rHSV = o.toHSV();
    int h = Tools.FTOI(3.5 * hsv.h);
    int rH = Tools.FTOI(3.5 * rHSV.h);
    if (h < rH) {
      return -1;
    }
    else if (h > rH) {
      return 1;
    }
    else {
      if (hsv.v < rHSV.v) {
        return -1;
      }
      else if (hsv.v > rHSV.v) {
        return 1;
      }
      else {
        return 0;
      }
    }
  }

  @Override
  public String toString() {
    return "RGBColor [red=" + red + ", green=" + green + ", blue=" + blue + "]";
  }
}
