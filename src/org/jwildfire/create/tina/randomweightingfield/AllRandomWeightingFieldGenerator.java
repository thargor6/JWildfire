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
package org.jwildfire.create.tina.randomweightingfield;

import java.util.ArrayList;
import java.util.List;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.swing.TinaControllerContextService;

public class AllRandomWeightingFieldGenerator implements RandomWeightingFieldGenerator {

  protected static final List<RandomWeightingFieldGenerator> allGenerators;
  protected static final List<RandomWeightingFieldGenerator> gpuSupportingGenerators;

  static {
    allGenerators = new ArrayList<>();
    allGenerators.add(new CellularNoiseNoiseRandomWeightingFieldGenerator());
    allGenerators.add(new BasicNoiseRandomWeightingFieldGenerator());
    allGenerators.add(new FractalNoiseRandomWeightingFieldGenerator());
    allGenerators.add(new ImageMapRandomWeightingFieldGenerator());

    gpuSupportingGenerators = new ArrayList<>();
    gpuSupportingGenerators.add(new CellularNoiseNoiseRandomWeightingFieldGenerator());
    gpuSupportingGenerators.add(new BasicNoiseRandomWeightingFieldGenerator());
    gpuSupportingGenerators.add(new FractalNoiseRandomWeightingFieldGenerator());
  }

  @Override
  public void addWeightingField(Flame pFlame) {
    RandomWeightingFieldGenerator generator;
    if(TinaControllerContextService.getContext().isGpuMode()) {
      generator = gpuSupportingGenerators.get((int) (Math.random() * gpuSupportingGenerators.size()));
    }
    else {
      generator = allGenerators.get((int) (Math.random() * allGenerators.size()));
    }
    generator.addWeightingField(pFlame);
  }

  @Override
  public String getName() {
    return "(All)";
  }

}
