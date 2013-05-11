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

import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBPalette;

public class UniversalPaletteReader {

  public List<RGBPalette> readPalettes(String pFilename) {
    String extension = "";
    {
      int i = pFilename.lastIndexOf('.');
      if (i > 0) {
        extension = pFilename.substring(i + 1);
      }
    }
    if (Tools.FILEEXT_XML.equalsIgnoreCase(extension)) {
      return new Flam3GradientReader().readPalettes(pFilename);
    }
    else if (Tools.FILEEXT_MAP.equalsIgnoreCase(extension)) {
      return new MapGradientReader().readPalettes(pFilename);
    }
    else if (Tools.FILEEXT_UGR.equalsIgnoreCase(extension) || Tools.FILEEXT_GRADIENT.equalsIgnoreCase(extension)) {
      return new UgrGradientReader().readPalettes(pFilename);
    }
    else if (Tools.FILEEXT_PNG.equalsIgnoreCase(extension) || Tools.FILEEXT_JPG.equalsIgnoreCase(extension) || Tools.FILEEXT_JPEG.equalsIgnoreCase(extension)) {
      return new ImgPaletteReader().readPalettes(pFilename);
    }
    else {
      return null;
    }
  }

}
