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

public class RobinsonFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_SCALE};

  private double scale = 1.0;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double rbn_lat = clamp(pAffineTP.y * this.scale, -1.5707963267948966, 1.5707963267948966);
    double rbn_t = fabs(rbn_lat) / 1.5707963267948966;
    double rbn_xscale = 1.0 - 0.42 * rbn_t * rbn_t;
    double rbn_yscale = 1.0 + 0.12 * rbn_t;

    pVarTP.x += pAmount * pAffineTP.x * Math.max(rbn_xscale, 0.05) / this.scale;
    pVarTP.y += pAmount * rbn_lat * rbn_yscale / this.scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "robinson"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float rbn_lat = clamp(__y * __robinson_scale, -1.570796f, 1.570796f);\n"
         + "  float rbn_t = fabsf(rbn_lat) / 1.570796f;\n"
         + "  float rbn_xscale = 1.0f - 0.42f * rbn_t * rbn_t;\n"
         + "  float rbn_yscale = 1.0f + 0.12f * rbn_t;\n"
         + "  __px += __robinson * __x * fmaxf(rbn_xscale, 0.05f) / __robinson_scale;\n"
         + "  __py += __robinson * rbn_lat * rbn_yscale / __robinson_scale;\n";
  }
}