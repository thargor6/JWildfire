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

public class CantorFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String[] paramNames = {PARAM_FREQ};

  private double freq = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double fx = (pAffineTP.x * this.freq) % 3.0;
    if (fx < 0) fx += 3.0;
    double fy = (pAffineTP.y * this.freq) % 3.0;
    if (fy < 0) fy += 3.0;

    if (fx > 1.0 && fx < 2.0) fx = (fx < 1.5) ? 1.0 : 2.0;
    if (fy > 1.0 && fy < 2.0) fy = (fy < 1.5) ? 1.0 : 2.0;

    pVarTP.x += pAmount * fx / this.freq;
    pVarTP.y += pAmount * fy / this.freq;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "cantor"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float fx = fmod(__x * __cantor_freq, 3.0f);\n"
         + "  if (fx < 0.0f) fx += 3.0f;\n"
         + "  float fy = fmod(__y * __cantor_freq, 3.0f);\n"
         + "  if (fy < 0.0f) fy += 3.0f;\n"
         + "  if (fx > 1.0f && fx < 2.0f) fx = (fx < 1.5f) ? 1.0f : 2.0f;\n"
         + "  if (fy > 1.0f && fy < 2.0f) fy = (fy < 1.5f) ? 1.0f : 2.0f;\n"
         + "  __px += __cantor * fx / __cantor_freq;\n"
         + "  __py += __cantor * fy / __cantor_freq;\n";
  }
}