/*

  JWildfire - an image and animation processor written in Java

  Copyright (C) 1995-2026 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class ScrewFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PITCH = "pitch";
  private static final String[] paramNames = {PARAM_PITCH};

  private double pitch = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sc_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double sc_theta = atan2(pAffineTP.y, pAffineTP.x);
    double sc_nt = sc_theta + sc_r * this.pitch;

    pVarTP.x += pAmount * sc_r * cos(sc_nt);
    pVarTP.y += pAmount * sc_r * sin(sc_nt);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{pitch}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PITCH.equalsIgnoreCase(pName)) pitch = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "screw"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float sc_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float sc_theta = atan2f(__y, __x);\n"
         + "  float sc_nt = sc_theta + sc_r * __screw_pitch;\n"
         + "  __px += __screw * sc_r * cosf(sc_nt);\n"
         + "  __py += __screw * sc_r * sinf(sc_nt);\n";
  }
}