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

public class PostFalloff2Func extends Falloff2Func implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* post_falloff2 by Xyrus02 */
    switch (type) {
      case 1:
        calcFunctionRadial(pContext, pXForm, pVarTP.x, pVarTP.y, pVarTP.z, pAffineTP, pVarTP, pAmount);
        break;
      case 2:
        calcFunctionGaussian(pContext, pXForm, pVarTP.x, pVarTP.y, pVarTP.z, pAffineTP, pVarTP, pAmount);
        break;
      default:
        calcFunction(pContext, pXForm, pVarTP.x, pVarTP.y, pVarTP.z, pAffineTP, pVarTP, pAmount);
    }
  }

  @Override
  public String getName() {
    return "post_falloff2";
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_BLUR, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _rmax = 0.04 * __post_falloff2_scatter;\n"
            + "float pIn_x = __px;\n"
            + "float pIn_y = __py;\n"
            + "float pIn_z = __pz;\n"
            + "switch (lroundf(__post_falloff2_type)) {\n"
            + "      case 1:\n"
            + "        {\n"
            + "float r_in = sqrtf(pIn_x*pIn_x + pIn_y*pIn_y + pIn_z*pIn_z) + 1e-6;\n"
            + "    float d = sqrtf(sqrf(pIn_x - __post_falloff2_x0) + sqrf(pIn_y - __post_falloff2_y0) + sqrf(pIn_z - __post_falloff2_z0));\n"
            + "    if (lroundf(__post_falloff2_invert) != 0)\n"
            + "      d = 1 - d;\n"
            + "    if (d < 0)\n"
            + "      d = 0;\n"
            + "    d = (d - __post_falloff2_mindist) * _rmax;\n"
            + "    if (d < 0)\n"
            + "      d = 0;\n"
            + "\n"
            + "    float sigma = asinf(pIn_z / r_in) + __post_falloff2_mul_z * RANDFLOAT() * d;\n"
            + "    float phi = atan2f(pIn_y, pIn_x) + __post_falloff2_mul_y * RANDFLOAT() * d;\n"
            + "    float r = r_in + __post_falloff2_mul_x * RANDFLOAT() * d;\n"
            + "\n"
            + "    float sins = sinf(sigma);\n"
            + "    float coss = cosf(sigma);\n"
            + "\n"
            + "    float sinp = sinf(phi);\n"
            + "    float cosp = cosf(phi);\n"
            + "\n"
            + "    __px += __post_falloff2 * (r * coss * cosp);\n"
            + "    __py += __post_falloff2 * (r * coss * sinp);\n"
            + "    __pz += __post_falloff2 * (sins);\n"
            + "    __pal = fabsf(fracf(__pal + __post_falloff2_mul_c * RANDFLOAT() * d));\n"
            + "        }\n"
            + "        break;\n"
            + "      case 2:\n"
            + "        {\n"
            + "float d = sqrtf(sqrf(pIn_x - __post_falloff2_x0) + sqrf(pIn_y - __post_falloff2_y0) + sqrf(pIn_z - __post_falloff2_z0));\n"
            + "    if (lroundf(__post_falloff2_invert) != 0)\n"
            + "      d = 1 - d;\n"
            + "    if (d < 0)\n"
            + "      d = 0;\n"
            + "    d = (d - __post_falloff2_mindist) * _rmax;\n"
            + "    if (d < 0)\n"
            + "      d = 0;\n"
            + "\n"
            + "    float sigma = d * RANDFLOAT() * 2 * PI;\n"
            + "    float phi = d * RANDFLOAT() * PI;\n"
            + "    float r = d * RANDFLOAT();\n"
            + "\n"
            + "    float sins = sinf(sigma);\n"
            + "    float coss = cosf(sigma);\n"
            + "\n"
            + "    float sinp = sinf(phi);\n"
            + "    float cosp = cosf(phi);\n"
            + "\n"
            + "    __px += __post_falloff2 * (pIn_x + __post_falloff2_mul_x * r * coss * cosp);\n"
            + "    __py += __post_falloff2 * (pIn_y + __post_falloff2_mul_y * r * coss * sinp);\n"
            + "    __pz += __post_falloff2 * (pIn_z + __post_falloff2_mul_z * r * sins);\n"
            + "    __pal = fabsf(fracf(__pal + __post_falloff2_mul_c * RANDFLOAT() * d));\n"
            + "        }\n"
            + "        break;\n"
            + "      default:\n"
            + "        {\n"
            + "float d = sqrtf(sqrf(pIn_x - __post_falloff2_x0) + sqrf(pIn_y - __post_falloff2_y0) + sqrf(pIn_z - __post_falloff2_z0));\n"
            + "    if (lroundf(__post_falloff2_invert) != 0)\n"
            + "      d = 1 - d;\n"
            + "    if (d < 0)\n"
            + "      d = 0;\n"
            + "    d = (d - __post_falloff2_mindist) * _rmax;\n"
            + "    if (d < 0)\n"
            + "      d = 0;\n"
            + "\n"
            + "    __px += __post_falloff2 * (pIn_x + __post_falloff2_mul_x * RANDFLOAT() * d);\n"
            + "    __py += __post_falloff2 * (pIn_y + __post_falloff2_mul_y * RANDFLOAT() * d);\n"
            + "    __pz += __post_falloff2 * (pIn_z + __post_falloff2_mul_z * RANDFLOAT() * d);\n"
            + "    __pal = fabsf(fracf(__pal + __post_falloff2_mul_c * RANDFLOAT() * d));\n"
            + "        }\n"
            + "    }\n";
  }

}
