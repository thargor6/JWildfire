/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;

public class Rings3Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_VAL = "val";
  private static final String PARAM_N = "n";
  
  private static final String[] paramNames = {PARAM_VAL, PARAM_N};

  private double val = 0.01;
  private double n = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double l = pAffineTP.getPrecalcSqrt();
    if (_dx == 0 || l == 0) {
      return;
    }
    double k = (int) (l / _dx + 1) / 2;
    double r = pAmount * (2.0 - _dx * (k * 2 / l + 1) - n * (k * c - 1) / l);

    pVarTP.x += r * pAffineTP.x;
    pVarTP.y += r * pAffineTP.y;

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
    return new Object[]{val, n};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_VAL.equalsIgnoreCase(pName))
      val = pValue;
    else if (PARAM_N.equalsIgnoreCase(pName))
      n = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "rings3";
  }

  private double _dx;
  private double c;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _dx = val * val + EPSILON;
    c = 2.0 * (_dx - _dx * _dx);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }


  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float l = sqrtf(__x * __x + __y * __y);\n"
        + "float _dx = __rings2_val * __rings2_val ADD_EPSILON;\n"
        + "float c = 2.f * (_dx - _dx * _dx);\n"
        + " if (_dx == 0.f || l == 0.f) {\n"
        + "    return;\n"
        + " }\n"
        + "float k = (int) ((l / _dx + 1.f) / 2.f)"
        + " float r = __rings2 * (2.f - _dx * (k * 2.f / l + 1.f) - __rings2_n * (k * c - 1.f) / l);\n"
        + "\n"
        + "    __px += r * __x;\n"
        + "    __py += r * __y;\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "__pz += __rings2 * __z;\n" : "");
  }
  
}
