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
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class AddTransformer extends Mesh2DTransformer {
  public enum Mode {
    ADD, SUBTRACT, EXPOSURE
  }

  @Property(description = "Add or subtract the pixel values of the foreground image", editorClass = ModeEditor.class)
  private Mode mode = Mode.ADD;
  @Property(category = PropertyCategory.PRIMARY, description = "Image to add/subtract", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer foreground;
  private SimpleImage foregroundImage; // Alternative way to specify the foreground image directly
  @Property(category = PropertyCategory.SECONDARY, description = "Exposure exponent (mode=EXPOSURE)")
  private double exposureExp;
  @Property(category = PropertyCategory.SECONDARY, description = "Exposure bias (mode=EXPOSURE)")
  private double exposureBias;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    SimpleImage fImg = (foregroundImage != null) ? foregroundImage : foreground.getImage();
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel srcPixel = new Pixel();
    Pixel dstPixel = new Pixel();
    if (mode == Mode.ADD) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          srcPixel.setARGBValue(fImg.getARGBValueIgnoreBounds(j, i));
          dstPixel.setARGBValue(img.getARGBValue(j, i));
          dstPixel.r += srcPixel.r;
          if (dstPixel.r > 255)
            dstPixel.r = 255;
          dstPixel.g += srcPixel.g;
          if (dstPixel.g > 255)
            dstPixel.g = 255;
          dstPixel.b += srcPixel.b;
          if (dstPixel.b > 255)
            dstPixel.b = 255;
          img.setRGB(j, i, dstPixel);
        }
      }
    }
    else if (mode == Mode.SUBTRACT) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          srcPixel.setARGBValue(fImg.getARGBValueIgnoreBounds(j, i));
          dstPixel.setARGBValue(img.getARGBValue(j, i));
          dstPixel.r -= srcPixel.r;
          if (dstPixel.r < 0)
            dstPixel.r = 0;
          dstPixel.g -= srcPixel.g;
          if (dstPixel.g < 0)
            dstPixel.g = 0;
          dstPixel.b -= srcPixel.b;
          if (dstPixel.b < 0)
            dstPixel.b = 0;
          img.setRGB(j, i, dstPixel);
        }
      }
    }
    else if (mode == Mode.EXPOSURE) {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          srcPixel.setARGBValue(fImg.getARGBValueIgnoreBounds(j, i));
          dstPixel.setARGBValue(img.getARGBValue(j, i));
          dstPixel.r = expose(dstPixel.r + 2 * srcPixel.r);
          dstPixel.g = expose(dstPixel.g + 2 * srcPixel.g);
          dstPixel.b = expose(dstPixel.b + 2 * srcPixel.b);
          img.setRGB(j, i, dstPixel);
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    mode = Mode.ADD;
    exposureExp = -0.0032;
    exposureBias = 66.0;
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.ADD, Mode.SUBTRACT, Mode.EXPOSURE });
    }
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public Buffer getForeground() {
    return foreground;
  }

  public void setForeground(Buffer foreground) {
    this.foreground = foreground;
  }

  public void setForegroundImage(SimpleImage pForegroundImage) {
    foregroundImage = pForegroundImage;
  }

  private int expose(double light) {
    return Tools.roundColor((1.0 - Math.exp(light * exposureExp)) * 255.0 + exposureBias);
  }

  public double getExposureExp() {
    return exposureExp;
  }

  public void setExposureExp(double exposureExp) {
    this.exposureExp = exposureExp;
  }

  public double getExposureBias() {
    return exposureBias;
  }

  public void setExposureBias(double exposureBias) {
    this.exposureBias = exposureBias;
  }

}
