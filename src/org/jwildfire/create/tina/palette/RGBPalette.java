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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.BalancingTransformer;
import org.jwildfire.transform.HSLTransformer;
import org.jwildfire.transform.SwapRGBTransformer;
import org.jwildfire.transform.SwapRGBTransformer.Mode;

public class RGBPalette implements Assignable<RGBPalette>, Serializable {
  private static final long serialVersionUID = 1L;
  public static final int PALETTE_SIZE = 256;
  static final RGBColor BLACK = new RGBColor(0, 0, 0);
  private int highestIdx = -1;
  private boolean modified = true;
  @AnimAware
  private int modRed;
  @AnimAware
  private int modGreen;
  @AnimAware
  private int modBlue;
  @AnimAware
  private int modShift;
  private final MotionCurve modShiftCurve = new MotionCurve();
  @AnimAware
  private int modHue;
  @AnimAware
  private int modContrast;
  @AnimAware
  private int modGamma;
  @AnimAware
  private int modBrightness;
  @AnimAware
  private int modSaturation;
  @AnimAware
  private int modSwapRGB;
  @AnimAware
  private int modFrequency = 1;
  @AnimAware
  private int modBlur;
  private String flam3Number; // imported from flam3, just for display
  private String flam3Name; // imported from flam3, just for display

  private Map<Integer, RGBColor> rawColors = new HashMap<Integer, RGBColor>();

  private final RGBColor[] transformedColors = new RGBColor[PALETTE_SIZE];

  private GradientSelectionProvider selectionProvider = new DefaultGradientSelectionProvider();

