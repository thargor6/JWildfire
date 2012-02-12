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
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.swing.HDRImageBufferComboBoxEditor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class HDRMergeTransformer extends Transformer {
  public enum MergeMode {
    ADD, BLUE, DARKEN, GREEN, LIGHTEN, MULTIPLY, NORMAL, RED, SUBTRACT
  }

  /*  

  Screen  
  Multiplies the inverse of the layer with the inverse of the underlying layers, and inverts that again. The result is always a lighter color, thus brightening the underlying layers. Screen is the inverse of Multiply.

  Overlay 
  Multiplies or screens the colors, depending on the color in the underlying layers. Creates color blending effects between the layer and the underlying layers.

  Hard Light  
  Multiplies or screens the colors, depending on the color in the layer. Emphasizes the dark and light regions in the layer, while the areas with medium brightness become transparent. Useful if the layer contains shadows or embossing effects.

  Soft Light  
  Darkens or lightens the colors, depending on the color in the layer. Creates an effect similar to Hard Light, but with less emphasis on the dark and light areas in the layer.

  Difference  
  Returns the difference between the layer and the underlying layers. Often creates unusual and unexpected color transitions.

  Hue 
  Returns the hue of the layer, and the saturation and luminance of the underlying layers. Colors the underlying layers with the hue of the layer.

  Saturation  
  Returns the saturation of the layer, and the hue and luminance of the underlying layers. Changes the saturation of the underlying layers depending on the layer.

  Color 
  Returns the hue and saturation of the layer, and the luminance of the underlying layers. Colors the underlying layers with the layer. The underlying layers control the brightness of the resulting image.

  Luminance 
  Returns the luminance of the layer, and the hue and saturation of the underlying layers. The layer controls the brightness of the underlying layers. Luminance is the inverse of Color.

  HSL Addition  
  Adds the layer to the underlying layers using the HSL color model. Creates unusual effects.
    */

  @Property(description = "Merge mode", editorClass = MergeModeEditor.class)
  private MergeMode mergeMode = MergeMode.NORMAL;
  @Property(category = PropertyCategory.PRIMARY, description = "Foreground image", editorClass = HDRImageBufferComboBoxEditor.class)
  private Buffer foreground;
  @Property(category = PropertyCategory.PRIMARY, description = "Foreground transparency")
  private double transparency = 0.0;
  @Property(category = PropertyCategory.PRIMARY, description = "Foreground intensity")
  private double intensity = 1.0;

  @Override
  protected void performImageTransformation(WFImage pImg) {
    SimpleHDRImage fgImg = foreground.getHDRImage();
    SimpleHDRImage bgImg = (SimpleHDRImage) pImg;
    if ((fgImg.getImageWidth() != bgImg.getImageWidth()) || (fgImg.getImageHeight() != bgImg.getImageHeight())) {
      throw new IllegalArgumentException();
    }
    SimpleHDRImage res = new SimpleHDRImage(fgImg.getImageWidth(), fgImg.getImageHeight());
    float fgRGB[] = new float[3];
    float bgRGB[] = new float[3];
    float r, g, b;
    float trans = (float) transparency * 0.01f;
    if (trans < 0.0f) {
      trans = 0.0f;
    }
    else if (trans > 1.0f) {
      trans = 1.0f;
    }
    float invTrans = 1.0f - trans;
    float fgScale = (float) intensity;
    float bgScale = (float) intensity;
    float fgRed, fgGreen, fgBlue;
    float bgRed, bgGreen, bgBlue;
    float mergedRed, mergedGreen, mergedBlue;

    float lum[] = new float[2];
    fgImg.getMinMaxLum(lum);
    float fgLumMin = lum[0];
    float fgLumMax = lum[1];
    if ((fgLumMax - fgLumMin) < Tools.EPSILON) {
      fgLumMax = fgLumMin + (float) Tools.EPSILON;
    }
    bgImg.getMinMaxLum(lum);
    float bgLumMin = lum[0];
    float bgLumMax = lum[1];
    if ((bgLumMax - bgLumMin) < Tools.EPSILON) {
      bgLumMax = bgLumMin + (float) Tools.EPSILON;
    }
    bgScale *= (fgLumMax - fgLumMin) / (bgLumMax - bgLumMin);
    for (int i = 0; i < fgImg.getImageHeight(); i++) {
      for (int j = 0; j < fgImg.getImageWidth(); j++) {
        fgImg.getRGBValues(fgRGB, j, i);
        fgRed = fgRGB[0] * fgScale;
        fgGreen = fgRGB[1] * fgScale;
        fgBlue = fgRGB[2] * fgScale;
        bgRed = bgRGB[0] * bgScale;
        bgGreen = bgRGB[1] * bgScale;
        bgBlue = bgRGB[2] * bgScale;
        bgImg.getRGBValues(bgRGB, j, i);
        switch (mergeMode) {
          case MULTIPLY:
            mergedRed = fgRed * bgRed;
            mergedGreen = fgGreen * bgGreen;
            mergedBlue = fgBlue * bgBlue;
            break;
          case ADD:
            mergedRed = (fgRed + bgRed) * 0.5f;
            mergedGreen = (fgGreen + bgGreen) * 0.5f;
            mergedBlue = (fgBlue + bgBlue) * 0.5f;
            break;
          case SUBTRACT:
            mergedRed = bgRed - fgRed;
            if (mergedRed < 0.0f) {
              mergedRed = 0.0f;
            }
            mergedGreen = bgGreen - fgGreen;
            if (mergedGreen < 0.0f) {
              mergedGreen = 0.0f;
            }
            mergedBlue = bgBlue - fgBlue;
            if (mergedBlue < 0.0f) {
              mergedBlue = 0.0f;
            }
            break;
          case RED:
            mergedRed = fgRed;
            mergedGreen = bgGreen;
            mergedBlue = bgBlue;
            break;
          case GREEN:
            mergedRed = bgRed;
            mergedGreen = fgGreen;
            mergedBlue = bgBlue;
            break;
          case BLUE:
            mergedRed = bgRed;
            mergedGreen = bgGreen;
            mergedBlue = fgBlue;
            break;
          case LIGHTEN: {
            float fgLum = SimpleHDRImage.calcLum(fgRed, fgGreen, fgBlue);
            float bgLum = SimpleHDRImage.calcLum(bgRed, bgGreen, bgBlue);
            if (fgLum > bgLum) {
              mergedRed = fgRed;
              mergedGreen = fgGreen;
              mergedBlue = fgBlue;
            }
            else {
              mergedRed = bgRed;
              mergedGreen = bgGreen;
              mergedBlue = bgBlue;
            }
          }
            break;
          case DARKEN: {
            float fgLum = SimpleHDRImage.calcLum(fgRed, fgGreen, fgBlue);
            float bgLum = SimpleHDRImage.calcLum(bgRed, bgGreen, bgBlue);
            if (fgLum < bgLum) {
              mergedRed = fgRed;
              mergedGreen = fgGreen;
              mergedBlue = fgBlue;
            }
            else {
              mergedRed = bgRed;
              mergedGreen = bgGreen;
              mergedBlue = bgBlue;
            }
          }
            break;
          default:
            mergedRed = fgRed;
            mergedGreen = fgGreen;
            mergedBlue = fgBlue;
            break;
        }
        r = mergedRed * invTrans + bgRed * trans;
        g = mergedGreen * invTrans + bgGreen * trans;
        b = mergedBlue * invTrans + bgBlue * trans;
        res.setRGB(j, i, r, g, b);
      }
    }

    ((SimpleHDRImage) pImg).assignImage(res);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    mergeMode = MergeMode.NORMAL;
  }

  public MergeMode getMergeMode() {
    return mergeMode;
  }

  public void setMergeMode(MergeMode mergeMode) {
    this.mergeMode = mergeMode;
  }

  public Buffer getForeground() {
    return foreground;
  }

  public void setForeground(Buffer foreground) {
    this.foreground = foreground;
  }

  public double getTransparency() {
    return transparency;
  }

  public void setTransparency(double transparency) {
    this.transparency = transparency;
  }

  @Override
  public BufferType getBufferType() {
    return BufferType.HDR_IMAGE;
  }

  @Override
  public boolean supports3DOutput() {
    return false;
  }

  @Override
  public boolean acceptsInputBufferType(BufferType pBufferType) {
    return pBufferType.equals(BufferType.HDR_IMAGE);
  }

  public double getIntensity() {
    return intensity;
  }

  public void setIntensity(double intensity) {
    this.intensity = intensity;
  }

  public static class MergeModeEditor extends ComboBoxPropertyEditor {
    public MergeModeEditor() {
      super();
      setAvailableValues(new MergeMode[] { MergeMode.ADD, MergeMode.BLUE, MergeMode.DARKEN, MergeMode.GREEN, MergeMode.LIGHTEN, MergeMode.MULTIPLY, MergeMode.NORMAL, MergeMode.RED, MergeMode.SUBTRACT });
    }
  }

}
