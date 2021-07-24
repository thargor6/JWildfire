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

public class TanqFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Tanq by zephyrtronium http://zephyrtronium.deviantart.com/art/Quaternion-Apo-Plugin-Pack-165451482 */

    double abs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z);
    double s = sin(pAffineTP.x);
    double sysz = sqr(pAffineTP.y) + sqr(pAffineTP.z);
    double ni = pAmount / (sqr(pAffineTP.x) + sysz);
    double c = cos(pAffineTP.x);
    double sh = sinh(abs_v);
    double ch = cosh(abs_v);
    double C = c * sh / abs_v;
    double B = -s * sh / abs_v;
    double stcv = s * ch;
    double nstcv = -stcv;
    double ctcv = c * ch;
    pVarTP.x += (stcv * ctcv + C * B * sysz) * ni;
    pVarTP.y += (nstcv * B * pAffineTP.y + C * pAffineTP.y * ctcv) * ni;
    pVarTP.z += (nstcv * B * pAffineTP.z + C * pAffineTP.z * ctcv) * ni;


  }

  @Override
  public String getName() {
    return "tanq";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "   float abs_v = hypotf(__y, __z);\n"
        + "    float s = sinf(__x);\n"
        + "    float sysz = __y*__y + __z*__z;\n"
        + "    float ni = __tanq / (__x*__x + sysz);\n"
        + "    float c = cosf(__x);\n"
        + "    float sh = sinhf(abs_v);\n"
        + "    float ch = coshf(abs_v);\n"
        + "    float C = c * sh / abs_v;\n"
        + "    float B = -s * sh / abs_v;\n"
        + "    float stcv = s * ch;\n"
        + "    float nstcv = -stcv;\n"
        + "    float ctcv = c * ch;\n"
        + "    __px += (stcv * ctcv + C * B * sysz) * ni;\n"
        + "    __py += (nstcv * B * __y + C * __y * ctcv) * ni;\n"
        + "    __pz += (nstcv * B * __z + C * __z * ctcv) * ni;\n";
  }
}
