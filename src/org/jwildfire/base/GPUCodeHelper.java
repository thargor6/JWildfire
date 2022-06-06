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

import org.jwildfire.create.tina.variation.*;
import org.jwildfire.create.tina.variation.plot.ParPlot2DWFFunc;
import org.jwildfire.create.tina.variation.plot.PolarPlot2DWFFunc;
import org.jwildfire.create.tina.variation.plot.PolarPlot3DWFFunc;
import org.jwildfire.create.tina.variation.plot.YPlot2DWFFunc;
import org.jwildfire.create.tina.variation.plot.YPlot3DWFFunc;

public class GPUCodeHelper {

  public static void main(String args[]) {
    new GPUCodeHelper().run();
  }

  private void run() {
    String code =
        "    pVarTP.x += pAmount * tanh(pAffineTP.x) * (1.0 / cos(pAffineTP.x) + effect * M_PI);\n"
            + "    pVarTP.y += pAmount * tanh(pAffineTP.y) * (1.0 / cos(pAffineTP.y) + effect * M_PI);\n";

     System.err.println(convertCodeSwan(code, "polar", new String[]{}));
    //FlameTransformationContext ctx = new FlameTransformationContext(null, null, 1, 1);
    //System.err.println(new ParPlot2DWFFunc().getGPUCode(ctx));
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

  private String convertCodeSwan(String code, String varName, String paramNames[]) {
    // functions
    String newCode =
            // pAffineTP.getPrecalcSumsq()
            code.replaceAll("pAffineTP\\.getPrecalcSumsq\\(\\)", "_r2")
            // pAffineTP.getPrecalcAtan()
            .replaceAll("pAffineTP\\.getPrecalcAtan\\(\\)", "_phi")
            // pAffineTP.getPrecalcAtanYX()
            .replaceAll("pAffineTP\\.getPrecalcAtanYX\\(\\)", "_theta")
            // pAffineTP.getPrecalcSqrt()
            .replaceAll("pAffineTP\\.getPrecalcSqrt\\(\\)", "_r")
            // pVarTP.color
            .replaceAll("pVarTP\\.color", "_pal")
            // pAffineTP.color
            .replaceAll("pAffineTP\\.color", "_pal")
            // pVarTP.doHide
            .replaceAll("pVarTP\\.doHide", "_doHide")
            // pAffineTP
            .replaceAll("pAffineTP\\.", "_t")
            // pVarTP
            .replaceAll("pVarTP\\.", "_v")
            // (double)
            .replaceAll("\\(double\\)", "(float)")
            // double
            .replaceAll("(\\s*)double(\\s+)", "$1float$2")
            // return
            .replaceAll("(\\s+)return([\\s;]+)", "$1!!!RETURN!!!$2")
            // pAmount
            .replaceAll("pAmount", "amount")
            // SMALL_EPSILON
            .replaceAll("SMALL_EPSILON", "EPSILON")
            // M_1_2PI
            .replaceAll("M_1_2PI", "(1.0f / (M_PI + M_PI))")
            // M_2_PI
            .replaceAll("M_2_PI", "(2.0f / M_PI)")
            // M_1_PI
            .replaceAll("M_1_PI", "(1.0f / M_PI)")
            // M_PI_2
            .replaceAll("M_PI_2", "(M_PI*0.5f)")
            // M_PI_4
            .replaceAll("M_PI_4", "(0.25f*M_PI)")
            // M_2PI
            .replaceAll("M_2PI", "(2.0f*M_PI)")
            // resolve sqr()
            .replaceAll("([\\s\\(]+)sqrf\\((\\w*)\\)", "$1$2*$2")
            // resolve sgn()
            .replaceAll("([\\s\\(]+)sgnf\\((\\w*)\\)", "$1($2<0.f ? -1 : $2>0.f ? 1 : 0)")
            // pContext.random()
            .replaceAll("pContext\\.random\\(\\)", "rand8(tex, rngState)")
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
