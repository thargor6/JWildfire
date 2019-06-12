package org.jwildfire.create.tina.base.weightmap;

import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class EmptyWeightMap implements WeightMap {

  @Override
  public double getValue(FlameTransformationContext pContext, double x, double y, double z) {
    return 0.0;
  }
}
