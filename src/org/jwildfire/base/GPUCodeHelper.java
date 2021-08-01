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
        " double t = pAffineTP.getPrecalcAtan()-M_PI_2;\n"
            + "    double r = (filled > 0 && filled > pContext.random()) ? pAmount * pContext.random() : pAmount;\n"
            + "\n"
            + "    // maple leaf formula by Hamid Naderi Yeganeh: https://blogs.scientificamerican.com/guest-blog/how-to-draw-with-math/\n"
            + "    double lx = (1.0+pow((cos(6.0*t)),2.0)+1.0/5.0*pow((cos(6.0*t)*cos(24.0*t)),10.0)+(1/4*pow((cos(30.0*t)),2)+1.0/9.0*pow((cos(30.0*t)),12.0))*(1.0-pow((sin(6.0*t)),10.0)))*(sin(2.0*t))*(1.0-pow((cos(t)),4.0))*(1.0-pow((cos(t)),10.0)*pow((cos(3.0*t)),2.0));\n"
            + "    double ly = 21.0/20.0*cos(2.0*t)*(1.0-pow((cos(t)),4.0)+1.0/2.0*pow((cos(t)*cos(3.0*t)),10.0))*(1.0+pow((cos(6.0*t)),2.0)+1.0/5.0*pow((cos(6.0*t)*cos(18.0*t)),10.0)+(1.0/4.0*pow((cos(30.0*t)),4)+1.0/10.0*pow((cos(30.0*t)),12.0))*(1.0-pow((cos(t)),10.0)*pow((cos(3.0*t)),2.0))*(1.0-pow((sin(6.0*t)),10.0)));\n"
            + "\n"
            + "    pVarTP.x += r * lx;\n"
            + "    pVarTP.y += r * ly;\n"
            + "\n"
            + "    if (pContext.isPreserveZCoordinate()) {\n"
            + "      pVarTP.z += pAmount * pAffineTP.z;\n"
            + "    }\n";

    System.err.println(convertCode(code, "maple_leaf", new String[]{"filled"}));
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
