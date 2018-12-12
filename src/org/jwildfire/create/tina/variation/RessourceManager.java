/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

import org.jwildfire.base.Tools;
import org.jwildfire.image.WFImage;
import org.jwildfire.io.ImageReader;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RessourceManager {
  private static final Map<String, WFImage> imageMapByName = new HashMap<String, WFImage>();
  private static final Map<Integer, WFImage> imageMapByHash = new HashMap<Integer, WFImage>();
  private static final Map<String, Object> ressourceMap = new HashMap<String, Object>();

  public static void clearAll() {
    imageMapByName.clear();
    imageMapByHash.clear();
    ressourceMap.clear();
  }

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
      } else {
        res = new ImageReader(new JLabel()).loadImage(pFilename);
      }
      imageMapByName.put(pFilename, res);
    }
    return res;
  }

  public static void clearImage(String pFilename) {
    imageMapByName.remove(pFilename);
  }

  public static Object getRessource(String pKey) {
    return ressourceMap.get(pKey);
  }

  public static void putRessource(String pKey, Object pRessource) {
    ressourceMap.put(pKey, pRessource);
  }

  private static Map<String, Integer> hashMap = new HashMap<String, Integer>();

  private static String getHashKey(byte[] pImageData) {
    if (pImageData != null && pImageData.length > 0) {
      return pImageData.toString() + "_" + pImageData.length + "_" + pImageData[0] + "_" + pImageData[pImageData.length / 2] + "_" + pImageData[pImageData.length - 1];
    } else {
      return "0";
    }
  }

  public static int calcHashCode(byte[] pImageData) {
    String key = getHashKey(pImageData);
    Integer res = hashMap.get(key);
    if (res == null) {
      res = pImageData != null ? Arrays.hashCode(pImageData) : 0;
      hashMap.put(key, res);
    }
    return res;
  }

  public static WFImage getImage(int pHashCode, byte[] pImageData) throws Exception {
    Integer key = Integer.valueOf(pHashCode);
    WFImage res = imageMapByHash.get(key);
    if (res == null) {
      String fileExt = guessImageExtension(pImageData);
      File f = File.createTempFile("tmp", "." + fileExt);
      f.deleteOnExit();
      Tools.writeFile(f.getAbsolutePath(), pImageData);

      if ("hdr".equalsIgnoreCase(fileExt)) {
        res = new ImageReader(new JLabel()).loadHDRImage(f.getAbsolutePath());
      } else {
        res = new ImageReader(new JLabel()).loadImage(f.getAbsolutePath());
      }
      imageMapByHash.put(key, res);
    }
    return res;
  }

  public static String guessImageExtension(byte[] pImageData) {
    if (pImageData.length > 6 && new String(pImageData, 0, 6).equals("#?RGBE")) {
      return "hdr";
    } else if (pImageData.length > 5 && new String(pImageData, 1, 3).equals("PNG")) {
      return "png";
    } else {
      return "jpg";
    }
  }

  public static void clearRessources(String pKeyPrefix) {
    List<String> keys = new ArrayList<String>();
    for (String key : ressourceMap.keySet()) {
      if (key.startsWith(pKeyPrefix)) {
        keys.add(key);
      }
    }
    for (String key : keys) {
      System.out.println("REM: " + key);
      ressourceMap.remove(key);
    }
  }

  public static void removeRessource(String pKey) {
    ressourceMap.remove(pKey);
  }
}
