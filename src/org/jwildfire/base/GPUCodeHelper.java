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
        "  int _rswtch; //  for choosing between 6 or 3 segments to a plane\n"
            + "  int _fcycle; //  markers to count cycles... \n"
            + "  int _bcycle;\n\n"
            + " _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses 6 or 3 nodes\n"
            + "    double hlift = sin(M_PI / 3.0); // sin(60)\n"
            + "    _fcycle = 0;\n"
            + "    _bcycle = 0;\n"
            + "    _seg60x[0] = 1.0;\n"
            + "    _seg60x[1] = 0.5;\n"
            + "    _seg60x[2] = -0.5;\n"
            + "    _seg60x[3] = -1.0;\n"
            + "    _seg60x[4] = -0.5;\n"
            + "    _seg60x[5] = 0.5;\n"
            + "\n"
            + "    _seg60y[0] = 0.0;\n"
            + "    _seg60y[1] = -hlift;\n"
            + "    _seg60y[2] = -hlift;\n"
            + "    _seg60y[3] = 0.0;\n"
            + "    _seg60y[4] = hlift;\n"
            + "    _seg60y[5] = hlift;\n"
            + "\n"
            + "    _seg120x[0] = 0.0; // These settings cause the 3-node setting to \n"
            + "    _seg120x[1] = cos(7.0 * M_PI / 6.0); // rotate 30 degrees relative to the hex structure.\n"
            + "    _seg120x[2] = cos(11.0 * M_PI / 6.0); // \n"
            + "\n"
            + "    _seg120y[0] = -1.0;\n"
            + "    _seg120y[1] = 0.5;\n"
            + "    _seg120y[2] = 0.5;\n"
            + "    if (_fcycle > 5) {// Resets the cyclic counting\n"
            + "      _fcycle = 0;\n"
            + "      _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses new 6 or 3 nodes\n"
            + "    }\n"
            + "    if (_bcycle > 2) {\n"
            + "      _bcycle = 0;\n"
            + "      _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses new 6 or 3 nodes\n"
            + "    }\n"
            + "\n"
            + "    double lrmaj = pAmount; // Sets hexagon length radius - major plane\n"
            + "    double smooth = 1.0;\n"
            + "    double smRotxTP = 0.0;\n"
            + "    double smRotyTP = 0.0;\n"
            + "    double smRotxFT = 0.0;\n"
            + "    double smRotyFT = 0.0;\n"
            + "    double gentleZ = 0.0;\n"
            + "\n"
            + "    if (fabs(pAmount) <= 0.5) {\n"
            + "      smooth = pAmount * 2.0;\n"
            + "    } else {\n"
            + "      smooth = 1.0;\n"
            + "    }\n"
            + "    double boost = 0.0; //  Boost is the separation distance between the two planes\n"
            + "    int posNeg = 1;\n"
            + "    int loc60;\n"
            + "    int loc120;\n"
            + "    double scale = this.scale;\n"
            + "    double scale3 = this._3side;\n"
            + "\n"
            + "    if (pContext.random() < 0.5) {\n"
            + "      posNeg = -1;\n"
            + "    }\n"
            + "\n"
            + "    // Determine whether one or two major planes\n"
            + "    int majplane = 0;\n"
            + "    double abmajp = fabs(this.majp);\n"
            + "    if (abmajp <= 1.0) {\n"
            + "      majplane = 0; // 0= 1 plate active  1= transition to two plates active  2= defines two plates\n"
            + "      boost = 0.0;\n"
            + "    } else if (abmajp > 1.0 && abmajp < 2.0) {\n"
            + "      majplane = 1;\n"
            + "      boost = 0.0;\n"
            + "    } else {\n"
            + "      majplane = 2;\n"
            + "      boost = (abmajp - 2.0) * 0.5; // distance above and below XY plane\n"
            + "    }\n"
            + "\n"
            + "    //      Creating Z factors relative to the planes \n"
            + "    if (majplane == 0) {\n"
            + "      pVarTP.z += smooth * pAffineTP.z * scale * this.zlift; // single plate instructions\n"
            + "    } else if (majplane == 1 && this.majp < 0.0) {// Transition for reversing plates  because  majp is negative value\n"
            + "      if (this.majp < -1.0 && this.majp >= -2.0) {\n"
            + "        gentleZ = (abmajp - 1.0); //  Set transition smoothing values  0.00001 to 1.0    \n"
            + "      } else {\n"
            + "        gentleZ = 1.0; // full effect explicit default value\n"
            + "      }\n"
            + "      // Begin reverse transition - starts with pVarTP.z==pVarTP.z proceeds by gradual negative\n"
            + "      if (posNeg < 0) {\n"
            + "        pVarTP.z += -2.0 * (pVarTP.z * gentleZ); // gradually grows negative plate, in place, no boost,\n"
            + "      }\n"
            + "    }\n"
            + "    if (majplane == 2 && this.majp < 0.0) {// Begin the splitting operation, animation transition is done\n"
            + "      if (posNeg > 0) {//  The splitting operation positive side\n"
            + "        pVarTP.z += (smooth * (pAffineTP.z * scale * this.zlift + boost));\n"
            + "      } else {//  The splitting operation negative side\n"
            + "        pVarTP.z = (pVarTP.z - (2.0 * smooth * pVarTP.z)) + (smooth * posNeg * (pAffineTP.z * scale * this.zlift + boost));\n"
            + "      }\n"
            + "    } else {//  majp > 0.0       The splitting operation\n"
            + "      pVarTP.z += smooth * (pAffineTP.z * scale * this.zlift + (posNeg * boost));\n"
            + "    }\n"
            + "\n"
            + "    if (this._rswtch <= 1) {//  Occasion to build using 60 degree segments    \n"
            + "      loc60 = (int) trunc(pContext.random() * 6.0); // random nodes selection\n"
            + "      //loc60 = this.fcycle;   // sequential nodes selection - seems to create artifacts that are progressively displaced\n"
            + "      smRotxTP = (smooth * scale * pVarTP.x * _seg60x[loc60]) - (smooth * scale * pVarTP.y * _seg60y[loc60]);\n"
            + "      smRotyTP = (smooth * scale * pVarTP.y * _seg60x[loc60]) + (smooth * scale * pVarTP.x * _seg60y[loc60]);\n"
            + "      smRotxFT = (pAffineTP.x * smooth * scale * _seg60x[loc60]) - (pAffineTP.y * smooth * scale * _seg60y[loc60]);\n"
            + "      smRotyFT = (pAffineTP.y * smooth * scale * _seg60x[loc60]) + (pAffineTP.x * smooth * scale * _seg60y[loc60]);\n"
            + "\n"
            + "      pVarTP.x = pVarTP.x * (1.0 - smooth) + smRotxTP + smRotxFT + smooth * lrmaj * _seg60x[loc60];\n"
            + "      pVarTP.y = pVarTP.y * (1.0 - smooth) + smRotyTP + smRotyFT + smooth * lrmaj * _seg60y[loc60];\n"
            + "\n"
            + "      this._fcycle += 1;\n"
            + "    } else { // Occasion to build on 120 degree segments\n"
            + "      loc120 = (int) trunc(pContext.random() * 3.0); // random nodes selection\n"
            + "      //loc120 = this.bcycle;  // sequential nodes selection - seems to create artifacts that are progressively displaced\n"
            + "      smRotxTP = (smooth * scale * pVarTP.x * _seg120x[loc120]) - (smooth * scale * pVarTP.y * _seg120y[loc120]);\n"
            + "      smRotyTP = (smooth * scale * pVarTP.y * _seg120x[loc120]) + (smooth * scale * pVarTP.x * _seg120y[loc120]);\n"
            + "      smRotxFT = (pAffineTP.x * smooth * scale * _seg120x[loc120]) - (pAffineTP.y * smooth * scale * _seg120y[loc120]);\n"
            + "      smRotyFT = (pAffineTP.y * smooth * scale * _seg120x[loc120]) + (pAffineTP.x * smooth * scale * _seg120y[loc120]);\n"
            + "\n"
            + "      pVarTP.x = pVarTP.x * (1.0 - smooth) + smRotxTP + smRotxFT + smooth * lrmaj * scale3 * _seg120x[loc120];\n"
            + "      pVarTP.y = pVarTP.y * (1.0 - smooth) + smRotyTP + smRotyFT + smooth * lrmaj * scale3 * _seg120y[loc120];\n"
            + "\n"
            + "      this._bcycle += 1;\n"
            + "    }\n";

    System.err.println(convertCode(code, "hexnix3D", new String[]{"majp", "scale", "zlift", "3side"}));
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
            // comments
            .replaceAll("(//.*)", "")
            // this.
            .replaceAll("this\\.", "");

    for(String paramName: paramNames) {
      newCode = newCode.replaceAll("([\\s\\(\\-\\+\\*]+)("+paramName+")([\\s\\+\\-\\*\\);]+)", "$1varpar->"+varName+"_"+paramName+"$3");
    }
    return newCode;
  }
}
