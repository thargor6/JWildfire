package org.jwildfire.create.tina.base.weightingfield;

public enum WeightingFieldInputType {

  AFFINE {
    @Override
    public String toString() {
      return "Affine";
    }
  },

  POSITION {
    @Override
    public String toString() {
      return "Position";
    }
  };
}
