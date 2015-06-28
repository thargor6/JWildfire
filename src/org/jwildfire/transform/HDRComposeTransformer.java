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
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.swing.HDRImageBufferComboBoxEditor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class HDRComposeTransformer extends Transformer {
  public enum MergeMode {
    ADD, BLUE, DARKEN, GREEN, HSL_ADD, LIGHTEN, MULTIPLY, NORMAL, RED, SUBTRACT
  }

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
    HSLTransformer fgHSL = new HSLTransformer();
    HSLTransformer bgHSL = new HSLTransformer();
    HSLTransformer mergedHSL = new HSLTransformer();

    float lum[] = new float[2];
    fgImg.getMinMaxLum(lum);
    float fgLumMin = lum[0];
    float fgLumMax = lum[1];
    if ((fgLumMax - fgLumMin) < MathLib.EPSILON) {
      fgLumMax = fgLumMin + (float) MathLib.EPSILON;
    }
    bgImg.getMinMaxLum(lum);
    float bgLumMin = lum[0];
    float bgLumMax = lum[1];
    if ((bgLumMax - bgLumMin) < MathLib.EPSILON) {
      bgLumMax = bgLumMin + (float) MathLib.EPSILON;
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
          case HSL_ADD:
            fgHSL.setRGB(fgRed, fgGreen, fgBlue);
            bgHSL.setRGB(bgRed, bgGreen, bgBlue);
            mergedHSL.setHSL(fgHSL.getHue() + bgHSL.getHue(), fgHSL.getSaturation() + bgHSL.getSaturation(), fgHSL.getLuminosity() + bgHSL.getLuminosity(), fgHSL.getAmp());
            mergedRed = mergedHSL.getRed();
            mergedGreen = mergedHSL.getGreen();
            mergedBlue = mergedHSL.getBlue();
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

  public static class HSLTransformer {
    private float red, green, blue, amp;
    private float hue, saturation, luminosity;
    private static final float EPSILON = 0.000001f;

    private float _max(float x, float y) {
      return (((x) > (y)) ? (x) : (y));
    }

    private float _min(float x, float y) {
      return (((x) < (y)) ? (x) : (y));
    }

    public void setRGB(float pRed, float pGreen, float pBlue) {
      this.red = pRed;
      this.green = pGreen;
      this.blue = pBlue;
      this.hue = 1.0f;
      this.saturation = 0.0f;
      this.luminosity = 0.0f;
      this.amp = _max(_max(pRed, pGreen), pBlue);
      if (amp < 0.00001f) {
        return;
      }
      float r = pRed / amp;
      float g = pGreen / amp;
      float b = pBlue / amp;
      float max = _max(r, _max(g, b));
      float min = _min(r, _min(g, b));
      this.luminosity = (min + max) / 2.0f;
      if (Math.abs(this.luminosity) <= EPSILON) {
        return;
      }
      this.saturation = max - min;
      if (Math.abs(this.saturation) <= EPSILON) {
        return;
      }

      this.saturation /= ((this.luminosity) <= 0.5f) ? (min + max) : (2.0f - max - min);
      if (Math.abs(r - max) < EPSILON) {
        this.hue = ((g == min) ? 5.0f + (max - b) / (max - min) : 1.0f - (max - g) / (max - min));
      }
      else {
        if (Math.abs(g - max) < EPSILON) {
          this.hue = ((b == min) ? 1.0f + (max - r) / (max - min) : 3.0f - (max - b) / (max - min));
        }
        else {
          this.hue = ((r == min) ? 3.0f + (max - g) / (max - min) : 5.0f - (max - r) / (max - min));
        }
      }
      this.hue /= 6.0f;
    }

    private float limit_int(float pVal) {
      if (pVal < 0.0f) {
        return 0.0f;
      }
      else if (pVal > 1.0f) {
        return 1.0f;
      }
      else {
        return pVal;
      }
    }

    public void setHSL(float pHue, float pSaturation, float pLuminosity, float amp) {
      this.luminosity = limit_int(pLuminosity);
      this.saturation = limit_int(pSaturation);
      this.hue = limit_int(pHue);
      this.amp = amp;
      float v = (luminosity <= 0.5f) ? (luminosity * (1.0f + saturation))
          : (luminosity + saturation - luminosity * saturation);
      if (v <= 0) {
        this.red = 0.0f;
        this.green = 0.0f;
        this.blue = 0.0f;
        this.amp = 0.0f;
        return;
      }
      this.hue *= 6.0f;
      if (this.hue < 0.0f)
        this.hue = 0.0f;
      else if (this.hue > 6.0f)
        this.hue = 6.0f;
      float y = this.luminosity + this.luminosity - v;
      float x = y + (v - y) * (this.hue - (int) this.hue);
      float z = v - (v - y) * (this.hue - (int) this.hue);
      float r, g, b;
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
          r = v;
          g = y;
          b = z;
      }
      this.red = r * this.amp;
      this.green = g * this.amp;
      this.blue = b * this.amp;
    }

    public float getRed() {
      return red;
    }

    public float getGreen() {
      return green;
    }

    public float getBlue() {
      return blue;
    }

    public float getHue() {
      return hue;
    }

    public float getSaturation() {
      return saturation;
    }

    public float getLuminosity() {
      return luminosity;
    }

    public float getAmp() {
      return amp;
    }
  }

  public static class MergeModeEditor extends ComboBoxPropertyEditor {
    public MergeModeEditor() {
      super();
      setAvailableValues(new MergeMode[] { MergeMode.ADD, MergeMode.BLUE, MergeMode.DARKEN, MergeMode.GREEN, MergeMode.HSL_ADD, MergeMode.LIGHTEN, MergeMode.MULTIPLY, MergeMode.NORMAL, MergeMode.RED, MergeMode.SUBTRACT });
    }
  }

}
