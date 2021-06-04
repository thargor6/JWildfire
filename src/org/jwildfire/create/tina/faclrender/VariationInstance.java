package org.jwildfire.create.tina.faclrender;

import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.SupportsGPU;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariationInstance {
  private final String originalName;
  private final int instanceId;
  private final boolean singleton; // one instance per variation set which is the default
  private final String transformedName;
  private final VariationFunc func;

  public VariationInstance(VariationFunc func, boolean singleton, int instanceId) {
    this.originalName = func.getName();
    this.singleton = singleton;
    this.instanceId = instanceId;
    this.func = func;
    // renaming required because some variation names may be reserved words or functions in CUDA, e.g. "log" or "sin"
    this.transformedName = (this.singleton ? "jwf_" : String.format("jwf%d_", instanceId)) + this.originalName;
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

  public String getTransformedCode(FlameTransformationContext transformCtx) {
    String gpuCode;
    {
      String rawGpuCode = ((SupportsGPU) func).getGPUCode(transformCtx);
      gpuCode = singleton ? rawGpuCode : injectInstanceId(rawGpuCode, instanceId);
      gpuCode = gpuCode.replace("->"+ originalName, "->"+ transformedName);
    }
    String gpuFunctions;
    {
      String rawGpuFunctions = ((SupportsGPU) func).getGPUFunctions(transformCtx);
      gpuFunctions = singleton ? rawGpuFunctions : injectInstanceId(rawGpuFunctions, instanceId);
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

}
