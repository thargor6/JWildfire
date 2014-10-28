/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.base;

import org.jwildfire.create.tina.variation.FlameTransformationContext;

public final class TransformationAffineFullStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;

  public TransformationAffineFullStep(XForm pXForm) {
    super(pXForm);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    double x, y, z;

    if (xform.hasXYCoeffs) {
      x = xform.xyCoeff00 * pSrcPoint.x + xform.xyCoeff10 * pSrcPoint.y;
      y = xform.xyCoeff01 * pSrcPoint.x + xform.xyCoeff11 * pSrcPoint.y;
      z = pSrcPoint.z;
    }
    else {
      x = pSrcPoint.x;
      y = pSrcPoint.y;
      z = pSrcPoint.z;
    }

    if (xform.hasYZCoeffs) {
      double ny = xform.yzCoeff00 * y + xform.yzCoeff10 * z;
      double nz = xform.yzCoeff01 * y + xform.yzCoeff11 * z;
      y = ny;
      z = nz;
    }

    if (xform.hasZXCoeffs) {
      double nx = xform.zxCoeff00 * x + xform.zxCoeff10 * z;
      double nz = xform.zxCoeff01 * x + xform.zxCoeff11 * z;
      x = nx;
      z = nz;
    }

    pAffineT.x = x + xform.xyCoeff20 + xform.zxCoeff20;
    pAffineT.y = y + xform.xyCoeff21 + xform.yzCoeff20;
    pAffineT.z = z + xform.yzCoeff21 + xform.zxCoeff21;
  }
}
