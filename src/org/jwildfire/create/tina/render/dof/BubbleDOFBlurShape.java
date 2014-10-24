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
package org.jwildfire.create.tina.render.dof;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.util.Collections;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XYZPoint;

public class BubbleDOFBlurShape extends AbstractDOFBlurShape {

  @Override
  public void applyDOFAndCamera(XYZPoint pSrc, XYZPoint pDest, double pZDist, double pZR) {
    double dr = doFade() * camDOF_10 * pZDist * scale;
    double a = 2.0 * M_PI * randGen.random();
    double dsina = sin(a);
    double dcosa = cos(a);
    pDest.x = (pSrc.x + dr * dcosa) / pZR;
    pDest.y = (pSrc.y + dr * dsina) / pZR;
  }

  @Override
  public List<String> getParamNames() {
    return Collections.emptyList();
  }

  @Override
  public void setDefaultParams(Flame pFlame) {
    pFlame.setCamDOFFade(1.0);
    pFlame.setCamDOFScale(1.0);
    pFlame.setCamDOFAngle(0.0);
  }

  @Override
  public boolean isUseColor() {
    return false;
  }

}