  // only required to add the feature of creating color-curves by random-gradient-generators without changing (=breaking) the api for existing scripts
  private int gradHueX[];
  private double gradHue[];
  private int gradSaturationX[];
  private double gradSaturation[];
  private int gradLuminosityX[];
  private double gradLuminosity[];

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
    RGBColor res = transformedColors[pIdx];
    return res != null ? res : BLACK;
  }

  public RGBColor getRawColor(int pIdx) {
    RGBColor res = rawColors.get(pIdx);
    return res != null ? res : BLACK;
  }

  public RenderColor[] createRenderPalette(double pWhiteLevel) {
    transformColors();
    RenderColor res[] = new RenderColor[PALETTE_SIZE];
    for (int i = 0; i < res.length; i++) {
      RGBColor color = transformedColors[i];
      if (color != null) {
        res[i] = new RenderColor(pWhiteLevel, color);
      } else {
        int r, g, b;
        RGBColor leftColor = null, rightColor = null;
        int leftIdx = i, rightIdx = i;
        while (leftIdx-- >= 0) {
          leftColor = transformedColors[leftIdx];
          if (leftColor != null) {
            break;
          }
        }
        while (rightIdx++ < PALETTE_SIZE) {
          rightColor = transformedColors[rightIdx];
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
        res[i] = new RenderColor(pWhiteLevel, r, g, b);
      }
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
    int startIdx = selectionProvider.getFrom();
    int endIdx = selectionProvider.getTo();
    if (modified) {
      for (int i = 0; i < transformedColors.length; i++) {
        transformedColors[i] = null;
      }
      for (int i = 0; i < PALETTE_SIZE; i++) {
        RGBColor color = getRawColor(i);
        int idx = i + modShift;
        if (idx < 0) {
          idx += PALETTE_SIZE;
        }
        else if (idx >= PALETTE_SIZE) {
          idx -= PALETTE_SIZE;
        }
        transformedColors[idx] = new RGBColor(color.getRed(), color.getGreen(), color.getBlue());
      }
      if (modFrequency > 1) {
        RGBColor[] newTransformedColors = new RGBColor[PALETTE_SIZE];
        System.arraycopy(transformedColors, 0, newTransformedColors, 0, PALETTE_SIZE);
        int n = PALETTE_SIZE / modFrequency;
        for (int j = 0; j < modFrequency; j++) {
          for (int i = 0; i < n; i++) {
            int idx = i + j * n;
            if (idx < PALETTE_SIZE) {
              newTransformedColors[idx] = transformedColors[i * modFrequency];
            }
          }
        }
        System.arraycopy(newTransformedColors, 0, transformedColors, 0, PALETTE_SIZE);
        newTransformedColors = null;
      }
      if (modBlur > 0) {
        RGBColor[] newTransformedColors = new RGBColor[PALETTE_SIZE];
        System.arraycopy(transformedColors, 0, newTransformedColors, 0, PALETTE_SIZE);
        for (int i = 0; i < PALETTE_SIZE; i++) {
          int r = 0;
          int g = 0;
          int b = 0;
          int n = -1;
          for (int j = i - modBlur; j <= i + modBlur; j++) {
            n++;
            int k = (PALETTE_SIZE + j) % PALETTE_SIZE;
            if (k != i) {
              RGBColor color = transformedColors[k];
              r += color.getRed();
              g += color.getGreen();
              b += color.getBlue();
            }
          }
          if (n != 0) {
            RGBColor color = new RGBColor(Tools.limitColor(r / n), Tools.limitColor(g / n), Tools.limitColor(b / n));
            newTransformedColors[i] = color;
          }
        }
        System.arraycopy(newTransformedColors, 0, transformedColors, 0, PALETTE_SIZE);
        newTransformedColors = null;
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
      for (int i = startIdx; i <= endIdx; i++) {
        RGBColor color = transformedColors[i];
        pixel.setARGBValue(img.getARGBValue(i, 0));
        color.setRed(pixel.r);
        color.setGreen(pixel.g);
        color.setBlue(pixel.b);
      }
      modified = false;
    }
  }

  public RGBColor[] getTransformedColors() {
    transformColors();
    return transformedColors;
  }

  @Override
  public RGBPalette makeCopy() {
    RGBPalette res = new RGBPalette();
    res.assign(this);
    res.assignRawColors(this);
    return res;
  }

  public RGBPalette makeDeepCopy() {
    RGBPalette res = new RGBPalette();
    res.assign(this);
    res.assignRawColors(this);
    return res;
  }

  public void assignRawColors(RGBPalette pRGBPalette) {
    rawColors.clear();
    for (Integer key : pRGBPalette.rawColors.keySet()) {
      RGBColor newColor = pRGBPalette.rawColors.get(key).makeCopy();
      rawColors.put(key, newColor);
    }
  }

  @Override
  public void assign(RGBPalette pRGBPalette) {
    highestIdx = pRGBPalette.highestIdx;
    modified = pRGBPalette.modified;
    modRed = pRGBPalette.modRed;
    modGreen = pRGBPalette.modGreen;
    modBlue = pRGBPalette.modBlue;
    modShift = pRGBPalette.modShift;
    modShiftCurve.assign(pRGBPalette.modShiftCurve);
    modHue = pRGBPalette.modHue;
    modContrast = pRGBPalette.modContrast;
    modGamma = pRGBPalette.modGamma;
    modBrightness = pRGBPalette.modBrightness;
    modSaturation = pRGBPalette.modSaturation;
    modSwapRGB = pRGBPalette.modSwapRGB;
    modFrequency = pRGBPalette.modFrequency;
    modBlur = pRGBPalette.modBlur;
    flam3Name = pRGBPalette.flam3Name;
    flam3Number = pRGBPalette.flam3Number;
    /*
        rawColors.clear();
        for (Integer key : pRGBPalette.rawColors.keySet()) {
          RGBColor newColor = pRGBPalette.rawColors.get(key).makeCopy();
          rawColors.put(key, newColor);
        }
      */
    // raw colors may not be modified
    rawColors.clear();
    rawColors.putAll(pRGBPalette.rawColors);

    System.arraycopy(pRGBPalette.transformedColors, 0, transformedColors, 0, PALETTE_SIZE);
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
        return flam3Number;
      }
    }
    else if (flam3Name != null && flam3Name.length() > 0) {
      return flam3Name;
    }
    else {
      return "untitled gradient";
    }
  }

  @Override
  public boolean isEqual(RGBPalette pSrc) {
    // flam3Number and flam3Name can not be changed
    if ((modRed != pSrc.modRed) || (modGreen != pSrc.modGreen) || (modBlue != pSrc.modBlue) ||
        (modShift != pSrc.modShift) || !modShiftCurve.isEqual(pSrc.modShiftCurve) ||
        (modHue != pSrc.modHue) || (modContrast != pSrc.modContrast) ||
        (modGamma != pSrc.modGamma) || (modBrightness != pSrc.modBrightness) || (modSaturation != pSrc.modSaturation) ||
        (modSwapRGB != pSrc.modSwapRGB) || (modFrequency != pSrc.modFrequency) || (modBlur != pSrc.modBlur) ||
        (rawColors.size() != pSrc.rawColors.size())) {
      return false;
    }
    if (rawColors.size() > 0) {
      for (int i = 0; i < PALETTE_SIZE; i++) {
        RGBColor color = rawColors.get(i);
        RGBColor srcColor = pSrc.rawColors.get(i);
        if (!color.isEqual(srcColor)) {
          return false;
        }
      }
    }
    return true;
  }

  private void doSort(int pStartIdx, int pEndIdx) {
    List<RGBColor> colors = new ArrayList<RGBColor>();
    for (int i = pStartIdx; i <= pEndIdx; i++) {
      colors.add(rawColors.get(i));
    }
    Collections.sort(colors, new Comparator<RGBColor>() {

      @Override
      public int compare(RGBColor o1, RGBColor o2) {
        return o1.compareToRGBColor(o2);
      }

    });
    for (int i = pStartIdx; i <= pEndIdx; i++) {
      rawColors.put(Integer.valueOf(i), colors.get(i - pStartIdx));
    }
    modified = true;
  }

  public void sort() {
    doSort(selectionProvider.getFrom(), selectionProvider.getTo());
  }

  private void doNegativeColors(int pStartIdx, int pEndIdx) {
    for (int i = pStartIdx; i <= pEndIdx; i++) {
      RGBColor color = rawColors.get(Integer.valueOf(i));
      if (color != null) {
        color.setRed(255 - color.getRed());
        color.setGreen(255 - color.getGreen());
        color.setBlue(255 - color.getBlue());
      }
    }
    modified = true;
  }

  public void negativeColors() {
    doNegativeColors(selectionProvider.getFrom(), selectionProvider.getTo());
  }

  public void reverseColors() {
    doReverseColors(selectionProvider.getFrom(), selectionProvider.getTo());
  }

  private void doReverseColors(int pStartIdx, int pEndIdx) {
    Map<Integer, RGBColor> newRawColors = new HashMap<Integer, RGBColor>();
    newRawColors.putAll(rawColors);
    for (int i = pStartIdx; i <= pEndIdx; i++) {
      RGBColor color = rawColors.get(i);
      //      System.out.println(pStartIdx + " " + pEndIdx + ": " + i + "->" + Integer.valueOf(pEndIdx - (i - pStartIdx)));
      if (color != null) {
        newRawColors.put(Integer.valueOf(pEndIdx - (i - pStartIdx)), color);
      }
    }
    rawColors = newRawColors;
    modified = true;
  }

  public int getModFrequency() {
    return modFrequency;
  }

  public void setModFrequency(int modFrequency) {
    this.modFrequency = modFrequency;
    modified = true;
  }

  public int getModBlur() {
    return modBlur;
  }

  public void setModBlur(int modBlur) {
    this.modBlur = modBlur;
    modified = true;
  }

  public void fadeRange(int pStartIdx, int pEndIdx) {
    if (pStartIdx < pEndIdx + 1) {
      RGBColor lColor = getRawColor(pStartIdx);
      RGBColor rColor = getRawColor(pEndIdx);
      double scl = 1.0 / (pEndIdx - pStartIdx);
      for (int i = pStartIdx + 1; i < pEndIdx; i++) {
        double x = scl * (i - pStartIdx);
        int r = Tools.roundColor((double) lColor.getRed() + ((double) (rColor.getRed() - lColor.getRed())) * x);
        int g = Tools.roundColor((double) lColor.getGreen() + ((double) (rColor.getGreen() - lColor.getGreen())) * x);
        int b = Tools.roundColor((double) lColor.getBlue() + ((double) (rColor.getBlue() - lColor.getBlue())) * x);
        rawColors.put(Integer.valueOf(i), new RGBColor(r, g, b));
      }
      modified = true;
    }
  }

  public void setSelectionProvider(GradientSelectionProvider pSelectionProvider) {
    selectionProvider = pSelectionProvider;
    modified = true;
  }

  public void monochrome(int pStartIdx, int pEndIdx) {
    if (pStartIdx < pEndIdx + 1) {
      Pixel rgbPixel = new Pixel();
      HSLTransformer.HSLPixel hslPixel = new HSLTransformer.HSLPixel();
      double avgHue = 0.0;
      int cnt = 0;
      for (int i = pStartIdx; i <= pEndIdx; i++) {
        RGBColor color = getRawColor(i);
        rgbPixel.setRGB(color.getRed(), color.getGreen(), color.getBlue());
        HSLTransformer.rgb2hsl(rgbPixel, hslPixel);
        avgHue += hslPixel.hue;
        cnt++;
      }
      avgHue /= (double) cnt;
      for (int i = pStartIdx; i <= pEndIdx; i++) {
        RGBColor color = getRawColor(i);
        rgbPixel.setRGB(color.getRed(), color.getGreen(), color.getBlue());
        HSLTransformer.rgb2hsl(rgbPixel, hslPixel);
        hslPixel.hue = avgHue;
        HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
        color.setRed(rgbPixel.r);
        color.setGreen(rgbPixel.g);
        color.setBlue(rgbPixel.b);
        rawColors.put(i, color);
      }
      modified = true;
    }
  }

  public void setColors(Map<Integer, RGBColor> pColors, boolean pDoInterpolate, boolean pDoFade) {
    if (pColors.size() > 0) {
      int maxIdx = -1;
      for (Integer idx : pColors.keySet()) {
        if (idx > maxIdx) {
          maxIdx = idx;
        }
      }
      int srcSize = maxIdx + 1;

      if (pDoInterpolate) {
        setColorsSmooth(pColors, srcSize, pDoFade);
      }
      else {
        setColorsNonInterpolate(pColors, srcSize);
      }
    }
  }

  private void setColorsNonInterpolate(Map<Integer, RGBColor> pColors, int pSrcSize) {
    int dstSize = PALETTE_SIZE;

    for (int i = 0; i < dstSize; i++) {
      double coord = (double) (i * (pSrcSize - 1)) / (double) (dstSize - 1);
      int colorIdx = (int) (coord);
      RGBColor lColor = pColors.get(colorIdx);
      if (lColor == null) {
        lColor = BLACK;
      }
      RGBColor rColor = pColors.get(colorIdx + 1);
      if (rColor == null) {
        rColor = BLACK;
      }
      double x = coord - colorIdx;

      int r = Tools.roundColor((double) lColor.getRed() + ((double) (rColor.getRed() - lColor.getRed())) * x);
      int g = Tools.roundColor((double) lColor.getGreen() + ((double) (rColor.getGreen() - lColor.getGreen())) * x);
      int b = Tools.roundColor((double) lColor.getBlue() + ((double) (rColor.getBlue() - lColor.getBlue())) * x);
      rawColors.put(Integer.valueOf(i), new RGBColor(r, g, b));
    }
  }

  private void setColorsSmooth(Map<Integer, RGBColor> pColors, int pSrcSize, boolean pDoSmooth) {
    int dstSize = PALETTE_SIZE;

    for (int i = 0; i < dstSize; i++) {
      double coord = (double) (i * (pSrcSize - 1)) / (double) (dstSize - 1);

      RGBColor lColor = null;
      int lColorIdx = 0;
      for (int j = (int) coord; j >= 0; j--) {
        lColor = pColors.get(j);
        if (lColor != null) {
          lColorIdx = j;
          break;
        }
      }
      if (lColor == null) {
        lColor = BLACK;
        lColorIdx = 0;
      }

      RGBColor rColor = null;
      int rColorIdx = 0;
      for (int j = ((int) coord) + 1; j < pSrcSize; j++) {
        rColor = pColors.get(j);
        if (rColor != null) {
          rColorIdx = j;
          break;
        }
      }
      if (rColor == null) {
        rColor = BLACK;
        rColorIdx = pSrcSize - 1;
      }

      double srcRange = rColorIdx - lColorIdx + 1;
      double x = (coord - lColorIdx) / srcRange;
      if (pDoSmooth) x = x * x * (3 - 2 * x);
      
      int r = Tools.roundColor((double) lColor.getRed() + ((double) (rColor.getRed() - lColor.getRed())) * x);
      int g = Tools.roundColor((double) lColor.getGreen() + ((double) (rColor.getGreen() - lColor.getGreen())) * x);
      int b = Tools.roundColor((double) lColor.getBlue() + ((double) (rColor.getBlue() - lColor.getBlue())) * x);
      rawColors.put(Integer.valueOf(i), new RGBColor(r, g, b));

    }
  }

  public void applyBalancing() {
    modified = true;
    int saveModSwapRGB = modSwapRGB;
    int saveModFrequency = modFrequency;
    int saveModBlur = modBlur;
    try {
      modSwapRGB = 0;
      modFrequency = 0;
      modBlur = 0;

      transformColors();
      rawColors.clear();
      for (int i = 0; i < transformedColors.length; i++) {
        rawColors.put(Integer.valueOf(i), transformedColors[i]);
      }

      modRed = 0;
      modGreen = 0;
      modBlue = 0;
      modShift = 0;
      modHue = 0;
      modContrast = 0;
      modGamma = 0;
      modBrightness = 0;
      modSaturation = 0;
      modified = true;
    }
    finally {
      modSwapRGB = saveModSwapRGB;
      modFrequency = saveModFrequency;
      modBlur = saveModBlur;
    }
  }

  public void applyTX() {
    int saveModRed = modRed;
    int saveModGreen = modGreen;
    int saveModBlue = modBlue;
    int saveModShift = modShift;
    int saveModHue = modHue;
    int saveModContrast = modContrast;
    int saveModGamma = modGamma;
    int saveModBrightness = modBrightness;
    int saveModSaturation = modSaturation;
    try {
      modRed = 0;
      modGreen = 0;
      modBlue = 0;
      modShift = 0;
      modHue = 0;
      modContrast = 0;
      modGamma = 0;
      modBrightness = 0;
      modSaturation = 0;

      transformColors();
      rawColors.clear();
      for (int i = 0; i < transformedColors.length; i++) {
        rawColors.put(Integer.valueOf(i), transformedColors[i]);
      }

      modSwapRGB = 0;
      modFrequency = 0;
      modBlur = 0;
      modified = true;
    }
    finally {
      modRed = saveModRed;
      modGreen = saveModGreen;
      modBlue = saveModBlue;
      modShift = saveModShift;
      modHue = saveModHue;
      modContrast = saveModContrast;
      modGamma = saveModGamma;
      modBrightness = saveModBrightness;
      modSaturation = saveModSaturation;
    }
  }

  public void shiftColors(int pShift) {
    if (pShift != 0) {
      while (pShift < -255) {
        pShift += 256;
      }
      while (pShift > 255) {
        pShift -= 256;
      }

      Map<Integer, RGBColor> newColors = new HashMap<Integer, RGBColor>();
      for (int i = 0; i < PALETTE_SIZE; i++) {
        RGBColor color = getRawColor(i);
        int idx = i + pShift;
        if (idx < 0) {
          idx += PALETTE_SIZE;
        }
        else if (idx >= PALETTE_SIZE) {
          idx -= PALETTE_SIZE;
        }
        newColors.put(idx, new RGBColor(color.getRed(), color.getGreen(), color.getBlue()));
      }
      rawColors = null;
      rawColors = newColors;
      modified = true;
    }
  }

  public int[] getGradHueX() {
    return gradHueX;
  }

  public void setGradHueX(int[] gradHueX) {
    this.gradHueX = gradHueX;
  }

  public double[] getGradHue() {
    return gradHue;
  }

  public void setGradHue(double[] gradHue) {
    this.gradHue = gradHue;
  }

  public int[] getGradSaturationX() {
    return gradSaturationX;
  }

  public void setGradSaturationX(int[] gradSaturationX) {
    this.gradSaturationX = gradSaturationX;
  }

  public double[] getGradSaturation() {
    return gradSaturation;
  }

  public void setGradSaturation(double[] gradSaturation) {
    this.gradSaturation = gradSaturation;
  }

  public int[] getGradLuminosityX() {
    return gradLuminosityX;
  }

  public void setGradLuminosityX(int[] gradLuminosityX) {
    this.gradLuminosityX = gradLuminosityX;
  }

  public double[] getGradLuminosity() {
    return gradLuminosity;
  }

  public void setGradLuminosity(double[] gradLuminosity) {
    this.gradLuminosity = gradLuminosity;
  }
}
