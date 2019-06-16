package org.jwildfire.create.tina.base.weightingfield;

import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class EmptyWeightingField implements WeightingField {

  @Override
  public double getValue(FlameTransformationContext pContext, double x, double y, double z) {
    return 0.0;
  }
}
