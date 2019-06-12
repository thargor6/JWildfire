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

import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.weightmap.EmptyWeightMap;
import org.jwildfire.create.tina.base.weightmap.ImageMapWeightMap;
import org.jwildfire.create.tina.base.weightmap.PerlinNoiseWeightMap;
import org.jwildfire.create.tina.base.weightmap.WeightMap;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.NoiseTools;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.ColorToGrayTransformer;

public final class TransformationInitWeightMapStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;
  private final WeightMap weightMap;
  private final WeightMapInputType inputType;

  public TransformationInitWeightMapStep(XForm pXForm) {
    super(pXForm);
    weightMap = createWeightMap(pXForm);
    inputType = pXForm.getWeightMapInput();
  }

  private WeightMap createWeightMap(XForm pXForm) {
    switch(pXForm.getWeightMapType()) {
      case IMAGE_MAP:
        return new ImageMapWeightMap(pXForm.getWeightMapColorMapFilename(), pXForm.getWeightMapColorMapXCentre(), pXForm.getWeightMapColorMapYCentre(), pXForm.getWeightMapColorMapXSize(), pXForm.getWeightMapColorMapYSize());
      case PERLIN_NOISE:
        return new PerlinNoiseWeightMap(pXForm.getWeightMapPerlinNoiseAScale(), pXForm.getWeightMapPerlinNoiseFScale(), pXForm.getWeightMapPerlinNoiseOctaves());
      default:
        return new EmptyWeightMap();
    }
  }


  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    switch(inputType) {
      case AFFINE:
        pAffineT.weightMapValue = weightMap.getValue(pContext, pAffineT.x, pAffineT.y, pAffineT.z);
        break;
      case POSITION:
        pAffineT.weightMapValue = weightMap.getValue(pContext, pSrcPoint.x, pSrcPoint.y, pSrcPoint.z);
        break;
    }
  }
}
