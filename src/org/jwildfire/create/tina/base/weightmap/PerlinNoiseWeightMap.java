package org.jwildfire.create.tina.base.weightmap;

import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.NoiseTools;

public class PerlinNoiseWeightMap implements WeightMap {
  private final double v[] = new double[3];
  private final double aScale;
  private final double fScale;
  private final int octaves;

  public PerlinNoiseWeightMap(double aScale, double fScale, int octaves) {
    this.aScale = aScale;
    this.fScale = fScale;
    this.octaves = octaves;
  }

  @Override
  public double getValue(FlameTransformationContext pContext, double x, double y, double z) {
    v[0]=x;
    v[1]=y;
    v[2]=z;
    return NoiseTools.perlinNoise3D(v, aScale, fScale, octaves);
  }
}
