/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.*;

public class PostDepthFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String[] paramNames = {PARAM_POWER};

  private double power = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // post_depth by Zyorg, http://zy0rg.deviantart.com/art/Blur-Package-347648919
    double coeff = fabs(pVarTP.z);
    if (coeff != 0.0 && power != 1.0)
      coeff = exp(log(coeff) * power);

    pVarTP.x += pAmount * (pAffineTP.x + pVarTP.x * coeff);
    pVarTP.y += pAmount * (pAffineTP.y + pVarTP.y * coeff);
    pVarTP.z += pAmount * (pAffineTP.z + pVarTP.z * coeff);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{power};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_depth";
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float coeff = fabsf(__z);\n"
        + "if (coeff != 0.f  &&  __post_depth_power != 1.f)\n"
        + "    coeff = expf(logf(coeff) * __post_depth_power);\n"
        + "__px += __post_depth * (__x + __px * coeff);\n"
        + "__py += __post_depth * (__y + __py * coeff);\n"
        + "__pz += __post_depth * (__z + __pz * coeff);";
  }
}
