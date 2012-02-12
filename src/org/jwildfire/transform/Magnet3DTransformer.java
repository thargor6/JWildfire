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
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Magnet3DTransformer extends Mesh3DTransformer {

  public enum Shape {
    SHARP, SMOOTH
  }

  @Property(description = "Strength of the effect")
  @PropertyMin(0.0)
  private double strength = 1.0;

  @Property(description = "X-coordinate of the source point")
  private double srcX = 400.0;

  @Property(description = "Y-coordinate of the source point")
  private double srcY = 400.0;

  @Property(description = "Z-coordinate of the source point")
  private double srcZ = 0.0;

  @Property(description = "X-coordinate of the destination")
  private double dstX = 400.0;

  @Property(description = "Y-coordinate of the destination")
  private double dstY = 400.0;

  @Property(description = "Z-coordinate of the destination")
  private double dstZ = 100.0;

  @Property(description = "Shape of the effect", editorClass = ShapeEditor.class)
  private Shape shape = Shape.SMOOTH;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int width = pImageWidth;
    int height = pImageHeight;
    int pCount = pMesh3D.getPCount();
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();

    double srcX = this.srcX - (double) width / 2.0;
    double srcY = this.srcY - (double) height / 2.0;
    double srcZ = 0.0 - this.srcZ;
    double dstX = this.dstX - (double) width / 2.0;
    double dstY = this.dstY - (double) height / 2.0;
    double dstZ = 0.0 - this.dstZ;

    double strength = this.strength;

    strength = strength * 0.001;
    if (strength < 0.0)
      strength = 0.0 - strength;

    if ((Math.abs(srcX - dstX) < 0.000001) && (Math.abs(srcY - dstY) < 0.000001)
        && (Math.abs(srcZ - dstZ) < 0.000001))
      return;
    if (shape == Shape.SHARP) {
      for (int i = 0; i < pCount; i++) {
        double dx = x[i] - srcX;
        double dy = y[i] - srcY;
        double dz = z[i] - srcZ;
        double dr = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dr < 0.00001)
          dr = 0.00001;
        double F = Math.exp(-dr * dr * strength);
        dx = x[i] - dstX;
        dy = y[i] - dstY;
        dz = z[i] - dstZ;
        x[i] -= dx * F;
        y[i] -= dy * F;
        z[i] -= dz * F;
      }
    }
    else {
      dstX = srcX + srcX - dstX;
      dstY = srcY + srcY - dstY;
      dstZ = srcZ + srcZ - dstZ;
      for (int i = 0; i < pCount; i++) {
        double dx = x[i] - srcX;
        double dy = y[i] - srcY;
        double dz = z[i] - srcZ;
        double dr = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dr < 0.00001)
          dr = 0.00001;
        double F = Math.exp(-dr * dr * strength);
        dx = x[i] - dstX;
        dy = y[i] - dstY;
        dz = z[i] - dstZ;
        x[i] += dx * F;
        y[i] += dy * F;
        z[i] += dz * F;
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    srcX = Math.round((double) width / 2.2);
    srcY = Math.round((double) height / 2.4);
    srcZ = 0.0;
    dstX = Math.round((double) width / 2.2);
    dstY = Math.round((double) height / 2.4);
    dstZ = Math.round(rr / 10);
    strength = 0.5;
    shape = Shape.SMOOTH;
    alpha = 10.0;
    beta = 70.0;
  }

  public double getStrength() {
    return strength;
  }

  public void setStrength(double strength) {
    this.strength = strength;
  }

  public double getSrcX() {
    return srcX;
  }

  public void setSrcX(double srcX) {
    this.srcX = srcX;
  }

  public double getSrcY() {
    return srcY;
  }

  public void setSrcY(double srcY) {
    this.srcY = srcY;
  }

  public double getSrcZ() {
    return srcZ;
  }

  public void setSrcZ(double srcZ) {
    this.srcZ = srcZ;
  }

  public double getDstX() {
    return dstX;
  }

  public void setDstX(double dstX) {
    this.dstX = dstX;
  }

  public double getDstY() {
    return dstY;
  }

  public void setDstY(double dstY) {
    this.dstY = dstY;
  }

  public double getDstZ() {
    return dstZ;
  }

  public void setDstZ(double dstZ) {
    this.dstZ = dstZ;
  }

  public Shape getShape() {
    return shape;
  }

  public void setShape(Shape shape) {
    this.shape = shape;
  }

  public static class ShapeEditor extends ComboBoxPropertyEditor {
    public ShapeEditor() {
      super();
      setAvailableValues(new Shape[] { Shape.SMOOTH, Shape.SHARP });
    }
  }
}
