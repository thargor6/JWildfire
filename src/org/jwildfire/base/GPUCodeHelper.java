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
        "    double width2 = width * pAmount;\n"
            + "    double x = pAffineTP.x / width;\n"
            + "    double aux = 0;\n"
            + "    if (x > 0.0) {\n"
            + "      aux = x - (int) x;\n"
            + "    } else {\n"
            + "      aux = x + (int) x;\n"
            + "    }\n"
            + "    aux = cos(aux * M_PI);\n"
            + "    double aux2 = 0;\n"
            + "    if (aux < pContext.random() * 2.0 - 1.0) {\n"
            + "      if (x > 0) {\n"
            + "        aux2 = -width2;\n"
            + "      } else {\n"
            + "        aux2 = width2;\n"
            + "      }\n"
            + "    }\n"
            + "\n"
            + "    pVarTP.x += pAffineTP.x * pAmount + aux2;\n"
            + "    pVarTP.y += pAmount * pAffineTP.y;\n"
            + "    if (pContext.isPreserveZCoordinate()) {\n"
            + "      pVarTP.z += pAmount * pAffineTP.z;\n"
            + "    }\n";
    System.err.println(convertCode(code, "tile_hlp", new String[]{"width"}));
  }

  private String convertCode(String code, String varName, String paramNames[]) {
    // functions
    String newCode = code.replaceAll("(atan2|asin|sin|acos|lerp|cos|fabs|log|pow|sqrt|sqr|sgn|exp|fmod|sinh|round|tan|cosh|hypot|rint|trunc|floor)\\(", "$1f(")
            // pAffineTP.getPrecalcSumsq()
            .replaceAll("pAffineTP\\.getPrecalcSumsq\\(\\)", "__r2")
            // pAffineTP.getPrecalcAtan()
            .replaceAll("pAffineTP\\.getPrecalcAtan\\(\\)", "__phi")
            // pAffineTP.getPrecalcAtanYX()
            .replaceAll("pAffineTP\\.getPrecalcAtanYX\\(\\)", "__theta")
            // pAffineTP.getPrecalcSqrt()
            .replaceAll("pAffineTP\\.getPrecalcSqrt\\(\\)", "__r")
            // pVarTP.color
            .replaceAll("pVarTP\\.color", "__pal")
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
            .replaceAll("pAmount", "varpar->"+varName)
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
            // this.
            .replaceAll("this\\.", "");

    for(String paramName: paramNames) {
      newCode = newCode.replaceAll("([\\s\\(\\-\\+\\*]+)("+paramName+")([\\s\\+\\-\\*\\);]+)", "$1varpar->"+varName+"_"+paramName+"$3");
    }
    return newCode;
  }
}
