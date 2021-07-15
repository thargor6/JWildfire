/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.*;

public class PreDiscFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;



  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    pAffineTP.x = pAmount * pAffineTP.getPrecalcAtan()/M_PI * sin(M_PI * pAffineTP.getPrecalcSqrt());
    pAffineTP.y = pAmount * pAffineTP.getPrecalcAtan()/M_PI * cos(M_PI * pAffineTP.getPrecalcSqrt());
  }

  @Override
  public String getName() {
    return "pre_disc";
  }

  @Override
  public int getPriority() {
    return -1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_PRE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "__x = varpar->pre_disc*(__phi/PI)*sinf(PI*__r);\n"
        + "__y = varpar->pre_disc*(__phi/PI)*cosf(PI*__r);\n"
        + "__r2 = __x*__x+__y*__y;\n"
        + "__r = sqrtf(__r2);\n"
        + "__rinv = 1.f/__r;\n"
        + "__phi = atan2f(__x,__y);\n"
        + "__theta = .5f*PI-__phi;\n"
        + "if (__theta > PI)\n"
        + "    __theta -= 2.f*PI;\n";
  }
}
