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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.image.Pixel;

public class UgrPaletteReader {

  public List<RGBPalette> readPalettes(String pFilename) {
    try {
      String gradientUgr = Tools.readUTF8Textfile(pFilename);
      return readPalettesFromUgr(gradientUgr);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<RGBPalette> readPalettesFromUgr(String pUgr) {
    List<RGBPalette> res = new ArrayList<RGBPalette>();
    int pPalettes = 0;
    while (true) {
      String paletteUgr;
      {
        int ps = pUgr.indexOf("{", pPalettes);
        if (ps < 0)
          break;
        int pe = pUgr.indexOf("}", ps + 1);
        if (pe < 0)
          break;
        pPalettes = pe + 1;
        paletteUgr = pUgr.substring(ps, pPalettes);
      }

      RGBPalette palette = new RGBPalette();
      res.add(palette);
      parsePalette(palette, paletteUgr);
    }
    return res;
  }

  private void parsePalette(RGBPalette pPalette, String pUgr) {
    Map<Integer, RGBColor> colors = new HashMap<Integer, RGBColor>();
    StringTokenizer tokenizer = new StringTokenizer(pUgr, "\n\r");
    int p1, p2;
    Pixel pixel = new Pixel();
    while (tokenizer.hasMoreElements()) {
      String line = tokenizer.nextToken();
      if (line.indexOf("title=") >= 0) {
        StringTokenizer lineTokenizer = new StringTokenizer(line, "\"");
        if (lineTokenizer.hasMoreElements()) {
          lineTokenizer.nextElement();
          if (lineTokenizer.hasMoreElements()) {
            pPalette.setFlam3Name((String) lineTokenizer.nextElement());
          }
        }
      }
      else if ((p1 = line.indexOf("index=")) >= 0 && (p2 = line.indexOf("color=")) >= 0) {
        int p1e = line.indexOf(" ", p1);
        if (p1e > p1) {
          int index = Integer.parseInt(line.substring(p1 + 6, p1e));
          int colorValue = Integer.parseInt(line.substring(p2 + 6, line.length()));
          pixel.setARGBValue(colorValue);
          RGBColor color = new RGBColor(pixel.r, pixel.g, pixel.b);
          colors.put(index, color);
        }
      }
    }
    pPalette.setColors(colors);
  }
}
