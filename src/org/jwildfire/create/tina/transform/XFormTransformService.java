/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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
package org.jwildfire.create.tina.transform;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;

public class XFormTransformService {

  public static Matrix3x3 multiply(Matrix3x3 pMatrix1, Matrix3x3 pMatrix2) {
    Matrix3x3 res = new Matrix3x3();
    res.val[0][0] = pMatrix1.val[0][0] * pMatrix2.val[0][0] + pMatrix1.val[0][1] * pMatrix2.val[1][0] + pMatrix1.val[0][2] * pMatrix2.val[2][0];
    res.val[0][1] = pMatrix1.val[0][0] * pMatrix2.val[0][1] + pMatrix1.val[0][1] * pMatrix2.val[1][1] + pMatrix1.val[0][2] * pMatrix2.val[2][1];
    res.val[0][2] = pMatrix1.val[0][0] * pMatrix2.val[0][2] + pMatrix1.val[0][1] * pMatrix2.val[1][2] + pMatrix1.val[0][2] * pMatrix2.val[2][2];
    res.val[1][0] = pMatrix1.val[1][0] * pMatrix2.val[0][0] + pMatrix1.val[1][1] * pMatrix2.val[1][0] + pMatrix1.val[1][2] * pMatrix2.val[2][0];
    res.val[1][1] = pMatrix1.val[1][0] * pMatrix2.val[0][1] + pMatrix1.val[1][1] * pMatrix2.val[1][1] + pMatrix1.val[1][2] * pMatrix2.val[2][1];
    res.val[1][2] = pMatrix1.val[1][0] * pMatrix2.val[0][2] + pMatrix1.val[1][1] * pMatrix2.val[1][2] + pMatrix1.val[1][2] * pMatrix2.val[2][2];
    res.val[2][0] = pMatrix1.val[2][0] * pMatrix2.val[0][0] + pMatrix1.val[2][1] * pMatrix2.val[1][0] + pMatrix1.val[2][2] * pMatrix2.val[2][0];
    res.val[2][0] = pMatrix1.val[2][0] * pMatrix2.val[0][1] + pMatrix1.val[2][1] * pMatrix2.val[1][1] + pMatrix1.val[2][2] * pMatrix2.val[2][1];
    res.val[2][0] = pMatrix1.val[2][0] * pMatrix2.val[0][2] + pMatrix1.val[2][1] * pMatrix2.val[1][2] + pMatrix1.val[2][2] * pMatrix2.val[2][2];
    return res;
  }

  public static void rotate(XForm pXForm, double pAngle) {
    rotate(pXForm, pAngle, false);
  }

  public static void rotate(XForm pXForm, double pAngle, boolean pPostTransform) {
    if (Math.abs(pAngle) < MathLib.SMALL_EPSILON)
      return;
    double alpha = pAngle * Math.PI / 180.0;
    Matrix3x3 m1 = new Identity3x3();
    m1.val[0][0] = Math.cos(alpha);
    m1.val[0][1] = -Math.sin(alpha);
    m1.val[1][0] = Math.sin(alpha);
    m1.val[1][1] = Math.cos(alpha);
    Matrix3x3 m2 = new Identity3x3();
    if (pPostTransform) {
      m2.val[0][0] = pXForm.getPostCoeff00();
      m2.val[0][1] = pXForm.getPostCoeff01();
      m2.val[1][0] = pXForm.getPostCoeff10();
      m2.val[1][1] = pXForm.getPostCoeff11();
      m2.val[0][2] = pXForm.getPostCoeff20();
      m2.val[1][2] = pXForm.getPostCoeff21();
    }
    else {
      m2.val[0][0] = pXForm.getCoeff00();
      m2.val[0][1] = pXForm.getCoeff01();
      m2.val[1][0] = pXForm.getCoeff10();
      m2.val[1][1] = pXForm.getCoeff11();
      m2.val[0][2] = pXForm.getCoeff20();
      m2.val[1][2] = pXForm.getCoeff21();
    }
    m2 = multiply(m2, m1);
    if (pPostTransform) {
      pXForm.setPostCoeff00(m2.val[0][0]);
      pXForm.setPostCoeff01(m2.val[0][1]);
      pXForm.setPostCoeff10(m2.val[1][0]);
      pXForm.setPostCoeff11(m2.val[1][1]);
      pXForm.setPostCoeff20(m2.val[0][2]);
      pXForm.setPostCoeff21(m2.val[1][2]);
    }
    else {
      pXForm.setCoeff00(m2.val[0][0]);
      pXForm.setCoeff01(m2.val[0][1]);
      pXForm.setCoeff10(m2.val[1][0]);
      pXForm.setCoeff11(m2.val[1][1]);
      pXForm.setCoeff20(m2.val[0][2]);
      pXForm.setCoeff21(m2.val[1][2]);
    }
  }

