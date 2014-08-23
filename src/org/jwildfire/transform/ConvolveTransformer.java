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
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ConvolveTransformer extends Mesh2DTransformer {
  public enum EdgeMode {
    MIRROR, WRAP, BLACK
  }

  public enum ColorMode {
    COLOR, GREY
  }

  public enum KernelType {
    EDGE_DETECT_3x3, EMBOSS_3x3, LAPLACE_3x3, SHARPEN_3x3, SHARPEN_LESS_3x3, SOBEL_3X3,
    GAUSSIAN_BLUR_5x5, GAUSSIAN_BLUR_3x3
  }

  public enum KernelDirection {
    NORTH, EAST, SOUTH, WEST
  }

  @Property(description = "Effect transparency")
  @PropertyMin(0)
  @PropertyMax(100)
  private int transparency;

  @Property(description = "Constant part to add to each pixel value")
  @PropertyMin(0)
  @PropertyMax(255)
  private int bias;

  @Property(description = "How to treat the outer space of the source image", editorClass = EdgeModeEditor.class)
  private EdgeMode edgeMode;

  @Property(description = "Color or Grey mode", editorClass = ColorModeEditor.class)
  private ColorMode colorMode;

  @Property(description = "The convolution kernel", editorClass = KernelTypeEditor.class)
  private KernelType kernelType;

  @Property(description = "The kernel alignment", editorClass = KernelDirectionEditor.class)
  private KernelDirection kernelDirection;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int kernel[][] = getKernel();
    int width = srcImg.getImageWidth();
    int height = srcImg.getImageHeight();

    int m1 = this.transparency;
    int m2 = (100 - this.transparency);

    int kernelSize = kernel.length;
    int halveKernelSize = kernelSize / 2;
    int kernelSum = 0;
    for (int i = 0; i < kernelSize; i++) {
      for (int j = 0; j < kernelSize; j++) {
        kernelSum += kernel[i][j];
      }
    }
    if (kernelSum == 0)
      kernelSum = 1;
    Pixel pixel = new Pixel();
    if (colorMode == ColorMode.GREY) {
      srcImg = srcImg.clone();
      new ColorToGrayTransformer().transformImage(srcImg);
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int intSumR = 0;
          for (int k = 0; k < kernelSize; k++) {
            int y = i - halveKernelSize + k;
            addPixels: for (int l = 0; l < kernelSize; l++) {
              int x = j - halveKernelSize + l;
              if (x < 0) {
                switch (this.edgeMode) {
                  case MIRROR:
                    x = -x;
                    break;
                  case WRAP:
                    x += width;
                    break;
                  case BLACK:
                    continue addPixels;
                }
              }
              else if (x >= width) {
                switch (this.edgeMode) {
                  case MIRROR:
                    x = 2 * width - x - 1;
                    break;
                  case WRAP:
                    x -= width;
                    break;
                  case BLACK:
                    continue addPixels;
                }
              }
              if (y < 0) {
                switch (this.edgeMode) {
                  case MIRROR:
                    y = -y;
                    break;
                  case WRAP:
                    y += height;
                    break;
                  case BLACK:
                    continue addPixels;
                }
              }
              else if (y >= height) {
                switch (this.edgeMode) {
                  case MIRROR:
                    y = 2 * height - y - 1;
                    break;
                  case WRAP:
                    y -= height;
                    break;
                  case BLACK:
                    continue addPixels;
                }
              }
              intSumR += srcImg.getRValue(x, y) * kernel[k][l];
            }
          }
          intSumR = Tools.limitColor((intSumR / kernelSum) + this.bias);
          if (this.transparency == 0) {
            img.setRGB(j, i, intSumR, intSumR, intSumR);
          }
          else {
            pixel.setARGBValue(srcImg.getARGBValue(j, i));
            int rval = Tools.limitColor(((pixel.r * m1 + intSumR * m2) / 100));
            img.setRGB(j, i, rval, rval, rval);
          }
        }
      }
    }
    else {
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int intSumR = 0;
          int intSumG = 0;
          int intSumB = 0;
          for (int k = 0; k < kernelSize; k++) {
            int y = i - halveKernelSize + k;
            addPixels: for (int l = 0; l < kernelSize; l++) {
              int x = j - halveKernelSize + l;
              if (x < 0) {
                switch (this.edgeMode) {
                  case MIRROR:
                    x = -x;
                    break;
                  case WRAP:
                    x += width;
                    break;
                  case BLACK:
                    continue addPixels;
                }
              }
              else if (x >= width) {
                switch (this.edgeMode) {
                  case MIRROR:
                    x = 2 * width - x - 1;
                    break;
                  case WRAP:
                    x -= width;
                    break;
                  case BLACK:
                    continue addPixels;
                }
              }
              if (y < 0) {
                switch (this.edgeMode) {
                  case MIRROR:
                    y = -y;
                    break;
                  case WRAP:
                    y += height;
                    break;
                  case BLACK:
                    continue addPixels;
                }
              }
              else if (y >= height) {
                switch (this.edgeMode) {
                  case MIRROR:
                    y = 2 * height - y - 1;
                    break;
                  case WRAP:
                    y -= height;
                    break;
                  case BLACK:
                    continue addPixels;
                }
              }
              pixel.setARGBValue(srcImg.getARGBValue(x, y));
              intSumR += pixel.r * kernel[k][l];
              intSumG += pixel.g * kernel[k][l];
              intSumB += pixel.b * kernel[k][l];
            }
          }
          intSumR = Tools.limitColor((intSumR / kernelSum) + this.bias);
          intSumG = Tools.limitColor((intSumG / kernelSum) + this.bias);
          intSumB = Tools.limitColor((intSumB / kernelSum) + this.bias);

          if (this.transparency == 0) {
            img.setRGB(j, i, intSumR, intSumG, intSumB);
          }
          else {
            pixel.setARGBValue(srcImg.getARGBValue(j, i));
            int rval = Tools.limitColor(((pixel.r * m1 + intSumR * m2) / 100));
            int gval = Tools.limitColor(((pixel.g * m1 + intSumG * m2) / 100));
            int bval = Tools.limitColor(((pixel.b * m1 + intSumB * m2) / 100));
            img.setRGB(j, i, rval, gval, bval);
          }
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    edgeMode = EdgeMode.MIRROR;
    colorMode = ColorMode.COLOR;
    transparency = 0;
    kernelDirection = KernelDirection.NORTH;
    bias = 0;
    kernelType = KernelType.EMBOSS_3x3;
  }

  // 1
  private static final int[][] KERNEL_EMPTY = { { 1 } };
  // -1 -1 -1
  // -1  8 -1
  // -1 -1 -1
  private static final int[][] KERNEL_EDGE_DETECT_3x3 = { { -1, -1, -1 }, { -1, 8, -1 },
      { -1, -1, -1 } };
  // -2  0  0
  //  0  1  0
  //  0  0  2
  private static final int[][] KERNEL_EMBOSS_3x3 = { { -2, 0, 0 }, { 0, 1, 0 }, { 0, 0, 2 } };
  //  0 -1  0
  // -1  4 -1
  //  0 -1  0
  private static final int[][] KERNEL_LAPLACE_3x3 = { { 0, -1, 0 }, { -1, 4, -1 }, { 0, -1, 0 } };
  // -1 -1 -1
  // -1  9 -1
  // -1 -1 -1
  private static final int[][] KERNEL_SHARPEN_3x3 = { { -1, -1, -1 }, { -1, 9, -1 }, { -1, -1, -1 } };
  // -1 -1 -1
  // -1  17 -1
  // -1 -1 -1
  private static final int[][] KERNEL_SHARPEN_LESS_3x3 = { { -1, -1, -1 }, { -1, 17, -1 },
      { -1, -1, -1 } };
  // -1  0  1
  // -2  0  2
  // -1  0  1
  private static final int[][] KERNEL_SOBEL_3x3 = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
  //  2  4  5  4  2
  //  4  9 12  9  4
  //  5 12 15 12  5
  //  4  9 12  9  4
  //  2  4  5  4  2
  private static final int[][] KERNEL_GAUSSIAN_BLUR_5x5 = { { 2, 4, 5, 4, 2 }, { 4, 9, 12, 9, 4 },
      { 5, 12, 15, 12, 5 }, { 4, 9, 12, 9, 4 }, { 2, 4, 5, 4, 2 } };
  // 1  2  1
  // 2  4  2
  // 1  2  1
  private static final int[][] KERNEL_GAUSSIAN_BLUR_3x3 = { { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 } };

  private int[][] getKernel() {
    int res[][];
    switch (kernelType) {
      case EDGE_DETECT_3x3:
        res = KERNEL_EDGE_DETECT_3x3;
        break;
      case EMBOSS_3x3:
        res = KERNEL_EMBOSS_3x3;
        break;
      case LAPLACE_3x3:
        res = KERNEL_LAPLACE_3x3;
        break;
      case SHARPEN_3x3:
        res = KERNEL_SHARPEN_3x3;
        break;
      case SHARPEN_LESS_3x3:
        res = KERNEL_SHARPEN_LESS_3x3;
        break;
      case SOBEL_3X3:
        res = KERNEL_SOBEL_3x3;
        break;
      case GAUSSIAN_BLUR_3x3:
        res = KERNEL_GAUSSIAN_BLUR_3x3;
        break;
      case GAUSSIAN_BLUR_5x5:
        res = KERNEL_GAUSSIAN_BLUR_5x5;
        break;
      default:
        res = KERNEL_EMPTY;
        break;
    }
    final int size = res.length;
    switch (kernelDirection) {
      case EAST: {
        int[][] rotKernel = new int[size][size];
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
            rotKernel[j][i] = res[size - 1 - i][j];
          }
        }
        return rotKernel;
      }
      case WEST: {
        int[][] rotKernel = new int[size][size];
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
            rotKernel[j][i] = res[i][size - 1 - j];
          }
        }
        return rotKernel;
      }
      case SOUTH: {
        int[][] rotKernel = new int[size][size];
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
            rotKernel[j][i] = res[size - 1 - j][size - 1 - i];
          }
        }
        return rotKernel;
      }
      default:
        return res;
    }

  }

  public static class EdgeModeEditor extends ComboBoxPropertyEditor {
    public EdgeModeEditor() {
      super();
      setAvailableValues(new EdgeMode[] { EdgeMode.MIRROR, EdgeMode.WRAP, EdgeMode.BLACK });
    }
  }

  public static class ColorModeEditor extends ComboBoxPropertyEditor {
    public ColorModeEditor() {
      super();
      setAvailableValues(new ColorMode[] { ColorMode.COLOR, ColorMode.GREY });
    }
  }

  public static class KernelTypeEditor extends ComboBoxPropertyEditor {
    public KernelTypeEditor() {
      super();
      setAvailableValues(new KernelType[] { KernelType.EDGE_DETECT_3x3, KernelType.EMBOSS_3x3,
          KernelType.LAPLACE_3x3, KernelType.SHARPEN_3x3, KernelType.SHARPEN_LESS_3x3,
          KernelType.SOBEL_3X3, KernelType.GAUSSIAN_BLUR_3x3, KernelType.GAUSSIAN_BLUR_5x5 });
    }
  }

  public static class KernelDirectionEditor extends ComboBoxPropertyEditor {
    public KernelDirectionEditor() {
      super();
      setAvailableValues(new KernelDirection[] { KernelDirection.NORTH, KernelDirection.EAST,
          KernelDirection.SOUTH, KernelDirection.WEST });
    }
  }

  public int getTransparency() {
    return transparency;
  }

  public void setTransparency(int transparency) {
    this.transparency = transparency;
  }

  public int getBias() {
    return bias;
  }

  public void setBias(int bias) {
    this.bias = bias;
  }

  public EdgeMode getEdgeMode() {
    return edgeMode;
  }

  public void setEdgeMode(EdgeMode edgeMode) {
    this.edgeMode = edgeMode;
  }

  public KernelType getKernelType() {
    return kernelType;
  }

  public void setKernelType(KernelType kernelType) {
    this.kernelType = kernelType;
  }

  public ColorMode getColorMode() {
    return colorMode;
  }

  public void setColorMode(ColorMode colorMode) {
    this.colorMode = colorMode;
  }

  public KernelDirection getKernelDirection() {
    return kernelDirection;
  }

  public void setKernelDirection(KernelDirection kernelDirection) {
    this.kernelDirection = kernelDirection;
  }
}
