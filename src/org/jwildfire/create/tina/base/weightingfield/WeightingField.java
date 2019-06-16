package org.jwildfire.create.tina.base.weightingfield;

import org.jwildfire.create.tina.variation.FlameTransformationContext;

public interface WeightingField {
  double getValue(FlameTransformationContext pContext, double x, double y, double z);
}
