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
import java.util.List;
import java.util.StringTokenizer;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBPalette;

public class MapPaletteReader {

  public List<RGBPalette> readPalettes(String pFilename) {
    try {
      String mapData = new String(Tools.readFile(pFilename));
      return readPaletteFromMapData(mapData, pFilename);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<RGBPalette> readPaletteFromMapData(String pMapData, String pFilename) {
    List<RGBPalette> res = new ArrayList<RGBPalette>();
    RGBPalette gradient = new RGBPalette();
    res.add(gradient);
    gradient.setFlam3Name(new File(pFilename).getName());
    StringTokenizer tokenizer = new StringTokenizer(pMapData, "\n\r");
    int idx = 0;
    while (tokenizer.hasMoreElements()) {
      String line = tokenizer.nextToken();
      StringTokenizer lineTokenizer = new StringTokenizer(line, " ");
      int r = Integer.parseInt((String) lineTokenizer.nextElement());
      int g = Integer.parseInt((String) lineTokenizer.nextElement());
      int b = Integer.parseInt((String) lineTokenizer.nextElement());
      if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
        if (idx > 255) {
          throw new RuntimeException("Invalid color index <" + idx + "> in file <" + pFilename + ">");
        }
        gradient.setColor(idx++, r, g, b);
      }
      else {
        throw new RuntimeException("Invalid color values <" + line + ">");
      }
    }
    return res;
  }
}
