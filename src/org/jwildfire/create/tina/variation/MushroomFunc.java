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
import static org.jwildfire.base.mathlib.MathLib.exp;

public class MushroomFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_CAP_R = "cap_r";
  private static final String PARAM_CAP_W = "cap_w";
  private static final String PARAM_STALK_W = "stalk_w";
  private static final String[] paramNames = {PARAM_CAP_R, PARAM_CAP_W, PARAM_STALK_W};

  private double cap_r = 0.5;
  private double cap_w = 2.0;
  private double stalk_w = 0.3;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double mu_cr = Math.max(fabs(this.cap_r), 0.01);
    double mu_cw = Math.max(fabs(this.cap_w), 0.01);
    double mu_sw = clamp(this.stalk_w, 0.05, 2.0);
    
    // Fixed: Replaced sw_tanh with native Math.tanh for the Java CPU layer
    double mu_above = 0.5 + 0.5 * Math.tanh(pAffineTP.y / (mu_cr * 0.3));
    double mu_cap_env = exp(-(pAffineTP.y - mu_cr * 0.5) * (pAffineTP.y - mu_cr * 0.5) / (mu_cr * mu_cr * 0.5 + 1e-6));
    double mu_scale = mu_sw + (mu_cw - mu_sw) * mu_above + mu_cap_env * 0.4;

    pVarTP.x = pAmount * pAffineTP.x * mu_scale;
    pVarTP.y = pAmount * pAffineTP.y;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{cap_r, cap_w, stalk_w}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CAP_R.equalsIgnoreCase(pName)) cap_r = pValue;
    else if (PARAM_CAP_W.equalsIgnoreCase(pName)) cap_w = pValue;
    else if (PARAM_STALK_W.equalsIgnoreCase(pName)) stalk_w = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "mushroom"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float mu_cr = fmaxf(fabsf(__mushroom_cap_r), 0.01f);\n"
         + "  float mu_cw = fmaxf(fabsf(__mushroom_cap_w), 0.01f);\n"
         + "  float mu_sw = clamp(__mushroom_stalk_w, 0.05f, 2.0f);\n"
         + "  float mu_above = 0.5f + 0.5f * tanhf(__y / (mu_cr * 0.3f));\n"
         + "  float mu_cap_env = expf(-(__y - mu_cr * 0.5f) * (__y - mu_cr * 0.5f) / (mu_cr * mu_cr * 0.5f + 1e-6f));\n"
         + "  float mu_scale = mix(mu_sw, mu_cw, mu_above) + mu_cap_env * 0.4f;\n"
         + "  __px = __mushroom * __x * mu_scale;\n"
         + "  __py = __mushroom * __y;\n";
  }
}