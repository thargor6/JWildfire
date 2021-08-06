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
package org.jwildfire.base;

public class GPUCodeHelper {

  public static void main(String args[]) {
    new GPUCodeHelper().run();
  }

  private void run() {
    String code =
        "double xx = 0.0, yy = 0.0;\n"
            + "  double xr = pAffineTP.x, yr = pAffineTP.y;\n"
            + "\n"
            + "  if (reciprocalpow != 0) {\n"
            + "   Complex z = new Complex(pAffineTP.x * zx_mult + zx_add, pAffineTP.y * zy_mult + zy_add);\n"
            + "\n"
            + "   z.Recip();\n"
            + "\n"
            + "   z.Scale(pAmount);\n"
            + "\n"
            + "   xr = reciprocalpow * z.re;\n"
            + "   yr = reciprocalpow * z.im;\n"
            + "  }\n"
            + "\n"
            + "  if (dividepow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "   Complex z2 = new Complex(xr, yr);\n"
            + "\n"
            + "   z2.Dec();\n"
            + "   z.Inc();\n"
            + "   z.Div(z2);\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   xr = dividepow * z.re;\n"
            + "   yr = dividepow * z.im;\n"
            + "  }\n"
            + "\n"
            + "  if (sqrtpow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.Sqrt();\n"
            + "\n"
            + "   z.Scale(pAmount);\n"
            + "\n"
            + "   if (pContext.random() < 0.5) {\n"
            + "    xr = sqrtpow * z.re;\n"
            + "    yr = sqrtpow * z.im;\n"
            + "   } else {\n"
            + "    xr = sqrtpow * -z.re;\n"
            + "    yr = sqrtpow * -z.im;\n"
            + "   }\n"
            + "  }\n"
            + "\n"
            + "  if (asinhpow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.AsinH();\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   xx += asinhpow * z.re;\n"
            + "   yy += asinhpow * z.im;\n"
            + "\n"
            + "  }\n"
            + "\n"
            + "  if (acoshpow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.AcosH();\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   if (pContext.random() < 0.5) {\n"
            + "    xx += acoshpow * z.re;\n"
            + "    yy += acoshpow * z.im;\n"
            + "   } else {\n"
            + "    xx += acoshpow * -z.re;\n"
            + "    yy += acoshpow * -z.im;\n"
            + "   }\n"
            + "  }\n"
            + "\n"
            + "  if (atanhpow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.AtanH();\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   xx += atanhpow * z.re;\n"
            + "   yy += atanhpow * z.im;\n"
            + "\n"
            + "  }\n"
            + "\n"
            + "  if (asechpow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.AsecH();\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   xx += asechpow * z.re;\n"
            + "   yy += asechpow * z.im;\n"
            + "\n"
            + "  }\n"
            + "\n"
            + "  if (acosechpow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.AcosecH();\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   if (pContext.random() < 0.5) {\n"
            + "    xx += acosechpow * z.re;\n"
            + "    yy += acosechpow * z.im;\n"
            + "   } else {\n"
            + "    xx += acosechpow * -z.re;\n"
            + "    yy += acosechpow * -z.im;\n"
            + "   }\n"
            + "  }\n"
            + "\n"
            + "  if (acothpow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.AcotH();\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   xx += acothpow * z.re;\n"
            + "   yy += acothpow * z.im;\n"
            + "\n"
            + "  }\n"
            + "\n"
            + "  if (logpow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.Log();\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   xx += logpow * z.re;\n"
            + "   yy += logpow * z.im;\n"
            + "\n"
            + "  }\n"
            + "\n"
            + "  if (exppow != 0) {\n"
            + "   Complex z = new Complex(xr * zx_mult + zx_add, yr * zy_mult + zy_add);\n"
            + "\n"
            + "   z.Exp();\n"
            + "\n"
            + "   z.Scale(pAmount * M_2_PI);\n"
            + "\n"
            + "   xx += exppow * z.re;\n"
            + "   yy += exppow * z.im;\n"
            + "\n"
            + "  }\n"
            + "\n"
            + "  if (asinhpow + acoshpow + atanhpow + asechpow + acosechpow + acothpow + logpow + exppow == 0) {\n"
            + "   pAffineTP.x = xr;\n"
            + "   pAffineTP.y = yr;\n"
            + "  }\n"
            + "\n"
            + "  else {\n"
            + "   pAffineTP.x = xx;\n"
            + "   pAffineTP.y = yy;\n"
            + "  }\n"
            + "\n"
            + "  if (pContext.isPreserveZCoordinate()) {\n"
            + "   pVarTP.z += pAmount * pAffineTP.z;\n"
            + "  }\n";

    System.err.println(convertCode(code, "pre_recip", new String[]{"reciprocalpow", "dividepow", "sqrtpow", "asinhpow", "acoshpow", "atanhpow", "asechpow", "acosechpow",
            "acothpow", "logpow", "exppow", "zx_mult", "zy_mult", "zx_add, zy_add"}));
  }

