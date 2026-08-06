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
import static org.jwildfire.base.mathlib.MathLib.pow;

public class GothicArchFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_HEIGHT = "height";
  private static final String PARAM_SHARPNESS = "sharpness";
  private static final String[] paramNames = {PARAM_HEIGHT, PARAM_SHARPNESS};

  private double height = 1.0;
  private double sharpness = 2.0;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ga_h = Math.max(fabs(this.height), 0.1);
    double ga_sharp = Math.max(fabs(this.sharpness), 0.1);
    double ga_t = clamp(pAffineTP.y / ga_h, -2.0, 2.0);
    double ga_profile = Math.max(1.0 - pow(fabs(ga_t), ga_sharp), 0.01);

    pVarTP.x += pAmount * pAffineTP.x * ga_profile;
    pVarTP.y += pAmount * pAffineTP.y;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{height, sharpness}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_HEIGHT.equalsIgnoreCase(pName)) height = pValue;
    else if (PARAM_SHARPNESS.equalsIgnoreCase(pName)) sharpness = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "gothic_arch"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float ga_h = fmaxf(fabsf(__gothic_arch_height), 0.1f);\n"
         + "  float ga_sharp = fmaxf(fabsf(__gothic_arch_sharpness), 0.1f);\n"
         + "  float ga_t = clamp(__y / ga_h, -2.0f, 2.0f);\n"
         + "  float ga_profile = fmaxf(1.0f - powf(fabsf(ga_t), ga_sharp), 0.01f);\n"
         + "  __px += __gothic_arch * __x * ga_profile;\n"
         + "  __py += __gothic_arch * __y;\n";
  }
}