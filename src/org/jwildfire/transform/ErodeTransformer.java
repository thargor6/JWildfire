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

import java.awt.Color;
import java.awt.Graphics;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ErodeTransformer extends Mesh2DTransformer {
  public enum Mode {
    ERODE, DILATE, OILTRANSFER, NEON
  };

  public enum Shape {
    SQUARE, DIAMOND, DISK, PLUS, X, RANDOM
  }

  @Property(editorClass = ModeEditor.class, description = "ERODE (shrink), DILATE (grow) or OILTRANSFER (most popular color) mode")
  private Mode mode;

  @Property(editorClass = ShapeEditor.class, description = "Shape of the area to compute the darkest/brightest color")
  private Shape shape;

  @Property(description = "Size of the shape to compute the darkest/brightest color")
  private int size;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    if (this.size <= 1)
      return;
    SimpleImage img = (SimpleImage) pImg;
    Tools.srand123(this.seed);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    short[][] shape;
    switch (this.shape) {
      case DIAMOND:
        shape = generateDiamond(this.size);
        break;
      case DISK:
        shape = generateDisk(this.size);
        break;
      case SQUARE:
        shape = generateSquare(this.size);
        break;
      case PLUS:
        shape = generatePlus(this.size);
        break;
      case X:
        shape = generateX(this.size);
        break;
      default:
        shape = generateRandom(this.size);
        break;
    }

    SimpleImage srcGreyImg = null;
    if ((this.mode == Mode.DILATE) || (this.mode == Mode.ERODE) || (this.mode == Mode.NEON)) {
      srcGreyImg = srcImg.clone();
      ColorToGrayTransformer cT = new ColorToGrayTransformer();
      cT.setWeights(ColorToGrayTransformer.Weights.LUMINANCE);
      cT.transformImage(srcGreyImg);
    }

    switch (mode) {
      case ERODE:
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int lumMin = srcGreyImg.getRValue(j, i);
            int xLumMin = j;
            int yLumMin = i;
            for (int s = 0; s < shape.length; s++) {
              int x = j + shape[s][0];
              int y = i + shape[s][1];
              if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
                int lum = srcGreyImg.getRValue(x, y);
                if (lum < lumMin) {
                  lumMin = lum;
                  xLumMin = x;
                  yLumMin = y;
                }
              }
            }
            img.setARGB(j, i, srcImg.getARGBValue(xLumMin, yLumMin));
          }
        }
        break;
      case DILATE:
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int lumMax = srcGreyImg.getRValue(j, i);
            int xLumMax = j;
            int yLumMax = i;
            for (int s = 0; s < shape.length; s++) {
              int x = j + shape[s][0];
              int y = i + shape[s][1];
              if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
                int lum = srcGreyImg.getRValue(x, y);
                if (lum > lumMax) {
                  lumMax = lum;
                  xLumMax = x;
                  yLumMax = y;
                }
              }
            }
            img.setARGB(j, i, srcImg.getARGBValue(xLumMax, yLumMax));
          }
        }
        break;
      case NEON:
        Pixel srcPixel = new Pixel();
        Pixel currPixel = new Pixel();
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int lumMax = srcGreyImg.getRValue(j, i);
            int lum0 = lumMax;
            int xLumMax = j;
            int yLumMax = i;
            for (int s = 0; s < shape.length; s++) {
              int x = j + shape[s][0];
              int y = i + shape[s][1];
              if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
                int lum = srcGreyImg.getRValue(x, y);
                if (lum > lumMax) {
                  lumMax = lum;
                  xLumMax = x;
                  yLumMax = y;
                }
              }
            }
            int m1 = 30;
            int m2 = 100 - m1;
            srcPixel.setARGBValue(srcImg.getARGBValue(xLumMax, yLumMax));
            currPixel.setARGBValue(srcImg.getARGBValue(j, i));
            currPixel.r = (srcPixel.r * m1 + currPixel.r * m2) / 100;
            currPixel.g = (srcPixel.g * m1 + currPixel.g * m2) / 100;
            currPixel.b = (srcPixel.b * m1 + currPixel.b * m2) / 100;
            if (Math.abs(lum0 - lumMax) > 50)
              img.setRGB(j, i, 0, 128, 0);
            //pImg.setARGB(j, i, srcImg.getARGBValue(xLumMax, yLumMax));
          }
        }
        break;
      case OILTRANSFER:
        int[] colorValues = new int[shape.length];
        short[] colorUsed = new short[shape.length];
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int cCount = 0;
            for (int s = 0; s < colorUsed.length; s++)
              colorUsed[s] = 0;
            for (int s = 0; s < shape.length; s++) {
              int x = j + shape[s][0];
              int y = i + shape[s][1];
              if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
                int color = srcImg.getARGBValue(x, y);
                boolean found = false;
                for (short t = 0; t < cCount; t++) {
                  if (colorValues[t] == color) {
                    colorUsed[t]++;
                    found = true;
                    break;
                  }
                }
                if (!found) {
                  colorValues[cCount] = color;
                  colorUsed[cCount++] = 1;
                }
              }
            }
            int color = srcImg.getARGBValue(j, i);
            int usedMax = 1;
            for (int t = 0; t < cCount; t++) {
              if (colorUsed[t] > usedMax) {
                usedMax = colorUsed[t];
                color = colorValues[t];
              }
            }
            img.setARGB(j, i, color);
          }
        }
        break;
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    mode = Mode.ERODE;
    shape = Shape.DISK;
    size = 5;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.ERODE, Mode.DILATE, Mode.OILTRANSFER /*, Mode.NEON */});
    }
  }

  public static class ShapeEditor extends ComboBoxPropertyEditor {
    public ShapeEditor() {
      super();
      setAvailableValues(new Shape[] { Shape.DISK, Shape.SQUARE, Shape.DIAMOND, Shape.PLUS,
          Shape.X, Shape.RANDOM });
    }
  }

  public Shape getShape() {
    return shape;
  }

  public void setShape(Shape shape) {
    this.shape = shape;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

  // xxx {-1,-1} { 0,-1} { 1,-1} 
  // xxx {-1, 0} { 0, 0} { 1, 0}
  // xxx {-1, 1} { 0, 1} { 1, 1}
  private short[][] generateSquare(int pSize) {
    short s = (short) pSize;
    if ((s < 3) || (s % 2 == 0))
      throw new IllegalArgumentException(String.valueOf(pSize));
    short s2 = (short) (s / 2);
    short[][] res = new short[s * s][2];
    int idx = 0;
    for (short i = 0; i < s; i++) {
      for (short j = 0; j < s; j++) {
        res[idx][0] = (short) (j - s2);
        res[idx++][1] = (short) (i - s2);
      }
    }
    return res;
  }

  //    x                   { 0,-2}
  //   xxx          {-1,-1} { 0,-1} { 1,-1}
  //  xxxxx {-2, 0} {-1, 0} { 0, 0} { 1, 0} { 2, 0}
  //   xxx          {-1, 1} { 0, 1} { 1, 1}
  //    x                   { 0, 2}
  private short[][] generateDiamond(int pSize) {
    short s = (short) pSize;
    if ((s < 3) || (s % 2 == 0))
      throw new IllegalArgumentException(String.valueOf(pSize));
    short s2 = (short) (s / 2);
    int cnt = s;
    for (short i = 0; i < s2; i++)
      cnt += 2 * (1 + 2 * i);
    short[][] res = new short[cnt][2];
    int idx = 0;
    for (short i = (short) -s2; i <= s2; i++) {
      int k = s2 - (i > 0 ? i : -i);
      for (short j = (short) -k; j <= k; j++) {
        res[idx][0] = j;
        res[idx++][1] = i;
      }
    }
    return res;
  }

  public static void printShape(short[][] pShape) {
    int idx = 0;
    while (idx < pShape.length) {
      System.out.print("{");
      if (pShape[idx][0] >= 0)
        System.out.print(" ");
      System.out.print(pShape[idx][0] + ",");
      if (pShape[idx][1] >= 0)
        System.out.print(" ");
      System.out.print(pShape[idx][1] + "} ");
      idx++;
      if (idx % 3 == 0)
        System.out.println("");
    }
  }

  //   xxx          {-1,-2} { 0,-2} { 1,-2} 
  //  xxxxx {-2,-1} {-1,-1} { 0,-1} { 1,-1} { 2,-1}
  //  xxxxx {-2, 0} {-1, 0} { 0, 0} { 1, 0} { 2, 0}
  //  xxxxx {-2, 1} {-1, 1} { 0, 1} { 1, 1} { 2, 1}
  //   xxx          {-1, 2} { 0, 2} { 1, 2}
  private short[][] generateDisk(int pSize) {
    short s = (short) pSize;
    if ((s < 3) || (s % 2 == 0))
      throw new IllegalArgumentException(String.valueOf(pSize));
    short s2 = (short) (s / 2);
    SimpleImage img = new SimpleImage(s + 120, s + 120);
    Graphics g = img.getGraphics();
    g.setColor(new Color(255, 0, 0));
    // fillOval gave strange effects at low scales, but drawOval seems to work 
    g.drawOval(0, 0, s - 1, s - 1);

    // fill it manually
    for (short i = 0; i < s; i++) {
      nextLine: for (short j = 0; j <= s2; j++) {
        if (img.getRValue(j, i) == 255) {
          g.drawLine(j, i, s - j - 1, i);
          continue nextLine;
        }
      }
    }
    int cnt = 0;
    for (short i = 0; i < s; i++) {
      for (short j = 0; j < s; j++) {
        if (img.getRValue(j, i) == 255)
          cnt++;
      }
    }
    int idx = 0;
    short[][] res = new short[cnt][2];
    for (short i = 0; i < s; i++) {
      for (short j = 0; j < s; j++) {
        if (img.getRValue(j, i) == 255) {
          res[idx][0] = (short) (j - s2);
          res[idx++][1] = (short) (i - s2);
        }
      }
    }
    /*    
        try {
          new ImageSaver().saveAsPNG(img, "D:\\TMP\\_z.png");
        }
        catch (Exception e) {
          e.printStackTrace();
        }
    */
    return res;
  }

  private short[][] generateRandom(int pSize) {
    short[][] res = new short[pSize * pSize][2];
    int s2 = pSize / 2;
    for (int i = 0; i < pSize; i++) {
      for (int j = 0; j < pSize; j++) {
        int idx = i * pSize + j;
        if (Tools.drand() > 0.33) {
          res[idx][0] = (short) ((double) (j - s2) * Tools.drand() + 0.5);
          res[idx][1] = (short) ((double) (i - s2) * Tools.drand() + 0.5);
        }
        else {
          res[idx][0] = 0;
          res[idx][1] = 0;
        }
      }
    }
    return res;
  }

  //  x          { 0,-1}  
  // xxx {-1, 0} { 0, 0} { 1, 0}
  //  x          { 0, 1} 
  private short[][] generatePlus(int pSize) {
    short s = (short) pSize;
    if ((s < 3) || (s % 2 == 0))
      throw new IllegalArgumentException(String.valueOf(pSize));
    short s2 = (short) (s / 2);

    short[][] res = new short[2 * s - 1][2];
    int idx = 0;
    for (short i = 0; i < s; i++) {
      short jMin = (i == s2) ? 0 : s2;
      short jMax = (i == s2) ? s : (short) (s2 + 1);
      System.out.println(i + ": " + jMin + "..." + jMax);
      for (short j = jMin; j < jMax; j++) {
        res[idx][0] = (short) (j - s2);
        res[idx++][1] = (short) (i - s2);
      }
    }
    return res;
  }

  // x x {-1,-1}         { 1,-1} 
  //  x          { 0, 0} 
  // x x {-1, 1}         { 1, 1}
  private short[][] generateX(int pSize) {
    short s = (short) pSize;
    if ((s < 3) || (s % 2 == 0))
      throw new IllegalArgumentException(String.valueOf(pSize));
    short s2 = (short) (s / 2);
    short[][] res = new short[2 * s - 1][2];
    int idx = 0;
    for (short i = 0; i < s; i++) {
      if (i == s2) {
        res[idx][0] = 0;
        res[idx++][1] = (short) (i - s2);
      }
      else {
        short x = (short) (s2 - i);
        if (i > s2)
          x = (short) -x;
        res[idx][0] = (short) -x;
        res[idx++][1] = (short) (i - s2);
        res[idx][0] = (short) x;
        res[idx++][1] = (short) (i - s2);
      }
    }
    return res;
  }
  /*
    public static void main(String[] args) {
      ErodeTransformer t = new ErodeTransformer();
      // short[][] shape = t.generateSquare(5);
      // short[][] shape = t.generateDiamond(5);
      // short[][] shape = t.generateDisk(13);
      // short[][] shape = t.generatePlus(5);
      short[][] shape = t.generateX(5);
      t.printShape(shape);
    }
  */
}
