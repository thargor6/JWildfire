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

import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public final class TransformationGradientColorStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;

  public TransformationGradientColorStep(XForm pXForm) {
    super(pXForm);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    
    if (pDstPoint.rgbColor) {  
      return;
    }
    
    RenderColor[] colorMap = xform.getOwner().getColorMap();
    double paletteIdxScl = colorMap.length - 2;
    int colorIdx = (int) (pDstPoint.color * paletteIdxScl + 0.5);
    if (colorIdx < 0)
      colorIdx = 0;
    else if (colorIdx >= RGBPalette.PALETTE_SIZE)
      colorIdx = RGBPalette.PALETTE_SIZE - 1;
    
    RenderColor color = colorMap[colorIdx];
    pDstPoint.redColor = color.red;
    pDstPoint.greenColor = color.green;
    pDstPoint.blueColor = color.blue;

  }

}
