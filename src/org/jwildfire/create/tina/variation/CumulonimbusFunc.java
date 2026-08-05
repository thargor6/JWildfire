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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class CumulonimbusFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_HEIGHT = "height";
  private static final String PARAM_SPREAD = "spread";
  private static final String PARAM_TURB = "turb";
  private static final String[] paramNames = {PARAM_HEIGHT, PARAM_SPREAD, PARAM_TURB};

  private double height = 1.0;
  private double spread = 0.5;
  private double turb = 0.15;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cb_h = Math.max(fabs(this.height), 0.1);
    double cb_t = clamp(pAffineTP.y / cb_h, -1.5, 1.5);
    double cb_anvil = this.spread * cb_t * fabs(cb_t);
    double cb_shake = this.turb * sin(pAffineTP.y * 4.1 + fabs(pAffineTP.x) * 2.3);

    pVarTP.x += pAmount * (pAffineTP.x + cb_anvil + cb_shake * (1.0 - fabs(cb_t) * 0.5));
    pVarTP.y += pAmount * (pAffineTP.y * (1.0 + 0.5 * fabs(cb_t)));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{height, spread, turb}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_HEIGHT.equalsIgnoreCase(pName)) height = pValue;
    else if (PARAM_SPREAD.equalsIgnoreCase(pName)) spread = pValue;
    else if (PARAM_TURB.equalsIgnoreCase(pName)) turb = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "cumulonimbus"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cb_h = fmaxf(fabsf(__cumulonimbus_height), 0.1f);\n"
         + "  float cb_t = clamp(__y / cb_h, -1.5f, 1.5f);\n"
         + "  float cb_anvil = __cumulonimbus_spread * cb_t * fabsf(cb_t);\n"
         + "  float cb_shake = __cumulonimbus_turb * sinf(__y * 4.1f + fabsf(__x) * 2.3f);\n"
         + "  __px += __cumulonimbus * (__x + cb_anvil + cb_shake * (1.0f - fabsf(cb_t) * 0.5f));\n"
         + "  __py += __cumulonimbus * (__y * (1.0f + 0.5f * fabsf(cb_t)));\n";
  }
}