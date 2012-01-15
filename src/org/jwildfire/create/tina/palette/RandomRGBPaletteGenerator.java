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

public class RandomRGBPaletteGenerator {

  public List<RGBColor> generateKeyFrames(int pKeyFrames) {
    if (pKeyFrames < 0 || pKeyFrames > RGBPalette.PALETTE_SIZE)
      throw new IllegalArgumentException(String.valueOf(pKeyFrames));
    List<RGBColor> keyFrames = new ArrayList<RGBColor>();
    for (int i = 0; i < pKeyFrames; i++) {
      int r = Tools.roundColor(256.0 * Math.random());
      int g = Tools.roundColor(256.0 * Math.random());
      int b = Tools.roundColor(256.0 * Math.random());
      double rnd = Math.random();
      if (rnd < 0.1) {
        b = g;
      }
      else if (r < 0.2) {
        g = r;
      }
      else if (r < 0.3) {
        r = b;
      }
      else if (r < 0.4) {
        b = g = r;
      }
      else if (Math.random() < 0.5) {
        g /= 3;
        b /= 2;
      }
      RGBColor col = new RGBColor(r, g, b);
      keyFrames.add(col);
    }
    return keyFrames;
  }

  public RGBPalette generatePalette(List<RGBColor> pKeyFrames) {
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
        RGBColor rColor = pKeyFrames.get(lIdx + 1);
        int r = Tools.roundColor((double) lColor.getRed() + ((double) (rColor.getRed() - lColor.getRed())) * relX);
        int g = Tools.roundColor((double) lColor.getGreen() + ((double) (rColor.getGreen() - lColor.getGreen())) * relX);
        int b = Tools.roundColor((double) lColor.getBlue() + ((double) (rColor.getBlue() - lColor.getBlue())) * relX);
        res.addColor(r, g, b);
        //      System.out.println(i + ": " + r + " " + g + " " + b);
      }
    }
    return res;
  }

  public RGBPalette generatePalette(int pKeyFrames) {
    List<RGBColor> keyFrames = generateKeyFrames(pKeyFrames);
    return generatePalette(keyFrames);
  }

}
