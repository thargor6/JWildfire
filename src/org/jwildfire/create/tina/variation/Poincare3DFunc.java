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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Poincare3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_R = "r";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";

  private static final String[] paramNames = {PARAM_R, PARAM_A, PARAM_B};

  private double r = 0;
  private double a = 0;
  private double b = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* poincare3D by Zueuk, http://zueuk.deviantart.com/art/3D-Hyperbolic-tiling-plugins-169047926 */
    double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z);

    double x2cx = c2x * pAffineTP.x, y2cy = c2y * pAffineTP.y, z2cz = c2z * pAffineTP.z;

    double d = pAmount / (c2 * r2 - x2cx - y2cy - z2cz + 1);

    pVarTP.x += d * (pAffineTP.x * s2x + cx * (y2cy + z2cz - r2 - 1));
    pVarTP.y += d * (pAffineTP.y * s2y + cy * (x2cx + z2cz - r2 - 1));
    pVarTP.z += d * (pAffineTP.z * s2z + cz * (y2cy + x2cx - r2 - 1));
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{r, a, b};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_R.equalsIgnoreCase(pName))
      r = pValue;
    else if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "poincare3D";
  }

  private double cx, cy, cz, c2, c2x, c2y, c2z, s2x, s2y, s2z;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    cx = -r * cos(a * M_PI_2) * cos(b * M_PI_2);
    cy = r * sin(a * M_PI_2) * cos(b * M_PI_2);
    cz = -r * sin(b * M_PI_2);

    c2 = sqr(cx) + sqr(cy) + sqr(cz);

    c2x = 2 * cx;
    c2y = 2 * cy;
    c2z = 2 * cz;

    s2x = sqr(cx) - sqr(cy) - sqr(cz) + 1;
    s2y = sqr(cy) - sqr(cx) - sqr(cz) + 1;
    s2z = sqr(cz) - sqr(cy) - sqr(cx) + 1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float cx, cy, cz, c2, c2x, c2y, c2z, s2x, s2y, s2z;\n"
        + "cx = -varpar->poincare3D_r * cosf(varpar->poincare3D_a * (PI*0.5f)) * cosf(varpar->poincare3D_b * (PI*0.5f));\n"
        + "    cy = varpar->poincare3D_r * sinf(varpar->poincare3D_a * (PI*0.5f)) * cosf(varpar->poincare3D_b * (PI*0.5f));\n"
        + "    cz = -varpar->poincare3D_r * sinf(varpar->poincare3D_b * (PI*0.5f));\n"
        + "\n"
        + "    c2 = cx*cx + cy*cy + cz*cz;\n"
        + "\n"
        + "    c2x = 2 * cx;\n"
        + "    c2y = 2 * cy;\n"
        + "    c2z = 2 * cz;\n"
        + "\n"
        + "    s2x = cx*cx - cy*cy - cz*cz + 1;\n"
        + "    s2y = cy*cy - cx*cx - cz*cz + 1;\n"
        + "    s2z = cz*cz - cy*cy - cx*cx + 1;\n"
        + "    float r2 = __x*__x + __y*__y + __z*__z;\n"
        + "\n"
        + "    float x2cx = c2x * __x, y2cy = c2y * __y, z2cz = c2z * __z;\n"
        + "\n"
        + "    float d = varpar->poincare3D / (c2 * r2 - x2cx - y2cy - z2cz + 1);\n"
        + "\n"
        + "    __px += d * (__x * s2x + cx * (y2cy + z2cz - r2 - 1));\n"
        + "    __py += d * (__y * s2y + cy * (x2cx + z2cz - r2 - 1));\n"
        + "    __pz += d * (__z * s2z + cz * (y2cy + x2cx - r2 - 1));\n";
  }
}
