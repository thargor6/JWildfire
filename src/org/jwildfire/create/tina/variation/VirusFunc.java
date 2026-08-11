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

public class VirusFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPREAD = "spread";
  private static final String PARAM_MUT = "mut";
  private static final String[] paramNames = {PARAM_SPREAD, PARAM_MUT};

  private double spread = 2.0;
  private double mut = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double vr_s = sin(pAffineTP.x * this.spread) * cos(pAffineTP.y * this.spread);
    
    // Fixed: Replaced unassigned sign variables with clean inline conditionals
    double sign_x = (pAffineTP.x == 0.0) ? 0.0 : ((pAffineTP.x < 0.0) ? -1.0 : 1.0);
    double sign_y = (pAffineTP.y == 0.0) ? 0.0 : ((pAffineTP.y < 0.0) ? -1.0 : 1.0);

    double vr_nx = pAffineTP.x + this.mut * vr_s * sign_x;
    double vr_ny = pAffineTP.y - this.mut * vr_s * sign_y;
    double vr_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double vr_nr = sqrt(vr_nx * vr_nx + vr_ny * vr_ny);
    double vr_scale = vr_r / (vr_nr + 1e-6);

    pVarTP.x += pAmount * vr_nx * vr_scale;
    pVarTP.y += pAmount * vr_ny * vr_scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{spread, mut}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPREAD.equalsIgnoreCase(pName)) spread = pValue;
    else if (PARAM_MUT.equalsIgnoreCase(pName)) mut = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "virus"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float vr_s = sinf(__x * __virus_spread) * cosf(__y * __virus_spread);\n"
         + "  float sign_x = (__x == 0.0f) ? 0.0f : (__x < 0.0f ? -1.0f : 1.0f);\n"
         + "  float sign_y = (__y == 0.0f) ? 0.0f : (__y < 0.0f ? -1.0f : 1.0f);\n"
         + "  float vr_nx = __x + __virus_mut * vr_s * sign_x;\n"
         + "  float vr_ny = __y - __virus_mut * vr_s * sign_y;\n"
         + "  float vr_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float vr_nr = sqrtf(vr_nx * vr_nx + vr_ny * vr_ny);\n"
         + "  float vr_scale = vr_r / (vr_nr + 1e-6f);\n"
         + "  __px += __virus * vr_nx * vr_scale;\n"
         + "  __py += __virus * vr_ny * vr_scale;\n";
  }
}