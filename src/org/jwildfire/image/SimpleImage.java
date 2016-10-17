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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;

public class SimpleImage implements WFImage, Cloneable {
  private int imageWidth = -1;
  private int imageHeight = -1;
  private BufferedImage bufferedImg;

  public SimpleImage(int pWidth, int pHeight) {
    bufferedImg = new BufferedImage(pWidth, pHeight, BufferedImage.TYPE_INT_ARGB);
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
        BufferedImage.TYPE_INT_ARGB);
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
    getBufferedImg().setRGB(pX, pY, getARGB(255, pR, pG, pB));
  }

  public void setARGB(int pX, int pY, int pA, int pR, int pG, int pB) {
    getBufferedImg().setRGB(pX, pY, getARGB(pA, pR, pG, pB));
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

  public int getAValue(int pX, int pY) {
    return getA(getARGBValue(pX, pY));
  }

  public int getRValue(int pX, int pY) {
    return getR(getARGBValue(pX, pY));
  }

  public int getGValue(int pX, int pY) {
    return getG(getARGBValue(pX, pY));
  }

  public int getBValue(int pX, int pY) {
    return getB(getARGBValue(pX, pY));
  }

  public int getRGBEValue(int pX, int pY) {
    try {
      return convertRGBToRGBE(getRValue(pX, pY), getGValue(pX, pY), getBValue(pX, pY));
    }
    catch (Exception ex) {
      throw new RuntimeException("(" + pX + ", " + pY + ") is out of bounds (0.."
          + (imageWidth - 1) + ", 0.." + (imageHeight - 1) + ")", ex);
    }
  }

  private int convertRGBToRGBE(int pR, int pG, int pB) {
    return SimpleHDRImage.convertRGBToRGBE((float) pR / 255.0f, (float) pG / 255.0f, (float) pB / 255.0f);
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

  public void fillBackground(SimpleImage pImage) {
    Pixel toolPixel = new Pixel();
    if (pImage.getImageWidth() == imageWidth && pImage.getImageHeight() == imageHeight) {
      for (int i = 0; i < imageHeight; i++) {
        for (int j = 0; j < imageWidth; j++) {
          setARGB(j, i, pImage.getARGBValue(j, i));
        }
      }
    }
    else {
      for (int i = 0; i < imageHeight; i++) {
        for (int j = 0; j < imageWidth; j++) {
          double xCoord = (double) j * (double) (pImage.getImageWidth() - 1) / (double) (imageWidth - 1);
          double yCoord = (double) i * (double) (pImage.getImageHeight() - 1) / (double) (imageHeight - 1);

          toolPixel.setARGBValue(pImage.getARGBValueIgnoreBounds((int) xCoord, (int) yCoord));
          int luR = toolPixel.r;
          int luG = toolPixel.g;
          int luB = toolPixel.b;

          toolPixel.setARGBValue(pImage.getARGBValueIgnoreBounds(((int) xCoord) + 1, (int) yCoord));
          int ruR = toolPixel.r;
          int ruG = toolPixel.g;
          int ruB = toolPixel.b;
          toolPixel.setARGBValue(pImage.getARGBValueIgnoreBounds((int) xCoord, ((int) yCoord) + 1));
          int lbR = toolPixel.r;
          int lbG = toolPixel.g;
          int lbB = toolPixel.b;
          toolPixel.setARGBValue(pImage.getARGBValueIgnoreBounds(((int) xCoord) + 1, ((int) yCoord) + 1));
          int rbR = toolPixel.r;
          int rbG = toolPixel.g;
          int rbB = toolPixel.b;

          double x = MathLib.frac(xCoord);
          double y = MathLib.frac(yCoord);
          int r = Tools.roundColor(GfxMathLib.blerp(luR, ruR, lbR, rbR, x, y));
          int g = Tools.roundColor(GfxMathLib.blerp(luG, ruG, lbG, rbG, x, y));
          int b = Tools.roundColor(GfxMathLib.blerp(luB, ruB, lbB, rbB, x, y));
          setRGB(j, i, r, g, b);
        }
      }
    }
  }

  private int getARGB(int a, int r, int g, int b) {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  private int getA(int argb) {
    return (argb >>> 24) & 0xff;
  }

  private int getR(int argb) {
    return (argb >>> 16) & 0xff;
  }

  private int getG(int argb) {
    return (argb >>> 8) & 0xff;
  }

  private int getB(int argb) {
    return argb & 0xff;
  }

}
