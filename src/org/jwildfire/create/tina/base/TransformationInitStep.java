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

public final class TransformationInitStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;

  public TransformationInitStep(XForm pXForm) {
    super(pXForm);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    pAffineT.clear();
    pAffineT.doHide = pSrcPoint.doHide;
    pAffineT.color = pSrcPoint.color * xform.c1 + xform.c2;
    pAffineT.material = pSrcPoint.material * xform.material1 + xform.material2;
    pAffineT.modGamma = pSrcPoint.modGamma * xform.modGamma1 + xform.modGamma2;
    pAffineT.modContrast = pSrcPoint.modContrast * xform.modContrast1 + xform.modContrast2;
    pAffineT.modSaturation = pSrcPoint.modSaturation * xform.modSaturation1 + xform.modSaturation2;
  }
}
