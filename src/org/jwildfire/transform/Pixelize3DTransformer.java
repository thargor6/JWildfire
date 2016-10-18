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

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer.BufferType;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Pixelize3DTransformer extends Mesh3DTransformer {
  public enum Genlock {
    OFF, COLOR, IN_RANGE, OUT_RANGE
  }

  public enum BlockType {
    SIMPLE, DETAILED
  }

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed;

  @Property(description = "Block width")
  @PropertyMin(2)
  private int blockWidth;

  @Property(description = "Block height")
  @PropertyMin(2)
  private int blockHeight;

  @Property(description = "Block thickness")
  @PropertyMin(2)
  private int blockThickness;

  @Property(description = "Centre blocks")
  private boolean centre = true;

  @Property(description = "Block scale")
  @PropertyMin(0.0)
  private double blockScale;

  @Property(description = "Maximum block scale variance")
  @PropertyMin(0.0)
  private double blockScaleVariance;

  @Property(description = "Rotation angle alpha")
  @PropertyMin(-360)
  @PropertyMax(360)
  protected double blockAngleAlpha = 0.0;

  @Property(description = "Rotation angle beta")
  @PropertyMin(-360)
  @PropertyMax(360)
  protected double blockAngleBeta = 0.0;

  @Property(description = "Maximum block rotation variance")
  @PropertyMin(0.0)
  private double blockAngleVariance;

  @Property(description = "Genlock mode (suppress the creation of blocks)", editorClass = GenlockEditor.class)
  private Genlock genlock;
  @Property(description = "Genlock color A")
  private Color genlockColorA;
  @Property(description = "Genlock color B")
  private Color genlockColorB;

  @Property(description = "Block type", editorClass = BlockTypeEditor.class)
  private BlockType blockType;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    centre = true;
    blockType = BlockType.SIMPLE;
    blockWidth = pImg.getImageWidth() / 25;
    blockHeight = pImg.getImageHeight() / 25;
    blockThickness = (int) (Math.sqrt(blockWidth * blockWidth + blockHeight * blockHeight) * 0.5 + 0.5);
    blockScale = 0.7;
    blockScaleVariance = 0.0;
    seed = 123;

    genlock = Genlock.OFF;
    genlockColorA = new Color(0, 0, 0);
    genlockColorB = new Color(0, 0, 0);

    blockScale = 0.5;
    zoom = 1.0;
    ambient = 0.7;
  }

  @Override
  public boolean acceptsInputBufferType(BufferType pBufferType) {
    return (pBufferType == BufferType.IMAGE);
  }

  @Override
  protected void createMeshFromImage(Mesh3D pMesh3D, SimpleImage pImg, double pQuant3D) {
    Tools.srand123(seed);
    int imgWidth = pImg.getImageWidth();
    int imgHeight = pImg.getImageHeight();
    double xMin = -(double) imgWidth * 0.5;
    double yMin = -(double) imgHeight * 0.5;
    double zsize2 = blockThickness * 0.5;

    // allocate structure
    Block block;
    switch (blockType) {
      case SIMPLE:
        block = new SimpleBlock(blockWidth, blockHeight);
        break;
      case DETAILED:
        block = new DetailedBlock(blockWidth, blockHeight);
        break;
      default:
        throw new IllegalStateException();
    }

    int rectXCount = imgWidth / blockWidth + (imgWidth % blockWidth > 0 ? 1 : 0);
    int rectYCount = imgHeight / blockHeight + (imgHeight % blockHeight > 0 ? 1 : 0);
    int rectCount = rectXCount * rectYCount;
    int rectPCount = block.estimatePCount();
    int rectFCount = block.estimateFCount();

    int pCount = rectCount * rectPCount;
    int currP = 0;
    int fCount = rectCount * rectFCount;
    int currF = 0;
    double x[] = new double[pCount];
    double y[] = new double[pCount];
    double z[] = new double[pCount];

    int pp1[] = new int[fCount];
    int pp2[] = new int[fCount];
    int pp3[] = new int[fCount];

    int color[] = new int[fCount];
    // create structure
    int dx = centre ? (imgWidth % blockWidth) / 2 : 0;
    int dy = centre ? (imgHeight % blockHeight) / 2 : 0;

    for (int row = 0; row < imgHeight;) {
      int y1 = row - dy;
      if (y1 < 0)
        y1 = 0;
      int y2 = row - dy + blockHeight;
      if (y2 > imgHeight - dy)
        y2 = imgHeight - dy;
      for (int col = 0; col < imgWidth;) {
        int x1 = col - dx;
        if (x1 < 0)
          x1 = 0;
        int x2 = col - dx + blockWidth;
        if (x2 > imgWidth - dx)
          x2 = imgWidth - dx;
        Pixel p = block.computeAVGColor(pImg, x1, x2, y1, y2);
        if (!filteredByGenlock(p)) {
          block.prepareTransformation(blockScale, blockScaleVariance, blockAngleAlpha, blockAngleBeta, blockAngleVariance);
          block.createBlock(pImg, x1, x2, y1, y2, zsize2, p.getARGBValue());
          block.transformBlock(x1, x2, y1, y2, xMin, yMin);
          block.addToMainStructure(x, y, z, pp1, pp2, pp3, color, currP, currF);
          currF += block.getFCount();
          currP += block.getPCount();
        }
        else
          System.out.println("GENLOCK");

        col += blockWidth;
      }
      row += blockHeight;
    }
    //
    System.out.println("FACES: " + currF + " (" + fCount + ")");
    System.out.println("POINTS: " + currP + " (" + pCount + ")");

    pMesh3D.setFCount(currF);
    pMesh3D.setPCount(currP);
    pMesh3D.setX(x);
    pMesh3D.setY(y);
    pMesh3D.setZ(z);
    pMesh3D.setPP1(pp1);
    pMesh3D.setPP2(pp2);
    pMesh3D.setPP3(pp3);
    pMesh3D.setColor(color);
    pMesh3D.setImageWidth(imgWidth);
    pMesh3D.setImageHeight(imgHeight);
  }

  private boolean filteredByGenlock(Pixel pPixel) {
    switch (this.genlock) {
      case OFF:
        return false;
      case COLOR:
        return !(pPixel.r != genlockColorA.getRed() || pPixel.g != genlockColorA.getGreen() || pPixel.b != genlockColorA.getBlue());
      case IN_RANGE:
        return (((pPixel.r >= genlockColorA.getRed() && pPixel.r <= genlockColorB.getRed()) || (pPixel.r >= genlockColorB.getRed() && pPixel.r <= genlockColorA.getRed()))
            || ((pPixel.g >= genlockColorA.getGreen() && pPixel.g <= genlockColorB.getGreen()) || (pPixel.g >= genlockColorB.getGreen() && pPixel.g <= genlockColorA.getGreen())) || ((pPixel.b >= genlockColorA.getBlue() && pPixel.b <= genlockColorB.getBlue()) || (pPixel.b >= genlockColorB.getBlue() && pPixel.b <= genlockColorA.getBlue())));
      case OUT_RANGE:
        return !(((pPixel.r >= genlockColorA.getRed() && pPixel.r <= genlockColorB.getRed()) || (pPixel.r >= genlockColorB.getRed() && pPixel.r <= genlockColorA.getRed()))
            || ((pPixel.g >= genlockColorA.getGreen() && pPixel.g <= genlockColorB.getGreen()) || (pPixel.g >= genlockColorB.getGreen() && pPixel.g <= genlockColorA.getGreen())) || ((pPixel.b >= genlockColorA.getBlue() && pPixel.b <= genlockColorB.getBlue()) || (pPixel.b >= genlockColorB.getBlue() && pPixel.b <= genlockColorA.getBlue())));
      default:
        throw new IllegalStateException();
    }
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

  public boolean isCentre() {
    return centre;
  }

  public void setCentre(boolean centre) {
    this.centre = centre;
  }

  public double getBlockScale() {
    return blockScale;
  }

  public void setBlockScale(double blockScale) {
    this.blockScale = blockScale;
  }

  public double getBlockScaleVariance() {
    return blockScaleVariance;
  }

  public void setBlockScaleVariance(double blockScaleVariance) {
    this.blockScaleVariance = blockScaleVariance;
  }

  public double getBlockAngleAlpha() {
    return blockAngleAlpha;
  }

  public void setBlockAngleAlpha(double blockAngleAlpha) {
    this.blockAngleAlpha = blockAngleAlpha;
  }

  public double getBlockAngleBeta() {
    return blockAngleBeta;
  }

  public void setBlockAngleBeta(double blockAngleBeta) {
    this.blockAngleBeta = blockAngleBeta;
  }

  public double getBlockAngleVariance() {
    return blockAngleVariance;
  }

  public void setBlockAngleVariance(double blockAngleVariance) {
    this.blockAngleVariance = blockAngleVariance;
  }

  private static abstract class Block {
    protected int blockWidth;
    protected int blockHeight;
    protected int pCount;
    protected int fCount;
    protected double x[];
    protected double y[];
    protected double z[];

    protected int pp1[];
    protected int pp2[];
    protected int pp3[];

    protected int color[];

    protected double scale;
    protected double alpha;
    protected double beta;

    public abstract int estimatePCount();

    public abstract int estimateFCount();

    public abstract void createBlock(SimpleImage pImg, int pX1, int pX2, int pY1, int pY2, double pZSize2, int pAvgColor);

    protected void transformBlock(int pX1, int pX2, int pY1, int pY2, double pXMin, double pYMin) {
      double zeroX = (double) (pX2 - pX1) * 0.5 + (double) pX1;
      double zeroY = (double) (pY2 - pY1) * 0.5 + (double) pY1;
      // rotate
      Rotate doRotate = Rotate.XY;
      double sinA = Math.sin(alpha);
      double cosA = Math.cos(alpha);
      double sinB = Math.sin(beta);
      double cosB = Math.cos(beta);
      if (doRotate == Rotate.XY) {
        double sinBsinA = sinB * sinA;
        double cosBsinA = cosB * sinA;
        double sinBcosA = sinB * cosA;
        double cosBcosA = cosB * cosA;
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i];
          x[i] = cosA * dx - sinBsinA * dy + cosBsinA * dz + zeroX;
          y[i] = cosB * dy + sinB * dz + zeroY;
          z[i] = -sinA * dx - sinBcosA * dy + cosBcosA * dz;
        }
      }
      else if (doRotate == Rotate.YZ) {
        double cosAcosB = cosA * cosB;
        double sinAcosB = sinA * cosB;
        double cosAsinB = cosA * sinB;
        double sinAsinB = sinA * sinB;
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i];
          x[i] = cosAcosB * dx + sinAcosB * dy + sinB * dz + zeroX;
          y[i] = -sinA * dx + cosA * dy + zeroY;
          z[i] = -cosAsinB * dx - sinAsinB * dy + cosB * dz;
        }
      }
      else if (doRotate == Rotate.XZ) {
        double cosBsinA = cosB * sinA;
        double sinBsinA = sinB * sinA;
        double cosBcosA = cosB * cosA;
        double sinBcosA = sinB * cosA;
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i];
          x[i] = cosA * dx + cosBsinA * dy + sinBsinA * dz + zeroX;
          y[i] = -sinA * dx + cosBcosA * dy + sinBcosA * dz + zeroY;
          z[i] = -sinB * dy + cosB * dz;
        }
      }
      // scale
      for (int i = 0; i < pCount; i++) {
        x[i] = (x[i] - zeroX) * scale + zeroX + pXMin;
        y[i] = (y[i] - zeroY) * scale + zeroY + pYMin;
        z[i] = z[i] * scale;
      }
    }

    protected Pixel computeAVGColor(SimpleImage pImg, int pX1, int pX2, int pY1, int pY2) {
      Pixel p = new Pixel();
      double red = 0.0;
      double green = 0.0;
      double blue = 0.0;
      int count = 0;
      for (int i = pX1; i < pX2; i++) {
        for (int j = pY1; j < pY2; j++) {
          p.setARGBValue(pImg.getARGBValue(i, j));
          red += p.r;
          green += p.g;
          blue += p.b;
          count++;
        }
      }
      p.r = Tools.roundColor(red / (double) count);
      p.g = Tools.roundColor(green / (double) count);
      p.b = Tools.roundColor(blue / (double) count);
      return p;
    }

    protected void prepareTransformation(double pScale, double pScaleVariance, double pAlphaDeg, double pBetaDeg, double pAngleVarianceDeg) {
      scale = pScale;
      if (Math.abs(pScaleVariance) > MathLib.EPSILON) {
        if (Tools.drand() > 0.5) {
          scale += pScaleVariance * Tools.drand();
        }
        else {
          scale -= pScaleVariance * Tools.drand();
        }
      }

      double alphaDeg = pAlphaDeg;
      if (Math.abs(pAngleVarianceDeg) > MathLib.EPSILON) {
        if (Tools.drand() > 0.5) {
          alphaDeg += pAngleVarianceDeg * Tools.drand();
        }
        else {
          alphaDeg -= pAngleVarianceDeg * Tools.drand();
        }
      }
      alpha = alphaDeg * Math.PI / 180.0;

      double betaDeg = pBetaDeg;
      if (Math.abs(pAngleVarianceDeg) > MathLib.EPSILON) {
        if (Tools.drand() > 0.5) {
          betaDeg += pAngleVarianceDeg * Tools.drand();
        }
        else {
          betaDeg -= pAngleVarianceDeg * Tools.drand();
        }
      }
      beta = betaDeg * Math.PI / 180.0;
    }

    public void addToMainStructure(double[] pX, double[] pY, double[] pZ, int[] pPP1, int[] pPP2, int[] pPP3, int[] pColor, int pCurrP, int pCurrF) {
      for (int i = 0; i < pCount; i++) {
        pX[pCurrP + i] = x[i];
        pY[pCurrP + i] = y[i];
        pZ[pCurrP + i] = z[i];
      }
      for (int i = 0; i < fCount; i++) {
        pPP1[pCurrF + i] = pp1[i] + pCurrP;
        pPP2[pCurrF + i] = pp2[i] + pCurrP;
        pPP3[pCurrF + i] = pp3[i] + pCurrP;
        pColor[pCurrF + i] = color[i];
      }
    }

    public int getPCount() {
      return pCount;
    }

    public int getFCount() {
      return fCount;
    }
  }

  private static class SimpleBlock extends Block {
    private final static int PCOUNT = 8;
    private final static int FCOUNT = 12;

    public SimpleBlock(int pBlockWidth, int pBlockHeight) {
      blockWidth = pBlockWidth;
      blockHeight = pBlockHeight;

      x = new double[PCOUNT];
      y = new double[PCOUNT];
      z = new double[PCOUNT];

      pp1 = new int[FCOUNT];
      pp2 = new int[FCOUNT];
      pp3 = new int[FCOUNT];
      color = new int[FCOUNT];
    }

    @Override
    public int estimatePCount() {
      return PCOUNT;
    }

    @Override
    public int estimateFCount() {
      return FCOUNT;
    }

    @Override
    public void createBlock(SimpleImage pImg, int pX1, int pX2, int pY1, int pY2, double pZSize2, int pAvgColor) {
      pCount = 0;
      fCount = 0;
      int currColor = pAvgColor;

      // points
      //       7 6
      //       4 5
      //     /
      // 3 2 
      // 0 1
      x[pCount] = pX1;
      y[pCount] = pY2;
      z[pCount++] = -pZSize2;

      x[pCount] = pX2;
      y[pCount] = pY2;
      z[pCount++] = -pZSize2;

      x[pCount] = pX2;
      y[pCount] = pY1;
      z[pCount++] = -pZSize2;

      x[pCount] = pX1;
      y[pCount] = pY1;
      z[pCount++] = -pZSize2;

      x[pCount] = pX1;
      y[pCount] = pY2;
      z[pCount++] = pZSize2;

      x[pCount] = pX2;
      y[pCount] = pY2;
      z[pCount++] = pZSize2;

      x[pCount] = pX2;
      y[pCount] = pY1;
      z[pCount++] = pZSize2;

      x[pCount] = pX1;
      y[pCount] = pY1;
      z[pCount++] = pZSize2;

      // faces
      // 0 1 2
      pp1[fCount] = 0;
      pp2[fCount] = 1;
      pp3[fCount] = 2;
      color[fCount++] = currColor;
      // 0 2 3
      pp1[fCount] = 0;
      pp2[fCount] = 2;
      pp3[fCount] = 3;
      color[fCount++] = currColor;
      // 1 5 6
      pp1[fCount] = 1;
      pp2[fCount] = 5;
      pp3[fCount] = 6;
      color[fCount++] = currColor;
      // 1 6 2
      pp1[fCount] = 1;
      pp2[fCount] = 6;
      pp3[fCount] = 2;
      color[fCount++] = currColor;
      // 5 4 6
      pp1[fCount] = 5;
      pp2[fCount] = 4;
      pp3[fCount] = 6;
      color[fCount++] = currColor;
      // 4 7 6
      pp1[fCount] = 4;
      pp2[fCount] = 7;
      pp3[fCount] = 6;
      color[fCount++] = currColor;
      // 4 0 3
      pp1[fCount] = 4;
      pp2[fCount] = 0;
      pp3[fCount] = 3;
      color[fCount++] = currColor;
      // 3 7 4
      pp1[fCount] = 3;
      pp2[fCount] = 7;
      pp3[fCount] = 4;
      color[fCount++] = currColor;
      // 3 2 7
      pp1[fCount] = 3;
      pp2[fCount] = 2;
      pp3[fCount] = 7;
      color[fCount++] = currColor;
      // 2 6 7
      pp1[fCount] = 2;
      pp2[fCount] = 6;
      pp3[fCount] = 7;
      color[fCount++] = currColor;
      // 0 4 5
      pp1[fCount] = 0;
      pp2[fCount] = 4;
      pp3[fCount] = 5;
      color[fCount++] = currColor;
      // 0 5 1
      pp1[fCount] = 0;
      pp2[fCount] = 5;
      pp3[fCount] = 1;
      color[fCount++] = currColor;
    }
  }

  private static class DetailedBlock extends Block {
    private static final int ZSEGMENTS = 3;

    public DetailedBlock(int pBlockWidth, int pBlockHeight) {
      blockWidth = pBlockWidth;
      blockHeight = pBlockHeight;

      x = new double[estimatePCount()];
      y = new double[estimatePCount()];
      z = new double[estimatePCount()];

      pp1 = new int[estimateFCount()];
      pp2 = new int[estimateFCount()];
      pp3 = new int[estimateFCount()];
      color = new int[estimateFCount()];
    }

    @Override
    public int estimatePCount() {
      return (blockWidth + 1) * (blockHeight + 1) + // front
          ZSEGMENTS * ((blockWidth + 1) * 2 + (blockHeight + 1) * 2) + // segments 
          (blockWidth + 1) * (blockHeight + 1);//   back

    }

    @Override
    public int estimateFCount() {
      return blockWidth * blockHeight * 2 + // front
          blockHeight * 2 * (ZSEGMENTS + 1) + // right side
          blockHeight * 2 * (ZSEGMENTS + 1) + // left side
          blockWidth * 2 * (ZSEGMENTS + 1) + // top side
          blockWidth * 2 * (ZSEGMENTS + 1) + // bottom side
          blockWidth * blockHeight * 2; // back
    }

    @Override
    public void createBlock(SimpleImage pImg, int pX1, int pX2, int pY1, int pY2, double pZSize2, int pAvgColor) {
      pCount = 0;
      fCount = 0;
      // point order:
      //  front:
      //    0 1 2 3 
      //    4 5 6 7
      //  back:
      //   8   9 10 11
      //   12 13 14 15

      // front
      for (int i = pY1; i <= pY2; i++) {
        for (int j = pX1; j <= pX2; j++) {
          x[pCount] = j;
          y[pCount] = i;
          z[pCount++] = -pZSize2;
        }
      }
      int pBackOffSet = pCount;
      // back
      for (int i = pY1; i <= pY2; i++) {
        for (int j = pX1; j <= pX2; j++) {
          x[pCount] = j;
          y[pCount] = i;
          z[pCount++] = +pZSize2;
        }
      }
      // segments
      double segSize = (2.0 * pZSize2) / (double) (ZSEGMENTS - 1);
      for (int i = 0; i < ZSEGMENTS; i++) {
        // top
        for (int j = pX1; j <= pX2; j++) {
          x[pCount] = j;
          y[pCount] = pY1;
          z[pCount++] = -pZSize2 + i * segSize;
        }
        // bottom
        for (int j = pX1; j <= pX2; j++) {
          x[pCount] = j;
          y[pCount] = pY2;
          z[pCount++] = -pZSize2 + i * segSize;
        }
        // left
        for (int j = pY1; j <= pY2; j++) {
          x[pCount] = pX1;
          y[pCount] = j;
          z[pCount++] = -pZSize2 + i * segSize;
        }
        // right
        for (int j = pY1; j <= pY2; j++) {
          x[pCount] = pX2;
          y[pCount] = j;
          z[pCount++] = -pZSize2 + i * segSize;
        }
      }

      int horizPCount = pX2 - pX1 + 1;
      int p1, p2, p3, p4;

      // faces front side
      for (int i = 0; i < pY2 - pY1; i++) {
        for (int j = 0; j < pX2 - pX1; j++) {
          int col = pImg.getARGBValue(j + pX1, i + pY1);
          p1 = i * horizPCount + j;
          p2 = i * horizPCount + (j + 1);
          p3 = (i + 1) * horizPCount + j;
          p4 = (i + 1) * horizPCount + (j + 1);
          addRect(p1, p2, p3, p4, col);
        }
      }
      // faces right side
      for (int i = 0; i < pY2 - pY1; i++) {
        p1 = i * horizPCount + (horizPCount - 1);
        p2 = i * horizPCount + pBackOffSet + (horizPCount - 1);
        p3 = (i + 1) * horizPCount + (horizPCount - 1);
        p4 = (i + 1) * horizPCount + pBackOffSet + (horizPCount - 1);
        addRect(p1, p2, p3, p4, pAvgColor);
      }
      // faces left side
      for (int i = 0; i < pY2 - pY1; i++) {
        p1 = i * horizPCount + pBackOffSet;
        p2 = i * horizPCount;
        p3 = (i + 1) * horizPCount + pBackOffSet;
        p4 = (i + 1) * horizPCount;
        addRect(p1, p2, p3, p4, pAvgColor);
      }
      // faces top side
      for (int i = 0; i < pX2 - pX1; i++) {
        p1 = i + pBackOffSet;
        p2 = i + 1 + pBackOffSet;
        p3 = i;
        p4 = i + 1;
        addRect(p1, p2, p3, p4, pAvgColor);
      }
      // faces bottom side
      for (int i = 0; i < pX2 - pX1; i++) {
        p1 = pBackOffSet - horizPCount + i;
        p2 = pBackOffSet - horizPCount + i + 1;
        p3 = 2 * pBackOffSet - horizPCount + i;
        p4 = 2 * pBackOffSet - horizPCount + i + 1;
        addRect(p1, p2, p3, p4, pAvgColor);
      }
      // faces back side
      for (int i = 0; i < pY2 - pY1; i++) {
        for (int j = 0; j < pX2 - pX1; j++) {
          int col = pImg.getARGBValue(j + pX1, i + pY1);
          p1 = i * horizPCount + (j + 1) + pBackOffSet;
          p2 = i * horizPCount + j + pBackOffSet;
          p3 = (i + 1) * horizPCount + (j + 1) + pBackOffSet;
          p4 = (i + 1) * horizPCount + j + pBackOffSet;
          addRect(p1, p2, p3, p4, col);
        }
      }

    }

    // 1 2
    // 3 4 
    private void addRect(int p1, int p2, int p3, int p4, int col) {
      pp1[fCount] = p1;
      pp2[fCount] = p3;
      pp3[fCount] = p2;
      color[fCount++] = col;

      pp1[fCount] = p2;
      pp2[fCount] = p3;
      pp3[fCount] = p4;
      color[fCount++] = col;
    }
  }

  public int getBlockWidth() {
    return blockWidth;
  }

  public void setBlockWidth(int blockWidth) {
    this.blockWidth = blockWidth;
  }

  public int getBlockHeight() {
    return blockHeight;
  }

  public void setBlockHeight(int blockHeight) {
    this.blockHeight = blockHeight;
  }

  public static class GenlockEditor extends ComboBoxPropertyEditor {
    public GenlockEditor() {
      super();
      setAvailableValues(new Genlock[] { Genlock.OFF, Genlock.COLOR, Genlock.IN_RANGE, Genlock.OUT_RANGE });
    }
  }

  public static class BlockTypeEditor extends ComboBoxPropertyEditor {
    public BlockTypeEditor() {
      super();
      setAvailableValues(new BlockType[] { BlockType.SIMPLE, BlockType.DETAILED });
    }
  }

  public int getBlockThickness() {
    return blockThickness;
  }

  public void setBlockThickness(int blockThickness) {
    this.blockThickness = blockThickness;
  }

  public Genlock getGenlock() {
    return genlock;
  }

  public void setGenlock(Genlock genlock) {
    this.genlock = genlock;
  }

  public Color getGenlockColorA() {
    return genlockColorA;
  }

  public void setGenlockColorA(Color genlockColorA) {
    this.genlockColorA = genlockColorA;
  }

  public Color getGenlockColorB() {
    return genlockColorB;
  }

  public void setGenlockColorB(Color genlockColorB) {
    this.genlockColorB = genlockColorB;
  }

  public BlockType getBlockType() {
    return blockType;
  }

  public void setBlockType(BlockType blockType) {
    this.blockType = blockType;
  }

}
