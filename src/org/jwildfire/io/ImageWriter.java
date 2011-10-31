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
package org.jwildfire.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;

import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;


public class ImageWriter {

  public void saveAsJPEG(SimpleImage pImg, String pFilename) throws Exception {
    ImageIO.write(pImg.getBufferedImg(), "jpg", new File(pFilename));
  }

  public void saveAsPNG(SimpleImage pImg, String pFilename) throws Exception {
    ImageIO.write(pImg.getBufferedImg(), "png", new File(pFilename));
  }

  public void saveImage(SimpleImage pImg, String pFilename) throws Exception {
    String filename = new File(pFilename).getName();
    System.out.println("file: " + filename);
    String[] p = filename.split("\\.");
    if (p.length == 1)
      saveAsJPEG(pImg, pFilename + ".jpg");
    else if (p[p.length - 1].equalsIgnoreCase("jpg"))
      saveAsJPEG(pImg, pFilename);
    else if (p[p.length - 1].equalsIgnoreCase("png"))
      saveAsPNG(pImg, pFilename);
    else if (p[p.length - 1].equalsIgnoreCase("txt"))
      savePalette(pImg, pFilename);
    else
      throw new IllegalArgumentException(pFilename);
  }

  public void savePalette(SimpleImage pImg, String pFilename) throws Exception {
    int width = pImg.getImageWidth();
    StringBuilder sb = new StringBuilder();
    String name = "JWildfire";
    sb.append(name + " {\n");
    sb.append("gradient:\n");
    sb.append(" title=\"" + name + "\" smooth=no\n");
    Pixel p = new Pixel();
    int lastColor = -1;
    for (int i = 0; i < width; i++) {
      // swap R and B
      p.r = pImg.getBValue(i, 0);
      p.g = pImg.getGValue(i, 0);
      p.b = pImg.getRValue(i, 0);
      p.a = 0;
      int color = p.getARGBValue();
      if (color != lastColor) {
        sb.append(" index=" + i + " color=" + color + "\n");
        lastColor = color;
      }
    }
    sb.append("}\n");

    Writer out = new OutputStreamWriter(new FileOutputStream(pFilename), Tools.FILE_ENCODING);
    try {
      out.write(sb.toString());
    }
    finally {
      out.close();
    }

  }
}
