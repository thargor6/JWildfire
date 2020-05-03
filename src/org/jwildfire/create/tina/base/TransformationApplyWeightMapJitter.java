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

import java.util.Optional;

import org.jwildfire.create.tina.base.weightingfield.WeightingField;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public final class TransformationApplyWeightMapJitter extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;
  private final WeightingField weightingField;
  private final double jitterIntensity;

  public TransformationApplyWeightMapJitter(XForm pXForm) {
    super(pXForm);
    weightingField = Optional.ofNullable(pXForm.getWeightingFieldType()).orElse(WeightingFieldType.NONE).getInstance(pXForm);
    jitterIntensity = pXForm.getWeightingFieldJitterIntensity() * 0.1;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    pDstPoint.x += (weightingField.getValue(pDstPoint.x, pDstPoint.y, pDstPoint.z) * jitterIntensity);
    pDstPoint.y += (weightingField.getValue(pDstPoint.y, pDstPoint.x, pDstPoint.z) * jitterIntensity);
    pDstPoint.z += (weightingField.getValue(pDstPoint.z, pDstPoint.x, pDstPoint.y) * jitterIntensity);
  }
}
