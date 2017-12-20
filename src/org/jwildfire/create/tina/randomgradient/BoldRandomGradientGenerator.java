/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public class BoldRandomGradientGenerator extends RandomGradientGenerator {

  @Override
  public String getName() {
    return "Bold";
  }

  @Override
  public List<RGBColor> generateKeyFrames(int pKeyFrameCount) {
    List<RGBColor> baseColors = new StrongHueRandomGradientGenerator().generateKeyFrames(128);
    RGBColor black = new RGBColor();
    List<RGBColor> res = new ArrayList<RGBColor>();
    int stripeWidth = 12 + Tools.randomInt(24);
    int blackWidth = Math.random() < 0.25 ? 8 + Tools.randomInt(16) : 0;
    int stripeIdx = 0, colorIdx = 0;
    while (res.size() < RGBPalette.PALETTE_SIZE) {
      RGBColor stripeColor;
      int width;
      if (stripeIdx++ % 2 == 0 || blackWidth == 0) {
        stripeColor = baseColors.get(colorIdx++);
        width = stripeWidth;
      }
      else {
        stripeColor = black;
        width = blackWidth;
      }
      for (int i = 0; i < width; i++) {
        res.add(stripeColor);
      }
    }
    while (res.size() >= RGBPalette.PALETTE_SIZE) {
      res.remove(res.size() - 1);
    }
    return res;
  }

}
