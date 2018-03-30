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
package org.jwildfire.create.tina.base;

import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;

public final class PostInvVariationTransformationStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;

  private final Variation variation;

  public PostInvVariationTransformationStep(XForm pXForm, Variation pVariation) {
    super(pXForm);
    variation = pVariation;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    variation.invtransform(pContext, xform, pAffineT, pVarT);
  }

}