  public static void globalTranslate(XForm pXForm, double pDeltaX, double pDeltaY) {
    globalTranslate(pXForm, pDeltaX, pDeltaY, false);
  }

  public static void localTranslate(XForm pXForm, double pDeltaX, double pDeltaY) {
    localTranslate(pXForm, pDeltaX, pDeltaY, false);
  }

  public static void globalTranslate(XForm pXForm, double pDeltaX, double pDeltaY, boolean pPostTransform) {
    if (Math.abs(pDeltaX) < MathLib.SMALL_EPSILON && Math.abs(pDeltaY) < MathLib.SMALL_EPSILON)
      return;
    if (pPostTransform) {
      pXForm.setPostCoeff20(pDeltaX + pXForm.getPostCoeff20());
      pXForm.setPostCoeff21(pDeltaY + pXForm.getPostCoeff21());
    }
    else {
      pXForm.setCoeff20(pDeltaX + pXForm.getCoeff20());
      pXForm.setCoeff21(pDeltaY + pXForm.getCoeff21());
    }
  }

  public static void localTranslate(XForm pXForm, double pDeltaX, double pDeltaY, boolean pPostTransform) {
    if (Math.abs(pDeltaX) < MathLib.SMALL_EPSILON && Math.abs(pDeltaY) < MathLib.SMALL_EPSILON)
      return;
    Matrix3x3 m1 = new Identity3x3();
    m1.val[0][2] = pDeltaX;
    m1.val[1][2] = pDeltaY;
    Matrix3x3 m2 = new Identity3x3();
    if (pPostTransform) {
      m2.val[0][0] = pXForm.getPostCoeff00();
      m2.val[0][1] = pXForm.getPostCoeff01();
      m2.val[1][0] = pXForm.getPostCoeff10();
      m2.val[1][1] = pXForm.getPostCoeff11();
      m2.val[0][2] = pXForm.getPostCoeff20();
      m2.val[1][2] = pXForm.getPostCoeff21();
    }
    else {
      m2.val[0][0] = pXForm.getCoeff00();
      m2.val[0][1] = pXForm.getCoeff01();
      m2.val[1][0] = pXForm.getCoeff10();
      m2.val[1][1] = pXForm.getCoeff11();
      m2.val[0][2] = pXForm.getCoeff20();
      m2.val[1][2] = pXForm.getCoeff21();
    }
    m2 = multiply(m2, m1);
    if (pPostTransform) {
      pXForm.setPostCoeff00(m2.val[0][0]);
      pXForm.setPostCoeff01(m2.val[0][1]);
      pXForm.setPostCoeff10(m2.val[1][0]);
      pXForm.setPostCoeff11(m2.val[1][1]);
      pXForm.setPostCoeff20(m2.val[0][2]);
      pXForm.setPostCoeff21(m2.val[1][2]);
    }
    else {
      pXForm.setCoeff00(m2.val[0][0]);
      pXForm.setCoeff01(m2.val[0][1]);
      pXForm.setCoeff10(m2.val[1][0]);
      pXForm.setCoeff11(m2.val[1][1]);
      pXForm.setCoeff20(m2.val[0][2]);
      pXForm.setCoeff21(m2.val[1][2]);
    }
  }

  public static void scale(XForm pXForm, double pScale, boolean pXScale, boolean pYScale) {
    scale(pXForm, pScale, pXScale, pYScale, false);
  }

