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
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.image.Pixel;
import org.jwildfire.transform.HSLTransformer;
import org.jwildfire.transform.HSLTransformer.HSLPixel;

public class StripesRandomGradientGenerator extends RandomGradientGenerator {

  @Override
  public String getName() {
    return "Stripes";
  }

  @Override
  public List<RGBColor> generateKeyFrames(int pKeyFrameCount) {
    RGBColor stripeColor = createStripeColor();
    List<RGBColor> baseColors = new StrongHueRandomGradientGenerator().generateKeyFrames(pKeyFrameCount);
    List<RGBColor> res = new ArrayList<RGBColor>();
    int stripeWidth = 1 + Tools.randomInt(2);
    int colorWidth = 3 + Tools.randomInt(5);
    int colorIdx = 0;
    while (res.size() < RGBPalette.PALETTE_SIZE) {
      for (int i = 0; i < stripeWidth; i++) {
        res.add(stripeColor);
      }

      RGBColor colorLeft = baseColors.get(colorIdx++);
      if (colorIdx >= baseColors.size()) {
        colorIdx = 0;
      }
      RGBColor colorRight = baseColors.get(colorIdx++);
      if (colorIdx >= baseColors.size()) {
        colorIdx = 0;
      }

      res.add(colorLeft);
      for (int i = 1; i < colorWidth; i++) {
        double scl = (double) i / (double) (colorWidth - 1);
        double r = colorLeft.getRed() + (colorRight.getRed() - colorLeft.getRed()) * scl;
        double g = colorLeft.getGreen() + (colorRight.getGreen() - colorLeft.getGreen()) * scl;
        double b = colorLeft.getBlue() + (colorRight.getBlue() - colorLeft.getBlue()) * scl;
        RGBColor color = new RGBColor(Tools.roundColor(r), Tools.roundColor(g), Tools.roundColor(b));
        res.add(color);
      }

    }
    while (res.size() >= RGBPalette.PALETTE_SIZE) {
      res.remove(res.size() - 1);
    }
    return res;
  }

  private RGBColor createStripeColor() {
    HSLPixel hslPixel = new HSLPixel();
    hslPixel.saturation = Math.random() * 0.1 + 0.8999;
    double rnd = Math.random() + Math.random() + Math.random() + Math.random() + Math.random();
    if (rnd > 4.0)
      hslPixel.hue = 0.8 + (rnd - 4.0) / 7.0;
    else if (rnd > 3.0)
      hslPixel.hue = 0.2 + (rnd - 3.0) / 7.0;
    else if (rnd > 2.0)
      hslPixel.hue = 0.0 + (rnd - 2.0) / 7.0;
    else if (rnd > 1.0)
      hslPixel.hue = 0.6 + (rnd - 1.0) / 7.0;
    else
      hslPixel.hue = 0.4 + rnd / 7.0;
    hslPixel.luminosity = Math.random() * 0.1 + 0.8999;
    Pixel rgbPixel = new Pixel();
    HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
    return new RGBColor(rgbPixel.r, rgbPixel.g, rgbPixel.b);
  }

}
