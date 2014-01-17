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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import org.jwildfire.base.Tools;
import org.jwildfire.image.WFImage;
import org.jwildfire.io.ImageReader;

public class RessourceManager {

  private static final Map<String, WFImage> imageMapByName = new HashMap<String, WFImage>();
  private static final Map<Integer, WFImage> imageMapByHash = new HashMap<Integer, WFImage>();
  private static final Map<String, Object> ressourceMap = new HashMap<String, Object>();

  public static WFImage getImage(String pFilename) throws Exception {
    if (pFilename == null || pFilename.length() == 0) {
      throw new IllegalStateException();
    }
    WFImage res = imageMapByName.get(pFilename);
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
      imageMapByName.put(pFilename, res);
    }
    return res;
  }

  public static Object getRessource(String pKey) {
    return ressourceMap.get(pKey);
  }

  public static void putRessource(String pKey, Object pRessource) {
    ressourceMap.put(pKey, pRessource);
  }

  public static int calcHashCode(byte[] pImageData) {
    return pImageData != null ? Arrays.hashCode(pImageData) : 0;
  }

  public static WFImage getImage(int pHashCode, byte[] pImageData) throws Exception {
    Integer key = Integer.valueOf(pHashCode);
    WFImage res = imageMapByHash.get(key);
    if (res == null) {
      String fileExt = "jpg";
      if (pImageData.length > 6 && new String(pImageData, 0, 6).equals("#?RGBE")) {
        fileExt = "hdr";
      }
      else if (pImageData.length > 5 && new String(pImageData, 1, 3).equals("PNG")) {
        fileExt = "png";
      }
      File f = File.createTempFile("tmp", "." + fileExt);
      f.deleteOnExit();
      Tools.writeFile(f.getAbsolutePath(), pImageData);

      if ("hdr".equalsIgnoreCase(fileExt)) {
        res = new ImageReader(new JLabel()).loadHDRImage(f.getAbsolutePath());
      }
      else {
        res = new ImageReader(new JLabel()).loadImage(f.getAbsolutePath());
      }
      imageMapByHash.put(key, res);
    }
    return res;
  }
}
