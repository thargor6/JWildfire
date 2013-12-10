package org.jwildfire.create.tina.swing;

public enum RandomBatchQuality {
  LOW {
    @Override
    public double getCoverage() {
      return 0.32;
    }

    @Override
    public int getMaxSamples() {
      return 10;
    }
  },
  NORMAL {
    @Override
    public double getCoverage() {
      return 0.48;
    }

    @Override
    public int getMaxSamples() {
      return 32;
    }
  },
  HIGH {
    @Override
    public double getCoverage() {
      return 0.64;
    }

    @Override
    public int getMaxSamples() {
      return 64;
    }
  };

  public abstract double getCoverage();

  public abstract int getMaxSamples();
}
