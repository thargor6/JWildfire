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

import org.jwildfire.base.mathlib.Complex;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class PlusRecipFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_AR = "ar";
  private static final String PARAM_AI = "ai";

  private static final String[] paramNames = {PARAM_AR, PARAM_AI};

  private double ar = 4.0;
  private double ai = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // author DarkBeam. Implemented by DarkBeam 2019
    Complex z = new Complex(pAffineTP.x, pAffineTP.y);
    Complex k = new Complex(z);
    Complex a = new Complex(ar, ai);
    double aa = sqrt(a.Mag2eps());
    k.Sqr();
    k.Sub(a);
    k.Sqrt();
    k.Add(z);
    z.Copy(k);
    z.Sqr();

    if (sqrt(z.Mag2eps()) < aa) {  // forces it to stay in its half plane but ONLY when imag(a) = 0. Else... ;)
      k.Conj();
      a.Scale(-1.0 / aa);
      k.Mul(a);
    }

    if (k.re < 0) {
      k.Neg(); // fixes the issue when imag(a) != 0.
    }

    k.Scale(pAmount);
    pVarTP.x += k.re;
    pVarTP.y += k.im;
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
    return new Object[]{ar, ai};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_AR.equalsIgnoreCase(pName))
      ar = pValue;
    else if (PARAM_AI.equalsIgnoreCase(pName))
      ai = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "plusrecip";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }


  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "Complex z;\n"
            + "Complex_Init(&z, __x, __y);\n"
            + "Complex k;\n"
            + "Complex_Init(&k, z.re, z.im);\n"
            + "Complex a;\n"
            + "Complex_Init(&a, __plusrecip_ar, __plusrecip_ai);\n"
            + "float aa = sqrtf(Complex_Mag2eps(&a));\n"
            + "Complex_Sqr(&k);\n"
            + "Complex_Sub(&k, &a);\n"
            + "Complex_Sqrt(&k);\n"
            + "Complex_Add(&k, &z);\n"
            + "Complex_Copy(&z, &k);\n"
            + "Complex_Sqr(&z);\n"
            + "if(sqrtf(Complex_Mag2eps(&z))<aa) {\n"
            + "  Complex_Conj(&k);\n"
            + "  Complex_Scale(&a, -1.0 / aa);\n"
            + "  Complex_Mul(&k, &a);\n"
            + "}\n"
            + "if(k.re < 0) {\n"
            + "  Complex_Neg(&k);\n"
            + "}\n"
            + "Complex_Scale(&k, __plusrecip);\n"
           + "__px += k.re;\n"
            + "__py += k.im;\n"
            + (context.isPreserveZCoordinate() ? "__pz += __plusrecip * __z;\n": "");
  }
}










