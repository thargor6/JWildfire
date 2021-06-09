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
        "  double _f, _s, _p, _is, _pxa, _pixa, _vxp;\n"
            + "  boolean _fixed_dist_calc;\n"
            + "_f = frequency * 5;\n"
            + "    double a = amplitude * 0.01;\n"
            + "    _p = phase * M_2PI - M_PI;\n"
            + "\n"
            + "    // scale must not be zero\n"
            + "    _s = scale == 0 ? SMALL_EPSILON : scale;\n"
            + "\n"
            + "    // we will need the inverse scale\n"
            + "    _is = 1.0 / _s;\n"
            + "\n"
            + "    // pre-multiply velocity+phase, phase+amplitude and (PI-phase)+amplitude\n"
            + "    _vxp = velocity * _p;\n"
            + "    _pxa = _p * a;\n"
            + "    _pixa = (M_PI - _p) * a;\n"
            + "    _fixed_dist_calc = fixed_dist_calc != 0;\n"
            + "    double x = (pAffineTP.x * _s) - centerx, y = (pAffineTP.y * _s) + centery;\n"
            + "\n"
            + "    // calculate distance from center but constrain it to EPS\n"
            + "    double d = _fixed_dist_calc ? sqrt(x * x + y * y) : sqrt(x * x * y * y);\n"
            + "    if (d < SMALL_EPSILON)\n"
            + "      d = SMALL_EPSILON;\n"
            + "\n"
            + "    // normalize (x,y)\n"
            + "    double nx = x / d, ny = y / d;\n"
            + "\n"
            + "    // calculate cosine wave with given frequency, velocity \n"
            + "    // and phase based on the distance to center\n"
            + "    double wave = cos(_f * d - _vxp);\n"
            + "\n"
            + "    // calculate the wave offsets\n"
            + "    double d1 = wave * _pxa + d, d2 = wave * _pixa + d;\n"
            + "\n"
            + "    // we got two offsets, so we also got two new positions (u,v)\n"
            + "    double u1 = (centerx + nx * d1), v1 = (-centery + ny * d1);\n"
            + "    double u2 = (centerx + nx * d2), v2 = (-centery + ny * d2);\n"
            + "\n"
            + "    // interpolate the two positions by the given phase and\n"
            + "    // invert the multiplication with scale from before\n"
            + "    pVarTP.x = pAmount * (lerp(u1, u2, _p)) * _is;\n"
            + "    pVarTP.y = pAmount * (lerp(v1, v2, _p)) * _is;\n";
    System.err.println(convertCode(code, "ripple", new String[]{"frequency", "velocity", "amplitude", "centerx", "centery", "phase", "scale"}));
  }

  private String convertCode(String code, String varName, String paramNames[]) {
    // functions
    String newCode = code.replaceAll("(atan2|asin|sin|acos|lerp|cos|fabs|log|pow|sqrt|sqr|sgn|exp|fmod|sinh|cosh|hypot|rint|trunc|floor)\\(", "$1f(")
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
