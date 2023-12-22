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
        " double zc = pVarTP.color;\n" +
                "    double random = pContext.random();\n" +
                "    double x1 = pAffineTP.x;\n" +
                "    double y1 = pAffineTP.y;\n" +
                "    double z1 = pAffineTP.z;\n" +
                "    double spreadx = -x_spread;\n" +
                "    double spready = -y_spread;\n" +
                "    double spreadz = -z_spread;\n" +
                "    Complex z = new Complex(pAffineTP.x, pAffineTP.y + 1.0);\n" +
                "    Complex z2 = new Complex(re, im);\n" +
                "\n" +
                "    for (int i = 0; i < iterations; i++){\n" +
                "      z.re = pAffineTP.x;\n" +
                "      z.im = pAffineTP.y + 1.0;\n" +
                "      z.Div(z2);\n" +
                "\n" +
                "      // reciprocal of z for 3D\n" +
                "      double r1 = pAmount/(z.re*z.re+z.im*z.im+z1*z1);\n" +
                "      z.re = z.re * r1;\n" +
                "      z.im = -z.im * r1;\n" +
                "      pVarTP.z = -z1 * r1;\n" +
                "\n" +
                "      z.Scale(pAmount);\n" +
                "      pAffineTP.x = z.re;\n" +
                "      pAffineTP.y = z.im + 1.0;\n" +
                "\n" +
                "\n" +
                "      // line\n" +
                "      if (line_enable == 1){\n" +
                "        if ( random < line_weight){\n" +
                "          z.re = pContext.random() * spreadx;\n" +
                "          z.im = 0.0;\n" +
                "          pVarTP.z = 0.0;\n" +
                "          pVarTP.color += line_color_shift;\n" +
                "        }\n" +
                "      }\n" +
                "      // experimental iteration coloring that didn't work out but may add in the future:\n" +
                "      //zc += ((double)k*.1);\n" +
                "    }\n" +
                "\n" +
                "    // Log_tile by Zy0rg implemented into JWildfire by Brad Stefanov\n" +
                "    //converted to a post variation by Whittaker Courtney\n" +
                "\n" +
                "    if (pContext.random() < 0.5)\n" +
                "      spreadx = x_spread;\n" +
                "\n" +
                "    if (pContext.random() < 0.5)\n" +
                "      spready = y_spread;\n" +
                "\n" +
                "    if (pContext.random() < 0.5)\n" +
                "      spreadz = z_spread;\n" +
                "\n" +
                "    if(random < 0.5){\n" +
                "      z.re = pAmount * x_add + (z.re + spreadx * round(log(pContext.random())/log(log_spread)));\n" +
                "      z.im = pAmount * (y_add + (z.im + spready * round(log(pContext.random())/log(log_spread))))+1.0;\n" +
                "      pVarTP.z = pAmount * (pVarTP.z + spreadz * round(log(pContext.random())/log(log_spread)));\n" +
                "    }\n" +
                "    else{\n" +
                "      z.re = pAmount * (-z.re + spreadx * round(log(pContext.random())/log(log_spread)));\n" +
                "      z.im = pAmount * (-2 -(z.im + spready * round(log(pContext.random())/log(log_spread))))+1.0;\n" +
                "      pVarTP.z = pAmount * (-pVarTP.z + spreadz * round(log(pContext.random())/log(log_spread)));\n" +
                "    }\n" +
                "\n" +
                "// magnitude coloring:\n" +
                "    double mag = sqrt(z.re*z.re+z.im*z.im+z1*z1)*(mag_color_scale /6);\n" +
                "\n" +
                "    if (mag_color == 1){\n" +
                "      zc += tanh(mag);\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    pVarTP.x = z.re;\n" +
                "    pVarTP.y = z.im;\n" +
                "    pVarTP.color = zc;\n" +
                "\n" +
                "\n" +
                "\n" +
                "    if (pContext.isPreserveZCoordinate()) {\n" +
                "      pVarTP.z += pAmount * pAffineTP.z;\n" +
                "    }";

//     System.err.println(convertCodeSwan(code, "mobius_dragon_3D", new String[]{}));
     System.err.println(convertCode(code, "mobius_dragon_3D", new String[]{}));
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
