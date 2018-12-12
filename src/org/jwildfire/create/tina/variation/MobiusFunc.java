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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class MobiusFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RE_A = "re_a";
  private static final String PARAM_RE_B = "re_b";
  private static final String PARAM_RE_C = "re_c";
  private static final String PARAM_RE_D = "re_d";
  private static final String PARAM_IM_A = "im_a";
  private static final String PARAM_IM_B = "im_b";
  private static final String PARAM_IM_C = "im_c";
  private static final String PARAM_IM_D = "im_d";

  private static final String[] paramNames = {PARAM_RE_A, PARAM_RE_B, PARAM_RE_C, PARAM_RE_D, PARAM_IM_A, PARAM_IM_B, PARAM_IM_C, PARAM_IM_D};

  private double re_a = 0.1;
  private double re_b = 0.2;
  private double re_c = -0.15;
  private double re_d = 0.21;
  private double im_a = 0.2;
  private double im_b = -0.12;
  private double im_c = -0.15;
  private double im_d = 0.1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Mobius, by eralex
    double re_u = re_a * pAffineTP.x - im_a * pAffineTP.y + re_b;
    double im_u = re_a * pAffineTP.y + im_a * pAffineTP.x + im_b;
    double re_v = re_c * pAffineTP.x - im_c * pAffineTP.y + re_d;
    double im_v = re_c * pAffineTP.y + im_c * pAffineTP.x + im_d;

    double d = (re_v * re_v + im_v * im_v);
    if (d == 0) {
      return;
    }
    double rad_v = pAmount / d;

    pVarTP.x += rad_v * (re_u * re_v + im_u * im_v);
    pVarTP.y += rad_v * (im_u * re_v - re_u * im_v);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{re_a, re_b, re_c, re_d, im_a, im_b, im_c, im_d};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"Re_A", "Re_B", "Re_C", "Re_D", "Im_A", "Im_B", "Im_C", "Im_D"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RE_A.equalsIgnoreCase(pName))
      re_a = pValue;
    else if (PARAM_RE_B.equalsIgnoreCase(pName))
      re_b = pValue;
    else if (PARAM_RE_C.equalsIgnoreCase(pName))
      re_c = pValue;
    else if (PARAM_RE_D.equalsIgnoreCase(pName))
      re_d = pValue;
    else if (PARAM_IM_A.equalsIgnoreCase(pName))
      im_a = pValue;
    else if (PARAM_IM_B.equalsIgnoreCase(pName))
      im_b = pValue;
    else if (PARAM_IM_C.equalsIgnoreCase(pName))
      im_c = pValue;
    else if (PARAM_IM_D.equalsIgnoreCase(pName))
      im_d = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "mobius";
  }

}
