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

public final class TransformationPostAffineStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;

  public TransformationPostAffineStep(XForm pXForm) {
    super(pXForm);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    double px, py, pz;
    if (xform.hasXYPostCoeffs) {
      px = xform.xyPostCoeff00 * pVarT.x + xform.xyPostCoeff10 * pVarT.y;
      py = xform.xyPostCoeff01 * pVarT.x + xform.xyPostCoeff11 * pVarT.y;
      pz = pVarT.z;
    }
    else {
      px = pVarT.x;
      py = pVarT.y;
      pz = pVarT.z;
    }

    if (xform.hasYZPostCoeffs) {
      double ny = xform.yzPostCoeff00 * py + xform.yzPostCoeff10 * pz;
      double nz = xform.yzPostCoeff01 * py + xform.yzPostCoeff11 * pz;
      py = ny;
      pz = nz;
    }

    if (xform.hasZXPostCoeffs) {
      double nx = xform.zxPostCoeff00 * px + xform.zxPostCoeff10 * pz;
      double nz = xform.zxPostCoeff01 * px + xform.zxPostCoeff11 * pz;
      px = nx;
      pz = nz;
    }

    pDstPoint.x = px + xform.xyPostCoeff20 + xform.zxPostCoeff20;
    pDstPoint.y = py + xform.xyPostCoeff21 + xform.yzPostCoeff20;
    pDstPoint.z = pz + xform.yzPostCoeff21 + xform.zxPostCoeff21;
  }
}
