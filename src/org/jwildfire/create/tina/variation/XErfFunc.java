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


public class XErfFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // "xerf" variation created by zephyrtronium implemented into JWildfire by darkbeam

    double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z);
    if (r2 <= SMALL_EPSILON) r2 = SMALL_EPSILON; // no overflow fix by Dark

    pVarTP.x += ((fabs(pAffineTP.x) >= 2.0) ? (pAffineTP.x / r2) : erf(pAffineTP.x)) * pAmount;
    pVarTP.y += ((fabs(pAffineTP.y) >= 2.0) ? (pAffineTP.y / r2) : erf(pAffineTP.y)) * pAmount;
    pVarTP.z += ((fabs(pAffineTP.z) >= 2.0) ? (pAffineTP.z / r2) : erf(pAffineTP.z)) * pAmount;
  }

  @Override
  public String getName() {
    return "xerf";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float r2 = __x*__x + __y*__y + __z*__z;\n"
         + "if (r2 <= 1.e-6f) r2 = 1.e-6f;\n"
         + "__px += ((fabsf(__x) >= 2.0) ? (__x / r2) : xerf_erf(__x)) * varpar->xerf;\n"
         + "__py += ((fabsf(__y) >= 2.0) ? (__y / r2) : xerf_erf(__y)) * varpar->xerf;\n"
         + "__pz += ((fabsf(__z) >= 2.0) ? (__z / r2) : xerf_erf(__z)) * varpar->xerf;\n";
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float xerf_erf(float z) {\n"
        + "    float t = 1.0 / (1.0 + 0.5 * fabsf(z));\n"
        + "    float ans = 1 - t * expf(-z * z - 1.26551223 +\n"
        + "        t * (1.00002368 +\n"
        + "        t * (0.37409196 +\n"
        + "            t * (0.09678418 +\n"
        + "                t * (-0.18628806 +\n"
        + "                    t * (0.27886807 +\n"
        + "                        t * (-1.13520398 +\n"
        + "                            t * (1.48851587 +\n"
        + "                                t * (-0.82215223 +\n"
        + "                                    t * (0.17087277))))))))));\n"
        + "    if (z >= 0)\n"
        + "      return ans;\n"
        + "    else\n"
        + "      return -ans;\n"
        + "  }";
  }
}