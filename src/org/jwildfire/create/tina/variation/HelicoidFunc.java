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

public class HelicoidFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_FREQUENCY = "frequency";

  private static final String[] paramNames = {PARAM_FREQUENCY};

  private double frequency = 1.0;


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* helicoid by zy0rg, http://zy0rg.deviantart.com/art/Helix-Helicoid-687956099 converted by Brad Stefanov */
    double range = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double s = sin(pAffineTP.z * M_2PI * frequency + atan2(pAffineTP.y, pAffineTP.x));
    double c = cos(pAffineTP.z * M_2PI * frequency + atan2(pAffineTP.y, pAffineTP.x));


    pVarTP.x += pAmount * c * range;
    pVarTP.y += pAmount * s * range;
    pVarTP.z += pAmount * pAffineTP.z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{frequency};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQUENCY.equalsIgnoreCase(pName))
      frequency = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "helicoid";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float range = sqrtf(__x * __x + __y * __y);\n"
        + "    float s = sinf(__z * (2.0f*PI) * __helicoid_frequency + atan2f(__y, __x));\n"
        + "    float c = cosf(__z * (2.0f*PI) * __helicoid_frequency + atan2f(__y, __x));\n"
        + "    __px += __helicoid * c * range;\n"
        + "    __py += __helicoid * s * range;\n"
        + "    __pz += __helicoid * __z;\n";
  }
}
