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
import static org.jwildfire.base.mathlib.MathLib.exp;

public class InflateFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_STRENGTH = "strength";
  private static final String[] paramNames = {PARAM_STRENGTH};

  private double strength = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double il_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double il_nr = il_r + this.strength * exp(-il_r);
    double il_scale = il_r > 0.001 ? il_nr / il_r : 1.0;

    pVarTP.x += pAmount * pAffineTP.x * il_scale;
    pVarTP.y += pAmount * pAffineTP.y * il_scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{strength}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "inflate"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float il_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float il_nr = il_r + __inflate_strength * expf(-il_r);\n"
         + "  float il_scale = il_r > 0.001f ? il_nr / il_r : 1.0f;\n"
         + "  __px += __inflate * __x * il_scale;\n"
         + "  __py += __inflate * __y * il_scale;\n";
  }
}