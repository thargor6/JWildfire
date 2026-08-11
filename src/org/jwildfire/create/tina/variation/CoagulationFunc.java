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

public class CoagulationFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_PULL = "pull";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_PULL};

  private double freq = 3.0;
  private double pull = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cg_cx = Math.round(pAffineTP.x * this.freq) / this.freq;
    double cg_cy = Math.round(pAffineTP.y * this.freq) / this.freq;
    double cg_dx = cg_cx - pAffineTP.x;
    double cg_dy = cg_cy - pAffineTP.y;

    pVarTP.x += pAmount * (pAffineTP.x + this.pull * cg_dx);
    pVarTP.y += pAmount * (pAffineTP.y + this.pull * cg_dy);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, pull}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_PULL.equalsIgnoreCase(pName)) pull = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "coagulation"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cg_cx = roundf(__x * __coagulation_freq) / __coagulation_freq;\n"
         + "  float cg_cy = roundf(__y * __coagulation_freq) / __coagulation_freq;\n"
         + "  float cg_dx = cg_cx - __x;\n"
         + "  float cg_dy = cg_cy - __y;\n"
         + "  __px += __coagulation * (__x + __coagulation_pull * cg_dx);\n"
         + "  __py += __coagulation * (__y + __coagulation_pull * cg_dy);\n";
  }
}