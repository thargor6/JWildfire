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
import org.jwildfire.create.tina.swing.MessageLogger;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.NotDesiredForGPURendering;
import org.jwildfire.create.tina.variation.SupportsGPU;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class VariationSet implements VariationnameTransformer {
  private static final Logger logger = LoggerFactory.getLogger(VariationSet.class);
  private final MessageLogger messageLogger;
  private final List<VariationInstance> variationInstances = new ArrayList<>();
  private final FlameTransformationContext transformCtx;
  private String uuid;

  public VariationSet(Flame flame, FlameTransformationContext transformCtx, MessageLogger messageLogger) {
    this.messageLogger = messageLogger;
    this.transformCtx = transformCtx;
    initVariationNames(flame);
  }

  private void initVariationNames(Flame pFlame) {
    variationInstances.clear();
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
  }

  private void addVariation(VariationFunc func) {
    if(func instanceof SupportsGPU) {
      if(((SupportsGPU) func).isStateful()) {
        int count = (int)variationInstances.stream().filter(i -> !i.isSingleton() &&  i.getOriginalName().equals(func.getName())).count();
        variationInstances.add(new VariationInstance(transformCtx, func, false, count));
      }
      else {
        if(!variationInstances.stream().filter(i -> i.isSingleton() && i.getOriginalName().equals(func.getName())).findAny().isPresent()) {
          variationInstances.add(new VariationInstance(transformCtx, func, true, -1));
        }
      }
    }
    else {
      String msg;
      if(func instanceof NotDesiredForGPURendering) {
        msg = String.format("\"%s\" is not supported on GPU", func.getName());
        msg+=";\nreason: "+((NotDesiredForGPURendering)func).getDeprecationReason();
      }
      else {
        msg = String.format("\"%s\" is not supported (yet) on GPU, maybe it will be the future", func.getName());
      }
      if(messageLogger !=null) {
        messageLogger.logMessage(msg);
      }
      logger.error(msg);
    }
  }

  @Override
  public String transformVariationName(VariationFunc func) {
    if(((SupportsGPU) func).isStateful()) {
       return variationInstances.stream().filter(i -> i.getFunc() == func).findFirst().get().getTransformedName();
    }
    else {
      String varName = VariationInstance.getVariationName(transformCtx, func);
      return variationInstances.stream().filter(i -> i.getOriginalName().equals(varName)).findFirst().get().getTransformedName();
    }
  }

  public String getUuid() {
    if(uuid==null) {
      uuid = UUID.nameUUIDFromBytes(
              generateVariationsKey(transformCtx, getVariationNames()).getBytes(StandardCharsets.UTF_8))
              .toString().toUpperCase();
    }
    return uuid;
  }

  private String generateVariationsKey(FlameTransformationContext transformCtx, Set<String> variationNames) {
    // TODO remove
    return  UUID.randomUUID().toString() + "V001"+transformCtx.isPreserveZCoordinate() +"#"+variationNames.stream().sorted().collect(Collectors.joining("#"));
  }

  public Set<String> getVariationNames() {
    return variationInstances.stream().map( i-> i.getTransformedName()).collect(Collectors.toSet());
  }

  public List<VariationInstance> getVariationInstances() {
    return variationInstances;
  }
}
