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
package org.jwildfire.create.tina.palette;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.BalancingTransformer;
import org.jwildfire.transform.HSLTransformer;
import org.jwildfire.transform.SwapRGBTransformer;
import org.jwildfire.transform.SwapRGBTransformer.Mode;

public class RGBPalette {
  public static final int PALETTE_SIZE = 256;
  static final RGBColor BLACK = new RGBColor(0, 0, 0);
  private int highestIdx = -1;
  private boolean modified = true;
  private int modRed;
  private int modGreen;
  private int modBlue;
  private int modShift;
  private int modHue;
  private int modContrast;
  private int modGamma;
  private int modBrightness;
  private int modSaturation;
  private int modSwapRGB;
  private String flam3Number; // imported from flam3, just for display
  private String flam3Name; // imported from flam3, just for display

  private final Map<Integer, RGBColor> rawColors = new HashMap<Integer, RGBColor>();
  private final Map<Integer, RGBColor> transformedColors = new HashMap<Integer, RGBColor>();

  public void addColor(int pRed, int pGreen, int pBlue) {
    RGBColor color = new RGBColor(pRed, pGreen, pBlue);
    if (highestIdx + 1 == PALETTE_SIZE) {
      throw new IllegalStateException("Palette is already complete");
    }
    else {
      rawColors.put(Integer.valueOf(++highestIdx), color);
    }
    modified = true;
  }

  public void setColor(int pIndex, int pRed, int pGreen, int pBlue) {
    if (pIndex < 0 || pIndex >= PALETTE_SIZE)
      throw new IllegalArgumentException(pIndex + " is no valid index");
    RGBColor color = new RGBColor(pRed, pGreen, pBlue);
    rawColors.put(Integer.valueOf(pIndex), color);
    modified = true;
  }

  public RGBColor getColor(int pIdx) {
    transformColors();
    RGBColor res = transformedColors.get(pIdx);
    return res != null ? res : BLACK;
  }

  public RenderColor[] createRenderPalette(int pWhiteLevel) {
    transformColors();
    RenderColor res[] = new RenderColor[PALETTE_SIZE];
    for (int i = 0; i < res.length; i++) {
      res[i] = new RenderColor();
      RGBColor color = transformedColors.get(i);
      int r, g, b;
      if (color != null) {
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
      }
      else {
        RGBColor leftColor = null, rightColor = null;
        int leftIdx = i, rightIdx = i;
        while (leftIdx-- >= 0) {
          leftColor = transformedColors.get(leftIdx);
          if (leftColor != null) {
            break;
          }
        }
        while (rightIdx++ < PALETTE_SIZE) {
          rightColor = transformedColors.get(rightIdx);
          if (rightColor != null) {
            break;
          }
        }
        if (leftColor != null && rightColor != null) {
          r = leftColor.getRed() + (rightColor.getRed() - leftColor.getRed()) * (leftIdx - i) / (leftIdx - rightIdx);
          g = leftColor.getGreen() + (rightColor.getGreen() - leftColor.getGreen()) * (leftIdx - i) / (leftIdx - rightIdx);
          b = leftColor.getBlue() + (rightColor.getBlue() - leftColor.getBlue()) * (leftIdx - i) / (leftIdx - rightIdx);
        }
        else if (leftColor != null && rightColor == null) {
          r = leftColor.getRed();
          g = leftColor.getGreen();
          b = leftColor.getBlue();
        }
        else if (leftColor == null && rightColor != null) {
          r = rightColor.getRed();
          g = rightColor.getGreen();
          b = rightColor.getBlue();
        }
        else {
          r = BLACK.getRed();
          g = BLACK.getGreen();
          b = BLACK.getBlue();
        }
      }

      res[i].red = (r * pWhiteLevel) / 256;
      res[i].green = (g * pWhiteLevel) / 256;
      res[i].blue = (b * pWhiteLevel) / 256;
    }
    return res;
  }

  public int getSize() {
    return rawColors.size();
  }

  public int getModRed() {
    return modRed;
  }

  public void setModRed(int modRed) {
    this.modRed = modRed;
    modified = true;
  }

  public int getModGreen() {
    return modGreen;
  }

  public void setModGreen(int modGreen) {
    this.modGreen = modGreen;
    modified = true;
  }

  public int getModBlue() {
    return modBlue;
  }

  public void setModBlue(int modBlue) {
    this.modBlue = modBlue;
    modified = true;
  }

  public int getModShift() {
    return modShift;
  }

  public void setModShift(int modShift) {
    this.modShift = modShift;
    modified = true;
  }

  public int getModHue() {
    return modHue;
  }

  public void setModHue(int modHue) {
    this.modHue = modHue;
    modified = true;
  }

  public int getModContrast() {
    return modContrast;
  }

  public void setModContrast(int modContrast) {
    this.modContrast = modContrast;
    modified = true;
  }

  public int getModGamma() {
    return modGamma;
  }

