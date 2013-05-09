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
package org.jwildfire.create.tina.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;

public class ImgPaletteReader {

  public List<RGBPalette> readPalettes(String pFilename) {
    try {
      String mapData = new String(Tools.readFile(pFilename));
      return readPaletteFromMapData(mapData, pFilename);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<RGBPalette> readPaletteFromMapData(String pMapData, String pFilename) throws Exception {
    List<RGBPalette> res = new ArrayList<RGBPalette>();
    RGBPalette gradient = new RGBPalette();
    res.add(gradient);
    gradient.setFlam3Name(new File(pFilename).getName());
    SimpleImage img = new ImageReader().loadImage(pFilename);
    if (img.getImageWidth() > 0 && img.getImageHeight() > 0) {
      Map<Integer, RGBColor> colors = new HashMap<Integer, RGBColor>();
      Pixel rgbPixel = new Pixel();
      for (int i = 0; i < img.getImageWidth(); i++) {
        rgbPixel.setARGBValue(img.getARGBValue(i, 0));
        RGBColor color = new RGBColor(rgbPixel.r, rgbPixel.g, rgbPixel.b);
        colors.put(i, color);
      }
      gradient.setColors(colors, false, false);
    }
    return res;
  }
}
