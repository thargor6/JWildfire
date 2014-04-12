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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;

public class SmoothRandomGradientGenerator extends RandomGradientGenerator {

  @Override
  public String getName() {
    return "Smooth";
  }

  @Override
  public List<RGBColor> generateKeyFrames(int pKeyFrameCount) {
    List<RGBColor> keyFrames = new ArrayList<RGBColor>();
    int lastR = 0, lastG = 0, lastB = 0;
    int r, g, b;
    for (int i = 0; i < pKeyFrameCount; i++) {
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

}
