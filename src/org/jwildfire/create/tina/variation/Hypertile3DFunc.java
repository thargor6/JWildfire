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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Hypertile3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_P = "p";
  private static final String PARAM_Q = "q";
  private static final String PARAM_N = "n";

  private static final String[] paramNames = {PARAM_P, PARAM_Q, PARAM_N};

  private int p = 3;
  private int q = 7;
  private int n = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* hypertile3D by Zueuk, http://zueuk.deviantart.com/art/3D-Hyperbolic-tiling-plugins-169047926 */
    double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z);

    double x2cx = c2x * pAffineTP.x, y2cy = c2y * pAffineTP.y;

    double d = pAmount / (c2 * r2 + x2cx - y2cy + 1);

    pVarTP.x += d * (pAffineTP.x * s2x - cx * (y2cy - r2 - 1));
    pVarTP.y += d * (pAffineTP.y * s2y + cy * (-x2cx - r2 - 1));
    pVarTP.z += d * (pAffineTP.z * s2z);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{p, q, n};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_P.equalsIgnoreCase(pName))
      p = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else if (PARAM_Q.equalsIgnoreCase(pName))
      q = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else if (PARAM_N.equalsIgnoreCase(pName))
      n = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hypertile3D";
  }

  private double c2x, c2y, c2, s2x, s2y, s2z, cx, cy;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double pa = 2 * M_PI / p, qa = 2 * M_PI / q;

    double r = -(cos(pa) - 1) / (cos(pa) + cos(qa));
    if (r > 0)
      r = 1 / sqrt(1 + r);
    else
      r = 1;

    double na = n * pa;

    cx = r * cos(na);
    cy = r * sin(na);

    c2 = sqr(cx) + sqr(cy);

    c2x = 2 * cx;
    c2y = 2 * cy;

    s2x = 1 + sqr(cx) - sqr(cy);
    s2y = 1 + sqr(cy) - sqr(cx);
    s2z = 1 - sqr(cy) - sqr(cx);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float c2x, c2y, c2, s2x, s2y, s2z, cx, cy;\n"
        + "float pa = 2 * PI / lroundf(__hypertile3D_p), qa = 2 * PI / lroundf(__hypertile3D_q);\n"
        + "float r = -(cosf(pa) - 1) / (cosf(pa) + cosf(qa));\n"
        + "if (r > 0)\n"
        + "  r = 1.f / sqrtf(1.f + r);\n"
        + "else\n"
        + "  r = 1.f;\n"
        + "float na = lroundf(__hypertile3D_n) * pa;\n"
        + "cx = r * cosf(na);\n"
        + "cy = r * sinf(na);\n"
        + "c2 = cx*cx + cy*cy;\n"
        + "c2x = 2 * cx;\n"
        + "c2y = 2 * cy;\n"
        + "s2x = 1 + cx*cx - cy*cy;\n"
        + "s2y = 1 + cy*cy - cx*cx;\n"
        + "s2z = 1 - cy*cy - cx*cx;\n"
        + "float r2 = __x*__x + __y*__y + __z*__z;\n"
        + "float x2cx = c2x * __x, y2cy = c2y * __y;\n"
        + "float d = __hypertile3D / (c2 * r2 + x2cx - y2cy + 1.f);\n"
        + "__px += d * (__x * s2x - cx * (y2cy - r2 - 1.f));\n"
        + "__py += d * (__y * s2y + cy * (-x2cx - r2 - 1.f));\n"
        + "__pz += d * (__z * s2z);\n";
  }
}
