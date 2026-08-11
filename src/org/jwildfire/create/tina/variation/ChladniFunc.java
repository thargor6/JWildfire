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

public class ChladniFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_M = "m";
  private static final String PARAM_N = "n";
  private static final String PARAM_AMPLITUDE = "amplitude";
  private static final String[] paramNames = {PARAM_M, PARAM_N, PARAM_AMPLITUDE};

  private double m = 2.0;
  private double n = 3.0;
  private double amplitude = 0.25;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dx = this.amplitude * cos(this.m * Math.PI * pAffineTP.x) * sin(this.n * Math.PI * pAffineTP.y);
    double dy = this.amplitude * sin(this.m * Math.PI * pAffineTP.x) * cos(this.n * Math.PI * pAffineTP.y);

    pVarTP.x += pAmount * (pAffineTP.x + dx);
    pVarTP.y += pAmount * (pAffineTP.y + dy);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{m, n, amplitude}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_M.equalsIgnoreCase(pName)) m = pValue;
    else if (PARAM_N.equalsIgnoreCase(pName)) n = pValue;
    else if (PARAM_AMPLITUDE.equalsIgnoreCase(pName)) amplitude = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "chladni"; }
  
  @Override
  public void randomize() {
  	m = Math.random() * 10.0 - 5.0;
  	n = Math.random() * 10.0 - 5.0;
  	if (Math.random() < 0.75) {
  		amplitude = Math.random() * 0.5;
  	} else {
  		amplitude = Math.random();
  	}
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float dx = __chladni_amplitude * cosf(__chladni_m * M_PI * __x) * sinf(__chladni_n * M_PI * __y);\n"
         + "  float dy = __chladni_amplitude * sinf(__chladni_m * M_PI * __x) * cosf(__chladni_n * M_PI * __y);\n"
         + "  __px += __chladni * (__x + dx);\n"
         + "  __py += __chladni * (__y + dy);\n";
  }
}