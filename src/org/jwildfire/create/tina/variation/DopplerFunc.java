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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class DopplerFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_DIR = "dir";
  private static final String PARAM_SPEED = "speed";
  private static final String[] paramNames = {PARAM_DIR, PARAM_SPEED};

  private double dir = 0.0;
  private double speed = 0.5;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dp_spd = clamp(this.speed, -0.99, 0.99);
    double dp_ca = cos(this.dir);
    double dp_sa = sin(this.dir);
    double dp_along = pAffineTP.x * dp_ca + pAffineTP.y * dp_sa;
    double dp_perp = -pAffineTP.x * dp_sa + pAffineTP.y * dp_ca;
    double dp_scale = 1.0 / (1.0 + dp_spd * dp_along);
    double dp_r = dp_along * dp_scale;

    pVarTP.x += pAmount * (dp_r * dp_ca - dp_perp * dp_sa);
    pVarTP.y += pAmount * (dp_r * dp_sa + dp_perp * dp_ca);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{dir, speed}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_DIR.equalsIgnoreCase(pName)) dir = pValue;
    else if (PARAM_SPEED.equalsIgnoreCase(pName)) speed = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "doppler"; }
  
  @Override
  public void randomize() {
  	dir = Math.random() * 2.0 * Math.PI - Math.PI;
  	speed = Math.random() * 2.0 - 1.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float dp_spd = clamp(__doppler_speed, -0.99f, 0.99f);\n"
         + "  float dp_ca = cosf(__doppler_dir);\n"
         + "  float dp_sa = sinf(__doppler_dir);\n"
         + "  float dp_along = __x * dp_ca + __y * dp_sa;\n"
         + "  float dp_perp = -__x * dp_sa + __y * dp_ca;\n"
         + "  float dp_scale = 1.0f / (1.0f + dp_spd * dp_along);\n"
         + "  float dp_r = dp_along * dp_scale;\n"
         + "  __px += __doppler * (dp_r * dp_ca - dp_perp * dp_sa);\n"
         + "  __py += __doppler * (dp_r * dp_sa + dp_perp * dp_ca);\n";
  }
}