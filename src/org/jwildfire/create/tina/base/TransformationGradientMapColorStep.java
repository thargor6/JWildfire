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
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;


import org.jwildfire.create.tina.variation.FlameTransformationContext;

public final class TransformationGradientMapColorStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;

  public TransformationGradientMapColorStep(XForm pXForm) {
    super(pXForm);
  }

  private final Pixel toolPixel = new Pixel();

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    
    if (pDstPoint.rgbColor) {  
      return;
    }
    
    SimpleImage map = xform.getOwner().getGradientMap();

    double localColorAdd = xform.getOwner().getGradientMapLocalColorAdd();
    double localColorMultiply = xform.getOwner().getGradientMapLocalColorScale();

    double x = (pDstPoint.x * (1.0 - localColorMultiply) + pDstPoint.x * localColorMultiply * pDstPoint.color + localColorAdd * pDstPoint.color) * xform.getOwner().getGradientMapHorizScale() + xform.getOwner().getGradientMapHorizOffset();
    double y = (pDstPoint.y * (1.0 - localColorMultiply) + pDstPoint.y * localColorMultiply * pDstPoint.color + localColorAdd * pDstPoint.color) * xform.getOwner().getGradientMapVertScale() + xform.getOwner().getGradientMapVertOffset();

    double width = map.getImageWidth() - 2;
    double height = map.getImageHeight() - 2;
    double fx = MathLib.fabs(x);
    double imageX = MathLib.fmod(fx * width, width);
    if (((int) fx) % 2 != 0) {
      imageX = width - imageX;
    }
    double fy = MathLib.fabs(y);
    double imageY = MathLib.fmod(fy * height, height);
    if (((int) fy) % 2 != 0) {
      imageY = height - imageY;
    }

    toolPixel.setARGBValue(map.getARGBValueIgnoreBounds((int) imageX, (int) imageY));
    int luR = toolPixel.r;
    int luG = toolPixel.g;
    int luB = toolPixel.b;

    toolPixel.setARGBValue(map.getARGBValueIgnoreBounds(((int) imageX) + 1, (int) imageY));
    int ruR = toolPixel.r;
    int ruG = toolPixel.g;
    int ruB = toolPixel.b;
    toolPixel.setARGBValue(map.getARGBValueIgnoreBounds((int) imageX, ((int) imageY) + 1));
    int lbR = toolPixel.r;
    int lbG = toolPixel.g;
    int lbB = toolPixel.b;
    toolPixel.setARGBValue(map.getARGBValueIgnoreBounds(((int) imageX) + 1, ((int) imageY) + 1));
    int rbR = toolPixel.r;
    int rbG = toolPixel.g;
    int rbB = toolPixel.b;

    double localX = MathLib.frac(imageX);
    double localY = MathLib.frac(imageY);
    pDstPoint.redColor = GfxMathLib.blerp(luR, ruR, lbR, rbR, localX, localY);
    pDstPoint.greenColor = GfxMathLib.blerp(luG, ruG, lbG, rbG, localX, localY);
    pDstPoint.blueColor = GfxMathLib.blerp(luB, ruB, lbB, rbB, localX, localY);

  }

}
