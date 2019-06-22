package org.jwildfire.create.tina.base.weightingfield;

public class EmptyWeightingField implements WeightingField {

  @Override
  public double getValue(double x, double y, double z) {
    return 0.0;
  }
}
