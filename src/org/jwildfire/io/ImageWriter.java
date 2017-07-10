/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleGrayImage;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;

public class ImageWriter {

  public void saveAsJPEG(SimpleImage pImg, String pFilename) throws Exception {
	  // JPEG doesn't support alpha channel, so convert from ARGB to RGB before saving
      BufferedImage img = new BufferedImage(pImg.getImageWidth(),pImg.getImageHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = img.createGraphics();
      g2d.drawImage(pImg.getBufferedImg(), 0, 0, null);
      g2d.dispose();
	  ImageIO.write(img, "jpg", new File(pFilename));
  }

  public void saveAsPNG(SimpleImage pImg, String pFilename) throws Exception {
    ImageIO.write(pImg.getBufferedImg(), "png", new File(pFilename));
  }

  public void saveImage(SimpleImage pImg, String pFilename) throws Exception {
    saveImage(pImg, pFilename, false);
  }

  public void saveImage(SimpleGrayImage pImg, String pFilename) throws Exception {
    ImageIO.write(pImg.getBufferedImg(), "png", new File(pFilename));
  }

  public void saveImage(SimpleImage pImg, String pFilename, boolean pQuiet) throws Exception {
    String filename = new File(pFilename).getName();
    if (!pQuiet) {
      System.out.println("file: " + filename);
    }
    String[] p = filename.split("\\.");
    if (p.length == 1)
      saveAsJPEG(pImg, pFilename + ".jpg");
    else if (p[p.length - 1].equalsIgnoreCase(Tools.FILEEXT_JPG))
      saveAsJPEG(pImg, pFilename);
    else if (p[p.length - 1].equalsIgnoreCase(Tools.FILEEXT_JPS))
      saveAsJPEG(pImg, pFilename);
    else if (p[p.length - 1].equalsIgnoreCase(Tools.FILEEXT_PNG))
      saveAsPNG(pImg, pFilename);
    else if (p[p.length - 1].equalsIgnoreCase(Tools.FILEEXT_PNS))
      saveAsPNG(pImg, pFilename);
    else if (p[p.length - 1].equalsIgnoreCase(Tools.FILEEXT_HDR))
      saveAsHDR(pImg, pFilename);
    else if (p[p.length - 1].equalsIgnoreCase(Tools.FILEEXT_TXT))
      savePalette(pImg, pFilename);
    else
      throw new IllegalArgumentException(pFilename);
  }

  public void saveImage(SimpleHDRImage pImg, String pFilename) throws Exception {
    String filename = new File(pFilename).getName();
    System.out.println("file: " + filename);
    String[] p = filename.split("\\.");
    if (p.length == 1)
      saveAsHDR(pImg, pFilename + ".jpg");
    else if (p[p.length - 1].equalsIgnoreCase("hdr"))
      saveAsHDR(pImg, pFilename);
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

  public void saveAsHDR(SimpleImage pImg, String pFilename) throws Exception {
    OutputStream f = new BufferedOutputStream(new FileOutputStream(pFilename));
    f.write("#?RGBE\n".getBytes());
    f.write("FORMAT=32-bit_rle_rgbe\n\n".getBytes());
    f.write(("-Y " + pImg.getImageHeight() + " +X " + pImg.getImageWidth() + "\n").getBytes());
    for (int i = 0; i < pImg.getImageHeight(); i++) {
      for (int j = 0; j < pImg.getImageWidth(); j++) {
        int rgbe = pImg.getRGBEValue(j, i);
        f.write(rgbe >> 24);
        f.write(rgbe >> 16);
        f.write(rgbe >> 8);
        f.write(rgbe);
      }
    }
    f.close();
  }

  public void saveAsHDR(SimpleHDRImage pImg, String pFilename) throws Exception {
    OutputStream f = new BufferedOutputStream(new FileOutputStream(pFilename));
    f.write("#?RGBE\n".getBytes());
    f.write("FORMAT=32-bit_rle_rgbe\n\n".getBytes());
    f.write(("-Y " + pImg.getImageHeight() + " +X " + pImg.getImageWidth() + "\n").getBytes());
    for (int i = 0; i < pImg.getImageHeight(); i++) {
      for (int j = 0; j < pImg.getImageWidth(); j++) {
        int rgbe = pImg.getRGBEValue(j, i);
        f.write(rgbe >> 24);
        f.write(rgbe >> 16);
        f.write(rgbe >> 8);
        f.write(rgbe);
      }
    }
    f.close();
  }
}
