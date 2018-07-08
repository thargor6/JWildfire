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

public final class TransformationPrepareVariationsStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;

  public TransformationPrepareVariationsStep(XForm pXForm) {
    super(pXForm);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    pVarT.color = pAffineT.color;
    pVarT.material = pAffineT.material;
    pVarT.receiveOnlyShadows = pAffineT.receiveOnlyShadows;
    pVarT.modGamma = pAffineT.modGamma;
    pVarT.modContrast = pAffineT.modContrast;
    pVarT.modSaturation = pAffineT.modSaturation;
    pVarT.modHue = pAffineT.modHue;
    pVarT.rgbColor = pAffineT.rgbColor;
    pVarT.redColor = pAffineT.redColor;
    pVarT.greenColor = pAffineT.greenColor;
    pVarT.blueColor = pAffineT.blueColor;
    pVarT.doHide = pAffineT.doHide;
  }

}
