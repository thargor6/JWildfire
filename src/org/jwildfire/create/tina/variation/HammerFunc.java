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
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class HammerFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_SCALE};

  private double scale = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double hm_lat = pAffineTP.y * this.scale;
    double hm_lon = pAffineTP.x * this.scale;
    double hm_cos_lat = cos(hm_lat);
    double hm_d = sqrt(1.0 + hm_cos_lat * cos(hm_lon * 0.5)) + 0.001;

    pVarTP.x += pAmount * 2.82842712474619 * hm_cos_lat * sin(hm_lon * 0.5) / hm_d;
    pVarTP.y += pAmount * 1.414213562373095 * sin(hm_lat) / hm_d;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "hammer"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float hm_lat = __y * __hammer_scale;\n"
         + "  float hm_lon = __x * __hammer_scale;\n"
         + "  float hm_cos_lat = cosf(hm_lat);\n"
         + "  float hm_d = sqrtf(1.0f + hm_cos_lat * cosf(hm_lon * 0.5f)) + 0.001f;\n"
         + "  __px += __hammer * 2.82842712f * hm_cos_lat * sinf(hm_lon * 0.5f) / hm_d;\n"
         + "  __py += __hammer * 1.41421356f * sinf(hm_lat) / hm_d;\n";
  }
}