  public static void scale(XForm pXForm, double pScale, boolean pXScale, boolean pYScale, boolean pPostTransform) {
    if (Math.abs(pScale - 1.0) < MathLib.SMALL_EPSILON) {
      return;
    }
    if (pXScale) {
      if (pPostTransform) {
        pXForm.setPostCoeff00(pXForm.getPostCoeff00() * pScale);
        pXForm.setPostCoeff01(pXForm.getPostCoeff01() * pScale);
      }
      else {
        pXForm.setCoeff00(pXForm.getCoeff00() * pScale);
        pXForm.setCoeff01(pXForm.getCoeff01() * pScale);
      }
    }
    if (pYScale) {
      if (pPostTransform) {
        pXForm.setPostCoeff10(pXForm.getPostCoeff10() * pScale);
        pXForm.setPostCoeff11(pXForm.getPostCoeff11() * pScale);
      }
      else {
        pXForm.setCoeff10(pXForm.getCoeff10() * pScale);
        pXForm.setCoeff11(pXForm.getCoeff11() * pScale);
      }
    }
    //    Matrix3x3 m1 = new Identity3x3();
    //    m1.val[0][0] = pScale;
    //    m1.val[1][1] = pScale;
    //    Matrix3x3 m2 = new Identity3x3();
    //    if (pPostTransform) {
    //      m2.val[0][0] = pXForm.getPostCoeff00();
    //      m2.val[0][1] = pXForm.getPostCoeff01();
    //      m2.val[1][0] = pXForm.getPostCoeff10();
    //      m2.val[1][1] = pXForm.getPostCoeff11();
    //      m2.val[0][2] = pXForm.getPostCoeff20();
    //      m2.val[1][2] = pXForm.getPostCoeff21();
    //    }
    //    else {
    //      m2.val[0][0] = pXForm.getCoeff00();
    //      m2.val[0][1] = pXForm.getCoeff01();
    //      m2.val[1][0] = pXForm.getCoeff10();
    //      m2.val[1][1] = pXForm.getCoeff11();
    //      m2.val[0][2] = pXForm.getCoeff20();
    //      m2.val[1][2] = pXForm.getCoeff21();
    //    }
    //    m2 = multiply(m2, m1);
    //    if (pPostTransform) {
    //      pXForm.setPostCoeff00(m2.val[0][0]);
    //      pXForm.setPostCoeff01(m2.val[0][1]);
    //      pXForm.setPostCoeff10(m2.val[1][0]);
    //      pXForm.setPostCoeff11(m2.val[1][1]);
    //      pXForm.setPostCoeff20(m2.val[0][2]);
    //      pXForm.setPostCoeff21(m2.val[1][2]);
    //    }
    //    else {
    //      pXForm.setCoeff00(m2.val[0][0]);
    //      pXForm.setCoeff01(m2.val[0][1]);
    //      pXForm.setCoeff10(m2.val[1][0]);
    //      pXForm.setCoeff11(m2.val[1][1]);
    //      pXForm.setCoeff20(m2.val[0][2]);
    //      pXForm.setCoeff21(m2.val[1][2]);
    //    }
  }

  public static void reset(XForm pXForm, boolean pPostTransform) {
    if (pPostTransform) {
      pXForm.setPostCoeff00(1.0);
      pXForm.setPostCoeff01(0.0);
      pXForm.setPostCoeff10(0.0);
      pXForm.setPostCoeff11(1.0);
      pXForm.setPostCoeff20(0.0);
      pXForm.setPostCoeff21(0.0);
    }
    else {
      pXForm.setCoeff00(1.0);
      pXForm.setCoeff01(0.0);
      pXForm.setCoeff10(0.0);
      pXForm.setCoeff11(1.0);
      pXForm.setCoeff20(0.0);
      pXForm.setCoeff21(0.0);
    }
  }

  public static void flipVertical(XForm pXForm, boolean pPostTransform) {
    if (pPostTransform) {
      pXForm.setPostCoeff01(-pXForm.getPostCoeff01());
      pXForm.setPostCoeff11(-pXForm.getPostCoeff11());
    }
    else {
      pXForm.setCoeff01(-pXForm.getCoeff01());
      pXForm.setCoeff11(-pXForm.getCoeff11());
    }
  }

  public static void flipHorizontal(XForm pXForm, boolean pPostTransform) {
    if (pPostTransform) {
      pXForm.setPostCoeff00(-pXForm.getPostCoeff00());
      pXForm.setPostCoeff10(-pXForm.getPostCoeff10());
    }
    else {
      pXForm.setCoeff00(-pXForm.getCoeff00());
      pXForm.setCoeff10(-pXForm.getCoeff10());
    }
  }

}
