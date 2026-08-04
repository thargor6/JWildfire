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

public class BravaisFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_PULL = "pull";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_PULL};

  private double scale = 3.0;
  private double pull = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double bv_cx = Math.round(pAffineTP.x * this.scale) / this.scale;
    double bv_cy = Math.round(pAffineTP.y * this.scale) / this.scale;
    double bv_dx = bv_cx - pAffineTP.x;
    double bv_dy = bv_cy - pAffineTP.y;

    pVarTP.x += pAmount * (pAffineTP.x + this.pull * bv_dx);
    pVarTP.y += pAmount * (pAffineTP.y + this.pull * bv_dy);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, pull}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_PULL.equalsIgnoreCase(pName)) pull = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "bravais"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float bv_cx = roundf(__x * __bravais_scale) / __bravais_scale;\n"
         + "  float bv_cy = roundf(__y * __bravais_scale) / __bravais_scale;\n"
         + "  float bv_dx = bv_cx - __x;\n"
         + "  float bv_dy = bv_cy - __y;\n"
         + "  __px += __bravais * (__x + __bravais_pull * bv_dx);\n"
         + "  __py += __bravais * (__y + __bravais_pull * bv_dy);\n";
  }
}