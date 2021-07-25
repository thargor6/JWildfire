package org.jwildfire.create.tina.faclrender;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariationInstance {
  private static final Logger logger = LoggerFactory.getLogger(VariationInstance.class);
  private final String originalName;
  private final int instanceId;
  private final boolean singleton; // one instance per variation set which is the default
  private final String transformedName;
  private final VariationFunc func;

  public VariationInstance(FlameTransformationContext transformCtx, VariationFunc func, boolean singleton, int instanceId) {
    this.originalName = getVariationName(transformCtx, func);
    this.singleton = singleton;
    this.instanceId = instanceId;
    this.func = func;
    String baseVarName;
    if(func instanceof CustomFullVariationWrapperFunc) {
      // append random id to enforce that this coe is recompiled each time, because the user may change it
      baseVarName = this.originalName+"_"+UUID.randomUUID().toString().replaceAll("-","");
    }
    else {
      baseVarName = this.originalName;
    }
     // renaming required because some variation names may be reserved words or functions in CUDA, e.g. "log" or "sin"
    this.transformedName = (this.singleton ? "jwf_" : String.format("jwf%d_", instanceId)) + baseVarName;
  }

  public static String getVariationName(FlameTransformationContext transformCtx, VariationFunc func) {
    if(func instanceof CustomFullVariationWrapperFunc) {
      // compile the custom func in order to get the actual name of the (inner) variation func
      try {
        return ((CustomFullVariationWrapperFunc)getInitializedVarFunc(transformCtx, func)).getNameOfWrappedVariation();
      }
      catch(Exception ex) {
        logger.error(ex.getMessage(), ex);
      }
    }
    return func.getName();
  }

  public String getOriginalName() {
    return originalName;
  }

  public boolean isSingleton() {
    return singleton;
  }

  public String getTransformedName() {
    return transformedName;
  }

  public VariationFunc getFunc() {
    return func;
  }

  private String injectInstanceId(String code, int instanceId) {
    Pattern p = Pattern.compile("%d");
    Matcher m = p.matcher(code);
    List<Integer> args=new ArrayList<>();
    while (m.find()){
      args.add(instanceId);
    }
    return String.format(code, args.toArray());
  }

  private static SupportsGPU getInitializedVarFunc(FlameTransformationContext transformCtx, VariationFunc variationFunc) {
    VariationFunc copy = variationFunc.makeCopy();
    copy.init(transformCtx, new Layer(), new XForm(), 1.0);
    return (SupportsGPU)copy;
  }

  public String getTransformedCode(FlameTransformationContext transformCtx) {
    String gpuCode, gpuFunctions;
    {
      SupportsGPU supportsGPU = VariationInstance.getInitializedVarFunc(transformCtx, func);
      {
        String rawGpuCode = getWFieldsInitCode(transformedName)
                          +  supportsGPU.getGPUCode(transformCtx);
        gpuCode = singleton ? rawGpuCode : injectInstanceId(rawGpuCode, instanceId);
        gpuCode = gpuCode.replace("varpar->" + originalName, "varpar->" + transformedName);
        gpuCode = gpuCode.replace("__" + originalName, "__" + transformedName);
      }
      {
        String rawGpuFunctions = supportsGPU.getGPUFunctions(transformCtx);
        gpuFunctions = singleton ? rawGpuFunctions : injectInstanceId(rawGpuFunctions, instanceId);
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("<variation name=\""+ transformedName +"\" dimension=\"3d\"");
    if (Arrays.asList(func.getVariationTypes()).contains(VariationFuncType.VARTYPE_DC)) {
      sb.append(" directColor=\"yes\"");
    }
    sb.append(">\n");
    for(String param: func.getParameterNames()) {
      sb.append("<parameter name=\""+param+"\" variation=\""+ transformedName +"\"/>\n");
    }
    for(String param: ((SupportsGPU)func).getGPUExtraParameterNames()) {
      sb.append("<parameter name=\""+param+"\" variation=\""+ transformedName +"\"/>\n");
    }
    sb.append("<source>\n");
    sb.append("<![CDATA[\n");
    sb.append(gpuCode);
    sb.append("]]>\n");
    sb.append("</source>\n");
    if(gpuFunctions!=null && !gpuFunctions.trim().isEmpty()) {
      sb.append("<functions>\n");
      sb.append("<![CDATA[\n");
      sb.append(gpuFunctions);
      sb.append("]]>\n");
      sb.append("</functions>\n");
    }
    sb.append("</variation>\n");
    return sb.toString();
  }

  private String getWFieldsInitCode(String funcName) {
    StringBuffer sb = new StringBuffer();
    sb.append("float __"+originalName+" = varpar->"+originalName+" * __wFieldAmountScale;\n");
    if (func.getParameterNames().length > 0) {
      int idx = 0;
      sb.append("float wFieldScale;\n");
      for (String param : func.getParameterNames()) {
        sb.append("if(" + idx + "==__wFieldVar1IntensityIdx)\n");
        sb.append("  wFieldScale = __wFieldVar1Intensity;\n");
        sb.append("else if(" + idx + "==__wFieldVar2IntensityIdx)\n");
        sb.append("  wFieldScale = __wFieldVar2Intensity;\n");
        sb.append("else if(" + idx + "==__wFieldVar3IntensityIdx)\n");
        sb.append("  wFieldScale = __wFieldVar3Intensity;\n");
        sb.append("else\n");
        sb.append("  wFieldScale = 1.f;\n");
        sb.append(
            "float __"
                + originalName
                + "_"
                + param
                + " = varpar->"
                + originalName
                + "_"
                + param
                + " * wFieldScale;\n");
        idx++;
      }
    }
    return sb.toString();
  }

}
