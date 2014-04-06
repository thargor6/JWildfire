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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.transform.HSLTransformer;
import org.jwildfire.transform.HSLTransformer.HSLPixel;

public class RandomRGBPaletteGenerator {

  public List<RGBColor> generateKeyFrames(int pKeyFrames) {
    if (pKeyFrames < 0 || pKeyFrames > RGBPalette.PALETTE_SIZE)
      throw new IllegalArgumentException(String.valueOf(pKeyFrames));
    if (Math.random() < 0.25) {
      return generateKeyFrames_random(pKeyFrames);
    }
    else if (Math.random() < 0.75) {
      return generateKeyFrames_hsl(pKeyFrames);
    }
    else {
      return generateKeyFrames_hsl_monochrome(pKeyFrames);
    }
  }

  public List<RGBColor> generateKeyFrames_hsl(int pKeyFrames) {
    HSLPixel hslPixel = new HSLPixel();
    Pixel rgbPixel = new Pixel();
    List<RGBColor> keyFrames = new ArrayList<RGBColor>();

    hslPixel.saturation = Math.random() * 0.1 + 0.8999;
    for (int i = 0; i < pKeyFrames; i++) {
      hslPixel.luminosity = Math.random() * 0.90;
      hslPixel.hue = Math.random() * Math.random();
      HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
      RGBColor col = new RGBColor(rgbPixel.r, rgbPixel.g, rgbPixel.b);
      keyFrames.add(col);
    }
    return keyFrames;
  }

  public List<RGBColor> generateKeyFrames_hsl_monochrome(int pKeyFrames) {
    HSLPixel hslPixel = new HSLPixel();
    Pixel rgbPixel = new Pixel();
    List<RGBColor> keyFrames = new ArrayList<RGBColor>();

    hslPixel.saturation = Math.random() * 0.3 + 0.6999;
    hslPixel.hue = Math.random() * Math.random();
    for (int i = 0; i < pKeyFrames; i++) {
      hslPixel.luminosity = Math.random() * 0.99;
      HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
      RGBColor col = new RGBColor(rgbPixel.r, rgbPixel.g, rgbPixel.b);
      keyFrames.add(col);
    }
    return keyFrames;
  }

  public List<RGBColor> generateKeyFrames_random(int pKeyFrames) {
    List<RGBColor> keyFrames = new ArrayList<RGBColor>();
    int lastR = 0, lastG = 0, lastB = 0;
    int r, g, b;
    for (int i = 0; i < pKeyFrames; i++) {
      while (true) {
        r = Tools.roundColor(256.0 * Math.random());
        g = Tools.roundColor(256.0 * Math.random());
        b = Tools.roundColor(256.0 * Math.random());
        double diff = Math.abs(r - lastR) * 0.299 + Math.abs(g - lastG) * 0.588 + Math.abs(b - lastB) * 0.1130;
        if (diff > 66)
          break;
      }
      RGBColor col = new RGBColor(r, g, b);
      lastR = r;
      lastG = g;
      lastB = b;
      keyFrames.add(col);
    }
    return keyFrames;
  }

  public RGBPalette generatePalette(List<RGBColor> pKeyFrames, boolean pFadeColors) {
    RGBPalette res = new RGBPalette();
    if (pKeyFrames.size() == 1) {
      RGBColor c = pKeyFrames.get(0);
      for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
        res.addColor(c.getRed(), c.getGreen(), c.getBlue());
      }
    }
    else {
      double idxScl = (double) (RGBPalette.PALETTE_SIZE) / (double) (pKeyFrames.size() - 1);
      for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
        double x = (double) i / idxScl;
        int lIdx = (int) x;
        double relX = x - (double) lIdx;
        RGBColor lColor = pKeyFrames.get(lIdx);
        int r, g, b;
        if (pFadeColors) {
          RGBColor rColor = pKeyFrames.get(lIdx + 1);
          r = Tools.roundColor((double) lColor.getRed() + ((double) (rColor.getRed() - lColor.getRed())) * relX);
          g = Tools.roundColor((double) lColor.getGreen() + ((double) (rColor.getGreen() - lColor.getGreen())) * relX);
          b = Tools.roundColor((double) lColor.getBlue() + ((double) (rColor.getBlue() - lColor.getBlue())) * relX);
        }
        else {
          r = lColor.getRed();
          g = lColor.getGreen();
          b = lColor.getBlue();
        }
        res.addColor(r, g, b);
        //      System.out.println(i + ": " + r + " " + g + " " + b);
      }
    }
    return res;
  }

  public RGBPalette generatePalette(int pKeyFrames, boolean pFadeColors) {
    List<RGBColor> keyFrames = generateKeyFrames(pKeyFrames);
    return generatePalette(keyFrames, pFadeColors);
  }

}
