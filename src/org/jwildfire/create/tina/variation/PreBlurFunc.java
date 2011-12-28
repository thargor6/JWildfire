/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class PreBlurFunc extends SimpleVariationFunc {
  private final double gauss_rnd[] = new double[4];
  private int gauss_N;

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = pContext.random() * 2 * Math.PI;
    double sina = pContext.sin(r);
    double cosa = pContext.cos(r);
    r = pAmount * (gauss_rnd[0] + gauss_rnd[1] + gauss_rnd[2] + gauss_rnd[3] - 2);
    gauss_rnd[gauss_N] = pContext.random();
    gauss_N = (gauss_N + 1) & 3;
    pAffineTP.x += r * cosa;
    pAffineTP.y += r * sina;
  }

  @Override
  public String getName() {
    return "pre_blur";
  }

  @Override
  public void init(XFormTransformationContext pContext, XForm pXForm) {
    gauss_rnd[0] = pContext.random();
    gauss_rnd[1] = pContext.random();
    gauss_rnd[2] = pContext.random();
    gauss_rnd[3] = pContext.random();
    gauss_N = 0;
  }

  @Override
  public int getPriority() {
    return -1;
  }

}
