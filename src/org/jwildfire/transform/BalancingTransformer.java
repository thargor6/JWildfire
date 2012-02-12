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
package org.jwildfire.transform;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class BalancingTransformer extends Mesh2DTransformer {

  @Property(description = "Color correction of the red channel")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int red = 0;

  @Property(description = "Color correction of the green channel")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int green = 0;

  @Property(description = "Color correction of the blue channel")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int blue = 0;

  @Property(description = "Brightness correction")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int brightness = 0;

  @Property(description = "Contrast correction")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int contrast = 0;

  @Property(description = "Gamma correction")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int gamma = 0;

  @Property(description = "Saturation correction")
  @PropertyMin(-255)
  @PropertyMax(255)
  private int saturation = 0;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    final double brMAX = 256.0;
    Pixel pixel = new Pixel();
    /* saturation */
    if (this.saturation != 0) {
      int rs = 2990;
      int gs = 5880;
      int bs = 1130;
      rs = (rs * Tools.VPREC) / 10000;
      gs = (gs * Tools.VPREC) / 10000;
      bs = (bs * Tools.VPREC) / 10000;
      int scl = (int) ((double) this.saturation / 255.0 * 1024.0 + 0.5);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(img.getARGBValue(j, i));
          int avg = (rs * pixel.r + gs * pixel.g + bs * pixel.b) >> Tools.SPREC;
          pixel.r += ((pixel.r - avg) * scl) >> Tools.SPREC;
          if (pixel.r < 0)
            pixel.r = 0;
          else if (pixel.r > 255)
            pixel.r = 255;
          pixel.g += ((pixel.g - avg) * scl) >> Tools.SPREC;
          if (pixel.g < 0)
            pixel.g = 0;
          else if (pixel.g > 255)
            pixel.g = 255;
          pixel.b += ((pixel.b - avg) * scl) >> Tools.SPREC;
          if (pixel.b < 0)
            pixel.b = 0;
          else if (pixel.b > 255)
            pixel.b = 255;
          img.setRGB(j, i, pixel);
        }
      }
    }
    /* gamma */
    if (this.gamma != 0) {
      double max = 255.0;
      double g = (double) 512.0 / (512.0 + (double) this.gamma);
      int gamma[] = new int[256];
      double da, aa;
      da = aa = 1.0 / 255.0;
      gamma[0] = 0;
      for (int i = 1; i < 256; i++) {
        int val = (int) (max * Math.pow(aa, g) + 0.5);
        aa += da;
        gamma[i] = val;
      }
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(img.getARGBValue(j, i));
          pixel.r = gamma[pixel.r];
          pixel.g = gamma[pixel.g];
          pixel.b = gamma[pixel.b];
          img.setRGB(j, i, pixel);
        }
      }
    }
    /* red */
    if (this.red != 0) {
      int tt = (int) ((double) this.red / (double) brMAX * (double) 255.0 + 0.5);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(img.getARGBValue(j, i));
          pixel.r += tt;
          if (pixel.r < 0)
            pixel.r = 0;
          else if (pixel.r > 255)
            pixel.r = 255;
          img.setRGB(j, i, pixel);
        }
      }
    }
    /* green */
    if (this.green != 0) {
      int tt = (int) ((double) this.green / (double) brMAX * (double) 255.0 + 0.5);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(img.getARGBValue(j, i));
          pixel.g += tt;
          if (pixel.g < 0)
            pixel.g = 0;
          else if (pixel.g > 255)
            pixel.g = 255;
          img.setRGB(j, i, pixel);
        }
      }
    }
    /* blue */
    if (this.blue != 0) {
      int tt = (int) ((double) this.blue / (double) brMAX * (double) 255.0 + 0.5);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(img.getARGBValue(j, i));
          pixel.b += tt;
          if (pixel.b < 0)
            pixel.b = 0;
          else if (pixel.b > 255)
            pixel.b = 255;
          img.setRGB(j, i, pixel);
        }
      }
    }
    /* brightness */
    if (brightness != 0) {
      int tt = (int) ((double) this.brightness / (double) brMAX * (double) 255.0 + 0.5);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          pixel.setARGBValue(img.getARGBValue(j, i));
          pixel.r += tt;
          if (pixel.r < 0)
            pixel.r = 0;
          else if (pixel.r > 255)
            pixel.r = 255;
          pixel.g += tt;
          if (pixel.g < 0)
            pixel.g = 0;
          else if (pixel.g > 255)
            pixel.g = 255;
          pixel.b += tt;
          if (pixel.b < 0)
            pixel.b = 0;
          else if (pixel.b > 255)
            pixel.b = 255;
          img.setRGB(j, i, pixel);
        }
      }

    }
    /* contrast */
    if (this.contrast != 0) {
      double scale = (double) this.contrast / brMAX;
      int sc = (int) (scale * (double) Tools.VPREC + 0.5);
      int dc;
      if (this.contrast > 0) {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            pixel.setARGBValue(img.getARGBValue(j, i));
            dc = (int) (((int) (pixel.r - (int) 127) * sc) >> Tools.SPREC);
            if (dc < (-255))
              dc = (-255);
            else if (dc > 255)
              dc = 255;
            pixel.r += dc;
            if (pixel.r < 0)
              pixel.r = 0;
            else if (pixel.r > 255)
              pixel.r = 255;
            dc = (int) (((int) (pixel.g - (int) 127) * sc) >> Tools.SPREC);
            if (dc < (-255))
              dc = (-255);
            else if (dc > 255)
              dc = 255;
            pixel.g += dc;
            if (pixel.g < 0)
              pixel.g = 0;
            else if (pixel.g > 255)
              pixel.g = 255;
            dc = (int) ((int) ((pixel.b - (int) 127) * sc) >> Tools.SPREC);
            if (dc < (-255))
              dc = (-255);
            else if (dc > 255)
              dc = 255;
            pixel.b += dc;
            if (pixel.b < 0)
              pixel.b = 0;
            else if (pixel.b > 255)
              pixel.b = 255;
            img.setRGB(j, i, pixel);
          }
        }
      }
      else {
        int val;
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            pixel.setARGBValue(img.getARGBValue(j, i));
            dc = (int) (((int) ((int) pixel.r - (int) 127) * sc) >> Tools.SPREC);
            if (dc < (-255))
              dc = (-255);
            else if (dc > 255)
              dc = 255;
            val = pixel.r + dc;
            if (pixel.r < 127) {
              if (val > 127)
                val = 127;
            }
            else {
              if (val < 127)
                val = 127;
            }
            pixel.r = val;

            dc = (int) ((int) ((pixel.g - (int) 127) * sc) >> Tools.SPREC);
            if (dc < (-255))
              dc = (-255);
            else if (dc > 255)
              dc = 255;
            val = pixel.g + dc;
            if (pixel.g < 127) {
              if (val > 127)
                val = 127;
            }
            else {
              if (val < 127)
                val = 127;
            }
            pixel.g = val;

            dc = (int) ((int) ((pixel.b - (int) 127) * sc) >> Tools.SPREC);
            if (dc < (-255))
              dc = (-255);
            else if (dc > 255)
              dc = 255;
            val = pixel.b + dc;
            if (pixel.b < 127) {
              if (val > 127)
                val = 127;
            }
            else {
              if (val < 127)
                val = 127;
            }
            pixel.b = val;

            img.setRGB(j, i, pixel);
          }
        }
      }
    }
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

  public int getBrightness() {
    return brightness;
  }

  public void setBrightness(int brightness) {
    this.brightness = brightness;
  }

  public int getContrast() {
    return contrast;
  }

  public void setContrast(int contrast) {
    this.contrast = contrast;
  }

  public int getGamma() {
    return gamma;
  }

  public void setGamma(int gamma) {
    this.gamma = gamma;
  }

  public int getSaturation() {
    return saturation;
  }

  public void setSaturation(int saturation) {
    this.saturation = saturation;
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    red = green = blue = brightness = contrast = gamma = saturation = 0;
  }

}
