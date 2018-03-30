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

  public static final RGBPalette generatePalette(List<RGBColor> pKeyFrames, boolean pFadeColors, boolean pUniformWidth) {
    RGBPalette res = new RGBPalette();
    if (pKeyFrames.size() == 1) {
      RGBColor c = pKeyFrames.get(0);
      for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
        res.addColor(c.getRed(), c.getGreen(), c.getBlue());
      }
    }
    else {
      // Compute uniform sizes for each color
      int[] kfCount = new int[pKeyFrames.size()];
      int sum1 = 0;
      double sum2 = 0.0;
      int n = pKeyFrames.size() - (pFadeColors ? 1 : 0);
      double idxScl = (double) (RGBPalette.PALETTE_SIZE) / (double) (n);
      for (int i = 0; i < n; i++) {
    	  kfCount[i] = (int) idxScl;
    	  sum1 += kfCount[i];
    	  sum2 += idxScl;
    	  if (sum2 - sum1 > 1) {
    		  kfCount[i]++;
    		  sum1++;
    	  }
      }
      kfCount[n-1] += RGBPalette.PALETTE_SIZE - sum1;
      
      // Randomize sizes
      if (!pUniformWidth && idxScl > 3) {
    	  for (int i = 0; i < n; i++) {
    		  int j = (int) (Math.random() * n);
    		  int min = (kfCount[i] < kfCount[j] ? kfCount[i] : kfCount[j]);
    		  int r = (int) (Math.random() * min - min / 2);
    		  kfCount[i] += r;
    		  kfCount[j] -= r;
    	  }
      }
      
      // Allocate colors
      int j = 0;
      int lIdx = 0;
      for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
    	RGBColor lColor = pKeyFrames.get(lIdx);
    	int r, g, b;
        if (pFadeColors) {
          double relX = (double) (j) / (double) kfCount[lIdx];
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
        j++;
        if (j >= kfCount[lIdx]) {
        	j = 0;
        	if (lIdx < n-1) lIdx ++;
        }
      }
    }
    return res;
  }

  public final RGBPalette generatePalette(int pKeyFrameCount, boolean pFadeColors, boolean pUniformWidth) {
    List<RGBColor> keyFrames = generateKeyFrames(pKeyFrameCount);
    return generatePalette(keyFrames, pFadeColors, pUniformWidth);
  }

}
