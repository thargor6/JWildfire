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

import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.image.Pixel;
import org.jwildfire.transform.HSLTransformer;
import org.jwildfire.transform.HSLTransformer.HSLPixel;

public class MonochromeRandomGradientGenerator extends RandomGradientGenerator {

  @Override
  public String getName() {
    return "Monochrome";
  }

  @Override
  public List<RGBColor> generateKeyFrames(int pKeyFrameCount) {
    HSLPixel hslPixel = new HSLPixel();
    Pixel rgbPixel = new Pixel();
    List<RGBColor> keyFrames = new ArrayList<RGBColor>();

    hslPixel.saturation = Math.random() * 0.3 + 0.6999;
    hslPixel.hue = Math.random() * Math.random();
    for (int i = 0; i < pKeyFrameCount; i++) {
      hslPixel.luminosity = Math.random() * 0.99;
      HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
      RGBColor col = new RGBColor(rgbPixel.r, rgbPixel.g, rgbPixel.b);
      keyFrames.add(col);
    }
    return keyFrames;
  }

}
