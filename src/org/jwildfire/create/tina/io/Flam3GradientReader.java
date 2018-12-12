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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBPalette;

public class Flam3GradientReader implements RGBPaletteReader {

  @Override
  public List<RGBPalette> readPalettes(InputStream pInputStream) {
    try {
      String palettesXML;
      {
        StringBuffer content = new StringBuffer();
        String lineFeed = System.getProperty("line.separator");
        String line;
        Reader r = new InputStreamReader(pInputStream, "utf-8");
        BufferedReader in = new BufferedReader(r);
        while ((line = in.readLine()) != null) {
          content.append(line).append(lineFeed);
        }
        in.close();
        palettesXML = content.toString();
      }
      return readPalettesFromXML(palettesXML);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public List<RGBPalette> readPalettesFromStream(InputStream inputStream, String filename) {
    try {
      String flamesXML = new String(Tools.getBytesFromInputStream(inputStream), "UTF-8");
      return readPalettesFromXML(flamesXML);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<RGBPalette> readPalettes(String pFilename) {
    try {
      String flamesXML = Tools.readUTF8Textfile(pFilename);
      return readPalettesFromXML(flamesXML);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private Map<String, String> parseAttributes(String pXML) {
    Map<String, String> res = new HashMap<String, String>();
    int p = 0;
    while (true) {
      int ps = pXML.indexOf("=\"", p + 1);
      if (ps < 0)
        break;
      int pe = pXML.indexOf("\"", ps + 2);
      String name = pXML.substring(p, ps).trim();
      String value;
      try {
        value = pXML.substring(ps + 2, pe);
        //      System.out.println("#" + name + "#" + value + "#");
      }
      catch (Throwable ex) {
        throw new RuntimeException("Error parsing attribute \"" + name + "\" (" + pXML + ")", ex);
      }
      res.put(name, value);
      p = pe + 2;
    }
    return res;
  }

  private static final String ATTR_NUMBER = "number";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_DATA = "data";

  private void parsePaletteAttributes(RGBPalette pPalette, String pXML) {
    Map<String, String> atts = parseAttributes(pXML);
    String hs;
    if ((hs = atts.get(ATTR_NUMBER)) != null) {
      pPalette.setFlam3Number(hs.trim());
    }
    if ((hs = atts.get(ATTR_NAME)) != null) {
      pPalette.setFlam3Name(hs.trim());
    }
    if ((hs = atts.get(ATTR_DATA)) != null) {
      hs = hs.trim().replaceAll("\\s", "");
      int index = 0;
      for (int i = 0; i < hs.length(); i += 8) {
        int r = Integer.parseInt(hs.substring(i + 2, i + 4), 16);
        int g = Integer.parseInt(hs.substring(i + 4, i + 6), 16);
        int b = Integer.parseInt(hs.substring(i + 6, i + 8), 16);
        pPalette.setColor(index++, r, g, b);
      }
    }
  }

  public List<RGBPalette> readPalettesFromXML(String pXML) {
    List<RGBPalette> res = new ArrayList<RGBPalette>();
    int pPalettes = 0;
    while (true) {
      String paletteXML;
      {
        int ps = pXML.indexOf("<palette ", pPalettes);
        if (ps < 0)
          break;
        int pe = pXML.indexOf("/>", ps + 1);
        if (pe < 0)
          break;
        pPalettes = pe + 2;
        paletteXML = pXML.substring(ps, pPalettes);
      }

      RGBPalette palette = new RGBPalette();
      res.add(palette);
      {
        int ps = paletteXML.indexOf("<flame ");
        int pe = -1;
        boolean qt = false;
        for (int i = ps + 1; i < paletteXML.length(); i++) {
          if (paletteXML.charAt(i) == '\"') {
            qt = !qt;
          }
          else if (!qt && paletteXML.charAt(i) == '>') {
            pe = i;
            break;
          }
        }
        String hs = paletteXML.substring(ps + 7, pe);
        parsePaletteAttributes(palette, hs);
      }
    }
    return res;
  }

}
