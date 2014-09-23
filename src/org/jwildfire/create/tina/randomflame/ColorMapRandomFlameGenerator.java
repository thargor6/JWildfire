/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.randomflame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.MedianCutQuantizer;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.io.ImageReader;

public class ColorMapRandomFlameGenerator extends WikimediaCommonsRandomFlameGenerator {

  @Override
  public String getName() {
    return "Color map";
  }

  private List<File> files = null;

  public void scanFiles(String path, List<File> pFiles) {
    File root = new File(path);
    File[] list = root.listFiles();
    if (list != null) {
      for (File f : list) {
        if (f.isDirectory()) {
          scanFiles(f.getAbsolutePath(), pFiles);
        }
        else {
          String lc = f.getName().toLowerCase();
          if (lc.endsWith(".png") || lc.endsWith(".jpg") || lc.endsWith(".jpef")) {
            pFiles.add(f);
          }
        }
      }
    }
  }

  private File getRandomFile() throws Exception {
    if (files == null) {
      files = new ArrayList<File>();
      Prefs prefs = Prefs.getPrefs();
      try {
        if (prefs.getTinaRandGenColorMapImagePath() != null && prefs.getTinaRandGenColorMapImagePath().length() > 0) {
          scanFiles(prefs.getTinaRandGenColorMapImagePath(), files);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (files.size() > 0) {
      return files.get((int) (Math.random() * files.size()));
    }
    else {
      return null;
    }
  }

  @Override
  protected ImageData obtainImage() {
    try {
      int minSize = 16;
      int maxSize = 16000;
      File file = getRandomFile();
      if (file != null) {
        byte[] imgData = Tools.readFile(file.getAbsolutePath());
        WFImage img = new ImageReader(new JLabel()).loadImage(file.getAbsolutePath());
        if (img.getImageWidth() >= minSize && img.getImageWidth() <= maxSize && img.getImageHeight() >= minSize && img.getImageHeight() <= maxSize) {
          int hashcode = RessourceManager.calcHashCode(imgData);
          SimpleImage wfImg = (SimpleImage) RessourceManager.getImage(hashcode, imgData);
          RGBPalette gradient = new MedianCutQuantizer().createPalette(wfImg);
          return new ImageData(null, file.getName(), imgData, gradient);
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

}
