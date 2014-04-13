/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.randomgradient;

import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public abstract class RandomGradientGenerator {

  public abstract String getName();

  public abstract List<RGBColor> generateKeyFrames(int pKeyFrameCount);

  public static final RGBPalette generatePalette(List<RGBColor> pKeyFrames, boolean pFadeColors) {
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

  public final RGBPalette generatePalette(int pKeyFrameCount, boolean pFadeColors) {
    List<RGBColor> keyFrames = generateKeyFrames(pKeyFrameCount);
    return generatePalette(keyFrames, pFadeColors);
  }

}
