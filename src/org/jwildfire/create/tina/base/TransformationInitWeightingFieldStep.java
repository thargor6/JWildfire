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

import org.jwildfire.create.tina.base.weightingfield.WeightingField;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldInputType;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

import java.util.Optional;

public final class TransformationInitWeightingFieldStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;
  private final WeightingField weightingField;
  private final WeightingFieldInputType inputType;

  public TransformationInitWeightingFieldStep(XForm pXForm) {
    super(pXForm);
    weightingField = Optional.ofNullable(pXForm.getWeightingFieldType()).orElse(WeightingFieldType.NONE).getInstance(pXForm);
    inputType = pXForm.getWeightingFieldInput();
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    switch(inputType) {
      case AFFINE:
        pAffineT.weightMapValue = weightingField.getValue(pAffineT.x, pAffineT.y, pAffineT.z);
        break;
      case POSITION:
        pAffineT.weightMapValue = weightingField.getValue(pSrcPoint.x, pSrcPoint.y, pSrcPoint.z);
        break;
    }
  }
}
