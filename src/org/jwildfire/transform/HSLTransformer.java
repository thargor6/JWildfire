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
package org.jwildfire.transform;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class HSLTransformer extends Mesh2DTransformer {

  @Property(description = "Hue correction")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int hue = 0;

  @Property(description = "Saturation correction")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int saturation = 0;

  @Property(description = "Luminosity correction")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int luminosity = 0;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel rgbPixel = new Pixel();
    HSLPixel hslPixel = new HSLPixel();

    double phue = (double) (this.hue) / 255.0;
    if (phue < (-1.0))
      phue = -1;
    else if (phue > 1.0)
      phue = 1.0;
    double psaturation = (double) (this.saturation) / 255.0;
    if (psaturation < (-1.0))
      psaturation = -1.0;
    else if (psaturation > 1.0)
      psaturation = 1.0;
    double pluminosity = (double) (this.luminosity) / 255.0;
    if (pluminosity < (-1.0))
      pluminosity = -1;
    else if (pluminosity > 1.0)
      pluminosity = 1.0;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        rgbPixel.setARGBValue(img.getARGBValue(j, i));
        rgb2hsl(rgbPixel, hslPixel);
        hslPixel.luminosity += pluminosity;
        if (hslPixel.luminosity < 0.0)
          hslPixel.luminosity = 0.0;
        else if (hslPixel.luminosity > 1.0)
          hslPixel.luminosity = 1.0;
        hslPixel.saturation += psaturation;
        if (hslPixel.saturation < 0.0)
          hslPixel.saturation = 0.0;
        else if (hslPixel.saturation > 1.0)
          hslPixel.saturation = 1.0;
        if (hslPixel.hue != (-1.0)) {
          hslPixel.hue += phue;
          if (hslPixel.hue < 0.0)
            hslPixel.hue += 1.0;
          else if (hslPixel.hue > 1.0)
            hslPixel.hue -= 1.0;
        }
        hsl2rgb(hslPixel, rgbPixel);
        img.setRGB(j, i, rgbPixel);
      }
    }
  }

  public static void hsl2rgb(HSLPixel hslPixel, Pixel rgbPixel) {
    double luminosity = hslPixel.luminosity;
    double saturation = hslPixel.saturation;
    double hue = hslPixel.hue;
    double v = (luminosity <= 0.5) ? (luminosity * (1.0 + saturation))
        : (luminosity + saturation - luminosity * saturation);
    if (v <= 0) {
      rgbPixel.r = 0;
      rgbPixel.g = 0;
      rgbPixel.b = 0;
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

    double r, g, b;
    //    double h = hue;
    //    if ((h >= 0.0) && (h < 1.0)) {
    //      r = (1.0 - h) * v + h * z;
    //      g = (1.0 - h) * x + h * v;
    //      b = (1.0 - h) * y + h * y;
    //    }
    //    else if ((hue >= 1.0) && (hue < 2.0)) {
    //      h -= 1.0;
    //      r = (1.0 - h) * z + h * y;
    //      g = (1.0 - h) * v + h * v;
    //      b = (1.0 - h) * y + h * x;
    //    }
    //    else if ((hue >= 2.0) && (hue < 3.0)) {
    //      h -= 2.0;
    //      r = (1.0 - h) * y + h * y;
    //      g = (1.0 - h) * v + h * z;
    //      b = (1.0 - h) * x + h * v;
    //    }
    //    else if ((hue >= 3.0) && (hue < 4.0)) {
    //      h -= 3.0;
    //      r = (1.0 - h) * y + h * x;
    //      g = (1.0 - h) * z + h * y;
    //      b = (1.0 - h) * v + h * v;
    //    }
    //    else if ((hue >= 4.0) && (hue < 5.0)) {
    //      h -= 4.0;
    //      r = (1.0 - h) * x + h * v;
    //      g = (1.0 - h) * y + h * y;
    //      b = (1.0 - h) * v + h * z;
    //    }
    //    else {
    //      h -= 5.0;
    //      r = (1.0 - h) * v + h * v;
    //      g = (1.0 - h) * y + h * x;
    //      b = (1.0 - h) * z + h * y;
    //    }

    switch ((int) hue) {
      case 0:
        r = v;
        g = x;
        b = y;
        break;
      case 1:
        r = z;
        g = v;
        b = y;
        break;
      case 2:
        r = y;
        g = v;
        b = x;
        break;
      case 3:
        r = y;
        g = z;
        b = v;
        break;
      case 4:
        r = x;
        g = y;
        b = v;
        break;
      case 5:
        r = v;
        g = y;
        b = z;
        break;
      default:
        //            r = v;
        //            g = x;
        //            b = y;
        r = v;
        g = y;
        b = z;
    }
    rgbPixel.r = Tools.roundColor(r * 255.0);
    rgbPixel.g = Tools.roundColor(g * 255.0);
    rgbPixel.b = Tools.roundColor(b * 255.0);
  }

  private static double _max(double x, double y) {
    return (((x) > (y)) ? (x) : (y));
  }

  private static double _min(double x, double y) {
    return (((x) < (y)) ? (x) : (y));
  }

  public static void rgb2hsl(Pixel rgbPixel, HSLPixel hslPixel) {
    hslPixel.hue = 1.0;
    hslPixel.saturation = 0.0;
    double r = (double) rgbPixel.r / 255.0;
    double g = (double) rgbPixel.g / 255.0;
    double b = (double) rgbPixel.b / 255.0;
    double max = _max(r, _max(g, b));
    double min = _min(r, _min(g, b));
    /*
    if ((max - min) < 0.1)
      min = max;
    */
    hslPixel.luminosity = (min + max) / 2.0;
    if (Math.abs(hslPixel.luminosity) <= MathLib.EPSILON)
      return;
    hslPixel.saturation = max - min;

    if (Math.abs(hslPixel.saturation) <= MathLib.EPSILON)
      return;

    hslPixel.saturation /= ((hslPixel.luminosity) <= 0.5) ? (min + max) : (2.0 - max - min);
    if (Math.abs(r - max) < MathLib.EPSILON) {
      hslPixel.hue = ((g == min) ? 5.0 + (max - b) / (max - min) : 1.0 - (max - g) / (max - min));
    }
    else {
      if (Math.abs(g - max) < MathLib.EPSILON) {
        hslPixel.hue = ((b == min) ? 1.0 + (max - r) / (max - min) : 3.0 - (max - b) / (max - min));
      }
      else {
        hslPixel.hue = ((r == min) ? 3.0 + (max - g) / (max - min) : 5.0 - (max - r) / (max - min));
      }
    }
    hslPixel.hue /= 6.0;
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    hue = saturation = luminosity = 0;
  }

  public static class HSLPixel {
    public double hue;
    public double saturation;
    public double luminosity;

    public HSLPixel() {
    }

    public HSLPixel(double hue, double saturation, double luminosity) {
      this.hue = hue;
      this.saturation = saturation;
      this.luminosity = luminosity;
    }
  }

  public int getHue() {
    return hue;
  }

  public void setHue(int hue) {
    this.hue = hue;
  }

  public int getSaturation() {
    return saturation;
  }

  public void setSaturation(int saturation) {
    this.saturation = saturation;
  }

  public int getLuminosity() {
    return luminosity;
  }

  public void setLuminosity(int luminosity) {
    this.luminosity = luminosity;
  }

}
