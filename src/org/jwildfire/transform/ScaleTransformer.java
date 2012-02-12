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
package org.jwildfire.transform;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.swing.ScaleAspectEditor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ScaleTransformer extends Transformer {
  public enum Unit {
    PIXELS, PERCENT
  }

  @Property(description = "Scale unit", editorClass = UnitEditor.class)
  private Unit unit = Unit.PIXELS;

  @Property(description = "How to treat the aspect ratio of the original", editorClass = ScaleAspectEditor.class)
  private ScaleAspect aspect = ScaleAspect.KEEP_WIDTH;

  @Property(description = "Width of the scaled image")
  @PropertyMin(1)
  private int scaleWidth = 320;

  @Property(description = "Height of the scaled image")
  @PropertyMin(1)
  private int scaleHeight = 256;

  public static class UnitEditor extends ComboBoxPropertyEditor {
    public UnitEditor() {
      super();
      setAvailableValues(new Unit[] { Unit.PERCENT, Unit.PIXELS });
    }
  }

  public Unit getUnit() {
    return unit;
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  public ScaleAspect getAspect() {
    return aspect;
  }

  public void setAspect(ScaleAspect aspect) {
    this.aspect = aspect;
  }

  public int getScaleWidth() {
    return scaleWidth;
  }

  public void setScaleWidth(int scaleWidth) {
    this.scaleWidth = scaleWidth;
  }

  public int getScaleHeight() {
    return scaleHeight;
  }

  public void setScaleHeight(int scaleHeight) {
    this.scaleHeight = scaleHeight;
  }

  @Override
  public boolean supports3DOutput() {
    return false;
  }

  @Override
  protected void performImageTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int srcWidth = pImg.getImageWidth();
    int srcHeight = pImg.getImageHeight();
    double width, height;
    if (unit == Unit.PIXELS) {
      width = (double) scaleWidth;
      height = (double) scaleHeight;
    }
    else {
      width = (double) scaleWidth / 100.0 * (double) srcWidth;
      height = (double) scaleHeight / 100.0 * (double) srcHeight;
    }
    if (aspect == ScaleAspect.KEEP_WIDTH) {
      double scl = width / (double) srcWidth;
      height = (double) srcHeight * scl;
    }
    else if (aspect == ScaleAspect.KEEP_HEIGHT) {
      double scl = height / (double) srcHeight;
      width = (double) srcWidth * scl;
    }
    int dstWidth = (int) (width + 0.5);
    if (dstWidth < 1)
      dstWidth = 1;
    int dstHeight = (int) (height + 0.5);
    if (dstHeight < 1)
      dstHeight = 1;
    SimpleImage srcImg = img.clone();
    img.resetImage(dstWidth, dstHeight);

    if ((dstWidth >= srcWidth) && (dstHeight >= srcHeight))
      scale_up_up(srcImg, img);
    else if ((dstWidth < srcWidth) && (dstHeight >= srcHeight))
      scale_down_up(srcImg, img);
    else if ((dstWidth >= srcWidth) && (dstHeight < srcHeight))
      scale_up_down(srcImg, img);
    else if ((dstWidth < srcWidth) && (dstHeight < srcHeight))
      scale_down_down(srcImg, img);
  }

  private void scale_up_up(SimpleImage srcImg, SimpleImage dstImg) {
    int swidth = srcImg.getImageWidth();
    int sheight = srcImg.getImageHeight();
    int width = dstImg.getImageWidth();
    int height = dstImg.getImageHeight();
    double sclX, sclY;
    if (width > 1) {
      sclX = (double) (swidth - 1) / (double) (width - 1);
    }
    else
      sclX = 1.0;
    if (height > 1) {
      sclY = (double) (sheight - 1) / (double) (height - 1);
    }
    else
      sclY = 1.0;
    double cxD = (double) (width - 1) * 0.5;
    double cyD = (double) (height - 1) * 0.5;
    double cxS = (double) (swidth - 1) * 0.5;
    double cyS = (double) (sheight - 1) * 0.5;
    Pixel pPixel = new Pixel();

    for (int i = 0; i < height; i++) {
      double y0 = (double) i - cyD;
      double y = y0 * sclY + cyS;
      for (int j = 0; j < width; j++) {
        // transform the point 
        double x0 = (double) j - cxD;
        double x = x0 * sclX + cxS;

        // render it            
        if (((int) x < 0) || ((int) x >= swidth) || ((int) y < 0) || ((int) y >= sheight)) {
          pPixel.r = pPixel.g = pPixel.b = 0;
        }
        else {
          double xi = Tools.fmod33(x);
          double yi = Tools.fmod33(y);
          readSrcPixels(srcImg, x, y);
          pPixel.r = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.r) + xi * (srcQ.r)) + yi
              * ((1.0 - xi) * (srcR.r) + xi * (srcS.r))));
          pPixel.g = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.g) + xi * (srcQ.g)) + yi
              * ((1.0 - xi) * (srcR.g) + xi * (srcS.g))));
          pPixel.b = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.b) + xi * (srcQ.b)) + yi
              * ((1.0 - xi) * (srcR.b) + xi * (srcS.b))));
        }
        dstImg.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  private void scale_down_up(SimpleImage srcImg, SimpleImage dstImg) {
    int swidth = srcImg.getImageWidth();
    int sheight = srcImg.getImageHeight();
    int width = dstImg.getImageWidth();
    int height = dstImg.getImageHeight();

    double sclX, sclY;
    if (width > 1) {
      sclX = (double) (swidth - 1) / (double) (width - 1);
    }
    else
      sclX = 1.0;
    if (height > 1) {
      sclY = (double) (sheight - 1) / (double) (height - 1);
    }
    else
      sclY = 1.0;
    double cxD = (double) (width - 1) * 0.5;
    double cyD = (double) (height - 1) * 0.5;
    double cxS = (double) (swidth - 1) * 0.5;
    double cyS = (double) (sheight - 1) * 0.5;
    Pixel pPixel = new Pixel();

    for (int i = 0; i < height; i++) {
      double y0 = (double) i - cyD;
      double y = y0 * sclY + cyS;
      for (int j = 0; j < width; j++) {
        // transform the point 
        double x0 = (double) j - cxD;
        double x = x0 * sclX + cxS;
        double xn = (x0 + 1.0) * sclX + cxS;
        if (xn >= (swidth - 0.5))
          xn = swidth - 1.0;

        if (((int) x < 0) || ((int) x >= swidth) || ((int) y < 0) || ((int) y >= sheight)) {
          pPixel.r = pPixel.g = pPixel.b = 0;
        }
        else {
          double xi = Tools.fmod33(x);
          double yi = Tools.fmod33(y);
          readSrcPixels(srcImg, x, y);
          int cvalR = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.r) + xi * (srcQ.r)) + yi
              * ((1.0 - xi) * (srcR.r) + xi * (srcS.r))));
          int cvalG = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.g) + xi * (srcQ.g)) + yi
              * ((1.0 - xi) * (srcR.g) + xi * (srcS.g))));
          int cvalB = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.b) + xi * (srcQ.b)) + yi
              * ((1.0 - xi) * (srcR.b) + xi * (srcS.b))));

          x = x - xi + 1.0;
          int cnt = 1;
          while (x < xn) {
            readSrcPixels(srcImg, x, y);
            cvalR += (1.0 - yi) * srcP.r + yi * srcR.r;
            cvalG += (1.0 - yi) * srcP.g + yi * srcR.g;
            cvalB += (1.0 - yi) * srcP.b + yi * srcR.b;
            x += 1.0;
            cnt++;
          }
          pPixel.r = roundColor(cvalR / (double) cnt);
          pPixel.g = roundColor(cvalG / (double) cnt);
          pPixel.b = roundColor(cvalB / (double) cnt);
        }
        dstImg.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  private void scale_up_down(SimpleImage srcImg, SimpleImage dstImg) {
    int swidth = srcImg.getImageWidth();
    int sheight = srcImg.getImageHeight();
    int width = dstImg.getImageWidth();
    int height = dstImg.getImageHeight();
    double sclX, sclY;
    if (width > 1) {
      sclX = (double) (swidth - 1) / (double) (width - 1);
    }
    else
      sclX = 1.0;
    if (height > 1) {
      sclY = (double) (sheight - 1) / (double) (height - 1);
    }
    else
      sclY = 1.0;
    double cxD = (double) (width - 1) * 0.5;
    double cyD = (double) (height - 1) * 0.5;
    double cxS = (double) (swidth - 1) * 0.5;
    double cyS = (double) (sheight - 1) * 0.5;
    Pixel pPixel = new Pixel();

    for (int i = 0; i < height; i++) {
      double y0 = (double) i - cyD;
      double yn = (y0 + 1.0) * sclY + cyS;
      if (yn >= (sheight - 0.5))
        yn = sheight - 1.0;
      for (int j = 0; j < width; j++) {
        // transform the point 
        double x0 = (double) j - cxD;
        double x = x0 * sclX + cxS;
        double y = y0 * sclY + cyS;

        if (((int) x < 0) || ((int) x >= swidth) || ((int) y < 0) || ((int) y >= sheight)) {
          pPixel.r = pPixel.g = pPixel.b = 0;
        }
        else {
          double xi = Tools.fmod33(x);
          double yi = Tools.fmod33(y);
          readSrcPixels(srcImg, x, y);
          int cvalR = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.r) + xi * (srcQ.r)) + yi
              * ((1.0 - xi) * (srcR.r) + xi * (srcS.r))));
          int cvalG = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.g) + xi * (srcQ.g)) + yi
              * ((1.0 - xi) * (srcR.g) + xi * (srcS.g))));
          int cvalB = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.b) + xi * (srcQ.b)) + yi
              * ((1.0 - xi) * (srcR.b) + xi * (srcS.b))));
          y = y - yi + 1.0;
          int cnt = 1;
          while (y < yn) {
            readSrcPixels(srcImg, x, y);
            cvalR += (1.0 - xi) * srcP.r + xi * srcQ.r;
            cvalG += (1.0 - xi) * srcP.g + xi * srcQ.g;
            cvalB += (1.0 - xi) * srcP.b + xi * srcQ.b;
            y += 1.0;
            cnt++;
          }
          pPixel.r = roundColor(cvalR / (double) cnt);
          pPixel.g = roundColor(cvalG / (double) cnt);
          pPixel.b = roundColor(cvalB / (double) cnt);
        }
        dstImg.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  private void scale_down_down(SimpleImage srcImg, SimpleImage dstImg) {
    int swidth = srcImg.getImageWidth();
    int sheight = srcImg.getImageHeight();
    int width = dstImg.getImageWidth();
    int height = dstImg.getImageHeight();
    double sclX, sclY;
    if (width > 1) {
      sclX = (double) (swidth - 1) / (double) (width - 1);
    }
    else
      sclX = 1.0;
    if (height > 1) {
      sclY = (double) (sheight - 1) / (double) (height - 1);
    }
    else
      sclY = 1.0;
    double cxD = (double) (width - 1) * 0.5;
    double cyD = (double) (height - 1) * 0.5;
    double cxS = (double) (swidth - 1) * 0.5;
    double cyS = (double) (sheight - 1) * 0.5;
    Pixel pPixel = new Pixel();

    for (int i = 0; i < height; i++) {
      double y0 = (double) i - cyD;
      for (int j = 0; j < width; j++) {
        // transform the point 
        double x0 = (double) j - cxD;
        double x = x0 * sclX + cxS;
        double xn = (x0 + 1.0) * sclX + cxS;
        if (xn >= (swidth - 0.5))
          xn = swidth - 1.0;
        double y = y0 * sclY + cyS;
        double yn = (y0 + 1.0) * sclY + cyS;
        if (yn >= (sheight - 0.5))
          yn = sheight - 1.0;

        // render it 
        if (((int) x < 0) || ((int) x >= swidth) || ((int) y < 0) || ((int) y >= sheight)) {
          pPixel.r = pPixel.g = pPixel.b = 0;
        }
        else {
          double xi = Tools.fmod33(x);
          double yi = Tools.fmod33(y);
          readSrcPixels(srcImg, x, y);
          // 1st line 
          readSrcPixels(srcImg, x, y);
          int cvalR = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.r) + xi * (srcQ.r)) + yi
              * ((1.0 - xi) * (srcR.r) + xi * (srcS.r))));
          int cvalG = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.g) + xi * (srcQ.g)) + yi
              * ((1.0 - xi) * (srcR.g) + xi * (srcS.g))));
          int cvalB = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.b) + xi * (srcQ.b)) + yi
              * ((1.0 - xi) * (srcR.b) + xi * (srcS.b))));
          double xc = x - xi + 1.0;
          int cnt = 1;
          while (xc < xn) {
            readSrcPixels(srcImg, x, y);
            cvalR += (1.0 - yi) * srcP.r + yi * srcR.r;
            cvalG += (1.0 - yi) * srcP.g + yi * srcR.g;
            cvalB += (1.0 - yi) * srcP.b + yi * srcR.b;
            xc += 1.0;
            cnt++;
          }
          // remaining lines 
          y = y - yi + 1.0;
          while (y < yn) {
            // 1st point 
            readSrcPixels(srcImg, x, y);
            cvalR += (1.0 - xi) * srcP.r + xi * srcQ.r;
            cvalG += (1.0 - xi) * srcP.g + xi * srcQ.g;
            cvalB += (1.0 - xi) * srcP.b + xi * srcQ.b;
            // remaining points 
            xc = x - xi + 1.0;
            cnt++;
            while (xc < xn) {
              readSrcPixels(srcImg, x, y);
              cvalR += srcP.r;
              cvalG += srcP.g;
              cvalB += srcP.b;
              xc += 1.0;
              cnt++;
            }
            y += 1.0;
          }
          pPixel.r = roundColor(cvalR / (double) cnt);
          pPixel.g = roundColor(cvalG / (double) cnt);
          pPixel.b = roundColor(cvalB / (double) cnt);
        }
        dstImg.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  double fmod33(double arg) {
    return (arg - (double) ((int) arg));
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    unit = Unit.PIXELS;
    aspect = ScaleAspect.KEEP_WIDTH;
    scaleWidth = 320;
    scaleHeight = 256;
  }

  public boolean acceptsInputBufferType(BufferType pBufferType) {
    return (pBufferType == BufferType.IMAGE);
  }

  protected Pixel srcP = new Pixel();
  protected Pixel srcQ = new Pixel();
  protected Pixel srcR = new Pixel();
  protected Pixel srcS = new Pixel();

  protected void readSrcPixels(SimpleImage srcImg, double pX, double pY) {
    int x = (int) (pX + 0.5);
    int y = (int) (pY + 0.5);
    int w = srcImg.getImageWidth();
    int h = srcImg.getImageHeight();
    if ((x < 0) || (x >= w) || (y < 0) || (y >= h)) {
      srcP.clear();
      srcQ.clear();
      srcR.clear();
      srcS.clear();
    }
    else {
      srcP.setARGBValue(srcImg.getBufferedImg().getRGB(x, y));
      if (x < w - 1)
        srcQ.setARGBValue(srcImg.getBufferedImg().getRGB(x + 1, y));
      else
        srcQ.assign(srcP);
      if (y < h - 1) {
        srcR.setARGBValue(srcImg.getBufferedImg().getRGB(x, y + 1));
        if (x < w - 1)
          srcS.setARGBValue(srcImg.getBufferedImg().getRGB(x + 1, y + 1));
        else
          srcS.assign(srcQ);
      }
      else {
        srcR.assign(srcP);
        srcS.assign(srcQ);
      }
    }
  }

  @Override
  protected boolean allowShowStats() {
    return false;
  }

}
