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

import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;
import org.jwildfire.transform.ScaleTransformer.Unit;

public class RGBPaletteRenderer {

  public SimpleImage renderHorizPalette(RGBPalette pPalette, int pWidth, int pHeight) {
    return renderHorizPalette(pPalette.getTransformedColors(), pWidth, pHeight);
  }

  public SimpleImage renderHorizPalette(RGBColor[] pColors, int pWidth, int pHeight) {
    if (pColors == null || pWidth < 1 || pHeight < 1)
      throw new IllegalArgumentException();
    SimpleImage img = new SimpleImage(RGBPalette.PALETTE_SIZE, pHeight);
    for (int i = 0; i < pColors.length; i++) {
      RGBColor color = pColors[i];
      if (color == null) {
        color = RGBPalette.BLACK;
      }
      int r = color.getRed();
      int g = color.getGreen();
      int b = color.getBlue();
      for (int j = 0; j < pHeight; j++) {
        img.setRGB(i, j, r, g, b);
      }
    }
    if (img.getImageWidth() != pWidth || img.getImageHeight() != pHeight) {
      ScaleTransformer scaleT = new ScaleTransformer();
      scaleT.setScaleWidth(pWidth);
      scaleT.setScaleHeight(pHeight);
      scaleT.setAspect(ScaleAspect.IGNORE);
      scaleT.setUnit(Unit.PIXELS);
      scaleT.transformImage(img);
    }
    return img;
  }

}
