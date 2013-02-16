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
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Transform3DTransformer extends Mesh3DTransformer {
  public enum Rotate {
    XY, YZ, XZ, NONE
  }

  @Property(category = PropertyCategory.PRIMARY, description = "X-coordinate of the centre of the transformation")
  private double originX = 120.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Y-coordinate of the centre of the transformation")
  private double originY = 120.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Z-coordinate of the centre of the transformation")
  private double originZ = 0.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Object movement in x-direction")
  private double transX = 0.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Object movement in y-direction")
  private double transY = 0.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Object movement in z-direction")
  private double transZ = 0.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Object scale factor for x-direction")
  private double scaleX = 1.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Object scale factor for y-direction")
  private double scaleY = 1.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Object scale factor for z-direction")
  private double scaleZ = 1.0;

  @Property(category = PropertyCategory.RENDERING, description = "Object rotation angle alpha")
  @PropertyMin(-360)
  @PropertyMax(360)
  protected double objAlpha = 0.0;

  @Property(category = PropertyCategory.RENDERING, description = "Object rotation angle beta")
  @PropertyMin(-360)
  @PropertyMax(360)
  protected double objBeta = 0.0;

  @Property(category = PropertyCategory.RENDERING, description = "Object rotation mode", editorClass = RotateEditor.class)
  protected Rotate objRotate = Rotate.XY;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();
    int pCount = pMesh3D.getPCount();
    int width = pMesh3D.getImageWidth();
    int height = pMesh3D.getImageHeight();

    boolean doRotate = (objRotate != Rotate.NONE);
    boolean doTrans = (Math.abs(transX) > MathLib.EPSILON) || (Math.abs(transY) > MathLib.EPSILON)
        || (Math.abs(transZ) > MathLib.EPSILON);
    boolean doScale = (Math.abs(scaleX - 1.0) > MathLib.EPSILON)
        || (Math.abs(scaleY - 1.0) > MathLib.EPSILON) || (Math.abs(scaleX - 1.0) > MathLib.EPSILON)
        || (Math.abs(scaleZ - 1.0) > MathLib.EPSILON);

    double zeroX = (double) originX - (double) width / 2.0;
    double zeroY = (double) originY - (double) height / 2.0;
    double zeroZ = (double) originZ;

    if (doScale) {
      for (int i = 0; i < pCount; i++) {
        double dx = x[i] - zeroX;
        double dy = y[i] - zeroY;
        double dz = z[i] - zeroZ;
        x[i] = dx * scaleX + zeroX;
        y[i] = dy * scaleY + zeroY;
        z[i] = dz * scaleZ + zeroZ;
      }
    }
    if (doRotate) {
      double alpha = this.objAlpha * Math.PI / 180.0;
      double beta = this.objBeta * Math.PI / 180.0;
      double sinA = Math.sin(alpha);
      double cosA = Math.cos(alpha);
      double sinB = Math.sin(beta);
      double cosB = Math.cos(beta);
      if (objRotate == Rotate.XY) {
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i] - zeroZ;
          x[i] = cosA * dx - sinB * sinA * dy + cosB * sinA * dz + zeroX;
          y[i] = cosB * dy + sinB * dz + zeroY;
          z[i] = -sinA * dx - sinB * cosA * dy + cosB * cosA * dz + zeroZ;
        }
      }
      else if (objRotate == Rotate.YZ) {
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i] - zeroZ;
          x[i] = cosA * cosB * dx + sinA * cosB * dy + sinB * dz + zeroX;
          y[i] = -sinA * dx + cosA * dy + zeroY;
          z[i] = -cosA * sinB * dx - sinA * sinB * dy + cosB * dz + zeroZ;
        }
      }
      else /* xz */{
        for (int i = 0; i < pCount; i++) {
          double dx = x[i] - zeroX;
          double dy = y[i] - zeroY;
          double dz = z[i] - zeroZ;
          x[i] = cosA * dx + cosB * sinA * dy + sinB * sinA * dz + zeroX;
          y[i] = -sinA * dx + cosB * cosA * dy + sinB * cosA * dz + zeroY;
          z[i] = -sinB * dy + cosB * dz + zeroZ;
        }
      }
    }
    if (doTrans) {
      for (int i = 0; i < pCount; i++) {
        x[i] += transX;
        y[i] += transY;
        z[i] += transZ;
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    originX = (double) width / 2.0;
    originY = (double) height / 2.0;
    originZ = 0.0;
    transX = 0.0;
    transY = 0.0;
    transZ = 0.0;
    scaleX = 1.0;
    scaleY = 1.0;
    scaleZ = 1.0;
    objAlpha = 0.0;
    objBeta = 0.0;
    objRotate = Rotate.XY;
  }

  public static class RotateEditor extends ComboBoxPropertyEditor {
    public RotateEditor() {
      super();
      setAvailableValues(new Rotate[] { Rotate.XY, Rotate.YZ, Rotate.XZ, Rotate.NONE });
    }
  }

  public double getOriginX() {
    return originX;
  }

  public void setOriginX(double originX) {
    this.originX = originX;
  }

  public double getOriginY() {
    return originY;
  }

  public void setOriginY(double originY) {
    this.originY = originY;
  }

  public double getOriginZ() {
    return originZ;
  }

  public void setOriginZ(double originZ) {
    this.originZ = originZ;
  }

  public double getTransX() {
    return transX;
  }

  public void setTransX(double transX) {
    this.transX = transX;
  }

  public double getTransY() {
    return transY;
  }

  public void setTransY(double transY) {
    this.transY = transY;
  }

  public double getTransZ() {
    return transZ;
  }

  public void setTransZ(double transZ) {
    this.transZ = transZ;
  }

  public double getScaleX() {
    return scaleX;
  }

  public void setScaleX(double scaleX) {
    this.scaleX = scaleX;
  }

  public double getScaleY() {
    return scaleY;
  }

  public void setScaleY(double scaleY) {
    this.scaleY = scaleY;
  }

  public double getScaleZ() {
    return scaleZ;
  }

  public void setScaleZ(double scaleZ) {
    this.scaleZ = scaleZ;
  }

  public double getObjAlpha() {
    return objAlpha;
  }

  public void setObjAlpha(double objAlpha) {
    this.objAlpha = objAlpha;
  }

  public double getObjBeta() {
    return objBeta;
  }

  public void setObjBeta(double objBeta) {
    this.objBeta = objBeta;
  }

  public Rotate getObjRotate() {
    return objRotate;
  }

  public void setObjRotate(Rotate objRotate) {
    this.objRotate = objRotate;
  }

}
