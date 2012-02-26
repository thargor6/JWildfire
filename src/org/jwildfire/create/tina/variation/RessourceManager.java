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
package org.jwildfire.create.tina.variation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import org.jwildfire.image.WFImage;
import org.jwildfire.io.ImageReader;

public class RessourceManager {

  private static final Map<String, WFImage> imageMap = new HashMap<String, WFImage>();
  private static final Map<String, Object> ressourceMap = new HashMap<String, Object>();

  public static WFImage getImage(String pFilename) throws Exception {
    if (pFilename == null || pFilename.length() == 0) {
      throw new IllegalStateException();
    }
    WFImage res = imageMap.get(pFilename);
    if (res == null) {
      if (!new File(pFilename).exists()) {
        throw new FileNotFoundException(pFilename);
      }
      String fileExt = null;
      {
        int p = pFilename.lastIndexOf(".");
        if (p >= 0 && p < pFilename.length() - 2) {
          fileExt = pFilename.substring(p + 1, pFilename.length());
        }
      }
      if ("hdr".equalsIgnoreCase(fileExt)) {
        res = new ImageReader(new JLabel()).loadHDRImage(pFilename);
      }
      else {
        res = new ImageReader(new JLabel()).loadImage(pFilename);
      }
      imageMap.put(pFilename, res);
    }
    return res;
  }

  public static Object getRessource(String pKey) throws Exception {
    return ressourceMap.get(pKey);
  }

  public static void putRessource(String pKey, Object pRessource) throws Exception {
    ressourceMap.put(pKey, pRessource);
  }
}
