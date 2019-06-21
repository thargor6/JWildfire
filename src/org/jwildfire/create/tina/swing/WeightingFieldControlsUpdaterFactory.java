/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2019 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;

import java.util.HashMap;
import java.util.Map;

public class WeightingFieldControlsUpdaterFactory {
  private static final Map<WeightingFieldType, WeightingFieldControlsUpdater> instances = new HashMap<>();

  public static WeightingFieldControlsUpdater getInstance(WeightingFieldType weightingFieldType, TinaController controller, TinaWeightingFieldControllerData controls) {
    WeightingFieldControlsUpdater instance = instances.get(weightingFieldType);
    if(instance==null) {
      switch (weightingFieldType) {
        case CELLULAR_NOISE:
          instance = new CellularNoiseWeightingFieldControlsUpdater(controller, controls);
          break;
        case CUBIC_NOISE:
        case PERLIN_NOISE:
        case SIMPLEX_NOISE:
        case VALUE_NOISE:
          instance = new NoiseWeightingFieldControlsUpdater(controller, controls);
          break;
        case CUBIC_FRACTAL_NOISE:
        case PERLIN_FRACTAL_NOISE:
        case SIMPLEX_FRACTAL_NOISE:
        case VALUE_FRACTAL_NOISE:
          instance = new FractalNoiseWeightingFieldControlsUpdater(controller, controls);
          break;
        case IMAGE_MAP:
          instance = new ImageMapWeightingFieldControlsUpdater(controller, controls);
          break;
        case NONE:
          instance = new EmptyWeightingFieldControlsUpdater(controller, controls);
          break;
        default:
          throw new RuntimeException("Unknown weighting-field-type <" + weightingFieldType +">");
      }
      instances.put(weightingFieldType, instance);
    }
    return instance;
  }
}
