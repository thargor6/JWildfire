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

import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.fabs;

public class DomainFoldFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FOLD_X = "fold_x";
  private static final String PARAM_FOLD_Y = "fold_y";
  private static final String[] paramNames = {PARAM_FOLD_X, PARAM_FOLD_Y};

  private double fold_x = 1.0;
  private double fold_y = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double df_fx = Math.max(fabs(this.fold_x), 1e-4);
    double df_fy = Math.max(fabs(this.fold_y), 1e-4);
    double nx = pAffineTP.x;
    double ny = pAffineTP.y;

    if (nx > df_fx) nx = 2.0 * df_fx - nx;
    else if (nx < -df_fx) nx = -2.0 * df_fx - nx;

    if (ny > df_fy) ny = 2.0 * df_fy - ny;
    else if (ny < -df_fy) ny = -2.0 * df_fy - ny;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{fold_x, fold_y}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FOLD_X.equalsIgnoreCase(pName)) fold_x = pValue;
    else if (PARAM_FOLD_Y.equalsIgnoreCase(pName)) fold_y = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "domain_fold"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float df_fx = fmaxf(fabsf(__domain_fold_fold_x), 1e-4f);\n"
         + "  float df_fy = fmaxf(fabsf(__domain_fold_fold_y), 1e-4f);\n"
         + "  float nx = __x;\n"
         + "  float ny = __y;\n"
         + "  if (nx > df_fx) nx = 2.0f * df_fx - nx;\n"
         + "  else if (nx < -df_fx) nx = -2.0f * df_fx - nx;\n"
         + "  if (ny > df_fy) ny = 2.0f * df_fy - ny;\n"
         + "  else if (ny < -df_fy) ny = -2.0f * df_fy - ny;\n"
         + "  __px += __domain_fold * nx;\n"
         + "  __py += __domain_fold * ny;\n";
  }
}