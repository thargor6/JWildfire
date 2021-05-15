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

public class Fibonacci2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SC = "sc";
  private static final String PARAM_SC2 = "sc2";
  private static final String[] paramNames = {PARAM_SC, PARAM_SC2};

  private double sc = 1.0;
  private double sc2 = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // fibonacci2 by Larry Berlin, http://aporev.deviantart.com/gallery/#/d2blmhg
    //  p^z - (-p)^(-z)
    //z' = -----------------
    //      sqrt(5)
    //
    //Where p is the Golden Ratio.
    //This function generates the fibonacci sequence
    //for real integer values.
    //1 2 3 4 5 6  7  8  9 10 11  12  13  14  15 < Real Value
    //1 1 2 3 5 8 13 21 34 55 89 144 233 377 610 < Fib Value
    //
    //Negative real integers produce the negative fibonacci sequence,
    //which is the same as the normal one, except every
    //other number is negative.
    //1 0 -1 -2 -3 -4 -5 -6 -7  -8 < Real Value
    //1 0  1 -1  3 -3  5 -8 13 -21 < Fib Value

    double a = pAffineTP.y * fnatlog;
    double snum1 = sin(a);
    double cnum1 = cos(a);
    double b = (pAffineTP.x * M_PI + pAffineTP.y * fnatlog) * -1.0;
    double snum2 = sin(b);
    double cnum2 = cos(b);

    double eradius1 = sc * exp(sc2 * (pAffineTP.x * fnatlog));
    double eradius2 = sc * exp(sc2 * ((pAffineTP.x * fnatlog - pAffineTP.y * M_PI) * -1));

    pVarTP.x += pAmount * (eradius1 * cnum1 - eradius2 * cnum2) * ffive;
    pVarTP.y += pAmount * (eradius1 * snum1 - eradius2 * snum2) * ffive;
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
    return new Object[]{sc, sc2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SC.equalsIgnoreCase(pName))
      sc = pValue;
    else if (PARAM_SC2.equalsIgnoreCase(pName))
      sc2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "fibonacci2";
  }

  private double ffive, fnatlog;
  private final double M_SQRT5 = 2.2360679774997898;
  private final double M_PHI = 1.61803398874989484820;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    ffive = 1.0 / M_SQRT5;
    fnatlog = log(M_PHI);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float ffive = 0.447213595f;\n"
        + "float fnatlog = logf(1.61803398f);\n"
        + "float a = __y * fnatlog;\n"
        + "float snum1 = sinf(a);\n"
        + "float cnum1 = cosf(a);\n"
        + "float b = (__x * PI + __y * fnatlog) * -1.0f;\n"
        + "float snum2 = sinf(b);\n"
        + "float cnum2 = cosf(b);\n"
        + "float eradius1 = varpar->fibonacci2_sc * expf(varpar->fibonacci2_sc2 * (__x * fnatlog));\n"
        + "float eradius2 = varpar->fibonacci2_sc * expf(varpar->fibonacci2_sc2 * ((__x * fnatlog - __y * PI) * -1));\n"
        + "__px += varpar->fibonacci2 * (eradius1 * cnum1 - eradius2 * cnum2) * ffive;\n"
        + "__py += varpar->fibonacci2 * (eradius1 * snum1 - eradius2 * snum2) * ffive;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->fibonacci2 * __z;\n" : "");
  }
}

