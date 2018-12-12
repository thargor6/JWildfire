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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

public class MapGradientReader {

  public List<RGBPalette> readPalettes(String filename) {
    try {
      String mapData = new String(Tools.readFile(filename));
      return readPaletteFromMapData(mapData, filename);
    }
    catch (Exception ex) {
      throw new RuntimeException(filename, ex);
    }
  }

  public List<RGBPalette> readPalettesFromStream(InputStream inputStream, String filename) {
    try {
      String mapData = new String(Tools.getBytesFromInputStream(inputStream));
      return readPaletteFromMapData(mapData, filename);
    }
    catch (Exception ex) {
      throw new RuntimeException(filename, ex);
    }
  }

  public List<RGBPalette> readPaletteFromMapData(String pMapData, String pFilename) {
    List<RGBPalette> res = new ArrayList<RGBPalette>();
    RGBPalette gradient = new RGBPalette();
    res.add(gradient);
    gradient.setFlam3Name(new File(pFilename).getName());
    StringTokenizer tokenizer = new StringTokenizer(pMapData, "\n\r");
    int idx = 0;
    Map<Integer, RGBColor> colors = new HashMap<Integer, RGBColor>();
    while (tokenizer.hasMoreElements()) {
      String line = tokenizer.nextToken();
      StringTokenizer lineTokenizer = new StringTokenizer(line, "\t ");
      int r, g, b;
      try {
        r = Integer.parseInt(((String) lineTokenizer.nextElement()).trim());
        g = Integer.parseInt(((String) lineTokenizer.nextElement()).trim());
        b = Integer.parseInt(((String) lineTokenizer.nextElement()).trim());
      }
      catch (Exception ex) {
        break;
      }
      if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
        RGBColor color = new RGBColor(r, g, b);
        colors.put(idx++, color);
      }
      else {
        break;
      }
    }
    gradient.setColors(colors, false, false);
    return res;
  }

}
