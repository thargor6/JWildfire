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

public class ZigzagFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PERIOD = "period";
  private static final String[] paramNames = {PARAM_PERIOD};

  private double period = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double zz_period = Math.max(fabs(this.period), 0.01);
    double zz_row = Math.floor(pAffineTP.y / zz_period);
    
    double row_mod = zz_row % 2.0;
    if (row_mod < 0.0) row_mod += 2.0;
    double zz_lx = (row_mod > 0.5) ? -pAffineTP.x : pAffineTP.x;

    pVarTP.x += pAmount * zz_lx;
    pVarTP.y += pAmount * pAffineTP.y;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{period}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PERIOD.equalsIgnoreCase(pName)) period = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "zigzag"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float zz_period = fmaxf(fabsf(__zigzag_period), 0.01f);\n"
         + "  float zz_row = floorf(__y / zz_period);\n"
         + "  float row_mod = fmod(zz_row, 2.0f);\n"
         + "  if (row_mod < 0.0f) row_mod += 2.0f;\n"
         + "  float zz_lx = (row_mod > 0.5f) ? -__x : __x;\n"
         + "  __px += __zigzag * zz_lx;\n"
         + "  __py += __zigzag * __y;\n";
  }
}