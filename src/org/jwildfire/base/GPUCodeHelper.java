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
        "    // Mobiq by zephyrtronium converted to work in JWildfire by Brad Stefanov\n" +
                "\t/*  Qlib uses the notation T + X i + Y j + Z k, so I used the following while\n" +
                "    simplifying. I took a usual Mobius transform (ax + b) / (cx + d) and made\n" +
                "    a, b, c, and d quaternions instead of just complex numbers, with x being\n" +
                "    a quaternion FTx + FTy i + FTz j + 0 k. Multiplying quaternions happens to\n" +
                "    be a very complex thing, and dividing is no simpler, so I needed to use\n" +
                "    names that are easily and quickly typed while simplifying to prevent\n" +
                "    massive insanity/violence/genocide. I then found switching back from those\n" +
                "    names in my head to be rather difficult and confusing, so I decided to\n" +
                "    use these macros instead.\n" +
                "*/\n" +
                "\n" +
                "    double t1 = qat;\n" +
                "    double t2 = pAffineTP.x;\n" +
                "    double t3 = qbt;\n" +
                "    double t4 = qct;\n" +
                "    double t5 = qdt;\n" +
                "    double x1 = qax;\n" +
                "    double x2 = pAffineTP.y;\n" +
                "    double x3 = qbx;\n" +
                "    double x4 = qcx;\n" +
                "    double x5 = qdx;\n" +
                "    double y1 = qay;\n" +
                "    double y2 = pAffineTP.z;\n" +
                "    double y3 = qby;\n" +
                "    double y4 = qcy;\n" +
                "    double y5 = qdy;\n" +
                "    double z1 = qaz;\n" +
                "    /* z2 is 0 and simplified out (there is no fourth generated coordinate). */\n" +
                "    double z3 = qbz;\n" +
                "    double z4 = qcz;\n" +
                "    double z5 = qdz;\n" +
                "\n" +
                "    double nt = t1 * t2 - x1 * x2 - y1 * y2 + t3;\n" +
                "    double nx = t1 * x2 + x1 * t2 - z1 * y2 + x3;\n" +
                "    double ny = t1 * y2 + y1 * t2 + z1 * x2 + y3;\n" +
                "    double nz = z1 * t2 + x1 * y2 - y1 * x2 + z3;\n" +
                "    double dt = t4 * t2 - x4 * x2 - y4 * y2 + t5;\n" +
                "    double dx = t4 * x2 + x4 * t2 - z4 * y2 + x5;\n" +
                "    double dy = t4 * y2 + y4 * t2 + z4 * x2 + y5;\n" +
                "    double dz = z4 * t2 + x4 * y2 - y4 * x2 + z5;\n" +
                "    double ni = pAmount / (sqr(dt) + sqr(dx) + sqr(dy) + sqr(dz));\n" +
                "\n" +
                "\n" +
                "    pVarTP.x += (nt * dt + nx * dx + ny * dy + nz * dz) * ni;\n" +
                "    pVarTP.y += (nx * dt - nt * dx - ny * dz + nz * dy) * ni;\n" +
                "    pVarTP.z += (ny * dt - nt * dy - nz * dx + nx * dz) * ni;\n";

//     System.err.println(convertCodeSwan(code, "mobius_dragon_3D", new String[]{}));
     //System.err.println(convertCode(code, "mobius_dragon_3D", new String[]{}));
    //FlameTransformationContext ctx = new FlameTransformationContext(null, null, 1, 1);
    //System.err.println(new ParPlot2DWFFunc().getGPUCode(ctx));
    System.err.println(createSwanVariation(new MobiqFunc(), code));
  }

  private String createSwanVariation(VariationFunc variationFunc, String code) {
    StringBuilder sb = new StringBuilder();
    // params
    if(variationFunc.getParameterNames().length > 0) {
      addIndented(0, sb, "class " + variationFunc.getClass().getSimpleName() + " extends VariationFuncWaves:\n");
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
      addIndented(2, sb, "return __params;\n");
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
    addIndented(2, sb, "return '" + variationFunc.getName() + "';\n");
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
            .replaceAll("pContext\\.random\\(\\)", "rand8(tex, rngState)")
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
