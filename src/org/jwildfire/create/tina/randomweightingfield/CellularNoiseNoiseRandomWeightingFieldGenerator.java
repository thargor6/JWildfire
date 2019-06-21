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
package org.jwildfire.create.tina.randomweightingfield;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;
import org.jwildfire.create.tina.mutagen.WeightingFieldMutation;

import java.util.ArrayList;
import java.util.List;

public class CellularNoiseNoiseRandomWeightingFieldGenerator implements RandomWeightingFieldGenerator {

  private static List<WeightingFieldType> WEIGHTING_FIELD_TYPE_LIST;

  static {
    WEIGHTING_FIELD_TYPE_LIST = new ArrayList<>();
    WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.CELLULAR_NOISE);
  }

  @Override
  public void addWeightingField(Flame pFlame) {
    WeightingFieldMutation weightingFieldMutation = new WeightingFieldMutation();
    weightingFieldMutation.setWeightingFieldTypeList(WEIGHTING_FIELD_TYPE_LIST);
    for(Layer layer: pFlame.getLayers()) {
      weightingFieldMutation.execute(layer);
    }
  }

  @Override
  public String getName() {
    return "Cellular Noise";
  }

}
