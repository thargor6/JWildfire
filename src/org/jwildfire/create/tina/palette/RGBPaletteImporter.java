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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;


public class RGBPaletteImporter {

  private static class ColorHistEntry implements Comparable<ColorHistEntry> {
    public int r, g, b;
    public int count;

    public ColorHistEntry(int pR, int pG, int pB) {
      r = pR;
      g = pG;
      b = pB;
      count = 1;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + b;
      result = prime * result + count;
      result = prime * result + g;
      result = prime * result + r;
      return result;
    }

    @Override
    public int compareTo(ColorHistEntry o) {
      if (count < o.count) {
        return 1;
      }
      else if (count > o.count) {
        return -1;
      }
      else {
        return 0;
      }
    }

  }

  public RGBPalette importFromImage(SimpleImage pImg) {
    RGBPalette res = new RGBPalette();
    Pixel pixel = new Pixel();

    Map<String, ColorHistEntry> colorMap = new HashMap<String, ColorHistEntry>();
    for (int i = 0; i < pImg.getImageHeight(); i++) {
      for (int j = 0; j < pImg.getImageWidth(); j++) {
        pixel.setARGBValue(pImg.getARGBValue(j, i));
        String key = pixel.r + "#" + pixel.g + "#" + pixel.b;
        ColorHistEntry hist = colorMap.get(key);
        if (hist == null) {
          hist = new ColorHistEntry(pixel.r, pixel.g, pixel.b);
          colorMap.put(key, hist);
        }
        else {
          hist.count++;
        }
      }
    }
    List<ColorHistEntry> colors = new ArrayList<ColorHistEntry>(colorMap.values());
    Collections.sort(colors);
    for (int i = 0; i < colors.size() && i < RGBPalette.PALETTE_SIZE; i++) {
      ColorHistEntry color = colors.get(i);
      res.addColor(color.r, color.g, color.b);

    }
    while (res.getSize() < RGBPalette.PALETTE_SIZE) {
      res.addColor(0, 0, 0);
    }
    return res;
  }

}