  public void setModGamma(int modGamma) {
    this.modGamma = modGamma;
    modified = true;
  }

  public int getModBrightness() {
    return modBrightness;
  }

  public void setModBrightness(int modBrightness) {
    this.modBrightness = modBrightness;
    modified = true;
  }

  public int getModSwapRGB() {
    return modSwapRGB;
  }

  public void setModSwapRGB(int modSwapRGB) {
    this.modSwapRGB = modSwapRGB;
    modified = true;
  }

  public int getModSaturation() {
    return modSaturation;
  }

  public void setModSaturation(int modSaturation) {
    this.modSaturation = modSaturation;
    modified = true;
  }

  private void transformColors() {
    if (modified) {
      transformedColors.clear();
      for (int i = 0; i < PALETTE_SIZE; i++) {
        RGBColor color = rawColors.get(i);
        int idx = i + modShift;
        if (idx < 0) {
          idx += PALETTE_SIZE;
        }
        else if (idx >= PALETTE_SIZE) {
          idx -= PALETTE_SIZE;
        }
        transformedColors.put(idx, new RGBColor(color.getRed(), color.getGreen(), color.getBlue()));
      }
      SimpleImage img = new RGBPaletteRenderer().renderHorizPalette(transformedColors, PALETTE_SIZE, 1);
      if (modRed != 0 || modGreen != 0 || modBlue != 0 || modContrast != 0 || modGamma != 0 || modBrightness != 0 || modSaturation != 0) {
        BalancingTransformer bT = new BalancingTransformer();
        bT.setRed(modRed);
        bT.setGreen(modGreen);
        bT.setBlue(modBlue);
        bT.setContrast(modContrast);
        bT.setGamma(modGamma);
        bT.setBrightness(modBrightness);
        bT.setSaturation(modSaturation);
        bT.transformImage(img);
      }
      if (modHue != 0) {
        HSLTransformer hT = new HSLTransformer();
        hT.setHue(modHue);
        hT.transformImage(img);
      }
      if (modSwapRGB != 0) {
        SwapRGBTransformer sT = new SwapRGBTransformer();
        int maxValues = Mode.values().length;
        int idx = (int) ((double) Math.abs(modSwapRGB) / (double) 255.0 * (double) (maxValues - 1));
        sT.setMode(Mode.values()[idx]);
        sT.transformImage(img);
      }
      Pixel pixel = new Pixel();
      for (int i = 0; i < PALETTE_SIZE; i++) {
        RGBColor color = transformedColors.get(i);
        pixel.setARGBValue(img.getARGBValue(i, 0));
        color.setRed(pixel.r);
        color.setGreen(pixel.g);
        color.setBlue(pixel.b);
      }

      modified = false;
    }
  }

  public Map<Integer, RGBColor> getTransformedColors() {
    transformColors();
    return transformedColors;
  }

  public RGBPalette makeCopy() {
    RGBPalette res = new RGBPalette();
    res.assign(this);
    return res;
  }

  private void assign(RGBPalette pRGBPalette) {
    highestIdx = pRGBPalette.highestIdx;
    modified = pRGBPalette.modified;
    modRed = pRGBPalette.modRed;
    modGreen = pRGBPalette.modGreen;
    modBlue = pRGBPalette.modBlue;
    modShift = pRGBPalette.modShift;
    modHue = pRGBPalette.modHue;
    modContrast = pRGBPalette.modContrast;
    modGamma = pRGBPalette.modGamma;
    modBrightness = pRGBPalette.modBrightness;
    modSaturation = pRGBPalette.modSaturation;
    modSwapRGB = pRGBPalette.modSwapRGB;
    flam3Name = pRGBPalette.flam3Name;
    flam3Number = pRGBPalette.flam3Number;

    rawColors.clear();
    for (Integer key : pRGBPalette.rawColors.keySet()) {
      RGBColor newColor = pRGBPalette.rawColors.get(key).makeCopy();
      rawColors.put(key, newColor);
    }
    transformedColors.clear();
    for (Integer key : pRGBPalette.transformedColors.keySet()) {
      RGBColor newColor = pRGBPalette.transformedColors.get(key).makeCopy();
      transformedColors.put(key, newColor);
    }
  }

  public String getFlam3Number() {
    return flam3Number;
  }

  public void setFlam3Number(String flam3Number) {
    this.flam3Number = flam3Number;
  }

  public String getFlam3Name() {
    return flam3Name;
  }

  public void setFlam3Name(String flam3Name) {
    this.flam3Name = flam3Name;
  }

  @Override
  public String toString() {
    if (flam3Number != null && flam3Number.length() > 0) {
      if (flam3Name != null && flam3Name.length() > 0) {
        return flam3Number + " - " + flam3Name;
      }
      else {
        return flam3Name;
      }
    }
    else if (flam3Name != null && flam3Name.length() > 0) {
      return flam3Name;
    }
    else {
      return super.toString();
    }
  }

}
