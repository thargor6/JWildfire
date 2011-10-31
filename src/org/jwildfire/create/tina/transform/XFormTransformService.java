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
package org.jwildfire.create.tina.transform;

import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.XForm;

public class XFormTransformService {

  private static Matrix3x3 multiply(Matrix3x3 pMatrix1, Matrix3x3 pMatrix2) {
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
    if (Math.abs(pAngle) < Constants.EPSILON)
      return;
    double alpha = pAngle * Math.PI / 180.0;
    Matrix3x3 m1 = new Identity3x3();
    m1.val[0][0] = Math.cos(alpha);
    m1.val[0][1] = -Math.sin(alpha);
    m1.val[1][0] = Math.sin(alpha);
    m1.val[1][1] = Math.cos(alpha);
    Matrix3x3 m2 = new Identity3x3();
    m2.val[0][0] = pXForm.getCoeff00();
    m2.val[0][1] = pXForm.getCoeff01();
    m2.val[1][0] = pXForm.getCoeff10();
    m2.val[1][1] = pXForm.getCoeff11();
    m2.val[0][2] = pXForm.getCoeff20();
    m2.val[1][2] = pXForm.getCoeff21();
    m2 = multiply(m2, m1);
    pXForm.setCoeff00(m2.val[0][0]);
    pXForm.setCoeff01(m2.val[0][1]);
    pXForm.setCoeff10(m2.val[1][0]);
    pXForm.setCoeff11(m2.val[1][1]);
    pXForm.setCoeff20(m2.val[0][2]);
    pXForm.setCoeff21(m2.val[1][2]);
  }

  public static void translate(XForm pXForm, double pDeltaX, double pDeltaY) {
    if (Math.abs(pDeltaX) < Constants.EPSILON && Math.abs(pDeltaY) < Constants.EPSILON)
      return;
    Matrix3x3 m1 = new Identity3x3();
    m1.val[0][2] = pDeltaX;
    m1.val[1][2] = pDeltaY;
    Matrix3x3 m2 = new Identity3x3();
    m2.val[0][0] = pXForm.getCoeff00();
    m2.val[0][1] = pXForm.getCoeff01();
    m2.val[1][0] = pXForm.getCoeff10();
    m2.val[1][1] = pXForm.getCoeff11();
    m2.val[0][2] = pXForm.getCoeff20();
    m2.val[1][2] = pXForm.getCoeff21();
    m2 = multiply(m2, m1);
    pXForm.setCoeff00(m2.val[0][0]);
    pXForm.setCoeff01(m2.val[0][1]);
    pXForm.setCoeff10(m2.val[1][0]);
    pXForm.setCoeff11(m2.val[1][1]);
    pXForm.setCoeff20(m2.val[0][2]);
    pXForm.setCoeff21(m2.val[1][2]);
  }

  public static void scale(XForm pXForm, double pScale) {
    if (Math.abs(pScale - 1.0) < Constants.EPSILON)
      return;
    Matrix3x3 m1 = new Identity3x3();
    m1.val[0][0] = pScale;
    m1.val[1][1] = pScale;
    Matrix3x3 m2 = new Identity3x3();
    m2.val[0][0] = pXForm.getCoeff00();
    m2.val[0][1] = pXForm.getCoeff01();
    m2.val[1][0] = pXForm.getCoeff10();
    m2.val[1][1] = pXForm.getCoeff11();
    m2.val[0][2] = pXForm.getCoeff20();
    m2.val[1][2] = pXForm.getCoeff21();
    m2 = multiply(m2, m1);
    pXForm.setCoeff00(m2.val[0][0]);
    pXForm.setCoeff01(m2.val[0][1]);
    pXForm.setCoeff10(m2.val[1][0]);
    pXForm.setCoeff11(m2.val[1][1]);
    pXForm.setCoeff20(m2.val[0][2]);
    pXForm.setCoeff21(m2.val[1][2]);
  }

}
