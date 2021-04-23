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

package org.jwildfire.create.tina.faclrender;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.SupportsGPU;
import org.jwildfire.create.tina.variation.VariationFunc;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class VariationSet implements VariationnameTransformer {
  private final Set<String> variationNames =new HashSet<>();
  private final Map<String, VariationFunc> supportingVariations = new HashMap<>();
  private Map<String, String> transformedNames = new HashMap<>();
  private Map<String, String> invTransformedNames = new HashMap<>();

  private final FlameTransformationContext transformCtx;
  private String uuid;

  public VariationSet(Flame flame) {
    transformCtx = createTransformCtx(flame);
    initVariationNames(flame);
  }

  private final static Set<String> MANDATORY_VARIATIONS = new HashSet<>(Arrays.asList(/*"pre_matrix2d", "matrix2d", "post_matrix2d",*/
          "pre_matrix3d", "matrix3d", "post_matrix3d"));

  private void initVariationNames(Flame pFlame) {
    variationNames.clear();
    supportingVariations.clear();
    pFlame.getFirstLayer().getXForms().forEach(xf -> {
      for(int i=0;i<xf.getVariationCount();i++) {
        addVariation(xf.getVariation(i).getFunc());
      }
    });
    pFlame.getFirstLayer().getFinalXForms().forEach(xf -> {
      for(int i=0;i<xf.getVariationCount();i++) {
        addVariation(xf.getVariation(i).getFunc());
      }
    });
    variationNames.addAll(MANDATORY_VARIATIONS);
  }

  private void addVariation(VariationFunc func) {
    String fName = func.getName();
    if(func instanceof SupportsGPU) {
      supportingVariations.put(fName, func);
    }
    if (supportingVariations.containsKey(fName)) {
      String transformedName = "jwf_"+fName.replace("_","");
      transformedNames.put(fName, transformedName);
      invTransformedNames.put(transformedName, fName);
    }
    variationNames.add(transformVariationName(func.getName()));
  }

  private FlameTransformationContext createTransformCtx(Flame transformedFlame) {
    FlameTransformationContext context = new FlameTransformationContext(null, null, 1, 1);
    context.setPreserveZCoordinate(transformedFlame.isPreserveZ());
    return context;
  }

  @Override
  public String transformVariationName(String name) {
    String transformedName = transformedNames.get(name);
    return  transformedName!=null ? transformedName : name;
  }

  public String getUuid() {
    if(uuid==null) {
      uuid = UUID.nameUUIDFromBytes(
              generateVariationsKey(transformCtx, variationNames).getBytes(StandardCharsets.UTF_8))
              .toString().toUpperCase();
    }
    return uuid;
  }

  private String generateVariationsKey(FlameTransformationContext transformCtx, Set<String> variationNames) {
    return "V6"+transformCtx.isPreserveZCoordinate() +"#"+variationNames.stream().sorted().collect(Collectors.joining("#"));
  }

  public Set<String> getVariationNames() {
    return variationNames;
  }

  public String getCode(String transformedName) {
     String variationName = invTransformedNames.get(transformedName);
     if(variationName==null) {
       variationName = transformedName;
     }
     VariationFunc func = supportingVariations.get(variationName);
     if(func!=null && func instanceof SupportsGPU) {
       String code = ((SupportsGPU)func).getGPUCode(transformCtx);
       code=code.replace("->"+variationName, "->"+transformedName);
       StringBuilder sb = new StringBuilder();
       sb.append("<variation name=\""+transformedName+"\" dimension=\"3d\">\n");
       for(String param: func.getParameterNames()) {
         sb.append("<parameter name=\""+param+"\" variation=\""+transformedName+"\"/>\n");
       }
       sb.append("<source>\n");
       sb.append("<![CDATA[\n");
       sb.append(code);
       sb.append("]]>\n");
       sb.append("</source>\n");
       sb.append("</variation>\n");
       return sb.toString();
     }
     return null;
  }

}
