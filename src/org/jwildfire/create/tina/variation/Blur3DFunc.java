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

import static org.jwildfire.base.MathLib.M_PI;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.sin;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_CUDA;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_JWILDFIRE;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class Blur3DFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  private double gauss_rnd[] = new double[4];
  private int gauss_N;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double angle = pContext.random() * 2 * M_PI;
    double sina = sin(angle);
    double cosa = cos(angle);
    double r = pAmount * (gauss_rnd[0] + gauss_rnd[1] + gauss_rnd[2] + gauss_rnd[3] - 2);
    gauss_rnd[gauss_N] = pContext.random();
    gauss_N = (gauss_N + 1) & 3;
    angle = pContext.random() * M_PI;
    double sinb = sin(angle);
    double cosb = cos(angle);
    pVarTP.x += r * sinb * cosa;
    pVarTP.y += r * sinb * sina;
    pVarTP.z += r * cosb;
  }

  @Override
  public String getName() {
    return "blur3D";
  }

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    gauss_rnd[0] = pContext.random();
    gauss_rnd[1] = pContext.random();
    gauss_rnd[2] = pContext.random();
    gauss_rnd[3] = pContext.random();
    gauss_N = 0;
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE | AVAILABILITY_CUDA;
  }

}