  private String convertCode(String code, String varName, String paramNames[]) {
    // functions
    String newCode = code.replaceAll("(atan2|asin|sin|acos|lerp|cos|fabs|log|pow|sqrt|sqr|sgn|exp|fmod|sinh|round|tan|cosh|hypot|rint|trunc|floor)\\(", "$1f(")
            // pAffineTP.getPrecalcSumsq()
            .replaceAll("pAffineTP\\.getPrecalcSumsq\\(\\)", "__r2")
            // pAffineTP.getPrecalcAtan()
            .replaceAll("pAffineTP\\.getPrecalcAtanf\\(\\)", "__phi")
            // pAffineTP.getPrecalcAtanYX()
            .replaceAll("pAffineTP\\.getPrecalcAtanYX\\(\\)", "__theta")
            // pAffineTP.getPrecalcSqrt()
            .replaceAll("pAffineTP\\.getPrecalcSqrt\\(\\)", "__r")
            // pVarTP.color
            .replaceAll("pVarTP\\.color", "__pal")
            // pAffineTP.color
            .replaceAll("pAffineTP\\.color", "__pal")
            // pVarTP.doHide
            .replaceAll("pVarTP\\.doHide", "__doHide")
            // pAffineTP
            .replaceAll("pAffineTP\\.", "__")
            // pVarTP
            .replaceAll("pVarTP\\.", "__p")
            // (double)
            .replaceAll("\\(double\\)", "(float)")
            // double
            .replaceAll("(\\s*)double(\\s+)", "$1float$2")
            // return
            .replaceAll("(\\s+)return([\\s;]+)", "$1!!!RETURN!!!$2")
            // pAmount
            .replaceAll("pAmount", "__"+varName)
            // SMALL_EPSILON
            .replaceAll("SMALL_EPSILON", "1.e-6f")
            // EPSILON
            .replaceAll("EPSILON", "1.e-6f")
            // M_1_2PI
            .replaceAll("M_1_2PI", "(1.0f / (PI + PI))")
            // M_2_PI
            .replaceAll("M_2_PI", "(2.0f / PI)")
            // M_1_PI
            .replaceAll("M_1_PI", "(1.0f / PI)")
            // M_PI_2
            .replaceAll("M_PI_2", "(PI*0.5f)")
            // M_PI_4
            .replaceAll("M_PI_4", "(0.25f*PI)")
            // M_PI
            .replaceAll("M_PI", "PI")
            // M_2PI
            .replaceAll("M_2PI", "(2.0f*PI)")
            // resolve sqr()
            .replaceAll("([\\s\\(]+)sqrf\\((\\w*)\\)", "$1$2*$2")
            // resolve sgn()
            .replaceAll("([\\s\\(]+)sgnf\\((\\w*)\\)", "$1($2<0.f ? -1 : $2>0.f ? 1 : 0)")
            // pContext.random()
            .replaceAll("pContext\\.random\\(\\)", "RANDFLOAT()")
            // boolean -> short
            .replaceAll("boolean", "short")
            // comments
            .replaceAll("(//.*)", "")
            // this.
            .replaceAll("this\\.", "");

    for(String paramName: paramNames) {
      newCode = newCode.replaceAll("([\\s\\(\\-\\+\\*]+)("+paramName+")([\\s\\+\\-\\*\\);]+)", "$1__"+varName+"_"+paramName+"$3");
    }
    return newCode;
  }
}
