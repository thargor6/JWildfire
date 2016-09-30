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
package org.jwildfire.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;

public class SimpleGrayImage implements WFImage {
  private int imageWidth = -1;
  private int imageHeight = -1;
  private final BufferedImage bufferedImg;
  private final DataBufferUShort buffer;

  public SimpleGrayImage(int pWidth, int pHeight) {
    bufferedImg = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_USHORT_GRAY);
    buffer = (DataBufferUShort) bufferedImg.getRaster().getDataBuffer();
    imageWidth = pWidth;
    imageHeight = pHeight;
  }

  @Override
  public int getImageWidth() {
    return imageWidth;
  }

  @Override
  public int getImageHeight() {
    return imageHeight;
  }

  public BufferedImage getBufferedImg() {
    return bufferedImg;
  }

  @Override
  public double getAspect() {
    return imageHeight != 0 ? (double) imageWidth / (double) imageHeight : 0.0;
  }

  public void setValue(int pX, int pY, int pValue) {
    buffer.setElem(pX + pY * imageWidth, pValue & 0xffff);
  }

  public int getValue(int pX, int pY) {
    return buffer.getElem(pX + pY * imageWidth);
  }

}
