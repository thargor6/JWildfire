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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.image.Pixel;

public class UgrGradientReader {

  public List<RGBPalette> readPalettes(String pFilename) {
    try {
      String gradientUgr = Tools.readUTF8Textfile(pFilename);
      return readPalettesFromUgr(gradientUgr);
    }
    catch (Exception ex) {
      throw new RuntimeException(pFilename, ex);
    }
  }

  public List<RGBPalette> readPalettesFromStream(InputStream inputStream, String filename) {
    try {
      String gradientUgr = new String(Tools.getBytesFromInputStream(inputStream), "UTF-8");
      return readPalettesFromUgr(gradientUgr);
    }
    catch (Exception ex) {
      throw new RuntimeException(filename, ex);
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
      if (paletteUgr.contains("title=\"") && paletteUgr.contains("color=") && paletteUgr.contains("index=")) {
        RGBPalette palette = new RGBPalette();
        res.add(palette);
        parsePalette(palette, paletteUgr);
      }
    }
    return res;
  }

  private void parsePalette(RGBPalette pPalette, String pUgr) {
    Map<Integer, RGBColor> colors = new HashMap<Integer, RGBColor>();
    StringTokenizer tokenizer = new StringTokenizer(pUgr, " \n\r");
    Pixel pixel = new Pixel();
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      String nextToken;
      if (token.startsWith("title=")) {
        while (token.charAt(token.length() - 1) != '"' && tokenizer.hasMoreTokens()) {
          token = token + "_" + tokenizer.nextToken();
        }
        StringTokenizer lineTokenizer = new StringTokenizer(token, "\"");
        if (lineTokenizer.hasMoreElements()) {
          lineTokenizer.nextElement();
          if (lineTokenizer.hasMoreElements()) {
            pPalette.setFlam3Name((String) lineTokenizer.nextElement());
          }
        }
      }
      else if (token.startsWith("index=") && tokenizer.hasMoreElements() && (nextToken = tokenizer.nextToken()).startsWith("color=")) {
        int index = Integer.parseInt(token.substring(6, token.length()));
        int colorValue = Integer.parseInt(nextToken.substring(6, nextToken.length()));
        pixel.setARGBValue(colorValue);
        RGBColor color = new RGBColor(pixel.b, pixel.g, pixel.r);
        colors.put(index, color);
      }
    }
    boolean doInterpolate = colors.size() < 256;
    boolean doSmooth = pUgr.contains("smooth=yes");
    final int maxIdx = 399;
    if (doInterpolate) {
      if (colors.get(0) == null) {
        colors.put(0, new RGBColor(0, 0, 0));
      }
      if (colors.get(maxIdx) == null) {
        colors.put(maxIdx, new RGBColor(0, 0, 0));
      }
    }
    else {
      RGBColor lastColor = new RGBColor(0, 0, 0);
      for (int i = 0; i <= maxIdx; i++) {
        RGBColor color = colors.get(i);
        if (color == null) {
          colors.put(i, lastColor);
        }
        else {
          lastColor = color;
        }
      }
    }
    pPalette.setColors(colors, doInterpolate, doSmooth);
  }

}
