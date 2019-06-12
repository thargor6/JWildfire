package org.jwildfire.create.tina.base.weightmap;

import org.jwildfire.create.tina.variation.FlameTransformationContext;

public interface WeightMap {
  double getValue(FlameTransformationContext pContext, double x, double y, double z);
}
