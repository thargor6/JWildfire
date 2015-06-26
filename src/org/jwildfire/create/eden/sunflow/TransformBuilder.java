/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.eden.sunflow;

import java.lang.reflect.Field;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.eden.sunflow.base.PartBuilder;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point3f;
import org.sunflow.math.Matrix4;

public class TransformBuilder<TParent extends PrimitiveBuilder<?>> implements PartBuilder {
  private final TParent parent;
  protected final Point3f rotate = new Point3f(0.0, 0.0, 0.0);
  protected final Point3f scale = new Point3f(1.0, 1.0, 1.0);
  protected final Point3f translate = new Point3f(0.0, 0.0, 0.0);
  protected Matrix4 matrix = null;

  public TransformBuilder(TParent pParent) {
    parent = pParent;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    if (!isEmpty()) {
      if (matrix != null) {
        pTarget.append("  transform row ");
        pTarget.append(readMatrixValue(matrix, "m00") + " ");
        pTarget.append(readMatrixValue(matrix, "m01") + " ");
        pTarget.append(readMatrixValue(matrix, "m02") + " ");
        pTarget.append(readMatrixValue(matrix, "m03") + " ");
        pTarget.append(readMatrixValue(matrix, "m10") + " ");
        pTarget.append(readMatrixValue(matrix, "m11") + " ");
        pTarget.append(readMatrixValue(matrix, "m12") + " ");
        pTarget.append(readMatrixValue(matrix, "m13") + " ");
        pTarget.append(readMatrixValue(matrix, "m20") + " ");
        pTarget.append(readMatrixValue(matrix, "m21") + " ");
        pTarget.append(readMatrixValue(matrix, "m22") + " ");
        pTarget.append(readMatrixValue(matrix, "m23") + " ");
        pTarget.append("0 0 0 1\n");
      }
      else {
        pTarget.append("  transform {\n");
        if (doScale()) {
          if (areTheSame(scale.x, scale.y, scale.z)) {
            pTarget.append("    scaleu " + scale.x + "\n");
          }
          else {
            pTarget.append("    scale " + scale.x + " " + scale.y + " " + scale.z + "\n");
          }
        }
        if (doRotateX()) {
          pTarget.append("    rotatex " + rotate.x + "\n");
        }
        if (doRotateY()) {
          pTarget.append("    rotatey " + rotate.y + "\n");
        }
        if (doRotateZ()) {
          pTarget.append("    rotatez " + rotate.z + "\n");
        }
        if (doTranslate()) {
          pTarget.append("    translate " + translate.x + " " + translate.y + " " + translate.z + "\n");
        }
        pTarget.append("  }\n");
      }
    }
  }

  private float readMatrixValue(Matrix4 pMatrix, String pFieldname) {
    Field f;
    try {
      f = pMatrix.getClass().getDeclaredField(pFieldname);
      f.setAccessible(true);
      return f.getFloat(pMatrix);
    }
    catch (Exception e) {
      e.printStackTrace();
      return 0.0f;
    }
  }

  private boolean areTheSame(float pX, float pY, float pZ) {
    return MathLib.fabs(pY - pX) < MathLib.EPSILON && MathLib.fabs(pZ - pY) < MathLib.EPSILON && MathLib.fabs(pX - pZ) < MathLib.EPSILON;
  }

  public TParent close() {
    return parent;
  }

  private boolean doRotateX() {
    return MathLib.fabs(rotate.x) > MathLib.EPSILON;
  }

  private boolean doRotateY() {
    return MathLib.fabs(rotate.y) > MathLib.EPSILON;
  }

  private boolean doRotateZ() {
    return MathLib.fabs(rotate.z) > MathLib.EPSILON;
  }

  private boolean doTranslate() {
    return MathLib.fabs(translate.x) > MathLib.EPSILON || MathLib.fabs(translate.y) > MathLib.EPSILON || MathLib.fabs(translate.z) > MathLib.EPSILON;
  }

  private boolean doScale() {
    return MathLib.fabs(scale.x - 1.0) > MathLib.EPSILON || MathLib.fabs(scale.y - 1.0) > MathLib.EPSILON || MathLib.fabs(scale.z - 1.0) > MathLib.EPSILON;
  }

  private boolean isEmpty() {
    return !(matrix != null || doRotateX() || doRotateY() || doRotateZ() || doTranslate() || doScale());
  }

  public TransformBuilder<TParent> withRotateX(double pRotateX) {
    rotate.x = (float) pRotateX;
    return this;
  }

  public TransformBuilder<TParent> withRotateY(double pRotateY) {
    rotate.y = (float) pRotateY;
    return this;
  }

  public TransformBuilder<TParent> withRotateZ(double pRotateZ) {
    rotate.z = (float) pRotateZ;
    return this;
  }

  public TransformBuilder<TParent> withScaleX(double pScaleX) {
    scale.x = (float) pScaleX;
    return this;
  }

  public TransformBuilder<TParent> withScaleY(double pScaleY) {
    scale.y = (float) pScaleY;
    return this;
  }

  public TransformBuilder<TParent> withScaleZ(double pScaleZ) {
    scale.z = (float) pScaleZ;
    return this;
  }

  public TransformBuilder<TParent> withTranslateX(double pTranslateX) {
    translate.x = (float) pTranslateX;
    return this;
  }

  public TransformBuilder<TParent> withTranslateY(double pTranslateY) {
    translate.y = (float) pTranslateY;
    return this;
  }

  public TransformBuilder<TParent> withTranslateZ(double pTranslateZ) {
    translate.z = (float) pTranslateZ;
    return this;
  }

  public void withMatrix(Matrix4 pMatrix) {
    matrix = pMatrix;
  }

}
