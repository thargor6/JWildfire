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

import java.util.ArrayList;
import java.util.List;

import static org.jwildfire.create.tina.variation.VariationFuncType.*;

public class GPUCodeHelper {

  public static void main(String args[]) {
    new GPUCodeHelper().run();
  }

  private void run() {
    String code =
        "    /* hexnix3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */\n" +
                "    if (_fcycle > 5) {// Resets the cyclic counting\n" +
                "      _fcycle = 0;\n" +
                "      _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses new 6 or 3 nodes\n" +
                "    }\n" +
                "    if (_bcycle > 2) {\n" +
                "      _bcycle = 0;\n" +
                "      _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses new 6 or 3 nodes\n" +
                "    }\n" +
                "\n" +
                "    double lrmaj = pAmount; // Sets hexagon length radius - major plane\n" +
                "    double smooth = 1.0;\n" +
                "    double smRotxTP = 0.0;\n" +
                "    double smRotyTP = 0.0;\n" +
                "    double smRotxFT = 0.0;\n" +
                "    double smRotyFT = 0.0;\n" +
                "    double gentleZ = 0.0;\n" +
                "\n" +
                "    if (fabs(pAmount) <= 0.5) {\n" +
                "      smooth = pAmount * 2.0;\n" +
                "    } else {\n" +
                "      smooth = 1.0;\n" +
                "    }\n" +
                "    double boost = 0.0; //  Boost is the separation distance between the two planes\n" +
                "    int posNeg = 1;\n" +
                "    int loc60;\n" +
                "    int loc120;\n" +
                "    double scale = this.scale;\n" +
                "    double scale3 = this._3side;\n" +
                "\n" +
                "    if (pContext.random() < 0.5) {\n" +
                "      posNeg = -1;\n" +
                "    }\n" +
                "\n" +
                "    // Determine whether one or two major planes\n" +
                "    int majplane = 0;\n" +
                "    double abmajp = fabs(this.majp);\n" +
                "    if (abmajp <= 1.0) {\n" +
                "      majplane = 0; // 0= 1 plate active  1= transition to two plates active  2= defines two plates\n" +
                "      boost = 0.0;\n" +
                "    } else if (abmajp > 1.0 && abmajp < 2.0) {\n" +
                "      majplane = 1;\n" +
                "      boost = 0.0;\n" +
                "    } else {\n" +
                "      majplane = 2;\n" +
                "      boost = (abmajp - 2.0) * 0.5; // distance above and below XY plane\n" +
                "    }\n" +
                "\n" +
                "    //      Creating Z factors relative to the planes \n" +
                "    if (majplane == 0) {\n" +
                "      pVarTP.z += smooth * pAffineTP.z * scale * this.zlift; // single plate instructions\n" +
                "    } else if (majplane == 1 && this.majp < 0.0) {// Transition for reversing plates  because  majp is negative value\n" +
                "      if (this.majp < -1.0 && this.majp >= -2.0) {\n" +
                "        gentleZ = (abmajp - 1.0); //  Set transition smoothing values  0.00001 to 1.0    \n" +
                "      } else {\n" +
                "        gentleZ = 1.0; // full effect explicit default value\n" +
                "      }\n" +
                "      // Begin reverse transition - starts with pVarTP.z==pVarTP.z proceeds by gradual negative\n" +
                "      if (posNeg < 0) {\n" +
                "        pVarTP.z += -2.0 * (pVarTP.z * gentleZ); // gradually grows negative plate, in place, no boost,\n" +
                "      }\n" +
                "    }\n" +
                "    if (majplane == 2 && this.majp < 0.0) {// Begin the splitting operation, animation transition is done\n" +
                "      if (posNeg > 0) {//  The splitting operation positive side\n" +
                "        pVarTP.z += (smooth * (pAffineTP.z * scale * this.zlift + boost));\n" +
                "      } else {//  The splitting operation negative side\n" +
                "        pVarTP.z = (pVarTP.z - (2.0 * smooth * pVarTP.z)) + (smooth * posNeg * (pAffineTP.z * scale * this.zlift + boost));\n" +
                "      }\n" +
                "    } else {//  majp > 0.0       The splitting operation\n" +
                "      pVarTP.z += smooth * (pAffineTP.z * scale * this.zlift + (posNeg * boost));\n" +
                "    }\n" +
                "\n" +
                "    if (this._rswtch <= 1) {//  Occasion to build using 60 degree segments    \n" +
                "      loc60 = (int) trunc(pContext.random() * 6.0); // random nodes selection\n" +
                "      //loc60 = this.fcycle;   // sequential nodes selection - seems to create artifacts that are progressively displaced\n" +
                "      smRotxTP = (smooth * scale * pVarTP.x * _seg60x[loc60]) - (smooth * scale * pVarTP.y * _seg60y[loc60]);\n" +
                "      smRotyTP = (smooth * scale * pVarTP.y * _seg60x[loc60]) + (smooth * scale * pVarTP.x * _seg60y[loc60]);\n" +
                "      smRotxFT = (pAffineTP.x * smooth * scale * _seg60x[loc60]) - (pAffineTP.y * smooth * scale * _seg60y[loc60]);\n" +
                "      smRotyFT = (pAffineTP.y * smooth * scale * _seg60x[loc60]) + (pAffineTP.x * smooth * scale * _seg60y[loc60]);\n" +
                "\n" +
                "      pVarTP.x = pVarTP.x * (1.0 - smooth) + smRotxTP + smRotxFT + smooth * lrmaj * _seg60x[loc60];\n" +
                "      pVarTP.y = pVarTP.y * (1.0 - smooth) + smRotyTP + smRotyFT + smooth * lrmaj * _seg60y[loc60];\n" +
                "\n" +
                "      this._fcycle += 1;\n" +
                "    } else { // Occasion to build on 120 degree segments\n" +
                "      loc120 = (int) trunc(pContext.random() * 3.0); // random nodes selection\n" +
                "      //loc120 = this.bcycle;  // sequential nodes selection - seems to create artifacts that are progressively displaced\n" +
                "      smRotxTP = (smooth * scale * pVarTP.x * _seg120x[loc120]) - (smooth * scale * pVarTP.y * _seg120y[loc120]);\n" +
                "      smRotyTP = (smooth * scale * pVarTP.y * _seg120x[loc120]) + (smooth * scale * pVarTP.x * _seg120y[loc120]);\n" +
                "      smRotxFT = (pAffineTP.x * smooth * scale * _seg120x[loc120]) - (pAffineTP.y * smooth * scale * _seg120y[loc120]);\n" +
                "      smRotyFT = (pAffineTP.y * smooth * scale * _seg120x[loc120]) + (pAffineTP.x * smooth * scale * _seg120y[loc120]);\n" +
                "\n" +
                "      pVarTP.x = pVarTP.x * (1.0 - smooth) + smRotxTP + smRotxFT + smooth * lrmaj * scale3 * _seg120x[loc120];\n" +
                "      pVarTP.y = pVarTP.y * (1.0 - smooth) + smRotyTP + smRotyFT + smooth * lrmaj * scale3 * _seg120y[loc120];\n" +
                "\n" +
                "      this._bcycle += 1;\n" +
                "    }\n";

//     System.err.println(convertCodeSwan(code, "mobius_dragon_3D", new String[]{}));
     //System.err.println(convertCode(code, "mobius_dragon_3D", new String[]{}));
    //FlameTransformationContext ctx = new FlameTransformationContext(null, null, 1, 1);
    //System.err.println(new ParPlot2DWFFunc().getGPUCode(ctx));
    System.err.println(createSwanVariation(new Hexnix3DFunc(), code));
  }

