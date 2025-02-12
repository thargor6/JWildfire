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

import odk.lang.FastMath;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class SechqFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Sechq by zephyrtronium http://zephyrtronium.deviantart.com/art/Quaternion-Apo-Plugin-Pack-165451482 */
    double abs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z);
    double s = sin(abs_v);
    double c = cos(abs_v);
    double sh = sinh(pAffineTP.x);
    double ch = cosh(pAffineTP.x);
    double ni = pAmount / (sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z));
    double C = ni * sh * s / abs_v;
    pVarTP.x += ch * c * ni;
    pVarTP.y -= C * pAffineTP.y;
    pVarTP.z -= C * pAffineTP.z;
  }

  @Override
  public String getName() {
    return "sechq";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float abs_v = hypotf(__y, __z);\n"
        + "    float s = sinf(abs_v);\n"
        + "    float c = cosf(abs_v);\n"
        + "    float sh = sinhf(__x);\n"
        + "    float ch = coshf(__x);\n"
        + "    float ni = __sechq / (__x*__x + __y*__y + __z*__z);\n"
        + "    float C = ni * sh * s / abs_v;\n"
        + "    __px += ch * c * ni;\n"
        + "    __py -= C * __y;\n"
        + "    __pz -= C * __z;\n";
  }
}
