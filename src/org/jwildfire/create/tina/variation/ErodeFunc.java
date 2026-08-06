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

public class ErodeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_STRENGTH = "strength";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_ROUGH = "rough";
  private static final String[] paramNames = {PARAM_STRENGTH, PARAM_SCALE, PARAM_ROUGH};

  private double strength = 0.3;
  private double scale = 2.0;
  private double rough = 0.5;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double er_rough = clamp(this.rough, 0.0, 1.0);
    double er_dx0 = this.strength * sin(this.scale * pAffineTP.x + 0.7 * sin(this.scale * pAffineTP.y));
    double er_dy0 = this.strength * cos(this.scale * pAffineTP.y + 0.7 * cos(this.scale * pAffineTP.x));
    double er_dx1 = er_rough * this.strength * 0.5 * sin(this.scale * 2.1 * pAffineTP.x - this.scale * pAffineTP.y * 0.4);
    double er_dy1 = er_rough * this.strength * 0.5 * cos(this.scale * 2.1 * pAffineTP.y + this.scale * pAffineTP.x * 0.4);

    pVarTP.x += pAmount * (pAffineTP.x + er_dx0 + er_dx1);
    pVarTP.y += pAmount * (pAffineTP.y + er_dy0 + er_dy1);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{strength, scale, rough}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_ROUGH.equalsIgnoreCase(pName)) rough = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "erode"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float er_rough = clamp(__erode_rough, 0.0f, 1.0f);\n"
         + "  float er_dx0 = __erode_strength * sinf(__erode_scale * __x + 0.7f * sinf(__erode_scale * __y));\n"
         + "  float er_dy0 = __erode_strength * cosf(__erode_scale * __y + 0.7f * cosf(__erode_scale * __x));\n"
         + "  float er_dx1 = er_rough * __erode_strength * 0.5f * sinf(__erode_scale * 2.1f * __x - __erode_scale * __y * 0.4f);\n"
         + "  float er_dy1 = er_rough * __erode_strength * 0.5f * cosf(__erode_scale * 2.1f * __y + __erode_scale * __x * 0.4f);\n"
         + "  __px += __erode * (__x + er_dx0 + er_dx1);\n"
         + "  __py += __erode * (__y + er_dy0 + er_dy1);\n";
  }
}