  private String createSwanVariation(VariationFunc variationFunc, String code) {
    StringBuilder sb = new StringBuilder();
    addIndented(0, sb, "### " + variationFunc.getClass().getSimpleName() + "\n");
    addIndented(0, sb, "class " + variationFunc.getClass().getSimpleName() + " extends VariationFuncWaves:\n");
    // params
    if(variationFunc.getParameterNames().length > 0) {
      for (String paramName : variationFunc.getParameterNames()) {
        addIndented(1, sb, "const PARAM_" + paramNameToSwan(paramName) + " = \"" + paramName + "\"\n");
      }
      addIndented(0, sb, "\n");
      addIndented(1, sb, "var __params: Array[VariationParam] = [\n");
      for(int idx = 0; idx < variationFunc.getParameterNames().length; idx++) {
        String paramName = paramNameToSwan(variationFunc.getParameterNames()[idx]);
        Object paramValue = variationFunc.getParameterValues()[idx];
        if(paramValue instanceof Double) {
          addIndented(2, sb, "VariationParam.new(PARAM_" + paramName + ", FlameParameter.DataType.FLOAT, " + paramValue + ")");
        }
        else if(paramValue instanceof Integer) {
          addIndented(2, sb, "VariationParam.new(PARAM_" + paramName + ", FlameParameter.DataType.INT, " + paramValue + ")");
        }
        else {
          throw new IllegalArgumentException("Unsupported parameter type: " + paramValue.getClass().getName());
        }
        if(idx < variationFunc.getParameterNames().length - 1) {
          sb.append(",\n");
        }
        else {
          sb.append("\n");
        }
      }
      addIndented(2, sb, "]\n");

      addIndented(0, sb, "\n");
      addIndented(1, sb, "func get_params() -> Array[VariationParam]:\n");
      addIndented(2, sb, "return __params\n");
      addIndented(0, sb, "\n");
    }
    // shader code
    addIndented(1, sb, "func get_shader_code(ctx: RenderFlamePrepareContext,_xform: RenderXForm, variation: RenderVariation) -> String:\n");
    addIndented(2, sb, "return \"\"\"{\n");
    addIndented(1, sb, "float amount = %s;\n");
    for(int idx = 0; idx < variationFunc.getParameterNames().length; idx++) {
      String paramName = variationFunc.getParameterNames()[idx];
      Object paramValue = variationFunc.getParameterValues()[idx];
      if(paramValue instanceof Double) {
        addIndented(1, sb, "float " + variationFunc.getName() +  "_" + paramName + " = %s;\n");
      }
      else if(paramValue instanceof Integer) {
        addIndented(1, sb, "int " + variationFunc.getName() +  "_" + paramName + " = %s;\n");
      }
    }

    addIndented(2, sb, convertCodeSwan( code, variationFunc.getName(), variationFunc.getParameterNames()));

    if(variationFunc.getParameterNames().length > 0) {
      addIndented(2, sb, "}\"\"\" % [variation.amount.to_glsl(ctx), \n");
      for(int idx = 0; idx < variationFunc.getParameterNames().length; idx++) {
        String paramName = paramNameToSwan(variationFunc.getParameterNames()[idx]);
        addIndented(3, sb, "variation.params[PARAM_" + paramName + "].to_glsl(ctx)");
        if(idx < variationFunc.getParameterNames().length - 1) {
          sb.append(",\n");
        }
        else {
          sb.append("]\n");
        }
      }
    }
    else {
      addIndented(2, sb, "}\"\"\" % variation.amount.to_glsl(ctx)\n");
    }
    addIndented(0, sb, "\n");

    // name etc
    addIndented(1, sb, "func get_name() -> String:\n");
    addIndented(2, sb, "return '" + variationFunc.getName() + "'\n");
    addIndented(0, sb, "\n");

    addIndented(1, sb, "func get_variation_types() -> Array[Variation.VariationType]:\n");
    addIndented(2, sb, "return [" + variationTypesToSwan(variationFunc.getVariationTypes()) +  "]\n");
    addIndented(0, sb, "\n");

    return sb.toString();
  }

