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

import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class ContourMapFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_BANDS = "bands";
  private static final String PARAM_PULL = "pull";
  private static final String[] paramNames = {PARAM_BANDS, PARAM_PULL};

  private double bands = 5.0;
  private double pull = 0.7;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double cm_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double cm_band_r = Math.round(cm_r * this.bands) / this.bands;
    double cm_nr = cm_r + this.pull * (cm_band_r - cm_r); // Manual mix/linear interpolation
    double cm_scale = cm_r > 0.001 ? cm_nr / cm_r : 1.0;

    pVarTP.x += pAmount * pAffineTP.x * cm_scale;
    pVarTP.y += pAmount * pAffineTP.y * cm_scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{bands, pull}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_BANDS.equalsIgnoreCase(pName)) bands = pValue;
    else if (PARAM_PULL.equalsIgnoreCase(pName)) pull = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "contour_map"; }
  
  @Override
  public void randomize() {
  	bands = Math.random() * 15.0;
  	pull = Math.random() * 2.0 - 1.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float cm_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float cm_band_r = roundf(cm_r * __contour_map_bands) / __contour_map_bands;\n"
         + "  float cm_nr = mix(cm_r, cm_band_r, __contour_map_pull);\n"
         + "  float cm_scale = cm_r > 0.001f ? cm_nr / cm_r : 1.0f;\n"
         + "  __px += __contour_map * __x * cm_scale;\n"
         + "  __py += __contour_map * __y * cm_scale;\n";
  }
}