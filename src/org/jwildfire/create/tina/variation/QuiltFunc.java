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

public class QuiltFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_PUFF = "puff";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_PUFF};

  private double scale = 3.0;
  private double puff = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double qt_cx = pAffineTP.x * this.scale;
    double qt_cy = pAffineTP.y * this.scale;
    double qt_col = Math.round(qt_cx);
    double qt_row = Math.round(qt_cy);
    double qt_lx = qt_cx - qt_col;
    double qt_ly = qt_cy - qt_row;
    double qt_dist = sqrt(qt_lx * qt_lx + qt_ly * qt_ly);
    double qt_bulge = this.puff * (1.0 - qt_dist * 2.0);

    pVarTP.x += pAmount * (qt_col + qt_lx * (1.0 - qt_bulge)) / this.scale;
    pVarTP.y += pAmount * (qt_row + qt_ly * (1.0 - qt_bulge)) / this.scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, puff}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_PUFF.equalsIgnoreCase(pName)) puff = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "quilt"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float qt_cx = __x * __quilt_scale;\n"
         + "  float qt_cy = __y * __quilt_scale;\n"
         + "  float qt_col = roundf(qt_cx);\n"
         + "  float qt_row = roundf(qt_cy);\n"
         + "  float qt_lx = qt_cx - qt_col;\n"
         + "  float qt_ly = qt_cy - qt_row;\n"
         + "  float qt_dist = sqrtf(qt_lx * qt_lx + qt_ly * qt_ly);\n"
         + "  float qt_bulge = __quilt_puff * (1.0f - qt_dist * 2.0f);\n"
         + "  __px += __quilt * (qt_col + qt_lx * (1.0f - qt_bulge)) / __quilt_scale;\n"
         + "  __py += __quilt * (qt_row + qt_ly * (1.0f - qt_bulge)) / __quilt_scale;\n";
  }
}