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

public class AbsFoldFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FOLD = "fold";
  private static final String[] paramNames = {PARAM_FOLD};

  private double fold = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double nx = pAffineTP.x;
    double ny = pAffineTP.y;

    if (nx > fold) {
      nx = 2.0 * fold - nx;
    } else if (nx < -fold) {
      nx = -2.0 * fold - nx;
    }

    if (ny > fold) {
      ny = 2.0 * fold - ny;
    } else if (ny < -fold) {
      ny = -2.0 * fold - ny;
    }

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
}

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{fold};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FOLD.equalsIgnoreCase(pName)) {
      fold = pValue;
    } else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "abs_fold";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    // Defined as a 2D variation that supports GPU and the Swan renderer
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // Uses the required '__abs_fold_' prefix for parameters and '__abs_fold' for the amount in JWildfire GPU compiling
    return "  float nx = __x;\n"
         + "  float ny = __y;\n"
         + "  if (nx > __abs_fold_fold) nx = 2.0f * __abs_fold_fold - nx;\n"
         + "  else if (nx < -__abs_fold_fold) nx = -2.0f * __abs_fold_fold - nx;\n"
         + "  if (ny > __abs_fold_fold) ny = 2.0f * __abs_fold_fold - ny;\n"
         + "  else if (ny < -__abs_fold_fold) ny = -2.0f * __abs_fold_fold - ny;\n"
         + "  __px += __abs_fold * nx;\n"
         + "  __py += __abs_fold * ny;\n";
  }
}