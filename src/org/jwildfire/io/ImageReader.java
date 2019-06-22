/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019 Andreas Maschke

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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;

public class ImageReader {
  private Component owner;

  public ImageReader(Component pOwner) {
    owner = pOwner;
  }

  public ImageReader() {
    for(int i=0;i<200;i++) {
      try {
        owner = new JLabel();
        break;
      }
      catch(Exception ex) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          // EMPTY
        }
      }
    }
  }

  public SimpleImage loadImage(String pFilename) throws Exception {
    File file = new File(pFilename);
    if (!file.exists())
      throw new FileNotFoundException(pFilename);
    Image fileImg = Toolkit.getDefaultToolkit().createImage(pFilename);
    MediaTracker tracker = new MediaTracker(owner);
    tracker.addImage(fileImg, 0);
    tracker.waitForID(0);
    int width = fileImg.getWidth(null);
    int height = fileImg.getHeight(null);
    BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = bImg.getGraphics();
    g.drawImage(fileImg, 0, 0, null);
    fileImg = null;
    return new SimpleImage(bImg, width, height);
  }

  public SimpleImage loadImage(URL url) throws Exception {
    Image fileImg = Toolkit.getDefaultToolkit().createImage(url);
    MediaTracker tracker = new MediaTracker(owner);
    tracker.addImage(fileImg, 0);
    tracker.waitForID(0);
    int width = fileImg.getWidth(null);
    int height = fileImg.getHeight(null);
    BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = bImg.getGraphics();
    g.drawImage(fileImg, 0, 0, null);
    return new SimpleImage(bImg, width, height);
  }


  private String readNextLine(InputStream is) throws Exception {
    int b, idx = 0;
    byte line[] = new byte[256];
    while ((b = is.read()) != -1) {
      if (b == '\n') {
        break;
      }
      line[idx++] = (byte) b;
    }
    return idx > 0 ? new String(line, 0, idx) : "";
  }

  public SimpleHDRImage loadHDRImage(String pFilename) throws Exception {
    File file = new File(pFilename);
    if (!file.exists())
      throw new FileNotFoundException(pFilename);
    InputStream f = new BufferedInputStream(new FileInputStream(pFilename));
    // header
    String header = readNextLine(f);
    if (!"#?RGBE".equals(header)) {
      throw new IllegalArgumentException("Invalid header <" + header + ">");
    }
    // format
    String format = readNextLine(f);
    if (!"FORMAT=32-bit_rle_rgbe".equals(format)) {
      throw new IllegalArgumentException("Invalid format <" + format + ">");
    }
    // skip empty line
    readNextLine(f);
    // image size
    String dimension = readNextLine(f);
    Pattern pattern = Pattern.compile("\\-Y ([0-9]+) \\+X ([0-9]+)");
    Matcher matcher = pattern.matcher(dimension);
    if (!matcher.find()) {
      throw new IllegalArgumentException("Invalid dimension identifier<" + dimension + ">");
    }
    int width = Integer.parseInt(matcher.group(2));
    int height = Integer.parseInt(matcher.group(1));
    SimpleHDRImage res = new SimpleHDRImage(width, height);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int r = f.read();
        int g = f.read();
        int b = f.read();
        int e = f.read();
        res.setRGBEValue(j, i, r, g, b, e);
      }
    }
    return res;
  }
}
