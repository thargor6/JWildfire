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
package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Stereo3dEye;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class DrawFocusPointFlameRendererView extends FlameRendererView {

  public DrawFocusPointFlameRendererView(Flame pFlame, AbstractRandomGenerator pRandGen, int pBorderWidth, int pImageWidth, int pImageHeight, int pRasterWidth, int pRasterHeight, FlameTransformationContext pFlameTransformationContext, RenderInfo renderInfo) {
    super(Stereo3dEye.UNSPECIFIED, pFlame, pRandGen, pBorderWidth, pImageWidth, pImageHeight, pRasterWidth, pRasterHeight, pFlameTransformationContext, renderInfo);
  }

  @Override
  public boolean project(XYZPoint pPoint, XYZProjectedPoint pProjectedPoint) {
    if (!super.project(pPoint, pProjectedPoint))
      return false;
    double xdist = (camPoint.x - flame.getFocusX());
    double ydist = (camPoint.y - flame.getFocusY());
    double zdist = (camPoint.z - flame.getFocusZ());

    double dist = sqrt(xdist * xdist + ydist * ydist + zdist * zdist);

    if (dist > 0.12 + flame.getCamDOFArea()) {
      // fade outer points 
      pProjectedPoint.intensity = 0.16;
    }
    else {
      pProjectedPoint.intensity = 1.0;
    }
    return true;
  }

}
