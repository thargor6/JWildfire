/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

public class Hypertile3D1Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_P = "p";
  private static final String PARAM_Q = "q";

  private static final String[] paramNames = {PARAM_P, PARAM_Q};

  private int p = 3;
  private int q = 7;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* hypertile3D1 by Zueuk, http://zueuk.deviantart.com/art/3D-Hyperbolic-tiling-plugins-169047926 */
    double a = pContext.random(Integer.MAX_VALUE) * pa;
    double sina = sin(a);
    double cosa = cos(a);
    double cx = r * cosa;
    double cy = r * sina;

    double s2x = 1 + sqr(cx) - sqr(cy);
    double s2y = 1 + sqr(cy) - sqr(cx);

    double r2 = sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z);

    double x2cx = 2 * cx * pAffineTP.x, y2cy = 2 * cy * pAffineTP.y;

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
    return new Object[]{p, q};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_P.equalsIgnoreCase(pName))
      p = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else if (PARAM_Q.equalsIgnoreCase(pName))
      q = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hypertile3D1";
  }

  private double pa, qa, r, c2, s2z;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    pa = 2 * M_PI / p;
    qa = 2 * M_PI / q;

    r = -(cos(pa) - 1) / (cos(pa) + cos(qa));
    if (r > 0)
      r = 1 / sqrt(1 + r);
    else
      r = 1;

    c2 = sqr(r);
    s2z = 1 - c2;
  }

}
