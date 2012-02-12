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
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.ImageBufferComboBoxEditor;
import org.jwildfire.swing.ScaleAspectEditor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Bump3DTransformer extends Mesh3DTransformer {
  public enum SmoothingMatrix {
    MATRIX_3x3, MATRIX_5x5,
  }

  @Property(category = PropertyCategory.PRIMARY, description = "Image which holds the height information", editorClass = ImageBufferComboBoxEditor.class)
  private Buffer heightMap;
  @Property(category = PropertyCategory.PRIMARY, description = "Maximum displacement amount")
  private double amount = 10.0;
  @Property(category = PropertyCategory.PRIMARY, description = "Size of the smoothing matrix (larger -> smoother and slower)", editorClass = SmoothingMatrixEditor.class)
  private SmoothingMatrix smoothingMatrix = SmoothingMatrix.MATRIX_3x3;
  @Property(category = PropertyCategory.SECONDARY, description = "Left offset of the height map")
  private int hLeft = 0;
  @Property(category = PropertyCategory.SECONDARY, description = "Top offset of the height map")
  private int hTop = 0;
  @Property(category = PropertyCategory.SECONDARY, description = "Scaled width of the height map")
  private int hWidth = 400;
  @Property(category = PropertyCategory.SECONDARY, description = "Scaled height of the height map")
  private int hHeight = 400;
  @Property(category = PropertyCategory.SECONDARY, description = "How to treat the aspect ration of the height map", editorClass = ScaleAspectEditor.class)
  private ScaleAspect aspect = ScaleAspect.IGNORE;
  @Property(category = PropertyCategory.SECONDARY, description = "Treat the lowest intensity of the heightmap as zero intensity")
  private boolean noGround = true;
  @Property(category = PropertyCategory.SECONDARY, description = "Centre the height map")
  private boolean hCentre = true;

  private double lumMin, lumMax, lumRange;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int pCount = pMesh3D.getPCount();
    int width = pImageWidth;
    int height = pImageHeight;
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();

    WFImage heightMap = this.heightMap.getHDRImage();
    if (heightMap != null) {
      int hwidth = heightMap.getImageWidth();
      int hheight = heightMap.getImageHeight();
      if ((hwidth != this.hWidth) || (hheight != this.hHeight)) {
        throw new IllegalArgumentException("Heightmap has the wrong size (scaling of HDR images currently not supported)");
      }
      float lum[] = new float[2];
      ((SimpleHDRImage) heightMap).getMinMaxLum(lum);
      lumMin = lum[0];
      lumMax = lum[1];
      lumRange = lumMax - lumMin;
    }
    else {
      heightMap = this.heightMap.getImage();
      int hwidth = heightMap.getImageWidth();
      int hheight = heightMap.getImageHeight();
      if ((hwidth != this.hWidth) || (hheight != this.hHeight)) {
        SimpleImage scaledHeightMap = ((SimpleImage) heightMap).clone();
        ScaleTransformer scaleT = new ScaleTransformer();
        scaleT.setAspect(this.aspect);
        scaleT.setUnit(ScaleTransformer.Unit.PIXELS);
        scaleT.setScaleWidth(this.hWidth);
        scaleT.setScaleHeight(this.hHeight);
        scaleT.performImageTransformation(scaledHeightMap);
        heightMap = scaledHeightMap;
      }
    }

    double amount = 0.0 - this.amount;
    int dx = hLeft - width / 2;
    int dy = hTop - height / 2;
    if (hCentre) {
      dx += (width - this.hWidth) / 2;
      dy += (height - this.hHeight) / 2;
    }
    double[][] weights, intArray;
    if (this.smoothingMatrix == SmoothingMatrix.MATRIX_3x3) {
      int smoothSize = 3;
      weights = weights_3x3;
      intArray = new double[smoothSize][smoothSize];
    }
    else {
      int smoothSize = 5;
      weights = weights_5x5;
      intArray = new double[smoothSize][smoothSize];
    }

    double zmin = 0.0, zmax = 0.0;
    for (int i = 0; i < pCount; i++) {
      int xx = (int) (x[i] - (double) dx + 0.5);
      int yy = (int) (y[i] - (double) dy + 0.5);
      if ((xx >= 0) && (xx < this.hWidth) && (yy >= 0) && (yy < this.hHeight)) {
        readPixels(heightMap, xx, yy, intArray);
        double intensity = getWeightedIntensity(intArray, weights) * amount;

        if (intensity < zmin)
          zmin = intensity;
        else if (intensity > zmax)
          zmax = intensity;
        z[i] += intensity;
      }
    }
    // Subtract ground
    double fam = (zmax - zmin) / 2.0 + zmin;
    if ((fam != 0.0) && (noGround)) {
      for (int i = 0; i < pCount; i++) {
        z[i] -= fam;
      }
    }

  }

  private double getWeightedIntensity(double[][] pIntArray, double[][] pWeights) {
    double res = 0.0;
    int size = pIntArray.length;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        res += pIntArray[i][j] * pWeights[i][j];
      }
    }
    return res;
  }

  private void readPixels(WFImage pHeightMap, int pX, int pY, double[][] pIntArray) {
    int size = pIntArray.length;
    for (int i = 0; i < size; i++) {
      int y = pY - size / 2 + i;
      for (int j = 0; j < size; j++) {
        int x = pX - size / 2 + j;
        if (pHeightMap instanceof SimpleHDRImage) {
          pIntArray[i][j] = ((((SimpleHDRImage) pHeightMap).getLumIgnoreBounds(x, y)) - lumMin) / lumRange;
        }
        else {
          pIntArray[i][j] = (double) (((SimpleImage) pHeightMap).getRValueIgnoreBounds(x, y)) / 255.0;
        }
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    hLeft = 0;
    hTop = 0;
    this.hWidth = width;
    this.hHeight = height;
    aspect = ScaleAspect.IGNORE;
    amount = Math.round(rr / 80.0);
    noGround = true;
    smoothingMatrix = SmoothingMatrix.MATRIX_3x3;
  }

  public Buffer getHeightMap() {
    return heightMap;
  }

  public void setHeightMap(Buffer heightMap) {
    this.heightMap = heightMap;
  }

  public int getHLeft() {
    return hLeft;
  }

  public void setHLeft(int hLeft) {
    this.hLeft = hLeft;
  }

  public int getHTop() {
    return hTop;
  }

  public void setHTop(int hTop) {
    this.hTop = hTop;
  }

  public int getHWidth() {
    return hWidth;
  }

  public void setHWidth(int hWidth) {
    this.hWidth = hWidth;
  }

  public int getHHeight() {
    return hHeight;
  }

  public void setHHeight(int hHeight) {
    this.hHeight = hHeight;
  }

  public ScaleAspect getAspect() {
    return aspect;
  }

  public void setAspect(ScaleAspect aspect) {
    this.aspect = aspect;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public boolean isNoGround() {
    return noGround;
  }

  public void setNoGround(boolean noGround) {
    this.noGround = noGround;
  }

  public boolean isHCentre() {
    return hCentre;
  }

  public void setHCentre(boolean hCentre) {
    this.hCentre = hCentre;
  }

  public static class SmoothingMatrixEditor extends ComboBoxPropertyEditor {
    public SmoothingMatrixEditor() {
      super();
      setAvailableValues(new SmoothingMatrix[] { SmoothingMatrix.MATRIX_3x3,
          SmoothingMatrix.MATRIX_5x5 });
    }
  }

  public SmoothingMatrix getSmoothingMatrix() {
    return smoothingMatrix;
  }

  public void setSmoothingMatrix(SmoothingMatrix smoothingMatrix) {
    this.smoothingMatrix = smoothingMatrix;
  }

  private final static double[][] weights_3x3 = { { 0.07, 0.11, 0.07 }, { 0.11, 0.28, 0.11 },
      { 0.07, 0.11, 0.07 } };
  private final static double[][] weights_5x5 = { { 0.01, 0.015, 0.035, 0.015, 0.01 },
      { 0.015, 0.05, 0.08, 0.05, 0.015 }, { 0.035, 0.08, 0.18, 0.08, 0.035 },
      { 0.015, 0.05, 0.08, 0.05, 0.015 }, { 0.01, 0.015, 0.035, 0.015, 0.01 } };

}
