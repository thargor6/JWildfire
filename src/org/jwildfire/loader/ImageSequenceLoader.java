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
package org.jwildfire.loader;

import java.io.File;

import org.jwildfire.base.Property;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;


public class ImageSequenceLoader extends ImageLoader {

  @Property(description = "Filename of one image of the sequence")
  private String filename = "D:\\TMP\\wf\\render\\Img0007.jpg";

  @Property(description = "Number of the image")
  private int frame = 1;

  @Override
  protected SimpleImage loadImage() {
    String fn = new File(this.filename).getName();
    String ext;
    int p = fn.lastIndexOf(".");
    if ((p > 0) && (p < fn.length() - 1)) {
      ext = fn.substring(p, fn.length());
      fn = fn.substring(0, p);
    }
    else {
      ext = "";
    }
    String baseFn = fn;
    String numberPart = "";
    for (int i = fn.length() - 1; i >= 0; i--) {
      char c = fn.charAt(i);
      if (!((c >= '0') && (c <= '9'))) {
        baseFn = fn.substring(0, i + 1);
        if (i < fn.length() - 1)
          numberPart = fn.substring(i + 1, fn.length());
        break;
      }
      else if (i == 0) {
        numberPart = fn;
        baseFn = "";
      }
    }
    System.out.println("BASE: " + baseFn);
    System.out.println("NUMBER: " + numberPart);
    System.out.println("EXT: " + ext);

    // generate current filename
    fn = String.valueOf(this.frame);
    while (fn.length() < numberPart.length())
      fn = "0" + fn;
    fn = new File(this.filename).getParent() + File.separator + baseFn + fn + ext;
    System.out.println(this.frame + ": " + fn);
    try {
      return new ImageReader(getRootFrame()).loadImage(fn);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public int getFrame() {
    return frame;
  }

  public void setFrame(int frame) {
    this.frame = frame;
  }

}
