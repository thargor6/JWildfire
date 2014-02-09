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
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;

public class DrawFocusPointFlameRendererView extends FlameRendererView {

  public DrawFocusPointFlameRendererView(Flame pFlame, AbstractRandomGenerator pRandGen, int pBorderWidth, int pMaxBorderWidth, int pImageWidth, int pImageHeight, int pRasterWidth, int pRasterHeight) {
    super(pFlame, pRandGen, pBorderWidth, pMaxBorderWidth, pImageWidth, pImageHeight, pRasterWidth, pRasterHeight);
  }

  @Override
  public boolean project(XYZPoint pPoint, XYZProjectedPoint pProjectedPoint) {
    double z = pPoint.z;
    double px = cameraMatrix[0][0] * pPoint.x + cameraMatrix[1][0] * pPoint.y /*+ cameraMatrix[2][0] * z*/;
    double py = cameraMatrix[0][1] * pPoint.x + cameraMatrix[1][1] * pPoint.y + cameraMatrix[2][1] * z;
    double pz = cameraMatrix[0][2] * pPoint.x + cameraMatrix[1][2] * pPoint.y + cameraMatrix[2][2] * z;
    double zr = 1.0 - flame.getCamPerspective() * pz;

    double xdist = (px - flame.getFocusX());
    double ydist = (py - flame.getFocusY());
    double zdist = (pz - flame.getFocusZ());
    double dist = sqrt(xdist * xdist + ydist * ydist + zdist * zdist);

    if (dist > 0.12 + flame.getCamDOFArea()) {
      // dont draw outer points
      pPoint.x = 10000 + 10000 * Math.random();
      pPoint.y = 10000 + 10000 * Math.random();
    }
    else {
      pPoint.x = px / zr;
      pPoint.y = py / zr;
    }
    pProjectedPoint.intensity = 1.0;
    pProjectedPoint.x = pPoint.x * cosa + pPoint.y * sina + rcX;
    if ((pProjectedPoint.x < 0) || (pProjectedPoint.x > camW))
      return false;
    pProjectedPoint.y = pPoint.y * cosa - pPoint.x * sina + rcY;
    if ((pProjectedPoint.y < 0) || (pProjectedPoint.y > camH))
      return false;
    return true;
  }

}
