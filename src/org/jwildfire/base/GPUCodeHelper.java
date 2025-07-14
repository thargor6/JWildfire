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

import java.util.ArrayList;
import java.util.List;

public class GPUCodeHelper {

  public static void main(String args[]) {
    new GPUCodeHelper().run();
  }

  private void run() {
    VariationFunc codeFunc = new CutWoodFunc();
    String code =
            " ";

    String gpuCode = "";
    String gpuFunctions = "";
    FlameTransformationContext ctx = new FlameTransformationContext(null, null, 1, 1);

    if(codeFunc instanceof SupportsGPU) {
      gpuCode = ((SupportsGPU) codeFunc).getGPUCode(ctx);
      gpuFunctions = ((SupportsGPU) codeFunc).getGPUFunctions(ctx);
    }
    System.err.println(createSwanVariation(codeFunc, code, gpuCode, gpuFunctions));
  }

  private String createSwanVariation(VariationFunc variationFunc, String code, String gpuCode, String gpuFunctions) {
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

    if(gpuCode != null && gpuCode.trim().length() > 0) {
      addIndented(
              2,
              sb,
              convertGpuCodeToSwan(gpuCode, variationFunc.getName(), variationFunc.getParameterNames()));
    } else {
      addIndented(
          2,
          sb,
          convertCodeToSwan(code, variationFunc.getName(), variationFunc.getParameterNames()));
     }
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

    if(gpuFunctions!= null && gpuFunctions.trim().length() > 0) {
      addIndented(1, sb, "func get_func_dependencies() -> Array[String]:\n");
      addIndented(2, sb, "return [VariationUtilFunctions.FUNC_" +variationFunc.getName().toUpperCase()+ "_UTILS]\n");
      addIndented(0, sb, "\n");
    }

    if(variationFunc.getPriority() !=0) {
      addIndented(1, sb, "func get_default_priority() -> int:\n");
      addIndented(2, sb, "return "+ variationFunc.getPriority() +"\n");
      addIndented(0, sb, "\n");
    }

    if(gpuFunctions != null && gpuFunctions.trim().length() > 0) {
      addIndented(1, sb, "###############################################");
      addIndented(0, sb, "\n");
      addIndented(
          1,
          sb,
          "mathService.register_function(VariationUtilFunctions.FUNC_" +variationFunc.getName().toUpperCase()+ "_UTILS,\n");
      addIndented(0, sb, "\"\"\"\n");
      addIndented(
              0,
              sb,
              convertGpuCodeToSwan(gpuFunctions, "_NONE", new String[]{} ) + "\n");
      addIndented(0, sb, "\"\"\")\n");
    }

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

  private String convertCodeToSwan(String code, String varName, String paramNames[]) {
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

  private String convertGpuCodeToSwan(String code, String varName, String paramNames[]) {
    // functions
    String newCode =
        // pAffineTP.getPrecalcSumsq()
        code.replaceAll("__r2", "_r2")
            .replaceAll("__phi", "_phi")
            .replaceAll("__x", "_tx")
            .replaceAll("__y", "_ty")
            .replaceAll("__z", "_tz")
            .replaceAll("__px", "_vx")
            .replaceAll("__py", "_vy")
            .replaceAll("__pz", "_vz")
                .replaceAll("__doHide", "_doHide")
            .replaceAll("__pal", "_color")
            .replaceAll("__" + varName, varName)
            .replaceAll("make_float3\\(", "vec3(")
                .replaceAll("make_vec3\\(", "vec3(")
                .replaceAll("float3", "vec3")
            .replaceAll("make_float2\\(", "vec2(")
                .replaceAll("make_float4\\(", "vec4(")
                .replaceAll("make_vec2\\(", "vec2(")
            .replaceAll("float2", "vec2")
            .replaceAll("sinf\\(", "sin(")
                .replaceAll("floorf\\(", "floor(")
            .replaceAll("cosf\\(", "cos(")
                .replaceAll("sinhf\\(", "sinh(")
                .replaceAll("coshf\\(", "cosh(")
                .replaceAll("logf\\(", "log(")
                .replaceAll("powf\\(", "pow(")
                .replaceAll("lroundf\\(", "round(")
            .replaceAll("expf\\(", "exp(")
                .replaceAll("ceilf\\(", "ceil(")
            .replaceAll("sqrtf\\(", "sqrt(")
            .replaceAll("sqrf\\(", "sqr(")
                .replaceAll("fabsf\\(", "abs(")
                .replaceAll("fabs\\(", "abs(")
            .replaceAll("absf\\(", "abs(")
                .replaceAll("atan2f\\(", "sw_atan2(")
            .replaceAll("fminf\\(", "min(")
            .replaceAll("fmaxf\\(", "max(")
            .replaceAll("tanf\\(", "tan(")
                .replaceAll("floorf\\(", "floor(")
                .replaceAll("Complex_Init\\(&", "Complex_Init(")
                .replaceAll("Complex_Dec\\(&", "Complex_Dec(")
                .replaceAll("Complex_Inc\\(&", "Complex_Inc(")
                .replaceAll("Complex_Div\\(&", "Complex_Div(")
                .replaceAll("Complex_Scale\\(&", "Complex_Scale(")
                .replaceAll("Complex_Sqrt\\(&", "Complex_Sqrt(")
                .replaceAll("Complex_Exp\\(&", "Complex_Exp(")
                .replaceAll("Complex_Log\\(&", "Complex_Log(")
                .replaceAll("Complex_AsinH\\(&", "Complex_AsinH(")
                .replaceAll("Complex_AcosH\\(&", "Complex_AcosH(")
                .replaceAll("Complex_AtanH\\(&", "Complex_AtanH(")
                .replaceAll("Complex_Asin\\(&", "Complex_Asin(")
                .replaceAll("Complex_Acos\\(&", "Complex_Acos(")
                .replaceAll("Complex_Atan\\(&", "Complex_Atan(")
                .replaceAll("__device__", "")
                .replaceAll("short ", "bool ")
            .replaceAll(" PI ", " M_PI ")
                .replaceAll(" " + varName + " ", " amount ")
            .replaceAll("RANDFLOAT\\(\\)", "rand8(uv, rngState)");

    /*
    for(String paramName: paramNames) {
      newCode = newCode.replaceAll("([\\s\\(\\-\\+\\*]+)("+paramName+")([\\s\\+\\-\\*\\);]+)", "$1"+varName+"_"+paramName+"$3");
    }*/
    return newCode;
  }

}