  private String variationTypesToSwan(VariationFuncType[] variationTypes) {
    List<String> res = new ArrayList<>();
    for(VariationFuncType variationType: variationTypes) {
      switch(variationType) {
        case VARTYPE_2D:
          res.add("Variation.VariationType.VARTYPE_2D");
          break;
        case VARTYPE_3D:
          res.add("Variation.VariationType.VARTYPE_3D");
          break;
        case VARTYPE_CROP:
          res.add("Variation.VariationType.VARTYPE_CROP");
          break;
        case VARTYPE_BLUR:
          res.add("Variation.VariationType.VARTYPE_BLUR");
          break;
        case VARTYPE_ZTRANSFORM:
          res.add("Variation.VariationType.VARTYPE_ZTRANSFORM");
          break;
        case VARTYPE_DC:
          res.add("Variation.VariationType.VARTYPE_DC");
          break;
        case VARTYPE_SIMULATION:
          res.add("Variation.VariationType.VARTYPE_SIMULATION");
          break;
        case VARTYPE_POST:
          res.add("Variation.VariationType.VARTYPE_POST");
          break;
        case VARTYPE_PRE:
          res.add("Variation.VariationType.VARTYPE_PRE");
          break;
        case VARTYPE_BASE_SHAPE:
          res.add("Variation.VariationType.VARTYPE_BASE_SHAPE");
          break;
        case VARTYPE_EDIT_FORMULA:
          res.add("Variation.VariationType.VARTYPE_EDIT_FORMULA");
          break;
        case VARTYPE_SUPPORTS_EXTERNAL_SHAPES:
          res.add("Variation.VariationType.VARTYPE_SUPPORTS_EXTERNAL_SHAPES");
          break;
        case VARTYPE_ESCAPE_TIME_FRACTAL:
          res.add("Variation.VariationType.VARTYPE_ESCAPE_TIME_FRACTAL");
          break;
        case VARTYPE_SUPPORTS_BACKGROUND:
          res.add("Variation.VariationType.VARTYPE_SUPPORTS_BACKGROUND");
          break;
        case VARTYPE_PREPOST:
          res.add("Variation.VariationType.VARTYPE_PREPOST");
          break;
      }
    }

    return String.join(", ", res);
  }

  private String paramNameToSwan(String paramName) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < paramName.length(); i++) {
      char c = paramName.charAt(i);
      if (Character.isUpperCase(c)) {
        sb.append("_");
        sb.append(Character.toUpperCase(c));
      } else {
        sb.append(Character.toUpperCase(c));
      }
    }
    return sb.toString();
  }

  private void addIndented(int indent, StringBuilder sb, String s) {
    for (int i = 0; i < indent; i++) {
      sb.append("\t");
    }
    sb.append(s);
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
            .replaceAll("pContext\\.random\\(\\)", "rand8(uv, rngState)")
            // boolean -> short
            .replaceAll("boolean", "short")
            // comments
            .replaceAll("(//.*)", "")
            // this.
            .replaceAll("this\\.", "");

    for(String paramName: paramNames) {
      newCode = newCode.replaceAll("([\\s\\(\\-\\+\\*]+)("+paramName+")([\\s\\+\\-\\*\\);]+)", "$1"+varName+"_"+paramName+"$3");
    }
    return newCode;
  }

}
