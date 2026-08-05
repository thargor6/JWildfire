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

import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.cos;

public class CurlNoiseFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_STRENGTH = "strength";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_STRENGTH};

  private double freq = 2.0;
  private double strength = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dx = this.strength * this.freq * sin(this.freq * pAffineTP.x) * cos(this.freq * pAffineTP.y);
    double dy = -this.strength * this.freq * cos(this.freq * pAffineTP.x) * sin(this.freq * pAffineTP.y);

    pVarTP.x += pAmount * (pAffineTP.x + dx);
    pVarTP.y += pAmount * (pAffineTP.y + dy);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, strength}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "curl_noise"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float dx = __curl_noise_strength * __curl_noise_freq * sinf(__curl_noise_freq * __x) * cosf(__curl_noise_freq * __y);\n"
         + "  float dy = -__curl_noise_strength * __curl_noise_freq * cosf(__curl_noise_freq * __x) * sinf(__curl_noise_freq * __y);\n"
         + "  __px += __curl_noise * (__x + dx);\n"
         + "  __py += __curl_noise * (__y + dy);\n";
  }
}