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
import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class RainbowArcFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_ARC = "arc";
  private static final String[] paramNames = {PARAM_RADIUS, PARAM_WIDTH, PARAM_ARC};

  private double radius = 0.5;
  private double width = 0.2;
  private double arc = 3.14159;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ra_radius = Math.max(fabs(this.radius), 0.01);
    double ra_width = Math.max(fabs(this.width), 0.001);
    double ra_arc = Math.max(fabs(this.arc), 0.01);
    double ra_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double ra_theta = atan2(pAffineTP.y, pAffineTP.x);
    double ra_band_r = Math.round((ra_r - ra_radius) / ra_width) * ra_width + ra_radius;
    double ra_new_r = ra_r + 0.6 * (Math.max(ra_band_r, 1e-4) - ra_r);
    double ra_new_theta = clamp(ra_theta, -ra_arc, ra_arc);

    pVarTP.x += pAmount * ra_new_r * cos(ra_new_theta);
    pVarTP.y += pAmount * ra_new_r * sin(ra_new_theta);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{radius, width, arc}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName)) radius = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else if (PARAM_ARC.equalsIgnoreCase(pName)) arc = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "rainbow_arc"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float ra_radius = fmaxf(fabsf(__rainbow_arc_radius), 0.01f);\n"
         + "  float ra_width = fmaxf(fabsf(__rainbow_arc_width), 0.001f);\n"
         + "  float ra_arc = fmaxf(fabsf(__rainbow_arc_arc), 0.01f);\n"
         + "  float ra_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float ra_theta = atan2f(__y, __x);\n"
         + "  float ra_band_r = roundf((ra_r - ra_radius) / ra_width) * ra_width + ra_radius;\n"
         + "  float ra_new_r = mix(ra_r, fmaxf(ra_band_r, 1e-4f), 0.6f);\n"
         + "  float ra_new_theta = clamp(ra_theta, -ra_arc, ra_arc);\n"
         + "  __px += __rainbow_arc * ra_new_r * cosf(ra_new_theta);\n"
         + "  __py += __rainbow_arc * ra_new_r * sinf(ra_new_theta);\n";
  }
}