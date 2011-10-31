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
package org.jwildfire.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class SimpleImage {
  private Pixel toolPixel = new Pixel();
  private int imageWidth = -1;
  private int imageHeight = -1;
  private BufferedImage bufferedImg;

  public SimpleImage(int pWidth, int pHeight) {
    bufferedImg = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_RGB);
    imageWidth = pWidth;
    imageHeight = pHeight;
  }

  public SimpleImage(BufferedImage pBufferedImg, int pWidth, int pHeight) {
    setBufferedImage(pBufferedImg, pWidth, pHeight);
  }

  public SimpleImage() {
  }

  public void setBufferedImage(BufferedImage pBufferedImg, int pWidth, int pHeight) {
    bufferedImg = pBufferedImg;
    imageWidth = pWidth;
    imageHeight = pHeight;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public BufferedImage getBufferedImg() {
    return bufferedImg;
  }

  public double getAspect() {
    return imageHeight != 0 ? (double) imageWidth / (double) imageHeight : 0.0;
  }

  public int getScaledWidth(int pScaledHeight) {
    return (int) ((double) pScaledHeight * getAspect() + 0.5);
  }

  public int getScaledHeight(int pScaledWidth) {
    return (int) ((double) pScaledWidth / getAspect() + 0.5);
  }

  @Override
  public SimpleImage clone() {
    SimpleImage res = new SimpleImage();
    res.bufferedImg = new BufferedImage(getImageWidth(), getImageHeight(),
        BufferedImage.TYPE_INT_RGB);
    res.imageWidth = getImageWidth();
    res.imageHeight = getImageHeight();
    Graphics g = res.bufferedImg.getGraphics();
    g.drawImage(getBufferedImg(), 0, 0, null);
    return res;
  }

  public Graphics getGraphics() {
    return bufferedImg.getGraphics();
  }

  public void setRGB(int pX, int pY, int pR, int pG, int pB) {
    toolPixel.setRGB(pR, pG, pB);
    int argb = toolPixel.getARGBValue();
    getBufferedImg().setRGB(pX, pY, argb);
  }

  private void setARGBValue(int pX, int pY, int pARGBValue) {
    getBufferedImg().setRGB(pX, pY, pARGBValue);
  }

  public void setRGB(int pX, int pY, Pixel pPixel) {
    getBufferedImg().setRGB(pX, pY, pPixel.getARGBValue());
  }

  public void setARGB(int pX, int pY, int pARGB) {
    getBufferedImg().setRGB(pX, pY, pARGB);
  }

  public int getARGBValue(int pX, int pY) {
    try {
      return getBufferedImg().getRGB(pX, pY);
    }
    catch (Exception ex) {
      throw new RuntimeException("(" + pX + ", " + pY + ") is out of bounds (0.."
          + (imageWidth - 1) + ", 0.." + (imageHeight - 1) + ")", ex);
    }
  }

  public int getARGBValueIgnoreBounds(int pX, int pY) {
    if ((pX < 0) || (pX >= imageWidth) || (pY < 0) || (pY >= imageHeight))
      return 0;
    else
      return getBufferedImg().getRGB(pX, pY);
  }

  public int getRValueIgnoreBounds(int pX, int pY) {
    if ((pX < 0) || (pX >= imageWidth) || (pY < 0) || (pY >= imageHeight))
      return 0;
    else
      return getRValue(pX, pY);
  }

  public int getGValueIgnoreBounds(int pX, int pY) {
    if ((pX < 0) || (pX >= imageWidth) || (pY < 0) || (pY >= imageHeight))
      return 0;
    else
      return getGValue(pX, pY);
  }

  public int getRValue(int pX, int pY) {
    toolPixel.setARGBValue(getARGBValue(pX, pY));
    return toolPixel.r;
  }

  public int getGValue(int pX, int pY) {
    toolPixel.setARGBValue(getARGBValue(pX, pY));
    return toolPixel.g;
  }

  public int getBValue(int pX, int pY) {
    toolPixel.setARGBValue(getARGBValue(pX, pY));
    return toolPixel.b;
  }

  public void fillBackground(int pR, int pG, int pB) {
    Graphics g = bufferedImg.getGraphics();
    g.setColor(new Color(pR, pG, pB));
    g.fillRect(0, 0, imageWidth, imageHeight);
  }

  public void resetImage(int pWidth, int pHeight) {
    bufferedImg = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_RGB);
    imageWidth = pWidth;
    imageHeight = pHeight;
  }

  public int[] getLine(int pY) {
    int res[] = new int[imageWidth];
    for (int i = 0; i < imageWidth; i++) {
      res[i] = getARGBValue(i, pY);
    }
    return res;
  }

  public void setLine(int pY, int[] pLine) {
    for (int i = 0; i < imageWidth; i++) {
      setARGBValue(i, pY, pLine[i]);
    }
  }
}
