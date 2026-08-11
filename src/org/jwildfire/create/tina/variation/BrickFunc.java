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

public class BrickFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE_X = "scale_x";
  private static final String PARAM_SCALE_Y = "scale_y";
  private static final String[] paramNames = {PARAM_SCALE_X, PARAM_SCALE_Y};

  private double scale_x = 1.0;
  private double scale_y = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double br_sx = Math.max(fabs(this.scale_x), 1e-4);
    double br_sy = Math.max(fabs(this.scale_y), 1e-4);
    
    double row = Math.floor(pAffineTP.y / br_sy);
    double offset = ((row % 2.0 + 2.0) % 2.0 > 0.5) ? br_sx * 0.5 : 0.0;
    
    double nx = pAffineTP.x - (Math.floor((pAffineTP.x + offset) / br_sx) * br_sx + br_sx * 0.5 - offset);
    double ny = pAffineTP.y - (Math.floor(pAffineTP.y / br_sy) * br_sy + br_sy * 0.5);

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale_x, scale_y}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE_X.equalsIgnoreCase(pName)) scale_x = pValue;
    else if (PARAM_SCALE_Y.equalsIgnoreCase(pName)) scale_y = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "brick"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float br_sx = fmaxf(fabsf(__brick_scale_x), 1e-4f);\n"
         + "  float br_sy = fmaxf(fabsf(__brick_scale_y), 1e-4f);\n"
         + "  float row = floorf(__y / br_sy);\n"
         + "  float offset = (fmod(row, 2.0f) > 0.5f) ? br_sx * 0.5f : 0.0f;\n"
         + "  float nx = __x - (floorf((__x + offset) / br_sx) * br_sx + br_sx * 0.5f - offset);\n"
         + "  float ny = __y - (floorf(__y / br_sy) * br_sy + br_sy * 0.5f);\n"
         + "  __px += __brick * nx;\n"
         + "  __py += __brick * ny;\n";
  }
}