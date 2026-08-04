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

public class CatapultFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_HEIGHT = "height";
  private static final String PARAM_RANGE = "range";
  private static final String[] paramNames = {PARAM_HEIGHT, PARAM_RANGE};

  private double height = 0.5;
  private double range = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ca_range = Math.max(fabs(this.range), 0.01);
    double ca_t = pAffineTP.x / ca_range;
    double ca_arc = this.height * (1.0 - ca_t * ca_t);

    pVarTP.x += pAmount * pAffineTP.x;
    pVarTP.y += pAmount * (pAffineTP.y + ca_arc);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{height, range}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_HEIGHT.equalsIgnoreCase(pName)) height = pValue;
    else if (PARAM_RANGE.equalsIgnoreCase(pName)) range = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "catapult"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float ca_range = fmaxf(fabsf(__catapult_range), 0.01f);\n"
         + "  float ca_t = __x / ca_range;\n"
         + "  float ca_arc = __catapult_height * (1.0f - ca_t * ca_t);\n"
         + "  __px += __catapult * __x;\n"
         + "  __py += __catapult * (__y + ca_arc);\n";
  }
}