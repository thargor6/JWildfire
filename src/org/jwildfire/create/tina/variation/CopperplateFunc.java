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
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;

public class CopperplateFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_TAPER = "taper";
  private static final String[] paramNames = {PARAM_ANGLE, PARAM_FREQ, PARAM_WIDTH, PARAM_TAPER};

  private double angle = 0.0;
  private double freq = 5.0;
  private double width = 0.3;
  private double taper = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cp_ca = cos(this.angle);
    double cp_sa = sin(this.angle);
    double cp_u = pAffineTP.x * cp_ca + pAffineTP.y * cp_sa;
    double cp_v = -pAffineTP.x * cp_sa + pAffineTP.y * cp_ca;
    double cp_env = exp(-fabs(cp_u) * this.taper);
    double cp_nv = cp_v * (1.0 + this.width * cp_env * sin(this.freq * cp_u));

    pVarTP.x += pAmount * (cp_u * cp_ca - cp_nv * cp_sa);
    pVarTP.y += pAmount * (cp_u * cp_sa + cp_nv * cp_ca);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{angle, freq, width, taper}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANGLE.equalsIgnoreCase(pName)) angle = pValue;
    else if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else if (PARAM_TAPER.equalsIgnoreCase(pName)) taper = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "copperplate"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cp_ca = cosf(__copperplate_angle);\n"
         + "  float cp_sa = sinf(__copperplate_angle);\n"
         + "  float cp_u = __x * cp_ca + __y * cp_sa;\n"
         + "  float cp_v = -__x * cp_sa + __y * cp_ca;\n"
         + "  float cp_env = expf(-fabsf(cp_u) * __copperplate_taper);\n"
         + "  float cp_nv = cp_v * (1.0f + __copperplate_width * cp_env * sinf(__copperplate_freq * cp_u));\n"
         + "  __px += __copperplate * (cp_u * cp_ca - cp_nv * cp_sa);\n"
         + "  __py += __copperplate * (cp_u * cp_sa + cp_nv * cp_ca);\n";
  }
}